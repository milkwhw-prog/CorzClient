package com.corz.client.utils;

public final class ElapsedTimeStats {
   private long elapsedTicks;
   private long timeSampleCount;
   private long lastRecordedTime;
   private int currentProgress = -1;
   private int progressLimit;

   public void handleTime(long time) {
      if (this.isProgressLimitReached()) {
         this.currentProgress++;
         this.lastRecordedTime = time;
         this.timeSampleCount++;
      }
   }

   public long getTimeSampleCount() {
      return this.lastRecordedTime;
   }

   public int getValue() {
      return this.currentProgress;
   }

   public boolean isMax() {
      return this.progressLimit > 0 && this.currentProgress >= this.progressLimit;
   }

   public long getTime() {
      return this.elapsedTicks;
   }

   public void handleMaxTime(int bound, int limit, long time) {
      this.progressLimit = Math.max(0, bound);
      this.currentProgress = this.progressLimit > 0 && limit >= 0 ? Math.min(limit, this.progressLimit) : -1;
      this.lastRecordedTime = time;
   }

   public long getLastRecordedTime() {
      return this.timeSampleCount;
   }

   public boolean canTime(long longValue) {
      return this.isProgressLimitReached() && longValue != this.lastRecordedTime;
   }

   public void setMaxProgress(int max, long time) {
      this.currentProgress = max > 0 ? 0 : -1;
      this.progressLimit = max;
      this.lastRecordedTime = time;
   }

   public void updateTimeMax() {
      this.currentProgress = -1;
      this.progressLimit = 0;
      this.elapsedTicks++;
   }

   public boolean isProgressLimitReached() {
      return this.currentProgress >= 0 && this.currentProgress < this.progressLimit;
   }
}
