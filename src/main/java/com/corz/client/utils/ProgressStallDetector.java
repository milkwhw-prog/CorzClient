package com.corz.client.utils;
import com.corz.client.utils.DeltaHeadroomUtils;

public final class ProgressStallDetector {
   private int lastNodeIndex;
   private int elapsedTicks;
   private ProgressStallDetector.Verdict verdict;
   private DeltaHeadroomUtils.Kind lastStepKind;
   private ProgressStallDetector.Progress lastProgress;
   private int nodeAdvanceCount;
   private int startTick;
   private boolean landedAtLevel;
   private int stallCount;
   private double bestVerticalGap;
   private long lastProgressTime = Long.MIN_VALUE;
   private int lastProgressTick;
   private double bestHorizontalDistance;
   private boolean wasOnGround;

   public double getBestVerticalGap() {
      return this.bestVerticalGap;
   }

   public ProgressStallDetector.Progress getLastProgress() {
      return this.lastProgress;
   }

   public ProgressStallDetector() {
      this.lastNodeIndex = -1;
      this.bestHorizontalDistance = Double.POSITIVE_INFINITY;
      this.bestVerticalGap = Double.POSITIVE_INFINITY;
      this.lastProgress = ProgressStallDetector.Progress.NONE;
      this.verdict = ProgressStallDetector.Verdict.ALIVE;
   }

   public int getElapsedTicks() {
      return this.elapsedTicks;
   }

   public void reset() {
      this.lastProgressTime = Long.MIN_VALUE;
      this.lastNodeIndex = -1;
      this.lastStepKind = null;
      this.verdict = ProgressStallDetector.Verdict.ALIVE;
      this.lastProgress = ProgressStallDetector.Progress.NONE;
   }

   public double getBestHorizontalDistance() {
      return this.bestHorizontalDistance;
   }

   public int getTicksSinceProgress(int aliveStalledValue) {
      return Math.max(0, aliveStalledValue - this.lastProgressTick);
   }

   public boolean hasLandedAtLevel() {
      return this.landedAtLevel;
   }

   public ProgressStallDetector.Verdict evaluateProgress(
      long time,
      int value,
      DeltaHeadroomUtils.Kind stepApproachType,
      int value2,
      double amount,
      double amount2,
      boolean flag,
      boolean stalledAliveFlag
   ) {
      if (time == this.lastProgressTime && value == this.lastNodeIndex && stepApproachType == this.lastStepKind) {
         this.elapsedTicks = value2 - this.startTick;
         ProgressStallDetector.Progress levelNodeType = ProgressStallDetector.Progress.NONE;
         boolean stalledAliveFlag2 = amount2 <= 0.35;
         if (amount2 < this.bestVerticalGap - 0.06) {
            this.bestVerticalGap = amount2;
            levelNodeType = ProgressStallDetector.Progress.VERTICAL;
         }

         if (amount < this.bestHorizontalDistance - 0.06) {
            this.bestHorizontalDistance = amount;
            if (stalledAliveFlag2) {
               levelNodeType = ProgressStallDetector.Progress.HORIZONTAL_AT_LEVEL;
            }
         }

         if (flag && !this.wasOnGround && stalledAliveFlag2 && !this.landedAtLevel) {
            this.landedAtLevel = true;
            levelNodeType = ProgressStallDetector.Progress.LANDED_AT_LEVEL;
         }

         if (stalledAliveFlag) {
            levelNodeType = ProgressStallDetector.Progress.PHASE;
         }

         this.wasOnGround = flag;
         this.lastProgress = levelNodeType;
         if (levelNodeType != ProgressStallDetector.Progress.NONE) {
            this.lastProgressTick = value2;
         }

         int stalledAliveValue = stepApproachType == DeltaHeadroomUtils.Kind.GAP_JUMP ? 40 : 60;
         if (value2 - this.lastProgressTick > stalledAliveValue && this.verdict == ProgressStallDetector.Verdict.ALIVE) {
            this.verdict = ProgressStallDetector.Verdict.STALLED;
            this.stallCount++;
         }

         return this.verdict;
      } else {
         boolean stalledAliveFlag3 = time == this.lastProgressTime && value > this.lastNodeIndex;
         if (stalledAliveFlag3) {
            this.nodeAdvanceCount++;
         }

         this.lastProgressTime = time;
         this.lastNodeIndex = value;
         this.lastStepKind = stepApproachType;
         this.startTick = value2;
         this.lastProgressTick = value2;
         this.elapsedTicks = 0;
         this.bestHorizontalDistance = amount;
         this.bestVerticalGap = amount2;
         this.landedAtLevel = false;
         this.wasOnGround = flag;
         this.verdict = ProgressStallDetector.Verdict.ALIVE;
         this.lastProgress = stalledAliveFlag3 ? ProgressStallDetector.Progress.NODE_ADVANCE : ProgressStallDetector.Progress.NONE;
         return this.verdict;
      }
   }

   public ProgressStallDetector.Verdict getVerdict() {
      return this.verdict;
   }

   public int getNodeAdvanceCount() {
      return this.nodeAdvanceCount;
   }

   public int getStallCount() {
      return this.stallCount;
   }

   public static enum Progress {
      NONE,
      NODE_ADVANCE,
      VERTICAL,
      HORIZONTAL_AT_LEVEL,
      LANDED_AT_LEVEL,
      PHASE;
   }

   public static enum Verdict {
      ALIVE,
      STALLED;
   }
}
