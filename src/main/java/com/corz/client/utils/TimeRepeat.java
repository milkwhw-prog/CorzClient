package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;

public final class TimeRepeat {
   public static final int UNUSED_CODE_ONE = 1;
   private long elapsedTicks;
   private long repeatCount;
   private long totalRepeatCount;
   private final Map<Long, Integer> repeatIntegerMap = new HashMap<>();

   public void updateTime() {
      this.repeatCount++;
      this.repeatIntegerMap.clear();
   }

   public int getOrDefault(long orDefaultValue) {
      return this.repeatIntegerMap.getOrDefault(orDefaultValue, 0);
   }

   private static long getTime(long repeatValue, long repeatValue2) {
      return (repeatValue ^ repeatValue2) * 1099511628211L;
   }

   public long getTotalRepeatCount() {
      return this.totalRepeatCount;
   }

   public static long getTime(int repeatValue, long repeatValue2, long repeatValue3, long repeatValue4, long repeatValue5, long repeatValue6) {
      long repeatValue7 = -3750763034362895579L;
      long time8 = getTime(repeatValue7, repeatValue);
      long time9 = getTime(time8, repeatValue2);
      long time10 = getTime(time9, repeatValue3);
      long time11 = getTime(time10, repeatValue4);
      long time12 = getTime(time11, repeatValue5);
      return getTime(time12, repeatValue6);
   }

   public long getRepeatCount() {
      return this.repeatCount;
   }

   public long getTime() {
      return this.elapsedTicks;
   }

   public void update() {
      this.repeatIntegerMap.clear();
   }

   public TimeRepeat.Verdict getTimeRepeat(long repeatValue) {
      if (this.repeatIntegerMap.size() >= 512 && !this.repeatIntegerMap.containsKey(repeatValue)) {
         this.repeatIntegerMap.clear();
      }

      int merge = this.repeatIntegerMap.merge(repeatValue, 1, Integer::sum);
      if (merge == 1) {
         return TimeRepeat.Verdict.FIRST;
      } else if (merge <= 1) {
         this.totalRepeatCount++;
         return TimeRepeat.Verdict.REPEAT;
      } else {
         this.elapsedTicks++;
         return TimeRepeat.Verdict.FORBIDDEN;
      }
   }

   public static enum Verdict {
      FIRST,
      REPEAT,
      FORBIDDEN;
   }
}
