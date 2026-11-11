package com.corz.client.utils;

import java.util.Random;

public final class RotationAccelerationLimiter {
   public static final float MIN_STEP_DEGREES = 0.35F;
   private float yawStepDegrees;
   public static final float MAX_SPEED_CAP_DEGREES = 30.0F;
   private float pitchStepDegrees;
   private float peakYawStepDegrees;
   private float maxStepDegrees;
   private float peakStepChangeDegrees;
   private float peakPitchStepDegrees;
   private final float maxAccelerationDegrees;

   private double getStoppingDistance(double pitchFactor) {
      if (pitchFactor <= 0.0) {
         return 0.0;
      } else {
         int ceil2 = (int)Math.ceil(pitchFactor / this.maxAccelerationDegrees - 1.0E-9);
         if (ceil2 < 1) {
            ceil2 = 1;
         }

         return ceil2 * pitchFactor - this.maxAccelerationDegrees * ceil2 * (ceil2 - 1) / 2.0;
      }
   }

   public static float wrapDegrees(float pitchValue) {
      float pitchTotal = pitchValue % 360.0F;
      if (pitchTotal >= 180.0F) {
         pitchTotal -= 360.0F;
      }

      if (pitchTotal < -180.0F) {
         pitchTotal += 360.0F;
      }

      return pitchTotal;
   }

   public RotationAccelerationLimiter(float pitchBound, float rocketsElytraBound) {
      this.maxStepDegrees = Math.max(0.001F, pitchBound);
      this.maxAccelerationDegrees = Math.max(0.001F, rocketsElytraBound);
   }

   public RotationAccelerationLimiter.Output computeNextRotation(float pitchValue, float pitchLimit, float yawClampValue, float pitchValue4, float f3, float f4) {
      float pitchValue7 = pitchValue + wrapDegrees(yawClampValue + f3 - pitchValue);
      float pitch = clampPitch(pitchValue4 + f4);
      float amount = this.computeAxisStep(pitchValue7 - pitchValue, this.yawStepDegrees);
      float axisStep = this.computeAxisStep(pitch - clampPitch(pitchLimit), this.pitchStepDegrees);
      this.peakStepChangeDegrees = Math.max(this.peakStepChangeDegrees, Math.max(Math.abs(amount - this.yawStepDegrees), Math.abs(axisStep - this.pitchStepDegrees)));
      this.yawStepDegrees = amount;
      this.pitchStepDegrees = axisStep;
      this.peakYawStepDegrees = Math.max(this.peakYawStepDegrees, Math.abs(this.yawStepDegrees));
      this.peakPitchStepDegrees = Math.max(this.peakPitchStepDegrees, Math.abs(this.pitchStepDegrees));
      return new RotationAccelerationLimiter.Output(pitchValue + this.yawStepDegrees, clampPitch(clampPitch(pitchLimit) + this.pitchStepDegrees), this.yawStepDegrees, this.pitchStepDegrees);
   }

   public float getPeakStepChangeDegrees() {
      return this.peakStepChangeDegrees;
   }

   public float getYawStepDegrees() {
      return this.yawStepDegrees;
   }

   public static float getSeededSignedJitter(long pitchValue) {
      long legKeyScaled = pitchValue * -7046029254386353131L;
      long pitchValue2 = legKeyScaled ^ legKeyScaled >>> 30;
      long pitchValue3 = pitchValue2 * -4658895280553007687L;
      long pitchValue4 = pitchValue3 ^ pitchValue3 >>> 27;
      long pitchValue5 = pitchValue4 * -7723592293110705685L;
      long pitchValue6 = pitchValue5 ^ pitchValue5 >>> 31;
      double pitchValue7 = 1.5 + (pitchValue6 >>> 20) % 2001L / 2000.0 * 2.0;
      return (float)((pitchValue6 >>> 17 & 1L) == 0L ? pitchValue7 : -pitchValue7);
   }

   public float getPeakPitchStepDegrees() {
      return this.peakPitchStepDegrees;
   }

   public int countStepsToReach(float pitchValue) {
      double absDeltaDegrees = Math.abs(pitchValue);
      double pitchLimit2 = 0.0;

      int pitchCounter;
      for (pitchCounter = 0; absDeltaDegrees > 1.0E-4 && pitchCounter < 1000; pitchCounter++) {
         double pitchLimit = Math.min((double)this.maxStepDegrees, Math.min(pitchLimit2 + this.maxAccelerationDegrees, this.getMaxSpeedToStopWithin(absDeltaDegrees)));
         if (pitchLimit > absDeltaDegrees) {
            pitchLimit = absDeltaDegrees;
         }

         if (pitchLimit > 0.0 && pitchLimit < 0.35F) {
            pitchLimit = Math.min(0.35F, absDeltaDegrees);
         }

         absDeltaDegrees -= pitchLimit;
         pitchLimit2 = pitchLimit;
      }

      return pitchCounter;
   }

