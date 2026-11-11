package com.corz.client.gui;

public final class CursorScreenUtils {

   public static int getValue(double rawFraction) {
      double clampedFraction = Double.isNaN(rawFraction) ? 0.0 : Math.max(0.0, Math.min(0.999999, rawFraction));
      return 4 + (int)(clampedFraction * 5.0);
   }

   public static int getValue(int baseSize, double rawFraction) {
      double clampedFraction = Double.isNaN(rawFraction) ? 0.5 : Math.max(0.0, Math.min(1.0, rawFraction));
      double scaledSize = Math.max(0, baseSize) * (0.8 + 0.4 * clampedFraction);
      double flooredSize = Math.max(100.0, scaledSize);
      return (int)Math.ceil(flooredSize / 50.0 - 1.0E-9);
   }

   private CursorScreenUtils() {
   }
}
