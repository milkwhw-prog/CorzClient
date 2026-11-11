package com.corz.client.utils;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.BonecaAmbalabu;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.LoftLatheFlag;

public final class TungPupil extends WarmBrainrotBase {
   private ForeEar foreR;
   private ForeEar handL;
   private static final float[] shinProgressTable = new float[]{0.3F, -1.13F, 2.61F, 1.06F, -0.76F, 1.12F};
   private ForeEar classShinFoot;
   private ForeEar legL;
   private ForeEar foreL;
   private static final float[] tungPupilTable = new float[]{1.44F, 0.43F, 1.65F, 0.98F, -0.88F, -0.08F};
   private static final float[] table = new float[]{0.38F, -0.69F, 1.11F, 2.0F, -0.06F, 1.1F};
   private ForeEar pupilL;
   private ForeEar classShinProgress;
   private ForeEar shinFootForeEar;
   private static final float[] POSE_WEIGHTS_A = new float[]{0.4F, 0.76F, -1.08F, 1.86F, 0.99F, 0.03F};
   private static final float[] POSE_WEIGHTS_B = new float[]{0.85F, 1.31F, -2.01F, 1.0F, -0.01F, -0.47F};
   private ForeEar handR;
   private static final float[] POSE_WEIGHTS_C = new float[]{1.64F, -0.4F, -1.75F, 0.66F, -0.23F, -0.11F};
   private ForeEar shinProgressForeEar;
   private ForeEar shinR;
   private ForeEar footR;
   private static final float[] OFFSET_POSE_A = new float[]{-0.98F, 4.5F, -2.7F};
   private static final float[] OFFSET_POSE_B = new float[]{-0.4F, 0.5F, -1.2F};
   private static final float[] PUPIL_OUTLINE_POINTS = new float[]{
      -1.18F, 0.55F, -0.98F, 1.22F, -0.5F, 1.5F, -1.18F, -1.9F, -1.18F, -3.5F, -1.05F, -4.24F, -1.2F, -5.43F, -1.0F, -5.76F
   };
   private ForeEar pupilR;
   private ForeEar footL;
   private ForeEar armL;
   private ForeEar shinL;

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, BonecaAmbalabu bonecaAmbalabu2, int bonecaAmbalabuValue) {
      for (int i = 0; i < bonecaAmbalabu2.bonecaValue; i++) {
         for (int j = 0; j < bonecaAmbalabu2.ambalabuBonecaValue; j++) {
            bonecaAmbalabu2.getBonecaAmbalabu(j, i, bonecaAmbalabu.getBonecaAmbalabuForInt(j, bonecaAmbalabuValue - i));
         }
      }
   }

   private static void handle(LatheLoft latheLoft, float tungPupilValue, ShinBrr shinBrr) {
      float latheLoftValue = tungPupilValue * 4.2F;
      float eyeValue = 24.15F;
      float tungPupilValue4 = tungPupilValue * 4.6F;
      latheLoft.getLatheLoft(new float[]{latheLoftValue, eyeValue, 0.0F, tungPupilValue * 4.4F, 21.6F, -0.1F, tungPupilValue4, 19.3F, -0.2F}, new float[]{0.6F, 0.57F, 0.56F}, 8, shinBrr, false);
      latheLoft.getLatheLoft(latheLoftValue, eyeValue, 0.0F, 0.6F, 8, shinBrr);
      latheLoft.getLatheLoft(tungPupilValue4, 19.3F, -0.2F, 0.56F, 8, shinBrr);
   }

   private static void handle(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(16038008))
         .getBonecaAmbalabu(0.05F, 14.0F, 3)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11565637), 11.0F, 0.28F, 0.32F, 7)
         .getBonecaAmbalabu(0.03F, 3.0F, 9);
   }

   private static float getAmount(BonecaAmbalabu bonecaAmbalabu, float tungPupilValue) {
      return bonecaAmbalabu.bonecaValue * (35.2F - tungPupilValue) / 19.400002F;
   }

   private static void handle(LatheLoft latheLoft, float tungPupilFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      float toeScaled = tungPupilFactor * 1.8F;
      float tungPupilValue = toeScaled + tungPupilFactor * 0.25F;
      latheLoft.getLatheLoft(toeScaled, 1.3F, 0.0F, 0.55F, 6, shinBrr);
      latheLoft.getLatheLoft(tungPupilValue, 0.62F, -1.9F, 1.25F, 0.6F, 2.8F, 12, shinBrr2);
      latheLoft.getLatheLoft(tungPupilValue - tungPupilFactor * 0.1F, 0.46F, -3.5F, 1.4F, 0.44F, 1.05F, 10, shinBrr2);
      latheLoft.getLatheLoft(tungPupilValue, 0.7F, 0.55F, 0.75F, 0.68F, 0.95F, 8, shinBrr2);

      for (int i = 0; i < 5; i++) {
         float tungPupilValue2 = tungPupilValue - tungPupilFactor * (0.8F - i * 0.4F);
         float tungPupilValue3 = i == 0 ? 1.5F : 1.15F - 0.07F * i;
         float tungPupilValue4 = i == 0 ? 0.32F : 0.24F - 0.012F * i;
         float tungPupilValue5 = tungPupilValue4 * 0.8F + 0.1F;
         float tungPupilValue6 = -4.15F;
         float deltaVar = tungPupilValue6 - tungPupilValue3;
         float tungPupilValue7 = tungPupilFactor * (i - 1.5F) * 0.14F;
         latheLoft.getLoftPath(
            new float[]{
               tungPupilValue2,
               tungPupilValue5,
               tungPupilValue6 + 0.25F,
               tungPupilValue2 + tungPupilValue7 * 0.5F,
               tungPupilValue5 + 0.01F,
               tungPupilValue6 - tungPupilValue3 * 0.5F,
               tungPupilValue2 + tungPupilValue7 * 0.85F,
               tungPupilValue5 - 0.1F,
               tungPupilValue6 - tungPupilValue3 * 0.85F,
               tungPupilValue2 + tungPupilValue7,
               tungPupilValue5 - 0.12F,
               deltaVar,
               tungPupilValue2 + tungPupilValue7 * 1.05F,
               tungPupilValue5 - 0.1F,
               deltaVar - tungPupilValue4 * 0.35F
            },
            new float[]{tungPupilValue4, tungPupilValue4 * 0.97F, tungPupilValue4 * 0.88F, tungPupilValue4 * 0.66F, tungPupilValue4 * 0.14F},
            new float[]{tungPupilValue4 * 0.9F, tungPupilValue4 * 0.87F, tungPupilValue4 * 0.8F, tungPupilValue4 * 0.6F, tungPupilValue4 * 0.12F},
            5,
            shinBrr3,
            null,
            null,
            0.0F,
            1.0F,
            0.0F
         );
      }
   }

   private static float getAmounts(float angle, float tungPupilValue, float angle2) {
      float angle3 = angle + tungPupilValue;
      float tungPupilValue2 = 15.599999F
         + -7.3999996F * (float)Math.cos(angle)
         + 0.2F * (float)Math.sin(angle)
         + -7.0F * (float)Math.cos(angle3)
         - 0.2F * (float)Math.sin(angle3);
      float cos = (float)Math.cos(angle2);
      float sin = (float)Math.sin(angle2);
      float tungPupilBound = Float.MAX_VALUE;

      for (int i = 0; i < PUPIL_OUTLINE_POINTS.length; i += 2) {
         tungPupilBound = Math.min(tungPupilBound, PUPIL_OUTLINE_POINTS[i] * cos - PUPIL_OUTLINE_POINTS[i + 1] * sin);
      }

      return tungPupilValue2 + tungPupilBound;
   }

   private static float getAmount(float tungPupilValue) {
      return (float)(-Math.toDegrees(Math.asin(tungPupilValue / 4.1F)));
   }

   @Override
   public int getValue() {
      return 3;
   }

   private static void handleForLatheLoftFloat(LatheLoft latheLoft, float tungPupilValue, ShinBrr shinBrr) {
      float tungPupilValue2 = tungPupilValue * 1.8F;
      latheLoft.getLatheLoft(new float[]{tungPupilValue2, 8.2F, -0.2F, tungPupilValue2, 4.5F, -0.05F, tungPupilValue2, 1.2F, 0.0F}, new float[]{0.7F, 0.62F, 0.55F}, 8, shinBrr, true);
   }

   private static void handle(LatheLoft latheLoft, float tungPupilFactor, ShinBrr shinBrr, boolean tungPupilFlag) {
      float gripScaled = tungPupilFactor * 4.7999997F;
      latheLoft.getLatheLoft(gripScaled, 14.4F, -0.7F, 0.62F, 0.9F, 0.7F, 10, shinBrr);
      if (tungPupilFlag) {
         float gripScaled2 = tungPupilFactor * 4.65F;
         float tungPupilValue = -0.9F;
         float tungPupilFactor2 = 0.58F;
         latheLoft.getLatheLoft(gripScaled2 + tungPupilFactor * 0.45F, 14.45F, tungPupilValue - 0.15F, 0.5F, 0.88F, 0.62F, 10, shinBrr);
         latheLoft.getLatheLoft(gripScaled2 - tungPupilFactor * 0.36F, 14.45F, tungPupilValue + 0.32F, 0.34F, 0.78F, 0.42F, 8, shinBrr);

         for (int i = 0; i < 4; i++) {
            boolean tungPupilFlag2 = i == 3;
            float tungPupilValue2 = tungPupilFlag2 ? 13.85F : 15.0F - i * 0.4F;
            float tungPupilValue3 = tungPupilFlag2 ? 0.16F : 0.21F - 0.02F * i;
            float tungPupilFactor3 = tungPupilFlag2 ? 0.8F : 1.0F;
            float tungPupilValue4 = tungPupilFactor * 0.05F * i;
            latheLoft.getLatheLoft(
               new float[]{
                  gripScaled2 + tungPupilFactor * tungPupilFactor2 * 0.92F + tungPupilValue4,
                  tungPupilValue2,
                  tungPupilValue - tungPupilFactor2 * 0.4F,
                  gripScaled2 + tungPupilFactor * tungPupilFactor2 * 0.2F,
                  tungPupilValue2 - 0.03F,
                  tungPupilValue - tungPupilFactor2 * 0.98F,
                  gripScaled2 - tungPupilFactor * tungPupilFactor2 * 0.7F * tungPupilFactor3,
                  tungPupilValue2 - 0.06F,
                  tungPupilValue - tungPupilFactor2 * 0.72F,
                  gripScaled2 - tungPupilFactor * tungPupilFactor2 * 0.95F * tungPupilFactor3,
                  tungPupilValue2 - 0.08F,
                  tungPupilValue + tungPupilFactor2 * 0.2F * tungPupilFactor3
               },
               new float[]{tungPupilValue3, tungPupilValue3, tungPupilValue3 * 0.94F, tungPupilValue3 * 0.82F},
               5,
               shinBrr,
               true
            );
         }

         latheLoft.getLatheLoft(
            new float[]{gripScaled2 - tungPupilFactor * 0.15F, 15.35F, tungPupilValue + 0.35F, gripScaled2 - tungPupilFactor * 0.6F, 15.05F, tungPupilValue - 0.45F, gripScaled2 - tungPupilFactor * 0.3F, 14.75F, tungPupilValue - 0.95F},
            new float[]{0.23F, 0.2F, 0.16F},
            5,
            shinBrr,
            true
         );
      } else {
         for (int j = 0; j < 4; j++) {
            float tungPupilValue5 = -1.15F + j * 0.36F;
            latheLoft.getLatheLoft(
               new float[]{
                  gripScaled - tungPupilFactor * 0.3F,
                  14.2F,
                  tungPupilValue5,
                  gripScaled - tungPupilFactor * 0.6F,
                  13.7F,
                  tungPupilValue5 - 0.04F,
                  gripScaled - tungPupilFactor * 0.45F,
                  13.32F,
                  tungPupilValue5 + 0.02F,
                  gripScaled - tungPupilFactor * 0.08F,
                  13.42F,
                  tungPupilValue5 + 0.15F
               },
               new float[]{0.17F, 0.17F, 0.16F, 0.14F},
               5,
               shinBrr,
               true
            );
         }

         latheLoft.getLatheLoft(
            new float[]{gripScaled - tungPupilFactor * 0.35F, 14.6F, -1.2F, gripScaled - tungPupilFactor * 0.62F, 14.05F, -1.45F, gripScaled - tungPupilFactor * 0.5F, 13.75F, -1.38F},
            new float[]{0.19F, 0.17F, 0.15F},
            5,
            shinBrr,
            true
         );
      }
   }

   private ForeEar getArm(LatheLoft latheLoft, String armText, float armValue) {
      float nameScaled = armValue * 1.85F;
      float amount2 = getAmount2(nameScaled);
      double angle = Math.toRadians(getAmount(nameScaled));
      return latheLoft.getClassForStringForeEar(armText, this.armL, nameScaled + 1.8F * (float)Math.sin(angle), 29.9F, amount2 + 1.8F * (float)Math.cos(angle));
   }

   @Override
   public String formatNoteBlock() {
      return "block.note_block.basedrum";
   }

   private static float getAmount(float tungPupilLimit, float tungPupilValue) {
      return tungPupilLimit < -tungPupilValue ? -tungPupilValue : (tungPupilLimit > tungPupilValue ? tungPupilValue : tungPupilLimit);
   }

   private static float getBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu, float bonecaAmbalabuValue) {
      double asin = Math.asin(Math.max(-1.0F, Math.min(1.0F, bonecaAmbalabuValue / 4.1F)));
      return bonecaAmbalabu.ambalabuBonecaValue * (float)(0.5 - asin / (Math.PI * 2));
   }

   @Override
   public void handleShinProgress(PoseProgress poseProgress, ForeShinScale foreShinScale) {
      float shinProgressFactor = poseProgress.limbSwingStrength;
      float angle = poseProgress.animationProgress * 0.6662F;
      float shinProgressValue = getAmount3((shinProgressFactor - 0.88F) / 0.1F);
      float shinProgressTotal = poseProgress.getAnimationProgressForFloat(0.0F) * (0.55F + 0.15F * shinProgressValue);
      float shinProgressTotal2 = poseProgress.getAnimationProgressForFloat((float) Math.PI) * (0.55F + 0.15F * shinProgressValue);
      float tungPupilValue = -poseProgress.getAnimationProgress((float) Math.PI) * (0.6F + 0.45F * shinProgressValue);
      float shinProgressTotal4 = -poseProgress.getAnimationProgress(0.0F) * (0.6F + 0.45F * shinProgressValue);
      float amountForFloat = getAmountForFloat(angle, shinProgressFactor);
      float amountForFloat3 = getAmountForFloat(angle + (float) Math.PI, shinProgressFactor);
      float min2 = Math.min(getAmounts(shinProgressTotal, tungPupilValue, amountForFloat), getAmounts(shinProgressTotal2, shinProgressTotal4, amountForFloat3));
      float shinProgressValue4 = shinProgressFactor * Math.abs((float)Math.sin(angle));
      float shinProgressValue5 = (1.0F - shinProgressValue4) * shinProgressFactor;
      float planValuesItem = poseProgress.shinProgressAmount * 0.25F;
      foreShinScale.getAmountsBoneCount(this.armL, planValuesItem - 0.08F * shinProgressFactor, poseProgress.headYawRadians * 0.35F, -0.05F * shinProgressFactor * (float)Math.sin(angle + 0.6F));
      foreShinScale.getAmountsForeShin2(this.armL, 0.0F, 0.45F * shinProgressValue4, 0.0F);
      foreShinScale.getAmountsForForeEarFloat(this.armL, 1.0F + 0.03F * shinProgressValue5, 1.0F - 0.04F * shinProgressValue5, 1.0F + 0.03F * shinProgressValue5);
      float shinProgressValue7 = 0.15F + 0.2F * shinProgressFactor + (0.13F - 0.11F * poseProgress.getAnimationProgressForFloat((float) Math.PI)) * min2;
      float shinProgressTotal5 = getAmount(poseProgress.getAnimationProgressForFloat((float) Math.PI) * 0.25F, 2.6F + 0.12F * shinProgressValue4, shinProgressValue) - planValuesItem;
      float dinForeValue = 0.0F;
      float shinProgressFactor4 = getAmount(0.06F + 0.03F * poseProgress.getAgeCode(70.0F, 1.0F), -0.1F, shinProgressValue);
      float shinProgressTotal6 = poseProgress.getAnimationProgressForFloat(0.0F) * (0.35F + 0.35F * shinProgressValue) - planValuesItem;
      float shinProgressFactor5 = 0.0F;
      float shinProgressFactor6 = -0.06F - 0.03F * poseProgress.getAgeCode(70.0F, 0.0F);
      float foreShinValue = getAmount(shinProgressValue7, 0.6F, shinProgressValue) + 0.6F * Math.abs(planValuesItem);
      float shinProgressFactor7 = 0.15F + 0.2F * shinProgressFactor + 0.7F * shinProgressValue;
      float shinProgressValue8 = 0.0F;
      float shinProgressValue9 = 0.0F;
      float shinProgressValue10 = 0.0F;
      float shinProgressValue11 = 0.0F;
      float shinProgressValue12 = getAmount(poseProgress.shinProgressAmount * 0.4F, 0.18F);
      float shinProgressValue13 = getAmount(poseProgress.headYawRadians * 0.5F, 0.22F);
      foreShinScale.getAmounts(this.pupilR, shinProgressValue12, shinProgressValue13, 0.0F);
      foreShinScale.getAmounts(this.pupilL, shinProgressValue12, shinProgressValue13, 0.0F);
      if (shinProgressFactor < 0.3F && poseProgress.swingProgress <= 0.0F && !poseProgress.poseProgressActive && !poseProgress.sneakingPose) {
         float shinProgressValue14 = (poseProgress.age + (poseProgress.hashCode & 63)) % 90.0F;
         if (shinProgressValue14 < 30.0F) {
            float shinProgressFactor8 = shinProgressValue14 % 10.0F / 10.0F;
            float shinProgressFactor9 = 1.0F - shinProgressFactor / 0.3F;
            foreShinValue += (float)Math.sin(shinProgressFactor8 * Math.PI) * 0.55F * shinProgressFactor9;
            foreShinScale.getAmountsBoneCount(this.armL, 0.0F, 0.0F, (float)Math.sin(shinProgressFactor8 * Math.PI) * 0.02F * shinProgressFactor9);
         }
      }

      if (poseProgress.swingProgress > 0.0F) {
         float swingProgress = poseProgress.swingProgress;
         float shinProgressFactor10 = 0.0F;
         float max11;
         float shinProgressFactor12;
         if (swingProgress < 0.3F) {
            float shinProgressValue15 = getAmount3(swingProgress / 0.3F) * 2.0F;
            shinProgressFactor10 = shinProgressValue15 < 1.0F ? shinProgressValue15 : 2.0F - shinProgressValue15;
            max11 = Math.max(0.0F, shinProgressValue15 - 1.0F);
            shinProgressFactor12 = 0.0F;
         } else {
            max11 = 1.0F - getAmount3((swingProgress - 0.3F) / 0.2F);
            shinProgressFactor12 = swingProgress < 0.6F ? getAmount3((swingProgress - 0.3F) / 0.2F) : 1.0F - getAmount3((swingProgress - 0.6F) / 0.4F);
         }

         float shinProgressFactor13 = 1.0F - shinProgressFactor10 - max11 - shinProgressFactor12;
         shinProgressTotal5 = shinProgressFactor13 * shinProgressTotal5 + shinProgressFactor10 * table[0] + max11 * shinProgressTable[0] + shinProgressFactor12 * tungPupilTable[0];
         dinForeValue = shinProgressFactor13 * dinForeValue + shinProgressFactor10 * table[1] + max11 * shinProgressTable[1] + shinProgressFactor12 * tungPupilTable[1];
         shinProgressFactor4 = shinProgressFactor13 * shinProgressFactor4 + shinProgressFactor10 * table[2] + max11 * shinProgressTable[2] + shinProgressFactor12 * tungPupilTable[2];
         foreShinValue = shinProgressFactor13 * foreShinValue + shinProgressFactor10 * table[3] + max11 * shinProgressTable[3] + shinProgressFactor12 * tungPupilTable[3];
         shinProgressValue8 = shinProgressFactor10 * table[4] + max11 * shinProgressTable[4] + shinProgressFactor12 * tungPupilTable[4];
         shinProgressValue9 = shinProgressFactor10 * table[5] + max11 * shinProgressTable[5] + shinProgressFactor12 * tungPupilTable[5];
         shinProgressTotal6 = shinProgressFactor13 * shinProgressTotal6 + shinProgressFactor10 * POSE_WEIGHTS_A[0] + max11 * POSE_WEIGHTS_B[0] + shinProgressFactor12 * POSE_WEIGHTS_C[0];
         shinProgressFactor5 = shinProgressFactor13 * shinProgressFactor5 + shinProgressFactor10 * POSE_WEIGHTS_A[1] + max11 * POSE_WEIGHTS_B[1] + shinProgressFactor12 * POSE_WEIGHTS_C[1];
         shinProgressFactor6 = shinProgressFactor13 * shinProgressFactor6 + shinProgressFactor10 * POSE_WEIGHTS_A[2] + max11 * POSE_WEIGHTS_B[2] + shinProgressFactor12 * POSE_WEIGHTS_C[2];
         shinProgressFactor7 = shinProgressFactor13 * shinProgressFactor7 + shinProgressFactor10 * POSE_WEIGHTS_A[3] + max11 * POSE_WEIGHTS_B[3] + shinProgressFactor12 * POSE_WEIGHTS_C[3];
         shinProgressValue10 = shinProgressFactor10 * POSE_WEIGHTS_A[4] + max11 * POSE_WEIGHTS_B[4] + shinProgressFactor12 * POSE_WEIGHTS_C[4];
         shinProgressValue11 = shinProgressFactor10 * POSE_WEIGHTS_A[5] + max11 * POSE_WEIGHTS_B[5] + shinProgressFactor12 * POSE_WEIGHTS_C[5];
         float shinProgressFactor14 = max11 + 0.5F * shinProgressFactor10;
         float shinProgressValue16 = shinProgressFactor14 * OFFSET_POSE_A[0] + shinProgressFactor12 * OFFSET_POSE_B[0];
         float shinProgressValue17 = shinProgressFactor14 * OFFSET_POSE_A[1] + shinProgressFactor12 * OFFSET_POSE_B[1];
         float shinProgressValue18 = shinProgressFactor14 * OFFSET_POSE_A[2] + shinProgressFactor12 * OFFSET_POSE_B[2];
         foreShinScale.getAmountsForeShin2(this.foreR, shinProgressValue16, shinProgressValue17, shinProgressValue18);
         foreShinScale.getAmountsForeShin2(this.foreL, -shinProgressValue16, shinProgressValue17, shinProgressValue18);
         float shinProgressFactor15 = swingProgress > 0.45F && swingProgress < 0.65F ? (float)Math.sin((swingProgress - 0.45F) / 0.2F * Math.PI) : 0.0F;
         float shinProgressValue19;
         if (swingProgress < 0.3F) {
            shinProgressValue19 = 0.12F * getAmount3(swingProgress / 0.3F);
         } else if (swingProgress < 0.6F) {
            shinProgressValue19 = 0.12F - 0.34F * getAmount3((swingProgress - 0.3F) / 0.15F);
         } else {
            shinProgressValue19 = -0.22F * (1.0F - getAmount3((swingProgress - 0.6F) / 0.4F));
         }

         foreShinScale.getAmountsBoneCount(this.armL, shinProgressValue19, 0.0F, 0.0F);
         foreShinScale.getAmountsForeShin2(this.armL, 0.0F, -0.35F * shinProgressFactor15, 0.0F);
         if (swingProgress > 0.45F && swingProgress < 0.6F) {
            float sin = (float)Math.sin((swingProgress - 0.45F) / 0.15F * Math.PI);
            foreShinScale.getAmountsForForeEarFloat(this.armL, 1.0F + 0.05F * sin, 1.0F - 0.06F * sin, 1.0F + 0.05F * sin);
         }
      }

      if (poseProgress.sneakingPose) {
         foreShinScale.getAmountsBoneCount(this.armL, -0.3F, 0.0F, 0.0F);
         shinProgressTotal5 += 0.3F;
         shinProgressTotal6 += 0.3F;
         foreShinValue += 0.3F;
         shinProgressTotal += 0.45F;
         shinProgressTotal2 += 0.45F;
         tungPupilValue -= 0.9F;
         shinProgressTotal4 -= 0.9F;
         amountForFloat = 0.0F;
         amountForFloat3 = 0.0F;
      }

      if (poseProgress.gliding || poseProgress.swimming) {
         shinProgressTotal5 = -0.4F;
         dinForeValue = 0.0F;
         shinProgressFactor4 = 0.9F;
         shinProgressTotal6 = -0.4F;
         shinProgressFactor5 = 0.0F;
         shinProgressFactor6 = -0.9F;
      } else if (poseProgress.vehicle && poseProgress.swingProgress <= 0.0F) {
         shinProgressTotal5 = 0.35F;
         dinForeValue = 0.0F;
         shinProgressFactor4 = 0.12F;
         foreShinValue = 2.3F;
         shinProgressValue8 = 0.5F;
         shinProgressValue9 = 0.28F;
         shinProgressTotal6 = 0.5F;
         shinProgressFactor5 = 0.0F;
         shinProgressFactor6 = -0.1F;
         shinProgressFactor7 = 0.6F;
      }

      foreShinScale.getAmounts(this.foreR, shinProgressTotal5, dinForeValue, shinProgressFactor4);
      foreShinScale.getAmounts(this.foreL, shinProgressTotal6, shinProgressFactor5, shinProgressFactor6);
      foreShinScale.getAmounts(this.handR, foreShinValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.handL, shinProgressFactor7, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinProgressForeEar, shinProgressValue8, 0.0F, shinProgressValue9);
      foreShinScale.getAmounts(this.classShinProgress, shinProgressValue10, 0.0F, shinProgressValue11);
      foreShinScale.getAmounts(this.shinR, shinProgressTotal, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinL, shinProgressTotal2, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footR, tungPupilValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.footL, shinProgressTotal4, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.classShinFoot, amountForFloat - shinProgressTotal - tungPupilValue, 0.0F, 0.0F);
      foreShinScale.getAmounts(this.shinFootForeEar, amountForFloat3 - shinProgressTotal2 - shinProgressTotal4, 0.0F, 0.0F);
      if (poseProgress.vehicle) {
         foreShinScale.getAmounts(this.shinR, 1.35F, 0.0F, 0.3F);
         foreShinScale.getAmounts(this.shinL, 1.35F, 0.0F, -0.3F);
         foreShinScale.getAmounts(this.footR, -0.55F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.footL, -0.55F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.classShinFoot, -0.5F, 0.0F, 0.0F);
         foreShinScale.getAmounts(this.shinFootForeEar, -0.5F, 0.0F, 0.0F);
      } else if (!poseProgress.gliding && !poseProgress.swimming && !poseProgress.inPose) {
         float min20 = Math.min(getAmounts(shinProgressTotal, tungPupilValue, amountForFloat), getAmounts(shinProgressTotal2, shinProgressTotal4, amountForFloat3));
         foreShinScale.getAmountsForeShin2(this.legL, 0.0F, -min20, 0.0F);
      }

      if (poseProgress.shinProgressActive) {
         foreShinScale.getAmountsForForeEarFloat(this.armL, 1.07F, 0.93F, 1.07F);
      }
   }

   private void handlePupilArm(LatheLoft latheLoft) {
      ShinBrr shinBrr = latheLoft.getMax(16513781);
      ShinBrr max2 = latheLoft.getMax(1708557);
      ShinBrr max3 = latheLoft.getMax(16777215);

      for (int i = -1; i <= 1; i += 2) {
         float pupilArmValue = i * 1.85F;
         float armTrunkValue = getAmount2(pupilArmValue);
         float degrees = getAmount(pupilArmValue);
         float sin = (float)Math.sin(Math.toRadians(degrees));
         float cos = (float)Math.cos(Math.toRadians(degrees));
         latheLoft.getLatheLoftForForeEar(this.armL);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(pupilArmValue, 29.9F, armTrunkValue).getLatheLoft4(degrees);
         latheLoft.getLatheLoft(0.0F, 0.0F, 0.0F, 1.42F, 1.6F, 0.78F, 16, shinBrr);
         latheLoft.getDequeArray();
         latheLoft.getLatheLoftForForeEar(i > 0 ? this.pupilR : this.pupilL);
         latheLoft.getDequeArrayArrayDeque().getLatheLoft(pupilArmValue, 29.9F, armTrunkValue).getLatheLoft4(degrees);
         latheLoft.getLatheLoft(-i * 0.31F, -0.05F, -0.62F, 0.5F, 0.52F, 0.24F, 8, max2);
         latheLoft.getLatheLoft((0.15F + 0.84F * sin) / cos, 0.22F, -0.84F, 0.16F, 6, max3);
         latheLoft.getDequeArray();
      }

      latheLoft.getLatheLoftForForeEar(this.armL);
      latheLoft.getLatheLoft(0.0F, 28.300001F, -3.85F, 0.6F, 0.9F, 0.48F, 12, latheLoft.getMax(13933666));
   }

   private static void handleBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      bonecaAmbalabu.getBonecaAmbalabu(ForeShinUtils.getValueForInt(14925967));
      float bonecaAmbalabuValue = bonecaAmbalabu.ambalabuBonecaValue * 0.52F;
      float amountForFloat = bonecaAmbalabu.bonecaValue * 0.48F;

      for (int i = 1; i <= 9; i++) {
         float classDivisor = i * bonecaAmbalabu.ambalabuBonecaValue * 0.055F;
         bonecaAmbalabu.getClass022(bonecaAmbalabuValue, amountForFloat, classDivisor * 1.04F, classDivisor, 1.1F, ForeShinUtils.getValueForIntFloat(12620124, 0.75F));
      }

      bonecaAmbalabu.getClass02(bonecaAmbalabuValue, amountForFloat, bonecaAmbalabu.ambalabuBonecaValue * 0.92F, bonecaAmbalabu.bonecaValue * 0.3F, 1.2F, ForeShinUtils.getValueForIntFloat(11041354, 0.6F));
      bonecaAmbalabu.getBonecaAmbalabu(0.05F, 6.0F, 13);
   }

   @Override
   public float getAmountFloat() {
      return 1.2F;
   }

   private void handleBonecaAmbalabuForBonecaAmbalabu(BonecaAmbalabu bonecaAmbalabu) {
      float bonecaAmbalabuValue = bonecaAmbalabu.bonecaValue * 0.38F;
      float hashHuntBound = bonecaAmbalabu.bonecaValue * 0.62F;

      for (int i = 0; i <= 1; i++) {
         float classBound = i * bonecaAmbalabu.ambalabuBonecaValue;
         bonecaAmbalabu.getClass02(classBound, bonecaAmbalabuValue - 1.0F, classBound, hashHuntBound + 1.0F, 6.5F, ForeShinUtils.getValueForIntFloat(9067056, 0.22F));
         bonecaAmbalabu.getClass02(classBound - 1.9F, bonecaAmbalabuValue + 1.2F, classBound - 1.9F, hashHuntBound - 1.2F, 1.0F, ForeShinUtils.getValueForIntFloat(16770748, 0.4F));
         bonecaAmbalabu.getClass02(classBound + 1.9F, bonecaAmbalabuValue + 1.2F, classBound + 1.9F, hashHuntBound - 1.2F, 1.0F, ForeShinUtils.getValueForIntFloat(9067056, 0.5F));
         bonecaAmbalabu.getClass02(classBound, bonecaAmbalabuValue, classBound, hashHuntBound, 1.8F, ForeShinUtils.getValueForIntFloat(3810326, 0.6F));
      }

      for (int j = -1; j <= 1; j += 2) {
         float classValue = getBonecaAmbalabu(bonecaAmbalabu, j * 1.85F);
         float amountForFloat = getAmount(bonecaAmbalabu, 29.9F);
         bonecaAmbalabu.getClass022(classValue, amountForFloat, 13.5F, 15.0F, ForeShinUtils.getValueForIntFloat(9067056, 0.85F));
         float bonecaAmbalabu3 = getBonecaAmbalabu(bonecaAmbalabu, j * 2.0F);
         float bonecaAmbalabuValue5 = getAmount(bonecaAmbalabu, 32.1F);
         float bonecaAmbalabu4 = getBonecaAmbalabu(bonecaAmbalabu, j * 0.55F);
         float bonecaAmbalabu5 = getBonecaAmbalabu(bonecaAmbalabu, j * 3.4F);
         bonecaAmbalabu.getClassForFloatInt(bonecaAmbalabu4, bonecaAmbalabuValue5 + 1.6F, bonecaAmbalabu3, bonecaAmbalabuValue5 - 3.5F, bonecaAmbalabu5, bonecaAmbalabuValue5 + 1.0F, 5.2F, ForeShinUtils.getValueForInt(2825492));
         bonecaAmbalabu.getClassForFloatInt(bonecaAmbalabu4, bonecaAmbalabuValue5 + 4.6F, bonecaAmbalabu3, bonecaAmbalabuValue5 - 0.8F, bonecaAmbalabu5, bonecaAmbalabuValue5 + 3.4F, 1.6F, ForeShinUtils.getValueForIntFloat(7029798, 0.6F));
      }

      float classFactor = getBonecaAmbalabu(bonecaAmbalabu, 1.75F);
      float ambalabuBonecaValue = getBonecaAmbalabu(bonecaAmbalabu, -1.75F);
      float bonecaAmbalabuValue6 = getAmount(bonecaAmbalabu, 26.7F);
      bonecaAmbalabu.getClassForFloatInt(classFactor, bonecaAmbalabuValue6 - 3.5F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabuValue6 + 5.5F, ambalabuBonecaValue, bonecaAmbalabuValue6 - 3.5F, 2.6F, ForeShinUtils.getValueForInt(4860442));
      bonecaAmbalabu.getClassForFloatInt(
         classFactor + 1.0F, bonecaAmbalabuValue6 - 1.5F, bonecaAmbalabu.ambalabuBonecaValue * 0.5F, bonecaAmbalabuValue6 + 8.5F, ambalabuBonecaValue - 1.0F, bonecaAmbalabuValue6 - 1.5F, 1.2F, ForeShinUtils.getValueForIntFloat(15780244, 0.45F)
      );
      bonecaAmbalabu.getClass022(classFactor - 0.5F, bonecaAmbalabuValue6 - 3.8F, 1.6F, 1.6F, ForeShinUtils.getValueForInt(4860442));
      bonecaAmbalabu.getClass022(ambalabuBonecaValue + 0.5F, bonecaAmbalabuValue6 - 3.8F, 1.6F, 1.6F, ForeShinUtils.getValueForInt(4860442));
      bonecaAmbalabu.getClass022(bonecaAmbalabu.ambalabuBonecaValue * 0.5F, getAmount(bonecaAmbalabu, 27.6F), 3.4F, 1.4F, ForeShinUtils.getValueForIntFloat(10381882, 0.35F));
   }

   private static void handleForLatheLoftFloatShinBrr(LatheLoft latheLoft, float tungPupilValue, ShinBrr shinBrr) {
      float tungPupilValue2 = tungPupilValue * 4.6F;
      float tungPupilValue3 = tungPupilValue * 4.7999997F;
      latheLoft.getLatheLoft(new float[]{tungPupilValue2, 19.3F, -0.2F, tungPupilValue3, 17.0F, -0.45F, tungPupilValue3, 14.7F, -0.65F}, new float[]{0.55F, 0.5F, 0.44F}, 8, shinBrr, false);
   }

   private static float getAmountForFloat(float tungPupilValue, float tungPupilFactor) {
      float angle = (float)Math.IEEEremainder(tungPupilValue, Math.PI * 2);
      float tungPupilFactor2 = (angle + 0.25F) / 0.4F;
      return tungPupilFactor * (0.32F * (float)Math.exp(-tungPupilFactor2 * tungPupilFactor2) - 0.3F * Math.max(0.0F, -((float)Math.sin(angle))));
   }

   private static float getAmount2(float tungPupilFactor) {
      return -((float)Math.sqrt(16.81F - tungPupilFactor * tungPupilFactor)) + 0.18F;
   }

   private static float getAmount(float tungPupilValue, float tungPupilValue2, float tungPupilFactor) {
      return tungPupilValue + (tungPupilValue2 - tungPupilValue) * tungPupilFactor;
   }

   @Override
   protected void handleShinFoot(LatheLoft latheLoft) {
      this.legL = latheLoft.getClassForStringForeEar("body", latheLoft.getClassForeEarList(), 0.0F, 15.2F, 0.0F);
      this.armL = latheLoft.getClassForStringForeEar("log", this.legL, 0.0F, 15.2F, 0.0F);
      this.pupilR = this.getArm(latheLoft, "pupil_r", 1.0F);
      this.pupilL = this.getArm(latheLoft, "pupil_l", -1.0F);
      this.foreR = latheLoft.getClassForStringForeEar("arm_r", this.armL, 4.2F, 24.15F, 0.0F);
      this.handR = latheLoft.getClassForStringForeEar("fore_r", this.foreR, 4.6F, 19.3F, -0.2F);
      this.shinProgressForeEar = latheLoft.getClassForStringForeEar("hand_r", this.handR, 4.7999997F, 14.8F, -0.65F);
      this.foreL = latheLoft.getClassForStringForeEar("arm_l", this.armL, -4.2F, 24.15F, 0.0F);
      this.handL = latheLoft.getClassForStringForeEar("fore_l", this.foreL, -4.6F, 19.3F, -0.2F);
      this.classShinProgress = latheLoft.getClassForStringForeEar("hand_l", this.handL, -4.7999997F, 14.8F, -0.65F);
      this.shinR = latheLoft.getClassForStringForeEar("leg_r", this.legL, 1.8F, 15.599999F, 0.0F);
      this.footR = latheLoft.getClassForStringForeEar("shin_r", this.shinR, 1.8F, 8.2F, -0.2F);
      this.classShinFoot = latheLoft.getClassForStringForeEar("foot_r", this.footR, 1.8F, 1.2F, 0.0F);
      this.shinL = latheLoft.getClassForStringForeEar("leg_l", this.legL, -1.8F, 15.599999F, 0.0F);
      this.footL = latheLoft.getClassForStringForeEar("shin_l", this.shinL, -1.8F, 8.2F, -0.2F);
      this.shinFootForeEar = latheLoft.getClassForStringForeEar("foot_l", this.footL, -1.8F, 1.2F, 0.0F);
      latheLoft.getLatheLoft(this.foreR);
      LoftLatheFlag.Region region = latheLoft.getMax(216, 160);
      BonecaAmbalabu classBonecaAmbalabu = region.getClassBonecaAmbalabu();
      handle(classBonecaAmbalabu);
      LoftLatheFlag.Region max2 = latheLoft.getMax(216, 11);
      handleBonecaAmbalabu(classBonecaAmbalabu, max2.getClassBonecaAmbalabu(), max2.shinFootValue - 1);
      LoftLatheFlag.Region max3 = latheLoft.getMax(216, 12);
      handleBonecaAmbalabu(classBonecaAmbalabu, max3.getClassBonecaAmbalabu(), region.shinFootValue - 1);
      this.handleBonecaAmbalabuForBonecaAmbalabu(classBonecaAmbalabu);
      LoftLatheFlag.Region max4 = latheLoft.getMax(72, 72);
      handleBonecaAmbalabu(max4.getClassBonecaAmbalabu());
      LoftLatheFlag.Region max5 = latheLoft.getMax(48, 96);
      max5.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(13144666))
         .getBonecaAmbalabu(0.05F, 10.0F, 41)
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(10250808), 5.0F, 0.3F, 0.45F, 43);
      LoftLatheFlag.Region max6 = latheLoft.getMax(32, 64);
      max6.getClassBonecaAmbalabu()
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(13605478))
         .getBonecaAmbalabu(ForeShinUtils.getValueForInt(11565637), 6.0F, 0.15F, 0.3F, 51)
         .getBonecaAmbalabu(0.04F, 4.0F, 52);
      LoftLatheFlag.Region max7 = latheLoft.getMax(4, 16);
      BonecaAmbalabu classBonecaAmbalabu3 = max7.getClassBonecaAmbalabu();

      for (int i = 0; i < classBonecaAmbalabu3.bonecaValue; i++) {
         int shinFootValue = ForeShinUtils.getValue2(
            ForeShinUtils.getValueForInt(14592117), ForeShinUtils.getValueForInt(15120522), getAmount3((i + 0.5F) / classBonecaAmbalabu3.bonecaValue * 2.6F - 1.5F)
         );

         for (int j = 0; j < classBonecaAmbalabu3.ambalabuBonecaValue; j++) {
            classBonecaAmbalabu3.getBonecaAmbalabu(j, i, shinFootValue);
         }
      }

      ShinBrr classForRegion = latheLoft.getClassForRegion(region);
      ShinBrr classForRegion5 = latheLoft.getClassForRegion(max4);
      ShinBrr classForRegion6 = latheLoft.getClassForRegion(max6);
      ShinBrr shinBrr = latheLoft.getMax(13605478);
      ShinBrr shinBrr2 = latheLoft.getMax(14592117);
      ShinBrr classForRegion7 = latheLoft.getClassForRegion(max7);
      ShinBrr classForRegion8 = latheLoft.getClassForRegion(max5);
      ShinBrr shinBrr3 = latheLoft.getMax(12158290);
      latheLoft.getLatheLoftForForeEar(this.armL);
      latheLoft.getLatheLoft(
         new float[]{3.35F, 3.55F, 3.8F, 3.98F, 4.08F, 4.1F},
         new float[]{14.599999F, 14.7F, 14.88F, 15.099999F, 15.4F, 15.8F},
         24,
         latheLoft.getClassForRegion(max3),
         latheLoft.getClassForRegionInt(max4, 14275272),
         null
      );
      latheLoft.getLatheLoft(new float[]{4.1F, 4.1F}, new float[]{15.8F, 35.2F}, 24, classForRegion, null, null);
      latheLoft.getLatheLoft(
         new float[]{4.1F, 4.08F, 3.98F, 3.8F, 3.58F, 3.3999999F},
         new float[]{35.2F, 35.45F, 35.7F, 35.920002F, 36.100002F, 36.2F},
         24,
         latheLoft.getClassForRegion(max2),
         null,
         classForRegion5
      );
      this.handlePupilArm(latheLoft);
      latheLoft.getLatheLoftForForeEar(this.foreR);
      handle(latheLoft, 1.0F, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.handR);
      handleForLatheLoftFloatShinBrr(latheLoft, 1.0F, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.shinProgressForeEar);
      handle(latheLoft, 1.0F, shinBrr, true);
      latheLoft.getDequeArrayArrayDeque().getLatheLoft(4.65F, 14.2F, -0.9F).getLatheLoftLatheLoftAmountLoft(6.0F).getLatheLoft(4.0F);
      latheLoft.getLatheLoft(
         new float[]{0.6F, 1.02F, 1.08F, 1.0F, 0.62F, 0.42F, 0.38F, 0.56F, 0.58F, 0.4F},
         new float[]{-14.9F, -14.55F, -13.8F, -9.4F, -4.7F, -1.4F, 1.3F, 1.55F, 1.75F, 2.0F},
         12,
         classForRegion8,
         shinBrr3,
         shinBrr3
      );
      latheLoft.getDequeArray();
      latheLoft.getLatheLoftForForeEar(this.foreL);
      handle(latheLoft, -1.0F, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.handL);
      handleForLatheLoftFloatShinBrr(latheLoft, -1.0F, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.classShinProgress);
      handle(latheLoft, -1.0F, shinBrr, false);
      latheLoft.getLatheLoftForForeEar(this.shinR);
      handle4(latheLoft, 1.0F, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.footR);
      handleForLatheLoftFloat(latheLoft, 1.0F, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.classShinFoot);
      handle(latheLoft, 1.0F, shinBrr, shinBrr2, classForRegion7);
      latheLoft.getLatheLoftForForeEar(this.shinL);
      handle4(latheLoft, -1.0F, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.footL);
      handleForLatheLoftFloat(latheLoft, -1.0F, classForRegion6);
      latheLoft.getLatheLoftForForeEar(this.shinFootForeEar);
      handle(latheLoft, -1.0F, shinBrr, shinBrr2, classForRegion7);
   }

   @Override
   public String formatTungDin() {
      return "tung_tung_tung_sahur";
   }

   private static void handle4(LatheLoft latheLoft, float tungPupilValue, ShinBrr shinBrr) {
      float latheLoftValue = tungPupilValue * 1.8F;
      latheLoft.getLatheLoft(new float[]{latheLoftValue, 15.5F, 0.0F, latheLoftValue, 11.8F, -0.1F, latheLoftValue, 8.2F, -0.2F}, new float[]{0.78F, 0.74F, 0.7F}, 8, shinBrr, true);
      latheLoft.getLatheLoft(latheLoftValue, 8.2F, -0.2F, 0.76F, 6, shinBrr);
   }

   @Override
   public String formatTungDinString() {
      return "Tung Tung Tung Sahur";
   }

   private static float getAmount3(float tungPupilFactor) {
      tungPupilFactor = tungPupilFactor < 0.0F ? 0.0F : (tungPupilFactor > 1.0F ? 1.0F : tungPupilFactor);
      return tungPupilFactor * tungPupilFactor * (3.0F - 2.0F * tungPupilFactor);
   }
}
