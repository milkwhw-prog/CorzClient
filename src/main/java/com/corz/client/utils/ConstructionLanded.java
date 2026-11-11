package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.corz.client.utils.DeltaHeadroomUtils;

public final class ConstructionLanded {
   private double bestDistance;
   private double constructionLandedAmount;
   private int landedPhase;
   private int lastTick;
   private boolean placementReady;
   private long routeFingerprint;
   private boolean onGroundConstructionLanded;
   private ConstructionLanded.ProgressKind constructionLandedType;
   private int tickConstructionLanded;
   private final Map<Long, Integer> constructionLandedIntegerMap;
   private final ArrayDeque<Long> recentRouteQueue;
   private int noneConstructionCount;
   private long noneConstructionValue;
   private final Map<Long, Integer> repeatedRouteIntegerMap = new HashMap<>();
   private long maxConstructionEpoch;
   private int routeIndex;

   public ConstructionLanded.Snapshot getSnapshotAtTick(int constructionLandedValue) {
      return new ConstructionLanded.Snapshot(
         this.routeIndex,
         this.bestDistance,
         this.constructionLandedAmount,
         this.tickConstructionLanded,
         Math.max(0, constructionLandedValue - this.tickConstructionLanded),
         this.noneConstructionCount,
         this.landedPhase,
         this.constructionLandedType,
         List.copyOf(this.recentRouteQueue)
      );
   }

   public ConstructionLanded.Event getRepeatedRoute(long repeatedRouteValue, int repeatedRouteValue2, int repeatedRouteBound) {
      int merge = this.repeatedRouteIntegerMap.merge(repeatedRouteValue, 1, Integer::sum);
      if (merge < 2) {
         return ConstructionLanded.Event.NONE;
      } else {
         this.constructionLandedIntegerMap.put(repeatedRouteValue, repeatedRouteValue2 + Math.max(1, repeatedRouteBound));
         return ConstructionLanded.Event.REPEATED_ROUTE;
      }
   }

   private boolean canDequeArray(long dequeArrayValue) {
      if (!this.recentRouteQueue.isEmpty() && this.recentRouteQueue.peekLast() == dequeArrayValue) {
         return false;
      } else {
         this.recentRouteQueue.addLast(dequeArrayValue);

         while (this.recentRouteQueue.size() > 8) {
            this.recentRouteQueue.removeFirst();
         }

         return true;
      }
   }

   public void updateLastTick(int value) {
      this.tickConstructionLanded = value;
      if (this.landedPhase > 0) {
         this.lastTick = value;
      }
   }

   private static long hashRoutePair(long constructionLandedValue, long constructionLandedValue2) {
      constructionLandedValue ^= constructionLandedValue2;
      return constructionLandedValue * 1099511628211L;
   }

   public void recordMovementSample(long time, ConstructionLanded.MovementSample movementSample) {
      this.routeFingerprint = time;
      this.recentRouteQueue.clear();
      this.noneConstructionCount = 0;
      this.noneConstructionValue = 0L;
      this.landedPhase = 0;
      this.lastTick = -1073741824;
      this.routeIndex = movementSample.routeIndex();
      this.bestDistance = normalizeAmount(movementSample.distance());
      this.constructionLandedAmount = normalizeAmount(movementSample.verticalDistance());
      this.tickConstructionLanded = movementSample.tick();
      this.onGroundConstructionLanded = movementSample.onGround();
      this.maxConstructionEpoch = movementSample.constructionEpoch();
      this.placementReady = movementSample.placementReady();
      this.constructionLandedType = ConstructionLanded.ProgressKind.NONE;
      this.canDequeArray(movementSample.feetCell());
   }

   public boolean hasReachedRepeatLimit(long constructionLandedValue, int constructionLandedLimit) {
      Integer integer = this.constructionLandedIntegerMap.get(constructionLandedValue);
      if (integer == null) {
         return true;
      } else if (constructionLandedLimit >= integer) {
         this.constructionLandedIntegerMap.remove(constructionLandedValue);
         this.repeatedRouteIntegerMap.remove(constructionLandedValue);
         return true;
      } else {
         return false;
      }
   }

   private long getDequeArray() {
      Long[] array = this.recentRouteQueue.toArray(Long[]::new);
      int dequeArrayValue = array.length;
      long dequeArrayValue2 = array[dequeArrayValue - 1];
      long dequeArrayValue3 = array[dequeArrayValue - 2];
      long dequeArrayValue4 = Long.compareUnsigned(dequeArrayValue2, dequeArrayValue3) <= 0 ? dequeArrayValue2 : dequeArrayValue3;
      long dequeArrayValue5 = dequeArrayValue4 == dequeArrayValue2 ? dequeArrayValue3 : dequeArrayValue2;
      return hashRoutePair(hashRoutePair(-3750763034362895579L, dequeArrayValue4), dequeArrayValue5);
   }

