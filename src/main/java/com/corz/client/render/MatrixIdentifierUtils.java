package com.corz.client.render;

public final class MatrixIdentifierUtils {
   public static double getAmount(double matrixIdentifierValue, double thumbAmount, double ctlValue, double matrixIdentifierFactor) {
      if (matrixIdentifierFactor <= 0.0) {
         return matrixIdentifierValue;
      } else {
         return ctlValue <= 0.0 ? thumbAmount : matrixIdentifierValue + (thumbAmount - matrixIdentifierValue) * (1.0 - Math.exp(-ctlValue * matrixIdentifierFactor));
      }
   }

   public static double getAmount(double matrixIdentifierValue, double matrixIdentifierValue2, double matrixIdentifierFactor) {
      return matrixIdentifierValue + (matrixIdentifierValue2 - matrixIdentifierValue) * matrixIdentifierFactor;
   }

   private MatrixIdentifierUtils() {
   }

   public static double getAmountForDouble(double matrixIdentifierLimit, double matrixIdentifierLimit2, double matrixIdentifierValue) {
      return matrixIdentifierLimit < matrixIdentifierLimit2 ? matrixIdentifierLimit2 : (matrixIdentifierLimit > matrixIdentifierValue ? matrixIdentifierValue : matrixIdentifierLimit);
   }

   public static final class Spring {
      public double springPosition;
      public double anywayEnableCount;

      public void handleAmount(double springValue, double springFactor, double springFactor2, double springFactor3) {
         if (!(springFactor3 <= 0.0)) {
            double springFactor4 = (springValue - this.springPosition) * springFactor - this.anywayEnableCount * springFactor2;
            this.anywayEnableCount += springFactor4 * springFactor3;
            this.springPosition = this.springPosition + this.anywayEnableCount * springFactor3;
         }
      }

      public Spring(double amount) {
         this.springPosition = amount;
      }

      public void handleAmount(double amount) {
         this.springPosition = amount;
         this.anywayEnableCount = 0.0;
      }

      public boolean canAmount(double springValue, double springLimit) {
         return Math.abs(this.springPosition - springValue) < springLimit && Math.abs(this.anywayEnableCount) < springLimit;
      }
   }
}
