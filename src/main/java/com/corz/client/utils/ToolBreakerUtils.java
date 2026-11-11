package com.corz.client.utils;

public final class ToolBreakerUtils {

   private ToolBreakerUtils() {
   }

   public static int selectBestToolIndex(float[] toolBreakerValues, boolean[] toolBreakerFlags, int toolBreakerLimit) {
      int min2 = Math.min(toolBreakerValues.length, toolBreakerFlags.length);
      if (min2 == 0) {
         return toolBreakerLimit;
      } else {
         int index = toolBreakerLimit >= 0 && toolBreakerLimit < min2 ? toolBreakerLimit : 0;

         for (int i = 0; i < min2; i++) {
            if (i != index && isBetterCandidate(toolBreakerValues[i], toolBreakerFlags[i], toolBreakerValues[index], toolBreakerFlags[index])) {
               index = i;
            }
         }

         return index;
      }
   }

   private static boolean isBetterCandidate(float toolBreakerLimit, boolean toolBreakerFlag, float toolBreakerValue, boolean toolBreakerFlag2) {
      if (toolBreakerLimit > toolBreakerValue + 1.0E-4F) {
         return true;
      } else {
         return toolBreakerLimit < toolBreakerValue - 1.0E-4F ? false : toolBreakerFlag && !toolBreakerFlag2;
      }
   }
}
