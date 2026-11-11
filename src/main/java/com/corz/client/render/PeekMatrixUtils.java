package com.corz.client.render;

import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class PeekMatrixUtils {
   public static void handle(
      float peekMatrixValue, float peekMatrixValue2, float n3, float n4, float iconSize, Color color, float f2, float panelSamplerValue, boolean peekMatrixFlag, MatrixStack matrixStack
   ) {
      handlePeekPosition(peekMatrixValue, peekMatrixValue2, n3, n4, iconSize, color, f2, panelSamplerValue, peekMatrixFlag, matrixStack, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public static void handlePeekPosition(
      float peekPositionValue,
      float peekPositionValue2,
      float n3,
      float n4,
      float iconSize,
      Color color,
      float f2,
      float panelSamplerValue,
      boolean peekPositionFlag,
      MatrixStack matrixStack,
      float peekPositionValue8,
      float peekPositionValue9,
      float peekPositionValue10,
      float peekPositionValue11,
      float peekPositionValue12,
      float peekPositionValue13
   ) {
      int glowSize2 = (int)peekPositionValue;
      int cx2 = (int)peekPositionValue2;
      int peekPositionValue14 = (int)(peekPositionValue + n3);
      int peekPositionValue15 = (int)(peekPositionValue2 + n4);
      float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
      float max16 = Math.max(2.0F, f2 + 2.0F);
      Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
      CorzRoundedUtils.handleRectRounded(
         positionMatrix,
         glowSize2 - max16,
         cx2 - max16,
         peekPositionValue14 + max16,
         peekPositionValue15 + max16,
         glowSize2 / framebufferHeight,
         cx2 / framebufferHeight,
         (peekPositionValue14 - glowSize2) / framebufferHeight,
         (peekPositionValue15 - cx2) / framebufferHeight,
         iconSize / framebufferHeight,
         color.getRed() / 255.0F,
         color.getGreen() / 255.0F,
         color.getBlue() / 255.0F,
         color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount,
         f2 / framebufferHeight,
         panelSamplerValue,
         peekPositionFlag,
         peekPositionValue8 / framebufferHeight,
         peekPositionValue9 / framebufferHeight,
         peekPositionValue10 / framebufferHeight,
         peekPositionValue11 / framebufferHeight,
         peekPositionValue12 / framebufferHeight,
         peekPositionValue13 / framebufferHeight
      );
   }
}
