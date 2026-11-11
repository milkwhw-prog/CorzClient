package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class TailFore extends WarmBrainrotBase {
   private ForeEar classShinFoot;
   private static final float[] eyeTable = new float[]{-1.0F, -3.3F, -5.0F, -6.8F, -8.6F, -10.3F, -11.8F, -12.8F, -13.35F, -13.66F, -13.84F};
   private static final float[] tailForeTable = new float[]{21.9F, 21.57F, 21.36F, 21.28F, 21.24F, 21.24F, 21.26F, 21.28F, 21.3F, 21.3F, 21.3F};
   private ForeEar classTail;
   private ForeEar classShinProgress;
   private static final float[] table = new float[]{0.95F, 1.75F, 1.65F, 1.3F, 1.05F, 0.95F, 0.95F, 0.82F, 0.62F, 0.36F, 0.03F};
   private ForeEar shinFootForeEar;
   private ForeEar shinProgressForeEar;
   private ForeEar legL;
   private ForeEar footR;
   private static final float[] EYE_THICKNESSES = new float[]{0.7F, 1.06F, 0.98F, 0.88F, 0.78F, 0.72F, 0.72F, 0.6F, 0.47F, 0.3F, 0.025F};
   private ForeEar armL;
   private ForeEar eyeL;
   private ForeEar footL;
   private ForeEar tailForeForeEar;
   private static final float[] EYE_WIDTHS = new float[]{1.5F, 2.35F, 2.3F, 2.05F, 1.8F, 1.62F, 1.55F, 1.38F, 1.06F, 0.66F, 0.04F};
   private static final float[] shinFootTable = new float[]{0.0F, 16.2F, 0.0F, 0.0F, 18.5F, -0.7F, 0.0F, 20.2F, -1.8F, 0.0F, 21.1F, -2.8F};
   private ForeEar foreEar;
   private static final float[] TAIL_WIDTHS = new float[]{3.0F, 2.3F, 1.95F, 1.7F};
   private ForeEar tailForeEar;
   private static final float[] TAIL_THICKNESSES = new float[]{2.7F, 2.0F, 1.5F, 1.2F};
   private ForeEar classEye;
   private static final float[] tailFootAmounts = new float[]{
      0.0F, 9.0F, 4.6F, 0.0F, 8.2F, 8.4F, 0.0F, 6.8F, 12.2F, 0.0F, 4.6F, 15.8F, 0.0F, 2.2F, 18.8F, 0.0F, 0.8F, 21.2F
   };
   private ForeEar shinR;
   private static final float[] TAIL_TIP_RADII = new float[]{2.0F, 1.85F, 1.32F, 0.88F, 0.52F, 0.03F};
   private static final float[] bodyMelonAmounts = new float[]{1.8F, 1.62F, 1.1F, 0.72F, 0.4F, 0.03F};
   private ForeEar eyeForeEar;
   private static final float[] shinProgressTable = new float[]{
      0.0F,
      -0.031F,
      -0.084F,
      -0.162F,
      -0.263F,
      -0.385F,
      -0.527F,
      -0.687F,
      -0.864F,
      -1.055F,
      -1.261F,
      -1.478F,
      -1.706F,
      -1.943F,
      -2.187F,
      -2.437F,
      -2.691F,
      -2.948F,
      -3.206F,
      -3.464F,
      -3.72F,
      -3.972F,
      -4.219F,
      -4.46F,
      -4.692F,
      -4.915F,
      -5.127F,
      -5.326F,
      -5.51F,
      -6.2832F
   };
   private static final float[] SWING_COEFFICIENTS = new float[]{
      0.0F,
      0.37F,
      0.34F,
      0.38F,
      0.45F,
      0.54F,
      0.61F,
      0.61F,
      0.58F,
      0.35F,
      0.0F,
      0.96F,
      1.49F,
      1.62F,
      1.62F,
      1.27F,
      1.04F,
      0.93F,
      0.83F,
      0.59F,
      0.9F,
      1.35F,
      1.62F,
      1.81F,
      1.89F,
      1.89F,
      1.54F,
      0.8F,
      0.0F,
      0.0F
   };
   private ForeEar foreR;
   private ForeEar foreL;
   private ForeEar shinL;
   private ForeEar tailSegmentForeEar;
   private ForeEar head;

   private static void handle(LatheLoft latheLoft, float tailForeValue, float tailForeValue2, float tailForeLimit, float tailForeValue4, float tailForeValue5, float tailForeValue6, float tailForeValue7, ShinBrr shinBrr) {
      handle(latheLoft, tailForeValue, tailForeValue2, tailForeLimit, tailForeValue4, tailForeValue5, tailForeValue6, tailForeValue7, 4, shinBrr);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuLimit, boolean bonecaAmbalabuFlag) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuLimit2 = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuValue = 0.3F + 0.65F * (1.0F - getAmount(bonecaAmbalabuLimit2 / 0.7F));

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float abs3 = Math.abs((j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F);
            if (bonecaAmbalabuFlag ? !(abs3 >= bonecaAmbalabuLimit) : !(abs3 < bonecaAmbalabuLimit)) {
               bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), ForeShinUtils.getValueForInt(6173230), bonecaAmbalabuValue));
               if (bonecaAmbalabuLimit2 > 0.93F) {
                  bonecaAmbalabu.getBonecaAmbalabu(
                     j, i, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(j, i), ForeShinUtils.getValueForInt(6711610), getAmount((bonecaAmbalabuLimit2 - 0.93F) / 0.04F))
                  );
               }
            }
         }
      }
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      float[] pointsSpline = LatheLoft.getPointsSpline(2, tailFootAmounts);
      int shinFootValue = LatheLoft.getValue(pointsSpline);
      float[] shinFootValues = LatheLoft.getAmounts(shinFootValue, TAIL_TIP_RADII);
      float[] standCellKeys = LatheLoft.getAmounts(shinFootValue, bodyMelonAmounts);
      this.legL = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 6.8F, 0.6F);
      this.armL = latheLoft.getClassForStringForeEar("melon", this.legL, 0.0F, 6.8F, 0.6F);
      this.head = latheLoft.getClassForStringForeEar("neck", this.armL, 0.0F, 17.6F, -0.6F);
      this.eyeL = latheLoft.getClassForStringForeEar("head", this.head, 0.0F, 20.2F, -1.6F);
      this.classEye = latheLoft.getClassForStringForeEar("jaw", this.eyeL, 0.0F, 21.65F, -2.2F);
      this.eyeForeEar = latheLoft.getClassForStringForeEar("eye_r", this.eyeL, 1.25F, 23.55F, -5.2F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("eye_l", this.eyeL, -1.25F, 23.55F, -5.2F);
      this.foreR = latheLoft.getClassForStringForeEar("arm_r", this.armL, 5.4F, 16.6F, -1.4F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("fore_r", this.foreR, 6.9F, 14.9F, -1.9F);
      this.foreL = latheLoft.getClassForStringForeEar("arm_l", this.armL, -5.4F, 16.6F, -1.4F);
      this.foreEar = latheLoft.getClassForStringForeEar("fore_l", this.foreL, -6.9F, 14.9F, -1.9F);
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", this.legL, 3.5F, 6.8F, 0.6F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 4.6F, 3.4F, -0.1F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("foot_r", this.footR, 4.4F, 1.2F, 0.5F);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", this.legL, -3.5F, 6.8F, 0.6F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -4.6F, 3.4F, -0.1F);
      this.tailForeForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -4.4F, 1.2F, 0.5F);
      this.tailSegmentForeEar = latheLoft.getClassForStringForeEar("tail_1", this.armL, tailFootAmounts[0], tailFootAmounts[1], tailFootAmounts[2]);
      this.classTail = latheLoft.getClassForStringForeEar("tail_2", this.tailSegmentForeEar, pointsSpline[9], pointsSpline[10], pointsSpline[11]);
      this.classShinProgress = latheLoft.getClassForStringForeEar("tail_3", this.classTail, pointsSpline[15], pointsSpline[16], pointsSpline[17]);
      this.tailForeEar = latheLoft.getClassForStringForeEar("tail_4", this.classShinProgress, pointsSpline[21], pointsSpline[22], pointsSpline[23]);
      latheLoft.getLatheLoft(this.foreR);
      LoftLatheFlag.Region region = latheLoft.getMax(640, 400);
      handleBonecaAmbalabu3(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(288, 224);
      handleBonecaAmbalabu5(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(256, 192);
      handleBonecaAmbalabu4(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(128, 256);
      handleBonecaAmbalabu(max4.getClassBonecaAmbalabu(), false);
      LoftLatheFlag.Region max5 = latheLoft.getMax(128, 256);
      handleBonecaAmbalabu(max5.getClassBonecaAmbalabu(), true);
      LoftLatheFlag.Region max6 = latheLoft.getMax(136, 72);
      handleBonecaAmbalabu(max6.getClassBonecaAmbalabu(), 15, 10, 61);
      LoftLatheFlag.Region max7 = latheLoft.getMax(96, 34);
      handleBonecaAmbalabu(max7.getClassBonecaAmbalabu(), 11, 5, 63);
      LoftLatheFlag.Region max8 = latheLoft.getMax(144, 52);
      handleBonecaAmbalabu(max8.getClassBonecaAmbalabu(), 16, 7, 65);
      LoftLatheFlag.Region max9 = latheLoft.getMax(28, 20);
      handleBonecaAmbalabu(max9.getClassBonecaAmbalabu(), 3, 3, 67);
      LoftLatheFlag.Region max10 = latheLoft.getMax(72, 42);
      handleBonecaAmbalabu(max10.getClassBonecaAmbalabu(), 8, 6, 69);
      LoftLatheFlag.Region max11 = latheLoft.getMax(50, 56);
      handleBonecaAmbalabu(max11.getClassBonecaAmbalabu(), 6, 8, 71);
      LoftLatheFlag.Region max12 = latheLoft.getMax(64, 48);
      handleBonecaAmbalabu6(max12.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max13 = latheLoft.getMax(96, 64);
      handleBonecaAmbalabuForBonecaAmbalabu(max13.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max14 = latheLoft.getMax(96, 64);
      handleBonecaAmbalabuForBonecaAmbalabuFlag(max14.getClassBonecaAmbalabu(), false);
      LoftLatheFlag.Region max15 = latheLoft.getMax(128, 64);
      handleBonecaAmbalabuForBonecaAmbalabuFlag(max15.getClassBonecaAmbalabu(), true);
      LoftLatheFlag.Region max16 = latheLoft.getMax(288, 128);
      handleBonecaAmbalabu7(max16.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max17 = latheLoft.getMax(180, 84);
      handleBonecaAmbalabu(max17.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max3);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max6);
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
      ShinBrr classForRegion18 = latheLoft.getClassForRegion(max17);
      ShinBrr shinBrr = latheLoft.getMax(4148007);
      ShinBrr shinBrr2 = latheLoft.getMax(5463086);
      ShinBrr shinBrr3 = latheLoft.getMax(2762784);
      ShinBrr shinBrr4 = latheLoft.getMax(15524546);
      ShinBrr shinBrr5 = latheLoft.getMax(14208430);
      latheLoft.getLatheLoftForForeEar(this.armL);
      byte shinFootLimit = 12;
      float[] floats = new float[shinFootLimit + 1];
      float[] tailKatanaValues = new float[shinFootLimit + 1];

      for (int i = 0; i <= shinFootLimit; i++) {
         double angle = Math.PI * i / shinFootLimit;
         floats[i] = 6.6F * (float)Math.sin(angle);
         tailKatanaValues[i] = -8.3F * (float)Math.cos(angle);
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 12.6F, 0.4F).getLatheLoftLatheLoftAmountLoft(-20.0F);
      latheLoft.getLatheProfile(floats, tailKatanaValues, 24, 1.0F, 0.9545455F, classForRegion, null, null);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.head);
      latheLoft.getLoftPath(shinFootTable, TAIL_WIDTHS, TAIL_THICKNESSES, 12, classForRegion17, null, shinBrr2, 0.0F, 0.6F, 0.8F);
      handleAmounts(latheLoft, classForRegion15);
      this.handleAmountsEye(latheLoft, classForRegion3, classForRegion4, classForRegion15, classForRegion16, shinBrr2, classForRegion18, shinBrr4, shinBrr5, classForRegion13, classForRegion14);
      this.handleTail(latheLoft, pointsSpline, standCellKeys, shinFootValues, classForRegion6, classForRegion5, shinBrr);
      handle(latheLoft, this.foreR, this.shinProgressForeEar, 1.0F, classForRegion11, classForRegion12, shinBrr3);
      handle(latheLoft, this.foreL, this.foreEar, -1.0F, classForRegion11, classForRegion12, shinBrr3);
      handle(latheLoft, this.shinR, this.footR, this.shinFootForeEar, 1.0F, classForRegion7, classForRegion8, classForRegion9, classForRegion10, shinBrr3);
      handle(latheLoft, this.shinL, this.footL, this.tailForeForeEar, -1.0F, classForRegion7, classForRegion8, classForRegion9, classForRegion10, shinBrr3);
   }

   private static float getAmount(float tailForeFactor, float tailForeValue, float tailForeDivisor, float tailForeValue2) {
      float tailForeFactor2 = (tailForeValue2 - tailForeValue) / tailForeDivisor;
      return tailForeFactor * (float)Math.sqrt(Math.max(0.0F, 1.0F - tailForeFactor2 * tailForeFactor2));
   }

   private static float getAmount(float[] tailForeValues, float[] crownForeValues, float tailForeLimit) {
      for (int i = 0; i + 1 < tailForeValues.length; i++) {
         if (tailForeLimit <= tailForeValues[i] && tailForeLimit >= tailForeValues[i + 1]) {
            float tailForeFactor = (tailForeValues[i] - tailForeLimit) / (tailForeValues[i] - tailForeValues[i + 1]);
            return crownForeValues[i] + (crownForeValues[i + 1] - crownForeValues[i]) * tailForeFactor;
         }
      }

      return tailForeLimit > tailForeValues[0] ? crownForeValues[0] : crownForeValues[crownForeValues.length - 1];
   }

   @Override
   public String formatTungDinString() {
      return "Glorbo Fruttodrillo";
   }

   @Override
   public float getAmountFloat() {
      return 1.1F;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F);
            bonecaAmbalabu.getBonecaAmbalabu(
               j, i, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(4148007), ForeShinUtils.getValueForInt(5463086), Math.min(1.0F, bonecaAmbalabuValue / 0.3F))
            );
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3094552), 0.3F, 6.0F, 111);
      bonecaAmbalabu.getBonecaAmbalabu(0.04F, 3.0F, 113);
      handleBonecaAmbalabu(bonecaAmbalabu, 0.3F, 9.0F, 7.0F, 0.0F, 0.0F, 1.0F);
   }

   private static boolean canFlagForFloat(float tailForeValue, float tailForeValue2, float tailForeValue3) {
      float growScaled = tailForeValue * 11.0F;
      int round = Math.round(growScaled);
      int floorMod = Math.floorMod(round, 11);
      float tailForeValue5 = 0.075F * (float)Math.sin(tailForeValue2 * 8.5F + floorMod * 2.3F) + 0.035F * (float)Math.sin(tailForeValue2 * 21.0F + floorMod * 1.1F);
      float abs6 = Math.abs(growScaled - round - tailForeValue5);
      float tailForeValue7 = 0.03F * (float)Math.sin(tailForeValue2 * 64.0F + floorMod * 3.7F) + 0.02F * (float)Math.sin(tailForeValue2 * 143.0F + floorMod * 1.9F);
      return abs6 < 0.21F + tailForeValue3 + tailForeValue7;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuDivisor, float bonecaAmbalabuDivisor2, float bonecaAmbalabuValue2, float bonecaAmbalabuLimit, float bonecaAmbalabuValue3) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuFactor = bonecaAmbalabuLimit > bonecaAmbalabuValue2 ? 1.0F - getAmount((i + 0.5F - bonecaAmbalabuValue2) / (bonecaAmbalabuLimit - bonecaAmbalabuValue2)) : 1.0F;
         if (!(bonecaAmbalabuFactor <= 0.0F)) {
            for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
               float abs2 = Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F);
               if (!(abs2 >= bonecaAmbalabuValue)) {
                  float hashRegistryValue = (j + 0.5F - ambalabuBonecaValue * 0.5F) / bonecaAmbalabuDivisor;
                  float bonecaAmbalabuValue5 = (i + 0.5F) / bonecaAmbalabuDivisor2;
                  int floor6 = (int)Math.floor(hashRegistryValue);
                  int floor7 = (int)Math.floor(bonecaAmbalabuValue5);
                  float bonecaAmbalabuValue8 = (hashRegistryValue - floor6) * bonecaAmbalabuDivisor;
                  float bonecaAmbalabuValue9 = (bonecaAmbalabuValue5 - floor7) * bonecaAmbalabuDivisor2;
                  int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(j, i);
                  int foreShinValue = ForeShinUtils.getValue(bonecaAmbalabuForInt, 0.9F + 0.2F * getAmount(floor6 * 31 + floor7 * 7));
                  if (bonecaAmbalabuValue8 < 1.5F || bonecaAmbalabuValue9 < 1.5F) {
                     foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(3094552), 0.85F * bonecaAmbalabuValue3);
                  } else if (bonecaAmbalabuValue9 < 2.5F) {
                     foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(7240261), 0.75F * bonecaAmbalabuValue3);
                  }

                  float bonecaAmbalabuFactor2 = abs2 > bonecaAmbalabuValue - 0.04F ? (bonecaAmbalabuValue - abs2) / 0.04F : 1.0F;
                  bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue2(bonecaAmbalabuForInt, foreShinValue, bonecaAmbalabuFactor2 * bonecaAmbalabuFactor));
               }
            }
         }
      }
   }

   private static void handle(LatheLoft latheLoft, ForeEar foreEar, ForeEar foreEar2, float tailForeFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float foreScaled = tailForeFactor * 6.9F;
      float latheLoftValue = tailForeFactor * 6.3F;
      latheLoft.getLatheLoftForForeEar(foreEar);
      float[] pointsSpline = LatheLoft.getPointsSpline(2, tailForeFactor * 4.8F, 16.800001F, -1.4F, tailForeFactor * 6.35F, 16.05F, -1.45F, foreScaled, 14.9F, -1.9F);
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 0.9F, 0.78F, 0.64F), 8, shinBrr, false);
      latheLoft.getLatheLoft(foreScaled, 14.9F, -1.9F, 0.64F, 8, shinBrr);
      latheLoft.getLatheLoftForForeEar(foreEar2);
      float[] latheLoftValues = LatheLoft.getPointsSpline(2, foreScaled, 14.9F, -1.9F, tailForeFactor * 6.85F, 14.3F, -3.6F, latheLoftValue, 14.0F, -5.6F);
      latheLoft.getLatheLoft(latheLoftValues, LatheLoft.getAmounts(LatheLoft.getValue(latheLoftValues), 0.62F, 0.54F, 0.46F), 8, shinBrr2, false);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, 13.8F, -6.02F).getLatheLoft4(tailForeFactor * 12.0F).getLatheLoftLatheLoftAmountLoft(35.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.6F, 0.34F, 0.76F, 8, shinBrr2);
      latheLoft.getDequeArray();

      for (int i = 0; i < 5; i++) {
         float tailForeValue = latheLoftValue + tailForeFactor * (i - 2) * 0.24F - tailForeFactor * 0.06F;
         float tailForeLimit = -6.5499997F + Math.abs(i - 2) * 0.14F;
         handle(latheLoft, tailForeValue, 13.55F, tailForeLimit, tailForeValue + tailForeFactor * (i - 2) * 0.12F, 13.0F, tailForeLimit - 0.62F, 0.15F, shinBrr3);
      }
   }

   private static float getAmount(float tailForeLimit, float tailForeLimit2, float tailForeValue) {
      return tailForeLimit < tailForeLimit2 ? tailForeLimit2 : (tailForeLimit > tailForeValue ? tailForeValue : tailForeLimit);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F);
            int bonecaAmbalabuValue2 = bonecaAmbalabuValue < 0.16F
               ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(4148007), ForeShinUtils.getValueForInt(5463086), bonecaAmbalabuValue / 0.16F)
               : (
                  bonecaAmbalabuValue < 0.41F
                     ? ForeShinUtils.getValueForInt(5463086)
                     : ForeShinUtils.getValue2(
                        ForeShinUtils.getValueForInt(5463086),
                        ForeShinUtils.getValueForInt(bonecaAmbalabuFlag ? 6711610 : 11841142),
                        Math.min(1.0F, (bonecaAmbalabuValue - 0.41F) / 0.05F) * (bonecaAmbalabuFlag ? 1.0F : getAmount(((i + 0.5F) / bonecaValue - 0.25F) / 0.45F))
                     )
               );
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3094552), 0.3F, 7.0F, 51);
      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 3.0F, 53);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(3094552, 0.8F), (bonecaAmbalabuValue3, bonecaAmbalabuValue4) -> {
         float var4x = Math.abs(bonecaAmbalabuValue3 / ambalabuBonecaValue - 0.5F);
         float var5x = bonecaAmbalabuValue4 / bonecaValue * 26.0F;
         float var6x = var5x - (float)Math.floor(var5x);
         boolean var7x = var6x < 0.15F && var4x < 0.4F;
         boolean bonecaAmbalabuFlag2 = var4x < 0.38F && (Math.abs(var4x - 0.1F) < 0.01F || Math.abs(var4x - 0.2F) < 0.01F || Math.abs(var4x - 0.29F) < 0.01F);
         return var7x || bonecaAmbalabuFlag2;
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(7240261, 0.6F), (bonecaAmbalabuValue5, bonecaAmbalabuValue6) -> {
         float var4x = Math.abs(bonecaAmbalabuValue5 / ambalabuBonecaValue - 0.5F);
         float var5x = bonecaAmbalabuValue6 / bonecaValue * 26.0F;
         float var6x = var5x - (float)Math.floor(var5x);
         return var4x < 0.38F && var6x >= 0.15F && var6x < 0.22F;
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(9341016, 0.7F), (bonecaAmbalabuValue7, bonecaAmbalabuValue8) -> {
         float var4x = Math.abs(bonecaAmbalabuValue7 / ambalabuBonecaValue - 0.5F);
         float var5x = bonecaAmbalabuValue8 / bonecaValue * 26.0F;
         return var4x > 0.42F && var5x - (float)Math.floor(var5x) < 0.1F;
      });
   }

   private static float getAmounts(float tailForeLimit) {
      float tailForeTotal = 0.0F;
      float tailForeValue = 0.0F;

      for (int i = 0; i + 1 < eyeTable.length; i++) {
         float tailForeValue2 = tailForeTable[i] + 0.5F * table[i];
         float tailForeValue3 = tailForeTable[i + 1] + 0.5F * table[i + 1];
         float hypot = (float)Math.hypot(eyeTable[i + 1] - eyeTable[i], tailForeValue3 - tailForeValue2);
         if (tailForeLimit <= eyeTable[i] && tailForeLimit >= eyeTable[i + 1]) {
            tailForeValue = tailForeTotal + hypot * (eyeTable[i] - tailForeLimit) / (eyeTable[i] - eyeTable[i + 1]);
         }

         tailForeTotal += hypot;
      }

      return tailForeValue / tailForeTotal;
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaValue = bonecaAmbalabu.bonecaValue;
      float bonecaAmbalabuValue = ambalabuBonecaValue * 0.5F;
      float amountForFloat = bonecaValue * 0.5F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(5463086));
      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 3.0F, 81);
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, ambalabuBonecaValue * 0.2F, bonecaValue * 0.24F, ForeShinUtils.getValueForIntFloat(7240261, 0.5F));

      for (int i = -1; i <= 1; i += 2) {
         float classFactor = bonecaAmbalabuValue + i * 4.0F;
         float bonecaAmbalabuValue4 = bonecaAmbalabuValue + i * 5.8F;
         bonecaAmbalabu.getClassForFloatInt(classFactor, amountForFloat - 5.0F, bonecaAmbalabuValue + i * 3.0F, amountForFloat, bonecaAmbalabuValue4, amountForFloat + 5.0F, 2.0F, ForeShinUtils.getValueForInt(2106394));
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuDivisor, int bonecaAmbalabuDivisor2, int bonecaAmbalabuValue) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaValue = bonecaAmbalabu.bonecaValue;
      float bonecaAmbalabuFactor = ambalabuBonecaValue / bonecaAmbalabuDivisor;
      float bonecaAmbalabuFactor2 = bonecaValue / bonecaAmbalabuDivisor2;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float abs2 = Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F);
            bonecaAmbalabu.getBonecaAmbalabu(
               j, i, ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5463086), ForeShinUtils.getValueForInt(11117676), Math.max(0.0F, (abs2 - 0.3F) / 0.2F))
            );
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3094552), 0.25F, 6.0F, bonecaAmbalabuValue);
      bonecaAmbalabu.getBonecaAmbalabu(0.04F, 3.0F, bonecaAmbalabuValue + 1);

      for (int k = 0; k < bonecaAmbalabuDivisor2; k++) {
         for (int m = -1; m <= bonecaAmbalabuDivisor; m++) {
            float classValue = (m + k % 2 * 0.5F) * bonecaAmbalabuFactor;
            float amountForFloat = (k + 0.5F) * bonecaAmbalabuFactor2;
            bonecaAmbalabu.getClass022(classValue, amountForFloat, bonecaAmbalabuFactor * 0.37F, bonecaAmbalabuFactor2 * 0.38F, 1.5F, ForeShinUtils.getValueForIntFloat(3094552, 0.85F));
            bonecaAmbalabu.getClass022(classValue - bonecaAmbalabuFactor * 0.06F, amountForFloat - bonecaAmbalabuFactor2 * 0.08F, bonecaAmbalabuFactor * 0.12F, bonecaAmbalabuFactor2 * 0.12F, ForeShinUtils.getValueForIntFloat(7240261, 0.55F));
         }
      }
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11063160), ForeShinUtils.getValueForInt(8367694));
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(9420122), 0.5F, 9.0F, 11);
      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(13034138, 0.45F), 0.5F, 1.0F, 13);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(3038244, 0.22F), (bonecaAmbalabuValue, bonecaAmbalabuValue2) -> canFlagForFloat(bonecaAmbalabuValue / ambalabuBonecaValue, bonecaAmbalabuValue2 / bonecaValue, 0.15F));
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(3038244, 0.45F), (bonecaAmbalabuValue3, bonecaAmbalabuValue4) -> canFlagForFloat(bonecaAmbalabuValue3 / ambalabuBonecaValue, bonecaAmbalabuValue4 / bonecaValue, 0.08F));
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(3038244), (bonecaAmbalabuValue5, bonecaAmbalabuValue6) -> canFlagForFloat(bonecaAmbalabuValue5 / ambalabuBonecaValue, bonecaAmbalabuValue6 / bonecaValue, 0.0F));
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(2247195, 0.7F), (bonecaAmbalabuValue7, bonecaAmbalabuValue8) -> canFlagForFloat(bonecaAmbalabuValue7 / ambalabuBonecaValue, bonecaAmbalabuValue8 / bonecaValue, -0.1F));
      float bonecaAmbalabuFactor = bonecaValue * 0.07F;
      float bonecaAmbalabuValue9 = bonecaValue * 0.86F;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float min2 = Math.min(j + 0.5F, ambalabuBonecaValue - j - 0.5F);
            float y2 = (i + 0.5F - bonecaAmbalabuValue9) * 1.15F;
            float angle = (float)Math.atan2(y2, j + 0.5F < ambalabuBonecaValue * 0.5F ? min2 : -min2);
            float bonecaAmbalabuValue10 = (float)Math.sqrt(min2 * min2 + y2 * y2)
               / (bonecaAmbalabuFactor * (1.0F + 0.18F * (float)Math.sin(3.0F * angle + 0.7F) + 0.1F * (float)Math.sin(5.0F * angle + 2.1F)));
            float foreShinFactor = (1.0F - getAmount((bonecaAmbalabuValue10 - 0.45F) / 0.55F)) * 0.28F;
            if (!(foreShinFactor <= 0.0F)) {
               float bonecaAmbalabuValue12 = 0.5F
                  + 0.25F * (float)Math.sin(min2 * 0.31F + 1.3F * (float)Math.sin(i * 0.19F))
                  + 0.25F * (float)Math.sin(i * 0.27F + 1.7F * (float)Math.sin(min2 * 0.15F));
               bonecaAmbalabu.getBonecaAmbalabu(
                  j,
                  i,
                  ForeShinUtils.getValue2(
                     bonecaAmbalabu.getBonecaAmbalabuForInt(j, i),
                     ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(14078858), ForeShinUtils.getValueForInt(13222522), bonecaAmbalabuValue12),
                     foreShinFactor
                  )
               );
            }
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 4.0F, 21);
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabuFlag(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(bonecaAmbalabuFlag ? 5463086 : 4148007), ForeShinUtils.getValueForInt(bonecaAmbalabuFlag ? 6711610 : 5463086));
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3094552), 0.3F, 5.0F, 91);
      bonecaAmbalabu.getBonecaAmbalabu(0.04F, 3.0F, 95);
      int bonecaAmbalabuLimit = bonecaAmbalabuFlag ? 16 : 12;

      for (int i = bonecaAmbalabuFlag ? 0 : 2; i < 8; i++) {
         for (int j = 0; j <= bonecaAmbalabuLimit; j++) {
            float bonecaAmbalabuValue = (j + i % 2 * 0.5F) * ambalabuBonecaValue / bonecaAmbalabuLimit;
            float amountForFloat = (i + 0.5F) * bonecaValue / 8.0F;
            bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, ambalabuBonecaValue / (bonecaAmbalabuLimit * 2.3F), bonecaValue / 19.0F, 1.1F, ForeShinUtils.getValueForIntFloat(3094552, 0.75F));
            bonecaAmbalabu.getClass022(bonecaAmbalabuValue - 0.4F, amountForFloat - 0.5F, 0.9F, 0.9F, ForeShinUtils.getValueForIntFloat(7240261, 0.4F));
         }
      }
   }

   @Override
   public String formatNoteBlock() {
      return "entity.evoker_fangs.attack";
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F);
            int bonecaAmbalabuValue2 = bonecaAmbalabuValue < 0.4F
               ? ForeShinUtils.getValueForInt(6711610)
               : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(6711610), ForeShinUtils.getValueForInt(12762244), Math.min(1.0F, (bonecaAmbalabuValue - 0.4F) / 0.06F));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 3.0F, 41);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3094552), 0.2F, 7.0F, 43);

      for (int k = 0; k * 5.0F < bonecaValue; k++) {
         for (int m = 0; m < 6; m++) {
            float bonecaAmbalabuValue3 = 0.2F + m * 0.03F + k % 2 * 0.015F;
            float amountForFloat = k * 5.0F + 2.5F;
            bonecaAmbalabu.getClass022(ambalabuBonecaValue * (0.5F - bonecaAmbalabuValue3), amountForFloat, 1.5F, 1.5F, ForeShinUtils.getValueForIntFloat(3094552, 0.55F));
            bonecaAmbalabu.getClass022(ambalabuBonecaValue * (0.5F + bonecaAmbalabuValue3), amountForFloat, 1.5F, 1.5F, ForeShinUtils.getValueForIntFloat(3094552, 0.55F));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(10261862, 0.6F), (bonecaAmbalabuValue5, bonecaAmbalabuValue6) -> {
         float var4x = Math.abs(bonecaAmbalabuValue5 / ambalabuBonecaValue - 0.5F);
         float var5x = bonecaAmbalabuValue6 / bonecaValue * 18.0F;
         float var6x = var4x * 40.0F;
         return var4x > 0.42F && (var5x - (float)Math.floor(var5x) < 0.1F || bonecaAmbalabuValue6 / bonecaValue < 0.82F && var6x - (float)Math.floor(var6x) < 0.08F);
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(15116192), (bonecaAmbalabuValue7, bonecaAmbalabuValue8) -> Math.abs(bonecaAmbalabuValue7 / ambalabuBonecaValue - 0.5F) < 0.152F);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(14258054), (bonecaAmbalabuValue9, bonecaAmbalabuValue10) -> {
         float var4x = Math.abs(bonecaAmbalabuValue9 / ambalabuBonecaValue - 0.5F) / 0.1F;
         float var5x = (bonecaAmbalabuValue10 / bonecaValue - 0.42F) / 0.36F;
         return var4x * var4x + var5x * var5x < 1.0F;
      });
      handleBonecaAmbalabu(bonecaAmbalabu, 0.152F, true);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(2830617), (bonecaAmbalabuValue11, bonecaAmbalabuValue12) -> {
         float var3x = Math.abs(bonecaAmbalabuValue11 / ambalabuBonecaValue - 0.5F);
         return var3x > 0.152F && var3x < 0.18F;
      });
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F);
            int bonecaAmbalabuValue2 = bonecaAmbalabuValue < 0.24F
               ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(4148007), ForeShinUtils.getValueForInt(5463086), bonecaAmbalabuValue / 0.24F)
               : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5463086), ForeShinUtils.getValueForInt(6711610), Math.min(1.0F, (bonecaAmbalabuValue - 0.24F) / 0.06F));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3094552), 0.3F, 9.0F, 31);
      bonecaAmbalabu.getBonecaAmbalabu(0.04F, 3.0F, 33);
      float bonecaAmbalabuValue3 = getAmounts(-6.5F) * bonecaValue;
      float bonecaAmbalabuLimit = getAmounts(-8.5F) * bonecaValue;
      handleBonecaAmbalabu(bonecaAmbalabu, 0.22F, 9.0F, 7.0F, bonecaAmbalabuValue3, bonecaAmbalabuLimit, 1.0F);

      for (int k = 0; k < 150; k++) {
         float bonecaAmbalabuValue5 = bonecaAmbalabuValue3 + (bonecaValue - bonecaAmbalabuValue3) * getAmount(k * 3 + 501);
         float classValue = ambalabuBonecaValue * (0.5F + 0.52F * (getAmount(k * 3 + 502) - 0.5F));
         float bonecaAmbalabuValue7 = getAmount((bonecaAmbalabuValue5 - bonecaAmbalabuValue3) / (bonecaAmbalabuLimit - bonecaAmbalabuValue3));
         if (!(bonecaAmbalabuValue7 <= 0.05F)) {
            float classDivisor = 1.6F + 1.0F * getAmount(k * 3 + 503);
            bonecaAmbalabu.getClass022(classValue, bonecaAmbalabuValue5, classDivisor * 1.15F, classDivisor, 0.9F, ForeShinUtils.getValueForIntFloat(3094552, 0.55F * bonecaAmbalabuValue7));
            bonecaAmbalabu.getClass022(classValue - 0.4F, bonecaAmbalabuValue5 - 0.5F, classDivisor * 0.45F, classDivisor * 0.4F, ForeShinUtils.getValueForIntFloat(7240261, 0.4F * bonecaAmbalabuValue7));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(3094552, 0.45F), 0.35F, 1.2F, 37);

      for (int m = 0; m * 4.5F < bonecaValue; m++) {
         for (int p = 0; p < 4; p++) {
            float bonecaAmbalabuValue9 = 0.232F + p * 0.022F + m % 2 * 0.011F;
            float amountForFloat = m * 4.5F + 2.0F;
            bonecaAmbalabu.getClass022(ambalabuBonecaValue * (0.5F - bonecaAmbalabuValue9), amountForFloat, 1.4F, 1.4F, ForeShinUtils.getValueForIntFloat(3094552, 0.7F));
            bonecaAmbalabu.getClass022(ambalabuBonecaValue * (0.5F + bonecaAmbalabuValue9), amountForFloat, 1.4F, 1.4F, ForeShinUtils.getValueForIntFloat(3094552, 0.7F));
            bonecaAmbalabu.getClass022(ambalabuBonecaValue * (0.5F - bonecaAmbalabuValue9) - 0.4F, amountForFloat - 0.4F, 0.7F, 0.7F, ForeShinUtils.getValueForIntFloat(7240261, 0.8F));
            bonecaAmbalabu.getClass022(ambalabuBonecaValue * (0.5F + bonecaAmbalabuValue9) - 0.4F, amountForFloat - 0.4F, 0.7F, 0.7F, ForeShinUtils.getValueForIntFloat(7240261, 0.8F));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(2830617), (bonecaAmbalabuValue11, bonecaAmbalabuValue12) -> {
         float var3x = Math.abs(bonecaAmbalabuValue11 / ambalabuBonecaValue - 0.5F);
         return var3x > 0.318F && var3x < 0.346F;
      });
      float bonecaAmbalabuValue13 = getAmounts(-12.2F) * bonecaValue;
      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.178F, bonecaAmbalabuValue13, 3.2F, 2.2F, ForeShinUtils.getValueForInt(2830617));
      bonecaAmbalabu.getClass022(ambalabuBonecaValue * 0.822F, bonecaAmbalabuValue13, 3.2F, 2.2F, ForeShinUtils.getValueForInt(2830617));
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(15116192), (bonecaAmbalabuValue14, bonecaAmbalabuValue15) -> Math.abs(bonecaAmbalabuValue14 / ambalabuBonecaValue - 0.5F) >= 0.346F);
      handleBonecaAmbalabu(bonecaAmbalabu, 0.346F, false);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(13208196, 0.7F), (bonecaAmbalabuValue16, bonecaAmbalabuValue17) -> {
         float var4x = Math.abs(bonecaAmbalabuValue16 / ambalabuBonecaValue - 0.5F);
         float var5x = bonecaAmbalabuValue17 / bonecaValue * 28.0F;
         return var4x > 0.37F && var5x - (float)Math.floor(var5x) < 0.22F;
      });
   }

   private void handleAmountsEye(
      LatheLoft latheLoft,
      ShinBrr shinBrr,
      ShinBrr shinBrr2,
      ShinBrr shinBrr3,
      ShinBrr shinBrr4,
      ShinBrr shinBrr5,
      ShinBrr shinBrr6,
      ShinBrr shinBrr7,
      ShinBrr shinBrr8,
      ShinBrr shinBrr9,
      ShinBrr shinBrr10
   ) {
      int eyeSize = eyeTable.length;
      float[] floats = new float[3 * eyeSize];
      float[] loftPathValues = new float[3 * eyeSize];
      float[] crownForeValues = new float[eyeSize];
      float[] floats4 = new float[eyeSize];
      float[] floats5 = new float[eyeSize];

      for (int i = 0; i < eyeSize; i++) {
         crownForeValues[i] = tailForeTable[i] + 0.5F * table[i];
         floats4[i] = tailForeTable[i] - 0.5F * EYE_THICKNESSES[i];
         floats5[i] = EYE_WIDTHS[i] * 0.93F;
         floats[3 * i + 1] = crownForeValues[i];
         floats[3 * i + 2] = eyeTable[i];
         loftPathValues[3 * i + 1] = floats4[i];
         loftPathValues[3 * i + 2] = eyeTable[i];
      }

      latheLoft.getLatheLoftForForeEar(this.eyeL);
      latheLoft.getLoftPath(floats, EYE_WIDTHS, table, 16, shinBrr, null, null, 0.0F, 1.0F, 0.0F);
      float eyeValue = crownForeValues[0] - 0.05F;
      float armTrunkValue = eyeTable[0] + 0.1F;
      float eyeValue3 = EYE_WIDTHS[0] + 0.35F;
      float eyeValue4 = table[0] + 0.1F;
      float loftLatheFactor = 1.6F;
      latheLoft.getLoftLathe(0.0F, eyeValue, armTrunkValue, eyeValue3, eyeValue4, loftLatheFactor, 12, 0.0F, 1.0F, shinBrr4);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 23.6F, -3.8F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.6F, 12, shinBrr6);
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.classEye);
      latheLoft.getLoftPath(loftPathValues, floats5, EYE_THICKNESSES, 14, shinBrr2, shinBrr5, null, 0.0F, 1.0F, 0.0F);

      for (int j = -1; j <= 1; j += 2) {
         for (int k = 0; k < 7; k++) {
            float tailForeLimit = -6.8F - k * 0.85F;
            float eyeValue7 = getAmount(eyeTable, tailForeTable, tailForeLimit);
            float eyeValue8 = getAmount(eyeTable, EYE_WIDTHS, tailForeLimit);
            float tailForeValue = getAmount(eyeTable, floats4, tailForeLimit);
            float tailForeDivisor = getAmount(eyeTable, EYE_THICKNESSES, tailForeLimit);
            float eyeBound = 0.22F + 0.045F * k + 0.07F * (float)Math.sin(k * 1.9F) + (k >= 4 ? 0.03F : 0.0F);
            if (k < 2) {
               eyeBound = Math.min(eyeBound, 0.16F);
            }

            if (k >= 5) {
               eyeBound = Math.min(eyeBound, k == 6 ? 0.2F : 0.3F);
            }

            float eyeFactor = 0.866F * eyeValue8 + 0.03F;
            float rocketsElytraBound = k < 2 ? eyeFactor : Math.min(eyeFactor + 0.12F, getAmount(getAmount(eyeTable, floats5, tailForeLimit), tailForeValue, tailForeDivisor, eyeValue7 - eyeBound) + 0.04F);
            latheLoft.getLatheLoftForForeEar(this.eyeL);
            handle(latheLoft, j * eyeFactor, eyeValue7 + 0.16F, tailForeLimit, j * Math.max(eyeFactor, rocketsElytraBound), eyeValue7 - eyeBound, tailForeLimit - 0.05F, 0.18F, k < 2 ? shinBrr8 : shinBrr7);
            boolean eyeFlag = k == 6;
            float eyeValue11 = eyeFlag ? -12.2F : tailForeLimit + 0.43F;
            float eyeValue12 = getAmount(eyeTable, tailForeTable, eyeValue11);
            float tailForeFactor = getAmount(eyeTable, EYE_WIDTHS, eyeValue11);
            float eyeValue14 = getAmount(eyeTable, crownForeValues, eyeValue11);
            float eyeValue15 = getAmount(eyeTable, table, eyeValue11);
            float wrapDegreesBound = eyeFlag ? 0.5F : 0.24F + 0.025F * k + 0.06F * (float)Math.cos(k * 1.3F);
            if (k < 2) {
               wrapDegreesBound = Math.min(wrapDegreesBound, 0.16F);
            }

            float pilotStatusBound = 0.866F * getAmount(eyeTable, floats5, eyeValue11) + (eyeFlag ? 0.12F : 0.05F);
            float eyeBound4 = k < 2 ? pilotStatusBound : Math.min(pilotStatusBound + 0.16F, getAmount(tailForeFactor, eyeValue14, eyeValue15, eyeValue12 + wrapDegreesBound) + (eyeFlag ? 0.05F : 0.01F));
            latheLoft.getLatheLoftForForeEar(this.classEye);
            handle(
               latheLoft,
               j * pilotStatusBound,
               eyeValue12 - 0.16F,
               eyeValue11,
               j * Math.max(pilotStatusBound, eyeBound4),
               eyeValue12 + wrapDegreesBound,
               eyeValue11 + 0.04F,
               eyeFlag ? 0.22F : 0.17F,
               k < 2 ? shinBrr8 : shinBrr7
            );
         }

         latheLoft.getLatheLoftForForeEar(this.eyeL);
         latheLoft.getLatheLoft(j * 1.22F, 23.22F, -4.9F, 0.92F, 0.62F, 1.2F, 10, shinBrr3);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(j * 1.3F, 24.02F, -5.25F).getLatheLoft4(-j * 25.0F).getLatheLoft(j * 16.0F).getLatheLoftLatheLoftAmountLoft(14.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.68F, 0.28F, 1.0F, 8, shinBrr3);
         latheLoft.getDequeArray();
         latheLoft.getLatheLoftForForeEar(j > 0 ? this.eyeForeEar : this.classShinFoot);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(j * 1.25F, 23.55F, -5.2F).getLatheLoft4(-j * 38.0F).getLatheLoftLatheLoftAmountLoft(10.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.66F, 0.6F, 0.66F, 12, shinBrr9);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.eyeL);
      float eyeValue16 = -12.7F;
      float eyeValue17 = getAmount(eyeTable, crownForeValues, eyeValue16) + getAmount(eyeTable, table, eyeValue16) * 0.88F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, eyeValue17 + 0.03F, eyeValue16).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.62F, 0.5F, 0.3F, 10, shinBrr10);
      latheLoft.getDequeArray();
   }

   private static void handleAmounts(LatheLoft latheLoft, ShinBrr shinBrr) {
      float[] floats = new float[]{1.85F, 2.12F};

      for (float tailForeValue : floats) {
         int index = Math.min(2, (int)tailForeValue);
         float deltaVar = tailForeValue - index;
         float tailForeValue2 = shinFootTable[3 * index + 1] + (shinFootTable[3 * index + 4] - shinFootTable[3 * index + 1]) * deltaVar;
         float tailForeValue3 = shinFootTable[3 * index + 2] + (shinFootTable[3 * index + 5] - shinFootTable[3 * index + 2]) * deltaVar;
         float tailForeFactor = TAIL_WIDTHS[index] + (TAIL_WIDTHS[index + 1] - TAIL_WIDTHS[index]) * deltaVar;
         float tailForeFactor2 = TAIL_THICKNESSES[index] + (TAIL_THICKNESSES[index + 1] - TAIL_THICKNESSES[index]) * deltaVar;
         float tailForeFactor3 = shinFootTable[3 * index + 4] - shinFootTable[3 * index + 1];
         float tailForeFactor4 = shinFootTable[3 * index + 5] - shinFootTable[3 * index + 2];
         float distance = (float)Math.sqrt(tailForeFactor3 * tailForeFactor3 + tailForeFactor4 * tailForeFactor4);
         float y = tailForeFactor3 / distance;
         float tailForeValue4 = tailForeFactor4 / distance;
         float tailForeFactor5 = -tailForeValue4;
         float tailForeFactor6 = y;
         if (tailForeFactor5 * 0.6F + y * 0.8F < 0.0F) {
            tailForeFactor5 = -tailForeFactor5;
            tailForeFactor6 = -y;
         }

         float degrees = (float)Math.toDegrees(Math.atan2(y, -tailForeValue4));

         for (int i = -1; i <= 1; i += 2) {
            float latheLoftValue = i * 0.62F;
            float tailForeFactor8 = (float)Math.sqrt(Math.max(0.0F, 1.0F - latheLoftValue * latheLoftValue / (tailForeFactor * tailForeFactor))) * tailForeFactor2;
            latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, tailForeValue2 + tailForeFactor5 * tailForeFactor8, tailForeValue3 + tailForeFactor6 * tailForeFactor8).getLatheLoftLatheLoftAmountLoft(degrees).getLatheLoft(-i * 12.0F);
            latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.44F, 0.24F, 0.52F, 6, shinBrr);
            latheLoft.getDequeArray();
         }
      }
   }

   @Override
   public String formatTungDin() {
      return "glorbo_fruttodrillo";
   }

   private void handleTail(LatheLoft latheLoft, float[] tailValues, float[] standCellKeys, float[] shinFootValues, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      int tailValue = LatheLoft.getValue(tailValues);
      int[][] ints = new int[][]{{0, 3}, {2, 5}, {4, 7}, {6, tailValue - 1}};
      ForeEar[] foreEars = new ForeEar[]{this.tailSegmentForeEar, this.classTail, this.classShinProgress, this.tailForeEar};

      for (int i = 0; i < 4; i++) {
         int subSequenceLimit = ints[i][0];
         int tailValue3 = ints[i][1];
         int tailSize = tailValue3 - subSequenceLimit + 1;
         float[] floats = new float[3 * tailSize];
         float[] copyOf = new float[tailSize];
         float[] floats3 = new float[tailSize];
         System.arraycopy(tailValues, 3 * subSequenceLimit, floats, 0, 3 * tailSize);
         System.arraycopy(standCellKeys, subSequenceLimit, copyOf, 0, tailSize);
         System.arraycopy(shinFootValues, subSequenceLimit, floats3, 0, tailSize);
         latheLoft.getLatheLoftForForeEar(foreEars[i]);
         latheLoft.getLoftPath(floats, copyOf, floats3, 10, i == 0 ? shinBrr : shinBrr2, null, null, 0.0F, 1.0F, 0.0F);
      }

      for (int j = 4; j <= 17; j++) {
         float hashRegistryValue = j * 0.5F;
         int index = (int)Math.floor(hashRegistryValue);
         float deltaVar = hashRegistryValue - index;
         int min2 = Math.min(tailValue - 1, index + 1);
         float tailValue5 = tailValues[3 * index + 1] + (tailValues[3 * min2 + 1] - tailValues[3 * index + 1]) * deltaVar;
         float tailValue6 = tailValues[3 * index + 2] + (tailValues[3 * min2 + 2] - tailValues[3 * index + 2]) * deltaVar;
         float tailDivisor = standCellKeys[index] + (standCellKeys[min2] - standCellKeys[index]) * deltaVar;
         float tailFactor = shinFootValues[index] + (shinFootValues[min2] - shinFootValues[index]) * deltaVar;
         float tailFactor2 = tailValues[3 * min2 + 1] - tailValues[3 * index + 1];
         float tailFactor3 = tailValues[3 * min2 + 2] - tailValues[3 * index + 2];
         float distance = (float)Math.sqrt(tailFactor2 * tailFactor2 + tailFactor3 * tailFactor3);
         float tailFactor4 = tailFactor2 / distance;
         float x2 = tailFactor3 / distance;
         latheLoft.getLatheLoftForForeEar(hashRegistryValue < 3.0F ? this.tailSegmentForeEar : (hashRegistryValue < 5.0F ? this.classTail : (hashRegistryValue < 7.0F ? this.classShinProgress : this.tailForeEar)));
         float tailValue7 = 0.22F + 0.45F * tailFactor / shinFootValues[0];
         boolean tailFlag = hashRegistryValue < 5.5F;

         for (int k = tailFlag ? -1 : 0; k <= (tailFlag ? 1 : 0); k += 2) {
            float tailFactor6 = tailFlag ? 0.45F * tailDivisor : 0.0F;
            float tailFactor7 = tailFactor6 / tailDivisor;
            float tailFactor8 = tailFactor * (float)Math.sqrt(Math.max(0.0F, 1.0F - tailFactor7 * tailFactor7)) * 0.92F;
            handleForLatheLoftFloat(latheLoft, k * tailFactor6, tailValue5 + x2 * tailFactor8, tailValue6 - tailFactor4 * tailFactor8, tailFactor4, x2, 1.05F, tailValue7, shinBrr3);
            if (!tailFlag) {
               break;
            }
         }
      }
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float angle = poseProgress.animationProgress * 0.6662F;
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float shinProgressValue = (float)Math.sin(angle) * shinProgressFactor;
      float planValuesItem = (float)Math.cos(angle) * shinProgressFactor;
      float shinProgressValue2 = -0.22F * (float)Math.sin(angle + 0.5F) * shinProgressFactor;
      float shinProgressValue3 = 0.08F * planValuesItem;
      float shinProgressValue4 = 0.45F * Math.abs(shinProgressValue);
      float tailForeLimit2 = 0.0F;
      float shinProgressTotal = 0.0F;
      float shinProgressValue5 = 0.0F;
      float shinProgressTotal2 = 0.5F * poseProgress.getAnimationProgressForFloat(0.0F);
      float shinProgressTotal3 = 0.5F * poseProgress.getAnimationProgressForFloat((float) Math.PI);
      float animationProgress = -poseProgress.getAnimationProgress((float) Math.PI);
      float animationProgress2 = -poseProgress.getAnimationProgress(0.0F);
      float shinProgressValue6 = 1.0F;
      float shinProgressValue7 = 1.0F;
      float shinProgressValue8 = 1.0F;
      float classValue = 1.0F;
      float shinProgressTotal4 = 0.4F * poseProgress.getAnimationProgressForFloat((float) Math.PI);
      float shinProgressTotal5 = 0.4F * poseProgress.getAnimationProgressForFloat(0.0F);
      float shinProgressValue10 = 0.05F * poseProgress.getAgeCode(70.0F, 0.0F);
      float shinProgressValue11 = -0.05F * poseProgress.getAgeCode(70.0F, 1.0F);
      float shinProgressTotal6 = 0.1F;
      float shinProgressValue12 = 0.18F * shinProgressValue + 0.06F * poseProgress.getAgeCode(90.0F, 0.0F);
      float shinProgressValue13 = 0.17F * (float)Math.sin(angle - 0.6F) * shinProgressFactor + 0.06F * poseProgress.getAgeCode(90.0F, -0.7F);
      float dinForeValue = 0.18F * (float)Math.sin(angle - 1.2F) * shinProgressFactor + 0.07F * poseProgress.getAgeCode(90.0F, -1.4F);
      float shinProgressValue15 = 0.2F * (float)Math.sin(angle - 1.8F) * shinProgressFactor + 0.08F * poseProgress.getAgeCode(90.0F, -2.1F);
      float shinProgressValue16 = 0.0F;
      float shinProgressValue17 = 0.0F;
      float foreShinValue = 0.0F;
      float shinProgressValue19 = 0.0F;
      float shinProgressValue20 = 0.0F;
      float shinProgressValue21 = 0.0F;
      float shinProgressLimit = getAmount(poseProgress.shinProgressAmount, -0.6F, 0.7F);
      float max3 = Math.max(0.0F, -shinProgressLimit);
      float shinProgressTotal7 = shinProgressLimit > 0.0F ? shinProgressLimit * 0.8F : shinProgressLimit * 0.45F;
      float shinProgressValue22 = getAmount(poseProgress.headYawRadians, -1.1F, 1.1F) * 0.85F;
      float shinProgressTotal8 = -0.15F;
      float shinProgressValue23 = -0.05F * planValuesItem;
      float shinProgressCounter = 0.6F * max3;
      float shinProgressValue24 = -1.6F * max3;
      float shinProgressValue25 = 0.0F;
      float shinProgressValue26 = 0.0F;
      float shinProgressValue27 = 0.0F;
      float shinProgressFactor4 = 0.0F;
      float angle3 = 0.15F;
      float shinProgressValue28 = 0.25F;
      boolean shinProgressFlag = true;
      if (poseProgress.sneakingPose) {
         tailForeLimit2 -= 0.35F;
         shinProgressTotal2 += 0.72F;
         shinProgressTotal3 += 0.72F;
         animationProgress -= 0.5F;
         animationProgress2 -= 0.5F;
         shinProgressTotal8 += 0.4F;
         shinProgressCounter--;
         shinProgressTotal4 += 0.55F;
         shinProgressTotal5 += 0.55F;
         shinProgressTotal6 += 0.3F;
         float shinProgressValue29 = tailForeLimit2 + shinProgressTotal8 + shinProgressTotal7;
         if (shinProgressValue29 < -0.35F) {
            shinProgressTotal7 += -0.35F - shinProgressValue29;
         }
      }

      if (poseProgress.vehicle) {
         shinProgressTotal2 = 1.45F;
         shinProgressTotal3 = 1.45F;
         animationProgress = -0.4F;
         animationProgress2 = -0.4F;
         angle3 = 0.32F;
         shinProgressValue4 = 0.0F;
      }

      if (poseProgress.usingItem && poseProgress.swingProgress <= 0.0F) {
         shinProgressTotal4 += 0.5F;
         shinProgressTotal6 += 0.3F;
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         float shinProgressValue30 = poseProgress.swimming ? 1.0F : 0.4F;
         float angle4 = poseProgress.age * 0.45F;
         shinProgressTotal2 = -0.9F;
         shinProgressTotal3 = -0.9F;
         animationProgress = 0.0F;
         animationProgress2 = 0.0F;
         shinProgressTotal4 = -0.6F;
         shinProgressTotal5 = -0.6F;
         shinProgressValue10 = 0.5F;
         shinProgressValue11 = -0.5F;
         shinProgressValue12 = 0.22F * shinProgressValue30 * (float)Math.sin(angle4);
         shinProgressValue13 = 0.26F * shinProgressValue30 * (float)Math.sin(angle4 - 0.7F);
         dinForeValue = 0.28F * shinProgressValue30 * (float)Math.sin(angle4 - 1.4F);
         shinProgressValue15 = 0.3F * shinProgressValue30 * (float)Math.sin(angle4 - 2.1F);
         shinProgressFlag = false;
         shinProgressValue4 = 0.0F;
      }

      if (!poseProgress.vehicle && !poseProgress.gliding && !poseProgress.swimming) {
         float min31 = Math.min(getAmount(tailForeLimit2 + shinProgressTotal2, tailForeLimit2 + shinProgressTotal2 + animationProgress), getAmount(tailForeLimit2 + shinProgressTotal3, tailForeLimit2 + shinProgressTotal3 + animationProgress2));
         shinProgressTotal = 1.2F - min31;
      }

      float shinProgressValue32 = -0.75F * tailForeLimit2 + shinProgressTotal / 14.0F + (poseProgress.sneakingPose ? 0.18F : 0.0F) - 0.05F * shinProgressFactor + (poseProgress.vehicle ? -0.25F : 0.0F);
      float max5 = Math.max(0.0F, 1.0F - shinProgressFactor * 3.0F);
      if (!poseProgress.poseProgressActive && poseProgress.swingProgress <= 0.0F) {
         float shinProgressLimit2 = (poseProgress.age + (poseProgress.hashCode & 127) + 60.0F) % 260.0F;
         float shinProgressFactor6;
         if (shinProgressLimit2 < 24.0F) {
            shinProgressFactor6 = getAmount(shinProgressLimit2 / 24.0F);
         } else if (shinProgressLimit2 < 38.0F) {
            shinProgressFactor6 = 1.0F;
         } else if (shinProgressLimit2 < 40.0F) {
            shinProgressFactor6 = 1.0F - (shinProgressLimit2 - 38.0F) / 2.0F;
         } else {
            shinProgressFactor6 = 0.0F;
         }

         float shinProgressValue33 = shinProgressLimit2 >= 38.0F && shinProgressLimit2 < 42.0F ? (float)Math.sin(Math.PI * (shinProgressLimit2 - 38.0F) / 4.0) : 0.0F;
         shinProgressValue27 = -0.22F * shinProgressFactor6 * max5;
         shinProgressTotal8 += (0.07F * shinProgressFactor6 - 0.06F * shinProgressValue33) * max5;
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         shinProgressFactor4 = getAmount(swingProgress / 0.2F) * (1.0F - getAmount((swingProgress - 0.72F) / 0.12F));
         float tailForeLimit = (float) (-Math.PI * 2) * getAmount((swingProgress - 0.03F) / 0.73F);
         float shinProgressFactor7 = 5.8F;
         float shinProgressFactor8 = -0.20000002F;
         float cos = (float)Math.cos(tailForeLimit);
         float sin = (float)Math.sin(tailForeLimit);
         float angle6 = (float)Math.toRadians(20.0) - tailForeLimit;
         float sqrt = (float)Math.sqrt(68.89F * Math.cos(angle6) * Math.cos(angle6) + 39.690002F * Math.sin(angle6) * Math.sin(angle6));
         tailForeLimit2 = tailForeLimit;
         shinProgressTotal = shinProgressFactor7 - (cos * shinProgressFactor7 - sin * shinProgressFactor8) - shinProgressFactor4 * (Math.max(0.0F, 12.6F - sqrt - 0.2F) - getAmount(shinProgressTable, SWING_COEFFICIENTS, tailForeLimit));
         shinProgressValue5 = shinProgressFactor8 - (sin * shinProgressFactor7 + cos * shinProgressFactor8) - 3.0F * (float)Math.sin(Math.PI * swingProgress);
         shinProgressValue2 = 0.0F;
         shinProgressValue3 = 0.0F;
         shinProgressValue4 = 0.0F;
         shinProgressTotal7 *= 1.0F - shinProgressFactor4;
         shinProgressValue22 *= 1.0F - shinProgressFactor4;
         shinProgressTotal8 = -0.15F - 0.35F * shinProgressFactor4;
         shinProgressCounter *= 1.0F - shinProgressFactor4;
         shinProgressValue24 *= 1.0F - shinProgressFactor4;
         classValue = 1.0F - 0.5F * shinProgressFactor4;
         shinProgressValue25 = 1.0F * shinProgressFactor4;
         shinProgressValue26 = -0.3F * shinProgressFactor4;
         shinProgressValue27 = -0.55F * getAmount((swingProgress - 0.8F) / 0.08F) * (1.0F - getAmount((swingProgress - 0.91F) / 0.05F));
         shinProgressTotal2 = 1.5F * shinProgressFactor4;
         shinProgressTotal3 = 1.5F * shinProgressFactor4;
         animationProgress = -1.7F * shinProgressFactor4;
         animationProgress2 = -1.7F * shinProgressFactor4;
         shinProgressValue6 = 1.0F - 0.45F * shinProgressFactor4;
         shinProgressTotal4 = 0.9F * shinProgressFactor4;
         shinProgressTotal5 = 0.9F * shinProgressFactor4;
         shinProgressValue10 = -0.25F * shinProgressFactor4;
         shinProgressValue11 = 0.25F * shinProgressFactor4;
         shinProgressTotal6 = 0.1F + 0.8F * shinProgressFactor4;
         shinProgressValue7 = 1.0F - 0.45F * shinProgressFactor4;
         shinProgressValue32 = -2.4F * shinProgressFactor4;
         shinProgressValue16 = 0.6F * shinProgressFactor4;
         shinProgressValue17 = 0.21F * shinProgressFactor4;
         foreShinValue = 0.34F * shinProgressFactor4;
         shinProgressValue21 = (float) Math.PI * shinProgressFactor4;
         shinProgressValue12 *= 1.0F - shinProgressFactor4;
         shinProgressValue13 *= 1.0F - shinProgressFactor4;
         dinForeValue *= 1.0F - shinProgressFactor4;
         shinProgressValue15 *= 1.0F - shinProgressFactor4;
         shinProgressValue19 = -0.83F * shinProgressFactor4;
         shinProgressValue20 = 0.14F * shinProgressFactor4;
         shinProgressValue8 = 1.0F - 0.2F * shinProgressFactor4;
         angle3 *= 1.0F - shinProgressFactor4;
         shinProgressValue28 *= 1.0F - shinProgressFactor4;
         shinProgressFlag = false;
      }

      if (!poseProgress.vehicle && !poseProgress.gliding && !poseProgress.swimming) {
         shinProgressTotal -= 0.9000001F * (float)Math.sin(angle3) + 5.6000004F * (1.0F - (float)Math.cos(angle3));
      }

      float shinProgressValue34 = 0.012F * poseProgress.getAgeCode(55.0F, 0.0F);
      float shinProgressValue35 = poseProgress.swingProgress > 0.0F ? 0.0F : 0.04F * Math.abs(planValuesItem);
      float shinProgressDivisor = 1.0F + shinProgressValue34 * 0.5F - shinProgressValue35;
      float foreShinFactor = 1.0F + shinProgressValue34 + shinProgressValue35 * 0.6F;
      if (poseProgress.shinProgressActive) {
         shinProgressDivisor *= 0.93F;
         foreShinFactor *= 1.07F;
      }

      foreShinScale.getAmountsForeShin2(this.legL, 0.0F, shinProgressTotal, shinProgressValue5);
      foreShinScale.getAmountsForeShin2(this.armL, 0.0F, shinProgressValue4, 0.0F);
      foreShinScale.getAmounts(this.armL, tailForeLimit2, shinProgressValue3, shinProgressValue2);
      foreShinScale.getAmountsForForeEarFloat(this.armL, foreShinFactor, shinProgressDivisor, foreShinFactor);
      float cos2 = (float)Math.cos(tailForeLimit2);
      float sin2 = (float)Math.sin(tailForeLimit2);
      float shinProgressFactor9 = 1.0F * shinProgressFactor4;
      float shinProgressFactor10 = -0.6F * shinProgressFactor4;
      foreShinScale.getAmounts(this.shinR, tailForeLimit2 + shinProgressTotal2, 0.0F, angle3);
      foreShinScale.getAmounts(this.shinL, tailForeLimit2 + shinProgressTotal3, 0.0F, -angle3);
      foreShinScale.getAmountsForeShin2(this.shinR, -0.7F * shinProgressFactor4, shinProgressFactor9 * cos2 - shinProgressFactor10 * sin2, shinProgressFactor9 * sin2 + shinProgressFactor10 * cos2);
      foreShinScale.getAmountsForeShin2(this.shinL, 0.7F * shinProgressFactor4, shinProgressFactor9 * cos2 - shinProgressFactor10 * sin2, shinProgressFactor9 * sin2 + shinProgressFactor10 * cos2);
      foreShinScale.getClassForForeEarFloat(this.shinR, shinProgressValue6);
      foreShinScale.getClassForForeEarFloat(this.shinL, shinProgressValue6);
      foreShinScale.getAmounts(this.footR, animationProgress, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footL, animationProgress2, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinFootForeEar, shinProgressFlag ? -(tailForeLimit2 + shinProgressTotal2 + animationProgress) : 0.0F, -shinProgressValue28, shinProgressFlag ? -angle3 : 0.0F);
      foreShinScale.getAmounts(this.tailForeForeEar, shinProgressFlag ? -(tailForeLimit2 + shinProgressTotal3 + animationProgress2) : 0.0F, shinProgressValue28, shinProgressFlag ? angle3 : 0.0F);
      foreShinScale.getAmounts(this.foreR, shinProgressTotal4, 0.0F, shinProgressValue10);
      foreShinScale.getAmounts(this.foreL, shinProgressTotal5, 0.0F, shinProgressValue11);
      foreShinScale.getAmountsForeShin2(this.foreR, -0.6F * shinProgressFactor4, 0.0F, 0.3F * shinProgressFactor4);
      foreShinScale.getAmountsForeShin2(this.foreL, 0.6F * shinProgressFactor4, 0.0F, 0.3F * shinProgressFactor4);
      foreShinScale.getClassForForeEarFloat(this.foreR, shinProgressValue7);
      foreShinScale.getClassForForeEarFloat(this.foreL, shinProgressValue7);
      foreShinScale.getAmounts(this.shinProgressForeEar, shinProgressTotal6, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.foreEar, shinProgressTotal6, 0.0F, 0.0F);
      foreShinScale.getAmountsForeShin2(this.tailSegmentForeEar, 0.0F, -shinProgressValue4 + shinProgressValue19, shinProgressValue20);
      foreShinScale.getAmounts(this.tailSegmentForeEar, shinProgressValue32, shinProgressValue12 + shinProgressValue21, 0.0F);
      foreShinScale.getAmounts(this.classTail, shinProgressValue16, shinProgressValue13, 0.0F);
      foreShinScale.getAmounts(this.classShinProgress, shinProgressValue17, dinForeValue, 0.0F);
      foreShinScale.getAmounts(this.tailForeEar, foreShinValue, shinProgressValue15, 0.0F);
      foreShinScale.getClassForForeEarFloat(this.tailSegmentForeEar, shinProgressValue8);
      foreShinScale.getAmountsForeShin2(this.head, 0.0F, shinProgressValue25, shinProgressValue26);
      foreShinScale.getAmounts(this.head, 0.4F * shinProgressTotal7, 0.5F * shinProgressValue22, -shinProgressValue2 * 0.3F);
      foreShinScale.getClassForForeEarFloat(this.head, classValue);
      foreShinScale.getAmountsForeShin2(this.eyeL, 0.0F, shinProgressCounter, shinProgressValue24);
      foreShinScale.getAmounts(this.eyeL, shinProgressTotal8 + 0.6F * shinProgressTotal7, shinProgressValue23 + 0.5F * shinProgressValue22, -shinProgressValue2 * 0.5F);
      foreShinScale.getAmounts(this.classEye, shinProgressValue27, 0.0F, 0.0F);
      float shinProgressLimit3 = (poseProgress.age + (poseProgress.hashCode & 63)) % 110.0F;
      if (shinProgressLimit3 > 105.0F) {
         foreShinScale.getAmountsForForeEarFloat(this.eyeForeEar, 1.0F, 0.15F, 1.0F);
         foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F, 0.15F, 1.0F);
      }
   }

   private static float getAmount(int tailForeValue) {
      int tailForeValue2 = tailForeValue * 374761393 + 668265263;
      int tailForeValue3 = (tailForeValue2 ^ tailForeValue2 >>> 13) * 1274126177;
      return ((tailForeValue3 ^ tailForeValue3 >>> 16) & 65535) / 65535.0F;
   }

   private static float getAmount(float angle, float angle2) {
      float tailForeFactor = -3.4F;
      float tailForeFactor2 = -0.70000005F;
      float tailForeFactor3 = -2.2F;
      float tailForeFactor4 = 0.6F;
      return 6.8F + (float)(Math.cos(angle) * tailForeFactor - Math.sin(angle) * tailForeFactor2 + Math.cos(angle2) * tailForeFactor3 - Math.sin(angle2) * tailForeFactor4);
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float tailForeValue, float tailForeValue2, float armTrunkValue, float tailForeValue4, float x2, float tailForeFactor, float tailForeValue5, ShinBrr shinBrr) {
      float degrees = (float)Math.toDegrees(Math.atan2(-tailForeValue4, x2));
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(tailForeValue, tailForeValue2, armTrunkValue).getLatheLoftLatheLoftAmountLoft(degrees).getLatheLoft4(-90.0F);
      latheLoft.getPolygonExtrude(new float[]{-0.5F * tailForeFactor, -0.2F, 0.5F * tailForeFactor, -0.2F, 0.3F * tailForeFactor, tailForeValue5}, -0.07F, 0.07F, shinBrr, shinBrr, null);
      latheLoft.getDequeArray();
   }

   private static float getAmount(float tailForeFactor) {
      tailForeFactor = tailForeFactor < 0.0F ? 0.0F : (tailForeFactor > 1.0F ? 1.0F : tailForeFactor);
      return tailForeFactor * tailForeFactor * (3.0F - 2.0F * tailForeFactor);
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaValue = bonecaAmbalabu.bonecaValue;
      float bonecaAmbalabuValue = ambalabuBonecaValue * 0.5F;
      float amountForFloat = bonecaValue * 0.5F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13152830));
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(11771956), 0.4F, 5.0F, 71);

      for (int i = 0; i < 28; i++) {
         double angle = i / 28.0 * Math.PI * 2.0;
         float bonecaAmbalabuFactor = 3.0F;
         float bonecaAmbalabuFactor2 = 9.0F + 4.0F * getAmount(i);
         bonecaAmbalabu.getClass02(
            bonecaAmbalabuValue + (float)Math.cos(angle) * bonecaAmbalabuFactor,
            amountForFloat + (float)Math.sin(angle) * bonecaAmbalabuFactor * 0.9F,
            bonecaAmbalabuValue + (float)Math.cos(angle) * bonecaAmbalabuFactor2,
            amountForFloat + (float)Math.sin(angle) * bonecaAmbalabuFactor2 * 0.9F,
            0.9F,
            ForeShinUtils.getValueForIntFloat(9075234, 0.7F)
         );
      }

      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, 15.0F, 15.0F, 4.0F, ForeShinUtils.getValueForIntFloat(7233560, 0.6F));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, 1.7F, 13.0F, ForeShinUtils.getValueForInt(657928));
      bonecaAmbalabu.getClass022(bonecaAmbalabuValue + 4.0F, amountForFloat + 3.0F, 1.4F, 1.9F, ForeShinUtils.getValueForIntFloat(16777215, 0.8F));
      float bonecaAmbalabuValue3 = bonecaValue * 0.38F;
      bonecaAmbalabu.getClassForIntFloatArray(
         ForeShinUtils.getValueForInt(4148007),
         0.0F,
         0.0F,
         ambalabuBonecaValue,
         0.0F,
         ambalabuBonecaValue,
         bonecaAmbalabuValue3 - 2.5F,
         ambalabuBonecaValue * 0.75F,
         bonecaAmbalabuValue3 - 0.6F,
         ambalabuBonecaValue * 0.5F,
         bonecaAmbalabuValue3,
         ambalabuBonecaValue * 0.25F,
         bonecaAmbalabuValue3 - 0.6F,
         0.0F,
         bonecaAmbalabuValue3 - 2.5F
      );
      bonecaAmbalabu.getClassForFloatInt(0.0F, bonecaAmbalabuValue3 - 2.5F, ambalabuBonecaValue * 0.5F, bonecaAmbalabuValue3 + 2.4F, ambalabuBonecaValue, bonecaAmbalabuValue3 - 2.5F, 1.4F, ForeShinUtils.getValueForInt(3094552));
   }

   private static void handle(
      LatheLoft latheLoft, ForeEar foreEar, ForeEar foreEar2, ForeEar foreEar3, float tailForeValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4, ShinBrr shinBrr5
   ) {
      float footScaled = tailForeValue * 3.5F;
      float footScaled2 = tailForeValue * 4.6F;
      float footScaled3 = tailForeValue * 4.4F;
      latheLoft.getLatheLoftForForeEar(foreEar);
      latheLoft.getLatheLoft(
         new float[]{footScaled * 0.8F, 8.0F, 0.6F, footScaled, 6.2000003F, 0.40000004F, footScaled2, 3.4F, -0.1F}, new float[]{1.75F, 1.55F, 1.2F}, 12, shinBrr, false
      );
      latheLoft.getLatheLoft(footScaled2, 3.4F, -0.1F, 1.18F, 8, shinBrr2);
      latheLoft.getLatheLoftForForeEar(foreEar2);
      float tailForeValue2 = 0.5F * (footScaled2 + footScaled3);
      float tailForeValue3 = 2.3000002F;
      float tailForeValue4 = 0.32F;
      latheLoft.getLatheLoft(new float[]{footScaled2, 3.4F, -0.1F, tailForeValue2, tailForeValue3, tailForeValue4, footScaled3, 1.2F, 0.5F}, new float[]{1.18F, 1.1F, 0.95F}, 12, shinBrr2, false);
      latheLoft.getLatheLoftForForeEar(foreEar3);
      float latheLoftValue = footScaled3 + tailForeValue * 0.1F;
      latheLoft.getLatheLoft(latheLoftValue, 0.62F, -0.3F, 1.3F, 0.62F, 1.8F, 8, shinBrr3);

      for (int i = 0; i < 4; i++) {
         float tailForeValue6 = (i - 1.5F) * 0.62F;
         float tailForeValue7 = latheLoftValue + tailForeValue6 * 0.85F;
         float tailForeValue8 = latheLoftValue + tailForeValue6 * 1.4F;
         latheLoft.getLoftPath(
            new float[]{tailForeValue7, 0.5F, -1.4F, tailForeValue8, 0.32F, -2.8F}, new float[]{0.4F, 0.2F}, new float[]{0.34F, 0.17F}, 5, shinBrr4, null, null, 0.0F, 1.0F, 0.0F
         );
         handle(latheLoft, tailForeValue8, 0.32F, -2.75F, tailForeValue8 + tailForeValue6 * 0.12F, 0.1F, -3.45F, 0.19F, shinBrr5);
      }
   }

   private static void handle(LatheLoft latheLoft, float tailForeValue, float tailForeValue2, float tailForeLimit, float tailForeValue4, float tailForeValue5, float tailForeValue6, float tailForeValue7, int loftPathFactor, ShinBrr shinBrr) {
      latheLoft.getLoftPath(new float[]{tailForeValue, tailForeValue2, tailForeLimit, tailForeValue4, tailForeValue5, tailForeValue6}, new float[]{tailForeValue7, tailForeValue7 * 0.1F}, new float[]{tailForeValue7, tailForeValue7 * 0.1F}, loftPathFactor, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
   }

   @Override
   protected int getValueInt() {
      return 1024;
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      float ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      float bonecaValue = bonecaAmbalabu.bonecaValue;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue = Math.abs((j + 0.5F) / ambalabuBonecaValue - 0.5F);
            int bonecaAmbalabuValue2 = bonecaAmbalabuValue < 0.24F
               ? ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(4148007), ForeShinUtils.getValueForInt(5463086), bonecaAmbalabuValue / 0.24F)
               : ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(5463086), ForeShinUtils.getValueForInt(6711610), Math.min(1.0F, (bonecaAmbalabuValue - 0.24F) / 0.12F));
            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue2);
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3094552), 0.3F, 9.0F, 101);
      bonecaAmbalabu.getBonecaAmbalabu(0.04F, 3.0F, 103);
      handleBonecaAmbalabu(bonecaAmbalabu, 0.3F, 8.0F, 8.0F, 0.0F, 0.0F, 0.6F);

      for (int k = 0; k * 5.0F < bonecaValue; k++) {
         for (int m = 0; m < 5; m++) {
            float bonecaAmbalabuValue3 = 0.3F + m * 0.026F + k % 2 * 0.013F;
            float amountForFloat = k * 5.0F + 2.5F;
            bonecaAmbalabu.getClass022(ambalabuBonecaValue * (0.5F - bonecaAmbalabuValue3), amountForFloat, 1.6F, 1.6F, ForeShinUtils.getValueForIntFloat(3094552, 0.5F));
            bonecaAmbalabu.getClass022(ambalabuBonecaValue * (0.5F + bonecaAmbalabuValue3), amountForFloat, 1.6F, 1.6F, ForeShinUtils.getValueForIntFloat(3094552, 0.5F));
         }
      }

      int value2 = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(11841142), ForeShinUtils.getValueForInt(6711610), 0.4F);

      for (int p = 0; p < bonecaAmbalabu.bonecaValue; p++) {
         float bonecaAmbalabuValue6 = (p + 0.5F) / bonecaValue;

         for (int q = 0; q < bonecaAmbalabu.ambalabuBonecaValue; q++) {
            float abs7 = Math.abs((q + 0.5F) / ambalabuBonecaValue - 0.5F);
            float foreShinFactor = getAmount((abs7 - 0.41F) / 0.05F) * getAmount((bonecaAmbalabuValue6 - 0.12F) / 0.4F);
            if (!(foreShinFactor <= 0.0F)) {
               int foreShinValue = ForeShinUtils.getValue(value2, 0.9F + 0.08F * getAmount(q / 10 * 17 + p / 9 * 5));
               if ((p + 0.5F) % (bonecaValue / 10.0F) < 1.4F) {
                  foreShinValue = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(7301696), 0.55F);
               }

               bonecaAmbalabu.getBonecaAmbalabu(q, p, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(q, p), foreShinValue, foreShinFactor));
            }
         }
      }
   }
}
