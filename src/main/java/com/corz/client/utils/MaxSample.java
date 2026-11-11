package com.corz.client.utils;

import java.util.Arrays;
import java.util.Locale;

public final class MaxSample {
   private long observationCount;
   private long lastSampleTimeMs;
   private long sampleCount;
   private long peakSample;
   private final long[] table = new long[2048];
   private int tableWriteIndex;
   private long gapCount;
   private long minGapSeconds;
   private long changeCount;
   private int lastObservedValue;
   private long repeatCount;
   private long skipCount;

   public MaxSample() {
      this.lastSampleTimeMs = Long.MIN_VALUE;
      this.lastObservedValue = Integer.MIN_VALUE;
      this.minGapSeconds = Long.MAX_VALUE;
   }

   public long getMax() {
      return this.minGapSeconds == Long.MAX_VALUE ? 0L : this.minGapSeconds;
   }

   public long getTime() {
      return this.observationCount;
   }

   public long getChangeCount() {
      return this.changeCount;
   }

   public void handleTimeMax(int value, long time, boolean flag, boolean flag2, boolean flag3) {
      this.changeCount++;
      if (value == this.lastObservedValue) {
         this.observationCount++;
      }

      this.lastObservedValue = value;
      if (flag) {
         this.skipCount++;
      }

      if (flag2) {
         this.repeatCount++;
      }

      if (flag3) {
         this.gapCount++;
      }

      if (this.lastSampleTimeMs != Long.MIN_VALUE) {
         long max = Math.max(0L, (time - this.lastSampleTimeMs) / 1000L);
         this.table[Math.floorMod(this.tableWriteIndex, 2048)] = max;
         this.tableWriteIndex++;
         if (max < this.minGapSeconds) {
            this.minGapSeconds = max;
         }

         if (max > this.peakSample) {
            this.peakSample = max;
         }

         if (max < 40000L) {
            this.sampleCount++;
         }
      }

      this.lastSampleTimeMs = time;
   }

   public void updateTimeMax() {
      Arrays.fill(this.table, 0L);
      this.tableWriteIndex = 0;
      this.lastSampleTimeMs = Long.MIN_VALUE;
      this.lastObservedValue = Integer.MIN_VALUE;
      this.changeCount = this.observationCount = this.skipCount = this.repeatCount = this.gapCount = 0L;
      this.minGapSeconds = Long.MAX_VALUE;
      this.peakSample = 0L;
      this.sampleCount = 0L;
   }

   public double getAmount() {
      long[] copyOf = this.getCopyOf();
      if (copyOf.length == 0) {
         return 0.0;
      } else {
         long total = 0L;

         for (long longValue : copyOf) {
            total += longValue;
         }

         return total == 0L ? 0.0 : copyOf.length / (total / 1000000.0);
      }
   }

   private long[] getCopyOf() {
      int n = Math.min(this.tableWriteIndex, 2048);
      long[] copyOf = Arrays.copyOf(this.table, n);
      Arrays.sort(copyOf);
      return copyOf;
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "movePackets=%d packetsPerRealSecond=%.2f minInterPacketUs=%d p1=%d p50=%d p99=%d maxUs=%d duplicateTickPackets=%d catchupBurstPackets=%d posChanges=%d rotChanges=%d groundChanges=%d",
         this.changeCount,
         this.getAmount(),
         this.getMax(),
         this.getTime(1.0),
         this.getTime(50.0),
         this.getTime(99.0),
         this.getPeakSample(),
         this.observationCount,
         this.sampleCount,
         this.skipCount,
         this.repeatCount,
         this.gapCount
      );
   }

   public long getTime(double d) {
      long[] copyOf = this.getCopyOf();
      if (copyOf.length == 0) {
         return 0L;
      } else {
         int bound = (int)Math.round(d / 100.0 * (copyOf.length - 1));
         return copyOf[Math.max(0, Math.min(copyOf.length - 1, bound))];
      }
   }

   public long getPeakSample() {
      return this.peakSample;
   }

   public long getSampleCount() {
      return this.sampleCount;
   }
}
