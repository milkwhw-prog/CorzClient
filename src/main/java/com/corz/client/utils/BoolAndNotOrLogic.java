package com.corz.client.utils;

public final class BoolAndNotOrLogic {

   public static boolean isFirstOnly(boolean first, boolean second) {
      return first && !second;
   }

   public static boolean isEitherSet(boolean first, boolean second) {
      return first || second;
   }

   private BoolAndNotOrLogic() {
   }
}
