package com.corz.client.inventory;

public final class FlagInventoryUtils {

   static int getValue(boolean[] inventoryFlags, int[] inventoryValues, int inventoryValue) {
      int inventoryValue2 = -1;
      int inventoryValue3 = -1;
      int inventoryLimit = Math.min(Math.min(inventoryFlags.length, inventoryValues.length), 9);

      for (int i = 0; i < inventoryLimit; i++) {
         if (inventoryFlags[i]) {
            int max2 = Math.max(0, inventoryValues[i]);
            if (max2 > inventoryValue3 || max2 == inventoryValue3 && i == inventoryValue) {
               inventoryValue2 = i;
               inventoryValue3 = max2;
            }
         }
      }

      return inventoryValue2;
   }

   static double getCos(double cosBound) {
      double degrees = Math.max(0.0, Math.min(180.0, cosBound));
      return Math.cos(Math.toRadians(degrees));
   }

   static double getAmount(
      double inventoryLimit, double inventoryLimit2, double inventoryLimit3, double inventoryFactor, double inventoryFactor2, double inventoryFactor3, double inventoryValue, double inventoryValue2, double inventoryValue3, double inventoryLimit4, double inventoryLimit5, double inventoryLimit6
   ) {
      if (inventoryLimit >= inventoryValue && inventoryLimit <= inventoryLimit4 && inventoryLimit2 >= inventoryValue2 && inventoryLimit2 <= inventoryLimit5 && inventoryLimit3 >= inventoryValue3 && inventoryLimit3 <= inventoryLimit6) {
         return 0.0;
      } else {
         double distance = Math.sqrt(inventoryFactor * inventoryFactor + inventoryFactor2 * inventoryFactor2 + inventoryFactor3 * inventoryFactor3);
         if (!(distance > 1.0E-12)) {
            return Double.POSITIVE_INFINITY;
         } else {
            double[] doubles = new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
            if (canFlag(inventoryLimit, inventoryFactor / distance, inventoryValue, inventoryLimit4, doubles) && canFlag(inventoryLimit2, inventoryFactor2 / distance, inventoryValue2, inventoryLimit5, doubles) && canFlag(inventoryLimit3, inventoryFactor3 / distance, inventoryValue3, inventoryLimit6, doubles)) {
               return doubles[0] >= 0.0 ? doubles[0] : Double.POSITIVE_INFINITY;
            } else {
               return Double.POSITIVE_INFINITY;
            }
         }
      }
   }

   static boolean canFlag(double inventoryFactor, double inventoryFactor2, double inventoryFactor3, double inventoryFactor4, double inventoryValue, double inventoryValue2) {
      double distance = Math.sqrt(inventoryFactor3 * inventoryFactor3 + inventoryFactor4 * inventoryFactor4);
      double inventoryLimit = distance < 1.0E-5 ? 0.0 : (inventoryFactor3 * inventoryFactor + inventoryFactor4 * inventoryFactor2) / distance;
      return inventoryLimit >= inventoryValue - inventoryValue2;
   }

   static int getValue(int inventoryValue) {
      return inventoryValue <= 0 ? 0 : (inventoryValue + 25) / 50;
   }

   public static boolean canFlag(long inventoryLimit, long inventoryValue, long inventoryValue2) {
      return inventoryLimit >= inventoryValue && canFlag(inventoryLimit, inventoryValue2);
   }

   static int getValue(boolean[] inventoryFlags, int index) {
      int inventoryLimit = Math.min(inventoryFlags.length, 9);
      if (index >= 0 && index < inventoryLimit && inventoryFlags[index]) {
         return index;
      } else {
         for (int i = 0; i < inventoryLimit; i++) {
            if (inventoryFlags[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static boolean canFlag(int inventoryValue, int delayHeadValue, boolean inventoryFlag) {
      return inventoryValue == delayHeadValue || inventoryFlag;
   }

   static boolean canFlag(int inventoryLimit, int inventoryBound, int inventoryValue) {
      return inventoryLimit >= 0 && inventoryLimit + Math.max(0, inventoryBound) >= inventoryValue;
   }

   static double[] getAmounts(double inventoryValue, double inventoryValue2, int inventoryBound) {
      double[] doubles = new double[Math.max(0, inventoryBound)];
      double vy2 = inventoryValue;
      double inventoryTotal = 0.0;

      for (int i = 0; i < doubles.length; i++) {
         inventoryTotal += vy2;
         doubles[i] = inventoryTotal;
         vy2 = (vy2 - inventoryValue2) * 0.98;
      }

      return doubles;
   }

   static boolean canFlag(int inventoryValue, boolean inventoryFlag, int inventoryLimit) {
      return inventoryFlag || inventoryValue <= inventoryLimit;
   }

   static boolean canFlag(double inventoryValue, double inventoryLimit) {
      return inventoryValue <= inventoryLimit;
   }

   public static boolean canFlag(long inventoryLimit, long inventoryValue) {
      return inventoryLimit > inventoryValue;
   }

   static boolean canFlag(double inventoryLimit, boolean inventoryFlag, double inventoryValue) {
      return !inventoryFlag && inventoryLimit > 1.5 && inventoryLimit >= inventoryValue;
   }

   private static boolean canFlag(double inventoryLimit, double inventoryDivisor, double inventoryValue, double inventoryLimit2, double[] inventoryValues) {
      if (!(Math.abs(inventoryDivisor) < 1.0E-12)) {
         double inventoryBound = (inventoryValue - inventoryLimit) / inventoryDivisor;
         double goalBound = (inventoryLimit2 - inventoryLimit) / inventoryDivisor;
         inventoryValues[0] = Math.max(inventoryValues[0], Math.min(inventoryBound, goalBound));
         inventoryValues[1] = Math.min(inventoryValues[1], Math.max(inventoryBound, goalBound));
         return inventoryValues[0] <= inventoryValues[1];
      } else {
         return inventoryLimit >= inventoryValue && inventoryLimit <= inventoryLimit2;
      }
   }

   public static boolean canFlag(int inventoryLimit, long inventoryValue, int inventoryValue2, long inventoryLimit2, int inventoryLimit3) {
      return inventoryLimit >= 0 && inventoryLimit == inventoryValue2 && inventoryLimit2 >= inventoryValue && inventoryLimit2 - inventoryValue < inventoryLimit3;
   }

   public static boolean canFlagForLong(long inventoryLimit, long inventoryValue, long inventoryLimit2) {
      return inventoryLimit > inventoryValue && inventoryLimit <= inventoryLimit2;
   }

   static double getAmount(double inventoryBound, double inventoryValue, double inventoryValue2) {
      return Double.isNaN(inventoryValue) ? Math.max(0.0, inventoryBound) : Math.max(0.0, Math.min(inventoryBound, inventoryValue - inventoryValue2));
   }

   private FlagInventoryUtils() {
   }
}
