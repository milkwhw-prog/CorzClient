package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class RendererPress {
   private final ButtonBase next;
   private int buttonX;
   private final QuintScissor hoverAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 180, 0.0);
   private int scaleValue;
   private int rendererValue;
   private int rendererFontValue;
   private final QuintScissor rendererQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 260, 0.0);

   public void handle(int x, int y, int width, int height) {
      this.rendererValue = x;
      this.buttonX = y;
      this.rendererFontValue = width;
      this.scaleValue = height;
   }

   public RendererPress(ButtonBase next, int x, int y, int width, int height) {
      this.next = next;
      this.rendererValue = x;
      this.buttonX = y;
      this.rendererFontValue = width;
      this.scaleValue = height;
   }

   private int getValue(int colorA, int colorB, double factor) {
      factor = Math.max(0.0, Math.min(1.0, factor));
      int alphaA = colorA >> 24 & 0xFF;
      int redA = colorA >> 16 & 0xFF;
      int greenA = colorA >> 8 & 0xFF;
      int blueA = colorA & 0xFF;
      int alphaB = colorB >> 24 & 0xFF;
      int redB = colorB >> 16 & 0xFF;
      int greenB = colorB >> 8 & 0xFF;
      int blueB = colorB & 0xFF;
      return (int)(alphaA + (alphaB - alphaA) * factor) << 24
         | (int)(redA + (redB - redA) * factor) << 16
         | (int)(greenA + (greenB - greenA) * factor) << 8
         | (int)(blueA + (blueB - blueA) * factor);
   }

   private static int getValue(VertexTextureFont vertexTextureFont, String text, float maxWidth) {
      int lastSpaceIndex = -1;

      for (int i = text.indexOf(32);
         i > 0 && !(vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(text.substring(0, i)) > maxWidth);
         i = text.indexOf(32, i + 1)
      ) {
         lastSpaceIndex = i;
      }

      if (lastSpaceIndex > 0) {
         return lastSpaceIndex;
      } else {
         int textLength = text.length();

         while (textLength > 1 && vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(text.substring(0, textLength)) > maxWidth) {
            textLength--;
         }

         return textLength;
      }
   }

   private int[] getRendererFont() {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int rawTextWidth = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.next.getText());
      int clampedWidth = Math.min(this.rendererFontValue - 8, Math.max(56, rawTextWidth + 22));
      int boxX = this.rendererValue + this.rendererFontValue - 4 - clampedWidth;
      int boxY = this.buttonX + this.scaleValue / 2 - 9;
      return new int[]{boxX, boxY, clampedWidth, 18};
   }

   public boolean canTime(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return false;
      } else {
         int[] rendererFont = this.getRendererFont();
         if (mouseX >= rendererFont[0] && mouseX <= rendererFont[0] + rendererFont[2] && mouseY >= rendererFont[1] && mouseY <= rendererFont[1] + rendererFont[3]) {
            this.next.updateAction();
            this.rendererQuintScissor.handleAmountTime(1.0);
            return true;
         } else {
            return false;
         }
      }
   }

   private void handlePushScale(VertexTextureFont vertexTextureFont, String scaleText, int textX, int maxWidth, int lineHeight) {
      if (scaleText != null && !scaleText.isEmpty() && maxWidth > 0) {
         Color color = new Color(-3947581);
         if (vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(scaleText) <= maxWidth) {
            vertexTextureFont.handleForStringFloat(scaleText, textX, vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzBrown(this.buttonX, this.scaleValue), color);
         } else {
            float roomScaled = maxWidth / 0.82F;
            String firstLine = scaleText;
            String secondLine = "";
            if (vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(scaleText) > roomScaled) {
               int end = getValue(vertexTextureFont, scaleText, roomScaled);
               firstLine = scaleText.substring(0, end).stripTrailing();
               secondLine = vertexTextureFont.formatText(scaleText.substring(end).stripLeading(), roomScaled);
            }

            float charHeight = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzBrown() * 0.82F;
            float drawY = secondLine.isEmpty() ? this.buttonX + (this.scaleValue - charHeight) / 2.0F - 1.0F : this.buttonX + (this.scaleValue - charHeight * 2.0F - 1.0F) / 2.0F - 1.0F;
            MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
            matrixStack.push();
            matrixStack.scale(0.82F, 0.82F, 1.0F);
            vertexTextureFont.handleForStringFloat(firstLine, textX / 0.82F, drawY / 0.82F, color);
            if (!secondLine.isEmpty()) {
               vertexTextureFont.handleForStringFloat(secondLine, textX / 0.82F, (drawY + charHeight + 1.0F) / 0.82F, color);
            }

            matrixStack.pop();
         }
      }
   }

   public int getScaleValue() {
      return this.scaleValue;
   }

   public void renderTimeRenderer(DrawContext context, int mouseX, int mouseY) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int lineHeight = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown();
      int textX = this.rendererValue + 4;
      int supplier = this.next.getSupplier();
      if (supplier != 0) {
         byte iconSize = 2;
         int iconX = this.rendererValue + 4 + iconSize;
         int iconY = this.buttonX + this.scaleValue / 2;
         MatrixPeekUtilsRender.renderMatrixStack(context, iconX - iconSize, iconY - iconSize, iconSize * 2, iconSize * 2, iconSize, supplier, 4.5F, 0.5F);
         MatrixPeekUtilsRender.renderMatrixStack(context, iconX - iconSize, iconY - iconSize, iconSize * 2, iconSize * 2, iconSize, supplier);
         textX = iconX + iconSize + 6;
      }

      int[] rendererFont = this.getRendererFont();
      int boxX = rendererFont[0];
      int boxY = rendererFont[1];
      int boxWidth = rendererFont[2];
      int boxHeight = rendererFont[3];
      this.handlePushScale(fontRenderer, this.next.formatSupplier(), textX, boxX - 6 - textX, lineHeight);
      boolean hovered = mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight;
      if (Math.abs(this.hoverAnimation.getTargetAmount() - (hovered ? 1 : 0)) > 0.01) {
         this.hoverAnimation.handleAmountTime(hovered ? 1.0 : 0.0);
      }

      if (this.rendererQuintScissor.getAmount() > 0.99 && this.rendererQuintScissor.getTargetAmount() == 1.0) {
         this.rendererQuintScissor.handleAmountTime(0.0);
      }

      double hoverAmount = this.hoverAnimation.getAmount();
      double pressAmount = this.rendererQuintScissor.getAmount();
      double animAmount = Math.max(hoverAmount, pressAmount);
      boolean buttonActive = this.next.isButtonActive();
      int colorA = this.getValue(-15461350, buttonActive ? -14411235 : -14737624, hoverAmount);
      int matrixStackValue = this.getValue(colorA, buttonActive ? -14411235 : -16184564, pressAmount * 0.5);
      int borderColor = buttonActive ? this.getValue(MatrixPeekUtilsRender.getReleaseRight(0.0), -1931130803, animAmount) : MatrixPeekUtilsRender.getReleaseRight(animAmount);
      MatrixPeekUtilsRender.render(context, boxX, boxY, boxWidth, boxHeight, 5, matrixStackValue, borderColor);
      int textColor = this.getValue(-1, buttonActive ? -1750963 : MatrixEspUtils.releaseRightValue, animAmount * 0.8);
      int textWidth = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.next.getText());
      fontRenderer.handleForStringFloat(this.next.getText(), boxX + (boxWidth - textWidth) / 2, fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(boxY, boxHeight), new Color(textColor, true));
   }
}
