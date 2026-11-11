package com.corz.client.render;

import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.VertexGuiUtils;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public final class VertexConsumerUtilsAmounts {
   private static float framebufferHeightScale;
   private static VertexConsumer vertexConsumer;
   private static Matrix4f positionMatrix;
   private static final float[] vertexConsumerTable = new float[21];
   private static final float[] table = new float[21];

   static {
      for (int i = 0; i <= 20; i++) {
         double angle = (Math.PI * 2) * i / 20.0;
         table[i] = (float)Math.cos(angle);
         vertexConsumerTable[i] = (float)Math.sin(angle);
      }

      framebufferHeightScale = 0.6F;
   }

   public static void updatePositionMatrix() {
      CorzRoundedUtils.updateSamplerView();
      positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
      framebufferHeightScale = Math.max(0.35F, CorzRoundedUtils.getFramebufferHeight() * 1.2F);
      vertexConsumer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLES);
   }

   static void handleAmountsMax(float vertexConsumerValue, float vertexConsumerValue2, float vertexConsumerFactor, float vertexConsumerBound, int vertexConsumerValue3) {
      int partNeckValue = vertexConsumerValue3 & 16777215;
      float vertexConsumerFactor2 = vertexConsumerFactor + Math.max(vertexConsumerBound, framebufferHeightScale);

      for (int i = 0; i < 20; i++) {
         int index = i + 1;
         float vertexColorValue = vertexConsumerValue + table[i] * vertexConsumerFactor;
         float vertexConsumerValue6 = vertexConsumerValue2 + vertexConsumerTable[i] * vertexConsumerFactor;
         float vertexConsumerValue7 = vertexConsumerValue + table[index] * vertexConsumerFactor;
         float vertexConsumerValue8 = vertexConsumerValue2 + vertexConsumerTable[index] * vertexConsumerFactor;
         handleVertexColor(vertexConsumerValue, vertexConsumerValue2, vertexConsumerValue3, vertexColorValue, vertexConsumerValue6, vertexConsumerValue3, vertexConsumerValue7, vertexConsumerValue8, vertexConsumerValue3);
         float vertexConsumerValue9 = vertexConsumerValue + table[i] * vertexConsumerFactor2;
         float vertexConsumerValue10 = vertexConsumerValue2 + vertexConsumerTable[i] * vertexConsumerFactor2;
         float x = vertexConsumerValue + table[index] * vertexConsumerFactor2;
         float vertexConsumerValue12 = vertexConsumerValue2 + vertexConsumerTable[index] * vertexConsumerFactor2;
         handleVertexColor(vertexColorValue, vertexConsumerValue6, vertexConsumerValue3, vertexConsumerValue9, vertexConsumerValue10, partNeckValue, x, vertexConsumerValue12, partNeckValue);
         handleVertexColor(vertexColorValue, vertexConsumerValue6, vertexConsumerValue3, x, vertexConsumerValue12, partNeckValue, vertexConsumerValue7, vertexConsumerValue8, vertexConsumerValue3);
      }
   }

   public static int getValue(int vertexConsumerValue, float vertexConsumerBound) {
      return CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(vertexConsumerValue, vertexConsumerBound));
   }

   public static void handleVertexColor(float vertexColorValue, float vertexColorValue2, int vertexColorValue3, float vertexColorValue4, float vertexColorValue5, int partNeckValue, float x, float vertexColorValue8, int vertexColorValue9) {
      vertexConsumer.vertex(positionMatrix, vertexColorValue, vertexColorValue2, 0.0F).color(vertexColorValue3);
      vertexConsumer.vertex(positionMatrix, vertexColorValue4, vertexColorValue5, 0.0F).color(partNeckValue);
      vertexConsumer.vertex(positionMatrix, x, vertexColorValue8, 0.0F).color(vertexColorValue9);
   }

   static void handleAmounts(float vertexConsumerValue, float vertexConsumerValue2, float vertexConsumerFactor, float vertexConsumerFactor2, int vertexConsumerValue3, int partNeckValue) {
      int vertexConsumerValue5 = vertexConsumerValue3 & 16777215;

      for (int i = 0; i < 20; i++) {
         float vertexConsumerValue6 = vertexConsumerValue + table[i] * vertexConsumerFactor * 0.32F;
         float vertexConsumerValue7 = vertexConsumerValue2 + vertexConsumerTable[i] * vertexConsumerFactor2 * 0.32F;
         float x = vertexConsumerValue + table[i + 1] * vertexConsumerFactor * 0.32F;
         float vertexConsumerValue9 = vertexConsumerValue2 + vertexConsumerTable[i + 1] * vertexConsumerFactor2 * 0.32F;
         handleVertexColor(vertexConsumerValue, vertexConsumerValue2, vertexConsumerValue3, vertexConsumerValue6, vertexConsumerValue7, partNeckValue, x, vertexConsumerValue9, partNeckValue);
         float vertexColorValue = vertexConsumerValue + table[i] * vertexConsumerFactor;
         float vertexConsumerValue11 = vertexConsumerValue2 + vertexConsumerTable[i] * vertexConsumerFactor2;
         float vertexConsumerValue12 = vertexConsumerValue + table[i + 1] * vertexConsumerFactor;
         float vertexConsumerValue13 = vertexConsumerValue2 + vertexConsumerTable[i + 1] * vertexConsumerFactor2;
         handleVertexColor(vertexConsumerValue6, vertexConsumerValue7, partNeckValue, vertexColorValue, vertexConsumerValue11, vertexConsumerValue5, vertexConsumerValue12, vertexConsumerValue13, vertexConsumerValue5);
         handleVertexColor(vertexConsumerValue6, vertexConsumerValue7, partNeckValue, vertexConsumerValue12, vertexConsumerValue13, vertexConsumerValue5, x, vertexConsumerValue9, partNeckValue);
      }
   }

   private VertexConsumerUtilsAmounts() {
   }

   private static void handle(float vertexConsumerValue, float vertexConsumerValue2, float vertexConsumerValue3, float vertexConsumerValue4, float vertexConsumerFactor, float vertexConsumerFactor2, float vertexConsumerFactor3, float vertexConsumerFactor4, int vertexConsumerValue5, int vertexConsumerValue6, int partNeckValue, int vertexConsumerValue8) {
      float vertexConsumerValue9 = vertexConsumerValue + vertexConsumerFactor * vertexConsumerFactor3;
      float vertexConsumerValue10 = vertexConsumerValue2 + vertexConsumerFactor2 * vertexConsumerFactor3;
      float vertexColorValue = vertexConsumerValue3 + vertexConsumerFactor * vertexConsumerFactor3;
      float vertexConsumerValue12 = vertexConsumerValue4 + vertexConsumerFactor2 * vertexConsumerFactor3;
      float x = vertexConsumerValue3 + vertexConsumerFactor * vertexConsumerFactor4;
      float vertexConsumerValue14 = vertexConsumerValue4 + vertexConsumerFactor2 * vertexConsumerFactor4;
      float vertexConsumerValue15 = vertexConsumerValue + vertexConsumerFactor * vertexConsumerFactor4;
      float vertexConsumerValue16 = vertexConsumerValue2 + vertexConsumerFactor2 * vertexConsumerFactor4;
      handleVertexColor(vertexConsumerValue9, vertexConsumerValue10, vertexConsumerValue5, vertexColorValue, vertexConsumerValue12, vertexConsumerValue6, x, vertexConsumerValue14, vertexConsumerValue8);
      handleVertexColor(vertexConsumerValue9, vertexConsumerValue10, vertexConsumerValue5, x, vertexConsumerValue14, vertexConsumerValue8, vertexConsumerValue15, vertexConsumerValue16, partNeckValue);
   }

   static void handle(float vertexConsumerValue, float vertexConsumerValue2, float vertexColorValue, float vertexConsumerValue4, int partNeckValue, int vertexConsumerValue6) {
      handleVertexColor(vertexConsumerValue, vertexConsumerValue2, partNeckValue, vertexColorValue, vertexConsumerValue2, partNeckValue, vertexColorValue, vertexConsumerValue4, vertexConsumerValue6);
      handleVertexColor(vertexConsumerValue, vertexConsumerValue2, partNeckValue, vertexColorValue, vertexConsumerValue4, vertexConsumerValue6, vertexConsumerValue, vertexConsumerValue4, vertexConsumerValue6);
   }

   static int getValueForIntFloat(int vertexConsumerValue, float vertexConsumerBound) {
      int round = Math.round(255.0F * Math.max(0.0F, Math.min(1.0F, vertexConsumerBound)));
      return round << 24 | vertexConsumerValue & 16777215;
   }

   public static void handleMax(float vertexConsumerValue, float vertexConsumerValue2, float vertexConsumerValue3, float vertexConsumerValue4, float vertexConsumerValue5, int partNeckValue, int vertexConsumerValue7) {
      float vertexConsumerFactor = vertexConsumerValue3 - vertexConsumerValue;
      float vertexConsumerFactor2 = vertexConsumerValue4 - vertexConsumerValue2;
      float distance = (float)Math.sqrt(vertexConsumerFactor * vertexConsumerFactor + vertexConsumerFactor2 * vertexConsumerFactor2);
      if (!(distance < 0.001F)) {
         float vertexConsumerValue8 = -vertexConsumerFactor2 / distance;
         float vertexConsumerValue9 = vertexConsumerFactor / distance;
         int vertexConsumerValue10 = partNeckValue & 16777215;
         int vertexConsumerValue11 = vertexConsumerValue7 & 16777215;
         handle(vertexConsumerValue, vertexConsumerValue2, vertexConsumerValue3, vertexConsumerValue4, vertexConsumerValue8, vertexConsumerValue9, -vertexConsumerValue5 - framebufferHeightScale, -vertexConsumerValue5, vertexConsumerValue10, vertexConsumerValue11, partNeckValue, vertexConsumerValue7);
         handle(vertexConsumerValue, vertexConsumerValue2, vertexConsumerValue3, vertexConsumerValue4, vertexConsumerValue8, vertexConsumerValue9, -vertexConsumerValue5, vertexConsumerValue5, partNeckValue, vertexConsumerValue7, partNeckValue, vertexConsumerValue7);
         handle(vertexConsumerValue, vertexConsumerValue2, vertexConsumerValue3, vertexConsumerValue4, vertexConsumerValue8, vertexConsumerValue9, vertexConsumerValue5, vertexConsumerValue5 + framebufferHeightScale, partNeckValue, vertexConsumerValue7, vertexConsumerValue10, vertexConsumerValue11);
      }
   }

   public static void updateConsumerVertex() {
      VertexGuiUtils.updateBufferClose();
      vertexConsumer = null;
   }
}
