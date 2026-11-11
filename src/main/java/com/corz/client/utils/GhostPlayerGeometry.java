package com.corz.client.utils;

public final class GhostPlayerGeometry {
   private static final int[] ghostPlayerTable = new int[12];
   public static final float[][] ghostPlayerFloats = new float[][]{
      {-4.0F, 24.0F, -4.0F, 4.0F, 32.0F, 4.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F},
      {-4.5F, 23.5F, -4.5F, 4.5F, 32.5F, 4.5F, 32.0F, 0.0F, 8.0F, 8.0F, 8.0F},
      {-4.0F, 12.0F, -2.0F, 4.0F, 24.0F, 2.0F, 16.0F, 16.0F, 8.0F, 12.0F, 4.0F},
      {-4.0F, 11.75F, -2.25F, 4.0F, 24.25F, 2.25F, 16.0F, 32.0F, 8.0F, 12.0F, 4.0F},
      {-8.0F, 12.0F, -2.0F, -4.0F, 24.0F, 2.0F, 40.0F, 16.0F, 4.0F, 12.0F, 4.0F},
      {-8.25F, 11.75F, -2.25F, -4.0F, 24.25F, 2.25F, 40.0F, 32.0F, 4.0F, 12.0F, 4.0F},
      {4.0F, 12.0F, -2.0F, 8.0F, 24.0F, 2.0F, 32.0F, 48.0F, 4.0F, 12.0F, 4.0F},
      {4.0F, 11.75F, -2.25F, 8.25F, 24.25F, 2.25F, 48.0F, 48.0F, 4.0F, 12.0F, 4.0F},
      {-3.9F, 0.0F, -2.0F, 0.0F, 12.0F, 2.0F, 0.0F, 16.0F, 4.0F, 12.0F, 4.0F},
      {-4.15F, -0.25F, -2.25F, 0.0F, 12.25F, 2.25F, 0.0F, 32.0F, 4.0F, 12.0F, 4.0F},
      {0.0F, 0.0F, -2.0F, 3.9F, 12.0F, 2.0F, 16.0F, 48.0F, 4.0F, 12.0F, 4.0F},
      {0.0F, -0.25F, -2.25F, 4.15F, 12.25F, 2.25F, 0.0F, 48.0F, 4.0F, 12.0F, 4.0F}
   };
   public static final int UNUSED_ONE = 1;
   public static final float[][] ALT_FACE_VERTICES = new float[][]{
      ghostPlayerFloats[0],
      ghostPlayerFloats[1],
      ghostPlayerFloats[2],
      ghostPlayerFloats[3],
      {-7.0F, 12.0F, -2.0F, -4.0F, 24.0F, 2.0F, 40.0F, 16.0F, 3.0F, 12.0F, 4.0F},
      {-7.25F, 11.75F, -2.25F, -4.0F, 24.25F, 2.25F, 40.0F, 32.0F, 3.0F, 12.0F, 4.0F},
      {4.0F, 12.0F, -2.0F, 7.0F, 24.0F, 2.0F, 32.0F, 48.0F, 3.0F, 12.0F, 4.0F},
      {4.0F, 11.75F, -2.25F, 7.25F, 24.25F, 2.25F, 48.0F, 48.0F, 3.0F, 12.0F, 4.0F},
      ghostPlayerFloats[8],
      ghostPlayerFloats[9],
      ghostPlayerFloats[10],
      ghostPlayerFloats[11]
   };
   private static final float[] table = getAmountsALT_FACE_VERTICES(true);
   private static final float[] ALT_VERTEX_TABLE = getAmountsALT_FACE_VERTICES(false);

   public static float[][] getAmounts(boolean ghostPlayerFlag) {
      return ghostPlayerFlag ? ALT_FACE_VERTICES : ghostPlayerFloats;
   }

