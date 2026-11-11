package com.corz.client.utils;

public record ElytraStatus(String activity, int tone, int found, int checked, int rockets, int rocketMin, int durability, int durabilityMax, double distance) {
   public static final ElytraStatus INSTANCE = new ElytraStatus("Getting ready", 0, 0, 0, 0, 0, -1, -1, -1.0);

   public float getDurabilityFraction() {
      return this.durability >= 0 && this.durabilityMax > 0 ? Math.max(0.0F, Math.min(1.0F, (float)this.durability / this.durabilityMax)) : -1.0F;
   }
}
