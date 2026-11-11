package com.corz.client.utils;

public final class DealBoardUtils {

   private static long getMax(long dealBoardValue) {
      return Math.max(100000L, getTimeLong(dealBoardValue) / 100L);
   }

   public static int getValue(int dealBoardValue, int dealBoardLimit, boolean dealBoardFlag, int dealBoardBound) {
      long etaSeconds = Math.max(100000, dealBoardBound);
      if (dealBoardLimit == 0) {
         return dealBoardValue;
      } else if (dealBoardValue <= 0) {
         return dealBoardLimit > 0 ? 0 : (int)etaSeconds;
      } else {
         long dealBoardValue2 = dealBoardValue;
         if (dealBoardLimit > 0) {
            if (dealBoardValue2 >= etaSeconds) {
               return 0;
            } else {
               long unreachableBound = dealBoardValue2 < 100000L ? 100000L : (dealBoardFlag ? dealBoardValue2 / getMax(dealBoardValue2) * getMax(dealBoardValue2) + getMax(dealBoardValue2) : getTimeForLong(dealBoardValue2));
               return (int)Math.min(etaSeconds, unreachableBound);
            }
         } else if (dealBoardValue2 <= 100000L) {
            return dealBoardValue;
         } else {
            long dealBoardDivisor = getMax(dealBoardValue2);
            long dealBoardBound4 = dealBoardFlag ? (dealBoardValue2 + dealBoardDivisor - 1L) / dealBoardDivisor * dealBoardDivisor - dealBoardDivisor : getTime(dealBoardValue2);
            return (int)Math.max(100000L, Math.min(etaSeconds, dealBoardBound4));
         }
      }
   }

   private DealBoardUtils() {
   }

   private static long getTime(long dealBoardLimit) {
      long dealBoardFactor = getTimeLong(dealBoardLimit);
      long dealBoardFactor2 = dealBoardLimit / dealBoardFactor;
      if (dealBoardLimit > dealBoardFactor2 * dealBoardFactor) {
         return dealBoardFactor2 * dealBoardFactor;
      } else {
         return dealBoardFactor2 > 1L ? (dealBoardFactor2 - 1L) * dealBoardFactor : 9L * (dealBoardFactor / 10L);
      }
   }

   public static int getValueForIntFlag(int dealBoardBound, int dealBoardLimit, boolean dealBoardFlag, int rocketsBuyBound) {
      int intersectsCenterBound = Math.max(0, rocketsBuyBound);
      int dealBoardValue = Math.max(0, Math.min(intersectsCenterBound, dealBoardBound));
      if (dealBoardLimit == 0) {
         return dealBoardValue;
      } else if (dealBoardLimit > 0) {
         int dealBoardDivisor = dealBoardFlag ? 5 : (dealBoardValue < 120 ? 15 : 60);
         return Math.min(intersectsCenterBound, (dealBoardValue / dealBoardDivisor + 1) * dealBoardDivisor);
      } else {
         int dealBoardDivisor2 = dealBoardFlag ? 5 : (dealBoardValue <= 120 ? 15 : 60);
         return Math.max(0, (dealBoardValue + dealBoardDivisor2 - 1) / dealBoardDivisor2 * dealBoardDivisor2 - dealBoardDivisor2);
      }
   }

   private static long getTimeForLong(long dealBoardValue) {
      long dealBoardDivisor = getTimeLong(dealBoardValue);
      return (dealBoardValue / dealBoardDivisor + 1L) * dealBoardDivisor;
   }

   private static long getTimeLong(long dealBoardValue) {
      long dealBoardValue2 = 1L;

      while (dealBoardValue2 <= dealBoardValue / 10L) {
         dealBoardValue2 *= 10L;
      }

      return dealBoardValue2;
   }
}
