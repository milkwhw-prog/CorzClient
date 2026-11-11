package com.corz.client.utils;

public final class FixedRateTicker {
   private boolean started;
   private final long assistIntervalNanos;
   private long accumulatedNanos;
   private long lastNanoTime;

   public int getElapsedTicks(long nanoTime) {
      if (!this.started) {
         this.started = true;
         this.lastNanoTime = nanoTime;
         this.accumulatedNanos = 0L;
         return 0;
      } else {
         long assistValue = nanoTime - this.lastNanoTime;
         this.lastNanoTime = nanoTime;
         if (assistValue <= 0L) {
            return 0;
         } else {
            this.accumulatedNanos += assistValue;
            long assistFactor = this.accumulatedNanos / this.assistIntervalNanos;
            this.accumulatedNanos = this.accumulatedNanos - assistFactor * this.assistIntervalNanos;
            if (assistFactor > 50L) {
               this.accumulatedNanos = 0L;
               return 50;
            } else {
               return (int)assistFactor;
            }
         }
      }
   }

   public FixedRateTicker(int assistBound) {
      this.assistIntervalNanos = 1000000000L / Math.max(1, assistBound);
   }

   public void reset() {
      this.started = false;
      this.accumulatedNanos = 0L;
   }
}
