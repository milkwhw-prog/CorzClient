package com.corz.client.utils;

public final class IconsClearUtilsValue2 {

   public static int getValue(int rawSize, int extraOffset, int padding) {
      return getValue(getValueForInt(rawSize)) + padding + extraOffset;
   }

   public static int getValue(int scaledSize) {
      return Math.round(0.578125F * scaledSize);
   }

   private IconsClearUtilsValue2() {
   }

   public static int getValueForInt(int displaySize) {
      return Math.round(displaySize * 0.72F / 0.578125F);
   }
}
