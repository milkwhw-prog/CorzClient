package com.corz.client.utils;
import com.corz.client.utils.AmountConstUtilsAmounts;

public final class MaxTarget {
   private float targetPitch;
   private double fractionalStepAccumulator;
   private double maxBound;
   private float targetYaw;
   private boolean tracking;
   private boolean settledLastTick;
   private double turnSpeed = 2.0;
   private double remainderDelta;

   public boolean isFlag() {
      return this.tracking;
   }

   public void updateAmountFlag() {
      this.tracking = false;
      this.remainderDelta = 0.0;
      this.fractionalStepAccumulator = 0.0;
   }

   public double getAmount(float f) {
      return AmountConstUtilsAmounts.getAmount(f, this.targetYaw);
   }

   public float getTargetYaw() {
      return this.targetYaw;
   }

   public void handleAmountFlag(float amount, float bound, double d, double apiPutBound, boolean flag) {
      if (!this.tracking) {
         this.remainderDelta = 0.0;
         this.fractionalStepAccumulator = 0.0;
      }

      this.tracking = true;
      this.targetYaw = amount;
      this.targetPitch = Math.max(-90.0F, Math.min(90.0F, bound));
      this.turnSpeed = AmountConstUtilsAmounts.getAmountDouble(d, 2.0, 12.0);
      this.maxBound = Math.max(0.0, apiPutBound);
      this.settledLastTick = flag;
   }

   public float[] getAmountMax(float f, float f2, double factor, float divisor, double bound) {
      if (this.tracking && divisor > 0.0F && factor > 0.0) {
         double d = AmountConstUtilsAmounts.getAmount(this.targetYaw - f);
         double d2 = this.targetPitch - f2;
         double limit = Math.max(this.maxBound, divisor * 0.5);
         boolean flag = Math.abs(d) <= limit;
         boolean flag2 = Math.abs(d2) <= limit;
         double maxYaw = Math.max(0.0, bound);
         double factor2 = flag ? 0.0 : Math.min(Math.abs(d), AmountConstUtilsAmounts.getAmountForDouble(this.turnSpeed, d) * factor * maxYaw);
         double factor3 = flag2 ? 0.0 : Math.min(Math.abs(d2), AmountConstUtilsAmounts.getAmountForDouble(this.turnSpeed, d2) * factor * maxYaw);
         if (this.settledLastTick && !flag && !flag2) {
            double d3 = Math.abs(d) / Math.abs(d2);
            if (d3 < 1.0) {
               factor2 *= d3;
            } else {
               factor3 /= d3;
            }
         }

         if (flag) {
            this.remainderDelta = 0.0;
         } else {
            this.remainderDelta = this.remainderDelta + Math.signum(d) * factor2 / divisor;
         }

         if (flag2) {
            this.fractionalStepAccumulator = 0.0;
         } else {
            this.fractionalStepAccumulator = this.fractionalStepAccumulator + Math.signum(d2) * factor3 / divisor;
         }

         int j = (int)this.remainderDelta;
         int factor4 = (int)this.fractionalStepAccumulator;
         int round5 = (int)Math.round(Math.abs(d) / divisor);
         int round6 = (int)Math.round(Math.abs(d2) / divisor);
         if (Math.abs(j) > round5) {
            j = Integer.signum(j) * round5;
            this.remainderDelta = 0.0;
         } else {
            this.remainderDelta -= j;
         }

         if (Math.abs(factor4) > round6) {
            factor4 = Integer.signum(factor4) * round6;
            this.fractionalStepAccumulator = 0.0;
         } else {
            this.fractionalStepAccumulator -= factor4;
         }

         return new float[]{j * divisor, factor4 * divisor};
      } else {
         return new float[2];
      }
   }

   public float getMax() {
      return this.targetPitch;
   }
}
