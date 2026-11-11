package com.corz.client.utils;
import com.corz.client.utils.TrailSink;
import com.corz.client.utils.TrailBuffer;

public final class TrailMesher {
   private final float[] styleTable;
   private final float[] trailMesherTable;
   private final float[] table;
   private double z;
   private float maxTaper;
   private static final float[] sinkTable = new float[15];
   private float sinkBaseFactor;
   private static final float[] FLAT_PROFILE_WEIGHTS = new float[]{0.0F, 1.0F, 0.0F};
   private static final float[] ROUND_PROFILE_WEIGHTS = new float[]{0.0F, 0.3F, 1.0F, 0.3F, 0.0F};
   private final float[] negativeSideFade;
   private final double[] pointZ;
   private int totalSegmentCount;
   private double y;
   private final int[] segmentColors;
   private final double[] pointY;
   private final float[] segmentTaper;
   private final int sinkValue;
   private double x;
   private final double[] tailZ;
   private final float[] sideNormalX;
   private float sinkAmount;
   private static final float[] ROUND_PROFILE_OFFSETS = new float[]{-1.0F, -0.4F, 0.0F, 0.4F, 1.0F};
   private int styleCount;
   private TrailSink trailSink;
   private final float[] segmentAlpha;
   private final float[] sideNormalY;
   private final double[] segmentTimes;
   private final float[] tailY;
   private int sinkCount;
   private int trailMesherCount;
   private int segmentLimit;
   private float styleAmount;
   private int emittedSegmentCount;
   private final float[] renderX;
   private double trailMesherAmount;
   private final float[] renderLowerY;
   private final double[] pointX;
   private final float[] cumulativeLength;
   private final float[] rawZ;
   private float sinkIntensitySquared;
   private static final float MAX_BEND_COS = (float)Math.cos(Math.PI / 7);
   private TrailMesher.Style style;
   private int sinkTrailCount;
   private final float[] sideNormalZ;
   private static final float[] SIN_TABLE = new float[15];
   private final float[] renderUpperY;
   private int pointCount;
   private final float[] positiveSideFade;
   private final double[] tailX;
   private final int trailMesherValue;
   private int sinkLimit;
   private final float[] rawX;
   private final float[] rawY;
   private final float[] blendParams;
   private int styleMaxCount;
   private final double[] scratchPoint = new double[3];
   private float sinkDetailFactor;
   private static final float[] FLAT_PROFILE_OFFSETS = new float[]{-1.0F, 0.0F, 1.0F};
   private final float[] segmentHeight;
   private float sinkTintStrength;

   public int getEmittedSegmentCount() {
      return this.emittedSegmentCount;
   }

   public int getSinkTrailCount() {
      return this.sinkTrailCount;
   }

   private float getStyleAmount(float styleFactor) {
      return styleFactor * Math.max(1.0F, this.style.styleAmount / 0.3F);
   }

   static float getAmount(float trailMesherValue, float trailMesherValue2, float trailMesherFactor) {
      return trailMesherValue + (trailMesherValue2 - trailMesherValue) * trailMesherFactor;
   }

   public TrailMesher(int trailMesherValue) {
      this.blendParams = new float[3];
      this.trailMesherValue = trailMesherValue + 4;
      this.sinkValue = Math.max(640, trailMesherValue + 260);
      this.pointX = new double[this.trailMesherValue];
      this.pointY = new double[this.trailMesherValue];
      this.pointZ = new double[this.trailMesherValue];
      this.tailX = new double[this.trailMesherValue];
      this.tailZ = new double[this.trailMesherValue];
      this.tailY = new float[this.trailMesherValue];
      this.rawX = new float[this.sinkValue];
      this.rawY = new float[this.sinkValue];
      this.rawZ = new float[this.sinkValue];
      this.segmentHeight = new float[this.sinkValue];
      this.table = new float[this.sinkValue];
      this.cumulativeLength = new float[this.sinkValue];
      this.segmentTaper = new float[this.sinkValue];
      this.segmentAlpha = new float[this.sinkValue];
      this.segmentTimes = new double[this.sinkValue];
      this.segmentColors = new int[this.sinkValue];
      this.renderX = new float[this.sinkValue];
      this.renderUpperY = new float[this.sinkValue];
      this.styleTable = new float[this.sinkValue];
      this.renderLowerY = new float[this.sinkValue];
      this.sideNormalX = new float[this.sinkValue];
      this.sideNormalY = new float[this.sinkValue];
      this.sideNormalZ = new float[this.sinkValue];
      this.trailMesherTable = new float[this.sinkValue];
      this.positiveSideFade = new float[this.sinkValue];
      this.negativeSideFade = new float[this.sinkValue];
   }

   private void handleAmounts(float[] trailMesherValues, float[] trailMesherValues2, float[] trailMesherValues3) {
      float trailMesherFactor = 0.0F;
      float trailMesherFactor2 = 0.0F;
      float trailMesherFactor3 = 0.0F;
      boolean trailMesherFlag = false;

      for (int i = 0; i < this.styleCount; i++) {
         int index = i > 0 ? i - 1 : i;
         int index2 = i < this.styleCount - 1 ? i + 1 : i;
         float trailMesherFactor4 = trailMesherValues[index2] - trailMesherValues[index];
         float trailMesherFactor5 = trailMesherValues2[index2] - trailMesherValues2[index];
         float trailMesherFactor6 = trailMesherValues3[index2] - trailMesherValues3[index];
         float trailMesherFactor7 = trailMesherValues[i];
         float trailMesherFactor8 = trailMesherValues2[i];
         float trailMesherFactor9 = trailMesherValues3[i];
         float trailMesherFactor10 = trailMesherFactor5 * trailMesherFactor9 - trailMesherFactor6 * trailMesherFactor8;
         float trailMesherFactor11 = trailMesherFactor6 * trailMesherFactor7 - trailMesherFactor4 * trailMesherFactor9;
         float trailMesherFactor12 = trailMesherFactor4 * trailMesherFactor8 - trailMesherFactor5 * trailMesherFactor7;
         float distance = (float)Math.sqrt(trailMesherFactor10 * trailMesherFactor10 + trailMesherFactor11 * trailMesherFactor11 + trailMesherFactor12 * trailMesherFactor12);
         float sqrt2 = (float)Math.sqrt(trailMesherFactor4 * trailMesherFactor4 + trailMesherFactor5 * trailMesherFactor5 + trailMesherFactor6 * trailMesherFactor6);
         float sqrt3 = (float)Math.sqrt(trailMesherFactor7 * trailMesherFactor7 + trailMesherFactor8 * trailMesherFactor8 + trailMesherFactor9 * trailMesherFactor9);
         float trailMesherFactor13;
         float trailMesherFactor14;
         float trailMesherFactor15;
         if (distance > 0.02F * sqrt2 * sqrt3 && distance > 1.0E-9F) {
            trailMesherFactor13 = trailMesherFactor10 / distance;
            trailMesherFactor14 = trailMesherFactor11 / distance;
            trailMesherFactor15 = trailMesherFactor12 / distance;
            if (trailMesherFlag && trailMesherFactor13 * trailMesherFactor + trailMesherFactor14 * trailMesherFactor2 + trailMesherFactor15 * trailMesherFactor3 < 0.0F) {
               trailMesherFactor13 = -trailMesherFactor13;
               trailMesherFactor14 = -trailMesherFactor14;
               trailMesherFactor15 = -trailMesherFactor15;
            }
         } else if (trailMesherFlag) {
            trailMesherFactor13 = trailMesherFactor;
            trailMesherFactor14 = trailMesherFactor2;
            trailMesherFactor15 = trailMesherFactor3;
         } else {
            trailMesherFactor13 = 1.0F;
            trailMesherFactor14 = 0.0F;
            trailMesherFactor15 = 0.0F;
         }

         this.sideNormalX[i] = trailMesherFactor13;
         this.sideNormalY[i] = trailMesherFactor14;
         this.sideNormalZ[i] = trailMesherFactor15;
         trailMesherFactor = trailMesherFactor13;
         trailMesherFactor2 = trailMesherFactor14;
         trailMesherFactor3 = trailMesherFactor15;
         trailMesherFlag = true;
         this.positiveSideFade[i] = 1.0F;
         this.negativeSideFade[i] = 1.0F;
         if (i > 0 && i < this.styleCount - 1 && this.trailMesherTable[i] > 1.0E-5F) {
            float trailMesherLimit = getAmount(
               trailMesherValues[i - 1], trailMesherValues2[i - 1], trailMesherValues3[i - 1], trailMesherValues[i], trailMesherValues2[i], trailMesherValues3[i], trailMesherValues[i + 1], trailMesherValues2[i + 1], trailMesherValues3[i + 1], trailMesherFactor13, trailMesherFactor14, trailMesherFactor15
            );
            float trailMesherLimit2 = this.trailMesherTable[i] * Math.abs(trailMesherLimit);
            if (trailMesherLimit2 > 0.95F) {
               float trailMesherValue = 0.95F / trailMesherLimit2;
               if (trailMesherLimit > 0.0F) {
                  this.positiveSideFade[i] = trailMesherValue;
               } else {
                  this.negativeSideFade[i] = trailMesherValue;
               }
            }
         }
      }

      float trailMesherBound = 1.0F;
      float wrapDegreesBound2 = 1.0F;

      for (int j = 0; j < this.styleCount; j++) {
         float trailMesherBound3 = this.positiveSideFade[j];
         float wrapDegreesBound = this.negativeSideFade[j];
         float hashHuntBound = j + 1 < this.styleCount ? this.positiveSideFade[j + 1] : 1.0F;
         float trailMesherBound6 = j + 1 < this.styleCount ? this.negativeSideFade[j + 1] : 1.0F;
         this.positiveSideFade[j] = Math.min(trailMesherBound3, Math.min(trailMesherBound, hashHuntBound));
         this.negativeSideFade[j] = Math.min(wrapDegreesBound, Math.min(wrapDegreesBound2, trailMesherBound6));
         trailMesherBound = trailMesherBound3;
         wrapDegreesBound2 = wrapDegreesBound;
      }
   }

