package com.corz.client.utils;

public final class DedupLastTime {
   private long lastBuilderTime = Long.MIN_VALUE;

   public boolean canTime(long time) {
      if (this.lastBuilderTime == time) {
         return false;
      } else {
         this.lastBuilderTime = time;
         return true;
      }
   }

   public int getTime(long builderValue) {
      return this.lastBuilderTime == builderValue ? 1 : 0;
   }

   public void updateTime() {
      this.lastBuilderTime = Long.MIN_VALUE;
   }
}
