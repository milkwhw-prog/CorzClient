package com.corz.client.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public final class ParkMerge {
   private long lastHandledTick;
   private int parkCount;
   private final HashMap<Long, Integer> orDefaultIntegerMap = new HashMap<>();

   public boolean canFlag(long parkMergeValue) {
      return this.getOrDefault(parkMergeValue) >= 3;
   }

   public int getValue() {
      return this.parkCount;
   }

   public static long getTime(long[] parkMergeValues) {
      long[] clone = (long[])parkMergeValues.clone();
      Arrays.sort(clone);
      long parkMergeValue = 1469598103934665603L;

      for (long parkMergeValue2 : clone) {
         parkMergeValue = (parkMergeValue ^ parkMergeValue2) * 1099511628211L;
      }

      return parkMergeValue ^ clone.length;
   }

   public void handle(int parkMergeValue) {
      this.handleTime(parkMergeValue);
   }

   public int getOrDefault(long orDefaultValue) {
      return this.orDefaultIntegerMap.getOrDefault(orDefaultValue, 0);
   }

   public void handleTime(long time) {
      if (time != this.lastHandledTick) {
         this.lastHandledTick = time;
         this.orDefaultIntegerMap.clear();
      }
   }

   public ParkMerge.Level getPark(ParkMerge.Level level) {
      this.parkCount++;
      ParkMerge.Level[] levels = ParkMerge.Level.values();
      return level.ordinal() + 1 < levels.length ? levels[level.ordinal() + 1] : ParkMerge.Level.PARK;
   }

   public int getMerge(long parkMergeValue) {
      return this.orDefaultIntegerMap.merge(parkMergeValue, 1, Integer::sum);
   }

   public ParkMerge() {
      this.lastHandledTick = Long.MIN_VALUE;
   }

   public static final class DispatchBudget {
      private int churn;
      private long lastRefundTick;
      private int refunds;
      private int exhausted;
      private final HashSet<Long> refundsPositionSet;
      private int parkedHits;
      private long refundsCount;
      private final LinkedHashMap<Long, ParkMerge.DispatchBudget.Slot> churnRefundsSlotMap = new LinkedHashMap<Long, ParkMerge.DispatchBudget.Slot>(64, 0.75F, true) {
         @Override
         protected boolean removeEldestEntry(Entry<Long, ParkMerge.DispatchBudget.Slot> entry) {
            return this.size() > 4096;
         }
      };
      private long refundsValue;

      public boolean canMax(long dispatchBudgetValue, long dispatchBudgetValue2, int dispatchBudgetValue3) {
         ParkMerge.DispatchBudget.Slot slot = this.churnRefundsSlotMap.get(getTime(dispatchBudgetValue, dispatchBudgetValue2));
         return slot != null && dispatchBudgetValue3 < slot.blockedUntil || this.canFlag(dispatchBudgetValue2, dispatchBudgetValue3) || this.canMax(dispatchBudgetValue, dispatchBudgetValue3);
      }

      public String formatChurnRefunds() {
         return "keys="
            + this.churnRefundsSlotMap.size()
            + " exhausted="
            + this.exhausted
            + " parkedHits="
            + this.parkedHits
            + " refunds="
            + this.refunds
            + " churn="
            + this.churn;
      }

      private ParkMerge.DispatchBudget.Slot getTime(long dispatchBudgetValue) {
         ParkMerge.DispatchBudget.Slot slot = this.churnRefundsSlotMap.get(dispatchBudgetValue);
         if (slot == null) {
            slot = new ParkMerge.DispatchBudget.Slot();
            slot.refundsEpoch = this.refundsCount;
            slot.exhaustedValue = this.lastRefundTick;
            this.churnRefundsSlotMap.put(dispatchBudgetValue, slot);
         }

         if (slot.refundsEpoch != this.refundsCount) {
            slot.refundsEpoch = this.refundsCount;
            slot.parkedHitCount = 0;
            if (slot.parkedHitsPositionSet != null) {
               slot.parkedHitsPositionSet.clear();
            }
         }

         return slot;
      }

      public boolean canMax(long dispatchBudgetValue, int dispatchBudgetValue2) {
         ParkMerge.DispatchBudget.Slot slot = this.churnRefundsSlotMap.get(dispatchBudgetValue);
         return slot != null && dispatchBudgetValue2 < slot.blockedUntil;
      }

      public int getMax(long dispatchBudgetValue) {
         ParkMerge.DispatchBudget.Slot slot = this.churnRefundsSlotMap.get(getTime(6861051372358106689L, dispatchBudgetValue));
         return slot == null ? Integer.MIN_VALUE : slot.blockedUntil;
      }

      public static long getTime(String dispatchBudgetText, String churnRefundsText) {
         long dispatchBudgetValue = 6761240433388849681L;
         long dispatchBudgetValue2 = -3750763034362895579L;
         String dispatchBudgetText3 = dispatchBudgetText + '|' + churnRefundsText;
         long dispatchBudgetValue3 = dispatchBudgetValue ^ (0L ^ dispatchBudgetValue) & -4294967296L;

         while ((int)(dispatchBudgetValue3 >>> 32) < dispatchBudgetText3.length()) {
            long dispatchBudgetValue4 = dispatchBudgetValue3 ^ (dispatchBudgetText3.charAt((int)(dispatchBudgetValue3 >>> 32)) ^ dispatchBudgetValue3) & 4294967295L;
            if (((int)dispatchBudgetValue4 < 48 || (int)dispatchBudgetValue4 > 57) && (int)dispatchBudgetValue4 != 45) {
               dispatchBudgetValue2 = (dispatchBudgetValue2 ^ (int)dispatchBudgetValue4) * 1099511628211L;
            }

            dispatchBudgetValue3 = dispatchBudgetValue4 + 4294967296L;
         }

         return dispatchBudgetValue2;
      }

      public void updateTime() {
         this.churnRefundsSlotMap.clear();
         this.refundsPositionSet.clear();
         this.lastRefundTick = 0L;
         this.refundsValue = Long.MIN_VALUE;
         this.refundsCount++;
      }

      public int getValue(long dispatchBudgetValue, long l2) {
         ParkMerge.DispatchBudget.Slot slot = this.churnRefundsSlotMap.get(getTime(dispatchBudgetValue, l2));
         return slot == null ? 0 : slot.exhaustedCount;
      }

      public ParkMerge.DispatchBudget.Verdict getParkedHits(long parkedHitsValue, long l2, int parkedHitsLimit, int exhaustedValue) {
         ParkMerge.DispatchBudget.Slot slot = this.getTime(getTime(parkedHitsValue, l2));
         if (exhaustedValue < slot.blockedUntil) {
            this.parkedHits++;
            return ParkMerge.DispatchBudget.Verdict.PARKED;
         } else if (++slot.parkedHitCount < parkedHitsLimit) {
            return ParkMerge.DispatchBudget.Verdict.OK;
         } else {
            this.handleExhaustedTime(slot, exhaustedValue);
            ParkMerge.DispatchBudget.Slot slot2 = this.getTime(getTime(6861051372358106689L, l2));
            slot2.blockedUntil = Math.max(slot2.blockedUntil, slot.blockedUntil);
            return ParkMerge.DispatchBudget.Verdict.EXHAUSTED;
         }
      }

      public int getValue(long dispatchBudgetValue) {
         ParkMerge.DispatchBudget.Slot slot = this.churnRefundsSlotMap.get(dispatchBudgetValue);
         return slot == null ? 0 : slot.exhaustedCount;
      }

      private void handleExhaustedTime(ParkMerge.DispatchBudget.Slot slot, int exhaustedValue) {
         if (this.lastRefundTick - slot.exhaustedValue >= 64L) {
            slot.exhaustedCount = 0;
         }

         slot.exhaustedValue = this.lastRefundTick;
         slot.exhaustedCount++;
         slot.blockedUntil = exhaustedValue + getMin(slot.exhaustedCount);
         this.exhausted++;
      }

      public ParkMerge.DispatchBudget.Verdict getParkedHitsForLongInt(long parkedHitsValue, long l2, int parkedHitsLimit, int exhaustedValue) {
         ParkMerge.DispatchBudget.Slot slot = this.getTime(parkedHitsValue);
         if (exhaustedValue < slot.blockedUntil) {
            this.parkedHits++;
            return ParkMerge.DispatchBudget.Verdict.PARKED;
         } else {
            if (slot.parkedHitsPositionSet == null) {
               slot.parkedHitsPositionSet = new HashSet<>();
            }

            if (slot.parkedHitsPositionSet.size() < parkedHitsLimit) {
               slot.parkedHitsPositionSet.add(l2);
            }

            if (slot.parkedHitsPositionSet.size() < parkedHitsLimit) {
               return ParkMerge.DispatchBudget.Verdict.OK;
            } else {
               this.handleExhaustedTime(slot, exhaustedValue);
               return ParkMerge.DispatchBudget.Verdict.EXHAUSTED;
            }
         }
      }

      public boolean canRefundsTime(long time, long blockPos) {
         if (time != this.lastRefundTick) {
            this.lastRefundTick = time;
            this.refundsValue = blockPos;
            this.refundsPositionSet.clear();
            this.refundsPositionSet.add(blockPos);
            this.refundsCount++;
            this.refunds++;
            return true;
         } else if (blockPos == this.refundsValue) {
            return false;
         } else {
            this.refundsValue = blockPos;
            if (this.refundsPositionSet.size() >= 4096) {
               this.refundsPositionSet.clear();
            }

            if (!this.refundsPositionSet.add(blockPos)) {
               this.churn++;
               return false;
            } else {
               this.refundsCount++;
               this.refunds++;
               return true;
            }
         }
      }

      public static long getTime(long dispatchBudgetValue, long dispatchBudgetValue2) {
         return (dispatchBudgetValue ^ dispatchBudgetValue2 * -7046029254386353131L) * 1099511628211L;
      }

      public DispatchBudget() {
         this.refundsPositionSet = new HashSet<>();
         this.refundsValue = Long.MIN_VALUE;
         this.refundsCount = 1L;
      }

      public static int getMin(int dispatchBudgetBound) {
         return Math.min(6400, 200 << Math.min(Math.max(dispatchBudgetBound, 1) - 1, 5));
      }

      public boolean canFlag(long dispatchBudgetValue, int dispatchBudgetValue2) {
         return dispatchBudgetValue2 < this.getMax(dispatchBudgetValue);
      }

      private static final class Slot {
         long refundsEpoch;
         HashSet<Long> parkedHitsPositionSet;
         int parkedHitCount;
         int blockedUntil = Integer.MIN_VALUE;
         long exhaustedValue;
         int exhaustedCount;
      }

      public static enum Verdict {
         OK,
         EXHAUSTED,
         PARKED;
      }
   }

   public static enum Level {
      LOCAL_WORK,
      ACCESS_COLUMN,
      ISLAND_SUPPORT,
      COMPLETION_PLAN,
      COMPLETION_ALTERNATE,
      TEMP_LEDGE_BRIDGE,
      SERVICE_SCAFFOLD,
      PARK;
   }
}
