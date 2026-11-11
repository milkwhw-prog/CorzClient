package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.utils.PaletteBase;
import com.corz.client.utils.ToxicElectricUtils;

public class ThemeWhole {
   private float frameDeltaSeconds;
   private int paletteValue;
   private final float[] paletteAmounts;
   private final PaletteBase paletteBase;
   private long lastNanoTime;
   private int panelX;
   private int paletteWholeValue;
   private int themeWholeValue;
   private final float[] paletteWholeTable = new float[ToxicElectricUtils.motionTexturesValue];
   private final int[][] releaseRightInts;
   private int releaseRightValue;
   private long lastNanoTimePaletteWhole;
   private int paletteWholeValuePaletteWhol;

   private static int getValue(int themeWholeValue, int themeWholeValue2, float themeWholeValue3) {
      return 0xFF000000 | getValue(themeWholeValue | 0xFF000000, themeWholeValue2 | 0xFF000000, (double)themeWholeValue3) & 16777215;
   }

   public void handlePaletteWhole(int paletteWholeValue, int editClusterValue, int paletteValue, int value) {
      this.paletteWholeValuePaletteWhol = paletteWholeValue;
      this.paletteWholeValue = editClusterValue;
      this.paletteValue = paletteValue;
      this.themeWholeValue = value;
   }

   private static float getMax(float themeWholeLimit) {
      return themeWholeLimit < 0.0F ? 0.0F : (themeWholeLimit > 1.0F ? 1.0F : themeWholeLimit);
   }

   private static boolean canFlag(int mouseX, int mouseY, int boxX, int boxY, int boxWidth, int boxHeight) {
      return mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight;
   }

   private static int getValue(int themeWholeValue) {
      return 0xFF000000 | themeWholeValue & 16777215;
   }

   private float getMin(float current, float target, float speed) {
      float eased = current + (target - current) * (1.0F - (float)Math.exp(-speed * this.frameDeltaSeconds));
      return Math.abs(target - eased) < 0.0015F ? target : eased;
   }

   public void renderPaletteWhole(DrawContext context, int panelX, int panelY) {
      this.panelX = panelX;
      this.releaseRightValue = panelY;
      int instance = ToxicElectricUtils.getInstance();
      long nanoTime = System.nanoTime();
      boolean paletteWholeFlag = this.lastNanoTime == 0L || nanoTime - this.lastNanoTime > 250000000L;
      this.frameDeltaSeconds = paletteWholeFlag ? 0.0F : Math.min((float)(nanoTime - this.lastNanoTime) / 1.0E9F, 0.05F);
      this.lastNanoTime = nanoTime;
      if (paletteWholeFlag) {
         this.lastNanoTimePaletteWhole = nanoTime;

         for (int i = 0; i < this.paletteWholeTable.length; i++) {
            this.paletteWholeTable[i] = 0.0F;
            this.paletteAmounts[i] = i == instance ? 1.0F : 0.0F;
         }
      }

      float millis = (float)(nanoTime - this.lastNanoTimePaletteWhole) / 1000000.0F;
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int paletteWholeValue = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown();
      fontRenderer.handle("Click a palette to theme the whole client", this.paletteWholeValuePaletteWhol + 2, this.paletteWholeValue + 1, new Color(-7630437, true));
      int paletteWholeValue2 = this.paletteWholeValue + paletteWholeValue + 12;
      int texturesIconsFactor = (this.paletteValue - 4 - 18) / 4;
      int paletteWholeValue4 = this.getValue();

      for (int j = 0; j < ToxicElectricUtils.getCrimson().length; j++) {
         int paletteWholeFactor = j % 4;
         int paletteWholeFactor2 = j / 4;
         int paletteWholeValue5 = this.paletteWholeValuePaletteWhol + 2 + paletteWholeFactor * (texturesIconsFactor + 6);
         int paletteWholeValue6 = paletteWholeValue2 + paletteWholeFactor2 * (paletteWholeValue4 + 9);
         float releaseRightBound = getAmount(getMax((millis - j * 28.0F) / 300.0F));
         this.renderReleaseRight(context, fontRenderer, j, paletteWholeValue5, paletteWholeValue6, texturesIconsFactor, paletteWholeValue4, paletteWholeValue, j == instance, releaseRightBound);
      }
   }

