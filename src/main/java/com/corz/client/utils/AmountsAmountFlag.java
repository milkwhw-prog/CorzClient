package com.corz.client.utils;

import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.RenderMatrixUtilsAmount;
import com.corz.client.render.VertexConsumerUtilsAmounts;

public final class AmountsAmountFlag {
   public static final float merryAmount = (float)(-Math.cos(Math.toRadians(40.0)));
   public float merryAverage;
   final float[] table;
   private final float[] clippedX;
   public static final float SHADOW_DIR_Y = (float)(-Math.sin(Math.toRadians(40.0)));
   private boolean pathStarted;
   private float edgeStartX;
   public int pointCount;
   private float shadowOriginX;
   private final float[] arcY;
   private static final float[] BAND_POSITIONS = new float[]{0.0F, 0.035F, 0.14F, 0.3F, 0.58F, 1.0F};
   private float shadowOriginY;
   private static final float[] BAND_OFFSETS = new float[]{-0.38F, 0.06F, 0.26F, 0.1F, 0.0F, -0.2F};
   public final float[] merryTable;
   final float[] shiftedY;
   private final float[] sliceY;
   private float edgeEndX;
   int clippedCount;
   public final float[] matrixRenderTable;
   private float edgeEndY;
   int shiftedCount;
   float maxRadius;
   private final float[] bandY;
   private float edgeStartY;
   private final float[] sliceX;
   private final float[] arcX = new float[44];
   public float matrixRenderAmount;
   private final float[] clippedY;
   private final float[] bandX;

   public boolean canAmounts(float f, float f2) {
      return canFlag(this.merryTable, this.matrixRenderTable, this.pointCount, f, f2);
   }

   float getAmount(float f, float f2) {
      return (f - this.shadowOriginX) * merryAmount + (f2 - this.shadowOriginY) * SHADOW_DIR_Y;
   }

   private int getValue(float f, float f2, float f3, float divisor, float factor) {
      float max2 = Math.max(0.0F, Math.min(1.0F, 1.0F - (f3 - this.getAmount(f, f2)) / divisor));
      return VertexConsumerUtilsAmounts.getValue(0, 0.6F * max2 * max2 * factor);
   }

   public boolean canAmountsMax(float f, float f2) {
      return this.maxRadius >= 1.0F && canFlag(this.table, this.shiftedY, this.shiftedCount, f, f2);
   }

   private int getAmounts(float f, float f2, float f3, float f4, float factor) {
      int index = 0;
      float[][] floats = new float[][]{{f + f3 - factor, f2 + f4 - factor, 0.0F}, {f + factor, f2 + f4 - factor, 90.0F}, {f + factor, f2 + factor, 180.0F}, {f + f3 - factor, f2 + factor, 270.0F}};

      for (float[] floatsItem : floats) {
         for (int i = 0; i <= 8; i++) {
            double angle = Math.toRadians(floatsItem[2] + 90.0 * i / 8.0);
            this.arcX[index] = floatsItem[0] + (float)Math.cos(angle) * factor;
            this.arcY[index] = floatsItem[1] + (float)Math.sin(angle) * factor;
            index++;
         }
      }

      return index;
   }

   public AmountsAmountFlag() {
      this.arcY = new float[44];
      this.clippedX = new float[44];
      this.clippedY = new float[44];
      this.sliceX = new float[44];
      this.sliceY = new float[44];
      this.bandX = new float[44];
      this.bandY = new float[44];
      this.merryTable = new float[44];
      this.matrixRenderTable = new float[44];
      this.table = new float[44];
      this.shiftedY = new float[44];
   }

   public void handleAmountsMax(float f) {
      if (this.clippedCount >= 3 && !(f <= 0.01F)) {
         VertexConsumerUtilsAmounts.updatePositionMatrix();

         try {
            float f2 = this.maxRadius * 0.5F;
            float min3 = Math.min(22.0F, 6.0F + this.maxRadius * 0.05F);

            for (int i = 1; i < this.clippedCount - 1; i++) {
               VertexConsumerUtilsAmounts.handleVertexColor(
                  this.clippedX[0],
                  this.clippedY[0],
                  this.getValue(this.clippedX[0], this.clippedY[0], f2, min3, f),
                  this.clippedX[i],
                  this.clippedY[i],
                  this.getValue(this.clippedX[i], this.clippedY[i], f2, min3, f),
                  this.clippedX[i + 1],
                  this.clippedY[i + 1],
                  this.getValue(this.clippedX[i + 1], this.clippedY[i + 1], f2, min3, f)
               );
            }
         } finally {
            VertexConsumerUtilsAmounts.updateConsumerVertex();
         }
      }
   }

