package com.corz.client.render;

import com.corz.client.gui.VertexGuiUtils;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import com.corz.client.categories.visuals.NameTags;
import ai.auth.Module;

public final class VertexConsumerUtilsVector {
   private static final int[] windowFramebufferTable = new int[4];
   private static final Vector3f VECTOR3_FVECTOR3F = new Vector3f();
   private static final Vector4f VECTOR4F = new Vector4f();
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static final double[][][] vertexColorDoubles = new double[65][][];
   private static final double[][][] vertexConsumerDoubles = new double[65][][];
   private static final double[] vertexColorTable = new double[12];
   private static final Vector3f VECTOR3F = new Vector3f();
   private static float[] copyOfVertexColor = new float[64];
   private static float[] copyOf = new float[64];
   private static float[] vertexConsumerTable = new float[64];

   public static double getFpsInstance() {
      double fpsInstance;
      if (Module.mc.getCurrentFps() > 0) {
         fpsInstance = 1.0 / Module.mc.getCurrentFps();
      } else {
         fpsInstance = 1.0;
      }

      return fpsInstance;
   }

   private static void handleAmounts(int index, double vertexConsumerDivisor) {
      double[][] doubles = new double[4][];
      double[][] doubles2 = new double[4][];

      for (int i = 0; i < 4; i++) {
         int vertexConsumerCounter = 0;

         for (double vertexConsumerTotal = i * 90.0; vertexConsumerTotal < 90.0 + i * 90.0; vertexConsumerTotal += 90.0 / vertexConsumerDivisor) {
            vertexConsumerCounter++;
         }

         double[] doubles3 = new double[vertexConsumerCounter + 1];
         double[] doubles4 = new double[vertexConsumerCounter + 1];
         int index2 = 0;

         for (double degrees = i * 90.0; degrees < 90.0 + i * 90.0; degrees += 90.0 / vertexConsumerDivisor) {
            double radians = Math.toRadians(degrees);
            doubles3[index2] = Math.sin((float)radians);
            doubles4[index2] = Math.cos((float)radians);
            index2++;
         }

         double radians2 = Math.toRadians(90.0 + i * 90.0);
         doubles3[index2] = Math.sin((float)radians2);
         doubles4[index2] = Math.cos((float)radians2);
         doubles[i] = doubles3;
         doubles2[i] = doubles4;
      }

      vertexConsumerDoubles[index] = doubles2;
      vertexColorDoubles[index] = doubles;
   }

   public static Camera getCamera() {
      return Module.mc.gameRenderer.getCamera();
   }

   private static void handle(int vertexConsumerLimit) {
      if (vertexConsumerLimit > copyOfVertexColor.length) {
         int vertexConsumerValue = Math.max(vertexConsumerLimit, copyOfVertexColor.length * 2);
         copyOfVertexColor = Arrays.copyOf(copyOfVertexColor, vertexConsumerValue);
         copyOf = Arrays.copyOf(copyOf, vertexConsumerValue);
         vertexConsumerTable = Arrays.copyOf(vertexConsumerTable, vertexConsumerValue);
      }
   }

   private static void update() {
   }

   public static void handleFanTriangle(MatrixStack matrices, Color color, double fanTriangleValue, double fanTriangleValue2, double fanTriangleFactor, int value) {
      int clamp = MathHelper.clamp(value, 4, 360);
      int rgb = color.getRGB();
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      update();
      VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLE_FAN);

      for (int i = 0; i < 360; i += Math.min(360 / clamp, 360 - i)) {
         double angle = Math.toRadians(i);
         beginBuffer.vertex(positionMatrix, (float)(fanTriangleValue + Math.sin(angle) * fanTriangleFactor), (float)(fanTriangleValue2 + Math.cos(angle) * fanTriangleFactor), 0.0F)
            .color((rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, (rgb >> 24 & 0xFF) / 255.0F);
      }

      VertexGuiUtils.updateBufferClose();
      update2();
   }

   public static Vec3d getCameraPos() {
      return Module.mc.gameRenderer.getCamera().getCameraPos();
   }

