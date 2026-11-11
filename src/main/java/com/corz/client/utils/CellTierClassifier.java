package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public final class CellTierClassifier {
   public static CellTierClassifier.Report classifyCells(List<CellTierClassifier.Cell> list, int activeStaleValue, int activeStaleLimit) {
      int staleticksSize = list.size();
      int activeStaleCounter = 0;
      int activeStaleCounter2 = 0;
      int activeStaleCounter3 = 0;
      int activeStaleCounter4 = 0;
      int activeStaleCounter5 = 0;
      int activeStaleCounter6 = 0;
      int activeStaleCounter7 = 0;
      int activeStaleCounter8 = 0;
      int activeStaleCounter9 = 0;
      int activeStaleCounter10 = 0;
      int activeStaleCounter11 = 0;
      int activeStaleCounter12 = 0;
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      ArrayList arrayList3 = new ArrayList();
      ArrayList arrayList4 = new ArrayList();
      ArrayList arrayList5 = new ArrayList();

      for (CellTierClassifier.Cell cell : list) {
         if (!cell.underPlayer()
            && !cell.accessRoute()
            && !cell.activeMission()
            && cell.nearRemainingWork()
            && !cell.staleOwner()
            && cell.ageTicks() < activeStaleLimit) {
            arrayList5.add(cell);
         }
      }

      arrayList5.sort(Comparator.comparingInt(CellTierClassifier.Cell::ageTicks));
      int highFutureKeepCount = getHighFutureKeepCount(activeStaleValue);
      HashSet hashSet = new HashSet();

      for (int i = 0; i < arrayList5.size() && i < highFutureKeepCount; i++) {
         hashSet.add(((CellTierClassifier.Cell)arrayList5.get(i)).key());
      }

      for (CellTierClassifier.Cell cell2 : list) {
         CellTierClassifier.Tier tier;
         if (cell2.underPlayer() || cell2.accessRoute()) {
            tier = CellTierClassifier.Tier.HARD_REQUIRED;
         } else if (cell2.activeMission()) {
            tier = CellTierClassifier.Tier.ACTIVE_MISSION;
         } else if (hashSet.contains(cell2.key())) {
            tier = CellTierClassifier.Tier.HIGH_FUTURE_VALUE;
         } else if (cell2.nearRemainingWork() && !cell2.staleOwner() && cell2.ageTicks() < activeStaleLimit) {
            tier = CellTierClassifier.Tier.LOW_FUTURE_VALUE;
         } else if (!cell2.staleOwner() && cell2.ageTicks() < activeStaleLimit) {
            tier = CellTierClassifier.Tier.ORPHANED;
         } else {
            tier = CellTierClassifier.Tier.STALE;
         }

         switch (tier) {
            case HARD_REQUIRED:
               activeStaleCounter++;
               if (cell2.accessRoute() && !cell2.underPlayer()) {
                  activeStaleCounter7++;
               }
               break;
            case ACTIVE_MISSION:
               activeStaleCounter2++;
               break;
            case HIGH_FUTURE_VALUE:
               activeStaleCounter3++;
               break;
            case LOW_FUTURE_VALUE:
               activeStaleCounter4++;
               break;
            case STALE:
               activeStaleCounter5++;
               break;
            case ORPHANED:
               activeStaleCounter6++;
         }

         if (tier != CellTierClassifier.Tier.HARD_REQUIRED && tier != CellTierClassifier.Tier.ACTIVE_MISSION) {
            if (cell2.banned()) {
               activeStaleCounter11++;
               arrayList4.add(cell2.key());
            } else {
               boolean activeStaleFlag = cell2.reachableTop() && !cell2.hasTempAbove();
               if (activeStaleFlag) {
                  activeStaleCounter8++;
               } else {
                  if (!cell2.baseReachable()) {
                     activeStaleCounter10++;
                     arrayList3.add(cell2.key());
                     continue;
                  }

                  activeStaleCounter9++;
                  arrayList2.add(cell2.key());
               }
               byte activeStaleValue2 = switch (tier) {
                  case LOW_FUTURE_VALUE -> 2;
                  case STALE -> 1;
                  case ORPHANED -> 0;
                  default -> 3;
               };
               if (activeStaleValue2 == 3) {
                  activeStaleCounter12++;
               }

               arrayList.add(new long[]{cell2.key(), activeStaleValue2, activeStaleFlag ? 0L : 1L});
            }
         }
      }

      arrayList.sort(Comparator.<long[]>comparingLong(activeStaleValues -> activeStaleValues[1]).thenComparingLong(activeStaleValues2 -> activeStaleValues2[2]));
      ArrayList arrayList6 = new ArrayList(arrayList.size());

      for (long[] activeStaleValues3 : (Iterable<long[]>) (arrayList)) {
         arrayList6.add(activeStaleValues3[0]);
      }

      return new CellTierClassifier.Report(staleticksSize, activeStaleCounter, activeStaleCounter2, activeStaleCounter3, activeStaleCounter4, activeStaleCounter5, activeStaleCounter6, activeStaleCounter8, activeStaleCounter9, activeStaleCounter10, activeStaleCounter11, activeStaleCounter7, activeStaleCounter12, arrayList6, arrayList2, arrayList3, arrayList4);
   }

   private CellTierClassifier() {
   }

   public static boolean isValueBelowLimit(int activeLimit, int activeValue) {
      return activeLimit > activeValue;
   }

   public static int getHighFutureKeepCount(int activeValue) {
      return Math.max(1, activeValue / 2);
   }

   public record Cell(
      long key,
      int y,
      boolean underPlayer,
      boolean activeMission,
      boolean banned,
      boolean hasTempAbove,
      boolean nearRemainingWork,
      boolean staleOwner,
      int ageTicks,
      boolean reachableTop,
      boolean baseReachable,
      boolean accessRoute
   ) {
      public Cell(
         long banned,
         int baseReachable,
         boolean hasTempAbove,
         boolean reachableTop,
         boolean y,
         boolean ageTicks,
         boolean key,
         boolean nearRemainingWork,
         int underPlayer,
         boolean activeMission,
         boolean staleOwner
      ) {
         this(banned, baseReachable, hasTempAbove, reachableTop, y, ageTicks, key, nearRemainingWork, underPlayer, activeMission, staleOwner, false);
      }
   }

   public record Report(
      int live,
      int hardRequired,
      int active,
      int highFuture,
      int lowFuture,
      int stale,
      int orphaned,
      int reclaimableNow,
      int reclaimableBottomUp,
      int unreachable,
      int banned,
      int route,
      int protectedInRetire,
      List<Long> retireOrder,
      List<Long> bottomUpCells,
      List<Long> unreachableCells,
      List<Long> bannedCells
   ) {
      public String formatSummary() {
         return "live="
            + this.live
            + " reclaimableNow="
            + this.reclaimableNow
            + " bottomUp="
            + this.reclaimableBottomUp
            + " hardRequired="
            + this.hardRequired
            + " route="
            + this.route
            + " active="
            + this.active
            + " keptFutureROI="
            + this.highFuture
            + " lowFuture="
            + this.lowFuture
            + " stale="
            + this.stale
            + " orphaned="
            + this.orphaned
            + " unreachable="
            + this.unreachable
            + " banned="
            + this.banned
            + " actionableDebt="
            + this.getActionableDebt();
      }

      public int getActionableDebt() {
         return Math.max(0, this.live - this.hardRequired - this.active - this.unreachable - this.banned);
      }
   }

   public static enum Tier {
      HARD_REQUIRED,
      ACTIVE_MISSION,
      HIGH_FUTURE_VALUE,
      LOW_FUTURE_VALUE,
      STALE,
      ORPHANED;
   }
}
