package com.corz.client.utils;

import java.util.Arrays;

public final class AmountsScaffold {
   private final double[] scaffoldTable = new double[4];
   private double accumulatedDelta;
   private int historyIndex;

   public double getAmountsAmount() {
      double amountsAmount = this.accumulatedDelta;

      for (double scaffoldValue : this.scaffoldTable) {
         amountsAmount += scaffoldValue;
      }

      return amountsAmount;
   }

   public void updateAmountAmounts() {
      this.scaffoldTable[this.historyIndex] = this.accumulatedDelta;
      this.historyIndex = (this.historyIndex + 1) % 4;
      this.accumulatedDelta = 0.0;
   }

   public void handleAmount(double scaffoldValue, double groundY) {
      if (Double.isFinite(scaffoldValue)) {
         this.accumulatedDelta = this.accumulatedDelta + Math.abs(scaffoldValue);
      }

      if (Double.isFinite(groundY)) {
         this.accumulatedDelta = this.accumulatedDelta + Math.abs(groundY);
      }
   }

   public boolean isFlag() {
      return this.getAmountsAmount() >= 16.0;
   }

   public void updateAmountsAmount() {
      Arrays.fill(this.scaffoldTable, 0.0);
      this.historyIndex = 0;
      this.accumulatedDelta = 0.0;
   }
}
