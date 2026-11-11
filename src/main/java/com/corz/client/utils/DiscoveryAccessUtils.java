package com.corz.client.utils;

public final class DiscoveryAccessUtils {
   private DiscoveryAccessUtils() {
   }

   public static DiscoveryAccessUtils.Decision getAccessDiscovery(
      boolean accessDiscoveryFlag,
      boolean accessDiscoveryFlag2,
      boolean accessDiscoveryFlag3,
      boolean accessDiscoveryFlag4,
      boolean accessDiscoveryFlag5,
      int accessDiscoveryLimit
   ) {
      if (accessDiscoveryFlag5) {
         return new DiscoveryAccessUtils.Decision(DiscoveryAccessUtils.Verdict.REFUSE, DiscoveryAccessUtils.FirstAction.NONE, "access family already disproved");
      } else if (accessDiscoveryFlag) {
         return new DiscoveryAccessUtils.Decision(
            DiscoveryAccessUtils.Verdict.ADMIT, DiscoveryAccessUtils.FirstAction.PLACE_FROM_CURRENT_POSE, "target provable from the current pose"
         );
      } else if (accessDiscoveryFlag2) {
         return new DiscoveryAccessUtils.Decision(
            DiscoveryAccessUtils.Verdict.ADMIT, DiscoveryAccessUtils.FirstAction.WALK_TO_PROVEN_STAND, "the stand-solver accepted a stand"
         );
      } else if (accessDiscoveryFlag4) {
         return new DiscoveryAccessUtils.Decision(
            DiscoveryAccessUtils.Verdict.ADMIT, DiscoveryAccessUtils.FirstAction.ENTER_PROVEN_RUN, "the run lane entry is proven"
         );
      } else if (accessDiscoveryFlag3) {
         return new DiscoveryAccessUtils.Decision(
            DiscoveryAccessUtils.Verdict.ADMIT,
            DiscoveryAccessUtils.FirstAction.EXECUTE_PROVEN_ACCESS_CONSTRUCTION,
            "the access construction's first placement is executable now"
         );
      } else {
         return accessDiscoveryLimit > 0
            ? new DiscoveryAccessUtils.Decision(
               DiscoveryAccessUtils.Verdict.DISCOVER_ACCESS,
               DiscoveryAccessUtils.FirstAction.NONE,
               "no concrete first action \u2014 bounded access discovery first"
            )
            : new DiscoveryAccessUtils.Decision(
               DiscoveryAccessUtils.Verdict.REFUSE, DiscoveryAccessUtils.FirstAction.NONE, "no concrete first action and the discovery budget is spent"
            );
      }
   }

   public record Decision(DiscoveryAccessUtils.Verdict verdict, DiscoveryAccessUtils.FirstAction first, String why) {
      public boolean isRefuseVerdict() {
         return this.verdict == DiscoveryAccessUtils.Verdict.REFUSE;
      }

      public boolean isAdmitVerdict() {
         return this.verdict == DiscoveryAccessUtils.Verdict.ADMIT;
      }

      public String formatVerdictWhy() {
         return this.verdict + "/" + this.first + " (" + this.why + ")";
      }
   }

   public static enum FirstAction {
      PLACE_FROM_CURRENT_POSE,
      WALK_TO_PROVEN_STAND,
      EXECUTE_PROVEN_ACCESS_CONSTRUCTION,
      ENTER_PROVEN_RUN,
      NONE;
   }

   public static enum Verdict {
      ADMIT,
      DISCOVER_ACCESS,
      REFUSE;
   }
}
