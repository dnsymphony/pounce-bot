package com.symphony.bots.pounce.data;

import java.util.List;

/**
 * Data store for pending pounces.  Maps user who's presence is monitored to list of users who want to be
 * notified when that user comes online.
 *
 * @author Dan Nathanson
 */
public interface DataStore {

  /**
   * Register that one user (pouncer) is interested in being notified when another user (pouncee) comes online.
   */
  void addPounce(Long pouncee, Long pouncer, boolean isChime);

  /**
   * Get list of pouncers who want to know when a specified pouncee comes online
   */
  List<PounceEntry> getPouncers(Long pouncee);

  /**
   * Get list of all pouncees.
   */
  List<Long> getPouncees();

  /**
   * Remove pouncee from the map.  Called after all pouncers have been notified
   */
  void removePouncee(Long pouncee);

  /**
   * Remove pounce interest for a single user.  If this is the only user, remove the pouncee altogether.
   */
  void removePounce(Long pouncee, Long pouncer);

  /**
   * Remove all entries.
   */
  void clear();

}
