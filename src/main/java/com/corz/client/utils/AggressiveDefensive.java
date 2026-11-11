package com.corz.client.utils;

public final class AggressiveDefensive {
   private DefensiveAggressiveType candidateStance;
   private int defensiveHoldCount;
   private DefensiveAggressiveType currentStance = DefensiveAggressiveType.BALANCED;
   private int candidateStreakCount;
   private int escapeHoldCount;

   public void reset() {
      this.currentStance = DefensiveAggressiveType.BALANCED;
      this.candidateStance = DefensiveAggressiveType.BALANCED;
      this.candidateStreakCount = 0;
      this.escapeHoldCount = 0;
      this.defensiveHoldCount = 0;
   }

   private static float nanToZero(float aggressiveDefensiveValue) {
      return Float.isNaN(aggressiveDefensiveValue) ? 0.0F : aggressiveDefensiveValue;
   }

   static float getIncomingRatioLimit(CarefulBalancedType carefulBalancedType) {
      return switch (carefulBalancedType) {
         case TERMINATOR -> 1.0F;
         case BALANCED -> 0.6F;
         case CAREFUL -> 0.4F;
      };
   }

   static int getCautionLevel(CarefulBalancedType carefulBalancedType) {
      return switch (carefulBalancedType) {
         case TERMINATOR -> -1;
         case BALANCED -> 0;
         case CAREFUL -> 1;
      };
   }

   private DefensiveAggressiveType getRelaxedStance() {
      return this.candidateStance == DefensiveAggressiveType.AGGRESSIVE && this.candidateStreakCount >= 4
         ? DefensiveAggressiveType.AGGRESSIVE
         : DefensiveAggressiveType.BALANCED;
   }

   static boolean isSelfDominant(CarefulBalancedType carefulBalancedType, float terminatorLimit, int terminatorLimit2, float terminatorValue) {
      return carefulBalancedType == CarefulBalancedType.TERMINATOR ? terminatorLimit >= 20.0F && terminatorLimit2 >= 2 && terminatorValue <= 0.6F : terminatorLimit >= 24.0F && terminatorLimit2 >= 3 && terminatorValue <= 0.5F;
   }

   private DefensiveAggressiveType getTerminatorStance(AggressiveDefensive.Inputs inputs, float aggressiveDefensiveValue) {
      boolean aggressiveDefensiveFlag = inputs.selfTotems() <= 0 && inputs.enemiesWithin8() >= 2;
      this.currentStance = aggressiveDefensiveFlag ? DefensiveAggressiveType.ESCAPE : DefensiveAggressiveType.AGGRESSIVE;
      this.candidateStance = this.currentStance;
      this.candidateStreakCount = 0;
      this.escapeHoldCount = 0;
      this.defensiveHoldCount = 0;
      return this.currentStance;
   }

   public DefensiveAggressiveType decideStance(AggressiveDefensive.Inputs inputs) {
      if (inputs == null) {
         return this.currentStance;
      } else {
         CarefulBalancedType carefulBalancedType = inputs.style() == null ? CarefulBalancedType.TERMINATOR : inputs.style();
         float defensiveAggressiveBound = nanToZero(inputs.selfEhp());
         if (carefulBalancedType == CarefulBalancedType.TERMINATOR) {
            return this.getTerminatorStance(inputs, defensiveAggressiveBound);
         } else {
            int cautionLevel = getCautionLevel(carefulBalancedType);
            float defensiveAggressiveLimit = nanToZero(inputs.maxIncoming()) / Math.max(1.0F, defensiveAggressiveBound);
            float defensiveAggressiveLimit2 = nanToZero(inputs.bestHit()) / Math.max(1.0F, nanToZero(inputs.targetEhp()));
            boolean defensiveAggressiveFlag = inputs.selfTotems() == 0 && (defensiveAggressiveLimit >= getIncomingRatioLimit(carefulBalancedType) || defensiveAggressiveBound <= 8.0F)
               || inputs.enemiesWithin8() >= 2 && inputs.selfTotems() <= 1 + Math.max(0, cautionLevel);
            boolean defensiveAggressiveFlag2 = inputs.lethal() || defensiveAggressiveBound <= 8.0F + 2.0F * cautionLevel || inputs.selfTotems() <= 1 + cautionLevel || defensiveAggressiveLimit >= 0.9F - 0.15F * cautionLevel;
            boolean defensiveAggressiveFlag3 = inputs.targetAwaitingTotem()
               || defensiveAggressiveLimit2 >= 0.9F
               || inputs.targetEating()
               || inputs.targetArmorMin() <= 0.15F
               || isSelfDominant(carefulBalancedType, defensiveAggressiveBound, inputs.selfTotems(), defensiveAggressiveLimit);
            DefensiveAggressiveType defensiveAggressiveType = defensiveAggressiveFlag3 ? DefensiveAggressiveType.AGGRESSIVE : DefensiveAggressiveType.BALANCED;
            if (defensiveAggressiveType == this.candidateStance) {
               this.candidateStreakCount++;
            } else {
               this.candidateStance = defensiveAggressiveType;
               this.candidateStreakCount = 1;
            }

            if (defensiveAggressiveFlag) {
               if (this.currentStance != DefensiveAggressiveType.ESCAPE) {
                  this.escapeHoldCount = 0;
               }

               this.currentStance = DefensiveAggressiveType.ESCAPE;
               this.escapeHoldCount++;
               this.defensiveHoldCount = 0;
               return this.currentStance;
            } else if (this.currentStance == DefensiveAggressiveType.ESCAPE) {
               if (this.escapeHoldCount < 40) {
                  this.escapeHoldCount++;
                  return this.currentStance;
               } else {
                  this.escapeHoldCount = 0;
                  if (defensiveAggressiveFlag2) {
                     this.currentStance = DefensiveAggressiveType.DEFENSIVE;
                     this.defensiveHoldCount = 0;
                     return this.currentStance;
                  } else {
                     this.currentStance = this.getRelaxedStance();
                     return this.currentStance;
                  }
               }
            } else if (defensiveAggressiveFlag2) {
               this.currentStance = DefensiveAggressiveType.DEFENSIVE;
               this.defensiveHoldCount = 0;
               return this.currentStance;
            } else if (this.currentStance == DefensiveAggressiveType.DEFENSIVE) {
               this.defensiveHoldCount++;
               if (this.defensiveHoldCount < 10) {
                  return this.currentStance;
               } else {
                  this.defensiveHoldCount = 0;
                  this.currentStance = this.getRelaxedStance();
                  return this.currentStance;
               }
            } else {
               if (this.currentStance != this.candidateStance && this.candidateStreakCount >= 4) {
                  this.currentStance = this.candidateStance;
               }

               return this.currentStance;
            }
         }
      }
   }

   public AggressiveDefensive() {
      this.candidateStance = DefensiveAggressiveType.BALANCED;
   }

   public DefensiveAggressiveType getCurrentStance() {
      return this.currentStance;
   }

   public record Inputs(
      float selfEhp,
      int selfTotems,
      boolean totemConfirmed,
      float maxIncoming,
      boolean lethal,
      float bestHit,
      float targetEhp,
      boolean targetAwaitingTotem,
      boolean targetEating,
      float targetArmorMin,
      int enemiesWithin8,
      boolean pearlsAvailable,
      CarefulBalancedType style
   ) {
   }
}
