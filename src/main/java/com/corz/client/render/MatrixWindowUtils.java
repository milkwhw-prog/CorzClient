package com.corz.client.render;

import com.corz.client.gui.MatrixPeekUtilsRender;
import com.mojang.blaze3d.systems.RenderPass;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public final class MatrixWindowUtils {
   private static final Deque<int[]> DEQUE = new ArrayDeque<>();

   private MatrixWindowUtils() {
   }

   public static void updateDeque() {
      if (!DEQUE.isEmpty()) {
         DEQUE.pop();
      }
   }

   private static int[] getValues(int[] matrixWindowValues, int[] ints) {
      int matrixWindowValue = Math.max(matrixWindowValues[0], ints[0]);
      int max2 = Math.max(matrixWindowValues[1], ints[1]);
      int min3 = Math.min(matrixWindowValues[0] + matrixWindowValues[2], ints[0] + ints[2]);
      int min4 = Math.min(matrixWindowValues[1] + matrixWindowValues[3], ints[1] + ints[3]);
      return new int[]{matrixWindowValue, max2, Math.max(0, min3 - matrixWindowValue), Math.max(0, min4 - max2)};
   }

   public static void handleDequeScissor(RenderPass renderPass) {
      if (!DEQUE.isEmpty()) {
         int[] deque = DEQUE.peek();
         renderPass.enableScissor(deque[0], deque[1], deque[2], deque[3]);
      }
   }

   public static void handleDequeWindow(int dequeWindowFactor, int dequeWindowFactor2, int dequeWindowFactor3, int dequeWindowFactor4) {
      MinecraftClient instance = MinecraftClient.getInstance();
      double scaleFactor = instance.getWindow().getScaleFactor();
      int framebufferHeight = instance.getWindow().getFramebufferHeight();
      double y12 = dequeWindowFactor;
      double x22 = dequeWindowFactor2;
      double y22 = dequeWindowFactor3;
      double x12 = dequeWindowFactor4;
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      if (matrixStack != null) {
         Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
         if (CorzRoundedUtils.canFlag(positionMatrix)) {
            y12 = dequeWindowFactor * positionMatrix.m00() + positionMatrix.m30();
            y22 = dequeWindowFactor3 * positionMatrix.m00() + positionMatrix.m30();
            x22 = dequeWindowFactor2 * positionMatrix.m11() + positionMatrix.m31();
            x12 = dequeWindowFactor4 * positionMatrix.m11() + positionMatrix.m31();
         }
      }

      int dequeWindowValue = (int)Math.round(y12 * scaleFactor);
      int round2 = (int)Math.round(y22 * scaleFactor);
      int round3 = (int)Math.round(x22 * scaleFactor);
      int round4 = (int)Math.round(x12 * scaleFactor);
      int[] ints = new int[]{dequeWindowValue, framebufferHeight - round4, Math.max(0, round2 - dequeWindowValue), Math.max(0, round4 - round3)};
      if (!DEQUE.isEmpty()) {
         ints = getValues(DEQUE.peek(), ints);
      }

      DEQUE.push(ints);
   }

   public static boolean isDeque() {
      return !DEQUE.isEmpty();
   }
}
