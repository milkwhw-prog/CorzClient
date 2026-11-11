package com.corz.client.utils;

import java.util.Arrays;

public final class ZoneEscapeUtilsFlag {
   private static final int[] zoneEscapeTable = new int[0];

   public static double getMax(double zoneEscapeBound) {
      return Double.isNaN(zoneEscapeBound) ? 0.0 : Math.max(0.0, 1.0 - Math.max(0.0, zoneEscapeBound) / 12.0);
   }

   public static int[] getValues(int zoneEscapeValue, double zoneEscapeBound, int zoneEscapeLimit) {
      if (zoneEscapeLimit >= 1 && zoneEscapeValue <= zoneEscapeLimit) {
         double zoneEscapeFactor = Double.isFinite(zoneEscapeBound) ? Math.max(1.0, zoneEscapeBound) : Double.NaN;
         int[] ints = new int[Math.max(1, zoneEscapeLimit)];
         int zoneEscapeCounter = 0;

         for (int i = 0; i <= 200 + zoneEscapeLimit; i++) {
            long zoneEscapeLimit2 = Double.isNaN(zoneEscapeFactor) ? (i == 0 ? zoneEscapeValue : Long.MAX_VALUE) : zoneEscapeValue + Math.round(i * zoneEscapeFactor);
            if (zoneEscapeLimit2 > zoneEscapeLimit) {
               break;
            }

            if (zoneEscapeLimit2 >= 1L && (zoneEscapeCounter <= 0 || ints[zoneEscapeCounter - 1] < zoneEscapeLimit2)) {
               ints[zoneEscapeCounter++] = (int)zoneEscapeLimit2;
            }
         }

         return Arrays.copyOf(ints, zoneEscapeCounter);
      } else {
         return (int[])zoneEscapeTable.clone();
      }
   }

   private static double getAmount(double zoneEscapeValue, double cx2, double vy2, double zoneEscapeValue4, double groundY, double zoneEscapeValue6) {
      double zoneEscapeFactor = zoneEscapeValue - zoneEscapeValue4;
      double zoneEscapeFactor2 = cx2 - groundY;
      double zoneEscapeFactor3 = vy2 - zoneEscapeValue6;
      return Math.sqrt(zoneEscapeFactor * zoneEscapeFactor + zoneEscapeFactor2 * zoneEscapeFactor2 + zoneEscapeFactor3 * zoneEscapeFactor3);
   }

   public static double[] getClearInstance(
      double zoneEscapeValue,
      double zoneEscapeValue2,
      double zoneEscapeValue3,
      double zoneEscapeTotal,
      double zoneEscapeTotal2,
      double zoneEscapeTotal3,
      double zoneEscapeLimit,
      int[] zoneEscapeValues,
      double zoneEscapeValue4,
      double groundY,
      double zoneEscapeValue6,
      ZoneEscapeUtilsFlag.Clear clear
   ) {
      if (canFlag(zoneEscapeValue, zoneEscapeValue2, zoneEscapeValue3) && canFlag(zoneEscapeTotal, zoneEscapeTotal2, zoneEscapeTotal3) && !Double.isNaN(zoneEscapeLimit)) {
         int[] zoneEscapeValues2 = zoneEscapeValues == null ? zoneEscapeTable : zoneEscapeValues;
         ZoneEscapeUtilsFlag.Clear clear3 = clear == null ? ZoneEscapeUtilsFlag.Clear.INSTANCE : clear;
         boolean zoneEscapeFlag = canFlag(zoneEscapeValue4, groundY, zoneEscapeValue6);
         double landY2 = zoneEscapeValue;
         double cx2 = zoneEscapeValue2;
         double vy2 = zoneEscapeValue3;
         int index = 0;

         for (int i = 1; i <= 200; i++) {
            while (index < zoneEscapeValues2.length && zoneEscapeValues2[index] < i) {
               index++;
            }

            while (index < zoneEscapeValues2.length && zoneEscapeValues2[index] == i) {
               index++;
               if (zoneEscapeFlag) {
                  double zoneEscapeValue7 = cx2 + 0.2125;
                  double zoneEscapeFactor = getMax(getAmount(landY2, cx2, vy2, zoneEscapeValue4, groundY, zoneEscapeValue6));
                  if (!(zoneEscapeFactor <= 0.0)) {
                     double zoneEscapeFactor2 = landY2 - zoneEscapeValue4;
                     double zoneEscapeFactor3 = zoneEscapeValue7 - groundY;
                     double zoneEscapeFactor4 = vy2 - zoneEscapeValue6;
                     double distance = Math.sqrt(zoneEscapeFactor2 * zoneEscapeFactor2 + zoneEscapeFactor3 * zoneEscapeFactor3 + zoneEscapeFactor4 * zoneEscapeFactor4);
                     if (!(distance < 1.0E-9) && clear3.clear(zoneEscapeValue4, groundY, zoneEscapeValue6, landY2, zoneEscapeValue7, vy2)) {
                        zoneEscapeTotal += zoneEscapeFactor2 / distance * zoneEscapeFactor;
                        zoneEscapeTotal2 += zoneEscapeFactor3 / distance * zoneEscapeFactor;
                        zoneEscapeTotal3 += zoneEscapeFactor4 / distance * zoneEscapeFactor;
                     }
                  }
               }
            }

            double zoneEscapeValue8 = zoneEscapeTotal2 - 0.03;
            zoneEscapeTotal *= 0.99;
            zoneEscapeTotal2 = zoneEscapeValue8 * 0.99;
            zoneEscapeTotal3 *= 0.99;
            double zoneEscapeValue9 = landY2 + zoneEscapeTotal;
            double zoneEscapeValue10 = cx2 + zoneEscapeTotal2;
            double zoneEscapeValue11 = vy2 + zoneEscapeTotal3;
            if (zoneEscapeTotal2 < 0.0 && zoneEscapeValue10 <= zoneEscapeLimit && cx2 >= zoneEscapeLimit) {
               double zoneEscapeBound = cx2 - zoneEscapeValue10 > 0.0 ? (cx2 - zoneEscapeLimit) / (cx2 - zoneEscapeValue10) : 0.0;
               double max5 = Math.max(0.0, Math.min(1.0, zoneEscapeBound));
               return new double[]{landY2 + (zoneEscapeValue9 - landY2) * max5, zoneEscapeLimit, vy2 + (zoneEscapeValue11 - vy2) * max5, i};
            }

            landY2 = zoneEscapeValue9;
            cx2 = zoneEscapeValue10;
            vy2 = zoneEscapeValue11;
         }

         return null;
      } else {
         return null;
      }
   }

