package com.corz.client.utils;

public final class BlinkFlagUtils {

   public static boolean canFlag(boolean blinkFlag, int blinkThreshold, int currentBlinkCount) {
      return blinkFlag && blinkThreshold > 0 && currentBlinkCount >= blinkThreshold;
   }

   public static boolean canFlag(double startX, double startY, double endX, double endY) {
      double deltaX = endX - startX;
      double deltaY = endY - startY;
      return deltaX * deltaX + deltaY * deltaY >= 0.09;
   }

   public static double[] getAmounts(int index, float degrees) {
      int sign = index % 2 == 0 ? -1 : 1;
      double angle = Math.toRadians(degrees);
      return new double[]{0.2 * sign * Math.cos(angle), 0.2 * sign * Math.sin(angle)};
   }

   public static boolean canFlag(int blinkLimit) {
      return blinkLimit > 0 && blinkLimit % 4 == 0;
   }

   public static boolean canFlag(long start, long end, long minGap) {
      return end - start >= minGap;
   }

   private BlinkFlagUtils() {
   }
}
