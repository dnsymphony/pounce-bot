package com.symphony.bots.pounce.service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dan Nathanson
 */
public class BasicPounceMessage implements PounceMessage {
  private Long pouncer;
  private List<Long> pouncees = new ArrayList<>();
  private boolean chime;


  BasicPounceMessage() {

  }

  @Override
  public Long getPouncer() {
    return pouncer;
  }

  @Override
  public void setPouncer(Long pouncer) {
    this.pouncer = pouncer;
  }

  @Override
  public List<Long> getPouncees() {
    return pouncees;
  }

  @Override
  public void setPouncees(List<Long> pouncees) {
    this.pouncees = pouncees;
  }

  @Override
  public void setChime(boolean chime) {
    this.chime = chime;
  }

  @Override
  public boolean isChime() {
    return chime;
  }
}