   private static int getValue(int themeWholeValue, float themeWholeValue2) {
      return getValue(themeWholeValue, -1, themeWholeValue2);
   }

   private int getRendererFont() {
      return (int)CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown();
   }

   private int getValue() {
      return this.getRendererFont() + 7 + 22;
   }

   private void renderReleaseRight(DrawContext context, VertexTextureFont vertexTextureFont, int index, int releaseRightValue, int releaseRightValue2, int texturesIconsFactor, int releaseRightValue4, int releaseRightValue5, boolean releaseRightFlag, float releaseRightBound) {
      ToxicElectricUtils.Palette palette = ToxicElectricUtils.getCrimson()[index];
      float glfwLimit = releaseRightValue2 + (1.0F - releaseRightBound) * 10.0F;
      boolean canFlag2 = canFlag(this.panelX, this.releaseRightValue, releaseRightValue - 2, Math.round(glfwLimit) - 2, texturesIconsFactor + 4, releaseRightValue4 + 4);
      float pilotStatusBound = this.paletteWholeTable[index] = this.getMin(this.paletteWholeTable[index], canFlag2 ? 1.0F : 0.0F, canFlag2 ? 14.0F : 9.0F);
      float themeWholeLimit = this.paletteAmounts[index] = this.getMin(this.paletteAmounts[index], releaseRightFlag ? 1.0F : 0.0F, 9.0F);
      float f13 = glfwLimit - pilotStatusBound * 3.0F;
      float matrixPeekValue = releaseRightValue - 2.0F;
      float amountMax = f13 - 2.0F;
      float revOffset = texturesIconsFactor + 4.0F;
      float fontOffset = releaseRightValue4 + 4.0F;
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(Math.max(1.0E-4F, releaseRightBound));

      try {
         float releaseRightLimit = Math.max(themeWholeLimit * 0.16F, pilotStatusBound * 0.15F);
         if (releaseRightLimit > 0.004F) {
            MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, matrixPeekValue, amountMax, revOffset, fontOffset, 7.0F, getValueForIntFloat(MatrixEspUtils.releaseRightValue, releaseRightLimit), 7.0F, 0.42F);
         }

         int releaseRightValue10 = getValue(234881023, MatrixPeekUtilsRender.getReleaseRight(0.8), (double)(pilotStatusBound * 0.8F));
         int matrixStackValue = getValue(releaseRightValue10, -805306368 | MatrixEspUtils.releaseRightValue & 16777215, (double)themeWholeLimit);
         int releaseRightValue12 = CardMatrixUtils.getValue(-15723751, -1, 0.02 + 0.06 * Math.max(pilotStatusBound, themeWholeLimit));
         MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, amountMax, revOffset, fontOffset, 6.0F, releaseRightValue12, matrixStackValue);
         int releaseRightValue13 = getValue(getValue(-7630437, -920587, (double)pilotStatusBound), getValue(MatrixEspUtils.releaseRightValue, 0.3F), (double)themeWholeLimit);
         vertexTextureFont.handleForStringFloat(vertexTextureFont.formatText(palette.name(), texturesIconsFactor - 8), releaseRightValue + 1, f13, new Color(releaseRightValue13, true));
         if (themeWholeLimit > 0.01F) {
            MatrixPeekUtilsRender.renderMatrixPeek(context, releaseRightValue + texturesIconsFactor - 5.0F, f13 + 1.0F, 4.0F, 4.0F, 2.0F, getValueForIntFloat(MatrixEspUtils.releaseRightValue, themeWholeLimit));
         }

         float releaseRightValue14 = f13 + releaseRightValue5 + 7.0F;
         int[] colors = palette.colors();
         int releaseRightDivisor = colors.length;
         float releaseRightValue15 = 0.1F * pilotStatusBound;

         for (int j = 0; j < releaseRightDivisor; j++) {
            int releaseRightValue16 = releaseRightValue + (int)Math.round((double)texturesIconsFactor * j / releaseRightDivisor);
            int releaseRightValue17 = releaseRightValue + (int)Math.round((double)texturesIconsFactor * (j + 1) / releaseRightDivisor);
            float corzRoundedValue = j == 0 ? releaseRightValue16 - 1.0F : releaseRightValue16;
            float releaseRightValue19 = j == releaseRightDivisor - 1 ? releaseRightValue17 + 1.0F : releaseRightValue17;
            MatrixPeekUtilsRender.renderMatrixPeek(
               context, releaseRightValue, releaseRightValue14, texturesIconsFactor, 22.0F, 4.0F, getValue(getValue(colors[j]), -1, (double)releaseRightValue15), corzRoundedValue, releaseRightValue14 - 1.0F, releaseRightValue19 - corzRoundedValue, 24.0F
            );
         }