   private int getAmountStyle(float styleValue) {
      int styleValue2;
      if (this.style.equals) {
         float styleValue3 = (float)(this.style.styleThemeAmount * 0.22 + styleValue * 0.42);
         float styleValue4 = styleValue3 - (float)Math.floor(styleValue3);
         styleValue2 = getValue(styleValue4, 0.72F, 1.0F);
      } else {
         float hashRegistryValue = this.styleAmount - 0.07F * styleValue;
         float styleValue6 = hashRegistryValue - (float)Math.floor(hashRegistryValue);
         styleValue2 = getValue(styleValue6, this.sinkBaseFactor, this.sinkTintStrength);
      }

      float trailMesherFactor = 0.15F * this.sinkIntensitySquared * (1.0F - styleValue) * (1.0F - styleValue);
      return getValueInt(styleValue2, trailMesherFactor);
   }

   private void handleAmounts(int trailMesherLimit, double trailMesherFactor) {
      for (int i = 0; i < trailMesherLimit; i++) {
         double trailMesherValue = this.pointX[0];
         double trailMesherValue2 = this.pointY[0];
         double trailMesherValue3 = this.pointZ[0];

         for (int j = 1; j < this.pointCount - 1; j++) {
            double trailMesherValue4 = this.pointX[j];
            double trailMesherValue5 = this.pointY[j];
            double trailMesherValue6 = this.pointZ[j];
            this.pointX[j] = trailMesherValue4 + trailMesherFactor * ((trailMesherValue + this.pointX[j + 1]) * 0.5 - trailMesherValue4);
            this.pointY[j] = trailMesherValue5 + trailMesherFactor * ((trailMesherValue2 + this.pointY[j + 1]) * 0.5 - trailMesherValue5);
            this.pointZ[j] = trailMesherValue6 + trailMesherFactor * ((trailMesherValue3 + this.pointZ[j + 1]) * 0.5 - trailMesherValue6);
            trailMesherValue = trailMesherValue4;
            trailMesherValue2 = trailMesherValue5;
            trailMesherValue3 = trailMesherValue6;
         }
      }
   }

   public void handleAmountSink(TrailBuffer trailBuffer, double amount, double sinkValue, double sinkValue2, double sinkValue3, TrailMesher.Style style2, TrailSink trailSink) {
      int pointCount4 = trailBuffer.getPointCount();
      if (pointCount4 != 0 && !(style2.trailLength <= 0.0)) {
         this.trailMesherAmount = amount;
         this.x = sinkValue;
         this.y = sinkValue2;
         this.z = sinkValue3;
         this.style = style2;
         this.trailSink = trailSink;
         float[] sinkValues = this.getAmountsBlendParams(style2.themeCustomValue);
         this.styleAmount = sinkValues[0];
         this.sinkBaseFactor = sinkValues[1];
         this.sinkTintStrength = sinkValues[2];
         float sinkFactor = style2.equals ? 0.72F : this.sinkBaseFactor;
         this.sinkIntensitySquared = sinkFactor * sinkFactor;
         this.sinkDetailFactor = 0.45F + 0.55F * sinkFactor;
         this.sinkLimit = Math.max(1, Math.min(400, style2.clamp2Clamp));
         this.sinkAmount = this.sinkLimit * 0.75F;
         this.sinkCount = 0;
         int maxClamp = Math.max(64, style2.clampSink);
         int sinkLimit = pointCount4;

         while (sinkLimit > 0) {
            int sinkCounter = sinkLimit - 1;

            while (sinkCounter > 0 && !trailBuffer.canFlags(sinkCounter)) {
               sinkCounter--;
            }

            this.segmentLimit = Math.min(this.sinkValue, maxClamp);
            if (this.segmentLimit < 24 || style2.mode == TrailMesher.Mode.DOTS && this.isMax()) {
               break;
            }

            this.handleStyleMax(trailBuffer, sinkCounter, sinkLimit, sinkLimit == pointCount4);
            maxClamp -= this.styleCount;
            sinkLimit = sinkCounter;
         }

         this.trailSink = null;
         this.style = null;
      }
   }

   static int getMin(int trailMesherBound) {
      return trailMesherBound < 0 ? 0 : Math.min(trailMesherBound, 255);
   }

   public void updateMax() {
      this.styleMaxCount = 0;
      this.totalSegmentCount = 0;
      this.sinkTrailCount = 0;
      this.emittedSegmentCount = 0;
      this.trailMesherCount = 0;
      this.maxTaper = 0.0F;
   }

   private void handleAmountsSink(float sinkFactor, float sinkFactor2, float sinkFactor3, float sinkFactor4, float sinkValue, int sinkValue2, float sinkFactor5, int styleValue, float sinkValue4) {
      float distance = (float)Math.sqrt(sinkFactor * sinkFactor + sinkFactor2 * sinkFactor2 + sinkFactor3 * sinkFactor3);
      if (!(distance < 1.0E-4F)) {
         float sinkFactor6 = -sinkFactor / distance;
         float sinkFactor7 = -sinkFactor2 / distance;
         float sinkFactor8 = -sinkFactor3 / distance;
         float sinkValue5 = sinkFactor8;
         float sinkFactor9 = -sinkFactor6;
         float sqrt2 = (float)Math.sqrt(sinkFactor8 * sinkFactor8 + sinkFactor9 * sinkFactor9);
         if (sqrt2 < 1.0E-4F) {
            sinkValue5 = 1.0F;
            sinkFactor9 = 0.0F;
            sqrt2 = 1.0F;
         }

         float sinkFactor10 = sinkValue5 / sqrt2;
         float sinkFactor11 = sinkFactor9 / sqrt2;
         float sinkFactor12 = sinkFactor7 * sinkFactor11;
         float sinkFactor13 = sinkFactor8 * sinkFactor10 - sinkFactor6 * sinkFactor11;
         float sinkFactor14 = -sinkFactor7 * sinkFactor10;
         int sinkValue6 = getValue(getValueInt(sinkValue2, 0.12F * this.sinkIntensitySquared), sinkValue * 0.9F);
         int sinkValue7 = getValue(sinkValue2, 0.0F);
         int valueForIntFloat = getValueForIntFloat(getValueInt(sinkValue2, 0.1F * this.sinkIntensitySquared), sinkValue * 0.7F * this.sinkDetailFactor);
         float min15 = Math.min(sinkFactor4 * 2.4F, Math.max(sinkFactor4 * 1.2F, sinkFactor5 * distance));
         int index = 0;

         while (index < 14) {
            int index2 = index + styleValue;
            float sinkFactor16 = (index & 1) == 0 ? 1.0F : sinkValue4;
            float sinkFactor17 = (index2 & 1) == 0 ? 1.0F : sinkValue4;
            float sinkFactor18 = sinkTable[index] * sinkFactor16;
            float sinkFactor19 = SIN_TABLE[index] * sinkFactor16;
            float sinkFactor20 = sinkTable[index2] * sinkFactor17;
            float sinkFactor21 = SIN_TABLE[index2] * sinkFactor17;
            float sinkValue9 = sinkFactor + (sinkFactor10 * sinkFactor18 + sinkFactor12 * sinkFactor19) * sinkFactor4;
            float sinkValue10 = sinkFactor2 + sinkFactor13 * sinkFactor19 * sinkFactor4;
            float sinkValue11 = sinkFactor3 + (sinkFactor11 * sinkFactor18 + sinkFactor14 * sinkFactor19) * sinkFactor4;
            float sinkValue12 = sinkFactor + (sinkFactor10 * sinkFactor20 + sinkFactor12 * sinkFactor21) * sinkFactor4;
            float sinkValue13 = sinkFactor2 + sinkFactor13 * sinkFactor21 * sinkFactor4;
            float sinkValue14 = sinkFactor3 + (sinkFactor11 * sinkFactor20 + sinkFactor14 * sinkFactor21) * sinkFactor4;
            this.trailSink.handleConsumerVertex(sinkFactor, sinkFactor2, sinkFactor3, sinkValue6, sinkValue9, sinkValue10, sinkValue11, sinkValue7, sinkValue12, sinkValue13, sinkValue14, sinkValue7, sinkFactor, sinkFactor2, sinkFactor3, sinkValue6);
            this.emittedSegmentCount++;
            float sinkValue15 = sinkFactor + (sinkFactor10 * sinkFactor18 + sinkFactor12 * sinkFactor19) * min15;
            float sinkValue16 = sinkFactor2 + sinkFactor13 * sinkFactor19 * min15;
            float sinkValue17 = sinkFactor3 + (sinkFactor11 * sinkFactor18 + sinkFactor14 * sinkFactor19) * min15;
            float sinkValue18 = sinkFactor + (sinkFactor10 * sinkFactor20 + sinkFactor12 * sinkFactor21) * min15;
            float sinkValue19 = sinkFactor2 + sinkFactor13 * sinkFactor21 * min15;
            float sinkValue20 = sinkFactor3 + (sinkFactor11 * sinkFactor20 + sinkFactor14 * sinkFactor21) * min15;
            this.trailSink.handleConsumerVertex(sinkFactor, sinkFactor2, sinkFactor3, valueForIntFloat, sinkValue15, sinkValue16, sinkValue17, -16777216, sinkValue18, sinkValue19, sinkValue20, -16777216);
            this.sinkTrailCount++;
            index += styleValue;
         }
      }
   }

