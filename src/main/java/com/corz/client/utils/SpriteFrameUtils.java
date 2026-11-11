package com.corz.client.utils;

public final class SpriteFrameUtils {
   public static final int[][] seedQuadsInts = new int[][]{{0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}};
   public static final int UNUSED_ONE = 1;
   private static final int[][][] spriteFrameInts = new int[][][]{
      {{0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, 0, 1}},
      {{0, 1, 0}, {0, 1, 1}, {1, 1, 1}, {1, 1, 0}},
      {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}},
      {{0, 0, 1}, {1, 0, 1}, {1, 1, 1}, {0, 1, 1}},
      {{0, 0, 0}, {0, 0, 1}, {0, 1, 1}, {0, 1, 0}},
      {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}}
   };

   public static boolean canFlag(int spriteFrameValue, float[] spriteFrameValues) {
      return switch (spriteFrameValue) {
         case 0 -> spriteFrameValues[1] <= 0.0F;
         case 1 -> spriteFrameValues[4] >= 1.0F;
         case 2 -> spriteFrameValues[2] <= 0.0F;
         case 3 -> spriteFrameValues[5] >= 1.0F;
         case 4 -> spriteFrameValues[0] <= 0.0F;
         default -> spriteFrameValues[3] >= 1.0F;
      };
   }

   public static boolean canFlag(float[] spriteFrameValues, float[] spriteFrameValues2, float[] spriteFrameValues3, float[] spriteFrameValues4, float spriteFrameFactor, float spriteFrameFactor2, float spriteFrameFactor3) {
      float spriteFrameFactor4 = spriteFrameValues3[0] - spriteFrameValues[0];
      float spriteFrameFactor5 = spriteFrameValues3[1] - spriteFrameValues[1];
      float spriteFrameFactor6 = spriteFrameValues3[2] - spriteFrameValues[2];
      float spriteFrameFactor7 = spriteFrameValues4[0] - spriteFrameValues2[0];
      float spriteFrameFactor8 = spriteFrameValues4[1] - spriteFrameValues2[1];
      float spriteFrameFactor9 = spriteFrameValues4[2] - spriteFrameValues2[2];
      float spriteFrameFactor10 = spriteFrameFactor5 * spriteFrameFactor9 - spriteFrameFactor6 * spriteFrameFactor8;
      float spriteFrameFactor11 = spriteFrameFactor6 * spriteFrameFactor7 - spriteFrameFactor4 * spriteFrameFactor9;
      float spriteFrameFactor12 = spriteFrameFactor4 * spriteFrameFactor8 - spriteFrameFactor5 * spriteFrameFactor7;
      return spriteFrameFactor10 * spriteFrameFactor + spriteFrameFactor11 * spriteFrameFactor2 + spriteFrameFactor12 * spriteFrameFactor3 >= 0.0F;
   }

   public static float getAmount(int spriteFrameValue, float spriteFrameValue2, float spriteFrameValue3, float spriteFrameValue4) {
      return switch (spriteFrameValue) {
         case 0, 1, 3 -> spriteFrameValue2;
         case 2 -> 1.0F - spriteFrameValue2;
         case 4 -> spriteFrameValue4;
         default -> 1.0F - spriteFrameValue4;
      };
   }

   public static float getAmountForIntFloat(int spriteFrameValue, float spriteFrameValue2, float spriteFrameValue3, float spriteFrameValue4) {
      return switch (spriteFrameValue) {
         case 0 -> 1.0F - spriteFrameValue4;
         case 1 -> spriteFrameValue4;
         default -> 1.0F - spriteFrameValue3;
      };
   }

   public static void handle(int index, int i, float[] spriteFrameValues, float[] scratchTable) {
      int[] spriteFrameValues3 = spriteFrameInts[index][i];
      scratchTable[0] = spriteFrameValues3[0] == 0 ? spriteFrameValues[0] : spriteFrameValues[3];
      scratchTable[1] = spriteFrameValues3[1] == 0 ? spriteFrameValues[1] : spriteFrameValues[4];
      scratchTable[2] = spriteFrameValues3[2] == 0 ? spriteFrameValues[2] : spriteFrameValues[5];
   }

   private SpriteFrameUtils() {
   }
}
