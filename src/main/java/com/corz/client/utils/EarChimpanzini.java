package com.corz.client.utils;

import java.util.Arrays;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.PoseProgress;

public final class EarChimpanzini extends WarmBrainrotBase {
   private ForeEar base;
   private static final float[] shinFootTable = new float[]{42.0F, 136.0F, 226.0F, 316.0F};
   private static final float[] earChimpanziniTable = new float[]{1.0F, 0.94F, 0.97F, 1.03F};
   private ForeEar earL;
   private static final float[] table = new float[]{3.3F, 3.7F, 4.2F, 4.42F, 4.55F, 5.0F};
   private ForeEar classShinFoot;
   private ForeEar chest;
   private ForeEar head;
   private final ForeEar[] shinFootForeEars = new ForeEar[4];
   private static final float[] EAR_RING_HEIGHTS = new float[]{12.5F, 12.35F, 11.0F, 9.3F, 7.6F, 6.3F};
   private ForeEar classShinProgress;
   private ForeEar shinFootForeEar;
   private final ForeEar[] flapTipSections = new ForeEar[4];
   private ForeEar shinProgressForeEar;
   private ForeEar earChimpanziniForeEar;
   private ForeEar foreEar;

   @Override
   public String formatNoteBlock() {
      return "entity.villager.ambient";
   }

   private static float getAmount(int earChimpanziniValue, int earChimpanziniValue2, int bonecaAmbalabuValue) {
      int earChimpanziniValue4 = earChimpanziniValue * 374761393 + earChimpanziniValue2 * 668265263 + bonecaAmbalabuValue * 1442695041;
      int earChimpanziniValue5 = (earChimpanziniValue4 ^ earChimpanziniValue4 >>> 13) * 1274126177;
      int earChimpanziniValue6 = earChimpanziniValue5 ^ earChimpanziniValue5 >>> 16;
      return (earChimpanziniValue6 & 16777215) / 1.6777215E7F;
   }

