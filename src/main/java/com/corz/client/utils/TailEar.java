package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.LoftLatheFlag;

public final class TailEar extends WarmBrainrotBase {
   private static final float[] shinFootTable = new float[]{12.0F, 12.3F, 12.85F, 13.6F, 14.5F, 15.5F, 16.5F, 17.4F, 18.2F, 18.8F, 19.2F, 19.4F};
   private static final float[] tailEarTable = new float[]{0.01F, 2.1F, 3.15F, 3.75F, 3.95F, 3.85F, 3.6F, 3.3F, 2.95F, 2.5F, 1.6F, 0.01F};
   private static final float[] table = new float[]{-10.5F, -8.5F, -6.0F, -3.0F, 0.0F, 3.0F, 6.0F, 9.0F, 11.5F, 13.0F, 13.8F};
   private static final float[] tailFinAmounts = new float[]{15.4F, 15.1F, 14.75F, 15.05F, 15.15F, 15.1F, 15.0F, 15.1F, 15.15F, 15.2F, 15.2F};
   private static final float[] finTable = new float[]{2.7F, 3.5F, 4.45F, 4.6F, 4.35F, 3.75F, 2.7F, 1.7F, 1.05F, 0.95F, 1.1F};
   private ForeEar head;
   private ForeEar classShinFoot;
   private static final float[] TAIL_RADII = new float[]{2.5F, 3.0F, 3.35F, 3.1F, 2.7F, 2.42F, 1.85F, 1.15F, 0.72F, 0.55F, 0.45F};
   private static final float[] TAIL_SAMPLES = getAmounts();
   private static final float[] shinProgressTable = new float[]{2.05F, -2.05F, 1.1F, -1.1F};
   private ForeEar classShinProgress;
   private static final float[] LEG_SWAY_OFFSETS = new float[]{0.09F, -0.09F, 0.0F, 0.0F};
   private static final float[] LEG_OFFSETS_Z = new float[]{-4.3F, -4.3F, 4.2F, 4.2F};
   private static final float[] THIGH_HEIGHTS = new float[]{11.2F, 11.2F, 12.3F, 12.3F};
   private static final float[] HIP_HEIGHTS = new float[]{13.5F, 13.5F, 14.7F, 14.7F};
   private ForeEar shinProgressForeEar;
   private ForeEar trunk;
   private static final float[] THIGH_WIDTHS = new float[]{0.85F, 0.85F, 0.85F, 0.85F};
   private ForeEar tailFin;
   private final ForeEar[] shinProgressForeEars;
   private ForeEar earL;
   private ForeEar shinFootForeEar;
   private ForeEar tailEarForeEar;
   private static final float[] THIGH_RADII = new float[]{1.45F, 1.45F, 1.25F, 1.25F};
   private static final float[] SHIN_HEIGHTS = new float[]{5.8F, 5.8F, 6.3F, 6.3F};
   private ForeEar foreEar;
   private static final float[] LEG_PHASES = new float[]{(float) (Math.PI / 2), (float) (Math.PI * 3.0 / 2.0), (float) Math.PI, 0.0F};
   private static final float[] WING_ANCHOR_POINTS = new float[]{4.2F, 19.0F, -3.8F, 18.2F};
   private static final float[] WING_CURVE_POINTS = new float[]{
      3.95F, 20.7F, 3.3F, 22.0F, 2.4F, 22.7F, 1.3F, 22.45F, 0.15F, 21.85F, -1.0F, 21.15F, -2.1F, 20.4F, -3.1F, 19.65F, -3.75F, 19.0F
   };
   private static final float[] TAIL_ANCHOR_POINTS = new float[]{-13.0F, 16.25F, -13.0F, 14.15F};
   private ForeEar eyeRightSection;
   private static final float[] TAIL_CURVE_POINTS = new float[]{
      -14.1F,
      16.95F,
      -16.6F,
      19.0F,
      -18.0F,
      19.8F,
      -17.35F,
      18.5F,
      -16.35F,
      16.9F,
      -15.55F,
      15.85F,
      -15.1F,
      15.2F,
      -15.55F,
      14.55F,
      -16.35F,
      13.5F,
      -17.35F,
      11.9F,
      -18.0F,
      10.6F,
      -16.6F,
      11.4F,
      -14.1F,
      13.45F
   };
   private static final float[] FLIPPER_ANCHOR_POINTS = new float[]{-6.3F, 12.75F, -9.7F, 13.9F};
   private static final float[] FLIPPER_CURVE_POINTS = new float[]{-6.5F, 11.75F, -7.2F, 11.05F, -8.1F, 11.25F, -8.9F, 11.95F, -9.6F, 12.95F};
   private final ForeEar[] thighSections = new ForeEar[4];
   private ForeEar tailB;
   private static final float[] SPIKE_ANCHOR_POINTS = new float[]{0.0F, 0.5F, 0.0F, -0.5F};
   private static final float[] SPIKE_OUTLINE_POINTS = new float[]{0.9F, 1.0F, 2.2F, 1.0F, 3.25F, 0.4F, 3.2F, -0.4F, 2.2F, -0.9F, 0.9F, -0.9F};
   private ForeEar ruffSection;
   private static final float[] SMALL_SPIKE_ANCHOR_POINTS = new float[]{0.0F, 0.3F, 0.0F, -0.3F};
   private ForeEar tailFinSection;
   private static final float[] SMALL_SPIKE_OUTLINE_POINTS = new float[]{0.8F, 0.55F, 2.0F, 0.15F, 1.8F, -0.3F, 0.7F, -0.45F};
   private final ForeEar[] footSections;

   private static float[] getAmounts(float[] tailEarValues, float[] standCellKeys) {
      int tailEarValue = standCellKeys.length / 2;
      float[] floats = new float[2 * (tailEarValue + 2)];
      floats[0] = tailEarValues[0];
      floats[1] = tailEarValues[1];
      System.arraycopy(standCellKeys, 0, floats, 2, standCellKeys.length);
      floats[2 * tailEarValue + 2] = tailEarValues[2];
      floats[2 * tailEarValue + 3] = tailEarValues[3];
      return floats;
   }

