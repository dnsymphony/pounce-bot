package com.symphony.bots.pounce.service;

import java.util.List;

/**
 * @author Dan Nathanson
 */
public interface PounceMessage {
  Long getPouncer();

  void setPouncer(Long pouncer);

  List<Long> getPouncees();

  void setPouncees(List<Long> pouncees);

  void setChime(boolean chime);

  boolean isChime();
}
