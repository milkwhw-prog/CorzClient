package com.corz.client.utils;

import java.util.Locale;

public final class CostGainUtils {

   private CostGainUtils() {
   }

   public static String formatCostContinue(
      CostGainUtils.Decision decision, CostGainUtils.Current current, CostGainUtils.Alternative alternative, double costContinueValue, double costContinueValue2
   ) {
      return String.format(
         Locale.ROOT,
         "currentMission=%d remainingExpected=%d continueCost=%.0f alternative=%s(%d) switchCost=%.0f netGain=%.0f decision=%s",
         current == null ? 0L : current.missionId(),
         current == null ? 0 : current.remainingExpected(),
         current == null ? 0.0 : current.continueCostTicks(),
         alternative == null ? "NONE" : alternative.kind(),
         alternative == null ? 0L : alternative.target(),
         costContinueValue,
         costContinueValue2,
         decision
      );
   }

   public static CostGainUtils.Verdict getStayExpected(CostGainUtils.Current current, CostGainUtils.Alternative alternative, double stayExpectedBound) {
      double maxPerPlacementTicks = Math.max(0.0, stayExpectedBound);
      double nodesMaterial = getNodesMaterial(current, alternative);
      int stayExpectedFactor = alternative == null ? 0 : Math.max(0, alternative.expected());
      double stayExpectedValue = stayExpectedFactor * maxPerPlacementTicks;
      double stayExpectedLimit = stayExpectedFactor <= 0 ? 0.0 : stayExpectedFactor / Math.max(1.0, nodesMaterial + stayExpectedValue);
      if (current != null && current.remainingExpected() > 0) {
         double stayExpectedDivisor = current.remainingExpected() / Math.max(1.0, current.continueCostTicks());
         if (stayExpectedFactor <= 0) {
            return buildSwitchVerdict(CostGainUtils.Decision.STAY, current, alternative, stayExpectedDivisor, 0.0, nodesMaterial, 0.0);
         } else {
            double stayExpectedLimit2 = stayExpectedFactor / stayExpectedDivisor - (nodesMaterial + stayExpectedValue);
            CostGainUtils.Decision decision = stayExpectedLimit2 > 40.0 && stayExpectedLimit > stayExpectedDivisor * 1.5 ? CostGainUtils.Decision.SWITCH : CostGainUtils.Decision.STAY;
            return buildSwitchVerdict(decision, current, alternative, stayExpectedDivisor, stayExpectedLimit, nodesMaterial, stayExpectedLimit2);
         }
      } else {
         return buildSwitchVerdict(CostGainUtils.Decision.NO_MISSION, current, alternative, 0.0, stayExpectedLimit, nodesMaterial, 0.0);
      }
   }

   public static double getNodesMaterial(CostGainUtils.Current current, CostGainUtils.Alternative alternative) {
      if (alternative == null) {
         return 0.0;
      } else {
         double nodesMaterial = Math.max(0.0, alternative.travelNodes()) * 5.0 + Math.max(0.0, alternative.rotationDegrees()) * 0.08;
         if (current != null && alternative.material() != current.heldMaterial()) {
            nodesMaterial += 25.0;
         }

         if (current != null && alternative.requiresReturn()) {
            nodesMaterial += Math.max(0.0, current.investedNodes()) * 5.0 * 1.0;
         }

         return nodesMaterial;
      }
   }

   private static CostGainUtils.Verdict buildSwitchVerdict(
      CostGainUtils.Decision decision, CostGainUtils.Current current, CostGainUtils.Alternative alternative, double costGainValue, double stayExpectedLimit, double costContinueValue, double costGainValue4
   ) {
      return new CostGainUtils.Verdict(decision, costGainValue, stayExpectedLimit, costContinueValue, costGainValue4, formatCostContinue(decision, current, alternative, costContinueValue, costGainValue4));
   }

   public record Alternative(String kind, long target, int expected, double travelNodes, double rotationDegrees, long material, boolean requiresReturn) {
   }

   public record Current(long missionId, int remainingExpected, double continueCostTicks, double investedNodes, double investedDegrees, long heldMaterial) {
   }

   public static enum Decision {
      STAY,
      SWITCH,
      NO_MISSION;
   }

   public record Verdict(CostGainUtils.Decision decision, double currentRate, double alternativeRate, double switchCostTicks, double netGainTicks, String line) {
   }
}
