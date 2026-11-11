package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class LidBombardiro extends WarmBrainrotBase {
   private static final float radians = (float)Math.toRadians(38.0);
   private ForeEar classShinFoot;
   private ForeEar classShinProgress;
   private ForeEar eyeL;
   private static final float[][] lidBombardiroFloats = new float[][]{
      {-2.0F, 18.2F, 14.3F, 11.3F, 3.45F},
      {-4.6F, 19.5F, 13.95F, 10.0F, 5.4F},
      {-6.8F, 19.0F, 13.45F, 10.7F, 5.2F},
      {-8.8F, 17.3F, 13.35F, 11.45F, 4.4F},
      {-10.8F, 16.1F, 13.28F, 11.72F, 3.3F},
      {-12.8F, 15.25F, 13.05F, 11.92F, 2.75F},
      {-16.8F, 14.8F, 13.25F, 12.08F, 2.35F},
      {-18.4F, 15.2F, 13.12F, 12.08F, 2.5F},
      {-19.6F, 14.62F, 13.18F, 12.18F, 2.15F},
      {-20.3F, 14.1F, 13.25F, 12.47F, 1.3F},
      {-20.6F, 13.72F, 13.3F, 12.86F, 0.55F}
   };
   private ForeEar shinFootForeEar;
   private static final LidBombardiro.Spine CLASS = LidBombardiro.Spine.getClassForFloatArrayFlag(lidBombardiroFloats, true);
   private ForeEar lidR;
   private ForeEar classLidEye;
   private ForeEar shinProgressForeEar;
   private static final LidBombardiro.Spine LOWER_SPINE = LidBombardiro.Spine.getClassForFloatArrayFlag(lidBombardiroFloats, false);
   private static final LidBombardiro.Spine UPPER_SPINE = new LidBombardiro.Spine(
      2,
      new float[]{0.0F, 14.4F, -6.0F, 0.0F, 15.0F, -2.5F, 0.0F, 15.15F, 4.0F, 0.0F, 15.85F, 13.5F, 0.0F, 16.7F, 20.0F, 0.0F, 17.0F, 24.0F},
      new float[]{3.0F, 3.88F, 3.75F, 2.7F, 1.45F, 0.45F},
      new float[]{2.9F, 3.98F, 3.85F, 2.85F, 1.55F, 0.5F}
   );
   private static final float[] bonecaTable = new float[]{0.05F, 0.85F, 1.45F, 1.78F, 1.78F, 1.45F, 0.8F, 0.45F};
   private ForeEar lidBombardiroForeEar;
   private ForeEar sling;
   private ForeEar lidL;
   private ForeEar foreEar;
   private ForeEar bomb;
   private static final float[] shinFootTable = new float[]{-3.95F, -3.6F, -2.8F, -1.4F, 0.8F, 2.3F, 3.4F, 4.2F};

   private static void handle(LatheLoft latheLoft, float lidBombardiroValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(lidBombardiroValue * 10.8F, 13.9F, 0.0F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLatheLoft(new float[]{1.15F, 1.6F, 1.75F, 1.62F, 1.0F, 0.08F}, new float[]{-3.1F, -2.7F, -1.3F, 2.3F, 6.1F, 9.3F}, 12, shinBrr, shinBrr2, null);
      latheLoft.getDequeArray();
   }

   @Override
   public String formatTungDinString() {
      return "Bombardiro Crocodilo";
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue) {
      return ((bonecaAmbalabuValue + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 360.0F;
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.bomb = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 15.0F, 0.0F);
      this.eyeL = latheLoft.getClassForStringForeEar("head", this.bomb, 0.0F, 15.5F, -3.0F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("jaw", this.eyeL, 0.0F, 13.5F, -4.0F);
      float shinFootValue = CLASS.getAmount(1.95F, -7.0F) + 1.0F;
      this.lidR = latheLoft.getClassForStringForeEar("eye_r", this.eyeL, 2.1F, shinFootValue, -7.6F);
      this.lidL = latheLoft.getClassForStringForeEar("eye_l", this.eyeL, -2.1F, shinFootValue, -7.6F);
      this.classLidEye = latheLoft.getClassForStringForeEar("lid_r", this.lidR, 2.1F, shinFootValue, -7.6F);
      this.lidBombardiroForeEar = latheLoft.getClassForStringForeEar("lid_l", this.lidL, -2.1F, shinFootValue, -7.6F);
      this.foreEar = latheLoft.getClassForStringForeEar("prop_r", this.bomb, 10.8F, 13.9F, -3.45F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("prop_l", this.bomb, -10.8F, 13.9F, -3.45F);
      this.sling = latheLoft.getClassForStringForeEar("bomb", this.bomb, 0.0F, 17.4F, 0.0F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("sling", this.sling, 0.0F, 11.2F, 0.0F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("tail", this.bomb, 0.0F, 17.6F, 16.4F);
      latheLoft.getLatheLoft(this.sling);
      LoftLatheFlag.Region region = latheLoft.getMax(384, 240);
      handleBonecaAmbalabuCLASS(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(256, 144);
      handleBonecaAmbalabuLOWER_SPINE(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(48, 32);
      handleBonecaAmbalabu5(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(384, 288);
      handleBonecaAmbalabuUPPER_SPINE(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(160, 128);
      handleBonecaAmbalabu(max5.getClassBonecaAmbalabu(), false, true);
      LoftLatheFlag.Region max6 = latheLoft.getMax(160, 128);
      handleBonecaAmbalabu(max6.getClassBonecaAmbalabu(), true, true);
      LoftLatheFlag.Region max7 = latheLoft.getMax(64, 48);
      handleBonecaAmbalabu(max7.getClassBonecaAmbalabu(), false, false);
      LoftLatheFlag.Region max8 = latheLoft.getMax(64, 48);
      handleBonecaAmbalabu(max8.getClassBonecaAmbalabu(), true, false);
      LoftLatheFlag.Region max9 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu9(max9.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max10 = latheLoft.getMax(64, 96);
      handleAmountsBoneca(max10.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max11 = latheLoft.getMax(96, 48);
      handleBonecaAmbalabuForBonecaAmbalabu(max11.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max12 = latheLoft.getMax(64, 32);
      handleBonecaAmbalabu6(max12.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max13 = latheLoft.getMax(64, 32);
      handleBonecaAmbalabu10(max13.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max14 = latheLoft.getMax(96, 48);
      handleBonecaAmbalabu(max14.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max15 = latheLoft.getMax(64, 32);
      handleBonecaAmbalabu3(max15.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max16 = latheLoft.getMax(96, 64);
      handleBonecaAmbalabu11(max16.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max17 = latheLoft.getMax(16, 48);
      max17.getClassBonecaAmbalabu()
         .getBonecaAmbalabuBonecaAmbalabuActive(ForeShinUtils.getValueForInt(1973790), ForeShinUtils.getValueForInt(3815994))
         .getClassBonecaAmbalabu(0.0F, 38.4F, 16.0F, 48.0F, ForeShinUtils.getValueForInt(14267450))
         .getBonecaAmbalabu(0.05F, 3.0F, 61);
      ShinBrr shinBrr = latheLoft.getMax(5597500);
      ShinBrr shinBrr2 = latheLoft.getMax(4085288);
      ShinBrr shinBrr3 = latheLoft.getMax(5726271);
      ShinBrr shinBrr4 = latheLoft.getMax(15722704);
      ShinBrr shinBrr5 = latheLoft.getMax(3816755);
      ShinBrr shinBrr6 = latheLoft.getMax(4606783);
      latheLoft.getLatheLoftForForeEar(this.bomb);
      latheLoft.getLoftPath(UPPER_SPINE.pointsSpline, UPPER_SPINE.shinFootTable, UPPER_SPINE.sampledRadiiY, 14, latheLoft.getClassForRegion(max4), null, latheLoft.getMax(11125967), 0.0F, 1.0F, 0.0F);
      handleForLatheLoftFloat(latheLoft, 1.0F, latheLoft.getClassForRegion(max5));
      handleForLatheLoftFloat(latheLoft, -1.0F, latheLoft.getClassForRegion(max6));
      handle(latheLoft, 1.0F, latheLoft.getClassForRegion(max7));
      handle(latheLoft, -1.0F, latheLoft.getClassForRegion(max8));
      handle(latheLoft, 1.0F, latheLoft.getClassForRegion(max16), latheLoft.getMax(2303263));
      handle(latheLoft, -1.0F, latheLoft.getClassForRegion(max16), latheLoft.getMax(2303263));
      float shinFootValue2 = UPPER_SPINE.getAmount(0.0F, 6.5F) - 0.55F;
      latheLoft.getLoftLathe(0.0F, shinFootValue2, 6.5F, 1.7F, 1.35F, 2.0F, 10, 0.0F, 0.5F, latheLoft.getClassForRegion(max15));
      ShinBrr shinBrr7 = latheLoft.getMax(2303263);

      for (int i = -1; i <= 1; i += 2) {
         latheLoft.getLatheLoft(new float[]{i * 0.35F, shinFootValue2 + 0.6F, 6.5F, i * 0.35F, shinFootValue2 + 0.9F, 9.5F}, new float[]{0.13F, 0.11F}, 5, shinBrr7, false);
         latheLoft.getLatheLoft(new float[]{i * 0.25F, 17.0F, 23.6F, i * 0.25F, 17.0F, 26.0F}, new float[]{0.12F, 0.1F}, 5, shinBrr7, false);
         float[] shinFootValues = UPPER_SPINE.getAmountsForFloat(UPPER_SPINE.getAmounts(11.4F));
         latheLoft.getLatheLoft(
            new float[]{i * (shinFootValues[3] - 0.3F), shinFootValues[1] + 0.35F, 11.4F, i * (shinFootValues[3] + 1.0F), shinFootValues[1] + 0.2F, 11.8F},
            new float[]{0.12F, 0.1F},
            5,
            shinBrr7,
            false
         );
      }

      latheLoft.getLatheLoft(new float[]{0.55F, 0.42F}, new float[]{10.9F, 11.6F}, 6, shinBrr5, shinBrr5, null);

      for (int j = 0; j < 10; j++) {
         float shinFootValue3 = -2.5F + j * 1.2F;
         float foreShinFactor = j / 9.0F;
         ShinBrr shinBrr8 = latheLoft.getMax(ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(4085288), ForeShinUtils.getValueForInt(5726271), foreShinFactor));

         for (int k = -1; k <= 1; k += 2) {
            handle(latheLoft, UPPER_SPINE, k * 1.1F, shinFootValue3, 1.15F - 0.4F * foreShinFactor, shinBrr8);
         }
      }

      for (int m = 0; m < 5; m++) {
         float spineLimit = 13.1F + m * 0.92F;
         float shinFootValue6 = 0.5F + m * 0.15F;
         float[] amountsForFloat = UPPER_SPINE.getAmountsForFloat(UPPER_SPINE.getAmounts(spineLimit));
         handle(latheLoft, spineLimit, amountsForFloat[1] + amountsForFloat[4] - 0.2F, 1.0F, shinFootValue6, shinBrr3);
      }

      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      latheLoft.getLoftPath(
         new float[]{0.0F, 16.6F, 19.0F, 0.0F, 19.6F, 20.4F, 0.0F, 22.4F, 21.65F, 0.0F, 24.2F, 22.45F, 0.0F, 25.0F, 22.9F, 0.0F, 25.3F, 23.15F},
         new float[]{0.6F, 0.5F, 0.4F, 0.32F, 0.22F, 0.08F},
         new float[]{3.0F, 2.5F, 1.95F, 1.5F, 0.95F, 0.3F},
         6,
         latheLoft.getClassForRegion(max9),
         null,
         null,
         0.0F,
         0.0F,
         -1.0F
      );
      latheLoft.getLatheLoftForForeEar(this.eyeL);
      latheLoft.getLoftPath(CLASS.pointsSpline, CLASS.shinFootTable, CLASS.sampledRadiiY, 18, latheLoft.getClassForRegion(region), null, shinBrr, 0.0F, 1.0F, 0.0F);
      this.handleLidEye(latheLoft, latheLoft.getClassForRegion(max11), latheLoft.getClassForRegion(max14), latheLoft.getClassForRegion(max12), latheLoft.getClassForRegion(max13));

      for (int p = -1; p <= 1; p += 2) {
         for (float shinFootLimit : new float[]{-4.2F, -5.4F}) {
            float latheLoftValue = p * 0.9F;
            latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, CLASS.getAmount(latheLoftValue, shinFootLimit) - 0.15F, shinFootLimit).getLatheLoft4(45.0F);
            latheLoft.getLatheLoft(new float[]{0.8F, 0.02F}, new float[]{0.0F, 0.75F}, 4, shinFootLimit > -5.0F ? shinBrr2 : latheLoft.getMax(4347183), null, null);
            latheLoft.getDequeArray();
         }
      }

      float shinFootValue8 = -19.0F;
      float eyeValue = CLASS.getAmount(0.0F, shinFootValue8) + 0.15F;
      latheLoft.getLatheLoft(0.0F, eyeValue, shinFootValue8, 1.15F, 0.6F, 1.05F, 8, latheLoft.getMax(4873270));
      ShinBrr shinBrr9 = latheLoft.getMax(1712402);

      for (int q = -1; q <= 1; q += 2) {
         float shinFootValue10 = q * 0.42F;
         float shinFootValue11 = -19.3F;
         float shinFootFactor = shinFootValue10 / 1.15F;
         float shinFootFactor2 = (shinFootValue11 - shinFootValue8) / 1.05F;
         float armTrunkValue = eyeValue + 0.6F * (float)Math.sqrt(Math.max(0.0F, 1.0F - shinFootFactor * shinFootFactor - shinFootFactor2 * shinFootFactor2)) + 0.03F;
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinFootValue10, armTrunkValue, shinFootValue11).getLatheLoft4(q * 35.0F).getLatheLoftLatheLoftAmountLoft(80.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.16F, 0.4F, 8, shinBrr9);
         latheLoft.getDequeArray();
      }

      float[] floats = new float[]{-8.0F, -9.6F, -11.2F, -12.7F, -14.2F, -15.7F, -17.2F, -18.7F, -19.85F};
      float[] floats2 = new float[]{0.8F, 0.95F, 1.0F, 1.35F, 1.0F, 0.95F, 1.15F, 0.85F, 0.7F};

      for (int r = -1; r <= 1; r += 2) {
         for (int t = 0; t < floats.length; t++) {
            float[] amounts3 = CLASS.getAmounts(CLASS.getAmounts(floats[t]), r * 124.0F);
            latheLoft.getDequeArrayArrayDeque().getLatheLoft(amounts3[0], amounts3[1] + 0.12F, amounts3[2]).getLatheLoft(180.0F + r * 15.0F);
            handle(latheLoft, 0.28F, floats2[t] + 0.12F, shinBrr4);
            latheLoft.getDequeArray();
         }
      }

      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      latheLoft.getLoftPath(LOWER_SPINE.pointsSpline, LOWER_SPINE.shinFootTable, LOWER_SPINE.sampledRadiiY, 12, latheLoft.getClassForRegion(max2), null, latheLoft.getMax(8160840), 0.0F, 1.0F, 0.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, LOWER_SPINE.getAmount(0.0F, -11.8F) - 0.02F, -11.8F).getLatheLoftLatheLoftAmountLoft(-4.5F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.35F, 0.4F, 4.8F, 8, latheLoft.getClassForRegion(max3));
      latheLoft.getDequeArray();
      latheLoft.getLatheLoft(0.0F, LOWER_SPINE.getAmount(0.0F, -8.6F) - 0.3F, -8.6F, 2.1F, 0.75F, 1.3F, 8, latheLoft.getMax(5907504));
      float[] floats3 = new float[]{-8.8F, -10.4F, -11.95F, -13.45F, -14.95F, -16.45F, -17.95F, -19.3F};
      float[] floats4 = new float[]{0.75F, 0.85F, 0.8F, 0.85F, 0.85F, 1.5F, 0.85F, 0.75F};

      for (int shinFootTotal = -1; shinFootTotal <= 1; shinFootTotal += 2) {
         for (int index = 0; index < floats3.length; index++) {
            float[] amounts4 = LOWER_SPINE.getAmounts(LOWER_SPINE.getAmounts(floats3[index]), shinFootTotal * 62.0F);
            latheLoft.getDequeArrayArrayDeque().getLatheLoft(amounts4[0], amounts4[1] - 0.12F, amounts4[2]).getLatheLoft(-shinFootTotal * (index == 5 ? 32.0F : 24.0F));
            handle(latheLoft, 0.26F, floats4[index] + 0.12F, shinBrr4);
            latheLoft.getDequeArray();
         }
      }

      latheLoft.getLatheLoftForForeEar(this.foreEar);
      handle(latheLoft, 1.0F, latheLoft.getClassForRegion(max17), latheLoft.getMax(3158827), latheLoft.getMax(1974299));
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handle(latheLoft, -1.0F, latheLoft.getClassForRegion(max17), latheLoft.getMax(3158827), latheLoft.getMax(1974299));
      ShinBrr shinBrr10 = latheLoft.getMax(12562050);
      latheLoft.getLatheLoftForForeEar(this.sling);
      latheLoft.getLatheLoft(new float[]{0.0F, 18.6F, 0.0F, 0.0F, 11.0F, 0.0F}, new float[]{0.15F, 0.15F}, 5, shinBrr10, false);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 7.6F, 0.0F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLatheLoft(bonecaTable, shinFootTable, 10, latheLoft.getClassForRegion(max10), null, latheLoft.getMax(3027498));
      latheLoft.getDequeArray();

      for (int shinFootCounter = 0; shinFootCounter < 2; shinFootCounter++) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 7.6F, 0.0F).getLatheLoft(45.0F + 90.0F * shinFootCounter);
         latheLoft.getLatheLoft(-0.07F, -1.62F, 2.9F, 0.07F, 1.62F, 4.55F, shinBrr6);
         latheLoft.getDequeArray();
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 7.6F, 0.0F).getLatheLoftLatheLoftAmountLoft(90.0F).getLatheLoft4(45.0F);
      latheLoft.getLatheLoft(new float[]{1.584F, 1.584F}, new float[]{3.25F, 4.55F}, 4, shinBrr6, null, null);
      latheLoft.getDequeArray();
      float shinFootValue13 = 9.3F;
      latheLoft.getLatheLoft(
         LatheLoft.getPointsSpline(1, 0.0F, 11.599999F, 0.0F, 0.07F, 10.5F, 0.02F, -0.05F, shinFootValue13 + 0.6F, -0.03F, 0.0F, shinFootValue13 + 0.15F, 0.0F),
         LatheLoft.getAmounts(4, 0.15F, 0.14F, 0.13F, 0.12F),
         5,
         shinBrr10,
         false
      );
      latheLoft.getLatheLoft(0.0F, shinFootValue13 + 0.12F, 0.0F, 0.36F, 0.22F, 0.36F, 6, latheLoft.getMax(10259556));
      latheLoft.getLatheLoft(
         new float[]{0.1F, shinFootValue13 + 0.2F, -0.1F, 0.42F, shinFootValue13 + 0.05F, -0.35F, 0.62F, shinFootValue13 - 0.25F, -0.4F}, new float[]{0.07F, 0.05F, 0.02F}, 3, shinBrr10, false
      );
      latheLoft.getLatheLoft(
         new float[]{-0.1F, shinFootValue13 + 0.2F, 0.1F, -0.35F, shinFootValue13 + 0.12F, 0.4F, -0.45F, shinFootValue13 - 0.18F, 0.62F}, new float[]{0.07F, 0.05F, 0.02F}, 3, shinBrr10, false
      );
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      boolean sneakingPose = poseProgress.sneakingPose;
      float shinProgressValue = poseProgress.limbSwingStrength;
      float shinProgressValue2 = 0.6F * poseProgress.getAgeCode(64.0F, 0.0F);
      float shinProgressFactor = (0.05F * poseProgress.getAgeCode(110.0F, 1.3F) + 0.16F * poseProgress.getAnimationProgressForFloat(0.0F)) * (sneakingPose ? 0.3F : 1.0F);
      float foreShinValue = 0.035F * poseProgress.getAgeCode(83.0F, 2.1F) - 0.09F * shinProgressValue;
      foreShinScale.getAmountsForeShin2(this.bomb, 0.0F, shinProgressValue2, 0.0F);
      foreShinScale.getAmounts(this.bomb, foreShinValue, 0.05F * poseProgress.getAnimationProgressForFloat((float) (Math.PI / 2)), shinProgressFactor);
      float angle = poseProgress.inPose ? 0.5F : poseProgress.getRadians(!poseProgress.gliding && !poseProgress.swimming ? 48.0F : 75.0F) + poseProgress.animationProgress * 1.3F;
      if (sneakingPose) {
         angle *= 0.4F;
      }

      foreShinScale.getAmounts(this.foreEar, 0.0F, 0.0F, angle);
      foreShinScale.getAmounts(this.shinFootForeEar, 0.0F, 0.0F, -angle);
      float shinProgressValue5 = getAmount(poseProgress.headYawRadians, -0.9F, 0.9F);
      foreShinScale.getAmounts(
         this.eyeL,
         getAmount(poseProgress.shinProgressAmount, -0.7F, 0.7F) * 0.45F + 0.02F * poseProgress.getAgeCode(70.0F, 0.4F),
         shinProgressValue5 * 0.6F + 0.04F * poseProgress.getAgeCode(95.0F, 2.0F),
         -0.1F * shinProgressFactor + 0.15F * shinProgressValue5
      );
      float shinProgressValue6 = (poseProgress.age + (poseProgress.hashCode & 127)) % 97.0F;
      float pilotStatusBound = shinProgressValue6 < 6.0F ? (float)Math.sin(shinProgressValue6 / 6.0F * Math.PI) : 0.0F;
      if (sneakingPose) {
         pilotStatusBound = Math.max(pilotStatusBound, 0.45F);
      }

      if (poseProgress.inPose) {
         pilotStatusBound = 1.0F;
      }

      foreShinScale.getAmounts(this.lidR, 0.0F, 0.35F * shinProgressValue5, 0.0F);
      foreShinScale.getAmounts(this.lidL, 0.0F, 0.35F * shinProgressValue5, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(this.lidR, 1.0F, 1.0F - 0.3F * pilotStatusBound, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.lidL, 1.0F, 1.0F - 0.3F * pilotStatusBound, 1.0F);
      handle(foreShinScale, this.classLidEye, -1.1F * pilotStatusBound, -radians);
      handle(foreShinScale, this.lidBombardiroForeEar, -1.1F * pilotStatusBound, radians);
      foreShinScale.getAmounts(this.shinProgressForeEar, 0.0F, 0.06F * poseProgress.getAgeCode(48.0F, 0.5F) + 0.12F * poseProgress.getAnimationProgressForFloat((float) (Math.PI / 2)), 0.0F);
      foreShinScale.getAmounts(
         this.classShinProgress,
         0.06F * poseProgress.getAgeCode(40.0F, 1.0F) - 0.9F * foreShinValue - 0.22F * shinProgressValue + 0.1F * poseProgress.getAnimationProgressForFloat(0.8F),
         0.0F,
         0.08F * poseProgress.getAgeCode(52.0F, 0.2F) - 0.9F * shinProgressFactor
      );
      float shinProgressTotal = 0.07F;
      float shinProgressValue7 = (poseProgress.age + (poseProgress.hashCode & 63)) % 84.0F;
      if (shinProgressValue7 < 16.0F && !poseProgress.inPose) {
         shinProgressTotal += 0.2F * Math.max(0.0F, (float)Math.sin(shinProgressValue7 / 8.0F * Math.PI));
      }

      float shinProgressTotal2 = shinProgressTotal + 0.12F * shinProgressValue * Math.max(0.0F, (float)Math.sin(poseProgress.animationProgress * 1.2F));
      if (sneakingPose) {
         foreShinScale.getAmountsForeShin2(this.bomb, 0.0F, -2.4F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.bomb, -0.12F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.eyeL, -0.15F, 0.0F, 0.0F);
         shinProgressTotal2 = 0.0F;
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmountsForeShin2(this.bomb, 0.0F, -shinProgressValue2, 0.0F);
         foreShinScale.getAmountsForeShin2(this.sling, 0.0F, 1.8F, 0.0F);
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressFactor3 = getAmount(swingProgress / 0.25F) * (1.0F - getAmount((swingProgress - 0.55F) / 0.45F));
         foreShinScale.getAmountsBoneCount(this.bomb, -0.3F * shinProgressFactor3, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.bomb, 0.0F, -1.2F * shinProgressFactor3, 0.0F);
         float shinProgressValue8 = swingProgress < 0.3F ? getAmount(swingProgress / 0.3F) : 1.0F - getAmount((swingProgress - 0.3F) / 0.12F);
         shinProgressTotal2 += 0.32F * shinProgressValue8;
         float shinProgressFactor4 = swingProgress < 0.45F ? getAmount((swingProgress - 0.08F) / 0.37F) : 1.0F - getAmount((swingProgress - 0.62F) / 0.38F);
         float shinProgressValue9 = swingProgress > 0.45F ? 1.2F * (float)Math.sin((swingProgress - 0.45F) * 6.0F * Math.PI) * (1.0F - swingProgress) : 0.0F;
         float shinProgressValue10 = (poseProgress.vehicle ? 0.0F : shinProgressValue2) - (sneakingPose ? 2.4F : 0.0F);
         foreShinScale.getAmountsForeShin2(this.sling, 0.0F, Math.max(-4.5F * shinProgressFactor4 + shinProgressValue9, 1.2F * shinProgressFactor3 - shinProgressValue10 - 5.45F), 0.0F);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, 0.5F * shinProgressFactor4, 0.0F, 0.25F * shinProgressFactor4);
         if (swingProgress > 0.5F) {
            foreShinScale.getAmountsBoneCount(this.bomb, 0.0F, 0.0F, 0.44F * (float)Math.sin((swingProgress - 0.5F) * 4.0F * Math.PI) * (1.0F - swingProgress));
         }
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.bomb, 1.05F, 0.95F, 1.05F);
         shinProgressTotal2 += 0.25F;
      }

      foreShinScale.getAmounts(this.classShinFoot, -shinProgressTotal2, 0.0F, 0.0F);
   }

   private void handleLidEye(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      for (int i = -1; i <= 1; i += 2) {
         float lidEyeValue = i * 1.95F;
         float spineLimit = -7.0F;
         float lidEyeValue3 = CLASS.getAmount(lidEyeValue, spineLimit);
         float latheLoftValue = i * 2.1F;
         float armTrunkValue = lidEyeValue3 + 1.0F;
         latheLoft.getLatheLoftForForeEar(this.eyeL);
         latheLoft.getLatheLoft(lidEyeValue, lidEyeValue3 + 0.05F, spineLimit + 0.4F, 1.45F, 1.0F, 1.9F, 10, shinBrr2);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(i * 1.3999999F, armTrunkValue + 0.8F, -8.15F).getLatheLoft4(-i * 25.0F).getLatheLoft(i * 30.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.05F, 0.3F, 0.62F, 8, shinBrr4);
         latheLoft.getDequeArray();
         latheLoft.getLatheLoftForForeEar(i > 0 ? this.lidR : this.lidL);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, armTrunkValue, -7.6F).getLatheLoft4(-i * 38.0F).getLatheLoftLatheLoftAmountLoft(6.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.35F, 1.15F, 1.25F, 12, shinBrr);
         latheLoft.getDequeArray();
         latheLoft.getLatheLoftForForeEar(i > 0 ? this.classLidEye : this.lidBombardiroForeEar);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, armTrunkValue, -7.6F).getLatheLoft4(-i * 38.0F).getLatheLoft(i * 18.0F).getLatheLoftLatheLoftAmountLoft(2.0F);
         latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 1.42F, 1.3F, 1.38F, 12, 0.0F, 0.34F, shinBrr3);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.eyeL);
   }

   @Override
   public float getAmountFloat() {
      return 1.3F;
   }

   @Override
   public String formatTungDin() {
      return "bombardiro_crocodilo";
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4215599), ForeShinUtils.getValueForInt(5597500))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4085288), 0.3F, 5.0F, 141)
         .getBonecaAmbalabu(0.06F, 2.0F, 142);

      for (int i = 0; i < 26; i++) {
         float bonecaAmbalabuValue = getAmount(i, 1, 143) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = bonecaAmbalabu.bonecaValue * (0.08F + 0.5F * getAmount(i, 2, 143));
         float classDivisor = 1.6F + 1.2F * getAmount(i, 3, 143);
         int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt((int)bonecaAmbalabuValue, (int)amountForFloat);
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, classDivisor + 0.8F, classDivisor + 0.8F, ForeShinUtils.getValueForIntFloat(3031070, 0.5F));
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, classDivisor, classDivisor, bonecaAmbalabuForInt);
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue - 0.35F * classDivisor, amountForFloat - 0.35F * classDivisor, 0.45F * classDivisor, 0.45F * classDivisor, ForeShinUtils.getValueForIntFloat(9345638, 0.35F));
      }
   }

   private static void handle(LatheLoft latheLoft, LidBombardiro.Spine spine, float lidBombardiroValue, float spineLimit, float lidBombardiroValue3, ShinBrr shinBrr) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(lidBombardiroValue, spine.getAmount(lidBombardiroValue, spineLimit) - 0.18F, spineLimit).getLatheLoft4(45.0F);
      latheLoft.getLatheLoft(new float[]{0.55F * lidBombardiroValue3, 0.02F}, new float[]{0.0F, 0.62F * lidBombardiroValue3}, 4, shinBrr, null, null);
      latheLoft.getDequeArray();
   }

   private static float getAmount(int lidBombardiroValue, int m, int lidBombardiroValue3) {
      int lidBombardiroValue4 = lidBombardiroValue * 374761393 + m * 668265263 + lidBombardiroValue3 * 1442695041;
      int lidBombardiroValue5 = (lidBombardiroValue4 ^ lidBombardiroValue4 >>> 13) * 1274126177;
      int lidBombardiroValue6 = lidBombardiroValue5 ^ lidBombardiroValue5 >>> 16;
      return (lidBombardiroValue6 & 16777215) / 1.6777215E7F;
   }

   private static float getAmount(float lidBombardiroLimit, float lidBombardiroLimit2, float lidBombardiroValue) {
      return lidBombardiroLimit < lidBombardiroLimit2 ? lidBombardiroLimit2 : (lidBombardiroLimit > lidBombardiroValue ? lidBombardiroValue : lidBombardiroLimit);
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue) {
      return bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuValue / 360.0F);
   }

   private static void handleAmountsBoneca(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float absScaled = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, j)) / 180.0F;
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(ForeShinUtils.getValueForInt(3882806), 1.18F - 0.3F * absScaled));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.06F, 2.0F, 91);
      float bonecaValue = bonecaAmbalabu.bonecaValue * getAmount(bonecaTable, shinFootTable, -2.0F);
      float bonecaValue2 = bonecaAmbalabu.bonecaValue * getAmount(bonecaTable, shinFootTable, -2.7F);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue, bonecaAmbalabu.ambalabuBonecaValue, bonecaValue2, ForeShinUtils.getValueForInt(14267450));
      float bonecaValue3 = bonecaAmbalabu.bonecaValue * getAmount(bonecaTable, shinFootTable, 1.9F);
      float bonecaValue4 = bonecaAmbalabu.bonecaValue * getAmount(bonecaTable, shinFootTable, 1.6F);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue3, bonecaAmbalabu.ambalabuBonecaValue, bonecaValue4, ForeShinUtils.getValueForInt(14267450));
      float bonecaValue5 = bonecaAmbalabu.bonecaValue * getAmount(bonecaTable, shinFootTable, -0.2F);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue5 - 0.5F, bonecaAmbalabu.ambalabuBonecaValue, bonecaValue5 + 0.5F, ForeShinUtils.getValueForIntFloat(2303519, 0.8F));
   }

   @Override
   public String formatNoteBlock() {
      return "entity.ravager.roar";
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float degrees = getBonecaAmbalabu(bonecaAmbalabu, j);
            float degrees2 = ((i + 0.5F) / bonecaAmbalabu.bonecaValue - 0.5F) * 180.0F;
            float bonecaAmbalabuValue = (float)Math.sqrt(degrees * degrees + degrees2 * degrees2) / 80.0F;
            int foreShinValue = bonecaAmbalabuValue < 1.0F
               ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15785056), ForeShinUtils.getValueForInt(13218362), bonecaAmbalabuValue)
               : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13218362), ForeShinUtils.getValueForInt(6181912), Math.min(1.0F, (bonecaAmbalabuValue - 1.0F) * 2.0F));
            float degrees3 = (float)Math.toDegrees(Math.acos(Math.cos(Math.toRadians(degrees)) * Math.cos(Math.toRadians(degrees2))));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(4215599), getAmount((degrees3 - 92.0F) / 18.0F)));
         }
      }

      float classValue = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      float amountForFloat = bonecaAmbalabu.bonecaValue * 0.5F;

      for (int k = 0; k < 18; k++) {
         double angle = k * Math.PI * 2.0 / 18.0;
         bonecaAmbalabu.getClass02(
            classValue + (float)Math.cos(angle) * 4.0F,
            amountForFloat + (float)Math.sin(angle) * 4.0F,
            classValue + (float)Math.cos(angle) * 18.0F,
            amountForFloat + (float)Math.sin(angle) * 18.0F,
            0.8F,
            ForeShinUtils.getValueForIntFloat(9206302, 0.45F)
         );
      }

      float classDivisor = 0.21111111F * bonecaAmbalabu.ambalabuBonecaValue;
      bonecaAmbalabu.getClass022(classValue, amountForFloat, classDivisor, classDivisor, 1.5F, ForeShinUtils.getValueForInt(6181912));
      bonecaAmbalabu.getClass022(classValue, amountForFloat, 3.4F, 20.0F, ForeShinUtils.getValueForInt(1118481));
      bonecaAmbalabu.getClass022(classValue - 5.0F, amountForFloat - 7.0F, 2.2F, 2.2F, ForeShinUtils.getValueForIntFloat(16777215, 0.9F));
   }

   private static void handle(LatheLoft latheLoft, float lidBombardiroValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float bladeScaled = lidBombardiroValue * 10.8F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(bladeScaled, 13.9F, -3.0F).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLatheLoft(new float[]{1.05F, 0.75F, 0.05F}, new float[]{0.0F, 1.1F, 2.0F}, 12, shinBrr2, shinBrr3, null);
      latheLoft.getDequeArray();

      for (int i = 0; i < 3; i++) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(bladeScaled, 13.9F, -3.45F).getLatheLoft(i * 120.0F + 90.0F).getLatheLoft4(lidBombardiroValue * 24.0F);
         latheLoft.getLoftPath(
            new float[]{0.0F, 0.45F, 0.0F, 0.0F, 1.8F, 0.0F, 0.0F, 3.2F, 0.0F, 0.0F, 3.95F, 0.0F},
            new float[]{0.3F, 0.6F, 0.52F, 0.2F},
            new float[]{0.14F, 0.11F, 0.08F, 0.05F},
            5,
            shinBrr,
            null,
            null,
            0.0F,
            0.0F,
            -1.0F
         );
         latheLoft.getDequeArray();
      }
   }

   private static void handle(ForeShinScale foreShinScale, ForeEar foreEar, float angle2, float angle) {
      float cos = (float)Math.cos(angle);
      float sin = -((float)Math.sin(angle));
      float cos2 = (float)Math.cos(angle2);
      float sin2 = (float)Math.sin(angle2);
      float lidBombardiroFactor = 1.0F - cos2;
      foreShinScale.getAmounts(
         foreEar,
         (float)Math.atan2(sin2 * cos, cos2 + lidBombardiroFactor * sin * sin),
         (float)Math.asin(getAmount(-lidBombardiroFactor * cos * sin, -1.0F, 1.0F)),
         (float)Math.atan2(sin2 * sin, cos2 + lidBombardiroFactor * cos * cos)
      );
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(9348784), ForeShinUtils.getValueForInt(5006189));

      for (int i = 0; i < 8; i++) {
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * i / 8.0F - 0.8F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue * i / 8.0F + 0.8F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(3816755));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.5F - 0.6F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.5F + 0.6F, ForeShinUtils.getValueForInt(3816755));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.86F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(3816755));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.36F, bonecaAmbalabu.bonecaValue * 0.3F, 2.4F, 1.6F, ForeShinUtils.getValueForIntFloat(16777215, 0.55F));
   }

   private static void handleBonecaAmbalabuLOWER_SPINE(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue / 192.0F;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float absBonecaAmbalabu = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, j));
            int bonecaAmbalabuValue;
            if (absBonecaAmbalabu < 110.0F) {
               bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5597500), ForeShinUtils.getValueForInt(10723948), Math.max(0.0F, (absBonecaAmbalabu - 70.0F) / 40.0F));
            } else {
               bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12893824), ForeShinUtils.getValueForInt(14077839), (absBonecaAmbalabu - 110.0F) / 70.0F);
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4085288), 0.25F, 8.0F, 41).getBonecaAmbalabu(0.06F, 3.0F, 42);

      for (int k = 0; k < 22; k++) {
         float bonecaAmbalabuValue2 = bonecaAmbalabu.bonecaValue * LOWER_SPINE.getAmount(-5.5F - k * 0.62F);
         bonecaAmbalabu.getClassBonecaAmbalabu(
            getBonecaAmbalabu(bonecaAmbalabu, 112.0F), bonecaAmbalabuValue2 - 0.5F * bonecaAmbalabuFactor, getBonecaAmbalabu(bonecaAmbalabu, 180.0F), bonecaAmbalabuValue2 + 0.5F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(10393184, 0.7F)
         );
         bonecaAmbalabu.getClassBonecaAmbalabu(
            getBonecaAmbalabu(bonecaAmbalabu, -180.0F), bonecaAmbalabuValue2 - 0.5F * bonecaAmbalabuFactor, getBonecaAmbalabu(bonecaAmbalabu, -112.0F), bonecaAmbalabuValue2 + 0.5F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(10393184, 0.7F)
         );
      }

      for (int m = 0; m < 3; m++) {
         float bonecaAmbalabuValue3 = 130.0F + m * 20.0F;
         bonecaAmbalabu.getClassBonecaAmbalabu(
            getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue3) - 0.5F * bonecaAmbalabuFactor, 0.0F, getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue3) + 0.5F * bonecaAmbalabuFactor, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(10393184, 0.6F)
         );
         bonecaAmbalabu.getClassBonecaAmbalabu(
            getBonecaAmbalabu(bonecaAmbalabu, -bonecaAmbalabuValue3) - 0.5F * bonecaAmbalabuFactor, 0.0F, getBonecaAmbalabu(bonecaAmbalabu, -bonecaAmbalabuValue3) + 0.5F * bonecaAmbalabuFactor, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(10393184, 0.6F)
         );
      }

      for (int p = 0; p < 48; p++) {
         float spineValue = -7.0F - getAmount(p, 4, 19) * 12.5F;
         float bonecaAmbalabuValue5 = (76.0F + getAmount(p, 5, 19) * 26.0F) * (p % 2 == 0 ? 1.0F : -1.0F);
         float classValue = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue5);
         float amountForFloat = bonecaAmbalabu.bonecaValue * LOWER_SPINE.getAmount(spineValue);
         float classDivisor = (1.0F + 0.5F * getAmount(p, 6, 19)) * bonecaAmbalabuFactor;
         int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt((int)classValue, (int)amountForFloat);
         bonecaAmbalabu.getClass022(classValue, amountForFloat, classDivisor + 0.6F * bonecaAmbalabuFactor, classDivisor + 0.6F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(3031070, 0.45F));
         bonecaAmbalabu.getClass022(classValue, amountForFloat, classDivisor, classDivisor, bonecaAmbalabuForInt);
         bonecaAmbalabu.getClass022(classValue - 0.35F * classDivisor, amountForFloat - 0.35F * classDivisor, 0.45F * classDivisor, 0.45F * classDivisor, ForeShinUtils.getValueForIntFloat(10133614, 0.35F));
      }

      for (int q = 0; q < bonecaAmbalabu.bonecaValue; q++) {
         for (int r = 0; r < bonecaAmbalabu.ambalabuBonecaValue; r++) {
            float abs2 = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, r));
            if (abs2 < 40.0F) {
               bonecaAmbalabu.getBonecaAmbalabu(r, q, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(10111573), ForeShinUtils.getValueForInt(12083306), abs2 / 40.0F));
            } else if (abs2 < 70.0F) {
               bonecaAmbalabu.getBonecaAmbalabu(
                  r, q, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13617298), ForeShinUtils.getValueForInt(10724714), (abs2 - 40.0F) / 30.0F)
               );
            }
         }
      }

      float bonecaAmbalabuValue8 = LOWER_SPINE.getAmount(-7.0F);
      float bonecaAmbalabuValue9 = LOWER_SPINE.getAmount(-12.0F);

      for (int s = 0; s < bonecaAmbalabu.bonecaValue; s++) {
         float bonecaAmbalabuValue10 = 1.0F - getAmount(((s + 0.5F) / bonecaAmbalabu.bonecaValue - bonecaAmbalabuValue8) / (bonecaAmbalabuValue9 - bonecaAmbalabuValue8));
         if (!(bonecaAmbalabuValue10 <= 0.0F)) {
            for (int t = 0; t < bonecaAmbalabu.ambalabuBonecaValue; t++) {
               if (Math.abs(getBonecaAmbalabu(bonecaAmbalabu, t)) < 40.0F) {
                  bonecaAmbalabu.getBonecaAmbalabu(t, s, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(t, s), ForeShinUtils.getValueForInt(5119530), 0.8F * bonecaAmbalabuValue10));
               }
            }
         }
      }
   }

   private static void handle(LatheLoft latheLoft, float lidBombardiroFactor, ShinBrr shinBrr) {
      float[] floats = new float[]{0.3F, 3.8F, 6.2F, 7.4F, 7.8F};
      float[] floats2 = new float[]{21.4F, 21.65F, 21.9F, 22.0F, 22.05F};
      float[] loftPathValues = new float[15];

      for (int i = 0; i < 5; i++) {
         loftPathValues[3 * i] = lidBombardiroFactor * floats[i];
         loftPathValues[3 * i + 1] = 16.75F + 0.06F * i;
         loftPathValues[3 * i + 2] = floats2[i];
      }

      latheLoft.getLoftPath(loftPathValues, new float[]{2.0F, 1.75F, 1.35F, 0.85F, 0.25F}, new float[]{0.32F, 0.26F, 0.2F, 0.14F, 0.06F}, 6, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float lidBombardiroFactor, ShinBrr shinBrr) {
      float[] floats = new float[]{3.0F, 14.0F, 21.0F, 23.0F, 23.6F};
      float[] floats2 = new float[]{13.7F, 14.35F, 14.8F, 14.95F, 15.0F};
      float[] floats3 = new float[]{3.3F, 3.05F, 2.9F, 2.8F, 2.75F};
      float[] loftPathValues = new float[15];

      for (int i = 0; i < 5; i++) {
         loftPathValues[3 * i] = lidBombardiroFactor * floats[i];
         loftPathValues[3 * i + 1] = floats2[i];
         loftPathValues[3 * i + 2] = floats3[i];
      }

      latheLoft.getLoftPath(loftPathValues, new float[]{3.8F, 2.95F, 2.2F, 1.35F, 0.35F}, new float[]{0.85F, 0.62F, 0.42F, 0.28F, 0.1F}, 8, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
   }

   private static float getAmount(float[] lidBombardiroValues, float[] lidBombardiroValues2, float lidBombardiroValue) {
      float[] floats = new float[lidBombardiroValues.length];

      for (int i = 1; i < lidBombardiroValues.length; i++) {
         float lidBombardiroFactor = lidBombardiroValues[i] - lidBombardiroValues[i - 1];
         float lidBombardiroFactor2 = lidBombardiroValues2[i] - lidBombardiroValues2[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(lidBombardiroFactor * lidBombardiroFactor + lidBombardiroFactor2 * lidBombardiroFactor2);
      }

      for (int j = 1; j < lidBombardiroValues.length; j++) {
         if (lidBombardiroValue <= lidBombardiroValues2[j]) {
            float lidBombardiroFactor3 = (lidBombardiroValue - lidBombardiroValues2[j - 1]) / (lidBombardiroValues2[j] - lidBombardiroValues2[j - 1]);
            return 1.0F - (floats[j - 1] + (floats[j] - floats[j - 1]) * lidBombardiroFactor3) / floats[lidBombardiroValues.length - 1];
         }
      }

      return 0.0F;
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float lidBombardiroValue, float bonecaAmbalabuValue, float lidBombardiroValue3, float bonecaValue, float bonecaAmbalabuFactor) {
      float wrapDegreesBound = getBonecaAmbalabu(bonecaAmbalabu, lidBombardiroValue);
      float hashHuntBound = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue);
      float lidBombardiroBound = Math.min(wrapDegreesBound, hashHuntBound);
      float coverageValue = Math.max(wrapDegreesBound, hashHuntBound);

      for (int i = (int)Math.floor(lidBombardiroBound); i < (int)Math.ceil(coverageValue); i++) {
         float foreShinFactor = getAmount(Math.abs(i + 0.5F - wrapDegreesBound) / (coverageValue - lidBombardiroBound), 0.0F, 1.0F);
         bonecaAmbalabu.getClassBonecaAmbalabu(
            Math.max((float)i, lidBombardiroBound),
            lidBombardiroValue3,
            Math.min(i + 1.0F, coverageValue),
            bonecaValue,
            ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(11125967), ForeShinUtils.getValueForInt(8363691), foreShinFactor)
         );
      }

      float classValue = wrapDegreesBound + Math.signum(hashHuntBound - wrapDegreesBound) * (coverageValue - lidBombardiroBound) * 0.26F;
      bonecaAmbalabu.getClass022(classValue, lidBombardiroValue3 + (bonecaValue - lidBombardiroValue3) * 0.3F, (coverageValue - lidBombardiroBound) * 0.13F, (bonecaValue - lidBombardiroValue3) * 0.17F, ForeShinUtils.getValueForIntFloat(16777215, 0.85F));
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15245478), ForeShinUtils.getValueForInt(12611706)).getBonecaAmbalabu(0.05F, 2.0F, 121);
      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(15708342, 0.5F), 3.0F, 0.7F, 122);

      for (float bonecaAmbalabuValue : new float[]{0.0F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.ambalabuBonecaValue}) {
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuValue - 1.3F, 0.0F, bonecaAmbalabuValue + 1.3F, bonecaAmbalabu.bonecaValue * 0.42F, ForeShinUtils.getValueForIntFloat(9059400, 0.85F));
      }
   }

   private static void handle(LatheLoft latheLoft, float lidBombardiroValue, float lidBombardiroValue2, ShinBrr shinBrr) {
      latheLoft.getLatheLoft(new float[]{lidBombardiroValue, 0.02F}, new float[]{0.0F, lidBombardiroValue2}, 5, shinBrr, null, null);
   }

   @Override
   protected int getValueInt() {
      return 1024;
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4215599), ForeShinUtils.getValueForInt(5597500))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(4085288), 0.4F, 4.0F, 111)
         .getBonecaAmbalabu(0.06F, 2.0F, 112);
      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(3031070, 0.6F), 3.0F, 0.8F, 113);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = 1.0F - getAmount((Math.abs((i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) - 0.2F) / 0.2F);
         bonecaAmbalabu.getClassBonecaAmbalabu(i, bonecaAmbalabu.bonecaValue * 0.78F, i + 1, bonecaAmbalabu.bonecaValue * 0.86F, ForeShinUtils.getValueForIntFloat(6979150, bonecaAmbalabuValue));
         bonecaAmbalabu.getClassBonecaAmbalabu(i, bonecaAmbalabu.bonecaValue * (0.86F + 0.06F * (1.0F - bonecaAmbalabuValue)), i + 1, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(1844244, 0.3F + 0.7F * bonecaAmbalabuValue));
      }
   }

   private static void handleBonecaAmbalabuUPPER_SPINE(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue / 256.0F;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float absBonecaAmbalabu = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, j));
            int bonecaAmbalabuValue = absBonecaAmbalabu < 70.0F
               ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(6844496), ForeShinUtils.getValueForInt(7238998), absBonecaAmbalabu / 70.0F)
               : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(7238998), ForeShinUtils.getValueForInt(8159850), (absBonecaAmbalabu - 70.0F) / 110.0F);
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5593922), 0.55F, 8.0F, 31).getBonecaAmbalabu(0.06F, 2.5F, 32);
      float bonecaAmbalabuFactor2 = 0.85F;

      for (int k = 0; k < 15; k++) {
         for (int m = 0; m < 24; m++) {
            float bonecaAmbalabuValue2 = -6.4F + k * bonecaAmbalabuFactor2 + (getAmount(k, m, 13) - 0.5F) * 0.3F;
            float bonecaAmbalabuValue3 = -180.0F + m * 15.0F + k % 2 * 7.5F + (getAmount(k, m, 14) - 0.5F) * 4.0F;
            float bonecaAmbalabuValue4 = 1.0F - getAmount((bonecaAmbalabuValue2 + 2.2F + (getAmount(k, m, 3) - 0.5F) * 3.0F) / 5.5F);
            if (!(bonecaAmbalabuValue4 < 0.08F)) {
               float bonecaAmbalabuValue5 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(bonecaAmbalabuValue2);
               float bonecaAmbalabuValue6 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(bonecaAmbalabuValue2 + bonecaAmbalabuFactor2 * (0.9F + 0.25F * getAmount(k, m, 16)));
               float lidBombardiroValue = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue3);
               float bonecaAmbalabuValue7 = bonecaAmbalabu.ambalabuBonecaValue * 15.0F / 360.0F * 0.5F * (0.8F + 0.4F * getAmount(k, m, 15));
               float bonecaAmbalabuValue8 = 1.1F * bonecaAmbalabuFactor;
               handle(
                  bonecaAmbalabu,
                  lidBombardiroValue,
                  bonecaAmbalabuValue5,
                  bonecaAmbalabuValue6,
                  bonecaAmbalabuValue7,
                  bonecaAmbalabuValue8,
                  bonecaAmbalabuFactor,
                  ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(7238998), ForeShinUtils.getValueForInt(5597500), bonecaAmbalabuValue4),
                  4085288,
                  bonecaAmbalabuValue4,
                  0.9F
               );
            }
         }
      }

      for (int p = 0; p < 33; p++) {
         float bonecaAmbalabuLimit = -1.2F + p * 0.75F;
         if (bonecaAmbalabuLimit > 22.2F) {
            break;
         }

         float foreShinFactor = getAmount((bonecaAmbalabuLimit + 1.0F) / 22.0F);
         float lidBombardiroFactor = 0.95F - 0.35F * foreShinFactor;
         int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5597500), ForeShinUtils.getValueForInt(5726271), foreShinFactor);
         int bonecaAmbalabuValue12 = bonecaAmbalabuLimit > 6.0F ? -1 : -2;
         int bonecaAmbalabuLimit2 = bonecaAmbalabuLimit > 6.0F ? 0 : 1;

         for (int q = bonecaAmbalabuValue12; q <= bonecaAmbalabuLimit2; q++) {
            float bonecaAmbalabuValue13 = bonecaAmbalabuLimit + (getAmount(p, q + 5, 27) - 0.5F) * 0.2F;
            float bonecaAmbalabuValue14 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(bonecaAmbalabuValue13);
            float bonecaAmbalabuValue15 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(bonecaAmbalabuValue13 + 0.75F * (0.9F + 0.2F * getAmount(p, q + 5, 28)));
            float bonecaAmbalabuValue16 = bonecaAmbalabu.ambalabuBonecaValue
               * 14.0F
               / 360.0F
               * 0.5F
               * (q != -2 && q != 1 ? 1.0F : 1.0F - 0.4F * foreShinFactor)
               * (0.85F + 0.3F * getAmount(p, q + 5, 25));
            float bonecaAmbalabu3 = getBonecaAmbalabu(bonecaAmbalabu, (q + 0.5F) * 14.0F + (getAmount(p, q + 5, 26) - 0.5F) * 3.0F);
            handle(bonecaAmbalabu, bonecaAmbalabu3, bonecaAmbalabuValue14, bonecaAmbalabuValue15, bonecaAmbalabuValue16, 1.1F * bonecaAmbalabuFactor, bonecaAmbalabuFactor, foreShinValue, 3424287, lidBombardiroFactor, 0.75F);
         }
      }

      float[] floats = new float[]{0.6F, 3.4F, 6.3F, 9.3F, 12.4F, 15.5F, 18.6F, 21.6F};

      for (float floatsItem : floats) {
         float bonecaAmbalabuValue18 = getAmount((floatsItem + 0.5F) / 2.5F);
         float bonecaAmbalabuValue19 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(floatsItem);
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue19 - 0.7F * bonecaAmbalabuFactor, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuValue19 + 0.7F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(5001792, 0.9F * bonecaAmbalabuValue18));

         for (float bonecaAmbalabuTotal = 2.0F; bonecaAmbalabuTotal < bonecaAmbalabu.ambalabuBonecaValue; bonecaAmbalabuTotal += 5.0F * bonecaAmbalabuFactor) {
            bonecaAmbalabu.getClass022(bonecaAmbalabuTotal, bonecaAmbalabuValue19 - 2.4F * bonecaAmbalabuFactor, 0.6F * bonecaAmbalabuFactor, 0.6F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(10133630, 0.7F * bonecaAmbalabuValue18));
            bonecaAmbalabu.getClass022(bonecaAmbalabuTotal, bonecaAmbalabuValue19 + 2.4F * bonecaAmbalabuFactor, 0.6F * bonecaAmbalabuFactor, 0.6F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(10133630, 0.7F * bonecaAmbalabuValue18));
         }
      }

      float bonecaAmbalabuValue20 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(0.6F);
      float[] floats2 = new float[]{40.0F, 95.0F, 145.0F, 180.0F};

      for (float bonecaAmbalabuFactor3 : floats2) {
         for (int r = -1; r <= 1; r += 2) {
            float bonecaAmbalabu4 = getBonecaAmbalabu(bonecaAmbalabu, r * bonecaAmbalabuFactor3);
            bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu4 - 0.6F * bonecaAmbalabuFactor, bonecaAmbalabuValue20, bonecaAmbalabu4 + 0.6F * bonecaAmbalabuFactor, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(5001792, 0.8F));
         }
      }

      for (int s = 0; s < 8; s++) {
         float bonecaAmbalabuValue21 = (s % 2 == 0 ? 1.0F : -1.0F) * (62.0F + getAmount(s, 1, 71) * 50.0F);
         float spineValue = 7.3F + getAmount(s, 2, 71) * 1.2F;
         float bonecaAmbalabuValue23 = spineValue + 3.5F + getAmount(s, 3, 71) * 3.5F;
         float bonecaAmbalabu5 = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue21);
         float bonecaAmbalabuValue24 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(spineValue);
         float bonecaAmbalabuValue25 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(bonecaAmbalabuValue23);
         float bonecaAmbalabuValue26 = (1.6F + getAmount(s, 4, 71)) * bonecaAmbalabuFactor;
         bonecaAmbalabu.getClassForIntFloatArray(
            ForeShinUtils.getValueForIntFloat(5001792, 0.25F), bonecaAmbalabu5 - bonecaAmbalabuValue26, bonecaAmbalabuValue24, bonecaAmbalabu5 + bonecaAmbalabuValue26, bonecaAmbalabuValue24, bonecaAmbalabu5 + 0.3F * bonecaAmbalabuValue26, bonecaAmbalabuValue25, bonecaAmbalabu5 - 0.3F * bonecaAmbalabuValue26, bonecaAmbalabuValue25
         );
      }

      for (int t = -1; t <= 1; t += 2) {
         float bonecaAmbalabuValue27 = 1.2F * bonecaAmbalabuFactor;
         float minBonecaAmbalabu = Math.min(getBonecaAmbalabu(bonecaAmbalabu, t * 76.0F), getBonecaAmbalabu(bonecaAmbalabu, t * 102.0F));
         float maxBonecaAmbalabu = Math.max(getBonecaAmbalabu(bonecaAmbalabu, t * 76.0F), getBonecaAmbalabu(bonecaAmbalabu, t * 102.0F));

         for (float bonecaAmbalabuValue28 : new float[]{9.0F, 10.75F}) {
            float bonecaAmbalabuValue29 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(bonecaAmbalabuValue28);
            float bonecaAmbalabuValue30 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(bonecaAmbalabuValue28 + 1.35F);
            bonecaAmbalabu.getClassBonecaAmbalabu(minBonecaAmbalabu - bonecaAmbalabuValue27, bonecaAmbalabuValue29 - bonecaAmbalabuValue27, maxBonecaAmbalabu + bonecaAmbalabuValue27, bonecaAmbalabuValue30 + bonecaAmbalabuValue27, ForeShinUtils.getValueForInt(3816755));
            handle(bonecaAmbalabu, t * 76.0F, t * 102.0F, bonecaAmbalabuValue29, bonecaAmbalabuValue30, bonecaAmbalabuFactor);
         }

         for (int bonecaAmbalabuCounter = 0; bonecaAmbalabuCounter < 3; bonecaAmbalabuCounter++) {
            float bonecaAmbalabuValue31 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(3.4F + bonecaAmbalabuCounter * 1.15F);
            float bonecaValue = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(4.05F + bonecaAmbalabuCounter * 1.15F);
            float bonecaAmbalabuValue33 = 0.9F * bonecaAmbalabuFactor;
            float min2 = Math.min(getBonecaAmbalabu(bonecaAmbalabu, t * 58.0F), getBonecaAmbalabu(bonecaAmbalabu, t * 70.0F));
            float max2 = Math.max(getBonecaAmbalabu(bonecaAmbalabu, t * 58.0F), getBonecaAmbalabu(bonecaAmbalabu, t * 70.0F));
            bonecaAmbalabu.getClassBonecaAmbalabu(min2 - bonecaAmbalabuValue33, bonecaAmbalabuValue31 - bonecaAmbalabuValue33, max2 + bonecaAmbalabuValue33, bonecaValue + bonecaAmbalabuValue33, ForeShinUtils.getValueForInt(3816755));
            handle(bonecaAmbalabu, t * 58.0F, t * 70.0F, bonecaAmbalabuValue31, bonecaValue, bonecaAmbalabuFactor);
         }
      }

      float bonecaAmbalabuValue34 = bonecaAmbalabu.bonecaValue * UPPER_SPINE.getAmount(23.3F);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue34, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(11125967));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag, boolean bonecaAmbalabuFlag2) {
      float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue / 128.0F;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuLimit = bonecaAmbalabuFlag ? -getBonecaAmbalabu(bonecaAmbalabu, j) : getBonecaAmbalabu(bonecaAmbalabu, j);
            float bonecaAmbalabuValue = Math.abs(bonecaAmbalabuLimit);
            int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(6975826), ForeShinUtils.getValueForInt(8751982), getAmount((bonecaAmbalabuValue - 82.0F) / 16.0F));
            if (bonecaAmbalabuLimit > -112.0F && bonecaAmbalabuLimit < -70.0F) {
               value2 = ForeShinUtils.getValueForInt(2895399);
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, value2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5725507), 0.4F, 6.0F, bonecaAmbalabuFlag ? 52 : 51).getBonecaAmbalabu(0.05F, 2.5F, 53);
      if (bonecaAmbalabuFlag2) {
         float bonecaAmbalabuFactor2 = 0.37500003F;

         for (int k = 0; k < 9; k++) {
            float bonecaAmbalabuValue3 = -40.0F + k * 13.0F;
            bonecaAmbalabu.getClass02(
               getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuFlag ? -bonecaAmbalabuValue3 : bonecaAmbalabuValue3),
               bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor2,
               (3.2F + k * 0.35F) * bonecaAmbalabuFactor,
               0.4F,
               k,
               ForeShinUtils.getValueForIntFloat(3882035, 0.35F)
            );
         }
      }

      float[] floats = new float[]{-40.0F, 30.0F, 62.0F, 118.0F, -140.0F, 150.0F};

      for (float floatsItem : floats) {
         float bonecaAmbalabu2 = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuFlag ? -floatsItem : floatsItem);
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu2 - 0.5F * bonecaAmbalabuFactor, 0.0F, bonecaAmbalabu2 + 0.5F * bonecaAmbalabuFactor, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(5001792, 0.85F));
      }

      float[] floats2 = new float[]{0.2F, 0.42F, 0.62F, 0.8F};

      for (float bonecaAmbalabuFactor3 : floats2) {
         bonecaAmbalabu.getClassBonecaAmbalabu(
            0.0F,
            bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor3 - 0.5F * bonecaAmbalabuFactor,
            bonecaAmbalabu.ambalabuBonecaValue,
            bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor3 + 0.5F * bonecaAmbalabuFactor,
            ForeShinUtils.getValueForIntFloat(5001792, 0.75F)
         );
      }

      for (int m = 0; m < bonecaAmbalabu.ambalabuBonecaValue; m++) {
         float bonecaAmbalabuLimit2 = bonecaAmbalabuFlag ? -getBonecaAmbalabu(bonecaAmbalabu, m) : getBonecaAmbalabu(bonecaAmbalabu, m);
         if (bonecaAmbalabuLimit2 > 62.0F && bonecaAmbalabuLimit2 < 118.0F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(
               m, bonecaAmbalabu.bonecaValue * 0.5F - 0.6F * bonecaAmbalabuFactor, m + 1, bonecaAmbalabu.bonecaValue * 0.5F + 0.6F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(5001792, 0.9F)
            );
         }
      }
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float lidBombardiroValue, float i, float bonecaValue, float lidBombardiroValue4, float lidBombardiroValue5, float bonecaAmbalabuFactor, int foreShinValue, int lidBombardiroValue8, float lidBombardiroFactor, float lidBombardiroFactor2) {
      float amountForFloat = 0.5F * (i + bonecaValue);
      float lidBombardiroValue10 = 0.5F * (bonecaValue - i);
      bonecaAmbalabu.getClassBonecaAmbalabu(lidBombardiroValue - lidBombardiroValue4, i, lidBombardiroValue + lidBombardiroValue4, bonecaValue, ForeShinUtils.getValueForIntFloat(lidBombardiroValue8, lidBombardiroFactor2 * lidBombardiroFactor));
      bonecaAmbalabu.getClass022(lidBombardiroValue, amountForFloat, lidBombardiroValue4 - lidBombardiroValue5, lidBombardiroValue10 - lidBombardiroValue5, ForeShinUtils.getValueForIntFloat(foreShinValue, lidBombardiroFactor));
      bonecaAmbalabu.getClass022(lidBombardiroValue - 0.15F * lidBombardiroValue4, amountForFloat - 0.4F * lidBombardiroValue10, 0.6F * lidBombardiroValue4, 0.3F * lidBombardiroValue10, ForeShinUtils.getValueForIntFloat(7899734, 0.4F * lidBombardiroFactor));
      bonecaAmbalabu.getClass02(lidBombardiroValue, i + 2.4F * lidBombardiroValue5, lidBombardiroValue, bonecaValue - 2.4F * lidBombardiroValue5, 1.4F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(7373390, 0.6F * lidBombardiroFactor));
      bonecaAmbalabu.getClass02(lidBombardiroValue + 1.2F * bonecaAmbalabuFactor, i + 2.6F * lidBombardiroValue5, lidBombardiroValue + 1.2F * bonecaAmbalabuFactor, bonecaValue - 2.6F * lidBombardiroValue5, 0.9F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(3426335, 0.4F * lidBombardiroFactor));
   }

   private static float getAmount(float lidBombardiroFactor) {
      lidBombardiroFactor = lidBombardiroFactor < 0.0F ? 0.0F : (lidBombardiroFactor > 1.0F ? 1.0F : lidBombardiroFactor);
      return lidBombardiroFactor * lidBombardiroFactor * (3.0F - 2.0F * lidBombardiroFactor);
   }

   private static void handleBonecaAmbalabuCLASS(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue / 256.0F;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float absBonecaAmbalabu = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, j));
            int bonecaAmbalabuValue;
            if (absBonecaAmbalabu < 45.0F) {
               bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(4215599), ForeShinUtils.getValueForInt(5597500), absBonecaAmbalabu / 45.0F);
            } else if (absBonecaAmbalabu < 100.0F) {
               bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5597500), ForeShinUtils.getValueForInt(8225364), (absBonecaAmbalabu - 45.0F) / 55.0F);
            } else {
               bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(8225364), ForeShinUtils.getValueForInt(12170871), (absBonecaAmbalabu - 100.0F) / 20.0F);
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4085288), 0.3F, 10.0F, 21).getBonecaAmbalabu(0.07F, 3.0F, 22);

      for (int k = 0; k < 4; k++) {
         float bonecaAmbalabuValue2 = bonecaAmbalabu.bonecaValue * CLASS.getAmount(-2.6F - k * 0.95F);

         for (int m = -2; m <= 2; m++) {
            float bonecaAmbalabu2 = getBonecaAmbalabu(bonecaAmbalabu, m * 13.0F + k % 2 * 6.5F);
            bonecaAmbalabu.getClass022(bonecaAmbalabu2, bonecaAmbalabuValue2, 4.6F * bonecaAmbalabuFactor, 3.8F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(2767386, 0.55F));
            bonecaAmbalabu.getClass022(bonecaAmbalabu2, bonecaAmbalabuValue2, 3.9F * bonecaAmbalabuFactor, 3.1F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(5005878, 0.9F));
            bonecaAmbalabu.getClass022(bonecaAmbalabu2 - 0.9F * bonecaAmbalabuFactor, bonecaAmbalabuValue2 - 0.9F * bonecaAmbalabuFactor, 2.2F * bonecaAmbalabuFactor, 1.4F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(6979150, 0.45F));
         }
      }

      for (int p = 0; p < 16; p++) {
         float spineValue = -5.4F - getAmount(p, 1, 5) * 3.0F;
         float bonecaAmbalabuValue4 = (getAmount(p, 2, 5) - 0.5F) * 40.0F;
         bonecaAmbalabu.getClass02(
            getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue4),
            bonecaAmbalabu.bonecaValue * CLASS.getAmount(spineValue),
            (2.2F + getAmount(p, 3, 5) * 1.6F) * bonecaAmbalabuFactor,
            0.3F,
            p,
            ForeShinUtils.getValueForIntFloat(4085288, 0.55F)
         );
      }

      for (int q = -1; q <= 1; q += 2) {
         float[] floats = new float[9];
         float[] floats2 = new float[9];

         for (int r = 0; r < 9; r++) {
            float bonecaAmbalabuValue5 = -9.0F - r;
            floats[r] = getBonecaAmbalabu(bonecaAmbalabu, q * (14.0F + 2.5F * (float)Math.sin(r * 0.9F)));
            floats2[r] = bonecaAmbalabu.bonecaValue * CLASS.getAmount(bonecaAmbalabuValue5);
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 1.4F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(4085288, 0.5F));
      }

      for (int s = 0; s < 16; s++) {
         for (int t = 0; t < 7; t++) {
            float bonecaAmbalabuValue6 = -8.7F - s * 0.68F + (getAmount(s, t, 31) - 0.5F) * 0.3F;
            if (!(bonecaAmbalabuValue6 < -19.5F) && !(getAmount(s, t, 34) > 0.35F + 0.65F * (bonecaAmbalabuValue6 + 19.5F) / 11.0F)) {
               float bonecaAmbalabuValue7 = -39.0F + t * 13.0F + s % 2 * 6.5F + (getAmount(s, t, 32) - 0.5F) * 5.0F;
               if (!(Math.abs(bonecaAmbalabuValue7) > 45.0F)) {
                  float bonecaAmbalabuValue8 = (1.0F + 0.9F * getAmount(s, t, 33)) * bonecaAmbalabuFactor;
                  float classValue = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue7);
                  float amountForFloat = bonecaAmbalabu.bonecaValue * CLASS.getAmount(bonecaAmbalabuValue6);
                  int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt((int)classValue, (int)amountForFloat);
                  bonecaAmbalabu.getClass022(classValue, amountForFloat, bonecaAmbalabuValue8 * 1.5F + 0.7F * bonecaAmbalabuFactor, bonecaAmbalabuValue8 + 0.7F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(3031070, 0.55F));
                  bonecaAmbalabu.getClass022(classValue, amountForFloat, bonecaAmbalabuValue8 * 1.5F, bonecaAmbalabuValue8, ForeShinUtils.getValueForIntFloat(ForeShinUtils.getValue(bonecaAmbalabuForInt, 1.05F), 1.0F));
                  bonecaAmbalabu.getClass022(classValue - 0.4F * bonecaAmbalabuValue8, amountForFloat - 0.35F * bonecaAmbalabuValue8, 0.6F * bonecaAmbalabuValue8, 0.4F * bonecaAmbalabuValue8, ForeShinUtils.getValueForIntFloat(9345638, 0.4F));
               }
            }
         }
      }

      for (int bonecaAmbalabuTotal = -1; bonecaAmbalabuTotal <= 1; bonecaAmbalabuTotal += 2) {
         for (int bonecaAmbalabuCounter = 0; bonecaAmbalabuCounter < 15; bonecaAmbalabuCounter++) {
            for (int bonecaAmbalabuCounter2 = 0; bonecaAmbalabuCounter2 < 8; bonecaAmbalabuCounter2++) {
               float bonecaAmbalabuLimit = -4.6F - bonecaAmbalabuCounter * 1.0F - bonecaAmbalabuCounter2 % 2 * 0.5F + (getAmount(bonecaAmbalabuCounter, bonecaAmbalabuCounter2 + bonecaAmbalabuTotal * 9, 41) - 0.5F) * 0.35F;
               if (!(bonecaAmbalabuLimit < -19.6F)) {
                  float planValuesItem = bonecaAmbalabuTotal * (52.0F + bonecaAmbalabuCounter2 * 7.7F + (getAmount(bonecaAmbalabuCounter, bonecaAmbalabuCounter2 + bonecaAmbalabuTotal * 9, 42) - 0.5F) * 3.0F);
                  if (!(bonecaAmbalabuLimit > -9.2F) || !(Math.abs(planValuesItem) < 74.0F)) {
                     float classDivisor = (1.2F + 0.6F * getAmount(bonecaAmbalabuCounter, bonecaAmbalabuCounter2 + bonecaAmbalabuTotal * 9, 43)) * bonecaAmbalabuFactor;
                     float bonecaAmbalabu5 = getBonecaAmbalabu(bonecaAmbalabu, planValuesItem);
                     float bonecaAmbalabuValue12 = bonecaAmbalabu.bonecaValue * CLASS.getAmount(bonecaAmbalabuLimit);
                     int foreShinValue = bonecaAmbalabu.getBonecaAmbalabuForInt((int)bonecaAmbalabu5, (int)bonecaAmbalabuValue12);
                     bonecaAmbalabu.getClass022(bonecaAmbalabu5, bonecaAmbalabuValue12, classDivisor + 0.7F * bonecaAmbalabuFactor, classDivisor + 0.7F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(3031070, 0.55F));
                     bonecaAmbalabu.getClass022(bonecaAmbalabu5, bonecaAmbalabuValue12, classDivisor, classDivisor, ForeShinUtils.getValueForIntFloat(ForeShinUtils.getValue(foreShinValue, 1.02F), 1.0F));
                     bonecaAmbalabu.getClass022(bonecaAmbalabu5 - 0.35F * classDivisor, bonecaAmbalabuValue12 - 0.35F * classDivisor, 0.45F * classDivisor, 0.45F * classDivisor, ForeShinUtils.getValueForIntFloat(9345638, 0.35F));
                  }
               }
            }
         }
      }

      for (int bonecaAmbalabuTotal2 = -1; bonecaAmbalabuTotal2 <= 1; bonecaAmbalabuTotal2 += 2) {
         float[] classValues = new float[]{
            getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuTotal2 * 122.0F),
            getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuTotal2 * 116.0F),
            getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuTotal2 * 102.0F),
            getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuTotal2 * 90.0F)
         };
         float[] floats4 = new float[]{
            bonecaAmbalabu.bonecaValue * CLASS.getAmount(-8.5F),
            bonecaAmbalabu.bonecaValue * CLASS.getAmount(-6.4F),
            bonecaAmbalabu.bonecaValue * CLASS.getAmount(-5.0F),
            bonecaAmbalabu.bonecaValue * CLASS.getAmount(-4.3F)
         };
         bonecaAmbalabu.getClassForFloatArrayFloat(classValues, floats4, 2.2F * bonecaAmbalabuFactor, ForeShinUtils.getValueForInt(2371610));
      }

      float bonecaAmbalabuLimit2 = CLASS.getAmount(-19.8F);

      for (int bonecaAmbalabuCounter3 = 0; bonecaAmbalabuCounter3 < bonecaAmbalabu.bonecaValue; bonecaAmbalabuCounter3++) {
         float bonecaAmbalabuValue13 = (bonecaAmbalabuCounter3 + 0.5F) / bonecaAmbalabu.bonecaValue;
         if (!(bonecaAmbalabuValue13 < bonecaAmbalabuLimit2)) {
            float min14 = Math.min(1.0F, (bonecaAmbalabuValue13 - bonecaAmbalabuLimit2) / (1.0F - bonecaAmbalabuLimit2) * 1.6F);

            for (int bonecaAmbalabuCounter4 = 0; bonecaAmbalabuCounter4 < bonecaAmbalabu.ambalabuBonecaValue; bonecaAmbalabuCounter4++) {
               if (Math.abs(getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuCounter4)) < 116.0F) {
                  bonecaAmbalabu.getBonecaAmbalabu(
                     bonecaAmbalabuCounter4, bonecaAmbalabuCounter3, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(bonecaAmbalabuCounter4, bonecaAmbalabuCounter3), ForeShinUtils.getValueForInt(5005366), 0.85F * min14)
                  );
               }
            }
         }
      }

      float bonecaAmbalabuDivisor = CLASS.getAmount(-5.2F);

      for (int bonecaAmbalabuCounter5 = 0; bonecaAmbalabuCounter5 < bonecaAmbalabu.bonecaValue; bonecaAmbalabuCounter5++) {
         float bonecaAmbalabuLimit3 = (bonecaAmbalabuCounter5 + 0.5F) / bonecaAmbalabu.bonecaValue;
         if (bonecaAmbalabuLimit3 > bonecaAmbalabuDivisor) {
            break;
         }

         float foreShinFactor = (0.15F + 0.1F * (1.0F - bonecaAmbalabuLimit3 / bonecaAmbalabuDivisor)) * (1.0F - getAmount((bonecaAmbalabuLimit3 - 0.7F * bonecaAmbalabuDivisor) / (0.3F * bonecaAmbalabuDivisor)));

         for (int bonecaAmbalabuCounter6 = 0; bonecaAmbalabuCounter6 < bonecaAmbalabu.ambalabuBonecaValue; bonecaAmbalabuCounter6++) {
            if (Math.abs(getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuCounter6)) < 116.0F) {
               bonecaAmbalabu.getBonecaAmbalabu(bonecaAmbalabuCounter6, bonecaAmbalabuCounter5, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(bonecaAmbalabuCounter6, bonecaAmbalabuCounter5), ForeShinUtils.getValueForInt(7238998), foreShinFactor));
            }
         }
      }

      for (int bonecaAmbalabuCounter7 = 0; bonecaAmbalabuCounter7 < bonecaAmbalabu.bonecaValue; bonecaAmbalabuCounter7++) {
         for (int bonecaAmbalabuCounter8 = 0; bonecaAmbalabuCounter8 < bonecaAmbalabu.ambalabuBonecaValue; bonecaAmbalabuCounter8++) {
            float abs2 = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuCounter8));
            if (abs2 > 134.0F) {
               bonecaAmbalabu.getBonecaAmbalabu(
                  bonecaAmbalabuCounter8, bonecaAmbalabuCounter7, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12083306), ForeShinUtils.getValueForInt(8008768), (abs2 - 134.0F) / 46.0F)
               );
            } else if (abs2 > 116.0F) {
               bonecaAmbalabu.getBonecaAmbalabu(bonecaAmbalabuCounter8, bonecaAmbalabuCounter7, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(bonecaAmbalabuCounter8, bonecaAmbalabuCounter7), ForeShinUtils.getValueForInt(13617298), 0.55F));
            }
         }
      }

      float bonecaAmbalabuValue16 = CLASS.getAmount(-7.0F);
      float bonecaAmbalabuValue17 = CLASS.getAmount(-12.0F);

      for (int bonecaAmbalabuCounter9 = 0; bonecaAmbalabuCounter9 < bonecaAmbalabu.bonecaValue; bonecaAmbalabuCounter9++) {
         float bonecaAmbalabuValue18 = 1.0F - getAmount(((bonecaAmbalabuCounter9 + 0.5F) / bonecaAmbalabu.bonecaValue - bonecaAmbalabuValue16) / (bonecaAmbalabuValue17 - bonecaAmbalabuValue16));
         if (!(bonecaAmbalabuValue18 <= 0.0F)) {
            for (int bonecaAmbalabuCounter10 = 0; bonecaAmbalabuCounter10 < bonecaAmbalabu.ambalabuBonecaValue; bonecaAmbalabuCounter10++) {
               if (Math.abs(getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuCounter10)) > 136.0F) {
                  bonecaAmbalabu.getBonecaAmbalabu(bonecaAmbalabuCounter10, bonecaAmbalabuCounter9, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(bonecaAmbalabuCounter10, bonecaAmbalabuCounter9), ForeShinUtils.getValueForInt(5119530), 0.8F * bonecaAmbalabuValue18));
               }
            }
         }
      }

      for (int bonecaAmbalabuTotal3 = -1; bonecaAmbalabuTotal3 <= 1; bonecaAmbalabuTotal3 += 2) {
         bonecaAmbalabu.getClassBonecaAmbalabu(
            getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuTotal3 * 132.0F) - 0.7F * bonecaAmbalabuFactor,
            0.0F,
            getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuTotal3 * 132.0F) + 0.7F * bonecaAmbalabuFactor,
            bonecaAmbalabu.bonecaValue,
            ForeShinUtils.getValueForIntFloat(3809832, 0.7F)
         );
      }
   }

   private static void handleBonecaAmbalabu9(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float absBonecaAmbalabu = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, j));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, absBonecaAmbalabu < 22.0F ? ForeShinUtils.getValueForInt(2895399) : ForeShinUtils.getValueForInt(6975826));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5725507), 0.4F, 5.0F, 81).getBonecaAmbalabu(0.05F, 2.0F, 82);

      for (int k = -1; k <= 1; k += 2) {
         float bonecaAmbalabu2 = getBonecaAmbalabu(bonecaAmbalabu, k * 118.0F);
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu2 - 0.5F, 0.0F, bonecaAmbalabu2 + 0.5F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(5001792, 0.9F));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.35F - 0.5F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.35F + 0.5F, ForeShinUtils.getValueForIntFloat(5001792, 0.7F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.68F - 0.5F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.68F + 0.5F, ForeShinUtils.getValueForIntFloat(5001792, 0.7F));
   }

   private static void handleBonecaAmbalabu10(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5794111), ForeShinUtils.getValueForInt(3951916))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(2766880), 0.4F, 4.0F, 131)
         .getBonecaAmbalabu(0.08F, 3.0F, 132);

      for (int i = 0; i < 16; i++) {
         float bonecaAmbalabuValue = getAmount(i, 1, 133) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = bonecaAmbalabu.bonecaValue * (0.12F + 0.62F * getAmount(i, 2, 133));
         float classDivisor = 2.2F + 1.6F * getAmount(i, 3, 133);
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, classDivisor + 0.9F, classDivisor + 0.9F, ForeShinUtils.getValueForIntFloat(2240536, 0.45F));
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, classDivisor, classDivisor, ForeShinUtils.getValueForIntFloat(4873269, 0.95F));
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue - 0.4F * classDivisor, amountForFloat - 0.4F * classDivisor, 0.45F * classDivisor, 0.45F * classDivisor, ForeShinUtils.getValueForIntFloat(8030806, 0.45F));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.8F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(1581072, 0.45F));
   }

   private static void handle(LatheLoft latheLoft, float lidBombardiroValue, float armTrunkValue, float lidBombardiroValue3, float lidBombardiroValue4, ShinBrr shinBrr) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, armTrunkValue, lidBombardiroValue).getLatheLoft4(90.0F);
      latheLoft.getPolygonExtrude(new float[]{lidBombardiroValue3 * 0.5F, 0.0F, -lidBombardiroValue3 * 0.5F, 0.0F, -lidBombardiroValue3 * 0.5F + 0.25F, lidBombardiroValue4}, -0.2F, 0.2F, shinBrr, shinBrr, shinBrr);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu11(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float absScaled = Math.abs(getBonecaAmbalabu(bonecaAmbalabu, j)) / 180.0F;
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(ForeShinUtils.getValueForInt(7238998), 1.1F - 0.22F * absScaled));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5857350), 0.4F, 5.0F, 101).getBonecaAmbalabu(0.05F, 2.0F, 102);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.8F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.9F, ForeShinUtils.getValueForInt(5001792));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.8F - 0.6F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.8F + 0.6F, ForeShinUtils.getValueForInt(3158826));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.9F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(8028511));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.9F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.9F + 0.8F, ForeShinUtils.getValueForIntFloat(3158826, 0.8F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.5F - 0.5F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.5F + 0.5F, ForeShinUtils.getValueForIntFloat(5001792, 0.8F));

      for (int k = -1; k <= 1; k += 2) {
         float bonecaAmbalabu2 = getBonecaAmbalabu(bonecaAmbalabu, k * 80.0F);
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu2 - 2.0F, bonecaAmbalabu.bonecaValue * 0.2F, bonecaAmbalabu2 + 2.0F, bonecaAmbalabu.bonecaValue * 0.8F, ForeShinUtils.getValueForIntFloat(2763813, 0.45F));
      }
   }

   private static final class Spine {
      final float[] shinFootTable;
      final float[] spineTable;
      final float[] pointsSpline;
      final int pointCount;
      final float[] table;
      final float[] cumulativeLengths;
      final float[] controlRadiiY;
      final float[] sampledRadiiY;
      final int spineValue;

      float getAmounts(float spineLimit) {
         float spineValue = 0.0F;
         float spineValue2 = this.pointCount - 1.0F;
         boolean spineFlag = this.table[3 * (this.pointCount - 1) + 2] > this.table[2];

         for (int i = 0; i < 40; i++) {
            float spineValue3 = 0.5F * (spineValue + spineValue2);
            if (this.getAmountsForFloat(spineValue3)[2] < spineLimit == spineFlag) {
               spineValue = spineValue3;
            } else {
               spineValue2 = spineValue3;
            }
         }

         return 0.5F * (spineValue + spineValue2);
      }

      float getAmount(float spineValue, float spineLimit) {
         float[] spineValues = this.getAmountsForFloat(this.getAmounts(spineLimit));
         float squaredDistanceTo = Math.max(0.0F, 1.0F - spineValue / spineValues[3] * (spineValue / spineValues[3]));
         return spineValues[1] + spineValues[4] * (float)Math.sqrt(squaredDistanceTo);
      }

      float getAmount(float spineValue) {
         return this.getAmountsPointCount(this.getAmounts(spineValue));
      }

      float[] getAmountsForFloat(float spineValue) {
         spineValue = LidBombardiro.getAmount(spineValue, 0.0F, this.pointCount - 1.0F);
         int index = Math.min(this.pointCount - 2, (int)Math.floor(spineValue));
         float spineFactor = spineValue - index;
         float spineFactor2 = spineFactor * spineFactor;
         float spineFactor3 = spineFactor2 * spineFactor;
         int max2 = Math.max(0, index - 1);
         int spineValue3 = index + 1;
         int min4 = Math.min(this.pointCount - 1, index + 2);
         float[] floats = new float[5];

         for (int i = 0; i < 3; i++) {
            float spineValue5 = this.table[3 * max2 + i];
            float spineValue6 = this.table[3 * index + i];
            float spineValue7 = this.table[3 * spineValue3 + i];
            float spineValue8 = this.table[3 * min4 + i];
            floats[i] = this.spineValue == 1
               ? spineValue6 + (spineValue7 - spineValue6) * spineFactor
               : 0.5F
                  * (
                     2.0F * spineValue6
                        + (-spineValue5 + spineValue7) * spineFactor
                        + (2.0F * spineValue5 - 5.0F * spineValue6 + 4.0F * spineValue7 - spineValue8) * spineFactor2
                        + (-spineValue5 + 3.0F * spineValue6 - 3.0F * spineValue7 + spineValue8) * spineFactor3
                  );
         }

         float spineFactor4 = this.spineValue == 1 ? spineFactor : spineFactor * spineFactor * (3.0F - 2.0F * spineFactor);
         floats[3] = this.spineTable[index] + (this.spineTable[index + 1] - this.spineTable[index]) * spineFactor4;
         floats[4] = this.controlRadiiY[index] + (this.controlRadiiY[index + 1] - this.controlRadiiY[index]) * spineFactor4;
         return floats;
      }

      float[] getAmounts(float spineValue, float degrees) {
         float[] spineValues = this.getAmountsForFloat(spineValue);
         float[] amountsForFloat = this.getAmountsForFloat(spineValue - 0.02F);
         float[] amountsForFloat3 = this.getAmountsForFloat(spineValue + 0.02F);
         float spineFactor = amountsForFloat3[0] - amountsForFloat[0];
         float spineFactor2 = amountsForFloat3[1] - amountsForFloat[1];
         float spineFactor3 = amountsForFloat3[2] - amountsForFloat[2];
         float distance = (float)Math.sqrt(spineFactor * spineFactor + spineFactor2 * spineFactor2 + spineFactor3 * spineFactor3);
         float spineFactor4 = spineFactor / distance;
         float spineFactor5 = spineFactor2 / distance;
         float spineFactor6 = spineFactor3 / distance;
         float spineFactor7 = -spineFactor5 * spineFactor4;
         float spineFactor8 = 1.0F - spineFactor5 * spineFactor5;
         float spineFactor9 = -spineFactor5 * spineFactor6;
         float sqrt2 = (float)Math.sqrt(spineFactor7 * spineFactor7 + spineFactor8 * spineFactor8 + spineFactor9 * spineFactor9);
         float spineFactor10 = spineFactor7 / sqrt2;
         float spineFactor11 = spineFactor8 / sqrt2;
         float spineFactor12 = spineFactor9 / sqrt2;
         float spineFactor13 = spineFactor5 * spineFactor12 - spineFactor6 * spineFactor11;
         float spineFactor14 = spineFactor6 * spineFactor10 - spineFactor4 * spineFactor12;
         float spineFactor15 = spineFactor4 * spineFactor11 - spineFactor5 * spineFactor10;
         float angle = (float)Math.toRadians(degrees);
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         return new float[]{
            spineValues[0] + spineFactor10 * spineValues[4] * cos + spineFactor13 * spineValues[3] * sin,
            spineValues[1] + spineFactor11 * spineValues[4] * cos + spineFactor14 * spineValues[3] * sin,
            spineValues[2] + spineFactor12 * spineValues[4] * cos + spineFactor15 * spineValues[3] * sin
         };
      }

      static LidBombardiro.Spine getClassForFloatArrayFlag(float[][] floats, boolean classFlag) {
         int classSize = floats.length;
         float[] floats2 = new float[3 * classSize];
         float[] floats3 = new float[classSize];
         float[] floats4 = new float[classSize];

         for (int i = 0; i < classSize; i++) {
            float classValue = floats[i][0];
            float classValue2 = floats[i][1];
            float classValue3 = floats[i][2];
            float classValue4 = floats[i][3];
            float classValue5 = floats[i][4];
            if (classFlag) {
               floats2[3 * i + 1] = classValue3 + 0.35F * (classValue2 - classValue3);
               floats4[i] = 0.65F * (classValue2 - classValue3);
               floats3[i] = classValue5;
            } else {
               floats2[3 * i + 1] = classValue3 - 0.35F * (classValue3 - classValue4);
               floats4[i] = 0.65F * (classValue3 - classValue4);
               floats3[i] = classValue5 * 0.82F;
            }

            floats2[3 * i + 2] = classValue;
         }

         return new LidBombardiro.Spine(1, floats2, floats3, floats4);
      }

      float getAmountsPointCount(float spineValue) {
         float hashRegistryValue = LidBombardiro.getAmount(spineValue, 0.0F, this.pointCount - 1.0F) * this.spineValue;
         int index = Math.min(this.cumulativeLengths.length - 2, (int)Math.floor(hashRegistryValue));
         return (this.cumulativeLengths[index] + (this.cumulativeLengths[index + 1] - this.cumulativeLengths[index]) * (hashRegistryValue - index)) / this.cumulativeLengths[this.cumulativeLengths.length - 1];
      }

      Spine(int value, float[] amounts, float[] latheLoftValues, float[] amounts3) {
         this.spineValue = value;
         this.table = amounts;
         this.spineTable = latheLoftValues;
         this.controlRadiiY = amounts3;
         this.pointCount = amounts.length / 3;
         this.pointsSpline = LatheLoft.getPointsSpline(value, amounts);
         int spineSize = LatheLoft.getValue(this.pointsSpline);
         this.shinFootTable = LatheLoft.getAmounts(spineSize, latheLoftValues);
         this.sampledRadiiY = LatheLoft.getAmounts(spineSize, amounts3);
         this.cumulativeLengths = new float[spineSize];

         for (int i = 1; i < spineSize; i++) {
            float spineFactor = this.pointsSpline[3 * i] - this.pointsSpline[3 * i - 3];
            float spineFactor2 = this.pointsSpline[3 * i + 1] - this.pointsSpline[3 * i - 2];
            float spineFactor3 = this.pointsSpline[3 * i + 2] - this.pointsSpline[3 * i - 1];
            this.cumulativeLengths[i] = this.cumulativeLengths[i - 1] + (float)Math.sqrt(spineFactor * spineFactor + spineFactor2 * spineFactor2 + spineFactor3 * spineFactor3);
         }
      }
   }
}
