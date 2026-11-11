package com.corz.client.utils;

public record RotationDelta(int yaw, int pitch) {
   public static final RotationDelta INSTANCE = new RotationDelta(0, 0);

   public boolean isZero() {
      return this.yaw == 0 && this.pitch == 0;
   }

   static int clampDeltaToLimit(int delta, int limit, int net) {
      long pitchYawValue = Math.max((long)(-net), Math.min((long)net, (long)delta + limit));
      return (int)(pitchYawValue - delta);
   }
}
