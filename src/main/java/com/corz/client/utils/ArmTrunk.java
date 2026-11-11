package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LoftLatheFlag;

public final class ArmTrunk extends WarmBrainrotBase {
   private ForeEar sandalL;
   private ForeEar classShinProgress;
   private ForeEar classShinFoot;
   private ForeEar sandalR;
   private ForeEar shinFootForeEar;
   private static final float[] shinFootTable = new float[]{2.6F, 4.2F, 4.95F, 5.1F, 4.75F, 3.8F, 2.6F, 1.3F, 0.0F};
   private static final float[] armTrunkTable = new float[]{28.0F, 29.2F, 31.2F, 33.4F, 35.6F, 37.3F, 38.35F, 38.95F, 39.1F};
   private ForeEar earL;
   private static final float[] table = new float[]{5.3F, 6.5F, 7.1F, 6.8F, 5.6F, 3.6F};
   private static final float[] TRUNK_RING_HEIGHTS = new float[]{9.6F, 11.2F, 14.4F, 19.6F, 24.6F, 28.6F};
   private ForeEar armTipR;
   private static final float[] TRUNK_RING_RADII = new float[]{0.9F, 1.3F, 1.45F, 1.35F, 1.1F, 0.6F};
   private static final float[] UPPER_TIP_RADII = new float[]{0.0F, 1.1F, 1.6F, 1.64F, 1.25F};
   private static final float[] LOWER_TIP_RADII = new float[]{1.15F, 1.6F, 1.66F, 1.35F};
   private ForeEar trunkB;
   private static final float[] LOWER_TIP_HEIGHTS = new float[]{-5.3F, -4.3F, 0.0F, 1.2F};
   private ForeEar shinProgressForeEar;
   private ForeEar armTrunkForeEar;
   private static final float[] UPPER_TIP_HEIGHTS = new float[]{-8.0F, -7.5F, -6.3F, -4.5F, -3.4F};
   private ForeEar classHammer;
   private static final float[] BAND_OUTER_RADII = new float[]{3.35F, 2.2F};
   private ForeEar foreEar;
   private ForeEar armTipLeftSection;
   private static final float[] BAND_HEIGHTS = new float[]{3.4F, 15.0F};
   private ForeEar hammerSection;
   private ForeEar body;
   private ForeEar sandalRightSection;
   private ForeEar armTipL;
   private static final float[] BAND_INNER_RADII = new float[]{0.9F, 0.7F};
   private ForeEar footR;
   private static final float[] TRUNK_OUTLINE_POINTS = new float[]{
      0.1F, 1.6F, 2.6F, 2.9F, 5.4F, 2.3F, 6.95F, 0.0F, 6.4F, -3.4F, 5.0F, -6.2F, 3.6F, -9.4F, 1.4F, -7.4F, 0.0F, -3.4F
   };
   private ForeEar trunkC;
   private ForeEar classTrunkArm;
   private ForeEar hammer;
   private ForeEar armL;
   private static final float[] trunkArmAmounts = new float[]{
      0.0F, 33.4F, -4.3F, 0.0F, 30.4F, -6.1F, 0.0F, 26.6F, -7.1F, 0.0F, 23.8F, -7.55F, 0.0F, 22.0F, -7.95F, 0.0F, 20.9F, -8.6F
   };
   private ForeEar earLeftSection;
   private static final float[] SANDAL_STRAP_RADII = new float[]{0.6F, 0.48F, 0.3F, 0.05F};
   private static final float[] EVEN_RING_HEIGHTS = new float[]{13.5F, 21.2F};
   private static final float[] ODD_RING_HEIGHTS = new float[]{15.4F, 23.1F};
   private ForeEar minuteHandSection;
   private ForeEar footL;
   private static final float[] hammerTable = new float[]{2.75F, 3.35F, 3.32F, 2.95F};
   private static final float[] HAMMER_PROFILE_DEPTHS = new float[]{-1.15F, -0.75F, 0.95F, 1.2F};

   private static float getAmount(float armTrunkValue, float armTrunkValue2, float armTrunkDivisor) {
      float armTrunkFactor = (armTrunkValue - armTrunkValue2) / armTrunkDivisor;
      return (float)Math.exp(-armTrunkFactor * armTrunkFactor);
   }

   private static float getAmount(float[] armTrunkValues, float[] armTrunkValues2, float armTrunkValue) {
      if (armTrunkValue <= armTrunkValues2[0]) {
         return armTrunkValues[0];
      } else {
         for (int i = 0; i + 1 < armTrunkValues2.length; i++) {
            if (armTrunkValue <= armTrunkValues2[i + 1]) {
               return armTrunkValues[i] + (armTrunkValues[i + 1] - armTrunkValues[i]) * (armTrunkValue - armTrunkValues2[i]) / (armTrunkValues2[i + 1] - armTrunkValues2[i]);
            }
         }

         return armTrunkValues[armTrunkValues.length - 1];
      }
   }

   private static float[] getAmounts() {
      int armTrunkFactor = TRUNK_OUTLINE_POINTS.length / 2;
      byte pointsSplineBound = 4;
      float[] floats = new float[3 * (armTrunkFactor + 3)];

      for (int i = 0; i < armTrunkFactor + 3; i++) {
         int armTrunkValue = (i - 1 + armTrunkFactor) % armTrunkFactor;
         floats[3 * i] = TRUNK_OUTLINE_POINTS[2 * armTrunkValue] * 1.12F;
         floats[3 * i + 1] = TRUNK_OUTLINE_POINTS[2 * armTrunkValue + 1] * 1.12F;
      }

      float[] pointsSpline = LatheLoft.getPointsSpline(pointsSplineBound, floats);
      float[] floats2 = new float[2 * armTrunkFactor * pointsSplineBound];

      for (int j = 0; j < armTrunkFactor * pointsSplineBound; j++) {
         floats2[2 * j] = pointsSpline[3 * (pointsSplineBound + j)];
         floats2[2 * j + 1] = pointsSpline[3 * (pointsSplineBound + j) + 1];
      }

      return floats2;
   }

   @Override
   public String formatTungDinString() {
      return "Lirili Larila";
   }

   private static float getAmounts(float armTrunkValue) {
      return getAmount(shinFootTable, armTrunkTable, armTrunkValue) * 1.1F;
   }

   private static float[] getAmountsForFloat(float armTrunkValue) {
      float hashRegistryValue = armTrunkValue * 6.0F;
      int max3 = Math.max(0, Math.min(5, (int)Math.floor(hashRegistryValue)));
      float deltaVar = hashRegistryValue - max3;
      float armTrunkValue4 = getAmount(max3);
      float armTrunkValue5 = getAmount(max3 + 1);
      float cos = (float)Math.cos(getAmountForInt(max3));
      float cos2 = (float)Math.cos(getAmountForInt(max3 + 1));
      return new float[]{armTrunkValue4 + (armTrunkValue5 - armTrunkValue4) * deltaVar, cos + (cos2 - cos) * deltaVar};
   }

