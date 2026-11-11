package com.corz.client.gui;

import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.PeekMatrixUtils;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import com.corz.client.utils.PeekBetaUtils;

public class MatrixPeekUtilsRender {
   private static MatrixStack matrixStack = new MatrixStack();

   public static MatrixStack getMatrixStack() {
      return matrixStack;
   }

   public static void renderMatrixPeek(DrawContext context, float matrixPeekValue, float f2, float releaseRightBound, float rootDivisor, int matrixStackValue) {
      if (!(releaseRightBound <= 0.0F) && !(rootDivisor <= 0.0F)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         Color color = new Color(matrixStackValue, true);
         CorzRoundedUtils.handle(
            matrixStack.peek().getPositionMatrix(),
            matrixPeekValue - 1.0F,
            f2 - 1.0F,
            matrixPeekValue + releaseRightBound + 1.0F,
            f2 + rootDivisor + 1.0F,
            matrixPeekValue / framebufferHeight,
            f2 / framebufferHeight,
            releaseRightBound / framebufferHeight,
            rootDivisor / framebufferHeight,
            0.0F,
            color.getRed() / 255.0F,
            color.getGreen() / 255.0F,
            color.getBlue() / 255.0F,
            color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount
         );
      }
   }

   public static void renderMatrixPeek(DrawContext context, float matrixPeekValue, float configsLikeValue, float matrixPeekValue3, float materialFactor, float corzLicenseLimit, float matrixPeekValue6, int n7) {
      if (!(matrixPeekValue3 <= 0.0F) && !(materialFactor <= 0.0F)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         Color color = new Color(n7, true);
         CorzRoundedUtils.handleRectRounded(
            matrixStack.peek().getPositionMatrix(),
            matrixPeekValue - 1.0F,
            configsLikeValue - 1.0F,
            matrixPeekValue + matrixPeekValue3 + 1.0F,
            configsLikeValue + materialFactor + 1.0F,
            matrixPeekValue / framebufferHeight,
            configsLikeValue / framebufferHeight,
            matrixPeekValue3 / framebufferHeight,
            materialFactor / framebufferHeight,
            corzLicenseLimit / framebufferHeight,
            matrixPeekValue6 / framebufferHeight,
            color.getRed() / 255.0F,
            color.getGreen() / 255.0F,
            color.getBlue() / 255.0F,
            color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount
         );
      }
   }

   public static int getReleaseRight(double releaseRightFactor) {
      releaseRightFactor = releaseRightFactor < 0.0 ? 0.0 : (releaseRightFactor > 1.0 ? 1.0 : releaseRightFactor);
      int releaseRightValue = 318767103;
      int releaseRightValue2 = -1610612736 | MatrixEspUtils.releaseRightValue & 16777215;
      int releaseRightValue3 = releaseRightValue >>> 24 & 0xFF;
      int releaseRightValue4 = releaseRightValue >> 16 & 0xFF;
      int releaseRightValue5 = releaseRightValue >> 8 & 0xFF;
      int releaseRightValue6 = releaseRightValue & 0xFF;
      int releaseRightValue7 = releaseRightValue2 >>> 24 & 0xFF;
      int releaseRightValue8 = releaseRightValue2 >> 16 & 0xFF;
      int releaseRightValue9 = releaseRightValue2 >> 8 & 0xFF;
      int releaseRightValue10 = releaseRightValue2 & 0xFF;
      return (int)(releaseRightValue3 + (releaseRightValue7 - releaseRightValue3) * releaseRightFactor) << 24
         | (int)(releaseRightValue4 + (releaseRightValue8 - releaseRightValue4) * releaseRightFactor) << 16
         | (int)(releaseRightValue5 + (releaseRightValue9 - releaseRightValue5) * releaseRightFactor) << 8
         | (int)(releaseRightValue6 + (releaseRightValue10 - releaseRightValue6) * releaseRightFactor);
   }

   public static void render(DrawContext context, int matrixPeekValue, int editValue, int releaseRightLimit, int n4, int iconSize, int supplier) {
      renderMatrixStack(context, matrixPeekValue - 1, editValue - 1, releaseRightLimit + 2, n4 + 2, iconSize, supplier, 4.0F, 0.4F);
      renderMatrixStack(context, matrixPeekValue, editValue, releaseRightLimit, n4, iconSize, -16184564);
   }

