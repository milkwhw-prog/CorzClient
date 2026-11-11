package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ShinBrr;

public final class AmbalabuBoneca extends WarmBrainrotBase {
   private ForeEar shinR;
   private static final float[] footShinAmounts = new float[]{3.6F, 3.6F, 4.6F, 5.6F, 6.4F, 6.77F, 6.85F, 6.85F, 6.77F, 6.4F, 5.6F, 4.6F, 3.6F};
   private ForeEar classShinFoot;
   private ForeEar classShinProgress;
   private ForeEar footR;
   private static final float[] shinFootTable = new float[]{
      2.25F, -2.25F, -3.0F, -3.1000001F, -3.0F, -2.6000001F, -2.0500002F, 2.0500002F, 2.6000001F, 3.0F, 3.1000001F, 3.0F, 2.25F
   };
   private ForeEar shinFootForeEar;
   private ForeEar shinProgressForeEar;
   private ForeEar footL;
   private ForeEar ambalabuBonecaForeEar;
   private ForeEar shinL;
   private ForeEar head;
   private static final String[] ambalabuBonecaTexts = new String[]{
      "B11110100011000111110100011000111110",
      "O01110100011000110001100011000101110",
      "N10001110011010110011100011000110001",
      "E11111100001000011110100001000011111",
      "C01111100001000010000100001000001111",
      "A01110100011000111111100011000110001",
      "M10001110111010110101100011000110001",
      "L10000100001000010000100001000011111",
      "U10001100011000110001100011000101110"
   };
   private ForeEar legL;
   private ForeEar foreEar;
   private ForeEar eyeL;
   private ForeEar tongue;
   private ForeEar eyeLeftSection;

   private static float[] getAmounts(float ambalabuBonecaValue, float ambalabuBonecaFactor, int bonecaAmbalabuValue) {
      float angle = (float) (Math.PI * 2) * (ambalabuBonecaValue - 0.5F);
      float angle2 = (float) Math.PI * ambalabuBonecaFactor;
      float sin = (float)Math.sin(angle2);
      float[] floats = new float[]{-sin * (float)Math.sin(angle) * 1.95F, (float)Math.cos(angle2) * 1.95F, -sin * (float)Math.cos(angle) * 1.95F};
      handle(floats, 0, 4.0F);
      handle(floats, 1, -bonecaAmbalabuValue * 26.0F);
      floats[0] += bonecaAmbalabuValue * 3.45F;
      floats[1] += 30.8F;
      floats[2] += -1.35F;
      return floats;
   }

   @Override
   public float getAmountFloat() {
      return 0.7F;
   }

