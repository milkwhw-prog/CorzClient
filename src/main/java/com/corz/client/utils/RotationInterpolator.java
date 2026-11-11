package com.corz.client.utils;

public final class RotationInterpolator {
   private float startYaw;
   private boolean active;
   private float startPitch;
   private float targetYaw;
   private long lastUpdateTick;
   private float targetPitch;

   private static float clampToUnit(float pitchYawLimit) {
      return !(pitchYawLimit > 0.0F) ? 0.0F : Math.min(pitchYawLimit, 1.0F);
   }

   public float getInterpolatedPitch(float pitchValue) {
      return this.startPitch + (this.targetPitch - this.startPitch) * clampToUnit(pitchValue);
   }

   public float getInterpolatedYaw(float yawValue) {
      return this.startYaw + (this.targetYaw - this.startYaw) * clampToUnit(yawValue);
   }

   public void deactivate() {
      this.active = false;
   }

   public boolean isApplicableAt(long pitchValue, float pitchValue2, float planValuesItem) {
      return this.active
         && pitchValue == this.lastUpdateTick
         && areNearlyEqual(this.targetYaw, pitchValue2)
         && areNearlyEqual(this.targetPitch, planValuesItem)
         && Math.abs(this.targetYaw - this.startYaw) < 180.0F
         && Math.abs(this.targetPitch - this.startPitch) < 180.0F;
   }

   public void setSegment(long time, float yaw, float pitch, float amount, float routedPartialValue) {
      if (Float.isFinite(yaw) && Float.isFinite(pitch) && Float.isFinite(amount) && Float.isFinite(routedPartialValue)) {
         this.lastUpdateTick = time;
         this.startYaw = yaw;
         this.startPitch = pitch;
         this.targetYaw = amount;
         this.targetPitch = routedPartialValue;
         this.active = true;
      } else {
         this.active = false;
      }
   }

   private static boolean areNearlyEqual(float pitchYawValue, float planValuesItem) {
      float pitchYawLimit = Math.max(1.0E-4F, 4.0F * Math.ulp(Math.max(Math.abs(pitchYawValue), Math.abs(planValuesItem))));
      return Math.abs(pitchYawValue - planValuesItem) < pitchYawLimit;
   }
}
