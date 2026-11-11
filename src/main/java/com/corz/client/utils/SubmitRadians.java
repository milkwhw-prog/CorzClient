package com.corz.client.utils;

import java.util.Arrays;

public final class SubmitRadians {
   private final float[] submitRadiansTable = new float[105];
   private float nearestDistance = Float.POSITIVE_INFINITY;

   public float getMax(float submitRadiansValue) {
      float submitRadiansLimit = (submitRadiansValue - -14.0F) / 0.5F;
      if (submitRadiansLimit >= 0.0F && !(submitRadiansLimit > 104.0F)) {
         int index = (int)submitRadiansLimit;
         if (index >= 104) {
            return this.submitRadiansTable[104];
         } else {
            float submitRadiansBound = this.submitRadiansTable[index];
            float rocketsElytraBound = this.submitRadiansTable[index + 1];
            return !(submitRadiansBound <= -1000000.0F) && !(rocketsElytraBound <= -1000000.0F) ? submitRadiansBound + (rocketsElytraBound - submitRadiansBound) * (submitRadiansLimit - index) : Math.max(submitRadiansBound, rocketsElytraBound);
         }
      } else {
         return -1000000.0F;
      }
   }

   public float getAmount() {
      return this.nearestDistance;
   }

   public void handleAmounts(float[] submitRadiansValues, float[] submitRadiansValues2, int submitRadiansLimit, float submitRadiansBound) {
      float positiveInfinity = Float.POSITIVE_INFINITY;
      float negativeInfinity = Float.NEGATIVE_INFINITY;

      for (int i = 0; i < submitRadiansLimit; i++) {
         if (!Float.isFinite(submitRadiansValues[i]) || !Float.isFinite(submitRadiansValues2[i])) {
            return;
         }

         positiveInfinity = Math.min(positiveInfinity, submitRadiansValues[i]);
         negativeInfinity = Math.max(negativeInfinity, submitRadiansValues[i]);
      }

      float min2 = Math.min(negativeInfinity, submitRadiansBound);
      if (min2 >= positiveInfinity) {
         int submitRadiansValue = Math.max(0, (int)Math.ceil((positiveInfinity - -14.0F) / 0.5F));
         int min3 = Math.min(104, (int)Math.floor((min2 - -14.0F) / 0.5F));

         for (int j = submitRadiansValue; j <= min3; j++) {
            float submitRadiansValue2 = -14.0F + j * 0.5F;
            float submitRadiansLimit4 = -1000000.0F;

            for (int k = 0; k < submitRadiansLimit; k++) {
               float submitRadiansValue3 = submitRadiansValues[k];
               float pilotStatusBound = submitRadiansValues2[k];

               for (int m = k; m < submitRadiansLimit; m++) {
                  float submitRadiansValue4 = submitRadiansValues[m];
                  float rocketsElytraBound = submitRadiansValues2[m];
                  float max5;
                  if (submitRadiansValue3 == submitRadiansValue4) {
                     if (submitRadiansValue2 != submitRadiansValue3) {
                        continue;
                     }

                     max5 = Math.max(pilotStatusBound, rocketsElytraBound);
                  } else {
                     float submitRadiansLimit6 = (submitRadiansValue2 - submitRadiansValue3) / (submitRadiansValue4 - submitRadiansValue3);
                     if (submitRadiansLimit6 < 0.0F || submitRadiansLimit6 > 1.0F) {
                        continue;
                     }

                     max5 = pilotStatusBound + (rocketsElytraBound - pilotStatusBound) * submitRadiansLimit6;
                  }

                  if (max5 > submitRadiansLimit4) {
                     submitRadiansLimit4 = max5;
                  }
               }
            }

            if (submitRadiansLimit4 > -1000000.0F) {
               this.submitRadiansTable[j] = Math.max(this.submitRadiansTable[j], submitRadiansLimit4 - 0.5F);
            }
         }
      }
   }

   public void handleAmount(float submitRadiansValue) {
      this.nearestDistance = Float.isFinite(submitRadiansValue) ? submitRadiansValue : Float.POSITIVE_INFINITY;
   }

   public void handleAmountsAmount(SubmitRadians submitRadians) {
      System.arraycopy(submitRadians.submitRadiansTable, 0, this.submitRadiansTable, 0, 105);
      this.nearestDistance = submitRadians.nearestDistance;
   }

   public void handle(float submitRadiansValue, float submitRadiansValue2, float submitRadiansValue3, float submitRadiansValue4, float submitRadiansBound) {
      float[] floats = new float[]{submitRadiansValue, submitRadiansValue, submitRadiansValue3, submitRadiansValue3};
      float[] floats2 = new float[]{submitRadiansValue2, submitRadiansValue4, submitRadiansValue2, submitRadiansValue4};
      this.handleAmounts(floats, floats2, 4, submitRadiansBound);
   }

   public void updateAmountsAmount() {
      Arrays.fill(this.submitRadiansTable, -1000000.0F);
      this.nearestDistance = Float.POSITIVE_INFINITY;
   }

   public SubmitRadians() {
      this.updateAmountsAmount();
   }

   public static void handle(SubmitRadians submitRadians) {
      submitRadians.updateAmountsAmount();
      submitRadians.handle(-0.25F, -2.25F, 12.0F, 2.25F, Float.POSITIVE_INFINITY);
      submitRadians.handle(12.0F, -2.25F, 24.25F, 2.25F, Float.POSITIVE_INFINITY);
      submitRadians.handle(-8.5F, -4.5F, 0.0F, 4.5F, 0.0F);
   }
}
