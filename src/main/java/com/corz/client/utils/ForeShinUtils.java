package com.corz.client.utils;

public final class ForeShinUtils {
   public static int getValue(int foreShinValue, float foreShinFactor) {
      return getValue(getValue6(foreShinValue), Math.round(getValueInt(foreShinValue) * foreShinFactor), Math.round(getValue4(foreShinValue) * foreShinFactor), Math.round(getValue5(foreShinValue) * foreShinFactor));
   }

   private static int getValue(int foreShinLimit) {
      return foreShinLimit < 0 ? 0 : (foreShinLimit > 255 ? 255 : foreShinLimit);
   }

   public static int getValue(int foreShinValue, int foreShinLimit, int foreShinValue3) {
      return 0xFF000000 | getValue(foreShinValue) << 16 | getValue(foreShinLimit) << 8 | getValue(foreShinValue3);
   }

   public static int getValue(int foreShinValue, int foreShinLimit, int foreShinValue3, int foreShinValue4) {
      return getValue(foreShinValue) << 24 | getValue(foreShinLimit) << 16 | getValue(foreShinValue3) << 8 | getValue(foreShinValue4);
   }

   public static int getValueForInt(int foreShinValue) {
      return 0xFF000000 | foreShinValue & 16777215;
   }

   public static int getValueForIntFloat(int foreShinValue, float bonecaAmbalabuLimit) {
      return getValue(Math.round(bonecaAmbalabuLimit * 255.0F)) << 24 | foreShinValue & 16777215;
   }

   public static int getValueInt(int foreShinValue) {
      return foreShinValue >> 16 & 0xFF;
   }

   public static int getValue4(int foreShinValue) {
      return foreShinValue >> 8 & 0xFF;
   }

   public static int getValue(int foreShinValue, int bonecaAmbalabuValue, float foreShinFactor) {
      float foreShinFactor2 = getValue6(bonecaAmbalabuValue) / 255.0F * foreShinFactor;
      if (foreShinFactor2 <= 0.0F) {
         return foreShinValue;
      } else if (foreShinFactor2 >= 1.0F) {
         return bonecaAmbalabuValue | 0xFF000000;
      } else {
         float foreShinFactor3 = getValue6(foreShinValue) / 255.0F;
         float foreShinDivisor = foreShinFactor2 + foreShinFactor3 * (1.0F - foreShinFactor2);
         if (foreShinDivisor <= 0.0F) {
            return 0;
         } else {
            float foreShinFactor4 = foreShinFactor3 * (1.0F - foreShinFactor2);
            int round = Math.round((getValueInt(bonecaAmbalabuValue) * foreShinFactor2 + getValueInt(foreShinValue) * foreShinFactor4) / foreShinDivisor);
            int foreShinLimit = Math.round((getValue4(bonecaAmbalabuValue) * foreShinFactor2 + getValue4(foreShinValue) * foreShinFactor4) / foreShinDivisor);
            int round5 = Math.round((getValue5(bonecaAmbalabuValue) * foreShinFactor2 + getValue5(foreShinValue) * foreShinFactor4) / foreShinDivisor);
            return getValue(Math.round(foreShinDivisor * 255.0F), round, foreShinLimit, round5);
         }
      }
   }

   private ForeShinUtils() {
   }

   public static int getValue5(int foreShinValue) {
      return foreShinValue & 0xFF;
   }

   public static int getValue6(int foreShinValue) {
      return foreShinValue >>> 24;
   }

   public static int getValue2(int foreShinValue, int valueForInt, float foreShinFactor) {
      foreShinFactor = foreShinFactor < 0.0F ? 0.0F : (foreShinFactor > 1.0F ? 1.0F : foreShinFactor);
      return getValue(
         Math.round(getValue6(foreShinValue) + (getValue6(valueForInt) - getValue6(foreShinValue)) * foreShinFactor),
         Math.round(getValueInt(foreShinValue) + (getValueInt(valueForInt) - getValueInt(foreShinValue)) * foreShinFactor),
         Math.round(getValue4(foreShinValue) + (getValue4(valueForInt) - getValue4(foreShinValue)) * foreShinFactor),
         Math.round(getValue5(foreShinValue) + (getValue5(valueForInt) - getValue5(foreShinValue)) * foreShinFactor)
      );
   }
}
