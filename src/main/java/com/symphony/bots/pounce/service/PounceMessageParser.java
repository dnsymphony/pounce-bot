package com.symphony.bots.pounce.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dan Nathanson
 */
@Component
public class PounceMessageParser {
  private static final Logger LOG = LoggerFactory.getLogger(PounceMessageParser.class);

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public PounceMessage parse(SymMessage message) {
    PounceMessage pounceMessage = new BasicPounceMessage();
    pounceMessage.setPouncer(message.getFromUserId());
    List<Long> pouncees = new ArrayList<>();

    if (message.getMessage().toLowerCase().contains("pounce on <span class=\"entity\"")) {
      try {
        JsonNode top = objectMapper.readTree(message.getEntityData());
        for (JsonNode jsonNode : top) {
          if (jsonNode.get("type").asText().equals("com.symphony.user.mention")) {
            ArrayNode ids = (ArrayNode) jsonNode.get("id");
            for (JsonNode idNode : ids) {
              if (idNode.get("type").asText().equals("com.symphony.user.userId")) {
                long userId = idNode.get("value").asLong();
                pouncees.add(userId);
              }
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      pounceMessage.setPouncees(pouncees);
      if (message.getMessage().toLowerCase().contains("with chime")) {
        pounceMessage.setChime(true);
      }
      return pounceMessage;
    }
    return null;
  }
}
