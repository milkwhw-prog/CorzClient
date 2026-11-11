package com.corz.client.utils;

import java.util.function.LongSupplier;

public final class TargetDelayTimer {
   private int lastTargetId = -1;
   private long requiredDelayMs;
   private long startTimeMs;

   public boolean hasDelayElapsed(int triggerValue, long time, LongSupplier longSupplier) {
      if (triggerValue != this.lastTargetId) {
         this.lastTargetId = triggerValue;
         this.startTimeMs = time;
         this.requiredDelayMs = Math.max(0L, longSupplier.getAsLong());
      }

      return time - this.startTimeMs >= this.requiredDelayMs;
   }

   public void reset() {
      this.lastTargetId = -1;
      this.requiredDelayMs = 0L;
   }

   public int getLastTargetId() {
      return this.lastTargetId;
   }
}
