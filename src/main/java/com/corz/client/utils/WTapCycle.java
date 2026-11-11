package com.corz.client.utils;

public final class WTapCycle {
   private int pendingReleaseDelayTicks;
   private int holdDurationTicks;
   private long pendingDeadlineTick;
   private long armedAtTick;
   private int pendingEntityId;
   private boolean hasPendingHit;
   private int pendingHoldTicks;
   private int releaseDelayTicks;
   private long releasedAtTick;
   private WTapCycle.Phase phase = WTapCycle.Phase.IDLE;

   public void queuePendingTap(int value, long phaseIdleValue, int msToTicks, int rocketsBuyBound) {
      if (this.phase == WTapCycle.Phase.IDLE) {
         this.hasPendingHit = true;
         this.pendingEntityId = value;
         this.pendingDeadlineTick = phaseIdleValue + 10L;
         this.pendingReleaseDelayTicks = msToTicks;
         this.pendingHoldTicks = rocketsBuyBound;
      }
   }

   public boolean isIdle() {
      return this.phase == WTapCycle.Phase.IDLE;
   }

   public boolean isReleased() {
      return this.phase == WTapCycle.Phase.RELEASED;
   }

   public void reset() {
      this.phase = WTapCycle.Phase.IDLE;
      this.hasPendingHit = false;
   }

   public boolean armTap(long time, int phaseBound, int rocketsBuyBound) {
      if (this.phase != WTapCycle.Phase.IDLE) {
         return false;
      } else {
         this.releaseDelayTicks = Math.max(0, phaseBound);
         this.holdDurationTicks = Math.max(1, rocketsBuyBound);
         this.armedAtTick = time;
         this.hasPendingHit = false;
         this.phase = WTapCycle.Phase.ARMED;
         return true;
      }
   }

   public boolean confirmPendingTap(int wTapValue, long wTapValue2) {
      if (this.hasPendingHit && wTapValue == this.pendingEntityId) {
         this.hasPendingHit = false;
         return wTapValue2 <= this.pendingDeadlineTick && this.armTap(wTapValue2, this.pendingReleaseDelayTicks, this.pendingHoldTicks);
      } else {
         return false;
      }
   }

   public WTapCycle.Step advanceTap(long time, boolean nonePhaseFlag) {
      switch (this.phase) {
         case ARMED:
            if (!nonePhaseFlag) {
               this.reset();
               return WTapCycle.Step.NONE;
            } else {
               if (time > this.armedAtTick && time - this.armedAtTick >= Math.max(1, this.releaseDelayTicks)) {
                  this.phase = WTapCycle.Phase.RELEASED;
                  this.releasedAtTick = time;
                  return WTapCycle.Step.RELEASE;
               }

               return WTapCycle.Step.NONE;
            }
         case RELEASED:
            if (time - this.releasedAtTick < this.holdDurationTicks) {
               return WTapCycle.Step.HOLD;
            }

            this.reset();
            return WTapCycle.Step.REPRESS;
         default:
            return WTapCycle.Step.NONE;
      }
   }

   private static enum Phase {
      IDLE,
      ARMED,
      RELEASED;
   }

   public static enum Step {
      NONE,
      RELEASE,
      HOLD,
      REPRESS;
   }
}
