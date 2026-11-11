package com.corz.client.utils;

public final class BoolEquivalenceLogic {
   public static boolean isImplied(boolean antecedent, boolean consequent) {
      return !antecedent || consequent;
   }

   private BoolEquivalenceLogic() {
   }

   public static boolean areDifferent(boolean first, boolean second) {
      return first != second;
   }
}
