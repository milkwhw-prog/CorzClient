package com.corz.client.utils;

public final class PendingEntityHitTracker {
   private int escalationLevel = 1;
   private int consecutiveStreak;
   private long pendingDeadlineTick;
   private int pendingEntityId = -1;
   public static final int DEFAULT_ESCALATION_LEVEL = 1;

   public static int getRemainingTicksFloor(int playerValue, double playerBound) {
      if (playerValue == Integer.MAX_VALUE) {
         return 0;
      } else {
         double playerValue2 = Double.isFinite(playerBound) ? Math.max(0.0, playerBound) : 0.0;
         return Math.max(0, (int)Math.floor(10.0 - playerValue2 / 50.0) - 1 - playerValue);
      }
   }

   public void onPendingTimeout(long playerLimit, boolean playerFlag) {
      if (this.pendingEntityId >= 0 && playerLimit > this.pendingDeadlineTick) {
         this.pendingEntityId = -1;
         this.pendingDeadlineTick = Long.MIN_VALUE;
         if (playerFlag) {
            this.escalationLevel = Math.min(3, this.escalationLevel + 1);
            this.consecutiveStreak = 0;
         }
      }
   }

   public void onPendingConfirmed(int playerValue, boolean playerFlag, long playerValue2) {
      if (this.pendingEntityId >= 0 && playerValue == this.pendingEntityId && playerFlag) {
         this.pendingEntityId = -1;
         this.pendingDeadlineTick = Long.MIN_VALUE;
         this.consecutiveStreak++;
         if (this.consecutiveStreak >= 3) {
            this.escalationLevel = Math.max(0, this.escalationLevel - 1);
            this.consecutiveStreak = 0;
         }
      }
   }

   public static int getRemainingTicksCeilWithMargin(int playerValue, double playerBound, int playerValue2) {
      if (playerValue == Integer.MAX_VALUE) {
         return 0;
      } else {
         double playerValue3 = Double.isFinite(playerBound) ? Math.max(0.0, playerBound) : 0.0;
         return Math.max(0, (int)Math.ceil(10.0 - playerValue3 / 50.0) + playerValue2 - playerValue);
      }
   }

   public void reset() {
      this.escalationLevel = 1;
      this.consecutiveStreak = 0;
      this.pendingEntityId = -1;
      this.pendingDeadlineTick = Long.MIN_VALUE;
   }

   public PendingEntityHitTracker() {
      this.pendingDeadlineTick = Long.MIN_VALUE;
   }

   public int getEscalationLevel() {
      return this.escalationLevel;
   }

   public void trackPendingEntity(int value, long playerValue, int playerBound) {
      this.pendingEntityId = value;
      this.pendingDeadlineTick = playerValue + Math.max(1, playerBound);
   }
}
