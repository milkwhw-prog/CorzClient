package com.corz.client.world;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class BoxLengthUtils {
   public static final int UNIT_STEP = 1;

   public static boolean canFlag(float boxLengthLimit, float boxLengthValue, float boxLengthValue2, boolean boxLengthFlag, float absorptionHealthValue) {
      if (boxLengthLimit <= 0.0F) {
         return true;
      } else {
         return boxLengthFlag && boxLengthLimit >= boxLengthValue + boxLengthValue2 - 1.0F ? false : boxLengthLimit <= absorptionHealthValue;
      }
   }

   public static double getAmount(double boxLengthValue) {
      return getAmountForDouble(getAmount3(boxLengthValue, 1.0, 6.0));
   }

   public static int getMax(int boxLengthBound) {
      int roundOffset = Math.round(Math.max(0, boxLengthBound) / 50.0F) + 2;
      return Math.max(2, Math.min(10, roundOffset));
   }

   public static boolean canFlag(float boxLengthValue, float boxLengthValue2, float planValuesItem, float boxLengthValue4) {
      double boxLengthLimit = Math.max(0.001, 4.0 * Math.ulp(Math.max(Math.abs(boxLengthValue), Math.abs(planValuesItem))));
      return Math.abs(getAmount3((double)boxLengthValue - planValuesItem)) <= boxLengthLimit && Math.abs((double)boxLengthValue2 - boxLengthValue4) <= 0.001;
   }

   public static boolean canLengthContract(Box box, Vec3d vec, Vec3d vec2, double lengthContractLimit) {
      if (box != null && vec != null && vec2 != null) {
         double lengthContractBound = lengthContractLimit > 0.0 ? lengthContractLimit : 0.0;
         Box contract = box.contract(
            Math.min(lengthContractBound, box.getLengthX() * 0.25), Math.min(lengthContractBound, box.getLengthY() * 0.25), Math.min(lengthContractBound, box.getLengthZ() * 0.25)
         );
         return contract.contains(vec) || contract.raycast(vec, vec2).isPresent();
      } else {
         return false;
      }
   }

   private BoxLengthUtils() {
   }

   public static double getAmount(double boxLengthBound, double hypot, double boxLengthBound3, double boxLengthBound4, double hashRegistryBound, double boxLengthBound6, double boxLengthBound7, double boxLengthBound8, double goalBound) {
      double maxMaxX = Math.max(boxLengthBound4, Math.min(boxLengthBound, boxLengthBound7));
      double maxMinX = Math.max(hashRegistryBound, Math.min(hypot, boxLengthBound8));
      double boxLengthValue = Math.max(boxLengthBound6, Math.min(boxLengthBound3, goalBound));
      double boxLengthFactor = boxLengthBound - maxMaxX;
      double boxLengthFactor2 = hypot - maxMinX;
      double boxLengthFactor3 = boxLengthBound3 - boxLengthValue;
      return Math.sqrt(boxLengthFactor * boxLengthFactor + boxLengthFactor2 * boxLengthFactor2 + boxLengthFactor3 * boxLengthFactor3);
   }

   public static double getAmountForDouble(double boxLengthValue) {
      return getAmount2(boxLengthValue / 85.0, 0.0, 1.0);
   }

   public static double getAmount(double boxLengthBound, double apiPutBound) {
      return boxLengthBound <= 0.0 ? 0.0 : Math.min(boxLengthBound, 0.313 * boxLengthBound + 1.56 * Math.max(0.0, apiPutBound));
   }

   public static double getAmount(double boxLengthFactor, double boxLengthValue, double boxLengthFactor2) {
      return Math.toDegrees(Math.atan2(-boxLengthValue, Math.sqrt(boxLengthFactor * boxLengthFactor + boxLengthFactor2 * boxLengthFactor2)));
   }

   public static float[] getAmounts(float boxLengthValue, float clamp, float f, float boxLengthBound, double apiPutBound, double boxLengthValue6, float boxLengthLimit) {
      double boxLengthFactor = getAmount3((double)f - boxLengthValue);
      double deltaZ = (double)getAmount(boxLengthBound, -90.0F, 90.0F) - clamp;
      double hypot = Math.hypot(boxLengthFactor, deltaZ);
      if (hypot < 1.0E-6) {
         return new float[]{boxLengthValue, clamp};
      } else {
         double boxLengthValue8 = getAmount(hypot, apiPutBound);
         if (boxLengthValue8 < hypot) {
            boxLengthValue8 *= getAmount2(boxLengthValue6, 0.5, 1.0);
         }

         double boxLengthFactor3 = boxLengthValue8 / hypot;
         float boxLengthValue9 = getAmount((float)(boxLengthFactor * boxLengthFactor3), boxLengthLimit);
         float boxLengthValue10 = getAmount((float)(deltaZ * boxLengthFactor3), boxLengthLimit);
         return new float[]{boxLengthValue + boxLengthValue9, getAmount(clamp + boxLengthValue10, -90.0F, 90.0F)};
      }
   }

   public static double getAmountDouble(double boxLengthValue, double x) {
      return Math.toDegrees(Math.atan2(-boxLengthValue, x));
   }

   public static double getAmount(float boxLengthValue, float clamp, float f, float currentPitch) {
      return Math.hypot(getAmount3((double)f - boxLengthValue), (double)currentPitch - clamp);
   }

   public static float getAmount(float boxLengthBound, int boxLengthValue) {
      return switch (boxLengthValue) {
         case 0 -> 0.0F;
         case 1 -> Math.min(boxLengthBound / 2.0F + 1.0F, boxLengthBound);
         default -> boxLengthBound;
         case 3 -> boxLengthBound * 3.0F / 2.0F;
      };
   }

   public static double getAmount3(double boxLengthLimit, double boxLengthValue) {
      double boxLengthLimit2 = 3.5 * boxLengthValue * 2.0;
      if (boxLengthLimit > 1.0 && boxLengthLimit2 > 0.0 && !Double.isInfinite(boxLengthLimit)) {
         double boxLengthBound = (-1.0 + Math.sqrt(1.0 + 4.0 * (boxLengthLimit - 1.0) / boxLengthLimit2)) / 2.0;
         return getAmount2(boxLengthBound, 0.0, 1.0);
      } else {
         return 0.0;
      }
   }

   static double getAmount2(double boxLengthBound, double boxLengthLimit, double goalBound) {
      return boxLengthBound < boxLengthLimit ? boxLengthLimit : Math.min(boxLengthBound, goalBound);
   }

   public static double getAmount3(double boxLengthValue, double boxLengthBound, double distanceValue) {
      double distanceScaled = distanceValue * 2.0;
      if (!(distanceScaled <= 0.0) && !(boxLengthValue < 0.0)) {
         double boxLengthLimit = boxLengthValue / distanceScaled;
         if (boxLengthLimit > 1.0) {
            return 0.0;
         } else {
            double boxLengthFactor = (1.0 - boxLengthLimit) * getAmount2(boxLengthBound, 0.0, 1.0);
            return (boxLengthFactor * boxLengthFactor + boxLengthFactor) / 2.0 * 7.0 * distanceScaled + 1.0;
         }
      } else {
         return 0.0;
      }
   }

   static float getAmount(float boxLengthBound, float boxLengthLimit, float hashHuntBound) {
      return boxLengthBound < boxLengthLimit ? boxLengthLimit : Math.min(boxLengthBound, hashHuntBound);
   }

   public static float getAmount(float boxLengthValue, float boxLengthLimit) {
      return !(boxLengthLimit > 0.0F) ? boxLengthValue : Math.round(boxLengthValue / boxLengthLimit) * boxLengthLimit;
   }

   public static float getAmountForFloat(float boxLengthFactor, float boxLengthValue, float boxLengthValue2) {
      float boxLengthDivisor = 2.0F + boxLengthValue2 / 4.0F;
      float boxLengthValue3 = getAmount(boxLengthValue - boxLengthFactor / boxLengthDivisor, boxLengthValue * 0.2F, 20.0F);
      return boxLengthFactor * (1.0F - boxLengthValue3 / 25.0F);
   }

   public static boolean canFlag(double boxLengthFactor, double x, float boxLengthValue, double boxLengthLimit) {
      return boxLengthFactor * boxLengthFactor + x * x < 1.0 ? true : Math.abs(getAmount3(getAmountDouble(boxLengthFactor, x) - boxLengthValue)) <= boxLengthLimit;
   }

   public static float getAmount(float boxLengthValue, int boxLengthValue2, float boxLengthValue3, float boxLengthValue4, int boxLengthValue5, float boxLengthValue6) {
      float boxLengthFactor = getAmount(boxLengthValue, boxLengthValue2);
      if (boxLengthFactor <= 0.0F) {
         return 0.0F;
      } else {
         float amountForFloat = getAmountForFloat(boxLengthFactor, boxLengthValue3, boxLengthValue4);
         float amountForFloatInt = getAmountForFloatInt(amountForFloat, boxLengthValue5);
         return amountForFloatInt <= 0.0F ? 0.0F : Math.max(getAmountFloat(amountForFloatInt, boxLengthValue6), 0.0F);
      }
   }

   public static double getAmount3(double boxLengthValue) {
      double boxLengthTotal = boxLengthValue % 360.0;
      if (boxLengthTotal >= 180.0) {
         boxLengthTotal -= 360.0;
      }

      if (boxLengthTotal < -180.0) {
         boxLengthTotal += 360.0;
      }

      return boxLengthTotal;
   }

   public static float getAmountForFloatInt(float boxLengthFactor, int boxLengthValue) {
      if (boxLengthValue < 0) {
         return boxLengthFactor;
      } else {
         int boxLengthFactor2 = 25 - (boxLengthValue + 1) * 5;
         return Math.max(boxLengthFactor * boxLengthFactor2 / 25.0F, 0.0F);
      }
   }

   public static float getAmountFloat(float boxLengthFactor, float boxLengthValue) {
      float boxLengthValue2 = getAmount(boxLengthValue, 0.0F, 20.0F);
      return boxLengthFactor * (1.0F - boxLengthValue2 / 25.0F);
   }
}
