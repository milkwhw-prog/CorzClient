package com.corz.client.utils;

import com.corz.client.world.BoxLengthUtils;
import java.util.Random;
import com.corz.client.utils.HumanAimRotator;
import com.corz.client.utils.BotActionType;
import com.corz.client.utils.RotationAccelerationLimiter;

public final class TrackFlick {
   private long tickTimestamp;
   private boolean lockedCommitIssued;
   private float rotationStepDegrees;
   private double lastYawChangeDegrees;
   private boolean tickInitialized;
   private float currentPitch;
   private final HumanAimRotator aimHumanizer;
   private int commitMismatchStreak;
   private BotActionType pendingPurpose;
   private TrackFlick.Speed pendingSpeed;
   private boolean hasPreviousRotation;
   private int pendingMode;
   private float currentYaw;
   private float lastFlickPitch;
   private float currentPitchAim;
   private static final int MODE_SMOOTHED = 1;
   private boolean commitPending;
   private float lockedPitch;
   private float trackSpeedScale = 1.0F;
   private float lastFlickYaw;
   private int pendingPriority;
   private boolean lockedTrackConfirmed;
   private float currentYawAim;
   private TrackFlick.Commit lastCommit;
   private boolean trackLocked;
   private boolean rotationMatchedCommit;
   private float lockedYaw;

   public void confirmLockedTrack() {
      if (this.commitPending && this.trackLocked) {
         this.lockedTrackConfirmed = true;
      }
   }

   public float[] computeHumanizedRotation(float trackValue, float pitch, float trackValue3, float routedPartialValue, TrackFlick.Speed speed) {
      TrackFlick.Speed speed2 = speed == null ? TrackFlick.Speed.TRACK : speed;
      return this.aimHumanizer.computeHumanizedRotation(trackValue, pitch, trackValue3, routedPartialValue, this.rotationStepDegrees, speed2.turnSpeedDegrees * this.trackSpeedScale);
   }

   private void submitRequest(int value, BotActionType headingEatType, float amount, float routedPartialValue, TrackFlick.Speed flickTrackType, int flickValue) {
      if (this.commitPending && !this.trackLocked) {
         if (this.pendingMode == 0 || flickValue > this.pendingPriority) {
            this.pendingMode = value;
            this.pendingPurpose = headingEatType == null ? BotActionType.TRACK : headingEatType;
            this.currentYawAim = amount;
            this.currentPitchAim = routedPartialValue;
            this.pendingSpeed = flickTrackType;
            this.pendingPriority = flickValue;
         }
      }
   }

   public float getCurrentPitch() {
      return this.currentPitch;
   }

   public TrackFlick.Commit resolveCommit(boolean headingFlag) {
      if (!this.commitPending) {
         return null;
      } else {
         this.commitPending = false;
         TrackFlick.Commit data;
         if (headingFlag) {
            data = this.lockedTrackConfirmed && this.trackLocked
               ? new TrackFlick.Commit(this.tickTimestamp, this.lockedYaw, this.lockedPitch, this.pendingPurpose, false)
               : new TrackFlick.Commit(this.tickTimestamp, this.currentYaw, this.currentPitch, BotActionType.HOLD, true);
         } else if (this.pendingMode == 0) {
            data = null;
         } else if (this.pendingMode == 2) {
            float headingValue = (float)wrapDegrees((double)this.currentYawAim - this.currentYaw);
            float planValuesItem = clampPitch(this.currentPitchAim) - this.currentPitch;
            float floorToStep3 = floorToStep(HumanAimRotator.getMaxStepDegrees(), this.rotationStepDegrees);
            if (Math.abs(headingValue) > floorToStep3) {
               headingValue = Math.copySign(floorToStep3, headingValue);
            }

            if (Math.abs(planValuesItem) > floorToStep3) {
               planValuesItem = Math.copySign(floorToStep3, planValuesItem);
            }

            data = new TrackFlick.Commit(this.tickTimestamp, this.currentYaw + headingValue, clampPitch(this.currentPitch + planValuesItem), this.pendingPurpose, false);
         } else if (this.pendingMode == 1) {
            float[] amountRandom = this.aimHumanizer
               .computeHumanizedRotation(this.currentYaw, this.currentPitch, this.currentYawAim, this.currentPitchAim, this.rotationStepDegrees, this.pendingSpeed.turnSpeedDegrees * this.trackSpeedScale);
            data = new TrackFlick.Commit(this.tickTimestamp, amountRandom[0], clampPitch(amountRandom[1]), this.pendingPurpose, false);
         } else {
            data = new TrackFlick.Commit(this.tickTimestamp, this.currentYaw, this.currentPitch, this.pendingPurpose, true);
         }

         this.lockedCommitIssued = headingFlag && this.lockedTrackConfirmed && this.trackLocked && data != null;
         this.clearPendingRequest();
         if (data != null) {
            this.lastCommit = data;
         }

         return data;
      }
   }

