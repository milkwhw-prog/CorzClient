package com.corz.client.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class FingerprintPlacements {
   public long unknownCount;
   public long placementTickCount;
   public long sideRunCount;
   public long directionalTime;
   public long directionalCount;
   public long directionalValue;
   private final Map<FingerprintPlacements.Key, FingerprintPlacements.Reject> orDefaultRejectMap = new HashMap<>();

   public String formatPlacements() {
      return String.format(
         Locale.ROOT,
         "directionalRunsProposed=%d directionalRunsAdmitted=%d directionalRunsRejectedPreflight=%d directionalRunsZeroActionAbort=%d directionalRunPlacements=%d directionalRunTicks=%d directionalRunTicksPerPlacement=%.1f",
         this.unknownCount,
         this.directionalCount,
         this.placementTickCount,
         this.sideRunCount,
         this.directionalTime,
         this.directionalValue,
         this.getDirectionalAverageTime()
      );
   }

   public void recordReject(FingerprintPlacements.Key botCrystalText, FingerprintPlacements.Reject reject) {
      if (this.orDefaultRejectMap.size() > 512) {
         this.orDefaultRejectMap.clear();
      }

      this.orDefaultRejectMap.put(botCrystalText, reject);
      this.placementTickCount++;
   }

   public double getDirectionalAverageTime() {
      return this.directionalTime == 0L ? this.directionalValue : (double)this.directionalValue / this.directionalTime;
   }

   public FingerprintPlacements.Reject getOrDefault(FingerprintPlacements.Key Module) {
      return this.orDefaultRejectMap.getOrDefault(Module, FingerprintPlacements.Reject.NONE);
   }

   public int getRejectCount() {
      return this.orDefaultRejectMap.size();
   }

   public static boolean canFlagPlacement(int fingerprintPlacementsValue, int fingerprintPlacementsValue2, FingerprintPlacements.Reject reject, int fingerprintPlacementsLimit) {
      return fingerprintPlacementsValue == 0 && fingerprintPlacementsValue2 == 0 && canPredictionSolved(reject) && fingerprintPlacementsLimit >= 6;
   }

   public boolean hasRejectKey(FingerprintPlacements.Key obsidianIdleValue) {
      return this.orDefaultRejectMap.containsKey(obsidianIdleValue);
   }

   public static boolean canPredictionSolved(FingerprintPlacements.Reject reject) {
      return reject == FingerprintPlacements.Reject.NO_SUPPORT_FROM_LANE
         || reject == FingerprintPlacements.Reject.FACING_UNSOLVABLE
         || reject == FingerprintPlacements.Reject.BODY_POSE_INVALID
         || reject == FingerprintPlacements.Reject.STATE_PREDICTION_MISMATCH
         || reject == FingerprintPlacements.Reject.RAY_OFF_SOLVED_FACE;
   }

   public static String formatClicksPlacements(String clicksPlacementsText, int clicksPlacementsValue, int clicksPlacementsValue2, int clicksPlacementsValue3, String clicksPlacementsText2, long clicksPlacementsValue4) {
      return String.format(
         Locale.ROOT,
         "kind=%s ticksWithoutAction=%d placements=%d clicks=%d blockingReason=%s geometryFingerprint=%d",
         clicksPlacementsText,
         clicksPlacementsValue,
         clicksPlacementsValue2,
         clicksPlacementsValue3,
         clicksPlacementsText2,
         clicksPlacementsValue4
      );
   }

   public void updateDirectionalTime() {
      this.orDefaultRejectMap.clear();
      this.unknownCount = this.directionalCount = this.placementTickCount = this.sideRunCount = this.directionalTime = this.directionalValue = 0L;
   }

   public static boolean canFlagPlacementAlternate(int fingerprintPlacementsValue, int fingerprintPlacementsValue2, FingerprintPlacements.Reject reject, int fingerprintPlacementsLimit) {
      return fingerprintPlacementsValue == 0 && fingerprintPlacementsValue2 == 0 && canPredictionSolved(reject) && fingerprintPlacementsLimit >= 6;
   }

   public record Key(String kind, long cluster, long geometryFingerprint, long supportFingerprint) {
   }

   public record Preflight(
      String kind,
      int members,
      boolean firstExecutable,
      FingerprintPlacements.Reject firstRejectReason,
      boolean dependencyChainValid,
      int predictedFirstActionTicks
   ) {
      public String formatPreflight() {
         return String.format(
            Locale.ROOT,
            "kind=%s members=%d firstExecutable=%b firstRejectReason=%s dependencyChainValid=%b predictedFirstActionTicks=%d result=%s",
            this.kind,
            this.members,
            this.firstExecutable,
            this.firstRejectReason,
            this.dependencyChainValid,
            this.predictedFirstActionTicks,
            this.isExecutableReject() ? "ADMITTED" : "REJECTED"
         );
      }

      public boolean isExecutableReject() {
         return this.firstExecutable && this.firstRejectReason == FingerprintPlacements.Reject.NONE;
      }
   }

   public static enum Reject {
      NONE,
      NO_SUPPORT_FROM_LANE,
      FACING_UNSOLVABLE,
      NO_RAY_FROM_RUN_POSE,
      WALKING_TO_WINDOW,
      RAY_OFF_SOLVED_FACE,
      BODY_POSE_INVALID,
      STATE_PREDICTION_MISMATCH,
      NO_PLAUSIBLE_CONTINUATION,
      KNOWN_BAD_GEOMETRY;
   }
}
