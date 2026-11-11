package com.corz.client.utils;

import java.util.Arrays;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class TailKatana extends WarmBrainrotBase {
   private ForeEar katanaR;
   private ForeEar classShinFoot;
   private ForeEar classShinProgress;
   private static final float[] shinFootTable = new float[]{15.7F, 15.8F, 17.35F, 18.63F, 18.75F};
   private ForeEar cup;
   private static final float[] tailKatanaTable = new float[]{0.0F, 0.16F, 0.3F, 0.18F, 0.0F};
   private ForeEar katanaL;
   private ForeEar armL;
   private ForeEar shinFootForeEar;
   private ForeEar shinProgressForeEar;
   private ForeEar shinR;
   private ForeEar shinL;
   private ForeEar tailKatanaForeEar;
   private ForeEar footL;
   private ForeEar footR;
   private ForeEar foreEar;
   private ForeEar katanaLeftSection;
   private ForeEar katanaRightSection;
   private static final float[] table = new float[]{21.95F, 22.050001F, 23.05F, 24.05F, 24.15F};
   private static final float[] BLADE_THICKNESSES = new float[]{0.0F, 0.18F, 0.34F, 0.2F, 0.0F};
   private static final float[] foreKatanaAmounts = new float[]{6.0F, 12.7F, -0.6F};
   private ForeEar footLeftSection;
   private static final float[] katanaForeTable = new float[]{7.0F, 11.6F, -2.7F};
   private ForeEar foreR;
   private ForeEar eyesSection;
   private ForeEar brows;
   private ForeEar tailLeftSection;
   private static final float[] katanaForeAmounts = new float[]{7.05F, 11.4F, -3.1F};
   private static final float[] shinFootAmounts = new float[]{2.55F, 5.8F, -0.65F};
   private ForeEar foreL;
   private static final float[] footShinAmounts = new float[]{3.1F, 1.25F, 0.0F};

   private static float getAmounts(float tailKatanaValue) {
      float tailKatanaValue2 = 0.0F;

      for (int i = 1; i < table.length; i++) {
         if (tailKatanaValue <= table[i]) {
            float tailKatanaFactor = Math.max(0.0F, (tailKatanaValue - table[i - 1]) / (table[i] - table[i - 1]));
            tailKatanaValue2 = BLADE_THICKNESSES[i - 1] + (BLADE_THICKNESSES[i] - BLADE_THICKNESSES[i - 1]) * tailKatanaFactor;
            break;
         }
      }

      return getAmount3(tailKatanaValue) + tailKatanaValue2;
   }

   private static void handleShinFoot(LatheLoft latheLoft, float shinFootFactor, ShinBrr shinBrr) {
      float shinFootValue = shinFootFactor * footShinAmounts[0];
      latheLoft.getLatheLoft(new float[]{shinFootValue, 1.85F, 0.02F, shinFootValue, 0.7F, 0.0F}, new float[]{0.45F, 0.5F}, 10, shinBrr, false);
      latheLoft.getLatheLoft(shinFootValue, 0.55F, -0.5F, 0.6F, 0.55F, 1.15F, 10, shinBrr);
      latheLoft.getLatheLoft(shinFootValue, 0.52F, 0.22F, 0.5F, 0.52F, 0.5F, 8, shinBrr);
      latheLoft.getLatheLoft(shinFootValue - shinFootFactor * 0.41F, 0.42F, -1.47F, 0.31F, 0.41F, 0.53F, 6, shinBrr);
      latheLoft.getLatheLoft(shinFootValue + shinFootFactor * 0.24F, 0.39F, -1.42F, 0.43F, 0.38F, 0.53F, 6, shinBrr);
   }

   private static void handleBonecaAmbalabu(
      BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuFactor, float bonecaAmbalabuValue, float bonecaAmbalabuValue2, float bonecaAmbalabuFactor2, float bonecaAmbalabuFactor3, float bonecaAmbalabuValue3, float bonecaAmbalabuValue4, int bonecaAmbalabuValue5, TailKatana.Area area
   ) {
      float bonecaAmbalabuFactor4 = (bonecaAmbalabu.ambalabuBonecaValue - 1) / ((float) (Math.PI * 2) * bonecaAmbalabuFactor);
      float bonecaAmbalabuValue6 = 0.5F + 0.5F * (bonecaAmbalabu.ambalabuBonecaValue - 1);
      float bonecaAmbalabuFactor5 = (bonecaAmbalabu.bonecaValue - 1) / (bonecaAmbalabuValue - bonecaAmbalabuValue2);
      bonecaAmbalabu.getBonecaAmbalabu(
         bonecaAmbalabuValue6 - bonecaAmbalabuFactor3 * bonecaAmbalabuFactor4 - 1.0F,
         0.5F + (bonecaAmbalabuValue - bonecaAmbalabuValue4) * bonecaAmbalabuFactor5 - 1.0F,
         bonecaAmbalabuValue6 - bonecaAmbalabuFactor2 * bonecaAmbalabuFactor4 + 1.0F,
         0.5F + (bonecaAmbalabuValue - bonecaAmbalabuValue3) * bonecaAmbalabuFactor5 + 1.0F,
         bonecaAmbalabuValue5,
         (bonecaAmbalabuValue7, yTopx) -> area.at((bonecaAmbalabuValue6 - bonecaAmbalabuValue7) / bonecaAmbalabuFactor4, bonecaAmbalabuValue - (yTopx - 0.5F) / bonecaAmbalabuFactor5)
      );
   }

   private static float getAmount(float tailKatanaValue) {
      return 0.62F * (1.0F - 0.22F * tailKatanaValue);
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(0);
      float tailKatanaValue = getAmount3(20.1F) + 0.12F;

      for (int i = -1; i <= 1; i += 2) {
         float tailKatanaFactor = i;
         float min3 = Math.min(tailKatanaFactor * 0.1F, tailKatanaFactor * 4.2F);
         float max4 = Math.max(tailKatanaFactor * 0.1F, tailKatanaFactor * 4.2F);
         float tailKatanaValue5 = tailKatanaFactor * 1.71F;
         float tailKatanaValue6 = 20.130001F;
         float[] floats = new float[]{tailKatanaFactor * 3.25F, 20.6F, tailKatanaFactor * 3.85F, 20.66F, tailKatanaFactor * 3.42F, 20.32F};
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            tailKatanaValue,
            21.2F,
            19.2F,
            min3,
            max4,
            19.2F,
            21.2F,
            ForeShinUtils.getValueForInt(723723),
            (tailKatanaValue7, tailKatanaValue8) -> canFlag(tailKatanaValue7, tailKatanaValue8, tailKatanaFactor, 0.09F, 0.28F, 0.04F) || canFlag(tailKatanaValue7, tailKatanaValue8, floats)
         );
         handleBonecaAmbalabu(
            bonecaAmbalabu, tailKatanaValue, 21.2F, 19.2F, min3, max4, 19.2F, 21.2F, ForeShinUtils.getValueForInt(16777215), (tailKatanaValue9, tailKatanaValue10) -> canFlag(tailKatanaValue9, tailKatanaValue10, tailKatanaFactor, 0.0F, 0.0F, 0.0F)
         );
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            tailKatanaValue,
            21.2F,
            19.2F,
            min3,
            max4,
            19.2F,
            21.2F,
            ForeShinUtils.getValueForIntFloat(11449536, 0.25F),
            (tailKatanaValue11, tailKatanaValue12) -> canFlag(tailKatanaValue11, tailKatanaValue12, tailKatanaFactor, 0.0F, 0.0F, 0.0F) && !canFlag(tailKatanaValue11, tailKatanaValue12 + 0.13F, tailKatanaFactor, 0.0F, 0.0F, 0.0F)
         );
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            tailKatanaValue,
            21.2F,
            19.2F,
            min3,
            max4,
            19.2F,
            21.2F,
            ForeShinUtils.getValueForInt(2824720),
            (tailKatanaValue13, tailKatanaValue14) -> canFlag(tailKatanaValue13, tailKatanaValue14, tailKatanaFactor, 0.0F, 0.0F, 0.0F) && getAmount(tailKatanaValue13 - tailKatanaValue5, tailKatanaValue14 - 20.130001F) < 0.2704F
         );
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            tailKatanaValue,
            21.2F,
            19.2F,
            min3,
            max4,
            19.2F,
            21.2F,
            ForeShinUtils.getValueForIntFloat(6964006, 0.35F),
            (tailKatanaValue15, tailKatanaValue16) -> canFlag(tailKatanaValue15, tailKatanaValue16, tailKatanaFactor, 0.0F, 0.0F, 0.0F) && getAmount(tailKatanaValue15 - tailKatanaValue5, tailKatanaValue16 - 20.130001F + 0.16F) < 0.1296F && tailKatanaValue16 < 20.11F
         );
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            tailKatanaValue,
            21.2F,
            19.2F,
            min3,
            max4,
            19.2F,
            21.2F,
            ForeShinUtils.getValueForInt(394758),
            (tailKatanaValue17, tailKatanaValue18) -> canFlag(tailKatanaValue17, tailKatanaValue18, tailKatanaFactor, 0.0F, 0.0F, 0.0F) && getAmount(tailKatanaValue17 - tailKatanaValue5, tailKatanaValue18 - 20.130001F) < 0.0784F
         );
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            tailKatanaValue,
            21.2F,
            19.2F,
            min3,
            max4,
            19.2F,
            21.2F,
            ForeShinUtils.getValueForInt(16777215),
            (tailKatanaValue19, tailKatanaValue20) -> canFlag(tailKatanaValue19, tailKatanaValue20, tailKatanaFactor, 0.0F, 0.0F, 0.0F) && getAmount(tailKatanaValue19 - tailKatanaValue5 - 0.19F, tailKatanaValue20 - 20.130001F - 0.17F) < 0.0169F
         );
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            tailKatanaValue,
            21.2F,
            19.2F,
            min3,
            max4,
            19.2F,
            21.2F,
            ForeShinUtils.getValueForIntFloat(16777215, 0.85F),
            (tailKatanaValue21, tailKatanaValue22) -> canFlag(tailKatanaValue21, tailKatanaValue22, tailKatanaFactor, 0.0F, 0.0F, 0.0F) && getAmount(tailKatanaValue21 - tailKatanaValue5 + 0.18F, tailKatanaValue22 - 20.130001F + 0.2F) < 0.004225F
         );
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuFactor, float bonecaAmbalabuDivisor, float bonecaAmbalabuValue) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(7963275));
      float glfwLimit = bonecaAmbalabuFactor * (bonecaAmbalabu.ambalabuBonecaValue - 1);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue3 = (float)i / (bonecaAmbalabu.bonecaValue - 1);
         int bonecaAmbalabuValue4 = bonecaAmbalabuValue3 < 0.5F
            ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14015456), ForeShinUtils.getValueForInt(10660787), bonecaAmbalabuValue3 * 2.0F)
            : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(10660787), ForeShinUtils.getValueForInt(9279136), (bonecaAmbalabuValue3 - 0.5F) * 2.0F);

         for (int j = 0; j <= Math.round(glfwLimit); j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue4);
         }
      }

      float bonecaAmbalabuFactor2 = glfwLimit / bonecaAmbalabuDivisor;
      float bonecaAmbalabuFactor3 = (bonecaAmbalabu.bonecaValue - 1) / (2.0F * bonecaAmbalabuValue);
      float bonecaAmbalabuValue5 = 0.5F + glfwLimit * 0.5F;
      float amountForFloat = 0.5F + (bonecaAmbalabu.bonecaValue - 1) * 0.5F;
      float classBound = 1.6F;
      bonecaAmbalabu.getClassBonecaAmbalabu(classBound, classBound, glfwLimit - classBound, classBound + 1.8F, ForeShinUtils.getValueForIntFloat(16777215, 0.6F));
      bonecaAmbalabu.getClassBonecaAmbalabu(classBound, bonecaAmbalabu.bonecaValue - classBound - 2.6F, glfwLimit - classBound, bonecaAmbalabu.bonecaValue - classBound, ForeShinUtils.getValueForIntFloat(5133918, 0.65F));
      bonecaAmbalabu.getClassBonecaAmbalabu(classBound, classBound, classBound + 2.0F, bonecaAmbalabu.bonecaValue - classBound, ForeShinUtils.getValueForIntFloat(16777215, 0.32F));
      bonecaAmbalabu.getClassBonecaAmbalabu(glfwLimit - classBound - 2.2F, classBound, glfwLimit - classBound, bonecaAmbalabu.bonecaValue - classBound, ForeShinUtils.getValueForIntFloat(5133918, 0.5F));
      int valueForIntFloat = ForeShinUtils.getValueForIntFloat(3817800, 0.95F);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, glfwLimit, classBound, valueForIntFloat);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue - classBound, glfwLimit, bonecaAmbalabu.bonecaValue, valueForIntFloat);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, classBound, bonecaAmbalabu.bonecaValue, valueForIntFloat);
      bonecaAmbalabu.getClassBonecaAmbalabu(glfwLimit - classBound, 0.0F, glfwLimit, bonecaAmbalabu.bonecaValue, valueForIntFloat);

      for (int k = 0; k < 6; k++) {
         float bonecaAmbalabuValue9 = 6.0F + k * 6.5F;
         bonecaAmbalabu.getClass02(4.0F, bonecaAmbalabuValue9, glfwLimit - 4.0F, bonecaAmbalabuValue9 + 0.6F, 0.8F, ForeShinUtils.getValueForIntFloat(16777215, 0.12F));
      }

      int bonecaAmbalabuLimit = 48;
      float[] floats = new float[bonecaAmbalabuLimit + 1];
      float[] floats2 = new float[bonecaAmbalabuLimit + 1];

      for (int m = 0; m <= bonecaAmbalabuLimit; m++) {
         float bonecaAmbalabuValue10 = (float)m / bonecaAmbalabuLimit;
         double angle = bonecaAmbalabuValue10 * 2.4 * 2.0 * Math.PI + 0.6;
         float bonecaAmbalabuFactor4 = 0.07F + 0.5F * bonecaAmbalabuValue10;
         floats[m] = bonecaAmbalabuValue5 + (float)Math.cos(angle) * bonecaAmbalabuFactor4 * bonecaAmbalabuFactor2;
         floats2[m] = amountForFloat - (float)Math.sin(angle) * bonecaAmbalabuFactor4 * bonecaAmbalabuFactor3;
      }

      bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 3.2F, ForeShinUtils.getValueForInt(6120813));
      bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 1.0F, ForeShinUtils.getValueForIntFloat(4080972, 0.8F));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue5, amountForFloat, 2.4F, 2.4F, ForeShinUtils.getValueForInt(6120813));

      for (int p = -1; p <= 1; p += 2) {
         for (int q = -1; q <= 1; q += 2) {
            float classValue = bonecaAmbalabuValue5 + p * (bonecaAmbalabuDivisor * 0.5F - 0.3F) * bonecaAmbalabuFactor2;
            float bonecaAmbalabuValue12 = amountForFloat + q * (bonecaAmbalabuValue - 0.24F) * bonecaAmbalabuFactor3;
            bonecaAmbalabu.getClass022(classValue, bonecaAmbalabuValue12, 2.8F, 2.8F, ForeShinUtils.getValueForInt(7239552));
            bonecaAmbalabu.getClass022(classValue - 0.7F, bonecaAmbalabuValue12 - 0.7F, 1.1F, 1.1F, ForeShinUtils.getValueForIntFloat(16777215, 0.7F));
         }
      }
   }

   private static boolean canFlag(float tailKatanaValue, float tailKatanaValue2, float... tailKatanaValues) {
      boolean tailKatanaFlag = false;
      int tailKatanaLimit = tailKatanaValues.length / 2;
      int tailKatanaCounter = 0;

      for (int i = tailKatanaLimit - 1; tailKatanaCounter < tailKatanaLimit; i = tailKatanaCounter++) {
         float tailKatanaValue3 = tailKatanaValues[2 * tailKatanaCounter];
         float tailKatanaLimit2 = tailKatanaValues[2 * tailKatanaCounter + 1];
         float tailKatanaValue4 = tailKatanaValues[2 * i];
         float tailKatanaLimit3 = tailKatanaValues[2 * i + 1];
         if (tailKatanaLimit2 > tailKatanaValue2 != tailKatanaLimit3 > tailKatanaValue2 && tailKatanaValue < (tailKatanaValue4 - tailKatanaValue3) * (tailKatanaValue2 - tailKatanaLimit2) / (tailKatanaLimit3 - tailKatanaLimit2) + tailKatanaValue3) {
            tailKatanaFlag = !tailKatanaFlag;
         }
      }

      return tailKatanaFlag;
   }

   private static void handleKatanaFore(
      LatheLoft latheLoft,
      float katanaForeFactor,
      float[] katanaForeValues,
      ShinBrr shinBrr,
      ShinBrr shinBrr2,
      ShinBrr shinBrr3,
      ShinBrr shinBrr4,
      ShinBrr shinBrr5,
      ShinBrr shinBrr6,
      ShinBrr shinBrr7,
      ShinBrr shinBrr8
   ) {
      latheLoft.getDequeArrayArrayDeque()
         .getLatheLoft(katanaForeFactor * katanaForeAmounts[0], katanaForeAmounts[1], katanaForeAmounts[2])
         .getLatheLoft(-katanaForeFactor * 28.0F)
         .getLatheLoftLatheLoftAmountLoft(-22.0F);
      latheLoft.getLatheLoft(new float[]{0.27F, 0.31F, 0.31F, 0.29F}, new float[]{-1.95F, -1.8000001F, 1.05F, 1.15F}, 8, shinBrr3, shinBrr8, null);
      latheLoft.getLatheLoft(new float[]{0.7F, 0.7F}, new float[]{1.15F, 1.37F}, 10, shinBrr4, shinBrr5, shinBrr5);
      latheLoft.getLatheLoft(-0.36F, 1.37F, -0.12F, 0.36F, 1.79F, 0.12F, shinBrr6);
      latheLoft.getPolygonExtrude(katanaForeValues, -0.07F, 0.07F, shinBrr, shinBrr2, shinBrr7);
      latheLoft.getDequeArray();
   }

   private static float getAmount(float tailKatanaLimit, float tailKatanaLimit2, float tailKatanaValue) {
      return tailKatanaLimit < tailKatanaLimit2 ? tailKatanaLimit2 : (tailKatanaLimit > tailKatanaValue ? tailKatanaValue : tailKatanaLimit);
   }

   private static void handleKatanaFore(LatheLoft latheLoft, float katanaForeFactor, ShinBrr shinBrr, ShinBrr shinBrr2) {
      latheLoft.getLatheLoft(
         new float[]{katanaForeFactor * foreKatanaAmounts[0], foreKatanaAmounts[1], foreKatanaAmounts[2], katanaForeFactor * 6.5F, 12.2F, -1.7F, katanaForeFactor * katanaForeTable[0], katanaForeTable[1], katanaForeTable[2]},
         new float[]{0.38F, 0.37F, 0.35F},
         8,
         shinBrr,
         false
      );
      latheLoft.getLatheLoft(new float[]{katanaForeFactor * 6.9F, 11.75F, -2.35F, katanaForeFactor * katanaForeTable[0], katanaForeTable[1], katanaForeTable[2] - 0.15F}, new float[]{0.44F, 0.46F}, 8, shinBrr2, true);
      latheLoft.getLatheLoft(katanaForeFactor * katanaForeAmounts[0], katanaForeAmounts[1], katanaForeAmounts[2], 0.6F, 0.56F, 0.62F, 8, shinBrr2);
      latheLoft.getLatheLoft(katanaForeFactor * (katanaForeAmounts[0] - 0.36F), katanaForeAmounts[1] + 0.2F, katanaForeAmounts[2] - 0.3F, 0.2F, 0.19F, 0.32F, 6, shinBrr2);
   }

   private static float getAmount(float tailKatanaValue, float tailKatanaValue2, float tailKatanaValue3, float tailKatanaValue4, float tailKatanaValue5, float tailKatanaValue6) {
      float deltaY = tailKatanaValue5 - tailKatanaValue3;
      float tailKatanaFactor = tailKatanaValue6 - tailKatanaValue4;
      float tailKatanaLimit = deltaY * deltaY + tailKatanaFactor * tailKatanaFactor;
      float tailKatanaBound = tailKatanaLimit > 0.0F ? ((tailKatanaValue - tailKatanaValue3) * deltaY + (tailKatanaValue2 - tailKatanaValue4) * tailKatanaFactor) / tailKatanaLimit : 0.0F;
      float max2 = Math.max(0.0F, Math.min(1.0F, tailKatanaBound));
      float tailKatanaFactor3 = tailKatanaValue3 + max2 * deltaY - tailKatanaValue;
      float tailKatanaFactor4 = tailKatanaValue4 + max2 * tailKatanaFactor - tailKatanaValue2;
      return (float)Math.sqrt(tailKatanaFactor3 * tailKatanaFactor3 + tailKatanaFactor4 * tailKatanaFactor4);
   }

   private static float getAmountForFloat(float tailKatanaFactor) {
      return 0.85F * (float)Math.pow(Math.max(0.0F, 1.0F - tailKatanaFactor * tailKatanaFactor), 0.55) * (1.0F + 0.22F * tailKatanaFactor);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float[] bonecaAmbalabuValues, float[] bonecaAmbalabuValues2) {
      float[] amounts3 = getAmounts(bonecaAmbalabuValues, bonecaAmbalabuValues2);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = 1.0F - (float)i / (bonecaAmbalabu.bonecaValue - 1);
         float bonecaAmbalabuLimit = getAmount(bonecaAmbalabuValues, amounts3, bonecaAmbalabuValue);
         int bonecaAmbalabuValue2;
         if (bonecaAmbalabuLimit > 3.4F) {
            bonecaAmbalabuValue2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13213294), ForeShinUtils.getValueForInt(11896410), getAmount4((bonecaAmbalabuLimit - 3.7F) / 0.75F));
         } else if (bonecaAmbalabuLimit > 2.55F) {
            bonecaAmbalabuValue2 = ForeShinUtils.getValue2(
               ForeShinUtils.getValueForInt(13213294), ForeShinUtils.getValueForInt(15258804), 0.55F * getAmount4(1.0F - Math.abs(bonecaAmbalabuLimit - 2.95F) / 0.42F)
            );
         } else {
            bonecaAmbalabuValue2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12159834), ForeShinUtils.getValueForInt(13213294), getAmount4(bonecaAmbalabuLimit / 2.55F));
         }

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14267529), 0.06F, 6.0F, 31);
      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(9331256, 0.16F), 0.06F, 0.5F, 33);
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(16116698), (bonecaAmbalabuValue3, bonecaAmbalabuValue4) -> {
         float var6x = getAmount(bonecaAmbalabuValues, amounts3, 1.0F - (bonecaAmbalabuValue4 - 0.5F) / (bonecaValue - 1));
         double var7x = (Math.PI * 2) * ((bonecaAmbalabuValue3 - 0.5F) / (ambalabuBonecaValue - 1) - 0.5);
         float var9x = (float)(-var6x * Math.sin(var7x));
         float var10x = (float)(-var6x * Math.cos(var7x));
         float var9xScaled = var9x / 1.45F;
         float bonecaAmbalabuFactor = (var10x - 0.2F) / 1.45F;
         float bonecaAmbalabuFactor2 = var9xScaled * var9xScaled + bonecaAmbalabuFactor * bonecaAmbalabuFactor - 1.0F;
         return bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2 - var9xScaled * var9xScaled * bonecaAmbalabuFactor * bonecaAmbalabuFactor * bonecaAmbalabuFactor <= 0.0F;
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 4.0F, 35);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16513267), ForeShinUtils.getValueForInt(16117992));
      bonecaAmbalabu.getBonecaAmbalabu(0.012F, 10.0F, 5);
      float bonecaAmbalabuValue = 24.3F;
      float bonecaAmbalabuValue2 = 11.299999F;
      float[][] floats = new float[][]{{35.0F, 0.55F}, {-55.0F, 0.25F}};

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuTotal = 0.0F;

         for (float[] bonecaAmbalabuValues : floats) {
            float bonecaAmbalabuFactor = (i + 0.5F - bonecaAmbalabu.ambalabuBonecaValue * LatheLoft.getAmount(bonecaAmbalabuValues[0])) / (0.035F * bonecaAmbalabu.ambalabuBonecaValue);
            bonecaAmbalabuTotal += bonecaAmbalabuValues[1] * (float)Math.exp(-bonecaAmbalabuFactor * bonecaAmbalabuFactor);
         }

         if (!(bonecaAmbalabuTotal < 0.004F)) {
            for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
               bonecaAmbalabu.getBonecaAmbalabu(i, j, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(i, j), -1, Math.min(1.0F, bonecaAmbalabuTotal)));
            }
         }
      }

      float classValue = bonecaAmbalabu.ambalabuBonecaValue * LatheLoft.getAmount(115.0F);
      float bonecaAmbalabuValue4 = bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue - 21.95F) / (bonecaAmbalabuValue - bonecaAmbalabuValue2);
      float bonecaAmbalabuValue5 = bonecaAmbalabu.bonecaValue / (bonecaAmbalabuValue - bonecaAmbalabuValue2);
      bonecaAmbalabu.getClassBonecaAmbalabu(classValue - 0.05F * bonecaAmbalabuValue5, bonecaAmbalabuValue4 - 2.0F, classValue + 0.05F * bonecaAmbalabuValue5, bonecaAmbalabuValue4 + 1.6F * bonecaAmbalabuValue5, ForeShinUtils.getValueForInt(12094046));
      bonecaAmbalabu.getClass022(classValue, bonecaAmbalabuValue4 + 1.6F * bonecaAmbalabuValue5, 0.16F * bonecaAmbalabuValue5, 0.24F * bonecaAmbalabuValue5, ForeShinUtils.getValueForInt(12094046));
      bonecaAmbalabu.getClass022(classValue - 0.04F * bonecaAmbalabuValue5, bonecaAmbalabuValue4 + 1.55F * bonecaAmbalabuValue5, 0.05F * bonecaAmbalabuValue5, 0.08F * bonecaAmbalabuValue5, ForeShinUtils.getValueForIntFloat(14860956, 0.8F));
      float[][] floats2 = new float[][]{{21.95F, -0.5F}, {18.75F, 0.32F}, {16.4F, -0.55F}, {24.15F, 0.15F}};

      for (float[] bonecaAmbalabuValues2 : floats2) {
         for (int k = 1; k <= 4; k++) {
            float bonecaAmbalabuBound = bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue - bonecaAmbalabuValues2[0]) / (bonecaAmbalabuValue - bonecaAmbalabuValue2);
            float hashHuntBound = bonecaAmbalabu.bonecaValue * (bonecaAmbalabuValue - (bonecaAmbalabuValues2[0] + bonecaAmbalabuValues2[1] * k / 4.0F)) / (bonecaAmbalabuValue - bonecaAmbalabuValue2);
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, Math.min(bonecaAmbalabuBound, hashHuntBound), bonecaAmbalabu.ambalabuBonecaValue, Math.max(bonecaAmbalabuBound, hashHuntBound), ForeShinUtils.getValueForIntFloat(9208954, 0.035F));
         }
      }
   }

   private static float getAmount(float tailKatanaFactor, float tailKatanaFactor2) {
      return tailKatanaFactor * tailKatanaFactor + tailKatanaFactor2 * tailKatanaFactor2;
   }

   private static float getAmount(float[] tailKatanaValues, float[] tailKatanaValues2, float tailKatanaValue) {
      for (int i = 1; i < tailKatanaValues2.length; i++) {
         if (tailKatanaValue <= tailKatanaValues2[i]) {
            float tailKatanaBound = (tailKatanaValue - tailKatanaValues2[i - 1]) / Math.max(1.0E-6F, tailKatanaValues2[i] - tailKatanaValues2[i - 1]);
            return tailKatanaValues[i - 1] + (tailKatanaValues[i] - tailKatanaValues[i - 1]) * Math.max(0.0F, tailKatanaBound);
         }
      }

      return tailKatanaValues[tailKatanaValues.length - 1];
   }

   private static float getAmount3(float tailKatanaValue) {
      return 3.9F + 1.04F * (tailKatanaValue - 10.4F) / 13.9F;
   }

   private static void handleKatanaFore(LatheLoft latheLoft, float katanaForeFactor, ShinBrr shinBrr) {
      latheLoft.getLatheLoft(
         new float[]{katanaForeFactor * 3.8F, 15.25F, 0.0F, katanaForeFactor * 5.0F, 14.0F, -0.3F, katanaForeFactor * foreKatanaAmounts[0], foreKatanaAmounts[1], foreKatanaAmounts[2]},
         new float[]{0.42F, 0.4F, 0.38F},
         8,
         shinBrr,
         false
      );
      latheLoft.getLatheLoft(katanaForeFactor * foreKatanaAmounts[0], foreKatanaAmounts[1], foreKatanaAmounts[2], 0.4F, 8, shinBrr);
   }

   private static void handleShinFootForLatheLoftFloat(LatheLoft latheLoft, float shinFootFactor, ShinBrr shinBrr) {
      latheLoft.getLatheLoft(
         new float[]{
            shinFootFactor * shinFootAmounts[0],
            shinFootAmounts[1],
            shinFootAmounts[2],
            shinFootFactor * 2.85F,
            3.6F,
            -0.35F,
            shinFootFactor * footShinAmounts[0],
            footShinAmounts[1] + 0.3F,
            footShinAmounts[2]
         },
         new float[]{0.42F, 0.39F, 0.36F},
         8,
         shinBrr,
         false
      );
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15789027));
      float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, bonecaAmbalabuValue, bonecaAmbalabuValue * 0.98F, bonecaAmbalabuValue * 0.98F, bonecaAmbalabuValue * 0.2F, ForeShinUtils.getValueForInt(13221549));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, bonecaAmbalabuValue, bonecaAmbalabuValue * 0.7F, bonecaAmbalabuValue * 0.7F, ForeShinUtils.getValueForInt(15130322));
      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 4.0F, 9);
   }

   @Override
   protected int getValueInt() {
      return 1024;
   }

   @Override
   public String formatNoteBlock() {
      return "entity.player.attack.sweep";
   }

   @Override
   public String formatTungDinString() {
      return "Cappuccino Assassino";
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(1842987)).getBonecaAmbalabu(0.12F, 6.0F, 81);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuLimit = (float)i / (bonecaAmbalabu.bonecaValue - 1);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = (float)j / (bonecaAmbalabu.ambalabuBonecaValue - 1);
            float abs2 = Math.abs((float)Math.cos((Math.PI * 2) * (bonecaAmbalabuValue - 0.5)));
            if (bonecaAmbalabuFlag && bonecaAmbalabuLimit > 1.0F - 0.2F * (1.0F - abs2)) {
               bonecaAmbalabu.getBonecaAmbalabu(j, i, 0);
            } else if (abs2 > 0.8F) {
               bonecaAmbalabu.getBonecaAmbalabu(
                  j,
                  i,
                  ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), ForeShinUtils.getValueForInt(4608110), 0.75F * getAmount4((abs2 - 0.8F) / 0.17F))
               );
            }
         }
      }
   }

   private static float[] getAmounts(float[] tailKatanaValues, float[] tailKatanaValues2) {
      float[] floats = new float[tailKatanaValues.length];

      for (int i = 1; i < tailKatanaValues.length; i++) {
         floats[i] = floats[i - 1] + (float)Math.hypot(tailKatanaValues[i] - tailKatanaValues[i - 1], tailKatanaValues2[i] - tailKatanaValues2[i - 1]);
      }

      float tailKatanaValue = floats[tailKatanaValues.length - 1];

      for (int j = 0; j < tailKatanaValues.length; j++) {
         floats[j] /= tailKatanaValue;
      }

      return floats;
   }

   private void handle(LatheLoft latheLoft, LoftLatheFlag.Region region) {
      float tailKatanaValue = 23.0F;
      float bonecaAmbalabuValue = 0.95F;
      float tailKatanaFactor = getAmount3(tailKatanaValue + bonecaAmbalabuValue) + 0.3F;
      float tailKatanaFactor2 = tailKatanaFactor + 0.24F;
      float tailKatanaFactor3 = 2.3F / tailKatanaFactor2;
      byte tailKatanaLimit = 6;
      float[] floats = new float[4 * (tailKatanaLimit + 1)];

      for (int i = 0; i <= tailKatanaLimit; i++) {
         float angle = tailKatanaFactor3 * (1.0F - 2.0F * i / tailKatanaLimit);
         floats[2 * i] = tailKatanaFactor2 * (float)Math.sin(angle);
         floats[2 * i + 1] = -tailKatanaFactor2 * (float)Math.cos(angle);
         floats[2 * (tailKatanaLimit + 1 + i)] = tailKatanaFactor * (float)Math.sin(-angle);
         floats[2 * (tailKatanaLimit + 1 + i) + 1] = -tailKatanaFactor * (float)Math.cos(-angle);
      }

      float tailKatanaTotal = 0.0F;
      float tailKatanaTotal2 = 0.0F;
      int tailKatanaLimit2 = floats.length / 2;

      for (int j = 0; j < tailKatanaLimit2; j++) {
         int tailKatanaValue3 = (j + 1) % tailKatanaLimit2;
         float hypot = (float)Math.hypot(floats[2 * tailKatanaValue3] - floats[2 * j], floats[2 * tailKatanaValue3 + 1] - floats[2 * j + 1]);
         tailKatanaTotal += hypot;
         if (j < tailKatanaLimit) {
            tailKatanaTotal2 += hypot;
         }
      }

      handleBonecaAmbalabu(region.getClassBonecaAmbalabu(), tailKatanaTotal2 / tailKatanaTotal, 2.0F * tailKatanaFactor3 * tailKatanaFactor2, bonecaAmbalabuValue);
      ShinBrr shinBrr = latheLoft.getMax(10660787);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, tailKatanaValue, 0.0F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getPolygonExtrude(floats, -bonecaAmbalabuValue, bonecaAmbalabuValue, shinBrr, shinBrr, latheLoft.getClassForRegion(region));
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float[] bonecaAmbalabuValues, boolean bonecaAmbalabuFlag) {
      float bonecaAmbalabuBound = Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;
      float maxValue4 = -Float.MAX_VALUE;

      for (int i = 0; i < bonecaAmbalabuValues.length; i += 2) {
         bonecaAmbalabuBound = Math.min(bonecaAmbalabuBound, bonecaAmbalabuValues[i]);
         pilotStatusBound = Math.max(pilotStatusBound, bonecaAmbalabuValues[i]);
         maxValue = Math.min(maxValue, bonecaAmbalabuValues[i + 1]);
         maxValue4 = Math.max(maxValue4, bonecaAmbalabuValues[i + 1]);
      }

      float bonecaAmbalabuValue = 11.82F;

      for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
         for (int k = 0; k < bonecaAmbalabu.ambalabuBonecaValue; k++) {
            float bonecaAmbalabuFactor = (float)k / (bonecaAmbalabu.ambalabuBonecaValue - 1);
            float bonecaAmbalabuFactor2 = (float)j / (bonecaAmbalabu.bonecaValue - 1);
            float bonecaAmbalabuValue2 = bonecaAmbalabuFlag ? pilotStatusBound - bonecaAmbalabuFactor * (pilotStatusBound - bonecaAmbalabuBound) : bonecaAmbalabuBound + bonecaAmbalabuFactor * (pilotStatusBound - bonecaAmbalabuBound);
            float bonecaAmbalabuValue3 = maxValue4 - bonecaAmbalabuFactor2 * (maxValue4 - maxValue);
            float tailKatanaValue = Math.max(0.0F, Math.min(1.0F, (bonecaAmbalabuValue3 - 1.37F) / 11.7F));
            float bonecaAmbalabuDivisor = getAmount(tailKatanaValue);
            float bonecaAmbalabuValue5 = getAmount3(tailKatanaValue, 1.0F) - bonecaAmbalabuDivisor * 0.5F;
            float bonecaAmbalabuLimit = (bonecaAmbalabuValue2 - bonecaAmbalabuValue5) / bonecaAmbalabuDivisor;
            float bonecaAmbalabuValue6 = 0.64F + 0.05F * (float)Math.sin(bonecaAmbalabuValue3 * 3.7F) + 0.025F * (float)Math.sin(bonecaAmbalabuValue3 * 9.3F + 1.0F);
            int valueForInt7;
            if (bonecaAmbalabuLimit < 0.12F) {
               valueForInt7 = ForeShinUtils.getValueForInt(10792375);
            } else if (bonecaAmbalabuLimit < 0.36F) {
               valueForInt7 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12897235), ForeShinUtils.getValueForInt(14672872), (bonecaAmbalabuLimit - 0.12F) / 0.24F);
            } else {
               valueForInt7 = ForeShinUtils.getValue2(
                  ForeShinUtils.getValueForInt(14672872), ForeShinUtils.getValueForInt(15791094), Math.max(0.0F, Math.min(1.0F, (bonecaAmbalabuLimit - bonecaAmbalabuValue6) / 0.05F + 0.5F))
               );
            }

            if (bonecaAmbalabuLimit > 0.86F) {
               valueForInt7 = ForeShinUtils.getValueForInt(16251387);
            }

            if (Math.abs(bonecaAmbalabuLimit - 0.36F) < 0.035F) {
               valueForInt7 = ForeShinUtils.getValueForInt(11844805);
            }

            if (Math.abs(bonecaAmbalabuValue3 - bonecaAmbalabuValue) < 0.05F && bonecaAmbalabuLimit > 0.36F) {
               valueForInt7 = ForeShinUtils.getValueForInt(12173770);
            }

            bonecaAmbalabu.getBonecaAmbalabu(k, j, valueForInt7);
         }
      }
   }

   private static float[] getAmountsForFloatArray(float[] tailKatanaValues, float[] tailKatanaValues2) {
      float[] amounts3 = getAmounts(tailKatanaValues, tailKatanaValues2);
      int tailKatanaValue = tailKatanaValues.length;
      float tailKatanaValue2 = amounts3[1];
      float tailKatanaValue3 = amounts3[tailKatanaValue - 2];
      float tailKatanaValue4 = tailKatanaValues2[1];
      float tailKatanaValue5 = tailKatanaValues2[tailKatanaValue - 2];
      float tailKatanaValue6 = tailKatanaValue4 + (0.0F - tailKatanaValue2) / (tailKatanaValue3 - tailKatanaValue2) * (tailKatanaValue5 - tailKatanaValue4);
      float tailKatanaValue7 = tailKatanaValue4 + (1.0F - tailKatanaValue2) / (tailKatanaValue3 - tailKatanaValue2) * (tailKatanaValue5 - tailKatanaValue4);
      return new float[]{tailKatanaValue7, tailKatanaValue6};
   }

   private static float[] getAmounts(float tailKatanaFactor, float tailKatanaValue, float tailKatanaFactor2, float tailKatanaFactor3) {
      float[] floats = new float[]{0.3F, -0.05F, -0.1F, 1.7F, 0.05F, 0.95F, 3.3F, -0.2F, 1.75F, 5.0F, -0.6F, 2.4F, 6.7F, -1.05F, 2.9F, 8.4F, -1.55F, 3.3F};
      float[] pointsSplineValues = new float[floats.length];

      for (int i = 0; i < floats.length / 3; i++) {
         float tailKatanaFactor4 = i / (floats.length / 3.0F - 1.0F);
         pointsSplineValues[3 * i] = tailKatanaFactor * (floats[0] + (floats[3 * i] - floats[0]) * tailKatanaFactor2);
         pointsSplineValues[3 * i + 1] = 23.0F + floats[1] + (floats[3 * i + 1] - floats[1]) * tailKatanaFactor2 - tailKatanaFactor3 * tailKatanaFactor4 * tailKatanaFactor4;
         pointsSplineValues[3 * i + 2] = tailKatanaValue + floats[2] + (floats[3 * i + 2] - floats[2]) * tailKatanaFactor2;
      }

      return LatheLoft.getPointsSpline(2, pointsSplineValues);
   }

   private static void handleForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(0);
      float tailKatanaValue = getAmount3(21.0F) + 0.19F;

      for (int i = -1; i <= 1; i += 2) {
         float tailKatanaValue2 = i;
         float[] floats = new float[]{
            tailKatanaValue2 * 0.12F, 20.12F, tailKatanaValue2 * 0.2F, 20.88F, tailKatanaValue2 * 1.25F, 21.5F, tailKatanaValue2 * 3.35F, 21.6F, tailKatanaValue2 * 3.25F, 21.22F, tailKatanaValue2 * 1.2F, 20.95F
         };
         float min3 = Math.min(tailKatanaValue2 * 0.02F, tailKatanaValue2 * 3.6F);
         float max4 = Math.max(tailKatanaValue2 * 0.02F, tailKatanaValue2 * 3.6F);
         handleBonecaAmbalabu(bonecaAmbalabu, tailKatanaValue, 21.75F, 20.0F, min3, max4, 20.0F, 21.75F, ForeShinUtils.getValueForInt(986895), (tailKatanaValue5, tailKatanaValue6) -> canFlag(tailKatanaValue5, tailKatanaValue6, floats));
      }
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.cup = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 10.4F, 0.0F);
      this.armL = latheLoft.getClassForStringForeEar("cup", this.cup, 0.0F, 10.4F, 0.0F);
      this.brows = latheLoft.getClassForStringForeEar("head", this.armL, 0.0F, 20.0F, 0.0F);
      this.eyesSection = latheLoft.getClassForStringForeEar("eyes", this.brows, 0.0F, 20.1F, 0.0F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("brows", this.brows, 0.0F, 21.0F, 0.0F);
      float shinFootValue = getAmounts(23.0F) + 0.12F + 0.12F;
      float[] shinFootValues = getAmounts(1.0F, shinFootValue, 1.0F, 0.0F);
      float[] amounts2 = getAmounts(-1.0F, shinFootValue, 0.95F, 0.5F);
      int index = 15;
      this.foreEar = latheLoft.getClassForStringForeEar("tail_r", this.brows, shinFootValues[0], shinFootValues[1], shinFootValues[2]);
      this.classShinFoot = latheLoft.getClassForStringForeEar("tail_r2", this.foreEar, shinFootValues[index], shinFootValues[index + 1], shinFootValues[index + 2]);
      this.classShinProgress = latheLoft.getClassForStringForeEar("tail_l", this.brows, amounts2[0], amounts2[1], amounts2[2]);
      this.tailLeftSection = latheLoft.getClassForStringForeEar("tail_l2", this.classShinProgress, amounts2[index], amounts2[index + 1], amounts2[index + 2]);
      this.tailKatanaForeEar = latheLoft.getClassForStringForeEar("crema", this.armL, 0.0F, 24.3F, 0.0F);
      this.foreR = latheLoft.getClassForStringForeEar("arm_r", this.armL, 4.0F, 15.2F, 0.0F);
      this.katanaR = latheLoft.getClassForStringForeEar("fore_r", this.foreR, foreKatanaAmounts[0], foreKatanaAmounts[1], foreKatanaAmounts[2]);
      this.katanaRightSection = latheLoft.getClassForStringForeEar("katana_r", this.katanaR, katanaForeAmounts[0], katanaForeAmounts[1], katanaForeAmounts[2]);
      this.foreL = latheLoft.getClassForStringForeEar("arm_l", this.armL, -4.0F, 15.2F, 0.0F);
      this.katanaL = latheLoft.getClassForStringForeEar("fore_l", this.foreL, -foreKatanaAmounts[0], foreKatanaAmounts[1], foreKatanaAmounts[2]);
      this.katanaLeftSection = latheLoft.getClassForStringForeEar("katana_l", this.katanaL, -katanaForeAmounts[0], katanaForeAmounts[1], katanaForeAmounts[2]);
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", this.cup, 1.9F, 10.9F, 0.0F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, shinFootAmounts[0], shinFootAmounts[1], shinFootAmounts[2]);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("foot_r", this.footR, footShinAmounts[0], footShinAmounts[1], footShinAmounts[2]);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", this.cup, -1.9F, 10.9F, 0.0F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -shinFootAmounts[0], shinFootAmounts[1], shinFootAmounts[2]);
      this.footLeftSection = latheLoft.getClassForStringForeEar("foot_l", this.footL, -footShinAmounts[0], footShinAmounts[1], footShinAmounts[2]);
      latheLoft.getLatheLoft(this.foreR);
      LoftLatheFlag.Region region = latheLoft.getMax(512, 256);
      handleBonecaAmbalabu(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabuForBonecaAmbalabu(max2.getClassBonecaAmbalabu());
      float amount3 = getAmount3(24.3F);
      float[] floats = new float[]{amount3 - 0.42F, amount3 - 0.62F, amount3 - 1.15F, 3.1F, 1.7F, 0.0F};
      float[] floats2 = new float[]{24.25F, 24.699999F, 25.08F, 25.279999F, 25.38F, 25.4F};
      LoftLatheFlag.Region max3 = latheLoft.getMax(256, 96);
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu(), floats, floats2);
      float[] shinFootTable3 = shinFootTable;
      float[] amountsFloatArray = getAmountsFloatArray(shinFootTable, tailKatanaTable);
      LoftLatheFlag.Region max4 = latheLoft.getMax(1016, 128);
      handleBonecaAmbalabuForBonecaAmbalabuFloatArray(max4.getClassBonecaAmbalabu(), amountsFloatArray, shinFootTable3);
      float[] table32 = table;
      float[] amountsFloatArray5 = getAmountsFloatArray(table, BLADE_THICKNESSES);
      LoftLatheFlag.Region max5 = latheLoft.getMax(512, 64);
      handle(max5.getClassBonecaAmbalabu(), amountsFloatArray5, table32);
      float[] tailKatanaValues = new float[]{22.4F, 22.449999F, 23.550001F, 23.6F};
      float[] floats4 = new float[]{getAmounts(tailKatanaValues[0]), getAmounts(tailKatanaValues[1]) + 0.12F, getAmounts(tailKatanaValues[2]) + 0.12F, getAmounts(tailKatanaValues[3])};
      LoftLatheFlag.Region max6 = latheLoft.getMax(512, 32);
      handleBonecaAmbalabu3(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(240, 48);
      LoftLatheFlag.Region max8 = latheLoft.getMax(1016, 56);
      handleForBonecaAmbalabu(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(1016, 72);
      handle(max9.getClassBonecaAmbalabu());
      float[] copyOf = getCopyOf(1.0F);
      LoftLatheFlag.Region max10 = latheLoft.getMax(40, 320);
      handleBonecaAmbalabu(max10.getClassBonecaAmbalabu(), copyOf, true);
      LoftLatheFlag.Region max11 = latheLoft.getMax(40, 320);
      handleBonecaAmbalabu(max11.getClassBonecaAmbalabu(), copyOf, false);
      LoftLatheFlag.Region max12 = latheLoft.getMax(32, 96);
      max12.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(7040884))
         .getBonecaAmbalabu(0.08F, 4.0F, 51)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(1776411), 12.0F, 6.5F, 62.0F)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(1776411), 12.0F, 6.5F, 118.0F);
      LoftLatheFlag.Region max13 = latheLoft.getMax(64, 64);
      max13.getClassBonecaAmbalabu().getBonecaAmbalabu(ForeShinUtils.getValueForInt(2565932)).getBonecaAmbalabu(0.12F, 6.0F, 61);
      LoftLatheFlag.Region max14 = latheLoft.getMax(64, 64);
      max14.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(2565932))
         .getBonecaAmbalabu(0.1F, 6.0F, 63)
         .getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(3947587, 0.9F), 9.0F, 2.2F, 70.0F);
      LoftLatheFlag.Region max15 = latheLoft.getMax(48, 96);
      handleBonecaAmbalabu(max15.getClassBonecaAmbalabu(), true);
      LoftLatheFlag.Region max16 = latheLoft.getMax(48, 48);
      handleBonecaAmbalabu(max16.getClassBonecaAmbalabu(), false);
      ShinBrr classForRegion = latheLoft.getClassForRegion(max13);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max14);
      ShinBrr shinBrr = latheLoft.getMax(3421242);
      ShinBrr shinBrr2 = latheLoft.getMax(1973794);
      ShinBrr shinBrr3 = latheLoft.getMax(1842987);
      latheLoft.getLatheLoftForForeEar(this.armL);
      int shinFootSize = 4;
      float[] latheLoftValues = new float[shinFootSize];
      float[] floats6 = new float[shinFootSize];
      float tailKatanaValue = 11.299999F;
      float shinFootValue4 = getAmount3(tailKatanaValue);

      for (int i = 0; i < shinFootSize; i++) {
         double angle = (Math.PI / 2) * i / (shinFootSize - 1);
         latheLoftValues[i] = shinFootValue4 - 0.9F + 0.9F * (float)Math.sin(angle);
         floats6[i] = tailKatanaValue - 0.9F * (float)Math.cos(angle);
      }

      latheLoft.getLatheLoft(latheLoftValues, floats6, 28, latheLoft.getMax(15789027), latheLoft.getClassForRegion(max2), null);
      latheLoft.getLatheLoft(new float[]{shinFootValue4, amount3}, new float[]{tailKatanaValue, 24.3F}, 28, latheLoft.getClassForRegion(region), null, null);
      latheLoft.getLatheLoft(
         new float[]{amount3 - 0.01F, amount3 + 0.07F, amount3 - 0.12F, amount3 - 0.37F, amount3 - 0.44F},
         new float[]{24.16F, 24.4F, 24.599998F, 24.5F, 24.15F},
         28,
         latheLoft.getMax(16777215),
         null,
         null
      );

      for (int j = -1; j <= 1; j += 2) {
         latheLoft.getLatheLoft(j * (getAmount3(15.2F) + 0.02F), 15.2F, 0.0F, 0.22F, 0.6F, 0.6F, 8, classForRegion);
      }

      latheLoft.getLatheLoftForForeEar(this.tailKatanaForeEar);
      latheLoft.getLatheLoft(floats, floats2, 28, latheLoft.getClassForRegion(max3), null, null);
      latheLoft.getLatheLoftForForeEar(this.brows);
      latheLoft.getLatheLoft(amountsFloatArray, shinFootTable3, 28, latheLoft.getClassForRegion(max4), null, null);
      latheLoft.getLatheLoft(amountsFloatArray5, table32, 28, latheLoft.getClassForRegion(max5), null, null);
      latheLoft.getLatheLoft(floats4, tailKatanaValues, 28, latheLoft.getClassForRegion(max6), null, null);
      this.handle(latheLoft, max7);
      latheLoft.getLatheLoft(0.0F, 23.0F, shinFootValue - 0.05F, 0.5F, 0.45F, 0.35F, 8, shinBrr3);

      for (int k = -1; k <= 1; k += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(k * 0.8F, 23.1F, shinFootValue - 0.14F).getLatheLoft(k * 25.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.75F, 0.4F, 0.25F, 6, shinBrr3);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      latheLoft.getLatheLoft(new float[]{getAmount3(20.0F) + 0.19F, getAmount3(21.75F) + 0.19F}, new float[]{20.0F, 21.75F}, 28, latheLoft.getClassForRegion(max8), null, null);
      latheLoft.getLatheLoftForForeEar(this.eyesSection);
      latheLoft.getLatheLoft(new float[]{getAmount3(19.2F) + 0.12F, getAmount3(21.2F) + 0.12F}, new float[]{19.2F, 21.2F}, 28, latheLoft.getClassForRegion(max9), null, null);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max15);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max16);
      latheLoft.getLatheLoftForForeEar(this.foreEar);
      handle(latheLoft, shinFootValues, 0, 5, 1.0F, classForRegion5, shinBrr3, null);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      handle(latheLoft, shinFootValues, 5, LatheLoft.getValue(shinFootValues) - 1, 1.0F, classForRegion4, null, null);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      handle(latheLoft, amounts2, 0, 5, -1.0F, classForRegion5, shinBrr3, null);
      latheLoft.getLatheLoftForForeEar(this.tailLeftSection);
      handle(latheLoft, amounts2, 5, LatheLoft.getValue(amounts2) - 1, -1.0F, classForRegion4, null, null);
      ShinBrr shinBrr4 = latheLoft.getMax(13214282);
      ShinBrr shinBrr5 = latheLoft.getMax(2763306);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max12);
      ShinBrr shinBrr6 = latheLoft.getMax(13938514);
      ShinBrr shinBrr7 = latheLoft.getMax(12831442);
      ShinBrr shinBrr8 = latheLoft.getMax(3026483);

      for (int m = -1; m <= 1; m += 2) {
         float katanaForeFactor = m;
         latheLoft.getLatheLoftForForeEar(m > 0 ? this.foreR : this.foreL);
         handleKatanaFore(latheLoft, katanaForeFactor, classForRegion);
         latheLoft.getLatheLoftForForeEar(m > 0 ? this.katanaR : this.katanaL);
         handleKatanaFore(latheLoft, katanaForeFactor, classForRegion, shinBrr);
         latheLoft.getLatheLoftForForeEar(m > 0 ? this.katanaRightSection : this.katanaLeftSection);
         handleKatanaFore(
            latheLoft,
            katanaForeFactor,
            getCopyOf(katanaForeFactor),
            latheLoft.getClassForRegion(m > 0 ? max10 : max11),
            latheLoft.getClassForRegion(m > 0 ? max11 : max10),
            classForRegion6,
            shinBrr4,
            shinBrr5,
            shinBrr6,
            shinBrr7,
            shinBrr8
         );
         latheLoft.getLatheLoftForForeEar(m > 0 ? this.shinR : this.shinL);
         handleShinFootForLatheLoftFloatShinBrr(latheLoft, katanaForeFactor, classForRegion);
         latheLoft.getLatheLoftForForeEar(m > 0 ? this.footR : this.footL);
         handleShinFootForLatheLoftFloat(latheLoft, katanaForeFactor, classForRegion3);
         latheLoft.getLatheLoftForForeEar(m > 0 ? this.shinProgressForeEar : this.footLeftSection);
         handleShinFoot(latheLoft, katanaForeFactor, shinBrr2);
      }
   }

   private static boolean canFlag(float tailKatanaValue, float tailKatanaValue2, float tailKatanaFactor, float tailKatanaValue3, float tailKatanaValue4, float tailKatanaValue5) {
      float tailKatanaLimit = (tailKatanaValue - tailKatanaFactor * 1.95F) * tailKatanaFactor / (1.62F + tailKatanaValue3);
      if (!(tailKatanaLimit <= -1.0F) && !(tailKatanaLimit >= 1.0F)) {
         float tailKatanaLimit2 = tailKatanaValue2 - 20.1F;
         float amountForFloat = getAmountForFloat(tailKatanaLimit);
         float tailKatanaValue7 = 0.04F + 0.12F * tailKatanaLimit;
         float tailKatanaLimit3 = Math.min(tailKatanaValue7 + amountForFloat, 0.24F + 0.58F * tailKatanaLimit) + tailKatanaValue4;
         float tailKatanaValue8 = tailKatanaValue7 - 0.85F * amountForFloat - tailKatanaValue5;
         return tailKatanaLimit2 <= tailKatanaLimit3 && tailKatanaLimit2 >= tailKatanaValue8;
      } else {
         return false;
      }
   }

   private static float getAmount4(float tailKatanaFactor) {
      tailKatanaFactor = tailKatanaFactor < 0.0F ? 0.0F : (tailKatanaFactor > 1.0F ? 1.0F : tailKatanaFactor);
      return tailKatanaFactor * tailKatanaFactor * (3.0F - 2.0F * tailKatanaFactor);
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float animationProgressScaled = poseProgress.animationProgress * 0.6662F;
      float shinProgressValue = poseProgress.sneakingPose ? 1.0F : 0.0F;
      float shinProgressValue2 = -0.42F * shinProgressFactor;
      float shinProgressValue3 = getAmount(poseProgress.shinProgressAmount, -1.0F, 1.0F);
      float shinProgressValue4 = (1.0F - shinProgressValue) * (1.0F - 0.35F * shinProgressFactor);
      float shinProgressTotal = poseProgress.getAnimationProgressForFloat(0.0F) * 0.62F + 0.72F * shinProgressValue + 0.35F * shinProgressValue4;
      float angle = poseProgress.getAnimationProgressForFloat((float) Math.PI) * 0.62F + 0.62F * shinProgressValue + 0.35F * shinProgressValue4;
      float shinProgressTotal3 = -poseProgress.getAnimationProgress((float) Math.PI) * 1.05F - 0.8F * shinProgressValue - 0.65F * shinProgressValue4;
      float shinFootValue = -poseProgress.getAnimationProgress(0.0F) * 1.05F - 0.74F * shinProgressValue - 0.65F * shinProgressValue4;
      float shinProgressTotal5 = -poseProgress.getAnimationProgress((float) Math.PI) * 0.45F - 0.9F * shinProgressValue + 0.3F * shinProgressValue4;
      float foreShinValue = -poseProgress.getAnimationProgress(0.0F) * 0.45F - 0.9F * shinProgressValue + 0.3F * shinProgressValue4;
      float shinProgressValue5 = 0.0F;
      float shinProgressFactor2 = 0.0F;
      float shinProgressFactor3 = 0.0F;
      float sin4 = 0.0F;
      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         shinProgressValue5 = getAmount4(swingProgress / 0.1F) * (1.0F - getAmount4((swingProgress - 0.14F) / 0.12F));
         shinProgressFactor2 = getAmount4((swingProgress - 0.12F) / 0.14F) * (1.0F - getAmount4((swingProgress - 0.82F) / 0.18F));
         shinProgressFactor3 = getAmount4((swingProgress - 0.2F) / 0.62F);
         sin4 = (float)Math.sin(shinProgressFactor3 * Math.PI);
         shinProgressTotal += 0.35F * shinProgressValue5;
         angle += 0.35F * shinProgressValue5;
         shinProgressTotal3 -= 0.6F * shinProgressValue5;
         shinFootValue -= 0.6F * shinProgressValue5;
         shinProgressTotal5 += 0.25F * shinProgressValue5;
         foreShinValue += 0.25F * shinProgressValue5;
      }

      if (!poseProgress.vehicle && !poseProgress.gliding && !poseProgress.swimming) {
         float maxShinFoot = Math.max(getShinFoot(shinProgressTotal, shinProgressTotal3, shinProgressTotal5), getShinFoot(angle, shinFootValue, foreShinValue));
         foreShinScale.getAmountsForeShin2(this.cup, 0.0F, maxShinFoot - 10.9F, 0.0F);
      }

      if (poseProgress.swingProgress > 0.0F) {
         foreShinScale.getAmounts(this.cup, 0.0F, -shinProgressFactor3 * 2.0F * (float) Math.PI, 0.17F * sin4);
         foreShinScale.getAmountsForeShin2(this.cup, 0.0F, 1.2F * sin4, 0.0F);
         shinProgressTotal += 0.3F * sin4;
         angle += 0.3F * sin4;
         shinProgressTotal3 -= 0.7F * sin4;
         shinFootValue -= 0.7F * sin4;
      }

      foreShinScale.getAmounts(
         this.armL,
         shinProgressValue2 + shinProgressValue3 * 0.1F - 0.32F * shinProgressValue - 0.2F * shinProgressValue5 - 0.08F * shinProgressFactor2,
         poseProgress.headYawRadians * 0.25F,
         0.035F * poseProgress.getAnimationProgressForFloat(0.0F) + 0.06F * shinProgressValue * (float)Math.sin(animationProgressScaled * 0.5F)
      );
      foreShinScale.getAmounts(this.brows, 0.0F, getAmount(poseProgress.headYawRadians, -1.3F, 1.3F) * 0.75F, 0.0F);
      foreShinScale.getAmountsForeShin2(this.eyesSection, 0.0F, shinProgressValue3 * 0.45F, 0.0F);
      foreShinScale.getAmountsForeShin2(this.shinFootForeEar, 0.0F, shinProgressValue3 * 0.42F, 0.0F);
      foreShinScale.getAmounts(this.shinR, shinProgressTotal, 0.0F, 0.08F + 0.03F * shinProgressFactor);
      foreShinScale.getAmounts(this.shinL, angle, 0.0F, -0.08F - 0.03F * shinProgressFactor);
      foreShinScale.getAmounts(this.footR, shinProgressTotal3, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footL, shinFootValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, shinProgressTotal5, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footLeftSection, foreShinValue, 0.0F, 0.0F);
      float shinProgressFactor5 = 0.0F;
      float shinProgressValue6 = 0.0F;
      float shinProgressFactor6 = 0.0F;
      if (shinProgressFactor < 0.3F && poseProgress.swingProgress <= 0.0F && !poseProgress.poseProgressActive) {
         float shinProgressFactor7 = 1.0F - shinProgressFactor / 0.3F;
         float shinProgressLimit = getAmount2(poseProgress.age + (poseProgress.hashCode & 127), 160.0F);
         if (shinProgressLimit >= 27.0F && shinProgressLimit < 55.0F) {
            float shinProgressValue7 = shinProgressLimit - 27.0F;
            shinProgressFactor5 = getAmount4(shinProgressValue7 / 4.0F) * (1.0F - getAmount4((shinProgressValue7 - 22.0F) / 6.0F)) * shinProgressFactor7;
            shinProgressValue6 = getAmount4((shinProgressValue7 - 3.0F) / 17.0F) * shinProgressFactor7;
         } else if (shinProgressLimit >= 55.0F && shinProgressLimit < 71.0F) {
            float shinProgressValue8 = shinProgressLimit - 55.0F;
            shinProgressFactor6 = getAmount4(shinProgressValue8 / 2.0F) * (1.0F - getAmount4((shinProgressValue8 - 12.0F) / 4.0F)) * shinProgressFactor7;
         }
      }

      float shinProgressFactor8 = (!(poseProgress.swingProgress > 0.0F) && !poseProgress.sneakingPose ? 1.0F - shinProgressFactor : 0.0F) * (1.0F - shinProgressFactor6);
      float shinProgressFactor9 = shinProgressFactor8 * (1.0F - shinProgressFactor5);
      float shinProgressValue9 = 0.035F * poseProgress.getAgeCode(70.0F, 0.0F);
      foreShinScale.getAmounts(this.foreR, -1.2F * shinProgressFactor + 0.1F * poseProgress.getAnimationProgressForFloat((float) Math.PI), 0.0F, 0.2F * shinProgressFactor + shinProgressValue9);
      foreShinScale.getAmounts(this.foreL, -1.2F * shinProgressFactor + 0.1F * poseProgress.getAnimationProgressForFloat(0.0F), 0.0F, -0.2F * shinProgressFactor - shinProgressValue9);
      foreShinScale.getAmounts(this.katanaR, -0.85F * shinProgressFactor, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.katanaL, -0.85F * shinProgressFactor, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.katanaRightSection, 4.5F * shinProgressFactor, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.katanaLeftSection, 4.5F * shinProgressFactor, 0.0F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.foreR, 0.45F * shinProgressFactor9, 0.0F, 0.72F * shinProgressFactor9);
      foreShinScale.getAmountsBoneCount(this.katanaR, 0.9F * shinProgressFactor9, 0.0F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.katanaRightSection, -0.3F * shinProgressFactor9, 0.0F, -0.16F * shinProgressFactor9);
      foreShinScale.getAmountsBoneCount(this.foreL, 0.3F * shinProgressFactor8, 0.0F, -0.5F * shinProgressFactor8);
      foreShinScale.getAmountsBoneCount(this.katanaL, 0.3F * shinProgressFactor8, 0.0F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.katanaLeftSection, -2.35F * shinProgressFactor8, 0.45F * shinProgressFactor8, 0.0F);
      foreShinScale.getAmountsBoneCount(this.foreR, 0.95F * shinProgressFactor5, 0.0F, -0.1F * shinProgressFactor5);
      foreShinScale.getAmountsBoneCount(this.katanaR, -0.4F * shinProgressFactor5, 0.0F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.katanaRightSection, -shinProgressValue6 * 4.0F * (float) Math.PI, 0.0F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.foreR, 0.9F * shinProgressFactor6, 0.0F, -0.4F * shinProgressFactor6);
      foreShinScale.getAmountsBoneCount(this.foreL, 0.9F * shinProgressFactor6, 0.0F, 0.4F * shinProgressFactor6);
      foreShinScale.getAmountsBoneCount(this.katanaR, 0.7F * shinProgressFactor6, 0.0F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.katanaL, 0.7F * shinProgressFactor6, 0.0F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.katanaRightSection, -1.2F * shinProgressFactor6, 1.0F * shinProgressFactor6, 0.0F);
      foreShinScale.getAmountsBoneCount(this.katanaLeftSection, -1.2F * shinProgressFactor6, -1.0F * shinProgressFactor6, 0.0F);
      foreShinScale.getAmountsBoneCount(this.armL, -0.05F * shinProgressFactor6, 0.0F, 0.0F);
      float shinProgressValue10 = 0.12F - 0.55F * shinProgressFactor;
      float shinProgressFactor10 = 1.0F - shinProgressFactor;
      float shinProgressFactor11 = 1.3F * shinProgressFactor;
      float shinProgressValue11 = 0.75F * shinProgressFactor;
      float shinProgressValue12 = shinProgressFactor10 * poseProgress.getAgeCode(19.0F, 0.0F) + shinProgressFactor11 * poseProgress.getAgeCode(8.0F, 0.0F);
      float shinProgressValue13 = shinProgressFactor10 * poseProgress.getAgeCode(19.0F, -1.3F) + shinProgressFactor11 * poseProgress.getAgeCode(8.0F, -1.4F);
      float shinProgressValue14 = shinProgressFactor10 * poseProgress.getAgeCode(21.0F, 1.6F) + shinProgressFactor11 * poseProgress.getAgeCode(9.0F, 1.6F);
      float shinProgressFactor12 = shinProgressFactor10 * poseProgress.getAgeCode(21.0F, 0.3F) + shinProgressFactor11 * poseProgress.getAgeCode(9.0F, 0.2F);
      foreShinScale.getAmounts(this.foreEar, -shinProgressValue10 + 0.13F * shinProgressValue12, 0.14F * poseProgress.getAgeCode(27.0F, 0.7F) - shinProgressValue11, 0.06F * poseProgress.getAgeCode(23.0F, 2.0F));
      foreShinScale.getAmounts(this.classShinFoot, 0.05F * shinProgressFactor + 0.12F * shinProgressValue13, 0.2F * poseProgress.getAgeCode(27.0F, -0.6F) - 0.3F * shinProgressFactor, 0.18F * shinProgressValue13);
      foreShinScale.getAmounts(this.classShinProgress, -shinProgressValue10 * 0.9F + 0.13F * shinProgressValue14, 0.14F * poseProgress.getAgeCode(29.0F, 2.4F) + shinProgressValue11, 0.06F * poseProgress.getAgeCode(25.0F, 0.3F));
      foreShinScale.getAmounts(this.tailLeftSection, 0.05F * shinProgressFactor + 0.12F * shinProgressFactor12, 0.2F * poseProgress.getAgeCode(29.0F, 1.1F) + 0.3F * shinProgressFactor, -0.18F * shinProgressFactor12);
      float shinProgressValue15 = 0.05F * (float)Math.sin(animationProgressScaled * 2.0F) * shinProgressFactor;
      foreShinScale.getAmounts(
         this.tailKatanaForeEar,
         getAmount(-shinProgressValue2 * 0.1F + shinProgressValue15 + 0.04F * shinProgressValue5, -0.07F, 0.07F),
         0.0F,
         getAmount(0.05F * (float)Math.cos(animationProgressScaled * 2.0F) * shinProgressFactor + 0.08F * sin4, -0.07F, 0.07F)
      );
      float amount2 = getAmount2(poseProgress.age + (poseProgress.hashCode & 127) + 60.0F, 110.0F);
      if (amount2 < 4.0F) {
         foreShinScale.getAmountsForForeEarFloat(this.eyesSection, 1.0F, 0.12F + 0.88F * Math.abs(amount2 - 2.0F) / 2.0F, 1.0F);
      }

      if (poseProgress.swingProgress > 0.0F) {
         foreShinScale.getAmounts(this.foreR, 0.8F * shinProgressValue5, 0.0F, 2.0F * shinProgressFactor2 + 0.1F * shinProgressValue5);
         foreShinScale.getAmounts(this.foreL, 0.8F * shinProgressValue5, 0.0F, -0.7F * shinProgressFactor2 - 0.1F * shinProgressValue5);
         foreShinScale.getAmounts(this.katanaR, -1.1F * shinProgressFactor2 + 0.35F * shinProgressValue5, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.katanaL, -1.1F * shinProgressFactor2 + 0.35F * shinProgressValue5, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.katanaRightSection, -1.2F * shinProgressFactor2 - 0.7F * shinProgressValue5, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.katanaLeftSection, -1.5F * shinProgressFactor2 - 0.7F * shinProgressValue5, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.armL, 0.0F, 0.0F, 0.22F * shinProgressFactor2);
         foreShinScale.getAmountsBoneCount(this.foreEar, -0.3F * shinProgressFactor2, 0.75F * sin4, 0.5F * sin4);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, -0.3F * shinProgressFactor2, 0.75F * sin4, -0.5F * sin4);
      }

      if (poseProgress.sneakingPose && poseProgress.swingProgress <= 0.0F) {
         float shinProgressValue17 = 0.05F * poseProgress.getAgeCode(40.0F, 0.0F);
         foreShinScale.getAmounts(this.foreR, 0.45F + shinProgressValue17, 0.9F, 0.5F);
         foreShinScale.getAmounts(this.foreL, 0.45F + shinProgressValue17, -0.9F, -0.5F);
         foreShinScale.getAmounts(this.katanaR, 0.2F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.katanaL, 0.2F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.katanaRightSection, -0.3F, 0.45F, 0.5F);
         foreShinScale.getAmounts(this.katanaLeftSection, -0.24F, -0.45F, -0.5F);
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.shinR, 1.35F, 0.1F, 0.3F);
         foreShinScale.getAmounts(this.shinL, 1.35F, -0.1F, -0.3F);
         foreShinScale.getAmounts(this.footR, -0.4F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.4F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, -0.35F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footLeftSection, -0.35F, 0.0F, 0.0F);
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         foreShinScale.getAmounts(this.foreR, -1.3F, 0.0F, 0.2F);
         foreShinScale.getAmounts(this.foreL, -1.3F, 0.0F, -0.2F);
         foreShinScale.getAmounts(this.katanaR, -0.9F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.katanaL, -0.9F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.katanaRightSection, 3.4F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.katanaLeftSection, 3.4F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.foreEar, 0.9F, 0.0F, -1.0F);
         foreShinScale.getAmounts(this.classShinProgress, 0.85F, 0.0F, 1.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, -0.5F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footLeftSection, -0.5F, 0.0F, 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.06F, 0.94F, 1.06F);
      }
   }

   @Override
   public int getValue() {
      return 2;
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(1842987)).getBonecaAmbalabu(0.14F, 8.0F, 71);

      for (int i = 0; i < 3; i++) {
         float bonecaAmbalabuValue = 7.5F + i * 8.5F;
         float bonecaAmbalabuValue2 = i * 2.1F;
         float bonecaAmbalabuValue3 = i * 1.3F + 0.4F;
         bonecaAmbalabu.getBonecaAmbalabu(
            0.0F,
            bonecaAmbalabuValue - 3.0F,
            bonecaAmbalabu.ambalabuBonecaValue,
            bonecaAmbalabuValue + 3.0F,
            ForeShinUtils.getValueForIntFloat(3883608, 0.4F),
            (bonecaAmbalabuValue4, bonecaAmbalabuValue5) -> Math.abs(bonecaAmbalabuValue5 + 0.9F - bonecaAmbalabuValue - 1.6F * (float)Math.sin(bonecaAmbalabuValue4 * 0.045F + bonecaAmbalabuValue2)) < 0.5F && Math.sin(bonecaAmbalabuValue4 * 0.017F + bonecaAmbalabuValue3) > -0.35F
         );
         bonecaAmbalabu.getBonecaAmbalabu(
            0.0F,
            bonecaAmbalabuValue - 3.0F,
            bonecaAmbalabu.ambalabuBonecaValue,
            bonecaAmbalabuValue + 3.0F,
            ForeShinUtils.getValueForIntFloat(921624, 0.75F),
            (bonecaAmbalabuValue6, bonecaAmbalabuValue7) -> Math.abs(bonecaAmbalabuValue7 - bonecaAmbalabuValue - 1.6F * (float)Math.sin(bonecaAmbalabuValue6 * 0.045F + bonecaAmbalabuValue2)) < 0.6F && Math.sin(bonecaAmbalabuValue6 * 0.017F + bonecaAmbalabuValue3) > -0.35F
         );
      }
   }

   private static void handleShinFootForLatheLoftFloatShinBrr(LatheLoft latheLoft, float shinFootFactor, ShinBrr shinBrr) {
      latheLoft.getLatheLoft(
         new float[]{shinFootFactor * 1.9F, 11.3F, 0.0F, shinFootFactor * 2.2F, 8.4F, -0.42F, shinFootFactor * shinFootAmounts[0], shinFootAmounts[1], shinFootAmounts[2]},
         new float[]{0.46F, 0.44F, 0.42F},
         8,
         shinBrr,
         false
      );
      latheLoft.getLatheLoft(shinFootFactor * shinFootAmounts[0], shinFootAmounts[1], shinFootAmounts[2], 0.43F, 8, shinBrr);
   }

   private static float getAmount2(float tailKatanaValue, float tailKatanaValue2) {
      float tailKatanaValue3 = tailKatanaValue % tailKatanaValue2;
      return tailKatanaValue3 < 0.0F ? tailKatanaValue3 + tailKatanaValue2 : tailKatanaValue3;
   }

   private static float getAmount3(float tailKatanaFactor, float tailKatanaValue) {
      return -tailKatanaValue * 0.5F * tailKatanaFactor * tailKatanaFactor;
   }

   private static float[] getCopyOf(float tailKatanaFactor) {
      byte tailKatanaLimit = 5;
      byte tailKatanaLimit2 = 3;
      float tailKatanaValue = 13.07F;
      float[] floats = new float[2 * (tailKatanaLimit + 1 + tailKatanaLimit2 + tailKatanaLimit + 1)];
      int tailKatanaCounter = 0;

      for (int i = 0; i <= tailKatanaLimit; i++) {
         float tailKatanaValue2 = 1.37F + 11.7F * i / tailKatanaLimit;
         float tailKatanaValue3 = (tailKatanaValue2 - 1.37F) / 11.7F;
         floats[tailKatanaCounter++] = getAmount3(tailKatanaValue3, tailKatanaFactor) - tailKatanaFactor * getAmount(tailKatanaValue3) * 0.5F;
         floats[tailKatanaCounter++] = tailKatanaValue2;
      }

      for (int j = 1; j <= tailKatanaLimit2; j++) {
         float tailKatanaValue4 = (float)j / tailKatanaLimit2;
         float tailKatanaValue5 = tailKatanaValue - 1.25F * tailKatanaValue4;
         float tailKatanaValue6 = (tailKatanaValue5 - 1.37F) / 11.7F;
         float tailKatanaValue7 = getAmount3(tailKatanaValue6, tailKatanaFactor) - tailKatanaFactor * getAmount(tailKatanaValue6) * 0.5F;
         float tailKatanaFactor2 = 1.0F - (1.0F - tailKatanaValue4) * (1.0F - tailKatanaValue4);
         floats[tailKatanaCounter++] = tailKatanaValue7 + tailKatanaFactor * getAmount(tailKatanaValue6) * tailKatanaFactor2;
         floats[tailKatanaCounter++] = tailKatanaValue5;
      }

      float tailKatanaValue8 = tailKatanaValue - 1.25F;

      for (int k = 1; k <= tailKatanaLimit; k++) {
         float tailKatanaValue9 = tailKatanaValue8 - (tailKatanaValue8 - 1.37F) * k / tailKatanaLimit;
         float tailKatanaValue10 = (tailKatanaValue9 - 1.37F) / 11.7F;
         floats[tailKatanaCounter++] = getAmount3(tailKatanaValue10, tailKatanaFactor) + tailKatanaFactor * getAmount(tailKatanaValue10) * 0.5F;
         floats[tailKatanaCounter++] = tailKatanaValue9;
      }

      return Arrays.copyOf(floats, tailKatanaCounter);
   }

   private static float[] getAmountsFloatArray(float[] tailKatanaValues, float[] tailKatanaValues2) {
      float[] floats = new float[tailKatanaValues.length];

      for (int i = 0; i < tailKatanaValues.length; i++) {
         floats[i] = getAmount3(tailKatanaValues[i]) + tailKatanaValues2[i];
      }

      return floats;
   }

   @Override
   public String formatTungDin() {
      return "cappuccino_assassino";
   }

   @Override
   public float getAmountFloat() {
      return 1.4F;
   }

   private static float getShinFoot(float angle, float shinFootValue, float foreShinValue) {
      float angle2 = shinFootValue + angle;
      float angle3 = angle2 + foreShinValue;
      float shinFootValue3 = -(10.9F - shinFootAmounts[1]) * (float)Math.cos(angle) - shinFootAmounts[2] * (float)Math.sin(angle);
      float shinFootValue4 = -(shinFootAmounts[1] - footShinAmounts[1]) * (float)Math.cos(angle2) - (footShinAmounts[2] - shinFootAmounts[2]) * (float)Math.sin(angle2);
      float cos = (float)Math.cos(angle3);
      float sin = (float)Math.sin(angle3);
      float shinFootBound = -footShinAmounts[1] * cos - -1.65F * sin;
      float hashHuntBound = -footShinAmounts[1] * cos - 0.55F * sin;
      return -(shinFootValue3 + shinFootValue4 + Math.min(shinFootBound, hashHuntBound));
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabuFloatArray(BonecaAmbalabu bonecaAmbalabu, float[] bonecaAmbalabuValues, float[] tailKatanaValues) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(1315860));
      float[] amountsForFloatArray = getAmountsForFloatArray(bonecaAmbalabuValues, tailKatanaValues);
      float bonecaAmbalabuValue = bonecaAmbalabuValues[2];

      for (int i = 0; i < 3; i++) {
         float bonecaAmbalabuValue2 = 16.9F + i * 0.62F;
         float bonecaAmbalabuValue3 = 1.9F * i;
         handle(
            bonecaAmbalabu,
            bonecaAmbalabuValue,
            amountsForFloatArray,
            -20.0F,
            20.0F,
            bonecaAmbalabuValue2 - 0.2F,
            bonecaAmbalabuValue2 + 0.2F,
            (bonecaAmbalabuValue4, bonecaAmbalabuValue5) -> Math.abs(bonecaAmbalabuValue5 - bonecaAmbalabuValue2 - 0.05F * (float)Math.sin(bonecaAmbalabuValue4 * 1.3F + bonecaAmbalabuValue2)) < 0.045F && Math.sin(bonecaAmbalabuValue4 * 0.5F + bonecaAmbalabuValue3) > -0.5
         );
      }

      for (int j = -1; j <= 1; j += 2) {
         float bonecaAmbalabuValue6 = j;
         handle(
            bonecaAmbalabu,
            bonecaAmbalabuValue,
            amountsForFloatArray,
            -3.0F,
            3.0F,
            15.7F,
            18.75F,
            (bonecaAmbalabuValue7, bonecaAmbalabuValue8) -> getAmount(bonecaAmbalabuValue7, bonecaAmbalabuValue8, bonecaAmbalabuValue6 * 0.15F, 15.95F, bonecaAmbalabuValue6 * 1.6F, 17.3F) < 0.045F
               || getAmount(bonecaAmbalabuValue7, bonecaAmbalabuValue8, bonecaAmbalabuValue6 * 0.3F, 16.1F, bonecaAmbalabuValue6 * 2.6F, 18.45F) < 0.045F
         );
      }

      float[] amounts4 = getAmounts(bonecaAmbalabuValues, tailKatanaValues);

      for (int k = 0; k < bonecaAmbalabu.bonecaValue; k++) {
         float tailKatanaValue = 1.0F - (float)k / (bonecaAmbalabu.bonecaValue - 1);
         float bonecaAmbalabuValue10 = getAmount(tailKatanaValues, amounts4, tailKatanaValue);
         float bonecaAmbalabuFactor = getAmount(bonecaAmbalabuValues, amounts4, tailKatanaValue);

         for (int m = 0; m < bonecaAmbalabu.ambalabuBonecaValue; m++) {
            float planValuesItem = (0.5F - (float)m / (bonecaAmbalabu.ambalabuBonecaValue - 1)) * 2.0F * (float) Math.PI * bonecaAmbalabuFactor;
            float bonecaAmbalabuValue12 = 16.4F - 0.6999998F * Math.max(0.0F, 1.0F - Math.abs(planValuesItem) / 1.8F);
            if (bonecaAmbalabuValue10 < bonecaAmbalabuValue12 - 0.01F) {
               bonecaAmbalabu.getBonecaAmbalabu(m, k, 0);
            }
         }
      }
   }

   private static void handle(LatheLoft latheLoft, float[] tailKatanaValues, int tailKatanaValue, int tailKatanaValue2, float tailKatanaValue3, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      int latheLoftSize = LatheLoft.getValue(tailKatanaValues);
      float[] pointsSpline = LatheLoft.getAmounts(latheLoftSize, 0.1F, 0.08F, 0.07F);
      float[] amounts3 = LatheLoft.getAmounts(latheLoftSize, 0.5F, 0.75F, 0.82F, 0.7F);
      latheLoft.getLoftPath(
         Arrays.copyOfRange(tailKatanaValues, 3 * tailKatanaValue, 3 * (tailKatanaValue2 + 1)),
         Arrays.copyOfRange(pointsSpline, tailKatanaValue, tailKatanaValue2 + 1),
         Arrays.copyOfRange(amounts3, tailKatanaValue, tailKatanaValue2 + 1),
         6,
         shinBrr,
         shinBrr2,
         shinBrr3,
         tailKatanaValue3 * 0.15F,
         1.0F,
         0.9F
      );
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float[] tailKatanaValues, float[] tailKatanaValues2) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(1315860));
      float[] amountsForFloatArray = getAmountsForFloatArray(tailKatanaValues, tailKatanaValues2);
      float tailKatanaFactor = tailKatanaValues[2];
      float tailKatanaFactor2 = (float) Math.PI * tailKatanaFactor;

      for (int i = 0; i < 2; i++) {
         float tailKatanaValue = 22.300001F + i * 1.45F;
         handle(bonecaAmbalabu, tailKatanaFactor, amountsForFloatArray, -20.0F, 20.0F, tailKatanaValue - 0.2F, tailKatanaValue + 0.2F, (tailKatanaValue2, tailKatanaValue3) -> Math.abs(tailKatanaValue3 - tailKatanaValue - 0.04F * (float)Math.sin(tailKatanaValue2 * 1.1F + tailKatanaValue * 3.0F)) < 0.045F);
      }

      for (int j = -1; j <= 1; j += 2) {
         float tailKatanaValue4 = j * tailKatanaFactor2;
         float tailKatanaValue5 = -j;
         handle(
            bonecaAmbalabu,
            tailKatanaFactor,
            amountsForFloatArray,
            -20.0F,
            20.0F,
            21.95F,
            24.15F,
            (tailKatanaValue6, tailKatanaValue7) -> getAmount(tailKatanaValue6, tailKatanaValue7, tailKatanaValue4, 23.0F, tailKatanaValue4 + tailKatanaValue5 * 2.2F, 22.25F) < 0.05F
               || getAmount(tailKatanaValue6, tailKatanaValue7, tailKatanaValue4, 23.0F, tailKatanaValue4 + tailKatanaValue5 * 2.5F, 23.7F) < 0.05F
               || getAmount(tailKatanaValue6, tailKatanaValue7, tailKatanaValue4, 23.0F, tailKatanaValue4 + tailKatanaValue5 * 1.6F, 22.95F) < 0.045F
         );
      }
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float tailKatanaValue, float[] tailKatanaValues, float tailKatanaValue2, float tailKatanaValue3, float tailKatanaValue4, float tailKatanaValue5, TailKatana.Area area) {
      handleBonecaAmbalabu(bonecaAmbalabu, tailKatanaValue, tailKatanaValues[0], tailKatanaValues[1], tailKatanaValue2, tailKatanaValue3, tailKatanaValue4 - 0.12F, tailKatanaValue5, ForeShinUtils.getValueForIntFloat(657930, 0.9F), (areaValue, tailKatanaValue7) -> area.at(areaValue, tailKatanaValue7 + 0.07F));
      handleBonecaAmbalabu(bonecaAmbalabu, tailKatanaValue, tailKatanaValues[0], tailKatanaValues[1], tailKatanaValue2, tailKatanaValue3, tailKatanaValue4, tailKatanaValue5, ForeShinUtils.getValueForIntFloat(4473932, 0.9F), area);
   }

   private interface Area {
      boolean at(float areaValue, float areaValue2);
   }
}
