package com.corz.client.utils;

import com.corz.client.utils.DivPenUtilsPenalized.Result;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.corz.client.utils.CityMonument;
import com.corz.client.utils.CraftedExcavation;
import com.corz.client.utils.ExcavationCraftedType;
import com.corz.client.utils.ExcavationCluster;
import com.corz.client.utils.SpawnerSculkUtils;
import com.corz.client.utils.DivPenUtilsPenalized;
import com.corz.client.utils.DivPenUtilsFlag;
import com.corz.client.utils.DivPenUtilsConst;

public final class PenDivUtils {

   public static PenDivUtils.Decision getPenDiv(ExcavationCluster maxCrafted, CityMonument cityMonument, DivPenUtilsFlag divPenUtilsFlag) {
      if (cityMonument == null) {
         cityMonument = new CityMonument();
      }

      boolean times = cityMonument.hasNoStructureBits();
      int penDivValue = cityMonument.penNatFlag ? 0 : 1;
      int penDivTotal = 0;
      int penDivTotal2 = 0;
      int penDivTotal3 = 0;
      int penDivTotal4 = 0;
      int penDivTotal5 = 0;
      int penDivTotal6 = 0;
      int penDivTotal7 = 0;
      int penDivCounter = 0;
      int penDivCounter2 = 0;
      int penDivCounter3 = 0;
      int penDivCounter4 = 0;
      var hashSet = new HashSet();
      List<CraftedExcavation> list = times ? new ArrayList<>() : maxCrafted.getCells();
      boolean[] booleans = new boolean[148];

      for (CraftedExcavation craftedExcavation : maxCrafted.getCells()) {
         boolean penDivFlag = times && cityMonument.hasStructureBit(craftedExcavation.craftedTypeId);
         int penDivValue2 = penDivFlag ? penDivValue : 4;
         if (penDivFlag) {
            penDivCounter4++;
         } else {
            penDivCounter3++;
            if (times) {
               list.add(craftedExcavation);
            }
         }

         switch (craftedExcavation.excavationCraftedType) {
            case STORAGE:
               penDivTotal += penDivValue2;
               break;
            case UTILITY:
               penDivTotal2 += penDivValue2;
               break;
            case LIGHTING:
               penDivTotal3 += penDivValue2;
               break;
            case REDSTONE:
               penDivTotal4 += penDivValue2;
               break;
            case CRAFTED:
               penDivTotal5 += penDivValue2;
               break;
            case FARM:
               penDivTotal6 += penDivValue2;
               break;
            case EXCAVATION:
               penDivTotal7 += penDivValue2;
               break;
            case SPAWNER:
               penDivCounter++;
               if (craftedExcavation.hasFlag(8)) {
                  penDivCounter2++;
               }
         }

         if (!penDivFlag) {
            if (craftedExcavation.penDivValue != 0) {
               hashSet.add(craftedExcavation.penDivValue);
            }

            if (SpawnerSculkUtils.isBarrelId(craftedExcavation.craftedTypeId)) {
               booleans[craftedExcavation.craftedTypeId] = true;
            }
         }
      }

      int penDivValue3 = penDivCounter2 + (penDivCounter >= 2 ? penDivCounter - penDivCounter2 : 0);
      int penDivValue4 = penDivTotal2 + penDivValue3 * 4;
      int penDivLimit = penDivTotal / 4;
      int penDivLimit2 = penDivValue4 / 4;
      int penDivLimit3 = divPenUtilsFlag.penDivActive ? penDivTotal3 / 4 : 0;
      int penDivLimit4 = divPenUtilsFlag.divPenActive ? penDivTotal4 / 4 : 0;
      int penDivBound = penDivTotal5 / 4;
      int penDivLimit5 = divPenUtilsFlag.requireQuarterDensity ? penDivTotal6 / 4 : 0;
      int penDivLimit6 = divPenUtilsFlag.countQuarterTotal ? penDivTotal7 / 4 : 0;
      int intersectsCenterBound = divPenUtilsFlag.countCraftedExcavations ? DivPenUtilsConst.getCraftedExcavation((List<CraftedExcavation>)list) : 0;
      int penDivValue5 = divPenUtilsFlag.countUniqueChunks ? hashSet.size() : 0;
      boolean penDivFlag2 = divPenUtilsFlag.divPenActive && canCraftedExcavation((List<CraftedExcavation>)list);
      int penDivLimit8 = 0;

      for (boolean booleansItem : booleans) {
         if (booleansItem) {
            penDivLimit8++;
         }
      }

      int lightingFarmValue = Math.min(penDivLimit * 5, 28);
      int lightingFarmLimit = Math.min(penDivLimit2 * 8, 28);
      int min8 = Math.min(penDivLimit3 * 2, 14);
      int penDivValue9 = Math.min(penDivLimit4 * 3, 18) + (penDivFlag2 ? 8 : 0);
      int min10 = Math.min(penDivBound, 22);
      int min11 = Math.min(intersectsCenterBound, 18);
      int min12 = Math.min(penDivValue5 * 4, 12);
      int penDivValue13 = Math.min(penDivLimit5 * 2, 16) + Math.min(Math.max(penDivLimit5 - 16, 0) / 4, 8);
      int min14 = Math.min(penDivLimit6 * 2, 16);
      int min15 = Math.min(Math.max(penDivLimit - 8, 0), 20);
      int penDivValue16 = penDivLimit3 > 0 ? Math.min(Math.max(penDivLimit3 - 10, 0) / 2, 6) : 0;
      int penDivTotal8 = Math.min(penDivLimit8 * 5, 20);
      if (penDivLimit8 >= 2 && intersectsCenterBound >= 12) {
         penDivTotal8 += 6;
      }

      int min17 = Math.min(maxCrafted.getFurnaceCount() * 3, 9);
      boolean penDivFlag4 = divPenUtilsFlag.requireCraftedPresence && maxCrafted.getShulkerCount() > 0;
      int penDivValue18 = penDivFlag4 ? 10 : 0;
      int typeIdCount = maxCrafted.getTypeIdCount(146);
      boolean penDivFlag5 = typeIdCount > 0;
      int min19 = Math.min(typeIdCount * 5, 25);
      boolean penDivFlag6 = penDivFlag4 || penDivFlag5;
      int penDivCounter5 = 0;
      if (penDivLimit > 0) {
         penDivCounter5++;
      }

      if (penDivLimit2 > 0) {
         penDivCounter5++;
      }

      if (penDivLimit3 > 0) {
         penDivCounter5++;
      }

      if (penDivLimit4 > 0) {
         penDivCounter5++;
      }

      if (penDivBound > 0) {
         penDivCounter5++;
      }

      if (intersectsCenterBound > 0) {
         penDivCounter5++;
      }

      if (penDivLimit5 >= 4) {
         penDivCounter5++;
      }

      if (penDivLimit6 >= 3) {
         penDivCounter5++;
      }

      if (penDivFlag6) {
         penDivCounter5++;
      }

      int penDivLimit10 = penDivCounter5 - (penDivLimit5 >= 4 ? 1 : 0) - (penDivLimit6 >= 3 ? 1 : 0);
      int penDivTotal9 = 0;
      if (divPenUtilsFlag.requireQuarterDensity && penDivLimit10 >= 1 && penDivLimit5 >= 8 && maxCrafted.getFlaggedBit1Count() * 2 >= penDivLimit5) {
         penDivTotal9 += 3;
      }

      if (divPenUtilsFlag.concurrentActive && penDivLimit10 >= 2) {
         if (maxCrafted.getPeakSecondary() >= 16) {
            penDivTotal9 += 6;
         } else if (maxCrafted.getPeakSecondary() >= 8) {
            penDivTotal9 += 3;
         }
      }

      boolean penDivFlag7 = penDivLimit6 >= 3;
      boolean penDivFlag8 = maxCrafted.getTypeIdCount(139) > 0;
      int penDivTotal10 = 0;
      if (penDivFlag7 && penDivFlag6) {
         penDivTotal10 += 12;
      }

      if (penDivFlag7 && typeIdCount >= 2) {
         penDivTotal10 += 6;
      }

      if (penDivFlag7 && maxCrafted.getChestCount() > 0) {
         penDivTotal10 += 12;
      }

      if (intersectsCenterBound >= 8 && penDivLimit4 >= 3) {
         penDivTotal10 += 8;
      }

      if (penDivLimit > 0 && penDivLimit2 > 0 && penDivLimit3 > 0) {
         penDivTotal10 += 8;
      }

      if (penDivLimit8 >= 2 && penDivFlag8) {
         penDivTotal10 += 8;
      }

      if (penDivFlag4 && penDivFlag7 && penDivLimit3 > 0) {
         penDivTotal10 += 10;
      }

      if (divPenUtilsFlag.countQuarterTotal && maxCrafted.getExcavationPatternCount() >= 2) {
         penDivTotal10 += 8;
      }

      if (penDivFlag7 && (penDivLimit > 0 || penDivLimit2 > 0 || penDivLimit4 > 0)) {
         penDivTotal10 += 8;
      }

      int min20 = Math.min(penDivTotal10, 30);
      int penaltyScore = lightingFarmValue + lightingFarmLimit + min8 + penDivValue9 + min10 + min11 + min12 + min15 + penDivValue16 + penDivValue13 + min14 + penDivTotal8 + min17 + penDivValue18 + min19 + min20 + penDivTotal9;
      Result result = divPenUtilsFlag.usePenalizedResult ? DivPenUtilsPenalized.getIgnoredPenalized(cityMonument.monumentScore, cityMonument.craftedExcavationActive, penaltyScore, divPenUtilsFlag.checkIgnoredPenalty) : Result.NOT_TRIAL;
      int round22 = (int)Math.round(maxCrafted.getCraftedDensityRatio() * 15.0);
      if (penDivLimit8 >= 1 || penDivFlag6 || maxCrafted.getHopperCount() > 0 || penDivFlag7) {
         round22 = 0;
      }

      int divPenValue23 = 0;
      if (maxCrafted.getMinY() <= 40 && penDivCounter5 >= 2 && (penDivLimit > 0 || penDivLimit2 > 0 || penDivLimit4 > 0 || penDivFlag6)) {
         divPenValue23 = divPenUtilsFlag.divPenValue;
      }

      int penDivValue24 = 0;
      boolean penDivFlag9 = false;
      if (result == Result.PENALIZED) {
         penDivValue24 = 40;
      } else if (divPenUtilsFlag.allowAncientCity && cityMonument.hasAnyStructure() && !cityMonument.penDivActive) {
         if (penaltyScore < 60 && penDivLimit8 < 3) {
            penDivFlag9 = true;
         } else {
            penDivValue24 = 32;
         }
      }

      int penDivLimit11 = getValue(penaltyScore - round22 - penDivValue24 + divPenValue23, 0, 100);
      boolean penDivFlag10 = result == Result.IGNORED;
      boolean penDivFlag11 = penDivLimit >= divPenUtilsFlag.penDivLimitCap || penDivFlag6 || maxCrafted.getHopperCount() > 0;
      boolean penDivFlag12 = penDivCounter5 >= divPenUtilsFlag.minCounterThreshold && maxCrafted.getCellCount() >= divPenUtilsFlag.penDivValue && penDivFlag11 && penDivLimit11 >= divPenUtilsFlag.minCraftedCount;
      boolean penDivFlag13 = penDivFlag12 && !penDivFlag10 && !penDivFlag9;
      String formatLightingFarm2 = formatLightingFarm(lightingFarmValue, lightingFarmLimit, min8, penDivValue9, min10, min11, penDivValue13, min14);
      String penDivText = "raw="
         + penaltyScore
         + " st="
         + penDivLimit
         + " ut="
         + penDivLimit2
         + " lt="
         + penDivLimit3
         + " rs="
         + penDivLimit4
         + " cr="
         + penDivBound
         + " fa="
         + penDivLimit5
         + " ex="
         + penDivLimit6
         + " geo="
         + intersectsCenterBound
         + " beDiv="
         + penDivValue5
         + " auto="
         + penDivFlag2
         + " stAb="
         + min15
         + " ltAb="
         + penDivValue16
         + " po="
         + penDivLimit8
         + " act="
         + maxCrafted.getFurnaceCount()
         + " farmBonus="
         + penDivTotal9
         + " spawners="
         + penDivCounter
         + "/"
         + penDivCounter2
         + " rem="
         + maxCrafted.getHopperCount()
         + " enc="
         + maxCrafted.getExcavationHitCount()
         + " deep="
         + maxCrafted.getShulkerCount()
         + " hl="
         + typeIdCount
         + " combo="
         + min20
         + " exFam="
         + maxCrafted.getExcavationPatternCount()
         + " exposed="
         + maxCrafted.getSpawnerCount()
         + " storageOk="
         + penDivFlag11
         + " foreign="
         + penDivCounter3
         + " native="
         + penDivCounter4
         + (cityMonument.penNatFlag ? "(def)" : "")
         + " natPen="
         + round22
         + " strucPen="
         + penDivValue24
         + " ugBias="
         + divPenValue23
         + " div="
         + penDivCounter5;
      return new PenDivUtils.Decision(penDivLimit11, penaltyScore, penDivCounter5, penDivFlag13, result, penDivFlag9, formatLightingFarm2, penDivText, penDivCounter3, penDivCounter4, penDivLimit8, penDivLimit5, penDivLimit6);
   }

