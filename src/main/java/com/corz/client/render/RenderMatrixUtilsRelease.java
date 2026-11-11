package com.corz.client.render;

import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.TwoDidUtils;
import com.corz.client.gui.VertexGuiUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;

public final class RenderMatrixUtilsRelease {

   private static int getValue(double matrixReleaseLimit) {
      return matrixReleaseLimit > 0.004 ? CardMatrixUtils.getValue(234881023, CardMatrixUtils.getReleaseRight(0.42), matrixReleaseLimit) : 234881023;
   }

   public static void renderReleaseRight(DrawContext context, int releaseRightValue, int editValue, int releaseRightLimit, int n4) {
      if (releaseRightLimit > 0) {
         int iconSize = n4 / 2;
         int releaseRightValue5 = MatrixEspUtils.releaseRightValue;
         int cardMatrixValue = CardMatrixUtils.getValue(-14673876, releaseRightValue5, 0.4);
         int releaseRight = CardMatrixUtils.getValue(-14410956, releaseRightValue5, 0.64);
         MatrixPeekUtilsRender.renderMatrixStack(context, releaseRightValue, editValue, releaseRightLimit, n4, iconSize, CardMatrixUtils.getReleaseRight(0.34), 3.5F, 0.34F);
         MatrixPeekUtilsRender.renderMatrixStack(context, releaseRightValue, editValue, releaseRightLimit, n4, iconSize, cardMatrixValue);
         int releaseRightValue8 = releaseRightValue + iconSize;
         int releaseRightValue9 = releaseRightValue + releaseRightLimit;
         if (releaseRightValue9 - releaseRightValue8 >= 1) {
            VertexConsumer quads = VertexGuiUtils.getQuads();
            int releaseRightDivisor = Math.max(1, (releaseRightValue9 - releaseRightValue8) / 2);

            for (int i = 0; i < releaseRightDivisor; i++) {
               int vertexColorValue = releaseRightValue8 + i * (releaseRightValue9 - releaseRightValue8) / releaseRightDivisor;
               int customFlameFactor = releaseRightValue8 + (i + 1) * (releaseRightValue9 - releaseRightValue8) / releaseRightDivisor;
               if (customFlameFactor <= vertexColorValue) {
                  customFlameFactor = vertexColorValue + 1;
               }

               float matrixFactor = ((vertexColorValue + customFlameFactor) * 0.5F - releaseRightValue) / releaseRightLimit;
               VertexGuiUtils.handleVertexColor(
                  quads, vertexColorValue, editValue, customFlameFactor, editValue + n4, CorzRoundedUtils.getPanelOverlay(MatrixAmountUtils.getValue(cardMatrixValue, releaseRight, matrixFactor))
               );
            }

            VertexGuiUtils.updateBufferClose();
         }
      }
   }

   public static void render(DrawContext context, float matrixReleaseValue, float peekPositionFactor, float w, float n4, float n5, int twoDidValue, int n7) {
      MatrixPeekUtilsRender.render(context, matrixReleaseValue, peekPositionFactor, w, n4, n5, TwoDidUtils.getValue(twoDidValue), n7);
   }

