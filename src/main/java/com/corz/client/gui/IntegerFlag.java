package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.config.IntegerSetting;
import com.corz.client.render.RenderMatrixUtilsRelease;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class IntegerFlag {
   private boolean rendererFontActive = false;
   private int boundsY;
   private final QuintScissor valueAnimation;
   private final QuintScissor rendererFontQuintScissor;
   private final IntegerSetting next;
   private int rendererFontValue;
   private int integerValue;
   private int boundsHeight;

   public void renderRendererFont(DrawContext context, int rendererFontLimit, int mouseY) {
      String rendererFontText = this.next.getSettingName() + ": " + this.next.getValue();
      CorzClient.fontRenderer().handleForStringFloat(rendererFontText, this.integerValue + 4, this.boundsY + 4, new Color(-3947581));
      String formatTextMax = this.next.getHintLabel();
      if (formatTextMax != null) {
         int rendererFontValue = (int)CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont(rendererFontText);
         CorzClient.fontRenderer().handleForStringFloat(formatTextMax, this.integerValue + 4 + rendererFontValue + 5, this.boundsY + 4, new Color(235, 96, 92));
      }

      int releaseRightValue = this.integerValue + 4;
      int editValue = this.boundsY + 4 + 9 + 9;
      int rendererFontFactor = this.rendererFontValue - 8;
      boolean rendererFontFlag = rendererFontLimit >= releaseRightValue && rendererFontLimit <= releaseRightValue + rendererFontFactor && mouseY >= editValue - 4 && mouseY <= editValue + 4 + 4;
      this.rendererFontQuintScissor.handleAmountTime(!rendererFontFlag && !this.rendererFontActive ? 0.0 : 1.0);
      double rendererFontBound = this.rendererFontQuintScissor.getAmount();
      RenderMatrixUtilsRelease.renderReleaseRightForDrawContextInt(context, releaseRightValue, editValue, rendererFontFactor, 4);
      double rendererFontValue4 = this.next.getValue();
      if (Math.abs(this.valueAnimation.getTargetAmount() - rendererFontValue4) > 0.01) {
         this.valueAnimation.handleAmountTime(rendererFontValue4);
      }

      double rendererFontValue5 = this.valueAnimation.getAmount();
      float rendererFontFactor2 = (float)(rendererFontValue5 - this.next.getMinValue()) / (this.next.getMaxValue() - this.next.getMinValue());
      int releaseRightLimit = (int)(rendererFontFactor2 * rendererFontFactor);
      int matrixReleaseValue = releaseRightValue + releaseRightLimit;
      int rendererFontValue8 = editValue + 2;
      RenderMatrixUtilsRelease.renderReleaseRight(context, releaseRightValue, editValue, releaseRightLimit, 4);
      RenderMatrixUtilsRelease.render(context, matrixReleaseValue, rendererFontValue8, 4, Math.max(rendererFontBound, this.rendererFontActive ? 1.0 : 0.0));
   }

   public void handle(int value, int y, int width, int betaText) {
      this.integerValue = value;
      this.boundsY = y;
      this.rendererFontValue = width;
      this.boundsHeight = betaText;
   }

   public boolean canFlag(double integerLimit, double mouseY, int integerValue) {
      if (integerValue != 0) {
         return false;
      } else {
         int integerValue2 = this.integerValue + 4;
         int integerValue3 = this.boundsY + 4 + 9 + 9;
         int integerValue4 = this.rendererFontValue - 8;
         if (integerLimit >= integerValue2 && integerLimit <= integerValue2 + integerValue4 && mouseY >= integerValue3 - 4 && mouseY <= integerValue3 + 4 + 4) {
            this.rendererFontActive = true;
            this.handle(integerLimit);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean isFlag() {
      return this.rendererFontActive;
   }

   public void handleFlag(double integerValue, double mouseReleasedValue, int button) {
      if (button == 0) {
         this.rendererFontActive = false;
      }
   }

   public int getBoundsHeight() {
      return this.boundsHeight;
   }

   public boolean canFlag(double integerLimit, double integerLimit2) {
      return integerLimit >= this.integerValue && integerLimit <= this.integerValue + this.rendererFontValue && integerLimit2 >= this.boundsY && integerLimit2 <= this.boundsY + this.boundsHeight;
   }

   public void handleFlagForDoubleInt(double integerValue, double minmaxValue, int button) {
      if (this.rendererFontActive) {
         this.handle(integerValue);
      }
   }

   private void handle(double integerValue) {
      int integerValue2 = this.integerValue + 4;
      int integerDivisor = this.rendererFontValue - 8;
      double integerBound = (integerValue - integerValue2) / integerDivisor;
      double integerFactor = Math.max(0.0, Math.min(1.0, integerBound));
      int deltaValue = this.next.getMaxValue() - this.next.getMinValue();
      int integerValue3 = (int)(this.next.getMinValue() + integerFactor * deltaValue);
      int stepSize = this.next.getStepSize();
      int integerValue4 = (integerValue3 - this.next.getMinValue()) / stepSize * stepSize + this.next.getMinValue();
      this.next.handleMax(integerValue4);
   }

   public IntegerFlag(IntegerSetting next, int value, int value2, int value3, int value4) {
      this.next = next;
      this.integerValue = value;
      this.boundsY = value2;
      this.rendererFontValue = value3;
      this.boundsHeight = value4;
      this.valueAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 250, next.getValue());
      this.rendererFontQuintScissor = new QuintScissor(QuintBounceType.SINE_OUT, 200, 0.0);
   }
}
