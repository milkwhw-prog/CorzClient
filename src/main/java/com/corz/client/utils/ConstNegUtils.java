package com.corz.client.utils;

public final class ConstNegUtils {

   private ConstNegUtils() {
   }

   public static double getFlag(ConstNegUtils.Candidate candidate) {
      double constNegTotal = 0.0;
      if (candidate.isBlocked) {
         constNegTotal += 50.0;
      }

      if (candidate.isExposed) {
         constNegTotal += -30.0;
      }

      if (candidate.isSafe) {
         constNegTotal += 40.0;
      }

      if (candidate.candidateActive) {
         constNegTotal += 20.0;
      }

      double constNegTotal2 = constNegTotal + Math.max(0, candidate.priorityScore) * 8.0;
      if (candidate.isReachable) {
         constNegTotal2 += 60.0;
      }

      if (candidate.isPreferred) {
         constNegTotal2 += -80.0;
      }

      if (candidate.isDuplicate) {
         constNegTotal2 += 10.0;
      }

      if (candidate.hasSupport) {
         constNegTotal2 += -25.0;
      }

      double constNegValue = constNegTotal2 + Math.max(0, candidate.nearbyThreatCount) * -15.0;
      double constNegValue2 = constNegValue + Math.max(0, candidate.weakThreatCount) * -3.0;
      double constNegValue3 = constNegValue2 + Math.max(0, candidate.candidateValue) * -5.0;
      return constNegValue3 - Math.max(0, candidate.penaltyCount);
   }

   public static final class Candidate {
      public boolean candidateActive;
      public int priorityScore;
      public boolean hasSupport;
      public boolean isExposed;
      public boolean isReachable;
      public boolean isSafe;
      public int candidateValue;
      public int penaltyCount;
      public boolean isPreferred;
      public int nearbyThreatCount;
      public boolean isBlocked;
      public boolean isDuplicate;
      public int weakThreatCount;
   }
}
