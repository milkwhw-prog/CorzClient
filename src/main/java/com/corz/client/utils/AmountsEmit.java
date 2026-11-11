package com.corz.client.utils;

public final class AmountsEmit {
   public final float[] vectorOutlineTable = new float[1856];
   private final float[] emitTable;
   private final float[] table = new float[15];
   private final float[] segmentZ;
   private final float[] frontAxisZ;
   private final float[] sinPitchCosYaw;
   private final float[] frontAxisX;
   private final float[] cosPitchSinYaw;
   private final float[] upAxisY;
   private final float[] cosPitchCosYaw;
   private final float[] sideAxisZ;
   private final float[] frontAxisY;
   private final float[] upAxisX;
   private final float[] cosPitch;
   private final float[] segmentY = new float[15];
   private int emitIndex;
   private final float[] upAxisZ;
   private final float[] sinPitchSinYaw;
   private final float[] negSinPitch;
   private final float[] sideAxisY;

   private void handleAmounts(int index, int emitValue, int emitValue2, float emitValue3, float emitValue4, float emitValue5, float emitValue6, float emitValue7) {
      float emitFactor = emitValue * 5.0F;
      float outerScaled = emitValue2 * 1.0F;
      int index2 = this.emitIndex * 8;
      this.vectorOutlineTable[index2] = this.table[index] + this.emitTable[index] * emitFactor + this.upAxisX[index] * outerScaled;
      this.vectorOutlineTable[index2 + 1] = this.segmentY[index] + this.sideAxisY[index] * emitFactor + this.upAxisY[index] * outerScaled;
      this.vectorOutlineTable[index2 + 2] = this.segmentZ[index] + this.sideAxisZ[index] * emitFactor + this.upAxisZ[index] * outerScaled;
      this.vectorOutlineTable[index2 + 3] = emitValue3;
      this.vectorOutlineTable[index2 + 4] = emitValue4;
      this.vectorOutlineTable[index2 + 5] = emitValue5;
      this.vectorOutlineTable[index2 + 6] = emitValue6;
      this.vectorOutlineTable[index2 + 7] = emitValue7;
      this.emitIndex++;
   }

   public float getAmounts(int index) {
      return this.segmentZ[index];
   }

   public int getValue() {
      return this.emitIndex;
   }