   public void requestDirectRotation(BotActionType headingEatType, float flickValue, float routedPartialValue, int flickValue3) {
      if (Float.isFinite(flickValue) && Float.isFinite(routedPartialValue)) {
         this.submitRequest(2, headingEatType, flickValue, routedPartialValue, TrackFlick.Speed.TRACK, flickValue3);
      }
   }

   public float[] lockTrackRotation(BotActionType headingEatType, float trackValue, float flickBound, TrackFlick.Speed speed, int value, float trackLimit) {
      if (!this.commitPending) {
         return null;
      } else if (this.trackLocked) {
         return new float[]{this.lockedYaw, this.lockedPitch};
      } else if (!Float.isFinite(trackValue) || !Float.isFinite(flickBound)) {
         return null;
      } else if (this.pendingMode != 0 && this.pendingPriority > value) {
         return null;
      } else {
         TrackFlick.Speed flickTrackType = speed == null ? TrackFlick.Speed.TRACK : speed;
         float[] amountRandom = this.aimHumanizer.computeHumanizedRotation(this.currentYaw, this.currentPitch, trackValue, flickBound, this.rotationStepDegrees, flickTrackType.turnSpeedDegrees * this.trackSpeedScale);
         if (trackLimit > 0.0F) {
            this.snapToRotationStep(amountRandom, trackValue, flickBound, trackLimit);
         }

         this.lockedYaw = amountRandom[0];
         this.lockedPitch = clampPitch(amountRandom[1]);
         this.trackLocked = true;
         this.lockedTrackConfirmed = false;
         this.pendingMode = 2;
         this.pendingPurpose = headingEatType == null ? BotActionType.TRACK : headingEatType;
         this.currentYawAim = this.lockedYaw;
         this.currentPitchAim = this.lockedPitch;
         this.pendingSpeed = flickTrackType;
         this.pendingPriority = value;
         return new float[]{this.lockedYaw, this.lockedPitch};
      }
   }

   static float floorToStep(float flickValue, float flickLimit) {
      return flickLimit > 0.0F && Float.isFinite(flickLimit) ? (float)(Math.floor(flickValue / flickLimit) * flickLimit) : flickValue;
   }

   public TrackFlick(Random random) {
      this.pendingMode = 0;
      this.aimHumanizer = new HumanAimRotator(random == null ? new Random() : random);
   }

   public boolean hasPendingRequest() {
      return this.pendingMode != 0;
   }

   public void requestSmoothedRotation(BotActionType headingEatType, float flickValue, float routedPartialValue, TrackFlick.Speed speed, int flickValue3) {
      if (Float.isFinite(flickValue) && Float.isFinite(routedPartialValue)) {
         this.submitRequest(1, headingEatType, flickValue, routedPartialValue, speed == null ? TrackFlick.Speed.TRACK : speed, flickValue3);
      }
   }

   public boolean isLockedCommitActive() {
      return this.lockedCommitIssued || this.trackLocked && this.lockedTrackConfirmed;
   }

   public void setSpeedScale(float flickLimit) {
      this.trackSpeedScale = Float.isFinite(flickLimit) && flickLimit > 0.0F ? Math.max(0.25F, Math.min(2.0F, flickLimit)) : 1.0F;
   }

   public double getLastYawChangeDegrees() {
      return this.lastYawChangeDegrees;
   }

   private static double wrapDegrees(double flickValue) {
      return (flickValue % 360.0 + 540.0) % 360.0 - 180.0;
   }

   public boolean isYawChangeSmall() {
      return this.lastYawChangeDegrees <= 1.9;
   }

   private void clearPendingRequest() {
      this.pendingMode = 0;
      this.pendingPurpose = null;
      this.currentYawAim = 0.0F;
      this.currentPitchAim = 0.0F;
      this.pendingSpeed = null;
      this.pendingPriority = Integer.MIN_VALUE;
      this.trackLocked = false;
      this.lockedTrackConfirmed = false;
      this.lockedYaw = 0.0F;
      this.lockedPitch = 0.0F;
   }