   private static float getAmount(float tailEarValue, float tailEarValue2) {
      float amountsShinFootTable = getAmountsShinFootTable(tailEarValue2);
      float tailEarFactor = amountsShinFootTable * 1.0F;
      float tailEarFactor2 = amountsShinFootTable * 0.85F;
      float tailEarValue4 = getAmount4(tailEarValue, tailEarValue2) - -11.0F;
      float tailEarValue5 = tailEarValue / (tailEarFactor * tailEarFactor);
      float tailEarValue6 = tailEarValue4 / (tailEarFactor2 * tailEarFactor2);
      return (float)Math.toDegrees(Math.atan2(-tailEarValue5, -tailEarValue6));
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      boolean shinProgressFlag = poseProgress.sneakingPose || poseProgress.vehicle || poseProgress.gliding || poseProgress.swimming || poseProgress.inPose || poseProgress.swingProgress > 0.0F;
      float shinProgressFactor2 = !shinProgressFlag && !poseProgress.poseProgressActive ? 1.0F - getAmount2(shinProgressFactor / 0.2F) : 0.0F;

      for (int i = 0; i < 4; i++) {
         float shinProgressValue = (float)Math.cos(angle + LEG_PHASES[i]) * shinProgressFactor;
         float shinProgressFactor3 = Math.max(0.0F, -((float)Math.sin(angle + LEG_PHASES[i]))) * shinProgressFactor;
         float shinProgressValue2 = 0.3F * shinProgressValue + 0.75F * shinProgressFactor3;
         float foreShinValue = -1.55F * shinProgressFactor3;
         foreShinScale.getAmounts(this.thighSections[i], shinProgressValue2, 0.0F, LEG_SWAY_OFFSETS[i]);
         foreShinScale.getAmounts(this.shinProgressForeEars[i], foreShinValue, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footSections[i], -(shinProgressValue2 + foreShinValue) - 0.3F * shinProgressFactor3, 0.0F, -LEG_SWAY_OFFSETS[i]);
      }

      float shinProgressValue4 = -0.3F * Math.abs((float)Math.sin(2.0F * angle)) * shinProgressFactor;
      foreShinScale.getAmountsForeShin2(this.trunk, 0.0F, shinProgressValue4, 0.0F);
      float amount5 = getAmount5(poseProgress.headYawRadians, 1.2F);
      float shinProgressValue6 = getAmount5(poseProgress.shinProgressAmount, 0.9F);
      float shinProgressFactor4 = (float)Math.sin(angle) * shinProgressFactor;
      foreShinScale.getAmounts(this.head, 0.0F, 0.11F * shinProgressFactor4 + 0.08F * amount5, 0.045F * shinProgressFactor4);
      float shinProgressTotal = shinProgressValue6 * 0.8F;
      float dinForeValue = amount5 * 0.8F - 0.1F * shinProgressFactor4;
      float shinProgressTotal3 = -0.04F * shinProgressFactor4;
      float shinProgressValue7 = 0.12F * poseProgress.getAgeCode(90.0F, 0.0F) * shinProgressFactor2 + 0.04F * poseProgress.getAgeCode(37.0F, 1.0F) * shinProgressFactor2;
      foreShinScale.getAmounts(this.tailB, 0.0F, 0.17F * (float)Math.sin(angle - 0.9F) * shinProgressFactor + shinProgressValue7 * 0.6F, 0.0F);
      foreShinScale.getAmounts(this.tailFin, 0.0F, 0.26F * (float)Math.sin(angle - 1.8F) * shinProgressFactor + shinProgressValue7, 0.0F);
      foreShinScale.getAmounts(this.tailFinSection, 0.0F, 0.36F * (float)Math.sin(angle - 2.7F) * shinProgressFactor + shinProgressValue7 * 1.3F, 0.0F);
      float shinProgressValue8 = 0.12F * poseProgress.getAgeCode(45.0F, 0.3F) + 0.25F * (float)Math.sin(angle * 2.0F) * shinProgressFactor;
      foreShinScale.getAmounts(this.shinProgressForeEar, 0.0F, 0.1F + shinProgressValue8, 0.0F);
      foreShinScale.getAmounts(this.tailEarForeEar, 0.0F, -0.1F - shinProgressValue8, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(this.head, 1.0F + 0.012F * poseProgress.getAgeCode(60.0F, 0.0F), 1.0F + 0.012F * poseProgress.getAgeCode(60.0F, 0.0F), 1.0F);
      float shinProgressFactor5 = ((poseProgress.age + (poseProgress.hashCode & 127) * 2.0F + 70.0F) % 97.0F + 97.0F) % 97.0F;
      float shinProgressBound = shinProgressFactor5 < 4.0F ? (float)Math.sin(Math.PI * shinProgressFactor5 / 4.0) : 0.0F;
      float shinProgressValue9 = ((poseProgress.age + (poseProgress.hashCode & 0xFF) + 150.0F) % 260.0F + 260.0F) % 260.0F;
      if (shinProgressValue9 < 28.0F) {
         shinProgressBound = Math.max(shinProgressBound, getAmount3(shinProgressValue9 / 9.0F) * (1.0F - getAmount3((shinProgressValue9 - 15.0F) / 13.0F)) * 0.9F);
      }

      if (poseProgress.inPose) {
         shinProgressBound = 1.0F;
      }

      foreShinScale.getAmountsForForeEarFloat(this.eyeRightSection, 1.0F, 1.0F - 0.9F * shinProgressBound, 1.0F);
      foreShinScale.getAmountsForForeEarFloat(this.classShinFoot, 1.0F, 1.0F - 0.9F * shinProgressBound, 1.0F);
      float shinProgressFactor6 = ((poseProgress.age + (poseProgress.hashCode & 63) * 3.0F) % 110.0F + 110.0F) % 110.0F;
      float shinProgressFactor7 = shinProgressFactor6 < 6.0F ? (float)Math.sin(Math.PI * shinProgressFactor6 / 6.0) : 0.0F;
      foreShinScale.getAmounts(this.foreEar, 0.25F * shinProgressFactor7, 0.0F, -0.35F * shinProgressFactor7);
      foreShinScale.getAmounts(this.classShinProgress, 0.02F * poseProgress.getAgeCode(80.0F, 0.5F), 0.0F, 0.03F * poseProgress.getAgeCode(70.0F, 1.1F));
      float shinProgressFactor8 = ((poseProgress.age + (poseProgress.hashCode & 0xFF) * 1.3F + 240.0F) % 320.0F + 320.0F) % 320.0F;
      if (shinProgressFactor2 > 0.0F && shinProgressFactor8 < 22.0F) {
         float shinProgressFactor9 = (float)Math.sin(Math.PI * shinProgressFactor8 / 22.0) * shinProgressFactor2;
         foreShinScale.getAmounts(this.shinFootForeEar, -0.4F * shinProgressFactor9, 0.0F, 0.0F);
         shinProgressTotal += 0.18F * shinProgressFactor9;
         shinProgressTotal3 += 0.1F * shinProgressFactor9;
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, 0.0F, 0.6F * shinProgressFactor9, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailEarForeEar, 0.0F, -0.6F * shinProgressFactor9, 0.0F);
         foreShinScale.getAmountsForForeEarFloat(this.ruffSection, 1.0F + 0.08F * shinProgressFactor9, 1.0F + 0.08F * shinProgressFactor9, 1.0F);
      }

      float shinProgressValue10 = ((poseProgress.age + (poseProgress.hashCode & 0xFF) * 2.3F + 300.0F) % 700.0F + 700.0F) % 700.0F;
      if (shinProgressFactor2 > 0.0F && shinProgressValue10 < 72.0F) {
         float shinProgressFactor10 = getAmount3(shinProgressValue10 / 11.0F) * (1.0F - getAmount3((shinProgressValue10 - 54.0F) / 16.0F)) * shinProgressFactor2;
         float planValuesItem = (float)Math.sin(shinProgressValue10 * 1.15F) * getAmount3((shinProgressValue10 - 9.0F) / 4.0F) * (1.0F - getAmount3((shinProgressValue10 - 50.0F) / 6.0F)) * shinProgressFactor2;
         float shinProgressValue12 = 4.2F * shinProgressFactor10;
         foreShinScale.getAmountsForeShin2(this.trunk, 0.0F, -shinProgressValue12, 0.0F);
         this.handleAmounts(foreShinScale, shinProgressValue12);
         float angle2 = 0.55F * shinProgressFactor10 + 0.1F * planValuesItem * shinProgressFactor10;
         foreShinScale.getAmountsBoneCount(this.head, 0.0F, 0.16F * planValuesItem, angle2);
         foreShinScale.getAmountsForeShin2(this.head, -2.8F * (float)Math.sin(angle2), -2.8F * (1.0F - (float)Math.cos(angle2)), 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailB, 0.0F, 0.25F * planValuesItem, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailFin, 0.0F, 0.35F * planValuesItem, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailFinSection, 0.0F, 0.45F * planValuesItem, 0.0F);
         shinProgressTotal3 -= 0.3F * shinProgressFactor10;
         dinForeValue -= 0.12F * planValuesItem;
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, 0.0F, 0.5F * Math.abs(planValuesItem), 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailEarForeEar, 0.0F, -0.5F * Math.abs(planValuesItem), 0.0F);
         foreShinScale.getAmountsBoneCount(this.foreEar, 0.3F * shinProgressFactor10, 0.0F, -0.3F * shinProgressFactor10);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, 0.3F * shinProgressFactor10, 0.0F, 0.3F * shinProgressFactor10);
      }

      if (poseProgress.swingProgress > 0.0F) {
         float sin = poseProgress.getSwingProgress();
         float sin2 = (float)Math.sin(Math.PI * getAmount2(poseProgress.swingProgress / 0.75F));
         float sin3 = (float)Math.sin(poseProgress.swingProgress * 2.0F * Math.PI);
         foreShinScale.getAmounts(this.shinFootForeEar, -0.6F * sin2, 0.0F, 0.0F);
         shinProgressTotal += 0.26F * sin2;
         foreShinScale.getAmountsForForeEarFloat(this.ruffSection, 1.0F + 0.18F * sin2, 1.0F + 0.18F * sin2, 1.0F);
         foreShinScale.getAmountsForeShin2(this.earL, 0.0F, 0.2F * sin, -0.7F * sin);
         foreShinScale.getAmountsForeShin2(this.head, 0.0F, 0.0F, -0.5F * sin);
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEar, 0.0F, 0.7F * sin2, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailEarForeEar, 0.0F, -0.7F * sin2, 0.0F);
         foreShinScale.getAmountsBoneCount(this.thighSections[0], 1.05F * sin, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEars[0], -0.35F * sin, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailB, 0.0F, 0.3F * sin3, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailFin, 0.0F, 0.3F * sin3, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailFinSection, 0.0F, 0.45F * sin3, 0.0F);
      }

      if (poseProgress.sneakingPose) {
         float tailEarValue = 2.6F;
         foreShinScale.getAmountsForeShin2(this.trunk, 0.0F, -tailEarValue, 0.0F);
         this.handleAmounts(foreShinScale, tailEarValue);
         foreShinScale.getAmountsBoneCount(this.foreEar, 0.55F, 0.0F, -0.45F);
         foreShinScale.getAmountsBoneCount(this.classShinProgress, 0.55F, 0.0F, 0.45F);
         shinProgressTotal -= 0.25F;
         foreShinScale.getAmountsBoneCount(this.tailB, 0.06F, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.tailFinSection, 0.0F, 0.3F * poseProgress.getAgeCode(20.0F, 0.0F), 0.0F);
      }

      foreShinScale.getAmounts(this.earL, shinProgressTotal, dinForeValue, shinProgressTotal3);
      foreShinScale.getAmounts(this.ruffSection, -0.85F * shinProgressTotal, -0.85F * dinForeValue, -0.85F * shinProgressTotal3);
      if (poseProgress.vehicle) {
         for (int j = 0; j < 4; j++) {
            float signum = Math.signum(shinProgressTable[j]);
            if (j < 2) {
               foreShinScale.getAmounts(this.thighSections[j], 1.4F, -0.14F * signum, 0.0F);
               foreShinScale.getAmounts(this.shinProgressForeEars[j], -0.2F, 0.0F, 0.0F);
               foreShinScale.getAmounts(this.footSections[j], -0.9F, 0.0F, 0.0F);
            } else {
               foreShinScale.getAmounts(this.thighSections[j], 1.3F, -0.2F * signum, 0.0F);
               foreShinScale.getAmounts(this.shinProgressForeEars[j], -2.6F, 0.0F, 0.0F);
               foreShinScale.getAmounts(this.footSections[j], 1.3F, 0.0F, 0.0F);
            }
         }
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         float angle3 = poseProgress.age * 0.45F;

         for (int k = 0; k < 4; k++) {
            foreShinScale.getAmounts(this.thighSections[k], -1.15F + 0.25F * (float)Math.sin(angle3 + k * 1.6F), 0.0F, 0.0F);
            foreShinScale.getAmounts(this.shinProgressForeEars[k], -0.3F, 0.0F, 0.0F);
            foreShinScale.getAmounts(this.footSections[k], -0.4F, 0.0F, 0.0F);
         }

         foreShinScale.getAmounts(this.tailB, 0.0F, 0.2F * (float)Math.sin(angle3), 0.0F);
         foreShinScale.getAmounts(this.tailFin, 0.0F, 0.3F * (float)Math.sin(angle3 - 0.9F), 0.0F);
         foreShinScale.getAmounts(this.tailFinSection, 0.0F, 0.4F * (float)Math.sin(angle3 - 1.8F), 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.head, 1.06F, 0.92F, 1.06F);
      }
   }

   private static float getAmount(BonecaAmbalabu bonecaAmbalabu, float tailEarValue) {
      return bonecaAmbalabu.bonecaValue * getAmounts(tailEarValue);
   }

   @Override
   public int getValue() {
      return 2;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float cos = (float)Math.cos((float) (Math.PI * 2) * ((i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F));
         bonecaAmbalabu.getClassBonecaAmbalabu(
            i,
            0.0F,
            i + 1,
            bonecaAmbalabu.bonecaValue,
            ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15251350), ForeShinUtils.getValueForInt(14260864), 0.45F * getAmount3((cos - 0.6F) / 0.35F))
         );
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 3.0F, 97);
   }

   private static float[] getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2) {
      float angle = bonecaAmbalabuValue2 / bonecaAmbalabu.bonecaValue * 0.55F * (float) Math.PI;
      if (angle > Math.PI / 2) {
         return null;
      } else {
         float angle2 = (float) (Math.PI * 2) * (bonecaAmbalabuValue / bonecaAmbalabu.ambalabuBonecaValue - 0.5F);
         float sin = (float)Math.sin(angle);
         return new float[]{-((float)Math.sin(angle2)) * sin, -((float)Math.cos(angle2)) * sin};
      }
   }

   private void handle(LatheLoft latheLoft, float tailEarValue, ShinBrr shinBrr) {
      float[] floats = new float[]{tailEarValue * 2.2F, 18.3F, -11.35F, tailEarValue * 2.65F, 20.1F, -11.3F, tailEarValue * 3.1F, 21.8F, -11.05F};
      int latheLoftSize = LatheLoft.getValue(floats);
      latheLoft.getLoftPath(
         floats, LatheLoft.getAmounts(latheLoftSize, 1.6F, 1.1F, 0.05F), LatheLoft.getAmounts(latheLoftSize, 0.46F, 0.34F, 0.04F), 8, shinBrr, shinBrr, null, tailEarValue * 0.3F, 0.0F, -1.0F
      );
   }

   private static void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      short bonecaAmbalabuValue = 301;
      byte bonecaAmbalabuDivisor = 46;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(13208138, 0.0F));

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float absScaled = Math.abs((i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 2.0F;
         bonecaAmbalabu.getClassBonecaAmbalabu(i, bonecaAmbalabu.bonecaValue * 0.55F, i + 1, bonecaAmbalabu.bonecaValue, getValueForFloat(absScaled));
      }

      for (int j = 0; j < 2; j++) {
         for (int k = 0; k < bonecaAmbalabuDivisor; k++) {
            float bonecaAmbalabuFactor = (k + 0.5F * j + getAmount(k, 1 + j, bonecaAmbalabuValue) * 0.3F) / bonecaAmbalabuDivisor;
            float bonecaAmbalabuValue2 = bonecaAmbalabuFactor * bonecaAmbalabu.ambalabuBonecaValue;
            float tailEarValue = Math.abs(bonecaAmbalabuFactor - 0.5F) * 2.0F;
            int valueForFloat = getValueForFloat(tailEarValue);
            if (j == 0) {
               valueForFloat = ForeShinUtils.getValue(valueForFloat, 0.92F);
            }

            if (k % 4 == 1 && tailEarValue < 0.6F) {
               valueForFloat = ForeShinUtils.getValue2(valueForFloat, ForeShinUtils.getValueForInt(7029798), 0.4F);
            }

            float bonecaAmbalabuValue4 = (j == 0 ? 1.0F : 0.75F) * (0.55F + 0.45F * getAmount(k, 2 + j, bonecaAmbalabuValue));
            float bonecaAmbalabuValue5 = bonecaAmbalabu.bonecaValue * (0.6F - 0.58F * bonecaAmbalabuValue4);
            float bonecaAmbalabuValue6 = (float)bonecaAmbalabu.ambalabuBonecaValue / bonecaAmbalabuDivisor * (0.7F + 0.3F * getAmount(k, 3 + j, bonecaAmbalabuValue));
            float bonecaAmbalabuValue7 = (getAmount(k, 4 + j, bonecaAmbalabuValue) - 0.5F) * bonecaAmbalabu.ambalabuBonecaValue / bonecaAmbalabuDivisor * 1.2F;
            bonecaAmbalabu.getClassForIntFloatArray(valueForFloat, bonecaAmbalabuValue2 - bonecaAmbalabuValue6, bonecaAmbalabu.bonecaValue * 0.62F, bonecaAmbalabuValue2 + bonecaAmbalabuValue6, bonecaAmbalabu.bonecaValue * 0.62F, bonecaAmbalabuValue2 + bonecaAmbalabuValue7, bonecaAmbalabuValue5);
            bonecaAmbalabu.getClassForIntFloatArray(
               ForeShinUtils.getValueForIntFloat(14921330, tailEarValue < 0.6F ? 0.5F : 0.25F),
               bonecaAmbalabuValue2 - bonecaAmbalabuValue6 * 0.35F + bonecaAmbalabuValue7 * 0.6F,
               bonecaAmbalabu.bonecaValue * 0.62F - (bonecaAmbalabu.bonecaValue * 0.62F - bonecaAmbalabuValue5) * 0.55F,
               bonecaAmbalabuValue2 + bonecaAmbalabuValue6 * 0.35F + bonecaAmbalabuValue7 * 0.6F,
               bonecaAmbalabu.bonecaValue * 0.62F - (bonecaAmbalabu.bonecaValue * 0.62F - bonecaAmbalabuValue5) * 0.55F,
               bonecaAmbalabuValue2 + bonecaAmbalabuValue7,
               bonecaAmbalabuValue5
            );
         }
      }

      for (int m = 0; m < 900; m++) {
         float classBound = getAmount(m, 1, bonecaAmbalabuValue + 7) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = bonecaAmbalabu.bonecaValue * (0.15F + 0.85F * getAmount(m, 2, bonecaAmbalabuValue + 7));
         if (ForeShinUtils.getValue6(bonecaAmbalabu.getBonecaAmbalabuForInt((int)classBound, (int)amountForFloat)) >= 128) {
            float bonecaAmbalabuValue10 = bonecaAmbalabu.bonecaValue * 0.18F * (0.5F + getAmount(m, 3, bonecaAmbalabuValue + 7));
            bonecaAmbalabu.getClass02(
               classBound,
               amountForFloat,
               classBound + (getAmount(m, 4, bonecaAmbalabuValue + 7) - 0.5F) * 3.0F,
               amountForFloat - bonecaAmbalabuValue10,
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

   private static float getAmount(float tailEarFactor) {
      return -8.0F + 1.4F * (float)Math.sqrt(Math.max(0.0F, 1.0F - tailEarFactor * tailEarFactor));
   }

   private static float getAmountForFloat(float tailEarValue, float tailEarValue2) {
      float cos = (float)Math.cos((float) (Math.PI * 2) * (tailEarValue - 0.5F));
      if (tailEarValue2 < getAmount(cos)) {
         return 1.04F;
      } else {
         float amountsForFloat = getAmountsForFloat(tailEarValue2);
         float tailEarValue4 = tailEarValue * 22.0F;
         int tailEarValue5 = (int)Math.floor(amountsForFloat) - 1;
         int tailEarLimit = Integer.MAX_VALUE;
         float distance2 = 0.0F;

         for (int i = tailEarValue5; i <= tailEarValue5 + 2; i++) {
            float tailEarValue6 = (i & 1) * 0.5F;
            int floor7 = (int)Math.floor(tailEarValue4 - tailEarValue6);

            for (int j = floor7 - 1; j <= floor7 + 1; j++) {
               float tailEarFactor = tailEarValue4 - (j + 0.5F + tailEarValue6);
               float deltaVar = amountsForFloat - i;
               float distance = (float)Math.sqrt(tailEarFactor * tailEarFactor + deltaVar * deltaVar);
               if (distance < 0.72F && (i < tailEarLimit || i == tailEarLimit && distance < distance2)) {
                  tailEarLimit = i;
                  distance2 = distance;
               }
            }
         }

         if (tailEarLimit == Integer.MAX_VALUE) {
            return 1.0F;
         } else {
            float tailEarValue8 = 1.0F - getAmount3((0.72F - distance2) / 0.14F);
            float tailEarValue9 = (tailEarLimit & 1) * 0.5F;
            int floor10 = (int)Math.floor(tailEarValue4 - tailEarValue9 + 0.5F);
            float tailEarValue11 = (getAmount(tailEarLimit + 4096, floor10 & 63, 77) - 0.5F) * 0.09F;
            float tailEarValue12 = 1.0F - getAmount3((amountsForFloat - tailEarLimit + 0.2F) / 0.45F);
            return 1.0F + tailEarValue11 + 0.06F * (1.0F - distance2 / 0.72F) + 0.12F * tailEarValue12 - 0.28F * tailEarValue8;
         }
      }
   }

   private void handle(LatheLoft latheLoft, float tailEarValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float sideScaled = tailEarValue * 1.6F;
      latheLoft.getDequeArrayArrayDeque()
         .getLatheLoft(sideScaled, 16.35F, getAmount4(sideScaled, 16.35F))
         .getLatheLoft4(getAmount(sideScaled, 16.35F) * 0.85F)
         .getLatheLoft(tailEarValue * 9.0F)
         .getLatheLoft(0.0F, 0.0F, 0.18F);
      latheLoft.getDequeArrayArrayDeque().getLatheLoftLatheLoftAmountLoft(-90.0F);
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 0.98F, 0.5F, 0.7F, 14, 0.0F, 0.55F, shinBrr);
      latheLoft.getDequeArray();
      latheLoft.getLoftLathe(0.0F, 0.0F, 0.0F, 1.0486001F, 0.784F, 0.625F, 10, 0.0F, 0.12F, shinBrr2);
      latheLoft.getLoftLathe(-tailEarValue * 0.35F, 0.3F, -0.5F, 0.14F, 0.14F, 6, shinBrr3);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuValue2 = bonecaAmbalabuFlag ? getAmount3((bonecaAmbalabuValue - 0.62F) / 0.38F) : 1.0F - 0.6F * getAmount3(bonecaAmbalabuValue / 0.28F);

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue3 = (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
            float cos = (float)Math.cos((float) (Math.PI * 2) * (bonecaAmbalabuValue3 - 0.5F));
            bonecaAmbalabu.getBonecaAmbalabu(
               j,
               i,
               ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(15251350), ForeShinUtils.getValueForInt(14260864), 0.5F * bonecaAmbalabuValue2 * getAmount3((cos - 0.45F) / 0.45F))
            );
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 4.0F, bonecaAmbalabuFlag ? 91 : 93);
   }

   private static void handleBonecaAmbalabu3(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuFactor = 1.7278761F;

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuLimit = (i + 0.5F) / bonecaAmbalabu.bonecaValue * bonecaAmbalabuFactor;
         float sin = (float)Math.sin(Math.min((double)bonecaAmbalabuLimit, Math.PI / 2));
         int bonecaAmbalabuValue;
         if (bonecaAmbalabuLimit > 1.4451326206513049) {
            bonecaAmbalabuValue = ForeShinUtils.getValueForInt(1839628);
         } else if (sin < 0.3F) {
            bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13738554), ForeShinUtils.getValueForInt(12759114), sin / 0.3F);
         } else if (sin < 0.62F) {
            bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12759114), ForeShinUtils.getValueForInt(10203210), (sin - 0.3F) / 0.32F);
         } else if (sin < 0.88F) {
            bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(10203210), ForeShinUtils.getValueForInt(7244342), (sin - 0.62F) / 0.26F);
         } else {
            bonecaAmbalabuValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(7244342), ForeShinUtils.getValueForInt(2502682), (sin - 0.88F) / 0.12F);
         }

         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, bonecaAmbalabuValue);
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(6191658, 0.25F), 4.0F * bonecaAmbalabu.ambalabuBonecaValue / 96.0F, 1.2F * bonecaAmbalabu.ambalabuBonecaValue / 96.0F, 0.0F);
      bonecaAmbalabu.getBonecaAmbalabu(0.06F, 3.0F * bonecaAmbalabu.ambalabuBonecaValue / 96.0F, 31);
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(1053706, 0.35F), (bonecaAmbalabuValue2, bonecaAmbalabuValue3) -> {
         float[] var3x = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue2, bonecaAmbalabuValue3);
         return var3x != null && var3x[1] > 0.55F;
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForInt(855309), (bonecaAmbalabuValue4, bonecaAmbalabuValue5) -> {
         float[] var3x = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue4, bonecaAmbalabuValue5);
         return var3x != null && getAmount4(var3x[0] / 0.16F) + getAmount4(var3x[1] / 0.9F) <= 1.0F;
      });
      bonecaAmbalabu.getBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(16777215, 0.9F), (bonecaAmbalabuValue6, bonecaAmbalabuValue7) -> {
         float[] var3x = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValue6, bonecaAmbalabuValue7);
         if (var3x == null) {
            return false;
         } else {
            float var4x = var3x[0] - 0.36F;
            float var5x = var3x[1] - 0.38F;
            return var4x * var4x + var5x * var5x <= 0.0121F;
         }
      });
   }

   private static float getAmount3(float tailEarValue, float tailEarValue2) {
      float tailEarBound = 1.0F - getAmount4(tailEarValue / 1.5F) - getAmount4((tailEarValue2 - 13.9F) / 0.95F);
      return getAmount() - 1.15F * (float)Math.sqrt(Math.max(0.0F, tailEarBound));
   }

   private void handleAmountsFin(LatheLoft latheLoft, float finValue, float finValue2, float finLimit, float finDivisor, int loftPathFactor, ShinBrr shinBrr, ShinBrr shinBrr2) {
      int finSize = Math.max(2, Math.round((finValue2 - finValue) / finDivisor)) + 1;
      float[] floats = new float[3 * finSize];
      float[] crownForeValues = new float[finSize];
      float[] floats3 = new float[finSize];

      for (int i = 0; i < finSize; i++) {
         float tailEarLimit = finValue + (finValue2 - finValue) * i / (finSize - 1);
         float finFactor = finLimit > 0.0F && tailEarLimit < finValue + finLimit ? 0.94F + 0.06F * getAmount3((tailEarLimit - finValue) / finLimit) : 1.0F;
         floats[3 * i + 1] = getAmounts(tailFinAmounts, tailEarLimit);
         floats[3 * i + 2] = tailEarLimit;
         floats3[i] = getAmounts(finTable, tailEarLimit) * finFactor;
         crownForeValues[i] = getAmounts(TAIL_RADII, tailEarLimit) * finFactor;
      }

      latheLoft.getLoftPath(floats, crownForeValues, floats3, loftPathFactor, shinBrr, null, shinBrr2, 0.0F, 1.0F, 0.0F);
   }

   private static float[] getAmounts(BonecaAmbalabu bonecaAmbalabu, float tailEarValue, float tailEarValue2, float tailEarFactor, float tailEarFactor2, float degrees, int tailEarLimit) {
      float[] floats = new float[2 * tailEarLimit];
      float cos = (float)Math.cos(Math.toRadians(degrees));
      float sin = (float)Math.sin(Math.toRadians(degrees));

      for (int i = 0; i < tailEarLimit; i++) {
         float angle = (float) (Math.PI * 2) * i / tailEarLimit;
         float tailEarFactor3 = tailEarFactor * (float)Math.cos(angle);
         float tailEarFactor4 = tailEarFactor2 * (float)Math.sin(angle);
         float bonecaAmbalabuValue = tailEarValue + tailEarFactor3 * cos - tailEarFactor4 * sin;
         float tailEarValue4 = tailEarValue2 + tailEarFactor3 * sin + tailEarFactor4 * cos;
         floats[2 * i] = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, bonecaAmbalabuValue, tailEarValue4);
         floats[2 * i + 1] = getAmount(bonecaAmbalabu, tailEarValue4);
      }

      return floats;
   }

   private static float getAmount4(float tailEarValue, float tailEarValue2) {
      float amountsShinFootTable = getAmountsShinFootTable(tailEarValue2);
      float tailEarFactor = tailEarValue / (amountsShinFootTable * 1.0F);
      return -11.0F - 0.85F * amountsShinFootTable * (float)Math.sqrt(Math.max(0.0F, 1.0F - tailEarFactor * tailEarFactor));
   }

   private static float getAmounts(float[] tailEarValues, float tailEarLimit) {
      int tailEarValue = table.length;
      if (tailEarLimit <= table[0]) {
         return tailEarValues[0];
      } else if (tailEarLimit >= table[tailEarValue - 1]) {
         return tailEarValues[tailEarValue - 1];
      } else {
         int index = 0;

         while (index < tailEarValue - 2 && tailEarLimit > table[index + 1]) {
            index++;
         }

         float tailEarFactor = (tailEarLimit - table[index]) / (table[index + 1] - table[index]);
         float tailEarValue2 = tailEarValues[Math.max(0, index - 1)];
         float tailEarValue3 = tailEarValues[index];
         float tailEarValue4 = tailEarValues[index + 1];
         float tailEarValue5 = tailEarValues[Math.min(tailEarValue - 1, index + 2)];
         float tailEarFactor2 = tailEarFactor * tailEarFactor;
         float tailEarFactor3 = tailEarFactor2 * tailEarFactor;
         return 0.5F
            * (
               2.0F * tailEarValue3
                  + (-tailEarValue2 + tailEarValue4) * tailEarFactor
                  + (2.0F * tailEarValue2 - 5.0F * tailEarValue3 + 4.0F * tailEarValue4 - tailEarValue5) * tailEarFactor2
                  + (-tailEarValue2 + 3.0F * tailEarValue3 - 3.0F * tailEarValue4 + tailEarValue5) * tailEarFactor3
            );
      }
   }

   @Override
   public String formatTungDin() {
      return "trulimero_trulicina";
   }

   private static void handleBonecaAmbalabu4(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13208138))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11103798), 0.4F, 3.0F, 63)
         .getBonecaAmbalabu(0.06F, 3.0F, 61);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.8F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(9328166, 0.5F));
      bonecaAmbalabu.getClassForIntFloatArray(
         ForeShinUtils.getValueForIntFloat(14921330, 0.9F),
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
         ForeShinUtils.getValueForInt(14919066),
         bonecaAmbalabu.ambalabuBonecaValue * 0.32F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.68F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.51F,
         bonecaAmbalabu.bonecaValue * 0.84F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.49F,
         bonecaAmbalabu.bonecaValue * 0.84F
      );
      bonecaAmbalabu.getClassForIntFloatArray(
         ForeShinUtils.getValueForIntFloat(13601410, 0.7F),
         bonecaAmbalabu.ambalabuBonecaValue * 0.42F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.58F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * 0.55F
      );

      for (int i = -3; i <= 3; i++) {
         if (i != 0) {
            float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * (0.5F + i * 0.035F);
            bonecaAmbalabu.getClassForFloatInt(
               bonecaAmbalabuValue,
               bonecaAmbalabu.bonecaValue * 0.02F,
               bonecaAmbalabuValue + i * bonecaAmbalabu.ambalabuBonecaValue / 40.0F,
               bonecaAmbalabu.bonecaValue * 0.25F,
               bonecaAmbalabuValue + i * bonecaAmbalabu.ambalabuBonecaValue / 16.0F,
               bonecaAmbalabu.bonecaValue * 0.42F,
               bonecaAmbalabu.ambalabuBonecaValue / 50.0F,
               ForeShinUtils.getValueForIntFloat(15853788, 0.85F)
            );
         }
      }

      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue * 0.12F, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(7029798, 0.4F));
      bonecaAmbalabu.getClassBonecaAmbalabu(bonecaAmbalabu.ambalabuBonecaValue * 0.88F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(7029798, 0.4F));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.88F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(4861466, 0.55F));
   }

   private void handle(LatheLoft latheLoft, float[] tailEarValues, float[] standCellKeys, ShinBrr shinBrr, ShinBrr shinBrr2) {
      latheLoft.getDequeArrayArrayDeque().getLatheLoft4(90.0F);
      latheLoft.getPolygonExtrude(getAmounts(tailEarValues, standCellKeys), -0.07F, 0.07F, shinBrr, shinBrr2, null);
      latheLoft.getDequeArray();
   }

   private static int getValue(float[] tailEarValues, int index, BonecaAmbalabu bonecaAmbalabu, float[] bonecaAmbalabuValues, boolean tailEarFlag, float tailEarValue, float tailEarValue2) {
      tailEarValues[index] = getBonecaAmbalabu(bonecaAmbalabu, bonecaAmbalabuValues, tailEarFlag, tailEarValue);
      tailEarValues[index + 1] = getAmount(bonecaAmbalabu, bonecaAmbalabuValues, tailEarValue2);
      return index + 2;
   }

   private static int getValue(float tailEarLimit) {
      int tailEarValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(12107974), ForeShinUtils.getValueForInt(16777215), 0.55F);
      if (tailEarLimit > 0.55F) {
         tailEarValue = ForeShinUtils.getValue2(tailEarValue, ForeShinUtils.getValueForInt(4152174), getAmount3((tailEarLimit - 0.55F) / 0.3F));
      }

      if (tailEarLimit > 0.88F) {
         tailEarValue = ForeShinUtils.getValue2(tailEarValue, ForeShinUtils.getValueForInt(3428447), 0.5F * getAmount3((tailEarLimit - 0.88F) / 0.12F));
      }

      if (tailEarLimit < -0.3F) {
         tailEarValue = ForeShinUtils.getValue2(tailEarValue, ForeShinUtils.getValueForInt(15922421), getAmount3((-tailEarLimit - 0.3F) / 0.4F));
      }

      float tailEarValue2 = 1.0F - getAmount3(Math.abs(tailEarLimit - 0.15F) / 0.25F);
      int foreShinValue = ForeShinUtils.getValue2(tailEarValue, ForeShinUtils.getValueForInt(16777215), 0.6F * tailEarValue2);
      float tailEarValue4 = 1.0F - getAmount3(Math.abs(tailEarLimit + 0.05F) / 0.14F);
      return ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(15129840), 0.2F * tailEarValue4);
   }

   public TailEar() {
      this.shinProgressForeEars = new ForeEar[4];
      this.footSections = new ForeEar[4];
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float[] bonecaAmbalabuValues, boolean bonecaAmbalabuFlag, float bonecaAmbalabuValue) {
      float bonecaAmbalabuDivisor = bonecaAmbalabuValues[1] - bonecaAmbalabuValues[0];
      return bonecaAmbalabuFlag ? (bonecaAmbalabuValue - bonecaAmbalabuValues[0]) / bonecaAmbalabuDivisor * bonecaAmbalabu.ambalabuBonecaValue : (bonecaAmbalabuValues[1] - bonecaAmbalabuValue) / bonecaAmbalabuDivisor * bonecaAmbalabu.ambalabuBonecaValue;
   }

   private void handleAmounts(ForeShinScale foreShinScale, float tailEarValue) {
      for (int i = 0; i < 4; i++) {
         float x = THIGH_HEIGHTS[i] - SHIN_HEIGHTS[i];
         float x2 = SHIN_HEIGHTS[i] - 1.15F;
         float sqrt = (float)Math.sqrt(x * x + 0.0144F);
         float sqrt2 = (float)Math.sqrt(x2 * x2 + 0.0144F);
         float angle = (float)Math.atan2(0.12F, x);
         float angle2 = (float)Math.atan2(0.12F, x2);
         float tailEarFactor = x + x2 - tailEarValue;
         float tailEarBound = (sqrt * sqrt + tailEarFactor * tailEarFactor - sqrt2 * sqrt2) / (2.0F * sqrt * tailEarFactor);
         float acos3 = (float)Math.acos(Math.max(-1.0F, Math.min(1.0F, tailEarBound)));
         float asin = (float)Math.asin(Math.max(-1.0F, Math.min(1.0F, sqrt * (float)Math.sin(acos3) / sqrt2)));
         foreShinScale.getAmountsBoneCount(this.thighSections[i], acos3 - angle, 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.shinProgressForeEars[i], -(asin - angle2) - (acos3 - angle), 0.0F, 0.0F);
         foreShinScale.getAmountsBoneCount(this.footSections[i], asin - angle2, 0.0F, 0.0F);
      }
   }

   private void handleAmounts(LatheLoft latheLoft, int index, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float tailEarValue = shinProgressTable[index];
      float tailEarValue2 = LEG_OFFSETS_Z[index];
      float eyeValue = SHIN_HEIGHTS[index];
      float[] floats = new float[]{
         tailEarValue,
         eyeValue + 0.25F,
         tailEarValue2 - 0.1F,
         tailEarValue,
         eyeValue - 0.9F,
         tailEarValue2 + 0.17F,
         tailEarValue,
         eyeValue - 1.8F,
         tailEarValue2 + 0.4F,
         tailEarValue,
         eyeValue - 3.0F,
         tailEarValue2 + 0.14F,
         tailEarValue,
         0.9F,
         tailEarValue2 + 0.04F
      };
      latheLoft.getLoftPath(floats, new float[]{0.68F, 0.8F, 0.76F, 0.6F, 0.46F}, new float[]{0.7F, 0.9F, 1.1F, 0.74F, 0.49F}, 8, shinBrr, null, null, 0.0F, 0.0F, -1.0F);
      latheLoft.getLatheLoft(tailEarValue, eyeValue, tailEarValue2 - 0.1F, 0.64F, 0.66F, 0.66F, 8, shinBrr2);
   }

   private void handle(LatheLoft latheLoft, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4, ShinBrr shinBrr5, ShinBrr shinBrr6, ShinBrr shinBrr7) {
      latheLoft.getLatheLoft(0.0F, 13.9F, getAmount(), 1.5F, 0.95F, 1.15F, 10, shinBrr);
      latheLoft.getLatheLoft(0.0F, 15.5F, getAmount4(0.0F, 15.5F) + 0.2F, 0.55F, 0.95F, 0.45F, 8, shinBrr2);

      for (int i = -1; i <= 1; i += 2) {
         float tailEarValue = i * 1.6F;
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(tailEarValue, 17.1F, getAmount4(tailEarValue, 17.1F) + 0.15F).getLatheLoft4(getAmount(tailEarValue, 17.1F));
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.95F, 0.2F, 0.28F, 8, shinBrr3);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoft(0.0F, 13.15F, getAmount4(0.0F, 13.15F) + 0.3F, 0.62F, 0.42F, 0.35F, 8, shinBrr7);

      for (int j = -1; j <= 1; j += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(j * 0.38F, 13.2F, getAmount() + 0.35F);
         latheLoft.getLatheLoft(0.01F, 0.11F, -0.5F, 0.0F, 6, shinBrr6, null);
         latheLoft.getDequeArray();
      }

      float[] floats = new float[2];

      for (int k = -1; k <= 1; k += 2) {
         float tailEarValue2 = k * 0.72F;
         float tailEarValue3 = getAmount3(tailEarValue2, 14.15F) + 0.25F;
         floats[(k + 1) / 2] = tailEarValue3;
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(tailEarValue2, 14.15F, tailEarValue3).getLatheLoft4(k * -14.0F);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 0.92F, 0.6F, 0.6F, 8, shinBrr4);
         latheLoft.getDequeArray();
      }

      float armTrunkValue = getAmount4(0.0F, 15.0F) - 0.8F;
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 15.0F, armTrunkValue).getLatheLoftLatheLoftAmountLoft(30.0F).getLatheLoft3(0.85F);
      latheLoft.getPolygonExtrude(
         new float[]{0.0F, -0.48F, 0.3F, -0.24F, 0.6F, 0.18F, 0.5F, 0.43F, 0.0F, 0.48F, -0.5F, 0.43F, -0.6F, 0.18F, -0.3F, -0.24F}, 0.0F, 0.55F, shinBrr5, shinBrr5, shinBrr5
      );
      latheLoft.getDequeArray();

      for (int m = -1; m <= 1; m += 2) {
         float tailEarValue5 = floats[(m + 1) / 2];

         for (int p = 0; p < 3; p++) {
            float angle = (float)Math.toRadians(-20.0F + p * 12.0F);
            float tailEarValue6 = m * 1.27F;
            float tailEarValue7 = 14.25F + (p - 1.0F) * 0.18F;
            float tailEarValue8 = tailEarValue5 - 0.25F;
            float tailEarFactor = m * (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float tailEarFactor2 = 0.4F;
            float distance = (float)Math.sqrt(tailEarFactor * tailEarFactor + sin * sin + tailEarFactor2 * tailEarFactor2);
            float tailEarFactor3 = 4.0F - Math.abs(p - 1.0F) * 0.45F;
            latheLoft.getLatheLoft(
               new float[]{tailEarValue6, tailEarValue7, tailEarValue8, tailEarValue6 + tailEarFactor / distance * tailEarFactor3, tailEarValue7 + sin / distance * tailEarFactor3, tailEarValue8 + tailEarFactor2 / distance * tailEarFactor3},
               new float[]{0.09F, 0.04F},
               4,
               shinBrr6,
               false
            );
         }
      }
   }

   private static float getAmount() {
      return getAmount4(0.0F, 13.9F) + 0.05F;
   }

   @Override
   public String formatTungDinString() {
      return "Trulimero Trulicina";
   }

   @Override
   public float getAmountFloat() {
      return 1.3F;
   }

   private static float getAmounts(float tailEarLimit) {
      float tailEarFactor = 0.925F;
      int tailEarSize = shinFootTable.length;
      float[] floats = new float[tailEarSize];

      for (int i = 1; i < tailEarSize; i++) {
         float tailEarFactor2 = (tailEarTable[i] - tailEarTable[i - 1]) * tailEarFactor;
         float tailEarFactor3 = shinFootTable[i] - shinFootTable[i - 1];
         floats[i] = floats[i - 1] + (float)Math.sqrt(tailEarFactor2 * tailEarFactor2 + tailEarFactor3 * tailEarFactor3);
      }

      float tailEarValue = tailEarLimit >= shinFootTable[tailEarSize - 1] ? floats[tailEarSize - 1] : 0.0F;
      if (tailEarLimit > shinFootTable[0] && tailEarLimit < shinFootTable[tailEarSize - 1]) {
         for (int j = 0; j + 1 < tailEarSize; j++) {
            if (tailEarLimit <= shinFootTable[j + 1]) {
               float tailEarFactor4 = (tailEarLimit - shinFootTable[j]) / (shinFootTable[j + 1] - shinFootTable[j]);
               tailEarValue = floats[j] + (floats[j + 1] - floats[j]) * tailEarFactor4;
               break;
            }
         }
      }

      return 1.0F - tailEarValue / floats[tailEarSize - 1];
   }

   private static void handleBonecaAmbalabu5(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15590357)).getBonecaAmbalabu(0.04F, 3.0F, 71);
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.65F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(14734014, 0.7F));

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 3; j++) {
            float bonecaAmbalabuValue = 0.12F + j * 0.06F + i * 0.015F;
            float amountForFloat = bonecaAmbalabu.bonecaValue * (0.28F + i * 0.075F);
            bonecaAmbalabu.getClass022(
               bonecaAmbalabu.ambalabuBonecaValue * (0.5F - bonecaAmbalabuValue),
               amountForFloat,
               bonecaAmbalabu.ambalabuBonecaValue / 40.0F,
               bonecaAmbalabu.ambalabuBonecaValue / 40.0F,
               ForeShinUtils.getValueForIntFloat(10387044, 0.9F)
            );
            bonecaAmbalabu.getClass022(
               bonecaAmbalabu.ambalabuBonecaValue * (0.5F + bonecaAmbalabuValue),
               amountForFloat,
               bonecaAmbalabu.ambalabuBonecaValue / 40.0F,
               bonecaAmbalabu.ambalabuBonecaValue / 40.0F,
               ForeShinUtils.getValueForIntFloat(10387044, 0.9F)
            );
         }
      }
   }

   private void handleAmountsForLatheLoftInt(LatheLoft latheLoft, int index, ShinBrr shinBrr, ShinBrr shinBrr2) {
      float signum = Math.signum(shinProgressTable[index]);
      float tailEarValue = shinProgressTable[index] + signum * 0.06F;
      float tailEarValue2 = LEG_OFFSETS_Z[index];
      latheLoft.getLoftPath(
         new float[]{tailEarValue, 0.4F, tailEarValue2 + 0.98F, tailEarValue, 0.5F, tailEarValue2 + 0.66F, tailEarValue, 0.74F, tailEarValue2 - 0.2F, tailEarValue, 0.52F, tailEarValue2 - 1.4F, tailEarValue, 0.33F, tailEarValue2 - 2.1F},
         new float[]{0.24F, 0.46F, 0.56F, 0.62F, 0.6F},
         new float[]{0.26F, 0.48F, 0.7F, 0.5F, 0.3F},
         8,
         shinBrr,
         shinBrr,
         shinBrr,
         0.0F,
         1.0F,
         0.0F
      );

      for (int j = 0; j < 5; j++) {
         float angle = (float)Math.toRadians(signum * (j - 2.0F) * 4.0F);
         float tailEarValue3 = j == 0 ? 0.34F : 0.24F - 0.02F * j;
         float tailEarFactor = j == 0 ? 0.65F : 0.56F - 0.06F * j;
         float tailEarValue4 = tailEarValue - signum * (0.44F - j * 0.235F);
         float tailEarValue5 = tailEarValue2 - 2.05F + Math.abs(j - 1.0F) * 0.06F;
         float tailEarValue6 = tailEarValue4 + (float)Math.sin(angle) * tailEarFactor;
         float tailEarValue7 = tailEarValue5 - (float)Math.cos(angle) * tailEarFactor;
         float tailEarValue8 = tailEarValue3 * 0.9F + 0.01F;
         latheLoft.getLoftPath(
            new float[]{tailEarValue4, tailEarValue8, tailEarValue5, tailEarValue6, tailEarValue8 * 0.85F, tailEarValue7},
            new float[]{tailEarValue3 * 0.95F, tailEarValue3 * 0.55F},
            new float[]{tailEarValue3 * 0.9F, tailEarValue3 * 0.5F},
            5,
            shinBrr2,
            null,
            null,
            0.0F,
            1.0F,
            0.0F
         );
      }
   }

   private static float getAmountsForFloat(float tailEarValue) {
      float hashRegistryValue = (tailEarValue - -11.0F) / 0.05F;
      int index = Math.max(0, Math.min(TAIL_SAMPLES.length - 2, (int)Math.floor(hashRegistryValue)));
      float tailEarFactor = Math.max(0.0F, Math.min(1.0F, hashRegistryValue - index));
      return TAIL_SAMPLES[index] + (TAIL_SAMPLES[index + 1] - TAIL_SAMPLES[index]) * tailEarFactor;
   }

   private static float getAmount2(float tailEarLimit) {
      return tailEarLimit < 0.0F ? 0.0F : (tailEarLimit > 1.0F ? 1.0F : tailEarLimit);
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float bonecaAmbalabuValue2) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float bonecaAmbalabuValue3 = (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
            float bonecaAmbalabuValue4 = bonecaAmbalabuValue + (bonecaAmbalabuValue2 - bonecaAmbalabuValue) * (i + 0.5F) / bonecaAmbalabu.bonecaValue;
            float cos = (float)Math.cos((float) (Math.PI * 2) * (bonecaAmbalabuValue3 - 0.5F));
            float bonecaAmbalabuTotal = 0.0F;

            for (int k = 0; k < 4; k++) {
               float tailEarValue = (j + 0.25F + 0.5F * (k & 1)) / bonecaAmbalabu.ambalabuBonecaValue;
               float bonecaAmbalabuValue6 = bonecaAmbalabuValue + (bonecaAmbalabuValue2 - bonecaAmbalabuValue) * (i + 0.25F + 0.5F * (k >> 1)) / bonecaAmbalabu.bonecaValue;
               bonecaAmbalabuTotal += getAmountForFloat(tailEarValue, bonecaAmbalabuValue6);
            }

            int foreShinValue = ForeShinUtils.getValue(getValue(cos), bonecaAmbalabuTotal * 0.25F);
            float bonecaAmbalabuLimit = bonecaAmbalabuValue4 - getAmount(cos);
            int bonecaAmbalabuValue8;
            if (bonecaAmbalabuLimit < 0.0F) {
               int valueForInt = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13208138), ForeShinUtils.getValueForInt(16181718), getAmount3((-cos - 0.35F) / 0.4F));
               int bonecaAmbalabuValue10 = ForeShinUtils.getValue2(foreShinValue, valueForInt, 0.85F);
               bonecaAmbalabuValue8 = ForeShinUtils.getValue2(bonecaAmbalabuValue10, ForeShinUtils.getValueForInt(14921330), 0.6F * (1.0F - getAmount3(-bonecaAmbalabuLimit / 0.2F)));
            } else {
               bonecaAmbalabuValue8 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(5134432), 0.7F * (1.0F - getAmount3(bonecaAmbalabuLimit / 0.35F)));
            }

            int bonecaAmbalabuValue11 = ForeShinUtils.getValue2(bonecaAmbalabuValue8, ForeShinUtils.getValueForInt(7239808), 0.8F * (1.0F - getAmount3(Math.abs(bonecaAmbalabuLimit) / 0.07F)));
            if (bonecaAmbalabuLimit > 0.2F) {
               float bonecaAmbalabuValue12 = 1.0F - getAmount3(Math.abs(Math.abs(cos) * Math.signum(cos) - 0.4F) / 0.035F);
               float bonecaAmbalabuFactor = getAmountsForFloat(bonecaAmbalabuValue4) % 1.0F < 0.55F ? 1.0F : 0.3F;
               bonecaAmbalabuValue11 = ForeShinUtils.getValue2(bonecaAmbalabuValue11, ForeShinUtils.getValueForInt(8161940), 0.8F * bonecaAmbalabuValue12 * bonecaAmbalabuFactor);
            }

            bonecaAmbalabu.getBonecaAmbalabu(j, i, bonecaAmbalabuValue11);
         }
      }

      if (bonecaAmbalabuValue < -9.0F) {
         float bonecaAmbalabuFactor2 = bonecaAmbalabu.bonecaValue / (bonecaAmbalabuValue2 - bonecaAmbalabuValue);

         for (int m = 0; m < 900; m++) {
            float bonecaAmbalabuFactor3 = getAmount(m, 1, 131);
            float tailEarFactor = (float)Math.cos((float) (Math.PI * 2) * (bonecaAmbalabuFactor3 - 0.5F));
            float bonecaAmbalabuValue13 = bonecaAmbalabuValue + getAmount(m, 2, 131) * (getAmount(tailEarFactor) - 0.15F - bonecaAmbalabuValue);
            float classBound = bonecaAmbalabuFactor3 * bonecaAmbalabu.ambalabuBonecaValue;
            float amountForFloat = (bonecaAmbalabuValue13 - bonecaAmbalabuValue) * bonecaAmbalabuFactor2;
            bonecaAmbalabu.getClass02(
               classBound,
               amountForFloat,
               classBound + (getAmount(m, 3, 131) - 0.5F) * 4.0F,
               amountForFloat + bonecaAmbalabu.bonecaValue / 40.0F,
               1.3F,
               ForeShinUtils.getValueForIntFloat(getAmount(m, 4, 131) < 0.5F ? 0 : 16773596, 0.16F)
            );
         }

         for (int p = -3; p <= 3; p++) {
            float bonecaAmbalabuFactor4 = 0.5F + p * 0.07F;
            float cos3 = (float)Math.cos((float) (Math.PI * 2) * (bonecaAmbalabuFactor4 - 0.5F));
            float bonecaAmbalabuValue16 = (getAmount(cos3) - 0.3F - bonecaAmbalabuValue) * bonecaAmbalabuFactor2;
            float classFactor = bonecaAmbalabuFactor4 * bonecaAmbalabu.ambalabuBonecaValue;
            bonecaAmbalabu.getClassForFloatInt(
               classFactor,
               0.0F,
               classFactor + p * 2.0F,
               bonecaAmbalabuValue16 * 0.5F,
               classFactor + p * 5.0F,
               bonecaAmbalabuValue16,
               bonecaAmbalabu.ambalabuBonecaValue / 110.0F,
               ForeShinUtils.getValueForIntFloat(7029798, 0.6F)
            );
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.025F, 3.0F, 17);
   }

   private static float getAmount3(float tailEarFactor) {
      tailEarFactor = getAmount2(tailEarFactor);
      return tailEarFactor * tailEarFactor * (3.0F - 2.0F * tailEarFactor);
   }

   private static float getBonecaAmbalabuForBonecaAmbalabuFloat(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue, float tailEarValue) {
      return bonecaAmbalabu.ambalabuBonecaValue * LatheLoft.getAmount(bonecaAmbalabuValue, getAmountsShinFootTable(tailEarValue) * 1.0F);
   }

   private static float getAmountsShinFootTable(float tailEarValue) {
      if (tailEarValue <= shinFootTable[0]) {
         return tailEarTable[0];
      } else {
         for (int i = 0; i + 1 < shinFootTable.length; i++) {
            if (tailEarValue <= shinFootTable[i + 1]) {
               float tailEarFactor = (tailEarValue - shinFootTable[i]) / (shinFootTable[i + 1] - shinFootTable[i]);
               return tailEarTable[i] + (tailEarTable[i + 1] - tailEarTable[i]) * tailEarFactor;
            }
         }

         return tailEarTable[tailEarTable.length - 1];
      }
   }

   private static void handleBonecaAmbalabu6(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16181718)).getBonecaAmbalabu(0.04F, 2.5F, 79);

      for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue;
         float bonecaAmbalabuValue2 = 1.0F - getAmount3(Math.abs(bonecaAmbalabuValue - 0.25F) / 0.2F);
         bonecaAmbalabu.getClassBonecaAmbalabu(i, 0.0F, i + 1, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(15258812, 0.6F * bonecaAmbalabuValue2));
      }

      for (int j = 0; j < 120; j++) {
         float classBound = getAmount(j, 1, 81) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(j, 2, 81) * bonecaAmbalabu.bonecaValue;
         bonecaAmbalabu.getClass02(
            classBound, amountForFloat, classBound - bonecaAmbalabu.ambalabuBonecaValue / 30.0F, amountForFloat + (getAmount(j, 3, 81) - 0.5F) * 3.0F, 1.0F, ForeShinUtils.getValueForIntFloat(8020048, 0.12F)
         );
      }
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue) {
      float hashRegistryValue = 0.5F - bonecaAmbalabuValue / 360.0F;
      return bonecaAmbalabu.ambalabuBonecaValue * (hashRegistryValue - (float)Math.floor(hashRegistryValue));
   }

   private static float[] getAmounts() {
      short tailEarSize = 640;
      float[] floats = new float[tailEarSize];
      float tailEarTotal = 0.0F;

      for (int i = 0; i < tailEarSize; i++) {
         floats[i] = tailEarTotal;
         float tailEarValue = -11.0F + i * 0.05F;
         tailEarTotal += 0.05F / (0.45F + 0.25F * getAmounts(finTable, tailEarValue));
      }

      return floats;
   }

   @Override
   public String formatNoteBlock() {
      return "entity.cat.ambient";
   }

   private static void handleBonecaAmbalabu7(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15853788)).getBonecaAmbalabu(0.035F, 3.0F, 73);

      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, ForeShinUtils.getValueForIntFloat(14865344, 0.55F * getAmount3((bonecaAmbalabuValue - 0.55F) / 0.4F)));
      }

      for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
         float absScaled = Math.abs((j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 2.0F;
         bonecaAmbalabu.getClassBonecaAmbalabu(j, 0.0F, j + 1, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(14921330, 0.45F * getAmount3((absScaled - 0.45F) / 0.35F)));
      }

      for (int k = 0; k < 260; k++) {
         float classBound = getAmount(k, 1, 75) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(k, 2, 75) * bonecaAmbalabu.bonecaValue;
         float bonecaAmbalabuFactor = classBound < bonecaAmbalabu.ambalabuBonecaValue * 0.5F ? -1.0F : 1.0F;
         bonecaAmbalabu.getClass02(
            classBound,
            amountForFloat,
            classBound + bonecaAmbalabuFactor * bonecaAmbalabu.ambalabuBonecaValue / 40.0F,
            amountForFloat + bonecaAmbalabu.bonecaValue / 30.0F,
            1.1F,
            ForeShinUtils.getValueForIntFloat(getAmount(k, 3, 75) < 0.5F ? 8020048 : 16777215, 0.14F)
         );
      }

      bonecaAmbalabu.getClass02(
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         0.0F,
         bonecaAmbalabu.ambalabuBonecaValue * 0.5F,
         bonecaAmbalabu.bonecaValue * 0.42F,
         bonecaAmbalabu.ambalabuBonecaValue / 60.0F,
         ForeShinUtils.getValueForIntFloat(9067076, 0.7F)
      );
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float[] bonecaAmbalabuValues, float[] standCellKeys, boolean bonecaAmbalabuFlag) {
      float[] amounts3 = getAmounts(bonecaAmbalabuValues, standCellKeys);
      float bonecaAmbalabuBound = Float.MAX_VALUE;
      float maxValue2 = -Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;
      float maxValue4 = -Float.MAX_VALUE;

      for (int i = 0; i < amounts3.length; i += 2) {
         bonecaAmbalabuBound = Math.min(bonecaAmbalabuBound, amounts3[i]);
         maxValue2 = Math.max(maxValue2, amounts3[i]);
         maxValue = Math.min(maxValue, amounts3[i + 1]);
         maxValue4 = Math.max(maxValue4, amounts3[i + 1]);
      }

      float[] floats = new float[]{bonecaAmbalabuBound, maxValue2, maxValue, maxValue4};
      int bonecaAmbalabuLimit = standCellKeys.length / 2;
      float bonecaAmbalabuValue = 0.5F * (bonecaAmbalabuValues[0] + bonecaAmbalabuValues[2]);
      float bonecaAmbalabuValue2 = 0.5F * (bonecaAmbalabuValues[1] + bonecaAmbalabuValues[3]);
      float[] tailEarValues = new float[2 * (2 * bonecaAmbalabuLimit + 1)];
      int bonecaAmbalabuValue3 = getValue(tailEarValues, 0, bonecaAmbalabu, floats, bonecaAmbalabuFlag, bonecaAmbalabuValues[0], bonecaAmbalabuValues[1]);

      for (int j = 0; j < bonecaAmbalabuLimit; j++) {
         float tailEarValue = standCellKeys[2 * j];
         float bonecaAmbalabuValue5 = standCellKeys[2 * j + 1];
         bonecaAmbalabuValue3 = getValue(tailEarValues, bonecaAmbalabuValue3, bonecaAmbalabu, floats, bonecaAmbalabuFlag, tailEarValue, bonecaAmbalabuValue5);
         if (j + 1 < bonecaAmbalabuLimit) {
            float bonecaAmbalabuValue6 = 0.5F * (tailEarValue + standCellKeys[2 * j + 2]);
            float bonecaAmbalabuValue7 = 0.5F * (bonecaAmbalabuValue5 + standCellKeys[2 * j + 3]);
            bonecaAmbalabuValue3 = getValue(tailEarValues, bonecaAmbalabuValue3, bonecaAmbalabu, floats, bonecaAmbalabuFlag, bonecaAmbalabuValue6 + (bonecaAmbalabuValue - bonecaAmbalabuValue6) * 0.1F, bonecaAmbalabuValue7 + (bonecaAmbalabuValue2 - bonecaAmbalabuValue7) * 0.1F);
         }
      }

      getValue(tailEarValues, bonecaAmbalabuValue3, bonecaAmbalabu, floats, bonecaAmbalabuFlag, bonecaAmbalabuValues[2], bonecaAmbalabuValues[3]);
      bonecaAmbalabu.getBonecaAmbalabu(0);
      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(14703150), tailEarValues);
      float pilotStatusBound = 0.001F;

      for (int k = 0; k < bonecaAmbalabuLimit; k++) {
         pilotStatusBound = Math.max(pilotStatusBound, (float)Math.sqrt(getAmount(standCellKeys[2 * k], standCellKeys[2 * k + 1], bonecaAmbalabuValues[0], bonecaAmbalabuValues[1], bonecaAmbalabuValues[2], bonecaAmbalabuValues[3])));
      }

      for (int m = 0; m < bonecaAmbalabu.bonecaValue; m++) {
         for (int p = 0; p < bonecaAmbalabu.ambalabuBonecaValue; p++) {
            int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(p, m);
            if (ForeShinUtils.getValue6(bonecaAmbalabuForInt) >= 8) {
               float bonecaAmbalabuValue8 = bonecaAmbalabuFlag
                  ? bonecaAmbalabuBound + (p + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue * (maxValue2 - bonecaAmbalabuBound)
                  : maxValue2 - (p + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue * (maxValue2 - bonecaAmbalabuBound);
               float bonecaAmbalabuValue9 = maxValue4 - (m + 0.5F) / bonecaAmbalabu.bonecaValue * (maxValue4 - maxValue);
               float hashHuntBound = (float)Math.sqrt(getAmount(bonecaAmbalabuValue8, bonecaAmbalabuValue9, bonecaAmbalabuValues[0], bonecaAmbalabuValues[1], bonecaAmbalabuValues[2], bonecaAmbalabuValues[3])) / pilotStatusBound;
               int value2 = ForeShinUtils.getValue2(
                  ForeShinUtils.getValueForInt(14703150), ForeShinUtils.getValueForInt(16294514), (float)Math.pow(Math.min(1.0F, hashHuntBound), 1.6)
               );
               bonecaAmbalabu.getBonecaAmbalabu(p, m, bonecaAmbalabuForInt & 0xFF000000 | value2 & 16777215);
            }
         }
      }

      float classValue = Math.max(1.3F, Math.min(bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue) / 48.0F);

      for (int q = 0; q < bonecaAmbalabuLimit; q++) {
         float bonecaAmbalabuFactor = bonecaAmbalabuLimit == 1 ? 0.5F : (float)q / (bonecaAmbalabuLimit - 1);
         float bonecaAmbalabuValue12 = bonecaAmbalabuValues[0] + (bonecaAmbalabuValues[2] - bonecaAmbalabuValues[0]) * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue13 = bonecaAmbalabuValues[1] + (bonecaAmbalabuValues[3] - bonecaAmbalabuValues[1]) * bonecaAmbalabuFactor;
         float bonecaAmbalabuValue14 = standCellKeys[2 * q];
         float bonecaAmbalabuValue15 = standCellKeys[2 * q + 1];
         float bonecaAmbalabuValue16 = 0.5F * (bonecaAmbalabuValue12 + bonecaAmbalabuValue14);
         float bonecaAmbalabuValue17 = 0.5F * (bonecaAmbalabuValue13 + bonecaAmbalabuValue15);
         bonecaAmbalabu.getClass02(
            getBonecaAmbalabu(bonecaAmbalabu, floats, bonecaAmbalabuFlag, bonecaAmbalabuValue12),
            getAmount(bonecaAmbalabu, floats, bonecaAmbalabuValue13),
            getBonecaAmbalabu(bonecaAmbalabu, floats, bonecaAmbalabuFlag, bonecaAmbalabuValue16),
            getAmount(bonecaAmbalabu, floats, bonecaAmbalabuValue17),
            classValue * 1.5F,
            ForeShinUtils.getValueForIntFloat(11024924, 0.95F)
         );
         bonecaAmbalabu.getClass02(
            getBonecaAmbalabu(bonecaAmbalabu, floats, bonecaAmbalabuFlag, bonecaAmbalabuValue16),
            getAmount(bonecaAmbalabu, floats, bonecaAmbalabuValue17),
            getBonecaAmbalabu(bonecaAmbalabu, floats, bonecaAmbalabuFlag, bonecaAmbalabuValue14),
            getAmount(bonecaAmbalabu, floats, bonecaAmbalabuValue15),
            classValue,
            ForeShinUtils.getValueForIntFloat(11024924, 0.9F)
         );
      }

      for (int r = 0; r < bonecaAmbalabu.bonecaValue; r++) {
         for (int s = 0; s < bonecaAmbalabu.ambalabuBonecaValue; s++) {
            int foreShinValue = bonecaAmbalabu.getBonecaAmbalabuForInt(s, r);
            bonecaAmbalabu.getBonecaAmbalabu(s, r, ForeShinUtils.getValue6(foreShinValue) < 120 ? 0 : foreShinValue | 0xFF000000);
         }
      }
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, int bonecaAmbalabuLimit, float bonecaAmbalabuFactor, float bonecaAmbalabuValue, int foreShinValue, float bonecaAmbalabuFactor2, int bonecaAmbalabuValue3) {
      for (int i = 0; i < bonecaAmbalabuLimit; i++) {
         float classBound = getAmount(i, 1, bonecaAmbalabuValue3) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(i, 2, bonecaAmbalabuValue3) * bonecaAmbalabu.bonecaValue;
         float planValuesItem = classBound / bonecaAmbalabu.ambalabuBonecaValue - 0.5F;
         float bonecaAmbalabuValue7 = amountForFloat / bonecaAmbalabu.bonecaValue;
         float min8 = Math.min(1.0F, Math.abs(planValuesItem) * 2.0F);
         float bonecaAmbalabuValue9 = planValuesItem < 0.0F ? -90.0F - 180.0F * min8 : -90.0F + 180.0F * min8;
         float bonecaAmbalabuFactor3 = getAmount3((bonecaAmbalabuValue7 - 0.45F) / 0.25F) * (1.0F - getAmount3((Math.abs(planValuesItem) - 0.22F) / 0.1F));
         float bonecaAmbalabuValue10 = planValuesItem < 0.0F ? 160.0F : 20.0F;
         float degrees = bonecaAmbalabuValue9 + (bonecaAmbalabuValue10 - bonecaAmbalabuValue9) * bonecaAmbalabuFactor3 + (getAmount(i, 3, bonecaAmbalabuValue3) - 0.5F) * 50.0F;
         float angle = (float)Math.toRadians(degrees);
         float bonecaAmbalabuFactor4 = bonecaAmbalabuFactor * (0.6F + 0.8F * getAmount(i, 4, bonecaAmbalabuValue3));
         int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt((int)classBound, (int)amountForFloat);
         float bonecaAmbalabuLimit2 = (ForeShinUtils.getValueInt(bonecaAmbalabuForInt) + ForeShinUtils.getValue4(bonecaAmbalabuForInt) + ForeShinUtils.getValue5(bonecaAmbalabuForInt)) / 765.0F;
         float bonecaAmbalabuFactor5 = foreShinValue == 0 && bonecaAmbalabuLimit2 > 0.75F ? 0.12F : 1.0F;
         bonecaAmbalabu.getClass02(
            classBound,
            amountForFloat,
            classBound + (float)Math.cos(angle) * bonecaAmbalabuFactor4,
            amountForFloat + (float)Math.sin(angle) * bonecaAmbalabuFactor4,
            bonecaAmbalabuValue,
            ForeShinUtils.getValueForIntFloat(foreShinValue, bonecaAmbalabuFactor5 * bonecaAmbalabuFactor2 * (0.5F + 0.5F * getAmount(i, 5, bonecaAmbalabuValue3)))
         );
      }
   }

   private static void handleBonecaAmbalabu8(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;
         int foreShinValue = ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(11103798), ForeShinUtils.getValueForInt(13208138), getAmount3(bonecaAmbalabuValue / 0.25F));
         int bonecaAmbalabuValue3 = ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(8014372), getAmount3((bonecaAmbalabuValue - 0.25F) / 0.15F));
         int classValue = ForeShinUtils.getValue2(bonecaAmbalabuValue3, ForeShinUtils.getValueForInt(1839628), getAmount3((bonecaAmbalabuValue - 0.4F) / 0.2F));
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, i, bonecaAmbalabu.ambalabuBonecaValue, i + 1, classValue);
      }

      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForIntFloat(7029798, 0.2F), 5.0F, 1.5F, 0.0F);
      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 2.0F, 99);
   }

   private void handleAmounts(LatheLoft latheLoft, float tailEarValue, ShinBrr shinBrr, ShinBrr shinBrr2) {
      latheLoft.getDequeArrayArrayDeque()
         .getLatheLoft(tailEarValue * 2.9F, 14.7F, -6.3F)
         .getLatheLoft4(-90.0F + tailEarValue * 12.0F)
         .getLatheLoft(-16.0F)
         .getLatheLoftLatheLoftAmountLoft(-tailEarValue * 26.0F)
         .getLatheLoft3(1.1F);
      latheLoft.getPolygonExtrude(getAmounts(SPIKE_ANCHOR_POINTS, SPIKE_OUTLINE_POINTS), -0.07F, 0.07F, shinBrr, shinBrr2, null);
      latheLoft.getDequeArray();
   }

   private static void handleBonecaAmbalabu9(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuFactor = bonecaAmbalabu.ambalabuBonecaValue / 320.0F;
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13208138))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11103798), 0.4F, 7.0F * bonecaAmbalabuFactor, 11)
         .getBonecaAmbalabu(0.05F, 2.5F * bonecaAmbalabuFactor, 5);

      for (int i = -1; i <= 1; i += 2) {
         bonecaAmbalabu.getClass022(
            getBonecaAmbalabu(bonecaAmbalabu, i * 62.0F),
            getAmount(bonecaAmbalabu, 14.4F),
            bonecaAmbalabu.ambalabuBonecaValue * 0.1F,
            (getAmount(bonecaAmbalabu, 12.9F) - getAmount(bonecaAmbalabu, 16.0F)) * 0.5F,
            ForeShinUtils.getValueForIntFloat(14921330, 0.55F)
         );
      }

      for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
         float absScaled = Math.abs(0.5F - (j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue) * 360.0F;
         float bonecaAmbalabuValue = 0.6F * (1.0F - getAmount3((absScaled - 72.0F) / 22.0F));
         float bonecaAmbalabuValue2 = 1.0F - getAmount3((absScaled - 52.0F) / 24.0F);

         for (int k = 0; k < 4; k++) {
            bonecaAmbalabu.getClassBonecaAmbalabu(j, getAmount(bonecaAmbalabu, 13.25F - 0.1F * k), j + 1, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(16181718, bonecaAmbalabuValue * 0.25F));
            bonecaAmbalabu.getClassBonecaAmbalabu(j, getAmount(bonecaAmbalabu, 12.9F - 0.1F * k), j + 1, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(16181718, bonecaAmbalabuValue2 * 0.25F));
         }
      }

      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForIntFloat(15853788, 0.5F), getAmounts(bonecaAmbalabu, 0.0F, 13.85F, 2.45F, 1.55F, 0.0F, 40));

      for (int m = -1; m <= 1; m += 2) {
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(15853788), getAmounts(bonecaAmbalabu, m * 0.85F, 14.2F, 1.3F, 0.95F, m * 8.0F, 32));
      }

      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(16181718), getAmounts(bonecaAmbalabu, 0.0F, 13.15F, 1.5F, 0.9F, 0.0F, 32));
      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForIntFloat(15853788, 0.85F), getAmounts(bonecaAmbalabu, 0.0F, 15.2F, 0.42F, 0.45F, 0.0F, 20));
      handleBonecaAmbalabu(bonecaAmbalabu, 5600, 9.0F * bonecaAmbalabuFactor, 1.3F * bonecaAmbalabuFactor, 0, 0.13F, 101);
      handleBonecaAmbalabu(bonecaAmbalabu, 5600, 8.0F * bonecaAmbalabuFactor, 1.2F * bonecaAmbalabuFactor, 16773596, 0.18F, 103);
      int valueForIntFloat = ForeShinUtils.getValueForIntFloat(7029798, 0.88F);
      float bonecaAmbalabuValue4 = 3.4F * bonecaAmbalabuFactor;
      handle(bonecaAmbalabu, bonecaAmbalabuValue4, valueForIntFloat, -1.5F, 17.35F, -1.05F, 18.4F, -0.45F, 17.75F, 0.0F, 18.05F, 0.45F, 17.75F, 1.05F, 18.4F, 1.5F, 17.35F);
      bonecaAmbalabu.getClass02(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, getAmount(bonecaAmbalabu, 18.0F), bonecaAmbalabu.ambalabuBonecaValue * 0.5F, getAmount(bonecaAmbalabu, 19.0F), 3.2F * bonecaAmbalabuFactor, valueForIntFloat);

      for (int p = -1; p <= 1; p += 2) {
         float classFactor = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, p * 0.62F, 18.3F);
         bonecaAmbalabu.getClassForFloatInt(
            classFactor,
            getAmount(bonecaAmbalabu, 18.3F),
            classFactor + p * 1.5F * bonecaAmbalabuFactor,
            getAmount(bonecaAmbalabu, 18.7F),
            getBonecaAmbalabu(bonecaAmbalabu, p * 14.0F),
            getAmount(bonecaAmbalabu, 19.0F),
            2.8F * bonecaAmbalabuFactor,
            valueForIntFloat
         );
         float bonecaAmbalabu3 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, p * 1.35F, 18.15F);
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu3,
            getAmount(bonecaAmbalabu, 18.15F),
            bonecaAmbalabu3 + p * 2.0F * bonecaAmbalabuFactor,
            getAmount(bonecaAmbalabu, 18.7F),
            getBonecaAmbalabu(bonecaAmbalabu, p * 34.0F),
            getAmount(bonecaAmbalabu, 19.3F),
            2.6F * bonecaAmbalabuFactor,
            valueForIntFloat
         );
      }

      int valueForIntFloat5 = ForeShinUtils.getValueForIntFloat(7029798, 0.55F);

      for (int q = -2; q <= 2; q++) {
         float bonecaAmbalabu4 = getBonecaAmbalabu(bonecaAmbalabu, 180.0F + q * 17.0F);
         float bonecaAmbalabuValue6 = -q / 2.0F * 12.0F * bonecaAmbalabuFactor;

         for (int r = -1; r <= 1; r++) {
            float bonecaAmbalabuValue7 = bonecaAmbalabu4 + r * bonecaAmbalabu.ambalabuBonecaValue;
            bonecaAmbalabu.getClassForFloatInt(bonecaAmbalabuValue7, getAmount(bonecaAmbalabu, 19.0F), bonecaAmbalabuValue7 + bonecaAmbalabuValue6 * 0.25F, getAmount(bonecaAmbalabu, 16.8F), bonecaAmbalabuValue7 + bonecaAmbalabuValue6, getAmount(bonecaAmbalabu, 13.6F), 3.5F * bonecaAmbalabuFactor, valueForIntFloat5);
         }
      }

      for (int s = -1; s <= 1; s += 2) {
         for (int t = 0; t < 2; t++) {
            float bonecaAmbalabu5 = getBonecaAmbalabu(bonecaAmbalabu, s * (104.0F + t * 24.0F));
            bonecaAmbalabu.getClassForFloatInt(
               bonecaAmbalabu5,
               getAmount(bonecaAmbalabu, 19.0F),
               bonecaAmbalabu5 + s * 2.0F * bonecaAmbalabuFactor,
               getAmount(bonecaAmbalabu, 16.6F),
               bonecaAmbalabu5 - s * 9.0F * bonecaAmbalabuFactor,
               getAmount(bonecaAmbalabu, 14.4F),
               3.5F * bonecaAmbalabuFactor,
               valueForIntFloat5
            );
         }

         float bonecaAmbalabu6 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, s * 2.6299999F, 16.300001F);
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu6,
            getAmount(bonecaAmbalabu, 16.300001F),
            getBonecaAmbalabu(bonecaAmbalabu, s * 70.0F),
            getAmount(bonecaAmbalabu, 16.1F),
            getBonecaAmbalabu(bonecaAmbalabu, s * 92.0F),
            getAmount(bonecaAmbalabu, 15.6F),
            3.6F * bonecaAmbalabuFactor,
            valueForIntFloat
         );
         float bonecaAmbalabu7 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, s * 2.35F, 15.1F);
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu7,
            getAmount(bonecaAmbalabu, 15.1F),
            getBonecaAmbalabu(bonecaAmbalabu, s * 74.0F),
            getAmount(bonecaAmbalabu, 15.15F),
            getBonecaAmbalabu(bonecaAmbalabu, s * 96.0F),
            getAmount(bonecaAmbalabu, 14.6F),
            3.6F * bonecaAmbalabuFactor,
            valueForIntFloat
         );
         float bonecaAmbalabu8 = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, s * 2.75F, 14.45F);
         bonecaAmbalabu.getClassForFloatInt(
            bonecaAmbalabu8,
            getAmount(bonecaAmbalabu, 14.45F),
            getBonecaAmbalabu(bonecaAmbalabu, s * 78.0F),
            getAmount(bonecaAmbalabu, 14.3F),
            getBonecaAmbalabu(bonecaAmbalabu, s * 94.0F),
            getAmount(bonecaAmbalabu, 13.8F),
            3.0F * bonecaAmbalabuFactor,
            ForeShinUtils.getValueForIntFloat(7029798, 0.7F)
         );
      }

      handleBonecaAmbalabu(bonecaAmbalabu, 2600, 7.0F * bonecaAmbalabuFactor, 1.1F * bonecaAmbalabuFactor, 13208138, 0.4F, 107);

      for (int bonecaAmbalabuTotal = -1; bonecaAmbalabuTotal <= 1; bonecaAmbalabuTotal += 2) {
         float tailEarValue = bonecaAmbalabuTotal * 1.6F;
         float degrees = bonecaAmbalabuTotal * 9.0F;
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForIntFloat(14921330, 0.3F), getAmounts(bonecaAmbalabu, tailEarValue, 16.37F, 1.5189999F, 1.225F, degrees, 32));
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForIntFloat(15853788, 0.2F), getAmounts(bonecaAmbalabu, tailEarValue, 16.23F, 1.3230001F, 1.05F, degrees, 32));
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForIntFloat(8014370, 0.3F), getAmounts(bonecaAmbalabu, tailEarValue, 16.35F, 1.1760001F, 0.90999997F, degrees, 32));
         bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(1839628), getAmounts(bonecaAmbalabu, tailEarValue, 16.35F, 1.0192F, 0.75600004F, degrees, 32));
         handle(bonecaAmbalabu, 2.4F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(7029798, 0.9F), bonecaAmbalabuTotal * 0.669F, 16.1F, bonecaAmbalabuTotal * 0.5710001F, 15.700001F, bonecaAmbalabuTotal * 0.62F, 15.35F);
      }

      handle(bonecaAmbalabu, 3.0F * bonecaAmbalabuFactor, ForeShinUtils.getValueForIntFloat(11561522, 0.55F), 0.0F, 16.9F, 0.0F, 15.6F);
      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForIntFloat(4856864, 0.5F), getAmounts(bonecaAmbalabu, 0.0F, 12.64F, 0.56F, 0.44F, 0.0F, 32));
      bonecaAmbalabu.getClassForIntFloatArray(ForeShinUtils.getValueForInt(4856864), getAmounts(bonecaAmbalabu, 0.0F, 12.64F, 0.48F, 0.38F, 0.0F, 32));
      bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, bonecaAmbalabu.bonecaValue * 0.85F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(14921330, 0.15F));
      bonecaAmbalabu.getBonecaAmbalabu(0.035F, 1.5F * bonecaAmbalabuFactor, 21);
      int bonecaAmbalabuLimit = (int)Math.ceil(bonecaAmbalabu.bonecaValue * 0.16F);

      for (int bonecaAmbalabuCounter = 0; bonecaAmbalabuCounter < bonecaAmbalabuLimit; bonecaAmbalabuCounter++) {
         float bonecaAmbalabuValue10 = (bonecaAmbalabuCounter + 0.5F) / bonecaAmbalabu.bonecaValue;
         int bonecaAmbalabuTotal2 = 0;
         int bonecaAmbalabuTotal3 = 0;
         int bonecaAmbalabuTotal4 = 0;

         for (int bonecaAmbalabuBound = 0; bonecaAmbalabuBound < bonecaAmbalabu.ambalabuBonecaValue; bonecaAmbalabuBound++) {
            int bonecaAmbalabuForInt = bonecaAmbalabu.getBonecaAmbalabuForInt(bonecaAmbalabuBound, bonecaAmbalabuCounter);
            bonecaAmbalabuTotal2 += ForeShinUtils.getValueInt(bonecaAmbalabuForInt);
            bonecaAmbalabuTotal3 += ForeShinUtils.getValue4(bonecaAmbalabuForInt);
            bonecaAmbalabuTotal4 += ForeShinUtils.getValue5(bonecaAmbalabuForInt);
         }

         int valueForInt = ForeShinUtils.getValue2(
            ForeShinUtils.getValue(bonecaAmbalabuTotal2 / bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuTotal3 / bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabuTotal4 / bonecaAmbalabu.ambalabuBonecaValue),
            ForeShinUtils.getValueForInt(13208138),
            0.4F
         );
         float foreShinFactor = 1.0F - getAmount3((bonecaAmbalabuValue10 - 0.06F) / 0.1F);

         for (int bonecaAmbalabuCounter3 = 0; bonecaAmbalabuCounter3 < bonecaAmbalabu.ambalabuBonecaValue; bonecaAmbalabuCounter3++) {
            bonecaAmbalabu.getBonecaAmbalabu(bonecaAmbalabuCounter3, bonecaAmbalabuCounter, ForeShinUtils.getValue2(bonecaAmbalabu.getBonecaAmbalabuForInt(bonecaAmbalabuCounter3, bonecaAmbalabuCounter), valueForInt, foreShinFactor));
         }
      }
   }

   @Override
   protected int getValueInt() {
      return 1024;
   }

   private static float getAmount5(float tailEarLimit, float tailEarValue) {
      return tailEarLimit < -tailEarValue ? -tailEarValue : (tailEarLimit > tailEarValue ? tailEarValue : tailEarLimit);
   }

   private static void handleBonecaAmbalabu10(BonecaAmbalabu bonecaAmbalabu) {
      for (int i = 0; i < bonecaAmbalabu.bonecaValue; i++) {
         float bonecaAmbalabuValue = (i + 0.5F) / bonecaAmbalabu.bonecaValue;

         for (int j = 0; j < bonecaAmbalabu.ambalabuBonecaValue; j++) {
            float cos = (float)Math.cos((float) (Math.PI * 2) * ((j + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F));
            int foreShinValue = ForeShinUtils.getValue2(
               ForeShinUtils.getValueForInt(15251350),
               ForeShinUtils.getValueForInt(14260864),
               0.35F * getAmount3((bonecaAmbalabuValue - 0.7F) / 0.3F) * getAmount3((cos + 0.2F) / 0.6F)
            );
            bonecaAmbalabu.getBonecaAmbalabu(j, i, ForeShinUtils.getValue2(foreShinValue, ForeShinUtils.getValueForInt(14260864), getAmount3((-cos - 0.45F) / 0.25F)));
         }
      }

      bonecaAmbalabu.getBonecaAmbalabu(0.03F, 3.0F, 96);

      for (int k = 0; k < 60; k++) {
         float bonecaAmbalabuFactor = 0.5F + (getAmount(k, 1, 503) < 0.5F ? -0.5F : 0.5F) * (1.0F - 0.24F * getAmount(k, 2, 503));
         float classValue = bonecaAmbalabuFactor * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = bonecaAmbalabu.bonecaValue * getAmount(k, 3, 503);
         bonecaAmbalabu.getClass022(classValue, amountForFloat, 0.6F + 0.5F * getAmount(k, 4, 503), 0.6F + 0.5F * getAmount(k, 5, 503), ForeShinUtils.getValueForIntFloat(15126424, 0.85F));
      }
   }

   private static int getValueForFloat(float tailEarValue) {
      return ForeShinUtils.getValue2(ForeShinUtils.getValueForInt(13208138), ForeShinUtils.getValueForInt(15853788), getAmount3((tailEarValue - 0.62F) / 0.25F));
   }

   private static float getAmount4(float tailEarFactor) {
      return tailEarFactor * tailEarFactor;
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, boolean bonecaAmbalabuFlag, int bonecaAmbalabuValue) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(13208138))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11103798), 0.35F, bonecaAmbalabu.ambalabuBonecaValue / 12.0F, bonecaAmbalabuValue)
         .getBonecaAmbalabu(0.05F, 2.5F, bonecaAmbalabuValue + 1);
      if (bonecaAmbalabuFlag) {
         for (int i = 0; i < bonecaAmbalabu.ambalabuBonecaValue; i++) {
            float absScaled = Math.abs((i + 0.5F) / bonecaAmbalabu.ambalabuBonecaValue - 0.5F) * 2.0F;
            bonecaAmbalabu.getClassBonecaAmbalabu(i, 0.0F, i + 1, bonecaAmbalabu.bonecaValue, ForeShinUtils.getValueForIntFloat(11561522, 0.5F * (1.0F - getAmount3(absScaled / 0.18F))));
         }
      } else {
         bonecaAmbalabu.getClassBonecaAmbalabu(0.0F, 0.0F, bonecaAmbalabu.ambalabuBonecaValue, bonecaAmbalabu.bonecaValue * 0.5F, ForeShinUtils.getValueForIntFloat(14921330, 0.3F));
      }

      for (int j = 0; j < 220; j++) {
         float classBound = getAmount(j, 1, bonecaAmbalabuValue) * bonecaAmbalabu.ambalabuBonecaValue;
         float amountForFloat = getAmount(j, 2, bonecaAmbalabuValue) * bonecaAmbalabu.bonecaValue;
         bonecaAmbalabu.getClass02(
            classBound,
            amountForFloat,
            classBound + (getAmount(j, 3, bonecaAmbalabuValue) - 0.5F) * bonecaAmbalabu.ambalabuBonecaValue / 20.0F,
            amountForFloat + bonecaAmbalabu.bonecaValue / 10.0F,
            1.2F,
            ForeShinUtils.getValueForIntFloat(getAmount(j, 4, bonecaAmbalabuValue) < 0.5F ? 0 : 16773596, 0.15F)
         );
      }
   }

   private static float getAmount(float tailEarValue, float tailEarValue2, float tailEarValue3, float tailEarValue4, float tailEarValue5, float tailEarValue6) {
      float deltaY = tailEarValue5 - tailEarValue3;
      float deltaX = tailEarValue6 - tailEarValue4;
      float tailEarLimit = deltaY * deltaY + deltaX * deltaX;
      float tailEarLimit2 = tailEarLimit > 0.0F ? ((tailEarValue - tailEarValue3) * deltaY + (tailEarValue2 - tailEarValue4) * deltaX) / tailEarLimit : 0.0F;
      float tailEarFactor = tailEarLimit2 < 0.0F ? 0.0F : (tailEarLimit2 > 1.0F ? 1.0F : tailEarLimit2);
      float tailEarFactor2 = tailEarValue3 + tailEarFactor * deltaY - tailEarValue;
      float tailEarFactor3 = tailEarValue4 + tailEarFactor * deltaX - tailEarValue2;
      return tailEarFactor2 * tailEarFactor2 + tailEarFactor3 * tailEarFactor3;
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.trunk = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 12.0F, 0.0F);
      this.head = latheLoft.getClassForStringForeEar("trunk", this.trunk, 0.0F, 14.8F, 0.0F);
      this.earL = latheLoft.getClassForStringForeEar("head", this.head, 0.0F, 15.2F, -10.6F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("jaw", this.earL, 0.0F, 13.6F, -10.2F);
      this.eyeRightSection = latheLoft.getClassForStringForeEar("eye_r", this.earL, 1.6F, 16.35F, getAmount4(1.6F, 16.35F));
      this.classShinFoot = latheLoft.getClassForStringForeEar("eye_l", this.earL, -1.6F, 16.35F, getAmount4(-1.6F, 16.35F));
      this.foreEar = latheLoft.getClassForStringForeEar("ear_r", this.earL, 2.2F, 18.3F, -11.35F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("ear_l", this.earL, -2.2F, 18.3F, -11.35F);
      this.ruffSection = latheLoft.getClassForStringForeEar("ruff", this.earL, 0.0F, 15.2F, -10.6F);
      this.tailB = latheLoft.getClassForStringForeEar("tail_a", this.head, 0.0F, getAmounts(tailFinAmounts, 6.1F), 6.1F);
      this.tailFin = latheLoft.getClassForStringForeEar("tail_b", this.tailB, 0.0F, getAmounts(tailFinAmounts, 9.9F), 9.9F);
      this.tailFinSection = latheLoft.getClassForStringForeEar("tail_fin", this.tailFin, 0.0F, getAmounts(tailFinAmounts, 13.3F), 13.3F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("pec_r", this.head, 2.9F, 14.7F, -6.3F);
      this.tailEarForeEar = latheLoft.getClassForStringForeEar("pec_l", this.head, -2.9F, 14.7F, -6.3F);
      String[] strings = new String[]{"fr", "fl", "br", "bl"};

      for (int i = 0; i < 4; i++) {
         this.thighSections[i] = latheLoft.getClassForStringForeEar("thigh_" + strings[i], this.trunk, shinProgressTable[i], THIGH_HEIGHTS[i], LEG_OFFSETS_Z[i]);
         this.shinProgressForeEars[i] = latheLoft.getClassForStringForeEar("shin_" + strings[i], this.thighSections[i], shinProgressTable[i], SHIN_HEIGHTS[i], LEG_OFFSETS_Z[i] - 0.12F);
         this.footSections[i] = latheLoft.getClassForStringForeEar("foot_" + strings[i], this.shinProgressForeEars[i], shinProgressTable[i], 1.15F, LEG_OFFSETS_Z[i]);
      }

      latheLoft.getLatheLoft(this.thighSections[0]);
      LoftLatheFlag.Region region = latheLoft.getMax(512, 256);
      handleBonecaAmbalabu9(region.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max2 = latheLoft.getMax(512, 288);
      handleBonecaAmbalabu(max2.getClassBonecaAmbalabu(), -10.5F, 6.7F);
      LoftLatheFlag.Region max3 = latheLoft.getMax(256, 96);
      handleBonecaAmbalabu(max3.getClassBonecaAmbalabu(), 5.5F, 10.2F);
      LoftLatheFlag.Region max4 = latheLoft.getMax(160, 96);
      handleBonecaAmbalabu(max4.getClassBonecaAmbalabu(), 9.4F, 13.8F);
      LoftLatheFlag.Region max5 = latheLoft.getMax(128, 80);
      handleBonecaAmbalabu3(max5.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max6 = latheLoft.getMax(96, 96);
      handleBonecaAmbalabu4(max6.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max7 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu5(max7.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max8 = latheLoft.getMax(128, 64);
      handleBonecaAmbalabu7(max8.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max9 = latheLoft.getMax(64, 64);
      handleBonecaAmbalabu(max9.getClassBonecaAmbalabu(), true, 111);
      LoftLatheFlag.Region max10 = latheLoft.getMax(96, 48);
      handleBonecaAmbalabu(max10.getClassBonecaAmbalabu(), false, 113);
      LoftLatheFlag.Region max11 = latheLoft.getMax(64, 32);
      handleBonecaAmbalabu6(max11.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max12 = latheLoft.getMax(32, 24);
      handleBonecaAmbalabu11(max12.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max13 = latheLoft.getMax(32, 96);
      handleBonecaAmbalabu(max13.getClassBonecaAmbalabu(), true);
      LoftLatheFlag.Region max14 = latheLoft.getMax(32, 96);
      handleBonecaAmbalabu(max14.getClassBonecaAmbalabu(), false);
      LoftLatheFlag.Region max15 = latheLoft.getMax(48, 48);
      handleBonecaAmbalabu10(max15.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max16 = latheLoft.getMax(32, 32);
      handleBonecaAmbalabu(max16.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max17 = latheLoft.getMax(320, 64);
      handleBonecaAmbalabuForBonecaAmbalabu(max17.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max18 = latheLoft.getMax(32, 32);
      handleBonecaAmbalabu8(max18.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max19 = latheLoft.getMax(176, 96);
      handleBonecaAmbalabu(max19.getClassBonecaAmbalabu(), WING_ANCHOR_POINTS, WING_CURVE_POINTS, false);
      LoftLatheFlag.Region max20 = latheLoft.getMax(176, 96);
      handleBonecaAmbalabu(max20.getClassBonecaAmbalabu(), WING_ANCHOR_POINTS, WING_CURVE_POINTS, true);
      LoftLatheFlag.Region max21 = latheLoft.getMax(112, 192);
      handleBonecaAmbalabu(max21.getClassBonecaAmbalabu(), TAIL_ANCHOR_POINTS, TAIL_CURVE_POINTS, false);
      LoftLatheFlag.Region max22 = latheLoft.getMax(112, 192);
      handleBonecaAmbalabu(max22.getClassBonecaAmbalabu(), TAIL_ANCHOR_POINTS, TAIL_CURVE_POINTS, true);
      LoftLatheFlag.Region max23 = latheLoft.getMax(96, 80);
      handleBonecaAmbalabu(max23.getClassBonecaAmbalabu(), FLIPPER_ANCHOR_POINTS, FLIPPER_CURVE_POINTS, false);
      LoftLatheFlag.Region max24 = latheLoft.getMax(96, 80);
      handleBonecaAmbalabu(max24.getClassBonecaAmbalabu(), FLIPPER_ANCHOR_POINTS, FLIPPER_CURVE_POINTS, true);
      LoftLatheFlag.Region max25 = latheLoft.getMax(128, 80);
      handleBonecaAmbalabu(max25.getClassBonecaAmbalabu(), SPIKE_ANCHOR_POINTS, SPIKE_OUTLINE_POINTS, false);
      LoftLatheFlag.Region max26 = latheLoft.getMax(128, 80);
      handleBonecaAmbalabu(max26.getClassBonecaAmbalabu(), SPIKE_ANCHOR_POINTS, SPIKE_OUTLINE_POINTS, true);
      LoftLatheFlag.Region max27 = latheLoft.getMax(80, 48);
      handleBonecaAmbalabu(max27.getClassBonecaAmbalabu(), SMALL_SPIKE_ANCHOR_POINTS, SMALL_SPIKE_OUTLINE_POINTS, false);
      LoftLatheFlag.Region max28 = latheLoft.getMax(80, 48);
      handleBonecaAmbalabu(max28.getClassBonecaAmbalabu(), SMALL_SPIKE_ANCHOR_POINTS, SMALL_SPIKE_OUTLINE_POINTS, true);
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
      ShinBrr classForRegion19 = latheLoft.getClassForRegion(max18);
      ShinBrr shinBrr = latheLoft.getMax(15449759);
      ShinBrr shinBrr2 = latheLoft.getMax(16119285);
      ShinBrr shinBrr3 = latheLoft.getMax(11121336);
      ShinBrr shinBrr4 = latheLoft.getMax(4856864);
      ShinBrr shinBrr5 = latheLoft.getMax(13922168);
      latheLoft.getLatheLoftForForeEar(this.earL);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(0.0F, 0.0F, -11.0F);
      latheLoft.getLatheProfile(tailEarTable, shinFootTable, 22, 1.0F, 0.85F, classForRegion, null, null);
      latheLoft.getDequeArray();
      this.handle(latheLoft, classForRegion9, classForRegion10, classForRegion11, classForRegion8, classForRegion13, shinBrr2, shinBrr4);
      latheLoft.getLatheLoftForForeEar(this.ruffSection);
      float[] floats = new float[]{
         0.0F, getAmounts(tailFinAmounts, -7.3F) + 0.05F, -7.3F, 0.0F, 15.15F, -7.9F, 0.0F, 15.4F, -8.5F, 0.0F, 15.6F, -9.05F, 0.0F, 15.65F, -9.6F
      };
      float[] floats2 = new float[]{getAmounts(TAIL_RADII, -7.3F) + 0.12F, 3.4F, 3.45F, 3.4F, 3.2F};
      float[] crownForeValues = new float[]{getAmounts(finTable, -7.3F) + 0.12F, 4.0F, 3.8F, 3.5F, 3.15F};
      latheLoft.getLoftPath(floats, floats2, crownForeValues, 16, classForRegion18, null, null, 0.0F, 1.0F, 0.0F);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      float[] pointsSpline = LatheLoft.getPointsSpline(
         1, -0.72F, 13.0F, getAmount4(-0.72F, 12.95F) - 0.1F, 0.0F, 12.95F, getAmount4(0.0F, 12.95F) - 0.17F, 0.72F, 13.0F, getAmount4(0.72F, 12.95F) - 0.1F
      );
      latheLoft.getLatheLoft(pointsSpline, LatheLoft.getAmounts(LatheLoft.getValue(pointsSpline), 0.22F, 0.38F, 0.22F), 8, classForRegion12, true);
      latheLoft.getLatheLoft(0.0F, 12.7F, -12.75F, 0.9F, 0.55F, 1.4F, 8, classForRegion12);
      latheLoft.getLatheLoft(0.0F, 13.4F, -12.6F, 1.25F, 0.9F, 1.2F, 8, shinBrr4);
      latheLoft.getLatheLoft(0.0F, 12.85F, -13.25F, 0.48F, 0.13F, 0.5F, 8, shinBrr5);
      latheLoft.getLatheLoftForForeEar(this.eyeRightSection);
      this.handle(latheLoft, 1.0F, classForRegion6, classForRegion19, shinBrr2);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      this.handle(latheLoft, -1.0F, classForRegion6, classForRegion19, shinBrr2);
      latheLoft.getLatheLoftForForeEar(this.foreEar);
      this.handle(latheLoft, 1.0F, classForRegion7);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      this.handle(latheLoft, -1.0F, classForRegion7);
      latheLoft.getLatheLoftForForeEar(this.head);
      this.handleAmountsFin(latheLoft, -10.5F, 6.7F, 0.0F, 2.15F, 20, classForRegion3, shinBrr3);
      ShinBrr shinBrr6 = latheLoft.getMax(7239808);

      for (int j = -1; j <= 1; j += 2) {
         float[] latheLoftValues = new float[15];

         for (int k = 0; k < 5; k++) {
            float shinFootFactor = -0.8F + 1.6F * k / 4.0F;
            float shinFootValue = getAmount(shinFootFactor);
            latheLoftValues[3 * k] = j * getAmounts(TAIL_RADII, shinFootValue) * (float)Math.sqrt(1.0F - shinFootFactor * shinFootFactor);
            latheLoftValues[3 * k + 1] = getAmounts(tailFinAmounts, shinFootValue) + getAmounts(finTable, shinFootValue) * shinFootFactor;
            latheLoftValues[3 * k + 2] = shinFootValue;
         }

         latheLoft.getLatheLoft(latheLoftValues, LatheLoft.getAmounts(5, 0.03F, 0.12F, 0.12F, 0.03F), 6, shinBrr6, false);
      }

      this.handle(latheLoft, WING_ANCHOR_POINTS, WING_CURVE_POINTS, latheLoft.getClassForRegion(max19), latheLoft.getClassForRegion(max20));

      for (int m = -1; m <= 1; m += 2) {
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(m * 0.95F, 11.0F, -0.8F).getLatheLoft4(-90.0F + m * 15.0F).getLatheLoft(-35.0F);
         latheLoft.getPolygonExtrude(getAmounts(SMALL_SPIKE_ANCHOR_POINTS, SMALL_SPIKE_OUTLINE_POINTS), -0.07F, 0.07F, latheLoft.getClassForRegion(max27), latheLoft.getClassForRegion(max28), null);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.tailB);
      this.handleAmountsFin(latheLoft, 5.5F, 10.2F, 1.0F, 1.5F, 14, classForRegion4, shinBrr3);
      this.handle(latheLoft, FLIPPER_ANCHOR_POINTS, FLIPPER_CURVE_POINTS, latheLoft.getClassForRegion(max23), latheLoft.getClassForRegion(max24));
      latheLoft.getLatheLoftForForeEar(this.tailFin);
      this.handleAmountsFin(latheLoft, 9.4F, 13.8F, 0.9F, 1.5F, 12, classForRegion5, shinBrr3);
      latheLoft.getLatheLoftForForeEar(this.tailFinSection);
      this.handle(latheLoft, TAIL_ANCHOR_POINTS, TAIL_CURVE_POINTS, latheLoft.getClassForRegion(max21), latheLoft.getClassForRegion(max22));
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      this.handleAmounts(latheLoft, 1.0F, latheLoft.getClassForRegion(max25), latheLoft.getClassForRegion(max26));
      latheLoft.getLatheLoftForForeEar(this.tailEarForeEar);
      this.handleAmounts(latheLoft, -1.0F, latheLoft.getClassForRegion(max25), latheLoft.getClassForRegion(max26));

      for (int p = 0; p < 4; p++) {
         latheLoft.getLatheLoftForForeEar(this.thighSections[p]);
         this.handleAmounts(latheLoft, p, classForRegion14);
         latheLoft.getLatheLoftForForeEar(this.shinProgressForeEars[p]);
         this.handleAmounts(latheLoft, p, classForRegion15, classForRegion17);
         latheLoft.getLatheLoftForForeEar(this.footSections[p]);
         this.handleAmountsForLatheLoftInt(latheLoft, p, classForRegion16, shinBrr);
      }
   }

   private static float getAmount(int tailEarValue, int tailEarValue2, int bonecaAmbalabuValue) {
      int tailEarValue4 = tailEarValue * 374761393 + tailEarValue2 * 668265263 + bonecaAmbalabuValue * 1442695041;
      int tailEarValue5 = (tailEarValue4 ^ tailEarValue4 >>> 13) * 1274126177;
      int tailEarValue6 = tailEarValue5 ^ tailEarValue5 >>> 16;
      return (tailEarValue6 & 16777215) / 1.6777215E7F;
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu, float tailEarValue, int valueForIntFloat, float... tailEarValues) {
      int tailEarSize = tailEarValues.length / 2;
      float[] floats = new float[tailEarSize];
      float[] floats2 = new float[tailEarSize];

      for (int i = 0; i < tailEarSize; i++) {
         floats[i] = getBonecaAmbalabuForBonecaAmbalabuFloat(bonecaAmbalabu, tailEarValues[2 * i], tailEarValues[2 * i + 1]);
         floats2[i] = getAmount(bonecaAmbalabu, tailEarValues[2 * i + 1]);
      }

      bonecaAmbalabu.getClassForFloatArrayFloat(floats, floats2, tailEarValue, valueForIntFloat);
   }

   private void handleAmounts(LatheLoft latheLoft, int index, ShinBrr shinBrr) {
      float tailEarValue = shinProgressTable[index];
      float tailEarValue2 = LEG_OFFSETS_Z[index];
      float tailEarValue3 = SHIN_HEIGHTS[index];
      float tailEarValue4 = THIGH_RADII[index];
      float tailEarValue5 = THIGH_HEIGHTS[index];
      latheLoft.getLatheLoft(
         new float[]{tailEarValue, HIP_HEIGHTS[index], tailEarValue2 + 0.1F, tailEarValue, tailEarValue5 + 0.4F, tailEarValue2 + 0.06F, tailEarValue, 0.5F * (tailEarValue5 + tailEarValue3), tailEarValue2, tailEarValue, tailEarValue3 - 0.05F, tailEarValue2 - 0.05F},
         new float[]{THIGH_WIDTHS[index], tailEarValue4 * 1.05F, tailEarValue4 * 0.85F, 0.68F},
         8,
         shinBrr,
         false
      );
   }

   private static void handleBonecaAmbalabu11(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(15245984), ForeShinUtils.getValueForInt(14257292)).getBonecaAmbalabu(0.05F, 2.0F, 81);
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabu.bonecaValue * 0.25F, bonecaAmbalabu.ambalabuBonecaValue * 0.22F, bonecaAmbalabu.bonecaValue * 0.1F, ForeShinUtils.getValueForIntFloat(16172740, 0.6F));
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

   private static float getAmount(BonecaAmbalabu bonecaAmbalabu, float[] tailEarValues, float tailEarValue) {
      return (tailEarValues[3] - tailEarValue) / (tailEarValues[3] - tailEarValues[2]) * bonecaAmbalabu.bonecaValue;
   }
}
