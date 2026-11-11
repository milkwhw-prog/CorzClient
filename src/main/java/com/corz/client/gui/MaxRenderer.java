package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.config.StringBase;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class MaxRenderer {
   private int fieldWidth;
   private int cursorIndex;
   private final QuintScissor focusAnimation;
   private int releaseValue;
   private int startRelease;
   private final ClearEdit rendererFontClearEdit = new ClearEdit();
   private boolean releaseActive;
   private int rendererFontValue;
   private int rendererValue;
   private final StringBase next;

   public boolean canMaxTime(char rendererLimit, int rendererValue) {
      if (this.releaseActive && rendererLimit >= ' ' && rendererLimit != 127) {
         String rendererText = this.next.getCurrentText();
         this.next.handleText(rendererText.substring(0, this.cursorIndex) + rendererLimit + rendererText.substring(this.cursorIndex));
         this.cursorIndex++;
         this.rendererFontClearEdit.updateTime();
         return true;
      } else {
         return false;
      }
   }

   public void renderMaxRelease(DrawContext context, int releaseLimit, int mouseY) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      fontRenderer.handleForStringFloat(this.next.getSettingName(), this.rendererFontValue + 4, this.releaseValue + 3, new Color(-3947581));
      int releaseValue = this.rendererFontValue + 4;
      int brownValue = this.getMin();
      int materialFactor = this.fieldWidth - 8;
      boolean releaseFlag = releaseLimit >= releaseValue && releaseLimit <= releaseValue + materialFactor && mouseY >= brownValue && mouseY <= brownValue + 20;
      handle(this.focusAnimation, !releaseFlag && !this.releaseActive ? 0.0 : 1.0);
      double cardMatrixFactor = getAmount(this.focusAnimation.getAmount());
      int matrixStackValue = CardMatrixUtils.getValue(getValue(16777215, 18), getValue(MatrixEspUtils.releaseRightValue, 255), cardMatrixFactor);
      MatrixPeekUtilsRender.render(context, releaseValue, brownValue, materialFactor, 20, 6, CardMatrixUtils.getValue7(), matrixStackValue);
      int vertexTextureValue = releaseValue + 6;
      int abcdefghijklmnopqrstuvwxyzBrown = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, 20);
      String releaseText = this.next.getCurrentText();
      this.cursorIndex = Math.max(0, Math.min(this.cursorIndex, releaseText.length()));
      float rendererFontLimit = releaseValue + materialFactor - 6 - vertexTextureValue;
      if (releaseText.isEmpty() && !this.releaseActive) {
         fontRenderer.handleForStringFloat("Type here...", vertexTextureValue, abcdefghijklmnopqrstuvwxyzBrown, new Color(-8421505));
         this.startRelease = 0;
      } else {
         if (this.cursorIndex < this.startRelease) {
            this.startRelease = this.cursorIndex;
         }

         while (this.startRelease < this.cursorIndex && fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(releaseText.substring(this.startRelease, this.cursorIndex)) > rendererFontLimit) {
            this.startRelease++;
         }

         if (this.startRelease > releaseText.length()) {
            this.startRelease = 0;
         }

         String vertexTextureText = formatRendererFont(releaseText.substring(this.startRelease), rendererFontLimit);
         fontRenderer.handleForStringFloat(vertexTextureText, vertexTextureValue, abcdefghijklmnopqrstuvwxyzBrown, new Color(-1));
         if (this.releaseActive) {
            float releaseValue9 = vertexTextureValue + fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(releaseText.substring(this.startRelease, this.cursorIndex));
            int n4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() - 2;
            this.rendererFontClearEdit.renderAmountTime(context, releaseValue9, brownValue + (20 - n4) / 2, n4, MatrixEspUtils.releaseRightValue, MatrixClearUtils.getAmount());
         }
      }
   }

   public boolean canRendererFont(double rendererFontLimit, double mouseY, int rendererFontValue) {
      if (rendererFontValue != 0) {
         return false;
      } else {
         int rendererFontValue2 = this.rendererFontValue + 4;
         int min3 = this.getMin();
         int rendererFontValue4 = this.fieldWidth - 8;
         boolean rendererFontFlag = rendererFontLimit >= rendererFontValue2 && rendererFontLimit <= rendererFontValue2 + rendererFontValue4 && mouseY >= min3 && mouseY <= min3 + 20;
         if (rendererFontFlag) {
            this.handleMaxTime(true);
            int rendererFontValue5 = rendererFontValue2 + 6;
            String rendererFontText = this.next.getCurrentText();
            int max = rendererFontText.length();
            double maxValue = Double.MAX_VALUE;

            for (int i = Math.min(this.startRelease, rendererFontText.length()); i <= rendererFontText.length(); i++) {
               double rendererFontValue6 = rendererFontValue5
                  + CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont(rendererFontText.substring(Math.min(this.startRelease, rendererFontText.length()), i));
               double abs7 = Math.abs(rendererFontValue6 - rendererFontLimit);
               if (abs7 < maxValue) {
                  maxValue = abs7;
                  max = i;
               }
            }

            this.cursorIndex = max;
            this.rendererFontClearEdit
               .handleAmount(
                  rendererFontValue5 + CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont(rendererFontText.substring(Math.min(this.startRelease, rendererFontText.length()), max))
               );
            this.rendererFontClearEdit.updateTime();
            return true;
         } else {
            this.handleMaxTime(false);
            return false;
         }
      }
   }

   private static int getValue(int rendererValue, int rendererValue2) {
      return (rendererValue2 & 0xFF) << 24 | rendererValue & 16777215;
   }

   public boolean canFlag(double rendererLimit, double rendererLimit2) {
      return rendererLimit >= this.rendererFontValue && rendererLimit <= this.rendererFontValue + this.fieldWidth && rendererLimit2 >= this.releaseValue && rendererLimit2 <= this.releaseValue + this.rendererValue;
   }

   public boolean isFlag() {
      return this.releaseActive;
   }

   public MaxRenderer(StringBase next, int value, int value2, int value3, int value4) {
      this.startRelease = 0;
      this.next = next;
      this.rendererFontValue = value;
      this.releaseValue = value2;
      this.fieldWidth = value3;
      this.rendererValue = value4;
      this.cursorIndex = next.getCurrentText().length();
      this.releaseActive = false;
      this.focusAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 200, 0.0);
   }

   private int getMin() {
      int rendererBound = this.releaseValue + 3 + 9 + 7;
      int spotBound = this.releaseValue + this.rendererValue - 20 - 3;
      return Math.min(rendererBound, spotBound);
   }

   public void handle(int value, int y, int width, int betaText) {
      this.rendererFontValue = value;
      this.releaseValue = y;
      this.fieldWidth = width;
      this.rendererValue = betaText;
   }

   private static double getAmount(double rendererLimit) {
      return rendererLimit < 0.0 ? 0.0 : (rendererLimit > 1.0 ? 1.0 : rendererLimit);
   }

   private static String formatRendererFont(String formatRendererFont, float rendererFontLimit) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      if (rendererFontLimit <= 0.0F) {
         return "";
      } else if (fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatRendererFont) <= rendererFontLimit) {
         return formatRendererFont;
      } else {
         String corzfontText = formatRendererFont;

         while (corzfontText.length() > 1 && fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) > rendererFontLimit) {
            corzfontText = corzfontText.substring(0, corzfontText.length() - 1);
         }

         return corzfontText;
      }
   }

   private static void handle(QuintScissor quintScissor, double rendererValue) {
      if (Math.abs(quintScissor.getTargetAmount() - rendererValue) > 0.001) {
         quintScissor.handleAmountTime(rendererValue);
      }
   }

   public boolean canMaxTime(int rendererValue, int scancode, int modifiers) {
      if (!this.releaseActive) {
         return false;
      } else {
         String rendererText = this.next.getCurrentText();
         if (rendererValue == 259 && this.cursorIndex > 0) {
            this.next.handleText(rendererText.substring(0, this.cursorIndex - 1) + rendererText.substring(this.cursorIndex));
            this.cursorIndex--;
            this.rendererFontClearEdit.updateTime();
            return true;
         } else if (rendererValue == 261 && this.cursorIndex < rendererText.length()) {
            this.next.handleText(rendererText.substring(0, this.cursorIndex) + rendererText.substring(this.cursorIndex + 1));
            this.rendererFontClearEdit.updateTime();
            return true;
         } else if (rendererValue == 263 && this.cursorIndex > 0) {
            this.cursorIndex--;
            this.rendererFontClearEdit.updateTime();
            return true;
         } else if (rendererValue == 262 && this.cursorIndex < rendererText.length()) {
            this.cursorIndex++;
            this.rendererFontClearEdit.updateTime();
            return true;
         } else if (rendererValue == 268) {
            this.cursorIndex = 0;
            this.rendererFontClearEdit.updateTime();
            return true;
         } else if (rendererValue == 269) {
            this.cursorIndex = rendererText.length();
            this.rendererFontClearEdit.updateTime();
            return true;
         } else if (rendererValue != 257 && rendererValue != 256) {
            return false;
         } else {
            this.releaseActive = false;
            return true;
         }
      }
   }

   public void handleMaxTime(boolean flag) {
      this.releaseActive = flag;
      if (flag) {
         this.cursorIndex = this.next.getCurrentText().length();
         this.rendererFontClearEdit.updateTime();
      }
   }
}
