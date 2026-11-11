package com.corz.client.utils;

public final class ServerTpsEstimator {
   private long lastSampleTimeMs;
   private double smoothedIntervalMs;
   private long lastUpdateTimeMs = -1L;

   public double getEstimatedTps() {
      if (!Double.isNaN(this.smoothedIntervalMs) && !(this.smoothedIntervalMs <= 0.0)) {
         double playerBound = 20000.0 / this.smoothedIntervalMs;
         return Math.max(0.0, Math.min(20.0, playerBound));
      } else {
         return 20.0;
      }
   }

   public void reset() {
      this.lastUpdateTimeMs = -1L;
      this.lastSampleTimeMs = -1L;
      this.smoothedIntervalMs = Double.NaN;
   }

   public ServerTpsEstimator() {
      this.lastSampleTimeMs = -1L;
      this.smoothedIntervalMs = Double.NaN;
   }

   public void onTimeUpdate(long time) {
      if (this.lastUpdateTimeMs >= 0L) {
         long playerLimit = time - this.lastUpdateTimeMs;
         if (playerLimit > 0L) {
            this.smoothedIntervalMs = Double.isNaN(this.smoothedIntervalMs) ? playerLimit : this.smoothedIntervalMs + 0.3 * (playerLimit - this.smoothedIntervalMs);
         }
      }

      if (this.lastUpdateTimeMs < 0L || time >= this.lastUpdateTimeMs) {
         this.lastUpdateTimeMs = time;
      }
   }

   public boolean isTimeStale(long time) {
      if (this.lastUpdateTimeMs < 0L) {
         if (this.lastSampleTimeMs < 0L) {
            this.lastSampleTimeMs = time;
         }

         return time - this.lastSampleTimeMs > 1500L;
      } else {
         return time - this.lastUpdateTimeMs > 1500L;
      }
   }
}
