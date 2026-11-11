package com.corz.client.utils;

import net.minecraft.util.math.MathHelper;
import com.corz.client.utils.RotationAccelerationLimiter;

public final class GlidePhysics {

   public static float rotateToward(float wrapDegrees, float wrapDegreesValue, float humanAimDivisor) {
      float planValuesItem = MathHelper.wrapDegrees(wrapDegreesValue - wrapDegrees);
      float wrapDegreesBound = Math.abs(planValuesItem);
      if (wrapDegreesBound < 1.0E-4F) {
         return wrapDegrees;
      } else {
         float min3 = Math.min(wrapDegreesBound, RotationAccelerationLimiter.HumanAim.getMaxStepForTurnSpeed(wrapDegreesBound, humanAimDivisor));
         return wrapDegrees + Math.copySign(min3, planValuesItem);
      }
   }

   private GlidePhysics() {
   }

   public static int getRocketBoostTicks(int glidePhysicsBound) {
      return 10 * (Math.max(1, glidePhysicsBound) + 1) + 5;
   }

   public static void applyVelocity(GlidePhysics.Body body) {
      body.x = body.x + body.velocityX;
      body.y = body.y + body.velocityY;
      body.positionZ = body.positionZ + body.velocityZ;
   }

   public static float stepPitchToward(float clampValue, float value) {
      return MathHelper.clamp(rotateToward(clampValue, MathHelper.clamp(value, -90.0F, 90.0F), 260.0F), -90.0F, 90.0F);
   }

   public static double getLookY(float sinFactor) {
      return -MathHelper.sin(sinFactor * (float) (Math.PI / 180.0));
   }

   public static void applyGlideTick(GlidePhysics.Body body, double sinPitchFactor) {
      float yaw = body.yaw;
      float pitch = body.pitch;
      double cosSin = getLookX(yaw, pitch);
      double cos = getLookZ(yaw, pitch);
      float angle = pitch * (float) (Math.PI / 180.0);
      double distance = Math.sqrt(cosSin * cosSin + cos * cos);
      double sqrt2 = Math.sqrt(body.velocityX * body.velocityX + body.velocityZ * body.velocityZ);
      double cos2 = Math.cos(angle);
      double sinPitchFactor2 = cos2 * cos2;
      double x2 = body.velocityX;
      double y2 = body.velocityY;
      double z = body.velocityZ;
      double sinPitchTotal = y2 + sinPitchFactor * (-1.0 + sinPitchFactor2 * 0.75);
      if (sinPitchTotal < 0.0 && distance > 0.0) {
         double sinPitchFactor3 = sinPitchTotal * -0.1 * sinPitchFactor2;
         x2 += cosSin * sinPitchFactor3 / distance;
         sinPitchTotal += sinPitchFactor3;
         z += cos * sinPitchFactor3 / distance;
      }

      if (angle < 0.0F && distance > 0.0) {
         double sinPitchFactor4 = sqrt2 * -MathHelper.sin(angle) * 0.04;
         x2 += -cosSin * sinPitchFactor4 / distance;
         sinPitchTotal += sinPitchFactor4 * 3.2;
         z += -cos * sinPitchFactor4 / distance;
      }

      if (distance > 0.0) {
         x2 += (cosSin / distance * sqrt2 - x2) * 0.1;
         z += (cos / distance * sqrt2 - z) * 0.1;
      }

      body.velocityX = x2 * 0.99F;
      body.velocityY = sinPitchTotal * 0.98F;
      body.velocityZ = z * 0.99F;
   }

   public static double getLookZ(float cosValue, float cosFactor) {
      return MathHelper.cos(-cosValue * (float) (Math.PI / 180.0)) * MathHelper.cos(cosFactor * (float) (Math.PI / 180.0));
   }

   public static void applyRocketBoost(GlidePhysics.Body body) {
      double cosSin = getLookX(body.yaw, body.pitch);
      double sin = getLookY(body.pitch);
      double cos = getLookZ(body.yaw, body.pitch);
      body.velocityX = body.velocityX + (cosSin * 0.1 + (cosSin * 1.5 - body.velocityX) * 0.5);
      body.velocityY = body.velocityY + (sin * 0.1 + (sin * 1.5 - body.velocityY) * 0.5);
      body.velocityZ = body.velocityZ + (cos * 0.1 + (cos * 1.5 - body.velocityZ) * 0.5);
   }

   public static double getLookX(float cosSinValue, float cosSinFactor) {
      return MathHelper.sin(-cosSinValue * (float) (Math.PI / 180.0)) * MathHelper.cos(cosSinFactor * (float) (Math.PI / 180.0));
   }

   public static float getYawToward(double glidePhysicsValue, double glidePhysicsValue2, double glidePhysicsValue3, double glidePhysicsValue4) {
      return (float)Math.toDegrees(Math.atan2(-(glidePhysicsValue3 - glidePhysicsValue), glidePhysicsValue4 - glidePhysicsValue2));
   }

   public static final class Body {
      public double velocityZ;
      public double y;
      public double x;
      public double velocityY;
      public float pitch;
      public double velocityX;
      public double positionZ;
      public float yaw;

      public GlidePhysics.Body copyFrom(GlidePhysics.Body body) {
         return this.setState(body.x, body.y, body.positionZ, body.velocityX, body.velocityY, body.velocityZ, body.yaw, body.pitch);
      }

      public double getHorizontalSpeed() {
         return Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
      }

      public GlidePhysics.Body setState(double pitchYawValue, double pitchYawValue2, double pitchYawValue3, double pitchYawValue4, double pitchYawValue5, double pitchYawValue6, float yaw2, float pitch2) {
         this.x = pitchYawValue;
         this.y = pitchYawValue2;
         this.positionZ = pitchYawValue3;
         this.velocityX = pitchYawValue4;
         this.velocityY = pitchYawValue5;
         this.velocityZ = pitchYawValue6;
         this.yaw = yaw2;
         this.pitch = pitch2;
         return this;
      }
   }
}
