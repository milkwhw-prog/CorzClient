package com.corz.client.utils;

import java.util.Locale;

public final class ProgressDeclaredUtils {
   public static ProgressDeclaredUtils.Disposition getRetainRetire(boolean retainRetireFlag, int retainRetireValue, int retainRetireValue2) {
      return retainRetireFlag && retainRetireValue < 2 * retainRetireValue2 ? ProgressDeclaredUtils.Disposition.RETAIN : ProgressDeclaredUtils.Disposition.RETIRE;
   }

   public static boolean canProgressSupersed(String progressSupersedText) {
      if (progressSupersedText == null) {
         return false;
      } else {
         String lowerCase = progressSupersedText.toLowerCase(Locale.ROOT);
         return !lowerCase.contains("supersed") && !lowerCase.contains("lease released") && !lowerCase.contains("timeout")
            ? lowerCase.contains("no_declared_progress")
               || lowerCase.contains("does_not_reach")
               || lowerCase.contains("no stand")
               || lowerCase.contains("unreachable")
               || lowerCase.contains("stalled")
               || lowerCase.contains("no_progress")
            : false;
      }
   }

   public static ProgressDeclaredUtils.Disposition getRetainRetire(boolean retainRetireFlag, boolean retainRetireFlag2, int retainRetireValue, int retainRetireValue2) {
      if (retainRetireFlag) {
         return ProgressDeclaredUtils.Disposition.TRANSFER;
      } else {
         return retainRetireFlag2 && retainRetireValue < 2 * retainRetireValue2 ? ProgressDeclaredUtils.Disposition.RETAIN : ProgressDeclaredUtils.Disposition.RETIRE;
      }
   }

   private ProgressDeclaredUtils() {
   }

   public static enum Disposition {
      TRANSFER,
      RETAIN,
      RETIRE;
   }
}
