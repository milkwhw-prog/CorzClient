package com.corz.client.utils;

import java.util.Arrays;
import com.corz.client.utils.AmountAmounts;

public final class MaxMin {
   private long windowStartTick;
   private long peakTick;
   private long pearlValue;
   private double smoothedHoleTime;
   private double pearlAmount;
   private long lastMinTick;
   private double boomDistance;
   private final int[] pearlTable;
   private final int[] table;
   private long lastMaxTick;
   public static final int UNUSED_ONE = 1;
   private long pearlMinTick;
   private final int[] pearlLowCounts;
   private long currentTick;
   private final int[] jumpResetCounts = new int[2];
   private long earliestMinTick;
   private long firstMaxTick;

   public void handleAmount(boolean flag) {
      this.pearlAmount = getAmount(this.pearlAmount + 0.02 * ((flag ? 1.0 : 0.0) - this.pearlAmount), 0.0, 1.0);
   }

   public double getAmount() {
      return this.smoothedHoleTime;
   }

   public double getTable() {
      return getAmount(this.table);
   }

   private static void handle(int[] ints, int index) {
      if (ints[index] < 1000000) {
         ints[index]++;
      }
   }

   public static int getValue(double d, double limit) {
      if (!Double.isFinite(d) || !Double.isFinite(limit)) {
         return -1;
      } else if (d < -2.0) {
         return 0;
      } else if (limit > 4.0) {
         return 1;
      } else {
         return limit < -4.0 ? 2 : 3;
      }
   }

   public MaxMin.State getAmountJumpResetCounts() {
      return new MaxMin.State(
         (int[])this.jumpResetCounts.clone(),
         (int[])this.table.clone(),
         (int[])this.pearlLowCounts.clone(),
         this.pearlAmount,
         this.boomDistance,
         this.smoothedHoleTime,
         (int[])this.pearlTable.clone()
      );
   }

   public void handleMaxMin(long min) {
      this.handleTime(min);
      if (this.lastMinTick == Long.MIN_VALUE) {
         this.earliestMinTick = min;
         this.lastMinTick = min;
      } else {
         this.earliestMinTick = Math.min(this.earliestMinTick, min);
         this.lastMinTick = Math.max(this.lastMinTick, min);
      }
   }

   private void updateTimeMax() {
      this.currentTick = Long.MIN_VALUE;
      this.earliestMinTick = Long.MIN_VALUE;
      this.lastMinTick = Long.MIN_VALUE;
      this.pearlValue = Long.MIN_VALUE;
      this.pearlMinTick = Long.MIN_VALUE;
      this.firstMaxTick = Long.MIN_VALUE;
      this.windowStartTick = Long.MIN_VALUE;
      this.lastMaxTick = Long.MIN_VALUE;
      this.peakTick = Long.MIN_VALUE;
   }

   public double getPearlTable() {
      return getAmount(this.pearlTable);
   }

   public double getJumpResetCounts() {
      return getAmount(this.jumpResetCounts);
   }

   public int getValue() {
      int index = 1;

      for (int i = 0; i < this.pearlLowCounts.length; i++) {
         if (this.pearlLowCounts[i] > this.pearlLowCounts[index]) {
            index = i;
         }
      }

      return index;
   }

   public void handleMaxTime(long time) {
      this.handleTime(time);
      boolean flag = this.lastMaxTick == Long.MIN_VALUE || time - this.lastMaxTick > 20L;
      if (flag && this.firstMaxTick == Long.MIN_VALUE) {
         this.firstMaxTick = time;
      }

      this.lastMaxTick = Math.max(this.lastMaxTick, time);
   }

   private void handleTime(long time) {
      if (this.currentTick != Long.MIN_VALUE && time < this.currentTick - 200L) {
         this.updateTimeMax();
      }

      if (this.currentTick == Long.MIN_VALUE || time > this.currentTick) {
         this.currentTick = time;
      }

      this.updateTimeMaxLastMinTick();
   }

   private static void handle(int[] ints) {
      if (ints[1] > 0) {
         ints[1]--;
      }

      handle(ints, 0);
   }

   private static double getAmount(int[] ints) {
      return (ints[0] + 1.0) / (ints[0] + ints[1] + 2.0);
   }

