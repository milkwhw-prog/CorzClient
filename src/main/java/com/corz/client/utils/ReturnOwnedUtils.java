package com.corz.client.utils;

import java.util.Locale;

public final class ReturnOwnedUtils {
   private ReturnOwnedUtils() {
   }

   public static ReturnOwnedUtils.Result getReturnOwned(
      int returnOwnedValue, int returnOwnedLimit, boolean returnOwnedFlag, boolean returnOwnedFlag2, boolean returnOwnedFlag3
   ) {
      if (returnOwnedValue < returnOwnedLimit && !returnOwnedFlag3 && !returnOwnedFlag2) {
         return new ReturnOwnedUtils.Result(
            ReturnOwnedUtils.Verdict.REJECT,
            returnOwnedValue,
            String.format(Locale.ROOT, "one-way drop into a pocket of %d < %d with no return and no owned escape", returnOwnedValue, returnOwnedLimit)
         );
      } else if (returnOwnedFlag || returnOwnedFlag2) {
         return new ReturnOwnedUtils.Result(
            ReturnOwnedUtils.Verdict.SAFE,
            returnOwnedValue,
            returnOwnedFlag ? "work continues inside the post component" : "a return path to the pre-action component survives"
         );
      } else {
         return returnOwnedFlag3
            ? new ReturnOwnedUtils.Result(
               ReturnOwnedUtils.Verdict.NEEDS_OWNED_ESCAPE,
               returnOwnedValue,
               "no continuation and no return \u2014 an owned block bounds the pocket and must stay mineable"
            )
            : new ReturnOwnedUtils.Result(
               ReturnOwnedUtils.Verdict.REJECT, returnOwnedValue, "the action would leave no continuation, no return and no owned escape"
            );
      }
   }

   public static ReturnOwnedUtils.Result getPostVerdict(
      int postVerdictValue, int returnOwnedLimit, boolean postVerdictFlag, boolean postVerdictFlag2, boolean postVerdictFlag3
   ) {
      ReturnOwnedUtils.Result returnOwned = getReturnOwned(postVerdictValue, returnOwnedLimit, postVerdictFlag, postVerdictFlag2, postVerdictFlag3);
      return new ReturnOwnedUtils.Result(returnOwned.verdict(), returnOwned.postComponentSize(), "entry \u2014 " + returnOwned.why());
   }

   public record Result(ReturnOwnedUtils.Verdict verdict, int postComponentSize, String why) {
      public boolean isRejectVerdict() {
         return this.verdict == ReturnOwnedUtils.Verdict.REJECT;
      }

      public String getFormat() {
         return String.format(Locale.ROOT, "%s post=%d (%s)", this.verdict, this.postComponentSize, this.why);
      }

      public boolean isVerdict() {
         return this.verdict != ReturnOwnedUtils.Verdict.REJECT;
      }
   }

   public static enum Verdict {
      SAFE,
      NEEDS_OWNED_ESCAPE,
      REJECT;
   }
}
