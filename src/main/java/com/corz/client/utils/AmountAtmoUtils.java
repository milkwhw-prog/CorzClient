package com.corz.client.utils;

public final class AmountAtmoUtils {
   public static float getAmount(double atmoValue, double hashRegistryValue) {
      double floor = Math.floor(atmoValue);
      double floor4 = Math.floor(hashRegistryValue);
      int floor5 = (int)((long)floor);
      int atmoValue6 = (int)((long)floor4);
      float atmoFactor = getAmount((float)(atmoValue - floor));
      float atmoFactor2 = getAmount((float)(hashRegistryValue - floor4));
      float amountForInt = getAmountForInt(floor5, atmoValue6);
      float amountForInt8 = getAmountForInt(floor5 + 1, atmoValue6);
      float amountForInt9 = getAmountForInt(floor5, atmoValue6 + 1);
      float amountForInt10 = getAmountForInt(floor5 + 1, atmoValue6 + 1);
      float atmoValue11 = amountForInt + (amountForInt8 - amountForInt) * atmoFactor;
      float atmoValue12 = amountForInt9 + (amountForInt10 - amountForInt9) * atmoFactor;
      return atmoValue11 + (atmoValue12 - atmoValue11) * atmoFactor2;
   }

   public static float getAmount(int atmoValue, int atmoValue2) {
      return (getValue(atmoValue ^ atmoValue2 * 1663821227 + 1540483477) >>> 8) * 5.9604645E-8F;
   }

   private static float getAmountForInt(int atmoValue, int atmoValue2) {
      return (getValue(atmoValue * 668265263 + atmoValue2 * 1640531527 + 374761393) >>> 8) * 5.9604645E-8F;
   }

   public static int getValue(int atmoValue, int atmoValue2) {
      return getValue(atmoValue * -1640531535 + getValue(atmoValue2));
   }

   public static float getAmount(double atmoValue) {
      double floor = Math.floor(atmoValue);
      int floor3 = (int)((long)floor);
      float atmoFactor = getAmount((float)(atmoValue - floor));
      float atmoValue4 = getAmount(floor3);
      float atmoValue5 = getAmount(floor3 + 1);
      return atmoValue4 + (atmoValue5 - atmoValue4) * atmoFactor;
   }

   public static int getValue(int atmoValue) {
      atmoValue ^= atmoValue >>> 16;
      atmoValue *= 2146121005;
      atmoValue ^= atmoValue >>> 15;
      atmoValue *= -2073254261;
      return atmoValue ^ atmoValue >>> 16;
   }

   static float getAmount(float atmoFactor) {
      return atmoFactor * atmoFactor * atmoFactor * (atmoFactor * (atmoFactor * 6.0F - 15.0F) + 10.0F);
   }

   public static float getAmount(float atmoValue, float atmoValue2, float petalsSnowValue) {
      float atmoLimit = (petalsSnowValue - atmoValue) / (atmoValue2 - atmoValue);
      float atmoFactor = atmoLimit < 0.0F ? 0.0F : (atmoLimit > 1.0F ? 1.0F : atmoLimit);
      return atmoFactor * atmoFactor * (3.0F - 2.0F * atmoFactor);
   }

   private static float getAmount(int atmoValue) {
      return (getValue(atmoValue * 668265263 + 374761393) >>> 8) * 5.9604645E-8F;
   }

   private AmountAtmoUtils() {
   }
}
