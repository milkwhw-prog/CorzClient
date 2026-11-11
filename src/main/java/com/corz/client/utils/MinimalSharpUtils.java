package com.corz.client.utils;

import java.util.function.BooleanSupplier;

public final class MinimalSharpUtils {
   public static final String[] fireFlameTexts = new String[]{"Native", "Minimal", "Sharp"};
   public static final int UNUSED_ONE = 1;

   public static int getMinimalSharp(String minimalSharpText) {
      if (minimalSharpText == null) {
         return 0;
      } else {
         switch (minimalSharpText) {
            case "Minimal":
               return 1;
            case "Sharp":
               return 2;
            case "Native":
            default:
               return 0;
         }
      }
   }

   public static float getAmount(double minimalSharpValue) {
      return (float)getAmount(minimalSharpValue, 0.0, 1.0);
   }

   public static float getAmountForDouble(double minimalSharpValue) {
      return (float)getAmount(minimalSharpValue, 0.2, 1.5);
   }

   public static boolean canFlag(boolean minimalSharpFlag, boolean minimalSharpFlag2, boolean firstPerson, float minimalSharpLimit) {
      return minimalSharpFlag && minimalSharpFlag2 && !firstPerson && minimalSharpLimit > 0.002F;
   }

   public static float getAmount(float minimalSharpLimit, float minimalSharpLimit2, float minimalSharpValue) {
      return minimalSharpLimit < minimalSharpLimit2 ? minimalSharpLimit2 : (minimalSharpLimit > minimalSharpValue ? minimalSharpValue : minimalSharpLimit);
   }

   private MinimalSharpUtils() {
   }

   private static float getAmount(float minimalSharpValue) {
      return minimalSharpValue - (float)Math.floor(minimalSharpValue);
   }

   public static double getAmount(double minimalSharpLimit, double minimalSharpLimit2, double minimalSharpValue) {
      return minimalSharpLimit < minimalSharpLimit2 ? minimalSharpLimit2 : (minimalSharpLimit > minimalSharpValue ? minimalSharpValue : minimalSharpLimit);
   }

   public static boolean canFlag(boolean minimalSharpFlag, boolean minimalSharpFlag2, boolean minimalSharpFlag3, boolean minimalSharpFlag4) {
      return minimalSharpFlag4 || minimalSharpFlag && minimalSharpFlag2 && minimalSharpFlag3;
   }

   public static float getAmountFloat(double minimalSharpValue) {
      return (float)getAmount(minimalSharpValue, 0.0, 1.0);
   }

   public static float getAmount(float minimalSharpValue, float minimalSharpValue2, int minimalSharp, float minimalSharpValue4) {
      float minimalSharpValue5 = minimalSharp == 2 ? 3.0F : (minimalSharp == 1 ? 1.7F : 2.2F);
      float amount3 = getAmount3(minimalSharpValue * 5.0F + minimalSharpValue2 * 0.9F);
      float minimalSharpFactor = amount3 * 0.65F + getAmount3(minimalSharpValue * 11.0F - minimalSharpValue2 * 1.3F) * 0.35F;
      float minimalSharpFactor2 = 0.55F + 0.45F * getAmount3(minimalSharpValue * 2.1F + minimalSharpValue2 * 0.25F);
      float minimalSharpBound = minimalSharpFactor * minimalSharpFactor2;
      float minimalSharpValue7 = getAmount((minimalSharpValue4 - 0.2F) / 1.3F, 0.0F, 1.0F);
      float minimalSharpLimit = Math.max(minimalSharpBound, 0.15F * minimalSharpValue7);
      float exponent = Math.max(0.8F, minimalSharpValue5 - minimalSharpValue7 * 0.7F);
      float pow = (float)Math.pow(getAmount(minimalSharpLimit, 0.0F, 1.0F), exponent);
      return getAmount(pow, 0.0F, 1.0F);
   }

   public static int getValue(int minimalSharpValue) {
      int minimalSharpValue2 = minimalSharpValue >>> 24 & 0xFF;
      int minimalSharpValue3 = minimalSharpValue >> 16 & 0xFF;
      int minimalSharpValue4 = minimalSharpValue >> 8 & 0xFF;
      int minimalSharpValue5 = minimalSharpValue & 0xFF;
      int valueForInt = getValueForInt(Math.round(minimalSharpValue3 + (255 - minimalSharpValue3) * 0.55F));
      int valueForInt7 = getValueForInt(Math.round(minimalSharpValue4 + (255 - minimalSharpValue4) * 0.72F));
      int valueForInt8 = getValueForInt(Math.round(minimalSharpValue5 + (255 - minimalSharpValue5) * 0.38F));
      return minimalSharpValue2 << 24 | valueForInt << 16 | valueForInt7 << 8 | valueForInt8;
   }