   private static void handle(LatheLoft latheLoft, float armTrunkFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4, ShinBrr shinBrr5) {
      float bedScaled = armTrunkFactor * 3.9F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(bedScaled, 0.0F, 0.0F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getPolygonExtrude(getAmounts(armTrunkFactor, 0.0F), -1.4F, 0.0F, shinBrr, shinBrr2, shinBrr3);
      latheLoft.getDequeArray();
      float[] floats = new float[]{bedScaled - 1.9F, 0.98F, -7.55F, bedScaled, 1.35F, -7.55F, bedScaled + 1.9F, 0.98F, -7.55F};
      latheLoft.getLoftPath(floats, new float[]{0.42F, 0.5F, 0.42F}, new float[]{0.3F, 0.3F, 0.3F}, 6, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
      float armTrunkFactor2 = 2.9399998F;
      float armTrunkValue = 2.0F;

      for (int i = 0; i < 2; i++) {
         float armTrunkValue2 = -2.4F - i * 2.8F;
         float armTrunkFactor3 = getMax(armTrunkValue2) + 0.15F;
         float[] latheProfileValues = new float[]{getMax(armTrunkValue2 - 0.95F) + 0.15F, getMax(armTrunkValue2 + 0.95F) + 0.15F};
         float[] tailKatanaValues = new float[]{armTrunkValue2 - 0.95F, armTrunkValue2 + 0.95F};
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(bedScaled, armTrunkValue, 0.0F).getLatheLoftLatheLoftAmountLoft(90.0F);
         latheLoft.getLatheProfile(latheProfileValues, tailKatanaValues, 12, armTrunkFactor2, 1.15F, shinBrr4, null, null);
         latheLoft.getDequeArray();
         float armTrunkFactor4 = armTrunkFactor2 * armTrunkFactor3;
         float armTrunkFactor5 = 1.15F * armTrunkFactor3;
         double angle = Math.PI / 5;
         float latheLoftValue = bedScaled + armTrunkFactor * armTrunkFactor4 * (float)Math.cos(angle);
         float armTrunkValue4 = armTrunkValue + armTrunkFactor5 * (float)Math.sin(angle);
         float degrees = (float)Math.toDegrees(Math.atan2(Math.cos(angle) / armTrunkFactor4, Math.sin(angle) / armTrunkFactor5));
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, armTrunkValue4, armTrunkValue2).getLatheLoft(-armTrunkFactor * degrees);
         latheLoft.getLatheLoft(-0.45F, -0.1F, -0.5F, 0.45F, 0.28F, 0.5F, shinBrr5, shinBrr5, null, null, shinBrr5, null);
         latheLoft.getDequeArray();
      }
   }

   private static float getAmount(float[] armTrunkValues, float armTrunkValue, float armTrunkValue2, float armTrunkFactor, float armTrunkFactor2) {
      int armTrunkLimit = armTrunkValues.length / 2;
      float armTrunkBound = Float.MAX_VALUE;

      for (int i = 0; i < armTrunkLimit; i++) {
         int armTrunkValue3 = (i + 1) % armTrunkLimit;
         float armTrunkFactor3 = armTrunkValues[2 * i] - armTrunkValue;
         float armTrunkFactor4 = armTrunkValues[2 * i + 1] - armTrunkValue2;
         float armTrunkFactor5 = armTrunkValues[2 * armTrunkValue3] - armTrunkValues[2 * i];
         float armTrunkFactor6 = armTrunkValues[2 * armTrunkValue3 + 1] - armTrunkValues[2 * i + 1];
         float armTrunkDivisor = armTrunkFactor * armTrunkFactor6 - armTrunkFactor2 * armTrunkFactor5;
         if (!(Math.abs(armTrunkDivisor) < 1.0E-6F)) {
            float hashHuntBound = (armTrunkFactor3 * armTrunkFactor6 - armTrunkFactor4 * armTrunkFactor5) / armTrunkDivisor;
            float armTrunkLimit3 = (armTrunkFactor3 * armTrunkFactor2 - armTrunkFactor4 * armTrunkFactor) / armTrunkDivisor;
            if (hashHuntBound > 0.0F && armTrunkLimit3 >= 0.0F && armTrunkLimit3 <= 1.0F) {
               armTrunkBound = Math.min(armTrunkBound, hashHuntBound);
            }
         }
      }

      return armTrunkBound == Float.MAX_VALUE ? 1.0F : armTrunkBound;
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float sin = (float)Math.sin(angle);
      float animationProgressScaled = poseProgress.getAnimationProgressForFloat(0.0F) * 0.34F;
      float shinProgressValue = -animationProgressScaled;
      float animationProgress = poseProgress.getAnimationProgress((float) Math.PI);
      float animationProgress2 = poseProgress.getAnimationProgress(0.0F);
      float shinProgressFactor2 = 6.9999995F;
      float shinProgressValue2 = poseProgress.sneakingPose ? 0.35F : 0.0F;
      float angle2 = poseProgress.sneakingPose ? 0.15F : 0.0F;
      float angle3 = animationProgressScaled + shinProgressValue2;
      float angle4 = shinProgressValue + shinProgressValue2;
      float shinProgressValue3 = 0.28F * animationProgress;
      float shinProgressValue4 = 0.28F * animationProgress2;
      float shinProgressBound = shinProgressFactor2 * (1.0F - (float)(Math.cos(angle3) * Math.cos(angle2))) + 2.8F * animationProgress - getAmount5(shinProgressValue3);
      float hashHuntBound = shinProgressFactor2 * (1.0F - (float)(Math.cos(angle4) * Math.cos(angle2))) + 2.8F * animationProgress2 - getAmount5(shinProgressValue4);
      float shinProgressValue5 = -0.13F * sin * shinProgressFactor;
      float shinProgressFactor3 = 1.0F - shinProgressFactor;
      float shinProgressValue6 = 0.03F * poseProgress.getAgeCode(110.0F, 0.0F) * shinProgressFactor3;
      foreShinScale.getAmountsForeShin2(this.body, 0.0F, -Math.min(shinProgressBound, hashHuntBound), 0.0F);
      foreShinScale.getAmounts(this.armL, -0.04F * shinProgressFactor + 0.012F * poseProgress.getAgeCode(55.0F, 1.0F) * shinProgressFactor3, 0.07F * sin * shinProgressFactor, shinProgressValue5 + shinProgressValue6);
      foreShinScale.getAmountsForeShin2(this.armL, 0.7F * sin * shinProgressFactor, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footR, angle3, 0.0F, angle2);
      foreShinScale.getAmounts(this.footL, angle4, 0.0F, -angle2);
      foreShinScale.getAmountsForeShin2(this.footR, 0.0F, 2.8F * animationProgress, 0.0F);
      foreShinScale.getAmountsForeShin2(this.footL, 0.0F, 2.8F * animationProgress2, 0.0F);
      handle(foreShinScale, this.sandalR, angle3, angle2);
      handle(foreShinScale, this.sandalL, angle4, -angle2);
      foreShinScale.getAmounts(this.sandalRightSection, shinProgressValue3, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinFootForeEar, shinProgressValue4, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.armTipR, shinProgressValue * 0.9F, 0.0F, 0.41000003F + 0.03F * poseProgress.getAgeCode(70.0F, 0.0F) - shinProgressValue5 * 0.5F);
      foreShinScale.getAmounts(this.armTipL, animationProgressScaled * 0.9F, 0.0F, -0.41000003F - 0.03F * poseProgress.getAgeCode(70.0F, 1.0F) - shinProgressValue5 * 0.5F);
      foreShinScale.getAmounts(this.foreEar, 0.32F + 0.2F * shinProgressValue + 0.05F * poseProgress.getAgeCode(70.0F, 0.6F), 0.0F, 0.38F + 0.04F * poseProgress.getAgeCode(70.0F, 0.3F));
      foreShinScale.getAmounts(this.armTipLeftSection, 0.32F + 0.2F * animationProgressScaled + 0.05F * poseProgress.getAgeCode(70.0F, 1.6F), 0.0F, -0.38F - 0.04F * poseProgress.getAgeCode(70.0F, 1.3F));
      foreShinScale.getAmounts(this.earL, 0.04F * (float)Math.sin(2.0F * angle) * shinProgressFactor + 0.03F * poseProgress.getAgeCode(90.0F, 0.4F) * shinProgressFactor3, 0.0F, -shinProgressValue5 * 0.6F - shinProgressValue6 * 1.4F);
      foreShinScale.getAmountsBoneCount(this.earL, poseProgress.shinProgressAmount, poseProgress.headYawRadians, 0.0F);
      float dinForeValue = 0.08F * poseProgress.getAgeCode(70.0F, 0.0F) + 0.24F * (float)Math.sin(2.0F * angle) * shinProgressFactor;
      foreShinScale.getAmounts(this.classShinProgress, 0.0F, dinForeValue, 0.0F);
      foreShinScale.getAmounts(this.earLeftSection, 0.0F, -dinForeValue, 0.0F);
      float shinProgressValue8 = (poseProgress.age + (poseProgress.hashCode & 127)) % 220.0F;
      float pilotStatusBound = shinProgressValue8 < 26.0F ? (float)Math.sin(shinProgressValue8 / 26.0F * Math.PI) : 0.0F;
      if (poseProgress.swingProgress > 0.0F) {
         pilotStatusBound = Math.max(pilotStatusBound, poseProgress.getSin());
      }

      if (poseProgress.poseProgressActive) {
         pilotStatusBound = 0.0F;
      }

      foreShinScale.getAmounts(
         this.trunkB, 0.04F * (float)Math.cos(2.0F * angle) * shinProgressFactor, 0.0F, 0.16F * (float)Math.sin(angle - 0.8F) * shinProgressFactor + 0.04F * poseProgress.getAgeCode(80.0F, 0.0F)
      );
      foreShinScale.getAmounts(this.trunkC, 0.05F, 0.0F, 0.22F * (float)Math.sin(angle - 1.5F) * shinProgressFactor + 0.05F * poseProgress.getAgeCode(80.0F, -0.8F));
      foreShinScale.getAmounts(
         this.classTrunkArm,
         0.12F + 0.06F * poseProgress.getAgeCode(55.0F, 0.3F) + 0.08F * pilotStatusBound * shinProgressFactor3,
         0.0F,
         0.3F * (float)Math.sin(angle - 2.2F) * shinProgressFactor + 0.07F * poseProgress.getAgeCode(80.0F, -1.6F)
      );
      float radians = poseProgress.getRadians(2.2F);
      foreShinScale.getAmountsForeShin2(this.hammer, 0.8F * (float)Math.cos(radians), 0.75F * poseProgress.getAgeCode(46.0F, 0.0F), 0.8F * (float)Math.sin(radians));
      foreShinScale.getAmounts(this.hammer, 0.0F, 0.2F * poseProgress.getAgeCode(130.0F, 1.0F), 0.06F * poseProgress.getAgeCode(60.0F, 2.0F) + 0.16F * pilotStatusBound * (float)Math.sin(poseProgress.age * 2.9F));
      foreShinScale.getAmountsBoneCount(this.hammer, 0.0F, 0.25F * pilotStatusBound * (float)Math.sin(poseProgress.age * 3.3F), 0.0F);
      foreShinScale.getAmounts(this.hammerSection, 0.0F, 0.0F, 0.55F * pilotStatusBound * (float)Math.sin(poseProgress.age * 4.1F));
      foreShinScale.getAmounts(this.classHammer, 0.0F, 0.0F, poseProgress.getRadians(0.5F) + pilotStatusBound * 3.0F);
      foreShinScale.getAmounts(this.minuteHandSection, 0.0F, 0.0F, poseProgress.getRadians(6.0F) + pilotStatusBound * 9.0F);
      float shinProgressLimit = (poseProgress.age + (poseProgress.hashCode & 63)) % 110.0F;
      float shinProgressDivisor = shinProgressLimit > 104.0F ? 0.15F : 1.0F - 0.15F * shinProgressFactor3;
      if (poseProgress.inPose) {
         shinProgressDivisor = 0.1F;
      }

      foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.shinProgressForeEar, 1.0F, shinProgressDivisor, 1.0F);
      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressValue10;
         float foreShinValue;
         float shinProgressValue12;
         if (swingProgress < 0.3F) {
            float amount4 = getAmount4(swingProgress / 0.3F);
            shinProgressValue10 = 0.41000003F + amount4 * 1.35F;
            foreShinValue = 0.25F * amount4;
            shinProgressValue12 = 0.45F * amount4;
         } else if (swingProgress < 0.55F) {
            float shinProgressValue14 = getAmount4((swingProgress - 0.3F) / 0.25F);
            shinProgressValue10 = 1.76F - shinProgressValue14 * 1.45F;
            foreShinValue = 0.25F + 0.7F * shinProgressValue14;
            shinProgressValue12 = 0.45F - 0.9F * shinProgressValue14;
         } else {
            float shinProgressValue15 = getAmount4((swingProgress - 0.55F) / 0.45F);
            shinProgressValue10 = -0.05F + shinProgressValue15 * 0.46F;
            foreShinValue = 0.95F * (1.0F - shinProgressValue15);
            shinProgressValue12 = -0.45F * (1.0F - shinProgressValue15);
         }

         float swingProgress2 = poseProgress.getSwingProgress();
         foreShinScale.getAmounts(this.armTipR, foreShinValue, 0.0F, shinProgressValue10);
         foreShinScale.getAmounts(this.foreEar, 0.16F, 0.0F, 0.38F + shinProgressValue12);
         foreShinScale.getAmountsBoneCount(this.trunkB, 1.25F * swingProgress2, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.trunkC, 0.55F * swingProgress2, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.classTrunkArm, 0.75F * swingProgress2, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.earL, 0.14F * swingProgress2, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.armTrunkForeEar, -0.5F * swingProgress2, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, 0.0F, 0.35F * swingProgress2, 0.0F);
         foreShinScale.getAmountsBoneCount(this.earLeftSection, 0.0F, -0.35F * swingProgress2, 0.0F);
         foreShinScale.getAmountsBoneCount(this.armL, swingProgress > 0.3F && swingProgress < 0.6F ? -0.08F : 0.0F, 0.0F, 0.0F);
      }

      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsBoneCount(this.armL, -0.3F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.earL, 0.06F, 0.0F, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.04F, 0.94F, 1.04F);
         foreShinScale.getAmountsBoneCount(this.armTipR, 0.35F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.armTipL, 0.35F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.foreEar, 0.3F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.armTipLeftSection, 0.3F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, 0.0F, -0.35F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.earLeftSection, 0.0F, 0.35F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.trunkB, -0.25F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.classTrunkArm, 0.4F, 0.0F, 0.0F);
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.footR, 1.35F, 0.12F, 0.0F);
         foreShinScale.getAmounts(this.footL, 1.35F, -0.12F, 0.0F);
         foreShinScale.getAmounts(this.sandalR, -0.7F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.sandalL, -0.7F, 0.0F, 0.0F);
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         float sin3 = (float)Math.sin(poseProgress.age * 0.9F);
         foreShinScale.getAmounts(this.armTipR, -0.3F, 0.0F, 1.46F);
         foreShinScale.getAmounts(this.armTipL, -0.3F, 0.0F, -1.46F);
         foreShinScale.getAmounts(this.classShinProgress, 0.0F, 0.35F + 0.45F * sin3, 0.0F);
         foreShinScale.getAmounts(this.earLeftSection, 0.0F, -0.35F - 0.45F * sin3, 0.0F);
         foreShinScale.getAmounts(this.footR, -0.25F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.25F, 0.0F, 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.07F, 0.93F, 1.07F);
      }
   }

   private void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(8818070))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8358553), 0.55F, 20.0F, 5)
         .getBonecaAmbalabu(0.05F, 8.0F, 11)
         .getBonecaAmbalabu(0.03F, 2.0F, 12);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuLimit = (float)i / bonecaAmbalabu.bonecaValue;
         if (bonecaAmbalabuLimit < 0.3F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(10791860, getAmount(bonecaAmbalabuLimit)));
         }

         if (bonecaAmbalabuLimit > 0.82F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(6186092, 0.4F * (bonecaAmbalabuLimit - 0.82F) / 0.18F));
         }
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 220, 31, 0.2F, 7172987);

      for (int j = 0; j < 4; j++) {
         float bonecaAmbalabuValue = bonecaAmbalabu.bonecaValue * (0.83F + j * 0.045F);
         float[] floats = new float[33];
         float[] floats2 = new float[33];

         for (int k = 0; k <= 32; k++) {
            floats[k] = bonecaAmbalabu.ambalabuBonecaValue * k / 32.0F;
            floats2[k] = bonecaAmbalabuValue + 1.5F * (float)Math.sin(k * 0.9F + j * 1.7F);
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 1.4F, ForeShinUtils.getValueForIntFloat(7172987, 0.65F));

         for (int m = 0; m <= 32; m++) {
            floats2[m]++;
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 1.0F, ForeShinUtils.getValueForIntFloat(11449789, 0.3F));
      }

      for (int p = 0; p < 6; p++) {
         float bonecaAmbalabuValue2 = bonecaAmbalabu.bonecaValue * (0.22F + p * 0.1F);

         for (int q = 0; q < 2; q++) {
            float[] classValues = new float[9];
            float[] floats4 = new float[9];

            for (int r = 0; r <= 8; r++) {
               float bonecaAmbalabuFactor = q == 0 ? 0.8F + 0.2F * r / 8.0F : 0.2F * r / 8.0F;
               float bonecaAmbalabuValue3 = q == 0 ? 1.0F - bonecaAmbalabuFactor : bonecaAmbalabuFactor;
               classValues[r] = bonecaAmbalabu.ambalabuBonecaValue * bonecaAmbalabuFactor;
               floats4[r] = bonecaAmbalabuValue2 + 2.5F * bonecaAmbalabuValue3 / 0.2F + 1.0F * (float)Math.sin(bonecaAmbalabuValue3 * 34.0F + p * 2.2F);
            }

            bonecaAmbalabu.getClassForFloatArrayFloat(classValues, floats4, 1.3F, ForeShinUtils.getValueForIntFloat(7172987, 0.22F));
         }
      }

      for (int s = 0; s < 3; s++) {
         float bonecaAmbalabuValue4 = 33.6F + s * 0.6F;
         float bonecaAmbalabuDivisor = 1.8F - s * 0.2F;
         float[] floats5 = new float[11];
         float[] floats6 = new float[11];
         float[] floats7 = new float[11];

         for (int t = 0; t <= 10; t++) {
            float armTrunkValue = -bonecaAmbalabuDivisor + 2.0F * bonecaAmbalabuDivisor * t / 10.0F;
            float bonecaAmbalabuValue6 = bonecaAmbalabuValue4 + 0.3F * (1.0F - getAmount3(armTrunkValue / bonecaAmbalabuDivisor)) + 0.06F * (float)Math.sin(t * 1.7F + s);
            floats5[t] = getAmount(armTrunkValue, bonecaAmbalabuValue6);
            floats6[t] = getAmountForFloat(bonecaAmbalabuValue6);
            floats7[t] = floats6[t] + 1.4F;
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats5, floats7, 1.2F, ForeShinUtils.getValueForIntFloat(11778754, 0.35F));
         bonecaAmbalabu.getClassForFloatArrayFloat(floats5, floats6, 1.5F, ForeShinUtils.getValueForIntFloat(7172987, 0.8F));
      }

      for (int bonecaAmbalabuTotal = -1; bonecaAmbalabuTotal <= 1; bonecaAmbalabuTotal += 2) {
         float classValue = getAmount(bonecaAmbalabuTotal * 3.55F, 32.8F);
         float amountForFloat = getAmountForFloat(32.8F);
         float abs9 = Math.abs(getAmount(bonecaAmbalabuTotal * 3.55F + 0.5F, 32.8F) - getAmount(bonecaAmbalabuTotal * 3.55F - 0.5F, 32.8F));
         float abs10 = Math.abs(getAmountForFloat(33.3F) - getAmountForFloat(32.3F));
         bonecaAmbalabu.getClass022(classValue, amountForFloat, 1.9F * abs9, 1.5F * abs10, ForeShinUtils.getValueForIntFloat(6975608, 0.15F));
         bonecaAmbalabu.getClass022(classValue, amountForFloat, 1.4F * abs9, 1.05F * abs10, ForeShinUtils.getValueForIntFloat(5922662, 0.15F));
         float classBound = getAmount(bonecaAmbalabuTotal * 4.75F, 32.8F);

         for (int bonecaAmbalabuCounter = -1; bonecaAmbalabuCounter <= 1; bonecaAmbalabuCounter++) {
            float hashHuntBound = getAmount(bonecaAmbalabuTotal * 5.75F, 32.8F + bonecaAmbalabuCounter * 0.5F);
            float amountForFloat13 = getAmountForFloat(32.8F + bonecaAmbalabuCounter * 0.55F - 0.1F);
            bonecaAmbalabu.getClass02(classBound, amountForFloat, hashHuntBound, amountForFloat13, 1.0F, ForeShinUtils.getValueForIntFloat(7172987, 0.6F));
         }

         float classFactor = getAmount(bonecaAmbalabuTotal * 2.25F, 33.8F);
         float bonecaAmbalabuValue15 = getAmount(bonecaAmbalabuTotal * 4.95F, 33.6F);
         float amountForFloat16 = getAmountForFloat(33.95F);
         bonecaAmbalabu.getClassForFloatInt(classFactor, amountForFloat16 + 2.0F, classValue, amountForFloat16 - 4.0F, bonecaAmbalabuValue15, amountForFloat16 + 1.0F, 3.2F, ForeShinUtils.getValueForIntFloat(6186092, 0.4F));
         bonecaAmbalabu.getClassForFloatInt(classFactor, amountForFloat16 - 2.0F, classValue, amountForFloat16 - 8.0F, bonecaAmbalabuValue15, amountForFloat16 - 3.0F, 1.6F, ForeShinUtils.getValueForIntFloat(11120824, 0.4F));
         float amountForFloat17 = getAmountForFloat(31.75F);
         bonecaAmbalabu.getClassForFloatInt(
            getAmount(bonecaAmbalabuTotal * 2.55F, 31.9F),
            amountForFloat17 - 1.0F,
            classValue,
            amountForFloat17 + 4.0F,
            getAmount(bonecaAmbalabuTotal * 4.75F, 32.0F),
            amountForFloat17 - 2.0F,
            1.1F,
            ForeShinUtils.getValueForIntFloat(7172987, 0.45F)
         );
      }
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float armTrunkValue, float amountForFloat, int armTrunkValue3, float armTrunkFactor, float armTrunkFactor2, float armTrunkValue4) {
      bonecaAmbalabu.getClass022(armTrunkValue, amountForFloat - armTrunkValue4 * 0.08F * armTrunkFactor2, 0.32F * armTrunkFactor, 0.28F * armTrunkFactor2, ForeShinUtils.getValueForIntFloat(2311711, 0.35F));

      for (int i = 0; i < 5; i++) {
         double angle = i * Math.PI * 2.0 / 5.0 + getAmount(armTrunkValue3, 3) * 1.2 + (getAmount(armTrunkValue3, i) - 0.5) * 0.4;
         float armTrunkFactor3 = 0.26F + 0.1F * getAmount(armTrunkValue3, i + 7);
         bonecaAmbalabu.getClass02(armTrunkValue, amountForFloat, armTrunkValue + (float)Math.cos(angle) * armTrunkFactor3 * armTrunkFactor, amountForFloat + (float)Math.sin(angle) * armTrunkFactor3 * armTrunkFactor2, 0.9F, ForeShinUtils.getValueForIntFloat(15987174, 0.9F));
      }

      bonecaAmbalabu.getClass022(armTrunkValue, amountForFloat, 0.28F * armTrunkFactor, 0.28F * armTrunkFactor2, ForeShinUtils.getValueForInt(14275510));
      bonecaAmbalabu.getClass022(armTrunkValue - 0.05F * armTrunkFactor, amountForFloat - 0.05F * armTrunkFactor2, 0.13F * armTrunkFactor, 0.13F * armTrunkFactor2, ForeShinUtils.getValueForInt(15854808));
   }

   private static float[] getAmounts(float[] armTrunkValues, int armTrunkValue, int armTrunkValue2) {
      float[] floats = new float[3 * (armTrunkValue2 - armTrunkValue + 1)];
      System.arraycopy(armTrunkValues, 3 * armTrunkValue, floats, 0, floats.length);
      return floats;
   }

   private void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(12884588))
         .getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(9333316, 0.6F), 6.0F, 0.6F, 45)
         .getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(14728846, 0.6F), 4.0F, 0.5F, 46)
         .getBonecaAmbalabu(0.05F, 3.0F, 47);
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.2F, bonecaAmbalabu.ambalabuBonecaValue * 0.3F, bonecaAmbalabu.bonecaValue * 0.14F, ForeShinUtils.getValueForIntFloat(10253904, 0.35F));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.62F, bonecaAmbalabu.ambalabuBonecaValue * 0.32F, bonecaAmbalabu.bonecaValue * 0.18F, ForeShinUtils.getValueForIntFloat(10253904, 0.25F));
      bonecaAmbalabu.getClass02(
         bonecaAmbalabu.ambalabuBonecaValue * 0.22F, bonecaAmbalabu.bonecaValue * 0.83F, bonecaAmbalabu.ambalabuBonecaValue * 0.78F, bonecaAmbalabu.bonecaValue * 0.83F, 2.5F, ForeShinUtils.getValueForIntFloat(11043415, 0.6F)
      );
      bonecaAmbalabu.getClass02(
         bonecaAmbalabu.ambalabuBonecaValue * 0.24F, bonecaAmbalabu.bonecaValue * 0.85F, bonecaAmbalabu.ambalabuBonecaValue * 0.76F, bonecaAmbalabu.bonecaValue * 0.85F, 1.2F, ForeShinUtils.getValueForIntFloat(14861462, 0.5F)
      );
      float[] bonecaAmbalabuValues = getAmounts(bonecaAmbalabuValue, 0.0F);
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

      int bonecaAmbalabuLimit = bonecaAmbalabuValues.length / 2;
      float[] floats = new float[bonecaAmbalabuLimit + 1];
      float[] floats2 = new float[bonecaAmbalabuLimit + 1];

      for (int j = 0; j <= bonecaAmbalabuLimit; j++) {
         floats[j] = (maxValue2 - bonecaAmbalabuValues[2 * (j % bonecaAmbalabuLimit)]) / (maxValue2 - bonecaAmbalabuBound) * bonecaAmbalabu.ambalabuBonecaValue;
         floats2[j] = (pilotStatusBound - bonecaAmbalabuValues[2 * (j % bonecaAmbalabuLimit) + 1]) / (pilotStatusBound - maxValue) * bonecaAmbalabu.bonecaValue;
      }

      bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 3.2F, ForeShinUtils.getValueForIntFloat(9069632, 0.55F));
      bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 1.2F, ForeShinUtils.getValueForIntFloat(7228462, 0.5F));
   }

   private static float getMax(float armTrunkValue) {
      float armTrunkFactor = (armTrunkValue - -3.3F) / 4.0F;
      return (float)Math.sqrt(Math.max(0.04F, 1.0F - armTrunkFactor * armTrunkFactor));
   }

   private static void handleAmounts(LatheLoft latheLoft, float armTrunkFactor, ForeEar foreEar, ForeEar foreEar2, ShinBrr shinBrr, ShinBrr shinBrr2) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(armTrunkFactor * 4.8F, 24.6F, -0.2F).getLatheLoft(armTrunkFactor * 8.0F);
      latheLoft.getLatheLoftForForeEar(foreEar);
      latheLoft.getLatheLoft(LOWER_TIP_RADII, LOWER_TIP_HEIGHTS, 8, shinBrr, null, null);
      latheLoft.getLatheLoftForForeEar(foreEar2);
      latheLoft.getLatheLoft(UPPER_TIP_RADII, UPPER_TIP_HEIGHTS, 8, shinBrr, null, null);
      float[] floats = new float[]{-6.0F, -2.4F};

      for (int i = 0; i < floats.length; i++) {
         latheLoft.getLatheLoftForForeEar(i == 0 ? foreEar2 : foreEar);
         float armTrunkFactor2 = (i == 0 ? getAmount(UPPER_TIP_RADII, UPPER_TIP_HEIGHTS, floats[i]) : getAmount(LOWER_TIP_RADII, LOWER_TIP_HEIGHTS, floats[i])) - 0.1F;
         float degrees = armTrunkFactor * (90.0F - 30.0F * i);
         double angle = Math.toRadians(degrees);
         handle(latheLoft, (float)Math.sin(angle) * armTrunkFactor2, floats[i], -((float)Math.cos(angle)) * armTrunkFactor2, -degrees, i == 0 ? 20.0F : -20.0F, 0.7F, shinBrr2);
      }

      latheLoft.getDequeArray();
   }

   private static void handle(LatheLoft latheLoft, float armTrunkValue, float armTrunkValue2, float armTrunkValue3, float degrees, float armTrunkValue5, ShinBrr shinBrr) {
      float hypot = 3.0125F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(armTrunkValue, armTrunkValue2, armTrunkValue3).getLatheLoft4(degrees).getLatheLoftLatheLoftAmountLoft(armTrunkValue5);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft4(40.0F);
      latheLoft.getLatheLoft(0.0F, -hypot, 0.0F, 1.7F, hypot, 0.0F, shinBrr, null, null, null, null, null);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft4(-40.0F).getLatheLoft(180.0F);
      latheLoft.getLatheLoft(0.0F, -hypot, 0.0F, 1.7F, hypot, 0.0F, shinBrr, null, null, null, null, null);
      latheLoft.getDequeArray();
      latheLoft.getDequeArray();
   }

   private void handle(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4074012)).getBonecaAmbalabu(0.06F, 3.0F, 49);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(2759698, 0.8F), 7.0F, 2.5F, 90.0F);
   }

   private void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float absScaled = Math.abs((i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 4.0F;
         int bonecaAmbalabuValue = absScaled < 0.4F
            ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(6989903), ForeShinUtils.getValueForInt(5016126), absScaled / 0.4F)
            : (
               absScaled < 0.65F
                  ? ForeShinUtils.getValueForInt(5016126)
                  : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5016126), ForeShinUtils.getValueForInt(3104298), Math.min(1.0F, (absScaled - 0.65F) / 0.3F))
            );
         bonecaAmbalabu.getClassBonecaAmbalabu(i, 0.0F, i + 1, bonecaAmbalabu.bonecaValue, bonecaAmbalabuValue);
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.06F, 5.0F, 81).getBonecaAmbalabu(ForeShinUtils.getValueForInt(6199879), 0.3F, 6.0F, 83);

      for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
         float bonecaAmbalabuValue2 = (float)j / bonecaAmbalabu.bonecaValue;
         if (bonecaAmbalabuValue2 < 0.12F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, j, bonecaAmbalabu.ambalabuBonecaValue, j + 1, ForeShinUtils.getValueForIntFloat(2047772, 0.45F * (1.0F - bonecaAmbalabuValue2 / 0.12F)));
         }
      }

      bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, (float)bonecaAmbalabu.bonecaValue, 1.2F, ForeShinUtils.getValueForIntFloat(9225326, 0.3F));

      for (int k = 0; k < 3; k++) {
         handle(bonecaAmbalabu, bonecaAmbalabu.ambalabuBonecaValue * 0.5F + (k % 2 == 0 ? -0.8F : 0.8F), bonecaAmbalabu.bonecaValue * (0.2F + k * 0.22F), 40 + k, 4.0F, 9.0F, -1.0F);
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuFactor, float[][] floats, float[] bonecaAmbalabuValues, boolean bonecaAmbalabuFlag, float bonecaAmbalabuValue, int valueForIntFloat) {
      int bonecaAmbalabuSize = bonecaAmbalabuValues.length / 2;
      float[] classValues = new float[bonecaAmbalabuSize];
      float[] floats3 = new float[bonecaAmbalabuSize];

      for (int i = 0; i < bonecaAmbalabuSize; i++) {
         float[] amounts2 = getAmounts(floats, bonecaAmbalabuValues[2 * i + 1]);
         float armTrunkLimit = Math.max(-1.0F, Math.min(1.0F, (bonecaAmbalabuValues[2 * i] - amounts2[0]) * bonecaAmbalabuFactor / amounts2[2]));
         classValues[i] = getAmountForFloatFlag(armTrunkLimit, bonecaAmbalabuFlag) * bonecaAmbalabu.ambalabuBonecaValue;
         floats3[i] = amounts2[1] * bonecaAmbalabu.bonecaValue;
      }

      bonecaAmbalabu.getClassForFloatArrayFloat(classValues, floats3, bonecaAmbalabuValue, valueForIntFloat);
      if (!bonecaAmbalabuFlag) {
         for (int j = 0; j < bonecaAmbalabuSize; j++) {
            classValues[j] -= bonecaAmbalabu.ambalabuBonecaValue;
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(classValues, floats3, bonecaAmbalabuValue, valueForIntFloat);
      }
   }

   private void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(7029286)).getBonecaAmbalabu(0.07F, 4.0F, 51);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.1F, ForeShinUtils.getValueForInt(5583897));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.9F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(5583897));

      for (int i = 0; i < 16; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) * bonecaAmbalabu.ambalabuBonecaValue / 16.0F;
         bonecaAmbalabu.getClass02(bonecaAmbalabuValue - 1.2F, bonecaAmbalabu.bonecaValue * 0.22F, bonecaAmbalabuValue + 1.2F, bonecaAmbalabu.bonecaValue * 0.22F, 0.9F, ForeShinUtils.getValueForIntFloat(13214074, 0.7F));
         bonecaAmbalabu.getClass02(bonecaAmbalabuValue - 1.2F, bonecaAmbalabu.bonecaValue * 0.78F, bonecaAmbalabuValue + 1.2F, bonecaAmbalabu.bonecaValue * 0.78F, 0.9F, ForeShinUtils.getValueForIntFloat(13214074, 0.7F));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.4F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.6F, ForeShinUtils.getValueForIntFloat(9067062, 0.35F));
   }

   private static float getMax() {
      float armTrunkBound = 0.0F;

      for (int i = 0; i < 6; i++) {
         armTrunkBound = Math.max(armTrunkBound, Math.abs((float)Math.sin(getAmountForInt(i))));
      }

      return armTrunkBound;
   }

   private static float getAmount(float armTrunkValue) {
      return armTrunkValue < 0.3F ? 0.3F * (1.0F - armTrunkValue / 0.3F) : 0.0F;
   }

   private static float getAmount(int armTrunkValue) {
      return (float)Math.sin(getAmountForInt(armTrunkValue)) / getMax();
   }

   private static float[] getAmounts(float[] armTrunkValues, float armTrunkValue) {
      float armTrunkBound = Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;
      int armTrunkLimit = armTrunkValues.length / 2;

      for (int i = 0; i < armTrunkLimit; i++) {
         int armTrunkValue2 = (i + 1) % armTrunkLimit;
         float armTrunkValue3 = armTrunkValues[2 * i + 1];
         float armTrunkValue4 = armTrunkValues[2 * armTrunkValue2 + 1];
         if (!((armTrunkValue3 - armTrunkValue) * (armTrunkValue4 - armTrunkValue) > 0.0F) && armTrunkValue3 != armTrunkValue4) {
            float hashHuntBound = armTrunkValues[2 * i] + (armTrunkValues[2 * armTrunkValue2] - armTrunkValues[2 * i]) * (armTrunkValue - armTrunkValue3) / (armTrunkValue4 - armTrunkValue3);
            armTrunkBound = Math.min(armTrunkBound, hashHuntBound);
            pilotStatusBound = Math.max(pilotStatusBound, hashHuntBound);
         }
      }

      return new float[]{armTrunkBound, pilotStatusBound};
   }

   private void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float absScaled = Math.abs(bonecaAmbalabuValue - 0.5F) * 4.0F;
         int classValue = absScaled < 0.45F
            ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(6989903), ForeShinUtils.getValueForInt(5016126), absScaled / 0.45F)
            : (
               absScaled < 0.7F
                  ? ForeShinUtils.getValueForInt(5016126)
                  : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5016126), ForeShinUtils.getValueForInt(3104298), Math.min(1.0F, (absScaled - 0.7F) / 0.3F))
            );
         bonecaAmbalabu.getClassBonecaAmbalabu(i, 0.0F, i + 1, bonecaAmbalabu.bonecaValue, classValue);
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.06F, 6.0F, 23).getBonecaAmbalabu(ForeShinUtils.getValueForInt(6199879), 0.3F, 8.0F, 25);

      for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
         float bonecaAmbalabuValue3 = (float)j / bonecaAmbalabu.bonecaValue;
         if (bonecaAmbalabuValue3 < 0.16F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, j, bonecaAmbalabu.ambalabuBonecaValue, j + 1, ForeShinUtils.getValueForIntFloat(2047772, 0.4F * (1.0F - bonecaAmbalabuValue3 / 0.16F)));
         }
      }

      bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, (float)bonecaAmbalabu.bonecaValue, 1.5F, ForeShinUtils.getValueForIntFloat(9225326, 0.3F));
   }

   private void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(8818070))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8358553), 0.5F, 6.0F, 7)
         .getBonecaAmbalabu(0.05F, 4.0F, 15);
      handleBonecaAmbalabu(bonecaAmbalabu, 40, 61, 0.18F, 7172987);

      for (int i = 0; i < 3; i++) {
         float bonecaAmbalabuValue = bonecaAmbalabu.bonecaValue * (0.2F + i * 0.075F);
         float bonecaAmbalabuValue2 = bonecaAmbalabu.ambalabuBonecaValue * (0.13F + i * 0.02F);
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F - bonecaAmbalabuValue2,
            bonecaAmbalabuValue + 2.5F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
            bonecaAmbalabuValue - 2.5F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F + bonecaAmbalabuValue2,
            bonecaAmbalabuValue + 2.5F,
            1.4F,
            ForeShinUtils.getValueForIntFloat(7172987, 0.75F)
         );
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F - bonecaAmbalabuValue2,
            bonecaAmbalabuValue + 3.8F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
            bonecaAmbalabuValue - 1.2F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F + bonecaAmbalabuValue2,
            bonecaAmbalabuValue + 3.8F,
            1.0F,
            ForeShinUtils.getValueForIntFloat(11449789, 0.3F)
         );
      }

      for (int j = 0; j < 8; j++) {
         float bonecaAmbalabuValue3 = bonecaAmbalabu.bonecaValue * (0.43F + j * 0.07F);
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu.ambalabuBonecaValue * 0.22F,
            bonecaAmbalabuValue3 + 3.0F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
            bonecaAmbalabuValue3 - 2.5F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.78F,
            bonecaAmbalabuValue3 + 3.0F,
            1.4F,
            ForeShinUtils.getValueForIntFloat(7172987, 0.8F)
         );
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu.ambalabuBonecaValue * 0.24F,
            bonecaAmbalabuValue3 + 4.4F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
            bonecaAmbalabuValue3 - 1.0F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.76F,
            bonecaAmbalabuValue3 + 4.4F,
            1.0F,
            ForeShinUtils.getValueForIntFloat(11449789, 0.3F)
         );
      }
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      float shinFootValue = 0.5F - 1.08F * (float)Math.sqrt(getAmount3(getAmounts(32.8F)) - 12.6025F);
      this.body = latheLoft.getClassForStringForeEar("hips", latheLoft.getClassForeEarList(), 0.0F, 10.4F, 0.0F);
      this.armL = latheLoft.getClassForStringForeEar("body", this.body, 0.0F, 10.4F, 0.0F);
      this.earL = latheLoft.getClassForStringForeEar("head", this.armL, 0.0F, 28.4F, 0.4F);
      this.armTrunkForeEar = latheLoft.getClassForStringForeEar("jaw", this.earL, 0.0F, 30.0F, -3.2F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("ear_r", this.earL, 4.75F, 33.2F, 1.7F);
      this.earLeftSection = latheLoft.getClassForStringForeEar("ear_l", this.earL, -4.75F, 33.2F, 1.7F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("eye_r", this.earL, 3.55F, 32.8F, shinFootValue);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("eye_l", this.earL, -3.55F, 32.8F, shinFootValue);
      this.trunkB = latheLoft.getClassForStringForeEar("trunk_a", this.earL, trunkArmAmounts[0], trunkArmAmounts[1], trunkArmAmounts[2]);
      this.trunkC = latheLoft.getClassForStringForeEar("trunk_b", this.trunkB, trunkArmAmounts[6], trunkArmAmounts[7], trunkArmAmounts[8]);
      this.classTrunkArm = latheLoft.getClassForStringForeEar("trunk_c", this.trunkC, trunkArmAmounts[9], trunkArmAmounts[10], trunkArmAmounts[11]);
      this.armTipR = latheLoft.getClassForStringForeEar("arm_r", this.armL, 4.8F, 24.6F, -0.2F);
      this.armTipL = latheLoft.getClassForStringForeEar("arm_l", this.armL, -4.8F, 24.6F, -0.2F);
      float shinFootValue2 = (float)Math.sin(Math.toRadians(8.0)) * 4.5F;
      float shinFootValue3 = 24.6F + (float)Math.cos(Math.toRadians(8.0)) * -4.5F;
      this.foreEar = latheLoft.getClassForStringForeEar("arm_tip_r", this.armTipR, 4.8F + shinFootValue2, shinFootValue3, -0.2F);
      this.armTipLeftSection = latheLoft.getClassForStringForeEar("arm_tip_l", this.armTipL, -4.8F - shinFootValue2, shinFootValue3, -0.2F);
      this.footR = latheLoft.getClassForStringForeEar("leg_r", this.body, 3.9F, 10.4F, 0.0F);
      this.sandalR = latheLoft.getClassForStringForeEar("foot_r", this.footR, 3.9F, 3.4F, 0.0F);
      this.sandalRightSection = latheLoft.getClassForStringForeEar("sandal_r", this.sandalR, 3.9F, 1.4F, -2.4F);
      this.footL = latheLoft.getClassForStringForeEar("leg_l", this.body, -3.9F, 10.4F, 0.0F);
      this.sandalL = latheLoft.getClassForStringForeEar("foot_l", this.footL, -3.9F, 3.4F, 0.0F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("sandal_l", this.sandalL, -3.9F, 1.4F, -2.4F);
      this.hammer = latheLoft.getClassForStringForeEar("clock", this.armL, -16.0F, 35.4F, -0.5F);
      this.classHammer = latheLoft.getClassForStringForeEar("hour_hand", this.hammer, -16.0F, 35.4F, -1.76F);
      this.minuteHandSection = latheLoft.getClassForStringForeEar("minute_hand", this.hammer, -16.0F, 35.4F, -1.76F);
      this.hammerSection = latheLoft.getClassForStringForeEar("hammer", this.hammer, -16.0F, 38.8125F, -0.605F);
      latheLoft.getLatheLoft(this.armTipR);
      float[] shinFootValues = getAmounts();
      float[][] floats = getAmounts(shinFootValues);
      LoftLatheFlag.Region region = latheLoft.getMax(256, 128);
      this.handleBonecaAmbalabu(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(96, 64);
      this.handleBonecaAmbalabu15(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(64, 72);
      this.handleBonecaAmbalabu5(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(48, 64);
      this.handleBonecaAmbalabu(max4.getClassBonecaAmbalabu(), false);
      LoftLatheFlag.Region max5 = latheLoft.getMax(8, 32);
      max5.getClassBonecaAmbalabu().getBonecaAmbalabu(ForeShinUtils.getValueForInt(13220760), ForeShinUtils.getValueForInt(15722194)).getBonecaAmbalabu(0.03F, 4.0F, 21);
      LoftLatheFlag.Region max6 = latheLoft.getMax(192, 96);
      this.handleBonecaAmbalabu(max6.getClassBonecaAmbalabu(), 1.0F, shinFootValues, floats);
      LoftLatheFlag.Region max7 = latheLoft.getMax(192, 96);
      this.handleBonecaAmbalabu(max7.getClassBonecaAmbalabu(), -1.0F, shinFootValues, floats);
      LoftLatheFlag.Region max8 = latheLoft.getMax(48, 96);
      this.handleBonecaAmbalabuForBonecaAmbalabu(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(48, 96);
      this.handleBonecaAmbalabu(max9.getClassBonecaAmbalabu(), 1.0F);
      LoftLatheFlag.Region max10 = latheLoft.getMax(96, 192);
      this.handleBonecaAmbalabu4(max10.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max11 = latheLoft.getMax(128, 96);
      this.handleBonecaAmbalabu7(max11.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max12 = latheLoft.getMax(48, 96);
      this.handleBonecaAmbalabu(max12.getClassBonecaAmbalabu(), -1.0F);
      LoftLatheFlag.Region max13 = latheLoft.getMax(48, 96);
      this.handle(max13.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max14 = latheLoft.getMax(48, 64);
      this.handleBonecaAmbalabu(max14.getClassBonecaAmbalabu(), true);
      LoftLatheFlag.Region max15 = latheLoft.getMax(24, 24);
      this.handleBonecaAmbalabu12(max15.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max16 = latheLoft.getMax(32, 114);
      this.handleBonecaAmbalabu10(max16.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max17 = latheLoft.getMax(64, 32);
      this.handleBonecaAmbalabuForBonecaAmbalabuFloat(max17.getClassBonecaAmbalabu(), 1.0F);
      LoftLatheFlag.Region max18 = latheLoft.getMax(64, 32);
      this.handleBonecaAmbalabuForBonecaAmbalabuFloat(max18.getClassBonecaAmbalabu(), -1.0F);
      LoftLatheFlag.Region max19 = latheLoft.getMax(32, 40);
      this.handleBonecaAmbalabu14(max19.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max20 = latheLoft.getMax(64, 64);
      this.handleBonecaAmbalabu16(max20.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max21 = latheLoft.getMax(16, 48);
      this.handleBonecaAmbalabu8(max21.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max22 = latheLoft.getMax(16, 24);
      this.handleBonecaAmbalabu11(max22.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max23 = latheLoft.getMax(64, 20);
      this.handleBonecaAmbalabu13(max23.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max24 = latheLoft.getMax(64, 16);
      this.handleBonecaAmbalabu3(max24.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max25 = latheLoft.getMax(16, 20);
      this.handleBonecaAmbalabu9(max25.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max26 = latheLoft.getMax(48, 32);
      this.handleBonecaAmbalabu6(max26.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max26);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max14);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max15);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max10);
      ShinBrr classForRegion11 = latheLoft.getClassForRegion(max11);
      ShinBrr classForRegion12 = latheLoft.getClassForRegion(max8);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max19);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max16);
      ShinBrr shinBrr = latheLoft.getMax(2906663);
      ShinBrr shinBrr2 = latheLoft.getMax(8028297);
      ShinBrr[] shinBrrs = new ShinBrr[]{latheLoft.getClassForRegion(max17), latheLoft.getClassForRegion(max18)};
      ShinBrr shinBrr3 = latheLoft.getMax(11830661);
      ShinBrr shinBrr4 = latheLoft.getMax(6171699);
      ShinBrr[] shinBrrs2 = new ShinBrr[]{latheLoft.getClassForRegion(max9), latheLoft.getClassForRegion(max12)};
      ShinBrr classForRegion15 = latheLoft.getClassForRegion(max13);
      ShinBrr classForRegion16 = latheLoft.getClassForRegion(max23);
      ShinBrr classForRegion17 = latheLoft.getClassForRegion(max24);
      ShinBrr classForRegion18 = latheLoft.getClassForRegion(max25);
      ShinBrr classForRegion19 = latheLoft.getClassForRegion(max20);
      ShinBrr classForRegion20 = latheLoft.getClassForRegion(max21);
      ShinBrr shinBrr5 = latheLoft.getMax(9344155);
      ShinBrr classForRegion21 = latheLoft.getClassForRegion(max22);
      ShinBrr shinBrr6 = latheLoft.getMax(1447707);
      latheLoft.getLatheLoftForForeEar(this.armL);
      this.handleAmounts(latheLoft, classForRegion10, shinBrr, classForRegion14);
      latheLoft.getLatheLoftForForeEar(this.earL);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, 0.5F);
      latheLoft.getLatheProfile(shinFootTable, armTrunkTable, 20, 1.1F, 1.1880001F, classForRegion, null, null);
      latheLoft.getDequeArray();
      float shinFootValue4 = 1.08F * getAmounts(35.2F) - 0.5F + 0.16F;
      latheLoft.getLatheLoft(0.0F, 35.2F, -2.0F, 4.4F, 2.6F, shinFootValue4 + -2.0F, 10, classForRegion3);

      for (int i = -1; i <= 1; i += 2) {
         float latheLoftValue = i * 2.0F;
         float shinFootBound = -2.0F - (shinFootValue4 + -2.0F) * (float)Math.sqrt(Math.max(0.0F, 1.0F - getAmount3(0.46153876F) - getAmount3(0.45454544F)));
         float shinFootValue6 = Math.min(getAmount2(2.0F, 36.4F), shinFootBound) - 0.16F;
         latheLoft.getLatheLoft(latheLoftValue, 36.4F, shinFootValue6 + 1.5F, 2.5F, 2.1F, 1.5F, 10, classForRegion4);
      }

      latheLoft.getLatheLoft(0.0F, 32.6F, -4.6500006F, 2.5F, 3.3F, 2.3F, 10, classForRegion5);
      latheLoft.getLoftLathe(0.0F, 29.55F, getAmount2(0.0F, 29.55F) - 0.06F, 1.0F, 0.4F, 8, shinBrr4);

      for (int j = -1; j <= 1; j += 2) {
         float shinFootValue7 = j;
         float[] floats2 = new float[]{shinFootValue7 * 1.8F, 29.5F, -4.3F, shinFootValue7 * 2.6F, 29.1F, -5.6F, shinFootValue7 * 3.3F, 29.6F, -6.6F, shinFootValue7 * 3.7F, 30.7F, -7.1F};
         latheLoft.getLoftPath(floats2, SANDAL_STRAP_RADII, SANDAL_STRAP_RADII, 6, classForRegion9, null, null, 0.0F, 1.0F, 0.0F);
         float[] loftPathValues = new float[]{shinFootValue7 * 1.55F, 29.6F, -3.9F, shinFootValue7 * 2.05F, 29.37F, -4.7F};
         latheLoft.getLoftPath(loftPathValues, new float[]{0.95F, 0.78F}, new float[]{0.85F, 0.7F}, 8, shinBrr2, null, shinBrr2, 0.0F, 1.0F, 0.0F);
      }

      latheLoft.getLatheLoftForForeEar(this.armTrunkForeEar);
      latheLoft.getLatheLoft(0.0F, 29.5F, getAmount2(0.0F, 29.5F) + 0.4F, 1.3F, 0.5F, 0.8F, 8, shinBrr3);
      this.handle(latheLoft, this.classShinFoot, 1.0F, shinFootValue, shinBrrs[0], shinBrr2);
      this.handle(latheLoft, this.shinProgressForeEar, -1.0F, shinFootValue, shinBrrs[1], shinBrr2);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      this.handle(latheLoft, 1.0F, floats, latheLoft.getClassForRegion(max6));
      latheLoft.getLatheLoftForForeEar(this.earLeftSection);
      this.handle(latheLoft, -1.0F, floats, latheLoft.getClassForRegion(max7));
      this.handleTrunkArm(latheLoft, classForRegion6, classForRegion7, classForRegion8);
      handleAmounts(latheLoft, 1.0F, this.armTipR, this.foreEar, classForRegion11, classForRegion13);
      handleAmounts(latheLoft, -1.0F, this.armTipL, this.armTipLeftSection, classForRegion11, classForRegion13);
      latheLoft.getLatheLoftForForeEar(this.footR);
      handleAmounts(latheLoft, 1.0F, classForRegion11, classForRegion12, classForRegion13);
      latheLoft.getLatheLoftForForeEar(this.sandalR);
      handle(latheLoft, 1.0F, classForRegion11);
      latheLoft.getLatheLoftForForeEar(this.sandalRightSection);
      handle(latheLoft, 1.0F, shinBrrs2[0], classForRegion15, classForRegion16, classForRegion17, classForRegion18);
      latheLoft.getLatheLoftForForeEar(this.footL);
      handleAmounts(latheLoft, -1.0F, classForRegion11, classForRegion12, classForRegion13);
      latheLoft.getLatheLoftForForeEar(this.sandalL);
      handle(latheLoft, -1.0F, classForRegion11);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handle(latheLoft, -1.0F, shinBrrs2[1], classForRegion15, classForRegion16, classForRegion17, classForRegion18);
      this.handleHammerAmounts(latheLoft, classForRegion20, shinBrr5, classForRegion19, classForRegion21, shinBrr6);
   }

   private static float getAmountForFloat(float armTrunkValue) {
      return 128.0F * getAmountsArmTrunkTable(armTrunkValue);
   }

   private static float[] getAmounts(float[][] floats, float armTrunkValue) {
      byte armTrunkValue2 = 12;
      int index = 0;

      while (index < armTrunkValue2 - 2 && armTrunkValue < floats[1][index + 1]) {
         index++;
      }

      float armTrunkBound = (armTrunkValue - floats[1][index]) / (floats[1][index + 1] - floats[1][index]);
      float armTrunkFactor = Math.max(0.0F, Math.min(1.0F, armTrunkBound));
      return new float[]{
         floats[0][index] + (floats[0][index + 1] - floats[0][index]) * armTrunkFactor,
         floats[5][index] + (floats[5][index + 1] - floats[5][index]) * armTrunkFactor,
         floats[2][index] + (floats[2][index + 1] - floats[2][index]) * armTrunkFactor
      };
   }

   private static float getAmount(float armTrunkValue, float armTrunkValue2) {
      return 256.0F * LatheLoft.getAmount(armTrunkValue, getAmounts(armTrunkValue2));
   }

   private void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(8818070))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8358553), 0.55F, 7.0F, 71)
         .getBonecaAmbalabu(0.05F, 5.0F, 72)
         .getBonecaAmbalabu(0.03F, 2.0F, 73);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuLimit = (float)i / bonecaAmbalabu.bonecaValue;
         if (bonecaAmbalabuLimit < 0.45F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(10791860, 0.28F * (1.0F - bonecaAmbalabuLimit / 0.45F)));
         }

         if (bonecaAmbalabuLimit > 0.6F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(6317678, 0.08F * (bonecaAmbalabuLimit - 0.6F) / 0.4F));
         }
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 40, 75, 0.2F, 7172987);
   }

   private void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      byte bonecaAmbalabuFactor = 6;

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue * bonecaAmbalabuFactor;
         float bonecaAmbalabuBound = bonecaAmbalabuValue - (float)Math.floor(bonecaAmbalabuValue);
         float bonecaAmbalabuValue2 = 1.0F - Math.min(bonecaAmbalabuBound, 1.0F - bonecaAmbalabuBound) * 2.0F;
         int classValue = bonecaAmbalabuValue2 < 0.35F
            ? ForeShinUtils.getValueForInt(3104298)
            : (
               bonecaAmbalabuValue2 < 0.6F
                  ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(3104298), ForeShinUtils.getValueForInt(5016126), (bonecaAmbalabuValue2 - 0.35F) / 0.25F)
                  : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5016126), ForeShinUtils.getValueForInt(6989903), (bonecaAmbalabuValue2 - 0.6F) / 0.4F)
            );
         bonecaAmbalabu.getClassBonecaAmbalabu(i, 0.0F, i + 1, bonecaAmbalabu.bonecaValue, classValue);
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.06F, 5.0F, 27).getBonecaAmbalabu(ForeShinUtils.getValueForInt(6199879), 0.25F, 6.0F, 29);

      for (int j = 0; j < bonecaAmbalabuFactor; j++) {
         float armTrunkValue = (float)(j * bonecaAmbalabu.ambalabuBonecaValue) / bonecaAmbalabuFactor + 0.5F;

         for (int k = 0; k < 3; k++) {
            float amountForFloat = 12.0F + k * 34.0F + j % 2 * 17.0F;
            if (amountForFloat < bonecaAmbalabu.bonecaValue - 5) {
               handle(bonecaAmbalabu, armTrunkValue, amountForFloat, j * 5 + k, 6.0F, 7.0F, 1.0F);
            }
         }
      }
   }

   @Override
   public String formatTungDin() {
      return "lirili_larila";
   }

   private void handleBonecaAmbalabu8(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15264750), ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12896460), ForeShinUtils.getValueForInt(8291467), 0.4F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.08F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.16F, ForeShinUtils.getValueForIntFloat(16777215, 0.7F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.5F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.56F, ForeShinUtils.getValueForIntFloat(16777215, 0.4F));
   }

   private static float getAmount3(float armTrunkFactor) {
      return armTrunkFactor * armTrunkFactor;
   }

   private void handleBonecaAmbalabuForBonecaAmbalabuFloat(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuFactor) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(1776154));
      float bonecaAmbalabuFactor2 = 0.5F - bonecaAmbalabuFactor * (float)(Math.asin(0.27906975F) / (Math.PI * 2));
      float bonecaAmbalabuFactor3 = (float)(Math.acos(0.0625) / Math.PI);
      bonecaAmbalabu.getClass022(bonecaAmbalabuFactor2 * bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuFactor3 * bonecaAmbalabu.bonecaValue, 2.4F, 3.0F, ForeShinUtils.getValueForInt(16777215));
      bonecaAmbalabu.getClass022((bonecaAmbalabuFactor2 + bonecaAmbalabuFactor * 0.035F) * bonecaAmbalabu.ambalabuBonecaValue, (bonecaAmbalabuFactor3 + 0.12F) * bonecaAmbalabu.bonecaValue, 1.1F, 1.3F, ForeShinUtils.getValueForIntFloat(16777215, 0.5F));
   }

   private void handleHammerAmounts(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4, ShinBrr shinBrr5) {
      latheLoft.getLatheLoftForForeEar(this.hammer);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(-16.0F, 35.4F, -0.5F).getLatheLoft3(1.05F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLatheLoft(hammerTable, HAMMER_PROFILE_DEPTHS, 16, shinBrr, shinBrr, null);
      latheLoft.getDequeArray();
      latheLoft.getLoftLathe(0.0F, 0.0F, -1.08F, 2.95F, 2.95F, 16, shinBrr3);

      for (int i = -1; i <= 1; i += 2) {
         double angle = Math.toRadians(44.0);
         float hammerValue = i * (float)Math.sin(angle) * 3.1F;
         float armTrunkValue = (float)Math.cos(angle) * 3.1F;
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(hammerValue, armTrunkValue, -0.1F).getLatheLoft(-i * 44.0F);
         latheLoft.getLatheLoft(0.22F, 0.22F, -0.3F, 0.95F, 6, shinBrr2, null);
         latheLoft.getLoftLathe(0.0F, 0.85F, 0.0F, 1.75F, 1.5F, 1.75F, 10, 0.0F, 0.5F, shinBrr4);
         latheLoft.getDequeArray();
      }

      for (int j = -1; j <= 1; j += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(j * 2.05F, -2.7F, 0.05F).getLatheLoft(j * 25.0F);
         latheLoft.getLatheLoft(0.0F, 0.62F, -0.75F, 0.0F, 6, shinBrr2, null);
         latheLoft.getDequeArray();
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, 1.1F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLatheLoft(0.25F, 0.25F, 0.0F, 0.8F, 6, shinBrr2, null);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoft(-0.6F, -0.4F, 1.85F, 0.6F, 0.4F, 2.0F, shinBrr, shinBrr, shinBrr, shinBrr, shinBrr, null);
      latheLoft.getLatheLoftForForeEar(this.classHammer);
      latheLoft.getLatheLoft(-0.18F, -0.35F, -1.2F, 0.18F, 1.65F, -1.13F, shinBrr5, null, null, null, null, null);
      latheLoft.getLatheLoftForForeEar(this.minuteHandSection);
      latheLoft.getLatheLoft(-0.11F, -0.45F, -1.27F, 0.11F, 2.45F, -1.2F, shinBrr5, null, null, null, null, null);
      latheLoft.getLatheLoft(-0.24F, -0.24F, -1.33F, 0.24F, 0.24F, -1.26F, shinBrr5, null, null, null, null, null);
      latheLoft.getLatheLoftForForeEar(this.hammerSection);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 3.1F, -0.1F);
      latheLoft.getLatheLoft(0.13F, 0.13F, 0.0F, 1.05F, 6, shinBrr2, null);
      latheLoft.getLatheLoft(0.0F, 1.2F, 0.0F, 0.36F, 8, shinBrr2);
      latheLoft.getDequeArray();
      latheLoft.getDequeArray();
   }

   private static float getAmount2(float armTrunkFactor, float armTrunkValue) {
      return 0.5F - 1.08F * (float)Math.sqrt(Math.max(0.0F, getAmount3(getAmounts(armTrunkValue)) - armTrunkFactor * armTrunkFactor));
   }

   private static float[] getAmountsForFloatArrayFloat(float[][] floats, float armTrunkLimit) {
      byte armTrunkValue = 12;
      int index = 0;

      while (index < armTrunkValue - 2 && armTrunkLimit > floats[5][index + 1]) {
         index++;
      }

      float armTrunkBound = (armTrunkLimit - floats[5][index]) / (floats[5][index + 1] - floats[5][index]);
      float armTrunkFactor = Math.max(0.0F, Math.min(1.0F, armTrunkBound));
      return new float[]{
         floats[0][index] + (floats[0][index + 1] - floats[0][index]) * armTrunkFactor,
         floats[1][index] + (floats[1][index + 1] - floats[1][index]) * armTrunkFactor,
         floats[2][index] + (floats[2][index + 1] - floats[2][index]) * armTrunkFactor
      };
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float amountForFloat, int armTrunkValue) {
      float[] floats = new float[]{52.0F, 18.0F, -14.0F, -48.0F};
      float[] floats2 = new float[]{0.66F, 0.92F, 0.86F, 0.62F};

      for (int i = 0; i < floats.length; i++) {
         double angle = Math.toRadians(floats[i] + (getAmount(armTrunkValue, i + 11) - 0.5F) * 14.0F);
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         float bonecaAmbalabuFactor = floats2[i] * (0.9F + 0.18F * getAmount(armTrunkValue, i + 23)) * (bonecaAmbalabu.ambalabuBonecaValue - 2.0F);
         float bonecaAmbalabuFactor2 = 2.6F;
         float bonecaAmbalabuValue4 = bonecaAmbalabuValue - cos * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue5 = amountForFloat - sin * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue6 = sin * bonecaAmbalabuFactor2;
         float bonecaAmbalabuValue7 = -cos * bonecaAmbalabuFactor2;
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(12103828), bonecaAmbalabuValue + bonecaAmbalabuValue6, amountForFloat + bonecaAmbalabuValue7, bonecaAmbalabuValue4, bonecaAmbalabuValue5, bonecaAmbalabuValue - bonecaAmbalabuValue6, amountForFloat - bonecaAmbalabuValue7);
         bonecaAmbalabu.getClassForIntFloatArray(
            ForeShinUtils.getValueForInt(15987174),
            bonecaAmbalabuValue + bonecaAmbalabuValue6 * 0.55F,
            amountForFloat + bonecaAmbalabuValue7 * 0.55F,
            bonecaAmbalabuValue4 + cos * 1.2F,
            bonecaAmbalabuValue5 + sin * 1.2F,
            bonecaAmbalabuValue - bonecaAmbalabuValue6 * 0.55F,
            amountForFloat - bonecaAmbalabuValue7 * 0.55F
         );
         bonecaAmbalabu.getClass02(bonecaAmbalabuValue4 + cos * 0.6F, bonecaAmbalabuValue5 + sin * 0.6F, bonecaAmbalabuValue4 + cos * 4.0F, bonecaAmbalabuValue5 + sin * 4.0F, 1.0F, ForeShinUtils.getValueForIntFloat(10260080, 0.8F));
      }

      for (int j = 0; j < 9; j++) {
         double angle2 = (Math.PI / 2) + Math.PI * (j + 0.5) / 9.0;
         float bonecaAmbalabuFactor3 = 4.6F + 1.2F * getAmount(j, 93);
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue + (float)Math.cos(angle2) * bonecaAmbalabuFactor3, amountForFloat + (float)Math.sin(angle2) * bonecaAmbalabuFactor3, 1.7F, 1.7F, ForeShinUtils.getValueForInt(15328203));
      }

      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, 5.2F, 5.6F, ForeShinUtils.getValueForInt(14275510));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue - 0.8F, amountForFloat - 0.6F, 3.0F, 3.2F, ForeShinUtils.getValueForInt(15657430));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, 1.8F, 2.0F, ForeShinUtils.getValueForInt(9076578));
   }

   private static void handle(ForeShinScale foreShinScale, ForeEar foreEar, float angle, float angle2) {
      double sin = Math.sin(angle2);
      double cos = Math.cos(angle2);
      double sin2 = Math.sin(angle);
      double x = Math.cos(angle);
      foreShinScale.getAmounts(foreEar, (float)Math.atan2(-sin2 * cos, x), (float)(-Math.asin(sin2 * sin)), (float)Math.atan2(-x * sin, cos));
   }

   @Override
   public float getAmountFloat() {
      return 1.33F;
   }

   @Override
   public String formatNoteBlock() {
      return "block.note_block.flute";
   }

   private static void handle(LatheLoft latheLoft, float armTrunkValue, float armTrunkValue2, float armTrunkValue3, float degrees, float armTrunkValue5, float latheLoftValue, ShinBrr shinBrr) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(armTrunkValue, armTrunkValue2, armTrunkValue3).getLatheLoft4(degrees).getLatheLoft(armTrunkValue5).getLatheLoft3(latheLoftValue).getLatheLoft4(40.0F);
      latheLoft.getLatheLoft(0.0F, -1.0625F, 0.0F, 1.7F, 1.0625F, 0.0F, shinBrr, null, null, null, null, null);
      latheLoft.getDequeArray();
   }

   private static float getAmountsArmTrunkTable(float armTrunkValue) {
      int armTrunkSize = armTrunkTable.length;
      float armTrunkFactor = 1.1439999F;
      float[] floats = new float[armTrunkSize];

      for (int i = 1; i < armTrunkSize; i++) {
         float armTrunkFactor2 = (shinFootTable[i] - shinFootTable[i - 1]) * armTrunkFactor;
         float armTrunkFactor3 = armTrunkTable[i] - armTrunkTable[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(armTrunkFactor2 * armTrunkFactor2 + armTrunkFactor3 * armTrunkFactor3);
      }

      return 1.0F - getAmount(floats, armTrunkTable, armTrunkValue) / floats[armTrunkSize - 1];
   }

   private static void handle(LatheLoft latheLoft, float armTrunkValue, ShinBrr shinBrr) {
      latheLoft.getLatheLoft(armTrunkValue * 3.9F, 2.05F, -3.3F, 2.8F, 1.15F, 4.0F, 8, shinBrr);
   }

   private void handleBonecaAmbalabu9(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14864279), ForeShinUtils.getValueForInt(10389072));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.24F, bonecaAmbalabu.bonecaValue * 0.2F, bonecaAmbalabu.ambalabuBonecaValue * 0.76F, bonecaAmbalabu.bonecaValue * 0.8F, ForeShinUtils.getValueForInt(4861466));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.44F, bonecaAmbalabu.bonecaValue * 0.12F, bonecaAmbalabu.ambalabuBonecaValue * 0.56F, bonecaAmbalabu.bonecaValue * 0.88F, ForeShinUtils.getValueForInt(13218426));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, 1.2F, ForeShinUtils.getValueForIntFloat(16774872, 0.7F));
   }

   private static float getAmount(float armTrunkValue, boolean armTrunkFlag) {
      float armTrunkValue2 = 3.15F + 0.75F * getAmount(armTrunkValue, 0.76F, 0.17F) - 0.32F * getAmount(armTrunkValue, 0.43F, 0.12F);
      return armTrunkFlag ? armTrunkValue2 * 0.92F : armTrunkValue2;
   }

   private static float[] getAmounts(float armTrunkFactor, float armTrunkFactor2, float armTrunkFactor3, float armTrunkFactor4, float armTrunkFactor5, float armTrunkFactor6) {
      byte armTrunkLimit = 18;
      float[] floats = new float[2 * (armTrunkLimit + 1)];

      for (int i = 0; i <= armTrunkLimit; i++) {
         float armTrunkFactor7 = (float)i / armTrunkLimit;
         float armTrunkFactor8 = 1.0F - armTrunkFactor7;
         floats[2 * i] = armTrunkFactor8 * armTrunkFactor8 * armTrunkFactor + 2.0F * armTrunkFactor8 * armTrunkFactor7 * armTrunkFactor3 + armTrunkFactor7 * armTrunkFactor7 * armTrunkFactor5;
         floats[2 * i + 1] = armTrunkFactor8 * armTrunkFactor8 * armTrunkFactor2 + 2.0F * armTrunkFactor8 * armTrunkFactor7 * armTrunkFactor4 + armTrunkFactor7 * armTrunkFactor7 * armTrunkFactor6;
      }

      return floats;
   }

   private static float getAmount4(float armTrunkFactor) {
      armTrunkFactor = armTrunkFactor < 0.0F ? 0.0F : (armTrunkFactor > 1.0F ? 1.0F : armTrunkFactor);
      return armTrunkFactor * armTrunkFactor * (3.0F - 2.0F * armTrunkFactor);
   }

   private void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuLimit, float[] bonecaAmbalabuValues, float[][] floats) {
      int bonecaAmbalabuValue = bonecaAmbalabuLimit > 0.0F ? 0 : 7;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(8818070))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8358553), 0.55F, 7.0F, 33 + bonecaAmbalabuValue)
         .getBonecaAmbalabu(0.05F, 4.0F, 34 + bonecaAmbalabuValue);
      short bonecaAmbalabuFactor = 240;
      float[] floats2 = new float[bonecaAmbalabuFactor];

      for (int i = 0; i < bonecaAmbalabuFactor; i++) {
         double angle = (Math.PI * 2) * i / bonecaAmbalabuFactor;
         floats2[i] = getAmount(bonecaAmbalabuValues, 0.7F, -0.6F, (float)Math.cos(angle), (float)Math.sin(angle));
      }

      int valueForInt = ForeShinUtils.getValueForInt(8818070);
      int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(8818070), ForeShinUtils.getValueForInt(11832974), 0.7F);
      int deltaValue = ForeShinUtils.getValueInt(value2) - ForeShinUtils.getValueInt(valueForInt);
      int deltaValue2 = ForeShinUtils.getValue4(value2) - ForeShinUtils.getValue4(valueForInt);
      int deltaValue3 = ForeShinUtils.getValue5(value2) - ForeShinUtils.getValue5(valueForInt);

      for (int j = 0; j < bonecaAmbalabu.bonecaValue; j++) {
         float[] amountsForFloatArrayFloat = getAmountsForFloatArrayFloat(floats, (j + 0.5F) / bonecaAmbalabu.bonecaValue);

         for (int k = 0; k < bonecaAmbalabu.ambalabuBonecaValue; k++) {
            float[] amountsForFloat = getAmountsForFloat((k + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue);
            float bonecaAmbalabuValue4 = amountsForFloatArrayFloat[0] + bonecaAmbalabuLimit * amountsForFloatArrayFloat[2] * amountsForFloat[0];
            int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(k, j);
            if (amountsForFloat[1] > 0.0F) {
               int foreShinValue = ForeShinUtils.getValue(bonecaAmbalabuForInt, ForeShinUtils.getValueForInt(6251886), 0.14F * Math.min(1.0F, amountsForFloat[1] * 2.0F));
               float x = bonecaAmbalabuValue4 - 0.7F;
               float y = amountsForFloatArrayFloat[1] - -0.6F;
               double atan2 = Math.atan2(y, x);
               int round6 = (int)Math.round(atan2 / (Math.PI * 2) * bonecaAmbalabuFactor);
               int index = (round6 % bonecaAmbalabuFactor + bonecaAmbalabuFactor) % bonecaAmbalabuFactor;
               float bonecaAmbalabuValue7 = (float)Math.hypot(x, y) / floats2[index];
               float bonecaAmbalabuFactor2 = getAmount4((0.85F - bonecaAmbalabuValue7) / 0.13F) * (0.85F + 0.15F * getAmount(k / 3, j / 3 + 501));
               bonecaAmbalabuForInt = ForeShinUtils.getValue(
                  ForeShinUtils.getValueInt(foreShinValue) + Math.round(deltaValue * bonecaAmbalabuFactor2),
                  ForeShinUtils.getValue4(foreShinValue) + Math.round(deltaValue2 * bonecaAmbalabuFactor2),
                  ForeShinUtils.getValue5(foreShinValue) + Math.round(deltaValue3 * bonecaAmbalabuFactor2)
               );
            }

            float foreShinFactor = getAmount4((Math.abs(amountsForFloat[0]) - 0.5F) / 0.5F) * 0.2F + getAmount4((Math.abs(amountsForFloat[0]) - 0.9F) / 0.1F) * 0.45F;
            if (foreShinFactor > 0.0F) {
               bonecaAmbalabuForInt = ForeShinUtils.getValue(bonecaAmbalabuForInt, ForeShinUtils.getValueForInt(5922920), foreShinFactor);
            }

            bonecaAmbalabu.getBonecaAmbalabu(k, j, bonecaAmbalabuForInt);
         }
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 110, 43 + bonecaAmbalabuValue, 0.22F, 7172987);
      float[] floats3 = new float[]{38.0F, -14.0F, -64.0F};

      for (int m = 0; m < floats3.length; m++) {
         double radians = Math.toRadians(floats3[m]);
         int index2 = ((int)Math.round(radians / (Math.PI * 2) * bonecaAmbalabuFactor) % bonecaAmbalabuFactor + bonecaAmbalabuFactor) % bonecaAmbalabuFactor;
         float bonecaAmbalabuFactor3 = floats2[index2] * 0.78F;
         float cos = (float)Math.cos(radians);
         float sin = (float)Math.sin(radians);
         float bonecaAmbalabuValue8 = 0.7F + cos * bonecaAmbalabuFactor3;
         float bonecaAmbalabuValue9 = -0.6F + sin * bonecaAmbalabuFactor3;
         float bonecaAmbalabuValue10 = 0.5F * (0.7F + bonecaAmbalabuValue8) - sin * 0.45F;
         float bonecaAmbalabuValue11 = 0.5F * (-0.6F + bonecaAmbalabuValue9) + cos * 0.45F;
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            bonecaAmbalabuLimit,
            floats,
            getAmounts(0.7F + cos * 0.6F, -0.6F + sin * 0.6F, bonecaAmbalabuValue10, bonecaAmbalabuValue11, bonecaAmbalabuValue8, bonecaAmbalabuValue9),
            true,
            1.2F,
            ForeShinUtils.getValueForIntFloat(8677484, 0.22F)
         );
      }

      float[] floats4 = new float[]{0.9F, -2.5F, -5.9F};

      for (int p = 0; p < floats4.length; p++) {
         float[] amounts4 = getAmounts(floats, floats4[p], 0.45F, 0.12F, p * 1.9F);
         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuLimit, floats, amounts4, true, 1.5F, ForeShinUtils.getValueForIntFloat(7172987, p == 0 ? 0.3F : 0.42F));

         for (int q = 1; q < amounts4.length; q += 2) {
            amounts4[q] -= 0.17F;
         }

         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuLimit, floats, amounts4, true, 1.1F, ForeShinUtils.getValueForIntFloat(11712704, 0.22F));
      }

      float[] floats5 = new float[]{1.4F, -1.2F, -4.0F, -6.8F};

      for (int r = 0; r < floats5.length; r++) {
         float[] amounts5 = getAmounts(floats, floats5[r], 0.4F, 0.14F, 0.7F + r * 2.3F);
         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuLimit, floats, amounts5, false, 1.5F, ForeShinUtils.getValueForIntFloat(7172987, 0.34F));

         for (int s = 1; s < amounts5.length; s += 2) {
            amounts5[s] -= 0.17F;
         }

         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuLimit, floats, amounts5, false, 1.1F, ForeShinUtils.getValueForIntFloat(10923445, 0.25F));
      }

      float[] floats6 = new float[]{30.0F, 2.0F, -30.0F, -58.0F};

      for (int t = 0; t < floats6.length; t++) {
         double radians4 = Math.toRadians(floats6[t]);
         int index3 = ((int)Math.round(radians4 / (Math.PI * 2) * bonecaAmbalabuFactor) % bonecaAmbalabuFactor + bonecaAmbalabuFactor) % bonecaAmbalabuFactor;
         float bonecaAmbalabuFactor4 = floats2[index3] * 0.7F;
         float cos2 = (float)Math.cos(radians4);
         float sin2 = (float)Math.sin(radians4);
         float bonecaAmbalabuValue12 = 0.7F + cos2 * bonecaAmbalabuFactor4;
         float bonecaAmbalabuValue13 = -0.6F + sin2 * bonecaAmbalabuFactor4;
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            bonecaAmbalabuLimit,
            floats,
            getAmounts(0.7F + cos2 * 0.5F, -0.6F + sin2 * 0.5F, 0.5F * (0.7F + bonecaAmbalabuValue12) - sin2 * 0.3F, 0.5F * (-0.6F + bonecaAmbalabuValue13) + cos2 * 0.3F, bonecaAmbalabuValue12, bonecaAmbalabuValue13),
            false,
            1.6F,
            ForeShinUtils.getValueForIntFloat(7238781, 0.38F)
         );
      }
   }

   private static float[] getAmountsForFloatArrayInt(float[] armTrunkValues, int armTrunkValue, int armTrunkValue2) {
      float[] floats = new float[armTrunkValue2 - armTrunkValue + 1];
      System.arraycopy(armTrunkValues, armTrunkValue, floats, 0, floats.length);
      return floats;
   }

   private static void handleAmounts(LatheLoft latheLoft, float armTrunkFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(armTrunkFactor * 3.9F, 0.0F, 0.0F);
      latheLoft.getLoftLathe(0.0F, 3.4F, 0.0F, BAND_OUTER_RADII[0], 2.0F, BAND_OUTER_RADII[0], 8, 0.5F, 1.0F, shinBrr);
      int armTrunkSize = BAND_HEIGHTS.length;
      byte armTrunkDivisor = 6;
      float armTrunkFactor2 = (float)Math.tan(Math.PI / armTrunkDivisor) * 1.12F;

      for (int i = 0; i < armTrunkDivisor; i++) {
         double angle = Math.toRadians(30.0F + 360.0F / armTrunkDivisor * i);
         float sin = (float)Math.sin(angle);
         float cos = -((float)Math.cos(angle));
         float[] floats = new float[3 * armTrunkSize];
         float[] floats2 = new float[armTrunkSize];
         float[] crownForeValues = new float[armTrunkSize];

         for (int j = 0; j < armTrunkSize; j++) {
            float armTrunkFactor3 = BAND_OUTER_RADII[j] - BAND_INNER_RADII[j];
            floats[3 * j] = sin * armTrunkFactor3;
            floats[3 * j + 1] = BAND_HEIGHTS[j];
            floats[3 * j + 2] = cos * armTrunkFactor3;
            crownForeValues[j] = BAND_INNER_RADII[j];
            floats2[j] = armTrunkFactor3 * armTrunkFactor2 + 0.12F;
         }

         latheLoft.getLoftPath(floats, floats2, crownForeValues, 6, shinBrr2, null, null, sin, 0.0F, cos);
      }

      float[] floats4 = new float[]{5.4F, 8.6F};
      float[] floats5 = new float[]{90.0F, 30.0F};

      for (int k = 0; k < floats4.length; k++) {
         float armTrunkFactor4 = getAmount(BAND_OUTER_RADII, BAND_HEIGHTS, floats4[k]) - 0.1F;
         double radians = Math.toRadians(armTrunkFactor * floats5[k]);
         handle(
            latheLoft,
            (float)Math.sin(radians) * armTrunkFactor4,
            floats4[k],
            -((float)Math.cos(radians)) * armTrunkFactor4,
            (float)(-Math.toDegrees(radians)),
            k == 0 ? -20.0F : 20.0F,
            0.75F,
            shinBrr3
         );
      }

      latheLoft.getDequeArray();
   }

   private static float getAmount5(float angle) {
      return 6.8F * (float)Math.sin(angle) - 1.4F * (1.0F - (float)Math.cos(angle));
   }

   private void handleBonecaAmbalabu10(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuDivisor = 6.025F;

      for (int i = 0; i < 2; i++) {
         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabu.ambalabuBonecaValue - 0.5F, bonecaAmbalabu.bonecaValue * (1.0625F + i * 3.9F) / bonecaAmbalabuDivisor, i + 1);
      }
   }

   private void handleBonecaAmbalabu11(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14475492), ForeShinUtils.getValueForInt(11844289));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.12F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.22F, ForeShinUtils.getValueForIntFloat(16777215, 0.55F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.78F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.84F, ForeShinUtils.getValueForIntFloat(15659508, 0.5F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.84F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(7765382));
   }

   private void handle(LatheLoft latheLoft, float armTrunkFactor, float[][] floats, ShinBrr shinBrr) {
      byte armTrunkLimit = 12;
      float[] loftPathValues = new float[3 * armTrunkLimit];

      for (int i = 0; i < armTrunkLimit; i++) {
         loftPathValues[3 * i] = armTrunkFactor * floats[0][i];
         loftPathValues[3 * i + 1] = floats[1][i];
         loftPathValues[3 * i + 2] = floats[4][i];
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoft(armTrunkFactor * 4.75F, 33.2F, 1.7F).getLatheLoft4(-armTrunkFactor * 25.0F);
      float[] crownForeValues = new float[armTrunkLimit];

      for (int j = 0; j < armTrunkLimit; j++) {
         crownForeValues[j] = floats[2][j] / getMax();
      }

      latheLoft.getLoftPath(loftPathValues, crownForeValues, floats[3], 6, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getDequeArray();
   }

   private void handleBonecaAmbalabu12(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13081242), ForeShinUtils.getValueForInt(8818070), 0.5F))
         .getBonecaAmbalabu(0.05F, 3.0F, 19);
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.5F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.5F, 2.0F, ForeShinUtils.getValueForIntFloat(9073780, 0.7F));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.34F, bonecaAmbalabu.bonecaValue * 0.52F, 2.2F, 2.8F, ForeShinUtils.getValueForInt(4863030));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.66F, bonecaAmbalabu.bonecaValue * 0.52F, 2.2F, 2.8F, ForeShinUtils.getValueForInt(4863030));
   }

   private void handleBonecaAmbalabu13(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuValue = 0.53571427F * bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(12159835))
         .getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(8280630, 0.7F), 5.0F, 0.5F, 53)
         .getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(14531466, 0.6F), 4.0F, 0.45F, 54)
         .getBonecaAmbalabu(0.05F, 3.0F, 55);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuValue * 0.18F, ForeShinUtils.getValueForInt(14202243));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue * 0.18F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuValue * 0.3F, ForeShinUtils.getValueForIntFloat(8017460, 0.45F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue * 0.8F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuValue, ForeShinUtils.getValueForIntFloat(7031340, 0.45F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(4074012));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuValue + (bonecaAmbalabu.bonecaValue - bonecaAmbalabuValue) * 0.22F, ForeShinUtils.getValueForInt(5914411));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue - (bonecaAmbalabu.bonecaValue - bonecaAmbalabuValue) * 0.25F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(2891027));
   }

   private static float getAmount(int armTrunkValue, int i) {
      int armTrunkValue3 = armTrunkValue * 374761393 + i * 668265263 + 1540483477;
      int armTrunkValue4 = (armTrunkValue3 ^ armTrunkValue3 >>> 13) * 1274126177;
      int armTrunkValue5 = armTrunkValue4 ^ armTrunkValue4 >>> 16;
      return (armTrunkValue5 & 16777215) / 1.6777215E7F;
   }

   private void handleBonecaAmbalabu14(BonecaAmbalabu bonecaAmbalabu) {
      handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabu.ambalabuBonecaValue - 0.5F, bonecaAmbalabu.bonecaValue * 0.5F, 0);
   }

   private static float getAmountForInt(int armTrunkValue) {
      return (float)((Math.PI * 2) * (armTrunkValue / 6.0 - 0.5));
   }

   private void handleTrunkArm(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float[] pointsSpline = LatheLoft.getPointsSpline(2, trunkArmAmounts);
      int trunkArmValue = LatheLoft.getValue(pointsSpline);
      float[] trunkArmValues = LatheLoft.getAmounts(trunkArmValue, 2.3F, 1.85F, 1.4F, 1.1F, 0.9F, 0.72F);
      latheLoft.getLatheLoftForForeEar(this.trunkB);
      latheLoft.getLoftPath(getAmounts(pointsSpline, 0, 4), getAmountsForFloatArrayInt(trunkArmValues, 0, 4), getAmountsForFloatArrayInt(trunkArmValues, 0, 4), 10, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoftForForeEar(this.trunkC);
      latheLoft.getLatheLoft(pointsSpline[12], pointsSpline[13], pointsSpline[14], trunkArmValues[4], 8, shinBrr);
      latheLoft.getLoftPath(getAmounts(pointsSpline, 4, 6), getAmountsForFloatArrayInt(trunkArmValues, 4, 6), getAmountsForFloatArrayInt(trunkArmValues, 4, 6), 10, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoftForForeEar(this.classTrunkArm);
      latheLoft.getLatheLoft(pointsSpline[18], pointsSpline[19], pointsSpline[20], trunkArmValues[6], 8, shinBrr);
      latheLoft.getLoftPath(getAmounts(pointsSpline, 6, trunkArmValue - 1), getAmountsForFloatArrayInt(trunkArmValues, 6, trunkArmValue - 1), getAmountsForFloatArrayInt(trunkArmValues, 6, trunkArmValue - 1), 10, shinBrr2, null, shinBrr3, 0.0F, 0.0F, -1.0F);
   }

   private static float[] getAmounts(float[][] floats, float armTrunkValue, float armTrunkValue2, float armTrunkFactor, float armTrunkValue3) {
      float[] armTrunkValues = getAmounts(floats, armTrunkValue);
      float armTrunkValue4 = armTrunkValues[0] - armTrunkValues[2] + armTrunkValue2;
      float armTrunkValue5 = armTrunkValues[0] + armTrunkValues[2] - armTrunkValue2;
      byte armTrunkDivisor = 16;
      float[] floats2 = new float[2 * (armTrunkDivisor + 1)];

      for (int i = 0; i <= armTrunkDivisor; i++) {
         float armTrunkValue6 = armTrunkValue4 + (armTrunkValue5 - armTrunkValue4) * i / armTrunkDivisor;
         floats2[2 * i] = armTrunkValue6;
         floats2[2 * i + 1] = armTrunkValue + armTrunkFactor * (float)Math.sin(armTrunkValue6 * 2.1F + armTrunkValue3) + 0.18F * (float)Math.sin(i * Math.PI / armTrunkDivisor);
      }

      return floats2;
   }

   private void handle(LatheLoft latheLoft, ForeEar foreEar, float armTrunkValue, float shinFootValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      latheLoft.getLatheLoftForForeEar(foreEar);
      float degrees = LatheLoft.getAmountForFloat(armTrunkValue * 3.55F, getAmounts(32.8F)) * 0.92F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(armTrunkValue * 3.55F, 32.8F, shinFootValue + 0.06F).getLatheLoft4(degrees).getLatheLoft(-armTrunkValue * 8.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.86F, 0.64F, 0.48F, 8, shinBrr);
      latheLoft.getLoftLathe(0.0F, 0.05F, 0.04F, 1.05F, 0.8F, 0.62F, 8, 0.0F, 0.42F, shinBrr2);
      latheLoft.getDequeArray();
   }

   @Override
   public int getValue() {
      return 3;
   }

   private static float[] getAmounts(float armTrunkFactor, float armTrunkValue) {
      byte armTrunkLimit = 14;
      float[] floats = new float[2 * armTrunkLimit];
      float armTrunkValue2 = -1.95F;
      float armTrunkFactor2 = 6.3500004F - armTrunkValue;

      for (int i = 0; i < armTrunkLimit; i++) {
         double angle = (Math.PI * 2) * i / armTrunkLimit;
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         float armTrunkValue3 = 0.5F * (1.0F - cos);
         float max3 = Math.max(0.0F, getAmount(armTrunkValue3, sin * armTrunkFactor < 0.0F) - armTrunkValue);
         floats[2 * i] = Math.signum(sin) * max3 * (float)Math.pow(Math.abs(sin), 0.5);
         floats[2 * i + 1] = armTrunkValue2 + armTrunkFactor2 * cos;
      }

      return floats;
   }

   private void handleAmounts(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      int armTrunkSize = TRUNK_RING_HEIGHTS.length;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, TRUNK_RING_HEIGHTS[0] + 0.1F, 0.0F).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, table[0] - 0.4F, table[0] - 0.4F, 8, shinBrr2);
      latheLoft.getDequeArray();
      float armTrunkFactor = (float)Math.tan(Math.PI / 12) * 1.12F;

      for (int i = 0; i < 12; i++) {
         double angle = Math.toRadians(30.0F * i);
         float sin = (float)Math.sin(angle);
         float cos = -((float)Math.cos(angle));
         float[] floats = new float[3 * armTrunkSize];
         float[] floats2 = new float[armTrunkSize];
         float[] crownForeValues = new float[armTrunkSize];

         for (int j = 0; j < armTrunkSize; j++) {
            float armTrunkFactor2 = table[j] - TRUNK_RING_RADII[j];
            floats[3 * j] = sin * armTrunkFactor2;
            floats[3 * j + 1] = TRUNK_RING_HEIGHTS[j];
            floats[3 * j + 2] = cos * armTrunkFactor2;
            crownForeValues[j] = TRUNK_RING_RADII[j];
            floats2[j] = armTrunkFactor2 * armTrunkFactor + 0.15F;
         }

         latheLoft.getLoftPath(floats, floats2, crownForeValues, 6, shinBrr, null, null, sin, 0.0F, cos);
         float[] armTrunkValues = i % 2 == 0 ? EVEN_RING_HEIGHTS : ODD_RING_HEIGHTS;

         for (float armTrunkValue : armTrunkValues) {
            float armTrunkValue2 = armTrunkValue - 1.95F;
            float armTrunkValue3 = armTrunkValue + 1.95F;
            float armTrunkValue4 = getAmount(table, TRUNK_RING_HEIGHTS, armTrunkValue2);
            float armTrunkValue5 = getAmount(table, TRUNK_RING_HEIGHTS, armTrunkValue3);
            float armTrunkFactor3 = 0.5F * (armTrunkValue4 + armTrunkValue5) - 0.1F;
            float degrees = (float)(-Math.toDegrees(Math.atan((armTrunkValue5 - armTrunkValue4) / 3.9F)));
            handle(latheLoft, sin * armTrunkFactor3, armTrunkValue, cos * armTrunkFactor3, (float)(-Math.toDegrees(angle)), degrees, shinBrr3);
         }
      }
   }

   private static float[][] getAmounts(float[] armTrunkValues) {
      int armTrunkLimit = 12;
      float armTrunkBound = -Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;

      for (int i = 1; i < armTrunkValues.length; i += 2) {
         armTrunkBound = Math.max(armTrunkBound, armTrunkValues[i]);
         maxValue = Math.min(maxValue, armTrunkValues[i]);
      }

      float[][] floats = new float[6][armTrunkLimit];

      for (int j = 0; j < armTrunkLimit; j++) {
         float armTrunkFactor = 0.012F + 0.488F * (1.0F - (float)Math.cos(Math.PI * j / (armTrunkLimit - 1)));
         float armTrunkValue = armTrunkBound - (armTrunkBound - maxValue) * armTrunkFactor;
         float[] amounts2 = getAmounts(armTrunkValues, armTrunkValue);
         floats[0][j] = 0.5F * (amounts2[0] + amounts2[1]);
         floats[1][j] = armTrunkValue;
         floats[2][j] = Math.max(0.03F, 0.5F * (amounts2[1] - amounts2[0]));
         floats[3][j] = 0.45F * (float)Math.sqrt(Math.min(1.0F, floats[2][j] / 1.8F));
         floats[4][j] = -1.1F * armTrunkFactor * armTrunkFactor;
      }

      for (int k = 1; k < armTrunkLimit; k++) {
         float armTrunkFactor2 = floats[0][k] - floats[0][k - 1];
         float armTrunkFactor3 = floats[1][k] - floats[1][k - 1];
         float armTrunkFactor4 = floats[4][k] - floats[4][k - 1];
         floats[5][k] = floats[5][k - 1] + (float)Math.sqrt(armTrunkFactor2 * armTrunkFactor2 + armTrunkFactor3 * armTrunkFactor3 + armTrunkFactor4 * armTrunkFactor4);
      }

      for (int m = 1; m < armTrunkLimit; m++) {
         floats[5][m] = floats[5][m] / floats[5][armTrunkLimit - 1];
      }

      return floats;
   }

   private void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(8818070))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8358553), 0.5F, 5.0F, bonecaAmbalabuFlag ? 9 : 8)
         .getBonecaAmbalabu(0.05F, 4.0F, bonecaAmbalabuFlag ? 17 : 16);
      int bonecaAmbalabuLimit = bonecaAmbalabuFlag ? 8 : 7;

      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float bonecaAmbalabuValue = bonecaAmbalabu.bonecaValue * (i + 0.5F) / bonecaAmbalabuLimit;
         float[] floats = new float[9];
         float[] floats2 = new float[9];

         for (int j = 0; j <= 8; j++) {
            floats[j] = bonecaAmbalabu.ambalabuBonecaValue * j / 8.0F;
            floats2[j] = bonecaAmbalabuValue + 1.2F * (float)Math.sin(j * 1.3F + i * 2.1F);
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 1.5F, ForeShinUtils.getValueForIntFloat(7172987, 0.85F));

         for (int k = 0; k <= 8; k++) {
            floats2[k]++;
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 1.0F, ForeShinUtils.getValueForIntFloat(11449789, 0.35F));
      }

      if (bonecaAmbalabuFlag) {
         for (int m = 0; m < bonecaAmbalabu.bonecaValue; m++) {
            float bonecaAmbalabuLimit2 = ((float)m / bonecaAmbalabu.bonecaValue - 0.72F) / 0.28F;
            if (bonecaAmbalabuLimit2 > 0.0F) {
               bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, m, bonecaAmbalabu.ambalabuBonecaValue, m + 1, ForeShinUtils.getValueForIntFloat(13081242, Math.min(1.0F, bonecaAmbalabuLimit2 * 1.1F) * 0.5F));
            }
         }
      }
   }

   private void handleBonecaAmbalabu15(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(8818070))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(8358553), 0.55F, 9.0F, 63)
         .getBonecaAmbalabu(0.05F, 6.0F, 64)
         .getBonecaAmbalabu(0.03F, 2.0F, 65);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = 35.2F + 2.6F * (float)Math.cos(Math.PI * (i + 0.5F) / bonecaAmbalabu.bonecaValue);
         float bonecaAmbalabuLimit = getAmount(getAmountsArmTrunkTable(bonecaAmbalabuValue));
         if (bonecaAmbalabuLimit > 0.0F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(10791860, bonecaAmbalabuLimit));
         }
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 60, 67, 0.2F, 7172987);
      bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.04F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.46F, 5.0F, ForeShinUtils.getValueForIntFloat(7172987, 0.3F));
      bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.06F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.42F, 2.0F, ForeShinUtils.getValueForIntFloat(5264990, 0.5F));
      bonecaAmbalabu.getClass02(
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F + 3.0F,
         bonecaAmbalabu.bonecaValue * 0.08F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F + 3.0F,
         bonecaAmbalabu.bonecaValue * 0.4F,
         1.2F,
         ForeShinUtils.getValueForIntFloat(11449789, 0.25F)
      );

      for (int j = 0; j < 3; j++) {
         float bonecaAmbalabuValue2 = bonecaAmbalabu.bonecaValue * (0.6F + j * 0.09F);
         float bonecaAmbalabuValue3 = bonecaAmbalabu.ambalabuBonecaValue * (0.1F - j * 0.015F);
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F - bonecaAmbalabuValue3,
            bonecaAmbalabuValue2 + 2.0F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
            bonecaAmbalabuValue2 - 2.5F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F + bonecaAmbalabuValue3,
            bonecaAmbalabuValue2 + 2.0F,
            1.3F,
            ForeShinUtils.getValueForIntFloat(7172987, 0.7F)
         );
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F - bonecaAmbalabuValue3,
            bonecaAmbalabuValue2 + 3.3F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
            bonecaAmbalabuValue2 - 1.2F,
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F + bonecaAmbalabuValue3,
            bonecaAmbalabuValue2 + 3.3F,
            1.0F,
            ForeShinUtils.getValueForIntFloat(11449789, 0.3F)
         );
      }
   }

   private static float getAmountForFloatFlag(float armTrunkLimit, boolean armTrunkFlag) {
      for (int i = 0; i < 6; i++) {
         float cos = (float)Math.cos(0.5F * (getAmountForInt(i) + getAmountForInt(i + 1)));
         if (armTrunkFlag ? !(cos < 0.001F) : !(cos > -0.001F)) {
            float armTrunkBound = getAmount(i);
            float hashHuntBound = getAmount(i + 1);
            if (!(Math.abs(hashHuntBound - armTrunkBound) < 1.0E-5F) && !(armTrunkLimit < Math.min(armTrunkBound, hashHuntBound)) && !(armTrunkLimit > Math.max(armTrunkBound, hashHuntBound))) {
               float armTrunkValue = (i + (armTrunkLimit - armTrunkBound) / (hashHuntBound - armTrunkBound)) / 6.0F;
               return !armTrunkFlag && armTrunkValue < 0.5F ? armTrunkValue + 1.0F : armTrunkValue;
            }
         }
      }

      return armTrunkFlag ? (armTrunkLimit < 0.0F ? 0.25F : 0.75F) : (armTrunkLimit > 0.0F ? 0.75F : 1.25F);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuLimit, int bonecaAmbalabuValue, float bonecaAmbalabuValue2, int foreShinValue) {
      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float classFactor = getAmount(i, bonecaAmbalabuValue) * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue5 = getAmount(i, bonecaAmbalabuValue + 1) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuFactor = 3.0F + getAmount(i, bonecaAmbalabuValue + 2) * 6.0F;
         float angle = (getAmount(i, bonecaAmbalabuValue + 3) - 0.5F) * 1.4F;
         float bonecaAmbalabuValue6 = classFactor + (float)Math.cos(angle) * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue7 = bonecaAmbalabuValue5 + (float)Math.sin(angle) * bonecaAmbalabuFactor;
         bonecaAmbalabu.getClassForFloatInt(
            classFactor,
            bonecaAmbalabuValue5,
            (classFactor + bonecaAmbalabuValue6) * 0.5F,
            (bonecaAmbalabuValue5 + bonecaAmbalabuValue7) * 0.5F + (getAmount(i, bonecaAmbalabuValue + 4) - 0.5F) * 3.0F,
            bonecaAmbalabuValue6,
            bonecaAmbalabuValue7,
            0.9F,
            ForeShinUtils.getValueForIntFloat(foreShinValue, bonecaAmbalabuValue2)
         );
      }
   }

   private void handleBonecaAmbalabu16(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16052712));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.5F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.5F, ForeShinUtils.getValueForInt(16052712));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.5F, bonecaAmbalabu.ambalabuBonecaValue * 0.49F, bonecaAmbalabu.bonecaValue * 0.49F, 2.2F, ForeShinUtils.getValueForInt(7238520));

      for (int i = 0; i < 12; i++) {
         double angle = i * Math.PI / 6.0;
         float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue * (i % 3 == 0 ? 0.33F : 0.38F);
         float bonecaAmbalabuFactor2 = bonecaAmbalabu.ambalabuBonecaValue * 0.44F;
         float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
         float bonecaAmbalabuValue2 = bonecaAmbalabu.bonecaValue * 0.5F;
         bonecaAmbalabu.getClass02(
            bonecaAmbalabuValue + (float)Math.sin(angle) * bonecaAmbalabuFactor,
            bonecaAmbalabuValue2 - (float)Math.cos(angle) * bonecaAmbalabuFactor,
            bonecaAmbalabuValue + (float)Math.sin(angle) * bonecaAmbalabuFactor2,
            bonecaAmbalabuValue2 - (float)Math.cos(angle) * bonecaAmbalabuFactor2,
            i % 3 == 0 ? 3.2F : 1.6F,
            ForeShinUtils.getValueForInt(1974050)
         );
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.02F, 4.0F, 55);
   }
}
