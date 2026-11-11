package com.corz.client.utils;

public final class ConstAmountUtils {

   public static ConstAmountUtils.Columns getDataForInt(int constValue, int constValue2) {
      byte constValue3 = 36;
      int constValue4 = constValue3 + 166 + 4;
      return new ConstAmountUtils.Columns(
         new ConstAmountUtils.Rect(0, 0, 32, constValue2), new ConstAmountUtils.Rect(constValue3, 0, 166, constValue2), new ConstAmountUtils.Rect(constValue4, 0, Math.max(0, constValue - constValue4), constValue2)
      );
   }

   public static double getAmount(double constValue, double keybindBindLimit) {
      return Math.max(0.0, constValue - keybindBindLimit);
   }

   public static double getAmount(double constValue) {
      return 20.0 + constValue + 6.0 + 11.0 + 6.0;
   }

   public static double getAmount(double constValue, int constValue2, int constLimit, int constValue3, double constFactor) {
      if (!(constFactor <= 0.0) && constLimit > constValue3) {
         double constFactor2 = getAmount((constValue - constValue2) / (constLimit - constValue3), 0.0, 1.0);
         return Math.round(constFactor2 * constFactor);
      } else {
         return 0.0;
      }
   }

   private static double getAmount(double constLimit, double constLimit2, double constValue) {
      return constLimit < constLimit2 ? constLimit2 : (constLimit > constValue ? constValue : constLimit);
   }

   public static float getAmount(ConstAmountUtils.RailGeom railGeom, int constValue, double constValue2) {
      return (float)(railGeom.getValue(constValue) + 12.0 - constValue2);
   }

   public static float getMin(float constValue) {
      return Math.min(154.0F, constValue + 14.0F);
   }

   public static int getValue(double constDivisor, double constLimit, int constFactor) {
      return !(constDivisor <= constLimit) && !(constDivisor <= 0.0) ? Math.max(18, (int)(constLimit / constDivisor * constFactor)) : 0;
   }

   public static boolean canFlag(int constLimit, int constValue) {
      return constValue <= 24 || constLimit >= 268;
   }

   public static int getValue(int constValue) {
      return constValue - 12;
   }

   private ConstAmountUtils() {
   }

   public static int getValue() {
      return 8;
   }

   public static int getValue(int constValue, int constValue2, int slots) {
      if (slots <= 0) {
         return -1;
      } else {
         int constBound = constValue < 0 ? (constValue2 < 0 ? slots - 1 : 0) : constValue + constValue2;
         return constBound < 0 ? 0 : Math.min(constBound, slots - 1);
      }
   }

   public static double getAmountForRailGeomInt(ConstAmountUtils.RailGeom railGeom, int constValue, double constValue2) {
      return getAmount(railGeom, constValue, 0.0, constValue2);
   }

   public static int getValue(int constValue, float constValue2) {
      return Math.max(0, (int)(getValue4(constValue) - 8 - 8 - constValue2));
   }

   public static float getMin(double constValue) {
      return constValue <= 0.0 ? 0.0F : (float)Math.min(1.0, constValue / 10.0);
   }

   public static ConstAmountUtils.Rect getMaxDataForFlag(boolean constFlag) {
      return constFlag ? new ConstAmountUtils.Rect(6, 6, 12, 12) : new ConstAmountUtils.Rect(4, 4, 16, 16);
   }

   public static float getAmount(float constBound) {
      float constValue = constBound < 0.0F ? 0.0F : Math.min(1.0F, constBound);
      return 20.0F + 198.0F * (1.0F - constValue);
   }

   public static int getMax(int constValue) {
      return Math.max(0, constValue - 8 - 62);
   }

   public static int getValueForInt(int constValue) {
      return constValue - 16;
   }

   public static ConstAmountUtils.RailGeom getMaxForInt(int constValue, int constValue2) {
      return new ConstAmountUtils.RailGeom(constValue, constValue2);
   }

   public static int getValueInt(int constValue) {
      return constValue - 5;
   }

   public static float getAmount(double constValue, double constLimit, double constValue3, int keybindValue, int constValue5) {
      int constValue6 = getValue(constValue, constLimit, constValue5);
      if (constValue6 == 0) {
         return -1.0F;
      } else {
         double constFactor = getAmount(constValue3 / getAmount(constValue, constLimit), 0.0, 1.0);
         return (float)(keybindValue + constFactor * (constValue5 - constValue6));
      }
   }

   public static int getMaxInt(int constValue) {
      return Math.max(0, constValue - 58 - 4);
   }

