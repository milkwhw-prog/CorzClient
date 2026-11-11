package com.corz.client.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public final class WakeRev {
   private final LinkedHashMap<String, Integer> suppressedForceIntegerMap;
   private static final WakeRev.Reason[] appendReasons = WakeRev.Reason.values();
   private final int[] forcePendingValues = new int[appendReasons.length];
   private boolean forcePendingFlag;
   private String pendingForceText;
   private int tickRunValue;
   private WakeRev.Revisions revisionChangedRevisions;
   private int revSuppressedCount;
   private int suppressedWakeCount;
   private WakeRev.Reason changedHeartbeatType;
   private long revSinceTime;

   public int getSuppressedTotal() {
      return this.suppressedForceIntegerMap.size();
   }

   public WakeRev() {
      this.suppressedForceIntegerMap = new LinkedHashMap<String, Integer>(16, 0.75F, false) {
         @Override
         protected boolean removeEldestEntry(Entry<String, Integer> entry) {
            return this.size() > 16;
         }
      };
      this.pendingForceText = "";
      this.changedHeartbeatType = WakeRev.Reason.SUPPRESSED;
   }

   public void updateSuppressedForce() {
      Arrays.fill(this.forcePendingValues, 0);
      this.suppressedForceIntegerMap.clear();
      this.revisionChangedRevisions = null;
      this.tickRunValue = 0;
      this.suppressedWakeCount = 0;
      this.revSuppressedCount = 0;
      this.revSinceTime = 0L;
      this.forcePendingFlag = false;
      this.pendingForceText = "";
      this.changedHeartbeatType = WakeRev.Reason.SUPPRESSED;
   }

   public int getSuppressedForce(WakeRev.Reason reason) {
      return reason == WakeRev.Reason.SUPPRESSED ? 0 : this.forcePendingValues[reason.ordinal()];
   }

   public WakeRev.Reason evaluateRevisionWake(WakeRev.Revisions data, int tickRunValue, boolean revSuppressedFlag) {
      WakeRev.Reason changedHeartbeatType = this.getRevisionChanged(data, tickRunValue, revSuppressedFlag);
      this.forcePendingValues[changedHeartbeatType.ordinal()]++;
      this.changedHeartbeatType = changedHeartbeatType;
      if (changedHeartbeatType == WakeRev.Reason.SUPPRESSED) {
         this.revSuppressedCount++;
         return changedHeartbeatType;
      } else {
         this.suppressedWakeCount++;
         this.revisionChangedRevisions = data;
         this.tickRunValue = tickRunValue;
         this.forcePendingFlag = false;
         if (changedHeartbeatType != WakeRev.Reason.FORCED && changedHeartbeatType != WakeRev.Reason.HEARTBEAT) {
            this.revSinceTime = 0L;
         } else {
            this.revSinceTime++;
         }

         return changedHeartbeatType;
      }
   }

   public int getTickRunValue() {
      return this.tickRunValue;
   }

   public WakeRev.Reason getChangedHeartbeatType() {
      return this.changedHeartbeatType;
   }

   public String getAppend() {
      StringBuilder append = new StringBuilder(128)
         .append("wake runs=")
         .append(this.suppressedWakeCount)
         .append(" suppressed=")
         .append(this.revSuppressedCount)
         .append(" sinceRev=")
         .append(this.revSinceTime)
         .append(" last=")
         .append(this.changedHeartbeatType)
         .append(" runTick=")
         .append(this.tickRunValue);

      for (WakeRev.Reason reason : appendReasons) {
         if (reason != WakeRev.Reason.SUPPRESSED && this.forcePendingValues[reason.ordinal()] > 0) {
            append.append(' ').append(reason).append('=').append(this.forcePendingValues[reason.ordinal()]);
         }
      }

      append.append(" force=").append(this.forcePendingFlag ? (this.pendingForceText.isEmpty() ? "pending" : this.pendingForceText) : "-");
      return append.toString();
   }

   public String getPendingForceText() {
      return this.pendingForceText;
   }

   public boolean isHeartbeatSuppressed() {
      return this.changedHeartbeatType != WakeRev.Reason.SUPPRESSED;
   }

   public int getSuppressedWakeCount() {
      return this.suppressedWakeCount;
   }

   public long getRevSinceTime() {
      return this.revSinceTime;
   }

   public boolean isForcePendingFlag() {
      return this.forcePendingFlag;
   }

   public int getRevSuppressedCount() {
      return this.revSuppressedCount;
   }

   private WakeRev.Reason getRevisionChanged(WakeRev.Revisions revisions, int revisionChangedValue, boolean revisionChangedFlag) {
      if (this.revisionChangedRevisions == null) {
         return WakeRev.Reason.FIRST;
      } else if (revisions.worldRevision() != this.revisionChangedRevisions.worldRevision()) {
         return WakeRev.Reason.WORLD_CHANGED;
      } else if (revisions.feetCell() != this.revisionChangedRevisions.feetCell()) {
         return WakeRev.Reason.MOVED;
      } else if (revisions.confirmCount() != this.revisionChangedRevisions.confirmCount()) {
         return WakeRev.Reason.CONFIRMED;
      } else if (revisions.routeRevision() != this.revisionChangedRevisions.routeRevision()) {
         return WakeRev.Reason.ROUTE_CHANGED;
      } else if (revisions.componentKey() != this.revisionChangedRevisions.componentKey()) {
         return WakeRev.Reason.COMPONENT_CHANGED;
      } else if (revisions.missionRevision() != this.revisionChangedRevisions.missionRevision()) {
         return WakeRev.Reason.MISSION_CHANGED;
      } else if (revisions.inventoryRevision() != this.revisionChangedRevisions.inventoryRevision()) {
         return WakeRev.Reason.INVENTORY_CHANGED;
      } else if (revisions.ackCount() != this.revisionChangedRevisions.ackCount()) {
         return WakeRev.Reason.ACK_ARRIVED;
      } else if (this.forcePendingFlag) {
         return WakeRev.Reason.FORCED;
      } else {
         return (long)revisionChangedValue - this.tickRunValue >= (revisionChangedFlag ? 160L : 40L) ? WakeRev.Reason.HEARTBEAT : WakeRev.Reason.SUPPRESSED;
      }
   }

   public void handleForcePending(String forcePendingText) {
      this.forcePendingFlag = true;
      this.pendingForceText = forcePendingText == null ? "" : forcePendingText;
      this.suppressedForceIntegerMap.merge(this.pendingForceText, 1, Integer::sum);
   }

   public int getOrDefault(String orDefaultText) {
      return this.suppressedForceIntegerMap.getOrDefault(orDefaultText == null ? "" : orDefaultText, 0);
   }

   public static enum Reason {
      FIRST,
      WORLD_CHANGED,
      MOVED,
      CONFIRMED,
      ROUTE_CHANGED,
      COMPONENT_CHANGED,
      MISSION_CHANGED,
      INVENTORY_CHANGED,
      ACK_ARRIVED,
      FORCED,
      HEARTBEAT,
      SUPPRESSED;
   }

   public record Revisions(
      long worldRevision, long feetCell, long confirmCount, long routeRevision, long componentKey, long missionRevision, long inventoryRevision, long ackCount
   ) {
   }
}
