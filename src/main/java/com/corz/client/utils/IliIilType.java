package com.corz.client.utils;

public enum IliIilType {
   N(0, -1),
   NE(1, -1),
   E(1, 0),
   SE(1, 1),
   S(0, 1),
   SW(-1, 1),
   W(-1, 0),
   NW(-1, -1);
   private static final IliIilType[] $VALUES = values();
   public final int directionX;
   public final int degreesValue;

   public boolean isFlag() {
      return this.directionX == 0 || this.degreesValue == 0;
   }

   public IliIilType getIilIliType(int n) {
      return $VALUES[Math.floorMod(this.ordinal() + n, $VALUES.length)];
   }

   public static IliIilType getIilIliType(double d) {
      double limit = (d % 360.0 + 360.0) % 360.0;
      if (limit > 315.0 || limit <= 45.0) {
         return S;
      } else if (limit <= 135.0) {
         return W;
      } else {
         return limit <= 225.0 ? N : E;
      }
   }

   public static IliIilType getIilIliTypeForDouble(double d) {
      return getIilIliType(d + 180.0);
   }

   private IliIilType(int value, int value2) {
      this.directionX = value;
      this.degreesValue = value2;
   }
}
