package com.corz.client.utils;

public final class DeltaHeadroomUtils {
   public static final int HEADROOM_MIN_BLOCKS = 1;

   public static boolean isHeightOrFootingFailure(DeltaHeadroomUtils.Result result) {
      return result == DeltaHeadroomUtils.Result.TOO_HIGH || result == DeltaHeadroomUtils.Result.UNSAFE_DROP || result == DeltaHeadroomUtils.Result.NO_FOOTING;
   }

   public static DeltaHeadroomUtils.Result evaluateStep(DeltaHeadroomUtils.Step step) {
      if (step.blockedMemory()) {
         return DeltaHeadroomUtils.Result.BLOCKED_MEMORY;
      } else if (step.deltaY() > 1) {
         return DeltaHeadroomUtils.Result.TOO_HIGH;
      } else if (step.deltaY() < -2) {
         return DeltaHeadroomUtils.Result.UNSAFE_DROP;
      } else if (!step.nextPassable() || !step.headroom()) {
         return DeltaHeadroomUtils.Result.NO_HEADROOM;
      } else if (step.deltaY() > 0 && !step.takeoffHeadroom()) {
         return DeltaHeadroomUtils.Result.NO_TAKEOFF_HEADROOM;
      } else if (step.deltaY() < 0 && !step.dropEntryClear()) {
         return DeltaHeadroomUtils.Result.DROP_ENTRY_BLOCKED;
      } else {
         return !step.nextStandable() && !step.supportBelowNext() ? DeltaHeadroomUtils.Result.NO_FOOTING : DeltaHeadroomUtils.Result.OK;
      }
   }

   public static boolean isStepOk(DeltaHeadroomUtils.Step step) {
      return evaluateStep(step) == DeltaHeadroomUtils.Result.OK;
   }

   public static DeltaHeadroomUtils.Primitive getPrimitiveForKind(DeltaHeadroomUtils.Kind kind) {
      return switch (kind) {
         case WALK_FLAT, CHEST_TOP -> DeltaHeadroomUtils.Primitive.WALK;
         case WALK_DIAGONAL -> DeltaHeadroomUtils.Primitive.WALK_DIAGONAL;
         case STEP_UP, JUMP_MOUNT, OWNED_DIRT_STEP -> DeltaHeadroomUtils.Primitive.JUMP;
         case DROP -> DeltaHeadroomUtils.Primitive.CONTROLLED_DROP;
         case GAP_JUMP -> DeltaHeadroomUtils.Primitive.SPRINT_JUMP;
         case BRIDGE_APPROACH, PILLAR_APPROACH, NARROW_EDGE, CONSTRUCTIVE_ACCESS_STEP -> DeltaHeadroomUtils.Primitive.EDGE_WALK;
      };
   }

   public static boolean isKindAllowed(DeltaHeadroomUtils.Kind kind, boolean walkJumpFlag, boolean edgesBannedFlag) {
      if (kind == null) {
         return false;
      } else {
         return edgesBannedFlag
            ? kind == DeltaHeadroomUtils.Kind.WALK_FLAT
               || kind == DeltaHeadroomUtils.Kind.WALK_DIAGONAL
               || kind == DeltaHeadroomUtils.Kind.NARROW_EDGE
               || kind == DeltaHeadroomUtils.Kind.OWNED_DIRT_STEP
            : walkJumpFlag || getPrimitiveForKind(kind) != DeltaHeadroomUtils.Primitive.JUMP && getPrimitiveForKind(kind) != DeltaHeadroomUtils.Primitive.SPRINT_JUMP;
      }
   }

   public static boolean hasPrimitive(DeltaHeadroomUtils.Kind kind) {
      return kind != null && getPrimitiveForKind(kind) != null;
   }

