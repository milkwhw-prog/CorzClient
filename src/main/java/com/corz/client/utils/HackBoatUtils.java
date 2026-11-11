package com.corz.client.utils;

public final class HackBoatUtils {

   public static double[] getAmounts(double hackBoatFactor, double pressedOptionsValue, double hackBoatValue, float hackBoatFactor3) {
      double hackBoatLimit = hackBoatFactor * hackBoatFactor + pressedOptionsValue * pressedOptionsValue;
      if (hackBoatLimit < 1.0E-7) {
         return new double[]{0.0, 0.0};
      } else {
         double hackBoatFactor4 = hackBoatValue / (hackBoatLimit > 1.0 ? Math.sqrt(hackBoatLimit) : 1.0);
         double hackBoatFactor5 = hackBoatFactor * hackBoatFactor4;
         double hackBoatFactor6 = pressedOptionsValue * hackBoatFactor4;
         double sin = Math.sin(hackBoatFactor3 * (Math.PI / 180.0));
         double cos = Math.cos(hackBoatFactor3 * (Math.PI / 180.0));
         return new double[]{hackBoatFactor5 * cos - hackBoatFactor6 * sin, hackBoatFactor6 * cos + hackBoatFactor5 * sin};
      }
   }

   private HackBoatUtils() {
   }
}
