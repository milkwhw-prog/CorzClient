package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import com.corz.client.utils.FloorBuildPlanner;
import com.corz.client.utils.EyeStandsData;

public final class SurfaceFrontier {
   public int surfaceFailuresCount;
   private int previousSurfaceValue;
   private EyeStandsData surfaceNoneEyeStandsData;
   public int arraylistSize;
   private int maxChooseCount;
   private String none;
   private SurfaceFrontier.Phase probingExitingType = SurfaceFrontier.Phase.INACTIVE;
   private final Set<Long> noneFrontierSet;
   private int walkableDeckValue = Integer.MIN_VALUE;
   public int surfaceDropValue;
   private long noneCount;
   public int probingExitingCount;
   public int deckSurfaceCount;
   public int surfaceDeckCount;
   public int sessionResetCount;
   public int frontierExhaustedCount;
   public int frontierSurfaceCount;
   public int entryCount;
   private int signumDirection;
   private int lastNoneValue;
   public int surfaceMicroCount;
   private int signumFrontierSurface;
   public int surfaceDropRefusals;
   public int deckExplicitCount;
   private boolean signumPlacementsActive;
   public int distinctStandCellCount;
   public int surfaceExitsCount;
   public int deckExplicitlyCount;
   public int surfaceActivationsCount;
   public int surfaceDropCount;
   private FloorBuildPlanner.Deck remaining;
   private final Map<SurfaceFrontier.Entry, SurfaceFrontier.EntryFailure> noneMap;
   public int surfacePlacementsCount;
   private int frontierLocalValue;
   public int cycleMissionCount;
   public final Map<String, Integer> redirectRoiMap;
   private long lastLongValue;
   public int surfaceNoneCount;

   public FloorBuildPlanner.Deck getRemaining() {
      return this.remaining;
   }

   public boolean isProbingExiting() {
      return this.probingExitingType == SurfaceFrontier.Phase.PROBING
         || this.probingExitingType == SurfaceFrontier.Phase.ENTRY_PLANNED
         || this.probingExitingType == SurfaceFrontier.Phase.ENTERING;
   }

   public boolean canSurfaceDrop(int surfaceDropValue, int surfaceDropValue2) {
      if (this.probingExitingType == SurfaceFrontier.Phase.ON_DECK && this.remaining != null) {
         this.surfaceDropCount++;
         boolean surfaceDrop = surfaceDropValue - surfaceDropValue2 >= 2 && surfaceDropValue2 <= this.walkableDeckValue && this.remaining.getFrontierCount() > 0;
         if (surfaceDrop) {
            this.surfaceDropRefusals++;
         }

         return surfaceDrop;
      } else {
         return false;
      }
   }

