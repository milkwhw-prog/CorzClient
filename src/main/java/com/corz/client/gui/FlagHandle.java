package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.config.DoubleSetting;
import com.corz.client.render.RenderMatrixUtilsRelease;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class FlagHandle {
   private final DoubleSetting next;
   private int boundsY;
   private final QuintScissor valueAnimation;
   private final QuintScissor rendererQuintScissor;
   private boolean rendererActive = false;
   private int rendererValue;
   private int handleValue;
   private int boundsHeight;

   public void renderTimeRenderer(DrawContext context, int rendererLimit, int mouseY) {
      String rendererText = this.next.getSettingName() + ": " + String.format("%.2f", this.next.getValue());
      CorzClient.fontRenderer().handleForStringFloat(rendererText, this.rendererValue + 4, this.boundsY + 4, new Color(-3947581));
      int rendererValue = this.rendererValue + 4;
      int editValue = this.boundsY + 4 + 9 + 9;
      int rendererFactor = this.handleValue - 8;
      boolean rendererFlag = rendererLimit >= rendererValue && rendererLimit <= rendererValue + rendererFactor && mouseY >= editValue - 4 && mouseY <= editValue + 4 + 4;
      this.rendererQuintScissor.handleAmountTime(!rendererFlag && !this.rendererActive ? 0.0 : 1.0);
      double rendererBound = this.rendererQuintScissor.getAmount();
      RenderMatrixUtilsRelease.renderReleaseRightForDrawContextInt(context, rendererValue, editValue, rendererFactor, 4);
      double rendererValue3 = this.next.getValue();
      if (Math.abs(this.valueAnimation.getTargetAmount() - rendererValue3) > 0.001) {
         this.valueAnimation.handleAmountTime(rendererValue3);
      }

      double rendererValue4 = this.valueAnimation.getAmount();
      double rendererFactor2 = (rendererValue4 - this.next.getMinValue()) / (this.next.getMaxValue() - this.next.getMinValue());
      int releaseRightLimit = (int)(rendererFactor2 * rendererFactor);
      int matrixReleaseValue = rendererValue + releaseRightLimit;
      int rendererValue7 = editValue + 2;
      RenderMatrixUtilsRelease.renderReleaseRight(context, rendererValue, editValue, releaseRightLimit, 4);
      RenderMatrixUtilsRelease.render(context, matrixReleaseValue, rendererValue7, 4, Math.max(rendererBound, this.rendererActive ? 1.0 : 0.0));
   }

   public void handle(int value, int y, int width, int betaText) {
      this.rendererValue = value;
      this.boundsY = y;
      this.handleValue = width;
      this.boundsHeight = betaText;
   }

   public boolean isFlag() {
      return this.rendererActive;
   }

   public boolean canFlag(double handleLimit, double mouseY, int handleValue) {
      if (handleValue != 0) {
         return false;
      } else {
         int handleValue2 = this.rendererValue + 4;
         int handleValue3 = this.boundsY + 4 + 9 + 9;
         int handleValue4 = this.handleValue - 8;
         if (handleLimit >= handleValue2 && handleLimit <= handleValue2 + handleValue4 && mouseY >= handleValue3 - 4 && mouseY <= handleValue3 + 4 + 4) {
            this.rendererActive = true;
            this.handle(handleLimit);
            return true;
         } else {
            return false;
         }
      }
   }

   public int getBoundsHeight() {
      return this.boundsHeight;
   }

   private void handle(double handleValue) {
      int handleValue2 = this.rendererValue + 4;
      int handleDivisor = this.handleValue - 8;
      double handleBound = (handleValue - handleValue2) / handleDivisor;
      double handleFactor = Math.max(0.0, Math.min(1.0, handleBound));
      double deltaAmount = this.next.getMaxValue() - this.next.getMinValue();
      double handleValue3 = this.next.getMinValue() + handleFactor * deltaAmount;
      double stepSize = this.next.getStepSize();
      double goalBound = Math.round(handleValue3 / stepSize) * stepSize;
      double doubleBound = Math.max(this.next.getMinValue(), Math.min(this.next.getMaxValue(), goalBound));
      this.next.handleMaxAmount(doubleBound);
   }

   public void handleFlag(double handleValue, double mouseReleasedValue, int button) {
      if (button == 0) {
         this.rendererActive = false;
      }
   }

   public boolean canFlag(double handleLimit, double handleLimit2) {
      return handleLimit >= this.rendererValue && handleLimit <= this.rendererValue + this.handleValue && handleLimit2 >= this.boundsY && handleLimit2 <= this.boundsY + this.boundsHeight;
   }

   public FlagHandle(DoubleSetting next, int value, int value2, int value3, int value4) {
      this.next = next;
      this.rendererValue = value;
      this.boundsY = value2;
      this.handleValue = value3;
      this.boundsHeight = value4;
      this.valueAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 250, next.getValue());
      this.rendererQuintScissor = new QuintScissor(QuintBounceType.SINE_OUT, 200, 0.0);
   }

   public void handleFlagForDoubleInt(double handleValue, double minmaxValue, int button) {
      if (this.rendererActive) {
         this.handle(handleValue);
      }
   }
}
