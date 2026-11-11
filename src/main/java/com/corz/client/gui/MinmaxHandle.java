package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.config.RangeSetting;
import com.corz.client.render.RenderMatrixUtilsRelease;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class MinmaxHandle {
   private final QuintScissor valueAnimation;
   private final QuintScissor minmaxQuintScissor;
   private final QuintScissor minmaxHandleQuintScissor;
   private int trackX;
   private boolean minmaxActive = false;
   private final RangeSetting minmax;
   private boolean minmaxHandleActive = false;
   private final QuintScissor quintScissor;
   private int minmaxValue;
   private int minmaxHandleValue;
   private int trackWidth;

   private void handleMinmax(double minmaxBound) {
      double minmax = this.getMinmax(minmaxBound);
      this.minmax.handleMaxAmount2(minmax);
   }

   private void handleMinmaxForDouble(double minmaxBound) {
      double minmax = this.getMinmax(minmaxBound);
      this.minmax.handleMaxAmount(minmax);
   }

   public MinmaxHandle(RangeSetting minmax, int value, int value2, int value3, int value4) {
      this.minmax = minmax;
      this.trackX = value;
      this.minmaxValue = value2;
      this.trackWidth = value3;
      this.minmaxHandleValue = value4;
      this.valueAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 250, minmax.getMin());
      this.minmaxHandleQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 250, minmax.getMax());
      this.minmaxQuintScissor = new QuintScissor(QuintBounceType.SINE_OUT, 200, 0.0);
      this.quintScissor = new QuintScissor(QuintBounceType.SINE_OUT, 200, 0.0);
   }

   public boolean canFlag(double minmaxHandleLimit, double minmaxHandleLimit2) {
      return minmaxHandleLimit >= this.trackX && minmaxHandleLimit <= this.trackX + this.trackWidth && minmaxHandleLimit2 >= this.minmaxValue && minmaxHandleLimit2 <= this.minmaxValue + this.minmaxHandleValue;
   }

   public void handleMinmaxFlag(double minmaxBound, double minmaxValue, int button) {
      if (this.minmaxActive || this.minmaxHandleActive) {
         double minmax = this.getMinmax(minmaxBound);
         double step = this.minmax.getStep();
         if (this.minmaxActive) {
            if (minmax > this.minmax.getMax() + step * 0.5) {
               this.minmaxActive = false;
               this.minmaxHandleActive = true;
               this.handleMinmaxForDouble(minmaxBound);
            } else {
               this.handleMinmax(minmaxBound);
            }
         } else {
            if (this.minmaxHandleActive) {
               if (minmax < this.minmax.getMin() - step * 0.5) {
                  this.minmaxHandleActive = false;
                  this.minmaxActive = true;
                  this.handleMinmax(minmaxBound);
               } else {
                  this.handleMinmaxForDouble(minmaxBound);
               }
            }
         }
      }
   }

   public int getMinmaxHandleValue() {
      return this.minmaxHandleValue;
   }

   public void renderMinmaxTime(DrawContext context, int minmaxValue, int mouseY) {
      String format = String.format("%s: %.2f - %.2f", this.minmax.getSettingName(), this.minmax.getMin(), this.minmax.getMax());
      CorzClient.fontRenderer().handleForStringFloat(format, this.trackX + 4, this.minmaxValue + 4, new Color(-3947581));
      int releaseRightValue = this.trackX + 4;
      int editValue = this.minmaxValue + 4 + 9 + 9;
      int minmaxFactor = this.trackWidth - 8;
      RenderMatrixUtilsRelease.renderReleaseRightForDrawContextInt(context, releaseRightValue, editValue, minmaxFactor, 4);
      if (Math.abs(this.valueAnimation.getTargetAmount() - this.minmax.getMin()) > 0.001) {
         this.valueAnimation.handleAmountTime(this.minmax.getMin());
      }

      if (Math.abs(this.minmaxHandleQuintScissor.getTargetAmount() - this.minmax.getMax()) > 0.001) {
         this.minmaxHandleQuintScissor.handleAmountTime(this.minmax.getMax());
      }

      double minmaxValue5 = this.valueAnimation.getAmount();
      double minmaxValue6 = this.minmaxHandleQuintScissor.getAmount();
      double minmaxFactor2 = (minmaxValue5 - this.minmax.getRangeMin()) / (this.minmax.getRangeMax() - this.minmax.getRangeMin());
      double minmaxFactor3 = (minmaxValue6 - this.minmax.getRangeMin()) / (this.minmax.getRangeMax() - this.minmax.getRangeMin());
      int minmaxBound = releaseRightValue + (int)(minmaxFactor2 * minmaxFactor);
      int matrixReleaseValue = releaseRightValue + (int)(minmaxFactor3 * minmaxFactor);
      int minmaxValue7 = editValue + 2;
      boolean minmaxFlag = Math.abs(minmaxValue - minmaxBound) < 8 && Math.abs(mouseY - minmaxValue7) < 8;
      boolean minmaxFlag2 = Math.abs(minmaxValue - matrixReleaseValue) < 8 && Math.abs(mouseY - minmaxValue7) < 8;
      this.minmaxQuintScissor.handleAmountTime(!minmaxFlag && !this.minmaxActive ? 0.0 : 1.0);
      this.quintScissor.handleAmountTime(!minmaxFlag2 && !this.minmaxHandleActive ? 0.0 : 1.0);
      double hashRegistryBound = this.minmaxQuintScissor.getAmount();
      double minmaxBound4 = this.quintScissor.getAmount();
      int min8 = Math.min(minmaxBound, matrixReleaseValue);
      int releaseRightLimit = Math.abs(matrixReleaseValue - minmaxBound);
      RenderMatrixUtilsRelease.renderReleaseRight(context, min8, editValue, releaseRightLimit, 4);
      RenderMatrixUtilsRelease.render(context, minmaxBound, minmaxValue7, 4, Math.max(hashRegistryBound, this.minmaxActive ? 1.0 : 0.0));
      RenderMatrixUtilsRelease.render(context, matrixReleaseValue, minmaxValue7, 4, Math.max(minmaxBound4, this.minmaxHandleActive ? 1.0 : 0.0));
   }

   public boolean canMinmaxFlag(double minmaxBound, double minmaxLimit, int minmaxValue) {
      if (minmaxValue != 0) {
         return false;
      } else {
         int minmaxValue2 = this.trackX + 4;
         int minmaxValue3 = this.minmaxValue + 4 + 9 + 9;
         int minmaxFactor = this.trackWidth - 8;
         if (minmaxBound >= minmaxValue2 && minmaxBound <= minmaxValue2 + minmaxFactor && minmaxLimit >= minmaxValue3 - 4 && minmaxLimit <= minmaxValue3 + 4 + 4) {
            double minmaxFactor2 = (this.minmax.getMin() - this.minmax.getRangeMin()) / (this.minmax.getRangeMax() - this.minmax.getRangeMin());
            double minmaxFactor3 = (this.minmax.getMax() - this.minmax.getRangeMin()) / (this.minmax.getRangeMax() - this.minmax.getRangeMin());
            int minmaxLimit2 = minmaxValue2 + (int)(minmaxFactor2 * minmaxFactor);
            int minmaxValue4 = minmaxValue2 + (int)(minmaxFactor3 * minmaxFactor);
            double abs5 = Math.abs(minmaxBound - minmaxLimit2);
            double abs3 = Math.abs(minmaxBound - minmaxValue4);
            if (Math.abs(abs5 - abs3) < 1.0E-4) {
               if (minmaxBound < minmaxLimit2) {
                  this.minmaxActive = true;
                  this.handleMinmax(minmaxBound);
               } else {
                  this.minmaxHandleActive = true;
                  this.handleMinmaxForDouble(minmaxBound);
               }

               return true;
            } else {
               if (abs5 < abs3) {
                  this.minmaxActive = true;
                  this.handleMinmax(minmaxBound);
               } else {
                  this.minmaxHandleActive = true;
                  this.handleMinmaxForDouble(minmaxBound);
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public void handleFlag(double minmaxHandleValue, double mouseReleasedValue, int button) {
      if (button == 0) {
         this.minmaxActive = false;
         this.minmaxHandleActive = false;
      }
   }

   public boolean isFlag() {
      return this.minmaxActive || this.minmaxHandleActive;
   }

   private double getMinmax(double minmaxValue) {
      int minmaxValue2 = this.trackX + 4;
      int minmaxDivisor = this.trackWidth - 8;
      double minmaxBound = (minmaxValue - minmaxValue2) / minmaxDivisor;
      double minmaxFactor = Math.max(0.0, Math.min(1.0, minmaxBound));
      double deltaAmount = this.minmax.getRangeMax() - this.minmax.getRangeMin();
      double minmaxValue3 = this.minmax.getRangeMin() + minmaxFactor * deltaAmount;
      double step = this.minmax.getStep();
      return Math.round(minmaxValue3 / step) * step;
   }

   public void handle(int value, int editClusterValue, int width, int betaText) {
      this.trackX = value;
      this.minmaxValue = editClusterValue;
      this.trackWidth = width;
      this.minmaxHandleValue = betaText;
   }
}
