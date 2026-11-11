package com.corz.client.utils;

public final class WaitInventoryUtils {
   public static boolean canFlag(
      boolean inventoryFlag, boolean falling, boolean inventoryFlag3, boolean inventoryFlag4, boolean inventoryFlag5, boolean inventoryFlag6, boolean inventoryFlag7
   ) {
      return inventoryFlag && !falling && !inventoryFlag3 && !inventoryFlag4 && !inventoryFlag5 && !inventoryFlag6 && !inventoryFlag7;
   }

   public static WaitInventoryUtils.Out getWaitSafe(WaitInventoryUtils.Phase phase, WaitInventoryUtils.In in2) {
      return switch (phase) {
         case IDLE -> WaitInventoryUtils.Out.ofPhase(WaitInventoryUtils.Phase.WAIT_SAFE);
         case WAIT_SAFE -> in2.timedOut()
            ? WaitInventoryUtils.Out.ofFail(WaitInventoryUtils.Fail.UNSAFE_TIMEOUT)
            : (in2.safe() ? WaitInventoryUtils.Out.ofPhase(WaitInventoryUtils.Phase.SWAP) : WaitInventoryUtils.Out.ofPhase(WaitInventoryUtils.Phase.WAIT_SAFE));
         case SWAP -> WaitInventoryUtils.Out.ofPhase(WaitInventoryUtils.Phase.WAIT_SYNC);
         case WAIT_SYNC -> in2.timedOut()
            ? WaitInventoryUtils.Out.ofFail(WaitInventoryUtils.Fail.SYNC_TIMEOUT)
            : (in2.synced() ? WaitInventoryUtils.Out.ofPhase(WaitInventoryUtils.Phase.DONE) : WaitInventoryUtils.Out.ofPhase(WaitInventoryUtils.Phase.WAIT_SYNC));
         case DONE, FAILED -> WaitInventoryUtils.Out.ofPhase(phase);
      };
   }

   private WaitInventoryUtils() {
   }

   public static enum Fail {
      NONE,
      UNSAFE_TIMEOUT,
      SYNC_TIMEOUT;
   }

   public record In(boolean safe, boolean synced, boolean timedOut) {
   }

   public record Out(WaitInventoryUtils.Phase phase, WaitInventoryUtils.Fail fail) {
      static WaitInventoryUtils.Out ofFail(WaitInventoryUtils.Fail f) {
         return new WaitInventoryUtils.Out(WaitInventoryUtils.Phase.FAILED, f);
      }

      static WaitInventoryUtils.Out ofPhase(WaitInventoryUtils.Phase p) {
         return new WaitInventoryUtils.Out(p, WaitInventoryUtils.Fail.NONE);
      }
   }

   public static enum Phase {
      IDLE,
      WAIT_SAFE,
      SWAP,
      WAIT_SYNC,
      DONE,
      FAILED;
   }
}
