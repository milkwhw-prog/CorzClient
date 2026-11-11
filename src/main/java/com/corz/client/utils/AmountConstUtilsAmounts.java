package com.corz.client.utils;
import com.corz.client.utils.IliIilType;

public final class AmountConstUtilsAmounts {

   public static int getY(double constValue) {
      double constValue2 = constValue - Math.floor(constValue);
      return Math.abs(constValue2 - 0.5) < 1.0E-4 ? (int)Math.floor(constValue) : (int)Math.floor(constValue - 1.0 + 1.0E-4);
   }

   private static boolean canFlag(int constValue, int constValue2, IliIilType iliIilType) {
      return constValue != 0 && constValue == iliIilType.directionX || constValue2 != 0 && constValue2 == iliIilType.degreesValue;
   }

   public static double[] getAmounts(IliIilType iliIilType, double constValue, double constValue2, double constValue3, double constValue4) {
      double constValue5 = iliIilType.directionX != 0 ? constValue - iliIilType.directionX * 0.15 : constValue3;
      double constValue6 = iliIilType.degreesValue != 0 ? constValue2 - iliIilType.degreesValue * 0.15 : constValue4;
      return new double[]{constValue5, constValue6};
   }

   public static boolean canFlag(int constValue, int constLimit, int constValue2, IliIilType iliIilType, IliIilType iliIilType2, boolean constFlag) {
      if (constLimit < 0) {
         return false;
      } else {
         return constLimit > 0 ? constFlag : canFlag(constValue, constValue2, iliIilType) || iliIilType2 != null && canFlag(constValue, constValue2, iliIilType2);
      }
   }

   public static boolean canFlag(IliIilType iliIilType, double constValue, boolean constFlag) {
      if (!iliIilType.isFlag()) {
         return !constFlag;
      } else {
         double degrees = getDegrees(iliIilType);
         return getAmount(constValue, degrees + 45.0) <= getAmount(constValue, degrees - 45.0);
      }
   }

   public static float getAmount(boolean constFlag, int constLimit) {
      if (constFlag) {
         return constLimit >= 6 ? 80.0F : 78.0F;
      } else {
         return constLimit > 10 ? 83.0F : 81.0F;
      }
   }

   private static double getAmount(double constValue, double constValue2, double constValue3, double constValue4) {
      double constFactor = constValue - constValue3;
      double constFactor2 = constValue2 - constValue4;
      return constFactor * constFactor + constFactor2 * constFactor2;
   }

   public static double getDegrees(IliIilType iliIilType) {
      return Math.toDegrees(Math.atan2(iliIilType.directionX, -iliIilType.degreesValue));
   }

   public static double getAmount(double constValue, double constValue2) {
      return Math.abs(getAmount(constValue - constValue2));
   }

   public static boolean canFlag(IliIilType iliIilType, double constValue, double constValue2) {
      return iliIilType.directionX * (constValue - 0.5) + iliIilType.degreesValue * (constValue2 - 0.5) > 0.0;
   }

   public static int getMin(double constValue) {
      byte constFactor = 3;
      int constBound = 2 + (int)Math.floor(getAmountDouble(constValue, 0.0, 1.0) * constFactor);
      return Math.min(4, constBound);
   }

   public static double getAmount(double constValue, double constValue2, double constDivisor) {
      return getAmountDouble(2.0 + getAmount(constValue, constValue2) / constDivisor, 2.0, 12.0);
   }

   public static boolean canFlag(double constValue, double constValue2, double constValue3, double constValue4, double constLimit) {
      return Math.abs(constValue3 - constValue) <= constLimit && Math.abs(constValue4 - constValue2) <= constLimit;
   }

   public static boolean canFlag(int constValue, int constValue2, int constValue3, int constValue4, int constValue5, int constValue6, IliIilType iliIilType, int constValue7, int scaffoldLimit) {
      if (constValue2 != constValue5) {
         return true;
      } else if (iliIilType.directionX != 0 && iliIilType.degreesValue == 0 && constValue3 != constValue6) {
         return true;
      } else if (iliIilType.degreesValue != 0 && iliIilType.directionX == 0 && constValue != constValue4) {
         return true;
      } else {
         double constFactor = constValue - (double)constValue7 * iliIilType.directionX - constValue4;
         double constFactor2 = constValue3 - (double)constValue7 * iliIilType.degreesValue - constValue6;
         return Math.sqrt(constFactor * constFactor + constFactor2 * constFactor2) > scaffoldLimit + 2.0;
      }
   }

