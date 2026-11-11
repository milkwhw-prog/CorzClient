package com.corz.client.utils;

public final class FinishingPressUtils {

   public static FinishingPressUtils.Phase getPressPhase(long nonePressLimit, long nonePressValue) {
      if (nonePressValue == Long.MIN_VALUE) {
         return FinishingPressUtils.Phase.NONE;
      } else if (nonePressLimit == nonePressValue) {
         return FinishingPressUtils.Phase.RELEASE;
      } else {
         return nonePressLimit > nonePressValue ? FinishingPressUtils.Phase.PRESS : FinishingPressUtils.Phase.NONE;
      }
   }

   public static long getPressLeadTime(long finishingPressValue) {
      return finishingPressValue == Long.MIN_VALUE ? Long.MIN_VALUE : finishingPressValue - 3L;
   }

   private FinishingPressUtils() {
   }

   public static enum Phase {
      NONE,
      RELEASE,
      PRESS;
   }
}
