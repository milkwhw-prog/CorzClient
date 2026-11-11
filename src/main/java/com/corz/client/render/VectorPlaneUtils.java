package com.corz.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import com.corz.client.utils.FovOptionsUtils;

public final class VectorPlaneUtils {
   private static float diagonalPlaneNegY;
   private static float[] planeTable = new float[512];
   private static VertexConsumer vertexConsumer;
   private static float framebufferHeight;
   private static float planeAmount;
   private static float x;
   private static float[] vectorPlaneTable = new float[512];
   private static float[] table = new float[512];
   private static Matrix4f positionMatrix;
   private static float[] vertexTable = new float[512];
   private static float[] vertexClipY = new float[512];
   private static float vertexAmount;
   private static float[] vertexClipW = new float[512];
   private static float z;
   private static float[] edgeNormalX = new float[512];
   private static float vectorPlaneAmount;
   private static float y;
   private static float[] edgeNormalY = new float[512];
   private static int[] vertexColors = new int[512];
   private static float verticalPlaneY;
   private static float horizontalPlaneZ;

   public static void handleAmountsPlane(
      VertexConsumer buffer, Matrix4f matrix4f, Camera camera, double[] planeValues, int[] planeValues2, int planeLimit, float planeValue, float planeBound, boolean planeFlag, boolean planeFlag2
   ) {
      if (planeLimit >= 2 && !(planeValue <= 0.0F)) {
         handleAmounts(planeLimit + 2);
         Vector3fc horizontalPlane = camera.getHorizontalPlane();
         Vector3fc verticalPlane = camera.getVerticalPlane();
         Vector3fc diagonalPlane = camera.getDiagonalPlane();
         vectorPlaneAmount = horizontalPlane.x();
         y = horizontalPlane.y();
         horizontalPlaneZ = horizontalPlane.z();
         x = verticalPlane.x();
         verticalPlaneY = verticalPlane.y();
         z = verticalPlane.z();
         vertexAmount = -diagonalPlane.x();
         diagonalPlaneNegY = -diagonalPlane.y();
         planeAmount = -diagonalPlane.z();
         framebufferHeight = getFramebufferHeight();
         vertexConsumer = buffer;
         positionMatrix = matrix4f;
         Vec3d cameraPos = camera.getCameraPos();

         for (int i = 0; i < planeLimit; i++) {
            float planeFactor = (float)(planeValues[i * 3] - cameraPos.x);
            float planeFactor2 = (float)(planeValues[i * 3 + 1] - cameraPos.y);
            float planeFactor3 = (float)(planeValues[i * 3 + 2] - cameraPos.z);
            planeTable[i] = planeFactor * vertexAmount + planeFactor2 * diagonalPlaneNegY + planeFactor3 * planeAmount;
            vectorPlaneTable[i] = planeFactor * x + planeFactor2 * verticalPlaneY + planeFactor3 * z;
            table[i] = planeFactor * vectorPlaneAmount + planeFactor2 * y + planeFactor3 * horizontalPlaneZ;
         }

         float planeDivisor = Math.max(0.5F, planeBound);
         float max2 = Math.max(0.0F, (planeValue - planeDivisor) * 0.5F);
         float vectorPlaneValue = (planeValue + planeDivisor) * 0.5F;
         float vectorPlaneLimit = Math.min(1.0F, planeValue / planeDivisor);
         int planeCounter = 0;
         boolean vectorPlaneFlag = planeFlag && table[0] >= 0.1F;

         for (int j = 0; j < planeLimit; j++) {
            boolean planeFlag4 = table[j] >= 0.1F;
            if (j > 0 && planeFlag4 != table[j - 1] >= 0.1F) {
               float vectorPlaneFactor = (0.1F - table[j - 1]) / (table[j] - table[j - 1]);
               handleAmounts(
                  planeCounter++,
                  planeTable[j - 1] + (planeTable[j] - planeTable[j - 1]) * vectorPlaneFactor,
                  vectorPlaneTable[j - 1] + (vectorPlaneTable[j] - vectorPlaneTable[j - 1]) * vectorPlaneFactor,
                  0.1F,
                  getValue(planeValues2[j - 1], planeValues2[j], vectorPlaneFactor),
                  vectorPlaneLimit
               );
               if (!planeFlag4) {
                  handleAmountsAmount(planeCounter, vectorPlaneFlag, false, max2, vectorPlaneValue);
                  planeCounter = 0;
                  vectorPlaneFlag = false;
               }
            }

            if (planeFlag4) {
               handleAmounts(planeCounter++, planeTable[j], vectorPlaneTable[j], table[j], planeValues2[j], vectorPlaneLimit);
            }
         }

         handleAmountsAmount(planeCounter, vectorPlaneFlag, planeFlag2, max2, vectorPlaneValue);
         vertexConsumer = null;
         positionMatrix = null;
      }
   }

