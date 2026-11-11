package com.corz.client.world;

public final class VecPosUtilsDamage {

   public static double getSelfDamage(VecPosUtilsDamage.Input input, VecPosUtilsDamage.Weights weights) {
      if (!(input.selfDamage() <= input.selfBudget())) {
         return Double.NEGATIVE_INFINITY;
      } else {
         double damage = input.damage();
         double selfDamageValue = weights.aggression() * weights.off() * (damage + (input.pops() ? 8.0 : 0.0) + (input.kills() ? 100.0 : 0.0));
         double selfDamageValue2 = weights.airValue() * Math.min(Math.max(input.airGain(), 0), weights.airCap());
         return selfDamageValue
            + selfDamageValue2
            - getAmount(input.apexAboveFloor())
            + weights.toward() * input.towardDot()
            - weights.self() * input.selfDamage()
            - weights.time() * input.ticksToBlast()
            - weights.obsidian() * input.obsidianPlaces()
            - input.failRisk() * damage
            + (input.reusable() ? weights.reuse() : 0.0);
      }
   }

   public static double getAmount(double vecPosLimit) {
      if (vecPosLimit < 3.0) {
         return (3.0 - vecPosLimit) * 1.5;
      } else {
         return vecPosLimit > 7.0 ? (vecPosLimit - 7.0) * 0.6 : 0.0;
      }
   }

   private VecPosUtilsDamage() {
   }

   public record Input(
      double damage,
      boolean pops,
      boolean kills,
      int airGain,
      double apexAboveFloor,
      double towardDot,
      double selfDamage,
      double selfBudget,
      int ticksToBlast,
      int obsidianPlaces,
      double failRisk,
      boolean reusable
   ) {
   }

   public record Weights(double off, double self, double aggression, double airValue, int airCap, double toward, double time, double obsidian, double reuse) {
      public static VecPosUtilsDamage.Weights getDataForDouble(double aggression, double self, double off) {
         return new VecPosUtilsDamage.Weights(aggression, self, off, 0.25, 24, 1.0, 0.15, 1.0, 1.5);
      }
   }
}
