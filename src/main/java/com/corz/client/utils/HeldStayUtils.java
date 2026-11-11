package com.corz.client.utils;

import java.util.Map;
import com.corz.client.utils.SupportSnapshot;

public final class HeldStayUtils {
   public static HeldStayUtils.LayerAction decideLayerAction(
      int stayAdvanceValue, int n69, int stayAdvanceLimit, boolean stayAdvanceFlag, boolean stayAdvanceFlag2, int n42
   ) {
      if (!SupportSnapshot.canFlag(stayAdvanceValue, n69)) {
         return HeldStayUtils.LayerAction.STAY_WORK_REMAINS;
      } else if (n42 > 0) {
         return HeldStayUtils.LayerAction.STAY_HELD_ESCALATE;
      } else if (!stayAdvanceFlag) {
         return HeldStayUtils.LayerAction.ADVANCE_TO_FRONTIER;
      } else if (stayAdvanceLimit > 0) {
         return HeldStayUtils.LayerAction.STAY_ADMIT_CONTAINERS;
      } else {
         return stayAdvanceFlag2 ? HeldStayUtils.LayerAction.ADVANCE_TO_FRONTIER : HeldStayUtils.LayerAction.STAY_NOTHING_ABOVE;
      }
   }

   public static long hashLayerState(
      long heldStayValue,
      int heldStayValue2,
      int heldStayValue3,
      long heldStayValue4,
      Map<Long, Integer> map,
      int heldStayLimit,
      int heldStayValue5,
      int heldStayValue6,
      boolean heldStayFlag,
      long heldStayValue7,
      int heldStayValue8
   ) {
      int heldStayCounter = 0;

      for (int heldStayValue9 : map.values()) {
         if (heldStayLimit >= heldStayValue9) {
            heldStayCounter++;
         }
      }

      long heldStayValue10 = -3750763034362895579L;

      for (long heldStayValue11 : new long[]{
         heldStayValue,
         heldStayValue2,
         heldStayValue3,
         heldStayValue4,
         map.size(),
         heldStayCounter,
         heldStayValue5,
         heldStayValue6,
         heldStayFlag ? 1L : 0L,
         heldStayValue7,
         heldStayValue8
      }) {
         heldStayValue10 = (heldStayValue10 ^ heldStayValue11) * 1099511628211L;
      }

      return heldStayValue10;
   }

   private HeldStayUtils() {
   }

   public static HeldStayUtils.Suppression classifyReopenSuppression(
      int reopenHeldValue, int reopenHeldLimit, long reopenHeldValue2, long reopenHeldValue3, boolean reopenHeldFlag
   ) {
      if (reopenHeldValue <= reopenHeldLimit) {
         return HeldStayUtils.Suppression.HELD_NEAR;
      } else if (reopenHeldValue2 != reopenHeldValue3) {
         return HeldStayUtils.Suppression.REOPEN_WORLD_CHANGED;
      } else {
         return reopenHeldFlag ? HeldStayUtils.Suppression.HELD : HeldStayUtils.Suppression.REOPEN_DIFFERENT_COMPONENT;
      }
   }

   public static boolean isHeldSuppression(HeldStayUtils.Suppression suppression) {
      return suppression == HeldStayUtils.Suppression.HELD || suppression == HeldStayUtils.Suppression.HELD_NEAR;
   }

   public static HeldStayUtils.LayerAction decideLayerActionWithoutEscalation(
      int stayAdmitValue, int n69, int stayAdvanceLimit, boolean stayAdmitFlag, boolean stayAdmitFlag2
   ) {
      return decideLayerAction(stayAdmitValue, n69, stayAdvanceLimit, stayAdmitFlag, stayAdmitFlag2, 0);
   }

   public static HeldStayUtils.Decision decideNextStep(HeldStayUtils.Census census, int blockedValue) {
      if (census.getExecutableCount() > 0) {
         return HeldStayUtils.Decision.DISPATCH;
      } else if (census.getNeedsAccessConstructionCount() > 0) {
         return HeldStayUtils.Decision.DISCOVER_ACCESS;
      } else if (!SupportSnapshot.canFlag(census.getExecutableCount(), blockedValue, census.getBlockedCount())) {
         return HeldStayUtils.Decision.BLOCKED_WAIT;
      } else {
         return census.getCensusPassCount() > 0 ? HeldStayUtils.Decision.ADVANCE_FRONTIER : HeldStayUtils.Decision.BLOCKED_WAIT;
      }
   }

