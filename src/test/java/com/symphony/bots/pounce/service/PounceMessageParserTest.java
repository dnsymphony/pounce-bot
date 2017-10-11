package com.symphony.bots.pounce.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * @author Dan Nathanson
 */
public class PounceMessageParserTest {
  private PounceMessageParser parser = new PounceMessageParser();

  @Test
  public void testParseMessage() {
    SymMessage symMessage = new SymMessage();
    symMessage.setEntityData("{\n"
        + "  \"mention1\" : {\n"
        + "    \"type\" : \"com.symphony.user.mention\",\n"
        + "    \"id\": [\n"
        + "      {\n"
        + "        \"type\" : \"com.symphony.user.userId\",\n"
        + "        \"value\" : \"68719476746\"\n"
        + "      }\n"
        + "    ]\n"
        + "  },\n"
        + "  \"mention2\" : {\n"
        + "    \"type\" : \"com.symphony.user.mention\",\n"
        + "    \"id\": [\n"
        + "      {\n"
        + "        \"type\" : \"com.symphony.user.userId\",\n"
        + "        \"value\" : \"654321\"\n"
        + "      }\n"
        + "    ]\n"
        + "  }\n"
        + "}");
    symMessage.setMessage("<div data-format=\"PresentationML\" data-version=\"2.0\"><br/>pounce on <span class=\"entity\" "
        + "data-entity-id=\"mention1\">@Local Bot03</span> with chime</div>");
    symMessage.setFromUserId(123L);


    PounceMessage pounceMessage = parser.parse(symMessage);
    assertEquals("Pouncer", 123L, (long) pounceMessage.getPouncer());
    assertEquals("Num pouncees", 2, pounceMessage.getPouncees().size());
    assertEquals("Pouncee 0", 68719476746L, (long) pounceMessage.getPouncees().get(0));
    assertEquals("Pouncee 1", 654321L, (long) pounceMessage.getPouncees().get(1));
    assertEquals("Chime ?", true, pounceMessage.isChime());
  }

/*

{
  "mention1" : {
    "type" : "com.symphony.user.mention"
    "id": [
      {
        "type" : "com.symphony.user.userId",
        "value" : "68719476746"
      }
    ]
  },
  "mention2" : {
    "type" : "com.symphony.user.mention"
    "id": [
      {
        "type" : "com.symphony.user.userId",
        "value" : "654321"
      }
    ]
  }
}

*/

}