   public TrailMesher() {
      this(384);
   }

   static double getAmount(double trailMesherValue, double trailBufferValue, double trailMesherFactor) {
      return trailMesherValue + (trailBufferValue - trailMesherValue) * trailMesherFactor;
   }

   private void handleStyleMax(TrailBuffer trailBuffer, int styleValue, int styleLimit, boolean styleFlag) {
      this.styleCount = 0;
      double deltaAmount = this.trailMesherAmount - this.style.trailLength;
      int styleCounter = styleValue;

      while (styleCounter < styleLimit && trailBuffer.getAmounts(styleCounter) <= deltaAmount) {
         styleCounter++;
      }

      boolean styleFlag2 = styleFlag && trailBuffer.isFlag() && trailBuffer.getAmount() > deltaAmount;
      if (styleCounter != styleLimit || styleFlag2) {
         this.pointCount = 0;
         boolean styleFlag3 = styleCounter > styleValue;
         if (styleFlag3) {
            int styleValue2 = styleCounter - 1;
            double amountsPointXs3;
            double amountsHeightTable4;
            double trailBufferValue;
            double amounts6;
            float amountsForInt7;
            double amountsPointTimestamps8;
            if (styleCounter < styleLimit) {
               amountsPointXs3 = trailBuffer.getAmountsPointXs(styleCounter);
               amountsHeightTable4 = trailBuffer.getAmountsHeightTable(styleCounter);
               trailBufferValue = trailBuffer.getAmountsLerpTable(styleCounter);
               amounts6 = trailBuffer.getAmounts(styleCounter);
               amountsForInt7 = trailBuffer.getAmountsForInt(styleCounter);
               amountsPointTimestamps8 = trailBuffer.getAmountsPointTimestamps(styleCounter);
            } else {
               amountsPointXs3 = trailBuffer.getLerp();
               amountsHeightTable4 = trailBuffer.getLerp2LerpHeight();
               trailBufferValue = trailBuffer.getLerp3LerpHeight();
               amounts6 = trailBuffer.getAmount();
               amountsForInt7 = trailBuffer.getHeight();
               amountsPointTimestamps8 = trailBuffer.getAmountsPointTimestamps(styleValue2)
                  + TrailBuffer.getAmount(amountsPointXs3, amountsHeightTable4, trailBufferValue, trailBuffer.getAmountsPointXs(styleValue2), trailBuffer.getAmountsHeightTable(styleValue2), trailBuffer.getAmountsLerpTable(styleValue2));
            }

            double styleDivisor = amounts6 - trailBuffer.getAmounts(styleValue2);
            double trailMesherFactor = styleDivisor <= 1.0E-9 ? 1.0 : getAmount((deltaAmount - trailBuffer.getAmounts(styleValue2)) / styleDivisor);
            this.handleAmounts(
               getAmount(trailBuffer.getAmountsPointXs(styleValue2), amountsPointXs3, trailMesherFactor),
               getAmount(trailBuffer.getAmountsHeightTable(styleValue2), amountsHeightTable4, trailMesherFactor),
               getAmount(trailBuffer.getAmountsLerpTable(styleValue2), trailBufferValue, trailMesherFactor),
               deltaAmount,
               (float)getAmount((double)trailBuffer.getAmountsForInt(styleValue2), (double)amountsForInt7, trailMesherFactor),
               getAmount(trailBuffer.getAmountsPointTimestamps(styleValue2), amountsPointTimestamps8, trailMesherFactor),
               false
            );
         }

         double min2 = getMin(this.style.styleAmount);
         if (this.segmentLimit < this.sinkValue) {
            int styleValue10 = styleFlag3 ? styleCounter - 1 : styleValue;
            double deltaAmounts = trailBuffer.getAmountsPointTimestamps(styleLimit - 1) - trailBuffer.getAmountsPointTimestamps(styleValue10);
            if (styleFlag2) {
               deltaAmounts += TrailBuffer.getAmount(
                  trailBuffer.getLerp(), trailBuffer.getLerp2LerpHeight(), trailBuffer.getLerp3LerpHeight(), trailBuffer.getAmountsPointXs(styleLimit - 1), trailBuffer.getAmountsHeightTable(styleLimit - 1), trailBuffer.getAmountsLerpTable(styleLimit - 1)
               );
            }

            double styleValue11 = this.segmentLimit * 0.5 - 4.0;

            while (deltaAmounts / min2 > styleValue11 && min2 < 10000.0) {
               min2 *= 2.0;
            }
         }

         for (int i = styleFlag3 ? styleCounter : styleValue; i < styleLimit; i++) {
            boolean trailMesherFlag = !styleFlag2 && i == styleLimit - 1;
            boolean styleFlag5 = i == styleValue && !styleFlag3 || trailMesherFlag;
            boolean styleFlag6 = i > styleValue && Math.floor(trailBuffer.getAmountsPointTimestamps(i) / min2) != Math.floor(trailBuffer.getAmountsPointTimestamps(i - 1) / min2);
            if (styleFlag5 || styleFlag6) {
               this.handleAmounts(
                  trailBuffer.getAmountsPointXs(i),
                  trailBuffer.getAmountsHeightTable(i),
                  trailBuffer.getAmountsLerpTable(i),
                  trailBuffer.getAmounts(i),
                  trailBuffer.getAmountsForInt(i),
                  trailBuffer.getAmountsPointTimestamps(i),
                  trailMesherFlag
               );
            }
         }

         if (styleFlag2) {
            int styleValue12 = styleLimit - 1;
            double styleValue13 = trailBuffer.getAmountsPointTimestamps(styleValue12)
               + TrailBuffer.getAmount(
                  trailBuffer.getLerp(), trailBuffer.getLerp2LerpHeight(), trailBuffer.getLerp3LerpHeight(), trailBuffer.getAmountsPointXs(styleValue12), trailBuffer.getAmountsHeightTable(styleValue12), trailBuffer.getAmountsLerpTable(styleValue12)
               );
            this.handleAmounts(trailBuffer.getLerp(), trailBuffer.getLerp2LerpHeight(), trailBuffer.getLerp3LerpHeight(), trailBuffer.getAmount(), trailBuffer.getHeight(), styleValue13, true);
         }

         if (this.pointCount >= 2) {
            this.handleAmounts(2, 0.5);
            this.updateAmountsMin();
            if (this.styleCount >= 2) {
               this.handleAmountsStyle(styleFlag3, styleFlag && styleFlag2);
               this.styleMaxCount++;
               this.totalSegmentCount = this.totalSegmentCount + this.styleCount;
               this.maxTaper = Math.max(this.maxTaper, Math.max(this.segmentTaper[0], this.segmentTaper[this.styleCount - 1]));
               switch (this.style.mode) {
                  case RIBBON:
                     this.updateAmountsStyle();
                     break;
                  case LINE:
                     this.updateAmountsStyle2();
                     break;
                  case DOTS:
                     this.updateAmountsStyle3();
               }
            }
         }
      }
   }

   public int getTrailMesherCount() {
      return this.trailMesherCount;
   }

   private void handleAmounts(double trailMesherValue, double trailMesherValue2, double trailMesherValue3, double deltaAmount, float trailMesherValue5, double trailMesherValue6, boolean trailMesherFlag) {
      double trailBufferValue = trailMesherValue - this.x;
      double trailMesherValue8 = trailMesherValue2 - this.y;
      double trailMesherValue9 = trailMesherValue3 - this.z;
      if (this.pointCount > 0) {
         int index = this.pointCount - 1;
         if (TrailBuffer.getAmount(trailBufferValue, trailMesherValue8, trailMesherValue9, this.pointX[index], this.pointY[index], this.pointZ[index]) < 0.015) {
            if (trailMesherFlag && this.pointCount > 1) {
               this.handleAmounts(index, trailBufferValue, trailMesherValue8, trailMesherValue9, deltaAmount, trailMesherValue5, trailMesherValue6);
            }

            return;
         }
      }

      if (this.pointCount >= this.trailMesherValue) {
         if (trailMesherFlag) {
            this.handleAmounts(this.pointCount - 1, trailBufferValue, trailMesherValue8, trailMesherValue9, deltaAmount, trailMesherValue5, trailMesherValue6);
         }
      } else {
         this.handleAmounts(this.pointCount++, trailBufferValue, trailMesherValue8, trailMesherValue9, deltaAmount, trailMesherValue5, trailMesherValue6);
      }
   }

