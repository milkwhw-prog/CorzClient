package com.corz.client.utils;
import com.corz.client.utils.IncompleteStand;

public final class TransitionSourceUtils {
   private static final TransitionSourceUtils.Decision DECISION_A = new TransitionSourceUtils.Decision(
      TransitionSourceUtils.Verdict.APPLY, "certificate-owned airborne transition keeps all of its own inputs"
   );
   private static final TransitionSourceUtils.Decision DECISION_B = new TransitionSourceUtils.Decision(
      TransitionSourceUtils.Verdict.APPLY, "source pose holds and the body is settled"
   );
   private static final TransitionSourceUtils.Decision DECISION_C = new TransitionSourceUtils.Decision(
      TransitionSourceUtils.Verdict.APPLY, "settle bound reached \u2014 handing the input over rather than stalling"
   );
   private static final TransitionSourceUtils.Decision DECISION_E = new TransitionSourceUtils.Decision(
      TransitionSourceUtils.Verdict.ABORT_STALE_SOURCE, "the live feet left this transition's source cell"
   );
   private static final TransitionSourceUtils.Decision DECISION_D = new TransitionSourceUtils.Decision(
      TransitionSourceUtils.Verdict.WAIT_FOR_SETTLE, "a new route's first input may not be written while the body is still resolving the previous transition"
   );

   private TransitionSourceUtils() {
   }

   public static TransitionSourceUtils.Decision decideTransition(
      boolean transitionSourceFlag,
      boolean incompleteStandFlag,
      boolean transitionSourceFlag3,
      double transitionSourceValue,
      boolean transitionSourceFlag4,
      boolean transitionSourceFlag5,
      int transitionSourceLimit,
      int transitionSourceLimit2
   ) {
      if (transitionSourceFlag5) {
         return DECISION_A;
      } else if (!transitionSourceFlag4) {
         return DECISION_E;
      } else if (!transitionSourceFlag || transitionSourceLimit2 <= 0 || transitionSourceLimit >= transitionSourceLimit2 || IncompleteStand.isGroundedAndSettled(incompleteStandFlag, transitionSourceValue) && !transitionSourceFlag3
         )
       {
         return !transitionSourceFlag || transitionSourceLimit2 <= 0 || transitionSourceLimit < transitionSourceLimit2 || IncompleteStand.isGroundedAndSettled(incompleteStandFlag, transitionSourceValue) && !transitionSourceFlag3
            ? DECISION_B
            : DECISION_C;
      } else {
         return DECISION_D;
      }
   }

   public static TransitionSourceUtils.Decision getDataForFlagDouble(
      boolean transitionSourceFlag, boolean incompleteStandFlag, boolean transitionSourceFlag3, double transitionSourceValue, boolean transitionSourceFlag4, boolean transitionSourceFlag5, int transitionSourceLimit
   ) {
      return decideTransition(transitionSourceFlag, incompleteStandFlag, transitionSourceFlag3, transitionSourceValue, transitionSourceFlag4, transitionSourceFlag5, transitionSourceLimit, 4);
   }

   public record Decision(TransitionSourceUtils.Verdict verdict, String why) {
      public boolean isVerdictStale() {
         return this.verdict == TransitionSourceUtils.Verdict.ABORT_STALE_SOURCE;
      }

      public boolean isApplyVerdict() {
         return this.verdict == TransitionSourceUtils.Verdict.APPLY;
      }
   }

   public static enum Verdict {
      APPLY,
      WAIT_FOR_SETTLE,
      ABORT_STALE_SOURCE;
   }
}
