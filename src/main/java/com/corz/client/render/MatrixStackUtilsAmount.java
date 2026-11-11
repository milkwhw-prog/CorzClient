package com.corz.client.render;

import com.corz.client.CorzClient;
import com.corz.client.config.PlayerSkinResolver;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.TwoDidUtils;
import com.corz.client.gui.VertexGuiUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

public final class MatrixStackUtilsAmount {
   public static final int UNUSED_TWELVE = 12;
   private static final String[] charAtTexts = new String[256];
   private static final float UNUSED_THREE = 3.0F;
   private static final int UNUSED_ONE = 1;

   public static float getAmount(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      String matrixStackText2,
      float matrixStackValue,
      float targetOffset,
      float segmentSlider,
      int matrixStackLimit,
      int scaledY,
      boolean matrixStackFlag,
      Runnable runnable
   ) {
      return getReleaseRight(context, hits, anims, matrixStackText, matrixStackText2, matrixStackValue, false, targetOffset, segmentSlider, 40.0F, matrixStackLimit, scaledY, matrixStackFlag, 0.0F, runnable);
   }

   private static float getIdentifierText(MatrixStackUtilsAmount.Ctl ctl, String text, Identifier identifier, float max) {
      long nanoTime = System.nanoTime();
      boolean identifierFlag = nanoTime - ctl.identifierValue > 300000000L;
      ctl.identifierValue = nanoTime;
      if (ctl.identifierText != null && !identifierFlag) {
         if (!ctl.identifierText.equals(text)) {
            ctl.ctlText = ctl.identifierText;
            ctl.identifier = ctl.identifierIdentifier;
            ctl.identifierText = text;
            ctl.previousValue = ctl.animatedValue;
            ctl.lastNanoTimeIdentifier = nanoTime;
         }

         ctl.identifierIdentifier = identifier;
         if (ctl.lastNanoTimeIdentifier == 0L) {
            ctl.animatedValue = max;
            return max;
         } else {
            float amount = (float)(nanoTime - ctl.lastNanoTimeIdentifier) / 1.7E8F;
            if (amount >= 1.0F) {
               ctl.lastNanoTimeIdentifier = 0L;
               ctl.ctlText = null;
               ctl.identifier = null;
               ctl.animatedValue = max;
               return max;
            } else {
               ctl.identifierAmount = amount;
               ctl.animatedValue = ctl.previousValue + (max - ctl.previousValue) * (max >= ctl.previousValue ? getAmountForFloat(amount) : getAmount3(amount));
               return ctl.animatedValue;
            }
         }
      } else {
         ctl.identifierText = text;
         ctl.identifierIdentifier = identifier;
         ctl.ctlText = null;
         ctl.identifier = null;
         ctl.lastNanoTimeIdentifier = 0L;
         ctl.animatedValue = max;
         if (identifierFlag) {
            ctl.identifierActive = false;
         }

         return max;
      }
   }

   public static VertexTextureFont getFontRenderer() {
      return CorzClient.fontRenderer();
   }

