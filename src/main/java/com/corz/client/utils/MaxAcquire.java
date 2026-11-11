package com.corz.client.utils;

public final class MaxAcquire {
   private MaxAcquire.Phase acquireReleaseType = MaxAcquire.Phase.IDLE;
   private int lastAcquireTick;
   private final int minAcquireGapTicks;
   private int acquireReleaseValue = -1;
   private int acquireReleaseCount;
   private int acquireValue = -1;
   private final int maxAcquireBound;

   public void handleAcquireRelease(int value) {
      if (this.acquireReleaseType == MaxAcquire.Phase.IDLE) {
         this.acquireReleaseType = MaxAcquire.Phase.ACQUIRE;
         this.acquireReleaseValue = value;
         this.lastAcquireTick = value;
         this.acquireValue = -1;
         this.acquireReleaseCount = 0;
      }
   }

   public void handleAcquireRelease(int value, boolean acquireReleaseFlag) {
      if (this.acquireReleaseType != MaxAcquire.Phase.IDLE) {
         this.acquireReleaseCount++;
         switch (this.acquireReleaseType) {
            case ACQUIRE:
               this.acquireReleaseType = MaxAcquire.Phase.SETTLE;
               this.lastAcquireTick = value;
               break;
            case SETTLE:
               if (acquireReleaseFlag && value - this.lastAcquireTick >= this.minAcquireGapTicks) {
                  this.acquireReleaseType = MaxAcquire.Phase.CLICK_WINDOW;
                  this.lastAcquireTick = value;
               }
               break;
            case CLICK_WINDOW:
               if (value - this.lastAcquireTick >= 20) {
                  this.acquireReleaseType = MaxAcquire.Phase.IDLE;
                  this.acquireReleaseCount = 0;
               }
               break;
            case RELEASE:
               if (value - this.lastAcquireTick >= this.maxAcquireBound) {
                  this.acquireReleaseType = MaxAcquire.Phase.IDLE;
               }
         }
      }
   }

   public int getAcquireReleaseCount() {
      return this.acquireReleaseCount;
   }

   public void updateAcquireRelease() {
      this.acquireReleaseType = MaxAcquire.Phase.IDLE;
      this.lastAcquireTick = 0;
   }

   public boolean isAcquireClick() {
      return this.acquireReleaseType == MaxAcquire.Phase.CLICK_WINDOW;
   }

   public MaxAcquire(int acquireBound, int rocketsBuyBound) {
      this.minAcquireGapTicks = Math.max(0, acquireBound);
      this.maxAcquireBound = Math.max(0, rocketsBuyBound);
   }

   public boolean isAcquireRelease() {
      return this.acquireReleaseType != MaxAcquire.Phase.IDLE;
   }

   public int getAcquireValue() {
      return this.acquireValue;
   }

   public MaxAcquire.Phase getAcquireReleaseType() {
      return this.acquireReleaseType;
   }

   public void handleAcquireReleaseForInt(int value) {
      if (this.acquireReleaseType == MaxAcquire.Phase.CLICK_WINDOW) {
         this.acquireReleaseType = MaxAcquire.Phase.RELEASE;
         this.lastAcquireTick = value;
         this.acquireValue = value;
      }
   }

   public int getAcquireReleaseValue() {
      return this.acquireReleaseValue;
   }

   public static enum Phase {
      IDLE,
      ACQUIRE,
      SETTLE,
      CLICK_WINDOW,
      RELEASE;
   }
}
