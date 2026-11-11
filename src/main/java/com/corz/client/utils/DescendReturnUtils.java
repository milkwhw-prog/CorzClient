package com.corz.client.utils;

import java.util.Locale;
import java.util.function.LongPredicate;

public final class DescendReturnUtils {

   public static DescendReturnUtils.Verdict getOwnReachable(DescendReturnUtils.Situation situation) {
      if (situation.usefulReachable() > 0) {
         return DescendReturnUtils.Verdict.STAY;
      } else if (situation.workElsewhere() <= 0) {
         return DescendReturnUtils.Verdict.NOT_ISOLATED;
      } else if (situation.onOwnTemp()) {
         return DescendReturnUtils.Verdict.DESCEND_OWN_PILLAR;
      } else {
         return situation.anchorReachable() ? DescendReturnUtils.Verdict.RETURN_TO_ANCHOR : DescendReturnUtils.Verdict.NO_PLANNED_EXIT;
      }
   }

   public static int countMatching(Iterable<Long> iterable, LongPredicate longPredicate) {
      int descendReturnCounter = 0;

      for (long descendReturnValue : iterable) {
         int valueInt = ProofSupportUtils.getValueInt(descendReturnValue);
         int descendReturnValue3 = ProofSupportUtils.getValue(descendReturnValue);
         int valueForLong = ProofSupportUtils.getValueForLong(descendReturnValue);
         if (isWithinDescendLimitFiltered(valueInt, descendReturnValue3, valueForLong, longPredicate)) {
            descendReturnCounter++;
         }
      }

      return descendReturnCounter;
   }

   private DescendReturnUtils() {
   }

   public static boolean canDescendReturn(DescendReturnUtils.Verdict verdict) {
      return verdict == DescendReturnUtils.Verdict.DESCEND_OWN_PILLAR
         || verdict == DescendReturnUtils.Verdict.RETURN_TO_ANCHOR
         || verdict == DescendReturnUtils.Verdict.NO_PLANNED_EXIT;
   }

   public static boolean isWithinDescendLimit(int descendReturnValue, int descendReturnLimit, int j) {
      return Math.abs(descendReturnValue) <= 3 && Math.abs(j) <= 3 && descendReturnLimit >= -2 && descendReturnLimit <= 3;
   }

   public static boolean isDescendReturnStrict(DescendReturnUtils.Verdict verdict) {
      return verdict == DescendReturnUtils.Verdict.DESCEND_OWN_PILLAR || verdict == DescendReturnUtils.Verdict.RETURN_TO_ANCHOR;
   }

   public static boolean isWithinDescendLimitFiltered(int descendReturnValue, int descendReturnValue2, int valueForLong, LongPredicate longPredicate) {
      for (int i = -3; i <= 2; i++) {
         for (int j = -3; j <= 3; j++) {
            for (int k = -3; k <= 3; k++) {
               if (longPredicate.test(ProofSupportUtils.getTime(descendReturnValue + j, descendReturnValue2 + i, valueForLong + k))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public record Situation(int usefulReachable, boolean onOwnTemp, boolean anchorReachable, int workElsewhere, int componentSize) {
      public String getFormat() {
         return String.format(
            Locale.ROOT,
            "usefulReachable=%d onOwnTemp=%s anchorReachable=%s workElsewhere=%d component=%d",
            this.usefulReachable,
            this.onOwnTemp,
            this.anchorReachable,
            this.workElsewhere,
            this.componentSize
         );
      }
   }

   public static enum Verdict {
      STAY,
      DESCEND_OWN_PILLAR,
      RETURN_TO_ANCHOR,
      NO_PLANNED_EXIT,
      NOT_ISOLATED;
   }
}
