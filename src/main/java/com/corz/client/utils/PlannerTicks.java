package com.corz.client.utils;

import java.util.Locale;

public final class PlannerTicks {
   private int consecutiveCatchupTicks;
   private int plannerCatchupValue;
   private int catchupPlannerCount;
   private long previousSampleNanos = -1L;
   private int catchupCount;
   private long lastNanoTime;
   private int catchupPlannerValue;
   private boolean catchupActive;
   private boolean catchupPlannerActive;
   private long nanoTimeCatchup;
   private long lastTick;
   private long catchupElapsedNanos;
   private long catchupValue;
   private int plannerTicksCount;
   private long lastNanoTimeCatchup;
   private long plannerTicksValue;
   private long lastThrottleNanos;
   private long lastNanoTimeCatchupPlanner = -1L;
   private long lastPlannerLimitNanos;
   private int catchupSampleCount;
   private long catchupTotalNanos;

   public boolean canTime(long nanoTime, long plannerTicksLimit) {
      if (this.lastPlannerLimitNanos >= 0L && nanoTime - this.lastPlannerLimitNanos < plannerTicksLimit) {
         return false;
      } else {
         this.lastPlannerLimitNanos = nanoTime;
         return true;
      }
   }

   public boolean isCatchupPlannerActive() {
      return this.catchupPlannerActive;
   }

   public int getCatchupPlannerValue() {
      return this.catchupPlannerValue;
   }

   public boolean isTimeFlag() {
      return this.plannerTicksValue >= 0L && (this.plannerTicksValue < 40000000L || this.catchupPlannerActive);
   }

   public long getNanoTimeCatchup() {
      return this.nanoTimeCatchup;
   }

   public PlannerTicks() {
      this.nanoTimeCatchup = -1L;
      this.lastTick = -1L;
      this.lastPlannerLimitNanos = -1L;
      this.lastNanoTimeCatchup = -1L;
      this.lastNanoTime = -1L;
      this.lastThrottleNanos = -1L;
   }

   public void updateCatchupPlanner() {
      this.catchupPlannerCount++;
      this.catchupCount++;
   }

   public String formatRootFlag(int rootValue, int rootValue2, int rootValue3, double rootValue4) {
      return String.format(
         Locale.ROOT,
         "clientTick=%d wallClockDeltaMs=%d clientMovePacketsThisTick=%d autoMovePacketsThisTick=%d catchup=%s compressed=%s catchupDepth=%d plannerMs=%d serverCorrectionAge=%d serverCorrectionDistance=%.3f",
         this.consecutiveCatchupTicks,
         this.getTime(),
         rootValue,
         rootValue2,
         this.catchupPlannerActive,
         this.isTimeFlag(),
         this.catchupPlannerValue,
         this.getMax(),
         rootValue3,
         rootValue4
      );
   }

   public void handleCatchupPlanner(long time, int catchupPlannerValue) {
      this.consecutiveCatchupTicks++;
      this.catchupSampleCount++;
      this.plannerTicksValue = this.previousSampleNanos < 0L ? -1L : time - this.previousSampleNanos;
      this.catchupPlannerActive = this.previousSampleNanos >= 0L && catchupPlannerValue == 0 && (this.plannerTicksValue < 40000000L || this.catchupPlannerActive);
      if (this.catchupPlannerActive) {
         this.plannerTicksCount++;
         this.plannerCatchupValue++;
      }

      this.catchupPlannerValue = this.isTimeFlag() ? this.catchupPlannerValue + 1 : 0;
      this.previousSampleNanos = this.lastNanoTimeCatchupPlanner = time;
   }

   public int getPlannerTicksCount() {
      return this.plannerTicksCount;
   }

   public boolean canTime(long nanoTime) {
      if (this.lastNanoTime >= 0L && nanoTime - this.lastNanoTime < 500000000L) {
         return false;
      } else {
         this.lastNanoTime = nanoTime;
         return true;
      }
   }

