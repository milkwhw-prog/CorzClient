package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class AmountConstTime {
   private int consecutiveHitCount;
   private boolean constActive;
   private long lastSetTimeMs;
   private final boolean[] constFlags;
   public static final int UNUSED_ONE = 1;
   private double estimatedIntervalMs = 12.0;
   private int constValue;
   private final Map<Integer, Long> constPositionMap;
   private final ArrayDeque<AmountConstTime.SpotTrack> arrayDeque;
   private long lastEventTimeMs;
   private double constAmount = 4.0;
   private int constCount;

   public void handleDequeArray(long time, boolean dequeArrayFlag, int dequeArrayValue, double dequeArrayValue2, double groundY, double dequeArrayValue4, float dequeArrayLimit) {
      this.handleFlagsAmount(time, dequeArrayFlag, dequeArrayValue);
      if (dequeArrayLimit >= 5.5 && Double.isFinite(dequeArrayValue2) && Double.isFinite(groundY) && Double.isFinite(dequeArrayValue4)) {
         int floor5 = (int)Math.floor(dequeArrayValue2);
         int dequeArrayValue6 = (int)Math.floor(groundY + 0.001) - 1;
         int floor7 = (int)Math.floor(dequeArrayValue4);
         AmountConstTime.SpotTrack spotTrack = null;

         for (AmountConstTime.SpotTrack spotTrack2 : this.arrayDeque) {
            if (spotTrack2.x == floor5 && spotTrack2.blockY == dequeArrayValue6 && spotTrack2.z == floor7) {
               spotTrack = spotTrack2;
               break;
            }
         }

         if (spotTrack != null) {
            long constBound = time - spotTrack.lastUpdateTimeMs;
            if (constBound > 0L) {
               if (constBound <= 60L) {
                  spotTrack.dequeArrayCount++;
                  spotTrack.avgIntervalMs = spotTrack.dequeArrayCount == 2 ? getAmount(constBound, 1.0, 40.0) : getAmount(spotTrack.avgIntervalMs + 0.3 * (constBound - spotTrack.avgIntervalMs), 1.0, 40.0);
               } else {
                  spotTrack.dequeArrayCount = 1;
                  spotTrack.avgIntervalMs = 6.0;
               }

               spotTrack.lastUpdateTimeMs = time;
               this.arrayDeque.remove(spotTrack);
               this.arrayDeque.addLast(spotTrack);
            }
         } else {
            this.arrayDeque.addLast(new AmountConstTime.SpotTrack(floor5, dequeArrayValue6, floor7, time));

            while (this.arrayDeque.size() > 4) {
               this.arrayDeque.pollFirst();
            }
         }
      }
   }

   public void handleAmountFlag(double constValue, double constBound) {
      if (!this.constActive) {
         if (Double.isFinite(constValue)) {
            this.estimatedIntervalMs = getAmount(constValue, 3.0, 40.0);
         }

         if (Double.isFinite(constBound)) {
            this.constAmount = getAmount(constBound, 1.0, 40.0);
         }
      }
   }

   public double getConstAmount() {
      return this.constAmount;
   }

   public int getMin(long constValue) {
      AmountConstTime.Spot timeAmount = this.getTimeAmount(constValue);
      if (timeAmount == null) {
         return 99;
      } else {
         long maxRound = Math.max(1L, Math.round(timeAmount.interval()));
         long max2 = Math.max(0L, constValue - timeAmount.lastBoom());
         long constBound = max2 < maxRound ? maxRound - max2 : (maxRound - max2 % maxRound) % maxRound;
         return (int)Math.min(99L, constBound);
      }
   }

   public double getAmount() {
      return this.estimatedIntervalMs;
   }

   public boolean isFlag() {
      return this.consecutiveHitCount >= 3 && this.constCount >= 3;
   }

   public long getLastEventTimeMs() {
      return this.lastEventTimeMs;
   }

   public AmountConstTime() {
      this.lastEventTimeMs = Long.MIN_VALUE;
      this.lastSetTimeMs = Long.MIN_VALUE;
      this.constFlags = new boolean[5];
      this.constPositionMap = new LinkedHashMap<>();
      this.arrayDeque = new ArrayDeque<>();
   }

   public void updateAmountTime() {
      this.estimatedIntervalMs = 12.0;
      this.constAmount = 4.0;
      this.lastEventTimeMs = Long.MIN_VALUE;
      this.lastSetTimeMs = Long.MIN_VALUE;
      this.constActive = false;
      Arrays.fill(this.constFlags, false);
      this.constCount = 0;
      this.constValue = 0;
      this.consecutiveHitCount = 0;
      this.constPositionMap.clear();
      this.arrayDeque.clear();
   }

   public void handleFlagsAmount(long time, boolean constFlag, int constValue) {
      if (this.lastEventTimeMs != Long.MIN_VALUE) {
         long constLimit = time - this.lastEventTimeMs;
         if (constLimit > 0L && constLimit <= 80L) {
            this.estimatedIntervalMs = getAmount(this.estimatedIntervalMs + 0.3 * (constLimit - this.estimatedIntervalMs), 3.0, 40.0);
         }
      }

      this.lastEventTimeMs = time;
      this.constActive = true;
      if (constFlag) {
         boolean constFlag2 = Math.abs(constValue) <= 1;
         if (this.constFlags[this.constValue]) {
            this.constCount--;
         }

         this.constFlags[this.constValue] = constFlag2;
         if (constFlag2) {
            this.constCount++;
         }

         this.constValue = (this.constValue + 1) % 5;
         this.consecutiveHitCount++;
      }
   }

   public String getFormat(long constValue) {
      AmountConstTime.Spot timeAmount = this.getTimeAmount(constValue);
      return timeAmount == null
         ? "-"
         : String.format(Locale.ROOT, "%d,%d,%d:%d:%.1f:%d", timeAmount.x(), timeAmount.y(), timeAmount.z(), timeAmount.booms(), timeAmount.interval(), this.getMin(constValue));
   }

   public AmountConstTime.Spot getTimeAmount(long constValue) {
      AmountConstTime.SpotTrack spotTrack = null;

      for (AmountConstTime.SpotTrack spotTrack2 : this.arrayDeque) {
         if (spotTrack2.dequeArrayCount >= 2) {
            long constLimit = constValue - spotTrack2.lastUpdateTimeMs;
            if (constLimit >= 0L
               && constLimit <= Math.max(10L, Math.round(2.0 * spotTrack2.avgIntervalMs))
               && (spotTrack == null || spotTrack2.dequeArrayCount > spotTrack.dequeArrayCount || spotTrack2.dequeArrayCount == spotTrack.dequeArrayCount && spotTrack2.lastUpdateTimeMs >= spotTrack.lastUpdateTimeMs)) {
               spotTrack = spotTrack2;
            }
         }
      }

      return spotTrack == null ? null : new AmountConstTime.Spot(spotTrack.x, spotTrack.blockY, spotTrack.z, spotTrack.dequeArrayCount, spotTrack.lastUpdateTimeMs, spotTrack.avgIntervalMs);
   }

   public int getMinForLong(long constValue) {
      long constBound = Long.MAX_VALUE;
      if (this.lastEventTimeMs != Long.MIN_VALUE && constValue - this.lastEventTimeMs <= Math.round(3.0 * this.estimatedIntervalMs)) {
         constBound = Math.max(0L, this.lastEventTimeMs + Math.round(this.estimatedIntervalMs) - constValue);
      }

      this.constPositionMap.values().removeIf(nowx -> constValue - nowx > 100L);

      for (long constValue2 : this.constPositionMap.values()) {
         long unreachableBound = Math.max(0L, constValue2 + Math.round(this.constAmount) - constValue);
         if (this.lastSetTimeMs != Long.MIN_VALUE && constValue - this.lastSetTimeMs <= 1L) {
            unreachableBound = 0L;
         }

         constBound = Math.min(constBound, unreachableBound);
      }

      return constBound == Long.MAX_VALUE ? 99 : (int)Math.min(99L, constBound);
   }

   public void handle(int constValue, long predicateBlockingValue) {
      this.constPositionMap.put(constValue, predicateBlockingValue);
      if (this.constPositionMap.size() > 32) {
         Iterator iterator = this.constPositionMap.keySet().iterator();
         iterator.next();
         iterator.remove();
      }
   }

   private static double getAmount(double constBound, double constLimit, double goalBound) {
      return constBound < constLimit ? constLimit : Math.min(constBound, goalBound);
   }

   public void setTime(long time) {
      this.lastSetTimeMs = time;
   }

   public void handleAmountFlag(int constValue, long constValue2) {
      Long long2 = this.constPositionMap.remove(constValue);
      if (long2 != null) {
         long constLimit = constValue2 - long2;
         if (constLimit >= 0L && constLimit <= 100L) {
            this.constAmount = getAmount(this.constAmount + 0.3 * (constLimit - this.constAmount), 1.0, 40.0);
            this.constActive = true;
         }
      }
   }

   public record Spot(int x, int y, int z, int booms, long lastBoom, double interval) {
      public double getAmount() {
         return this.y + 1.0;
      }

      public double getY() {
         return this.y + 1.0;
      }

      public double getZ() {
         return this.z + 0.5;
      }

      public double getX() {
         return this.x + 0.5;
      }
   }

   private static final class SpotTrack {
      long lastUpdateTimeMs;
      final int z;
      double avgIntervalMs;
      final int blockY;
      int dequeArrayCount = 1;
      final int x;

      SpotTrack(int spotTrackValue, int value, int spotTrackValue2, long time) {
         this.avgIntervalMs = 6.0;
         this.x = spotTrackValue;
         this.blockY = value;
         this.z = spotTrackValue2;
         this.lastUpdateTimeMs = time;
      }
   }
}
