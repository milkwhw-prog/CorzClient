package com.corz.client.utils;
import com.corz.client.utils.SpawnerSculkUtils;
import com.corz.client.utils.CityMonument;
import com.corz.client.utils.ExcavationTypeStats;

public final class TimesSensorUtils {
   static final long[] CITY_BLOCK_TIMES = SpawnerSculkUtils.toBitSet(
      1, 2, 6, 30, 23, 24, 27, 40, 35, 32, 34, 43, 50, 53, 63, 65, 67, 81, 82, 85, 87, 93, 94, 101, 102, 88, 95, 139, 142
   );
   static final long[] CITY_EXTRA_TIMES = SpawnerSculkUtils.toBitSet(18, 63, 64, 50, 139, 140, 141, 147, 143);
   static final long[] SENSOR_BLOCK_TIMES = SpawnerSculkUtils.toBitSet(1, 65, 139);
   static final long[] GENERAL_BLOCK_TIMES = SpawnerSculkUtils.toBitSet(66, 88, 63, 50, 1, 32, 95, 12, 65, 139, 140, 141, 147);
   static final long[] ANCIENT_CITY_TIMES = SpawnerSculkUtils.toBitSet(1, 52, 59, 70, 71, 72, 64, 63, 87, 61, 11, 12, 13, 41, 77, 89, 99, 48, 139, 140, 147);
   static final long[] CITY_MONUMENT_TIMES = SpawnerSculkUtils.toBitSet(73, 74, 60, 59, 39, 8, 1, 2, 139, 140, 147);
   static final long[] MINESHAFT_TIMES = SpawnerSculkUtils.toBitSet(98);
   static final long[] STRONGHOLD_TIMES = SpawnerSculkUtils.toBitSet(80, 55, 100, 90, 139, 140);
   static final long[] TRAIL_RUINS_TIMES = SpawnerSculkUtils.toBitSet(66, 65, 81, 55, 1, 80, 91, 67);
   static final long[] OCEAN_RUIN_TIMES = SpawnerSculkUtils.toBitSet(63, 64, 1, 95, 139);
   static final long[] JUNGLE_TEMPLE_TIMES = SpawnerSculkUtils.toBitSet(81, 82, 20, 12, 1, 139, 141);
   static final long[] OUTPOST_TIMES = SpawnerSculkUtils.toBitSet(65, 66, 8, 10, 11, 12, 1, 139, 140, 147);
   static final long[] IGLOO_TIMES = SpawnerSculkUtils.toBitSet(63, 23, 40, 43, 139);
   static final long[] DESERT_PYRAMID_TIMES = SpawnerSculkUtils.toBitSet(87, 30, 23, 6, 50, 95, 63, 66, 88, 27, 40, 1, 11, 139, 141);
   static final long[] END_CITY_TIMES = SpawnerSculkUtils.toBitSet(64, 63, 65, 87, 85, 1, 30, 32, 43, 38, 40, 23, 50, 91, 90, 20, 95, 88, 62, 41, 66, 139, 140, 142);
   static final long[] VILLAGE_TIMES = SpawnerSculkUtils.toBitSet(64, 63, 65, 1, 38, 19, 93, 88, 50, 53, 139, 142);
   static final long[] SWAMP_HUT_TIMES = SpawnerSculkUtils.toBitSet(82, 69, 68, 66, 65, 63, 39, 67);
   static final long[] SHIPWRECK_TIMES = SpawnerSculkUtils.toBitSet(91, 66, 75, 1, 88, 90, 65);
   static final long[] OCEAN_MONUMENT_TIMES = SpawnerSculkUtils.toBitSet(76, 108, 1, 139, 140, 147);
   static final long[] FORTRESS_TIMES = SpawnerSculkUtils.toBitSet(75, 89, 53, 52, 54, 1, 90, 77, 139, 140, 147);
   static final long[] BASTION_TIMES = SpawnerSculkUtils.toBitSet(79, 58, 85, 38, 1, 27, 95, 41, 139, 142);

