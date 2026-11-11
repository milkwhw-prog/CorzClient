package com.corz.client.utils;

public final class TimeMaxAmount {
   private long lastSampleTick;
   private final long[] table;
   private double averageNanosPerTick;
   private long lastNanoTime = Long.MIN_VALUE;
   private double peakExcessNanos;
   private int tableWriteIndex;
   private int storedSampleCount;

   public synchronized boolean canAmountMax(long limit) {
      if (this.averageNanosPerTick <= 0.0) {
         return false;
      } else {
         long bound = limit > 0L ? limit : 50000000L;
         return this.averageNanosPerTick > bound * 1.3 || this.peakExcessNanos > Math.max(bound, 25000000L);
      }
   }

   public synchronized long getMin(long limit) {
      long bound = limit > 0L ? limit : 50000000L;
      double hypot = this.averageNanosPerTick > 0.0 ? Math.max(this.averageNanosPerTick, bound * 0.25) : Math.max(bound, 50000000L);
      return (long)Math.min(hypot, 1.0E9);
   }

   public synchronized long getMax() {
      return (long)this.peakExcessNanos;
   }

   public synchronized boolean isAmount() {
      return this.averageNanosPerTick > 0.0;
   }

   public synchronized void handleTimeAmount(long nanoTime, long time) {
      this.table[this.tableWriteIndex] = nanoTime;
      this.tableWriteIndex = (this.tableWriteIndex + 1) % this.table.length;
      if (this.storedSampleCount < this.table.length) {
         this.storedSampleCount++;
      }

      if (this.lastSampleTick != Long.MIN_VALUE) {
         if (time < this.lastSampleTick) {
            this.averageNanosPerTick = -1.0;
            this.peakExcessNanos = 0.0;
         } else if (time > this.lastSampleTick && nanoTime > this.lastNanoTime) {
            double bound = (double)(nanoTime - this.lastNanoTime) / (time - this.lastSampleTick);
            double d = Math.max(100000.0, Math.min(1.0E9, bound));
            double apiPutBound = this.averageNanosPerTick > 0.0 ? Math.min(1.0E9, nanoTime - this.lastNanoTime - this.averageNanosPerTick * (time - this.lastSampleTick)) : 0.0;
            this.peakExcessNanos = Math.max(Math.max(0.0, apiPutBound), this.peakExcessNanos * 0.7);
            this.averageNanosPerTick = this.averageNanosPerTick < 0.0 ? d : this.averageNanosPerTick * 0.6 + d * 0.4;
         }
      }

      this.lastNanoTime = nanoTime;
      this.lastSampleTick = time;
   }

   public TimeMaxAmount() {
      this.lastSampleTick = Long.MIN_VALUE;
      this.averageNanosPerTick = -1.0;
      this.table = new long[8];
   }

   public synchronized void updateTimeMax() {
      this.lastNanoTime = Long.MIN_VALUE;
      this.lastSampleTick = Long.MIN_VALUE;
      this.averageNanosPerTick = -1.0;
      this.peakExcessNanos = 0.0;
      this.tableWriteIndex = 0;
      this.storedSampleCount = 0;
   }

   public synchronized int getTimes(long longValue) {
      int times = 0;

      for (int i = 0; i < this.storedSampleCount; i++) {
         if (this.table[Math.floorMod(this.tableWriteIndex - 1 - i, this.table.length)] > longValue) {
            times++;
         }
      }

      return times;
   }
}
