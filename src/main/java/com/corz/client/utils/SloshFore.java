package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ForeShinUtils;

public final class SloshFore extends WarmBrainrotBase {
   private static final float[] shinFootTable = new float[]{0.6F, 1.5F, 1.55F, 2.0F, 2.55F, 3.0F, 3.32F, 3.5F, 3.55F, 3.47F, 3.32F};
   private ForeEar handL;
   private ForeEar classShinProgress;
   private static final float[] sloshForeTable = new float[]{28.88F, 29.0F, 29.3F, 29.42F, 29.75F, 30.2F, 30.85F, 31.6F, 32.4F, 33.3F, 33.95F};
   private static final float[] table = new float[]{3.26F, 3.01F, 2.3F, 1.2F, 0.0F};
   private static final float[] CAP_PROFILE_HEIGHTS = new float[]{33.87F, 34.07F, 34.21F, 34.280003F, 34.3F};
   private static final float[] SPINE_Y = new float[]{16.9F, 17.6F, 18.8F, 20.0F, 21.3F, 22.6F, 23.9F, 25.1F, 26.2F, 27.1F, 27.75F, 28.15F};
   private ForeEar foreR;
   private final ForeEar[] shinProgressForeEars = new ForeEar[4];
   private static final float[] SPINE_Z_OFFSETS = new float[]{0.1F, 0.05F, 0.0F, 0.0F, 0.0F, -0.05F, -0.12F, -0.12F, -0.05F, 0.05F, 0.05F, 0.05F};
   private ForeEar classShinFoot;
   private ForeEar legL;
   private ForeEar slosh;
   private static final float[] SPINE_RADII_X = new float[]{1.3F, 2.1F, 2.3F, 2.05F, 1.8F, 1.92F, 2.2F, 2.32F, 2.45F, 2.7F, 2.22F, 0.78F};
   private final ForeEar[] shinFootForeEars = new ForeEar[3];
   private static final float[] SPINE_RADII_Z = new float[]{0.95F, 1.4F, 1.5F, 1.38F, 1.22F, 1.28F, 1.52F, 1.58F, 1.4F, 1.2F, 0.98F, 0.66F};
   private ForeEar shinProgressForeEar;
   private ForeEar shinFootForeEar;
   private static final float[] torsoHipsAmounts = new float[]{17.55F, 18.0F, 18.45F, 18.9F};
   private ForeEar shinL;
   private ForeEar foreL;
   private ForeEar handR;
   private ForeEar sloshForeForeEar;
   private ForeEar armL;
   private ForeEar shinR;
   private ForeEar hips;
   private ForeEar foreEar;
   private static final float[] hipsTorsoTable = new float[]{5.6F, 6.0F, 6.35F, 6.65F};

   @Override
   public int getValue() {
      return 3;
   }