         int[] releaseRightValues = this.releaseRightInts[index];
         releaseRightValues[0] = releaseRightValue - 2;
         releaseRightValues[1] = Math.round(glfwLimit) - 2;
         releaseRightValues[2] = texturesIconsFactor + 4;
         releaseRightValues[3] = releaseRightValue4 + 4;
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   private static int getValueForIntFloat(int themeWholeValue, float themeWholeLimit) {
      int round = Math.round(255.0F * getMax(themeWholeLimit));
      return round << 24 | themeWholeValue & 16777215;
   }

   public boolean canTheme(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return false;
      } else {
         int mouseX2 = (int)mouseX;
         int mouseY2 = (int)mouseY;

         for (int i = 0; i < ToxicElectricUtils.getCrimson().length; i++) {
            int[] themeValues = this.releaseRightInts[i];
            if (themeValues[2] > 0 && canFlag(mouseX2, mouseY2, themeValues[0], themeValues[1], themeValues[2], themeValues[3])) {
               ToxicElectricUtils.Palette palette = ToxicElectricUtils.getCrimson()[i];
               ToxicElectricUtils.handleBaseInstance(palette);
               DisabledEnabled.getInstance().handleDisabledEnabled("Theme", palette.name() + " applied", true);
               return true;
            }
         }

         return false;
      }
   }

   private static float getAmount(float themeWholeValue) {
      themeWholeValue = getMax(themeWholeValue);
      float themeWholeFactor = 1.0F - themeWholeValue;
      return 1.0F - themeWholeFactor * themeWholeFactor * themeWholeFactor;
   }

   private static int getValue(int themeWholeValue, int themeWholeValue2, double themeWholeFactor) {
      themeWholeFactor = Math.max(0.0, Math.min(1.0, themeWholeFactor));
      int themeWholeValue3 = themeWholeValue >> 24 & 0xFF;
      int themeWholeValue4 = themeWholeValue >> 16 & 0xFF;
      int themeWholeValue5 = themeWholeValue >> 8 & 0xFF;
      int themeWholeValue6 = themeWholeValue & 0xFF;
      int themeWholeValue7 = themeWholeValue2 >> 24 & 0xFF;
      int themeWholeValue8 = themeWholeValue2 >> 16 & 0xFF;
      int themeWholeValue9 = themeWholeValue2 >> 8 & 0xFF;
      int themeWholeValue10 = themeWholeValue2 & 0xFF;
      return (int)(themeWholeValue3 + (themeWholeValue7 - themeWholeValue3) * themeWholeFactor) << 24
         | (int)(themeWholeValue4 + (themeWholeValue8 - themeWholeValue4) * themeWholeFactor) << 16
         | (int)(themeWholeValue5 + (themeWholeValue9 - themeWholeValue5) * themeWholeFactor) << 8
         | (int)(themeWholeValue6 + (themeWholeValue10 - themeWholeValue6) * themeWholeFactor);
   }

   public int getValueInt() {
      int themeWholeFactor = (ToxicElectricUtils.getCrimson().length + 4 - 1) / 4;
      return this.getRendererFont() + 12 + themeWholeFactor * this.getValue() + (themeWholeFactor - 1) * 9 + 2 + 2;
   }

   public ThemeWhole(PaletteBase paletteBase, int paletteWholeValue, int paletteWholeValue2, int paletteValue, int value) {
      this.paletteAmounts = new float[ToxicElectricUtils.motionTexturesValue];
      this.releaseRightInts = new int[ToxicElectricUtils.motionTexturesValue][4];
      this.frameDeltaSeconds = 0.0F;
      this.lastNanoTime = 0L;
      this.lastNanoTimePaletteWhole = 0L;
      this.paletteBase = paletteBase;
      this.paletteWholeValuePaletteWhol = paletteWholeValue;
      this.paletteWholeValue = paletteWholeValue2;
      this.paletteValue = paletteValue;
      this.themeWholeValue = value;
   }
}