   public static void renderMatrixPeek(
      DrawContext context, float matrixPeekValue, float matrixPeekValue2, float texturesIconsFactor, float matrixPeekValue4, float matrixPeekValue5, int matrixPeekValue6, float corzRoundedValue, float matrixStackValue, float matrixPeekValue9, float matrixPeekValue10
   ) {
      if (!(texturesIconsFactor <= 0.0F) && !(matrixPeekValue4 <= 0.0F) && !(matrixPeekValue9 <= 0.0F) && !(matrixPeekValue10 <= 0.0F)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         Color color = new Color(matrixPeekValue6, true);
         CorzRoundedUtils.handle(
            matrixStack.peek().getPositionMatrix(),
            corzRoundedValue,
            matrixStackValue,
            corzRoundedValue + matrixPeekValue9,
            matrixStackValue + matrixPeekValue10,
            matrixPeekValue / framebufferHeight,
            matrixPeekValue2 / framebufferHeight,
            texturesIconsFactor / framebufferHeight,
            matrixPeekValue4 / framebufferHeight,
            matrixPeekValue5 / framebufferHeight,
            color.getRed() / 255.0F,
            color.getGreen() / 255.0F,
            color.getBlue() / 255.0F,
            color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount
         );
      }
   }

   public static void render(DrawContext context, float matrixPeekValue, float configsLikeValue, float materialFactor, float matrixPeekValue4, float corzLicenseLimit, int matrixStackValue, int n7) {
      renderMatrixPeek(context, matrixPeekValue, configsLikeValue, materialFactor, matrixPeekValue4, corzLicenseLimit, matrixStackValue);
      renderMatrixPeek(context, matrixPeekValue, configsLikeValue, materialFactor, matrixPeekValue4, corzLicenseLimit, getAmount(), n7);
   }

   public static void renderMatrixPeek(DrawContext context, float matrixPeekValue, float configsLikeValue, float materialFactor, float matrixPeekValue4, float corzLicenseLimit, int matrixStackValue) {
      if (!(materialFactor <= 0.0F) && !(matrixPeekValue4 <= 0.0F)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         Color color = new Color(matrixStackValue, true);
         CorzRoundedUtils.handle(
            matrixStack.peek().getPositionMatrix(),
            matrixPeekValue - 1.0F,
            configsLikeValue - 1.0F,
            matrixPeekValue + materialFactor + 1.0F,
            configsLikeValue + matrixPeekValue4 + 1.0F,
            matrixPeekValue / framebufferHeight,
            configsLikeValue / framebufferHeight,
            materialFactor / framebufferHeight,
            matrixPeekValue4 / framebufferHeight,
            corzLicenseLimit / framebufferHeight,
            color.getRed() / 255.0F,
            color.getGreen() / 255.0F,
            color.getBlue() / 255.0F,
            color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount
         );
      }
   }

   public static void renderMatrixPeek(DrawContext context, float matrixPeekValue, float matrixStackValue, float identifierText, float segmentSlider, float corzLicenseLimit, int releaseRight, float matrixPeekValue7, float matrixPeekValue8) {
      if (!(identifierText <= 0.0F) && !(segmentSlider <= 0.0F)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         float max9 = Math.max(2.0F, matrixPeekValue7 + 2.0F);
         Color color = new Color(releaseRight, true);
         CorzRoundedUtils.handle(
            matrixStack.peek().getPositionMatrix(),
            matrixPeekValue - max9,
            matrixStackValue - max9,
            matrixPeekValue + identifierText + max9,
            matrixStackValue + segmentSlider + max9,
            matrixPeekValue / framebufferHeight,
            matrixStackValue / framebufferHeight,
            identifierText / framebufferHeight,
            segmentSlider / framebufferHeight,
            corzLicenseLimit / framebufferHeight,
            color.getRed() / 255.0F,
            color.getGreen() / 255.0F,
            color.getBlue() / 255.0F,
            color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount,
            matrixPeekValue7 / framebufferHeight,
            matrixPeekValue8,
            false
         );
      }
   }

   public static void renderMatrixStack(DrawContext context, int matrixStackValue, int editValue, int releaseRightLimit, int n4, int iconSize, int cardMatrixValue) {
      PeekBetaUtils.handle(matrixStackValue, editValue, releaseRightLimit, n4, iconSize, new Color(cardMatrixValue, true), matrixStack);
   }

