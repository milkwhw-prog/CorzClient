package com.corz.client.utils;

public final class ZoneEscapeUtils2 {

   private ZoneEscapeUtils2() {
   }

   private static double getAmount(double zoneEscapeBound, double zoneEscapeLimit, double goalBound) {
      return zoneEscapeBound < zoneEscapeLimit ? zoneEscapeLimit : Math.min(zoneEscapeBound, goalBound);
   }

   public static double getAmount(ZoneEscapeUtils2.Miss miss, double zoneEscapeValue, double zoneEscapeLimit, double pointsValue, double vecHashValue, int zoneEscapeLimit3) {
      if (miss != null && Double.isFinite(zoneEscapeLimit) && Double.isFinite(pointsValue) && zoneEscapeLimit3 >= 1) {
         if (zoneEscapeLimit > pointsValue) {
            double zoneEscapeLimit2 = zoneEscapeLimit;
            zoneEscapeLimit = pointsValue;
            pointsValue = zoneEscapeLimit2;
         }

         double zoneEscapeLimit4 = Double.isNaN(vecHashValue) ? 0.0 : Math.abs(vecHashValue);
         ZoneEscapeUtils2.Points points = new ZoneEscapeUtils2.Points(zoneEscapeLimit3 + 4);
         double zoneEscapeValue3 = Double.isFinite(zoneEscapeValue) ? getAmount(zoneEscapeValue, zoneEscapeLimit, pointsValue) : 0.5 * (zoneEscapeLimit + pointsValue);
         double amounts4 = points.getAmounts(miss, zoneEscapeValue3);
         if (Math.abs(amounts4) <= zoneEscapeLimit4) {
            return zoneEscapeValue3;
         } else if (points.pointCount >= zoneEscapeLimit3) {
            return points.getAmounts();
         } else {
            double zoneEscapeValue5 = getAmount(zoneEscapeValue3 + 2.0, zoneEscapeLimit, pointsValue);
            if (zoneEscapeValue5 == zoneEscapeValue3) {
               zoneEscapeValue5 = getAmount(zoneEscapeValue3 - 2.0, zoneEscapeLimit, pointsValue);
            }

            if (zoneEscapeValue5 == zoneEscapeValue3) {
               return Double.NaN;
            } else {
               double zoneEscapeFactor = points.getAmounts(miss, zoneEscapeValue5);
               if (Math.abs(zoneEscapeFactor) <= zoneEscapeLimit4) {
                  return zoneEscapeValue5;
               } else {
                  boolean zoneEscapeFlag = zoneEscapeValue3 == zoneEscapeLimit || zoneEscapeValue5 == zoneEscapeLimit;
                  boolean zoneEscapeFlag2 = zoneEscapeValue3 == pointsValue || zoneEscapeValue5 == pointsValue;

                  while (points.pointCount < zoneEscapeLimit3) {
                     double naN = Double.NaN;
                     if (Double.isFinite(amounts4) && Double.isFinite(zoneEscapeFactor) && zoneEscapeFactor != amounts4) {
                        naN = zoneEscapeValue5 - zoneEscapeFactor * (zoneEscapeValue5 - zoneEscapeValue3) / (zoneEscapeFactor - amounts4);
                     }

                     boolean zoneEscapeFlag3 = Double.isFinite(naN) && naN >= zoneEscapeLimit && naN <= pointsValue && Math.abs(naN - zoneEscapeValue5) > 1.0E-9;
                     if (zoneEscapeFlag3 && points.isAmount()) {
                        zoneEscapeFlag3 = naN > points.pointsAmount && naN < points.upperBound;
                     }

                     if (!zoneEscapeFlag3) {
                        if (!points.isAmount()) {
                           if (!zoneEscapeFlag) {
                              zoneEscapeFlag = true;
                              double amounts6 = points.getAmounts(miss, zoneEscapeLimit);
                              if (Math.abs(amounts6) <= zoneEscapeLimit4) {
                                 return zoneEscapeLimit;
                              }

                              if (points.pointCount >= zoneEscapeLimit3) {
                                 break;
                              }
                           }

                           if (!points.isAmount() && !zoneEscapeFlag2) {
                              zoneEscapeFlag2 = true;
                              double amounts7 = points.getAmounts(miss, pointsValue);
                              if (Math.abs(amounts7) <= zoneEscapeLimit4) {
                                 return pointsValue;
                              }

                              if (points.pointCount >= zoneEscapeLimit3) {
                                 break;
                              }
                           }

                           if (!points.isAmount()) {
                              return Double.NaN;
                           }
                        }

                        naN = 0.5 * (points.pointsAmount + points.upperBound);
                     }

                     double spotValue = points.getAmounts(miss, naN);
                     if (Math.abs(spotValue) <= zoneEscapeLimit4) {
                        return naN;
                     }

                     zoneEscapeValue3 = zoneEscapeValue5;
                     amounts4 = zoneEscapeFactor;
                     zoneEscapeValue5 = naN;
                     zoneEscapeFactor = spotValue;
                  }

                  return points.getAmounts();
               }
            }
         }
      } else {
         return Double.NaN;
      }
   }