   public static void renderReleaseRight(DrawContext context, float releaseRightValue, float f, float maxItemWidth, float segmentSlider, float n5) {
      if (!CardMatrixUtils.isModernFlag()) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, releaseRightValue, f, maxItemWidth, segmentSlider, n5, CardMatrixUtils.getReleaseRight(0.13), 4.0F, 0.85F);
      }

      int twoDidValue = CardMatrixUtils.getValue(CardMatrixUtils.getValue9(), MatrixEspUtils.releaseRightValue, 0.08);
      render(context, releaseRightValue, f, maxItemWidth, segmentSlider, n5, twoDidValue, CardMatrixUtils.getReleaseRight(0.34));
   }

   public static void render(DrawContext context, float matrixReleaseValue, float configsLikeValue, float round, double releaseRightBound) {
      if (!(round <= 0.0F) && !(releaseRightBound <= 0.004)) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixReleaseValue, configsLikeValue, 1.0F, round, 0.5F, CardMatrixUtils.getReleaseRight(releaseRightBound));
      }
   }

   public static void render(DrawContext context, int matrixReleaseValue, int editValue, int releaseRightLimit, int n4, int iconSize, int cardMatrixValue) {
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixReleaseValue, editValue, releaseRightLimit, n4, iconSize, cardMatrixValue);
   }

   public static void renderReleaseRightForDrawContextInt(DrawContext context, int releaseRightValue, int editValue, int rendererFactor, int n4) {
      int cardMatrixValue = CardMatrixUtils.getValue(-13948620, MatrixEspUtils.releaseRightValue, 0.04);
      MatrixPeekUtilsRender.renderMatrixStack(context, releaseRightValue, editValue, rendererFactor, n4, n4 / 2, cardMatrixValue);
   }

   public static void render(DrawContext context, float matrixReleaseValue, float vectorTransformValue, float w, float segmentSlider, float n5, double releaseRightLimit, double lockFactor, double matrixReleaseValue8) {
      render(context, matrixReleaseValue, vectorTransformValue, w, segmentSlider, n5, releaseRightLimit);
      render(context, matrixReleaseValue, vectorTransformValue, w, segmentSlider, n5, getReleaseRight(CardMatrixUtils.getValue4(), releaseRightLimit, lockFactor, matrixReleaseValue8), getValue(releaseRightLimit));
   }

   private static int getValue(int matrixReleaseValue, double matrixReleaseValue2) {
      return Math.max(0, Math.min(255, (int)Math.round(matrixReleaseValue2 * 255.0))) << 24 | matrixReleaseValue & 16777215;
   }

   public static void render(DrawContext context, int matrixReleaseValue, int matrixReleaseValue2, int matrixReleaseValue3, double matrixReleaseBound) {
      double max4 = Math.max(0.0, Math.min(1.0, matrixReleaseBound));
      int iconSize = Math.max(2, matrixReleaseValue3 - 1);
      double releaseRightBound = 0.24 + 0.18 * max4;
      float f3 = (float)(0.34 + 0.16 * max4);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixReleaseValue - iconSize, matrixReleaseValue2 - iconSize, iconSize * 2, iconSize * 2, iconSize, CardMatrixUtils.getReleaseRight(releaseRightBound), 4.0F, f3);
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixReleaseValue - iconSize, matrixReleaseValue2 - iconSize, iconSize * 2, iconSize * 2, iconSize, -658441);
   }

   private static int getReleaseRight(int releaseRightValue, double releaseRightLimit, double lockFactor, double releaseRightLimit3) {
      int cardMatrixValue = releaseRightValue;
      if (releaseRightLimit > 0.004) {
         cardMatrixValue = CardMatrixUtils.getValue(releaseRightValue, MatrixEspUtils.releaseRightValue, 0.05 * releaseRightLimit);
      }

      if (lockFactor > 0.004) {
         cardMatrixValue = CardMatrixUtils.getValue(cardMatrixValue, -1, 0.04 * lockFactor);
      }

      if (releaseRightLimit3 > 0.004) {
         cardMatrixValue = CardMatrixUtils.getValue(cardMatrixValue, -1, 0.07 * releaseRightLimit3);
      }

      return cardMatrixValue;
   }

   public static void renderForDrawContextInt(DrawContext context, int matrixReleaseValue, int f2, int releaseRightBound, double matrixReleaseBound) {
      int matrixReleaseLimit = (int)Math.round(31.0 * Math.max(0.0, Math.min(1.0, matrixReleaseBound)));
      if (matrixReleaseLimit > 0) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixReleaseValue, f2, releaseRightBound, MatrixPeekUtilsRender.getAmount(), matrixReleaseLimit << 24 | 16777215);
      }
   }

   private static void render(DrawContext context, float matrixReleaseValue, float vectorTransformValue, float w, float segmentSlider, float n5, double releaseRightLimit) {
      if (!CardMatrixUtils.isModernFlag()) {
         if (!(releaseRightLimit <= 0.02)) {
            int releaseRight = CardMatrixUtils.getReleaseRight(0.2 * releaseRightLimit);
            MatrixPeekUtilsRender.renderMatrixPeek(context, matrixReleaseValue, vectorTransformValue, w, segmentSlider, n5, releaseRight, 3.0F, 0.85F);
         }
      }
   }

   public static void renderForDrawContextFloat(DrawContext context, float matrixReleaseValue, float vectorTransformValue, float w, float segmentSlider, float n5, double releaseRightLimit, double lockFactor, double matrixReleaseValue8) {
      render(context, matrixReleaseValue, vectorTransformValue, w, segmentSlider, n5, releaseRightLimit);
      render(context, matrixReleaseValue, vectorTransformValue, w, segmentSlider, n5, getReleaseRight(CardMatrixUtils.getValue3(), releaseRightLimit, lockFactor, matrixReleaseValue8), getValue(releaseRightLimit));
   }

   public static void renderForDrawContextFloatDouble(DrawContext context, float matrixReleaseValue, float matrixScissorLimit, float maxItemWidth, float materialFactor, float corzLicenseLimit, double cardMatrixFactor) {
      if (!(cardMatrixFactor <= 0.01)) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixReleaseValue, matrixScissorLimit, maxItemWidth, materialFactor, corzLicenseLimit, getValue(16777215, 0.045 * cardMatrixFactor));
      }
   }

   public static void render(DrawContext context, float matrixReleaseValue, float configsLikeValue, float matrixReleaseValue3, float materialFactor, float matrixReleaseLimit, int matrixStackValue) {
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixReleaseValue, configsLikeValue, matrixReleaseValue3, materialFactor, matrixReleaseLimit, matrixStackValue);
      if (matrixReleaseLimit > 0.0F) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixReleaseValue, configsLikeValue, matrixReleaseLimit, materialFactor, matrixStackValue);
      }
   }

   private RenderMatrixUtilsRelease() {
   }

   public static void render(DrawContext context, int matrixReleaseValue, int editValue, int releaseRightLimit, int n4, int iconSize) {
      MatrixPeekUtilsRender.renderMatrixStack(context, matrixReleaseValue, editValue, releaseRightLimit, n4, iconSize, 234881023);
   }
}
