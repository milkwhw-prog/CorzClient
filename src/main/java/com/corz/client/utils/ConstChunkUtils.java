package com.corz.client.utils;
import com.corz.client.utils.SpawnerSculkUtils;

public final class ConstChunkUtils {
   public static final int CHUNK_UTILS_VERSION = 1;

   public static int getPercentRemaining(int constChunkDivisor, int constChunkValue) {
      if (constChunkDivisor <= 0) {
         return 100;
      } else {
         int max2 = Math.max(0, constChunkDivisor - constChunkValue);
         return (int)Math.round(100.0 * max2 / constChunkDivisor);
      }
   }

   public static boolean passesDetectionThresholds(int constChunkLimit, int constChunkLimit2, int constChunkValue, int constChunkValue2, int constChunkValue3) {
      if (constChunkLimit >= 12 && constChunkLimit2 >= 2 && constChunkLimit * 2 >= constChunkValue) {
         return true;
      } else {
         int constChunkLimit3 = constChunkValue2 + constChunkValue3;
         return constChunkLimit3 >= 512 && constChunkValue2 * 100 >= constChunkLimit3 * 12;
      }
   }

   public static int getPercentShare(int constChunkValue, int constChunkValue2, int constChunkValue3) {
      int constChunkDivisor = constChunkValue + constChunkValue2 + constChunkValue3;
      return constChunkDivisor <= 0 ? 100 : (int)Math.round(100.0 * constChunkValue / constChunkDivisor);
   }

   public static int classifyFlags(int constChunkValue, int spawnerSculkValue, boolean constChunkFlag) {
      if ((constChunkValue & 16) != 0) {
         return 2;
      } else if (SpawnerSculkUtils.isExtendedTargetId(spawnerSculkValue)) {
         return 1;
      } else if ((constChunkValue & 256) != 0 || (constChunkValue & 32) == 0) {
         return 0;
      } else if (!constChunkFlag) {
         return 3;
      } else {
         return (constChunkValue & 64) != 0 ? 5 : 4;
      }
   }

   public static boolean isLowCategoryCode(int constChunkValue) {
      return constChunkValue <= 3;
   }

   private ConstChunkUtils() {
   }
}