   public static float getAmount(float trailMesherValue, float trailMesherValue2, float trailMesherValue3, float trailMesherFactor, float trailMesherFactor2, float trailMesherFactor3, float trailMesherValue4, float trailMesherValue5, float trailMesherValue6, float trailMesherFactor4, float trailMesherFactor5, float trailMesherFactor6) {
      float distance = (float)Math.sqrt(trailMesherFactor * trailMesherFactor + trailMesherFactor2 * trailMesherFactor2 + trailMesherFactor3 * trailMesherFactor3);
      float trailMesherFactor7 = 0.0F;
      float trailMesherFactor8 = 0.0F;
      float trailMesherFactor9 = 0.0F;
      if (distance > 1.0E-6F) {
         trailMesherFactor7 = trailMesherFactor / distance;
         trailMesherFactor8 = trailMesherFactor2 / distance;
         trailMesherFactor9 = trailMesherFactor3 / distance;
      }

      float trailMesherFactor10 = trailMesherFactor - trailMesherValue;
      float trailMesherFactor11 = trailMesherFactor2 - trailMesherValue2;
      float trailMesherFactor12 = trailMesherFactor3 - trailMesherValue3;
      float trailMesherFactor13 = trailMesherFactor10 * trailMesherFactor7 + trailMesherFactor11 * trailMesherFactor8 + trailMesherFactor12 * trailMesherFactor9;
      float trailMesherFactor14 = trailMesherFactor10 - trailMesherFactor13 * trailMesherFactor7;
      float trailMesherFactor15 = trailMesherFactor11 - trailMesherFactor13 * trailMesherFactor8;
      float trailMesherFactor16 = trailMesherFactor12 - trailMesherFactor13 * trailMesherFactor9;
      float trailMesherFactor17 = trailMesherValue4 - trailMesherFactor;
      float trailMesherFactor18 = trailMesherValue5 - trailMesherFactor2;
      float trailMesherFactor19 = trailMesherValue6 - trailMesherFactor3;
      float trailMesherFactor20 = trailMesherFactor17 * trailMesherFactor7 + trailMesherFactor18 * trailMesherFactor8 + trailMesherFactor19 * trailMesherFactor9;
      float trailMesherFactor21 = trailMesherFactor17 - trailMesherFactor20 * trailMesherFactor7;
      float trailMesherFactor22 = trailMesherFactor18 - trailMesherFactor20 * trailMesherFactor8;
      float trailMesherFactor23 = trailMesherFactor19 - trailMesherFactor20 * trailMesherFactor9;
      float sqrt2 = (float)Math.sqrt(trailMesherFactor14 * trailMesherFactor14 + trailMesherFactor15 * trailMesherFactor15 + trailMesherFactor16 * trailMesherFactor16);
      float sqrt3 = (float)Math.sqrt(trailMesherFactor21 * trailMesherFactor21 + trailMesherFactor22 * trailMesherFactor22 + trailMesherFactor23 * trailMesherFactor23);
      if (!(sqrt2 < 1.0E-6F) && !(sqrt3 < 1.0E-6F)) {
         float trailMesherFactor24 = 2.0F / (sqrt2 + sqrt3);
         float trailMesherFactor25 = (trailMesherFactor21 / sqrt3 - trailMesherFactor14 / sqrt2) * trailMesherFactor24;
         float trailMesherFactor26 = (trailMesherFactor22 / sqrt3 - trailMesherFactor15 / sqrt2) * trailMesherFactor24;
         float trailMesherFactor27 = (trailMesherFactor23 / sqrt3 - trailMesherFactor16 / sqrt2) * trailMesherFactor24;
         return trailMesherFactor25 * trailMesherFactor4 + trailMesherFactor26 * trailMesherFactor5 + trailMesherFactor27 * trailMesherFactor6;
      } else {
         return 0.0F;
      }
   }

   float getAmounts(int index) {
      return this.segmentAlpha[index];
   }

   public static int getValue(int trailMesherValue, float trailMesherLimit) {
      int min3 = getMin(Math.round(getAmount(trailMesherLimit) * 255.0F));
      return min3 << 24 | trailMesherValue & 16777215;
   }

   public static int getValueForIntFloat(int trailMesherValue, float trailMesherFactor) {
      if (trailMesherFactor <= 0.0F) {
         return -16777216;
      } else {
         int min2 = getMin(Math.round((trailMesherValue >> 16 & 0xFF) * trailMesherFactor));
         int min3 = getMin(Math.round((trailMesherValue >> 8 & 0xFF) * trailMesherFactor));
         int min4 = getMin(Math.round((trailMesherValue & 0xFF) * trailMesherFactor));
         return 0xFF000000 | min2 << 16 | min3 << 8 | min4;
      }
   }

   private boolean isStyleAmount() {
      return this.style.clampAmount >= 0.99F;
   }

   private static int getValue(int trailMesherValue, int j, int trailMesherValue3, float trailMesherLimit) {
      return j == trailMesherValue3 && trailMesherLimit > 0.0F ? getValueInt(trailMesherValue, trailMesherLimit) : trailMesherValue;
   }

   public static void handle(
      double trailMesherValue,
      double trailMesherValue2,
      double trailMesherValue3,
      double trailMesherFactor,
      double trailMesherFactor2,
      double trailMesherFactor3,
      double trailBufferValue,
      double trailMesherFactor5,
      double trailMesherFactor6,
      double trailMesherValue4,
      double trailMesherValue5,
      double trailMesherValue6,
      double trailMesherFactor7,
      double[] trailMesherValues
   ) {
      double sqrt = Math.sqrt(TrailBuffer.getAmount(trailMesherFactor, trailMesherFactor2, trailMesherFactor3, trailBufferValue, trailMesherFactor5, trailMesherFactor6));
      if (sqrt < 1.0E-6) {
         trailMesherValues[0] = trailMesherFactor + (trailBufferValue - trailMesherFactor) * trailMesherFactor7;
         trailMesherValues[1] = trailMesherFactor2 + (trailMesherFactor5 - trailMesherFactor2) * trailMesherFactor7;
         trailMesherValues[2] = trailMesherFactor3 + (trailMesherFactor6 - trailMesherFactor3) * trailMesherFactor7;
      } else {
         double sqrt2 = Math.sqrt(TrailBuffer.getAmount(trailMesherValue, trailMesherValue2, trailMesherValue3, trailMesherFactor, trailMesherFactor2, trailMesherFactor3));
         double sqrt3 = Math.sqrt(TrailBuffer.getAmount(trailBufferValue, trailMesherFactor5, trailMesherFactor6, trailMesherValue4, trailMesherValue5, trailMesherValue6));
         if (sqrt2 < 1.0E-6) {
            sqrt2 = sqrt;
         }

         if (sqrt3 < 1.0E-6) {
            sqrt3 = sqrt;
         }

         double trailMesherFactor8 = ((trailMesherFactor - trailMesherValue) / sqrt2 - (trailBufferValue - trailMesherValue) / (sqrt2 + sqrt) + (trailBufferValue - trailMesherFactor) / sqrt) * sqrt;
         double trailMesherFactor9 = ((trailMesherFactor2 - trailMesherValue2) / sqrt2 - (trailMesherFactor5 - trailMesherValue2) / (sqrt2 + sqrt) + (trailMesherFactor5 - trailMesherFactor2) / sqrt) * sqrt;
         double trailMesherFactor10 = ((trailMesherFactor3 - trailMesherValue3) / sqrt2 - (trailMesherFactor6 - trailMesherValue3) / (sqrt2 + sqrt) + (trailMesherFactor6 - trailMesherFactor3) / sqrt) * sqrt;
         double trailMesherFactor11 = ((trailBufferValue - trailMesherFactor) / sqrt - (trailMesherValue4 - trailMesherFactor) / (sqrt + sqrt3) + (trailMesherValue4 - trailBufferValue) / sqrt3) * sqrt;
         double trailMesherFactor12 = ((trailMesherFactor5 - trailMesherFactor2) / sqrt - (trailMesherValue5 - trailMesherFactor2) / (sqrt + sqrt3) + (trailMesherValue5 - trailMesherFactor5) / sqrt3) * sqrt;
         double trailMesherFactor13 = ((trailMesherFactor6 - trailMesherFactor3) / sqrt - (trailMesherValue6 - trailMesherFactor3) / (sqrt + sqrt3) + (trailMesherValue6 - trailMesherFactor6) / sqrt3) * sqrt;
         double trailMesherFactor14 = trailMesherFactor7 * trailMesherFactor7;
         double trailMesherFactor15 = trailMesherFactor14 * trailMesherFactor7;
         double trailMesherFactor16 = 2.0 * trailMesherFactor15 - 3.0 * trailMesherFactor14 + 1.0;
         double trailMesherFactor17 = trailMesherFactor15 - 2.0 * trailMesherFactor14 + trailMesherFactor7;
         double trailMesherFactor18 = -2.0 * trailMesherFactor15 + 3.0 * trailMesherFactor14;
         double deltaVar = trailMesherFactor15 - trailMesherFactor14;
         trailMesherValues[0] = trailMesherFactor16 * trailMesherFactor + trailMesherFactor17 * trailMesherFactor8 + trailMesherFactor18 * trailBufferValue + deltaVar * trailMesherFactor11;
         trailMesherValues[1] = trailMesherFactor16 * trailMesherFactor2 + trailMesherFactor17 * trailMesherFactor9 + trailMesherFactor18 * trailMesherFactor5 + deltaVar * trailMesherFactor12;
         trailMesherValues[2] = trailMesherFactor16 * trailMesherFactor3 + trailMesherFactor17 * trailMesherFactor10 + trailMesherFactor18 * trailMesherFactor6 + deltaVar * trailMesherFactor13;
      }
   }

