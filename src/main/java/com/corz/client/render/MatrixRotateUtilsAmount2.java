package com.corz.client.render;

import org.joml.Matrix4f;

public final class MatrixRotateUtilsAmount2 {

   public static float getAmount(float matrixRotateLimit, float matrixRotateBound) {
      float maxLimit = Math.max(0.0F, matrixRotateBound);
      return matrixRotateLimit < -maxLimit ? -maxLimit : (matrixRotateLimit > maxLimit ? maxLimit : matrixRotateLimit);
   }

   private MatrixRotateUtilsAmount2() {
   }

   public static float getAmount(float matrixRotateValue, float matrixRotateBound, float matrixRotateValue2, float matrixRotateValue3, float matrixRotateValue4, float planValuesItem) {
      if (!(matrixRotateValue3 <= 0.0F) && !(matrixRotateValue4 <= 0.0F)) {
         float matrixRotateFactor = Math.max(0.5F, getAmountForFloat(matrixRotateValue, matrixRotateValue2));
         double angle = Math.toRadians(Math.min(90.0, (double)Math.abs(planValuesItem)));
         float rocketsElytraBound = (float)(matrixRotateFactor * Math.sin(angle) + Math.max(0.5F, matrixRotateBound) * 0.5 * Math.cos(angle));
         float wrapDegreesBound = matrixRotateValue3 * 0.5F / matrixRotateFactor;
         float hashHuntBound = matrixRotateValue4 * 0.5F / Math.max(0.5F, rocketsElytraBound);
         return Math.max(0.001F, 0.8F * Math.min(wrapDegreesBound, hashHuntBound));
      } else {
         return 1.0F;
      }
   }

   public static float getAmount(float matrixRotateValue, float matrixRotateFactor, float matrixRotateDivisor, float matrixRotateDivisor2) {
      return !(matrixRotateDivisor <= 0.0F) && !(matrixRotateDivisor2 <= 0.0F) ? matrixRotateValue + matrixRotateFactor * (1.0F / matrixRotateDivisor2 - 1.0F / matrixRotateDivisor) : matrixRotateValue;
   }

   public static float getAmountForFloat(float matrixRotateFactor, float matrixRotateFactor2) {
      return 0.5F * (float)Math.sqrt(matrixRotateFactor * matrixRotateFactor + matrixRotateFactor2 * matrixRotateFactor2);
   }

   public static Matrix4f getRotateTranslate(
      Matrix4f matrix, float rotateTranslateValue, float rotateTranslateValue2, float rotateTranslateValue3, float rotateTranslateBound, float rotateTranslateValue4, float degrees, float x, float x2, float y, float rotateTranslateValue5, float rotateTranslateValue6
   ) {
      float rotateTranslateValue7 = (Math.max(0.5F, rotateTranslateBound) + 2.0F) * x;
      return matrix.setOrtho(-rotateTranslateValue5 * 0.5F, rotateTranslateValue5 * 0.5F, -rotateTranslateValue6 * 0.5F, rotateTranslateValue6 * 0.5F, -rotateTranslateValue7, rotateTranslateValue7)
         .scale(x)
         .translate(x2, y, 0.0F)
         .rotateX((float)Math.toRadians(degrees))
         .rotateY((float)Math.toRadians(-rotateTranslateValue4))
         .translate(-rotateTranslateValue, -rotateTranslateValue2, -rotateTranslateValue3);
   }

   public static float getAmount(float matrixRotateFactor, float matrixRotateFactor2, float matrixRotateFactor3) {
      return 0.5F * (float)Math.sqrt(matrixRotateFactor * matrixRotateFactor + matrixRotateFactor2 * matrixRotateFactor2 + matrixRotateFactor3 * matrixRotateFactor3);
   }
}
