package com.symphony.bots.pounce.data;

import static java.util.stream.Collectors.toList;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Simple Caffeine cache implementation of DataStore.  No persistence.
 *
 * @author Dan Nathanson
 */
@Component
public class InMemoryDataStoreImpl implements DataStore {
  private Cache<Long, List<PounceEntry>> pounceMap;

  public InMemoryDataStoreImpl() {
    this.pounceMap = Caffeine.newBuilder()
          .expireAfterWrite(24, TimeUnit.HOURS)
          .build();
  }

  @Override
  public void addPounce(Long pouncee, Long pouncer, boolean isChime) {
    List<PounceEntry> pouncers = pounceMap.getIfPresent(pouncer);
    if (pouncers == null) {
      pouncers = new ArrayList<>();
      pounceMap.put(pouncee, pouncers);
    }
    removePounce(pouncee, pouncer);
    pouncers.add(new PounceEntry(pouncer, isChime));
  }

  @Override
  public List<PounceEntry> getPouncers(Long pouncee) {
    List<PounceEntry> pouncers = pounceMap.getIfPresent(pouncee);

    if (pouncers == null) {
      return Collections.unmodifiableList(Collections.emptyList());
    }
    else {
      return Collections.unmodifiableList(pouncers);
    }
  }

  @Override
  public List<Long> getPouncees() {
    return pounceMap.asMap()
        .keySet()
        .stream()
        .collect(Collectors.collectingAndThen(toList(), Collections::unmodifiableList));
  }

  @Override
  public void removePouncee(Long pouncee) {
    pounceMap.invalidate(pouncee);
  }

  @Override
  public void removePounce(Long pouncee, Long pouncer) {
    List<PounceEntry> pouncers = pounceMap.getIfPresent(pouncee);
    if (pouncers != null) {
      Set<PounceEntry> toRemove = pouncers.stream()
          .filter(pounceEntry -> !pounceEntry.getPouncer().equals(pouncer))
          .collect(Collectors.toSet());
      pouncers.removeAll(toRemove);
    }
  }

  @Override
  public void clear() {
    pounceMap.invalidateAll();;
  }

}
