package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.CrossMatrixUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class CrossMatrix {
   private long lastNanoTime;
   private final long lastNanoTimeCross;
   private long fadeStartNanos = 0L;
   private String crossText;
   private final boolean cross;
   private static final int MAX_BOX_WIDTH_PX = CrossMatrixUtils.getValue(7, 20, 8, 203);
   private long crossValue;
   private static final int BACKGROUND_ALPHA_MASK = Math.round(178.5F) << 24;
   private static final Color OFF_WHITE = new Color(-920587, true);
   private final String crossMatrixText;
   private long crossMatrixValue;
   private static final Color GRAY = new Color(-7630437, true);
   private int contentHeightPx;
   private static final int UNIT_STEP = 1;
   private float smoothedCrossAmount;
   private static final Color RED = new Color(-1750963, true);
   private String subtitleText;
   private boolean crossActive = false;
   private final String rawCrossText;
   private float crossAmount;
   private int boxWidthPx;

   public int getValue() {
      return 34;
   }

   private static void handlePopPush(VertexTextureFont vertexTextureFont, String crossMatrixText, float crossMatrixValue, float crossMatrixValue2, float x2, Color color) {
      MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
      matrixStack.push();

      try {
         matrixStack.scale(x2, x2, 1.0F);
         vertexTextureFont.handleForStringFloat(crossMatrixText, crossMatrixValue / x2, crossMatrixValue2 / x2, color);
      } finally {
         matrixStack.pop();
      }
   }

   public void updateFlagTime() {
      if (!this.crossActive) {
         this.crossActive = true;
         this.crossValue = System.nanoTime();
      }
   }

   public void updateTimeFlag() {
      this.crossMatrixValue = System.nanoTime();
      this.fadeStartNanos = this.crossMatrixValue;
      this.crossActive = false;
   }

   private static boolean canFlag(MatrixStack matrices, MatrixStack matrices2, float crossMatrixValue, float y, float x2) {
      if (Math.abs(x2 - 1.0F) <= 5.0E-4F) {
         return false;
      } else {
         handleTranslatePush(matrices, crossMatrixValue, y, x2);
         if (matrices2 != matrices) {
            handleTranslatePush(matrices2, crossMatrixValue, y, x2);
         }

         return true;
      }
   }

   private static float getAmount(float crossMatrixLimit) {
      return crossMatrixLimit < 0.0F ? 0.0F : (crossMatrixLimit > 1.0F ? 1.0F : crossMatrixLimit);
   }

   private static void handleTranslatePush(MatrixStack matrices, float x, float y, float x2) {
      matrices.push();
      matrices.translate(x, y, 0.0F);
      matrices.scale(x2, x2, 1.0F);
      matrices.translate(-x, -y, 0.0F);
   }

   public boolean isFlag() {
      return this.crossActive;
   }

   public float getTimeFlag() {
      if (!this.crossActive) {
         return 1.0F;
      } else {
         float crossMatrixFactor = getAmount((float)(System.nanoTime() - this.crossValue) / 1000000.0F / 150.0F);
         return 1.0F - crossMatrixFactor * crossMatrixFactor;
      }
   }

   private void handleTextAmount(VertexTextureFont vertexTextureFont) {
      this.crossAmount = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzBrown();
      this.crossText = vertexTextureFont.formatText(this.rawCrossText, 210.52632F);
      this.subtitleText = this.crossMatrixText.isEmpty() ? "" : vertexTextureFont.formatText(this.crossMatrixText, 243.90244F);
      this.contentHeightPx = this.subtitleText.isEmpty() ? 11 : CrossMatrixUtils.getValueForInt(11, 10, 1);
      int crossMatrixValue = (int)Math.ceil(
         Math.max(vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.crossText) * 0.95F, vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.subtitleText) * 0.82F)
      );
      this.boxWidthPx = CrossMatrixUtils.getValue(CrossMatrixUtils.getValue(7, 20, 8, crossMatrixValue + 3), 140, MAX_BOX_WIDTH_PX);
   }

   private float getTime(long crossMatrixValue) {
      if (this.fadeStartNanos == 0L) {
         return 1.0F;
      } else {
         float crossMatrixLimit = (float)(crossMatrixValue - this.fadeStartNanos) / 1000000.0F / 240.0F;
         if (crossMatrixLimit >= 1.0F) {
            this.fadeStartNanos = 0L;
            return 1.0F;
         } else {
            return 1.0F + 0.14F * (float)Math.sin(Math.PI * MatrixStackUtilsAmount.getAmountForFloat(crossMatrixLimit));
         }
      }
   }

   private static float getAmount(float crossMatrixValue, float crossMatrixValue2, float crossMatrixValue3) {
      return crossMatrixValue + crossMatrixValue2 / 2.0F - 8.28F * crossMatrixValue3;
   }

   public void renderCrossAmount(DrawContext context, int crossValue, float amount) {
      long nanoTime = System.nanoTime();
      float millis = (float)(nanoTime - this.crossMatrixValue) / 1000000.0F;
      if (!this.crossActive && millis > 2000.0F) {
         this.updateFlagTime();
      }

      float panelOverlayLimit;
      float x2;
      if (this.crossActive) {
         float crossFactor = getAmount((float)(nanoTime - this.crossValue) / 1000000.0F / 150.0F);
         if (crossFactor >= 1.0F) {
            return;
         }

         panelOverlayLimit = 1.0F - crossFactor * crossFactor;
         x2 = 1.0F - 0.04000002F * crossFactor * crossFactor;
      } else {
         float millis2 = (float)(nanoTime - this.lastNanoTimeCross) / 1000000.0F;
         panelOverlayLimit = MatrixStackUtilsAmount.getAmountForFloat(millis2 / 130.0F);
         x2 = 0.94F + 0.060000002F * MatrixStackUtilsAmount.getAmount(millis2 / 220.0F, 1.7F);
      }

      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      if (fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() != this.crossAmount) {
         this.handleTextAmount(fontRenderer);
      }

      if (Float.isNaN(this.smoothedCrossAmount)) {
         this.smoothedCrossAmount = amount;
      } else {
         float min2 = Math.min(0.05F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
         this.smoothedCrossAmount = this.smoothedCrossAmount + (amount - this.smoothedCrossAmount) * (1.0F - (float)Math.exp(-16.0F * min2));
         if (Math.abs(amount - this.smoothedCrossAmount) < 0.01F) {
            this.smoothedCrossAmount = amount;
         }
      }

      this.lastNanoTime = nanoTime;
      boolean crossFlag = this.smoothedCrossAmount == amount;
      float crossValue4 = crossValue - this.boxWidthPx;
      float crossValue5 = crossFlag ? NativeHud.getAmount4(this.smoothedCrossAmount) : this.smoothedCrossAmount;
      float crossValue6 = MatrixPeekUtilsRender.getAmount();
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      MatrixStack matrices = CorzClient.fontManager.getMatrixStack();
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);
      boolean crossMatrixFlag = canFlag(matrixStack, matrices, crossValue4 + this.boxWidthPx / 2.0F, crossValue5 + 17.0F, x2);

      try {
         NativeHud.handle(NativeHud.resizeScrollValue, crossValue4, crossValue5, (float)this.boxWidthPx, 34.0F, 1.0F);
         int crossValue7 = (this.cross ? MatrixEspUtils.releaseRightValue : -1750963) & 16777215;
         float nativeHudValue = crossValue4 + 7.0F;
         float crossValue9 = crossValue5 + 7.0F;
         float crossValue10 = 4.0F;
         boolean canFlag3 = canFlag(matrixStack, matrices, nativeHudValue + 10.0F, crossValue9 + 10.0F, this.getTime(nanoTime));

         try {
            NativeHud.handle(nativeHudValue, crossValue9, 20.0F, 20.0F, 6.0F, 1.0F);
            MatrixPeekUtilsIdentifier.handle(
               nativeHudValue + crossValue10,
               crossValue9 + crossValue10,
               12.0F,
               12.0F,
               IcoGenUtils.getIdentifierForString(this.cross ? "check" : "cross"),
               this.cross ? NativeHud.getWhite() : RED,
               matrixStack
            );
         } finally {
            handlePop(matrixStack, matrices, canFlag3);
         }

         float crossValue11 = nativeHudValue + 20.0F + 8.0F;
         float crossValue12 = crossValue5 + (34 - this.contentHeightPx) / 2.0F;
         float crossValue13 = getAmount(crossValue12, 11.0F, 0.95F);
         handlePopPush(fontRenderer, this.crossText, crossValue11, crossFlag ? NativeHud.getAmount4(crossValue13) : crossValue13, 0.95F, OFF_WHITE);
         if (!this.subtitleText.isEmpty()) {
            float crossValue14 = getAmount(crossValue12 + 11.0F + 1.0F, 10.0F, 0.82F);
            handlePopPush(fontRenderer, this.subtitleText, crossValue11, crossFlag ? NativeHud.getAmount4(crossValue14) : crossValue14, 0.82F, GRAY);
         }

         float peekPositionValue = crossValue4 + 8.0F;
         float matrixStackValue = crossValue5 + 34.0F - crossValue6 * 2.0F;
         float crossFactor3 = this.boxWidthPx - 16.0F;
         NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, crossFactor3, crossValue6, 0.0F, 0.0F, 318767103);
         float crossLimit = NativeHud.getAmount4(crossFactor3 * (1.0F - Math.min(1.0F, millis / 2000.0F)));
         if (crossLimit > 0.0F) {
            NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, crossLimit, crossValue6, 0.0F, 0.0F, BACKGROUND_ALPHA_MASK | crossValue7);
         }
      } finally {
         handlePop(matrixStack, matrices, crossMatrixFlag);
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   public CrossMatrix(String crossMatrixText, String crossMatrixText2, boolean cross) {
      this.crossValue = 0L;
      this.smoothedCrossAmount = Float.NaN;
      this.lastNanoTime = 0L;
      this.crossAmount = -1.0F;
      this.crossText = "";
      this.subtitleText = "";
      this.boxWidthPx = 140;
      this.contentHeightPx = 11;
      this.rawCrossText = crossMatrixText == null ? "" : crossMatrixText;
      this.crossMatrixText = crossMatrixText2 == null ? "" : crossMatrixText2;
      this.cross = cross;
      this.lastNanoTimeCross = System.nanoTime();
      this.crossMatrixValue = this.lastNanoTimeCross;
   }

   private static void handlePop(MatrixStack matrices, MatrixStack matrixStack, boolean crossMatrixFlag) {
      if (crossMatrixFlag) {
         if (matrixStack != matrices) {
            matrixStack.pop();
         }

         matrices.pop();
      }
   }

   public boolean canCrossText(String crossText, String substringText, boolean crossFlag) {
      return this.cross == crossFlag && this.rawCrossText.equals(crossText == null ? "" : crossText) && this.crossMatrixText.equals(substringText == null ? "" : substringText);
   }

   public boolean isTimeFlag() {
      return this.crossActive && (float)(System.nanoTime() - this.crossValue) / 1000000.0F >= 150.0F;
   }
}
