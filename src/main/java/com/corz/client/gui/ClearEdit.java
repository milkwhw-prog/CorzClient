package com.corz.client.gui;

import com.corz.client.render.MatrixIdentifierUtils;
import net.minecraft.client.gui.DrawContext;

public final class ClearEdit {
   private double displayedAmount = Double.NaN;
   private long timeMillis = 0L;

   public void handleAmount(float amount) {
      this.displayedAmount = amount;
   }

   public void updateTime() {
      this.timeMillis = System.currentTimeMillis();
   }

   public void renderAmountTime(DrawContext context, float amount, int editValue, int n4, int editValue3, double matrixIdentifierFactor) {
      if (Double.isNaN(this.displayedAmount)) {
         this.displayedAmount = amount;
      }

      this.displayedAmount = MatrixIdentifierUtils.getAmount(this.displayedAmount, amount, 32.0, matrixIdentifierFactor);
      long editValue5 = System.currentTimeMillis() - this.timeMillis;
      float editFactor;
      if (editValue5 < 420L) {
         editFactor = 1.0F;
      } else {
         double angle = (editValue5 - 420L) / 1060.0 * Math.PI * 2.0;
         editFactor = (float)(0.3 + 0.7 * (0.5 + 0.5 * Math.cos(angle)));
      }

      int editFactor2 = editValue3 >>> 24 & 0xFF;
      if (editFactor2 == 0) {
         editFactor2 = 255;
      }

      int cardMatrixValue = (int)(editFactor2 * editFactor) << 24 | editValue3 & 16777215;
      MatrixPeekUtilsRender.renderMatrixStack(context, (int)Math.round(this.displayedAmount), editValue, 1, n4, 0, cardMatrixValue);
   }
}