   float getAmountsForInt(int index) {
      return this.rawX[index];
   }

   float getAmountsSegmentTaper(int index) {
      return this.segmentTaper[index];
   }

   private void handleSinkTrail(
      float sinkTrailValue, float sinkTrailValue2, float sinkTrailValue3, int valueForIntFloat, float sinkTrailValue5, float sinkTrailValue6, float sinkTrailValue7, int sinkTrailValue8, float sinkTrailValue9, float sinkTrailValue10, float sinkTrailValue11, int sinkTrailValue12, float sinkFactor, float sinkTrailValue14, float sinkTrailValue15, int sinkTrailValue16
   ) {
      if ((valueForIntFloat | sinkTrailValue8 | sinkTrailValue12 | sinkTrailValue16) != -16777216) {
         this.trailSink.handleConsumerVertex(sinkTrailValue, sinkTrailValue2, sinkTrailValue3, valueForIntFloat, sinkTrailValue5, sinkTrailValue6, sinkTrailValue7, sinkTrailValue8, sinkTrailValue9, sinkTrailValue10, sinkTrailValue11, sinkTrailValue12);
         this.trailSink.handleConsumerVertex(sinkTrailValue, sinkTrailValue2, sinkTrailValue3, valueForIntFloat, sinkTrailValue9, sinkTrailValue10, sinkTrailValue11, sinkTrailValue12, sinkFactor, sinkTrailValue14, sinkTrailValue15, sinkTrailValue16);
         this.sinkTrailCount += 2;
      }
   }

   float getAmountsRawZ(int index) {
      return this.rawZ[index];
   }

   private void updateAmountsStyle() {
      boolean styleAmount = this.isStyleAmount();
      float styleFactor = 1.0F - getAmount(this.style.clampAmount);
      float styleFactor2 = 0.22F * this.sinkDetailFactor * styleFactor;
      float max3 = Math.max(0.08F, getAmount(this.style.widthScale));

      for (int i = 0; i < this.styleCount; i++) {
         float styleValue = max3 * this.segmentHeight[i];
         float styleValue2 = 0.03F + styleValue * 0.5F;
         float styleValue3 = styleValue * 0.5F * this.segmentTaper[i];
         this.renderX[i] = this.rawX[i];
         this.styleTable[i] = this.rawZ[i];
         this.renderUpperY[i] = this.rawY[i] + styleValue2 + styleValue3;
         this.renderLowerY[i] = this.rawY[i] + styleValue2 - styleValue3;
      }

      for (int j = 0; j < this.styleCount - 1; j++) {
         float styleFactor4 = this.segmentAlpha[j];
         float styleFactor5 = this.segmentAlpha[j + 1];
         if ((!(styleFactor4 < 0.004F) || !(styleFactor5 < 0.004F)) && (!(this.segmentTaper[j] < 0.001F) || !(this.segmentTaper[j + 1] < 0.001F))) {
            int styleValue4 = this.segmentColors[j];
            int styleValue5 = this.segmentColors[j + 1];
            this.trailSink
               .handleConsumerVertex(
                  this.renderX[j],
                  this.renderLowerY[j],
                  this.styleTable[j],
                  getValue(styleValue4, styleFactor4 * 0.05F),
                  this.renderX[j],
                  this.renderUpperY[j],
                  this.styleTable[j],
                  getValue(styleValue4, styleFactor4 * 0.55F),
                  this.renderX[j + 1],
                  this.renderUpperY[j + 1],
                  this.styleTable[j + 1],
                  getValue(styleValue5, styleFactor5 * 0.55F),
                  this.renderX[j + 1],
                  this.renderLowerY[j + 1],
                  this.styleTable[j + 1],
                  getValue(styleValue5, styleFactor5 * 0.05F)
               );
            this.emittedSegmentCount++;
            if (!styleAmount) {
               this.handleSinkTrail(
                  this.renderX[j],
                  this.renderLowerY[j],
                  this.styleTable[j],
                  -16777216,
                  this.renderX[j],
                  this.renderUpperY[j],
                  this.styleTable[j],
                  getValueForIntFloat(styleValue4, styleFactor4 * styleFactor2),
                  this.renderX[j + 1],
                  this.renderUpperY[j + 1],
                  this.styleTable[j + 1],
                  getValueForIntFloat(styleValue5, styleFactor5 * styleFactor2),
                  this.renderX[j + 1],
                  this.renderLowerY[j + 1],
                  this.styleTable[j + 1],
                  -16777216
               );
            }
         }
      }

      float styleFactor6 = this.style.styleAmount * 0.5F;
      float trailMesherFactor = this.getStyleAmount(0.035F);

      for (int k = 0; k < this.styleCount; k++) {
         this.trailMesherTable[k] = styleFactor6 * 0.4F * (float)Math.sqrt(this.segmentTaper[k]);
      }

      this.handleAmountsAmount(this.renderX, this.renderUpperY, this.styleTable, FLAT_PROFILE_OFFSETS, FLAT_PROFILE_WEIGHTS, 0.8F, false, 0.0F);

      for (int m = 0; m < this.styleCount; m++) {
         this.trailMesherTable[m] = this.getAmounts(m, styleFactor6 * (float)Math.sqrt(this.segmentTaper[m]), trailMesherFactor);
      }

      this.handleAmountsAmount(this.renderX, this.renderUpperY, this.styleTable, ROUND_PROFILE_OFFSETS, ROUND_PROFILE_WEIGHTS, 0.8F, true, 0.25F);
      if (!styleAmount) {
         for (int p = 0; p < this.styleCount; p++) {
            this.renderUpperY[p] = this.renderLowerY[p];
            this.trailMesherTable[p] = this.getAmounts(p, styleFactor6 * 0.7F * (float)Math.sqrt(this.segmentTaper[p]), trailMesherFactor);
         }

         this.handleAmountsAmount(this.renderX, this.renderUpperY, this.styleTable, ROUND_PROFILE_OFFSETS, ROUND_PROFILE_WEIGHTS, 0.28F * styleFactor, true, 0.0F);
      }
   }

   public int getValue() {
      return this.totalSegmentCount;
   }

   public static float getAmount(float trailMesherValue, float trailMesherLimit) {
      float trailMesherFactor = getAmount(trailMesherLimit);
      float trailMesherValue3 = getAmount(trailMesherValue);
      float trailMesherFactor2 = (1.0F - trailMesherValue3) * (1.0F - trailMesherValue3 * 0.5F);
      return 1.0F - trailMesherFactor + trailMesherFactor * trailMesherFactor2;
   }

   private void updateAmountsStyle2() {
      float styleFactor = getAmount(this.style.widthScale);

      for (int i = 0; i < this.styleCount; i++) {
         this.renderX[i] = this.rawX[i];
         this.renderUpperY[i] = this.rawY[i] + 0.03F + styleFactor * this.segmentHeight[i];
         this.styleTable[i] = this.rawZ[i];
      }

      float styleValue = this.style.styleAmount * 0.5F;

      for (int j = 0; j < this.styleCount; j++) {
         this.trailMesherTable[j] = styleValue * 0.5F * this.segmentTaper[j];
      }

      this.handleAmountsAmount(this.renderX, this.renderUpperY, this.styleTable, FLAT_PROFILE_OFFSETS, FLAT_PROFILE_WEIGHTS, 0.85F, false, 0.0F);
      float styleAmount = this.getStyleAmount(0.035F);

      for (int k = 0; k < this.styleCount; k++) {
         this.trailMesherTable[k] = this.getAmounts(k, styleValue * 1.5F * this.segmentTaper[k], styleAmount);
      }

      this.handleAmountsAmount(this.renderX, this.renderUpperY, this.styleTable, ROUND_PROFILE_OFFSETS, ROUND_PROFILE_WEIGHTS, 0.95F, true, 0.4F);
   }

