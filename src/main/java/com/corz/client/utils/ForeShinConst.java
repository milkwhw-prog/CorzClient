package com.corz.client.utils;

import com.corz.client.utils.LoftLatheFlag.Region;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.PoseProgress;

public final class ForeShinConst extends WarmBrainrotBase {
   private static final float[] shinFootTable = new float[]{3.5F, 3.55F, 3.86F, 3.98F, 3.84F, 3.2F, 1.5F};
   private static final float[] foreShinTable = new float[]{10.4F, 13.6F, 16.6F, 19.4F, 20.8F, 21.75F, 22.6F};
   private ForeEar classShinFoot;
   private ForeEar classShinProgress;
   private ForeEar foreR;
   private static final float[] table = new float[]{4.85F, 4.25F, 3.8F, 3.62F};
   private static final float[] ARM_PROFILE_HEIGHTS = new float[]{6.3F, 9.1F, 11.5F, 13.2F};
   private ForeEar shinR;
   private static final float[] BODY_PROFILE_RADII = new float[]{0.6F, 2.35F, 3.0F, 2.8F, 2.3F, 1.6F, 0.0F};
   private ForeEar shinL;
   private static final float[] BODY_PROFILE_HEIGHTS = new float[]{22.5F, 22.9F, 23.95F, 25.2F, 26.4F, 27.4F, 27.85F};
   private static final float[] CAP_PROFILE_RADII = new float[]{2.45F, 2.47F, 2.3F, 2.1F};
   private static final float[] CAP_PROFILE_HEIGHTS = new float[]{-0.15F, 0.8F, 1.6F, 2.02F};
   private static final float[] pointsSpline = LatheLoft.getPointsSpline(
      1, 0.0F, 6.2F, 1.7F, 0.0F, 4.4F, 3.2F, 0.0F, 2.6F, 4.9F, 0.0F, 1.2F, 6.8F, 0.0F, 0.6F, 8.6F, 0.0F, 0.52F, 10.0F, 0.0F, 0.5F, 10.5F, 0.0F, 0.5F, 10.75F
   );
   private static final float[] bonecaTable = new float[]{1.2F, 2.0F, 2.55F, 2.7F, 2.35F, 1.55F, 0.95F, 0.3F};
   private ForeEar shinFootForeEar;
   private ForeEar flash;
   private ForeEar body;
   private ForeEar shinProgressForeEar;
   private ForeEar foreShinForeEar;
   private ForeEar foreL;
   private static final float[] TAIL_RADII = new float[]{0.55F, 0.5F, 0.45F, 0.42F, 0.36F, 0.28F, 0.21F, 0.12F};
   private static final float[] shinProgressTable = getAmountsForFloat(0.0F, -0.1F, -5.0F);
   private ForeEar footR;
   private ForeEar foreEar;
   private static final float[] ARM_REST_OFFSET = getAmountsForFloat(-0.85F, -0.5F, -2.55F);
   private ForeEar tailSection;
   private static final float[] flashLegAmounts = getAmountsForFloat(0.0F, 1.05F, -9.75F);
   private ForeEar cigar;
   private ForeEar ember;
   private static final float[] SWING_AXIS = getAmounts(0.0F, 0.0F, -1.0F);
   private static final float[] cigarEmberAmounts = new float[]{1.0F, 22.95F, -3.3F};
   private ForeEar emberSection;
   private ForeEar footL;
   private ForeEar flashSection;
   private ForeEar[] shinFootForeEars;
   private static final float[] emberArmAmounts = new float[]{2.55F, 23.5F, -5.0F};
   private ForeEar foreRightSection;
   private static final float[] MAIN_OUTLINE_PAIRS = new float[]{0.0F, 14.8F, 2.142F, 22.45F, 2.9F, 22.55F, 3.2F, 20.95F, 2.68F, 20.5F, 3.4F, 19.9F, 0.85F, 14.62F};
   private static final float[] SECONDARY_OUTLINE_PAIRS = new float[]{0.3F, 22.3F, 1.25F, 22.5F, 0.62F, 21.15F};
   private ForeEar armL;

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, ForeShinConst.Wrap wrap, ForeShinConst.Area area) {
      float[] floats = new float[3];

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            wrap.handleAmountsAmount(bonecaAmbalabu, j + 0.5F, i + 0.5F, floats);
            if (area.in(floats[0], floats[1], floats[2])) {
               bonecaAmbalabu.getBonecaAmbalabu(j, i, 0);
            }
         }
      }
   }

   private static void handleAmountsBoneca(BonecaAmbalabu bonecaAmbalabu) {
      int bonecaSize = pointsSpline.length / 3;
      float[] floats = new float[bonecaSize];

      for (int i = 1; i < bonecaSize; i++) {
         float bonecaFactor = pointsSpline[3 * i] - pointsSpline[3 * i - 3];
         float bonecaFactor2 = pointsSpline[3 * i + 1] - pointsSpline[3 * i - 2];
         float bonecaFactor3 = pointsSpline[3 * i + 2] - pointsSpline[3 * i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(bonecaFactor * bonecaFactor + bonecaFactor2 * bonecaFactor2 + bonecaFactor3 * bonecaFactor3);
      }

      float bonecaValue = floats[bonecaSize - 1];
      float bonecaDivisor = 0.62F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4142641)).getBonecaAmbalabu(0.06F, 6.0F, 71);
      float[] foreShinValues = new float[4];

      for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
         for (int k = 0; k < bonecaAmbalabu.ambalabuBonecaValue; k++) {
            handleAmountsBoneca(bonecaAmbalabu, floats, bonecaValue, k + 0.5F, j + 0.5F, bonecaDivisor, foreShinValues);
            float foreShinFactor = 0.9F + 0.2F * getAmount((int)foreShinValues[2] * 31 + 7, (int)foreShinValues[3] + 101) - 0.12F * foreShinValues[1];
            bonecaAmbalabu.getBonecaAmbalabu(k, j, ForeShinUtils.getValue(bonecaAmbalabu.getBonecaAmbalabuForInt(k, j), foreShinFactor));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(5918535, 0.55F), (total, bonecaValue4) -> {
         handleAmountsBoneca(bonecaAmbalabu, floats, bonecaValue, total, bonecaValue4, bonecaDivisor, foreShinValues);
         float var7x = getAmount(foreShinValues);
         return var7x > 0.6F && var7x <= 0.84F && foreShinValues[1] > 0.5F;
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(2893090, 0.7F), (total, bonecaValue5) -> {
         handleAmountsBoneca(bonecaAmbalabu, floats, bonecaValue, total, bonecaValue5, bonecaDivisor, foreShinValues);
         return getAmount(foreShinValues) > 0.84F;
      });
      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(2893090, 0.35F), 0.4F, 0.8F, 72);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.09F, ForeShinUtils.getValueForInt(4532759));

      for (int m = 0; m < 90; m++) {
         float classBound = getAmount(m, 9) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(m, 10) * bonecaAmbalabu.bonecaValue * 0.12F;
         bonecaAmbalabu.getClass02(classBound, amountForFloat, classBound + 1.0F, amountForFloat + 3.5F, 0.9F, ForeShinUtils.getValueForIntFloat(7029801, 0.6F));
      }
   }

   private static boolean canFlag(float foreShinValue, float foreShinLimit) {
      return foreShinLimit > 17.3F && Math.abs(foreShinValue) < (foreShinLimit - 17.3F) * 0.2F;
   }

   private static float getAmountForFloat(float angle, float foreShinValue, float foreShinValue2) {
      return 4.2000003F * (float)Math.cos(angle) + 3.55F * (float)Math.cos(angle + foreShinValue) - getAmount3(angle + foreShinValue + foreShinValue2);
   }

   private static boolean canFlagForFloat(float foreShinValue, float foreShinLimit) {
      return foreShinLimit > 14.8F && Math.abs(foreShinValue) < (foreShinLimit - 14.8F) * 0.28F;
   }

   private static float getAmount(float[] foreShinValues, float[] foreShinValues2, float foreShinFactor, float foreShinFactor2, float foreShinValue, float foreShinValue2) {
      float amountForFloatArrayFloat = getAmountForFloatArrayFloat(foreShinValues, foreShinValues2, foreShinFactor, foreShinFactor2, foreShinValue, foreShinValue2);
      float foreShinValue4 = foreShinValue / (foreShinFactor * foreShinFactor);
      float foreShinValue5 = amountForFloatArrayFloat / (foreShinFactor2 * foreShinFactor2);
      return (float)Math.toDegrees(Math.atan2(-foreShinValue4, -foreShinValue5));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, ForeShinConst.Wrap wrap) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(7029801))
         .getBonecaAmbalabu(0.08F, 5.0F, 41)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4532759), 0.2F, 8.0F, 42);

      for (int i = 0; i < 320; i++) {
         float bonecaAmbalabuValue = getAmount(i, 1) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(i, 2) * bonecaAmbalabu.bonecaValue;
         float angle = (float)Math.toRadians(80.0F + 40.0F * getAmount(i, 3));
         float bonecaAmbalabuFactor = 2.5F + 3.0F * getAmount(i, 4);
         int bonecaAmbalabuValue3 = (i & 1) == 0 ? ForeShinUtils.getValueForIntFloat(9200195, 0.4F) : ForeShinUtils.getValueForIntFloat(4532759, 0.45F);
         bonecaAmbalabu.getClass02(bonecaAmbalabuValue, amountForFloat, bonecaAmbalabuValue + bonecaAmbalabuFactor * (float)Math.cos(angle), amountForFloat + bonecaAmbalabuFactor * (float)Math.sin(angle), 0.9F, bonecaAmbalabuValue3);
      }

      handleBonecaAmbalabu(
         bonecaAmbalabu,
         wrap,
         ForeShinUtils.getValueForIntFloat(9200195, 0.45F),
         (planValuesItem, bonecaAmbalabuValue5, bonecaAmbalabuValue6) -> bonecaAmbalabuValue6 < 0.0F && getAmount4((Math.abs(planValuesItem) - 1.45F) / 1.7F) + getAmount4((bonecaAmbalabuValue5 - 23.7F) / 1.2F) < 1.0F
      );
      handleBonecaAmbalabu(
         bonecaAmbalabu,
         wrap,
         ForeShinUtils.getValueForIntFloat(9200195, 0.8F),
         (bonecaAmbalabuValue7, bonecaAmbalabuValue8, bonecaAmbalabuValue9) -> bonecaAmbalabuValue9 < 0.0F && getAmount4((Math.abs(bonecaAmbalabuValue7) - 1.5F) / 1.25F) + getAmount4((bonecaAmbalabuValue8 - 23.65F) / 0.85F) < 1.0F
      );
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, (ForeShinConst.Shade) (bonecaAmbalabuValue10, bonecaAmbalabuLimit, bonecaAmbalabuValue11) -> {
         float var3x = 1.0F;
         if (bonecaAmbalabuValue11 < 0.0F && bonecaAmbalabuLimit > 25.0F) {
            var3x -= 0.4F * Math.min(1.0F, (bonecaAmbalabuLimit - 25.0F) / 1.3F);
         }

         if (bonecaAmbalabuValue11 < 0.0F && bonecaAmbalabuLimit > 25.7F) {
            var3x *= 0.75F;
         }

         float var4x = getAmount4((Math.abs(bonecaAmbalabuValue10) - 1.1F) / 0.75F) + getAmount4((bonecaAmbalabuLimit - 25.35F) / 0.55F);
         if (bonecaAmbalabuValue11 < 0.0F && var4x < 1.0F) {
            var3x -= 0.25F * (1.0F - var4x);
         }

         if (bonecaAmbalabuLimit < 23.1F) {
            var3x -= 0.2F * Math.min(1.0F, (23.1F - bonecaAmbalabuLimit) / 0.5F);
         }

         return Math.max(0.3F, var3x);
      });
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, ForeShinConst.Wrap wrap, int bonecaAmbalabuValue, ForeShinConst.Area area) {
      float[] floats = new float[3];
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, bonecaAmbalabuValue, (wrapValue, bonecaAmbalabuValue3) -> {
         wrap.handleAmountsAmount(bonecaAmbalabu, wrapValue, bonecaAmbalabuValue3, floats);
         return area.in(floats[0], floats[1], floats[2]);
      });
   }

   private static void handle(float angle, float angle2, float angle3, float foreShinFactor, float foreShinFactor2, float foreShinFactor3, float[] foreShinValues) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float foreShinFactor4 = foreShinFactor2 * cos - foreShinFactor3 * sin;
      float foreShinFactor5 = foreShinFactor2 * sin + foreShinFactor3 * cos;
      float cos2 = (float)Math.cos(angle2);
      float sin2 = (float)Math.sin(angle2);
      float foreShinFactor6 = foreShinFactor * cos2 + foreShinFactor5 * sin2;
      float foreShinValue = -foreShinFactor * sin2 + foreShinFactor5 * cos2;
      float cos3 = (float)Math.cos(angle3);
      float sin3 = (float)Math.sin(angle3);
      foreShinValues[0] = foreShinFactor6 * cos3 - foreShinFactor4 * sin3;
      foreShinValues[1] = foreShinFactor6 * sin3 + foreShinFactor4 * cos3;
      foreShinValues[2] = foreShinValue;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, ForeShinConst.Wrap wrap, ForeShinConst.Shade shade) {
      float[] floats = new float[3];

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            wrap.handleAmountsAmount(bonecaAmbalabu, j + 0.5F, i + 0.5F, floats);
            float at = shade.at(floats[0], floats[1], floats[2]);
            if (at != 1.0F) {
               bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), at));
            }
         }
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4866363))
         .getBonecaAmbalabu(0.07F, 5.0F, 63)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(2761761), 0.2F, 7.0F, 64);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float abs2 = Math.abs(bonecaAmbalabuValue - 0.61F);
         float bonecaAmbalabuTotal = abs2 < 0.1F ? 0.6F + 0.4F * abs2 / 0.1F : 1.0F;
         if (Math.abs(bonecaAmbalabuValue - 0.5F) < 0.08F) {
            bonecaAmbalabuTotal += 0.08F * (1.0F - Math.abs(bonecaAmbalabuValue - 0.5F) / 0.08F);
         }

         for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, ForeShinUtils.getValue(bonecaAmbalabu.getBonecaAmbalabuForInt(i, j), bonecaAmbalabuTotal));
         }
      }
   }

   private static float[] getEmberCigar(float emberCigarFactor, float emberCigarFactor2) {
      return new float[]{
         cigarEmberAmounts[0] + (emberArmAmounts[0] - cigarEmberAmounts[0]) * emberCigarFactor,
         cigarEmberAmounts[1] + (emberArmAmounts[1] - cigarEmberAmounts[1]) * emberCigarFactor,
         cigarEmberAmounts[2] + (emberArmAmounts[2] - cigarEmberAmounts[2]) * emberCigarFactor,
         cigarEmberAmounts[0] + (emberArmAmounts[0] - cigarEmberAmounts[0]) * emberCigarFactor2,
         cigarEmberAmounts[1] + (emberArmAmounts[1] - cigarEmberAmounts[1]) * emberCigarFactor2,
         cigarEmberAmounts[2] + (emberArmAmounts[2] - cigarEmberAmounts[2]) * emberCigarFactor2
      };
   }

   private static float getAmount(float foreShinValue, float foreShinValue2) {
      float foreShinBound = Float.MAX_VALUE;
      float abs3 = Math.abs(foreShinValue);

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 2; j++) {
            float foreShinFactor = abs3 - (1.55F + j * 0.85F + i * 0.25F);
            float foreShinFactor2 = foreShinValue2 - (15.05F + i * 1.3F);
            foreShinBound = Math.min(foreShinBound, foreShinFactor * foreShinFactor + foreShinFactor2 * foreShinFactor2);
         }
      }

      return (float)Math.sqrt(foreShinBound);
   }

   private static float getAmount(float foreShinValue) {
      return 0.08F + (13.2F - foreShinValue) * 0.2F;
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabuWrap(BonecaAmbalabu bonecaAmbalabu, ForeShinConst.Wrap wrap) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(9200195)).getBonecaAmbalabu(0.06F, 4.0F, 51);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, (ForeShinConst.Shade) (bonecaAmbalabuValue, bonecaAmbalabuValue2, bonecaAmbalabuValue3) -> bonecaAmbalabuValue2 < 23.2F ? 0.85F : 1.0F);

      for (int i = -1; i <= 1; i += 2) {
         for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 3; k++) {
               float bonecaAmbalabuValue4 = i * (0.84F + k * 0.27F + j * 0.1F);
               float bonecaAmbalabuValue5 = 23.48F + j * 0.25F;
               float bonecaAmbalabuFactor = (bonecaAmbalabuValue5 - 23.75F) / 1.15F;
               float sqrt = (float)Math.sqrt(Math.max(0.01F, 1.0F - bonecaAmbalabuFactor * bonecaAmbalabuFactor));
               float max2 = Math.max(-0.95F, Math.min(0.95F, -bonecaAmbalabuValue4 / (1.75F * sqrt)));
               float sqrt2 = (float)Math.sqrt(1.0F - max2 * max2);
               float bonecaAmbalabuFactor3 = (float)Math.asin(max2) / (float) (Math.PI * 2) + 0.5F;
               float bonecaAmbalabuFactor4 = (float)Math.acos(bonecaAmbalabuFactor) / (float) Math.PI;
               float classDivisor = 0.095F * (bonecaAmbalabu.ambalabuBonecaValue - 1) / (((float) Math.PI * 7.0F / 2.0F) * sqrt * sqrt2);
               float bonecaAmbalabuValue7 = 0.095F * (bonecaAmbalabu.bonecaValue - 1) / (3.6128316F * sqrt);
               bonecaAmbalabu.getClass022(bonecaAmbalabuFactor3 * (bonecaAmbalabu.ambalabuBonecaValue - 1) + 0.5F, bonecaAmbalabuFactor4 * (bonecaAmbalabu.bonecaValue - 1) + 0.5F, classDivisor, bonecaAmbalabuValue7, ForeShinUtils.getValueForIntFloat(3810324, 0.8F));
            }
         }
      }
   }

   private static float getAmount(float[] foreShinValues, float foreShinValue, float foreShinValue2) {
      float foreShinBound = Float.MAX_VALUE;
      int foreShinLimit = foreShinValues.length / 2;
      int foreShinCounter = 0;

      for (int i = foreShinLimit - 1; foreShinCounter < foreShinLimit; i = foreShinCounter++) {
         foreShinBound = Math.min(foreShinBound, getAmount(foreShinValue, foreShinValue2, foreShinValues[2 * i], foreShinValues[2 * i + 1], foreShinValues[2 * foreShinCounter], foreShinValues[2 * foreShinCounter + 1]));
      }

      return foreShinBound;
   }

   private static boolean canFlag3(float foreShinValue, float foreShinLimit) {
      float abs2 = Math.abs(foreShinValue);
      if (!(abs2 < 2.2F) && !(abs2 > 3.45F)) {
         float foreShinValue2 = 9.55F + (abs2 - 2.2F) * 0.3F;
         return foreShinLimit > foreShinValue2 && foreShinLimit < foreShinValue2 + 0.7F;
      } else {
         return false;
      }
   }

   private static void handle(ForeShinScale foreShinScale, ForeEar foreEar, float[] foreShinValues, float angle) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float foreShinFactor = 1.0F - cos;
      float x = foreShinFactor * foreShinValues[0] * foreShinValues[0] + cos;
      float y = foreShinFactor * foreShinValues[0] * foreShinValues[1] + sin * foreShinValues[2];
      float foreShinValue = foreShinFactor * foreShinValues[0] * foreShinValues[2] - sin * foreShinValues[1];
      float y2 = foreShinFactor * foreShinValues[1] * foreShinValues[2] + sin * foreShinValues[0];
      float x2 = foreShinFactor * foreShinValues[2] * foreShinValues[2] + cos;
      foreShinScale.getAmounts(foreEar, (float)Math.atan2(y2, x2), (float)Math.asin(getMax(-foreShinValue)), (float)Math.atan2(y, x));
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.body = latheLoft.getClassForStringForeEar("hips", latheLoft.getClassForeEarList(), 0.0F, 8.8F, 0.0F);
      this.armL = latheLoft.getClassForStringForeEar("body", this.body, 0.0F, 8.8F, 0.0F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("skirt", this.armL, 0.0F, 13.0F, 0.0F);
      this.cigar = latheLoft.getClassForStringForeEar("head", this.armL, 0.0F, 22.5F, 0.0F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("eyes", this.cigar, 0.0F, 25.35F, -2.4F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("hat", this.cigar, 0.0F, 26.78F, 0.05F);
      this.ember = latheLoft.getClassForStringForeEar("cigar", this.cigar, cigarEmberAmounts[0], cigarEmberAmounts[1], cigarEmberAmounts[2]);
      this.emberSection = latheLoft.getClassForStringForeEar("ember", this.ember, emberArmAmounts[0], emberArmAmounts[1], emberArmAmounts[2]);
      this.shinFootForeEars = new ForeEar[3];

      for (int i = 0; i < this.shinFootForeEars.length; i++) {
         this.shinFootForeEars[i] = latheLoft.getClassForStringForeEar("smoke" + i, this.cigar, emberArmAmounts[0], emberArmAmounts[1] + 0.3F, emberArmAmounts[2]);
      }

      this.foreR = latheLoft.getClassForStringForeEar("arm_r", this.armL, 3.85F, 20.9F, 0.1F);
      this.foreRightSection = latheLoft.getClassForStringForeEar("fore_r", this.foreR, 3.85F, 15.599999F, 0.1F);
      this.foreL = latheLoft.getClassForStringForeEar("arm_l", this.armL, -3.85F, 20.9F, 0.1F);
      this.foreEar = latheLoft.getClassForStringForeEar("fore_l", this.foreL, -3.85F, 15.599999F, 0.1F);
      this.flash = latheLoft.getClassForStringForeEar("gun", this.armL, 3.45F, 14.1F, -2.0F);
      this.flashSection = latheLoft.getClassForStringForeEar("flash", this.flash, flashLegAmounts[0], flashLegAmounts[1], flashLegAmounts[2]);
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", this.body, 1.6F, 8.8F, 0.0F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 1.6F, 4.6F, -0.05F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("foot_r", this.footR, 1.6F, 1.05F, 0.02F);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", this.body, -1.6F, 8.8F, 0.0F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -1.6F, 4.6F, -0.05F);
      this.foreShinForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -1.6F, 1.05F, 0.02F);
      this.tailSection = latheLoft.getClassForStringForeEar("tail", this.armL, 0.0F, 6.2F, 1.7F);
      latheLoft.getLatheLoft(this.foreR);
      Region region = latheLoft.getMax(256, 192);
      Region max2 = latheLoft.getMax(192, 112);
      Region max3 = latheLoft.getMax(128, 128);
      Region max4 = latheLoft.getMax(48, 48);
      Region max5 = latheLoft.getMax(256, 96);
      Region max6 = latheLoft.getMax(128, 64);
      Region max7 = latheLoft.getMax(32, 32);
      Region max8 = latheLoft.getMax(64, 96);
      Region max9 = latheLoft.getMax(160, 80);
      Region max10 = latheLoft.getMax(64, 64);
      Region max11 = latheLoft.getMax(128, 32);
      Region max12 = latheLoft.getMax(128, 32);
      Region max13 = latheLoft.getMax(64, 32);
      Region max14 = latheLoft.getMax(64, 32);
      Region max15 = latheLoft.getMax(32, 32);
      Region max16 = latheLoft.getMax(16, 32);
      handleForBonecaAmbalabuWrap(region.getClassBonecaAmbalabu(), new ForeShinConst.Wrap(shinFootTable, foreShinTable, 1.0F, 0.7F));
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu(), new ForeShinConst.Wrap(BODY_PROFILE_RADII, BODY_PROFILE_HEIGHTS, 1.08F, 0.86F));
      handleBonecaAmbalabu6(max4.getClassBonecaAmbalabu());
      handle(max5.getClassBonecaAmbalabu(), new ForeShinConst.Wrap(table, ARM_PROFILE_HEIGHTS, 1.0F, 0.74F));
      handleBonecaAmbalabuForBonecaAmbalabu(max6.getClassBonecaAmbalabu());
      handleBonecaAmbalabu(max14.getClassBonecaAmbalabu());
      handleAmountsBoneca(max3.getClassBonecaAmbalabu());
      handleBonecaAmbalabu5(max15.getClassBonecaAmbalabu());
      handleBonecaAmbalabu3(max16.getClassBonecaAmbalabu());
      max7.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(2763823))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(1184532), 4.0F, 2.0F, 90.0F)
         .getBonecaAmbalabu(0.05F, 4.0F, 81);
      max8.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(1776414))
         .getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(13684944, 0.42F), 8.0F, 0.9F, 0.0F)
         .getBonecaAmbalabu(0.06F, 6.0F, 82);
      handleBonecaAmbalabuForBonecaAmbalabuWrap(max9.getClassBonecaAmbalabu(), new ForeShinConst.Wrap(23.75F, 1.75F, 1.15F, 1.45F));
      max10.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11036986))
         .getBonecaAmbalabu(0.05F, 6.0F, 83)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8342825), 0.18F, 6.0F, 84);
      BonecaAmbalabu classBonecaAmbalabu = max11.getClassBonecaAmbalabu();
      classBonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4866363))
         .getBonecaAmbalabu(0.06F, 6.0F, 85)
         .getClassBonecaAmbalabu(0.0F, classBonecaAmbalabu.bonecaValue * 0.42F, classBonecaAmbalabu.ambalabuBonecaValue, classBonecaAmbalabu.bonecaValue * 0.58F, ForeShinUtils.getValueForIntFloat(2038296, 0.75F));
      classBonecaAmbalabu.getClassBonecaAmbalabu(0.0F, classBonecaAmbalabu.bonecaValue * 0.58F, classBonecaAmbalabu.ambalabuBonecaValue, classBonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(2761761, 0.35F));
      BonecaAmbalabu bonecaAmbalabu = max12.getClassBonecaAmbalabu();
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11036986))
         .getBonecaAmbalabu(0.05F, 6.0F, 86)
         .getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, 3.0F, ForeShinUtils.getValueForInt(6173975))
         .getClassBonecaAmbalabu(0.0F, 3.0F, bonecaAmbalabu.ambalabuBonecaValue, 4.5F, ForeShinUtils.getValueForIntFloat(12023620, 0.7F));
      handleBonecaAmbalabu4(bonecaAmbalabu);
      max13.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8276003))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4859920), 8.0F, 0.4F, 0.35F, 87)
         .getBonecaAmbalabu(0.06F, 4.0F, 88);
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max9);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max6);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max14);
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max11);
      ShinBrr classForRegion11 = latheLoft.getClassForRegion(max12);
      ShinBrr classForRegion12 = latheLoft.getClassForRegion(max8);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max10);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion15 = latheLoft.getClassForRegion(max13);
      ShinBrr classForRegion16 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion17 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion18 = latheLoft.getClassForRegion(max15);
      ShinBrr classForRegion19 = latheLoft.getClassForRegion(max16);
      ShinBrr shinBrr = latheLoft.getMax(11036986);
      ShinBrr shinBrr2 = latheLoft.getMax(8342825);
      ShinBrr shinBrr3 = latheLoft.getMax(9393712);
      ShinBrr shinBrr4 = latheLoft.getMax(12755290);
      ShinBrr shinBrr5 = latheLoft.getMax(3810324);
      ShinBrr shinBrr6 = latheLoft.getMax(4860951);
      ShinBrr shinBrr7 = latheLoft.getMax(7227950);
      ShinBrr shinBrr8 = latheLoft.getMax(2759956);
      ShinBrr shinBrr9 = latheLoft.getMax(1315860);
      ShinBrr shinBrr10 = latheLoft.getMax(2759956);
      ShinBrr shinBrr11 = latheLoft.getMax(5912608);
      ShinBrr shinBrr12 = latheLoft.getMax(9793616);
      ShinBrr shinBrr13 = latheLoft.getMax(4532759);
      ShinBrr shinBrr14 = latheLoft.getMax(920072);
      ShinBrr shinBrr15 = latheLoft.getMax(16777215);
      ShinBrr shinBrr16 = latheLoft.getMax(1840402);
      ShinBrr max17 = latheLoft.getMax(9200195);
      ShinBrr max18 = latheLoft.getMax(14270334);
      ShinBrr max19 = latheLoft.getMax(9073736);
      ShinBrr max20 = latheLoft.getMax(2757648);
      ShinBrr max21 = latheLoft.getMax(1971730);
      ShinBrr max22 = latheLoft.getMax(5978657);
      ShinBrr max23 = latheLoft.getMax(12071466);
      ShinBrr max24 = latheLoft.getMax(12170412);
      ShinBrr max25 = latheLoft.getMax(16738847);
      ShinBrr max26 = latheLoft.getMax(13618116);
      ShinBrr max27 = latheLoft.getMax(2763823);
      ShinBrr max28 = latheLoft.getMax(2303014);
      ShinBrr max29 = latheLoft.getMax(1971988);
      ShinBrr max30 = latheLoft.getMax(16770140);
      ShinBrr max31 = latheLoft.getMax(16747039);
      latheLoft.getLatheLoftForForeEar(this.body);
      latheLoft.getLatheProfile(new float[]{2.7F, 3.0F, 3.12F}, new float[]{7.6F, 9.4F, 12.4F}, 8, 1.0F, 0.76F, classForRegion12, null, null);
      latheLoft.getLatheLoftForForeEar(this.armL);
      latheLoft.getLatheProfile(shinFootTable, foreShinTable, 16, 1.0F, 0.7F, classForRegion, null, null);
      latheLoft.getLatheProfile(new float[]{3.73F, 3.73F}, new float[]{12.9F, 14.0F}, 16, 1.0F, 0.75F, shinBrr3, null, null);
      float shinFootValue = -2.7975001F;
      latheLoft.getLatheLoft(-0.55F, 12.88F, shinFootValue - 0.2F, 0.55F, 14.02F, shinFootValue + 0.12F, shinBrr4);
      latheLoft.getLatheLoft(-0.32F, 13.14F, shinFootValue - 0.22F, 0.32F, 13.76F, shinFootValue - 0.19F, shinBrr3);
      latheLoft.getLatheLoft(-0.04F, 13.3F, shinFootValue - 0.25F, 0.28F, 13.6F, shinFootValue - 0.21F, shinBrr4);

      for (int j = -1; j <= 1; j += 2) {
         float shinFootFactor = j * 1.5F;
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinFootFactor, 13.45F, 0.75F * (float)Math.sqrt(13.9129F - shinFootFactor * shinFootFactor) + 0.03F).getLatheLoft4(180.0F + j * 18.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.22F, 0.22F, 6, shinBrr5);
         latheLoft.getDequeArray();
         latheLoft.getLatheLoft(j * 3.55F, 21.35F, 0.1F, 1.3F, 0.5F, 1.2F, 8, classForRegion13);
         latheLoft.getLoftPath(
            new float[]{j * 2.1F, 22.28F, 0.05F, j * 2.95F, 22.12F, 0.05F, j * 3.7F, 22.18F, 0.05F},
            new float[]{0.62F, 0.6F, 0.58F},
            new float[]{0.1F, 0.1F, 0.11F},
            8,
            shinBrr,
            null,
            shinBrr,
            0.0F,
            1.0F,
            0.0F
         );
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(j * 3.45F, 22.3F, 0.05F).getLatheLoftLatheLoftAmountLoft(90.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.17F, 0.17F, 6, shinBrr5);
         latheLoft.getDequeArray();
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 22.2F, 0.35F).getLatheLoftLatheLoftAmountLoft(-10.0F);
      latheLoft.getLatheProfile(new float[]{2.4F, 2.75F, 3.2F}, new float[]{-0.6F, 0.2F, 0.55F}, 12, 1.0F, 0.8F, classForRegion11, null, null);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      latheLoft.getLatheProfile(table, ARM_PROFILE_HEIGHTS, 16, 1.0F, 0.74F, classForRegion5, null, null);

      for (int k = -1; k <= 1; k += 2) {
         float shinFootValue2 = k * 1.6F;
         latheLoft.getLatheLoftForForeEar(k > 0 ? this.shinR : this.shinL);
         latheLoft.getLatheLoft(new float[]{shinFootValue2, 9.400001F, 0.0F, shinFootValue2, 4.6F, -0.05F}, new float[]{1.3F, 1.2F}, 12, classForRegion12, false);
         latheLoft.getLatheLoftForForeEar(k > 0 ? this.footR : this.footL);
         latheLoft.getLatheLoft(shinFootValue2, 4.6F, -0.05F, 1.2F, 8, classForRegion12);
         latheLoft.getLatheLoft(new float[]{shinFootValue2, 4.6F, -0.05F, shinFootValue2, 1.7F, 0.0F, shinFootValue2, 1.05F, 0.02F}, new float[]{1.2F, 1.18F, 1.25F}, 12, classForRegion12, false);
         latheLoft.getLatheLoftForForeEar(k > 0 ? this.classShinFoot : this.foreShinForeEar);
         float latheLoftValue = shinFootValue2 + k * 0.05F;
         latheLoft.getLatheLoft(latheLoftValue, 0.66F, -0.72F, 0.98F, 0.64F, 1.75F, 8, shinBrr9);
         latheLoft.getLoftLathe(latheLoftValue, 0.2F, -0.72F, 1.04F, 0.2F, 1.83F, 8, 0.5F, 1.0F, shinBrr10);
      }

      for (int m = -1; m <= 1; m += 2) {
         float shinFootValue4 = m * 3.85F;
         float shinFootValue5 = 15.599999F;
         float eyeValue = shinFootValue5 - 5.3F;
         latheLoft.getLatheLoftForForeEar(m > 0 ? this.foreR : this.foreL);
         latheLoft.getLatheLoft(shinFootValue4, 20.9F, 0.1F, 1.17F, 8, classForRegion13);
         latheLoft.getLatheLoft(new float[]{shinFootValue4, 20.9F, 0.1F, shinFootValue4, shinFootValue5, 0.1F}, new float[]{1.15F, 1.0F}, 12, classForRegion13, false);
         latheLoft.getLatheLoftForForeEar(m > 0 ? this.foreRightSection : this.foreEar);
         latheLoft.getLatheLoft(shinFootValue4, shinFootValue5, 0.1F, 1.0F, 8, classForRegion13);
         latheLoft.getLatheLoft(
            new float[]{shinFootValue4, shinFootValue5, 0.1F, shinFootValue4, eyeValue + 1.0F, 0.1F, shinFootValue4, eyeValue + 0.72F, 0.1F}, new float[]{0.98F, 1.08F, 1.12F}, 12, classForRegion13, false
         );
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinFootValue4, eyeValue + 0.72F, 0.1F).getLatheLoftLatheLoftAmountLoft(-90.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 1.1F, 1.1F, 12, shinBrr6);
         latheLoft.getDequeArray();
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinFootValue4, 0.0F, 0.1F);
         latheLoft.getLatheLoft(1.09F, 1.09F, eyeValue + 1.3F, eyeValue + 1.62F, 12, shinBrr2, null);
         latheLoft.getDequeArray();
         latheLoft.getLatheLoft(shinFootValue4, eyeValue, 0.1F, 0.62F, 0.75F, 0.66F, 8, shinBrr7);
         latheLoft.getLatheLoft(new float[]{shinFootValue4 - 0.42F, eyeValue - 0.38F, -0.3F, shinFootValue4 + 0.42F, eyeValue - 0.38F, -0.3F}, new float[]{0.32F, 0.32F}, 6, shinBrr7, true);

         for (int p = 0; p < 4; p++) {
            float shinFootValue7 = shinFootValue4 - 0.42F + p * 0.28F;
            latheLoft.getLatheLoft(shinFootValue7 - 0.09F, eyeValue - 0.55F, -0.53F, shinFootValue7 + 0.09F, eyeValue - 0.55F, -0.53F, shinFootValue7, eyeValue - 0.88F, -0.32F, shinBrr8);
         }
      }

      latheLoft.getLatheLoftForForeEar(this.flash);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(3.45F, 14.1F, -2.0F).getLatheLoft4(45.0F).getLatheLoftLatheLoftAmountLoft(12.0F);
      latheLoft.getLatheLoft(-0.36F, 0.55F, -3.1F, 0.36F, 1.5F, 1.0F, max27);
      float[] floats = new float[]{0.0F, 1.08F, 0.85F, 0.0F, 0.86F, 2.55F, 0.0F, 0.47F, 3.92F, 0.0F, 0.39F, 4.22F};
      latheLoft.getLoftPath(floats, new float[]{0.3F, 0.34F, 0.38F, 0.38F}, new float[]{0.38F, 0.6F, 0.82F, 0.82F}, 6, classForRegion15, null, max29, 0.0F, 1.0F, 0.0F);
      latheLoft.getLoftPath(
         new float[]{0.0F, 0.65F, -0.15F, 0.0F, -0.2F, 0.08F, 0.0F, -0.95F, 0.32F},
         new float[]{0.26F, 0.27F, 0.28F},
         new float[]{0.34F, 0.37F, 0.36F},
         6,
         classForRegion15,
         null,
         classForRegion15,
         0.0F,
         0.0F,
         -1.0F
      );
      latheLoft.getLatheLoft(-0.05F, 0.0F, -1.1F, 0.05F, 0.6F, -0.36F, max27);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 1.05F, -3.05F).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLatheLoft(new float[]{0.42F, 0.42F}, new float[]{0.0F, 2.6F}, 6, classForRegion16, null, max27);
      latheLoft.getLatheLoft(new float[]{0.2F, 0.2F}, new float[]{2.6F, 5.75F}, 6, max27, null, null);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoft(-0.27F, 0.8F, -9.75F, 0.27F, 1.36F, -8.8F, max27);
      latheLoft.getLatheLoft(-0.07F, 1.36F, -9.6F, 0.07F, 1.6F, -9.35F, max27);
      latheLoft.getLoftPath(
         new float[]{0.0F, 0.75F, -4.95F, 0.0F, 0.0F, -5.0F, 0.0F, -0.95F, -5.08F},
         new float[]{0.27F, 0.25F, 0.28F},
         new float[]{0.36F, 0.3F, 0.34F},
         6,
         classForRegion15,
         null,
         classForRegion15,
         0.0F,
         0.0F,
         -1.0F
      );
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(-0.58F, -0.62F, -2.55F).getLatheLoft(-90.0F);
      latheLoft.getLatheLoft(1.45F, 1.45F, 0.0F, 1.16F, 16, max28, classForRegion17);
      latheLoft.getDequeArray();
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.flashSection);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(3.45F, 14.1F, -2.0F).getLatheLoft4(45.0F).getLatheLoftLatheLoftAmountLoft(12.0F).getLatheLoft(0.0F, 1.08F, -9.8F);
      latheLoft.getPolygonExtrude(getAmounts(6, 1.7F, 0.62F), -0.22F, -0.22F, max31, null, null);
      latheLoft.getPolygonExtrude(getAmounts(6, 1.0F, 0.38F), -0.36F, -0.36F, max30, null, null);
      float[] polygonExtrudeValues = new float[]{0.0F, 0.45F, 1.2F, 0.9F, 2.6F, 0.0F, 1.2F, -0.9F, 0.0F, -0.45F};
      float[] floats3 = new float[]{0.0F, 0.3F, 0.8F, 0.5F, 1.8F, 0.0F, 0.8F, -0.5F, 0.0F, -0.3F};

      for (int q = 0; q < 2; q++) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(45.0F + q * 90.0F).getLatheLoft4(90.0F);
         latheLoft.getPolygonExtrude(polygonExtrudeValues, 0.0F, 0.0F, max31, null, null);
         latheLoft.getPolygonExtrude(floats3, -0.05F, -0.05F, max30, null, null);
         latheLoft.getPolygonExtrude(floats3, 0.05F, 0.05F, max30, null, null);
         latheLoft.getDequeArray();
      }

      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.cigar);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, -0.1F);
      latheLoft.getLatheProfile(BODY_PROFILE_RADII, BODY_PROFILE_HEIGHTS, 16, 1.08F, 0.86F, classForRegion6, null, null);
      latheLoft.getDequeArray();

      for (int r = -1; r <= 1; r += 2) {
         latheLoft.getLatheLoft(r * 1.7F, 23.6F, -1.8F, 1.15F, 0.95F, 1.05F, 8, classForRegion18);
      }

      latheLoft.getLatheLoft(0.0F, 23.75F, -2.7F, 1.75F, 1.15F, 1.45F, 10, classForRegion7);
      latheLoft.getLatheLoft(-0.6F, 23.32F, -3.35F, 0.75F, 0.58F, 0.6F, 8, shinBrr12);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.6F, 23.58F, -3.35F).getLatheLoft(12.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.75F, 0.58F, 0.6F, 8, shinBrr12);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoft(0.0F, 22.25F, -2.5F, 1.1F, 0.6F, 0.78F, 8, max17);
      latheLoft.getLatheLoft(0.0F, 22.78F, -3.35F, 1.0F, 0.2F, 0.3F, 6, max20);
      latheLoft.getLatheLoft(0.0F, 24.3F, -3.8F, 0.85F, 0.52F, 0.55F, 8, shinBrr16);
      latheLoft.getLatheLoft(new float[]{0.75F, 22.95F, -3.55F, 1.3F, 23.22F, -3.42F}, new float[]{0.06F, 0.05F}, 4, max20, false);

      for (int t = -1; t <= 1; t += 2) {
         float shinFootValue8 = t * 0.37F;
         latheLoft.getLoftPath(
            new float[]{shinFootValue8, 23.62F, -3.63F, shinFootValue8, 22.0F, -3.98F},
            new float[]{0.37F, 0.34F},
            new float[]{0.22F, 0.21F},
            8,
            classForRegion19,
            null,
            max18,
            0.0F,
            0.0F,
            -1.0F
         );
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 22.75F, -3.83F).getLatheLoftLatheLoftAmountLoft(12.0F);
      latheLoft.getLatheLoft(-0.03F, -0.62F, -0.1F, 0.03F, 0.5F, 0.1F, max19);
      latheLoft.getDequeArray();

      for (int shinFootTotal = -1; shinFootTotal <= 1; shinFootTotal += 2) {
         float[] floats4 = new float[]{shinFootTotal * 0.4F, shinFootTotal * 1.15F, shinFootTotal * 1.95F};
         float[] floats5 = new float[]{shinFootTotal < 0 ? 25.25F : 25.4F, 25.68F, shinFootTotal > 0 ? 26.07F : 25.95F};
         float[] floats6 = new float[9];

         for (int index = 0; index < 3; index++) {
            floats6[3 * index] = floats4[index];
            floats6[3 * index + 1] = floats5[index];
            floats6[3 * index + 2] = -0.1F + getAmountForFloatArrayFloat(BODY_PROFILE_RADII, BODY_PROFILE_HEIGHTS, 1.08F, 0.86F, floats4[index], floats5[index]) - 0.28F;
         }

         latheLoft.getLoftPath(floats6, new float[]{0.13F, 0.22F, 0.1F}, new float[]{0.36F, 0.55F, 0.32F}, 6, shinBrr13, null, null, 0.0F, 0.0F, -1.0F);
         latheLoft.getLatheLoft(shinFootTotal * 2.45F, 26.3F, 0.35F, 0.3F, 0.5F, 0.48F, 6, shinBrr11);

         for (int shinFootCounter = 0; shinFootCounter < 3; shinFootCounter++) {
            float shinFootValue9 = 23.55F + shinFootCounter * 0.2F;
            latheLoft.getLatheLoft(shinFootTotal * 1.3F, shinFootValue9 + 0.06F, -3.55F, shinFootTotal * 1.3F, shinFootValue9 - 0.06F, -3.55F, shinFootTotal * 3.75F, 23.35F + shinFootCounter * 0.5F, -2.6F, max21);
         }
      }

      latheLoft.getLatheLoftForForeEar(this.classShinProgress);

      for (int shinFootTotal2 = -1; shinFootTotal2 <= 1; shinFootTotal2 += 2) {
         float shinFootValue10 = shinFootTotal2 * 1.1F;
         latheLoft.getDequeArrayArrayDeque()
            .getLatheLoft(shinFootValue10, 25.35F, -0.1F + getAmountForFloatArrayFloat(BODY_PROFILE_RADII, BODY_PROFILE_HEIGHTS, 1.08F, 0.86F, shinFootValue10, 25.35F) - 0.1F)
            .getLatheLoft4(getAmount(BODY_PROFILE_RADII, BODY_PROFILE_HEIGHTS, 1.08F, 0.86F, shinFootValue10, 25.35F));
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.42F, 0.4F, 0.3F, 8, shinBrr14);
         latheLoft.getLoftLathe(0.1F * shinFootTotal2, -0.04F, -0.302F, 0.075F, 0.075F, 6, shinBrr15);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinFootTotal2 * 16.0F);
         latheLoft.getLoftLathe(0.0F, 0.1F, -0.02F, 0.5F, 0.45F, 0.38F, 8, 0.0F, 0.5F, shinBrr13);
         latheLoft.getDequeArray();
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 26.78F, 0.05F).getLatheLoftLatheLoftAmountLoft(-17.0F).getLatheLoft(-7.0F);
      latheLoft.getLatheProfile(CAP_PROFILE_RADII, CAP_PROFILE_HEIGHTS, 16, 1.0F, 1.1F, classForRegion8, null, classForRegion9);
      float foreShinValue = 1.78F;
      float shinFootFactor2 = getAmount(CAP_PROFILE_RADII, CAP_PROFILE_HEIGHTS, foreShinValue);

      for (int shinFootTotal3 = -1; shinFootTotal3 <= 1; shinFootTotal3 += 2) {
         float[] floats7 = new float[]{-2.32F, -2.0F, -1.1F, 0.2F, 1.3F, 2.05F, 2.35F};
         float[] floats8 = new float[]{0.25F, 0.55F, 0.9F, 1.0F, 0.8F, 0.5F, 0.25F};
         float[] floats9 = new float[]{1.98F, 2.26F, 2.48F, 2.58F, 2.52F, 2.28F, 1.98F};
         int shinFootSize = floats7.length;
         float[] loftPathValues = new float[3 * shinFootSize];
         float[] crownForeValues = new float[shinFootSize];
         float[] floats12 = new float[shinFootSize];

         for (int index2 = 0; index2 < shinFootSize; index2++) {
            int index3 = shinFootTotal3 > 0 ? index2 : shinFootSize - 1 - index2;
            float shinFootValue12 = shinFootFactor2 * (float)Math.sqrt(Math.max(0.0F, 1.0F - getAmount4(floats7[index3] / (shinFootFactor2 * 1.1F))));
            float shinFootValue13 = CAP_PROFILE_RADII[3] * (float)Math.sqrt(Math.max(0.0F, 1.0F - getAmount4(floats7[index3] / (CAP_PROFILE_RADII[3] * 1.1F))));
            floats12[index2] = floats9[index3] - foreShinValue;
            float shinFootLimit = 1.0F - getAmount4((CAP_PROFILE_HEIGHTS[3] - foreShinValue) / floats12[index2]);
            float shinFootBound = shinFootLimit > 0.05F ? (shinFootValue13 - 0.03F - floats8[index3]) / (float)Math.sqrt(shinFootLimit) : shinFootValue12 - floats8[index3] - 0.1F;
            loftPathValues[3 * index2] = shinFootTotal3 * floats8[index3];
            loftPathValues[3 * index2 + 1] = foreShinValue;
            loftPathValues[3 * index2 + 2] = floats7[index3];
            crownForeValues[index2] = Math.max(0.2F, Math.min(shinFootValue12 - floats8[index3] - 0.04F, shinFootBound));
         }

         latheLoft.getLoftPath(loftPathValues, crownForeValues, floats12, 8, classForRegion9, null, null, 0.0F, 1.0F, 0.0F);
      }

      latheLoft.getLatheProfile(new float[]{2.2F, 3.95F, 3.9F, 2.2F}, new float[]{-0.04F, 0.12F, 0.45F, 0.18F}, 16, 1.0F, 1.12F, classForRegion10, null, null);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.ember);
      latheLoft.getLoftPath(getEmberCigar(0.0F, 0.86F), new float[]{0.3F, 0.32F}, new float[]{0.3F, 0.32F}, 8, max22, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoft(getEmberCigar(0.24F, 0.33F), new float[]{0.345F, 0.345F}, 8, max23, false);
      latheLoft.getLatheLoftForForeEar(this.emberSection);
      latheLoft.getLoftPath(getEmberCigar(0.85F, 0.96F), new float[]{0.32F, 0.32F}, new float[]{0.32F, 0.32F}, 8, max24, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getLoftPath(getEmberCigar(0.955F, 1.05F), new float[]{0.31F, 0.12F}, new float[]{0.31F, 0.12F}, 8, max25, null, max25, 0.0F, 0.0F, -1.0F);
      ShinBrr max32 = latheLoft.getMax(15000028);

      for (int index4 = 0; index4 < this.shinFootForeEars.length; index4++) {
         ForeEar foreEar = this.shinFootForeEars[index4];
         latheLoft.getLatheLoftForForeEar(foreEar);
         if (index4 == 0) {
            latheLoft.getDequeArrayArrayDeque().getLatheLoft(foreEar.offsetX, foreEar.foreEarAmount, foreEar.shinFootAmount).getLatheLoftLatheLoftAmountLoft(20.0F);
            latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.75F, 0.24F, 8, 4, max26);
            latheLoft.getDequeArray();
         } else {
            latheLoft.getLatheLoft(foreEar.offsetX, foreEar.foreEarAmount, foreEar.shinFootAmount, 0.62F, 0.55F, 0.62F, 7, max26);
            latheLoft.getLatheLoft(foreEar.offsetX + 0.5F, foreEar.foreEarAmount + 0.3F, foreEar.shinFootAmount + 0.1F, 0.72F, 0.62F, 0.7F, 7, max32);
            latheLoft.getLatheLoft(foreEar.offsetX - 0.35F, foreEar.foreEarAmount + 0.48F, foreEar.shinFootAmount - 0.1F, 0.5F, 0.45F, 0.5F, 7, index4 == 1 ? max26 : max32);
         }
      }

      latheLoft.getLatheLoftForForeEar(this.tailSection);
      latheLoft.getLoftPath(pointsSpline, bonecaTable, TAIL_RADII, 8, classForRegion14, null, classForRegion14, 0.0F, 1.0F, 0.0F);
   }

   private static float getAmountForFloatArrayFloat(float[] foreShinValues, float[] foreShinValues2, float foreShinDivisor, float foreShinValue, float foreShinValue2, float foreShinValue3) {
      float foreShinFactor = getAmount(foreShinValues, foreShinValues2, foreShinValue3);
      float foreShinFactor2 = foreShinValue2 / foreShinDivisor;
      return -foreShinValue * (float)Math.sqrt(Math.max(0.0F, foreShinFactor * foreShinFactor - foreShinFactor2 * foreShinFactor2));
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4866363))
         .getBonecaAmbalabu(0.07F, 5.0F, 61)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(2761761), 0.2F, 7.0F, 62);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = i < bonecaAmbalabu.bonecaValue * 0.22F ? 0.82F + 0.18F * i / (bonecaAmbalabu.bonecaValue * 0.22F) : 1.0F;

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), bonecaAmbalabuValue));
         }
      }

      for (int k = -1; k <= 1; k += 2) {
         float bonecaAmbalabuValue2 = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + k * 0.08F);

         for (int m = 0; m < 4; m++) {
            float bonecaAmbalabuFactor = 1.0F - m * 0.22F;
            bonecaAmbalabu.getClass022(
               bonecaAmbalabuValue2 - k * bonecaAmbalabu.ambalabuBonecaValue * 0.004F * m,
               bonecaAmbalabu.bonecaValue * (0.375F - 0.005F * m),
               bonecaAmbalabu.ambalabuBonecaValue * 0.03F * bonecaAmbalabuFactor,
               bonecaAmbalabu.bonecaValue * 0.18F * bonecaAmbalabuFactor,
               ForeShinUtils.getValueForIntFloat(1709331, 0.09F)
            );
         }

         bonecaAmbalabu.getClass022(
            bonecaAmbalabuValue2 + k * bonecaAmbalabu.ambalabuBonecaValue * 0.03F,
            bonecaAmbalabu.bonecaValue * 0.37F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.008F,
            bonecaAmbalabu.bonecaValue * 0.15F,
            ForeShinUtils.getValueForIntFloat(6970966, 0.22F)
         );
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.665F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.894F, ForeShinUtils.getValueForInt(1381138));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.665F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.685F, ForeShinUtils.getValueForIntFloat(3814192, 0.8F));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.66F, bonecaAmbalabu.bonecaValue * 0.68F, bonecaAmbalabu.ambalabuBonecaValue * 0.7F, bonecaAmbalabu.bonecaValue * 0.88F, ForeShinUtils.getValueForIntFloat(2762018, 0.9F));
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float angle = poseProgress.animationProgress * 0.6662F;
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float animationProgress = poseProgress.getAnimationProgressForFloat(0.0F);
      float animationProgressForFloat = poseProgress.getAnimationProgressForFloat((float) Math.PI);
      float shinProgressFactor2 = 0.55F;
      float shinProgressValue = (float)Math.sin(angle) * shinProgressFactor;
      float shinProgressValue2 = Math.abs((float)Math.sin(angle)) * shinProgressFactor;
      float ageCode = poseProgress.getAgeCode(70.0F, 0.0F);
      float shinProgressFactor3 = poseProgress.swingProgress > 0.0F ? 0.0F : Math.max(0.0F, 1.0F - shinProgressFactor / 0.3F);
      float hashRegistryValue = angle / (float) Math.PI;
      float shinProgressValue4 = hashRegistryValue - (float)Math.floor(hashRegistryValue);
      float shinProgressValue5 = (shinProgressValue4 < 0.8F ? getAmount2(shinProgressValue4 / 0.8F) : 1.0F - getAmount2((shinProgressValue4 - 0.8F) / 0.2F)) * shinProgressFactor;
      float amount2 = getAmount2(animationProgress, poseProgress.getAnimationProgress((float) Math.PI));
      float shinProgressValue7 = getAmount2(animationProgressForFloat, poseProgress.getAnimationProgress(0.0F));
      float shinProgressValue8 = poseProgress.sneakingPose ? 0.5F : 0.0F;
      float shinProgressTotal = 0.0F;
      float dinForeValue = -0.12F * animationProgress;
      float shinProgressTotal3 = 0.18F * shinProgressValue;
      float shinProgressTotal4 = -0.03F * ageCode * shinProgressFactor3;
      float shinProgressTotal5 = 0.0F;
      float shinProgressTotal6 = 0.0F;
      float shinProgressTotal7 = 0.16F * shinProgressValue2;
      float shinProgressTotal8 = 0.04F * (float)Math.sin(2.0F * angle) * shinProgressFactor;
      float shinProgressTotal9 = 0.05F * animationProgress;
      float shinProgressValue9 = 0.0F;
      float shinProgressTotal10 = 0.0F;
      float shinProgressTotal11 = 0.12F * ageCode * shinProgressFactor3;
      float shinProgressTotal12 = 0.0F;
      float shinProgressLimit = 0.0F;
      float shinProgressFactor4 = 0.0F;
      float shinProgressTotal13 = 0.0F;
      if (poseProgress.swingProgress > 0.0F) {
         float sin = (float)Math.sin(poseProgress.swingProgress * Math.PI * 12.0);
         float shinProgressFactor5 = getAmount2(poseProgress.swingProgress / 0.08F) * (1.0F - getAmount2((poseProgress.swingProgress - 0.75F) / 0.25F));
         shinProgressFactor4 = Math.abs(sin) * shinProgressFactor5;
         shinProgressLimit = Math.max(0.0F, sin) * shinProgressFactor5;
         shinProgressTotal8 += 0.08F * shinProgressFactor4 + 0.1F * shinProgressFactor5;
         shinProgressTotal9 += 0.03F * sin * shinProgressFactor5 + 0.14F * (float)Math.sin(poseProgress.swingProgress * Math.PI * 2.0) * shinProgressFactor5 - 0.35F * shinProgressFactor5;
         shinProgressTotal10 -= 0.38F * SWING_AXIS[0] * shinProgressFactor4;
         shinProgressTotal11 -= 0.38F * SWING_AXIS[1] * shinProgressFactor4;
         shinProgressTotal12 -= 0.38F * SWING_AXIS[2] * shinProgressFactor4;
         shinProgressTotal3 += 0.02F * sin * shinProgressFactor5;
         shinProgressTotal += 0.08F * shinProgressFactor4;
         dinForeValue += 0.03F * sin * shinProgressFactor5;
         shinProgressTotal13 += 0.06F * sin * shinProgressFactor5;
         shinProgressTotal6 -= 0.09F * shinProgressFactor4;
         shinProgressTotal4 += 0.04F * shinProgressFactor4;
         shinProgressTotal5 += 0.03F * sin * shinProgressFactor5;
         foreShinScale.getAmountsForeShin2(this.shinFootForeEar, 0.0F, 0.5F * shinProgressFactor4, 0.0F);
      }

      if (poseProgress.sneakingPose) {
         shinProgressTotal -= 0.45F;
         shinProgressTotal7 += 0.2F;
         shinProgressTotal4 += 0.4F;
         shinProgressTotal6 += 0.58F;
         shinProgressTotal11 += 2.5F;
         shinProgressTotal8 += 0.75F;
      }

      float shinProgressValue10 = animationProgress * shinProgressFactor2 + shinProgressValue8;
      float shinProgressValue11 = animationProgressForFloat * shinProgressFactor2 + shinProgressValue8;
      float shinProgressValue12 = -amount2 - 2.0F * shinProgressValue8;
      float shinProgressValue13 = -shinProgressValue7 - 2.0F * shinProgressValue8;
      float shinProgressValue14 = shinProgressValue8 - 0.7F * (animationProgress * shinProgressFactor2 - amount2);
      float foreShinValue = shinProgressValue8 - 0.7F * (animationProgressForFloat * shinProgressFactor2 - shinProgressValue7);
      float max16 = Math.max(getAmountForFloat(shinProgressValue10, shinProgressValue12, shinProgressValue14), getAmountForFloat(shinProgressValue11, shinProgressValue13, foreShinValue));
      boolean shinProgressFlag = !poseProgress.vehicle && !poseProgress.gliding && !poseProgress.swimming;
      float shinProgressValue17 = shinProgressFlag ? 8.8F - max16 : 0.0F;
      float shinProgressDivisor = 1.0F + 0.006F * ageCode * shinProgressFactor3;
      foreShinScale.getAmountsForeShin2(this.body, 0.0F, -shinProgressValue17, 0.0F);
      foreShinScale.getAmounts(this.armL, shinProgressTotal, dinForeValue, shinProgressTotal3);
      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.06F, 0.94F * shinProgressDivisor, 1.06F);
      } else {
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.0F, shinProgressDivisor, 1.0F);
      }

      foreShinScale.getAmounts(this.shinR, shinProgressValue10, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinL, shinProgressValue11, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footR, shinProgressValue12, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footL, shinProgressValue13, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classShinFoot, shinProgressValue14, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.foreShinForeEar, foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, shinProgressTotal7 + 0.12F * Math.abs(animationProgress) * shinProgressFactor, -0.13F * animationProgress, -0.15F * (float)Math.sin(angle - 1.0F) * shinProgressFactor + shinProgressTotal13);
      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.shinR, 1.35F, 0.12F, 0.0F);
         foreShinScale.getAmounts(this.shinL, 1.35F, -0.12F, 0.0F);
         foreShinScale.getAmounts(this.footR, -0.2F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.2F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.classShinFoot, 0.0F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.foreShinForeEar, 0.0F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, 0.55F, 0.0F, 0.0F);
         shinProgressTotal6 -= 0.4F;
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         foreShinScale.getAmounts(this.shinR, -0.15F + 0.2F * poseProgress.getAgeCode(12.0F, 0.0F), 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinL, -0.15F + 0.2F * poseProgress.getAgeCode(12.0F, (float) Math.PI), 0.0F, 0.0F);
         foreShinScale.getAmounts(this.classShinFoot, 0.3F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.foreShinForeEar, 0.3F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, -0.2F, 0.0F, 0.0F);
         shinProgressTotal6 -= 0.5F;
      }

      float shinProgressValue19 = (poseProgress.age + (poseProgress.hashCode & 127) * 0.9F) % 120.0F;
      float shinProgressValue20 = shinProgressValue19 < 6.0F ? getAmount2(shinProgressValue19 / 6.0F) : (shinProgressValue19 < 8.0F ? 1.0F - (shinProgressValue19 - 6.0F) / 2.0F : 0.0F);
      float shinProgressValue21 = 0.15F * (shinProgressValue17 + 0.6F * shinProgressValue2) + 0.3F * shinProgressValue20 * shinProgressFactor3 + 0.22F * shinProgressValue5;
      foreShinScale.getAmounts(this.tailSection, shinProgressTotal6 - shinProgressValue21, 0.22F * (float)Math.sin(angle) * shinProgressFactor + 0.04F * poseProgress.getAgeCode(90.0F, 1.0F) * shinProgressFactor3, 0.0F);
      foreShinScale.getAmounts(this.cigar, shinProgressTotal4 + poseProgress.shinProgressAmount, -0.8F * dinForeValue + poseProgress.headYawRadians, -0.7F * shinProgressTotal3 + shinProgressTotal5);
      foreShinScale.getAmountsForeShin2(this.cigar, 0.0F, -0.3F * shinProgressFactor * (0.5F + 0.5F * (float)Math.cos(2.0F * angle)), 0.0F);
      foreShinScale.getAmounts(this.flash, shinProgressTotal8, shinProgressTotal9, shinProgressValue9);
      foreShinScale.getAmountsForeShin2(this.flash, shinProgressTotal10, shinProgressTotal11, shinProgressTotal12);
      foreShinScale.getClassForForeEarFloat(this.flashSection, shinProgressLimit > 0.2F ? 0.8F + 0.7F * shinProgressLimit : 0.01F);
      handle(foreShinScale, this.flashSection, SWING_AXIS, poseProgress.swingProgress * 9.0F);
      float[] floats = new float[3];
      float shinProgressTotal14 = shinProgressTable[0];
      float shinProgressTotal15 = shinProgressTable[1];
      float shinProgressTotal16 = shinProgressTable[2];
      float shinProgressLimit2 = (poseProgress.age + (poseProgress.hashCode & 127)) % 160.0F;
      if (shinProgressFactor3 > 0.0F && shinProgressLimit2 > 100.0F && shinProgressLimit2 < 136.0F) {
         float shinProgressFactor6 = (shinProgressLimit2 - 100.0F) / 36.0F;
         float shinProgressFactor7 = getAmount2(shinProgressFactor6 / 0.25F) * (1.0F - getAmount2((shinProgressFactor6 - 0.75F) / 0.25F)) * shinProgressFactor3;
         float shinProgressValue22 = 0.35F * Math.abs((float)Math.sin(shinProgressFactor6 * Math.PI * 4.0));
         shinProgressTotal14 += (ARM_REST_OFFSET[0] - 0.35F * shinProgressValue22 - shinProgressTotal14) * shinProgressFactor7;
         shinProgressTotal15 += (ARM_REST_OFFSET[1] - shinProgressTotal15) * shinProgressFactor7;
         shinProgressTotal16 += (ARM_REST_OFFSET[2] + 0.15F * shinProgressValue22 - shinProgressTotal16) * shinProgressFactor7;
      }

      handle(shinProgressTotal8, shinProgressTotal9, shinProgressValue9, shinProgressTotal14 - 3.45F, shinProgressTotal15 - 14.1F, shinProgressTotal16 - -2.0F, floats);
      handle(
         foreShinScale,
         this.foreL,
         this.foreEar,
         -3.85F,
         20.9F,
         0.1F,
         3.45F + shinProgressTotal10 + floats[0],
         14.1F + shinProgressTotal11 + floats[1],
         -2.0F + shinProgressTotal12 + floats[2],
         -0.35F,
         -0.85F,
         0.35F
      );
      handle(foreShinScale, this.foreR, this.foreRightSection, 3.85F, 20.9F, 0.1F, 3.45F + shinProgressTotal10, 14.1F + shinProgressTotal11, -2.0F + shinProgressTotal12, 0.3F, -0.45F, 0.85F);
      float shinProgressValue23 = (poseProgress.age + (poseProgress.hashCode & 63)) % 140.0F;
      float shinProgressValue24 = shinProgressValue23 < 36.0F ? (float)Math.sin(shinProgressValue23 / 36.0F * Math.PI) : 0.0F;
      foreShinScale.getAmounts(this.ember, 0.3F * shinProgressValue24 * shinProgressFactor3 + 0.05F * poseProgress.getAgeCode(50.0F, 2.0F) + 0.2F * shinProgressFactor4, 0.0F, 0.0F);
      foreShinScale.getClassForForeEarFloat(this.emberSection, 1.0F + 0.45F * shinProgressValue24);

      for (int i = 0; i < this.shinFootForeEars.length; i++) {
         float shinProgressFactor8 = (shinProgressValue23 - 24.0F - i * 10.0F) / 44.0F;
         if (!(shinProgressFactor8 <= 0.0F) && !(shinProgressFactor8 >= 1.0F)) {
            float shinProgressBound = (0.3F + (i == 0 ? 0.9F : 0.6F) * shinProgressFactor8) * Math.min(1.0F, shinProgressFactor8 / 0.08F) * Math.min(1.0F, (1.0F - shinProgressFactor8) / 0.18F);
            foreShinScale.getAmountsForeShin2(this.shinFootForeEars[i], 1.6F * shinProgressFactor8 + 0.2F * (float)Math.sin(shinProgressFactor8 * 6.0F + i), 3.5F * shinProgressFactor8, -0.45F * shinProgressFactor8);
            foreShinScale.getClassForForeEarFloat(this.shinFootForeEars[i], Math.max(0.01F, shinProgressBound));
         } else {
            foreShinScale.getClassForForeEarFloat(this.shinFootForeEars[i], 0.01F);
         }
      }

      float shinProgressLimit3 = (poseProgress.age + (poseProgress.hashCode & 0xFF) * 0.37F) % 97.0F;
      if (shinProgressLimit3 > 92.0F) {
         foreShinScale.getAmountsForForeEarFloat(this.classShinProgress, 1.0F, 0.15F, 1.0F);
      }
   }

   private static void handle(
      ForeShinScale foreShinScale, ForeEar foreEar, ForeEar foreEar2, float foreShinValue, float foreShinValue2, float foreShinValue3, float foreShinValue4, float foreShinValue5, float foreShinValue6, float foreShinFactor, float foreShinFactor2, float foreShinFactor3
   ) {
      float foreShinFactor4 = foreShinValue4 - foreShinValue;
      float foreShinFactor5 = foreShinValue5 - foreShinValue2;
      float foreShinFactor6 = foreShinValue6 - foreShinValue3;
      float distance = (float)Math.sqrt(foreShinFactor4 * foreShinFactor4 + foreShinFactor5 * foreShinFactor5 + foreShinFactor6 * foreShinFactor6);
      if (!(distance < 1.0E-4F)) {
         float foreShinFactor7 = foreShinFactor4 / distance;
         float foreShinFactor8 = foreShinFactor5 / distance;
         float foreShinFactor9 = foreShinFactor6 / distance;
         float max10 = Math.max(0.05F, Math.min(10.58F, distance));
         float foreShinFactor11 = (0.0F + max10 * max10) / (2.0F * max10);
         float sqrt = (float)Math.sqrt(Math.max(0.0F, 28.090002F - foreShinFactor11 * foreShinFactor11));
         float foreShinFactor12 = foreShinFactor * foreShinFactor7 + foreShinFactor2 * foreShinFactor8 + foreShinFactor3 * foreShinFactor9;
         float foreShinFactor13 = foreShinFactor - foreShinFactor12 * foreShinFactor7;
         float foreShinFactor14 = foreShinFactor2 - foreShinFactor12 * foreShinFactor8;
         float foreShinFactor15 = foreShinFactor3 - foreShinFactor12 * foreShinFactor9;
         float sqrt2 = (float)Math.sqrt(foreShinFactor13 * foreShinFactor13 + foreShinFactor14 * foreShinFactor14 + foreShinFactor15 * foreShinFactor15);
         if (sqrt2 < 1.0E-4F) {
            foreShinFactor13 = 0.0F;
            foreShinFactor14 = 0.0F;
            foreShinFactor15 = 1.0F;
            sqrt2 = 1.0F;
         }

         float foreShinFactor16 = foreShinFactor13 / sqrt2;
         float foreShinFactor17 = foreShinFactor14 / sqrt2;
         float foreShinFactor18 = foreShinFactor15 / sqrt2;
         float foreShinValue7 = foreShinFactor7 * foreShinFactor11 + foreShinFactor16 * sqrt;
         float foreShinValue8 = foreShinFactor8 * foreShinFactor11 + foreShinFactor17 * sqrt;
         float foreShinValue9 = foreShinFactor9 * foreShinFactor11 + foreShinFactor18 * sqrt;
         float angle = (float)Math.asin(getMax(-foreShinValue9 / 5.3F));
         float angle2 = (float)Math.atan2(foreShinValue7 / 5.3F, -foreShinValue8 / 5.3F);
         float foreShinFactor19 = foreShinFactor7 * max10 - foreShinValue7;
         float foreShinFactor20 = foreShinFactor8 * max10 - foreShinValue8;
         float foreShinFactor21 = foreShinFactor9 * max10 - foreShinValue9;
         float sqrt3 = (float)Math.sqrt(foreShinFactor19 * foreShinFactor19 + foreShinFactor20 * foreShinFactor20 + foreShinFactor21 * foreShinFactor21);
         float foreShinFactor22 = foreShinFactor19 / sqrt3;
         float foreShinFactor23 = foreShinFactor20 / sqrt3;
         float foreShinFactor24 = foreShinFactor21 / sqrt3;
         float cos = (float)Math.cos(angle2);
         float sin = (float)Math.sin(angle2);
         float y = foreShinFactor22 * cos + foreShinFactor23 * sin;
         float foreShinFactor25 = -foreShinFactor22 * sin + foreShinFactor23 * cos;
         float cos2 = (float)Math.cos(angle);
         float sin2 = (float)Math.sin(angle);
         float foreShinValue10 = foreShinFactor25 * cos2 + foreShinFactor24 * sin2;
         float foreShinValue11 = -foreShinFactor25 * sin2 + foreShinFactor24 * cos2;
         foreShinScale.getAmounts(foreEar, angle, 0.0F, angle2);
         foreShinScale.getAmounts(foreEar2, (float)Math.asin(getMax(-foreShinValue11)), 0.0F, (float)Math.atan2(y, -foreShinValue10));
      }
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue2 = (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
            float absScaled = Math.abs(bonecaAmbalabuValue2 - 0.5F) * 2.0F;
            int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15785896), ForeShinUtils.getValueForInt(14270334), getAmount2((bonecaAmbalabuValue - 0.75F) / 0.25F));
            float bonecaAmbalabuLimit = 1.0F - Math.abs(bonecaAmbalabuValue2 - 0.35F) / 0.07F;
            if (bonecaAmbalabuLimit > 0.0F) {
               foreShinValue = ForeShinUtils.getValue2(
                  foreShinValue, ForeShinUtils.getValueForInt(16774874), 0.6F * Math.min(1.0F, bonecaAmbalabuLimit * 2.5F) * (1.0F - 0.6F * getAmount2((bonecaAmbalabuValue - 0.7F) / 0.3F))
               );
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(foreShinValue, 1.0F - 0.18F * absScaled * absScaled));
         }
      }
   }

   private static float getAmount(float[] foreShinValues) {
      float foreShinFactor = Math.abs(2.0F * foreShinValues[0] - 1.0F);
      float abs2 = Math.abs(2.0F * foreShinValues[1] - 1.0F);
      float foreShinFactor3 = foreShinFactor * foreShinFactor;
      float foreShinFactor4 = abs2 * abs2;
      return (float)Math.sqrt(Math.sqrt(foreShinFactor3 * foreShinFactor3 + foreShinFactor4 * foreShinFactor4));
   }

   private static float getAmount2(float foreShinFactor) {
      foreShinFactor = foreShinFactor < 0.0F ? 0.0F : (foreShinFactor > 1.0F ? 1.0F : foreShinFactor);
      return foreShinFactor * foreShinFactor * (3.0F - 2.0F * foreShinFactor);
   }

   private static float[] getAmounts(int foreShinDivisor, float foreShinValue, float foreShinValue2) {
      float[] floats = new float[4 * foreShinDivisor];

      for (int i = 0; i < 2 * foreShinDivisor; i++) {
         double angle = (Math.PI / 2) + Math.PI * i / foreShinDivisor;
         float foreShinFactor = (i & 1) == 0 ? foreShinValue : foreShinValue2;
         floats[2 * i] = foreShinFactor * (float)Math.cos(angle);
         floats[2 * i + 1] = foreShinFactor * (float)Math.sin(angle);
      }

      return floats;
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (float)i / (bonecaAmbalabu.bonecaValue - 1);
         float bonecaAmbalabuLimit = 0.13F + 0.07F * (1.0F - bonecaAmbalabuValue);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue2 = (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
            if (Math.abs(bonecaAmbalabuValue2 - 0.5F) < bonecaAmbalabuLimit) {
               bonecaAmbalabu.getBonecaAmbalabu(j, i, 0);
            }
         }
      }
   }

   private static boolean canFlag4(float foreShinValue, float foreShinLimit) {
      return foreShinLimit > 16.85F && Math.abs(foreShinValue) < Math.max(0.0F, (foreShinLimit - 17.3F) * 0.2F) + 0.3F && !canFlag(foreShinValue, foreShinLimit);
   }

   private static float[] getAmounts(float foreShinValue, float foreShinValue2, float foreShinValue3) {
      float[] floats = new float[3];
      handle((float)Math.toRadians(12.0), (float)Math.toRadians(45.0), 0.0F, foreShinValue, foreShinValue2, foreShinValue3, floats);
      return floats;
   }

   @Override
   public float getAmountFloat() {
      return 1.6F;
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, ForeShinConst.Wrap wrap) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11036986))
         .getBonecaAmbalabu(0.045F, 6.0F, 31)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8342825), 0.16F, 9.0F, 32);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, (ForeShinConst.Shade) (foreShinValue, foreShinLimit, foreShinValue2) -> {
         float angle = (float)Math.atan2(-foreShinValue, -foreShinValue2);
         float max3 = Math.max(0.0F, (13.2F - foreShinLimit) / 6.9F);
         float foreShinTotal = 1.0F - 0.16F * max3 * (0.5F + 0.5F * (float)Math.sin(angle * 7.0F + 0.6F));
         if (foreShinLimit > 12.4F) {
            foreShinTotal -= 0.1F * (foreShinLimit - 12.4F) / 0.8F;
         }

         if (foreShinLimit < 6.7F) {
            foreShinTotal -= 0.12F;
         }

         return foreShinTotal;
      });
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(12023620, 0.9F), (foreShinValue4, foreShinValue5, foreShinValue6) -> foreShinValue6 < 0.0F && canFlag3(foreShinValue4, foreShinValue5));
      handleBonecaAmbalabu(
         bonecaAmbalabu,
         wrap,
         ForeShinUtils.getValueForInt(6173975),
         (foreShinValue7, foreShinValue8, foreShinValue9) -> foreShinValue9 < 0.0F
            && !canFlag3(foreShinValue7, foreShinValue8)
            && Math.abs(foreShinValue7) > 2.1F
            && Math.abs(foreShinValue7) < 3.55F
            && (canFlag3(foreShinValue7 + 0.06F, foreShinValue8) || canFlag3(foreShinValue7 - 0.06F, foreShinValue8) || canFlag3(foreShinValue7, foreShinValue8 + 0.06F) || canFlag3(foreShinValue7, foreShinValue8 - 0.06F))
      );
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(6173975), (planValuesItem, foreShinValue11, foreShinValue12) -> foreShinValue12 < 0.0F && Math.abs(Math.abs(planValuesItem) - getAmount(foreShinValue11)) < 0.08F);
      handleBonecaAmbalabu(
         bonecaAmbalabu,
         wrap,
         ForeShinUtils.getValueForIntFloat(12023620, 0.85F),
         (foreShinValue13, foreShinValue14, foreShinValue15) -> foreShinValue15 < 0.0F && Math.abs(Math.abs(foreShinValue13) - getAmount(foreShinValue14) - 0.25F) < 0.03F && foreShinValue14 * 4.0F - (float)Math.floor(foreShinValue14 * 4.0F) < 0.6F
      );
      handleBonecaAmbalabu(
         bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(12023620, 0.85F), (foreShinValue16, foreShinValue17, foreShinValue18) -> Math.abs(foreShinValue17 - 6.85F) < 0.03F && foreShinValue16 * 4.0F - (float)Math.floor(foreShinValue16 * 4.0F) < 0.6F
      );
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(6173975, 0.7F), (foreShinValue19, foreShinValue20, foreShinLimit2) -> foreShinLimit2 > 0.0F && Math.abs(foreShinValue19) < 0.045F && foreShinValue20 < 10.2F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, (ForeShinConst.Shade) (foreShinLimit3, foreShinValue21, foreShinLimit4) -> foreShinLimit4 > 0.0F && foreShinLimit3 > 0.0F && foreShinLimit3 < 0.35F && foreShinValue21 < 10.2F ? 0.84F : 1.0F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, (ForeShinConst.Area) (foreShinValue22, foreShinValue23, foreShinValue24) -> foreShinValue24 < 0.0F && Math.abs(foreShinValue22) < getAmount(foreShinValue23));
   }

   private static float[] getAmountsForFloat(float foreShinValue, float foreShinValue2, float foreShinValue3) {
      float[] foreShinValues = getAmounts(foreShinValue, foreShinValue2, foreShinValue3);
      foreShinValues[0] += 3.45F;
      foreShinValues[1] += 14.1F;
      foreShinValues[2] += -2.0F;
      return foreShinValues;
   }

   private static boolean canFlag(float[] foreShinValues, float foreShinValue, float foreShinValue2) {
      boolean foreShinFlag = false;
      int foreShinLimit = foreShinValues.length / 2;
      int foreShinCounter = 0;

      for (int i = foreShinLimit - 1; foreShinCounter < foreShinLimit; i = foreShinCounter++) {
         float foreShinValue3 = foreShinValues[2 * foreShinCounter];
         float foreShinLimit2 = foreShinValues[2 * foreShinCounter + 1];
         float foreShinValue4 = foreShinValues[2 * i];
         float foreShinLimit3 = foreShinValues[2 * i + 1];
         if (foreShinLimit2 > foreShinValue2 != foreShinLimit3 > foreShinValue2 && foreShinValue < (foreShinValue4 - foreShinValue3) * (foreShinValue2 - foreShinLimit2) / (foreShinLimit3 - foreShinLimit2) + foreShinValue3) {
            foreShinFlag = !foreShinFlag;
         }
      }

      return foreShinFlag;
   }

   private static float getAmount(float[] foreShinValues, float[] foreShinValues2, float foreShinValue) {
      if (foreShinValue <= foreShinValues2[0]) {
         return foreShinValues[0];
      } else {
         for (int i = 1; i < foreShinValues2.length; i++) {
            if (foreShinValue <= foreShinValues2[i]) {
               float foreShinFactor = (foreShinValue - foreShinValues2[i - 1]) / Math.max(1.0E-6F, foreShinValues2[i] - foreShinValues2[i - 1]);
               return foreShinValues[i - 1] + (foreShinValues[i] - foreShinValues[i - 1]) * foreShinFactor;
            }
         }

         return foreShinValues[foreShinValues.length - 1];
      }
   }

   private static float getAmount3(float angle) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      return -0.85F * cos + 0.74F * sin - (float)Math.sqrt(0.04F * cos * cos + 3.35F * sin * sin);
   }

   @Override
   public String formatNoteBlock() {
      return "block.note_block.snare";
   }

   private static float getAmount4(float foreShinFactor) {
      return foreShinFactor * foreShinFactor;
   }

   private static float getMax(float foreShinBound) {
      return Math.max(-1.0F, Math.min(1.0F, foreShinBound));
   }

   private static float getAmount(float foreShinValue, float foreShinValue2, float foreShinValue3, float foreShinValue4, float foreShinValue5, float foreShinValue6) {
      float foreShinFactor = foreShinValue5 - foreShinValue3;
      float deltaY = foreShinValue6 - foreShinValue4;
      float foreShinLimit = foreShinFactor * foreShinFactor + deltaY * deltaY;
      float foreShinBound = foreShinLimit > 0.0F ? ((foreShinValue - foreShinValue3) * foreShinFactor + (foreShinValue2 - foreShinValue4) * deltaY) / foreShinLimit : 0.0F;
      float max2 = Math.max(0.0F, Math.min(1.0F, foreShinBound));
      float foreShinFactor3 = foreShinValue3 + max2 * foreShinFactor - foreShinValue;
      float foreShinFactor4 = foreShinValue4 + max2 * deltaY - foreShinValue2;
      return (float)Math.sqrt(foreShinFactor3 * foreShinFactor3 + foreShinFactor4 * foreShinFactor4);
   }

   @Override
   public String formatTungDin() {
      return "bobrito_bandito";
   }

   private static float getAmount5(float foreShinValue) {
      return 19.55F + 0.3F * getAmount4(foreShinValue / 3.9F);
   }

   private static boolean canFlag20(float foreShinValue, float foreShinDivisor, float foreShinLimit) {
      float hashRegistryValue = (foreShinValue + 50.0F) / foreShinDivisor;
      return hashRegistryValue - (float)Math.floor(hashRegistryValue) < foreShinLimit;
   }

   private static float getAmount2(float foreShinBound, float foreShinValue) {
      return Math.max(0.9F * foreShinValue * (1.0F - Math.max(0.0F, foreShinBound)), 0.65F * getAmount2(-foreShinBound / 0.35F));
   }

   @Override
   public int getValue() {
      return 6;
   }

   private static void handleAmountsBoneca(BonecaAmbalabu bonecaAmbalabu, float[] bonecaValues, float bonecaFactor, float bonecaValue, float bonecaValue2, float bonecaDivisor, float[] foreShinValues) {
      float bonecaValue3 = bonecaValue / bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaFactor2 = bonecaValue2 / bonecaAmbalabu.bonecaValue;
      float bonecaLimit = bonecaFactor2 * bonecaFactor;
      int index = 0;

      while (index < bonecaValues.length - 2 && bonecaValues[index + 1] < bonecaLimit) {
         index++;
      }

      float max3 = Math.max(0.0F, Math.min(1.0F, (bonecaLimit - bonecaValues[index]) / Math.max(1.0E-6F, bonecaValues[index + 1] - bonecaValues[index])));
      float bonecaFactor4 = bonecaTable[index] + (bonecaTable[index + 1] - bonecaTable[index]) * max3;
      float bonecaValue4 = bonecaFactor4 * (float)Math.sin((float) (Math.PI * 2) * (bonecaValue3 - 0.5F));
      float hashRegistryValue = bonecaLimit / bonecaDivisor;
      float floor6 = (float)Math.floor(hashRegistryValue);
      float bonecaValue7 = bonecaValue4 / (bonecaDivisor * 1.15F) + (((int)floor6 & 1) == 0 ? 0.0F : 0.5F);
      float floor8 = (float)Math.floor(bonecaValue7);
      foreShinValues[0] = bonecaValue7 - floor8;
      foreShinValues[1] = hashRegistryValue - floor6;
      foreShinValues[2] = floor6;
      foreShinValues[3] = floor8;
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(7029801)).getBonecaAmbalabu(0.07F, 4.0F, 55);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuLimit = (i + 0.5F) / bonecaAmbalabu.bonecaValue;

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float cos = (float)Math.cos((float) (Math.PI * 2) * ((j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F));
            float bonecaAmbalabuValue = 0.75F * getAmount2((cos + 0.1F) / 0.8F);
            int foreShinValue = ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), ForeShinUtils.getValueForInt(9200195), bonecaAmbalabuValue);
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(foreShinValue, bonecaAmbalabuLimit > 0.62F ? 1.0F - 0.25F * getAmount2((bonecaAmbalabuLimit - 0.62F) / 0.3F) : 1.0F));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(9200195), 0.12F, 5.0F, 56);

      for (int k = 0; k < 40; k++) {
         float classBound = getAmount(k, 21) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(k, 22) * bonecaAmbalabu.bonecaValue;
         int bonecaAmbalabuValue5 = (k & 1) == 0 ? ForeShinUtils.getValueForIntFloat(9200195, 0.45F) : ForeShinUtils.getValueForIntFloat(4532759, 0.4F);
         bonecaAmbalabu.getClass02(classBound, amountForFloat, classBound + 0.6F, amountForFloat + 2.5F, 0.8F, bonecaAmbalabuValue5);
      }
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(2303014)).getBonecaAmbalabu(0.05F, 4.0F, 91);
      float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      float amountForFloat = bonecaAmbalabu.bonecaValue * 0.5F;
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, bonecaAmbalabu.ambalabuBonecaValue * 0.47F, bonecaAmbalabu.bonecaValue * 0.47F, 1.6F, ForeShinUtils.getValueForInt(3948099));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, bonecaAmbalabu.ambalabuBonecaValue * 0.3F, bonecaAmbalabu.bonecaValue * 0.3F, 1.2F, ForeShinUtils.getValueForInt(3421754));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, bonecaAmbalabu.ambalabuBonecaValue * 0.1F, bonecaAmbalabu.bonecaValue * 0.1F, ForeShinUtils.getValueForInt(4869201));

      for (int i = 0; i < 6; i++) {
         double angle = (Math.PI * 2) * i / 6.0;
         bonecaAmbalabu.getClass022(
            bonecaAmbalabuValue + (float)Math.cos(angle) * bonecaAmbalabu.ambalabuBonecaValue * 0.39F,
            amountForFloat + (float)Math.sin(angle) * bonecaAmbalabu.bonecaValue * 0.39F,
            1.3F,
            1.3F,
            ForeShinUtils.getValueForInt(5592924)
         );
      }
   }

   @Override
   public String formatTungDinString() {
      return "Bobrito Bandito";
   }

   private static void handleForBonecaAmbalabuWrap(BonecaAmbalabu bonecaAmbalabu, ForeShinConst.Wrap wrap) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11036986))
         .getBonecaAmbalabu(0.045F, 6.0F, 21)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8342825), 0.16F, 9.0F, 22);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, (ForeShinConst.Shade) (foreShinValue, foreShinLimit, foreShinLimit2) -> {
         float foreShinTotal = 1.0F;
         float absScaled = Math.abs(foreShinValue) / 3.9F;
         if (foreShinLimit < 20.6F && foreShinLimit > 14.5F && absScaled > 0.78F && Math.abs(foreShinLimit2) < 1.6F) {
            foreShinTotal -= 0.14F * Math.min(1.0F, (absScaled - 0.78F) / 0.2F);
         }

         float minAbs = Math.min(Math.abs(foreShinLimit - 14.05F), Math.abs(foreShinLimit - 12.85F));
         if (minAbs < 0.8F) {
            foreShinTotal -= 0.12F * (1.0F - minAbs / 0.8F);
         }

         float foreShinTotal2 = foreShinTotal - 0.06F * (0.5F + 0.5F * (float)Math.sin(foreShinValue * 2.3F + foreShinLimit * 0.35F)) * (foreShinLimit2 > 0.0F ? 1.0F : 0.5F);
         if (foreShinLimit2 > 0.0F && foreShinLimit > 19.8F) {
            foreShinTotal2 += 0.05F;
         }

         return foreShinTotal2;
      });
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(1776414), (foreShinValue2, foreShinValue3, foreShinValue4) -> foreShinValue4 < 0.0F && canFlagForFloat(foreShinValue2, foreShinValue3));
      handleBonecaAmbalabu(
         bonecaAmbalabu,
         wrap,
         ForeShinUtils.getValueForIntFloat(13684944, 0.8F),
         (foreShinValue5, foreShinValue6, foreShinValue7) -> foreShinValue7 < 0.0F && canFlagForFloat(foreShinValue5, foreShinValue6) && !canFlag4(foreShinValue5, foreShinValue6) && !canFlag(foreShinValue5, foreShinValue6) && canFlag20(foreShinValue5, 0.62F, 0.12F)
      );
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(3026485), (foreShinValue8, foreShinValue9, foreShinValue10) -> foreShinValue10 < 0.0F && canFlagForFloat(foreShinValue8, foreShinValue9) && canFlag4(foreShinValue8, foreShinValue9));
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(15526370), (foreShinValue11, foreShinValue12, foreShinValue13) -> foreShinValue13 < 0.0F && canFlag(foreShinValue11, foreShinValue12));
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(11117464, 0.9F), (foreShinValue14, foreShinValue15, foreShinValue16) -> foreShinValue16 < 0.0F && canFlag(foreShinValue14, foreShinValue15) && getAmount(SECONDARY_OUTLINE_PAIRS, Math.abs(foreShinValue14), foreShinValue15) < 0.05F);
      handleBonecaAmbalabu(
         bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(1118481), (foreShinValue17, foreShinLimit3, foreShinValue18) -> foreShinValue18 < 0.0F && canFlag(foreShinValue17, foreShinLimit3) && foreShinLimit3 > 21.4F && foreShinLimit3 < 22.25F && Math.abs(foreShinValue17) < 0.36F - (22.25F - foreShinLimit3) * 0.1F
      );
      handleBonecaAmbalabu(
         bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(1118481), (foreShinValue19, foreShinValue20, foreShinValue21) -> foreShinValue21 < 0.0F && canFlag(foreShinValue19, foreShinValue20) && foreShinValue20 <= 21.4F && Math.abs(foreShinValue19) < 0.22F + (21.4F - foreShinValue20) * 0.05F
      );
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(3816000, 0.9F), (planValuesItem, foreShinValue23, foreShinValue24) -> foreShinValue24 < 0.0F && Math.abs(foreShinValue23 - 21.4F) < 0.035F && Math.abs(planValuesItem) < 0.27F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(4539724), (foreShinFactor, foreShinValue25, foreShinValue26) -> foreShinValue26 < 0.0F && foreShinFactor * foreShinFactor + (foreShinValue25 - 16.35F) * (foreShinValue25 - 16.35F) < 0.0121F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(12023620), (foreShinValue27, foreShinValue28, foreShinValue29) -> foreShinValue29 < 0.0F && !canFlagForFloat(foreShinValue27, foreShinValue28) && canFlag(MAIN_OUTLINE_PAIRS, Math.abs(foreShinValue27), foreShinValue28));
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, (ForeShinConst.Shade) (foreShinValue30, foreShinValue31, foreShinLimit4) -> {
         if (!(foreShinLimit4 >= 0.0F) && !canFlagForFloat(foreShinValue30, foreShinValue31) && !canFlag(MAIN_OUTLINE_PAIRS, Math.abs(foreShinValue30), foreShinValue31)) {
            float foreShinValue32 = getAmount(MAIN_OUTLINE_PAIRS, Math.abs(foreShinValue30), foreShinValue31);
            return foreShinValue32 < 0.3F ? 0.8F + 0.2F * foreShinValue32 / 0.3F : 1.0F;
         } else {
            return 1.0F;
         }
      });
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(6173975), (foreShinValue33, foreShinLimit5, foreShinValue34) -> foreShinValue34 < 0.0F && getAmount(MAIN_OUTLINE_PAIRS, Math.abs(foreShinValue33), foreShinLimit5) < 0.065F && foreShinLimit5 > 14.7F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(2364940, 0.85F), (foreShinValue35, foreShinValue36, foreShinValue37) -> foreShinValue37 < 0.0F && getAmount(foreShinValue35, foreShinValue36) < 0.27F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(3810324), (foreShinValue38, foreShinValue39, foreShinValue40) -> foreShinValue40 < 0.0F && getAmount(foreShinValue38, foreShinValue39) < 0.22F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(12023620, 0.85F), (foreShinValue41, foreShinValue42, foreShinValue43) -> foreShinValue43 < 0.0F && getAmount(foreShinValue41 + 0.07F, foreShinValue42 - 0.07F) < 0.075F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, (ForeShinConst.Shade) (foreShinValue44, foreShinLimit6, foreShinLimit7) -> foreShinLimit7 > 0.0F && foreShinLimit6 < getAmount5(foreShinValue44) && foreShinLimit6 > getAmount5(foreShinValue44) - 0.22F ? 0.84F + 0.16F * (getAmount5(foreShinValue44) - foreShinLimit6) / 0.22F : 1.0F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(12023620, 0.65F), (foreShinValue45, foreShinLimit8, foreShinLimit9) -> foreShinLimit9 > 0.3F && foreShinLimit8 >= getAmount5(foreShinValue45) && foreShinLimit8 < 21.8F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForInt(6173975), (foreShinValue46, foreShinValue47, foreShinLimit10) -> foreShinLimit10 > 0.3F && Math.abs(foreShinValue47 - getAmount5(foreShinValue46)) < 0.065F);
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(6173975, 0.55F), (foreShinValue48, foreShinValue49, foreShinLimit11) -> foreShinLimit11 > 0.0F && Math.abs(foreShinValue48) < 0.04F && foreShinValue49 < getAmount5(foreShinValue48));
      handleBonecaAmbalabu(bonecaAmbalabu, wrap, ForeShinUtils.getValueForIntFloat(8342825, 0.6F), (foreShinValue50, foreShinValue51, foreShinValue52) -> Math.abs(foreShinValue52) < 0.05F && foreShinValue51 < 20.0F);
   }

   private static float getAmount(int foreShinValue, int foreShinValue2) {
      int foreShinValue3 = foreShinValue * 374761393 + foreShinValue2 * 668265263 + 1442695041;
      int foreShinValue4 = (foreShinValue3 ^ foreShinValue3 >>> 13) * 1274126177;
      int foreShinValue5 = foreShinValue4 ^ foreShinValue4 >>> 16;
      return (foreShinValue5 & 16777215) / 1.6777215E7F;
   }

   private interface Area {
      boolean in(float areaValue, float areaValue2, float areaValue3);
   }

   private interface Shade {
      float at(float shadeValue, float shadeValue2, float shadeValue3);
   }

   private static final class Wrap {
      private final float totalLength;
      private final float[] wrapTable;
      private final float wrapAmount;
      private final boolean wrapActive;
      private final float[] table;
      private final float cosOffsetScale;
      private final float depthScale;
      private final float[] cumulativeLengths;
      private final float centerOffsetX;

      Wrap(float[] amounts, float[] amounts2, float amount, float amount2) {
         this.wrapTable = amounts;
         this.table = amounts2;
         this.wrapAmount = amount;
         this.depthScale = amount2;
         this.wrapActive = false;
         this.centerOffsetX = 0.0F;
         this.cosOffsetScale = 0.0F;
         this.cumulativeLengths = new float[amounts.length];
         float wrapFactor = 0.5F * (amount + amount2);

         for (int i = 1; i < amounts.length; i++) {
            float wrapFactor2 = (amounts[i] - amounts[i - 1]) * wrapFactor;
            float wrapFactor3 = amounts2[i] - amounts2[i - 1];
            this.cumulativeLengths[i] = this.cumulativeLengths[i - 1] + (float)Math.sqrt(wrapFactor2 * wrapFactor2 + wrapFactor3 * wrapFactor3);
         }

         this.totalLength = this.cumulativeLengths[amounts.length - 1];
      }

      void handleAmountsAmount(BonecaAmbalabu bonecaAmbalabu, float wrapValue, float wrapValue2, float[] wrapValues) {
         float max3 = Math.max(0.0F, Math.min(1.0F, (wrapValue - 0.5F) / (bonecaAmbalabu.ambalabuBonecaValue - 1)));
         float wrapFactor = Math.max(0.0F, Math.min(1.0F, (wrapValue2 - 0.5F) / (bonecaAmbalabu.bonecaValue - 1)));
         float angle = (float) (Math.PI * 2) * (max3 - 0.5F);
         float sin4;
         float wrapValue5;
         if (this.wrapActive) {
            float angle2 = (float) Math.PI * wrapFactor;
            sin4 = (float)Math.sin(angle2);
            wrapValue5 = this.centerOffsetX + this.cosOffsetScale * (float)Math.cos(angle2);
         } else {
            float wrapLimit = (1.0F - wrapFactor) * this.totalLength;
            int index = 0;

            while (index < this.cumulativeLengths.length - 2 && this.cumulativeLengths[index + 1] < wrapLimit) {
               index++;
            }

            float wrapLimit2 = this.cumulativeLengths[index + 1] - this.cumulativeLengths[index];
            float wrapBound = wrapLimit2 > 1.0E-6F ? (wrapLimit - this.cumulativeLengths[index]) / wrapLimit2 : 0.0F;
            float max2 = Math.max(0.0F, Math.min(1.0F, wrapBound));
            sin4 = this.wrapTable[index] + (this.wrapTable[index + 1] - this.wrapTable[index]) * max2;
            wrapValue5 = this.table[index] + (this.table[index + 1] - this.table[index]) * max2;
         }

         wrapValues[0] = -sin4 * this.wrapAmount * (float)Math.sin(angle);
         wrapValues[1] = wrapValue5;
         wrapValues[2] = -sin4 * this.depthScale * (float)Math.cos(angle);
      }

      Wrap(float amount, float amount2, float amount3, float amount4) {
         this.wrapTable = null;
         this.table = null;
         this.cumulativeLengths = null;
         this.totalLength = 1.0F;
         this.wrapAmount = amount2;
         this.depthScale = amount4;
         this.centerOffsetX = amount;
         this.cosOffsetScale = amount3;
         this.wrapActive = true;
      }
   }
}
