package com.corz.client.utils;

import com.corz.client.world.HimEtaData;
import net.minecraft.util.math.BlockPos;

public final class DefenseChooser {
   public static final int UNIT_VALUE = 1;

   private DefenseChooser() {
   }

   static double getShieldValue(float constFactor, float constFactor2, float constFactor3) {
      return constFactor * constFactor2 * constFactor3;
   }

   static double getDefuseValue(float constFactor, float constFactor2, float constFactor3, float constFactor4, float constFactor5, float constFactor6) {
      return constFactor * constFactor3 - constFactor2 * constFactor4 + constFactor2 * constFactor5 * constFactor6;
   }

   static int countSetFlags(boolean[] constFlags, int constLimit) {
      int constCounter = 0;
      if (constFlags == null) {
         return constLimit >= 0 ? 1 : 0;
      } else {
         for (int i = 0; i < constFlags.length; i++) {
            if (constFlags[i] || i == constLimit) {
               constCounter++;
            }
         }

         return constCounter;
      }
   }

   static int countRayHitsOnBlock(double constValue, double constValue2, double constValue3, double[][] doubles, int constValue4, int constValue5, int constValue6) {
      if (doubles == null) {
         return 0;
      } else {
         int constCounter = 0;

         for (double[] constValues : doubles) {
            if (constValues != null && doesSegmentCrossVoxel(constValue, constValue2, constValue3, constValues[0], constValues[1], constValues[2], constValue4, constValue5, constValue6)) {
               constCounter++;
            }
         }

         return constCounter;
      }
   }

   public static boolean isChargeThreatSerious(HimEtaData himEtaData, float chargesEtaValue) {
      return himEtaData != null && himEtaData.isCell()
         ? himEtaData.getDetHit() >= Math.max(2.0F, 0.25F * chargesEtaValue) || himEtaData.fullUs() >= chargesEtaValue - 0.5F || himEtaData.eta() <= 1 && himEtaData.charges() >= 1 && himEtaData.hitUs() >= 1.0F
         : false;
   }

   public static boolean hasTimeBudget(boolean constFlag, int constValue, int constLimit) {
      return constLimit > 20 && (constFlag || constValue < 16);
   }

   public static int getDistanceTier(double constValue, boolean constFlag, double constLimit) {
      if (constFlag) {
         return 0;
      } else {
         double constLimit2 = Double.isFinite(constLimit) && constLimit > 0.0 ? Math.min(constLimit, 20.0) : 20.0;
         return constValue <= constLimit2 ? 1 : (constValue <= constLimit2 + 18.0 ? 2 : 3);
      }
   }

   public static float getReducedValue(float constValue, int distanceTier) {
      return constValue - 0.75F * distanceTier;
   }

   static double[][] getBoxSamplePoints(double constValue, double constValue2, double constValue3, double constValue4, double constValue5, double constValue6) {
      double constValue7 = (constValue4 - constValue) * 0.1;
      double constValue8 = (constValue5 - constValue2) * 0.1;
      double constValue9 = (constValue6 - constValue3) * 0.1;
      double constValue10 = constValue + constValue7;
      double constValue11 = constValue4 - constValue7;
      double constValue12 = constValue2 + constValue8;
      double constValue13 = constValue5 - constValue8;
      double constValue14 = constValue3 + constValue9;
      double constValue15 = constValue6 - constValue9;
      return new double[][]{
         {(constValue + constValue4) * 0.5, (constValue2 + constValue5) * 0.5, (constValue3 + constValue6) * 0.5},
         {constValue10, constValue12, constValue14},
         {constValue11, constValue12, constValue14},
         {constValue10, constValue12, constValue15},
         {constValue11, constValue12, constValue15},
         {constValue10, constValue13, constValue14},
         {constValue11, constValue13, constValue14},
         {constValue10, constValue13, constValue15},
         {constValue11, constValue13, constValue15}
      };
   }

   public static boolean isGainSufficient(float constLimit, float constValue, float constLimit2) {
      return constLimit2 >= 0.5F && Float.isFinite(constLimit) && (constLimit >= constValue - 0.5F || constLimit >= Math.max(4.0F, 0.3F * constValue));
   }

   static boolean canShieldInTime(float constLimit, float constValue, int constLimit2, int constValue2, int constValue3) {
      return constLimit >= Math.max(1.5F, 0.4F * constValue) && constLimit2 >= constValue2 && constValue3 < 2;
   }

   public static boolean isCountLow(int constValue) {
      return constValue < 3;
   }

   static boolean doesSegmentCrossVoxel(double constValue, double constValue2, double constValue3, double constValue4, double constValue5, double constValue6, int constValue7, int constValue8, int constValue9) {
      double constBound = 0.0;
      double hypot = 1.0;
      double[] doubles = new double[]{constValue, constValue2, constValue3};
      double[] doubles2 = new double[]{constValue4 - constValue, constValue5 - constValue2, constValue6 - constValue3};
      int[] ints = new int[]{constValue7, constValue8, constValue9};

      for (int i = 0; i < 3; i++) {
         double constLimit = ints[i];
         double constValue10 = ints[i] + 1.0;
         if (Math.abs(doubles2[i]) < 1.0E-9) {
            if (doubles[i] <= constLimit || doubles[i] >= constValue10) {
               return false;
            }
         } else {
            double apiPutBound = (constLimit - doubles[i]) / doubles2[i];
            double goalBound = (constValue10 - doubles[i]) / doubles2[i];
            if (apiPutBound > goalBound) {
               double apiPutBound11 = apiPutBound;
               apiPutBound = goalBound;
               goalBound = apiPutBound11;
            }

            constBound = Math.max(constBound, apiPutBound);
            hypot = Math.min(hypot, goalBound);
            if (constBound >= hypot) {
               return false;
            }
         }
      }

      return true;
   }