   private static int getValue(int penDivBound, int penDivLimit, int spotBound) {
      return penDivBound < penDivLimit ? penDivLimit : Math.min(penDivBound, spotBound);
   }

   static boolean canCraftedExcavation(List<CraftedExcavation> list) {
      int craftedExcavationCounter = 0;
      int craftedExcavationCounter2 = 0;
      int craftedExcavationCounter3 = 0;

      for (CraftedExcavation craftedExcavation : list) {
         if (craftedExcavation.excavationCraftedType == ExcavationCraftedType.REDSTONE) {
            craftedExcavationCounter3++;
            if (craftedExcavation.craftedCategory == 8) {
               craftedExcavationCounter++;
            }

            if (craftedExcavation.craftedCategory == 9 || craftedExcavation.craftedCategory == 10 || craftedExcavation.craftedCategory == 12) {
               craftedExcavationCounter2++;
            }
         }
      }

      return craftedExcavationCounter >= 3 || craftedExcavationCounter2 >= 1 && craftedExcavationCounter3 >= 3;
   }

   static boolean canFlag(ExcavationCluster maxCrafted) {
      return canCraftedExcavation(maxCrafted.getCells());
   }

   private static String formatLightingFarm(int lightingFarmValue, int lightingFarmLimit, int lightingFarmLimit2, int lightingFarmLimit3, int lightingFarmLimit4, int lightingFarmLimit5, int lightingFarmLimit6, int lightingFarmLimit7) {
      int lightingFarmValue2 = lightingFarmValue;
      String lightingFarmText = "storage";
      if (lightingFarmLimit > lightingFarmValue) {
         lightingFarmValue2 = lightingFarmLimit;
         lightingFarmText = "utility";
      }

      if (lightingFarmLimit3 > lightingFarmValue2) {
         lightingFarmValue2 = lightingFarmLimit3;
         lightingFarmText = "redstone";
      }

      if (lightingFarmLimit4 > lightingFarmValue2) {
         lightingFarmValue2 = lightingFarmLimit4;
         lightingFarmText = "crafted";
      }

      if (lightingFarmLimit5 > lightingFarmValue2) {
         lightingFarmValue2 = lightingFarmLimit5;
         lightingFarmText = "geometry";
      }

      if (lightingFarmLimit2 > lightingFarmValue2) {
         lightingFarmValue2 = lightingFarmLimit2;
         lightingFarmText = "lighting";
      }

      if (lightingFarmLimit6 > lightingFarmValue2) {
         lightingFarmValue2 = lightingFarmLimit6;
         lightingFarmText = "farm";
      }

      if (lightingFarmLimit7 > lightingFarmValue2) {
         lightingFarmValue2 = lightingFarmLimit7;
         lightingFarmText = "excavation";
      }

      return lightingFarmValue2 == 0 ? "none" : lightingFarmText;
   }

   private PenDivUtils() {
   }

   public static final class Decision {
      public final int decisionLevel;
      public final int decisionValue;
      public final int penalizedTrialValue;
      public final String append;
      public final int penalizedValue;
      public final int totalScore;
      public final int primaryScore;
      public final boolean decisionActive;
      public final boolean concurrentActive;
      public final int penaltyScore;
      public final String penalizedTrialText;
      public final Result penalizedTrialType;
      public final int bonusScore;

      Decision(
         int penalizedValue,
         int value,
         int value2,
         boolean flag,
         Result penalizedTrialType,
         boolean flag2,
         String text,
         String append,
         int value3,
         int value4,
         int value5,
         int value6,
         int value7
      ) {
         this.penalizedValue = penalizedValue;
         this.primaryScore = value;
         this.penalizedTrialValue = value2;
         this.concurrentActive = flag;
         this.penalizedTrialType = penalizedTrialType;
         this.decisionActive = flag2;
         this.penalizedTrialText = text;
         this.append = append;
         this.bonusScore = value3;
         this.decisionLevel = value4;
         this.penaltyScore = value5;
         this.decisionValue = value6;
         this.totalScore = value7;
      }
   }
}
