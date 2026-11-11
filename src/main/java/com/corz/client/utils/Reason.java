package com.corz.client.utils;

import java.util.Locale;
import com.corz.client.utils.ValidatedRefusalUtils;

public final class Reason {
   private final long eventTick;
   private final long reasonValue;
   private final int reasonCode;
   private final int reasonStartTick;
   private int pinnedDispatchedValue;
   private int lastPinnedDispatchedTick;
   private final ValidatedRefusalUtils.AlternativeTicket unassignedMembersData;
   private Reason.State pinnedDispatchedType = Reason.State.PINNED;
   private String reason = "";

   public long getUnassignedMembers() {
      return this.unassignedMembersData.stand();
   }

   public long getUnassignedMembersUnassignedMembersData() {
      return this.unassignedMembersData.target();
   }

   public Reason.State getPinnedDispatchedType() {
      return this.pinnedDispatchedType;
   }

   public void handlePinnedDispatched(int value) {
      if (this.pinnedDispatchedType == Reason.State.PINNED) {
         this.pinnedDispatchedType = Reason.State.DISPATCHED;
         this.lastPinnedDispatchedTick = value;
      }
   }

   public long getReasonValue() {
      return this.reasonValue;
   }

   public String getReason() {
      return this.reason;
   }

   public boolean isPinnedDispatched() {
      return this.pinnedDispatchedType == Reason.State.PINNED || this.pinnedDispatchedType == Reason.State.DISPATCHED;
   }

   public ValidatedRefusalUtils.AlternativeTicket getUnassignedMembersData() {
      return this.unassignedMembersData;
   }

   public void handlePinnedDispatched(String pinnedDispatchedText, int value) {
      if (this.isPinnedDispatched()) {
         this.pinnedDispatchedType = Reason.State.INVALIDATED;
         this.reason = pinnedDispatchedText == null ? "" : pinnedDispatchedText;
         this.pinnedDispatchedValue = value;
      }
   }

   public int getReasonStartTick() {
      return this.reasonStartTick;
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "redirectId=%d redirectState=%s alternativeType=%s alternativeTarget=%d alternativeStand=%d alternativeMission=%d alternativeExpected=%d alternativeCost=%.0f refused=%d refusedRouteLen=%d age=%d%s",
         this.eventTick,
         this.pinnedDispatchedType,
         this.unassignedMembersData.kind(),
         this.unassignedMembersData.target(),
         this.unassignedMembersData.stand(),
         this.unassignedMembersData.missionId(),
         this.unassignedMembersData.expected(),
         this.unassignedMembersData.cost(),
         this.reasonValue,
         this.reasonCode,
         (this.pinnedDispatchedValue >= 0 ? this.pinnedDispatchedValue : Math.max(this.reasonStartTick, this.lastPinnedDispatchedTick)) - this.reasonStartTick,
         this.reason.isEmpty() ? "" : " reason=" + this.reason
      );
   }

   public long getTime() {
      return this.eventTick;
   }

   public Reason(long time, ValidatedRefusalUtils.AlternativeTicket unassignedMembersData, long time2, int value, int value2) {
      this.lastPinnedDispatchedTick = -1;
      this.pinnedDispatchedValue = -1;
      this.eventTick = time;
      this.unassignedMembersData = unassignedMembersData;
      this.reasonValue = time2;
      this.reasonCode = value;
      this.reasonStartTick = value2;
   }

   public boolean canPinnedUnassigned(long pinnedUnassignedValue, int value) {
      if (this.isPinnedDispatched() && pinnedUnassignedValue == this.unassignedMembersData.target()) {
         this.pinnedDispatchedType = Reason.State.COMPLETED;
         this.pinnedDispatchedValue = value;
         return true;
      } else {
         return false;
      }
   }

   public static enum State {
      PINNED,
      DISPATCHED,
      COMPLETED,
      INVALIDATED;
   }

   public static final class Stats {
      public int redirectFallbackOriginal;
      public int redirects;
      public int redirectCompleted;
      public int acceptCurrent;
      public int phantomAlternatives;
      public int redirectInvalidated;
      public int arbitrations;
      public int roiDeadlocksPrevented;

      public void updateRedirectRedirects() {
         this.arbitrations = this.acceptCurrent = this.redirects = this.redirectCompleted = this.redirectInvalidated = this.redirectFallbackOriginal = this.phantomAlternatives = this.roiDeadlocksPrevented = 0;
      }

      public String formatRedirectRedirects() {
         return "arbitrations="
            + this.arbitrations
            + " acceptCurrent="
            + this.acceptCurrent
            + " redirects="
            + this.redirects
            + " redirectCompleted="
            + this.redirectCompleted
            + " redirectInvalidated="
            + this.redirectInvalidated
            + " redirectFallbackOriginal="
            + this.redirectFallbackOriginal
            + " phantomAlternatives="
            + this.phantomAlternatives
            + " roiDeadlocksPrevented="
            + this.roiDeadlocksPrevented;
      }

      public void handleRedirectsArbitrations(ValidatedRefusalUtils.Result result) {
         this.arbitrations++;
         this.phantomAlternatives = this.phantomAlternatives + result.getPhantoms();
         if (result.decision() == ValidatedRefusalUtils.Decision.REDIRECT_TO_ALTERNATIVE) {
            this.redirects++;
         } else {
            this.acceptCurrent++;
            if (result.getPhantoms() > 0) {
               this.roiDeadlocksPrevented++;
            }
         }
      }
   }
}