   private static void handleAmountsAmount(int vectorPlaneLimit, boolean vectorPlaneFlag, boolean vectorPlaneFlag2, float vectorPlaneLimit2, float vectorPlaneValue) {
      if (vectorPlaneLimit >= 2) {
         float vectorPlaneFactor = 1.0F;
         float vectorPlaneFactor2 = 0.0F;
         int index = -1;

         for (int i = 0; i < vectorPlaneLimit; i++) {
            int max2 = Math.max(i - 1, 0);
            int min3 = Math.min(i + 1, vectorPlaneLimit - 1);
            float vectorPlaneFactor3 = (vertexTable[min3] / vertexClipW[min3] - vertexTable[max2] / vertexClipW[max2]) * framebufferHeight;
            float vectorPlaneFactor4 = (vertexClipY[min3] / vertexClipW[min3] - vertexClipY[max2] / vertexClipW[max2]) * framebufferHeight;
            float distance = (float)Math.sqrt(vectorPlaneFactor3 * vectorPlaneFactor3 + vectorPlaneFactor4 * vectorPlaneFactor4);
            if (distance > 0.05F) {
               float vectorPlaneFactor5 = -vectorPlaneFactor4 / distance;
               float vectorPlaneFactor6 = vectorPlaneFactor3 / distance;
               if (index >= 0 && vectorPlaneFactor5 * vectorPlaneFactor + vectorPlaneFactor6 * vectorPlaneFactor2 < 0.0F) {
                  vectorPlaneFactor5 = -vectorPlaneFactor5;
                  vectorPlaneFactor6 = -vectorPlaneFactor6;
               }

               vectorPlaneFactor = vectorPlaneFactor5;
               vectorPlaneFactor2 = vectorPlaneFactor6;
               if (index < 0) {
                  index = i;
               }
            }

            edgeNormalX[i] = vectorPlaneFactor;
            edgeNormalY[i] = vectorPlaneFactor2;
         }

         for (int j = 0; j < index; j++) {
            edgeNormalX[j] = edgeNormalX[index];
            edgeNormalY[j] = edgeNormalY[index];
         }

         for (int k = 0; k < vectorPlaneLimit - 1; k++) {
            int vectorPlaneValue2 = vertexColors[k];
            int vectorPlaneValue3 = vertexColors[k + 1];
            if ((vectorPlaneValue2 | vectorPlaneValue3) >>> 24 != 0) {
               int vertexValue = vectorPlaneValue2 & 16777215;
               int vectorPlaneValue5 = vectorPlaneValue3 & 16777215;
               if (vectorPlaneLimit2 > 0.0F) {
                  handleAmounts(k, -vectorPlaneLimit2, vectorPlaneValue2, vectorPlaneLimit2, vectorPlaneValue2, vectorPlaneLimit2, vectorPlaneValue3, -vectorPlaneLimit2, vectorPlaneValue3);
               }

               handleAmounts(k, vectorPlaneLimit2, vectorPlaneValue2, vectorPlaneValue, vertexValue, vectorPlaneValue, vectorPlaneValue5, vectorPlaneLimit2, vectorPlaneValue3);
               handleAmounts(k, -vectorPlaneLimit2, vectorPlaneValue2, -vectorPlaneValue, vertexValue, -vectorPlaneValue, vectorPlaneValue5, -vectorPlaneLimit2, vectorPlaneValue3);
            }
         }

         if (vectorPlaneFlag) {
            handleAmountsAmount(0, 1, vectorPlaneLimit2, vectorPlaneValue);
         }

         if (vectorPlaneFlag2) {
            handleAmountsAmount(vectorPlaneLimit - 1, vectorPlaneLimit - 2, vectorPlaneLimit2, vectorPlaneValue);
         }
      }
   }

   private VectorPlaneUtils() {
   }

