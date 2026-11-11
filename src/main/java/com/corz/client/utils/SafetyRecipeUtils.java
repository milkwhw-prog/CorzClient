package com.corz.client.utils;

import java.util.EnumSet;
import java.util.function.BiPredicate;

public final class SafetyRecipeUtils {
   public static SafetyRecipeUtils.Regime getControlInterior(double controlInteriorValue, double controlInteriorValue2, double d4, double d5, BiPredicate<Integer, Integer> biPredicate) {
      if (!canFlag(controlInteriorValue - 0.32 + d4, controlInteriorValue + 0.32 + d4, controlInteriorValue2 - 0.32 + d5, controlInteriorValue2 + 0.32 + d5, biPredicate)) {
         return SafetyRecipeUtils.Regime.EDGE_CONTROL;
      } else {
         return !canFlag(controlInteriorValue - 1.3, controlInteriorValue + 1.3, controlInteriorValue2 - 1.3, controlInteriorValue2 + 1.3, biPredicate) ? SafetyRecipeUtils.Regime.FRONTIER_WALK : SafetyRecipeUtils.Regime.SAFE_INTERIOR;
      }
   }

   public static EnumSet<SafetyRecipeUtils.Reason> getSafetyRecipe(boolean safetyRecipeFlag, boolean safetyRecipeFlag2, boolean safetyRecipeFlag3, boolean safetyRecipeFlag4, boolean safetyRecipeFlag5) {
      EnumSet noneOf = EnumSet.noneOf(SafetyRecipeUtils.Reason.class);
      if (safetyRecipeFlag5) {
         return noneOf;
      } else {
         if (safetyRecipeFlag) {
            noneOf.add(SafetyRecipeUtils.Reason.EDGE_SAFETY);
         }

         if (safetyRecipeFlag2) {
            noneOf.add(SafetyRecipeUtils.Reason.INTERACTIVE_SUPPORT);
         }

         if (safetyRecipeFlag3) {
            noneOf.add(SafetyRecipeUtils.Reason.PLACEMENT_RECIPE);
         }

         if (safetyRecipeFlag4) {
            noneOf.add(SafetyRecipeUtils.Reason.TEMP_SCAFFOLD_SAFETY);
         }

         return noneOf;
      }
   }

   public static boolean canFlag(double safetyRecipeValue, double safetyRecipeValue2, double hashRegistryValue, double safetyRecipeValue4, BiPredicate<Integer, Integer> biPredicate) {
      for (int i = (int)Math.floor(safetyRecipeValue); i <= Math.floor(safetyRecipeValue2 - 1.0E-7); i++) {
         for (int j = (int)Math.floor(hashRegistryValue); j <= Math.floor(safetyRecipeValue4 - 1.0E-7); j++) {
            if (!biPredicate.test(i, j)) {
               return false;
            }
         }
      }

      return true;
   }

   public static enum Reason {
      EDGE_SAFETY,
      INTERACTIVE_SUPPORT,
      PLACEMENT_RECIPE,
      TEMP_SCAFFOLD_SAFETY;
   }

   public static enum Regime {
      SAFE_INTERIOR,
      FRONTIER_WALK,
      EDGE_CONTROL;
   }
}
