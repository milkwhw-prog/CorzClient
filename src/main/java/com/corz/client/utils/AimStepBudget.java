package com.corz.client.utils;

public final class AimStepBudget {
   private final double targetBudget;
   private double targetBudgetUsed;
   private final double maxBudget;
   private double maxBudgetUsed;

   public int consumeMaxBudget(int assistValue, double assistFactor) {
      int clampedSteps = clampStepsToBudget(assistValue, assistFactor, this.maxBudget - this.maxBudgetUsed);
      this.maxBudgetUsed = this.maxBudgetUsed + Math.abs(clampedSteps) * assistFactor;
      return clampedSteps;
   }

   private static int clampStepsToBudget(int assistValue, double assistLimit, double remainingBudget) {
      if (assistValue != 0 && assistLimit > 0.0 && !(remainingBudget <= 0.0)) {
         long assistBound = (long)Math.floor(remainingBudget / assistLimit + 1.0E-9);
         int minAbs = (int)Math.min(Math.abs((long)assistValue), Math.max(0L, assistBound));
         return assistValue < 0 ? -minAbs : minAbs;
      } else {
         return 0;
      }
   }

   public AimStepBudget(double targetBudget, double maxBudget) {
      this.targetBudget = targetBudget;
      this.maxBudget = maxBudget;
   }

   public int consumeTargetBudget(int assistValue, double assistFactor) {
      int clampedSteps = clampStepsToBudget(assistValue, assistFactor, this.targetBudget - this.targetBudgetUsed);
      this.targetBudgetUsed = this.targetBudgetUsed + Math.abs(clampedSteps) * assistFactor;
      return clampedSteps;
   }

   public void resetBudgets() {
      this.targetBudgetUsed = 0.0;
      this.maxBudgetUsed = 0.0;
   }
}
