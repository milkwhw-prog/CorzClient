package com.corz.client.utils;

public final class RetaliationFilter {
   private int lastAttackerId;
   private int currentTargetId = -1;
   private long lastAttackTimeMs;
   private long targetLostSinceMs = -1L;
   private boolean blocked;

   public RetaliationFilter() {
      this.lastAttackerId = -1;
   }

   public void trackTargetLoss(long time) {
      if (this.currentTargetId != -1) {
         if (this.targetLostSinceMs < 0L) {
            this.targetLostSinceMs = time;
         } else {
            if (time - this.targetLostSinceMs >= 1000L) {
               this.resetTarget();
            }
         }
      }
   }

   public void recordAttacker(int triggerLimit, long time) {
      if (triggerLimit >= 0) {
         this.lastAttackerId = triggerLimit;
         this.lastAttackTimeMs = time;
         if (this.blocked && triggerLimit == this.currentTargetId) {
            this.blocked = false;
         }
      }
   }

   public boolean isTargetAllowed(int triggerValue, long nanoTimeScaled) {
      this.targetLostSinceMs = -1L;
      if (triggerValue != this.currentTargetId) {
         this.currentTargetId = triggerValue;
         this.blocked = triggerValue != this.lastAttackerId || nanoTimeScaled - this.lastAttackTimeMs > 3000L;
      }

      return !this.blocked;
   }

   public void resetAll() {
      this.resetTarget();
      this.lastAttackerId = -1;
      this.lastAttackTimeMs = 0L;
   }

   public void resetTarget() {
      this.currentTargetId = -1;
      this.blocked = false;
      this.targetLostSinceMs = -1L;
   }
}
