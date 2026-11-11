package com.corz.client.world;

public final class VecPosUtilsAmount {

   public static boolean canFlag(boolean vecPosFlag, double vecPosLimit) {
      return vecPosFlag && vecPosLimit > 0.9;
   }

   private static double getMin(double vecPosLimit) {
      return !(vecPosLimit > 0.0) ? 0.0 : Math.min(1.0, vecPosLimit);
   }

   public static void handle(double[] vecPosValues, double vecPosFactor, double vecPosValue, double vecPosFactor2, double vecPosFactor3, boolean vecPosFlag) {
      double vecPosFactor4 = vecPosFactor * (1.0 - vecPosValue);
      if (vecPosFactor4 > 0.0) {
         double vecPosLimit = vecPosFactor2 * vecPosFactor2 + vecPosFactor3 * vecPosFactor3;
         if (vecPosLimit >= 1.0E-5) {
            double sqrt = Math.sqrt(vecPosLimit);
            double vecPosValue2 = vecPosFactor2 / sqrt * vecPosFactor4;
            double vecPosValue3 = vecPosFactor3 / sqrt * vecPosFactor4;
            vecPosValues[0] = vecPosValues[0] / 2.0 - vecPosValue2;
            vecPosValues[1] = vecPosFlag ? Math.min(0.4, vecPosValues[1] / 2.0 + vecPosFactor4) : vecPosValues[1];
            vecPosValues[2] = vecPosValues[2] / 2.0 - vecPosValue3;
         }
      }
   }

   public static double getAmount(double vecPosValue, int enchantmentsDefault, boolean vecPosFlag) {
      return (vecPosValue + enchantmentsDefault) / 2.0 + (vecPosFlag ? 0.5 : 0.0);
   }

   public static double getAmount(int vecPosValue) {
      return getMin(0.1 * vecPosValue);
   }

   public static int getValue(double vecPosLimit) {
      return !(vecPosLimit > 0.0) ? 1000000 : (int)Math.floor(18.0 / vecPosLimit - 0.5) + 1;
   }

   public static double[] getFreshBlocked(
      double[] freshBlockedValues,
      boolean freshBlockedFlag,
      double freshBlockedValue,
      double freshBlockedValue2,
      double freshBlockedValue3,
      float degrees,
      double freshBlockedLimit,
      VecPosUtilsAmount.HitKind hitKind
   ) {
      double[] doubles = new double[]{freshBlockedValues[0], freshBlockedValues[1], freshBlockedValues[2]};
      if (hitKind != null && hitKind != VecPosUtilsAmount.HitKind.BLOCKED) {
         if (hitKind == VecPosUtilsAmount.HitKind.FRESH) {
            handle(doubles, 0.4F, freshBlockedValue, freshBlockedValue2, freshBlockedValue3, freshBlockedFlag);
         }

         if (freshBlockedLimit > 0.0) {
            double angle = Math.toRadians(degrees);
            handle(doubles, freshBlockedLimit, freshBlockedValue, Math.sin(angle), -Math.cos(angle), freshBlockedFlag);
         }

         return doubles;
      } else {
         return doubles;
      }
   }

   public static double[] getAmounts(
      double vecPosValue, double vecPosValue2, double vecPosValue3, double vecPosValue4, double vecPosValue5, double vecPosValue6, double vecPosValue7, double vecPosLimit, double vecPosFactor, double rExpl
   ) {
      if (!(vecPosLimit > 0.0)) {
         return new double[3];
      } else {
         double vecPosFactor2 = vecPosValue - vecPosValue5;
         double vecPosFactor3 = vecPosValue2 - vecPosValue6;
         double vecPosFactor4 = vecPosValue3 - vecPosValue7;
         double vecPosValue9 = Math.sqrt(vecPosFactor2 * vecPosFactor2 + vecPosFactor3 * vecPosFactor3 + vecPosFactor4 * vecPosFactor4) / (2.0 * vecPosLimit);
         if (!(vecPosValue9 <= 1.0)) {
            return new double[3];
         } else {
            double vecPosFactor5 = vecPosValue2 + vecPosValue4 - vecPosValue6;
            double distance = Math.sqrt(vecPosFactor2 * vecPosFactor2 + vecPosFactor5 * vecPosFactor5 + vecPosFactor4 * vecPosFactor4);
            if (!(distance >= 1.0E-5)) {
               return new double[3];
            } else {
               double vecPosFactor6 = (1.0 - vecPosValue9) * vecPosFactor * (1.0 - getMin(rExpl));
               return new double[]{vecPosFactor2 / distance * vecPosFactor6, vecPosFactor5 / distance * vecPosFactor6, vecPosFactor4 / distance * vecPosFactor6};
            }
         }
      }
   }

   public static double getAmount(boolean vecPosFlag, boolean vecPosFlag2, boolean vecPosFlag3) {
      if (vecPosFlag) {
         return 1.6;
      } else if (vecPosFlag2) {
         return 1.0;
      } else {
         return vecPosFlag3 ? 0.6 : 4.0;
      }
   }

   private VecPosUtilsAmount() {
   }

   public static double getAmount(int[] vecPosValues) {
      if (vecPosValues == null) {
         return 0.0;
      } else {
         double vecPosTotal = 0.0;

         for (int vecPosLimit : vecPosValues) {
            if (vecPosLimit > 0) {
               vecPosTotal += 0.15 * vecPosLimit;
            }
         }

         return getMin(vecPosTotal);
      }
   }

   public static enum HitKind {
      FRESH,
      EXCESS,
      BLOCKED;
   }
}
