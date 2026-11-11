package com.corz.client.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;

public final class TimesFormat {
   private long totalFormattedTime;
   private static final TimesFormat.Bucket[] formatBuckets = TimesFormat.Bucket.values();
   private final long[] formatTable;
   private static final TimesFormat.Outcome[] formatOutcomes = TimesFormat.Outcome.values();
   private final long[] bucketCounts = new long[formatBuckets.length];
   private final long[] confirmedTable;
   private final LinkedHashMap<Integer, Long> formatPositionMap;

   public void handleTime(int formatValue) {
      this.formatPositionMap.merge(formatValue, 1L, Long::sum);

      while (this.formatPositionMap.size() > 512) {
         Iterator iterator = this.formatPositionMap.entrySet().iterator();
         this.totalFormattedTime = this.totalFormattedTime + (Long)((Entry)iterator.next()).getValue();
         iterator.remove();
      }
   }

   public long getTimes(TimesFormat.Outcome outcome) {
      return outcome == null ? 0L : this.confirmedTable[outcome.ordinal()];
   }

   public void handleTimes(TimesFormat.Bucket bucket) {
      if (bucket == null) {
         bucket = TimesFormat.Bucket.OTHER;
      }

      this.bucketCounts[bucket.ordinal()]++;
      this.formatTable[bucket.ordinal()]++;
   }

   public String formatTimes() {
      return getFormat(this.formatTable);
   }

   public long getTimes(TimesFormat.Bucket bucket) {
      return bucket == null ? 0L : this.bucketCounts[bucket.ordinal()];
   }

   private static long getTime(long[] formatValues) {
      long formatTotal = 0L;

      for (long formatValue : formatValues) {
         formatTotal += formatValue;
      }

      return formatTotal;
   }

   public double getTimesConfirmed() {
      long confirmedTotal = this.getTime();

      for (long confirmedValue : this.confirmedTable) {
         confirmedTotal += confirmedValue;
      }

      return confirmedTotal <= 0L ? 0.0 : (double)this.confirmedTable[TimesFormat.Outcome.CONFIRMED.ordinal()] / confirmedTotal;
   }

   public long getTimes() {
      return sumConfirmedValues(this.bucketCounts);
   }

   public void handleTimes(int formatValue, TimesFormat.Outcome outcome) {
      if (outcome != null) {
         Long long2 = this.formatPositionMap.remove(formatValue);
         if (long2 != null) {
            this.confirmedTable[outcome.ordinal()] += long2;
         }
      }
   }

   public TimesFormat() {
      this.formatTable = new long[formatBuckets.length];
      this.confirmedTable = new long[formatOutcomes.length];
      this.formatPositionMap = new LinkedHashMap<>();
   }

   public String formatConfirmedTimes() {
      return getFormat(this.bucketCounts);
   }

   public double getAmount() {
      long times = this.getConfirmedTotal();
      return times <= 0L ? 0.0 : (double)this.getTimes() / times;
   }

   public long getConfirmedTotal() {
      return getTime(this.bucketCounts);
   }

   public void updateTimes() {
      Arrays.fill(this.formatTable, 0L);
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "usefulProductiveDuty=%.3f confirmedMissionMovementTicks=%d failedMissionMovementTicks=%d accessProbeMovementTicks=%d replanMovementTicks=%d recoveryMovementTicks=%d unsettledMovementTicks=%d",
         this.getTimesConfirmed(),
         this.confirmedTable[TimesFormat.Outcome.CONFIRMED.ordinal()],
         this.confirmedTable[TimesFormat.Outcome.FAILED.ordinal()],
         this.confirmedTable[TimesFormat.Outcome.ACCESS_PROBE.ordinal()],
         this.confirmedTable[TimesFormat.Outcome.REPLAN.ordinal()],
         this.confirmedTable[TimesFormat.Outcome.RECOVERY.ordinal()],
         this.getTime()
      );
   }

   public long getTime() {
      long formatTotal = this.totalFormattedTime;

      for (long formatValue : this.formatPositionMap.values()) {
         formatTotal += formatValue;
      }

      return formatTotal;
   }

   private static String getFormat(long[] formatValues) {
      long formatDivisor = getTime(formatValues);
      double formatValue = formatDivisor <= 0L ? 0.0 : (double)sumConfirmedValues(formatValues) / formatDivisor;
      return String.format(
         Locale.ROOT,
         "productive=%.3f movement=%d aim=%d placement=%d confirmation=%d inventory=%d deadScheduler=%d deadMovement=%d deadAim=%d other=%d total=%d",
         formatValue,
         formatValues[TimesFormat.Bucket.PRODUCTIVE_MOVEMENT.ordinal()],
         formatValues[TimesFormat.Bucket.CAMERA_PREP.ordinal()],
         formatValues[TimesFormat.Bucket.PLACEMENT.ordinal()],
         formatValues[TimesFormat.Bucket.CONFIRMATION.ordinal()],
         formatValues[TimesFormat.Bucket.INVENTORY.ordinal()],
         formatValues[TimesFormat.Bucket.DEAD_SCHEDULER.ordinal()],
         formatValues[TimesFormat.Bucket.DEAD_MOVEMENT.ordinal()],
         formatValues[TimesFormat.Bucket.DEAD_AIM.ordinal()],
         formatValues[TimesFormat.Bucket.OTHER.ordinal()],
         formatDivisor
      );
   }

   public long getConfirmedBucketCount(TimesFormat.Bucket bucket) {
      return bucket == null ? 0L : this.formatTable[bucket.ordinal()];
   }

   public void updateTimesTime() {
      this.formatPositionMap.clear();
      Arrays.fill(this.confirmedTable, 0L);
      this.totalFormattedTime = 0L;
   }

   private static long sumConfirmedValues(long[] formatValues) {
      long formatTotal = 0L;

      for (int i = 0; i < 5; i++) {
         formatTotal += formatValues[i];
      }

      return formatTotal;
   }

   public static enum Bucket {
      PRODUCTIVE_MOVEMENT,
      CAMERA_PREP,
      PLACEMENT,
      CONFIRMATION,
      INVENTORY,
      DEAD_SCHEDULER,
      DEAD_MOVEMENT,
      DEAD_AIM,
      OTHER;
   }

   public static enum Outcome {
      CONFIRMED,
      FAILED,
      ACCESS_PROBE,
      REPLAN,
      RECOVERY;
   }
}
