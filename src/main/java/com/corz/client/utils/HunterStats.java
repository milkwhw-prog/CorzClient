package com.corz.client.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public final class HunterStats {
   public long totalHuntTimeMs;
   public int rtpCount;
   public int lostFleeCount;
   public int anchorsEmptyCount;
   public int spotCount;
   public int logoutCount;
   public int anchorsDugCount;
   public int playersMetCount;
   public int fleeCount;
   public int ghostAnchorCount;
   public int anchorsMinedCount;
   public int chargedAnchorCount;
   public long hunterCount;
   public long anchorsErrsCount;
   private final Map<String, Integer> other = new TreeMap<>();
   public int errorCount;
   public int emptyLandCount;

   public void recordSkipReason(String hunterStatsText) {
      if (hunterStatsText == null || hunterStatsText.isBlank()) {
         hunterStatsText = "other";
      }

      this.other.merge(hunterStatsText, 1, Integer::sum);
   }

   public static int computePerHour(int hunterStatsLimit, long hunterStatsLimit2) {
      return hunterStatsLimit > 0 && hunterStatsLimit2 >= 60000L ? (int)Math.round(hunterStatsLimit * 3600000.0 / hunterStatsLimit2) : 0;
   }

   public String getSummaryText() {
      return this.anchorsMinedCount
         + (this.anchorsMinedCount == 1 ? " anchor, " : " anchors, ")
         + this.rtpCount
         + (this.rtpCount == 1 ? " RTP, " : " RTPs, ")
         + this.getAnchorsPerHour()
         + "/h";
   }

   public int getTotalSkipCount() {
      int hunterStatsTotal = 0;

      for (int hunterStatsValue : this.other.values()) {
         hunterStatsTotal += hunterStatsValue;
      }

      return hunterStatsTotal;
   }

   public int getAnchorsPerHour() {
      return computePerHour(this.anchorsMinedCount, this.totalHuntTimeMs);
   }

   public int getSkipCount(String orDefaultText) {
      return this.other.getOrDefault(orDefaultText, 0);
   }

   public void recordAnchorsMined(int rtpsChargedLimit, boolean rtpsChargedFlag) {
      if (rtpsChargedLimit > 0) {
         this.anchorsMinedCount += rtpsChargedLimit;
         if (rtpsChargedFlag) {
            this.chargedAnchorCount += rtpsChargedLimit;
         }
      }
   }

   public String formatSkipReasons() {
      if (this.other.isEmpty()) {
         return "none";
      } else {
         StringBuilder stringBuilder = new StringBuilder();

         for (Entry entry : this.other.entrySet()) {
            if (stringBuilder.length() > 0) {
               stringBuilder.append(',');
            }

            stringBuilder.append((String)entry.getKey()).append(':').append(entry.getValue());
         }

         return stringBuilder.toString();
      }
   }

   public void resetAll() {
      this.anchorsMinedCount = this.chargedAnchorCount = this.rtpCount = this.emptyLandCount = this.anchorsEmptyCount = this.playersMetCount = this.fleeCount = this.logoutCount = this.anchorsDugCount = this.lostFleeCount = this.ghostAnchorCount = this.errorCount = this.spotCount = 0;
      this.totalHuntTimeMs = this.anchorsErrsCount = this.hunterCount = 0L;
      this.other.clear();
   }

   public String formatDebugLine(String anchorsErrsText, long anchorsErrsBound, String anchorsErrsText2, String anchorsErrsText3) {
      return "state="
         + anchorsErrsText
         + " spot="
         + this.spotCount
         + " anchors="
         + this.anchorsMinedCount
         + " charged="
         + this.chargedAnchorCount
         + " rtps="
         + this.rtpCount
         + " land="
         + this.emptyLandCount
         + " empty="
         + this.anchorsEmptyCount
         + " cd="
         + Math.max(0L, anchorsErrsBound)
         + " threat="
         + (anchorsErrsText2 != null && !anchorsErrsText2.isBlank() ? anchorsErrsText2 : "none")
         + " skips="
         + this.formatSkipReasons()
         + " dug="
         + this.anchorsDugCount
         + " ghosts="
         + this.ghostAnchorCount
         + " lost="
         + this.lostFleeCount
         + " flee="
         + this.fleeCount
         + " met="
         + this.playersMetCount
         + " logouts="
         + this.logoutCount
         + " errs="
         + this.errorCount
         + " perh="
         + this.getAnchorsPerHour()
         + " why="
         + (anchorsErrsText3 != null && !anchorsErrsText3.isBlank() ? anchorsErrsText3.replace(' ', '_') : "-");
   }
}