   public static int getValue(float trailMesherValue, float trailMesherFactor, float trailMesherFactor2) {
      float trailMesherValue2 = (trailMesherValue - (float)Math.floor(trailMesherValue)) * 6.0F;
      int trailMesherValue3 = (int)trailMesherValue2;
      float deltaVar = trailMesherValue2 - trailMesherValue3;
      float trailMesherValue4 = trailMesherFactor2 * (1.0F - trailMesherFactor);
      float trailMesherValue5 = trailMesherFactor2 * (1.0F - trailMesherFactor * deltaVar);
      float trailMesherValue6 = trailMesherFactor2 * (1.0F - trailMesherFactor * (1.0F - deltaVar));
      float trailMesherValue7;
      float trailMesherValue8;
      float trailMesherValue9;
      switch (trailMesherValue3) {
         case 0:
            trailMesherValue7 = trailMesherFactor2;
            trailMesherValue8 = trailMesherValue6;
            trailMesherValue9 = trailMesherValue4;
            break;
         case 1:
            trailMesherValue7 = trailMesherValue5;
            trailMesherValue8 = trailMesherFactor2;
            trailMesherValue9 = trailMesherValue4;
            break;
         case 2:
            trailMesherValue7 = trailMesherValue4;
            trailMesherValue8 = trailMesherFactor2;
            trailMesherValue9 = trailMesherValue6;
            break;
         case 3:
            trailMesherValue7 = trailMesherValue4;
            trailMesherValue8 = trailMesherValue5;
            trailMesherValue9 = trailMesherFactor2;
            break;
         case 4:
            trailMesherValue7 = trailMesherValue6;
            trailMesherValue8 = trailMesherValue4;
            trailMesherValue9 = trailMesherFactor2;
            break;
         default:
            trailMesherValue7 = trailMesherFactor2;
            trailMesherValue8 = trailMesherValue4;
            trailMesherValue9 = trailMesherValue5;
      }

      return getMin(Math.round(trailMesherValue7 * 255.0F)) << 16 | getMin(Math.round(trailMesherValue8 * 255.0F)) << 8 | getMin(Math.round(trailMesherValue9 * 255.0F));
   }

   public int getStyleMaxCount() {
      return this.styleMaxCount;
   }

   public float getMax() {
      return this.maxTaper;
   }

   public static int getValueInt(int trailMesherValue, float trailMesherFactor) {
      if (trailMesherFactor <= 0.0F) {
         return trailMesherValue & 16777215;
      } else {
         int trailMesherValue2 = trailMesherValue >> 16 & 0xFF;
         int trailMesherValue3 = trailMesherValue >> 8 & 0xFF;
         int trailMesherValue4 = trailMesherValue & 0xFF;
         int round = Math.round(trailMesherValue2 + (255 - trailMesherValue2) * trailMesherFactor);
         int round6 = Math.round(trailMesherValue3 + (255 - trailMesherValue3) * trailMesherFactor);
         int trailMesherBound = Math.round(trailMesherValue4 + (255 - trailMesherValue4) * trailMesherFactor);
         return getMin(round) << 16 | getMin(round6) << 8 | getMin(trailMesherBound);
      }
   }

   public static float getAmount(boolean trailMesherFlag, float trailMesherFactor, float trailMesherFactor2, float trailMesherFactor3, float trailMesherFactor4, float planValuesItem, float trailMesherFactor6, float trailMesherValue) {
      float trailMesherFactor7 = trailMesherFactor2 * trailMesherFactor6 - trailMesherFactor3 * planValuesItem;
      float trailMesherFactor8 = trailMesherFactor3 * trailMesherFactor4 - trailMesherFactor * trailMesherFactor6;
      float trailMesherFactor9 = trailMesherFactor * planValuesItem - trailMesherFactor2 * trailMesherFactor4;
      float distance = (float)Math.sqrt(trailMesherFactor7 * trailMesherFactor7 + trailMesherFactor8 * trailMesherFactor8 + trailMesherFactor9 * trailMesherFactor9);
      float amount2 = getAmount2(0.08F, 0.45F, distance);
      if (trailMesherFlag) {
         float sqrt2 = (float)Math.sqrt(trailMesherFactor * trailMesherFactor + trailMesherFactor3 * trailMesherFactor3);
         if (sqrt2 < 1.0E-4F) {
            return 1.0F;
         }

         float trailMesherValue2 = Math.abs(trailMesherFactor3 * trailMesherFactor4 - trailMesherFactor * trailMesherFactor6) / sqrt2;
         float trailMesherFactor11 = getAmount2(0.6F, 0.9F, Math.abs(planValuesItem)) * getAmount2(3.0F, 6.0F, trailMesherValue);
         amount2 = Math.max(getAmount2(0.04F, 0.3F, trailMesherValue2), amount2 * trailMesherFactor11);
      }

      return 0.12F + 0.88F * amount2;
   }

   static float getAmount(float trailMesherLimit) {
      return trailMesherLimit < 0.0F ? 0.0F : (trailMesherLimit > 1.0F ? 1.0F : trailMesherLimit);
   }

   float getAmountsRawY(int index) {
      return this.rawY[index];
   }

   static double getAmount(double trailMesherLimit) {
      return trailMesherLimit < 0.0 ? 0.0 : (trailMesherLimit > 1.0 ? 1.0 : trailMesherLimit);
   }

   private void updateAmountsStyle3() {
      float styleFactor = getAmount(this.style.widthScale);
      double max2 = Math.max(0.18, this.style.styleAmount * 1.5);
      float styleFactor3 = this.style.styleAmount * 0.5F;
      float styleAmount = this.getStyleAmount(0.04F);
      int styleValue = this.isStyleAmount() ? 2 : 1;
      float styleValue2 = 1.0F - (1.0F - MAX_BEND_COS) * getAmount(this.style.clampAmount / 0.99F);

      for (int i = this.styleCount - 2; i >= 0; i--) {
         double styleLimit = this.segmentTimes[i];
         double styleValue3 = this.segmentTimes[i + 1];
         if (!(styleValue3 <= styleLimit)) {
            double floor = Math.floor(styleValue3 / max2);

            while (floor * max2 > styleLimit) {
               if (this.isMax()) {
                  return;
               }

               float maxAmount = this.getMaxAmount();
               float trailMesherFactor = (float)((floor * max2 - styleLimit) / (styleValue3 - styleLimit));
               floor--;
               float styleValue5 = getAmount(this.segmentTaper[i], this.segmentTaper[i + 1], trailMesherFactor);
               float styleValue6 = getAmount(this.segmentAlpha[i], this.segmentAlpha[i + 1], trailMesherFactor) * maxAmount;
               if (!(styleValue5 < 0.02F) && !(styleValue6 < 0.004F)) {
                  float sinkFactor = getAmount(this.rawX[i], this.rawX[i + 1], trailMesherFactor);
                  float styleValue8 = getAmount(this.rawY[i], this.rawY[i + 1], trailMesherFactor)
                     + 0.03F
                     + styleFactor * getAmount(this.segmentHeight[i], this.segmentHeight[i + 1], trailMesherFactor);
                  float styleValue9 = getAmount(this.rawZ[i], this.rawZ[i + 1], trailMesherFactor);
                  int styleValue10 = trailMesherFactor < 0.5F ? this.segmentColors[i] : this.segmentColors[i + 1];
                  this.handleAmountsSink(sinkFactor, styleValue8, styleValue9, styleFactor3 * (0.3F + 0.7F * styleValue5), styleValue6, styleValue10, styleAmount, styleValue, styleValue2);
                  this.sinkCount++;
                  this.trailMesherCount++;
               }
            }
         }
      }
   }

   private float getAmountsStyle(int index) {
      float styleBound = getAmount(this.style.widthScale);
      float styleFactor;
      if (this.style.mode == TrailMesher.Mode.RIBBON) {
         float styleLimit = this.rawY[index] + 0.03F;
         float styleValue = styleLimit + Math.max(0.08F, styleBound) * this.segmentHeight[index];
         styleFactor = styleLimit > 0.0F ? styleLimit : (styleValue < 0.0F ? styleValue : 0.0F);
      } else {
         styleFactor = this.rawY[index] + 0.03F + styleBound * this.segmentHeight[index];
      }

      return (float)Math.sqrt(this.rawX[index] * this.rawX[index] + styleFactor * styleFactor + this.rawZ[index] * this.rawZ[index]);
   }

   private void handleAmounts(int index, double trailMesherValue, double trailMesherValue2, double trailBufferValue, double deltaAmount, float trailMesherValue5, double trailMesherValue6) {
      this.pointX[index] = trailMesherValue;
      this.pointY[index] = trailMesherValue2;
      this.pointZ[index] = trailBufferValue;
      this.tailX[index] = deltaAmount;
      this.tailY[index] = trailMesherValue5;
      this.tailZ[index] = trailMesherValue6;
   }

   static float getAmountForFloat(float trailMesherValue, float trailMesherValue2) {
      return trailMesherValue2 <= 1.0E-4F ? 1.0F : getAmount2(0.0F, trailMesherValue2, trailMesherValue);
   }

