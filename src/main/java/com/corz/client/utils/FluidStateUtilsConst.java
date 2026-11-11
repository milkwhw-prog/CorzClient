package com.corz.client.utils;

public final class FluidStateUtilsConst {

   public static float getAmount(float fluidConstLimit, float fluidConstLimit2, float fluidConstLimit3, float fluidConstLimit4) {
      if (!(fluidConstLimit3 >= 1.0F) && !(fluidConstLimit2 >= 1.0F)) {
         float fluidConstTotal = 0.0F;
         float fluidConstTotal2 = 0.0F;
         if (fluidConstLimit3 > 0.0F || fluidConstLimit2 > 0.0F) {
            if (fluidConstLimit4 >= 1.0F) {
               return 1.0F;
            }

            if (fluidConstLimit4 >= 0.8F) {
               fluidConstTotal += fluidConstLimit4 * 10.0F;
               fluidConstTotal2 += 10.0F;
            } else if (fluidConstLimit4 >= 0.0F) {
               fluidConstTotal += fluidConstLimit4;
               fluidConstTotal2++;
            }
         }

         if (fluidConstLimit >= 0.8F) {
            fluidConstTotal += fluidConstLimit * 10.0F;
            fluidConstTotal2 += 10.0F;
         } else if (fluidConstLimit >= 0.0F) {
            fluidConstTotal += fluidConstLimit;
            fluidConstTotal2++;
         }

         if (fluidConstLimit3 >= 0.8F) {
            fluidConstTotal += fluidConstLimit3 * 10.0F;
            fluidConstTotal2 += 10.0F;
         } else if (fluidConstLimit3 >= 0.0F) {
            fluidConstTotal += fluidConstLimit3;
            fluidConstTotal2++;
         }

         if (fluidConstLimit2 >= 0.8F) {
            fluidConstTotal += fluidConstLimit2 * 10.0F;
            fluidConstTotal2 += 10.0F;
         } else if (fluidConstLimit2 >= 0.0F) {
            fluidConstTotal += fluidConstLimit2;
            fluidConstTotal2++;
         }

         return fluidConstTotal / fluidConstTotal2;
      } else {
         return 1.0F;
      }
   }

   public static float getAmount(boolean fluidConstFlag, boolean unreflectReference, float fluidConstValue, boolean fluidConstFlag3) {
      if (fluidConstFlag) {
         return unreflectReference ? 1.0F : fluidConstValue;
      } else {
         return fluidConstFlag3 ? -1.0F : 0.0F;
      }
   }

   private FluidStateUtilsConst() {
   }

   public static boolean canFlag(float[] fluidConstValues) {
      return fluidConstValues[0] == fluidConstValues[1] && fluidConstValues[1] == fluidConstValues[2] && fluidConstValues[2] == fluidConstValues[3];
   }

   public static void handle(float[] fluidConstValues, int fluidConstValue, int index2, float[] fluidConstValues2) {
      float fluidConstLimit = fluidConstValues[index2];
      if (fluidConstLimit >= 1.0F) {
         fluidConstValues2[0] = fluidConstValues2[1] = fluidConstValues2[2] = fluidConstValues2[3] = 1.0F;
      } else {
         float fluidConstValue2 = fluidConstValues[index2 - fluidConstValue];
         float fluidConstValue3 = fluidConstValues[index2 + fluidConstValue];
         float fluidConstValue4 = fluidConstValues[index2 + 1];
         float fluidConstValue5 = fluidConstValues[index2 - 1];
         fluidConstValues2[0] = getAmount(fluidConstLimit, fluidConstValue2, fluidConstValue5, fluidConstValues[index2 - fluidConstValue - 1]);
         fluidConstValues2[1] = getAmount(fluidConstLimit, fluidConstValue3, fluidConstValue5, fluidConstValues[index2 + fluidConstValue - 1]);
         fluidConstValues2[2] = getAmount(fluidConstLimit, fluidConstValue3, fluidConstValue4, fluidConstValues[index2 + fluidConstValue + 1]);
         fluidConstValues2[3] = getAmount(fluidConstLimit, fluidConstValue2, fluidConstValue4, fluidConstValues[index2 - fluidConstValue + 1]);
      }
   }

   public static float getMin(float[] fluidConstValues) {
      return Math.min(Math.min(fluidConstValues[0], fluidConstValues[1]), Math.min(fluidConstValues[2], fluidConstValues[3]));
   }
}