   public void updateTimeCatchup() {
      this.previousSampleNanos = this.lastNanoTimeCatchupPlanner = this.nanoTimeCatchup = -1L;
      this.consecutiveCatchupTicks = 0;
      this.catchupPlannerActive = false;
      this.plannerTicksValue = -1L;
      this.plannerTicksCount = this.plannerCatchupValue = this.catchupPlannerCount = this.catchupCount = this.catchupSampleCount = this.catchupPlannerValue = 0;
      this.catchupElapsedNanos = this.catchupTotalNanos = this.catchupValue = 0L;
      this.lastTick = this.lastPlannerLimitNanos = this.lastNanoTimeCatchup = this.lastNanoTime = this.lastThrottleNanos = -1L;
      this.catchupActive = false;
   }

   public String formatRootFlag(int rootValue, int rootValue2, int rootValue3, String rootText) {
      return String.format(
         Locale.ROOT,
         "tickId=%d plannerMs=%d movementStepsThisTick=%d autoMovementPacketsThisTick=%d clientMovePacketsThisTick=%d routeState=%s catchupTicksObserved=%d catchup=%s gapMs=%d",
         this.consecutiveCatchupTicks,
         this.getMax(),
         rootValue,
         rootValue2,
         rootValue3,
         rootText,
         this.plannerTicksCount,
         this.catchupPlannerActive,
         this.getTime()
      );
   }

   public boolean canTimeForLong(long nanoTime) {
      if (this.lastNanoTimeCatchup >= 0L && nanoTime - this.lastNanoTimeCatchup < 500000000L) {
         return false;
      } else {
         this.lastNanoTimeCatchup = nanoTime;
         return true;
      }
   }

   public int getCatchupPlannerCount() {
      return this.catchupPlannerCount;
   }

   public boolean canTimeLastThrottleNanos(long nanoTime) {
      if (this.lastThrottleNanos >= 0L && nanoTime - this.lastThrottleNanos < 500000000L) {
         return false;
      } else {
         this.lastThrottleNanos = nanoTime;
         return true;
      }
   }

   public void handleMaxTime(long nanoTime) {
      this.catchupElapsedNanos = Math.max(0L, nanoTime - this.lastNanoTimeCatchupPlanner);
      this.catchupTotalNanos = this.catchupTotalNanos + this.catchupElapsedNanos;
      this.catchupValue = Math.max(this.catchupValue, this.catchupElapsedNanos);
      this.nanoTimeCatchup = nanoTime;
      if (this.catchupActive) {
         this.lastTick = nanoTime;
         this.catchupActive = false;
      }
   }

   public long getMax() {
      return this.catchupElapsedNanos / 1000000L;
   }

   public String getFormat() {
      String format = String.format(
         Locale.ROOT,
         "ticks=%d catchupTicks=%d plannerSkips=%d plannerAvgMs=%.1f plannerMaxMs=%d catchupTicksObserved=%d plannerSkipsTotal=%d",
         this.catchupSampleCount,
         this.plannerCatchupValue,
         this.catchupCount,
         this.catchupSampleCount == 0 ? 0.0 : this.catchupTotalNanos / 1000000.0 / this.catchupSampleCount,
         this.catchupValue / 1000000L,
         this.plannerTicksCount,
         this.catchupPlannerCount
      );
      this.catchupSampleCount = this.plannerCatchupValue = this.catchupCount = 0;
      this.catchupTotalNanos = this.catchupValue = 0L;
      return format;
   }

   public long getTime() {
      return this.plannerTicksValue < 0L ? -1L : this.plannerTicksValue / 1000000L;
   }

   public boolean canTimeFlag(long plannerTicksValue) {
      if (this.catchupActive) {
         return false;
      } else if (this.lastTick >= 0L && plannerTicksValue - this.lastTick < 250000000L) {
         return false;
      } else {
         this.catchupActive = true;
         return true;
      }
   }

   public int getValue() {
      return this.consecutiveCatchupTicks;
   }
}
