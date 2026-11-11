package com.corz.client.gui;

import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.MatrixStackUtilsAmount;
import java.awt.Color;
import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import com.corz.client.utils.MaxMax2;

public final class MatrixStackUtilsRelease {

   public static void render(DrawContext context, float matrixStackValue, float configsLikeValue, float peekBound, float materialFactor) {
      renderForDrawContextFloat(context, matrixStackValue, configsLikeValue, peekBound, materialFactor, 1.0F);
   }

   public static float getRoot(DrawContext context, String rootText, String scaleText, float rootValue, float rootValue2, int matrixStackValue) {
      float rootValue4 = 0.0F;
      if (rootText != null && !rootText.isEmpty()) {
         MatrixStackUtilsAmount.handlePushScale(rootText.toUpperCase(Locale.ROOT), rootValue + 1.0F, rootValue2, 0.8F, -11117467);
         rootValue4 = 12.2F;
      }

      float priceValue = rootValue2 + rootValue4;
      MatrixStackUtilsAmount.handlePushScale(scaleText, rootValue, priceValue, 2.0F, matrixStackValue);
      MatrixStackUtilsAmount.handlePushScale(scaleText, rootValue + MatrixPeekUtilsRender.getAmount(), priceValue, 2.0F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.55F));
      return rootValue4 + 23.0F;
   }

   public static void render(DrawContext context, float matrixStackValue, float configsLikeValue, float materialFactor, float matrixStackValue4, float corzLicenseLimit) {
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue, configsLikeValue, materialFactor, matrixStackValue4, corzLicenseLimit, MatrixPeekUtilsRender.getAmount(), CardMatrixUtils.getValueInt());
   }

   public static void handle(String matrixStackText, float matrixStackValue, float matrixStackValue2, float materialFactor, float matrixStackFactor, int matrixStackValue5) {
      MatrixStackUtilsAmount.handlePushScale(matrixStackText, matrixStackValue, MatrixStackUtilsAmount.getAmount(matrixStackValue2, materialFactor, matrixStackFactor), matrixStackFactor, matrixStackValue5);
   }

   public static float getAmount(String matrixStackText, float matrixStackValue) {
      return MatrixStackUtilsAmount.getAmount(matrixStackText, matrixStackValue);
   }

   public static void renderForDrawContextFloat(DrawContext context, float matrixStackValue, float configsLikeValue, float peekBound, float materialFactor, float matrixStackBound) {
      MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, matrixStackValue + 3.0F, configsLikeValue + 7.0F, peekBound - 6.0F, materialFactor - 4.0F, 10.0F, 1375731712, 20.0F, 1.0F);
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixStackValue, configsLikeValue, peekBound, materialFactor, 10.0F, MatrixStackUtilsAmount.getValue(), 352321535);
      TwoDidUtils.renderMinPeek(context, 2, matrixStackValue, configsLikeValue, peekBound, materialFactor, 10.0F, 0.9F * Math.max(0.0F, Math.min(1.0F, matrixStackBound)));
   }

   public static void render3(DrawContext context, float matrixStackValue, float configsLikeValue, float materialFactor, float matrixStackValue4, float corzLicenseLimit) {
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue, configsLikeValue, materialFactor, matrixStackValue4, corzLicenseLimit, CardMatrixUtils.getValue7());
   }

   public static void render(DrawContext context, float matrixStackValue, float configsLikeValue, float materialFactor) {
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixStackValue, configsLikeValue, materialFactor, materialFactor, 5.0F, CardMatrixUtils.getValue7(), CardMatrixUtils.getValueInt());
   }

   public static float getMFloat() {
      return MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix().m00();
   }

   public static void renderScissorEnable(DrawContext context, float scissorEnableValue, float cubeSchematicBound, float buttonWidth, float cubeLimit) {
      context.enableScissor((int)Math.floor(scissorEnableValue), (int)Math.floor(cubeSchematicBound), (int)Math.ceil(scissorEnableValue + buttonWidth), (int)Math.ceil(cubeSchematicBound + cubeLimit));
   }

   public static void render(DrawContext context, float matrixStackValue, float cubeSchematicAmount, float buttonWidth, float cubeLimit, Identifier id, String matrixStackText, String scaleText) {
      float abcdefghijklmnopqrstuvwxyzBrown = MatrixStackUtilsAmount.getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown();
      float matrixStackValue6 = (id == null ? 0.0F : 26.0F) + abcdefghijklmnopqrstuvwxyzBrown + (scaleText == null ? 0.0F : 3.0F + abcdefghijklmnopqrstuvwxyzBrown * 0.82F);
      float matrixStackTotal = cubeSchematicAmount + Math.max(0.0F, (cubeLimit - matrixStackValue6) / 2.0F);
      float matrixStackValue7 = matrixStackValue + buttonWidth / 2.0F;
      if (id != null) {
         MatrixPeekUtilsIdentifier.handle(matrixStackValue7 - 9.0F, matrixStackTotal, 18.0F, 18.0F, id, new Color(-10722709, true), MatrixPeekUtilsRender.getMatrixStack());
         matrixStackTotal += 26.0F;
      }

      if (matrixStackText != null) {
         MatrixStackUtilsAmount.handle(matrixStackText, MatrixStackUtilsAmount.getAmount(matrixStackValue7 - MatrixStackUtilsAmount.getAmount3(matrixStackText) / 2.0F), matrixStackTotal, -920587);
      }

      if (scaleText != null) {
         MatrixStackUtilsAmount.handlePushScale(scaleText, matrixStackValue7 - MatrixStackUtilsAmount.getAmount(scaleText, 0.82F) / 2.0F, matrixStackTotal + abcdefghijklmnopqrstuvwxyzBrown + 3.0F, 0.82F, -7630437);
      }
   }

   public static Identifier getIdentifier(String identifierText) {
      return IcoGenUtils.getIdentifierForString(identifierText);
   }

   public static float getPeekPosition(float peekPositionFactor) {
      Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
      return peekPositionFactor * positionMatrix.m11() + positionMatrix.m31();
   }

   private MatrixStackUtilsRelease() {
   }

   public static void renderReleaseRight(DrawContext context, float releaseRightValue, float configsLikeValue, float materialFactor, float releaseRightValue4, float corzLicenseLimit, float cardMatrixFactor, float releaseRightValue7) {
      int matrixStackValue = CardMatrixUtils.getValue(CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), -1, 0.035 * cardMatrixFactor), MatrixEspUtils.releaseRightValue, 0.15 * releaseRightValue7);
      int releaseRightValue9 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(CardMatrixUtils.getValueInt(), 1040187391, cardMatrixFactor), CardMatrixUtils.getReleaseRight(0.55), releaseRightValue7);
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, releaseRightValue, configsLikeValue, materialFactor, releaseRightValue4, corzLicenseLimit, matrixStackValue, releaseRightValue9);
   }

   public static boolean canTranslatePush(DrawContext context, float x, float translatePushValue, float x2, float planValuesItem) {
      boolean translatePushFlag = Math.abs(x2 - 1.0F) > 5.0E-4F;
      boolean translatePushFlag2 = Math.abs(planValuesItem) > 5.0E-4F;
      if (!translatePushFlag && !translatePushFlag2) {
         return false;
      } else {
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(x, translatePushValue + planValuesItem, 0.0F);
         matrixStack.scale(x2, x2, 1.0F);
         matrixStack.translate(-x, -translatePushValue, 0.0F);
         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();
         matrices.translate(x, translatePushValue + planValuesItem);
         matrices.scale(x2, x2);
         matrices.translate(-x, -translatePushValue);
         return true;
      }
   }

   public static void renderPopMatrices(DrawContext context, boolean matricesFlag) {
      if (matricesFlag) {
         context.getMatrices().popMatrix();
         MatrixPeekUtilsRender.getMatrixStack().pop();
      }
   }

   public static int getReleaseRight(float releaseRightValue, float cardMatrixFactor) {
      return CardMatrixUtils.getValue(
         CardMatrixUtils.getValue(-7630437, -920587, releaseRightValue), CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.22), cardMatrixFactor
      );
   }

   public static void render(DrawContext context, float matrixStackValue, float configsLikeValue, float matrixStackValue3, float materialFactor, int matrixStackValue5, float matrixStackLimit) {
      if ((matrixStackValue5 & 1) == 1) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue, configsLikeValue, matrixStackValue3, materialFactor, 5.0F, 117440511);
      }

      if (matrixStackLimit > 0.01F) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue, configsLikeValue, matrixStackValue3, materialFactor, 5.0F, MatrixStackUtilsAmount.getValue(-1, 0.045F * matrixStackLimit));
      }
   }

   public static void renderReleaseRight(DrawContext context, float releaseRightValue, float amountMax, float materialFactor, float fontOffset, float releaseRightLimit) {
      if (releaseRightLimit > 0.01F) {
         MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, releaseRightValue, amountMax, materialFactor, fontOffset, 5.0F, CardMatrixUtils.getReleaseRight(0.16 * releaseRightLimit), 4.0F, 0.5F);
      }

      int matrixStackValue = CardMatrixUtils.getValue(MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.92F), -1, 0.1 * releaseRightLimit);
      MatrixPeekUtilsRender.renderMatrixPeek(context, releaseRightValue, amountMax, materialFactor, fontOffset, 5.0F, matrixStackValue);
   }

   public static void render(DrawContext context, float matrixStackValue, float configsLikeValue, float matrixStackValue3, float materialFactor, float cardMatrixFactor, float matrixStackValue6) {
      renderReleaseRight(context, matrixStackValue, configsLikeValue, matrixStackValue3, materialFactor, 5.0F, cardMatrixFactor, matrixStackValue6);
   }

   public static float getPeekPositionForFloat(float peekPositionFactor) {
      Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
      return peekPositionFactor * positionMatrix.m00() + positionMatrix.m30();
   }

   public static int getValue(int matrixStackValue, float matrixStackFactor) {
      return MatrixStackUtilsAmount.getValue(matrixStackValue, matrixStackFactor);
   }

   public static void render(DrawContext context, float matrixStackValue, float matrixStackValue2, float bound, MaxMax2 max7) {
      float matrixStackValue4 = max7.getAmount();
      if (!(matrixStackValue4 <= 0.01F) && max7.isFlag()) {
         float maxContentHeight = max7.getMaxContentHeight(bound);
         float max6 = max7.getMax(bound);
         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixStackValue, matrixStackValue2 + max6, 2.0F, maxContentHeight, 1.0F, MatrixStackUtilsAmount.getValue(-1, 0.22F * matrixStackValue4));
      }
   }

   public static void handle(Identifier id, float matrixStackValue, float roundOffset, float peekPositionFactor, int matrixStackValue4) {
      if (id != null) {
         MatrixPeekUtilsIdentifier.handle(matrixStackValue, roundOffset, peekPositionFactor, peekPositionFactor, id, new Color(matrixStackValue4, true), MatrixPeekUtilsRender.getMatrixStack());
      }
   }
}