   private void handleAmountsAmount(float[] trailMesherValues, float[] trailMesherValues2, float[] trailMesherValues3, float[] trailMesherValues4, float[] trailMesherValues5, float trailMesherFactor, boolean trailMesherFlag, float trailMesherFactor2) {
      this.handleAmounts(trailMesherValues, trailMesherValues2, trailMesherValues3);
      int trailMesherValue = trailMesherValues4.length;
      int trailMesherValue2 = trailMesherValue / 2;
      float trailMesherLimit = trailMesherFactor2 * this.sinkIntensitySquared;

      for (int i = 0; i < this.styleCount - 1; i++) {
         float trailMesherFactor3 = this.segmentAlpha[i] * trailMesherFactor;
         float trailMesherFactor4 = this.segmentAlpha[i + 1] * trailMesherFactor;
         if (!(trailMesherFactor3 < 0.004F) || !(trailMesherFactor4 < 0.004F)) {
            float trailMesherFactor5 = this.trailMesherTable[i];
            float trailMesherFactor6 = this.trailMesherTable[i + 1];
            if (!(trailMesherFactor5 < 1.0E-4F) || !(trailMesherFactor6 < 1.0E-4F)) {
               int trailMesherValue4 = this.segmentColors[i];
               int trailMesherValue5 = this.segmentColors[i + 1];

               for (int j = 0; j < trailMesherValue - 1; j++) {
                  float trailMesherFactor7 = trailMesherValues4[j];
                  float trailMesherFactor8 = trailMesherValues4[j + 1];
                  float trailMesherFactor9 = trailMesherFactor7 * trailMesherFactor5 * (trailMesherFactor7 > 0.0F ? this.positiveSideFade[i] : this.negativeSideFade[i]);
                  float trailMesherFactor10 = trailMesherFactor8 * trailMesherFactor5 * (trailMesherFactor8 > 0.0F ? this.positiveSideFade[i] : this.negativeSideFade[i]);
                  float trailMesherFactor11 = trailMesherFactor7 * trailMesherFactor6 * (trailMesherFactor7 > 0.0F ? this.positiveSideFade[i + 1] : this.negativeSideFade[i + 1]);
                  float trailMesherFactor12 = trailMesherFactor8 * trailMesherFactor6 * (trailMesherFactor8 > 0.0F ? this.positiveSideFade[i + 1] : this.negativeSideFade[i + 1]);
                  float sinkTrailValue = trailMesherValues[i] + this.sideNormalX[i] * trailMesherFactor9;
                  float trailMesherValue7 = trailMesherValues2[i] + this.sideNormalY[i] * trailMesherFactor9;
                  float trailMesherValue8 = trailMesherValues3[i] + this.sideNormalZ[i] * trailMesherFactor9;
                  float trailMesherValue9 = trailMesherValues[i] + this.sideNormalX[i] * trailMesherFactor10;
                  float trailMesherValue10 = trailMesherValues2[i] + this.sideNormalY[i] * trailMesherFactor10;
                  float trailMesherValue11 = trailMesherValues3[i] + this.sideNormalZ[i] * trailMesherFactor10;
                  float trailMesherValue12 = trailMesherValues[i + 1] + this.sideNormalX[i + 1] * trailMesherFactor12;
                  float trailMesherValue13 = trailMesherValues2[i + 1] + this.sideNormalY[i + 1] * trailMesherFactor12;
                  float trailMesherValue14 = trailMesherValues3[i + 1] + this.sideNormalZ[i + 1] * trailMesherFactor12;
                  float sinkFactor = trailMesherValues[i + 1] + this.sideNormalX[i + 1] * trailMesherFactor11;
                  float trailMesherValue16 = trailMesherValues2[i + 1] + this.sideNormalY[i + 1] * trailMesherFactor11;
                  float trailMesherValue17 = trailMesherValues3[i + 1] + this.sideNormalZ[i + 1] * trailMesherFactor11;
                  if (trailMesherFlag) {
                     int valueForIntFloat = getValueForIntFloat(getValue(trailMesherValue4, j, trailMesherValue2, trailMesherLimit), trailMesherFactor3 * trailMesherValues5[j] * this.sinkDetailFactor);
                     int valueForIntFloat19 = getValueForIntFloat(getValue(trailMesherValue4, j + 1, trailMesherValue2, trailMesherLimit), trailMesherFactor3 * trailMesherValues5[j + 1] * this.sinkDetailFactor);
                     int valueForIntFloat20 = getValueForIntFloat(getValue(trailMesherValue5, j + 1, trailMesherValue2, trailMesherLimit), trailMesherFactor4 * trailMesherValues5[j + 1] * this.sinkDetailFactor);
                     int valueForIntFloat21 = getValueForIntFloat(getValue(trailMesherValue5, j, trailMesherValue2, trailMesherLimit), trailMesherFactor4 * trailMesherValues5[j] * this.sinkDetailFactor);
                     this.handleSinkTrail(sinkTrailValue, trailMesherValue7, trailMesherValue8, valueForIntFloat, trailMesherValue9, trailMesherValue10, trailMesherValue11, valueForIntFloat19, trailMesherValue12, trailMesherValue13, trailMesherValue14, valueForIntFloat20, sinkFactor, trailMesherValue16, trailMesherValue17, valueForIntFloat21);
                  } else {
                     int trailMesherValue22 = getValue(trailMesherValue4, trailMesherFactor3 * trailMesherValues5[j]);
                     int trailMesherValue23 = getValue(trailMesherValue4, trailMesherFactor3 * trailMesherValues5[j + 1]);
                     int trailMesherValue24 = getValue(trailMesherValue5, trailMesherFactor4 * trailMesherValues5[j + 1]);
                     int trailMesherValue25 = getValue(trailMesherValue5, trailMesherFactor4 * trailMesherValues5[j]);
                     this.trailSink
                        .handleConsumerVertex(sinkTrailValue, trailMesherValue7, trailMesherValue8, trailMesherValue22, trailMesherValue9, trailMesherValue10, trailMesherValue11, trailMesherValue23, trailMesherValue12, trailMesherValue13, trailMesherValue14, trailMesherValue24, sinkFactor, trailMesherValue16, trailMesherValue17, trailMesherValue25);
                     this.emittedSegmentCount++;
                  }
               }
            }
         }
      }
   }

   private float getAmountsStyleForInt(int index) {
      int index2 = index > 0 ? index - 1 : index;
      int index3 = index < this.styleCount - 1 ? index + 1 : index;
      float styleFactor = this.rawX[index3] - this.rawX[index2];
      float styleFactor2 = this.rawY[index3] - this.rawY[index2];
      float styleFactor3 = this.rawZ[index3] - this.rawZ[index2];
      float distance = (float)Math.sqrt(styleFactor * styleFactor + styleFactor2 * styleFactor2 + styleFactor3 * styleFactor3);
      if (distance < 1.0E-6F) {
         return 1.0F;
      } else {
         float styleValue = styleFactor / distance;
         float styleValue2 = styleFactor2 / distance;
         float styleValue3 = styleFactor3 / distance;
         boolean styleFlag = this.style.mode == TrailMesher.Mode.RIBBON;
         float styleBound = getAmount(this.style.widthScale);
         float styleValue4 = styleFlag ? 0.5F * Math.max(0.08F, styleBound) * this.segmentHeight[index] : styleBound * this.segmentHeight[index];
         float styleFactor4 = this.rawX[index];
         float styleFactor5 = this.rawY[index] + 0.03F + styleValue4;
         float styleFactor6 = this.rawZ[index];
         float sqrt2 = (float)Math.sqrt(styleFactor4 * styleFactor4 + styleFactor5 * styleFactor5 + styleFactor6 * styleFactor6);
         return sqrt2 < 1.0E-6F ? 0.12F : getAmount(styleFlag, styleValue, styleValue2, styleValue3, styleFactor4 / sqrt2, styleFactor5 / sqrt2, styleFactor6 / sqrt2, sqrt2);
      }
   }

   float getAmountsTable(int index) {
      return this.table[index];
   }

   static {
      for (int i = 0; i <= 14; i++) {
         double angle = (Math.PI * 2) * i / 14.0;
         sinkTable[i] = (float)Math.cos(angle);
         SIN_TABLE[i] = (float)Math.sin(angle);
      }
   }

   public static double getMin(float trailMesherValue) {
      double widthScaled = trailMesherValue * 0.6;
      return widthScaled < 0.2 ? 0.2 : Math.min(widthScaled, 0.6);
   }

   public static float getAmountFloat(float trailMesherValue) {
      return 1.0F - getAmount2(0.12F, 1.0F, trailMesherValue);
   }