   public boolean canProbingExiting(long probingExitingValue) {
      if (this.isProbingExitingConfirmed() && this.remaining != null && this.remaining.containsCell(probingExitingValue)) {
         if (this.probingExitingType != SurfaceFrontier.Phase.ON_DECK) {
            this.probingExitingType = SurfaceFrontier.Phase.ON_DECK;
            this.entryCount++;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isProbingExitingLatched() {
      return this.probingExitingType == SurfaceFrontier.Phase.ON_DECK;
   }

   public void updateMicroSurface() {
      this.surfaceMicroCount++;
      this.signumPlacementsActive = true;
   }

   public EyeStandsData getEyeStandsData() {
      return this.surfaceNoneEyeStandsData;
   }

   public SurfaceFrontier.Action getActionAt(int surfaceFrontierValue, int builderAutoValue, int j) {
      return this.getActionWithPredicates(surfaceFrontierValue, builderAutoValue, j, pick -> this.remaining.frontiersByCell.getOrDefault(pick.cell(), List.of()).stream().anyMatch(frontier -> frontier.stand() == pick.stand()), pick2 -> true);
   }

   public int getWalkableDeckValue() {
      return this.walkableDeckValue;
   }

   public int getRemainingCount() {
      return this.remaining == null ? 0 : this.remaining.getFrontierCount();
   }

   public void updateEnteringProbing() {
      if (this.isProbingExiting()) {
         this.probingExitingType = SurfaceFrontier.Phase.ENTERING;
      }
   }

   public void resetFrontierFlags() {
      this.signumPlacementsActive = true;
   }

   public void onProbingExiting(String none) {
      if (this.isProbingExiting()) {
         this.probingExitingCount++;
         this.none = none;
         this.probingExitingType = SurfaceFrontier.Phase.FAILED_ENTRY;
         this.remaining = null;
      }
   }

   public void onSurfaceDeck(long surfaceDeckValue) {
      this.noneFrontierSet.add(surfaceDeckValue);
      this.surfaceDeckCount++;
   }

   public void onDeckSurface(long longValue) {
      this.surfaceDeckCount++;
      if (this.remaining != null && this.remaining.containsCell(longValue)) {
         this.probingExitingType = SurfaceFrontier.Phase.ON_DECK;
         this.frontierLocalValue = 0;
         this.lastLongValue = longValue;
      }
   }

   public String getNone() {
      return this.none;
   }

   public boolean isProbingExitingConfirmed() {
      return this.probingExitingType == SurfaceFrontier.Phase.PROBING
         || this.probingExitingType == SurfaceFrontier.Phase.ENTRY_PLANNED
         || this.probingExitingType == SurfaceFrontier.Phase.ENTERING
         || this.probingExitingType == SurfaceFrontier.Phase.ON_DECK
         || this.probingExitingType == SurfaceFrontier.Phase.EXITING;
   }

   public void onNoneSignum(FloorBuildPlanner.Deck remaining, long longValue) {
      this.remaining = remaining;
      this.walkableDeckValue = remaining.layerY;
      this.maxChooseCount = remaining.getCellCount();
      this.noneFrontierSet.clear();
      this.frontierLocalValue = 0;
      this.surfaceActivationsCount++;
      this.noneCount++;
      this.signumPlacementsActive = false;
      if (remaining.containsCell(longValue)) {
         this.probingExitingType = SurfaceFrontier.Phase.ON_DECK;
         this.entryCount++;
      } else {
         this.probingExitingType = SurfaceFrontier.Phase.ENTERING;
      }

      this.lastLongValue = longValue;
      this.signumFrontierSurface = this.signumDirection = 0;
      this.previousSurfaceValue = this.lastNoneValue = Integer.MIN_VALUE;
   }

   private long getChooseRemaining(int remainingValue, int j) {
      long chooseRemaining = 0L;
      int remainingLimit = Integer.MAX_VALUE;

      for (long proofSupportValue : this.remaining.floorCells) {
         int maxAbs = Math.max(Math.abs(ProofSupportUtils.getValueInt(proofSupportValue) - remainingValue), Math.abs(ProofSupportUtils.getValueForLong(proofSupportValue) - j));
         if (maxAbs < remainingLimit) {
            remainingLimit = maxAbs;
            chooseRemaining = proofSupportValue;
         }
      }

      return chooseRemaining;
   }

   public String getAppend() {
      return "surfaceActivations="
         + this.surfaceActivationsCount
         + " surfaceEntries="
         + this.entryCount
         + " surfaceExits="
         + this.surfaceExitsCount
         + " surfacePlacements="
         + this.surfacePlacementsCount
         + " surfaceMicroMoves="
         + this.surfaceMicroCount
         + " surfaceExtensions="
         + this.surfaceDropValue
         + " surfaceDropEvaluations="
         + this.surfaceDropCount
         + " surfaceDropRefusals="
         + this.surfaceDropRefusals
         + " surfaceBootstrapPillars="
         + this.surfaceFailuresCount
         + " surfaceMoveFailures="
         + this.surfaceDeckCount
         + " deckPhase="
         + this.probingExitingType
         + (
            this.isProbingExitingConfirmed()
               ? " deckY="
                  + this.walkableDeckValue
                  + " walkable="
                  + (this.remaining == null ? 0 : this.remaining.getCellCount())
                  + " frontier="
                  + this.getRemainingCount()
               : ""
         );
   }

   public SurfaceFrontier.Action getFrontierSurface(
      int frontierSurfaceValue,
      int builderAutoValue,
      int frontierSurfaceValue3,
      Predicate<FloorBuildPlanner.Pick> predicate,
      Predicate<FloorBuildPlanner.Pick> predicate2,
      BiPredicate<Long, Long> biPredicate
   ) {
      if (this.isProbingExitingConfirmed() && this.remaining != null) {
         long longValue = ProofSupportUtils.getTime(frontierSurfaceValue, builderAutoValue, frontierSurfaceValue3);
         if (longValue != this.lastLongValue) {
            int j = frontierSurfaceValue - ProofSupportUtils.getValueInt(this.lastLongValue);
            int frontierSurfaceValue5 = frontierSurfaceValue3 - ProofSupportUtils.getValueForLong(this.lastLongValue);
            if (Math.abs(j) + Math.abs(frontierSurfaceValue5) > 0 && Math.abs(j) <= 3 && Math.abs(frontierSurfaceValue5) <= 3) {
               this.signumFrontierSurface = Integer.signum(j);
               this.signumDirection = Integer.signum(frontierSurfaceValue5);
            }

            this.lastLongValue = longValue;
         }

         if (!this.remaining.containsCell(longValue)) {
            if (this.probingExitingType == SurfaceFrontier.Phase.ON_DECK) {
               if (++this.frontierLocalValue > 3) {
                  return new SurfaceFrontier.Action(SurfaceFrontier.Kind.EXIT, List.of(), 0L, "lost the surface");
               } else {
                  long chooseRemaining = this.getChooseRemaining(frontierSurfaceValue, frontierSurfaceValue3);
                  return chooseRemaining == 0L
                     ? new SurfaceFrontier.Action(SurfaceFrontier.Kind.EXIT, List.of(), 0L, "no surface cell left")
                     : new SurfaceFrontier.Action(SurfaceFrontier.Kind.MOVE, List.of(), chooseRemaining, "back onto the surface");
               }
            } else {
               return !this.remaining.hasEnoughEdgeFrontiers()
                  ? new SurfaceFrontier.Action(SurfaceFrontier.Kind.EXIT, List.of(), 0L, "too little frontier to enter for")
                  : new SurfaceFrontier.Action(SurfaceFrontier.Kind.ENTER, List.of(), this.getChooseRemaining(frontierSurfaceValue, frontierSurfaceValue3), "enter the surface once");
            }
         } else {
            if (this.probingExitingType != SurfaceFrontier.Phase.ON_DECK) {
               this.probingExitingType = SurfaceFrontier.Phase.ON_DECK;
               this.entryCount++;
            }

            this.frontierLocalValue = 0;
            this.frontierSurfaceCount++;
            var arrayList = new ArrayList();

            for (long sortCellKeysItem : FloorBuildPlanner.sortCellKeys(this.remaining.frontiersByCell.keySet())) {
               int frontierSurfaceValue7 = ProofSupportUtils.getValueInt(sortCellKeysItem) - frontierSurfaceValue;
               int frontierSurfaceValue8 = ProofSupportUtils.getValueForLong(sortCellKeysItem) - frontierSurfaceValue3;
               FloorBuildPlanner.Pose pose = Math.abs(frontierSurfaceValue7) + Math.abs(frontierSurfaceValue8) == 1
                  ? FloorBuildPlanner.Pose.EDGE
                  : FloorBuildPlanner.Pose.DIAGONAL;
               FloorBuildPlanner.Pick pick = new FloorBuildPlanner.Pick(sortCellKeysItem, longValue, pose, 0, frontierSurfaceValue7, frontierSurfaceValue8);
               if (predicate.test(pick)) {
                  arrayList.add(pick);
               }
            }

            this.arraylistSize = arrayList.size();
            var standPose = FloorBuildPlanner.pickStandPoses(
               this.remaining, frontierSurfaceValue, frontierSurfaceValue3, this.previousSurfaceValue, this.lastNoneValue, this.signumFrontierSurface, this.signumDirection, this.noneFrontierSet
            );
            var hashMap = new HashMap();

            for (int i = 0; i < standPose.size(); i++) {
               hashMap.put(((FloorBuildPlanner.Pick)standPose.get(i)).cell(), i);
            }

            arrayList.sort(Comparator.comparingInt(obj -> (Integer)hashMap.getOrDefault(((com.corz.client.utils.FloorBuildPlanner.Pick) obj).cell(), Integer.MAX_VALUE)));
            if (!arrayList.isEmpty()) {
               return new SurfaceFrontier.Action(SurfaceFrontier.Kind.PLACE, arrayList, longValue, "executor-proven work at the current pose");
            } else {
               var standCell = FloorBuildPlanner.pickBestStandCells(this.remaining, frontierSurfaceValue, frontierSurfaceValue3, this.noneFrontierSet, predicate2, biPredicate);
               if (standCell.isEmpty()) {
                  return new SurfaceFrontier.Action(
                     SurfaceFrontier.Kind.EXIT,
                     standCell,
                     0L,
                     this.remaining.getFrontierCount() == 0 ? "frontier exhausted" : "no reachable stand for the remaining frontier"
                  );
               } else {
                  FloorBuildPlanner.Pick floorBuildPlannerPick = (FloorBuildPlanner.Pick)standCell.get(0);
                  this.frontierExhaustedCount++;
                  this.distinctStandCellCount = (int)standCell.stream().filter(first -> first.stand() == floorBuildPlannerPick.stand()).map(FloorBuildPlanner.Pick::cell).distinct().count();
                  return new SurfaceFrontier.Action(
                     SurfaceFrontier.Kind.MOVE, standCell, floorBuildPlannerPick.stand(), floorBuildPlannerPick.standDist() > 8 ? "LOCAL_FRONTIER_EXHAUSTED" : "LOCAL_FRONTIER"
                  );
               }
            }
         }
      } else {
         return new SurfaceFrontier.Action(SurfaceFrontier.Kind.NONE, List.of(), 0L, "inactive");
      }
   }

   public void onExitingProbing(String exitingProbingText) {
      if (this.isProbingExiting()) {
         this.onProbingExiting(exitingProbingText);
      } else {
         if (this.probingExitingType == SurfaceFrontier.Phase.ON_DECK) {
            this.probingExitingType = SurfaceFrontier.Phase.EXITING;
            this.surfaceExitsCount++;
         }

         this.probingExitingType = SurfaceFrontier.Phase.DONE;
         this.remaining = null;
         this.noneFrontierSet.clear();
      }
   }

   public Set<Long> getNoneFrontierSet() {
      return this.noneFrontierSet;
   }

   public void updateSurfaceNone() {
      this.probingExitingType = SurfaceFrontier.Phase.INACTIVE;
      this.remaining = null;
      this.walkableDeckValue = Integer.MIN_VALUE;
      this.noneFrontierSet.clear();
      this.frontierLocalValue = 0;
      this.noneMap.clear();
      this.surfaceNoneEyeStandsData = EyeStandsData.INSTANCE;
      this.none = "none";
      this.noneCount = 0L;
      this.surfaceActivationsCount = this.entryCount = this.surfaceExitsCount = this.surfacePlacementsCount = this.surfaceMicroCount = this.surfaceDropValue = 0;
      this.surfaceDropCount = this.surfaceDropRefusals = this.surfaceFailuresCount = this.surfaceDeckCount = this.probingExitingCount = 0;
      this.frontierSurfaceCount = this.arraylistSize = this.surfaceNoneCount = this.frontierExhaustedCount = this.distinctStandCellCount = 0;
      this.deckExplicitCount = this.sessionResetCount = this.cycleMissionCount = this.deckSurfaceCount = this.deckExplicitlyCount = 0;
      this.redirectRoiMap.clear();
   }

   public void setEyeStandsData(EyeStandsData data) {
      this.surfaceNoneEyeStandsData = data;
   }

   public void onSignumPlacements(long signumPlacementsValue) {
      if (this.isProbingExitingConfirmed() && this.remaining != null && ProofSupportUtils.getValue(signumPlacementsValue) == this.walkableDeckValue) {
         this.surfacePlacementsCount++;
         if (!this.signumPlacementsActive) {
            this.surfaceNoneCount++;
         }

         this.signumPlacementsActive = false;
         int value = ProofSupportUtils.getValueInt(signumPlacementsValue);
         int noneValue = ProofSupportUtils.getValueForLong(signumPlacementsValue);
         if (this.previousSurfaceValue != Integer.MIN_VALUE) {
            this.signumFrontierSurface = Integer.signum(value - this.previousSurfaceValue);
            this.signumDirection = Integer.signum(noneValue - this.lastNoneValue);
         }

         this.previousSurfaceValue = value;
         this.lastNoneValue = noneValue;
      }
   }

   public boolean canGeometryNone(String geometryNoneText, long geometryNoneValue, long geometryNoneValue2) {
      SurfaceFrontier.EntryFailure nonemap = this.noneMap.get(new SurfaceFrontier.Entry(geometryNoneText, geometryNoneValue));
      return nonemap == null || nonemap.geometry() != geometryNoneValue2;
   }

   public void updateFailuresSurface() {
      this.surfaceFailuresCount++;
   }

   public SurfaceFrontier.Phase getProbingExitingType() {
      return this.probingExitingType;
   }

   public long getNoneCount() {
      return this.noneCount;
   }

   public SurfaceFrontier.Action getActionWithPredicates(
      int surfaceFrontierValue, int builderAutoValue, int j, Predicate<FloorBuildPlanner.Pick> predicate, Predicate<FloorBuildPlanner.Pick> predicate2
   ) {
      return this.getFrontierSurface(surfaceFrontierValue, builderAutoValue, j, predicate, predicate2, (long2, long3) -> true);
   }

   public void onNoneRecorded(String noneText, long noneValue, long noneValue2, String noneText2) {
      this.noneMap.put(new SurfaceFrontier.Entry(noneText, noneValue), new SurfaceFrontier.EntryFailure(noneValue2, noneText2));
      this.none = noneText + ": " + noneText2;
   }

   public SurfaceFrontier() {
      this.previousSurfaceValue = Integer.MIN_VALUE;
      this.lastNoneValue = Integer.MIN_VALUE;
      this.noneFrontierSet = new HashSet<>();
      this.surfaceNoneEyeStandsData = EyeStandsData.INSTANCE;
      this.none = "none";
      this.noneMap = new HashMap<>();
      this.redirectRoiMap = new LinkedHashMap<>();
   }

   public void updateProbingExitingState() {
      if (this.isProbingExiting()) {
         this.probingExitingType = SurfaceFrontier.Phase.ENTRY_PLANNED;
      }
   }

   public void onMaxSurface(FloorBuildPlanner.Deck remaining) {
      if (remaining != null && this.isProbingExitingConfirmed()) {
         if (remaining.getCellCount() > this.maxChooseCount) {
            this.surfaceDropValue = this.surfaceDropValue + (remaining.getCellCount() - this.maxChooseCount);
         }

         this.maxChooseCount = Math.max(this.maxChooseCount, remaining.getCellCount());
         this.remaining = remaining;
      }
   }

   public record Action(SurfaceFrontier.Kind kind, List<FloorBuildPlanner.Pick> picks, long stand, String why) {
   }

   private record Entry(String strategy, long stand) {
   }

   private record EntryFailure(long geometry, String why) {
   }

   public static enum Kind {
      NONE,
      PLACE,
      MOVE,
      ENTER,
      EXIT;
   }

   public static enum Phase {
      INACTIVE,
      PROBING,
      ENTRY_PLANNED,
      ENTERING,
      ON_DECK,
      EXITING,
      DONE,
      FAILED_ENTRY;
   }
}
