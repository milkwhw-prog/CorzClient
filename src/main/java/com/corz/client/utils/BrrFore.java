package com.corz.client.utils;

import java.util.Arrays;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.LoftLatheFlag;

public final class BrrFore extends WarmBrainrotBase {
   private ForeEar classShinProgress;
   private boolean bodyCrownFlag;
   private static final int[] bonecaAmbalabuTable = new int[]{5140012, 5929524, 6456372, 7311162, 5600562, 8034878, 4875818};
   private ForeEar classShinFoot;
   private static final float[] shinFootTable = new float[]{19.9F, 20.4F, 21.4F, 22.9F, 24.8F, 27.0F, 29.3F, 31.6F, 33.6F, 35.4F, 36.7F, 37.6F, 38.0F};
   private static final float[] brrForeTable = new float[]{0.0F, 2.7F, 4.7F, 5.95F, 6.6F, 6.85F, 6.8F, 6.5F, 5.95F, 5.1F, 3.9F, 2.3F, 0.0F};
   private static final float[] table = getAmounts();
   private ForeEar armL;
   private static final float[] crownTable = new float[]{22.9F, 24.1F, 25.6F, 27.2F, 28.6F};
   private static final float[] CROWN_CENTER_Z = new float[]{-4.95F, -5.25F, -5.5F, -5.5F, -5.1F};
   private static final float[] CROWN_RADII_X = new float[]{0.3F, 1.5F, 2.4F, 2.6F, 2.0F};
   private ForeEar footL;
   private ForeEar shinFootForeEar;
   private static final float[] CROWN_RADII_Z = new float[]{0.2F, 0.85F, 1.2F, 1.2F, 0.55F};
   private ForeEar shinProgressForeEar;
   private ForeEar brrForeForeEar;
   private ForeEar shinL;
   private ForeEar foreR;
   private ForeEar foreEar;
   private ForeEar foreL;
   private static final float[] CROWN_SPIKE_X = new float[]{-3.0F, -1.5F, 0.0F, 1.5F, 3.0F};
   private static final float[] CROWN_SPIKE_WIDTHS = new float[]{0.12F, 0.62F, 0.55F, 0.62F, 0.12F};
   private int bodyCrownCount;
   private ForeEar foreRightSection;
   private ForeEar eyeLeftSection;
   private static final float[] CROWN_SPIKE_DEPTHS = new float[]{0.12F, 0.95F, 0.66F, 0.95F, 0.12F};
   private static final float BODY_PROFILE_TOP_HEIGHT = getAmount();
   private ForeEar footR;
   private ForeEar shinR;

