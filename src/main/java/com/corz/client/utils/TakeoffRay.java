package com.corz.client.utils;

import java.util.Locale;

public final class TakeoffRay {
   private int windowPlacementCount;
   private TakeoffRay.Failure timeoutDriftedType;
   private int prepareStartTick;
   private final TakeoffRay.Certificate provableHeadroomData;
   private int takeoffGroundValue;
   private boolean windowPassedFlag;
   private int groundWaitingCount;
   private int windowRayCount;
   private TakeoffRay.Phase prepareClickedType = TakeoffRay.Phase.PREPARE;

   public int getClampedValue(int takeoffRayValue) {
      return this.prepareStartTick == Integer.MIN_VALUE ? 0 : takeoffRayValue - this.prepareStartTick;
   }

   public boolean isClickedPrepare() {
      return this.prepareClickedType == TakeoffRay.Phase.COMPLETE || this.prepareClickedType == TakeoffRay.Phase.FAILED;
   }

   public String getFormat(int takeoffRayValue) {
      return String.format(
         Locale.ROOT,
         "phase=%s attempts=%d/%d clicks=%d ticks=%d airborneTicks=%d everClear=%s failure=%s",
         this.prepareClickedType,
         this.groundWaitingCount,
         2,
         this.windowPlacementCount,
         this.getClampedValue(takeoffRayValue),
         this.windowRayCount,
         this.windowPassedFlag,
         this.timeoutDriftedType
      );
   }

   public int getGroundWaitingCount() {
      return this.groundWaitingCount;
   }

   public TakeoffRay.Phase getPrepareClickedType() {
      return this.prepareClickedType;
   }

   public TakeoffRay(TakeoffRay.Certificate provableHeadroomData) {
      this.timeoutDriftedType = TakeoffRay.Failure.NONE;
      this.prepareStartTick = Integer.MIN_VALUE;
      this.takeoffGroundValue = Integer.MIN_VALUE;
      this.provableHeadroomData = provableHeadroomData;
      if (provableHeadroomData == null || !provableHeadroomData.isReject()) {
         this.prepareClickedType = TakeoffRay.Phase.FAILED;
         this.timeoutDriftedType = TakeoffRay.Failure.NOT_CERTIFIED;
      }
   }

   public TakeoffRay.Failure getTimeoutDriftedType() {
      return this.timeoutDriftedType;
   }

   public int getWindowPlacementCount() {
      return this.windowPlacementCount;
   }

   public TakeoffRay.Command failWithReason(TakeoffRay.Failure failure, String takeoffRayText) {
      return this.getClickedPrepare(failure, takeoffRayText);
   }

   public boolean isPrepareGroundReached() {
      return this.prepareClickedType == TakeoffRay.Phase.COMPLETE;
   }

   private TakeoffRay.Command getClickedPrepare(TakeoffRay.Failure timeoutDriftedType, String clickedPrepareText) {
      this.prepareClickedType = TakeoffRay.Phase.FAILED;
      this.timeoutDriftedType = timeoutDriftedType;
      return new TakeoffRay.Command(false, false, false, this.prepareClickedType, clickedPrepareText);
   }

   public TakeoffRay.Command getClickedPrepare(TakeoffRay.Sample sample) {
      if (this.prepareStartTick == Integer.MIN_VALUE) {
         this.prepareStartTick = sample.tick();
         this.takeoffGroundValue = sample.tick();
      }

      if (this.prepareClickedType == TakeoffRay.Phase.COMPLETE || this.prepareClickedType == TakeoffRay.Phase.FAILED) {
         return new TakeoffRay.Command(
            false, false, false, this.prepareClickedType, this.prepareClickedType == TakeoffRay.Phase.FAILED ? this.timeoutDriftedType.name() : "complete"
         );
      } else if (sample.targetBuilt()) {
         this.prepareClickedType = TakeoffRay.Phase.COMPLETE;
         return new TakeoffRay.Command(false, false, false, this.prepareClickedType, "world confirmed");
      } else if (!sample.feetOnColumn()) {
         return this.getClickedPrepare(TakeoffRay.Failure.DRIFTED, "feet left the column");
      } else {
         switch (this.prepareClickedType) {
            case PREPARE:
               if (sample.tick() - this.takeoffGroundValue > 40) {
                  return this.getClickedPrepare(TakeoffRay.Failure.PREPARE_TIMEOUT, "aim/brake never settled");
               } else if (sample.horizontalSpeed() > 0.02) {
                  return new TakeoffRay.Command(
                     false, false, sample.slippery(), this.prepareClickedType, sample.slippery() ? "braking before takeoff" : "coasting to a stop before takeoff"
                  );
               } else if (!sample.aimAligned()) {
                  return new TakeoffRay.Command(false, false, false, this.prepareClickedType, "turning onto the support top");
               } else {
                  if (!sample.onGround()) {
                     return new TakeoffRay.Command(false, false, false, this.prepareClickedType, "waiting for ground");
                  }

                  this.groundWaitingCount++;
                  this.prepareClickedType = TakeoffRay.Phase.TAKEOFF;
                  this.takeoffGroundValue = sample.tick();
                  this.windowRayCount = 0;
                  this.windowPassedFlag = false;
                  return new TakeoffRay.Command(true, false, false, this.prepareClickedType, "takeoff " + this.groundWaitingCount + "/" + 2);
               }
            case TAKEOFF:
               if (!sample.onGround()) {
                  this.prepareClickedType = TakeoffRay.Phase.AIRBORNE;
                  this.takeoffGroundValue = sample.tick();
                  return this.getWindowRay(sample);
               } else {
                  if (sample.tick() - this.takeoffGroundValue > 4) {
                     return this.getClickedPrepare(TakeoffRay.Failure.NO_TAKEOFF, "jump pressed but the body never left the ground");
                  }

                  return new TakeoffRay.Command(false, false, false, this.prepareClickedType, "takeoff pending");
               }
            case AIRBORNE:
               return this.getWindowRay(sample);
            case CLICKED:
               if (sample.tick() - this.takeoffGroundValue > 20) {
                  return this.getClickedPrepare(TakeoffRay.Failure.NO_CONFIRMATION, "clicked, the world never showed the block");
               }

               return new TakeoffRay.Command(false, false, false, this.prepareClickedType, "awaiting world confirm");
            default:
               return new TakeoffRay.Command(false, false, false, this.prepareClickedType, this.timeoutDriftedType.name());
         }
      }
   }