   private static void handle(int[] ints, int[] ints2) {
      if (ints != null) {
         for (int i = 0; i < ints2.length && i < ints.length; i++) {
            ints2[i] = Math.max(0, Math.min(1000000, ints[i]));
         }
      }
   }

   private void updateTimeMaxLastMinTick() {
      if (this.lastMinTick != Long.MIN_VALUE && this.currentTick - this.lastMinTick > 20L) {
         handle(this.jumpResetCounts, 1);
         this.pearlValue = this.earliestMinTick;
         this.pearlMinTick = this.lastMinTick;
         this.earliestMinTick = Long.MIN_VALUE;
         this.lastMinTick = Long.MIN_VALUE;
      }

      if (this.firstMaxTick != Long.MIN_VALUE && this.currentTick - this.firstMaxTick > 40L) {
         handle(this.table, 1);
         this.windowStartTick = this.firstMaxTick;
         this.firstMaxTick = Long.MIN_VALUE;
      }

      if (this.peakTick != Long.MIN_VALUE && this.currentTick - this.peakTick > 80.0) {
         this.peakTick = Long.MIN_VALUE;
      }
   }

   public double getBoomDistance() {
      return this.boomDistance;
   }

   private static double getAmount(double d, double d2, double d3) {
      return AmountAmounts.getAmount(d, d2, d3);
   }

   public void handle(boolean flag) {
      handle(this.pearlTable, flag ? 0 : 1);
   }

   public MaxMin() {
      this.table = new int[2];
      this.pearlLowCounts = new int[4];
      this.pearlTable = new int[2];
      this.pearlAmount = 0.0;
      this.boomDistance = 8.0;
      this.smoothedHoleTime = 12.0;
      this.currentTick = Long.MIN_VALUE;
      this.earliestMinTick = Long.MIN_VALUE;
      this.lastMinTick = Long.MIN_VALUE;
      this.pearlValue = Long.MIN_VALUE;
      this.pearlMinTick = Long.MIN_VALUE;
      this.firstMaxTick = Long.MIN_VALUE;
      this.windowStartTick = Long.MIN_VALUE;
      this.lastMaxTick = Long.MIN_VALUE;
      this.peakTick = Long.MIN_VALUE;
   }

   private void updateAmount() {
      Arrays.fill(this.jumpResetCounts, 0);
      Arrays.fill(this.table, 0);
      Arrays.fill(this.pearlLowCounts, 0);
      Arrays.fill(this.pearlTable, 0);
      this.pearlAmount = 0.0;
      this.boomDistance = 8.0;
      this.smoothedHoleTime = 12.0;
   }

   public double getPearlAmount() {
      return this.pearlAmount;
   }

   public void handleMax(long bound) {
      this.handleTime(bound);
      this.peakTick = Math.max(this.peakTick, bound);
   }

   public void handleMaxAmount(long limit) {
      this.handleTime(limit);
      if (this.peakTick != Long.MIN_VALUE && limit >= this.peakTick) {
         double d = Math.min(40.0, (double)(limit - this.peakTick));
         this.boomDistance = getAmount(this.boomDistance + 0.3 * (d - this.boomDistance), 0.0, 40.0);
         this.peakTick = Long.MIN_VALUE;
      }
   }

   public void handlePearlTime(MaxMin.State state) {
      this.updateAmount();
      this.pearlValue = Long.MIN_VALUE;
      this.pearlMinTick = Long.MIN_VALUE;
      this.windowStartTick = Long.MIN_VALUE;
      if (state != null) {
         handle(state.jumpReset(), this.jumpResetCounts);
         handle(state.pearlDir(), this.table);
         handle(state.pearlLow(), this.pearlLowCounts);
         handle(state.pearlLaunched(), this.pearlTable);
         this.pearlAmount = AmountAmounts.getAmount(state.retotem(), 0.0, 1.0, 0.0);
         this.boomDistance = AmountAmounts.getAmount(state.boom(), 0.0, 40.0, 8.0);
         this.smoothedHoleTime = AmountAmounts.getAmount(state.holeTime(), 3.0, 40.0, 12.0);
      }
   }

   public void update() {
      this.updateAmount();
      this.updateTimeMax();
   }

