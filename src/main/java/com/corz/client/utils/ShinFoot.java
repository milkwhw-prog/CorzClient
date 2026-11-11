package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class ShinFoot extends WarmBrainrotBase {
   private ForeEar shinR;
   private static final float cosSinAmount = (float)Math.cos(Math.toRadians(-15.0));
   private static final float shinFootAmount = (float)Math.sin(Math.toRadians(-15.0));
   private ForeEar classShinFoot;
   private static final float[] breathTable = new float[]{0.0F, 0.85F, 1.1F, 0.0F, 1.0F, -0.3F, 0.0F, 0.9F, -2.3F, 0.0F, 0.8F, -4.2F, 0.0F, 0.27F, -5.4F};
   private ForeEar cab;
   private static final float[] shinFootTable = new float[]{1.55F, 1.8F, 1.45F, 1.25F, 1.35F};
   private ForeEar head;
   private ForeEar classBreath;
   private ForeEar classShinProgress;
   private ForeEar shinFootForeEar;
   private static final float[] table = new float[]{1.75F, 2.0F, 1.75F, 1.5F, 1.25F};
   private static final float[] PATH_HEIGHTS = new float[]{
      0.0F, 25.2F, -2.2F, 0.0F, 28.0F, -2.4F, 0.0F, 30.4F, -3.6F, 0.0F, 32.6F, -5.6F, 0.0F, 34.6F, -6.4F, 0.0F, 37.0F, -5.9F
   };
   private ForeEar door;
   private static final float[] PATH_RADII_X = new float[]{2.1F, 1.6F, 1.35F, 1.2F, 1.12F, 1.0F};
   private ForeEar neckTop;
   private ForeEar breath;
   private static final float[] PATH_RADII_Y = new float[]{2.5F, 2.1F, 1.75F, 1.5F, 1.38F, 1.15F};
   private static final float[] RIM_RADII_X = new float[]{0.3F, 0.5F, 0.45F, 0.2F};
   private static final float[] RIM_RADII_Y = new float[]{0.3F, 0.48F, 0.42F, 0.18F};
   private ForeEar classBonecaAmbalabu;
   private ForeEar shinProgressForeEar;
   private ForeEar footR;
   private ForeEar bonecaAmbalabuForeEar;
   private static final float[] NECK_PROFILE_RADII = new float[]{1.56F, 1.45F, 1.42F, 1.5F};
   private ForeEar shinL;
   private ForeEar footL;
   private static final float[] NECK_PROFILE_HEIGHTS = new float[]{0.9F, 2.8F, 3.9F, 4.2F};

   private static float[][] getAmounts(float[] shinFootValues, float[] latheLoftValues, float[] shinFootValues3, int shinFootValue, float shinFootFactor, int shinFootLimit, float shinFootFactor2, int shinFootLimit2) {
      float[] pointsSpline = LatheLoft.getPointsSpline(shinFootValue, shinFootValues);
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      float[] amounts4 = LatheLoft.getAmounts(latheLoftSize, latheLoftValues);
      float[] amounts5 = LatheLoft.getAmounts(latheLoftSize, shinFootValues3);
      int shinFootSize = latheLoftSize + shinFootLimit + shinFootLimit2;
      float[] floats = new float[3 * shinFootSize];
      float[] floats2 = new float[shinFootSize];
      float[] floats3 = new float[shinFootSize];
      int index = 0;
      if (shinFootLimit > 0) {
         float[] amounts6 = getAmounts(pointsSpline, 1, 0);

         for (int i = shinFootLimit; i >= 1; i--) {
            float angle = (float)((Math.PI / 2) * i / shinFootLimit);
            float shinFootFactor3 = shinFootFactor * (float)Math.sin(angle);
            float max4 = Math.max(0.0F, (float)Math.cos(angle));

            for (int j = 0; j < 3; j++) {
               floats[3 * index + j] = pointsSpline[j] + amounts6[j] * shinFootFactor3;
            }

            floats2[index] = amounts4[0] * max4;
            floats3[index] = amounts5[0] * max4;
            index++;
         }
      }

      for (int k = 0; k < latheLoftSize; k++) {
         for (int m = 0; m < 3; m++) {
            floats[3 * index + m] = pointsSpline[3 * k + m];
         }

         floats2[index] = amounts4[k];
         floats3[index] = amounts5[k];
         index++;
      }

      if (shinFootLimit2 > 0) {
         float[] amounts7 = getAmounts(pointsSpline, latheLoftSize - 2, latheLoftSize - 1);

         for (int p = 1; p <= shinFootLimit2; p++) {
            float angle2 = (float)((Math.PI / 2) * p / shinFootLimit2);
            float shinFootFactor5 = shinFootFactor2 * (float)Math.sin(angle2);
            float max6 = Math.max(0.0F, (float)Math.cos(angle2));

            for (int q = 0; q < 3; q++) {
               floats[3 * index + q] = pointsSpline[3 * (latheLoftSize - 1) + q] + amounts7[q] * shinFootFactor5;
            }

            floats2[index] = amounts4[latheLoftSize - 1] * max6;
            floats3[index] = amounts5[latheLoftSize - 1] * max6;
            index++;
         }
      }

      return new float[][]{floats, floats2, floats3};
   }

   private static void handle(LatheLoft latheLoft, float shinFootValue, float hypot, float wrapDegreesBound, float hashHuntBound, ShinBrr shinBrr, boolean shinFootFlag) {
      for (int i = 0; i < 4; i++) {
         float shinFootValue5 = -0.45F + 0.3F * i;
         latheLoft.getLatheLoft(shinFootValue5 - 0.11F, shinFootValue, wrapDegreesBound, shinFootValue5 + 0.11F, hypot, hashHuntBound, shinBrr, null, null, null, shinFootFlag ? shinBrr : null, shinFootFlag ? null : shinBrr);
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16250868), ForeShinUtils.getValueForInt(15132643)).getBonecaAmbalabu(0.008F, 6.0F, 21);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float foreShinFactor = (float)Math.exp(-Math.pow((bonecaAmbalabuValue - 0.3F) / 0.09F, 2.0)) * 0.7F;

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(i, j), -1, foreShinFactor));
         }
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue - 2.5F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(12171958, 0.5F));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16777214), ForeShinUtils.getValueForInt(15658730)).getBonecaAmbalabu(0.008F, 8.0F, 5);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float foreShinFactor = (float)Math.exp(-Math.pow((bonecaAmbalabuValue - 0.3F) / 0.35F, 2.0)) * 0.25F;

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(i, j), -1, foreShinFactor));
         }
      }

      int valueForIntFloat = ForeShinUtils.getValueForIntFloat(10856877, 0.9F);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, 1.4F, valueForIntFloat)
         .getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue - 1.4F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, valueForIntFloat)
         .getClassBonecaAmbalabu(0.0F, 0.0F, 1.4F, bonecaAmbalabu.bonecaValue, valueForIntFloat)
         .getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue - 1.4F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, valueForIntFloat);
      float bonecaAmbalabuValue4 = bonecaAmbalabu.ambalabuBonecaValue * 0.951F;
      bonecaAmbalabu.getClass02(bonecaAmbalabuValue4 + 1.5F, 3.0F, bonecaAmbalabuValue4 + 1.5F, bonecaAmbalabu.bonecaValue - 3.0F, 2.5F, ForeShinUtils.getValueForIntFloat(10132634, 0.18F));
      if (bonecaAmbalabuFlag) {
         bonecaAmbalabu.getClassBonecaAmbalabu(6.0F, 5.0F, 20.0F, 9.0F, ForeShinUtils.getValueForInt(9278102));
         bonecaAmbalabu.getClassBonecaAmbalabu(7.0F, 5.6F, 19.0F, 6.3F, ForeShinUtils.getValueForIntFloat(14014940, 0.8F));

         for (int k = 0; k < 4; k++) {
            bonecaAmbalabu.getClassBonecaAmbalabu(8.0F + k * 3.0F, 7.0F, 9.8F + k * 3.0F, 8.2F, ForeShinUtils.getValueForIntFloat(15593456, 0.85F));
         }
      }
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float exp = (float)Math.exp(-Math.pow((bonecaAmbalabuValue - 0.4F) / 0.16F, 2.0));
         int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValue(ForeShinUtils.getValueForInt(12172996), 0.62F), ForeShinUtils.getValueForInt(14541285), exp);

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, value2);
         }
      }
   }

   private void handleBonecaAmbalabu(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      LoftLatheFlag.Region region = latheLoft.getMax(48, 60);
      BonecaAmbalabu classBonecaAmbalabu2 = region.getClassBonecaAmbalabu();
      classBonecaAmbalabu2.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16185593), ForeShinUtils.getValueForInt(15002092));
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(14015197);
      classBonecaAmbalabu2.getClassBonecaAmbalabu(2.0F, 2.0F, classBonecaAmbalabu2.ambalabuBonecaValue - 2.0F, 3.2F, bonecaAmbalabuValue)
         .getClassBonecaAmbalabu(2.0F, classBonecaAmbalabu2.bonecaValue - 3.2F, classBonecaAmbalabu2.ambalabuBonecaValue - 2.0F, classBonecaAmbalabu2.bonecaValue - 2.0F, bonecaAmbalabuValue);
      classBonecaAmbalabu2.getClassBonecaAmbalabu(2.0F, 2.0F, 3.2F, classBonecaAmbalabu2.bonecaValue - 2.0F, bonecaAmbalabuValue)
         .getClassBonecaAmbalabu(classBonecaAmbalabu2.ambalabuBonecaValue - 3.2F, 2.0F, classBonecaAmbalabu2.ambalabuBonecaValue - 2.0F, classBonecaAmbalabu2.bonecaValue - 2.0F, bonecaAmbalabuValue);
      ShinBrr max4 = latheLoft.getMax(15724523);
      ShinBrr max5 = latheLoft.getMax(10856877);
      ShinBrr max6 = latheLoft.getMax(15331828);
      ShinBrr max7 = latheLoft.getMax(13227740);
      latheLoft.getLatheLoftForForeEar(this.bonecaAmbalabuForeEar);
      latheLoft.getLoftLathe(
         -5.95F, 9.56F, -5.35F, 5.95F, 21.37F, -4.75F, 0.35F, 2, new ShinBrr[]{shinBrr, latheLoft.getClassForRegion(region), max4, max4, max4, max4}, max4
      );
      latheLoft.getLatheLoft(-5.75F, 9.65F, -4.8F, 5.75F, 21.300001F, -4.6F, null, null, max5, max5, max5, max5);

      for (float wrapDegreesBound : new float[]{11.0F, 16.0F}) {
         latheLoft.getLatheLoft(-4.7F, wrapDegreesBound, -4.75F, 4.7F, wrapDegreesBound + 1.1F, -3.9F, null, max6, max6, max6, max7, null);
      }

      handle(latheLoft, 13.4F, 20.5F, shinBrr2, shinBrr3);
   }

   private static float getAmount(float shinFootValue, float shinFootValue2) {
      float shinFootBound = (shinFootValue - 31.2F) / (shinFootValue2 - 31.2F);
      float shinFootFactor = Math.max(0.0F, Math.min(1.0F, shinFootBound));
      return 1.0F - 0.2F * shinFootFactor * shinFootFactor * shinFootFactor;
   }

   private static float getAmountForFloat(float angle, float angle2) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float cos2 = (float)Math.cos(angle2);
      float sin2 = (float)Math.sin(angle2);
      return 7.2F - (2.7F * cos - 0.1F * sin + 4.5F * cos2 + 0.25F * sin2);
   }

   @Override
   public String formatNoteBlock() {
      return "entity.camel.ambient";
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuLimit, float bonecaAmbalabuFactor, float bonecaAmbalabuValue, int foreShinValue, float bonecaAmbalabuValue3, int bonecaAmbalabuValue4, float bonecaAmbalabuFactor2, float bonecaAmbalabuValue5, float bonecaAmbalabuValue6) {
      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float classBound = (bonecaAmbalabuValue5 + (bonecaAmbalabuValue6 - bonecaAmbalabuValue5) * getAmount(i, bonecaAmbalabuValue4)) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(i, bonecaAmbalabuValue4 + 1) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuFactor3 = bonecaAmbalabuFactor * (0.6F + 0.8F * getAmount(i, bonecaAmbalabuValue4 + 2));
         float bonecaAmbalabuValue9 = (getAmount(i, bonecaAmbalabuValue4 + 3) - 0.5F) * bonecaAmbalabuFactor2 * bonecaAmbalabuFactor3;
         bonecaAmbalabu.getClass02(classBound, amountForFloat, classBound + bonecaAmbalabuValue9, amountForFloat + bonecaAmbalabuFactor3, bonecaAmbalabuValue, ForeShinUtils.getValueForIntFloat(foreShinValue, bonecaAmbalabuValue3));
      }
   }

   private static float getAmount(float shinFootFactor) {
      shinFootFactor = shinFootFactor < 0.0F ? 0.0F : (shinFootFactor > 1.0F ? 1.0F : shinFootFactor);
      return shinFootFactor * shinFootFactor * (3.0F - 2.0F * shinFootFactor);
   }

   private void handleBreathAmounts(LatheLoft latheLoft) {
      float[][] floats = getAmounts(breathTable, shinFootTable, table, 2, 1.0F, 2, 0.95F, 2);
      LoftLatheFlag.Region region = latheLoft.getMax(184, 128);
      handleBonecaAmbalabu(region.getClassBonecaAmbalabu(), floats[0]);
      LoftLatheFlag.Region max2 = latheLoft.getMax(24, 24);
      max2.getClassBonecaAmbalabu().getBonecaAmbalabu(ForeShinUtils.getValueForInt(12884588), ForeShinUtils.getValueForInt(11831902)).getBonecaAmbalabu(0.05F, 6.0F, 141);
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr shinBrr = latheLoft.getMax(11831902);
      ShinBrr shinBrr2 = latheLoft.getMax(10648407);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max2);
      ShinBrr max3 = latheLoft.getMax(9069893);
      ShinBrr max4 = latheLoft.getMax(3809050);
      ShinBrr max5 = latheLoft.getMax(14536867);
      latheLoft.getLatheLoftForForeEar(this.breath);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 35.5F, -6.2F).getLatheLoftLatheLoftAmountLoft(-15.0F).getLatheLoft3(1.15F);
      latheLoft.getLoftPath(floats[0], floats[1], floats[2], 16, classForRegion, null, null, 0.0F, 1.0F, 0.0F);

      for (int i = -1; i <= 1; i += 2) {
         latheLoft.getLatheLoft(i * 0.55F, -0.75F, -5.35F, 0.7F, 0.6F, 0.75F, 8, shinBrr);
      }

      latheLoft.getLatheLoft(-0.06F, -1.1F, -5.85F, 0.06F, -0.45F, -5.5F, max4, null, max4, max4, null, null);
      latheLoft.getLatheLoft(-0.42F, -0.95F, -4.8F, 0.42F, -0.6F, -3.4F, max4, null, max4, max4, null, max4);
      handle(latheLoft, -0.78F, -0.55F, -4.6F, -4.4F, max5, false);

      for (int j = -1; j <= 1; j += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(j * 1.15F, 2.0F, 0.9F).getLatheLoft4(-j * 25.0F).getLatheLoft(-j * 25.0F).getLatheLoftLatheLoftAmountLoft(-30.0F);
         latheLoft.getLatheLoft(0.0F, 0.55F, 0.0F, 0.5F, 0.75F, 0.22F, 8, classForRegion3);
         latheLoft.getLoftLathe(0.0F, 0.6F, -0.23F, 0.3F, 0.48F, 6, max3);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.classBreath);
      float[][] amounts2 = getAmounts(
         new float[]{0.0F, -0.42F, -1.5F, 0.0F, -0.58F, -3.5F, 0.0F, -0.74F, -5.0F},
         new float[]{0.95F, 0.86F, 0.74F},
         new float[]{0.58F, 0.52F, 0.45F},
         1,
         0.5F,
         1,
         0.35F,
         1
      );
      LoftLatheFlag.Region region3 = latheLoft.getMax(40, 48);
      region3.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(14071694), ForeShinUtils.getValueForInt(11831902))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(13215098), 0.3F, 5.0F, 131)
         .getBonecaAmbalabu(0.04F, 8.0F, 133);
      latheLoft.getLoftPath(amounts2[0], amounts2[1], amounts2[2], 10, latheLoft.getClassForRegion(region3), null, null, 0.0F, 1.0F, 0.0F);
      latheLoft.getLatheLoft(0.0F, -0.12F, -3.6F, 0.6F, 0.15F, 1.3F, 8, max4);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, -1.3F, -4.75F).getLatheLoftLatheLoftAmountLoft(-28.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.9F, 0.42F, 0.75F, 8, shinBrr2);
      latheLoft.getDequeArray();
      handle(latheLoft, -0.36F, -0.08F, -4.95F, -4.75F, max5, true);
      LoftLatheFlag.Region region4 = latheLoft.getMax(24, 8);
      BonecaAmbalabu classBonecaAmbalabu4 = region4.getClassBonecaAmbalabu();

      for (int k = 0; k < classBonecaAmbalabu4.ambalabuBonecaValue; k++) {
         float absScaled = Math.abs((k + 0.5F) / classBonecaAmbalabu4.ambalabuBonecaValue - 0.5F) * 2.0F;
         int breathValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15328214), ForeShinUtils.getValueForInt(12094558), getAmount((absScaled - 0.46F) / 0.1F));

         for (int m = 0; m < classBonecaAmbalabu4.bonecaValue; m++) {
            classBonecaAmbalabu4.getBonecaAmbalabu(k, m, breathValue);
         }
      }

      ShinBrr classForRegion5 = latheLoft.getClassForRegion(region4);
      ShinBrr max6 = latheLoft.getMax(3810326);
      ShinBrr max7 = latheLoft.getMax(854279);
      ShinBrr max8 = latheLoft.getMax(16777215);
      ShinBrr max9 = latheLoft.getMax(12094558);
      ShinBrr max10 = latheLoft.getMax(2825490);
      ShinBrr max11 = latheLoft.getMax(11108435);

      for (int p = -1; p <= 1; p += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(p * 1.5F, 1.68F, -0.7F).getLatheLoft4(-p * 38.0F).getLatheLoftLatheLoftAmountLoft(12.0F).getLatheLoft(-p * 10.0F);
         latheLoft.getLatheLoftForForeEar(p > 0 ? this.shinFootForeEar : this.classShinProgress);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.2F, 0.85F, 0.55F, 8, classForRegion5);
         latheLoft.getLatheLoft(0.0F, -0.02F, -0.18F, 0.98F, 0.8F, 0.4F, 8, max6);
         latheLoft.getLoftLathe(0.0F, -0.08F, -0.6F, 0.5F, 0.3F, 8, max7);
         latheLoft.getLoftLathe(0.3F * p, -0.2F, -0.63F, 0.11F, 0.11F, 6, max8);
         latheLoft.getLatheLoftForForeEar(this.breath);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(-p * 22.0F);
         latheLoft.getLatheLoft(0.0F, 0.62F, -0.15F, 0.85F, 0.24F, 0.5F, 8, max11);
         latheLoft.getDequeArray();
         latheLoft.getDequeArrayArrayDeque().getLatheLoftLatheLoftAmountLoft(9.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 1.28F, 0.95F, 0.72F, 10, 0.0F, 0.5F, max9);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.3F, 0.12F, 0.74F, 8, max10);

         for (int q = 0; q < 5; q++) {
            float angle = (float)Math.toRadians(50.0F + q * 20.0F);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float breathValue2 = 1.24F * cos;
            float breathValue3 = -0.7F * sin;
            float breathFactor = 0.2F + 0.4F * sin * sin;
            float breathFactor2 = -1.24F * sin;
            float breathFactor3 = -0.7F * cos;
            float breathFactor4 = 0.14F / (float)Math.sqrt(breathFactor2 * breathFactor2 + breathFactor3 * breathFactor3);
            latheLoft.getLatheLoft(
               breathValue2 - breathFactor2 * breathFactor4,
               0.0F,
               breathValue3 - breathFactor3 * breathFactor4,
               breathValue2 + breathFactor2 * breathFactor4,
               0.0F,
               breathValue3 + breathFactor3 * breathFactor4,
               breathValue2 + cos * 0.2F * breathFactor,
               -0.85F * breathFactor,
               breathValue3 - sin * 0.25F * breathFactor,
               max10
            );
         }

         latheLoft.getDequeArray();
         latheLoft.getDequeArray();
      }

      ShinBrr[] shinBrrs = new ShinBrr[]{latheLoft.getMax(14676991), latheLoft.getMax(16777215)};
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, -0.9F, -5.9F).getLatheLoft3(0.6F).getLatheLoft(0.0F, 0.9F, 5.9F);
      float breathTotal = -6.2F;

      for (int r = 0; r < 7; r++) {
         float loftLatheFactor = 0.28F + 0.09F * r;
         float latheLoftValue = (float)Math.sin(r * 2.4F) * 0.16F * r;
         float eyeValue = -0.6F + 0.25F * r + (float)Math.cos(r * 1.7F) * 0.3F;
         latheLoft.getLatheLoft(latheLoftValue, eyeValue, breathTotal, loftLatheFactor, 6, shinBrrs[r % 2]);
         if (r >= 3) {
            latheLoft.getLatheLoft(
               latheLoftValue - (r % 2 == 0 ? 0.55F : -0.55F) * loftLatheFactor, eyeValue + 0.45F * loftLatheFactor, breathTotal + 0.25F * loftLatheFactor, 0.62F * loftLatheFactor, 6, shinBrrs[(r + 1) % 2]
            );
         }

         breathTotal -= 1.45F * loftLatheFactor;
      }

      for (int t = 0; t < 3; t++) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft((t - 1) * 1.1F, 1.3F + (t == 1 ? 0.6F : -0.1F * t), breathTotal - 0.6F - (t == 1 ? 0.5F : 0.0F));

         for (int breathCounter = 0; breathCounter < 3; breathCounter++) {
            latheLoft.getDequeArrayArrayDeque().getLatheLoft(breathCounter * 60.0F);
            latheLoft.getLatheLoft(-0.55F, -0.06F, -0.06F, 0.55F, 0.06F, 0.06F, shinBrrs[1], shinBrrs[1], null, null, shinBrrs[1], shinBrrs[1]);
            latheLoft.getDequeArray();
         }

         latheLoft.getDequeArray();
      }

      latheLoft.getDequeArray();
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float[] bonecaAmbalabuValues) {
      float[] amounts2 = getAmounts(bonecaAmbalabuValues);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float absScaled = Math.abs(bonecaAmbalabuValue - 0.5F) * 2.0F;

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            float bonecaAmbalabuValue2 = (j + 0.5F) / bonecaAmbalabu.bonecaValue;
            int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12884588), ForeShinUtils.getValueForInt(14071694), getAmount((absScaled - 0.62F) / 0.3F));
            int bonecaAmbalabuValue4 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(11831902), getAmount((bonecaAmbalabuValue2 - 0.62F) / 0.25F) * 0.9F);
            bonecaAmbalabu.getBonecaAmbalabu(i, j, bonecaAmbalabuValue4);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11963229), 0.3F, 7.0F, 87).getBonecaAmbalabu(0.03F, 14.0F, 81);
      handleBonecaAmbalabu(bonecaAmbalabu, 520, 6.0F, 1.4F, 9332544, 0.11F, 83, 0.35F, 0.0F, 1.0F);
      handleBonecaAmbalabu(bonecaAmbalabu, 380, 6.0F, 1.3F, 14861982, 0.12F, 85, 0.35F, 0.0F, 1.0F);
      float bonecaAmbalabuFactor = getAmount(bonecaAmbalabuValues, amounts2, -2.7F);
      float bonecaAmbalabuFactor2 = getAmount(bonecaAmbalabuValues, amounts2, -5.3F);
      bonecaAmbalabu.getClass02(
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor2,
         7.0F,
         ForeShinUtils.getValueForIntFloat(14269588, 0.18F)
      );
      bonecaAmbalabu.getClass02(
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor2,
         3.0F,
         ForeShinUtils.getValueForIntFloat(14928291, 0.14F)
      );
      float bonecaAmbalabuValue5 = getAmount(bonecaAmbalabuValues, amounts2, 1.9F);
      float bonecaAmbalabuValue6 = getAmount(bonecaAmbalabuValues, amounts2, -1.0F);
      bonecaAmbalabu.getClass022(
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue5 + bonecaAmbalabuValue6) * 0.5F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.09F,
         bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue6 - bonecaAmbalabuValue5) * 0.55F,
         ForeShinUtils.getValueForIntFloat(11108435, 0.45F)
      );

      for (int k = 0; k < 160; k++) {
         float bonecaAmbalabuValue7 = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + (getAmount(k, 91) - 0.5F) * 0.3F);
         float bonecaAmbalabuValue8 = bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue5 + (bonecaAmbalabuValue6 - bonecaAmbalabuValue5) * getAmount(k, 92));
         float bonecaAmbalabuValue9 = 4.0F + 6.0F * getAmount(k, 93);
         float bonecaAmbalabuValue10 = (getAmount(k, 94) - 0.5F) * 6.0F;
         int valueForIntFloat = k % 3 == 0 ? ForeShinUtils.getValueForIntFloat(13609079, 0.4F) : ForeShinUtils.getValueForIntFloat(8280117, 0.42F);
         bonecaAmbalabu.getClassForFloatInt(bonecaAmbalabuValue7, bonecaAmbalabuValue8, bonecaAmbalabuValue7 + bonecaAmbalabuValue10 * 0.2F, bonecaAmbalabuValue8 + bonecaAmbalabuValue9 * 0.5F, bonecaAmbalabuValue7 + bonecaAmbalabuValue10, bonecaAmbalabuValue8 + bonecaAmbalabuValue9, 1.6F, valueForIntFloat);
      }

      float bonecaAmbalabuFactor3 = getAmount(bonecaAmbalabuValues, amounts2, -0.7F);
      float bonecaAmbalabuFactor4 = (float)Math.toDegrees(Math.asin(0.85714287F)) / 360.0F;

      for (int m = -1; m <= 1; m += 2) {
         float bonecaAmbalabuValue12 = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + m * bonecaAmbalabuFactor4);
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue12, bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor3, 15.0F, 11.0F, ForeShinUtils.getValueForIntFloat(8017460, 0.18F));
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue12, bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor3, 10.0F, 7.5F, ForeShinUtils.getValueForIntFloat(6965806, 0.3F));
         float bonecaAmbalabuValue13 = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + m * (bonecaAmbalabuFactor4 - 0.06F));

         for (int p = 0; p < 26; p++) {
            float bonecaAmbalabuValue14 = bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor3 - 9.0F + 18.0F * getAmount(p, 95 + m);
            float classBound = bonecaAmbalabuValue13 + (getAmount(p, 97 + m) - 0.5F) * 5.0F;
            bonecaAmbalabu.getClass02(classBound, bonecaAmbalabuValue14, classBound - m * (2.0F + 2.0F * getAmount(p, 99)), bonecaAmbalabuValue14 + 3.0F, 1.6F, ForeShinUtils.getValueForIntFloat(7228464, 0.4F));
         }
      }

      float bonecaAmbalabuValue16 = getAmount(bonecaAmbalabuValues, amounts2, -4.4F);
      float bonecaAmbalabuValue17 = getAmount(bonecaAmbalabuValues, amounts2, -5.6F);

      for (int q = 0; q < 18; q++) {
         float bonecaAmbalabuFactor5 = q % 2 == 0 ? 1.0F : -1.0F;
         float classValue = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuFactor5 * (0.2F + 0.14F * getAmount(q, 101)));
         float amountForFloat = bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue16 + (bonecaAmbalabuValue17 - bonecaAmbalabuValue16) * getAmount(q, 103));
         bonecaAmbalabu.getClass022(classValue, amountForFloat, 0.9F, 0.9F, ForeShinUtils.getValueForIntFloat(4862756, 0.5F));
      }

      float bonecaAmbalabuFactor6 = getAmount(bonecaAmbalabuValues, amounts2, -5.85F);

      for (int r = -1; r <= 1; r += 2) {
         float bonecaAmbalabuValue20 = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + r * 0.1F);
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue20, bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor6, 7.5F, 4.6F, ForeShinUtils.getValueForIntFloat(9069893, 0.35F));
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabuValue20 - r * 3.6F,
            bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor6 + 3.6F,
            bonecaAmbalabuValue20 + r * 0.7F,
            bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor6,
            bonecaAmbalabuValue20 + r * 6.4F,
            bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor6 - 3.2F,
            2.6F,
            ForeShinUtils.getValueForInt(3877407)
         );
      }

      bonecaAmbalabu.getClass02(
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor6,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         (float)bonecaAmbalabu.bonecaValue,
         3.5F,
         ForeShinUtils.getValueForIntFloat(4862756, 0.9F)
      );
      float bonecaAmbalabuLimit = getAmount(bonecaAmbalabuValues, amounts2, 1.5F) * bonecaAmbalabu.bonecaValue;

      for (int s = 0; s < bonecaAmbalabuLimit; s++) {
         float bonecaAmbalabuValue21 = 1.0F - s / bonecaAmbalabuLimit;

         for (int t = 0; t < bonecaAmbalabu.ambalabuBonecaValue; t++) {
            bonecaAmbalabu.getBonecaAmbalabu(
               t, s, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(t, s), ForeShinUtils.getValueForInt(11832158), bonecaAmbalabuValue21 * 0.9F)
            );
         }
      }

      float bonecaAmbalabuFactor7 = getAmount(bonecaAmbalabuValues, amounts2, -3.0F);
      float bonecaAmbalabuFactor8 = getAmount(bonecaAmbalabuValues, amounts2, -5.6F);

      for (int bonecaAmbalabuTotal = -1; bonecaAmbalabuTotal <= 1; bonecaAmbalabuTotal += 2) {
         float classFactor = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuTotal * 0.405F);
         float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuTotal * 0.37F);
         bonecaAmbalabu.getClassForFloatInt(
            classFactor,
            bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor7,
            bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuTotal * 0.362F),
            bonecaAmbalabu.bonecaValue * (bonecaAmbalabuFactor7 + bonecaAmbalabuFactor8) * 0.5F,
            ambalabuBonecaValue,
            bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor8,
            2.6F,
            ForeShinUtils.getValueForIntFloat(3809050, 0.65F)
         );
      }
   }

   @Override
   public String formatTungDin() {
      return "frigo_camelo";
   }

   private static void handleAmounts(LatheLoft latheLoft, ForeEar foreEar, ForeEar foreEar2, ForeEar foreEar3, float shinFootValue, ShinBrr shinBrr, ShinBrr shinBrr2, float[][] floats, ShinBrr[] shinBrrs) {
      float sideScaled = shinFootValue * 3.15F;
      latheLoft.getLatheLoftForForeEar(foreEar);
      latheLoft.getLoftPath(
         new float[]{sideScaled, 10.799999F, 0.0F, sideScaled, 8.1F, -0.05F, sideScaled, 6.7F, -0.1F},
         new float[]{0.85F, 0.78F, 0.72F},
         new float[]{0.85F, 0.78F, 0.72F},
         8,
         shinBrr,
         null,
         null,
         0.0F,
         0.0F,
         -1.0F
      );
      latheLoft.getLatheLoftForForeEar(foreEar2);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(sideScaled, 6.7F, -0.35F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.78F, 0.85F, 0.7F, 8, shinBrr2);
      latheLoft.getDequeArray();
      latheLoft.getLoftPath(
         new float[]{sideScaled, 6.7F, -0.1F, sideScaled, 4.3F, 0.05F, sideScaled, 2.4F, 0.15F},
         new float[]{0.62F, 0.56F, 0.54F},
         new float[]{0.62F, 0.56F, 0.54F},
         8,
         shinBrr,
         null,
         null,
         0.0F,
         0.0F,
         -1.0F
      );
      latheLoft.getLatheLoftForForeEar(foreEar3);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(sideScaled, 0.0F, 0.15F).getLatheLoft4(-shinFootValue * 6.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, -1.3F);
      latheLoft.getLatheProfile(new float[]{1.0F, 0.97F}, new float[]{0.0F, 1.12F}, 12, 2.1F, 3.95F, shinBrrs[2], shinBrrs[3], shinBrrs[4]);
      latheLoft.getDequeArray();
      latheLoft.getLoftPath(floats[0], floats[1], floats[2], 10, shinBrrs[0], null, null, 0.0F, 1.0F, 0.0F);
      latheLoft.getLatheProfile(NECK_PROFILE_RADII, NECK_PROFILE_HEIGHTS, 10, 1.12F, 1.22F, shinBrrs[1], null, shinBrrs[5]);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 4.45F, 0.0F).getLatheLoftForFloat(1.12F, 1.0F, 1.22F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 1.42F, 0.45F, 12, 5, shinBrrs[7]);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 4.1F, -1.2F).getLatheLoftLatheLoftAmountLoft(-6.0F);
      latheLoft.getPolygonExtrude(new float[]{-0.7F, 0.0F, 0.7F, 0.0F, 0.68F, 0.9F, 0.35F, 1.05F, -0.35F, 1.05F, -0.68F, 0.9F}, -0.15F, 0.15F, shinBrrs[6], shinBrrs[6], shinBrrs[6]);
      latheLoft.getDequeArray();
      latheLoft.getDequeArray();
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.cab = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 9.4F, 0.0F);
      this.door = latheLoft.getClassForStringForeEar("cab", this.cab, 0.0F, 8.7F, 0.0F);
      this.bonecaAmbalabuForeEar = latheLoft.getClassForStringForeEar("door", this.door, 5.95F, 15.0F, -4.75F);
      this.neckTop = latheLoft.getClassForStringForeEar("neck", this.door, 0.0F, 26.9F, getAmounts(26.9F)[0]);
      this.head = latheLoft.getClassForStringForeEar("neck_top", this.neckTop, 0.0F, 31.2F, getAmounts(31.2F)[0]);
      this.breath = latheLoft.getClassForStringForeEar("head", this.head, 0.0F, 35.5F, -6.2F);
      this.classBreath = latheLoft.getClassForStringForeEar("jaw", this.breath, 0.0F, getCosSinForFloat(-0.5F, -1.3F), getCosSin(-0.5F, -1.3F));
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("eye_r", this.breath, 1.7249999F, getCosSinForFloat(1.68F, -0.7F), getCosSin(1.68F, -0.7F));
      this.classShinProgress = latheLoft.getClassForStringForeEar("eye_l", this.breath, -1.7249999F, getCosSinForFloat(1.68F, -0.7F), getCosSin(1.68F, -0.7F));
      this.classShinFoot = latheLoft.getClassForStringForeEar("breath", this.breath, 0.0F, getCosSinForFloat(-0.9F, -5.9F), getCosSin(-0.9F, -5.9F));
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", this.cab, 3.15F, 9.4F, 0.0F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 3.15F, 6.7F, -0.1F);
      this.classBonecaAmbalabu = latheLoft.getClassForStringForeEar("foot_r", this.footR, 3.15F, 2.2F, 0.15F);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", this.cab, -3.15F, 9.4F, 0.0F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -3.15F, 6.7F, -0.1F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -3.15F, 2.2F, 0.15F);
      latheLoft.getLatheLoft(this.shinR);
      LoftLatheFlag.Region region = latheLoft.getMax(32, 2);
      handleBonecaAmbalabuForBonecaAmbalabu(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(90, 90);
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu(), true);
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr shinBrr = latheLoft.getMax(14541285);
      this.handleDoor(latheLoft, classForRegion, shinBrr);
      this.handleBonecaAmbalabuForLatheLoft(latheLoft);
      this.handleBonecaAmbalabu(latheLoft, latheLoft.getClassForRegion(max2), classForRegion, shinBrr);
      this.handleNeckHead(latheLoft);
      this.handleBreathAmounts(latheLoft);
      this.handleBonecaAmbalabu(latheLoft);
   }

   private static void handle(LatheLoft latheLoft, float shinFootValue, float hypot, ShinBrr shinBrr, ShinBrr shinBrr2) {
      latheLoft.getLatheLoft(-5.4F, shinFootValue, -6.6F, -4.6F, hypot, -5.85F, shinBrr, shinBrr2, shinBrr2, shinBrr2, shinBrr2, shinBrr2);
      latheLoft.getLatheLoft(-5.2F, shinFootValue + 0.35F, -5.9F, -4.8F, hypot - 0.35F, -5.2999997F, null, null, shinBrr2, shinBrr2, shinBrr2, shinBrr2);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue, boolean bonecaAmbalabuFlag) {
      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue2 = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float absScaled = Math.abs(bonecaAmbalabuValue2 - 0.5F) * 2.0F;
         int bonecaAmbalabuValue3 = absScaled < 0.5F
            ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14071694), ForeShinUtils.getValueForInt(12884588), getAmount(absScaled / 0.5F))
            : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12884588), ForeShinUtils.getValueForInt(11108435), getAmount((absScaled - 0.62F) / 0.38F));

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, bonecaAmbalabuValue3);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(12160608), 0.3F, 6.0F, bonecaAmbalabuValue + 2).getBonecaAmbalabu(0.03F, 12.0F, bonecaAmbalabuValue);
      handleBonecaAmbalabu(bonecaAmbalabu, 210, 9.0F, 1.5F, 9332544, 0.12F, bonecaAmbalabuValue + 10, 0.3F, 0.0F, 1.0F);
      handleBonecaAmbalabu(bonecaAmbalabu, 160, 8.0F, 1.4F, 14861982, 0.12F, bonecaAmbalabuValue + 12, 0.3F, 0.0F, 1.0F);
      handleBonecaAmbalabu(bonecaAmbalabu, 75, 12.0F, 2.4F, 8017460, 0.32F, bonecaAmbalabuValue + 16, 0.5F, 0.0F, 0.1F);
      handleBonecaAmbalabu(bonecaAmbalabu, 75, 12.0F, 2.4F, 8017460, 0.32F, bonecaAmbalabuValue + 18, 0.5F, 0.9F, 1.0F);
      handleBonecaAmbalabu(bonecaAmbalabu, 45, 9.0F, 1.6F, 12293221, 0.3F, bonecaAmbalabuValue + 20, 0.6F, 0.0F, 0.08F);
      handleBonecaAmbalabu(bonecaAmbalabu, 45, 9.0F, 1.6F, 12293221, 0.3F, bonecaAmbalabuValue + 22, 0.6F, 0.92F, 1.0F);
      if (bonecaAmbalabuFlag) {
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue - 4.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(7228464, 0.25F));
      }
   }

   private static float getAmount(int shinFootValue, int shinFootValue2) {
      int shinFootValue3 = shinFootValue * 374761393 + shinFootValue2 * 668265263 + 1540483477;
      int shinFootValue4 = (shinFootValue3 ^ shinFootValue3 >>> 13) * 1274126177;
      int shinFootValue5 = shinFootValue4 ^ shinFootValue4 >>> 16;
      return (shinFootValue5 & 16777215) / 1.6777215E7F;
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13605455))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11566650), 0.35F, 6.0F, 121)
         .getBonecaAmbalabu(0.05F, 14.0F, 123);
      float bonecaAmbalabuValue = 3.0F;
      float bonecaAmbalabuFactor = 0.1F;
      bonecaAmbalabu.getClassBonecaAmbalabu(
         bonecaAmbalabu.ambalabuBonecaValue * (0.5F - bonecaAmbalabuFactor * 0.6F), bonecaAmbalabuValue, bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuFactor * 0.6F), bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(12618306)
      );
      handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuFactor, bonecaAmbalabuValue + 1.0F, bonecaAmbalabu.bonecaValue - 1.0F, 3);

      for (int i = Math.round(bonecaAmbalabuValue) + 2; i < bonecaAmbalabu.bonecaValue; i += 3) {
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, 1.2F, i + 1.4F, ForeShinUtils.getValueForInt(15255690));
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue - 1.2F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1.4F, ForeShinUtils.getValueForInt(15255690));
      }

      for (int j = -1; j <= 1; j += 2) {
         float classBound = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + j * 0.14F);
         bonecaAmbalabu.getClass02(classBound, bonecaAmbalabuValue, classBound, (float)bonecaAmbalabu.bonecaValue, 0.8F, ForeShinUtils.getValueForIntFloat(9068076, 0.6F));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuValue, ForeShinUtils.getValueForIntFloat(9068076, 0.35F));
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4869456));

      for (int i = 0; i < 14; i++) {
         float bonecaAmbalabuValue = 4.0F + i * (bonecaAmbalabu.ambalabuBonecaValue - 8.0F) / 14.0F;
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuValue, 2.2F, bonecaAmbalabuValue + 3.4F, bonecaAmbalabu.bonecaValue - 2.2F, ForeShinUtils.getValueForInt(1974306));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, 0.8F, ForeShinUtils.getValueForInt(6974832));
   }

   @Override
   public float getAmountFloat() {
      return 0.8F;
   }

   private void handleBonecaAmbalabu(LatheLoft latheLoft) {
      LoftLatheFlag.Region region = latheLoft.getMax(32, 40);
      BonecaAmbalabu bonecaAmbalabu = region.getClassBonecaAmbalabu();
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(12884588)).getBonecaAmbalabu(0.06F, 6.0F, 101);
      handleBonecaAmbalabu(bonecaAmbalabu, 60, 5.0F, 0.8F, 9332544, 0.3F, 103, 0.3F, 0.0F, 1.0F);
      LoftLatheFlag.Region max2 = latheLoft.getMax(32, 24);
      BonecaAmbalabu classBonecaAmbalabu3 = max2.getClassBonecaAmbalabu();
      classBonecaAmbalabu3.getBonecaAmbalabu(ForeShinUtils.getValueForInt(12884588)).getBonecaAmbalabu(0.05F, 6.0F, 105);
      classBonecaAmbalabu3.getClass02(classBonecaAmbalabu3.ambalabuBonecaValue * 0.5F, classBonecaAmbalabu3.bonecaValue * 0.52F, classBonecaAmbalabu3.ambalabuBonecaValue * 0.3F, 0.25F, 7, ForeShinUtils.getValueForIntFloat(9399368, 0.6F))
         .getClass02(classBonecaAmbalabu3.ambalabuBonecaValue * 0.5F, classBonecaAmbalabu3.bonecaValue * 0.55F, classBonecaAmbalabu3.ambalabuBonecaValue * 0.22F, 0.3F, 9, ForeShinUtils.getValueForInt(8018490));
      classBonecaAmbalabu3.getBonecaAmbalabu(0.06F, 12.0F, 107);
      float[][] floats = getAmounts(
         new float[]{0.0F, 1.55F, 1.5F, 0.0F, 1.7F, 0.0F, 0.0F, 1.55F, -2.2F, 0.0F, 1.38F, -3.6F},
         new float[]{1.72F, 1.86F, 1.88F, 1.63F},
         new float[]{1.5F, 1.68F, 1.42F, 1.12F},
         1,
         0.45F,
         1,
         0.9F,
         2
      );
      LoftLatheFlag.Region max3 = latheLoft.getMax(96, 72);
      handleBonecaAmbalabuForBonecaAmbalabuFloatArray(max3.getClassBonecaAmbalabu(), floats[0]);
      LoftLatheFlag.Region max4 = latheLoft.getMax(96, 48);
      handleBonecaAmbalabu3(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(128, 12);
      handleBonecaAmbalabu5(max5.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr shinBrr = latheLoft.getClassForRegion(max2);
      ShinBrr[] shinBrrs = new ShinBrr[]{
         latheLoft.getClassForRegion(max3),
         latheLoft.getClassForRegion(max4),
         latheLoft.getClassForRegion(max5),
         latheLoft.getMax(3811869),
         latheLoft.getMax(10186556),
         latheLoft.getMax(2365455),
         latheLoft.getMax(11566650),
         latheLoft.getMax(4928033)
      };
      handleAmounts(latheLoft, this.shinR, this.footR, this.classBonecaAmbalabu, 1.0F, classForRegion, shinBrr, floats, shinBrrs);
      handleAmounts(latheLoft, this.shinL, this.footL, this.shinProgressForeEar, -1.0F, classForRegion, shinBrr, floats, shinBrrs);
   }

   private static float[] getAmounts(float shinFootValue) {
      float[] pointsSpline = LatheLoft.getPointsSpline(8, PATH_HEIGHTS);
      int shinFootLimit = LatheLoft.getValue(pointsSpline);
      float[] shinFootValues = LatheLoft.getAmounts(shinFootLimit, PATH_RADII_X);
      float[] amounts2 = LatheLoft.getAmounts(shinFootLimit, PATH_RADII_Y);

      for (int i = 1; i < shinFootLimit; i++) {
         float shinFootValue2 = pointsSpline[3 * i - 2];
         float shinFootValue3 = pointsSpline[3 * i + 1];
         if ((shinFootValue2 - shinFootValue) * (shinFootValue3 - shinFootValue) <= 0.0F && shinFootValue2 != shinFootValue3) {
            float shinFootFactor = (shinFootValue - shinFootValue2) / (shinFootValue3 - shinFootValue2);
            float deltaVar = shinFootValue3 - shinFootValue2;
            float shinFootFactor2 = pointsSpline[3 * i + 2] - pointsSpline[3 * i - 1];
            float distance = (float)Math.sqrt(deltaVar * deltaVar + shinFootFactor2 * shinFootFactor2);
            return new float[]{
               pointsSpline[3 * i - 1] + shinFootFactor2 * shinFootFactor,
               shinFootValues[i - 1] + (shinFootValues[i] - shinFootValues[i - 1]) * shinFootFactor,
               amounts2[i - 1] + (amounts2[i] - amounts2[i - 1]) * shinFootFactor,
               deltaVar / distance,
               shinFootFactor2 / distance
            };
         }
      }

      return new float[]{PATH_HEIGHTS[2], PATH_RADII_X[0], PATH_RADII_Y[0], 1.0F, 0.0F};
   }

   private void handleBonecaAmbalabuForLatheLoft(LatheLoft latheLoft) {
      LoftLatheFlag.Region region = latheLoft.getMax(40, 48);
      region.getClassBonecaAmbalabu().getBonecaAmbalabu(ForeShinUtils.getValueForInt(16777215), ForeShinUtils.getValueForInt(14936556));
      LoftLatheFlag.Region max2 = latheLoft.getMax(48, 16);
      BonecaAmbalabu classBonecaAmbalabu2 = max2.getClassBonecaAmbalabu();
      classBonecaAmbalabu2.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14544866), ForeShinUtils.getValueForInt(13099727));
      classBonecaAmbalabu2.getClassBonecaAmbalabu(0.0F, 0.0F, classBonecaAmbalabu2.ambalabuBonecaValue, 2.0F, ForeShinUtils.getValueForInt(16054007))
         .getClassBonecaAmbalabu(classBonecaAmbalabu2.ambalabuBonecaValue * 0.3F, 3.0F, classBonecaAmbalabu2.ambalabuBonecaValue * 0.7F, 5.0F, ForeShinUtils.getValueForInt(11914684));
      classBonecaAmbalabu2.getClass022(classBonecaAmbalabu2.ambalabuBonecaValue * 0.3F, classBonecaAmbalabu2.bonecaValue * 0.7F, 5.0F, 4.0F, ForeShinUtils.getValueForIntFloat(7319130, 0.6F))
         .getClass022(classBonecaAmbalabu2.ambalabuBonecaValue * 0.62F, classBonecaAmbalabu2.bonecaValue * 0.72F, 4.0F, 3.5F, ForeShinUtils.getValueForIntFloat(14704702, 0.55F));
      LoftLatheFlag.Region max3 = latheLoft.getMax(32, 32);
      BonecaAmbalabu classBonecaAmbalabu3 = max3.getClassBonecaAmbalabu();
      classBonecaAmbalabu3.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16448248))
         .getClassBonecaAmbalabu(0.0F, 0.0F, classBonecaAmbalabu3.ambalabuBonecaValue, classBonecaAmbalabu3.bonecaValue * 0.16F, ForeShinUtils.getValueForInt(3105973));
      classBonecaAmbalabu3.getClassBonecaAmbalabu(0.0F, classBonecaAmbalabu3.bonecaValue * 0.5F, classBonecaAmbalabu3.ambalabuBonecaValue, classBonecaAmbalabu3.bonecaValue * 0.72F, ForeShinUtils.getValueForInt(3895222))
         .getClassBonecaAmbalabu(0.0F, classBonecaAmbalabu3.bonecaValue * 0.55F, classBonecaAmbalabu3.ambalabuBonecaValue, classBonecaAmbalabu3.bonecaValue * 0.67F, ForeShinUtils.getValueForInt(6986454));
      ShinBrr shinBrr = latheLoft.getMax(16054007);
      ShinBrr shinBrr2 = latheLoft.getMax(14149611);
      latheLoft.getLatheLoftForForeEar(this.door);
      latheLoft.getLatheLoft(-5.1F, 9.3F, -4.65F, 5.1F, 21.25F, 4.6F, null, latheLoft.getClassForRegion(region), shinBrr, shinBrr, shinBrr, shinBrr);

      for (float bonecaAmbalabuValue : new float[]{13.3F, 17.1F}) {
         latheLoft.getLatheLoft(-5.0499997F, bonecaAmbalabuValue, -3.6F, 5.0499997F, bonecaAmbalabuValue + 0.22F, 4.5499997F, shinBrr2, null, null, null, shinBrr2, shinBrr2);
      }

      latheLoft.getLatheLoft(-4.85F, 9.75F, -3.55F, 4.85F, 12.7F, 4.35F, latheLoft.getClassForRegion(max2), null, null, null, latheLoft.getMax(12573126), null);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(-2.4F, 17.32F, -0.6F);
      latheLoft.getLatheLoft(new float[]{0.78F, 0.8F, 0.68F, 0.42F, 0.42F}, new float[]{0.0F, 2.0F, 2.45F, 2.85F, 3.3F}, 6, latheLoft.getClassForRegion(max3), null, latheLoft.getMax(3105973));
      latheLoft.getDequeArray();
      latheLoft.getLatheLoft(0.5F, 13.52F, -2.3F, 2.9F, 14.3F, -1.0F, latheLoft.getMax(15913835));
   }

   private static float[] getAmounts(float[] shinFootValues, int shinFootValue, int shinFootValue2) {
      float shinFootFactor = shinFootValues[3 * shinFootValue2] - shinFootValues[3 * shinFootValue];
      float shinFootFactor2 = shinFootValues[3 * shinFootValue2 + 1] - shinFootValues[3 * shinFootValue + 1];
      float shinFootFactor3 = shinFootValues[3 * shinFootValue2 + 2] - shinFootValues[3 * shinFootValue + 2];
      float distance = (float)Math.sqrt(shinFootFactor * shinFootFactor + shinFootFactor2 * shinFootFactor2 + shinFootFactor3 * shinFootFactor3);
      return new float[]{shinFootFactor / distance, shinFootFactor2 / distance, shinFootFactor3 / distance};
   }

   private void handleNeckHead(LatheLoft latheLoft) {
      LoftLatheFlag.Region region = latheLoft.getMax(88, 76);
      LoftLatheFlag.Region max2 = latheLoft.getMax(88, 76);
      handleBonecaAmbalabu(region.getClassBonecaAmbalabu(), 61, false);
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu(), 161, true);
      LoftLatheFlag.Region max3 = latheLoft.getMax(16, 48);
      BonecaAmbalabu bonecaAmbalabu = max3.getClassBonecaAmbalabu();
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11108435)).getBonecaAmbalabu(0.05F, 6.0F, 151);
      handleBonecaAmbalabu(bonecaAmbalabu, 70, 6.0F, 1.2F, 7228464, 0.45F, 153, 0.6F, 0.0F, 1.0F);
      handleBonecaAmbalabu(bonecaAmbalabu, 40, 5.0F, 1.0F, 12884588, 0.35F, 155, 0.6F, 0.0F, 1.0F);
      ShinBrr classForRegion = latheLoft.getClassForRegion(max3);
      latheLoft.getLatheLoftForForeEar(this.neckTop);
      handleAmounts(latheLoft, new float[]{25.6F, 27.8F, 30.0F, 31.2F, 31.5F, 31.9F}, latheLoft.getClassForRegion(region), classForRegion, 31.9F);
      latheLoft.getLatheLoftForForeEar(this.head);
      handleAmounts(latheLoft, new float[]{30.5F, 30.9F, 31.2F, 32.6F, 34.1F, 35.6F, 36.2F}, latheLoft.getClassForRegion(max2), classForRegion, 35.6F);
   }

   private void handleDoor(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2) {
      LoftLatheFlag.Region region = latheLoft.getMax(90, 38);
      handleBonecaAmbalabu(region.getClassBonecaAmbalabu(), false);
      LoftLatheFlag.Region max2 = latheLoft.getMax(40, 72);
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(48, 72);
      handleBonecaAmbalabu6(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(88, 8);
      handleBonecaAmbalabu4(max4.getClassBonecaAmbalabu());
      ShinBrr shinBrr3 = latheLoft.getMax(15724523);
      ShinBrr shinBrr4 = latheLoft.getMax(15856109);
      ShinBrr max5 = latheLoft.getMax(12171958);
      ShinBrr max6 = latheLoft.getMax(3092787);
      ShinBrr max7 = latheLoft.getMax(10856877);
      ShinBrr max8 = latheLoft.getMax(9278102);
      latheLoft.getLatheLoftForForeEar(this.door);
      latheLoft.getLoftLathe(
         -5.95F, 8.7F, -4.75F, 5.95F, 26.9F, 5.35F, 0.8F, 2, new ShinBrr[]{null, latheLoft.getClassForRegion(max3), latheLoft.getClassForRegion(max2), latheLoft.getClassForRegion(max2), shinBrr4, max5}, shinBrr3
      );
      latheLoft.getLoftLathe(-5.95F, 21.53F, -5.35F, 5.95F, 26.9F, -4.73F, 0.35F, 2, new ShinBrr[]{latheLoft.getClassForRegion(region), shinBrr3, shinBrr3, shinBrr3, shinBrr3, shinBrr3}, shinBrr3);
      latheLoft.getLatheLoft(-5.75F, 21.6F, -4.8F, 5.75F, 26.75F, -4.6F, null, null, max7, max7, max7, max7);
      latheLoft.getLatheLoft(-5.6499996F, 21.25F, -5.05F, 5.6499996F, 21.650002F, -4.65F, max8, null, null, null, max8, max8);
      latheLoft.getLatheLoft(-5.6F, 8.72F, -5.0499997F, 5.6F, 9.5F, -4.45F, latheLoft.getClassForRegion(max4), null, max6, max6, max6, max6);
      handle(latheLoft, 22.45F, 25.650002F, shinBrr, shinBrr2);
      float doorValue = getAmounts(26.9F)[0];
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 26.96F, doorValue).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 2.3F, 2.53F, 12, max6);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 27.02F, doorValue).getLatheLoftForFloat(1.0F, 1.0F, 1.1F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 2.4F, 0.3F, 12, 4, max7);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuFactor, float bonecaAmbalabuValue, float bonecaAmbalabuValue2, int bonecaAmbalabuDivisor) {
      for (int i = 0; i < bonecaAmbalabuDivisor; i++) {
         float amountForFloat = bonecaAmbalabuValue + (bonecaAmbalabuValue2 - bonecaAmbalabuValue) * (i + 0.5F) / bonecaAmbalabuDivisor;
         float hashHuntBound = bonecaAmbalabuValue + (bonecaAmbalabuValue2 - bonecaAmbalabuValue) * (i + 1.5F) / bonecaAmbalabuDivisor;
         if (i < bonecaAmbalabuDivisor - 1) {
            bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue * (0.5F - bonecaAmbalabuFactor), amountForFloat, bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuFactor), hashHuntBound, 2.2F, ForeShinUtils.getValueForInt(8015143));
            bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuFactor), amountForFloat, bonecaAmbalabu.ambalabuBonecaValue * (0.5F - bonecaAmbalabuFactor), hashHuntBound, 2.2F, ForeShinUtils.getValueForInt(8015143));
         }

         for (int j = -1; j <= 1; j += 2) {
            bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * (0.5F + j * bonecaAmbalabuFactor), amountForFloat, 1.8F, 1.8F, ForeShinUtils.getValueForInt(12099696));
            bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * (0.5F + j * bonecaAmbalabuFactor), amountForFloat, 0.7F, 0.7F, ForeShinUtils.getValueForInt(3811869));
         }
      }
   }

   private static float[] getAmounts(float[] shinFootValues) {
      int shinFootSize = shinFootValues.length / 3;
      float[] floats = new float[shinFootSize];

      for (int i = 1; i < shinFootSize; i++) {
         float shinFootFactor = shinFootValues[3 * i] - shinFootValues[3 * i - 3];
         float shinFootFactor2 = shinFootValues[3 * i + 1] - shinFootValues[3 * i - 2];
         float shinFootFactor3 = shinFootValues[3 * i + 2] - shinFootValues[3 * i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(shinFootFactor * shinFootFactor + shinFootFactor2 * shinFootFactor2 + shinFootFactor3 * shinFootFactor3);
      }

      for (int j = 0; j < shinFootSize; j++) {
         floats[j] /= floats[shinFootSize - 1];
      }

      return floats;
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuValue = bonecaAmbalabu.bonecaValue * 0.34F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3811869));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuValue, ForeShinUtils.getValueForInt(10186556));

      for (int i = 0; i < 64; i++) {
         float bonecaAmbalabuValue2 = i * bonecaAmbalabu.ambalabuBonecaValue / 64.0F;
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuValue2 + 0.4F, bonecaAmbalabuValue * 0.38F, bonecaAmbalabuValue2 + 1.3F, bonecaAmbalabuValue * 0.62F, ForeShinUtils.getValueForInt(15255690));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue - 0.6F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuValue + 0.4F, ForeShinUtils.getValueForInt(2365970));

      for (int j = 0; j < 28; j++) {
         float classBound = j * bonecaAmbalabu.ambalabuBonecaValue / 28.0F;
         bonecaAmbalabu.getClassBonecaAmbalabu(classBound, bonecaAmbalabu.bonecaValue * 0.62F, classBound + 1.6F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(1708556));
      }
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabuFloatArray(BonecaAmbalabu bonecaAmbalabu, float[] bonecaAmbalabuValues) {
      float[] amounts2 = getAmounts(bonecaAmbalabuValues);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13605455))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11566650), 0.35F, 7.0F, 111)
         .getBonecaAmbalabu(0.05F, 16.0F, 113);
      float bonecaAmbalabuValue = getAmount(bonecaAmbalabuValues, amounts2, -2.6F);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue2 = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue3 = (float)Math.pow(Math.abs(bonecaAmbalabuValue2 - 0.5F) * 2.0F, 2.0) * 0.14F;
         float bonecaAmbalabuValue4 = bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue - bonecaAmbalabuValue3);
         bonecaAmbalabu.getClassBonecaAmbalabu(i, bonecaAmbalabuValue4 - 0.5F, i + 1.0F, bonecaAmbalabuValue4 + 0.5F, ForeShinUtils.getValueForIntFloat(9068076, 0.6F));
         if (i % 3 == 0) {
            bonecaAmbalabu.getClassBonecaAmbalabu(i, bonecaAmbalabuValue4 - 2.6F, i + 1.4F, bonecaAmbalabuValue4 - 1.8F, ForeShinUtils.getValueForIntFloat(15255690, 0.85F));
         }

         if (i % 3 == 1) {
            bonecaAmbalabu.getClassBonecaAmbalabu(i, bonecaAmbalabuValue4 + 1.4F, i + 1.4F, bonecaAmbalabuValue4 + 2.2F, ForeShinUtils.getValueForIntFloat(15255690, 0.85F));
         }
      }

      float bonecaAmbalabuFactor = getAmount(bonecaAmbalabuValues, amounts2, -1.0F);
      float bonecaAmbalabuFactor2 = getAmount(bonecaAmbalabuValues, amounts2, -2.2F);
      float bonecaAmbalabuValue5 = 0.1F;
      bonecaAmbalabu.getClassBonecaAmbalabu(
         bonecaAmbalabu.ambalabuBonecaValue * (0.5F - bonecaAmbalabuValue5 * 0.6F),
         bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor - 2.0F,
         bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuValue5 * 0.6F),
         bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor2,
         ForeShinUtils.getValueForInt(12618306)
      );
      handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue5, bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor, bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor2, 3);
      float bonecaAmbalabuFactor3 = getAmount(bonecaAmbalabuValues, amounts2, 0.9F);
      bonecaAmbalabu.getClass02(0.0F, bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor3, (float)bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor3, 0.8F, ForeShinUtils.getValueForIntFloat(9068076, 0.5F));
   }

   private static float getCosSin(float cosSinFactor, float cosSinFactor2) {
      return -6.2F + 1.15F * (cosSinFactor * shinFootAmount + cosSinFactor2 * cosSinAmount);
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11843512)).getBonecaAmbalabu(0.03F, 6.0F, 31);

      for (int i = 0; i < 7; i++) {
         float bonecaAmbalabuValue = 7.0F + i * (bonecaAmbalabu.ambalabuBonecaValue - 14.0F) / 6.0F;
         bonecaAmbalabu.getClass02(bonecaAmbalabuValue, 8.0F, bonecaAmbalabuValue, bonecaAmbalabu.bonecaValue - 12.0F, 1.3F, ForeShinUtils.getValueForInt(5593436));
      }

      for (int j = 0; j < 12; j++) {
         float amountForFloat = 8.0F + j * (bonecaAmbalabu.bonecaValue - 20.0F) / 11.0F;
         bonecaAmbalabu.getClass02(5.0F, amountForFloat, bonecaAmbalabu.ambalabuBonecaValue - 5.0F, amountForFloat, 0.7F, ForeShinUtils.getValueForInt(7238261));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.3F, bonecaAmbalabu.bonecaValue - 9.0F, bonecaAmbalabu.ambalabuBonecaValue * 0.7F, bonecaAmbalabu.bonecaValue - 4.0F, ForeShinUtils.getValueForInt(15592936));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.34F, bonecaAmbalabu.bonecaValue - 7.5F, bonecaAmbalabu.ambalabuBonecaValue * 0.66F, bonecaAmbalabu.bonecaValue - 6.8F, ForeShinUtils.getValueForInt(9079430));
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float angle = poseProgress.animationProgress * 0.6662F;
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float animationProgressScaled = poseProgress.getAnimationProgressForFloat(0.0F) * 0.55F;
      float animationProgressScaled2 = poseProgress.getAnimationProgressForFloat((float) Math.PI) * 0.55F;
      float shinProgressCounter = -poseProgress.getAnimationProgress((float) Math.PI) * 0.75F;
      float foreShinValue = -poseProgress.getAnimationProgress(0.0F) * 0.75F;
      float swingProgress = poseProgress.swingProgress;
      float shinProgressFactor2 = swingProgress <= 0.0F ? 0.0F : (swingProgress < 0.3F ? getAmount(swingProgress / 0.3F) : 1.0F - getAmount((swingProgress - 0.3F) / 0.7F));
      float shinProgressTotal = animationProgressScaled + 0.2F * shinProgressFactor2;
      float shinProgressTotal2 = animationProgressScaled2 - 0.2F * shinProgressFactor2;
      if (poseProgress.sneakingPose) {
         shinProgressTotal += 0.98F;
         shinProgressTotal2 += 0.98F;
         shinProgressCounter--;
         foreShinValue--;
      }

      boolean shinProgressFlag = !poseProgress.vehicle && !poseProgress.gliding && !poseProgress.swimming;
      float shinProgressValue = shinProgressFlag ? Math.min(getAmountForFloat(shinProgressTotal, shinProgressTotal + shinProgressCounter), getAmountForFloat(shinProgressTotal2, shinProgressTotal2 + foreShinValue)) : 0.0F;
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      foreShinScale.getAmountsForeShin2(this.cab, 0.0F, -shinProgressValue, 0.0F);
      foreShinScale.getAmounts(this.door, 0.0F, cos * 0.06F * shinProgressFactor, -cos * 0.16F * shinProgressFactor);
      foreShinScale.getAmountsForeShin2(this.door, 0.0F, 0.6F * Math.abs(sin) * shinProgressFactor, 0.0F);
      foreShinScale.getAmounts(this.shinR, shinProgressTotal, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinL, shinProgressTotal2, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footR, shinProgressCounter, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footL, foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classBonecaAmbalabu, -shinProgressTotal - shinProgressCounter, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, -shinProgressTotal2 - foreShinValue, 0.0F, 0.0F);
      float shinProgressFactor3 = (float)Math.cos(2.0F * angle) * shinProgressFactor;
      float max2 = Math.max(-0.6F, Math.min(0.7F, poseProgress.shinProgressAmount));
      float shinProgressValue3 = -0.22F * shinProgressFactor3 + 0.02F * poseProgress.getAgeCode(90.0F, 0.0F) + 0.35F * max2;
      foreShinScale.getAmounts(this.neckTop, Math.max(-0.25F, 0.35F * shinProgressValue3 - 0.15F * shinProgressFactor2), poseProgress.headYawRadians * 0.4F, 0.0F);
      foreShinScale.getAmounts(this.head, 0.65F * shinProgressValue3 - 0.35F * shinProgressFactor2, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.breath, 0.15F * shinProgressFactor3 + 0.5F * max2 + 0.3F * shinProgressFactor2, poseProgress.headYawRadians * 0.6F, 0.0F);
      float shinProgressFactor4 = 0.0F;
      float shinProgressBound = 0.0F;
      if (poseProgress.limbSwingStrength < 0.3F && swingProgress <= 0.0F && !poseProgress.poseProgressActive && !poseProgress.sneakingPose) {
         float shinProgressValue4 = (poseProgress.age + (poseProgress.hashCode & 0xFF)) % 260.0F;
         shinProgressFactor4 = getAmount((shinProgressValue4 - 28.0F) / 16.0F) * (1.0F - getAmount((shinProgressValue4 - 100.0F) / 18.0F)) * (1.0F - poseProgress.limbSwingStrength / 0.3F);
         shinProgressBound = 1.05F * shinProgressFactor4;
         foreShinScale.getAmountsBoneCount(this.breath, -0.3F * shinProgressFactor4, -0.3F * shinProgressFactor4, 0.0F);
      }

      float shinProgressLimit = (poseProgress.age + (poseProgress.hashCode & 0xFF) + 62.0F) % 90.0F;
      float shinProgressFactor5 = shinProgressLimit > 36.0F ? 0.0F : getAmount(shinProgressLimit / 8.0F) * (1.0F - getAmount((shinProgressLimit - 22.0F) / 14.0F));
      float min6 = Math.min(1.0F, shinProgressLimit / 36.0F);
      if (shinProgressFactor4 * 0.8F > shinProgressFactor5) {
         shinProgressFactor5 = shinProgressFactor4 * 0.8F;
         min6 = 0.35F;
      }

      if (swingProgress > 0.0F) {
         float sin2 = (float)Math.sin(Math.PI * Math.min(1.0F, swingProgress / 0.85F));
         if (sin2 > shinProgressFactor5) {
            shinProgressFactor5 = sin2;
            min6 = 0.15F + 0.35F * swingProgress;
         }
      }

      foreShinScale.getClassForForeEarFloat(this.classShinFoot, Math.max(0.001F, shinProgressFactor5 * (0.75F + 0.45F * min6)) / 0.6F);
      foreShinScale.getAmountsForeShin2(this.classShinFoot, 0.0F, 1.4F * min6, -3.8F * min6);
      float shinProgressValue5 = (poseProgress.age + (poseProgress.hashCode & 127) * 3.0F) % 110.0F;
      if (shinProgressValue5 < 5.0F) {
         float shinProgressDivisor = 0.1F + 0.9F * Math.abs(shinProgressValue5 - 2.5F) / 2.5F;
         foreShinScale.getAmountsForForeEarFloat(this.shinFootForeEar, 1.0F, shinProgressDivisor, 1.0F);
         foreShinScale.getAmountsForForeEarFloat(this.classShinProgress, 1.0F, shinProgressDivisor, 1.0F);
      }

      if (swingProgress > 0.0F) {
         float sin3 = (float)Math.sin(Math.PI * Math.min(1.0F, swingProgress / 0.5F));
         foreShinScale.getAmountsForeShin2(this.neckTop, 0.0F, 0.7F * shinProgressFactor2, -0.6F * shinProgressFactor2);
         foreShinScale.getAmounts(this.classBreath, -0.3F * sin3, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.door, -0.08F * shinProgressFactor2, 0.0F, 0.0F);
         shinProgressBound = Math.max(shinProgressBound, 1.4F * getAmount(swingProgress / 0.25F) * (1.0F - getAmount((swingProgress - 0.45F) / 0.15F)));
      }

      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsForeShin2(this.door, 0.0F, 0.5F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.door, -0.08F, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.neckTop, 0.0F, -4.0F * (1.0F - 0.7F * shinProgressFactor2), 0.4F);
         foreShinScale.getAmountsBoneCount(this.neckTop, 0.05F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.breath, -0.1F, 0.0F, 0.0F);
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.shinR, 1.3F, 0.1F, 0.3F);
         foreShinScale.getAmounts(this.shinL, 1.3F, -0.1F, -0.3F);
         foreShinScale.getAmounts(this.footR, -0.55F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.55F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.classBonecaAmbalabu, -0.5F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, -0.5F, 0.0F, 0.0F);
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         foreShinScale.getAmounts(this.shinR, -0.45F, 0.0F, 0.08F);
         foreShinScale.getAmounts(this.shinL, -0.45F, 0.0F, -0.08F);
         foreShinScale.getAmounts(this.footR, -0.3F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.3F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.classBonecaAmbalabu, 0.3F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, 0.3F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.neckTop, -0.2F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.head, -0.35F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.breath, 0.45F, 0.0F, 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.door, 1.05F, 0.95F, 1.05F);
         shinProgressBound = Math.max(shinProgressBound, 0.8F);
      }

      foreShinScale.getAmounts(this.bonecaAmbalabuForeEar, 0.0F, -shinProgressBound, 0.0F);
   }

   private static float getCosSinForFloat(float cosSinFactor, float cosSinFactor2) {
      return 35.5F + 1.15F * (cosSinFactor * cosSinAmount - cosSinFactor2 * shinFootAmount);
   }

   private static float getAmount(float[] shinFootValues, float[] shinFootValues2, float shinFootValue) {
      int shinFootLimit = shinFootValues.length / 3;

      for (int i = 1; i < shinFootLimit; i++) {
         float shinFootValue2 = shinFootValues[3 * i - 1];
         float shinFootValue3 = shinFootValues[3 * i + 2];
         if ((shinFootValue2 - shinFootValue) * (shinFootValue3 - shinFootValue) <= 0.0F && shinFootValue2 != shinFootValue3) {
            float shinFootFactor = (shinFootValue - shinFootValue2) / (shinFootValue3 - shinFootValue2);
            return shinFootValues2[i - 1] + (shinFootValues2[i] - shinFootValues2[i - 1]) * shinFootFactor;
         }
      }

      return 0.5F;
   }

   private static void handleAmounts(LatheLoft latheLoft, float[] shinFootValues, ShinBrr shinBrr, ShinBrr classForRegion, float shinFootBound) {
      int shinFootSize = shinFootValues.length;
      float shinFootValue = shinFootValues[0] < 31.2F ? shinFootValues[shinFootSize - 1] : shinFootValues[0];
      float[] floats = new float[3 * shinFootSize];
      float[] floats2 = new float[shinFootSize];
      float[] floats3 = new float[shinFootSize];

      for (int i = 0; i < shinFootSize; i++) {
         float[] amounts2 = getAmounts(shinFootValues[i]);
         float shinFootFactor = getAmount(shinFootValues[i], shinFootValue);
         floats[3 * i + 1] = shinFootValues[i];
         floats[3 * i + 2] = amounts2[0];
         floats2[i] = amounts2[1] * shinFootFactor;
         floats3[i] = amounts2[2] * shinFootFactor;
      }

      latheLoft.getLoftPath(floats, floats2, floats3, 12, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      float max2 = Math.max(shinFootValues[0], 26.0F);
      float min3 = Math.min(shinFootValues[shinFootSize - 1], shinFootBound);
      byte shinFootSize2 = 4;
      float[] loftPathValues = new float[3 * shinFootSize2];
      float[] floats5 = new float[shinFootSize2];
      float[] crownForeValues = new float[shinFootSize2];

      for (int j = 0; j < shinFootSize2; j++) {
         float shinFootValue4 = max2 + (min3 - max2) * j / (shinFootSize2 - 1);
         float[] amounts3 = getAmounts(shinFootValue4);
         float shinFootFactor2 = getAmount(shinFootValue4, shinFootValue);
         float shinFootFactor3 = amounts3[2] * 0.9F * shinFootFactor2;
         float shinFootValue5 = (shinFootValue4 - 26.0F) / 9.6F;
         loftPathValues[3 * j + 1] = shinFootValue4 - amounts3[4] * shinFootFactor3;
         loftPathValues[3 * j + 2] = amounts3[0] + amounts3[3] * shinFootFactor3;
         floats5[j] = getAmount(shinFootValue5, RIM_RADII_X) * shinFootFactor2;
         crownForeValues[j] = getAmount(shinFootValue5, RIM_RADII_Y) * shinFootFactor2;
      }

      latheLoft.getLoftPath(loftPathValues, floats5, crownForeValues, 6, classForRegion, null, null, 0.0F, 0.0F, 1.0F);
   }

   @Override
   public String formatTungDinString() {
      return "Frigo Camelo";
   }

   private static float getAmount(float shinFootBound, float[] shinFootValues) {
      float shinFootValue = Math.max(0.0F, Math.min(1.0F, shinFootBound)) * (shinFootValues.length - 1);
      int index = Math.min(shinFootValues.length - 2, (int)shinFootValue);
      return shinFootValues[index] + (shinFootValues[index + 1] - shinFootValues[index]) * getAmount(shinFootValue - index);
   }

   @Override
   public int getValue() {
      return 1;
   }
}
