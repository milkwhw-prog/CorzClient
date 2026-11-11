package com.corz.client.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import com.corz.client.utils.PlacementExplicit;
import com.corz.client.utils.MovementMissionData;

public final class InvalidNone {
   private long replacementYieldTime;
   public int noneValue;
   public final Map<InvalidNone.Failure, Integer> failureCountMap;
   public int replacementYieldCount;
   public int noneCount;
   public int noneReplacementCount;
   private PlacementExplicit.Owner legacyConstructionType;
   public int legacyConstructionCount;
   public String none = "none";

   public void resetCounters() {
      this.legacyConstructionType = null;
      this.replacementYieldTime = 0L;
      this.replacementYieldCount = this.noneCount = this.legacyConstructionCount = this.noneReplacementCount = this.noneValue = 0;
      this.none = "none";
      this.failureCountMap.clear();
   }

   public void recordFailure(InvalidNone.Failure failure) {
      this.failureCountMap.merge(failure, 1, Integer::sum);
   }

   public boolean isLegacyConstruction() {
      return this.legacyConstructionType != null;
   }

   public InvalidNone.Arrival classifyArrival(
      PlacementExplicit<?> placementExplicit, long invalidGeometryValue, boolean invalidGeometryFlag, boolean invalidGeometryFlag2, Long long2, BooleanSupplier booleanSupplier
   ) {
      if (!invalidGeometryFlag || invalidGeometryValue != placementExplicit.egressActionTime) {
         return InvalidNone.Arrival.MOVING;
      } else if (!invalidGeometryFlag2) {
         placementExplicit.handleFailureExplicit("DESTINATION_UNSAFE");
         return InvalidNone.Arrival.INVALID;
      } else if (!placementExplicit.canRevalidateAlign(invalidGeometryValue)) {
         return InvalidNone.Arrival.INVALID;
      } else if (placementExplicit.isPlacementMove()) {
         if (!booleanSupplier.getAsBoolean()) {
            placementExplicit.handleFailureExplicit("EXECUTOR_PROOF_INVALID");
            return InvalidNone.Arrival.INVALID;
         } else {
            return InvalidNone.Arrival.EXECUTE_PLACEMENT;
         }
      } else if (placementExplicit.actionType != PlacementExplicit.Type.ENTER_SURFACE || long2 != null && long2 == invalidGeometryValue) {
         placementExplicit.canRevalidateCompleted(invalidGeometryValue);

         return switch (placementExplicit.actionType) {
            case MOVE_TO_ACCESS_BASE -> InvalidNone.Arrival.BUILD_ACCESS;
            case MOVE_TO_SUPPORT -> InvalidNone.Arrival.BUILD_SUPPORT;
            case ENTER_SURFACE -> InvalidNone.Arrival.ON_SURFACE;
            default -> InvalidNone.Arrival.MOVE_COMPLETE;
         };
      } else {
         placementExplicit.handleFailureExplicit("ENTRY_GEOMETRY_INVALID");
         return InvalidNone.Arrival.INVALID;
      }
   }

   public boolean isLegacyMissionMatch(MovementMissionData movementMissionData) {
      return this.legacyConstructionType == movementMissionData.owner() && this.replacementYieldTime == movementMissionData.missionId();
   }

   public void finishLegacyConstruction(String none) {
      if (this.legacyConstructionType != null) {
         this.legacyConstructionCount++;
         this.none = none;
      }

      this.legacyConstructionType = null;
      this.replacementYieldTime = 0L;
   }

   public InvalidNone() {
      this.failureCountMap = new LinkedHashMap<>();
   }

   public void beginReplacementYield(PlacementExplicit.Owner legacyConstructionType, long replacementYieldTime) {
      if (this.legacyConstructionType != null) {
         throw new IllegalStateException("selected mission must yield before replacement");
      } else {
         this.legacyConstructionType = legacyConstructionType;
         this.replacementYieldTime = replacementYieldTime;
         this.replacementYieldCount++;
      }
   }

   public InvalidNone.Failure validateReplacement(MovementMissionData movementMissionData, Supplier<InvalidNone.Failure> supplier, BooleanSupplier booleanSupplier) {
      if (movementMissionData.isExecutionScope() && !this.isLegacyMissionMatch(movementMissionData)) {
         return InvalidNone.Failure.ACCESS_INVALIDATED;
      } else if (!movementMissionData.isExecutionScope() && this.legacyConstructionType != null) {
         return InvalidNone.Failure.SCHEDULER_REJECT;
      } else {
         InvalidNone.Failure invalidNoneFailure = (InvalidNone.Failure)supplier.get();
         if (invalidNoneFailure != InvalidNone.Failure.NONE) {
            return invalidNoneFailure;
         } else if (movementMissionData.isExecutionScope()) {
            this.noneCount++;
            return InvalidNone.Failure.NONE;
         } else {
            this.replacementYieldCount++;
            this.noneReplacementCount++;
            return booleanSupplier.getAsBoolean() ? InvalidNone.Failure.NONE : InvalidNone.Failure.SCHEDULER_REJECT;
         }
      }
   }

   public static enum Arrival {
      MOVING,
      BUILD_ACCESS,
      BUILD_SUPPORT,
      ON_SURFACE,
      MOVE_COMPLETE,
      EXECUTE_PLACEMENT,
      INVALID;
   }

   public static enum Failure {
      NONE(false),
      PATH_INVALID(true),
      COLLISION(true),
      NO_HEADROOM(true),
      EGRESS_UNSAFE(true),
      ENTRY_GEOMETRY_INVALID(true),
      SCHEDULER_REJECT(false),
      ACCESS_INVALIDATED(false),
      ROUTE_LOOP(false);
      public final boolean retryDifferentEntry;

      private Failure(boolean retryDifferentEntry) {
         this.retryDifferentEntry = retryDifferentEntry;
      }
   }
}
