package com.corz.client.utils;

import java.util.HashMap;

public final class TimeHandle {
   private final HashMap<Long, TimeHandle.Entry> handleEntryMap = new HashMap<>();
   private long elapsedTicks;
   private long handleCount;

   public long getTime() {
      return this.elapsedTicks;
   }

   public int getValue() {
      return this.handleEntryMap.size();
   }

   public void update() {
      this.handleEntryMap.clear();
   }

   public void handleTime(long handleValue, long time, long time2) {
      if (this.handleEntryMap.size() >= 512 && !this.handleEntryMap.containsKey(handleValue)) {
         this.handleEntryMap.clear();
      }

      TimeHandle.Entry ifAbsent = this.handleEntryMap.computeIfAbsent(handleValue, long2 -> new TimeHandle.Entry());
      ifAbsent.time = time;
      ifAbsent.time2 = time2;
   }

   public long getTime(long handleValue, long l) {
      TimeHandle.Entry entry = this.handleEntryMap.get(handleValue);
      if (entry != null && entry.time == l) {
         this.handleCount++;
         return entry.time2;
      } else {
         this.elapsedTicks++;
         return -9223372036854775807L;
      }
   }

   public void handle(long handleValue) {
      this.handleEntryMap.remove(handleValue);
   }

   public long getHandleCount() {
      return this.handleCount;
   }

   private static final class Entry {
      long time;
      long time2;
   }
}
