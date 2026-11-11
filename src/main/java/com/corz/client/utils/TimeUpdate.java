package com.corz.client.utils;

public final class TimeUpdate {
   private long elapsedTicks;
   private long updateCount;
   private long primaryUpdateCount;
   private long secondaryUpdateCount;

   public long getTime() {
      return this.elapsedTicks;
   }

   public void handleTime(int updateLimit) {
      if (updateLimit > 0) {
         this.elapsedTicks++;
      }
   }

   public long getPrimaryUpdateCount() {
      return this.primaryUpdateCount;
   }

   public void updateTime() {
      this.primaryUpdateCount++;
   }

   public long getSecondaryUpdateCount() {
      return this.updateCount;
   }

   public long getTotalUpdateCount() {
      return this.secondaryUpdateCount;
   }

   public void resetPrimaryCount() {
      this.updateCount++;
   }

   public void resetSecondaryCount() {
      this.secondaryUpdateCount++;
   }
}
