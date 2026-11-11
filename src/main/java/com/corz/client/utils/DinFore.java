package com.corz.client.utils;

import java.util.Arrays;
import java.util.Random;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public final class DinFore extends WarmBrainrotBase {
   private ForeEar classShinFoot;
   private ForeEar armL;
   private ForeEar footR;
   private ForeEar classShinProgress;
   private ForeEar shinFootForeEar;
   private ForeEar body;
   private ForeEar shinProgressForeEar;
   private ForeEar foreR;
   private ForeEar foreL;
   private ForeEar footL;
   private ForeEar dinForeForeEar;
   private ForeEar foreEar;
   private ForeEar shinL;
   private ForeEar foreRightSection;
   private ForeEar shinR;

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16118248));
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(14208442, 0.8F), (bonecaAmbalabuValue, bonecaAmbalabuValue2) -> getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, bonecaAmbalabuValue, bonecaAmbalabuValue2) > 0.5F);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(1969926), (bonecaAmbalabuValue3, bonecaAmbalabuValue4) -> getAmount(bonecaAmbalabu, bonecaAmbalabuValue3, bonecaAmbalabuValue4, 0.0F, -0.04F) < 0.74F);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(3810324), (bonecaAmbalabuValue5, bonecaAmbalabuValue6) -> getAmount(bonecaAmbalabu, bonecaAmbalabuValue5, bonecaAmbalabuValue6, 0.0F, -0.04F) < 0.64F);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(7029798), (dinForeValue, bonecaAmbalabuValue8) -> {
         float bonecaAmbalabuLimit = getAmount(bonecaAmbalabu, dinForeValue, bonecaAmbalabuValue8, 0.0F, -0.04F);
         return bonecaAmbalabuLimit < 0.56F && bonecaAmbalabuLimit > 0.37F && (int)(dinForeValue * 0.9F) % 3 == 0;
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(722950), (bonecaAmbalabuValue9, bonecaAmbalabuValue10) -> getAmount(bonecaAmbalabu, bonecaAmbalabuValue9, bonecaAmbalabuValue10, 0.0F, -0.04F) < 0.32F);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(16777215), (bonecaAmbalabuValue11, bonecaAmbalabuValue12) -> getAmount(bonecaAmbalabu, bonecaAmbalabuValue11, bonecaAmbalabuValue12, 0.22F, 0.19F) < 0.15F);
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(12853290)).getBonecaAmbalabu(0.05F, 6.0F, 47);
      bonecaAmbalabu.getClass022(0.5F * ambalabuBonecaValue, 0.5F * bonecaValue, 0.1F * ambalabuBonecaValue, 0.32F * bonecaValue, ForeShinUtils.getValueForIntFloat(15224922, 0.45F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.2F * bonecaValue, ambalabuBonecaValue, 0.26F * bonecaValue, ForeShinUtils.getValueForInt(9311264));
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForInt(9311264), (bonecaAmbalabuValue, bonecaAmbalabuValue2) -> getAmount4(bonecaAmbalabuValue / ambalabuBonecaValue, bonecaAmbalabuValue2 / bonecaValue) > -0.035F);

      for (int i = 0; i < bonecaValue; i++) {
         for (int j = 0; j < ambalabuBonecaValue; j++) {
            if (getAmount4((j + 0.5F) / ambalabuBonecaValue, (i + 0.5F) / bonecaValue) > 0.0F) {
               bonecaAmbalabu.getBonecaAmbalabu(j, i, 0);
            }
         }
      }
   }

   @Override
   public String formatTungDin() {
      return "udin_din_din_dun";
   }

   private static float getAmount(float dinForeBound, float wrapDegreesBound) {
      return Math.max(-wrapDegreesBound, Math.min(wrapDegreesBound, dinForeBound));
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14646308)).getBonecaAmbalabu(0.04F, 8.0F, 57);

      for (int i = 0; i < 2; i++) {
         float bonecaAmbalabuValue = (0.25F + 0.5F * i) * ambalabuBonecaValue;
         handle(bonecaAmbalabu, bonecaAmbalabuValue, 0.42F * bonecaValue, 0.16F * ambalabuBonecaValue, 0.34F * bonecaValue, 16754253, 0.55F);
         bonecaAmbalabu.getClassForFloatInt(bonecaAmbalabuValue - 0.12F * ambalabuBonecaValue, 0.62F * bonecaValue, bonecaAmbalabuValue, 0.98F * bonecaValue, bonecaAmbalabuValue + 0.12F * ambalabuBonecaValue, 0.62F * bonecaValue, 1.6F, ForeShinUtils.getValueForIntFloat(10768911, 0.45F));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.84F * bonecaValue, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(10768911, 0.4F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, 0.08F * bonecaValue, ForeShinUtils.getValueForIntFloat(10768911, 0.3F));
   }

   @Override
   public String formatTungDinString() {
      return "Udin Din Din Dun";
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2) {
      float angle = bonecaAmbalabuValue2 / bonecaAmbalabu.bonecaValue * (float) Math.PI * 0.5F;
      float angle2 = (float) (Math.PI * 2) * (bonecaAmbalabuValue / bonecaAmbalabu.ambalabuBonecaValue - 0.5F);
      return -((float)Math.sin(angle)) * (float)Math.sin(angle2) * 1.9F;
   }

   private static float getAmountForFloat(float angle, float dinForeValue) {
      float angle2 = angle + dinForeValue;
      return 14.4F - 7.199999F * (float)Math.cos(angle) + 0.2F * (float)Math.sin(angle) - 7.2000003F * (float)Math.cos(angle2) - 0.35F * (float)Math.sin(angle2);
   }

   private static float getAmount(float dinForeFactor) {
      dinForeFactor = dinForeFactor < 0.0F ? 0.0F : (dinForeFactor > 1.0F ? 1.0F : dinForeFactor);
      return dinForeFactor * dinForeFactor * (3.0F - 2.0F * dinForeFactor);
   }

   private static float getAmount2(float dinForeValue) {
      float dinForeFactor = (dinForeValue - 26.6F) / 8.3F;
      return 8.8F * (float)Math.sqrt(Math.max(1.0E-4F, 1.0F - dinForeFactor * dinForeFactor));
   }

   @Override
   public String formatNoteBlock() {
      return "block.note_block.basedrum";
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(9308960);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13111342)).getBonecaAmbalabu(0.03F, 4.0F, 59);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, 0.12F * bonecaValue, bonecaAmbalabuValue);

      for (int i = 0; i < 24; i++) {
         float classBound = (i + 0.25F) * ambalabuBonecaValue / 24.0F;
         bonecaAmbalabu.getClass02(classBound, 0.22F * bonecaValue, classBound + 0.45F * ambalabuBonecaValue / 24.0F, 0.22F * bonecaValue, 0.9F, ForeShinUtils.getValueForIntFloat(15907008, 0.7F));
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, 1.5F, bonecaValue, bonecaAmbalabuValue);
      bonecaAmbalabu.getClassBonecaAmbalabu(ambalabuBonecaValue - 1.5F, 0.0F, ambalabuBonecaValue, bonecaValue, bonecaAmbalabuValue);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.9F * bonecaValue, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(bonecaAmbalabuValue, 0.6F));
   }

   private static void handle(LatheLoft latheLoft, float dinForeValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float sideScaled = dinForeValue * 9.6F;
      latheLoft.getLatheLoft(sideScaled + dinForeValue * 0.35F, 24.7F, 0.05F, 3.0F, 2.8F, 2.9F, 12, shinBrr2);
      float[] floats = new float[]{
         sideScaled + dinForeValue * 0.3F,
         25.0F,
         0.1F,
         sideScaled + dinForeValue * 0.45F,
         22.5F,
         -0.4F,
         sideScaled + dinForeValue * 0.5F,
         20.3F,
         -0.95F,
         sideScaled + dinForeValue * 0.45F,
         18.400002F,
         -0.5F,
         sideScaled + dinForeValue * 0.4F,
         17.2F,
         0.0F
      };
      latheLoft.getLoftPath(floats, new float[]{2.6F, 2.8F, 3.0F, 2.4F, 1.8F}, new float[]{2.5F, 2.7F, 2.8F, 2.4F, 1.85F}, 12, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(sideScaled + dinForeValue * 0.45F, 20.3F, -2.3F).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 2.4F, 2.75F, 2.5F, 12, 0.0F, 0.5F, shinBrr3);
      latheLoft.getDequeArray();
   }

   private static void handle(LatheLoft latheLoft, float dinForeValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float latheLoftValue = dinForeValue * 3.62F;
      latheLoft.getLatheLoft(latheLoftValue, 10.6F, -0.2F, 2.35F, 8, shinBrr2);
      latheLoft.getLatheLoft(latheLoftValue, 10.900001F, -2.2F, 1.0F, 1.1F, 0.6F, 6, shinBrr2);
      float[] pointsSpline = LatheLoft.getPointsSpline(
         2, latheLoftValue, 11.200001F, -0.2F, latheLoftValue + dinForeValue * 0.08F, 9.200001F, 0.5F, latheLoftValue + dinForeValue * 0.16F, 6.7F, 0.35F, latheLoftValue + dinForeValue * 0.18F, 3.8000002F, 0.3F
      );
      int latheLoftSize = LatheLoft.getValue(pointsSpline);
      latheLoft.getLoftPath(
         pointsSpline,
         LatheLoft.getAmounts(latheLoftSize, 2.2F, 2.75F, 1.75F, 1.4F),
         LatheLoft.getAmounts(latheLoftSize, 2.2F, 2.85F, 1.8F, 1.45F),
         10,
         shinBrr,
         null,
         null,
         0.0F,
         0.0F,
         -1.0F
      );
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(3439138), ForeShinUtils.getValueForInt(4165418)).getBonecaAmbalabu(0.05F, 6.0F, 45);
      bonecaAmbalabu.getClass022(0.4F * ambalabuBonecaValue, 0.42F * bonecaValue, 0.12F * ambalabuBonecaValue, 0.25F * bonecaValue, ForeShinUtils.getValueForIntFloat(9426026, 0.35F));
      bonecaAmbalabu.getClass02(0.5F * ambalabuBonecaValue, 0.0F, 0.5F * ambalabuBonecaValue, (float)bonecaValue, 1.6F, ForeShinUtils.getValueForInt(8109909));
      bonecaAmbalabu.getClass02(0.0F, 0.0F, 0.0F, (float)bonecaValue, 1.6F, ForeShinUtils.getValueForInt(8109909));
      bonecaAmbalabu.getClass02((float)ambalabuBonecaValue, 0.0F, (float)ambalabuBonecaValue, (float)bonecaValue, 1.6F, ForeShinUtils.getValueForInt(8109909));

      for (int i = 1; i < 6; i++) {
         float bonecaAmbalabuValue = i * bonecaValue / 6.5F;
         bonecaAmbalabu.getClass02(0.5F * ambalabuBonecaValue, bonecaAmbalabuValue, 0.3F * ambalabuBonecaValue, bonecaAmbalabuValue + 0.12F * bonecaValue, 1.0F, ForeShinUtils.getValueForIntFloat(8109909, 0.8F));
         bonecaAmbalabu.getClass02(0.5F * ambalabuBonecaValue, bonecaAmbalabuValue, 0.7F * ambalabuBonecaValue, bonecaAmbalabuValue + 0.12F * bonecaValue, 1.0F, ForeShinUtils.getValueForIntFloat(8109909, 0.8F));
      }
   }

   private void handle(LatheLoft latheLoft, int dinForeValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float sideScaled = dinForeValue * 3.0F;
      float armTrunkValue = getAmount6(sideScaled, 29.0F) + 0.3F;
      float amountForFloat = LatheLoft.getAmountForFloat(sideScaled, getAmount2(29.0F));
      float degrees = (float)Math.toDegrees(Math.asin(0.2891566F)) * 0.9F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(sideScaled, 29.0F, armTrunkValue).getLatheLoft4(amountForFloat).getLatheLoftLatheLoftAmountLoft(degrees);
      latheLoft.getDequeArrayArrayDeque().getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 1.9F, 0.62F, 1.15F, 10, 0.0F, 0.5F, shinBrr);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(dinForeValue * 14.0F);
      latheLoft.getLoftLathe(0.0F, 0.12F, 0.06F, 2.1F, 1.35F, 0.84F, 12, 0.0F, 0.44F, shinBrr2);
      latheLoft.getDequeArray();
      latheLoft.getLoftLathe(0.0F, -0.05F, 0.1F, 2.02F, 1.23F, 0.74F, 10, 0.72F, 1.0F, shinBrr3);
      latheLoft.getDequeArray();
   }

   @Override
   public float getAmountFloat() {
      return 0.8F;
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14646308)).getBonecaAmbalabu(0.04F, 12.0F, 35);
      handle(bonecaAmbalabu, 0.5F * ambalabuBonecaValue, 0.42F * bonecaValue, 0.14F * ambalabuBonecaValue, 0.34F * bonecaValue, 16754253, 0.55F);
      bonecaAmbalabu.getClass02(0.4F * ambalabuBonecaValue, 0.12F * bonecaValue, 0.45F * ambalabuBonecaValue, 0.78F * bonecaValue, 2.6F, ForeShinUtils.getValueForIntFloat(10768911, 0.55F));
      bonecaAmbalabu.getClass02(0.6F * ambalabuBonecaValue, 0.12F * bonecaValue, 0.55F * ambalabuBonecaValue, 0.78F * bonecaValue, 2.6F, ForeShinUtils.getValueForIntFloat(10768911, 0.55F));
      bonecaAmbalabu.getClass022(0.36F * ambalabuBonecaValue, 0.84F * bonecaValue, 0.07F * ambalabuBonecaValue, 0.1F * bonecaValue, ForeShinUtils.getValueForIntFloat(16754253, 0.45F));
      bonecaAmbalabu.getClass022(0.64F * ambalabuBonecaValue, 0.84F * bonecaValue, 0.07F * ambalabuBonecaValue, 0.1F * bonecaValue, ForeShinUtils.getValueForIntFloat(16754253, 0.45F));
      bonecaAmbalabu.getClassForFloatInt(0.29F * ambalabuBonecaValue, 0.75F * bonecaValue, 0.33F * ambalabuBonecaValue, 0.98F * bonecaValue, 0.42F * ambalabuBonecaValue, 0.92F * bonecaValue, 2.2F, ForeShinUtils.getValueForIntFloat(10768911, 0.55F));
      bonecaAmbalabu.getClassForFloatInt(0.71F * ambalabuBonecaValue, 0.75F * bonecaValue, 0.67F * ambalabuBonecaValue, 0.98F * bonecaValue, 0.58F * ambalabuBonecaValue, 0.92F * bonecaValue, 2.2F, ForeShinUtils.getValueForIntFloat(10768911, 0.55F));
      bonecaAmbalabu.getClass02(0.02F * ambalabuBonecaValue, 0.2F * bonecaValue, 0.02F * ambalabuBonecaValue, 0.9F * bonecaValue, 2.0F, ForeShinUtils.getValueForIntFloat(10768911, 0.25F));
      bonecaAmbalabu.getClass02(0.98F * ambalabuBonecaValue, 0.2F * bonecaValue, 0.98F * ambalabuBonecaValue, 0.9F * bonecaValue, 2.0F, ForeShinUtils.getValueForIntFloat(10768911, 0.25F));
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14646308)).getBonecaAmbalabu(0.04F, 8.0F, 33);
      bonecaAmbalabu.getClass022(0.5F * ambalabuBonecaValue, 0.3F * bonecaValue, 0.2F * ambalabuBonecaValue, 0.26F * bonecaValue, ForeShinUtils.getValueForIntFloat(16754253, 0.45F));
      bonecaAmbalabu.getClass02(0.3F * ambalabuBonecaValue, 0.08F * bonecaValue, 0.42F * ambalabuBonecaValue, 0.92F * bonecaValue, 2.2F, ForeShinUtils.getValueForIntFloat(10768911, 0.5F));
      bonecaAmbalabu.getClass02(0.72F * ambalabuBonecaValue, 0.1F * bonecaValue, 0.62F * ambalabuBonecaValue, 0.9F * bonecaValue, 2.0F, ForeShinUtils.getValueForIntFloat(10768911, 0.4F));
      bonecaAmbalabu.getClassForFloatArrayFloat(
         new float[]{0.55F * ambalabuBonecaValue, 0.5F * ambalabuBonecaValue, 0.53F * ambalabuBonecaValue, 0.48F * ambalabuBonecaValue},
         new float[]{0.05F * bonecaValue, 0.35F * bonecaValue, 0.6F * bonecaValue, 0.95F * bonecaValue},
         1.2F,
         ForeShinUtils.getValueForIntFloat(16229462, 0.85F)
      );
   }

   private static float getAmount(BonecaAmbalabu bonecaAmbalabu, float dinForeValue, float dinForeValue2, float dinForeValue3, float dinForeValue4) {
      float dinForeFactor = getBonecaAmbalabu(bonecaAmbalabu, dinForeValue, dinForeValue2) - dinForeValue3;
      float dinForeFactor2 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, dinForeValue, dinForeValue2) - dinForeValue4;
      return (float)Math.sqrt(dinForeFactor * dinForeFactor + dinForeFactor2 * dinForeFactor2);
   }

   private void handle(LatheLoft latheLoft, ShinBrr shinBrr) {
      ShinBrr max2 = latheLoft.getMax(5982754);
      latheLoft.getLatheLoft(LatheLoft.getPointsSpline(1, 0.0F, 34.7F, 0.0F, 0.15F, 35.6F, 0.05F, 0.45F, 36.3F, 0.15F), new float[]{0.42F, 0.36F, 0.3F}, 6, max2, false);
      float[] pointsSpline = LatheLoft.getPointsSpline(2, 0.45F, 36.25F, 0.15F, 2.1F, 37.5F, -0.7F, 4.0F, 38.1F, -1.9F, 5.6F, 37.6F, -3.1F);
      int dinForeValue = LatheLoft.getValue(pointsSpline);
      latheLoft.getLoftPath(
         pointsSpline,
         LatheLoft.getAmounts(dinForeValue, 0.15F, 1.35F, 1.2F, 0.06F),
         LatheLoft.getAmounts(dinForeValue, 0.1F, 0.22F, 0.18F, 0.04F),
         6,
         shinBrr,
         null,
         null,
         -0.3F,
         0.8F,
         -0.5F
      );
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.body = latheLoft.getClassForStringForeEar("pelvis", latheLoft.getClassForeEarList(), 0.0F, 17.8F, 0.0F);
      this.armL = latheLoft.getClassForStringForeEar("body", this.body, 0.0F, 18.6F, 0.0F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("eyes", this.armL, 0.0F, 29.0F, -7.9F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("brows", this.armL, 0.0F, 30.8F, -7.6F);
      this.foreEar = latheLoft.getClassForStringForeEar("leaf", this.armL, 0.0F, 35.0F, 0.0F);
      this.foreR = latheLoft.getClassForStringForeEar("arm_r", this.armL, 9.6F, 24.6F, 0.0F);
      this.foreRightSection = latheLoft.getClassForStringForeEar("fore_r", this.foreR, 10.0F, 17.2F, 0.0F);
      this.foreL = latheLoft.getClassForStringForeEar("arm_l", this.armL, -9.6F, 24.6F, 0.0F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("fore_l", this.foreL, -10.0F, 17.2F, 0.0F);
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", this.body, 3.5F, 17.8F, 0.0F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 3.62F, 10.6F, -0.2F);
      this.dinForeForeEar = latheLoft.getClassForStringForeEar("foot_r", this.footR, 3.8F, 3.4F, 0.15F);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", this.body, -3.5F, 17.8F, 0.0F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -3.62F, 10.6F, -0.2F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -3.8F, 3.4F, 0.15F);
      latheLoft.getLatheLoft(this.foreR);
      LoftLatheFlag.Region region = latheLoft.getMax(504, 252);
      this.handle(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(96, 64);
      handleBonecaAmbalabu9(max2.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max3 = latheLoft.getMax(64, 48);
      handleBonecaAmbalabu7(max3.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max4 = latheLoft.getMax(96, 64);
      handleBonecaAmbalabu6(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(80, 64);
      handleBonecaAmbalabu14(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(160, 96);
      handleBonecaAmbalabu10(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(96, 16);
      handleBonecaAmbalabu13(max7.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max8 = latheLoft.getMax(96, 24);
      handleBonecaAmbalabu4(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(48, 64);
      handleBonecaAmbalabu5(max9.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max10 = latheLoft.getMax(128, 48);
      handleBonecaAmbalabuForBonecaAmbalabu(max10.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max11 = latheLoft.getMax(96, 48);
      handleBonecaAmbalabu(max11.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max12 = latheLoft.getMax(16, 24);
      handleBonecaAmbalabu(max12.getClassBonecaAmbalabu(), false);
      LoftLatheFlag.Region max13 = latheLoft.getMax(16, 12);
      handleBonecaAmbalabu(max13.getClassBonecaAmbalabu(), true);
      LoftLatheFlag.Region max14 = latheLoft.getMax(48, 32);
      handleForBonecaAmbalabu(max14.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max15 = latheLoft.getMax(40, 24);
      handleBonecaAmbalabu11(max15.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max16 = latheLoft.getMax(48, 48);
      handleBonecaAmbalabu12(max16.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max17 = latheLoft.getMax(64, 32);
      handleBonecaAmbalabu3(max17.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max18 = latheLoft.getMax(32, 16);
      handleBonecaAmbalabu8(max18.getClassBonecaAmbalabu());
      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion3 = latheLoft.getClassForRegion(max14);
      ShinBrr classForRegion4 = latheLoft.getClassForRegion(max16);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max17);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max18);
      latheLoft.getLatheLoftForForeEar(this.armL);
      latheLoft.getLatheLoft(0.0F, 26.6F, 0.0F, 8.8F, 8.3F, 8.8F, 22, classForRegion);
      this.handleForLatheLoftShinBrr(latheLoft, latheLoft.getClassForRegion(max10));
      this.handle3(latheLoft, latheLoft.getClassForRegion(max15));
      this.handle(latheLoft);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max11);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max12);
      ShinBrr classForRegion9 = latheLoft.getClassForRegion(max13);

      for (int i = -1; i <= 1; i += 2) {
         this.handle(latheLoft, (int)i, classForRegion7, classForRegion8, classForRegion9);
      }

      latheLoft.getLatheLoftForForeEar(this.classShinProgress);

      for (int j = -1; j <= 1; j += 2) {
         this.handle(latheLoft, j);
      }

      latheLoft.getLatheLoftForForeEar(this.foreEar);
      this.handle(latheLoft, latheLoft.getClassForRegion(max9));
      ShinBrr classForRegion10 = latheLoft.getClassForRegion(max2);
      ShinBrr classForRegion11 = latheLoft.getClassForRegion(max3);
      latheLoft.getLatheLoftForForeEar(this.foreR);
      handle(latheLoft, 1.0F, classForRegion10, classForRegion5, classForRegion4);
      latheLoft.getLatheLoftForForeEar(this.foreRightSection);
      handleForLatheLoftFloat(latheLoft, 1.0F, classForRegion11, classForRegion3, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.foreL);
      handle(latheLoft, -1.0F, classForRegion10, classForRegion5, classForRegion4);
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      handleForLatheLoftFloat(latheLoft, -1.0F, classForRegion11, classForRegion3, classForRegion6);
      ShinBrr shinBrr = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion13 = latheLoft.getClassForRegion(max5);
      ShinBrr classForRegion14 = latheLoft.getClassForRegion(max6);
      ShinBrr classForRegion15 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion16 = latheLoft.getClassForRegion(max8);
      latheLoft.getLatheLoftForForeEar(this.shinR);
      handle3(latheLoft, 1.0F, shinBrr, classForRegion3);
      latheLoft.getLatheLoftForForeEar(this.footR);
      handle(latheLoft, 1.0F, classForRegion13, classForRegion3);
      handle(latheLoft, 1.0F, classForRegion16);
      latheLoft.getLatheLoftForForeEar(this.dinForeForeEar);
      handleForLatheLoftFloatShinBrr(latheLoft, 1.0F, classForRegion14, classForRegion15);
      latheLoft.getLatheLoftForForeEar(this.shinL);
      handle3(latheLoft, -1.0F, shinBrr, classForRegion3);
      latheLoft.getLatheLoftForForeEar(this.footL);
      handle(latheLoft, -1.0F, classForRegion13, classForRegion3);
      handle(latheLoft, -1.0F, classForRegion16);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handleForLatheLoftFloatShinBrr(latheLoft, -1.0F, classForRegion14, classForRegion15);
   }

   private static float[] getAmounts(float[] dinForeValues, float dinForeLimit, boolean dinForeFlag) {
      int dinForeLimit2 = dinForeValues.length / 2;
      int dinForeCounter = 0;
      float[] floats = new float[4 * dinForeLimit2];

      for (int i = 0; i < dinForeLimit2; i++) {
         int dinForeValue = (i + 1) % dinForeLimit2;
         float dinForeValue2 = dinForeValues[2 * i];
         float dinForeLimit3 = dinForeValues[2 * i + 1];
         float dinForeValue3 = dinForeValues[2 * dinForeValue];
         float dinForeLimit4 = dinForeValues[2 * dinForeValue + 1];
         boolean dinForeFlag2 = dinForeFlag ? dinForeLimit3 <= dinForeLimit : dinForeLimit3 >= dinForeLimit;
         boolean dinForeFlag3 = dinForeFlag ? dinForeLimit4 <= dinForeLimit : dinForeLimit4 >= dinForeLimit;
         if (dinForeFlag2 != dinForeFlag3) {
            float dinForeFactor = (dinForeLimit - dinForeLimit3) / (dinForeLimit4 - dinForeLimit3);
            floats[dinForeCounter++] = dinForeValue2 + (dinForeValue3 - dinForeValue2) * dinForeFactor;
            floats[dinForeCounter++] = dinForeLimit;
         }

         if (dinForeFlag3) {
            floats[dinForeCounter++] = dinForeValue3;
            floats[dinForeCounter++] = dinForeLimit4;
         }
      }

      return Arrays.copyOf(floats, dinForeCounter);
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float dinForeFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float skinScaled = dinForeFactor * 10.0F;
      latheLoft.getLatheLoft(skinScaled, 17.2F, 0.4F, 2.45F, 8, shinBrr2);
      float[] floats = new float[]{
         skinScaled,
         17.800001F,
         0.0F,
         skinScaled + dinForeFactor * 0.1F,
         15.800001F,
         -0.3F,
         skinScaled + dinForeFactor * 0.05F,
         14.000001F,
         -0.2F,
         skinScaled - dinForeFactor * 0.05F,
         11.900001F,
         -0.05F,
         skinScaled - dinForeFactor * 0.1F,
         10.5F,
         0.0F
      };
      latheLoft.getLoftPath(
         floats, new float[]{2.2F, 2.55F, 2.3F, 1.75F, 1.55F}, new float[]{2.1F, 2.45F, 2.2F, 1.7F, 1.5F}, 12, shinBrr, null, null, 0.0F, 0.0F, -1.0F
      );
      float dinForeValue = skinScaled - dinForeFactor * 0.1F;
      float eyeValue = 8.700001F;
      float dinForeValue3 = -0.3F;
      latheLoft.getLatheLoft(dinForeValue, eyeValue, dinForeValue3, 2.9F, 2.7F, 2.6F, 12, shinBrr2);
      float[] floats2 = new float[]{-1.35F, -0.45F, 0.45F, 1.35F};
      float[] floats3 = new float[]{-1.0F, -1.15F, -1.15F, -1.0F};

      for (int i = 0; i < 4; i++) {
         latheLoft.getLatheLoft(dinForeValue + dinForeFactor * floats2[i], eyeValue + floats3[i], dinForeValue3 - 2.0F, 0.66F, 0.72F, 0.72F, 6, shinBrr3);
      }

      latheLoft.getLatheLoft(
         new float[]{
            dinForeValue - dinForeFactor * 2.3F,
            eyeValue + 0.4F,
            dinForeValue3 - 0.9F,
            dinForeValue - dinForeFactor * 1.75F,
            eyeValue - 0.5F,
            dinForeValue3 - 2.35F,
            dinForeValue - dinForeFactor * 0.75F,
            eyeValue - 0.75F,
            dinForeValue3 - 2.85F,
            dinForeValue + dinForeFactor * 0.05F,
            eyeValue - 0.7F,
            dinForeValue3 - 2.85F
         },
         new float[]{0.85F, 0.78F, 0.62F, 0.35F},
         6,
         shinBrr2,
         false
      );
   }

   private void handle(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15895580));
      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 20.0F, 3);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16228410), 0.22F, 14.0F, 5);
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14711314), 0.18F, 9.0F, 6);
      handleBonecaAmbalabu(bonecaAmbalabu, 1.6F, 1.0F, 11);
      bonecaAmbalabu.getBonecaAmbalabu(0.02F, 2.0F, 17);
      bonecaAmbalabu.getClass022(getBonecaAmbalabuFloat(bonecaAmbalabu, -3.6F, 33.0F), getAmount(bonecaAmbalabu, 33.0F), 24.699999F, 14.25F, ForeShinUtils.getValueForIntFloat(16756813, 0.45F));
      bonecaAmbalabu.getClass022(getBonecaAmbalabuFloat(bonecaAmbalabu, -3.9F, 33.3F), getAmount(bonecaAmbalabu, 33.3F), 12.349999F, 7.125F, ForeShinUtils.getValueForInt(16774370));
      handle(bonecaAmbalabu, getBonecaAmbalabuFloat(bonecaAmbalabu, 5.6F, 30.5F), getAmount(bonecaAmbalabu, 30.5F), 13.3F, 20.9F, 16756813, 0.2F);

      for (int i = -1; i <= 1; i += 2) {
         handle(bonecaAmbalabu, getBonecaAmbalabuFloat(bonecaAmbalabu, i * 8.4F, 24.6F), getAmount(bonecaAmbalabu, 24.6F), 30.4F, 24.699999F, 12082186, 0.35F);
         handle(bonecaAmbalabu, getBonecaAmbalabuFloat(bonecaAmbalabu, i * 3.5F, 18.6F), getAmount(bonecaAmbalabu, 18.6F), 22.800001F, 11.400001F, 12082186, 0.35F);
      }

      for (int j = -1; j <= 1; j += 2) {
         float dinForeValue = j * 3.0F;
         bonecaAmbalabu.getClass022(getBonecaAmbalabuFloat(bonecaAmbalabu, dinForeValue, 29.2F), getAmount(bonecaAmbalabu, 29.2F), 25.175001F, 18.525F, ForeShinUtils.getValueForIntFloat(12082186, 0.2F));
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabuFloat(bonecaAmbalabu, dinForeValue - j * 1.45F, 27.7F),
            getAmount(bonecaAmbalabu, 27.7F),
            getBonecaAmbalabuFloat(bonecaAmbalabu, dinForeValue, 27.05F),
            getAmount(bonecaAmbalabu, 27.05F),
            getBonecaAmbalabuFloat(bonecaAmbalabu, dinForeValue + j * 1.55F, 27.85F),
            getAmount(bonecaAmbalabu, 27.85F),
            1.71F,
            ForeShinUtils.getValueForIntFloat(12082186, 0.35F)
         );
         bonecaAmbalabu.getClassForFloatInt(
            getBonecaAmbalabuFloat(bonecaAmbalabu, j * 1.5F, 26.0F),
            getAmount(bonecaAmbalabu, 26.0F),
            getBonecaAmbalabuFloat(bonecaAmbalabu, j * 2.8F, 24.4F),
            getAmount(bonecaAmbalabu, 24.4F),
            getBonecaAmbalabuFloat(bonecaAmbalabu, j * 2.65F, 22.9F),
            getAmount(bonecaAmbalabu, 22.9F),
            1.9F,
            ForeShinUtils.getValueForIntFloat(12082186, 0.3F)
         );
         bonecaAmbalabu.getClass02(
            getBonecaAmbalabuFloat(bonecaAmbalabu, j * 0.6F, 30.45F),
            getAmount(bonecaAmbalabu, 30.45F),
            getBonecaAmbalabuFloat(bonecaAmbalabu, j * 0.38F, 29.65F),
            getAmount(bonecaAmbalabu, 29.65F),
            1.52F,
            ForeShinUtils.getValueForIntFloat(12082186, 0.45F)
         );
      }

      handle(bonecaAmbalabu, getBonecaAmbalabuFloat(bonecaAmbalabu, 0.0F, 25.1F), getAmount(bonecaAmbalabu, 25.1F), 10.45F, 4.275F, 10111496, 0.2F);

      for (int k = -1; k <= 1; k += 2) {
         bonecaAmbalabu.getClass022(getBonecaAmbalabuFloat(bonecaAmbalabu, k * 0.45F, 25.35F), getAmount(bonecaAmbalabu, 25.35F), 2.66F, 1.52F, ForeShinUtils.getValueForIntFloat(5906950, 0.8F));
      }

      float[] floats = new float[16];
      float[] floats2 = new float[]{-2.3F, -1.25F, -0.42F, 0.0F, 0.42F, 1.3F, 2.4F, 0.0F};
      float[] floats3 = new float[]{-0.03F, 0.4F, 0.58F, 0.42F, 0.58F, 0.5F, 0.6F, 0.0F};

      for (int m = 0; m < 7; m++) {
         floats[2 * m] = getBonecaAmbalabuFloat(bonecaAmbalabu, floats2[m], 23.5F + floats3[m]);
         floats[2 * m + 1] = getAmount(bonecaAmbalabu, 23.5F + floats3[m]);
      }

      floats[14] = getBonecaAmbalabuFloat(bonecaAmbalabu, 0.0F, 23.48F);
      floats[15] = getAmount(bonecaAmbalabu, 23.48F);
      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(11554320), floats);
      bonecaAmbalabu.getClass022(getBonecaAmbalabuFloat(bonecaAmbalabu, 0.1F, 23.0F), getAmount(bonecaAmbalabu, 23.0F), 16.625F, 5.7000003F, ForeShinUtils.getValueForInt(12737044));
      bonecaAmbalabu.getClass022(getBonecaAmbalabuFloat(bonecaAmbalabu, 0.15F, 23.06F), getAmount(bonecaAmbalabu, 23.06F), 8.075F, 1.9F, ForeShinUtils.getValueForIntFloat(15242586, 0.65F));
      float[] floats4 = new float[]{-2.35F, -1.1F, 0.0F, 1.1F, 2.4F};
      float[] floats5 = new float[]{-0.08F, -0.06F, 0.0F, 0.12F, 0.65F};
      float[] classValues = new float[5];
      float[] floats7 = new float[5];

      for (int p = 0; p < 5; p++) {
         classValues[p] = getBonecaAmbalabuFloat(bonecaAmbalabu, floats4[p], 23.5F + floats5[p]);
         floats7[p] = getAmount(bonecaAmbalabu, 23.5F + floats5[p]);
      }

      bonecaAmbalabu.getClassForFloatArrayFloat(classValues, floats7, 3.04F, ForeShinUtils.getValueForInt(5906950));
      bonecaAmbalabu.getClass022(classValues[0], floats7[0], 2.09F, 2.09F, ForeShinUtils.getValueForInt(5906950));
      bonecaAmbalabu.getClass022(classValues[4], floats7[4], 2.09F, 2.09F, ForeShinUtils.getValueForInt(5906950));
      bonecaAmbalabu.getClassForFloatInt(
         getBonecaAmbalabuFloat(bonecaAmbalabu, -1.2F, 22.25F),
         getAmount(bonecaAmbalabu, 22.25F),
         getBonecaAmbalabuFloat(bonecaAmbalabu, 0.0F, 21.9F),
         getAmount(bonecaAmbalabu, 21.9F),
         getBonecaAmbalabuFloat(bonecaAmbalabu, 1.2F, 22.25F),
         getAmount(bonecaAmbalabu, 22.25F),
         2.09F,
         ForeShinUtils.getValueForIntFloat(12082186, 0.4F)
      );
      bonecaAmbalabu.getClass022(getBonecaAmbalabuFloat(bonecaAmbalabu, 0.0F, 21.1F), getAmount(bonecaAmbalabu, 21.1F), 15.2F, 7.6F, ForeShinUtils.getValueForIntFloat(16756813, 0.2F));
   }

   private static float getAmount3(float dinForeLimit, float dinForeValue) {
      return dinForeLimit > 0.0F ? 1.0F - 0.5F * dinForeValue : 0.5F * dinForeValue;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuFactor, float bonecaAmbalabuValue, int bonecaAmbalabuValue2) {
      Random random = new Random(bonecaAmbalabuValue2);
      int bonecaAmbalabuLimit = Math.round(bonecaAmbalabuFactor * bonecaAmbalabu.ambalabuBonecaValue * bonecaAmbalabu.bonecaValue / 100.0F);

      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float classValue = random.nextFloat() * bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue4 = random.nextFloat() * bonecaAmbalabu.bonecaValue;
         float classDivisor = 1.1F + 0.7F * random.nextFloat();
         bonecaAmbalabu.getClass022(classValue, bonecaAmbalabuValue4 + 0.55F * classDivisor, 1.1F * classDivisor, 0.75F * classDivisor, ForeShinUtils.getValueForIntFloat(16763274, 0.55F * bonecaAmbalabuValue));
         bonecaAmbalabu.getClass022(classValue, bonecaAmbalabuValue4 - 0.15F * classDivisor, classDivisor, 0.85F * classDivisor, ForeShinUtils.getValueForIntFloat(14249999, 0.85F * bonecaAmbalabuValue));
      }
   }

   private static void handleForLatheLoftFloatShinBrr(LatheLoft latheLoft, float dinForeValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float soleScaled = dinForeValue * 4.0F;
      ShinBrr max3 = latheLoft.getMax(16053492);
      ShinBrr max4 = latheLoft.getMax(13111342);
      byte dinForeLimit = 24;
      float[] floats = new float[2 * dinForeLimit];

      for (int i = 0; i < dinForeLimit; i++) {
         double angle = i * Math.PI * 2.0 / dinForeLimit;
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         float dinForeFactor = (float)Math.pow(Math.abs(cos), 0.8) * Math.signum(cos);
         float planValuesItem = (float)Math.pow(Math.abs(sin), 0.8) * Math.signum(sin);
         float dinForeLimit2 = -2.1F + 5.7F * planValuesItem;
         floats[2 * i] = soleScaled + 3.0F * (dinForeLimit2 > -2.1F ? 1.0F - 0.1F * Math.abs(planValuesItem) : 1.0F) * dinForeFactor;
         floats[2 * i + 1] = dinForeLimit2;
      }

      latheLoft.getDequeArrayArrayDeque().getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getPolygonExtrude(getAmounts(floats, -3.8F, false), -1.9F, 0.0F, max3, null, shinBrr2);
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, -3.8F).getLatheLoftLatheLoftAmountLoft(6.0F).getLatheLoft(0.0F, 0.0F, 3.8F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getPolygonExtrude(getAmounts(floats, -3.8F, true), -1.9F, 0.0F, max3, null, shinBrr2);
      latheLoft.getDequeArray();
      float[] floats2 = new float[]{3.2F, 2.5F, 0.9F, -1.2F, -3.4F, -5.3F, -6.7F, -7.4F, -7.75F};
      float[] floats3 = new float[]{2.35F, 2.4F, 2.4F, 2.3F, 2.2F, 2.15F, 2.1F, 2.05F, 2.05F};
      float[] floats4 = new float[]{2.1F, 2.25F, 2.3F, 2.2F, 1.95F, 1.6F, 1.25F, 0.8F, 0.08F};
      float[] floats5 = new float[]{0.93F, 0.96F, 0.97F, 0.97F, 0.97F, 0.97F, 0.96F, 0.85F, 0.15F};
      float[] loftPathValues = new float[3 * floats2.length];
      float[] crownForeValues = new float[floats2.length];

      for (int j = 0; j < floats2.length; j++) {
         loftPathValues[3 * j] = soleScaled;
         loftPathValues[3 * j + 1] = floats3[j] + getAmount3(floats2[j]);
         loftPathValues[3 * j + 2] = floats2[j];
         crownForeValues[j] = getAmount4(floats2[j]) * floats5[j];
      }

      latheLoft.getLoftPath(loftPathValues, crownForeValues, floats4, 10, shinBrr, max4, null, 0.0F, 1.0F, 0.0F);
   }

   private static float getAmount4(float dinForeValue, float dinForeValue2) {
      if (dinForeValue2 < 0.4F) {
         return -1.0F;
      } else {
         float squaredDistanceTo = (dinForeValue2 - 0.4F) / 0.6F;
         float dinForeValue4 = 0.215F * (float)Math.sqrt(squaredDistanceTo);
         float minAbs = Math.min(Math.abs(dinForeValue - 0.25F), Math.abs(dinForeValue - 0.75F));
         return dinForeValue4 - minAbs;
      }
   }

   private static float getAmount5(float dinForeValue, float dinForeValue2) {
      float cos = (float)Math.cos(0.07F);
      return getAmountForFloat(dinForeValue, dinForeValue2) * cos + 14.4F * (1.0F - cos);
   }

   private static float getAmount(float dinForeLimit, float dinForeLimit2, float dinForeDivisor) {
      return !(dinForeLimit < dinForeLimit2) && !(dinForeLimit > dinForeLimit2 + dinForeDivisor) ? (float)Math.sin((dinForeLimit - dinForeLimit2) / dinForeDivisor * Math.PI) : 0.0F;
   }

   private static void handleBonecaAmbalabu8(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14646308)).getBonecaAmbalabu(0.04F, 4.0F, 61);
      handle(bonecaAmbalabu, 0.5F * ambalabuBonecaValue, 0.4F * bonecaValue, 0.16F * ambalabuBonecaValue, 0.4F * bonecaValue, 16754253, 0.5F);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.2F * ambalabuBonecaValue, 0.0F, 0.3F * ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(10768911, 0.6F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.7F * ambalabuBonecaValue, 0.0F, 0.8F * ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(10768911, 0.6F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.82F * bonecaValue, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(10768911, 0.35F));
   }

   private static void handle(LatheLoft latheLoft, float dinForeValue, ShinBrr shinBrr) {
      float latheLoftValue = dinForeValue * 4.0F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, 0.0F, 0.6F);
      latheLoft.getLatheProfile(new float[]{2.45F, 2.4F, 2.3F}, new float[]{3.7F, 5.8F, 7.2F}, 10, 1.0F, 1.1F, shinBrr, null, null);
      latheLoft.getLatheLoft(0.0F, 7.2F, 0.0F).getLatheLoftForFloat(1.0F, 1.0F, 1.1F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 2.1F, 0.45F, 6, 4, latheLoft.getMax(1118481));
      latheLoft.getDequeArray();
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(latheLoftValue, 6.0F, -2.2F).getLatheLoftLatheLoftAmountLoft(-20.0F);
      latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.15F, 1.75F, 0.36F, 6, latheLoft.getMax(16777215));
      latheLoft.getLoftLathe(0.0F, 0.85F, -0.33F, 0.5F, 0.3F, 8, latheLoft.getMax(13111342));
      latheLoft.getDequeArray();
   }

   private static float getAmount3(float dinForeValue) {
      return dinForeValue < -3.8F ? (-3.8F - dinForeValue) * (float)Math.tan(Math.toRadians(6.0)) : 0.0F;
   }

   private static void handleBonecaAmbalabu9(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14646308)).getBonecaAmbalabu(0.04F, 10.0F, 31);
      handle(bonecaAmbalabu, 0.5F * ambalabuBonecaValue, 0.5F * bonecaValue, 0.17F * ambalabuBonecaValue, 0.32F * bonecaValue, 16754253, 0.55F);
      bonecaAmbalabu.getClassForFloatInt(0.22F * ambalabuBonecaValue, 0.04F * bonecaValue, 0.5F * ambalabuBonecaValue, 0.5F * bonecaValue, 0.78F * ambalabuBonecaValue, 0.04F * bonecaValue, 3.2F, ForeShinUtils.getValueForIntFloat(10768911, 0.7F));
      bonecaAmbalabu.getClassForFloatInt(0.3F * ambalabuBonecaValue, 0.25F * bonecaValue, 0.5F * ambalabuBonecaValue, 0.12F * bonecaValue, 0.7F * ambalabuBonecaValue, 0.25F * bonecaValue, 2.2F, ForeShinUtils.getValueForIntFloat(10768911, 0.45F));
      bonecaAmbalabu.getClassForFloatInt(0.33F * ambalabuBonecaValue, 0.88F * bonecaValue, 0.5F * ambalabuBonecaValue, 1.02F * bonecaValue, 0.67F * ambalabuBonecaValue, 0.88F * bonecaValue, 3.2F, ForeShinUtils.getValueForIntFloat(10768911, 0.7F));
      bonecaAmbalabu.getClassForFloatInt(0.3F * ambalabuBonecaValue, 0.3F * bonecaValue, 0.25F * ambalabuBonecaValue, 0.62F * bonecaValue, 0.31F * ambalabuBonecaValue, 0.94F * bonecaValue, 3.2F, ForeShinUtils.getValueForIntFloat(10768911, 0.7F));
      bonecaAmbalabu.getClassForFloatInt(0.7F * ambalabuBonecaValue, 0.3F * bonecaValue, 0.75F * ambalabuBonecaValue, 0.62F * bonecaValue, 0.69F * ambalabuBonecaValue, 0.94F * bonecaValue, 3.2F, ForeShinUtils.getValueForIntFloat(10768911, 0.7F));
      handle(bonecaAmbalabu, 0.0F, 0.55F * bonecaValue, 0.12F * ambalabuBonecaValue, 0.32F * bonecaValue, 16754253, 0.45F);
      handle(bonecaAmbalabu, ambalabuBonecaValue, 0.55F * bonecaValue, 0.12F * ambalabuBonecaValue, 0.32F * bonecaValue, 16754253, 0.45F);
      bonecaAmbalabu.getClass02(0.06F * ambalabuBonecaValue, 0.3F * bonecaValue, 0.08F * ambalabuBonecaValue, 0.8F * bonecaValue, 1.6F, ForeShinUtils.getValueForIntFloat(10768911, 0.4F));
      bonecaAmbalabu.getClass02(0.94F * ambalabuBonecaValue, 0.3F * bonecaValue, 0.92F * ambalabuBonecaValue, 0.8F * bonecaValue, 1.6F, ForeShinUtils.getValueForIntFloat(10768911, 0.4F));
      bonecaAmbalabu.getClassForFloatArrayFloat(
         new float[]{0.56F * ambalabuBonecaValue, 0.53F * ambalabuBonecaValue, 0.57F * ambalabuBonecaValue, 0.55F * ambalabuBonecaValue},
         new float[]{0.24F * bonecaValue, 0.45F * bonecaValue, 0.66F * bonecaValue, 0.85F * bonecaValue},
         1.3F,
         ForeShinUtils.getValueForIntFloat(16229462, 0.8F)
      );
   }

   private static float getAmount4(float dinForeLimit) {
      float base = Math.min(1.0F, Math.abs(dinForeLimit - -2.1F) / 5.7F);
      float dinForeValue = dinForeLimit > -2.1F ? 1.0F - 0.1F * base : 1.0F;
      return 3.0F * dinForeValue * (float)Math.pow(Math.max(0.0, 1.0 - Math.pow(base, 2.5)), 0.4);
   }

   private void handle(LatheLoft latheLoft, int dinForeFactor) {
      float[] floats = new float[]{0.9F, 1.75F, 2.85F, 3.95F, 4.75F};
      float[] floats2 = new float[]{29.75F, 30.5F, 31.25F, 31.4F, 31.1F};
      float[] loftPathValues = new float[15];

      for (int i = 0; i < 5; i++) {
         float dinForeValue = dinForeFactor * floats[i];
         loftPathValues[3 * i] = dinForeValue;
         loftPathValues[3 * i + 1] = floats2[i];
         loftPathValues[3 * i + 2] = getAmount6(dinForeValue, floats2[i]) - 0.12F;
      }

      ShinBrr shinBrr = latheLoft.getMax(3811866);
      latheLoft.getLoftPath(loftPathValues, new float[]{0.35F, 0.66F, 0.72F, 0.56F, 0.12F}, new float[]{0.25F, 0.4F, 0.4F, 0.32F, 0.1F}, 6, shinBrr, null, null, 0.0F, 0.25F, -1.0F);
   }

   private static void handleBonecaAmbalabu10(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16053492)).getBonecaAmbalabu(0.02F, 6.0F, 41);
      int bonecaAmbalabuValue = ForeShinUtils.getValueForInt(13111342);
      int valueForInt = ForeShinUtils.getValueForInt(1118481);

      for (int i = -1; i <= 1; i += 2) {
         float bonecaAmbalabuValue3 = i;
         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue3, bonecaAmbalabuValue, 0.0F, 0.0F, 0.2F, 0.0F, 0.24F, 0.4F, 0.2F, 1.0F, 0.0F, 1.0F);
         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue3, bonecaAmbalabuValue, 0.12F, 0.7F, 0.46F, 0.74F, 0.42F, 1.0F, 0.12F, 1.0F);
         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue3, bonecaAmbalabuValue, 0.5F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.9F, 1.0F, 0.84F, 0.55F, 0.7F, 0.42F, 0.55F, 0.36F, 0.5F, 0.3F);
         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue3, bonecaAmbalabuValue, 0.38F, 0.86F, 0.84F, 0.86F, 0.84F, 1.0F, 0.38F, 1.0F);
         handleBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue3, valueForInt, 0.0F, 0.93F, 0.36F, 0.93F, 0.36F, 1.0F, 0.0F, 1.0F);
         handleBonecaAmbalabu(
            bonecaAmbalabu,
            bonecaAmbalabuValue3,
            valueForInt,
            0.72F,
            0.54F,
            0.66F,
            0.36F,
            0.56F,
            0.22F,
            0.42F,
            0.2F,
            0.28F,
            0.34F,
            0.17F,
            0.58F,
            0.1F,
            0.82F,
            0.21F,
            0.64F,
            0.33F,
            0.48F,
            0.46F,
            0.41F,
            0.59F,
            0.44F
         );
      }

      float classBound = 0.455F * ambalabuBonecaValue;
      float hashHuntBound = 0.545F * ambalabuBonecaValue;
      bonecaAmbalabu.getClassBonecaAmbalabu(classBound - 2.0F, 0.36F * bonecaValue, hashHuntBound + 2.0F, 0.84F * bonecaValue, ForeShinUtils.getValueForInt(16053492));

      for (int j = 0; j < 6; j++) {
         float amountForFloat = (0.4F + j * 0.075F) * bonecaValue;
         bonecaAmbalabu.getClass02(classBound, amountForFloat, hashHuntBound, amountForFloat + 0.05F * bonecaValue, 1.6F, valueForInt);
         bonecaAmbalabu.getClass02(hashHuntBound, amountForFloat, classBound, amountForFloat + 0.05F * bonecaValue, 1.6F, valueForInt);
      }

      bonecaAmbalabu.getBonecaAmbalabuForIntFloat(ForeShinUtils.getValueForIntFloat(12303291, 0.7F), 0.5F, 0.5F, 43);
   }

   private static float getAmount2(float dinForeValue, float dinForeValue2, float dinForeFactor) {
      return dinForeValue + (dinForeValue2 - dinForeValue) * dinForeFactor;
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float dinForeValue, float amountForFloat, float dinForeFactor, float dinForeFactor2, int foreShinValue, float dinForeValue4) {
      byte dinForeDivisor = 6;
      float bonecaAmbalabuLimit = 1.0F - (float)Math.pow(1.0F - dinForeValue4, 1.0 / dinForeDivisor);

      for (int i = 0; i < dinForeDivisor; i++) {
         float dinForeFactor3 = 1.0F - (float)i / dinForeDivisor;
         bonecaAmbalabu.getClass022(dinForeValue, amountForFloat, dinForeFactor * dinForeFactor3, dinForeFactor2 * dinForeFactor3, ForeShinUtils.getValueForIntFloat(foreShinValue, bonecaAmbalabuLimit));
      }
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressValue = getAmount(poseProgress.limbSwingStrength * 2.5F);
      float shinProgressFactor = !poseProgress.sneakingPose && !poseProgress.vehicle && !poseProgress.gliding && !poseProgress.swimming ? 1.0F - shinProgressValue : 0.0F;
      float angle = poseProgress.animationProgress * 0.6662F;
      float animationProgress = poseProgress.getAnimationProgressForFloat(0.0F);
      float shinProgressValue2 = -((float)Math.sin(angle)) * 0.07F * poseProgress.limbSwingStrength;
      float dinForeLimit = (poseProgress.age + (poseProgress.hashCode & 63)) % 48.0F;
      float shinProgressValue4 = (getAmount(dinForeLimit, 16.0F, 5.0F) + getAmount(dinForeLimit, 22.0F, 5.0F) + getAmount(dinForeLimit, 28.0F, 5.0F)) * shinProgressFactor;
      float shinProgressFactor2 = getAmount(dinForeLimit, 35.0F, 9.0F) * shinProgressFactor;
      float shinProgressValue5 = getAmount(dinForeLimit, 29.5F, 5.5F) * shinProgressFactor;
      float shinProgressValue6 = (poseProgress.age + (poseProgress.hashCode & 0xFF)) % 240.0F;
      float shinProgressFactor3 = shinProgressValue6 < 150.0F
         ? 0.0F
         : (shinProgressValue6 < 164.0F ? getAmount((shinProgressValue6 - 150.0F) / 14.0F) : (shinProgressValue6 < 226.0F ? 1.0F : 1.0F - getAmount((shinProgressValue6 - 226.0F) / 14.0F)));
      if (poseProgress.sneakingPose) {
         shinProgressFactor3 = 1.0F;
      }

      float shinProgressFactor4 = poseProgress.sneakingPose ? 1.0F : 1.0F - shinProgressValue;
      float shinProgressValue7 = shinProgressFactor3 * shinProgressFactor4;
      float shinProgressTotal = animationProgress * 0.55F + 0.45F * shinProgressValue5 + 0.08F * shinProgressValue7;
      float shinProgressTotal2 = -animationProgress * 0.55F + 0.08F * shinProgressValue7;
      float shinProgressCounter = -poseProgress.getAnimationProgress((float) Math.PI) * 0.9F - 0.95F * shinProgressValue5 - 0.16F * shinProgressValue7;
      float foreShinValue = -poseProgress.getAnimationProgress(0.0F) * 0.9F - 0.16F * shinProgressValue7;
      foreShinScale.getAmounts(this.armL, -0.1F * shinProgressFactor2 * shinProgressFactor3, -0.08F * animationProgress, 1.4F * shinProgressValue2);
      foreShinScale.getAmountsForeShin2(this.armL, 0.0F, 0.45F * shinProgressValue4 - 0.55F * shinProgressFactor2, 0.0F);
      float shinProgressValue8 = 0.008F * poseProgress.getAgeCode(80.0F, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(this.armL, 1.0F + shinProgressValue8 + 0.03F * shinProgressFactor2, 1.0F + shinProgressValue8 - 0.045F * shinProgressFactor2, 1.0F + shinProgressValue8 + 0.03F * shinProgressFactor2);
      foreShinScale.getAmountsBoneCount(this.armL, getAmount(poseProgress.shinProgressAmount * 0.35F, 0.4F), getAmount(poseProgress.headYawRadians * 0.5F, 0.7F), 0.0F);
      float shinProgressValue9 = 0.1F * shinProgressValue4 + 0.15F * shinProgressFactor2;
      float shinProgressFactor5 = (0.75F + 0.1F * shinProgressFactor2) * shinProgressFactor3;
      float shinProgressFactor6 = -1.45F * (1.0F - shinProgressFactor3);
      float shinProgressFactor7 = 1.72F * (1.0F - shinProgressFactor3) + 0.25F * shinProgressFactor3;
      float shinProgressFactor8 = (1.7F + shinProgressValue9) * (1.0F - shinProgressFactor3) + (1.6F + 0.1F * shinProgressFactor2) * shinProgressFactor3;
      float shinProgressFactor9 = 1.2F * shinProgressFactor3;
      float shinProgressValue10 = 0.5F * animationProgress * (1.0F - shinProgressFactor4);
      float shinProgressValue11 = shinProgressFactor5 * shinProgressFactor4 - shinProgressValue10;
      float shinProgressValue12 = shinProgressFactor6 * shinProgressFactor4;
      float shinProgressValue13 = shinProgressFactor7 * shinProgressFactor4 + 0.5F * (1.0F - shinProgressFactor4);
      float shinProgressValue14 = shinProgressFactor8 * shinProgressFactor4 + 0.4F * (1.0F - shinProgressFactor4);
      float shinProgressValue15 = shinProgressFactor9 * shinProgressFactor4;
      float shinProgressValue16 = shinProgressFactor5 * shinProgressFactor4 + shinProgressValue10;
      float dinForeValue = -shinProgressValue12;
      float shinProgressValue18 = -shinProgressValue13;
      float shinProgressTotal3 = shinProgressValue14;
      float shinProgressValue19 = -shinProgressValue15;
      float shinProgressValue20 = 0.0F;
      if (poseProgress.usingItem && poseProgress.swingProgress <= 0.0F) {
         shinProgressValue11 = 0.95F;
         shinProgressValue12 = 0.2F;
         shinProgressValue13 = 0.25F;
         shinProgressValue14 = 0.7F;
         shinProgressValue15 = 0.0F;
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float dinForeFactor = swingProgress < 0.12F ? getAmount(swingProgress / 0.12F) : 1.0F - getAmount((swingProgress - 0.62F) / 0.38F);
         float shinProgressValue21 = getAmount((swingProgress - 0.12F) / 0.2F);
         float shinProgressFactor11 = getAmount(swingProgress, 0.22F, 0.3F);
         shinProgressValue11 = getAmount2(shinProgressValue11, 3.0F - 2.05F * shinProgressValue21, dinForeFactor);
         shinProgressValue12 = getAmount2(shinProgressValue12, 0.1F, dinForeFactor);
         shinProgressValue13 = getAmount2(shinProgressValue13, 0.35F - 0.2F * shinProgressValue21, dinForeFactor);
         shinProgressValue14 = getAmount2(shinProgressValue14, 0.9F - 0.45F * shinProgressValue21, dinForeFactor);
         shinProgressValue15 = getAmount2(shinProgressValue15, 0.0F, dinForeFactor);
         shinProgressTotal3 += 0.3F * shinProgressFactor11 * dinForeFactor;
         foreShinScale.getAmountsBoneCount(this.armL, (0.08F * (1.0F - shinProgressValue21) - 0.14F * shinProgressValue21) * dinForeFactor, 0.1F * shinProgressValue21 * dinForeFactor, 0.0F);
         foreShinScale.getAmountsForeShin2(this.armL, 0.0F, -0.5F * shinProgressFactor11, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.0F + 0.06F * shinProgressFactor11, 1.0F - 0.08F * shinProgressFactor11, 1.0F + 0.06F * shinProgressFactor11);
         foreShinScale.getAmountsForeShin2(this.classShinProgress, 0.0F, -0.3F * shinProgressFactor11, 0.0F);
         shinProgressValue20 = 0.4F * shinProgressFactor11;
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         shinProgressValue11 = -0.3F;
         shinProgressValue12 = 0.0F;
         shinProgressValue13 = 1.3F;
         shinProgressValue14 = 0.3F;
         shinProgressValue15 = 0.0F;
         shinProgressValue16 = -0.3F;
         dinForeValue = 0.0F;
         shinProgressValue18 = -1.3F;
         shinProgressTotal3 = 0.3F;
         shinProgressValue19 = 0.0F;
      }

      foreShinScale.getAmounts(this.foreR, shinProgressValue11, shinProgressValue12, shinProgressValue13);
      foreShinScale.getAmounts(this.foreRightSection, shinProgressValue14, shinProgressValue15, 0.0F);
      foreShinScale.getAmounts(this.foreL, shinProgressValue16, dinForeValue, shinProgressValue18);
      foreShinScale.getAmounts(this.shinProgressForeEar, shinProgressTotal3, shinProgressValue19, 0.0F);
      foreShinScale.getAmountsForeShin2(this.classShinProgress, 0.0F, -0.25F * shinProgressFactor2, 0.0F);
      foreShinScale.getAmounts(this.foreEar, 0.0F, 0.0F, 0.05F * poseProgress.getAgeCode(45.0F, 0.0F) + 0.22F * shinProgressFactor2 - 0.1F * shinProgressValue5 + shinProgressValue20);
      float shinProgressLimit = (poseProgress.age + (poseProgress.hashCode & 127) * 3.0F) % 110.0F;
      if (shinProgressLimit > 100.0F && shinProgressLimit < 104.0F && !poseProgress.poseProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F, 0.15F, 1.0F);
      }

      if (poseProgress.sneakingPose) {
         shinProgressTotal += 0.7F;
         shinProgressTotal2 += 0.7F;
         shinProgressCounter--;
         foreShinValue--;
         foreShinScale.getAmountsBoneCount(this.armL, -0.22F, 0.0F, 0.0F);
      }

      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.shinR, 1.4F, -0.22F, 0.05F);
         foreShinScale.getAmounts(this.shinL, 1.4F, 0.22F, -0.05F);
         foreShinScale.getAmounts(this.footR, -0.6F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.6F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.dinForeForeEar, -0.4F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinFootForeEar, -0.4F, 0.0F, 0.0F);
      } else {
         foreShinScale.getAmounts(this.shinR, shinProgressTotal, -0.18F, 0.07F);
         foreShinScale.getAmounts(this.shinL, shinProgressTotal2, 0.18F, -0.07F);
         foreShinScale.getAmounts(this.footR, shinProgressCounter, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, foreShinValue, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.dinForeForeEar, -(shinProgressTotal + shinProgressCounter), 0.0F, -0.07F);
         foreShinScale.getAmounts(this.shinFootForeEar, -(shinProgressTotal2 + foreShinValue), 0.0F, 0.07F);
         foreShinScale.getAmountsForeShin2(this.body, 0.0F, -Math.min(getAmount5(shinProgressTotal, shinProgressCounter), getAmount5(shinProgressTotal2, foreShinValue)), 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.08F, 0.92F, 1.08F);
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag) {
      if (bonecaAmbalabuFlag) {
         bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15040534));
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.22F, ForeShinUtils.getValueForInt(11886096));
      } else {
         bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15962666), ForeShinUtils.getValueForInt(15632926));
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.84F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(3807750));
      }
   }

   private static float getBonecaAmbalabuForBonecaAmbalabuFloat(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2) {
      float angle = bonecaAmbalabuValue2 / bonecaAmbalabu.bonecaValue * (float) Math.PI * 0.5F;
      float angle2 = (float) (Math.PI * 2) * (bonecaAmbalabuValue / bonecaAmbalabu.ambalabuBonecaValue - 0.5F);
      return -((float)Math.sin(angle)) * (float)Math.cos(angle2) * 1.15F;
   }

   private void handleForLatheLoftShinBrr(LatheLoft latheLoft, ShinBrr shinBrr) {
      latheLoft.getLatheProfile(new float[]{0.0F, 2.4F, 4.8F, 5.6F, 5.3F}, new float[]{15.5F, 15.8F, 16.9F, 18.6F, 20.4F}, 12, 1.0F, 0.92F, shinBrr, null, null);
   }

   @Override
   public int getValue() {
      return 4;
   }

   private static void handleBonecaAmbalabu11(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15631641)).getBonecaAmbalabu(0.03F, 4.0F, 55);
      bonecaAmbalabu.getClass022(0.5F * ambalabuBonecaValue, 0.3F * bonecaValue, 0.1F * ambalabuBonecaValue, 0.14F * bonecaValue, ForeShinUtils.getValueForIntFloat(16758630, 0.6F));

      for (int i = -1; i <= 1; i += 2) {
         bonecaAmbalabu.getClass022((0.5F + i * 0.085F) * ambalabuBonecaValue, 0.78F * bonecaValue, 0.055F * ambalabuBonecaValue, 0.075F * bonecaValue, ForeShinUtils.getValueForIntFloat(5906950, 0.9F));
      }
   }

   private static float getBonecaAmbalabuFloat(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float dinForeValue) {
      float max3 = Math.max(-1.0F, Math.min(1.0F, -bonecaAmbalabuValue / getAmount2(dinForeValue)));
      return bonecaAmbalabu.ambalabuBonecaValue * (0.5F + (float)Math.asin(max3) / (float) (Math.PI * 2));
   }

   private static void handleBonecaAmbalabu12(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15766586), ForeShinUtils.getValueForInt(14646308)).getBonecaAmbalabu(0.04F, 8.0F, 53);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, 0.35F * bonecaValue, ForeShinUtils.getValueForIntFloat(16754253, 0.35F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, ambalabuBonecaValue, 0.15F * bonecaValue, ForeShinUtils.getValueForIntFloat(16761466, 0.35F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.72F * bonecaValue, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(10768911, 0.25F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.86F * bonecaValue, ambalabuBonecaValue, bonecaValue, ForeShinUtils.getValueForIntFloat(10768911, 0.3F));
   }

   private static void handle3(LatheLoft latheLoft, float dinForeValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float skinScaled = dinForeValue * 3.5F;
      float[] floats = new float[]{
         skinScaled,
         20.599998F,
         0.0F,
         skinScaled + dinForeValue * 0.1F,
         16.599998F,
         -0.35F,
         skinScaled + dinForeValue * 0.25F,
         13.799999F,
         -0.45F,
         skinScaled + dinForeValue * 0.15F,
         12.0F,
         -0.3F,
         skinScaled + dinForeValue * 0.12F,
         10.200001F,
         -0.15F
      };
      latheLoft.getLoftPath(floats, new float[]{2.8F, 3.7F, 3.5F, 2.6F, 2.3F}, new float[]{2.8F, 3.6F, 3.4F, 2.5F, 2.3F}, 12, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(dinForeValue * 2.45F, 12.3F, -0.85F).getLatheLoft4(dinForeValue * 35.0F).getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 1.0F, 1.15F, 1.4F, 8, 0.0F, 0.5F, shinBrr2);
      latheLoft.getDequeArray();
   }

   private void handle3(LatheLoft latheLoft, ShinBrr shinBrr) {
      ShinBrr max2 = latheLoft.getMax(15631641);
      float[] floats = new float[]{
         0.0F, 29.3F, getAmount6(0.0F, 29.3F) + 0.2F, 0.0F, 28.0F, getAmount6(0.0F, 28.0F) - 0.35F, 0.0F, 26.6F, getAmount6(0.0F, 26.6F) - 0.8F
      };
      latheLoft.getLoftPath(floats, new float[]{0.5F, 0.66F, 0.8F}, new float[]{0.45F, 0.6F, 0.75F}, 8, max2, null, null, 0.0F, 0.0F, -1.0F);
      float dinForeValue = getAmount6(0.0F, 26.1F) - 0.95F;
      latheLoft.getLatheLoft(0.0F, 26.1F, dinForeValue, 0.85F, 0.7F, 0.6F, 8, shinBrr);

      for (int i = -1; i <= 1; i += 2) {
         float latheLoftValue = i * 0.8F;
         latheLoft.getLatheLoft(latheLoftValue, 25.85F, getAmount6(latheLoftValue, 25.85F) - 0.15F, 0.5F, 0.42F, 0.45F, 6, max2);
      }
   }

   private void handle(LatheLoft latheLoft) {
      float[] floats = new float[20];

      for (int i = 0; i < 10; i++) {
         float angle = (float)(i * Math.PI / 5.0);
         float dinForeFactor = i % 2 == 0 ? 1.5F : 0.6F;
         floats[2 * i] = dinForeFactor * (float)Math.sin(angle);
         floats[2 * i + 1] = dinForeFactor * (float)Math.cos(angle);
      }

      ShinBrr shinBrr = latheLoft.getMax(4939298);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 34.75F, 0.0F).getLatheLoftLatheLoftAmountLoft(90.0F);
      latheLoft.getPolygonExtrude(floats, -0.12F, 0.0F, shinBrr, null, null);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu13(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16448250), ForeShinUtils.getValueForInt(15132390));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.7F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(13111342));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.64F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.7F, ForeShinUtils.getValueForInt(12434877));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.9F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(10226722));
   }

   private static float getAmount6(float dinForeValue, float dinForeValue2) {
      float dinForeFactor = dinForeValue / 8.8F;
      float dinForeFactor2 = (dinForeValue2 - 26.6F) / 8.3F;
      return -8.8F * (float)Math.sqrt(Math.max(0.0F, 1.0F - dinForeFactor * dinForeFactor - dinForeFactor2 * dinForeFactor2));
   }

   private static float getAmount(BonecaAmbalabu bonecaAmbalabu, float dinForeValue) {
      float bonecaAmbalabuFactor = Math.max(-1.0F, Math.min(1.0F, (dinForeValue - 26.6F) / 8.3F));
      return bonecaAmbalabu.bonecaValue * (float)(Math.acos(bonecaAmbalabuFactor) / Math.PI);
   }

   private static void handleBonecaAmbalabu14(BonecaAmbalabu bonecaAmbalabu) {
      int ambalabuBonecaValue = bonecaAmbalabu.ambalabuBonecaValue;
      int bonecaValue = bonecaAmbalabu.bonecaValue;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14646308)).getBonecaAmbalabu(0.04F, 10.0F, 37);
      bonecaAmbalabu.getClass02(0.5F * ambalabuBonecaValue, 0.1F * bonecaValue, 0.5F * ambalabuBonecaValue, 0.85F * bonecaValue, 2.0F, ForeShinUtils.getValueForIntFloat(16754253, 0.4F));
      bonecaAmbalabu.getClass022(0.12F * ambalabuBonecaValue, 0.36F * bonecaValue, 0.08F * ambalabuBonecaValue, 0.22F * bonecaValue, ForeShinUtils.getValueForIntFloat(16754253, 0.4F));
      bonecaAmbalabu.getClass022(0.88F * ambalabuBonecaValue, 0.36F * bonecaValue, 0.08F * ambalabuBonecaValue, 0.22F * bonecaValue, ForeShinUtils.getValueForIntFloat(16754253, 0.4F));
      bonecaAmbalabu.getClassForFloatInt(0.22F * ambalabuBonecaValue, 0.15F * bonecaValue, 0.2F * ambalabuBonecaValue, 0.5F * bonecaValue, 0.04F * ambalabuBonecaValue, 0.66F * bonecaValue, 1.8F, ForeShinUtils.getValueForIntFloat(10768911, 0.35F));
      bonecaAmbalabu.getClassForFloatInt(0.78F * ambalabuBonecaValue, 0.15F * bonecaValue, 0.8F * ambalabuBonecaValue, 0.5F * bonecaValue, 0.96F * ambalabuBonecaValue, 0.66F * bonecaValue, 1.8F, ForeShinUtils.getValueForIntFloat(10768911, 0.35F));
      bonecaAmbalabu.getClassForFloatInt(0.17F * ambalabuBonecaValue, 0.18F * bonecaValue, 0.15F * ambalabuBonecaValue, 0.45F * bonecaValue, 0.0F, 0.57F * bonecaValue, 2.6F, ForeShinUtils.getValueForIntFloat(10768911, 0.6F));
      bonecaAmbalabu.getClassForFloatInt(0.83F * ambalabuBonecaValue, 0.18F * bonecaValue, 0.85F * ambalabuBonecaValue, 0.45F * bonecaValue, ambalabuBonecaValue, 0.57F * bonecaValue, 2.6F, ForeShinUtils.getValueForIntFloat(10768911, 0.6F));
      bonecaAmbalabu.getClass02(0.0F, 0.08F * bonecaValue, 0.0F, 0.5F * bonecaValue, 2.4F, ForeShinUtils.getValueForIntFloat(10768911, 0.55F));
      bonecaAmbalabu.getClass02((float)ambalabuBonecaValue, 0.08F * bonecaValue, (float)ambalabuBonecaValue, 0.5F * bonecaValue, 2.4F, ForeShinUtils.getValueForIntFloat(10768911, 0.55F));
      handle(bonecaAmbalabu, 0.07F * ambalabuBonecaValue, 0.3F * bonecaValue, 0.07F * ambalabuBonecaValue, 0.18F * bonecaValue, 16754253, 0.45F);
      handle(bonecaAmbalabu, 0.93F * ambalabuBonecaValue, 0.3F * bonecaValue, 0.07F * ambalabuBonecaValue, 0.18F * bonecaValue, 16754253, 0.45F);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, int classValue, float... bonecaAmbalabuValues) {
      float[] floats = new float[bonecaAmbalabuValues.length];

      for (int i = 0; i + 1 < bonecaAmbalabuValues.length; i += 2) {
         floats[i] = getAmount3(bonecaAmbalabuValue, 0.4F + 0.6F * bonecaAmbalabuValues[i + 1]) * bonecaAmbalabu.ambalabuBonecaValue;
         floats[i + 1] = bonecaAmbalabuValues[i] * bonecaAmbalabu.bonecaValue;
      }

      bonecaAmbalabu.getClassForIntFloatArray(classValue, floats);
   }

   private static void handleForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14646308)).getBonecaAmbalabu(0.04F, 8.0F, 51);
   }
}
