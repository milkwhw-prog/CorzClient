package com.corz.client.utils;

public final class ActionPickRules {
   public static final int UNIT_VALUE = 1;

   public static double getStepCostTicks(ActionPickRules.CrystalStepKind crystalStepKind, double constValue, int constBound) {
      return Math.max(getDelayBucket(constValue) + getKindExtraTicks(crystalStepKind), (double)Math.max(0, constBound));
   }

   public static boolean isWorthExecuting(boolean constFlag, double constLimit, boolean constFlag2, boolean crystalFlag) {
      return constFlag && Double.isFinite(constLimit) && (constLimit > 0.25 || constFlag2 || crystalFlag);
   }

   public static double getValueUnlessFlagged(boolean constFlag, double constValue) {
      return constFlag ? 0.0 : constValue;
   }

   public static boolean isWithinTickWindow(int constValue, int constBound) {
      return constValue - Math.max(0, constBound) <= 4;
   }

   public static ActionPickRules.ActionChoice pickActionPreferAnchor(
      boolean crystalAnchorFlag,
      boolean crystalAnchorFlag2,
      boolean crystalAnchorFlag3,
      boolean constFlag,
      boolean crystalAnchorFlag5,
      boolean crystalAnchorFlag6,
      double crystalAnchorLimit,
      boolean worthExecuting,
      double crystalAnchorValue,
      double distanceTo,
      boolean crystalAnchorFlag8,
      boolean crystalAnchorFlag9,
      boolean crystalAnchorFlag10,
      boolean crystalAnchorFlag11
   ) {
      if (crystalAnchorFlag8 && crystalAnchorFlag9) {
         return ActionPickRules.ActionChoice.MELEE;
      } else if (crystalAnchorFlag) {
         return constFlag && crystalAnchorFlag5 && crystalAnchorFlag2 && crystalAnchorLimit >= crystalAnchorValue ? ActionPickRules.ActionChoice.CRYSTAL : ActionPickRules.ActionChoice.ANCHOR;
      } else if (!crystalAnchorFlag10 || !(distanceTo > 3.0) || constFlag && crystalAnchorFlag5) {
         if (worthExecuting && crystalAnchorFlag3) {
            return ActionPickRules.ActionChoice.ANCHOR;
         } else if (!constFlag || !crystalAnchorFlag5 && worthExecuting && !(1.3 * crystalAnchorLimit >= crystalAnchorValue)) {
            if (!crystalAnchorFlag6 || worthExecuting && !(1.3 * crystalAnchorLimit >= crystalAnchorValue)) {
               if (worthExecuting) {
                  return ActionPickRules.ActionChoice.ANCHOR;
               } else if (constFlag) {
                  return ActionPickRules.ActionChoice.CRYSTAL;
               } else if (crystalAnchorFlag8) {
                  return ActionPickRules.ActionChoice.MELEE;
               } else {
                  return crystalAnchorFlag11 && distanceTo > 3.0 ? ActionPickRules.ActionChoice.PEARL : ActionPickRules.ActionChoice.MOVE;
               }
            } else {
               return ActionPickRules.ActionChoice.MOVE;
            }
         } else {
            return ActionPickRules.ActionChoice.CRYSTAL;
         }
      } else {
         return ActionPickRules.ActionChoice.PEARL;
      }
   }

   public static boolean fitsWithinTickBudget(ActionPickRules.CrystalStepKind crystalStepKind, double constValue, int constBound, int constLimit) {
      return constLimit > 0 && Double.isFinite(constValue) && getStepCostTicks(crystalStepKind, constValue, constBound) + 1.0 <= constLimit;
   }

   public static boolean isDamageSufficient(boolean constFlag, boolean constFlag2, float constLimit, double groundY, boolean constFlag3, boolean constFlag4, double constValue) {
      if (constFlag) {
         return true;
      } else if (!constFlag3 && (!constFlag4 || constFlag2)) {
         return constFlag2 || Float.isFinite(constLimit) && constLimit >= 2.0F
            ? Double.isFinite(groundY) && (!Double.isFinite(constValue) || groundY >= 0.5 * constValue)
            : false;
      } else {
         return false;
      }
   }

   public static boolean canMeleeMove(ActionPickRules.ActionChoice actionChoice, boolean meleeMoveFlag, boolean meleeMoveFlag2, boolean meleeMoveFlag3) {
      return meleeMoveFlag2 && meleeMoveFlag3 && actionChoice != null
         ? actionChoice == ActionPickRules.ActionChoice.MOVE || actionChoice == ActionPickRules.ActionChoice.MELEE && !meleeMoveFlag
         : false;
   }

   public static boolean canBreakPlace(ActionPickRules.CrystalStepKind crystalStepKind, boolean constFlag, boolean constFlag2, boolean constFlag3) {
      return constFlag && constFlag2 && !constFlag3 && (crystalStepKind == ActionPickRules.CrystalStepKind.BREAK || crystalStepKind == ActionPickRules.CrystalStepKind.PLACE);
   }

