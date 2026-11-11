package com.corz.client.utils;

public final class LagFakeUtilsAmount {
   public static double getAmount(double lagFakeValue, double lagFakeBound, double lagFakeValue3, double lagFakeValue4, double lagFakeLimit, double lagFakeValue6, double widthScaled, double height) {
      double lagFakeValue9 = getAmount(lagFakeValue, lagFakeValue4 - widthScaled, lagFakeValue4 + widthScaled);
      double lagFakeValue10 = getAmount(lagFakeBound, lagFakeLimit, lagFakeLimit + height);
      double lagFakeValue11 = getAmount(lagFakeValue3, lagFakeValue6 - widthScaled, lagFakeValue6 + widthScaled);
      double lagFakeFactor = lagFakeValue - lagFakeValue9;
      double lagFakeFactor2 = lagFakeBound - lagFakeValue10;
      double lagFakeFactor3 = lagFakeValue3 - lagFakeValue11;
      return Math.sqrt(lagFakeFactor * lagFakeFactor + lagFakeFactor2 * lagFakeFactor2 + lagFakeFactor3 * lagFakeFactor3);
   }

   static double getAmount(double lagFakeBound, double lagFakeLimit, double goalBound) {
      return lagFakeBound < lagFakeLimit ? lagFakeLimit : Math.min(lagFakeBound, goalBound);
   }

   private LagFakeUtilsAmount() {
   }
}