   public static long hashRouteList(long constructionLandedValue, long constructionLandedValue2, List<Long> list) {
      long constructionLandedValue3 = list != null && !list.isEmpty() ? (Long)list.get(0) : 0L;
      return hashRouteWithKind(constructionLandedValue, constructionLandedValue2, constructionLandedValue3, null, list);
   }

   private boolean isDequeArray() {
      Long[] array = this.recentRouteQueue.toArray(Long[]::new);
      int dequeArrayLimit = array.length;
      return dequeArrayLimit >= 3 && array[dequeArrayLimit - 1].equals(array[dequeArrayLimit - 3]) && !array[dequeArrayLimit - 1].equals(array[dequeArrayLimit - 2]);
   }

   private ConstructionLanded.ProgressKind getDistanceVertical(ConstructionLanded.MovementSample movementSample) {
      if (movementSample.routeIndex() > this.routeIndex) {
         this.routeIndex = movementSample.routeIndex();
         this.bestDistance = normalizeAmount(movementSample.distance());
         this.constructionLandedAmount = normalizeAmount(movementSample.verticalDistance());
         return ConstructionLanded.ProgressKind.ROUTE_INDEX;
      } else if (movementSample.routeIndex() == this.routeIndex && normalizeAmount(movementSample.distance()) < this.bestDistance - 0.06) {
         this.bestDistance = normalizeAmount(movementSample.distance());
         return ConstructionLanded.ProgressKind.DISTANCE;
      } else if (movementSample.routeIndex() == this.routeIndex && normalizeAmount(movementSample.verticalDistance()) < this.constructionLandedAmount - 0.06) {
         this.constructionLandedAmount = normalizeAmount(movementSample.verticalDistance());
         return ConstructionLanded.ProgressKind.VERTICAL;
      } else if (movementSample.constructionEpoch() > this.maxConstructionEpoch) {
         return ConstructionLanded.ProgressKind.CONSTRUCTION;
      } else if (movementSample.placementReady() && !this.placementReady) {
         return ConstructionLanded.ProgressKind.PLACEMENT_READY;
      } else {
         return movementSample.onGround() && !this.onGroundConstructionLanded && movementSample.verticalDistance() <= 0.2 && movementSample.atNode()
            ? ConstructionLanded.ProgressKind.LANDED
            : ConstructionLanded.ProgressKind.NONE;
      }
   }

   public static long hashRouteWithKind(long constructionLandedValue, long constructionLandedValue2, long constructionLandedValue3, DeltaHeadroomUtils.Kind kind, List<Long> list) {
      long constructionLandedValue4 = -3750763034362895579L;
      long hashRoutePair5 = hashRoutePair(constructionLandedValue4, constructionLandedValue);
      long hashRoutePair6 = hashRoutePair(hashRoutePair5, constructionLandedValue2);
      long hashRoutePair7 = hashRoutePair(hashRoutePair6, constructionLandedValue3);
      long hashRoutePair8 = hashRoutePair(hashRoutePair7, kind == null ? -1L : kind.ordinal());
      if (list != null) {
         for (long constructionLandedValue9 : list) {
            hashRoutePair8 = hashRoutePair(hashRoutePair8, constructionLandedValue9);
         }
      }

      return hashRoutePair8;
   }

