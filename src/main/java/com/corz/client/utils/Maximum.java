package com.corz.client.utils;

import java.util.function.LongPredicate;

public final class Maximum {
   private long maximumTime = Long.MIN_VALUE;
   private int maximumHitCount;
   private final int maximumValue;

   public void updateMaximumTime() {
      this.maximumTime = Long.MIN_VALUE;
      this.maximumHitCount = 0;
   }

   public Maximum(int maximumValue) {
      if (maximumValue < 1) {
         throw new IllegalArgumentException("maximum");
      } else {
         this.maximumValue = maximumValue;
      }
   }

   public Long getMaximumTime(LongPredicate longPredicate) {
      if (this.maximumTime != Long.MIN_VALUE && this.maximumHitCount < this.maximumValue && longPredicate.test(this.maximumTime)) {
         return this.maximumTime;
      } else {
         this.updateMaximumTime();
         return null;
      }
   }

   public void handleMaximumTime(long maximumTime) {
      if (this.maximumTime != maximumTime) {
         this.maximumTime = maximumTime;
         this.maximumHitCount = 0;
      }

      this.maximumHitCount++;
   }

   public long getMaximumTime() {
      return this.maximumTime;
   }

   public int getValue() {
      return this.maximumHitCount;
   }
}
