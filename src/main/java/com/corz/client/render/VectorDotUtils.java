package com.corz.client.render;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.corz.client.categories.visuals.NameTags;

public final class VectorDotUtils {
   private static int lastRotateCount;
   private static float dotBaseFactor;
   private static boolean immediateActive;
   private static final Vector3f VECTOR3_FVECTOR3F = new Vector3f(0.0F, 0.0F, -1.5F);
   private static int vectorRotateCount;
   private static final Vector3f VECTOR3F = new Vector3f(0.0F, 0.0F, -1.0F);
   private static final Vector3f VECTOR3F4 = new Vector3f(1.0F, 0.0F, 0.0F);
   private static final Vector3f VECTOR3F8 = new Vector3f(0.0F, 1.0F, 0.0F);
   private static float previousDotProduct;
   private static final Vector3f VECTOR3F10 = new Vector3f();
   private static final Vector3f VECTOR3F6 = new Vector3f();
   private static final Vector3f VECTOR3F9 = new Vector3f();
   private static final Vector3f VECTOR3F3 = new Vector3f();
   private static boolean vectorDotActive;
   private static final Vector3f VECTOR3F2 = new Vector3f();
   private static float vectorDotAmount;
   private static float vectorRotateAmount;
   private static final Vector3f VECTOR3F7 = new Vector3f();
   private static float lastDotProduct = 1.5F;
   private static boolean vectorRotateActive = true;

   public static float getX() {
      return VECTOR3_FVECTOR3F.x;
   }

   public static float getY() {
      return VECTOR3_FVECTOR3F.y;
   }

   public static void updateDrawImmediate() {
      if (immediateActive) {
         immediateActive = false;
         VectorDotUtils.Late.immediate.draw();
      }
   }

   public static void handle(double vectorDotValue, double vertexBuilderValue, double vectorDotValue3, int matrixValue, boolean vectorDotFlag) {
      handleVectorDot(
         VertexBuilderUtils.getAmountX(vectorDotValue),
         VertexBuilderUtils.getAmount(vertexBuilderValue),
         VertexBuilderUtils.getAmountForDouble(vectorDotValue3),
         MatrixAmountUtils.getValueInt(matrixValue, vectorDotFlag ? 0.65F : 0.45F),
         MatrixAmountUtils.getValueInt(matrixValue, 1.0F),
         vectorDotFlag ? 2.6F : 2.2F
      );
   }

   public static float getZ() {
      return VECTOR3_FVECTOR3F.z;
   }

   public static void handle(double vectorDotValue, double vertexBuilderValue, double vectorDotValue3, int vectorDotValue4, float vectorDotBound) {
      handleVectorDot(VertexBuilderUtils.getAmountX(vectorDotValue), VertexBuilderUtils.getAmount(vertexBuilderValue), VertexBuilderUtils.getAmountForDouble(vectorDotValue3), vectorDotValue4, vectorDotValue4, vectorDotBound);
   }

   static float getAmount(float vectorDotFactor, float vectorDotFactor2, float vectorDotFactor3) {
      if (vectorDotFactor <= 0.0F) {
         return 0.0F;
      } else if (vectorDotFactor >= 1.0F) {
         return 1.0F;
      } else {
         float vectorDotLimit = (1.0F - vectorDotFactor) * vectorDotFactor3 + vectorDotFactor * vectorDotFactor2;
         return vectorDotLimit > 1.0E-6F ? vectorDotFactor * vectorDotFactor2 / vectorDotLimit : vectorDotFactor;
      }
   }

