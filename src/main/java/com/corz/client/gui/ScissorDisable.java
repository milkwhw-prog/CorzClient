package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class ScissorDisable {
   private final QuintScissor toggleAnimation;
   private double slideDirection;
   private int panelWidth;
   private int rendererFontValue;
   private int scissorDisableValue;
   private String scissorDisableText;
   private String previousText;
   private final QuintScissor scissorDisableQuintScissor;
   private double scissorDisableAmount = 1.0;
   private int leftX;
   private final EnumSetting next;

   public ScissorDisable(EnumSetting next, int value, int value2, int value3, int value4) {
      this.slideDirection = 1.0;
      this.next = next;
      this.leftX = value;
      this.rendererFontValue = value2;
      this.panelWidth = value3;
      this.scissorDisableValue = value4;
      this.toggleAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 200, 0.0);
      this.scissorDisableQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 180, 1.0);
      this.scissorDisableText = next.getCurrentOption();
      this.previousText = null;
   }

   private int getValue(int scissorDisableValue, int scissorDisableValue2, double scissorDisableFactor) {
      scissorDisableFactor = Math.max(0.0, Math.min(1.0, scissorDisableFactor));
      int scissorDisableValue3 = scissorDisableValue >> 24 & 0xFF;
      int scissorDisableValue4 = scissorDisableValue >> 16 & 0xFF;
      int scissorDisableValue5 = scissorDisableValue >> 8 & 0xFF;
      int scissorDisableValue6 = scissorDisableValue & 0xFF;
      int scissorDisableValue7 = scissorDisableValue2 >> 24 & 0xFF;
      int scissorDisableValue8 = scissorDisableValue2 >> 16 & 0xFF;
      int scissorDisableValue9 = scissorDisableValue2 >> 8 & 0xFF;
      int scissorDisableValue10 = scissorDisableValue2 & 0xFF;
      return (int)(scissorDisableValue3 + (scissorDisableValue7 - scissorDisableValue3) * scissorDisableFactor) << 24
         | (int)(scissorDisableValue4 + (scissorDisableValue8 - scissorDisableValue4) * scissorDisableFactor) << 16
         | (int)(scissorDisableValue5 + (scissorDisableValue9 - scissorDisableValue5) * scissorDisableFactor) << 8
         | (int)(scissorDisableValue6 + (scissorDisableValue10 - scissorDisableValue6) * scissorDisableFactor);
   }

   private static int getValue(int scissorDisableValue, int scissorDisableValue2) {
      return scissorDisableValue2 << 24 | scissorDisableValue & 16777215;
   }

   public int getScissorDisableValue() {
      return this.scissorDisableValue;
   }

   private void renderScissorDisable(DrawContext context, int scissorDisableValue, int brownValue, int materialFactor, int boxHeight, int scissorDisableValue5, double scissorDisableFactor) {
      int ceil6 = (int)Math.ceil(3.5);
      int scissorDisableLimit = scissorDisableValue + 8 + ceil6 + 3;
      int scissorDisableValue7 = scissorDisableValue + materialFactor - 8 - ceil6 - 3;
      if (scissorDisableValue7 <= scissorDisableLimit) {
         scissorDisableLimit = scissorDisableValue + 2;
         scissorDisableValue7 = scissorDisableValue + materialFactor - 2;
      }

      int abcdefghijklmnopqrstuvwxyzBrown = CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, boxHeight);
      context.enableScissor(scissorDisableLimit, brownValue, scissorDisableValue7, brownValue + boxHeight);

      try {
         if (this.previousText != null && scissorDisableFactor < 0.999) {
            float scissorDisableValue9 = (float)(-this.scissorDisableAmount * 8.0 * scissorDisableFactor);
            this.renderRendererFont(context, this.previousText, scissorDisableValue, materialFactor, abcdefghijklmnopqrstuvwxyzBrown, scissorDisableValue9, -1, 1.0 - scissorDisableFactor);
         }

         float scissorDisableValue10 = (float)(this.scissorDisableAmount * 8.0 * (1.0 - scissorDisableFactor));
         int scissorDisableValue11 = this.getValue(MatrixEspUtils.releaseRightValue, -1, scissorDisableFactor);
         this.renderRendererFont(context, this.scissorDisableText, scissorDisableValue, materialFactor, abcdefghijklmnopqrstuvwxyzBrown, scissorDisableValue10, scissorDisableValue11, scissorDisableFactor);
      } finally {
         context.disableScissor();
      }
   }

   public void handle(int value, int y, int width, int betaText) {
      this.leftX = value;
      this.rendererFontValue = y;
      this.panelWidth = width;
      this.scissorDisableValue = betaText;
   }

   private void renderRendererFont(DrawContext context, String rendererFontText, int rendererFontValue, int materialFactor, int abcdefghijklmnopqrstuvwxyzBrown, float rendererFontValue4, int rendererFontValue5, double scissorDisableFactor) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int rendererFontValue7 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(rendererFontText);
      float vertexTextureValue = rendererFontValue + (materialFactor - rendererFontValue7) / 2.0F + rendererFontValue4;
      int max2 = Math.max(0, Math.min(255, (int)Math.round(scissorDisableFactor * 255.0)));
      fontRenderer.handleForStringFloat(rendererFontText, vertexTextureValue, abcdefghijklmnopqrstuvwxyzBrown, new Color(rendererFontValue5 >> 16 & 0xFF, rendererFontValue5 >> 8 & 0xFF, rendererFontValue5 & 0xFF, max2));
   }

   public boolean canAmount(double scissorDisableLimit, double mouseY, int scissorDisableValue) {
      if (scissorDisableValue != 0) {
         return false;
      } else {
         int[] rendererFont = this.getRendererFont();
         int scissorDisableValue2 = rendererFont[0];
         int scissorDisableValue3 = rendererFont[1];
         int scissorDisableValue4 = rendererFont[2];
         int scissorDisableValue5 = rendererFont[3];
         if (scissorDisableLimit >= scissorDisableValue2 && scissorDisableLimit <= scissorDisableValue2 + scissorDisableValue4 && mouseY >= scissorDisableValue3 && mouseY <= scissorDisableValue3 + scissorDisableValue5) {
            if (scissorDisableLimit < scissorDisableValue2 + scissorDisableValue4 / 2.0) {
               this.slideDirection = -1.0;
               this.next.selectPrevious();
            } else {
               this.slideDirection = 1.0;
               this.next.selectNext();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private int[] getRendererFont() {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int rendererFontBound = 0;

      for (String rendererFontText : this.next.getOptions()) {
         rendererFontBound = Math.max(rendererFontBound, (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(rendererFontText));
      }

      int rendererFontLimit = Math.min(this.panelWidth - 8, Math.max(92, rendererFontBound + 46));
      int aggroPreemptBound = this.panelWidth - 8 - (int)Math.ceil(fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.next.getSettingName())) - 6;
      if (rendererFontLimit > aggroPreemptBound) {
         rendererFontLimit = Math.max(aggroPreemptBound, Math.min(rendererFontLimit, rendererFontBound + 34));
      }

      int rendererFontValue = this.leftX + this.panelWidth - 4 - rendererFontLimit;
      int rendererFontValue2 = this.rendererFontValue + this.scissorDisableValue / 2 - 9;
      return new int[]{rendererFontValue, rendererFontValue2, rendererFontLimit, 18};
   }

   public void renderTextTime(DrawContext context, int scissorDisableLimit, int mouseY) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int scissorDisableValue = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown();
      fontRenderer.handleForStringFloat(this.next.getSettingName(), this.leftX + 4, fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(this.rendererFontValue, this.scissorDisableValue), new Color(-3947581));
      int[] rendererFont = this.getRendererFont();
      int matrixPeekValue = rendererFont[0];
      int brownValue = rendererFont[1];
      int materialFactor = rendererFont[2];
      int boxHeight = rendererFont[3];
      boolean scissorDisableFlag = scissorDisableLimit >= matrixPeekValue && scissorDisableLimit <= matrixPeekValue + materialFactor && mouseY >= brownValue && mouseY <= brownValue + boxHeight;
      double scissorDisableValue6 = scissorDisableFlag ? 1.0 : 0.0;
      if (Math.abs(this.toggleAnimation.getTargetAmount() - scissorDisableValue6) > 0.01) {
         this.toggleAnimation.handleAmountTime(scissorDisableValue6);
      }

      double scissorDisableFactor = this.toggleAnimation.getAmount();
      String text = this.next.getCurrentOption();
      if (!text.equals(this.scissorDisableText)) {
         this.previousText = this.scissorDisableText;
         this.scissorDisableText = text;
         this.scissorDisableAmount = this.slideDirection;
         this.scissorDisableQuintScissor.handleAmountFlag(0.0);
         this.scissorDisableQuintScissor.handleAmountTime(1.0);
      }

      double scissorDisableLimit3 = this.scissorDisableQuintScissor.getAmount();
      if (scissorDisableLimit3 >= 0.999) {
         this.previousText = null;
      }

      int matrixStackValue = this.getValue(getValue(16777215, 18), getValue(MatrixEspUtils.releaseRightValue, 160), scissorDisableFactor);
      MatrixPeekUtilsRender.render(context, matrixPeekValue, brownValue, materialFactor, boxHeight, 4, -16448249, matrixStackValue);
      boolean scissorDisableFlag2 = scissorDisableFlag && scissorDisableLimit < matrixPeekValue + materialFactor / 2.0;
      float scissorDisableValue9 = brownValue + boxHeight / 2.0F;
      this.render(context, matrixPeekValue + 8, scissorDisableValue9, false, this.getValue(-8421505, MatrixEspUtils.releaseRightValue, scissorDisableFlag2 ? scissorDisableFactor : 0.0));
      this.render(context, matrixPeekValue + materialFactor - 8, scissorDisableValue9, true, this.getValue(-8421505, MatrixEspUtils.releaseRightValue, scissorDisableFlag && !scissorDisableFlag2 ? scissorDisableFactor : 0.0));
      this.renderScissorDisable(context, matrixPeekValue, brownValue, materialFactor, boxHeight, scissorDisableValue, scissorDisableLimit3);
   }

   public boolean canFlag(double scissorDisableLimit, double scissorDisableLimit2) {
      return scissorDisableLimit >= this.leftX && scissorDisableLimit <= this.leftX + this.panelWidth && scissorDisableLimit2 >= this.rendererFontValue && scissorDisableLimit2 <= this.rendererFontValue + this.scissorDisableValue;
   }

   private void render(DrawContext context, float scissorDisableValue, float scissorDisableValue2, boolean scissorDisableFlag, int scissorDisableValue3) {
      if ((scissorDisableValue3 >>> 24 & 0xFF) == 0) {
         scissorDisableValue3 |= -16777216;
      }

      float peekPositionFactor = 7.0F;
      float matrixPeekValue = scissorDisableValue - peekPositionFactor / 2.0F;
      float roundOffset = scissorDisableValue2 - peekPositionFactor / 2.0F;
      Color color = new Color(scissorDisableValue3, true);
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      if (scissorDisableFlag) {
         MatrixPeekUtilsIdentifier.handle(matrixPeekValue, roundOffset, peekPositionFactor, peekPositionFactor, IcoGenUtils.getChevronIco(), color, matrixStack);
      } else {
         MatrixPeekUtilsIdentifier.handlePeekPosition(matrixPeekValue, roundOffset, peekPositionFactor, peekPositionFactor, 1.0F, 0.0F, 0.0F, 1.0F, IcoGenUtils.getChevronIco(), color, matrixStack);
      }
   }
}
