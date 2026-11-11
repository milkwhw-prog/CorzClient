package com.corz.client.utils;

public final class MaxFlag {
   public float maxItemX;
   public float maxItemWidth;
   public float maxItemHeight;
   public float minItemHeight;
   public float titleX;
   public float nonAmount;
   public float buttonAmount;
   private float cachedScreenWidth = -1.0F;
   public float minItemY;
   private float lastLayoutParamF;
   public float titleHeight;
   public float previewCloseAmount;
   public float minItemWidth;
   private float lastLayoutParamC;
   public float screenHeight;
   public float buttonX;
   private float cachedScreenHeight;
   private float lastContentHeight;
   public float panelX;
   public float screenWidth;
   private boolean lastHasSubtitle;
   public float closeButtonY;
   public float titleY;
   public float maxItemY;
   public float previewAmount;
   public float subtitleY;
   private float lastLayoutParamE;
   public float closeButtonX;
   public float buttonHeight;
   public float panelHeight;
   public float previewY;
   public float previewWidth;
   public float previewX;
   public float panelY;
   public float buttonWidth;
   public float minItemX;
   private boolean lastHasPreview;

   public boolean isAmount() {
      return this.minItemWidth > 0.0F;
   }

   public static int getValue(int n, int limit, int limit2, boolean flag) {
      int counter = 1;

      while (counter != n && counter < limit && counter < limit2 && limit / (counter + 1) >= 320 && limit2 / (counter + 1) >= 240) {
         counter++;
      }

      if (flag && counter % 2 != 0) {
         counter++;
      }

      return counter;
   }

   public boolean canAmountMax(double limit, double amountMaxForDouble) {
      return limit >= this.buttonX && limit <= this.buttonX + this.buttonWidth && amountMaxForDouble >= this.buttonAmount && amountMaxForDouble <= this.buttonAmount + this.buttonHeight;
   }

   public boolean canAmount(double limit, double amountMaxForDouble) {
      return this.maxItemWidth > 0.0F && limit >= this.maxItemX && limit <= this.maxItemX + this.maxItemWidth && amountMaxForDouble >= this.maxItemY && amountMaxForDouble <= this.maxItemY + this.maxItemHeight;
   }

