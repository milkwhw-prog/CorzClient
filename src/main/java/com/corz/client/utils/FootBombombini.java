package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class FootBombombini extends WarmBrainrotBase {
   private ForeEar classShinFoot;
   private ForeEar classShinProgress;
   private static final float[] footBombombiniTable = new float[]{-12.3F, -12.0F, -11.4F, -10.5F, -9.0F, -6.6F, -3.0F, 2.0F, 7.0F, 11.8F, 15.8F, 19.0F, 21.2F, 22.3F};
   private ForeEar footL;
   private static final float[] table = new float[]{
      10.4F, 10.45F, 10.6F, 10.8F, 11.0F, 11.15F, 11.3F, 11.4F, 11.45F, 11.55F, 11.95F, 12.55F, 13.25F, 13.75F
   };
   private static final float[] BODY_PROFILE_RADII = new float[]{0.0F, 1.5F, 2.45F, 3.1F, 3.15F, 3.38F, 3.45F, 3.4F, 3.15F, 2.85F, 2.1F, 1.35F, 0.7F, 0.0F};
   private static final float[] bonecaTable = new float[]{-12.3F, -11.0F, -9.9F, -5.0F, -1.0F, 8.2F, 12.4F, 16.0F, 19.0F, 21.2F, 22.3F};
   private static final float[] BODY_PROFILE_SAMPLES = getAmountsFootBombombiniTable();
   private static final float[] shinFootTable = new float[]{8.0F, 14.6F};
   private static final float[][] footAmounts = new float[][]{{-10.2F, 62.0F}, {-7.4F, 62.0F}};
   private static final float[] GRENADE_OFFSETS_X = new float[]{-1.65F, 0.0F, 1.65F};
   private final ForeEar[] shinFootForeEars = new ForeEar[4];
   private ForeEar shinFootForeEar;
   private static final float GRENADE_BASE_Y = getAmount(-12.0F) - 2.0F;
   private static final float[] CAP_RADII_X = new float[]{1.12F, 0.82F, 0.43F, 0.29F, 0.1F};
   private ForeEar footR;
   private static final float[] CAP_RADII_Z = new float[]{1.38F, 0.92F, 0.44F, 0.31F, 0.115F};
   private ForeEar neck;
   private static final float eyeAmount = getAmountForFloat(0.68F) + 0.62F;
   private final ForeEar[] grenadeSections = new ForeEar[7];
   private static final float shinFootAmount = getAmount9(0.68F) * (float)Math.sqrt(1.0F - getAmount8(0.62F / getAmount6(0.68F))) - 0.16F;
   private ForeEar shinProgressForeEar;
   private static final float[] pointsSpline = LatheLoft.getPointsSpline(
      2, 0.0F, 8.200001F, -10.2F, 0.0F, 11.0F, -13.6F, 0.0F, 15.6F, -15.0F, 0.0F, 20.4F, -15.4F, 0.0F, 23.2F, -17.4F, 0.0F, 24.2F, -19.6F
   );
   private ForeEar eyeL;
   private ForeEar footBombombiniForeEar;
   private static final float[] BAND_POINTS_A = new float[]{
      14.6F, 13.2F, 16.3F, 14.6F, 17.6F, 16.2F, 18.7F, 17.0F, 19.6F, 17.3F, 20.4F, 17.25F, 21.0F, 16.8F, 21.35F, 15.6F, 21.3F, 13.6F
   };
   private ForeEar hull;
   private ForeEar head;
   private static final float[] BAND_POINTS_B = new float[]{
      18.3F, 12.3F, 19.2F, 14.6F, 20.0F, 16.5F, 20.8F, 17.2F, 21.5F, 17.2F, 22.0F, 16.4F, 21.95F, 13.2F, 21.6F, 12.0F, 20.8F, 11.55F, 19.4F, 11.75F
   };

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(0);
      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.5F, bonecaValue * 0.36F, ambalabuBonecaValue * 0.48F, bonecaValue * 0.35F, ForeShinUtils.getValueForInt(1973790));
      bonecaAmbalabu.getClassBonecaAmbalabu(ambalabuBonecaValue * 0.32F, bonecaValue * 0.4F, ambalabuBonecaValue * 0.68F, bonecaValue, ForeShinUtils.getValueForInt(1973790));
      int bonecaAmbalabuLimit = Math.round(bonecaValue * 0.17F);

      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         for (int j = 0; j < ambalabuBonecaValue; j++) {
            int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(j, i);
            if (ForeShinUtils.getValue6(bonecaAmbalabuForInt) > 0) {
               bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuForInt & 0xFF000000 | ForeShinUtils.getValueForInt(15254320) & 16777215);
            }
         }
      }
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(0);
      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.5F, bonecaValue * 0.5F, ambalabuBonecaValue * 0.42F, bonecaValue * 0.42F, ambalabuBonecaValue * 0.16F, ForeShinUtils.getValueForInt(12633288));
   }

   @Override
   public int getValue() {
      return 2;
   }

   @Override
   public String formatNoteBlock() {
      return "block.note_block.didgeridoo";
   }

   private static void handle(LatheLoft latheLoft, float footBombombiniValue, float eyeValue, float dinForeValue, float degrees, float footBombombiniFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float pinOffset = eyeValue + 1.02F;
      latheLoft.getLatheLoft(footBombombiniValue, eyeValue, dinForeValue, 0.82F, 1.02F, 0.82F, 8, shinBrr);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(footBombombiniValue, 0.0F, dinForeValue);
      latheLoft.getLatheLoft(new float[]{0.27F, 0.19F}, new float[]{pinOffset - 0.1F, pinOffset + 0.3F}, 6, shinBrr2, null, shinBrr2);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(footBombombiniValue, pinOffset + 0.22F, dinForeValue).getLatheLoft4(degrees).getLatheLoft(0.26F, 0.0F, 0.0F).getLatheLoft(24.0F);
      latheLoft.getLatheLoft(-0.05F, -0.85F, -0.06F, 0.05F, 0.0F, 0.06F, shinBrr2, shinBrr2, shinBrr2, null, null, null);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(footBombombiniValue, pinOffset + 0.12F, dinForeValue).getLatheLoft4(degrees).getLatheLoft(0.0F, 0.0F, -0.44F * footBombombiniFactor).getLatheLoft4(90.0F);
      latheLoft.getLatheLoft(-0.24F, -0.24F, -0.01F, 0.24F, 0.24F, 0.01F, shinBrr3, shinBrr3, null, null, null, null);
      latheLoft.getDequeArray();
   }

   private static float getAmount(float footBombombiniValue) {
      return getAmounts(footBombombiniValue, 1);
   }

   private static float[] getAmounts(float footBombombiniValue, float degrees, float footBombombiniFactor) {
      float amount2 = getAmount2(footBombombiniValue, degrees);
      double angle = Math.toRadians(degrees);
      float footBombombiniBound = amount2 * (float)Math.sin(angle);
      float footBombombiniValue2 = getAmount(footBombombiniValue) + amount2 * (float)Math.cos(angle);
      float max3 = Math.max(footBombombiniValue2 - 1.25F, 5.52F);
      float footBombombiniFactor3 = Math.max(footBombombiniBound, getAmount(footBombombiniValue, max3) + 0.82F - 0.1F);
      return new float[]{footBombombiniFactor * footBombombiniBound, footBombombiniValue2, footBombombiniFactor * footBombombiniFactor3, max3};
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16777215)).getBonecaAmbalabu(0.012F, 4.0F, 77);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.8F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(9077882));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.72F, ambalabuBonecaValue, bonecaValue * 0.8F, ForeShinUtils.getValueForIntFloat(9077882, 0.45F));
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.hull = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 3.0F, -1.0F);
      this.neck = latheLoft.getClassForStringForeEar("hull", this.hull, 0.0F, 9.4F, -1.0F);
      this.head = latheLoft.getClassForStringForeEar("neck", this.neck, 0.0F, 10.0F, -12.4F);
      this.eyeL = latheLoft.getClassForStringForeEar("head", this.head, 0.0F, 24.0F, -19.6F);
      this.footBombombiniForeEar = latheLoft.getClassForStringForeEar("jaw", this.eyeL, 0.0F, 23.658829F, -23.485998F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("eye_r", this.eyeL, shinFootAmount, eyeAmount, -22.311998F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("eye_l", this.eyeL, -shinFootAmount, eyeAmount, -22.311998F);

      for (int i = 0; i < 4; i++) {
         float shinFootFactor = i < 2 ? 1.0F : -1.0F;
         float shinFootValue = shinFootFactor * shinFootTable[i % 2];
         float footBombombiniValue = getAmount7(shinFootTable[i % 2]);
         this.shinFootForeEars[i] = latheLoft.getClassForStringForeEar("prop" + i, this.neck, shinFootValue, getAmount3(footBombombiniValue) - 0.3F, getAmount10(footBombombiniValue) - 2.5F);
      }

      this.footR = latheLoft.getClassForStringForeEar("leg_r", this.hull, 2.1F, 6.2999997F, -1.0F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("foot_r", this.footR, 2.1999998F, 0.95F, -1.1F);
      this.footL = latheLoft.getClassForStringForeEar("leg_l", this.hull, -2.1F, 6.2999997F, -1.0F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -2.1999998F, 0.95F, -1.1F);

      for (int j = 0; j < 4; j++) {
         float footBombombiniFactor = (j & 1) == 0 ? 1.0F : -1.0F;
         float[] shinFootValues = getAmounts(footAmounts[j / 2][0], footAmounts[j / 2][1], footBombombiniFactor);
         this.grenadeSections[j] = latheLoft.getClassForStringForeEar("grenade" + j, this.neck, shinFootValues[0], shinFootValues[1], footAmounts[j / 2][0]);
      }

      for (int k = 0; k < 3; k++) {
         this.grenadeSections[4 + k] = latheLoft.getClassForStringForeEar("grenade" + (4 + k), this.neck, GRENADE_OFFSETS_X[k], GRENADE_BASE_Y + 1.02F + 0.35F, -12.09F);
      }

      latheLoft.getLatheLoft(this.footR);
      LoftLatheFlag.Region region = latheLoft.getMax(192, 288);
      handleAmountsBoneca(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(64, 160);
      handleBonecaAmbalabu6(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(64, 128);
      handleBonecaAmbalabu4(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(224, 128);
      handleBonecaAmbalabu11(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(160, 48);
      handleBonecaAmbalabu8(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(48, 48);
      handleBonecaAmbalabu5(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(64, 48);
      handleBonecaAmbalabu13(max7.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max8 = latheLoft.getMax(48, 32);
      handleBonecaAmbalabu7(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(16, 48);
      handleBonecaAmbalabu(max9.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max10 = latheLoft.getMax(48, 48);
      handle(max10.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max11 = latheLoft.getMax(32, 32);
      handleBonecaAmbalabu9(max11.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max12 = latheLoft.getMax(40, 48);
      handleBonecaAmbalabu(max12.getClassBonecaAmbalabu(), false);
      LoftLatheFlag.Region max13 = latheLoft.getMax(40, 48);
      handleBonecaAmbalabu(max13.getClassBonecaAmbalabu(), true);
      LoftLatheFlag.Region max14 = latheLoft.getMax(64, 16);
      handleBonecaAmbalabu14(max14.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max15 = latheLoft.getMax(48, 48);
      handleBonecaAmbalabu12(max15.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max16 = latheLoft.getMax(48, 32);
      handleBonecaAmbalabu10(max16.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max17 = latheLoft.getMax(16, 16);
      handleBonecaAmbalabu3(max17.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max18 = latheLoft.getMax(16, 16);
      handleBonecaAmbalabuForBonecaAmbalabu(max18.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max6);
      ShinBrr shinBrr = latheLoft.getMax(3027511);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max8);
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max9);
      ShinBrr classForRegion11 = latheLoft.getClassForRegion(max10);
      ShinBrr classForRegion12 = latheLoft.getClassForRegion(max11);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max12);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max13);
      ShinBrr classForRegion15 = latheLoft.getClassForRegion(max14);
      ShinBrr classForRegion16 = latheLoft.getClassForRegion(max15);
      ShinBrr classForRegion17 = latheLoft.getClassForRegion(max16);
      ShinBrr shinBrr2 = latheLoft.getMax(12107461);
      ShinBrr shinBrr3 = latheLoft.getMax(7238470);
      ShinBrr shinBrr4 = latheLoft.getMax(8752017);
      ShinBrr shinBrr5 = latheLoft.getMax(4145704);
      ShinBrr classForRegion18 = latheLoft.getClassForRegion(max18);
      ShinBrr shinBrr6 = latheLoft.getMax(15764004);
      ShinBrr shinBrr7 = latheLoft.getMax(4869973);
      ShinBrr shinBrr8 = latheLoft.getMax(2829874);
      ShinBrr shinBrr9 = latheLoft.getMax(15763754);
      ShinBrr shinBrr10 = latheLoft.getMax(15040034);
      ShinBrr shinBrr11 = latheLoft.getMax(15909002);
      ShinBrr classForRegion19 = latheLoft.getClassForRegion(max17);
      ShinBrr shinBrr12 = latheLoft.getMax(1774608);
      ShinBrr shinBrr13 = latheLoft.getMax(16777215);
      ShinBrr shinBrr14 = latheLoft.getMax(14977674);
      ShinBrr shinBrr15 = latheLoft.getMax(15918792);
      latheLoft.getLatheLoftForForeEar(this.hull);

      for (int m = -1; m <= 1; m += 2) {
         latheLoft.getLatheLoft(m * 2.1F, 5.85F, -1.0F, 1.0F, 0.72F, 1.35F, 8, shinBrr2);
      }

      latheLoft.getLatheLoftForForeEar(this.neck);
      handleAmounts(latheLoft, classForRegion);
      handle(latheLoft, classForRegion6, shinBrr3, classForRegion17);

      for (int p = -1; p <= 1; p += 2) {
         handle(latheLoft, p, classForRegion3);

         for (float shinFootTableItem : shinFootTable) {
            handle(latheLoft, p * shinFootTableItem, classForRegion7, shinBrr);
         }

         handle(latheLoft, p, shinBrr7, shinBrr8, shinBrr2);
         handle3(latheLoft, p, classForRegion16);
         handle(latheLoft, p * 7.6F, BAND_POINTS_B, classForRegion13, classForRegion14, shinBrr2);
      }

      handle(latheLoft, 0.0F, BAND_POINTS_A, classForRegion13, classForRegion14, shinBrr2);

      for (int q = 0; q < 4; q++) {
         latheLoft.getLatheLoftForForeEar(this.shinFootForeEars[q]);
         handleAmount(latheLoft, this.shinFootForeEars[q], shinBrr6, classForRegion10);
      }

      latheLoft.getLatheLoftForForeEar(this.head);
      handleAmountsPointsSpline(latheLoft, classForRegion4);
      handleAmountsForLatheLoftShinBrr(latheLoft, classForRegion15);
      latheLoft.getLatheLoftForForeEar(this.eyeL);
      handleAmounts(latheLoft, classForRegion5, classForRegion8, shinBrr11, shinBrr15);
      latheLoft.getLatheLoftForForeEar(this.footBombombiniForeEar);
      handle(latheLoft, classForRegion9, shinBrr14);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handleEyeAmount(latheLoft, 1.0F, shinBrr12, shinBrr13, classForRegion19);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      handleEyeAmount(latheLoft, -1.0F, shinBrr12, shinBrr13, classForRegion19);
      latheLoft.getLatheLoftForForeEar(this.footR);
      handleForLatheLoftFloat(latheLoft, 1.0F, shinBrr9);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      handleForLatheLoftFloatShinBrr(latheLoft, 1.0F, classForRegion11, shinBrr10, shinBrr9);
      latheLoft.getLatheLoftForForeEar(this.footL);
      handleForLatheLoftFloat(latheLoft, -1.0F, shinBrr9);
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      handleForLatheLoftFloatShinBrr(latheLoft, -1.0F, classForRegion11, shinBrr10, shinBrr9);

      for (int r = 0; r < 4; r++) {
         latheLoft.getLatheLoftForForeEar(this.grenadeSections[r]);
         float shinFootLimit = (r & 1) == 0 ? 1.0F : -1.0F;
         float[] amounts2 = getAmounts(footAmounts[r / 2][0], footAmounts[r / 2][1], shinFootLimit);
         handle(latheLoft, amounts2[2], amounts2[3], footAmounts[r / 2][0], shinFootLimit > 0.0F ? 0.0F : 180.0F, shinFootLimit, classForRegion12, shinBrr4, classForRegion18);
         handle(latheLoft, amounts2[2], amounts2[3] + 1.02F + 0.22F, amounts2[0], amounts2[1] + 0.1F, footAmounts[r / 2][0], shinBrr5);
      }

      for (int t = 0; t < 3; t++) {
         latheLoft.getLatheLoftForForeEar(this.grenadeSections[4 + t]);
         handle(latheLoft, GRENADE_OFFSETS_X[t], GRENADE_BASE_Y, -12.34F, 90.0F, 1.0F, classForRegion12, shinBrr4, classForRegion18);
         handle(latheLoft, GRENADE_OFFSETS_X[t], GRENADE_BASE_Y + 1.02F + 0.22F, GRENADE_OFFSETS_X[t], GRENADE_BASE_Y + 1.02F + 0.95F, -12.34F, shinBrr5);
      }
   }

   private static float getAmountForFloat(float footBombombiniFactor) {
      return 24.7F - 1.1800001F * (0.95F * footBombombiniFactor * footBombombiniFactor + 0.45F * (1.0F - footBombombiniFactor) * (1.0F - footBombombiniFactor));
   }

   private static void handle(LatheLoft latheLoft, float footBombombiniValue, float footBombombiniValue2, float footBombombiniValue3, float footBombombiniValue4, float armTrunkValue, ShinBrr shinBrr) {
      float deltaX = footBombombiniValue3 - footBombombiniValue;
      float x = footBombombiniValue4 - footBombombiniValue2;
      float hypot = (float)Math.hypot(deltaX, x);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(footBombombiniValue, footBombombiniValue2, armTrunkValue).getLatheLoft((float)Math.toDegrees(Math.atan2(-deltaX, x)));
      latheLoft.getLatheLoft(-0.13F, -0.05F, -0.13F, 0.13F, hypot, 0.13F, shinBrr, shinBrr, shinBrr, shinBrr, null, null);
      latheLoft.getDequeArray();
   }

   private static float getAmount3(float footBombombiniValue) {
      return 7.6F + 1.6F * footBombombiniValue;
   }

   @Override
   public String formatTungDin() {
      return "bombombini_gusini";
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      float bonecaAmbalabuValue = getAmounts(getAmounts()) * bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16777215)).getBonecaAmbalabu(0.015F, 6.0F, 31);

      for (int i = 0; i < 12; i++) {
         float bonecaAmbalabuFactor = (i + 0.5F) / 12.0F;
         float classFactor = bonecaAmbalabuFactor * ambalabuBonecaValue;
         float bonecaAmbalabuLimit = 0.08F + 0.2F * Math.abs(bonecaAmbalabuFactor - 0.5F);
         bonecaAmbalabu.getClassForFloatInt(classFactor, bonecaAmbalabuValue + 5.0F, classFactor + 1.5F, bonecaValue * 0.55F, classFactor - 0.8F, bonecaValue * 0.97F, 1.5F, ForeShinUtils.getValueForIntFloat(14473423, bonecaAmbalabuLimit));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue, ambalabuBonecaValue, bonecaAmbalabuValue + 4.0F, ForeShinUtils.getValueForIntFloat(14473423, 0.6F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaAmbalabuValue, ForeShinUtils.getValueForInt(7238470));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue - 2.5F, ambalabuBonecaValue, bonecaAmbalabuValue, ForeShinUtils.getValueForInt(4145704));

      for (float bonecaAmbalabuTotal = bonecaAmbalabuValue * 0.3F; bonecaAmbalabuTotal < bonecaAmbalabuValue - 4.0F; bonecaAmbalabuTotal += 4.5F) {
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuTotal, ambalabuBonecaValue, bonecaAmbalabuTotal + 1.6F, ForeShinUtils.getValueForInt(4145704));

         for (float classBound = 2.0F; classBound < ambalabuBonecaValue; classBound += 5.0F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(classBound, bonecaAmbalabuTotal + 0.3F, classBound + 1.1F, bonecaAmbalabuTotal + 1.3F, ForeShinUtils.getValueForIntFloat(8028240, 0.8F));
         }
      }
   }

   private static void handle(LatheLoft latheLoft, float footBombombiniFactor, ShinBrr shinBrr) {
      float[] floats = new float[]{2.6F, 21.3F, 22.9F, 23.65F};
      int footBombombiniSize = floats.length;
      float[] loftPathValues = new float[3 * footBombombiniSize];
      float[] floats3 = new float[footBombombiniSize];
      float[] crownForeValues = new float[footBombombiniSize];

      for (int i = 0; i < footBombombiniSize; i++) {
         float footBombombiniLimit = floats[i];
         float footBombombiniValue = getAmount7(footBombombiniLimit);
         float amount10 = getAmount10(footBombombiniValue);
         float amount4 = getAmount4(footBombombiniValue);
         float footBombombiniFactor2 = footBombombiniLimit > 21.3F ? (float)Math.sqrt(Math.max(0.0F, 1.0F - getAmount8((footBombombiniLimit - 21.3F) / 2.35F))) : 1.0F;
         loftPathValues[3 * i] = footBombombiniFactor * footBombombiniLimit;
         loftPathValues[3 * i + 1] = getAmount3(footBombombiniValue);
         loftPathValues[3 * i + 2] = 0.5F * (amount10 + amount4);
         floats3[i] = Math.max(0.02F, 0.5F * (amount4 - amount10) * footBombombiniFactor2);
         crownForeValues[i] = Math.max(0.02F, (0.55F - 0.3F * footBombombiniValue) * (0.4F + 0.6F * footBombombiniFactor2));
      }

      latheLoft.getLoftPath(loftPathValues, floats3, crownForeValues, 8, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(7568002)).getBonecaAmbalabu(0.04F, 6.0F, 25);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue * 0.08F, ForeShinUtils.getValueForInt(9212827));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.08F, ambalabuBonecaValue, bonecaValue * 0.11F, ForeShinUtils.getValueForInt(5133403));

      for (float bonecaAmbalabuFactor : new float[]{0.34F, 0.62F}) {
         bonecaAmbalabu.getClass02(0.0F, bonecaAmbalabuFactor * bonecaValue, (float)ambalabuBonecaValue, bonecaAmbalabuFactor * bonecaValue, 1.1F, ForeShinUtils.getValueForInt(5593954));
      }

      for (float bonecaAmbalabuTotal = 3.0F; bonecaAmbalabuTotal < ambalabuBonecaValue; bonecaAmbalabuTotal += 8.0F) {
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuTotal, bonecaValue * 0.66F, bonecaAmbalabuTotal + 2.2F, bonecaValue * 0.8F, ForeShinUtils.getValueForIntFloat(5264989, 0.8F));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.86F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(4869973));
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / ambalabuBonecaValue;
         float cos = (float)Math.cos((bonecaAmbalabuValue - 0.5F) * (float) (Math.PI * 2));
         int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(10397101), ForeShinUtils.getValueForInt(11778496), 0.5F + 0.5F * cos);

         for (int j = 0; j < bonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, value2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.025F, 8.0F, 21);
      float hashHuntBound = bonecaValue * 0.87F;

      for (float bonecaAmbalabuFactor : new float[]{0.08F, 0.32F, 0.42F, 0.58F, 0.68F, 0.92F}) {
         bonecaAmbalabu.getClass02(bonecaAmbalabuFactor * ambalabuBonecaValue, 0.0F, bonecaAmbalabuFactor * ambalabuBonecaValue, hashHuntBound, 0.9F, ForeShinUtils.getValueForIntFloat(9212827, 0.6F));
      }

      for (float bonecaAmbalabuFactor2 : new float[]{0.2F, 0.42F, 0.64F}) {
         bonecaAmbalabu.getClass02(0.0F, bonecaAmbalabuFactor2 * bonecaValue, (float)ambalabuBonecaValue, bonecaAmbalabuFactor2 * bonecaValue, 0.9F, ForeShinUtils.getValueForIntFloat(9212827, 0.7F));

         for (float bonecaAmbalabuTotal = 2.0F; bonecaAmbalabuTotal < ambalabuBonecaValue; bonecaAmbalabuTotal += 5.0F) {
            bonecaAmbalabu.getClass022(bonecaAmbalabuTotal, bonecaAmbalabuFactor2 * bonecaValue + 2.0F, 0.5F, 0.5F, ForeShinUtils.getValueForIntFloat(8225932, 0.6F));
         }
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, hashHuntBound, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(15764004));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, hashHuntBound, ambalabuBonecaValue, hashHuntBound + 1.5F, ForeShinUtils.getValueForIntFloat(12082706, 0.8F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, hashHuntBound + 1.5F, ambalabuBonecaValue, hashHuntBound + 3.0F, ForeShinUtils.getValueForIntFloat(16761466, 0.4F));
   }

   private static float getAmount(float footBombombiniValue, float footBombombiniValue2) {
      float footBombombiniFactor = getAmount11(footBombombiniValue) + 0.3F;
      float footBombombiniDivisor = getAmount11(footBombombiniValue) * 1.03F + 0.3F;
      float footBombombiniFactor2 = (footBombombiniValue2 - getAmount(footBombombiniValue)) / footBombombiniDivisor;
      return footBombombiniFactor2 * footBombombiniFactor2 >= 1.0F ? 0.0F : footBombombiniFactor * (float)Math.sqrt(1.0F - footBombombiniFactor2 * footBombombiniFactor2);
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16097354));
      bonecaAmbalabu.getClassBonecaAmbalabu(ambalabuBonecaValue * 0.38F, 0.0F, ambalabuBonecaValue * 0.62F, bonecaValue * 0.82F, ForeShinUtils.getValueForInt(12083278));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.85F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(15909002));
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float footBombombiniValue, ShinBrr shinBrr) {
      float footBombombiniValue2 = footBombombiniValue * 2.1F;
      latheLoft.getLatheLoft(
         new float[]{footBombombiniValue2, 5.9999995F, -1.0F, footBombombiniValue2 + footBombombiniValue * 0.05F, 3.6249998F, -0.95F, footBombombiniValue2 + footBombombiniValue * 0.1F, 0.95F, -1.1F},
         new float[]{0.78F, 0.64F, 0.55F},
         8,
         shinBrr,
         false
      );
   }

   private static void handleEyeAmount(LatheLoft latheLoft, float eyeFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(eyeFactor * shinFootAmount, eyeAmount, -22.311998F).getLatheLoft4(eyeFactor * -60.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.46F, 0.3F, 0.46F, 10, 0.0F, 0.5F, shinBrr);
      latheLoft.getDequeArray();
      latheLoft.getLoftLathe(-eyeFactor * 0.12F, -0.13F, -0.3F, 0.15F, 0.15F, 6, shinBrr2);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(eyeFactor * 30.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.54F, 0.54F, 0.39F, 10, 0.0F, 0.42F, shinBrr3);
      latheLoft.getDequeArray();
      latheLoft.getDequeArray();
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float[] floats = new float[]{-11.5F, -11.2F, -5.2000003F, -4.9F};
      float[] floats2 = new float[]{0.08F, 0.3F, 0.3F, 0.08F};
      int footBombombiniSize = floats.length;
      float[] loftPathValues = new float[3 * footBombombiniSize];
      float[] floats4 = new float[footBombombiniSize];
      float[] floats5 = new float[footBombombiniSize];

      for (int i = 0; i < footBombombiniSize; i++) {
         loftPathValues[3 * i + 1] = getAmount(floats[i]);
         loftPathValues[3 * i + 2] = floats[i];
         floats4[i] = getAmount11(floats[i]) + floats2[i];
         floats5[i] = getAmount11(floats[i]) * 1.03F + floats2[i];
      }

      latheLoft.getLoftPath(loftPathValues, floats4, floats5, 16, shinBrr, null, null, 0.0F, 1.0F, 0.0F);

      for (float footBombombiniValue : new float[]{-9.6F, -6.4F}) {
         float[] floats6 = new float[15];
         float[] floats7 = new float[5];
         float[] crownForeValues = new float[5];

         for (int j = 0; j < 5; j++) {
            double angle = Math.toRadians(-58.0F + 29.0F * j);
            floats6[3 * j] = (getAmount11(footBombombiniValue) + 0.12F) * (float)Math.sin(angle);
            floats6[3 * j + 1] = getAmount(footBombombiniValue) + (getAmount11(footBombombiniValue) * 1.03F + 0.12F) * (float)Math.cos(angle);
            floats6[3 * j + 2] = footBombombiniValue;
            floats7[j] = 0.1F;
            crownForeValues[j] = 0.5F;
         }

         latheLoft.getLoftPath(floats6, floats7, crownForeValues, 6, shinBrr2, null, null, 0.0F, 0.0F, 1.0F);
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, getAmount(-11.9F) - 0.3F, -12.15F).getLatheLoftLatheLoftAmountLoft(-38.0F);
      latheLoft.getLatheLoft(-2.3F, -1.6F, -0.3F, 2.3F, 1.6F, 0.3F, shinBrr3, null, shinBrr2, shinBrr2, shinBrr2, shinBrr2);
      latheLoft.getDequeArray();
   }

   private static float getAmount4(float footBombombiniValue) {
      return 1.4F - 1.6F * footBombombiniValue;
   }

   private static void handleBonecaAmbalabu8(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(7238470)).getBonecaAmbalabu(0.03F, 10.0F, 71);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue * 0.08F, ForeShinUtils.getValueForInt(4145704));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.92F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(4145704));

      for (float bonecaAmbalabuFactor : new float[]{72.0F, 96.0F, 120.0F, 144.0F, 168.0F}) {
         for (int i = -1; i <= 1; i += 2) {
            float bonecaAmbalabuValue = (0.5F + i * bonecaAmbalabuFactor / 360.0F) * ambalabuBonecaValue;
            bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuValue - 1.3F, bonecaValue * 0.08F, bonecaAmbalabuValue + 1.3F, bonecaValue * 0.92F, ForeShinUtils.getValueForInt(4145704));

            for (float bonecaAmbalabuTotal = bonecaValue * 0.16F; bonecaAmbalabuTotal < bonecaValue * 0.9F; bonecaAmbalabuTotal += 7.0F) {
               bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuValue - 1.3F, bonecaAmbalabuTotal, bonecaAmbalabuValue + 1.3F, bonecaAmbalabuTotal + 1.0F, ForeShinUtils.getValueForIntFloat(8028240, 0.8F));
            }
         }
      }

      float bonecaAmbalabuValue2 = 0.13F * ambalabuBonecaValue;
      bonecaAmbalabu.getClassBonecaAmbalabu(ambalabuBonecaValue * 0.5F - bonecaAmbalabuValue2 - 2.0F, 0.0F, ambalabuBonecaValue * 0.5F + bonecaAmbalabuValue2 + 2.0F, bonecaValue, ForeShinUtils.getValueForInt(4145704));

      for (int j = 0; j < ambalabuBonecaValue; j++) {
         if (Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F) < 0.13F) {
            for (int k = 0; k < bonecaValue; k++) {
               bonecaAmbalabu.getBonecaAmbalabu(j, k, 0);
            }
         }
      }
   }

   private static void handleAmounts(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4) {
      int footBombombiniSize = 12;
      float[] floats = new float[3 * footBombombiniSize];
      float[] floats2 = new float[footBombombiniSize];
      float[] crownForeValues = new float[footBombombiniSize];

      for (int i = 0; i < footBombombiniSize; i++) {
         float footBombombiniFactor = 0.5F - 0.5F * (float)Math.cos(Math.PI * i / (footBombombiniSize - 1));
         floats[3 * i + 1] = getAmountForFloat(footBombombiniFactor);
         floats[3 * i + 2] = -18.3F + -5.8999996F * footBombombiniFactor;
         floats2[i] = Math.max(0.02F, getAmount9(footBombombiniFactor));
         crownForeValues[i] = Math.max(0.02F, getAmount6(footBombombiniFactor));
      }

      latheLoft.getLoftPath(floats, floats2, crownForeValues, 20, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
      float[] loftPathValues = new float[]{
         0.0F, 24.34883F, -23.255999F, 0.0F, 23.88883F, -24.935999F, 0.0F, 23.50883F, -26.655998F, 0.0F, 23.29883F, -27.216F, 0.0F, 23.068829F, -27.485998F
      };
      latheLoft.getLoftPath(loftPathValues, CAP_RADII_X, CAP_RADII_Z, 14, shinBrr2, null, shinBrr3, 0.0F, 1.0F, 0.0F);

      for (int j = -1; j <= 1; j += 2) {
         for (int k = 0; k < 5; k++) {
            float footBombombiniValue = 0.62F + k * 0.27F;
            float footBombombiniValue2 = footBombombiniValue + 0.22F;
            float[] footBombombiniValues = getAmounts(footBombombiniValue, (float)j);
            float[] amounts2 = getAmounts(footBombombiniValue2, (float)j);
            float[] amounts3 = getAmounts(0.5F * (footBombombiniValue + footBombombiniValue2), (float)j);
            float footBombombiniValue3 = 0.27F - 0.03F * k;
            latheLoft.getLatheLoft(footBombombiniValues[0], footBombombiniValues[1], footBombombiniValues[2], amounts3[0] * 0.8F, amounts3[1] - footBombombiniValue3, amounts3[2], amounts2[0], amounts2[1], amounts2[2], shinBrr4);
         }
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13883614), ForeShinUtils.getValueForInt(12107461)).getBonecaAmbalabu(0.02F, 6.0F, 61);
      float bonecaAmbalabuValue = (bonecaAmbalabuFlag ? 0.66F : 0.34F) * ambalabuBonecaValue;
      bonecaAmbalabu.getClass02(bonecaAmbalabuValue, bonecaValue * 0.12F, bonecaAmbalabuValue, (float)bonecaValue, 1.1F, ForeShinUtils.getValueForIntFloat(9212827, 0.9F));

      for (float bonecaAmbalabuFactor : new float[]{0.35F, 0.6F, 0.85F}) {
         bonecaAmbalabu.getClass02(0.0F, bonecaAmbalabuFactor * bonecaValue, (float)ambalabuBonecaValue, bonecaAmbalabuFactor * bonecaValue, 0.8F, ForeShinUtils.getValueForIntFloat(9212827, 0.5F));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue * 0.1F, ForeShinUtils.getValueForInt(15764004));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.1F, ambalabuBonecaValue, bonecaValue * 0.1F + 1.0F, ForeShinUtils.getValueForIntFloat(12082706, 0.8F));
   }

   private static void handle(LatheLoft latheLoft, float footBombombiniValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float sideScaled = footBombombiniValue * 5.0F;
      float[] floats = new float[]{0.3F, 0.7F, 0.7F, 0.75F};
      latheLoft.getLoftPath(new float[]{sideScaled, 5.4F, -0.2F, sideScaled, 5.4F, -0.9F, sideScaled, 5.4F, -6.15F, sideScaled, 5.4F, -6.6F}, floats, floats, 8, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(sideScaled, 5.4F, -6.6F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.6F, 0.14F, 8, 3, shinBrr);
      latheLoft.getDequeArray();
      latheLoft.getLoftLathe(sideScaled, 5.4F, -6.45F, 0.7F, 0.7F, 8, shinBrr2);
      float amount7 = getAmount7(5.0F);
      latheLoft.getLatheLoft(sideScaled - 0.13F, 5.7000003F, -3.6F, sideScaled + 0.13F, getAmount3(amount7) - 0.1F, -1.2F, null, null, shinBrr3, shinBrr3, null, null);
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float animationProgress = poseProgress.getAnimationProgressForFloat(0.0F);
      float cos = (float)Math.cos(angle);
      float shinProgressValue = cos * 0.22F * shinProgressFactor;
      float foreShinValue = animationProgress * 0.36F;
      float shinProgressValue2 = 5.35F * (1.0F - (float)Math.cos(foreShinValue));
      foreShinScale.getAmountsForeShin2(this.hull, 0.0F, -shinProgressValue2, 0.0F);
      foreShinScale.getAmounts(this.neck, 0.0F, cos * 0.08F * shinProgressFactor, shinProgressValue);
      foreShinScale.getAmountsForeShin2(this.neck, 0.0F, 0.35F * Math.abs((float)Math.sin(angle)) * shinProgressFactor, 0.0F);
      foreShinScale.getAmounts(this.footR, foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footL, -foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classShinProgress, -foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmountsForeShin2(this.footR, 0.0F, 0.8F * poseProgress.getAnimationProgress((float) Math.PI), 0.0F);
      foreShinScale.getAmountsForeShin2(this.footL, 0.0F, 0.8F * poseProgress.getAnimationProgress(0.0F), 0.0F);
      float shinProgressFactor2 = (float)Math.cos(2.0F * angle) * shinProgressFactor;
      foreShinScale.getAmounts(
         this.head, -0.15F * shinProgressFactor2 + 0.03F * poseProgress.getAgeCode(90.0F, 0.5F), 0.05F * poseProgress.getAgeCode(130.0F, 1.3F), -shinProgressValue * 0.8F + 0.035F * poseProgress.getAgeCode(110.0F, 0.0F)
      );
      foreShinScale.getAmounts(this.eyeL, 0.13F * shinProgressFactor2, 0.14F * poseProgress.getAgeCode(97.0F, 2.0F), shinProgressValue * 0.3F);
      foreShinScale.getAmountsForeShin2(this.head, 0.0F, 0.0F, -0.5F * shinProgressFactor2);
      foreShinScale.getAmountsBoneCount(this.head, poseProgress.shinProgressAmount * 0.45F, poseProgress.headYawRadians * 0.3F, 0.0F);
      foreShinScale.getAmountsBoneCount(this.eyeL, poseProgress.shinProgressAmount * 0.5F, poseProgress.headYawRadians * 0.7F, 0.0F);
      float radiansFactor = 30.0F + 45.0F * shinProgressFactor + (poseProgress.gliding ? 60.0F : 0.0F);
      float shinProgressValue4 = poseProgress.swingProgress > 0.0F ? (float) (Math.PI * 4) * getAmount5((poseProgress.swingProgress - 0.1F) / 0.6F) : 0.0F;

      for (int i = 0; i < 4; i++) {
         foreShinScale.getAmounts(this.shinFootForeEars[i], 0.0F, 0.0F, poseProgress.getRadians(radiansFactor) + i * 0.9F + shinProgressValue4);
      }

      float shinProgressValue5 = (poseProgress.age + (poseProgress.hashCode & 127)) % 70.0F;
      if (shinProgressValue5 < 2.5F) {
         float shinProgressValue6 = 1.0F - (float)Math.sin(shinProgressValue5 / 2.5F * Math.PI) * 0.88F;
         foreShinScale.getAmountsForForeEarFloat(this.shinFootForeEar, 1.0F, shinProgressValue6, 1.0F);
         foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F, shinProgressValue6, 1.0F);
      }

      for (int j = 0; j < this.grenadeSections.length; j++) {
         float shinProgressValue7 = 0.07F * poseProgress.getAgeCode(23.0F, j * 1.1F) * (0.4F + 0.6F * shinProgressFactor) - 0.1F * animationProgress * shinProgressFactor;
         foreShinScale.getAmounts(this.grenadeSections[j], shinProgressValue7, 0.0F, -shinProgressValue * 0.5F + 0.03F * poseProgress.getAgeCode(29.0F, j * 0.7F));
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressValue8 = swingProgress < 0.12F ? getAmount5(swingProgress / 0.12F) : 1.0F - getAmount5((swingProgress - 0.12F) / 0.12F);
         float shinProgressFactor3 = swingProgress < 0.12F
            ? 0.0F
            : (swingProgress < 0.26F ? getAmount5((swingProgress - 0.12F) / 0.14F) : (swingProgress < 0.62F ? 1.0F : 1.0F - getAmount5((swingProgress - 0.62F) / 0.38F)));
         foreShinScale.getAmountsBoneCount(this.head, 0.25F * shinProgressValue8 - 0.62F * shinProgressFactor3, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.eyeL, 0.5F * shinProgressFactor3, 0.0F, 0.12F * shinProgressFactor3 * (float)Math.sin(poseProgress.age * 3.1F));
         float shinProgressDivisor = 1.0F + 0.12F * shinProgressFactor3;
         foreShinScale.getAmountsForForeEarFloat(this.head, 1.0F, shinProgressDivisor, 1.0F);
         foreShinScale.getAmountsForForeEarFloat(this.eyeL, 1.0F, 1.0F / shinProgressDivisor, 1.0F);
         foreShinScale.getAmounts(this.footBombombiniForeEar, -0.7F * shinProgressFactor3 * (0.85F + 0.15F * (float)Math.sin(poseProgress.age * 2.6F)), 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.neck, -0.05F * shinProgressFactor3, 0.0F, 0.0F);

         for (int k = 0; k < this.grenadeSections.length; k++) {
            foreShinScale.getAmountsBoneCount(this.grenadeSections[k], -0.35F * shinProgressFactor3 + 0.2F * shinProgressValue8, 0.0F, 0.0F);
         }
      }

      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsBoneCount(this.head, -1.0F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.eyeL, 0.85F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.footBombombiniForeEar, -0.45F, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.hull, 0.0F, -0.45F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.footR, 0.25F, 0.0F, 0.3F);
         foreShinScale.getAmountsBoneCount(this.footL, 0.25F, 0.0F, -0.3F);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, -0.25F, 0.0F, -0.3F);
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, -0.25F, 0.0F, 0.3F);
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.footR, 1.3F, 0.0F, 0.12F);
         foreShinScale.getAmounts(this.footL, 1.3F, 0.0F, -0.12F);
         foreShinScale.getAmounts(this.classShinProgress, -0.9F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, -0.9F, 0.0F, 0.0F);
      } else if (poseProgress.gliding) {
         foreShinScale.getAmounts(this.footR, -1.35F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -1.35F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.classShinProgress, -0.2F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinProgressForeEar, -0.2F, 0.0F, 0.0F);
      }

      if (poseProgress.gliding) {
         foreShinScale.getAmountsBoneCount(this.hull, (float) (Math.PI / 2), 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.hull, 0.0F, 18.0F, -5.5F);
         foreShinScale.getAmountsBoneCount(this.neck, 0.0F, 0.22F * poseProgress.getAgeCode(160.0F, 0.0F), 0.0F);
         foreShinScale.getAmountsBoneCount(this.head, -0.5F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.eyeL, 0.45F, 0.0F, 0.0F);
      }

      if (poseProgress.leaningPitch > 0.0F && !poseProgress.gliding) {
         foreShinScale.getAmountsBoneCount(this.hull, (float) (Math.PI / 2) * poseProgress.leaningPitch, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.hull, 0.0F, 18.0F * poseProgress.leaningPitch, -5.5F * poseProgress.leaningPitch);
         float shinProgressValue9 = (float)Math.sin(poseProgress.age * 0.45F) * 0.7F * poseProgress.leaningPitch;
         foreShinScale.getAmounts(this.footR, -0.6F * poseProgress.leaningPitch + shinProgressValue9, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.6F * poseProgress.leaningPitch - shinProgressValue9, 0.0F, 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.neck, 1.05F, 0.95F, 1.05F);
      }
   }

   private static float getAmount5(float footBombombiniFactor) {
      footBombombiniFactor = footBombombiniFactor < 0.0F ? 0.0F : (footBombombiniFactor > 1.0F ? 1.0F : footBombombiniFactor);
      return footBombombiniFactor * footBombombiniFactor * (3.0F - 2.0F * footBombombiniFactor);
   }

   private static void handleBonecaAmbalabu9(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(4875812)).getBonecaAmbalabu(0.04F, 6.0F, 51);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.3F - 1.0F, ambalabuBonecaValue, bonecaValue * 0.3F + 1.0F, ForeShinUtils.getValueForIntFloat(8364102, 0.5F));

      for (float bonecaAmbalabuTotal = 0.0F; bonecaAmbalabuTotal < ambalabuBonecaValue; bonecaAmbalabuTotal += ambalabuBonecaValue / 3.0F) {
         bonecaAmbalabu.getClass02(bonecaAmbalabuTotal, 0.0F, bonecaAmbalabuTotal, (float)bonecaValue, 1.4F, ForeShinUtils.getValueForIntFloat(2897942, 0.7F));
      }

      for (float amountForFloat = bonecaValue / 3.5F * 0.5F; amountForFloat < bonecaValue; amountForFloat += bonecaValue / 3.5F) {
         bonecaAmbalabu.getClass02(0.0F, amountForFloat, (float)ambalabuBonecaValue, amountForFloat, 1.4F, ForeShinUtils.getValueForIntFloat(2897942, 0.7F));
      }
   }

   private static float getAmount6(float footBombombiniValue) {
      return getAmount9(footBombombiniValue) * (1.15F - 0.15F * footBombombiniValue);
   }

   private static void handleForLatheLoftFloatShinBrr(LatheLoft latheLoft, float footBombombiniValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float toeScaled = footBombombiniValue * 2.1999998F;
      float dinForeValue = -1.1F;
      latheLoft.getLatheLoft(toeScaled, 0.95F, dinForeValue, 0.56F, 6, shinBrr3);
      float footBombombiniValue3 = dinForeValue - 3.1F;
      float footBombombiniValue4 = dinForeValue + 0.35F;
      latheLoft.getLatheLoft(toeScaled - 1.6F, 0.02F, footBombombiniValue3, toeScaled + 1.6F, 0.3F, footBombombiniValue4, null, null, null, null, shinBrr, shinBrr);
      float[][] floats = new float[][]{{-1.22F, 0.2F}, {0.0F, 0.06F}, {1.22F, 0.2F}};

      for (float[] footBombombiniValues : floats) {
         float footBombombiniValue5 = toeScaled + footBombombiniValues[0];
         float footBombombiniValue6 = footBombombiniValue3 + footBombombiniValues[1] * (footBombombiniValue4 - footBombombiniValue3);
         latheLoft.getLatheLoft(new float[]{toeScaled, 0.5F, dinForeValue - 0.2F, footBombombiniValue5, 0.3F, footBombombiniValue6}, new float[]{0.26F, 0.09F}, 5, shinBrr2, false);
      }
   }

   private static float getAmounts(float footBombombiniValue, int index) {
      int footBombombiniLimit = BODY_PROFILE_SAMPLES.length / 3;
      if (footBombombiniValue <= BODY_PROFILE_SAMPLES[0]) {
         return BODY_PROFILE_SAMPLES[index];
      } else {
         for (int i = 1; i < footBombombiniLimit; i++) {
            float footBombombiniLimit2 = BODY_PROFILE_SAMPLES[3 * i];
            if (footBombombiniValue <= footBombombiniLimit2) {
               float footBombombiniValue2 = BODY_PROFILE_SAMPLES[3 * i - 3];
               float footBombombiniFactor = footBombombiniLimit2 > footBombombiniValue2 ? (footBombombiniValue - footBombombiniValue2) / (footBombombiniLimit2 - footBombombiniValue2) : 0.0F;
               return BODY_PROFILE_SAMPLES[3 * i - 3 + index] + (BODY_PROFILE_SAMPLES[3 * i + index] - BODY_PROFILE_SAMPLES[3 * i - 3 + index]) * footBombombiniFactor;
            }
         }

         return BODY_PROFILE_SAMPLES[3 * (footBombombiniLimit - 1) + index];
      }
   }

   private static void handleBonecaAmbalabu10(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(7238470)).getBonecaAmbalabu(0.03F, 8.0F, 75);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, 2.0F, ForeShinUtils.getValueForInt(4145704));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue - 2.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(4145704));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, 2.0F, bonecaValue, ForeShinUtils.getValueForInt(4145704));
      bonecaAmbalabu.getClassBonecaAmbalabu(ambalabuBonecaValue - 2.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(4145704));

      for (float bonecaAmbalabuTotal = 7.0F; bonecaAmbalabuTotal < bonecaValue - 4.0F; bonecaAmbalabuTotal += 6.0F) {
         bonecaAmbalabu.getClassBonecaAmbalabu(4.0F, bonecaAmbalabuTotal, ambalabuBonecaValue - 4.0F, bonecaAmbalabuTotal + 2.2F, ForeShinUtils.getValueForInt(4145704));

         for (float classBound = 7.0F; classBound < ambalabuBonecaValue - 5.0F; classBound += 6.0F) {
            bonecaAmbalabu.getClassBonecaAmbalabu(classBound, bonecaAmbalabuTotal + 0.4F, classBound + 1.2F, bonecaAmbalabuTotal + 1.8F, ForeShinUtils.getValueForIntFloat(8028240, 0.8F));
         }
      }
   }

   private static void handleAmountsBoneca(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue3 = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < ambalabuBonecaValue; i++) {
         float bonecaValue = (i + 0.5F) / ambalabuBonecaValue;
         float absScaled = Math.abs(0.5F - bonecaValue) * 360.0F;
         int valueForInt = ForeShinUtils.getValueForInt(12107461);
         if (absScaled > 80.0F) {
            valueForInt = ForeShinUtils.getValue2(valueForInt, ForeShinUtils.getValueForInt(10265515), Math.min(1.0F, (absScaled - 80.0F) / 50.0F));
         }

         int bonecaAmbalabuValue = ForeShinUtils.getValue(valueForInt, 1.0F + 0.07F * (float)Math.exp(-getAmount8((absScaled - 38.0F) / 16.0F)));

         for (int j = 0; j < bonecaValue3; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, bonecaAmbalabuValue);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.022F, 12.0F, 5);
      float bonecaTotal = 0.0F;

      for (int k = 1; k < bonecaTable.length; k++) {
         float bonecaFactor = bonecaTable[k] - bonecaTable[k - 1];
         float deltaAmount = getAmount(bonecaTable[k]) - getAmount(bonecaTable[k - 1]);
         bonecaTotal += (float)Math.sqrt(bonecaFactor * bonecaFactor + deltaAmount * deltaAmount);
      }

      float bonecaValue4 = bonecaValue3 / bonecaTotal;

      for (float footBombombiniLimit : new float[]{-4.6F, 0.8F, 4.6F, 8.6F, 12.4F, 15.8F, 19.0F}) {
         float bonecaValue6 = getAmounts(footBombombiniLimit) * bonecaValue3;
         bonecaAmbalabu.getClass02(0.0F, bonecaValue6, (float)ambalabuBonecaValue, bonecaValue6, 1.1F, ForeShinUtils.getValueForIntFloat(9212827, 0.8F));

         for (float classValue = 3.0F; classValue < ambalabuBonecaValue; classValue += 6.0F) {
            bonecaAmbalabu.getClass022(classValue, bonecaValue6 + 2.4F, 0.6F, 0.6F, ForeShinUtils.getValueForIntFloat(8225932, 0.75F));
         }
      }

      for (float bonecaValue7 : new float[]{-112.0F, -28.0F, 28.0F, 112.0F}) {
         float classBound = (0.5F - bonecaValue7 / 360.0F) * ambalabuBonecaValue;
         bonecaAmbalabu.getClass02(classBound, getAmounts(-10.4F) * bonecaValue3, classBound, getAmounts(20.5F) * bonecaValue3, 1.0F, ForeShinUtils.getValueForIntFloat(9212827, 0.6F));
      }

      for (int m = -1; m <= 1; m += 2) {
         float bonecaValue9 = (0.5F - m * 64.0F / 360.0F) * ambalabuBonecaValue;

         for (float bonecaCounter = -4.4F; bonecaCounter < 17.05F; bonecaCounter++) {
            float amountForFloat = getAmounts(bonecaCounter) * bonecaValue3;
            float bonecaValue11 = ambalabuBonecaValue / ((float) (Math.PI * 2) * getAmount11(bonecaCounter));
            bonecaAmbalabu.getClass022(bonecaValue9, amountForFloat, 0.4F * bonecaValue11, 0.4F * bonecaValue4, ForeShinUtils.getValueForIntFloat(9212827, 0.9F));
            bonecaAmbalabu.getClass022(bonecaValue9, amountForFloat, 0.3F * bonecaValue11, 0.3F * bonecaValue4, ForeShinUtils.getValueForInt(2765371));
            bonecaAmbalabu.getClass022(bonecaValue9 - 0.1F * bonecaValue11, amountForFloat - 0.1F * bonecaValue4, 0.09F * bonecaValue11, 0.09F * bonecaValue4, ForeShinUtils.getValueForIntFloat(10466504, 0.8F));
         }
      }

      float bonecaValue12 = 0.7222222F * ambalabuBonecaValue;
      float bonecaValue13 = getAmounts(9.3F) * bonecaValue3;
      float bonecaValue14 = getAmounts(10.5F) * bonecaValue3;
      float bonecaValue15 = 0.75F * ambalabuBonecaValue / ((float) (Math.PI * 2) * getAmount11(10.0F));
      bonecaAmbalabu.getClass02(bonecaValue12 - bonecaValue15, bonecaValue13, bonecaValue12 + bonecaValue15, bonecaValue13, 1.0F, ForeShinUtils.getValueForIntFloat(9212827, 0.9F));
      bonecaAmbalabu.getClass02(bonecaValue12 - bonecaValue15, bonecaValue14, bonecaValue12 + bonecaValue15, bonecaValue14, 1.0F, ForeShinUtils.getValueForIntFloat(9212827, 0.9F));
      bonecaAmbalabu.getClass02(bonecaValue12 - bonecaValue15, bonecaValue13, bonecaValue12 - bonecaValue15, bonecaValue14, 1.0F, ForeShinUtils.getValueForIntFloat(9212827, 0.9F));
      bonecaAmbalabu.getClass02(bonecaValue12 + bonecaValue15, bonecaValue13, bonecaValue12 + bonecaValue15, bonecaValue14, 1.0F, ForeShinUtils.getValueForIntFloat(9212827, 0.9F));
   }

   private static float getAmount7(float footBombombiniValue) {
      return (footBombombiniValue - 2.6F) / 21.0F;
   }

   private static void handle3(LatheLoft latheLoft, float footBombombiniFactor, ShinBrr shinBrr) {
      float[] floats = new float[]{0.0F, 4.0F, 7.6F};
      int footBombombiniSize = floats.length;
      float[] loftPathValues = new float[3 * footBombombiniSize];
      float[] floats3 = new float[footBombombiniSize];
      float[] crownForeValues = new float[footBombombiniSize];

      for (int i = 0; i < footBombombiniSize; i++) {
         float footBombombiniValue = floats[i] / 7.6F;
         float footBombombiniValue2 = 16.4F + 1.6F * footBombombiniValue;
         float footBombombiniValue3 = 20.6F - 0.2F * footBombombiniValue;
         loftPathValues[3 * i] = footBombombiniFactor * floats[i];
         loftPathValues[3 * i + 1] = 9.85F + 0.25F * footBombombiniValue;
         loftPathValues[3 * i + 2] = 0.5F * (footBombombiniValue2 + footBombombiniValue3);
         floats3[i] = 0.5F * (footBombombiniValue3 - footBombombiniValue2);
         crownForeValues[i] = 0.34F - 0.12F * footBombombiniValue;
      }

      latheLoft.getLoftPath(loftPathValues, floats3, crownForeValues, 8, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
   }

   private static void handleBonecaAmbalabu11(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16777215)).getBonecaAmbalabu(0.014F, 6.0F, 37);

      for (int i = 0; i < ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / ambalabuBonecaValue;
         float cos = (float)Math.cos((bonecaAmbalabuValue - 0.5F) * (float) (Math.PI * 2));
         float bonecaAmbalabuLimit = Math.max(0.0F, -cos - 0.35F) * 0.22F;
         if (bonecaAmbalabuLimit > 0.0F) {
            for (int j = 0; j < bonecaValue; j++) {
               bonecaAmbalabu.getBonecaAmbalabu(i, j, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(i, j), ForeShinUtils.getValueForInt(14473423), bonecaAmbalabuLimit));
            }
         }
      }
   }

   private static float getAmount8(float footBombombiniFactor) {
      return footBombombiniFactor * footBombombiniFactor;
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(0);
      bonecaAmbalabu.getClassForIntFloatArray(
         ForeShinUtils.getValueForInt(15763754),
         20.0F,
         46.0F,
         5.0F,
         11.0F,
         6.5F,
         7.0F,
         15.0F,
         12.5F,
         21.5F,
         2.5F,
         26.5F,
         2.5F,
         33.0F,
         12.5F,
         41.5F,
         7.0F,
         43.0F,
         11.0F,
         28.0F,
         46.0F
      );
      bonecaAmbalabu.getClass022(24.0F, 42.0F, 5.0F, 4.5F, ForeShinUtils.getValueForInt(15763754));

      for (float[] footBombombiniValues : new float[][]{{6.0F, 9.0F}, {24.0F, 3.0F}, {42.0F, 9.0F}}) {
         bonecaAmbalabu.getClass02(24.0F, 40.0F, footBombombiniValues[0], footBombombiniValues[1], 2.4F, ForeShinUtils.getValueForInt(13133338));
         bonecaAmbalabu.getClass022(footBombombiniValues[0], footBombombiniValues[1], 1.6F, 1.6F, ForeShinUtils.getValueForInt(5913116));
      }
   }

   private static void handleAmounts(LatheLoft latheLoft, ShinBrr shinBrr) {
      int footBombombiniSize = bonecaTable.length;
      float[] floats = new float[3 * footBombombiniSize];
      float[] floats2 = new float[footBombombiniSize];
      float[] crownForeValues = new float[footBombombiniSize];

      for (int i = 0; i < footBombombiniSize; i++) {
         floats[3 * i + 1] = getAmount(bonecaTable[i]);
         floats[3 * i + 2] = bonecaTable[i];
         floats2[i] = getAmount11(bonecaTable[i]);
         crownForeValues[i] = getAmount11(bonecaTable[i]) * 1.03F;
      }

      latheLoft.getLoftPath(floats, floats2, crownForeValues, 16, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
   }

   private static int getAmounts() {
      int footBombombiniCounter = 1;

      while (footBombombiniCounter < LatheLoft.getValue(pointsSpline) - 2 && pointsSpline[3 * footBombombiniCounter + 1] < 9.799999F) {
         footBombombiniCounter++;
      }

      return footBombombiniCounter;
   }

   private static float[] getPointsSpline() {
      return LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 1.9F, 1.62F, 1.38F, 1.24F, 1.18F, 1.22F, 1.42F);
   }

   private static float[] getAmountsFootBombombiniTable() {
      int footBombombiniLimit = footBombombiniTable.length;
      float[] floats = new float[3 * footBombombiniLimit];
      float[] pointsSplineValues = new float[3 * footBombombiniLimit];

      for (int i = 0; i < footBombombiniLimit; i++) {
         floats[3 * i + 1] = table[i] - 2.4F;
         floats[3 * i + 2] = footBombombiniTable[i];
         pointsSplineValues[3 * i] = BODY_PROFILE_RADII[i];
      }

      float[] pointsSpline = LatheLoft.getPointsSpline(8, floats);
      float[] pointsSpline2 = LatheLoft.getPointsSpline(8, pointsSplineValues);
      int footBombombiniLimit2 = pointsSpline.length / 3;
      float[] floats3 = new float[3 * footBombombiniLimit2];

      for (int j = 0; j < footBombombiniLimit2; j++) {
         floats3[3 * j] = pointsSpline[3 * j + 2];
         floats3[3 * j + 1] = pointsSpline[3 * j + 1];
         floats3[3 * j + 2] = Math.max(0.0F, pointsSpline2[3 * j]);
      }

      return floats3;
   }

   private static void handleAmount(LatheLoft latheLoft, ForeEar foreEar, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float footBombombiniValue = foreEar.offsetX;
      float foreEarAmount = foreEar.foreEarAmount;
      float armTrunkValue = foreEar.shinFootAmount;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(footBombombiniValue, foreEarAmount, armTrunkValue).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLatheLoft(new float[]{0.7F, 0.56F, 0.0F}, new float[]{0.0F, 0.65F, 1.35F}, 8, shinBrr, null, null);
      latheLoft.getDequeArray();

      for (int i = 0; i < 3; i++) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(footBombombiniValue, foreEarAmount, armTrunkValue - 0.35F).getLatheLoft(i * 120.0F + 30.0F).getLatheLoft4(22.0F);
         latheLoft.getLatheLoft(-0.42F, 0.3F, -0.07F, 0.42F, 3.15F, 0.07F, shinBrr2, shinBrr2, null, null, null, null);
         latheLoft.getDequeArray();
      }
   }

   private static void handle(LatheLoft latheLoft, float footBombombiniValue, float[] footBombombiniValues, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(footBombombiniValue, -2.9F, 0.0F).getLatheLoft4(-90.0F);
      latheLoft.getPolygonExtrude(footBombombiniValues, -0.22F, 0.22F, shinBrr, shinBrr2, shinBrr3);
      latheLoft.getDequeArray();
   }

   private static float getAmount9(float footBombombiniValue) {
      float footBombombiniFactor = 2.0F * footBombombiniValue - 1.0F;
      float sqrt = (float)Math.sqrt(Math.max(0.0F, 1.0F - footBombombiniFactor * footBombombiniFactor));
      return 2.12F * sqrt * (1.0F - 0.2F * footBombombiniValue);
   }

   private static float getAmounts(float footBombombiniLimit) {
      float footBombombiniTotal = 0.0F;
      float footBombombiniTotal2 = 0.0F;

      for (int i = 1; i < bonecaTable.length; i++) {
         float footBombombiniFactor = bonecaTable[i] - bonecaTable[i - 1];
         float deltaAmount = getAmount(bonecaTable[i]) - getAmount(bonecaTable[i - 1]);
         float distance = (float)Math.sqrt(footBombombiniFactor * footBombombiniFactor + deltaAmount * deltaAmount);
         if (footBombombiniLimit > bonecaTable[i - 1]) {
            footBombombiniTotal2 += distance * Math.min(1.0F, (footBombombiniLimit - bonecaTable[i - 1]) / footBombombiniFactor);
         }

         footBombombiniTotal += distance;
      }

      return footBombombiniTotal2 / footBombombiniTotal;
   }

   private static void handleBonecaAmbalabu12(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(12107461)).getBonecaAmbalabu(0.02F, 6.0F, 95);

      for (float bonecaAmbalabuFactor : new float[]{0.35F, 0.65F}) {
         bonecaAmbalabu.getClass02(bonecaAmbalabuFactor * ambalabuBonecaValue, 0.0F, bonecaAmbalabuFactor * ambalabuBonecaValue, (float)bonecaValue, 0.9F, ForeShinUtils.getValueForIntFloat(9212827, 0.6F));
      }

      bonecaAmbalabu.getClass02(0.0F, bonecaValue * 0.5F, (float)ambalabuBonecaValue, bonecaValue * 0.5F, 0.9F, ForeShinUtils.getValueForIntFloat(9212827, 0.6F));
   }

   @Override
   public String formatTungDinString() {
      return "Bombombini Gusini";
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float[] floats = new float[]{0.0F, 23.42883F, -23.366F, 0.0F, 23.168829F, -25.135998F, 0.0F, 23.02883F, -26.805998F, 0.0F, 23.01883F, -27.135998F};
      float[] crownForeValues = new float[]{0.92F, 0.67F, 0.29F, 0.09F};
      float[] floats3 = new float[]{0.44F, 0.31F, 0.14F, 0.07F};
      latheLoft.getLoftPath(floats, crownForeValues, floats3, 10, shinBrr, null, shinBrr, 0.0F, 1.0F, 0.0F);
      latheLoft.getLatheLoft(0.0F, 23.738829F, -24.505999F, 0.39F, 0.14F, 1.03F, 8, shinBrr2);
   }

   @Override
   public float getAmountFloat() {
      return 1.5F;
   }

   private static void handleAmountsForLatheLoftShinBrr(LatheLoft latheLoft, ShinBrr shinBrr) {
      float[] footBombombiniValues = getPointsSpline();
      int index = getAmounts();
      float x = pointsSpline[3 * index + 4] - pointsSpline[3 * index - 2];
      float footBombombiniValue = pointsSpline[3 * index + 5] - pointsSpline[3 * index - 1];
      float degrees = (float)Math.toDegrees(Math.atan2(-footBombombiniValue, x));
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, pointsSpline[3 * index + 1], pointsSpline[3 * index + 2]).getLatheLoftLatheLoftAmountLoft(-degrees);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, footBombombiniValues[index] + 0.06F, 0.3F, 16, 5, shinBrr);
      latheLoft.getDequeArray();
   }

   private static void handle(LatheLoft latheLoft, float footBombombiniValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float amount7 = getAmount7(Math.abs(footBombombiniValue));
      float footBombombiniValue3 = getAmount3(amount7) - 0.3F;
      float footBombombiniValue4 = getAmount10(amount7) - 2.5F;
      float armTrunkValue = getAmount4(amount7) - 0.2F;
      float deltaVar = armTrunkValue - footBombombiniValue4;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(footBombombiniValue, footBombombiniValue3, armTrunkValue).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLatheLoft(new float[]{0.3F, 1.0F, 1.18F}, new float[]{0.0F, 0.9F, deltaVar}, 8, shinBrr, null, shinBrr2);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu13(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / ambalabuBonecaValue;
         float cos = (float)Math.cos((bonecaAmbalabuValue - 0.5F) * (float) (Math.PI * 2));
         int value2 = ForeShinUtils.getValue2(
            ForeShinUtils.getValue(ForeShinUtils.getValueForInt(15894047), 0.92F),
            ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15894047), ForeShinUtils.getValueForInt(16761466), 0.3F),
            0.5F + 0.5F * cos
         );

         for (int j = 0; j < bonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(i, j, value2);
         }
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue * 0.05F, ForeShinUtils.getValueForIntFloat(14182930, 0.8F));

      for (float bonecaAmbalabuFactor : new float[]{0.32F, 0.68F}) {
         bonecaAmbalabu.getClass022(
            bonecaAmbalabuFactor * ambalabuBonecaValue, bonecaValue * 0.42F, 0.8F, 2.4F, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5913116), ForeShinUtils.getValueForInt(15894047), 0.35F)
         );
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.9F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(15909002));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaValue * 0.86F, ambalabuBonecaValue, bonecaValue * 0.9F, ForeShinUtils.getValueForIntFloat(15909002, 0.5F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue * 0.04F, bonecaValue, ForeShinUtils.getValueForInt(12083278));
      bonecaAmbalabu.getClassBonecaAmbalabu(ambalabuBonecaValue * 0.96F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(12083278));

      for (float bonecaAmbalabuTotal = 2.0F; bonecaAmbalabuTotal < bonecaValue * 0.86F; bonecaAmbalabuTotal += 4.0F) {
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(15918792), ambalabuBonecaValue * 0.12F, bonecaAmbalabuTotal, ambalabuBonecaValue * 0.12F, bonecaAmbalabuTotal + 3.5F, ambalabuBonecaValue * 0.01F, bonecaAmbalabuTotal + 1.75F);
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(15918792), ambalabuBonecaValue * 0.88F, bonecaAmbalabuTotal, ambalabuBonecaValue * 0.88F, bonecaAmbalabuTotal + 3.5F, ambalabuBonecaValue * 0.99F, bonecaAmbalabuTotal + 1.75F);
      }

      bonecaAmbalabu.getClass02(ambalabuBonecaValue * 0.125F, 0.0F, ambalabuBonecaValue * 0.125F, bonecaValue * 0.9F, 1.0F, ForeShinUtils.getValueForInt(12079130));
      bonecaAmbalabu.getClass02(ambalabuBonecaValue * 0.875F, 0.0F, ambalabuBonecaValue * 0.875F, bonecaValue * 0.9F, 1.0F, ForeShinUtils.getValueForInt(12079130));
   }

   private static void handleAmountsPointsSpline(LatheLoft latheLoft, ShinBrr shinBrr) {
      latheLoft.getLatheLoft(pointsSpline, getPointsSpline(), 10, shinBrr, false);
   }

   private static float getAmount2(float footBombombiniValue, float degrees) {
      double angle = Math.toRadians(degrees);
      float sin = (float)Math.sin(angle);
      float cos = (float)Math.cos(angle);
      float footBombombiniDivisor = getAmount11(footBombombiniValue) + 0.3F;
      float footBombombiniDivisor2 = getAmount11(footBombombiniValue) * 1.03F + 0.3F;
      return 1.0F / (float)Math.sqrt(getAmount8(sin / footBombombiniDivisor) + getAmount8(cos / footBombombiniDivisor2));
   }

   private static float getAmount10(float footBombombiniValue) {
      return -4.6F + 2.0F * footBombombiniValue;
   }

   private static float getAmount11(float footBombombiniValue) {
      return getAmounts(footBombombiniValue, 2);
   }

   private static float[] getAmounts(float footBombombiniValue, float footBombombiniFactor) {
      float[] floats = new float[]{0.0F, -0.46F, -0.84F};
      float[] floats2 = new float[]{0.0F, -1.68F, -3.4F};
      int index = Math.min(1, (int)footBombombiniValue);
      float footBombombiniFactor2 = footBombombiniValue - index;
      float footBombombiniFactor3 = CAP_RADII_X[index] + (CAP_RADII_X[index + 1] - CAP_RADII_X[index]) * footBombombiniFactor2;
      float footBombombiniValue2 = CAP_RADII_Z[index] + (CAP_RADII_Z[index + 1] - CAP_RADII_Z[index]) * footBombombiniFactor2;
      return new float[]{
         footBombombiniFactor * footBombombiniFactor3 * 0.9F,
         24.34883F + floats[index] + (floats[index + 1] - floats[index]) * footBombombiniFactor2 - footBombombiniValue2 * 0.42F,
         -23.255999F + floats2[index] + (floats2[index + 1] - floats2[index]) * footBombombiniFactor2
      };
   }

   private static float getAmounts(int footBombombiniLimit) {
      float footBombombiniTotal = 0.0F;
      float footBombombiniTotal2 = 0.0F;

      for (int i = 1; i < LatheLoft.getValue(pointsSpline); i++) {
         float footBombombiniFactor = pointsSpline[3 * i + 1] - pointsSpline[3 * i - 2];
         float footBombombiniFactor2 = pointsSpline[3 * i + 2] - pointsSpline[3 * i - 1];
         float distance = (float)Math.sqrt(footBombombiniFactor * footBombombiniFactor + footBombombiniFactor2 * footBombombiniFactor2);
         if (i <= footBombombiniLimit) {
            footBombombiniTotal2 += distance;
         }

         footBombombiniTotal += distance;
      }

      return footBombombiniTotal2 / footBombombiniTotal;
   }

   private static void handleBonecaAmbalabu14(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(10133928)).getBonecaAmbalabu(0.04F, 6.0F, 91);

      for (float bonecaAmbalabuTotal = 2.0F; bonecaAmbalabuTotal < ambalabuBonecaValue; bonecaAmbalabuTotal += 5.33F) {
         bonecaAmbalabu.getClass022(bonecaAmbalabuTotal, 1.5F, 0.9F, 0.9F, ForeShinUtils.getValueForInt(7239036));
      }

      bonecaAmbalabu.getClass02(0.0F, bonecaValue * 0.5F, (float)ambalabuBonecaValue, bonecaValue * 0.5F, 1.0F, ForeShinUtils.getValueForIntFloat(7239036, 0.6F));
   }
}