   public static int getValue(double[] vectorPlaneValues, int vectorPlaneValue, double vectorPlaneValue2, double vectorPlaneBound, int vectorPlaneDivisor, double[] vectorPlaneValues2) {
      int vectorPlaneCounter = 0;
      double vectorPlaneFactor = 1.0 / vectorPlaneDivisor;
      double hashRegistryValue = vectorPlaneValue2;

      while (true) {
         int max3 = Math.max(0, Math.min((int)Math.floor(hashRegistryValue), vectorPlaneValue - 2));
         double deltaVar = hashRegistryValue - max3;
         double vectorPlaneFactor2 = deltaVar * deltaVar;
         double vectorPlaneFactor3 = vectorPlaneFactor2 * deltaVar;
         int vectorPlaneValue4 = Math.max(max3 - 1, 0) * 3;
         int vectorPlaneValue5 = max3 * 3;
         int vectorPlaneValue6 = (max3 + 1) * 3;
         int vectorPlaneValue7 = Math.min(max3 + 2, vectorPlaneValue - 1) * 3;

         for (int i = 0; i < 3; i++) {
            vectorPlaneValues2[vectorPlaneCounter++] = 0.5
               * (
                  2.0 * vectorPlaneValues[vectorPlaneValue5 + i]
                     + (vectorPlaneValues[vectorPlaneValue6 + i] - vectorPlaneValues[vectorPlaneValue4 + i]) * deltaVar
                     + (2.0 * vectorPlaneValues[vectorPlaneValue4 + i] - 5.0 * vectorPlaneValues[vectorPlaneValue5 + i] + 4.0 * vectorPlaneValues[vectorPlaneValue6 + i] - vectorPlaneValues[vectorPlaneValue7 + i]) * vectorPlaneFactor2
                     + (3.0 * vectorPlaneValues[vectorPlaneValue5 + i] - vectorPlaneValues[vectorPlaneValue4 + i] - 3.0 * vectorPlaneValues[vectorPlaneValue6 + i] + vectorPlaneValues[vectorPlaneValue7 + i]) * vectorPlaneFactor3
               );
         }

         if (hashRegistryValue >= vectorPlaneBound) {
            return vectorPlaneCounter / 3;
         }

         hashRegistryValue = Math.min(vectorPlaneBound, (Math.floor(hashRegistryValue * vectorPlaneDivisor + 1.0E-6) + 1.0) * vectorPlaneFactor);
      }
   }

   private static int getValue(int vectorPlaneValue, int vectorPlaneValue2, float vectorPlaneFactor) {
      int vectorPlaneValue3 = vectorPlaneValue >>> 24;
      int vectorPlaneValue4 = vectorPlaneValue >> 16 & 0xFF;
      int vectorPlaneValue5 = vectorPlaneValue >> 8 & 0xFF;
      int vectorPlaneValue6 = vectorPlaneValue & 0xFF;
      return (int)(vectorPlaneValue3 + ((vectorPlaneValue2 >>> 24) - vectorPlaneValue3) * vectorPlaneFactor) << 24
         | (int)(vectorPlaneValue4 + ((vectorPlaneValue2 >> 16 & 0xFF) - vectorPlaneValue4) * vectorPlaneFactor) << 16
         | (int)(vectorPlaneValue5 + ((vectorPlaneValue2 >> 8 & 0xFF) - vectorPlaneValue5) * vectorPlaneFactor) << 8
         | (int)(vectorPlaneValue6 + ((vectorPlaneValue2 & 0xFF) - vectorPlaneValue6) * vectorPlaneFactor);
   }

   public static float getFramebufferHeight() {
      return MinecraftClient.getInstance().getWindow().getFramebufferHeight() * 0.5F / (float)Math.tan(Math.toRadians(FovOptionsUtils.getValue()) * 0.5);
   }

   private static void handleAmountsVertex(int index, float vertexFactor, float vertexFactor2, int vertexValue) {
      float vertexFactor3 = vertexClipW[index] / framebufferHeight;
      float vertexFactor4 = vertexTable[index] + vertexFactor * vertexFactor3;
      float vertexFactor5 = vertexClipY[index] + vertexFactor2 * vertexFactor3;
      float vertexFactor6 = vertexClipW[index];
      vertexConsumer.vertex(positionMatrix, vertexAmount * vertexFactor4 + x * vertexFactor5 + vectorPlaneAmount * vertexFactor6, diagonalPlaneNegY * vertexFactor4 + verticalPlaneY * vertexFactor5 + y * vertexFactor6, planeAmount * vertexFactor4 + z * vertexFactor5 + horizontalPlaneZ * vertexFactor6)
         .color(vertexValue);
   }

   private static void handleAmounts(int index, float vectorPlaneValue, float vectorPlaneValue2, float vectorPlaneValue3, int vectorPlaneValue4, float vectorPlaneLimit) {
      vertexTable[index] = vectorPlaneValue;
      vertexClipY[index] = vectorPlaneValue2;
      vertexClipW[index] = vectorPlaneValue3;
      vertexColors[index] = vectorPlaneLimit >= 1.0F ? vectorPlaneValue4 : (int)((vectorPlaneValue4 >>> 24) * vectorPlaneLimit) << 24 | vectorPlaneValue4 & 16777215;
   }