   static boolean canVectorCross(Matrix4f matrix, float vectorCrossFactor, Vector3f vector3f) {
      VECTOR3F10.set(matrix.m00(), matrix.m10(), matrix.m20());
      VECTOR3F6.set(matrix.m01(), matrix.m11(), matrix.m21());
      VECTOR3F10.cross(VECTOR3F6, VECTOR3F7);
      float vector3fLength = VECTOR3F7.length();
      if (!(vector3fLength > 1.0E-6F)) {
         return false;
      } else {
         VECTOR3F9.set(VECTOR3F7).div(vector3fLength);
         if (VECTOR3F9.z > 0.0F) {
            VECTOR3F9.negate();
         }

         VECTOR3F6.cross(VECTOR3F9, VECTOR3F3);
         VECTOR3F9.cross(VECTOR3F10, VECTOR3F2);
         float dot = VECTOR3F10.dot(VECTOR3F3);
         if (!(Math.abs(dot) > 1.0E-9F)) {
            return false;
         } else {
            float vectorCrossFactor2 = -matrix.m30();
            float vectorCrossFactor3 = -matrix.m31();
            vector3f.set(
               (VECTOR3F3.x * vectorCrossFactor2 + VECTOR3F2.x * vectorCrossFactor3 + VECTOR3F7.x * vectorCrossFactor) / dot,
               (VECTOR3F3.y * vectorCrossFactor2 + VECTOR3F2.y * vectorCrossFactor3 + VECTOR3F7.y * vectorCrossFactor) / dot,
               (VECTOR3F3.z * vectorCrossFactor2 + VECTOR3F2.z * vectorCrossFactor3 + VECTOR3F7.z * vectorCrossFactor) / dot
            );
            return vector3f.isFinite() && vector3f.lengthSquared() < vectorCrossFactor * vectorCrossFactor * 16.0F;
         }
      }
   }

   public static void handle(double vectorDotValue, double vertexBuilderValue, double vectorDotValue3, int rgb, int matrixValue, float vectorDotBound) {
      handleVectorDot(
         VertexBuilderUtils.getAmountX(vectorDotValue), VertexBuilderUtils.getAmount(vertexBuilderValue), VertexBuilderUtils.getAmountForDouble(vectorDotValue3), rgb, matrixValue, vectorDotBound
      );
   }

   private VectorDotUtils() {
   }

   private static float getMinAmount(float vectorDotBound) {
      if (vectorDotActive) {
         return getAmount(vectorDotBound, previousDotProduct, vectorDotAmount);
      } else {
         return vectorDotBound <= 0.18F ? vectorDotBound / 0.18F * dotBaseFactor : dotBaseFactor + (Math.min(1.0F, vectorDotBound) - 0.18F) / 0.82F * (1.0F - dotBaseFactor);
      }
   }

