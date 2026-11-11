package com.corz.client.render;

import com.corz.client.gui.VertexGuiUtils;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public final class VertexConsumerUtilsAmount {

   public static void handle(Matrix4f matrix, float vertexConsumerValue, float randomValue, float vertexConsumerBound, float hashHuntBound, float wrapDegreesBound, float vertexConsumerFactor, float vertexConsumerValue3, int vertexConsumerValue4) {
      if (!(vertexConsumerValue3 < 0.01F) && !(vertexConsumerFactor <= 5.0E-4F)) {
         float vertexConsumerValue5 = 0.5F * CorzRoundedUtils.getFramebufferHeight();
         float vertexConsumerValue6 = vertexConsumerValue - vertexConsumerValue5;
         float vertexConsumerValue7 = randomValue - vertexConsumerValue5;
         float vertexConsumerValue8 = vertexConsumerValue + vertexConsumerBound + vertexConsumerValue5;
         float vertexConsumerValue9 = randomValue + hashHuntBound + vertexConsumerValue5;
         float vertexConsumerValue10 = Math.min(wrapDegreesBound, Math.min(vertexConsumerBound, hashHuntBound) * 0.5F) + vertexConsumerValue5;
         float vertexConsumerValue11 = (vertexConsumerValue6 + vertexConsumerValue8) * 0.5F;
         ArrayList arrayList = new ArrayList();
         handle(arrayList, vertexConsumerValue11, vertexConsumerValue9, vertexConsumerValue8 - vertexConsumerValue10, vertexConsumerValue9, 0.0F, 1.0F);
         handle(arrayList, vertexConsumerValue8 - vertexConsumerValue10, vertexConsumerValue9 - vertexConsumerValue10, vertexConsumerValue10, 90.0F, 0.0F);
         handle(arrayList, vertexConsumerValue8, vertexConsumerValue9 - vertexConsumerValue10, vertexConsumerValue8, vertexConsumerValue7 + vertexConsumerValue10, 1.0F, 0.0F);
         handle(arrayList, vertexConsumerValue8 - vertexConsumerValue10, vertexConsumerValue7 + vertexConsumerValue10, vertexConsumerValue10, 0.0F, -90.0F);
         handle(arrayList, vertexConsumerValue8 - vertexConsumerValue10, vertexConsumerValue7, vertexConsumerValue11, vertexConsumerValue7, 0.0F, -1.0F);
         handle(arrayList, vertexConsumerValue11, vertexConsumerValue7, vertexConsumerValue6 + vertexConsumerValue10, vertexConsumerValue7, 0.0F, -1.0F);
         handle(arrayList, vertexConsumerValue6 + vertexConsumerValue10, vertexConsumerValue7 + vertexConsumerValue10, vertexConsumerValue10, -90.0F, -180.0F);
         handle(arrayList, vertexConsumerValue6, vertexConsumerValue7 + vertexConsumerValue10, vertexConsumerValue6, vertexConsumerValue9 - vertexConsumerValue10, -1.0F, 0.0F);
         handle(arrayList, vertexConsumerValue6 + vertexConsumerValue10, vertexConsumerValue9 - vertexConsumerValue10, vertexConsumerValue10, 180.0F, 90.0F);
         handle(arrayList, vertexConsumerValue6 + vertexConsumerValue10, vertexConsumerValue9, vertexConsumerValue11, vertexConsumerValue9, 0.0F, 1.0F);
         int varSize = arrayList.size();
         if (varSize >= 8) {
            int vertexConsumerLimit = Math.round(vertexConsumerFactor * (varSize / 2.0F));
            if (vertexConsumerLimit >= 1) {
               if (vertexConsumerLimit > varSize / 2) {
                  vertexConsumerLimit = varSize / 2;
               }

               int vertexConsumerValue12 = vertexConsumerValue4 >> 16 & 0xFF;
               int vertexConsumerValue13 = vertexConsumerValue4 >> 8 & 0xFF;
               int vertexConsumerValue14 = vertexConsumerValue4 & 0xFF;
               handle(matrix, arrayList, varSize, vertexConsumerLimit, vertexConsumerFactor, 4.0F, 0.22F * vertexConsumerValue3, vertexConsumerValue12, vertexConsumerValue13, vertexConsumerValue14);
               handle(matrix, arrayList, varSize, vertexConsumerLimit, vertexConsumerFactor, 2.2F, 0.45F * vertexConsumerValue3, vertexConsumerValue12, vertexConsumerValue13, vertexConsumerValue14);
               handle(matrix, arrayList, varSize, vertexConsumerLimit, vertexConsumerFactor, 0.9F, 0.95F * vertexConsumerValue3, vertexConsumerValue12, vertexConsumerValue13, vertexConsumerValue14);
               handle(
                  matrix, arrayList, varSize, vertexConsumerLimit, vertexConsumerFactor, 0.55F, 0.55F * vertexConsumerValue3, getValue(vertexConsumerValue12, 255, 0.6F), getValue(vertexConsumerValue13, 255, 0.6F), getValue(vertexConsumerValue14, 255, 0.6F)
               );
               if (vertexConsumerFactor < 0.995F) {
                  float[] vertexConsumerValues = (float[])arrayList.get(vertexConsumerLimit % varSize);
                  float[] vertexConsumerValues2 = (float[])arrayList.get((-vertexConsumerLimit % varSize + varSize) % varSize);
                  handleVertexColor(matrix, vertexConsumerValues[0], vertexConsumerValues[1], 5.4F, 0.5F * vertexConsumerValue3, vertexConsumerValue12, vertexConsumerValue13, vertexConsumerValue14);
                  handleVertexColor(matrix, vertexConsumerValues2[0], vertexConsumerValues2[1], 5.4F, 0.5F * vertexConsumerValue3, vertexConsumerValue12, vertexConsumerValue13, vertexConsumerValue14);
               }
            }
         }
      }
   }

   private VertexConsumerUtilsAmount() {
   }

   private static float getAmount(float vertexConsumerBound, float pilotStatusBound, float wrapDegreesBound) {
      return Math.max(pilotStatusBound, Math.min(wrapDegreesBound, vertexConsumerBound));
   }

   private static int getValue(int vertexConsumerValue, int vertexConsumerValue2, float vertexConsumerFactor) {
      return getValue((int)(vertexConsumerValue + (vertexConsumerValue2 - vertexConsumerValue) * vertexConsumerFactor), 0, 255);
   }

   private static float getAmount(float vertexConsumerLimit, float vertexConsumerFactor) {
      float vertexConsumerDivisor = 0.12F;
      float vertexConsumerValue = vertexConsumerLimit < vertexConsumerDivisor ? vertexConsumerLimit / vertexConsumerDivisor : (vertexConsumerLimit > 1.0F - vertexConsumerDivisor ? (1.0F - vertexConsumerLimit) / vertexConsumerDivisor : 1.0F);
      float vertexConsumerFactor2 = getAmount(vertexConsumerValue, 0.0F, 1.0F);
      float vertexConsumerValue2 = vertexConsumerFactor2 * vertexConsumerFactor2 * (3.0F - 2.0F * vertexConsumerFactor2);
      return vertexConsumerValue2 + (1.0F - vertexConsumerValue2) * vertexConsumerFactor;
   }

   private static void handleVertexColor(Matrix4f matrix, float vertexColorValue, float vertexColorValue2, float vertexColorFactor, float vertexColorValue3, int vertexColorValue4, int vertexColorValue5, int vertexConsumerValue) {
      if (!(vertexColorValue3 < 0.01F)) {
         byte vertexColorLimit = 20;
         int vertexColorValue7 = getValue((int)(vertexColorValue3 * 255.0F), 0, 255);
         VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLE_STRIP);

         for (int i = 0; i <= vertexColorLimit; i++) {
            double angle = (Math.PI * 2) * i / vertexColorLimit;
            beginBuffer.vertex(matrix, vertexColorValue, vertexColorValue2, 0.0F).color(vertexColorValue4, vertexColorValue5, vertexConsumerValue, vertexColorValue7);
            beginBuffer.vertex(matrix, vertexColorValue + (float)Math.cos(angle) * vertexColorFactor, vertexColorValue2 + (float)Math.sin(angle) * vertexColorFactor, 0.0F).color(vertexColorValue4, vertexColorValue5, vertexConsumerValue, 0);
         }

         VertexGuiUtils.updateBufferClose();
      }
   }

   private static void handle(ArrayList<float[]> arrayList, float vertexConsumerValue, float vertexConsumerValue2, float vertexConsumerFactor, float vertexConsumerValue3, float vertexConsumerValue4) {
      int maxRound = Math.max(1, Math.round((float)(Math.abs(vertexConsumerValue4 - vertexConsumerValue3) * Math.PI / 180.0 * vertexConsumerFactor) / 2.5F));

      for (int i = 0; i < maxRound; i++) {
         double angle = Math.toRadians(vertexConsumerValue3 + (vertexConsumerValue4 - vertexConsumerValue3) * i / maxRound);
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         arrayList.add(new float[]{vertexConsumerValue + cos * vertexConsumerFactor, vertexConsumerValue2 + sin * vertexConsumerFactor, cos, sin});
      }
   }

   private static void handleVertexColor(Matrix4f matrix, ArrayList<float[]> arrayList, int vertexColorValue, int vertexColorLimit, float vertexConsumerFactor, float vertexColorFactor, float vertexColorFactor2, int vertexColorValue3, int vertexColorValue4, int vertexColorValue5) {
      int vertexColorDivisor = 2 * vertexColorLimit;
      VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLE_STRIP);

      for (int i = -vertexColorLimit; i <= vertexColorLimit; i++) {
         int vertexColorValue6 = (i % vertexColorValue + vertexColorValue) % vertexColorValue;
         float vertexConsumerLimit = (float)(i + vertexColorLimit) / vertexColorDivisor;
         float vertexColorValue8 = vertexColorFactor2 * getAmount(vertexConsumerLimit, vertexConsumerFactor);
         int vertexColorValue9 = getValue((int)(vertexColorValue8 * 255.0F), 0, 255);
         float[] vertexColorValues = (float[])arrayList.get(vertexColorValue6);
         beginBuffer.vertex(matrix, vertexColorValues[0], vertexColorValues[1], 0.0F).color(vertexColorValue3, vertexColorValue4, vertexColorValue5, vertexColorValue9);
         beginBuffer.vertex(matrix, vertexColorValues[0] + vertexColorValues[2] * vertexColorFactor, vertexColorValues[1] + vertexColorValues[3] * vertexColorFactor, 0.0F).color(vertexColorValue3, vertexColorValue4, vertexColorValue5, 0);
      }

      VertexGuiUtils.updateBufferClose();
   }

   private static void handle(ArrayList<float[]> arrayList, float vertexConsumerValue, float vertexConsumerValue2, float vertexConsumerValue3, float vertexConsumerValue4, float vertexConsumerValue5, float vertexConsumerValue6) {
      float vertexConsumerFactor = vertexConsumerValue3 - vertexConsumerValue;
      float vertexConsumerFactor2 = vertexConsumerValue4 - vertexConsumerValue2;
      int maxRound = Math.max(1, Math.round((float)Math.sqrt(vertexConsumerFactor * vertexConsumerFactor + vertexConsumerFactor2 * vertexConsumerFactor2) / 2.5F));

      for (int i = 0; i < maxRound; i++) {
         float vertexConsumerFactor3 = (float)i / maxRound;
         arrayList.add(new float[]{vertexConsumerValue + vertexConsumerFactor * vertexConsumerFactor3, vertexConsumerValue2 + vertexConsumerFactor2 * vertexConsumerFactor3, vertexConsumerValue5, vertexConsumerValue6});
      }
   }

   private static int getValue(int vertexConsumerBound, int aggroPreemptBound, int intersectsCenterBound) {
      return Math.max(aggroPreemptBound, Math.min(intersectsCenterBound, vertexConsumerBound));
   }

   private static void handle(Matrix4f matrix, ArrayList<float[]> arrayList, int vertexConsumerValue, int vertexColorLimit, float vertexConsumerFactor, float vertexColorFactor, float vertexConsumerValue5, int vertexConsumerValue6, int vertexConsumerValue7, int vertexConsumerValue8) {
      if (!(vertexConsumerValue5 <= 0.003F)) {
         handleVertexColor(matrix, arrayList, vertexConsumerValue, vertexColorLimit, vertexConsumerFactor, vertexColorFactor, vertexConsumerValue5, vertexConsumerValue6, vertexConsumerValue7, vertexConsumerValue8);
         handleVertexColor(matrix, arrayList, vertexConsumerValue, vertexColorLimit, vertexConsumerFactor, -vertexColorFactor, vertexConsumerValue5, vertexConsumerValue6, vertexConsumerValue7, vertexConsumerValue8);
      }
   }
}