   public static int getValue(float[] ghostPlayerValues, int ghostPlayerValue, float ghostPlayerLimit, float ghostPlayerValue3, float[] ghostPlayerValues2) {
      return getValue(ghostPlayerValues, ghostPlayerValue, ghostPlayerLimit, ghostPlayerValue3, ghostPlayerValues2, false);
   }

   private static int getValue(float[] ghostPlayerValues, int ghostPlayerCounter, float... ghostPlayerValues2) {
      for (int i = 0; i < 4; i++) {
         ghostPlayerValues[ghostPlayerCounter++] = ghostPlayerValues2[i * 5];
         ghostPlayerValues[ghostPlayerCounter++] = ghostPlayerValues2[i * 5 + 1];
         ghostPlayerValues[ghostPlayerCounter++] = ghostPlayerValues2[i * 5 + 2];
         ghostPlayerValues[ghostPlayerCounter++] = ghostPlayerValues2[i * 5 + 3] / 64.0F;
         ghostPlayerValues[ghostPlayerCounter++] = ghostPlayerValues2[i * 5 + 4] / 64.0F;
      }

      return ghostPlayerCounter;
   }

   private static int getValue(float[] ghostPlayerValues, int index, float ghostPlayerValue, float ghostPlayerValue2, float ghostPlayerValue3, float ghostPlayerValue4, float ghostPlayerValue5, float ghostPlayerValue6) {
      ghostPlayerValues[index] = ghostPlayerValue;
      ghostPlayerValues[index + 1] = ghostPlayerValue2;
      ghostPlayerValues[index + 2] = ghostPlayerValue3;
      ghostPlayerValues[index + 3] = ghostPlayerValue4;
      ghostPlayerValues[index + 4] = ghostPlayerValue5;
      ghostPlayerValues[index + 5] = ghostPlayerValue6;
      return index + 6;
   }

   public static float getAmount(float ghostPlayerLimit) {
      return ghostPlayerLimit < 0.0F ? 0.0F : (ghostPlayerLimit > 1.0F ? 1.0F : ghostPlayerLimit);
   }

   public static float[] getAmountsForFlag(boolean ghostPlayerFlag) {
      return ghostPlayerFlag ? table : ALT_VERTEX_TABLE;
   }

   public static float getAmount(float ghostPlayerValue, float ghostPlayerValue2) {
      return -0.25F - ghostPlayerValue2 + getAmount(ghostPlayerValue) * (32.75F + 2.0F * ghostPlayerValue2);
   }

   public static float getAmount(float ghostPlayerValue, float ghostPlayerLimit, float ghostPlayerValue2) {
      if (ghostPlayerValue2 <= 0.0F) {
         return ghostPlayerValue <= ghostPlayerLimit ? 1.0F : 0.0F;
      } else {
         float ghostPlayerFactor = getAmount((ghostPlayerValue - (ghostPlayerLimit - ghostPlayerValue2)) / (2.0F * ghostPlayerValue2));
         return 1.0F - ghostPlayerFactor * ghostPlayerFactor * (3.0F - 2.0F * ghostPlayerFactor);
      }
   }

   private GhostPlayerGeometry() {
   }

   private static float getAmountForFloat(float ghostPlayerLimit, float ghostPlayerLimit2, float ghostPlayerValue) {
      return ghostPlayerLimit < ghostPlayerLimit2 ? ghostPlayerLimit2 : (ghostPlayerLimit > ghostPlayerValue ? ghostPlayerValue : ghostPlayerLimit);
   }