   private int getValue(float[] floats, float[] floats2, int limit, float f, boolean flag, float[] floats3, float[] floats4) {
      int index = 0;

      for (int i = 0; i < limit; i++) {
         int index2 = (i + 1) % limit;
         float f2 = this.getAmount(floats[i], floats2[i]) - f;
         float f3 = this.getAmount(floats[index2], floats2[index2]) - f;
         if (!flag) {
            f2 = -f2;
            f3 = -f3;
         }

         if (f2 <= 0.0F) {
            floats3[index] = floats[i];
            floats4[index] = floats2[i];
            index++;
         }

         if (f2 <= 0.0F != f3 <= 0.0F && index < floats3.length) {
            float factor = f2 / (f2 - f3);
            floats3[index] = floats[i] + (floats[index2] - floats[i]) * factor;
            floats4[index] = floats2[i] + (floats2[index2] - floats2[i]) * factor;
            index++;
         }
      }

      return index;
   }

   private int getAmounts(int n, float f, float f2, float bound, float f3) {
      float limit = Math.max(0.0F, Math.min(1.0F, (this.getAmount(f, f2) - bound) / Math.max(1.0F, bound)));
      int index = 1;

      while (index < BAND_POSITIONS.length - 1 && limit > BAND_POSITIONS[index]) {
         index++;
      }

      float hashHuntBound = (limit - BAND_POSITIONS[index - 1]) / (BAND_POSITIONS[index] - BAND_POSITIONS[index - 1]);
      float matrixFactor = BAND_OFFSETS[index - 1] + (BAND_OFFSETS[index] - BAND_OFFSETS[index - 1]) * Math.max(0.0F, Math.min(1.0F, hashHuntBound));
      int n2 = matrixFactor >= 0.0F ? RenderMatrixUtilsAmount.getValue(n, -1, matrixFactor) : RenderMatrixUtilsAmount.getValue(n, -16777216, -matrixFactor);
      return VertexConsumerUtilsAmounts.getValue(n2, f3);
   }

   private static boolean canFlag(float[] floats, float[] floats2, int limit, float f, float f2) {
      if (limit < 3) {
         return false;
      } else {
         for (int i = 0; i < limit; i++) {
            int index = (i + 1) % limit;
            float f3 = (floats[index] - floats[i]) * (f2 - floats2[i]) - (floats2[index] - floats2[i]) * (f - floats[i]);
            if (f3 < 0.0F) {
               return false;
            }
         }

         return true;
      }
   }

   public void handleAmountsAmount(float f, float f2, float bound, float bound2, float wrapDegreesBound, float peekMerry) {
      this.maxRadius = Math.max(0.0F, peekMerry);
      int n = this.getAmounts(f, f2, bound, bound2, Math.min(wrapDegreesBound, Math.min(bound, bound2) / 2.0F));
      this.shadowOriginX = f + bound;
      this.shadowOriginY = f2 + bound2;
      float f3 = this.maxRadius * 0.5F;
      this.pointCount = this.getValue(this.arcX, this.arcY, n, f3, true, this.merryTable, this.matrixRenderTable);
      this.merryAverage = 0.0F;
      this.matrixRenderAmount = 0.0F;

      for (int i = 0; i < this.pointCount; i++) {
         this.merryAverage = this.merryAverage + this.merryTable[i];
         this.matrixRenderAmount = this.matrixRenderAmount + this.matrixRenderTable[i];
      }

      if (this.pointCount > 0) {
         this.merryAverage = this.merryAverage / this.pointCount;
         this.matrixRenderAmount = this.matrixRenderAmount / this.pointCount;
      }

      this.shiftedCount = this.pointCount;

      for (int j = 0; j < this.pointCount; j++) {
         int index = this.pointCount - 1 - j;
         float factor = this.maxRadius - 2.0F * this.getAmount(this.merryTable[index], this.matrixRenderTable[index]);
         this.table[j] = this.merryTable[index] + merryAmount * factor;
         this.shiftedY[j] = this.matrixRenderTable[index] + SHADOW_DIR_Y * factor;
      }

      this.pathStarted = false;

      for (int k = 0; k < this.pointCount; k++) {
         int index2 = (k + 1) % this.pointCount;
         if (Math.abs(this.getAmount(this.merryTable[k], this.matrixRenderTable[k]) - f3) < 0.01F
            && Math.abs(this.getAmount(this.merryTable[index2], this.matrixRenderTable[index2]) - f3) < 0.01F) {
            this.edgeStartX = this.merryTable[k];
            this.edgeStartY = this.matrixRenderTable[k];
            this.edgeEndX = this.merryTable[index2];
            this.edgeEndY = this.matrixRenderTable[index2];
            this.pathStarted = true;
         }
      }

      float min4 = Math.min(22.0F, 6.0F + this.maxRadius * 0.05F);
      this.clippedCount = this.getValue(this.merryTable, this.matrixRenderTable, this.pointCount, f3 - min4, false, this.clippedX, this.clippedY);
   }