   @Override
   public int getValue() {
      return 3;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5941306))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4161322), 0.45F, 12.0F, bonecaAmbalabuValue)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8176207), 0.3F, 8.0F, bonecaAmbalabuValue + 5)
         .getBonecaAmbalabu(0.05F, 5.0F, bonecaAmbalabuValue + 9);
      int bonecaAmbalabuLimit = Math.round(bonecaAmbalabu.ambalabuBonecaValue * bonecaAmbalabu.bonecaValue / 8.0F);

      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float bonecaAmbalabuValue2 = getAmount(i, 1, bonecaAmbalabuValue) * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue3 = getAmount(i, 2, bonecaAmbalabuValue) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuFactor = 2.5F + 3.0F * getAmount(i, 3, bonecaAmbalabuValue);
         float bonecaAmbalabuValue4 = (getAmount(i, 4, bonecaAmbalabuValue) - 0.5F) * bonecaAmbalabuFactor * 0.6F;
         float bonecaAmbalabuValue5 = getAmount(i, 5, bonecaAmbalabuValue);
         int valueForIntFloat = bonecaAmbalabuValue5 < 0.45F
            ? ForeShinUtils.getValueForIntFloat(4161322, 0.7F)
            : (bonecaAmbalabuValue5 < 0.8F ? ForeShinUtils.getValueForIntFloat(8176207, 0.55F) : ForeShinUtils.getValueForIntFloat(5018928, 0.8F));

         for (int j = -1; j <= 1; j++) {
            float classFactor = bonecaAmbalabuValue2 + j * bonecaAmbalabu.ambalabuBonecaValue;
            if (!(classFactor + 2.0F < 0.0F) && !(classFactor - bonecaAmbalabuFactor - 2.0F > bonecaAmbalabu.ambalabuBonecaValue)) {
               bonecaAmbalabu.getClassForFloatInt(classFactor, bonecaAmbalabuValue3, classFactor - bonecaAmbalabuFactor * 0.5F, bonecaAmbalabuValue3 + bonecaAmbalabuValue4, classFactor - bonecaAmbalabuFactor, bonecaAmbalabuValue3 + bonecaAmbalabuValue4 * 0.4F, 1.1F, valueForIntFloat);
            }
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4161322), 0.55F, 6.0F, bonecaAmbalabuValue + 13);

      for (int k = 0; k < bonecaAmbalabu.ambalabuBonecaValue; k++) {
         float bonecaAmbalabuValue8 = (k + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float max2 = Math.max(0.0F, 1.0F - Math.abs(bonecaAmbalabuValue8 - 0.75F) / 0.12F);
         if (max2 > 0.0F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(k, 0.0F, k + 1, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(4161322, 0.5F * max2 * max2 * (3.0F - 2.0F * max2)));
         }
      }
   }

   private static void handle(ForeShinScale foreShinScale, ForeEar foreEar, float angle, float angle2) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float cos2 = (float)Math.cos(angle2);
      float sin2 = (float)Math.sin(angle2);
      float foreShinValue = (float)Math.atan2(sin2 * cos, cos2 + (1.0F - cos2) * sin * sin);
      float asin = (float)Math.asin(Math.max(-1.0F, Math.min(1.0F, -(1.0F - cos2) * cos * sin)));
      float angle4 = (float)Math.atan2(sin2 * sin, cos2 + (1.0F - cos2) * cos * cos);
      foreShinScale.getAmounts(foreEar, foreShinValue, asin, angle4);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15984565));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.31F * bonecaAmbalabu.ambalabuBonecaValue, 0.0F, 0.69F * bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(15913786));

      for (int i = 0; i < 6; i++) {
         float classBound = (0.04F + 0.035F * i) * bonecaAmbalabu.ambalabuBonecaValue;
         bonecaAmbalabu.getClass02(classBound, 0.0F, classBound, (float)bonecaAmbalabu.bonecaValue, 0.8F, ForeShinUtils.getValueForIntFloat(14864274, 0.6F));
         bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue - classBound, 0.0F, bonecaAmbalabu.ambalabuBonecaValue - classBound, (float)bonecaAmbalabu.bonecaValue, 0.8F, ForeShinUtils.getValueForIntFloat(14864274, 0.6F));
      }

      bonecaAmbalabu.getClass02(0.31F * bonecaAmbalabu.ambalabuBonecaValue, 0.0F, 0.31F * bonecaAmbalabu.ambalabuBonecaValue, (float)bonecaAmbalabu.bonecaValue, 1.6F, ForeShinUtils.getValueForIntFloat(14267690, 0.9F));
      bonecaAmbalabu.getClass02(0.69F * bonecaAmbalabu.ambalabuBonecaValue, 0.0F, 0.69F * bonecaAmbalabu.ambalabuBonecaValue, (float)bonecaAmbalabu.bonecaValue, 1.6F, ForeShinUtils.getValueForIntFloat(14267690, 0.9F));
      bonecaAmbalabu.getClass02(0.5F * bonecaAmbalabu.ambalabuBonecaValue, 0.0F, 0.5F * bonecaAmbalabu.ambalabuBonecaValue, (float)bonecaAmbalabu.bonecaValue, 1.4F, ForeShinUtils.getValueForIntFloat(14267690, 0.45F));

      for (int j = 0; j < 9; j++) {
         float classValue = (0.35F + 0.3F * getAmount(j, 1, 71)) * bonecaAmbalabu.ambalabuBonecaValue;
         bonecaAmbalabu.getClass02(classValue, getAmount(j, 2, 71) * bonecaAmbalabu.bonecaValue, 0.6F + getAmount(j, 3, 71), 0.3F, j + 3, ForeShinUtils.getValueForIntFloat(8018474, 0.6F));
      }

      for (int k = 0; k < bonecaAmbalabu.bonecaValue; k++) {
         float bonecaAmbalabuLimit = bonecaAmbalabuValue + (bonecaAmbalabuValue2 - bonecaAmbalabuValue) * (k + 0.5F) / bonecaAmbalabu.bonecaValue;
         if (bonecaAmbalabuLimit > 0.68F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, k, bonecaAmbalabu.ambalabuBonecaValue, k + 1, ForeShinUtils.getValueForIntFloat(9071146, Math.min(1.0F, (bonecaAmbalabuLimit - 0.68F) / 0.2F)));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 6.0F, 17);
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.base = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 0.0F, 1.35F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("base", this.base, 0.0F, 9.0F, -0.42F);
      this.chest = latheLoft.getClassForStringForeEar("trunk", this.base, 0.0F, 9.0F, -0.42F);
      this.head = latheLoft.getClassForStringForeEar("chest", this.chest, 0.0F, 12.45F, -0.1F);
      this.earL = latheLoft.getClassForStringForeEar("head", this.head, 0.0F, 16.9F, 0.3F);
      this.foreEar = latheLoft.getClassForStringForeEar("jaw", this.earL, 0.0F, getAmount(17.7F), getAmountForFloat(-0.8F));
      this.classShinProgress = latheLoft.getClassForStringForeEar("eye_r", this.earL, 0.9936001F, getAmount(19.72F), getAmountForFloat(-2.34F));
      this.classShinFoot = latheLoft.getClassForStringForeEar("eye_l", this.earL, -0.9936001F, getAmount(19.72F), getAmountForFloat(-2.34F));
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("ear_r", this.earL, 2.6136F, getAmount(19.75F), getAmountForFloat(0.45F));
      this.earChimpanziniForeEar = latheLoft.getClassForStringForeEar("ear_l", this.earL, -2.6136F, getAmount(19.75F), getAmountForFloat(0.45F));
      String[] strings = new String[]{"fr", "br", "bl", "fl"};

      for (int i = 0; i < 4; i++) {
         float angle = (float)Math.toRadians(shinFootTable[i]);
         float sin = (float)Math.sin(angle);
         float cos = -((float)Math.cos(angle));
         this.shinFootForeEars[i] = latheLoft.getClassForStringForeEar("flap_" + strings[i], this.chest, 3.36F * sin, 12.45F, -0.1F + 3.36F * cos);
         float shinFootValue = 12.45F + (EAR_RING_HEIGHTS[3] - 12.45F) * earChimpanziniTable[i];
         this.flapTipSections[i] = latheLoft.getClassForStringForeEar("flap_" + strings[i] + "_tip", this.shinFootForeEars[i], table[3] * sin, shinFootValue, -0.1F + table[3] * cos);
      }

      latheLoft.getLatheLoft(this.shinFootForeEars[0]);
      float[] pointsSpline = LatheLoft.getPointsSpline(
         2, 0.0F, 0.55F, 1.7F, 0.0F, 1.5F, 1.25F, 0.0F, 3.7F, 0.45F, 0.0F, 6.3F, -0.25F, 0.0F, 9.0F, -0.42F, 0.0F, 12.45F, -0.1F
      );
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      int index = 8;
      float[] shinFootValues = LatheLoft.getAmounts(latheLoftSize, 0.62F, 1.55F, 2.4F, 2.98F, 3.24F, 3.36F);
      float bonecaAmbalabuValue = getAmount(pointsSpline, 0, index) / getAmount(pointsSpline, 0, latheLoftSize - 1);
      int round = Math.round(112.0F * bonecaAmbalabuValue);
      LoftLatheFlag.Region region = latheLoft.getMax(160, round);
      handleBonecaAmbalabu(region.getClassBonecaAmbalabu(), 0.0F, bonecaAmbalabuValue, 3);
      LoftLatheFlag.Region max2 = latheLoft.getMax(160, 112 - round);
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu(), bonecaAmbalabuValue, 1.0F, 5);
      LoftLatheFlag.Region max3 = latheLoft.getMax(64, 56);
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu(), 0.0F, 0.58F);
      LoftLatheFlag.Region max4 = latheLoft.getMax(64, 48);
      handleBonecaAmbalabu(max4.getClassBonecaAmbalabu(), 0.58F, 1.0F);
      LoftLatheFlag.Region max5 = latheLoft.getMax(192, 56);
      handleBonecaAmbalabu(max5.getClassBonecaAmbalabu(), 11, 1.0F, true);
      LoftLatheFlag.Region max6 = latheLoft.getMax(192, 24);
      handleBonecaAmbalabuForBonecaAmbalabu(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(160, 80);
      handleBonecaAmbalabu(max7.getClassBonecaAmbalabu(), 23);
      LoftLatheFlag.Region max8 = latheLoft.getMax(160, 96);
      handle(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(64, 64);
      max9.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(13610630))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11044447), 0.25F, 10.0F, 5)
         .getBonecaAmbalabu(0.05F, 4.0F, 6);
      LoftLatheFlag.Region max10 = latheLoft.getMax(96, 16);
      max10.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(15324828))
         .getBonecaAmbalabu(0.04F, 4.0F, 8)
         .getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(14205829, 0.55F), 6.0F, 1.1F, 0.0F);
      LoftLatheFlag.Region max11 = latheLoft.getMax(32, 16);
      max11.getClassBonecaAmbalabu().getBonecaAmbalabu(ForeShinUtils.getValueForInt(4863008)).getBonecaAmbalabu(0.12F, 4.0F, 9);
      LoftLatheFlag.Region max12 = latheLoft.getMax(32, 48);
      handleBonecaAmbalabu(max12.getClassBonecaAmbalabu(), true, 61);
      LoftLatheFlag.Region max13 = latheLoft.getMax(32, 48);
      handleBonecaAmbalabu(max13.getClassBonecaAmbalabu(), false, 67);
      LoftLatheFlag.Region max14 = latheLoft.getMax(96, 48);
      handleBonecaAmbalabu(max14.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max6);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max8);
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max9);
      ShinBrr classForRegionInt = latheLoft.getClassForRegionInt(max9, 15128000);
      ShinBrr classForRegion12 = latheLoft.getClassForRegion(max10);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max11);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max12);
      ShinBrr classForRegion15 = latheLoft.getClassForRegion(max13);
      ShinBrr classForRegion16 = latheLoft.getClassForRegion(max14);
      ShinBrr classForRegionInt17 = latheLoft.getClassForRegionInt(max5, 12110000);
      float shinFootFactor = pointsSpline[3 * index + 3] - pointsSpline[3 * index - 3];
      float shinFootFactor2 = pointsSpline[3 * index + 4] - pointsSpline[3 * index - 2];
      float shinFootFactor3 = pointsSpline[3 * index + 5] - pointsSpline[3 * index - 1];
      float shinFootFactor4 = 0.15F / (float)Math.sqrt(shinFootFactor * shinFootFactor + shinFootFactor2 * shinFootFactor2 + shinFootFactor3 * shinFootFactor3);
      float[] copyOf = Arrays.copyOf(pointsSpline, 3 * (index + 2));
      copyOf[3 * index] = pointsSpline[3 * index] - shinFootFactor * shinFootFactor4;
      copyOf[3 * index + 1] = pointsSpline[3 * index + 1] - shinFootFactor2 * shinFootFactor4;
      copyOf[3 * index + 2] = pointsSpline[3 * index + 2] - shinFootFactor3 * shinFootFactor4;
      System.arraycopy(pointsSpline, 3 * index, copyOf, 3 * index + 3, 3);
      float[] crownForeValues = Arrays.copyOf(shinFootValues, index + 2);
      crownForeValues[index + 1] = shinFootValues[index];
      float[] floats = new float[3 * (latheLoftSize - index + 1)];
      System.arraycopy(pointsSpline, 3 * index, floats, 0, 3);
      floats[3] = pointsSpline[3 * index] + shinFootFactor * shinFootFactor4;
      floats[4] = pointsSpline[3 * index + 1] + shinFootFactor2 * shinFootFactor4;
      floats[5] = pointsSpline[3 * index + 2] + shinFootFactor3 * shinFootFactor4;
      System.arraycopy(pointsSpline, 3 * index + 3, floats, 6, 3 * (latheLoftSize - index - 1));
      float[] floats2 = new float[latheLoftSize - index + 1];
      floats2[0] = shinFootValues[index];
      System.arraycopy(shinFootValues, index, floats2, 1, latheLoftSize - index);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      latheLoft.getLoftPath(copyOf, crownForeValues, crownForeValues, 10, classForRegion, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.12F, 1.85F).getLatheLoftLatheLoftAmountLoft(-24.0F);
      latheLoft.getLatheLoft(new float[]{0.62F, 0.92F, 0.96F, 0.8F}, new float[]{0.0F, 0.3F, 0.85F, 1.45F}, 10, classForRegion13, classForRegion13, null);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.chest);
      latheLoft.getLoftPath(floats, floats2, floats2, 10, classForRegion3, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoft(0.0F, 9.0F, -0.42F, 2.95F, 8, classForRegion3);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, -0.1F);
      latheLoft.getLatheLoft(new float[]{3.42F, 3.4F, 3.32F}, new float[]{12.45F, 12.95F, 13.2F}, 16, classForRegion12, null, latheLoft.getMax(14270853));
      latheLoft.getDequeArray();

      for (int j = 0; j < 4; j++) {
         this.handleAmounts(latheLoft, j, classForRegion4, classForRegion5);
      }

      latheLoft.getLatheLoftForForeEar(this.head);
      latheLoft.getLoftPath(
         new float[]{
            0.0F,
            19.4F,
            0.6F,
            0.0F,
            18.6F,
            0.6F,
            0.0F,
            18.0F,
            0.55F,
            0.0F,
            17.35F,
            0.45F,
            0.0F,
            16.6F,
            0.3F,
            0.0F,
            16.15F,
            0.2F,
            0.0F,
            15.75F,
            0.12F,
            0.0F,
            14.8F,
            0.0F,
            0.0F,
            13.9F,
            -0.08F,
            0.0F,
            13.2F,
            -0.1F,
            0.0F,
            12.75F,
            -0.1F
         },
         new float[]{1.0F, 1.7F, 2.4F, 3.3F, 4.15F, 4.45F, 4.45F, 4.3F, 4.0F, 3.62F, 2.95F},
         new float[]{0.9F, 1.5F, 2.0F, 2.5F, 3.0F, 3.35F, 3.4F, 3.4F, 3.3F, 3.08F, 2.54F},
         18,
         classForRegion6,
         null,
         null,
         0.0F,
         0.0F,
         -1.0F
      );
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, -0.1F);
      latheLoft.getLatheLoft(new float[]{3.6F, 3.55F, 3.25F}, new float[]{13.05F, 13.6F, 14.3F}, 18, classForRegion7, null, null);
      latheLoft.getDequeArray();

      for (int k = -1; k <= 1; k += 2) {
         handle(latheLoft, k * 3.9F, 15.95F, -1.3F, k * 4.35F, 15.0F, -1.5F, 0.42F, classForRegion6);
         handle(latheLoft, k * 4.3F, 15.8F, 0.1F, k * 4.8F, 14.85F, 0.1F, 0.42F, classForRegionInt17);
         handle(latheLoft, k * 3.9F, 15.7F, 1.5F, k * 4.35F, 14.75F, 1.7F, 0.42F, classForRegion6);
      }

      latheLoft.getLatheLoftForForeEar(this.earL);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 16.9F, 0.3F).getLatheLoft3(1.08F).getLatheLoft(0.0F, -16.9F, -0.3F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 19.6F, 0.4F).getLatheLoft(90.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 2.45F, 2.45F, 2.9F, 18, classForRegion8);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoft(0.0F, 19.65F, -1.15F, 2.1F, 1.5F, 1.6F, 14, classForRegion9);
      float[] latheLoftValues = LatheLoft.getPointsSpline(
         4, -2.05F, 20.35F, -1.2F, -1.05F, 21.0F, -2.25F, 0.0F, 20.8F, -2.42F, 1.05F, 21.0F, -2.25F, 2.05F, 20.35F, -1.2F
      );
      int shinFootValue5 = LatheLoft.getValue(latheLoftValues);
      latheLoft.getLoftPath(
         latheLoftValues,
         LatheLoft.getAmounts(shinFootValue5, 0.16F, 0.5F, 0.55F, 0.42F, 0.55F, 0.5F, 0.16F),
         LatheLoft.getAmounts(shinFootValue5, 0.12F, 0.45F, 0.5F, 0.36F, 0.5F, 0.45F, 0.12F),
         12,
         classForRegion16,
         null,
         null,
         0.0F,
         1.0F,
         0.0F
      );
      latheLoft.getLatheLoft(0.0F, 19.25F, -2.5F, 0.45F, 0.75F, 0.62F, 10, classForRegion10);
      latheLoft.getLoftLathe(0.0F, 18.2F, -2.3F, 1.85F, 1.2F, 1.55F, 16, 0.0F, 0.62F, classForRegion10);
      ShinBrr shinBrr = latheLoft.getMax(4860458);
      latheLoft.getLatheLoft(0.0F, 17.25F, -2.0F, 1.4F, 1.1F, 0.95F, 8, shinBrr);

      for (int m = -1; m <= 1; m += 2) {
         latheLoft.getLatheLoft(m * 1.38F, 17.75F, -2.55F, 0.42F, 0.5F, 0.85F, 8, classForRegion10);
      }

      latheLoft.getLatheLoft(0.0F, 19.05F, -3.3F, 0.62F, 0.38F, 0.36F, 8, classForRegionInt);
      ShinBrr shinBrr2 = latheLoft.getMax(5914672);

      for (int p = -1; p <= 1; p += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(p * 0.3F, 18.88F, -3.7F).getLatheLoftLatheLoftAmountLoft(-35.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.2F, 0.12F, 0.12F, 6, shinBrr2);
         latheLoft.getDequeArray();
      }

      handle(latheLoft, 17.68F, -2.3F, 1.15F, 1.32F, 0.92F, 0.13F, classForRegion14);

      for (int q = -1; q <= 1; q += 2) {
         handle(latheLoft, q * 1.7F, 18.95F, -1.2F, q * 2.3F, 17.75F, -1.55F, 0.38F, classForRegion6);
         handle(latheLoft, q * 1.8F, 18.25F, -0.75F, q * 2.3F, 17.05F, -0.95F, 0.38F, classForRegionInt17);
         handle(latheLoft, q * 1.6F, 17.7F, -0.2F, q * 2.3F, 16.5F, -0.3F, 0.38F, classForRegion6);
      }

      float[][] floats3 = new float[][]{{2.9F, 2.36F, 2.66F}, {2.55F, 2.04F, 2.45F}};

      for (int r = 0; r < 3; r++) {
         float shinFootValue6 = -1.1F + 1.1F * r;
         float[] shinFootValues2 = floats3[Math.abs(r - 1)];
         latheLoft.getLoftPath(
            new float[]{shinFootValue6, 18.55F, shinFootValues2[0], shinFootValue6 * 1.05F, 17.4F, shinFootValues2[1], shinFootValue6 * 1.1F, 16.4F, shinFootValues2[2]},
            new float[]{0.62F, 0.72F, 0.06F},
            new float[]{0.15F, 0.15F, 0.04F},
            4,
            r % 2 == 0 ? classForRegion6 : classForRegionInt17,
            null,
            null,
            shinFootValue6 * 0.45F,
            0.0F,
            1.0F
         );
      }

      ShinBrr shinBrr3 = latheLoft.getMax(15261904);
      ShinBrr shinBrr4 = latheLoft.getMax(1708556);
      ShinBrr shinBrr5 = latheLoft.getMax(16777215);

      for (int t = -1; t <= 1; t += 2) {
         latheLoft.getLatheLoftForForeEar(t > 0 ? this.classShinProgress : this.classShinFoot);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(t * 0.92F, 19.72F, -2.34F).getLatheLoft4(-t * 12.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.62F, 0.56F, 0.32F, 10, shinBrr3);
         latheLoft.getLatheLoft(0.0F, 0.0F, -0.1F, 0.5F, 0.5F, 0.3F, 10, shinBrr4);
         latheLoft.getLatheLoft(0.14F, 0.15F, -0.38F, 0.085F, 6, shinBrr5);
         latheLoft.getDequeArray();
      }

      ShinBrr shinBrr6 = latheLoft.getMax(13610630);
      ShinBrr shinBrr7 = latheLoft.getMax(10649178);

      for (int shinFootTotal = -1; shinFootTotal <= 1; shinFootTotal += 2) {
         latheLoft.getLatheLoftForForeEar(shinFootTotal > 0 ? this.shinProgressForeEar : this.earChimpanziniForeEar);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinFootTotal * 2.62F, 19.75F, 0.5F).getLatheLoft4(shinFootTotal * 55.0F).getLatheLoft(-shinFootTotal * 8.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.45F, 1.35F, 1.15F, 10, shinBrr6);
         latheLoft.getLatheLoft(shinFootTotal * 0.3F, 0.04F, -0.12F, 0.22F, 0.95F, 0.78F, 8, shinBrr7);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.foreEar);
      latheLoft.getLatheLoft(0.0F, 16.9F, -2.05F, 1.5F, 0.82F, 1.4F, 14, classForRegion10);
      latheLoft.getLatheLoft(0.0F, 17.66F, -2.3F, 0.82F, 0.3F, 0.8F, 8, latheLoft.getMax(11554906));
      handle(latheLoft, 17.5F, -2.1F, 1.0F, 0.95F, 0.55F, 0.14F, classForRegion15);
      latheLoft.getDequeArray();
   }

   private static float[] getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2) {
      float max3 = Math.max(-1.0F, Math.min(1.0F, (bonecaAmbalabuValue2 - 19.65F) / 1.5F));
      float angle = (float)Math.acos(max3);
      float max4 = Math.max(0.05F, (float)Math.sin(angle));
      float bonecaAmbalabuFactor = 0.5F - (float)(Math.asin(Math.max(-1.0F, Math.min(1.0F, bonecaAmbalabuValue / (2.1F * max4)))) / (Math.PI * 2));
      return new float[]{bonecaAmbalabuFactor * bonecaAmbalabu.ambalabuBonecaValue, (float)(angle / Math.PI) * bonecaAmbalabu.bonecaValue};
   }

   private void handleAmounts(LatheLoft latheLoft, int index, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float angle = (float)Math.toRadians(shinFootTable[index]);
      float sin = (float)Math.sin(angle);
      float cos = -((float)Math.cos(angle));
      float[] floats = new float[18];

      for (int j = 0; j < 6; j++) {
         floats[3 * j] = table[j] * sin;
         floats[3 * j + 1] = 12.45F + (EAR_RING_HEIGHTS[j] - 12.45F) * (j == 0 ? 1.0F : earChimpanziniTable[index]);
         floats[3 * j + 2] = -0.1F + table[j] * cos;
      }

      float[] pointsSpline = LatheLoft.getPointsSpline(2, floats);
      int earChimpanziniValue = LatheLoft.getValue(pointsSpline);
      float[] earChimpanziniValues = LatheLoft.getAmounts(earChimpanziniValue, 1.6F, 1.9F, 1.8F, 1.55F, 1.2F, 0.6F);
      float[] amounts2 = LatheLoft.getAmounts(earChimpanziniValue, 0.19F, 0.17F, 0.15F, 0.12F, 0.09F);
      byte earChimpanziniValue2 = 6;
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEars[index]);
      latheLoft.getLoftPath(
         Arrays.copyOfRange(pointsSpline, 0, 3 * (earChimpanziniValue2 + 1)),
         Arrays.copyOfRange(earChimpanziniValues, 0, earChimpanziniValue2 + 1),
         Arrays.copyOfRange(amounts2, 0, earChimpanziniValue2 + 1),
         6,
         shinBrr,
         null,
         null,
         0.0F,
         1.0F,
         0.0F
      );
      latheLoft.getLatheLoftForForeEar(this.flapTipSections[index]);
      latheLoft.getLoftPath(
         Arrays.copyOfRange(pointsSpline, 3 * earChimpanziniValue2, 3 * earChimpanziniValue),
         Arrays.copyOfRange(earChimpanziniValues, earChimpanziniValue2, earChimpanziniValue),
         Arrays.copyOfRange(amounts2, earChimpanziniValue2, earChimpanziniValue),
         6,
         shinBrr2,
         null,
         shinBrr2,
         sin,
         0.0F,
         cos
      );
   }

   private static void handle(LatheLoft latheLoft, float earChimpanziniValue, float earChimpanziniValue2, float earChimpanziniDivisor, float earChimpanziniFactor, float earChimpanziniValue3, float latheLoftValues, ShinBrr shinBrr) {
      float[] floats = new float[15];

      for (int i = 0; i < 5; i++) {
         float earChimpanziniValue5 = -earChimpanziniValue3 + 2.0F * earChimpanziniValue3 * i / 4.0F;
         floats[3 * i] = earChimpanziniValue5;
         floats[3 * i + 1] = earChimpanziniValue;
         floats[3 * i + 2] = earChimpanziniValue2 - earChimpanziniFactor * (float)Math.sqrt(Math.max(0.0F, 1.0F - earChimpanziniValue5 / earChimpanziniDivisor * (earChimpanziniValue5 / earChimpanziniDivisor)));
      }

      float[] pointsSpline = LatheLoft.getPointsSpline(2, floats);
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      latheLoft.getLoftPath(pointsSpline, LatheLoft.getAmounts(latheLoftSize, 0.11F), LatheLoft.getAmounts(latheLoftSize, latheLoftValues), 6, shinBrr, shinBrr, shinBrr, 0.0F, 1.0F, 0.0F);
   }

   private static float getAmount(float[] earChimpanziniValues, int earChimpanziniValue, int earChimpanziniLimit) {
      float earChimpanziniTotal = 0.0F;

      for (int i = earChimpanziniValue + 1; i <= earChimpanziniLimit; i++) {
         float earChimpanziniFactor = earChimpanziniValues[3 * i] - earChimpanziniValues[3 * i - 3];
         float earChimpanziniFactor2 = earChimpanziniValues[3 * i + 1] - earChimpanziniValues[3 * i - 2];
         float earChimpanziniFactor3 = earChimpanziniValues[3 * i + 2] - earChimpanziniValues[3 * i - 1];
         earChimpanziniTotal += (float)Math.sqrt(earChimpanziniFactor * earChimpanziniFactor + earChimpanziniFactor2 * earChimpanziniFactor2 + earChimpanziniFactor3 * earChimpanziniFactor3);
      }

      return earChimpanziniTotal;
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float cos = (float)Math.cos(angle);
      float shinProgressValue = Math.max(0.0F, cos) * shinProgressFactor;
      float shinProgressFactor2 = Math.max(0.0F, -cos) * shinProgressFactor;
      float shinProgressFactor3 = shinProgressFactor2 * shinProgressFactor2;
      float shinProgressFactor4 = 1.0F - Math.min(1.0F, shinProgressFactor * 2.0F);
      float shinProgressFactor5 = poseProgress.getAgeCode(64.0F, 0.0F) * shinProgressFactor4;
      float ageCode = poseProgress.getAgeCode(46.0F, 0.7F);
      foreShinScale.getAmountsForeShin2(this.base, 0.0F, 0.22F * shinProgressValue, 0.0F);
      float shinProgressValue2 = 0.08F * shinProgressFactor3 - 0.04F * shinProgressValue;
      foreShinScale.getAmountsForForeEarFloat(this.base, 1.0F + shinProgressValue2, 1.0F - 0.15F * shinProgressFactor3 + 0.1F * shinProgressValue, 1.0F + shinProgressValue2);
      foreShinScale.getAmounts(this.chest, -0.1F * shinProgressFactor + 0.06F * shinProgressFactor * (float)Math.sin(angle), 0.0F, 0.075F * shinProgressFactor5);
      foreShinScale.getAmounts(this.head, -0.14F * shinProgressFactor3 + 0.06F * shinProgressValue, 0.0F, -0.05F * shinProgressFactor5);
      foreShinScale.getAmountsForForeEarFloat(this.head, 1.0F + 0.015F * ageCode, 1.0F + 0.01F * ageCode, 1.0F + 0.015F * ageCode);
      foreShinScale.getAmounts(this.earL, 0.12F * shinProgressFactor3 + 0.07F * shinProgressValue, 0.0F, -0.04F * shinProgressFactor5);
      float shinProgressTotal = 0.22F + 0.22F * shinProgressValue;
      float shinProgressValue3 = (poseProgress.age + (poseProgress.hashCode & 127)) % 110.0F;
      if (shinProgressValue3 < 18.0F && !poseProgress.poseProgressActive) {
         float shinProgressValue4 = Math.abs((float)Math.sin(shinProgressValue3 / 6.0F * Math.PI)) * shinProgressFactor4;
         shinProgressTotal += 0.24F * shinProgressValue4;
         foreShinScale.getAmountsBoneCount(this.earL, 0.08F * shinProgressValue4, 0.0F, 0.0F);
      }

      float shinProgressLimit = (poseProgress.age + (poseProgress.hashCode & 63) * 3.0F) % 90.0F;
      float shinProgressDivisor = shinProgressLimit > 86.0F ? 0.12F : 1.0F;
      foreShinScale.getAmountsForForeEarFloat(this.classShinProgress, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, 0.0F, 0.0F, 0.07F * poseProgress.getAgeCode(53.0F, 0.4F));
      foreShinScale.getAmounts(this.earChimpanziniForeEar, 0.0F, 0.0F, -0.07F * poseProgress.getAgeCode(53.0F, 2.1F));
      float shinProgressValue6 = 0.35F + 0.05F * poseProgress.getAgeCode(37.0F, 1.3F) + 0.22F * shinProgressFactor * Math.max(0.0F, (float)Math.cos(angle - 0.7F)) - 0.08F * shinProgressFactor3;
      float shinProgressFactor6 = 0.3F + 0.06F * poseProgress.getAgeCode(31.0F, 0.5F) + 0.15F * shinProgressFactor * (float)Math.sin(angle - 1.6F) + 0.15F * shinProgressFactor3;
      float sin7 = 0.0F;
      float max8 = 0.0F;
      if (poseProgress.swingProgress > 0.0F) {
         float sin = poseProgress.getSwingProgress();
         foreShinScale.getAmountsBoneCount(this.chest, -0.15F * sin, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.head, -0.1F * sin, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.head, 0.0F, 0.0F, (poseProgress.sneakingPose ? -0.3F : -1.0F) * sin);
         foreShinScale.getAmountsBoneCount(this.earL, 0.1F * sin, 0.0F, 0.0F);
         foreShinScale.getClassForForeEarFloat(this.earL, 1.0F + 0.06F * sin);
         shinProgressTotal += 0.6F * sin + 0.25F * Math.abs((float)Math.sin(sin * 3.0F * Math.PI));
         if (poseProgress.swingProgress > 0.25F) {
            max8 = Math.max(0.0F, Math.min(1.0F, (0.3F - sin) / 0.15F));
         }

         sin7 = sin;
      }

      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsForeShin2(this.head, 0.0F, -3.0F, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.head, 0.68F, 0.82F, 0.72F);
         foreShinScale.getAmountsForForeEarFloat(this.chest, 1.03F, 0.95F, 1.03F);
         foreShinScale.getAmountsBoneCount(this.earL, -0.1F, 0.0F, 0.0F);
         shinProgressFactor6 = 0.35F;
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.shinFootForeEar, 1.3F, 0.0F, 0.0F);
         shinProgressValue6 = 0.75F;
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         shinProgressValue6 = 0.6F + 0.15F * (float)Math.sin(poseProgress.age * 0.9F);
         shinProgressFactor6 = 0.3F + 0.3F * (float)Math.sin(poseProgress.age * 0.9F - 1.2F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.chest, 1.08F, 0.9F, 1.08F);
      }

      float max9 = Math.max(-1.2F, Math.min(1.2F, poseProgress.shinProgressAmount));
      foreShinScale.getAmountsBoneCount(this.head, 0.3F * max9, 0.15F * poseProgress.headYawRadians, 0.0F);
      foreShinScale.getAmountsBoneCount(this.earL, (max9 < 0.0F ? 0.55F : 0.7F) * max9, 0.85F * poseProgress.headYawRadians, 0.0F);
      float min7 = Math.min(0.6F + 0.12F * sin7, shinProgressTotal);
      float shinProgressValue8 = min7 + (0.1F - min7) * max8;
      foreShinScale.getAmounts(this.foreEar, -shinProgressValue8, 0.0F, 0.0F);

      for (int i = 0; i < 4; i++) {
         float min2 = Math.min(0.75F, shinProgressValue6 + 0.03F * (i - 1.5F) + 0.04F * poseProgress.getAgeCode(29.0F, i * 1.7F));
         float shinProgressBound = shinProgressFactor6 + 0.05F * poseProgress.getAgeCode(23.0F, i * 2.3F);
         float max3 = Math.max(-0.1F, Math.min(1.1F - min2, shinProgressBound));
         float degrees = shinFootTable[i];
         if (sin7 > 0.0F && !poseProgress.sneakingPose) {
            if (i == 0) {
               degrees += (-5.0F - degrees) * sin7;
               min2 += (1.2F - min2) * sin7;
               max3 += (0.6F - max3) * sin7;
            } else {
               min2 += 0.2F * sin7;
            }
         }

         float radians = (float)Math.toRadians(degrees);
         if (poseProgress.sneakingPose) {
            boolean shinProgressFlag = i == 0 || i == 3;
            float shinProgressValue9 = shinProgressFlag ? (float)Math.toRadians(i == 0 ? 92.0 : 268.0) : radians;
            handleForForeShin2ForeEar(foreShinScale, this.shinFootForeEars[i], shinProgressValue9, (shinProgressFlag ? -0.05F - 0.6F * sin7 : 0.72F) + 0.03F * poseProgress.getAgeCode(29.0F, i * 1.7F));
            handle(foreShinScale, this.flapTipSections[i], shinProgressValue9, (shinProgressFlag ? 0.3F : -1.6F) * shinProgressFactor6 - 0.05F * poseProgress.getAgeCode(23.0F, i * 2.3F));
         } else {
            handle(foreShinScale, this.shinFootForeEars[i], radians, min2);
            handle(foreShinScale, this.flapTipSections[i], radians, max3);
         }
      }
   }

   @Override
   public String formatTungDin() {
      return "chimpanzini_bananini";
   }

   private static float getAmount(float earChimpanziniValue) {
      return 16.9F + (earChimpanziniValue - 16.9F) * 1.08F;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(14269592);
      int valueForInt2 = ForeShinUtils.getValueForInt(12754552);
      int foreShinValue = ForeShinUtils.getValueForInt(7228986);
      int valueForInt = ForeShinUtils.getValueForInt(13610630);
      int valueForInt5 = ForeShinUtils.getValueForInt(4161322);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue6 = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         int bonecaAmbalabuValue7;
         if (bonecaAmbalabuValue6 < 0.12F) {
            bonecaAmbalabuValue7 = ForeShinUtils.getValue2(foreShinValue, valueForInt2, bonecaAmbalabuValue6 / 0.12F);
         } else if (bonecaAmbalabuValue6 < 0.28F) {
            bonecaAmbalabuValue7 = ForeShinUtils.getValue2(valueForInt2, bonecaAmbalabuValue, (bonecaAmbalabuValue6 - 0.12F) / 0.16F);
         } else if (bonecaAmbalabuValue6 < 0.4F) {
            bonecaAmbalabuValue7 = ForeShinUtils.getValue2(bonecaAmbalabuValue, valueForInt, (bonecaAmbalabuValue6 - 0.28F) / 0.12F);
         } else if (bonecaAmbalabuValue6 < 0.5F) {
            bonecaAmbalabuValue7 = valueForInt;
         } else if (bonecaAmbalabuValue6 < 0.58F) {
            bonecaAmbalabuValue7 = ForeShinUtils.getValue2(valueForInt, valueForInt5, (bonecaAmbalabuValue6 - 0.5F) / 0.08F);
         } else if (bonecaAmbalabuValue6 < 0.85F) {
            bonecaAmbalabuValue7 = valueForInt5;
         } else {
            bonecaAmbalabuValue7 = ForeShinUtils.getValue2(valueForInt5, foreShinValue, (bonecaAmbalabuValue6 - 0.85F) / 0.15F);
         }

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, bonecaAmbalabuValue7);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11044447), 0.25F, 8.0F, 41).getBonecaAmbalabu(0.05F, 4.0F, 42);

      for (int k = 0; k < 70; k++) {
         float classBound = (0.52F + 0.32F * getAmount(k, 1, 43)) * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue9 = getAmount(k, 2, 43) * bonecaAmbalabu.bonecaValue;
         int bonecaAmbalabuValue10 = getAmount(k, 3, 43) < 0.5F ? ForeShinUtils.getValueForIntFloat(5941306, 0.8F) : ForeShinUtils.getValueForIntFloat(8176207, 0.55F);
         bonecaAmbalabu.getClass02(classBound, bonecaAmbalabuValue9, classBound + 3.0F + 3.0F * getAmount(k, 4, 43), bonecaAmbalabuValue9 + (getAmount(k, 5, 43) - 0.5F) * 2.0F, 1.1F, bonecaAmbalabuValue10);
      }

      for (int m = 0; m < 3; m++) {
         float amountForFloat = bonecaAmbalabu.bonecaValue * (0.3F + 0.2F * m);
         bonecaAmbalabu.getClass02(0.2F * bonecaAmbalabu.ambalabuBonecaValue, amountForFloat, 0.32F * bonecaAmbalabu.ambalabuBonecaValue, amountForFloat + 1.0F, 0.8F, ForeShinUtils.getValueForIntFloat(9071186, 0.35F));
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2, int bonecaAmbalabuValue3) {
      int foreShinValue = ForeShinUtils.getValue(ForeShinUtils.getValueForInt(15913786), 1.08F);
      int valueForInt = ForeShinUtils.getValue(ForeShinUtils.getValueForInt(15913786), 0.82F);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float hashRegistryValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue * 5.0F;
         float bonecaAmbalabuValue7 = hashRegistryValue - (float)Math.floor(hashRegistryValue);
         float absScaled = Math.abs(bonecaAmbalabuValue7 - 0.5F) * 2.0F;
         int value2 = ForeShinUtils.getValue2(foreShinValue, valueForInt, (float)Math.pow(absScaled, 1.6F) * 0.85F);

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            float bonecaAmbalabuValue9 = bonecaAmbalabuValue + (bonecaAmbalabuValue2 - bonecaAmbalabuValue) * (j + 0.5F) / bonecaAmbalabu.bonecaValue;
            float base = Math.max(0.0F, 1.0F - bonecaAmbalabuValue9 / 0.22F);
            bonecaAmbalabu.getBonecaAmbalabu(
               i, j, ForeShinUtils.getValue2(value2, ForeShinUtils.getValueForInt(6966306), Math.min(1.0F, 1.25F * (float)Math.pow(base, 1.3F)) * 0.82F)
            );
         }
      }

      for (int k = 0; k <= 5; k++) {
         float classBound = k * bonecaAmbalabu.ambalabuBonecaValue / 5.0F;
         if (k < 5) {
            bonecaAmbalabu.getClass02(classBound + 2.25F, 0.0F, classBound + 2.25F, (float)bonecaAmbalabu.bonecaValue, 2.5F, ForeShinUtils.getValueForIntFloat(14267690, 0.55F));
            bonecaAmbalabu.getClassBonecaAmbalabu(classBound, 0.0F, classBound + 1.5F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(16769658, 0.25F));
         }

         if (k > 0) {
            bonecaAmbalabu.getClass02(classBound - 2.25F, 0.0F, classBound - 2.25F, (float)bonecaAmbalabu.bonecaValue, 2.5F, ForeShinUtils.getValueForIntFloat(14267690, 0.55F));
            bonecaAmbalabu.getClassBonecaAmbalabu(classBound - 1.5F, 0.0F, classBound, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(16769658, 0.25F));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 8.0F, bonecaAmbalabuValue3);
      int bonecaAmbalabuLimit = Math.round(34.0F * (bonecaAmbalabuValue2 - bonecaAmbalabuValue));

      for (int m = 0; m < bonecaAmbalabuLimit; m++) {
         float bonecaAmbalabuFactor = getAmount(m, 2, 51 + bonecaAmbalabuValue3) * bonecaAmbalabu.bonecaValue;
         if (!(bonecaAmbalabuValue + (bonecaAmbalabuValue2 - bonecaAmbalabuValue) * bonecaAmbalabuFactor / bonecaAmbalabu.bonecaValue < 0.12F)) {
            float classValue = getAmount(m, 1, 51 + bonecaAmbalabuValue3) * bonecaAmbalabu.ambalabuBonecaValue;
            bonecaAmbalabu.getClass02(
               classValue,
               bonecaAmbalabuFactor,
               0.6F + 1.1F * getAmount(m, 3, 51 + bonecaAmbalabuValue3),
               0.35F,
               m,
               ForeShinUtils.getValueForIntFloat(8018474, 0.55F + 0.3F * getAmount(m, 4, 51 + bonecaAmbalabuValue3))
            );
         }
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag, int bonecaAmbalabuValue) {
      int valueForInt = ForeShinUtils.getValueForInt(15392968);
      int valueForInt3 = ForeShinUtils.getValueForInt(13483940);
      float[] floats = new float[]{0.17F, 0.33F, 0.5F, 0.67F, 0.83F};
      float[] floats2 = new float[7];
      floats2[6] = 1.0F;

      for (int i = 0; i < 5; i++) {
         floats2[i + 1] = floats[i] + 0.06F * (getAmount(i, 1, bonecaAmbalabuValue) - 0.5F);
      }

      for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
         float bonecaAmbalabuBound = (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue4 = bonecaAmbalabuFlag ? Math.abs(bonecaAmbalabuBound - 0.5F) : Math.min(bonecaAmbalabuBound, 1.0F - bonecaAmbalabuBound);
         float foreShinFactor = Math.max(0.0F, Math.min(1.0F, 1.0F - (bonecaAmbalabuValue4 - 0.06F) / 0.2F));
         int foreShinValue = ForeShinUtils.getValue2(valueForInt, valueForInt3, foreShinFactor);

         for (int k = 0; k < bonecaAmbalabu.bonecaValue; k++) {
            float bonecaAmbalabuLimit = (k + 0.5F) / bonecaAmbalabu.bonecaValue;
            int bonecaAmbalabuCounter = 0;

            while (bonecaAmbalabuCounter < 5 && bonecaAmbalabuLimit > floats2[bonecaAmbalabuCounter + 1]) {
               bonecaAmbalabuCounter++;
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, k, ForeShinUtils.getValue(foreShinValue, 0.96F + 0.08F * getAmount(bonecaAmbalabuCounter, 2, bonecaAmbalabuValue)));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 4.0F, bonecaAmbalabuValue);

      for (int m = 1; m <= 5; m++) {
         float amountForFloat = floats2[m] * bonecaAmbalabu.bonecaValue;
         bonecaAmbalabu.getClass02(0.0F, amountForFloat, (float)bonecaAmbalabu.ambalabuBonecaValue, amountForFloat, 1.0F, ForeShinUtils.getValueForIntFloat(12562586, 0.6F));
      }
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13610630))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11044447), 0.22F, 12.0F, 31)
         .getBonecaAmbalabu(0.05F, 5.0F, 32);

      for (int i = -1; i <= 1; i += 2) {
         float[] bonecaAmbalabu2 = getBonecaAmbalabu(bonecaAmbalabu, i * 0.92F, 19.72F);
         bonecaAmbalabu.getClass022(bonecaAmbalabu2[0], bonecaAmbalabu2[1], 10.0F, 9.0F, ForeShinUtils.getValueForIntFloat(9071186, 0.55F));
         bonecaAmbalabu.getClass022(bonecaAmbalabu2[0], bonecaAmbalabu2[1], 7.0F, 6.5F, ForeShinUtils.getValueForIntFloat(7228986, 0.6F));
      }
   }

   @Override
   public String formatTungDinString() {
      return "Chimpanzini Bananini";
   }

   private static void handle(LatheLoft latheLoft, float earChimpanziniValue, float earChimpanziniValue2, float earChimpanziniValue3, float earChimpanziniValue4, float earChimpanziniValue5, float earChimpanziniValue6, float earChimpanziniValue7, ShinBrr shinBrr) {
      latheLoft.getLatheLoft(new float[]{earChimpanziniValue, earChimpanziniValue2, earChimpanziniValue3, earChimpanziniValue4, earChimpanziniValue5, earChimpanziniValue6}, new float[]{earChimpanziniValue7, 0.04F}, 4, shinBrr, false);
   }

   @Override
   public float getAmountFloat() {
      return 1.8F;
   }

   private static float getAmountForFloat(float earChimpanziniValue) {
      return 0.3F + (earChimpanziniValue - 0.3F) * 1.08F;
   }

   private static void handleForForeShin2ForeEar(ForeShinScale foreShinScale, ForeEar foreEar, float angle, float angle2) {
      float sin = (float)Math.sin(angle);
      float cos = -((float)Math.cos(angle));
      float cos2 = (float)Math.cos(angle);
      float sin2 = (float)Math.sin(angle);
      float cos3 = (float)Math.cos(angle2);
      float sin3 = (float)Math.sin(angle2);
      float earChimpanziniFactor = cos3 + (1.0F - cos3) * cos2 * cos2;
      float earChimpanziniFactor2 = (1.0F - cos3) * cos2 * sin2;
      float earChimpanziniFactor3 = sin3 * sin2;
      float earChimpanziniFactor4 = -sin3 * cos2;
      float earChimpanziniFactor5 = (1.0F - cos3) * cos2 * sin2;
      float earChimpanziniValue = sin3 * cos2;
      float earChimpanziniFactor6 = cos3 + (1.0F - cos3) * sin2 * sin2;
      float earChimpanziniFactor7 = 2.0F * sin * sin - 1.0F;
      float earChimpanziniFactor8 = 2.0F * sin * cos;
      float earChimpanziniFactor9 = 2.0F * cos * cos - 1.0F;
      float x = earChimpanziniFactor * earChimpanziniFactor7 + earChimpanziniFactor2 * earChimpanziniFactor8;
      float y = earChimpanziniFactor3 * earChimpanziniFactor7 + earChimpanziniFactor4 * earChimpanziniFactor8;
      float earChimpanziniValue2 = earChimpanziniFactor5 * earChimpanziniFactor7 + earChimpanziniFactor6 * earChimpanziniFactor8;
      float x2 = earChimpanziniFactor5 * earChimpanziniFactor8 + earChimpanziniFactor6 * earChimpanziniFactor9;
      foreShinScale.getAmounts(foreEar, (float)Math.atan2(-earChimpanziniValue, x2), (float)Math.asin(Math.max(-1.0F, Math.min(1.0F, -earChimpanziniValue2))), (float)Math.atan2(y, x));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue, float bonecaAmbalabuFactor, boolean bonecaAmbalabuFlag) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5941306))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4161322), 0.5F, 14.0F, bonecaAmbalabuValue)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8176207), 0.3F, 9.0F, bonecaAmbalabuValue + 5)
         .getBonecaAmbalabu(0.05F, 5.0F, bonecaAmbalabuValue + 9);
      int bonecaAmbalabuLimit = Math.round(bonecaAmbalabu.ambalabuBonecaValue * bonecaAmbalabu.bonecaValue / 14.0F);

      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float bonecaAmbalabuFactor2 = (6.0F + 5.0F * getAmount(i, 3, bonecaAmbalabuValue)) * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue2 = getAmount(i, 1, bonecaAmbalabuValue) * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue3 = getAmount(i, 2, bonecaAmbalabuValue) * (bonecaAmbalabu.bonecaValue + bonecaAmbalabuFactor2) - bonecaAmbalabuFactor2;
         float bonecaAmbalabuValue4 = (getAmount(i, 4, bonecaAmbalabuValue) - 0.5F) * bonecaAmbalabuFactor2 * 0.7F;
         float bonecaAmbalabuValue5 = getAmount(i, 5, bonecaAmbalabuValue);
         int valueForIntFloat = bonecaAmbalabuValue5 < 0.45F
            ? ForeShinUtils.getValueForIntFloat(4161322, 0.75F)
            : (bonecaAmbalabuValue5 < 0.8F ? ForeShinUtils.getValueForIntFloat(8176207, 0.6F) : ForeShinUtils.getValueForIntFloat(5018928, 0.8F));

         for (int j = -1; j <= 1; j++) {
            float classFactor = bonecaAmbalabuValue2 + j * bonecaAmbalabu.ambalabuBonecaValue;
            if (!(classFactor + bonecaAmbalabuFactor2 < 0.0F) && !(classFactor - bonecaAmbalabuFactor2 > bonecaAmbalabu.ambalabuBonecaValue)) {
               bonecaAmbalabu.getClassForFloatInt(classFactor, bonecaAmbalabuValue3, classFactor + bonecaAmbalabuValue4, bonecaAmbalabuValue3 + bonecaAmbalabuFactor2 * 0.5F, classFactor + bonecaAmbalabuValue4 * 0.4F, bonecaAmbalabuValue3 + bonecaAmbalabuFactor2, 1.7F, valueForIntFloat);
            }
         }
      }

      if (bonecaAmbalabuFlag) {
         for (int k = 0; k < bonecaAmbalabu.bonecaValue; k++) {
            float bonecaAmbalabuLimit2 = (k + 0.5F) / bonecaAmbalabu.bonecaValue;
            float bonecaAmbalabuLimit3 = bonecaAmbalabuLimit2 < 0.2F ? 1.0F - bonecaAmbalabuLimit2 / 0.2F : (bonecaAmbalabuLimit2 > 0.75F ? (bonecaAmbalabuLimit2 - 0.75F) / 0.25F : 0.0F);
            if (bonecaAmbalabuLimit3 > 0.0F) {
               bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, k, bonecaAmbalabu.ambalabuBonecaValue, k + 1, ForeShinUtils.getValueForIntFloat(4161322, 0.35F * bonecaAmbalabuLimit3));
            }
         }
      }
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      handleBonecaAmbalabu(bonecaAmbalabu, 13, 0.8F, false);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuLimit = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         if (bonecaAmbalabuLimit > 0.45F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(4161322, 0.35F * (bonecaAmbalabuLimit - 0.45F) / 0.55F));
         }
      }

      byte index = 26;
      float[] floats = new float[index + 1];
      float[] floats2 = new float[index];

      for (int j = 0; j < index; j++) {
         floats[j] = 3.0F + 2.0F * getAmount(j, 1, 83);
         floats2[j] = 0.3F * (getAmount(j, 2, 83) - 0.5F);
      }

      floats[index] = floats[0];
      float bonecaAmbalabuDivisor = (float)bonecaAmbalabu.ambalabuBonecaValue / index;

      for (int k = 0; k < bonecaAmbalabu.ambalabuBonecaValue; k++) {
         float bonecaAmbalabuValue = (k + 0.5F) / bonecaAmbalabuDivisor;
         int min2 = Math.min(index - 1, (int)Math.floor(bonecaAmbalabuValue));
         float deltaVar = bonecaAmbalabuValue - min2;
         float bonecaAmbalabuLimit2 = 0.5F + floats2[min2];
         float bonecaAmbalabuValue2 = deltaVar < bonecaAmbalabuLimit2 ? floats[min2] * (1.0F - deltaVar / bonecaAmbalabuLimit2) : floats[min2 + 1] * (deltaVar - bonecaAmbalabuLimit2) / (1.0F - bonecaAmbalabuLimit2);
         float bonecaAmbalabuValue3 = bonecaAmbalabu.bonecaValue - bonecaAmbalabuValue2;

         for (int m = 0; m < bonecaAmbalabu.bonecaValue; m++) {
            if (m + 0.5F > bonecaAmbalabuValue3) {
               bonecaAmbalabu.getBonecaAmbalabu(k, m, 0);
            }
         }
      }
   }
}