   private TakeoffRay.Command getPrepareGround(TakeoffRay.Sample sample, String groundText) {
      if (this.groundWaitingCount >= 2) {
         return this.getClickedPrepare(TakeoffRay.Failure.WINDOW_MISSED, groundText + " (attempts exhausted)");
      } else {
         this.prepareClickedType = TakeoffRay.Phase.PREPARE;
         this.takeoffGroundValue = sample.tick();
         return new TakeoffRay.Command(
            false, false, false, this.prepareClickedType, "attempt " + this.groundWaitingCount + " missed: " + groundText + " - preparing once more"
         );
      }
   }

   public TakeoffRay.Certificate getProvableHeadroomData() {
      return this.provableHeadroomData;
   }

   public boolean isPrepareFinished() {
      return this.prepareClickedType == TakeoffRay.Phase.FAILED;
   }

   private TakeoffRay.Command getWindowRay(TakeoffRay.Sample sample) {
      this.windowRayCount++;
      if (sample.clearOfTarget()) {
         this.windowPassedFlag = true;
      }

      if (sample.clearOfTarget() && sample.rayOnSupportTop() && sample.targetAir()) {
         this.windowPlacementCount++;
         this.prepareClickedType = TakeoffRay.Phase.CLICKED;
         this.takeoffGroundValue = sample.tick();
         return new TakeoffRay.Command(false, true, false, this.prepareClickedType, "click in the placement window");
      } else if (sample.onGround() && this.windowRayCount > 1) {
         return this.getPrepareGround(sample, this.windowPassedFlag ? "window passed without a legal ray" : "never cleared the cell");
      } else {
         return this.windowRayCount > 16
            ? this.getPrepareGround(sample, "airborne longer than a jump")
            : new TakeoffRay.Command(false, false, false, this.prepareClickedType, sample.clearOfTarget() ? "in the window, ray not on the support top" : "rising");
      }
   }

   public record Certificate(
      long target,
      long support,
      boolean supportTopFull,
      boolean headroomClear,
      boolean feetOnTarget,
      boolean materialInHand,
      boolean plainOrExactState,
      String reject
   ) {
      public static TakeoffRay.Certificate getProvableHeadroom(
         long headroomClear, long feetOnTarget, boolean support, boolean materialInHand, boolean target, boolean plainOrExactState, boolean supportTopFull
      ) {
         String provableHeadroomText = null;
         if (!target) {
            provableHeadroomText = "FEET_NOT_ON_TARGET";
         } else if (!support) {
            provableHeadroomText = "SUPPORT_TOP_NOT_FULL";
         } else if (!materialInHand) {
            provableHeadroomText = "NO_HEADROOM";
         } else if (!plainOrExactState) {
            provableHeadroomText = "NO_MATERIAL";
         } else if (!supportTopFull) {
            provableHeadroomText = "STATE_NOT_PROVABLE";
         }

         return new TakeoffRay.Certificate(headroomClear, feetOnTarget, support, materialInHand, target, plainOrExactState, supportTopFull, provableHeadroomText);
      }

      public boolean isReject() {
         return this.reject == null;
      }
   }

   public record Command(boolean jump, boolean click, boolean brake, TakeoffRay.Phase phase, String note) {
   }

   public static enum Failure {
      NONE,
      NOT_CERTIFIED,
      PREPARE_TIMEOUT,
      NO_TAKEOFF,
      AIRBORNE_TIMEOUT,
      WINDOW_MISSED,
      NO_CONFIRMATION,
      DRIFTED,
      SERVER_CORRECTION,
      ATTEMPTS_EXHAUSTED;
   }

   public static enum Phase {
      PREPARE,
      TAKEOFF,
      AIRBORNE,
      CLICKED,
      COMPLETE,
      FAILED;
   }

   public record Sample(
      int tick,
      double y,
      boolean onGround,
      double horizontalSpeed,
      boolean feetOnColumn,
      boolean clearOfTarget,
      boolean aimAligned,
      boolean rayOnSupportTop,
      boolean targetAir,
      boolean targetBuilt,
      boolean slippery
   ) {
   }
}
