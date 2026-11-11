package com.corz.client.utils;

import java.util.Locale;
import com.corz.client.utils.PlacementExplicit;

public final class TicksProgress {
   private int progressTicksTotal;
   private int ticksProgressCount;
   private int progressTicksCount;
   private int progressRunTicks;
   private int lastTick = -1073741824;
   private int lifetimeProgressTicks;
   private int stallRunTicks;

   public void update() {
      this.lastTick++;
      if (this.ticksProgressCount >= 0) {
         this.ticksProgressCount++;
      }
   }

   public int getLastTick() {
      return this.lifetimeProgressTicks;
   }

   public int getValue(int ticksProgressValue) {
      return ticksProgressValue - this.lastTick;
   }

   public TicksProgress() {
      this.ticksProgressCount = -1;
   }

   public String getFormat(int ticksProgressValue) {
      return String.format(
         Locale.ROOT,
         "sinceProgress=%d progressEvents=%d movementTicks=%d executionTicks=%d stalls=%d confirmationTimeouts=%d",
         this.getValue(ticksProgressValue),
         this.progressRunTicks,
         this.stallRunTicks,
         this.progressTicksCount,
         this.lifetimeProgressTicks,
         this.progressTicksTotal
      );
   }

   public static boolean canMovePlacement(PlacementExplicit.Type type2, PlacementExplicit.Phase phase) {
      if (type2 == PlacementExplicit.Type.PLACE_TARGET) {
         return false;
      } else {
         return type2 == PlacementExplicit.Type.MOVE_TO_PLACEMENT_STAND ? phase == PlacementExplicit.Phase.MOVE : true;
      }
   }

   public TicksProgress.Verdict getProgressTicks(PlacementExplicit.Type type2, PlacementExplicit.Phase phase, int value) {
      if (canMovePlacement(type2, phase)) {
         this.stallRunTicks++;
         if (value - this.lastTick > 100) {
            this.lifetimeProgressTicks++;
            return TicksProgress.Verdict.MOVEMENT_STALLED;
         } else {
            return TicksProgress.Verdict.ALIVE;
         }
      } else {
         if (this.ticksProgressCount < 0) {
            this.ticksProgressCount = value;
         }

         this.progressTicksCount++;
         if (value - this.ticksProgressCount > 200) {
            this.progressTicksTotal++;
            return TicksProgress.Verdict.ACTION_NO_CONFIRMATION;
         } else {
            return TicksProgress.Verdict.ALIVE;
         }
      }
   }

   public int getProgressTicksCount() {
      return this.progressTicksCount;
   }

   public int getProgressRunTicks() {
      return this.stallRunTicks;
   }

   public void onProgressTick(int value) {
      this.lastTick = value;
      this.progressRunTicks++;
   }

   public void onStallTick(int value) {
      this.lastTick = value;
      this.ticksProgressCount = -1;
      this.progressRunTicks = 0;
      this.stallRunTicks = 0;
      this.progressTicksCount = 0;
   }

   public int getStallRunTicks() {
      return this.progressTicksTotal;
   }

   public int getLifetimeProgressTicks() {
      return this.progressRunTicks;
   }

   public static enum Verdict {
      ALIVE,
      MOVEMENT_STALLED,
      ACTION_NO_CONFIRMATION;
   }
}
