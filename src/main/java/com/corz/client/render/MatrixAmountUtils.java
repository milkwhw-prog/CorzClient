package com.corz.client.render;

import java.awt.Color;

public final class MatrixAmountUtils {
   public static float getAmount(int matrixValue) {
      return (matrixValue >> 16 & 0xFF) / 255.0F;
   }

   public static float getAmountForInt(int matrixValue) {
      return 0.2126F * getAmount(matrixValue) + 0.7152F * getAmount4(matrixValue) + 0.0722F * getAmount6(matrixValue);
   }

   private static float getAmount(float base) {
      base = getMax(base);
      return base <= 0.0031308F ? base * 12.92F : 1.055F * (float)Math.pow(base, 0.4166666666666667) - 0.055F;
   }

   public static float getAmountFloat(int matrixValue) {
      return (matrixValue >>> 24 & 0xFF) / 255.0F;
   }

   public static float getMax(float matrixLimit) {
      return matrixLimit < 0.0F ? 0.0F : (matrixLimit > 1.0F ? 1.0F : matrixLimit);
   }

   public static int getValue(float matrixFactor, float matrixValue, float matrixLimit, float matrixValue3) {
      return getValue(getAmount() * matrixFactor * 0.1F + matrixValue, matrixLimit, matrixValue3);
   }

   public static float getAmount4(int matrixValue) {
      return (matrixValue >> 8 & 0xFF) / 255.0F;
   }

   private static float[] getAmounts(int matrixValue) {
      float pow = getPow(getAmount(matrixValue));
      float pow2 = getPow(getAmount4(matrixValue));
      float pow3 = getPow(getAmount6(matrixValue));
      float cbrt = (float)Math.cbrt(0.41222146F * pow + 0.53633255F * pow2 + 0.051445995F * pow3);
      float cbrt2 = (float)Math.cbrt(0.2119035F * pow + 0.6806995F * pow2 + 0.10739696F * pow3);
      float cbrt3 = (float)Math.cbrt(0.08830246F * pow + 0.28171885F * pow2 + 0.6299787F * pow3);
      return new float[]{
         0.21045426F * cbrt + 0.7936178F * cbrt2 - 0.004072047F * cbrt3,
         1.9779985F * cbrt - 2.4285922F * cbrt2 + 0.4505937F * cbrt3,
         0.025904037F * cbrt + 0.78277177F * cbrt2 - 0.80867577F * cbrt3
      };
   }

   public static float getAmountForFloat(float matrixFactor) {
      matrixFactor = getMax(matrixFactor);
      return matrixFactor * matrixFactor * (3.0F - 2.0F * matrixFactor);
   }

   public static float getAmount5(int matrixValue) {
      return getAmounts(matrixValue)[0];
   }

   public static int getValue(int matrixValue, float degrees, float matrixFactor, float matrixFactor2) {
      float[] matrixValues = getAmounts(matrixValue);
      double matrixFactor3 = Math.hypot(matrixValues[1], matrixValues[2]) * matrixFactor2;
      double angle = Math.atan2(matrixValues[2], matrixValues[1]) + Math.toRadians(degrees);
      float min2 = Math.min(0.95F, matrixValues[0] * matrixFactor);
      return matrixValue & 0xFF000000 | getValueForFloat(min2, (float)(matrixFactor3 * Math.cos(angle)), (float)(matrixFactor3 * Math.sin(angle)));
   }

   public static int getValue(int matrixValue, float matrixLimit) {
      return Math.round((matrixValue >>> 24 & 0xFF) * getMax(matrixLimit)) << 24 | matrixValue & 16777215;
   }

   public static int getValue(float matrixValue, float matrixValue2) {
      return getValue(matrixValue, matrixValue2, 0.75F, 1.0F);
   }

   public static float getAmount() {
      return (float)(System.nanoTime() % 1000000000000L) / 1.0E9F;
   }

   public static boolean canFlag(int matrixValue) {
      return getAmountForInt(matrixValue) > 0.72F;
   }

   public static int getValue(float matrixValue, float matrixLimit, float matrixValue3) {
      return 0xFF000000 | Color.HSBtoRGB(matrixValue - (float)Math.floor(matrixValue), getMax(matrixLimit), getMax(matrixValue3)) & 16777215;
   }