   public static DeltaHeadroomUtils.Result evaluateEdge(DeltaHeadroomUtils.Edge edge) {
      int absDeltaX = Math.abs(edge.deltaX());
      int absDeltaZ = Math.abs(edge.deltaZ());
      boolean deltaHeadroomFlag = absDeltaX == 1 && absDeltaZ == 1;
      boolean deltaHeadroomFlag2 = absDeltaX + absDeltaZ == 2 && (absDeltaX == 2 || absDeltaZ == 2);
      if ((deltaHeadroomFlag2 || absDeltaX <= 1 && absDeltaZ <= 1) && (absDeltaX != 0 || absDeltaZ != 0 || edge.deltaY() != 0)) {
         if (deltaHeadroomFlag && !edge.diagonalClear()) {
            return DeltaHeadroomUtils.Result.CORNER_BLOCKED;
         } else if (!deltaHeadroomFlag2 || edge.deltaY() <= 0 && edge.deltaY() >= -1) {
            if (deltaHeadroomFlag2 && !edge.takeoffHeadroom()) {
               return DeltaHeadroomUtils.Result.NO_TAKEOFF_HEADROOM;
            } else {
               return deltaHeadroomFlag2 && !edge.gapClear()
                  ? DeltaHeadroomUtils.Result.GAP_BLOCKED
                  : evaluateStep(
                     new DeltaHeadroomUtils.Step(
                        edge.deltaY(),
                        edge.nextStandable(),
                        edge.nextPassable(),
                        edge.headroom(),
                        edge.supportBelowNext(),
                        edge.blockedMemory(),
                        edge.takeoffHeadroom(),
                        edge.dropEntryClear()
                     )
                  );
            }
         } else {
            return DeltaHeadroomUtils.Result.UNSUPPORTED_GEOMETRY;
         }
      } else {
         return DeltaHeadroomUtils.Result.UNSUPPORTED_GEOMETRY;
      }
   }

   private DeltaHeadroomUtils() {
   }

   public static DeltaHeadroomUtils.Kind classifyEdge(DeltaHeadroomUtils.Edge edge) {
      int absDeltaX = Math.abs(edge.deltaX());
      int absDeltaZ = Math.abs(edge.deltaZ());
      if (absDeltaX + absDeltaZ != 2 || absDeltaX != 2 && absDeltaZ != 2) {
         if (edge.ownedDirtFloor()) {
            return DeltaHeadroomUtils.Kind.OWNED_DIRT_STEP;
         } else if (edge.hint() == DeltaHeadroomUtils.Hint.JUMP_MOUNT) {
            return DeltaHeadroomUtils.Kind.JUMP_MOUNT;
         } else if (edge.deltaY() > 0) {
            return DeltaHeadroomUtils.Kind.STEP_UP;
         } else if (edge.deltaY() < 0) {
            return DeltaHeadroomUtils.Kind.DROP;
         } else if (edge.hint() == DeltaHeadroomUtils.Hint.CONSTRUCTIVE) {
            return DeltaHeadroomUtils.Kind.CONSTRUCTIVE_ACCESS_STEP;
         } else if (edge.hint() == DeltaHeadroomUtils.Hint.PILLAR) {
            return DeltaHeadroomUtils.Kind.PILLAR_APPROACH;
         } else if (edge.hint() == DeltaHeadroomUtils.Hint.BRIDGE) {
            return DeltaHeadroomUtils.Kind.BRIDGE_APPROACH;
         } else if (edge.chestFloor()) {
            return DeltaHeadroomUtils.Kind.CHEST_TOP;
         } else if (edge.narrowEdge()) {
            return DeltaHeadroomUtils.Kind.NARROW_EDGE;
         } else {
            return absDeltaX == 1 && absDeltaZ == 1 ? DeltaHeadroomUtils.Kind.WALK_DIAGONAL : DeltaHeadroomUtils.Kind.WALK_FLAT;
         }
      } else {
         return DeltaHeadroomUtils.Kind.GAP_JUMP;
      }
   }

