package com.corz.client.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class MatrixRotateUtilsHandle {

   public static int getValue(float matrixRotateFactor, double scaleFactor, int matrixRotateFactor3) {
      return Math.max(1, Math.round((float)(matrixRotateFactor * scaleFactor * matrixRotateFactor3)));
   }

   public static float getAmount(float matrixRotateValue, float peekPositionFactor, float matrixRotateFactor) {
      return peekPositionFactor + (0.5F - matrixRotateValue * 0.5F) * matrixRotateFactor;
   }

   private MatrixRotateUtilsHandle() {
   }

   public static Matrix4f getPerspective(Matrix4f matrix, float perspectiveValue, float perspectiveValue2) {
      return matrix.setPerspective(0.41887903F, perspectiveValue, Math.max(0.05F, perspectiveValue2 - 3.0F), perspectiveValue2 + 3.0F);
   }

   public static void handleScaleTranslate(Matrix4f matrix, Matrix3f matrix3f) {
      matrix.scale(0.9375F, -0.9375F, -0.9375F).translate(0.0F, -1.501F, 0.0F);
      if (matrix3f != null) {
         matrix3f.scale(1.0F, -1.0F, -1.0F);
      }
   }

   public static float getAmount(float degrees, float matrixRotateValue, float matrixRotateValue2, float matrixRotateValue3) {
      double tan = Math.tan(Math.toRadians(degrees) / 2.0);
      return (float)Math.max(matrixRotateValue3 / tan, matrixRotateValue2 / (tan * Math.max(0.001, (double)matrixRotateValue)));
   }

   public static float getAmount(float matrixRotateValue) {
      return getAmount(24.0F, matrixRotateValue, 0.8F, 1.1F) / 0.92F;
   }

   public static float getAmountForFloat(float matrixRotateValue, float matrixRenderValue, float matrixRotateFactor) {
      return matrixRenderValue + (matrixRotateValue * 0.5F + 0.5F) * matrixRotateFactor;
   }

   public static float getAmount(float matrixRotateValue, float matrixRotateValue2) {
      float viewZ2 = -matrixRotateValue;
      return !(viewZ2 > 1.0E-4F) ? 0.0F : matrixRotateValue2 / (2.0F * viewZ2 * (float)Math.tan((float) (Math.PI / 15)));
   }

   public static float getAmountFloat(float matrixRotateLimit) {
      return matrixRotateLimit < 0.0F ? 0.0F : (matrixRotateLimit > 1.0F ? 1.0F : matrixRotateLimit);
   }

   public static void handleRotateTranslate(Matrix4f matrix, Matrix3f matrix3f, float rotateTranslateValue, float rotateTranslateFactor) {
      matrix.translate(0.0F, 0.0F, -rotateTranslateValue).rotateX(rotateTranslateFactor * (float) (Math.PI / 180.0)).translate(0.0F, -0.94F, 0.0F);
      if (matrix3f != null) {
         matrix3f.rotateX(rotateTranslateFactor * (float) (Math.PI / 180.0));
      }
   }

   public static void handleRotate(Matrix4f matrix, Matrix3f matrix3f, float rotateValue) {
      matrix.rotateY(-rotateValue * (float) (Math.PI / 180.0));
      if (matrix3f != null) {
         matrix3f.rotateY(-rotateValue * (float) (Math.PI / 180.0));
      }
   }

   public static float getLength(Vector3f vector3f, Vector3f vector3f2) {
      float deltaX = vector3f2.x - vector3f.x;
      float deltaY = vector3f2.y - vector3f.y;
      float deltaZ = vector3f2.z - vector3f.z;
      float lengthLimit = (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * vector3f.length();
      if (!(lengthLimit > 1.0E-6F)) {
         return 0.0F;
      } else {
         float lengthValue = -(deltaX * vector3f.x + deltaY * vector3f.y + deltaZ * vector3f.z) / lengthLimit;
         return getAmountFloat((lengthValue - 0.25F) / 0.75F);
      }
   }

   public static Matrix4f getIdentity(Matrix4f identity, float identityValue, float rotateTranslateFactor, float rotateValue) {
      identity.identity();
      handleRotateTranslate(identity, null, identityValue, rotateTranslateFactor);
      handleRotate(identity, null, rotateValue);
      handleScaleTranslate(identity, null);
      return identity;
   }
}
