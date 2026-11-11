package com.corz.client.utils;

import java.util.Random;
import com.corz.client.utils.RotationAccelerationLimiter;

public final class HumanAimRotator {
   private float burstSpeedFactor = 1.0F;
   private boolean hasLastTarget;
   private float yawSpeedJitterFactor;
   private static volatile boolean reducedStepLimit = false;
   private float yawAimBias;
   private float lastTargetPitch;
   private float yawResidualDegrees;
   private float driftSpeedFactor;
   private float lastTargetYaw;
   private final Random random;
   private float pitchStepCarry;
   private float pitchResidualDegrees;
   private float yawStepCarry;
   private float pitchSpeedJitterFactor = 0.845F;

   public boolean isAimSettled() {
      return Math.abs(this.yawResidualDegrees) < 0.35F && Math.abs(this.pitchResidualDegrees) < 0.35F;
   }

   public float[] computeHumanizedRotation(float randomValue, float pitch, float amount, float clampPitch2, float randomBound, float routedPartialValue) {
      if (Float.isFinite(randomValue) && Float.isFinite(pitch) && Float.isFinite(amount) && Float.isFinite(clampPitch2)) {
         float randomFactor = Float.isFinite(randomBound) ? Math.max(randomBound, 1.0E-4F) : 1.0E-4F;
         float randomFactor2 = Float.isFinite(routedPartialValue) ? Math.max(0.0F, routedPartialValue) : 0.0F;
         clampPitch2 = clampPitch(clampPitch2);
         if (!this.hasLastTarget || Math.hypot(wrapDegrees(amount - this.lastTargetYaw), clampPitch2 - this.lastTargetPitch) > 20.0) {
            this.burstSpeedFactor = 0.85F + this.random.nextFloat() * 0.3F;
            this.yawAimBias = this.random.nextFloat() * 0.6F;
            this.pitchSpeedJitterFactor = 0.72F + this.random.nextFloat() * 0.25F;
            this.yawSpeedJitterFactor = 0.95F + this.random.nextFloat() * 0.05F;
         }

         this.hasLastTarget = true;
         this.lastTargetYaw = amount;
         this.lastTargetPitch = clampPitch2;
         this.driftSpeedFactor = Math.max(0.85F, Math.min(1.15F, this.driftSpeedFactor + (this.random.nextFloat() - 0.5F) * 0.24F));
         float randomValue3 = randomFactor2 * this.burstSpeedFactor * this.driftSpeedFactor * this.yawSpeedJitterFactor;
         float humanAimDivisor = randomFactor2 * this.burstSpeedFactor * this.driftSpeedFactor * this.pitchSpeedJitterFactor;
         float planValuesItem = wrapDegrees(RotationAccelerationLimiter.HumanAim.nudgeOffGridAngle(amount, randomFactor, this.yawAimBias) - randomValue);
         float randomValue6 = clampPitch2 - pitch;
         int j;
         if (Math.abs(planValuesItem) <= randomFactor * 0.5F) {
            this.yawStepCarry = 0.0F;
            j = 0;
         } else {
            float clampStepDegrees7 = clampStepDegrees(this.yawStepCarry + Math.copySign(RotationAccelerationLimiter.HumanAim.getMaxStepForTurnSpeed(Math.abs(planValuesItem), randomValue3), planValuesItem));
            j = Math.round(clampStepDegrees7 / randomFactor);
            this.yawStepCarry = clampStepDegrees7 - j * randomFactor;
         }

         int randomFactor4;
         if (Math.abs(randomValue6) <= randomFactor * 0.5F) {
            this.pitchStepCarry = 0.0F;
            randomFactor4 = 0;
         } else {
            float clampStepDegrees8 = clampStepDegrees(this.pitchStepCarry + Math.copySign(RotationAccelerationLimiter.HumanAim.getMaxStepForTurnSpeed(Math.abs(randomValue6), humanAimDivisor), randomValue6));
            randomFactor4 = Math.round(clampStepDegrees8 / randomFactor);
            this.pitchStepCarry = clampStepDegrees8 - randomFactor4 * randomFactor;
         }

         if (j != 0) {
            int signum = Integer.signum(j);
            int min9 = Math.min(8, (int)Math.ceil(0.2F / randomFactor) + 1);
            if ((Math.abs(j) + min9) * randomFactor > 20.0F) {
               signum = -signum;
            }

            for (int i = 0; i < 8 && j != 0 && RotationAccelerationLimiter.HumanAim.distanceToNearestMultipleOfForFloat(randomValue + j * randomFactor) < 0.1F; i++) {
               j += signum;
               this.yawStepCarry -= signum * randomFactor;
            }
         }

         float randomValue10 = j == 0 ? randomValue : randomValue + j * randomFactor;
         float randomValue11 = randomFactor4 == 0 ? pitch : clampPitch(pitch + randomFactor4 * randomFactor);
         this.yawResidualDegrees = randomValue10 - randomValue;
         this.pitchResidualDegrees = randomValue11 - pitch;
         return new float[]{randomValue10, randomValue11};
      } else {
         this.yawResidualDegrees = 0.0F;
         this.pitchResidualDegrees = 0.0F;
         return new float[]{randomValue, pitch};
      }
   }

   private static float clampPitch(float playerBound) {
      return Math.max(-90.0F, Math.min(90.0F, playerBound));
   }

   private static float clampStepDegrees(float playerValue) {
      float playerValue2 = reducedStepLimit ? 15.0F : 20.0F;
      return Math.abs(playerValue) > playerValue2 ? Math.copySign(playerValue2, playerValue) : playerValue;
   }

   private static float wrapDegrees(float playerValue) {
      return (playerValue % 360.0F + 540.0F) % 360.0F - 180.0F;
   }

   public static void setReducedStepLimit(boolean flag) {
      reducedStepLimit = flag;
   }

   public HumanAimRotator(Random random) {
      this.yawSpeedJitterFactor = 0.975F;
      this.driftSpeedFactor = 1.0F;
      this.random = random == null ? new Random() : random;
   }

   public void resetAimState() {
      this.hasLastTarget = false;
      this.yawStepCarry = 0.0F;
      this.pitchStepCarry = 0.0F;
      this.yawResidualDegrees = 0.0F;
      this.pitchResidualDegrees = 0.0F;
   }

   public static float getMaxStepDegrees() {
      return reducedStepLimit ? 15.0F : 20.0F;
   }
}