   public record Edge(
      int deltaX,
      int deltaY,
      int deltaZ,
      boolean nextStandable,
      boolean nextPassable,
      boolean headroom,
      boolean supportBelowNext,
      boolean blockedMemory,
      boolean diagonalClear,
      boolean gapClear,
      boolean ownedDirtFloor,
      boolean chestFloor,
      boolean narrowEdge,
      boolean takeoffHeadroom,
      boolean dropEntryClear,
      DeltaHeadroomUtils.Hint hint
   ) {
      public Edge(
         int deltaX,
         int deltaY,
         int deltaZ,
         boolean nextStandable,
         boolean nextPassable,
         boolean headroom,
         boolean supportBelowNext,
         boolean blockedMemory,
         boolean diagonalClear,
         boolean gapClear,
         boolean ownedDirtFloor,
         boolean chestFloor,
         boolean narrowEdge,
         boolean takeoffHeadroom,
         boolean dropEntryClear,
         DeltaHeadroomUtils.Hint hint
      ) {
         hint = hint == null ? DeltaHeadroomUtils.Hint.NONE : hint;
         this.deltaX = deltaX;
         this.deltaY = deltaY;
         this.deltaZ = deltaZ;
         this.nextStandable = nextStandable;
         this.nextPassable = nextPassable;
         this.headroom = headroom;
         this.supportBelowNext = supportBelowNext;
         this.blockedMemory = blockedMemory;
         this.diagonalClear = diagonalClear;
         this.gapClear = gapClear;
         this.ownedDirtFloor = ownedDirtFloor;
         this.chestFloor = chestFloor;
         this.narrowEdge = narrowEdge;
         this.takeoffHeadroom = takeoffHeadroom;
         this.dropEntryClear = dropEntryClear;
         this.hint = hint;
      }

      public Edge(
         int deltaZ,
         int diagonalClear,
         int supportBelowNext,
         boolean hint,
         boolean chestFloor,
         boolean nextStandable,
         boolean blockedMemory,
         boolean ownedDirtFloor,
         boolean nextPassable,
         boolean headroom,
         boolean gapClear,
         boolean deltaY,
         boolean narrowEdge,
         DeltaHeadroomUtils.Hint deltaX
      ) {
         this(
            deltaZ,
            diagonalClear,
            supportBelowNext,
            hint,
            chestFloor,
            nextStandable,
            blockedMemory,
            ownedDirtFloor,
            nextPassable,
            headroom,
            gapClear,
            deltaY,
            narrowEdge,
            true,
            true,
            deltaX
         );
      }

      public Edge(
         int chestFloor,
         int headroom,
         int narrowEdge,
         boolean deltaX,
         boolean supportBelowNext,
         boolean hint,
         boolean takeoffHeadroom,
         boolean gapClear,
         boolean nextPassable,
         boolean nextStandable,
         boolean deltaY,
         boolean ownedDirtFloor,
         boolean deltaZ,
         boolean blockedMemory,
         DeltaHeadroomUtils.Hint diagonalClear
      ) {
         this(
            chestFloor,
            headroom,
            narrowEdge,
            deltaX,
            supportBelowNext,
            hint,
            takeoffHeadroom,
            gapClear,
            nextPassable,
            nextStandable,
            deltaY,
            ownedDirtFloor,
            deltaZ,
            blockedMemory,
            true,
            diagonalClear
         );
      }
   }

   public static enum Hint {
      NONE,
      JUMP_MOUNT,
      BRIDGE,
      PILLAR,
      CONSTRUCTIVE;
   }

   public static enum Kind {
      WALK_FLAT,
      WALK_DIAGONAL,
      STEP_UP,
      JUMP_MOUNT,
      DROP,
      GAP_JUMP,
      BRIDGE_APPROACH,
      PILLAR_APPROACH,
      OWNED_DIRT_STEP,
      CHEST_TOP,
      NARROW_EDGE,
      CONSTRUCTIVE_ACCESS_STEP;
   }

   public static enum Primitive {
      WALK,
      WALK_DIAGONAL,
      JUMP,
      CONTROLLED_DROP,
      SPRINT_JUMP,
      EDGE_WALK;
   }

   public static enum Result {
      OK,
      BLOCKED_MEMORY,
      TOO_HIGH,
      UNSAFE_DROP,
      NO_HEADROOM,
      NO_TAKEOFF_HEADROOM,
      NO_FOOTING,
      UNSUPPORTED_GEOMETRY,
      CORNER_BLOCKED,
      GAP_BLOCKED,
      DROP_ENTRY_BLOCKED;
   }

   public record Step(
      int deltaY,
      boolean nextStandable,
      boolean nextPassable,
      boolean headroom,
      boolean supportBelowNext,
      boolean blockedMemory,
      boolean takeoffHeadroom,
      boolean dropEntryClear
   ) {
      public Step(
         int blockedMemory, boolean nextPassable, boolean nextStandable, boolean headroom, boolean takeoffHeadroom, boolean supportBelowNext, boolean deltaY
      ) {
         this(blockedMemory, nextPassable, nextStandable, headroom, takeoffHeadroom, supportBelowNext, deltaY, true);
      }

      public Step(int deltaY, boolean headroom, boolean nextStandable, boolean blockedMemory, boolean nextPassable, boolean supportBelowNext) {
         this(deltaY, headroom, nextStandable, blockedMemory, nextPassable, supportBelowNext, true, true);
      }
   }
}