   public static CityMonument detectCityMonument(ExcavationTypeStats flagsHandle) {
      TimesSensorUtils.StructureScanState ctx = new TimesSensorUtils.StructureScanState();
      CityMonument cityMonument = ctx.cityMonument;
      int biomeCraftedValue = flagsHandle.biomeCraftedValue;
      boolean cityFlag = biomeCraftedValue == 0;
      boolean cityFlag2 = biomeCraftedValue == 1;
      boolean cityFlag3 = biomeCraftedValue == 2;
      boolean hasHighWorldLoadedValue = flagsHandle.hasHighWorldLoadedValue();
      cityMonument.craftedExcavationActive = flagsHandle.getTypeCount(120) > 0;
      cityMonument.monumentScore = CityMonument.sumWhenBothLimitsReachEight(flagsHandle.getTypeCount(73), flagsHandle.getTypeCount(74), flagsHandle.getTypeCount(39), flagsHandle.getTypeCount(121));
      if (cityMonument.craftedExcavationActive || cityMonument.monumentScore >= 40) {
         cityMonument.copyStructureBits(CITY_MONUMENT_TIMES);
      } else if (flagsHandle.wasTypeSeen(120)) {
         cityMonument.cityActive = true;
         cityMonument.copyStructureBits(CITY_MONUMENT_TIMES);
      }

      if (flagsHandle.getTypeCount(113) > 0) {
         cityMonument.mineshaft = true;
         ctx.applyStructureTimes(MINESHAFT_TIMES, true, false);
      } else if (flagsHandle.getTypeCount(114) >= 6 && flagsHandle.getTypeCount(115) >= 6 && flagsHandle.getTypeCount(131) + flagsHandle.getTypeCount(98) >= 4) {
         cityMonument.mineshaft = true;
         ctx.applyStructureTimes(MINESHAFT_TIMES, false, false);
      } else if (flagsHandle.wasTypeSeen(113)) {
         cityMonument.mineshaft = true;
         ctx.applyStructureTimes(MINESHAFT_TIMES, false, true);
      }

      int cityValue = flagsHandle.getTypeCount(132);
      int cityLimit = flagsHandle.getTypeCountSum(132, 133, 134, 135, 136, 138);
      if (!cityFlag || cityValue < 1 || cityLimit > 1 || flagsHandle.getTypeCount(137) < 1 && flagsHandle.getTypeCount(65) < 1) {
         if (cityFlag && flagsHandle.wasTypeSeen(132) && cityLimit == 0) {
            cityMonument.geode = true;
            ctx.applyStructureTimes(SENSOR_BLOCK_TIMES, false, true);
         }
      } else {
         cityMonument.geode = true;
         ctx.applyStructureTimes(SENSOR_BLOCK_TIMES, true, false);
      }

      int cityLimit2 = flagsHandle.getTypeCount(63) + flagsHandle.getTypeCount(64);
      if (!cityFlag || (flagsHandle.getTypeCount(122) < 2 || flagsHandle.getTypeCount(18) < 4) && flagsHandle.getTypeCount(133) < 1) {
         if (cityFlag && flagsHandle.maxWorldLoadedValue <= 90 && flagsHandle.getTypeCount(18) >= 16 && (flagsHandle.getTypeCount(122) >= 1 || cityLimit2 >= 20) && flagsHandle.getTypeCount(17) == 0) {
            cityMonument.ruinedPortal = true;
            ctx.applyStructureTimes(CITY_EXTRA_TIMES, false, false);
         } else if (cityFlag && flagsHandle.wasTypeSeen(133)) {
            cityMonument.ruinedPortal = true;
            ctx.applyStructureTimes(CITY_EXTRA_TIMES, false, true);
         }
      } else {
         cityMonument.ruinedPortal = true;
         ctx.applyStructureTimes(CITY_EXTRA_TIMES, true, false);
      }

      if (!cityFlag || flagsHandle.getTypeCount(119) <= 0 && flagsHandle.getTypeCount(128) <= 0) {
         if (!cityFlag
            || flagsHandle.maxWorldLoadedValue > 64
            || flagsHandle.getTypeCount(66) < 60
            || flagsHandle.getTypeCount(88) < 1 && flagsHandle.getTypeCount(12) < 1 && flagsHandle.getTypeCount(122) < 1 && flagsHandle.getTypeCount(32) < 6 && flagsHandle.getTypeCount(136) < 1) {
            if (cityFlag && (flagsHandle.wasTypeSeen(119) || flagsHandle.wasTypeSeen(128) || flagsHandle.wasTypeSeen(136))) {
               cityMonument.ancientCity = true;
               ctx.applyStructureTimes(GENERAL_BLOCK_TIMES, false, true);
            }
         } else {
            cityMonument.ancientCity = true;
            ctx.applyStructureTimes(GENERAL_BLOCK_TIMES, false, false);
         }
      } else {
         cityMonument.ancientCity = true;
         ctx.applyStructureTimes(GENERAL_BLOCK_TIMES, true, false);
      }

      double groupFraction = flagsHandle.getGroupFraction(2);
      if (!cityFlag || flagsHandle.getTypeCount(116) <= 0 && (!(groupFraction >= 0.5) || flagsHandle.getTypeCount(117) < 1)) {
         if (!cityFlag
            || !(groupFraction >= 0.5)
            || flagsHandle.getTypeCount(70) < 20
            || flagsHandle.getTypeCount(52) < 1 && flagsHandle.getTypeCount(59) < 1 && flagsHandle.getTypeCount(87) < 2 && flagsHandle.getTypeCount(41) < 1 && flagsHandle.getTypeCount(64) < 4) {
            if (cityFlag && flagsHandle.maxWorldLoadedValue <= 0 && (flagsHandle.wasTypeSeen(116) || flagsHandle.wasTypeSeen(117))) {
               cityMonument.cityAncientFlag = true;
               ctx.applyStructureTimes(ANCIENT_CITY_TIMES, false, true);
            }
         } else {
            cityMonument.cityAncientFlag = true;
            ctx.applyStructureTimes(ANCIENT_CITY_TIMES, false, false);
         }
      } else {
         cityMonument.cityAncientFlag = true;
         ctx.applyStructureTimes(ANCIENT_CITY_TIMES, true, false);
      }

      double groupFraction4 = flagsHandle.getGroupFraction(5);
      double groupsFraction = flagsHandle.getGroupsFraction(5, 7, 6);
      if (cityFlag && flagsHandle.getTypeCount(80) >= 40 && flagsHandle.getTypeCount(55) >= 2) {
         cityMonument.stronghold = true;
         ctx.applyStructureTimes(STRONGHOLD_TIMES, flagsHandle.getTypeCount(80) >= 200 || flagsHandle.getTypeCount(100) >= 1, false);
      }

      int typeCountSum = flagsHandle.getTypeCountSum(23, 24, 25, 26, 27, 28, 30, 31, 32, 33, 35, 36, 37, 40, 44, 45);
      int typeCountSum3 = flagsHandle.getTypeCountSum(50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62);
      int typeCountSum4 = flagsHandle.getTypeCountSum(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21);
      if (cityFlag && groupFraction4 >= 0.5 && flagsHandle.maxWorldLoadedValue <= 63 && !cityMonument.stronghold) {
         if (flagsHandle.getTypeCount(127) >= 1) {
            cityMonument.trailRuins = true;
            ctx.applyStructureTimes(TRAIL_RUINS_TIMES, true, false);
         } else if (flagsHandle.getTypeCount(1) <= 2 && typeCountSum == 0 && typeCountSum4 == 0 && flagsHandle.getTypeCountSum(66, 65, 81) >= 12) {
            cityMonument.trailRuins = true;
            ctx.applyStructureTimes(TRAIL_RUINS_TIMES, false, false);
         }
      }

      if (cityFlag && groupsFraction >= 0.5 && flagsHandle.maxWorldLoadedValue <= 72 && cityLimit2 >= 40 && flagsHandle.getTypeCount(1) <= 3 && typeCountSum3 == 0 && typeCountSum == 0 && typeCountSum4 == 0) {
         cityMonument.oceanRuin = true;
         ctx.applyStructureTimes(OCEAN_RUIN_TIMES, false, false);
      }

      double groupFraction6 = flagsHandle.getGroupFraction(8);
      if (cityFlag && groupFraction6 >= 0.5 && flagsHandle.getTypeCount(81) >= 12 && flagsHandle.getTypeCount(82) >= 4) {
         cityMonument.jungleTemple = true;
         ctx.applyStructureTimes(JUNGLE_TEMPLE_TIMES, flagsHandle.getTypeCount(20) >= 4 || flagsHandle.getTypeCount(127) >= 1, false);
      } else if (cityFlag && groupFraction6 >= 0.5 && flagsHandle.getTypeCount(127) >= 1 && flagsHandle.getTypeCount(81) >= 4) {
         cityMonument.jungleTemple = true;
         ctx.applyStructureTimes(JUNGLE_TEMPLE_TIMES, true, false);
      }

      if (cityFlag && flagsHandle.getGroupFraction(10) >= 0.5 && flagsHandle.getTypeCount(65) >= 30 && (flagsHandle.getTypeCount(8) >= 1 || flagsHandle.getTypeCount(12) >= 2 || flagsHandle.getTypeCount(10) >= 1)) {
         cityMonument.outpost = true;
         ctx.applyStructureTimes(OUTPOST_TIMES, flagsHandle.getTypeCount(8) >= 1 && flagsHandle.getTypeCount(12) >= 2, false);
      }

      if (cityFlag
         && flagsHandle.getGroupFraction(13) >= 0.5
         && flagsHandle.getTypeCount(63) >= 15
         && flagsHandle.getTypeCount(40) >= 1
         && flagsHandle.getTypeCount(23) >= 1
         && flagsHandle.getTypeCount(30) == 0
         && flagsHandle.getTypeCount(1) == 0) {
         cityMonument.igloo = true;
         ctx.applyStructureTimes(IGLOO_TIMES, flagsHandle.getTypeCount(43) >= 1, false);
      }

      double groupsFraction7 = flagsHandle.getGroupsFraction(12, 11);
      if (cityFlag && groupsFraction7 >= 0.5 && flagsHandle.getTypeCount(129) >= 30 && flagsHandle.getTypeCount(30) >= 1 && (flagsHandle.getTypeCount(6) >= 1 || flagsHandle.getTypeCount(23) >= 1)) {
         cityMonument.desertPyramid = true;
         ctx.applyStructureTimes(DESERT_PYRAMID_TIMES, flagsHandle.getTypeCount(27) >= 1 && flagsHandle.getTypeCount(40) >= 1, false);
      }

      if (cityFlag && flagsHandle.getGroupFraction(14) >= 0.4 && flagsHandle.getTypeCount(64) >= 120 && flagsHandle.getTypeCount(87) >= 10 && flagsHandle.getTypeCount(65) >= 20) {
         cityMonument.endCity = true;
         ctx.applyStructureTimes(END_CITY_TIMES, flagsHandle.getTypeCount(64) >= 300, false);
      }

      if (cityFlag && hasHighWorldLoadedValue && flagsHandle.getTypeCount(64) >= 40 && flagsHandle.getTypeCount(65) >= 10 && (flagsHandle.getTypeCount(19) >= 1 || flagsHandle.getTypeCount(38) >= 1 || flagsHandle.cityValue >= 2)) {
         cityMonument.village = true;
         ctx.applyStructureTimes(VILLAGE_TIMES, flagsHandle.cityValue >= 3 || flagsHandle.getTypeCount(19) >= 1 && flagsHandle.getTypeCount(38) >= 1, false);
      }

      if (!cityFlag || flagsHandle.getTypeCount(127) < 1 || flagsHandle.getTypeCount(69) < 3 && flagsHandle.getTypeCount(82) < 5 && flagsHandle.getTypeCount(68) < 2) {
         if (cityFlag && flagsHandle.getTypeCount(69) >= 5 && flagsHandle.getTypeCount(82) >= 8 && flagsHandle.getTypeCount(68) >= 1) {
            cityMonument.swampHut = true;
            ctx.applyStructureTimes(SWAMP_HUT_TIMES, false, false);
         }
      } else {
         cityMonument.swampHut = true;
         ctx.applyStructureTimes(SWAMP_HUT_TIMES, true, false);
      }

      if (cityFlag && hasHighWorldLoadedValue) {
         boolean cityFlag5 = flagsHandle.getGroupsFraction(16, 8, 17, 11, 12) >= 0.5;
         int typeCountSum5 = flagsHandle.getTypeCountSum(63, 64, 65, 66, 67, 68, 69, 70, 71, 73, 74, 75, 76, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 90, 91, 92);
         int typeCountSum6 = flagsHandle.getTypeCountSum(63, 65, 67, 81, 82, 85, 87);
         boolean cityFlag6 = typeCountSum5 == 0 || typeCountSum6 * 10 >= typeCountSum5 * 6;
         int cityCounter = 0;
         if (flagsHandle.getTypeCount(34) >= 1) {
            cityCounter += 4;
         }

         if (flagsHandle.entitiesWorldTier >= 2) {
            cityCounter += 2;
         }

         if (flagsHandle.entitiesWorldTier >= 4) {
            cityCounter++;
         }

         if (flagsHandle.entitiesWorldValue >= 1) {
            cityCounter++;
         }

         if (flagsHandle.getTypeCount(94) >= 6) {
            cityCounter += 2;
         } else if (flagsHandle.getTypeCount(94) >= 2) {
            cityCounter++;
         }

         if (flagsHandle.getTypeCount(30) >= 2) {
            cityCounter++;
         }

         if (flagsHandle.getTypeCount(30) >= 4) {
            cityCounter++;
         }

         if (flagsHandle.getTypeCount(24) >= 2) {
            cityCounter++;
         }

         if (flagsHandle.getTypeCount(93) >= 1) {
            cityCounter++;
         }

         if (flagsHandle.getTypeCount(101) >= 4 && flagsHandle.getTypeCount(102) >= 4) {
            cityCounter++;
         }

         if (cityFlag5) {
            cityCounter++;
         }

         if (cityFlag6) {
            cityCounter++;
         }

         boolean cityFlag7 = flagsHandle.getTypeCount(34) >= 1 || flagsHandle.getTypeCount(94) >= 2 || flagsHandle.getTypeCount(30) >= 2 || flagsHandle.entitiesWorldTier >= 2;
         if (cityFlag7 && cityCounter >= 4) {
            cityMonument.mansion = true;
            ctx.applyStructureTimes(CITY_BLOCK_TIMES, flagsHandle.getTypeCount(34) >= 1 || cityCounter >= 8, false);
         } else if (flagsHandle.wasTypeSeen(34) && cityCounter + 2 >= 4) {
            cityMonument.mansion = true;
            ctx.applyStructureTimes(CITY_BLOCK_TIMES, false, true);
         }
      }

      if (flagsHandle.getTypeCount(124) > 0) {
         cityMonument.shipwreck = true;
         ctx.applyStructureTimes(SHIPWRECK_TIMES, true, false);
      } else if (flagsHandle.getTypeCount(91) < 6
         || flagsHandle.getTypeCount(1) > 1
         || flagsHandle.getTypeCount(126) < 1
         || (cityFlag ? flagsHandle.getTypeCount(125) < 3 : flagsHandle.getTypeCount(66) < 4 && flagsHandle.getTypeCount(75) < 4)) {
         if (flagsHandle.wasTypeSeen(124)) {
            cityMonument.shipwreck = true;
            ctx.applyStructureTimes(SHIPWRECK_TIMES, false, true);
         }
      } else {
         cityMonument.shipwreck = true;
         ctx.applyStructureTimes(SHIPWRECK_TIMES, false, false);
      }

      if (!cityFlag2 || flagsHandle.getTypeCount(134) < 1 && (flagsHandle.getTypeCount(108) < 1 || flagsHandle.getTypeCount(76) < 30) && flagsHandle.getTypeCount(76) < 150) {
         if (cityFlag2 && flagsHandle.getTypeCount(76) >= 60) {
            cityMonument.monument = true;
            ctx.applyStructureTimes(OCEAN_MONUMENT_TIMES, false, false);
         } else if (cityFlag2 && flagsHandle.wasTypeSeen(134)) {
            cityMonument.monument = true;
            ctx.applyStructureTimes(OCEAN_MONUMENT_TIMES, false, true);
         }
      } else {
         cityMonument.monument = true;
         ctx.applyStructureTimes(OCEAN_MONUMENT_TIMES, true, false);
      }

      if (cityFlag2 && flagsHandle.getTypeCount(123) > 0) {
         cityMonument.fortress = true;
         ctx.applyStructureTimes(FORTRESS_TIMES, true, false);
      } else if (!cityFlag2
         || flagsHandle.getTypeCount(75) < 40
         || flagsHandle.getTypeCount(89) < 1 && flagsHandle.getTypeCount(53) < 1 && flagsHandle.getTypeCount(1) < 1 && flagsHandle.getTypeCount(135) < 1 && flagsHandle.getTypeCount(90) < 1) {
         if (cityFlag2 && (flagsHandle.wasTypeSeen(123) || flagsHandle.wasTypeSeen(135))) {
            cityMonument.fortress = true;
            ctx.applyStructureTimes(FORTRESS_TIMES, false, true);
         }
      } else {
         cityMonument.fortress = true;
         ctx.applyStructureTimes(FORTRESS_TIMES, false, false);
      }

      if (cityFlag3 && flagsHandle.getTypeCount(79) >= 30 && (flagsHandle.getTypeCount(58) >= 1 || flagsHandle.getTypeCount(38) >= 1 || flagsHandle.getTypeCount(27) >= 1)) {
         cityMonument.bastion = true;
         ctx.applyStructureTimes(BASTION_TIMES, flagsHandle.getTypeCount(79) >= 80 && flagsHandle.getTypeCount(58) >= 2, false);
      }

      cityMonument.penDivActive = ctx.cityPenaltyActive && !ctx.naturalPenaltyActive;
      return cityMonument;
   }

   private TimesSensorUtils() {
   }

   private static final class StructureScanState {
      boolean naturalPenaltyActive;
      boolean cityPenaltyActive;
      final CityMonument cityMonument = new CityMonument();

      void applyStructureTimes(long[] natPenValues, boolean natPenFlag, boolean natPenFlag2) {
         this.cityMonument.copyStructureBits(natPenValues);
         if (natPenFlag2) {
            this.cityPenaltyActive = true;
         } else {
            this.naturalPenaltyActive = true;
            this.cityMonument.penNatFlag |= natPenFlag;
         }
      }

      private StructureScanState() {
         this.naturalPenaltyActive = false;
         this.cityPenaltyActive = false;
      }
   }
}
