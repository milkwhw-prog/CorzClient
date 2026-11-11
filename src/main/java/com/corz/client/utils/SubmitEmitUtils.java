package com.corz.client.utils;
import com.corz.client.utils.AmountsAmountUpdate;
import com.corz.client.utils.SubmitRadians;

public final class SubmitEmitUtils {

   static float getAmount(float submitEmitLimit) {
      return submitEmitLimit < 0.0F ? 0.0F : (submitEmitLimit > 1.0F ? 1.0F : submitEmitLimit);
   }

   public static void handleAmountsAmount(
      AmountsAmountUpdate amountsAmountUpdate, float submitEmitValue, float submitEmitValue2, float submitEmitValue3, float submitEmitValue4, float[] submitEmitValues, float[] submitEmitValues2
   ) {
      float submitEmitFactor = Float.isFinite(submitEmitValue) ? getAmount(submitEmitValue) : 1.0F;
      float routedPartialValue = getAmount(submitEmitFactor, amountsAmountUpdate.lastUpdateCount, amountsAmountUpdate.updateCount);
      float submitEmitValue7 = getAmount(submitEmitFactor, amountsAmountUpdate.lastWavePhase, amountsAmountUpdate.wavePhase);
      float submitEmitBound = getAmount(submitEmitFactor, amountsAmountUpdate.updateAmount, amountsAmountUpdate.waveIntensity);
      if (!Float.isFinite(routedPartialValue)) {
         routedPartialValue = 0.0F;
      }

      if (!Float.isFinite(submitEmitValue7)) {
         submitEmitValue7 = 0.0F;
      }

      if (!Float.isFinite(submitEmitBound)) {
         submitEmitBound = 0.0F;
      }

      float amountForFloat = getAmountForFloat(0.06F, 0.55F, submitEmitBound);
      float submitEmitLimit = (1.0F - amountForFloat) * Math.max(0.0F, getAmountFloat(submitEmitValue4));
      float submitEmitLimit2 = 0.085F * amountForFloat * (0.6F + 0.4F * Math.min(submitEmitBound, 1.0F));
      float submitEmitLimit3 = getAmount(getAmountFloat(submitEmitValue3));
      float angle = getAmountFloat(submitEmitValue2) * 0.6662F;
      float cos = (float)Math.cos(2.0F * angle);
      float cos2 = (float)Math.cos(angle);
      float sin = (float)Math.sin(1.3194689F * routedPartialValue);
      float sin2 = (float)Math.sin(1.0053097F * routedPartialValue + 0.5F);

      for (int i = 0; i < 14; i++) {
         float base = AmountsAmountUpdate.getAmount(i);
         float submitEmitTotal = getAmount(submitEmitFactor, amountsAmountUpdate.lastTickSwingAngles[i], amountsAmountUpdate.swingAngles[i]);
         float submitEmitTotal2 = getAmount(submitEmitFactor, amountsAmountUpdate.lastTickSideAngles[i], amountsAmountUpdate.sideAngles[i]);
         if (submitEmitLimit2 > 0.0F) {
            submitEmitTotal += submitEmitLimit2 * (float)Math.pow(base, 1.3) * (float)Math.sin(submitEmitValue7 - 2.6F * base);
            submitEmitTotal2 += submitEmitLimit2 * 0.45F * base * (float)Math.sin(0.73F * submitEmitValue7 - 2.0F * base + 1.1F);
         }

         if (submitEmitLimit > 0.0F) {
            float sin3 = (float)Math.sin(2.3247786F * routedPartialValue + 1.3F - 1.2F * base);
            submitEmitTotal += 0.04F * submitEmitLimit * base * (0.6F * sin + 0.4F * sin3);
            submitEmitTotal2 += 0.027999999F * submitEmitLimit * base * sin2;
         }

         if (submitEmitLimit3 > 0.0F) {
            submitEmitTotal += 0.05F * submitEmitLimit3 * (0.35F + 0.65F * base) * cos;
            submitEmitTotal2 += 0.04F * submitEmitLimit3 * base * cos2;
         }

         submitEmitValues[i] = submitEmitTotal;
         submitEmitValues2[i] = submitEmitTotal2;
      }
   }

   public static void handle(float[] submitEmitValues, float[] submitEmitValues2) {
      for (int i = 0; i < 14; i++) {
         submitEmitValues[i] = 0.0F;
         submitEmitValues2[i] = 0.0F;
      }
   }