   public static float[] getAmountsALT_FACE_VERTICES(boolean ghostPlayerFlag) {
      float[][] floats = ghostPlayerFlag ? ALT_FACE_VERTICES : ghostPlayerFloats;
      float[] floats2 = new float[floats.length * 6 * 4 * 5];
      int ghostPlayerValue = 0;

      for (float[] ghostPlayerValues : floats) {
         float ghostPlayerValue2 = ghostPlayerValues[0];
         float ghostPlayerValue3 = ghostPlayerValues[1];
         float ghostPlayerValue4 = ghostPlayerValues[2];
         float ghostPlayerValue5 = ghostPlayerValues[3];
         float ghostPlayerValue6 = ghostPlayerValues[4];
         float ghostPlayerValue7 = ghostPlayerValues[5];
         float ghostPlayerValue8 = ghostPlayerValues[6];
         float ghostPlayerValue9 = ghostPlayerValues[7];
         float ghostPlayerValue10 = ghostPlayerValues[8];
         float ghostPlayerValue11 = ghostPlayerValues[9];
         float ghostPlayerValue12 = ghostPlayerValues[10];
         float ghostPlayerValue13 = ghostPlayerValue8 + ghostPlayerValue12;
         float ghostPlayerValue14 = ghostPlayerValue8 + ghostPlayerValue12 + ghostPlayerValue10;
         float ghostPlayerValue15 = ghostPlayerValue8 + ghostPlayerValue12 + ghostPlayerValue10 + ghostPlayerValue10;
         float ghostPlayerValue16 = ghostPlayerValue8 + ghostPlayerValue12 + ghostPlayerValue10 + ghostPlayerValue12;
         float ghostPlayerValue17 = ghostPlayerValue8 + ghostPlayerValue12 + ghostPlayerValue10 + ghostPlayerValue12 + ghostPlayerValue10;
         float ghostPlayerValue18 = ghostPlayerValue9 + ghostPlayerValue12;
         float ghostPlayerValue19 = ghostPlayerValue9 + ghostPlayerValue12 + ghostPlayerValue11;
         int ghostPlayerValue20 = getValue(
            floats2,
            ghostPlayerValue,
            ghostPlayerValue5,
            ghostPlayerValue6,
            ghostPlayerValue4,
            ghostPlayerValue14,
            ghostPlayerValue9,
            ghostPlayerValue2,
            ghostPlayerValue6,
            ghostPlayerValue4,
            ghostPlayerValue13,
            ghostPlayerValue9,
            ghostPlayerValue2,
            ghostPlayerValue6,
            ghostPlayerValue7,
            ghostPlayerValue13,
            ghostPlayerValue18,
            ghostPlayerValue5,
            ghostPlayerValue6,
            ghostPlayerValue7,
            ghostPlayerValue14,
            ghostPlayerValue18
         );
         int ghostPlayerValue21 = getValue(
            floats2, ghostPlayerValue20, ghostPlayerValue5, ghostPlayerValue3, ghostPlayerValue7, ghostPlayerValue15, ghostPlayerValue18, ghostPlayerValue2, ghostPlayerValue3, ghostPlayerValue7, ghostPlayerValue14, ghostPlayerValue18, ghostPlayerValue2, ghostPlayerValue3, ghostPlayerValue4, ghostPlayerValue14, ghostPlayerValue9, ghostPlayerValue5, ghostPlayerValue3, ghostPlayerValue4, ghostPlayerValue15, ghostPlayerValue9
         );
         int ghostPlayerCounter = getValue(
            floats2, ghostPlayerValue21, ghostPlayerValue2, ghostPlayerValue6, ghostPlayerValue7, ghostPlayerValue13, ghostPlayerValue18, ghostPlayerValue2, ghostPlayerValue6, ghostPlayerValue4, ghostPlayerValue8, ghostPlayerValue18, ghostPlayerValue2, ghostPlayerValue3, ghostPlayerValue4, ghostPlayerValue8, ghostPlayerValue19, ghostPlayerValue2, ghostPlayerValue3, ghostPlayerValue7, ghostPlayerValue13, ghostPlayerValue19
         );
         int ghostPlayerValue23 = getValue(
            floats2, ghostPlayerCounter, ghostPlayerValue5, ghostPlayerValue6, ghostPlayerValue7, ghostPlayerValue14, ghostPlayerValue18, ghostPlayerValue2, ghostPlayerValue6, ghostPlayerValue7, ghostPlayerValue13, ghostPlayerValue18, ghostPlayerValue2, ghostPlayerValue3, ghostPlayerValue7, ghostPlayerValue13, ghostPlayerValue19, ghostPlayerValue5, ghostPlayerValue3, ghostPlayerValue7, ghostPlayerValue14, ghostPlayerValue19
         );
         int ghostPlayerValue24 = getValue(
            floats2,
            ghostPlayerValue23,
            ghostPlayerValue5,
            ghostPlayerValue6,
            ghostPlayerValue4,
            ghostPlayerValue16,
            ghostPlayerValue18,
            ghostPlayerValue5,
            ghostPlayerValue6,
            ghostPlayerValue7,
            ghostPlayerValue14,
            ghostPlayerValue18,
            ghostPlayerValue5,
            ghostPlayerValue3,
            ghostPlayerValue7,
            ghostPlayerValue14,
            ghostPlayerValue19,
            ghostPlayerValue5,
            ghostPlayerValue3,
            ghostPlayerValue4,
            ghostPlayerValue16,
            ghostPlayerValue19
         );
         ghostPlayerValue = getValue(
            floats2, ghostPlayerValue24, ghostPlayerValue2, ghostPlayerValue6, ghostPlayerValue4, ghostPlayerValue17, ghostPlayerValue18, ghostPlayerValue5, ghostPlayerValue6, ghostPlayerValue4, ghostPlayerValue16, ghostPlayerValue18, ghostPlayerValue5, ghostPlayerValue3, ghostPlayerValue4, ghostPlayerValue16, ghostPlayerValue19, ghostPlayerValue2, ghostPlayerValue3, ghostPlayerValue4, ghostPlayerValue17, ghostPlayerValue19
         );
      }

      return floats2;
   }