   private static int getValueForFloat(float matrixValue, float matrixValue2, float matrixValue3) {
      float matrixFactor = matrixValue + 0.39633778F * matrixValue2 + 0.21580376F * matrixValue3;
      float matrixFactor2 = matrixValue - 0.105561346F * matrixValue2 - 0.06385417F * matrixValue3;
      float matrixFactor3 = matrixValue - 0.08948418F * matrixValue2 - 1.2914855F * matrixValue3;
      float matrixFactor4 = matrixFactor * matrixFactor * matrixFactor;
      float matrixValue4 = matrixFactor2 * matrixFactor2 * matrixFactor2;
      float matrixValue5 = matrixFactor3 * matrixFactor3 * matrixFactor3;
      int round = Math.round(getAmount(4.0767417F * matrixFactor4 - 3.3077116F * matrixValue4 + 0.23096994F * matrixValue5) * 255.0F);
      int round7 = Math.round(getAmount(-1.268438F * matrixFactor4 + 2.6097574F * matrixValue4 - 0.34131938F * matrixValue5) * 255.0F);
      int round8 = Math.round(getAmount(-0.0041960864F * matrixFactor4 - 0.7034186F * matrixValue4 + 1.7076147F * matrixValue5) * 255.0F);
      return round << 16 | round7 << 8 | round8;
   }

   public static int getValue(int matrixValue, double releaseRightFactor, int matrixValue3, int matrixValue4, int matrixValue5) {
      float matrixFactor = getMax((float)releaseRightFactor);
      float max7 = getMax((getAmountForInt(matrixValue) - 0.62F) / 0.16F);
      int releaseRight = getValue(matrixValue4, matrixValue5, max7);
      return getValue(matrixValue3, releaseRight, matrixFactor);
   }

   public static int getValue(int matrixValue, int releaseRight, float matrixFactor, float matrixValue3) {
      float matrixValue4 = (float)(0.5 - 0.5 * Math.cos((getAmount() * matrixFactor * 0.1F + matrixValue3) * Math.PI * 2.0));
      return getValue(matrixValue, releaseRight, matrixValue4);
   }

   public static int getValue(int matrixValue, int releaseRight, float matrixFactor) {
      matrixFactor = getMax(matrixFactor);
      int matrixValue3 = matrixValue >>> 24 & 0xFF;
      int matrixValue4 = matrixValue >> 16 & 0xFF;
      int matrixValue5 = matrixValue >> 8 & 0xFF;
      int matrixValue6 = matrixValue & 0xFF;
      int matrixValue7 = releaseRight >>> 24 & 0xFF;
      int matrixValue8 = releaseRight >> 16 & 0xFF;
      int matrixValue9 = releaseRight >> 8 & 0xFF;
      int matrixValue10 = releaseRight & 0xFF;
      return Math.round(matrixValue3 + (matrixValue7 - matrixValue3) * matrixFactor) << 24
         | Math.round(matrixValue4 + (matrixValue8 - matrixValue4) * matrixFactor) << 16
         | Math.round(matrixValue5 + (matrixValue9 - matrixValue5) * matrixFactor) << 8
         | Math.round(matrixValue6 + (matrixValue10 - matrixValue6) * matrixFactor);
   }

   private static float getPow(float powValue) {
      return powValue <= 0.04045F ? powValue / 12.92F : (float)Math.pow((powValue + 0.055F) / 1.055F, 2.4);
   }

   public static int getValueForIntFloat(int matrixValue, int matrixValue2, float matrixFactor) {
      matrixFactor = getMax(matrixFactor);
      if (matrixFactor <= 0.0F || matrixValue == matrixValue2) {
         return matrixValue;
      } else if (matrixFactor >= 1.0F) {
         return matrixValue2;
      } else {
         float[] matrixValues = getAmounts(matrixValue);
         float[] amounts2 = getAmounts(matrixValue2);
         float matrixValue3 = matrixValues[0] + (amounts2[0] - matrixValues[0]) * matrixFactor;
         float matrixValue4 = matrixValues[1] + (amounts2[1] - matrixValues[1]) * matrixFactor;
         float matrixValue5 = matrixValues[2] + (amounts2[2] - matrixValues[2]) * matrixFactor;
         int matrixValue6 = matrixValue >>> 24 & 0xFF;
         int matrixValue7 = matrixValue2 >>> 24 & 0xFF;
         return Math.round(matrixValue6 + (matrixValue7 - matrixValue6) * matrixFactor) << 24 | getValueForFloat(matrixValue3, matrixValue4, matrixValue5);
      }
   }

   public static int getValueInt(int matrixValue, float matrixLimit) {
      return Math.round(getMax(matrixLimit) * 255.0F) << 24 | matrixValue & 16777215;
   }

   private MatrixAmountUtils() {
   }

   public static float getAmount6(int matrixValue) {
      return (matrixValue & 0xFF) / 255.0F;
   }
}