   private static void handleAmountsAmount(int index, int index2, float vectorPlaneFactor, float vectorPlaneFactor2) {
      int vectorPlaneValue = vertexColors[index];
      int vertexValue = vectorPlaneValue & 16777215;
      if (vectorPlaneValue >>> 24 != 0) {
         float vectorPlaneFactor3 = (vertexTable[index] / vertexClipW[index] - vertexTable[index2] / vertexClipW[index2]) * framebufferHeight;
         float vectorPlaneFactor4 = (vertexClipY[index] / vertexClipW[index] - vertexClipY[index2] / vertexClipW[index2]) * framebufferHeight;
         float distance = (float)Math.sqrt(vectorPlaneFactor3 * vectorPlaneFactor3 + vectorPlaneFactor4 * vectorPlaneFactor4);
         float vectorPlaneFactor5;
         float vectorPlaneFactor6;
         if (distance > 0.05F) {
            vectorPlaneFactor5 = vectorPlaneFactor3 / distance;
            vectorPlaneFactor6 = vectorPlaneFactor4 / distance;
         } else {
            vectorPlaneFactor5 = edgeNormalY[index];
            vectorPlaneFactor6 = -edgeNormalX[index];
         }

         float vectorPlaneFactor7 = edgeNormalX[index];
         float vectorPlaneFactor8 = edgeNormalY[index];

         for (int j = 1; j <= 6; j++) {
            double angle = Math.PI * j / 6.0;
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float vectorPlaneFactor9 = edgeNormalX[index] * cos + vectorPlaneFactor5 * sin;
            float vectorPlaneFactor10 = edgeNormalY[index] * cos + vectorPlaneFactor6 * sin;
            if (vectorPlaneFactor > 0.0F) {
               VertexBuilderUtils.handleParticleCorz(4);
               handleAmountsVertex(index, 0.0F, 0.0F, vectorPlaneValue);
               handleAmountsVertex(index, 0.0F, 0.0F, vectorPlaneValue);
               handleAmountsVertex(index, vectorPlaneFactor7 * vectorPlaneFactor, vectorPlaneFactor8 * vectorPlaneFactor, vectorPlaneValue);
               handleAmountsVertex(index, vectorPlaneFactor9 * vectorPlaneFactor, vectorPlaneFactor10 * vectorPlaneFactor, vectorPlaneValue);
            }

            VertexBuilderUtils.handleParticleCorz(4);
            handleAmountsVertex(index, vectorPlaneFactor7 * vectorPlaneFactor, vectorPlaneFactor8 * vectorPlaneFactor, vectorPlaneValue);
            handleAmountsVertex(index, vectorPlaneFactor7 * vectorPlaneFactor2, vectorPlaneFactor8 * vectorPlaneFactor2, vertexValue);
            handleAmountsVertex(index, vectorPlaneFactor9 * vectorPlaneFactor2, vectorPlaneFactor10 * vectorPlaneFactor2, vertexValue);
            handleAmountsVertex(index, vectorPlaneFactor9 * vectorPlaneFactor, vectorPlaneFactor10 * vectorPlaneFactor, vectorPlaneValue);
            vectorPlaneFactor7 = vectorPlaneFactor9;
            vectorPlaneFactor8 = vectorPlaneFactor10;
         }
      }
   }

   private static void handleAmounts(int vectorPlaneLimit) {
      if (planeTable.length < vectorPlaneLimit) {
         int vectorPlaneSize = Math.max(vectorPlaneLimit, planeTable.length * 2);
         planeTable = new float[vectorPlaneSize];
         vectorPlaneTable = new float[vectorPlaneSize];
         table = new float[vectorPlaneSize];
         vertexTable = new float[vectorPlaneSize];
         vertexClipY = new float[vectorPlaneSize];
         vertexClipW = new float[vectorPlaneSize];
         edgeNormalX = new float[vectorPlaneSize];
         edgeNormalY = new float[vectorPlaneSize];
         vertexColors = new int[vectorPlaneSize];
      }
   }

   private static void handleAmounts(int index, float vectorPlaneFactor, int vectorPlaneValue, float vectorPlaneFactor2, int vertexValue, float vectorPlaneFactor3, int vectorPlaneValue3, float vectorPlaneFactor4, int vectorPlaneValue4) {
      VertexBuilderUtils.handleParticleCorz(4);
      handleAmountsVertex(index, edgeNormalX[index] * vectorPlaneFactor, edgeNormalY[index] * vectorPlaneFactor, vectorPlaneValue);
      handleAmountsVertex(index, edgeNormalX[index] * vectorPlaneFactor2, edgeNormalY[index] * vectorPlaneFactor2, vertexValue);
      handleAmountsVertex(index + 1, edgeNormalX[index + 1] * vectorPlaneFactor3, edgeNormalY[index + 1] * vectorPlaneFactor3, vectorPlaneValue3);
      handleAmountsVertex(index + 1, edgeNormalX[index + 1] * vectorPlaneFactor4, edgeNormalY[index + 1] * vectorPlaneFactor4, vectorPlaneValue4);
   }
}