   public static HeldStayUtils.Cls classifyCell(HeldStayUtils.CellFacts cellFacts) {
      if (cellFacts.complete()) {
         return HeldStayUtils.Cls.COMPLETE;
      } else if (cellFacts.visibleNow()) {
         return HeldStayUtils.Cls.EXECUTABLE_NOW;
      } else if (cellFacts.structurallyDead()) {
         return HeldStayUtils.Cls.STRUCTURALLY_BLOCKED;
      } else if (cellFacts.hasProvenStand()) {
         return HeldStayUtils.Cls.EXECUTABLE_AFTER_WALK;
      } else if (cellFacts.causallyDeferred()) {
         return HeldStayUtils.Cls.DEFERRED_CAUSAL;
      } else if (cellFacts.temporarilyForbidden()) {
         return HeldStayUtils.Cls.TEMPORARILY_FORBIDDEN;
      } else if (!cellFacts.materialAvailable()) {
         return HeldStayUtils.Cls.MISSING_MATERIAL;
      } else if (cellFacts.waitingDependency()) {
         return HeldStayUtils.Cls.WAITING_DEPENDENCY;
      } else {
         return cellFacts.accessConstructible() ? HeldStayUtils.Cls.NEEDS_ACCESS_CONSTRUCTION : HeldStayUtils.Cls.STRUCTURALLY_BLOCKED;
      }
   }

   public record CellFacts(
      boolean complete,
      boolean visibleNow,
      boolean hasProvenStand,
      boolean accessConstructible,
      boolean materialAvailable,
      boolean waitingDependency,
      boolean structurallyDead,
      boolean causallyDeferred,
      boolean temporarilyForbidden
   ) {
   }

   public static final class Census {
      private int censusPassCount;
      private final int[] classCountTable = new int[HeldStayUtils.Cls.values().length];

      public void recordCensusPass() {
         this.censusPassCount++;
      }

      public void recordCell(HeldStayUtils.Cls cls) {
         this.classCountTable[cls.ordinal()]++;
      }

      public int getExecutableCount() {
         return this.classCountTable[HeldStayUtils.Cls.EXECUTABLE_NOW.ordinal()] + this.classCountTable[HeldStayUtils.Cls.EXECUTABLE_AFTER_WALK.ordinal()];
      }

      public int getCensusPassCount() {
         return this.censusPassCount;
      }

      public int getClassCount(HeldStayUtils.Cls cls) {
         return this.classCountTable[cls.ordinal()];
      }

      public int getIncompleteCount() {
         int complete = 0;

         for (HeldStayUtils.Cls cls : HeldStayUtils.Cls.values()) {
            if (cls != HeldStayUtils.Cls.COMPLETE) {
               complete += this.classCountTable[cls.ordinal()];
            }
         }

         return complete;
      }

      public int getNeedsAccessConstructionCount() {
         return this.classCountTable[HeldStayUtils.Cls.NEEDS_ACCESS_CONSTRUCTION.ordinal()];
      }

      public int getBlockedCount() {
         return this.classCountTable[HeldStayUtils.Cls.DEFERRED_CAUSAL.ordinal()]
            + this.classCountTable[HeldStayUtils.Cls.MISSING_MATERIAL.ordinal()]
            + this.classCountTable[HeldStayUtils.Cls.TEMPORARILY_FORBIDDEN.ordinal()]
            + this.classCountTable[HeldStayUtils.Cls.NEEDS_ACCESS_CONSTRUCTION.ordinal()];
      }
   }

   public static enum Cls {
      EXECUTABLE_NOW(true),
      EXECUTABLE_AFTER_WALK(true),
      NEEDS_ACCESS_CONSTRUCTION(false),
      WAITING_DEPENDENCY(false),
      MISSING_MATERIAL(false),
      TEMPORARILY_FORBIDDEN(false),
      STRUCTURALLY_BLOCKED(false),
      DEFERRED_CAUSAL(false),
      COMPLETE(false);
      private final boolean executable;

      public boolean isExecutable() {
         return this.executable;
      }

      private Cls(boolean flag2) {
         this.executable = flag2;
      }
   }

   public static enum Decision {
      DISPATCH,
      DISCOVER_ACCESS,
      ADVANCE_FRONTIER,
      BLOCKED_WAIT;
   }

   public static enum LayerAction {
      STAY_WORK_REMAINS,
      STAY_ADMIT_CONTAINERS,
      ADVANCE_TO_FRONTIER,
      STAY_NOTHING_ABOVE,
      STAY_HELD_ESCALATE;
   }

   public static enum Suppression {
      HELD,
      REOPEN_DIFFERENT_COMPONENT,
      REOPEN_WORLD_CHANGED,
      HELD_NEAR;
   }
}
