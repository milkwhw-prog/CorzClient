package com.corz.client.utils;

public final class MillisCooldownTimer {
   private long lastResetMs = System.currentTimeMillis();

   public long getElapsedMs() {
      return System.currentTimeMillis() - this.lastResetMs;
   }

   public boolean hasElapsed(long autoAggroValue) {
      return this.getElapsedMs() >= autoAggroValue;
   }

   public void restart() {
      this.lastResetMs = System.currentTimeMillis();
   }
}
