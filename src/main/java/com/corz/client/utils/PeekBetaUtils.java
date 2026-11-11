package com.corz.client.utils;

import com.corz.client.render.CorzRoundedUtils;
import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class PeekBetaUtils {
   public static void handle(float peekBetaValue, float editValue, float releaseRightLimit, float n4, float iconSize, Color color, MatrixStack matrixStack) {
      handlePeekPosition(peekBetaValue, editValue, releaseRightLimit, n4, iconSize, 0.0F, color, matrixStack);
   }

   private static void handlePeekPosition(float peekPositionValue, float editValue, float releaseRightLimit, float n4, float iconSize, float peekPositionValue6, Color color, MatrixStack matrixStack) {
      int peekPositionValue3 = (int)peekPositionValue;
      int editValue3 = (int)editValue;
      int peekPositionValue7 = (int)(peekPositionValue + releaseRightLimit);
      int peekPositionValue8 = (int)(editValue + n4);
      float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
      Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
      CorzRoundedUtils.handleRectRounded(
         positionMatrix,
         peekPositionValue3 - 1,
         editValue3 - 1,
         peekPositionValue7 + 1,
         peekPositionValue8 + 1,
         peekPositionValue3 / framebufferHeight,
         editValue3 / framebufferHeight,
         (peekPositionValue7 - peekPositionValue3) / framebufferHeight,
         (peekPositionValue8 - editValue3) / framebufferHeight,
         iconSize / framebufferHeight,
         peekPositionValue6 / framebufferHeight,
         color.getRed() / 255.0F,
         color.getGreen() / 255.0F,
         color.getBlue() / 255.0F,
         color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount
      );
   }

   public static void handle(float peekBetaValue, float editValue, float releaseRightLimit, float n4, float iconSize, float peekBetaValue6, Color color, MatrixStack matrixStack) {
      handlePeekPosition(peekBetaValue, editValue, releaseRightLimit, n4, iconSize, peekBetaValue6, color, matrixStack);
   }
}
