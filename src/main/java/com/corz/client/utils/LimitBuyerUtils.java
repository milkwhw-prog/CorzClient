package com.corz.client.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.corz.client.utils.TotalPriceData;
import com.corz.client.utils.BuyerAppendUtils;

public final class LimitBuyerUtils {

   public static LimitBuyerUtils.Limit getSpendingLimit(long spendingLimitBound, long spendingLimitLimit, double apiPutBound) {
      long maxJumpMultiplier = Math.max(0L, spendingLimitBound);
      long max3 = 0L;
      if (spendingLimitLimit > 0L && apiPutBound > 0.0) {
         double ceil4 = Math.ceil(spendingLimitLimit * Math.max(1.0, apiPutBound));
         long cooldownPlayerBound = ceil4 >= 9.223372E18F ? Long.MAX_VALUE : (long)ceil4;
         max3 = Math.max(cooldownPlayerBound, 250L);
      }

      if (maxJumpMultiplier <= 0L || max3 > 0L && maxJumpMultiplier > max3) {
         return max3 > 0L
            ? new LimitBuyerUtils.Limit(max3, LimitBuyerUtils.Reason.PRICE_JUMP, maxJumpMultiplier, spendingLimitLimit, apiPutBound)
            : new LimitBuyerUtils.Limit(0L, LimitBuyerUtils.Reason.NONE, maxJumpMultiplier, spendingLimitLimit, apiPutBound);
      } else {
         return new LimitBuyerUtils.Limit(maxJumpMultiplier, LimitBuyerUtils.Reason.SPENDING_LIMIT, maxJumpMultiplier, spendingLimitLimit, apiPutBound);
      }
   }

   public static boolean canFlag(long limitBuyerLimit, long l2) {
      return limitBuyerLimit > 0L && l2 >= 0L && limitBuyerLimit > l2 + Math.max(1L, l2 / 4L);
   }

   public static boolean canFlag(List<TotalPriceData> list, LimitBuyerUtils.Limit limit) {
      long totalPrice = getTotalPrice(list);
      return totalPrice >= 0L && limit != null && !limit.canUnitPrice(totalPrice);
   }

   private LimitBuyerUtils() {
   }

   public static long getTotalPrice(List<TotalPriceData> list) {
      long totalPrice = -1L;
      if (list != null) {
         for (TotalPriceData totalPriceData : list) {
            if (totalPriceData != null && totalPriceData.count() > 0 && totalPriceData.totalPrice() >= 0L) {
               long totalPrice2 = totalPriceData.getTotalPrice();
               if (totalPrice < 0L || totalPrice2 < totalPrice) {
                  totalPrice = totalPrice2;
               }
            }
         }
      }

      return totalPrice;
   }

   private static String formatText(double limitBuyerValue) {
      return limitBuyerValue == Math.rint(limitBuyerValue) ? Long.toString((long)limitBuyerValue) : String.valueOf(limitBuyerValue);
   }

   public static long getTime(long limitBuyerLimit, int limitBuyerLimit2) {
      return limitBuyerLimit2 > 0 && limitBuyerLimit > 0L ? (limitBuyerLimit + limitBuyerLimit2 - 1L) / limitBuyerLimit2 : 0L;
   }

   public record Entry(long unitPrice, long day, long lastPrice, long lastDay) {
      public Entry(long unitPrice, long day) {
         this(unitPrice, day, unitPrice, day);
      }
   }

   public record Limit(long maxUnitPrice, LimitBuyerUtils.Reason reason, long spendingLimit, long lastPaid, double jump) {
      public boolean isUnitPrice() {
         return this.maxUnitPrice > 0L;
      }

      public String formatLimitUnit() {
         return switch (this.reason) {
            case NONE -> "no limit";
            case SPENDING_LIMIT -> "over the Spending Limit of " + BuyerAppendUtils.formatText(this.maxUnitPrice) + " per item";
            case PRICE_JUMP -> "more than "
               + LimitBuyerUtils.formatText(this.jump)
               + "x the "
               + BuyerAppendUtils.formatText(this.lastPaid)
               + " paid before (limit "
               + BuyerAppendUtils.formatText(this.maxUnitPrice)
               + " per item)";
         };
      }

      public boolean canUnitPrice(long unitPrice) {
         return this.maxUnitPrice <= 0L || unitPrice <= this.maxUnitPrice;
      }
   }

   public static final class Memory<K> {
      private final ConcurrentHashMap<K, LimitBuyerUtils.Entry> concurrentHashMap = new ConcurrentHashMap<>();

      public void handleConcurrentUnit(K k2, LimitBuyerUtils.Entry entry) {
         if (k2 != null && entry != null && entry.unitPrice() > 0L) {
            this.concurrentHashMap.put((K)k2, entry);
         }
      }

      public int getConcurrentHash() {
         return this.concurrentHashMap.size();
      }

      public void updateConcurrentHash() {
         this.concurrentHashMap.clear();
      }

      public Map<K, LimitBuyerUtils.Entry> getUnmodifiableMap() {
         return Collections.unmodifiableMap(this.concurrentHashMap);
      }

      public int getConcurrentHash(long concurrentHashValue) {
         int concurrenthashmapSize = this.concurrentHashMap.size();
         this.concurrentHashMap.values().removeIf(entry -> concurrentHashValue - Math.max(entry.day(), entry.lastDay()) > 30L);
         return concurrenthashmapSize - this.concurrentHashMap.size();
      }

      public long getPriceDay(K k2, long priceDayValue) {
         LimitBuyerUtils.Entry entry = k2 == null ? null : this.concurrentHashMap.get(k2);
         if (entry == null) {
            return 0L;
         } else if (entry.unitPrice() > 0L && priceDayValue - entry.day() <= 30L) {
            return entry.unitPrice();
         } else {
            return entry.lastPrice() > 0L && priceDayValue - entry.lastDay() <= 30L ? entry.lastPrice() : 0L;
         }
      }

      public boolean canDayConcurrent(K k2, long dayConcurrentLimit, long dayConcurrentValue) {
         if (k2 != null && dayConcurrentLimit > 0L) {
            LimitBuyerUtils.Entry concurrenthashmap = this.concurrentHashMap.get(k2);
            long priceDay = this.getPriceDay((K)k2, dayConcurrentValue);
            if (concurrenthashmap != null && priceDay > 0L && dayConcurrentLimit > priceDay) {
               boolean dayConcurrentFlag = concurrenthashmap.unitPrice() > 0L && dayConcurrentValue - concurrenthashmap.day() <= 30L;
               long dayConcurrentValue2 = dayConcurrentFlag && concurrenthashmap.lastPrice() > concurrenthashmap.unitPrice() && dayConcurrentValue - concurrenthashmap.lastDay() <= 30L ? Math.min(concurrenthashmap.lastPrice(), dayConcurrentLimit) : dayConcurrentLimit;
               this.concurrentHashMap.put((K)k2, new LimitBuyerUtils.Entry(priceDay, dayConcurrentFlag ? concurrenthashmap.day() : concurrenthashmap.lastDay(), dayConcurrentValue2, dayConcurrentValue));
               return !dayConcurrentFlag;
            } else {
               this.concurrentHashMap.put((K)k2, new LimitBuyerUtils.Entry(dayConcurrentLimit, dayConcurrentValue));
               return true;
            }
         } else {
            return false;
         }
      }
   }

   public static enum Reason {
      NONE,
      SPENDING_LIMIT,
      PRICE_JUMP;
   }
}