   public void handleAmountsAmount(float factor) {
      if (this.shiftedCount >= 3 && !(factor <= 0.01F) && !(this.maxRadius < 1.0F)) {
         float bound = this.maxRadius * 0.5F;
         VertexConsumerUtilsAmounts.updatePositionMatrix();

         try {
            for (int i = 0; i < 2; i++) {
               float f = i == 0 ? 1.5F : 4.0F;
               float f2 = i == 0 ? 2.5F : 6.0F;
               int n = VertexConsumerUtilsAmounts.getValue(0, (i == 0 ? 0.3F : 0.12F) * factor);

               for (int j = 1; j < this.shiftedCount - 1; j++) {
                  VertexConsumerUtilsAmounts.handleVertexColor(
                     this.table[0] + f,
                     this.shiftedY[0] + f2,
                     n,
                     this.table[j] + f,
                     this.shiftedY[j] + f2,
                     n,
                     this.table[j + 1] + f,
                     this.shiftedY[j + 1] + f2,
                     n
                  );
               }
            }

            int n2 = RenderMatrixUtilsAmount.getValue(0xFF000000 | CardMatrixUtils.getValue() & 16777215, -6310698, 0.14F);

            for (int k = 0; k + 1 < BAND_POSITIONS.length; k++) {
               int limit = this.getValue(this.table, this.shiftedY, this.shiftedCount, bound + BAND_POSITIONS[k] * bound - 0.01F, false, this.sliceX, this.sliceY);
               if (limit >= 3) {
                  int n3 = this.getValue(
                     this.sliceX, this.sliceY, limit, bound + BAND_POSITIONS[k + 1] * bound + 0.01F, true, this.bandX, this.bandY
                  );

                  for (int m = 1; m < n3 - 1; m++) {
                     VertexConsumerUtilsAmounts.handleVertexColor(
                        this.bandX[0],
                        this.bandY[0],
                        this.getAmounts(n2, this.bandX[0], this.bandY[0], bound, factor),
                        this.bandX[m],
                        this.bandY[m],
                        this.getAmounts(n2, this.bandX[m], this.bandY[m], bound, factor),
                        this.bandX[m + 1],
                        this.bandY[m + 1],
                        this.getAmounts(n2, this.bandX[m + 1], this.bandY[m + 1], bound, factor)
                     );
                  }
               }
            }

            int n4 = VertexConsumerUtilsAmounts.getValue(16777215, 0.16F * factor);

            for (int p = 0; p < this.shiftedCount; p++) {
               int index = (p + 1) % this.shiftedCount;
               if (!(Math.abs(this.getAmount(this.table[p], this.shiftedY[p]) - bound) < 0.05F)
                  || !(Math.abs(this.getAmount(this.table[index], this.shiftedY[index]) - bound) < 0.05F)) {
                  VertexConsumerUtilsAmounts.handleMax(
                     this.table[p], this.shiftedY[p], this.table[index], this.shiftedY[index], 0.3F, n4, n4
                  );
               }
            }

            if (this.pathStarted) {
               int n5 = VertexConsumerUtilsAmounts.getValue(16777215, 0.32F * factor);
               VertexConsumerUtilsAmounts.handleMax(this.edgeStartX, this.edgeStartY, this.edgeEndX, this.edgeEndY, 0.4F, n5, n5);
            }
         } finally {
            VertexConsumerUtilsAmounts.updateConsumerVertex();
         }
      }
   }
}
