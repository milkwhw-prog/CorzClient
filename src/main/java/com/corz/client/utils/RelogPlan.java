package com.corz.client.utils;

import java.util.Locale;

public final class RelogPlan {

   public static long waitSecondsToMillis(int relogPlanBound) {
      return Math.max(0, Math.min(10, relogPlanBound)) * 1000L;
   }

   public static boolean isRejoinedSameServer(String relogPlanText, String relogPlanText2, long relogPlanLimit) {
      return relogPlanLimit >= 0L && relogPlanLimit <= 120000L && addressesMatch(relogPlanText, relogPlanText2);
   }

   private RelogPlan() {
   }

   public static RelogPlan.Verdict checkRelogAllowed(boolean realmSingleplayerFlag, boolean realmSingleplayerFlag2, String realmSingleplayerText, boolean realmSingleplayerFlag3, long realmSingleplayerLimit, long realmSingleplayerLimit2) {
      if (!realmSingleplayerFlag) {
         return RelogPlan.Verdict.NOT_IN_WORLD;
      } else if (realmSingleplayerFlag2) {
         return RelogPlan.Verdict.SINGLEPLAYER;
      } else if (realmSingleplayerText == null || realmSingleplayerText.isBlank()) {
         return RelogPlan.Verdict.NO_SERVER;
      } else if (realmSingleplayerFlag3) {
         return RelogPlan.Verdict.REALM;
      } else {
         return realmSingleplayerLimit2 > 0L && realmSingleplayerLimit >= realmSingleplayerLimit2 && realmSingleplayerLimit - realmSingleplayerLimit2 < 10000L ? RelogPlan.Verdict.TOO_SOON : RelogPlan.Verdict.GO;
      }
   }

   public static boolean addressesMatch(String rootText, String rootText2) {
      return rootText != null && rootText2 != null ? rootText.trim().toLowerCase(Locale.ROOT).equals(rootText2.trim().toLowerCase(Locale.ROOT)) : false;
   }

   public static String getVerdictMessage(RelogPlan.Verdict verdict) {
      return switch (verdict) {
         case SINGLEPLAYER -> "Relog only works on servers, not in singleplayer.";
         case NO_SERVER -> "Couldn't find the server address - nothing happened.";
         case REALM -> "Relog does not work on Realms - nothing happened.";
         case TOO_SOON -> "Wait a few seconds between relogs.";
         default -> null;
      };
   }

   public static enum Verdict {
      NOT_IN_WORLD,
      SINGLEPLAYER,
      NO_SERVER,
      REALM,
      TOO_SOON,
      GO;
   }
}