   public ConstructionLanded.Event evaluateMovementSample(ConstructionLanded.MovementSample movementSample) {
      if (movementSample.routeFingerprint() == 0L) {
         return ConstructionLanded.Event.NONE;
      } else if (this.routeFingerprint != movementSample.routeFingerprint()) {
         this.recordMovementSample(movementSample.routeFingerprint(), movementSample);
         return ConstructionLanded.Event.NONE;
      } else {
         ConstructionLanded.ProgressKind constructionLandedType = this.getDistanceVertical(movementSample);
         if (constructionLandedType != ConstructionLanded.ProgressKind.NONE) {
            this.constructionLandedType = constructionLandedType;
            this.tickConstructionLanded = movementSample.tick();
            if (constructionLandedType == ConstructionLanded.ProgressKind.ROUTE_INDEX
               || constructionLandedType == ConstructionLanded.ProgressKind.CONSTRUCTION
               || constructionLandedType == ConstructionLanded.ProgressKind.LANDED
               || constructionLandedType == ConstructionLanded.ProgressKind.PLACEMENT_READY) {
               this.recentRouteQueue.clear();
               this.noneConstructionCount = 0;
               this.noneConstructionValue = 0L;
            }
         } else {
            this.constructionLandedType = ConstructionLanded.ProgressKind.NONE;
         }

         boolean dequeArray = this.canDequeArray(movementSample.feetCell());
         boolean noneConstructionFlag = dequeArray && this.isDequeArray();
         this.onGroundConstructionLanded = movementSample.onGround();
         this.maxConstructionEpoch = Math.max(this.maxConstructionEpoch, movementSample.constructionEpoch());
         this.placementReady = this.placementReady | movementSample.placementReady();
         if (!noneConstructionFlag) {
            return constructionLandedType == ConstructionLanded.ProgressKind.NONE ? ConstructionLanded.Event.NONE : ConstructionLanded.Event.MOVEMENT_PROGRESS;
         } else {
            long time = this.getDequeArray();
            if (time == this.noneConstructionValue) {
               this.noneConstructionCount++;
            } else {
               this.noneConstructionValue = time;
               this.noneConstructionCount = 1;
            }

            int noneConstructionValue = Math.max(0, movementSample.tick() - this.tickConstructionLanded);
            if (constructionLandedType != ConstructionLanded.ProgressKind.NONE || noneConstructionValue < 16) {
               return ConstructionLanded.Event.CYCLE_IGNORED;
            } else if (this.landedPhase == 0 && this.noneConstructionCount >= 3) {
               this.landedPhase = 1;
               this.lastTick = movementSample.tick();
               this.noneConstructionCount = 0;
               this.recentRouteQueue.clear();
               this.canDequeArray(movementSample.feetCell());
               return ConstructionLanded.Event.LOCAL_RECOVERY;
            } else {
               return this.landedPhase > 0 && this.noneConstructionCount >= 3 && movementSample.tick() - this.lastTick >= 10
                  ? ConstructionLanded.Event.MOVEMENT_CYCLE
                  : ConstructionLanded.Event.CYCLE_IGNORED;
            }
         }
      }
   }

   private static double normalizeAmount(double constructionLandedBound) {
      return Double.isFinite(constructionLandedBound) ? Math.max(0.0, constructionLandedBound) : Double.POSITIVE_INFINITY;
   }

   public ConstructionLanded() {
      this.constructionLandedIntegerMap = new HashMap<>();
      this.recentRouteQueue = new ArrayDeque<>();
      this.routeIndex = -1;
      this.bestDistance = Double.POSITIVE_INFINITY;
      this.constructionLandedAmount = Double.POSITIVE_INFINITY;
      this.lastTick = -1073741824;
      this.constructionLandedType = ConstructionLanded.ProgressKind.NONE;
   }

   public void updateTimeDeque() {
      this.recentRouteQueue.clear();
      this.noneConstructionCount = 0;
      this.noneConstructionValue = 0L;
      this.landedPhase = 0;
      this.routeFingerprint = 0L;
   }

   public ConstructionLanded.ResetStats getResetStats() {
      ConstructionLanded.ResetStats resetStats = new ConstructionLanded.ResetStats(this.repeatedRouteIntegerMap.size(), this.constructionLandedIntegerMap.size(), this.recentRouteQueue.size());
      this.repeatedRouteIntegerMap.clear();
      this.constructionLandedIntegerMap.clear();
      this.updateTimeDeque();
      this.routeIndex = -1;
      this.bestDistance = Double.POSITIVE_INFINITY;
      this.constructionLandedAmount = Double.POSITIVE_INFINITY;
      this.tickConstructionLanded = 0;
      this.constructionLandedType = ConstructionLanded.ProgressKind.NONE;
      return resetStats;
   }

   public static enum Event {
      NONE,
      MOVEMENT_PROGRESS,
      CYCLE_IGNORED,
      LOCAL_RECOVERY,
      MOVEMENT_CYCLE,
      REPEATED_ROUTE;
   }

   public record MovementSample(
      int tick,
      long routeFingerprint,
      int routeIndex,
      long feetCell,
      double distance,
      double verticalDistance,
      boolean onGround,
      DeltaHeadroomUtils.Kind transition,
      long constructionEpoch,
      boolean placementReady,
      boolean atNode
   ) {
      public MovementSample(
         int onGround,
         long constructionEpoch,
         int routeFingerprint,
         long verticalDistance,
         double transition,
         double placementReady,
         boolean tick,
         DeltaHeadroomUtils.Kind feetCell,
         long routeIndex,
         boolean distance
      ) {
         this(onGround, constructionEpoch, routeFingerprint, verticalDistance, transition, placementReady, tick, feetCell, routeIndex, distance, true);
      }
   }

   public static enum ProgressKind {
      NONE,
      ROUTE_INDEX,
      DISTANCE,
      VERTICAL,
      LANDED,
      CONSTRUCTION,
      PLACEMENT_READY;
   }

   public record ResetStats(int routeFingerprints, int bannedFingerprints, int feetHistory) {
   }

   public record Snapshot(
      int routeIndex,
      double bestDistance,
      double bestVerticalDistance,
      int lastProgressTick,
      int noProgressTicks,
      int cycleRepeats,
      int localRecoveries,
      ConstructionLanded.ProgressKind progressKind,
      List<Long> recentFeet
   ) {
   }
}
