package com.corz.client.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class MathClampUtils {
   private static boolean clampActive;
   private static final MinecraftClient mathClampMinecraftClient = MinecraftClient.getInstance();
   private static boolean mathClampActive;
   private static boolean wrapDegreesActive;
   private static float clamp;
   private static float yaw;
   private static int overridePriority = Integer.MIN_VALUE;
   private static boolean equals;
   private static float wrapDegrees;
   private static boolean clampApplied;
   private static boolean wrapAimActive;
   private static boolean rotationOverrideActive;
   private static float pitchClamp;
   private static boolean clampSettled;

   public static Vec3d getCosSin(float cosSinValue, float eyeProduct) {
      float angle = -cosSinValue * (float) (Math.PI / 180.0) - (float) Math.PI;
      float angle2 = -eyeProduct * (float) (Math.PI / 180.0);
      float cos = MathHelper.cos(angle);
      float sin = MathHelper.sin(angle);
      float cos2 = -MathHelper.cos(angle2);
      float sin2 = MathHelper.sin(angle2);
      return new Vec3d(sin * cos2, sin2, cos * cos2);
   }

   public static void updateFlag() {
      rotationOverrideActive = false;
      clampApplied = false;
      clampActive = false;
      clampSettled = false;
      mathClampActive = false;
      overridePriority = Integer.MIN_VALUE;
   }

   public static void handleWrapDegrees(Object obj, float degrees, float f2, int eyeValue, boolean flag, boolean equals2, boolean flag2) {
      if (mathClampMinecraftClient.player != null) {
         if (!rotationOverrideActive || eyeValue >= overridePriority) {
            rotationOverrideActive = true;
            overridePriority = eyeValue;
            wrapDegrees = MathHelper.wrapDegrees(degrees);
            clamp = MathHelper.clamp(f2, -90.0F, 90.0F);
            wrapDegreesActive = flag;
            equals = equals2;
            wrapAimActive = flag2;
         }
      }
   }

   public static boolean isFlag() {
      return mathClampActive;
   }

   public static boolean isClampSettled() {
      return clampSettled;
   }

   public static boolean isClampApplied() {
      return clampApplied;
   }

   public static void handleInstanceEye(Object obj, Vec3d vec, int eyeValue, boolean eyeFlag, boolean eyeFlag2, boolean eyeFlag3) {
      if (mathClampMinecraftClient.player != null) {
         float[] wrapDegrees = getWrapDegrees(mathClampMinecraftClient.player.getEyePos(), vec);
         handleWrapDegrees(obj, wrapDegrees[0], wrapDegrees[1], eyeValue, eyeFlag, eyeFlag2, eyeFlag3);
      }
   }

   public static void updateFlagClamp() {
      if (mathClampMinecraftClient.player != null && mathClampMinecraftClient.getNetworkHandler() != null) {
         float yaw2 = mathClampMinecraftClient.player.getYaw();
         float pitch = mathClampMinecraftClient.player.getPitch();
         float sensitivityMouse = ClampMath.getSensitivityMouse();
         float clampValue = 25.0F;
         if (sensitivityMouse > 0.0F && Float.isFinite(sensitivityMouse)) {
            clampValue = (float)(Math.floor(clampValue / sensitivityMouse) * sensitivityMouse);
         }

         boolean clampFlag = !rotationOverrideActive && clampActive && (Math.abs(MathHelper.wrapDegrees(yaw - yaw2)) > clampValue || Math.abs(pitchClamp - pitch) > clampValue);
         if (!rotationOverrideActive && !clampFlag) {
            clampApplied = false;
            clampActive = false;
            clampSettled = false;
            mathClampActive = false;
            yaw = mathClampMinecraftClient.player.getYaw();
            pitchClamp = mathClampMinecraftClient.player.getPitch();
         } else {
            boolean clampFlag2 = (!rotationOverrideActive || wrapDegreesActive) && clampActive;
            float clampValue2 = clampFlag2 ? yaw2 + MathHelper.wrapDegrees(yaw - yaw2) : yaw2;
            float clampValue3 = clampFlag2 ? pitchClamp : pitch;
            float planValuesItem = MathHelper.wrapDegrees((rotationOverrideActive ? wrapDegrees : yaw2) - clampValue2);
            if (Math.abs(planValuesItem) > clampValue) {
               planValuesItem = Math.copySign(clampValue, planValuesItem);
            }

            float copySign4 = (rotationOverrideActive ? MathHelper.clamp(clamp, -90.0F, 90.0F) : pitch) - clampValue3;
            if (Math.abs(copySign4) > clampValue) {
               copySign4 = Math.copySign(clampValue, copySign4);
            }

            yaw = clampValue2 + planValuesItem;
            pitchClamp = MathHelper.clamp(clampValue3 + copySign4, -90.0F, 90.0F);
            clampApplied = true;
            if (!rotationOverrideActive) {
               mathClampActive = false;
            } else {
               clampActive = wrapDegreesActive;
               clampSettled = wrapDegreesActive && equals;
               mathClampActive = wrapAimActive;
               if (!wrapDegreesActive) {
                  mathClampMinecraftClient.player.setYaw(yaw);
                  mathClampMinecraftClient.player.setPitch(pitchClamp);
               }
            }
         }

         rotationOverrideActive = false;
         overridePriority = Integer.MIN_VALUE;
      } else {
         updateFlag();
      }
   }

   public static void handle(Object obj, float mathClampValue, float f2, int eyeValue, boolean mathClampFlag, boolean mathClampFlag2) {
      handleWrapDegrees(obj, mathClampValue, f2, eyeValue, mathClampFlag, mathClampFlag2, false);
   }

   public static boolean isClampActive() {
      return clampActive;
   }

   public static float[] getWrapDegrees(Vec3d vec, Vec3d vec2) {
      double deltaX = vec2.x - vec.x;
      double deltaY = vec2.y - vec.y;
      double deltaZ = vec2.z - vec.z;
      double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
      float degrees = (float)Math.toDegrees(Math.atan2(-deltaX, deltaZ));
      float degrees2 = (float)Math.toDegrees(Math.atan2(-deltaY, distance));
      return new float[]{MathHelper.wrapDegrees(degrees), MathHelper.clamp(degrees2, -90.0F, 90.0F)};
   }

   public static float getYaw() {
      return yaw;
   }

   public static float getClamp() {
      return pitchClamp;
   }

   public static void handle(Object obj, float mathClampValue, float f2, int eyeValue, boolean mathClampFlag) {
      handleWrapDegrees(obj, mathClampValue, f2, eyeValue, mathClampFlag, false, false);
   }

   private MathClampUtils() {
   }

   public static void handleInstanceEye(Object obj, Vec3d vec, int eyeValue, boolean eyeFlag) {
      if (mathClampMinecraftClient.player != null) {
         float[] wrapDegrees = getWrapDegrees(mathClampMinecraftClient.player.getEyePos(), vec);
         handle(obj, wrapDegrees[0], wrapDegrees[1], eyeValue, eyeFlag);
      }
   }
}