   public static void renderMatrixStack(DrawContext context, int matrixStackValue, int matrixStackValue2, int n3, int n4, int iconSize) {
      PeekMatrixUtils.handle(matrixStackValue, matrixStackValue2 + 2, n3, n4, iconSize, new Color(Integer.MIN_VALUE, true), 8.0F, 0.3F, true, matrixStack);
   }

   public static void renderMatrixStack(DrawContext context, int matrixStackValue, int editValue, int releaseRightLimit, int n4, int iconSize, float matrixStackValue6, int matrixStackValue7) {
      PeekBetaUtils.handle(matrixStackValue, editValue, releaseRightLimit, n4, iconSize, matrixStackValue6, new Color(matrixStackValue7, true), matrixStack);
   }

   public static void handleMatrixStack(MatrixStack matrixStack2) {
      matrixStack = matrixStack2;
   }

   public static void renderForDrawContextFloat(DrawContext context, float matrixPeekValue, float configsLikeValue, float materialFactor, float matrixPeekValue4, float corzLicenseLimit, int matrixPeekValue6, int matrixStackValue) {
      float matrixPeekValue8 = getAmount();
      if (CardMatrixUtils.isModernFlag()) {
         renderMatrixPeek(context, matrixPeekValue, configsLikeValue, materialFactor, matrixPeekValue4, corzLicenseLimit, matrixPeekValue6);
         renderMatrixPeek(context, matrixPeekValue, configsLikeValue, materialFactor, matrixPeekValue4, corzLicenseLimit, matrixPeekValue8, matrixStackValue);
      } else {
         renderMatrixPeek(context, matrixPeekValue, configsLikeValue, materialFactor, matrixPeekValue4, corzLicenseLimit, matrixStackValue);
         renderMatrixPeek(context, matrixPeekValue + matrixPeekValue8, configsLikeValue + matrixPeekValue8, materialFactor - matrixPeekValue8 * 2.0F, matrixPeekValue4 - matrixPeekValue8 * 2.0F, Math.max(0.0F, corzLicenseLimit - matrixPeekValue8), matrixPeekValue6);
      }
   }

   public static void renderMatrixStack(DrawContext context, int matrixStackValue, int matrixStackValue2, int iconSize, int matrixStackValue4) {
      PeekMatrixUtils.handle(matrixStackValue - iconSize, matrixStackValue2 - iconSize, iconSize * 2, iconSize * 2, iconSize, new Color(matrixStackValue4, true), 6.0F, 0.5F, true, matrixStack);
   }

   public static float getAmount() {
      return CorzRoundedUtils.getFramebufferHeight();
   }

   public static void renderMatrixStack(DrawContext context, int matrixStackValue, int matrixStackValue2, int n3, int n4, int iconSize, int supplier, float f2, float f3) {
      PeekMatrixUtils.handle(matrixStackValue, matrixStackValue2, n3, n4, iconSize, new Color(supplier, true), f2, f3, true, matrixStack);
   }

   public static void render(DrawContext context, int matrixPeekValue, int brownValue, int materialFactor, int boxHeight, int corzLicenseLimit, int matrixPeekValue6, int matrixStackValue) {
      renderForDrawContextFloat(context, matrixPeekValue, brownValue, materialFactor, boxHeight, corzLicenseLimit, matrixPeekValue6, matrixStackValue);
   }

   public static void renderMatrixPeekForDrawContextFloat(DrawContext context, float matrixPeekValue, float amountMax, float cubeBound, float fontOffset, float corzLicenseLimit, int valueForIntFloat, float matrixPeekValue7, float matrixPeekValue8) {
      if (!(cubeBound <= 0.0F) && !(fontOffset <= 0.0F)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         float max9 = Math.max(2.0F, matrixPeekValue7 + 2.0F);
         Color color = new Color(valueForIntFloat, true);
         CorzRoundedUtils.handle(
            matrixStack.peek().getPositionMatrix(),
            matrixPeekValue - max9,
            amountMax - max9,
            matrixPeekValue + cubeBound + max9,
            amountMax + fontOffset + max9,
            matrixPeekValue / framebufferHeight,
            amountMax / framebufferHeight,
            cubeBound / framebufferHeight,
            fontOffset / framebufferHeight,
            corzLicenseLimit / framebufferHeight,
            color.getRed() / 255.0F,
            color.getGreen() / 255.0F,
            color.getBlue() / 255.0F,
            color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount,
            matrixPeekValue7 / framebufferHeight,
            matrixPeekValue8,
            true
         );
      }
   }
}
