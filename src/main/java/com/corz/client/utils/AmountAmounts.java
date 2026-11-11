package com.corz.client.utils;

import java.util.Arrays;

public final class AmountAmounts {
   private final double[] table;
   public static final int[] UNUSED_STEP_THRESHOLDS = new int[]{2, 4, 6};
   private double airMultiplier;
   private static final int[] STEP_THRESHOLDS = new int[]{2, 4, 6};
   private double explosionScale;
   private int activeTicks;
   private double meleeScale;
   private final double[] lateralOffsets = new double[STEP_THRESHOLDS.length];
   private double speedOffset;

   public double getAmount() {
      return this.airMultiplier;
   }

   public double getAmounts(int n) {
      return getAmount(this.table, n) * this.getMin();
   }

   public static double getAmount(double limit, double d2, double d) {
      return limit < d2 ? d2 : (limit > d ? d : limit);
   }

   public void handleAmount(double d) {
      if (Double.isFinite(d) && !(Math.abs(d) > 0.05)) {
         this.speedOffset = getAmount(this.speedOffset + 0.1 * (d - this.speedOffset), -0.026, 0.026);
      }
   }

   public static double getAmount(double d, double d2, double d3, double d4) {
      return Double.isNaN(d) ? d4 : getAmount(d, d2, d3);
   }

   private static int getValue(int n) {
      for (int i = 0; i < STEP_THRESHOLDS.length; i++) {
         if (STEP_THRESHOLDS[i] == n) {
            return i;
         }
      }

      return -1;
   }

   public int getMax() {
      return this.activeTicks;
   }

   private static double getAmount(double d, double divisor, double bound, double factor, double d2, double d3) {
      if (Double.isFinite(divisor) && Double.isFinite(bound) && !(divisor <= 0.2)) {
         double d4 = getAmount(Math.max(0.0, bound) / divisor, d2, d3);
         return getAmount(d + factor * (d4 - d), d2, d3);
      } else {
         return d;
      }
   }

   public double[] getAmount(double factor, double factor2, double factor3, double factor4, int n) {
      double airMultiplier5 = this.airMultiplier;
      double distance = Math.sqrt(factor3 * factor3 + factor4 * factor4);
      if (Double.isFinite(distance) && !(distance < 0.03)) {
         double factor6 = factor3 / distance;
         double factor7 = factor4 / distance;
         double factor8 = -factor7;
         double amountsForInt = this.getAmountsForInt(n);
         double amounts10 = this.getAmounts(n);
         return new double[]{factor * airMultiplier5 + factor6 * amountsForInt + factor8 * amounts10, factor2 * airMultiplier5 + factor7 * amountsForInt + factor6 * amounts10};
      } else {
         return new double[]{factor * airMultiplier5, factor2 * airMultiplier5};
      }
   }

   public void handleAmount(double d, double d2) {
      this.explosionScale = getAmount(this.explosionScale, d, d2, 0.2, 0.6, 1.3);
   }

   public double getExplosionScale() {
      return this.explosionScale;
   }

   private double getMin() {
      return Math.min(1.0, this.activeTicks / 20.0);
   }

   private static double getAmount(double[] doubles, int factor) {
      if (factor <= 0) {
         return 0.0;
      } else if (factor < STEP_THRESHOLDS[0]) {
         return doubles[0] * factor / STEP_THRESHOLDS[0];
      } else {
         for (int i = 1; i < STEP_THRESHOLDS.length; i++) {
            if (factor <= STEP_THRESHOLDS[i]) {
               double factor2 = (double)(factor - STEP_THRESHOLDS[i - 1]) / (STEP_THRESHOLDS[i] - STEP_THRESHOLDS[i - 1]);
               return doubles[i - 1] + (doubles[i] - doubles[i - 1]) * factor2;
            }
         }

         return doubles[STEP_THRESHOLDS.length - 1];
      }
   }

   public double getAmountsForInt(int n) {
      return getAmount(this.lateralOffsets, n) * this.getMin();
   }

   public void updateAmountAmounts() {
      Arrays.fill(this.lateralOffsets, 0.0);
      Arrays.fill(this.table, 0.0);
      this.airMultiplier = 1.0;
      this.explosionScale = 1.0;
      this.meleeScale = 1.0;
      this.speedOffset = 0.0;
      this.activeTicks = 0;
   }

   public double getSpeedOffset() {
      return this.speedOffset;
   }

