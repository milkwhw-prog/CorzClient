package com.corz.client.utils;
import com.corz.client.utils.Cos;
import com.corz.client.utils.SubmitRadians;
import com.corz.client.utils.SubmitEmitUtils;

public final class AmountsAmountUpdate {
   private final float[] updateTable;
   float lastUpdateCount;
   private final float[] table;
   final float[] sideVelocities;
   float waveIntensity;
   float updateCount;
   private final float[] swingTargetAngles;
   private final float[] sideTargetAngles;
   private final float[] prevSideAngles;
   final float[] lastTickSwingAngles;
   final float[] swingVelocities;
   final float[] sideAngles;
   final float[] lastTickSideAngles;
   private final float[] prevSwingAngles;
   private final float[] sideAccelerations;
   float lastWavePhase;
   float wavePhase;
   float updateAmount;
   final float[] swingAngles = new float[14];

   public void handleAmountMin(Cos cos, SubmitRadians submitRadians) {
      if (!cos.isAmountCos()) {
         this.handleAmountsMin(new Cos(), submitRadians);
      } else {
         System.arraycopy(this.swingAngles, 0, this.lastTickSwingAngles, 0, 14);
         System.arraycopy(this.sideAngles, 0, this.lastTickSideAngles, 0, 14);
         this.lastUpdateCount = this.updateCount;
         this.lastWavePhase = this.wavePhase;
         this.updateAmount = this.waveIntensity;
         float updateValue = Math.min(getMin(cos.getAmount()), 2.0F);
         float updateFactor = 1.0F - (float)Math.exp(-0.050000004F);

         for (int i = 0; i < 6; i++) {
            this.waveIntensity = this.waveIntensity + (updateValue - this.waveIntensity) * updateFactor;
            this.wavePhase = this.wavePhase + 0.052359883F * (1.6F + 2.4F * Math.min(this.waveIntensity, 1.0F));
            this.updateCount += 0.008333334F;
            this.handleAmounts(cos, submitRadians);
         }

         if (this.wavePhase > 4096.0F) {
            float updateValue2 = (float) (Math.PI * 2) * (float)Math.floor(this.wavePhase / (float) (Math.PI * 2));
            this.wavePhase -= updateValue2;
            this.lastWavePhase -= updateValue2;
         }

         if (this.updateCount > 100000.0F) {
            this.updateCount -= 100000.0F;
            this.lastUpdateCount -= 100000.0F;
         }

         if (!this.isAmountsAmount()) {
            this.handleAmountsMin(new Cos(), submitRadians);
         }
      }
   }

   private static float getAmount(float updateValue) {
      return Float.isFinite(updateValue) ? updateValue : 0.0F;
   }

   public float getAmounts(int index) {
      return this.sideAngles[index];
   }

   public float getMin() {
      return this.waveIntensity;
   }

   private static float getMin(float updateBound) {
      return !Float.isFinite(updateBound) ? 4.0F : Math.min(updateBound, 4.0F);
   }

   public static float getAmount(int updateValue) {
      return updateValue / 13.0F;
   }

   public float getAmountsForInt(int index) {
      return this.swingAngles[index];
   }

