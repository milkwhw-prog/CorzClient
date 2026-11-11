package com.corz.client.utils;

import java.util.Objects;
import com.corz.client.utils.MovementMissionData;

public final class PlacementExplicit<P> {
   public final P executorUnavailableObject;
   public final long actionMissionTime;
   public final PlacementExplicit.Owner owner;
   public final PlacementExplicit.Type actionType;
   private String explicitFailureText;
   public final long createdTick;
   public final long egressActionTime;
   private PlacementExplicit.Phase revalidateAlignType;
   public final long actionTargetPos;

   public void handleUnresolvedAlign(PlacementExplicit.Phase revalidateAlignType) {
      if (!this.isPlacementMove() || !this.isRevalidateAlign()) {
         throw new IllegalStateException("not an unresolved placement");
      } else if (revalidateAlignType != PlacementExplicit.Phase.ALIGN && revalidateAlignType != PlacementExplicit.Phase.PLACE && revalidateAlignType != PlacementExplicit.Phase.WAIT_CONFIRMATION) {
         throw new IllegalArgumentException("not an execution phase: " + revalidateAlignType);
      } else if (this.revalidateAlignType == PlacementExplicit.Phase.MOVE) {
         throw new IllegalStateException("stand not reached");
      } else {
         this.revalidateAlignType = revalidateAlignType;
      }
   }

   public boolean isPlacementMove() {
      return this.actionType == PlacementExplicit.Type.MOVE_TO_PLACEMENT_STAND || this.actionType == PlacementExplicit.Type.PLACE_TARGET;
   }

   public boolean isRevalidateAlign() {
      return this.revalidateAlignType != PlacementExplicit.Phase.COMPLETED && this.revalidateAlignType != PlacementExplicit.Phase.INVALIDATED;
   }

   private PlacementExplicit(
      PlacementExplicit.Owner owner2, long actionMissionTime, PlacementExplicit.Type type2, long actionIllegalTime, long egressActionTime, long time, P p, boolean placementExplicitFlag
   ) {
      this.actionType = Objects.requireNonNull(type2);
      this.owner = Objects.requireNonNull(owner2);
      this.actionMissionTime = actionMissionTime;
      this.actionTargetPos = actionIllegalTime;
      this.egressActionTime = egressActionTime;
      this.createdTick = time;
      this.executorUnavailableObject = (P)(this.isPlacementMove() ? Objects.requireNonNull((P)p) : p);
      this.revalidateAlignType = placementExplicitFlag ? PlacementExplicit.Phase.MOVE : PlacementExplicit.Phase.REVALIDATE;
   }

   public boolean canActionIllegal(long actionIllegalValue, long actionIllegalValue2) {
      return this.isPlacementMove() && this.isRevalidateAlign() && this.actionTargetPos == actionIllegalValue && this.egressActionTime == actionIllegalValue2;
   }

   public static <P> PlacementExplicit<P> getRequiresRecipe(MovementMissionData movementMissionData, long requiresRecipeValue) {
      if (movementMissionData.isPurposeMove()) {
         throw new IllegalArgumentException("placement requires its recipe");
      } else {
         return new PlacementExplicit<>(movementMissionData.owner(), movementMissionData.missionId(), movementMissionData.getMovePurpose(), 0L, movementMissionData.destination(), requiresRecipeValue, null, true);
      }
   }

   public boolean canRevalidateCompleted(long revalidateCompletedValue) {
      if (!this.isPlacementMove() && this.isRevalidateAlign() && revalidateCompletedValue == this.egressActionTime) {
         this.revalidateAlignType = PlacementExplicit.Phase.COMPLETED;
         return true;
      } else {
         return false;
      }
   }

   public PlacementExplicit.Phase getRevalidateAlignType() {
      return this.revalidateAlignType;
   }

   public void handleFailureExplicit(String explicitFailureText) {
      if (explicitFailureText == null || explicitFailureText.isBlank()) {
         throw new IllegalArgumentException("failure must be explicit");
      } else if (this.isRevalidateAlign()) {
         this.explicitFailureText = explicitFailureText;
         this.revalidateAlignType = PlacementExplicit.Phase.INVALIDATED;
      }
   }

   public boolean canRevalidateAlign(long revalidateAlignValue) {
      if (this.isRevalidateAlign() && revalidateAlignValue == this.egressActionTime) {
         if (this.revalidateAlignType == PlacementExplicit.Phase.MOVE) {
            this.revalidateAlignType = PlacementExplicit.Phase.REVALIDATE;
         }

         return true;
      } else {
         return false;
      }
   }

   public Long getIllegalAction() {
      return this.isPlacementMove() ? this.actionTargetPos : null;
   }

   public String getExplicitFailureText() {
      return this.explicitFailureText;
   }

   public PlacementExplicit(PlacementExplicit.Owner owner2, long placementExplicitValue, long actionIllegalTime, long placementExplicitValue3, long placementExplicitValue4, P p, boolean placementExplicitFlag) {
      this(
         owner2,
         placementExplicitValue,
         placementExplicitFlag ? PlacementExplicit.Type.MOVE_TO_PLACEMENT_STAND : PlacementExplicit.Type.PLACE_TARGET,
         actionIllegalTime,
         placementExplicitValue3,
         placementExplicitValue4,
         (P)p,
         placementExplicitFlag
      );
   }

   public boolean canRevalidateIllegal(long revalidateIllegalValue) {
      if (this.isPlacementMove() && this.isRevalidateAlign() && this.actionTargetPos == revalidateIllegalValue) {
         this.revalidateAlignType = PlacementExplicit.Phase.COMPLETED;
         return true;
      } else {
         return false;
      }
   }

   public static enum Owner {
      DECK,
      CONSTRUCTION,
      COMPLETION,
      ACCESS,
      LOCAL_SWEEP,
      LEGACY,
      CLEANUP,
      RESTOCK,
      EGRESS;
   }

   public static enum Phase {
      MOVE,
      REVALIDATE,
      ALIGN,
      PLACE,
      WAIT_CONFIRMATION,
      COMPLETED,
      INVALIDATED;
   }

   public static enum Type {
      MOVE_TO_ACCESS_BASE,
      MOVE_TO_SUPPORT,
      ENTER_SURFACE,
      MOVE_ONLY,
      MOVE_TO_PLACEMENT_STAND,
      PLACE_TARGET;
   }
}
