package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class LegBurbaloni extends WarmBrainrotBase {
   private ForeEar shinFootForeEar;
   private ForeEar head;
   private ForeEar classShinFoot;
   private ForeEar classShinProgress;
   private static final float legBurbaloniAmount = getAmountForFloat(2.47F);
   private ForeEar shinProgressForeEar;
   private ForeEar legBurbaloniForeEar;
   private ForeEar foreEar;
   private ForeEar legBl;
   private static final float BASE_HEIGHT_OFFSET = -7.7F * (float)Math.cos(2.47F);
   private static final float headShellAmount = 1.6F - LegBurbaloni.BASE_HEIGHT_OFFSET;
   private static final float HEAD_SHELL_OFFSET = LegBurbaloni.headShellAmount;
   private static final float[] shinFootTable = new float[]{2.47F, 2.72F, 2.82F, 3.06F};
   private static final float[] legBurbaloniTable = new float[]{-3.4F, -1.5F, -0.12F, 0.12F};
   private static final float[] table = new float[]{3.06F, 3.85F, 4.0F, legBurbaloniAmount};
   private ForeEar legBackLeftSection;
   private static final float[] LAYER_THICKNESSES = new float[]{0.12F, 0.16F, 0.04F, 0.0F};
   private ForeEar legFrontRightSection;
   private ForeEar eyeRightSection;
   private static final int[] UNDERSIDE_COLORS = new int[]{1971212, 2365712, 3811868, 16052194};
   private ForeEar earLeftSection;
   private static final int[] TOPSIDE_COLORS = new int[]{16052194, 15590866, 3876887, 4862496};
   private ForeEar earRightSection;
   private ForeEar lidRightSection;

   private static void handle(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(10512960));
      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 4.0F, 41);
      handleBonecaAmbalabu(bonecaAmbalabu, 90, 43, 0.0F, 4.0F, 2.0F, 11831904);
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.legBl = latheLoft.getClassForStringForeEar("base", latheLoft.getClassForeEarList(), 0.0F, 0.0F, 1.6F);
      this.head = latheLoft.getClassForStringForeEar("shell", this.legBl, 0.0F, 7.9F, 1.6F);
      this.foreEar = latheLoft.getClassForStringForeEar("head", this.head, 0.0F, 7.9F, headShellAmount + 0.3F);
      this.legBurbaloniForeEar = this.getAmount(latheLoft, "jaw", 0.0F, 6.55F, -6.6F);
      this.eyeRightSection = this.getAmount(latheLoft, "eye_r", 1.74F, 10.2F, -7.1F);
      this.shinFootForeEar = this.getAmount(latheLoft, "eye_l", -1.74F, 10.2F, -7.1F);
      this.lidRightSection = this.getAmount(latheLoft, "lid_r", 1.74F, 10.8F, -7.1F);
      this.shinProgressForeEar = this.getAmount(latheLoft, "lid_l", -1.74F, 10.8F, -7.1F);
      this.earRightSection = this.getAmount(latheLoft, "ear_r", 1.5F, 10.55F, -5.4F);
      this.earLeftSection = this.getAmount(latheLoft, "ear_l", -1.5F, 10.55F, -5.4F);
      this.legFrontRightSection = latheLoft.getClassForStringForeEar("leg_fr", this.legBl, 2.4F, 3.4F, -1.1999999F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("leg_fl", this.legBl, -2.4F, 3.4F, -1.1999999F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("leg_br", this.legBl, 2.4F, 3.4F, 4.1F);
      this.legBackLeftSection = latheLoft.getClassForStringForeEar("leg_bl", this.legBl, -2.4F, 3.4F, 4.1F);
      latheLoft.getLatheLoft(this.legFrontRightSection);
      LoftLatheFlag.Region region = latheLoft.getMax(512, 256);
      handleBonecaAmbalabuForBonecaAmbalabu(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(448, 224);
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(256, 192);
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu(), shinFootTable, legBurbaloniTable, UNDERSIDE_COLORS, false);
      LoftLatheFlag.Region max4 = latheLoft.getMax(512, 64);
      handleBonecaAmbalabu(max4.getClassBonecaAmbalabu(), table, LAYER_THICKNESSES, TOPSIDE_COLORS, true);
      LoftLatheFlag.Region max5 = latheLoft.getMax(64, 64);
      handle(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(96, 48);
      handleBonecaAmbalabu(max6.getClassBonecaAmbalabu(), 1.0F);
      LoftLatheFlag.Region max7 = latheLoft.getMax(96, 48);
      handleBonecaAmbalabu(max7.getClassBonecaAmbalabu(), -1.0F);
      LoftLatheFlag.Region max8 = latheLoft.getMax(48, 48);
      handleBonecaAmbalabu4(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(48, 96);
      handleBonecaAmbalabu5(max9.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max10 = latheLoft.getMax(32, 48);
      handleBonecaAmbalabu3(max10.getClassBonecaAmbalabu());
      latheLoft.getLatheLoftForForeEar(this.head);
      handleAmountsAmount(latheLoft, latheLoft.getClassForRegion(region), latheLoft.getClassForRegion(max2), latheLoft.getClassForRegion(max3), latheLoft.getClassForRegion(max4));
      handle(latheLoft);
      handleAmount(latheLoft, new ShinBrr[]{latheLoft.getMax(3811094), latheLoft.getMax(4862496), latheLoft.getMax(9069632)});
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, -0.5999999F, 0.3F).getLatheLoft(0.0F, 8.5F, HEAD_SHELL_OFFSET).getLatheLoftLatheLoftAmountLoft(-5.0F).getLatheLoft(0.0F, -8.5F, -HEAD_SHELL_OFFSET);
      latheLoft.getLatheLoftForForeEar(this.foreEar);
      ShinBrr classForRegion = latheLoft.getClassForRegion(max5);
      handleShellHead(latheLoft, classForRegion, latheLoft.getClassForRegion(max6), latheLoft.getClassForRegion(max7), latheLoft.getClassForRegion(max8), latheLoft.getClassForRegion(max9), latheLoft.getMax(10512960));
      latheLoft.getLatheLoftForForeEar(this.legBurbaloniForeEar);
      latheLoft.getLatheLoft(-1.0F, 5.85F, -8.7F, 1.0F, 6.75F, -5.2F, 0.5F, 2, latheLoft.getMax(9331264));
      latheLoft.getLatheLoft(0.0F, 6.85F, -7.75F, 0.68F, 0.1F, 0.9F, 8, latheLoft.getMax(11824228));
      latheLoft.getLatheLoftForForeEar(this.eyeRightSection);
      handle(latheLoft, 1.0F);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handle(latheLoft, -1.0F);
      latheLoft.getLatheLoftForForeEar(this.lidRightSection);
      handle3(latheLoft, 1.0F);
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      handle3(latheLoft, -1.0F);
      latheLoft.getLatheLoftForForeEar(this.earRightSection);
      handleForLatheLoftFloat(latheLoft, 1.0F);
      latheLoft.getLatheLoftForForeEar(this.earLeftSection);
      handleForLatheLoftFloat(latheLoft, -1.0F);
      latheLoft.getDequeArray();
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max10);
      ShinBrr shinBrr = latheLoft.getMax(5913640);
      ShinBrr shinBrr2 = latheLoft.getMax(2827296);
      latheLoft.getLatheLoftForForeEar(this.legFrontRightSection);
      handle(latheLoft, 2.4F, -1.1999999F, 4, classForRegion3, shinBrr, shinBrr2);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      handle(latheLoft, -2.4F, -1.1999999F, 4, classForRegion3, shinBrr, shinBrr2);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      handle(latheLoft, 2.4F, 4.1F, 3, classForRegion3, shinBrr, shinBrr2);
      latheLoft.getLatheLoftForForeEar(this.legBackLeftSection);
      handle(latheLoft, -2.4F, 4.1F, 3, classForRegion3, shinBrr, shinBrr2);
   }

   @Override
   public int getValue() {
      return 2;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue / 256.0F;
      int[] ints = new int[]{7227948, 3811094, 9069632, 4862496, 6242600, 10713679, 5191200, 9728074};

      for (int i = 0; i < 600; i++) {
         float bonecaAmbalabuValue = getAmount(i, 31) * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue2 = -0.1F * bonecaAmbalabu.bonecaValue + getAmount(i, 32) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuValue3 = (10.0F + getAmount(i, 33) * 16.0F) * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue4 = (getAmount(i, 34) - 0.5F) * 6.0F * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue5 = (getAmount(i, 36) - 0.5F) * 2.5F * bonecaAmbalabuFactor;
         float[] floats = new float[]{bonecaAmbalabuValue, bonecaAmbalabuValue + bonecaAmbalabuValue4 * 0.35F + bonecaAmbalabuValue5, bonecaAmbalabuValue + bonecaAmbalabuValue4 * 0.7F + bonecaAmbalabuValue5, bonecaAmbalabuValue + bonecaAmbalabuValue4};
         float[] floats2 = new float[]{bonecaAmbalabuValue2, bonecaAmbalabuValue2 + bonecaAmbalabuValue3 * 0.33F, bonecaAmbalabuValue2 + bonecaAmbalabuValue3 * 0.66F, bonecaAmbalabuValue2 + bonecaAmbalabuValue3};
         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, (0.75F + getAmount(i, 35) * 0.7F) * bonecaAmbalabuFactor, ForeShinUtils.getValueForInt(ints[i % ints.length]));
      }
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float shinProgressValue = (float)Math.cos(angle) * shinProgressFactor * 0.5F;
      foreShinScale.getAmounts(this.legFrontRightSection, shinProgressValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.legBackLeftSection, shinProgressValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classShinProgress, -shinProgressValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classShinFoot, -shinProgressValue, 0.0F, 0.0F);
      foreShinScale.getAmountsForeShin2(this.legBl, 0.0F, -(3.4F + Math.min(getMin(shinProgressValue), getMin(-shinProgressValue))), 0.0F);
      float shinProgressValue2 = (float)Math.cos(angle) * shinProgressFactor;
      foreShinScale.getAmountsForeShin2(this.head, 0.0F, 0.45F * Math.abs((float)Math.sin(angle)) * shinProgressFactor, 0.0F);
      foreShinScale.getAmounts(this.head, 0.0F, shinProgressValue2 * 0.08F, shinProgressValue2 * 0.2F);
      foreShinScale.getAmounts(this.foreEar, -0.04F * (float)Math.cos(2.0F * angle) * shinProgressFactor, -shinProgressValue2 * 0.06F, -shinProgressValue2 * 0.12F);
      float shinProgressValue3 = getAmount(poseProgress.headYawRadians, -1.2F, 1.2F);
      float shinProgressValue4 = getAmount(poseProgress.shinProgressAmount, -0.8F, 0.8F);
      float shinProgressValue5 = getAmount(shinProgressValue3 * 0.65F, -0.5F, 0.5F);
      foreShinScale.getAmountsBoneCount(this.head, 0.0F, (shinProgressValue3 - shinProgressValue5) * 0.45F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.foreEar, getAmount(shinProgressValue4 * 0.7F, -0.4F, 0.4F), shinProgressValue5, 0.0F);
      float legBurbaloniValue = poseProgress.age + (poseProgress.hashCode & 0xFF);
      boolean shinProgressFlag = poseProgress.swingProgress <= 0.0F && !poseProgress.poseProgressActive && !poseProgress.sneakingPose && !poseProgress.inPose && !poseProgress.vehicle;
      float shinProgressFactor2 = shinProgressFlag ? Math.max(0.0F, 1.0F - shinProgressFactor / 0.2F) : 0.0F;
      float shinProgressBound = getAmount4(legBurbaloniValue) * shinProgressFactor2;
      if (poseProgress.inPose) {
         shinProgressBound = 1.0F;
      }

      if (poseProgress.shinProgressActive) {
         shinProgressBound = Math.max(shinProgressBound, 0.45F);
      }

      float max7 = Math.max(poseProgress.inPose ? 1.0F : getAmount(legBurbaloniValue), getAmount(shinProgressBound * 1.4F, 0.0F, 1.0F));
      float shinProgressDivisor = 1.0F + 1.0F * max7;
      foreShinScale.getAmountsForForeEarFloat(this.lidRightSection, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.shinProgressForeEar, 1.0F, shinProgressDivisor, 1.0F);
      float foreShinFactor = 1.0F - 0.15F * max7;
      foreShinScale.getAmountsForForeEarFloat(this.eyeRightSection, foreShinFactor, 1.0F - 0.9F * max7, foreShinFactor);
      foreShinScale.getAmountsForForeEarFloat(this.shinFootForeEar, foreShinFactor, 1.0F - 0.9F * max7, foreShinFactor);
      float shinProgressLimit = legBurbaloniValue % 140.0F;
      float shinProgressFactor3 = shinProgressLimit > 20.0F && shinProgressLimit < 80.0F ? (0.5F - 0.5F * (float)Math.cos(legBurbaloniValue * 0.9F)) * shinProgressFactor2 : 0.0F;
      foreShinScale.getAmounts(this.legBurbaloniForeEar, -0.08F * shinProgressFactor3, 0.0F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.foreEar, 0.015F * shinProgressFactor3, 0.0F, 0.0F);
      foreShinScale.getAmountsForeShin2(this.foreEar, 0.0F, 0.06F * poseProgress.getAgeCode(50.0F, 0.0F), 0.0F);
      float shinProgressLimit2 = legBurbaloniValue % 90.0F;
      if (shinProgressLimit2 < 6.0F) {
         foreShinScale.getAmounts(this.earRightSection, 0.0F, 0.0F, 0.35F * (float)Math.sin(shinProgressLimit2 / 6.0F * (float) Math.PI * 2.0F));
      }

      if (shinProgressLimit2 > 45.0F && shinProgressLimit2 < 51.0F) {
         foreShinScale.getAmounts(this.earLeftSection, 0.0F, 0.0F, -0.35F * (float)Math.sin((shinProgressLimit2 - 45.0F) / 6.0F * (float) Math.PI * 2.0F));
      }

      foreShinScale.getAmountsForeShin2(this.foreEar, 0.0F, -0.15F * shinProgressBound, 5.6F * shinProgressBound);
      foreShinScale.getAmountsBoneCount(this.earRightSection, 0.5F * shinProgressBound, 0.0F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.earLeftSection, 0.5F * shinProgressBound, 0.0F, 0.0F);
      float shinProgressFactor4 = poseProgress.shinProgressActive ? 0.0F : Math.max(0.0F, shinProgressBound);
      foreShinScale.getAmountsForeShin2(this.head, 0.0F, -0.8F * shinProgressFactor4, 0.0F);
      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressValue10;
         float shinProgressFactor5;
         if (swingProgress < 0.18F) {
            shinProgressValue10 = getAmount3(swingProgress / 0.18F);
            shinProgressFactor5 = 0.0F;
         } else if (swingProgress < 0.32F) {
            shinProgressValue10 = 1.0F - getAmount3((swingProgress - 0.18F) / 0.14F);
            shinProgressFactor5 = getAmount3((swingProgress - 0.18F) / 0.14F);
         } else if (swingProgress < 0.55F) {
            shinProgressValue10 = 0.0F;
            shinProgressFactor5 = 1.0F;
         } else {
            shinProgressValue10 = 0.0F;
            shinProgressFactor5 = 1.0F - getAmount3((swingProgress - 0.55F) / 0.45F);
         }

         float shinProgressFactor6 = swingProgress < 0.18F ? shinProgressValue10 : 1.0F - getAmount3((swingProgress - 0.18F) / 0.08F);
         foreShinScale.getAmountsForeShin2(this.foreEar, 0.0F, 0.3F * shinProgressFactor5, 1.8F * shinProgressValue10 - 2.2F * shinProgressFactor5);
         foreShinScale.getAmountsBoneCount(this.foreEar, -0.12F * shinProgressFactor5, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.legBurbaloniForeEar, -0.55F * shinProgressFactor6, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.head, 0.08F * shinProgressValue10 - 0.12F * shinProgressFactor5, 0.0F, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.head, 1.0F + 0.04F * shinProgressFactor5, 1.0F - 0.04F * shinProgressFactor5, 1.0F + 0.04F * shinProgressFactor5);
      }

      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsForeShin2(this.head, 0.0F, -0.8F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.foreEar, 0.0F, 0.0F, 1.6F);
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmountsForeShin2(this.head, 0.0F, -0.8F, 0.0F);
         foreShinScale.getAmounts(this.legFrontRightSection, 1.1F, 0.0F, 0.15F);
         foreShinScale.getAmounts(this.classShinProgress, 1.1F, 0.0F, -0.15F);
         foreShinScale.getAmounts(this.classShinFoot, 0.9F, 0.0F, 0.15F);
         foreShinScale.getAmounts(this.legBackLeftSection, 0.9F, 0.0F, -0.15F);
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         float foreShinValue = 0.6F * (float)Math.sin(legBurbaloniValue * 0.8F);
         foreShinScale.getAmounts(this.legFrontRightSection, foreShinValue, 0.0F, 0.3F);
         foreShinScale.getAmounts(this.legBackLeftSection, foreShinValue, 0.0F, -0.3F);
         foreShinScale.getAmounts(this.classShinProgress, -foreShinValue, 0.0F, -0.3F);
         foreShinScale.getAmounts(this.classShinFoot, -foreShinValue, 0.0F, 0.3F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForeShin2(this.head, 0.0F, -0.6F, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.head, 1.06F, 0.94F, 1.06F);
         foreShinScale.getAmountsBoneCount(this.head, 0.0F, 0.0F, 0.12F * (float)Math.sin(poseProgress.age * 1.6F));
      }
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue / 320.0F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(6242600));
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5191200), 0.35F, 9.0F, 11);
      int[] ints = new int[]{3811094, 10713679, 4862496, 3811094, 9069632, 5191200};
      float[] floats = new float[]{0.75F, 0.45F, 0.7F, 0.6F, 0.5F, 0.7F};

      for (int i = 0; i < 520; i++) {
         int index = i % ints.length;
         float bonecaAmbalabuValue = getAmount(i, 1) * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuFactor2 = (0.25F + 0.75F * getAmount(i, 2)) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuValue2 = -0.15F * bonecaAmbalabu.bonecaValue + getAmount(i, 3) * (bonecaAmbalabu.bonecaValue * 1.15F - bonecaAmbalabuFactor2 * 0.6F);
         float bonecaAmbalabuFactor3 = (getAmount(i, 4) - 0.5F) * 14.0F * bonecaAmbalabuFactor;
         float bonecaAmbalabuFactor4 = (1.2F + getAmount(i, 5) * 2.2F) * bonecaAmbalabuFactor;
         byte bonecaAmbalabuSize = 8;
         float[] classValues = new float[bonecaAmbalabuSize];
         float[] floats3 = new float[bonecaAmbalabuSize];

         for (int j = 0; j < bonecaAmbalabuSize; j++) {
            float bonecaAmbalabuFactor5 = (float)j / (bonecaAmbalabuSize - 1);
            classValues[j] = bonecaAmbalabuValue + bonecaAmbalabuFactor3 * bonecaAmbalabuFactor5 + bonecaAmbalabuFactor4 * (float)Math.sin(bonecaAmbalabuFactor5 * 6.0F + i);
            floats3[j] = bonecaAmbalabuValue2 + bonecaAmbalabuFactor2 * bonecaAmbalabuFactor5;
         }

         float classValue = (index != 1 && index != 4 ? 1.1F + getAmount(i, 6) * 1.2F : 0.8F + getAmount(i, 6) * 0.7F) * bonecaAmbalabuFactor;
         bonecaAmbalabu.getClassForFloatArrayFloat(classValues, floats3, classValue, ForeShinUtils.getValueForIntFloat(ints[index], floats[index]));
      }

      for (int k = 0; k < 120; k++) {
         float bonecaAmbalabuValue4 = getAmount(k, 21) * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuFactor6 = (0.4F + 0.5F * getAmount(k, 24)) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuValue5 = -0.1F * bonecaAmbalabu.bonecaValue + getAmount(k, 22) * (bonecaAmbalabu.bonecaValue * 1.1F - bonecaAmbalabuFactor6 * 0.5F);
         float bonecaAmbalabuFactor7 = (getAmount(k, 23) - 0.5F) * 10.0F * bonecaAmbalabuFactor;
         float bonecaAmbalabuFactor8 = (0.6F + getAmount(k, 25) * 1.2F) * bonecaAmbalabuFactor;
         byte bonecaAmbalabuSize2 = 10;
         float[] floats4 = new float[bonecaAmbalabuSize2];
         float[] floats5 = new float[bonecaAmbalabuSize2];

         for (int m = 0; m < bonecaAmbalabuSize2; m++) {
            float bonecaAmbalabuFactor9 = (float)m / (bonecaAmbalabuSize2 - 1);
            floats4[m] = bonecaAmbalabuValue4 + bonecaAmbalabuFactor7 * bonecaAmbalabuFactor9 + bonecaAmbalabuFactor8 * (float)Math.sin(bonecaAmbalabuFactor9 * 7.0F + k * 1.3F);
            floats5[m] = bonecaAmbalabuValue5 + bonecaAmbalabuFactor6 * bonecaAmbalabuFactor9;
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats4, floats5, 0.6F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(12096616, 0.5F));
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 3.0F * bonecaAmbalabuFactor, 21);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.015F, ForeShinUtils.getValueForIntFloat(3811094, 0.35F));
   }

   @Override
   public String formatTungDin() {
      return "burbaloni_luliloli";
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuLimit) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(10512960));
      float bonecaAmbalabuValue = bonecaAmbalabuLimit > 0.0F ? bonecaAmbalabu.ambalabuBonecaValue * 0.78F : bonecaAmbalabu.ambalabuBonecaValue * 0.22F;

      for (int i = 0; i < 6; i++) {
         bonecaAmbalabu.getClass022(
            bonecaAmbalabuValue, bonecaAmbalabu.bonecaValue * 0.6F, bonecaAmbalabu.ambalabuBonecaValue * (0.24F - i * 0.03F), bonecaAmbalabu.bonecaValue * (0.3F - i * 0.04F), ForeShinUtils.getValueForIntFloat(12093530, 0.15F)
         );
      }

      for (int j = 0; j < 4; j++) {
         bonecaAmbalabu.getClass022(
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
            bonecaAmbalabu.bonecaValue * 0.66F,
            bonecaAmbalabu.ambalabuBonecaValue * (0.36F - j * 0.06F),
            bonecaAmbalabu.bonecaValue * (0.22F - j * 0.04F),
            ForeShinUtils.getValueForIntFloat(12093530, 0.1F)
         );
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 4.0F, 51);
      handleBonecaAmbalabu(bonecaAmbalabu, 90, 53, -bonecaAmbalabuLimit * 4.0F, 0.4F, 1.5F, 12490346);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuLimit, int bonecaAmbalabuValue, float bonecaAmbalabuFactor, float bonecaAmbalabuFactor2, float bonecaAmbalabuFactor3, int bonecaAmbalabuValue2) {
      int[] ints = new int[]{7227952, bonecaAmbalabuValue2, 8675391};
      float distance = (float)Math.sqrt(bonecaAmbalabuFactor * bonecaAmbalabuFactor + bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2);

      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float classBound = getAmount(i, bonecaAmbalabuValue) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(i, bonecaAmbalabuValue + 1) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuFactor4 = 0.7F + getAmount(i, bonecaAmbalabuValue + 2) * 0.8F;
         float bonecaAmbalabuFactor5 = (getAmount(i, bonecaAmbalabuValue + 3) - 0.5F) * bonecaAmbalabuFactor3;
         bonecaAmbalabu.getClass02(
            classBound,
            amountForFloat,
            classBound + bonecaAmbalabuFactor * bonecaAmbalabuFactor4 - bonecaAmbalabuFactor2 / distance * bonecaAmbalabuFactor5,
            amountForFloat + bonecaAmbalabuFactor2 * bonecaAmbalabuFactor4 + bonecaAmbalabuFactor / distance * bonecaAmbalabuFactor5,
            0.7F,
            ForeShinUtils.getValueForIntFloat(ints[i % 3], 0.28F)
         );
      }
   }

   @Override
   public String formatTungDinString() {
      return "Burbaloni Luliloli";
   }

   private static float getAmount(float legBurbaloniLimit, float legBurbaloniLimit2, float legBurbaloniValue) {
      return legBurbaloniLimit < legBurbaloniLimit2 ? legBurbaloniLimit2 : (legBurbaloniLimit > legBurbaloniValue ? legBurbaloniValue : legBurbaloniLimit);
   }

   private static void handleAmount(LatheLoft latheLoft, ShinBrr[] shinBrrs) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 7.9F, 1.6F).getLatheLoftForFloat(1.0F, 1.077F, 1.0F).getLatheLoftLatheLoftAmountLoft(-90.0F).getLatheLoft4(180.0F);
      byte legBurbaloniLimit = 14;

      for (int i = 0; i < legBurbaloniLimit; i++) {
         float angle = (i + 0.7F * getAmount(i, 91)) / legBurbaloniLimit * (float) (Math.PI * 2);
         float legBurbaloniValue = 4.12F + 0.3F * getAmount(i, 92);
         float legBurbaloniFactor = 0.3F + 0.25F * getAmount(i, 93);
         float legBurbaloniFactor2 = 0.05F + 0.05F * getAmount(i, 94);
         float legBurbaloniFactor3 = 0.15F + 0.2F * getAmount(i, 95);
         float legBurbaloniFactor4 = (getAmount(i, 96) - 0.5F) * 0.5F;
         float sin = (float)Math.sin(angle);
         float cos = (float)Math.cos(angle);
         float legBurbaloniValue2 = BASE_HEIGHT_OFFSET + 0.03F;
         float legBurbaloniValue3 = legBurbaloniValue + legBurbaloniFactor * (1.0F - legBurbaloniFactor3 * 0.6F);
         float legBurbaloniValue4 = legBurbaloniValue2 + legBurbaloniFactor * legBurbaloniFactor3;
         float legBurbaloniFactor5 = -cos * legBurbaloniFactor2;
         float legBurbaloniFactor6 = sin * legBurbaloniFactor2;
         float rootValue = -legBurbaloniValue3 * sin + legBurbaloniFactor5 * legBurbaloniFactor4 * 4.0F;
         float minSide = -legBurbaloniValue3 * cos + legBurbaloniFactor6 * legBurbaloniFactor4 * 4.0F;
         latheLoft.getLatheLoft(
            -legBurbaloniValue * sin - legBurbaloniFactor5, legBurbaloniValue2, -legBurbaloniValue * cos - legBurbaloniFactor6, -legBurbaloniValue * sin + legBurbaloniFactor5, legBurbaloniValue2, -legBurbaloniValue * cos + legBurbaloniFactor6, rootValue, legBurbaloniValue4, minSide, shinBrrs[i % shinBrrs.length]
         );
      }

      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(8213812));
      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 3.0F, 45);
      handleBonecaAmbalabu(bonecaAmbalabu, 50, 47, 0.0F, 4.0F, 1.5F, 10121288);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(5913640, 0.5F * getAmount3(((float)i / bonecaAmbalabu.bonecaValue - 0.5F) / 0.5F)));
      }
   }

   private static float getAmount(float legBurbaloniValue) {
      float legBurbaloniValue2 = legBurbaloniValue % 100.0F;
      if (legBurbaloniValue2 < 5.0F) {
         return getAmount3(legBurbaloniValue2 / 5.0F);
      } else if (legBurbaloniValue2 < 9.0F) {
         return 1.0F;
      } else {
         return legBurbaloniValue2 < 16.0F ? 1.0F - getAmount3((legBurbaloniValue2 - 9.0F) / 7.0F) : 0.0F;
      }
   }

   private static float getAmountForFloat(float angle) {
      return 6.15F * (float)Math.sin(angle) * (1.0F - 0.13F * (float)Math.cos(angle));
   }

   private ForeEar getAmount(LatheLoft latheLoft, String legBurbaloniText, float legBurbaloniValue, float legBurbaloniValue2, float legBurbaloniValue3) {
      float angle = (float)Math.toRadians(-5.0);
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float legBurbaloniFactor = legBurbaloniValue2 - 8.5F;
      float legBurbaloniFactor2 = legBurbaloniValue3 - HEAD_SHELL_OFFSET;
      return latheLoft.getClassForStringForeEar(legBurbaloniText, this.foreEar, legBurbaloniValue, 8.5F + legBurbaloniFactor * cos - legBurbaloniFactor2 * sin + -0.5999999F, HEAD_SHELL_OFFSET + legBurbaloniFactor * sin + legBurbaloniFactor2 * cos + 0.3F);
   }

   private static void handleShellHead(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4, ShinBrr shinBrr5, ShinBrr shinBrr6) {
      latheLoft.getLatheLoft(0.0F, 8.48F, headShellAmount + 1.9F - 0.3F, 2.62F, 2.85F, 3.6F, 10, latheLoft.getMax(8213812));
      latheLoft.getLoftLathe(-1.95F, 6.2F, -9.7F, 1.95F, 10.75F, -0.6F, 1.15F, 3, new ShinBrr[]{shinBrr4, shinBrr, shinBrr2, shinBrr3, shinBrr5, shinBrr}, shinBrr6);
      latheLoft.getLatheLoft(0.0F, 7.5F, -4.2F, 2.36F, 2.0F, 2.6F, 10, shinBrr6);
      ShinBrr max7 = latheLoft.getMax(11171404);
      ShinBrr max8 = latheLoft.getMax(16184040);
      ShinBrr max9 = latheLoft.getMax(2760215);

      for (int i = -1; i <= 1; i += 2) {
         latheLoft.getLatheLoft(i * 1.72F, 7.4F, -6.1F, 0.62F, 1.2F, 2.1F, 12, shinBrr);
         latheLoft.getLatheLoft(i * 0.5F, 8.2F, -9.64F, 0.66F, 0.72F, 0.14F, 12, max7);
         latheLoft.getLatheLoft(i * 0.03F, 5.95F, -9.52F, i * 0.32F, 7.0F, -9.36F, max8);

         for (int j = 0; j < 3; j++) {
            float shellHeadValue = 7.85F + j * 0.34F;
            latheLoft.getLatheLoft(
               new float[]{i * 1.8F, shellHeadValue, -9.0F, i * 2.8F, shellHeadValue + (j - 1) * 0.25F, -9.4F}, new float[]{0.05F, 0.025F}, 3, max9, false
            );
         }
      }

      ShinBrr max10 = latheLoft.getMax(3811874);
      ShinBrr max11 = latheLoft.getMax(328450);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 9.5F, -9.65F).getLatheLoftLatheLoftAmountLoft(38.0F);
      latheLoft.getPolygonExtrude(
         new float[]{
            -1.35F,
            0.32F,
            -1.22F,
            0.53F,
            -0.85F,
            0.61F,
            0.0F,
            0.63F,
            0.85F,
            0.61F,
            1.22F,
            0.53F,
            1.35F,
            0.32F,
            1.3F,
            -0.05F,
            0.9F,
            -0.33F,
            0.0F,
            -0.4F,
            -0.9F,
            -0.33F,
            -1.3F,
            -0.05F
         },
         -0.18F,
         0.3F,
         max10,
         null,
         max10
      );

      for (int k = -1; k <= 1; k += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(k * 0.85F, 0.08F, -0.28F).getLatheLoft(35.0F * k);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.13F, 0.36F, 0.1F, 8, max11);
         latheLoft.getDequeArray();
      }

      latheLoft.getDequeArray();
      float shellHeadValue2 = 9.6F;
      float armTrunkValue = -8.55F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, shellHeadValue2, armTrunkValue).getLatheLoft(90.0F);
      latheLoft.getLatheLoft(new float[]{1.0F, 1.1F, 1.19F, 1.19F, 1.1F, 1.0F}, new float[]{-1.22F, -1.08F, -0.92F, 0.92F, 1.08F, 1.22F}, 16, max10, null, null);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, shellHeadValue2 + 1.008F, armTrunkValue - 0.64800006F).getLatheLoftLatheLoftAmountLoft(-33.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.72F, 0.05F, 0.12F, 8, latheLoft.getMax(6048320));
      latheLoft.getDequeArray();
      latheLoft.getLatheLoft(-0.6F, 6.0F, -8.8F, 0.6F, 6.3F, -7.7F, latheLoft.getMax(3808792));
   }

   private static float[] getAmounts(float angle, float angle2) {
      float legBurbaloniFactor = getAmountForFloat(angle);
      float legBurbaloniValue = legBurbaloniFactor * (float)Math.sin(angle2);
      float legBurbaloniValue2 = 7.9F + 1.077F * legBurbaloniFactor * (float)Math.cos(angle2);
      float legBurbaloniValue3 = 1.6F + 7.7F * (float)Math.cos(angle);
      float legBurbaloniFactor2 = legBurbaloniValue / 37.822502F;
      float legBurbaloniFactor3 = (legBurbaloniValue2 - 7.9F) / 43.871418F;
      float legBurbaloniFactor4 = (legBurbaloniValue3 - 1.6F) / 59.289997F;
      float distance = (float)Math.sqrt(legBurbaloniFactor2 * legBurbaloniFactor2 + legBurbaloniFactor3 * legBurbaloniFactor3 + legBurbaloniFactor4 * legBurbaloniFactor4);
      float legBurbaloniFactor5 = 6.15F * ((float)Math.cos(angle) * (1.0F - 0.13F * (float)Math.cos(angle)) + 0.13F * (float)(Math.sin(angle) * Math.sin(angle)));
      float legBurbaloniFactor6 = -legBurbaloniFactor5 * (float)Math.sin(angle2);
      float legBurbaloniFactor7 = -1.077F * legBurbaloniFactor5 * (float)Math.cos(angle2);
      float legBurbaloniFactor8 = 7.7F * (float)Math.sin(angle);
      float sqrt2 = (float)Math.sqrt(legBurbaloniFactor6 * legBurbaloniFactor6 + legBurbaloniFactor7 * legBurbaloniFactor7 + legBurbaloniFactor8 * legBurbaloniFactor8);
      return new float[]{legBurbaloniValue, legBurbaloniValue2, legBurbaloniValue3, legBurbaloniFactor2 / distance, legBurbaloniFactor3 / distance, legBurbaloniFactor4 / distance, legBurbaloniFactor6 / sqrt2, legBurbaloniFactor7 / sqrt2, legBurbaloniFactor8 / sqrt2};
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float[] bonecaAmbalabuValues, float[] bonecaAmbalabuValues2, int[] bonecaAmbalabuValues3, boolean bonecaAmbalabuFlag) {
      int bonecaAmbalabuSize = bonecaAmbalabuValues.length;
      float[] floats = new float[bonecaAmbalabuSize];
      float[] floats2 = new float[bonecaAmbalabuSize];

      for (int i = 1; i < bonecaAmbalabuSize; i++) {
         float bonecaAmbalabuFactor = bonecaAmbalabuValues[i] - bonecaAmbalabuValues[i - 1];
         float bonecaAmbalabuFactor2 = bonecaAmbalabuValues2[i] - bonecaAmbalabuValues2[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(bonecaAmbalabuFactor * bonecaAmbalabuFactor + bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2);
      }

      float bonecaAmbalabuDivisor = floats[bonecaAmbalabuSize - 1];
      float bonecaAmbalabuFactor3 = (bonecaAmbalabu.bonecaValue - 1) / bonecaAmbalabuDivisor;

      for (int j = 0; j < bonecaAmbalabuSize; j++) {
         floats2[j] = (bonecaAmbalabu.bonecaValue - 1) * (1.0F - floats[j] / bonecaAmbalabuDivisor);
      }

      for (int k = 0; k < bonecaAmbalabu.bonecaValue; k++) {
         float bonecaAmbalabuLimit = (1.0F - (float)k / (bonecaAmbalabu.bonecaValue - 1)) * bonecaAmbalabuDivisor;
         int index = 0;

         while (index < bonecaAmbalabuSize - 2 && floats[index + 1] < bonecaAmbalabuLimit) {
            index++;
         }

         float bonecaAmbalabuValue = (bonecaAmbalabuLimit - floats[index]) / Math.max(1.0E-4F, floats[index + 1] - floats[index]);
         bonecaAmbalabu.getClassBonecaAmbalabu(
            0.0F,
            k,
            bonecaAmbalabu.ambalabuBonecaValue,
            k + 1,
            ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(bonecaAmbalabuValues3[index]), ForeShinUtils.getValueForInt(bonecaAmbalabuValues3[index + 1]), bonecaAmbalabuValue)
         );
      }

      if (bonecaAmbalabuFlag) {
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, floats2[1], ForeShinUtils.getValueForInt(4862496));

         for (int m = 0; m < 520; m++) {
            float classBound = getAmount(m, 81) * bonecaAmbalabu.ambalabuBonecaValue;
            float bonecaAmbalabuValue3 = getAmount(m, 82) * floats2[1] * 1.1F - 0.1F * floats2[1];
            float bonecaAmbalabuValue4 = (0.12F + 0.22F * getAmount(m, 83)) * bonecaAmbalabuFactor3;
            int bonecaAmbalabuValue5 = m % 4;
            int foreShinValue = bonecaAmbalabuValue5 == 0 ? 10713679 : (bonecaAmbalabuValue5 == 1 ? 3811094 : (bonecaAmbalabuValue5 == 2 ? 9069632 : 6242600));
            bonecaAmbalabu.getClass02(
               classBound,
               bonecaAmbalabuValue3,
               classBound + (getAmount(m, 84) - 0.5F) * 3.0F,
               Math.min(bonecaAmbalabuValue3 + bonecaAmbalabuValue4, floats2[1]),
               0.9F + getAmount(m, 85) * 0.8F,
               ForeShinUtils.getValueForIntFloat(foreShinValue, 0.8F)
            );
         }

         for (int p = 0; p < 260; p++) {
            float classValue = getAmount(p, 71) * bonecaAmbalabu.ambalabuBonecaValue;
            float amountForFloat = floats2[1] + 0.08F * bonecaAmbalabuFactor3 + getAmount(p, 72) * (floats2[0] - floats2[1] - 0.08F * bonecaAmbalabuFactor3);
            bonecaAmbalabu.getClass022(
               classValue,
               amountForFloat,
               0.7F + getAmount(p, 73) * 1.2F,
               0.5F + getAmount(p, 74) * 0.8F,
               ForeShinUtils.getValueForIntFloat(getAmount(p, 75) < 0.5F ? 14866626 : 16513264, 0.5F)
            );
         }

         for (int q = 0; q < bonecaAmbalabu.ambalabuBonecaValue; q++) {
            float bonecaAmbalabuValue9 = (float)q / bonecaAmbalabu.ambalabuBonecaValue * (float) (Math.PI * 2);
            float bonecaAmbalabuBound = (0.04F + 0.06F * (float)(Math.sin(bonecaAmbalabuValue9 * 7.0F + 1.3F) * Math.sin(bonecaAmbalabuValue9 * 3.0F)) + 0.07F * getAmount(q / 3, 87)) * bonecaAmbalabuFactor3;
            float bonecaAmbalabuBound2 = (0.1F + 0.05F * (float)Math.sin(bonecaAmbalabuValue9 * 11.0F + 0.7F) * (float)Math.sin(bonecaAmbalabuValue9 * 4.0F) + 0.1F * getAmount(q / 2, 90)) * bonecaAmbalabuFactor3;
            float bonecaValue = floats2[1] + Math.max(0.015F * bonecaAmbalabuFactor3, bonecaAmbalabuBound);
            float bonecaAmbalabuValue11 = floats2[1] - Math.max(0.04F * bonecaAmbalabuFactor3, bonecaAmbalabuBound2);
            bonecaAmbalabu.getClassBonecaAmbalabu(q, bonecaAmbalabuValue11, q + 1, bonecaValue, ForeShinUtils.getValueForInt(3876887));
            bonecaAmbalabu.getClassBonecaAmbalabu(q, bonecaValue, q + 1, bonecaValue + 0.03F * bonecaAmbalabuFactor3, ForeShinUtils.getValueForIntFloat(14076592, 0.5F));
            if (getAmount(q / 2, 97) < 0.22F) {
               bonecaAmbalabu.getClassBonecaAmbalabu(q, bonecaAmbalabuValue11 - 0.1F * bonecaAmbalabuFactor3 * getAmount(q, 98), q + 1, bonecaAmbalabuValue11, ForeShinUtils.getValueForIntFloat(3811094, 0.85F));
            }
         }
      } else {
         for (int r = 0; r < bonecaAmbalabu.ambalabuBonecaValue; r++) {
            float bonecaAmbalabuValue12 = (float)r / bonecaAmbalabu.ambalabuBonecaValue * (float) (Math.PI * 2);
            float rocketsElytraBound = (0.1F + 0.04F * (float)Math.sin(bonecaAmbalabuValue12 * 5.0F + 0.4F) + 0.05F * getAmount(r / 3, 88)) * bonecaAmbalabuFactor3;
            int bonecaAmbalabuLimit2 = (int)Math.ceil(Math.max(floats2[2], rocketsElytraBound + 0.3F * bonecaAmbalabuFactor3)) + 1;

            for (int s = 0; s <= bonecaAmbalabuLimit2 && s < bonecaAmbalabu.bonecaValue; s++) {
               float bonecaAmbalabuValue13 = (s - rocketsElytraBound) / bonecaAmbalabuFactor3;
               int bonecaAmbalabuValue14;
               if (bonecaAmbalabuValue13 < 0.0F) {
                  bonecaAmbalabuValue14 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(16052194), ForeShinUtils.getValueForInt(15590866), s / Math.max(1.0F, rocketsElytraBound));
               } else if (bonecaAmbalabuValue13 < 0.12F) {
                  bonecaAmbalabuValue14 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15590866), ForeShinUtils.getValueForInt(13616300), bonecaAmbalabuValue13 / 0.12F);
               } else if (bonecaAmbalabuValue13 < 0.28F) {
                  bonecaAmbalabuValue14 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13616300), ForeShinUtils.getValueForInt(3811868), (bonecaAmbalabuValue13 - 0.12F) / 0.16F);
               } else {
                  bonecaAmbalabuValue14 = ForeShinUtils.getValueForInt(3811868);
               }

               bonecaAmbalabu.getBonecaAmbalabu(r, s, bonecaAmbalabuValue14);
            }
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 3.0F, bonecaAmbalabuFlag ? 85 : 86);
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(10512960));

      for (int i = 0; i < 5; i++) {
         bonecaAmbalabu.getClass022(
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
            bonecaAmbalabu.bonecaValue * 0.74F,
            bonecaAmbalabu.ambalabuBonecaValue * (0.42F - i * 0.06F),
            bonecaAmbalabu.bonecaValue * (0.22F - i * 0.03F),
            ForeShinUtils.getValueForIntFloat(12093530, 0.14F)
         );
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 3.0F, 61);
      handleBonecaAmbalabu(bonecaAmbalabu, 40, 63, 0.0F, 3.0F, 2.0F, 11831904);
      float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      bonecaAmbalabu.getClass02(bonecaAmbalabuValue, bonecaAmbalabu.bonecaValue * 0.62F, bonecaAmbalabuValue, (float)bonecaAmbalabu.bonecaValue, 0.8F, ForeShinUtils.getValueForInt(5913126));
   }

   private static float getMin(float angle) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float legBurbaloniBound = -2.98F * cos + 0.25F * sin - (float)Math.sqrt(0.17639999F * cos * cos + 0.7225F * sin * sin);
      float wrapDegreesBound = -3.28F * cos + 1.07F * sin;
      float hashHuntBound = -3.32F * cos + 0.87F * sin;
      return Math.min(legBurbaloniBound, Math.min(wrapDegreesBound, hashHuntBound));
   }

   @Override
   public String formatNoteBlock() {
      return "block.note_block.cow_bell";
   }

   private static float getAmount(int legBurbaloniValue, int bonecaAmbalabuValue) {
      int legBurbaloniValue3 = legBurbaloniValue * 374761393 + bonecaAmbalabuValue * 668265263 + 763781969;
      int legBurbaloniValue4 = (legBurbaloniValue3 ^ legBurbaloniValue3 >>> 13) * 1274126177;
      int legBurbaloniValue5 = legBurbaloniValue4 ^ legBurbaloniValue4 >>> 16;
      return (legBurbaloniValue5 & 16777215) / 1.6777215E7F;
   }

   private static float getAmount3(float legBurbaloniFactor) {
      legBurbaloniFactor = legBurbaloniFactor < 0.0F ? 0.0F : (legBurbaloniFactor > 1.0F ? 1.0F : legBurbaloniFactor);
      return legBurbaloniFactor * legBurbaloniFactor * (3.0F - 2.0F * legBurbaloniFactor);
   }

   private static void handle(LatheLoft latheLoft, float legBurbaloniValue, float legBurbaloniValue2, int legBurbaloniLimit, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      latheLoft.getLatheLoft(new float[]{legBurbaloniValue, 3.4F, legBurbaloniValue2, legBurbaloniValue, 1.7F, legBurbaloniValue2 + 0.15F, legBurbaloniValue, 0.55F, legBurbaloniValue2 - 0.12F}, new float[]{0.82F, 0.76F, 0.68F}, 8, shinBrr, false);
      latheLoft.getLatheLoft(legBurbaloniValue, 0.42F, legBurbaloniValue2 - 0.25F, 0.72F, 0.42F, 0.85F, 8, shinBrr2);
      float legBurbaloniFactor = -0.61904764F;

      for (int i = 0; i < legBurbaloniLimit; i++) {
         float legBurbaloniFactor2 = (float)i / (legBurbaloniLimit - 1) - 0.5F;
         float legBurbaloniValue3 = legBurbaloniFactor2 * (legBurbaloniLimit == 4 ? 0.8F : 0.6F);
         float legBurbaloniBound = 1.0F - legBurbaloniFactor * legBurbaloniFactor - legBurbaloniValue3 / 0.72F * (legBurbaloniValue3 / 0.72F);
         float minSide = legBurbaloniValue2 - 0.25F - 0.85F * (float)Math.sqrt(Math.max(0.0F, legBurbaloniBound)) + 0.05F;
         float rootValue = legBurbaloniValue + legBurbaloniValue3;
         float legBurbaloniValue6 = 0.12F;
         float legBurbaloniValue7 = minSide - 0.2F;
         latheLoft.getLatheLoft(rootValue - 0.09F, 0.25F, minSide, rootValue + 0.09F, 0.25F, minSide, rootValue, legBurbaloniValue6, legBurbaloniValue7, shinBrr3);
         latheLoft.getLatheLoft(rootValue + 0.09F, 0.25F, minSide, rootValue, 0.08F, minSide, rootValue, legBurbaloniValue6, legBurbaloniValue7, shinBrr3);
         latheLoft.getLatheLoft(rootValue, 0.08F, minSide, rootValue - 0.09F, 0.25F, minSide, rootValue, legBurbaloniValue6, legBurbaloniValue7, shinBrr3);
      }
   }

   private static void handle(LatheLoft latheLoft, float legBurbaloniFactor) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(legBurbaloniFactor * 1.74F, 10.2F, -7.1F).getLatheLoft4(-24.0F * legBurbaloniFactor).getLatheLoftLatheLoftAmountLoft(16.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.62F, 0.46F, 0.34F, 10, latheLoft.getMax(2233612));
      latheLoft.getLatheLoft(0.0F, -0.03F, -0.21F, 0.33F, 0.3F, 0.15F, 8, latheLoft.getMax(525572));
      latheLoft.getLoftLathe(-0.2F * legBurbaloniFactor, 0.0F, -0.35F, 0.09F, 0.08F, 10, latheLoft.getMax(16777215));
      latheLoft.getDequeArray();
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float legBurbaloniFactor) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(legBurbaloniFactor * 1.5F, 10.95F, -5.4F).getLatheLoft4(-45.0F * legBurbaloniFactor).getLatheLoft(-15.0F * legBurbaloniFactor);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.58F, 0.62F, 0.18F, 8, latheLoft.getMax(9132604));
      latheLoft.getLatheLoft(0.0F, -0.04F, -0.11F, 0.36F, 0.42F, 0.09F, 6, latheLoft.getMax(12024422));
      latheLoft.getDequeArray();
   }

   private static void handleAmountsAmount(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      byte legBurbaloniLimit = 12;
      byte legBurbaloniLimit2 = 9;
      float[] floats = new float[legBurbaloniLimit + 1];
      float[] floats2 = new float[legBurbaloniLimit + 1];
      float[] latheLoftValues = new float[legBurbaloniLimit2 + 1];
      float[] tailKatanaValues = new float[legBurbaloniLimit2 + 1];
      float legBurbaloniFactor = 2.45F;

      for (int i = 0; i <= legBurbaloniLimit; i++) {
         float angle = 2.47F * i / legBurbaloniLimit;
         floats[i] = getAmountForFloat(angle);
         floats2[i] = -7.7F * (float)Math.cos(angle);
      }

      for (int j = 0; j <= legBurbaloniLimit2; j++) {
         float angle2 = legBurbaloniFactor * j / legBurbaloniLimit2;
         latheLoftValues[j] = getAmountForFloat(angle2) + 0.25F * (float)Math.sin(angle2);
         tailKatanaValues[j] = -7.95F * (float)Math.cos(angle2);
      }

      float[] floats5 = new float[shinFootTable.length];
      float[] floats6 = new float[table.length];

      for (int k = 0; k < floats5.length; k++) {
         floats5[k] = BASE_HEIGHT_OFFSET + legBurbaloniTable[k];
      }

      for (int m = 0; m < floats6.length; m++) {
         floats6[m] = BASE_HEIGHT_OFFSET + LAYER_THICKNESSES[m];
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 7.9F, 1.6F).getLatheLoftForFloat(1.0F, 1.077F, 1.0F).getLatheLoftLatheLoftAmountLoft(-90.0F).getLatheLoft4(180.0F);
      latheLoft.getLatheLoft(floats, floats2, 22, shinBrr, null, null);
      latheLoft.getLatheLoft(latheLoftValues, tailKatanaValues, 16, shinBrr2, null, null);
      latheLoft.getLatheProfile(shinFootTable, floats5, 20, 1.0F, 1.06F, shinBrr3, latheLoft.getMax(1971212), null);
      latheLoft.getLatheLoft(table, floats6, 22, shinBrr4, null, null);
      latheLoft.getDequeArray();
   }

   @Override
   protected int getValueInt() {
      return 1024;
   }

   @Override
   public float getAmountFloat() {
      return 0.6F;
   }

   private static void handle(LatheLoft latheLoft) {
      ShinBrr shinBrr = latheLoft.getMax(1971212);

      for (int i = 0; i < 3; i++) {
         float[] legBurbaloniValues = getAmounts(0.27F, 0.5F + i * 2.0944F);
         float degrees = (float)Math.toDegrees(Math.atan2(legBurbaloniValues[3], legBurbaloniValues[5]));
         float degrees2 = (float)(-Math.toDegrees(Math.asin(legBurbaloniValues[4])));
         latheLoft.getDequeArrayArrayDeque()
            .getLatheLoft(legBurbaloniValues[0] - legBurbaloniValues[3] * 0.06F, legBurbaloniValues[1] - legBurbaloniValues[4] * 0.06F, legBurbaloniValues[2] - legBurbaloniValues[5] * 0.06F)
            .getLatheLoft4(degrees)
            .getLatheLoftLatheLoftAmountLoft(degrees2);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.42F, 0.42F, 0.14F, 6, shinBrr);
         latheLoft.getDequeArray();
      }
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = getAmount3((float)i / bonecaAmbalabu.bonecaValue / 0.2F);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float absScaled = Math.abs((j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 2.0F;
            float foreShinFactor = 1.0F - getAmount3((absScaled - 0.35F) / 0.6F);
            bonecaAmbalabu.getBonecaAmbalabu(
               j,
               i,
               ForeShinUtils.getValue2(
                  ForeShinUtils.getValueForInt(10512960),
                  ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(10512960), ForeShinUtils.getValue(ForeShinUtils.getValueForInt(8213812), 0.95F), foreShinFactor),
                  bonecaAmbalabuValue
               )
            );
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 4.0F, 71);
      handleBonecaAmbalabu(bonecaAmbalabu, 90, 73, 0.0F, 4.5F, 2.0F, 11436120);
   }

   private static void handle3(LatheLoft latheLoft, float legBurbaloniFactor) {
      latheLoft.getDequeArrayArrayDeque()
         .getLatheLoft(legBurbaloniFactor * 1.74F, 10.8F, -7.1F)
         .getLatheLoftForFloat(1.0F, 0.5F, 1.0F)
         .getLatheLoft(0.0F, -0.78000003F, 0.0F)
         .getLatheLoft4(-24.0F * legBurbaloniFactor)
         .getLatheLoftLatheLoftAmountLoft(6.0F)
         .getLatheLoft(-6.0F * legBurbaloniFactor);
      latheLoft.getLatheLoft(0.0F, 0.02F, 0.03F, 0.66F, 0.4F, 0.41F, 10, latheLoft.getMax(5911584));
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque()
         .getLatheLoft(legBurbaloniFactor * 1.74F, 10.8F, -7.1F)
         .getLatheLoftForFloat(1.0F, 0.5F, 1.0F)
         .getLatheLoft(0.0F, -0.72F, 0.0F)
         .getLatheLoft4(-24.0F * legBurbaloniFactor)
         .getLatheLoftLatheLoftAmountLoft(6.0F)
         .getLatheLoft(-6.0F * legBurbaloniFactor);
      latheLoft.getLoftLathe(0.0F, 0.02F, 0.03F, 0.68F, 0.52F, 0.43F, 10, 0.0F, 0.62F, latheLoft.getMax(10512960));
      latheLoft.getDequeArray();
   }

   private static float getAmount4(float legBurbaloniValue) {
      float legBurbaloniValue2 = legBurbaloniValue % 400.0F;
      if (legBurbaloniValue2 < 320.0F) {
         return 0.0F;
      } else if (legBurbaloniValue2 < 328.0F) {
         return getAmount3((legBurbaloniValue2 - 320.0F) / 8.0F);
      } else if (legBurbaloniValue2 < 364.0F) {
         return 1.0F;
      } else if (legBurbaloniValue2 < 374.0F) {
         float legBurbaloniFactor = (legBurbaloniValue2 - 364.0F) / 10.0F - 1.0F;
         return -(2.70158F * legBurbaloniFactor * legBurbaloniFactor * legBurbaloniFactor + 1.70158F * legBurbaloniFactor * legBurbaloniFactor);
      } else {
         return 0.0F;
      }
   }
}
