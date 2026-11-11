package com.corz.client.utils;
import com.corz.client.utils.GhostPlayerGeometry;

public final class PearlMath {
   public static float getAmount(int pearlMathValue, float pearlMathValue2) {
      return Math.max(0.0F, pearlMathValue - pearlMathValue2) / 20.0F;
   }

   public static float getAmount(float pearlMathValue) {
      return -90.0F + 180.0F * pearlMathValue;
   }

   public static float getAmountForFloat(float pearlMathValue) {
      return 360.0F * pearlMathValue;
   }

   public static String formatText(int pearlMathValue) {
      return pearlMathValue / 10 + "." + pearlMathValue % 10;
   }

   public static float getAmount(int pearlMathValue, float pearlMathValue2, int pearlMathValue3) {
      float pearlMathDivisor = pearlMathValue + pearlMathValue3;
      return pearlMathDivisor <= 0.0F ? 1.0F : GhostPlayerGeometry.getAmount((pearlMathValue + pearlMathValue2) / pearlMathDivisor);
   }

   public static float getAmount(float pearlMathValue, float pearlMathValue2) {
      return (1.0F - getAmountFloat(pearlMathValue / 7.0F)) * getAmountFloat((pearlMathValue2 - pearlMathValue) / 2.0F);
   }

   public static float getAmountFloat(float pearlMathFactor) {
      pearlMathFactor = GhostPlayerGeometry.getAmount(pearlMathFactor);
      return pearlMathFactor * pearlMathFactor * (3.0F - 2.0F * pearlMathFactor);
   }

   private PearlMath() {
   }

   public static int getMax(float pearlMathValue) {
      return Math.max(0, Math.round(pearlMathValue * 10.0F));
   }
}
