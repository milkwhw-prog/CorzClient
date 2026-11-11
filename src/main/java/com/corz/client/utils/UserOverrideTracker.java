package com.corz.client.utils;

public final class UserOverrideTracker {
   private boolean hasInputTime;
   private boolean mouseThresholdExceeded;
   private long lastFlagTimeMs;
   private final AmountsScaffold mouseMovementWindow = new AmountsScaffold();

   public void recordUserInputTime(long time) {
      if (!this.hasInputTime || time > this.lastFlagTimeMs) {
         this.lastFlagTimeMs = time;
      }

      this.hasInputTime = true;
   }

   public void addMouseMovement(double playerValue, double groundY) {
      this.mouseMovementWindow.handleAmount(playerValue, groundY);
      if (this.mouseMovementWindow.isFlag()) {
         this.mouseThresholdExceeded = true;
      }
   }

   public void reset() {
      this.mouseMovementWindow.updateAmountsAmount();
      this.hasInputTime = false;
      this.lastFlagTimeMs = 0L;
      this.mouseThresholdExceeded = false;
   }

   public void registerUserInput(long playerValue) {
      this.recordUserInputTime(playerValue);
   }

   public boolean isUserOverriding(long playerValue) {
      return this.mouseThresholdExceeded ? true : this.hasInputTime && playerValue - this.lastFlagTimeMs < 1500L;
   }

   public void finishTick(long playerValue, boolean playerFlag) {
      if (this.mouseThresholdExceeded) {
         this.mouseThresholdExceeded = false;
         this.recordUserInputTime(playerValue);
      }

      if (playerFlag) {
         this.registerUserInput(playerValue);
      }

      this.mouseMovementWindow.updateAmountAmounts();
   }
}
