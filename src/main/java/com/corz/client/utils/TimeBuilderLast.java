package com.corz.client.utils;

public final class TimeBuilderLast {
   private int lastAcceptedTick;
   private long lastAcceptedTime = Long.MIN_VALUE;

   public void updateTime() {
      this.lastAcceptedTime = Long.MIN_VALUE;
   }

   public TimeBuilderLast() {
      this.lastAcceptedTick = Integer.MIN_VALUE;
   }

   public boolean canTime(long time, int value, int builderLimit) {
      if (time == this.lastAcceptedTime && value - this.lastAcceptedTick < builderLimit) {
         return false;
      } else {
         this.lastAcceptedTime = time;
         this.lastAcceptedTick = value;
         return true;
      }
   }
}