   private static float getAmount(float[] ambalabuBonecaValues, int ambalabuBonecaValue) {
      float[] floats = new float[]{ambalabuBonecaValues[0] - ambalabuBonecaValue * 3.45F, ambalabuBonecaValues[1] - 30.8F, ambalabuBonecaValues[2] - -1.35F};
      handle(floats, 1, ambalabuBonecaValue * 26.0F);
      handle(floats, 0, -30.0F);
      handle(floats, 2, ambalabuBonecaValue * 6.0F);
      float distance = (float)Math.sqrt(floats[0] * floats[0] + floats[1] * floats[1] + floats[2] * floats[2]);
      return (float)Math.acos(Math.max(-1.0F, Math.min(1.0F, floats[1] / distance))) / (float) Math.PI;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14933926)).getBonecaAmbalabu(ForeShinUtils.getValueForInt(13618060), 0.2F, 6.0F, 18);

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue2 = (j + 0.5F) / ambalabuBonecaValue;
            float cos = (float)Math.cos((float) (Math.PI * 2) * (bonecaAmbalabuValue2 - 0.5F));
            float bonecaAmbalabuFactor = Math.max(getAmount((cos + 0.1F) / 0.5F) * getAmount((bonecaAmbalabuValue - 0.1F) / 0.18F), getAmount((bonecaAmbalabuValue - 0.6F) / 0.2F));
            float foreShinFactor = bonecaAmbalabuFactor * getAmount((bonecaAmbalabuValue - 0.035F) / 0.04F);
            int foreShinValue = ForeShinUtils.getValue2(
               ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(8830546), ForeShinUtils.getValueForInt(7909450), getAmount(bonecaAmbalabuValue / 0.4F)),
               ForeShinUtils.getValueForInt(6729280),
               getAmount(-cos)
            );
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue2(foreShinValue, bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), foreShinFactor));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 20.0F, 17);
      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.5F, bonecaValue * 0.3F, ambalabuBonecaValue * 0.16F, bonecaValue * 0.14F, ForeShinUtils.getValueForIntFloat(16184271, 0.3F));
      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.5F, bonecaValue * 0.27F, ambalabuBonecaValue * 0.09F, bonecaValue * 0.07F, ForeShinUtils.getValueForIntFloat(16447708, 0.25F));

      for (int k = 0; k < 18; k++) {
         float bonecaAmbalabuFactor2 = getAmount(k, 21);
         float cos2 = (float)Math.cos((float) (Math.PI * 2) * (bonecaAmbalabuFactor2 - 0.5F));
         if (!(cos2 > -0.2F)) {
            float bonecaAmbalabuFactor3 = 0.05F + 0.4F * getAmount(k, 22);
            bonecaAmbalabu.getClass02(bonecaAmbalabuFactor2 * ambalabuBonecaValue, bonecaAmbalabuFactor3 * bonecaValue, 2.0F + 3.0F * getAmount(k, 23), 0.35F, 70 + k, ForeShinUtils.getValueForIntFloat(4023078, 0.6F));
         }
      }

      float bonecaAmbalabuValue5 = 0.185F * ambalabuBonecaValue;
      float bonecaAmbalabuValue6 = 0.815F * ambalabuBonecaValue;
      handleBonecaAmbalabu(bonecaAmbalabu, 0.0F, 13.2F, 0.1F, 0.9F, 0.345F, 0.14F, ForeShinUtils.getValueForIntFloat(13951898, 0.55F));
      handleBonecaAmbalabu(bonecaAmbalabu, 0.0F, 13.2F, 0.0F, 1.0F, 0.0F, 0.55F, ForeShinUtils.getValueForInt(3095838));
      float bonecaAmbalabuValue7 = getAmountForFloat(1.0F) * 13.2F;
      bonecaAmbalabu.getClassForFloatInt(bonecaAmbalabuValue5 + 1.0F, bonecaAmbalabuValue7, bonecaAmbalabuValue5 - 1.5F, bonecaAmbalabuValue7 + 1.5F, bonecaAmbalabuValue5 - 2.5F, bonecaAmbalabuValue7 + 9.0F, 2.8F, ForeShinUtils.getValueForInt(3095838));
      bonecaAmbalabu.getClassForFloatInt(bonecaAmbalabuValue6 - 1.0F, bonecaAmbalabuValue7, bonecaAmbalabuValue6 + 1.5F, bonecaAmbalabuValue7 + 1.5F, bonecaAmbalabuValue6 + 2.5F, bonecaAmbalabuValue7 + 9.0F, 2.8F, ForeShinUtils.getValueForInt(3095838));
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float animationProgressScaled = poseProgress.getAnimationProgressForFloat(0.0F) * 0.7F;
      float shinProgressTotal = -animationProgressScaled;
      float shinProgressCounter = -poseProgress.getAnimationProgress((float) Math.PI) * 0.28F;
      float foreShinValue = -poseProgress.getAnimationProgress(0.0F) * 0.28F;
      float shinProgressValue = 0.0F;
      boolean shinProgressFlag = poseProgress.gliding || poseProgress.swimming;
      float shinProgressValue2 = 1.0F - getAmount(shinProgressFactor / 0.1F);
      if (!poseProgress.sneakingPose && !poseProgress.vehicle && !shinProgressFlag) {
         shinProgressTotal += 0.08F * shinProgressValue2;
         foreShinValue -= 0.18F * shinProgressValue2;
      }

      if (poseProgress.sneakingPose) {
         shinProgressValue = -0.2F;
         animationProgressScaled += 0.55F;
         shinProgressTotal += 0.55F;
         shinProgressCounter--;
         foreShinValue--;
      }

      if (poseProgress.vehicle) {
         animationProgressScaled = 1.4F;
         shinProgressTotal = 1.4F;
         shinProgressCounter = -0.6F;
         foreShinValue = -0.6F;
      }

      if (shinProgressFlag) {
         animationProgressScaled = 1.55F + 0.1F * poseProgress.getAgeCode(20.0F, 0.0F);
         shinProgressTotal = 1.55F + 0.1F * poseProgress.getAgeCode(20.0F, 1.5F);
         shinProgressCounter = -2.15F;
         foreShinValue = -2.15F;
      }

      float angle2 = shinProgressValue + animationProgressScaled;
      float angle3 = shinProgressValue + shinProgressTotal;
      float shinProgressFactor2 = shinProgressFlag ? 0.3F : (poseProgress.vehicle ? 0.5F : 0.85F);
      float shinProgressValue3 = 0.0F;
      if (!poseProgress.vehicle && !shinProgressFlag) {
         float shinProgressBound = 6.6F * (float)Math.cos(angle2) + 6.1499996F * (float)Math.cos(angle2 + shinProgressCounter) + getMax((angle2 + shinProgressCounter) * (1.0F - shinProgressFactor2));
         float rocketsElytraBound = 6.6F * (float)Math.cos(angle3) + 6.1499996F * (float)Math.cos(angle3 + foreShinValue) + getMax((angle3 + foreShinValue) * (1.0F - shinProgressFactor2));
         shinProgressValue3 = 12.75F + getMax(0.0F) - Math.max(shinProgressBound, rocketsElytraBound);
      }

      foreShinScale.getAmountsForeShin2(this.legL, 0.0F, -shinProgressValue3, 0.0F);
      foreShinScale.getAmounts(this.head, shinProgressValue, 0.0F, 0.0F);
      float shinProgressValue4 = poseProgress.vehicle ? 0.22F : (shinProgressFlag ? 0.0F : 0.05F * shinProgressFactor);
      foreShinScale.getAmounts(this.shinR, angle2, poseProgress.vehicle ? -0.12F : 0.0F, shinProgressValue4);
      foreShinScale.getAmounts(this.shinL, angle3, poseProgress.vehicle ? 0.12F : 0.0F, -shinProgressValue4);
      foreShinScale.getAmounts(this.footR, shinProgressCounter, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footL, foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classShinProgress, -(angle2 + shinProgressCounter) * shinProgressFactor2, -0.1F, 0.0F);
      foreShinScale.getAmounts(this.ambalabuBonecaForeEar, -(angle3 + foreShinValue) * shinProgressFactor2, 0.1F, 0.0F);
      foreShinScale.getAmounts(this.foreEar, 0.0F, 0.0F, (float)Math.sin(angle) * shinProgressFactor * 0.06F);
      if (shinProgressFlag) {
         foreShinScale.getAmounts(this.foreEar, 0.0F, 0.0F, -poseProgress.getRadians(28.0F));
      }

      foreShinScale.getAmountsForeShin2(this.eyeL, 0.0F, poseProgress.sneakingPose ? -0.9F : Math.min(shinProgressValue3 * 0.45F, 0.9F), 0.0F);
      float shinProgressValue5 = Math.max(-0.45F, Math.min(0.4F, poseProgress.shinProgressAmount)) * 0.8F;
      foreShinScale.getAmounts(this.eyeL, shinProgressValue5 - shinProgressValue, poseProgress.headYawRadians, 0.0F);
      float shinProgressDivisor = 1.0F;
      float shinProgressValue7 = (poseProgress.age + (poseProgress.hashCode & 127)) % 84.0F;
      if (shinProgressValue7 < 7.0F) {
         shinProgressDivisor = 1.0F - 0.8F * (float)Math.sin(shinProgressValue7 / 7.0F * Math.PI);
      }

      if (poseProgress.inPose) {
         shinProgressDivisor = 0.18F;
      }

      foreShinScale.getAmountsForForeEarFloat(this.shinProgressForeEar, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.eyeLeftSection, 1.0F, shinProgressDivisor, 1.0F);
      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, -0.15F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.eyeLeftSection, -0.15F, 0.0F, 0.0F);
      }

      if (poseProgress.swingProgress <= 0.0F && !poseProgress.poseProgressActive) {
         float shinProgressValue8 = (poseProgress.age + 96.0F + (poseProgress.hashCode & 0xFF) * 1.3F) % 130.0F;
         if (shinProgressValue8 < 24.0F) {
            float sin = (float)Math.sin(shinProgressValue8 / 12.0F * Math.PI);
            float shinProgressValue9 = sin * sin;
            foreShinScale.getAmountsForForeEarFloat(this.tongue, 1.0F + 0.05F * shinProgressValue9, 1.0F + 0.28F * shinProgressValue9, 1.0F + 0.03F * shinProgressValue9);
         }

         if (shinProgressFactor < 0.3F) {
            float shinProgressValue10 = (poseProgress.age + 150.0F + (poseProgress.hashCode & 511)) % 260.0F;
            if (shinProgressValue10 < 10.0F) {
               float shinProgressFactor3 = (float)Math.sin(shinProgressValue10 / 10.0F * Math.PI) * (1.0F - shinProgressFactor / 0.3F);
               foreShinScale.getAmounts(this.tongue, -0.1F * shinProgressFactor3, 0.0F, 0.0F);
               this.handle(foreShinScale, -0.04F * shinProgressFactor3, 1.6F * shinProgressFactor3, 0.0F);
               foreShinScale.getAmountsBoneCount(this.eyeL, 0.14F * shinProgressFactor3, 0.0F, 0.0F);
               foreShinScale.getAmountsForeShin2(this.eyeL, 0.0F, 0.5F * shinProgressFactor3, 0.0F);
            }
         }
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressFactor4 = 0.0F;
         float shinProgressFactor5;
         float shinProgressFactor6;
         if (swingProgress < 0.18F) {
            shinProgressFactor5 = getAmount(swingProgress / 0.18F);
            shinProgressFactor6 = getAmount((swingProgress - 0.04F) / 0.14F);
         } else if (swingProgress < 0.5F) {
            shinProgressFactor5 = 1.0F;
            shinProgressFactor6 = 1.0F;
            shinProgressFactor4 = -0.35F * (float)Math.sin(swingProgress * Math.PI * 3.0);
         } else {
            shinProgressFactor5 = 1.0F - getAmount((swingProgress - 0.5F) / 0.4F);
            shinProgressFactor6 = 1.0F - getAmount((swingProgress - 0.5F) / 0.15F);
         }

         foreShinScale.getAmounts(this.tongue, -0.32F * shinProgressFactor5, 0.0F, 0.0F);
         this.handle(foreShinScale, 0.04F * shinProgressFactor5 - 0.12F * (float)Math.sin(swingProgress * Math.PI * 2.0) * shinProgressFactor6, 4.0F * shinProgressFactor6, shinProgressFactor4 * shinProgressFactor6);
         foreShinScale.getAmountsBoneCount(this.eyeL, 0.32F * shinProgressFactor5, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.eyeL, 0.0F, 1.2F * shinProgressFactor5, 0.0F);
      }

      if (poseProgress.usingItem && poseProgress.swingProgress <= 0.0F) {
         float abs11 = Math.abs((float)Math.sin(poseProgress.age * 0.9F));
         foreShinScale.getAmounts(this.tongue, -0.06F - 0.08F * abs11, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.eyeL, 0.1F * abs11, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.eyeL, 0.0F, 0.4F * abs11, 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.foreEar, 1.08F, 0.92F, 1.08F);
         foreShinScale.getAmountsForeShin2(this.eyeL, 0.0F, -0.6F, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.eyeL, 1.05F, 0.95F, 1.05F);
      }
   }

   private static float getAmount(int ambalabuBonecaValue, int ambalabuBonecaValue2) {
      int ambalabuBonecaValue3 = ambalabuBonecaValue * 374761393 + ambalabuBonecaValue2 * 668265263 + 1013904223;
      int ambalabuBonecaValue4 = (ambalabuBonecaValue3 ^ ambalabuBonecaValue3 >>> 13) * 1274126177;
      int ambalabuBonecaValue5 = ambalabuBonecaValue4 ^ ambalabuBonecaValue4 >>> 16;
      return (ambalabuBonecaValue5 & 16777215) / 1.6777215E7F;
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.legL = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 14.2F, 0.0F);
      this.head = latheLoft.getClassForStringForeEar("torso", this.legL, 0.0F, 14.2F, 0.0F);
      this.foreEar = latheLoft.getClassForStringForeEar("tire", this.head, 0.0F, 19.6F, 0.0F);
      this.eyeL = latheLoft.getClassForStringForeEar("head", this.head, 0.0F, 24.9F, -0.099999994F);
      this.tongue = latheLoft.getClassForStringForeEar("jaw", this.eyeL, 0.0F, 26.9F, 3.4F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("tongue", this.tongue, 0.0F, 27.35F, 1.1F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("tongue_tip", this.tongue, 0.0F, 27.35F, 1.1F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("eye_r", this.eyeL, 3.35F, 29.9F, -0.099999994F);
      this.eyeLeftSection = latheLoft.getClassForStringForeEar("eye_l", this.eyeL, -3.35F, 29.9F, -0.099999994F);
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", this.legL, 3.5F, 14.2F, 0.0F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 3.4F, 7.6F, 0.0F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("foot_r", this.footR, 3.36F, 1.45F, 0.0F);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", this.legL, -3.5F, 14.2F, 0.0F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -3.4F, 7.6F, 0.0F);
      this.ambalabuBonecaForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -3.36F, 1.45F, 0.0F);
      float[] shinFootValues = getAmounts(footShinAmounts, shinFootTable);
      LoftLatheFlag.Region region = latheLoft.getMax(448, 224);
      handleAmbalabuBoneca(region.getClassBonecaAmbalabu(), shinFootValues);
      LoftLatheFlag.Region max2 = latheLoft.getMax(248, 72);
      handleBonecaAmbalabu7(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(248, 56);
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(128, 64);
      handleBonecaAmbalabuForBonecaAmbalabuInt(max4.getClassBonecaAmbalabu(), 1);
      LoftLatheFlag.Region max5 = latheLoft.getMax(128, 64);
      handleBonecaAmbalabuForBonecaAmbalabuInt(max5.getClassBonecaAmbalabu(), -1);
      LoftLatheFlag.Region max6 = latheLoft.getMax(112, 40);
      handleBonecaAmbalabu4(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(64, 24);
      handleBonecaAmbalabu5(max7.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max8 = latheLoft.getMax(48, 96);
      max8.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(14066298))
         .getBonecaAmbalabu(0.05F, 6.0F, 21)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(12157536), 0.2F, 4.0F, 22);
      handleBonecaAmbalabu6(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(48, 96);
      max9.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(14066298))
         .getBonecaAmbalabu(0.05F, 6.0F, 21)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(12157536), 0.2F, 4.0F, 22);
      handleBonecaAmbalabu(max8.getClassBonecaAmbalabu(), max9.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max10 = latheLoft.getMax(48, 48);
      LoftLatheFlag.Region max11 = latheLoft.getMax(40, 40);
      handleBonecaAmbalabu(max11.getClassBonecaAmbalabu(), 8010305);
      LoftLatheFlag.Region max12 = latheLoft.getMax(40, 40);
      handleBonecaAmbalabu(max12.getClassBonecaAmbalabu(), 9324112);
      handleBonecaAmbalabuForBonecaAmbalabu(max10.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max13 = latheLoft.getMax(32, 16);
      handleBonecaAmbalabu3(max13.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr shinBrr = latheLoft.getMax(2500134);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max10);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max6);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max11);
      ShinBrr classForRegion11 = latheLoft.getClassForRegion(max12);
      ShinBrr classForRegion12 = latheLoft.getClassForRegion(max13);
      ShinBrr shinBrr2 = latheLoft.getMax(12734302);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max8);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max9);
      ShinBrr shinBrr3 = latheLoft.getMax(13802354);
      ShinBrr shinBrr4 = latheLoft.getMax(14461568);
      ShinBrr shinBrr5 = latheLoft.getMax(15255472);
      latheLoft.getLatheLoftForForeEar(this.foreEar);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 19.6F, 0.0F).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLatheLoft(footShinAmounts, shinFootTable, 28, classForRegion, null, null);
      latheLoft.getDequeArray();

      for (int i = -1; i <= 1; i += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 19.6F, i * 2.6000001F).getLatheLoftLatheLoftAmountLoft(90.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 3.75F, 0.28F, 22, 5, shinBrr);
         latheLoft.getDequeArray();
      }

      latheLoft.getLoftLathe(0.0F, 19.6F, 0.4F, 3.6499999F, 3.6499999F, 22, classForRegion3);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 19.6F, 0.45F).getLatheLoft4(180.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 3.6499999F, 3.6499999F, 18, classForRegion3);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.eyeL);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 26.9F, -0.5F);
      handle(latheLoft, 0.18F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 6.3F, 3.7F, 5.2F, 30, 0.0F, 0.5F, classForRegion4);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 26.93F, -0.5F).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 6.26F, 5.16F, 24, classForRegion10);
      latheLoft.getDequeArray();

      for (int j = -1; j <= 1; j += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(j * 3.35F, 29.9F, -0.099999994F).getLatheLoft(-j * 14.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 2.5F, 1.4F, 2.5F, 14, 0.0F, 0.75F, classForRegion7);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.tongue);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 26.9F, -0.35F);
      handle(latheLoft, -0.3F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 6.2000003F, 2.7F, 5.1499996F, 24, 0.5F, 1.0F, classForRegion5);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 26.869999F, -0.35F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 6.1800003F, 5.1299996F, 24, classForRegion11);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      float[] pointsSpline = LatheLoft.getPointsSpline(2, 0.0F, 27.35F, 1.1F, 0.0F, 27.25F, -1.1F, 0.0F, 27.449999F, -3.5F);
      float[] crownForeValues = LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 1.0F, 1.25F, 1.55F);
      float[] amounts3 = LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 0.32F, 0.36F, 0.4F);
      latheLoft.getLoftPath(pointsSpline, crownForeValues, amounts3, 10, classForRegion12, null, null, 0.0F, 1.0F, 0.0F);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      latheLoft.getLatheLoft(0.0F, 27.4F, -3.1F, 2.1F, 0.8F, 1.6F, 12, shinBrr2);
      handle(latheLoft, this.shinProgressForeEar, 1, classForRegion8, classForRegion6);
      handle(latheLoft, this.eyeLeftSection, -1, classForRegion9, classForRegion6);
      handle(latheLoft, this.shinR, this.footR, this.classShinProgress, 1.0F, classForRegion13, classForRegion14, shinBrr3, shinBrr4, shinBrr5);
      handle(latheLoft, this.shinL, this.footL, this.ambalabuBonecaForeEar, -1.0F, classForRegion13, classForRegion14, shinBrr3, shinBrr4, shinBrr5);
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuDivisor = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      float bonecaAmbalabuDivisor2 = bonecaAmbalabu.bonecaValue * 0.5F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(657930));

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float hypot = (float)Math.hypot((j + 0.5F - bonecaAmbalabuDivisor) / bonecaAmbalabuDivisor, (i + 0.5F - bonecaAmbalabuDivisor2) / bonecaAmbalabuDivisor2);
            float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
            int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(1842204), ForeShinUtils.getValueForInt(3815994), getAmount((bonecaAmbalabuValue - 0.15F) / 0.75F));
            int foreShinValue = ForeShinUtils.getValue2(value2, ForeShinUtils.getValueForInt(2763306), 0.5F * (1.0F - getAmount(hypot / 0.5F)));
            int bonecaAmbalabuValue4 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(1315860), getAmount((hypot - 0.8F) / 0.12F));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue4);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.08F, 8.0F, 31);
   }

   private static void handle(
      LatheLoft latheLoft, ForeEar foreEar, ForeEar foreEar2, ForeEar foreEar3, float ambalabuBonecaFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4, ShinBrr shinBrr5
   ) {
      float toeScaled = ambalabuBonecaFactor * 3.5F;
      float latheLoftValue = ambalabuBonecaFactor * 3.4F;
      float pointsSplineValues = ambalabuBonecaFactor * 3.36F;
      latheLoft.getLatheLoftForForeEar(foreEar);
      float[] floats = new float[]{toeScaled, 15.099999F, 0.0F, toeScaled, 12.4F, -0.08F, toeScaled - ambalabuBonecaFactor * 0.05F, 10.0F, -0.12F, latheLoftValue, 7.6F, -0.02F};
      float[] floats2 = new float[]{1.7F, 1.62F, 1.35F, 1.16F};
      latheLoft.getLoftPath(floats, floats2, floats2, 10, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoftForForeEar(foreEar2);
      latheLoft.getLatheLoft(latheLoftValue, 7.6F, -0.02F, 1.1F, 0.95F, 1.1F, 10, shinBrr2);
      float[] loftPathValues = new float[]{
         latheLoftValue, 7.6F, -0.02F, latheLoftValue, 7.0499997F, 0.04F, latheLoftValue, 6.2F, 0.38F, latheLoftValue, 4.7F, 0.4F, pointsSplineValues, 3.1F, 0.08F, pointsSplineValues, 1.5500001F, 0.0F
      };
      float[] floats4 = new float[]{1.12F, 1.16F, 1.28F, 1.12F, 0.76F, 0.6F};
      latheLoft.getLoftPath(loftPathValues, floats4, floats4, 10, shinBrr2, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoftForForeEar(foreEar3);
      float[] pointsSpline = LatheLoft.getPointsSpline(
         1, pointsSplineValues, 0.72F, 0.95F, pointsSplineValues, 0.95F, 0.3F, pointsSplineValues + ambalabuBonecaFactor * 0.04F, 0.78F, -0.9F, pointsSplineValues + ambalabuBonecaFactor * 0.08F, 0.62F, -2.0F, pointsSplineValues + ambalabuBonecaFactor * 0.1F, 0.5F, -2.55F
      );
      float[] floats5 = new float[]{0.52F, 0.8F, 0.92F, 1.05F, 1.02F};
      float[] crownForeValues = new float[]{0.58F, 0.9F, 0.62F, 0.46F, 0.38F};
      latheLoft.getLoftPath(pointsSpline, floats5, crownForeValues, 10, shinBrr3, shinBrr3, shinBrr3, 0.0F, 1.0F, 0.0F);

      for (int i = 0; i < 5; i++) {
         float ambalabuBonecaValue = pointsSplineValues + ambalabuBonecaFactor * 0.1F - ambalabuBonecaFactor * (0.66F - i * 0.33F);
         float ambalabuBonecaValue2 = i == 0 ? 0.95F : 0.78F - i * 0.07F;
         float ambalabuBonecaValue3 = i == 0 ? 0.3F : 0.21F - i * 0.014F;
         float ambalabuBonecaValue4 = -2.3F - (i == 0 ? 0.0F : i * 0.08F);
         latheLoft.getLoftPath(
            new float[]{ambalabuBonecaValue, 0.4F, ambalabuBonecaValue4, ambalabuBonecaValue + ambalabuBonecaFactor * 0.02F, 0.34F, ambalabuBonecaValue4 - ambalabuBonecaValue2},
            new float[]{ambalabuBonecaValue3, ambalabuBonecaValue3 * 0.95F},
            new float[]{ambalabuBonecaValue3 * 0.9F, ambalabuBonecaValue3 * 0.82F},
            5,
            shinBrr4,
            null,
            shinBrr4,
            0.0F,
            1.0F,
            0.0F
         );
         if (i == 0) {
            latheLoft.getDequeArrayArrayDeque().getLatheLoft(ambalabuBonecaValue + ambalabuBonecaFactor * 0.02F, 0.34F + ambalabuBonecaValue3 * 0.8F, ambalabuBonecaValue4 - ambalabuBonecaValue2 + 0.2F).getLatheLoftLatheLoftAmountLoft(90.0F);
            latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, ambalabuBonecaValue3 * 0.7F, 0.22F, 8, shinBrr5);
            latheLoft.getDequeArray();
         }
      }
   }

   private static float getAmount(float ambalabuBonecaFactor) {
      ambalabuBonecaFactor = ambalabuBonecaFactor < 0.0F ? 0.0F : (ambalabuBonecaFactor > 1.0F ? 1.0F : ambalabuBonecaFactor);
      return ambalabuBonecaFactor * ambalabuBonecaFactor * (3.0F - 2.0F * ambalabuBonecaFactor);
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / ambalabuBonecaValue;
         float cos = (float)Math.cos((float) (Math.PI * 2) * (bonecaAmbalabuValue - 0.5F));
         float bonecaAmbalabuValue2 = 1.0F - Math.abs(cos);
         int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14185339), ForeShinUtils.getValueForInt(15241368), 0.7F * getAmount((bonecaAmbalabuValue2 - 0.4F) / 0.5F));
         if (cos < 0.0F) {
            foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(12734302), 0.35F * -cos);
         }

         bonecaAmbalabu.getClassBonecaAmbalabu(i, 0.0F, i + 1, bonecaValue, foreShinValue);
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.04F, 4.0F, 71);
      bonecaAmbalabu.getClassBonecaAmbalabu(ambalabuBonecaValue * 0.5F - 0.9F, 0.0F, ambalabuBonecaValue * 0.5F + 0.9F, bonecaValue, ForeShinUtils.getValueForIntFloat(12734302, 0.85F));
   }

   private static String formatTexts(char ch2) {
      for (String formatTexts : ambalabuBonecaTexts) {
         if (formatTexts.charAt(0) == ch2) {
            return formatTexts;
         }
      }

      return null;
   }

   private static float getMax(float angle) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float ambalabuBonecaBound = 1.31F * cos + 0.95F * sin;
      float pilotStatusBound = 1.4F * cos + 0.3F * sin;
      float rocketsElytraBound = 1.32F * cos - 3.3F * sin;
      return Math.max(ambalabuBonecaBound, Math.max(pilotStatusBound, rocketsElytraBound));
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaValue;
         int classValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(6267450), ForeShinUtils.getValueForInt(6729280), getAmount(bonecaAmbalabuValue));
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, ambalabuBonecaValue, i + 1, classValue);
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.08F, 12.0F, 81);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4023078), 0.25F, 5.0F, 82);

      for (int j = 0; j < 7; j++) {
         bonecaAmbalabu.getClass02(
            getAmount(j, 31) * ambalabuBonecaValue,
            bonecaValue * (0.45F + 0.4F * getAmount(j, 32)),
            1.6F + 2.0F * getAmount(j, 33),
            0.4F,
            120 + j,
            ForeShinUtils.getValueForIntFloat(3495711, 0.7F)
         );
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue) {
      float classValue = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      float amountForFloat = bonecaAmbalabu.bonecaValue * 0.5F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3095838));
      bonecaAmbalabu.getClass022(
         classValue,
         amountForFloat,
         classValue - 2.5F,
         amountForFloat - 2.5F,
         ForeShinUtils.getValueForInt(ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(bonecaAmbalabuValue), ForeShinUtils.getValueForInt(3095838), 0.45F))
      );
      bonecaAmbalabu.getClass022(classValue, amountForFloat, classValue - 5.0F, amountForFloat - 5.0F, ForeShinUtils.getValueForInt(bonecaAmbalabuValue));
      bonecaAmbalabu.getBonecaAmbalabu(0.06F, 6.0F, 61);
   }

   private static float getAmountForFloat(float ambalabuBonecaValue) {
      return -0.12F + 0.95F * getAmount((ambalabuBonecaValue - 0.1F) / 0.9F);
   }

   private static void handle(LatheLoft latheLoft, ForeEar foreEar, int ambalabuBonecaValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      latheLoft.getLatheLoftForForeEar(foreEar);
      float latheLoftValue = ambalabuBonecaValue * 3.45F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, 30.8F, -1.35F).getLatheLoft4(-ambalabuBonecaValue * 26.0F).getLatheLoftLatheLoftAmountLoft(4.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.95F, 18, shinBrr);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, 30.8F, -1.35F).getLatheLoft4(-ambalabuBonecaValue * 26.0F).getLatheLoftLatheLoftAmountLoft(30.0F).getLatheLoft(-ambalabuBonecaValue * 6.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 2.03775F, 2.03775F, 2.03775F, 18, 0.0F, 0.36F, shinBrr2);
      latheLoft.getDequeArray();
   }

   private static void handleAmbalabuBoneca(BonecaAmbalabu bonecaAmbalabu, float[] ambalabuBonecaValues) {
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(1907997)).getBonecaAmbalabu(0.12F, 60.0F, 5).getBonecaAmbalabu(0.06F, 8.0F, 6);
      float ambalabuBonecaValue2 = ambalabuBonecaValues[7] * bonecaValue;
      float ambalabuBonecaValue3 = ambalabuBonecaValues[6] * bonecaValue;
      float deltaVar = ambalabuBonecaValue3 - ambalabuBonecaValue2;
      float ambalabuBonecaValue4 = ambalabuBonecaValues[9] * bonecaValue;
      float ambalabuBonecaValue5 = ambalabuBonecaValues[4] * bonecaValue;
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, ambalabuBonecaValue4, ambalabuBonecaValue, ambalabuBonecaValue5, ForeShinUtils.getValueForInt(2500134));
      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 10.0F, 7);
      float[] floats = new float[]{ambalabuBonecaValue2 + deltaVar * 0.25F, ambalabuBonecaValue2 + deltaVar * 0.5F, ambalabuBonecaValue2 + deltaVar * 0.75F};
      float ambalabuBonecaFactor = ambalabuBonecaValue / 48.0F;

      for (int i = 0; i < 48; i++) {
         float ambalabuBonecaValue6 = (i + 0.5F) * ambalabuBonecaFactor;

         for (int j = 0; j < 4; j++) {
            float amountForFloat = j == 0 ? ambalabuBonecaValue2 + 0.8F : floats[j - 1] + 1.4F;
            float ambalabuBonecaValue8 = j == 3 ? ambalabuBonecaValue3 - 0.8F : floats[j] - 1.4F;
            float ambalabuBonecaValue9 = (j < 2 ? 1.0F : -1.0F) * ambalabuBonecaFactor * 0.42F;
            float ambalabuBonecaValue10 = (j & 1) * ambalabuBonecaFactor * 0.5F;

            for (int k = -1; k <= 1; k++) {
               float ambalabuBonecaValue11 = ambalabuBonecaValue6 + ambalabuBonecaValue10 + k * ambalabuBonecaValue;
               bonecaAmbalabu.getClass02(ambalabuBonecaValue11 - ambalabuBonecaValue9 * 0.5F, amountForFloat, ambalabuBonecaValue11 + ambalabuBonecaValue9 * 0.5F, ambalabuBonecaValue8, 0.8F, ForeShinUtils.getValueForInt(1447446));
            }
         }
      }

      float ambalabuBonecaFactor2 = ambalabuBonecaValue / 36.0F;

      for (int m = 0; m < 36; m++) {
         float ambalabuBonecaValue12 = (m + 0.25F) * ambalabuBonecaFactor2;

         for (int p = -1; p <= 1; p++) {
            float ambalabuBonecaValue13 = ambalabuBonecaValue12 + p * ambalabuBonecaValue;
            bonecaAmbalabu.getClassBonecaAmbalabu(ambalabuBonecaValue13 - 0.9F, ambalabuBonecaValue4 - 1.0F, ambalabuBonecaValue13 + 0.9F, ambalabuBonecaValue2 + deltaVar * 0.2F, ForeShinUtils.getValueForInt(1184274));
            bonecaAmbalabu.getClassBonecaAmbalabu(ambalabuBonecaValue13 + ambalabuBonecaFactor2 * 0.5F - 0.9F, ambalabuBonecaValue3 - deltaVar * 0.2F, ambalabuBonecaValue13 + ambalabuBonecaFactor2 * 0.5F + 0.9F, ambalabuBonecaValue5 + 1.0F, ForeShinUtils.getValueForInt(1184274));
         }
      }

      for (float floatsItem : floats) {
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, floatsItem - 0.6F, ambalabuBonecaValue, floatsItem + 0.6F, ForeShinUtils.getValueForIntFloat(1184274, 1.0F));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, ambalabuBonecaValue4 - 0.6F, ambalabuBonecaValue, ambalabuBonecaValue4 + 0.6F, ForeShinUtils.getValueForIntFloat(3421236, 0.6F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, ambalabuBonecaValue5 - 0.6F, ambalabuBonecaValue, ambalabuBonecaValue5 + 0.6F, ForeShinUtils.getValueForIntFloat(3421236, 0.6F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, ambalabuBonecaValues[1] * bonecaValue, bonecaAmbalabu.ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(921102));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, ambalabuBonecaValues[11] * bonecaValue, ForeShinUtils.getValueForInt(1052688));

      for (int q = 0; q < 2; q++) {
         boolean ambalabuBonecaFlag = q == 0;
         float ambalabuBonecaFactor3 = ambalabuBonecaFlag ? getShinFoot(ambalabuBonecaValues, 8, 12, 4.35F) : getShinFoot(ambalabuBonecaValues, 5, 1, 4.35F);
         float ambalabuBonecaFactor4 = ambalabuBonecaFlag ? getShinFoot(ambalabuBonecaValues, 8, 12, 6.15F) : getShinFoot(ambalabuBonecaValues, 5, 1, 6.15F);
         float ambalabuBonecaBound = ambalabuBonecaFlag ? getShinFoot(ambalabuBonecaValues, 8, 12, 4.7F) : getShinFoot(ambalabuBonecaValues, 5, 1, 4.7F);
         float hashHuntBound = ambalabuBonecaFlag ? getShinFoot(ambalabuBonecaValues, 8, 12, 5.95F) : getShinFoot(ambalabuBonecaValues, 5, 1, 5.95F);

         for (int r = 0; r < 8; r++) {
            float ambalabuBonecaFactor5 = r / 8.0F;
            float ambalabuBonecaValue15 = Math.min(ambalabuBonecaBound, hashHuntBound) * bonecaValue + Math.abs(hashHuntBound - ambalabuBonecaBound) * bonecaValue * ambalabuBonecaFactor5 * 0.5F;
            float ambalabuBonecaValue16 = Math.max(ambalabuBonecaBound, hashHuntBound) * bonecaValue - Math.abs(hashHuntBound - ambalabuBonecaBound) * bonecaValue * ambalabuBonecaFactor5 * 0.5F;
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, ambalabuBonecaValue15, bonecaAmbalabu.ambalabuBonecaValue, ambalabuBonecaValue16, ForeShinUtils.getValueForIntFloat(2894892, 0.1F));
         }

         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, ambalabuBonecaFactor3 * bonecaValue - 0.7F, bonecaAmbalabu.ambalabuBonecaValue, ambalabuBonecaFactor3 * bonecaValue + 0.7F, ForeShinUtils.getValueForInt(3026478));
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, ambalabuBonecaFactor4 * bonecaValue - 0.6F, bonecaAmbalabu.ambalabuBonecaValue, ambalabuBonecaFactor4 * bonecaValue + 0.6F, ForeShinUtils.getValueForInt(2763306));
         float wrapDegreesBound = ambalabuBonecaFlag ? getShinFoot(ambalabuBonecaValues, 8, 12, 4.8F) : getShinFoot(ambalabuBonecaValues, 5, 1, 4.8F);
         float ambalabuBonecaBound4 = ambalabuBonecaFlag ? getShinFoot(ambalabuBonecaValues, 8, 12, 5.75F) : getShinFoot(ambalabuBonecaValues, 5, 1, 5.75F);
         float ambalabuBonecaValue17 = Math.abs(ambalabuBonecaBound4 - wrapDegreesBound) * bonecaValue / 7.0F;
         float ambalabuBonecaValue18 = 0.13571428F * bonecaAmbalabu.ambalabuBonecaValue / 33.300884F;
         float ambalabuBonecaValue19 = Math.min(wrapDegreesBound, ambalabuBonecaBound4) * bonecaValue;

         for (int s = -1; s <= 1; s++) {
            handle(bonecaAmbalabu, "BONECA AMBALABU", s * bonecaAmbalabu.ambalabuBonecaValue, ambalabuBonecaValue19, ambalabuBonecaValue18, ambalabuBonecaValue17, ambalabuBonecaFlag, ForeShinUtils.getValueForInt(4868682));
         }

         handle(bonecaAmbalabu, "AMBALABU", bonecaAmbalabu.ambalabuBonecaValue * 0.5F, ambalabuBonecaValue19, ambalabuBonecaValue18, ambalabuBonecaValue17, !ambalabuBonecaFlag, ForeShinUtils.getValueForInt(3815994));
      }
   }

   private void handle(ForeShinScale foreShinScale, float angle, float ambalabuBonecaValue, float ambalabuBonecaValue2) {
      float ambalabuBonecaValue3 = 4.6F * ambalabuBonecaValue;
      float foreShinValue = angle + ambalabuBonecaValue2;
      foreShinScale.getAmounts(this.classShinFoot, angle, 0.0F, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F, 1.0F, 1.0F + ambalabuBonecaValue);
      foreShinScale.getAmounts(this.shinFootForeEar, foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmountsForeShin2(
         this.shinFootForeEar,
         0.0F,
         (float)Math.sin(angle) * (4.6F + ambalabuBonecaValue3) - (float)Math.sin(foreShinValue) * 4.6F,
         -((float)Math.cos(angle)) * (4.6F + ambalabuBonecaValue3) + (float)Math.cos(foreShinValue) * 4.6F
      );
   }

   private static void handle(LatheLoft latheLoft, float ambalabuBonecaValue) {
      double x = 1.0;
      double y = 0.5 * ambalabuBonecaValue;
      double distance = Math.sqrt(x * x + y * y);
      double abs2 = Math.abs(y);
      double angle = Math.atan2(y, 0.0);
      double atan2 = Math.atan2(y, x);
      latheLoft.getLatheLoftLatheLoftAmountLoft((float)Math.toDegrees(0.5 * (atan2 + angle)))
         .getLatheLoftForFloat(1.0F, (float)(distance + abs2), (float)(distance - abs2))
         .getLatheLoftLatheLoftAmountLoft((float)Math.toDegrees(0.5 * (atan2 - angle)));
   }

   @Override
   public String formatNoteBlock() {
      return "entity.frog.ambient";
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, String ambalabuBonecaText, float ambalabuBonecaValue, float ambalabuBonecaValue2, float ambalabuBonecaFactor, float ambalabuBonecaFactor2, boolean ambalabuBonecaFlag, int classValue) {
      float ambalabuBonecaValue4 = (ambalabuBonecaText.length() * 6 - 1) * ambalabuBonecaFactor;

      for (int i = 0; i < ambalabuBonecaText.length(); i++) {
         String formatTexts2 = formatTexts(ambalabuBonecaText.charAt(i));
         if (formatTexts2 != null) {
            for (int j = 0; j < 7; j++) {
               for (int k = 0; k < 5; k++) {
                  if (formatTexts2.charAt(1 + j * 5 + k) == '1') {
                     float classBound;
                     float ambalabuBonecaValue6;
                     if (ambalabuBonecaFlag) {
                        classBound = ambalabuBonecaValue + ambalabuBonecaValue4 * 0.5F - (i * 6 + k + 1) * ambalabuBonecaFactor;
                        ambalabuBonecaValue6 = ambalabuBonecaValue2 + (6 - j) * ambalabuBonecaFactor2;
                     } else {
                        classBound = ambalabuBonecaValue - ambalabuBonecaValue4 * 0.5F + (i * 6 + k) * ambalabuBonecaFactor;
                        ambalabuBonecaValue6 = ambalabuBonecaValue2 + j * ambalabuBonecaFactor2;
                     }

                     bonecaAmbalabu.getClassBonecaAmbalabu(classBound, ambalabuBonecaValue6, classBound + ambalabuBonecaFactor, ambalabuBonecaValue6 + ambalabuBonecaFactor2, classValue);
                  }
               }
            }
         }
      }
   }

   private static float getShinFoot(float[] shinFootValues, int shinFootValue, int index, float shinFootValue2) {
      int shinFootValue3 = index > shinFootValue ? 1 : -1;

      for (int i = shinFootValue; i != index; i += shinFootValue3) {
         float shinFootValue4 = footShinAmounts[i];
         float shinFootValue5 = footShinAmounts[i + shinFootValue3];
         if ((shinFootValue2 - shinFootValue4) * (shinFootValue2 - shinFootValue5) <= 0.0F && shinFootValue4 != shinFootValue5) {
            float shinFootFactor = (shinFootValue2 - shinFootValue4) / (shinFootValue5 - shinFootValue4);
            return shinFootValues[i] + (shinFootValues[i + shinFootValue3] - shinFootValues[i]) * shinFootFactor;
         }
      }

      return shinFootValues[index];
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(6267450), ForeShinUtils.getValueForInt(4946478));
      bonecaAmbalabu.getBonecaAmbalabu(0.08F, 8.0F, 91);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4023078), 0.15F, 4.0F, 92);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue - 4.0F, ambalabuBonecaValue, bonecaValue - 2.0F, ForeShinUtils.getValueForIntFloat(4023078, 0.45F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue - 2.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(4023078));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuFactor, float bonecaAmbalabuValue2, float bonecaAmbalabuValue3, float bonecaAmbalabuValue4, float bonecaAmbalabuFactor2, int valueForIntFloat) {
      float bonecaAmbalabuValue6 = 0.185F * bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaAmbalabuValue7 = 0.815F * bonecaAmbalabu.ambalabuBonecaValue;
      byte bonecaAmbalabuLimit = 48;
      float[] floats = new float[bonecaAmbalabuLimit + 1];
      float[] floats2 = new float[bonecaAmbalabuLimit + 1];

      for (int i = 0; i <= bonecaAmbalabuLimit; i++) {
         float bonecaAmbalabuFactor3 = bonecaAmbalabuValue2 + (bonecaAmbalabuValue3 - bonecaAmbalabuValue2) * i / bonecaAmbalabuLimit;
         floats[i] = bonecaAmbalabuValue6 + (bonecaAmbalabuValue7 - bonecaAmbalabuValue6) * bonecaAmbalabuFactor3;
         floats2[i] = bonecaAmbalabuValue + (getAmountForFloat(Math.abs(2.0F * bonecaAmbalabuFactor3 - 1.0F)) + bonecaAmbalabuValue4) * bonecaAmbalabuFactor;
      }

      bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, bonecaAmbalabuFactor2 * bonecaAmbalabuFactor, valueForIntFloat);
   }

   private static float[] getAmounts(float[] ambalabuBonecaValues, float[] ambalabuBonecaValues2) {
      float[] floats = new float[ambalabuBonecaValues.length];

      for (int i = 1; i < ambalabuBonecaValues.length; i++) {
         floats[i] = floats[i - 1] + (float)Math.hypot(ambalabuBonecaValues[i] - ambalabuBonecaValues[i - 1], ambalabuBonecaValues2[i] - ambalabuBonecaValues2[i - 1]);
      }

      float[] floats2 = new float[ambalabuBonecaValues.length];

      for (int j = 0; j < ambalabuBonecaValues.length; j++) {
         floats2[j] = 1.0F - floats[j] / floats[ambalabuBonecaValues.length - 1];
      }

      return floats2;
   }

   @Override
   public String formatTungDin() {
      return "boneca_ambalabu";
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabuInt(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11568426), ForeShinUtils.getValueForInt(14199870));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.36F, ambalabuBonecaValue, bonecaValue * 0.64F, ForeShinUtils.getValueForIntFloat(14199870, 0.45F));
      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.5F, bonecaValue * 0.66F, ambalabuBonecaValue * 0.3F, bonecaValue * 0.12F, ForeShinUtils.getValueForIntFloat(15781722, 0.7F));
      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 14.0F, 51);
      float bonecaAmbalabuValue2 = ambalabuBonecaValue * (0.5F + bonecaAmbalabuValue * 0.06F);
      float bonecaAmbalabuValue3 = bonecaValue * 0.5F;

      for (int i = 0; i < 70; i++) {
         float classFactor = getAmount(i, 41) * ambalabuBonecaValue;
         float bonecaAmbalabuValue5 = getAmount(i, 42) * bonecaValue;
         float angle = (float) (Math.PI * 2) * getAmount(i, 43);
         float bonecaAmbalabuFactor = 3.0F + 5.0F * getAmount(i, 44);
         float bonecaAmbalabuValue6 = classFactor + (float)Math.cos(angle) * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue7 = bonecaAmbalabuValue5 + (float)Math.sin(angle) * bonecaAmbalabuFactor * 0.6F;
         bonecaAmbalabu.getClassForFloatInt(classFactor, bonecaAmbalabuValue5, (classFactor + bonecaAmbalabuValue6) * 0.5F + 1.5F, (bonecaAmbalabuValue5 + bonecaAmbalabuValue7) * 0.5F - 1.0F, bonecaAmbalabuValue6, bonecaAmbalabuValue7, 0.6F, ForeShinUtils.getValueForIntFloat(4862472, 0.14F));
      }

      for (int j = 0; j < 26; j++) {
         float angle2 = (float) (Math.PI * 2) * j / 26.0F;
         float bonecaAmbalabuFactor2 = 12.5F;
         float bonecaAmbalabuFactor3 = 26.0F + 6.0F * getAmount(j, 45);
         bonecaAmbalabu.getClass02(
            bonecaAmbalabuValue2 + (float)Math.cos(angle2) * bonecaAmbalabuFactor2,
            bonecaAmbalabuValue3 + (float)Math.sin(angle2) * bonecaAmbalabuFactor2 * 0.6F,
            bonecaAmbalabuValue2 + (float)Math.cos(angle2) * bonecaAmbalabuFactor3,
            bonecaAmbalabuValue3 + (float)Math.sin(angle2) * bonecaAmbalabuFactor3 * 0.6F,
            0.7F,
            ForeShinUtils.getValueForIntFloat(7031826, 0.12F)
         );
      }

      bonecaAmbalabu.getClass022(bonecaAmbalabuValue2, bonecaAmbalabuValue3, 14.5F, 6.0F, ForeShinUtils.getValueForIntFloat(15912546, 0.55F));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue2, bonecaAmbalabuValue3, 11.8F, 4.2F, ForeShinUtils.getValueForInt(723723));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue2 - 3.0F, bonecaAmbalabuValue3 - 1.4F, 3.6F, 1.0F, ForeShinUtils.getValueForIntFloat(3815994, 0.5F));
      float classValue = bonecaAmbalabuValue2 - 7.0F;
      float amountForFloat = bonecaAmbalabuValue3;

      for (int k = 0; k < bonecaValue; k++) {
         for (int m = 0; m < ambalabuBonecaValue; m++) {
            float[] bonecaAmbalabuValues = getAmounts((m + 0.5F) / ambalabuBonecaValue, (k + 0.5F) / bonecaValue, bonecaAmbalabuValue);
            float bonecaAmbalabuValue10 = (getAmount(bonecaAmbalabuValues, bonecaAmbalabuValue) - 0.36F) * 180.0F;
            float amountForFloatArrayInt = getAmountForFloatArrayInt(bonecaAmbalabuValues, bonecaAmbalabuValue);
            float bonecaAmbalabuLimit = Math.max(0.42F * (1.0F - getAmount(bonecaAmbalabuValue10 / 18.0F)), 0.4F * (1.0F - getAmount(amountForFloatArrayInt / 0.6F)));
            int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(m, k);
            if (bonecaAmbalabuLimit > 0.0F) {
               bonecaAmbalabuForInt = ForeShinUtils.getValue2(bonecaAmbalabuForInt, ForeShinUtils.getValueForInt(5914124), bonecaAmbalabuLimit);
            }

            if (bonecaAmbalabuValue10 < 6.5F || amountForFloatArrayInt < 0.22F) {
               bonecaAmbalabuForInt = ForeShinUtils.getValueForInt(5914124);
            }

            if (bonecaAmbalabuValue10 < -5.0F) {
               bonecaAmbalabuForInt = ForeShinUtils.getValueForInt(4946478);
            } else if (amountForFloatArrayInt < -0.3F) {
               bonecaAmbalabuForInt = ForeShinUtils.getValueForInt(6729280);
            }

            bonecaAmbalabu.getBonecaAmbalabu(m, k, bonecaAmbalabuForInt);
            if (m == (int)classValue && bonecaAmbalabuValue10 < 13.0F) {
               amountForFloat = k + 1.5F;
            }
         }
      }

      bonecaAmbalabu.getClass022(classValue, amountForFloat, 2.0F, 1.3F, ForeShinUtils.getValueForInt(16777215));
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue * 0.22F; i++) {
         float bonecaAmbalabuValue = 1.0F - i / (bonecaAmbalabu.bonecaValue * 0.22F);
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(7226928, 0.55F * bonecaAmbalabuValue));
      }
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaValue;

         for (int j = 0; j < ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue2 = (j + 0.5F) / ambalabuBonecaValue;
            float cos = (float)Math.cos((float) (Math.PI * 2) * (bonecaAmbalabuValue2 - 0.5F));
            int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(6267450), ForeShinUtils.getValueForInt(6729280), getAmount(bonecaAmbalabuValue / 0.6F));
            int bonecaAmbalabuValue4 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(8830546), getAmount((bonecaAmbalabuValue - 0.55F) / 0.35F) * (0.75F + 0.25F * cos));
            if (cos > -0.55F) {
               bonecaAmbalabuValue4 = ForeShinUtils.getValue2(
                  bonecaAmbalabuValue4, ForeShinUtils.getValueForInt(11126892), 0.4F * getAmount((bonecaAmbalabuValue - 0.88F) / 0.07F) * getAmount((cos + 0.55F) / 0.3F)
               );
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue4);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.07F, 24.0F, 11);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4023078), 0.3F, 9.0F, 13);

      for (int k = 0; k < 36; k++) {
         float bonecaAmbalabuFactor = getAmount(k, 1);
         float bonecaAmbalabuFactor2 = 0.05F + 0.8F * getAmount(k, 2);
         float cos2 = (float)Math.cos((float) (Math.PI * 2) * (bonecaAmbalabuFactor - 0.5F));
         if (!(cos2 > 0.45F) || !(bonecaAmbalabuFactor2 > 0.38F)) {
            float classFactor = 2.4F + 4.4F * getAmount(k, 3);

            for (int m = -1; m <= 1; m++) {
               float classValue = bonecaAmbalabuFactor * ambalabuBonecaValue + m * ambalabuBonecaValue;
               bonecaAmbalabu.getClass02(classValue, bonecaAmbalabuFactor2 * bonecaValue, classFactor * 1.4F, 0.4F, 40 + k, ForeShinUtils.getValueForIntFloat(4023078, 0.28F));
               bonecaAmbalabu.getClass02(classValue, bonecaAmbalabuFactor2 * bonecaValue, classFactor, 0.4F, 40 + k, ForeShinUtils.getValueForIntFloat(3495711, 0.82F));
               bonecaAmbalabu.getClass02(classValue - classFactor * 0.2F, bonecaAmbalabuFactor2 * bonecaValue - classFactor * 0.15F, classFactor * 0.45F, 0.3F, 90 + k, ForeShinUtils.getValueForIntFloat(2770200, 0.5F));
            }
         }
      }

      for (int p = -1; p <= 1; p += 2) {
         float[] bonecaAmbalabuValues = getAmounts(p * 3.35F, 30.9F, -0.099999994F);
         bonecaAmbalabu.getClass022(bonecaAmbalabuValues[0] * ambalabuBonecaValue, bonecaAmbalabuValues[1] * bonecaValue, 15.0F, 9.0F, ForeShinUtils.getValueForIntFloat(3034908, 0.18F));
         float[] amounts2 = getAmounts(p * 4.4F, 29.699999F, 0.5F);
         float[] amounts3 = getAmounts(p * 4.2F, 29.55F, 1.3F);
         float[] amounts4 = getAmounts(p * 3.6F, 29.199999F, 1.9000001F);
         bonecaAmbalabu.getClassForFloatInt(
            amounts2[0] * ambalabuBonecaValue,
            amounts2[1] * bonecaValue + 1.6F,
            amounts3[0] * ambalabuBonecaValue,
            amounts3[1] * bonecaValue + 1.6F,
            amounts4[0] * ambalabuBonecaValue,
            amounts4[1] * bonecaValue + 1.6F,
            1.6F,
            ForeShinUtils.getValueForIntFloat(2902554, 0.2F)
         );
         bonecaAmbalabu.getClassForFloatInt(
            amounts2[0] * ambalabuBonecaValue, amounts2[1] * bonecaValue, amounts3[0] * ambalabuBonecaValue, amounts3[1] * bonecaValue, amounts4[0] * ambalabuBonecaValue, amounts4[1] * bonecaValue, 1.4F, ForeShinUtils.getValueForIntFloat(11063146, 0.3F)
         );
         float[] amounts5 = getAmounts(p * 5.0F, 28.5F, 1.2F);
         bonecaAmbalabu.getClass022(amounts5[0] * ambalabuBonecaValue, amounts5[1] * bonecaValue, 8.6F, 7.6F, ForeShinUtils.getValueForIntFloat(4870694, 0.9F));
         bonecaAmbalabu.getClass022(amounts5[0] * ambalabuBonecaValue, amounts5[1] * bonecaValue, 7.2F, 6.3F, ForeShinUtils.getValueForInt(7305786));
         bonecaAmbalabu.getClass022(amounts5[0] * ambalabuBonecaValue, amounts5[1] * bonecaValue, 5.4F, 4.7F, 0.9F, ForeShinUtils.getValueForIntFloat(9213008, 0.45F));
         bonecaAmbalabu.getClass022(amounts5[0] * ambalabuBonecaValue - 1.2F, amounts5[1] * bonecaValue - 1.4F, 2.0F, 1.5F, ForeShinUtils.getValueForIntFloat(12107386, 0.6F));
         float[] amounts6 = getAmounts(p * 0.8F, 28.4F, -5.4F);
         bonecaAmbalabu.getClass022(amounts6[0] * ambalabuBonecaValue, amounts6[1] * bonecaValue, 1.8F, 1.4F, ForeShinUtils.getValueForInt(2371610));
      }

      for (int q = 0; q < 14; q++) {
         float bonecaAmbalabuFactor3 = 0.5F + (getAmount(q, 7) - 0.5F) * 0.44F;
         float bonecaAmbalabuFactor4 = 0.25F + 0.35F * getAmount(q, 8);
         bonecaAmbalabu.getClass022(
            bonecaAmbalabuFactor3 * ambalabuBonecaValue,
            bonecaAmbalabuFactor4 * bonecaValue,
            3.0F + 4.0F * getAmount(q, 9),
            0.9F + 0.6F * getAmount(q, 10),
            ForeShinUtils.getValueForIntFloat(15792856, 0.18F + 0.12F * getAmount(q, 11))
         );
      }

      for (int r = 0; r < 4; r++) {
         float bonecaAmbalabuFactor5 = 0.5F + (r - 1.5F) * 0.05F + (getAmount(r, 13) - 0.5F) * 0.02F;
         float bonecaAmbalabuFactor6 = 0.55F + 0.2F * getAmount(r, 14);
         bonecaAmbalabu.getClass022(bonecaAmbalabuFactor5 * ambalabuBonecaValue, bonecaAmbalabuFactor6 * bonecaValue, 8.0F + 4.0F * getAmount(r, 15), 2.0F, ForeShinUtils.getValueForIntFloat(16056288, 0.18F));
      }

      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(16056288, 0.2F), 0.5F, 0.45F, 19);
      int valueForInt = ForeShinUtils.getValueForInt(5212722);

      for (int s = 0; s < bonecaValue * 0.26F; s++) {
         float foreShinFactor = 1.0F - getAmount(s / (bonecaValue * 0.26F));

         for (int t = 0; t < ambalabuBonecaValue; t++) {
            bonecaAmbalabu.getBonecaAmbalabu(t, s, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(t, s), valueForInt, foreShinFactor));
         }
      }

      handleBonecaAmbalabu(bonecaAmbalabu, bonecaValue, 10.7F, 0.0F, 1.0F, 0.0F, 0.55F, ForeShinUtils.getValueForInt(3095838));
   }

   private static float[] getAmounts(float ambalabuBonecaValue, float ambalabuBonecaValue2, float ambalabuBonecaValue3) {
      float ambalabuBonecaFactor = ambalabuBonecaValue / 6.3F;
      float ambalabuBonecaFactor2 = (ambalabuBonecaValue2 - 26.9F) / 3.7F;
      float ambalabuBonecaFactor3 = (ambalabuBonecaValue3 - -0.5F - 0.18F * (ambalabuBonecaValue2 - 26.9F)) / 5.2F;
      float distance = (float)Math.sqrt(ambalabuBonecaFactor * ambalabuBonecaFactor + ambalabuBonecaFactor2 * ambalabuBonecaFactor2 + ambalabuBonecaFactor3 * ambalabuBonecaFactor3);
      float ambalabuBonecaValue4 = ambalabuBonecaFactor / distance;
      float ambalabuBonecaBound = ambalabuBonecaFactor2 / distance;
      float ambalabuBonecaValue5 = ambalabuBonecaFactor3 / distance;
      float acos = (float)Math.acos(Math.max(-1.0F, Math.min(1.0F, ambalabuBonecaBound)));
      float angle = (float)Math.atan2(-ambalabuBonecaValue4, -ambalabuBonecaValue5);
      return new float[]{0.5F + angle / (float) (Math.PI * 2), acos / (float) (Math.PI / 2)};
   }

   private static void handle(float[] ambalabuBonecaValues, int ambalabuBonecaValue, float degrees) {
      float angle = (float)Math.toRadians(degrees);
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      int index = ambalabuBonecaValue == 0 ? 1 : (ambalabuBonecaValue == 1 ? 2 : 0);
      int index2 = ambalabuBonecaValue == 0 ? 2 : (ambalabuBonecaValue == 1 ? 0 : 1);
      float ambalabuBonecaFactor = ambalabuBonecaValues[index];
      float ambalabuBonecaFactor2 = ambalabuBonecaValues[index2];
      ambalabuBonecaValues[index] = cos * ambalabuBonecaFactor - sin * ambalabuBonecaFactor2;
      ambalabuBonecaValues[index2] = sin * ambalabuBonecaFactor + cos * ambalabuBonecaFactor2;
   }

   @Override
   public int getValue() {
      return 1;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, BonecaAmbalabu bonecaAmbalabu2) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(15582108, 0.1F * getAmount((bonecaAmbalabuValue - 0.3F) / 0.7F)));
      }

      for (int j = 0; j < bonecaAmbalabu2.bonecaValue; j++) {
         float bonecaAmbalabuValue2 = (j + 0.5F) / bonecaAmbalabu2.bonecaValue;
         bonecaAmbalabu2.getClassBonecaAmbalabu(
            0.0F,
            j,
            bonecaAmbalabu2.ambalabuBonecaValue,
            j + 1,
            ForeShinUtils.getValueForIntFloat(12157536, 0.1F * getAmount(bonecaAmbalabuValue2 / 0.12F) * (1.0F - getAmount((bonecaAmbalabuValue2 - 0.12F) / 0.3F)))
         );
      }

      byte bonecaAmbalabuLimit = 22;

      for (int k = 0; k < bonecaAmbalabuLimit; k++) {
         float foreShinFactor = getAmount((float)k / bonecaAmbalabuLimit);

         for (int m = 0; m < bonecaAmbalabu2.ambalabuBonecaValue; m++) {
            int foreShinValue = ForeShinUtils.getValue2(
               bonecaAmbalabu.getBonecaAmbalabuForInt(m, bonecaAmbalabu.bonecaValue - 1 - k / 3), bonecaAmbalabu.getBonecaAmbalabuForInt(m, bonecaAmbalabu.bonecaValue - 1), 0.5F
            );
            bonecaAmbalabu2.getBonecaAmbalabu(m, k, ForeShinUtils.getValue2(foreShinValue, bonecaAmbalabu2.getBonecaAmbalabuForInt(m, k), foreShinFactor));
         }
      }
   }

   private static float getAmountForFloatArrayInt(float[] ambalabuBonecaValues, int ambalabuBonecaValue) {
      float[] floats = new float[]{ambalabuBonecaValues[0] - ambalabuBonecaValue * 3.35F, ambalabuBonecaValues[1] - 29.9F, ambalabuBonecaValues[2] - -0.099999994F};
      handle(floats, 2, ambalabuBonecaValue * 14.0F);
      float sqrt = (float)Math.sqrt(floats[0] * floats[0] / 6.25F + floats[1] * floats[1] / 1.9599999F + floats[2] * floats[2] / 6.25F);
      return (sqrt - 1.0F) * 2.1F;
   }

   @Override
   public String formatTungDinString() {
      return "Boneca Ambalabu";
   }
}
