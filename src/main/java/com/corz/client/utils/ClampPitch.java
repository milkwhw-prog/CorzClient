package com.corz.client.utils;

import com.corz.client.world.MathClampUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.WrapDegreesUtils;

public final class ClampPitch {
   private float pitch;
   private float yaw;
   public static final int PITCH_CLAMP_STEP = 1;
   private int lastTargetPriority;
   private final float wrapSmoothingFactor;
   private float maxStepDegrees = 30.0F;
   private final float smoothingAmount;
   private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
   private boolean overrideActive;
   private float smoothedYaw;
   private float targetYawDegrees;
   private float targetPitchDegrees;
   private boolean hasTarget;
   private float smoothedPitch;

   public void applyInterpolatedAngles(DeltaAngle deltaAngle) {
      if (this.overrideActive && CLIENT.player != null) {
         float tickDelta = deltaAngle.getTickDelta();
         deltaAngle.setAngleDegrees(MathHelper.lerpAngleDegrees(tickDelta, this.yaw, this.smoothedYaw));
         deltaAngle.setLerp(MathHelper.lerp(tickDelta, this.pitch, this.smoothedPitch));
         deltaAngle.updateFlag();
      }
   }

   public void updateSmoothedRotation() {
      if (CLIENT.player == null) {
         this.overrideActive = false;
      } else if (!this.hasTarget) {
         this.overrideActive = false;
      } else {
         this.yaw = CLIENT.player.getYaw();
         this.pitch = CLIENT.player.getPitch();
         float newYaw = WrapDegreesUtils.stepAngleProportional(this.yaw, this.targetYawDegrees, this.smoothingAmount, this.maxStepDegrees, this.wrapSmoothingFactor);
         float newPitch = MathHelper.clamp(WrapDegreesUtils.stepAngleProportional(this.pitch, this.targetPitchDegrees, this.smoothingAmount, this.maxStepDegrees, this.wrapSmoothingFactor), -90.0F, 90.0F);
         CLIENT.player.setYaw(newYaw);
         CLIENT.player.setPitch(newPitch);
         this.smoothedYaw = newYaw;
         this.smoothedPitch = newPitch;
         this.overrideActive = true;
      }
   }

   public void setTargetAngles(float degrees, float value, int eyeLimit) {
      if (!this.hasTarget || eyeLimit >= this.lastTargetPriority) {
         this.hasTarget = true;
         this.lastTargetPriority = eyeLimit;
         this.targetYawDegrees = degrees;
         this.targetPitchDegrees = MathHelper.clamp(value, -90.0F, 90.0F);
      }
   }

   public ClampPitch(float amount, float amount2) {
      this.smoothingAmount = amount;
      this.wrapSmoothingFactor = amount2;
   }

   public void setTargetFromPoint(Vec3d vec, int eyeValue) {
      if (CLIENT.player != null && vec != null) {
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(CLIENT.player.getEyePos(), vec);
         this.setTargetAngles(wrapDegrees[0], wrapDegrees[1], eyeValue);
      }
   }

   public void clearTarget() {
      this.hasTarget = false;
   }

   public void setMaxStep(float value) {
      this.maxStepDegrees = MathHelper.clamp(value, 4.0F, 60.0F);
   }

   public boolean isPointWithinAngle(Vec3d vec, float eyeValue) {
      if (CLIENT.player != null && vec != null) {
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(CLIENT.player.getEyePos(), vec);
         return this.isAngleWithinTolerance(wrapDegrees[0], wrapDegrees[1], eyeValue);
      } else {
         return false;
      }
   }

   public boolean isAngleWithinTolerance(float playerValue, float playerValue2, float playerLimit) {
      if (CLIENT.player == null) {
         return false;
      } else {
         float absWrapDegrees = Math.abs(MathHelper.wrapDegrees(playerValue - CLIENT.player.getYaw()));
         float abs3 = Math.abs(playerValue2 - CLIENT.player.getPitch());
         return absWrapDegrees <= playerLimit && abs3 <= playerLimit;
      }
   }

   public void clearTargetAndOverride() {
      this.hasTarget = false;
      this.overrideActive = false;
   }
}