   private static float getReleaseRight(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String releaseRightText,
      String matrixStackText,
      float releaseRightValue,
      boolean releaseRightFlag,
      float targetOffset,
      float segmentSlider,
      float releaseRightBound,
      int matrixStackLimit,
      int scaledY,
      boolean matrixStackFlag,
      float releaseRightValue6,
      Runnable runnable
   ) {
      MatrixStackUtilsAmount.Ctl timeIdentifier = anims.getTimeIdentifier(releaseRightText);
      float identifierText = getIdentifierText(timeIdentifier, matrixStackText, null, Math.max(releaseRightBound, (float)Math.ceil(getAmount3(matrixStackText)) + 18.0F));
      float releaseRightValue7 = releaseRightFlag ? releaseRightValue : releaseRightValue - identifierText;
      float pilotStatusBound = anims.getAmount(releaseRightText, canFlag(matrixStackLimit, scaledY, releaseRightValue7, targetOffset, identifierText, segmentSlider));
      float cardMatrixFactor = Math.max(pilotStatusBound, getMax(releaseRightValue6));
      float amountFlag = timeIdentifier.getAmountFlag(matrixStackFlag ? 1.0F : 0.0F, 14.0F);
      int releaseRightValue9 = CardMatrixUtils.getValue(-15461350, CardMatrixUtils.getValue(-14737624, -14411235, amountFlag), cardMatrixFactor);
      int matrixStackValue = CardMatrixUtils.getValue(
         MatrixPeekUtilsRender.getReleaseRight(cardMatrixFactor), CardMatrixUtils.getValue(MatrixPeekUtilsRender.getReleaseRight(0.0), -1931130803, cardMatrixFactor), amountFlag
      );
      boolean translatePush = canTranslatePush(context, releaseRightValue7 + identifierText / 2.0F, targetOffset + segmentSlider / 2.0F, anims.getTime(releaseRightText, 0.05F));
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, releaseRightValue7, targetOffset, identifierText, segmentSlider, 5.0F, releaseRightValue9, matrixStackValue);
      handleTextIdentifier(
         timeIdentifier,
         1,
         matrixStackText,
         null,
         releaseRightValue7,
         targetOffset,
         identifierText,
         segmentSlider,
         CardMatrixUtils.getValue(-920587, CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1750963, amountFlag), cardMatrixFactor * 0.8)
      );
      renderPopMatrices(context, translatePush);
      hits.handleFlag(releaseRightValue7, targetOffset - 1.0F, identifierText, segmentSlider + 2.0F, runnable, anims, releaseRightText);
      return releaseRightFlag ? releaseRightValue7 + identifierText : releaseRightValue7;
   }

   public static float getAmount(String matrixStackText, float matrixStackFactor, float swapValue) {
      if (matrixStackText == null || matrixStackText.isEmpty()) {
         return 0.0F;
      } else if (swapValue == 0.0F) {
         return getAmount(matrixStackText, matrixStackFactor);
      } else {
         VertexTextureFont fontRenderer = getFontRenderer();
         float matrixStackTotal = 0.0F;

         for (int i = 0; i < matrixStackText.length(); i++) {
            matrixStackTotal += fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(getCharAt(matrixStackText.charAt(i)));
         }

         return matrixStackTotal * matrixStackFactor + swapValue * (matrixStackText.length() - 1);
      }
   }

   public static float getAmount(float matrixStackValue) {
      float matrixStackDivisor = MatrixPeekUtilsRender.getAmount();
      return Math.round(matrixStackValue / matrixStackDivisor) * matrixStackDivisor;
   }

   public static void renderRoot(DrawContext context, String rootText, float rootValue, float sideBuildingValue, float sideWalkAmount) {
      String upperCase = rootText.toUpperCase(Locale.ROOT);
      float matrixStackFactor = 0.8F;
      float rootDivisor = MatrixPeekUtilsRender.getAmount();
      float rootValue5 = sideBuildingValue + 6.0F;
      handlePushScale(upperCase, rootValue + 1.0F, rootValue5 - 11.5F * matrixStackFactor, matrixStackFactor, -11117467);
      float matrixPeekValue = (float)Math.ceil((rootValue + 1.0F + getAmount(upperCase, matrixStackFactor) + 7.0F) / rootDivisor) * rootDivisor;
      float f2 = getAmount(rootValue5 - 6.44F * matrixStackFactor / 2.0F - rootDivisor / 2.0F);
      if (matrixPeekValue < rootValue + sideWalkAmount) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, f2, rootValue + sideWalkAmount - matrixPeekValue, rootDivisor, CardMatrixUtils.getValueInt());
      }
   }

   public static float getAmount(float matrixStackValue, float matrixStackFactor) {
      float matrixStackFactor2 = getMax(matrixStackValue) - 1.0F;
      return 1.0F + (matrixStackFactor + 1.0F) * matrixStackFactor2 * matrixStackFactor2 * matrixStackFactor2 + matrixStackFactor * matrixStackFactor2 * matrixStackFactor2;
   }

   public static float getAmount(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      String[] texts,
      int matrixStackValue,
      float releaseRightValue,
      float ctxOffset,
      float matrixStackValue4,
      int matrixStackLimit,
      int mouseY,
      IntConsumer intConsumer
   ) {
      float matrixStackDivisor = MatrixPeekUtilsRender.getAmount();
      releaseRightValue = (float)Math.ceil(releaseRightValue / matrixStackDivisor) * matrixStackDivisor;
      return getReleaseRight(context, hits, anims, matrixStackText, texts, matrixStackValue, releaseRightValue, ctxOffset + (matrixStackValue4 - 18.0F) / 2.0F, 18.0F, 16.0F, 8.0F, 6.0F, matrixStackLimit, mouseY, intConsumer);
   }

   public static float getAmount(DrawContext context, boolean matrixStackFlag, String matrixStackText, float matrixStackValue, float priceValue) {
      return getAmount(context, matrixStackFlag, matrixStackText, matrixStackValue, priceValue, true);
   }

   public static void render(DrawContext context, float matrixStackValue, float matrixStackValue2, float loadedValue, float matrixStackValue4, float releaseRightBound, int j) {
      double cardMatrixFactor = (Math.sin(System.nanoTime() / 1.0E9 * 4.0 + j * 0.6) + 1.0) / 2.0;
      float releaseRight = getReleaseRight(context, matrixStackValue, matrixStackValue2, loadedValue, matrixStackValue4, releaseRightBound, (float)(0.25 * cardMatrixFactor), 0.0F);
      int matrixStackValue8 = CardMatrixUtils.getValue(-15065820, -14012872, cardMatrixFactor);
      int matrixStackValue9 = getValue(matrixStackValue8, 0.7F);
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue + 8.0F, matrixStackValue2 + 6.0F, releaseRightBound - 16.0F, 6.0F, 2.0F, matrixStackValue8);
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue + 8.0F, releaseRight + 9.0F, loadedValue * 0.34F, 5.0F, 2.0F, matrixStackValue9);
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue + 8.0F, matrixStackValue2 + matrixStackValue4 - 16.0F, loadedValue * 0.26F, 5.0F, 2.0F, matrixStackValue9);
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue + loadedValue - 8.0F - 50.0F, matrixStackValue2 + matrixStackValue4 - 22.0F, 50.0F, 16.0F, 5.0F, matrixStackValue9);
   }

   static {
      for (int i = 0; i < charAtTexts.length; i++) {
         charAtTexts[i] = String.valueOf((char)i);
      }
   }

   public static int getReleaseRight() {
      return MatrixAmountUtils.getAmountForInt(MatrixEspUtils.releaseRightValue) > 0.55F ? -15724006 : -1;
   }

   public static int getValue(int matrixStackValue, int w, int matrixStackDivisor, int matrixStackValue4, int matrixStackFactor) {
      return getValueForInt(matrixStackValue, w, matrixStackDivisor, matrixStackValue4, matrixStackFactor + 1) - matrixStackValue4 - getValueForInt(matrixStackValue, w, matrixStackDivisor, matrixStackValue4, matrixStackFactor);
   }

   public static float getAmount(DrawContext context, String matrixStackText, float matrixStackValue, float cardOffset, float matrixStackValue3) {
      float matrixStackDivisor = 0.78F;
      float materialFactor = 11.0F;
      String formatText = getFontRenderer().formatText(matrixStackText, (matrixStackValue3 - 10.0F) / matrixStackDivisor);
      float matrixStackValue5 = getAmount(formatText);
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue, cardOffset, matrixStackValue5, materialFactor, 4.0F, getValue(-1, 0.07F));
      handlePushScale(formatText, matrixStackValue + 5.0F, getAmount(cardOffset, materialFactor, matrixStackDivisor), matrixStackDivisor, -7630437);
      return matrixStackValue5;
   }

   private static void handle(float matrixStackValue, float matrixStackValue2, float loadedValue, float matrixStackValue4, float matrixStackValue5, float matrixStackValue6, float matrixStackValue7, int releaseRight) {
      float matrixStackValue9 = matrixStackValue2 + 16.0F;
      float segmentSlider = matrixStackValue4 - 16.0F;
      float matrixStackValue11 = matrixStackValue2 + matrixStackValue4;
      float peekPositionLimit = matrixStackValue + matrixStackValue5 + matrixStackValue7;
      float matrixStackValue13 = matrixStackValue6 * 2.0F + 2.0F;
      handlePeekPosition(matrixStackValue, matrixStackValue2, matrixStackValue5, 16.0F + matrixStackValue13, matrixStackValue6, 0.0F, releaseRight, matrixStackValue - 1.0F, matrixStackValue2 - 1.0F, matrixStackValue + matrixStackValue5 + 1.0F, matrixStackValue9);
      handlePeekPosition(matrixStackValue + matrixStackValue5, matrixStackValue9 - matrixStackValue7 * 2.0F, matrixStackValue7 * 2.0F, matrixStackValue7 * 2.0F, matrixStackValue7, releaseRight, 1000000.0F, 1.0F, false, matrixStackValue + matrixStackValue5, matrixStackValue9 - matrixStackValue7, peekPositionLimit, matrixStackValue9);
      handlePeekPosition(matrixStackValue, matrixStackValue9 - matrixStackValue13, loadedValue, segmentSlider + matrixStackValue13, matrixStackValue6, 0.0F, releaseRight, matrixStackValue - 1.0F, matrixStackValue9, peekPositionLimit, matrixStackValue11 + 1.0F);
      handlePeekPosition(matrixStackValue, matrixStackValue9, loadedValue, segmentSlider, matrixStackValue6, 0.0F, releaseRight, peekPositionLimit, matrixStackValue9 - 1.0F, matrixStackValue + loadedValue + 1.0F, matrixStackValue11 + 1.0F);
   }

   public static float getAmount(String matrixStackText) {
      return (float)Math.ceil(getAmount(matrixStackText, 0.78F)) + 10.0F;
   }

   public static float getAmount(DrawContext context, int matrixStackValue, int matrixStackValue2, String matrixStackText, float matrixStackValue3, float priceValue) {
      return getAmount(context, matrixStackValue, matrixStackValue2, matrixStackText, matrixStackValue3, priceValue, true);
   }

   public static void render(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      Identifier id2,
      float matrixStackValue,
      float targetOffset,
      float identifierText,
      float peekPositionFactor,
      int matrixStackLimit,
      int scaledY,
      boolean matrixStackFlag,
      Runnable runnable
   ) {
      renderReleaseRight(context, hits, anims, matrixStackText, id2, matrixStackValue, targetOffset, identifierText, peekPositionFactor, matrixStackLimit, scaledY, matrixStackFlag, 0.0F, runnable);
   }

   public static float getAmount(float matrixStackValue, float materialFactor, float matrixStackFactor) {
      float matrixStackFactor2 = getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown(0, 100) - 50.0F;
      return matrixStackValue + materialFactor / 2.0F + matrixStackFactor2 * matrixStackFactor;
   }

   public static float getReleaseRight(DrawContext context, float releaseRightValue, float matrixStackValue, float loadedValue, float releaseRightValue4, float releaseRightBound, float releaseRightLimit, float cardMatrixFactor) {
      float releaseRightValue5 = 7.0F;
      float releaseRightValue6 = 4.0F;
      float logoRoundValue = MatrixPeekUtilsRender.getAmount();
      float max8 = Math.max(releaseRightValue5 * 2.0F + 2.0F, Math.min(releaseRightBound, loadedValue - releaseRightValue5 - releaseRightValue6));
      int cardMatrixValue = CardMatrixUtils.getValue4();
      if (cardMatrixFactor > 0.004F) {
         cardMatrixValue = CardMatrixUtils.getValue(cardMatrixValue, MatrixEspUtils.releaseRightValue, 0.05 * cardMatrixFactor);
      }

      if (releaseRightLimit > 0.004F) {
         cardMatrixValue = CardMatrixUtils.getValue(cardMatrixValue, -1, 0.04 * releaseRightLimit);
      }

      int releaseRight = cardMatrixFactor > 0.004F ? CardMatrixUtils.getValue(234881023, CardMatrixUtils.getReleaseRight(0.42), cardMatrixFactor) : 234881023;
      int releaseRightValue11 = TwoDidUtils.getValue(cardMatrixValue);
      if (!CardMatrixUtils.isModernFlag() && releaseRightValue11 >>> 24 == 255) {
         if (cardMatrixFactor > 0.02F) {
            handle(releaseRightValue, matrixStackValue, loadedValue, releaseRightValue4, max8, releaseRightValue5, releaseRightValue6, CardMatrixUtils.getReleaseRight(0.2 * cardMatrixFactor), true);
         }

         handle(releaseRightValue, matrixStackValue, loadedValue, releaseRightValue4, max8, releaseRightValue5, releaseRightValue6, releaseRight);
         handle(releaseRightValue + logoRoundValue, matrixStackValue + logoRoundValue, loadedValue - logoRoundValue * 2.0F, releaseRightValue4 - logoRoundValue * 2.0F, max8 - logoRoundValue * 2.0F, releaseRightValue5 - logoRoundValue, releaseRightValue6 + logoRoundValue, releaseRightValue11);
      } else {
         if (!CardMatrixUtils.isModernFlag() && cardMatrixFactor > 0.02F) {
            handle(releaseRightValue, matrixStackValue, loadedValue, releaseRightValue4, max8, releaseRightValue5, releaseRightValue6, CardMatrixUtils.getReleaseRight(0.2 * cardMatrixFactor), false);
         }

         handle(releaseRightValue, matrixStackValue, loadedValue, releaseRightValue4, max8, releaseRightValue5, releaseRightValue6, releaseRightValue11);
         handle(releaseRightValue, matrixStackValue, loadedValue, releaseRightValue4, max8, releaseRightValue5, releaseRightValue6, logoRoundValue, releaseRight);
      }

      return matrixStackValue + 16.0F;
   }

   public static boolean canTranslatePush(DrawContext context, float x, float y, float x2) {
      if (Math.abs(x2 - 1.0F) <= 5.0E-4F) {
         return false;
      } else {
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(x, y, 0.0F);
         matrixStack.scale(x2, x2, 1.0F);
         matrixStack.translate(-x, -y, 0.0F);
         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();
         matrices.translate(x, y);
         matrices.scale(x2, x2);
         matrices.translate(-x, -y);
         return true;
      }
   }

   public static float getAmount(String matrixStackText, float matrixStackFactor) {
      return getFontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont(matrixStackText) * matrixStackFactor;
   }

   public static void handlePushScale(String scaleText, float scaleValue, float priceValue, float x2, int matrixStackValue) {
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      matrixStack.push();
      matrixStack.scale(x2, x2, 1.0F);
      getFontRenderer().handleForStringFloat(scaleText, scaleValue / x2, priceValue / x2, new Color(matrixStackValue, true));
      matrixStack.pop();
   }

   public static void handleVertexColor(float vertexColorValue, float keybindListeningValue, float vertexColorValue3, float vertexColorValue4, int matrixStackValue, float matrixStackLimit, boolean vertexColorFlag) {
      if (!(vertexColorValue3 <= 0.0F) && !(vertexColorValue4 <= 0.0F) && !(matrixStackLimit <= 0.01F)) {
         int panelOverlay = CorzRoundedUtils.getPanelOverlay(getValue(matrixStackValue, getMax(matrixStackLimit)));
         if (panelOverlay >>> 24 != 0) {
            int vertexColorValue7 = panelOverlay & 16777215;
            int vertexColorValue8 = vertexColorFlag ? panelOverlay : vertexColorValue7;
            int vertexColorValue9 = vertexColorFlag ? vertexColorValue7 : panelOverlay;
            Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
            VertexConsumer quads = VertexGuiUtils.getQuads();
            quads.vertex(positionMatrix, vertexColorValue, keybindListeningValue, 0.0F).color(vertexColorValue8);
            quads.vertex(positionMatrix, vertexColorValue, keybindListeningValue + vertexColorValue4, 0.0F).color(vertexColorValue9);
            quads.vertex(positionMatrix, vertexColorValue + vertexColorValue3, keybindListeningValue + vertexColorValue4, 0.0F).color(vertexColorValue9);
            quads.vertex(positionMatrix, vertexColorValue + vertexColorValue3, keybindListeningValue, 0.0F).color(vertexColorValue8);
            VertexGuiUtils.updateBufferClose();
         }
      }
   }

   public static void renderPopMatrices(DrawContext context, boolean matricesFlag) {
      if (matricesFlag) {
         context.getMatrices().popMatrix();
         MatrixPeekUtilsRender.getMatrixStack().pop();
      }
   }

   public static float getAmountForString(String matrixStackText) {
      return 8.0F + getAmount(matrixStackText, 0.82F);
   }

   private static void handle(float matrixStackValue, float matrixStackValue2, float loadedValue, float matrixStackValue4, float matrixStackValue5, float matrixStackValue6, float matrixStackValue7, float logoRoundValue, int releaseRight) {
      float matrixStackValue10 = matrixStackValue2 + 16.0F;
      float segmentSlider = matrixStackValue4 - 16.0F;
      float matrixStackValue12 = matrixStackValue2 + matrixStackValue4;
      float peekPositionLimit = matrixStackValue + matrixStackValue5 + matrixStackValue7;
      float peekPositionValue = matrixStackValue + matrixStackValue5 - logoRoundValue;
      float matrixStackValue15 = matrixStackValue6 * 2.0F + 2.0F;
      float matrixStackValue16 = matrixStackValue7 + logoRoundValue;
      handlePeekPosition(matrixStackValue, matrixStackValue2, matrixStackValue5, 16.0F + matrixStackValue15, matrixStackValue6, logoRoundValue, releaseRight, matrixStackValue - 1.0F, matrixStackValue2 - 1.0F, matrixStackValue + matrixStackValue5 + 1.0F, matrixStackValue10 - matrixStackValue7);
      handlePeekPosition(peekPositionValue, matrixStackValue10 - matrixStackValue7 - matrixStackValue16, matrixStackValue16 * 2.0F, matrixStackValue16 * 2.0F, matrixStackValue16, logoRoundValue, releaseRight, peekPositionValue, matrixStackValue10 - matrixStackValue7, peekPositionLimit, matrixStackValue10 + logoRoundValue);
      handlePeekPosition(matrixStackValue, matrixStackValue10 - matrixStackValue15, loadedValue, segmentSlider + matrixStackValue15, matrixStackValue6, logoRoundValue, releaseRight, matrixStackValue - 1.0F, matrixStackValue10 - matrixStackValue7, peekPositionValue, matrixStackValue12 + 1.0F);
      handlePeekPosition(matrixStackValue, matrixStackValue10, loadedValue, segmentSlider, matrixStackValue6, logoRoundValue, releaseRight, peekPositionLimit, matrixStackValue10 - 1.0F, matrixStackValue + loadedValue + 1.0F, matrixStackValue10 + matrixStackValue6);
      handlePeekPosition(matrixStackValue, matrixStackValue10, loadedValue, segmentSlider, matrixStackValue6, logoRoundValue, releaseRight, peekPositionValue, matrixStackValue10 + matrixStackValue6, matrixStackValue + loadedValue + 1.0F, matrixStackValue12 + 1.0F);
   }

   public static float getPopPush(String matrixStackText, float matrixStackValue, float priceValue, float x2, float swapValue, int matrixStackValue4) {
      if (matrixStackText == null || matrixStackText.isEmpty()) {
         return 0.0F;
      } else if (swapValue == 0.0F) {
         handlePushScale(matrixStackText, matrixStackValue, priceValue, x2, matrixStackValue4);
         return getAmount(matrixStackText, x2);
      } else {
         VertexTextureFont fontRenderer = getFontRenderer();
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.scale(x2, x2, 1.0F);

         float popPush;
         try {
            Color color = new Color(matrixStackValue4, true);
            float matrixStackTotal = matrixStackValue / x2;
            float f13 = priceValue / x2;
            float matrixStackValue6 = swapValue / x2;

            for (int i = 0; i < matrixStackText.length(); i++) {
               String charAt = getCharAt(matrixStackText.charAt(i));
               fontRenderer.handleForStringFloat(charAt, matrixStackTotal, f13, color);
               matrixStackTotal += fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(charAt) + (i < matrixStackText.length() - 1 ? matrixStackValue6 : 0.0F);
            }

            popPush = matrixStackTotal * x2 - matrixStackValue;
         } finally {
            matrixStack.pop();
         }

         return popPush;
      }
   }

   public static void render(DrawContext context, float matrixStackValue, float scaledHeight, float materialFactor, float matrixStackValue4) {
      render(context, matrixStackValue, scaledHeight, materialFactor, matrixStackValue4, getValue(), 352321535);
   }

   public static int getValue(int matrixStackValue, float matrixStackFactor) {
      int matrixStackFactor2 = matrixStackValue >>> 24 & 0xFF;
      if (matrixStackFactor2 == 0) {
         matrixStackFactor2 = 255;
      }

      int max2 = Math.max(0, Math.min(255, Math.round(matrixStackFactor2 * matrixStackFactor)));
      return max2 << 24 | matrixStackValue & 16777215;
   }

   public static void renderMatricesPop(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matricesPopText,
      float matricesPopValue,
      float downloadCommunityValue,
      float w,
      float segmentSlider,
      Identifier id2,
      String matrixStackText,
      String scaleText,
      String matricesPopText4,
      int matrixStackLimit,
      int mouseY,
      Runnable runnable
   ) {
      MatrixStackUtilsAmount.Ctl timeIdentifier = anims.getTimeIdentifier(matricesPopText);
      long nanoTime = System.nanoTime();
      if (nanoTime - timeIdentifier.lastNanoTimeMatricesPop > 300000000L) {
         timeIdentifier.lastNanoTime = nanoTime;
      }

      timeIdentifier.lastNanoTimeMatricesPop = nanoTime;
      float panelOverlayLimit = 1.0F;
      if (timeIdentifier.lastNanoTime != 0L) {
         float matricesPopLimit = (float)(nanoTime - timeIdentifier.lastNanoTime) / 2.2E8F;
         if (matricesPopLimit >= 1.0F) {
            timeIdentifier.lastNanoTime = 0L;
         } else {
            panelOverlayLimit = getAmountForFloat(matricesPopLimit);
         }
      }

      float y2 = (1.0F - panelOverlayLimit) * 4.0F;
      boolean matricesPopFlag = y2 > 0.0F;
      if (matricesPopFlag) {
         MatrixPeekUtilsRender.getMatrixStack().push();
         MatrixPeekUtilsRender.getMatrixStack().translate(0.0F, y2, 0.0F);
         context.getMatrices().pushMatrix();
         context.getMatrices().translate(0.0F, y2);
      }

      float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

      try {
         float matricesPopValue8 = getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown();
         float matricesPopValue9 = 26.0F + matricesPopValue8 + 3.0F + matricesPopValue8 * 0.82F + (matricesPopText4 == null ? 0.0F : 28.0F);
         float roundOffset = downloadCommunityValue + Math.max(0.0F, (segmentSlider - matricesPopValue9) / 2.0F);
         float x = matricesPopValue + w / 2.0F;
         if (id2 != null) {
            boolean translatePush = canTranslatePush(context, x, roundOffset + 9.0F, 0.86F + 0.14F * getAmount(panelOverlayLimit, 1.9F));
            MatrixPeekUtilsIdentifier.handle(x - 9.0F, roundOffset, 18.0F, 18.0F, id2, new Color(-10722709, true), MatrixPeekUtilsRender.getMatrixStack());
            renderPopMatrices(context, translatePush);
         }

         float abcdefghijklmnopqrstuvwxyzBrown = roundOffset + 18.0F + 8.0F;
         handle(matrixStackText, x - getAmount3(matrixStackText) / 2.0F, abcdefghijklmnopqrstuvwxyzBrown, -920587);
         float priceValue = abcdefghijklmnopqrstuvwxyzBrown + (matricesPopValue8 + 3.0F);
         if (scaleText != null && !scaleText.isEmpty()) {
            handlePushScale(scaleText, x - getAmount(scaleText, 0.82F) / 2.0F, priceValue, 0.82F, -7630437);
         }

         if (matricesPopText4 != null) {
            float max14 = Math.max(40.0F, (float)Math.ceil(getAmount3(matricesPopText4)) + 18.0F);
            getAmount(context, hits, anims, matricesPopText, matricesPopText4, x + max14 / 2.0F, priceValue + matricesPopValue8 * 0.82F + 10.0F, 18.0F, matrixStackLimit, mouseY, false, runnable);
         }
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         if (matricesPopFlag) {
            context.getMatrices().popMatrix();
            MatrixPeekUtilsRender.getMatrixStack().pop();
         }
      }
   }

   public static float getAmount(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      String matrixStackText2,
      Identifier id2,
      float matrixStackValue,
      float friendsTabsValueOffset,
      float segmentSlider,
      int matrixStackLimit,
      int mouseY,
      boolean matrixStackFlag,
      Runnable runnable
   ) {
      return getAmount(context, hits, anims, matrixStackText, matrixStackText2, id2, matrixStackValue, friendsTabsValueOffset, segmentSlider, 0.0F, matrixStackLimit, mouseY, matrixStackFlag, runnable);
   }

   private static float getReleaseRight(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String releaseRightText,
      String matrixStackText,
      Identifier id2,
      float releaseRightValue,
      float matrixStackValue,
      float segmentSlider,
      float releaseRightBound,
      int matrixStackLimit,
      int scaledY,
      float cardMatrixFactor,
      float releaseRightLimit,
      Runnable runnable
   ) {
      float releaseRightValue7 = 7.0F;
      float peekPositionFactor = 8.0F;
      MatrixStackUtilsAmount.Ctl timeIdentifier = anims.getTimeIdentifier(releaseRightText);
      float releaseRightValue9 = id2 == null ? 0.0F : 5.0F + peekPositionFactor;
      float identifierText = getIdentifierText(timeIdentifier, matrixStackText, null, Math.max(releaseRightBound, (float)Math.ceil(getAmount3(matrixStackText)) + releaseRightValue7 * 2.0F + releaseRightValue9));
      float releaseRight = releaseRightValue - identifierText;
      float releaseRightValue10 = anims.getAmount(releaseRightText, canFlag(matrixStackLimit, scaledY, releaseRight, matrixStackValue, identifierText, segmentSlider));
      boolean releaseRightFlag = releaseRightLimit > 0.01F;
      float releaseRightValue11 = releaseRightFlag ? getAmount(1.5) : 0.0F;
      int releaseRightValue12 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-15461350, -14737624, releaseRightValue10), MatrixEspUtils.releaseRightValue, 0.1 * releaseRightLimit);
      int releaseRightValue13 = CardMatrixUtils.getValue(
         CardMatrixUtils.getValue(MatrixPeekUtilsRender.getReleaseRight(releaseRightValue10), CardMatrixUtils.getReleaseRight(0.78), cardMatrixFactor),
         CardMatrixUtils.getReleaseRight(0.6 + 0.32 * releaseRightValue11),
         releaseRightLimit
      );
      boolean translatePush = canTranslatePush(context, releaseRight + identifierText / 2.0F, matrixStackValue + segmentSlider / 2.0F, anims.getTime(releaseRightText, 0.05F));
      if (releaseRightFlag) {
         MatrixPeekUtilsRender.renderMatrixPeek(
            context, releaseRight, matrixStackValue, identifierText, segmentSlider, 5.0F, CardMatrixUtils.getReleaseRight((0.16 + 0.24 * releaseRightValue11) * releaseRightLimit), 3.5F + 1.5F * releaseRightValue11, 0.85F
         );
      }

      MatrixPeekUtilsRender.renderForDrawContextFloat(context, releaseRight, matrixStackValue, identifierText, segmentSlider, 5.0F, releaseRightValue12, releaseRightValue13);
      if (releaseRightFlag) {
         handleReleaseRight(releaseRight, matrixStackValue, identifierText, segmentSlider, 5.0F, releaseRightLimit);
      }

      int releaseRightValue14 = CardMatrixUtils.getValue(
         CardMatrixUtils.getValue(-920587, MatrixEspUtils.releaseRightValue, releaseRightValue10 * 0.8), MatrixEspUtils.releaseRightValue, cardMatrixFactor
      );
      handleTextIdentifier(timeIdentifier, id2 == null ? 1 : 2, matrixStackText, null, releaseRight, matrixStackValue, identifierText, segmentSlider, releaseRightValue14);
      if (id2 != null) {
         MatrixPeekUtilsIdentifier.handle(
            releaseRight + identifierText - releaseRightValue7 - peekPositionFactor + 1.0F,
            matrixStackValue + (segmentSlider - peekPositionFactor) / 2.0F,
            peekPositionFactor,
            peekPositionFactor,
            id2,
            new Color(CardMatrixUtils.getValue(-7630437, -920587, releaseRightValue10), true),
            MatrixPeekUtilsRender.getMatrixStack()
         );
      }

      renderPopMatrices(context, translatePush);
      hits.handleFlag(releaseRight, matrixStackValue - 1.0F, identifierText, segmentSlider + 2.0F, runnable, anims, releaseRightText);
      return releaseRight;
   }

   public static float getAmountForFloat(float matrixStackValue) {
      float matrixStackFactor = 1.0F - getMax(matrixStackValue);
      return 1.0F - matrixStackFactor * matrixStackFactor * matrixStackFactor;
   }

   public static float getAmount(float matrixStackFactor, boolean matrixStackFlag) {
      matrixStackFactor = getMax(matrixStackFactor);
      return matrixStackFlag ? 1.0F - 0.036000002F * matrixStackFactor * matrixStackFactor : 0.94F + 0.060000002F * getAmount(matrixStackFactor, 1.6F);
   }

   public static void renderReleaseRight(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String releaseRightText,
      Identifier id2,
      float releaseRightValue,
      float targetOffset,
      float identifierText,
      float peekPositionFactor,
      int matrixStackLimit,
      int mouseY,
      boolean releaseRightFlag,
      float releaseRightBound,
      Runnable runnable
   ) {
      float pilotStatusBound = anims.getAmount(releaseRightText, canFlag(matrixStackLimit, mouseY, releaseRightValue, targetOffset, identifierText, identifierText));
      int releaseRightValue7 = releaseRightFlag ? -1750963 : MatrixEspUtils.releaseRightValue;
      float cardMatrixFactor = releaseRightFlag ? 0.35F + 0.65F * Math.max(pilotStatusBound, releaseRightBound) : releaseRightBound;
      int releaseRightValue9 = CardMatrixUtils.getValue(
         CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), -1, 0.03 + 0.06 * pilotStatusBound), releaseRightValue7, 0.14 * cardMatrixFactor + (releaseRightFlag ? 0.55 : 0.14) * releaseRightBound
      );
      int matrixStackValue = CardMatrixUtils.getValue(
         CardMatrixUtils.getValue(CardMatrixUtils.getValueInt(), 1040187391, pilotStatusBound), getValue(releaseRightValue7, releaseRightFlag ? 0.55F + 0.45F * releaseRightBound : 0.55F), cardMatrixFactor
      );
      boolean translatePush = canTranslatePush(context, releaseRightValue + identifierText / 2.0F, targetOffset + identifierText / 2.0F, anims.getTime(releaseRightText, 0.07F));
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, releaseRightValue, targetOffset, identifierText, identifierText, 5.0F, releaseRightValue9, matrixStackValue);
      int cardMatrixValue = releaseRightFlag ? CardMatrixUtils.getValue(-7630437, -1750963, 0.7 + 0.3 * pilotStatusBound) : CardMatrixUtils.getValue(-7630437, -920587, pilotStatusBound);
      int releaseRightValue12 = CardMatrixUtils.getValue(cardMatrixValue, releaseRightFlag ? -1 : CardMatrixUtils.getValue(releaseRightValue7, -1, 0.22), releaseRightBound);
      float releaseRightValue13 = (identifierText - peekPositionFactor) / 2.0F;
      MatrixPeekUtilsIdentifier.handle(releaseRightValue + releaseRightValue13, targetOffset + releaseRightValue13, peekPositionFactor, peekPositionFactor, id2, new Color(releaseRightValue12, true), MatrixPeekUtilsRender.getMatrixStack());
      renderPopMatrices(context, translatePush);
      hits.handleFlag(releaseRightValue - 1.0F, targetOffset - 1.0F, identifierText + 2.0F, identifierText + 2.0F, runnable, anims, releaseRightText);
   }

   public static void render(DrawContext context, float matrixStackValue, float matrixStackValue2, float matrixStackFactor) {
      if (!(matrixStackFactor <= 0.002F)) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, -matrixStackValue, -matrixStackValue2, matrixStackValue * 3.0F, matrixStackValue2 * 3.0F, getValue(-16777216, matrixStackFactor));
      }
   }

   private static void handle(int matrixStackValue, String matrixStackText, Identifier id, float matrixStackValue2, float glfwLimit, float identifierText, float configsLikeValue, int matrixStackValue6, float matrixStackValue7) {
      float abcdefghijklmnopqrstuvwxyzBrown = getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown(Math.round(glfwLimit), Math.round(configsLikeValue)) + matrixStackValue7;
      if (matrixStackValue == 0) {
         float matrixStackValue9 = id == null ? 0.0F : 13.0F;
         float matrixStackTotal = matrixStackValue2 + 8.0F + (identifierText - ((float)Math.ceil(getAmount3(matrixStackText)) + 16.0F + matrixStackValue9)) / 2.0F;
         if (id != null) {
            MatrixPeekUtilsIdentifier.handle(
               matrixStackTotal - 1.0F, glfwLimit + (configsLikeValue - 10.0F) / 2.0F + matrixStackValue7, 10.0F, 10.0F, id, new Color(matrixStackValue6, true), MatrixPeekUtilsRender.getMatrixStack()
            );
            matrixStackTotal += matrixStackValue9;
         }

         handle(matrixStackText, matrixStackTotal, abcdefghijklmnopqrstuvwxyzBrown, matrixStackValue6);
      } else if (matrixStackValue == 2) {
         handle(matrixStackText, matrixStackValue2 + 7.0F, abcdefghijklmnopqrstuvwxyzBrown, matrixStackValue6);
      } else {
         handle(matrixStackText, getAmount(matrixStackValue2 + (identifierText - getAmount3(matrixStackText)) / 2.0F), abcdefghijklmnopqrstuvwxyzBrown, matrixStackValue6);
      }
   }

   public static float getAmount(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      String matrixStackText2,
      Identifier id2,
      float matrixStackValue,
      float brownValue,
      float segmentSlider,
      float releaseRightBound,
      int matrixStackLimit,
      int scaledY,
      boolean matrixStackFlag,
      Runnable runnable
   ) {
      return getReleaseRight(context, hits, anims, matrixStackText, matrixStackText2, id2, matrixStackValue, brownValue, segmentSlider, releaseRightBound, matrixStackLimit, scaledY, matrixStackFlag, 0.0F, runnable);
   }

   public static float getAmount(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      String matrixStackText2,
      float matrixStackValue,
      float targetOffset,
      float segmentSlider,
      int matrixStackLimit,
      int scaledY,
      boolean matrixStackFlag,
      float matrixStackValue6,
      Runnable runnable
   ) {
      return getReleaseRight(context, hits, anims, matrixStackText, matrixStackText2, matrixStackValue, true, targetOffset, segmentSlider, 40.0F, matrixStackLimit, scaledY, matrixStackFlag, matrixStackValue6, runnable);
   }

   public static float getAmount(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      String matrixStackText2,
      Identifier id2,
      float matrixStackValue,
      float targetOffset,
      float segmentSlider,
      float releaseRightBound,
      int matrixStackLimit,
      int scaledY,
      float cardMatrixFactor,
      Runnable runnable
   ) {
      return getReleaseRight(context, hits, anims, matrixStackText, matrixStackText2, id2, matrixStackValue, targetOffset, segmentSlider, releaseRightBound, matrixStackLimit, scaledY, cardMatrixFactor, 0.0F, runnable);
   }

   public static void handlePeekPosition(float peekPositionValue, float matrixStackValue, float loadedValue, float segmentSlider, float peekPositionValue5, float logoRoundValue, int releaseRight, float peekPositionLimit, float peekPositionLimit2, float peekPositionValue8, float peekPositionValue9) {
      if (!(loadedValue <= 0.0F) && !(segmentSlider <= 0.0F) && !(peekPositionValue8 <= peekPositionLimit) && !(peekPositionValue9 <= peekPositionLimit2)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         CorzRoundedUtils.handleRectRounded(
            MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix(),
            peekPositionLimit,
            peekPositionLimit2,
            peekPositionValue8,
            peekPositionValue9,
            peekPositionValue / framebufferHeight,
            matrixStackValue / framebufferHeight,
            loadedValue / framebufferHeight,
            segmentSlider / framebufferHeight,
            peekPositionValue5 / framebufferHeight,
            logoRoundValue / framebufferHeight,
            (releaseRight >> 16 & 0xFF) / 255.0F,
            (releaseRight >> 8 & 0xFF) / 255.0F,
            (releaseRight & 0xFF) / 255.0F,
            (releaseRight >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount
         );
      }
   }

   public static float getReleaseRight(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String releaseRightText,
      String matrixStackText,
      Identifier id2,
      float releaseRightValue,
      float matrixStackValue,
      float segmentSlider,
      float releaseRightBound,
      int matrixStackLimit,
      int scaledY,
      boolean releaseRightFlag,
      float rocketsElytraBound,
      Runnable runnable
   ) {
      MatrixStackUtilsAmount.Ctl timeIdentifier = anims.getTimeIdentifier(releaseRightText);
      float releaseRightBound3 = (float)Math.ceil(getAmount3(matrixStackText)) + 16.0F + (id2 == null ? 0.0F : 13.0F);
      float identifierText = getIdentifierText(timeIdentifier, matrixStackText, id2, Math.max(releaseRightBound, releaseRightBound3));
      float releaseRight = releaseRightValue - identifierText;
      float pilotStatusBound = anims.getAmount(releaseRightText, releaseRightFlag && canFlag(matrixStackLimit, scaledY, releaseRight, matrixStackValue, identifierText, segmentSlider));
      float amountFlag = timeIdentifier.getAmountFlag(releaseRightFlag ? 1.0F : 0.0F, 14.0F);
      float releaseRightLimit = Math.max(pilotStatusBound, rocketsElytraBound) * amountFlag;
      int releaseRightValue6 = CardMatrixUtils.getValue(
         CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), MatrixEspUtils.releaseRightValue, 0.16),
         CardMatrixUtils.getValue(getValue(MatrixEspUtils.releaseRightValue, 0.92F), -1, 0.1 * pilotStatusBound + 0.07 * rocketsElytraBound),
         amountFlag
      );
      boolean translatePush = canTranslatePush(context, releaseRight + identifierText / 2.0F, matrixStackValue + segmentSlider / 2.0F, anims.getTime(releaseRightText, 0.05F));
      if (releaseRightLimit > 0.01F) {
         MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
            context, releaseRight, matrixStackValue, identifierText, segmentSlider, 5.0F, CardMatrixUtils.getReleaseRight(0.16 * releaseRightLimit + 0.1 * rocketsElytraBound * amountFlag), 4.0F, 0.5F
         );
      }

      MatrixPeekUtilsRender.renderMatrixPeek(context, releaseRight, matrixStackValue, identifierText, segmentSlider, 5.0F, releaseRightValue6);
      handleTextIdentifier(timeIdentifier, 0, matrixStackText, id2, releaseRight, matrixStackValue, identifierText, segmentSlider, CardMatrixUtils.getValue(-7630437, getReleaseRight(), amountFlag));
      renderPopMatrices(context, translatePush);
      if (releaseRightFlag) {
         hits.handleFlag(releaseRight, matrixStackValue - 1.0F, identifierText, segmentSlider + 2.0F, runnable, anims, releaseRightText);
      }

      return releaseRight;
   }

   public static void renderReleaseRight(DrawContext context, String releaseRightText, float releaseRightValue, float randomValue, float materialFactor) {
      SkinTextures skinTextures = releaseRightText == null ? null : PlayerSkinResolver.getSkinTextures(releaseRightText);
      if (skinTextures != null) {
         Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
         Identifier texturePath = skinTextures.body().texturePath();
         float max4 = Math.max(3.0F, materialFactor / 5.0F);
         CorzRoundedUtils.handleTextureRounded(positionMatrix, releaseRightValue, randomValue, releaseRightValue + materialFactor, randomValue + materialFactor, 0.125F, 0.125F, 0.25F, 0.25F, texturePath, max4);
         CorzRoundedUtils.handleTextureRounded(positionMatrix, releaseRightValue, randomValue, releaseRightValue + materialFactor, randomValue + materialFactor, 0.625F, 0.125F, 0.75F, 0.25F, texturePath, max4);
      } else {
         float corzLicenseLimit = Math.max(3.0F, materialFactor / 4.0F);
         MatrixPeekUtilsRender.renderForDrawContextFloat(
            context,
            releaseRightValue,
            randomValue,
            materialFactor,
            materialFactor,
            corzLicenseLimit,
            CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), MatrixEspUtils.releaseRightValue, 0.16),
            CardMatrixUtils.getReleaseRight(0.3)
         );
         String matrixStackText = releaseRightText != null && !releaseRightText.isEmpty() ? releaseRightText.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
         float amount3 = getAmount3(matrixStackText);
         handle(
            matrixStackText,
            releaseRightValue + (materialFactor - amount3) / 2.0F,
            getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown(Math.round(randomValue), Math.round(materialFactor)),
            CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.25)
         );
      }
   }

   public static int getValueForInt(int matrixStackValue, int w, int matrixStackDivisor, int matrixStackValue3, int matrixStackFactor) {
      return matrixStackValue + Math.round((float)(matrixStackFactor * (w + matrixStackValue3)) / matrixStackDivisor);
   }

   public static float getAmount(DrawContext context, String matrixStackText, float matrixStackValue, float cardOffset, int matrixStackValue3) {
      float matrixStackFactor = 0.78F;
      float materialFactor = 11.0F;
      float matrixStackValue6 = (float)Math.ceil(getAmount(matrixStackText, matrixStackFactor)) + 10.0F;
      int matrixStackValue7 = 0xFF000000 | matrixStackValue3 & 16777215;
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixStackValue, cardOffset, matrixStackValue6, materialFactor, 5.0F, getValue(matrixStackValue7, 0.18F), getValue(matrixStackValue7, 0.42F));
      handlePushScale(matrixStackText, matrixStackValue + 5.0F, getAmount(cardOffset, materialFactor, matrixStackFactor), matrixStackFactor, CardMatrixUtils.getValue(matrixStackValue7, -1, 0.3));
      return matrixStackValue6;
   }

   public static void handlePeekPosition(
      float peekPositionValue, float matrixStackValue, float loadedValue, float peekPositionValue4, float peekPositionValue5, int releaseRight, float panelSamplerValue, float peekPositionValue8, boolean peekPositionFlag, float peekPositionLimit, float peekPositionLimit2, float peekPositionValue9, float peekPositionValue10
   ) {
      if (!(loadedValue <= 0.0F) && !(peekPositionValue4 <= 0.0F) && !(peekPositionValue9 <= peekPositionLimit) && !(peekPositionValue10 <= peekPositionLimit2)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         CorzRoundedUtils.handle(
            MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix(),
            peekPositionLimit,
            peekPositionLimit2,
            peekPositionValue9,
            peekPositionValue10,
            peekPositionValue / framebufferHeight,
            matrixStackValue / framebufferHeight,
            loadedValue / framebufferHeight,
            peekPositionValue4 / framebufferHeight,
            peekPositionValue5 / framebufferHeight,
            (releaseRight >> 16 & 0xFF) / 255.0F,
            (releaseRight >> 8 & 0xFF) / 255.0F,
            (releaseRight & 0xFF) / 255.0F,
            (releaseRight >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount,
            panelSamplerValue,
            peekPositionValue8,
            peekPositionFlag
         );
      }
   }

   public static float getAmountForFloatFlag(float matrixStackFactor, boolean matrixStackFlag) {
      matrixStackFactor = getMax(matrixStackFactor);
      return matrixStackFlag ? 1.0F - matrixStackFactor * matrixStackFactor : getAmountForFloat(matrixStackFactor / 0.6F);
   }

   public static float getAmountForDrawContextHits(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      String[] texts,
      int matrixStackValue,
      float matrixStackValue2,
      float brownValue,
      float segmentSlider,
      int matrixStackLimit,
      int scaledY,
      IntConsumer intConsumer
   ) {
      float matrixStackTotal = 1.0F;

      for (String textsItem : texts) {
         matrixStackTotal += (float)Math.ceil(getAmount3(textsItem)) + 12.0F + 1.0F;
      }

      float releaseRightValue = matrixStackValue2 - matrixStackTotal;
      getReleaseRight(context, hits, anims, matrixStackText, texts, matrixStackValue, releaseRightValue, brownValue, segmentSlider, segmentSlider - 2.0F, 6.0F, 5.0F, matrixStackLimit, scaledY, intConsumer);
      return releaseRightValue;
   }

   private static void handleReleaseRight(float releaseRightValue, float matrixStackValue, float identifierText, float segmentSlider, float releaseRightValue5, float releaseRightLimit) {
      float logoRoundValue = MatrixPeekUtilsRender.getAmount();
      float releaseRightFactor = getAmount3((float)(System.nanoTime() / 1.0E9 / 1.7 % 1.0));
      float releaseRightValue8 = releaseRightValue - 14.0F + (identifierText + 28.0F) * releaseRightFactor;
      int releaseRight = getValue(CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.55), 0.24F * releaseRightLimit);

      for (int i = 1; i <= 4; i++) {
         float releaseRightValue10 = 3.0F * i;
         handlePeekPosition(releaseRightValue, matrixStackValue, identifierText, segmentSlider, releaseRightValue5, logoRoundValue, releaseRight, Math.max(releaseRightValue - 1.0F, releaseRightValue8 - releaseRightValue10), matrixStackValue - 1.0F, Math.min(releaseRightValue + identifierText + 1.0F, releaseRightValue8 + releaseRightValue10), matrixStackValue + segmentSlider + 1.0F);
      }
   }

   private static void handle(float matrixStackValue, float matrixStackValue2, float loadedValue, float matrixStackValue4, float matrixStackValue5, float matrixStackValue6, float matrixStackValue7, int releaseRight, boolean matrixStackFlag) {
      float matrixStackValue9 = matrixStackValue2 + 16.0F;
      float matrixStackValue10 = matrixStackValue4 - 16.0F;
      float matrixStackValue11 = matrixStackValue2 + matrixStackValue4;
      float peekPositionLimit = matrixStackValue + matrixStackValue5 + matrixStackValue7;
      float matrixStackValue13 = matrixStackValue6 * 2.0F + 2.0F;
      float panelSamplerValue = 3.0F / CorzRoundedUtils.getFramebufferHeight();
      float matrixStackValue15 = 5.0F;
      handlePeekPosition(matrixStackValue, matrixStackValue2, matrixStackValue5, 16.0F + matrixStackValue13, matrixStackValue6, releaseRight, panelSamplerValue, 0.85F, matrixStackFlag, matrixStackValue - matrixStackValue15, matrixStackValue2 - matrixStackValue15, peekPositionLimit, matrixStackValue9);
      handlePeekPosition(matrixStackValue, matrixStackValue9 - matrixStackValue13, loadedValue, matrixStackValue10 + matrixStackValue13, matrixStackValue6, releaseRight, panelSamplerValue, 0.85F, matrixStackFlag, matrixStackValue - matrixStackValue15, matrixStackValue9, peekPositionLimit, matrixStackValue11 + matrixStackValue15);
      handlePeekPosition(matrixStackValue, matrixStackValue9, loadedValue, matrixStackValue10, matrixStackValue6, releaseRight, panelSamplerValue, 0.85F, matrixStackFlag, peekPositionLimit, matrixStackValue9 - matrixStackValue15, matrixStackValue + loadedValue + matrixStackValue15, matrixStackValue11 + matrixStackValue15);
      handlePeekPosition(matrixStackValue, matrixStackValue9, loadedValue, matrixStackValue10, matrixStackValue6, releaseRight, panelSamplerValue, 0.85F, matrixStackFlag, matrixStackValue + matrixStackValue5, matrixStackValue9 - matrixStackValue15, peekPositionLimit, matrixStackValue9);
   }

   public static float getAmount(DrawContext context, int matrixStackValue, int matrixStackValue2, String matrixStackText, float matrixStackValue3, float priceValue, boolean matrixStackFlag) {
      float materialFactor = 4.0F;
      float matrixStackValue6 = priceValue + 7.6219F - materialFactor / 2.0F;
      MatrixPeekUtilsRender.renderMatrixPeek(context, getAmount(matrixStackValue3), matrixStackFlag ? getAmount(matrixStackValue6) : matrixStackValue6, materialFactor, materialFactor, materialFactor / 2.0F, matrixStackValue);
      handlePushScale(matrixStackText, getAmount(matrixStackValue3) + materialFactor + 4.0F, priceValue, 0.82F, matrixStackValue2);
      return getAmountForString(matrixStackText);
   }

   private static String getCharAt(char index) {
      return index < charAtTexts.length ? charAtTexts[index] : String.valueOf(index);
   }

   public static void renderReleaseRight(DrawContext context, float releaseRightValue, float releaseRightValue2, float releaseRightFactor, float matrixStackLimit, float cardMatrixFactor) {
      float materialFactor = 4.0F;
      float releaseRightValue6 = materialFactor / 2.0F;
      float configsLikeValue = releaseRightValue2 - releaseRightValue6;
      MatrixPeekUtilsRender.renderMatrixPeek(context, releaseRightValue, configsLikeValue, releaseRightFactor, materialFactor, releaseRightValue6, CardMatrixUtils.getValue(-13948620, MatrixEspUtils.releaseRightValue, 0.04));
      float releaseRightLimit = getAmount(releaseRightFactor * getMax(matrixStackLimit));
      if (releaseRightLimit > 0.0F) {
         int matrixStackValue = CardMatrixUtils.getValue(-14673876, MatrixEspUtils.releaseRightValue, 0.4);
         int releaseRight = CardMatrixUtils.getValue(-14410956, MatrixEspUtils.releaseRightValue, 0.64);
         MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, releaseRightValue, configsLikeValue, releaseRightLimit, materialFactor, releaseRightValue6, CardMatrixUtils.getReleaseRight(0.34), 3.5F, 0.34F);
         MatrixPeekUtilsRender.renderMatrixPeek(context, releaseRightValue, configsLikeValue, releaseRightLimit, materialFactor, releaseRightValue6, matrixStackValue);
         float releaseRightValue10 = releaseRightValue + releaseRightValue6;
         float releaseRightFactor2 = releaseRightValue + releaseRightLimit - releaseRightValue10;
         if (releaseRightFactor2 >= MatrixPeekUtilsRender.getAmount()) {
            VertexConsumer quads = VertexGuiUtils.getQuads();
            int releaseRightDivisor = Math.max(1, (int)(releaseRightFactor2 / 2.0F));

            for (int i = 0; i < releaseRightDivisor; i++) {
               float vertexColorValue = releaseRightValue10 + releaseRightFactor2 * i / releaseRightDivisor;
               float customFlameFactor = releaseRightValue10 + releaseRightFactor2 * (i + 1) / releaseRightDivisor;
               float matrixFactor = ((vertexColorValue + customFlameFactor) * 0.5F - releaseRightValue) / releaseRightFactor;
               VertexGuiUtils.handleVertexColor(
                  quads, vertexColorValue, configsLikeValue, customFlameFactor, configsLikeValue + materialFactor, CorzRoundedUtils.getPanelOverlay(MatrixAmountUtils.getValue(matrixStackValue, releaseRight, matrixFactor))
               );
            }

            VertexGuiUtils.updateBufferClose();
         }
      }

      float corzLicenseLimit = 3.0F + 0.5F * getMax(cardMatrixFactor);
      float matrixPeekValue = releaseRightValue + releaseRightLimit - corzLicenseLimit;
      float amountMax = releaseRightValue2 - corzLicenseLimit;
      MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
         context, matrixPeekValue, amountMax, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, CardMatrixUtils.getReleaseRight(0.24 + 0.18 * cardMatrixFactor), 4.0F, 0.34F + 0.16F * cardMatrixFactor
      );
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, amountMax, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, -658441);
   }

   public static float getAmount(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      String matrixStackText2,
      float matrixStackValue,
      float targetOffset,
      float segmentSlider,
      float releaseRightBound,
      int matrixStackLimit,
      int scaledY,
      float releaseRightLimit,
      Runnable runnable
   ) {
      return getReleaseRight(context, hits, anims, matrixStackText, matrixStackText2, null, matrixStackValue, targetOffset, segmentSlider, releaseRightBound, matrixStackLimit, scaledY, releaseRightLimit, releaseRightLimit, runnable);
   }

   public static float getAmountForDrawContextString(DrawContext context, String matrixStackText, float matrixStackValue, float ctxOffset, float materialFactor) {
      float priceValue = getAmount(ctxOffset, materialFactor, 1.22F);
      handlePushScale(matrixStackText, matrixStackValue, priceValue, 1.22F, -920587);
      handlePushScale(matrixStackText, matrixStackValue + MatrixPeekUtilsRender.getAmount(), priceValue, 1.22F, getValue(-920587, 0.55F));
      return matrixStackValue + getAmount(matrixStackText, 1.22F);
   }

   public static float getMax(float matrixStackLimit) {
      return matrixStackLimit < 0.0F ? 0.0F : (matrixStackLimit > 1.0F ? 1.0F : matrixStackLimit);
   }

   public static boolean canFlag(double matrixStackLimit, double scaledY, float matrixStackValue, float vectorTransformValue, float identifierText, float segmentSlider) {
      return matrixStackLimit >= matrixStackValue && matrixStackLimit <= matrixStackValue + identifierText && scaledY >= vectorTransformValue && scaledY <= vectorTransformValue + segmentSlider;
   }

   public static float getAmountFloat(float matrixStackValue, boolean matrixStackFlag) {
      matrixStackValue = getMax(matrixStackValue);
      return matrixStackFlag ? 1.0F - matrixStackValue : getAmountForFloat(matrixStackValue / 0.8F);
   }

   public static float getAmount3(String matrixStackText) {
      return getFontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont(matrixStackText);
   }

   public static float getReleaseRight(DrawContext context, String releaseRightText, float releaseRightValue, float matrixStackValue) {
      float matrixStackFactor = 0.78F;
      float materialFactor = 11.0F;
      float releaseRight = getAmount(releaseRightText);
      MatrixPeekUtilsRender.renderMatrixPeek(context, releaseRightValue, matrixStackValue, releaseRight, materialFactor, 5.0F, getValue(MatrixEspUtils.releaseRightValue, 0.92F));
      handlePushScale(releaseRightText, releaseRightValue + 5.0F, getAmount(matrixStackValue, materialFactor, matrixStackFactor), matrixStackFactor, getReleaseRight());
      return releaseRight;
   }

   public static void render(DrawContext context, float matrixStackValue, float vectorTransformValue, float materialFactor, float segmentSlider, float lockFactor, float matrixStackValue6, float releaseRightLimit) {
      RenderMatrixUtilsRelease.render(context, matrixStackValue, vectorTransformValue, materialFactor, segmentSlider, 7.0F, releaseRightLimit, lockFactor, matrixStackValue6);
   }

   public static float getAmount3(float matrixStackFactor) {
      matrixStackFactor = getMax(matrixStackFactor);
      if (matrixStackFactor < 0.5F) {
         return 4.0F * matrixStackFactor * matrixStackFactor * matrixStackFactor;
      } else {
         float matrixStackFactor2 = 1.0F - matrixStackFactor;
         return 1.0F - 4.0F * matrixStackFactor2 * matrixStackFactor2 * matrixStackFactor2;
      }
   }

   public static void render(DrawContext context, float matrixStackValue, float scaledHeight, float materialFactor, float matrixStackValue4, int matrixStackValue5, int matrixStackValue6) {
      MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, matrixStackValue + 3.0F, scaledHeight + 7.0F, materialFactor - 6.0F, matrixStackValue4 - 4.0F, 10.0F, 1375731712, 20.0F, 1.0F);
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixStackValue, scaledHeight, materialFactor, matrixStackValue4, 10.0F, matrixStackValue5, matrixStackValue6);
   }

   private static float getAmount(double matrixStackDivisor) {
      return (float)((Math.sin(System.nanoTime() / 1.0E9 * Math.PI * 2.0 / matrixStackDivisor) + 1.0) / 2.0);
   }

   public static float getAmount(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      String matrixStackText2,
      float matrixStackValue,
      float editCluster,
      float segmentSlider,
      float releaseRightBound,
      int matrixStackLimit,
      int scaledY,
      boolean matrixStackFlag,
      float matrixStackValue7,
      Runnable runnable
   ) {
      return getReleaseRight(context, hits, anims, matrixStackText, matrixStackText2, matrixStackValue, false, editCluster, segmentSlider, releaseRightBound, matrixStackLimit, scaledY, matrixStackFlag, matrixStackValue7, runnable);
   }

   private static float getAmount4(float matrixStackValue) {
      return matrixStackValue < 0.3F ? getAmountForFloat(matrixStackValue / 0.3F) : 1.0F - getAmount((matrixStackValue - 0.3F) / 0.7F, 2.0F);
   }

   private static float getReleaseRight(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String releaseRightText,
      String[] texts,
      int releaseRightBound,
      float releaseRightValue,
      float brownValue,
      float segmentSlider,
      float materialFactor,
      float releaseRightValue5,
      float corzLicenseLimit,
      int matrixStackLimit,
      int scaledY,
      IntConsumer intConsumer
   ) {
      float releaseRightValue9 = (segmentSlider - materialFactor) / 2.0F;
      float configsLikeValue = brownValue + releaseRightValue9;
      float[] floats = new float[texts.length];
      float[] floats2 = new float[texts.length];
      float releaseRight = releaseRightValue + releaseRightValue9;

      for (int i = 0; i < texts.length; i++) {
         floats[i] = releaseRight;
         floats2[i] = (float)Math.ceil(getAmount3(texts[i])) + releaseRightValue5 * 2.0F;
         releaseRight += floats2[i] + releaseRightValue9;
      }

      MatrixPeekUtilsRender.renderForDrawContextFloat(context, releaseRightValue, brownValue, releaseRight - releaseRightValue, segmentSlider, corzLicenseLimit, CardMatrixUtils.getValue7(), CardMatrixUtils.getValueInt());
      int index = Math.max(0, Math.min(texts.length - 1, releaseRightBound));
      float matrixPeekValue = anims.getAmount(releaseRightText + ":x", floats[index] - releaseRightValue, 20.0F) + releaseRightValue;
      float releaseRightValue12 = anims.getAmount(releaseRightText + ":w", floats2[index], 20.0F);
      MatrixStackUtilsAmount.Ctl timeIdentifier = anims.getTimeIdentifier(releaseRightText);
      float x2 = anims.getTime(releaseRightText, 0.07F);
      boolean translatePush = canTranslatePush(context, matrixPeekValue + releaseRightValue12 / 2.0F, configsLikeValue + materialFactor / 2.0F, x2);
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, configsLikeValue, releaseRightValue12, materialFactor, corzLicenseLimit - releaseRightValue9, getValue(MatrixEspUtils.releaseRightValue, 0.92F));
      renderPopMatrices(context, translatePush);
      int abcdefghijklmnopqrstuvwxyzBrown = getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown(Math.round(configsLikeValue), Math.round(materialFactor));

      for (int j = 0; j < texts.length; j++) {
         boolean releaseRightFlag = canFlag(matrixStackLimit, scaledY, floats[j], configsLikeValue, floats2[j], materialFactor);
         float releaseRightValue15 = anims.getAmount(releaseRightText + ":h" + j, releaseRightFlag && j != index);
         float cardMatrixFactor = anims.getAmount(releaseRightText + ":on" + j, j == index ? 1.0F : 0.0F, j == index ? 1.0F : 0.0F, 18.0F);
         int cardMatrixValue = CardMatrixUtils.getValue(-7630437, -920587, releaseRightValue15);
         boolean matricesFlag = j == timeIdentifier.activeIndex && canTranslatePush(context, floats[j] + floats2[j] / 2.0F, configsLikeValue + materialFactor / 2.0F, x2);
         handle(texts[j], floats[j] + releaseRightValue5, abcdefghijklmnopqrstuvwxyzBrown, CardMatrixUtils.getValue(cardMatrixValue, getReleaseRight(), cardMatrixFactor));
         renderPopMatrices(context, matricesFlag);
         int value = j;
         hits.handleFlag(floats[j], brownValue, floats2[j], segmentSlider, () -> {
            timeIdentifier.activeIndex = value;
            intConsumer.accept(value);
         }, anims, releaseRightText);
      }

      return releaseRight;
   }

   public static float getAmount(DrawContext context, boolean matrixStackFlag, String matrixStackText, float matrixStackValue, float priceValue, boolean matrixStackFlag2) {
      return getAmount(context, matrixStackFlag ? -10761846 : -10722709, matrixStackFlag ? CardMatrixUtils.getValue(-10761846, -7630437, 0.35) : -7630437, matrixStackText, matrixStackValue, priceValue, matrixStackFlag2);
   }

   public static int getValue() {
      return CardMatrixUtils.isModernFlag() ? -267579372 : -15921132;
   }

   private MatrixStackUtilsAmount() {
   }

   private static void handleTextIdentifier(MatrixStackUtilsAmount.Ctl ctl, int identifierValue, String identifierText, Identifier id, float matrixStackValue, float targetOffset, float identifierValue4, float segmentSlider, int identifierValue6) {
      if (ctl.lastNanoTimeIdentifier != 0L && ctl.ctlText != null) {
         float identifierFactor = getMax(ctl.identifierAmount / 0.45F);
         float identifierLimit = getAmountForFloat((ctl.identifierAmount - 0.3F) / 0.7F);
         if (identifierFactor < 1.0F) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(1.0F - identifierFactor);

            try {
               handle(identifierValue, ctl.ctlText, ctl.identifier, matrixStackValue, targetOffset, identifierValue4, segmentSlider, identifierValue6, -3.0F * identifierFactor);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }

         if (identifierLimit > 0.0F) {
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(identifierLimit);

            try {
               handle(identifierValue, identifierText, id, matrixStackValue, targetOffset, identifierValue4, segmentSlider, identifierValue6, 3.0F * (1.0F - identifierLimit));
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         }
      } else {
         handle(identifierValue, identifierText, id, matrixStackValue, targetOffset, identifierValue4, segmentSlider, identifierValue6, 0.0F);
      }
   }

   public static float getAmount(
      DrawContext context,
      MatrixStackUtilsAmount.Hits hits,
      MatrixStackUtilsAmount.Anims anims,
      String matrixStackText,
      Identifier id2,
      String matrixStackText2,
      float matrixStackValue,
      float matrixStackValue2,
      float matrixStackValue3,
      int matrixStackValue4,
      int mouseY,
      boolean matrixStackFlag,
      Runnable runnable
   ) {
      float materialFactor = 18.0F;
      float releaseRightValue = matrixStackValue - materialFactor;
      float configsLikeValue = matrixStackValue2 + (matrixStackValue3 - materialFactor) / 2.0F;
      float matrixStackValue9 = releaseRightValue - 2.0F;
      float matrixStackFactor = (float)Math.ceil(getAmount(matrixStackText2, 0.82F)) + 14.0F;
      float deltaVar = matrixStackValue9 - matrixStackFactor;
      float matrixStackLimit = anims.getAmount(matrixStackText + ":arm", matrixStackFlag ? 1.0F : 0.0F, 18.0F);
      if (matrixStackLimit > 0.01F) {
         float max10 = Math.max(10.0F, matrixStackFactor * matrixStackLimit);
         float matrixPeekValue = matrixStackLimit >= 1.0F ? deltaVar : matrixStackValue9 - max10;
         float max12 = getMax(matrixStackLimit / 0.35F);
         float amountForFloat = getAmountForFloat((matrixStackLimit - 0.86F) / 0.14F);
         MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, configsLikeValue, matrixStackValue9 - matrixPeekValue, materialFactor, 5.0F, getValue(-1750963, 0.13F * max12), getValue(-1750963, 0.42F * max12));
         if (amountForFloat > 0.01F) {
            handlePushScale(
               matrixStackText2,
               deltaVar + 7.0F,
               getAmount(configsLikeValue, materialFactor, 0.82F) + 3.0F * (1.0F - amountForFloat),
               0.82F,
               getValue(CardMatrixUtils.getValue(-1750963, -1, 0.18), amountForFloat)
            );
         }
      }

      if (matrixStackFlag) {
         hits.handle(deltaVar, configsLikeValue - 1.0F, matrixStackValue - deltaVar, materialFactor + 2.0F, runnable);
      }

      renderReleaseRight(context, hits, anims, matrixStackText, id2, releaseRightValue, configsLikeValue, materialFactor, 12.0F, matrixStackValue4, mouseY, true, matrixStackLimit, runnable);
      return deltaVar;
   }

   public static void handle(String matrixStackText, float matrixStackValue, float abcdefghijklmnopqrstuvwxyzBrown, int matrixStackValue3) {
      getFontRenderer().handleForStringFloat(matrixStackText, matrixStackValue, abcdefghijklmnopqrstuvwxyzBrown, new Color(matrixStackValue3, true));
   }

   public static float getReleaseRight(DrawContext context, float releaseRightValue, float brownValue, float identifierText, float segmentSlider, Identifier id, float releaseRightBound, float releaseRightValue5, float cardMatrixFactor) {
      float maxIcon = Math.max(releaseRightBound, releaseRightValue5 * 0.5F);
      int releaseRightValue7 = CardMatrixUtils.getValue(getValue(MatrixEspUtils.releaseRightValue, 1.0F), -1750963, cardMatrixFactor);
      if (releaseRightBound > 0.01F) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, releaseRightValue, brownValue, identifierText, segmentSlider, 6.0F, getValue(releaseRightValue7, 0.22F * releaseRightBound), 3.0F, 0.85F);
      }

      int matrixStackValue = CardMatrixUtils.getValue(
         CardMatrixUtils.getValue(318767103, getValue(MatrixEspUtils.releaseRightValue, 0.86F), maxIcon), getValue(-1750963, 0.8F), cardMatrixFactor
      );
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, releaseRightValue, brownValue, identifierText, segmentSlider, 6.0F, CardMatrixUtils.getValue7(), matrixStackValue);
      if (id == null) {
         return releaseRightValue + 9.0F;
      } else {
         float peekPositionFactor = 12.0F;
         MatrixPeekUtilsIdentifier.handle(
            releaseRightValue + 7.0F,
            brownValue + (segmentSlider - peekPositionFactor) / 2.0F,
            peekPositionFactor,
            peekPositionFactor,
            id,
            new Color(CardMatrixUtils.getValue(-10722709, -920587, maxIcon), true),
            MatrixPeekUtilsRender.getMatrixStack()
         );
         return releaseRightValue + 7.0F + peekPositionFactor + 6.0F;
      }
   }

   public static final class Anims {
      private final Map<String, MatrixStackUtilsAmount.Ctl> identifierCtlMap;
      private final Map<String, Float> animsFloatMap = new HashMap<>();

      public float getAmount(String animsText) {
         Float float2 = this.animsFloatMap.get(animsText);
         return float2 == null ? 0.0F : float2;
      }

      public void handleTime(String animsText) {
         this.getTimeIdentifier(animsText).ctlValue = System.nanoTime();
      }

      public float getTime(String animsText, float animsFactor) {
         MatrixStackUtilsAmount.Ctl ctl = this.identifierCtlMap.get(animsText);
         if (ctl != null && (ctl.animStartNanoTime != 0L || ctl.ctlValue != 0L)) {
            long nanoTime = System.nanoTime();
            float animsValue = 1.0F;
            if (ctl.animStartNanoTime != 0L) {
               float animsLimit = (float)(nanoTime - ctl.animStartNanoTime) / 1.8E8F;
               if (animsLimit >= 1.0F) {
                  ctl.animStartNanoTime = 0L;
               } else if (animsLimit > 0.0F) {
                  animsValue *= 1.0F - animsFactor * MatrixStackUtilsAmount.getAmount4(animsLimit);
               }
            }

            if (ctl.ctlValue != 0L) {
               float matrixStackValue = (float)(nanoTime - ctl.ctlValue) / 2.4E8F;
               if (matrixStackValue >= 1.0F) {
                  ctl.ctlValue = 0L;
               } else if (matrixStackValue > 0.0F) {
                  animsValue *= 1.0F + 0.07F * (float)Math.sin(Math.PI * MatrixStackUtilsAmount.getAmountForFloat(matrixStackValue));
               }
            }

            return animsValue;
         } else {
            return 1.0F;
         }
      }

      public float getAmount(String animsText, float animsValue, float thumbAmount, float ctlValue) {
         Float float2 = this.animsFloatMap.get(animsText);
         float botCrystalValue = float2 == null ? animsValue : (float)MatrixIdentifierUtils.getAmount(float2.floatValue(), thumbAmount, ctlValue, MatrixClearUtils.getAmount());
         if (Math.abs(botCrystalValue - thumbAmount) < 0.002F) {
            botCrystalValue = thumbAmount;
         }

         this.animsFloatMap.put(animsText, botCrystalValue);
         return botCrystalValue;
      }

      public Anims() {
         this.identifierCtlMap = new HashMap<>();
      }

      public void handle(String animsText, float animsValue) {
         this.animsFloatMap.put(animsText, animsValue);
      }

      public void handleTimeForString(String animsText) {
         this.getTimeIdentifier(animsText).animStartNanoTime = System.nanoTime();
      }

      public float getAmount(String animsText, float animsValue, float ctlValue) {
         return this.getAmount(animsText, animsValue, animsValue, ctlValue);
      }

      public float getAmount(String animsText, boolean animsFlag) {
         return this.getAmount(animsText, 0.0F, animsFlag ? 1.0F : 0.0F, animsFlag ? 16.0F : 11.0F);
      }

      private MatrixStackUtilsAmount.Ctl getTimeIdentifier(String identifierText) {
         MatrixStackUtilsAmount.Ctl timeIdentifier = this.identifierCtlMap.get(identifierText);
         if (timeIdentifier == null) {
            timeIdentifier = new MatrixStackUtilsAmount.Ctl();
            this.identifierCtlMap.put(identifierText, timeIdentifier);
         }

         return timeIdentifier;
      }

      public void handle(Predicate<String> predicate) {
         this.animsFloatMap.keySet().removeIf(keepx -> !predicate.test(keepx));
         this.identifierCtlMap.keySet().removeIf(keepx -> !predicate.test(keepx));
      }
   }

   private static final class Ctl {
      int activeIndex;
      long lastNanoTime;
      float previousValue;
      long lastNanoTimeMatricesPop;
      long ctlValue;
      Identifier identifier;
      Identifier identifierIdentifier;
      float identifierAmount;
      String identifierText;
      boolean identifierActive;
      float ctlAmount;
      long animStartNanoTime;
      float animatedValue;
      String ctlText;
      long lastNanoTimeIdentifier;
      long identifierValue;

      float getAmountFlag(float amount, float ctlValue) {
         if (!this.identifierActive) {
            this.identifierActive = true;
            this.ctlAmount = amount;
            return this.ctlAmount;
         } else {
            this.ctlAmount = (float)MatrixIdentifierUtils.getAmount(this.ctlAmount, amount, ctlValue, MatrixClearUtils.getAmount());
            if (Math.abs(this.ctlAmount - amount) < 0.002F) {
               this.ctlAmount = amount;
            }

            return this.ctlAmount;
         }
      }
   }

   public static final class Entrance {
      private static final int UNUSED_TWELVE = 12;
      public float entranceProgress = 1.0F;
      private long lastNanoTime;
      private long entranceValue;
      private boolean entranceActive;
      public float tabsSettingsAmount;
      public boolean activeFlag;
      public float matrixScissorAmount = 1.0F;
      public boolean matrixScissorActive;

      public boolean isAmount() {
         return this.entranceProgress <= 0.002F && this.tabsSettingsAmount <= 0.004F;
      }

      public float getAmount(int entranceValue) {
         return this.getAmountFlag(entranceValue).entranceProgress;
      }

      public boolean isTimeFlag() {
         long nanoTime = System.nanoTime();
         boolean timeFlag = this.lastNanoTime == 0L || nanoTime - this.lastNanoTime > 300000000L;
         this.lastNanoTime = nanoTime;
         if (this.entranceValue != 0L && (float)(nanoTime - this.entranceValue) / 1000000.0F > (this.entranceActive ? 0.0F : 312.0F) + 260.0F) {
            this.entranceValue = 0L;
         }

         return timeFlag;
      }

      public void updateTimeFlag() {
         this.entranceValue = System.nanoTime();
         this.entranceActive = false;
      }

      public void updateTimeFlagEntranceValue() {
         if (this.entranceValue == 0L) {
            this.entranceValue = System.nanoTime();
            this.entranceActive = true;
         }
      }

      public boolean isTime() {
         return this.entranceValue != 0L;
      }

      public Entrance() {
         this.activeFlag = true;
         this.matrixScissorActive = true;
      }

      public MatrixStackUtilsAmount.Entrance getAmountFlag(int entranceBound) {
         this.tabsSettingsAmount = 0.0F;
         this.entranceProgress = 1.0F;
         this.matrixScissorAmount = 1.0F;
         this.activeFlag = true;
         this.matrixScissorActive = true;
         if (this.entranceValue == 0L) {
            return this;
         } else {
            float entranceValue = Math.max(0.0F, (float)(this.lastNanoTime - this.entranceValue) / 1000000.0F);
            float entranceLimit = entranceValue - (this.entranceActive ? 0.0F : Math.min(Math.max(entranceBound, 0), 12) * 26.0F);
            if (entranceLimit >= 260.0F) {
               return this;
            } else {
               float matrixStackValue = Math.max(0.0F, entranceLimit / 260.0F);
               float amount = entranceLimit <= 0.0F ? 0.0F : MatrixStackUtilsAmount.getAmountForFloat(entranceLimit / 150.0F);
               if (this.entranceActive) {
                  this.entranceProgress = 0.55F + 0.45F * amount;
                  this.matrixScissorAmount = 0.965F + 0.035000026F * MatrixStackUtilsAmount.getAmount(matrixStackValue, 1.1F);
               } else {
                  this.entranceProgress = amount;
                  this.matrixScissorAmount = 0.9F + 0.100000024F * MatrixStackUtilsAmount.getAmount(matrixStackValue, 1.9F);
                  this.tabsSettingsAmount = (1.0F - amount) * 0.5F * MatrixStackUtilsAmount.getMax(entranceValue / 70.0F);
                  this.matrixScissorActive = entranceLimit >= 75.0F;
               }

               this.activeFlag = matrixStackValue > 0.6F;
               return this;
            }
         }
      }
   }

   public static final class Hits {
      private final boolean hitsActive;
      public static final MatrixStackUtilsAmount.Hits INSTANCE = new MatrixStackUtilsAmount.Hits(true);
      private final List<MatrixStackUtilsAmount.Hits.Hit> animsPressHitList = new ArrayList<>();

      public void handle(float hitsValue, float plusDismissValue, float materialFactor, float segmentSlider, Runnable runnable) {
         this.handleFlag(hitsValue, plusDismissValue, materialFactor, segmentSlider, runnable, null, null);
      }

      private Hits(boolean flag) {
         this.hitsActive = flag;
      }

      public void update() {
         this.animsPressHitList.clear();
      }

      public boolean canAnimsPress(double animsPressLimit, double iconsClearValue) {
         for (int i = this.animsPressHitList.size() - 1; i >= 0; i--) {
            MatrixStackUtilsAmount.Hits.Hit hit = this.animsPressHitList.get(i);
            if (animsPressLimit >= hit.x && animsPressLimit <= hit.x + hit.w && iconsClearValue >= hit.y && iconsClearValue <= hit.y + hit.h) {
               if (hit.anims != null && hit.press != null) {
                  hit.anims.handleTimeForString(hit.press);
               }

               hit.act.run();
               return true;
            }
         }

         return false;
      }

      public Hits() {
         this(false);
      }

      public void handleFlag(float hitsValue, float matrixStackValue, float materialFactor, float segmentSlider, Runnable runnable, MatrixStackUtilsAmount.Anims anims, String hitsText) {
         if (!this.hitsActive && runnable != null && !(materialFactor <= 0.0F) && !(segmentSlider <= 0.0F)) {
            this.animsPressHitList.add(new MatrixStackUtilsAmount.Hits.Hit(hitsValue, matrixStackValue, materialFactor, segmentSlider, runnable, anims, hitsText));
         }
      }

      private record Hit(float x, float y, float w, float h, Runnable act, MatrixStackUtilsAmount.Anims anims, String press) {
      }
   }

   public static final class Swap {
      private float previousTextWidth;
      private float swapAmount;
      private String swapText;
      private float swapTextWidth;
      private float lastAmountParam;
      private String previousRawText;
      private String rawText;
      private long lastNanoTime;
      private static final float UNUSED_THREE = 3.0F;
      private String previousFormattedText;
      private float lastMaxWidth = Float.NaN;

      private static void handle(String swapText, float swapValue, float priceValue, float x2, float swapValue4, int matrixStackValue, float panelOverlayLimit) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            MatrixStackUtilsAmount.getPopPush(swapText, swapValue, priceValue, x2, swapValue4, matrixStackValue);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }

      public String formatText() {
         return this.rawText == null ? "" : this.rawText;
      }

      public float getText() {
         return this.previousRawText == null ? 1.0F : MatrixStackUtilsAmount.getAmountForFloat((this.getTextTime() - 0.3F) / 0.7F);
      }

      public float getAmount(String swapText, float swapValue, float priceValue, float matrixStackFactor, int matrixStackValue, float swapValue5) {
         return this.getTextAmount(swapText, swapValue, priceValue, matrixStackFactor, 0.0F, matrixStackValue, swapValue5, false);
      }

      private float getTextTime() {
         return this.previousRawText == null ? 1.0F : (float)(System.nanoTime() - this.lastNanoTime) / 1000000.0F / 170.0F;
      }

      public void updateTextMax() {
         this.rawText = this.previousRawText = this.swapText = this.previousFormattedText = null;
         this.lastMaxWidth = Float.NaN;
         this.lastNanoTime = 0L;
      }

      public float getTextAmount(String text, float swapValue, float priceValue, float matrixStackFactor, float swapValue4, int matrixStackValue, float swapValue6, boolean swapFlag) {
         if (text == null) {
            text = "";
         }

         if (this.rawText == null) {
            this.rawText = text;
            this.lastMaxWidth = Float.NaN;
         } else if (!this.rawText.equals(text)) {
            this.previousRawText = this.rawText;
            this.previousFormattedText = this.swapText;
            this.previousTextWidth = this.swapTextWidth;
            this.rawText = text;
            this.swapText = null;
            this.lastNanoTime = System.nanoTime();
         }

         float textTime = this.getTextTime();
         if (textTime >= 1.0F) {
            this.previousRawText = null;
            this.previousFormattedText = null;
         }

         this.handleTextAmount(Math.max(0.0F, swapValue6 - swapValue), matrixStackFactor, swapValue4);
         if (this.previousRawText == null) {
            MatrixStackUtilsAmount.getPopPush(this.swapText, swapFlag ? swapValue6 - this.swapTextWidth : swapValue, priceValue, matrixStackFactor, swapValue4, matrixStackValue);
            return this.swapTextWidth;
         } else {
            float max7 = MatrixStackUtilsAmount.getMax(textTime / 0.45F);
            float swapLimit = MatrixStackUtilsAmount.getAmountForFloat((textTime - 0.3F) / 0.7F);
            if (max7 < 1.0F) {
               handle(this.previousFormattedText, swapFlag ? swapValue6 - this.previousTextWidth : swapValue, priceValue - 3.0F * max7, matrixStackFactor, swapValue4, matrixStackValue, 1.0F - max7);
            }

            if (swapLimit > 0.0F) {
               handle(this.swapText, swapFlag ? swapValue6 - this.swapTextWidth : swapValue, priceValue + 3.0F * (1.0F - swapLimit), matrixStackFactor, swapValue4, matrixStackValue, swapLimit);
            }

            return this.swapTextWidth;
         }
      }

      private static String formatText(String formatText, float swapLimit, float swapDivisor, float swapValue) {
         if (swapValue == 0.0F) {
            return MatrixStackUtilsAmount.getFontRenderer().formatText(formatText, swapLimit / swapDivisor);
         } else if (MatrixStackUtilsAmount.getAmount(formatText, swapDivisor, swapValue) <= swapLimit) {
            return formatText;
         } else {
            String swapText = formatText;

            while (swapText.length() > 1 && MatrixStackUtilsAmount.getAmount(swapText + "...", swapDivisor, swapValue) > swapLimit) {
               swapText = swapText.substring(0, swapText.length() - 1);
            }

            return swapText.stripTrailing() + "...";
         }
      }

      private void handleTextAmount(float max, float amount, float swapValue) {
         boolean swapFlag = max == this.lastMaxWidth && amount == this.lastAmountParam && swapValue == this.swapAmount;
         if (!swapFlag || this.swapText == null || this.previousRawText != null && this.previousFormattedText == null) {
            this.lastMaxWidth = max;
            this.lastAmountParam = amount;
            this.swapAmount = swapValue;
            this.swapText = formatText(this.rawText, max, amount, swapValue);
            this.swapTextWidth = MatrixStackUtilsAmount.getAmount(this.swapText, amount, swapValue);
            if (this.previousRawText != null) {
               this.previousFormattedText = formatText(this.previousRawText, max, amount, swapValue);
               this.previousTextWidth = MatrixStackUtilsAmount.getAmount(this.previousFormattedText, amount, swapValue);
            }
         }
      }
   }
}
