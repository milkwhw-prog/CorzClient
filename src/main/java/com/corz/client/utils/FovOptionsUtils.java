package com.corz.client.utils;

import net.minecraft.client.MinecraftClient;

public final class FovOptionsUtils {
   private static volatile float cachedFov = -1.0F;

   public static float getValue() {
      float cachedValue = cachedFov;
      if (cachedValue > 0.0F) {
         return cachedValue;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         return instance != null && instance.options != null && instance.options.getFov() != null ? instance.options.getFov().getValue().floatValue() : 70.0F;
      }
   }

   private FovOptionsUtils() {
   }

   public static void handleAmount(float newFov) {
      if (newFov > 0.0F && Float.isFinite(newFov)) {
         cachedFov = newFov;
      }
   }
}
