package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.Deque;

public final class DoneMaxUtils {
   private static long lastDequeTimeMs;
   private static final Deque<DoneMaxUtils.Sample> DEQUE = new ArrayDeque<>();
   private static int doneValue = -1;
   private static volatile boolean dequeActive;
   private static volatile double elapsedSeconds = -1.0;
   private static volatile int maxDequeLimit = 1;
   private static int lastDequeBound;
   private static long timeMillis;
   private static long dequeCount;
   private static volatile double dequeAmount;

   public static void updateTimeMax() {
      DEQUE.clear();
      dequeCount = 0L;
      timeMillis = 0L;
      doneValue = -1;
      lastDequeBound = 0;
      lastDequeTimeMs = 0L;
      elapsedSeconds = -1.0;
      dequeAmount = 0.0;
      dequeActive = false;
      maxDequeLimit = 1;
   }

   private static void handleDequeAmount(int max, int dequeBound) {
      DEQUE.clear();
      DEQUE.addLast(new DoneMaxUtils.Sample(0L, max));
      dequeCount = 0L;
      lastDequeTimeMs = 0L;
      doneValue = max;
      lastDequeBound = dequeBound;
      elapsedSeconds = -1.0;
      dequeAmount = 0.0;
      dequeActive = false;
   }

   public static double getAmount() {
      return elapsedSeconds;
   }

   public static double getDequeAmount() {
      return dequeAmount;
   }

   public static void handleTimeDeque(int intersectsCenterBound, int dequeBound, int rocketsBuyBound, boolean dequeFlag, long timeMillis2) {
      maxDequeLimit = Math.max(1, rocketsBuyBound);
      if (dequeBound <= 0) {
         updateTimeMax();
      } else {
         intersectsCenterBound = Math.max(0, Math.min(intersectsCenterBound, dequeBound));
         long dequeValue = timeMillis == 0L ? 0L : Math.max(0L, Math.min(1000L, timeMillis2 - timeMillis));
         timeMillis = timeMillis2;
         if (doneValue < 0 || dequeBound != lastDequeBound || intersectsCenterBound < doneValue - 8) {
            handleDequeAmount(intersectsCenterBound, dequeBound);
         } else if (dequeFlag) {
            dequeCount += dequeValue;
            if (intersectsCenterBound > doneValue) {
               lastDequeTimeMs = dequeCount;
            }

            doneValue = intersectsCenterBound;
            DEQUE.addLast(new DoneMaxUtils.Sample(dequeCount, intersectsCenterBound));

            while (DEQUE.size() > 2 && dequeCount - DEQUE.peekFirst().clockMs() > 15000L) {
               DEQUE.removeFirst();
            }

            DoneMaxUtils.Sample peekFirst = DEQUE.peekFirst();
            long dequeLimit = dequeCount - peekFirst.clockMs();
            int dequeValue2 = intersectsCenterBound - peekFirst.placed();
            double dequeDivisor = dequeLimit > 0L ? Math.max(0.0, (double)dequeValue2 / dequeLimit) : 0.0;
            dequeAmount = dequeDivisor * 1000.0;
            int dequeLimit2 = dequeBound - intersectsCenterBound;
            dequeActive = dequeLimit2 > 0 && dequeCount - lastDequeTimeMs > 6000L;
            if (dequeLimit2 <= 0) {
               elapsedSeconds = 0.0;
            } else if (!(dequeDivisor <= 1.0E-4) && !dequeActive) {
               elapsedSeconds = dequeLimit2 / dequeDivisor / 1000.0;
            } else {
               elapsedSeconds = -1.0;
            }
         }
      }
   }

   public static String formatDone(double doneValue) {
      if (doneValue <= 0.0) {
         return "done";
      } else {
         long ceilSeconds = (long)Math.ceil(doneValue);
         long doneLimit = ceilSeconds / 3600L;
         long doneLimit2 = ceilSeconds % 3600L / 60L;
         long doneValue2 = ceilSeconds % 60L;
         if (doneLimit > 0L) {
            return doneLimit + "h " + doneLimit2 + "m";
         } else {
            return doneLimit2 > 0L ? doneLimit2 + "m " + doneValue2 + "s" : doneValue2 + "s";
         }
      }
   }

   public static String formatAmount() {
      double doneValue = elapsedSeconds;
      return doneValue < 0.0 ? "" : formatDone(doneValue);
   }

   private DoneMaxUtils() {
   }

   public static boolean isFlag() {
      return dequeActive;
   }

   public static int getMax() {
      return maxDequeLimit;
   }

   public static boolean isAmount() {
      return elapsedSeconds >= 0.0;
   }

   private record Sample(long clockMs, int placed) {
   }
}