   public static int getRemainingSlots(int constLimit, int constBound) {
      return constLimit >= 99 ? 3 : Math.min(3, constLimit - 1 - Math.max(0, constBound));
   }

   static int getWeightTier(double constLimit) {
      return constLimit > 25.0 ? 2 : 1;
   }

   public static boolean isWithinActionWindow(float constLimit, float constValue, int distanceTier, int intersectsLimit, int intersectsValue) {
      return distanceTier <= intersectsLimit && intersectsValue < 4 && constLimit >= Math.max(3.0F, 0.5F * constValue);
   }

   static boolean isDefuseWorthIt(float constLimit, float constLimit2, float constValue, float constValue2, double defuseCellBound, boolean constFlag, float defuseCellValue) {
      if (!Float.isFinite(constLimit) || !(defuseCellBound > 0.0)) {
         return false;
      } else if (constLimit <= 0.1F) {
         return true;
      } else {
         return constLimit >= constValue - 1.0F ? constFlag && constLimit2 >= defuseCellValue : constLimit2 >= constLimit || constLimit <= 0.6F * constValue2;
      }
   }

   static boolean isChargeWindowOpen(boolean constFlag, boolean distanceServer, int constLimit, int constLimit2) {
      return constFlag ? constLimit2 > 20 : constLimit > 0 && !distanceServer;
   }

   public static int getValue(double constValue, boolean constFlag) {
      return getDistanceTier(constValue, constFlag, 20.0);
   }

   public static DefenseChooser.DefenseChoice chooseDefense(DefenseChooser.DefenseInput in2) {
      return chooseDefenseWithThreshold(in2, Float.POSITIVE_INFINITY);
   }

   public static DefenseChooser.DefenseChoice chooseDefenseWithThreshold(DefenseChooser.DefenseInput in2, float defuseCellValue) {
      if (in2 != null && in2.a() != null && isChargeThreatSerious(in2.a(), in2.ehp())) {
         HimEtaData himEtaData = in2.a();
         DefenseChooser.Defuse defuse = in2.defuse();
         boolean defuseCellFlag = false;
         double defuseCellBound = Double.NEGATIVE_INFINITY;
         if (defuse != null && defuse.feasible() && himEtaData.charges() >= 1 && himEtaData.cell() != null) {
            defuseCellBound = getDefuseValue(in2.woff(), in2.wself(), defuse.dealt(), defuse.cost(), himEtaData.pDet(), himEtaData.hitUs());
            if (defuse.cost() <= 0.1F) {
               return new DefenseChooser.DefenseChoice(DefenseChooser.DefenseKind.DEFUSE, himEtaData.cell(), false, Math.max(0.0, defuseCellBound));
            }

            defuseCellFlag = isDefuseWorthIt(defuse.cost(), defuse.dealt(), in2.ehp(), himEtaData.hitUs(), defuseCellBound, in2.totemConfirmed(), defuseCellValue);
         }

         DefenseChooser.Shield shield = in2.shield();
         boolean defuseCellFlag2 = false;
         double defuseCellLimit = Double.NEGATIVE_INFINITY;
         if (shield != null && shield.feasible() && shield.cell() != null) {
            defuseCellLimit = getShieldValue(in2.wself(), himEtaData.pDet(), shield.gain());
            defuseCellFlag2 = canShieldInTime(shield.gain(), himEtaData.fullUs(), himEtaData.eta(), shield.prepTicks(), in2.shieldsRecent()) && defuseCellLimit > 0.0;
         }

         if (!defuseCellFlag || defuseCellFlag2 && !(defuseCellBound >= defuseCellLimit)) {
            return defuseCellFlag2 ? new DefenseChooser.DefenseChoice(DefenseChooser.DefenseKind.SHIELD, shield.cell(), shield.glowstone(), defuseCellLimit) : DefenseChooser.DefenseChoice.INSTANCE;
         } else {
            return new DefenseChooser.DefenseChoice(DefenseChooser.DefenseKind.DEFUSE, himEtaData.cell(), false, defuseCellBound);
         }
      } else {
         return DefenseChooser.DefenseChoice.INSTANCE;
      }
   }

   public record Defuse(boolean feasible, float cost, float dealt, int prepTicks) {
      public static final DefenseChooser.Defuse INSTANCE = new DefenseChooser.Defuse(false, 0.0F, 0.0F, 0);
   }

   public record DefenseInput(
      HimEtaData a, float ehp, boolean totemConfirmed, float woff, float wself, DefenseChooser.Defuse defuse, DefenseChooser.Shield shield, int shieldsRecent
   ) {
   }

   public static enum DefenseKind {
      NONE,
      DEFUSE,
      SHIELD;
   }

   public record DefenseChoice(DefenseChooser.DefenseKind kind, BlockPos cell, boolean glowstone, double ev) {
      public static final DefenseChooser.DefenseChoice INSTANCE = new DefenseChooser.DefenseChoice(DefenseChooser.DefenseKind.NONE, null, false, 0.0);
   }

   public record Shield(boolean feasible, BlockPos cell, boolean glowstone, float gain, int prepTicks) {
      public static final DefenseChooser.Shield INSTANCE = new DefenseChooser.Shield(false, null, false, 0.0F, 0);
   }
}
