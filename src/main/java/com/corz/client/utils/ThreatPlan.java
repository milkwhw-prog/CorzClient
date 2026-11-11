package com.corz.client.utils;

public final class ThreatPlan {

   public static boolean wasPlayerHitRecently(long threatPlanLimit) {
      return threatPlanLimit >= 0L && threatPlanLimit < 15000L;
   }

   public static ThreatPlan.React decideReaction(ThreatPlan.Input input) {
      if (input == null || !input.threat()) {
         return ThreatPlan.React.NONE;
      } else if (input.rtpAwaiting()) {
         return input.warmup() && input.nearest() >= 12.0 ? ThreatPlan.React.HOLD_STILL : ThreatPlan.React.FLEE;
      } else if (input.rtpReady()) {
         return ThreatPlan.React.RTP_NOW;
      } else {
         return input.logoutOn() && input.nearest() <= 5.0 && input.cooldownLeftMs() > 3000L && !wasPlayerHitRecently(input.sincePlayerHitMs())
            ? ThreatPlan.React.LOGOUT
            : ThreatPlan.React.FLEE;
      }
   }

   private ThreatPlan() {
   }

   public record Input(
      boolean threat, boolean rtpReady, boolean rtpAwaiting, boolean warmup, double nearest, long cooldownLeftMs, boolean logoutOn, long sincePlayerHitMs
   ) {
   }

   public static enum React {
      NONE,
      RTP_NOW,
      FLEE,
      HOLD_STILL,
      LOGOUT;
   }
}
