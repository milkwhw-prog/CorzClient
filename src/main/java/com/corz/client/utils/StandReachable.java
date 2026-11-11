package com.corz.client.utils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;

public final class StandReachable {
   private final LinkedHashMap<Long, StandReachable.Stamp> readmittedStampsMap = new LinkedHashMap<Long, StandReachable.Stamp>(64, 0.75F, false) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, StandReachable.Stamp> entry) {
         return this.size() > 8192;
      }
   };
   private static final String[] standingVisibleTexts = new String[]{
      "no path",
      "no access",
      "no stand",
      "no reachable stand",
      "unreachable",
      "no proven stand",
      "not a reachable stand",
      "disconnected stand",
      "known negative route",
      "no visible standing position"
   };
   private StandReachable.Verdict readmitTopologyType;
   private int readmittedStampsCount;
   private static final String[] correctionRecentreTexts = new String[]{"unstable", "settle", "recentre", "server correction"};
   private static final String[] deferringLowTexts = new String[]{"roi", "moratorium", "cooldown", "low value", "deferring"};
   private int standReachableCount;

   public int getReadmittedStamps() {
      return this.readmittedStampsMap.size();
   }

   public boolean isContainsKey(long containsValue) {
      return this.readmittedStampsMap.containsKey(containsValue);
   }

   public int getReadmittedStampsCount() {
      return this.readmittedStampsCount;
   }

   public StandReachable.Verdict getReadmitTopologyType() {
      return this.readmitTopologyType;
   }

   public static StandReachable.Verdict getReadmitFloor(StandReachable.Stamp stamp, int readmitFloorValue, int readmitFloorLimit, long activeComponentKey, long readmitFloorValue3) {
      if (stamp == null) {
         return StandReachable.Verdict.READMIT_FLOOR_ELAPSED;
      } else if (activeComponentKey != stamp.componentKey()) {
         return StandReachable.Verdict.READMIT_TOPOLOGY_CHANGED;
      } else if (readmitFloorValue3 != stamp.localGeometry()) {
         return StandReachable.Verdict.READMIT_GEOMETRY_CHANGED;
      } else if (stamp.kind() == StandReachable.FailureKind.ACCESS) {
         return StandReachable.Verdict.HOLD_UNCHANGED_TOPOLOGY;
      } else {
         return readmitFloorValue - stamp.tick() < readmitFloorLimit ? StandReachable.Verdict.HOLD_FLOOR : StandReachable.Verdict.READMIT_FLOOR_ELAPSED;
      }
   }

   public void updateReadmittedStamps() {
      this.readmittedStampsMap.clear();
      this.standReachableCount = 0;
      this.readmittedStampsCount = 0;
      this.readmitTopologyType = null;
   }

   public static boolean canReadmitChanged(StandReachable.Verdict verdict) {
      return verdict == StandReachable.Verdict.READMIT_TOPOLOGY_CHANGED
         || verdict == StandReachable.Verdict.READMIT_GEOMETRY_CHANGED
         || verdict == StandReachable.Verdict.READMIT_FLOOR_ELAPSED;
   }

   public StandReachable.Verdict getReadmittedStamps(long readmittedStampsValue, int readmitFloorValue, int readmitFloorLimit, long activeComponentKey, long readmittedStampsValue5) {
      StandReachable.Verdict readmitTopologyType = getReadmitFloor(this.readmittedStampsMap.get(readmittedStampsValue), readmitFloorValue, readmitFloorLimit, activeComponentKey, readmittedStampsValue5);
      if (canReadmitChanged(readmitTopologyType)) {
         this.readmittedStampsCount++;
      } else {
         this.standReachableCount++;
      }

      this.readmitTopologyType = readmitTopologyType;
      return readmitTopologyType;
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "stamps=%d held=%d readmitted=%d last=%s",
         this.readmittedStampsMap.size(),
         this.standReachableCount,
         this.readmittedStampsCount,
         this.readmitTopologyType == null ? "none" : this.readmitTopologyType
      );
   }

   public static StandReachable.FailureKind getTextsExecutor(String executorText) {
      if (executorText == null) {
         return StandReachable.FailureKind.EXECUTOR;
      } else {
         String lowerCase = executorText.toLowerCase(Locale.ROOT);
         if (canFlag(lowerCase, standingVisibleTexts)) {
            return StandReachable.FailureKind.ACCESS;
         } else if (canFlag(lowerCase, correctionRecentreTexts)) {
            return StandReachable.FailureKind.TRANSIENT;
         } else {
            return canFlag(lowerCase, deferringLowTexts) ? StandReachable.FailureKind.POLICY : StandReachable.FailureKind.EXECUTOR;
         }
      }
   }

   public int getStandReachableCount() {
      return this.standReachableCount;
   }

   private static boolean canFlag(String standReachableText, String[] texts) {
      for (String textsItem : texts) {
         if (standReachableText.contains(textsItem)) {
            return true;
         }
      }

      return false;
   }

   public StandReachable.Stamp getReadmittedStamps(long readmittedStampsValue) {
      return this.readmittedStampsMap.get(readmittedStampsValue);
   }

   public void handleReadmittedStamps(long readmittedStampsValue, StandReachable.Stamp stamp) {
      if (stamp == null) {
         this.readmittedStampsMap.remove(readmittedStampsValue);
      } else {
         this.readmittedStampsMap.remove(readmittedStampsValue);
         this.readmittedStampsMap.put(readmittedStampsValue, stamp);
      }
   }

   public void handleReadmittedStamps(long readmittedStampsValue) {
      this.readmittedStampsMap.remove(readmittedStampsValue);
   }

   public static enum FailureKind {
      ACCESS,
      TRANSIENT,
      EXECUTOR,
      POLICY;
   }

   public record Stamp(int tick, long componentKey, long localGeometry, StandReachable.FailureKind kind) {
   }

   public static enum Verdict {
      READMIT_TOPOLOGY_CHANGED,
      READMIT_GEOMETRY_CHANGED,
      READMIT_FLOOR_ELAPSED,
      HOLD_UNCHANGED_TOPOLOGY,
      HOLD_FLOOR;
   }
}
