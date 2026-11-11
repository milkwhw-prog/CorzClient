package com.corz.client.utils;

import com.corz.client.world.VecPosUtilsBreak;

public record PingTargetData(double pingMs, double targetPingMs) {
   public int getMax() {
      return Math.max(0, (int)Math.round(this.targetPingMs / 50.0));
   }

   public int getPing() {
      return VecPosUtilsBreak.getValue(this.pingMs);
   }

   public double getAmount() {
      return this.getPingMs() + 0.5;
   }

   public double getPingMs() {
      return this.pingMs / 50.0;
   }

   public double getPingPingMs() {
      return this.pingMs / 100.0;
   }
}