   private static void handleVectorDot(float vectorDotFactor, float vectorDotFactor2, float vectorDotFactor3, int vectorDotValue, int matrixValue, float vectorDotBound) {
      float amountFloat = MatrixAmountUtils.getAmountFloat(vectorDotValue);
      float rocketsElytraBound = MatrixAmountUtils.getAmountFloat(matrixValue);
      if (!(amountFloat < 0.004F) || !(rocketsElytraBound < 0.004F)) {
         float x = VECTOR3_FVECTOR3F.x;
         float y = VECTOR3_FVECTOR3F.y;
         float z = VECTOR3_FVECTOR3F.z;
         float vectorDotFactor4 = vectorDotFactor - x;
         float vectorDotFactor5 = vectorDotFactor2 - y;
         float vectorDotFactor6 = vectorDotFactor3 - z;
         float squaredDistanceTo = vectorDotFactor4 * vectorDotFactor4 + vectorDotFactor5 * vectorDotFactor5 + vectorDotFactor6 * vectorDotFactor6;
         if (!(squaredDistanceTo < 1.0E-6F)) {
            vectorRotateCount++;
            float amount4 = vectorDotFactor * VECTOR3F.x + vectorDotFactor2 * VECTOR3F.y + vectorDotFactor3 * VECTOR3F.z;
            vectorDotActive = amount4 > 0.05F && lastDotProduct > 0.05F;
            previousDotProduct = lastDotProduct;
            vectorDotAmount = amount4;
            dotBaseFactor = Math.min(0.5F, 0.2F / (float)Math.sqrt(squaredDistanceTo));
            float vectorDotValue4 = vectorDotActive ? MatrixAmountUtils.getMax((amount4 - 8.0F) / 88.0F) : 0.0F;
            float vectorDotValue5 = Math.max(1.0F, Math.min(6.0F, vectorDotBound)) * (1.2F - 0.35F * vectorDotValue4);
            float max7 = Math.max(amountFloat, rocketsElytraBound);
            Matrix4f matrix = VertexBuilderUtils.getMatrix();
            VertexConsumer buffer = getBuffer();
            float minAmount = getMinAmount(0.18F);
            float normalLineValue = x + vectorDotFactor4 * minAmount;
            float vectorDotValue7 = y + vectorDotFactor5 * minAmount;
            float vectorDotValue8 = z + vectorDotFactor6 * minAmount;
            if (lastRotateCount <= 640) {
               int valueInt = MatrixAmountUtils.getValueInt(0, 0.34F * amountFloat);
               VertexBuilderUtils.handleNormalLine(buffer, matrix, x, y, z, 0, normalLineValue, vectorDotValue7, vectorDotValue8, valueInt, vectorDotValue5 + 2.0F);
               VertexBuilderUtils.handleNormalLine(
                  buffer, matrix, normalLineValue, vectorDotValue7, vectorDotValue8, valueInt, vectorDotFactor, vectorDotFactor2, vectorDotFactor3, MatrixAmountUtils.getValueInt(0, 0.34F * rocketsElytraBound), vectorDotValue5 + 2.0F
               );
            }

            VertexBuilderUtils.handleNormalLine(buffer, matrix, x, y, z, vectorDotValue & 16777215, normalLineValue, vectorDotValue7, vectorDotValue8, vectorDotValue, vectorDotValue5);
            if (vectorDotValue == matrixValue) {
               VertexBuilderUtils.handleNormalLine(buffer, matrix, normalLineValue, vectorDotValue7, vectorDotValue8, vectorDotValue, vectorDotFactor, vectorDotFactor2, vectorDotFactor3, matrixValue, vectorDotValue5);
            } else {
               float minAmount2 = getMinAmount(0.6F);
               float vectorDotValue10 = x + vectorDotFactor4 * minAmount2;
               float vectorDotValue11 = y + vectorDotFactor5 * minAmount2;
               float vectorDotValue12 = z + vectorDotFactor6 * minAmount2;
               int vectorDotValue13 = MatrixAmountUtils.getValue(vectorDotValue, matrixValue, 0.51219517F);
               VertexBuilderUtils.handleNormalLine(buffer, matrix, normalLineValue, vectorDotValue7, vectorDotValue8, vectorDotValue, vectorDotValue10, vectorDotValue11, vectorDotValue12, vectorDotValue13, vectorDotValue5);
               VertexBuilderUtils.handleNormalLine(buffer, matrix, vectorDotValue10, vectorDotValue11, vectorDotValue12, vectorDotValue13, vectorDotFactor, vectorDotFactor2, vectorDotFactor3, matrixValue, vectorDotValue5);
            }

            if (vectorDotActive && lastRotateCount <= 96) {
               float x2 = vectorDotFactor * VECTOR3F4.x + vectorDotFactor2 * VECTOR3F4.y + vectorDotFactor3 * VECTOR3F4.z;
               float y2 = vectorDotFactor * VECTOR3F8.x + vectorDotFactor2 * VECTOR3F8.y + vectorDotFactor3 * VECTOR3F8.z;
               float hashRegistryValue = vectorRotateAmount + (float)Math.atan2(y2, x2) / (float) (Math.PI * 2);
               float vectorDotValue15 = hashRegistryValue - (float)Math.floor(hashRegistryValue);
               if (vectorDotValue15 < 0.55F) {
                  float vectorDotValue16 = vectorDotValue15 / 0.55F;
                  float vectorDotValue17 = 0.09F + 0.90999997F * (1.0F - (1.0F - vectorDotValue16) * (1.0F - vectorDotValue16));
                  float max18 = Math.max(0.0F, vectorDotValue17 - 0.14F);
                  float vectorDotFactor8 = Math.min(1.0F, vectorDotValue16 / 0.12F) * Math.min(1.0F, (1.0F - vectorDotValue16) / 0.3F);
                  int vectorDotValue19 = MatrixAmountUtils.getValue(matrixValue | 0xFF000000, -1, 0.62F) & 16777215;
                  float minAmount3 = getMinAmount(vectorDotValue17);
                  float minAmount4 = getMinAmount(max18);
                  VertexBuilderUtils.handleNormalLine(
                     buffer,
                     matrix,
                     x + vectorDotFactor4 * minAmount4,
                     y + vectorDotFactor5 * minAmount4,
                     z + vectorDotFactor6 * minAmount4,
                     vectorDotValue19,
                     x + vectorDotFactor4 * minAmount3,
                     y + vectorDotFactor5 * minAmount3,
                     z + vectorDotFactor6 * minAmount3,
                     MatrixAmountUtils.getValueInt(vectorDotValue19, max7 * vectorDotFactor8),
                     vectorDotValue5 + 1.0F
                  );
               }

               float vectorDotFactor9 = amount4 * 0.0062F * (0.8F + 0.2F * vectorDotValue5);
               float vectorDotValue20 = VECTOR3F4.x * vectorDotFactor9;
               float vectorDotValue21 = VECTOR3F4.y * vectorDotFactor9;
               float vectorDotValue22 = VECTOR3F4.z * vectorDotFactor9;
               float vectorDotValue23 = VECTOR3F8.x * vectorDotFactor9;
               float vectorDotValue24 = VECTOR3F8.y * vectorDotFactor9;
               float vectorDotValue25 = VECTOR3F8.z * vectorDotFactor9;
               int valueInt26 = MatrixAmountUtils.getValueInt(MatrixAmountUtils.getValue(matrixValue | 0xFF000000, -1, 0.25F), Math.max(rocketsElytraBound, amountFloat * 0.8F));
               float blockOverlayValue = Math.max(1.25F, vectorDotValue5 * 0.85F);
               VertexBuilderUtils.handleNormalLine(
                  buffer, matrix, vectorDotFactor + vectorDotValue23, vectorDotFactor2 + vectorDotValue24, vectorDotFactor3 + vectorDotValue25, valueInt26, vectorDotFactor + vectorDotValue20, vectorDotFactor2 + vectorDotValue21, vectorDotFactor3 + vectorDotValue22, valueInt26, blockOverlayValue
               );
               VertexBuilderUtils.handleNormalLine(
                  buffer, matrix, vectorDotFactor + vectorDotValue20, vectorDotFactor2 + vectorDotValue21, vectorDotFactor3 + vectorDotValue22, valueInt26, vectorDotFactor - vectorDotValue23, vectorDotFactor2 - vectorDotValue24, vectorDotFactor3 - vectorDotValue25, valueInt26, blockOverlayValue
               );
               VertexBuilderUtils.handleNormalLine(
                  buffer, matrix, vectorDotFactor - vectorDotValue23, vectorDotFactor2 - vectorDotValue24, vectorDotFactor3 - vectorDotValue25, valueInt26, vectorDotFactor - vectorDotValue20, vectorDotFactor2 - vectorDotValue21, vectorDotFactor3 - vectorDotValue22, valueInt26, blockOverlayValue
               );
               VertexBuilderUtils.handleNormalLine(
                  buffer, matrix, vectorDotFactor - vectorDotValue20, vectorDotFactor2 - vectorDotValue21, vectorDotFactor3 - vectorDotValue22, valueInt26, vectorDotFactor + vectorDotValue23, vectorDotFactor2 + vectorDotValue24, vectorDotFactor3 + vectorDotValue25, valueInt26, blockOverlayValue
               );
            }
         }
      }
   }