   public static boolean isMarginAcceptable(double constValue, double constLimit, double constValue2) {
      return !Double.isNaN(constValue) && !Double.isNaN(constLimit) ? constValue <= -0.5 || constLimit >= constValue2 : false;
   }

   public static boolean shouldForceCrystal(
      boolean constFlag, boolean constFlag2, boolean constFlag3, boolean constFlag4, boolean constFlag5, boolean worthExecuting, boolean constFlag7
   ) {
      if (!constFlag4 || !constFlag5) {
         return false;
      } else {
         return constFlag && !constFlag2 ? false : !constFlag3 || !worthExecuting || !constFlag7;
      }
   }

   public static double getDistanceFalloff(double constValue, double vecHashValue) {
      if (!Double.isNaN(constValue) && !Double.isNaN(vecHashValue)) {
         double constValue3 = vecHashValue - constValue - 0.5;
         return constValue3 <= 0.0 ? 1.0 : 1.0 / (1.0 + 0.5 * constValue3);
      } else {
         return 1.0;
      }
   }

   public static int countTicksUntilLanding(boolean constFlag, double constValue, double groundY) {
      if (!constFlag && Double.isFinite(constValue) && Double.isFinite(groundY) && !(groundY < 0.75)) {
         double groundY2 = groundY;
         double constValue2 = constValue;

         int constCounter;
         for (constCounter = 0; constCounter < 40; constCounter++) {
            groundY2 += constValue2;
            constValue2 = (constValue2 - 0.08) * 0.98;
            if (groundY2 < 0.75) {
               break;
            }
         }

         return constCounter;
      } else {
         return 0;
      }
   }

   public static int getKindExtraTicks(ActionPickRules.CrystalStepKind crystalStepKind) {
      if (crystalStepKind == null) {
         return 0;
      } else {
         return switch (crystalStepKind) {
            case BREAK, WAIT -> 0;
            case PLACE -> 1;
            case OBSIDIAN -> 2;
         };
      }
   }

   public static double computeStepScore(
      ActionPickRules.CrystalStepKind crystalStepKind, float constBound, float routedPartialValue, float constBound3, boolean constFlag, boolean constFlag2, boolean constFlag3, boolean constFlag4, double constValue, int constValue2
   ) {
      if (crystalStepKind != null && Float.isFinite(constBound)) {
         double maxSelfCost = Math.max(0.0F, constBound);
         double constValue3 = Float.isFinite(routedPartialValue) ? Math.max(0.0F, routedPartialValue) : 0.0;
         double constValue4 = Float.isFinite(constBound3) ? Math.max(0.0F, constBound3) : constValue3;
         double constCounter = maxSelfCost + (constFlag ? 4.0 : 0.0) + (constFlag2 ? 10.0 : 0.0) - 0.5 * constValue4 + (constFlag4 ? 3.0 : 0.0);
         if (crystalStepKind == ActionPickRules.CrystalStepKind.BREAK || crystalStepKind == ActionPickRules.CrystalStepKind.WAIT) {
            constCounter++;
            if (!constFlag3) {
               constCounter += 0.5 * Math.max(0.0, constValue3 - constValue4);
            }
         }

         return constCounter / (1.0 + getStepCostTicks(crystalStepKind, constValue, constValue2));
      } else {
         return Double.NEGATIVE_INFINITY;
      }
   }

   public static boolean isAlternativeBetter(double constValue, boolean constFlag, boolean constFlag2, double constLimit, boolean constFlag3, boolean constFlag4) {
      if (!Double.isFinite(constValue)) {
         return true;
      } else if (!Double.isFinite(constLimit)) {
         return false;
      } else if (constFlag4 && !constFlag2) {
         return true;
      } else {
         return constFlag3 && !constFlag && !constFlag2 ? true : constLimit >= constValue + Math.max(1.0, 0.5 * Math.abs(constValue));
      }
   }

   public static boolean canBreak(ActionPickRules.CrystalStepKind crystalStepKind, float constLimit, float constValue, float routedPartialValue, boolean constFlag, boolean ehpTarget) {
      boolean constFlag3 = Float.isFinite(constLimit) && constLimit >= 0.5F;
      if (crystalStepKind != ActionPickRules.CrystalStepKind.BREAK || constFlag) {
         return constFlag3;
      } else {
         return Float.isFinite(constValue) && Float.isFinite(routedPartialValue) ? constFlag3 && (ehpTarget || constLimit >= routedPartialValue) || constValue - routedPartialValue >= 1.0F : false;
      }
   }

   public static double getNearbyValueOrNaN(boolean constFlag, double constValue, double constLimit, double vecHashValue) {
      return !constFlag && constValue < 0.0 && !Double.isNaN(constLimit) && !Double.isNaN(vecHashValue) && vecHashValue < constLimit && !(constLimit - vecHashValue > 4.0) ? vecHashValue : Double.NaN;
   }

   public static boolean isAnyFlagSet(boolean constFlag, boolean constFlag2, boolean constFlag3, boolean constFlag4, boolean constFlag5) {
      return constFlag || constFlag2 || constFlag3 || constFlag4 || constFlag5;
   }

