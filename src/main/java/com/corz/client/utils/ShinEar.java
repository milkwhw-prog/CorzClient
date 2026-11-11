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

public final class ShinEar extends WarmBrainrotBase {
   private ForeEar classShinFoot;
   private ForeEar classShinProgress;
   private ForeEar shinFootForeEar;
   private static final float[] shinEarTable = new float[]{0.0F, 0.1F, 0.18F, 0.26F, 0.34F, 0.41F, 0.48F, 0.56F, 0.63F, 0.75F, 0.82F, 0.87F, 1.0F};
   private static final int[] table = new int[]{
      8025711, 8025711, 10262414, 14080476, 15856629, 12830925, 4146507, 4870230, 9278619, 13488854, 10726065, 15659507, 14871282
   };
   private ForeEar shinProgressForeEar;
   private static final float[] SPINE_NORMALS;
   private ForeEar shinEarForeEar;
   private ForeEar body;
   private ForeEar handL;
   private static final float[] SPINE_TANGENTS;
   private static final float[] SPINE_CUMULATIVE_LENGTHS;
   private static final float[] shinFootTable;
   private ForeEar shinL;
   private ForeEar foreEar;
   private ForeEar armL;
   private static final float[] TOAST_THICKNESSES = new float[]{0.3F, 4.2F, 5.2F, 5.0F, 4.3F, 3.4F, 3.0F, 2.7F, 2.5F, 2.2F};
   private ForeEar toastASection;
   private static final float[] SAMPLED_THICKNESSES;
   private ForeEar handLeftSection;
   private ForeEar shinR;
   private static final float[] SPINE_Y = new float[]{22.2F, 23.2F, 23.6F, 23.4F, 22.5F, 21.9F, 21.4F, 21.1F, 20.95F, 20.85F};
   private ForeEar leverSection;
   private static final float[] SPINE_Z = new float[]{-0.9F, -1.5F, -3.5F, -5.8F, -8.6F, -10.9F, -12.6F, -13.3F, -13.8F, -14.1F};
   private ForeEar handR;
   private ForeEar shinRightSection;
   private ForeEar earL;
   private static final float[] pointsSpline;
   private ForeEar toastBSection;
   private static final float[] TOAST_WIDTHS = new float[]{0.3F, 3.2F, 4.6F, 4.5F, 4.1F, 3.8F, 3.4F, 3.2F, 3.0F, 2.7F};
   private static final float[] bonecaTable;

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         int amountsShinEarTable = getAmountsShinEarTable(21.0F - (i + 0.5F) / bonecaAmbalabu.bonecaValue * 9.599999F);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, amountsShinEarTable);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.018F, 2.0F, bonecaAmbalabuValue);
      float bonecaAmbalabuValue3 = 0.49999994F * bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabuValue3 - 0.8F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuValue3 + 0.8F, ForeShinUtils.getValueForIntFloat(3027510, 0.9F));
   }

   private static float getAmount(BonecaAmbalabu bonecaAmbalabu, float shinEarValue) {
      return 0.5F + getAmountsForFloat(getAmountsShinFootTable(shinEarValue)) * (bonecaAmbalabu.bonecaValue - 1);
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft3(1.07F);
      this.body = getClassForLatheLoftString(latheLoft, "hips", latheLoft.getClassForeEarList(), 0.0F, 9.4F, 0.0F);
      this.armL = getClassForLatheLoftString(latheLoft, "body", this.body, 0.0F, 9.4F, 0.0F);
      this.earL = getClassForLatheLoftString(latheLoft, "head", this.armL, 0.0F, 22.0F, -3.8F);
      float shinFootValue = getAmountsShinFootTable(-6.3F);
      float[] shinFootValues = getAmounts(shinFootValue, 1.0F, 58.0F, -0.2F);
      float[] amounts2 = getAmounts(shinFootValue, -1.0F, 58.0F, -0.2F);
      float[] amounts3 = getAmounts(shinFootValue, 1.0F, 58.0F);
      float[] amounts4 = getAmounts(shinFootValue, -1.0F, 58.0F);
      this.shinFootForeEar = getClassForLatheLoftString(latheLoft, "eye_r", this.earL, shinFootValues[0], shinFootValues[1], shinFootValues[2]);
      this.classShinProgress = getClassForLatheLoftString(latheLoft, "eye_l", this.earL, amounts2[0], amounts2[1], amounts2[2]);
      float amountsShinFootTable = getAmountsShinFootTable(-2.6F);
      float[] amounts5 = getAmounts(amountsShinFootTable, 1.0F, 34.0F, -0.3F);
      float[] shinEarValues = getAmounts(amountsShinFootTable, -1.0F, 34.0F, -0.3F);
      this.classShinFoot = getClassForLatheLoftString(latheLoft, "ear_r", this.earL, amounts5[0], amounts5[1], amounts5[2]);
      this.foreEar = getClassForLatheLoftString(latheLoft, "ear_l", this.earL, shinEarValues[0], shinEarValues[1], shinEarValues[2]);
      this.toastASection = getClassForLatheLoftString(latheLoft, "toast_a", this.armL, -0.45F, 26.899998F, 0.7F);
      this.toastBSection = getClassForLatheLoftString(latheLoft, "toast_b", this.armL, 0.5F, 28.399998F, 3.15F);
      this.leverSection = getClassForLatheLoftString(latheLoft, "lever", this.armL, 7.4F, 20.8F, 3.55F);
      this.handR = getClassForLatheLoftString(latheLoft, "arm_r", this.armL, 7.3F, 19.3F, -0.2F);
      this.shinProgressForeEar = getClassForLatheLoftString(latheLoft, "hand_r", this.handR, 9.0F, 16.8F, -0.2F);
      this.handL = getClassForLatheLoftString(latheLoft, "arm_l", this.armL, -7.3F, 19.3F, -0.2F);
      this.handLeftSection = getClassForLatheLoftString(latheLoft, "hand_l", this.handL, -9.0F, 16.8F, -0.2F);
      this.shinR = getClassForLatheLoftString(latheLoft, "leg_r", this.body, 4.7F, 9.4F, 0.1F);
      this.shinRightSection = getClassForLatheLoftString(latheLoft, "shin_r", this.shinR, 4.7F, 4.8F, -0.1F);
      this.shinL = getClassForLatheLoftString(latheLoft, "leg_l", this.body, -4.7F, 9.4F, 0.1F);
      this.shinEarForeEar = getClassForLatheLoftString(latheLoft, "shin_l", this.shinL, -4.7F, 4.8F, -0.1F);
      latheLoft.getLatheLoft(this.handR);
      LoftLatheFlag.Region region = latheLoft.getMax(256, 144);
      handleBonecaAmbalabu3(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(96, 88);
      LoftLatheFlag.Region max3 = latheLoft.getMax(96, 88);
      LoftLatheFlag.Region max4 = latheLoft.getMax(64, 88);
      LoftLatheFlag.Region max5 = latheLoft.getMax(64, 88);
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu(), 41, 0.66F, 0.45F);
      handleBonecaAmbalabu6(max2.getClassBonecaAmbalabu());
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu(), 43, 0.32F, 0.2F);
      handleBonecaAmbalabu9(max3.getClassBonecaAmbalabu());
      handleBonecaAmbalabu(max4.getClassBonecaAmbalabu(), 45, 0.62F, 0.22F);
      handleBonecaAmbalabu4(max4.getClassBonecaAmbalabu());
      handleBonecaAmbalabu(max5.getClassBonecaAmbalabu(), 47, 0.5F, 0.22F);
      handleBonecaAmbalabu(max5.getClassBonecaAmbalabu(), -1.0F);
      LoftLatheFlag.Region max6 = latheLoft.getMax(72, 80);
      handleAmountsBoneca(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(96, 16);
      handleBonecaAmbalabu7(max7.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max8 = latheLoft.getMax(96, 64);
      handleBonecaAmbalabu5(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(64, 56);
      handle(max9.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max10 = latheLoft.getMax(48, 48);
      LoftLatheFlag.Region max11 = latheLoft.getMax(48, 48);
      LoftLatheFlag.Region max12 = latheLoft.getMax(64, 48);
      LoftLatheFlag.Region max13 = latheLoft.getMax(16, 48);
      LoftLatheFlag.Region max14 = latheLoft.getMax(48, 32);
      handleBonecaAmbalabu(max10.getClassBonecaAmbalabu(), 51, 0.78F, 0.87F, 0.95F);
      handleBonecaAmbalabu(max11.getClassBonecaAmbalabu(), 53, 0.06F, 0.15F, 0.86F, 0.94F);
      handleBonecaAmbalabu(max14.getClassBonecaAmbalabu(), 63, 0.12F, 0.26F);
      handleBonecaAmbalabu(max12.getClassBonecaAmbalabu(), 55);
      handleBonecaAmbalabu(max12.getClassBonecaAmbalabu(), 0.6F, 0.84F, 0.085F);
      max13.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(12168082), ForeShinUtils.getValueForInt(14208440))
         .getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(10260086, 0.22F), 4.0F, 1.0F, 0.0F)
         .getBonecaAmbalabu(0.04F, 3.0F, 57);
      LoftLatheFlag.Region max15 = latheLoft.getMax(96, 32);
      LoftLatheFlag.Region max16 = latheLoft.getMax(24, 24);
      LoftLatheFlag.Region max17 = latheLoft.getMax(40, 24);
      LoftLatheFlag.Region max18 = latheLoft.getMax(96, 16);
      handleBonecaAmbalabuForBonecaAmbalabu(max17.getClassBonecaAmbalabu());
      handleBonecaAmbalabu8(max18.getClassBonecaAmbalabu());
      handleBonecaAmbalabu(max15.getClassBonecaAmbalabu(), 59);
      handleBonecaAmbalabu(max15.getClassBonecaAmbalabu(), 0.55F, 1.02F, 0.09F);
      BonecaAmbalabu classBonecaAmbalabu = max16.getClassBonecaAmbalabu();
      classBonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(2039583))
         .getClass022(12.0F, 12.0F, 11.0F, 11.0F, 1.5F, ForeShinUtils.getValueForInt(3815994))
         .getClass02(12.0F, 12.0F, 12.0F, 2.5F, 2.2F, ForeShinUtils.getValueForInt(15263976));
      LoftLatheFlag.Region max19 = latheLoft.getMax(48, 88);
      handleBonecaAmbalabu(max19.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(max19);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max9);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max18);
      ShinBrr shinBrr = latheLoft.getMax(14804456);
      ShinBrr[] shinBrrs = new ShinBrr[]{
         latheLoft.getClassForRegion(max2), latheLoft.getClassForRegion(max3), latheLoft.getClassForRegion(max4), latheLoft.getClassForRegion(max5), latheLoft.getClassForRegion(max8), latheLoft.getMax(2763306)
      };
      ShinBrr shinBrr2 = latheLoft.getMax(2763306);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max6);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max10);
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max11);
      ShinBrr classForRegion11 = latheLoft.getClassForRegion(max14);
      ShinBrr classForRegion12 = latheLoft.getClassForRegion(max12);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max15);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max13);
      ShinBrr shinBrr3 = latheLoft.getMax(2039583);
      ShinBrr shinBrr4 = latheLoft.getMax(3815994);
      ShinBrr classForRegion15 = latheLoft.getClassForRegion(max16);
      ShinBrr classForRegion16 = latheLoft.getClassForRegion(max17);
      ShinBrr shinBrr5 = latheLoft.getMax(11571853);
      latheLoft.getLatheLoftForForeEar(this.armL);
      latheLoft.getLoftLathe(-7.4F, 9.6F, -4.6F, 7.4F, 22.8F, 5.8F, 1.8F, 4, shinBrrs, shinBrr);

      for (int i = -1; i <= 1; i += 2) {
         for (int j = -1; j <= 1; j += 2) {
            latheLoft.getDequeArrayArrayDeque().getLatheLoft(i * 5.6000004F, 0.0F, j < 0 ? -2.8F : 4.0F);
            latheLoft.getLatheLoft(new float[]{1.8599999F, 1.8599999F}, new float[]{11.400001F, 21.0F}, 12, classForRegion, null, null);
            latheLoft.getDequeArray();
         }
      }

      latheLoft.getLatheLoft(-7.6F, 8.6F, -4.7999997F, 7.6F, 10.5F, 6.0F, 0.9F, 2, shinBrr2);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(7.3F, 12.4F, 2.3F).getLatheLoft(-90.0F);
      latheLoft.getLatheLoft(1.0F, 0.88F, 0.0F, 0.75F, 12, shinBrr3, classForRegion15);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.leverSection);
      latheLoft.getLatheLoft(6.8F, 20.58F, 3.35F, 8.0F, 21.019999F, 3.75F, shinBrr3);
      latheLoft.getLoftLathe(7.5F, 20.05F, 2.4499998F, 10.0F, 21.55F, 4.65F, 0.55F, 2, new ShinBrr[]{shinBrr3, shinBrr3, shinBrr3, shinBrr3, shinBrr4, shinBrr3}, shinBrr3);

      for (int k = 0; k < 2; k++) {
         latheLoft.getLatheLoftForForeEar(k == 0 ? this.toastASection : this.toastBSection);
         latheLoft.getDequeArrayArrayDeque()
            .getLatheLoft(k == 0 ? -0.45F : 0.5F, 22.199999F + k * 1.5F, k == 0 ? 0.7F : 3.15F)
            .getLatheLoft(k == 0 ? -2.0F : 2.5F)
            .getLatheLoftLatheLoftAmountLoft(k == 0 ? -1.5F : 1.5F);
         latheLoft.getPolygonExtrude(bonecaTable, -0.75F, 0.75F, classForRegion7, classForRegion7, classForRegion8);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.earL);
      latheLoft.getLoftPath(pointsSpline, shinFootTable, SAMPLED_THICKNESSES, 24, classForRegion4, null, classForRegion5, 0.0F, -1.0F, 0.0F);
      handle(latheLoft, -4.3F, -5.5F, 0.3F, 16, classForRegion6);
      handle(latheLoft, classForRegion14);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handle(latheLoft, 1.0F, shinFootValues, amounts3);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      handle(latheLoft, -1.0F, amounts2, amounts4);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      handle(latheLoft, 1.0F, amounts5, classForRegion16, shinBrr5);
      latheLoft.getLatheLoftForForeEar(this.foreEar);
      handle(latheLoft, -1.0F, shinEarValues, classForRegion16, shinBrr5);
      handle(latheLoft, 1.0F, this.handR, this.shinProgressForeEar, classForRegion9, classForRegion11, classForRegion12);
      handle(latheLoft, -1.0F, this.handL, this.handLeftSection, classForRegion9, classForRegion11, classForRegion12);
      handleForLatheLoftFloat(latheLoft, 1.0F, this.shinR, this.shinRightSection, classForRegion9, classForRegion10, classForRegion13);
      handleForLatheLoftFloat(latheLoft, -1.0F, this.shinL, this.shinEarForeEar, classForRegion9, classForRegion10, classForRegion13);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue, float bonecaAmbalabuFactor, float bonecaAmbalabuLimit) {
      handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue);
      float classValues = bonecaAmbalabu.ambalabuBonecaValue * bonecaAmbalabuFactor;
      float bonecaAmbalabuValue4 = bonecaAmbalabu.ambalabuBonecaValue * 0.1F;
      float bonecaAmbalabuValue5 = bonecaAmbalabu.ambalabuBonecaValue * 0.2F;
      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForIntFloat(16777215, bonecaAmbalabuLimit), classValues, 0.0F, classValues + bonecaAmbalabuValue4, 0.0F, classValues + bonecaAmbalabuValue4 - bonecaAmbalabuValue5, bonecaAmbalabu.bonecaValue, classValues - bonecaAmbalabuValue5, bonecaAmbalabu.bonecaValue);
      bonecaAmbalabu.getClassForIntFloatArray(
         ForeShinUtils.getValueForIntFloat(16777215, bonecaAmbalabuLimit * 0.66F),
         classValues + bonecaAmbalabuValue4 * 1.6F,
         0.0F,
         classValues + bonecaAmbalabuValue4 * 2.1F,
         0.0F,
         classValues + bonecaAmbalabuValue4 * 2.1F - bonecaAmbalabuValue5,
         bonecaAmbalabu.bonecaValue,
         classValues + bonecaAmbalabuValue4 * 1.6F - bonecaAmbalabuValue5,
         bonecaAmbalabu.bonecaValue
      );
   }

   private static void handle(LatheLoft latheLoft, float shinEarValue, float[] shinEarValues, ShinBrr shinBrr, ShinBrr shinBrr2) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinEarValues[0], shinEarValues[1], shinEarValues[2]).getLatheLoft(-shinEarValue * 22.0F).getLatheLoft4(-shinEarValue * 14.0F).getLatheLoftLatheLoftAmountLoft(8.0F);
      latheLoft.getLatheProfile(
         new float[]{0.5F, 0.82F, 0.78F, 0.58F, 0.3F, 0.05F}, new float[]{-0.4F, 0.4F, 1.3F, 2.2F, 3.0F, 3.4F}, 8, 1.0F, 0.62F, shinBrr, null, null
      );
      latheLoft.getLatheLoft(0.0F, 1.5F, -0.38F, 0.45F, 1.25F, 0.18F, 8, shinBrr2);
      latheLoft.getDequeArray();
   }

   private static void handle(LatheLoft latheLoft, float shinEarValue, ForeEar foreEar, ForeEar foreEar2, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      latheLoft.getLatheLoftForForeEar(foreEar);
      float[] pointsSpline = LatheLoft.getPointsSpline(1, shinEarValue * 6.4F, 19.3F, -0.2F, shinEarValue * 8.3F, 19.0F, -0.2F, shinEarValue * 9.0F, 17.0F, -0.2F);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 2.05F, 2.0F, 1.9F), 10, shinBrr, false);
      latheLoft.getLatheLoft(shinEarValue * 9.0F, 16.8F, -0.2F, 1.9F, 8, shinBrr);
      latheLoft.getLatheLoftForForeEar(foreEar2);
      float[] latheLoftValues = LatheLoft.getPointsSpline(1, shinEarValue * 9.0F, 16.8F, -0.2F, shinEarValue * 9.15F, 15.4F, -0.25F, shinEarValue * 9.25F, 14.199999F, -0.3F);
      latheLoft.getLatheLoft(latheLoftValues, LatheLoft.getAmounts(LatheLoft.getValue(latheLoftValues), 1.85F, 1.8F, 1.75F), 10, shinBrr2, false);
      latheLoft.getLatheLoft(shinEarValue * 9.25F, 13.299999F, -0.35000002F, 1.9F, 1.6F, 2.0F, 10, shinBrr3);
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuLimit, float bonecaAmbalabuValue) {
      float bonecaAmbalabuFactor = bonecaAmbalabuLimit > 0.0F ? bonecaAmbalabuValue / 360.0F : 1.0F - bonecaAmbalabuValue / 360.0F;
      return 0.5F + bonecaAmbalabuFactor * (bonecaAmbalabu.ambalabuBonecaValue - 1);
   }

   static {
      float[] floats = new float[3 * SPINE_Z.length];

      for (int i = 0; i < SPINE_Z.length; i++) {
         floats[3 * i + 1] = SPINE_Y[i];
         floats[3 * i + 2] = SPINE_Z[i];
      }

      pointsSpline = LatheLoft.getPointsSpline(2, floats);
      int shinEarSize = LatheLoft.getValue(pointsSpline);
      shinFootTable = LatheLoft.getAmounts(shinEarSize, TOAST_WIDTHS);
      SAMPLED_THICKNESSES = LatheLoft.getAmounts(shinEarSize, TOAST_THICKNESSES);
      SPINE_CUMULATIVE_LENGTHS = new float[shinEarSize];
      SPINE_TANGENTS = new float[3 * shinEarSize];
      SPINE_NORMALS = new float[3 * shinEarSize];
      float shinEarFactor = 0.0F;
      float shinEarFactor2 = -1.0F;
      float shinEarFactor3 = 0.0F;

      for (int j = 0; j < shinEarSize; j++) {
         int shinEarValue = Math.max(0, j - 1);
         int shinEarValue2 = Math.min(shinEarSize - 1, j + 1);
         float shinEarFactor4 = pointsSpline[3 * shinEarValue2] - pointsSpline[3 * shinEarValue];
         float shinEarFactor5 = pointsSpline[3 * shinEarValue2 + 1] - pointsSpline[3 * shinEarValue + 1];
         float shinEarFactor6 = pointsSpline[3 * shinEarValue2 + 2] - pointsSpline[3 * shinEarValue + 2];
         float distance = (float)Math.sqrt(shinEarFactor4 * shinEarFactor4 + shinEarFactor5 * shinEarFactor5 + shinEarFactor6 * shinEarFactor6);
         float shinEarFactor7 = shinEarFactor4 / distance;
         float shinEarFactor8 = shinEarFactor5 / distance;
         float shinEarFactor9 = shinEarFactor6 / distance;
         float shinEarFactor10 = shinEarFactor * shinEarFactor7 + shinEarFactor2 * shinEarFactor8 + shinEarFactor3 * shinEarFactor9;
         float shinEarFactor11 = shinEarFactor - shinEarFactor10 * shinEarFactor7;
         float shinEarFactor12 = shinEarFactor2 - shinEarFactor10 * shinEarFactor8;
         float shinEarFactor13 = shinEarFactor3 - shinEarFactor10 * shinEarFactor9;
         float distance2 = (float)Math.sqrt(shinEarFactor11 * shinEarFactor11 + shinEarFactor12 * shinEarFactor12 + shinEarFactor13 * shinEarFactor13);
         shinEarFactor = shinEarFactor11 / distance2;
         shinEarFactor2 = shinEarFactor12 / distance2;
         shinEarFactor3 = shinEarFactor13 / distance2;
         SPINE_TANGENTS[3 * j] = shinEarFactor;
         SPINE_TANGENTS[3 * j + 1] = shinEarFactor2;
         SPINE_TANGENTS[3 * j + 2] = shinEarFactor3;
         SPINE_NORMALS[3 * j] = shinEarFactor8 * shinEarFactor3 - shinEarFactor9 * shinEarFactor2;
         SPINE_NORMALS[3 * j + 1] = shinEarFactor9 * shinEarFactor - shinEarFactor7 * shinEarFactor3;
         SPINE_NORMALS[3 * j + 2] = shinEarFactor7 * shinEarFactor2 - shinEarFactor8 * shinEarFactor;
         if (j > 0) {
            float shinEarFactor14 = pointsSpline[3 * j] - pointsSpline[3 * j - 3];
            float shinEarFactor15 = pointsSpline[3 * j + 1] - pointsSpline[3 * j - 2];
            float shinEarFactor16 = pointsSpline[3 * j + 2] - pointsSpline[3 * j - 1];
            SPINE_CUMULATIVE_LENGTHS[j] = SPINE_CUMULATIVE_LENGTHS[j - 1] + (float)Math.sqrt(shinEarFactor14 * shinEarFactor14 + shinEarFactor15 * shinEarFactor15 + shinEarFactor16 * shinEarFactor16);
         }
      }

      bonecaTable = getAmounts();
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(9801866))
         .getBonecaAmbalabu(0.05F, 6.0F, 21)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(10522494), 0.25F, 4.0F, 23);
      bonecaAmbalabu.getClassForFloatInt(
         getBonecaAmbalabu(bonecaAmbalabu, -0.45F),
         getAmount3(bonecaAmbalabu, 0.8F),
         getBonecaAmbalabu(bonecaAmbalabu, 0.0F),
         getAmount3(bonecaAmbalabu, 0.7F),
         getBonecaAmbalabu(bonecaAmbalabu, 0.45F),
         getAmount3(bonecaAmbalabu, 0.8F),
         1.4F,
         ForeShinUtils.getValueForIntFloat(7104611, 0.3F)
      );

      for (int i = -1; i <= 1; i += 2) {
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabu(bonecaAmbalabu, i * 0.28F),
            getAmount3(bonecaAmbalabu, 0.5F),
            getBonecaAmbalabu(bonecaAmbalabu, i * 0.52F),
            getAmount3(bonecaAmbalabu, 0.52F),
            getBonecaAmbalabu(bonecaAmbalabu, i * 0.76F),
            getAmount3(bonecaAmbalabu, 0.26F),
            3.2F,
            ForeShinUtils.getValueForInt(4078135)
         );
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabu(bonecaAmbalabu, i * 0.3F),
            getAmount3(bonecaAmbalabu, 0.6F),
            getBonecaAmbalabu(bonecaAmbalabu, i * 0.52F),
            getAmount3(bonecaAmbalabu, 0.62F),
            getBonecaAmbalabu(bonecaAmbalabu, i * 0.72F),
            getAmount3(bonecaAmbalabu, 0.42F),
            1.4F,
            ForeShinUtils.getValueForIntFloat(11907242, 0.5F)
         );
      }

      float angle = (float)Math.toRadians(88.0);
      float radians2 = (float)Math.toRadians(132.0);
      float angle3 = angle + 0.3F * (radians2 - angle);
      float angle4 = radians2 - 0.22F * (radians2 - angle);
      float cos = (float)Math.cos(angle);
      float cos2 = (float)Math.cos(radians2);
      float cos3 = (float)Math.cos(angle3);
      float cos4 = (float)Math.cos(angle4);
      float sin = (float)Math.sin(angle);
      float shinEarFactor = (float)Math.sin(radians2);
      float sin3 = (float)Math.sin(angle3);
      float sin4 = (float)Math.sin(angle4);
      float shinEarValue = -0.4F - cos;
      float shinEarValue2 = -1.9F - cos2;
      float shinEarValue3 = -0.9F - cos3;
      float shinEarValue4 = -1.66F - cos4;
      int shinEarLimit = 16;
      float[] floats = new float[4 * (shinEarLimit + 1)];
      float[] floats2 = new float[4 * (shinEarLimit + 1)];
      float[] classValues = new float[4 * (shinEarLimit + 1)];
      float[] floats4 = new float[shinEarLimit + 1];
      float[] floats5 = new float[shinEarLimit + 1];
      float[] floats6 = new float[shinEarLimit + 1];
      float[] floats7 = new float[shinEarLimit + 1];

      for (int j = 0; j <= shinEarLimit; j++) {
         float shinEarValue5 = (float)j / shinEarLimit;
         float[] shinEarValues = getAmounts(sin, cos, 0.0F, shinEarValue, -sin, cos, shinEarValue5);
         float[] amounts2 = getAmounts(shinEarFactor, cos2, 0.0F, shinEarValue2, -shinEarFactor, cos2, shinEarValue5);
         float[] amounts3 = getAmounts(sin3, cos3, 0.0F, shinEarValue3, -sin3, cos3, shinEarValue5);
         float[] amounts4 = getAmounts(sin4, cos4, 0.0F, shinEarValue4, -sin4, cos4, shinEarValue5);
         floats4[j] = getBonecaAmbalabu(bonecaAmbalabu, shinEarValues[0]);
         floats5[j] = getAmount3(bonecaAmbalabu, shinEarValues[1]);
         floats6[j] = getBonecaAmbalabu(bonecaAmbalabu, amounts2[0]);
         floats7[j] = getAmount3(bonecaAmbalabu, amounts2[1]);
         floats[2 * j] = floats4[j];
         floats[2 * j + 1] = floats5[j];
         floats[4 * (shinEarLimit + 1) - 2 - 2 * j] = floats6[j];
         floats[4 * (shinEarLimit + 1) - 1 - 2 * j] = floats7[j];
         floats2[2 * j] = floats4[j];
         floats2[2 * j + 1] = floats5[j];
         floats2[4 * (shinEarLimit + 1) - 2 - 2 * j] = getBonecaAmbalabu(bonecaAmbalabu, amounts3[0]);
         floats2[4 * (shinEarLimit + 1) - 1 - 2 * j] = getAmount3(bonecaAmbalabu, amounts3[1]);
         classValues[2 * j] = getBonecaAmbalabu(bonecaAmbalabu, amounts4[0]);
         classValues[2 * j + 1] = getAmount3(bonecaAmbalabu, amounts4[1]);
         classValues[4 * (shinEarLimit + 1) - 2 - 2 * j] = floats6[j];
         classValues[4 * (shinEarLimit + 1) - 1 - 2 * j] = floats7[j];
      }

      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(3877931), floats);
      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(15921126), floats2);
      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(15921126), classValues);

      for (int k = 1; k < 5; k++) {
         float shinEarValue6 = k / 5.0F;
         float[] amounts5 = getAmounts(sin, cos, 0.0F, shinEarValue, -sin, cos, shinEarValue6);
         float[] amounts6 = getAmounts(sin3, cos3, 0.0F, shinEarValue3, -sin3, cos3, shinEarValue6);
         float[] amounts7 = getAmounts(sin4, cos4, 0.0F, shinEarValue4, -sin4, cos4, shinEarValue6);
         float[] amounts8 = getAmounts(shinEarFactor, cos2, 0.0F, shinEarValue2, -shinEarFactor, cos2, shinEarValue6);
         bonecaAmbalabu.getClass02(
            getBonecaAmbalabu(bonecaAmbalabu, amounts5[0]),
            getAmount3(bonecaAmbalabu, amounts5[1]),
            getBonecaAmbalabu(bonecaAmbalabu, amounts6[0]),
            getAmount3(bonecaAmbalabu, amounts6[1]),
            0.7F,
            ForeShinUtils.getValueForInt(14275527)
         );
         bonecaAmbalabu.getClass02(
            getBonecaAmbalabu(bonecaAmbalabu, amounts7[0]),
            getAmount3(bonecaAmbalabu, amounts7[1]),
            getBonecaAmbalabu(bonecaAmbalabu, amounts8[0]),
            getAmount3(bonecaAmbalabu, amounts8[1]),
            0.7F,
            ForeShinUtils.getValueForInt(14275527)
         );
      }

      bonecaAmbalabu.getClassForFloatArrayFloat(floats6, floats7, 2.6F, ForeShinUtils.getValueForInt(5920338));
      bonecaAmbalabu.getClassForFloatArrayFloat(floats4, floats5, 3.0F, ForeShinUtils.getValueForInt(5920338));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue) {
      float classValue = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, bonecaAmbalabuValue, -0.2F);
      float amountForBonecaAmbalabuFloat = getAmountForBonecaAmbalabuFloat(bonecaAmbalabu, 19.3F);
      float bonecaAmbalabuValue3 = (bonecaAmbalabu.ambalabuBonecaValue - 1) / 6.8F;
      bonecaAmbalabu.getClass022(classValue, amountForBonecaAmbalabuFloat, 2.6F * bonecaAmbalabuValue3, 2.6F * bonecaAmbalabuValue3, ForeShinUtils.getValueForIntFloat(5593953, 0.6F));
      bonecaAmbalabu.getClass022(classValue, amountForBonecaAmbalabuFloat, 2.35F * bonecaAmbalabuValue3, 2.35F * bonecaAmbalabuValue3, ForeShinUtils.getValueForInt(2763306));
      bonecaAmbalabu.getClass022(classValue, amountForBonecaAmbalabuFloat, 2.35F * bonecaAmbalabuValue3, 2.35F * bonecaAmbalabuValue3, 2.2F, ForeShinUtils.getValueForInt(4540492));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      handleBonecaAmbalabu(bonecaAmbalabu, 49);

      for (int i = 0; i < 4; i++) {
         float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * (0.125F + 0.25F * i);
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuValue - 2.5F, 0.0F, bonecaAmbalabuValue + 2.5F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(5199452, 0.18F));
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuValue - 0.9F, 0.0F, bonecaAmbalabuValue + 0.9F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(16777215, 0.55F));
      }
   }

   @Override
   public int getValue() {
      return 1;
   }

   private static float getAmount(float angle, float angle2) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      float cos2 = (float)Math.cos(angle2);
      float sin2 = (float)Math.sin(angle2);
      float shinEarBound = 0.0F;

      for (int i = 0; i < 3; i++) {
         float shinEarFactor = -0.35F + (i - 1) * 3.0F;
         float shinEarFactor2 = -4.5999994F - 4.8F * cos2 - shinEarFactor * sin2;
         float shinEarFactor3 = -0.2F - 4.8F * sin2 + shinEarFactor * cos2;
         shinEarBound = Math.min(shinEarBound, shinEarFactor2 * cos - shinEarFactor3 * sin);
      }

      return shinEarBound;
   }

   @Override
   public String formatTungDinString() {
      return "Rhino Toasterino";
   }

   private static float getAmountForBonecaAmbalabuFloat(BonecaAmbalabu bonecaAmbalabu, float shinEarValue) {
      return 0.5F + (21.0F - shinEarValue) / 9.599999F * (bonecaAmbalabu.bonecaValue - 1);
   }

   private static void handleAmountsBoneca(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaValue = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      float bonecaValue2 = bonecaAmbalabu.bonecaValue * 0.4F;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaFactor = (j + 0.5F - bonecaValue) / (bonecaAmbalabu.ambalabuBonecaValue * 0.5F);
            float bonecaFactor2 = (i + 0.5F - bonecaValue2) / (bonecaAmbalabu.bonecaValue * 0.5F);
            float distance = (float)Math.sqrt(bonecaFactor * bonecaFactor + bonecaFactor2 * bonecaFactor2);
            bonecaAmbalabu.getBonecaAmbalabu(
               j, i, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15915420), ForeShinUtils.getValueForInt(13670988), getAmount((distance - 0.25F) / 0.8F))
            );
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15254138), 0.35F, 5.0F, 61);
      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(12158019, 0.55F), 3.2F, 0.7F, 63);
      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(16509368, 0.6F), 2.4F, 0.6F, 65);
      int bonecaLimit = bonecaTable.length / 2;
      float bonecaBound = -99.0F;
      float wrapDegreesBound = 99.0F;
      float pilotStatusBound = -99.0F;
      float min4 = 99.0F;

      for (int k = 0; k < bonecaLimit; k++) {
         bonecaBound = Math.max(bonecaBound, bonecaTable[2 * k]);
         wrapDegreesBound = Math.min(wrapDegreesBound, bonecaTable[2 * k]);
         pilotStatusBound = Math.max(pilotStatusBound, bonecaTable[2 * k + 1]);
         min4 = Math.min(min4, bonecaTable[2 * k + 1]);
      }

      float[] floats = new float[bonecaLimit + 1];
      float[] floats2 = new float[bonecaLimit + 1];

      for (int m = 0; m <= bonecaLimit; m++) {
         int bonecaValue3 = m % bonecaLimit;
         floats[m] = 0.5F + (bonecaBound - bonecaTable[2 * bonecaValue3]) / (bonecaBound - wrapDegreesBound) * (bonecaAmbalabu.ambalabuBonecaValue - 1);
         floats2[m] = 0.5F + (pilotStatusBound - bonecaTable[2 * bonecaValue3 + 1]) / (pilotStatusBound - min4) * (bonecaAmbalabu.bonecaValue - 1);
      }

      bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 7.5F, ForeShinUtils.getValueForInt(11036202));
      bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 3.0F, ForeShinUtils.getValueForInt(9327648));
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(9078401))
         .getBonecaAmbalabu(0.06F, 4.0F, 71)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(7104611), 0.2F, 3.0F, 73);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.16F, ForeShinUtils.getValueForIntFloat(6051923, 0.8F));

      for (int i = 0; i < 16; i++) {
         float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * (i + 0.5F) / 16.0F;
         float bonecaAmbalabuValue2 = (i % 3 - 1) * 1.2F;
         bonecaAmbalabu.getClass02(bonecaAmbalabuValue, 0.0F, bonecaAmbalabuValue + bonecaAmbalabuValue2, bonecaAmbalabu.bonecaValue * (0.22F + 0.1F * (i % 2)), 1.1F, ForeShinUtils.getValueForIntFloat(4143927, 0.85F));
      }
   }

   @Override
   public float getAmountFloat() {
      return 2.0F;
   }

   private static float getBonecaAmbalabuForBonecaAmbalabuFloat(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuLimit, float bonecaAmbalabuValue) {
      float bonecaAmbalabuFactor = bonecaAmbalabuLimit > 0.0F ? (4.0F - bonecaAmbalabuValue) / 6.8F : (bonecaAmbalabuValue - -2.8F) / 6.8F;
      return 0.5F + bonecaAmbalabuFactor * (bonecaAmbalabu.ambalabuBonecaValue - 1);
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
            float bonecaAmbalabuValue2 = (bonecaAmbalabuValue <= 0.5F ? bonecaAmbalabuValue : 1.0F - bonecaAmbalabuValue) * 360.0F;
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue(ForeShinUtils.getValueForInt(9341573), 0.9F + 0.14F * bonecaAmbalabuValue2 / 180.0F));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 14.0F, 3)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(7104611), 0.3F, 9.0F, 5)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(10522494), 0.28F, 6.0F, 8)
         .getBonecaAmbalabu(0.035F, 3.0F, 12);
      handle(bonecaAmbalabu, -2.4F, 0.0F, 80.0F, 0.2F, 2.6F, 2);
      handle(bonecaAmbalabu, -3.1F, 0.0F, 60.0F, 0.25F, 2.2F, 3);
      handle(bonecaAmbalabu, -5.9F, 0.0F, 24.0F, 0.25F, 1.8F, 4);

      for (int k = -1; k <= 1; k += 2) {
         for (int m = 0; m < 3; m++) {
            float bonecaAmbalabuValue3 = -8.8F - m * 0.75F;
            bonecaAmbalabu.getClassForFloatInt(
               getBonecaAmbalabu(bonecaAmbalabu, k, 62.0F),
               getAmount(bonecaAmbalabu, bonecaAmbalabuValue3),
               getBonecaAmbalabu(bonecaAmbalabu, k, 74.0F),
               getAmount(bonecaAmbalabu, bonecaAmbalabuValue3 - 0.35F),
               getBonecaAmbalabu(bonecaAmbalabu, k, 88.0F),
               getAmount(bonecaAmbalabu, bonecaAmbalabuValue3 - 0.15F),
               1.6F,
               ForeShinUtils.getValueForIntFloat(7104611, 0.6F)
            );
         }

         for (int p = 0; p < 4; p++) {
            bonecaAmbalabu.getClass022(
               getBonecaAmbalabu(bonecaAmbalabu, k, 108.0F), getAmount(bonecaAmbalabu, -6.6F), 18.0F - p * 3.0F, 12.0F - p * 2.0F, ForeShinUtils.getValueForIntFloat(10522494, 0.18F)
            );
         }

         float classValue = getBonecaAmbalabu(bonecaAmbalabu, k, 58.0F);
         float amountForFloat = getAmount(bonecaAmbalabu, -6.3F);
         bonecaAmbalabu.getClass022(classValue, amountForFloat, 15.0F, 13.0F, ForeShinUtils.getValueForIntFloat(7104611, 0.35F));
         bonecaAmbalabu.getClass022(classValue, amountForFloat, 12.5F, 11.0F, ForeShinUtils.getValueForIntFloat(4867651, 0.55F));
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabu(bonecaAmbalabu, k, 44.0F),
            getAmount(bonecaAmbalabu, -4.9F),
            getBonecaAmbalabu(bonecaAmbalabu, k, 34.0F),
            getAmount(bonecaAmbalabu, -6.3F),
            getBonecaAmbalabu(bonecaAmbalabu, k, 45.0F),
            getAmount(bonecaAmbalabu, -7.8F),
            2.6F,
            ForeShinUtils.getValueForIntFloat(7104611, 0.9F)
         );
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabu(bonecaAmbalabu, k, 39.0F),
            getAmount(bonecaAmbalabu, -4.7000003F),
            getBonecaAmbalabu(bonecaAmbalabu, k, 28.0F),
            getAmount(bonecaAmbalabu, -6.3F),
            getBonecaAmbalabu(bonecaAmbalabu, k, 40.0F),
            getAmount(bonecaAmbalabu, -8.0F),
            1.4F,
            ForeShinUtils.getValueForIntFloat(11907242, 0.5F)
         );
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabu(bonecaAmbalabu, k, 75.0F),
            getAmount(bonecaAmbalabu, -5.1000004F),
            getBonecaAmbalabu(bonecaAmbalabu, k, 65.0F),
            getAmount(bonecaAmbalabu, -6.3F),
            getBonecaAmbalabu(bonecaAmbalabu, k, 75.0F),
            getAmount(bonecaAmbalabu, -7.7000003F),
            2.2F,
            ForeShinUtils.getValueForIntFloat(7104611, 0.7F)
         );
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabu(bonecaAmbalabu, k, 79.0F),
            getAmount(bonecaAmbalabu, -5.0F),
            getBonecaAmbalabu(bonecaAmbalabu, k, 69.0F),
            getAmount(bonecaAmbalabu, -6.3F),
            getBonecaAmbalabu(bonecaAmbalabu, k, 79.0F),
            getAmount(bonecaAmbalabu, -7.8F),
            1.4F,
            ForeShinUtils.getValueForIntFloat(11907242, 0.45F)
         );

         for (int q = 0; q < 3; q++) {
            float bonecaAmbalabuValue5 = 52.0F + q * 7.0F;
            bonecaAmbalabu.getClass02(
               getBonecaAmbalabu(bonecaAmbalabu, k, bonecaAmbalabuValue5),
               getAmount(bonecaAmbalabu, -5.1000004F),
               getBonecaAmbalabu(bonecaAmbalabu, k, bonecaAmbalabuValue5 + (q - 1) * 4.0F),
               getAmount(bonecaAmbalabu, -4.4F),
               1.2F,
               ForeShinUtils.getValueForIntFloat(7104611, 0.55F)
            );
         }
      }

      for (float shinEarValue : new float[]{-11.0F, -7.9F}) {
         float classDivisor = shinEarValue < -9.0F ? 16.0F : 11.0F;
         bonecaAmbalabu.getClass022(0.5F, getAmount(bonecaAmbalabu, shinEarValue), classDivisor, classDivisor * 0.85F, ForeShinUtils.getValueForIntFloat(7104611, 0.35F));
         bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue - 0.5F, getAmount(bonecaAmbalabu, shinEarValue), classDivisor, classDivisor * 0.85F, ForeShinUtils.getValueForIntFloat(7104611, 0.35F));
      }

      handleAmounts(bonecaAmbalabu);
   }

   @Override
   public String formatNoteBlock() {
      return "block.note_block.bell";
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float shinProgressFactor2 = (float)Math.cos(angle) * shinProgressFactor;
      float shinProgressFactor3 = (float)Math.sin(angle) * shinProgressFactor;
      float shinProgressFactor4 = (float)Math.sin(angle + 0.8F) * shinProgressFactor;
      float shinProgressFactor5 = (float)Math.pow(Math.abs(Math.cos(angle)), 3.0) * shinProgressFactor;
      float shinProgressValue = (float)Math.pow(Math.abs(Math.cos(angle - 0.35F)), 3.0) * shinProgressFactor;
      float shinProgressFactor6 = 0.36F;
      float shinProgressTotal = shinProgressFactor6 * shinProgressFactor2;
      float rocketsElytraBound = -shinProgressFactor6 * shinProgressFactor2;
      float shinProgressTotal3 = -poseProgress.getAnimationProgress(3.6415927F) * 0.4F - 0.5F * Math.max(0.0F, shinProgressTotal) - 0.3F * Math.max(0.0F, -shinProgressTotal);
      float shinProgressTotal4 = -poseProgress.getAnimationProgress(0.5F) * 0.4F - 0.5F * Math.max(0.0F, rocketsElytraBound) - 0.3F * Math.max(0.0F, -rocketsElytraBound);
      if (poseProgress.sneakingPose) {
         shinProgressTotal += 0.7F;
         rocketsElytraBound += 0.7F;
         shinProgressTotal3 -= 0.7F;
         shinProgressTotal4 -= 0.7F;
      }

      if (poseProgress.vehicle) {
         shinProgressTotal = 1.4F;
         rocketsElytraBound = 1.4F;
         shinProgressTotal3 = -0.3F;
         shinProgressTotal4 = -0.3F;
      } else {
         foreShinScale.getAmountsForeShin2(this.body, 0.0F, -(9.4F + Math.min(getAmount(shinProgressTotal, shinProgressTotal3), getAmount(rocketsElytraBound, shinProgressTotal4))) * 1.07F, 0.0F);
      }

      foreShinScale.getAmounts(this.shinR, shinProgressTotal, poseProgress.vehicle ? 0.1F : 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinL, rocketsElytraBound, poseProgress.vehicle ? -0.1F : 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinRightSection, shinProgressTotal3, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinEarForeEar, shinProgressTotal4, 0.0F, 0.0F);
      foreShinScale.getAmountsForeShin2(this.armL, 0.0F, -1.6F * shinProgressFactor5, 0.0F);
      foreShinScale.getAmounts(this.armL, 0.0F, 0.09F * shinProgressFactor2, -0.28F * shinProgressFactor4);
      foreShinScale.getAmounts(this.handR, -0.6F * shinProgressFactor2, 0.0F, 0.22F + 0.1F * shinProgressFactor4 + 0.03F * poseProgress.getAgeCode(70.0F, 0.0F));
      foreShinScale.getAmounts(this.handL, 0.6F * shinProgressFactor2, 0.0F, -0.22F + 0.1F * shinProgressFactor4 - 0.03F * poseProgress.getAgeCode(70.0F, 1.0F));
      foreShinScale.getAmounts(this.shinProgressForeEar, 0.15F + 0.2F * shinProgressFactor, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.handLeftSection, 0.15F + 0.2F * shinProgressFactor, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.earL, 0.05F * shinProgressFactor5 + 0.025F * poseProgress.getAgeCode(90.0F, 0.5F), 0.0F, 0.1F * shinProgressFactor4);
      foreShinScale.getAmountsForeShin2(this.toastASection, 0.0F, 1.6F * shinProgressFactor5, 0.0F);
      foreShinScale.getAmountsForeShin2(this.toastBSection, 0.0F, 1.6F * shinProgressValue, 0.0F);
      foreShinScale.getAmounts(this.toastASection, 0.0F, 0.0F, 0.05F * shinProgressFactor3);
      foreShinScale.getAmounts(this.toastBSection, 0.0F, 0.0F, -0.05F * shinProgressFactor3);
      foreShinScale.getAmounts(this.classShinFoot, 0.08F * poseProgress.getAgeCode(60.0F, 0.0F), 0.0F, -0.18F * shinProgressFactor5);
      foreShinScale.getAmounts(this.foreEar, 0.08F * poseProgress.getAgeCode(60.0F, 2.0F), 0.0F, 0.18F * shinProgressFactor5);
      float shinProgressFactor7 = 1.0F - Math.min(1.0F, shinProgressFactor / 0.3F);
      float shinProgressBound = 0.0F;
      if (shinProgressFactor7 > 0.0F && poseProgress.swingProgress <= 0.0F) {
         float shinProgressLimit = (poseProgress.age + (poseProgress.hashCode & 127) * 1.7F) % 150.0F;
         float shinProgressFactor8 = 0.0F;
         float shinProgressFactor9 = 0.0F;
         float shinProgressFactor10 = 0.0F;
         float shinProgressFactor11 = 0.0F;
         if (shinProgressLimit >= 105.0F && shinProgressLimit < 113.0F) {
            float shinProgressFactor12 = getAmount((shinProgressLimit - 105.0F) / 8.0F);
            shinProgressFactor8 = -5.0F * shinProgressFactor12;
            shinProgressFactor9 = -2.4F * shinProgressFactor12;
            shinProgressFactor11 = shinProgressFactor9;
         } else if (shinProgressLimit >= 113.0F && shinProgressLimit < 128.0F) {
            shinProgressFactor8 = -5.0F;
            shinProgressFactor9 = -2.4F + 0.12F * (float)Math.sin(poseProgress.age * 2.7F);
            shinProgressFactor11 = -2.4F + 0.12F * (float)Math.sin(poseProgress.age * 2.7F + 1.3F);
            foreShinScale.getAmountsBoneCount(this.armL, 0.0F, 0.0F, 0.012F * (float)Math.sin(poseProgress.age * 3.1F));
         } else if (shinProgressLimit >= 128.0F) {
            float shinProgressValue2 = (shinProgressLimit - 128.0F) / 22.0F;
            float max3 = Math.max(0.0F, (shinProgressLimit - 129.5F) / 20.5F);
            shinProgressFactor8 = -5.0F * (1.0F - getAmount(shinProgressValue2 * 6.0F));
            shinProgressFactor9 = -2.4F * (1.0F - shinProgressValue2) + 36.0F * shinProgressValue2 * (1.0F - shinProgressValue2);
            shinProgressFactor11 = -2.4F * (1.0F - max3) + 42.0F * max3 * (1.0F - max3);
            shinProgressFactor10 = 0.3F * (float)Math.sin(shinProgressValue2 * 6.2832F);
            shinProgressBound = (float)Math.sin(Math.min(1.0F, shinProgressValue2 * 1.2F) * Math.PI);
         }

         foreShinScale.getAmountsForeShin2(this.leverSection, 0.0F, shinProgressFactor8 * shinProgressFactor7, 0.0F);
         foreShinScale.getAmountsForeShin2(this.toastASection, 0.0F, shinProgressFactor9 * shinProgressFactor7, 0.0F);
         foreShinScale.getAmountsForeShin2(this.toastBSection, 0.0F, shinProgressFactor11 * shinProgressFactor7, 0.0F);
         foreShinScale.getAmountsBoneCount(this.toastASection, shinProgressFactor10 * shinProgressFactor7, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.toastBSection, -shinProgressFactor10 * shinProgressFactor7, 0.0F, 0.0F);
         shinProgressBound *= shinProgressFactor7;
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressFactor13 = swingProgress < 0.2F ? getAmount(swingProgress / 0.2F) : 1.0F - getAmount((swingProgress - 0.2F) / 0.15F);
         float shinProgressValue4 = swingProgress < 0.2F ? 0.0F : (swingProgress < 0.4F ? getAmount((swingProgress - 0.2F) / 0.2F) : 1.0F - getAmount((swingProgress - 0.4F) / 0.6F));
         foreShinScale.getAmountsBoneCount(this.armL, 0.1F * shinProgressFactor13 - 0.18F * shinProgressValue4, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.earL, -0.35F * shinProgressFactor13 + 0.45F * shinProgressValue4, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.earL, 0.0F, -0.3F * shinProgressFactor13 + 0.3F * shinProgressValue4, -0.6F * shinProgressFactor13 - 2.6F * shinProgressValue4);
         float shinProgressValue5 = getAmount((swingProgress - 0.55F) / 0.2F) * (1.0F - getAmount((swingProgress - 0.88F) / 0.12F));
         foreShinScale.getAmounts(this.handR, -0.5F * shinProgressFactor13 + 1.4F * shinProgressValue4 + 2.1F * shinProgressValue5, 0.0F, 0.3F + 0.2F * shinProgressValue5);
         foreShinScale.getAmounts(this.handL, -0.5F * shinProgressFactor13 + 1.4F * shinProgressValue4 + 2.1F * shinProgressValue5, 0.0F, -0.3F - 0.2F * shinProgressValue5);
         foreShinScale.getAmounts(this.shinProgressForeEar, 0.5F * shinProgressValue5, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.handLeftSection, 0.5F * shinProgressValue5, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.leverSection, 0.0F, -5.0F * shinProgressFactor13, 0.0F);
         float max14 = Math.max(0.0F, Math.min(1.0F, (swingProgress - 0.18F) / 0.82F));
         float max6 = Math.max(0.0F, Math.min(1.0F, (swingProgress - 0.26F) / 0.74F));
         foreShinScale.getAmountsForeShin2(this.toastASection, -12.8F * max14 * (1.0F - max14), -2.4F * shinProgressFactor13 + 40.0F * max14 * (1.0F - max14), 0.0F);
         foreShinScale.getAmountsForeShin2(this.toastBSection, 12.8F * max6 * (1.0F - max6), -2.4F * shinProgressFactor13 + 48.0F * max6 * (1.0F - max6), 0.0F);
         foreShinScale.getAmountsBoneCount(this.toastASection, -getAmount((max14 - 0.12F) / 0.45F) * 6.2832F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.toastBSection, -getAmount((max6 - 0.12F) / 0.45F) * 12.5664F, 0.0F, 0.0F);
         shinProgressBound = Math.max(shinProgressBound, (float)Math.sin(max14 * Math.PI));
      }

      float foreShinValue = Math.max(-0.35F, Math.min(0.35F, poseProgress.shinProgressAmount * 0.6F));
      float max8 = Math.max(-0.6F, Math.min(0.6F, poseProgress.headYawRadians * 0.7F));
      foreShinScale.getAmountsBoneCount(this.earL, foreShinValue, max8, 0.0F);
      float shinProgressDivisor = !poseProgress.inPose && !((poseProgress.age + (poseProgress.hashCode & 63)) % 97.0F > 92.0F) ? 1.0F - 0.45F * shinProgressBound : 0.12F;
      foreShinScale.getAmountsForForeEarFloat(this.shinFootForeEar, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.classShinProgress, 1.0F, shinProgressDivisor, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.earL, 1.0F + 0.08F * shinProgressBound, 1.0F, 1.0F);
      foreShinScale.getAmountsBoneCount(this.classShinFoot, 0.0F, 0.0F, -0.25F * shinProgressBound);
      foreShinScale.getAmountsBoneCount(this.foreEar, 0.0F, 0.0F, 0.25F * shinProgressBound);
      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsBoneCount(this.armL, -0.3F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.earL, 0.25F, 0.0F, 0.0F);
      }

      if (poseProgress.usingItem && poseProgress.swingProgress <= 0.0F) {
         foreShinScale.getAmounts(this.handR, 0.95F, 0.0F, 0.1F);
         foreShinScale.getAmounts(this.shinProgressForeEar, 0.3F, 0.0F, 0.0F);
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         foreShinScale.getAmounts(this.handR, -0.3F, 0.0F, 1.0F);
         foreShinScale.getAmounts(this.handL, -0.3F, 0.0F, -1.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.06F, 0.94F, 1.06F);
      }
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      handleBonecaAmbalabu(bonecaAmbalabu, 1.0F);
      float bonecaAmbalabuFactor = (bonecaAmbalabu.ambalabuBonecaValue - 1) / 6.8F;
      float bonecaAmbalabu2 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, 1.0F, 3.55F);
      float bonecaAmbalabuValue = getAmountForBonecaAmbalabuFloat(bonecaAmbalabu, 21.55F);
      float amountForBonecaAmbalabuFloat = getAmountForBonecaAmbalabuFloat(bonecaAmbalabu, 15.049999F);
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu2 - 0.48F * bonecaAmbalabuFactor, bonecaAmbalabuValue - 1.0F, bonecaAmbalabu2 + 0.48F * bonecaAmbalabuFactor, amountForBonecaAmbalabuFloat + 1.0F, ForeShinUtils.getValueForIntFloat(16251386, 0.8F));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu2 - 0.34F * bonecaAmbalabuFactor, bonecaAmbalabuValue, bonecaAmbalabu2 + 0.34F * bonecaAmbalabuFactor, amountForBonecaAmbalabuFloat, ForeShinUtils.getValueForInt(1710618));
      float bonecaAmbalabu3 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, 1.0F, 2.3F);
      float bonecaAmbalabuValue3 = getAmountForBonecaAmbalabuFloat(bonecaAmbalabu, 12.4F);

      for (int i = 0; i <= 6; i++) {
         double angle = Math.toRadians(-105.0 + 35.0 * i);
         float sin = (float)Math.sin(angle);
         float cos = (float)Math.cos(angle);
         bonecaAmbalabu.getClass02(
            bonecaAmbalabu3 + sin * 1.3F * bonecaAmbalabuFactor,
            bonecaAmbalabuValue3 - cos * 1.3F * bonecaAmbalabuFactor,
            bonecaAmbalabu3 + sin * 1.62F * bonecaAmbalabuFactor,
            bonecaAmbalabuValue3 - cos * 1.62F * bonecaAmbalabuFactor,
            1.3F,
            ForeShinUtils.getValueForInt(2829616)
         );
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuValue, float... bonecaAmbalabuValues) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(9341573))
         .getBonecaAmbalabu(0.06F, 8.0F, bonecaAmbalabuValue)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(7104611), 0.25F, 5.0F, bonecaAmbalabuValue + 3)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(10522494), 0.22F, 4.0F, bonecaAmbalabuValue + 5);

      for (float bonecaAmbalabuFactor : bonecaAmbalabuValues) {
         byte bonecaAmbalabuLimit = 12;
         float[] floats = new float[bonecaAmbalabuLimit + 1];
         float[] floats2 = new float[bonecaAmbalabuLimit + 1];
         float[] floats3 = new float[bonecaAmbalabuLimit + 1];

         for (int i = 0; i <= bonecaAmbalabuLimit; i++) {
            floats[i] = (float)(bonecaAmbalabu.ambalabuBonecaValue * i) / bonecaAmbalabuLimit;
            floats2[i] = bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor + 1.0F * (float)Math.sin(i * 1.9F + bonecaAmbalabuFactor * 13.0F);
            floats3[i] = floats2[i] + 1.6F;
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats3, 1.2F, ForeShinUtils.getValueForIntFloat(11775656, 0.4F));
         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, 1.6F, ForeShinUtils.getValueForIntFloat(7104611, 0.85F));
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 2.0F, bonecaAmbalabuValue + 7);
   }

   private static float getAmount(float shinEarFactor) {
      shinEarFactor = shinEarFactor < 0.0F ? 0.0F : (shinEarFactor > 1.0F ? 1.0F : shinEarFactor);
      return shinEarFactor * shinEarFactor * (3.0F - 2.0F * shinEarFactor);
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float shinEarValue, float shinEarValue2, float shinEarValue3, float shinEarFactor, float classValue, int shinEarValue5) {
      for (int i = -1; i <= 1; i += 2) {
         int shinEarLimit = 14;
         float[] floats = new float[shinEarLimit + 1];
         float[] floats2 = new float[shinEarLimit + 1];
         float[] floats3 = new float[shinEarLimit + 1];

         for (int j = 0; j <= shinEarLimit; j++) {
            float shinEarFactor2 = (float)j / shinEarLimit;
            float bonecaAmbalabuValue = shinEarValue2 + (shinEarValue3 - shinEarValue2) * shinEarFactor2;
            float shinEarValue7 = shinEarValue + shinEarFactor * (float)Math.sin(shinEarFactor2 * Math.PI) + 0.1F * (float)Math.sin(shinEarFactor2 * 9.0F + shinEarValue5 * 1.7F);
            floats[j] = getBonecaAmbalabu(bonecaAmbalabu, i, bonecaAmbalabuValue);
            floats2[j] = getAmount(bonecaAmbalabu, shinEarValue7);
            floats3[j] = floats2[j] + classValue * 0.9F;
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats3, classValue * 0.8F, ForeShinUtils.getValueForIntFloat(11775656, 0.2F));
         bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, classValue, ForeShinUtils.getValueForIntFloat(7104611, 0.6F));
      }
   }

   private static float[] getAmounts(float shinEarValue, float shinEarValue2, float degrees) {
      float[] shinEarValues = getAmounts(shinEarValue, shinEarValue2, degrees - 2.0F, 0.0F);
      float[] amounts2 = getAmounts(shinEarValue, shinEarValue2, degrees + 2.0F, 0.0F);
      float[] amounts3 = getAmounts(shinEarValue - 0.15F, shinEarValue2, degrees, 0.0F);
      float[] amounts4 = getAmounts(shinEarValue + 0.15F, shinEarValue2, degrees, 0.0F);
      float[] amounts5 = getAmounts(shinEarValue, shinEarValue2, degrees, -1.0F);
      float[] amounts6 = getAmounts(shinEarValue, shinEarValue2, degrees, 0.0F);
      float shinEarFactor = amounts2[0] - shinEarValues[0];
      float shinEarFactor2 = amounts2[1] - shinEarValues[1];
      float shinEarFactor3 = amounts2[2] - shinEarValues[2];
      float shinEarFactor4 = amounts4[0] - amounts3[0];
      float shinEarFactor5 = amounts4[1] - amounts3[1];
      float shinEarFactor6 = amounts4[2] - amounts3[2];
      float shinEarFactor7 = shinEarFactor2 * shinEarFactor6 - shinEarFactor3 * shinEarFactor5;
      float shinEarFactor8 = shinEarFactor3 * shinEarFactor4 - shinEarFactor * shinEarFactor6;
      float shinEarFactor9 = shinEarFactor * shinEarFactor5 - shinEarFactor2 * shinEarFactor4;
      float shinEarValue4 = shinEarFactor7 * (amounts6[0] - amounts5[0]) + shinEarFactor8 * (amounts6[1] - amounts5[1]) + shinEarFactor9 * (amounts6[2] - amounts5[2]);
      float shinEarDivisor = (float)Math.sqrt(shinEarFactor7 * shinEarFactor7 + shinEarFactor8 * shinEarFactor8 + shinEarFactor9 * shinEarFactor9) * (shinEarValue4 < 0.0F ? -1.0F : 1.0F);
      return new float[]{shinEarFactor7 / shinEarDivisor, shinEarFactor8 / shinEarDivisor, shinEarFactor9 / shinEarDivisor};
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15330800), ForeShinUtils.getValueForInt(13620440)).getBonecaAmbalabu(0.02F, 2.0F, 31);
      float bonecaAmbalabuFactor = (bonecaAmbalabu.ambalabuBonecaValue - 1) / 11.200001F;
      float bonecaAmbalabuFactor2 = (bonecaAmbalabu.bonecaValue - 1) / 6.8F;
      float[] bonecaAmbalabuValues = getAmounts(getAmountsShinFootTable(-2.8F));

      for (int i = 0; i < 4; i++) {
         float bonecaAmbalabuValue = 0.3F + i * 0.35F;
         bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, 0.5F, (bonecaAmbalabuValues[3] + bonecaAmbalabuValue) * bonecaAmbalabuFactor, (2.2F + bonecaAmbalabuValue) * bonecaAmbalabuFactor2, ForeShinUtils.getValueForIntFloat(3027510, 0.2F - i * 0.04F));
      }

      float classBound = 0.5F + 0.8500004F * bonecaAmbalabuFactor;
      float ambalabuBonecaValue = 0.5F + 10.35F * bonecaAmbalabuFactor;
      float[] floats = new float[]{0.7F, 3.15F};

      for (int j = 0; j < 2; j++) {
         for (float floatsItem : floats) {
            float bonecaAmbalabuValue5 = 0.5F + (floatsItem - 1.05F - -2.8F) / 6.8F * (bonecaAmbalabu.bonecaValue - 1);
            float bonecaValue = 0.5F + (floatsItem + 1.05F - -2.8F) / 6.8F * (bonecaAmbalabu.bonecaValue - 1);
            if (j == 0) {
               bonecaAmbalabu.getClassBonecaAmbalabu(classBound - 2.0F, bonecaAmbalabuValue5 - 2.0F, ambalabuBonecaValue + 2.0F, bonecaValue + 2.0F, ForeShinUtils.getValueForInt(16317179));
               bonecaAmbalabu.getClassBonecaAmbalabu(classBound - 0.8F, bonecaAmbalabuValue5 - 0.8F, ambalabuBonecaValue + 0.8F, bonecaValue + 0.8F, ForeShinUtils.getValueForInt(9278618));
            } else {
               bonecaAmbalabu.getClassBonecaAmbalabu(classBound, bonecaAmbalabuValue5, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(1710618));
               bonecaAmbalabu.getClass02(classBound + 2.0F, bonecaAmbalabuValue5 + 1.6F, ambalabuBonecaValue - 2.0F, bonecaAmbalabuValue5 + 1.6F, 1.6F, ForeShinUtils.getValueForIntFloat(14243871, 0.95F));
               bonecaAmbalabu.getClass02(classBound + 2.0F, bonecaValue - 1.6F, ambalabuBonecaValue - 2.0F, bonecaValue - 1.6F, 1.6F, ForeShinUtils.getValueForIntFloat(14243871, 0.95F));
            }
         }
      }

      for (int k = 0; k < 14; k++) {
         float bonecaAmbalabuValue7 = (float)(Math.abs(Math.sin(k * 12.9898) * 43758.5453) % 1.0);
         float bonecaAmbalabuFactor3 = (float)(Math.abs(Math.sin(k * 78.233) * 12543.137) % 1.0);
         float classDivisor = 0.8F + 0.9F * bonecaAmbalabuValue7 * bonecaAmbalabuFactor3;
         bonecaAmbalabu.getClass022(
            bonecaAmbalabu.ambalabuBonecaValue * (0.12F + 0.76F * bonecaAmbalabuValue7),
            bonecaAmbalabu.bonecaValue * (0.08F + 0.88F * bonecaAmbalabuFactor3),
            classDivisor * 1.2F,
            classDivisor,
            ForeShinUtils.getValueForIntFloat(k % 3 == 0 ? 11036202 : 13212245, 0.9F)
         );
      }
   }

   private static float getAmount3(BonecaAmbalabu bonecaAmbalabu, float shinEarValue) {
      return 0.5F + (0.5F + 0.5F * shinEarValue) * (bonecaAmbalabu.bonecaValue - 1);
   }

   private static float[] getAmounts(float shinEarValue) {
      int shinEarValue2 = shinFootTable.length;
      int index = Math.max(0, Math.min(shinEarValue2 - 2, (int)Math.floor(shinEarValue)));
      float shinEarFactor = Math.max(0.0F, Math.min(1.0F, shinEarValue - index));
      float[] floats = new float[5];

      for (int i = 0; i < 3; i++) {
         floats[i] = pointsSpline[3 * index + i] + (pointsSpline[3 * index + 3 + i] - pointsSpline[3 * index + i]) * shinEarFactor;
      }

      floats[3] = shinFootTable[index] + (shinFootTable[index + 1] - shinFootTable[index]) * shinEarFactor;
      floats[4] = SAMPLED_THICKNESSES[index] + (SAMPLED_THICKNESSES[index + 1] - SAMPLED_THICKNESSES[index]) * shinEarFactor;
      return floats;
   }

   @Override
   public String formatTungDin() {
      return "rhino_toasterino";
   }

   private static void handle(LatheLoft latheLoft, ShinBrr shinBrr) {
      float[] shinEarValues = getAmounts(getAmountsShinFootTable(-11.0F), 1.0F, 0.0F, -0.45F);
      float[] pointsSpline = LatheLoft.getPointsSpline(
         2,
         0.0F,
         shinEarValues[1],
         shinEarValues[2],
         0.0F,
         shinEarValues[1] + 1.5F,
         shinEarValues[2] - 0.25F,
         0.0F,
         shinEarValues[1] + 3.1F,
         shinEarValues[2] + 0.15F,
         0.0F,
         shinEarValues[1] + 4.2F,
         shinEarValues[2] + 1.15F,
         0.0F,
         shinEarValues[1] + 4.7F,
         shinEarValues[2] + 2.2F
      );
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 1.6F, 1.3F, 0.9F, 0.45F, 0.06F), 12, shinBrr, false);
      float[] amounts2 = getAmounts(getAmountsShinFootTable(-7.9F), 1.0F, 0.0F, -0.4F);
      float[] latheLoftValues = LatheLoft.getPointsSpline(
         2, 0.0F, amounts2[1], amounts2[2], 0.0F, amounts2[1] + 1.1F, amounts2[2] - 0.1F, 0.0F, amounts2[1] + 2.0F, amounts2[2] + 0.35F, 0.0F, amounts2[1] + 2.5F, amounts2[2] + 1.0F
      );
      latheLoft.getLatheLoft(latheLoftValues, LatheLoft.getAmounts(LatheLoft.getValue(latheLoftValues), 1.05F, 0.8F, 0.42F, 0.05F), 10, shinBrr, false);
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      float[] bonecaAmbalabuValues = getAmounts(getAmountsShinFootTable(-4.6F));
      float bonecaAmbalabuFactor = (bonecaAmbalabu.ambalabuBonecaValue - 1) / 11.200001F;
      float bonecaAmbalabuFactor2 = (bonecaAmbalabu.bonecaValue - 1) / 9.599999F;
      float bonecaAmbalabuValue = getAmountForBonecaAmbalabuFloat(bonecaAmbalabu, bonecaAmbalabuValues[1]);

      for (int i = 0; i < 4; i++) {
         float bonecaAmbalabuValue2 = 0.45F + i * 0.35F;
         bonecaAmbalabu.getClass022(
            bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabuValue, (bonecaAmbalabuValues[3] + bonecaAmbalabuValue2) * bonecaAmbalabuFactor, (bonecaAmbalabuValues[4] + bonecaAmbalabuValue2) * bonecaAmbalabuFactor2, ForeShinUtils.getValueForIntFloat(3027510, 0.2F - i * 0.04F)
         );
      }

      float bonecaAmbalabuValue3 = bonecaAmbalabu.bonecaValue * 0.92F;
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.06F, bonecaAmbalabuValue3 - 1.2F, bonecaAmbalabu.ambalabuBonecaValue * 0.94F, bonecaAmbalabuValue3 + 1.2F, ForeShinUtils.getValueForIntFloat(3027252, 0.8F));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.06F, bonecaAmbalabuValue3 + 1.2F, bonecaAmbalabu.ambalabuBonecaValue * 0.94F, bonecaAmbalabuValue3 + 2.2F, ForeShinUtils.getValueForIntFloat(16777215, 0.6F));
      float bonecaAmbalabuValue4 = bonecaAmbalabu.ambalabuBonecaValue * 0.5F;
      float amountForFloat = bonecaAmbalabu.bonecaValue * 0.75F;
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuValue4 - 5.0F, amountForFloat - 2.0F, bonecaAmbalabuValue4 + 5.0F, amountForFloat + 2.0F, ForeShinUtils.getValueForInt(9278618));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue4 - 5.0F, amountForFloat, 2.0F, 2.0F, ForeShinUtils.getValueForInt(9278618));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue4 + 5.0F, amountForFloat, 2.0F, 2.0F, ForeShinUtils.getValueForInt(9278618));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabuValue4 - 5.0F, amountForFloat - 1.1F, bonecaAmbalabuValue4 + 5.0F, amountForFloat + 1.1F, ForeShinUtils.getValueForInt(15001835));
      bonecaAmbalabu.getClass02(bonecaAmbalabuValue4 - 3.5F, amountForFloat, bonecaAmbalabuValue4 + 3.5F, amountForFloat, 0.8F, ForeShinUtils.getValueForIntFloat(6975606, 0.8F));
   }

   private static float getAmountForFloat(float shinEarValue) {
      return 90.0F + 42.0F * (1.0F - (1.0F - shinEarValue) * (1.0F - shinEarValue));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2, float bonecaAmbalabuFactor) {
      for (int i = -1; i <= 1; i++) {
         float classValue = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + i * bonecaAmbalabuFactor);
         float amountForFloat = bonecaAmbalabu.bonecaValue * 0.5F * (bonecaAmbalabuValue + bonecaAmbalabuValue2);
         float bonecaAmbalabuValue5 = bonecaAmbalabu.ambalabuBonecaValue * 0.034F;
         float classDivisor = bonecaAmbalabu.bonecaValue * 0.5F * (bonecaAmbalabuValue2 - bonecaAmbalabuValue);
         bonecaAmbalabu.getClass022(classValue, amountForFloat, bonecaAmbalabuValue5 + 1.0F, classDivisor + 1.0F, ForeShinUtils.getValueForIntFloat(6183509, 0.7F));
         bonecaAmbalabu.getClass022(classValue, amountForFloat, bonecaAmbalabuValue5, classDivisor, ForeShinUtils.getValueForInt(12168082));
         bonecaAmbalabu.getClass022(classValue - bonecaAmbalabuValue5 * 0.3F, amountForFloat - classDivisor * 0.3F, bonecaAmbalabuValue5 * 0.35F, classDivisor * 0.4F, ForeShinUtils.getValueForIntFloat(15130319, 0.7F));
      }
   }

   private static float[] getAmounts(float shinEarFactor, float shinEarFactor2, float shinEarFactor3, float shinEarValue, float shinEarFactor5, float shinEarFactor6, float shinEarFactor7) {
      float shinEarFactor8 = 1.0F - shinEarFactor7;
      return new float[]{shinEarFactor8 * shinEarFactor8 * shinEarFactor + 2.0F * shinEarFactor8 * shinEarFactor7 * shinEarFactor3 + shinEarFactor7 * shinEarFactor7 * shinEarFactor5, shinEarFactor8 * shinEarFactor8 * shinEarFactor2 + 2.0F * shinEarFactor8 * shinEarFactor7 * shinEarValue + shinEarFactor7 * shinEarFactor7 * shinEarFactor6};
   }

   private static float getAmountsForFloat(float shinEarValue) {
      int shinEarValue2 = shinFootTable.length;
      int index = Math.max(0, Math.min(shinEarValue2 - 2, (int)Math.floor(shinEarValue)));
      float shinEarFactor = Math.max(0.0F, Math.min(1.0F, shinEarValue - index));
      return (SPINE_CUMULATIVE_LENGTHS[index] + (SPINE_CUMULATIVE_LENGTHS[index + 1] - SPINE_CUMULATIVE_LENGTHS[index]) * shinEarFactor) / SPINE_CUMULATIVE_LENGTHS[shinEarValue2 - 1];
   }

   private static void handleAmounts(BonecaAmbalabu bonecaAmbalabu) {
      int shinEarLimit = 20;
      float shinEarValue = SPINE_Z[SPINE_Z.length - 1];

      for (int i = -1; i <= 1; i += 2) {
         int shinEarSize = 4 * (shinEarLimit + 1);
         float[] floats = new float[shinEarSize];
         float[] floats2 = new float[shinEarSize];
         float[] floats3 = new float[shinEarSize];
         float[] classValues = new float[shinEarLimit + 1];
         float[] floats5 = new float[shinEarLimit + 1];
         float[] floats6 = new float[shinEarLimit + 1];
         float[] floats7 = new float[shinEarLimit + 1];

         for (int j = 0; j <= shinEarLimit; j++) {
            float shinEarFactor = (float)j / shinEarLimit;
            float shinEarValue2 = -6.6F + (shinEarValue - -6.6F) * shinEarFactor;
            float amount3 = getAmount3(shinEarFactor);
            float amountForFloat = getAmountForFloat(shinEarFactor);
            float bonecaAmbalabuValue = amount3 + 0.3F * (amountForFloat - amount3);
            float shinEarValue6 = amountForFloat - 0.22F * (amountForFloat - amount3);
            float shinEarValue7 = getAmount(bonecaAmbalabu, shinEarValue2);
            classValues[j] = getBonecaAmbalabu(bonecaAmbalabu, i, amount3);
            floats5[j] = shinEarValue7;
            floats6[j] = getBonecaAmbalabu(bonecaAmbalabu, i, amountForFloat);
            floats7[j] = shinEarValue7;
            floats[2 * j] = classValues[j];
            floats[2 * j + 1] = shinEarValue7;
            floats[shinEarSize - 2 - 2 * j] = floats6[j];
            floats[shinEarSize - 1 - 2 * j] = shinEarValue7;
            floats2[2 * j] = classValues[j];
            floats2[2 * j + 1] = shinEarValue7;
            floats2[shinEarSize - 2 - 2 * j] = getBonecaAmbalabu(bonecaAmbalabu, i, bonecaAmbalabuValue);
            floats2[shinEarSize - 1 - 2 * j] = shinEarValue7;
            floats3[2 * j] = getBonecaAmbalabu(bonecaAmbalabu, i, shinEarValue6);
            floats3[2 * j + 1] = shinEarValue7;
            floats3[shinEarSize - 2 - 2 * j] = floats6[j];
            floats3[shinEarSize - 1 - 2 * j] = shinEarValue7;
         }

         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(3877931), floats);
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(15921126), floats2);
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(15921126), floats3);

         for (float shinEarTotal = 0.12F; shinEarTotal < 1.0F; shinEarTotal += 0.16F) {
            float shinEarValue8 = -6.6F + (shinEarValue - -6.6F) * shinEarTotal;
            float shinEarValue9 = getAmount3(shinEarTotal);
            float amountForFloat10 = getAmountForFloat(shinEarTotal);
            bonecaAmbalabu.getClass02(
               getBonecaAmbalabu(bonecaAmbalabu, i, shinEarValue9),
               getAmount(bonecaAmbalabu, shinEarValue8),
               getBonecaAmbalabu(bonecaAmbalabu, i, shinEarValue9 + 0.3F * (amountForFloat10 - shinEarValue9)),
               getAmount(bonecaAmbalabu, shinEarValue8),
               0.7F,
               ForeShinUtils.getValueForInt(14275527)
            );
            bonecaAmbalabu.getClass02(
               getBonecaAmbalabu(bonecaAmbalabu, i, amountForFloat10 - 0.22F * (amountForFloat10 - shinEarValue9)),
               getAmount(bonecaAmbalabu, shinEarValue8),
               getBonecaAmbalabu(bonecaAmbalabu, i, amountForFloat10),
               getAmount(bonecaAmbalabu, shinEarValue8),
               0.7F,
               ForeShinUtils.getValueForInt(14275527)
            );
         }

         bonecaAmbalabu.getClassForFloatArrayFloat(floats6, floats7, 2.8F, ForeShinUtils.getValueForInt(5920338));
         bonecaAmbalabu.getClassForFloatArrayFloat(classValues, floats5, 3.4F, ForeShinUtils.getValueForInt(5920338));
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabu(bonecaAmbalabu, i, 91.0F),
            getAmount(bonecaAmbalabu, -6.7F),
            getBonecaAmbalabu(bonecaAmbalabu, i, 80.0F),
            getAmount(bonecaAmbalabu, -6.25F),
            getBonecaAmbalabu(bonecaAmbalabu, i, 68.0F),
            getAmount(bonecaAmbalabu, -5.2F),
            3.4F,
            ForeShinUtils.getValueForInt(5920338)
         );
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabu(bonecaAmbalabu, i, 64.0F),
            getAmount(bonecaAmbalabu, -4.6F),
            getBonecaAmbalabu(bonecaAmbalabu, i, 82.0F),
            getAmount(bonecaAmbalabu, -6.0F),
            getBonecaAmbalabu(bonecaAmbalabu, i, 100.0F),
            getAmount(bonecaAmbalabu, -5.6F),
            1.6F,
            ForeShinUtils.getValueForIntFloat(7104611, 0.6F)
         );
      }
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue) {
      return 0.5F + (0.5F - 0.5F * bonecaAmbalabuValue) * (bonecaAmbalabu.ambalabuBonecaValue - 1);
   }

   private static void handle(LatheLoft latheLoft, float shinEarValue, float shinEarLimit, float shinEarValue3, int loftPathFactor, ShinBrr shinBrr) {
      float[] floats = new float[]{getAmountsShinFootTable(shinEarValue), 0.0F, getAmountsShinFootTable(shinEarLimit)};
      floats[1] = 0.5F * (floats[0] + floats[2]);
      float[] loftPathValues = new float[9];
      float[] crownForeValues = new float[3];
      float[] floats4 = new float[3];

      for (int i = 0; i < 3; i++) {
         float[] shinEarValues = getAmounts(floats[i]);
         loftPathValues[3 * i] = shinEarValues[0];
         loftPathValues[3 * i + 1] = shinEarValues[1];
         loftPathValues[3 * i + 2] = shinEarValues[2];
         float shinEarValue5 = i == 1 ? shinEarValue3 : 0.05F;
         crownForeValues[i] = shinEarValues[3] + shinEarValue5;
         floats4[i] = shinEarValues[4] + shinEarValue5;
      }

      latheLoft.getLoftPath(loftPathValues, crownForeValues, floats4, loftPathFactor, shinBrr, null, null, 0.0F, -1.0F, 0.0F);
   }

   private static ForeEar getClassForLatheLoftString(LatheLoft latheLoft, String classText, ForeEar foreEar, float classValue, float classValue2, float classValue3) {
      return latheLoft.getClassForStringForeEar(classText, foreEar, classValue * 1.07F, classValue2 * 1.07F, classValue3 * 1.07F);
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuFactor = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         float sin = (float)Math.sin(bonecaAmbalabuFactor * Math.PI);
         int bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(9327648), ForeShinUtils.getValueForInt(11891250), sin);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(12615742), 0.3F, 3.0F, 81)
         .getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(7225112, 0.7F), 1.2F, 0.5F, 83)
         .getBonecaAmbalabu(0.05F, 2.0F, 85);
   }

   private static float[] getAmounts() {
      byte shinEarLimit = 9;
      float shinEarValue = 6.7F;
      float[] floats = new float[2 * (shinEarLimit + 7)];
      int shinEarCounter = 0;
      floats[shinEarCounter++] = 4.15F;
      floats[shinEarCounter++] = -1.9F;
      floats[shinEarCounter++] = 4.15F;
      floats[shinEarCounter++] = shinEarValue - 1.25F;
      floats[shinEarCounter++] = 4.32F;
      floats[shinEarCounter++] = shinEarValue - 0.72F;

      for (int i = 0; i <= shinEarLimit; i++) {
         double angle = Math.toRadians(-14.0 + 208.0 * i / shinEarLimit);
         floats[shinEarCounter++] = 4.7F * (float)Math.cos(angle);
         floats[shinEarCounter++] = shinEarValue + 2.7F * (float)Math.sin(angle);
      }

      floats[shinEarCounter++] = -4.32F;
      floats[shinEarCounter++] = shinEarValue - 0.72F;
      floats[shinEarCounter++] = -4.15F;
      floats[shinEarCounter++] = shinEarValue - 1.25F;
      floats[shinEarCounter++] = -4.15F;
      floats[shinEarCounter++] = -1.9F;
      return floats;
   }

   private static float[] getAmounts(float shinEarValue, float shinEarFactor, float degrees, float shinEarValue2) {
      int shinEarValue3 = shinFootTable.length;
      int max4 = Math.max(0, Math.min(shinEarValue3 - 2, (int)Math.floor(shinEarValue)));
      float max5 = Math.max(0.0F, Math.min(1.0F, shinEarValue - max4));
      float cos = (float)Math.cos(Math.toRadians(degrees));
      float sin = (float)Math.sin(Math.toRadians(degrees));
      float[] floats = new float[3];

      for (int i = 0; i < 2; i++) {
         int index = max4 + i;
         float shinEarFactor2 = i == 0 ? 1.0F - max5 : max5;
         float shinEarFactor3 = SAMPLED_THICKNESSES[index] + shinEarValue2;
         float shinEarFactor4 = shinFootTable[index] + shinEarValue2;

         for (int j = 0; j < 3; j++) {
            floats[j] += shinEarFactor2
               * (pointsSpline[3 * index + j] - SPINE_TANGENTS[3 * index + j] * shinEarFactor3 * cos - shinEarFactor * SPINE_NORMALS[3 * index + j] * shinEarFactor4 * sin);
         }
      }

      return floats;
   }

   private static void handleBonecaAmbalabu8(BonecaAmbalabu bonecaAmbalabu) {
      handleBonecaAmbalabu(bonecaAmbalabu, 67, 0.3F, 0.7F);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, 1.6F, ForeShinUtils.getValueForIntFloat(7104611, 0.85F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue - 1.6F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(7104611, 0.85F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.45F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.55F, ForeShinUtils.getValueForIntFloat(11775656, 0.3F));
   }

   private static float getAmount3(float base) {
      return 90.0F + -2.0F * (float)Math.pow(base, 0.85);
   }

   private static int getAmountsShinEarTable(float shinEarValue) {
      float max2 = Math.max(0.0F, Math.min(1.0F, (shinEarValue - 9.6F) / 13.199999F));

      for (int i = 0; i + 1 < shinEarTable.length; i++) {
         if (max2 <= shinEarTable[i + 1]) {
            float shinEarFactor = (max2 - shinEarTable[i]) / (shinEarTable[i + 1] - shinEarTable[i]);
            return ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(table[i]), ForeShinUtils.getValueForInt(table[i + 1]), getAmount(shinEarFactor));
         }
      }

      return ForeShinUtils.getValueForInt(table[table.length - 1]);
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float shinEarValue, ForeEar foreEar, ForeEar foreEar2, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float thighBScaled = shinEarValue * 4.7F;
      latheLoft.getLatheLoftForForeEar(foreEar);
      float[] pointsSpline = LatheLoft.getPointsSpline(1, thighBScaled, 10.4F, 0.2F, thighBScaled, 7.4F, 0.05F, thighBScaled, 4.8F, -0.1F);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 2.6F, 2.5F, 2.35F), 12, shinBrr, false);
      latheLoft.getLatheLoft(thighBScaled, 4.8F, -0.1F, 2.36F, 8, shinBrr);
      latheLoft.getLatheLoftForForeEar(foreEar2);
      float[] latheLoftValues = LatheLoft.getPointsSpline(1, thighBScaled, 4.8F, -0.1F, thighBScaled, 2.8F, 0.0F, thighBScaled, 0.7F, 0.0F);
      latheLoft.getLatheLoft(latheLoftValues, LatheLoft.getAmounts(LatheLoft.getValue(latheLoftValues), 2.25F, 2.15F, 2.35F), 12, shinBrr2, false);
      latheLoft.getLoftLathe(thighBScaled + shinEarValue * 0.15F, 0.0F, -0.45F, 2.75F, 1.55F, 3.0F, 14, 0.0F, 0.5F, shinBrr3);
   }

   private static void handleBonecaAmbalabu9(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < 5; i++) {
         float bonecaAmbalabuValue = bonecaAmbalabu.bonecaValue * (0.7F + i * 0.055F);
         bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.3F, bonecaAmbalabuValue, bonecaAmbalabu.ambalabuBonecaValue * 0.7F, bonecaAmbalabuValue + 1.6F, ForeShinUtils.getValueForIntFloat(3027252, 0.8F));
      }
   }

   private static void handle(LatheLoft latheLoft, float shinEarValue, float[] shinEarValues, float[] shinEarValues2) {
      float degrees = (float)Math.toDegrees(Math.atan2(shinEarValues2[0], -shinEarValues2[2]));
      float degrees2 = (float)Math.toDegrees(Math.asin(Math.max(-1.0F, Math.min(1.0F, shinEarValues2[1]))));
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(shinEarValues[0], shinEarValues[1], shinEarValues[2]).getLatheLoft4(-degrees).getLatheLoftLatheLoftAmountLoft(degrees2);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.95F, 0.85F, 0.42F, 8, latheLoft.getMax(15328474));
      latheLoft.getLatheLoft(shinEarValue * 0.12F, 0.02F, -0.16F, 0.78F, 0.78F, 0.34F, 10, latheLoft.getMax(1840916));
      latheLoft.getLatheLoft(-shinEarValue * 0.15F, 0.3F, -0.48F, 0.2F, 4, latheLoft.getMax(16777215));
      latheLoft.getDequeArray();
   }

   private static float getAmountsShinFootTable(float shinEarLimit) {
      int shinEarLimit2 = shinFootTable.length;
      if (shinEarLimit >= pointsSpline[2]) {
         return 0.0F;
      } else {
         for (int i = 0; i + 1 < shinEarLimit2; i++) {
            float shinEarLimit3 = pointsSpline[3 * i + 2];
            float shinEarValue = pointsSpline[3 * i + 5];
            if (shinEarLimit <= shinEarLimit3 && shinEarLimit >= shinEarValue) {
               return i + (shinEarLimit3 - shinEarLimit) / Math.max(1.0E-5F, shinEarLimit3 - shinEarValue);
            }
         }

         return shinEarLimit2 - 1;
      }
   }
}
