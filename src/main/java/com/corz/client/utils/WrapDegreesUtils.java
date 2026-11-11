package com.corz.client.utils;

import net.minecraft.util.math.MathHelper;

public final class WrapDegreesUtils {
   public static float stepAngleProportional(float wrapDegrees, float wrapDegreesValue, float wrapDegreesFactor, float wrapDegreesValue2, float wrapDegreesLimit) {
      float planValuesItem = MathHelper.wrapDegrees(wrapDegreesValue - wrapDegrees);
      float abs2 = Math.abs(planValuesItem);
      if (abs2 < 1.0E-4F) {
         return wrapDegrees;
      } else {
         float wrapDegreesLimit2 = abs2 * wrapDegreesFactor;
         if (wrapDegreesLimit2 > wrapDegreesValue2) {
            wrapDegreesLimit2 = wrapDegreesValue2;
         }

         if (wrapDegreesLimit2 < wrapDegreesLimit) {
            wrapDegreesLimit2 = wrapDegreesLimit;
         }

         if (wrapDegreesLimit2 > abs2) {
            wrapDegreesLimit2 = abs2;
         }

         return wrapDegrees + Math.copySign(wrapDegreesLimit2, planValuesItem);
      }
   }

   public static float stepAngleClamped(float wrapDegreesValue, float wrapDegreesValue2, float wrapDegreesValue3) {
      float wrapDegrees = MathHelper.wrapDegrees(wrapDegreesValue2 - wrapDegreesValue);
      if (wrapDegrees > wrapDegreesValue3) {
         wrapDegrees = wrapDegreesValue3;
      } else if (wrapDegrees < -wrapDegreesValue3) {
         wrapDegrees = -wrapDegreesValue3;
      }

      return wrapDegreesValue + wrapDegrees;
   }

   private WrapDegreesUtils() {
   }

   public static float getWobbleOffset(long wrapDegreesValue, float wrapDegreesFactor, int wrapDegreesValue2) {
      double angle = wrapDegreesValue * 0.5 + wrapDegreesValue2 * 7.13;
      double wrapDegreesFactor2 = 0.6 * Math.sin(angle) + 0.4 * Math.sin(angle * 2.3 + 1.1);
      return (float)(wrapDegreesFactor * wrapDegreesFactor2);
   }
}
