package com.corz.client.utils;

public final class WalkerSurface {
   private double z;
   private int surfaceTicks;
   private boolean noneActive;
   private boolean walkerSurfaceActive;
   private double x;
   private double hypot;
   private int noneValue = -1;
   private boolean surfaceTriggered;

   public WalkerSurface.Advice getNoneFlag(boolean noneFlag, double noneValue, double groundY, double hypot, boolean horizontalCollision, boolean noneFlag3) {
      if (Double.isFinite(noneValue) && Double.isFinite(groundY)) {
         double deltaX = noneValue - this.x;
         double deltaZ = groundY - this.z;
         boolean walkerSurfaceActive2 = this.walkerSurfaceActive;
         this.walkerSurfaceActive = true;
         this.x = noneValue;
         this.z = groundY;
         if (!noneFlag) {
            this.updateFlag();
            return WalkerSurface.Advice.NONE;
         } else {
            boolean finite = Double.isFinite(hypot);
            if (!walkerSurfaceActive2) {
               this.updateFlag();
               if (finite) {
                  this.hypot = hypot;
               }

               return WalkerSurface.Advice.NONE;
            } else {
               boolean noneFlag4 = finite && Double.isFinite(this.hypot) && hypot <= this.hypot - 0.05;
               if (!(Math.hypot(deltaX, deltaZ) >= 0.03) && !noneFlag4) {
                  if (finite && (!Double.isFinite(this.hypot) || hypot < this.hypot)) {
                     this.hypot = hypot;
                  }

                  this.surfaceTicks++;
                  if (this.surfaceTicks >= 40) {
                     this.updateFlag();
                     return WalkerSurface.Advice.UNREACHABLE;
                  } else if (this.surfaceTicks >= 22 && !this.surfaceTriggered) {
                     this.surfaceTriggered = true;
                     return WalkerSurface.Advice.STRAFE;
                  } else if (this.surfaceTicks >= 12 && !this.noneActive) {
                     this.noneActive = true;
                     return WalkerSurface.Advice.REPATH;
                  } else if (this.surfaceTicks < 6 || !noneFlag3 || this.noneValue >= 0 && (!horizontalCollision || this.surfaceTicks - this.noneValue < 8)) {
                     return WalkerSurface.Advice.NONE;
                  } else {
                     this.noneValue = this.surfaceTicks;
                     return WalkerSurface.Advice.JUMP;
                  }
               } else {
                  this.updateFlag();
                  if (finite) {
                     this.hypot = hypot;
                  }

                  return WalkerSurface.Advice.NONE;
               }
            }
         }
      } else {
         this.updateFlagWalkerSurfaceActive();
         return WalkerSurface.Advice.NONE;
      }
   }

   private void updateFlag() {
      this.surfaceTicks = 0;
      this.noneValue = -1;
      this.noneActive = false;
      this.surfaceTriggered = false;
      this.hypot = Double.NaN;
   }

   public void updateFlagWalkerSurfaceActive() {
      this.walkerSurfaceActive = false;
      this.x = 0.0;
      this.z = 0.0;
      this.updateFlag();
   }

   public WalkerSurface() {
      this.hypot = Double.NaN;
   }

   public static enum Advice {
      NONE,
      JUMP,
      REPATH,
      STRAFE,
      UNREACHABLE;
   }
}
