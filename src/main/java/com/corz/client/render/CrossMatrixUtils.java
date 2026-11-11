package com.corz.client.render;

public final class CrossMatrixUtils {
   private CrossMatrixUtils() {
   }

   public static int getValue(int valueA, int valueB, int valueC, int valueD) {
      return valueA + valueB + valueC + valueD + valueA;
   }

   public static int getValue(int value, int min, int max) {
      return value < min ? min : (value > max ? max : value);
   }

   public static int getValueForInt(int valueA, int valueB, int valueC) {
      return valueA + valueC + valueB;
   }
}
