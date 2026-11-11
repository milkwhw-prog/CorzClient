package com.corz.client.utils;

import java.util.ArrayDeque;

public final class RtpClock {
   private boolean requestPending;
   public static final long[] BACKOFF_STEPS_MS = new long[]{10000L, 20000L, 40000L, 60000L};
   private long estimatedIntervalMs;
   private long lastRequestMs;
   private long requestDeadlineMs;
   private long nextAllowedMs;
   private long lastRtpTimeMs;
   private int sampleCount;
   private boolean predictionActive;
   private int backoffStep;
   private final ArrayDeque<Long> recentRequestTimes = new ArrayDeque<>();

   public void onCooldownReply(long rtpClockValue, long rtpClockValue2) {
      this.requestPending = false;
      this.predictionActive = false;
      this.sampleCount = 0;
      if (rtpClockValue2 < 0L) {
         this.nextAllowedMs = Math.max(this.nextAllowedMs, rtpClockValue + this.nextBackoffMs());
      } else {
         this.backoffStep = 0;
         this.nextAllowedMs = Math.max(rtpClockValue + 1L, rtpClockValue + rtpClockValue2 + 600L);
         if (this.lastRtpTimeMs != -2305843009213693952L) {
            this.estimatedIntervalMs = clampInterval(rtpClockValue - this.lastRtpTimeMs + rtpClockValue2);
         }
      }
   }

   public boolean canSendAt(long rtpClockLimit) {
      return !this.requestPending && rtpClockLimit >= this.getNextAllowedMs(rtpClockLimit);
   }

   public void postponeShort(long rtpClockValue) {
      this.requestPending = false;
      this.predictionActive = false;
      this.nextAllowedMs = Math.max(this.nextAllowedMs, rtpClockValue + 1500L);
   }

   private long nextBackoffMs() {
      long times2 = BACKOFF_STEPS_MS[Math.min(this.backoffStep, BACKOFF_STEPS_MS.length - 1)];
      this.backoffStep++;
      return times2;
   }

   public long getLastRtpTimeMs() {
      return this.lastRtpTimeMs;
   }

   public void onRequestSent(long time) {
      this.pruneOldRequests(time);
      this.recentRequestTimes.addLast(time);
      this.lastRequestMs = time;
      this.requestPending = true;
      this.predictionActive = false;
      this.requestDeadlineMs = time + 12000L;
   }

   public boolean isPredictionActive() {
      return this.predictionActive;
   }

   public int getRecentRequestCount(long dequeArrayValue) {
      this.pruneOldRequests(dequeArrayValue);
      return this.recentRequestTimes.size();
   }

   public long getNextAllowedMs(long rtpClockValue) {
      this.pruneOldRequests(rtpClockValue);
      long rtpClockBound = Math.max(this.nextAllowedMs, this.lastRequestMs + 3000L);
      if (this.recentRequestTimes.size() >= 4) {
         rtpClockBound = Math.max(rtpClockBound, this.recentRequestTimes.peekFirst() + 60000L);
      }

      return rtpClockBound;
   }

   public void reset() {
      this.recentRequestTimes.clear();
      this.requestPending = false;
      this.requestDeadlineMs = -2305843009213693952L;
      this.lastRequestMs = -2305843009213693952L;
      this.nextAllowedMs = -2305843009213693952L;
      this.lastRtpTimeMs = -2305843009213693952L;
      this.estimatedIntervalMs = -1L;
      this.backoffStep = 0;
      this.sampleCount = 0;
      this.predictionActive = false;
   }

   public void postponeLong(long rtpClockValue) {
      this.requestPending = false;
      this.predictionActive = false;
      this.sampleCount = 0;
      this.nextAllowedMs = Math.max(this.nextAllowedMs, rtpClockValue + 10000L);
   }

   private static long clampInterval(long rtpClockBound) {
      return Math.max(5000L, Math.min(600000L, rtpClockBound));
   }

   public void postponeMedium(long rtpClockValue) {
      this.requestPending = false;
      this.predictionActive = false;
      this.sampleCount = 0;
      this.nextAllowedMs = Math.max(this.nextAllowedMs, rtpClockValue + 3000L);
   }

   private void pruneOldRequests(long dequeArrayValue) {
      while (!this.recentRequestTimes.isEmpty() && dequeArrayValue - this.recentRequestTimes.peekFirst() >= 60000L) {
         this.recentRequestTimes.removeFirst();
      }
   }

   public long getWaitRemainingMs(long rtpClockValue) {
      return this.requestPending ? Math.max(0L, this.requestDeadlineMs - rtpClockValue) : Math.max(0L, this.getNextAllowedMs(rtpClockValue) - rtpClockValue);
   }

   public boolean hasRequestTimedOut(long rtpClockLimit) {
      return this.requestPending && rtpClockLimit >= this.requestDeadlineMs;
   }

   public boolean canSendPredicted(long dequeLimit) {
      if (!this.requestPending && this.predictionActive) {
         this.pruneOldRequests(dequeLimit);
         return dequeLimit >= this.lastRequestMs + 3000L && this.recentRequestTimes.size() < 4;
      } else {
         return false;
      }
   }

   public boolean recordFailureAndCheckLimit(long rtpClockValue) {
      this.requestPending = false;
      this.predictionActive = false;
      this.sampleCount++;
      this.nextAllowedMs = Math.max(this.nextAllowedMs, rtpClockValue + this.nextBackoffMs());
      return this.sampleCount >= 5;
   }

   public RtpClock() {
      this.requestDeadlineMs = -2305843009213693952L;
      this.lastRequestMs = -2305843009213693952L;
      this.nextAllowedMs = -2305843009213693952L;
      this.lastRtpTimeMs = -2305843009213693952L;
      this.estimatedIntervalMs = -1L;
   }

   public void onRtpCompleted(long time) {
      this.requestPending = false;
      this.sampleCount = 0;
      this.backoffStep = 0;
      this.lastRtpTimeMs = time;
      this.predictionActive = this.estimatedIntervalMs > 0L;
      this.nextAllowedMs = this.predictionActive ? time + this.estimatedIntervalMs + 400L : -2305843009213693952L;
   }

   public void extendRequestDeadline(long rtpClockValue) {
      if (this.requestPending) {
         this.requestDeadlineMs = Math.max(this.requestDeadlineMs, rtpClockValue + 15000L);
      }

      this.sampleCount = 0;
   }

   public boolean isRequestPending() {
      return this.requestPending;
   }

   public int getSampleCount() {
      return this.sampleCount;
   }

   public long getEstimatedIntervalMs() {
      return this.estimatedIntervalMs;
   }
}