   static void handleVectorRotate(Camera camera) {
      if (immediateActive) {
         vectorRotateActive = false;
         updateDrawImmediate();
      }

      if (!canVectorCross(NameTags.MATRIX4F, 1.5F, VECTOR3_FVECTOR3F)) {
         VECTOR3_FVECTOR3F.set(0.0F, 0.0F, -1.5F);
      }

      Quaternionf rotation = camera.getRotation();
      VECTOR3_FVECTOR3F.rotate(rotation);
      VECTOR3F.set(0.0F, 0.0F, -1.0F).rotate(rotation);
      VECTOR3F4.set(1.0F, 0.0F, 0.0F).rotate(rotation);
      VECTOR3F8.set(0.0F, 1.0F, 0.0F).rotate(rotation);
      lastDotProduct = VECTOR3_FVECTOR3F.dot(VECTOR3F);
      vectorRotateAmount = (float)Math.floorMod(System.nanoTime(), 1600000000L) / 1.6E9F;
      lastRotateCount = vectorRotateCount;
      vectorRotateCount = 0;
   }

   private static VertexConsumer getBuffer() {
      if (!vectorRotateActive) {
         return VertexBuilderUtils.getBufferVertexConsumer();
      } else {
         immediateActive = true;
         return VectorDotUtils.Late.immediate.getBuffer(VectorDotUtils.Late.renderLayer);
      }
   }

   private static final class Late {
      static final RenderLayer renderLayer = RenderLayer.of("corz_tracers", RenderSetup.builder(VertexBuilderUtils.build).build());
      static final Immediate immediate = VertexConsumerProvider.immediate(new BufferAllocator(262144));
   }
}