   public void handleTimeMax(long time, boolean flag, int limit) {
      if (this.currentTick == Long.MIN_VALUE || time > this.currentTick) {
         this.currentTick = time;
      }

      this.updateTimeMaxLastMinTick();
      if (limit >= 0 && limit < this.pearlLowCounts.length) {
         handle(this.pearlLowCounts, limit);
      }

      if (this.pearlMinTick != Long.MIN_VALUE && time >= this.pearlValue && time - this.pearlMinTick <= 20L) {
         handle(this.jumpResetCounts);
         this.pearlValue = Long.MIN_VALUE;
         this.pearlMinTick = Long.MIN_VALUE;
      } else if (this.lastMinTick != Long.MIN_VALUE && time >= this.earliestMinTick) {
         handle(this.jumpResetCounts, 0);
         this.earliestMinTick = Long.MIN_VALUE;
         this.lastMinTick = Long.MIN_VALUE;
      }

      long longValue = flag ? 20L : 0L;
      if (this.windowStartTick != Long.MIN_VALUE && time >= this.windowStartTick - longValue && time - this.windowStartTick <= 40L) {
         handle(this.table);
         this.windowStartTick = Long.MIN_VALUE;
      } else if (this.firstMaxTick != Long.MIN_VALUE && time >= this.firstMaxTick - longValue) {
         handle(this.table, 0);
         this.firstMaxTick = Long.MIN_VALUE;
      } else if (flag && (this.lastMaxTick == Long.MIN_VALUE || time - this.lastMaxTick > 20L)) {
         handle(this.table, 0);
      }

      if (flag) {
         this.lastMaxTick = Math.max(this.lastMaxTick, time);
      }
   }

   public void handleAmount(double limit) {
      if (Double.isFinite(limit) && !(limit <= 0.0) && !(limit > 80.0)) {
         double d = getAmount(limit, 3.0, 40.0);
         this.smoothedHoleTime = getAmount(this.smoothedHoleTime + 0.3 * (d - this.smoothedHoleTime), 3.0, 40.0);
      }
   }

   public record State(int[] pearlLaunched, int[] pearlLow, int[] pearlDir, double holeTime, double retotem, double boom, int[] jumpReset) {
      public State(int[] pearlLaunched, int[] pearlLow, int[] pearlDir, double holeTime, double retotem, double boom, int[] jumpReset) {
         this.jumpReset = pearlLaunched;
         this.pearlDir = pearlLow;
         this.pearlLow = pearlDir;
         this.retotem = holeTime;
         this.boom = retotem;
         this.holeTime = boom;
         this.pearlLaunched = jumpReset;
      }

      @Override
      public int hashCode() {
         int hashCode = Arrays.hashCode(this.jumpReset);
         int hashCodeValue = 31 * hashCode + Arrays.hashCode(this.pearlDir);
         int hashCodeValue2 = 31 * hashCodeValue + Arrays.hashCode(this.pearlLow);
         int hashCodeValue3 = 31 * hashCodeValue2 + Double.hashCode(this.retotem);
         int hashCodeValue4 = 31 * hashCodeValue3 + Double.hashCode(this.boom);
         int hashCodeValue5 = 31 * hashCodeValue4 + Double.hashCode(this.holeTime);
         return 31 * hashCodeValue5 + Arrays.hashCode(this.pearlLaunched);
      }

      @Override
      public String toString() {
         return "State[pearlLaunched="
            + Arrays.toString(this.jumpReset)
            + ", pearlLow="
            + Arrays.toString(this.pearlDir)
            + ", pearlDir="
            + Arrays.toString(this.pearlLow)
            + ", holeTime="
            + this.retotem
            + ", retotem="
            + this.boom
            + ", boom="
            + this.holeTime
            + ", jumpReset="
            + Arrays.toString(this.pearlLaunched)
            + "]";
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return !(o instanceof MaxMin.State state)
               ? false
               : Arrays.equals(this.jumpReset, state.jumpReset)
                  && Arrays.equals(this.pearlDir, state.pearlDir)
                  && Arrays.equals(this.pearlLow, state.pearlLow)
                  && Double.compare(this.retotem, state.retotem) == 0
                  && Double.compare(this.boom, state.boom) == 0
                  && Double.compare(this.holeTime, state.holeTime) == 0
                  && Arrays.equals(this.pearlLaunched, state.pearlLaunched);
         }
      }
   }
}