   private static void handle(LatheLoft latheLoft, float brrForeValue, float brrForeValue2, float armTrunkValue, float degrees, float brrForeValue5, float brrForeValue6, float hypot, float brrForeValue8, ShinBrr shinBrr) {
      handle(latheLoft, brrForeValue, brrForeValue2, armTrunkValue, degrees, brrForeValue5, brrForeValue6, 0.0F, hypot, brrForeValue8, shinBrr);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuLimit = (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = (j + 0.5F) / ambalabuBonecaValue;
            int bonecaAmbalabuValue2 = j / 3;
            float bonecaAmbalabuValue3 = bonecaAmbalabuValue2 * 3 + 1.5F + (getAmount(bonecaAmbalabuValue2, 31) - 0.5F) * 1.2F;
            float bonecaAmbalabuValue4 = 0.76F + 0.23F * getAmount(bonecaAmbalabuValue2, 32);
            if (bonecaAmbalabuLimit > 0.66F) {
               if (bonecaAmbalabuLimit > bonecaAmbalabuValue4) {
                  continue;
               }

               float bonecaAmbalabuValue5 = (bonecaAmbalabuLimit - 0.66F) / (bonecaAmbalabuValue4 - 0.66F);
               if (Math.abs(j + 0.5F - bonecaAmbalabuValue3) > 1.7F * (1.0F - bonecaAmbalabuValue5) + 0.25F) {
                  continue;
               }
            }

            float bonecaAmbalabuValue6 = j + 0.9F * (float)Math.sin(bonecaAmbalabuLimit * 9.0F + bonecaAmbalabuValue2 * 0.7F);
            int brrForeValue = (int)Math.floor(bonecaAmbalabuValue6 / 2.4F);
            float absScaled = Math.abs(bonecaAmbalabuValue6 / 2.4F - brrForeValue - 0.5F) * 2.0F;
            float bonecaAmbalabuBound = 0.9F
               + 0.14F * getAmount(brrForeValue, 5)
               + 0.04F * getAmount(j * 13 + i * 59, 6)
               - 0.08F * getAmount(0.5F, 1.0F, bonecaAmbalabuLimit)
               - 0.12F * getAmount(0.14F, 0.0F, bonecaAmbalabuLimit)
               - 0.04F * getAmount(0.75F, 1.0F, absScaled);
            int foreShinValue = ForeShinUtils.getValue(ForeShinUtils.getValueForInt(15525853), Math.min(1.08F, bonecaAmbalabuBound));
            float absScaled2 = Math.abs(bonecaAmbalabuValue - 0.5F) * 2.0F;
            int value2 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(10333812), getAmount(0.85F, 1.0F, absScaled2) * 0.2F);
            bonecaAmbalabu.getBonecaAmbalabu(j, i, value2);
         }
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 46, 0.02F, 0.62F, 9.0F, 191);
   }

   private static float getAmounts(float brrForeValue, float brrForeLimit) {
      float brrForeDivisor = Math.max(0.05F, getAmountForFloatFloatArray(brrForeLimit, crownTable, CROWN_RADII_X));
      float brrForeFactor = Math.min(1.0F, Math.abs(brrForeValue) / brrForeDivisor);
      return getAmountForFloatFloatArray(brrForeLimit, crownTable, CROWN_CENTER_Z) - getAmountForFloatFloatArray(brrForeLimit, crownTable, CROWN_RADII_Z) * (float)Math.sqrt(1.0F - brrForeFactor * brrForeFactor);
   }

   private static float getAmounts(float brrForeLimit) {
      int brrForeValue = shinFootTable.length;
      if (brrForeLimit <= shinFootTable[0]) {
         return brrForeTable[0];
      } else if (brrForeLimit >= shinFootTable[brrForeValue - 1]) {
         return brrForeTable[brrForeValue - 1];
      } else {
         int index = 0;

         while (shinFootTable[index + 1] < brrForeLimit) {
            index++;
         }

         float brrForeFactor = (brrForeLimit - shinFootTable[index]) / (shinFootTable[index + 1] - shinFootTable[index]);
         return brrForeTable[index] + (brrForeTable[index + 1] - brrForeTable[index]) * brrForeFactor;
      }
   }

   private static float getAmount(float brrForeLimit, float[] brrForeValues, float[] brrForeValues2) {
      if (brrForeLimit <= brrForeValues[0]) {
         return brrForeValues2[0];
      } else {
         int brrForeValue = brrForeValues.length;
         if (brrForeLimit >= brrForeValues[brrForeValue - 1]) {
            return brrForeValues2[brrForeValue - 1];
         } else {
            int index = 0;

            while (brrForeValues[index + 1] < brrForeLimit) {
               index++;
            }

            float brrForeFactor = getAmount4((brrForeLimit - brrForeValues[index]) / (brrForeValues[index + 1] - brrForeValues[index]));
            return brrForeValues2[index] + (brrForeValues2[index + 1] - brrForeValues2[index]) * brrForeFactor;
         }
      }
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      int[] bonecaAmbalabuValues = getValues(ambalabuBonecaValue, bonecaValue, 1.0F, 7);

      for (int i = 0; i < bonecaValue; i++) {
         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuLimit = bonecaValue * (0.42F + 0.1F * (float)Math.sin(j * 0.21F + 0.5F) + 0.07F * (float)Math.sin(j * 0.53F + 1.7F));
            boolean bonecaAmbalabuFlag = i < bonecaAmbalabuLimit;
            if (!bonecaAmbalabuFlag) {
               int bonecaAmbalabuValue = j / 4;
               float bonecaAmbalabuValue2 = bonecaAmbalabuValue * 4 + 2.0F + (getAmount(bonecaAmbalabuValue, 21) - 0.5F) * 1.6F;
               float bonecaAmbalabuLimit2 = bonecaAmbalabuLimit + bonecaValue * 0.42F * getAmount(bonecaAmbalabuValue, 22) * getAmount(bonecaAmbalabuValue, 23);
               if (i < bonecaAmbalabuLimit2) {
                  float bonecaAmbalabuValue3 = (i - bonecaAmbalabuLimit) / Math.max(1.0F, bonecaAmbalabuLimit2 - bonecaAmbalabuLimit);
                  bonecaAmbalabuFlag = Math.abs(j + 0.5F - bonecaAmbalabuValue2) < 1.9F * (1.0F - bonecaAmbalabuValue3) + 0.3F;
               }
            }

            if (bonecaAmbalabuFlag) {
               bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(bonecaAmbalabuValues[i * ambalabuBonecaValue + j], 0.92F));
            }
         }
      }
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4, ShinBrr shinBrr5, boolean brrForeFlag) {
      for (int i = 0; i < 14; i++) {
         float brrForeLimit = 6.0F + i * 25.714285F + 10.0F * (getAmount(i, 72) - 0.5F);
         float brrForeValue = getAmount(60.0F, 25.0F, Math.abs(brrForeLimit > 180.0F ? brrForeLimit - 360.0F : brrForeLimit));
         float brrForeValue2 = 21.3F + 3.6F * getAmount(i, 71) * (1.0F - 0.6F * brrForeValue);
         float brrForeValue3 = 1.6F + 3.4F * getAmount(i, 74);
         handle(latheLoft, brrForeLimit, brrForeValue2, getAmounts(brrForeValue2) - 0.15F, 3.6F + 0.7F * getAmount(i, 73), brrForeValue3, 6.0F + 24.0F * getAmount(i, 75), shinBrr);
      }

      for (int j = 0; j < 6; j++) {
         handle(latheLoft, 60.0F + j * 48.0F, 34.9F, getAmounts(34.9F) - 0.2F, 4.0F, 2.8F, 34.0F, shinBrr);
      }

      float[][] floats = new float[][]{
         {75.0F, 27.2F},
         {100.0F, 33.0F},
         {125.0F, 26.0F},
         {148.0F, 31.6F},
         {172.0F, 26.8F},
         {190.0F, 35.0F},
         {212.0F, 29.6F},
         {236.0F, 34.0F},
         {258.0F, 26.6F},
         {284.0F, 31.2F}
      };

      for (int k = 0; k < floats.length; k++) {
         float degrees = floats[k][0];
         float brrForeValue4 = floats[k][1];
         float brrForeFactor = getAmounts(brrForeValue4) - 0.55F;
         float angle = (float)Math.toRadians(degrees);
         latheLoft.getDequeArrayArrayDeque()
            .getLatheLoft(brrForeFactor * (float)Math.sin(angle), brrForeValue4, -brrForeFactor * 0.88F * (float)Math.cos(angle))
            .getLatheLoft4(-degrees)
            .getLatheLoft(20.0F * (getAmount(k, 161) - 0.5F));
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.8F + 0.5F * getAmount(k, 162), 1.3F + 0.4F * getAmount(k, 163), 1.55F, 6, shinBrr5);
         latheLoft.getDequeArray();
      }

      float[][] floats2 = new float[][]{
         {60.0F, 25.5F, 35.0F, 2.0F},
         {-55.0F, 26.2F, 40.0F, 2.0F},
         {-40.0F, 35.2F, 45.0F, 2.0F},
         {38.0F, 35.6F, 42.0F, 2.0F},
         {82.0F, 28.2F, 0.0F, 0.0F},
         {103.0F, 27.0F, 0.0F, 0.0F},
         {110.0F, 34.4F, 0.0F, 0.0F},
         {-84.0F, 30.2F, 0.0F, 0.0F},
         {-104.0F, 27.6F, 0.0F, 0.0F},
         {-110.0F, 32.2F, 0.0F, 0.0F},
         {138.0F, 30.0F, 0.0F, 0.0F},
         {162.0F, 34.0F, 0.0F, 0.0F},
         {205.0F, 28.6F, 0.0F, 0.0F},
         {170.0F, 30.4F, 0.0F, 0.0F},
         {244.0F, 30.2F, 0.0F, 0.0F}
      };

      for (int m = 0; m < floats2.length; m++) {
         float[] brrForeValues = floats2[m];
         if (!brrForeFlag || !(brrForeValues[3] > 0.0F) && m % 2 != 1) {
            float brrForeFactor2 = getAmounts(brrForeValues[1]) - 0.2F;
            float radians2 = (float)Math.toRadians(brrForeValues[0]);
            float signum = Math.signum((float)Math.sin(radians2));
            float degrees2 = (float)Math.toDegrees(Math.atan((getAmounts(brrForeValues[1] - 0.4F) - getAmounts(brrForeValues[1] + 0.4F)) / 0.8F));
            float brrForeValue5 = brrForeValues[3] > 0.0F ? brrForeValues[2] : Math.max(10.0F, 36.0F + 12.0F * getAmount(m, 168) - degrees2);
            float hypot = brrForeValues[3] > 0.0F ? brrForeValues[3] : 1.6F + 1.6F * getAmount(m, 164);
            float brrForeValue7 = brrForeValues[3] > 0.0F ? 1.2F : 1.3F + 0.4F * getAmount(m, 165);
            float brrForeValue8 = brrForeValues[3] > 0.0F ? 0.0F : (m % 2 == 0 ? 1.0F : -1.0F) * (12.0F + 12.0F * getAmount(m, 167));
            handle(
               latheLoft,
               brrForeFactor2 * (float)Math.sin(radians2),
               brrForeValues[1],
               -brrForeFactor2 * 0.88F * (float)Math.cos(radians2),
               -brrForeValues[0],
               brrForeValue5,
               -signum * (8.0F + 6.0F * getAmount(m, 166)),
               brrForeValue8,
               hypot,
               brrForeValue7,
               brrForeValues[3] <= 0.0F && m % 2 == 1 ? shinBrr3 : shinBrr2
            );
         }
      }

      if (!brrForeFlag) {
         float[][] floats3 = new float[][]{{-3.3F, -0.7F, 5.4F, 0.7F, -0.6F}, {1.2F, -1.5F, 5.0F, 0.3F, -0.3F}, {4.0F, 0.1F, 6.0F, -0.8F, -0.9F}};

         for (float[] brrForeValues2 : floats3) {
            handle(latheLoft, brrForeValues2, new float[]{0.2F, 0.15F, 0.07F}, 4, shinBrr4);
         }

         float[][] floats4 = new float[][]{
            {4.3F, -3.0F, 4.6F, 0.3F, -0.2F},
            {-4.3F, -3.0F, 3.6F, -0.3F, -0.3F},
            {3.4F, 3.6F, 5.8F, 0.2F, 0.5F},
            {-2.6F, 4.2F, 4.2F, -0.2F, 0.4F},
            {0.6F, 4.6F, 3.2F, 0.1F, 0.3F},
            {-4.8F, 1.8F, 5.0F, -0.4F, 0.2F}
         };

         for (float[] brrForeValues3 : floats4) {
            handle(latheLoft, brrForeValues3, new float[]{0.28F, 0.18F, 0.06F}, 4, shinBrr5);
         }

         for (int p = -1; p <= 1; p += 2) {
            float[] latheLoftValues = new float[15];

            for (int q = 0; q < 5; q++) {
               float brrForeFactor3 = q / 4.0F;
               float brrForeValue9 = p * (2.5F - 1.5F * brrForeFactor3 + 0.35F * (float)Math.sin(brrForeFactor3 * (float) (Math.PI * 2)));
               float brrForeValue10 = 35.0F - 11.0F * brrForeFactor3;
               latheLoftValues[3 * q] = brrForeValue9;
               latheLoftValues[3 * q + 1] = brrForeValue10;
               latheLoftValues[3 * q + 2] = -getAmount3(brrForeValue9, brrForeValue10) + 0.1F;
            }

            latheLoft.getLatheLoft(latheLoftValues, LatheLoft.getAmounts(5, 0.2F, 0.16F, 0.06F), 4, shinBrr4, false);
         }
      }
   }

   private static void handle(LatheLoft latheLoft, float brrForeValue, float brrForeValue2, float armTrunkValue, float degrees, float brrForeValue5, float brrForeValue6, float brrForeValue7, float hypot, float brrForeValue9, ShinBrr shinBrr) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(brrForeValue, brrForeValue2, armTrunkValue).getLatheLoft4(degrees).getLatheLoftLatheLoftAmountLoft(-brrForeValue5).getLatheLoft(brrForeValue6).getLatheLoft4(brrForeValue7);
      latheLoft.getLatheLoft(-brrForeValue9 * 0.5F, 0.0F, -0.03F, brrForeValue9 * 0.5F, hypot, 0.03F, shinBrr, shinBrr, null, null, null, null);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(12226155));
      bonecaAmbalabu.getClass02(0.0F, bonecaAmbalabu.bonecaValue * 0.55F, (float)bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.55F, 1.2F, ForeShinUtils.getValueForIntFloat(8280640, 0.7F));
      bonecaAmbalabu.getClass02(0.0F, bonecaAmbalabu.bonecaValue * 0.78F, (float)bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.78F, 1.0F, ForeShinUtils.getValueForIntFloat(8280640, 0.5F));
      bonecaAmbalabu.getClass02(0.0F, bonecaAmbalabu.bonecaValue * 0.95F, (float)bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.95F, 1.4F, ForeShinUtils.getValueForIntFloat(4861474, 0.75F));
   }

   private static void handle(LatheLoft latheLoft, float degrees, float brrForeFactor, float brrForeValue, float brrForeValue2, float brrForeValue3, ShinBrr shinBrr) {
      double angle = Math.toRadians(degrees);
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float brrForeValue4 = 3.55F * brrForeFactor * Math.signum(cos) * (float)Math.pow(Math.abs(cos), 0.8);
      float brrForeLimit = 29.9F + (sin > 0.0F ? 4.6F : 4.1F) * brrForeFactor * Math.signum(sin) * (float)Math.pow(Math.abs(sin), 0.85);
      float degrees2 = (float)Math.toDegrees(Math.atan((getAmount3(brrForeValue4, brrForeLimit + 0.15F) - getAmount3(brrForeValue4, brrForeLimit - 0.15F)) / 0.3F));
      latheLoft.getDequeArrayArrayDeque()
         .getLatheLoft(brrForeValue4, brrForeLimit, getAmount3(brrForeValue4, brrForeLimit) + 0.05F)
         .getLatheLoft4(LatheLoft.getAmountForFloat(brrForeValue4, getAmounts(brrForeLimit)))
         .getLatheLoftLatheLoftAmountLoft(degrees2)
         .getLatheLoft(degrees - 90.0F)
         .getLatheLoftLatheLoftAmountLoft(brrForeValue3);
      latheLoft.getLatheLoft(-brrForeValue2 * 0.5F, -brrForeValue, -0.03F, brrForeValue2 * 0.5F, 0.0F, 0.03F, shinBrr, shinBrr, null, null, null, null);
      latheLoft.getDequeArray();
   }

   private void handleCrownAmounts(
      LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4, ShinBrr shinBrr5, ShinBrr shinBrr6, ShinBrr shinBrr7, ShinBrr shinBrr8, ShinBrr shinBrr9
   ) {
      for (int i = -1; i <= 1; i += 2) {
         float crownValue = i * 1.45F;
         float amountForFloat = getAmountForFloat(i);
         latheLoft.getDequeArrayArrayDeque()
            .getLatheLoft(crownValue, 31.630001F, amountForFloat + 0.02F)
            .getLatheLoft4(LatheLoft.getAmountForFloat(crownValue, getAmounts(31.35F)))
            .getLatheLoft(-i * 14.0F)
            .getLatheLoftLatheLoftAmountLoft(-4.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.84F, 0.62F, 0.56F, 8, 0.0F, 0.5F, shinBrr);
         latheLoft.getDequeArray();
      }

      float[] floats = new float[15];

      for (int j = 0; j < 5; j++) {
         float crownValue3 = CROWN_SPIKE_X[j];
         float amount5 = getAmount5(crownValue3);
         floats[3 * j] = crownValue3;
         floats[3 * j + 1] = amount5;
         floats[3 * j + 2] = getAmount3(crownValue3, amount5) - 0.15F;
      }

      float[] pointsSpline = LatheLoft.getPointsSpline(this.bodyCrownFlag ? 1 : 2, floats);
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      latheLoft.getLoftPath(pointsSpline, LatheLoft.getAmounts(latheLoftSize, CROWN_SPIKE_WIDTHS), LatheLoft.getAmounts(latheLoftSize, CROWN_SPIKE_DEPTHS), 6, shinBrr2, null, null, 0.0F, 1.0F, 0.0F);

      for (int k = 0; k < (this.bodyCrownFlag ? 0 : 11); k++) {
         float crownValue6 = -2.6F + 0.52F * k + 0.12F * (getAmount(k, 91) - 0.5F);
         float crownValue7 = getAmount5(crownValue6);
         float crownValue8 = getAmount(crownValue6, CROWN_SPIKE_X, CROWN_SPIKE_WIDTHS);
         float crownValue9 = getAmount(0.6F, 1.2F, Math.abs(crownValue6)) * getAmount(2.4F, 1.8F, Math.abs(crownValue6));
         float crownFactor = k % 2 == 0 ? 1.0F : -1.0F;
         handleForLatheLoftFloat(
            latheLoft,
            crownValue6,
            crownValue7 - 0.2F,
            getAmount3(crownValue6, crownValue7) - 0.15F - crownValue8 * 0.6F,
            LatheLoft.getAmountForFloat(crownValue6, getAmounts(crownValue7)),
            35.0F + 20.0F * getAmount(k, 93),
            crownFactor * (10.0F + 8.0F * Math.abs(crownValue6)),
            1.1F + 0.4F * getAmount(k, 92) - 0.25F * crownValue9,
            0.7F,
            shinBrr3
         );
      }

      float[] floats2 = new float[15];
      float[] floats3 = new float[5];
      float[] floats4 = new float[5];

      for (int m = 0; m < 5; m++) {
         int index = 4 - m;
         floats2[3 * m + 1] = crownTable[index];
         floats2[3 * m + 2] = CROWN_CENTER_Z[index];
         floats3[m] = CROWN_RADII_X[index];
         floats4[m] = CROWN_RADII_Z[index];
      }

      latheLoft.getLoftPath(floats2, floats3, floats4, 8, shinBrr4, null, null, 0.0F, 0.0F, -1.0F);
      float[] floats5 = new float[]{0.45F, 1.45F, 2.45F, 3.05F};
      float[] floats6 = new float[]{28.9F, 28.45F, 27.5F, 26.3F};
      float[] floats7 = new float[]{0.66F, 0.55F, 0.45F, 0.38F};

      for (int p = -1; p <= 1; p += 2) {
         float[] loftPathValues = new float[12];

         for (int q = 0; q < 4; q++) {
            loftPathValues[3 * q] = p * floats5[q];
            loftPathValues[3 * q + 1] = floats6[q];
            loftPathValues[3 * q + 2] = getAmount3(p * floats5[q], floats6[q]) - floats7[q];
         }

         latheLoft.getLoftPath(loftPathValues, new float[]{0.46F, 0.44F, 0.34F, 0.12F}, new float[]{0.78F, 0.74F, 0.56F, 0.18F}, 6, shinBrr5, null, null, 0.0F, 1.0F, 0.0F);
         float latheLoftValue = p * 2.3F;
         float crownValue11 = 29.6F;
         latheLoft.getDequeArrayArrayDeque()
            .getLatheLoft(latheLoftValue, crownValue11, getAmount3(latheLoftValue, crownValue11) + 0.12F)
            .getLatheLoft4(LatheLoft.getAmountForFloat(latheLoftValue, getAmounts(crownValue11)))
            .getLatheLoftLatheLoftAmountLoft(-90.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.85F, 0.28F, 0.5F, 8, 0.0F, 0.5F, shinBrr6);
         latheLoft.getDequeArray();

         for (int r = 0; r < (this.bodyCrownFlag ? 0 : 4); r++) {
            float crownValue12 = 0.45F + 0.18F * r;
            float brrForeValue = p * (2.45F + 0.6F * crownValue12);
            float brrForeLimit = 27.5F - 1.2F * crownValue12 + 0.1F;
            handleForLatheLoftFloat(
               latheLoft,
               brrForeValue,
               brrForeLimit,
               getAmount3(brrForeValue, brrForeLimit) - 0.3F,
               LatheLoft.getAmountForFloat(brrForeValue, getAmounts(brrForeLimit)),
               8.0F + 6.0F * getAmount(r, 171),
               p * (6.0F + 12.0F * r - 14.0F),
               0.75F + 0.3F * getAmount(r + p, 172),
               0.42F,
               shinBrr8
            );
         }
      }

      float[] floats9 = new float[]{-1.2F, -0.6F, 0.0F, 0.6F, 1.2F};

      for (int t = 0; t < (this.bodyCrownFlag ? 0 : floats9.length); t++) {
         float crownValue15 = floats9[t] + 0.12F * (getAmount(t, 173) - 0.5F);
         float armTrunkValue = getAmount3(crownValue15) + 0.35F + 0.25F * getAmount(t, 174);
         handleForLatheLoftFloat(
            latheLoft,
            crownValue15,
            armTrunkValue,
            getAmounts(crownValue15, armTrunkValue) - 0.04F,
            0.0F,
            5.0F + 15.0F * getAmount(t, 175),
            (getAmount(t, 176) - 0.5F) * 20.0F - 6.0F * crownValue15,
            1.0F + 0.5F * getAmount(t, 177),
            0.55F + 0.2F * getAmount(t, 178),
            shinBrr8
         );
      }

      for (int crownCounter = 0; crownCounter < (this.bodyCrownFlag ? 0 : 4); crownCounter++) {
         float crownFactor2 = crownCounter < 2 ? 1.0F : -1.0F;
         float crownValue17 = crownCounter % 2 == 0 ? 26.5F : 25.0F;
         float crownValue18 = getAmount(crownValue17, crownTable, CROWN_RADII_X);
         float crownValue19 = crownFactor2 * (crownValue18 - 0.3F);
         handleForLatheLoftFloat(
            latheLoft, crownValue19, crownValue17 + 0.7F, getAmounts(crownValue19, crownValue17) + 0.1F, -crownFactor2 * 18.0F, 8.0F, crownFactor2 * 20.0F, 1.7F + 0.2F * getAmount(crownCounter, 179), 0.95F, shinBrr8
         );
      }

      for (int crownCounter2 = 0; crownCounter2 < 14; crownCounter2++) {
         float degrees = -22.0F + crownCounter2 * 18.76923F + 6.0F * (getAmount(crownCounter2, 95) - 0.5F);
         handle(
            latheLoft,
            degrees,
            1.2F + 0.06F * getAmount(crownCounter2, 96),
            1.2F + 0.5F * getAmount(crownCounter2, 97),
            1.2F + 0.4F * getAmount(crownCounter2, 98),
            25.0F + 10.0F * getAmount(crownCounter2, 99),
            shinBrr7
         );
      }

      if (!this.bodyCrownFlag) {
         int crownSize = 13;
         float[] latheLoftValues = new float[3 * crownSize];
         float[] crownForeValues = new float[crownSize];

         for (int intersectsCenterBound = 0; intersectsCenterBound < crownSize; intersectsCenterBound++) {
            double angle = Math.toRadians(-30.0F + intersectsCenterBound * (240.0F / (crownSize - 1)));
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float crownFactor3 = 1.22F;
            float crownValue21 = 3.55F * crownFactor3 * Math.signum(cos) * (float)Math.pow(Math.abs(cos), 0.8);
            float crownValue22 = 29.9F + (sin > 0.0F ? 4.6F : 4.1F) * crownFactor3 * Math.signum(sin) * (float)Math.pow(Math.abs(sin), 0.85);
            float amounts4 = getAmounts(crownValue22);
            float amount3 = getAmount3(crownValue21, crownValue22);
            float crownFactor5 = crownValue21 / (amounts4 * amounts4);
            float crownFactor6 = amount3 / (amounts4 * amounts4 * 0.88F * 0.88F);
            float distance = (float)Math.sqrt(crownFactor5 * crownFactor5 + crownFactor6 * crownFactor6);
            latheLoftValues[3 * intersectsCenterBound] = crownValue21 + 0.2F * crownFactor5 / distance;
            latheLoftValues[3 * intersectsCenterBound + 1] = crownValue22;
            latheLoftValues[3 * intersectsCenterBound + 2] = amount3 + 0.2F * crownFactor6 / distance;
            float min24 = Math.min(intersectsCenterBound, crownSize - 1 - intersectsCenterBound);
            crownForeValues[intersectsCenterBound] = (0.52F + 0.3F * getAmount(intersectsCenterBound, 185)) * Math.min(1.0F, 0.4F + 0.3F * min24);
         }

         latheLoft.getLatheLoft(latheLoftValues, crownForeValues, 5, shinBrr9, false);
      }
   }

   private static float getAmount(float brrForeLimit) {
      float[] floats = new float[]{23.0F, 23.6F, 24.6F, 26.0F, 27.4F, 28.4F, 29.1F};
      float[] floats2 = new float[]{0.2F, 1.05F, 1.95F, 2.75F, 3.0F, 2.7F, 1.4F};
      return !(brrForeLimit <= floats[0]) && !(brrForeLimit >= floats[floats.length - 1]) ? getAmount(brrForeLimit, floats, floats2) : 0.0F;
   }

   private static void handle(LatheLoft latheLoft, float[] brrForeValues, float brrForeValue, float brrForeValue2, int brrForeLimit, float brrForeFactor, float brrForeFactor2, float brrForeValue3, float latheLoftValues, ShinBrr shinBrr) {
      int brrForeValue5 = LatheLoft.getValue(brrForeValues);
      float[] floats = new float[3 * brrForeLimit];

      for (int i = 0; i < brrForeLimit; i++) {
         float brrForeFactor3 = (float)i / (brrForeLimit - 1);
         float brrForeValue6 = (brrForeValue + (brrForeValue2 - brrForeValue) * brrForeFactor3) * (brrForeValue5 - 1);
         int min7 = Math.min(brrForeValue5 - 2, (int)brrForeValue6);
         float deltaVar = brrForeValue6 - min7;
         double angle = brrForeValue3 + brrForeFactor3 * brrForeFactor2 * 2.0F * Math.PI;

         for (int j = 0; j < 3; j++) {
            floats[3 * i + j] = brrForeValues[3 * min7 + j] + (brrForeValues[3 * min7 + 3 + j] - brrForeValues[3 * min7 + j]) * deltaVar;
         }

         floats[3 * i] = floats[3 * i] + brrForeFactor * (float)Math.cos(angle);
         floats[3 * i + 2] = floats[3 * i + 2] + brrForeFactor * (float)Math.sin(angle);
      }

      latheLoft.getLatheLoft(floats, LatheLoft.getAmounts(brrForeLimit, latheLoftValues, latheLoftValues * 0.85F, latheLoftValues * 0.6F), 4, shinBrr, false);
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuLimit = (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = (j + 0.5F) / ambalabuBonecaValue;
            float bonecaAmbalabuLimit2;
            if (bonecaAmbalabuLimit < 0.55F) {
               bonecaAmbalabuLimit2 = Math.abs(bonecaAmbalabuValue - 0.5F) / (0.5F - 0.08F * bonecaAmbalabuLimit);
            } else {
               float bonecaAmbalabuValue2 = (bonecaAmbalabuLimit - 0.55F) / 0.45F;
               float bonecaAmbalabuValue3 = bonecaAmbalabuValue < 0.5F ? 0.27F : 0.73F;
               float bonecaAmbalabuDivisor = bonecaAmbalabuValue < 0.5F ? 1.0F : 0.82F;
               if (bonecaAmbalabuLimit > 0.55F + 0.45F * bonecaAmbalabuDivisor) {
                  continue;
               }

               bonecaAmbalabuLimit2 = Math.abs(bonecaAmbalabuValue - bonecaAmbalabuValue3) / (0.25F * (1.0F - bonecaAmbalabuValue2 / bonecaAmbalabuDivisor) + 0.02F);
            }

            if (!(bonecaAmbalabuLimit2 > 1.0F)) {
               float bonecaAmbalabuValue4 = j + 0.7F * (float)Math.sin(bonecaAmbalabuLimit * 8.0F);
               float bonecaAmbalabuBound = 0.9F
                  + 0.12F * getAmount((int)Math.floor(bonecaAmbalabuValue4 / 2.0F), 181)
                  + 0.04F * getAmount(j * 11 + i * 37, 182)
                  - 0.1F * getAmount(0.25F, 0.0F, bonecaAmbalabuLimit)
                  - 0.05F * getAmount(0.7F, 1.0F, bonecaAmbalabuLimit2);
               bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(ForeShinUtils.getValueForInt(15525853), Math.min(1.08F, bonecaAmbalabuBound)));
            }
         }
      }
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuValue = 1.0F - (i + 0.5F) / bonecaValue;
         float bonecaAmbalabuBound = 0.5F
            * ambalabuBonecaValue
            * (float)Math.pow(Math.sin(Math.PI * Math.min(1.0F, bonecaAmbalabuValue * 1.04F)), 0.75)
            * (bonecaAmbalabuValue < 0.06F ? bonecaAmbalabuValue / 0.06F * 0.35F + 0.1F : 1.0F);

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuLimit = Math.abs(j + 0.5F - ambalabuBonecaValue * 0.5F);
            if (!(bonecaAmbalabuLimit > bonecaAmbalabuBound)) {
               int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5929524), ForeShinUtils.getValueForInt(9086538), bonecaAmbalabuValue);
               int foreShinValue = ForeShinUtils.getValue2(value2, ForeShinUtils.getValueForInt(4152358), getAmount(0.7F, 1.0F, bonecaAmbalabuLimit / Math.max(0.5F, bonecaAmbalabuBound)) * 0.55F);
               float abs4 = Math.abs((i + bonecaAmbalabuLimit * 1.1F) % 11.0F - 5.5F);
               if (abs4 < 0.9F && bonecaAmbalabuLimit > 1.2F) {
                  foreShinValue = ForeShinUtils.getValue(foreShinValue, 0.82F);
               }

               if (bonecaAmbalabuLimit < 0.9F) {
                  foreShinValue = ForeShinUtils.getValueForInt(4151846);
               }

               bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(foreShinValue, 0.95F + 0.1F * getAmount(j * 17 + i * 71, 3)));
            }
         }
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuLimit, float bonecaAmbalabuValue, float bonecaAmbalabuValue2, float bonecaAmbalabuFactor, int bonecaAmbalabuValue3) {
      int valueForIntFloat = ForeShinUtils.getValueForIntFloat(11774876, 0.4F);
      int valueForIntFloat5 = ForeShinUtils.getValueForIntFloat(16777215, 0.55F);

      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float bonecaAmbalabuValue6 = getAmount(i, bonecaAmbalabuValue3) * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue7 = getAmount(i, bonecaAmbalabuValue3 + 1) * 6.28F;
         float bonecaAmbalabuValue8 = bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue + 0.25F * (bonecaAmbalabuValue2 - bonecaAmbalabuValue) * getAmount(i, bonecaAmbalabuValue3 + 2));
         float bonecaAmbalabuValue9 = bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue2 - 0.3F * (bonecaAmbalabuValue2 - bonecaAmbalabuValue) * getAmount(i, bonecaAmbalabuValue3 + 3));
         float[] floats = new float[7];
         float[] floats2 = new float[7];

         for (int j = 0; j < 7; j++) {
            floats2[j] = bonecaAmbalabuValue8 + (bonecaAmbalabuValue9 - bonecaAmbalabuValue8) * j / 6.0F;
            floats[j] = bonecaAmbalabuValue6 + 0.9F * (float)Math.sin(floats2[j] / bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor + bonecaAmbalabuValue7);
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 0.75F, i % 3 == 0 ? valueForIntFloat5 : valueForIntFloat);
      }
   }

   private static float getAmount(BonecaAmbalabu bonecaAmbalabu, float brrForeValue) {
      return bonecaAmbalabu.bonecaValue * getAmountsShinFootTable(brrForeValue);
   }

   private static float getAmount(float brrForeValue, float bonecaAmbalabuLimit) {
      float brrForeLimit = bonecaAmbalabuLimit - 29.9F;
      float brrForeDivisor = brrForeLimit > 0.0F ? 4.6F : 4.1F;
      float brrForeDivisor2 = 3.55F * (1.0F - 0.14F * Math.max(0.0F, -brrForeLimit / 4.1F));
      return (float)Math.pow(Math.pow(Math.abs(brrForeValue) / brrForeDivisor2, 2.4) + Math.pow(Math.abs(brrForeLimit) / brrForeDivisor, 2.4), 0.4166666666666667);
   }

   @Override
   public String formatNoteBlock() {
      return "block.note_block.didgeridoo";
   }

   private static float getAmountsForFloat(float brrForeFactor, float brrForeValue) {
      float sqrt = (float)Math.sqrt(brrForeFactor * brrForeFactor + brrForeValue / 0.88F * (brrForeValue / 0.88F));
      float brrForeTotal = shinFootTable[0];

      while (brrForeTotal < 24.0F && getAmounts(brrForeTotal) < sqrt) {
         brrForeTotal += 0.02F;
      }

      return brrForeTotal;
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float brrForeValue, float brrForeValue2, float brrForeFactor, float brrForeFactor2, float angle, int classValue, int bonecaAmbalabuValue) {
      float[] floats = new float[]{0.0F, -0.5F, 0.42F, -0.28F, 0.5F, -0.02F, 0.36F, 0.26F, 0.0F, 0.5F, -0.36F, 0.26F, -0.5F, -0.02F, -0.42F, -0.28F};
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float[] classValues = new float[floats.length];

      for (int i = 0; i < floats.length; i += 2) {
         float brrForeFactor3 = floats[i] * brrForeFactor2;
         float brrForeFactor4 = floats[i + 1] * brrForeFactor;
         classValues[i] = brrForeValue + brrForeFactor3 * cos - brrForeFactor4 * sin;
         classValues[i + 1] = brrForeValue2 + brrForeFactor3 * sin + brrForeFactor4 * cos;
      }

      bonecaAmbalabu.getClassForIntFloatArray(classValue, classValues);
      bonecaAmbalabu.getClass02(brrForeValue + 0.45F * brrForeFactor * sin, brrForeValue2 - 0.45F * brrForeFactor * cos, brrForeValue - 0.45F * brrForeFactor * sin, brrForeValue2 + 0.45F * brrForeFactor * cos, 1.0F, bonecaAmbalabuValue);
   }

   private static void handle(LatheLoft latheLoft, float[] brrForeValues, float[] crownForeValues, int brrForeValue, ShinBrr shinBrr) {
      float brrForeFactor = brrForeValues[0];
      float brrForeValue3 = brrForeValues[1];
      float brrForeValue4 = brrForeValues[2];
      float brrForeValue5 = brrForeValues[3];
      float brrForeValue6 = brrForeValues[4];
      float brrForeValue7 = getAmountsForFloat(brrForeFactor, brrForeValue3) + 0.3F;
      latheLoft.getLatheLoft(
         new float[]{brrForeFactor, brrForeValue7, brrForeValue3, brrForeFactor + brrForeValue5 * 0.45F, brrForeValue7 - brrForeValue4 * 0.5F, brrForeValue3 + brrForeValue6 * 0.8F, brrForeFactor + brrForeValue5, brrForeValue7 - brrForeValue4, brrForeValue3 + brrForeValue6}, crownForeValues, brrForeValue, shinBrr, false
      );
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float brrForeFactor, float brrForeFactor2, float brrForeFactor3, float brrForeFactor4, float brrForeFactor5, float brrForeFactor6, float brrForeValue, int bonecaAmbalabuValue) {
      float[] floats = new float[7];
      float[] floats2 = new float[7];

      for (int i = 0; i < 7; i++) {
         float brrForeFactor7 = i / 6.0F;
         float brrForeFactor8 = 1.0F - brrForeFactor7;
         float brrForeValue3 = brrForeFactor8 * brrForeFactor8 * brrForeFactor + 2.0F * brrForeFactor8 * brrForeFactor7 * brrForeFactor3 + brrForeFactor7 * brrForeFactor7 * brrForeFactor5;
         float brrForeLimit = brrForeFactor8 * brrForeFactor8 * brrForeFactor2 + 2.0F * brrForeFactor8 * brrForeFactor7 * brrForeFactor4 + brrForeFactor7 * brrForeFactor7 * brrForeFactor6;
         floats[i] = getBonecaAmbalabu(bonecaAmbalabu, brrForeValue3, brrForeLimit);
         floats2[i] = getAmount(bonecaAmbalabu, brrForeLimit);
      }

      handle(bonecaAmbalabu, floats, floats2, brrForeValue, bonecaAmbalabuValue);
   }

   private static float getAmountForFloat(float brrForeValue) {
      return getAmount3(brrForeValue * 1.45F, 31.35F) + 0.1F;
   }

   private static float getAmount() {
      float brrForeLimit = getAmount2(0.4F, -0.75F);
      float brrForeValue = 0.0F;
      float angle2 = 1.4F;

      for (int i = 0; i < 30; i++) {
         float angle = 0.5F * (brrForeValue + angle2);
         if (getAmount2(angle, -0.1F) < brrForeLimit) {
            brrForeValue = angle;
         } else {
            angle2 = angle;
         }
      }

      return 0.5F * (brrForeValue + angle2);
   }

   private static int[] getValues(int brrForeFactor, int bonecaValue, float brrForeValue, int brrForeValue2) {
      int[] ints = new int[brrForeFactor * bonecaValue];
      float[] brrForeValues = getAmounts(brrForeFactor, bonecaValue, 10.0F * brrForeValue, brrForeValue2, ints);
      float[] amounts2 = getAmounts(brrForeFactor, bonecaValue, 5.5F * brrForeValue, brrForeValue2 + 5, null);
      int[] ints2 = new int[brrForeFactor * bonecaValue];

      for (int i = 0; i < ints2.length; i++) {
         float brrForeValue3 = brrForeValues[i] * (0.62F + 0.42F * amounts2[i]) * (0.94F + 0.12F * getAmount(i, brrForeValue2));
         ints2[i] = ForeShinUtils.getValue(ints[i], Math.min(1.1F, brrForeValue3 * 0.96F));
      }

      return ints2;
   }

   private static float getAmount2(float angle, float brrForeValue) {
      float brrForeFactor = -9.799999F;
      float brrForeFactor2 = -0.25F;
      float brrForeFactor3 = -9.6F;
      float brrForeFactor4 = 0.85F;
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float cos2 = (float)Math.cos(angle + brrForeValue);
      float sin2 = (float)Math.sin(angle + brrForeValue);
      return brrForeFactor * cos - brrForeFactor2 * sin + brrForeFactor3 * cos2 - brrForeFactor4 * sin2 - (brrForeFactor + brrForeFactor3);
   }

   private static float getAmountForFloatFloatArray(float brrForeLimit, float[] brrForeValues, float[] brrForeValues2) {
      if (brrForeLimit <= brrForeValues[0]) {
         return brrForeValues2[0];
      } else {
         int brrForeValue = brrForeValues.length;
         if (brrForeLimit >= brrForeValues[brrForeValue - 1]) {
            return brrForeValues2[brrForeValue - 1];
         } else {
            int index = 0;

            while (brrForeValues[index + 1] < brrForeLimit) {
               index++;
            }

            return brrForeValues2[index] + (brrForeValues2[index + 1] - brrForeValues2[index]) * (brrForeLimit - brrForeValues[index]) / (brrForeValues[index + 1] - brrForeValues[index]);
         }
      }
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      int[] bonecaAmbalabuValues = getValues(ambalabuBonecaValue, 2 * bonecaValue, 0.85F, 23);

      for (int i = 0; i < bonecaValue; i++) {
         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = 0.5F + 0.5F * (float)Math.cos(((j + 0.5F) / ambalabuBonecaValue - 0.5F) * (float) (Math.PI * 2));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(bonecaAmbalabuValues[2 * i * ambalabuBonecaValue + j], 0.8F + 0.28F * bonecaAmbalabuValue));
         }
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 70, 233);
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      int[] bonecaAmbalabuValues = getValues(bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, 0.8F, 13);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValues[i * bonecaAmbalabu.ambalabuBonecaValue + j]);
         }
      }
   }

   private static float getAmount3(float brrForeValue) {
      return getAmount(Math.abs(brrForeValue), new float[]{0.3F, 1.7F, 2.75F}, new float[]{22.9F, 24.1F, 25.6F});
   }

   @Override
   public String formatTungDin() {
      return "brr_brr_patapim";
   }

   private static void handleBonecaAmbalabu8(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      byte bonecaAmbalabuLimit = 5;

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuValue = 1.0F - (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue2 = j + 0.5F;

            for (int k = 0; k < bonecaAmbalabuLimit; k++) {
               float bonecaAmbalabuLimit2 = 0.04F + 0.38F * getAmount(k, 121);
               if (!(bonecaAmbalabuValue < bonecaAmbalabuLimit2)) {
                  float bonecaAmbalabuValue3 = (bonecaAmbalabuValue - bonecaAmbalabuLimit2) / (1.0F - bonecaAmbalabuLimit2);
                  float bonecaAmbalabuValue4 = (k + 0.5F) * ambalabuBonecaValue / bonecaAmbalabuLimit + (getAmount(k, 122) - 0.5F) * 2.0F + (1.0F - bonecaAmbalabuValue3) * (getAmount(k, 123) - 0.5F) * 3.0F;
                  float bonecaAmbalabuLimit3 = 0.35F + 1.1F * bonecaAmbalabuValue3;
                  if (Math.abs(bonecaAmbalabuValue2 - bonecaAmbalabuValue4) < bonecaAmbalabuLimit3) {
                     int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14201738), ForeShinUtils.getValueForInt(9069376), getAmount(0.05F, 0.85F, bonecaAmbalabuValue3));
                     bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(foreShinValue, 0.92F + 0.12F * getAmount(k * 31 + i, 124)));
                     break;
                  }
               }
            }
         }
      }
   }

   private static void handleBonecaAmbalabu9(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            int value2 = ForeShinUtils.getValue2(
               ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14268050), ForeShinUtils.getValueForInt(13148284), 0.45F),
               ForeShinUtils.getValueForInt(13148284),
               getAmount(0.05F, 0.75F, bonecaAmbalabuValue)
            );
            int foreShinValue = ForeShinUtils.getValue2(value2, ForeShinUtils.getValueForInt(12880492), 0.08F * (1.0F - getAmount(0.25F, 0.9F, bonecaAmbalabuValue)));
            int bonecaAmbalabuValue4 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(10253140), 0.4F * getAmount(0.7F, 1.0F, bonecaAmbalabuValue));
            float bonecaAmbalabuValue5 = 0.5F + 0.5F * (float)Math.cos(((j + 0.5F) / ambalabuBonecaValue - 0.5F) * (float) (Math.PI * 2));
            int bonecaAmbalabuValue6 = ForeShinUtils.getValue2(bonecaAmbalabuValue4, ForeShinUtils.getValueForInt(10253140), 0.3F * bonecaAmbalabuValue5 * getAmount(0.35F, 0.9F, bonecaAmbalabuValue));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(bonecaAmbalabuValue6, 0.97F + 0.06F * getAmount(j * 13 + i * 57, 15)));
         }
      }

      bonecaAmbalabu.getClassForFloatInt(
         bonecaAmbalabu.ambalabuBonecaValue * 0.15F,
         bonecaAmbalabu.bonecaValue * 0.62F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * 0.42F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.85F,
         bonecaAmbalabu.bonecaValue * 0.62F,
         1.4F,
         ForeShinUtils.getValueForIntFloat(9201737, 0.4F)
      );
   }

   @Override
   public int getValue() {
      return 2;
   }

   private static float getAmount3(float brrForeValue, float brrForeLimit) {
      float brrForeDivisor = Math.max(0.01F, getAmounts(brrForeLimit));
      float brrForeFactor = brrForeValue / brrForeDivisor;
      return -brrForeDivisor * 0.88F * (float)Math.sqrt(Math.max(0.0F, 1.0F - brrForeFactor * brrForeFactor));
   }

   private static float getAmount(int brrForeValue, int brrForeValue2) {
      int brrForeValue3 = brrForeValue * 374761393 + brrForeValue2 * 668265263 + 1013904223;
      int brrForeValue4 = (brrForeValue3 ^ brrForeValue3 >>> 13) * 1274126177;
      int brrForeValue5 = brrForeValue4 ^ brrForeValue4 >>> 16;
      return (brrForeValue5 & 16777215) / 1.6777215E7F;
   }

   private static void handleBonecaAmbalabu10(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaValue;
         int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13212284), ForeShinUtils.getValueForInt(13802622), getAmount(0.1F, 0.6F, bonecaAmbalabuValue));
         int bonecaAmbalabuValue3 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(13472888), getAmount(0.6F, 1.0F, bonecaAmbalabuValue));
         int bonecaAmbalabuValue4 = ForeShinUtils.getValue2(bonecaAmbalabuValue3, ForeShinUtils.getValueForInt(13208692), getAmount(0.85F, 1.0F, bonecaAmbalabuValue));
         float bonecaAmbalabuFactor = (bonecaAmbalabuValue - 0.45F) / 0.05F;
         int bonecaAmbalabuValue5 = ForeShinUtils.getValue2(bonecaAmbalabuValue4, ForeShinUtils.getValueForInt(10381394), 0.3F * (float)Math.exp(-bonecaAmbalabuFactor * bonecaAmbalabuFactor));

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue6 = (j + 0.5F) / ambalabuBonecaValue;
            float bonecaAmbalabuFactor2 = 0.5F + 0.5F * (float)Math.cos((bonecaAmbalabuValue6 - 0.5F) * (float) (Math.PI * 2));
            int bonecaAmbalabuValue7 = ForeShinUtils.getValue2(bonecaAmbalabuValue5, ForeShinUtils.getValueForInt(15446682), bonecaAmbalabuFactor2 * getAmount(0.5F, 0.82F, bonecaAmbalabuValue) * 0.4F);
            int bonecaAmbalabuValue8 = ForeShinUtils.getValue2(bonecaAmbalabuValue7, ForeShinUtils.getValueForInt(11564130), (1.0F - bonecaAmbalabuFactor2) * 0.25F);
            int bonecaAmbalabuValue9 = ForeShinUtils.getValue(bonecaAmbalabuValue8, 0.96F + 0.08F * getAmount(j / 2 * 37 + i / 2 * 101, 4));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue9);
         }
      }

      for (int k = 0; k < 4; k++) {
         float bonecaAmbalabuValue10 = bonecaValue * (0.07F + k * 0.06F);
         handleForBonecaAmbalabuFloat(bonecaAmbalabu, ambalabuBonecaValue * 0.22F, bonecaAmbalabuValue10 + 2.0F, ambalabuBonecaValue * 0.5F, bonecaAmbalabuValue10 - 2.5F, ambalabuBonecaValue * 0.78F, bonecaAmbalabuValue10 + 2.0F, 1.3F, ForeShinUtils.getValueForIntFloat(10512981, 0.6F));
         handleForBonecaAmbalabuFloat(bonecaAmbalabu, ambalabuBonecaValue * 0.24F, bonecaAmbalabuValue10 + 3.4F, ambalabuBonecaValue * 0.5F, bonecaAmbalabuValue10 - 1.1F, ambalabuBonecaValue * 0.76F, bonecaAmbalabuValue10 + 3.4F, 0.9F, ForeShinUtils.getValueForIntFloat(15580066, 0.45F));
      }

      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.5F, bonecaValue * 0.72F, 9.0F, 10.0F, ForeShinUtils.getValueForIntFloat(15776934, 0.22F));

      for (int m = -1; m <= 1; m += 2) {
         float classValue = ambalabuBonecaValue * (0.5F + m * 0.2F);
         bonecaAmbalabu.getClass022(classValue, bonecaValue * 0.84F, 6.5F, 5.5F, ForeShinUtils.getValueForIntFloat(10118224, 0.6F));
         bonecaAmbalabu.getClass022(classValue, bonecaValue * 0.845F, 4.5F, 3.5F, ForeShinUtils.getValueForInt(8015676));
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuLimit, int bonecaAmbalabuValue) {
      int valueForIntFloat = ForeShinUtils.getValueForIntFloat(4019238, 0.5F);
      int valueForIntFloat3 = ForeShinUtils.getValueForIntFloat(9086538, 0.4F);

      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float classBound = getAmount(i, bonecaAmbalabuValue) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(i, bonecaAmbalabuValue + 1) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuFactor = 3.0F + 2.0F * getAmount(i, bonecaAmbalabuValue + 2);
         float angle = 0.9F * (getAmount(i, bonecaAmbalabuValue + 3) - 0.5F);
         bonecaAmbalabu.getClass02(classBound, amountForFloat, classBound + bonecaAmbalabuFactor * (float)Math.sin(angle), amountForFloat + bonecaAmbalabuFactor * (float)Math.cos(angle), 1.0F, i % 2 == 0 ? valueForIntFloat : valueForIntFloat3);
      }
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float shinProgressFactor2 = Math.max(0.0F, cos) * shinProgressFactor;
      float squaredDistanceTo = Math.max(0.0F, -cos) * shinProgressFactor;
      float shinProgressValue = 0.3F * sin * shinProgressFactor;
      float shinProgressValue2 = shinProgressValue + 1.15F * shinProgressFactor2;
      float shinProgressValue3 = -shinProgressValue + 1.15F * squaredDistanceTo;
      float shinProgressValue4 = -1.3F * shinProgressFactor2;
      float shinProgressValue5 = -1.3F * squaredDistanceTo;
      float min6 = Math.min(getAmount2(shinProgressValue2, shinProgressValue4), getAmount2(shinProgressValue3, shinProgressValue5));
      float shinProgressValue7 = (float)Math.pow(Math.abs(sin), 6.0) * shinProgressFactor;
      float shinProgressFactor4 = (float)Math.pow(Math.max(0.0F, sin), 6.0) * shinProgressFactor;
      float shinProgressFactor5 = (float)Math.pow(Math.max(0.0F, -sin), 6.0) * shinProgressFactor;
      float shinProgressValue8 = 0.55F * (float)Math.sqrt(shinProgressFactor2) * Math.max(0.0F, sin) * (1.0F - shinProgressFactor4);
      float shinProgressValue9 = 0.55F * (float)Math.sqrt(squaredDistanceTo) * Math.max(0.0F, -sin) * (1.0F - shinProgressFactor5);
      foreShinScale.getAmounts(this.shinR, shinProgressValue2, -0.2F * shinProgressFactor2, 0.3F * shinProgressFactor2);
      foreShinScale.getAmounts(this.shinL, shinProgressValue3, 0.2F * squaredDistanceTo, -0.3F * squaredDistanceTo);
      foreShinScale.getAmounts(this.footR, shinProgressValue4, 0.0F, -0.14F * (float)Math.sin(3.0F * angle) * shinProgressFactor2);
      foreShinScale.getAmounts(this.footL, shinProgressValue5, 0.0F, 0.14F * (float)Math.sin(3.0F * angle + (float) Math.PI) * squaredDistanceTo);
      foreShinScale.getAmounts(this.foreEar, -(shinProgressValue2 + shinProgressValue4) + shinProgressValue8, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, -(shinProgressValue3 + shinProgressValue5) + shinProgressValue9, 0.0F, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(this.foreEar, 1.0F + 0.05F * shinProgressFactor4, 1.0F - 0.1F * shinProgressFactor4, 1.0F + 0.03F * shinProgressFactor4);
      foreShinScale.getAmountsForeShin2(this.foreEar, 0.0F, -0.3F * shinProgressFactor4, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(this.shinProgressForeEar, 1.0F + 0.05F * shinProgressFactor5, 1.0F - 0.1F * shinProgressFactor5, 1.0F + 0.03F * shinProgressFactor5);
      foreShinScale.getAmountsForeShin2(this.shinProgressForeEar, 0.0F, -0.3F * shinProgressFactor5, 0.0F);
      foreShinScale.getAmountsForeShin2(this.shinR, 0.0F, -min6, 0.0F);
      foreShinScale.getAmountsForeShin2(this.shinL, 0.0F, -min6, 0.0F);
      foreShinScale.getAmountsForeShin2(this.armL, 0.0F, -min6 - 0.45F * shinProgressValue7, 0.0F);
      foreShinScale.getAmounts(this.armL, -0.05F * shinProgressFactor, 0.1F * sin * shinProgressFactor, 0.09F * cos * shinProgressFactor);
      foreShinScale.getAmountsForForeEarFloat(this.armL, 1.0F + 0.03F * shinProgressValue7, 1.0F - 0.04F * shinProgressValue7 + 0.01F * poseProgress.getAgeCode(55.0F, 0.0F), 1.0F + 0.03F * shinProgressValue7);
      float max10 = Math.max(0.0F, 1.0F - shinProgressFactor);
      float shinProgressValue11 = 0.45F * (float)Math.sin(2.0F * angle + 0.4F) * shinProgressFactor;
      float shinProgressValue12 = 0.45F * (float)Math.sin(2.0F * angle + 2.0F) * shinProgressFactor;
      foreShinScale.getAmounts(this.foreR, 0.75F + 0.3F * sin * shinProgressFactor, 0.0F, 1.25F + shinProgressValue11 + 0.12F * poseProgress.getAgeCode(70.0F, 0.0F));
      foreShinScale.getAmounts(this.foreL, 0.75F - 0.3F * sin * shinProgressFactor, 0.0F, -1.75F - shinProgressValue12 - 0.12F * poseProgress.getAgeCode(70.0F, 1.3F));
      foreShinScale.getAmounts(this.foreRightSection, 0.1F, 0.0F, 0.5F + 0.4F * (float)Math.sin(2.0F * angle + 1.6F) * shinProgressFactor + 0.25F * max10 * poseProgress.getAgeCode(18.0F, 0.5F));
      foreShinScale.getAmounts(this.shinFootForeEar, 0.1F, 0.0F, -0.5F - 0.4F * (float)Math.sin(2.0F * angle + 3.1F) * shinProgressFactor - 0.25F * max10 * poseProgress.getAgeCode(18.0F, 2.1F));
      float foreShinValue = 0.28F * (float)Math.sin(2.0F * angle - 1.3F) * shinProgressFactor + 0.04F * poseProgress.getAgeCode(63.0F, 1.1F) - poseProgress.shinProgressAmount * 0.35F;
      float shinProgressValue14 = -0.2F * (float)Math.cos(angle - 0.9F) * shinProgressFactor + 0.05F * poseProgress.getAgeCode(80.0F, 0.3F);
      foreShinScale.getAmounts(this.classShinProgress, foreShinValue, 0.0F, shinProgressValue14);
      foreShinScale.getAmounts(this.brrForeForeEar, 0.04F * poseProgress.getAgeCode(73.0F, 0.7F), 0.0F, 0.05F * poseProgress.getAgeCode(90.0F, 0.0F) - 0.12F * cos * shinProgressFactor);
      float shinProgressLimit = (poseProgress.age + (poseProgress.hashCode & 127)) % 120.0F;
      float shinProgressDivisor = shinProgressLimit > 117.5F ? 0.12F : 1.0F;
      foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.eyeLeftSection, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsBoneCount(this.armL, poseProgress.shinProgressAmount * 0.45F, poseProgress.headYawRadians * 0.7F, 0.0F);
      if (shinProgressFactor < 0.3F && poseProgress.swingProgress <= 0.0F && !poseProgress.poseProgressActive && !poseProgress.vehicle && !poseProgress.sneakingPose) {
         float shinProgressFactor6 = 1.0F - shinProgressFactor / 0.3F;
         float shinProgressValue16 = (poseProgress.age + (poseProgress.hashCode & 63)) % 100.0F;
         if (shinProgressValue16 < 12.0F) {
            float shinProgressValue17 = (float)Math.sin(shinProgressValue16 * 2.1F) * shinProgressFactor6;
            foreShinScale.getAmountsBoneCount(this.armL, 0.0F, 0.0F, 0.035F * shinProgressValue17);
            foreShinScale.getAmountsBoneCount(this.classShinProgress, 0.0F, 0.0F, 0.12F * (float)Math.sin(shinProgressValue16 * 2.1F - 0.8F) * shinProgressFactor6);
         } else if (shinProgressValue16 < 27.0F) {
            float shinProgressValue18 = (shinProgressValue16 - 12.0F) / 5.0F;
            int shinProgressValue19 = (int)shinProgressValue18;
            float shinProgressFactor7 = (float)Math.sin((shinProgressValue18 - shinProgressValue19) * Math.PI) * shinProgressFactor6;
            boolean shinProgressFlag = shinProgressValue19 != 1;
            foreShinScale.getAmountsBoneCount(shinProgressFlag ? this.shinR : this.shinL, 0.5F * shinProgressFactor7, 0.0F, 0.0F);
            foreShinScale.getAmountsBoneCount(shinProgressFlag ? this.footR : this.footL, -0.75F * shinProgressFactor7, 0.0F, 0.0F);
            foreShinScale.getAmountsBoneCount(shinProgressFlag ? this.foreEar : this.shinProgressForeEar, 0.4F * shinProgressFactor7, 0.0F, 0.0F);
            foreShinScale.getAmountsBoneCount(this.classShinProgress, 0.12F * shinProgressFactor7, 0.0F, 0.0F);
            foreShinScale.getAmountsForeShin2(this.armL, 0.0F, -0.25F * shinProgressFactor7, 0.0F);
         }
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressFactor8 = swingProgress < 0.35F ? getAmount4(swingProgress / 0.35F) : 1.0F - getAmount4((swingProgress - 0.35F) / 0.13F);
         float shinProgressFactor9 = swingProgress < 0.4F ? 0.0F : (swingProgress < 0.58F ? getAmount4((swingProgress - 0.4F) / 0.12F) : 1.0F - getAmount4((swingProgress - 0.6F) / 0.25F));
         float shinProgressFactor10 = swingProgress < 0.47F ? 0.0F : (swingProgress < 0.53F ? getAmount4((swingProgress - 0.47F) / 0.06F) : 1.0F - getAmount4((swingProgress - 0.53F) / 0.3F));
         float shinProgressValue20 = 0.12F * shinProgressFactor8 + 0.4F * shinProgressFactor9;
         float shinProgressValue21 = -0.25F * shinProgressFactor8 + -0.75F * shinProgressFactor9;
         float shinProgressValue22 = 1.55F * shinProgressFactor8 + BODY_PROFILE_TOP_HEIGHT * shinProgressFactor9;
         float brrForeValue = -1.4F * shinProgressFactor8 + -0.1F * shinProgressFactor9;
         float min24 = Math.min(getAmount2(shinProgressValue20, shinProgressValue21), getAmount2(shinProgressValue22, brrForeValue));
         float amount4 = getAmount4(shinProgressValue20, shinProgressValue21);
         foreShinScale.getAmounts(this.shinR, shinProgressValue22, 0.0F, 0.3F * shinProgressFactor8);
         foreShinScale.getAmounts(this.footR, brrForeValue, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.foreEar, -(shinProgressValue22 + brrForeValue) + 0.12F * shinProgressFactor8, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinL, shinProgressValue20, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, shinProgressValue21, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, -(shinProgressValue20 + shinProgressValue21), 0.0F, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.foreEar, 1.0F + 0.06F * shinProgressFactor10, 1.0F - 0.12F * shinProgressFactor10, 1.0F + 0.04F * shinProgressFactor10);
         foreShinScale.getAmountsForeShin2(this.foreEar, 0.0F, -0.35999998F * shinProgressFactor10, 0.0F);
         foreShinScale.getAmountsForeShin2(this.shinR, 0.0F, min6 - min24, amount4);
         foreShinScale.getAmountsForeShin2(this.shinL, 0.0F, min6 - min24, amount4);
         foreShinScale.getAmountsForeShin2(this.armL, 0.0F, min6 - min24, amount4);
         foreShinScale.getAmountsBoneCount(this.armL, 0.28F * shinProgressFactor8 - 0.14F * shinProgressFactor9, 0.0F, 0.12F * shinProgressFactor8);
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.0F + 0.06F * shinProgressFactor10, 1.0F - 0.12F * shinProgressFactor10, 1.0F + 0.06F * shinProgressFactor10);
         foreShinScale.getAmountsBoneCount(this.foreR, -0.6F * shinProgressFactor8, 0.0F, 0.7F * shinProgressFactor8 + 0.3F * shinProgressFactor10);
         foreShinScale.getAmountsBoneCount(this.foreL, -0.6F * shinProgressFactor8, 0.0F, -0.7F * shinProgressFactor8 - 0.3F * shinProgressFactor10);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, -0.45F * shinProgressFactor8 + 0.8F * shinProgressFactor10, 0.0F, 0.0F);
      }

      if (poseProgress.sneakingPose) {
         float shinProgressValue26 = 0.55F;
         float shinProgressValue27 = -1.0F;
         float amount2 = getAmount2(shinProgressValue26, shinProgressValue27);
         float shinProgressValue29 = getAmount4(shinProgressValue26, shinProgressValue27);
         foreShinScale.getAmountsBoneCount(this.shinR, shinProgressValue26, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.shinL, shinProgressValue26, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.footR, shinProgressValue27, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.footL, shinProgressValue27, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.foreEar, -(shinProgressValue26 + shinProgressValue27), 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, -(shinProgressValue26 + shinProgressValue27), 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.shinR, 0.0F, -amount2, shinProgressValue29);
         foreShinScale.getAmountsForeShin2(this.shinL, 0.0F, -amount2, shinProgressValue29);
         foreShinScale.getAmountsForeShin2(this.armL, 0.0F, -amount2, shinProgressValue29);
         foreShinScale.getAmountsBoneCount(this.armL, -0.22F, 0.0F, 0.0F);
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.shinR, 1.4F, -0.12F, 0.22F);
         foreShinScale.getAmounts(this.shinL, 1.4F, 0.12F, -0.22F);
         foreShinScale.getAmounts(this.footR, -0.6F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.6F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.foreEar, -0.5F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, -0.5F, 0.0F, 0.0F);
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         foreShinScale.getAmounts(this.foreR, -0.5F, 0.0F, 1.5F);
         foreShinScale.getAmounts(this.foreL, -0.5F, 0.0F, -1.5F);
         foreShinScale.getAmounts(this.shinR, -0.2F, 0.0F, 0.1F);
         foreShinScale.getAmounts(this.shinL, -0.2F, 0.0F, -0.1F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.06F, 0.94F, 1.06F);
      }
   }

   @Override
   public float getAmountFloat() {
      return 0.7F;
   }

   private static float getAmount4(float brrForeValue) {
      return getAmount(0.0F, 1.0F, brrForeValue);
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float[] brrForeValues, float[] brrForeValues2, float brrForeValue, int bonecaAmbalabuValue) {
      float brrForeFactor = brrForeValue * 0.5F;
      float brrForeBound = Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;
      float maxValue3 = -Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;

      for (int i = 0; i < brrForeValues.length; i++) {
         brrForeBound = Math.min(brrForeBound, brrForeValues[i]);
         maxValue3 = Math.max(maxValue3, brrForeValues[i]);
         maxValue = Math.min(maxValue, brrForeValues2[i]);
         pilotStatusBound = Math.max(pilotStatusBound, brrForeValues2[i]);
      }

      float brrForeLimit = brrForeFactor * brrForeFactor;
      bonecaAmbalabu.getBonecaAmbalabu(brrForeBound - brrForeFactor, maxValue - brrForeFactor, maxValue3 + brrForeFactor, pilotStatusBound + brrForeFactor, bonecaAmbalabuValue, (brrForeValue3, ysx) -> {
         for (int var5x = 0; var5x + 1 < brrForeValues.length; var5x++) {
            float var6x = brrForeValues[var5x + 1] - brrForeValues[var5x];
            float var7x = brrForeValues2[var5x + 1] - brrForeValues2[var5x];
            float var8x = var6x * var6x + var7x * var7x;
            float var9x = var8x > 0.0F ? ((brrForeValue3 - brrForeValues[var5x]) * var6x + (ysx - brrForeValues2[var5x]) * var7x) / var8x : 0.0F;
            float var10x = var9x < 0.0F ? 0.0F : (var9x > 1.0F ? 1.0F : var9x);
            float var11x = brrForeValues[var5x] + var10x * var6x - brrForeValue3;
            float brrForeFactor2 = brrForeValues2[var5x] + var10x * var7x - ysx;
            if (var11x * var11x + brrForeFactor2 * brrForeFactor2 <= brrForeLimit) {
               return true;
            }
         }

         return false;
      });
   }

   private static float getAmountsShinFootTable(float brrForeLimit) {
      int brrForeValue = shinFootTable.length;
      float brrForeValue2;
      if (brrForeLimit <= shinFootTable[0]) {
         brrForeValue2 = 0.0F;
      } else if (brrForeLimit >= shinFootTable[brrForeValue - 1]) {
         brrForeValue2 = table[brrForeValue - 1];
      } else {
         int index = 0;

         while (shinFootTable[index + 1] < brrForeLimit) {
            index++;
         }

         float brrForeFactor = (brrForeLimit - shinFootTable[index]) / (shinFootTable[index + 1] - shinFootTable[index]);
         brrForeValue2 = table[index] + (table[index + 1] - table[index]) * brrForeFactor;
      }

      return 1.0F - brrForeValue2 / table[brrForeValue - 1];
   }

   private static float getAmount(float brrForeValue, float brrForeValue2, float bonecaAmbalabuLimit) {
      float brrForeLimit = (bonecaAmbalabuLimit - brrForeValue) / (brrForeValue2 - brrForeValue);
      float brrForeFactor = brrForeLimit < 0.0F ? 0.0F : (brrForeLimit > 1.0F ? 1.0F : brrForeLimit);
      return brrForeFactor * brrForeFactor * (3.0F - 2.0F * brrForeFactor);
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr, boolean brrForeFlag) {
      float[] pointsSpline = LatheLoft.getPointsSpline(brrForeFlag ? 1 : 2, 0.0F, 31.2F, -5.3F, 0.0F, 29.6F, -7.0F, 0.0F, 27.4F, -7.9F, 0.0F, 25.0F, -7.8F);
      int brrForeValue = LatheLoft.getValue(pointsSpline);
      float[] brrForeValues = LatheLoft.getAmounts(brrForeValue, 0.9F, 1.0F, 1.25F, 1.55F);
      float brrForeFactor = brrForeValues[brrForeValue - 1];
      float brrForeFactor2 = pointsSpline[3 * brrForeValue - 3] - pointsSpline[3 * brrForeValue - 6];
      float brrForeFactor3 = pointsSpline[3 * brrForeValue - 2] - pointsSpline[3 * brrForeValue - 5];
      float brrForeFactor4 = pointsSpline[3 * brrForeValue - 1] - pointsSpline[3 * brrForeValue - 4];
      float distance = (float)Math.sqrt(brrForeFactor2 * brrForeFactor2 + brrForeFactor3 * brrForeFactor3 + brrForeFactor4 * brrForeFactor4);
      float brrForeFactor5 = brrForeFactor2 / distance;
      float brrForeFactor6 = brrForeFactor3 / distance;
      float brrForeFactor7 = brrForeFactor4 / distance;
      float[] floats = new float[]{38.0F, 66.0F, 86.0F};
      float[] copyOf = Arrays.copyOf(pointsSpline, 3 * (brrForeValue + floats.length));
      float[] copyOf2 = Arrays.copyOf(brrForeValues, brrForeValue + floats.length);

      for (int i = 0; i < floats.length; i++) {
         double angle = Math.toRadians(floats[i]);
         float brrForeFactor8 = brrForeFactor * (float)Math.sin(angle);
         copyOf[3 * (brrForeValue + i)] = pointsSpline[3 * brrForeValue - 3] + brrForeFactor5 * brrForeFactor8;
         copyOf[3 * (brrForeValue + i) + 1] = pointsSpline[3 * brrForeValue - 2] + brrForeFactor6 * brrForeFactor8;
         copyOf[3 * (brrForeValue + i) + 2] = pointsSpline[3 * brrForeValue - 1] + brrForeFactor7 * brrForeFactor8;
         copyOf2[brrForeValue + i] = brrForeFactor * (float)Math.cos(angle);
      }

      float[] crownForeValues = new float[copyOf2.length];
      float[] floats3 = new float[copyOf2.length];

      for (int j = 0; j < copyOf2.length; j++) {
         crownForeValues[j] = copyOf2[j] * 1.05F;
         floats3[j] = copyOf2[j] * 0.95F;
      }

      latheLoft.getLoftPath(copyOf, crownForeValues, floats3, 12, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
   }

   private static void handleBonecaAmbalabu11(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         for (int j = 0; j < ambalabuBonecaValue; j++) {
            int bonecaAmbalabuValue = i * 3 + (int)Math.floor((j + 9.0F * getAmount(i, 106)) / 7.0F) * 97;
            float bonecaAmbalabuFactor = 0.82F + 0.3F * getAmount(bonecaAmbalabuValue, 107);
            float bonecaAmbalabuValue2 = 0.5F + 0.5F * (float)Math.cos(((j + 0.5F) / ambalabuBonecaValue - 0.5F) * (float) (Math.PI * 2));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(ForeShinUtils.getValueForInt(11567962), bonecaAmbalabuFactor * (0.85F + 0.2F * bonecaAmbalabuValue2)));
         }
      }

      int valueForIntFloat = ForeShinUtils.getValueForIntFloat(9069376, 0.75F);
      int valueForIntFloat4 = ForeShinUtils.getValueForIntFloat(14201738, 0.8F);

      for (int k = 0; k < 90; k++) {
         boolean bonecaAmbalabuFlag = k % 3 == 2;
         float bonecaAmbalabuValue5 = getAmount(k, 111) * bonecaValue;
         float bonecaAmbalabuValue6 = (getAmount(k, 112) - 0.5F) * 7.0F;
         float bonecaAmbalabuValue7 = bonecaAmbalabuFlag ? ambalabuBonecaValue * (0.97F - 0.08F * getAmount(k, 113)) : ambalabuBonecaValue * (0.03F + 0.1F * getAmount(k, 113));
         float hashHuntBound = bonecaAmbalabuFlag ? ambalabuBonecaValue * (0.5F + 0.08F * getAmount(k, 114)) : ambalabuBonecaValue * (0.36F + 0.16F * getAmount(k, 114));
         bonecaAmbalabu.getClass02(bonecaAmbalabuValue7, bonecaAmbalabuValue5, hashHuntBound, bonecaAmbalabuValue5 + bonecaAmbalabuValue6, 1.0F, valueForIntFloat);
         float classBound = hashHuntBound - (hashHuntBound - bonecaAmbalabuValue7) * 0.3F;
         float amountForFloat = bonecaAmbalabuValue5 + bonecaAmbalabuValue6 * 0.7F;
         bonecaAmbalabu.getClass02(classBound, amountForFloat, hashHuntBound, bonecaAmbalabuValue5 + bonecaAmbalabuValue6, 1.0F, valueForIntFloat4);
      }
   }

   private static void handleBonecaAmbalabu12(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = (j + 0.5F) / ambalabuBonecaValue;
            float bonecaAmbalabuValue2 = getAmount(0.3F, 0.5F, Math.abs(bonecaAmbalabuValue - 0.5F));
            int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13936776), ForeShinUtils.getValueForInt(15121570), bonecaAmbalabuValue2 * 0.7F);
            int bonecaAmbalabuValue4 = ForeShinUtils.getValue(foreShinValue, 0.97F + 0.05F * getAmount(j / 3 * 41 + i / 3 * 83, 8));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue4);
         }
      }

      for (int k = 0; k <= 1; k++) {
         float classValue = k * ambalabuBonecaValue;
         bonecaAmbalabu.getClass022(classValue, bonecaValue * 0.1F, ambalabuBonecaValue * 0.1F, bonecaValue * 0.085F, ForeShinUtils.getValueForIntFloat(13211258, 0.45F));
         bonecaAmbalabu.getClass022(classValue, bonecaValue * 0.8F, ambalabuBonecaValue * 0.11F, bonecaValue * 0.1F, ForeShinUtils.getValueForIntFloat(13211258, 0.45F));
         bonecaAmbalabu.getClass022(classValue, bonecaValue * 0.45F, ambalabuBonecaValue * 0.07F, bonecaValue * 0.12F, ForeShinUtils.getValueForIntFloat(11896426, 0.3F));
         float bonecaAmbalabuFactor = k == 0 ? 1.0F : -1.0F;
         handleForBonecaAmbalabuFloat(
            bonecaAmbalabu,
            classValue + bonecaAmbalabuFactor * ambalabuBonecaValue * 0.13F,
            bonecaValue * 0.02F,
            classValue + bonecaAmbalabuFactor * ambalabuBonecaValue * 0.17F,
            bonecaValue * 0.12F,
            classValue + bonecaAmbalabuFactor * ambalabuBonecaValue * 0.3F,
            bonecaValue * 0.17F,
            1.3F,
            ForeShinUtils.getValueForIntFloat(11041888, 0.4F)
         );
         handleForBonecaAmbalabuFloat(
            bonecaAmbalabu,
            classValue + bonecaAmbalabuFactor * ambalabuBonecaValue * 0.16F,
            bonecaValue * 0.02F,
            classValue + bonecaAmbalabuFactor * ambalabuBonecaValue * 0.2F,
            bonecaValue * 0.1F,
            classValue + bonecaAmbalabuFactor * ambalabuBonecaValue * 0.31F,
            bonecaValue * 0.13F,
            1.0F,
            ForeShinUtils.getValueForIntFloat(15583408, 0.4F)
         );
      }

      for (int m = 0; m < 3; m++) {
         float bonecaAmbalabuValue6 = bonecaValue * (0.42F + m * 0.035F);
         handleForBonecaAmbalabuFloat(bonecaAmbalabu, ambalabuBonecaValue * 0.32F, bonecaAmbalabuValue6 + 2.0F, ambalabuBonecaValue * 0.5F, bonecaAmbalabuValue6 - 2.0F, ambalabuBonecaValue * 0.68F, bonecaAmbalabuValue6 + 2.0F, 1.2F, ForeShinUtils.getValueForIntFloat(11041888, 0.55F));
      }

      for (int p = 0; p < 5; p++) {
         float bonecaAmbalabuValue7 = ambalabuBonecaValue * (0.36F + p * 0.07F);
         handleForBonecaAmbalabuFloat(bonecaAmbalabu, bonecaAmbalabuValue7 - 3.0F, bonecaValue * 0.94F, bonecaAmbalabuValue7, bonecaValue * 0.9F, bonecaAmbalabuValue7 + 3.0F, bonecaValue * 0.94F, 1.0F, ForeShinUtils.getValueForIntFloat(11041888, 0.6F));
      }

      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.5F, bonecaValue * 0.33F, ambalabuBonecaValue * 0.22F, bonecaValue * 0.13F, ForeShinUtils.getValueForIntFloat(10253140, 0.3F));
      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.5F, bonecaValue * 0.33F, ambalabuBonecaValue * 0.15F, bonecaValue * 0.09F, ForeShinUtils.getValueForIntFloat(10253140, 0.4F));
      handleForBonecaAmbalabuFloat(bonecaAmbalabu, ambalabuBonecaValue * 0.42F, bonecaValue * 0.55F, ambalabuBonecaValue * 0.47F, bonecaValue * 0.7F, ambalabuBonecaValue * 0.43F, bonecaValue * 0.85F, 1.2F, ForeShinUtils.getValueForIntFloat(11569790, 0.35F));
      handleForBonecaAmbalabuFloat(bonecaAmbalabu, ambalabuBonecaValue * 0.57F, bonecaValue * 0.55F, ambalabuBonecaValue * 0.53F, bonecaValue * 0.7F, ambalabuBonecaValue * 0.58F, bonecaValue * 0.85F, 1.2F, ForeShinUtils.getValueForIntFloat(11569790, 0.35F));
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float brrForeLimit) {
      return bonecaAmbalabu.ambalabuBonecaValue * LatheLoft.getAmount(bonecaAmbalabuValue, Math.max(0.01F, getAmounts(brrForeLimit)));
   }

   private static float getAmountsFloat(float brrForeValue) {
      float brrForeLimit2 = shinFootTable[0];
      float brrForeLimit3 = shinFootTable[shinFootTable.length - 1];

      for (int i = 0; i < 22; i++) {
         float brrForeLimit = 0.5F * (brrForeLimit2 + brrForeLimit3);
         if (getAmountsShinFootTable(brrForeLimit) > brrForeValue) {
            brrForeLimit2 = brrForeLimit;
         } else {
            brrForeLimit3 = brrForeLimit;
         }
      }

      return 0.5F * (brrForeLimit2 + brrForeLimit3);
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.bodyCrownFlag = this.bodyCrownCount++ > 0;
      this.armL = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 21.6F, 0.4F);
      this.brrForeForeEar = latheLoft.getClassForStringForeEar("crown", this.armL, 0.0F, 37.6F, 0.2F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("nose", this.armL, 0.0F, 30.9F, -5.6F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("eye_r", this.armL, 1.45F, 31.35F, getAmountForFloat(1.0F));
      this.eyeLeftSection = latheLoft.getClassForStringForeEar("eye_l", this.armL, -1.45F, 31.35F, getAmountForFloat(-1.0F));
      this.foreR = latheLoft.getClassForStringForeEar("arm_r", this.armL, 4.7F, 35.3F, 0.5F);
      this.foreRightSection = latheLoft.getClassForStringForeEar("fore_r", this.foreR, 5.0F, 31.0F, 0.5F);
      this.foreL = latheLoft.getClassForStringForeEar("arm_l", this.armL, -4.7F, 35.3F, 0.5F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("fore_l", this.foreL, -5.0F, 31.0F, 0.5F);
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", latheLoft.getClassForeEarList(), 2.5F, 22.4F, 0.5F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 1.7F, 12.6F, 0.25F);
      this.foreEar = latheLoft.getClassForStringForeEar("foot_r", this.footR, 2.6F, 3.0F, 1.1F);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", latheLoft.getClassForeEarList(), -2.5F, 22.4F, 0.5F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -1.7F, 12.6F, 0.25F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -2.6F, 3.0F, 1.1F);
      latheLoft.getLatheLoft(this.foreR);
      LoftLatheFlag.Region region = latheLoft.getMax(384, 256);
      handleBonecaAmbalabuBonecaAmbalabuTable(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(64, 128);
      handleBonecaAmbalabu10(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(64, 128);
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu(), 6178352, 3, true);
      LoftLatheFlag.Region max4 = latheLoft.getMax(128, 128);
      handleBonecaAmbalabu12(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(32, 96);
      handleBonecaAmbalabu11(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(32, 96);
      handleBonecaAmbalabu(max6.getClassBonecaAmbalabu(), 7229235, 9, false);
      LoftLatheFlag.Region max7 = latheLoft.getMax(128, 64);
      handleBonecaAmbalabuForBonecaAmbalabu(max7.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max8 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu7(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(32, 64);
      handleBonecaAmbalabu5(max9.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max10 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu(max10.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max11 = latheLoft.getMax(32, 32);
      handleBonecaAmbalabu13(max11.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max12 = latheLoft.getMax(32, 16);
      handleBonecaAmbalabu3(max12.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max13 = latheLoft.getMax(48, 48);
      handleBonecaAmbalabu15(max13.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max14 = latheLoft.getMax(16, 32);
      handleBonecaAmbalabu8(max14.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max15 = latheLoft.getMax(32, 32);
      handleBonecaAmbalabu9(max15.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max16 = latheLoft.getMax(16, 32);
      handleBonecaAmbalabu4(max16.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max17 = latheLoft.getMax(32, 112);
      handleBonecaAmbalabu6(max17.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max8);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max9);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max14);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max10);
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max11);
      ShinBrr classForRegion11 = latheLoft.getClassForRegion(max12);
      ShinBrr classForRegion12 = latheLoft.getClassForRegion(max15);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max16);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max17);
      ShinBrr classForRegion15 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion16 = latheLoft.getClassForRegion(max6);
      ShinBrr classForRegion17 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion18 = latheLoft.getClassForRegion(max13);
      ShinBrr shinBrr = latheLoft.getMax(13148284);
      ShinBrr shinBrr2 = latheLoft.getMax(4870441);
      latheLoft.getLatheLoftForForeEar(this.armL);
      latheLoft.getLatheProfile(brrForeTable, shinFootTable, 18, 1.0F, 0.88F, classForRegion, null, null);
      handle(latheLoft, classForRegion3, classForRegion5, classForRegion5.getFlag(11715228), shinBrr2, classForRegion4, this.bodyCrownFlag);
      this.handleCrownAmounts(latheLoft, classForRegion11, classForRegion7, classForRegion8, classForRegion9, classForRegion10, classForRegion12, classForRegion3, classForRegion13, classForRegion14);
      this.handleArm(latheLoft, this.classShinFoot, 1.0F);
      this.handleArm(latheLoft, this.eyeLeftSection, -1.0F);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      handle(latheLoft, classForRegion6, this.bodyCrownFlag);
      latheLoft.getLatheLoftForForeEar(this.brrForeForeEar);
      handle(latheLoft, classForRegion5, classForRegion4, classForRegion16);
      this.handleCrownBody(latheLoft, 1.0F, this.foreR, this.foreRightSection, classForRegion16, classForRegion4, classForRegion5, shinBrr);
      this.handleCrownBody(latheLoft, -1.0F, this.foreL, this.shinFootForeEar, classForRegion16, classForRegion4, classForRegion5, shinBrr);
      this.handleCrownBody(latheLoft, 1.0F, this.shinR, this.footR, this.foreEar, classForRegion15, classForRegion4, classForRegion17, classForRegion18);
      this.handleCrownBody(latheLoft, -1.0F, this.shinL, this.footL, this.shinProgressForeEar, classForRegion15, classForRegion4, classForRegion17, classForRegion18);
   }

   private static void handleBonecaAmbalabu13(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuLimit = (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = (j + 0.5F) / ambalabuBonecaValue;
            int bonecaAmbalabuValue2 = j / 2;
            float bonecaAmbalabuValue3 = 0.78F + 0.2F * getAmount(bonecaAmbalabuValue2, 131);
            if (!(bonecaAmbalabuLimit > bonecaAmbalabuValue3)) {
               float bonecaAmbalabuValue4 = j + 0.8F * (float)Math.sin(bonecaAmbalabuLimit * 7.0F + bonecaAmbalabuValue2);
               int brrForeValue = (int)Math.floor(bonecaAmbalabuValue4 / 1.6F);
               float bonecaAmbalabuValue6 = 0.5F + 0.5F * (float)Math.cos((bonecaAmbalabuValue - 0.5F) * (float) (Math.PI * 2));
               float bonecaAmbalabuValue7 = getAmount(brrForeValue, 132);
               float bonecaAmbalabuBound = Math.max(0.84F, 0.86F + 0.18F * bonecaAmbalabuValue7 + 0.08F * bonecaAmbalabuValue6 - 0.06F * getAmount(0.6F, 1.0F, bonecaAmbalabuLimit));
               bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(ForeShinUtils.getValueForInt(15525853), Math.min(1.08F, bonecaAmbalabuBound)));
            }
         }
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 18, 0.0F, 0.74F, 7.0F, 197);
   }

   private void handleCrownBody(LatheLoft latheLoft, float crownBodyFactor, ForeEar foreEar, ForeEar foreEar2, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      latheLoft.getLatheLoftForForeEar(foreEar);
      float crownBodyValue = crownBodyFactor * 5.0F;
      float[] pointsSpline = LatheLoft.getPointsSpline(this.bodyCrownFlag ? 1 : 2, crownBodyFactor * 4.1499996F, 35.5F, 0.5F, crownBodyFactor * 5.0499997F, 33.2F, 0.6F, crownBodyValue, 31.0F, 0.5F);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 0.55F, 0.44F, 0.42F), 6, shinBrr, false);
      latheLoft.getLatheLoft(crownBodyValue, 31.0F, 0.5F, 0.55F, 0.62F, 0.55F, 6, shinBrr);
      float[] floats = new float[]{crownBodyFactor * 4.1F, 35.75F, 0.5F, crownBodyFactor * 4.5499997F, 34.75F, 0.55F, crownBodyFactor * 4.85F, 34.1F, 0.5F};
      latheLoft.getLoftPath(floats, new float[]{1.05F, 0.8F, 0.35F}, new float[]{1.05F, 0.8F, 0.35F}, 5, shinBrr2, null, null, 0.0F, 0.0F, -1.0F);
      handle(latheLoft, crownBodyValue + crownBodyFactor * 0.3F, 31.1F, 0.4F, 0.0F, 15.0F, -crownBodyFactor * 70.0F, 1.9F, 1.15F, shinBrr3);
      latheLoft.getLatheLoftForForeEar(foreEar2);
      float latheLoftValue = crownBodyFactor * 5.2F;
      float crownBodyValue3 = 28.0F;
      float dinForeValue = 0.4F;
      latheLoft.getLatheLoft(new float[]{crownBodyValue, 31.0F, 0.5F, crownBodyFactor * 5.25F, 29.6F, 0.45F, latheLoftValue, crownBodyValue3, dinForeValue}, new float[]{0.42F, 0.37F, 0.32F}, 6, shinBrr, false);
      latheLoft.getLatheLoft(new float[]{crownBodyFactor * 5.15F, 30.1F, 0.5F, crownBodyFactor * 5.85F, 29.4F, 0.4F}, new float[]{0.17F, 0.05F}, 4, shinBrr, false);
      float eyeValue = crownBodyValue3 - 0.62F;
      latheLoft.getLatheLoft(latheLoftValue, eyeValue, dinForeValue, 0.56F, 0.66F, 0.3F, 6, shinBrr4);
      float[] floats2 = new float[]{-0.36F, -0.12F, 0.12F, 0.36F};
      float[] floats3 = new float[]{0.72F, 0.88F, 0.84F, 0.7F};

      for (int i = 0; i < 4; i++) {
         float crownBodyValue6 = latheLoftValue + crownBodyFactor * floats2[i];
         float crownBodyValue7 = latheLoftValue + crownBodyFactor * floats2[i] * 1.55F;
         float crownBodyValue8 = eyeValue - 0.5F;
         float crownBodyValue9 = crownBodyValue8 - floats3[i];
         latheLoft.getLatheLoft(new float[]{crownBodyValue6, crownBodyValue8 + 0.15F, dinForeValue, crownBodyValue7, crownBodyValue9, dinForeValue - 0.12F}, new float[]{0.16F, 0.07F}, 4, shinBrr4, false);
      }

      latheLoft.getLatheLoft(
         new float[]{latheLoftValue + crownBodyFactor * 0.42F, eyeValue + 0.15F, dinForeValue, latheLoftValue + crownBodyFactor * 1.02F, eyeValue - 0.66F, dinForeValue - 0.14F}, new float[]{0.17F, 0.08F}, 4, shinBrr4, false
      );
   }

   private static float getAmounts4(float brrForeLimit) {
      float brrForeTotal = shinFootTable[shinFootTable.length - 1];

      while (brrForeTotal > 30.0F && getAmounts(brrForeTotal) < brrForeLimit) {
         brrForeTotal -= 0.02F;
      }

      return brrForeTotal;
   }

   private static void handle(LatheLoft latheLoft, float degrees, float brrForeValue, float brrForeFactor, float brrForeValue2, float brrForeValue3, float brrForeValue4, ShinBrr shinBrr) {
      float angle = (float)Math.toRadians(degrees);
      float latheLoftValue = brrForeFactor * (float)Math.sin(angle);
      float armTrunkValue = -brrForeFactor * 0.88F * (float)Math.cos(angle);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, brrForeValue, armTrunkValue).getLatheLoft4(-degrees).getLatheLoftLatheLoftAmountLoft(brrForeValue4);
      latheLoft.getLatheLoft(-brrForeValue2 * 0.5F, -brrForeValue3, -0.03F, brrForeValue2 * 0.5F, 0.0F, 0.03F, shinBrr, shinBrr, null, null, null, null);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabuBonecaAmbalabuTable(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      float[] floats = new float[bonecaValue];

      for (int i = 0; i < bonecaValue; i++) {
         floats[i] = getAmountsFloat((i + 0.5F) / bonecaValue);
      }

      int[] bonecaAmbalabuValues = getValues(ambalabuBonecaValue, bonecaValue, 1.0F, 1);

      for (int j = 0; j < bonecaValue; j++) {
         float bonecaAmbalabuValue = floats[j];
         float foreShinFactor = 1.0F - 0.4F * getAmount(24.5F, 20.6F, bonecaAmbalabuValue) + 0.06F * getAmount(34.0F, 38.0F, bonecaAmbalabuValue);

         for (int k = 0; k < ambalabuBonecaValue; k++) {
            bonecaAmbalabu.getBonecaAmbalabu(k, j, ForeShinUtils.getValue(bonecaAmbalabuValues[j * ambalabuBonecaValue + k], foreShinFactor));
         }
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 400, 151);

      for (int m = 0; m < 70; m++) {
         float brrForeValue = getAmount(m, 41) * ambalabuBonecaValue;
         float bonecaAmbalabuValue4 = getAmount(m, 42) * bonecaValue * 0.92F;
         float brrForeFactor = 7.0F + 6.0F * getAmount(m, 43);
         handle(
            bonecaAmbalabu,
            brrForeValue,
            bonecaAmbalabuValue4,
            brrForeFactor,
            brrForeFactor * 0.55F,
            getAmount(m, 44) * 6.28F,
            ForeShinUtils.getValueForIntFloat(getAmount(m, 45) < 0.7F ? 8034878 : 9086538, 0.8F),
            ForeShinUtils.getValueForIntFloat(4019238, 0.7F)
         );
      }

      for (int p = 0; p < 6; p++) {
         float bonecaAmbalabuTotal = (0.12F + p * 0.15F + 0.05F * getAmount(p, 51)) * ambalabuBonecaValue;
         if (p == 2 || p == 3) {
            bonecaAmbalabuTotal += ambalabuBonecaValue * 0.5F;
         }

         float bonecaAmbalabuValue6 = bonecaAmbalabuTotal % ambalabuBonecaValue;
         float bonecaAmbalabuValue7 = getAmount(p, 52) * bonecaValue * 0.3F;
         int bonecaAmbalabuSize = 9;
         float[] floats2 = new float[bonecaAmbalabuSize];
         float[] floats3 = new float[bonecaAmbalabuSize];

         for (int q = 0; q < bonecaAmbalabuSize; q++) {
            floats2[q] = bonecaAmbalabuValue6 + (float)Math.sin(q * 1.1F + p) * 5.0F;
            floats3[q] = bonecaAmbalabuValue7 + q * (bonecaValue * 0.85F - bonecaAmbalabuValue7) / (bonecaAmbalabuSize - 1);
         }

         for (int r = 0; r + 1 < bonecaAmbalabuSize; r++) {
            bonecaAmbalabu.getClass02(floats2[r], floats3[r], floats2[r + 1], floats3[r + 1], 2.4F, ForeShinUtils.getValueForIntFloat(3096346, 0.7F));
            bonecaAmbalabu.getClass02(floats2[r] - 0.6F, floats3[r], floats2[r + 1] - 0.6F, floats3[r + 1], 0.9F, ForeShinUtils.getValueForIntFloat(6454328, 0.55F));
         }
      }

      int floorBonecaAmbalabu = (int)Math.floor(getBonecaAmbalabu(bonecaAmbalabu, 4.4F, 29.9F));
      int ceilBonecaAmbalabu = (int)Math.ceil(getBonecaAmbalabu(bonecaAmbalabu, -4.4F, 29.9F));
      int bonecaAmbalabuBound = (int)Math.floor(getAmount(bonecaAmbalabu, 35.42F));
      int spotBound = (int)Math.ceil(getAmount(bonecaAmbalabu, 22.8F));

      for (int s = Math.max(0, bonecaAmbalabuBound); s <= Math.min(bonecaValue - 1, spotBound); s++) {
         float bonecaAmbalabuLimit = floats[s];
         float amounts8 = getAmounts(bonecaAmbalabuLimit);

         for (int t = Math.max(0, floorBonecaAmbalabu); t <= Math.min(ambalabuBonecaValue - 1, ceilBonecaAmbalabu); t++) {
            float angle = (float) (Math.PI * 2) * ((t + 0.5F) / ambalabuBonecaValue - 0.5F);
            float bonecaAmbalabuValue9 = -amounts8 * (float)Math.sin(angle);
            int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(t, s);
            float bonecaAmbalabuLimit2 = getAmount(bonecaAmbalabuValue9, bonecaAmbalabuLimit);
            if (bonecaAmbalabuLimit2 < 1.16F) {
               if (bonecaAmbalabuLimit2 >= 1.0F) {
                  bonecaAmbalabuForInt = ForeShinUtils.getValue(bonecaAmbalabuForInt, 0.5F + 0.5F * getAmount(1.0F, 1.16F, bonecaAmbalabuLimit2));
               } else {
                  int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14268050), ForeShinUtils.getValueForInt(13148284), getAmount(0.2F, 0.75F, bonecaAmbalabuLimit2));
                  int bonecaAmbalabuValue11 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(10253140), getAmount(0.78F, 1.0F, bonecaAmbalabuLimit2) * (bonecaAmbalabuLimit > 29.9F ? 0.95F : 0.7F));
                  int valueForInt = ForeShinUtils.getValue(bonecaAmbalabuValue11, 0.97F + 0.06F * getAmount(t * 31 + s * 1013, 7));
                  bonecaAmbalabuForInt = ForeShinUtils.getValue2(bonecaAmbalabuForInt, valueForInt, Math.min(1.0F, (1.0F - bonecaAmbalabuLimit2) * 30.0F));
               }
            }

            float bonecaAmbalabuLimit3 = getAmount(bonecaAmbalabuLimit);
            if (bonecaAmbalabuLimit3 > 0.0F && Math.abs(bonecaAmbalabuValue9) < bonecaAmbalabuLimit3 + 0.6F) {
               float bonecaAmbalabuValue13 = Math.abs(bonecaAmbalabuValue9) / bonecaAmbalabuLimit3;
               int floor14 = (int)Math.floor((t + 0.18F * s) / 2.2F);
               float hashHuntBound = 0.92F + 0.12F * getAmount(floor14, 9) + 0.04F * getAmount(t * 7 + s * 131, 10);
               int bonecaAmbalabuValue15 = ForeShinUtils.getValue(ForeShinUtils.getValueForInt(15525853), Math.min(1.08F, hashHuntBound));
               float bonecaAmbalabuBound4 = getAmount(0.85F, 1.0F, bonecaAmbalabuValue13) + getAmount(28.5F, 29.1F, bonecaAmbalabuLimit) * 0.5F;
               int bonecaAmbalabuValue16 = ForeShinUtils.getValue2(bonecaAmbalabuValue15, ForeShinUtils.getValueForInt(11122826), Math.min(1.0F, bonecaAmbalabuBound4) * 0.35F);
               float min4 = Math.min(1.0F, (1.05F - bonecaAmbalabuValue13) * 6.0F + 0.35F * getAmount(floor14, 11));
               if (min4 > 0.0F) {
                  bonecaAmbalabuForInt = ForeShinUtils.getValue2(bonecaAmbalabuForInt, bonecaAmbalabuValue16, Math.min(1.0F, min4));
               }
            }

            bonecaAmbalabu.getBonecaAmbalabu(t, s, bonecaAmbalabuForInt);
         }
      }

      int valueForIntFloat = ForeShinUtils.getValueForIntFloat(8017212, 0.75F);
      int valueForIntFloat18 = ForeShinUtils.getValueForIntFloat(9201737, 0.5F);
      int valueForIntFloat19 = ForeShinUtils.getValueForIntFloat(15124390, 0.5F);

      for (int bonecaAmbalabuTotal2 = -1; bonecaAmbalabuTotal2 <= 1; bonecaAmbalabuTotal2 += 2) {
         float bonecaAmbalabuValue20 = bonecaAmbalabuTotal2 * 1.45F;
         float bonecaAmbalabu3 = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue20, 31.35F);
         float amountForFloat = getAmount(bonecaAmbalabu, 31.35F);
         bonecaAmbalabu.getClass022(bonecaAmbalabu3, amountForFloat, 10.5F, 9.0F, ForeShinUtils.getValueForIntFloat(7228214, 0.45F));
         bonecaAmbalabu.getClass022(bonecaAmbalabu3, amountForFloat, 7.4F, 6.2F, ForeShinUtils.getValueForIntFloat(3810840, 0.65F));
         float bonecaAmbalabu4 = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuTotal2 * 0.79F, 31.300001F);
         float classValue = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuTotal2 * 2.13F, 31.27F);
         bonecaAmbalabu.getClass022(bonecaAmbalabu4, amountForFloat + 0.5F, 1.8F, 1.5F, ForeShinUtils.getValueForInt(15327958));
         bonecaAmbalabu.getClass022(classValue, amountForFloat + 0.7F, 1.8F, 1.4F, ForeShinUtils.getValueForInt(15327958));
         handle(bonecaAmbalabu, bonecaAmbalabuTotal2 * 0.8F, 30.630001F, bonecaAmbalabuTotal2 * 1.45F, 30.050001F, bonecaAmbalabuTotal2 * 2.25F, 30.800001F, 1.6F, valueForIntFloat);
         handle(bonecaAmbalabu, bonecaAmbalabuTotal2 * 0.8F, 30.470001F, bonecaAmbalabuTotal2 * 1.45F, 29.89F, bonecaAmbalabuTotal2 * 2.25F, 30.65F, 1.1F, valueForIntFloat19);
         handle(bonecaAmbalabu, bonecaAmbalabuTotal2 * 0.95F, 30.1F, bonecaAmbalabuTotal2 * 1.55F, 29.45F, bonecaAmbalabuTotal2 * 2.35F, 30.25F, 1.2F, valueForIntFloat18);

         for (int bonecaAmbalabuCounter = 1; bonecaAmbalabuCounter <= 2; bonecaAmbalabuCounter++) {
            float bonecaAmbalabuValue22 = 0.35F * bonecaAmbalabuCounter;
            handle(
               bonecaAmbalabu,
               bonecaAmbalabuTotal2 * (0.85F + 0.1F * bonecaAmbalabuCounter),
               30.630001F - bonecaAmbalabuValue22,
               bonecaAmbalabuTotal2 * 1.5F,
               30.050001F - bonecaAmbalabuValue22,
               bonecaAmbalabuTotal2 * (2.3F + 0.1F * bonecaAmbalabuCounter),
               30.800001F - bonecaAmbalabuValue22,
               1.1F,
               bonecaAmbalabuCounter == 1 ? valueForIntFloat : valueForIntFloat18
            );
         }

         for (int bonecaAmbalabuCounter2 = 0; bonecaAmbalabuCounter2 < 3; bonecaAmbalabuCounter2++) {
            float bonecaAmbalabuValue23 = 31.6F - bonecaAmbalabuCounter2 * 0.38F;
            handle(bonecaAmbalabu, bonecaAmbalabuTotal2 * 2.2F, bonecaAmbalabuValue23, bonecaAmbalabuTotal2 * 2.55F, bonecaAmbalabuValue23 + 0.05F * (bonecaAmbalabuCounter2 - 1), bonecaAmbalabuTotal2 * 3.0F, bonecaAmbalabuValue23 + 0.3F * (bonecaAmbalabuCounter2 - 1), 1.2F, valueForIntFloat18);
         }

         handle(bonecaAmbalabu, bonecaAmbalabuTotal2 * 1.0F, 30.15F, bonecaAmbalabuTotal2 * 2.3F, 29.4F, bonecaAmbalabuTotal2 * 2.6F, 27.8F, 1.7F, valueForIntFloat);
         handle(bonecaAmbalabu, bonecaAmbalabuTotal2 * 1.15F, 30.05F, bonecaAmbalabuTotal2 * 2.45F, 29.25F, bonecaAmbalabuTotal2 * 2.75F, 27.8F, 1.1F, valueForIntFloat19);
         bonecaAmbalabu.getClass022(getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuTotal2 * 2.45F, 29.0F), getAmount(bonecaAmbalabu, 29.0F), 9.0F, 7.0F, ForeShinUtils.getValueForIntFloat(12156770, 0.08F));
      }

      handle(bonecaAmbalabu, -0.5F, 31.95F, 0.0F, 32.05F, 0.5F, 31.95F, 1.1F, valueForIntFloat18);
      handle(bonecaAmbalabu, -2.5F, 33.95F, 0.0F, 34.3F, 2.5F, 33.95F, 1.4F, valueForIntFloat);
      handle(bonecaAmbalabu, -2.3F, 34.05F, 0.0F, 34.4F, 2.3F, 34.05F, 0.9F, valueForIntFloat19);
      handle(bonecaAmbalabu, -1.9F, 34.45F, 0.0F, 34.7F, 1.9F, 34.45F, 1.1F, valueForIntFloat18);
      bonecaAmbalabu.getClass022(getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 30.2F), getAmount(bonecaAmbalabu, 30.2F), 9.0F, 14.0F, ForeShinUtils.getValueForIntFloat(7228214, 0.35F));
      bonecaAmbalabu.getClass022(getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 28.6F), getAmount(bonecaAmbalabu, 28.6F), 10.0F, 6.0F, ForeShinUtils.getValueForIntFloat(7228214, 0.3F));

      for (int bonecaAmbalabuCounter3 = 0; bonecaAmbalabuCounter3 < 90; bonecaAmbalabuCounter3++) {
         double angle2 = (Math.PI * 2) * (bonecaAmbalabuCounter3 + getAmount(bonecaAmbalabuCounter3, 61)) / 90.0;
         float sin = (float)Math.sin(angle2);
         if (!(sin < -0.25F)) {
            float cos = (float)Math.cos(angle2);
            float bonecaAmbalabuDivisor = 1.16F + 0.05F * getAmount(bonecaAmbalabuCounter3, 66);
            float bonecaAmbalabuFactor = 0.9F - 0.07F * getAmount(bonecaAmbalabuCounter3, 62);
            float bonecaAmbalabuValue24 = 3.55F * bonecaAmbalabuDivisor * Math.signum(cos) * (float)Math.pow(Math.abs(cos), 0.8);
            float brrForeLimit = 29.9F + 4.6F * bonecaAmbalabuDivisor * Math.signum(sin) * (float)Math.pow(Math.abs(sin), 0.85);
            float bonecaAmbalabuValue26 = bonecaAmbalabuValue24 / bonecaAmbalabuDivisor * bonecaAmbalabuFactor;
            float bonecaAmbalabuValue27 = 29.9F + (brrForeLimit - 29.9F) / bonecaAmbalabuDivisor * bonecaAmbalabuFactor - 0.3F;
            float bonecaAmbalabu6 = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue24, brrForeLimit);
            float bonecaAmbalabuValue28 = getAmount(bonecaAmbalabu, brrForeLimit);
            float bonecaAmbalabu7 = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue26, bonecaAmbalabuValue27);
            float bonecaAmbalabuValue29 = getAmount(bonecaAmbalabu, bonecaAmbalabuValue27);
            int bonecaAmbalabuValue30 = bonecaAmbalabuTable[(int)(getAmount(bonecaAmbalabuCounter3, 64) * bonecaAmbalabuTable.length)];
            float bonecaAmbalabuValue31 = 2.6F + 2.2F * getAmount(bonecaAmbalabuCounter3, 65);
            bonecaAmbalabu.getClassForIntFloatArray(
               ForeShinUtils.getValue(ForeShinUtils.getValueForInt(bonecaAmbalabuValue30), 0.72F + 0.26F * getAmount(bonecaAmbalabuCounter3, 67)),
               bonecaAmbalabu6 - bonecaAmbalabuValue31 * 0.5F,
               bonecaAmbalabuValue28,
               bonecaAmbalabu6 + bonecaAmbalabuValue31 * 0.5F,
               bonecaAmbalabuValue28,
               bonecaAmbalabu7,
               bonecaAmbalabuValue29
            );
         }
      }
   }

   private static float getAmount5(float brrForeValue) {
      float brrForeFactor = (Math.abs(brrForeValue) - 1.45F) / 1.6F;
      float brrForeFactor2 = brrForeValue / 0.5F;
      return 32.75F - 0.25F * brrForeFactor * brrForeFactor - 0.2F * (float)Math.exp(-brrForeFactor2 * brrForeFactor2);
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float brrForeValue, float armTrunkValue, float brrForeValue3, float degrees, float brrForeValue5, float brrForeValue6, float brrForeValue7, float brrForeValue8, ShinBrr shinBrr) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(brrForeValue, armTrunkValue, brrForeValue3).getLatheLoft4(degrees).getLatheLoftLatheLoftAmountLoft(brrForeValue5).getLatheLoft(brrForeValue6);
      latheLoft.getLatheLoft(-brrForeValue8 * 0.5F, -brrForeValue7, -0.03F, brrForeValue8 * 0.5F, 0.0F, 0.03F, shinBrr, shinBrr, null, null, null, null);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu15(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13936776));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.25F, bonecaAmbalabu.ambalabuBonecaValue * 0.16F, bonecaAmbalabu.bonecaValue * 0.18F, ForeShinUtils.getValueForInt(12884608));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.255F, bonecaAmbalabu.ambalabuBonecaValue * 0.14F, bonecaAmbalabu.bonecaValue * 0.16F, ForeShinUtils.getValueForInt(14927538));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.47F, bonecaAmbalabu.bonecaValue * 0.21F, bonecaAmbalabu.ambalabuBonecaValue * 0.04F, bonecaAmbalabu.bonecaValue * 0.05F, ForeShinUtils.getValueForIntFloat(16777215, 0.3F));
      bonecaAmbalabu.getClassForFloatInt(
         bonecaAmbalabu.ambalabuBonecaValue * 0.38F,
         bonecaAmbalabu.bonecaValue * 0.38F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * 0.42F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.62F,
         bonecaAmbalabu.bonecaValue * 0.38F,
         1.0F,
         ForeShinUtils.getValueForIntFloat(11041888, 0.4F)
      );
      bonecaAmbalabu.getClass02(
         bonecaAmbalabu.ambalabuBonecaValue * 0.42F, bonecaAmbalabu.bonecaValue * 0.09F, bonecaAmbalabu.ambalabuBonecaValue * 0.58F, bonecaAmbalabu.bonecaValue * 0.09F, 1.0F, ForeShinUtils.getValueForIntFloat(11041888, 0.5F)
      );
   }

   private static float[] getAmounts(int brrForeFactor, int bonecaValue, float brrForeDivisor, int brrForeValue, int[] brrForeValues) {
      int maxRound = Math.max(1, Math.round(brrForeFactor / brrForeDivisor));
      int brrForeLimit = (int)Math.ceil(bonecaValue / brrForeDivisor) + 3;
      float brrForeDivisor2 = (float)brrForeFactor / maxRound;
      int brrForeSize = maxRound * brrForeLimit;
      float[] floats = new float[brrForeSize];
      float[] floats2 = new float[brrForeSize];
      float[] floats3 = new float[brrForeSize];
      int[] ints = new int[brrForeSize];

      for (int i = 0; i < brrForeLimit; i++) {
         for (int j = 0; j < maxRound; j++) {
            int brrForeValue2 = j * 7919 + (i - 1) * 104729 + brrForeValue * 31337;
            int index = i * maxRound + j;
            floats[index] = 0.15F + 0.7F * getAmount(brrForeValue2, 1);
            floats2[index] = 0.15F + 0.7F * getAmount(brrForeValue2, 2);
            floats3[index] = 1.0F / (0.72F + 0.42F * getAmount(brrForeValue2, 3));
            ints[index] = ForeShinUtils.getValueForInt(bonecaAmbalabuTable[Math.min(bonecaAmbalabuTable.length - 1, (int)(getAmount(brrForeValue2, 4) * bonecaAmbalabuTable.length))]);
         }
      }

      float[] floats4 = new float[brrForeFactor * bonecaValue];

      for (int k = 0; k < bonecaValue; k++) {
         float hashRegistryValue = (k + 0.5F) / brrForeDivisor;
         int floor4 = (int)Math.floor(hashRegistryValue);

         for (int m = 0; m < brrForeFactor; m++) {
            float brrForeValue5 = (m + 0.5F) / brrForeDivisor2;
            int floor6 = (int)Math.floor(brrForeValue5);
            float brrForeLimit2 = 99.0F;
            float brrForeFactor3 = 0.0F;
            float brrForeValue7 = 0.0F;
            int index2 = 0;

            for (int p = -1; p <= 1; p++) {
               int brrForeLimit3 = floor4 + p + 1;
               if (brrForeLimit3 >= 0 && brrForeLimit3 < brrForeLimit) {
                  for (int q = -1; q <= 1; q++) {
                     int brrForeValue8 = floor6 + q;
                     int index3 = brrForeLimit3 * maxRound + Math.floorMod(brrForeValue8, maxRound);
                     float brrForeFactor4 = (brrForeValue5 - brrForeValue8 - floats[index3]) * floats3[index3];
                     float brrForeFactor5 = (hashRegistryValue - (floor4 + p) - floats2[index3]) * floats3[index3];
                     float brrForeValue9 = brrForeFactor4 * brrForeFactor4 + brrForeFactor5 * brrForeFactor5;
                     if (brrForeValue9 < brrForeLimit2) {
                        brrForeLimit2 = brrForeValue9;
                        brrForeFactor3 = brrForeFactor4;
                        brrForeValue7 = brrForeFactor5;
                        index2 = index3;
                     }
                  }
               }
            }

            float brrForeValue10;
            if (brrForeLimit2 < 1.0F) {
               float sqrt = (float)Math.sqrt(1.0F - brrForeLimit2);
               brrForeValue10 = (0.58F + 0.42F * Math.max(0.0F, -0.45F * brrForeFactor3 - 0.55F * brrForeValue7 + 0.7F * sqrt)) * (0.8F + 0.2F * sqrt);
            } else {
               brrForeValue10 = 0.4F;
            }

            floats4[k * brrForeFactor + m] = brrForeValue10;
            if (brrForeValues != null) {
               brrForeValues[k * brrForeFactor + m] = ints[index2];
            }
         }
      }

      return floats4;
   }

   private void handleCrownBody(LatheLoft latheLoft, float crownBodyLimit, ForeEar foreEar, ForeEar foreEar2, ForeEar foreEar3, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      latheLoft.getLatheLoftForForeEar(foreEar);
      float[] pointsSpline = LatheLoft.getPointsSpline(this.bodyCrownFlag ? 1 : 2, crownBodyLimit * 2.5F, 24.4F, 0.5F, crownBodyLimit * 2.75F, 18.4F, 0.2F, crownBodyLimit * 1.7F, 12.6F, 0.25F);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 1.0F, 0.86F, 0.84F), 8, shinBrr, false);
      latheLoft.getLatheLoft(crownBodyLimit * 1.8800001F, 12.700001F, 0.15F, 0.95F, 1.05F, 0.95F, 6, shinBrr);
      if (!this.bodyCrownFlag) {
         latheLoft.getLatheLoft(crownBodyLimit * 3.05F, 17.5F, 0.0F, 0.7F, 0.8F, 0.7F, 6, shinBrr);
      }

      latheLoft.getLoftPath(
         new float[]{crownBodyLimit * 2.6F, 22.6F, 0.4F, crownBodyLimit * 2.65F, 20.0F, 0.45F, crownBodyLimit * 2.5F, 17.6F, 0.5F},
         new float[]{1.3F, 1.0F, 0.55F},
         new float[]{1.3F, 1.0F, 0.55F},
         6,
         shinBrr2,
         null,
         null,
         0.0F,
         0.0F,
         -1.0F
      );
      handle(latheLoft, pointsSpline, 0.12F, 0.88F, 4, 0.98F, -0.9F, crownBodyLimit > 0.0F ? 0.6F : 2.5F, 0.18F, shinBrr);
      latheLoft.getLatheLoftForForeEar(foreEar2);
      float[] latheLoftValues = LatheLoft.getPointsSpline(this.bodyCrownFlag ? 1 : 2, crownBodyLimit * 1.7F, 12.6F, 0.25F, crownBodyLimit * 2.6F, 7.6F, 0.95F, crownBodyLimit * 2.6F, 3.0F, 1.1F);
      latheLoft.getLatheLoft(latheLoftValues, LatheLoft.getAmounts(LatheLoft.getValue(latheLoftValues), 0.84F, 0.76F, 1.0F), 8, shinBrr, false);
      if (!this.bodyCrownFlag) {
         latheLoft.getLatheLoft(crownBodyLimit * 2.95F, 7.0F, 0.6F, 0.7F, 0.8F, 0.7F, 6, shinBrr);
      }

      handle(latheLoft, latheLoftValues, 0.12F, 0.86F, 5, 0.86F, 1.3F, crownBodyLimit > 0.0F ? 1.4F : 4.0F, 0.22F, shinBrr);
      latheLoft.getLatheLoftForForeEar(foreEar3);
      float crownBodyValue = crownBodyLimit * 2.75F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(crownBodyLimit * 2.6F, 0.0F, 1.1F).getLatheLoft4(-crownBodyLimit * 7.0F).getLatheLoft(-crownBodyLimit * 2.6F, 0.0F, -1.1F);
      float[] floats = new float[]{4.28F, 4.05F, 3.6F, 2.55F, 1.0F, -0.8F, -2.6F, -4.0F, -4.85F};
      float[] floats2 = new float[]{0.3F, 0.6F, 1.15F, 1.82F, 1.92F, 1.62F, 1.28F, 0.98F, 0.76F};
      float[] crownForeValues = new float[]{0.42F, 0.95F, 1.57F, 1.89F, 2.0F, 2.14F, 2.38F, 2.51F, 2.4F};
      float[] loftPathValues = new float[3 * floats.length];

      for (int i = 0; i < floats.length; i++) {
         float crownBodyFactor = floats[i] - 1.1F;
         loftPathValues[3 * i] = crownBodyValue;
         loftPathValues[3 * i + 1] = Math.max(floats2[i] * 0.97F, 0.42F);
         loftPathValues[3 * i + 2] = 1.1F + crownBodyFactor * (crownBodyFactor < 0.0F ? 1.18F : 1.12F);
      }

      latheLoft.getLoftPath(loftPathValues, crownForeValues, floats2, 10, shinBrr3, shinBrr3, shinBrr3, 0.0F, 1.0F, 0.0F);
      float[] floats5 = new float[]{-1.53F, -0.35F, 0.65F, 1.51F, 2.22F};
      float[] floats6 = new float[]{0.82F, 0.6F, 0.55F, 0.49F, 0.42F};
      float[] floats7 = new float[]{2.93F, 2.36F, 2.13F, 1.84F, 1.5F};
      float[] floats8 = new float[]{-5.25F, -5.45F, -5.35F, -5.12F, -4.8F};

      for (int j = 0; j < 5; j++) {
         float loftLatheFactor = floats6[j];
         float crownBodyValue3 = floats7[j] + 0.3F;
         latheLoft.getLatheLoft(
            crownBodyValue + crownBodyLimit * floats5[j], loftLatheFactor * 0.95F, floats8[j] - crownBodyValue3 * 0.5F, loftLatheFactor, loftLatheFactor * 0.9F, crownBodyValue3 * 0.5F + 0.15F, j == 0 ? 8 : 6, shinBrr4
         );
      }

      float crownBodyValue4 = crownBodyLimit * 2.6F;
      latheLoft.getLatheLoft(new float[]{crownBodyValue4, 4.0F, 1.25F, crownBodyValue4, 3.0F, 2.3F, crownBodyValue4, 1.8F, 3.5F}, new float[]{0.34F, 0.45F, 0.52F}, 6, shinBrr3, false);
      if (!this.bodyCrownFlag) {
         handle(latheLoft, new float[]{crownBodyValue4, 5.4F, 1.05F, crownBodyValue4, 2.5F, 1.9F}, 0.0F, 1.0F, 5, 1.08F, 1.2F, crownBodyLimit > 0.0F ? 0.4F : 2.6F, 0.17F, shinBrr);
      }

      latheLoft.getDequeArray();
   }

   private static void handleForBonecaAmbalabuFloat(BonecaAmbalabu bonecaAmbalabu, float brrForeFactor, float brrForeFactor2, float brrForeFactor3, float brrForeFactor4, float brrForeFactor5, float brrForeFactor6, float brrForeValue, int bonecaAmbalabuValue) {
      float[] floats = new float[7];
      float[] floats2 = new float[7];

      for (int i = 0; i < 7; i++) {
         float brrForeFactor7 = i / 6.0F;
         float brrForeFactor8 = 1.0F - brrForeFactor7;
         floats[i] = brrForeFactor8 * brrForeFactor8 * brrForeFactor + 2.0F * brrForeFactor8 * brrForeFactor7 * brrForeFactor3 + brrForeFactor7 * brrForeFactor7 * brrForeFactor5;
         floats2[i] = brrForeFactor8 * brrForeFactor8 * brrForeFactor2 + 2.0F * brrForeFactor8 * brrForeFactor7 * brrForeFactor4 + brrForeFactor7 * brrForeFactor7 * brrForeFactor6;
      }

      handle(bonecaAmbalabu, floats, floats2, brrForeValue, bonecaAmbalabuValue);
   }

   private void handleArm(LatheLoft latheLoft, ForeEar foreEar, float armValue) {
      ShinBrr shinBrr = latheLoft.getMax(1118481);
      ShinBrr max2 = latheLoft.getMax(16777215);
      float latheLoftValue = armValue * 1.45F;
      latheLoft.getLatheLoftForForeEar(foreEar);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, 31.35F, getAmountForFloat(armValue)).getLatheLoft4(LatheLoft.getAmountForFloat(latheLoftValue, getAmounts(31.35F)));
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.68F, 0.64F, 0.46F, 8, shinBrr);
      latheLoft.getLoftLathe(0.22F, 0.14F, -0.45F, 0.17F, 0.17F, 8, max2);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.armL);
   }

   private static float[] getAmounts() {
      int brrForeSize = shinFootTable.length;
      float brrForeFactor = 0.94F;
      float[] floats = new float[brrForeSize];

      for (int i = 1; i < brrForeSize; i++) {
         float brrForeFactor2 = (brrForeTable[i] - brrForeTable[i - 1]) * brrForeFactor;
         float brrForeFactor3 = shinFootTable[i] - shinFootTable[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(brrForeFactor2 * brrForeFactor2 + brrForeFactor3 * brrForeFactor3);
      }

      return floats;
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      latheLoft.getLatheLoft(-2.1F, 37.5F, 0.5F, 1.9F, 1.5F, 1.8F, 8, shinBrr2);
      latheLoft.getLatheLoft(2.0F, 37.65F, 0.1F, 1.85F, 1.45F, 1.75F, 8, shinBrr2);
      latheLoft.getLatheLoft(0.0F, 38.45F, -0.4F, 1.95F, 1.35F, 1.9F, 8, shinBrr2);
      latheLoft.getLatheLoft(3.2F, 37.0F, 1.2F, 1.6F, 1.2F, 1.5F, 6, shinBrr2);
      latheLoft.getLatheLoft(-3.2F, 37.0F, 1.2F, 1.6F, 1.2F, 1.5F, 6, shinBrr2);

      for (int i = 0; i < 7; i++) {
         float degrees = 24.0F + i * 51.42857F + 16.0F * (getAmount(i, 81) - 0.5F);
         float angle = (float)Math.toRadians(degrees);
         float brrForeFactor = 2.2F + 1.2F * getAmount(i, 84);
         float brrForeValue = getAmounts4(brrForeFactor) - 0.35F;
         float degrees2 = (i % 3 == 1 ? 38.0F : 20.0F) + 10.0F * getAmount(i, 83);
         float planValuesItem = 30.0F * Math.signum((float)Math.sin(2.0 * angle));
         float brrForeValue3 = i % 2 == 0 ? 12.0F : -12.0F;
         float brrForeValue4 = 2.4F + 0.4F * getAmount(i, 86);
         float cos = (float)Math.cos(Math.toRadians(degrees2));
         float brrForeValue5 = 0.5F * brrForeValue4 * (float)Math.sin(Math.toRadians(Math.abs(brrForeValue3) + Math.abs(planValuesItem) * 0.5F));
         float hypot = Math.min(2.6F + 0.6F * getAmount(i, 82), (40.0F - brrForeValue - brrForeValue5) / cos);
         handle(latheLoft, brrForeFactor * (float)Math.sin(angle), brrForeValue, -brrForeFactor * 0.88F * (float)Math.cos(angle), -degrees, degrees2, brrForeValue3, planValuesItem, hypot, brrForeValue4, shinBrr);
      }

      for (int j = -1; j <= 1; j += 2) {
         float brrForeFactor2 = j > 0 ? 1.0F : 0.72F;
         float brrForeValue7 = j * (j > 0 ? 0.9F : 0.6F);
         float brrForeValue8 = 38.7F;
         float brrForeValue9 = j > 0 ? 0.8F : 1.3F;
         latheLoft.getLatheLoft(
            new float[]{
               brrForeValue7,
               brrForeValue8,
               brrForeValue9,
               brrForeValue7 + j * 0.35F * brrForeFactor2,
               brrForeValue8 + 0.8F * brrForeFactor2,
               brrForeValue9 + 0.15F,
               brrForeValue7 + j * (j > 0 ? 0.8F : 0.35F) * brrForeFactor2,
               brrForeValue8 + 1.55F * brrForeFactor2,
               brrForeValue9 + 0.55F - 0.2F * brrForeFactor2
            },
            new float[]{0.2F, 0.13F, 0.06F},
            4,
            shinBrr3,
            false
         );
      }
   }

   @Override
   public String formatTungDinString() {
      return "Brr Brr Patapim";
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue, int brrForeValue, boolean bonecaAmbalabuFlag) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue3 = (j + 0.5F) / ambalabuBonecaValue;
            float bonecaAmbalabuValue4 = (i + 0.5F) / bonecaValue;
            float bonecaAmbalabuValue5 = 0.6F * (float)Math.sin(bonecaAmbalabuValue4 * 9.0F + brrForeValue) + 0.3F * (float)Math.sin(bonecaAmbalabuValue4 * 23.0F + brrForeValue * 2);
            float sin = (float)Math.sin((bonecaAmbalabuValue3 * 7.0F + bonecaAmbalabuValue5 * 0.35F) * (float) (Math.PI * 2));
            float bonecaAmbalabuFactor = Math.max(0.0F, sin) * Math.max(0.0F, sin);
            float bonecaAmbalabuFactor2 = bonecaAmbalabuFactor * bonecaAmbalabuFactor;
            float foreShinFactor = 0.92F + 0.1F * sin - 0.22F * bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2 + 0.06F * getAmount(j / 2 * 29 + i * 7, brrForeValue);
            int foreShinValue = ForeShinUtils.getValue(ForeShinUtils.getValueForInt(bonecaAmbalabuValue), foreShinFactor);
            if (bonecaAmbalabuFlag) {
               float bonecaAmbalabuValue8 = (bonecaAmbalabuValue3 + bonecaAmbalabuValue4 * 1.6F) % 0.5F;
               if (bonecaAmbalabuValue8 < 0.07F) {
                  foreShinValue = ForeShinUtils.getValue(ForeShinUtils.getValueForInt(8019518), 0.85F + 2.2F * bonecaAmbalabuValue8);
               } else if (bonecaAmbalabuValue8 < 0.09F) {
                  foreShinValue = ForeShinUtils.getValue(foreShinValue, 0.6F);
               }
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, foreShinValue);
         }
      }

      for (int k = 0; k < 4; k++) {
         float classValue = getAmount(k + brrForeValue * 10, 141) * ambalabuBonecaValue;
         float amountForFloat = getAmount(k + brrForeValue * 10, 142) * bonecaValue;
         bonecaAmbalabu.getClass022(classValue, amountForFloat, 2.2F, 3.4F, ForeShinUtils.getValueForIntFloat(3022868, 0.7F));
         bonecaAmbalabu.getClass022(classValue, amountForFloat, 3.6F, 5.2F, 1.1F, ForeShinUtils.getValueForIntFloat(9334348, 0.5F));
      }
   }

   private static float getAmount4(float angle, float brrForeValue) {
      float brrForeFactor = -9.799999F;
      float brrForeFactor2 = -0.25F;
      float brrForeFactor3 = -9.6F;
      float brrForeFactor4 = 0.85F;
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float cos2 = (float)Math.cos(angle + brrForeValue);
      float sin2 = (float)Math.sin(angle + brrForeValue);
      return brrForeFactor2 + brrForeFactor4 - (brrForeFactor * sin + brrForeFactor2 * cos + brrForeFactor3 * sin2 + brrForeFactor4 * cos2);
   }
}