   private void handleAmountAmounts(Cos cos) {
      float updateTotal = 2.0F;
      float updateFactor = getMax(cos.quatX);
      float max2 = getMax(cos.quatY);
      float updateValue = getMax(cos.quatZ);
      float updateFactor3 = getAmount(getAmount(cos.cosAmount), -1.0F, 1.0F);
      float updateValue2 = getAmount(getAmount(cos.quatW), -1.0F, 1.0F);
      float updateValue3 = getAmount(getAmount(cos.negSinAngle), -1.0F, 1.0F);
      float updateValue4 = getAmount(getAmount(cos.tangentY), -4.0F, 4.0F);
      float updateValue5 = getAmount(getAmount(cos.tangentX), -4.0F, 4.0F);
      float updateValue6 = getAmount(getAmount(cos.tangentZ), -4.0F, 4.0F);

      for (int i = 0; i < 14; i++) {
         float updateValue7 = getAmount(i);
         float updateFactor4 = 0.5714286F * (float)Math.cos(this.sideAngles[i]);
         float sin = (float)Math.sin(this.swingAngles[i]);
         float updateBound = updateTotal + updateFactor4 * sin;
         updateTotal += 2.0F * updateFactor4 * sin;
         float updateFactor5 = Math.max(updateBound, 0.0F) / 16.0F;
         float updateFactor6 = 0.27F + 0.88F * updateValue7;
         float updateFactor7 = updateFactor3 * updateFactor5;
         float updateFactor8 = updateValue + updateFactor7;
         float distance = (float)Math.sqrt(updateFactor * updateFactor + max2 * max2 + updateFactor8 * updateFactor8);
         float updateFactor9 = (10.0F * distance + 0.2F) * updateFactor6;
         float updateValue8 = updateFactor3 * updateFactor3 * updateFactor5 / 0.08F;
         float x = updateValue2 + updateValue4 + updateFactor9 * updateFactor;
         float y = updateValue3 + updateValue5 + updateFactor9 * max2 + 0.1F + updateValue8;
         float y2 = updateValue6 + updateFactor9 * updateFactor8 + 3.0F * updateFactor6 * updateFactor7;
         float angle = (float)Math.atan2(y, x);
         float updateValue9 = this.swingAngles[i];
         if (angle - updateValue9 > Math.PI) {
            angle -= (float) (Math.PI * 2);
         } else if (updateValue9 - angle > Math.PI) {
            angle += (float) (Math.PI * 2);
         }

         this.swingTargetAngles[i] = getAmount(angle, -0.45F, getAmountForInt(i));
         float pilotStatusBound = (float)Math.sqrt(x * x + y * y);
         float amountFloat = getAmountFloat(i);
         this.sideTargetAngles[i] = getAmount((float)Math.atan2(y2, Math.max(pilotStatusBound, 0.25F)) * (0.25F + 0.75F * updateValue7), -amountFloat, amountFloat);
         float updateValue11 = getAmount((float)Math.sqrt(pilotStatusBound * pilotStatusBound + y2 * y2), 0.35F, 3.0F);
         this.table[i] = 40.0F * updateValue11 * (1.5F - 0.6F * updateValue7);
      }
   }

   public void updateAmountsMin() {
      for (int i = 0; i < 14; i++) {
         this.swingAngles[i] = 0.0F;
         this.sideAngles[i] = 0.0F;
         this.swingVelocities[i] = 0.0F;
         this.sideVelocities[i] = 0.0F;
      }

      this.waveIntensity = 0.0F;
      this.updateAmountsAmount();
   }

   public void handleAmountsMin(Cos cos, SubmitRadians submitRadians) {
      Cos cos2 = cos.isAmountCos() ? cos : new Cos();
      this.handleAmountAmounts(cos2);

      for (int i = 0; i < 14; i++) {
         this.swingAngles[i] = this.swingTargetAngles[i];
         this.sideAngles[i] = this.sideTargetAngles[i];
         this.swingVelocities[i] = 0.0F;
         this.sideVelocities[i] = 0.0F;
      }

      SubmitEmitUtils.handleForFloatArraySubmitRadians(this.swingAngles, this.sideAngles, submitRadians);
      this.waveIntensity = Math.min(getMin(cos2.getAmount()), 2.0F);
      this.updateAmountsAmount();
   }

   public static float getAmountForInt(int updateValue) {
      float squaredDistanceTo = getAmount(updateValue);
      return 1.2F + 0.55F * squaredDistanceTo * (float)Math.sqrt(squaredDistanceTo);
   }

