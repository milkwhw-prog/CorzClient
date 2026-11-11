package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class LidRing extends WarmBrainrotBase {
   private ForeEar classShinFoot;
   private static final float[] bonecaAmbalabuTable = new float[]{0.08F, 0.41F, 0.74F};
   private ForeEar classShinProgress;
   private ForeEar shinFootForeEar;
   private static final float[] lidRingTable = new float[]{
      0.0F, 0.06F, 0.12F, 0.17F, 0.215F, 0.26F, 0.305F, 0.35F, 0.395F, 0.44F, 0.48F, 0.53F, 0.57F, 0.61F, 0.655F, 0.7F, 0.75F, 0.8F, 0.86F, 0.93F, 1.0F
   };
   private ForeEar shinProgressForeEar;
   private static final int[] table = new int[]{
      12171172,
      13483928,
      14205841,
      14928017,
      13214570,
      15785908,
      13214570,
      15785908,
      14928017,
      15785908,
      11041605,
      11041605,
      15785908,
      14928017,
      13214570,
      15785908,
      13214570,
      14928017,
      13483928,
      12171172,
      12171172
   };
   private static final float[][] lidRingFloats = new float[][]{
      {37.6F, 0.8F, 4.0F, 3.4F},
      {36.6F, 0.6F, 5.4F, 4.1F},
      {35.2F, 0.1F, 5.4F, 4.5F},
      {33.7F, -0.8F, 4.6F, 4.8F},
      {32.2F, -1.8F, 4.1F, 5.1F},
      {30.7F, -2.8F, 3.9F, 5.4F},
      {29.3F, -3.5F, 4.45F, 5.5F},
      {28.0F, -3.8F, 4.8F, 5.5F},
      {26.8F, -3.5F, 4.8F, 5.0F},
      {25.4F, -2.4F, 5.0F, 4.6F},
      {23.6F, -1.2F, 5.2F, 4.0F}
   };
   private ForeEar lidRingForeEar;
   private ForeEar footL;
   private ForeEar lidLSkin;
   private ForeEar lidR;
   private ForeEar foreEar;
   private ForeEar footLeftSection;
   private ForeEar ring;
   private ForeEar shinL;
   private ForeEar planet;
   private ForeEar lidRSkin;
   private ForeEar head;
   private ForeEar shinR;
   private static final float[][] RING_PART_PARAMS = new float[][]{
      {-2.1F, 1.3F, -8.75F, 1.5F, 1.4F, 2.05F},
      {-0.38F, 1.05F, -8.6F, 1.08F, 1.08F, 1.6F},
      {0.88F, 0.98F, -8.25F, 0.98F, 1.0F, 1.5F},
      {2.0F, 0.9F, -7.8F, 0.88F, 0.92F, 1.35F},
      {2.95F, 0.82F, -7.25F, 0.78F, 0.82F, 1.2F}
   };
   private ForeEar footR;
   private ForeEar ringUnx;
   private ForeEar ringSpin;
   private static final float[][] shinFootFloats = getAmounts();
   private static final float[][] floats = getAmounts(LidRing.shinFootFloats);
   private ForeEar lidL;
   private static final float[] CUMULATIVE_LENGTHS = getAmountsForFloatArray(shinFootFloats);
   private ForeEar earL;
   private ForeEar eyeLeftSection;
   private static final float radians = (float)Math.toRadians(getAmounts(1.0F)[3] * 0.55F);
   private ForeEar eyeRightSection;
   private ForeEar ringSection;

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14923922), ForeShinUtils.getValueForInt(13211770), getAmount((bonecaAmbalabuValue - 0.55F) / 0.3F));

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, value2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 4.0F, 57);
      float classValue = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      float amountForFloat = bonecaAmbalabu.bonecaValue * 0.3F;
      bonecaAmbalabu.getClass022(classValue, amountForFloat + 0.6F, bonecaAmbalabu.ambalabuBonecaValue * 0.165F, bonecaAmbalabu.bonecaValue * 0.2F, ForeShinUtils.getValueForIntFloat(12881776, 0.6F));
      bonecaAmbalabu.getClass022(classValue, amountForFloat, bonecaAmbalabu.ambalabuBonecaValue * 0.14F, bonecaAmbalabu.bonecaValue * 0.17F, ForeShinUtils.getValueForInt(15783620));
   }

   private static void handle(LatheLoft latheLoft, float lidRingFactor, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float[] lidRingValues = getAmounts(lidRingFactor);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(lidRingValues[0], lidRingValues[1], lidRingValues[2]).getLatheLoft4(lidRingValues[3] * 0.55F).getLatheLoftLatheLoftAmountLoft(-6.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 2.16F, 2.04F, 1.22F, 12, 0.0F, 0.43F, shinBrr);
      float sin = (float)Math.sin(1.3508848635140327);
      float lidRingValue = 2.04F * (float)Math.cos(1.3508848635140327);
      float lidRingFactor2 = 2.16F * sin;
      float lidRingValue2 = 1.22F * sin + 0.04F;
      float[] floats = new float[18];

      for (int i = 0; i < 6; i++) {
         double angle = Math.toRadians(-75.0F + 30.0F * i);
         floats[3 * i] = lidRingFactor2 * (float)Math.sin(angle);
         floats[3 * i + 1] = lidRingValue - 0.04F;
         floats[3 * i + 2] = -lidRingValue2 * (float)Math.cos(angle);
      }

      latheLoft.getLatheLoft(floats, new float[]{0.1F, 0.15F, 0.17F, 0.17F, 0.15F, 0.1F}, 6, shinBrr2, false);

      for (int j = 0; j < 3; j++) {
         double radians2 = Math.toRadians(lidRingFactor * (48.0F + 15.0F * j));
         float lidRingValue3 = lidRingFactor2 * (float)Math.sin(radians2);
         float lidRingValue4 = -lidRingValue2 * (float)Math.cos(radians2);
         float lidRingFactor3 = 0.75F + 0.12F * j;
         latheLoft.getLatheLoft(
            new float[]{lidRingValue3, lidRingValue - 0.04F, lidRingValue4, lidRingValue3 + lidRingFactor * 0.5F * lidRingFactor3, lidRingValue - 0.04F - 0.5F * lidRingFactor3, lidRingValue4 - 0.5F * lidRingFactor3},
            new float[]{0.11F, 0.03F},
            4,
            shinBrr2,
            false
         );
      }

      latheLoft.getDequeArray();
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      float[] shinFootValues = getAmounts(1.0F);
      float[] amounts2 = getAmounts(-1.0F);
      this.planet = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 10.0F, 0.0F);
      this.head = latheLoft.getClassForStringForeEar("planet", this.planet, 0.0F, 8.599999F, 0.0F);
      this.earL = latheLoft.getClassForStringForeEar("head", this.head, 0.0F, 26.8F, 0.2F);
      this.lidRingForeEar = latheLoft.getClassForStringForeEar("jaw", this.earL, 0.0F, 25.8F, -6.1F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("ear_r", this.earL, 4.2F, 34.600002F, 0.9F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("ear_l", this.earL, -4.2F, 34.600002F, 0.9F);
      this.eyeRightSection = latheLoft.getClassForStringForeEar("eye_r", this.earL, shinFootValues[0], shinFootValues[1] - 1.6F, shinFootValues[2]);
      this.eyeLeftSection = latheLoft.getClassForStringForeEar("eye_l", this.earL, amounts2[0], amounts2[1] - 1.6F, amounts2[2]);
      this.lidR = latheLoft.getClassForStringForeEar("lid_r_yaw", this.earL, shinFootValues[0], shinFootValues[1] - 1.6F, shinFootValues[2]);
      this.lidRSkin = latheLoft.getClassForStringForeEar("lid_r", this.lidR, shinFootValues[0], shinFootValues[1] - 1.6F, shinFootValues[2]);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("lid_r_skin", this.lidRSkin, shinFootValues[0], shinFootValues[1] - 1.6F, shinFootValues[2]);
      this.lidL = latheLoft.getClassForStringForeEar("lid_l_yaw", this.earL, amounts2[0], amounts2[1] - 1.6F, amounts2[2]);
      this.lidLSkin = latheLoft.getClassForStringForeEar("lid_l", this.lidL, amounts2[0], amounts2[1] - 1.6F, amounts2[2]);
      this.foreEar = latheLoft.getClassForStringForeEar("lid_l_skin", this.lidLSkin, amounts2[0], amounts2[1] - 1.6F, amounts2[2]);
      this.ringSpin = latheLoft.getClassForStringForeEar("ring_tilt", this.head, 0.0F, 18.4F, 0.0F);
      this.ringUnx = latheLoft.getClassForStringForeEar("ring_spin", this.ringSpin, 0.0F, 18.4F, 0.0F);
      this.ring = latheLoft.getClassForStringForeEar("ring_unx", this.ringUnx, 0.0F, 18.4F, 0.0F);
      this.ringSection = latheLoft.getClassForStringForeEar("ring", this.ring, 0.0F, 18.4F, 0.0F);
      this.shinR = latheLoft.getClassForStringForeEar("thigh_r", this.planet, 5.0F, 12.4F, 0.3F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 5.2F, 6.2F, 0.5F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("foot_r", this.footR, 5.3F, 2.5F, 0.8F);
      this.shinL = latheLoft.getClassForStringForeEar("thigh_l", this.planet, -5.0F, 12.4F, 0.3F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -5.2F, 6.2F, 0.5F);
      this.footLeftSection = latheLoft.getClassForStringForeEar("foot_l", this.footL, -5.3F, 2.5F, 0.8F);
      latheLoft.getLatheLoft(this.shinR);
      LoftLatheFlag.Region region = latheLoft.getMax(320, 160);
      handleBonecaAmbalabu6(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(128, 256);
      handleBonecaAmbalabuBonecaAmbalabuTable(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(224, 160);
      handleBonecaAmbalabuCUMULATIVE_LENGTHS(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(192, 112);
      handleBonecaAmbalabuForBonecaAmbalabu(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(48, 64);
      handleBonecaAmbalabu3(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(8, 32);
      handleBonecaAmbalabu7(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(48, 48);
      handleBonecaAmbalabu(max7.getClassBonecaAmbalabu());
      ShinBrr shinBrr = latheLoft.getMax(16052714);
      ShinBrr shinBrr2 = latheLoft.getMax(1842204);
      ShinBrr shinBrr3 = latheLoft.getMax(14858390);
      ShinBrr classForRegion = latheLoft.getClassForRegion(max7);
      ShinBrr shinBrr4 = latheLoft.getMax(15115942);
      latheLoft.getLatheLoftForForeEar(this.head);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 18.4F, 0.0F).getLatheLoft(-18.0F).getLatheLoftLatheLoftAmountLoft(-18.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 10.2F, 9.8F, 10.2F, 24, latheLoft.getClassForRegion(region));
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.ringSection);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 18.4F, 0.0F).getLatheLoft(-18.0F).getLatheLoftLatheLoftAmountLoft(-18.0F);
      latheLoft.getLatheLoft(new float[]{12.7F, 21.4F, 21.4F, 12.7F}, new float[]{-0.17F, -0.17F, 0.17F, 0.17F}, 36, latheLoft.getClassForRegion(max2), null, null);
      float shinFootFactor = 18.1F;
      float angle = (float)Math.toRadians(125.0);
      latheLoft.getLatheLoft(shinFootFactor * (float)Math.sin(angle), 0.0F, -shinFootFactor * (float)Math.cos(angle), 0.42F, 6, latheLoft.getMax(13227744));
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.earL);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, -1.6F, 0.0F);
      int shinFootSize = shinFootFloats.length;
      float[] floats = new float[3 * shinFootSize];
      float[] floats2 = new float[shinFootSize];
      float[] crownForeValues = new float[shinFootSize];

      for (int i = 0; i < shinFootSize; i++) {
         floats[3 * i + 1] = shinFootFloats[i][0];
         floats[3 * i + 2] = shinFootFloats[i][1];
         floats2[i] = shinFootFloats[i][2];
         crownForeValues[i] = shinFootFloats[i][3];
      }

      latheLoft.getLoftPath(floats, floats2, crownForeValues, 20, latheLoft.getClassForRegion(max3), null, shinBrr, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoft(0.0F, 28.6F, -8.0F, 4.9F, 2.7F, 2.0F, 18, latheLoft.getClassForRegion(max4));
      ShinBrr shinBrr5 = latheLoft.getMax(15921128);
      ShinBrr shinBrr6 = latheLoft.getMax(3810326);
      ShinBrr shinBrr7 = latheLoft.getMax(788486);
      ShinBrr max8 = latheLoft.getMax(16777215);
      latheLoft.getLatheLoftForForeEar(this.eyeRightSection);
      handle(latheLoft, 1.0F, shinBrr5, shinBrr6, shinBrr7, max8);
      latheLoft.getLatheLoftForForeEar(this.eyeLeftSection);
      handle(latheLoft, -1.0F, shinBrr5, shinBrr6, shinBrr7, max8);
      latheLoft.getLatheLoftForForeEar(this.earL);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max6);
      handleForLatheLoftFloat(latheLoft, 1.0F, classForRegion3);
      handleForLatheLoftFloat(latheLoft, -1.0F, classForRegion3);
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      ShinBrr max9 = latheLoft.getMax(1708560);
      handle(latheLoft, 1.0F, shinBrr, max9);
      latheLoft.getLatheLoftForForeEar(this.foreEar);
      handle(latheLoft, -1.0F, latheLoft.getMax(3814707), max9);
      latheLoft.getLatheLoftForForeEar(this.lidRingForeEar);
      latheLoft.getLatheLoft(0.0F, 26.7F, -7.9F, 3.4F, 0.9F, 1.9F, 10, latheLoft.getMax(14522262));
      latheLoft.getLatheLoft(0.0F, 27.3F, -7.8F, 2.8F, 0.7F, 1.6F, 8, latheLoft.getMax(4857374));
      latheLoft.getLatheLoft(0.0F, 27.25F, -7.9F, 1.6F, 0.4F, 1.3F, 8, latheLoft.getMax(13793914));
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handleForLatheLoftFloatShinBrr(latheLoft, 1.0F, shinBrr2, shinBrr4);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      handleForLatheLoftFloatShinBrr(latheLoft, -1.0F, shinBrr, shinBrr4);
      latheLoft.getDequeArray();
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max5);
      latheLoft.getLatheLoftForForeEar(this.shinR);
      handle3(latheLoft, 1.0F, shinBrr3);
      latheLoft.getLatheLoftForForeEar(this.footR);
      handle(latheLoft, 1.0F, shinBrr3);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      handleAmounts(latheLoft, 1.0F, classForRegion4, classForRegion, shinBrr3);
      latheLoft.getLatheLoftForForeEar(this.shinL);
      handle3(latheLoft, -1.0F, shinBrr3);
      latheLoft.getLatheLoftForForeEar(this.footL);
      handle(latheLoft, -1.0F, shinBrr3);
      latheLoft.getLatheLoftForForeEar(this.footLeftSection);
      handleAmounts(latheLoft, -1.0F, classForRegion4, classForRegion, shinBrr3);
   }

   private static void handle(LatheLoft latheLoft, float lidRingValue, ShinBrr shinBrr) {
      float lidRingValue2 = lidRingValue * 5.2F;
      float lidRingValue3 = lidRingValue * 5.3F;
      latheLoft.getLatheLoft(new float[]{lidRingValue2, 6.2F, 0.5F, lidRingValue3, 4.3F, 1.1F, lidRingValue3, 2.5F, 0.8F}, new float[]{2.25F, 2.35F, 1.55F}, 10, shinBrr, false);
   }

   @Override
   public String formatTungDinString() {
      return "La Vaca Saturno Saturnita";
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue / 96.0F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15578033), ForeShinUtils.getValueForInt(14522262)).getBonecaAmbalabu(0.03F, 4.0F, 31);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15975102), 0.35F, 6.0F, 37);
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.3F, bonecaAmbalabu.ambalabuBonecaValue * 0.13F, bonecaAmbalabu.bonecaValue * 0.09F, ForeShinUtils.getValueForIntFloat(16777215, 0.12F));

      for (int i = -1; i <= 1; i += 2) {
         float lidRingValue = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + i * 0.145F);
         float bonecaAmbalabuValue3 = bonecaAmbalabu.bonecaValue * 0.4F;
         float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue * 0.06F;
         float rocketsElytraBound = bonecaAmbalabu.bonecaValue * 0.11F;
         float sin = (float)Math.sin(Math.toRadians(25.0));
         float cos = (float)Math.cos(Math.toRadians(25.0));
         float classFactor = lidRingValue - i * sin * rocketsElytraBound * 0.8F;
         float bonecaAmbalabuValue5 = bonecaAmbalabuValue3 + cos * rocketsElytraBound * 0.8F;
         handle(bonecaAmbalabu, lidRingValue, bonecaAmbalabuValue3, bonecaAmbalabuFactor * 1.18F, rocketsElytraBound * 1.12F, i, ForeShinUtils.getValueForIntFloat(12614264, 0.7F));
         bonecaAmbalabu.getClassForFloatInt(
            classFactor,
            bonecaAmbalabuValue5,
            classFactor - i * bonecaAmbalabuFactor * 0.1F,
            bonecaAmbalabuValue5 + rocketsElytraBound * 0.7F,
            classFactor - i * bonecaAmbalabuFactor * 0.95F,
            bonecaAmbalabuValue5 + rocketsElytraBound * 0.62F,
            bonecaAmbalabuFactor * 0.9F,
            ForeShinUtils.getValueForIntFloat(12614264, 0.6F)
         );
         handle(bonecaAmbalabu, lidRingValue, bonecaAmbalabuValue3, bonecaAmbalabuFactor, rocketsElytraBound, i, ForeShinUtils.getValueForInt(10246746));
         bonecaAmbalabu.getClassForFloatInt(
            classFactor,
            bonecaAmbalabuValue5 - rocketsElytraBound * 0.1F,
            classFactor - i * bonecaAmbalabuFactor * 0.08F,
            bonecaAmbalabuValue5 + rocketsElytraBound * 0.62F,
            classFactor - i * bonecaAmbalabuFactor * 0.9F,
            bonecaAmbalabuValue5 + rocketsElytraBound * 0.56F,
            bonecaAmbalabuFactor * 0.6F,
            ForeShinUtils.getValueForInt(10246746)
         );
         handle(bonecaAmbalabu, lidRingValue + i * bonecaAmbalabuFactor * 0.12F, bonecaAmbalabuValue3 + rocketsElytraBound * 0.1F, bonecaAmbalabuFactor * 0.52F, rocketsElytraBound * 0.62F, i, ForeShinUtils.getValueForInt(5909034));
      }

      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(13207686, 0.45F), 0.25F, 0.9F * bonecaAmbalabuValue, 39);
      bonecaAmbalabu.getClass02(
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.48F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.8F, 1.4F * bonecaAmbalabuValue, ForeShinUtils.getValueForIntFloat(11561578, 0.6F)
      );
   }

   private static float[] getAmounts(int index, float angle) {
      float cos = (float)Math.cos(angle);
      float lidRingFactor = shinFootFloats[index][3] * cos;
      return new float[]{shinFootFloats[index][2] * (float)Math.sin(angle), shinFootFloats[index][0] + floats[index][0] * lidRingFactor, shinFootFloats[index][1] + floats[index][1] * lidRingFactor};
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float abs2 = Math.abs(bonecaAmbalabuValue - 0.5F);
         int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14726031), ForeShinUtils.getValueForInt(13211770), getAmount((abs2 - 0.3F) / 0.12F));

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, value2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 5.0F, 51);

      for (int k = -2; k <= 2; k++) {
         bonecaAmbalabu.getClass02(
            bonecaAmbalabu.ambalabuBonecaValue * (0.5F + k * 0.045F),
            bonecaAmbalabu.bonecaValue * 0.55F,
            bonecaAmbalabu.ambalabuBonecaValue * (0.5F + k * 0.06F),
            bonecaAmbalabu.bonecaValue * 0.97F,
            1.2F,
            ForeShinUtils.getValueForIntFloat(13212280, 0.35F)
         );
      }
   }

   private static float getAmount(float angle, float lidRingValue) {
      return lidRingValue * 5.0F * (float)Math.sin(angle) + 2.3999996F * ((float)Math.cos(angle) - 1.0F);
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float shinProgressValue = poseProgress.sneakingPose ? 1.0F : 0.0F;
      float shinProgressFactor2 = poseProgress.sneakingPose ? 0.3F : 0.42F;
      float shinProgressFactor3 = poseProgress.swingProgress > 0.0F ? poseProgress.getSwingProgress() : 0.0F;
      float shinProgressFactor4 = poseProgress.sneakingPose ? 0.55F : 1.0F;
      float shinProgressFactor5 = Math.max(0.0F, -sin) * shinProgressFactor * shinProgressFactor4;
      float shinProgressFactor6 = Math.max(0.0F, sin) * shinProgressFactor * shinProgressFactor4;
      float shinProgressValue2 = shinProgressFactor2 * cos * shinProgressFactor + 0.6F * shinProgressFactor5 + 0.5F * shinProgressValue;
      float shinProgressValue3 = -1.15F * shinProgressFactor5 - shinProgressValue;
      float shinProgressValue4 = -shinProgressFactor2 * cos * shinProgressFactor + 0.6F * shinProgressFactor6 + 0.5F * shinProgressValue;
      float foreShinValue = -1.15F * shinProgressFactor6 - shinProgressValue;
      float shinProgressValue6 = -0.2F * (float)Math.sin(angle + 0.5F) * shinProgressFactor;
      float dinForeValue = 0.1F * cos * shinProgressFactor;
      float shinProgressBound = getAmountForFloat(shinProgressValue2, shinProgressValue2 + shinProgressValue3) + getAmount(shinProgressValue6, 1.0F);
      float hashHuntBound = getAmountForFloat(shinProgressValue4, shinProgressValue4 + foreShinValue) + getAmount(shinProgressValue6, -1.0F);
      float shinProgressValue8 = 0.24F * shinProgressFactor3;
      foreShinScale.getAmounts(this.shinR, shinProgressValue2, -dinForeValue, -shinProgressValue6);
      foreShinScale.getAmounts(this.footR, shinProgressValue3, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classShinProgress, -(shinProgressValue2 + shinProgressValue3) - shinProgressValue8, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinL, shinProgressValue4, -dinForeValue, -shinProgressValue6);
      foreShinScale.getAmounts(this.footL, foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footLeftSection, -(shinProgressValue4 + foreShinValue) - shinProgressValue8, 0.0F, 0.0F);
      foreShinScale.getAmountsForeShin2(this.planet, 0.0F, -Math.min(shinProgressBound, hashHuntBound) + 2.2F * shinProgressFactor3, 0.0F);
      foreShinScale.getAmounts(this.planet, 0.0F, dinForeValue, shinProgressValue6);
      float shinProgressValue9 = (float)Math.pow(Math.abs(cos), 8.0) * shinProgressFactor;
      float shinProgressValue10 = 0.008F * poseProgress.getAgeCode(70.0F, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(
         this.head, 1.0F + 0.06F * shinProgressValue9 + shinProgressValue10 - 0.03F * shinProgressFactor3, 1.0F - 0.09F * shinProgressValue9 + shinProgressValue10 + 0.05F * shinProgressFactor3, 1.0F + 0.06F * shinProgressValue9 + shinProgressValue10 - 0.03F * shinProgressFactor3
      );
      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsBoneCount(this.head, -0.12F, 0.0F, 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.head, 1.06F, 0.94F, 1.06F);
      }

      float radians2 = (float)Math.toRadians(-18.0);
      float radians3 = (float)Math.toRadians(-18.0);
      float shinProgressFactor7 = 0.04F * shinProgressFactor + 0.09F * shinProgressValue9;
      float radians4 = poseProgress.getRadians(1.4F);
      if (poseProgress.swingProgress > 0.0F) {
         radians4 += (float) (Math.PI * 2) * getAmount(poseProgress.swingProgress);
      }

      foreShinScale.getAmounts(
         this.ringSpin,
         radians2 + shinProgressFactor7 * (float)Math.sin(2.0F * angle) + 0.015F * poseProgress.getAgeCode(90.0F, 0.5F) + 0.35F * shinProgressFactor3,
         0.0F,
         radians3 + shinProgressFactor7 * (float)Math.cos(2.0F * angle) + 0.01F * poseProgress.getAgeCode(120.0F, 2.0F)
      );
      foreShinScale.getAmounts(this.ringUnx, 0.0F, radians4, 0.0F);
      foreShinScale.getAmounts(this.ring, -radians2, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.ringSection, 0.0F, 0.0F, -radians3);
      float shinProgressFactor8 = poseProgress.poseProgressActive ? 0.0F : (float)Math.sin((poseProgress.age + (poseProgress.hashCode & 63)) * 0.45F);
      foreShinScale.getAmounts(
         this.earL, 0.03F * poseProgress.getAgeCode(80.0F, 0.0F) + 0.05F * shinProgressValue9 + 0.4F * shinProgressFactor3, -dinForeValue * 0.7F, 0.015F * poseProgress.getAgeCode(130.0F, 1.0F) - shinProgressValue6 * 0.6F
      );
      foreShinScale.getAmountsBoneCount(this.earL, Math.max(-0.28F, Math.min(0.6F, poseProgress.shinProgressAmount * 0.8F)) * (1.0F - shinProgressFactor3), poseProgress.headYawRadians * 0.9F, 0.0F);
      foreShinScale.getAmounts(this.lidRingForeEar, -0.01F * shinProgressFactor8 * (1.0F - shinProgressFactor3) - 0.55F * shinProgressFactor3, 0.07F * shinProgressFactor8 * (1.0F - shinProgressFactor3), 0.0F);
      foreShinScale.getAmountsForeShin2(this.lidRingForeEar, 0.0F, -0.3F * shinProgressFactor3, 0.0F);
      foreShinScale.getAmounts(this.shinFootForeEar, 0.0F, 0.05F * poseProgress.getAgeCode(60.0F, 0.0F), -0.08F + 0.45F * getAgeCode(poseProgress, 0.0F) + 0.4F * shinProgressFactor3 + 0.12F * shinProgressValue9);
      foreShinScale.getAmounts(this.classShinFoot, 0.0F, -0.05F * poseProgress.getAgeCode(60.0F, 1.0F), 0.08F - 0.45F * getAgeCode(poseProgress, 31.0F) - 0.4F * shinProgressFactor3 - 0.12F * shinProgressValue9);
      float shinProgressValue11 = (poseProgress.age + (poseProgress.hashCode & 0xFF) * 0.61F) % 97.0F;
      float shinProgressFactor9 = shinProgressValue11 < 6.0F ? (float)Math.sin(shinProgressValue11 / 6.0F * Math.PI) : 0.0F;
      foreShinScale.getAmounts(this.lidR, 0.0F, radians, 0.0F);
      foreShinScale.getAmounts(this.lidRSkin, -0.3F * shinProgressFactor9, 0.0F, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(this.eyeRightSection, 1.0F, 1.0F - 0.88F * shinProgressFactor9, 1.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, 0.0F, -radians, 0.0F);
      foreShinScale.getAmounts(this.lidL, 0.0F, -radians, 0.0F);
      foreShinScale.getAmounts(this.lidLSkin, -0.3F * shinProgressFactor9, 0.0F, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(this.eyeLeftSection, 1.0F, 1.0F - 0.88F * shinProgressFactor9, 1.0F);
      foreShinScale.getAmounts(this.foreEar, 0.0F, radians, 0.0F);
      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.shinR, 1.2F, -0.2F, 0.0F);
         foreShinScale.getAmounts(this.footR, -0.5F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.classShinProgress, -0.5F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinL, 1.2F, 0.2F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.5F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footLeftSection, -0.5F, 0.0F, 0.0F);
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         foreShinScale.getAmounts(this.shinR, -0.25F, 0.0F, 0.05F);
         foreShinScale.getAmounts(this.shinL, -0.25F, 0.0F, -0.05F);
         foreShinScale.getAmounts(this.footR, 0.0F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, 0.0F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.classShinProgress, -0.7F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footLeftSection, -0.7F, 0.0F, 0.0F);
      }
   }

   @Override
   public float getAmountFloat() {
      return 0.55F;
   }

   private static void handleBonecaAmbalabuBonecaAmbalabuTable(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuLimit = 8.7F;
      float bonecaAmbalabuValue = 0.34F;
      float bonecaAmbalabuFactor = 2.0F * bonecaAmbalabuLimit + bonecaAmbalabuValue;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue2 = (float)i / (bonecaAmbalabu.bonecaValue - 1);
         float bonecaAmbalabuValue3 = (1.0F - bonecaAmbalabuValue2) * bonecaAmbalabuFactor;
         float lidRingLimit;
         if (bonecaAmbalabuValue3 < bonecaAmbalabuLimit) {
            lidRingLimit = 12.7F + bonecaAmbalabuValue3;
         } else if (bonecaAmbalabuValue3 < bonecaAmbalabuLimit + bonecaAmbalabuValue) {
            lidRingLimit = 21.4F;
         } else {
            lidRingLimit = 21.4F - (bonecaAmbalabuValue3 - bonecaAmbalabuLimit - bonecaAmbalabuValue);
         }

         int foreShinValue = getValue(lidRingLimit);
         boolean bonecaAmbalabuFlag = lidRingLimit > 14.0F && lidRingLimit < 17.7F;

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            if (foreShinValue == 0) {
               bonecaAmbalabu.getBonecaAmbalabu(j, i, 0);
            } else {
               float bonecaAmbalabuFactor2 = (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
               float foreShinFactor = 1.0F + 0.05F * (float)Math.sin((float) (Math.PI * 6) * bonecaAmbalabuFactor2 + lidRingLimit * 0.6F) + 0.03F * (float)Math.sin(43.9823F * bonecaAmbalabuFactor2 + 1.1F);
               if (bonecaAmbalabuFlag) {
                  for (float bonecaAmbalabuTableItem : bonecaAmbalabuTable) {
                     float bonecaAmbalabuBound = Math.abs(bonecaAmbalabuFactor2 - bonecaAmbalabuTableItem - (lidRingLimit - 14.0F) * 0.004F);
                     float min7 = Math.min(bonecaAmbalabuBound, 1.0F - bonecaAmbalabuBound);
                     foreShinFactor *= 1.0F - 0.15F * getAmount(1.0F - min7 / 0.028F);
                  }
               }

               bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(foreShinValue, foreShinFactor));
            }
         }
      }
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float lidRingValue, ShinBrr shinBrr) {
      float[] floats = new float[]{lidRingValue * 2.7F, 37.6F, 0.4F, lidRingValue * 4.3F, 38.05F, 0.45F, lidRingValue * 5.45F, 38.75F, 0.1F, lidRingValue * 5.9F, 39.25F, -0.6F};
      latheLoft.getLatheLoft(floats, new float[]{1.0F, 0.78F, 0.5F, 0.12F}, 8, shinBrr, false);
   }

   private static float[][] getAmounts() {
      byte lidRingDivisor = 4;
      float[][] floats = new float[lidRingDivisor + lidRingFloats.length][];
      int lidRingCounter = 0;
      float[] lidRingValues = lidRingFloats[0];

      for (int i = lidRingDivisor; i >= 1; i--) {
         double angle = (Math.PI / 2) * i / lidRingDivisor;
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         floats[lidRingCounter++] = new float[]{lidRingValues[0] + 0.9F * sin, lidRingValues[1] + 0.35F * sin, Math.max(0.002F, lidRingValues[2] * cos), Math.max(0.002F, lidRingValues[3] * cos)};
      }

      for (float[] lidRingFloatsItem : lidRingFloats) {
         floats[lidRingCounter++] = (float[])lidRingFloatsItem.clone();
      }

      return floats;
   }

   private static void handleAmounts(LatheLoft latheLoft, float lidRingFactor, ShinBrr shinBrr, ShinBrr classForRegion, ShinBrr shinBrr3) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(lidRingFactor * 5.3F, 0.0F, 0.8F).getLatheLoft4(-lidRingFactor * 13.0F);
      float[] floats = new float[]{
         0.0F,
         1.5F,
         3.3F,
         0.0F,
         1.5F,
         3.15F,
         0.0F,
         1.55F,
         2.8F,
         0.0F,
         1.6F,
         2.25F,
         0.0F,
         1.92F,
         1.4F,
         0.0F,
         2.22F,
         -0.6F,
         0.0F,
         1.88F,
         -3.6F,
         0.0F,
         1.38F,
         -6.3F,
         0.0F,
         1.03F,
         -7.7F
      };
      latheLoft.getLoftPath(
         floats,
         new float[]{0.05F, 0.7F, 1.2F, 1.5F, 1.8F, 2.2F, 2.9F, 3.1F, 2.85F},
         new float[]{0.05F, 0.7F, 1.2F, 1.55F, 1.9F, 2.2F, 1.85F, 1.35F, 1.0F},
         12,
         shinBrr,
         null,
         shinBrr,
         0.0F,
         1.0F,
         0.0F
      );

      for (float[] lidRingValues : RING_PART_PARAMS) {
         latheLoft.getLatheLoft(lidRingFactor * lidRingValues[0], lidRingValues[1], lidRingValues[2], lidRingValues[3], lidRingValues[4], lidRingValues[5], lidRingValues == RING_PART_PARAMS[0] ? 8 : 6, classForRegion);
      }

      if (lidRingFactor > 0.0F) {
         latheLoft.getLatheLoft(3.85F, 0.7F, -6.6F, 0.62F, 0.68F, 0.95F, 6, classForRegion);
      }

      latheLoft.getDequeArray();
      latheLoft.getLatheLoft(lidRingFactor * 6.6000004F, 2.6F, 0.9F, 0.55F, 0.6F, 0.6F, 6, shinBrr3);
   }

   private static void handleBonecaAmbalabuCUMULATIVE_LENGTHS(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16052714)).getBonecaAmbalabu(0.03F, 6.0F, 21);
      float bonecaAmbalabuLimit = getAmount3(0.0F, 32.4F);
      float bonecaAmbalabuValue = getAmount3(0.0F, 30.2F);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue2 = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         if (!(bonecaAmbalabuValue2 < bonecaAmbalabuLimit)) {
            float min3 = Math.min(1.0F, (bonecaAmbalabuValue2 - bonecaAmbalabuLimit) / (bonecaAmbalabuValue - bonecaAmbalabuLimit));

            for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
               float bonecaAmbalabuFactor = getAmount(1.0F - Math.abs((j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) / 0.3F);
               bonecaAmbalabu.getBonecaAmbalabu(
                  j, i, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), ForeShinUtils.getValueForInt(15850195), min3 * 0.85F * bonecaAmbalabuFactor)
               );
            }
         }
      }

      float bonecaAmbalabuValue4 = bonecaAmbalabu.ambalabuBonecaValue / 30.78761F;
      float bonecaAmbalabuValue5 = bonecaAmbalabu.bonecaValue / CUMULATIVE_LENGTHS[CUMULATIVE_LENGTHS.length - 1];
      float[] bonecaAmbalabuValues = getAmounts(1.0F);
      float[] amounts2 = getAmounts(-1.0F);
      float bonecaAmbalabuValue6 = bonecaAmbalabuValues[4] * bonecaAmbalabu.ambalabuBonecaValue;
      float amountForFloat = bonecaAmbalabuValues[5] * bonecaAmbalabu.bonecaValue;
      float classValue = amounts2[4] * bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaAmbalabuValue9 = amounts2[5] * bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue6, amountForFloat, 2.3F * bonecaAmbalabuValue4, 2.2F * bonecaAmbalabuValue5, ForeShinUtils.getValueForIntFloat(12103590, 0.22F));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue6, amountForFloat, 2.05F * bonecaAmbalabuValue4, 1.95F * bonecaAmbalabuValue5, ForeShinUtils.getValueForIntFloat(12103590, 0.22F));
      int valueForInt = ForeShinUtils.getValueForInt(1842204);
      bonecaAmbalabu.getClass02(classValue - 0.4F * bonecaAmbalabuValue4, bonecaAmbalabuValue9 - 0.2F * bonecaAmbalabuValue5, 2.7F * bonecaAmbalabuValue4, 0.28F, 3, valueForInt);
      bonecaAmbalabu.getClass022(classValue, bonecaAmbalabuValue9 - 1.0F * bonecaAmbalabuValue5, 2.2F * bonecaAmbalabuValue4, 2.3F * bonecaAmbalabuValue5, valueForInt);
      bonecaAmbalabu.getClass02(classValue - 1.6F * bonecaAmbalabuValue4, bonecaAmbalabuValue9 - 1.8F * bonecaAmbalabuValue5, 2.0F * bonecaAmbalabuValue4, 0.35F, 5, valueForInt);
      bonecaAmbalabu.getClass02(classValue - 1.2F * bonecaAmbalabuValue4, bonecaAmbalabuValue9 + 1.4F * bonecaAmbalabuValue5, 1.5F * bonecaAmbalabuValue4, 0.3F, 8, valueForInt);
      float bonecaAmbalabuValue11 = 0.86F * bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaAmbalabuValue12 = getAmounts(36.4F, 4) * bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getClass02(bonecaAmbalabuValue11, bonecaAmbalabuValue12, 2.2F * bonecaAmbalabuValue4, 0.3F, 11, valueForInt);
      bonecaAmbalabu.getClass02(bonecaAmbalabuValue11 - 1.4F * bonecaAmbalabuValue4, bonecaAmbalabuValue12 - 1.6F * bonecaAmbalabuValue5, 1.5F * bonecaAmbalabuValue4, 0.35F, 13, valueForInt);
      bonecaAmbalabu.getClass02(0.12F * bonecaAmbalabu.ambalabuBonecaValue, getAmounts(35.2F, 4) * bonecaAmbalabu.bonecaValue + 1.5F * bonecaAmbalabuValue5, 1.6F * bonecaAmbalabuValue4, 0.35F, 17, valueForInt);
      bonecaAmbalabu.getClass02(0.93F * bonecaAmbalabu.ambalabuBonecaValue, getAmounts(33.5F, 4) * bonecaAmbalabu.bonecaValue, 2.4F * bonecaAmbalabuValue4, 0.35F, 19, valueForInt);
   }

   private static float[] getAmounts(float lidRingValue, float lidRingLimit) {
      for (int i = 0; i + 1 < shinFootFloats.length; i++) {
         float[] lidRingValues = getAmounts(i, lidRingValue);
         float[] amounts2 = getAmounts(i + 1, lidRingValue);
         if (lidRingLimit <= lidRingValues[1] && lidRingLimit >= amounts2[1]) {
            float lidRingFactor = (lidRingValues[1] - lidRingLimit) / Math.max(1.0E-4F, lidRingValues[1] - amounts2[1]);
            float lidRingValue2 = (CUMULATIVE_LENGTHS[i] + (CUMULATIVE_LENGTHS[i + 1] - CUMULATIVE_LENGTHS[i]) * lidRingFactor) / CUMULATIVE_LENGTHS[CUMULATIVE_LENGTHS.length - 1];
            return new float[]{lidRingValues[0] + (amounts2[0] - lidRingValues[0]) * lidRingFactor, lidRingLimit, lidRingValues[2] + (amounts2[2] - lidRingValues[2]) * lidRingFactor, lidRingValue2};
         }
      }

      return getAmounts(shinFootFloats.length - 1, lidRingValue);
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float lidRingValue, float lidRingValue2, float lidRingBound, float rocketsElytraBound, int lidRingFactor, int bonecaAmbalabuValue) {
      float lidRingFactor2 = lidRingFactor * (float)Math.sin(Math.toRadians(25.0));
      float cos = (float)Math.cos(Math.toRadians(25.0));
      float max4 = Math.max(lidRingBound, rocketsElytraBound);
      bonecaAmbalabu.getBonecaAmbalabu(lidRingValue - max4, lidRingValue2 - max4, lidRingValue + max4, lidRingValue2 + max4, bonecaAmbalabuValue, (lidRingValue5, lidRingValue6) -> {
         float var8x = lidRingValue5 - lidRingValue;
         float var9x = lidRingValue6 - lidRingValue2;
         float lidRingFactor3 = (var8x * lidRingFactor2 - var9x * cos) / rocketsElytraBound;
         float lidRingFactor4 = (var8x * cos + var9x * lidRingFactor2) / lidRingBound;
         return lidRingFactor3 * lidRingFactor3 + lidRingFactor4 * lidRingFactor4 <= 1.0F;
      });
   }

   private static void handleForLatheLoftFloatShinBrr(LatheLoft latheLoft, float lidRingValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float innerScaled = lidRingValue * 4.2F;
      float lidRingValue2 = 36.2F;
      float[] floats = new float[]{
         innerScaled, lidRingValue2, 0.9F, innerScaled + lidRingValue * 1.6F, lidRingValue2 - 0.3F, 1.4F, innerScaled + lidRingValue * 3.1F, lidRingValue2 - 0.8F, 1.95F, innerScaled + lidRingValue * 4.3F, lidRingValue2 - 1.4F, 2.5F
      };
      latheLoft.getLoftPath(floats, new float[]{0.7F, 1.6F, 1.4F, 0.1F}, new float[]{0.45F, 0.36F, 0.28F, 0.05F}, 9, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      float[] loftPathValues = new float[]{
         innerScaled + lidRingValue * 0.53F,
         lidRingValue2 - 0.11F,
         0.81F,
         innerScaled + lidRingValue * 1.78F,
         lidRingValue2 - 0.36F,
         1.17F,
         innerScaled + lidRingValue * 3.08F,
         lidRingValue2 - 0.8F,
         1.64F,
         innerScaled + lidRingValue * 3.82F,
         lidRingValue2 - 1.13F,
         2.03F
      };
      latheLoft.getLoftPath(loftPathValues, new float[]{0.55F, 1.36F, 1.15F, 0.15F}, new float[]{0.16F, 0.16F, 0.13F, 0.06F}, 8, shinBrr2, null, null, 0.0F, 0.0F, -1.0F);
   }

   private static int getValue(float lidRingLimit) {
      if (lidRingLimit >= 17.7F && lidRingLimit <= 18.5F) {
         return 0;
      } else {
         int lidRingValue;
         if (lidRingLimit < 14.0F) {
            lidRingValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(7235158), ForeShinUtils.getValueForInt(9406064), (lidRingLimit - 12.7F) / 1.3000002F);
         } else if (lidRingLimit < 17.7F) {
            float lidRingValue2 = (lidRingLimit - 14.0F) / 3.7000008F;
            lidRingValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13878690), ForeShinUtils.getValueForInt(15127462), Math.min(1.0F, lidRingValue2 * 2.2F));
         } else {
            float lidRingFactor = (lidRingLimit - 18.5F) / 2.8999996F;
            lidRingValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13352346), ForeShinUtils.getValueForInt(11127008), lidRingFactor * lidRingFactor * 0.3F);
         }

         if (Math.abs(lidRingLimit - 20.6F) < 0.09F) {
            lidRingValue = ForeShinUtils.getValue(lidRingValue, 0.55F);
         }

         return ForeShinUtils.getValue(lidRingValue, 1.0F + 0.05F * (float)Math.sin(lidRingLimit * 11.0F) + 0.03F * (float)Math.sin(lidRingLimit * 29.0F));
      }
   }

   private static float getAmountForFloat(float angle, float angle2) {
      return 9.9F - 6.2F * (float)Math.cos(angle) - 0.19999999F * (float)Math.sin(angle) - 3.6999998F * (float)Math.cos(angle2) - 0.3F * (float)Math.sin(angle2);
   }

   private static float[] getAmounts(float lidRingFactor) {
      float lidRingValue = lidRingFactor * (float)Math.toRadians(37.0);
      float lidRingValue2 = 0.02F;
      float[] lidRingValues = getAmounts(lidRingValue, 34.6F);
      float[] amounts2 = getAmounts(lidRingValue + lidRingValue2, 34.6F);
      float[] amounts3 = getAmounts(lidRingValue - lidRingValue2, 34.6F);
      float[] amounts4 = getAmounts(lidRingValue, 34.699997F);
      float[] amounts5 = getAmounts(lidRingValue, 34.5F);
      float lidRingFactor2 = amounts2[0] - amounts3[0];
      float lidRingFactor3 = amounts2[1] - amounts3[1];
      float lidRingFactor4 = amounts2[2] - amounts3[2];
      float lidRingFactor5 = amounts4[0] - amounts5[0];
      float lidRingFactor6 = amounts4[1] - amounts5[1];
      float lidRingFactor7 = amounts4[2] - amounts5[2];
      float lidRingFactor8 = lidRingFactor3 * lidRingFactor7 - lidRingFactor4 * lidRingFactor6;
      float lidRingFactor9 = lidRingFactor4 * lidRingFactor5 - lidRingFactor2 * lidRingFactor7;
      float lidRingFactor10 = lidRingFactor2 * lidRingFactor6 - lidRingFactor3 * lidRingFactor5;
      float distance = (float)Math.sqrt(lidRingFactor8 * lidRingFactor8 + lidRingFactor9 * lidRingFactor9 + lidRingFactor10 * lidRingFactor10);
      if (lidRingFactor10 > 0.0F) {
         distance = -distance;
      }

      float y = lidRingFactor8 / distance;
      float lidRingValue3 = lidRingFactor9 / distance;
      float lidRingValue4 = lidRingFactor10 / distance;
      float lidRingValue5 = lidRingValues[0] - y * 0.62F;
      float lidRingValue6 = 34.6F - lidRingValue3 * 0.62F;
      float lidRingValue7 = lidRingValues[2] - lidRingValue4 * 0.62F;
      float degrees = -((float)Math.toDegrees(Math.atan2(y, -lidRingValue4)));
      return new float[]{lidRingValue5, lidRingValue6, lidRingValue7, degrees, 0.5F + lidRingValue / (float) (Math.PI * 2), lidRingValues[3]};
   }

   @Override
   public int getValue() {
      return 1;
   }

   private static float getAmounts(float lidRingLimit, int index) {
      for (int i = 0; i + 1 < shinFootFloats.length; i++) {
         float lidRingLimit2 = shinFootFloats[i][0];
         float lidRingValue = shinFootFloats[i + 1][0];
         if (lidRingLimit <= lidRingLimit2 && lidRingLimit >= lidRingValue) {
            float lidRingFactor = (lidRingLimit2 - lidRingLimit) / Math.max(1.0E-4F, lidRingLimit2 - lidRingValue);
            if (index < 4) {
               return shinFootFloats[i][index] + (shinFootFloats[i + 1][index] - shinFootFloats[i][index]) * lidRingFactor;
            }

            return (CUMULATIVE_LENGTHS[i] + (CUMULATIVE_LENGTHS[i + 1] - CUMULATIVE_LENGTHS[i]) * lidRingFactor) / CUMULATIVE_LENGTHS[CUMULATIVE_LENGTHS.length - 1];
         }
      }

      boolean lidRingFlag = lidRingLimit > shinFootFloats[0][0];
      if (index < 4) {
         return shinFootFloats[lidRingFlag ? 0 : shinFootFloats.length - 1][index];
      } else {
         return lidRingFlag ? 0.0F : 1.0F;
      }
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuFactor = (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
            float bonecaAmbalabuBound = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
            float bonecaAmbalabuValue = 0.01F * (float)Math.sin((float) (Math.PI * 4) * bonecaAmbalabuFactor + bonecaAmbalabuBound * 23.0F)
               + 0.006F * (float)Math.sin(31.415928F * bonecaAmbalabuFactor + bonecaAmbalabuBound * 41.0F + 1.3F);
            int amountsForFloat = getAmountsForFloat(bonecaAmbalabuBound + bonecaAmbalabuValue);
            float bonecaAmbalabuFactor2 = getAmount((Math.min(bonecaAmbalabuBound, 1.0F - bonecaAmbalabuBound) - 0.03F) / 0.17F);
            float foreShinFactor = 1.0F + bonecaAmbalabuFactor2 * (0.022F * (float)Math.sin((bonecaAmbalabuBound + bonecaAmbalabuValue * 1.6F) * 160.0F) + 0.012F * (float)Math.sin((bonecaAmbalabuBound - bonecaAmbalabuValue) * 250.0F + 2.0F));
            if (bonecaAmbalabuBound < 0.1F) {
               foreShinFactor *= 0.82F + 1.8F * bonecaAmbalabuBound;
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(amountsForFloat, foreShinFactor));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.015F, 8.0F, 61);
   }

   private static float getAmount3(float lidRingValue, float lidRingLimit) {
      return getAmounts(lidRingValue, lidRingLimit)[3];
   }

   private static void handle(LatheLoft latheLoft, float lidRingValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      float[] lidRingValues = getAmounts(lidRingValue);
      float loftLatheFactor = 1.1F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(lidRingValues[0], lidRingValues[1], lidRingValues[2]).getLatheLoft4(lidRingValues[3] * 0.55F).getLatheLoftLatheLoftAmountLoft(-4.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 2.0F, 1.88F, loftLatheFactor, 12, shinBrr);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, -0.1F, 0.0F).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 2.02F, loftLatheFactor * 1.04F, 1.8988F, 12, 0.0F, 0.3F, shinBrr2);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 2.04F, loftLatheFactor * 1.075F, 1.9175999F, 12, 0.0F, 0.14F, shinBrr3);
      latheLoft.getDequeArray();
      latheLoft.getLoftLathe(0.48F, -0.32F, -loftLatheFactor * 1.06F, 0.2F, 0.2F, 8, shinBrr4);
      latheLoft.getDequeArray();
   }

   private static int getAmountsForFloat(float lidRingBound) {
      lidRingBound = Math.max(0.0F, Math.min(1.0F, lidRingBound));

      for (int i = 0; i + 1 < lidRingTable.length; i++) {
         if (lidRingBound <= lidRingTable[i + 1]) {
            float lidRingValue = getAmount((lidRingBound - lidRingTable[i]) / (lidRingTable[i + 1] - lidRingTable[i]));
            return ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(table[i]), ForeShinUtils.getValueForInt(table[i + 1]), lidRingValue);
         }
      }

      return ForeShinUtils.getValueForInt(table[table.length - 1]);
   }

   @Override
   public String formatTungDin() {
      return "la_vaca_saturno_saturnita";
   }

   private static void handle3(LatheLoft latheLoft, float lidRingValue, ShinBrr shinBrr) {
      float lidRingValue2 = lidRingValue * 5.0F;
      float latheLoftValue = lidRingValue * 5.2F;
      latheLoft.getLatheLoft(new float[]{lidRingValue2, 12.4F, 0.3F, lidRingValue * 5.1F, 9.6F, 0.4F, latheLoftValue, 6.2F, 0.5F}, new float[]{2.7F, 2.5F, 2.25F}, 10, shinBrr, false);
      latheLoft.getLatheLoft(latheLoftValue, 6.2F, 0.5F, 2.25F, 8, shinBrr);
   }

   private static float[][] getAmounts(float[][] floats) {
      int lidRingSize = floats.length;
      float[][] floats2 = new float[lidRingSize][];

      for (int i = 0; i < lidRingSize; i++) {
         int index = Math.max(0, i - 1);
         int min2 = Math.min(lidRingSize - 1, i + 1);
         float lidRingFactor = floats[min2][0] - floats[index][0];
         float lidRingFactor2 = floats[min2][1] - floats[index][1];
         float distance = (float)Math.sqrt(lidRingFactor * lidRingFactor + lidRingFactor2 * lidRingFactor2);
         float lidRingFactor3 = lidRingFactor / distance;
         float lidRingFactor4 = lidRingFactor2 / distance;
         float lidRingFactor5 = lidRingFactor4 * lidRingFactor3;
         float lidRingFactor6 = -1.0F + lidRingFactor4 * lidRingFactor4;
         float sqrt2 = (float)Math.sqrt(lidRingFactor5 * lidRingFactor5 + lidRingFactor6 * lidRingFactor6);
         floats2[i] = new float[]{lidRingFactor5 / sqrt2, lidRingFactor6 / sqrt2};
      }

      return floats2;
   }

   private static float getAmount(float lidRingFactor) {
      lidRingFactor = lidRingFactor < 0.0F ? 0.0F : (lidRingFactor > 1.0F ? 1.0F : lidRingFactor);
      return lidRingFactor * lidRingFactor * (3.0F - 2.0F * lidRingFactor);
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15524552), ForeShinUtils.getValueForInt(9274744), getAmount((bonecaAmbalabuValue - 0.5F) / 0.45F));

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, value2);
         }
      }
   }

   private static float getAgeCode(PoseProgress poseProgress, float ageCodeValue) {
      float ageCodeValue2 = (poseProgress.age + (poseProgress.hashCode & 127) + ageCodeValue) % 75.0F;
      return ageCodeValue2 < 7.0F ? (float)Math.sin(ageCodeValue2 / 7.0F * Math.PI) : 0.0F;
   }

   private static float[] getAmountsForFloatArray(float[][] floats) {
      float[] floats2 = new float[floats.length];

      for (int i = 1; i < floats.length; i++) {
         float lidRingFactor = floats[i][0] - floats[i - 1][0];
         float lidRingFactor2 = floats[i][1] - floats[i - 1][1];
         floats2[i] = floats2[i - 1] + (float)Math.sqrt(lidRingFactor * lidRingFactor + lidRingFactor2 * lidRingFactor2);
      }

      return floats2;
   }

   @Override
   public String formatNoteBlock() {
      return "entity.cow.ambient";
   }
}
