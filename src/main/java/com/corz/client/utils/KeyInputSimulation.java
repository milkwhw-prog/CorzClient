package com.corz.client.utils;

import java.util.Arrays;
import java.util.Random;

public final class KeyInputSimulation {
   private static final double DIAGONAL_INPUT_FACTOR = 1.0 / Math.sqrt(2.0);

   private static float getAxisValue(boolean inputForwardFlag, boolean inputForwardFlag2) {
      return inputForwardFlag == inputForwardFlag2 ? 0.0F : (inputForwardFlag ? 1.0F : -1.0F);
   }

   private static boolean isValidDirection(int[] inputForwardValues) {
      if (inputForwardValues != null && inputForwardValues.length >= 2) {
         int inputForwardLimit = inputForwardValues[0];
         int inputForwardLimit2 = inputForwardValues[1];
         return inputForwardLimit >= -1 && inputForwardLimit <= 1 && inputForwardLimit2 >= -1 && inputForwardLimit2 <= 1 && (inputForwardLimit != 0 || inputForwardLimit2 != 0);
      } else {
         return false;
      }
   }

   private static double scoreDirection(int inputForwardFactor, int i, double inputForwardFactor3, double inputForwardFactor4) {
      double inputForwardFactor5 = inputForwardFactor != 0 && i != 0 ? DIAGONAL_INPUT_FACTOR : 1.0;
      return (inputForwardFactor * inputForwardFactor3 + i * inputForwardFactor4) * inputForwardFactor5;
   }

   public static float[] getMovementVector(KeyInputSimulation.Keys keys) {
      if (keys == null) {
         return new float[]{0.0F, 0.0F};
      } else {
         float forwardBackFactor = getAxisValue(keys.left(), keys.right());
         float axisValue = getAxisValue(keys.forward(), keys.back());
         float distance = (float)Math.sqrt(forwardBackFactor * forwardBackFactor + axisValue * axisValue);
         return distance < 1.0E-4F ? new float[]{0.0F, 0.0F} : new float[]{forwardBackFactor / distance, axisValue / distance};
      }
   }

   public static int[] chooseKeyDirection(double inputForwardFactor, double inputForwardFactor2, float degrees, int[] inputForwardValues) {
      double angle = Math.toRadians(degrees);
      double sin = Math.sin(angle);
      double cos = Math.cos(angle);
      double inputForwardValue = -inputForwardFactor * sin + inputForwardFactor2 * cos;
      double deltaX = inputForwardFactor * cos + inputForwardFactor2 * sin;
      double hypot = Math.hypot(deltaX, inputForwardValue);
      if (hypot >= 1.0E-6 && !Double.isInfinite(hypot)) {
         double inputForwardValue3 = deltaX / hypot;
         double inputForwardValue4 = inputForwardValue / hypot;
         int inputForwardValue5 = 0;
         int inputForwardValue6 = 0;
         double maxValue7 = -Double.MAX_VALUE;

         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               if (i != 0 || j != 0) {
                  double inputForwardLimit = scoreDirection(j, i, inputForwardValue3, inputForwardValue4);
                  if (inputForwardLimit > maxValue7) {
                     maxValue7 = inputForwardLimit;
                     inputForwardValue5 = j;
                     inputForwardValue6 = i;
                  }
               }
            }
         }