   private void handleAmountsStyle(boolean styleFlag, boolean styleFlag2) {
      this.cumulativeLength[0] = 0.0F;

      for (int i = 1; i < this.styleCount; i++) {
         this.cumulativeLength[i] = this.cumulativeLength[i - 1]
            + (float)TrailBuffer.getAmount(
               this.rawX[i], this.rawY[i], this.rawZ[i], this.rawX[i - 1], this.rawY[i - 1], this.rawZ[i - 1]
            );
      }

      float styleValue = this.cumulativeLength[this.styleCount - 1];
      float min2 = Math.min(0.7F, styleValue * 0.3F);
      float styleValue3 = styleFlag2 ? Math.min(0.3F, styleValue * 0.25F) : Math.min(0.7F, styleValue * 0.3F);
      float themeAlpha = this.style.themeAlpha;
      float themeAmount = this.style.themeAmount;

      for (int j = 0; j < this.styleCount; j++) {
         float styleValue4 = this.table[j];
         float styleFactor = getAmountFloat(styleValue4);
         float styleFactor2 = 1.0F;
         if (!styleFlag) {
            styleFactor2 *= getAmountForFloat(this.cumulativeLength[j], min2);
         }

         float styleFactor3 = styleFactor2 * getAmountForFloat(styleValue - this.cumulativeLength[j], styleValue3);
         this.segmentTaper[j] = styleFactor * styleFactor3;
         float amount2 = getAmount2(0.6F, 2.4F, this.getAmountsStyle(j));
         this.segmentAlpha[j] = getAmount(styleValue4, themeAlpha) * (0.35F + 0.65F * styleFactor3) * amount2 * themeAmount * this.getAmountsStyleForInt(j);
         this.segmentColors[j] = this.getAmountStyle(styleValue4);
      }
   }

   public static float getAmount2(float trailMesherLimit, float trailMesherValue, float distance) {
      if (trailMesherValue <= trailMesherLimit) {
         return distance < trailMesherLimit ? 0.0F : 1.0F;
      } else {
         float trailMesherFactor = getAmount((distance - trailMesherLimit) / (trailMesherValue - trailMesherLimit));
         return trailMesherFactor * trailMesherFactor * (3.0F - 2.0F * trailMesherFactor);
      }
   }

   private float getMaxAmount() {
      return 1.0F - getAmount2(this.sinkAmount, this.sinkLimit, this.sinkCount);
   }

   int getStyleCount() {
      return this.styleCount;
   }

   private float[] getAmountsBlendParams(int trailMesherValue) {
      float trailMesherBound = (trailMesherValue >> 16 & 0xFF) / 255.0F;
      float pilotStatusBound = (trailMesherValue >> 8 & 0xFF) / 255.0F;
      float rocketsElytraBound = (trailMesherValue & 0xFF) / 255.0F;
      float trailMesherDivisor = Math.max(trailMesherBound, Math.max(pilotStatusBound, rocketsElytraBound));
      float min2 = Math.min(trailMesherBound, Math.min(pilotStatusBound, rocketsElytraBound));
      float deltaVar = trailMesherDivisor - min2;
      float trailMesherCounter;
      if (deltaVar < 1.0E-6F) {
         trailMesherCounter = 0.0F;
      } else if (trailMesherDivisor == trailMesherBound) {
         trailMesherCounter = (pilotStatusBound - rocketsElytraBound) / deltaVar / 6.0F;
      } else if (trailMesherDivisor == pilotStatusBound) {
         trailMesherCounter = ((rocketsElytraBound - trailMesherBound) / deltaVar + 2.0F) / 6.0F;
      } else {
         trailMesherCounter = ((trailMesherBound - pilotStatusBound) / deltaVar + 4.0F) / 6.0F;
      }

      if (trailMesherCounter < 0.0F) {
         trailMesherCounter++;
      }

      this.blendParams[0] = trailMesherCounter;
      this.blendParams[1] = trailMesherDivisor <= 0.0F ? 0.0F : deltaVar / trailMesherDivisor;
      this.blendParams[2] = trailMesherDivisor;
      return this.blendParams;
   }

   private boolean isMax() {
      return this.sinkCount >= this.sinkLimit || this.getMaxAmount() < 0.004F;
   }

   public int getSinkValue() {
      return this.sinkValue;
   }

   private float getAmounts(int index, float trailMesherBound, float trailMesherFactor) {
      float distance = (float)Math.sqrt(this.renderX[index] * this.renderX[index] + this.renderUpperY[index] * this.renderUpperY[index] + this.styleTable[index] * this.styleTable[index]);
      return Math.min(trailMesherBound, trailMesherFactor * distance);
   }

   private void updateAmountsMin() {
      this.styleCount = 0;
      double trailMesherTotal = 0.0;

      for (int i = 0; i < this.pointCount - 1; i++) {
         trailMesherTotal += TrailBuffer.getAmount(
            this.pointX[i], this.pointY[i], this.pointZ[i], this.pointX[i + 1], this.pointY[i + 1], this.pointZ[i + 1]
         );
      }

      int trailMesherDivisor = Math.max(8, this.segmentLimit - this.pointCount - 2);
      double max2 = Math.max(0.1 * Math.max(1.0F, this.style.clamp), trailMesherTotal / trailMesherDivisor);

      for (int j = 0; j < this.pointCount - 1; j++) {
         double trailMesherValue;
         double trailMesherValue2;
         double trailMesherValue3;
         if (j > 0) {
            trailMesherValue = this.pointX[j - 1];
            trailMesherValue2 = this.pointY[j - 1];
            trailMesherValue3 = this.pointZ[j - 1];
         } else {
            trailMesherValue = 2.0 * this.pointX[0] - this.pointX[1];
            trailMesherValue2 = 2.0 * this.pointY[0] - this.pointY[1];
            trailMesherValue3 = 2.0 * this.pointZ[0] - this.pointZ[1];
         }

         double trailBufferValue;
         double trailMesherValue5;
         double trailMesherValue6;
         if (j + 2 < this.pointCount) {
            trailBufferValue = this.pointX[j + 2];
            trailMesherValue5 = this.pointY[j + 2];
            trailMesherValue6 = this.pointZ[j + 2];
         } else {
            trailBufferValue = 2.0 * this.pointX[this.pointCount - 1] - this.pointX[this.pointCount - 2];
            trailMesherValue5 = 2.0 * this.pointY[this.pointCount - 1] - this.pointY[this.pointCount - 2];
            trailMesherValue6 = 2.0 * this.pointZ[this.pointCount - 1] - this.pointZ[this.pointCount - 2];
         }

         double trailMesherValue7 = TrailBuffer.getAmount(
            this.pointX[j], this.pointY[j], this.pointZ[j], this.pointX[j + 1], this.pointY[j + 1], this.pointZ[j + 1]
         );
         int trailMesherLimit = (int)Math.ceil(trailMesherValue7 / max2);
         if (trailMesherLimit < 1) {
            trailMesherLimit = 1;
         }

         if (trailMesherLimit > 12) {
            trailMesherLimit = 12;
         }

         for (int k = 0; k < trailMesherLimit && this.styleCount < this.segmentLimit - 1; k++) {
            double trailMesherFactor = (double)k / trailMesherLimit;
            handle(
               trailMesherValue,
               trailMesherValue2,
               trailMesherValue3,
               this.pointX[j],
               this.pointY[j],
               this.pointZ[j],
               this.pointX[j + 1],
               this.pointY[j + 1],
               this.pointZ[j + 1],
               trailBufferValue,
               trailMesherValue5,
               trailMesherValue6,
               trailMesherFactor,
               this.scratchPoint
            );
            this.handleAmountsStyle(
               this.scratchPoint[0],
               this.scratchPoint[1],
               this.scratchPoint[2],
               getAmount(this.tailX[j], this.tailX[j + 1], trailMesherFactor),
               (float)getAmount((double)this.tailY[j], (double)this.tailY[j + 1], trailMesherFactor),
               getAmount(this.tailZ[j], this.tailZ[j + 1], trailMesherFactor)
            );
         }
      }

      int index = this.pointCount - 1;
      this.handleAmountsStyle(
         this.pointX[index], this.pointY[index], this.pointZ[index], this.tailX[index], this.tailY[index], this.tailZ[index]
      );
   }

   private void handleAmountsStyle(double styleValue, double styleValue2, double styleValue3, double styleValue4, float styleValue5, double styleValue6) {
      int index = this.styleCount++;
      this.rawX[index] = (float)styleValue;
      this.rawY[index] = (float)styleValue2;
      this.rawZ[index] = (float)styleValue3;
      this.segmentHeight[index] = styleValue5;
      this.segmentTimes[index] = styleValue6;
      this.table[index] = (float)getAmount((this.trailMesherAmount - styleValue4) / this.style.trailLength);
   }

   public static enum Mode {
      RIBBON,
      LINE,
      DOTS;
   }

   public static final class Style {
      public TrailMesher.Mode mode = TrailMesher.Mode.RIBBON;
      public int themeCustomValue = 11572223;
      public double trailLength;
      public double styleThemeAmount;
      public float clamp;
      public boolean equals;
      public float styleAmount;
      public float clampAmount;
      public int clampSink;
      public float themeAmount = 1.0F;
      public int clamp2Clamp;
      public float themeAlpha;
      public float widthScale;

      public void updateClampAmount() {
         this.clamp = 1.0F;
         this.clampAmount = 0.0F;
         this.clampSink = Integer.MAX_VALUE;
         this.clamp2Clamp = 400;
      }

      public Style() {
         this.trailLength = 1.5;
         this.styleAmount = 0.3F;
         this.widthScale = 1.0F;
         this.themeAlpha = 1.0F;
         this.clamp = 1.0F;
         this.clampSink = Integer.MAX_VALUE;
         this.clamp2Clamp = 400;
      }
   }
}