   public static double getAmount(ConstAmountUtils.RailGeom railGeom, int constLimit, double constLimit2, double constValue) {
      double max2 = railGeom.getMax(constValue);
      if (constLimit >= 0 && constLimit < railGeom.getValue()) {
         double constValue3 = railGeom.getValue(constLimit) - 6;
         double constLimit3 = railGeom.getValue(constLimit) + 24 + 6;
         double constValue4 = constLimit2;
         if (constValue3 < constLimit2) {
            constValue4 = constValue3;
         } else if (constLimit3 > constLimit2 + constValue) {
            constValue4 = constLimit3 - constValue;
         }

         return getAmount(constValue4, 0.0, max2);
      } else {
         return getAmount(constLimit2, 0.0, max2);
      }
   }

   public static int getValue4(int constValue) {
      return constValue - 12 - 22;
   }

   public static float getAmount() {
      return 38.0F;
   }

   public static float getAmountForRailGeomIntDouble(ConstAmountUtils.RailGeom railGeom, int constValue, double constValue2) {
      return getAmount(railGeom, constValue, constValue2) - 8.0F;
   }

   public static double getAmountForDouble(double constValue, double amountNext, double betaListeningValue) {
      return getAmount(constValue, 0.0, getAmount(amountNext, betaListeningValue));
   }

   public static ConstAmountUtils.Rect getMaxDataRect() {
      return new ConstAmountUtils.Rect(19, 19, 4, 4);
   }

   public record Columns(ConstAmountUtils.Rect rail, ConstAmountUtils.Rect stage, ConstAmountUtils.Rect inspector) {
   }

   public static final class RailGeom {
      private final int[] railGeomTable;
      private final int tailCount;
      private final int maxRailGeomBound;
      private final int startIndex;
      private final int railGeomValue;

      public int getMax() {
         return this.tailCount;
      }

      public int getMaxRailGeomBound() {
         return this.maxRailGeomBound;
      }

      private RailGeom(int railGeomBound, int rocketsBuyBound) {
         this.maxRailGeomBound = Math.max(0, railGeomBound);
         this.tailCount = Math.max(0, rocketsBuyBound);
         this.railGeomTable = new int[this.maxRailGeomBound + this.tailCount];
         int railGeomTotal = 6;
         int railGeomCounter = 0;
         int railGeomValue = 0;

         for (int i = 0; i < this.maxRailGeomBound; i++) {
            this.railGeomTable[railGeomCounter++] = railGeomTotal;
            railGeomValue = railGeomTotal + 24;
            railGeomTotal += 27;
         }

         if (this.maxRailGeomBound > 0 && this.tailCount > 0) {
            this.startIndex = railGeomValue + 6;
            railGeomTotal = railGeomValue + 13;
         } else {
            this.startIndex = -1;
         }

         for (int j = 0; j < this.tailCount; j++) {
            this.railGeomTable[railGeomCounter++] = railGeomTotal;
            railGeomValue = railGeomTotal + 24;
            railGeomTotal += 27;
         }

         this.railGeomValue = railGeomCounter == 0 ? 0 : railGeomValue + 6;
      }

      public int getValue(double railGeomValue, double railGeomValue2) {
         double railGeomLimit = railGeomValue + railGeomValue2;

         for (int i = 0; i < this.railGeomTable.length; i++) {
            if (railGeomLimit >= this.railGeomTable[i] && railGeomLimit < this.railGeomTable[i] + 24) {
               return i;
            }
         }

         return -1;
      }

      public int getRailGeomValue() {
         return this.railGeomValue;
      }

      public double getMax(double railGeomValue) {
         return Math.max(0.0, this.railGeomValue - railGeomValue);
      }

      public int getValue() {
         return this.railGeomTable.length;
      }

      public int getValue(int index) {
         return this.railGeomTable[index];
      }

      public boolean canMax(int railGeomValue) {
         return railGeomValue < this.maxRailGeomBound;
      }

      public int getStartIndex() {
         return this.startIndex;
      }
   }

   public record Rect(int x, int y, int w, int h) {
      public int getValue() {
         return this.y + this.h;
      }

      public ConstAmountUtils.Rect getMaxDataForInt(int dx, int dy) {
         return new ConstAmountUtils.Rect(this.x + dx, this.y + dy, this.w, this.h);
      }

      public boolean canFlag(double py, double px) {
         return py >= this.x && py < this.x + this.w && px >= this.y && px < this.y + this.h;
      }

      public int getValueX() {
         return this.x + this.w;
      }
   }
}
