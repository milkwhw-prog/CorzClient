package com.corz.client.utils;

public final class BreakTimingClassifier {
   public static int findFirstUsableTick(
      double[] noneValues, double[] noneValues2, int noneBound, int noneValue, int noneValue2, int noneValue3, int topDistanceValue, int topDistanceBound, double[] noneValues3
   ) {
      if (noneValues != null && noneValues2 != null && noneValues3 != null) {
         int noneLimit = Math.min(topDistanceBound - 1, Math.min(noneValues.length - 1, Math.min(noneValues2.length - 1, noneValues3.length - 1)));

         for (int i = Math.max(0, noneBound); i <= noneLimit; i++) {
            double noneValue6 = i + 1 < noneValues.length ? noneValues[i + 1] : noneValues[i];
            long noneLimit2 = (long)noneValue2 + i;
            int noneValue7 = noneLimit2 > 2147483647L ? Integer.MAX_VALUE : (int)noneLimit2;
            BreakTimingClassifier.Kind noneLift = classifyStrikeTick(
               noneValues[i], noneValues2[i], i >= noneValue, noneValue7, noneValue3, topDistanceValue, topDistanceBound - i, noneValue6, noneValues3[i]
            );
            if (noneLift != BreakTimingClassifier.Kind.NONE) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private BreakTimingClassifier() {
   }

   public static BreakTimingClassifier.Kind classifyStrikeTick(
      double noneLiftLimit,
      double noneLiftValue,
      boolean noneLiftFlag,
      int noneLiftLimit2,
      int noneLiftValue2,
      int topDistanceValue,
      int noneLiftLimit3,
      double noneLiftValue4,
      double noneLiftLimit4
   ) {
      boolean noneLiftFlag2 = noneLiftLimit2 >= noneLiftValue2 && topDistanceValue < noneLiftLimit3;
      if (!noneLiftFlag2) {
         return BreakTimingClassifier.Kind.NONE;
      } else if (noneLiftFlag && noneLiftLimit >= 1.5 && noneLiftLimit <= 3.5 && noneLiftValue <= 0.15 && noneLiftLimit4 >= 1.0) {
         return BreakTimingClassifier.Kind.DAMAGE;
      } else {
         return !(noneLiftLimit >= 1.0) || !(noneLiftValue4 < 1.0) && 1 + topDistanceValue < noneLiftLimit3 - 1 ? BreakTimingClassifier.Kind.NONE : BreakTimingClassifier.Kind.LIFT;
      }
   }

   public static enum Kind {
      DAMAGE,
      LIFT,
      NONE;
   }
}