         return isValidDirection(inputForwardValues) && scoreDirection(inputForwardValues[0], inputForwardValues[1], inputForwardValue3, inputForwardValue4) >= maxValue7 - 0.05
            ? new int[]{inputForwardValues[0], inputForwardValues[1]}
            : new int[]{inputForwardValue5, inputForwardValue6};
      } else {
         return new int[]{0, 0};
      }
   }

   private KeyInputSimulation() {
   }

   public static final class Dwell {
      private static final int DWELL_UNIT = 1;
      private final long[] keyPressTicks;
      private long currentTick;
      private final Random random;
      private final boolean[] keyStates = new boolean[7];
      private static final int[] DEFAULT_MIN_HOLD_TICKS = new int[]{2, 2, 3, 3, 1, 3, 2};
      private static final int[] MIN_RELEASE_TICKS = new int[]{1, 1, 1, 1, 1, 2, 1};
      private final long[] keyLastChangeTimes;
      private static final int[] KEY_ORDER_SPECIAL_FIRST = new int[]{6, 4, 5, 0, 1, 2, 3};
      private static final int[] KEY_ORDER_MOVEMENT_FIRST = new int[]{0, 1, 2, 3, 6, 4, 5};
      private final int[] minDwellTimes;
      private final int[] recentChangeBuckets;
      private final boolean[] previousKeyStates = new boolean[7];

      public int getRecentChangeCount() {
         int dwellTotal = 0;

         for (int dwellValue : this.recentChangeBuckets) {
            dwellTotal += dwellValue;
         }

         return dwellTotal;
      }

      private static boolean[] toKeyArray(KeyInputSimulation.Keys keys) {
         return keys instanceof KeyInputSimulation.Keys(boolean forwardSprintFlag, boolean forwardSprintFlag2, boolean forwardSprintFlag3, boolean forwardSprintFlag4, boolean forwardSprintFlag5, boolean forwardSprintFlag6, boolean forwardSprintFlag7)
            ? new boolean[]{forwardSprintFlag, forwardSprintFlag2, forwardSprintFlag3, forwardSprintFlag4, forwardSprintFlag5, forwardSprintFlag6, forwardSprintFlag7}
            : new boolean[7];
      }

      public KeyInputSimulation.Keys applyDwell(KeyInputSimulation.Keys keys, boolean dwellFlag, KeyInputSimulation.Keys keys2) {
         this.currentTick++;
         this.recentChangeBuckets[getBucketIndex(this.currentTick)] = 0;
         boolean[] forwardSprint = toKeyArray(keys);
         boolean[] keyArray = toKeyArray(keys2);

         for (int i = 0; i < 7; i++) {
            if (forwardSprint[i] && !this.previousKeyStates[i]) {
               this.keyPressTicks[i] = this.currentTick;
            }

            this.previousKeyStates[i] = forwardSprint[i];
         }

         this.resolveOpposingKeys(forwardSprint, 0, 1);
         this.resolveOpposingKeys(forwardSprint, 2, 3);
         if (forwardSprint[6] && (!forwardSprint[0] || forwardSprint[1])) {
            forwardSprint[6] = false;
         }

         int dwellLimit = 12 + (dwellFlag ? 4 : 0);

         for (int index : KEY_ORDER_SPECIAL_FIRST) {
            if (this.keyStates[index] && !forwardSprint[index]) {
               boolean dwellFlag2 = dwellFlag || keyArray[index];
               if ((dwellFlag2 || this.getTicksSinceChange(index) >= this.minDwellTimes[index]) && (index != 0 || !this.keyStates[6]) && this.getRecentChangeCount() < (dwellFlag2 ? 16 : dwellLimit)) {
                  this.setKeyState(index, false);
               }
            }
         }

         for (int keyOrderMovementFirstItem : KEY_ORDER_MOVEMENT_FIRST) {
            if (!this.keyStates[keyOrderMovementFirstItem] && forwardSprint[keyOrderMovementFirstItem] && (dwellFlag || this.getTicksSinceChange(keyOrderMovementFirstItem) >= MIN_RELEASE_TICKS[keyOrderMovementFirstItem])) {
               int oppositeKey = getOppositeKey(keyOrderMovementFirstItem);
               if ((oppositeKey < 0 || !this.keyStates[oppositeKey]) && (keyOrderMovementFirstItem != 6 || this.keyStates[0] && !this.keyStates[1]) && this.getRecentChangeCount() < dwellLimit) {
                  this.setKeyState(keyOrderMovementFirstItem, true);
                  this.minDwellTimes[keyOrderMovementFirstItem] = this.rollMinHoldTicks(keyOrderMovementFirstItem);
               }
            }
         }

         return new KeyInputSimulation.Keys(this.keyStates[0], this.keyStates[1], this.keyStates[2], this.keyStates[3], this.keyStates[4], this.keyStates[5], this.keyStates[6]);
      }

      private static boolean[] resolveConflicts(boolean[] flags) {
         if (flags[0] && flags[1]) {
            flags[0] = false;
            flags[1] = false;
         }

         if (flags[2] && flags[3]) {
            flags[2] = false;
            flags[3] = false;
         }

         if (flags[6] && (!flags[0] || flags[1])) {
            flags[6] = false;
         }

         return flags;
      }

      private void resolveOpposingKeys(boolean[] dwellFlags, int index, int index2) {
         if (dwellFlags[index] && dwellFlags[index2]) {
            if (this.keyPressTicks[index] > this.keyPressTicks[index2]) {
               dwellFlags[index2] = false;
            } else if (this.keyPressTicks[index2] > this.keyPressTicks[index]) {
               dwellFlags[index] = false;
            } else if (this.keyStates[index] && !this.keyStates[index2]) {
               dwellFlags[index2] = false;
            } else if (this.keyStates[index2] && !this.keyStates[index]) {
               dwellFlags[index] = false;
            } else {
               dwellFlags[index] = false;
               dwellFlags[index2] = false;
            }
         }
      }

      private static int getOppositeKey(int dwellValue) {
         return switch (dwellValue) {
            case 0 -> 1;
            case 1 -> 0;
            case 2 -> 3;
            case 3 -> 2;
            default -> -1;
         };
      }

      private int rollMinHoldTicks(int index) {
         if (index == 2 || index == 3) {
            return 3 + this.random.nextInt(4);
         } else {
            return index == 4 ? 1 + this.random.nextInt(3) : DEFAULT_MIN_HOLD_TICKS[index];
         }
      }

      private void setKeyState(int index, boolean dwellFlag) {
         this.keyStates[index] = dwellFlag;
         this.keyLastChangeTimes[index] = this.currentTick;
         this.recentChangeBuckets[getBucketIndex(this.currentTick)]++;
      }

      public Dwell(Random random) {
         this.keyPressTicks = new long[7];
         this.keyLastChangeTimes = new long[7];
         this.minDwellTimes = new int[7];
         this.recentChangeBuckets = new int[20];
         this.random = random != null ? random : new Random();
         this.resetToKeys(KeyInputSimulation.Keys.INSTANCE);
      }

      private static int getBucketIndex(long floorModValue) {
         return (int)Math.floorMod(floorModValue, 20L);
      }

      private long getTicksSinceChange(int index) {
         return this.currentTick - this.keyLastChangeTimes[index];
      }

      public void resetToKeys(KeyInputSimulation.Keys keys) {
         Arrays.fill(this.recentChangeBuckets, 0);
         boolean[] flags = resolveConflicts(toKeyArray(keys));

         for (int i = 0; i < 7; i++) {
            this.keyStates[i] = flags[i];
            this.previousKeyStates[i] = flags[i];
            this.keyPressTicks[i] = this.currentTick - 1048576L;
            this.keyLastChangeTimes[i] = this.currentTick - 1048576L;
            this.minDwellTimes[i] = DEFAULT_MIN_HOLD_TICKS[i];
         }
      }

      void syncChangedKeys(KeyInputSimulation.Keys keys) {
         boolean[] flags = resolveConflicts(toKeyArray(keys));

         for (int i = 0; i < 7; i++) {
            if (this.keyStates[i] != flags[i]) {
               this.keyStates[i] = flags[i];
               this.previousKeyStates[i] = flags[i];
               this.keyPressTicks[i] = this.currentTick - 1048576L;
               this.keyLastChangeTimes[i] = this.currentTick - 1048576L;
               this.minDwellTimes[i] = DEFAULT_MIN_HOLD_TICKS[i];
            }
         }
      }

      public KeyInputSimulation.Keys getDwelledKeys(KeyInputSimulation.Keys keys, boolean backwardForwardFlag) {
         return this.applyDwell(keys, backwardForwardFlag, null);
      }
   }

   public record Keys(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
      public static final KeyInputSimulation.Keys INSTANCE = new KeyInputSimulation.Keys(false, false, false, false, false, false, false);
   }

   public static final class SprintGuard {
      private long lastForwardTick;
      private long latestSprintTick = Long.MIN_VALUE;

      public SprintGuard() {
         this.lastForwardTick = Long.MIN_VALUE;
      }

      public KeyInputSimulation.Keys computeGuardedKeys(
         KeyInputSimulation.Dwell dwell,
         long max,
         KeyInputSimulation.Keys keys,
         boolean forwardFlag,
         boolean forwardFlag2,
         boolean forwardFlag3,
         boolean forwardFlag4,
         int forwardValue
      ) { boolean var48_u = false;  boolean var46_u = false;  boolean var44_u = false;  boolean var42_u = false;  boolean var40_u = false;  boolean var38_u = false; 
         if (this.lastForwardTick != max - 1L && forwardFlag4) {
            this.latestSprintTick = Math.max(this.latestSprintTick, max - 1L);
         }

         KeyInputSimulation.Keys keys2 = keys != null ? keys : KeyInputSimulation.Keys.INSTANCE;
         boolean forwardFlag5 = forwardFlag2 && forwardFlag3;
         boolean forwardFlag6 = forwardFlag2 && !forwardFlag4 && this.isWithinSprintWindow(max, forwardValue);
         boolean forwardFlag7 = forwardFlag5 || forwardFlag6;
         if (forwardFlag2 && keys2 instanceof KeyInputSimulation.Keys) {
            KeyInputSimulation.Keys keys3 = keys2;

            try {
               var38_u = keys3.forward();
            } catch (Throwable e) {
               throw new MatchException(e.toString(), e);
            }

            boolean var38_u2 = var38_u;
            keys3 = keys2;

            try {
               var40_u = keys3.back();
            } catch (Throwable e2) {
               throw new MatchException(e2.toString(), e2);
            }

            boolean var40_u2 = var40_u;
            keys3 = keys2;

            try {
               var42_u = keys3.left();
            } catch (Throwable e3) {
               throw new MatchException(e3.toString(), e3);
            }

            boolean var42_u2 = var42_u;
            keys3 = keys2;

            try {
               var44_u = keys3.right();
            } catch (Throwable e4) {
               throw new MatchException(e4.toString(), e4);
            }

            boolean var44_u2 = var44_u;
            keys3 = keys2;

            try {
               var46_u = keys3.jump();
            } catch (Throwable e5) {
               throw new MatchException(e5.toString(), e5);
            }

            boolean var46_u2 = var46_u;
            keys3 = keys2;

            try {
               var48_u = keys3.sneak();
            } catch (Throwable e6) {
               throw new MatchException(e6.toString(), e6);
            }

            boolean var48_u2 = var48_u;
            keys3 = keys2;

            try {
               keys3.sprint();
            } catch (Throwable e7) {
               throw new MatchException(e7.toString(), e7);
            }

            keys2 = new KeyInputSimulation.Keys(var38_u2 && !forwardFlag7, var40_u2, var42_u2, var44_u2, var46_u2, var48_u2, false);
         }

         KeyInputSimulation.Keys flagsTime = dwell.applyDwell(keys2, forwardFlag, new KeyInputSimulation.Keys(forwardFlag7, false, false, false, false, false, forwardFlag2));
         if (flagsTime.forward() && !flagsTime.back() && !forwardFlag4) {
            this.latestSprintTick = max;
         }

         this.lastForwardTick = max;
         return flagsTime;
      }

      public boolean isWithinSprintWindow(long sprintGuardValue, int sprintGuardLimit) {
         return this.latestSprintTick != Long.MIN_VALUE && sprintGuardValue - this.latestSprintTick < sprintGuardLimit;
      }

      public void reset() {
         this.latestSprintTick = Long.MIN_VALUE;
         this.lastForwardTick = Long.MIN_VALUE;
      }
   }
}