   public static float getAmount4(double minimalSharpValue) {
      return (float)getAmount(minimalSharpValue, 0.1, 1.0);
   }

   private static float getAmountForFloat(float minimalSharpLimit, float minimalSharpValue, float absScaled) {
      if (minimalSharpValue <= minimalSharpLimit) {
         return absScaled < minimalSharpLimit ? 0.0F : 1.0F;
      } else {
         float minimalSharpFactor = getAmount((absScaled - minimalSharpLimit) / (minimalSharpValue - minimalSharpLimit), 0.0F, 1.0F);
         return minimalSharpFactor * minimalSharpFactor * (3.0F - 2.0F * minimalSharpFactor);
      }
   }

   public static boolean canFlag(boolean minimalSharpFlag) {
      return minimalSharpFlag;
   }

   public static float getAmount2(float minimalSharpValue) {
      minimalSharpValue = getAmount(minimalSharpValue * 0.1031F);
      minimalSharpValue *= minimalSharpValue + 33.33F;
      minimalSharpValue *= minimalSharpValue + minimalSharpValue;
      return getAmount(minimalSharpValue);
   }

   public static boolean isBoolean(BooleanSupplier booleanSupplier) {
      try {
         return booleanSupplier.getAsBoolean();
      } catch (Throwable e) {
         return false;
      }
   }

   public static boolean canFlag(int minimalSharpLimit) {
      return minimalSharpLimit >= 3;
   }

   public static float getAmount5(double minimalSharpValue) {
      return (float)getAmount(minimalSharpValue, 0.2, 0.8);
   }

   public static void handle(Runnable runnable, Runnable runnable2) {
      try {
         runnable.run();
      } finally {
         runnable2.run();
      }
   }

   public static float getAmount(float minimalSharpValue, boolean minimalSharpFlag, double minimalSharpValue2) {
      if (minimalSharpValue2 < 0.0) {
         minimalSharpValue2 = 0.0;
      }

      float minimalSharpValue3 = getAmount(minimalSharpValue, 0.0F, 1.0F);
      return minimalSharpFlag ? Math.min(1.0F, minimalSharpValue3 + (float)(minimalSharpValue2 / 0.16)) : Math.max(0.0F, minimalSharpValue3 - (float)(minimalSharpValue2 / 0.24));
   }

   public static float getAmount6(double minimalSharpValue) {
      return (float)getAmount(minimalSharpValue, 0.2, 2.5);
   }

   public static float getAmount3(float minimalSharpValue) {
      float floor2 = (float)Math.floor(minimalSharpValue);
      float minimalSharpFactor = minimalSharpValue - floor2;
      float minimalSharpFactor2 = minimalSharpFactor * minimalSharpFactor * (3.0F - 2.0F * minimalSharpFactor);
      return getAmount2(floor2) * (1.0F - minimalSharpFactor2) + getAmount2(floor2 + 1.0F) * minimalSharpFactor2;
   }

   public static boolean canFlagForFlag(boolean minimalSharpFlag) {
      return !minimalSharpFlag;
   }

   public static float getAmount(float minimalSharpValue, float minimalSharpValue2) {
      float absScaled = Math.abs(minimalSharpValue - 0.5F) * 2.0F;
      float minimalSharpFactor = getAmountForFloat(0.15F, 0.75F, absScaled);
      float max3 = Math.max(0.0F, 1.0F - minimalSharpValue2 * 1.6F);
      return max3 + (1.0F - max3) * minimalSharpFactor;
   }

   private static int getValueForInt(int minimalSharpLimit) {
      return minimalSharpLimit < 0 ? 0 : (minimalSharpLimit > 255 ? 255 : minimalSharpLimit);
   }

   public static float getAmount7(double minimalSharpValue) {
      return (float)getAmount(minimalSharpValue, 0.08, 0.35);
   }

   public static float[] getAmounts(float minimalSharpValue, float minimalSharpValue2) {
      return new float[]{minimalSharpValue, minimalSharpValue2};
   }

   public static final class FailureGate {
      private boolean failureGateActive;

      public void updateFlag() {
         this.failureGateActive = false;
      }

      public boolean isFlag() {
         return this.failureGateActive;
      }

      public boolean isFlagFailureGateActive() {
         if (this.failureGateActive) {
            return false;
         } else {
            this.failureGateActive = true;
            return true;
         }
      }
   }
}
