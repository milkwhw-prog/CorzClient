package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.LoftLatheFlag;

public final class ShinThigh extends WarmBrainrotBase {
   private ForeEar classShinProgress;
   private static final float[] shinThighTable;
   private static final float[] bonecaTable;
   private static final float[] table = new float[]{
      -22.75F,
      17.03F,
      16.97F,
      0.02F,
      -22.6F,
      17.45F,
      16.57F,
      0.4F,
      -22.3F,
      17.85F,
      16.02F,
      0.8F,
      -21.8F,
      18.2F,
      15.5F,
      1.45F,
      -21.2F,
      18.5F,
      15.02F,
      1.95F,
      -19.5F,
      19.15F,
      14.05F,
      2.8F,
      -17.0F,
      19.85F,
      13.12F,
      3.4F,
      -13.5F,
      20.69F,
      12.33F,
      3.95F,
      -9.5F,
      21.09F,
      11.77F,
      4.05F,
      -5.0F,
      21.26F,
      11.54F,
      4.16F,
      -0.5F,
      21.15F,
      11.6F,
      4.02F,
      4.0F,
      20.69F,
      11.94F,
      3.64F,
      8.0F,
      19.96F,
      12.73F,
      2.94F,
      11.5F,
      19.11F,
      13.8F,
      2.11F,
      14.5F,
      18.38F,
      15.04F,
      1.28F,
      17.0F,
      17.93F,
      15.89F,
      0.83F,
      19.2F,
      17.81F,
      16.12F,
      0.69F,
      20.4F,
      17.75F,
      16.2F,
      0.3F
   };
   private static final float[] SPINE_Z;
   private ForeEar jaw;
   private static final float[] SPINE_ARC_LENGTHS;
   private static final float[] OUTLINE_POLYGON;
   private ForeEar shinL;
   private ForeEar footR;
   private static final float[] SPLINE_RADII;
   private ForeEar classShinFoot;
   private ForeEar shinFootForeEar;
   private ForeEar dorsal;
   private ForeEar shinB;
   private ForeEar shinR;
   private static final float[] PROFILE_POINTS;
   private ForeEar shinProgressForeEar;
   private static final float[] SPLINE_Z;
   private static final float[] LOFT_RADII_X;
   private ForeEar shinThighForeEar;
   private static final float[] bonecaAmbalabuTable;
   private ForeEar footB;
   private ForeEar foreEar;
   private ForeEar tail2;
   private static final float[] shinFootTable;
   private ForeEar footL;
   private ForeEar thighB;
   private static final float[] SPLINE_Y;
   private static final float[] SPINE_Y;

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float rocketsElytraBound = (float)Math.sin(2.0F * angle);
      float cos2 = (float)Math.cos(2.0F * angle);
      float shinProgressFactor2 = Math.max(0.0F, -sin) * shinProgressFactor;
      float shinProgressFactor3 = Math.max(0.0F, sin) * shinProgressFactor;
      float shinProgressFactor4 = Math.max(0.0F, cos2) * shinProgressFactor;
      float shinProgressTotal = 0.42F * cos * shinProgressFactor;
      float shinProgressTotal2 = -shinProgressTotal;
      float shinProgressTotal3 = 0.32F * rocketsElytraBound * shinProgressFactor;
      float shinProgressTotal4 = -0.75F * shinProgressFactor2;
      float foreShinValue = -0.75F * shinProgressFactor3;
      float shinProgressTotal6 = -0.9F * shinProgressFactor4;
      float shinProgressTotal7 = -0.1F * shinProgressFactor2 * shinProgressFactor2;
      float shinProgressValue = -0.1F * shinProgressFactor3 * shinProgressFactor3;
      float shinProgressValue2 = -0.1F * shinProgressFactor4 * shinProgressFactor4;
      float shinProgressTotal8 = -0.02F * cos * cos * shinProgressFactor - 0.1F * shinProgressFactor4 + 0.006F * poseProgress.getAgeCode(70.0F, 0.5F) * (1.0F - shinProgressFactor);
      float shinProgressTotal9 = -0.08F * sin * shinProgressFactor;
      float shinProgressTotal10 = 0.0F;
      float shinProgressLimit = 0.0F;
      float shinProgressFactor5 = 1.0F - shinProgressFactor;
      foreShinScale.getAmounts(this.tail2, 0.0F, 0.2F * sin * shinProgressFactor + 0.09F * poseProgress.getAgeCode(52.0F, 0.0F) * shinProgressFactor5, 0.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, -0.12F * shinProgressFactor4, 0.28F * (float)Math.sin(angle - 0.9F) * shinProgressFactor + 0.15F * poseProgress.getAgeCode(52.0F, -1.1F) * shinProgressFactor5, 0.0F);
      foreShinScale.getAmounts(this.shinFootForeEar, 0.0F, 0.0F, 0.14F * (float)Math.sin(2.0F * angle + 0.5F) * shinProgressFactor + 0.02F * poseProgress.getAgeCode(37.0F, 0.3F));
      foreShinScale.getAmounts(this.jaw, 0.05F * rocketsElytraBound * shinProgressFactor, -0.07F * sin * shinProgressFactor, 0.0F);
      foreShinScale.getAmounts(this.classShinProgress, -0.05F - 0.14F * Math.max(0.0F, rocketsElytraBound) * shinProgressFactor - 0.03F * (0.5F + 0.5F * poseProgress.getAgeCode(64.0F, 0.0F)) * shinProgressFactor5, 0.0F, 0.0F);
      float amount2 = getAmount2(poseProgress.headYawRadians * 0.45F, -0.24F, 0.24F);
      float shinProgressValue4 = getAmount2(poseProgress.shinProgressAmount * 0.4F, -0.15F, 0.15F);
      foreShinScale.getAmountsBoneCount(this.jaw, shinProgressValue4, amount2, 0.0F);
      foreShinScale.getAmountsBoneCount(this.tail2, 0.0F, -amount2 * 0.6F, 0.0F);
      if (shinProgressFactor < 0.3F && poseProgress.swingProgress <= 0.0F && !poseProgress.sneakingPose && !poseProgress.vehicle && !poseProgress.swimming && !poseProgress.gliding && !poseProgress.inPose) {
         float shinProgressFactor6 = 1.0F - shinProgressFactor / 0.3F;
         float shinProgressLimit2 = (poseProgress.age + (poseProgress.hashCode & 127)) % 160.0F;
         if (shinProgressLimit2 < 45.0F) {
            float shinProgressFactor7 = shinProgressLimit2 / 15.0F;
            float sin3 = (float)Math.sin((float) Math.PI * shinProgressFactor7);
            float shinProgressFactor8 = sin3 * sin3;
            float shinProgressValue5 = ((int)shinProgressFactor7 % 2 == 0 ? 1.0F : -1.0F) * shinProgressFactor8;
            foreShinScale.getAmountsBoneCount(this.classShinProgress, -0.34F * shinProgressFactor8 * shinProgressFactor6, 0.0F, 0.0F);
            foreShinScale.getAmountsBoneCount(this.jaw, 0.1F * shinProgressFactor8 * shinProgressFactor6, 0.07F * shinProgressValue5 * shinProgressFactor6, 0.0F);
            foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, 0.0F, 0.22F * shinProgressValue5 * shinProgressFactor6, 0.0F);
            foreShinScale.getAmountsBoneCount(this.tail2, 0.0F, 0.08F * shinProgressValue5 * shinProgressFactor6, 0.0F);
            shinProgressTotal9 += 0.05F * shinProgressValue5 * shinProgressFactor6;
            shinProgressTotal += 0.28F * shinProgressFactor8 * shinProgressFactor6;
            shinProgressTotal2 += 0.28F * shinProgressFactor8 * shinProgressFactor6;
            shinProgressTotal3 += 0.28F * shinProgressFactor8 * shinProgressFactor6;
            shinProgressTotal4 -= 0.55F * shinProgressFactor8 * shinProgressFactor6;
            foreShinValue -= 0.55F * shinProgressFactor8 * shinProgressFactor6;
            shinProgressTotal6 -= 0.55F * shinProgressFactor8 * shinProgressFactor6;
         } else if (shinProgressLimit2 >= 100.0F && shinProgressLimit2 < 136.0F) {
            shinProgressLimit = Math.max(0.0F, (float)Math.sin((shinProgressLimit2 - 100.0F) / 12.0F * (float) (Math.PI * 2))) * shinProgressFactor6;
            shinProgressTotal7 += 0.5F * shinProgressLimit;
         }
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressFactor9 = swingProgress < 0.3F ? getAmount3(swingProgress / 0.3F) : 1.0F - getAmount3((swingProgress - 0.3F) / 0.15F);
         float shinProgressValue6 = swingProgress < 0.45F ? getAmount3(swingProgress / 0.45F) : 1.0F - getAmount3((swingProgress - 0.45F) / 0.55F);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, -0.85F * shinProgressFactor9, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.jaw, 0.28F * shinProgressFactor9 - 0.34F * shinProgressValue6, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tail2, 0.0F, 0.12F * shinProgressFactor9, 0.0F);
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, 0.0F, 0.55F * (float)Math.sin(swingProgress * (float) (Math.PI * 2)), 0.0F);
         shinProgressTotal10 -= 3.6F * shinProgressValue6;
         shinProgressTotal8 -= 0.12F * shinProgressValue6;
         shinProgressTotal += shinProgressTotal10 / 12.5F + 0.12F * shinProgressValue6;
         shinProgressTotal2 += shinProgressTotal10 / 12.5F + 0.12F * shinProgressValue6;
         shinProgressTotal4 -= 0.38F * shinProgressValue6;
         foreShinValue -= 0.38F * shinProgressValue6;
         shinProgressTotal3 += shinProgressTotal10 / 11.5F - 0.4F * shinProgressValue6;
         shinProgressTotal6 -= 0.5F * shinProgressValue6;
      }

      if (poseProgress.sneakingPose) {
         shinProgressTotal += 0.42F;
         shinProgressTotal2 += 0.42F;
         shinProgressTotal3 += 0.42F;
         shinProgressTotal4 -= 0.82F;
         foreShinValue -= 0.82F;
         shinProgressTotal6 -= 0.82F;
         foreShinScale.getAmountsBoneCount(this.jaw, -0.1F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tail2, 0.06F, 0.0F, 0.0F);
      }

      boolean shinProgressFlag = !poseProgress.swimming && !poseProgress.gliding && !poseProgress.vehicle && !poseProgress.inPose;
      if (poseProgress.vehicle) {
         shinProgressTotal = 1.3F;
         shinProgressTotal2 = 1.3F;
         shinProgressTotal3 = 1.12F;
         shinProgressTotal4 = 0.12F;
         foreShinValue = 0.12F;
         shinProgressTotal6 = 0.3F;
         shinProgressTotal7 = 0.3F;
         shinProgressValue = 0.3F;
         shinProgressValue2 = 0.3F;
      }

      if (poseProgress.swimming || poseProgress.gliding) {
         float angle2 = poseProgress.age * 0.45F;
         float shinProgressFactor10 = poseProgress.gliding ? 0.15F : 0.4F;
         shinProgressTotal = (poseProgress.gliding ? -0.5F : 0.1F) + shinProgressFactor10 * (float)Math.sin(angle2);
         shinProgressTotal2 = (poseProgress.gliding ? -0.5F : 0.1F) + shinProgressFactor10 * (float)Math.sin(angle2 + (float) Math.PI);
         shinProgressTotal3 = (poseProgress.gliding ? -0.6F : 0.0F) + shinProgressFactor10 * (float)Math.sin(angle2 + (float) (Math.PI / 2));
         shinProgressTotal4 = -0.35F - 0.3F * (0.5F + 0.5F * (float)Math.sin(angle2 - 1.0F));
         foreShinValue = -0.35F - 0.3F * (0.5F + 0.5F * (float)Math.sin(angle2 + (float) Math.PI - 1.0F));
         shinProgressTotal6 = -0.35F - 0.3F * (0.5F + 0.5F * (float)Math.sin(angle2 + (float) (Math.PI / 2) - 1.0F));
         shinProgressTotal7 = 0.3F;
         shinProgressValue = 0.3F;
         shinProgressValue2 = 0.3F;
         foreShinScale.getAmounts(this.tail2, 0.0F, 0.28F * (float)Math.sin(poseProgress.age * 0.3F), 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, 0.0F, 0.42F * (float)Math.sin(poseProgress.age * 0.3F - 1.0F), 0.0F);
      }

      if (poseProgress.inPose) {
         shinProgressTotal = 0.55F;
         shinProgressTotal2 = 0.7F;
         shinProgressTotal3 = 0.5F;
         shinProgressTotal4 = -1.1F;
         foreShinValue = -1.2F;
         shinProgressTotal6 = -1.0F;
         foreShinScale.getAmounts(this.classShinProgress, -0.12F, 0.0F, 0.0F);
      }

      float shinProgressValue7 = shinProgressLimit > 0.0F ? -getAmount(shinProgressTotal7) + 0.12F * shinProgressLimit : 0.0F;
      float min8 = 0.0F;
      if (shinProgressFlag) {
         float shinProgressBound = Math.min(getAmount(14.3F, shinProgressTotal, shinProgressTotal4) + getAmount(shinProgressTotal7) + shinProgressValue7, getAmount(14.3F, shinProgressTotal2, foreShinValue) + getAmount(shinProgressValue));
         min8 = -Math.min(shinProgressBound, getAmount(13.9F, shinProgressTotal3, shinProgressTotal6) + getAmount(shinProgressValue2));
      }

      foreShinScale.getAmountsForeShin2(this.thighB, 0.0F, min8, shinProgressTotal10);
      foreShinScale.getAmounts(this.dorsal, shinProgressTotal8, 0.0F, shinProgressTotal9);
      foreShinScale.getAmounts(this.shinR, shinProgressTotal, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footR, shinProgressTotal4, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classShinFoot, shinProgressTotal7 - (shinProgressFlag ? shinProgressTotal + shinProgressTotal4 : 0.0F), 0.0F, 0.0F);
      foreShinScale.getAmountsForeShin2(this.classShinFoot, 0.0F, shinProgressValue7, 0.0F);
      foreShinScale.getAmounts(this.shinL, shinProgressTotal2, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footL, foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinThighForeEar, shinProgressValue - (shinProgressFlag ? shinProgressTotal2 + foreShinValue : 0.0F), 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinB, shinProgressTotal3, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footB, shinProgressTotal6, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.foreEar, shinProgressValue2 - (shinProgressFlag ? shinProgressTotal3 + shinProgressTotal6 : 0.0F), 0.0F, 0.0F);
      if (poseProgress.usingItem) {
         foreShinScale.getAmountsBoneCount(this.classShinProgress, -0.25F * (0.5F + 0.5F * (float)Math.sin(poseProgress.age * 1.3F)), 0.0F, 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.jaw, 1.06F, 0.94F, 1.06F);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, -0.3F, 0.0F, 0.0F);
      }
   }

   private static float[] getAmounts(float angle, float shinThighValue, float shinThighFactor, float shinThighValue3) {
      float shinThighValue4 = -19.7F + 6.9F * (1.0F - (float)Math.cos(angle));
      float shinThighValue5 = 1.36F * angle / (float) (Math.PI / 2) - shinThighValue3;
      float shinThighValue6 = -shinThighValue * ((float) Math.PI - shinThighValue5);
      return getAmounts(shinThighValue4, shinThighValue6, shinThighFactor);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int bonecaAmbalabuDivisor = bonecaAmbalabu.ambalabuBonecaValue - 1;
      int bonecaAmbalabuDivisor2 = bonecaAmbalabu.bonecaValue - 1;

      for (int i = 0; i <= bonecaAmbalabuDivisor2; i++) {
         for (int j = 0; j <= bonecaAmbalabuDivisor; j++) {
            int[] ints = new int[4];

            for (int k = 0; k < 4; k++) {
               float bonecaAmbalabuBound = (i + 0.5F * (k >> 1) - 0.25F) / bonecaAmbalabuDivisor2;
               float bonecaAmbalabuValue = (float) (Math.PI * 2) * ((j + 0.5F * (k & 1) - 0.25F) / bonecaAmbalabuDivisor - 0.5F);
               ints[k] = getValue(Math.max(0.0F, Math.min(1.0F, bonecaAmbalabuBound)), bonecaAmbalabuValue);
            }

            bonecaAmbalabu.getBonecaAmbalabu(
               j, i, ForeShinUtils.getValue2(ForeShinUtils.getValue2(ints[0], ints[1], 0.5F), ForeShinUtils.getValue2(ints[2], ints[3], 0.5F), 0.5F)
            );
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 6.0F, 31);

      for (int m = 0; m < 5; m++) {
         float hashHuntBound = (0.33F + m * 0.085F) * bonecaAmbalabuDivisor2;
         bonecaAmbalabu.getClass02(0.43F * bonecaAmbalabuDivisor, hashHuntBound + 2.9F, 0.5F * bonecaAmbalabuDivisor, hashHuntBound, 3.1F, ForeShinUtils.getValueForInt(16053492));
         bonecaAmbalabu.getClass02(0.5F * bonecaAmbalabuDivisor, hashHuntBound, 0.57F * bonecaAmbalabuDivisor, hashHuntBound + 2.9F, 3.1F, ForeShinUtils.getValueForInt(16053492));
         bonecaAmbalabu.getClass02(0.43F * bonecaAmbalabuDivisor, hashHuntBound + 4.5F, 0.57F * bonecaAmbalabuDivisor, hashHuntBound + 4.5F, 1.1F, ForeShinUtils.getValueForIntFloat(6063800, 0.6F));
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(16777215), (bonecaAmbalabuValue3, bonecaAmbalabuValue4) -> {
         float var4x = (bonecaAmbalabuValue3 - 0.5F) / bonecaAmbalabuDivisor;
         float var5x = (bonecaAmbalabuValue4 - 0.5F) / bonecaAmbalabuDivisor2;
         float var6x = (float) (Math.PI * 2) * (var4x - 0.5F);
         return Math.abs((float)Math.sin(var6x)) < 0.2F ? false : canFlag(bonecaAmbalabuTable, getAmountsForFloat(var5x)[0], (float)Math.cos(var6x));
      });
   }

   private static void handle(LatheLoft latheLoft, float[] shinThighValues, float[] shinThighValues2, float[] floats, float shinThighFactor, float shinThighFactor2, float shinThighFactor3, ShinBrr shinBrr) {
      float shinThighFactor4 = shinThighValues2[0] - shinThighValues[0];
      float shinThighFactor5 = shinThighValues2[1] - shinThighValues[1];
      float shinThighFactor6 = shinThighValues2[2] - shinThighValues[2];
      float shinThighFactor7 = floats[0] - shinThighValues[0];
      float shinThighFactor8 = floats[1] - shinThighValues[1];
      float shinThighFactor9 = floats[2] - shinThighValues[2];
      float shinThighValue = (shinThighFactor5 * shinThighFactor9 - shinThighFactor6 * shinThighFactor8) * shinThighFactor + (shinThighFactor6 * shinThighFactor7 - shinThighFactor4 * shinThighFactor9) * shinThighFactor2 + (shinThighFactor4 * shinThighFactor8 - shinThighFactor5 * shinThighFactor7) * shinThighFactor3;
      float[] shinThighValues4 = shinThighValue < 0.0F ? shinThighValues2 : shinThighValues;
      float[] shinThighValues5 = shinThighValue < 0.0F ? shinThighValues : shinThighValues2;
      float shinThighFactor10 = 0.07F / (float)Math.sqrt(shinThighFactor * shinThighFactor + shinThighFactor2 * shinThighFactor2 + shinThighFactor3 * shinThighFactor3);
      latheLoft.getLatheLoft(shinThighValues4[0], shinThighValues4[1], shinThighValues4[2], shinThighValues5[0], shinThighValues5[1], shinThighValues5[2], floats[0], floats[1], floats[2], shinBrr);
      latheLoft.getLatheLoft(
         shinThighValues5[0] - shinThighFactor * shinThighFactor10,
         shinThighValues5[1] - shinThighFactor2 * shinThighFactor10,
         shinThighValues5[2] - shinThighFactor3 * shinThighFactor10,
         shinThighValues4[0] - shinThighFactor * shinThighFactor10,
         shinThighValues4[1] - shinThighFactor2 * shinThighFactor10,
         shinThighValues4[2] - shinThighFactor3 * shinThighFactor10,
         floats[0] - shinThighFactor * shinThighFactor10 * 0.4F,
         floats[1] - shinThighFactor2 * shinThighFactor10 * 0.4F,
         floats[2] - shinThighFactor3 * shinThighFactor10 * 0.4F,
         shinBrr
      );
   }

   private static int getValue(float shinThighLimit, float angle) {
      float[] shinThighValues = getAmountsForFloat(shinThighLimit);
      float cos = (float)Math.cos(angle);
      float shinThighLimit2 = 0.5F + 0.5F * cos;
      float shinThighValue = shinThighValues[1] + shinThighValues[2] * cos;
      float shinThighLimit3 = shinThighValues[0];
      int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(4759794), ForeShinUtils.getValueForInt(8240630), 0.4F * getAmount3((shinThighLimit2 - 0.45F) / 0.4F));
      if (shinThighValue < 1.0F) {
         value2 = ForeShinUtils.getValueForInt(2780352);
      }

      if (shinThighLimit3 > 0.6F + 0.75F * shinThighLimit2 && shinThighLimit2 < 0.9F) {
         value2 = ForeShinUtils.getValueForInt(2780352);
      }

      if (shinThighLimit3 < -4.05F) {
         value2 = ForeShinUtils.getValueForInt(2780352);
      }

      if (shinThighLimit2 > 0.84F && shinThighLimit > 0.28F && shinThighLimit < 0.72F) {
         value2 = shinThighLimit2 > 0.88F ? ForeShinUtils.getValueForInt(9421813) : ForeShinUtils.getValueForInt(2780352);
      }

      if (shinThighLimit2 > 0.87F && shinThighLimit < 0.28F) {
         value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(1854086), ForeShinUtils.getValueForInt(1257562), getAmount3((shinThighLimit2 - 0.87F) / 0.1F));
      }

      return value2;
   }

   private static float getAmount(float shinThighValue, float angle, float foreShinValue) {
      float shinThighFactor = 7.2F - shinThighValue;
      float shinThighFactor2 = -4.7999997F;
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float cos2 = (float)Math.cos(angle + foreShinValue);
      float sin2 = (float)Math.sin(angle + foreShinValue);
      return shinThighValue + cos * shinThighFactor + sin * 0.25F + cos2 * shinThighFactor2 - sin2 * 0.25F - 2.4F;
   }

   private static float getAmount(float angle) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      return 2.4F + Math.min(-2.4F * cos - -5.18F * sin, -2.4F * cos - 2.05F * sin);
   }

   private static boolean canFlag(float[] shinThighValues, float shinThighValue, float shinThighValue2) {
      boolean shinThighFlag = false;
      int shinThighLimit = shinThighValues.length / 2;
      int shinThighCounter = 0;

      for (int i = shinThighLimit - 1; shinThighCounter < shinThighLimit; i = shinThighCounter++) {
         float shinThighValue3 = shinThighValues[2 * shinThighCounter];
         float shinThighLimit2 = shinThighValues[2 * shinThighCounter + 1];
         float shinThighValue4 = shinThighValues[2 * i];
         float shinThighLimit3 = shinThighValues[2 * i + 1];
         if (shinThighLimit2 > shinThighValue2 != shinThighLimit3 > shinThighValue2 && shinThighValue < (shinThighValue4 - shinThighValue3) * (shinThighValue2 - shinThighLimit2) / (shinThighLimit3 - shinThighLimit2) + shinThighValue3) {
            shinThighFlag = !shinThighFlag;
         }
      }

      return shinThighFlag;
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr) {
      int shinThighDivisor = 8;
      float shinThighFactor = (float) (Math.PI / 2) / shinThighDivisor;

      for (int i = -1; i <= 1; i += 2) {
         for (int j = 0; j < shinThighDivisor; j++) {
            float shinThighValue = j * shinThighFactor + (j == 0 ? 0.6F : 0.1F) * shinThighFactor;
            float shinThighValue2 = (j + 1) * shinThighFactor - 0.1F * shinThighFactor;
            float angle = 0.5F * (shinThighValue + shinThighValue2);
            float[] shinThighValues = getAmounts(shinThighValue, i, 0.985F, 0.07F);
            float[] amounts2 = getAmounts(shinThighValue2, i, 0.985F, 0.07F);
            float[] amounts3 = getAmounts(angle, i, 1.0F, 0.07F);
            float[] amounts4 = getAmounts(amounts3[2]);
            float shinThighValue3 = 0.5F * (amounts4[0] + amounts4[1]);
            float shinThighFactor2 = amounts3[0];
            float shinThighFactor3 = amounts3[1] - shinThighValue3;
            float distance = (float)Math.sqrt(shinThighFactor2 * shinThighFactor2 + shinThighFactor3 * shinThighFactor3);
            float shinThighValue4 = 1.15F - 0.5F * angle / (float) (Math.PI / 2);
            float[] floats = new float[]{amounts3[0] + shinThighFactor2 / distance * 0.12F, amounts3[1] - shinThighValue4, amounts3[2] - 0.05F};
            float shinThighValue5 = -0.5F * (float)Math.cos(angle);
            handle(latheLoft, shinThighValues, amounts2, floats, shinThighFactor2 / distance, shinThighFactor3 / distance, shinThighValue5, shinBrr);
         }
      }
   }

   @Override
   public String formatTungDin() {
      return "tralalero_tralala";
   }

   private static float[] getAmounts(float shinThighValue, float angle, float shinThighFactor) {
      float[] shinThighValues = getAmounts(shinThighValue);
      float shinThighValue2 = 0.5F * (shinThighValues[0] + shinThighValues[1]);
      float shinThighFactor2 = 0.5F * (shinThighValues[0] - shinThighValues[1]);
      return new float[]{-shinThighValues[2] * shinThighFactor * (float)Math.sin(angle), shinThighValue2 + shinThighFactor2 * shinThighFactor * (float)Math.cos(angle), shinThighValue};
   }

   private static void handle(LatheLoft latheLoft, ForeEar foreEar, ForeEar foreEar2, ForeEar foreEar3, float shinThighValue, float shinThighValue2, float armTrunkValue, ShinBrr[] shinBrrs) {
      latheLoft.getLatheLoftForForeEar(foreEar);
      float shinThighValue4 = armTrunkValue - 0.3F;
      float latheLoftValue = shinThighValue * 1.1F;
      float shinThighValue6 = shinThighValue2 - 1.7F;
      float shinThighValue7 = shinThighValue * 0.8F;
      float shinThighValue8 = shinThighValue7 + (latheLoftValue - shinThighValue7) * (shinThighValue6 - 10.6F) / (shinThighValue6 - 7.2F);
      float shinThighValue9 = shinThighValue7 + (latheLoftValue - shinThighValue7) * (shinThighValue6 - 9.0F) / (shinThighValue6 - 7.2F);
      float shinThighValue10 = shinThighValue7 + (latheLoftValue - shinThighValue7) * (shinThighValue6 - 7.2F - 0.8F) / (shinThighValue6 - 7.2F);
      latheLoft.getLoftPath(
         new float[]{
            shinThighValue * 0.72F,
            shinThighValue2 + 1.3F,
            armTrunkValue + 0.2F,
            shinThighValue7,
            shinThighValue6,
            armTrunkValue + 0.05F,
            shinThighValue8,
            10.6F,
            armTrunkValue - 0.05F,
            shinThighValue9,
            9.0F,
            armTrunkValue - 0.18F,
            shinThighValue10,
            8.0F,
            armTrunkValue - 0.27F,
            latheLoftValue,
            7.2F,
            shinThighValue4
         },
         new float[]{1.2F, 1.7F, 1.62F, 1.42F, 1.25F, 1.18F},
         new float[]{1.6F, 2.0F, 1.85F, 1.6F, 1.4F, 1.28F},
         10,
         shinBrrs[0],
         null,
         null,
         0.0F,
         0.0F,
         -1.0F
      );
      latheLoft.getLatheLoft(latheLoftValue, 7.2F, shinThighValue4 + 0.12F, 1.2F, 1.22F, 1.28F, 8, shinBrrs[1]);
      latheLoft.getLatheLoftForForeEar(foreEar2);
      latheLoft.getLoftPath(
         new float[]{
            latheLoftValue,
            7.6F,
            shinThighValue4,
            latheLoftValue,
            6.3999996F,
            armTrunkValue + 0.15F,
            latheLoftValue,
            5.3999996F,
            armTrunkValue + 0.5F,
            latheLoftValue,
            4.2999997F,
            armTrunkValue + 0.15F,
            latheLoftValue,
            2.8500001F,
            armTrunkValue,
            latheLoftValue,
            1.7F,
            armTrunkValue + 0.1F
         },
         new float[]{1.18F, 1.3F, 1.45F, 1.0F, 0.6F, 0.6F},
         new float[]{1.28F, 1.55F, 1.9F, 1.15F, 0.62F, 0.64F},
         10,
         shinBrrs[1],
         null,
         null,
         0.0F,
         0.0F,
         -1.0F
      );
      latheLoft.getLatheLoftForForeEar(foreEar3);
      handleAmounts(latheLoft, latheLoftValue, armTrunkValue, shinBrrs);
   }

   @Override
   public float getAmountFloat() {
      return 1.26F;
   }

   private static float getAmountForFloat(float shinThighLimit) {
      if (!(shinThighLimit < -19.7F) && !(shinThighLimit > -12.800001F)) {
         float shinThighBound = 1.0F - (shinThighLimit - -19.7F) / 6.9F;
         float acos = (float)Math.acos(Math.max(-1.0F, Math.min(1.0F, shinThighBound)));
         return 1.36F * acos / (float) (Math.PI / 2);
      } else {
         return -1.0F;
      }
   }

   @Override
   public String formatNoteBlock() {
      return "block.note_block.flute";
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int bonecaAmbalabuLimit = bonecaAmbalabu.ambalabuBonecaValue - 1;
      int bonecaAmbalabuLimit2 = bonecaAmbalabu.bonecaValue - 1;

      for (int i = 0; i <= bonecaAmbalabuLimit2; i++) {
         float bonecaAmbalabuValue = (float)i / bonecaAmbalabuLimit2;
         int bonecaAmbalabuValue2 = bonecaAmbalabuValue < 0.7F
            ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(16777215), ForeShinUtils.getValueForInt(16053492), bonecaAmbalabuValue / 0.7F)
            : (bonecaAmbalabuValue < 0.76F ? ForeShinUtils.getValueForInt(12173511) : ForeShinUtils.getValueForInt(10134443));

         for (int j = 0; j <= bonecaAmbalabuLimit; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.02F, 5.0F, 33);
   }

   private static int getValue(float shinThighValue, float angle, boolean shinThighFlag) {
      float cos = (float)Math.cos(angle);
      float shinThighValue2 = angle < 0.0F ? 1.0F : -1.0F;
      float shinThighValue3 = getAmount(shinThighValue, shinThighValue2);
      int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(7311278), ForeShinUtils.getValueForInt(5666458), getAmount3((cos - 0.3F) / 0.65F));
      int foreShinValue = ForeShinUtils.getValue2(value2, ForeShinUtils.getValueForInt(9349571), 0.45F * getAmount3(1.0F - (cos - shinThighValue3) / 0.3F));
      int valueForInt = ForeShinUtils.getValueForInt(15659507);
      int shinThighValue7 = ForeShinUtils.getValue2(foreShinValue, valueForInt, getAmount3((shinThighValue3 - cos) / 0.08F + 0.5F));
      float shinThighValue8 = (float) Math.PI - Math.abs(angle);
      float shinThighLimit = getAmountForFloat(shinThighValue);
      if (shinThighFlag && shinThighLimit > 0.14F) {
         if (shinThighValue8 < shinThighLimit - 0.17F) {
            shinThighValue7 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(7218224), ForeShinUtils.getValueForInt(4855328), getAmount3((shinThighLimit - 0.17F - shinThighValue8) / 0.5F));
         } else if (shinThighValue8 < shinThighLimit) {
            shinThighValue7 = ForeShinUtils.getValueForInt(12737130);
         } else if (shinThighValue8 < shinThighLimit + 0.07F) {
            shinThighValue7 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(4941956), shinThighValue7, getAmount3((shinThighValue8 - shinThighLimit) / 0.07F));
         }
      }

      return shinThighValue7;
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 14.65F, -12.9F).getLatheLoftLatheLoftAmountLoft(-12.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 3.15F, 1.7F, 6.75F, 18, 0.5F, 1.0F, shinBrr);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, -0.03F, 0.0F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 3.0555003F, 6.5475F, 14, shinBrr2);
      latheLoft.getDequeArray();
      latheLoft.getLoftLathe(0.0F, -0.25F, -2.025F, 2.2680001F, 0.42F, 3.915F, 10, 0.0F, 0.5F, shinBrr3);
      int shinThighDivisor = 6;
      float shinThighDivisor2 = 1.4451327F;
      float shinThighFactor = shinThighDivisor2 / shinThighDivisor;

      for (int i = -1; i <= 1; i += 2) {
         for (int j = 0; j < shinThighDivisor; j++) {
            float angle = i * (j * shinThighFactor + 0.1F * shinThighFactor);
            float angle2 = i * ((j + 1) * shinThighFactor - 0.1F * shinThighFactor);
            float planValuesItem = i * (j + 0.5F) * shinThighFactor;
            float shinThighValue = 0.9F - 0.4F * Math.abs(planValuesItem) / shinThighDivisor2;
            float[] floats = new float[]{2.9295F * (float)Math.sin(angle), -0.05F, -6.2775F * (float)Math.cos(angle)};
            float[] floats2 = new float[]{2.9295F * (float)Math.sin(angle2), -0.05F, -6.2775F * (float)Math.cos(angle2)};
            float[] floats3 = new float[]{2.9925F * (float)Math.sin(planValuesItem), shinThighValue, -6.4125F * (float)Math.cos(planValuesItem)};
            handle(latheLoft, floats, floats2, floats3, (float)Math.sin(planValuesItem), 0.0F, -((float)Math.cos(planValuesItem)), shinBrr4);
         }
      }

      latheLoft.getDequeArray();
   }

   private static float getAmount(float shinThighValue, float shinThighValue2) {
      float shinThighValue3 = -0.13F + 0.029999994F * getAmount3((shinThighValue + 16.0F) / 8.0F);
      float shinThighValue4 = shinThighValue3 + 0.16F * getAmount3((shinThighValue - 4.0F) / 14.0F);
      float shinThighFactor = 0.045F * (float)Math.sin(shinThighValue * 0.75F + shinThighValue2 * 0.9F) + 0.012F * (float)Math.sin(shinThighValue * 2.2F + 1.7F + shinThighValue2 * 2.1F);
      return shinThighValue4 + shinThighFactor * getAmount3((shinThighValue + 19.0F) / 4.0F);
   }

   private static void handleAmounts(LatheLoft latheLoft, float shinThighValue, float armTrunkValue, ShinBrr[] shinBrrs) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinThighValue, 0.0F, armTrunkValue).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getPolygonExtrude(OUTLINE_POLYGON, -0.8F, 0.0F, shinBrrs[4], shinBrrs[5], shinBrrs[3]);
      latheLoft.getDequeArray();
      int shinThighLimit = SPINE_Z.length;
      float[] floats = new float[3 * shinThighLimit];

      for (int i = 0; i < shinThighLimit; i++) {
         floats[3 * i] = shinThighValue;
         floats[3 * i + 1] = SPINE_Y[i];
         floats[3 * i + 2] = armTrunkValue + SPINE_Z[i];
      }

      ShinBrr shinBrr = latheLoft.getMax(2780352);
      latheLoft.getLoftPath(floats, LOFT_RADII_X, shinThighTable, 10, shinBrrs[2], shinBrr, shinBrr, 0.0F, 1.0F, 0.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinThighValue, 2.94F, armTrunkValue + 0.12F);
      latheLoft.getLatheProfile(new float[]{0.6F, 1.02F, 0.64F}, new float[]{-0.3F, 0.02F, 0.32F}, 8, 1.0F, 1.18F, shinBrrs[6], null, null);
      latheLoft.getDequeArray();
   }

   private static float getAmount2(float shinThighLimit, float shinThighLimit2, float shinThighValue) {
      return shinThighLimit < shinThighLimit2 ? shinThighLimit2 : (shinThighLimit > shinThighValue ? shinThighValue : shinThighLimit);
   }

   private static void handleAmountsBoneca(LatheLoft latheLoft) {
      float[] floats = new float[]{0.0F, 0.17F, 0.36F, 0.55F, 0.72F, 0.87F, 1.0F};
      int bonecaSize = floats.length;
      float[] loftPathValues = new float[3 * bonecaSize];
      float[] crownForeValues = new float[bonecaSize];
      float[] floats4 = new float[bonecaSize];

      for (int i = 0; i < bonecaSize; i++) {
         float[] bonecaValues = getAmounts(PROFILE_POINTS, floats[i]);
         float[] amounts2 = getAmounts(bonecaTable, floats[i]);
         loftPathValues[3 * i + 1] = 0.5F * (bonecaValues[1] + amounts2[1]);
         loftPathValues[3 * i + 2] = 0.5F * (bonecaValues[0] + amounts2[0]);
         floats4[i] = Math.max(0.03F, 0.5F * (float)Math.hypot(amounts2[0] - bonecaValues[0], amounts2[1] - bonecaValues[1]));
         crownForeValues[i] = 0.04F + 0.82F * (float)Math.pow(1.0F - floats[i], 1.15F);
      }

      LoftLatheFlag.Region region = latheLoft.getMax(48, 64);
      BonecaAmbalabu classBonecaAmbalabu = region.getClassBonecaAmbalabu();
      int bonecaLimit = classBonecaAmbalabu.ambalabuBonecaValue - 1;
      int bonecaLimit2 = classBonecaAmbalabu.bonecaValue - 1;

      for (int j = 0; j <= bonecaLimit2; j++) {
         float bonecaValue = (float)j / bonecaLimit2;

         for (int k = 0; k <= bonecaLimit; k++) {
            float abs2 = Math.abs((float)k / bonecaLimit - 0.5F);
            int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(8166072), ForeShinUtils.getValueForInt(6982056), getAmount3(bonecaValue * 1.4F));
            int bonecaAmbalabuValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(5599887), getAmount3((abs2 - 0.33F) / 0.07F));
            classBonecaAmbalabu.getBonecaAmbalabu(k, j, bonecaAmbalabuValue);
         }
      }

      classBonecaAmbalabu.getBonecaAmbalabu(0.04F, 8.0F, 23);
      latheLoft.getLoftPath(loftPathValues, crownForeValues, floats4, 8, latheLoft.getClassForRegion(region), null, null, 0.0F, 0.0F, -1.0F);
   }

   @Override
   public int getValue() {
      return 3;
   }

   private static void handleAmounts(LatheLoft latheLoft, int shinThighLimit, ShinBrr[] shinBrrs, ShinBrr shinBrr) {
      float[] shinThighValues = getAmounts(-10.8F, -shinThighLimit * 1.9915928F, 0.96F);
      float[] floats = new float[shinFootTable.length];

      for (int i = 0; i < floats.length; i += 2) {
         floats[i] = shinFootTable[i] * shinThighLimit;
         floats[i + 1] = shinFootTable[i + 1];
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinThighValues[0], shinThighValues[1], shinThighValues[2]).getLatheLoft(-shinThighLimit * 62.0F).getLatheLoft4(shinThighLimit * 18.0F).getLatheLoftLatheLoftAmountLoft(80.0F);
      latheLoft.getPolygonExtrude(floats, -0.14F, 0.14F, shinBrrs[shinThighLimit > 0 ? 0 : 1], shinBrrs[shinThighLimit > 0 ? 3 : 2], shinBrr);
      latheLoft.getDequeArray();
   }

   static {
      int shinThighLimit = table.length / 4;
      float[] floats = new float[3 * shinThighLimit];
      float[] pointsSplineValues = new float[3 * shinThighLimit];

      for (int i = 0; i < shinThighLimit; i++) {
         floats[3 * i] = table[4 * i];
         floats[3 * i + 1] = table[4 * i + 1];
         floats[3 * i + 2] = table[4 * i + 2];
         pointsSplineValues[3 * i] = table[4 * i];
         pointsSplineValues[3 * i + 1] = table[4 * i + 3];
      }

      float[] pointsSpline = LatheLoft.getPointsSpline(16, floats);
      float[] pointsSpline2 = LatheLoft.getPointsSpline(16, pointsSplineValues);
      int shinThighSize = LatheLoft.getValue(pointsSpline);
      SPINE_ARC_LENGTHS = new float[shinThighSize];
      SPLINE_Y = new float[shinThighSize];
      SPLINE_Z = new float[shinThighSize];
      SPLINE_RADII = new float[shinThighSize];
      float shinThighValue = -1.0E9F;

      for (int j = 0; j < shinThighSize; j++) {
         shinThighValue = Math.max(shinThighValue + 1.0E-4F, pointsSpline[3 * j]);
         SPINE_ARC_LENGTHS[j] = shinThighValue;
         SPLINE_Y[j] = pointsSpline[3 * j + 1];
         SPLINE_Z[j] = pointsSpline[3 * j + 2];
         SPLINE_RADII[j] = Math.max(0.02F, pointsSpline2[3 * j + 1]);
      }

      PROFILE_POINTS = new float[]{-4.8F, 20.1F, -4.7F, 20.6F, -3.1F, 22.85F, -0.8F, 25.1F, 1.4F, 26.65F, 3.3F, 27.2F};
      bonecaTable = new float[]{4.9F, 20.1F, 4.9F, 20.5F, 3.4F, 21.6F, 2.5F, 23.4F, 2.6F, 25.5F, 3.3F, 27.2F};
      shinFootTable = new float[]{0.0F, -1.44F, 2.04F, 0.06F, 4.08F, 2.1F, 2.04F, 1.58F, 0.0F, 1.5F};
      OUTLINE_POLYGON = new float[]{
         1.2F,
         1.3F,
         1.48F,
         -0.6F,
         1.5F,
         -2.9F,
         1.2F,
         -4.5F,
         0.95F,
         -4.85F,
         0.45F,
         -5.18F,
         -0.45F,
         -5.18F,
         -0.95F,
         -4.85F,
         -1.2F,
         -4.5F,
         -1.5F,
         -2.9F,
         -1.48F,
         -0.6F,
         -1.2F,
         1.3F,
         -0.9F,
         1.75F,
         -0.45F,
         2.05F,
         0.45F,
         2.05F,
         0.9F,
         1.75F
      };
      SPINE_Z = new float[]{1.88F, 1.4F, 0.45F, -0.85F, -2.15F, -3.3F, -4.2F, -4.82F};
      SPINE_Y = new float[]{1.6F, 1.75F, 1.76F, 1.58F, 1.34F, 1.18F, 1.08F, 1.0F};
      shinThighTable = new float[]{0.75F, 1.42F, 1.52F, 1.36F, 1.14F, 1.0F, 0.85F, 0.6F};
      LOFT_RADII_X = new float[]{0.42F, 1.0F, 1.22F, 1.3F, 1.4F, 1.37F, 1.19F, 0.8F};
      bonecaAmbalabuTable = new float[]{
         -3.35F,
         0.62F,
         -3.62F,
         0.5F,
         -3.76F,
         0.25F,
         -3.68F,
         0.02F,
         -3.4F,
         -0.06F,
         -2.9F,
         -0.1F,
         -2.2F,
         -0.1F,
         -1.4F,
         -0.04F,
         -0.6F,
         0.07F,
         0.2F,
         0.22F,
         0.9F,
         0.45F,
         1.5F,
         0.85F,
         0.75F,
         0.64F,
         -0.2F,
         0.54F,
         -1.1F,
         0.57F,
         -1.9F,
         0.56F,
         -2.4F,
         0.5F,
         -2.7F,
         0.38F,
         -2.95F,
         0.32F,
         -3.15F,
         0.44F
      };
   }

   @Override
   public String formatTungDinString() {
      return "Tralalero Tralala";
   }

   private static float[] getAmounts(float shinThighLimit) {
      int shinThighValue = SPINE_ARC_LENGTHS.length;
      if (shinThighLimit <= SPINE_ARC_LENGTHS[0]) {
         return new float[]{SPLINE_Y[0], SPLINE_Z[0], SPLINE_RADII[0]};
      } else if (shinThighLimit >= SPINE_ARC_LENGTHS[shinThighValue - 1]) {
         return new float[]{SPLINE_Y[shinThighValue - 1], SPLINE_Z[shinThighValue - 1], SPLINE_RADII[shinThighValue - 1]};
      } else {
         int index = 0;
         int index2 = shinThighValue - 1;

         while (index2 - index > 1) {
            int index3 = index + index2 >>> 1;
            if (SPINE_ARC_LENGTHS[index3] <= shinThighLimit) {
               index = index3;
            } else {
               index2 = index3;
            }
         }

         float shinThighFactor = (shinThighLimit - SPINE_ARC_LENGTHS[index]) / (SPINE_ARC_LENGTHS[index2] - SPINE_ARC_LENGTHS[index]);
         return new float[]{
            SPLINE_Y[index] + (SPLINE_Y[index2] - SPLINE_Y[index]) * shinThighFactor,
            SPLINE_Z[index] + (SPLINE_Z[index2] - SPLINE_Z[index]) * shinThighFactor,
            SPLINE_RADII[index] + (SPLINE_RADII[index2] - SPLINE_RADII[index]) * shinThighFactor
         };
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag) {
      int bonecaAmbalabuLimit = bonecaAmbalabu.ambalabuBonecaValue - 1;
      int bonecaAmbalabuLimit2 = bonecaAmbalabu.bonecaValue - 1;

      for (int i = 0; i <= bonecaAmbalabuLimit2; i++) {
         float bonecaAmbalabuValue = (float)i / bonecaAmbalabuLimit2;

         for (int j = 0; j <= bonecaAmbalabuLimit; j++) {
            float abs2 = Math.abs((float)j / bonecaAmbalabuLimit - 0.5F);
            int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(8033970), ForeShinUtils.getValueForInt(13226972), 0.45F * getAmount3((abs2 - 0.34F) / 0.12F));
            float bonecaAmbalabuValue4 = 1.0F;
            float foreShinFactor;
            if (bonecaAmbalabuFlag) {
               float bonecaAmbalabuValue6 = bonecaAmbalabuValue4 + 0.12F * getAmount3(1.0F - abs2 / 0.13F) * getAmount3(bonecaAmbalabuValue / 0.2F) * getAmount3((0.85F - bonecaAmbalabuValue) / 0.15F);
               float abs7 = Math.abs(abs2 - (0.17F - 0.06F * bonecaAmbalabuValue));
               float bonecaAmbalabuValue8 = bonecaAmbalabuValue6 - 0.16F * getAmount3(1.0F - abs7 / 0.03F) * getAmount3((bonecaAmbalabuValue - 0.12F) / 0.15F) * getAmount3((0.9F - bonecaAmbalabuValue) / 0.1F);
               float bonecaAmbalabuFactor = abs2 / 0.12F;
               float bonecaAmbalabuFactor2 = (bonecaAmbalabuValue - 0.93F) / 0.07F;
               float bonecaAmbalabuValue9 = bonecaAmbalabuValue8 + 0.12F * getAmount3(1.0F - (bonecaAmbalabuFactor * bonecaAmbalabuFactor + bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2));
               foreShinFactor = bonecaAmbalabuValue9 - 0.1F * getAmount3(1.0F - bonecaAmbalabuValue / 0.12F);
            } else {
               float bonecaAmbalabuValue10 = bonecaAmbalabuValue4 + 0.1F * getAmount3(1.0F - abs2 / 0.07F) * getAmount3((bonecaAmbalabuValue - 0.1F) / 0.2F);
               float abs11 = Math.abs(abs2 - 0.28F);
               float bonecaAmbalabuValue12 = bonecaAmbalabuValue10 - 0.16F * getAmount3(1.0F - abs11 / 0.035F) * getAmount3((bonecaAmbalabuValue - 0.08F) / 0.1F) * getAmount3((0.62F - bonecaAmbalabuValue) / 0.15F);
               foreShinFactor = bonecaAmbalabuValue12 - 0.07F * getAmount3((bonecaAmbalabuValue - 0.82F) / 0.18F);
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(foreShinValue, foreShinFactor));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 8.0F, bonecaAmbalabuFlag ? 51 : 53).getBonecaAmbalabu(0.02F, 2.0F, 52);
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      int bonecaAmbalabuLimit = bonecaAmbalabu.ambalabuBonecaValue - 1;
      int bonecaAmbalabuLimit2 = bonecaAmbalabu.bonecaValue - 1;

      for (int i = 0; i <= bonecaAmbalabuLimit2; i++) {
         float bonecaAmbalabuValue = (float)i / bonecaAmbalabuLimit2;

         for (int j = 0; j <= bonecaAmbalabuLimit; j++) {
            float bonecaAmbalabuValue2 = (float)j / bonecaAmbalabuLimit;
            float abs3 = Math.abs(bonecaAmbalabuValue2 - 0.5F);
            int valueForInt = ForeShinUtils.getValueForInt(15659507);
            if (abs3 > 0.17F && bonecaAmbalabuValue < 0.5F) {
               valueForInt = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(9349571), valueForInt, getAmount3((bonecaAmbalabuValue - 0.3F) / 0.2F));
            }

            if (bonecaAmbalabuValue < 0.16F) {
               valueForInt = ForeShinUtils.getValueForInt(12737130);
            } else if (bonecaAmbalabuValue < 0.22F) {
               valueForInt = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(4941956), valueForInt, getAmount3((bonecaAmbalabuValue - 0.16F) / 0.06F));
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, valueForInt);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 6.0F, 41);
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.thighB = latheLoft.getClassForStringForeEar("hips", latheLoft.getClassForeEarList(), 0.0F, 16.3F, -1.0F);
      this.dorsal = latheLoft.getClassForStringForeEar("trunk", this.thighB, 0.0F, 16.3F, -1.0F);
      this.jaw = latheLoft.getClassForStringForeEar("head", this.dorsal, 0.0F, 16.5F, -11.7F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("jaw", this.jaw, 0.0F, 14.65F, -12.9F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("dorsal", this.dorsal, 0.0F, 21.0F, -0.5F);
      this.tail2 = latheLoft.getClassForStringForeEar("tail1", this.dorsal, 0.0F, 16.4F, 6.3F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("tail2", this.tail2, 0.0F, 16.55F, 12.3F);
      this.shinR = latheLoft.getClassForStringForeEar("thigh_r", this.thighB, 3.0F, 14.3F, -7.8F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 3.3000002F, 7.2F, -8.05F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("foot_r", this.footR, 3.3000002F, 2.4F, -7.8F);
      this.shinL = latheLoft.getClassForStringForeEar("thigh_l", this.thighB, -3.0F, 14.3F, -7.8F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -3.3000002F, 7.2F, -8.05F);
      this.shinThighForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -3.3000002F, 2.4F, -7.8F);
      this.shinB = latheLoft.getClassForStringForeEar("thigh_b", this.thighB, 0.0F, 13.9F, 9.0F);
      this.footB = latheLoft.getClassForStringForeEar("shin_b", this.shinB, 0.0F, 7.2F, 8.75F);
      this.foreEar = latheLoft.getClassForStringForeEar("foot_b", this.footB, 0.0F, 2.4F, 9.0F);
      latheLoft.getLatheLoft(this.shinR);
      ShinThigh.Seg seg = new ShinThigh.Seg(
         1.0F, 0.92F, -22.75F, -22.63F, -22.38F, -21.95F, -21.25F, -20.25F, -19.0F, -17.55F, -15.95F, -14.3F, -13.2F, -12.4F, -11.4F
      );
      ShinThigh.Seg seg2 = new ShinThigh.Seg(0.92F, 0.92F, -12.9F, -11.4F, -8.4F, -6.0F, -3.0F, 0.0F, 3.0F, 5.6F, 7.6F);
      ShinThigh.Seg seg3 = new ShinThigh.Seg(0.92F, 0.92F, 5.0F, 6.4F, 8.0F, 9.6F, 11.2F, 12.8F, 13.6F);
      ShinThigh.Seg seg4 = new ShinThigh.Seg(0.92F, 1.0F, 11.0F, 12.6F, 14.0F, 15.4F, 16.8F, 18.2F, 19.4F, 20.4F);
      LoftLatheFlag.Region region = latheLoft.getMax(232, 168);
      handleBonecaAmbalabu(region.getClassBonecaAmbalabu(), seg, true);
      LoftLatheFlag.Region max2 = latheLoft.getMax(232, 176);
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu(), seg2, false);
      LoftLatheFlag.Region max3 = latheLoft.getMax(160, 72);
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu(), seg3, false);
      LoftLatheFlag.Region max4 = latheLoft.getMax(224, 128);
      handleBonecaAmbalabu(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(96, 64);
      handleBonecaAmbalabu3(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu(max6.getClassBonecaAmbalabu(), seg4, false);
      LoftLatheFlag.Region max7 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu(max7.getClassBonecaAmbalabu(), true);
      LoftLatheFlag.Region max8 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu(max8.getClassBonecaAmbalabu(), false);
      LoftLatheFlag.Region max9 = latheLoft.getMax(64, 16);
      handleBonecaAmbalabuForBonecaAmbalabu(max9.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max10 = latheLoft.getMax(64, 64);
      max10.getClassBonecaAmbalabu().getBonecaAmbalabu(ForeShinUtils.getValueForInt(6982056), ForeShinUtils.getValueForInt(8166072)).getBonecaAmbalabu(0.04F, 8.0F, 21);
      ShinBrr classForRegion = latheLoft.getClassForRegion(max10);
      ShinBrr shinBrr = latheLoft.getMax(6456227);
      latheLoft.getLatheLoftForForeEar(this.jaw);
      seg.handleAmounts(latheLoft, latheLoft.getClassForRegion(region), 20, null);
      this.handle(latheLoft);
      handle(latheLoft, latheLoft.getMax(16250350));
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      handle(latheLoft, latheLoft.getClassForRegion(max5), latheLoft.getMax(7218224), latheLoft.getMax(8006714), latheLoft.getMax(16250350));
      latheLoft.getLatheLoftForForeEar(this.dorsal);
      seg2.handleAmounts(latheLoft, latheLoft.getClassForRegion(max2), 14, null);
      ShinBrr[] bonecaAmbalabu = getBonecaAmbalabu(latheLoft, shinFootTable, 2, 4, false, 0.0F, 4.08F, 7311278, 5666458);
      ShinBrr[] bonecaAmbalabu2 = getBonecaAmbalabu(latheLoft, shinFootTable, 2, 4, false, 0.0F, 4.08F, 15659507, 13226973);
      ShinBrr[] shinBrrs = new ShinBrr[]{bonecaAmbalabu[0], bonecaAmbalabu[1], bonecaAmbalabu2[0], bonecaAmbalabu2[1]};

      for (int i = -1; i <= 1; i += 2) {
         handleAmounts(latheLoft, i, shinBrrs, shinBrr);
      }

      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handleAmountsBoneca(latheLoft);
      latheLoft.getLatheLoftForForeEar(this.tail2);
      seg3.handleAmounts(latheLoft, latheLoft.getClassForRegion(max3), 10, null);
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max6);
      seg4.handleAmounts(latheLoft, classForRegion3, 8, classForRegion3);
      float[] floats = new float[]{
         17.0F,
         17.96F,
         19.2F,
         20.6F,
         22.1F,
         24.08F,
         23.8F,
         25.5F,
         24.8F,
         26.0F,
         23.9F,
         23.5F,
         22.5F,
         19.7F,
         21.9F,
         17.3F,
         22.4F,
         14.9F,
         23.6F,
         11.6F,
         21.6F,
         12.85F,
         18.9F,
         14.96F,
         17.0F,
         16.04F
      };
      float[] bonecaAmbalabuValues = new float[]{17.0F, 17.9F, 19.1F, 20.35F, 20.9F, 17.4F, 19.0F, 14.9F};
      ShinBrr[] bonecaAmbalabu3 = getBonecaAmbalabu(latheLoft, floats, 4, 9, false, 17.0F, 24.8F, 8166072, 6982056);
      ShinBrr[] bonecaAmbalabu4 = getBonecaAmbalabu(latheLoft, bonecaAmbalabuValues, -1, -1, false, 17.0F, 24.8F, 8166072, 6982056);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft4(-90.0F);
      latheLoft.getPolygonExtrude(floats, -0.3F, 0.3F, bonecaAmbalabu3[0], bonecaAmbalabu3[1], shinBrr);
      latheLoft.getPolygonExtrude(bonecaAmbalabuValues, -0.36F, 0.36F, bonecaAmbalabu4[0], bonecaAmbalabu4[1], shinBrr);
      latheLoft.getPolygonExtrude(new float[]{11.9F, 18.4F, 12.9F, 19.85F, 13.4F, 19.95F, 13.7F, 18.2F}, 0.0F, 0.0F, classForRegion, null, null);
      latheLoft.getPolygonExtrude(new float[]{12.1F, 14.85F, 13.3F, 13.3F, 13.8F, 13.15F, 13.9F, 14.95F}, 0.0F, 0.0F, classForRegion, null, null);
      latheLoft.getDequeArray();
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max8);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max9);
      ShinBrr shinBrr2 = latheLoft.getMax(16053492);
      ShinBrr shinBrr3 = latheLoft.getMax(8291982);
      ShinBrr shinBrr4 = latheLoft.getMax(2780352);
      ShinBrr[] shinBrrs2 = new ShinBrr[]{classForRegion4, classForRegion5, classForRegion6, classForRegion7, shinBrr2, shinBrr3, shinBrr4};
      handle(latheLoft, this.shinR, this.footR, this.classShinFoot, 3.0F, 14.3F, -7.8F, shinBrrs2);
      handle(latheLoft, this.shinL, this.footL, this.shinThighForeEar, -3.0F, 14.3F, -7.8F, shinBrrs2);
      handle(latheLoft, this.shinB, this.footB, this.foreEar, 0.0F, 13.9F, 9.0F, shinBrrs2);
   }

   private static float[] getAmounts(float[] shinThighValues, float shinThighFactor) {
      float shinThighTotal = 0.0F;

      for (int i = 2; i < shinThighValues.length; i += 2) {
         shinThighTotal += (float)Math.hypot(shinThighValues[i] - shinThighValues[i - 2], shinThighValues[i + 1] - shinThighValues[i - 1]);
      }

      float shinThighTotal2 = shinThighFactor * shinThighTotal;

      for (int j = 2; j < shinThighValues.length; j += 2) {
         float hypot = (float)Math.hypot(shinThighValues[j] - shinThighValues[j - 2], shinThighValues[j + 1] - shinThighValues[j - 1]);
         if (shinThighTotal2 <= hypot || j == shinThighValues.length - 2) {
            float shinThighFactor2 = hypot > 0.0F ? Math.min(1.0F, shinThighTotal2 / hypot) : 0.0F;
            return new float[]{shinThighValues[j - 2] + (shinThighValues[j] - shinThighValues[j - 2]) * shinThighFactor2, shinThighValues[j - 1] + (shinThighValues[j + 1] - shinThighValues[j - 1]) * shinThighFactor2};
         }

         shinThighTotal2 -= hypot;
      }

      return new float[]{shinThighValues[shinThighValues.length - 2], shinThighValues[shinThighValues.length - 1]};
   }

   private static float[] getAmountsForFloat(float shinThighBound) {
      int shinThighSize = SPINE_Z.length;
      float[] floats = new float[shinThighSize];

      for (int i = 1; i < shinThighSize; i++) {
         float shinThighFactor = SPINE_Y[i] - SPINE_Y[i - 1];
         float shinThighFactor2 = SPINE_Z[i] - SPINE_Z[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(shinThighFactor * shinThighFactor + shinThighFactor2 * shinThighFactor2);
      }

      float shinThighLimit = Math.max(0.0F, Math.min(1.0F, shinThighBound)) * floats[shinThighSize - 1];
      int index = 0;

      while (index < shinThighSize - 2 && shinThighLimit > floats[index + 1]) {
         index++;
      }

      float shinThighFactor3 = (shinThighLimit - floats[index]) / (floats[index + 1] - floats[index]);
      return new float[]{
         SPINE_Z[index] + (SPINE_Z[index + 1] - SPINE_Z[index]) * shinThighFactor3,
         SPINE_Y[index] + (SPINE_Y[index + 1] - SPINE_Y[index]) * shinThighFactor3,
         shinThighTable[index] + (shinThighTable[index + 1] - shinThighTable[index]) * shinThighFactor3
      };
   }

   private void handle(LatheLoft latheLoft) {
      LoftLatheFlag.Region region = latheLoft.getMax(64, 32);
      BonecaAmbalabu classBonecaAmbalabu = region.getClassBonecaAmbalabu();
      classBonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(1711394), ForeShinUtils.getValueForInt(855309));
      classBonecaAmbalabu.getClass022(32.0F, 16.0F, 15.0F, 12.0F, ForeShinUtils.getValueForIntFloat(0, 0.85F));
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr shinBrr = latheLoft.getMax(16777215);

      for (int i = -1; i <= 1; i += 2) {
         float shinThighValue = -i * 1.3615928F;
         float[] shinThighValues = getAmounts(-13.2F, shinThighValue, 0.94F);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinThighValues[0], shinThighValues[1], shinThighValues[2]).getLatheLoft4(-i * 74.0F).getLatheLoftLatheLoftAmountLoft(6.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.68F, 0.64F, 0.24F, 12, classForRegion);
         latheLoft.getLatheLoft(-0.16F * i, 0.24F, -0.2F, 0.12F, 6, shinBrr);
         latheLoft.getDequeArray();
      }
   }

   private static float getAmount3(float shinThighFactor) {
      shinThighFactor = shinThighFactor < 0.0F ? 0.0F : (shinThighFactor > 1.0F ? 1.0F : shinThighFactor);
      return shinThighFactor * shinThighFactor * (3.0F - 2.0F * shinThighFactor);
   }

   private static ShinBrr[] getBonecaAmbalabu(LatheLoft latheLoft, float[] bonecaAmbalabuValues, int bonecaAmbalabuLimit, int bonecaAmbalabuValue, boolean bonecaAmbalabuFlag, float bonecaAmbalabuValue2, float bonecaAmbalabuValue3, int foreShinValue, int bonecaAmbalabuValue5) {
      float bonecaAmbalabuBound = Float.MAX_VALUE;
      float maxValue2 = -Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;

      for (int i = 0; i < bonecaAmbalabuValues.length; i += 2) {
         bonecaAmbalabuBound = Math.min(bonecaAmbalabuBound, bonecaAmbalabuValues[i]);
         maxValue2 = Math.max(maxValue2, bonecaAmbalabuValues[i]);
         maxValue = Math.min(maxValue, bonecaAmbalabuValues[i + 1]);
         pilotStatusBound = Math.max(pilotStatusBound, bonecaAmbalabuValues[i + 1]);
      }

      float deltaVar = maxValue2 - bonecaAmbalabuBound;
      float deltaVar2 = pilotStatusBound - maxValue;
      ShinBrr[] shinBrrs = new ShinBrr[2];

      for (int j = 0; j < 2; j++) {
         LoftLatheFlag.Region region = latheLoft.getMax(48, 48);
         BonecaAmbalabu classBonecaAmbalabu = region.getClassBonecaAmbalabu();
         int bonecaAmbalabuDivisor = classBonecaAmbalabu.ambalabuBonecaValue - 1;
         int bonecaAmbalabuLimit2 = classBonecaAmbalabu.bonecaValue - 1;

         for (int k = 0; k <= bonecaAmbalabuLimit2; k++) {
            float bonecaAmbalabuValue6 = pilotStatusBound - (float)k / bonecaAmbalabuLimit2 * deltaVar2;

            for (int m = 0; m <= bonecaAmbalabuDivisor; m++) {
               float bonecaAmbalabuValue7 = j == 0 ? maxValue2 - (float)m / bonecaAmbalabuDivisor * deltaVar : bonecaAmbalabuBound + (float)m / bonecaAmbalabuDivisor * deltaVar;
               float shinThighFactor = ((bonecaAmbalabuFlag ? bonecaAmbalabuValue6 : bonecaAmbalabuValue7) - bonecaAmbalabuValue2) / (bonecaAmbalabuValue3 - bonecaAmbalabuValue2);
               classBonecaAmbalabu.getBonecaAmbalabu(m, k, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(foreShinValue), ForeShinUtils.getValueForInt(bonecaAmbalabuValue5), getAmount3(shinThighFactor)));
            }
         }

         classBonecaAmbalabu.getBonecaAmbalabu(0.04F, 8.0F, 21 + j);
         if (bonecaAmbalabuLimit >= 0) {
            int bonecaAmbalabuSize = bonecaAmbalabuValue - bonecaAmbalabuLimit + 1;
            float[] floats = new float[bonecaAmbalabuSize];
            float[] floats2 = new float[bonecaAmbalabuSize];

            for (int p = 0; p < bonecaAmbalabuSize; p++) {
               float bonecaAmbalabuValue9 = bonecaAmbalabuValues[2 * (bonecaAmbalabuLimit + p)];
               float bonecaAmbalabuValue10 = bonecaAmbalabuValues[2 * (bonecaAmbalabuLimit + p) + 1];
               float bonecaAmbalabuFactor = j == 0 ? (maxValue2 - bonecaAmbalabuValue9) / deltaVar : (bonecaAmbalabuValue9 - bonecaAmbalabuBound) / deltaVar;
               floats[p] = 0.5F + bonecaAmbalabuFactor * bonecaAmbalabuDivisor;
               floats2[p] = 0.5F + (pilotStatusBound - bonecaAmbalabuValue10) / deltaVar2 * bonecaAmbalabuLimit2;
            }

            classBonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 4.2F, ForeShinUtils.getValueForInt(5599887));
         }

         shinBrrs[j] = latheLoft.getClassForRegion(region);
      }

      return shinBrrs;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, ShinThigh.Seg seg, boolean bonecaAmbalabuFlag) {
      int bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue - 1;
      int bonecaAmbalabuFactor2 = bonecaAmbalabu.bonecaValue - 1;

      for (int i = 0; i <= bonecaAmbalabuFactor2; i++) {
         float bonecaAmbalabuValue = seg.getAmountsForFloat((float)i / bonecaAmbalabuFactor2);

         for (int j = 0; j <= bonecaAmbalabuFactor; j++) {
            float angle = (float) (Math.PI * 2) * ((float)j / bonecaAmbalabuFactor - 0.5F);
            bonecaAmbalabu.getBonecaAmbalabu(j, i, getValue(bonecaAmbalabuValue, angle, bonecaAmbalabuFlag));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 10.0F, 5).getBonecaAmbalabu(0.02F, 3.0F, 6);

      for (int k = -1; k <= 1 && !bonecaAmbalabuFlag; k += 2) {
         for (int m = 0; m < 5; m++) {
            float bonecaAmbalabuValue3 = -10.8F + m * 0.95F;
            float bonecaAmbalabuValue4 = 0.34F - m * 0.03F;
            float bonecaAmbalabuValue5 = -0.42F + m * 0.03F;
            float[] floats = new float[9];
            float[] floats2 = new float[9];
            float[] floats3 = new float[9];
            float[] floats4 = new float[9];

            for (int p = 0; p < 9; p++) {
               float bonecaAmbalabuFactor3 = p / 8.0F;
               float bonecaAmbalabuValue6 = bonecaAmbalabuValue4 + (bonecaAmbalabuValue5 - bonecaAmbalabuValue4) * bonecaAmbalabuFactor3;
               float segLimit = bonecaAmbalabuValue3 - 0.3F * (float)Math.sin((float) Math.PI * bonecaAmbalabuFactor3);
               float bonecaAmbalabuValue8 = -k * (float)Math.acos(bonecaAmbalabuValue6);
               floats[p] = (0.5F + bonecaAmbalabuValue8 / (float) (Math.PI * 2)) * bonecaAmbalabuFactor;
               floats2[p] = seg.getAmounts(segLimit) * bonecaAmbalabuFactor2;
               floats3[p] = floats[p];
               floats4[p] = seg.getAmounts(segLimit + 0.17F) * bonecaAmbalabuFactor2;
            }

            bonecaAmbalabu.getClassForFloatArrayFloat(floats3, floats4, 1.4F, ForeShinUtils.getValueForIntFloat(9349571, 0.55F));
            bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 2.0F, ForeShinUtils.getValueForIntFloat(4152440, 0.92F));
         }
      }

      if (bonecaAmbalabuFlag) {
         for (int q = -1; q <= 1; q += 2) {
            float bonecaAmbalabuValue9 = -q * 1.3615928F;
            float bonecaAmbalabuValue10 = (0.5F + bonecaAmbalabuValue9 / (float) (Math.PI * 2)) * bonecaAmbalabuFactor;
            float amountForFloat = seg.getAmounts(-13.2F) * bonecaAmbalabuFactor2;
            bonecaAmbalabu.getClass022(bonecaAmbalabuValue10, amountForFloat, 9.0F, 7.0F, ForeShinUtils.getValueForIntFloat(5666458, 0.55F));
            bonecaAmbalabu.getClass022(bonecaAmbalabuValue10, amountForFloat, 6.5F, 5.0F, ForeShinUtils.getValueForIntFloat(3163484, 0.8F));
            float bonecaAmbalabuValue12 = -q * 2.3615928F;
            float classValue = (0.5F + bonecaAmbalabuValue12 / (float) (Math.PI * 2)) * bonecaAmbalabuFactor;
            float bonecaAmbalabuValue14 = seg.getAmounts(-21.7F) * bonecaAmbalabuFactor2;
            bonecaAmbalabu.getClass022(classValue, bonecaAmbalabuValue14, 1.5F, 0.8F, ForeShinUtils.getValueForIntFloat(5925498, 0.3F));
            float[] classValues = new float[6];
            float[] floats6 = new float[6];

            for (int r = 0; r < 6; r++) {
               float bonecaAmbalabuFactor4 = r / 5.0F;
               float bonecaAmbalabuValue15 = -12.900002F + 0.9F * bonecaAmbalabuFactor4;
               float bonecaAmbalabuValue16 = 1.38F + 0.4F * bonecaAmbalabuFactor4 * bonecaAmbalabuFactor4;
               float bonecaAmbalabuValue17 = -q * ((float) Math.PI - bonecaAmbalabuValue16);
               classValues[r] = (0.5F + bonecaAmbalabuValue17 / (float) (Math.PI * 2)) * bonecaAmbalabuFactor;
               floats6[r] = seg.getAmounts(bonecaAmbalabuValue15) * bonecaAmbalabuFactor2;
            }

            bonecaAmbalabu.getClassForFloatArrayFloat(classValues, floats6, 1.8F, ForeShinUtils.getValueForIntFloat(4941956, 0.85F));
         }
      }
   }

   private static final class Seg {
      final float[] segTable;
      final float[] table;
      final float[] pathPoints;
      final float[] radii;
      final float[] keyPositions;

      float getAmounts(float segLimit) {
         int segValue = this.keyPositions.length;
         int index = 0;

         while (index < segValue - 2 && segLimit > this.keyPositions[index + 1]) {
            index++;
         }

         return this.table[index]
            + (this.table[index + 1] - this.table[index]) * (segLimit - this.keyPositions[index]) / (this.keyPositions[index + 1] - this.keyPositions[index]);
      }

      void handleAmounts(LatheLoft latheLoft, ShinBrr shinBrr, int segValue, ShinBrr shinBrr2) {
         latheLoft.getLoftPath(this.pathPoints, this.radii, this.segTable, segValue, shinBrr, null, shinBrr2, 0.0F, 1.0F, 0.0F);
      }

      Seg(float segValue, float segValue2, float... amounts) {
         this.keyPositions = amounts;
         int segSize = amounts.length;
         this.pathPoints = new float[3 * segSize];
         this.radii = new float[segSize];
         this.segTable = new float[segSize];
         this.table = new float[segSize];

         for (int i = 0; i < segSize; i++) {
            float[] segValues = ShinThigh.getAmounts(amounts[i]);
            float segFactor = i == 0 ? segValue : (i == segSize - 1 ? segValue2 : 1.0F);
            this.pathPoints[3 * i + 1] = 0.5F * (segValues[0] + segValues[1]);
            this.pathPoints[3 * i + 2] = amounts[i];
            this.radii[i] = segValues[2] * segFactor;
            this.segTable[i] = 0.5F * (segValues[0] - segValues[1]) * segFactor;
         }

         float[] floats = new float[segSize];

         for (int j = 1; j < segSize; j++) {
            float segFactor2 = this.pathPoints[3 * j + 1] - this.pathPoints[3 * j - 2];
            float segFactor3 = amounts[j] - amounts[j - 1];
            floats[j] = floats[j - 1] + (float)Math.sqrt(segFactor2 * segFactor2 + segFactor3 * segFactor3);
         }

         for (int k = 0; k < segSize; k++) {
            this.table[k] = floats[k] / floats[segSize - 1];
         }
      }

      float getAmountsForFloat(float segLimit) {
         int segValue = this.keyPositions.length;
         int index = 0;

         while (index < segValue - 2 && segLimit > this.table[index + 1]) {
            index++;
         }

         return this.keyPositions[index]
            + (this.keyPositions[index + 1] - this.keyPositions[index]) * (segLimit - this.table[index]) / (this.table[index + 1] - this.table[index]);
      }
   }
}