   private boolean isAmountsAmount() {
      if (Float.isFinite(this.updateCount) && Float.isFinite(this.wavePhase) && Float.isFinite(this.waveIntensity)) {
         for (int i = 0; i < 14; i++) {
            if (!Float.isFinite(this.swingAngles[i])
               || !Float.isFinite(this.sideAngles[i])
               || !Float.isFinite(this.swingVelocities[i])
               || !Float.isFinite(this.sideVelocities[i])) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void updateAmountsAmount() {
      System.arraycopy(this.swingAngles, 0, this.lastTickSwingAngles, 0, 14);
      System.arraycopy(this.sideAngles, 0, this.lastTickSideAngles, 0, 14);
      this.lastUpdateCount = this.updateCount;
      this.lastWavePhase = this.wavePhase;
      this.updateAmount = this.waveIntensity;
   }

   public AmountsAmountUpdate() {
      this.swingVelocities = new float[14];
      this.sideAngles = new float[14];
      this.sideVelocities = new float[14];
      this.lastTickSwingAngles = new float[14];
      this.lastTickSideAngles = new float[14];
      this.updateTable = new float[14];
      this.sideAccelerations = new float[14];
      this.prevSwingAngles = new float[14];
      this.prevSideAngles = new float[14];
      this.swingTargetAngles = new float[14];
      this.sideTargetAngles = new float[14];
      this.table = new float[14];
      SubmitRadians submitRadians = new SubmitRadians();
      SubmitRadians.handle(submitRadians);
      this.handleAmountsMin(new Cos(), submitRadians);
   }

   static float getAmount(float updateLimit, float asin, float updateValue) {
      return updateLimit < asin ? asin : (updateLimit > updateValue ? updateValue : updateLimit);
   }

   public static float getAmountFloat(int updateValue) {
      return 0.08F + 0.42F * getAmount(updateValue);
   }

   private static float getMax(float updateBound) {
      return !Float.isFinite(updateBound) ? 0.0F : Math.max(-4.0F, Math.min(4.0F, updateBound));
   }

   private void handleAmounts(Cos cos, SubmitRadians submitRadians) {
      this.handleAmountAmounts(cos);

      for (int i = 0; i < 14; i++) {
         float updateValue = getAmount(i);
         float updateFactor = this.table[i];
         float updateFactor2 = 2.0F * (0.62F - 0.22F * updateValue) * (float)Math.sqrt(updateFactor);
         float updateValue2 = i == 0 ? 0.05F : this.swingAngles[i - 1];
         float updateTotal = 55.0F * (updateValue2 - this.swingAngles[i]);
         float updateValue3 = i == 0 ? 0.0F : this.sideAngles[i - 1];
         float updateTotal2 = 55.0F * (updateValue3 - this.sideAngles[i]);
         if (i < 13) {
            updateTotal += 55.0F * (this.swingAngles[i + 1] - this.swingAngles[i]);
            updateTotal2 += 55.0F * (this.sideAngles[i + 1] - this.sideAngles[i]);
         }

         this.updateTable[i] = updateFactor * (this.swingTargetAngles[i] - this.swingAngles[i]) + updateTotal - updateFactor2 * this.swingVelocities[i];
         this.sideAccelerations[i] = updateFactor * (this.sideTargetAngles[i] - this.sideAngles[i]) + updateTotal2 - updateFactor2 * this.sideVelocities[i];
      }

      for (int j = 0; j < 14; j++) {
         this.swingVelocities[j] = this.swingVelocities[j] + this.updateTable[j] * 0.008333334F;
         this.sideVelocities[j] = this.sideVelocities[j] + this.sideAccelerations[j] * 0.008333334F;
         this.swingAngles[j] = this.swingAngles[j] + this.swingVelocities[j] * 0.008333334F;
         this.sideAngles[j] = this.sideAngles[j] + this.sideVelocities[j] * 0.008333334F;
         this.prevSwingAngles[j] = this.swingAngles[j];
         this.prevSideAngles[j] = this.sideAngles[j];
      }

      SubmitEmitUtils.handleForFloatArraySubmitRadians(this.swingAngles, this.sideAngles, submitRadians);

      for (int k = 0; k < 14; k++) {
         float updateLimit = this.swingAngles[k] - this.prevSwingAngles[k];
         if (updateLimit > 0.0F && this.swingVelocities[k] < 0.0F) {
            this.swingVelocities[k] = 0.0F;
         } else if (updateLimit < 0.0F && this.swingVelocities[k] > 0.0F) {
            this.swingVelocities[k] = 0.0F;
         }

         float updateLimit2 = this.sideAngles[k] - this.prevSideAngles[k];
         if (updateLimit2 > 0.0F && this.sideVelocities[k] < 0.0F) {
            this.sideVelocities[k] = 0.0F;
         } else if (updateLimit2 < 0.0F && this.sideVelocities[k] > 0.0F) {
            this.sideVelocities[k] = 0.0F;
         }
      }
   }
}
