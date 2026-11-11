package com.corz.client.utils;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class RowRun {
   private final Map<RowRun.Geometry, RowRun.Stat> ifAbsentStatMap = new EnumMap<>(RowRun.Geometry.class);

   private RowRun.Stat getIfAbsent(RowRun.Geometry geometry) {
      return this.ifAbsentStatMap.computeIfAbsent(geometry == null ? RowRun.Geometry.MIXED : geometry, geometry2 -> new RowRun.Stat());
   }

   public double getTime(RowRun.Geometry geometry) {
      RowRun.Stat ifAbsent = this.getIfAbsent(geometry);
      return ifAbsent.amountSampleCount <= 0L ? 0.0 : ifAbsent.amountSampleCount / (getMax(ifAbsent) / 60.0);
   }

   private static double getMax(RowRun.Stat stat) {
      int rowRunValue = stat.baselineValue < 0 ? 0 : stat.latestRowValue - stat.baselineValue;
      return Math.max(1.0, rowRunValue / 20.0);
   }

   public static RowRun.Geometry getRowRun(
      String rowRunText, boolean rowRunFlag, boolean rowRunFlag2, boolean rowRunFlag3, boolean rowRunFlag4, boolean rowRunFlag5, String rowRunText2
   ) {
      String rowRunText3 = rowRunText2 == null ? "" : rowRunText2;
      if (rowRunFlag3) {
         return RowRun.Geometry.MULTIFACE;
      } else if (rowRunFlag5) {
         return RowRun.Geometry.ACCESS_HEAVY;
      } else if (rowRunFlag2 || rowRunText3.equals("CONTAINER_ROW_RUN")) {
         return RowRun.Geometry.CONTAINER_ROW;
      } else if (rowRunFlag || rowRunText3.equals("DIRECTIONAL_ROW_RUN")) {
         return RowRun.Geometry.DIRECTIONAL_ROW;
      } else if (rowRunFlag4) {
         return RowRun.Geometry.DECK;
      } else {
         boolean equals = "PLAIN".equals(rowRunText);
         boolean rowRunFlag6 = rowRunText3.equals("SIDE_ROW_RUN") || rowRunText3.equals("REGION_DRAIN") || rowRunText3.equals("STATIC_STAND_BATCH");
         return equals && rowRunFlag6 ? RowRun.Geometry.PLAIN_ROW : RowRun.Geometry.MIXED;
      }
   }

   public void handleTime(RowRun.Geometry geometry) {
      this.getIfAbsent(geometry).handledTimeCount++;
   }

   public String formatTimeAmount(int rowRunValue) {
      StringBuilder stringBuilder = new StringBuilder();

      for (RowRun.Geometry geometry : RowRun.Geometry.values()) {
         RowRun.Stat stat = this.ifAbsentStatMap.get(geometry);
         if (stat != null && stat.amountSampleCount > 0L) {
            if (stringBuilder.length() > 0) {
               stringBuilder.append(' ');
            }

            double max2 = getMax(stat);
            double rowRunValue3 = stat.statCount <= 0L ? 0.0 : (double)stat.handledTimeTotal / stat.statCount;
            stringBuilder.append(
               String.format(
                  Locale.ROOT,
                  "%s{placements=%d perMin=%.1f walkPerPlacement=%.2f rotationPerPlacement=%.1f sneakDuty=%.2f rollbacks=%d flags=%d missionSwitches=%d avgRunLength=%.1f}",
                  geometry.name(),
                  stat.amountSampleCount,
                  stat.amountSampleCount / (max2 / 60.0),
                  stat.amountTotal / stat.amountSampleCount,
                  stat.amountSum / stat.amountSampleCount,
                  stat.statValue / (max2 * 20.0),
                  stat.amountEventCount,
                  stat.handledTimeCount,
                  stat.totalTimeTicks,
                  rowRunValue3
               )
            );
         }
      }

      return stringBuilder.toString();
   }

   public void handleTimeAmount(RowRun.Geometry geometry, int value, double rowRunValue, double rowRunValue2, int rowRunBound, boolean rowRunFlag, int rocketsBuyBound) {
      RowRun.Stat ifAbsent = this.getIfAbsent(geometry);
      ifAbsent.amountSampleCount++;
      if (ifAbsent.baselineValue < 0) {
         ifAbsent.baselineValue = value;
      }

      ifAbsent.latestRowValue = value;
      ifAbsent.amountTotal += rowRunValue;
      ifAbsent.amountSum += rowRunValue2;
      ifAbsent.statValue = ifAbsent.statValue + Math.max(0, rowRunBound);
      if (rowRunFlag) {
         ifAbsent.amountEventCount++;
      }

      ifAbsent.totalTimeTicks = ifAbsent.totalTimeTicks + Math.max(0, rocketsBuyBound);
   }

   public void handleTime(RowRun.Geometry geometry, int rowRunValue) {
      RowRun.Stat ifAbsent = this.getIfAbsent(geometry);
      ifAbsent.statCount++;
      ifAbsent.handledTimeTotal += rowRunValue;
   }

   public long getTimeForGeometry(RowRun.Geometry geometry) {
      return this.getIfAbsent(geometry).amountSampleCount;
   }

   public static enum Geometry {
      PLAIN_ROW,
      DIRECTIONAL_ROW,
      CONTAINER_ROW,
      DECK,
      MULTIFACE,
      ACCESS_HEAVY,
      MIXED;
   }

   private static final class Stat {
      int latestRowValue;
      long totalTimeTicks;
      double amountSum;
      long statCount;
      long statValue;
      long handledTimeCount;
      long handledTimeTotal;
      int baselineValue = -1;
      double amountTotal;
      long amountEventCount;
      long amountSampleCount;

      private Stat() {
         this.latestRowValue = -1;
      }
   }
}