   public void handleAmounts(float[] emitValues, float[] emitValues2) {
      for (int i = 0; i < 14; i++) {
         float cos = (float)Math.cos(emitValues[i]);
         float sin = (float)Math.sin(emitValues[i]);
         float cos2 = (float)Math.cos(emitValues2[i]);
         float sin2 = (float)Math.sin(emitValues2[i]);
         this.negSinPitch[i] = -sin2;
         this.cosPitchCosYaw[i] = cos2 * cos;
         this.cosPitchSinYaw[i] = cos2 * sin;
         this.cosPitch[i] = cos2;
         this.sinPitchCosYaw[i] = sin2 * cos;
         this.sinPitchSinYaw[i] = sin2 * sin;
      }

      this.table[0] = 0.0F;
      this.segmentY[0] = 0.0F;
      this.segmentZ[0] = 2.0F;

      for (int j = 1; j < 15; j++) {
         this.table[j] = this.table[j - 1] + 1.1428572F * this.negSinPitch[j - 1];
         this.segmentY[j] = this.segmentY[j - 1] + 1.1428572F * this.cosPitchCosYaw[j - 1];
         this.segmentZ[j] = this.segmentZ[j - 1] + 1.1428572F * this.cosPitchSinYaw[j - 1];
      }

      for (int k = 0; k < 15; k++) {
         float emitFactor;
         float emitFactor2;
         float emitFactor3;
         float emitFactor4;
         float emitFactor5;
         float emitFactor6;
         if (k == 0) {
            emitFactor = this.negSinPitch[0];
            emitFactor2 = this.cosPitchCosYaw[0];
            emitFactor3 = this.cosPitchSinYaw[0];
            emitFactor4 = 1.0F;
            emitFactor5 = 0.0F;
            emitFactor6 = 0.0F;
         } else if (k == 14) {
            emitFactor = this.negSinPitch[k - 1];
            emitFactor2 = this.cosPitchCosYaw[k - 1];
            emitFactor3 = this.cosPitchSinYaw[k - 1];
            emitFactor4 = this.cosPitch[k - 1];
            emitFactor5 = this.sinPitchCosYaw[k - 1];
            emitFactor6 = this.sinPitchSinYaw[k - 1];
         } else {
            emitFactor = this.negSinPitch[k - 1] + this.negSinPitch[k];
            emitFactor2 = this.cosPitchCosYaw[k - 1] + this.cosPitchCosYaw[k];
            emitFactor3 = this.cosPitchSinYaw[k - 1] + this.cosPitchSinYaw[k];
            emitFactor4 = this.cosPitch[k - 1] + this.cosPitch[k];
            emitFactor5 = this.sinPitchCosYaw[k - 1] + this.sinPitchCosYaw[k];
            emitFactor6 = this.sinPitchSinYaw[k - 1] + this.sinPitchSinYaw[k];
         }

         float distance = (float)Math.sqrt(emitFactor * emitFactor + emitFactor2 * emitFactor2 + emitFactor3 * emitFactor3);
         if (distance < 1.0E-6F) {
            emitFactor = this.negSinPitch[Math.min(k, 13)];
            emitFactor2 = this.cosPitchCosYaw[Math.min(k, 13)];
            emitFactor3 = this.cosPitchSinYaw[Math.min(k, 13)];
            distance = (float)Math.sqrt(emitFactor * emitFactor + emitFactor2 * emitFactor2 + emitFactor3 * emitFactor3);
         }

         float emitFactor7 = emitFactor / distance;
         float emitFactor8 = emitFactor2 / distance;
         float emitFactor9 = emitFactor3 / distance;
         float emitFactor10 = emitFactor4 * emitFactor7 + emitFactor5 * emitFactor8 + emitFactor6 * emitFactor9;
         float emitFactor11 = emitFactor4 - emitFactor10 * emitFactor7;
         float emitFactor12 = emitFactor5 - emitFactor10 * emitFactor8;
         float emitFactor13 = emitFactor6 - emitFactor10 * emitFactor9;
         float sqrt2 = (float)Math.sqrt(emitFactor11 * emitFactor11 + emitFactor12 * emitFactor12 + emitFactor13 * emitFactor13);
         if (sqrt2 < 1.0E-6F) {
            float emitValue = 1.0F;
            float emitValue2 = 0.0F;
            float emitValue3 = 0.0F;
            emitFactor11 = emitValue - emitFactor7 * emitFactor7;
            emitFactor12 = emitValue2 - emitFactor7 * emitFactor8;
            emitFactor13 = emitValue3 - emitFactor7 * emitFactor9;
            sqrt2 = Math.max(1.0E-6F, (float)Math.sqrt(emitFactor11 * emitFactor11 + emitFactor12 * emitFactor12 + emitFactor13 * emitFactor13));
         }

         float emitFactor14 = emitFactor11 / sqrt2;
         float emitFactor15 = emitFactor12 / sqrt2;
         float emitFactor16 = emitFactor13 / sqrt2;
         this.frontAxisX[k] = emitFactor7;
         this.frontAxisY[k] = emitFactor8;
         this.frontAxisZ[k] = emitFactor9;
         this.emitTable[k] = emitFactor14;
         this.sideAxisY[k] = emitFactor15;
         this.sideAxisZ[k] = emitFactor16;
         float emitFactor17 = emitFactor15 * emitFactor9 - emitFactor16 * emitFactor8;
         float emitFactor18 = emitFactor16 * emitFactor7 - emitFactor14 * emitFactor9;
         float emitFactor19 = emitFactor14 * emitFactor8 - emitFactor15 * emitFactor7;
         float emitDivisor = Math.max(1.0E-6F, (float)Math.sqrt(emitFactor17 * emitFactor17 + emitFactor18 * emitFactor18 + emitFactor19 * emitFactor19));
         this.upAxisX[k] = emitFactor17 / emitDivisor;
         this.upAxisY[k] = emitFactor18 / emitDivisor;
         this.upAxisZ[k] = emitFactor19 / emitDivisor;
      }

      this.emitIndex = 0;

      for (int m = 0; m < 14; m++) {
         int index = m + 1;
         float emitValue4 = (1.0F + 16.0F * m / 14.0F) / 32.0F;
         float emitValue5 = (1.0F + 16.0F * index / 14.0F) / 32.0F;
         this.handleAmounts(m, 1, 1, 0.015625F, emitValue4, this.upAxisX[m], this.upAxisY[m], this.upAxisZ[m]);
         this.handleAmounts(index, 1, 1, 0.015625F, emitValue5, this.upAxisX[index], this.upAxisY[index], this.upAxisZ[index]);
         this.handleAmounts(index, -1, 1, 0.171875F, emitValue5, this.upAxisX[index], this.upAxisY[index], this.upAxisZ[index]);
         this.handleAmounts(m, -1, 1, 0.171875F, emitValue4, this.upAxisX[m], this.upAxisY[m], this.upAxisZ[m]);
         this.handleAmounts(m, 1, 0, 0.34375F, emitValue4, -this.upAxisX[m], -this.upAxisY[m], -this.upAxisZ[m]);
         this.handleAmounts(m, -1, 0, 0.1875F, emitValue4, -this.upAxisX[m], -this.upAxisY[m], -this.upAxisZ[m]);
         this.handleAmounts(index, -1, 0, 0.1875F, emitValue5, -this.upAxisX[index], -this.upAxisY[index], -this.upAxisZ[index]);
         this.handleAmounts(index, 1, 0, 0.34375F, emitValue5, -this.upAxisX[index], -this.upAxisY[index], -this.upAxisZ[index]);
         this.handleAmounts(m, 1, 0, 0.0F, emitValue4, this.emitTable[m], this.sideAxisY[m], this.sideAxisZ[m]);
         this.handleAmounts(index, 1, 0, 0.0F, emitValue5, this.emitTable[index], this.sideAxisY[index], this.sideAxisZ[index]);
         this.handleAmounts(index, 1, 1, 0.015625F, emitValue5, this.emitTable[index], this.sideAxisY[index], this.sideAxisZ[index]);
         this.handleAmounts(m, 1, 1, 0.015625F, emitValue4, this.emitTable[m], this.sideAxisY[m], this.sideAxisZ[m]);
         this.handleAmounts(m, -1, 0, 0.1875F, emitValue4, -this.emitTable[m], -this.sideAxisY[m], -this.sideAxisZ[m]);
         this.handleAmounts(m, -1, 1, 0.171875F, emitValue4, -this.emitTable[m], -this.sideAxisY[m], -this.sideAxisZ[m]);
         this.handleAmounts(index, -1, 1, 0.171875F, emitValue5, -this.emitTable[index], -this.sideAxisY[index], -this.sideAxisZ[index]);
         this.handleAmounts(index, -1, 0, 0.1875F, emitValue5, -this.emitTable[index], -this.sideAxisY[index], -this.sideAxisZ[index]);
      }

      byte index2 = 0;
      byte index3 = 14;
      this.handleAmounts(index2, 1, 0, 0.015625F, 0.0F, -this.frontAxisX[index2], -this.frontAxisY[index2], -this.frontAxisZ[index2]);
      this.handleAmounts(index2, 1, 1, 0.015625F, 0.03125F, -this.frontAxisX[index2], -this.frontAxisY[index2], -this.frontAxisZ[index2]);
      this.handleAmounts(index2, -1, 1, 0.171875F, 0.03125F, -this.frontAxisX[index2], -this.frontAxisY[index2], -this.frontAxisZ[index2]);
      this.handleAmounts(index2, -1, 0, 0.171875F, 0.0F, -this.frontAxisX[index2], -this.frontAxisY[index2], -this.frontAxisZ[index2]);
      this.handleAmounts(index3, 1, 0, 0.171875F, 0.0F, this.frontAxisX[index3], this.frontAxisY[index3], this.frontAxisZ[index3]);
      this.handleAmounts(index3, -1, 0, 0.328125F, 0.0F, this.frontAxisX[index3], this.frontAxisY[index3], this.frontAxisZ[index3]);
      this.handleAmounts(index3, -1, 1, 0.328125F, 0.03125F, this.frontAxisX[index3], this.frontAxisY[index3], this.frontAxisZ[index3]);
      this.handleAmounts(index3, 1, 1, 0.171875F, 0.03125F, this.frontAxisX[index3], this.frontAxisY[index3], this.frontAxisZ[index3]);
   }

   public float getAmountsForInt(int index) {
      return this.segmentY[index];
   }

   public AmountsEmit() {
      this.segmentZ = new float[15];
      this.emitTable = new float[15];
      this.sideAxisY = new float[15];
      this.sideAxisZ = new float[15];
      this.upAxisX = new float[15];
      this.upAxisY = new float[15];
      this.upAxisZ = new float[15];
      this.frontAxisX = new float[15];
      this.frontAxisY = new float[15];
      this.frontAxisZ = new float[15];
      this.negSinPitch = new float[14];
      this.cosPitchCosYaw = new float[14];
      this.cosPitchSinYaw = new float[14];
      this.cosPitch = new float[14];
      this.sinPitchCosYaw = new float[14];
      this.sinPitchSinYaw = new float[14];
   }
}
