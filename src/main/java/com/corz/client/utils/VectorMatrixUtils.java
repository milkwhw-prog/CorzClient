package com.corz.client.utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class VectorMatrixUtils {
   public static void handleNormalize(float normalizeFactor, float normalizeFactor2, float normalizeFactor3, float degrees, float normalizeValue, float degrees2, Vector3f vector3f) {
      float tan = (float)Math.tan(Math.toRadians(degrees) * 0.5);
      Vector3f normalize = new Vector3f(normalizeFactor * tan * normalizeFactor3, normalizeFactor2 * tan, 1.0F).normalize();
      float angle = (float)Math.toRadians(-normalizeValue);
      float radians2 = (float)Math.toRadians(degrees2);
      float cos = (float)Math.cos(radians2);
      float sin = (float)Math.sin(radians2);
      float x = normalize.x;
      float normalizeValue2 = cos * normalize.y - sin * normalize.z;
      float normalizeFactor4 = sin * normalize.y + cos * normalize.z;
      float cos2 = (float)Math.cos(angle);
      float sin2 = (float)Math.sin(angle);
      vector3f.set(cos2 * x - sin2 * normalizeFactor4, normalizeValue2, sin2 * x + cos2 * normalizeFactor4);
   }

   public static void handle(Vector3f vector3f, float[] vectorMatrixValues) {
      float x = vector3f.x;
      float vectorMatrixValue = Math.max(vector3f.y, 0.0F);
      float z = vector3f.z;
      float vectorMatrixDivisor = Math.abs(x) + vectorMatrixValue + Math.abs(z);
      if (vectorMatrixDivisor < 1.0E-6F) {
         vectorMatrixValues[0] = 0.5F;
         vectorMatrixValues[1] = 0.5F;
      } else {
         float vectorMatrixValue2 = x / vectorMatrixDivisor;
         float vectorMatrixValue3 = z / vectorMatrixDivisor;
         vectorMatrixValues[0] = (vectorMatrixValue2 + vectorMatrixValue3) * 0.5F + 0.5F;
         vectorMatrixValues[1] = (vectorMatrixValue2 - vectorMatrixValue3) * 0.5F + 0.5F;
      }
   }

   public static void handleNormalize(float normalizeValue, float normalizeValue2, Vector3f vector3f) {
      float normalizeValue3 = normalizeValue * 2.0F - 1.0F;
      float normalizeValue4 = normalizeValue2 * 2.0F - 1.0F;
      float planValuesItem = (normalizeValue3 + normalizeValue4) * 0.5F;
      float normalizeValue6 = (normalizeValue3 - normalizeValue4) * 0.5F;
      float max7 = Math.max(1.0F - Math.abs(planValuesItem) - Math.abs(normalizeValue6), 0.0F);
      vector3f.set(planValuesItem, max7, normalizeValue6).normalize();
   }

   public static void handle(Vector3f vector3f, Vector3f vector3f2) {
      vector3f2.set(-vector3f.x, vector3f.y, vector3f.z);
   }

   public static boolean canDeterminantTranspose(Matrix4f matrix, Quaternionf quaternionf, Matrix4f matrix4f) {
      if (matrix != null && quaternionf != null && matrix.m23() != 0.0F) {
         matrix4f.rotation(quaternionf).transpose();
         matrix4f.mulLocal(matrix);
         if (Float.isFinite(matrix4f.determinant()) && !(Math.abs(matrix4f.determinant()) < 1.0E-12F)) {
            matrix4f.invert();
            return matrix4f.isFinite();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void handleNormalizeMul(Matrix4f matrix, float x, float y, Vector3f vector3f) {
      Vector4f mul = new Vector4f(x, y, 1.0F, 1.0F).mul(matrix);
      Vector3f normalize = new Vector3f(mul.x / mul.w, mul.y / mul.w, mul.z / mul.w).normalize();
      handle(normalize, vector3f);
   }

   public static void handle(float angle, Vector3f vector3f) {
      vector3f.set((float)(-Math.sin(angle)), (float)Math.cos(angle), 0.0F);
   }

   public static void handleYxzRotation(float degrees, float degrees2, Quaternionf quaternionf) {
      quaternionf.rotationYXZ((float) Math.PI - (float)Math.toRadians(degrees), (float)(-Math.toRadians(degrees2)), 0.0F);
   }

   private VectorMatrixUtils() {
   }
}
