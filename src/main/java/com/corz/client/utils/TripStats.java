package com.corz.client.utils;

import java.util.Locale;
import com.corz.client.utils.Destination;
import com.corz.client.utils.BuyerAppendUtils;

public record TripStats(
   boolean success,
   String headline,
   String reason,
   String destination,
   String dimension,
   int landX,
   int landY,
   int landZ,
   double flownBlocks,
   long seconds,
   int rocketsUsed,
   int xpUsed,
   int pitStops,
   int purchases,
   long spent,
   double topSpeedBps,
   double offBy
) {
   public static final String DESTINATION_REACHED = "Destination Reached";
   public static final String TRIP_STOPPED = "Trip Stopped";

   public String formatLand() {
      return this.landX + ", " + this.landY + ", " + this.landZ;
   }

   private static String getReplace(String s) {
      if (s == null) {
         return "";
      } else {
         String trim = s.replace('\n', ' ').replace('\r', ' ').trim();
         return trim.length() > 160 ? trim.substring(0, 160).stripTrailing() : trim;
      }
   }

   public String formatDestinationLand() {
      return this.destination.isEmpty() ? this.landX + ", " + this.landZ : this.destination;
   }

   public String formatFlownBlocks() {
      return Destination.getFormat(this.flownBlocks);
   }

   public boolean isFlag() {
      return this.offBy >= 2.0;
   }

   private static double getAmount(double v) {
      return Double.isFinite(v) && v > 0.0 ? v : 0.0;
   }

   public String formatSpent() {
      return BuyerAppendUtils.formatText(this.spent);
   }

   public String formatSecondsFlown() {
      return this.seconds <= 0L ? "-" : getFormat(this.flownBlocks / this.seconds);
   }

   public String formatTripDestination() {
      return this.headline.isEmpty() ? (this.success ? "Destination Reached" : "Trip Stopped") : this.headline;
   }

   public String formatText() {
      return this.isFlag() ? Destination.getFormat(this.offBy) + " from your coords" : "";
   }

   public String formatBpsSpeed() {
      return this.topSpeedBps <= 0.0 ? "-" : getFormat(this.topSpeedBps);
   }

   public TripStats(
      boolean success,
      String headline,
      String reason,
      String destination,
      String dimension,
      int landX,
      int landY,
      int landZ,
      double flownBlocks,
      long seconds,
      int rocketsUsed,
      int xpUsed,
      int pitStops,
      int purchases,
      long spent,
      double topSpeedBps,
      double offBy
   ) {
      headline = getReplace(headline);
      reason = getReplace(reason);
      destination = getReplace(destination);
      dimension = getReplace(dimension);
      flownBlocks = getAmount(flownBlocks);
      topSpeedBps = getAmount(topSpeedBps);
      offBy = getAmount(offBy);
      seconds = Math.max(0L, seconds);
      rocketsUsed = Math.max(0, rocketsUsed);
      xpUsed = Math.max(0, xpUsed);
      pitStops = Math.max(0, pitStops);
      purchases = Math.max(0, purchases);
      spent = Math.max(0L, spent);
      this.success = success;
      this.headline = headline;
      this.reason = reason;
      this.destination = destination;
      this.dimension = dimension;
      this.landX = landX;
      this.landY = landY;
      this.landZ = landZ;
      this.flownBlocks = flownBlocks;
      this.seconds = seconds;
      this.rocketsUsed = rocketsUsed;
      this.xpUsed = xpUsed;
      this.pitStops = pitStops;
      this.purchases = purchases;
      this.spent = spent;
      this.topSpeedBps = topSpeedBps;
      this.offBy = offBy;
   }

   public String formatSeconds() {
      return Destination.formatText(this.seconds);
   }

   public String formatTripDestinationSuccess() {
      StringBuilder stringBuilder = new StringBuilder(96);
      if (this.success) {
         stringBuilder.append("Destination reached at ").append(this.formatLand());
      } else {
         stringBuilder.append("Trip stopped");
         if (!this.reason.isEmpty()) {
            stringBuilder.append(": ").append(this.reason);
         }

         stringBuilder.append(" at ").append(this.formatLand());
      }

      stringBuilder.append(" - ").append(this.formatFlownBlocks()).append(" in ").append(this.formatSeconds());
      if (this.success && this.isFlag()) {
         stringBuilder.append(" (").append(this.formatText()).append(')');
      }

      return stringBuilder.toString();
   }

   public static String getFormat(double bps) {
      if (!Double.isFinite(bps) || bps <= 0.0) {
         return "-";
      } else {
         return bps < 9.95 ? String.format(Locale.ROOT, "%.1f b/s", bps) : Math.round(bps) + " b/s";
      }
   }
}
