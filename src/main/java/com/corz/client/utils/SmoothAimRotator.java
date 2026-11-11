package com.corz.client.utils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class SmoothAimRotator {
   private Vec3d smoothedAimPoint;

   public boolean rotateTowardPoint(ClientPlayerEntity player, Vec3d vec3d, float vecClampFactor) {
      if (player != null && vec3d != null) {
         if (this.smoothedAimPoint == null || this.smoothedAimPoint.squaredDistanceTo(vec3d) > 9.0) {
            this.smoothedAimPoint = vec3d;
         }

         this.smoothedAimPoint = new Vec3d(
            this.smoothedAimPoint.x + (vec3d.x - this.smoothedAimPoint.x) * 0.35, this.smoothedAimPoint.y + (vec3d.y - this.smoothedAimPoint.y) * 0.35, this.smoothedAimPoint.z + (vec3d.z - this.smoothedAimPoint.z) * 0.35
         );
         Vec3d eyePos = player.getEyePos();
         double deltaX = this.smoothedAimPoint.x - eyePos.x;
         double deltaY = this.smoothedAimPoint.y - eyePos.y;
         double deltaZ = this.smoothedAimPoint.z - eyePos.z;
         double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
         float vecClampValue = (float)(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0);
         float clamp = MathHelper.clamp((float)(-Math.toDegrees(Math.atan2(deltaY, distance))), -90.0F, 90.0F);
         float yaw = player.getYaw();
         float pitch = player.getPitch();
         float wrapDegrees = MathHelper.wrapDegrees(vecClampValue - yaw);
         float deltaVar = clamp - pitch;
         float abs2 = Math.abs(wrapDegrees);
         float abs3 = Math.abs(deltaVar);
         if (abs2 <= 0.6F && abs3 <= 0.6F) {
            return true;
         } else {
            float min2 = Math.min(1.0F, abs2 / 35.0F);
            float vecClampValue4 = min2 * min2 * (3.0F - 2.0F * min2);
            float max = Math.max(2.5F, Math.min(22.0F, vecClampFactor * (0.25F + 0.75F * vecClampValue4)));
            float min3 = Math.min(1.0F, abs3 / 25.0F);
            float vecClampValue5 = min3 * min3 * (3.0F - 2.0F * min3);
            float max2 = Math.max(2.0F, Math.min(16.0F, vecClampFactor * (0.25F + 0.75F * vecClampValue5)));
            player.setYaw(yaw + MathHelper.clamp(wrapDegrees * 0.35F, -max, max));
            player.setPitch(MathHelper.clamp(pitch + MathHelper.clamp(deltaVar * 0.35F, -max2, max2), -90.0F, 90.0F));
            return abs2 < 3.5F && abs3 < 3.5F;
         }
      } else {
         return false;
      }
   }

   public static void snapYawTo(ClientPlayerEntity player, float yawWrapValue) {
      float yaw = player.getYaw();
      player.setYaw(yaw + MathHelper.wrapDegrees(yawWrapValue - yaw));
   }

   public static boolean stepYawToward(ClientPlayerEntity player, float yawWrapValue, float yawWrapFactor) {
      if (player == null) {
         return false;
      } else {
         float yaw = player.getYaw();
         float wrapDegrees = MathHelper.wrapDegrees(yawWrapValue - yaw);
         float yawWrapLimit = Math.abs(wrapDegrees);
         if (yawWrapLimit <= 0.3F) {
            return false;
         } else {
            float min2 = Math.min(1.0F, yawWrapLimit / 45.0F);
            float yawWrapValue2 = min2 * (2.0F - min2);
            float max = Math.max(3.0F, Math.min(20.0F, yawWrapFactor * (0.25F + 0.75F * yawWrapValue2)));
            float clamp = MathHelper.clamp(wrapDegrees * 0.35F, -max, max);
            player.setYaw(yaw + clamp);
            return yawWrapLimit > max;
         }
      }
   }

   public static float computeYawToward(double x, double y) {
      return (float)(Math.toDegrees(Math.atan2(y, x)) - 90.0);
   }
}
