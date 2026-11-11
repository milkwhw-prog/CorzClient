package com.corz.client.utils;
import com.corz.client.utils.EntityHeightSamples;

public final class HeightDeltaMath {

   public static double getUnreachableFraction(double fromHeight, double toHeight) {
      return !Double.isNaN(fromHeight) && !Double.isNaN(toHeight) ? 1.0 - EntityHeightSamples.getReachingFraction(fromHeight, toHeight) : 0.0;
   }

   public static double getHeightDeltaCost(double constDeflectLimit) {
      if (Double.isNaN(constDeflectLimit)) {
         return 6.0;
      } else if (constDeflectLimit > 0.5) {
         return 20.0;
      } else if (constDeflectLimit > -0.5) {
         return 6.0;
      } else if (constDeflectLimit > -1.5) {
         return 1.0;
      } else if (constDeflectLimit > -2.5) {
         return 0.0;
      } else {
         return constDeflectLimit >= -3.5 ? 4.0 : 20.0;
      }
   }

   public static HeightDeltaMath.Choice chooseApproachMethod(double walkNoneLimit, int walkNoneBound, int rocketsBuyBound, boolean walkNoneFlag, boolean lowAnchorFlag, boolean walkNoneFlag3) {
      if (!walkNoneFlag) {
         return HeightDeltaMath.Choice.NONE;
      } else {
         boolean walkNoneFlag4 = Double.isFinite(walkNoneLimit) && walkNoneLimit >= 0.0;
         double walkNoneValue = Math.max(0, walkNoneBound) + Math.max(0, rocketsBuyBound) + 1.0;
         double walkNoneValue2 = lowAnchorFlag ? 2.0 : 6.0;
         if (walkNoneFlag4 && walkNoneLimit <= walkNoneValue + walkNoneValue2) {
            return HeightDeltaMath.Choice.WALK;
         } else if (!walkNoneFlag3) {
            return walkNoneFlag4 ? HeightDeltaMath.Choice.WALK : HeightDeltaMath.Choice.NONE;
         } else {
            return HeightDeltaMath.Choice.PEARL;
         }
      }
   }

   public static double getHeightDeltaScore(double constDeflectLimit, boolean constDeflectFlag) {
      if (Double.isNaN(constDeflectLimit)) {
         return 0.0;
      } else if (constDeflectLimit > 0.5) {
         return -4.0 * constDeflectLimit;
      } else if (constDeflectLimit < -3.5) {
         return -4.0;
      } else {
         double constDeflectTotal = getUnreachableFraction(constDeflectLimit, 0.0) * 3.0;
         if (constDeflectLimit <= -0.5 && constDeflectLimit > -1.5) {
            constDeflectTotal += 0.4;
         }

         if (constDeflectLimit <= -2.5 && !constDeflectFlag) {
            constDeflectTotal--;
         }

         return constDeflectTotal;
      }
   }

   public static boolean isRiseWithinReach(double constDeflectValue, double constDeflectValue2, double vecHashValue) {
      if (!Double.isNaN(constDeflectValue) && !Double.isNaN(constDeflectValue2) && !Double.isNaN(vecHashValue)) {
         double constDeflectLimit = constDeflectValue2 - constDeflectValue;
         return constDeflectLimit >= 0.5 && constDeflectLimit <= 3.0 && vecHashValue <= 4.5;
      } else {
         return false;
      }
   }

   public static int estimateFallTicks(boolean constDeflectFlag, double constDeflectValue, double groundY) {
      if (!constDeflectFlag && Double.isFinite(constDeflectValue) && Double.isFinite(groundY) && !(groundY < 0.0)) {
         double constDeflectTotal = 0.0;
         double constDeflectValue2 = constDeflectValue;

         int constDeflectCounter;
         for (constDeflectCounter = 0; constDeflectCounter < 40; constDeflectCounter++) {
            constDeflectTotal += constDeflectValue2;
            constDeflectValue2 = (constDeflectValue2 - 0.08) * 0.98;
            if (constDeflectTotal <= -groundY) {
               break;
            }
         }

         return constDeflectCounter;
      } else {
         return 0;
      }
   }

   public static boolean isTargetAboveStepHeight(double constDeflectLimit) {
      return constDeflectLimit > 0.5;
   }

   public static double getDistanceWeightedReachFraction(double constDeflectValue, double constDeflectValue2, double vecHashValue) {
      if (!Double.isNaN(constDeflectValue) && !Double.isNaN(constDeflectValue2) && !Double.isNaN(vecHashValue)) {
         double constDeflectFactor = 1.0 - constDeflectValue2 / 12.0;
         return constDeflectFactor <= 0.0 ? 0.0 : constDeflectFactor * EntityHeightSamples.getReachingFraction(constDeflectValue, vecHashValue);
      } else {
         return 0.0;
      }
   }

   private HeightDeltaMath() {
   }

   public static int getClampedHeightLevel(double constDeflectValue) {
      return Double.isNaN(constDeflectValue) ? 0 : (int)Math.max(-3L, Math.min(3L, Math.round(constDeflectValue)));
   }

   public static enum Choice {
      WALK,
      PEARL,
      NONE;
   }
}
