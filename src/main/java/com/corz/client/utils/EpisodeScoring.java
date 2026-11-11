package com.corz.client.utils;

public final class EpisodeScoring {

   public static double computeOutcomeScore(double ownMetric, double opponentMetric, int killCount, int deathCount, boolean won, boolean lost) {
      double rawScore = sanitizeAmount(ownMetric) - 0.8 * sanitizeAmount(opponentMetric) + 8.0 * clampCount(killCount) - 8.0 * clampCount(deathCount) + (won ? 25.0 : 0.0) - (lost ? 25.0 : 0.0);
      return Math.tanh(rawScore / 20.0);
   }

   private static int clampCount(int rawCount) {
      return Math.max(0, Math.min(10000, rawCount));
   }

   private EpisodeScoring() {
   }

   private static double sanitizeAmount(double rawValue) {
      return Double.isNaN(rawValue) ? 0.0 : AmountAmounts.getAmount(rawValue, 0.0, 1000000.0);
   }
}
