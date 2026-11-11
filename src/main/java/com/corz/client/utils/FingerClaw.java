package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;

public final class FingerClaw extends WarmBrainrotBase {
   private ForeEar classShinFoot;
   private static final float[] shinFootTable = new float[]{5.55F, 5.8F, 6.4F, 7.2F, 8.1F, 9.4F, 10.75F, 12.0F, 12.8F, 13.5F, 14.1F, 14.3F};
   private ForeEar classShinProgress;
   private final ForeEar[] fan = new ForeEar[6];
   private ForeEar antL;
   private ForeEar shinFootForeEar;
   private ForeEar shinProgressForeEar;
   private ForeEar fingerClawForeEar;
   private ForeEar foreEar;
   private static final float[] fingerClawTable = new float[]{0.01F, 1.95F, 3.2F, 4.0F, 4.4F, 4.45F, 4.3F, 4.05F, 3.55F, 2.95F, 2.2F, 0.01F};
   private static final float[] clawFingerAmounts = new float[]{-4.4F, -2.6F, -0.6F, 1.4F, 3.2F};
   private static final float[] fingerTable = new float[]{0.0F, -2.6F, -1.0F, 1.1F, 2.8F};
   private ForeEar head;
   private static final float[] table = new float[]{0.0F, 5.1F, 5.65F, 6.15F, 6.6F};
   private ForeEar earLeftSection;
   private ForeEar fingerL;
   private final ForeEar[] clawR;
   private ForeEar fingerR;
   private static final float[] FINGER_SEGMENT_OFFSETS = new float[]{0.0F, 5.45F, 6.05F, 6.6F, 7.1F};
   private ForeEar antennaRightSection;
   private ForeEar eyeRightSection;
   private ForeEar shell;
   private static final float[] shinProgressTable = new float[]{0.0F, 0.229F, 0.203F, 0.184F, 0.169F};
   private final ForeEar[] clawL;
   private final ForeEar[] shinFootForeEars = new ForeEar[6];
   private static final float[] pointsSpline = LatheLoft.getPointsSpline(
      8, 0.0F, 7.2F, 1.8F, 0.0F, 9.0F, 4.4F, 0.0F, 9.2F, 7.2F, 0.0F, 8.3F, 10.2F, 0.0F, 6.0F, 12.6F, 0.0F, 3.6F, 13.6F
   );
   private static final float[] bodyShellAmounts = getAmounts(FingerClaw.pointsSpline);

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      float shinFootValue = getAmount(1.8F, 10.7F);
      float shinFootFactor = bodyShellAmounts[bodyShellAmounts.length - 1];
      this.shell = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 5.0F, -1.0F);
      this.head = latheLoft.getClassForStringForeEar("shell", this.shell, 0.0F, 5.0F, -1.0F);
      this.antL = latheLoft.getClassForStringForeEar("head", this.head, 0.0F, 9.0F, -8.0F);
      this.eyeRightSection = latheLoft.getClassForStringForeEar("eye_r", this.antL, 1.8F, 10.7F, shinFootValue);
      this.foreEar = latheLoft.getClassForStringForeEar("eye_l", this.antL, -1.8F, 10.7F, shinFootValue);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("ear_r", this.antL, 2.65F, 12.5F, -8.3F);
      this.earLeftSection = latheLoft.getClassForStringForeEar("ear_l", this.antL, -2.65F, 12.5F, -8.3F);
      this.antennaRightSection = latheLoft.getClassForStringForeEar("ant_r", this.antL, 1.6F, 11.8F, -4.4000006F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("ant_l", this.antL, -1.6F, 11.8F, -4.4000006F);
      ForeEar head = this.head;

      for (int i = 0; i < 6; i++) {
         float[] amountsShell = getAmountsShell(shinFootFactor * i / 6.0F);
         this.fan[i] = latheLoft.getClassForStringForeEar("abd" + (i + 1), head, amountsShell[0], amountsShell[1], amountsShell[2]);
         head = this.fan[i];
      }

      float[] fingerClawValues = getAmountsShell(shinFootFactor);
      this.classShinProgress = latheLoft.getClassForStringForeEar("fan", this.fan[5], fingerClawValues[0], fingerClawValues[1], fingerClawValues[2]);

      for (int j = 0; j < this.shinFootForeEars.length; j++) {
         float shellBound = getAmount(j, shinFootFactor);
         float[] amountsShell3 = getAmountsShell(shellBound);
         float shellBodyScaled = getShellBody(shellBound) * 0.86F;
         this.shinFootForeEars[j] = latheLoft.getClassForStringForeEar(
            "swim" + (j + 1), j == 0 ? this.head : this.fan[j - 1], 0.0F, amountsShell3[1] - amountsShell3[5] * shellBodyScaled, amountsShell3[2] + amountsShell3[4] * shellBodyScaled
         );
      }

      for (int k = 0; k < 5; k++) {
         this.clawR[k] = latheLoft.getClassForStringForeEar("leg_r" + (k + 1), this.shell, 1.55F, 4.4F, clawFingerAmounts[k]);
         this.clawL[k] = latheLoft.getClassForStringForeEar("leg_l" + (k + 1), this.shell, -1.55F, 4.4F, clawFingerAmounts[k]);
      }

      this.fingerR = latheLoft.getClassForStringForeEar("claw_r", this.clawR[0], 3.0F, 4.8F, clawFingerAmounts[0] + -0.9F);
      this.fingerL = latheLoft.getClassForStringForeEar("claw_l", this.clawL[0], -3.0F, 4.8F, clawFingerAmounts[0] + -0.9F);
      float[] fingerClaw = getFingerClaw(1.0F);
      float[] fingerClaw2 = getFingerClaw(-1.0F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("finger_r", this.fingerR, fingerClaw[12], fingerClaw[13], fingerClaw[14]);
      this.fingerClawForeEar = latheLoft.getClassForStringForeEar("finger_l", this.fingerL, fingerClaw2[12], fingerClaw2[13], fingerClaw2[14]);
      latheLoft.getLatheLoft(this.clawR[0]);
      LoftLatheFlag.Region region = latheLoft.getMax(640, 320);
      handleBonecaAmbalabu7(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(128, 96);
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu(), -0.06F);
      LoftLatheFlag.Region max3 = latheLoft.getMax(128, 96);
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu(), 0.06F);
      LoftLatheFlag.Region max4 = latheLoft.getMax(96, 96);
      handleBonecaAmbalabu9(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu3(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(32, 24);
      handleBonecaAmbalabu6(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(256, 128);
      handleBonecaAmbalabu(max7.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max8 = latheLoft.getMax(160, 80);
      handleBonecaAmbalabu4(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu5(max9.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max10 = latheLoft.getMax(16, 96);
      handleBonecaAmbalabu8(max10.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max11 = latheLoft.getMax(32, 32);
      handleBonecaAmbalabuForBonecaAmbalabu(max11.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max12 = latheLoft.getMax(320, 64);
      handleBonecaAmbalabu(max12.getClassBonecaAmbalabu(), 80, 301, 0.3F, 0.66F, true);
      LoftLatheFlag.Region max13 = latheLoft.getMax(320, 64);
      handleBonecaAmbalabu(max13.getClassBonecaAmbalabu(), 96, 201, 0.0F, 0.55F, false);
      ShinBrr classForRegion = latheLoft.getClassForRegion(max13);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max12);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max6);
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion11 = latheLoft.getClassForRegion(max8);
      ShinBrr classForRegion12 = latheLoft.getClassForRegion(max9);
      ShinBrr shinBrr = latheLoft.getMax(15391682);
      ShinBrr shinBrr2 = latheLoft.getMax(15591386);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max10);
      ShinBrr shinBrr3 = latheLoft.getMax(14977642);
      ShinBrr shinBrr4 = latheLoft.getMax(15901830);
      ShinBrr shinBrr5 = latheLoft.getMax(16032643);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max11);
      latheLoft.getLatheLoftForForeEar(this.antL);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, -8.6F);
      latheLoft.getLatheProfile(fingerClawTable, shinFootTable, 26, 1.16F, 0.84F, classForRegion3, null, null);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 7.9F, -10.200001F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLatheProfile(new float[]{4.0F, 5.3F, 6.3F}, new float[]{0.0F, 0.9F, 1.8F}, 18, 1.0F, 0.56F, classForRegion4, null, null);
      latheLoft.getDequeArray();
      this.handle(latheLoft, classForRegion8, classForRegion9, shinBrr, shinBrr2);
      latheLoft.getLatheLoftForForeEar(this.eyeRightSection);
      this.handleForLatheLoftFloatShinBrr(latheLoft, 1.0F, classForRegion5);
      latheLoft.getLatheLoftForForeEar(this.foreEar);
      this.handleForLatheLoftFloatShinBrr(latheLoft, -1.0F, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      this.handleForLatheLoftFloat(latheLoft, 1.0F, classForRegion7);
      latheLoft.getLatheLoftForForeEar(this.earLeftSection);
      this.handleForLatheLoftFloat(latheLoft, -1.0F, classForRegion7);
      latheLoft.getLatheLoftForForeEar(this.antennaRightSection);
      this.handle(latheLoft, 1.0F, shinBrr5);
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      this.handle(latheLoft, -1.0F, shinBrr5);
      latheLoft.getLatheLoftForForeEar(this.head);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 8.5F, -6.4F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLatheProfile(new float[]{3.3F, 3.75F, 3.95F}, new float[]{0.0F, 1.5F, 2.9F}, 16, 1.06F, 1.0F, classForRegion, null, null);
      latheLoft.getDequeArray();
      float[] pointsSpline = LatheLoft.getPointsSpline(2, 0.0F, 6.7F, -6.4F, 0.0F, 7.25F, -3.4F, 0.0F, 7.55F, -0.4F, 0.0F, 7.55F, 2.7F);
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      latheLoft.getLoftPath(
         pointsSpline,
         LatheLoft.getAmounts(latheLoftSize, 2.7F, 3.5F, 3.75F, 2.6F),
         LatheLoft.getAmounts(latheLoftSize, 2.7F, 3.6F, 3.85F, 3.0F),
         16,
         classForRegion10,
         null,
         latheLoft.getMax(16302502),
         0.0F,
         1.0F,
         0.0F
      );

      for (int m = 0; m < 6; m++) {
         latheLoft.getLatheLoftForForeEar(this.fan[m]);
         this.handle(latheLoft, m, shinFootFactor, classForRegion11, classForRegion14, m == 5 ? latheLoft.getMax(15305320) : null);
      }

      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      this.handle(latheLoft, fingerClawValues, classForRegion12);

      for (int p = 0; p < this.shinFootForeEars.length; p++) {
         latheLoft.getLatheLoftForForeEar(this.shinFootForeEars[p]);
         this.handle(latheLoft, p, shinFootFactor, shinBrr4);
      }

      for (int q = 0; q < 5; q++) {
         latheLoft.getLatheLoftForForeEar(this.clawR[q]);
         this.handleAmountsFinger(latheLoft, 1.0F, q, classForRegion13);
         latheLoft.getLatheLoftForForeEar(this.clawL[q]);
         this.handleAmountsFinger(latheLoft, -1.0F, q, classForRegion13);
      }

      latheLoft.getLatheLoftForForeEar(this.fingerR);
      this.handleFingerClaw(latheLoft, 1.0F, this.classShinFoot, classForRegion13, shinBrr3, latheLoft.getMax(13195066));
      latheLoft.getLatheLoftForForeEar(this.fingerL);
      this.handleFingerClaw(latheLoft, -1.0F, this.fingerClawForeEar, classForRegion13, shinBrr3, latheLoft.getMax(13195066));
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressLimit = poseProgress.limbSwingStrength;
      boolean shinProgressFlag = poseProgress.sneakingPose || poseProgress.vehicle || poseProgress.gliding || poseProgress.swimming || poseProgress.poseProgressActive || poseProgress.swingProgress > 0.0F;
      float shinProgressFactor = shinProgressFlag ? 0.0F : 1.0F - getAmount3(shinProgressLimit / 0.15F);
      float shinProgressLimit2 = ((poseProgress.age + (poseProgress.hashCode & 0xFF) * 1.7F) % 220.0F + 220.0F) % 220.0F;
      float shinProgressFactor2 = 0.0F;
      float shinProgressValue = 0.0F;
      float shinProgressTotal = 0.0F;
      float shinProgressBound = 0.0F;
      if (shinProgressFactor > 0.0F && shinProgressLimit2 < 56.0F) {
         shinProgressFactor2 = getAmount2(shinProgressLimit2 / 3.5F) * (1.0F - getAmount2((shinProgressLimit2 - 5.0F) / 6.0F)) * shinProgressFactor;
         shinProgressValue = 6.0F * getAmount2(shinProgressLimit2 / 7.0F) * (1.0F - getAmount2((shinProgressLimit2 - 18.0F) / 36.0F)) * shinProgressFactor;
         shinProgressTotal = 3.0F * (float)Math.pow(Math.sin(Math.PI * getAmount3(shinProgressLimit2 / 12.0F)), 0.45) * shinProgressFactor;
         shinProgressBound = (float)Math.sin(Math.PI * getAmount3((shinProgressLimit2 - 18.0F) / 36.0F)) * shinProgressFactor;
      }

      float shinProgressValue3 = poseProgress.animationProgress * 0.6662F * 3.0F;
      float angle = shinProgressLimit >= shinProgressBound ? shinProgressValue3 : shinProgressLimit2 * 0.9F;
      float max3 = Math.max(shinProgressLimit, shinProgressBound);
      float amount3 = getAmount3(shinProgressTotal / 1.5F);
      float shinProgressValue5 = 0.55F * Math.abs((float)Math.sin(angle * 2.0F)) * max3;
      float sin = poseProgress.getSwingProgress();
      float shinProgressValue6 = 0.035F * (float)Math.cos(angle) * max3;
      foreShinScale.getAmountsForeShin2(this.shell, 0.0F, shinProgressTotal, shinProgressValue);
      foreShinScale.getAmountsForeShin2(this.head, 0.0F, shinProgressValue5, 0.0F);
      foreShinScale.getAmounts(this.head, -0.55F * shinProgressFactor2, 0.0F, shinProgressValue6);

      for (int i = 0; i < 5; i++) {
         for (int j = -1; j <= 1; j += 2) {
            ForeEar foreEar = j > 0 ? this.clawR[i] : this.clawL[i];
            float angle2 = angle + i * 0.85F + (j < 0 ? (float) Math.PI : 0.0F);
            float shinProgressFactor4 = i == 0 ? 0.22F : 0.8F;
            float shinProgressFactor5 = (float)Math.cos(angle2) * shinProgressFactor4 * max3;
            float shinProgressFactor6 = Math.max(0.0F, -((float)Math.sin(angle2))) * 0.9F * max3 + 0.35F * amount3;
            foreShinScale.getAmounts(foreEar, 0.0F, shinProgressFactor5 * j, shinProgressFactor6 * j);
         }
      }

      float shinProgressValue7 = 0.09F * poseProgress.getAgeCode(160.0F, 0.0F) * shinProgressFactor;
      float amountForFloat = getAmountForFloat(poseProgress.shinProgressAmount * 0.8F, 0.7F);
      float dinForeValue = getAmountForFloat(poseProgress.headYawRadians * 0.85F, 1.05F);
      foreShinScale.getAmounts(this.antL, amountForFloat, dinForeValue, -shinProgressValue6 + shinProgressValue7);
      foreShinScale.getAmountsForeShin2(this.antL, 0.0F, -shinProgressValue5, 0.0F);
      float shinProgressFactor8 = (poseProgress.age + (poseProgress.hashCode & 127) * 2.0F) % 130.0F;
      float pilotStatusBound = shinProgressFactor8 < 3.0F ? (float)Math.sin(Math.PI * shinProgressFactor8 / 3.0) : 0.0F;
      if (shinProgressFactor > 0.0F && (shinProgressLimit2 >= 24.0F && shinProgressLimit2 < 27.0F || shinProgressLimit2 >= 30.0F && shinProgressLimit2 < 33.0F)) {
         pilotStatusBound = Math.max(pilotStatusBound, (float)Math.sin(Math.PI * ((shinProgressLimit2 - 24.0F) % 6.0F) / 3.0));
      }

      foreShinScale.getAmountsForForeEarFloat(this.eyeRightSection, 1.0F, (1.0F - 0.88F * pilotStatusBound) * (1.0F - 0.4F * sin), 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.foreEar, 1.0F, (1.0F - 0.88F * pilotStatusBound) * (1.0F - 0.4F * sin), 1.0F);
      float shinProgressFactor9 = (poseProgress.age + (poseProgress.hashCode & 63) * 3.0F) % 90.0F;
      float shinProgressFactor10 = shinProgressFactor9 < 5.0F ? (float)Math.sin(Math.PI * shinProgressFactor9 / 5.0) : 0.0F;
      foreShinScale.getAmounts(this.shinFootForeEar, 0.0F, 0.0F, -0.35F * shinProgressFactor10);
      foreShinScale.getAmounts(this.earLeftSection, 0.0F, 0.0F, 0.04F * poseProgress.getAgeCode(70.0F, 0.5F));
      foreShinScale.getAmounts(this.antennaRightSection, 0.07F * poseProgress.getAgeCode(70.0F, 0.0F) - 0.05F * max3 - 0.8F * amountForFloat, -0.75F * dinForeValue, 0.05F * poseProgress.getAgeCode(95.0F, 1.0F));
      foreShinScale.getAmounts(this.shinProgressForeEar, 0.07F * poseProgress.getAgeCode(70.0F, 1.3F) - 0.05F * max3 - 0.8F * amountForFloat, -0.75F * dinForeValue, -0.05F * poseProgress.getAgeCode(95.0F, 2.0F));

      for (int k = 0; k < 6; k++) {
         float shinProgressValue9 = 0.06F * (float)Math.sin(angle - k * 0.7F + 0.9F) * max3;
         foreShinScale.getAmounts(this.fan[k], shinProgressFactor2 * 0.3F - 0.012F + 0.012F * poseProgress.getAgeCode(80.0F, k * 0.6F), shinProgressValue9, 0.0F);
      }

      foreShinScale.getAmounts(this.classShinProgress, -0.5F * shinProgressFactor2 + 0.06F * poseProgress.getAgeCode(80.0F, 3.0F) - 0.04F, 0.0F, 0.0F);

      for (int m = 0; m < this.shinFootForeEars.length; m++) {
         foreShinScale.getAmounts(this.shinFootForeEars[m], 0.3F * (float)Math.sin(poseProgress.age * 0.35F - m * 0.9F) + 0.4F * shinProgressFactor2, 0.0F, 0.0F);
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         foreShinScale.getAmountsForeShin2(this.shell, 0.0F, 0.0F, -3.0F * sin);
         foreShinScale.getAmountsForeShin2(this.head, 0.0F, 1.1F * sin, -0.3F * sin);
         foreShinScale.getAmountsBoneCount(this.head, 0.1F * sin, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.antL, -0.45F * sin, 0.0F, 0.1F * (float)Math.sin(swingProgress * 2.0F * Math.PI));
         float foreShinValue = 0.4F * (float)Math.sin(swingProgress * 4.0F * Math.PI) * sin;
         foreShinScale.getAmountsBoneCount(this.clawR[0], 0.4F * sin, 0.7F * sin, 0.0F);
         foreShinScale.getAmountsBoneCount(this.fingerR, 1.0F * sin, -0.6F * sin, 0.0F);
         foreShinScale.getAmountsBoneCount(this.clawL[0], 0.4F * sin, -0.7F * sin, 0.0F);
         foreShinScale.getAmountsBoneCount(this.fingerL, 1.0F * sin, 0.6F * sin, 0.0F);
         foreShinScale.getAmounts(this.classShinFoot, foreShinValue, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.fingerClawForeEar, foreShinValue, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.antennaRightSection, 0.4F * sin, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, 0.4F * sin, 0.0F, 0.0F);
         foreShinScale.getClassForForeEarFloat(this.clawR[0], 1.0F + 0.6F * sin);
         foreShinScale.getClassForForeEarFloat(this.fingerR, 1.0F + 0.25F * sin);
         foreShinScale.getClassForForeEarFloat(this.clawL[0], 1.0F + 0.6F * sin);
         foreShinScale.getClassForForeEarFloat(this.fingerL, 1.0F + 0.25F * sin);

         for (int q = 0; q < 6; q++) {
            foreShinScale.getAmountsBoneCount(this.fan[q], (q == 0 ? -0.45F : (q == 1 ? -0.15F : 0.15F)) * sin, 0.0F, 0.0F);
         }

         foreShinScale.getAmountsBoneCount(this.classShinProgress, -0.45F * sin, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.shinFootForeEar, 0.35F * sin, 0.0F, -0.55F * sin);
         foreShinScale.getAmountsBoneCount(this.earLeftSection, 0.35F * sin, 0.0F, 0.55F * sin);
      }

      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsForeShin2(this.shell, 0.0F, -1.0F, 0.0F);

         for (int r = 0; r < 5; r++) {
            foreShinScale.getAmountsBoneCount(this.clawR[r], 0.0F, 0.0F, shinProgressTable[r]);
            foreShinScale.getAmountsBoneCount(this.clawL[r], 0.0F, 0.0F, -shinProgressTable[r]);
         }

         foreShinScale.getAmountsBoneCount(this.shinFootForeEar, 0.35F, 0.0F, -0.55F);
         foreShinScale.getAmountsBoneCount(this.earLeftSection, 0.35F, 0.0F, 0.55F);

         for (int s = 0; s < 6; s++) {
            foreShinScale.getAmountsBoneCount(this.fan[s], -0.035F, 0.0F, 0.0F);
         }

         foreShinScale.getAmountsBoneCount(this.antL, 0.12F, 0.0F, 0.0F);
      }

      if (poseProgress.vehicle) {
         for (int t = 0; t < 5; t++) {
            foreShinScale.getAmountsBoneCount(this.clawR[t], 0.0F, 0.6F, 0.8F);
            foreShinScale.getAmountsBoneCount(this.clawL[t], 0.0F, -0.6F, -0.8F);
         }

         for (int index = 0; index < 6; index++) {
            foreShinScale.getAmountsBoneCount(this.fan[index], -0.04F, 0.0F, 0.0F);
         }
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         for (int index2 = 0; index2 < 5; index2++) {
            foreShinScale.getAmountsBoneCount(this.clawR[index2], 0.0F, -0.5F, 0.3F);
            foreShinScale.getAmountsBoneCount(this.clawL[index2], 0.0F, 0.5F, -0.3F);
         }

         for (int index3 = 0; index3 < 6; index3++) {
            foreShinScale.getAmountsBoneCount(this.fan[index3], -0.06F + 0.1F * (float)Math.sin(poseProgress.age * 0.5F - index3 * 0.8F), 0.0F, 0.0F);
         }
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.head, 1.08F, 0.9F, 1.08F);
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      handleBonecaAmbalabu(bonecaAmbalabu, 15764588, 51);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float absScaled = Math.abs((i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 2.0F;
         float bonecaAmbalabuLimit = 1.0F - Math.abs(absScaled - 0.16F) / 0.045F;
         if (bonecaAmbalabuLimit > 0.0F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(i, bonecaAmbalabu.bonecaValue * 0.04F, i + 1, bonecaAmbalabu.bonecaValue * 0.92F, ForeShinUtils.getValueForIntFloat(16770778, 0.5F * getAmount2(bonecaAmbalabuLimit)));
         }

         float bonecaAmbalabuLimit2 = 1.0F - Math.abs(absScaled - 0.81F) / 0.035F;
         if (bonecaAmbalabuLimit2 > 0.0F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(i, 0.0F, i + 1, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(13853759, 0.6F * getAmount2(bonecaAmbalabuLimit2 * 1.5F)));
         }
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F - bonecaAmbalabu.ambalabuBonecaValue / 160.0F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F + bonecaAmbalabu.ambalabuBonecaValue / 160.0F,
         bonecaAmbalabu.bonecaValue * 0.9F,
         ForeShinUtils.getValueForIntFloat(13853759, 0.6F)
      );

      for (int j = -1; j <= 1; j += 2) {
         float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + j * 0.31F);
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabuValue,
            bonecaAmbalabu.bonecaValue * 0.12F,
            bonecaAmbalabuValue - j * bonecaAmbalabu.ambalabuBonecaValue * 0.025F,
            bonecaAmbalabu.bonecaValue * 0.55F,
            bonecaAmbalabuValue,
            bonecaAmbalabu.bonecaValue * 0.95F,
            bonecaAmbalabu.ambalabuBonecaValue / 80.0F,
            ForeShinUtils.getValueForIntFloat(13853759, 0.55F)
         );
         float classFactor = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + j * 0.06F);
         float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + j * 0.27F);
         bonecaAmbalabu.getClassForFloatInt(
            classFactor,
            bonecaAmbalabu.bonecaValue * 0.42F,
            0.5F * (classFactor + ambalabuBonecaValue),
            bonecaAmbalabu.bonecaValue * 0.36F,
            ambalabuBonecaValue,
            bonecaAmbalabu.bonecaValue * 0.5F,
            bonecaAmbalabu.ambalabuBonecaValue / 60.0F,
            ForeShinUtils.getValueForIntFloat(13853759, 0.8F)
         );
      }
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15899256), ForeShinUtils.getValueForInt(16168601)).getBonecaAmbalabu(0.04F, 2.0F, 93);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.82F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(13590332, 0.55F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue * 0.16F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(13590332, 0.45F));
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15391167)).getBonecaAmbalabu(0.04F, 3.0F, 71);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.62F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(13481882, 0.55F));

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 4; j++) {
            float bonecaAmbalabuValue = 0.1F + j * 0.055F + i * 0.02F;
            float amountForFloat = bonecaAmbalabu.bonecaValue * (0.3F + i * 0.08F);
            bonecaAmbalabu.getClass022(
               bonecaAmbalabu.ambalabuBonecaValue * (0.5F - bonecaAmbalabuValue),
               amountForFloat,
               bonecaAmbalabu.ambalabuBonecaValue / 60.0F,
               bonecaAmbalabu.ambalabuBonecaValue / 60.0F,
               ForeShinUtils.getValueForIntFloat(11836032, 0.8F)
            );
            bonecaAmbalabu.getClass022(
               bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuValue),
               amountForFloat,
               bonecaAmbalabu.ambalabuBonecaValue / 60.0F,
               bonecaAmbalabu.ambalabuBonecaValue / 60.0F,
               ForeShinUtils.getValueForIntFloat(11836032, 0.8F)
            );
         }
      }

      for (int k = 0; k < 260; k++) {
         float classBound = getAmount(k, 1, 73) * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue4 = getAmount(k, 2, 73) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuFactor = classBound < bonecaAmbalabu.ambalabuBonecaValue * 0.5F ? -1.0F : 1.0F;
         bonecaAmbalabu.getClass02(
            classBound,
            bonecaAmbalabuValue4,
            classBound + bonecaAmbalabuFactor * bonecaAmbalabu.ambalabuBonecaValue / 14.0F,
            bonecaAmbalabuValue4 + bonecaAmbalabu.bonecaValue / 40.0F,
            bonecaAmbalabu.ambalabuBonecaValue / 50.0F,
            ForeShinUtils.getValueForIntFloat(getAmount(k, 3, 73) < 0.5F ? 16777215 : 12099722, 0.22F)
         );
      }

      bonecaAmbalabu.getClassForFloatInt(
         bonecaAmbalabu.ambalabuBonecaValue * 0.2F,
         bonecaAmbalabu.bonecaValue * 0.58F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * 0.88F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.8F,
         bonecaAmbalabu.bonecaValue * 0.58F,
         bonecaAmbalabu.ambalabuBonecaValue / 18.0F,
         ForeShinUtils.getValueForInt(5911336)
      );
   }

   private static float getAmount(float fingerClawValue, float fingerClawValue2) {
      float amountsForFloat = getAmountsForFloat(fingerClawValue2);
      float fingerClawFactor = fingerClawValue / (amountsForFloat * 1.16F);
      return -8.6F - 0.84F * amountsForFloat * (float)Math.sqrt(Math.max(0.0F, 1.0F - fingerClawFactor * fingerClawFactor));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue) {
      float bonecaAmbalabuFactor = 1.8849558F;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuLimit = (i + 0.5F) / bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor;
         float sin = (float)Math.sin(Math.min((double)bonecaAmbalabuLimit, Math.PI / 2));
         int classValue;
         if (bonecaAmbalabuLimit > 1.4765485471872026) {
            classValue = ForeShinUtils.getValueForInt(2364940);
         } else if (sin < 0.38F) {
            classValue = ForeShinUtils.getValueForInt(10464838);
         } else if (sin < 0.66F) {
            classValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(10464838), ForeShinUtils.getValueForInt(13934650), (sin - 0.38F) / 0.28F);
         } else if (sin < 0.9F) {
            classValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13934650), ForeShinUtils.getValueForInt(12024354), (sin - 0.66F) / 0.24F);
         } else {
            classValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12024354), ForeShinUtils.getValueForInt(4860944), (sin - 0.9F) / 0.1F);
         }

         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, classValue);
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(9069592, 0.22F), 5.0F * bonecaAmbalabu.ambalabuBonecaValue / 96.0F, 1.4F * bonecaAmbalabu.ambalabuBonecaValue / 96.0F, 0.0F);
      bonecaAmbalabu.getBonecaAmbalabu(0.06F, 3.0F * bonecaAmbalabu.ambalabuBonecaValue / 96.0F, 31);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(921102), (bonecaAmbalabuValue3, shiftx) -> {
         float[] var4x = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, bonecaAmbalabuValue3, shiftx);
         return var4x != null && getAmount((var4x[0] - bonecaAmbalabuValue) / 0.46F) + getAmount(var4x[1] / 0.8F) <= 1.0F;
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(16777215), (bonecaAmbalabuValue4, bonecaAmbalabuValue5) -> {
         float[] var4x = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, bonecaAmbalabuValue4, bonecaAmbalabuValue5);
         if (var4x == null) {
            return false;
         } else {
            float var5x = var4x[0] - bonecaAmbalabuValue - 0.3F;
            float var6x = var4x[1] - 0.42F;
            return var5x * var5x + var6x * var6x <= 0.028900001F;
         }
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(16777215, 0.7F), (shiftx, bonecaAmbalabuValue6) -> {
         float[] var4x = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, shiftx, bonecaAmbalabuValue6);
         if (var4x == null) {
            return false;
         } else {
            float var5x = var4x[0] - bonecaAmbalabuValue + 0.26F;
            float var6x = var4x[1] + 0.36F;
            return var5x * var5x + var6x * var6x <= 0.0049F;
         }
      });
   }

   private void handle(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      float fingerClawValue = getAmount(0.78F, 8.0F) - 0.18F;

      for (int i = -1; i <= 1; i += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(i * 0.78F, 8.0F, fingerClawValue).getLatheLoft4(i * -12.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.86F, 0.7F, 0.62F, 10, shinBrr);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoft(0.0F, 7.35F, getAmount(0.0F, 7.35F) - 0.45F, 1.15F, 0.5F, 0.7F, 8, shinBrr3);
      float armTrunkValue = getAmount(0.0F, 8.95F) - 0.62F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 8.95F, armTrunkValue).getLatheLoftLatheLoftAmountLoft(22.0F).getLatheLoft3(1.15F);
      latheLoft.getPolygonExtrude(
         new float[]{0.0F, -0.48F, 0.3F, -0.24F, 0.6F, 0.18F, 0.5F, 0.43F, 0.0F, 0.48F, -0.5F, 0.43F, -0.6F, 0.18F, -0.3F, -0.24F}, 0.0F, 0.55F, shinBrr2, shinBrr2, shinBrr2
      );
      latheLoft.getDequeArray();

      for (int j = -1; j <= 1; j += 2) {
         for (int k = 0; k < 5; k++) {
            float angle = (float)Math.toRadians(16.0F - k * 7.0F);
            float fingerClawValue3 = 0.3F + 0.08F * k;
            float fingerClawValue4 = 0.3F - k * 0.17F;
            float fingerClawValue5 = j * (0.78F + fingerClawValue3);
            float fingerClawValue6 = 8.0F + fingerClawValue4;
            float fingerClawValue7 = fingerClawValue - 0.62F * (float)Math.sqrt(Math.max(0.04F, 1.0F - getAmount(fingerClawValue3 / 0.86F) - getAmount(fingerClawValue4 / 0.7F))) + 0.06F;
            float fingerClawFactor = j * (float)Math.cos(angle) * 0.94F;
            float sin = (float)Math.sin(angle);
            float fingerClawFactor2 = 0.28F;
            float distance = (float)Math.sqrt(fingerClawFactor * fingerClawFactor + sin * sin + fingerClawFactor2 * fingerClawFactor2);
            float fingerClawFactor3 = 5.6F - Math.abs(k - 2.0F) * 0.25F;
            float fingerClawValue8 = 0.15F * fingerClawFactor3 / 5.0F;
            float fingerClawFactor4 = fingerClawFactor / distance;
            float fingerClawFactor5 = sin / distance;
            float fingerClawFactor6 = fingerClawFactor2 / distance;
            latheLoft.getLatheLoft(
               new float[]{
                  fingerClawValue5,
                  fingerClawValue6,
                  fingerClawValue7,
                  fingerClawValue5 + fingerClawFactor4 * fingerClawFactor3 * 0.5F,
                  fingerClawValue6 + fingerClawFactor5 * fingerClawFactor3 * 0.5F - fingerClawValue8 * 0.75F,
                  fingerClawValue7 + fingerClawFactor6 * fingerClawFactor3 * 0.5F,
                  fingerClawValue5 + fingerClawFactor4 * fingerClawFactor3,
                  fingerClawValue6 + fingerClawFactor5 * fingerClawFactor3 - fingerClawValue8 * 3.0F,
                  fingerClawValue7 + fingerClawFactor6 * fingerClawFactor3
               },
               new float[]{0.1F, 0.07F, 0.04F},
               4,
               shinBrr4,
               false
            );
         }
      }
   }

   private static float getAmount(float fingerClawFactor) {
      return fingerClawFactor * fingerClawFactor;
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      handleBonecaAmbalabu(bonecaAmbalabu, 15899256, 57);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.14F, ForeShinUtils.getValueForIntFloat(13198399, 0.55F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.18F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.26F, ForeShinUtils.getValueForIntFloat(16769234, 0.22F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.76F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(13590332, 0.85F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.72F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.76F, ForeShinUtils.getValueForIntFloat(13590332, 0.4F));
   }

   private static float getAmountForFloat(float fingerClawLimit, float fingerClawValue) {
      return fingerClawLimit < -fingerClawValue ? -fingerClawValue : (fingerClawLimit > fingerClawValue ? fingerClawValue : fingerClawLimit);
   }

   @Override
   public String formatTungDin() {
      return "trippi_troppi";
   }

   private void handle(LatheLoft latheLoft, float fingerClawValue, ShinBrr shinBrr) {
      float[] pointsSpline = LatheLoft.getPointsSpline(2, fingerClawValue * 1.6F, 11.8F, -4.4000006F, fingerClawValue * 2.4F, 13.6F, -2.2F, fingerClawValue * 3.0F, 14.6F, 4.5F, fingerClawValue * 4.4F, 10.8F, 16.5F);
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(latheLoftSize, 0.16F, 0.11F, 0.07F, 0.03F), 4, shinBrr, false);
   }

   private static float getAmount(int fingerClawValue, float fingerClawFactor) {
      return fingerClawValue == 0 ? 0.25F : fingerClawFactor * (fingerClawValue - 0.45F) / 6.0F;
   }

   @Override
   public float getAmountFloat() {
      return 1.4F;
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         int classValue;
         if (bonecaAmbalabuValue < 0.28F) {
            classValue = ForeShinUtils.getValueForInt(16171952);
         } else if (bonecaAmbalabuValue < 0.32F) {
            classValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(16171952), ForeShinUtils.getValueForInt(12141610), (bonecaAmbalabuValue - 0.28F) / 0.04F);
         } else if (bonecaAmbalabuValue < 0.41F) {
            classValue = ForeShinUtils.getValueForInt(12141610);
         } else if (bonecaAmbalabuValue < 0.47F) {
            classValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12141610), ForeShinUtils.getValueForInt(14835775), (bonecaAmbalabuValue - 0.41F) / 0.06F);
         } else {
            classValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14835775), ForeShinUtils.getValueForInt(15629916), (bonecaAmbalabuValue - 0.47F) / 0.53F);
         }

         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, classValue);
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(16769748, 0.18F), 4.0F, 1.0F, 0.0F);
      bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.08F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, (float)bonecaAmbalabu.bonecaValue, 1.5F, ForeShinUtils.getValueForIntFloat(12141610, 0.6F));
      bonecaAmbalabu.getBonecaAmbalabu(0.04F, 3.0F, 91);
   }

   public FingerClaw() {
      this.clawR = new ForeEar[5];
      this.clawL = new ForeEar[5];
   }

   private void handleAmountsFinger(LatheLoft latheLoft, float fingerFactor, int index, ShinBrr shinBrr) {
      float fingerValue = clawFingerAmounts[index];
      float fingerValue2 = fingerTable[index];
      float fingerValue3 = fingerFactor * 1.55F;
      float fingerValue4 = 4.4F;
      if (index == 0) {
         float fingerValue5 = fingerFactor * 3.0F;
         float fingerValue6 = 4.8F;
         float fingerValue7 = fingerValue + -0.9F;
         latheLoft.getLatheLoft(
            new float[]{fingerValue3, fingerValue4, fingerValue, fingerValue5 + (fingerValue5 - fingerValue3) * 0.12F, fingerValue6 + (fingerValue6 - fingerValue4) * 0.12F, fingerValue7 + (fingerValue7 - fingerValue) * 0.12F},
            new float[]{0.24F, 0.25F},
            6,
            shinBrr,
            false
         );
      } else {
         float fingerFactor2 = index == 4 ? 1.1F : 1.0F;
         float fingerValue8 = fingerFactor * 4.1F;
         float fingerValue9 = 6.3F;
         float fingerValue10 = fingerValue + fingerValue2 * 0.4F;
         float fingerValue11 = fingerFactor * table[index];
         float fingerValue12 = 2.6F;
         float fingerValue13 = fingerValue + fingerValue2 * 1.0F * fingerFactor2;
         float fingerValue14 = fingerFactor * FINGER_SEGMENT_OFFSETS[index];
         float fingerValue15 = 0.05F;
         float fingerValue16 = fingerValue + fingerValue2 * 1.35F * fingerFactor2;
         latheLoft.getLatheLoft(
            new float[]{fingerValue3, fingerValue4, fingerValue, fingerValue8, fingerValue9, fingerValue10, fingerValue11, fingerValue12, fingerValue13, fingerValue14, fingerValue15, fingerValue16}, new float[]{0.26F, 0.28F, 0.11F, 0.03F}, 5, shinBrr, false
         );
      }
   }

   private static float getAmount(int fingerClawValue, int fingerClawValue2, int bonecaAmbalabuValue) {
      int fingerClawValue4 = fingerClawValue * 374761393 + fingerClawValue2 * 668265263 + bonecaAmbalabuValue * 1442695041;
      int fingerClawValue5 = (fingerClawValue4 ^ fingerClawValue4 >>> 13) * 1274126177;
      int fingerClawValue6 = fingerClawValue5 ^ fingerClawValue5 >>> 16;
      return (fingerClawValue6 & 16777215) / 1.6777215E7F;
   }

   @Override
   public String formatTungDinString() {
      return "Trippi Troppi";
   }

   private static float getAmounts(float fingerClawLimit) {
      float fingerClawFactor = 1.0F;
      int fingerClawSize = shinFootTable.length;
      float[] floats = new float[fingerClawSize];

      for (int i = 1; i < fingerClawSize; i++) {
         float fingerClawFactor2 = (fingerClawTable[i] - fingerClawTable[i - 1]) * fingerClawFactor;
         float fingerClawFactor3 = shinFootTable[i] - shinFootTable[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(fingerClawFactor2 * fingerClawFactor2 + fingerClawFactor3 * fingerClawFactor3);
      }

      float fingerClawValue = fingerClawLimit >= shinFootTable[fingerClawSize - 1] ? floats[fingerClawSize - 1] : 0.0F;
      if (fingerClawLimit > shinFootTable[0] && fingerClawLimit < shinFootTable[fingerClawSize - 1]) {
         for (int j = 0; j + 1 < fingerClawSize; j++) {
            if (fingerClawLimit <= shinFootTable[j + 1]) {
               float fingerClawFactor4 = (fingerClawLimit - shinFootTable[j]) / (shinFootTable[j + 1] - shinFootTable[j]);
               fingerClawValue = floats[j] + (floats[j + 1] - floats[j]) * fingerClawFactor4;
               break;
            }
         }
      }

      return 1.0F - fingerClawValue / floats[fingerClawSize - 1];
   }

   private static float[] getFingerClaw(float fingerClawValue) {
      float fingerClawValue2 = clawFingerAmounts[0];
      float sideScaled = fingerClawValue * 3.7F;
      float fingerClawValue3 = 2.4F;
      float fingerClawValue4 = fingerClawValue2 - 2.6F;
      float fingerClawFactor = -fingerClawValue * 0.22F;
      float fingerClawFactor2 = 0.3F;
      float fingerClawFactor3 = -0.9F;
      float distance = (float)Math.sqrt(fingerClawFactor * fingerClawFactor + fingerClawFactor2 * fingerClawFactor2 + fingerClawFactor3 * fingerClawFactor3);
      float fingerClawFactor4 = fingerClawFactor / distance;
      float fingerClawFactor5 = fingerClawFactor2 / distance;
      float fingerClawFactor6 = fingerClawFactor3 / distance;
      float fingerClawFactor7 = -fingerClawFactor4 * fingerClawFactor5;
      float fingerClawFactor8 = 1.0F - fingerClawFactor5 * fingerClawFactor5;
      float fingerClawFactor9 = -fingerClawFactor6 * fingerClawFactor5;
      float sqrt2 = (float)Math.sqrt(fingerClawFactor7 * fingerClawFactor7 + fingerClawFactor8 * fingerClawFactor8 + fingerClawFactor9 * fingerClawFactor9);
      float fingerClawValue5 = fingerClawFactor7 / sqrt2;
      float fingerClawValue6 = fingerClawFactor8 / sqrt2;
      float fingerClawValue7 = fingerClawFactor9 / sqrt2;
      float fingerClawFactor10 = 1.2F;
      float fingerClawValue8 = sideScaled + fingerClawFactor4 * fingerClawFactor10;
      float fingerClawValue9 = fingerClawValue3 + fingerClawFactor5 * fingerClawFactor10;
      float fingerClawValue10 = fingerClawValue4 + fingerClawFactor6 * fingerClawFactor10;
      return new float[]{
         sideScaled,
         fingerClawValue3,
         fingerClawValue4,
         fingerClawFactor4,
         fingerClawFactor5,
         fingerClawFactor6,
         fingerClawValue5,
         fingerClawValue6,
         fingerClawValue7,
         fingerClawValue8,
         fingerClawValue9,
         fingerClawValue10,
         fingerClawValue8 + fingerClawValue5 * 0.12F - fingerClawFactor4 * 0.12F,
         fingerClawValue9 + fingerClawValue6 * 0.12F - fingerClawFactor5 * 0.12F,
         fingerClawValue10 + fingerClawValue7 * 0.12F - fingerClawFactor6 * 0.12F
      };
   }

   private void handleForLatheLoftFloat(LatheLoft latheLoft, float fingerClawValue, ShinBrr shinBrr) {
      float[] pointsSpline = LatheLoft.getPointsSpline(2, fingerClawValue * 2.6F, 12.3F, -8.3F, fingerClawValue * 3.18F, 14.5F, -8.450001F, fingerClawValue * 3.75F, 16.4F, -8.150001F);
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      latheLoft.getLoftPath(
         pointsSpline,
         LatheLoft.getAmounts(latheLoftSize, 1.6F, 1.25F, 0.04F),
         LatheLoft.getAmounts(latheLoftSize, 0.58F, 0.42F, 0.03F),
         8,
         shinBrr,
         latheLoft.getMax(13204018),
         null,
         0.0F,
         0.0F,
         -1.0F
      );
   }

   private void handleForLatheLoftFloatShinBrr(LatheLoft latheLoft, float fingerClawValue, ShinBrr shinBrr) {
      float sideScaled = fingerClawValue * 1.8F;
      latheLoft.getDequeArrayArrayDeque()
         .getLatheLoft(sideScaled, 10.7F, getAmount(sideScaled, 10.7F))
         .getLatheLoft4(getAmount3(sideScaled, 10.7F) * 0.7F)
         .getLatheLoft(0.0F, 0.0F, 0.42F)
         .getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 1.12F, 0.8F, 1.12F, 16, 0.0F, 0.6F, shinBrr);
      latheLoft.getDequeArray();
   }

   @Override
   protected int getValueInt() {
      return 1024;
   }

   private static int getValue(float fingerClawValue, float fingerClawValue2) {
      return ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14256443), ForeShinUtils.getValueForInt(15918028), getAmount2((fingerClawValue - fingerClawValue2) / 0.25F));
   }

   private static float getAmount2(float fingerClawFactor) {
      fingerClawFactor = getAmount3(fingerClawFactor);
      return fingerClawFactor * fingerClawFactor * (3.0F - 2.0F * fingerClawFactor);
   }

   private static float[] getAmountsShell(float shellBound) {
      int shellValue = bodyShellAmounts.length;
      shellBound = Math.max(0.0F, Math.min(bodyShellAmounts[shellValue - 1], shellBound));
      int index = 0;

      while (index < shellValue - 2 && bodyShellAmounts[index + 1] < shellBound) {
         index++;
      }

      float shellLimit = bodyShellAmounts[index + 1] - bodyShellAmounts[index];
      float shellFactor = shellLimit > 0.0F ? (shellBound - bodyShellAmounts[index]) / shellLimit : 0.0F;
      float shellValue2 = pointsSpline[3 * index] + (pointsSpline[3 * index + 3] - pointsSpline[3 * index]) * shellFactor;
      float shellValue3 = pointsSpline[3 * index + 1] + (pointsSpline[3 * index + 4] - pointsSpline[3 * index + 1]) * shellFactor;
      float shellValue4 = pointsSpline[3 * index + 2] + (pointsSpline[3 * index + 5] - pointsSpline[3 * index + 2]) * shellFactor;
      float shellFactor2 = pointsSpline[3 * index + 3] - pointsSpline[3 * index];
      float shellFactor3 = pointsSpline[3 * index + 4] - pointsSpline[3 * index + 1];
      float shellFactor4 = pointsSpline[3 * index + 5] - pointsSpline[3 * index + 2];
      float distance = (float)Math.sqrt(shellFactor2 * shellFactor2 + shellFactor3 * shellFactor3 + shellFactor4 * shellFactor4);
      return new float[]{shellValue2, shellValue3, shellValue4, shellFactor2 / distance, shellFactor3 / distance, shellFactor4 / distance};
   }

   @Override
   public int getValue() {
      return 2;
   }

   private void handle(LatheLoft latheLoft, int fingerClawValue, float fingerClawFactor, ShinBrr shinBrr) {
      float shellBound = getAmount(fingerClawValue, fingerClawFactor);
      float[] amountsShell = getAmountsShell(shellBound);
      float shellBodyScaled = getShellBody(shellBound) * 0.86F;
      float armTrunkValue = amountsShell[1] - amountsShell[5] * shellBodyScaled;
      float fingerClawValue5 = amountsShell[2] + amountsShell[4] * shellBodyScaled;
      float degrees = (float)Math.toDegrees(Math.atan2(-amountsShell[4], amountsShell[5]));
      float fingerClawValue6 = fingerClawValue == 0 ? 0.8F : 1.15F - (fingerClawValue - 1) * 0.08F;
      float[] floats = new float[]{0.22F, 0.0F, 0.34F, -fingerClawValue6 * 0.55F, 0.12F, -fingerClawValue6, -0.22F, 0.0F};

      for (int i = -1; i <= 1; i += 2) {
         for (int j = fingerClawValue == 0 ? 1 : -1; j <= 1; j += 2) {
            latheLoft.getDequeArrayArrayDeque()
               .getLatheLoft(i * shellBodyScaled * 0.45F, armTrunkValue, fingerClawValue5 + j * 0.12F)
               .getLatheLoftLatheLoftAmountLoft(degrees)
               .getLatheLoft(i * 20.0F + j * 12.0F)
               .getLatheLoft4(90.0F);
            latheLoft.getPolygonExtrude(floats, -0.05F, 0.05F, shinBrr, shinBrr, null);
            latheLoft.getDequeArray();
         }
      }
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15376544), ForeShinUtils.getValueForInt(14252926)).getBonecaAmbalabu(0.05F, 2.0F, 81);
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.25F, bonecaAmbalabu.ambalabuBonecaValue * 0.25F, bonecaAmbalabu.bonecaValue * 0.12F, ForeShinUtils.getValueForIntFloat(16172226, 0.6F));
      bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.62F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, (float)bonecaAmbalabu.bonecaValue, 1.4F, ForeShinUtils.getValueForIntFloat(10374738, 0.8F));
      bonecaAmbalabu.getClassForFloatInt(
         bonecaAmbalabu.ambalabuBonecaValue * 0.26F,
         bonecaAmbalabu.bonecaValue * 0.5F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.3F,
         bonecaAmbalabu.bonecaValue * 0.7F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.42F,
         bonecaAmbalabu.bonecaValue * 0.72F,
         1.6F,
         ForeShinUtils.getValueForIntFloat(9190980, 0.85F)
      );
      bonecaAmbalabu.getClassForFloatInt(
         bonecaAmbalabu.ambalabuBonecaValue * 0.74F,
         bonecaAmbalabu.bonecaValue * 0.5F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.7F,
         bonecaAmbalabu.bonecaValue * 0.7F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.58F,
         bonecaAmbalabu.bonecaValue * 0.72F,
         1.6F,
         ForeShinUtils.getValueForIntFloat(9190980, 0.85F)
      );
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float fingerClawValue) {
      return bonecaAmbalabu.ambalabuBonecaValue * LatheLoft.getAmount(bonecaAmbalabuValue, getAmountsForFloat(fingerClawValue) * 1.16F);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue, int m) {
      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float absScaled = Math.abs((i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 2.0F;
         int foreShinValue;
         if (absScaled < 0.55F) {
            foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValue(ForeShinUtils.getValueForInt(bonecaAmbalabuValue), 0.93F), ForeShinUtils.getValueForInt(bonecaAmbalabuValue), absScaled / 0.55F);
         } else {
            foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(bonecaAmbalabuValue), ForeShinUtils.getValueForInt(16302502), getAmount2((absScaled - 0.55F) / 0.3F));
         }

         int classValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(16766144), 0.35F * (1.0F - getAmount2(absScaled * 0.5F / 0.22F)));
         bonecaAmbalabu.getClassBonecaAmbalabu(i, 0.0F, i + 1, bonecaAmbalabu.bonecaValue, classValue);
      }

      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(14245950, 0.35F), 0.9F, bonecaAmbalabu.ambalabuBonecaValue / 210.0F, m);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.9F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(13590332, 0.6F));
      bonecaAmbalabu.getBonecaAmbalabu(0.035F, bonecaAmbalabu.ambalabuBonecaValue / 32.0F, m + 1);
   }

   private void handle(LatheLoft latheLoft, float[] fingerClawValues, ShinBrr shinBrr) {
      float fingerClawValue = fingerClawValues[1] - fingerClawValues[4] * 0.35F;
      float degrees = (float)Math.toDegrees(Math.asin(Math.max(-1.0F, Math.min(1.0F, (fingerClawValue - 2.4F) / 5.3F))));
      float[] floats = new float[]{-0.5F, 0.0F, -0.8F, 1.6F, -0.82F, 3.3F, -0.55F, 4.8F, 0.0F, 5.95F, 0.55F, 4.8F, 0.82F, 3.3F, 0.8F, 1.6F, 0.5F, 0.0F};
      float[] floats2 = new float[]{
         -0.45F, 0.0F, -1.25F, 1.2F, -1.6F, 2.8F, -1.4F, 4.3F, -0.7F, 5.3F, 0.2F, 5.6F, 0.95F, 4.9F, 1.25F, 3.5F, 1.0F, 1.7F, 0.45F, 0.0F
      };
      float[] floats3 = new float[]{0.0F, 30.0F, -30.0F, 58.0F, -58.0F};

      for (int i = 0; i < floats3.length; i++) {
         float signum = Math.signum(floats3[i]);
         float fingerClawValue2 = i == 0 ? 0.0F : signum * (i < 3 ? 0.55F : 1.0F);
         float fingerClawValue3 = i < 3 ? 0.88F : 0.8F;
         latheLoft.getDequeArrayArrayDeque()
            .getLatheLoft(fingerClawValues[0] + fingerClawValue2, fingerClawValue + (i == 0 ? 0.05F : -0.05F * i), fingerClawValues[2] - fingerClawValues[5] * 0.35F)
            .getLatheLoftLatheLoftAmountLoft(degrees)
            .getLatheLoft4(floats3[i])
            .getLatheLoft(signum * (i < 3 ? 5.0F : 7.1F))
            .getLatheLoftLatheLoftAmountLoft(90.0F)
            .getLatheLoftForFloat(floats3[i] > 0.0F ? -fingerClawValue3 : fingerClawValue3, fingerClawValue3, 1.0F);
         latheLoft.getPolygonExtrude(i == 0 ? floats : floats2, -0.06F, 0.06F, shinBrr, shinBrr, null);
         latheLoft.getDequeArray();
      }
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue / 320.0F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14256443))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(12874284), 0.4F, 7.0F * bonecaAmbalabuFactor, 11)
         .getBonecaAmbalabu(0.05F, 2.5F * bonecaAmbalabuFactor, 5);
      float classValue = getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 7.6F);
      float bonecaAmbalabuValue = getAmount(bonecaAmbalabu, 7.6F);
      float classDivisor = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, 2.7F, 7.6F) - classValue);
      float bonecaAmbalabuValue3 = 0.5F * (getAmount(bonecaAmbalabu, 5.9F) - getAmount(bonecaAmbalabu, 9.5F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, getAmount(bonecaAmbalabu, 6.6F), bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(15918028, 0.6F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, getAmount(bonecaAmbalabu, 6.2F), bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(15918028));
      bonecaAmbalabu.getClass022(classValue, bonecaAmbalabuValue, classDivisor * 1.18F, bonecaAmbalabuValue3 * 1.18F, ForeShinUtils.getValueForIntFloat(15918028, 0.45F));
      bonecaAmbalabu.getClass022(classValue, bonecaAmbalabuValue, classDivisor, bonecaAmbalabuValue3, ForeShinUtils.getValueForInt(15918028));
      bonecaAmbalabu.getClass022(
         classValue,
         getAmount(bonecaAmbalabu, 9.3F),
         Math.abs(getBonecaAmbalabu(bonecaAmbalabu, 0.6F, 9.3F) - classValue),
         0.5F * (getAmount(bonecaAmbalabu, 8.6F) - getAmount(bonecaAmbalabu, 10.2F)),
         ForeShinUtils.getValueForIntFloat(15918028, 0.8F)
      );

      for (int i = -1; i <= 1; i += 2) {
         float bonecaAmbalabu3 = getBonecaAmbalabu(bonecaAmbalabu, i * 70.0F);
         bonecaAmbalabu.getClass022(
            bonecaAmbalabu3, getAmount(bonecaAmbalabu, 8.4F), bonecaAmbalabu.ambalabuBonecaValue * 0.07F, (getAmount(bonecaAmbalabu, 6.8F) - getAmount(bonecaAmbalabu, 10.2F)) * 0.5F, ForeShinUtils.getValueForIntFloat(15247461, 0.5F)
         );
      }

      float bonecaAmbalabuValue4 = getAmount(bonecaAmbalabu, 7.5F);
      float bonecaAmbalabuValue5 = getAmount(bonecaAmbalabu, 6.9F);

      for (int j = (int)bonecaAmbalabuValue4; j < bonecaAmbalabu.bonecaValue; j++) {
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, j, bonecaAmbalabu.ambalabuBonecaValue, j + 1, ForeShinUtils.getValueForIntFloat(16774888, getAmount2((j + 0.5F - bonecaAmbalabuValue4) / (bonecaAmbalabuValue5 - bonecaAmbalabuValue4))));
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 5200, 9.0F * bonecaAmbalabuFactor, 1.3F * bonecaAmbalabuFactor, 0, 0.13F, 7.0F, 0.0F, 101);
      handleBonecaAmbalabu(bonecaAmbalabu, 5200, 8.0F * bonecaAmbalabuFactor, 1.2F * bonecaAmbalabuFactor, 16773596, 0.2F, 0.0F, 0.2F, 103);
      int valueForIntFloat = ForeShinUtils.getValueForIntFloat(10900252, 0.85F);

      for (int k = -3; k <= 3; k++) {
         float bonecaAmbalabu4 = getBonecaAmbalabu(bonecaAmbalabu, 180.0F + k * 21.0F);
         float bonecaAmbalabuValue7 = (k % 2 == 0 ? 3.0F : -3.0F) * bonecaAmbalabuFactor;
         bonecaAmbalabu.getClassForFloatInt(bonecaAmbalabu4, getAmount(bonecaAmbalabu, 14.3F), bonecaAmbalabu4 + bonecaAmbalabuValue7, getAmount(bonecaAmbalabu, 11.0F), bonecaAmbalabu4 - bonecaAmbalabuValue7 * 0.5F, getAmount(bonecaAmbalabu, 7.8F), 6.5F * bonecaAmbalabuFactor, valueForIntFloat);
      }

      for (int m = -1; m <= 1; m += 2) {
         for (int p = 0; p < 2; p++) {
            float classFactor = getBonecaAmbalabu(bonecaAmbalabu, m * (100.0F + p * 22.0F));
            bonecaAmbalabu.getClassForFloatInt(
               classFactor,
               getAmount(bonecaAmbalabu, 13.9F),
               classFactor + m * 3.0F * bonecaAmbalabuFactor,
               getAmount(bonecaAmbalabu, 11.5F),
               classFactor - m * 2.0F * bonecaAmbalabuFactor,
               getAmount(bonecaAmbalabu, 9.4F),
               5.5F * bonecaAmbalabuFactor,
               valueForIntFloat
            );
         }

         for (int q = 0; q < 3; q++) {
            float fingerClawValue = 9.8F - q * 0.85F;
            float bonecaAmbalabu6 = getBonecaAmbalabu(bonecaAmbalabu, m * 46.0F);
            float bonecaAmbalabu7 = getBonecaAmbalabu(bonecaAmbalabu, m * 84.0F);
            bonecaAmbalabu.getClassForFloatInt(
               bonecaAmbalabu6,
               getAmount(bonecaAmbalabu, fingerClawValue),
               0.5F * (bonecaAmbalabu6 + bonecaAmbalabu7),
               getAmount(bonecaAmbalabu, fingerClawValue + 0.35F),
               bonecaAmbalabu7,
               getAmount(bonecaAmbalabu, fingerClawValue - 0.45F),
               (4.2F - q * 0.6F) * bonecaAmbalabuFactor,
               valueForIntFloat
            );
         }
      }

      float[] floats = new float[]{-1.85F, -1.2F, 0.0F, 1.2F, 1.85F};
      float[] floats2 = new float[]{11.95F, 13.1F, 12.25F, 13.1F, 11.95F};
      float[] classValues = new float[5];
      float[] floats4 = new float[5];

      for (int r = 0; r < 5; r++) {
         classValues[r] = getBonecaAmbalabu(bonecaAmbalabu, floats[r], floats2[r]);
         floats4[r] = getAmount(bonecaAmbalabu, floats2[r]);
      }

      bonecaAmbalabu.getClassForFloatArrayFloat(classValues, floats4, 4.6F * bonecaAmbalabuFactor, ForeShinUtils.getValueForInt(10900252));
      bonecaAmbalabu.getClass02(
         getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 12.3F),
         getAmount(bonecaAmbalabu, 12.3F),
         getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 14.2F),
         getAmount(bonecaAmbalabu, 14.2F),
         4.2F * bonecaAmbalabuFactor,
         ForeShinUtils.getValueForInt(10900252)
      );

      for (int s = -1; s <= 1; s += 2) {
         bonecaAmbalabu.getClass02(
            getBonecaAmbalabu(bonecaAmbalabu, s * 0.75F, 13.3F),
            getAmount(bonecaAmbalabu, 13.3F),
            getBonecaAmbalabu(bonecaAmbalabu, s * 18.0F),
            getAmount(bonecaAmbalabu, 14.45F),
            3.6F * bonecaAmbalabuFactor,
            ForeShinUtils.getValueForIntFloat(10900252, 0.9F)
         );
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 2600, 7.0F * bonecaAmbalabuFactor, 1.1F * bonecaAmbalabuFactor, 14256443, 0.45F, 7.6F, 0.15F, 107);
      bonecaAmbalabu.getClass022(
         classValue,
         getAmount(bonecaAmbalabu, 7.3F),
         Math.abs(getBonecaAmbalabu(bonecaAmbalabu, 1.9F, 7.3F) - classValue),
         0.5F * (getAmount(bonecaAmbalabu, 6.5F) - getAmount(bonecaAmbalabu, 8.1F)),
         ForeShinUtils.getValueForInt(15918028)
      );

      for (int t = -1; t <= 1; t += 2) {
         float bonecaAmbalabu8 = getBonecaAmbalabu(bonecaAmbalabu, t * 1.8F, 10.7F);
         float amountForFloat = getAmount(bonecaAmbalabu, 10.7F);
         float abs2 = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, t * 2.92F, 10.7F) - bonecaAmbalabu8);
         float abs10 = Math.abs(getAmount(bonecaAmbalabu, 11.82F) - amountForFloat);
         bonecaAmbalabu.getClass022(bonecaAmbalabu8, amountForFloat, abs2 * 1.42F, abs10 * 1.42F, ForeShinUtils.getValueForIntFloat(15247461, 0.7F));
         bonecaAmbalabu.getClass022(bonecaAmbalabu8, amountForFloat + abs10 * 0.2F, abs2 * 1.28F, abs10 * 1.2F, ForeShinUtils.getValueForIntFloat(15918028, 0.42F));
         float bonecaAmbalabu9 = getBonecaAmbalabu(bonecaAmbalabu, t * 2.808F, 10.599999F);
         float ambalabuBonecaValue = getBonecaAmbalabu(bonecaAmbalabu, t * 4.62F, 10.15F);
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu9,
            getAmount(bonecaAmbalabu, 10.599999F),
            0.5F * (bonecaAmbalabu9 + ambalabuBonecaValue),
            getAmount(bonecaAmbalabu, 10.55F),
            ambalabuBonecaValue,
            getAmount(bonecaAmbalabu, 10.099999F),
            3.2F * bonecaAmbalabuFactor,
            ForeShinUtils.getValueForIntFloat(10900252, 0.95F)
         );
         bonecaAmbalabu.getClass022(bonecaAmbalabu8 - t * abs2 * 0.055F, amountForFloat - abs10 * 0.105F, abs2 * 0.99F, abs10 * 0.98F, ForeShinUtils.getValueForIntFloat(8014370, 0.45F));
         float bonecaAmbalabuValue11 = bonecaAmbalabu8 - t * abs2 * 0.055F;
         float bonecaAmbalabuValue12 = amountForFloat - abs10 * 0.105F;
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue11, bonecaAmbalabuValue12, abs2 * 0.92F, abs10 * 0.91F, ForeShinUtils.getValueForInt(3809810));
         bonecaAmbalabu.getClass022(
            bonecaAmbalabuValue11, bonecaAmbalabuValue12, abs2 * 0.865F, abs10 * 0.85F, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14256443), ForeShinUtils.getValueForInt(15247461), 0.55F)
         );
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue11, bonecaAmbalabuValue12 - abs10 * 0.35F, abs2 * 0.7F, abs10 * 0.4F, ForeShinUtils.getValueForIntFloat(12874284, 0.35F));
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabuValue11 - abs2 * 0.86F,
            bonecaAmbalabuValue12 + abs10 * 0.08F,
            bonecaAmbalabuValue11,
            bonecaAmbalabuValue12 + abs10 * 0.36F,
            bonecaAmbalabuValue11 + abs2 * 0.86F,
            bonecaAmbalabuValue12 + abs10 * 0.08F,
            2.8F * bonecaAmbalabuFactor,
            ForeShinUtils.getValueForInt(3809810)
         );
      }

      bonecaAmbalabu.getClass02(
         getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 9.05F),
         getAmount(bonecaAmbalabu, 9.05F),
         getBonecaAmbalabu(bonecaAmbalabu, 0.0F, 7.4F),
         getAmount(bonecaAmbalabu, 7.4F),
         2.4F * bonecaAmbalabuFactor,
         ForeShinUtils.getValueForIntFloat(9067076, 0.8F)
      );
      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 1.5F * bonecaAmbalabuFactor, 21);
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue) {
      return bonecaAmbalabu.ambalabuBonecaValue * (0.5F - bonecaAmbalabuValue / 360.0F);
   }

   private void handleFingerClaw(LatheLoft latheLoft, float fingerClawValue, ForeEar foreEar, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float fingerClawValue2 = clawFingerAmounts[0];
      float fingerScaled = fingerClawValue * 3.0F;
      float fingerClawValue3 = 4.8F;
      float fingerClawValue4 = fingerClawValue2 + -0.9F;
      float[] fingerClaw = getFingerClaw(fingerClawValue);
      float latheLoftValue = fingerClaw[0];
      float eyeValue = fingerClaw[1];
      float dinForeValue = fingerClaw[2];
      float fingerClawFactor = fingerClaw[3];
      float fingerClawFactor2 = fingerClaw[4];
      float fingerClawFactor3 = fingerClaw[5];
      float fingerClawFactor4 = fingerClaw[6];
      float fingerClawFactor5 = fingerClaw[7];
      float fingerClawFactor6 = fingerClaw[8];
      float fingerClawValue8 = fingerClaw[9];
      float fingerClawValue9 = fingerClaw[10];
      float fingerClawValue10 = fingerClaw[11];
      float fingerClawValue11 = fingerClaw[12];
      float fingerClawValue12 = fingerClaw[13];
      float fingerClawValue13 = fingerClaw[14];
      float fingerClawFactor7 = 1.2F;
      latheLoft.getLatheLoft(
         new float[]{fingerScaled - (latheLoftValue - fingerScaled) * 0.08F, fingerClawValue3 - (eyeValue - fingerClawValue3) * 0.08F, fingerClawValue4 - (dinForeValue - fingerClawValue4) * 0.08F, latheLoftValue, eyeValue, dinForeValue},
         new float[]{0.25F, 0.17F},
         6,
         shinBrr,
         false
      );
      latheLoft.getLatheLoft(latheLoftValue, eyeValue, dinForeValue, 0.18F, 4, shinBrr2);
      latheLoft.getLatheLoft(
         new float[]{latheLoftValue, eyeValue, dinForeValue, latheLoftValue + fingerClawFactor * fingerClawFactor7 * 0.5F, eyeValue + fingerClawFactor2 * fingerClawFactor7 * 0.5F, dinForeValue + fingerClawFactor3 * fingerClawFactor7 * 0.5F, fingerClawValue8, fingerClawValue9, fingerClawValue10},
         new float[]{0.2F, 0.33F, 0.26F},
         6,
         shinBrr,
         false
      );
      float cos = (float)Math.cos(Math.toRadians(30.0));
      float sin = (float)Math.sin(Math.toRadians(30.0));
      float fingerClawValue14 = fingerClawFactor * cos + fingerClawFactor4 * sin;
      float fingerClawValue15 = fingerClawFactor2 * cos + fingerClawFactor5 * sin;
      float fingerClawValue16 = fingerClawFactor3 * cos + fingerClawFactor6 * sin;
      latheLoft.getLatheLoft(
         new float[]{
            fingerClawValue8 - fingerClawFactor * 0.1F,
            fingerClawValue9 - fingerClawFactor2 * 0.1F,
            fingerClawValue10 - fingerClawFactor3 * 0.1F,
            fingerClawValue8 + fingerClawFactor * 0.5F + fingerClawFactor4 * 0.03F,
            fingerClawValue9 + fingerClawFactor2 * 0.5F + fingerClawFactor5 * 0.03F,
            fingerClawValue10 + fingerClawFactor3 * 0.5F + fingerClawFactor6 * 0.03F,
            fingerClawValue8 + fingerClawFactor * 1.1F + fingerClawFactor4 * 0.13F,
            fingerClawValue9 + fingerClawFactor2 * 1.1F + fingerClawFactor5 * 0.13F,
            fingerClawValue10 + fingerClawFactor3 * 1.1F + fingerClawFactor6 * 0.13F
         },
         new float[]{0.16F, 0.1F, 0.025F},
         4,
         shinBrr3,
         false
      );
      latheLoft.getLatheLoftForForeEar(foreEar);
      latheLoft.getLatheLoft(
         new float[]{
            fingerClawValue11,
            fingerClawValue12,
            fingerClawValue13,
            fingerClawValue11 + fingerClawValue14 * 0.5F,
            fingerClawValue12 + fingerClawValue15 * 0.5F,
            fingerClawValue13 + fingerClawValue16 * 0.5F,
            fingerClawValue11 + fingerClawValue14 * 1.1F - fingerClawFactor4 * 0.15F,
            fingerClawValue12 + fingerClawValue15 * 1.1F - fingerClawFactor5 * 0.15F,
            fingerClawValue13 + fingerClawValue16 * 1.1F - fingerClawFactor6 * 0.15F
         },
         new float[]{0.16F, 0.1F, 0.025F},
         4,
         shinBrr3,
         false
      );
   }

   private static float[] getBonecaAmbalabuForBonecaAmbalabuFloat(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float shiftx) {
      float angle = shiftx / bonecaAmbalabu.bonecaValue * 0.6F * (float) Math.PI;
      if (angle > Math.PI / 2) {
         return null;
      } else {
         float angle2 = (float) (Math.PI * 2) * (bonecaAmbalabuValue / bonecaAmbalabu.ambalabuBonecaValue - 0.5F);
         float sin = (float)Math.sin(angle);
         return new float[]{-((float)Math.sin(angle2)) * sin, -((float)Math.cos(angle2)) * sin};
      }
   }

   private static float[] getAmounts(float[] fingerClawValues) {
      int fingerClawSize = LatheLoft.getValue(fingerClawValues);
      float[] floats = new float[fingerClawSize];

      for (int i = 1; i < fingerClawSize; i++) {
         float fingerClawFactor = fingerClawValues[3 * i] - fingerClawValues[3 * i - 3];
         float fingerClawFactor2 = fingerClawValues[3 * i + 1] - fingerClawValues[3 * i - 2];
         float fingerClawFactor3 = fingerClawValues[3 * i + 2] - fingerClawValues[3 * i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(fingerClawFactor * fingerClawFactor + fingerClawFactor2 * fingerClawFactor2 + fingerClawFactor3 * fingerClawFactor3);
      }

      return floats;
   }

   private static float getShellBody(float shellBodyValue) {
      float shellBodyBound = shellBodyValue / bodyShellAmounts[bodyShellAmounts.length - 1];
      return 3.4F - 2.35F * (float)Math.pow(Math.max(0.0F, shellBodyBound), 0.85F);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuDivisor, int bonecaAmbalabuValue, float bonecaAmbalabuLimit, float bonecaAmbalabuLimit2, boolean bonecaAmbalabuFlag) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(14256443, 0.0F));

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue2 = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float absScaled = Math.abs(bonecaAmbalabuValue2 - 0.5F) * 2.0F;
         if (!(absScaled < bonecaAmbalabuLimit)) {
            bonecaAmbalabu.getClassBonecaAmbalabu(i, bonecaAmbalabu.bonecaValue * 0.55F, i + 1, bonecaAmbalabu.bonecaValue, getValue(absScaled, bonecaAmbalabuLimit2));
         }
      }

      for (int j = 0; j < 2; j++) {
         for (int k = 0; k < bonecaAmbalabuDivisor; k++) {
            float bonecaAmbalabuFactor = (k + 0.5F * j + getAmount(k, 1 + j, bonecaAmbalabuValue) * 0.3F) / bonecaAmbalabuDivisor;
            float bonecaAmbalabuValue3 = bonecaAmbalabuFactor * bonecaAmbalabu.ambalabuBonecaValue;
            float fingerClawValue = Math.abs(bonecaAmbalabuFactor - 0.5F) * 2.0F;
            if (!(fingerClawValue < bonecaAmbalabuLimit)) {
               float bonecaAmbalabuFactor2 = bonecaAmbalabuFlag ? getAmount2((fingerClawValue - bonecaAmbalabuLimit) / 0.14F) * (1.0F - 0.2F * getAmount2((fingerClawValue - 0.8F) / 0.2F)) : 1.0F;
               int foreShinValue = getValue(fingerClawValue, bonecaAmbalabuLimit2);
               if (j == 0) {
                  foreShinValue = ForeShinUtils.getValue(foreShinValue, 0.93F);
               }

               boolean bonecaAmbalabuFlag2 = bonecaAmbalabuFlag ? k % 6 == 1 && fingerClawValue < bonecaAmbalabuLimit2 : k % 7 == 3 && fingerClawValue < 0.6F;
               if (bonecaAmbalabuFlag2) {
                  foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(10900252), 0.5F);
               }

               float bonecaAmbalabuFactor3 = getAmount(k, 2 + j, bonecaAmbalabuValue);
               float bonecaAmbalabuValue5 = (j == 0 ? 1.0F : 0.8F) * bonecaAmbalabuFactor2 * (bonecaAmbalabuFlag ? 0.5F + 0.5F * bonecaAmbalabuFactor3 : 0.3F + 0.7F * bonecaAmbalabuFactor3 * bonecaAmbalabuFactor3);
               float bonecaAmbalabuValue6 = bonecaAmbalabu.bonecaValue * (0.6F - 0.58F * bonecaAmbalabuValue5);
               float bonecaAmbalabuValue7 = (float)bonecaAmbalabu.ambalabuBonecaValue / bonecaAmbalabuDivisor * (0.5F + 0.3F * getAmount(k, 3 + j, bonecaAmbalabuValue));
               float bonecaAmbalabuValue8 = (getAmount(k, 4 + j, bonecaAmbalabuValue) - 0.5F) * bonecaAmbalabu.ambalabuBonecaValue / bonecaAmbalabuDivisor * 2.5F;
               float bonecaAmbalabuValue9 = bonecaAmbalabu.bonecaValue * 0.62F - (bonecaAmbalabu.bonecaValue * 0.62F - bonecaAmbalabuValue6) * 0.5F;
               bonecaAmbalabu.getClassForIntFloatArray(
                  foreShinValue,
                  bonecaAmbalabuValue3 - bonecaAmbalabuValue7,
                  bonecaAmbalabu.bonecaValue * 0.62F,
                  bonecaAmbalabuValue3 + bonecaAmbalabuValue7,
                  bonecaAmbalabu.bonecaValue * 0.62F,
                  bonecaAmbalabuValue3 + bonecaAmbalabuValue7 * 0.45F + bonecaAmbalabuValue8 * 0.5F,
                  bonecaAmbalabuValue9,
                  bonecaAmbalabuValue3 + bonecaAmbalabuValue8,
                  bonecaAmbalabuValue6,
                  bonecaAmbalabuValue3 - bonecaAmbalabuValue7 * 0.45F + bonecaAmbalabuValue8 * 0.5F,
                  bonecaAmbalabuValue9
               );
               int classValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(fingerClawValue < bonecaAmbalabuLimit2 ? 15247461 : 16774888), 0.6F);
               bonecaAmbalabu.getClassForIntFloatArray(classValue, bonecaAmbalabuValue3 - bonecaAmbalabuValue7 * 0.3F + bonecaAmbalabuValue8 * 0.5F, bonecaAmbalabuValue9, bonecaAmbalabuValue3 + bonecaAmbalabuValue7 * 0.3F + bonecaAmbalabuValue8 * 0.5F, bonecaAmbalabuValue9, bonecaAmbalabuValue3 + bonecaAmbalabuValue8, bonecaAmbalabuValue6);
            }
         }
      }

      for (int m = 0; m < 900; m++) {
         float classBound = getAmount(m, 1, bonecaAmbalabuValue + 7) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = bonecaAmbalabu.bonecaValue * (0.15F + 0.85F * getAmount(m, 2, bonecaAmbalabuValue + 7));
         if (ForeShinUtils.getValue6(bonecaAmbalabu.getBonecaAmbalabuForInt((int)classBound, (int)amountForFloat)) >= 128) {
            float bonecaAmbalabuValue13 = bonecaAmbalabu.bonecaValue * 0.18F * (0.5F + getAmount(m, 3, bonecaAmbalabuValue + 7));
            bonecaAmbalabu.getClass02(
               classBound,
               amountForFloat,
               classBound + (getAmount(m, 4, bonecaAmbalabuValue + 7) - 0.5F) * 3.0F,
               amountForFloat - bonecaAmbalabuValue13,
               1.4F,
               ForeShinUtils.getValueForIntFloat(getAmount(m, 5, bonecaAmbalabuValue + 7) < 0.5F ? 0 : 16773596, 0.16F)
            );
         }
      }

      for (int p = 0; p < bonecaAmbalabu.bonecaValue; p++) {
         for (int q = 0; q < bonecaAmbalabu.ambalabuBonecaValue; q++) {
            int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(q, p);
            bonecaAmbalabu.getBonecaAmbalabu(q, p, ForeShinUtils.getValue6(bonecaAmbalabuForInt) < 140 ? 0 : bonecaAmbalabuForInt | 0xFF000000);
         }
      }
   }

   private static float getAmount3(float fingerClawLimit) {
      return fingerClawLimit < 0.0F ? 0.0F : (fingerClawLimit > 1.0F ? 1.0F : fingerClawLimit);
   }

   private static float getAmount(BonecaAmbalabu bonecaAmbalabu, float fingerClawValue) {
      return bonecaAmbalabu.bonecaValue * getAmounts(fingerClawValue);
   }

   private static float getAmount3(float fingerClawValue, float fingerClawValue2) {
      float amountsForFloat = getAmountsForFloat(fingerClawValue2);
      float fingerClawFactor = amountsForFloat * 1.16F;
      float fingerClawFactor2 = amountsForFloat * 0.84F;
      float fingerClawValue4 = getAmount(fingerClawValue, fingerClawValue2) - -8.6F;
      float fingerClawValue5 = fingerClawValue / (fingerClawFactor * fingerClawFactor);
      float fingerClawValue6 = fingerClawValue4 / (fingerClawFactor2 * fingerClawFactor2);
      return (float)Math.toDegrees(Math.atan2(-fingerClawValue5, -fingerClawValue6));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuLimit, float bonecaAmbalabuFactor, float bonecaAmbalabuValue, int foreShinValue, float bonecaAmbalabuValue3, float fingerClawLimit, float bonecaAmbalabuValue4, int bonecaAmbalabuValue5) {
      float bonecaAmbalabuValue6 = fingerClawLimit > 0.0F ? getAmounts(fingerClawLimit) : 2.0F;

      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float classBound = getAmount(i, 1, bonecaAmbalabuValue5) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(i, 2, bonecaAmbalabuValue5) * bonecaAmbalabu.bonecaValue;
         float planValuesItem = classBound / bonecaAmbalabu.ambalabuBonecaValue - 0.5F;
         float bonecaAmbalabuLimit3 = amountForFloat / bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuFactor2 = bonecaAmbalabuLimit3 > bonecaAmbalabuValue6 ? bonecaAmbalabuValue4 : bonecaAmbalabuValue3;
         if (!(bonecaAmbalabuFactor2 <= 0.0F)) {
            float min10 = Math.min(1.0F, Math.abs(planValuesItem) * 2.0F);
            float bonecaAmbalabuValue11 = planValuesItem < 0.0F ? -90.0F - 180.0F * min10 : -90.0F + 180.0F * min10;
            float bonecaAmbalabuFactor3 = getAmount2((bonecaAmbalabuLimit3 - 0.45F) / 0.25F) * (1.0F - getAmount2((Math.abs(planValuesItem) - 0.22F) / 0.1F));
            float bonecaAmbalabuValue12 = planValuesItem < 0.0F ? 160.0F : 20.0F;
            float degrees = bonecaAmbalabuValue11 + (bonecaAmbalabuValue12 - bonecaAmbalabuValue11) * bonecaAmbalabuFactor3 + (getAmount(i, 3, bonecaAmbalabuValue5) - 0.5F) * 50.0F;
            float angle = (float)Math.toRadians(degrees);
            float bonecaAmbalabuFactor4 = bonecaAmbalabuFactor * (0.6F + 0.8F * getAmount(i, 4, bonecaAmbalabuValue5));
            int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt((int)classBound, (int)amountForFloat);
            float bonecaAmbalabuLimit4 = (ForeShinUtils.getValueInt(bonecaAmbalabuForInt) + ForeShinUtils.getValue4(bonecaAmbalabuForInt) + ForeShinUtils.getValue5(bonecaAmbalabuForInt)) / 765.0F;
            float bonecaAmbalabuFactor5 = foreShinValue == 0 && bonecaAmbalabuLimit4 > 0.75F ? 0.3F : 1.0F;
            bonecaAmbalabu.getClass02(
               classBound,
               amountForFloat,
               classBound + (float)Math.cos(angle) * bonecaAmbalabuFactor4,
               amountForFloat + (float)Math.sin(angle) * bonecaAmbalabuFactor4,
               bonecaAmbalabuValue,
               ForeShinUtils.getValueForIntFloat(foreShinValue, bonecaAmbalabuFactor5 * bonecaAmbalabuFactor2 * (0.5F + 0.5F * getAmount(i, 5, bonecaAmbalabuValue5)))
            );
         }
      }
   }

   private void handle(LatheLoft latheLoft, int fingerClawFactor, float shinFootFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float fingerClawValue = Math.max(0.0F, shinFootFactor * fingerClawFactor / 6.0F - 0.6F);
      float fingerClawValue2 = shinFootFactor * (fingerClawFactor + 1) / 6.0F + 0.05F;
      float[] floats = new float[]{0.0F, 0.8F, 1.0F};
      float[] floats2 = new float[]{0.9F, 1.05F, 1.0F};
      float[] loftPathValues = new float[9];
      float[] crownForeValues = new float[3];
      float[] floats5 = new float[3];

      for (int i = 0; i < 3; i++) {
         float shellBound = fingerClawValue + (fingerClawValue2 - fingerClawValue) * floats[i];
         float[] amountsShell = getAmountsShell(shellBound);
         loftPathValues[3 * i] = amountsShell[0];
         loftPathValues[3 * i + 1] = amountsShell[1];
         loftPathValues[3 * i + 2] = amountsShell[2];
         float fingerClawFactor3 = getShellBody(shellBound) * floats2[i];
         floats5[i] = fingerClawFactor3;
         crownForeValues[i] = fingerClawFactor3 * (fingerClawFactor == 0 && i == 0 ? 0.98F : 0.84F);
      }

      latheLoft.getLoftPath(loftPathValues, crownForeValues, floats5, 16, shinBrr, null, shinBrr3, 0.0F, 1.0F, 0.0F);
      float fingerClawValue4 = 0.5F * (fingerClawValue + fingerClawValue2) + 0.25F;
      float[] amountsShell2 = getAmountsShell(fingerClawValue4);
      float shellBodyScaled = getShellBody(fingerClawValue4) * 1.02F;
      float fingerClawValue5 = 0.45F + 0.22F * shellBodyScaled;
      float degrees = (float)Math.toDegrees(Math.atan2(-amountsShell2[4], amountsShell2[5]));
      float[] polygonExtrudeValues = new float[]{0.9F, 0.3F, -0.9F, 0.3F, -0.62F, -fingerClawValue5, 0.7F, -fingerClawValue5 * 0.92F};

      for (int j = -1; j <= 1; j += 2) {
         latheLoft.getDequeArrayArrayDeque()
            .getLatheLoft(j * shellBodyScaled * 0.71F, amountsShell2[1] - amountsShell2[5] * shellBodyScaled * 0.45F, amountsShell2[2] + amountsShell2[4] * shellBodyScaled * 0.45F)
            .getLatheLoftLatheLoftAmountLoft(degrees)
            .getLatheLoft(j * 5.0F)
            .getLatheLoft4(90.0F);
         latheLoft.getPolygonExtrude(polygonExtrudeValues, -0.05F, 0.05F, shinBrr2, shinBrr2, null);
         latheLoft.getDequeArray();
      }
   }

   @Override
   public String formatNoteBlock() {
      return "entity.cat.ambient";
   }

   private static void handleBonecaAmbalabu8(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15039324), ForeShinUtils.getValueForInt(15966596), getAmount2((bonecaAmbalabuValue - 0.2F) / 0.8F));
         float bonecaAmbalabuLimit = Math.max(1.0F - Math.abs(bonecaAmbalabuValue - 0.33F) / 0.04F, 1.0F - Math.abs(bonecaAmbalabuValue - 0.74F) / 0.03F);
         if (bonecaAmbalabuLimit > 0.0F) {
            foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(12141610), bonecaAmbalabuLimit);
         }

         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, foreShinValue);
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.38F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue * 0.62F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(16767432, 0.18F));
   }

   private static void handleBonecaAmbalabu9(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13204018)).getBonecaAmbalabu(0.06F, 3.0F, 61);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.78F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(10377762, 0.6F));
      bonecaAmbalabu.getClassForIntFloatArray(
         ForeShinUtils.getValueForIntFloat(15247461, 0.9F),
         bonecaAmbalabu.ambalabuBonecaValue * 0.28F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.72F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.515F,
         bonecaAmbalabu.bonecaValue * 0.92F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.485F,
         bonecaAmbalabu.bonecaValue * 0.92F
      );
      bonecaAmbalabu.getClassForIntFloatArray(
         ForeShinUtils.getValueForInt(15246240),
         bonecaAmbalabu.ambalabuBonecaValue * 0.32F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.68F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.51F,
         bonecaAmbalabu.bonecaValue * 0.86F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.49F,
         bonecaAmbalabu.bonecaValue * 0.86F
      );
      bonecaAmbalabu.getClassForIntFloatArray(
         ForeShinUtils.getValueForIntFloat(14256262, 0.7F),
         bonecaAmbalabu.ambalabuBonecaValue * 0.42F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.58F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * 0.6F
      );

      for (int i = -2; i <= 2; i++) {
         if (i != 0) {
            float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + i * 0.03F);
            bonecaAmbalabu.getClassForFloatInt(
               bonecaAmbalabuValue,
               bonecaAmbalabu.bonecaValue * 0.05F,
               bonecaAmbalabuValue + i * bonecaAmbalabu.ambalabuBonecaValue / 42.0F,
               bonecaAmbalabu.bonecaValue * 0.3F,
               bonecaAmbalabuValue + i * bonecaAmbalabu.ambalabuBonecaValue / 18.0F,
               bonecaAmbalabu.bonecaValue * 0.48F,
               bonecaAmbalabu.ambalabuBonecaValue / 48.0F,
               ForeShinUtils.getValueForIntFloat(16049874, 0.9F)
            );
         }
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue * 0.1F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(10900252, 0.35F));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.9F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(10900252, 0.35F));

      for (int j = -1; j <= 1; j += 2) {
         float bonecaAmbalabuBound = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + j * 0.29F);
         bonecaAmbalabu.getClassBonecaAmbalabu(
            Math.min(bonecaAmbalabuBound, bonecaAmbalabuBound + j * bonecaAmbalabu.ambalabuBonecaValue * 0.08F),
            0.0F,
            Math.max(bonecaAmbalabuBound, bonecaAmbalabuBound + j * bonecaAmbalabu.ambalabuBonecaValue * 0.08F),
            bonecaAmbalabu.bonecaValue * 0.85F,
            ForeShinUtils.getValueForIntFloat(15247461, 0.8F)
         );

         for (int k = 0; k < 2; k++) {
            float classBound = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + j * (0.36F + k * 0.08F));
            bonecaAmbalabu.getClass02(
               classBound,
               bonecaAmbalabu.bonecaValue * 0.02F,
               classBound + j * bonecaAmbalabu.ambalabuBonecaValue * 0.015F,
               bonecaAmbalabu.bonecaValue * (0.26F - k * 0.06F),
               bonecaAmbalabu.ambalabuBonecaValue / 26.0F,
               ForeShinUtils.getValueForIntFloat(10900252, 0.8F)
            );
         }
      }

      for (int m = 0; m < 140; m++) {
         float bonecaAmbalabuValue3 = getAmount(m, 1, 67) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(m, 2, 67) * bonecaAmbalabu.bonecaValue * 0.8F;
         if (!(Math.abs(bonecaAmbalabuValue3 / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) < 0.25F)) {
            bonecaAmbalabu.getClass02(
               bonecaAmbalabuValue3,
               amountForFloat,
               bonecaAmbalabuValue3 + (getAmount(m, 3, 67) - 0.5F) * bonecaAmbalabu.ambalabuBonecaValue * 0.03F,
               amountForFloat + bonecaAmbalabu.bonecaValue * 0.08F,
               bonecaAmbalabu.ambalabuBonecaValue / 70.0F,
               ForeShinUtils.getValueForIntFloat(getAmount(m, 4, 67) < 0.5F ? 8011284 : 15906426, 0.35F)
            );
         }
      }
   }

   private static float getAmountsForFloat(float fingerClawValue) {
      if (fingerClawValue <= shinFootTable[0]) {
         return fingerClawTable[0];
      } else {
         for (int i = 0; i + 1 < shinFootTable.length; i++) {
            if (fingerClawValue <= shinFootTable[i + 1]) {
               float fingerClawFactor = (fingerClawValue - shinFootTable[i]) / (shinFootTable[i + 1] - shinFootTable[i]);
               return fingerClawTable[i] + (fingerClawTable[i + 1] - fingerClawTable[i]) * fingerClawFactor;
            }
         }

         return fingerClawTable[fingerClawTable.length - 1];
      }
   }
}
