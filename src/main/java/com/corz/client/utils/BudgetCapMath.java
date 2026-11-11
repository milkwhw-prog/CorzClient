package com.corz.client.utils;

public final class BudgetCapMath {
   private BudgetCapMath() {
   }

   public static int getCappedBudget(int level, int perLevelAmount) {
      return Math.max(1, perLevelAmount) * Math.min(Math.max(0, level), 4);
   }

   public static boolean canAffordMore(
      int level, int spentAmount, int perLevelAmount, boolean paused, boolean disabled, boolean cancelled
   ) {
      if (level <= 0) {
         return false;
      } else if (cancelled) {
         return false;
      } else {
         return !paused && !disabled ? spentAmount < getCappedBudget(level, perLevelAmount) : false;
      }
   }

   public static boolean canFlag(int level, int spentAmount, int perLevelAmount, boolean paused, boolean disabled) {
      return canAffordMore(level, spentAmount, perLevelAmount, paused, disabled, false);
   }
}