   private static void handle(LatheLoft latheLoft, float sloshForeValue, ShinBrr shinBrr) {
      float sideScaled = sloshForeValue * 2.7F;
      float sideScaled2 = sloshForeValue * 3.0F;
      latheLoft.getLatheLoft(sideScaled, 27.2F, 0.0F, 0.68F, 8, shinBrr);
      float[] pointsSpline = LatheLoft.getPointsSpline(2, sideScaled, 27.2F, 0.0F, sloshForeValue * 3.25F, 24.6F, 0.02F, sideScaled2, 22.0F, 0.0F);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 0.64F, 0.56F, 0.52F), 10, shinBrr, false);
   }

   private static float getAmount(float sloshForeValue) {
      double sloshForeTotal = (sloshForeValue + Math.PI) % (Math.PI * 2);
      if (sloshForeTotal < 0.0) {
         sloshForeTotal += Math.PI * 2;
      }

      return (float)(sloshForeTotal - Math.PI);
   }

   private static void handleAmounts(BonecaAmbalabu bonecaAmbalabu) {
      float[] sloshForeValues = getAmounts(table, CAP_PROFILE_HEIGHTS);
      handleBonecaAmbalabu(bonecaAmbalabu, (sloshForeValue, sloshForeLimit) -> {
         float sloshForeValue3 = getAmount(sloshForeValues, table, sloshForeLimit);
         float angle = (float)((Math.PI * 2) * (sloshForeValue - 0.5F));
         float sloshForeFactor = -sloshForeValue3 * (float)Math.sin(angle);
         float sloshForeValue5 = -sloshForeValue3 * (float)Math.cos(angle);
         return getValueForFloat(sloshForeFactor, sloshForeValue5);
      });
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = getMin(poseProgress.limbSwingStrength);
      boolean shinProgressFlag = poseProgress.gliding || poseProgress.swimming;
      boolean shinProgressFlag2 = !poseProgress.sneakingPose && !poseProgress.vehicle && !shinProgressFlag && !poseProgress.inPose;
      float shinProgressFactor2 = shinProgressFlag2 ? getMin(1.0F - shinProgressFactor * 3.0F) : 0.0F;
      float shinProgressValue = poseProgress.age + (poseProgress.hashCode & 0xFF);
      float shinProgressLimit = shinProgressValue % 160.0F;
      float angle = poseProgress.animationProgress * 0.6662F * 3.0F;
      float shinProgressFactor3 = shinProgressFlag2 ? shinProgressFactor : 0.0F;
      float cos = (float)Math.cos(angle);
      float shinProgressFactor4 = Math.max(0.0F, cos) * shinProgressFactor3;
      float shinProgressFactor5 = Math.max(0.0F, -cos) * shinProgressFactor3;
      float shinProgressValue2 = 0.04F * shinProgressFactor3 + 0.3F * shinProgressFactor5;
      float shinProgressValue3 = 0.08F * shinProgressFactor2 + 0.025F * (1.0F - shinProgressFactor2) - 0.04F * shinProgressFactor3;
      float sloshForeValue = -0.9F * shinProgressFactor5;
      float shinProgressValue5 = 0.15F * shinProgressFactor2 - 0.04F * shinProgressFactor3 + 0.3F * shinProgressFactor4;
      float shinProgressValue6 = 1.28F * shinProgressFactor2 - 0.025F * (1.0F - shinProgressFactor2) + 0.04F * shinProgressFactor3;
      float shinProgressValue7 = -0.9F * shinProgressFactor4;
      float shinProgressValue8 = -2.42F * shinProgressFactor2;
      float shinProgressTotal = 0.0F;
      float shinProgressFactor6 = 0.0F;
      float swingProgress7 = 0.0F;
      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         swingProgress7 = poseProgress.getSwingProgress();
         shinProgressFactor6 = getAmount2(Math.min(swingProgress * 5.0F, (1.0F - swingProgress) * 2.5F));
         float shinProgressValue10 = getAmount(shinProgressValue2, 1.35F * swingProgress7, shinProgressFactor6);
         shinProgressValue3 = getAmount(shinProgressValue3, 0.02F, shinProgressFactor6);
         sloshForeValue = getAmount(sloshForeValue, 0.0F, shinProgressFactor6);
         float shinProgressValue11 = getAmount(shinProgressValue5, -1.35F * swingProgress7, shinProgressFactor6);
         shinProgressValue6 = getAmount(shinProgressValue6, 0.05F, shinProgressFactor6);
         shinProgressValue7 = getAmount(shinProgressValue7, 0.0F, shinProgressFactor6);
         shinProgressValue8 = getAmount(shinProgressValue8, 0.0F, shinProgressFactor6);
         shinProgressTotal = 4.5F * swingProgress7 * shinProgressFactor6;
         shinProgressValue2 = shinProgressValue10 - 0.06F * swingProgress7 * shinProgressFactor6;
         shinProgressValue5 = shinProgressValue11 - 0.06F * swingProgress7 * shinProgressFactor6;
      }

      float shinProgressValue12 = 0.4F * cos * cos * shinProgressFactor3;
      float shinProgressValue13 = 0.08F * (float)Math.sin(angle / 3.0F) * shinProgressFactor3;
      float shinProgressValue14 = 0.05F * (float)Math.sin(angle) * shinProgressFactor3;
      foreShinScale.getAmounts(this.shinL, shinProgressValue2, 0.0F, shinProgressValue3);
      foreShinScale.getAmounts(this.foreEar, sloshForeValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinR, shinProgressValue5, 0.0F, shinProgressValue6);
      foreShinScale.getAmounts(this.sloshForeForeEar, shinProgressValue7, 0.0F, shinProgressValue8);
      foreShinScale.getAmountsForeShin2(this.legL, 0.0F, shinProgressValue12, 0.0F);
      foreShinScale.getAmountsForeShin2(this.shinL, 0.0F, -shinProgressValue12, 0.0F);
      foreShinScale.getAmountsForeShin2(this.shinR, 0.0F, -shinProgressValue12, 0.0F);
      foreShinScale.getAmounts(this.armL, -0.05F * shinProgressFactor3 + 0.08F * swingProgress7 * shinProgressFactor6, shinProgressValue13, shinProgressValue14);
      float shinProgressValue15 = getAmount2((shinProgressLimit - 70.0F) / 28.0F) * (1.0F - getAmount2((shinProgressLimit - 118.0F) / 28.0F));
      float shinProgressFactor8 = shinProgressFactor2 * (1.0F - shinProgressValue15);
      float shinProgressValue16 = 0.04F * poseProgress.getAgeCode(90.0F, 0.0F);
      float shinProgressValue17 = 0.12F * (float)Math.sin(2.0F * angle) * shinProgressFactor3;
      float shinProgressValue18 = 0.12F * (float)Math.sin(2.0F * angle + (Math.PI / 2)) * shinProgressFactor3;
      handle(foreShinScale, this.foreR, this.handR, this.classShinFoot, 1.0F, shinProgressFactor8, shinProgressValue16, shinProgressValue17, shinProgressFactor6, 2.3F, 0.35F, 0.2F);
      handle(foreShinScale, this.foreL, this.handL, this.shinFootForeEar, -1.0F, shinProgressFactor8, shinProgressValue16, shinProgressValue18, shinProgressFactor6, 0.4F, 1.55F, 0.25F);
      float dinForeValue = 0.0F;
      float shinProgressValue20 = 0.0F;
      if (shinProgressFactor2 > 0.0F && poseProgress.swingProgress <= 0.0F && shinProgressLimit < 26.0F) {
         float shinProgressFactor9 = shinProgressLimit / 26.0F;
         dinForeValue = (float) (-Math.PI * 2) * getAmount2(shinProgressFactor9) * shinProgressFactor2;
         shinProgressValue20 = (float)Math.sin(Math.PI * shinProgressFactor9) * shinProgressFactor2;
      }

      foreShinScale.getAmounts(this.hips, 0.0F, dinForeValue, 0.0F);
      foreShinScale.getAmountsForeShin2(this.hips, 0.0F, shinProgressTotal, 0.0F);

      for (int i = 0; i < 4; i++) {
         float shinProgressValue21 = i * 0.35F;
         float shinProgressFactor10 = 0.01F + 0.015F * shinProgressFactor + 0.025F * shinProgressValue20 + 0.015F * shinProgressFactor6;
         foreShinScale.getAmounts(
            this.shinProgressForeEars[i],
            shinProgressFactor10 * (float)Math.sin(shinProgressValue * 0.21F + shinProgressValue21 + angle) + 0.16F * swingProgress7 * shinProgressFactor6,
            shinProgressValue13,
            shinProgressFactor10 * (float)Math.cos(shinProgressValue * 0.17F + shinProgressValue21 * 1.3F + angle) + shinProgressValue14
         );
         foreShinScale.getAmountsForForeEarFloat(this.shinProgressForeEars[i], 1.0F + 0.07F * shinProgressValue20, 1.0F - 0.2F * shinProgressValue20, 1.0F + 0.07F * shinProgressValue20);
      }

      float max11 = Math.max(-0.9F, Math.min(0.9F, poseProgress.headYawRadians));
      float max12 = Math.max(-0.6F, Math.min(0.6F, poseProgress.shinProgressAmount));
      foreShinScale.getAmountsBoneCount(this.armL, max12 * 0.3F * shinProgressFactor8, max11 * 0.6F * shinProgressFactor8, 0.0F);
      foreShinScale.getAmountsBoneCount(this.slosh, max12 * (0.7F - 0.3F * shinProgressFactor8), max11 * (0.75F - 0.6F * shinProgressFactor8), 0.02F * poseProgress.getAgeCode(120.0F, 2.0F));
      if (poseProgress.swingProgress > 0.0F) {
         float sloshForeFactor = Math.min(1.0F, poseProgress.swingProgress * 1.3F);
         float max14 = Math.max(0.0F, sloshForeFactor - 0.1F);
         foreShinScale.getAmountsForeShin2(this.shinProgressForeEar, -0.6F * sloshForeFactor, 8.5F * sloshForeFactor - 10.0F * sloshForeFactor * sloshForeFactor, -7.5F * sloshForeFactor);
         foreShinScale.getClassForForeEarFloat(this.shinProgressForeEar, Math.max(0.001F, getAmountForFloat(sloshForeFactor, 0.18F)));
         foreShinScale.getAmountsForeShin2(this.classShinProgress, 0.5F - 0.3F * max14, 0.3F + 7.0F * max14 - 9.0F * max14 * max14, -6.0F * max14);
         foreShinScale.getClassForForeEarFloat(this.classShinProgress, Math.max(0.001F, 0.7F * getAmountForFloat(max14, 0.18F)));
      } else if (shinProgressLimit >= 8.0F && shinProgressLimit < 24.0F && shinProgressFactor2 > 0.0F) {
         float shinProgressFactor15 = (shinProgressLimit - 8.0F) / 16.0F;
         float max16 = Math.max(0.0F, shinProgressFactor15 - 0.08F);
         foreShinScale.getAmountsForeShin2(this.shinProgressForeEar, 2.6F * shinProgressFactor15, 1.3F * shinProgressFactor15 - 4.2F * shinProgressFactor15 * shinProgressFactor15, -2.3F * shinProgressFactor15);
         foreShinScale.getClassForForeEarFloat(this.shinProgressForeEar, Math.max(0.001F, shinProgressFactor2 * getAmountForFloat(shinProgressFactor15, 0.15F)));
         foreShinScale.getAmountsForeShin2(this.classShinProgress, 2.3F * max16, 0.2F + 1.1F * max16 - 4.2F * max16 * max16, -1.6F * max16);
         foreShinScale.getClassForForeEarFloat(this.classShinProgress, Math.max(0.001F, 0.7F * shinProgressFactor2 * getAmountForFloat(max16, 0.15F)));
      } else {
         foreShinScale.getClassForForeEarFloat(this.shinProgressForeEar, 0.001F);
         foreShinScale.getClassForForeEarFloat(this.classShinProgress, 0.001F);
      }

      float shinProgressFactor17 = getMin(1.0F - shinProgressFactor * 2.0F) * (poseProgress.swimming ? 0.0F : 1.0F);

      for (int j = 0; j < 3; j++) {
         float shinProgressFactor18 = (shinProgressValue / 34.0F + j / 3.0F + 0.16666667F) % 1.0F;
         float shinProgressBound = shinProgressFactor17 * (float)Math.pow(Math.sin(Math.PI * shinProgressFactor18), 0.8);
         foreShinScale.getAmountsForeShin2(this.shinFootForeEars[j], 0.0F, 1.4F * shinProgressFactor18, 0.0F);
         foreShinScale.getAmounts(this.shinFootForeEars[j], 0.0F, 0.0F, (j == 1 ? 0.07F : 0.16F) * (float)Math.sin(shinProgressValue * 0.13F + j * 2.0F));
         foreShinScale.getClassForForeEarFloat(this.shinFootForeEars[j], Math.max(0.001F, shinProgressBound));
      }

      if (poseProgress.sneakingPose) {
         float angle2 = poseProgress.animationProgress * 0.6662F;
         float shinProgressValue22 = 0.2F * (float)Math.cos(angle2) * shinProgressFactor;
         float rocketsElytraBound = (float)Math.sin(angle2) * shinProgressFactor;
         float shinProgressValue23 = 16.2F;
         float shinProgressValue24 = 0.6F + shinProgressValue22;
         float foreShinValue = -0.3F - shinProgressValue22;
         foreShinScale.getAmountsForeShin2(this.legL, 0.0F, -2.0F, 0.0F);
         foreShinScale.getAmounts(this.shinL, shinProgressValue24, 0.0F, -0.08F);
         foreShinScale.getAmounts(this.foreEar, getAmount2(shinProgressValue23, shinProgressValue24, 0.17F + 0.9F * Math.max(0.0F, -rocketsElytraBound)), 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinR, foreShinValue, 0.0F, 0.12F);
         foreShinScale.getAmounts(this.sloshForeForeEar, getAmount2(shinProgressValue23, foreShinValue, 0.17F + 0.9F * Math.max(0.0F, rocketsElytraBound)), 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.armL, -0.24F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.foreR, 0.65F, 0.0F, 0.4F);
         foreShinScale.getAmounts(this.handR, 1.35F, 0.35F, 0.0F);
         foreShinScale.getAmounts(this.classShinFoot, -1.1F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.foreL, 0.65F, 0.0F, -0.4F);
         foreShinScale.getAmounts(this.handL, 1.35F, -0.35F, 0.0F);
         foreShinScale.getAmounts(this.shinFootForeEar, -1.1F, 0.0F, 0.0F);
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.shinR, 1.42F, -0.3F, 0.0F);
         foreShinScale.getAmounts(this.shinL, 1.42F, 0.3F, 0.0F);
         foreShinScale.getAmounts(this.sloshForeForeEar, -0.7F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.foreEar, -0.7F, 0.0F, 0.0F);
      }

      if (shinProgressFlag) {
         foreShinScale.getAmounts(this.shinL, -0.6F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.foreEar, 0.0F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinR, 0.1F, 0.0F, 0.0F);
      }

      if (poseProgress.inPose) {
         foreShinScale.getAmounts(this.foreR, 0.1F, 0.0F, 0.15F);
         foreShinScale.getAmounts(this.foreL, 0.1F, 0.0F, -0.15F);
         foreShinScale.getAmounts(this.handR, 0.0F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.handL, 0.0F, 0.0F, 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.slosh, 1.1F, 0.9F, 1.1F);
      }
   }

   private static float[] getAmounts(float[] sloshForeValues, float[] tailKatanaValues) {
      int sloshForeSize = sloshForeValues.length;
      float[] floats = new float[sloshForeSize];

      for (int i = 1; i < sloshForeSize; i++) {
         float sloshForeFactor = sloshForeValues[i] - sloshForeValues[i - 1];
         float sloshForeFactor2 = tailKatanaValues[i] - tailKatanaValues[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(sloshForeFactor * sloshForeFactor + sloshForeFactor2 * sloshForeFactor2);
      }

      float[] floats2 = new float[sloshForeSize];

      for (int j = 0; j < sloshForeSize; j++) {
         floats2[j] = 1.0F - floats[j] / floats[sloshForeSize - 1];
      }

      return floats2;
   }

   @Override
   public String formatNoteBlock() {
      return "block.amethyst_block.chime";
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.hips = latheLoft.getClassForStringForeEar("spin", latheLoft.getClassForeEarList(), 0.0F, 0.0F, 0.0F);
      this.legL = latheLoft.getClassForStringForeEar("hips", this.hips, 0.0F, 18.2F, 0.0F);

      for (int i = 0; i < 4; i++) {
         this.shinProgressForeEars[i] = latheLoft.getClassForStringForeEar("tutu_" + i, this.legL, 0.0F, torsoHipsAmounts[i], 0.0F);
      }

      this.armL = latheLoft.getClassForStringForeEar("torso", this.legL, 0.0F, 19.4F, 0.0F);
      this.slosh = latheLoft.getClassForStringForeEar("head", this.armL, 0.0F, 27.9F, 0.0F);
      float[][] floats = new float[][]{{-1.3F, -2.1F}, {0.0F, -2.3F}, {1.3F, -2.1F}};

      for (int j = 0; j < 3; j++) {
         float[] shinFootValues = getAmounts(floats[j][0], 34.45F, floats[j][1]);
         this.shinFootForeEars[j] = latheLoft.getClassForStringForeEar("steam_" + j, this.slosh, shinFootValues[0], shinFootValues[1], shinFootValues[2]);
      }

      float[] amounts2 = getAmounts(2.25F, 34.6F, -2.05F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("slosh", this.slosh, amounts2[0], amounts2[1], amounts2[2]);
      this.classShinProgress = latheLoft.getClassForStringForeEar("slosh_2", this.slosh, amounts2[0], amounts2[1], amounts2[2]);
      this.foreR = latheLoft.getClassForStringForeEar("arm_r", this.armL, 2.7F, 27.2F, 0.0F);
      this.handR = latheLoft.getClassForStringForeEar("fore_r", this.foreR, 3.0F, 22.0F, 0.0F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("hand_r", this.handR, 3.1F, 16.6F, 0.0F);
      this.foreL = latheLoft.getClassForStringForeEar("arm_l", this.armL, -2.7F, 27.2F, 0.0F);
      this.handL = latheLoft.getClassForStringForeEar("fore_l", this.foreL, -3.0F, 22.0F, 0.0F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("hand_l", this.handL, -3.1F, 16.6F, 0.0F);
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", this.legL, 0.95F, 18.2F, 0.0F);
      this.sloshForeForeEar = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 0.95F, 9.4F, 0.0F);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", this.legL, -0.95F, 18.2F, 0.0F);
      this.foreEar = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -0.95F, 9.4F, 0.0F);
      latheLoft.getLatheLoft(this.foreR);
      LoftLatheFlag.Region region = latheLoft.getMax(256, 96);
      handleAmountsForBonecaAmbalabu(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(256, 64);
      handleAmounts(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(160, 128);
      handleAmountsSPINE_Y(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(32, 48);
      handleForBonecaAmbalabu(max4.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max3);
      ShinBrr shinBrr = latheLoft.getMax(16184302);
      ShinBrr shinBrr2 = latheLoft.getMax(16777215);
      ShinBrr shinBrr3 = latheLoft.getMax(15328217);
      ShinBrr shinBrr4 = latheLoft.getMax(16308680);
      ShinBrr max5 = latheLoft.getMax(16042436);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max4);
      ShinBrr max6 = latheLoft.getMax(14587819);
      LoftLatheFlag.Region region5 = latheLoft.getMax(48, 96);
      handle(region5.getClassBonecaAmbalabu());
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(region5);
      LoftLatheFlag.Region region6 = latheLoft.getMax(8, 64);
      handleBonecaAmbalabu(region6.getClassBonecaAmbalabu());
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(region6);
      latheLoft.getLatheLoftForForeEar(this.slosh);
      latheLoft.getLatheLoft(new float[]{0.62F, 0.56F, 0.58F, 0.75F}, new float[]{27.3F, 28.2F, 28.75F, 29.1F}, 10, shinBrr4, null, null);
      getLatheLoft(latheLoft);
      latheLoft.getLatheLoft(new float[]{0.58F, 2.3F, 2.65F, 2.72F, 2.55F}, new float[]{28.3F, 28.4F, 28.6F, 28.85F, 28.95F}, 16, shinBrr3, null, shinBrr3);
      latheLoft.getLatheLoft(shinFootTable, sloshForeTable, 28, classForRegion, null, null);
      latheLoft.getLoftLathe(0.0F, 33.95F, 0.0F, 3.34F, 0.24F, 24, 6, shinBrr2);
      latheLoft.getLatheLoft(table, CAP_PROFILE_HEIGHTS, 24, classForRegion3, null, null);
      handle(latheLoft, shinBrr);
      latheLoft.getDequeArray();
      ShinBrr max7 = latheLoft.getMax(10118453);
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      latheLoft.getLatheLoft(amounts2[0], amounts2[1], amounts2[2], 0.36F, 0.46F, 0.36F, 6, max7);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      latheLoft.getLatheLoft(amounts2[0], amounts2[1], amounts2[2], 0.36F, 0.46F, 0.36F, 6, max7);

      for (int k = 0; k < 3; k++) {
         latheLoft.getLatheLoftForForeEar(this.shinFootForeEars[k]);
         handle(latheLoft, this.shinFootForeEars[k].offsetX, this.shinFootForeEars[k].foreEarAmount, this.shinFootForeEars[k].shinFootAmount, k, classForRegion7);
      }

      latheLoft.getLatheLoftForForeEar(this.armL);
      float[] loftPathValues = new float[3 * SPINE_Y.length];

      for (int m = 0; m < SPINE_Y.length; m++) {
         loftPathValues[3 * m + 1] = SPINE_Y[m];
         loftPathValues[3 * m + 2] = SPINE_Z_OFFSETS[m];
      }

      latheLoft.getLoftPath(loftPathValues, SPINE_RADII_X, SPINE_RADII_Z, 16, classForRegion4, null, shinBrr4, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoft(2.45F, 26.9F, 0.0F, 0.8F, 0.7F, 0.75F, 8, shinBrr4);
      latheLoft.getLatheLoft(-2.45F, 26.9F, 0.0F, 0.8F, 0.7F, 0.75F, 8, shinBrr4);
      latheLoft.getLatheLoftForForeEar(this.legL);
      handle(latheLoft);

      for (int p = 0; p < 4; p++) {
         latheLoft.getLatheLoftForForeEar(this.shinProgressForeEars[p]);
         handleHipsTorso(latheLoft, p);
      }

      latheLoft.getLatheLoftForForeEar(this.foreR);
      handle(latheLoft, 1.0F, shinBrr4);
      latheLoft.getLatheLoftForForeEar(this.handR);
      handleForLatheLoftFloatShinBrr(latheLoft, 1.0F, shinBrr4);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      handle4(latheLoft, 1.0F, shinBrr4);
      latheLoft.getLatheLoftForForeEar(this.foreL);
      handle(latheLoft, -1.0F, shinBrr4);
      latheLoft.getLatheLoftForForeEar(this.handL);
      handleForLatheLoftFloatShinBrr(latheLoft, -1.0F, shinBrr4);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handle4(latheLoft, -1.0F, shinBrr4);
      latheLoft.getLatheLoftForForeEar(this.shinR);
      handleForLatheLoftFloat(latheLoft, 1.0F, max5);
      latheLoft.getLatheLoftForForeEar(this.sloshForeForeEar);
      handle(latheLoft, 1.0F, max5, classForRegion6, classForRegion5, max6);
      latheLoft.getLatheLoftForForeEar(this.shinL);
      handleForLatheLoftFloat(latheLoft, -1.0F, max5);
      latheLoft.getLatheLoftForForeEar(this.foreEar);
      handle(latheLoft, -1.0F, max5, classForRegion6, classForRegion5, max6);
   }

   private static void handle(LatheLoft latheLoft) {
      LoftLatheFlag.Region region = latheLoft.getMax(128, 24);
      BonecaAmbalabu classBonecaAmbalabu = region.getClassBonecaAmbalabu();
      handleBonecaAmbalabu(classBonecaAmbalabu, (sloshForeFactor, sloshForeFactor2) -> {
         float var2x = 0.5F + 0.5F * (float)Math.sin((Math.PI * 2) * sloshForeFactor * 30.0);
         int sloshForeValue = ForeShinUtils.getValue(ForeShinUtils.getValueForInt(16236496), 0.92F + 0.08F * var2x);
         float sloshForeValue2 = 0.5F + 0.5F * (float)Math.cos((Math.PI * 2) * sloshForeFactor2 * 4.0);
         return ForeShinUtils.getValue2(sloshForeValue, ForeShinUtils.getValueForInt(16438239), 0.25F * sloshForeValue2);
      });
      latheLoft.getLatheLoft(new float[]{5.5F, 1.2F}, new float[]{17.3F, 18.8F}, 20, latheLoft.getClassForRegion(region), null, null);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (float)i / (bonecaAmbalabu.bonecaValue - 1);
         float bonecaAmbalabuLimit = (bonecaAmbalabuValue - 0.7F) / 0.3F;
         float bonecaAmbalabuValue2 = bonecaAmbalabuValue * 15.0F % 1.0F;
         boolean bonecaAmbalabuFlag = bonecaAmbalabuLimit > 0.0F && bonecaAmbalabuValue2 < 0.2F + 0.45F * bonecaAmbalabuLimit;
         int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(16777215), ForeShinUtils.getValueForInt(15857146), bonecaAmbalabuValue);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuFlag ? 0 : value2);
         }
      }
   }

   private static float getAmount(float sloshForeValue, float hashRegistryValue) {
      int floor3 = (int)Math.floor(sloshForeValue);
      int floor4 = (int)Math.floor(hashRegistryValue);
      float sloshForeFactor = sloshForeValue - floor3;
      float sloshForeFactor2 = hashRegistryValue - floor4;
      float sloshForeFactor3 = sloshForeFactor * sloshForeFactor * (3.0F - 2.0F * sloshForeFactor);
      float sloshForeFactor4 = sloshForeFactor2 * sloshForeFactor2 * (3.0F - 2.0F * sloshForeFactor2);
      float sloshForeValue5 = getAmount(floor3, floor4);
      float sloshForeValue6 = getAmount(floor3 + 1, floor4);
      float sloshForeValue7 = getAmount(floor3, floor4 + 1);
      float sloshForeValue8 = getAmount(floor3 + 1, floor4 + 1);
      return sloshForeValue5 + (sloshForeValue6 - sloshForeValue5) * sloshForeFactor3 + (sloshForeValue7 + (sloshForeValue8 - sloshForeValue7) * sloshForeFactor3 - (sloshForeValue5 + (sloshForeValue6 - sloshForeValue5) * sloshForeFactor3)) * sloshForeFactor4;
   }

   private static void handle(LatheLoft latheLoft, float sloshForeValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      float sideScaled = sloshForeValue * 0.95F;
      latheLoft.getLatheLoft(sideScaled, 9.4F, 0.0F, 0.66F, 8, shinBrr);
      latheLoft.getLatheLoft(
         new float[]{sideScaled, 9.4F, 0.0F, sideScaled, 7.3F, 0.12F, sideScaled, 5.0F, 0.04F, sideScaled, 3.2F, 0.0F}, new float[]{0.64F, 0.74F, 0.52F, 0.42F}, 10, shinBrr2, false
      );
      float[] floats = new float[]{sideScaled, 3.7F, 0.05F, sideScaled, 2.55F, -0.18F, sideScaled, 1.45F, -0.48F, sideScaled, 0.55F, -0.36F, sideScaled, 0.02F, -0.24F};
      latheLoft.getLoftPath(floats, new float[]{0.48F, 0.54F, 0.5F, 0.36F, 0.3F}, new float[]{0.52F, 0.64F, 0.58F, 0.4F, 0.26F}, 10, shinBrr3, null, shinBrr4, 0.0F, 0.0F, -1.0F);
   }

   @Override
   public float getAmountFloat() {
      return 1.4F;
   }

   private static float getAmountForFloat(float sloshForeFactor, float sloshForeValue) {
      return getAmount2((sloshForeFactor - sloshForeValue) / 0.08F) * (float)Math.sqrt(Math.max(0.0, Math.sin(Math.PI * sloshForeFactor)));
   }

   private static void handle(LatheLoft latheLoft, float sloshForeValue, float sloshForeValue2, float sloshForeValue3, int k, ShinBrr shinBrr) {
      float sloshForeValue5 = k == 1 ? 0.85F : 0.75F;
      float sloshForeValue6 = k % 2 == 0 ? 1.0F : -1.0F;
      float sloshForeFactor = k == 1 ? 0.6F : 1.0F;
      float[] pointsSpline = LatheLoft.getPointsSpline(
         3,
         sloshForeValue,
         sloshForeValue2,
         sloshForeValue3,
         sloshForeValue + sloshForeValue6 * 0.6F * sloshForeFactor,
         sloshForeValue2 + 1.2F * sloshForeValue5,
         sloshForeValue3 - 0.2F,
         sloshForeValue - sloshForeValue6 * 0.5F * sloshForeFactor,
         sloshForeValue2 + 2.4F * sloshForeValue5,
         sloshForeValue3 + 0.1F,
         sloshForeValue + sloshForeValue6 * 0.2F * sloshForeFactor,
         sloshForeValue2 + 3.6F * sloshForeValue5,
         sloshForeValue3 - 0.15F
      );
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(latheLoftSize, 0.15F, 0.19F, 0.11F, 0.02F), 4, shinBrr, false);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, SloshFore.Texel texel) {
      float bonecaAmbalabuFactor = 1.0F / Math.max(1, bonecaAmbalabu.ambalabuBonecaValue - 1);
      float bonecaAmbalabuFactor2 = 1.0F / Math.max(1, bonecaAmbalabu.bonecaValue - 1);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            int bonecaAmbalabuDivisor = 0;
            int bonecaAmbalabuTotal = 0;
            int bonecaAmbalabuTotal2 = 0;
            int bonecaAmbalabuTotal3 = 0;

            for (int k = 0; k < 2; k++) {
               for (int m = 0; m < 2; m++) {
                  float bonecaAmbalabuBound = (j + (m - 0.5F) * 0.5F) * bonecaAmbalabuFactor;
                  float hashHuntBound = (i + (k - 0.5F) * 0.5F) * bonecaAmbalabuFactor2;
                  int at = texel.at(Math.max(0.0F, Math.min(1.0F, bonecaAmbalabuBound)), Math.max(0.0F, Math.min(1.0F, hashHuntBound)));
                  if (at >>> 24 >= 128) {
                     bonecaAmbalabuDivisor++;
                     bonecaAmbalabuTotal += at >> 16 & 0xFF;
                     bonecaAmbalabuTotal2 += at >> 8 & 0xFF;
                     bonecaAmbalabuTotal3 += at & 0xFF;
                  }
               }
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuDivisor >= 2 ? ForeShinUtils.getValue(bonecaAmbalabuTotal / bonecaAmbalabuDivisor, bonecaAmbalabuTotal2 / bonecaAmbalabuDivisor, bonecaAmbalabuTotal3 / bonecaAmbalabuDivisor) : 0);
         }
      }
   }

   private static boolean canFlag(float sloshForeFactor, float sloshForeLimit) {
      float sloshForeFactor3 = sloshForeFactor * sloshForeFactor + sloshForeLimit * sloshForeLimit - 1.0F;
      return sloshForeFactor3 * sloshForeFactor3 * sloshForeFactor3 - sloshForeFactor * sloshForeFactor * sloshForeLimit * sloshForeLimit * sloshForeLimit <= 0.0F;
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu) {
      float sloshForeFactor = 6.2F;
      handleBonecaAmbalabu(
         bonecaAmbalabu,
         (len, sloshForeFactor2) -> {
            float sloshForeValue = 9.4F - sloshForeFactor2 * sloshForeFactor;
            float angle = (float)((Math.PI * 2) * (len - 0.5F));
            float sin = (float)Math.sin(angle);
            int valueForInt2 = ForeShinUtils.getValueForInt(16042436);
            int value2 = ForeShinUtils.getValue2(
               valueForInt2, ForeShinUtils.getValueForInt(16440794), 0.3F * (float)Math.exp(-(getAmount(angle + 0.3F) / 0.6F) * (getAmount(angle + 0.3F) / 0.6F))
            );
            float sloshForeBound = Math.abs(sloshForeValue - (4.0F + 0.62F * sin));
            float abs2 = Math.abs(sloshForeValue - (4.0F - 0.62F * sin));
            float hashHuntBound = Math.abs(sloshForeValue - 4.85F);
            float min4 = Math.min(Math.min(sloshForeBound, abs2), hashHuntBound);
            if (sloshForeValue < 5.1F && min4 < 0.19F) {
               int foreShinValue = ForeShinUtils.getValueForInt(15506610);
               int sloshForeValue6 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(16573418), 0.55F * getAmount2((0.08F - min4) / 0.08F));
               int valueForInt = ForeShinUtils.getValue2(sloshForeValue6, ForeShinUtils.getValueForInt(14254235), 0.5F * getAmount2((min4 - 0.11F) / 0.06F));
               value2 = ForeShinUtils.getValue2(value2, valueForInt, getAmount2((0.19F - min4) / 0.03F));
            }

            return value2;
         }
      );
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float[] sloshForeValues, float[] tailKatanaValues, float sloshForeValue, int foreShinValue, int sloshForeValue3) {
      float[] amounts3 = getAmounts(sloshForeValues, tailKatanaValues);
      float sloshForeFactor = 16.0F + 2.0F * sloshForeValue3;
      float sloshForeValue4 = 0.37F * sloshForeValue3;
      float sloshForeFactor2 = 0.35F;
      handleBonecaAmbalabu(bonecaAmbalabu, (sloshForeFactor3, sloshForeValue5) -> {
         float sloshForeLimit = getAmount(amounts3, sloshForeValues, sloshForeValue5);
         float base2 = Math.abs((float)Math.sin(Math.PI * (sloshForeFactor3 * sloshForeFactor + sloshForeValue4)));
         float sloshForeValue6 = sloshForeValue - sloshForeFactor2 * (1.0F - (float)Math.pow(base2, 0.55F));
         if (sloshForeLimit > sloshForeValue6) {
            return 0;
         } else {
            float sloshForeLimit2 = (sloshForeLimit - 1.2F) / (sloshForeValue - 1.2F);
            float sloshForeValue7 = 0.5F + 0.5F * (float)Math.sin((Math.PI * 2) * (sloshForeFactor3 * 30.0F + 0.3F * (float)Math.sin((Math.PI * 2) * sloshForeFactor3 * 5.0 + sloshForeValue3)));
            int sloshForeValue8 = ForeShinUtils.getValue(foreShinValue, 0.95F + 0.05F * sloshForeValue7);
            int value2 = ForeShinUtils.getValue2(sloshForeValue8, ForeShinUtils.getValueForInt(16639726), getAmount2((sloshForeLimit2 - 0.5F) / 0.5F) * 0.4F);
            if (sloshForeValue6 - sloshForeLimit < 0.1F) {
               value2 = ForeShinUtils.getValue2(value2, ForeShinUtils.getValueForInt(16773878), 0.5F);
            }

            if (sloshForeValue3 == 3 && sloshForeLimit2 > 0.3F && getAmount((int)(sloshForeFactor3 * 180.0F), (int)(sloshForeValue5 * 44.0F)) > 0.975F) {
               value2 = ForeShinUtils.getValue2(value2, ForeShinUtils.getValueForInt(16777215), 0.75F);
            }

            return value2;
         }
      });
   }

   private static float getAmount(float sloshForeValue, float sloshForeValue2, float sloshForeFactor) {
      return sloshForeValue + (sloshForeValue2 - sloshForeValue) * sloshForeFactor;
   }

   private static LatheLoft getLatheLoft(LatheLoft latheLoft) {
      double tan = -Math.tan(Math.toRadians(-14.0));
      double sqrt = Math.sqrt(tan * tan + 4.0);
      double angle = 0.5 * Math.atan2(2.0 * tan, tan * tan);
      double sqrt2 = Math.sqrt((2.0 + tan * tan + tan * sqrt) / 2.0);
      double sqrt3 = Math.sqrt((2.0 + tan * tan - tan * sqrt) / 2.0);
      double radians = Math.atan2(-Math.sin(angle) / sqrt3, Math.cos(angle) / sqrt2);
      return latheLoft.getDequeArrayArrayDeque()
         .getLatheLoft(0.0F, 29.0F, 0.0F)
         .getLatheLoftLatheLoftAmountLoft((float)Math.toDegrees(angle))
         .getLatheLoftForFloat(1.0F, (float)sqrt2, (float)sqrt3)
         .getLatheLoftLatheLoftAmountLoft((float)Math.toDegrees(radians))
         .getLatheLoft(0.0F, -29.0F, 0.0F);
   }

   private static void handleHipsTorso(LatheLoft latheLoft, int index) {
      float hipsTorsoValue = torsoHipsAmounts[index];
      float sloshForeValue = hipsTorsoTable[index];
      float hipsTorsoValue3 = 0.22F;
      float[] floats = new float[]{sloshForeValue, 1.2F};
      float[] tailKatanaValues = new float[]{hipsTorsoValue - hipsTorsoValue3 + 0.17F, hipsTorsoValue + 0.1F};
      int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15637430), ForeShinUtils.getValueForInt(16234700), 0.35F + 0.65F * index / 3.0F);
      LoftLatheFlag.Region region = latheLoft.getMax(320, 40);
      handle(region.getClassBonecaAmbalabu(), floats, tailKatanaValues, sloshForeValue, foreShinValue, index);
      latheLoft.getLatheLoft(floats, tailKatanaValues, 28, latheLoft.getClassForRegion(region), null, null);
      latheLoft.getLatheLoft(floats, new float[]{hipsTorsoValue - hipsTorsoValue3 - 0.17F, hipsTorsoValue - 0.1F}, 28, latheLoft.getClassForRegionInt(region, 14917816), null, null);
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float sloshForeValue, ShinBrr shinBrr) {
      float sideScaled = sloshForeValue * 0.95F;
      latheLoft.getLatheLoft(
         new float[]{sideScaled, 18.5F, 0.0F, sideScaled, 15.4F, -0.06F, sideScaled, 12.2F, -0.05F, sideScaled, 9.4F, 0.0F}, new float[]{1.18F, 1.02F, 0.84F, 0.66F}, 10, shinBrr, false
      );
   }

   private static void handleForLatheLoftFloatShinBrr(LatheLoft latheLoft, float sloshForeValue, ShinBrr shinBrr) {
      float latheLoftValue = sloshForeValue * 3.0F;
      float sloshForeValue3 = sloshForeValue * 3.1F;
      latheLoft.getLatheLoft(latheLoftValue, 22.0F, 0.0F, 0.54F, 8, shinBrr);
      float[] pointsSpline = LatheLoft.getPointsSpline(2, latheLoftValue, 22.0F, 0.0F, sloshForeValue * 3.15F, 19.5F, -0.03F, sloshForeValue3, 16.6F, 0.0F);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 0.48F, 0.43F, 0.31F), 10, shinBrr, false);
   }

   private static float getAmount(int sloshForeValue, int sloshForeValue2) {
      int sloshForeValue3 = sloshForeValue * 374761393 + sloshForeValue2 * 668265263 + 1442695041;
      int sloshForeValue4 = (sloshForeValue3 ^ sloshForeValue3 >>> 13) * 1274126177;
      int sloshForeValue5 = sloshForeValue4 ^ sloshForeValue4 >>> 16;
      return (sloshForeValue5 & 16777215) / 1.6777215E7F;
   }

   private static float getAmount2(float sloshForeValue, float angle, float sloshForeValue2) {
      float sloshForeBound = (sloshForeValue - 8.800001F * (float)Math.cos(angle) - sloshForeValue2) / 9.299999F;
      return -((float)Math.acos(Math.max(-1.0F, Math.min(1.0F, sloshForeBound)))) - angle;
   }

   private static void handleAmountsForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      float[] sloshForeValues = getAmounts(shinFootTable, sloshForeTable);
      float radians = (float)Math.toRadians(-32.0);
      handleBonecaAmbalabu(bonecaAmbalabu, (sloshForeValue, sloshForeLimit) -> {
         float sloshForeValue3 = getAmount(sloshForeValues, sloshForeTable, sloshForeLimit);
         float sloshForeValue4 = (float)((Math.PI * 2) * (sloshForeValue - 0.5F));
         int valueForInt = ForeShinUtils.getValueForInt(16118508);
         int value2 = ForeShinUtils.getValue2(valueForInt, ForeShinUtils.getValueForInt(14012099), getAmount2((30.6F - sloshForeValue3) / 1.0F) * 0.25F);
         int foreShinValue = ForeShinUtils.getValue2(value2, ForeShinUtils.getValueForInt(14012099), getAmount2((0.06F - Math.abs(sloshForeValue3 - 29.4F)) / 0.03F) * 0.4F);
         int sloshForeValue8 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(14209477), getAmount2((0.1F - Math.abs(sloshForeValue3 - 33.45F)) / 0.04F) * 0.5F);
         float sloshForeValue9 = getAmount(sloshForeValue4 - radians);
         float sloshForeFactor = getAmount2((sloshForeValue3 - 29.9F) / 0.8F) * getAmount2((33.25F - sloshForeValue3) / 0.4F);
         float sloshForeValue10 = getAmount(sloshForeValue4 - radians + 0.3F);
         int sloshForeValue11 = ForeShinUtils.getValue2(sloshForeValue8, ForeShinUtils.getValueForInt(13225169), 0.4F * (float)Math.exp(-(sloshForeValue10 / 0.14F) * (sloshForeValue10 / 0.14F)) * sloshForeFactor);
         float foreShinFactor = (float)Math.exp(-(sloshForeValue9 / 0.12F) * (sloshForeValue9 / 0.12F)) * sloshForeFactor;
         int sloshForeValue13 = ForeShinUtils.getValue2(sloshForeValue11, ForeShinUtils.getValueForInt(16777215), foreShinFactor);
         float sloshForeValue14 = getAmount(sloshForeValue4 - radians - 2.670354F);
         float sloshForeValue15 = (float)Math.exp(-(sloshForeValue14 / 0.22F) * (sloshForeValue14 / 0.22F)) * getAmount2((sloshForeValue3 - 30.4F) / 0.8F) * getAmount2((33.6F - sloshForeValue3) / 0.6F);
         return ForeShinUtils.getValue2(sloshForeValue13, ForeShinUtils.getValueForInt(16777215), sloshForeValue15 * 0.8F);
      });
   }

   private static void handle4(LatheLoft latheLoft, float sloshForeValue, ShinBrr shinBrr) {
      float latheLoftValue = sloshForeValue * 3.1F;
      latheLoft.getLatheLoft(latheLoftValue, 15.9800005F, -0.05F, 0.23F, 0.62F, 0.34F, 8, shinBrr);
      float[] floats = new float[]{latheLoftValue, 15.650001F, -0.06F, latheLoftValue - sloshForeValue * 0.08F, 15.05F, -0.14F, latheLoftValue - sloshForeValue * 0.22F, 14.6F, -0.25F};
      latheLoft.getLoftPath(floats, new float[]{0.17F, 0.14F, 0.08F}, new float[]{0.3F, 0.25F, 0.12F}, 8, shinBrr, null, shinBrr, 0.0F, 0.0F, -1.0F);
      float[] loftPathValues = new float[]{latheLoftValue - sloshForeValue * 0.12F, 15.900001F, -0.22F, latheLoftValue - sloshForeValue * 0.16F, 15.35F, -0.34F};
      latheLoft.getLoftPath(loftPathValues, new float[]{0.11F, 0.07F}, new float[]{0.11F, 0.07F}, 6, shinBrr, null, shinBrr, 0.0F, 0.0F, -1.0F);
   }

   private static void handleForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      handleBonecaAmbalabu(bonecaAmbalabu, (sloshForeValue, sloshForeLimit) -> {
         int valueForInt = ForeShinUtils.getValueForInt(15904191);
         float planValuesItem = (float)((Math.PI * 2) * (sloshForeValue - 0.5F));
         float exp = (float)Math.exp(-(getAmount(planValuesItem + 0.35F) / 0.45F) * (getAmount(planValuesItem + 0.35F) / 0.45F));
         float exp2 = (float)Math.exp(-(getAmount(planValuesItem - 0.9F) / 0.3F) * (getAmount(planValuesItem - 0.9F) / 0.3F));
         int foreShinValue = ForeShinUtils.getValue2(valueForInt, ForeShinUtils.getValueForInt(16639211), exp * 0.6F + exp2 * 0.3F);
         float amount2 = getAmount2((1.5F - Math.abs(planValuesItem)) / 0.9F);
         float sloshForeValue6 = 0.1F + 0.2F * amount2;
         float sloshForeValue7 = getAmount2((0.06F - Math.abs(getAmount(planValuesItem - (float) Math.PI))) / 0.03F);
         if (sloshForeLimit > 0.15F && sloshForeLimit < 0.6F) {
            foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(14848167), 0.55F * sloshForeValue7);
         }

         int sloshForeValue8 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(14980523), getAmount2((sloshForeLimit - 0.72F) / 0.28F) * 0.55F);
         int sloshForeValue9 = ForeShinUtils.getValue2(sloshForeValue8, ForeShinUtils.getValueForInt(13202061), getAmount2((0.045F - Math.abs(sloshForeLimit - sloshForeValue6)) / 0.02F));
         if (sloshForeLimit < sloshForeValue6 - 0.04F) {
            sloshForeValue9 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(16042436), ForeShinUtils.getValueForInt(16440794), 0.3F * amount2);
         }

         return sloshForeValue9;
      });
   }

   private static float getAmount(float[] sloshForeValues, float[] sloshForeValues2, float sloshForeLimit) {
      int sloshForeLimit2 = sloshForeValues.length;
      boolean sloshForeFlag = sloshForeValues[sloshForeLimit2 - 1] > sloshForeValues[0];

      for (int i = 0; i + 1 < sloshForeLimit2; i++) {
         float sloshForeLimit3 = sloshForeValues[i];
         float sloshForeLimit4 = sloshForeValues[i + 1];
         boolean sloshForeFlag2 = sloshForeFlag ? sloshForeLimit >= sloshForeLimit3 && sloshForeLimit <= sloshForeLimit4 : sloshForeLimit <= sloshForeLimit3 && sloshForeLimit >= sloshForeLimit4;
         if (sloshForeFlag2) {
            float sloshForeFactor = Math.abs(sloshForeLimit4 - sloshForeLimit3) < 1.0E-6F ? 0.0F : (sloshForeLimit - sloshForeLimit3) / (sloshForeLimit4 - sloshForeLimit3);
            return sloshForeValues2[i] + (sloshForeValues2[i + 1] - sloshForeValues2[i]) * sloshForeFactor;
         }
      }

      return (sloshForeFlag ? !(sloshForeLimit < sloshForeValues[0]) : !(sloshForeLimit > sloshForeValues[0])) ? sloshForeValues2[sloshForeLimit2 - 1] : sloshForeValues2[0];
   }

   private static float[] getAmounts(float sloshForeValue, float sloshForeValue2, float sloshForeFactor) {
      return new float[]{sloshForeValue, sloshForeValue2 - (float)Math.tan(Math.toRadians(-14.0)) * sloshForeFactor, sloshForeFactor};
   }

   private static float getAmount2(float sloshForeFactor) {
      sloshForeFactor = getMin(sloshForeFactor);
      return sloshForeFactor * sloshForeFactor * (3.0F - 2.0F * sloshForeFactor);
   }

   private static void handleAmountsSPINE_Y(BonecaAmbalabu bonecaAmbalabu) {
      int sloshForeSize = SPINE_Y.length;
      float[] floats = new float[sloshForeSize];

      for (int i = 1; i < sloshForeSize; i++) {
         float sloshForeFactor = SPINE_Y[i] - SPINE_Y[i - 1];
         float sloshForeFactor2 = SPINE_Z_OFFSETS[i] - SPINE_Z_OFFSETS[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(sloshForeFactor * sloshForeFactor + sloshForeFactor2 * sloshForeFactor2);
      }

      float[] sloshForeValues = new float[sloshForeSize];

      for (int j = 0; j < sloshForeSize; j++) {
         sloshForeValues[j] = floats[j] / floats[sloshForeSize - 1];
      }

      handleBonecaAmbalabu(
         bonecaAmbalabu,
         (sloshForeValue, sloshForeValue2) -> {
            float var3x = getAmount(sloshForeValues, SPINE_Y, sloshForeValue2);
            float var4x = getAmount(sloshForeValues, SPINE_RADII_X, sloshForeValue2);
            float var5x = (float)((Math.PI * 2) * (sloshForeValue - 0.5F));
            float var6x = -var4x * (float)Math.sin(var5x);
            float var7x = (float)Math.cos(var5x);
            float absVar6x = Math.abs(var6x);
            float sloshForeValue3 = 25.45F + 0.5F * (float)Math.exp(-((absVar6x - 0.95F) / 0.5F) * ((absVar6x - 0.95F) / 0.5F));
            float sloshForeValue4 = 24.9F + (sloshForeValue3 - 24.9F) * getAmount2((var7x + 0.35F) / 1.1F);
            boolean sloshForeFlag = Math.abs(absVar6x - 1.45F) < 0.19F && var3x > sloshForeValue4 - 0.2F;
            if (var3x > sloshForeValue4 && !sloshForeFlag) {
               int valueForInt = ForeShinUtils.getValueForInt(16308680);
               if (var7x > 0.3F) {
                  float exp = (float)Math.exp(
                     -((var3x - 27.05F + 0.25F * absVar6x * absVar6x / 2.5F) / 0.09F) * ((var3x - 27.05F + 0.25F * absVar6x * absVar6x / 2.5F) / 0.09F)
                  );
                  valueForInt = ForeShinUtils.getValue2(
                     valueForInt, ForeShinUtils.getValueForInt(14858140), exp * 0.5F * getAmount2((absVar6x - 0.25F) / 0.3F) * getAmount2((1.7F - absVar6x) / 0.4F)
                  );
               }

               return valueForInt;
            } else {
               int valueForInt6 = ForeShinUtils.getValueForInt(15902399);
               int foreShinValue = ForeShinUtils.getValue2(
                  valueForInt6,
                  ForeShinUtils.getValueForInt(16503775),
                  0.28F * getAmount2((var7x - 0.1F) / 0.8F) * (float)Math.exp(-((var3x - 24.2F) / 1.3F) * ((var3x - 24.2F) / 1.3F))
               );
               int sloshForeValue8 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(14256805), 0.35F * getAmount2((-var7x - 0.2F) / 0.8F));
               if (var7x > 0.0F && Math.abs(absVar6x - 0.85F) < 0.05F && var3x < sloshForeValue4 - 0.3F) {
                  sloshForeValue8 = ForeShinUtils.getValue2(sloshForeValue8, ForeShinUtils.getValueForInt(14256805), 0.6F);
               }

               if (var3x > sloshForeValue4 - 0.18F) {
                  sloshForeValue8 = ForeShinUtils.getValue2(sloshForeValue8, ForeShinUtils.getValueForInt(15044525), 0.85F);
               }

               if (sloshForeFlag) {
                  sloshForeValue8 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15902399), ForeShinUtils.getValueForInt(15044525), 0.4F);
               }

               if (var7x > 0.2F && var3x > 22.2F && var3x < sloshForeValue4 - 0.25F) {
                  float var6xScaled = var6x * 3.2F;
                  float var3xScaled = var3x * 3.2F;
                  float sloshForeValue9 = (float)Math.floor(var6xScaled) + 0.5F;
                  float sloshForeValue10 = (float)Math.floor(var3xScaled) + 0.5F;
                  float sloshForeLimit = getAmount((int)Math.floor(var6xScaled), (int)Math.floor(var3xScaled));
                  if (sloshForeLimit > 0.72F) {
                     float deltaVar = var6xScaled - sloshForeValue9;
                     float deltaVar2 = var3xScaled - sloshForeValue10;
                     if (deltaVar * deltaVar + deltaVar2 * deltaVar2 < 0.05F) {
                        sloshForeValue8 = ForeShinUtils.getValue2(sloshForeValue8, ForeShinUtils.getValueForInt(sloshForeLimit > 0.9F ? 16771825 : 16304598), 0.6F);
                     }
                  }
               }

               if (var3x < 19.6F) {
                  sloshForeValue8 = ForeShinUtils.getValue2(sloshForeValue8, ForeShinUtils.getValueForInt(15044525), 0.4F);
               }

               return sloshForeValue8;
            }
         }
      );
   }

   private static int getValueForFloat(float sloshForeFactor, float sloshForeFactor2) {
      float distance = (float)Math.sqrt(sloshForeFactor * sloshForeFactor + sloshForeFactor2 * sloshForeFactor2);
      float sloshForeValue = getAmount(sloshForeFactor * 1.8F + 7.0F, sloshForeFactor2 * 1.8F + 3.0F);
      int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(10514495), ForeShinUtils.getValueForInt(12356181), sloshForeValue * 0.55F);
      int sloshForeValue3 = ForeShinUtils.getValue2(
         value2, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15325368), ForeShinUtils.getValueForInt(16050902), sloshForeValue), getAmount2((distance - 2.3F) / 0.3F) * 0.85F
      );
      int sloshForeValue4 = ForeShinUtils.getValue2(sloshForeValue3, ForeShinUtils.getValueForInt(9132587), getAmount2((distance - 2.9F) / 0.2F) * 0.75F);
      int foreShinValue = ForeShinUtils.getValue2(sloshForeValue4, ForeShinUtils.getValueForInt(7226402), getAmount2((distance - 3.0F) / 0.15F) * 0.35F);
      float sloshForeDivisor = 1.7F;
      float sloshForeValue6 = sloshForeFactor / sloshForeDivisor;
      float sloshForeLimit = (sloshForeFactor2 + 0.15F) / sloshForeDivisor;
      if (canFlag(sloshForeValue6 / 1.13F, (sloshForeLimit + 0.04F) / 1.13F)) {
         foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(15325368), 0.85F);
      }

      if (canFlag(sloshForeValue6, sloshForeLimit)) {
         foreShinValue = ForeShinUtils.getValueForInt(16446438);
         if (canFlag(sloshForeValue6 / 0.72F, (sloshForeLimit - 0.16F) / 0.72F) && !canFlag(sloshForeValue6 / 0.64F, (sloshForeLimit - 0.2F) / 0.64F)) {
            foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(13081192), 0.75F);
         }

         if (canFlag(sloshForeValue6 / 0.44F, (sloshForeLimit - 0.3F) / 0.44F) && !canFlag(sloshForeValue6 / 0.37F, (sloshForeLimit - 0.33F) / 0.37F)) {
            foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(13081192), 0.7F);
         }
      }

      if (Math.abs(sloshForeValue6) < 0.045F && sloshForeLimit > -1.35F && sloshForeLimit < 0.95F) {
         foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(12158290), 0.6F);
      }

      return foreShinValue;
   }

   private static float getMin(float sloshForeLimit) {
      return sloshForeLimit < 0.0F ? 0.0F : (sloshForeLimit > 1.0F ? 1.0F : sloshForeLimit);
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr) {
      float[] pointsSpline = LatheLoft.getPointsSpline(2, -3.25F, 33.0F, 0.0F, -4.85F, 33.05F, 0.0F, -5.4F, 31.6F, 0.0F, -4.6F, 30.5F, 0.0F, -2.75F, 30.4F, 0.0F);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 0.36F, 0.4F, 0.38F, 0.34F, 0.32F), 8, shinBrr, false);
   }

   private static void handle(
      ForeShinScale foreShinScale, ForeEar foreEar, ForeEar foreEar2, ForeEar foreEar3, float sloshForeFactor, float sloshForeFactor2, float sloshForeValue, float sloshForeValue2, float sloshForeValue3, float sloshForeValue4, float sloshForeValue5, float sloshForeValue6
   ) {
      float foreShinValue = getAmount(getAmount(0.45F, 0.39F, sloshForeFactor2), sloshForeValue4, sloshForeValue3);
      float sloshForeFactor3 = getAmount(getAmount(0.9F + sloshForeValue2, 2.51F - sloshForeValue, sloshForeFactor2), sloshForeValue5, sloshForeValue3);
      float sloshForeValue8 = getAmount(getAmount(0.45F, -0.27F, sloshForeFactor2), 0.0F, sloshForeValue3);
      float sloshForeFactor4 = getAmount(getAmount(0.12F + 0.5F * sloshForeValue2, 1.18F + sloshForeValue * 0.5F, sloshForeFactor2), sloshForeValue6, sloshForeValue3);
      float sloshForeFactor5 = getAmount(getAmount(0.15F, 0.84F, sloshForeFactor2), 0.2F, sloshForeValue3);
      foreShinScale.getAmounts(foreEar, foreShinValue, 0.0F, sloshForeFactor * sloshForeFactor3);
      foreShinScale.getAmounts(foreEar2, sloshForeValue8, 0.0F, sloshForeFactor * sloshForeFactor4);
      foreShinScale.getAmounts(foreEar3, 0.3F * (1.0F - sloshForeFactor2) * (1.0F - sloshForeValue3), sloshForeFactor * 0.6F * sloshForeFactor2 * (1.0F - sloshForeValue3), sloshForeFactor * sloshForeFactor5);
   }

   @Override
   public String formatTungDinString() {
      return "Ballerina Cappuccina";
   }

   @Override
   public String formatTungDin() {
      return "ballerina_cappuccina";
   }

   private interface Texel {
      int at(float texelValue, float texelValue2);
   }
}
