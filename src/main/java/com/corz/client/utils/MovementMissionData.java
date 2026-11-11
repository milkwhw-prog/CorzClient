package com.corz.client.utils;

import java.util.Objects;
import com.corz.client.utils.PlacementExplicit;

public record MovementMissionData(
   PlacementExplicit.Owner owner,
   long missionId,
   MovementMissionData.Purpose purpose,
   long destination,
   Long associatedTarget,
   int missionExpectedValue,
   MovementMissionData.Scope scope
) {
   public boolean isPurposeMove() {
      return this.associatedTarget != null
         && (
            this.purpose == MovementMissionData.Purpose.LEGACY_TARGET_RELOCATION
               || this.purpose == MovementMissionData.Purpose.MISSION_STAND_MOVE
               || this.purpose == MovementMissionData.Purpose.DECK_LOCAL_MOVE
         );
   }

   public String formatMissionAssociated() {
      return "movementOwner="
         + this.owner
         + " movementPurpose="
         + this.purpose
         + " movementMissionId="
         + this.missionId
         + " destination="
         + this.destination
         + " associatedTarget="
         + this.associatedTarget
         + " missionExpectedValue="
         + this.missionExpectedValue
         + " scope="
         + this.scope;
   }

   public PlacementExplicit.Type getMovePurpose() {
      if (this.isPurposeMove()) {
         return PlacementExplicit.Type.MOVE_TO_PLACEMENT_STAND;
      } else {
         return switch (this.purpose) {
            case DECK_ENTRY -> PlacementExplicit.Type.ENTER_SURFACE;
            default -> PlacementExplicit.Type.MOVE_ONLY;
            case ACCESS_APPROACH -> PlacementExplicit.Type.MOVE_TO_ACCESS_BASE;
            case TEMP_SUPPORT_APPROACH -> PlacementExplicit.Type.MOVE_TO_SUPPORT;
         };
      }
   }

   public MovementMissionData(
      PlacementExplicit.Owner owner,
      long missionId,
      MovementMissionData.Purpose purpose,
      long destination,
      Long associatedTarget,
      int missionExpectedValue,
      MovementMissionData.Scope scope
   ) {
      Objects.requireNonNull(owner);
      Objects.requireNonNull(purpose);
      missionExpectedValue = Math.max(0, missionExpectedValue);
      this.owner = owner;
      this.missionId = missionId;
      this.purpose = purpose;
      this.destination = destination;
      this.associatedTarget = associatedTarget;
      this.missionExpectedValue = missionExpectedValue;
      this.scope = scope;
   }

   public boolean isExecutionScope() {
      return this.scope == MovementMissionData.Scope.MISSION_EXECUTION;
   }

   public static enum Purpose {
      LEGACY_TARGET_RELOCATION,
      DECK_ENTRY,
      DECK_LOCAL_MOVE,
      ACCESS_APPROACH,
      TEMP_SUPPORT_APPROACH,
      CLEANUP_ROUTE,
      RESTOCK_ROUTE,
      MISSION_STAND_MOVE,
      EGRESS,
      RUN_ENTRY;
   }

   public static enum Scope {
      GLOBAL_SELECTION,
      MISSION_EXECUTION;
   }
}
