package com.corz.client.utils;

import java.util.Locale;
import com.corz.client.utils.ScopeCaused;

public final class ChosenRunnerUtils {

   public static boolean canSurfaceAfford(int chosenRunnerBound, int rocketsBuyBound, ChosenRunnerUtils.Surface surface) {
      int chosenRunnerValue = Math.max(0, chosenRunnerBound) + Math.max(0, rocketsBuyBound);
      if (chosenRunnerValue <= 0) {
         return false;
      } else {
         return surface == null
            ? true
            : new ChosenRunnerUtils.Surface(0L, chosenRunnerValue, 0.0, 0, 0, 0, 0, 0).getExpectedConfirms() >= surface.getExpectedConfirms();
      }
   }

   public static ChosenRunnerUtils.Surface getTransitionsVertical(ChosenRunnerUtils.Surface transitionsVertical, ChosenRunnerUtils.Surface surface) {
      if (transitionsVertical == null) {
         return surface;
      } else if (surface == null) {
         return transitionsVertical;
      } else {
         double expectedConfirms = transitionsVertical.getExpectedConfirms();
         double expectedConfirms2 = surface.getExpectedConfirms();
         if (Math.abs(expectedConfirms - expectedConfirms2) > 1.0E-9) {
            return expectedConfirms > expectedConfirms2 ? transitionsVertical : surface;
         } else if (transitionsVertical.verticalTransitions() != surface.verticalTransitions()) {
            return transitionsVertical.verticalTransitions() < surface.verticalTransitions() ? transitionsVertical : surface;
         } else {
            return transitionsVertical.travelBlocks() <= surface.travelBlocks() ? transitionsVertical : surface;
         }
      }
   }

   public static boolean hasEnoughBudget(int chosenRunnerValue, int n2) {
      return chosenRunnerValue != 0 && n2 != 0 && Integer.signum(chosenRunnerValue) != Integer.signum(n2);
   }

   public static String formatRunnerChosen(ChosenRunnerUtils.Surface surface, ChosenRunnerUtils.Surface surface2, boolean runnerChosenFlag) {
      return "chosen{"
         + (surface == null ? "none" : surface.getFormat())
         + "} runnerUp{"
         + (surface2 == null ? "none" : surface2.getFormat())
         + "} decision="
         + (runnerChosenFlag ? "STAY" : "MOVE");
   }

   private ChosenRunnerUtils() {
   }

   public record Surface(
      long standCell,
      int expectedConfirms,
      double travelBlocks,
      int jumpTransitions,
      int verticalTransitions,
      int verticalReversals,
      int tempBlocks,
      int unclearedTemp
   ) {
      public String getFormat() {
         return String.format(
            Locale.ROOT,
            "stand=%s confirms=%d travel=%.1f jumps=%d vertical=%d reversals=%d temp=%d uncleared=%d cost=%.1f score=%.3f",
            ScopeCaused.formatText(this.standCell),
            this.expectedConfirms,
            this.travelBlocks,
            this.jumpTransitions,
            this.verticalTransitions,
            this.verticalReversals,
            this.tempBlocks,
            this.unclearedTemp,
            this.getTransitionsVertical(),
            this.getExpectedConfirms()
         );
      }

      public double getTransitionsVertical() {
         return this.travelBlocks * 1.0
            + this.jumpTransitions * 3.0
            + this.verticalTransitions * 2.0
            + this.verticalReversals * 6.0
            + this.tempBlocks * 4.0
            + this.unclearedTemp * 8.0;
      }

      public double getExpectedConfirms() {
         return this.expectedConfirms <= 0 ? 0.0 : this.expectedConfirms / (1.0 + this.getTransitionsVertical());
      }
   }
}
