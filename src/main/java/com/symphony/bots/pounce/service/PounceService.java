package com.symphony.bots.pounce.service;

import com.symphony.bots.pounce.data.DataStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * @author Dan Nathanson
 */
@Component
public class PounceService implements MessageListener {
  private static final Logger LOG = LoggerFactory.getLogger(PounceService.class);
  private SymphonyClient symClient;
  private PounceMessageParser parser;
  private DataStore dataStore;

  @Autowired
  public PounceService(PounceMessageParser parser, DataStore dataStore) {
    this.parser = parser;
    this.dataStore = dataStore;
    init();
  }

  private void init() {
      SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(true);

      //Create an initialized client
      symClient = SymphonyClientFactory.getClient(
          SymphonyClientFactory.TYPE.V4,symphonyClientConfig);

      symClient.getMessageService().addMessageListener(this);
  }

  @Override
  public void onMessage(SymMessage message) {
    if (message == null) {
      return;
    }


    LOG.debug("TS: {}\nFrom ID: {}\nSymMessage Type: {}\nSymMessage : {}\nEntityJSON : {}",
        message.getTimestamp(),
        message.getFromUserId(),
        message.getMessageType(),
        message.getMessage(),
        message.getEntityData());

    PounceMessage pounceMessage = parser.parse(message);
    if (pounceMessage != null) {
      try {
        SymUser pouncer = symClient.getUsersClient().getUserFromId(pounceMessage.getPouncer());
        for (Long pounceeId : pounceMessage.getPouncees()) {
          SymUser pouncee = symClient.getUsersClient().getUserFromId(pounceeId);
          LOG.info("Received pounce request from {} for {}", pouncer.getDisplayName(), pouncee.getDisplayName());

          dataStore.addPounce(pounceeId, pounceMessage.getPouncer(), pounceMessage.isChime());

          SymMessage aMessage = new SymMessage();

          if (pounceMessage.isChime()) {
            aMessage.setMessageText("OK.  I'll chime you when " + pouncee.getDisplayName() + " is online");
          }
          else {
            aMessage.setMessageText("OK.  I'll send you a message when " + pouncee.getDisplayName() + " is online");
          }

          try {
            symClient.getMessagesClient().sendMessage(message.getStream(), aMessage);
          } catch (MessagesException e) {
            LOG.error("Error sending message", e);
          }

        }
      } catch (UsersClientException e) {
        LOG.error("Error looking up user", e);
      }
    }


  }

  public SymphonyClient getSymphonyClient() {
    return this.symClient;
  }
}
