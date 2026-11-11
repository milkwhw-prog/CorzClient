package com.corz.client.render;

import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class MatrixPeekUtilsIdentifier {
   public static void handleWhite(float whiteValue, float roundOffset, float peekPositionFactor, float whiteValue4, Identifier id, MatrixStack matrixStack) {
      handle(whiteValue, roundOffset, peekPositionFactor, whiteValue4, id, Color.WHITE, matrixStack);
   }

   public static void handlePeekPosition(
      float peekPositionTotal, float roundOffset, float peekPositionFactor, float peekPositionFactor2, float peekPositionValue, float peekPositionValue2, float peekPositionValue3, float peekPositionValue4, Identifier id, Color color, MatrixStack matrixStack
   ) {
      Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
      boolean peekPositionFlag = peekPositionValue == 0.0F && peekPositionValue3 == 1.0F || peekPositionValue == 1.0F && peekPositionValue3 == 0.0F;
      boolean peekPositionFlag2 = peekPositionValue2 == 0.0F && peekPositionValue4 == 1.0F || peekPositionValue2 == 1.0F && peekPositionValue4 == 0.0F;
      if (peekPositionFlag && peekPositionFlag2 && CorzRoundedUtils.canFlag(positionMatrix)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         float m00 = positionMatrix.m00();
         float m11 = positionMatrix.m11();
         float peekPositionValue5 = peekPositionFactor * m00 / framebufferHeight;
         float glfwLimit = peekPositionFactor2 * m11 / framebufferHeight;
         int peekPositionLimit = Math.round(peekPositionValue5);
         int round = Math.round(glfwLimit);
         if (peekPositionLimit > 0 && round > 0 && Math.abs(peekPositionValue5 - peekPositionLimit) < 0.02F && Math.abs(glfwLimit - round) < 0.02F) {
            Identifier corzclientTexture = ImageCorzclientUtils.getCorzclientTexture(id, peekPositionLimit, round);
            if (corzclientTexture != id) {
               id = corzclientTexture;
               float configsLikeValue = (peekPositionTotal * m00 + positionMatrix.m30()) / framebufferHeight;
               float peekPositionValue8 = (roundOffset * m11 + positionMatrix.m31()) / framebufferHeight;
               peekPositionTotal += (Math.round(configsLikeValue) - configsLikeValue) * framebufferHeight / m00;
               roundOffset += (Math.round(peekPositionValue8) - peekPositionValue8) * framebufferHeight / m11;
            }
         }
      }

      CorzRoundedUtils.handleTextureColor(
         positionMatrix,
         peekPositionTotal,
         roundOffset,
         peekPositionTotal + peekPositionFactor,
         roundOffset + peekPositionFactor2,
         peekPositionValue,
         peekPositionValue2,
         peekPositionValue3,
         peekPositionValue4,
         id,
         color.getRed() / 255.0F,
         color.getGreen() / 255.0F,
         color.getBlue() / 255.0F,
         color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount
      );
   }

   public static void handle(float matrixPeekValue, float roundOffset, float peekPositionFactor, float matrixPeekValue4, Identifier id, Color color, MatrixStack matrixStack) {
      handlePeekPosition(matrixPeekValue, roundOffset, peekPositionFactor, matrixPeekValue4, 0.0F, 0.0F, 1.0F, 1.0F, id, color, matrixStack);
   }
}
