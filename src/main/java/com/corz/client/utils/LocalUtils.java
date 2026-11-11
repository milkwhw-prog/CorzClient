package com.corz.client.utils;

import java.util.List;
import java.util.Locale;
import com.corz.client.utils.MovementMissionData;

public final class LocalUtils {

   public static double getAmount(int localValue, int localValue2, int localValue3) {
      return localValue / (1.0 + localValue2 + localValue3 * 6.0);
   }

   public static boolean canLocal(int localValue, int localValue2, int localValue3, boolean localFlag, boolean localFlag2) {
      List list = localFlag2 ? List.of(new LocalUtils.Alternative("LOCAL", 1, 1.0, 0L)) : List.of();
      return !getRefuseRoiData(localValue, localValue2, localValue3, localFlag, 0, list).go();
   }

   public static LocalUtils.Decision getRefuseRoiData(
      int refuseRoiValue,
      int localValue,
      int refuseRoiValue3,
      boolean refuseRoiFlag,
      int betterAlternativeLimit,
      List<LocalUtils.Alternative> list,
      long a,
      LocalUtils.DependencyProof dependencyProof
   ) {
      return getBetterAlternative(refuseRoiValue, localValue, refuseRoiValue3, refuseRoiFlag, betterAlternativeLimit, list, a, dependencyProof, true);
   }

   private LocalUtils() {
   }

   public static LocalUtils.Decision getRefuseRoiData(
      int refuseRoiValue, int localValue, int refuseRoiValue3, boolean refuseRoiFlag, int betterAlternativeLimit, List<LocalUtils.Alternative> list
   ) {
      return getRefuseRoiData(refuseRoiValue, localValue, refuseRoiValue3, refuseRoiFlag, betterAlternativeLimit, list, 0L, null);
   }

   public static LocalUtils.Decision getBetterAlternative(
      int betterAlternativeValue,
      int localValue,
      int betterAlternativeValue3,
      boolean betterAlternativeFlag,
      int betterAlternativeLimit,
      List<LocalUtils.Alternative> list,
      long a,
      LocalUtils.DependencyProof dependencyProof,
      boolean betterAlternativeFlag2
   ) {
      double betterAlternativeLimit2 = getAmount(betterAlternativeValue, localValue, betterAlternativeValue3);
      LocalUtils.Alternative alternative = null;
      if (list != null) {
         for (LocalUtils.Alternative alternative2 : list) {
            if (alternative2.expected() > 0 && (alternative == null || alternative2.getExpectedCost() > alternative.getExpectedCost())) {
               alternative = alternative2;
            }
         }
      }

      if (dependencyProof != null && dependencyProof.canEvidenceAlternative(a, alternative)) {
         return new LocalUtils.Decision(true, LocalUtils.Reason.HARD_PREREQUISITE, betterAlternativeLimit2, alternative);
      } else if (alternative != null && alternative.getExpectedCost() > betterAlternativeLimit2 * 1.5) {
         return new LocalUtils.Decision(false, LocalUtils.Reason.BETTER_ALTERNATIVE, betterAlternativeLimit2, alternative);
      } else if (!betterAlternativeFlag2 && alternative == null && betterAlternativeValue < getValue(localValue)) {
         return new LocalUtils.Decision(false, LocalUtils.Reason.UNPROVEN_ABSENCE, betterAlternativeLimit2, null);
      } else if (localValue <= 12) {
         return new LocalUtils.Decision(true, LocalUtils.Reason.SHORT_HOP, betterAlternativeLimit2, alternative);
      } else if (betterAlternativeLimit2 >= 0.05) {
         return new LocalUtils.Decision(true, LocalUtils.Reason.ROI_OK, betterAlternativeLimit2, alternative);
      } else if (betterAlternativeFlag) {
         return new LocalUtils.Decision(true, LocalUtils.Reason.BOTTLENECK, betterAlternativeLimit2, alternative);
      } else if (betterAlternativeLimit >= 3) {
         return new LocalUtils.Decision(true, LocalUtils.Reason.DEPENDENCY_UNLOCK, betterAlternativeLimit2, alternative);
      } else if (alternative == null) {
         return new LocalUtils.Decision(true, LocalUtils.Reason.ONLY_PRODUCTIVE_REGION, betterAlternativeLimit2, null);
      } else {
         return alternative.getExpectedCost() < betterAlternativeLimit2 * 1.5
            ? new LocalUtils.Decision(true, LocalUtils.Reason.NO_BETTER_ALTERNATIVE, betterAlternativeLimit2, alternative)
            : new LocalUtils.Decision(false, LocalUtils.Reason.BETTER_ALTERNATIVE, betterAlternativeLimit2, alternative);
      }
   }

   public static boolean canFlag(int localValue, int j, int localValue3) {
      return Math.max(Math.abs(localValue), Math.abs(localValue3)) <= 8 && Math.abs(j) <= 2;
   }

   public static int getValue(int localValue) {
      if (localValue <= 5) {
         return 1;
      } else if (localValue <= 12) {
         return 3;
      } else {
         return localValue <= 24 ? 6 : 10;
      }
   }

   public record Alternative(
      String kind,
      int expected,
      double cost,
      long representative,
      String alternativeOwner,
      long alternativeMissionId,
      long alternativeAssetId,
      long alternativeRegion
   ) {
      public boolean canAlternativeAsset(MovementMissionData intent) {
         return this.kind.equals("ACCESS_ASSET")
            && this.alternativeAssetId != 0L
            && this.alternativeMissionId != 0L
            && this.alternativeOwner.equals(intent.owner().name())
            && this.alternativeMissionId == intent.missionId();
      }

      public String formatAlternativeRoot() {
         return this.kind
            + "(expected="
            + this.expected
            + " cost="
            + String.format(Locale.ROOT, "%.0f", this.cost)
            + " roi="
            + String.format(Locale.ROOT, "%.3f", this.getExpectedCost())
            + " alternativeMissionId="
            + this.alternativeMissionId
            + " alternativeAssetId="
            + this.alternativeAssetId
            + " alternativeOwner="
            + this.alternativeOwner
            + " alternativeRegion="
            + this.alternativeRegion
            + ")";
      }

      public double getExpectedCost() {
         return this.expected <= 0 ? 0.0 : this.expected / (1.0 + Math.max(0.0, this.cost));
      }

      public Alternative(String representative, int expected, double kind, long cost) {
         this(representative, expected, kind, cost, "UNASSIGNED", 0L, 0L, 0L);
      }
   }

   public record Decision(boolean go, LocalUtils.Reason reason, double roi, LocalUtils.Alternative best) {
      public String getAppend() {
         return (this.go ? "GO" : "REFUSE")
            + " reason="
            + this.reason
            + " roi="
            + String.format(Locale.ROOT, "%.3f", this.roi)
            + (this.best == null ? "" : " alt=" + this.best.formatAlternativeRoot());
      }
   }

   public record DependencyProof(long prerequisite, String alternativeKind, long alternative, String evidence) {
      boolean canEvidenceAlternative(long a, LocalUtils.Alternative target) {
         return target != null
            && this.prerequisite == a
            && this.alternative == target.representative()
            && target.kind().equals(this.alternativeKind)
            && this.evidence != null
            && !this.evidence.isBlank();
      }
   }

   public static enum Reason {
      SHORT_HOP,
      ROI_OK,
      BOTTLENECK,
      DEPENDENCY_UNLOCK,
      HARD_PREREQUISITE,
      ONLY_PRODUCTIVE_REGION,
      NO_BETTER_ALTERNATIVE,
      BETTER_ALTERNATIVE,
      UNPROVEN_ABSENCE;
   }
}
