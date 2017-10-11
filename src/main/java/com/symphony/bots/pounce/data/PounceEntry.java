package com.symphony.bots.pounce.data;

/**
 * @author Dan Nathanson
 */
public class PounceEntry {
  private Long pouncer;
  private boolean chime;

  public PounceEntry(Long pouncer, boolean chime) {
    this.pouncer = pouncer;
    this.chime = chime;
  }

  public Long getPouncer() {
    return pouncer;
  }

  public boolean isChime() {
    return chime;
  }
}
