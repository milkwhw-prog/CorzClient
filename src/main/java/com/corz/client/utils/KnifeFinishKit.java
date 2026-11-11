package com.corz.client.utils;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeFinishBake;

public final class KnifeFinishKit {
   public static float getAmount(KnifeFinish.Texel texel) {
      return getAmount3(getAmount3(texel));
   }

   public static KnifeFinishKit.Ramp getRamp(int... rampValues) {
      float[] floats = new float[rampValues.length];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = rampValues.length == 1 ? 0.0F : (float)i / (rampValues.length - 1);
      }

      return new KnifeFinishKit.Ramp(floats, rampValues);
   }

   public static float getAmount(float knifeFinishValue) {
      return knifeFinishValue - (float)Math.floor(knifeFinishValue);
   }

   public static int getValue(float[] knifeFinishValues, int knifeFinishValue) {
      float knifeFinishFactor = knifeFinishValues[0] + 0.39633778F * knifeFinishValues[1] + 0.21580376F * knifeFinishValues[2];
      float knifeFinishFactor2 = knifeFinishValues[0] - 0.105561346F * knifeFinishValues[1] - 0.06385417F * knifeFinishValues[2];
      float knifeFinishFactor3 = knifeFinishValues[0] - 0.08948418F * knifeFinishValues[1] - 1.2914855F * knifeFinishValues[2];
      float knifeFinishFactor4 = knifeFinishFactor * knifeFinishFactor * knifeFinishFactor;
      float knifeFinishValue2 = knifeFinishFactor2 * knifeFinishFactor2 * knifeFinishFactor2;
      float knifeFinishValue3 = knifeFinishFactor3 * knifeFinishFactor3 * knifeFinishFactor3;
      float base = 4.0767417F * knifeFinishFactor4 - 3.3077116F * knifeFinishValue2 + 0.23096994F * knifeFinishValue3;
      float knifeFinishValue5 = -1.268438F * knifeFinishFactor4 + 2.6097574F * knifeFinishValue2 - 0.34131938F * knifeFinishValue3;
      float knifeFinishValue6 = -0.0041960864F * knifeFinishFactor4 - 0.7034186F * knifeFinishValue2 + 1.7076147F * knifeFinishValue3;
      return getValueForInt(knifeFinishValue) << 24 | KnifeFinishBake.getValue(base) << 16 | KnifeFinishBake.getValue(knifeFinishValue5) << 8 | KnifeFinishBake.getValue(knifeFinishValue6);
   }

   public static int getValue(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor, float knifeFinishValue2) {
      float knifeFinishValue3 = getAmount(texel);
      float amountForTexel = getAmountForTexel(texel);
      float knifeFinishFactor3 = 1.0F + knifeFinishValue2 * 0.06F * getAmount(texel, 1.0F, texel.texelSeed);
      int value3 = getValue3(knifeFinishValue, knifeFinishFactor3 * (0.9F + 0.62F * (knifeFinishValue3 - 0.62F) * knifeFinishFactor));
      int valueInt = getValueInt(value3, getValue4(knifeFinishValue, 0.55F), knifeFinishFactor * 0.6F * amountForTexel);
      return getValueInt(valueInt, getValue4(knifeFinishValue, 0.45F), 0.3F * getAmount4(texel, 0.08F));
   }

   public static float getAmount(float knifeFinishValue, float knifeFinishValue2, float knifeFinishFactor) {
      return knifeFinishValue + (knifeFinishValue2 - knifeFinishValue) * knifeFinishFactor;
   }

   public static float getAmountForFloat(float knifeFinishValue, float knifeFinishValue2, float knifeFinishValue3) {
      float knifeFinishFactor = getMax((knifeFinishValue3 - knifeFinishValue) / (knifeFinishValue2 - knifeFinishValue));
      return knifeFinishFactor * knifeFinishFactor * (3.0F - 2.0F * knifeFinishFactor);
   }

   public static int getValue(int knifeFinishValue, float knifeFinishLimit) {
      return getValue(knifeFinishValue, 0xFF000000 | knifeFinishValue & 0xFF000000, knifeFinishLimit);
   }

   public static float getAmount(float knifeFinishValue, float hashRegistryValue, int knifeFinishLimit, int knifeFinishValue3) {
      float knifeFinishTotal = 0.0F;
      float knifeFinishFactor = 1.0F;
      float knifeFinishTotal2 = 0.0F;
      float max2 = 1.0F;

      for (int i = 0; i < knifeFinishLimit; i++) {
         float knifeFinishFactor3 = 1.0F - Math.abs(getAmountForFloatInt(knifeFinishValue, hashRegistryValue, knifeFinishValue3 + i * 733));
         float knifeFinishFactor4 = knifeFinishFactor3 * knifeFinishFactor3;
         knifeFinishTotal += knifeFinishFactor4 * knifeFinishFactor * max2;
         knifeFinishTotal2 += knifeFinishFactor;
         max2 = getMax(knifeFinishFactor4 * 1.6F);
         knifeFinishFactor *= 0.5F;
         float knifeFinishValue4 = knifeFinishValue * 0.8F - hashRegistryValue * 0.6F;
         float knifeFinishValue5 = knifeFinishValue * 0.6F + hashRegistryValue * 0.8F;
         knifeFinishValue = knifeFinishValue4 * 2.07F + 5.2F;
         hashRegistryValue = knifeFinishValue5 * 2.07F + 1.3F;
      }

      return getMax(knifeFinishTotal / knifeFinishTotal2);
   }

   public static float getAmount(KnifeFinish.Texel texel, float knifeFinishValue) {
      return texel.outlineDistance >= 1.0E19F ? 0.0F : 1.0F - getAmountForFloat(0.0F, knifeFinishValue, Math.max(0.0F, texel.outlineDistance));
   }

   public static float getAmountForTexelFloat(KnifeFinish.Texel texel, float knifeFinishValue) {
      return texel.rigEdgeDistance >= 1.0E9F ? 0.0F : 1.0F - getAmountForFloat(0.0F, knifeFinishValue, texel.rigEdgeDistance);
   }

   public static float getAmountForTexel(KnifeFinish.Texel texel) {
      float knifeFinishValue = getAmount(getAmount3(texel));
      float knifeFinishFactor = 0.4F + 0.6F * getMax(0.55F + 0.8F * getAmountForFloatInt(texel.rampAmount * 0.32F + 4.1F, texel.texelY * 0.2F, 4242));
      return knifeFinishFactor * (0.5F * getAmountFloat(knifeFinishValue, 0.6F, 0.022F) + 0.45F * getAmountFloat(knifeFinishValue, 0.6F, 0.045F)) + 0.4F * getAmountFloat(knifeFinishValue, 0.205F, 0.05F);
   }

   public static float getAmount(float knifeFinishValue, float hashRegistryValue, int knifeFinishValue3) {
      int floorSeed = (int)Math.floor(knifeFinishValue);
      int floor4 = (int)Math.floor(hashRegistryValue);
      float knifeFinishLimit = 9.0F;

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            int knifeFinishValue5 = floorSeed + j;
            int knifeFinishValue6 = floor4 + i;
            int knifeFinishValue7 = getValue(knifeFinishValue5, knifeFinishValue6, knifeFinishValue3);
            float knifeFinishValue8 = knifeFinishValue5 + (knifeFinishValue7 & 65535) / 65536.0F;
            float knifeFinishValue9 = knifeFinishValue6 + (knifeFinishValue7 >>> 16) / 65536.0F;
            float knifeFinishValue10 = (knifeFinishValue8 - knifeFinishValue) * (knifeFinishValue8 - knifeFinishValue) + (knifeFinishValue9 - hashRegistryValue) * (knifeFinishValue9 - hashRegistryValue);
            if (knifeFinishValue10 < knifeFinishLimit) {
               knifeFinishLimit = knifeFinishValue10;
            }
         }
      }

      return (float)Math.sqrt(knifeFinishLimit);
   }

   public static int getValueForIntFloat(int knifeFinishValue, float knifeFinishValue2) {
      float[] knifeFinishValues = getAmounts(knifeFinishValue);
      knifeFinishValues[1] *= knifeFinishValue2;
      knifeFinishValues[2] *= knifeFinishValue2;
      return getValue(knifeFinishValues, knifeFinishValue >>> 24);
   }

   private static float getAmount(int knifeFinishValue, int floorSeed, float knifeFinishFactor, float knifeFinishFactor2, int knifeFinishValue3) {
      return switch (getValue(knifeFinishValue, floorSeed, knifeFinishValue3) & 7) {
         case 0 -> knifeFinishFactor + knifeFinishFactor2;
         case 1 -> knifeFinishFactor - knifeFinishFactor2;
         case 2 -> -knifeFinishFactor + knifeFinishFactor2;
         case 3 -> -knifeFinishFactor - knifeFinishFactor2;
         case 4 -> 1.41421F * knifeFinishFactor;
         case 5 -> -1.41421F * knifeFinishFactor;
         case 6 -> 1.41421F * knifeFinishFactor2;
         default -> -1.41421F * knifeFinishFactor2;
      };
   }

   public static float getAmountForFloatInt(float knifeFinishValue, float hashRegistryValue, int knifeFinishValue3) {
      int floor4 = (int)Math.floor(knifeFinishValue);
      int floorSeed = (int)Math.floor(hashRegistryValue);
      float knifeFinishValue5 = knifeFinishValue - floor4;
      float knifeFinishValue6 = hashRegistryValue - floorSeed;
      float amount2 = getAmount2(knifeFinishValue5);
      float knifeFinishFactor = getAmount2(knifeFinishValue6);
      float knifeFinishValue9 = getAmount(floor4, floorSeed, knifeFinishValue5, knifeFinishValue6, knifeFinishValue3);
      float knifeFinishValue10 = getAmount(floor4 + 1, floorSeed, knifeFinishValue5 - 1.0F, knifeFinishValue6, knifeFinishValue3);
      float knifeFinishValue11 = getAmount(floor4, floorSeed + 1, knifeFinishValue5, knifeFinishValue6 - 1.0F, knifeFinishValue3);
      float knifeFinishValue12 = getAmount(floor4 + 1, floorSeed + 1, knifeFinishValue5 - 1.0F, knifeFinishValue6 - 1.0F, knifeFinishValue3);
      return getAmount4(getAmount(getAmount(knifeFinishValue9, knifeFinishValue10, amount2), getAmount(knifeFinishValue11, knifeFinishValue12, amount2), knifeFinishFactor) * 1.06F, -1.0F, 1.0F);
   }

   public static int getValue(int knifeFinishValue, int stopsRamp, float knifeFinishLimit) {
      knifeFinishLimit = getMax(knifeFinishLimit);
      float knifeFinishValue4 = getAmount(KnifeFinishBake.getAmounts(knifeFinishValue >> 16), KnifeFinishBake.getAmounts(stopsRamp >> 16), knifeFinishLimit);
      float knifeFinishValue5 = getAmount(KnifeFinishBake.getAmounts(knifeFinishValue >> 8), KnifeFinishBake.getAmounts(stopsRamp >> 8), knifeFinishLimit);
      float base = getAmount(KnifeFinishBake.getAmounts(knifeFinishValue), KnifeFinishBake.getAmounts(stopsRamp), knifeFinishLimit);
      int round = Math.round(getAmount((float)(knifeFinishValue >>> 24 & 0xFF), (float)(stopsRamp >>> 24 & 0xFF), knifeFinishLimit));
      return round << 24 | KnifeFinishBake.getValue(knifeFinishValue4) << 16 | KnifeFinishBake.getValue(knifeFinishValue5) << 8 | KnifeFinishBake.getValue(base);
   }

   public static float getAmountFloat(float knifeFinishValue, float knifeFinishValue2, float knifeFinishDivisor) {
      float knifeFinishFactor = (knifeFinishValue - knifeFinishValue2) / knifeFinishDivisor;
      return (float)Math.exp(-knifeFinishFactor * knifeFinishFactor);
   }

   private static float getAmount2(float knifeFinishFactor) {
      return knifeFinishFactor * knifeFinishFactor * knifeFinishFactor * (knifeFinishFactor * (knifeFinishFactor * 6.0F - 15.0F) + 10.0F);
   }

   public static float getMax(float knifeFinishLimit) {
      return knifeFinishLimit < 0.0F ? 0.0F : (knifeFinishLimit > 1.0F ? 1.0F : knifeFinishLimit);
   }

   public static int getValue(int knifeFinishValue) {
      return 0xFF000000 | knifeFinishValue;
   }

   public static KnifeFinishKit.Ramp getRamp(float[] rampValues, int[] rampValues2) {
      return new KnifeFinishKit.Ramp(rampValues, rampValues2);
   }

   public static int getValueInt(int knifeFinishValue, int knifeFinishValue2, float knifeFinishFactor) {
      float amounts3 = KnifeFinishBake.getAmounts(knifeFinishValue >> 16);
      float amounts4 = KnifeFinishBake.getAmounts(knifeFinishValue >> 8);
      float amounts5 = KnifeFinishBake.getAmounts(knifeFinishValue);
      float knifeFinishValue6 = KnifeFinishBake.getAmounts(knifeFinishValue2 >> 16) * knifeFinishFactor;
      float knifeFinishValue7 = KnifeFinishBake.getAmounts(knifeFinishValue2 >> 8) * knifeFinishFactor;
      float knifeFinishValue8 = KnifeFinishBake.getAmounts(knifeFinishValue2) * knifeFinishFactor;
      return knifeFinishValue & 0xFF000000
         | KnifeFinishBake.getValue(1.0F - (1.0F - amounts3) * (1.0F - knifeFinishValue6)) << 16
         | KnifeFinishBake.getValue(1.0F - (1.0F - amounts4) * (1.0F - knifeFinishValue7)) << 8
         | KnifeFinishBake.getValue(1.0F - (1.0F - amounts5) * (1.0F - knifeFinishValue8));
   }

   public static float getAmount(KnifeFinish.Texel texel, float knifeFinishFactor, int knifeFinishValue) {
      float knifeFinishValue2 = getAmountForFloatInt(texel.rampAmount * 46.0F * knifeFinishFactor, texel.texelY * 1.3F * knifeFinishFactor, knifeFinishValue + 7) * 0.6F;
      float knifeFinishValue3 = knifeFinishValue2 + getAmountForFloatInt(texel.rampAmount * 131.0F * knifeFinishFactor, texel.texelY * 2.9F * knifeFinishFactor, knifeFinishValue + 8) * 0.3F;
      float knifeFinishLimit = knifeFinishValue3 + (getAmount((int)Math.floor(texel.rampAmount * 260.0F * knifeFinishFactor), (int)Math.floor(texel.texelY * 9.0F * knifeFinishFactor), knifeFinishValue + 9) - 0.5F) * 0.35F;
      return getAmount4(knifeFinishLimit, -1.0F, 1.0F);
   }

   public static int getValue(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor, int knifeFinishValue2, int knifeFinishValue3) {
      float knifeFinishValue4 = getAmount(texel);
      float amountForTexel = getAmountForTexel(texel);
      float knifeFinishFactor3 = 1.0F + 0.03F * getAmount(texel, 1.0F, texel.texelSeed);
      int value3 = getValue3(knifeFinishValue, knifeFinishFactor3 * (0.86F + 0.78F * (knifeFinishValue4 - 0.62F) * knifeFinishFactor));
      int valueInt = getValueInt(value3, knifeFinishValue2, knifeFinishFactor * 0.45F * amountForTexel);
      int valueInt7 = getValueInt(valueInt, knifeFinishValue3, knifeFinishFactor * 0.12F * amountForTexel * amountForTexel);
      return getValueInt(valueInt7, getValue4(knifeFinishValue, 0.5F), 0.3F * getAmount4(texel, 0.08F));
   }

   public static float getAmount4(float knifeFinishLimit, float knifeFinishLimit2, float knifeFinishValue) {
      return knifeFinishLimit < knifeFinishLimit2 ? knifeFinishLimit2 : (knifeFinishLimit > knifeFinishValue ? knifeFinishValue : knifeFinishLimit);
   }

   public static float[] getAmounts(int knifeFinishValue) {
      float amounts2 = KnifeFinishBake.getAmounts(knifeFinishValue >> 16);
      float amounts3 = KnifeFinishBake.getAmounts(knifeFinishValue >> 8);
      float amounts4 = KnifeFinishBake.getAmounts(knifeFinishValue);
      float cbrt = (float)Math.cbrt(0.41222146F * amounts2 + 0.53633255F * amounts3 + 0.051445995F * amounts4);
      float cbrt2 = (float)Math.cbrt(0.2119035F * amounts2 + 0.6806995F * amounts3 + 0.10739696F * amounts4);
      float cbrt3 = (float)Math.cbrt(0.08830246F * amounts2 + 0.28171885F * amounts3 + 0.6299787F * amounts4);
      return new float[]{
         0.21045426F * cbrt + 0.7936178F * cbrt2 - 0.004072047F * cbrt3,
         1.9779985F * cbrt - 2.4285922F * cbrt2 + 0.4505937F * cbrt3,
         0.025904037F * cbrt + 0.78277177F * cbrt2 - 0.80867577F * cbrt3
      };
   }

   public static int getValue(int knifeFinishValue, int knifeFinishValue2) {
      return knifeFinishValue & 0xFF000000
         | KnifeFinishBake.getValue(KnifeFinishBake.getAmounts(knifeFinishValue >> 16) * KnifeFinishBake.getAmounts(knifeFinishValue2 >> 16)) << 16
         | KnifeFinishBake.getValue(KnifeFinishBake.getAmounts(knifeFinishValue >> 8) * KnifeFinishBake.getAmounts(knifeFinishValue2 >> 8)) << 8
         | KnifeFinishBake.getValue(KnifeFinishBake.getAmounts(knifeFinishValue) * KnifeFinishBake.getAmounts(knifeFinishValue2));
   }

   public static int getValue(int knifeFinishValue, int floorSeed, int knifeFinishValue3) {
      int knifeFinishValue4 = knifeFinishValue * 668265261 ^ floorSeed * 374761393 ^ knifeFinishValue3 * -1640531527;
      int knifeFinishValue5 = knifeFinishValue4 ^ knifeFinishValue4 >>> 15;
      int knifeFinishValue6 = knifeFinishValue5 * -2048144789;
      int knifeFinishValue7 = knifeFinishValue6 ^ knifeFinishValue6 >>> 13;
      int knifeFinishValue8 = knifeFinishValue7 * -1028477387;
      return knifeFinishValue8 ^ knifeFinishValue8 >>> 16;
   }

   public static float getAmount3(KnifeFinish.Texel texel, float knifeFinishValue) {
      return texel.edgeDistance >= 1.0E19F ? 0.0F : 1.0F - getAmountForFloat(0.0F, knifeFinishValue, Math.max(0.0F, texel.edgeDistance));
   }

   public static float getAmount(float knifeFinishValue, float seedScaled, float knifeFinishFactor, int knifeFinishLimit, int knifeFinishValue4) {
      float amount3 = getAmount3(knifeFinishValue, seedScaled, knifeFinishLimit, knifeFinishValue4 + 11);
      float knifeFinishFactor3 = getAmount3(knifeFinishValue + 5.2F, seedScaled + 1.3F, knifeFinishLimit, knifeFinishValue4 + 23);
      float knifeFinishFactor4 = getAmount3(knifeFinishValue + knifeFinishFactor * amount3 + 1.7F, seedScaled + knifeFinishFactor * knifeFinishFactor3 + 9.2F, knifeFinishLimit, knifeFinishValue4 + 37);
      float knifeFinishFactor5 = getAmount3(knifeFinishValue + knifeFinishFactor * amount3 + 8.3F, seedScaled + knifeFinishFactor * knifeFinishFactor3 + 2.8F, knifeFinishLimit, knifeFinishValue4 + 41);
      return getAmount3(knifeFinishValue + knifeFinishFactor * knifeFinishFactor4, seedScaled + knifeFinishFactor * knifeFinishFactor5, knifeFinishLimit, knifeFinishValue4);
   }

   public static float getAmount(float knifeFinishValue, float knifeFinishValue2, float knifeFinishValue3, float knifeFinishValue4, float knifeFinishValue5) {
      return knifeFinishValue4 + (knifeFinishValue5 - knifeFinishValue4) * getMax((knifeFinishValue - knifeFinishValue2) / (knifeFinishValue3 - knifeFinishValue2));
   }

   public static float getAmount(float knifeFinishValue, float knifeFinishValue2, float knifeFinishValue3, float knifeFinishValue4) {
      return getAmountForFloat(knifeFinishValue2 - knifeFinishValue4, knifeFinishValue2 + knifeFinishValue4, knifeFinishValue) * (1.0F - getAmountForFloat(knifeFinishValue3 - knifeFinishValue4, knifeFinishValue3 + knifeFinishValue4, knifeFinishValue));
   }

   public static float getAmountForTexelFloatInt(KnifeFinish.Texel texel, float knifeFinishFactor, int knifeFinishValue) {
      return getAmount((int)Math.floor(texel.rampAmount * 64.0F * knifeFinishFactor), (int)Math.floor(texel.texelY * 64.0F * knifeFinishFactor), knifeFinishValue) * 2.0F - 1.0F;
   }

   public static float getAmount2(float knifeFinishValue, float hashRegistryValue, int knifeFinishLimit, int knifeFinishValue3) {
      float knifeFinishTotal = 0.0F;
      float knifeFinishFactor = 1.0F;
      float knifeFinishTotal2 = 0.0F;

      for (int i = 0; i < knifeFinishLimit; i++) {
         knifeFinishTotal += knifeFinishFactor * Math.abs(getAmountForFloatInt(knifeFinishValue, hashRegistryValue, knifeFinishValue3 + i * 311));
         knifeFinishTotal2 += knifeFinishFactor;
         knifeFinishFactor *= 0.5F;
         knifeFinishValue = knifeFinishValue * 2.01F + 3.7F;
         hashRegistryValue = hashRegistryValue * 2.01F - 2.9F;
      }

      return knifeFinishTotal / knifeFinishTotal2;
   }

   private static int getValueForInt(int knifeFinishLimit) {
      return knifeFinishLimit < 0 ? 0 : (knifeFinishLimit > 255 ? 255 : knifeFinishLimit);
   }

   public static int getValue3(int knifeFinishValue, float knifeFinishFactor) {
      return knifeFinishValue & 0xFF000000
         | KnifeFinishBake.getValue(KnifeFinishBake.getAmounts(knifeFinishValue >> 16) * knifeFinishFactor) << 16
         | KnifeFinishBake.getValue(KnifeFinishBake.getAmounts(knifeFinishValue >> 8) * knifeFinishFactor) << 8
         | KnifeFinishBake.getValue(KnifeFinishBake.getAmounts(knifeFinishValue) * knifeFinishFactor);
   }

   public static float getAmount(KnifeFinish.Texel texel, float knifeFinishFactor, float knifeFinishFactor2, int knifeFinishValue) {
      if (texel.rigAmount <= -1.0E9F) {
         return 0.0F;
      } else {
         float knifeFinishFactor3 = 0.55F + 0.45F * getAmount3(texel.rampAmount * 6.0F * knifeFinishFactor2, texel.texelY * 6.0F * knifeFinishFactor2, 4, knifeFinishValue);
         float knifeFinishBound = getAmountForFloat(0.62F, 0.7F, getAmount3(texel.rampAmount * 22.0F * knifeFinishFactor2, texel.texelY * 22.0F * knifeFinishFactor2, 3, knifeFinishValue + 3))
            * getAmountForFloat(knifeFinishFactor * 2.5F, 0.0F, texel.rigAmount);
         return getMax(Math.max(getAmountForFloat(knifeFinishFactor * knifeFinishFactor3, knifeFinishFactor * knifeFinishFactor3 * 0.4F, texel.rigAmount), knifeFinishBound));
      }
   }

   public static float getAmount3(float knifeFinishValue, float hashRegistryValue, int knifeFinishValue3) {
      int floor4 = (int)Math.floor(knifeFinishValue);
      int floorSeed = (int)Math.floor(hashRegistryValue);
      int knifeFinishValue5 = floor4;
      int floorSeed6 = floorSeed;
      float knifeFinishLimit = 9.0F;

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            int knifeFinishValue7 = floor4 + j;
            int knifeFinishValue8 = floorSeed + i;
            int knifeFinishValue9 = getValue(knifeFinishValue7, knifeFinishValue8, knifeFinishValue3);
            float knifeFinishValue10 = knifeFinishValue7 + (knifeFinishValue9 & 65535) / 65536.0F;
            float knifeFinishValue11 = knifeFinishValue8 + (knifeFinishValue9 >>> 16) / 65536.0F;
            float knifeFinishValue12 = (knifeFinishValue10 - knifeFinishValue) * (knifeFinishValue10 - knifeFinishValue) + (knifeFinishValue11 - hashRegistryValue) * (knifeFinishValue11 - hashRegistryValue);
            if (knifeFinishValue12 < knifeFinishLimit) {
               knifeFinishLimit = knifeFinishValue12;
               knifeFinishValue5 = knifeFinishValue7;
               floorSeed6 = knifeFinishValue8;
            }
         }
      }

      return getAmount(knifeFinishValue5, floorSeed6, knifeFinishValue3 ^ 1540483477);
   }

   public static int getValue(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor) {
      return getValue(texel, knifeFinishValue, knifeFinishFactor, getValue4(knifeFinishValue, 0.6F));
   }

   public static float getAmount(int knifeFinishValue) {
      return 0.2126F * KnifeFinishBake.getAmounts(knifeFinishValue >> 16) + 0.7152F * KnifeFinishBake.getAmounts(knifeFinishValue >> 8) + 0.0722F * KnifeFinishBake.getAmounts(knifeFinishValue);
   }

   public static float getAmount4(float knifeFinishValue, float hashRegistryValue, int knifeFinishValue3) {
      int floorSeed = (int)Math.floor(knifeFinishValue);
      int floor4 = (int)Math.floor(hashRegistryValue);
      float knifeFinishValue5 = knifeFinishValue - floorSeed;
      float knifeFinishValue6 = hashRegistryValue - floor4;
      float amount2 = getAmount2(knifeFinishValue5);
      float knifeFinishFactor = getAmount2(knifeFinishValue6);
      return getAmount(
         getAmount(getAmount(floorSeed, floor4, knifeFinishValue3), getAmount(floorSeed + 1, floor4, knifeFinishValue3), amount2),
         getAmount(getAmount(floorSeed, floor4 + 1, knifeFinishValue3), getAmount(floorSeed + 1, floor4 + 1, knifeFinishValue3), amount2),
         knifeFinishFactor
      );
   }

   public static float getAmount2(float knifeFinishValue, float seedScaled, float knifeFinishFactor, int knifeFinishLimit, int knifeFinishValue4) {
      return knifeFinishValue + knifeFinishFactor * getAmount3(knifeFinishValue, seedScaled, knifeFinishLimit, knifeFinishValue4 + 101);
   }

   public static float getAmount3(KnifeFinish.Texel texel) {
      float knifeFinishValue = 0.15F * (0.86F * texel.texelY - 0.5F * texel.rampAmount) + 0.62F * texel.fieldX + 0.28F * texel.fieldY;
      return knifeFinishValue + 0.018F * getAmountForFloatInt(texel.rampAmount * 0.8F, texel.texelY * 0.3F, 991) + 0.16F * (getAmount(17, 29, texel.texelSeed) - 0.5F);
   }

   public static float getAmount(int knifeFinishValue, int floorSeed, int knifeFinishValue3) {
      return (getValue(knifeFinishValue, floorSeed, knifeFinishValue3) >>> 8) / 1.6777216E7F;
   }

   public static int getValue4(int knifeFinishValue, float knifeFinishLimit) {
      return getValue(knifeFinishValue, -1 | knifeFinishValue & 0xFF000000, knifeFinishLimit);
   }

   public static int getValue2(int knifeFinishValue, int knifeFinishLimit, int knifeFinishValue3) {
      return 0xFF000000 | getValueForInt(knifeFinishValue) << 16 | getValueForInt(knifeFinishLimit) << 8 | getValueForInt(knifeFinishValue3);
   }

   private KnifeFinishKit() {
   }

   public static int getValue3(int knifeFinishValue, int knifeFinishValue2, float knifeFinishFactor) {
      return knifeFinishValue & 0xFF000000
         | KnifeFinishBake.getValue(KnifeFinishBake.getAmounts(knifeFinishValue >> 16) + KnifeFinishBake.getAmounts(knifeFinishValue2 >> 16) * knifeFinishFactor) << 16
         | KnifeFinishBake.getValue(KnifeFinishBake.getAmounts(knifeFinishValue >> 8) + KnifeFinishBake.getAmounts(knifeFinishValue2 >> 8) * knifeFinishFactor) << 8
         | KnifeFinishBake.getValue(KnifeFinishBake.getAmounts(knifeFinishValue) + KnifeFinishBake.getAmounts(knifeFinishValue2) * knifeFinishFactor);
   }

   private static int getValue4(int knifeFinishValue, int knifeFinishValue2, float knifeFinishFactor) {
      int round = Math.round(getAmount((float)(knifeFinishValue >> 16 & 0xFF), (float)(knifeFinishValue2 >> 16 & 0xFF), knifeFinishFactor));
      int round5 = Math.round(getAmount((float)(knifeFinishValue >> 8 & 0xFF), (float)(knifeFinishValue2 >> 8 & 0xFF), knifeFinishFactor));
      int round6 = Math.round(getAmount((float)(knifeFinishValue & 0xFF), (float)(knifeFinishValue2 & 0xFF), knifeFinishFactor));
      return 0xFF000000 | round << 16 | round5 << 8 | round6;
   }

   public static float getAmount3(KnifeFinish.Texel texel, float knifeFinishValue, int knifeFinishValue2) {
      float knifeFinishBound = 0.0F;

      for (int i = 0; i < 3; i++) {
         float angle = 0.3F + i * 1.1F;
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         float knifeFinishValue3 = texel.rampAmount * cos + texel.texelY * sin;
         float knifeFinishValue4 = -texel.rampAmount * sin + texel.texelY * cos;
         float knifeFinishFactor = 1.0F - getAmountForFloat(0.0F, 0.012F, Math.abs(getAmount(knifeFinishValue3 * 9.0F + getAmountForFloatInt(knifeFinishValue4 * 0.7F, (float)i, knifeFinishValue2) * 0.4F) - 0.5F) / 9.0F);
         knifeFinishBound = Math.max(knifeFinishBound, knifeFinishFactor * getAmountForFloat(1.0F - knifeFinishValue, 1.0F, getAmount4(knifeFinishValue3 * 3.0F, knifeFinishValue4 * 0.6F, knifeFinishValue2 + i)));
      }

      return knifeFinishBound;
   }

   public static int getValue(float knifeFinishValue) {
      int valueForInt = getValueForInt(Math.round(getMax(knifeFinishValue) * 255.0F));
      return 0xFF000000 | valueForInt << 16 | valueForInt << 8 | valueForInt;
   }

   public static int getValue5(int knifeFinishValue, float degrees) {
      float[] knifeFinishValues = getAmounts(knifeFinishValue);
      double angle = Math.toRadians(degrees);
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      float knifeFinishValue2 = (float)(knifeFinishValues[1] * cos - knifeFinishValues[2] * sin);
      float knifeFinishValue3 = (float)(knifeFinishValues[1] * sin + knifeFinishValues[2] * cos);
      knifeFinishValues[1] = knifeFinishValue2;
      knifeFinishValues[2] = knifeFinishValue3;
      return getValue(knifeFinishValues, knifeFinishValue >>> 24);
   }

   public static float getAmount(KnifeFinish.Texel texel, float knifeFinishValue, float knifeFinishValue2) {
      if (texel.rigAmount <= -1.0E9F) {
         return 0.0F;
      } else {
         float abs3 = Math.abs(texel.rigAmount - knifeFinishValue);
         return 1.0F - getAmountForFloat(knifeFinishValue2 * 0.35F, knifeFinishValue2, abs3);
      }
   }

   public static float getAmount(float knifeFinishValue, float hashRegistryValue, int knifeFinishLimit, float knifeFinishFactor, float knifeFinishValue3, int knifeFinishValue4) {
      float knifeFinishTotal = 0.0F;
      float knifeFinishFactor2 = 1.0F;
      float knifeFinishTotal2 = 0.0F;

      for (int i = 0; i < knifeFinishLimit; i++) {
         knifeFinishTotal += knifeFinishFactor2 * getAmountForFloatInt(knifeFinishValue, hashRegistryValue, knifeFinishValue4 + i * 1013);
         knifeFinishTotal2 += knifeFinishFactor2;
         knifeFinishFactor2 *= knifeFinishValue3;
         float knifeFinishFactor3 = knifeFinishValue * 0.8F - hashRegistryValue * 0.6F;
         float knifeFinishFactor4 = knifeFinishValue * 0.6F + hashRegistryValue * 0.8F;
         knifeFinishValue = knifeFinishFactor3 * knifeFinishFactor + 17.3F;
         hashRegistryValue = knifeFinishFactor4 * knifeFinishFactor - 9.1F;
      }

      return knifeFinishTotal / knifeFinishTotal2;
   }

   public static float getAmount3(float knifeFinishValue, float knifeFinishValue2, float knifeFinishFactor, int knifeFinishLimit, int knifeFinishValue4) {
      return knifeFinishValue2 + knifeFinishFactor * getAmount3(knifeFinishValue + 3.1F, knifeFinishValue2 - 7.7F, knifeFinishLimit, knifeFinishValue4 + 202);
   }

   public static float getAmount5(float knifeFinishValue, float hashRegistryValue, int knifeFinishValue3) {
      int floor4 = (int)Math.floor(knifeFinishValue);
      int floor5 = (int)Math.floor(hashRegistryValue);
      float knifeFinishLimit = 9.0F;
      float squaredDistanceTo = 9.0F;

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            int knifeFinishValue6 = floor4 + j;
            int floorSeed = floor5 + i;
            int knifeFinishValue8 = getValue(knifeFinishValue6, floorSeed, knifeFinishValue3);
            float knifeFinishValue9 = knifeFinishValue6 + (knifeFinishValue8 & 65535) / 65536.0F;
            float knifeFinishValue10 = floorSeed + (knifeFinishValue8 >>> 16) / 65536.0F;
            float knifeFinishValue11 = (knifeFinishValue9 - knifeFinishValue) * (knifeFinishValue9 - knifeFinishValue) + (knifeFinishValue10 - hashRegistryValue) * (knifeFinishValue10 - hashRegistryValue);
            if (knifeFinishValue11 < knifeFinishLimit) {
               squaredDistanceTo = knifeFinishLimit;
               knifeFinishLimit = knifeFinishValue11;
            } else if (knifeFinishValue11 < squaredDistanceTo) {
               squaredDistanceTo = knifeFinishValue11;
            }
         }
      }

      return (float)(Math.sqrt(squaredDistanceTo) - Math.sqrt(knifeFinishLimit));
   }

   public static float getAmount3(float knifeFinishValue) {
      knifeFinishValue = getAmount(knifeFinishValue);
      float knifeFinishValue2 = getAmountFloat(knifeFinishValue, 0.22F, 0.13F) + getAmountFloat(knifeFinishValue, 1.22F, 0.13F);
      float amountFloat = getAmountFloat(knifeFinishValue, 0.6F, 0.03F);
      float knifeFinishValue4 = getAmountFloat(knifeFinishValue, 0.43F, 0.07F) + getAmountFloat(knifeFinishValue, 0.86F, 0.09F) + getAmountFloat(knifeFinishValue, -0.14F, 0.09F);
      return getAmount4(0.5F + 0.5F * knifeFinishValue2 + 0.42F * amountFloat - 0.28F * knifeFinishValue4, 0.0F, 1.3F);
   }

   public static float getAmount3(float knifeFinishValue, float seedScaled, int knifeFinishLimit, int knifeFinishValue4) {
      return getAmount(knifeFinishValue, seedScaled, knifeFinishLimit, 2.03F, 0.5F, knifeFinishValue4);
   }

   public static int getValue(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor, int rampAmount) {
      return getValue(texel, knifeFinishValue, knifeFinishFactor, rampAmount, -1);
   }

   public static int getValue6(int knifeFinishValue, float knifeFinishLimit) {
      return getValueForInt(Math.round(getMax(knifeFinishLimit) * 255.0F)) << 24 | knifeFinishValue & 16777215;
   }

   public static int getValueForTexelInt(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishValue2) {
      float knifeFinishValue3 = getAmount(texel);
      float knifeFinishFactor = 1.0F + 0.035F * getAmountForTexelFloatInt(texel, 1.6F, texel.texelSeed + 5);
      int value3 = getValue3(knifeFinishValue, knifeFinishFactor * (0.95F + 0.3F * (knifeFinishValue3 - 0.62F) * (1.0F - knifeFinishValue2)));
      return getValueInt(value3, getValue4(knifeFinishValue, 0.35F), (1.0F - knifeFinishValue2) * 0.18F * getAmountForTexel(texel));
   }

   public static float getAmount4(KnifeFinish.Texel texel, float knifeFinishValue) {
      return !(texel.rigAmount <= -1.0E9F) && !(texel.rigAmount < 0.0F) ? getAmountFloat(texel.rigAmount, knifeFinishValue * 0.5F, knifeFinishValue * 0.32F) : 0.0F;
   }

   public static int getValueForTexelIntFloat(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor) {
      return getValue6(getValue4(knifeFinishValue, 0.55F), getMax(knifeFinishFactor * getAmountForTexel(texel)));
   }

   public static final class Ramp {
      private final int[] rampStopsValues = new int[1025];

      public int getStopsRamp(float stopsRampLimit) {
         if (!(stopsRampLimit > 0.0F)) {
            return this.rampStopsValues[0];
         } else if (stopsRampLimit >= 1.0F) {
            return this.rampStopsValues[1024];
         } else {
            float stopsRampValue = stopsRampLimit * 1024.0F;
            int index = (int)stopsRampValue;
            return KnifeFinishKit.getValue4(this.rampStopsValues[index], this.rampStopsValues[index + 1], stopsRampValue - index);
         }
      }

      Ramp(float[] rampValues, int[] rampValues2) {
         if (rampValues.length == rampValues2.length && rampValues.length != 0) {
            float[][] floats = new float[rampValues2.length][];

            for (int i = 0; i < rampValues2.length; i++) {
               floats[i] = KnifeFinishKit.getAmounts(rampValues2[i]);
            }

            for (int j = 0; j <= 1024; j++) {
               float rampLimit = j / 1024.0F;
               int index = 0;

               while (index < rampValues.length - 1 && rampLimit > rampValues[index + 1]) {
                  index++;
               }

               if (rampLimit <= rampValues[0]) {
                  this.rampStopsValues[j] = rampValues2[0] | 0xFF000000;
               } else if (index >= rampValues.length - 1) {
                  this.rampStopsValues[j] = rampValues2[rampValues.length - 1] | 0xFF000000;
               } else {
                  float rampFactor = (rampLimit - rampValues[index]) / Math.max(1.0E-6F, rampValues[index + 1] - rampValues[index]);
                  float rampValue = rampFactor * rampFactor * (3.0F - 2.0F * rampFactor) * 0.35F + rampFactor * 0.65F;
                  float[] rampValues3 = floats[index];
                  float[] rampValues4 = floats[index + 1];
                  this.rampStopsValues[j] = KnifeFinishKit.getValue(
                     new float[]{
                        KnifeFinishKit.getAmount(rampValues3[0], rampValues4[0], rampValue),
                        KnifeFinishKit.getAmount(rampValues3[1], rampValues4[1], rampValue),
                        KnifeFinishKit.getAmount(rampValues3[2], rampValues4[2], rampValue)
                     },
                     255
                  );
               }
            }
         } else {
            throw new IllegalArgumentException("ramp stops");
         }
      }
   }
}
