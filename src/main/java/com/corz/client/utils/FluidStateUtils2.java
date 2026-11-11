package com.corz.client.utils;

public final class FluidStateUtils2 {
   private static int getValue(int color1, int color2, int color3, int color4, int shift, float weight1, float weight2, float weight3, float weight4) {
      float blendedChannel = (color1 >> shift & 0xFF) * weight1 + (color2 >> shift & 0xFF) * weight2 + (color3 >> shift & 0xFF) * weight3 + (color4 >> shift & 0xFF) * weight4;
      int roundedChannel = Math.round(blendedChannel);
      return roundedChannel < 0 ? 0 : Math.min(255, roundedChannel);
   }

   private FluidStateUtils2() {
   }

   public static int getValue(int color1, int color2, int color3, int color4, float fracX, float fracY) {
      float weight1 = (1.0F - fracX) * (1.0F - fracY);
      float weight2 = fracX * (1.0F - fracY);
      float weight3 = (1.0F - fracX) * fracY;
      float weight4 = fracX * fracY;
      int red = getValue(color1, color2, color3, color4, 16, weight1, weight2, weight3, weight4);
      int green = getValue(color1, color2, color3, color4, 8, weight1, weight2, weight3, weight4);
      int blue = getValue(color1, color2, color3, color4, 0, weight1, weight2, weight3, weight4);
      return 0xFF000000 | red << 16 | green << 8 | blue;
   }
}
