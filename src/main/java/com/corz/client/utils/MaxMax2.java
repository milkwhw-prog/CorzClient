package com.corz.client.utils;
import com.corz.client.utils.AmountPreviewUtils;

public final class MaxMax2 {
   private float contentHeight;
   private float displayedScroll;
   private float scrollbarOpacity;
   private float timeSinceInput = 0.9F;
   private float maxBound;
   private float targetScroll;

   public void handleAmount(float f) {
      this.targetScroll = this.getMaxForFloat(f);
      this.timeSinceInput = 0.0F;
   }

   public float getMax(float f, float f2, float f3, float divisor) {
      if (divisor <= 0.0F) {
         return 1.0F;
      } else {
         float max4 = Math.max(f3 - f, f2 - (f3 + this.maxBound));
         if (max4 <= 0.0F) {
            return 1.0F;
         } else {
            float factor = AmountPreviewUtils.getMin(max4 / divisor);
            return 1.0F - factor * factor * (3.0F - 2.0F * factor);
         }
      }
   }

   public float getAmount() {
      return this.scrollbarOpacity;
   }

   public void handleAmount(double d, float factor) {
      if (this.isFlag()) {
         this.targetScroll = this.getMaxForFloat(this.targetScroll - (float)d * factor);
         this.timeSinceInput = 0.0F;
      }
   }

   public float getMax(float f) {
      float divisor = this.getMax();
      return divisor <= 0.0F ? 0.0F : (f - this.getMaxContentHeight(f)) * Math.max(0.0F, Math.min(1.0F, this.displayedScroll / divisor));
   }

   public float getMax() {
      return Math.max(0.0F, this.contentHeight - this.maxBound);
   }

   public float getDisplayedScroll() {
      return this.displayedScroll;
   }

   public void handleMaxAmount(float f, float f2, float f3) {
      float f4 = f - f3;
      float limit = f2 + f3;
      if (f4 < this.targetScroll) {
         this.handleAmount(f4);
      } else if (limit > this.targetScroll + this.maxBound) {
         this.handleAmount(limit - this.maxBound);
      }
   }

   public void handleAmountMax(float limit, boolean flag) {
      this.displayedScroll = AmountPreviewUtils.getAmount(this.displayedScroll, this.targetScroll, 18.0F, limit, 0.05F);
      if (limit > 0.0F) {
         this.timeSinceInput += limit;
      }

      float f = !this.isFlag() || !flag && !(this.timeSinceInput < 0.9F) && !(Math.abs(this.displayedScroll - this.targetScroll) > 0.05F) ? 0.0F : 1.0F;
      this.scrollbarOpacity = AmountPreviewUtils.getAmount(this.scrollbarOpacity, f, 6.0F, limit, 0.002F);
   }

   public void handleAmountMax(float f) {
      this.targetScroll = this.getMaxForFloat(f);
      this.displayedScroll = this.targetScroll;
   }

   private float getMaxForFloat(float bound) {
      return Math.max(0.0F, Math.min(bound, this.getMax()));
   }

   public void handleMaxAmount(float bound, float cubeLimit) {
      this.contentHeight = Math.max(0.0F, bound);
      this.maxBound = Math.max(0.0F, cubeLimit);
      this.targetScroll = this.getMaxForFloat(this.targetScroll);
      this.displayedScroll = Math.max(0.0F, Math.min(this.displayedScroll, this.getMax()));
   }

   public float getTargetScroll() {
      return this.targetScroll;
   }

   public float getMaxMaxBound(float f, float f2, float f3, float divisor) {
      if (divisor <= 0.0F) {
         return 1.0F;
      } else {
         float bound = (f2 - f3) / divisor;
         float hashHuntBound = (f3 + this.maxBound - f) / divisor;
         return AmountPreviewUtils.getMin(Math.min(bound, hashHuntBound));
      }
   }

   public boolean isFlag() {
      return this.getMax() > 0.5F;
   }

   public float getMaxContentHeight(float bound) {
      return !(this.contentHeight <= 0.0F) && !(this.maxBound >= this.contentHeight) ? Math.max(Math.min(14.0F, bound), bound * this.maxBound / this.contentHeight) : bound;
   }
}
