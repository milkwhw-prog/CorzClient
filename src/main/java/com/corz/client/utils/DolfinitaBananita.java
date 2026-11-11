package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class DolfinitaBananita extends WarmBrainrotBase {
   private final ForeEar[] shinFootForeEars = new ForeEar[4];
   private ForeEar classShinFoot;
   private static final float[] dolfinitaBananitaTable = new float[]{
      -1.6F, -47.0F, 0.0F, -46.0F, 5.0F, -23.0F, 10.0F, -2.0F, 15.0F, 19.0F, 19.0F, 37.0F, 22.5F, 58.0F, 25.5F, 74.0F, 28.0F, 86.0F, 32.2F, 92.0F
   };
   private ForeEar classShinProgress;
   private ForeEar body;
   private static final float[][] dolfinitaBananitaFloats;
   private static final float[] bonecaTable = new float[]{
      14.85F,
      2.7F,
      2.7F,
      2.7F,
      15.8F,
      3.1F,
      3.15F,
      3.1F,
      17.6F,
      3.45F,
      3.55F,
      3.35F,
      19.6F,
      3.55F,
      3.75F,
      3.3F,
      21.4F,
      3.25F,
      3.5F,
      3.0F,
      23.0F,
      3.0F,
      3.45F,
      2.75F,
      24.6F,
      2.8F,
      3.5F,
      2.6F,
      25.9F,
      2.55F,
      3.35F,
      2.4F,
      26.9F,
      1.85F,
      2.0F,
      2.05F,
      27.7F,
      1.55F,
      1.55F,
      1.8F,
      28.9F,
      1.32F,
      1.18F,
      1.55F,
      30.1F,
      1.1F,
      0.95F,
      1.3F,
      30.9F,
      0.85F,
      0.72F,
      1.02F,
      31.3F,
      0.5F,
      0.42F,
      0.6F,
      31.5F,
      0.08F,
      0.06F,
      0.1F
   };
   private static final float[] table = new float[]{
      0.2F, 0.9F, 0.9F, 1.6F, 2.0F, 2.3F, 3.6F, 2.85F, 6.0F, 3.15F, 9.0F, 3.4F, 12.0F, 3.5F, 14.5F, 3.55F, 15.8F, 3.6F
   };
   private static final float[] TAIL_ANGLE_KEYFRAMES = new float[]{
      26.65F, 92.0F, 27.0F, 94.5F, 27.4F, 96.5F, 27.9F, 98.5F, 28.8F, 100.0F, 30.2F, 99.5F, 31.2F, 97.0F, 31.5F, 97.0F
   };
   private static final float[] BODY_ANGLE_KEYFRAMES = new float[]{22.5F, 112.0F, 24.5F, 106.0F, 26.2F, 100.0F, 27.6F, 99.0F};
   private static final float[] PROFILE_KEYFRAMES = new float[]{
      14.85F,
      15.75F,
      16.75F,
      17.9F,
      19.2F,
      20.4F,
      21.5F,
      22.5F,
      23.4F,
      24.3F,
      25.1F,
      25.8F,
      26.4F,
      26.95F,
      27.45F,
      27.95F,
      28.6F,
      29.3F,
      30.0F,
      30.6F,
      31.05F,
      31.32F,
      31.5F
   };
   private static final float[] RING_DISTANCES = new float[]{0.2F, 0.8F, 1.8F, 3.3F, 5.4F, 8.0F, 10.8F, 13.5F, 15.8F};
   private ForeEar shinFootForeEar;
   private static final float[] DORSAL_SAG_OFFSETS = new float[]{-0.2F, 0.25F, -0.5F, -1.8F, -3.3F, -4.5F, -5.1F};
   private static final float[] DORSAL_LIFT_OFFSETS = new float[]{-0.3F, 0.35F, 1.2F, 1.8F, 2.2F, 2.6F, 3.1F};
   private ForeEar shinProgressForeEar;
   private static final float[] CORNER_ANGLES_DEGREES = new float[]{45.0F, 135.0F, 225.0F, 315.0F};
   private ForeEar dorsal;
   private static final float[] RING_HEIGHTS = new float[]{26.5F, 26.95F, 27.45F, 28.1F, 28.9F, 29.7F, 30.4F, 31.0F, 31.45F};
   private static final float[] PATH_X = new float[671];
   private static final float[] PATH_Y = new float[671];
   private static final float[] PATH_ANGLES = new float[671];
   private static final int PROFILE_KEYFRAME_COUNT = DolfinitaBananita.PROFILE_KEYFRAMES.length;
   private ForeEar dolfinitaBananitaForeEar;
   private static final float[] shinFootTable = new float[3 * DolfinitaBananita.PROFILE_KEYFRAME_COUNT];
   private static final float[] RING_SEMI_AXES_A = new float[DolfinitaBananita.PROFILE_KEYFRAME_COUNT];
   private static final float[] RING_SEMI_AXES_B = new float[PROFILE_KEYFRAME_COUNT];
   private ForeEar foreEar;
   private static final float[] NORMALIZED_ARC_LENGTHS = new float[PROFILE_KEYFRAME_COUNT];
   private static final float[] shinProgressTable = new float[4];
   private static final float[] FIN_DIRECTION_Z = new float[4];

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr) {
      float[] dolfinitaBananitaValues = getAmounts(18.4F, 0.0F);
      float[] amountsPATHX = getAmountsPATH_X(18.4F);
      float cos = (float)Math.cos(amountsPATHX[2]);
      float sin = -((float)Math.sin(amountsPATHX[2]));
      float[] floats = new float[]{-0.45F, 0.6F, 1.7F, 2.7F, 3.5F, 3.95F};
      float[] floats2 = new float[]{0.0F, 0.2F, 0.6F, 1.25F, 2.05F, 2.75F};
      float[] pointsSplineValues = new float[18];

      for (int i = 0; i < 6; i++) {
         pointsSplineValues[3 * i] = dolfinitaBananitaValues[0] + dolfinitaBananitaValues[3] * floats[i];
         pointsSplineValues[3 * i + 1] = dolfinitaBananitaValues[1] + dolfinitaBananitaValues[4] * floats[i] - cos * floats2[i];
         pointsSplineValues[3 * i + 2] = dolfinitaBananitaValues[2] + dolfinitaBananitaValues[5] * floats[i] - sin * floats2[i];
      }

      float[] pointsSpline = LatheLoft.getPointsSpline(2, pointsSplineValues);
      int dolfinitaBananitaValue = LatheLoft.getValue(pointsSpline);
      latheLoft.getLoftPath(
         pointsSpline,
         LatheLoft.getAmounts(dolfinitaBananitaValue, 0.9F, 0.7F, 0.5F, 0.32F, 0.16F, 0.05F),
         LatheLoft.getAmounts(dolfinitaBananitaValue, 1.8F, 1.6F, 1.25F, 0.85F, 0.45F, 0.1F),
         8,
         shinBrr,
         null,
         null,
         0.0F,
         cos,
         sin
      );
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      float[] shinFootValues = getAmountsPATH_X(-1.5F);
      float[] amountsPATHX = getAmountsPATH_X(15.8F);
      this.body = latheLoft.getClassForStringForeEar("base", latheLoft.getClassForeEarList(), 0.0F, 0.0F, shinFootValues[1] * 1.08F);
      this.dorsal = getClassForLatheLoftString(latheLoft, "body", this.body, 0.0F, amountsPATHX[0], amountsPATHX[1]);
      float[] amounts3 = getAmounts(18.4F, 0.0F);
      this.classShinFoot = getClassForLatheLoftString(latheLoft, "dorsal", this.dorsal, amounts3[0], amounts3[1], amounts3[2]);
      float[] amountsForFloat = getAmountsForFloat(1.0F);
      float[] amountsForFloat5 = getAmountsForFloat(-1.0F);
      this.classShinProgress = getClassForLatheLoftString(latheLoft, "eye_r", this.dorsal, amountsForFloat[0], amountsForFloat[1], amountsForFloat[2]);
      this.foreEar = getClassForLatheLoftString(latheLoft, "eye_l", this.dorsal, amountsForFloat5[0], amountsForFloat5[1], amountsForFloat5[2]);
      float[] amounts6 = getAmounts(22.0F, 124.0F);
      float[] amounts7 = getAmounts(22.0F, -124.0F);
      this.dolfinitaBananitaForeEar = getClassForLatheLoftString(latheLoft, "fin_r", this.dorsal, amounts6[0], amounts6[1], amounts6[2]);
      this.shinFootForeEar = getClassForLatheLoftString(latheLoft, "fin_l", this.dorsal, amounts7[0], amounts7[1], amounts7[2]);
      float[] amounts8 = getAmounts(26.55F, 91.0F);
      float[] amounts9 = getAmounts(26.55F, -91.0F);
      this.shinProgressForeEar = getClassForLatheLoftString(latheLoft, "jaw", this.dorsal, 0.0F, 0.5F * (amounts8[1] + amounts9[1]), 0.5F * (amounts8[2] + amounts9[2]));

      for (int i = 0; i < 4; i++) {
         float[] amountsForInt = getAmountsForInt(i);
         this.shinFootForeEars[i] = getClassForLatheLoftString(latheLoft, "peel_" + i, this.body, amountsForInt[0], amountsForInt[1], amountsForInt[2]);
      }

      latheLoft.getLatheLoft(this.dolfinitaBananitaForeEar);
      LoftLatheFlag.Region region = latheLoft.getMax(256, 288);
      handleAmountsBoneca(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region[] regions = new LoftLatheFlag.Region[5];

      for (int j = 0; j < 5; j++) {
         regions[j] = latheLoft.getMax(112, 128);
         handleBonecaAmbalabu(regions[j].getClassBonecaAmbalabu(), 31 + 17 * j);
      }

      LoftLatheFlag.Region max2 = latheLoft.getMax(64, 96);
      handleBonecaAmbalabuForBonecaAmbalabu(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(32, 48);
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu(), 7177627, false);
      LoftLatheFlag.Region max4 = latheLoft.getMax(32, 48);
      handleBonecaAmbalabu(max4.getClassBonecaAmbalabu(), ForeShinUtils.getValue2(8361901, 5137784, 0.55F), true);
      LoftLatheFlag.Region max5 = latheLoft.getMax(48, 48);
      handleBonecaAmbalabu(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(32, 32);
      max6.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4008724))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(2759692), 4.0F, 0.3F, 0.6F, 5)
         .getBonecaAmbalabu(0.08F, 6.0F, 9);
      LoftLatheFlag.Region max7 = latheLoft.getMax(128, 32);
      handleBonecaAmbalabu3(max7.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max6);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft3(1.08F);
      latheLoft.getLatheLoftForForeEar(this.body);

      for (int k = 0; k < 5; k++) {
         handleAmounts(latheLoft, k, latheLoft.getClassForRegion(regions[k]), classForRegion3);
      }

      float[] floats = new float[12];
      float[] crownForeValues = new float[]{0.45F, 0.55F, 0.62F, 0.66F};
      float[] floats3 = new float[]{-1.5F, -0.7F, 0.1F, 0.8F};

      for (int m = 0; m < 4; m++) {
         float[] amountsPathX = getAmountsPATH_X(floats3[m]);
         floats[3 * m + 1] = amountsPathX[0];
         floats[3 * m + 2] = amountsPathX[1];
      }

      latheLoft.getLoftPath(floats, crownForeValues, crownForeValues, 8, classForRegion7, latheLoft.getMax(5914398), null, 0.0F, 0.0F, -1.0F);
      float[] amountsPATH_X12 = getAmountsPATH_X(15.7F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, amountsPATH_X12[0], amountsPATH_X12[1]).getLatheLoftLatheLoftAmountLoft(-amountsPATH_X12[2] / (float) (Math.PI / 180.0));
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 3.45F, 0.32F, 20, 6, latheLoft.getMax(16182469));
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.dorsal);
      latheLoft.getLoftPath(shinFootTable, RING_SEMI_AXES_A, RING_SEMI_AXES_B, 20, classForRegion, null, latheLoft.getMax(5663101), 0.0F, 0.0F, 1.0F);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      handle(latheLoft, classForRegion6);
      handleForLatheLoftShinBrr(latheLoft, latheLoft.getMax(5137784));
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      handleAmounts(latheLoft, latheLoft.getClassForRegion(max7), latheLoft.getMax(16053488));
      ShinBrr shinBrr = latheLoft.getMax(1776411);
      ShinBrr shinBrr2 = latheLoft.getMax(16777215);
      handle(latheLoft, this.classShinProgress, 1.0F, shinBrr, shinBrr2);
      handle(latheLoft, this.foreEar, -1.0F, shinBrr, shinBrr2);
      latheLoft.getLatheLoftForForeEar(this.dolfinitaBananitaForeEar);
      handle(latheLoft, 1.0F, classForRegion5);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handle(latheLoft, -1.0F, classForRegion5);

      for (int p = 0; p < 4; p++) {
         latheLoft.getLatheLoftForForeEar(this.shinFootForeEars[p]);
         handleAmounts(latheLoft, p, classForRegion4);
      }

      latheLoft.getDequeArray();
   }

   @Override
   public float getAmountFloat() {
      return 1.3F;
   }

   private static float[] getAmounts(int index) {
      float angle = CORNER_ANGLES_DEGREES[index] * (float) (Math.PI / 180.0);
      float[] dolfinitaBananitaValues = getAmountsPATH_X(15.45F);
      return new float[]{(float)Math.sin(angle), (float)Math.sin(dolfinitaBananitaValues[2]) * (float)Math.cos(angle), (float)Math.cos(dolfinitaBananitaValues[2]) * (float)Math.cos(angle)};
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16182469));

      for (int i = 1; i <= 2; i++) {
         bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.5F, i * 8.0F, i * 8.0F, 0.8F, ForeShinUtils.getValueForIntFloat(14864792, 0.35F));
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.04F, 6.0F, 61);
   }

   private static float getAmounts(float dolfinitaBananitaLimit) {
      int index = 0;

      while (index < PROFILE_KEYFRAME_COUNT - 2 && NORMALIZED_ARC_LENGTHS[index + 1] < dolfinitaBananitaLimit) {
         index++;
      }

      float dolfinitaBananitaLimit2 = (dolfinitaBananitaLimit - NORMALIZED_ARC_LENGTHS[index]) / (NORMALIZED_ARC_LENGTHS[index + 1] - NORMALIZED_ARC_LENGTHS[index]);
      float dolfinitaBananitaFactor = dolfinitaBananitaLimit2 < 0.0F ? 0.0F : (dolfinitaBananitaLimit2 > 1.0F ? 1.0F : dolfinitaBananitaLimit2);
      return PROFILE_KEYFRAMES[index] + (PROFILE_KEYFRAMES[index + 1] - PROFILE_KEYFRAMES[index]) * dolfinitaBananitaFactor;
   }

   private static void handle(LatheLoft latheLoft, float[] dolfinitaBananitaValues, float[] dolfinitaBananitaValues2, float[] dolfinitaBananitaValues3, float[] dolfinitaBananitaValues4, ShinBrr shinBrr) {
      float dolfinitaBananitaFactor = dolfinitaBananitaValues2[0] - dolfinitaBananitaValues[0];
      float dolfinitaBananitaFactor2 = dolfinitaBananitaValues2[1] - dolfinitaBananitaValues[1];
      float dolfinitaBananitaFactor3 = dolfinitaBananitaValues2[2] - dolfinitaBananitaValues[2];
      float dolfinitaBananitaFactor4 = dolfinitaBananitaValues3[0] - dolfinitaBananitaValues[0];
      float dolfinitaBananitaFactor5 = dolfinitaBananitaValues3[1] - dolfinitaBananitaValues[1];
      float dolfinitaBananitaFactor6 = dolfinitaBananitaValues3[2] - dolfinitaBananitaValues[2];
      float dolfinitaBananitaFactor7 = dolfinitaBananitaFactor2 * dolfinitaBananitaFactor6 - dolfinitaBananitaFactor3 * dolfinitaBananitaFactor5;
      float dolfinitaBananitaFactor8 = dolfinitaBananitaFactor3 * dolfinitaBananitaFactor4 - dolfinitaBananitaFactor * dolfinitaBananitaFactor6;
      float dolfinitaBananitaFactor9 = dolfinitaBananitaFactor * dolfinitaBananitaFactor5 - dolfinitaBananitaFactor2 * dolfinitaBananitaFactor4;
      float dolfinitaBananitaLimit = dolfinitaBananitaFactor7 * (dolfinitaBananitaValues[0] + dolfinitaBananitaValues2[0] + dolfinitaBananitaValues3[0] - 3.0F * dolfinitaBananitaValues4[0]) + dolfinitaBananitaFactor8 * (dolfinitaBananitaValues[1] + dolfinitaBananitaValues2[1] + dolfinitaBananitaValues3[1] - 3.0F * dolfinitaBananitaValues4[1]) + dolfinitaBananitaFactor9 * (dolfinitaBananitaValues[2] + dolfinitaBananitaValues2[2] + dolfinitaBananitaValues3[2] - 3.0F * dolfinitaBananitaValues4[2]);
      if (dolfinitaBananitaLimit >= 0.0F) {
         latheLoft.getLatheLoft(dolfinitaBananitaValues[0], dolfinitaBananitaValues[1], dolfinitaBananitaValues[2], dolfinitaBananitaValues2[0], dolfinitaBananitaValues2[1], dolfinitaBananitaValues2[2], dolfinitaBananitaValues3[0], dolfinitaBananitaValues3[1], dolfinitaBananitaValues3[2], shinBrr);
      } else {
         latheLoft.getLatheLoft(dolfinitaBananitaValues2[0], dolfinitaBananitaValues2[1], dolfinitaBananitaValues2[2], dolfinitaBananitaValues[0], dolfinitaBananitaValues[1], dolfinitaBananitaValues[2], dolfinitaBananitaValues3[0], dolfinitaBananitaValues3[1], dolfinitaBananitaValues3[2], shinBrr);
      }
   }

   private static float getAmount(float[] dolfinitaBananitaValues, int dolfinitaBananitaFactor, int index, float dolfinitaBananitaLimit) {
      int dolfinitaBananitaValue = dolfinitaBananitaValues.length / dolfinitaBananitaFactor;
      if (dolfinitaBananitaLimit <= dolfinitaBananitaValues[0]) {
         return dolfinitaBananitaValues[index];
      } else if (dolfinitaBananitaLimit >= dolfinitaBananitaValues[(dolfinitaBananitaValue - 1) * dolfinitaBananitaFactor]) {
         return dolfinitaBananitaValues[(dolfinitaBananitaValue - 1) * dolfinitaBananitaFactor + index];
      } else {
         int dolfinitaBananitaCounter = 0;

         while (dolfinitaBananitaCounter < dolfinitaBananitaValue - 2 && dolfinitaBananitaLimit > dolfinitaBananitaValues[(dolfinitaBananitaCounter + 1) * dolfinitaBananitaFactor]) {
            dolfinitaBananitaCounter++;
         }

         float dolfinitaBananitaValue2 = dolfinitaBananitaValues[dolfinitaBananitaCounter * dolfinitaBananitaFactor];
         float dolfinitaBananitaValue3 = dolfinitaBananitaValues[(dolfinitaBananitaCounter + 1) * dolfinitaBananitaFactor];
         float dolfinitaBananitaFactor2 = dolfinitaBananitaValues[dolfinitaBananitaCounter * dolfinitaBananitaFactor + index];
         float dolfinitaBananitaFactor3 = dolfinitaBananitaValues[(dolfinitaBananitaCounter + 1) * dolfinitaBananitaFactor + index];
         float dolfinitaBananitaFactor4 = dolfinitaBananitaCounter > 0 ? (dolfinitaBananitaFactor3 - dolfinitaBananitaValues[(dolfinitaBananitaCounter - 1) * dolfinitaBananitaFactor + index]) / (dolfinitaBananitaValue3 - dolfinitaBananitaValues[(dolfinitaBananitaCounter - 1) * dolfinitaBananitaFactor]) * (dolfinitaBananitaValue3 - dolfinitaBananitaValue2) : dolfinitaBananitaFactor3 - dolfinitaBananitaFactor2;
         float dolfinitaBananitaFactor5 = dolfinitaBananitaCounter < dolfinitaBananitaValue - 2 ? (dolfinitaBananitaValues[(dolfinitaBananitaCounter + 2) * dolfinitaBananitaFactor + index] - dolfinitaBananitaFactor2) / (dolfinitaBananitaValues[(dolfinitaBananitaCounter + 2) * dolfinitaBananitaFactor] - dolfinitaBananitaValue2) * (dolfinitaBananitaValue3 - dolfinitaBananitaValue2) : dolfinitaBananitaFactor3 - dolfinitaBananitaFactor2;
         float dolfinitaBananitaFactor6 = (dolfinitaBananitaLimit - dolfinitaBananitaValue2) / (dolfinitaBananitaValue3 - dolfinitaBananitaValue2);
         float dolfinitaBananitaFactor7 = dolfinitaBananitaFactor6 * dolfinitaBananitaFactor6;
         float dolfinitaBananitaFactor8 = dolfinitaBananitaFactor7 * dolfinitaBananitaFactor6;
         return (2.0F * dolfinitaBananitaFactor8 - 3.0F * dolfinitaBananitaFactor7 + 1.0F) * dolfinitaBananitaFactor2
            + (dolfinitaBananitaFactor8 - 2.0F * dolfinitaBananitaFactor7 + dolfinitaBananitaFactor6) * dolfinitaBananitaFactor4
            + (-2.0F * dolfinitaBananitaFactor8 + 3.0F * dolfinitaBananitaFactor7) * dolfinitaBananitaFactor3
            + (dolfinitaBananitaFactor8 - dolfinitaBananitaFactor7) * dolfinitaBananitaFactor5;
      }
   }

   private static float getAmount(float dolfinitaBananitaValue, float dolfinitaBananitaValue2, float absSin) {
      float dolfinitaBananitaLimit = (absSin - dolfinitaBananitaValue) / (dolfinitaBananitaValue2 - dolfinitaBananitaValue);
      float dolfinitaBananitaFactor = dolfinitaBananitaLimit < 0.0F ? 0.0F : (dolfinitaBananitaLimit > 1.0F ? 1.0F : dolfinitaBananitaLimit);
      return dolfinitaBananitaFactor * dolfinitaBananitaFactor * (3.0F - 2.0F * dolfinitaBananitaFactor);
   }

   private static float[] getAmounts(float dolfinitaBananitaLimit, float dolfinitaBananitaValue) {
      int index = 0;

      while (index < PROFILE_KEYFRAME_COUNT - 2 && PROFILE_KEYFRAMES[index + 1] < dolfinitaBananitaLimit) {
         index++;
      }

      float dolfinitaBananitaLimit2 = (dolfinitaBananitaLimit - PROFILE_KEYFRAMES[index]) / (PROFILE_KEYFRAMES[index + 1] - PROFILE_KEYFRAMES[index]);
      float dolfinitaBananitaFactor = dolfinitaBananitaLimit2 < 0.0F ? 0.0F : (dolfinitaBananitaLimit2 > 1.0F ? 1.0F : dolfinitaBananitaLimit2);
      float[] dolfinitaBananitaValues = getAmounts(index, dolfinitaBananitaValue);
      float[] amounts2 = getAmounts(index + 1, dolfinitaBananitaValue);
      float[] floats = new float[6];

      for (int i = 0; i < 6; i++) {
         floats[i] = dolfinitaBananitaValues[i] + (amounts2[i] - dolfinitaBananitaValues[i]) * dolfinitaBananitaFactor;
      }

      float distance = (float)Math.sqrt(floats[3] * floats[3] + floats[4] * floats[4] + floats[5] * floats[5]);
      floats[3] /= distance;
      floats[4] /= distance;
      floats[5] /= distance;
      return floats;
   }

   private static void handle(LatheLoft latheLoft, float dolfinitaBananitaValue, ShinBrr shinBrr) {
      float[] dolfinitaBananitaValues = getAmounts(22.0F, dolfinitaBananitaValue * 124.0F);
      float[] amountsPATHX = getAmountsPATH_X(22.0F);
      float cos = (float)Math.cos(amountsPATHX[2]);
      float sin = -((float)Math.sin(amountsPATHX[2]));
      float sin2 = (float)Math.sin(amountsPATHX[2]);
      float cos2 = (float)Math.cos(amountsPATHX[2]);
      float[] floats = new float[]{-0.3F, 0.6F, 1.5F, 2.4F, 3.1F, 3.5F, 3.7F};
      float[] floats2 = new float[]{0.0F, 0.2F, 0.8F, 1.6F, 2.3F, 2.8F, 3.1F};
      float[] pointsSplineValues = new float[21];

      for (int i = 0; i < 7; i++) {
         pointsSplineValues[3 * i] = dolfinitaBananitaValues[0] + dolfinitaBananitaValues[3] * floats[i];
         pointsSplineValues[3 * i + 1] = dolfinitaBananitaValues[1] + dolfinitaBananitaValues[4] * floats[i] - cos * floats2[i];
         pointsSplineValues[3 * i + 2] = dolfinitaBananitaValues[2] + dolfinitaBananitaValues[5] * floats[i] - sin * floats2[i];
      }

      float[] pointsSpline = LatheLoft.getPointsSpline(1, pointsSplineValues);
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      latheLoft.getLoftPath(
         pointsSpline,
         LatheLoft.getAmounts(latheLoftSize, 0.8F, 0.95F, 0.95F, 0.8F, 0.55F, 0.3F, 0.06F),
         LatheLoft.getAmounts(latheLoftSize, 0.22F, 0.2F, 0.18F, 0.15F, 0.11F, 0.07F, 0.03F),
         8,
         shinBrr,
         null,
         null,
         0.0F,
         sin2,
         cos2
      );
   }

   private static float[] getAmountsForFloat(float dolfinitaBananitaValue) {
      float[] dolfinitaBananitaValues = getAmounts(25.6F, dolfinitaBananitaValue * 70.0F);
      return new float[]{dolfinitaBananitaValues[0] - dolfinitaBananitaValues[3] * 0.34F, dolfinitaBananitaValues[1] - dolfinitaBananitaValues[4] * 0.34F, dolfinitaBananitaValues[2] - dolfinitaBananitaValues[5] * 0.34F, dolfinitaBananitaValues[3], dolfinitaBananitaValues[4], dolfinitaBananitaValues[5]};
   }

   private static void handleForLatheLoftShinBrr(LatheLoft latheLoft, ShinBrr shinBrr) {
      float[] dolfinitaBananitaValues = getAmounts(18.4F, 0.0F);
      float degrees = (float)Math.toDegrees(Math.atan2(dolfinitaBananitaValues[5], dolfinitaBananitaValues[4]));
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(dolfinitaBananitaValues[0] - dolfinitaBananitaValues[3] * 0.3F, dolfinitaBananitaValues[1] - dolfinitaBananitaValues[4] * 0.3F, dolfinitaBananitaValues[2] - dolfinitaBananitaValues[5] * 0.3F).getLatheLoftLatheLoftAmountLoft(degrees);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.05F, 0.5F, 2.0F, 8, shinBrr);
      latheLoft.getDequeArray();
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = !poseProgress.vehicle && !poseProgress.gliding ? Math.min(1.0F, poseProgress.limbSwingStrength) : 0.0F;
      float shinProgressValue = poseProgress.animationProgress * 0.6662F / (float) (Math.PI * 2);
      float shinProgressFactor2 = shinProgressValue - (float)Math.floor(shinProgressValue);
      float shinProgressFactor3;
      float sin4;
      float cos5;
      float shinProgressFactor6;
      if (shinProgressFactor2 < 0.22F) {
         float shinProgressFactor7 = shinProgressFactor2 / 0.22F;
         shinProgressFactor3 = 0.0F;
         sin4 = (float)Math.sin(shinProgressFactor7 * Math.PI);
         cos5 = -((float)Math.cos(shinProgressFactor7 * Math.PI));
         shinProgressFactor6 = 0.0F;
      } else {
         float shinProgressFactor8 = (shinProgressFactor2 - 0.22F) / 0.78F;
         shinProgressFactor3 = (float)Math.sin(shinProgressFactor8 * Math.PI);
         sin4 = 0.0F;
         cos5 = (float)Math.cos(shinProgressFactor8 * Math.PI);
         shinProgressFactor6 = cos5;
      }

      float shinProgressValue2 = shinProgressFactor3 * shinProgressFactor;
      float shinProgressValue3 = sin4 * shinProgressFactor;
      float shinProgressFactor9 = cos5 * shinProgressFactor;
      float planValuesItem = shinProgressFactor6 * shinProgressFactor;
      float shinProgressFactor10 = 1.0F - shinProgressFactor;
      float shinProgressDivisor = 1.0F;
      foreShinScale.getAmountsForeShin2(this.body, 0.0F, 2.6F * shinProgressValue2, 0.0F);
      foreShinScale.getAmounts(this.body, 0.14F * shinProgressFactor9 - 0.08F * shinProgressFactor10, 0.0F, 0.025F * poseProgress.getAgeCode(90.0F, 0.0F) * shinProgressFactor10);
      float shinProgressFactor11 = 1.0F + 0.05F * Math.abs(planValuesItem);
      foreShinScale.getAmountsForForeEarFloat(this.body, 1.0F + 0.08F * shinProgressValue3, (1.0F - 0.17F * shinProgressValue3) * shinProgressFactor11, 1.0F + 0.08F * shinProgressValue3);
      float shinProgressTotal = 0.1F * planValuesItem - 0.16F * shinProgressValue3 + 0.06F * shinProgressFactor10 + 0.03F * poseProgress.getAgeCode(70.0F, 1.0F) * shinProgressFactor10;
      float shinProgressTotal2 = 0.0F;
      float shinProgressValue6 = 0.0F;
      float max3 = Math.max(-0.15F, -0.6F * shinProgressFactor9 + 0.8F * shinProgressValue3);
      float max12 = 0.0F;
      float shinProgressTotal4 = 0.35F * planValuesItem;
      float hashHuntBound = 0.0F;
      if (shinProgressFactor > 0.3F && Math.floorMod((int)Math.floor(shinProgressValue), 5) == 3) {
         float shinProgressValue7 = (float)Math.sin(shinProgressFactor2 * (float) (Math.PI * 2) * 1.5F) * (float)Math.sin(Math.PI * shinProgressFactor2) * shinProgressFactor;
         foreShinScale.getAmountsBoneCount(this.body, 0.0F, 0.0F, 0.24F * shinProgressValue7);
         shinProgressTotal2 -= 0.2F * shinProgressValue7;
         foreShinScale.getAmountsForeShin2(this.body, 1.1F * shinProgressValue7, 0.0F, 0.0F);
         max3 += 0.35F * Math.abs(shinProgressValue7);
         shinProgressTotal4 += 0.6F * Math.abs(shinProgressValue7);
         shinProgressDivisor = 1.0F + 0.3F * Math.abs(shinProgressValue7);
      }

      boolean shinProgressFlag = shinProgressFactor < 0.1F && poseProgress.swingProgress <= 0.0F && !poseProgress.poseProgressActive && !poseProgress.sneakingPose && !poseProgress.swimming && !poseProgress.gliding && !poseProgress.vehicle;
      float shinProgressValue8 = (poseProgress.age + (poseProgress.hashCode & 0xFF)) % 480.0F;
      if (shinProgressFlag && shinProgressValue8 < 26.0F) {
         float absSin = shinProgressValue8 / 26.0F;
         float sin = (float)Math.sin(Math.PI * absSin);
         float angle = (float) (Math.PI * 2) * getAmount(0.0F, 1.0F, absSin);
         float sin2 = (float)Math.sin(angle);
         float cos = (float)Math.cos(angle);
         foreShinScale.getAmountsBoneCount(this.body, 0.0F, angle, 0.0F);
         foreShinScale.getAmountsForeShin2(this.body, this.body.shinFootAmount * sin2, 2.2F * sin, this.body.shinFootAmount * cos - this.body.shinFootAmount);
         max3 += 0.55F * sin;
         shinProgressTotal4 += 0.6F * sin;
         shinProgressDivisor = 1.0F - 0.6F * sin;
         hashHuntBound += 0.25F * sin;
      }

      float shinProgressFactor14 = (poseProgress.age + (poseProgress.hashCode & 127)) % 140.0F;
      if (shinProgressFactor14 < 26.0F && !poseProgress.poseProgressActive) {
         float sin3 = (float)Math.sin(Math.PI * shinProgressFactor14 / 26.0);
         shinProgressTotal += 0.07F * (float)Math.sin(shinProgressFactor14 * (float) (Math.PI * 2) / 6.5F) * sin3 * shinProgressFactor10;
         shinProgressTotal4 += 0.12F * sin3 * shinProgressFactor10;
         hashHuntBound += 0.3F * sin3 * Math.max(0.0F, (float)Math.sin(shinProgressFactor14 * (float) (Math.PI * 2) / 6.5F)) * shinProgressFactor10;
      }

      float shinProgressFactor15 = (poseProgress.age + (poseProgress.hashCode & 63)) % 83.0F;
      if (shinProgressFactor15 < 4.0F) {
         shinProgressDivisor *= 1.0F - 0.88F * (float)Math.sin(Math.PI * shinProgressFactor15 / 4.0);
      }

      foreShinScale.getAmountsForForeEarFloat(this.classShinProgress, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.foreEar, 1.0F, shinProgressDivisor, 1.0F);
      float max16 = Math.max(-0.9F, Math.min(0.9F, poseProgress.shinProgressAmount));
      float wrapDegreesBound = shinProgressTotal + max16 * (poseProgress.sneakingPose ? 0.75F : 0.5F);
      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressValue9;
         if (swingProgress < 0.12F) {
            shinProgressValue9 = 0.3F * getAmount(0.0F, 1.0F, swingProgress / 0.12F);
         } else if (swingProgress < 0.3F) {
            shinProgressValue9 = 0.3F - 0.85F * getAmount(0.0F, 1.0F, (swingProgress - 0.12F) / 0.18F);
         } else if (swingProgress < 0.5F) {
            shinProgressValue9 = -0.55F;
         } else {
            shinProgressValue9 = -0.55F * (1.0F - getAmount(0.0F, 1.0F, (swingProgress - 0.5F) / 0.5F));
         }

         wrapDegreesBound += 0.8F * shinProgressValue9;
         foreShinScale.getAmountsBoneCount(this.body, 0.4F * shinProgressValue9, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.body, 0.0F, 0.8F * Math.max(0.0F, -shinProgressValue9), 2.2F * shinProgressValue9);
         max12 = Math.max(0.0F, -shinProgressValue9);
         shinProgressTotal4 += 0.4F * max12;
         max3 += 0.6F * max12;
         hashHuntBound += 0.65F * Math.min(1.0F, max12 / 0.55F);
      }

      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsForForeEarFloat(this.body, 1.03F, 0.9F, 1.03F);
         foreShinScale.getAmountsBoneCount(this.body, -0.08F, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.dorsal, 0.0F, -1.5F, 0.8F);
         foreShinScale.getAmountsForForeEarFloat(this.dorsal, 0.92F, 1.0F, 0.92F);
         wrapDegreesBound -= 0.12F;
         shinProgressValue6 = 0.35F;
         max3 = Math.max(max3 - 0.3F, -0.2F);
         shinProgressTotal4 -= 0.3F;
      }

      if (poseProgress.vehicle) {
         float foreShinValue = 0.9F;
         float shinProgressFactor17 = 11.0F;
         foreShinScale.getAmountsBoneCount(this.body, foreShinValue, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.body, 0.0F, shinProgressFactor17 * (1.0F - (float)Math.cos(foreShinValue)) + 4.0F, -shinProgressFactor17 * (float)Math.sin(foreShinValue));
         wrapDegreesBound -= foreShinValue + 0.1F;
         max3 += 0.3F;
      }

      if (poseProgress.swimming || poseProgress.gliding) {
         float shinProgressFactor18 = poseProgress.swimming ? Math.max(0.5F, poseProgress.leaningPitch) : 1.0F;
         wrapDegreesBound += (0.52F + 0.1F * poseProgress.getAgeCode(16.0F, 0.0F)) * shinProgressFactor18;
         foreShinScale.getAmountsBoneCount(this.body, (0.6F - 0.1F * poseProgress.getAgeCode(16.0F, 1.2F)) * shinProgressFactor18, 0.0F, 0.0F);
         shinProgressTotal4 -= 0.3F * shinProgressFactor18;
         max3 -= 0.1F * shinProgressFactor18;
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.body, 1.06F, 0.93F, 1.06F);
      }

      float shinProgressBound = Math.min(wrapDegreesBound, 0.42F);
      foreShinScale.getAmounts(this.dorsal, shinProgressBound, poseProgress.headYawRadians * 0.7F, shinProgressTotal2);
      foreShinScale.getAmounts(this.classShinFoot, -0.9F * Math.max(0.0F, shinProgressBound) - shinProgressValue6, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, -Math.min(0.7F, hashHuntBound), 0.0F, 0.0F);
      foreShinScale.getAmounts(this.dolfinitaBananitaForeEar, 0.12F * poseProgress.getAgeCode(36.0F, 0.0F) * shinProgressFactor10, 0.0F, 0.06F + shinProgressTotal4 + 0.05F * poseProgress.getAgeCode(36.0F, 0.8F) * shinProgressFactor10);
      foreShinScale.getAmounts(this.shinFootForeEar, 0.12F * poseProgress.getAgeCode(36.0F, 0.4F) * shinProgressFactor10, 0.0F, -0.06F - shinProgressTotal4 - 0.05F * poseProgress.getAgeCode(36.0F, 1.2F) * shinProgressFactor10);
      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsBoneCount(this.dolfinitaBananitaForeEar, -0.7F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.shinFootForeEar, -0.7F, 0.0F, 0.0F);
         foreShinScale.getClassForForeEarFloat(this.dolfinitaBananitaForeEar, 0.8F);
         foreShinScale.getClassForForeEarFloat(this.shinFootForeEar, 0.8F);
      }

      foreShinScale.getAmountsBoneCount(this.dolfinitaBananitaForeEar, 0.0F, 0.0F, 0.5F * max12);
      foreShinScale.getAmountsBoneCount(this.shinFootForeEar, 0.0F, 0.0F, -0.5F * max12);

      for (int i = 0; i < 4; i++) {
         float shinProgressFactor19 = max3 + 0.05F * poseProgress.getAgeCode(48.0F, i * 1.7F) * shinProgressFactor10 + 0.1F * (float)Math.sin(shinProgressFactor2 * (float) (Math.PI * 2) + i * 1.3F) * shinProgressFactor;
         foreShinScale.getAmounts(this.shinFootForeEars[i], -FIN_DIRECTION_Z[i] * shinProgressFactor19, 0.0F, shinProgressTable[i] * shinProgressFactor19);
      }
   }

   @Override
   public String formatTungDinString() {
      return "Bananita Dolfinita";
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(15913019);
      int valueForInt = ForeShinUtils.getValueForInt(16182469);
      int valueForInt3 = ForeShinUtils.getValueForInt(14864792);

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuValue4 = (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue5 = (j + 0.5F) / ambalabuBonecaValue;
            float abs6 = Math.abs(bonecaAmbalabuValue5 - 0.5F);
            int foreShinValue = ForeShinUtils.getValue2(valueForInt, bonecaAmbalabuValue, getAmount(0.19F, 0.23F, abs6));
            if (abs6 < 0.2F) {
               float absSin = (float)Math.abs(Math.sin((bonecaAmbalabuValue5 * 7.0F + 0.15F * (float)Math.sin(bonecaAmbalabuValue4 * 9.0F)) * Math.PI));
               foreShinValue = ForeShinUtils.getValue2(foreShinValue, valueForInt3, 0.55F * getAmount(0.82F, 0.98F, absSin));
            }

            int bonecaAmbalabuValue8 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(11569710), 0.6F * getAmount(0.9F, 1.0F, bonecaAmbalabuValue4));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue8);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 6.0F, 77);
   }

   private static float getAmountForFloat(float dolfinitaBananitaValue, float dolfinitaBananitaValue2, float dolfinitaBananitaDivisor) {
      float dolfinitaBananitaLimit = (dolfinitaBananitaValue2 - dolfinitaBananitaValue) / dolfinitaBananitaDivisor + 0.5F;
      return dolfinitaBananitaLimit < 0.0F ? 0.0F : (dolfinitaBananitaLimit > 1.0F ? 1.0F : dolfinitaBananitaLimit);
   }

   private static float[] getAmountsTAIL_ANGLE_KEYFRAMES(float dolfinitaBananitaBound) {
      float[] dolfinitaBananitaValues = getAmountsPROFILE_KEYFRAME_COUNT(dolfinitaBananitaBound);
      float angle = (getAmount(TAIL_ANGLE_KEYFRAMES, 2, 1, Math.max(TAIL_ANGLE_KEYFRAMES[0], dolfinitaBananitaBound)) - 2.0F) * (float) (Math.PI / 180.0);
      float dolfinitaBananitaFactor = dolfinitaBananitaValues[6];
      float dolfinitaBananitaFactor2 = dolfinitaBananitaValues[7];
      float dolfinitaBananitaValue = dolfinitaBananitaFactor2 * (float)Math.cos(angle);
      float dolfinitaBananitaValue2 = dolfinitaBananitaFactor * (float)Math.sin(angle);
      float dolfinitaBananitaFactor3 = 0.2F * dolfinitaBananitaFactor2;
      float deltaVar = dolfinitaBananitaValue - dolfinitaBananitaFactor3;
      float dolfinitaBananitaFactor4 = deltaVar + 1.1F * dolfinitaBananitaFactor2;
      float dolfinitaBananitaFactor5 = dolfinitaBananitaValue2 / (float)Math.sqrt(1.0F - dolfinitaBananitaFactor3 * dolfinitaBananitaFactor3 / (dolfinitaBananitaFactor4 * dolfinitaBananitaFactor4));
      float dolfinitaBananitaValue3 = getAmount(26.5F, 26.95F, dolfinitaBananitaBound);
      float dolfinitaBananitaFactor6 = 0.75F + 0.25F * dolfinitaBananitaValue3;
      return new float[]{dolfinitaBananitaValues[0] + dolfinitaBananitaValues[3] * deltaVar, dolfinitaBananitaValues[1] + dolfinitaBananitaValues[4] * deltaVar, dolfinitaBananitaValues[2] + dolfinitaBananitaValues[5] * deltaVar, dolfinitaBananitaFactor5 * dolfinitaBananitaFactor6, dolfinitaBananitaFactor4 * dolfinitaBananitaFactor6};
   }

   @Override
   public int getValue() {
      return 1;
   }

   private static void handle(LatheLoft latheLoft, ForeEar foreEar, float dolfinitaBananitaFactor, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float[] dolfinitaBananitaValues = getAmountsForFloat(dolfinitaBananitaFactor);
      float degrees = (float)Math.toDegrees(Math.atan2(-dolfinitaBananitaValues[3], -dolfinitaBananitaValues[5]));
      float degrees2 = (float)Math.toDegrees(Math.asin(Math.max(-1.0F, Math.min(1.0F, dolfinitaBananitaValues[4]))));
      latheLoft.getLatheLoftForForeEar(foreEar);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(dolfinitaBananitaValues[0], dolfinitaBananitaValues[1], dolfinitaBananitaValues[2]).getLatheLoft4(degrees).getLatheLoftLatheLoftAmountLoft(degrees2);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.8F, 0.7F, 0.42F, 12, shinBrr);
      latheLoft.getLatheLoft(-0.32F * dolfinitaBananitaFactor, 0.24F, -0.3F, 0.17F, 6, shinBrr2);
      latheLoft.getDequeArray();
   }

   private static ForeEar getClassForLatheLoftString(LatheLoft latheLoft, String classText, ForeEar foreEar, float classValue, float classValue2, float classValue3) {
      return latheLoft.getClassForStringForeEar(classText, foreEar, classValue * 1.08F, classValue2 * 1.08F, classValue3 * 1.08F);
   }

   static {
      for (int i = 0; i <= 670; i++) {
         PATH_ANGLES[i] = getAmount(dolfinitaBananitaTable, 2, 1, -1.6F + 33.8F * i / 670.0F) * (float) (Math.PI / 180.0);
      }

      float dolfinitaBananitaFactor = 0.05044776F;

      for (int j = 1; j <= 670; j++) {
         float angle = 0.5F * (PATH_ANGLES[j - 1] + PATH_ANGLES[j]);
         PATH_X[j] = PATH_X[j - 1] + (float)Math.cos(angle) * dolfinitaBananitaFactor;
         PATH_Y[j] = PATH_Y[j - 1] - (float)Math.sin(angle) * dolfinitaBananitaFactor;
      }

      float[] dolfinitaBananitaValues = getAmountsPATH_X(-1.5F);
      float dolfinitaBananitaValue = dolfinitaBananitaValues[0] - 0.45F * Math.abs((float)Math.sin(dolfinitaBananitaValues[2])) - 0.02F;
      float dolfinitaBananitaBound = Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;

      for (float dolfinitaBananitaTotal = 0.0F; dolfinitaBananitaTotal <= 31.5F; dolfinitaBananitaTotal += 0.25F) {
         float[] dolfinitaBananitaValues2 = getAmountsPATH_X(dolfinitaBananitaTotal);
         float dolfinitaBananitaFactor2 = dolfinitaBananitaTotal < 15.8F ? getAmount(table, 2, 1, dolfinitaBananitaTotal) * 1.06F : getAmount(bonecaTable, 4, 2, dolfinitaBananitaTotal);
         float dolfinitaBananitaFactor3 = dolfinitaBananitaTotal < 15.8F ? dolfinitaBananitaFactor2 : getAmount(bonecaTable, 4, 3, dolfinitaBananitaTotal);
         float cos = (float)Math.cos(dolfinitaBananitaValues2[2]);
         pilotStatusBound = Math.max(pilotStatusBound, dolfinitaBananitaValues2[1] + cos * dolfinitaBananitaFactor2);
         dolfinitaBananitaBound = Math.min(dolfinitaBananitaBound, dolfinitaBananitaValues2[1] - cos * dolfinitaBananitaFactor3);
      }

      float dolfinitaBananitaValue2 = 0.5F * (dolfinitaBananitaBound + pilotStatusBound);

      for (int k = 0; k <= 670; k++) {
         PATH_X[k] = PATH_X[k] - dolfinitaBananitaValue;
         PATH_Y[k] = PATH_Y[k] - dolfinitaBananitaValue2;
      }

      for (int m = 0; m < PROFILE_KEYFRAME_COUNT; m++) {
         float dolfinitaBananitaValue3 = PROFILE_KEYFRAMES[m];
         float[] dolfinitaBananitaValues3 = getAmountsPATH_X(dolfinitaBananitaValue3);
         float dolfinitaBananitaValue4 = getAmount(bonecaTable, 4, 1, dolfinitaBananitaValue3);
         float dolfinitaBananitaValue5 = getAmount(bonecaTable, 4, 2, dolfinitaBananitaValue3);
         float dolfinitaBananitaTotal2 = 0.0F;
         float dolfinitaBananitaTotal3 = 0.0F;

         for (int p = -8; p <= 8; p++) {
            float planValuesItem = 0.2F * p;
            float dolfinitaBananitaValue7 = 1.7F - Math.abs(planValuesItem);
            dolfinitaBananitaTotal2 += dolfinitaBananitaValue7 * 0.5F * (getAmount(bonecaTable, 4, 2, dolfinitaBananitaValue3 + planValuesItem) - getAmount(bonecaTable, 4, 3, dolfinitaBananitaValue3 + planValuesItem));
            dolfinitaBananitaTotal3 += dolfinitaBananitaValue7;
         }

         float dolfinitaBananitaFactor4 = dolfinitaBananitaTotal2 / dolfinitaBananitaTotal3;
         shinFootTable[3 * m] = 0.0F;
         shinFootTable[3 * m + 1] = dolfinitaBananitaValues3[0] + (float)Math.sin(dolfinitaBananitaValues3[2]) * dolfinitaBananitaFactor4;
         shinFootTable[3 * m + 2] = dolfinitaBananitaValues3[1] + (float)Math.cos(dolfinitaBananitaValues3[2]) * dolfinitaBananitaFactor4;
         RING_SEMI_AXES_A[m] = dolfinitaBananitaValue4;
         RING_SEMI_AXES_B[m] = dolfinitaBananitaValue5 - dolfinitaBananitaFactor4;
      }

      float[] floats = new float[PROFILE_KEYFRAME_COUNT];

      for (int q = 1; q < PROFILE_KEYFRAME_COUNT; q++) {
         float dolfinitaBananitaFactor5 = shinFootTable[3 * q] - shinFootTable[3 * q - 3];
         float dolfinitaBananitaFactor6 = shinFootTable[3 * q + 1] - shinFootTable[3 * q - 2];
         float dolfinitaBananitaFactor7 = shinFootTable[3 * q + 2] - shinFootTable[3 * q - 1];
         floats[q] = floats[q - 1] + (float)Math.sqrt(dolfinitaBananitaFactor5 * dolfinitaBananitaFactor5 + dolfinitaBananitaFactor6 * dolfinitaBananitaFactor6 + dolfinitaBananitaFactor7 * dolfinitaBananitaFactor7);
      }

      for (int r = 0; r < PROFILE_KEYFRAME_COUNT; r++) {
         NORMALIZED_ARC_LENGTHS[r] = floats[r] / floats[PROFILE_KEYFRAME_COUNT - 1];
      }

      dolfinitaBananitaFloats = getAmounts(shinFootTable, 0.0F, 0.0F, 1.0F);

      for (int s = 0; s < 4; s++) {
         float[] dolfinitaBananitaValues4 = getAmounts(s);
         float distance = (float)Math.sqrt(dolfinitaBananitaValues4[0] * dolfinitaBananitaValues4[0] + dolfinitaBananitaValues4[2] * dolfinitaBananitaValues4[2]);
         shinProgressTable[s] = dolfinitaBananitaValues4[0] / distance;
         FIN_DIRECTION_Z[s] = dolfinitaBananitaValues4[2] / distance;
      }
   }

   private static float[] getAmountsPATH_X(float dolfinitaBananitaValue) {
      float hashRegistryValue = (dolfinitaBananitaValue - -1.6F) / 33.8F * 670.0F;
      int index = Math.max(0, Math.min(669, (int)Math.floor(hashRegistryValue)));
      float deltaVar = hashRegistryValue - index;
      return new float[]{
         PATH_X[index] + (PATH_X[index + 1] - PATH_X[index]) * deltaVar,
         PATH_Y[index] + (PATH_Y[index + 1] - PATH_Y[index]) * deltaVar,
         PATH_ANGLES[index] + (PATH_ANGLES[index + 1] - PATH_ANGLES[index]) * deltaVar
      };
   }

   @Override
   public String formatNoteBlock() {
      return "entity.dolphin.play";
   }

   private static float[] getAmountsPROFILE_KEYFRAME_COUNT(float dolfinitaBananitaLimit) {
      int index = 0;

      while (index < PROFILE_KEYFRAME_COUNT - 2 && PROFILE_KEYFRAMES[index + 1] < dolfinitaBananitaLimit) {
         index++;
      }

      float dolfinitaBananitaLimit2 = (dolfinitaBananitaLimit - PROFILE_KEYFRAMES[index]) / (PROFILE_KEYFRAMES[index + 1] - PROFILE_KEYFRAMES[index]);
      float dolfinitaBananitaFactor = dolfinitaBananitaLimit2 < 0.0F ? 0.0F : (dolfinitaBananitaLimit2 > 1.0F ? 1.0F : dolfinitaBananitaLimit2);
      float[] floats = new float[11];

      for (int i = 0; i < 3; i++) {
         floats[i] = shinFootTable[3 * index + i] + (shinFootTable[3 * index + 3 + i] - shinFootTable[3 * index + i]) * dolfinitaBananitaFactor;
         floats[3 + i] = dolfinitaBananitaFloats[1][3 * index + i] + (dolfinitaBananitaFloats[1][3 * index + 3 + i] - dolfinitaBananitaFloats[1][3 * index + i]) * dolfinitaBananitaFactor;
         floats[8 + i] = dolfinitaBananitaFloats[0][3 * index + i] + (dolfinitaBananitaFloats[0][3 * index + 3 + i] - dolfinitaBananitaFloats[0][3 * index + i]) * dolfinitaBananitaFactor;
      }

      floats[6] = RING_SEMI_AXES_A[index] + (RING_SEMI_AXES_A[index + 1] - RING_SEMI_AXES_A[index]) * dolfinitaBananitaFactor;
      floats[7] = RING_SEMI_AXES_B[index] + (RING_SEMI_AXES_B[index + 1] - RING_SEMI_AXES_B[index]) * dolfinitaBananitaFactor;

      for (int j = 3; j <= 8; j += 5) {
         float distance = (float)Math.sqrt(floats[j] * floats[j] + floats[j + 1] * floats[j + 1] + floats[j + 2] * floats[j + 2]);
         floats[j] /= distance;
         floats[j + 1] = floats[j + 1] / distance;
         floats[j + 2] = floats[j + 2] / distance;
      }

      return floats;
   }

   @Override
   public String formatTungDin() {
      return "bananita_dolfinita";
   }

   private static void handleAmounts(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2) {
      int dolfinitaBananitaSize = RING_HEIGHTS.length;
      float[] floats = new float[3 * dolfinitaBananitaSize];
      float[] crownForeValues = new float[dolfinitaBananitaSize];
      float[] floats3 = new float[dolfinitaBananitaSize];
      float[] dolfinitaBananitaValues = getAmountsPROFILE_KEYFRAME_COUNT(RING_HEIGHTS[0]);

      for (int i = 0; i < dolfinitaBananitaSize; i++) {
         float[] amountsTAILANGLEKEYFRAMES = getAmountsTAIL_ANGLE_KEYFRAMES(RING_HEIGHTS[i]);
         System.arraycopy(amountsTAILANGLEKEYFRAMES, 0, floats, 3 * i, 3);
         crownForeValues[i] = amountsTAILANGLEKEYFRAMES[3];
         floats3[i] = amountsTAILANGLEKEYFRAMES[4];
      }

      latheLoft.getLoftPath(floats, crownForeValues, floats3, 20, shinBrr, null, null, dolfinitaBananitaValues[3], dolfinitaBananitaValues[4], dolfinitaBananitaValues[5]);
      float cos = (float)Math.cos(0.80285144F);
      float sin = (float)Math.sin(0.80285144F);

      for (int j = 0; j < 5; j++) {
         float dolfinitaBananitaValue = 27.45F + 0.7F * j;
         float[] amountsPROFILEKEYFRAMECOUNT = getAmountsPROFILE_KEYFRAME_COUNT(dolfinitaBananitaValue);
         float[] amountsTailAngleKeyframes = getAmountsTAIL_ANGLE_KEYFRAMES(dolfinitaBananitaValue);
         float dolfinitaBananitaFactor = amountsPROFILEKEYFRAMECOUNT[9] * amountsPROFILEKEYFRAMECOUNT[5] - amountsPROFILEKEYFRAMECOUNT[10] * amountsPROFILEKEYFRAMECOUNT[4];
         float dolfinitaBananitaFactor2 = amountsPROFILEKEYFRAMECOUNT[10] * amountsPROFILEKEYFRAMECOUNT[3] - amountsPROFILEKEYFRAMECOUNT[8] * amountsPROFILEKEYFRAMECOUNT[5];
         float dolfinitaBananitaFactor3 = amountsPROFILEKEYFRAMECOUNT[8] * amountsPROFILEKEYFRAMECOUNT[4] - amountsPROFILEKEYFRAMECOUNT[9] * amountsPROFILEKEYFRAMECOUNT[3];
         float dolfinitaBananitaFactor4 = 0.2F - 0.02F * j;
         float dolfinitaBananitaFactor5 = 0.3F - 0.035F * j;

         for (int k = -1; k <= 1; k += 2) {
            float dolfinitaBananitaFactor6 = amountsTailAngleKeyframes[4] * cos;
            float dolfinitaBananitaFactor7 = k * amountsTailAngleKeyframes[3] * sin;
            float dolfinitaBananitaValue2 = amountsTailAngleKeyframes[0] + amountsPROFILEKEYFRAMECOUNT[3] * dolfinitaBananitaFactor6 + dolfinitaBananitaFactor * dolfinitaBananitaFactor7;
            float dolfinitaBananitaValue3 = amountsTailAngleKeyframes[1] + amountsPROFILEKEYFRAMECOUNT[4] * dolfinitaBananitaFactor6 + dolfinitaBananitaFactor2 * dolfinitaBananitaFactor7;
            float dolfinitaBananitaValue4 = amountsTailAngleKeyframes[2] + amountsPROFILEKEYFRAMECOUNT[5] * dolfinitaBananitaFactor6 + dolfinitaBananitaFactor3 * dolfinitaBananitaFactor7;
            float dolfinitaBananitaFactor8 = -k * 0.08F;
            float dolfinitaBananitaValue5 = dolfinitaBananitaValue2 + amountsPROFILEKEYFRAMECOUNT[3] * dolfinitaBananitaFactor5 + dolfinitaBananitaFactor * dolfinitaBananitaFactor8;
            float dolfinitaBananitaValue6 = dolfinitaBananitaValue3 + amountsPROFILEKEYFRAMECOUNT[4] * dolfinitaBananitaFactor5 + dolfinitaBananitaFactor2 * dolfinitaBananitaFactor8;
            float dolfinitaBananitaValue7 = dolfinitaBananitaValue4 + amountsPROFILEKEYFRAMECOUNT[5] * dolfinitaBananitaFactor5 + dolfinitaBananitaFactor3 * dolfinitaBananitaFactor8;
            float[] floats4 = new float[]{dolfinitaBananitaValue2 - amountsPROFILEKEYFRAMECOUNT[8] * dolfinitaBananitaFactor4, dolfinitaBananitaValue3 - amountsPROFILEKEYFRAMECOUNT[9] * dolfinitaBananitaFactor4, dolfinitaBananitaValue4 - amountsPROFILEKEYFRAMECOUNT[10] * dolfinitaBananitaFactor4};
            float[] floats5 = new float[]{dolfinitaBananitaValue2 + amountsPROFILEKEYFRAMECOUNT[8] * dolfinitaBananitaFactor4, dolfinitaBananitaValue3 + amountsPROFILEKEYFRAMECOUNT[9] * dolfinitaBananitaFactor4, dolfinitaBananitaValue4 + amountsPROFILEKEYFRAMECOUNT[10] * dolfinitaBananitaFactor4};
            float[] floats6 = new float[]{dolfinitaBananitaValue2 - dolfinitaBananitaFactor * k * 0.18F, dolfinitaBananitaValue3 - dolfinitaBananitaFactor2 * k * 0.18F, dolfinitaBananitaValue4 - dolfinitaBananitaFactor3 * k * 0.18F};
            float[] floats7 = new float[]{dolfinitaBananitaValue5, dolfinitaBananitaValue6, dolfinitaBananitaValue7};
            float[] floats8 = new float[]{
               (floats4[0] + floats5[0] + floats6[0] + floats7[0]) * 0.25F,
               (floats4[1] + floats5[1] + floats6[1] + floats7[1]) * 0.25F,
               (floats4[2] + floats5[2] + floats6[2] + floats7[2]) * 0.25F
            };
            handle(latheLoft, floats4, floats5, floats7, floats8, shinBrr2);
            handle(latheLoft, floats5, floats6, floats7, floats8, shinBrr2);
            handle(latheLoft, floats6, floats4, floats7, floats8, shinBrr2);
         }
      }
   }

   private static void handleAmounts(LatheLoft latheLoft, int dolfinitaBananitaValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float angle = 72.0F * dolfinitaBananitaValue * (float) (Math.PI / 180.0);
      int dolfinitaBananitaSize = RING_DISTANCES.length;
      float[] floats = new float[3 * dolfinitaBananitaSize];
      float[] floats2 = new float[dolfinitaBananitaSize];
      float[] crownForeValues = new float[dolfinitaBananitaSize];
      float loftPathValue = 0.0F;
      float dolfinitaBananitaValue3 = 0.0F;
      float dolfinitaBananitaValue4 = 0.0F;

      for (int i = 0; i < dolfinitaBananitaSize; i++) {
         float[] dolfinitaBananitaValues = getAmountsPATH_X(RING_DISTANCES[i]);
         float dolfinitaBananitaFactor = getAmount(table, 2, 1, RING_DISTANCES[i]);
         float sin = (float)Math.sin(angle);
         float dolfinitaBananitaValue5 = (float)Math.sin(dolfinitaBananitaValues[2]) * (float)Math.cos(angle);
         float dolfinitaBananitaValue6 = (float)Math.cos(dolfinitaBananitaValues[2]) * (float)Math.cos(angle);
         floats[3 * i] = sin * 0.42F * dolfinitaBananitaFactor;
         floats[3 * i + 1] = dolfinitaBananitaValues[0] + dolfinitaBananitaValue5 * 0.42F * dolfinitaBananitaFactor;
         floats[3 * i + 2] = dolfinitaBananitaValues[1] + dolfinitaBananitaValue6 * 0.42F * dolfinitaBananitaFactor;
         floats2[i] = 0.78F * dolfinitaBananitaFactor;
         crownForeValues[i] = 0.55F * dolfinitaBananitaFactor;
         if (i == 0) {
            loftPathValue = sin;
            dolfinitaBananitaValue3 = dolfinitaBananitaValue5;
            dolfinitaBananitaValue4 = dolfinitaBananitaValue6;
         }
      }

      latheLoft.getLoftPath(floats, floats2, crownForeValues, 10, shinBrr, null, shinBrr2, loftPathValue, dolfinitaBananitaValue3, dolfinitaBananitaValue4);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15913019), ForeShinUtils.getValueForInt(16771696), 0.5F);
      int valueForInt3 = ForeShinUtils.getValueForInt(13224266);
      int valueForInt4 = ForeShinUtils.getValueForInt(4008724);
      int valueForInt = ForeShinUtils.getValueForInt(12096030);
      int valueForInt6 = ForeShinUtils.getValueForInt(16441450);

      for (int i = 0; i < bonecaValue; i++) {
         float absSin = (i + 0.5F) / bonecaValue;
         float bonecaAmbalabuFactor = getAmount(0.05F, 0.16F, absSin) * (1.0F - getAmount(0.75F, 1.0F, absSin));

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float abs8 = Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F);
            int bonecaAmbalabuValue9 = ForeShinUtils.getValue2(value2, valueForInt6, 0.55F * (1.0F - getAmount(0.0F, 0.12F, abs8)) * bonecaAmbalabuFactor);
            int bonecaAmbalabuValue10 = ForeShinUtils.getValue2(bonecaAmbalabuValue9, valueForInt3, 0.3F * getAmount(0.85F, 1.0F, absSin));
            int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabuValue10, ForeShinUtils.getValueForInt(10123818), 1.0F - getAmount(0.05F, 0.16F, absSin));
            int bonecaAmbalabuValue12 = ForeShinUtils.getValue2(foreShinValue, valueForInt4, 1.0F - getAmount(0.02F, 0.07F, absSin));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue2(bonecaAmbalabuValue12, valueForInt, 0.25F * getAmount(0.46F, 0.5F, abs8)));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.04F, 9.0F, bonecaAmbalabuValue);
      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(7031326, 0.85F), 0.32F, 0.9F, bonecaAmbalabuValue + 1);
      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(9071146, 0.45F), 0.12F, 1.8F, bonecaAmbalabuValue + 2);
   }

   private static void handleAmountsBoneca(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int value3 = bonecaAmbalabu.bonecaValue;
      int bonecaValue = ForeShinUtils.getValueForInt(5137784);
      int valueForInt2 = ForeShinUtils.getValueForInt(8361901);
      int valueForInt3 = ForeShinUtils.getValueForInt(15002868);
      int valueForInt4 = ForeShinUtils.getValueForInt(4149862);
      int valueForInt = ForeShinUtils.getValueForInt(5663873);
      int valueForInt6 = ForeShinUtils.getValueForInt(3886430);
      int valueForInt7 = ForeShinUtils.getValueForInt(11847898);
      int valueForInt8 = ForeShinUtils.getValueForInt(5910074);

      for (int i = 0; i < value3; i++) {
         float dolfinitaBananitaLimit = (i + 0.5F) / value3;
         float bonecaLimit = getAmounts(dolfinitaBananitaLimit);
         float bonecaFactor = getAmount(bonecaTable, 4, 1, bonecaLimit);
         float bonecaFactor2 = 0.5F * (getAmount(bonecaTable, 4, 2, bonecaLimit) + getAmount(bonecaTable, 4, 3, bonecaLimit));
         float bonecaValue10 = 40.0F + 7.0F * (float)Math.sin(0.7F * bonecaLimit + 1.0F);
         if (bonecaLimit > 23.5F) {
            bonecaValue10 *= 1.0F - getAmount(23.5F, 27.2F, bonecaLimit);
         }

         float bonecaValue11 = getAmount(BODY_ANGLE_KEYFRAMES, 2, 1, bonecaLimit)
            + (getAmount(TAIL_ANGLE_KEYFRAMES, 2, 1, bonecaLimit) + 1.0F - getAmount(BODY_ANGLE_KEYFRAMES, 2, 1, bonecaLimit)) * getAmount(26.4F, 27.0F, bonecaLimit);
         float bonecaValue12 = 142.0F + 4.0F * (float)Math.sin(0.9F * bonecaLimit) + (bonecaValue11 - 142.0F - 4.0F * (float)Math.sin(0.9F * bonecaLimit)) * getAmount(21.2F, 23.6F, bonecaLimit);
         float bonecaLimit2 = getAmount(23.2F, 23.7F, bonecaLimit) * (1.0F - getAmount(24.7F, 25.2F, bonecaLimit));
         float bonecaBound = TAIL_ANGLE_KEYFRAMES[0];
         float bonecaValue13 = getAmount(TAIL_ANGLE_KEYFRAMES, 2, 1, Math.max(bonecaBound, bonecaLimit));

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float angle = (float) (Math.PI * 2) * ((j + 0.5F) / ambalabuBonecaValue - 0.5F);
            float bonecaValue14 = Math.abs(angle) / (float) (Math.PI / 180.0);
            float sin = (float)Math.sin(angle);
            float cos = (float)Math.cos(angle);
            float sqrt = (float)Math.sqrt(bonecaFactor2 * sin * bonecaFactor2 * sin + bonecaFactor * cos * bonecaFactor * cos);
            int value2 = ForeShinUtils.getValue2(bonecaValue, valueForInt2, getAmount(bonecaValue10 - 12.0F, bonecaValue10 + 12.0F, bonecaValue14));
            float bonecaValue16 = getAmount(bonecaValue12 - 36.0F, bonecaValue12 - 12.0F, bonecaValue14)
               * (1.0F - getAmount(bonecaValue12 - 7.0F, bonecaValue12 - 2.0F, bonecaValue14))
               * getAmount(16.6F, 18.2F, bonecaLimit)
               * (1.0F - getAmount(24.2F, 26.4F, bonecaLimit));
            int foreShinValue = ForeShinUtils.getValue2(value2, bonecaValue, 0.42F * bonecaValue16);
            float bonecaValue18 = getAmount(bonecaValue12 - 5.0F, bonecaValue12 + 5.0F, bonecaValue14);
            int bonecaValue19 = ForeShinUtils.getValue2(foreShinValue, valueForInt3, bonecaValue18);
            int bonecaValue20 = ForeShinUtils.getValue(bonecaValue19, 1.0F - 0.05F * bonecaValue18 * (1.0F - getAmount(bonecaValue12 + 3.0F, 176.0F, bonecaValue14)));
            if (bonecaLimit2 > 0.0F) {
               bonecaValue20 = ForeShinUtils.getValue2(bonecaValue20, bonecaValue, 0.15F * bonecaLimit2 * getAmount(156.0F, 164.0F, bonecaValue14));
            }

            float bonecaLimit3 = getAmountForFloat(Math.abs(bonecaValue14 - 34.0F) * (float) (Math.PI / 180.0) * sqrt, 0.32F, 0.35F)
               * getAmount(15.6F, 17.6F, bonecaLimit)
               * (1.0F - getAmount(25.8F, 27.4F, bonecaLimit));
            if (bonecaLimit3 > 0.0F) {
               bonecaValue20 = ForeShinUtils.getValue2(bonecaValue20, valueForInt7, 0.12F * bonecaLimit3);
            }

            int bonecaValue21 = ForeShinUtils.getValue2(bonecaValue20, ForeShinUtils.getValueForInt(3029832), 0.2F * (1.0F - getAmount(15.65F, 16.3F, bonecaLimit)));
            float bonecaFactor3 = (bonecaValue14 - 70.0F) * (float) (Math.PI / 180.0) * sqrt;
            float absSin = bonecaLimit - 25.6F;
            float sqrt2 = (float)Math.sqrt(absSin * absSin / 0.95F + bonecaFactor3 * bonecaFactor3 / 0.75F);
            if (sqrt2 < 1.3F) {
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, valueForInt, 0.3F * (1.0F - getAmount(0.6F, 1.3F, sqrt2)));
            }

            float distance = (float)Math.sqrt(absSin * absSin + bonecaFactor3 * bonecaFactor3);
            float bonecaLimit4 = getAmountForFloat(Math.abs(distance - 0.86F), 0.06F, 0.04F) * getAmount(0.05F, 0.45F, bonecaFactor3) * getAmount(-0.75F, -0.2F, absSin);
            if (bonecaLimit4 > 0.0F) {
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, valueForInt4, 0.35F * bonecaLimit4);
            }

            if (bonecaLimit > 22.0F && bonecaLimit < 25.6F) {
               float bonecaValue22 = (25.6F - bonecaLimit) / 3.6000004F;
               float bonecaValue23 = 78.0F + 42.0F * bonecaValue22;
               float bonecaLimit5 = getAmountForFloat(Math.abs(bonecaValue14 - bonecaValue23) * (float) (Math.PI / 180.0) * sqrt, 0.25F, 0.18F)
                  * getAmount(0.0F, 0.22F, bonecaValue22)
                  * (1.0F - getAmount(0.8F, 1.0F, bonecaValue22));
               if (bonecaLimit5 > 0.0F) {
                  bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, bonecaValue, 0.35F * bonecaLimit5);
               }
            }

            float bonecaFactor5 = 1.0F - getAmount(70.0F, 82.0F, bonecaValue14);
            float bonecaLimit6 = getAmount(26.45F, 26.6F, bonecaLimit) * (1.0F - getAmount(26.85F, 26.95F, bonecaLimit)) * bonecaFactor5;
            if (bonecaLimit6 > 0.0F) {
               bonecaValue21 = ForeShinUtils.getValue(bonecaValue21, 1.0F - 0.1F * bonecaLimit6);
            }

            float bonecaLimit7 = getAmount(26.95F, 27.4F, bonecaLimit) * (1.0F - getAmount(28.6F, 30.4F, bonecaLimit)) * (1.0F - getAmount(35.0F, 70.0F, bonecaValue14));
            if (bonecaLimit7 > 0.0F) {
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, valueForInt7, 0.15F * bonecaLimit7);
            }

            float bonecaLimit8 = getAmountForFloat(Math.abs(bonecaLimit - 26.9F), 0.08F, 0.3F) * bonecaFactor5;
            if (bonecaLimit8 > 0.0F) {
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, valueForInt4, 0.22F * bonecaLimit8);
            }

            float planValuesItem = (bonecaValue14 - bonecaValue13) * (float) (Math.PI / 180.0) * sqrt;
            float dolfinitaBananitaValue = bonecaLimit >= bonecaBound ? Math.abs(planValuesItem) : (float)Math.sqrt((bonecaLimit - bonecaBound) * (bonecaLimit - bonecaBound) + planValuesItem * planValuesItem);
            float bonecaLimit9 = getAmount(0.2F, 0.28F, planValuesItem) * (1.0F - getAmount(0.5F, 0.6F, planValuesItem)) * getAmount(bonecaBound, bonecaBound + 0.4F, bonecaLimit);
            if (bonecaLimit9 > 0.0F) {
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, valueForInt3, 0.45F * bonecaLimit9);
            }

            float bonecaLimit10 = getAmount(26.95F, 27.25F, bonecaLimit) * (1.0F - getAmount(31.0F, 31.3F, bonecaLimit)) * getAmount(bonecaValue13 + 12.0F, bonecaValue13 + 17.0F, bonecaValue14);
            if (bonecaLimit10 > 0.0F) {
               float bonecaLimit11 = (bonecaValue14 - bonecaValue13 - 19.0F) * (float) (Math.PI / 180.0) * sqrt;
               float hashRegistryValue = (bonecaLimit - 27.3F) / 0.6F;
               float bonecaValue26 = hashRegistryValue - (float)Math.floor(hashRegistryValue);
               float bonecaValue27 = bonecaLimit > 27.2F && bonecaLimit < 30.3F && bonecaLimit11 > 0.0F && bonecaLimit11 < 0.34F
                  ? getAmountForFloat(Math.abs(bonecaValue26 - 0.5F), 0.22F * (1.0F - bonecaLimit11 / 0.34F), 0.05F)
                  : 0.0F;
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, ForeShinUtils.getValue2(valueForInt8, ForeShinUtils.getValueForInt(16053488), bonecaValue27), bonecaLimit10);
            }

            float bonecaLimit12 = getAmountForFloat(Math.abs(planValuesItem - 0.24F), 0.22F, 0.06F) * getAmount(26.95F, 27.25F, bonecaLimit);
            if (bonecaLimit12 > 0.0F) {
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, valueForInt4, bonecaLimit12);
            }

            float amountForFloat = getAmountForFloat(dolfinitaBananitaValue, 0.16F, 0.07F);
            if (amountForFloat > 0.0F) {
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, valueForInt4, amountForFloat);
            }

            float bonecaFactor7 = (bonecaValue14 - 88.5F) * (float) (Math.PI / 180.0) * sqrt;
            float bonecaFactor8 = bonecaLimit - 26.5F;
            float foreShinFactor = getAmountForFloat((float)Math.sqrt(bonecaFactor7 * bonecaFactor7 + bonecaFactor8 * bonecaFactor8), 0.25F, 0.06F);
            if (foreShinFactor > 0.0F) {
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, ForeShinUtils.getValueForInt(2898762), foreShinFactor);
            }

            float bonecaFactor9 = angle * sqrt;
            float bonecaFactor10 = bonecaLimit - 22.32F;
            float amountForFloat11 = getAmountForFloat((float)Math.sqrt(bonecaFactor9 * bonecaFactor9 / 0.09F + bonecaFactor10 * bonecaFactor10 / 0.0256F), 1.0F, 0.25F);
            float amountForFloat28 = getAmountForFloat((float)Math.sqrt(bonecaFactor9 * bonecaFactor9 + (bonecaFactor10 - 0.36F) * (bonecaFactor10 - 0.36F)), 0.3F, 0.05F);
            float bonecaLimit15 = amountForFloat11 * (1.0F - amountForFloat28);
            if (bonecaLimit15 > 0.0F) {
               bonecaValue21 = ForeShinUtils.getValue2(bonecaValue21, valueForInt6, 0.8F * bonecaLimit15);
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaValue21);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.025F, 12.0F, 5);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue, boolean bonecaAmbalabuFlag) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(bonecaAmbalabuValue), ForeShinUtils.getValueForInt(5137784), 0.4F);
      int valueForInt = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(bonecaAmbalabuValue), ForeShinUtils.getValueForInt(15002868), 0.25F);

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuValue4 = (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float absSin = Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F);
            int bonecaAmbalabuValue6 = ForeShinUtils.getValue2(value2, valueForInt, getAmount(0.22F, 0.3F, absSin));
            int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabuValue6, ForeShinUtils.getValueForInt(5137528), (bonecaAmbalabuFlag ? 0.25F : 0.5F) * getAmount(0.6F, 1.0F, bonecaAmbalabuValue4));
            if (bonecaAmbalabuFlag) {
               float bonecaAmbalabuValue8 = getAmount(0.04F, 0.07F, absSin)
                  * (1.0F - getAmount(0.13F, 0.17F, absSin))
                  * getAmount(0.04F, 0.18F, bonecaAmbalabuValue4)
                  * (1.0F - getAmount(0.6F, 0.88F, bonecaAmbalabuValue4));
               foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(11847898), 0.6F * bonecaAmbalabuValue8);
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, foreShinValue);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 6.0F, 23);
   }

   private static void handleAmounts(LatheLoft latheLoft, int dolfinitaBananitaValue, ShinBrr shinBrr) {
      float[] dolfinitaBananitaValues = getAmounts(dolfinitaBananitaValue);
      float[] amountsForInt = getAmountsForInt(dolfinitaBananitaValue);
      float[] amountsPATHX = getAmountsPATH_X(15.45F);
      float cos = (float)Math.cos(amountsPATHX[2]);
      float sin = -((float)Math.sin(amountsPATHX[2]));
      int dolfinitaBananitaLimit = DORSAL_SAG_OFFSETS.length;
      float dolfinitaBananitaFactor = dolfinitaBananitaValue != 0 && dolfinitaBananitaValue != 3 ? 1.0F : 0.94F;
      float[] floats = new float[3 * dolfinitaBananitaLimit];

      for (int i = 0; i < dolfinitaBananitaLimit; i++) {
         float dolfinitaBananitaFactor2 = DORSAL_LIFT_OFFSETS[i] * dolfinitaBananitaFactor;
         float dolfinitaBananitaFactor3 = DORSAL_SAG_OFFSETS[i] * dolfinitaBananitaFactor;
         floats[3 * i] = amountsForInt[0] + dolfinitaBananitaValues[0] * dolfinitaBananitaFactor2;
         floats[3 * i + 1] = amountsForInt[1] + cos * dolfinitaBananitaFactor3 + dolfinitaBananitaValues[1] * dolfinitaBananitaFactor2;
         floats[3 * i + 2] = amountsForInt[2] + sin * dolfinitaBananitaFactor3 + dolfinitaBananitaValues[2] * dolfinitaBananitaFactor2;
      }

      float[] pointsSpline = LatheLoft.getPointsSpline(2, floats);
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      float[] amounts4 = LatheLoft.getAmounts(latheLoftSize, 3.2F, 2.9F, 2.35F, 1.8F, 1.25F, 0.75F, 0.3F);
      float[] crownForeValues = LatheLoft.getAmounts(latheLoftSize, 0.22F, 0.2F, 0.17F, 0.12F);
      latheLoft.getLoftPath(pointsSpline, amounts4, crownForeValues, 6, shinBrr, null, null, -dolfinitaBananitaValues[0], -dolfinitaBananitaValues[1], -dolfinitaBananitaValues[2]);
   }

   private static float[] getAmountsForInt(int dolfinitaBananitaValue) {
      float[] dolfinitaBananitaValues = getAmounts(dolfinitaBananitaValue);
      float[] amountsPATHX = getAmountsPATH_X(15.45F);
      float dolfinitaBananitaFactor = getAmount(table, 2, 1, 15.45F) * 0.97F;
      return new float[]{dolfinitaBananitaValues[0] * dolfinitaBananitaFactor, amountsPATHX[0] + dolfinitaBananitaValues[1] * dolfinitaBananitaFactor, amountsPATHX[1] + dolfinitaBananitaValues[2] * dolfinitaBananitaFactor};
   }

   private static float[][] getAmounts(float[] dolfinitaBananitaValues, float dolfinitaBananitaValue, float dolfinitaBananitaValue2, float dolfinitaBananitaValue3) {
      int dolfinitaBananitaLimit = dolfinitaBananitaValues.length / 3 - 1;
      float[] floats = new float[3 * (dolfinitaBananitaLimit + 1)];
      float[] floats2 = new float[3 * (dolfinitaBananitaLimit + 1)];
      float[] floats3 = new float[3 * (dolfinitaBananitaLimit + 1)];

      for (int i = 0; i <= dolfinitaBananitaLimit; i++) {
         int max4 = Math.max(0, i - 1);
         int min5 = Math.min(dolfinitaBananitaLimit, i + 1);
         float dolfinitaBananitaFactor = dolfinitaBananitaValues[3 * min5] - dolfinitaBananitaValues[3 * max4];
         float dolfinitaBananitaFactor2 = dolfinitaBananitaValues[3 * min5 + 1] - dolfinitaBananitaValues[3 * max4 + 1];
         float dolfinitaBananitaFactor3 = dolfinitaBananitaValues[3 * min5 + 2] - dolfinitaBananitaValues[3 * max4 + 2];
         float distance = (float)Math.sqrt(dolfinitaBananitaFactor * dolfinitaBananitaFactor + dolfinitaBananitaFactor2 * dolfinitaBananitaFactor2 + dolfinitaBananitaFactor3 * dolfinitaBananitaFactor3);
         if (distance < 1.0E-6F) {
            dolfinitaBananitaFactor = 0.0F;
            dolfinitaBananitaFactor2 = 1.0F;
            dolfinitaBananitaFactor3 = 0.0F;
            distance = 1.0F;
         }

         floats[3 * i] = dolfinitaBananitaFactor / distance;
         floats[3 * i + 1] = dolfinitaBananitaFactor2 / distance;
         floats[3 * i + 2] = dolfinitaBananitaFactor3 / distance;
      }

      float upX2 = dolfinitaBananitaValue;
      float path2 = dolfinitaBananitaValue2;
      float upZ2 = dolfinitaBananitaValue3;

      for (int j = 0; j <= dolfinitaBananitaLimit; j++) {
         float dolfinitaBananitaFactor4 = floats[3 * j];
         float dolfinitaBananitaFactor5 = floats[3 * j + 1];
         float dolfinitaBananitaFactor6 = floats[3 * j + 2];
         float dolfinitaBananitaFactor7 = upX2 * dolfinitaBananitaFactor4 + path2 * dolfinitaBananitaFactor5 + upZ2 * dolfinitaBananitaFactor6;
         float dolfinitaBananitaFactor8 = upX2 - dolfinitaBananitaFactor7 * dolfinitaBananitaFactor4;
         float dolfinitaBananitaFactor9 = path2 - dolfinitaBananitaFactor7 * dolfinitaBananitaFactor5;
         float dolfinitaBananitaFactor10 = upZ2 - dolfinitaBananitaFactor7 * dolfinitaBananitaFactor6;
         float sqrt2 = (float)Math.sqrt(dolfinitaBananitaFactor8 * dolfinitaBananitaFactor8 + dolfinitaBananitaFactor9 * dolfinitaBananitaFactor9 + dolfinitaBananitaFactor10 * dolfinitaBananitaFactor10);
         upX2 = dolfinitaBananitaFactor8 / sqrt2;
         path2 = dolfinitaBananitaFactor9 / sqrt2;
         upZ2 = dolfinitaBananitaFactor10 / sqrt2;
         floats2[3 * j] = upX2;
         floats2[3 * j + 1] = path2;
         floats2[3 * j + 2] = upZ2;
         floats3[3 * j] = dolfinitaBananitaFactor5 * upZ2 - dolfinitaBananitaFactor6 * path2;
         floats3[3 * j + 1] = dolfinitaBananitaFactor6 * upX2 - dolfinitaBananitaFactor4 * upZ2;
         floats3[3 * j + 2] = dolfinitaBananitaFactor4 * path2 - dolfinitaBananitaFactor5 * upX2;
      }

      return new float[][]{floats, floats2, floats3};
   }

   private static float[] getAmounts(int index, float dolfinitaBananitaFactor) {
      float angle = dolfinitaBananitaFactor * (float) (Math.PI / 180.0);
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float[] dolfinitaBananitaValues = dolfinitaBananitaFloats[1];
      float[] dolfinitaBananitaValues2 = dolfinitaBananitaFloats[2];
      float[] floats = new float[6];

      for (int i = 0; i < 3; i++) {
         floats[i] = shinFootTable[3 * index + i] + dolfinitaBananitaValues[3 * index + i] * RING_SEMI_AXES_B[index] * cos + dolfinitaBananitaValues2[3 * index + i] * RING_SEMI_AXES_A[index] * sin;
         floats[3 + i] = dolfinitaBananitaValues[3 * index + i] * cos / RING_SEMI_AXES_B[index] + dolfinitaBananitaValues2[3 * index + i] * sin / RING_SEMI_AXES_A[index];
      }

      float distance = (float)Math.sqrt(floats[3] * floats[3] + floats[4] * floats[4] + floats[5] * floats[5]);
      floats[3] /= distance;
      floats[4] /= distance;
      floats[5] /= distance;
      return floats;
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(9063002);
      int valueForInt2 = ForeShinUtils.getValueForInt(3808806);
      int valueForInt3 = ForeShinUtils.getValueForInt(4149862);
      int valueForInt = ForeShinUtils.getValueForInt(11561592);
      int valueForInt5 = ForeShinUtils.getValueForInt(15002868);

      for (int i = 0; i < bonecaValue; i++) {
         float absSin = (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float abs7 = Math.abs(360.0F * ((j + 0.5F) / ambalabuBonecaValue - 0.5F));
            int value2 = ForeShinUtils.getValue2(valueForInt2, bonecaAmbalabuValue, getAmount(24.0F, 62.0F, abs7));
            int bonecaAmbalabuValue9 = ForeShinUtils.getValue2(
               value2, valueForInt, 0.55F * (1.0F - getAmount(8.0F, 20.0F, abs7)) * getAmount(0.25F, 0.45F, absSin) * (1.0F - getAmount(0.82F, 0.95F, absSin))
            );
            int bonecaAmbalabuValue10 = ForeShinUtils.getValue2(bonecaAmbalabuValue9, valueForInt2, 0.6F * (1.0F - getAmount(0.04F, 0.3F, absSin)));
            float bonecaAmbalabuValue11 = 22.0F * getAmount(0.72F, 0.97F, absSin);
            int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabuValue10, valueForInt5, getAmount(79.0F + bonecaAmbalabuValue11, 84.0F + bonecaAmbalabuValue11, abs7));
            int bonecaAmbalabuValue13 = ForeShinUtils.getValue2(foreShinValue, valueForInt3, getAmountForFloat(Math.abs(abs7 - 71.0F - 0.5F * bonecaAmbalabuValue11), 9.5F + 0.5F * bonecaAmbalabuValue11, 2.5F));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue13);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.025F, 8.0F, 41);
   }
}
