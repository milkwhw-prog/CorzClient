package com.corz.client.utils;

public final class RotateQuaternionfUtils {

   public static float getAmount(double rotateQuaternionfValue) {
      return (float)(-rotateQuaternionfValue * 1.6F);
   }

   private RotateQuaternionfUtils() {
   }

   public static float getAmount(float rotateQuaternionfValue) {
      return rotateQuaternionfValue % 360.0F;
   }

   public static float getAmountForDouble(double rotateQuaternionfValue) {
      return (float)(rotateQuaternionfValue * 0.6F);
   }

   public static float getAmountForFloat(float rotateQuaternionfLimit) {
      return rotateQuaternionfLimit < -35.0F ? -35.0F : (rotateQuaternionfLimit > 35.0F ? 35.0F : rotateQuaternionfLimit);
   }

   public static final class Orbit {
      private final float minOrbitDistance;
      private float yawVelocity;
      private float targetDistance;
      private float orbitAmount;
      private final float parseFloat;
      private final float maxOrbitBound;
      private float orbitCount;
      private float smoothedDistance;
      private final float defaultPitch;
      private final float defaultYaw;
      private float smoothedPitch;
      private float smoothedYaw;
      private final float absOrbitValue;
      private boolean orbitActive;
      private float targetYaw;
      private float targetPitch;

      public Orbit(float amount, float orbitValue, float floatLimit, float orbitBound, float hashHuntBound, float planValuesItem) {
         this.minOrbitDistance = Math.min(orbitBound, hashHuntBound);
         this.maxOrbitBound = Math.max(orbitBound, hashHuntBound);
         this.absOrbitValue = Math.abs(planValuesItem);
         this.defaultYaw = amount;
         this.defaultPitch = this.getAbs(orbitValue);
         this.parseFloat = this.getParseFloat(floatLimit);
         this.updateAmountMaxSmoothedYaw();
      }

      public void handleAmountMax(float orbitFactor) {
         if (!(orbitFactor <= 0.0F)) {
            if (this.orbitActive) {
               this.yawVelocity = this.yawVelocity + (this.orbitCount / orbitFactor - this.yawVelocity) * 0.5F;
               this.orbitCount = 0.0F;
            } else if (this.yawVelocity != 0.0F) {
               this.targetYaw = this.targetYaw + this.yawVelocity * orbitFactor;
               this.yawVelocity = this.yawVelocity * (float)Math.exp(-4.5F * orbitFactor);
               if (Math.abs(this.yawVelocity) < 1.0F) {
                  this.yawVelocity = 0.0F;
               }
            } else if (this.orbitAmount != 0.0F) {
               this.targetYaw = this.targetYaw + this.orbitAmount * orbitFactor;
            }

            this.smoothedYaw = getAmount(this.smoothedYaw, this.targetYaw, 22.0F, orbitFactor);
            this.smoothedPitch = getAmount(this.smoothedPitch, this.targetPitch, 22.0F, orbitFactor);
            this.smoothedDistance = getAmount(this.smoothedDistance, this.targetDistance, 14.0F, orbitFactor);
            if (Math.abs(this.targetYaw) > 720.0F) {
               float roundScaled = Math.round(this.targetYaw / 360.0F) * 360.0F;
               this.targetYaw -= roundScaled;
               this.smoothedYaw -= roundScaled;
            }
         }
      }

      public void handleAmount(double exponent) {
         this.targetDistance = this.getParseFloat(this.targetDistance * (float)Math.pow(1.15F, exponent));
      }

      public void handleAmount(float orbitValue) {
         this.targetDistance = this.getParseFloat(orbitValue);
      }

      private float getAbs(float absLimit) {
         return absLimit < -this.absOrbitValue ? -this.absOrbitValue : (absLimit > this.absOrbitValue ? this.absOrbitValue : absLimit);
      }

      private static float getAmount(float orbitValue, float orbitValue2, float orbitValue3, float orbitFactor) {
         float orbitValue4 = orbitValue + (orbitValue2 - orbitValue) * (1.0F - (float)Math.exp(-orbitValue3 * orbitFactor));
         return Math.abs(orbitValue2 - orbitValue4) < 0.001F ? orbitValue2 : orbitValue4;
      }

      public float getAmount() {
         return this.targetDistance;
      }

      public float getSmoothedPitch() {
         return this.smoothedPitch;
      }

      public void updateAmountMax() {
         this.targetYaw = this.defaultYaw + Math.round((this.smoothedYaw - this.defaultYaw) / 360.0F) * 360.0F;
         this.targetPitch = this.defaultPitch;
         this.targetDistance = this.parseFloat;
         this.yawVelocity = 0.0F;
         this.orbitCount = 0.0F;
      }

      public float getSmoothedDistance() {
         return this.smoothedDistance;
      }

      public Orbit(float orbitValue, float orbitValue2, float floatLimit, float orbitBound, float hashHuntBound) {
         this(orbitValue, orbitValue2, floatLimit, orbitBound, hashHuntBound, 35.0F);
      }

      public void updateMaxAmount() {
         this.orbitActive = true;
         this.yawVelocity = 0.0F;
         this.orbitCount = 0.0F;
      }

      public RotateQuaternionfUtils.Orbit getAmount(float amount) {
         this.orbitAmount = amount;
         return this;
      }

      public float getSmoothedYaw() {
         return this.smoothedYaw;
      }

      public void handleAmount(double orbitValue, double rotateQuaternionfValue) {
         float orbitValue3 = RotateQuaternionfUtils.getAmount(orbitValue);
         this.targetYaw += orbitValue3;
         this.orbitCount += orbitValue3;
         this.targetPitch = this.getAbs(this.targetPitch + RotateQuaternionfUtils.getAmountForDouble(rotateQuaternionfValue));
      }

      public void updateMaxFlag() {
         this.orbitActive = false;
         if (Math.abs(this.yawVelocity) < 12.0F) {
            this.yawVelocity = 0.0F;
         }

         this.yawVelocity = Math.max(-720.0F, Math.min(720.0F, this.yawVelocity));
      }

      public float getMax() {
         return this.yawVelocity;
      }

      private float getParseFloat(float floatLimit) {
         return floatLimit < this.minOrbitDistance ? this.minOrbitDistance : (floatLimit > this.maxOrbitBound ? this.maxOrbitBound : floatLimit);
      }

      public void updateAmountMaxSmoothedYaw() {
         this.smoothedYaw = this.targetYaw = this.defaultYaw;
         this.smoothedPitch = this.targetPitch = this.defaultPitch;
         this.smoothedDistance = this.targetDistance = this.parseFloat;
         this.yawVelocity = 0.0F;
         this.orbitCount = 0.0F;
         this.orbitActive = false;
      }

      public boolean isFlag() {
         return this.orbitActive;
      }
   }
}