   private static void handle(float[] submitEmitValues, float[] submitEmitValues2, SubmitRadians submitRadians) {
      float submitEmitTotal = 0.0F;
      float submitEmitTotal2 = 2.0F;
      float submitEmitValue = 0.05F;
      float submitEmitValue2 = 0.0F;
      float submitEmitValue3 = submitRadians.getAmount();

      for (int i = 0; i < 14; i++) {
         float updateLimit = submitEmitValues[i];
         float routedPartialValue = submitEmitValues2[i];
         if (!Float.isFinite(updateLimit)) {
            updateLimit = submitEmitValue;
         }

         if (!Float.isFinite(routedPartialValue)) {
            routedPartialValue = submitEmitValue2;
         }

         float amountFloat = AmountsAmountUpdate.getAmountFloat(i);
         float angle = AmountsAmountUpdate.getAmount(routedPartialValue, -amountFloat, amountFloat);
         if (i > 0) {
            angle = AmountsAmountUpdate.getAmount(angle, submitEmitValue2 - 0.06F, submitEmitValue2 + 0.06F);
         }

         float angle2 = AmountsAmountUpdate.getAmount(updateLimit, -0.45F, AmountsAmountUpdate.getAmountForInt(i));
         if (i > 0) {
            angle2 = AmountsAmountUpdate.getAmount(angle2, submitEmitValue - 0.6F, submitEmitValue + 0.6F);
         }

         float submitEmitFactor = 1.1428572F * (float)Math.cos(angle);

         for (int j = 0; j < 4; j++) {
            boolean submitEmitFlag = false;
            float submitRadiansValue = submitEmitTotal + submitEmitFactor * (float)Math.cos(angle2);
            float submitEmitValue8 = submitEmitTotal2 + submitEmitFactor * (float)Math.sin(angle2);
            float max9 = submitRadians.getMax(submitRadiansValue);
            if (submitEmitValue8 < max9 - 1.0E-4F) {
               float submitEmitLimit = (max9 - submitEmitTotal2) / submitEmitFactor;
               if (submitEmitLimit >= 1.0F) {
                  angle2 = (float) (Math.PI / 2);
               } else {
                  float asin = (float)Math.asin(Math.max(submitEmitLimit, -1.0F));
                  angle2 = AmountsAmountUpdate.getAmount(angle2, asin, (float) Math.PI - asin);
               }

               submitEmitFlag = true;
            }

            if (submitEmitTotal + submitEmitFactor * (float)Math.cos(angle2) > submitEmitValue3 + 1.0E-4F) {
               float submitEmitLimit2 = (submitEmitValue3 - submitEmitTotal) / submitEmitFactor;
               float submitEmitLimit3 = submitEmitLimit2 >= 1.0F ? 0.0F : (submitEmitLimit2 <= -1.0F ? (float) Math.PI : (float)Math.acos(submitEmitLimit2));
               if (angle2 < submitEmitLimit3) {
                  angle2 = submitEmitLimit3;
                  submitEmitFlag = true;
               }
            }

            if (!submitEmitFlag) {
               break;
            }
         }

         float angle3 = AmountsAmountUpdate.getAmount(angle2, -0.45F, 1.85F);
         submitEmitValues[i] = angle3;
         submitEmitValues2[i] = angle;
         submitEmitTotal += submitEmitFactor * (float)Math.cos(angle3);
         submitEmitTotal2 += submitEmitFactor * (float)Math.sin(angle3);
         submitEmitValue = angle3;
         submitEmitValue2 = angle;
      }
   }

   static float getAmount(float submitEmitFactor, float submitEmitValue, float submitEmitValue2) {
      return submitEmitValue + (submitEmitValue2 - submitEmitValue) * submitEmitFactor;
   }

   static float getAmountForFloat(float submitEmitValue, float submitEmitValue2, float submitEmitBound) {
      float submitEmitFactor = getAmount((submitEmitBound - submitEmitValue) / (submitEmitValue2 - submitEmitValue));
      return submitEmitFactor * submitEmitFactor * (3.0F - 2.0F * submitEmitFactor);
   }

   private SubmitEmitUtils() {
   }

   public static void handleForFloatArraySubmitRadians(float[] submitEmitValues, float[] submitEmitValues2, SubmitRadians submitRadians) {
      handle(submitEmitValues, submitEmitValues2, submitRadians);

      for (int i = 0; i < 3; i++) {
         boolean submitEmitFlag = false;

         for (int j = 13; j > 0; j--) {
            float submitEmitLimit = Math.min(submitEmitValues[j] - 0.35F, AmountsAmountUpdate.getAmountForInt(j - 1));
            if (submitEmitValues[j] <= (float) (Math.PI / 2) && submitEmitValues[j - 1] < submitEmitLimit) {
               submitEmitValues[j - 1] = submitEmitLimit;
               submitEmitFlag = true;
            }
         }

         if (!submitEmitFlag) {
            return;
         }

         handle(submitEmitValues, submitEmitValues2, submitRadians);
      }
   }

   private static float getAmountFloat(float submitEmitValue) {
      return Float.isFinite(submitEmitValue) ? submitEmitValue : 0.0F;
   }
}
