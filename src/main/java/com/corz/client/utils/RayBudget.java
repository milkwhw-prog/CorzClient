package com.corz.client.utils;

public final class RayBudget {
   private int usedRayCount;
   private long lifetimeRayCount;
   private final int rayBudgetLimit;
   private long totalDeniedChecks;
   private final int softRayLimit;
   private int deniedChecksSinceReset;

   public int getSoftRayLimit() {
      return this.softRayLimit;
   }

   public int getUsedRayCount() {
      return this.usedRayCount;
   }

   public int getRayBudgetLimit() {
      return this.rayBudgetLimit;
   }

   public void resetFrame() {
      this.usedRayCount = 0;
      this.deniedChecksSinceReset = 0;
   }

   public int getDeniedChecksSinceReset() {
      return this.deniedChecksSinceReset;
   }

   public long getLifetimeRayCount() {
      return this.lifetimeRayCount;
   }

   public long getTotalDeniedChecks() {
      return this.totalDeniedChecks;
   }

   public RayBudget(int bound, int rocketsBuyBound) {
      this.softRayLimit = Math.max(0, bound);
      this.rayBudgetLimit = Math.max(this.softRayLimit, rocketsBuyBound);
   }

   public boolean tryReserveRays(int bound, int n) {
      int maxRays = Math.max(0, bound);
      boolean timeMax;
      if (n <= 1) {
         timeMax = true;
      } else if (n == 2) {
         timeMax = this.usedRayCount + maxRays <= this.rayBudgetLimit;
      } else {
         timeMax = this.usedRayCount + maxRays <= this.softRayLimit;
      }

      if (timeMax) {
         this.usedRayCount += maxRays;
         this.lifetimeRayCount += maxRays;
      } else {
         this.deniedChecksSinceReset++;
         this.totalDeniedChecks++;
      }

      return timeMax;
   }
}