   public void handleAmountForDouble(double d, double d2) {
      this.airMultiplier = getAmount(this.airMultiplier, d, d2, 0.15, 0.6, 1.2);
   }

   public AmountAmounts() {
      this.table = new double[STEP_THRESHOLDS.length];
      this.airMultiplier = 1.0;
      this.explosionScale = 1.0;
      this.meleeScale = 1.0;
      this.speedOffset = 0.0;
      this.activeTicks = 0;
   }

   public void handleAmountAmounts(AmountAmounts.State state) {
      this.updateAmountAmounts();
      if (state != null) {
         for (int i = 0; i < STEP_THRESHOLDS.length; i++) {
            this.lateralOffsets[i] = getAmount(getAmountForDoubleArrayInt(state.lateral(), i), -0.6, 0.6, 0.0);
            this.table[i] = getAmount(getAmountForDoubleArrayInt(state.along(), i), -0.6, 0.6, 0.0);
         }

         this.airMultiplier = getAmount(state.air(), 0.6, 1.2, 1.0);
         this.explosionScale = getAmount(state.expl(), 0.6, 1.3, 1.0);
         this.meleeScale = getAmount(state.melee(), 0.8, 1.2, 1.0);
         this.speedOffset = getAmount(state.speed(), -0.026, 0.026, 0.0);
         this.activeTicks = Math.max(0, Math.min(1000000, state.n()));
      }
   }

   public AmountAmounts.State getAmountAmounts() {
      return new AmountAmounts.State(
         (double[])this.lateralOffsets.clone(), (double[])this.table.clone(), this.airMultiplier, this.explosionScale, this.meleeScale, this.speedOffset, this.activeTicks
      );
   }

   public double getMeleeScale() {
      return this.meleeScale;
   }

   public void handleAmountMeleeScale(double d, double d2) {
      this.meleeScale = getAmount(this.meleeScale, d, d2, 0.2, 0.8, 1.2);
   }

   public void handleAmountsMax(int n, double d, double d2) {
      int index = getValue(n);
      if (index >= 0 && Double.isFinite(d) && Double.isFinite(d2)) {
         if (!(Math.abs(d) > 2.0) && !(Math.abs(d2) > 2.0)) {
            this.lateralOffsets[index] = getAmount(this.lateralOffsets[index] + 0.1 * (d - this.lateralOffsets[index]), -0.6, 0.6);
            this.table[index] = getAmount(this.table[index] + 0.1 * (d2 - this.table[index]), -0.6, 0.6);
            if (this.activeTicks < 1000000) {
               this.activeTicks++;
            }
         }
      }
   }

   private static double getAmountForDoubleArrayInt(double[] doubles, int index) {
      return doubles != null && index < doubles.length ? doubles[index] : 0.0;
   }

   public record State(double[] along, double[] lateral, double speed, double melee, double expl, double air, int n) {
      @Override
      public int hashCode() {
         int hashCode = Arrays.hashCode(this.lateral);
         int hashCodeValue = 31 * hashCode + Arrays.hashCode(this.along);
         int hashCodeValue2 = 31 * hashCodeValue + Double.hashCode(this.air);
         int hashCodeValue3 = 31 * hashCodeValue2 + Double.hashCode(this.expl);
         int hashCodeValue4 = 31 * hashCodeValue3 + Double.hashCode(this.melee);
         int hashCodeValue5 = 31 * hashCodeValue4 + Double.hashCode(this.speed);
         return 31 * hashCodeValue5 + this.n;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return !(o instanceof AmountAmounts.State state)
               ? false
               : Arrays.equals(this.lateral, state.lateral)
                  && Arrays.equals(this.along, state.along)
                  && Double.compare(this.air, state.air) == 0
                  && Double.compare(this.expl, state.expl) == 0
                  && Double.compare(this.melee, state.melee) == 0
                  && Double.compare(this.speed, state.speed) == 0
                  && this.n == state.n;
         }
      }

      public State(double[] along, double[] lateral, double speed, double melee, double expl, double air, int n) {
         this.lateral = along;
         this.along = lateral;
         this.air = speed;
         this.expl = melee;
         this.melee = expl;
         this.speed = air;
         this.n = n;
      }

      @Override
      public String toString() {
         return "State[along="
            + Arrays.toString(this.lateral)
            + ", lateral="
            + Arrays.toString(this.along)
            + ", speed="
            + this.air
            + ", melee="
            + this.expl
            + ", expl="
            + this.melee
            + ", air="
            + this.speed
            + ", n="
            + this.n
            + "]";
      }
   }
}
