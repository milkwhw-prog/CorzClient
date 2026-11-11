package com.corz.client.render;

import com.corz.client.CorzClient;
import com.corz.client.config.ColorSetting;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.VertexGuiUtils;
import java.awt.Color;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class ScissorMax {
   private int customWidth;
   private float hue = 0.0F;
   private boolean scissorActive;
   private int scissorValue;
   private int scissorDisableValue;
   private boolean popupHovered;
   private IntSupplier intSupplier;
   private final ColorSetting colorBase;
   private int panelWidth;
   private float brightness;
   private int customY;
   private int panelY;
   private boolean hasCustomBounds;
   private int panelHeight;
   private final QuintScissor openAnimation;
   private float colorAmount = 1.0F;
   private boolean scissorDisableActive;
   private boolean supplierActive;
   private boolean popupFocused;
   private int colorValue;

   private void renderMaxAmount(DrawContext context, int scissorValue, int configsLikeValue, int scissorFactor, int scissorValue3) {
      byte scissorDivisor = 4;
      VertexConsumer quads = VertexGuiUtils.getQuads();

      for (int i = configsLikeValue; i < configsLikeValue + scissorValue3; i += scissorDivisor) {
         for (int j = scissorValue; j < scissorValue + scissorFactor; j += scissorDivisor) {
            boolean scissorFlag = ((j - scissorValue) / scissorDivisor + (i - configsLikeValue) / scissorDivisor) % 2 == 0;
            int min4 = Math.min(scissorDivisor, scissorValue + scissorFactor - j);
            int min5 = Math.min(scissorDivisor, configsLikeValue + scissorValue3 - i);
            VertexGuiUtils.handleVertexColor(quads, j, i, j + min4, i + min5, getValue(scissorFlag ? -3355444 : -6710887));
         }
      }

      VertexGuiUtils.updateBufferClose();
      int scissorValue6 = Color.HSBtoRGB(this.hue, this.colorAmount, this.brightness) & 16777215;
      byte scissorDivisor2 = 60;
      VertexConsumer vertices = VertexGuiUtils.getQuads();

      for (int k = 0; k < scissorDivisor2; k++) {
         float scissorValue7 = (float)k / (scissorDivisor2 - 1);
         int vertexColorValue = scissorValue + k * scissorFactor / scissorDivisor2;
         int scissorValue9 = scissorValue + (k + 1) * scissorFactor / scissorDivisor2 - vertexColorValue;
         if (scissorValue9 < 1) {
            scissorValue9 = 1;
         }

         VertexGuiUtils.handleVertexColor(vertices, vertexColorValue, configsLikeValue, vertexColorValue + scissorValue9, configsLikeValue + scissorValue3, getValue((int)(scissorValue7 * 255.0F) << 24 | scissorValue6));
      }

      VertexGuiUtils.updateBufferClose();
      int matrixStackValue = getValue(scissorValue + (int)(this.colorValue / 255.0F * scissorFactor), scissorValue, scissorValue + scissorFactor);
      int scissorValue11 = configsLikeValue + scissorValue3 / 2;
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, scissorValue11, 5, -872415232);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, scissorValue11, 4, -1);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, scissorValue11, 3, this.colorValue << 24 | scissorValue6);
   }

   public boolean canFlag(double scissorLimit, double mouseY) {
      int flagHasCustomBounds = this.getFlagHasCustomBounds();
      int scissorValue = this.getMin();
      int scissorValue2 = this.getFlag();
      if (scissorLimit < flagHasCustomBounds || scissorLimit > flagHasCustomBounds + scissorValue) {
         return false;
      } else if (mouseY >= scissorValue2 && mouseY <= scissorValue2 + 60) {
         this.popupFocused = true;
         this.handleMaxAmount(scissorLimit, mouseY, flagHasCustomBounds, scissorValue2, scissorValue, 60);
         return true;
      } else {
         int scissorValue3 = scissorValue2 + 60 + 4;
         if (mouseY >= scissorValue3 && mouseY <= scissorValue3 + 8) {
            this.popupHovered = true;
            this.handleMax(scissorLimit, flagHasCustomBounds, scissorValue);
            return true;
         } else {
            int scissorValue4 = scissorValue3 + 8 + 4;
            if (mouseY >= scissorValue4 && mouseY <= scissorValue4 + 8) {
               this.scissorActive = true;
               this.handle(scissorLimit, flagHasCustomBounds, scissorValue);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private void updateMaxColor() {
      int hsBtoRGB = Color.HSBtoRGB(this.hue, this.colorAmount, this.brightness);
      int colorValue = this.colorValue << 24 | hsBtoRGB & 16777215;
      this.colorBase.setColor(colorValue);
      MatrixEspUtils.updateFlagTime();
   }

   public void render(DrawContext context) {
      this.updateSupplierFlag();
      this.renderForDrawContext(context);
   }

   private static int getValue(int scissorValue, int scissorValue2, double scissorFactor) {
      int scissorValue3 = scissorValue >> 24 & 0xFF;
      int scissorValue4 = scissorValue >> 16 & 0xFF;
      int scissorValue5 = scissorValue >> 8 & 0xFF;
      int scissorValue6 = scissorValue & 0xFF;
      int scissorValue7 = scissorValue2 >> 24 & 0xFF;
      int scissorValue8 = scissorValue2 >> 16 & 0xFF;
      int scissorValue9 = scissorValue2 >> 8 & 0xFF;
      int scissorValue10 = scissorValue2 & 0xFF;
      return (int)(scissorValue3 + (scissorValue7 - scissorValue3) * scissorFactor) << 24
         | (int)(scissorValue4 + (scissorValue8 - scissorValue4) * scissorFactor) << 16
         | (int)(scissorValue5 + (scissorValue9 - scissorValue5) * scissorFactor) << 8
         | (int)(scissorValue6 + (scissorValue10 - scissorValue6) * scissorFactor);
   }

   private void updateSupplierFlag() {
      if (this.intSupplier != null && !this.isFlag()) {
         int supplierValue = this.intSupplier.getAsInt() & 16777215;
         if (supplierValue != (this.colorBase.getColor() & 16777215)) {
            float[] rgBtoHSB = Color.RGBtoHSB(supplierValue >> 16 & 0xFF, supplierValue >> 8 & 0xFF, supplierValue & 0xFF, null);
            if (rgBtoHSB[1] > 0.0F) {
               this.hue = rgBtoHSB[0];
            }

            this.colorAmount = rgBtoHSB[1];
            this.brightness = rgBtoHSB[2];
            this.supplierActive = true;
         } else if (this.supplierActive) {
            this.updateMaxColorColorBase();
            this.supplierActive = false;
         }
      } else {
         this.supplierActive = false;
      }
   }

   public boolean canFlag(char ch, int scissorValue) {
      return false;
   }

   public ScissorMax(ColorSetting colorBase, int value, int value2, int value3, int value4) {
      this.brightness = 1.0F;
      this.colorValue = 255;
      this.popupFocused = false;
      this.popupHovered = false;
      this.scissorActive = false;
      this.scissorDisableActive = false;
      this.colorBase = colorBase;
      this.scissorDisableValue = value;
      this.panelY = value2;
      this.panelWidth = value3;
      this.panelHeight = value4;
      this.openAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 300, 0.0);
      this.updateMaxColorColorBase();
   }

   public int getTimeFlag() {
      double scissorValue = this.scissorDisableActive ? 1.0 : 0.0;
      if (Math.abs(this.openAnimation.getTargetAmount() - scissorValue) > 0.01) {
         this.openAnimation.handleAmountTime(scissorValue);
      }

      double scissorValue2 = this.openAnimation.getAmount();
      double scissorValue3 = this.scissorDisableActive ? 1.0 : scissorValue2;
      return 24 + (int)(92.0 * scissorValue3);
   }

   private static int getValue(int scissorValue) {
      return CorzRoundedUtils.getPanelOverlay(scissorValue);
   }

   private void renderMaxAmountForDrawContextInt(DrawContext context, int scissorValue, int scissorValue2, int scissorFactor, int scissorFactor2) {
      byte scissorDivisor = 48;
      byte scissorDivisor2 = 48;
      VertexConsumer quads = VertexGuiUtils.getQuads();

      for (int i = 0; i < scissorDivisor; i++) {
         float scissorValue3 = 1.0F - (float)i / (scissorDivisor - 1);
         int configsLikeValue = scissorValue2 + i * scissorFactor2 / scissorDivisor;
         int scissorValue5 = scissorValue2 + (i + 1) * scissorFactor2 / scissorDivisor - configsLikeValue;
         if (scissorValue5 < 1) {
            scissorValue5 = 1;
         }

         int scissorValue6 = 0xFF000000 | Color.HSBtoRGB(this.hue, 0.0F, scissorValue3) & 16777215;
         int scissorValue7 = 0xFF000000 | Color.HSBtoRGB(this.hue, 1.0F, scissorValue3) & 16777215;

         for (int j = 0; j < scissorDivisor2; j++) {
            float scissorValue8 = (float)j / (scissorDivisor2 - 1);
            int vertexColorValue = scissorValue + j * scissorFactor / scissorDivisor2;
            int scissorValue10 = scissorValue + (j + 1) * scissorFactor / scissorDivisor2 - vertexColorValue;
            if (scissorValue10 < 1) {
               scissorValue10 = 1;
            }

            VertexGuiUtils.handleVertexColor(quads, vertexColorValue, configsLikeValue, vertexColorValue + scissorValue10, configsLikeValue + scissorValue5, getValue(getValue(scissorValue6, scissorValue7, (double)scissorValue8)));
         }
      }

      VertexGuiUtils.updateBufferClose();
      int scissorValue11 = scissorValue + (int)(this.colorAmount * scissorFactor);
      int scissorBound = scissorValue2 + (int)((1.0F - this.brightness) * scissorFactor2);
      int matrixStackValue = getValue(scissorValue11, scissorValue, scissorValue + scissorFactor);
      int scissorValue14 = getValue(scissorBound, scissorValue2, scissorValue2 + scissorFactor2);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, scissorValue14, 5, -872415232);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, scissorValue14, 4, -1);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, scissorValue14, 2, 0xFF000000 | Color.HSBtoRGB(this.hue, this.colorAmount, this.brightness) & 16777215);
   }

   public void renderScissorDisable(DrawContext context, int scissorDisableValue, int mouseY) {
      this.updateSupplierFlag();
      double scissorDisableValue3 = this.scissorDisableActive ? 1.0 : 0.0;
      if (Math.abs(this.openAnimation.getTargetAmount() - scissorDisableValue3) > 0.01) {
         this.openAnimation.handleAmountTime(scissorDisableValue3);
      }

      double scissorDisableLimit = this.openAnimation.getAmount();
      int f13 = this.panelY + 4;
      int matrixStackValue = this.scissorDisableValue + this.panelWidth - 4 - 14;
      CorzClient.fontRenderer().handleForStringFloat(this.colorBase.getSettingName(), this.scissorDisableValue + 4, f13, new Color(-3947581));
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue - 1, f13 - 1, 16, 16, 4, -8421505);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, f13, 14, 14, 3, this.getSupplierColor());
      if (scissorDisableLimit > 0.001) {
         int scissorDisableValue6 = this.panelY + 24;
         int min7 = Math.min((int)(92.0 * scissorDisableLimit), Math.max(0, this.panelHeight - 24));
         if (min7 <= 0) {
            return;
         }

         context.enableScissor(this.scissorDisableValue, scissorDisableValue6, this.scissorDisableValue + this.panelWidth, scissorDisableValue6 + min7);
         this.renderForDrawContext(context);
         context.disableScissor();
      }
   }

   public boolean canFlagTime(double scissorLimit, double mouseY, int scissorValue) {
      if (scissorValue != 0) {
         return false;
      } else {
         int scissorValue2 = this.panelY + 4;
         int scissorValue3 = this.scissorDisableValue + this.panelWidth - 4 - 14;
         boolean scissorFlag = scissorLimit >= scissorValue3 && scissorLimit <= scissorValue3 + 14 && mouseY >= scissorValue2 && mouseY <= scissorValue2 + 14;
         if (scissorFlag) {
            this.scissorDisableActive = !this.scissorDisableActive;
            if (this.scissorDisableActive) {
               this.updateMaxColorColorBase();
            }

            return true;
         } else {
            return this.scissorDisableActive && this.openAnimation.getAmount() > 0.3 ? this.canFlag(scissorLimit, mouseY) : false;
         }
      }
   }

   public void handleFlag(double scissorValue, double minmaxValue, int button) {
      int flagHasCustomBounds = this.getFlagHasCustomBounds();
      int scissorDivisor = this.getMin();
      int scissorValue6 = this.getFlag();
      if (this.popupFocused) {
         this.handleMaxAmount(scissorValue, minmaxValue, flagHasCustomBounds, scissorValue6, scissorDivisor, 60);
      }

      if (this.popupHovered) {
         this.handleMax(scissorValue, flagHasCustomBounds, scissorDivisor);
      }

      if (this.scissorActive) {
         this.handle(scissorValue, flagHasCustomBounds, scissorDivisor);
      }
   }

   private void updateMaxColorColorBase() {
      int intValue = this.colorBase.getColor();
      this.colorValue = intValue >> 24 & 0xFF;
      int colorValue = intValue >> 16 & 0xFF;
      int colorValue2 = intValue >> 8 & 0xFF;
      int colorValue3 = intValue & 0xFF;
      float[] rgBtoHSB = Color.RGBtoHSB(colorValue, colorValue2, colorValue3, null);
      this.hue = rgBtoHSB[0];
      this.colorAmount = rgBtoHSB[1];
      this.brightness = rgBtoHSB[2];
   }

   private int getMin() {
      return this.hasCustomBounds ? this.customWidth : Math.min(this.panelWidth - 8, 160);
   }

   public void update() {
      this.updateMaxColorColorBase();
   }

   private void handle(double scissorValue, int flagHasCustomBounds, int scissorDivisor) {
      this.colorValue = (int)(Math.max(0.0, Math.min(1.0, (scissorValue - flagHasCustomBounds) / scissorDivisor)) * 255.0);
      this.updateMaxColor();
   }

   private int getFlag() {
      return this.hasCustomBounds ? this.customY : this.panelY + 24 + 4;
   }

   public boolean isScissorDisableActive() {
      return this.scissorDisableActive;
   }

   public void setIntSupplier(IntSupplier intSupplier) {
      this.intSupplier = intSupplier;
   }

   private int getFlagHasCustomBounds() {
      return this.hasCustomBounds ? this.scissorValue : this.scissorDisableValue + 4;
   }

   public int getSupplierColor() {
      return this.intSupplier != null ? this.intSupplier.getAsInt() : this.colorBase.getColor();
   }

   private void handleMaxAmount(double scissorValue, double minmaxValue, int flagHasCustomBounds, int scissorValue4, int scissorDivisor, int scissorDivisor2) {
      this.colorAmount = (float)Math.max(0.0, Math.min(1.0, (scissorValue - flagHasCustomBounds) / scissorDivisor));
      this.brightness = 1.0F - (float)Math.max(0.0, Math.min(1.0, (minmaxValue - scissorValue4) / scissorDivisor2));
      this.updateMaxColor();
   }

   private static int getValue(int scissorBound, int scissorBound2, int intersectsCenterBound) {
      return Math.max(scissorBound2, Math.min(intersectsCenterBound, scissorBound));
   }

   public boolean canFlag(int scissorValue, int scancode, int modifiers) {
      return false;
   }

   public boolean isFlag() {
      return this.popupFocused || this.popupHovered || this.scissorActive;
   }

   private void handleMax(double scissorValue, int flagHasCustomBounds, int scissorDivisor) {
      this.hue = (float)Math.max(0.0, Math.min(1.0, (scissorValue - flagHasCustomBounds) / scissorDivisor));
      this.updateMaxColor();
   }

   public void handleFlag(int value, int plusDismissValue, int materialFactor) {
      this.hasCustomBounds = true;
      this.scissorValue = value;
      this.customY = plusDismissValue;
      this.customWidth = materialFactor;
   }

   public void handle(int value, int editClusterValue, int width, int betaText) {
      this.scissorDisableValue = value;
      this.panelY = editClusterValue;
      this.panelWidth = width;
      this.panelHeight = betaText;
   }

   public void handleFlagForDoubleInt(double scissorValue, double mouseReleasedValue, int button) {
      this.popupFocused = false;
      this.popupHovered = false;
      this.scissorActive = false;
   }

   private void renderMax(DrawContext context, int scissorValue, int configsLikeValue, int scissorFactor, int scissorValue3) {
      byte scissorDivisor = 60;
      VertexConsumer quads = VertexGuiUtils.getQuads();

      for (int i = 0; i < scissorDivisor; i++) {
         float scissorValue4 = (float)i / (scissorDivisor - 1);
         int vertexColorValue = scissorValue + i * scissorFactor / scissorDivisor;
         int scissorValue6 = scissorValue + (i + 1) * scissorFactor / scissorDivisor - vertexColorValue;
         if (scissorValue6 < 1) {
            scissorValue6 = 1;
         }

         VertexGuiUtils.handleVertexColor(quads, vertexColorValue, configsLikeValue, vertexColorValue + scissorValue6, configsLikeValue + scissorValue3, getValue(0xFF000000 | Color.HSBtoRGB(scissorValue4, 1.0F, 1.0F) & 16777215));
      }

      VertexGuiUtils.updateBufferClose();
      int matrixStackValue = getValue(scissorValue + (int)(this.hue * scissorFactor), scissorValue, scissorValue + scissorFactor);
      int scissorValue8 = configsLikeValue + scissorValue3 / 2;
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, scissorValue8, 5, -872415232);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, scissorValue8, 4, -1);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, scissorValue8, 3, 0xFF000000 | Color.HSBtoRGB(this.hue, 1.0F, 1.0F) & 16777215);
   }

   private void renderForDrawContext(DrawContext context) {
      int scissorValue = this.getFlagHasCustomBounds();
      int scissorFactor = this.getMin();
      int scissorValue3 = this.getFlag();
      this.renderMaxAmountForDrawContextInt(context, scissorValue, scissorValue3, scissorFactor, 60);
      int scissorValue4 = scissorValue3 + 60 + 4;
      this.renderMax(context, scissorValue, scissorValue4, scissorFactor, 8);
      int configsLikeValue = scissorValue4 + 8 + 4;
      this.renderMaxAmount(context, scissorValue, configsLikeValue, scissorFactor, 8);
   }
}