   public boolean canAmountFlag(
      float amount, float rootDivisor, float amount3, float amount4, boolean flag, float amount5, float wrapDegreesBound, boolean flag2
   ) {
      if (amount == this.cachedScreenWidth
         && rootDivisor == this.cachedScreenHeight
         && amount3 == this.lastLayoutParamC
         && amount4 == this.lastContentHeight
         && flag == this.lastHasSubtitle
         && amount5 == this.lastLayoutParamE
         && wrapDegreesBound == this.lastLayoutParamF
         && flag2 == this.lastHasPreview) {
         return false;
      } else {
         this.cachedScreenWidth = amount;
         this.cachedScreenHeight = rootDivisor;
         this.lastLayoutParamC = amount3;
         this.lastContentHeight = amount4;
         this.lastHasSubtitle = flag;
         this.lastLayoutParamE = amount5;
         this.lastLayoutParamF = wrapDegreesBound;
         this.lastHasPreview = flag2;
         this.screenWidth = amount;
         this.screenHeight = rootDivisor;
         this.nonAmount = getAmount(amount3, 220.0F, amount);
         this.panelHeight = getAmount(amount4, 150.0F, rootDivisor);
         this.panelX = (float)Math.floor((amount - this.nonAmount) / 2.0F);
         this.panelY = (float)Math.floor((rootDivisor - this.panelHeight) / 2.0F);
         this.titleX = this.panelX + 12.0F;
         this.titleY = this.panelY + 12.0F;
         this.titleHeight = 14.0F;
         this.subtitleY = this.titleY + 14.0F + 2.0F;
         float f = flag ? this.subtitleY + 10.0F : this.titleY + 14.0F;
         this.previewCloseAmount = 18.0F;
         this.closeButtonX = this.panelX + this.nonAmount - 12.0F - 18.0F + 2.0F;
         this.closeButtonY = this.panelY + 10.0F;
         float amount7 = f + 10.0F;
         this.previewAmount = flag2 ? 18.0F : 0.0F;
         this.previewX = this.panelX + 12.0F;
         this.previewWidth = this.nonAmount - 24.0F;
         this.previewY = this.panelY + this.panelHeight - 12.0F - this.previewAmount;
         float f2 = flag2 ? this.previewY - 8.0F : this.panelY + this.panelHeight - 12.0F;
         float max = Math.max(0.0F, f2 - amount7);
         float f3 = this.nonAmount - 24.0F;
         float floor8 = 0.0F;
         if (amount5 > 0.0F) {
            float bound = Math.max(0.0F, Math.min(f3 * 0.42F, f3 - 8.0F - 96.0F));
            floor8 = (float)Math.floor(Math.min(amount5, bound));
            if (floor8 < 64.0F) {
               floor8 = 0.0F;
            }
         }

         this.maxItemX = this.panelX + 12.0F;
         this.maxItemY = amount7;
         this.maxItemWidth = floor8;
         this.maxItemHeight = floor8 > 0.0F ? max : 0.0F;
         this.buttonX = floor8 > 0.0F ? this.maxItemX + floor8 + 8.0F : this.panelX + 12.0F;
         this.buttonAmount = amount7;
         float floor9 = 0.0F;
         if (wrapDegreesBound > 0.0F) {
            float rocketsElytraBound = this.panelX + this.nonAmount - 12.0F - this.buttonX - 8.0F - 96.0F;
            floor9 = (float)Math.floor(Math.min(wrapDegreesBound, Math.max(0.0F, rocketsElytraBound)));
            if (floor9 < 64.0F) {
               floor9 = 0.0F;
            }
         }

         this.minItemWidth = floor9;
         this.minItemX = this.panelX + this.nonAmount - 12.0F - floor9;
         this.minItemY = amount7;
         this.minItemHeight = floor9 > 0.0F ? max : 0.0F;
         this.buttonWidth = Math.max(0.0F, (floor9 > 0.0F ? this.minItemX - 8.0F : this.panelX + this.nonAmount - 12.0F) - this.buttonX);
         this.buttonHeight = max;
         return true;
      }
   }

   public boolean canFlag(float f, float f2, float f3, float f4, boolean flag, float f5, boolean flag2) {
      return this.canAmountFlag(f, f2, f3, f4, flag, f5, 0.0F, flag2);
   }

   public boolean canAmountForDouble(double limit, double limit2) {
      return limit >= this.closeButtonX - 2.0F
         && limit <= this.closeButtonX + this.previewCloseAmount + 2.0F
         && limit2 >= this.closeButtonY - 2.0F
         && limit2 <= this.closeButtonY + this.previewCloseAmount + 2.0F;
   }

   static float getAmount(float bound, float rocketsElytraBound, float f) {
      float hashHuntBound = Math.max(0.0F, f - 24.0F);
      float min2 = Math.min(Math.max(bound, rocketsElytraBound), hashHuntBound);
      if (min2 < rocketsElytraBound) {
         min2 = Math.max(0.0F, f - 8.0F);
      }

      return (float)Math.floor(min2);
   }

   public boolean canAmountPreviewAmount(double limit, double limit2) {
      return this.previewAmount > 0.0F
         && limit >= this.previewX
         && limit <= this.previewX + this.previewWidth
         && limit2 >= this.previewY - 1.0F
         && limit2 <= this.previewY + this.previewAmount + 1.0F;
   }

   public boolean canAmountPanelX(double limit, double amountMaxForDouble) {
      return limit >= this.panelX && limit <= this.panelX + this.nonAmount && amountMaxForDouble >= this.panelY && amountMaxForDouble <= this.panelY + this.panelHeight;
   }

   public void updateAmount() {
      this.cachedScreenWidth = -1.0F;
   }

   public boolean canAmountMinItemWidth(double limit, double amountMaxForDouble) {
      return this.minItemWidth > 0.0F && limit >= this.minItemX && limit <= this.minItemX + this.minItemWidth && amountMaxForDouble >= this.minItemY && amountMaxForDouble <= this.minItemY + this.minItemHeight;
   }

   public boolean isMaxItemWidth() {
      return this.maxItemWidth > 0.0F;
   }
}