   public static double[] getAmounts(IliIilType iliIilType, boolean constFlag) {
      return switch (iliIilType) {
         case SE -> constFlag ? new double[]{0.65, 0.35} : new double[]{0.35, 0.65};
         case SW -> constFlag ? new double[]{0.65, 0.65} : new double[]{0.35, 0.35};
         case NW -> constFlag ? new double[]{0.35, 0.65} : new double[]{0.65, 0.35};
         case NE -> constFlag ? new double[]{0.35, 0.35} : new double[]{0.65, 0.65};
         case E -> constFlag ? new double[]{0.8, 0.8} : new double[]{0.8, 0.2};
         case W -> constFlag ? new double[]{0.2, 0.2} : new double[]{0.2, 0.8};
         case S -> constFlag ? new double[]{0.2, 0.8} : new double[]{0.8, 0.8};
         case N -> constFlag ? new double[]{0.8, 0.2} : new double[]{0.2, 0.2};
      };
   }

   private AmountConstUtilsAmounts() {
   }

   public static double getAmount(double constValue) {
      double constTotal = constValue % 360.0;
      if (constTotal >= 180.0) {
         constTotal -= 360.0;
      } else if (constTotal < -180.0) {
         constTotal += 360.0;
      }

      return constTotal;
   }

   public static double getAmount(IliIilType iliIilType, double constFactor, double constFactor2) {
      double constDivisor = iliIilType.isFlag() ? 1.0 : Math.sqrt(2.0);
      return getAmountDouble(20.0 * (iliIilType.directionX * constFactor2 - iliIilType.degreesValue * constFactor) / constDivisor, -25.0, 25.0);
   }

   public static double getAmountForDouble(double constValue, double spotValue) {
      return 0.15 * (0.25 * getAmountDouble(constValue, 2.0, 12.0) + 0.05 * Math.abs(spotValue));
   }

   public static double getAmount(IliIilType iliIilType, boolean constFlag) {
      double degrees = getDegrees(iliIilType);
      if (!iliIilType.isFlag()) {
         return degrees;
      } else {
         return constFlag ? degrees + 45.0 : degrees - 45.0;
      }
   }

   public static boolean[] getFlags(double constValue, double constValue2, double constValue3, double constValue4, double constValue5, double constValue6, double constValue7, double constFactor) {
      boolean[] booleans = new boolean[4];
      double constValue8 = constValue2 + constValue4;
      double constValue9 = constValue3 + constValue5;

      for (int i = 0; i < 4; i++) {
         double angle = Math.toRadians(constValue + 90.0 * i);
         double constValue10 = constValue8 - Math.sin(angle) * constFactor;
         double constValue11 = constValue9 + Math.cos(angle) * constFactor;
         if (getAmount(constValue10, constValue11, constValue6, constValue7) < getAmount(constValue8, constValue9, constValue6, constValue7)) {
            booleans[i] = true;
            constValue8 = constValue10;
            constValue9 = constValue11;
         }
      }

      return booleans;
   }

   public static double[] getAmountsForIliIilTypeDouble(IliIilType iliIilType, double constValue, double constValue2, double constValue3, double constValue4) {
      if (!iliIilType.isFlag()) {
         return new double[]{constValue + iliIilType.directionX * 0.2, constValue2 + iliIilType.degreesValue * 0.2};
      } else {
         double constValue5 = iliIilType.directionX != 0 ? constValue + iliIilType.directionX * 0.25 : constValue3;
         double constValue6 = iliIilType.degreesValue != 0 ? constValue2 + iliIilType.degreesValue * 0.25 : constValue4;
         return new double[]{constValue5, constValue6};
      }
   }

   static double getAmountDouble(double constBound, double constLimit, double goalBound) {
      return constBound < constLimit ? constLimit : Math.min(constBound, goalBound);
   }
}