   private static int getValue(float[] ghostPlayerValues, int ghostPlayerValue, float[] ghostPlayerValues2, int index, int index2, float ghostPlayerFactor, float ghostPlayerValue2, float ghostPlayerValue3) {
      return getValue(
         ghostPlayerValues,
         ghostPlayerValue,
         ghostPlayerValues2[index] + (ghostPlayerValues2[index2] - ghostPlayerValues2[index]) * ghostPlayerFactor,
         ghostPlayerValue2,
         ghostPlayerValues2[index + 2] + (ghostPlayerValues2[index2 + 2] - ghostPlayerValues2[index + 2]) * ghostPlayerFactor,
         ghostPlayerValues2[index + 3] + (ghostPlayerValues2[index2 + 3] - ghostPlayerValues2[index + 3]) * ghostPlayerFactor,
         ghostPlayerValues2[index + 4] + (ghostPlayerValues2[index2 + 4] - ghostPlayerValues2[index + 4]) * ghostPlayerFactor,
         ghostPlayerValue3
      );
   }

   static {
      ghostPlayerTable[2] = 23;
      ghostPlayerTable[4] = 16;
      ghostPlayerTable[6] = 4;
      ghostPlayerTable[8] = 17;
      ghostPlayerTable[10] = 5;
   }

