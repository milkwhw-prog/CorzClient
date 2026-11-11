package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class CrownFore extends WarmBrainrotBase {
   private ForeEar legL;
   private static final float[] shinFootTable = new float[]{0.0F, 2.4F, 3.8F, 4.6F, 5.05F, 5.2F, 5.1F, 4.8F, 4.3F, 3.7F, 2.9F, 0.0F};
   private ForeEar foreR;
   private static final float[] crownForeTable = new float[]{6.2F, 6.5F, 7.3F, 8.4F, 9.7F, 11.1F, 12.6F, 14.1F, 15.6F, 17.0F, 18.3F, 19.9F};
   private ForeEar armL;
   private ForeEar classShinFoot;
   private static final float[] bonecaAmbalabuTable = new float[]{4.5F, 4.55F, 4.6F, 4.5F, 4.35F, 3.85F};
   private static final float[] table = new float[]{17.2F, 19.5F, 20.9F, 22.6F, 24.2F, 25.3F};
   private ForeEar classShinProgress;
   private ForeEar shinProgressForeEar;
   private ForeEar shinFootForeEar;
   private ForeEar shinL;
   private ForeEar crownForeForeEar;
   private ForeEar classCrown;
   private ForeEar footR;
   private ForeEar foreEar;
   private ForeEar chunkBSection;
   private ForeEar foreL;
   private ForeEar crownOut;
   private static final float[] UPPER_PROFILE_RADII = new float[]{4.75F, 4.55F, 4.05F, 2.95F, 1.35F};
   private ForeEar footL;
   private ForeEar crownForeEar;
   private static final float[] UPPER_PROFILE_HEIGHTS = new float[]{22.5F, 23.9F, 25.1F, 26.2F, 26.85F};
   private ForeEar crown;
   private static final float[] LOWER_PROFILE_RADII = new float[]{4.6F, 4.3F, 3.9F, 3.6F};
   private ForeEar shinR;
   private static final float[] LOWER_PROFILE_HEIGHTS = new float[]{14.6F, 16.6F, 18.6F, 20.5F};

   private static float getAmount() {
      return getAmount3(0.98F, 23.15F) + 0.1F;
   }

   private static float getAmount(float crownForeValue) {
      float crownForeValue2 = crownForeValue - (float)Math.floor(crownForeValue);
      return 1.0F - 2.0F * Math.abs(crownForeValue2 - 0.5F);
   }

   private static float getAmountForFloat(float crownForeFactor) {
      crownForeFactor = crownForeFactor < 0.0F ? 0.0F : (crownForeFactor > 1.0F ? 1.0F : crownForeFactor);
      return crownForeFactor * crownForeFactor * (3.0F - 2.0F * crownForeFactor);
   }

   private void handleCrown(LatheLoft latheLoft, float crownValue) {
      ShinBrr shinBrr = latheLoft.getMax(9337960);
      LoftLatheFlag.Region region = latheLoft.getMax(48, 24);
      handleBonecaAmbalabu3(region.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr max2 = latheLoft.getMax(14209220);
      ShinBrr max3 = latheLoft.getMax(4864300);

      for (int i = -1; i <= 1; i += 2) {
         float latheLoftValue = i * 0.98F;
         float amountForFloat = LatheLoft.getAmountForFloat(latheLoftValue, 3.55F);
         latheLoft.getLatheLoftForForeEar(this.crown);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, 23.15F, crownValue).getLatheLoft4(amountForFloat * 0.75F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.06F, 0.3F, 0.28F, 0.22F, 8, shinBrr);
         latheLoft.getLatheLoft(0.0F, -0.02F, -0.17F, 0.44F, 0.41F, 0.22F, 8, classForRegion);
         latheLoft.getLoftLathe(0.1F, -0.06F, -0.4F, 0.045F, 0.045F, 6, max2);
         latheLoft.getDequeArray();
         latheLoft.getLatheLoftForForeEar(i > 0 ? this.crownForeEar : this.classCrown);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, 23.15F, crownValue).getLatheLoft4(amountForFloat);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.7F, 0.5F, 0.6F, 8, max3);
         latheLoft.getDequeArray();
      }
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr) {
      float[] floats = new float[]{3.6F, 4.0F, 4.3F, 4.1F};
      float[] floats2 = new float[]{23.5F, 22.8F, 22.1F, 21.5F};
      float[] floats3 = new float[]{-1.5F, -0.5F, 0.5F, 1.5F};
      float[] floats4 = new float[]{19.5F, 18.6F, 18.0F, 18.9F};

      for (int i = -1; i <= 1; i += 2) {
         for (int j = 0; j < 4; j++) {
            float crownForeValue = i * (floats[j] - 0.5F);
            float crownForeValue2 = i * (floats[j] + 0.55F + 0.1F * j);
            float crownForeValue3 = 0.5F * (floats2[j] + floats4[j]);
            float[] loftPathValues = new float[]{
               crownForeValue, floats2[j] + 0.4F, floats3[j], i * (floats[j] + 0.4F), crownForeValue3, floats3[j] + 0.15F, crownForeValue2, floats4[j], floats3[j] + 0.35F
            };
            float[] floats6 = new float[]{0.35F, 0.72F, 0.08F};
            float[] crownForeValues = new float[]{0.25F, 0.4F, 0.06F};
            latheLoft.getLoftPath(loftPathValues, floats6, crownForeValues, 5, shinBrr, null, null, i, 0.0F, 0.0F);
         }
      }
   }

   private static void handle(LatheLoft latheLoft, float degrees, float crownForeValue, float crownForeFactor, float crownForeValue2, float crownForeValue3, float crownForeFactor2, float crownForeValue4, int crownForeLimit, ShinBrr shinBrr) {
      double angle = Math.toRadians(degrees);
      float sin = (float)Math.sin(angle);
      float cos = -((float)Math.cos(angle));
      float[] floats = new float[3 * crownForeLimit];
      float crownForeTotal = sin * crownForeFactor2;
      float width2 = crownForeValue4;
      float crownForeTotal2 = 0.4F + cos * crownForeFactor2;
      float crownForeFactor3 = crownForeValue2 / (crownForeLimit - 1);

      for (int i = 0; i < crownForeLimit; i++) {
         floats[3 * i] = crownForeTotal;
         floats[3 * i + 1] = width2;
         floats[3 * i + 2] = crownForeTotal2;
         float crownForeFactor4 = (float)i / (crownForeLimit - 1);
         double radians = Math.toRadians(crownForeValue + crownForeFactor * crownForeFactor4 * crownForeFactor4);
         crownForeTotal += sin * (float)Math.sin(radians) * crownForeFactor3;
         width2 += (float)Math.cos(radians) * crownForeFactor3;
         crownForeTotal2 += cos * (float)Math.sin(radians) * crownForeFactor3;
      }

      float[] crownForeValues = LatheLoft.getAmounts(crownForeLimit, crownForeValue3 * 0.85F, crownForeValue3 * 1.1F, crownForeValue3 * 0.8F, crownForeValue3 * 0.4F, 0.03F);
      float[] amounts2 = LatheLoft.getAmounts(crownForeLimit, 0.16F, 0.13F, 0.1F, 0.06F, 0.02F);
      latheLoft.getLoftPath(floats, crownForeValues, amounts2, 4, shinBrr, null, null, sin, 0.0F, cos);
   }

   private static void handle(LatheLoft latheLoft, float crownForeValue, ShinBrr shinBrr) {
      float skinScaled = crownForeValue * 6.5F;
      float eyeValue = 4.6F;
      float dinForeValue = -0.4F;
      latheLoft.getLatheLoft(skinScaled, eyeValue, dinForeValue, 0.98F, 1.4F, 1.2F, 8, shinBrr);

      for (int i = 0; i < 4; i++) {
         float crownForeValue4 = dinForeValue - 0.8F + i * 0.54F;
         float crownForeValue5 = (i != 1 && i != 2 ? 0.86F : 1.0F) * 1.2F;
         float crownForeValue6 = skinScaled - crownForeValue * 0.12F;
         float[] floats = new float[]{
            crownForeValue6, eyeValue - 0.85F, crownForeValue4, crownForeValue6 - crownForeValue * 0.06F, eyeValue - 1.55F * crownForeValue5, crownForeValue4 - 0.3F, crownForeValue6 - crownForeValue * 0.5F, eyeValue - 2.15F * crownForeValue5, crownForeValue4 - 0.55F
         };
         float[] crownForeValues = new float[]{0.31F, 0.27F, 0.15F};
         latheLoft.getLoftPath(floats, crownForeValues, crownForeValues, 5, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      }

      float[] loftPathValues = new float[]{skinScaled - crownForeValue * 0.4F, eyeValue - 0.1F, dinForeValue - 1.05F, skinScaled - crownForeValue * 0.75F, eyeValue - 0.85F, dinForeValue - 1.5F};
      float[] floats4 = new float[]{0.3F, 0.15F};
      latheLoft.getLoftPath(loftPathValues, floats4, floats4, 5, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
   }

   private static float getAmount(float angle, float crownForeValue) {
      float crownForeFactor = -3.4F;
      float crownForeFactor2 = -0.45F;
      float crownForeFactor3 = -2.8999999F;
      float crownForeFactor4 = 0.35F;
      float angle2 = angle + crownForeValue;
      return -(crownForeFactor * (float)Math.cos(angle) - crownForeFactor2 * (float)Math.sin(angle) + crownForeFactor3 * (float)Math.cos(angle2) - crownForeFactor4 * (float)Math.sin(angle2));
   }

   private static void handle(LatheLoft latheLoft, float crownForeValue, ForeEar foreEar, ForeEar foreEar2, ForeEar foreEar3, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      latheLoft.getLatheLoftForForeEar(foreEar);
      float[] floats = new float[]{crownForeValue * 2.7F, 8.6F, 0.2F, crownForeValue * 3.55F, 6.1F, 0.0F, crownForeValue * 3.95F, 4.2F, -0.25F};
      float[] crownForeValues = new float[]{1.7F, 1.75F, 1.78F};
      latheLoft.getLoftPath(floats, crownForeValues, crownForeValues, 10, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(crownForeValue * 2.7F, 7.2F, 0.2F).getLatheLoft(crownForeValue * 6.0F);
      latheLoft.getLatheLoft(new float[]{2.05F, 1.9F, 1.4F, 0.0F}, new float[]{-2.0F, 0.0F, 1.0F, 1.5F}, 10, shinBrr2, null, null);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(foreEar2);
      float[] loftPathValues = new float[]{crownForeValue * 3.8500001F, 5.3999996F, -0.2F, crownForeValue * 3.6F, 2.7F, -0.05F, crownForeValue * 3.0F, 1.3F, 0.1F};
      float[] floats4 = new float[]{1.3F, 1.5F, 1.55F};
      latheLoft.getLoftPath(loftPathValues, floats4, floats4, 10, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoftForForeEar(foreEar3);
      handleForLatheLoftFloat(latheLoft, crownForeValue, shinBrr3);
   }

   private static float getAmount2(float crownForeValue, float crownForeValue2) {
      float floor3 = (float)Math.floor(crownForeValue / 7.0F);
      float crownForeValue4 = getAmount(crownForeValue / 7.0F) * (0.55F + 0.45F * getAmount((int)floor3 + 100, 3, 9));
      float crownForeFactor = crownForeValue / 45.6F * (1.0F + 0.07F * getAmount(crownForeValue2 / 0.45F));
      float crownForeFactor2 = (crownForeValue2 - 22.175F) / 2.575F + 0.1F * crownForeValue4 + 0.08F * getAmountForFloat((Math.abs(crownForeValue) - 25.0F) / 10.0F);
      float crownForeFactor3 = crownForeFactor * crownForeFactor + crownForeFactor2 * crownForeFactor2 - 1.0F;
      return crownForeFactor3 * crownForeFactor3 * crownForeFactor3 - crownForeFactor * crownForeFactor * crownForeFactor2 * crownForeFactor2 * crownForeFactor2;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(7033410))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8019532), 0.3F, 4.0F, 66)
         .getBonecaAmbalabu(0.06F, 5.0F, 67);
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(5916214);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue2 = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuValue3 = 1.0F - getAmountForFloat((bonecaAmbalabuValue2 - 0.2F) / 0.3F);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float absScaled = Math.abs((j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 2.0F;
            float amountForFloat = getAmountForFloat((absScaled - 0.25F) / 0.3F);
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), bonecaAmbalabuValue, Math.max(bonecaAmbalabuValue3 * 0.7F, amountForFloat * 0.8F)));
         }
      }
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13132830))
         .getBonecaAmbalabu(0.1F, 5.0F, 91)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(10111511), 40.0F, 0.3F, 0.8F, 92)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(14715450), 30.0F, 0.25F, 0.5F, 93);
      handleBonecaAmbalabu(bonecaAmbalabu, 34, 96, 0.4F);
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(10111511);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue2 = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float abs3 = Math.abs((bonecaAmbalabuValue2 - 0.5F) * 360.0F);
         float amountForFloat = getAmountForFloat((abs3 - 95.0F) / 50.0F);
         float amountForFloat5 = getAmountForFloat((abs3 - 150.0F) / 25.0F);
         float crownForeValue = 20.3F - 2.6F * amountForFloat * (1.0F - 0.45F * amountForFloat5) - 2.2F * getAmount(i + 0.5F, bonecaAmbalabu.ambalabuBonecaValue, 22, 94, 0.25F);
         float bonecaAmbalabuValue7 = getAmount(bonecaAmbalabuTable, table, crownForeValue);

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            float bonecaAmbalabuLimit = (j + 0.5F) / bonecaAmbalabu.bonecaValue;
            float floor8 = (float)Math.floor(bonecaAmbalabuLimit * 7.0F);
            float bonecaAmbalabuLimit2 = 60.0F + 9.0F * getAmount(bonecaAmbalabuLimit * 7.0F) * (0.5F + 0.5F * getAmount((int)floor8, 5, 95));
            if (!(bonecaAmbalabuLimit > bonecaAmbalabuValue7) && !(abs3 < bonecaAmbalabuLimit2)) {
               int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(i, j);
               float max9 = Math.max(0.0F, 1.0F - (bonecaAmbalabuValue7 - bonecaAmbalabuLimit) / 0.18F);
               int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabuForInt, bonecaAmbalabuValue, max9 * 0.3F);
               float max11 = Math.max(0.0F, 1.0F - (abs3 - bonecaAmbalabuLimit2) / 10.0F);
               int bonecaAmbalabuValue12 = ForeShinUtils.getValue2(foreShinValue, bonecaAmbalabuValue, max11 * 0.25F);
               bonecaAmbalabu.getBonecaAmbalabu(i, j, bonecaAmbalabuValue12);
            } else {
               bonecaAmbalabu.getBonecaAmbalabu(i, j, 0);
            }
         }
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuFactor, int bonecaAmbalabuValue, float bonecaAmbalabuValue2) {
      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            float bonecaAmbalabuValue3 = (j + 0.5F) / bonecaAmbalabu.bonecaValue;
            float hashRegistryValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue * bonecaAmbalabuFactor
               + 0.25F * (float)Math.sin(bonecaAmbalabuValue3 * 5.0F + getAmount(i * bonecaAmbalabuFactor / bonecaAmbalabu.ambalabuBonecaValue, 7, bonecaAmbalabuValue) * 6.0F);
            float bonecaAmbalabuFactor2 = hashRegistryValue - (float)Math.floor(hashRegistryValue);
            float pow = (float)Math.pow(Math.sin(Math.PI * bonecaAmbalabuFactor2), 0.7);
            bonecaAmbalabu.getBonecaAmbalabu(i, j, ForeShinUtils.getValue(bonecaAmbalabu.getBonecaAmbalabuForInt(i, j), 1.0F - bonecaAmbalabuValue2 + bonecaAmbalabuValue2 * 1.15F * pow));
         }
      }
   }

   private static float[] getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2) {
      float bonecaAmbalabuFactor = getAmount((bonecaAmbalabuValue2 - 22.6F) / 3.25F, -0.999F, 0.999F);
      float sqrt = (float)Math.sqrt(1.0F - bonecaAmbalabuFactor * bonecaAmbalabuFactor);
      float bonecaAmbalabuFactor2 = 0.5F - (float)(Math.asin(getAmount(bonecaAmbalabuValue / (3.55F * sqrt), -1.0F, 1.0F)) / (Math.PI * 2));
      float bonecaAmbalabuFactor3 = (float)(Math.acos(bonecaAmbalabuFactor) / Math.PI);
      return new float[]{bonecaAmbalabuFactor2 * bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuFactor3 * bonecaAmbalabu.bonecaValue};
   }

   @Override
   public String formatTungDin() {
      return "orangutini_ananasini";
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(7033410))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8019532), 0.35F, 4.0F, 62)
         .getBonecaAmbalabu(0.06F, 5.0F, 61);
      int crownForeValue = ForeShinUtils.getValueForInt(2759958);
      float[] bonecaAmbalabu2 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, 0.0F, 21.35F);
      float[] bonecaAmbalabu3 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, 0.0F, 20.300001F);
      bonecaAmbalabu.getClass02(bonecaAmbalabu2[0], bonecaAmbalabu2[1], bonecaAmbalabu3[0], bonecaAmbalabu3[1], 2.5F, ForeShinUtils.getValueForIntFloat(5915444, 0.4F));

      for (int i = -1; i <= 1; i += 2) {
         float[] bonecaAmbalabu4 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, i * 0.38F, 21.550001F);
         bonecaAmbalabu.getClass022(bonecaAmbalabu4[0], bonecaAmbalabu4[1], 3.6F, 2.4F, ForeShinUtils.getValueForIntFloat(3878179, 0.75F));
         bonecaAmbalabu.getClass022(bonecaAmbalabu4[0], bonecaAmbalabu4[1] + 0.2F, 2.5F, 1.5F, ForeShinUtils.getValueForInt(1839886));
      }

      float[] bonecaAmbalabu5 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, -2.4F, 20.1F);
      float[] bonecaAmbalabu6 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, 2.4F, 20.1F);
      float[] bonecaAmbalabu7 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, 0.0F, 20.2F);
      float[] bonecaAmbalabu8 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, -1.6F, 20.2F);
      float[] bonecaAmbalabu9 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, 1.6F, 20.2F);
      bonecaAmbalabu.getClassForFloatInt(bonecaAmbalabu5[0], bonecaAmbalabu5[1] - 2.6F, bonecaAmbalabu7[0], bonecaAmbalabu7[1] - 2.6F, bonecaAmbalabu6[0], bonecaAmbalabu6[1] - 2.6F, 5.0F, ForeShinUtils.getValueForIntFloat(8215116, 0.45F));
      bonecaAmbalabu.getClassForFloatArrayFloat(new float[]{bonecaAmbalabu5[0], bonecaAmbalabu8[0], bonecaAmbalabu7[0], bonecaAmbalabu9[0], bonecaAmbalabu6[0]}, new float[]{bonecaAmbalabu5[1], bonecaAmbalabu8[1], bonecaAmbalabu7[1], bonecaAmbalabu9[1], bonecaAmbalabu6[1]}, 2.6F, crownForeValue);
   }

   private static float getAmount(float crownForeValue, int crownForeFactor, int crownForeDivisor, int bonecaAmbalabuValue) {
      return getAmount(crownForeValue, crownForeFactor, crownForeDivisor, bonecaAmbalabuValue, 0.0F);
   }

   private static float getAmount3(float crownForeValue, float crownForeValue2) {
      float crownForeFactor = crownForeValue / 3.55F;
      float crownForeFactor2 = (crownForeValue2 - 22.6F) / 3.25F;
      return -0.2F - 3.35F * (float)Math.sqrt(Math.max(0.0F, 1.0F - crownForeFactor * crownForeFactor - crownForeFactor2 * crownForeFactor2));
   }

   private static float getAmount(float[] crownForeValues, float[] crownForeValues2, float crownForeValue) {
      float crownForeFactor = 0.975F;
      float[] floats = new float[crownForeValues.length];

      for (int i = 1; i < floats.length; i++) {
         float crownForeFactor2 = (crownForeValues[i] - crownForeValues[i - 1]) * crownForeFactor;
         float crownForeFactor3 = crownForeValues2[i] - crownForeValues2[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(crownForeFactor2 * crownForeFactor2 + crownForeFactor3 * crownForeFactor3);
      }

      float crownForeDivisor = floats[floats.length - 1];
      if (crownForeValue <= crownForeValues2[0]) {
         return 1.0F;
      } else {
         for (int j = 1; j < floats.length; j++) {
            if (crownForeValue <= crownForeValues2[j]) {
               float crownForeFactor4 = (crownForeValue - crownForeValues2[j - 1]) / (crownForeValues2[j] - crownForeValues2[j - 1]);
               return 1.0F - (floats[j - 1] + crownForeFactor4 * (floats[j] - floats[j - 1])) / crownForeDivisor;
            }
         }

         return 0.0F;
      }
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      float bonecaAmbalabuValue2 = bonecaAmbalabu.bonecaValue * 0.5F;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuFactor = (j + 0.5F - bonecaAmbalabuValue) / bonecaAmbalabu.ambalabuBonecaValue * 360.0F;
            float bonecaAmbalabuFactor2 = (i + 0.5F - bonecaAmbalabuValue2) / bonecaAmbalabu.bonecaValue * 180.0F;
            float distance = (float)Math.sqrt(bonecaAmbalabuFactor * bonecaAmbalabuFactor + bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2);
            int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(3941396), ForeShinUtils.getValueForInt(2824720), getAmountForFloat((distance - 28.0F) / 40.0F));
            if (distance < 26.0F) {
               foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(985607), Math.min(1.0F, (26.0F - distance) / 4.0F));
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, foreShinValue);
         }
      }
   }

   private static void handleBonecaAmbalabuUPPER_PROFILE_RADII(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13132830))
         .getBonecaAmbalabu(0.1F, 5.0F, 111)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(10111511), 44.0F, 0.3F, 0.8F, 112)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(14715450), 32.0F, 0.25F, 0.5F, 113);
      handleBonecaAmbalabu(bonecaAmbalabu, 36, 114, 0.4F);
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(10111511);
      int valueForInt = ForeShinUtils.getValueForInt(8009746);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue3 = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float abs4 = Math.abs((bonecaAmbalabuValue3 - 0.5F) * 360.0F);
         float bonecaAmbalabuValue5 = getAmount(i + 0.5F, bonecaAmbalabu.ambalabuBonecaValue, 26, 115, 0.3F);
         float bonecaAmbalabuFactor = getAmountForFloat((abs4 - 30.0F) / 45.0F);
         float crownForeValue = (25.75F - 0.45F * bonecaAmbalabuValue5) * (1.0F - bonecaAmbalabuFactor) + (24.8F - 2.4F * bonecaAmbalabuValue5) * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue7 = getAmount(UPPER_PROFILE_RADII, UPPER_PROFILE_HEIGHTS, crownForeValue);

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            float bonecaAmbalabuLimit = (j + 0.5F) / bonecaAmbalabu.bonecaValue;
            if (bonecaAmbalabuLimit > bonecaAmbalabuValue7) {
               bonecaAmbalabu.getBonecaAmbalabu(i, j, 0);
            } else {
               int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(i, j);
               float max8 = Math.max(0.0F, 1.0F - (bonecaAmbalabuValue7 - bonecaAmbalabuLimit) / 0.12F);
               int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabuForInt, bonecaAmbalabuValue, max8 * 0.3F);
               if (bonecaAmbalabuLimit < 0.2F) {
                  foreShinValue = ForeShinUtils.getValue2(foreShinValue, valueForInt, (0.2F - bonecaAmbalabuLimit) / 0.2F * 0.3F);
               }

               bonecaAmbalabu.getBonecaAmbalabu(i, j, foreShinValue);
            }
         }
      }
   }

   private static float[] getBonecaAmbalabuForBonecaAmbalabuFloat(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2) {
      float bonecaAmbalabuFactor = getAmount((bonecaAmbalabuValue2 - 20.6F) / 1.6F, -0.999F, 0.999F);
      float sqrt = (float)Math.sqrt(1.0F - bonecaAmbalabuFactor * bonecaAmbalabuFactor);
      float bonecaAmbalabuFactor2 = 0.5F - (float)(Math.asin(getAmount(bonecaAmbalabuValue / (2.7F * sqrt), -1.0F, 1.0F)) / (Math.PI * 2));
      float bonecaAmbalabuFactor3 = (float)(Math.acos(bonecaAmbalabuFactor) / Math.PI);
      return new float[]{bonecaAmbalabuFactor2 * bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuFactor3 * bonecaAmbalabu.bonecaValue};
   }

   private static float getAmount(float crownForeLimit, float crownForeLimit2, float crownForeValue) {
      return crownForeLimit < crownForeLimit2 ? crownForeLimit2 : (crownForeLimit > crownForeValue ? crownForeValue : crownForeLimit);
   }

   @Override
   public String formatNoteBlock() {
      return "entity.panda.ambient";
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float crownForeFactor, ShinBrr shinBrr) {
      float sideScaled = crownForeFactor * 3.05F;
      latheLoft.getLatheLoft(sideScaled, 0.62F, -0.75F, 1.45F, 0.62F, 2.2F, 10, shinBrr);
      latheLoft.getLatheLoft(sideScaled - crownForeFactor * 0.05F, 0.85F, 0.65F, 1.05F, 0.85F, 1.0F, 8, shinBrr);

      for (int i = 0; i < 4; i++) {
         float crownForeValue = sideScaled + crownForeFactor * (0.95F - i * 0.55F);
         float[] floats = new float[]{crownForeValue, 0.48F, -2.6F, crownForeValue, 0.3F, -3.62F};
         float[] crownForeValues = new float[]{0.32F, 0.17F};
         latheLoft.getLoftPath(floats, crownForeValues, crownForeValues, 5, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
      }

      float crownForeValue2 = sideScaled - crownForeFactor * 1.25F;
      float[] loftPathValues = new float[]{sideScaled - crownForeFactor * 0.9F, 0.5F, -1.4F, crownForeValue2 - crownForeFactor * 0.35F, 0.42F, -2.1F, crownForeValue2 - crownForeFactor * 0.5F, 0.32F, -2.6F};
      float[] floats4 = new float[]{0.38F, 0.34F, 0.18F};
      latheLoft.getLoftPath(loftPathValues, floats4, floats4, 5, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
   }

   @Override
   public float getAmountFloat() {
      return 0.7F;
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5916214))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4864043), 0.4F, 5.0F, 52)
         .getBonecaAmbalabu(0.06F, 6.0F, 51);
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(3878179);
      float bonecaAmbalabuValue2 = bonecaAmbalabu.ambalabuBonecaValue / 192.0F;
      int valueForInt = ForeShinUtils.getValueForInt(7033410);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue4 = 22.6F + 3.25F * (float)Math.cos(Math.PI * (i + 0.5F) / bonecaAmbalabu.bonecaValue);
         float foreShinFactor = 0.2F * getAmountForFloat((bonecaAmbalabuValue4 - 24.5F) / 0.7F);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), bonecaAmbalabuValue, foreShinFactor));
         }
      }

      float[] bonecaAmbalabu2 = getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 22.05F);
      bonecaAmbalabu.getClass022(bonecaAmbalabu2[0], bonecaAmbalabu2[1], 12.0F * bonecaAmbalabuValue2, 14.0F * bonecaAmbalabuValue2, ForeShinUtils.getValueForIntFloat(valueForInt, 0.18F));

      for (int k = -1; k <= 1; k += 2) {
         float[] bonecaAmbalabu3 = getBonecaAmbalabu(bonecaAmbalabu, k * 0.98F, 22.85F);
         bonecaAmbalabu.getClass022(bonecaAmbalabu3[0], bonecaAmbalabu3[1], 14.0F * bonecaAmbalabuValue2, 13.0F * bonecaAmbalabuValue2, ForeShinUtils.getValueForIntFloat(valueForInt, 0.25F));
         float[] bonecaAmbalabu4 = getBonecaAmbalabu(bonecaAmbalabu, k * 0.98F, 23.15F);
         bonecaAmbalabu.getClass022(bonecaAmbalabu4[0], bonecaAmbalabu4[1], 10.0F * bonecaAmbalabuValue2, 8.0F * bonecaAmbalabuValue2, ForeShinUtils.getValueForIntFloat(bonecaAmbalabuValue, 0.25F));
      }

      float[] bonecaAmbalabu5 = getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 23.449999F);
      float[] bonecaAmbalabu6 = getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 21.75F);
      bonecaAmbalabu.getClass02(bonecaAmbalabu5[0], bonecaAmbalabu5[1], bonecaAmbalabu6[0], bonecaAmbalabu6[1], 6.0F * bonecaAmbalabuValue2, ForeShinUtils.getValueForIntFloat(7231044, 0.5F));

      for (int m = 0; m < 2; m++) {
         float bonecaAmbalabuValue6 = 24.75F + m * 0.32F;
         float[] bonecaAmbalabu7 = getBonecaAmbalabu(bonecaAmbalabu, -1.4F, bonecaAmbalabuValue6 - 0.1F);
         float[] bonecaAmbalabu8 = getBonecaAmbalabu(bonecaAmbalabu, 1.4F, bonecaAmbalabuValue6 - 0.1F);
         bonecaAmbalabu.getClass02(bonecaAmbalabu7[0], bonecaAmbalabu7[1], bonecaAmbalabu8[0], bonecaAmbalabu8[1], 1.3F * bonecaAmbalabuValue2, ForeShinUtils.getValueForIntFloat(2759958, 0.2F));
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue) {
      int valueForInt2 = ForeShinUtils.getValueForInt(3038763);
      int valueForInt = ForeShinUtils.getValueForInt(4160826);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue4 = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         int foreShinValue = bonecaAmbalabuValue4 < 0.45F ? ForeShinUtils.getValue2(valueForInt2, valueForInt, bonecaAmbalabuValue4 / 0.45F) : ForeShinUtils.getValue2(valueForInt, bonecaAmbalabuValue, (bonecaAmbalabuValue4 - 0.45F) / 0.55F);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue6 = (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
            float absScaled = Math.abs(bonecaAmbalabuValue6 - 0.5F) * 2.0F;
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue2(foreShinValue, bonecaAmbalabuValue, absScaled * 0.35F));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(2773542), 6.0F, 0.05F, 0.35F, 73).getBonecaAmbalabu(0.05F, 4.0F, 74);
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.5F - 0.8F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F + 0.8F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(9289310, 0.55F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, 1.0F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(9289310, 0.45F));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue - 1.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(9289310, 0.45F));

      for (int k = 5; k < bonecaAmbalabu.bonecaValue - 3; k += 4) {
         for (int m = 1; m <= 3; m += 2) {
            float bonecaAmbalabuValue7 = bonecaAmbalabu.ambalabuBonecaValue * m / 4.0F;
            bonecaAmbalabu.getClass02(bonecaAmbalabuValue7 - 1.6F, (float)k, bonecaAmbalabuValue7 + 1.6F, k + 1.6F, 1.0F, ForeShinUtils.getValueForIntFloat(2376991, 0.55F));
         }
      }
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13132830))
         .getBonecaAmbalabu(0.1F, 5.0F, 101)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(10111511), 40.0F, 0.3F, 0.8F, 102)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(14715450), 30.0F, 0.25F, 0.5F, 103);
      handleBonecaAmbalabu(bonecaAmbalabu, 30, 104, 0.4F);
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(10111511);
      int valueForInt = ForeShinUtils.getValueForInt(8009746);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue3 = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float abs4 = Math.abs((bonecaAmbalabuValue3 - 0.5F) * 360.0F);
         float bonecaAmbalabuValue5 = 1.0F - getAmountForFloat((abs4 - 80.0F) / 55.0F);
         float min6 = Math.min(1.0F, (0.62F + 0.38F * getAmount(i + 0.5F, bonecaAmbalabu.ambalabuBonecaValue, 20, 105, 0.4F)) * (1.0F - 0.4F * bonecaAmbalabuValue5));

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            float bonecaAmbalabuLimit = (j + 0.5F) / bonecaAmbalabu.bonecaValue;
            float floor7 = (float)Math.floor(bonecaAmbalabuLimit * 5.0F);
            float bonecaAmbalabuLimit2 = 80.0F + 8.0F * getAmount(bonecaAmbalabuLimit * 5.0F) * (0.5F + 0.5F * getAmount((int)floor7, 6, 106));
            if (!(bonecaAmbalabuLimit > min6) && !(abs4 < bonecaAmbalabuLimit2)) {
               int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(i, j);
               float max8 = Math.max(0.0F, 1.0F - (min6 - bonecaAmbalabuLimit) / 0.25F);
               int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabuForInt, bonecaAmbalabuValue, max8 * 0.3F);
               if (bonecaAmbalabuLimit < 0.25F) {
                  foreShinValue = ForeShinUtils.getValue2(foreShinValue, valueForInt, (0.25F - bonecaAmbalabuLimit) / 0.25F * 0.45F);
               }

               float max10 = Math.max(0.0F, 1.0F - (abs4 - bonecaAmbalabuLimit2) / 12.0F);
               int bonecaAmbalabuValue11 = ForeShinUtils.getValue2(foreShinValue, bonecaAmbalabuValue, max10 * 0.25F);
               bonecaAmbalabu.getBonecaAmbalabu(i, j, bonecaAmbalabuValue11);
            } else {
               bonecaAmbalabu.getBonecaAmbalabu(i, j, 0);
            }
         }
      }
   }

   private static float getAmount(float crownForeValue, int crownForeFactor, int crownForeDivisor, int bonecaAmbalabuValue, float crownForeFactor2) {
      float pilotStatusBound = -crownForeFactor2;

      for (int i = 0; i < crownForeDivisor; i++) {
         float crownForeValue3 = (i + 0.5F + 0.6F * (getAmount(i, 1, bonecaAmbalabuValue) - 0.5F)) * crownForeFactor / crownForeDivisor;
         float crownForeFactor3 = 0.45F + 0.55F * getAmount(i, 2, bonecaAmbalabuValue);
         float crownForeDivisor2 = (float)crownForeFactor / crownForeDivisor * (0.8F + 0.5F * getAmount(i, 3, bonecaAmbalabuValue));
         float crownForeValue4 = crownForeFactor2 * (2.0F * getAmount(i, 4, bonecaAmbalabuValue) - 1.0F);

         for (int j = -1; j <= 1; j++) {
            float crownForeLimit = Math.abs(crownForeValue - (crownForeValue3 + j * crownForeFactor)) / crownForeDivisor2;
            if (!(crownForeLimit >= 1.0F)) {
               pilotStatusBound = Math.max(pilotStatusBound, crownForeValue4 + crownForeFactor3 * (float)Math.pow(1.0F - crownForeLimit, 1.3F));
            }
         }
      }

      return pilotStatusBound;
   }

   private static void handleForLatheLoftShinBrr(LatheLoft latheLoft, ShinBrr shinBrr) {
      byte crownForeSize = 11;
      float[] floats = new float[3 * crownForeSize];
      float[] floats2 = new float[crownForeSize];
      float[] crownForeValues = new float[crownForeSize];

      for (int i = 0; i < crownForeSize; i++) {
         float crownForeValue = (float)i / (crownForeSize - 1) * 2.0F - 1.0F;
         float planValuesItem = crownForeValue * 2.3F;
         float crownForeValue3 = 1.0F - (float)Math.pow(Math.abs(crownForeValue), 2.5);
         float crownForeFactor = (Math.abs(planValuesItem) - 0.98F) / 1.3F;
         float crownForeValue4 = 24.3F + 0.22F * (1.0F - crownForeFactor * crownForeFactor) - 0.07F * (1.0F - Math.abs(crownForeValue));
         floats[3 * i] = planValuesItem;
         floats[3 * i + 1] = crownForeValue4;
         floats[3 * i + 2] = getAmount3(planValuesItem, crownForeValue4) + 0.25F;
         floats2[i] = 0.28F + 0.27F * crownForeValue3;
         crownForeValues[i] = 0.3F + 0.28F * crownForeValue3;
      }

      latheLoft.getLoftPath(floats, floats2, crownForeValues, 6, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
   }

   private void handleCrown(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      latheLoft.getLatheLoftForForeEar(this.crownOut);
      latheLoft.getLatheLoft(new float[]{0.85F, 0.7F, 0.3F}, new float[]{25.7F, 26.800001F, 27.400002F}, 8, shinBrr3, null, shinBrr3);

      for (int i = 0; i < 2; i++) {
         handle(latheLoft, 90.0F + i * 180.0F, 6.0F, 10.0F, 6.4F, 1.0F, 0.3F, 27.2F, 4, shinBrr);
      }

      for (int j = 0; j < 5; j++) {
         handle(latheLoft, j * 72.0F, 24.0F, 10.0F, 7.6F, 1.1F, 0.55F, 26.900002F, 4, shinBrr);
      }

      for (int k = 0; k < 5; k++) {
         handle(latheLoft, k * 72.0F + 36.0F, 33.0F, 10.0F, 6.8F, 1.05F, 0.6F, 26.75F, 4, shinBrr);
      }

      latheLoft.getLatheLoftForForeEar(this.classShinFoot);

      for (int m = 0; m < 6; m++) {
         handle(latheLoft, m * 60.0F + 30.0F, 42.0F, 10.0F, 6.5F, 1.15F, 0.85F, 26.5F, 4, shinBrr2);
      }

      for (int p = 0; p < 6; p++) {
         handle(latheLoft, p * 60.0F + 15.0F, 62.0F, 10.0F, 2.6F, 1.25F, 0.7F, 26.650002F, 3, shinBrr2);
      }
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13132830))
         .getBonecaAmbalabu(0.1F, 5.0F, 21)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(10111511), 46.0F, 0.3F, 0.75F, 22)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(14715450), 34.0F, 0.25F, 0.5F, 23);
      handleBonecaAmbalabu(bonecaAmbalabu, 40, 24, 0.42F);
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(8009746);
      int valueForInt = ForeShinUtils.getValueForInt(10111511);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue3 = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float crownForeValue = (bonecaAmbalabuValue3 - 0.5F) * 360.0F;
         float bonecaAmbalabuValue5 = 0.78F + 0.22F * getAmount(i + 0.5F, bonecaAmbalabu.ambalabuBonecaValue, 22, 31);

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            float bonecaAmbalabuLimit = (j + 0.5F) / bonecaAmbalabu.bonecaValue;
            if (bonecaAmbalabuLimit > bonecaAmbalabuValue5) {
               bonecaAmbalabu.getBonecaAmbalabu(i, j, 0);
            } else {
               float angle = 2.7646017F * bonecaAmbalabuLimit;
               float bonecaAmbalabuValue6 = 23.2F + 3.75F * (float)Math.cos(angle);
               float amount2 = getAmount2(crownForeValue, bonecaAmbalabuValue6);
               if (amount2 < 0.0F) {
                  bonecaAmbalabu.getBonecaAmbalabu(i, j, 0);
               } else {
                  int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(i, j);
                  float max8 = Math.max(0.0F, 1.0F - (bonecaAmbalabuValue5 - bonecaAmbalabuLimit) / 0.12F);
                  int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabuForInt, valueForInt, max8 * 0.3F);
                  if (amount2 < 0.06F) {
                     foreShinValue = ForeShinUtils.getValue2(foreShinValue, valueForInt, 0.25F * (1.0F - amount2 / 0.06F));
                  }

                  if (bonecaAmbalabuLimit < 0.12F) {
                     foreShinValue = ForeShinUtils.getValue2(foreShinValue, bonecaAmbalabuValue, (0.12F - bonecaAmbalabuLimit) / 0.12F * 0.4F);
                  }

                  if (bonecaAmbalabuLimit < 0.18F) {
                     float max10 = Math.max(0.0F, (getAmount(bonecaAmbalabuValue3 * 11.0F + 0.15F * getAmount((int)(bonecaAmbalabuValue3 * 11.0F), 8, 24)) - 0.8F) / 0.2F);
                     foreShinValue = ForeShinUtils.getValue2(foreShinValue, bonecaAmbalabuValue, 0.35F * max10 * (1.0F - bonecaAmbalabuLimit / 0.18F));
                  }

                  bonecaAmbalabu.getBonecaAmbalabu(i, j, foreShinValue);
               }
            }
         }
      }
   }

   @Override
   public String formatTungDinString() {
      return "Orangutini Ananasini";
   }

   private static void handleBonecaAmbalabu8(BonecaAmbalabu bonecaAmbalabu) {
      byte bonecaAmbalabuDivisor = 14;
      float bonecaAmbalabuDivisor2 = (float)bonecaAmbalabu.ambalabuBonecaValue / bonecaAmbalabuDivisor;
      float bonecaAmbalabuDivisor3 = bonecaAmbalabu.bonecaValue / 6.6F;
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(9067039);
      int valueForInt = ForeShinUtils.getValueForInt(8014360);
      int valueForInt3 = ForeShinUtils.getValueForInt(15779154);
      int valueForInt4 = ForeShinUtils.getValueForInt(10721850);
      int value2 = ForeShinUtils.getValue2(valueForInt4, bonecaAmbalabuValue, 0.35F);
      int bonecaAmbalabuValue6 = ForeShinUtils.getValue(ForeShinUtils.getValue2(valueForInt4, bonecaAmbalabuValue, 0.3F), 0.8F);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue7 = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         int bonecaAmbalabuValue8 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14263600), valueForInt4, getAmountForFloat((bonecaAmbalabuValue7 - 0.5F) / 0.5F) * 0.8F);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float hashRegistryValue = (j + 0.5F) / bonecaAmbalabuDivisor2 + (i + 0.5F) / bonecaAmbalabuDivisor3;
            float bonecaAmbalabuValue10 = (j + 0.5F) / bonecaAmbalabuDivisor2 - (i + 0.5F) / bonecaAmbalabuDivisor3;
            int floor11 = (int)Math.floor(hashRegistryValue);
            int floor12 = (int)Math.floor(bonecaAmbalabuValue10);
            float planValuesItem = hashRegistryValue - floor11 - 0.5F;
            float bonecaAmbalabuValue14 = bonecaAmbalabuValue10 - floor12 - 0.5F;
            float bonecaAmbalabuFactor = planValuesItem + bonecaAmbalabuValue14;
            float deltaVar = planValuesItem - bonecaAmbalabuValue14;
            float bonecaAmbalabuFactor2 = 2.0F * Math.max(Math.abs(planValuesItem), Math.abs(bonecaAmbalabuValue14));
            int bonecaAmbalabuValue15 = ForeShinUtils.getValue(bonecaAmbalabuValue8, 0.92F + 0.14F * getAmount(Math.floorMod(floor11, bonecaAmbalabuDivisor), Math.floorMod(floor12, bonecaAmbalabuDivisor), 5));
            int bonecaAmbalabuValue16 = ForeShinUtils.getValue(bonecaAmbalabuValue15, 1.18F - 0.5F * bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2);
            float bonecaAmbalabuValue17 = Math.max(0.0F, -deltaVar * 0.75F - bonecaAmbalabuFactor * 0.35F) * (1.0F - bonecaAmbalabuFactor2 * 0.7F);
            int bonecaAmbalabuValue18 = ForeShinUtils.getValue2(bonecaAmbalabuValue16, valueForInt3, Math.min(1.0F, bonecaAmbalabuValue17 * 1.1F));
            int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabuValue18, value2, bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2 * 0.8F);
            if (bonecaAmbalabuFactor2 > 0.86F) {
               foreShinValue = ForeShinUtils.getValue2(foreShinValue, bonecaAmbalabuValue, Math.min(1.0F, (bonecaAmbalabuFactor2 - 0.86F) / 0.1F));
            }

            float bonecaAmbalabuFactor3 = deltaVar + 0.18F;
            float sqrt = (float)Math.sqrt(bonecaAmbalabuFactor * bonecaAmbalabuFactor * 1.6F + bonecaAmbalabuFactor3 * bonecaAmbalabuFactor3);
            if (bonecaAmbalabuFactor3 < 0.0F && bonecaAmbalabuFactor < 0.05F && Math.abs(sqrt - 0.23F) < 0.04F) {
               foreShinValue = ForeShinUtils.getValue2(foreShinValue, valueForInt3, 0.6F);
            }

            if (sqrt < 0.23F) {
               foreShinValue = ForeShinUtils.getValue2(foreShinValue, valueForInt, Math.min(1.0F, (0.23F - sqrt) / 0.06F));
            }

            float bonecaAmbalabuFactor4 = bonecaAmbalabuFactor + 0.06F;
            float bonecaAmbalabuFactor5 = bonecaAmbalabuFactor3 + 0.1F;
            float sqrt2 = (float)Math.sqrt(bonecaAmbalabuFactor4 * bonecaAmbalabuFactor4 * 1.6F + bonecaAmbalabuFactor5 * bonecaAmbalabuFactor5);
            if (sqrt2 < 0.06F) {
               foreShinValue = ForeShinUtils.getValue2(foreShinValue, valueForInt3, 0.75F * (1.0F - sqrt2 / 0.06F));
            }

            if (deltaVar < -0.1F && deltaVar > -0.42F && Math.abs(bonecaAmbalabuFactor) < 0.07F * (deltaVar + 0.42F) / 0.32F) {
               foreShinValue = ForeShinUtils.getValue2(foreShinValue, valueForInt, 0.85F);
            }

            if (bonecaAmbalabuValue7 < 0.1F) {
               foreShinValue = ForeShinUtils.getValue2(foreShinValue, bonecaAmbalabuValue6, (0.1F - bonecaAmbalabuValue7) / 0.1F * 0.65F);
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, foreShinValue);
         }
      }
   }

   private static void handleBonecaAmbalabu9(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5916214))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4864043), 0.4F, 5.0F, 72)
         .getBonecaAmbalabu(0.05F, 4.0F, 71);
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(3878179);
      int valueForInt = ForeShinUtils.getValueForInt(8283475);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float planValuesItem = ((i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 360.0F;
         float amountForFloat = getAmountForFloat((Math.abs(planValuesItem) - 100.0F) / 50.0F);
         float max5 = Math.max(0.0F, 1.0F - Math.abs(planValuesItem + 45.0F) / 40.0F);

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(i, j);
            int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabuForInt, bonecaAmbalabuValue, amountForFloat * 0.6F);
            int bonecaAmbalabuValue7 = ForeShinUtils.getValue2(foreShinValue, valueForInt, max5 * 0.22F);
            bonecaAmbalabu.getBonecaAmbalabu(i, j, bonecaAmbalabuValue7);
         }
      }
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float sin = (float)Math.sin(angle);
      float cos = (float)Math.cos(angle);
      float shinProgressFactor2 = poseProgress.poseProgressActive ? 0.4F : 1.0F - Math.min(1.0F, shinProgressFactor * 2.5F);
      float shinProgressValue = poseProgress.getAgeCode(64.0F, 0.0F) * shinProgressFactor2;
      float shinProgressFactor3 = (float)Math.pow(1.0F - Math.abs(sin), 3.0) * shinProgressFactor;
      float shinProgressValue2 = -0.14F * sin * shinProgressFactor + 0.12F * shinProgressValue;
      float shinProgressTotal = 0.6F * poseProgress.getAnimationProgressForFloat(0.0F);
      float shinProgressTotal2 = 0.6F * poseProgress.getAnimationProgressForFloat((float) Math.PI);
      float shinProgressTotal3 = -0.9F * poseProgress.getAnimationProgress((float) Math.PI);
      float shinProgressTotal4 = -0.9F * poseProgress.getAnimationProgress(0.0F);
      float shinProgressValue3 = 0.25F * shinProgressTotal3;
      float shinProgressValue4 = 0.25F * shinProgressTotal4;
      float shinProgressValue5 = 0.08F * shinProgressFactor;
      float animationProgress = poseProgress.getAnimationProgressForFloat((float) Math.PI);
      float animationProgressForFloat = poseProgress.getAnimationProgressForFloat(0.0F);
      float abs6 = Math.abs(shinProgressValue);
      foreShinScale.getAmounts(this.armL, -0.12F * shinProgressFactor, 0.08F * cos * shinProgressFactor + 0.05F * poseProgress.getAgeCode(128.0F, 0.5F) * shinProgressFactor2, shinProgressValue2);
      foreShinScale.getAmountsForeShin2(this.armL, 0.45F * shinProgressValue, -0.25F * shinProgressFactor3, 0.0F);
      foreShinScale.getAmounts(this.crown, 0.0F, 0.0F, -shinProgressValue2 * 0.7F);
      float foreShinValue = getAmount(poseProgress.shinProgressAmount, -0.35F, 0.8F);
      foreShinScale.getAmountsBoneCount(this.crown, foreShinValue, poseProgress.headYawRadians, 0.0F);
      if (foreShinValue < 0.0F) {
         foreShinScale.getAmountsForeShin2(this.crown, 0.0F, -0.4F * foreShinValue, 2.2F * foreShinValue);
      }

      foreShinScale.getAmountsBoneCount(this.armL, (poseProgress.shinProgressAmount - foreShinValue) * 0.3F, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.foreR, 0.55F * animationProgress + 0.14F * shinProgressValue, 0.0F, 0.07F + 0.04F * poseProgress.getAgeCode(70.0F, 1.0F) * shinProgressFactor2 + 0.1F * Math.max(0.0F, -animationProgress));
      foreShinScale.getAmounts(this.foreL, 0.55F * animationProgressForFloat - 0.14F * shinProgressValue, 0.0F, -0.07F - 0.04F * poseProgress.getAgeCode(70.0F, 0.0F) * shinProgressFactor2 - 0.1F * Math.max(0.0F, -animationProgressForFloat));
      foreShinScale.getAmounts(this.foreEar, 0.12F + 0.25F * shinProgressFactor + 0.3F * Math.max(0.0F, animationProgress) + 0.1F * abs6, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, 0.12F + 0.25F * shinProgressFactor + 0.3F * Math.max(0.0F, animationProgressForFloat) + 0.1F * abs6, 0.0F, 0.0F);
      foreShinScale.getAmounts(
         this.crownOut, 0.06F * (float)Math.cos(2.0F * angle) * shinProgressFactor + 0.03F * poseProgress.getAgeCode(40.0F, 1.0F) * shinProgressFactor2, 0.0F, 0.1F * sin * shinProgressFactor - 0.1F * shinProgressValue
      );
      foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F + 0.08F * shinProgressFactor3, 1.0F - 0.1F * shinProgressFactor3, 1.0F + 0.08F * shinProgressFactor3);
      float shinProgressValue7 = (poseProgress.age + (poseProgress.hashCode & 127)) % 150.0F;
      float shinProgressValue8 = shinProgressValue7 < 16.0F ? (float)Math.sin(shinProgressValue7 / 16.0F * Math.PI) : 0.0F;
      float shinProgressDivisor = poseProgress.inPose ? 1.0F : 0.08F + 0.92F * shinProgressValue8;
      foreShinScale.getAmountsForForeEarFloat(this.crownForeEar, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.classCrown, 1.0F, shinProgressDivisor, 1.0F);
      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float amountForFloat5;
         float shinProgressValue10;
         if (swingProgress < 0.3F) {
            amountForFloat5 = getAmountForFloat(swingProgress / 0.3F);
            shinProgressValue10 = amountForFloat5 * 2.42F;
         } else if (swingProgress < 0.37F) {
            amountForFloat5 = 1.0F;
            shinProgressValue10 = 2.42F + 0.06F * (float)Math.sin((swingProgress - 0.3F) / 0.07F * Math.PI);
         } else if (swingProgress < 0.48F) {
            amountForFloat5 = 1.0F - getAmountForFloat((swingProgress - 0.37F) / 0.11F);
            shinProgressValue10 = 0.9F + amountForFloat5 * 1.52F;
         } else {
            amountForFloat5 = 0.0F;
            shinProgressValue10 = 0.9F * (1.0F - getAmountForFloat((swingProgress - 0.48F) / 0.52F));
         }

         float shinProgressFactor6 = swingProgress > 0.4F && swingProgress < 0.55F ? (float)Math.sin((swingProgress - 0.4F) / 0.15F * Math.PI) : 0.0F;
         float shinProgressValue11 = 0.12F + 0.02F * amountForFloat5;
         float min12 = Math.min(1.0F, shinProgressValue10 / 0.9F);
         float dinForeValue = min12 * 0.45F * (1.0F - amountForFloat5);
         float shinProgressValue14 = 0.07F * amountForFloat5;
         float shinProgressValue15 = 0.63F * amountForFloat5;
         foreShinScale.getAmounts(this.foreR, shinProgressValue10, dinForeValue, shinProgressValue14);
         foreShinScale.getAmounts(this.foreL, shinProgressValue10, -dinForeValue, -shinProgressValue14);
         foreShinScale.getAmountsForeShin2(this.foreR, 0.0F, 1.5F * amountForFloat5, -1.3F * amountForFloat5);
         foreShinScale.getAmountsForeShin2(this.foreL, 0.0F, 1.5F * amountForFloat5, -1.3F * amountForFloat5);
         foreShinScale.getAmounts(this.foreEar, shinProgressValue11, 0.0F, -shinProgressValue15);
         foreShinScale.getAmounts(this.shinProgressForeEar, shinProgressValue11, 0.0F, shinProgressValue15);
         foreShinScale.getAmountsBoneCount(this.armL, 0.15F * amountForFloat5 - 0.55F * shinProgressFactor6, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.crown, 0.25F * shinProgressFactor6, 0.0F, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.0F + 0.06F * shinProgressFactor6, 1.0F - 0.08F * shinProgressFactor6, 1.0F + 0.06F * shinProgressFactor6);
         foreShinScale.getAmountsBoneCount(this.crownOut, -0.6F * shinProgressFactor6, 0.0F, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F + 0.1F * shinProgressFactor6, 1.0F - 0.12F * shinProgressFactor6, 1.0F + 0.1F * shinProgressFactor6);
      }

      if (poseProgress.usingItem && poseProgress.swingProgress <= 0.0F) {
         foreShinScale.getAmounts(this.foreR, 1.0F, 0.2F, 0.1F);
         foreShinScale.getAmounts(this.foreEar, 0.6F, 0.0F, 0.0F);
      }

      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsBoneCount(this.armL, -0.45F, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.armL, 0.0F, 0.0F, 0.3F);
         foreShinScale.getAmountsForeShin2(this.legL, 0.0F, 0.0F, 1.1F);
         foreShinScale.getAmountsBoneCount(this.crown, 0.38F, 0.0F, 0.0F);
         shinProgressTotal += 0.75F;
         shinProgressTotal2 += 0.75F;
         shinProgressTotal3 -= 0.75F;
         shinProgressTotal4 -= 0.75F;
         foreShinScale.getAmountsBoneCount(this.foreR, 0.62F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.foreL, 0.62F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.foreEar, 0.15F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, 0.15F, 0.0F, 0.0F);
      }

      if (poseProgress.vehicle) {
         shinProgressTotal = 1.4F;
         shinProgressTotal2 = 1.4F;
         shinProgressTotal3 = -0.6F;
         shinProgressTotal4 = -0.6F;
         shinProgressValue5 = 0.25F;
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         foreShinScale.getAmounts(this.foreR, -0.3F, 0.0F, 1.2F);
         foreShinScale.getAmounts(this.foreL, -0.3F, 0.0F, -1.2F);
         foreShinScale.getAmounts(this.foreEar, 0.2F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, 0.2F, 0.0F, 0.0F);
      }

      foreShinScale.getAmounts(this.shinR, shinProgressTotal, 0.0F, shinProgressValue5);
      foreShinScale.getAmounts(this.shinL, shinProgressTotal2, 0.0F, -shinProgressValue5);
      foreShinScale.getAmounts(this.footR, shinProgressTotal3, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footL, shinProgressTotal4, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classShinProgress, -(shinProgressTotal + shinProgressTotal3) + shinProgressValue3, 0.0F, -shinProgressValue5);
      foreShinScale.getAmounts(this.crownForeForeEar, -(shinProgressTotal2 + shinProgressTotal4) + shinProgressValue4, 0.0F, shinProgressValue5);
      if (!poseProgress.vehicle) {
         foreShinScale.getAmountsForeShin2(this.legL, 0.0F, Math.max(getAmount(shinProgressTotal, shinProgressTotal3), getAmount(shinProgressTotal2, shinProgressTotal4)) - getAmount(0.0F, 0.0F), 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         float shinProgressFactor7 = (poseProgress.age + (poseProgress.hashCode & 15)) % 10.0F / 10.0F;
         float shinProgressValue16 = 4.0F * shinProgressFactor7 * (1.0F - shinProgressFactor7);
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.07F, 0.93F, 1.07F);
         foreShinScale.getAmountsForeShin2(this.armL, 0.0F, -0.5F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.shinFootForeEar, 2.6F * shinProgressFactor7, 2.4F * shinProgressValue16, -1.6F * shinProgressFactor7);
         foreShinScale.getAmounts(this.shinFootForeEar, 6.0F * shinProgressFactor7, 3.0F * shinProgressFactor7, 0.0F);
         foreShinScale.getAmountsForeShin2(this.chunkBSection, -2.4F * shinProgressFactor7, 2.0F * shinProgressValue16, -1.9F * shinProgressFactor7);
         foreShinScale.getAmounts(this.chunkBSection, -5.0F * shinProgressFactor7, 0.0F, 4.0F * shinProgressFactor7);
      } else {
         foreShinScale.getClassForForeEarFloat(this.shinFootForeEar, 0.01F);
         foreShinScale.getClassForForeEarFloat(this.chunkBSection, 0.01F);
      }
   }

   private static float getAmount(int crownForeValue, int crownForeValue2, int bonecaAmbalabuValue) {
      int crownForeValue4 = crownForeValue * 374761393 + crownForeValue2 * 668265263 + bonecaAmbalabuValue * 1442695041;
      int crownForeValue5 = (crownForeValue4 ^ crownForeValue4 >>> 13) * 1274126177;
      int crownForeValue6 = crownForeValue5 ^ crownForeValue5 >>> 16;
      return (crownForeValue6 & 16777215) / 1.6777215E7F;
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      float shinFootValue = getAmount();
      this.legL = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 7.6F, 0.0F);
      this.armL = latheLoft.getClassForStringForeEar("upper", this.legL, 0.0F, 8.2F, 0.0F);
      this.crown = latheLoft.getClassForStringForeEar("head", this.armL, 0.0F, 21.0F, 0.3F);
      this.crownForeEar = latheLoft.getClassForStringForeEar("lid_r", this.crown, 0.98F, 23.65F, shinFootValue);
      this.classCrown = latheLoft.getClassForStringForeEar("lid_l", this.crown, -0.98F, 23.65F, shinFootValue);
      this.crownOut = latheLoft.getClassForStringForeEar("crown", this.crown, 0.0F, 26.7F, 0.4F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("crown_out", this.crownOut, 0.0F, 26.7F, 0.4F);
      this.foreR = latheLoft.getClassForStringForeEar("arm_r", this.armL, 4.45F, 17.0F, 0.25F);
      this.foreEar = latheLoft.getClassForStringForeEar("fore_r", this.foreR, 6.65F, 11.0F, 0.35F);
      this.foreL = latheLoft.getClassForStringForeEar("arm_l", this.armL, -4.45F, 17.0F, 0.25F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("fore_l", this.foreL, -6.65F, 11.0F, 0.35F);
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", this.legL, 2.7F, 7.6F, 0.2F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 3.95F, 4.2F, -0.25F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("foot_r", this.footR, 3.0F, 1.3F, 0.1F);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", this.legL, -2.7F, 7.6F, 0.2F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -3.95F, 4.2F, -0.25F);
      this.crownForeForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -3.0F, 1.3F, 0.1F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("chunk_a", this.armL, 3.6F, 14.6F, -3.5F);
      this.chunkBSection = latheLoft.getClassForStringForeEar("chunk_b", this.armL, -4.0F, 11.4F, -3.9F);
      latheLoft.getLatheLoft(this.foreR);
      LoftLatheFlag.Region region = latheLoft.getMax(256, 128);
      handleBonecaAmbalabu8(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(224, 128);
      handleBonecaAmbalabu5(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(320, 160);
      handleBonecaAmbalabu7(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(96, 64);
      handle(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(64, 24);
      handleBonecaAmbalabu9(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(48, 32);
      handleBonecaAmbalabu(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(224, 64);
      handleBonecaAmbalabuForBonecaAmbalabu(max7.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max8 = latheLoft.getMax(224, 56);
      handleBonecaAmbalabu6(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu(max9.getClassBonecaAmbalabu(), 31, 0.62F, 13, 18.0F);
      LoftLatheFlag.Region max10 = latheLoft.getMax(64, 48);
      handleBonecaAmbalabu(max10.getClassBonecaAmbalabu(), 41, 0.66F, 14, 18.0F);
      LoftLatheFlag.Region max11 = latheLoft.getMax(64, 40);
      handleBonecaAmbalabu(max11.getClassBonecaAmbalabu(), 51, 0.45F, 15, 20.0F);
      LoftLatheFlag.Region max12 = latheLoft.getMax(24, 64);
      handleBonecaAmbalabu(max12.getClassBonecaAmbalabu(), ForeShinUtils.getValueForInt(7315530));
      LoftLatheFlag.Region max13 = latheLoft.getMax(24, 64);
      handleBonecaAmbalabu(max13.getClassBonecaAmbalabu(), ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(7315530), ForeShinUtils.getValueForInt(6196314), 0.3F));
      LoftLatheFlag.Region max14 = latheLoft.getMax(224, 40);
      handleBonecaAmbalabuUPPER_PROFILE_RADII(max14.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max15 = latheLoft.getMax(40, 64);
      handleBonecaAmbalabu(max15.getClassBonecaAmbalabu(), 61, 0.6F, 5, 9.0F);
      LoftLatheFlag.Region max16 = latheLoft.getMax(32, 32);
      max16.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4864558))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(5916728), 0.3F, 6.0F, 81)
         .getBonecaAmbalabu(0.06F, 3.0F, 82);
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max8);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max6);
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max9);
      ShinBrr classForRegion11 = latheLoft.getClassForRegion(max10);
      ShinBrr classForRegion12 = latheLoft.getClassForRegion(max11);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max12);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max13);
      ShinBrr classForRegion15 = latheLoft.getClassForRegion(max14);
      ShinBrr classForRegion16 = latheLoft.getClassForRegion(max15);
      ShinBrr classForRegion17 = latheLoft.getClassForRegion(max16);
      ShinBrr shinBrr = latheLoft.getMax(11885083);
      latheLoft.getLatheLoftForForeEar(this.armL);
      latheLoft.getLatheProfile(shinFootTable, crownForeTable, 18, 1.0F, 0.92F, classForRegion, null, null);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, 0.3F);
      latheLoft.getLatheProfile(LOWER_PROFILE_RADII, LOWER_PROFILE_HEIGHTS, 12, 1.0F, 0.95F, classForRegion5, null, null);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.crown);
      latheLoft.getLatheLoft(0.0F, 22.6F, -0.2F, 3.55F, 3.25F, 3.35F, 12, classForRegion6);
      latheLoft.getLoftLathe(0.0F, 23.2F, 0.4F, 4.3F, 3.75F, 3.9F, 16, 0.0F, 0.88F, classForRegion3);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, 0.45F);
      latheLoft.getLatheProfile(bonecaAmbalabuTable, table, 14, 1.0F, 0.95F, classForRegion4, null, null);
      latheLoft.getDequeArray();
      handleForLatheLoftShinBrr(latheLoft, classForRegion8);
      latheLoft.getLatheLoft(0.0F, 21.8F, -3.05F, 1.5F, 1.05F, 1.25F, 8, classForRegion9);
      latheLoft.getLatheLoft(0.0F, 20.6F, -3.4F, 2.7F, 1.6F, 1.65F, 14, classForRegion7);
      latheLoft.getLatheLoft(0.0F, 19.95F, -3.75F, 2.2F, 0.55F, 1.25F, 8, latheLoft.getMax(8215116));
      this.handleCrown(latheLoft, shinFootValue);
      latheLoft.getLatheLoftForForeEar(this.crown);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, 0.45F);
      latheLoft.getLatheProfile(UPPER_PROFILE_RADII, UPPER_PROFILE_HEIGHTS, 12, 1.0F, 0.95F, classForRegion15, null, null);
      latheLoft.getDequeArray();
      handle(latheLoft, classForRegion16);
      this.handleCrown(latheLoft, classForRegion13, classForRegion14, latheLoft.getMax(3038763));
      handle(latheLoft, 1.0F, this.foreR, this.foreEar, shinBrr, classForRegion10, classForRegion12, classForRegion17);
      handle(latheLoft, -1.0F, this.foreL, this.shinProgressForeEar, shinBrr, classForRegion10, classForRegion12, classForRegion17);
      handle(latheLoft, 1.0F, this.shinR, this.footR, this.classShinProgress, classForRegion11, classForRegion12, classForRegion17);
      handle(latheLoft, -1.0F, this.shinL, this.footL, this.crownForeForeEar, classForRegion11, classForRegion12, classForRegion17);
      ShinBrr shinBrr2 = latheLoft.getMax(16047226);
      ShinBrr shinBrr3 = latheLoft.getMax(14263600);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      latheLoft.getLatheLoft(3.0F, 14.0F, -4.1F, 4.2F, 15.2F, -2.9F, shinBrr3, shinBrr2, shinBrr2, shinBrr2, shinBrr2, shinBrr2);
      latheLoft.getLatheLoftForForeEar(this.chunkBSection);
      latheLoft.getLatheLoft(-4.5F, 10.9F, -4.4F, -3.5F, 11.9F, -3.4F, shinBrr3, shinBrr2, shinBrr2, shinBrr2, shinBrr2, shinBrr2);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue, float bonecaAmbalabuValue2, int bonecaAmbalabuFactor, float bonecaAmbalabuValue4) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13132830))
         .getBonecaAmbalabu(0.1F, 4.0F, bonecaAmbalabuValue)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(10111511), bonecaAmbalabuValue4, 0.3F, 0.8F, bonecaAmbalabuValue + 1)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(14715450), bonecaAmbalabuValue4 * 0.7F, 0.3F, 0.45F, bonecaAmbalabuValue + 2);
      handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuFactor, bonecaAmbalabuValue + 3, 0.4F);
      int valueForInt5 = ForeShinUtils.getValueForInt(10111511);
      int valueForInt = ForeShinUtils.getValueForInt(8009746);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue7 = (bonecaAmbalabuValue2 + (1.0F - bonecaAmbalabuValue2) * getAmount(i + 0.5F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuFactor, bonecaAmbalabuValue)) * bonecaAmbalabu.bonecaValue;

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            if (j + 0.5F > bonecaAmbalabuValue7) {
               bonecaAmbalabu.getBonecaAmbalabu(i, j, 0);
            } else {
               int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(i, j);
               float bonecaAmbalabuLimit = 1.0F - (bonecaAmbalabuValue7 - j) / (bonecaAmbalabu.bonecaValue * 0.3F);
               if (bonecaAmbalabuLimit > 0.0F) {
                  bonecaAmbalabuForInt = ForeShinUtils.getValue2(bonecaAmbalabuForInt, valueForInt5, bonecaAmbalabuLimit * 0.3F);
               }

               float bonecaAmbalabuLimit2 = 1.0F - j / (bonecaAmbalabu.bonecaValue * 0.2F);
               if (bonecaAmbalabuLimit2 > 0.0F) {
                  bonecaAmbalabuForInt = ForeShinUtils.getValue2(bonecaAmbalabuForInt, valueForInt, bonecaAmbalabuLimit2 * 0.35F);
               }

               bonecaAmbalabu.getBonecaAmbalabu(i, j, bonecaAmbalabuForInt);
            }
         }
      }
   }

   private static void handle(LatheLoft latheLoft, float crownForeValue, ForeEar foreEar, ForeEar foreEar2, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      latheLoft.getLatheLoftForForeEar(foreEar);
      float[] floats = new float[]{crownForeValue * 4.45F, 17.6F, 0.25F, crownForeValue * 5.65F, 14.1F, 0.3F, crownForeValue * 6.65F, 10.2F, 0.45F};
      float[] crownForeValues = new float[]{1.45F, 1.6F, 1.72F};
      latheLoft.getLoftPath(floats, crownForeValues, crownForeValues, 10, shinBrr2, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(crownForeValue * 4.45F, 17.0F, 0.25F).getLatheLoft(crownForeValue * 12.0F);
      latheLoft.getLatheLoft(new float[]{1.8F, 1.85F, 1.6F, 1.0F, 0.0F}, new float[]{-3.4F, -0.8F, 0.5F, 1.3F, 1.75F}, 12, shinBrr3, null, null);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(foreEar2);
      float[] latheLoftValues = new float[]{crownForeValue * 6.65F, 11.3F, 0.35F, crownForeValue * 6.85F, 8.4F, 0.0F, crownForeValue * 6.6F, 5.75F, -0.3F};
      latheLoft.getLatheLoft(latheLoftValues, new float[]{1.0F, 0.9F, 0.78F}, 6, shinBrr, false);
      float[] loftPathValues = new float[]{crownForeValue * 5.95F, 13.2F, 0.29999998F, crownForeValue * 6.85F, 8.4F, 0.0F, crownForeValue * 6.6F, 5.75F, -0.3F};
      float[] floats5 = new float[]{1.25F, 1.45F, 1.5F};
      latheLoft.getLoftPath(loftPathValues, floats5, floats5, 10, shinBrr2, null, null, 0.0F, 0.0F, -1.0F);
      handle(latheLoft, crownForeValue, shinBrr4);
   }
}
