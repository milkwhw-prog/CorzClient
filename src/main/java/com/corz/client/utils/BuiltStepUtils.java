package com.corz.client.utils;

import java.util.Locale;
import com.corz.client.utils.ExhaustedLostUtils;

public final class BuiltStepUtils {
   public static final String EVERY_STEP_BUILT = "every step is built";

   private BuiltStepUtils() {
   }

   public static ExhaustedLostUtils.Yield getAccessLostYield(String accessLostText) {
      String normalizedText = accessLostText == null ? "" : accessLostText.toLowerCase(Locale.ROOT);
      if (normalizedText.contains("every step is built")) {
         return ExhaustedLostUtils.Yield.EXHAUSTED;
      } else if (normalizedText.contains("access lost")) {
         return ExhaustedLostUtils.Yield.ACCESS_LOST;
      } else if (normalizedText.contains("temporary block") || normalizedText.contains("temp")) {
         return ExhaustedLostUtils.Yield.TEMP_FAILED;
      } else if (normalizedText.contains("access")) {
         return ExhaustedLostUtils.Yield.ACCESS_FAILED;
      } else if (normalizedText.contains("material") || normalizedText.contains("hotbar")) {
         return ExhaustedLostUtils.Yield.MATERIAL_LOST;
      } else if (normalizedText.contains("pose") || normalizedText.contains("stand")) {
         return ExhaustedLostUtils.Yield.POSE_INVALIDATED;
      } else if (normalizedText.contains("support") || normalizedText.contains("dependen") || normalizedText.contains("changed")) {
         return ExhaustedLostUtils.Yield.DEPENDENCY_CHANGED;
      } else {
         return !normalizedText.contains("supersed") && !normalizedText.contains("better") ? ExhaustedLostUtils.Yield.ABORTED : ExhaustedLostUtils.Yield.SUPERSEDED;
      }
   }
}
