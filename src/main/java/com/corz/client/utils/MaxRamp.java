package com.corz.client.utils;

public final class MaxRamp {
   private long rampDurationMs;
   private long rampStartTime;
   private long maxBound;
   private long secondaryMaxBound;

   public long getMaxBound() {
      return this.maxBound;
   }

   public static double getAmount(double limit) {
      if (!(limit > 0.0)) {
         return 0.0;
      } else if (limit >= 1.0) {
         return 1.0;
      } else {
         double factor = 1.0 - limit;
         return 1.0 - factor * factor * factor * factor;
      }
   }

   public MaxRamp(long longValue) {
      this.handleMax(longValue);
   }

   public boolean canMax(long bound, long longValue, long cheapHigh) {
      long maxLengthNs = Math.max(0L, bound);
      if (maxLengthNs == this.secondaryMaxBound) {
         return false;
      } else {
         this.handleMaxTime(this.getMaxTime(longValue), maxLengthNs, longValue, cheapHigh);
         return true;
      }
   }

   public void handleMaxTime(long bound, long maxLengthNs, long time, long cheapHigh) {
      this.maxBound = Math.max(0L, bound);
      this.secondaryMaxBound = Math.max(0L, maxLengthNs);
      this.rampStartTime = time;
      this.rampDurationMs = Math.max(0L, cheapHigh);
   }

   public long getSecondaryMaxBound() {
      return this.secondaryMaxBound;
   }

   public boolean canMaxTime(long longValue) {
      return this.rampDurationMs <= 0L || this.maxBound == this.secondaryMaxBound || longValue - this.rampStartTime >= this.rampDurationMs;
   }

   public long getMaxTime(long longValue) {
      if (this.canMaxTime(longValue)) {
         return this.secondaryMaxBound;
      } else if (longValue <= this.rampStartTime) {
         return this.maxBound;
      } else {
         double factor = getAmount((double)(longValue - this.rampStartTime) / this.rampDurationMs);
         long bound = Math.max(this.maxBound, this.secondaryMaxBound) - Math.min(this.maxBound, this.secondaryMaxBound);
         long min2 = Math.min(bound, (long)Math.floor(bound * factor));
         return this.secondaryMaxBound >= this.maxBound ? this.maxBound + min2 : this.maxBound - min2;
      }
   }

   public float getTimeMax(long longValue) {
      if (this.canMaxTime(longValue)) {
         return 1.0F;
      } else {
         return longValue <= this.rampStartTime ? 0.0F : (float)getAmount((double)(longValue - this.rampStartTime) / this.rampDurationMs);
      }
   }

   public void handleMax(long bound) {
      this.maxBound = this.secondaryMaxBound = Math.max(0L, bound);
      this.rampDurationMs = 0L;
   }
}