   public interface Miss {
      double at(double missValue);
   }

   private static final class Points {
      private final double[] pointsTable;
      private final double[] table;
      private double upperBound;
      private int pointCount;
      private double pointsAmount = Double.NaN;

      private double getAmounts() {
         if (!this.isAmount()) {
            return Double.NaN;
         } else {
            double naN = Double.NaN;
            double pointsLimit = Double.POSITIVE_INFINITY;

            for (int i = 0; i < this.pointCount; i++) {
               double pointsValue = Math.abs(this.table[i]);
               if (pointsValue < pointsLimit) {
                  pointsLimit = pointsValue;
                  naN = this.pointsTable[i];
               }
            }

            return Double.isFinite(pointsLimit) ? naN : Double.NaN;
         }
      }

      private void updateAmountsAmount() {
         double pointsLimit = Double.POSITIVE_INFINITY;
         double amount = Double.NaN;
         double naN = Double.NaN;

         for (int i = 0; i < this.pointCount; i++) {
            for (int j = 0; j < this.pointCount; j++) {
               if (this.pointsTable[i] < this.pointsTable[j] && Math.signum(this.table[i]) * Math.signum(this.table[j]) < 0.0) {
                  double pointsValue = this.pointsTable[j] - this.pointsTable[i];
                  if (pointsValue < pointsLimit && this.canAmounts(this.pointsTable[i], this.pointsTable[j])) {
                     pointsLimit = pointsValue;
                     amount = this.pointsTable[i];
                     naN = this.pointsTable[j];
                  }
               }
            }
         }

         this.pointsAmount = amount;
         this.upperBound = naN;
      }

      private Points(int pointsSize) {
         this.upperBound = Double.NaN;
         this.pointsTable = new double[pointsSize];
         this.table = new double[pointsSize];
      }

      private double getAmounts(ZoneEscapeUtils2.Miss miss, double pointsValue) {
         double at = miss.at(pointsValue);
         if (Double.isNaN(at)) {
            at = Double.POSITIVE_INFINITY;
         }

         if (this.pointCount < this.pointsTable.length) {
            this.pointsTable[this.pointCount] = pointsValue;
            this.table[this.pointCount] = at;
            this.pointCount++;
         }

         this.updateAmountsAmount();
         return at;
      }

      private boolean isAmount() {
         return !Double.isNaN(this.pointsAmount);
      }

      private boolean canAmounts(double pointsValue, double pointsLimit) {
         for (int i = 0; i < this.pointCount; i++) {
            if (this.pointsTable[i] > pointsValue && this.pointsTable[i] < pointsLimit) {
               return false;
            }
         }

         return true;
      }
   }
}
