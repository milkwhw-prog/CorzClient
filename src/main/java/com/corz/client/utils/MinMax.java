package com.corz.client.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class MinMax {
   private final Set<Long> positionSet;
   private int boundSum;
   private int lowestBound;
   private int continuedCount;
   private int minBound;
   private int continuedValue;
   private final Set<Long> continuedPositionSet;
   private int continuedBaseTotal;
   private long stepSumTotal;
   private int highestBound;
   private int continuedRunCount;
   private int continuedStepCount;
   private long continuedSequenceId;
   private long continuedStartKey;
   private final Set<Long> secondaryPositionSet;
   private int continuedUpdateCount;
   private MinMax.Transition continuedMergedType;
   private int maxBound;
   private int continuedMergeCount;
   private boolean continuedActive;
   private int continuedSkipCount;
   private final Set<Long> continuedVisitedSet;
   private int maxLimitBound;
   private int continuedResetCount;
   private long nextSequenceId = 1L;
   private int minLimitBound;
   private long continuedWeightSum;

   public boolean isFlag() {
      return this.continuedActive;
   }

   public MinMax.Summary getTimeContinued(int continuedValue) {
      if (!this.continuedActive) {
         return null;
      } else {
         this.continuedActive = false;
         this.continuedMergedType = MinMax.Transition.ENDED;
         this.continuedRunCount++;
         this.stepSumTotal = this.stepSumTotal + this.continuedStepCount;
         this.continuedWeightSum = this.continuedWeightSum + this.boundSum;
         this.continuedBaseTotal = this.continuedBaseTotal + this.continuedCount;
         return new MinMax.Summary(
            this.continuedSequenceId,
            this.continuedStartKey,
            this.continuedStepCount,
            this.boundSum,
            this.continuedPositionSet.size(),
            this.continuedResetCount,
            this.continuedSkipCount,
            this.continuedUpdateCount,
            continuedValue - this.continuedValue,
            this.continuedMergeCount,
            this.continuedCount,
            this.continuedVisitedSet.size()
         );
      }
   }

   public long getContinuedStartKey() {
      return this.continuedStartKey;
   }

   public int getContinuedStepCount() {
      return this.continuedStepCount;
   }

   public MinMax.Summary getDataForInt(int n, int n2, int n3, int n4, int n5) {
      return this.getMinMax(n, n2, n3, null, n4, 0L, false, n5);
   }

   public void handleFlag(long longValue) {
      if (this.continuedActive && longValue != 0L) {
         this.positionSet.add(longValue);
      }
   }

   public int getContinuedCount() {
      return this.continuedCount;
   }

   public void updateFlag() {
      if (this.continuedActive) {
         this.continuedUpdateCount++;
      }
   }

   public long getContinuedSequenceId() {
      return this.continuedSequenceId;
   }

   public void updateFlagContinuedActive() {
      if (this.continuedActive) {
         this.continuedSkipCount++;
      }
   }

   public MinMax.Summary getMinMax(int n, int n2, int n3, List<Long> list, int bound, long longValue, boolean flag, int value) {
      MinMax.Summary minMax = null;
      if (this.continuedActive && !this.canMinMax(n, n2, n3, list, longValue)) {
         minMax = this.getTimeContinued(value);
      }

      if (!this.continuedActive) {
         this.continuedActive = true;
         this.continuedSequenceId = this.nextSequenceId++;
         this.continuedStartKey = getTime(n, n2, n3);
         this.continuedValue = value;
         this.continuedStepCount = 0;
         this.boundSum = 0;
         this.continuedResetCount = 0;
         this.continuedSkipCount = 0;
         this.continuedUpdateCount = 0;
         this.continuedMergeCount = 0;
         this.continuedCount = 0;
         this.continuedPositionSet.clear();
         this.secondaryPositionSet.clear();
         this.continuedVisitedSet.clear();
         this.positionSet.clear();
         this.minBound = this.lowestBound = this.minLimitBound = Integer.MAX_VALUE;
         this.highestBound = this.maxBound = this.maxLimitBound = Integer.MIN_VALUE;
         this.continuedMergedType = MinMax.Transition.STARTED;
      } else {
         this.continuedMergedType = flag ? MinMax.Transition.MERGED : MinMax.Transition.CONTINUED;
         if (flag) {
            this.continuedCount++;
         }
      }

      this.boundSum = this.boundSum + Math.max(0, bound);
      this.continuedMergeCount++;
      this.secondaryPositionSet.add(ProofSupportUtils.getTime(n, n2, n3));
      if (longValue != 0L) {
         this.positionSet.add(longValue);
      }

      if (list != null) {
         for (long proofSupportValue : list) {
            if (this.continuedVisitedSet.add(proofSupportValue)) {
               int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
               int spotBound = ProofSupportUtils.getValue(proofSupportValue);
               int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);
               this.minBound = Math.min(this.minBound, valueInt);
               this.lowestBound = Math.min(this.lowestBound, spotBound);
               this.minLimitBound = Math.min(this.minLimitBound, valueForLong);
               this.highestBound = Math.max(this.highestBound, valueInt);
               this.maxBound = Math.max(this.maxBound, spotBound);
               this.maxLimitBound = Math.max(this.maxLimitBound, valueForLong);
            }
         }
      }

      return minMax;
   }

   public int getFlag() {
      return this.continuedBaseTotal + (this.continuedActive ? this.continuedCount : 0);
   }

   public void handleFlagForLong(long longValue) {
      if (this.continuedActive) {
         this.continuedStepCount++;
         this.continuedPositionSet.add(longValue);
      }
   }

   public int getContinuedMergeCount() {
      return this.continuedMergeCount;
   }

   public int getContinuedRunCount() {
      return this.continuedRunCount;
   }

   public double getTime() {
      return this.continuedWeightSum == 0L ? 0.0 : (double)this.stepSumTotal / this.continuedWeightSum;
   }

   public double getTimeContinuedRunCount() {
      return this.continuedRunCount == 0 ? 0.0 : (double)this.stepSumTotal / this.continuedRunCount;
   }

   public void updateFlag3() {
      if (this.continuedActive) {
         this.continuedResetCount++;
      }
   }

   public static long getTime(int n, int n2, int n3) {
      return ProofSupportUtils.getTime(n >> 3, n2 >> 2, n3 >> 3);
   }

   public int getValue() {
      return this.continuedVisitedSet.size();
   }

   public MinMax.Summary getDataForIntLong(int n, int n2, int n3, int n4, long longValue, int n5) {
      MinMax.Summary timeContinued = this.getTimeContinued(n5);
      this.getMinMax(n, n2, n3, null, n4, longValue, false, n5);
      return timeContinued;
   }

   public MinMax.Transition getContinuedMergedType() {
      return this.continuedMergedType;
   }

   public boolean canMinMax(int n, int n2, int n3, List<Long> list, long longValue) {
      if (!this.continuedActive) {
         return false;
      } else if (longValue != 0L && this.positionSet.contains(longValue)) {
         return true;
      } else {
         for (long longValue2 : this.secondaryPositionSet) {
            if (canFlag(longValue2, n, n2, n3)) {
               return true;
            }
         }

         for (long longValue3 : this.continuedPositionSet) {
            if (canFlag(longValue3, n, n2, n3)) {
               return true;
            }
         }

         if (list != null && !this.continuedVisitedSet.isEmpty()) {
            for (long proofSupportValue : list) {
               int limit = ProofSupportUtils.getValueInt(proofSupportValue);
               int limit2 = ProofSupportUtils.getValue(proofSupportValue);
               int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);
               if (limit >= this.minBound - 3
                  && limit <= this.highestBound + 3
                  && limit2 >= this.lowestBound - 3
                  && limit2 <= this.maxBound + 3
                  && valueForLong >= this.minLimitBound - 3
                  && valueForLong <= this.maxLimitBound + 3) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static boolean canFlag(long longValue, int n, int n2, int n3) {
      return Math.abs(ProofSupportUtils.getValueInt(longValue) - n) <= 10
         && Math.abs(ProofSupportUtils.getValueForLong(longValue) - n3) <= 10
         && Math.abs(ProofSupportUtils.getValue(longValue) - n2) <= 4;
   }

   public MinMax() {
      this.continuedStartKey = Long.MIN_VALUE;
      this.continuedPositionSet = new HashSet<>();
      this.secondaryPositionSet = new HashSet<>();
      this.continuedVisitedSet = new HashSet<>();
      this.positionSet = new HashSet<>();
      this.continuedMergedType = MinMax.Transition.ENDED;
   }

   public record Summary(
      long id,
      long region,
      int placed,
      int expected,
      int stands,
      int routes,
      int infraReused,
      int cleanups,
      int durationTicks,
      int plans,
      int merges,
      int members
   ) {
      public String getFormat() {
         return String.format(
            Locale.ROOT,
            "id=%d region=%d placed=%d expected=%d stands=%d routes=%d infraReused=%d cleanups=%d plans=%d merges=%d members=%d duration=%ds",
            this.id,
            this.region,
            this.placed,
            this.expected,
            this.stands,
            this.routes,
            this.infraReused,
            this.cleanups,
            this.plans,
            this.merges,
            this.members,
            this.durationTicks / 20
         );
      }
   }

   public static enum Transition {
      STARTED,
      CONTINUED,
      MERGED,
      ENDED;
   }
}
