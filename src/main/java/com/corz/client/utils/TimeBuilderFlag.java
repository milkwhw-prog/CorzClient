package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.LongPredicate;

public final class TimeBuilderFlag {
   private final HashMap<Long, Long> builderPositionMap = new HashMap<>();
   private long positionSequence;

   public long[] getTime(long[] builderValues, int builderBound, LongPredicate longPredicate) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < builderValues.length; i++) {
         long builderValue = builderValues[i];
         if (longPredicate.test(builderValue)) {
            arrayList.add(new long[]{builderValue, this.builderPositionMap.getOrDefault(builderValue, Long.MIN_VALUE), i});
         }
      }

      arrayList.sort((obj, obj2) -> ((long[]) obj)[1] != ((long[]) obj2)[1] ? Long.compare(((long[]) obj)[1], ((long[]) obj2)[1]) : Long.compare(((long[]) obj)[2], ((long[]) obj2)[2]));
      int minProbes = Math.min(builderBound, arrayList.size());
      long[] longs = new long[minProbes];

      for (int j = 0; j < minProbes; j++) {
         longs[j] = ((long[])arrayList.get(j))[0];
         this.builderPositionMap.put(longs[j], ++this.positionSequence);
      }

      return longs;
   }

   public void updateTime() {
      this.builderPositionMap.clear();
      this.positionSequence = 0L;
   }

   public boolean canFlag(long[] builderValues) {
      for (long builderValue : builderValues) {
         if (!this.builderPositionMap.containsKey(builderValue)) {
            return false;
         }
      }

      return true;
   }

   public int getValue() {
      return this.builderPositionMap.size();
   }
}
