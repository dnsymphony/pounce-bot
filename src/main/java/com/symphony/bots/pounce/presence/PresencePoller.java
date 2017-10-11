package com.symphony.bots.pounce.presence;

import com.symphony.bots.pounce.data.DataStore;
import com.symphony.bots.pounce.data.PounceEntry;
import com.symphony.bots.pounce.service.PounceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.PresenceException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.symphony.clients.PresenceClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Presence;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Runnable that polls for presence for each pouncee.  This should eventually be replaced by presence feed.
 *
 * @author Dan Nathanson
 */
@Component
public class PresencePoller {

  private static final Logger LOG = LoggerFactory.getLogger(PresencePoller.class);
  private DataStore pounceStore;
  private final ScheduledExecutorService executor;
  private SymphonyClient symphonyClient;

  @Autowired
  public PresencePoller(DataStore pounceStore, PounceService pounceService) {
    this.pounceStore = pounceStore;
    this.symphonyClient = pounceService.getSymphonyClient();
    this.executor = Executors.newScheduledThreadPool(1);
  }


  @PostConstruct
  public void start() {
    Runnable task = () -> {
      List<Long> pouncees = pounceStore.getPouncees();
      pouncees.forEach(pounceeId -> processPouncee(symphonyClient, pounceeId));
    };

    executor.scheduleWithFixedDelay(task, 0, 5, TimeUnit.SECONDS);
  }

  private void processPouncee(SymphonyClient symphonyClient,  Long pounceeId) {
    try {
      SymUser pouncee = symphonyClient.getUsersClient().getUserFromId(pounceeId);
      LOG.info("Checking presence of {}", pouncee.getDisplayName());

      try {
        PresenceClient presenceClient = symphonyClient.getPresenceClient();
        Presence presence = presenceClient.getUserPresence(pounceeId);

        if (presence.getCategory() == Presence.CategoryEnum.AVAILABLE) {
          LOG.info("{} is online", pouncee.getDisplayName());
          List<PounceEntry> pouncers = pounceStore.getPouncers(pounceeId);

          for (PounceEntry pounceEntry : pouncers) {
            Chat chat = new Chat();
            SymUser pouncer = symphonyClient.getUsersClient().getUserFromId(pounceEntry.getPouncer());
            chat.setRemoteUsers(Collections.singleton(pouncer));
            symphonyClient.getChatService().addChat(chat);

            LOG.info("Sending online message to {}", pouncer.getDisplayName());
            SymMessage message = new SymMessage();
            message.setMessage("<messageML>" + pouncee.getDisplayName() + "  just came online </messageML>");
            symphonyClient.getMessageService().sendMessage(chat, message);

            if (pounceEntry.isChime()) {
              LOG.info("Sending chime to {}", pouncer.getDisplayName());
              SymMessage chime = new SymMessage();
              chime.setMessage("<messageML><chime/></messageML>");
              symphonyClient.getMessageService().sendMessage(chat, chime);
            }
          }
          pounceStore.removePouncee(pounceeId);
        }
        else {
          LOG.info("{} is offline", pouncee.getDisplayName());
        }
      } catch (PresenceException e) {
        LOG.error("Error getting presence for user: {}", pounceeId, e);
      } catch (UsersClientException e) {
        LOG.error("Error looking up pouncer", e);
      } catch (MessagesException e) {
        LOG.error("Error sending online message", e);
      }
    } catch (UsersClientException e) {
      LOG.error("Error looking up pouncee", e);
    }
  }

  @PreDestroy
  public void stop() {
    System.out.println("Shutting down");
    executor.shutdown();
  }

}