   public static double computeWaitScore(ActionPickRules.CrystalStepKind crystalStepKind, double constBound, double constValue, boolean constFlag, boolean constFlag2) {
      double constValue2 = Double.isFinite(constBound) ? Math.max(0.0, constBound) : 0.0;
      return constValue2 / (1.0 + getStepCostTicks(crystalStepKind, constValue, 0))
         + (constFlag ? 3.0 : 0.0)
         + (constFlag2 ? 1.5 : 0.0)
         + (crystalStepKind != ActionPickRules.CrystalStepKind.BREAK && crystalStepKind != ActionPickRules.CrystalStepKind.WAIT ? 0.0 : 1.5);
   }

   public static int getFloorOffset(double constValue) {
      return (int)Math.floor(constValue - 0.75);
   }

   public static ActionPickRules.ActionChoice pickActionPreferCrystal(
      boolean crystalFlag,
      boolean crystalFlag2,
      boolean crystalFlag3,
      boolean constFlag,
      boolean crystalFlag5,
      boolean crystalFlag6,
      double crystalValue,
      boolean worthExecuting,
      double crystalAnchorValue,
      double distanceTo,
      boolean crystalFlag8,
      boolean crystalFlag9,
      boolean crystalFlag10,
      boolean crystalFlag11,
      boolean crystalFlag12,
      boolean crystalFlag13
   ) {
      return (!crystalFlag8 || !crystalFlag9) && shouldForceCrystal(crystalFlag, crystalFlag2, crystalFlag3, constFlag, crystalFlag12, worthExecuting, crystalFlag13)
         ? ActionPickRules.ActionChoice.CRYSTAL
         : pickActionPreferAnchor(
            crystalFlag,
            crystalFlag2,
            crystalFlag3,
            constFlag,
            crystalFlag5,
            crystalFlag6,
            crystalValue,
            worthExecuting,
            crystalAnchorValue,
            distanceTo,
            crystalFlag8,
            crystalFlag9,
            crystalFlag10,
            crystalFlag11
         );
   }

   public static boolean isDamageNegligible(boolean constFlag, boolean constFlag2, double constLimit) {
      return constFlag && constFlag2 && !(constLimit > 0.25);
   }

   public static double getMinUnlessFlagged(boolean constFlag, double constValue, double constLimit) {
      return !constFlag && !Double.isNaN(constValue) && !Double.isNaN(constLimit) && !(constLimit > constValue) ? constLimit : constValue;
   }

   public static boolean isSelfCostAcceptable(boolean constFlag, int constLimit, boolean ok, float targetDamage, boolean ehpTarget) {
      return constFlag && constLimit > 0 && !ehpTarget ? !ok || !(targetDamage >= 2.0F) : false;
   }

   private ActionPickRules() {
   }

   public static double getDelayBucket(double constLimit) {
      if (Double.isNaN(constLimit) || Double.isInfinite(constLimit)) {
         return 9.0;
      } else {
         return !(constLimit > 0.0) ? 0.0 : Math.min(9.0, Math.ceil(constLimit / 20.0));
      }
   }

   public static boolean isStepCostWithinLimit(ActionPickRules.CrystalStepKind crystalStepKind, double constValue, double constLimit, int constBound) {
      return getStepCostTicks(crystalStepKind, constValue, 0) + getDelayBucket(constLimit) <= Math.max(1, constBound);
   }

   public static double[] getPointToward(double constValue, double constValue2, double constValue3, double constValue4, double constBound) {
      double hypot = Math.hypot(constValue3 - constValue, constValue4 - constValue2);
      double constFactor = hypot < 0.001 ? 1.0 : (constValue3 - constValue) / hypot;
      double constFactor2 = hypot < 0.001 ? 0.0 : (constValue4 - constValue2) / hypot;
      double constFactor3 = Double.isFinite(constBound) ? Math.max(0.0, constBound) : 0.0;
      return new double[]{constValue + constFactor * constFactor3, constValue2 + constFactor2 * constFactor3};
   }

   public static boolean isSafeToProceed(double constLimit, boolean constFlag, double constValue, float routedPartialValue, float constFactor, boolean ehpTarget, boolean constFlag3) {
      if (constFlag3) {
         return true;
      } else if (!Float.isFinite(routedPartialValue) || !Float.isFinite(constFactor) || Double.isNaN(constLimit) || Double.isNaN(constValue)) {
         return false;
      } else if (constFactor <= 2.0F || constLimit <= constValue - 0.5) {
         return true;
      } else {
         return !constFlag && constLimit > constValue + 0.5 ? false : routedPartialValue >= (ehpTarget ? 0.5 : 1.5) * constFactor;
      }
   }

   public static enum CrystalStepKind {
      BREAK,
      WAIT,
      PLACE,
      OBSIDIAN;
   }

   public static enum ActionChoice {
      CRYSTAL,
      ANCHOR,
      MELEE,
      PEARL,
      MOVE;
   }
}
