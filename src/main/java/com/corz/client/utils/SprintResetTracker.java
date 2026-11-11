package com.corz.client.utils;

public final class SprintResetTracker {
   private volatile boolean sprinting;
   private volatile long sprintStartTime = Long.MIN_VALUE;
   private volatile long lastSprintResetTime = Long.MIN_VALUE;

   public long getSprintStartTime() {
      return this.sprintStartTime;
   }

   public boolean isSprinting() {
      return this.sprinting;
   }

   public long getLastSprintResetTime() {
      return this.lastSprintResetTime;
   }

   public synchronized void recordAttack(boolean playerFlag, long time) {
      if (playerFlag) {
         this.sprinting = false;
         this.lastSprintResetTime = time;
      }
   }

   public synchronized void reset() {
      this.sprinting = false;
      this.sprintStartTime = Long.MIN_VALUE;
      this.lastSprintResetTime = Long.MIN_VALUE;
   }

   public synchronized void onSprintToggle(boolean flag, long time) {
      this.sprinting = flag;
      if (flag) {
         this.sprintStartTime = time;
      }
   }
}