   public boolean isSettled() {
      return Math.abs(this.yawStepDegrees) < 0.35F && Math.abs(this.pitchStepDegrees) < 0.35F;
   }

   private static float clampPitch(float pitchLimit) {
      return pitchLimit < -90.0F ? -90.0F : (pitchLimit > 90.0F ? 90.0F : pitchLimit);
   }

   public void resetSteps() {
      this.yawStepDegrees = 0.0F;
      this.pitchStepDegrees = 0.0F;
   }

   public void resetPeaks() {
      this.peakYawStepDegrees = 0.0F;
      this.peakPitchStepDegrees = 0.0F;
      this.peakStepChangeDegrees = 0.0F;
   }

   public static boolean isNearMultipleOfForFloat(float pitchValue, float pitchLimit) {
      double round = Math.round(pitchValue / 45.0);
      return Math.abs(pitchValue - round * 45.0) <= pitchLimit;
   }

   public float getPeakYawStepDegrees() {
      return this.peakYawStepDegrees;
   }

   private double getMaxSpeedToStopWithin(double pitchLimit) {
      if (pitchLimit <= 0.0) {
         return 0.0;
      } else if (this.getStoppingDistance((double)this.maxStepDegrees) <= pitchLimit) {
         return this.maxStepDegrees;
      } else {
         double pitchValue = 0.0;
         double maxStepDegrees2 = this.maxStepDegrees;

         for (int i = 0; i < 60; i++) {
            double pitchFactor = (pitchValue + maxStepDegrees2) * 0.5;
            if (this.getStoppingDistance(pitchFactor) <= pitchLimit) {
               pitchValue = pitchFactor;
            } else {
               maxStepDegrees2 = pitchFactor;
            }
         }

         return pitchValue;
      }
   }

   private float computeAxisStep(float pitchValue, float pitchFactor) {
      float pitchFactor2 = pitchValue < 0.0F ? -1.0F : 1.0F;
      double absRemaining = Math.abs(pitchValue);
      double spotValue = pitchFactor * pitchFactor2;
      if (absRemaining < 1.0E-6 && Math.abs(spotValue) < 1.0E-6) {
         return 0.0F;
      } else {
         double pitchLimit = Math.min((double)this.maxStepDegrees, Math.min(spotValue + this.maxAccelerationDegrees, this.getMaxSpeedToStopWithin(absRemaining)));
         if (pitchLimit > absRemaining) {
            pitchLimit = absRemaining;
         }

         if (pitchLimit > 0.0 && pitchLimit < 0.35F) {
            pitchLimit = Math.min(0.35F, absRemaining);
         }

         if (pitchLimit < spotValue - this.maxAccelerationDegrees) {
            pitchLimit = spotValue - this.maxAccelerationDegrees;
         }

         return (float)(pitchLimit * pitchFactor2);
      }
   }

   public float getPitchStepDegrees() {
      return this.pitchStepDegrees;
   }

   public void setMaxSpeedDegrees(float pitchBound) {
      this.maxStepDegrees = Math.max(1.0F, Math.min(30.0F, pitchBound));
   }

   public static final class HumanAim {
      public static final float MIN_STEP_DEGREES = 0.35F;
      private float yawCarryOver;
      private float lastTargetYaw;
      private float lastPitchStepDegrees;
      private float lastTargetPitch;
      public static final float MAX_AIM_STEP_DEGREES = 30.0F;
      private float speedDriftFactor;
      private boolean hasPreviousTarget;
      private float pitchCarryOver;
      private float lastYawStepDegrees;
      private final Random random;
      private float aimNoiseFactor;
      private float speedJitterFactor = 1.0F;

      public boolean isSettled() {
         return Math.abs(this.lastYawStepDegrees) < 0.35F && Math.abs(this.lastPitchStepDegrees) < 0.35F;
      }

      public void reset() {
         this.hasPreviousTarget = false;
         this.yawCarryOver = 0.0F;
         this.pitchCarryOver = 0.0F;
         this.lastYawStepDegrees = 0.0F;
         this.lastPitchStepDegrees = 0.0F;
      }

      public static float getMaxStepForTurnSpeed(float humanAimLimit, float humanAimDivisor) {
         if (!(humanAimLimit <= 0.0F) && !(humanAimDivisor <= 0.0F)) {
            double humanAimValue;
            if (humanAimLimit > 20.0F) {
               double humanAimLimit2 = (humanAimLimit - 20.0F) / humanAimDivisor;
               humanAimValue = 0.05 <= humanAimLimit2 ? humanAimLimit - humanAimDivisor * 0.05 : 20.0 * Math.exp(-(humanAimDivisor / 20.0F) * (0.05 - humanAimLimit2));
            } else {
               humanAimValue = humanAimLimit * Math.exp(-humanAimDivisor * 0.05 / 20.0);
            }

            return (float)Math.min(30.0, Math.max(0.0, humanAimLimit - humanAimValue));
         } else {
            return 0.0F;
         }
      }