   private static boolean canFlag(double zoneEscapeValue, double groundY, double zoneEscapeValue3) {
      return Double.isFinite(zoneEscapeValue) && Double.isFinite(groundY) && Double.isFinite(zoneEscapeValue3);
   }

   private static int[] getValues(int[] zoneEscapeValues, int zoneEscapeLimit) {
      int zoneEscapeCounter = 0;

      for (int zoneEscapeValuesItem : zoneEscapeValues) {
         if (zoneEscapeValuesItem >= 1 && zoneEscapeValuesItem <= zoneEscapeLimit) {
            zoneEscapeCounter++;
         }
      }

      if (zoneEscapeCounter == zoneEscapeValues.length) {
         return zoneEscapeValues;
      } else {
         int[] ints = new int[zoneEscapeCounter];
         int zoneEscapeCounter2 = 0;

         for (int zoneEscapeLimit3 : zoneEscapeValues) {
            if (zoneEscapeLimit3 >= 1 && zoneEscapeLimit3 <= zoneEscapeLimit) {
               ints[zoneEscapeCounter2++] = zoneEscapeLimit3;
            }
         }

         return ints;
      }
   }

   public static boolean canFlag(double zoneEscapeValue) {
      return !(zoneEscapeValue <= 1.2);
   }

   private ZoneEscapeUtilsFlag() {
   }

   public static double getInstance(
      double zoneEscapeValue,
      double zoneEscapeValue2,
      double zoneEscapeValue3,
      double zoneEscapeTotal,
      double zoneEscapeValue5,
      double zoneEscapeValue6,
      double zoneEscapeLimit,
      int[] zoneEscapeValues,
      double zoneEscapeValue8,
      double zoneEscapeValue9,
      double groundY,
      ZoneEscapeUtilsFlag.Clear clear
   ) {
      if (zoneEscapeValues != null && zoneEscapeValues.length != 0 && canFlag(zoneEscapeValue8, zoneEscapeValue9, groundY)) {
         double[] clearInstance = getClearInstance(zoneEscapeValue, zoneEscapeValue2, zoneEscapeValue3, zoneEscapeTotal, zoneEscapeValue5, zoneEscapeValue6, zoneEscapeLimit, zoneEscapeTable, zoneEscapeValue8, zoneEscapeValue9, groundY, ZoneEscapeUtilsFlag.Clear.INSTANCE);
         if (clearInstance == null) {
            return 0.0;
         } else {
            int[] values2 = getValues(zoneEscapeValues, (int)clearInstance[3]);
            if (values2.length == 0) {
               return 0.0;
            } else {
               double[] clearInstance2 = getClearInstance(zoneEscapeValue, zoneEscapeValue2, zoneEscapeValue3, zoneEscapeTotal, zoneEscapeValue5, zoneEscapeValue6, zoneEscapeLimit, values2, zoneEscapeValue8, zoneEscapeValue9, groundY, clear);
               return clearInstance2 == null ? Double.POSITIVE_INFINITY : Math.hypot(clearInstance2[0] - clearInstance[0], clearInstance2[2] - clearInstance[2]);
            }
         }
      } else {
         return 0.0;
      }
   }

   public interface Clear {
      ZoneEscapeUtilsFlag.Clear INSTANCE = (zoneEscapeValue, zoneEscapeValue2, zoneEscapeValue3, zoneEscapeValue4, zoneEscapeValue5, zoneEscapeValue6) -> true;

      boolean clear(double zoneEscapeValue, double groundY, double zoneEscapeValue3, double zoneEscapeValue4, double zoneEscapeValue5, double vy2);
   }
}