   public static int getValue(float[] ghostPlayerValues, int ghostPlayerValue, float ghostPlayerLimit, float ghostPlayerValue3, float[] ghostPlayerValues2, boolean ghostPlayerFlag) {
      float[] floats = new float[7];
      int ghostPlayerValue4 = 0;

      for (int i = 0; i < 12; i++) {
         if ((ghostPlayerValue >> i & 1) != 0) {
            for (int j = 0; j < 6; j++) {
               if (!ghostPlayerFlag || (ghostPlayerTable[i] >> j & 1) == 0) {
                  int ghostPlayerValue5 = (i * 6 + j) * 4 * 5;
                  if (j != 0 && j != 1) {
                     int ghostPlayerValue6 = ghostPlayerValue5;
                     int ghostPlayerValue7 = ghostPlayerValue5 + 5;
                     int ghostPlayerValue8 = ghostPlayerValue5 + 10;
                     int ghostPlayerValue9 = ghostPlayerValue5 + 15;
                     float ghostPlayerValue10 = ghostPlayerValues[ghostPlayerValue5 + 1];
                     float ghostPlayerValue11 = ghostPlayerValues[ghostPlayerValue9 + 1];
                     float deltaVar = ghostPlayerValue10 - ghostPlayerValue11;
                     floats[0] = ghostPlayerValue11;
                     floats[1] = getAmountForFloat(ghostPlayerLimit - ghostPlayerValue3, ghostPlayerValue11, ghostPlayerValue10);
                     floats[2] = getAmountForFloat(ghostPlayerLimit - ghostPlayerValue3 * 0.5F, ghostPlayerValue11, ghostPlayerValue10);
                     floats[3] = getAmountForFloat(ghostPlayerLimit, ghostPlayerValue11, ghostPlayerValue10);
                     floats[4] = getAmountForFloat(ghostPlayerLimit + ghostPlayerValue3 * 0.5F, ghostPlayerValue11, ghostPlayerValue10);
                     floats[5] = getAmountForFloat(ghostPlayerLimit + ghostPlayerValue3, ghostPlayerValue11, ghostPlayerValue10);
                     floats[6] = ghostPlayerValue10;

                     for (int k = 0; k < 6; k++) {
                        float ghostPlayerValue12 = floats[k];
                        float ghostPlayerValue13 = floats[k + 1];
                        if (!(ghostPlayerValue13 - ghostPlayerValue12 <= 1.0E-4F)) {
                           float ghostPlayerFactor = (ghostPlayerValue12 - ghostPlayerValue11) / deltaVar;
                           float ghostPlayerValue15 = (ghostPlayerValue13 - ghostPlayerValue11) / deltaVar;
                           float ghostPlayerValue16 = getAmount(ghostPlayerValue12, ghostPlayerLimit, ghostPlayerValue3);
                           float ghostPlayerValue17 = getAmount(ghostPlayerValue13, ghostPlayerLimit, ghostPlayerValue3);
                           int ghostPlayerValue18 = getValue(ghostPlayerValues2, ghostPlayerValue4, ghostPlayerValues, ghostPlayerValue9, ghostPlayerValue6, ghostPlayerValue15, ghostPlayerValue13, ghostPlayerValue17);
                           int ghostPlayerValue19 = getValue(ghostPlayerValues2, ghostPlayerValue18, ghostPlayerValues, ghostPlayerValue8, ghostPlayerValue7, ghostPlayerValue15, ghostPlayerValue13, ghostPlayerValue17);
                           int ghostPlayerValue20 = getValue(ghostPlayerValues2, ghostPlayerValue19, ghostPlayerValues, ghostPlayerValue8, ghostPlayerValue7, ghostPlayerFactor, ghostPlayerValue12, ghostPlayerValue16);
                           ghostPlayerValue4 = getValue(ghostPlayerValues2, ghostPlayerValue20, ghostPlayerValues, ghostPlayerValue9, ghostPlayerValue6, ghostPlayerFactor, ghostPlayerValue12, ghostPlayerValue16);
                        }
                     }
                  } else {
                     for (int m = 0; m < 4; m++) {
                        int index = ghostPlayerValue5 + m * 5;
                        ghostPlayerValue4 = getValue(
                           ghostPlayerValues2, ghostPlayerValue4, ghostPlayerValues[index], ghostPlayerValues[index + 1], ghostPlayerValues[index + 2], ghostPlayerValues[index + 3], ghostPlayerValues[index + 4], getAmount(ghostPlayerValues[index + 1], ghostPlayerLimit, ghostPlayerValue3)
                        );
                     }
                  }
               }
            }
         }
      }

      return ghostPlayerValue4 / 6;
   }
}
