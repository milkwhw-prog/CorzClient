package com.corz.client.render;

import net.minecraft.client.render.Frustum;

public final class FrustumUtils {
   private static Frustum frustum;

   public static void handleFrustum(Frustum newFrustum) {
      frustum = newFrustum;
   }

   private FrustumUtils() {
   }

   public static Frustum getFrustum() {
      return frustum;
   }
}
