package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongUnaryOperator;

public final class TimeFootprint {
   private final Map<Long, TimeFootprint.Entry> footprintEntryMap = new HashMap<>();
   private long footprintSequence;
   private final int maxFootprintEntries;

   public void update() {
      this.footprintEntryMap.clear();
   }

   public TimeFootprint.Entry getDataForLong(long footprintValue) {
      return this.footprintEntryMap.get(footprintValue);
   }

   public int getValue() {
      return this.footprintEntryMap.size();
   }

   public boolean canFootprintTime(long footprintValue, long iteratorWorldValue, long footprintValue3, int footprintValue4, int footprintValue5, LongUnaryOperator longUnaryOperator) {
      TimeFootprint.Entry entry = this.footprintEntryMap.get(footprintValue);
      if (entry == null || entry.originKey() != iteratorWorldValue || footprintValue4 - entry.tick() >= footprintValue5) {
         return false;
      } else if (entry.worldRevision() == footprintValue3) {
         return true;
      } else if (entry.footprint() == null) {
         return false;
      } else {
         boolean footprintTime = getTime(entry.footprint(), longUnaryOperator) == entry.fingerprint();
         if (footprintTime) {
            this.footprintSequence++;
         }

         return footprintTime;
      }
   }

   public static long getTime(long[] footprintValues, LongUnaryOperator longUnaryOperator) {
      long footprintValue = 1884001859415498954L;
      long footprintValue2 = -3750763034362895579L;
      long[] footprintValues2 = footprintValues;
      long footprintValue3 = footprintValue ^ ((long)footprintValues.length << 32 ^ footprintValue) & -4294967296L;

      for (long i = footprintValue3 ^ (0L ^ footprintValue3) & 4294967295L; (int)i < (int)(i >>> 32); i ^= (i ^ i + 1L) & 4294967295L) {
         long restartedSectionValue = footprintValues2[(int)i];
         footprintValue2 = (footprintValue2 ^ restartedSectionValue * -7046029254386353131L + longUnaryOperator.applyAsLong(restartedSectionValue)) * 1099511628211L;
      }

      return footprintValue2;
   }

   public TimeFootprint(int value) {
      this.maxFootprintEntries = value;
   }

   public void handle(long footprintValue, long footprintValue2, long changedPlanValue, int footprintValue4, long[] footprintValues, LongUnaryOperator longUnaryOperator) {
      if (this.footprintEntryMap.size() >= this.maxFootprintEntries) {
         this.footprintEntryMap.clear();
      }

      long footprintValue5 = footprintValues == null ? 0L : getTime(footprintValues, longUnaryOperator);
      this.footprintEntryMap.put(footprintValue, new TimeFootprint.Entry(footprintValue2, changedPlanValue, footprintValue4, footprintValues == null ? null : (long[])footprintValues.clone(), footprintValue5));
   }

   public long getTime() {
      return this.footprintSequence;
   }

   public record Entry(long worldRevision, long originKey, int tick, long[] footprint, long fingerprint) {
   }
}