   public static float getClamp(float clampFactor, float clampFactor2, float clampFactor3) {
      return (1.0F - MathHelper.clamp((float)(getFpsInstance() * clampFactor3), 0.0F, 1.0F)) * clampFactor + MathHelper.clamp((float)(getFpsInstance() * clampFactor3), 0.0F, 1.0F) * clampFactor2;
   }

   public static void handleVertexColor(
      Matrix4f matrix,
      float vertexColorValue,
      float vertexColorValue2,
      float vertexColorValue3,
      float vertexColorValue4,
      double peekPositionValue,
      double f12,
      double nameTagsValue,
      double vertexColorValue8,
      double vertexColorValue9,
      double vertexColorValue10,
      double vertexColorValue11,
      double vertexColorValue12,
      double vertexColorDivisor
   ) {
      double[] vertexColorTable22 = vertexColorTable;
      vertexColorTable22[0] = nameTagsValue - vertexColorValue12;
      vertexColorTable22[1] = vertexColorValue8 - vertexColorValue12;
      vertexColorTable22[2] = vertexColorValue12;
      vertexColorTable22[3] = nameTagsValue - vertexColorValue10;
      vertexColorTable22[4] = f12 + vertexColorValue10;
      vertexColorTable22[5] = vertexColorValue10;
      vertexColorTable22[6] = peekPositionValue + vertexColorValue9;
      vertexColorTable22[7] = f12 + vertexColorValue9;
      vertexColorTable22[8] = vertexColorValue9;
      vertexColorTable22[9] = peekPositionValue + vertexColorValue11;
      vertexColorTable22[10] = vertexColorValue8 - vertexColorValue11;
      vertexColorTable22[11] = vertexColorValue11;
      int index = (int)vertexColorDivisor;
      boolean vertexColorFlag = index == vertexColorDivisor && index >= 1 && index <= 64;
      if (vertexColorFlag && vertexColorDoubles[index] == null) {
         handleAmounts(index, vertexColorDivisor);
      }

      int index2 = 0;

      for (int i = 0; i < 4; i++) {
         double vertexColorValue13 = vertexColorTable22[i * 3];
         double vertexColorValue14 = vertexColorTable22[i * 3 + 1];
         double vertexColorFactor = vertexColorTable22[i * 3 + 2];
         if (vertexColorFlag) {
            double[] vertexColorValues = vertexColorDoubles[index][i];
            double[] vertexColorValues2 = vertexConsumerDoubles[index][i];
            handle(index2 + vertexColorValues.length);

            for (int j = 0; j < vertexColorValues.length; j++) {
               copyOfVertexColor[index2] = (float)vertexColorValue13 + (float)(vertexColorValues[j] * vertexColorFactor);
               copyOf[index2] = (float)vertexColorValue14 + (float)(vertexColorValues2[j] * vertexColorFactor);
               index2++;
            }
         } else {
            for (double degrees = i * 90.0; degrees < 90.0 + i * 90.0; degrees += 90.0 / vertexColorDivisor) {
               double radians = Math.toRadians(degrees);
               handle(index2 + 1);
               copyOfVertexColor[index2] = (float)vertexColorValue13 + (float)(Math.sin((float)radians) * vertexColorFactor);
               copyOf[index2] = (float)vertexColorValue14 + (float)(Math.cos((float)radians) * vertexColorFactor);
               index2++;
            }

            double radians2 = Math.toRadians(90.0 + i * 90.0);
            handle(index2 + 1);
            copyOfVertexColor[index2] = (float)vertexColorValue13 + (float)(Math.sin((float)radians2) * vertexColorFactor);
            copyOf[index2] = (float)vertexColorValue14 + (float)(Math.cos((float)radians2) * vertexColorFactor);
            index2++;
         }
      }

      if (!VertexGuiUtils.isFlag()) {
         VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLE_FAN);

         for (int k = 0; k < index2; k++) {
            beginBuffer.vertex(matrix, copyOfVertexColor[k], copyOf[k], 0.0F).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
         }

         VertexGuiUtils.updateBufferClose();
      } else if (index2 >= 3) {
         for (int m = 0; m < index2; m++) {
            matrix.transformPosition(copyOfVertexColor[m], copyOf[m], 0.0F, VECTOR3F);
            copyOfVertexColor[m] = VECTOR3F.x;
            copyOf[m] = VECTOR3F.y;
            vertexConsumerTable[m] = VECTOR3F.z;
         }

         VertexConsumer bufferBuilder = VertexGuiUtils.getBufferBuilder();

         for (int p = 1; p < index2 - 1; p++) {
            bufferBuilder.vertex(copyOfVertexColor[0], copyOf[0], vertexConsumerTable[0]).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
            bufferBuilder.vertex(copyOfVertexColor[p], copyOf[p], vertexConsumerTable[p]).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
            bufferBuilder.vertex(copyOfVertexColor[p + 1], copyOf[p + 1], vertexConsumerTable[p + 1]).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
         }
      }
   }

   public static void handleVertexColor(MatrixStack matrices, float vertexColorValue, float vertexColorValue2, float vertexColorValue3, float vertexColorValue4, int vertexColorValue5) {
      float vertexColorValue6 = (vertexColorValue5 >> 24 & 0xFF) / 255.0F;
      float vertexColorValue7 = (vertexColorValue5 >> 16 & 0xFF) / 255.0F;
      float vertexColorValue8 = (vertexColorValue5 >> 8 & 0xFF) / 255.0F;
      float vertexColorValue9 = (vertexColorValue5 & 0xFF) / 255.0F;
      matrices.push();
      matrices.scale(0.5F, 0.5F, 0.5F);
      matrices.translate((double)vertexColorValue, (double)vertexColorValue2, 0.0);
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.QUADS);
      beginBuffer.vertex(positionMatrix, 0.0F, 0.0F, 0.0F).color(vertexColorValue7, vertexColorValue8, vertexColorValue9, vertexColorValue6);
      beginBuffer.vertex(positionMatrix, 0.0F, vertexColorValue4, 0.0F).color(vertexColorValue7, vertexColorValue8, vertexColorValue9, vertexColorValue6);
      beginBuffer.vertex(positionMatrix, vertexColorValue3, vertexColorValue4, 0.0F).color(vertexColorValue7, vertexColorValue8, vertexColorValue9, vertexColorValue6);
      beginBuffer.vertex(positionMatrix, vertexColorValue3, 0.0F, 0.0F).color(vertexColorValue7, vertexColorValue8, vertexColorValue9, vertexColorValue6);
      VertexGuiUtils.updateBufferClose();
      matrices.pop();
   }

   public static void handlePeekPosition(
      MatrixStack matrices, Color color, double peekPositionValue, double f12, double nameTagsValue, double peekPositionValue4, double peekPositionValue5, double peekPositionValue6, double peekPositionValue7, double peekPositionValue8, double vertexColorDivisor
   ) {
      int rgb = color.getRGB();
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      handleVertexColor(
         positionMatrix,
         (rgb >> 16 & 0xFF) / 255.0F,
         (rgb >> 8 & 0xFF) / 255.0F,
         (rgb & 0xFF) / 255.0F,
         (rgb >> 24 & 0xFF) / 255.0F,
         peekPositionValue,
         f12,
         nameTagsValue,
         peekPositionValue4,
         peekPositionValue5,
         peekPositionValue6,
         peekPositionValue7,
         peekPositionValue8,
         vertexColorDivisor
      );
   }

   public static void handleVertexColor(
      Matrix4f matrix,
      float vertexColorValue,
      float vertexColorValue2,
      float vertexColorValue3,
      float vertexColorValue4,
      double vertexConsumerValue,
      double vertexColorValue6,
      double vertexColorValue7,
      double vertexColorValue8,
      double vertexColorValue9,
      double vertexColorValue10,
      double vertexColorValue11,
      double vertexColorValue12,
      double vertexColorFactor,
      double vertexColorDivisor
   ) {
      VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLE_STRIP);
      double[][] doubles = new double[][]{{vertexColorValue7 - vertexColorValue12, vertexColorValue8 - vertexColorValue12, vertexColorValue12}, {vertexColorValue7 - vertexColorValue10, vertexColorValue6 + vertexColorValue10, vertexColorValue10}, {vertexConsumerValue + vertexColorValue9, vertexColorValue6 + vertexColorValue9, vertexColorValue9}, {vertexConsumerValue + vertexColorValue11, vertexColorValue8 - vertexColorValue11, vertexColorValue11}};

      for (int i = 0; i < 4; i++) {
         double[] vertexColorValues = doubles[i];
         double vertexColorFactor2 = vertexColorValues[2];
         double degrees = i * 90.0;

         while (degrees < 90.0 + i * 90.0) {
            double radians = Math.toRadians(degrees);
            double sin = Math.sin((float)radians);
            double vertexColorValue13 = sin * vertexColorFactor2;
            double cos = Math.cos((float)radians);
            double vertexColorValue14 = cos * vertexColorFactor2;
            beginBuffer.vertex(matrix, (float)vertexColorValues[0] + (float)vertexColorValue13, (float)vertexColorValues[1] + (float)vertexColorValue14, 0.0F).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
            beginBuffer.vertex(matrix, (float)(vertexColorValues[0] + (float)vertexColorValue13 + sin * vertexColorFactor), (float)(vertexColorValues[1] + (float)vertexColorValue14 + cos * vertexColorFactor), 0.0F).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
            degrees += 90.0 / vertexColorDivisor;
         }

         double radians2 = Math.toRadians(90.0 + i * 90.0);
         double sin2 = Math.sin((float)radians2);
         double vertexColorValue15 = sin2 * vertexColorFactor2;
         double cos2 = Math.cos((float)radians2);
         double vertexColorValue16 = cos2 * vertexColorFactor2;
         beginBuffer.vertex(matrix, (float)vertexColorValues[0] + (float)vertexColorValue15, (float)vertexColorValues[1] + (float)vertexColorValue16, 0.0F).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
         beginBuffer.vertex(matrix, (float)(vertexColorValues[0] + (float)vertexColorValue15 + sin2 * vertexColorFactor), (float)(vertexColorValues[1] + (float)vertexColorValue16 + cos2 * vertexColorFactor), 0.0F).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
      }

      double[] vertexColorValues2 = doubles[0];
      double vertexColorValue17 = vertexColorValues2[2];
      beginBuffer.vertex(matrix, (float)vertexColorValues2[0], (float)vertexColorValues2[1] + (float)vertexColorValue17, 0.0F).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
      beginBuffer.vertex(matrix, (float)vertexColorValues2[0], (float)(vertexColorValues2[1] + (float)vertexColorValue17 + vertexColorFactor), 0.0F).color(vertexColorValue, vertexColorValue2, vertexColorValue3, vertexColorValue4);
      VertexGuiUtils.updateBufferClose();
   }

   private static void update2() {
   }

   public static void handleVertexColor(
      MatrixStack matrices, Color color, Color color6, Color color7, Color color8, float vertexColorValue, float vertexColorValue2, float vertexColorValue3, float vertexColorValue4, float vertexColorValue5, float vertexColorValue6
   ) {
      VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.QUADS);
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      beginBuffer.vertex(positionMatrix, vertexColorValue - 10.0F, vertexColorValue2 - 10.0F, 0.0F).color(color.getRGB());
      beginBuffer.vertex(positionMatrix, vertexColorValue - 10.0F, vertexColorValue2 + vertexColorValue4 + 20.0F, 0.0F).color(color6.getRGB());
      beginBuffer.vertex(positionMatrix, vertexColorValue + vertexColorValue3 + 20.0F, vertexColorValue2 + vertexColorValue4 + 20.0F, 0.0F).color(color7.getRGB());
      beginBuffer.vertex(positionMatrix, vertexColorValue + vertexColorValue3 + 20.0F, vertexColorValue2 - 10.0F, 0.0F).color(color8.getRGB());
      VertexGuiUtils.updateBufferClose();
   }

   public static void update3() {
      VertexGuiUtils.updateBufferBuilder();
   }

   public static void handle(MatrixStack matrices, Color color, double vertexConsumerValue, double f12, double nameTagsValue, double vertexConsumerValue4, double vertexConsumerValue5, double vertexColorDivisor) {
      handlePeekPosition(matrices, color, vertexConsumerValue, f12, nameTagsValue, vertexConsumerValue4, vertexConsumerValue5, vertexConsumerValue5, vertexConsumerValue5, vertexConsumerValue5, vertexColorDivisor);
   }

   public static void update4() {
      VertexGuiUtils.update();
   }

   public static Vec3d getWindowFramebuffer(Vec3d vec) {
      Camera camera = Module.mc.getEntityRenderDispatcher().camera;
      if (camera == null) {
         return null;
      } else {
         int framebufferHeight = Module.mc.getWindow().getFramebufferHeight();
         int[] windowFramebufferValues = windowFramebufferTable;
         windowFramebufferValues[2] = Module.mc.getWindow().getFramebufferWidth();
         windowFramebufferValues[3] = Module.mc.getWindow().getFramebufferHeight();
         Vector3f vector3f = VECTOR3_FVECTOR3F;
         Vec3d cameraPos = camera.getCameraPos();
         double deltaX = vec.x - cameraPos.x;
         double deltaY = vec.y - cameraPos.y;
         double deltaZ = vec.z - cameraPos.z;
         Vector4f mul = VECTOR4F.set((float)deltaX, (float)deltaY, (float)deltaZ, 1.0F).mul(NameTags.MATRIX4F3);
         MATRIX4F.set(NameTags.MATRIX4F).mul(NameTags.MATRIX4_FMATRIX4F).project(mul.x(), mul.y(), mul.z(), windowFramebufferValues, vector3f);
         return new Vec3d(
            vector3f.x / Module.mc.getWindow().getScaleFactor(), (framebufferHeight - vector3f.y) / Module.mc.getWindow().getScaleFactor(), vector3f.z
         );
      }
   }

   public static void handlePushPeek(MatrixStack matrices, Color color, Vec3d vec, Vec3d vec2) {
      matrices.push();
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      VertexBuilderUtils.handle(positionMatrix, vec, vec2, color.getRGB(), 1.0F);
      matrices.pop();
   }

   public static void handlePeekPosition(MatrixStack matrices, float peekPositionValue, float f2, float peekPositionValue3, float vertexColorValue, float f5, float amountForDouble, Color color) {
      VertexBuilderUtils.handle(
         matrices.peek().getPositionMatrix(),
         peekPositionValue,
         f2,
         peekPositionValue3,
         vertexColorValue,
         f5,
         amountForDouble,
         color.getRed() / 255.0F,
         color.getGreen() / 255.0F,
         color.getBlue() / 255.0F,
         color.getAlpha() / 255.0F
      );
   }

   public static void renderMatricesDraw(DrawContext context, ItemStack stack, int matricesDrawValue, int matricesDrawValue2, float matricesDrawDivisor, int matricesDrawValue3) {
      if (!stack.isEmpty()) {
         if (matricesDrawDivisor == 1.0F) {
            context.drawItem(stack, matricesDrawValue, matricesDrawValue2);
         } else {
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(matricesDrawDivisor, matricesDrawDivisor);
            context.drawItem(stack, (int)(matricesDrawValue / matricesDrawDivisor), (int)(matricesDrawValue2 / matricesDrawDivisor));
            context.getMatrices().popMatrix();
         }
      }
   }

   public static void render(
      DrawContext context, Color color, double vertexConsumerValue, double vertexConsumerValue2, double vertexConsumerValue3, double vertexConsumerValue4, double vertexConsumerValue5, double vertexConsumerValue6, double vertexConsumerValue7, double vertexConsumerValue8, double vertexColorFactor, double vertexColorDivisor
   ) {
      int rgb = color.getRGB();
      Matrix4f matrix4f = new Matrix4f();
      update();
      handleVertexColor(
         matrix4f,
         (rgb >> 16 & 0xFF) / 255.0F,
         (rgb >> 8 & 0xFF) / 255.0F,
         (rgb & 0xFF) / 255.0F,
         (rgb >> 24 & 0xFF) / 255.0F,
         vertexConsumerValue,
         vertexConsumerValue2,
         vertexConsumerValue3,
         vertexConsumerValue4,
         vertexConsumerValue5,
         vertexConsumerValue6,
         vertexConsumerValue7,
         vertexConsumerValue8,
         vertexColorFactor,
         vertexColorDivisor
      );
      update2();
   }
}
