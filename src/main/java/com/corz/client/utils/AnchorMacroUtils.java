package com.corz.client.utils;

import net.minecraft.util.math.BlockPos;

public final class AnchorMacroUtils {
   private static long lastRecordedTimeMs = Long.MIN_VALUE;

   public static void recordInteractTime(long time2) {
      lastRecordedTimeMs = time2;
   }

   public static boolean canInteractAt(long anchorMacroValue) {
      return lastRecordedTimeMs != anchorMacroValue;
   }

   private AnchorMacroUtils() {
   }

   public static void clearRecordedTime() {
      lastRecordedTimeMs = Long.MIN_VALUE;
   }

   public static final class Refusals {
      private int refusalCount;
      private boolean limitReported;
      private BlockPos trackedPos;

      public void reset() {
         this.trackedPos = null;
         this.refusalCount = 0;
         this.limitReported = false;
      }

      public void resetIfSamePos(BlockPos pos) {
         if (pos != null && pos.equals(this.trackedPos)) {
            this.reset();
         }
      }

      public boolean isRefusalLimitReached(BlockPos pos) {
         return pos != null && pos.equals(this.trackedPos) && this.refusalCount >= 2;
      }

      public boolean recordRefusal(BlockPos pos) {
         if (pos == null) {
            return false;
         } else {
            if (!pos.equals(this.trackedPos)) {
               this.trackedPos = pos.toImmutable();
               this.refusalCount = 0;
               this.limitReported = false;
            }

            this.refusalCount++;
            if (this.refusalCount >= 2 && !this.limitReported) {
               this.limitReported = true;
               return true;
            } else {
               return false;
            }
         }
      }

      public int getRefusalCount(BlockPos pos) {
         return pos != null && pos.equals(this.trackedPos) ? this.refusalCount : 0;
      }
   }
}
