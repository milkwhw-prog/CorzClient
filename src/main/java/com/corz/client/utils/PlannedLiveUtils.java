package com.corz.client.utils;

import java.util.Locale;

public final class PlannedLiveUtils {
   public static double getAmount(long plannedLiveValue, boolean plannedLiveFlag, long proofSupportValue) {
      double plannedLiveValue3 = ProofSupportUtils.getValueInt(plannedLiveValue) + 0.5;
      double plannedLiveValue4 = ProofSupportUtils.getValue(plannedLiveValue) + getAmount(plannedLiveFlag);
      double plannedLiveValue5 = ProofSupportUtils.getValueForLong(plannedLiveValue) + 0.5;
      double plannedLiveFactor = ProofSupportUtils.getValueInt(proofSupportValue) + 0.5 - plannedLiveValue3;
      double plannedLiveFactor2 = ProofSupportUtils.getValue(proofSupportValue) + 0.5 - plannedLiveValue4;
      double plannedLiveFactor3 = ProofSupportUtils.getValueForLong(proofSupportValue) + 0.5 - plannedLiveValue5;
      return Math.sqrt(plannedLiveFactor * plannedLiveFactor + plannedLiveFactor2 * plannedLiveFactor2 + plannedLiveFactor3 * plannedLiveFactor3);
   }

   public static double getAmount(boolean plannedLiveFlag) {
      return plannedLiveFlag ? 1.27 : 1.62;
   }

   public static String formatLivePlanned(PlannedLiveUtils.Certificate certificate, long livePlannedValue, PlannedLiveUtils.Verdict verdict) {
      return "plannedStand="
         + (certificate == null ? "none" : ProofSupportUtils.formatText(certificate.stand()))
         + " liveFeet="
         + ProofSupportUtils.formatText(livePlannedValue)
         + " target="
         + (certificate == null ? "none" : ProofSupportUtils.formatText(certificate.target()))
         + " eye="
         + (certificate != null && certificate.sneaking() ? "crouched" : "standing")
         + " plannedDistance="
         + String.format(Locale.ROOT, "%.2f", verdict.plannedDistance())
         + " liveDistance="
         + String.format(Locale.ROOT, "%.2f", verdict.liveDistance())
         + " result="
         + verdict.result();
   }

   public static PlannedLiveUtils.Verdict getTarget(PlannedLiveUtils.Certificate certificate, long targetValue, boolean targetFlag, double targetValue2) {
      return getStandCertified(certificate, targetValue, certificate == null ? Double.NaN : getAmount(targetValue, targetFlag, certificate.target()), targetValue2);
   }

   public static PlannedLiveUtils.Verdict getStandCertified(PlannedLiveUtils.Certificate certificate, long standCertifiedValue, double standCertifiedLimit, double standCertifiedValue2) {
      if (certificate == null) {
         return new PlannedLiveUtils.Verdict(PlannedLiveUtils.Result.NO_CERTIFICATE, Double.NaN, standCertifiedLimit);
      } else {
         double sneakingStand = certificate.getSneakingStand();
         if (standCertifiedValue != certificate.stand()) {
            return new PlannedLiveUtils.Verdict(PlannedLiveUtils.Result.STAND_MISMATCH, sneakingStand, standCertifiedLimit);
         } else {
            return standCertifiedLimit > standCertifiedValue2
               ? new PlannedLiveUtils.Verdict(PlannedLiveUtils.Result.OUT_OF_REACH_AT_PLANNED_STAND, sneakingStand, standCertifiedLimit)
               : new PlannedLiveUtils.Verdict(PlannedLiveUtils.Result.CERTIFIED, sneakingStand, standCertifiedLimit);
         }
      }
   }

   private PlannedLiveUtils() {
   }

   public record Certificate(long target, long stand, boolean sneaking, long revision) {
      public double getSneakingStand() {
         return PlannedLiveUtils.getAmount(this.stand, this.sneaking, this.target);
      }
   }

   public static enum Result {
      CERTIFIED,
      STAND_MISMATCH,
      OUT_OF_REACH_AT_PLANNED_STAND,
      NO_CERTIFICATE;
   }

   public record Verdict(PlannedLiveUtils.Result result, double plannedDistance, double liveDistance) {
      public boolean isCertified() {
         return this.result == PlannedLiveUtils.Result.CERTIFIED;
      }
   }
}