   public void beginTick(long time, float amount, float amount2, float amount3) {
      boolean flickFlag = this.tickInitialized && time == this.tickTimestamp;
      this.tickTimestamp = time;
      this.currentYaw = amount;
      this.currentPitch = amount2;
      this.rotationStepDegrees = amount3;
      this.tickInitialized = true;
      this.commitPending = true;
      this.lockedCommitIssued = false;
      this.clearPendingRequest();
      if (!flickFlag) {
         boolean flickFlag2 = this.lastCommit != null && this.lastCommit.tick() == time - 1L;
         this.rotationMatchedCommit = flickFlag2 && BoxLengthUtils.canFlag(amount, amount2, this.lastCommit.yaw(), this.lastCommit.pitch());
         if (flickFlag2 && !this.rotationMatchedCommit) {
            this.commitMismatchStreak++;
            this.aimHumanizer.resetAimState();
         } else {
            this.commitMismatchStreak = 0;
         }

         if (this.hasPreviousRotation) {
            double abs4 = Math.abs(wrapDegrees((double)amount - this.lastFlickYaw));
            double flickLimit = Math.abs((double)amount2 - this.lastFlickPitch);
            if (abs4 > 1.0E-4 || flickLimit > 1.0E-4) {
               this.lastYawChangeDegrees = abs4;
            }
         }

         this.lastFlickYaw = amount;
         this.lastFlickPitch = amount2;
         this.hasPreviousRotation = true;
      }
   }

   public void requestHold(BotActionType headingEatType, int flickValue) {
      this.submitRequest(3, headingEatType, this.currentYaw, this.currentPitch, TrackFlick.Speed.TRACK, flickValue);
   }

   public boolean didRotationMatchCommit() {
      return this.rotationMatchedCommit;
   }

   private void snapToRotationStep(float[] flickValues, float flickValue, float flickBound, float flickLimit) {
      float flickDivisor = Float.isFinite(this.rotationStepDegrees) ? Math.max(this.rotationStepDegrees, 1.0E-4F) : 1.0E-4F;
      float planValuesItem = (float)wrapDegrees((double)flickValue - flickValues[0]);
      float flickValue4 = clampPitch(flickBound) - flickValues[1];
      if (Math.abs(planValuesItem) <= flickLimit && Math.abs(flickValue4) <= flickLimit) {
         float flickValue5 = Math.round(planValuesItem / flickDivisor) * flickDivisor;
         float flickValue6 = Math.round(flickValue4 / flickDivisor) * flickDivisor;
         float flickValue7 = flickValues[0] - this.currentYaw;
         float flickValue8 = flickValues[1] - this.currentPitch;
         if (!(Math.abs(flickValue7 + flickValue5) > HumanAimRotator.getMaxStepDegrees()) && !(Math.abs(flickValue8 + flickValue6) > HumanAimRotator.getMaxStepDegrees())) {
            if (flickValue5 != 0.0F && RotationAccelerationLimiter.HumanAim.distanceToNearestMultipleOfForFloat(flickValues[0] + flickValue5) >= 0.1F) {
               flickValues[0] += flickValue5;
            }

            flickValues[1] += flickValue6;
         }
      }
   }

   private static float clampPitch(float flickBound) {
      return Math.max(-90.0F, Math.min(90.0F, flickBound));
   }

   public int getCommitMismatchStreak() {
      return this.commitMismatchStreak;
   }

   public void reset() {
      this.aimHumanizer.resetAimState();
      this.tickInitialized = false;
      this.commitPending = false;
      this.tickTimestamp = 0L;
      this.currentYaw = 0.0F;
      this.currentPitch = 0.0F;
      this.rotationStepDegrees = 0.0F;
      this.rotationMatchedCommit = false;
      this.commitMismatchStreak = 0;
      this.hasPreviousRotation = false;
      this.lastFlickYaw = 0.0F;
      this.lastFlickPitch = 0.0F;
      this.lastYawChangeDegrees = 0.0;
      this.lastCommit = null;
      this.lockedCommitIssued = false;
      this.clearPendingRequest();
   }

   public float getCurrentYaw() {
      return this.currentYaw;
   }

   public TrackFlick.Commit getLastCommit() {
      return this.lastCommit;
   }

   public float[] lockTrackRotationUnsnapped(BotActionType headingEatType, float flickValue, float flickBound, TrackFlick.Speed speed, int flickValue3) {
      return this.lockTrackRotation(headingEatType, flickValue, flickBound, speed, flickValue3, 0.0F);
   }

   public record Commit(long tick, float yaw, float pitch, BotActionType purpose, boolean hold) {
   }

   public static enum Speed {
      TRACK(360.0F),
      ACTION(500.0F),
      FLICK(620.0F);
      public final float turnSpeedDegrees;

      private Speed(float amount) {
         this.turnSpeedDegrees = amount;
      }
   }
}
