package com.corz.client.utils;

import java.util.Objects;

public final class EnteredSuppressed {
   private long suppressedTime;
   private boolean enteredSuppressedActive;

   public boolean isSuppressed() {
      return this.enteredSuppressedActive;
   }

   public EnteredSuppressed.Decision getEnteredSuppressed(EnteredSuppressed.Request request) {
      long time = request.getActiveReason();
      if (!this.enteredSuppressedActive) {
         this.enteredSuppressedActive = true;
         this.suppressedTime = time;
         return EnteredSuppressed.Decision.ENTERED;
      } else {
         return this.suppressedTime == time ? EnteredSuppressed.Decision.DUPLICATE_SUPPRESSED : EnteredSuppressed.Decision.ALREADY_PAUSED_DIFFERENT_REASON;
      }
   }

   public long getSuppressedTime() {
      return this.suppressedTime;
   }

   public void updateTimeFlag() {
      this.enteredSuppressedActive = false;
      this.suppressedTime = 0L;
   }

   public static enum Decision {
      ENTERED,
      DUPLICATE_SUPPRESSED,
      ALREADY_PAUSED_DIFFERENT_REASON;
   }

   public record Request(String reason, long target, long planId, int activeLayer) {
      public Request(String reason, long target, long planId, int activeLayer) {
         reason = Objects.requireNonNullElse(reason, "unknown");
         this.reason = reason;
         this.target = target;
         this.planId = planId;
         this.activeLayer = activeLayer;
      }

      public long getActiveReason() {
         long activeReasonValue = -3750763034362895579L;
         long activeReasonValue2 = (activeReasonValue ^ this.reason.hashCode()) * 1099511628211L;
         long activeReasonValue3 = (activeReasonValue2 ^ this.target) * 1099511628211L;
         long activeReasonValue4 = (activeReasonValue3 ^ this.planId) * 1099511628211L;
         return (activeReasonValue4 ^ this.activeLayer) * 1099511628211L;
      }
   }
}
