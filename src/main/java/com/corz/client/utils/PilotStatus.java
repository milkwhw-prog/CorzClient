package com.corz.client.utils;

public record PilotStatus(
   String activity,
   int tone,
   String destination,
   double remaining,
   double total,
   long etaSeconds,
   double speedBps,
   int altitude,
   int rockets,
   int rocketTarget,
   int xpBottles,
   int durability,
   int durabilityMax
) {
   public static final PilotStatus INSTANCE = new PilotStatus("Getting ready", 0, "", -1.0, -1.0, -1L, 0.0, 0, 0, 0, 0, -1, -1);

   public float getRouteProgress() {
      return this.total > 0.0 && !Double.isInfinite(this.total) && this.remaining >= 0.0 && !Double.isInfinite(this.remaining)
         ? (float)Math.max(0.0, Math.min(1.0, 1.0 - this.remaining / this.total))
         : 0.0F;
   }

   public float getDurabilityFraction() {
      return this.durability >= 0 && this.durabilityMax > 0 ? Math.max(0.0F, Math.min(1.0F, (float)this.durability / this.durabilityMax)) : -1.0F;
   }
}