      public static float distanceToNearestMultipleOfForFloat(float humanAimValue) {
         float humanAimBound = (humanAimValue % 45.0F + 45.0F) % 45.0F;
         return Math.min(humanAimBound, 45.0F - humanAimBound);
      }

      public double sampleReactionTicks() {
         double randomFactor = 90.0;
         double randomValue = this.random.nextGaussian() * randomFactor * 0.3;
         double randomValue2 = -Math.log(1.0 - this.random.nextDouble()) * randomFactor * 0.4;
         return Math.max(Math.max(66L, Math.round(randomFactor * 0.55)), Math.round(randomFactor + randomValue + randomValue2)) / 50.0;
      }

      private static float wrapDegrees(float humanAimTotal) {
         humanAimTotal %= 360.0F;
         if (humanAimTotal >= 180.0F) {
            humanAimTotal -= 360.0F;
         }

         if (humanAimTotal < -180.0F) {
            humanAimTotal += 360.0F;
         }

         return humanAimTotal;
      }

      public HumanAim() {
         this(new Random());
      }

      public float[] computeHumanizedRotation(float randomValue, float f4, float amount, float pitchLimit, float randomBound) {
         float maxWantYaw = Math.max(randomBound, 1.0E-4F);
         pitchLimit = RotationAccelerationLimiter.clampPitch(pitchLimit);
         if (!this.hasPreviousTarget || Math.hypot(wrapDegrees(amount - this.lastTargetYaw), pitchLimit - this.lastTargetPitch) > 20.0) {
            this.speedJitterFactor = 0.8F + this.random.nextFloat() * 0.4F;
            this.aimNoiseFactor = this.random.nextFloat() * 0.6F;
         }

         this.hasPreviousTarget = true;
         this.lastTargetYaw = amount;
         this.lastTargetPitch = pitchLimit;
         this.speedDriftFactor = Math.max(0.8F, Math.min(1.2F, this.speedDriftFactor + (this.random.nextFloat() - 0.5F) * 0.24F));
         float humanAimDivisor = Math.min(520.0F * this.speedJitterFactor * this.speedDriftFactor, 600.0F);
         float wrapDegrees4 = wrapDegrees(nudgeOffGridAngle(amount, maxWantYaw, this.aimNoiseFactor) - randomValue);
         float planValuesItem = pitchLimit - f4;
         int randomFactor = 0;
         int round2 = 0;
         if (Math.abs(wrapDegrees4) <= maxWantYaw * 0.5F) {
            this.yawCarryOver = 0.0F;
         } else {
            float randomValue6 = this.yawCarryOver + Math.copySign(getMaxStepForTurnSpeed(Math.abs(wrapDegrees4), humanAimDivisor), wrapDegrees4);
            randomFactor = Math.round(randomValue6 / maxWantYaw);
            this.yawCarryOver = randomValue6 - randomFactor * maxWantYaw;
         }

         if (Math.abs(planValuesItem) <= maxWantYaw * 0.5F) {
            this.pitchCarryOver = 0.0F;
         } else {
            float randomValue7 = this.pitchCarryOver + Math.copySign(getMaxStepForTurnSpeed(Math.abs(planValuesItem), humanAimDivisor), planValuesItem);
            round2 = Math.round(randomValue7 / maxWantYaw);
            this.pitchCarryOver = randomValue7 - round2 * maxWantYaw;
         }

         for (int i = 0; randomFactor != 0 && i < 8 && distanceToNearestMultipleOfForFloat(randomValue + randomFactor * maxWantYaw) < 0.1F; i++) {
            int signum = Integer.signum(randomFactor);
            randomFactor += signum;
            this.yawCarryOver -= signum * maxWantYaw;
         }

         float randomValue8 = randomFactor == 0 ? randomValue : randomValue + randomFactor * maxWantYaw;
         float randomValue9 = round2 == 0 ? f4 : RotationAccelerationLimiter.clampPitch(f4 + round2 * maxWantYaw);
         this.lastYawStepDegrees = randomValue8 - randomValue;
         this.lastPitchStepDegrees = randomValue9 - f4;
         return new float[]{randomValue8, randomValue9};
      }

      public static float nudgeOffGridAngle(float humanAimValue, float randomFactor, float humanAimValue3) {
         float max4 = Math.max(0.35F, randomFactor * 1.5F + 0.1F);
         if (distanceToNearestMultipleOfForFloat(humanAimValue) >= max4) {
            return humanAimValue;
         } else {
            float humanAimValue5 = (humanAimValue % 45.0F + 45.0F) % 45.0F;
            float roundScaled = Math.round(humanAimValue / 45.0F) * 45.0F;
            return roundScaled + (humanAimValue5 < 22.5F ? 1.0F : -1.0F) * (max4 + humanAimValue3);
         }
      }

      public HumanAim(Random random) {
         this.speedDriftFactor = 1.0F;
         this.random = random;
      }
   }

   public record Output(float yaw, float pitch, float yawVelocity, float pitchVelocity) {
   }
}
