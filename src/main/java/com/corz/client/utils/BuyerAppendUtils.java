package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class BuyerAppendUtils {
   private static final long[] chsTable = new long[]{1000L, 1000000L, 1000000000L, 1000000000000L};
   private static final char[] buyerAppendTable = new char[]{'K', 'M', 'B', 'T'};
   private static final long[] table = new long[]{1L, 10L, 100L, 1000L};
   public static final String EM_DASH = "\u2014";

   public static boolean canFlag(long buyerAppendLimit) {
      return buyerAppendLimit >= 1000L;
   }

   private BuyerAppendUtils() {
   }

   private static void handle(StringBuilder stringBuilder, long buyerAppendValue, int buyerAppendLimit) {
      if (buyerAppendLimit != 0 && buyerAppendValue != 0L) {
         String buyerAppendText = Long.toString(buyerAppendValue);
         int protectValue = buyerAppendLimit;
         StringBuilder stringBuilder2 = new StringBuilder(buyerAppendLimit);

         for (int i = buyerAppendText.length(); i < buyerAppendLimit; i++) {
            stringBuilder2.append('0');
         }

         stringBuilder2.append(buyerAppendText);

         while (protectValue > 0 && stringBuilder2.charAt(protectValue - 1) == '0') {
            protectValue--;
         }

         stringBuilder.append('.').append(stringBuilder2, 0, protectValue);
      }
   }

   public static String getAppend(long appendLimit) {
      if (appendLimit == 0L) {
         return "";
      } else {
         long appendValue = appendLimit == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(appendLimit);
         return (appendLimit > 0L ? "+" : "-") + formatText(appendValue);
      }
   }

   private static void handle(StringBuilder stringBuilder, long buyerAppendValue) {
      String buyerAppendText = Long.toString(buyerAppendValue);
      int varLength = buyerAppendText.length();

      for (int i = 0; i < varLength; i++) {
         if (i > 0 && (varLength - i) % 3 == 0) {
            stringBuilder.append(',');
         }

         stringBuilder.append(buyerAppendText.charAt(i));
      }
   }

   public static String getAppendForLong(long appendValue) {
      StringBuilder stringBuilder = new StringBuilder(28);
      if (appendValue < 0L) {
         stringBuilder.append('-');
         if (appendValue == Long.MIN_VALUE) {
            return stringBuilder.append("9,223,372,036,854,775,808").toString();
         }

         appendValue = -appendValue;
      }

      handle(stringBuilder, appendValue);
      return stringBuilder.toString();
   }

   public static String getAppendString(long appendValue) {
      if (appendValue < 0L) {
         return "\u2014";
      } else {
         StringBuilder append = new StringBuilder(28).append('$');
         handle(append, appendValue);
         return append.toString();
      }
   }

   public static String formatTimesChs(long chsValue, int chsBound) {
      if (chsValue < 0L) {
         return "\u2014";
      } else if (chsValue < 1000L) {
         return "$" + chsValue;
      } else {
         int index = Math.max(0, Math.min(table.length - 1, chsBound));
         long chsFactor = table[index];
         int chsLimit = chsTable.length - 1;

         for (int i = 0; i <= chsLimit; i++) {
            long chsDivisor = chsTable[i];
            long chsCounter = chsValue / chsDivisor;
            long chsTotal = (chsValue % chsDivisor * chsFactor + chsDivisor / 2L) / chsDivisor;
            if (chsTotal >= chsFactor) {
               chsCounter++;
               chsTotal -= chsFactor;
            }

            if (chsCounter < 1000L || i >= chsLimit) {
               StringBuilder append = new StringBuilder(20).append('$');
               if (i == chsLimit) {
                  handle(append, chsCounter);
               } else {
                  append.append(chsCounter);
               }

               handle(append, chsTotal, index);
               return append.append(buyerAppendTable[i]).toString();
            }
         }

         return getAppendString(chsValue);
      }
   }

   public static <K> BuyerAppendUtils.Result<K> getClassForMap(Map<K, Integer> map, Map<K, Long> map2) {
      ArrayList arrayList = new ArrayList();
      long classTotal = 0L;
      int classCounter = 0;
      int classCounter2 = 0;

      for (Entry entry : map.entrySet()) {
         int classFactor = entry.getValue() == null ? 0 : Math.max(0, (Integer)entry.getValue());
         if (classFactor != 0) {
            Long long2 = map2 == null ? null : (Long)map2.get(entry.getKey());
            if (long2 != null && long2 >= 0L) {
               long classValue = long2 * classFactor;
               arrayList.add(new BuyerAppendUtils.Line<>(entry.getKey(), classFactor, long2, classValue, true));
               classTotal += classValue;
               classCounter++;
            } else {
               arrayList.add(new BuyerAppendUtils.Line<>(entry.getKey(), classFactor, -1L, 0L, false));
               classCounter2++;
            }
         }
      }

      arrayList.sort((obj, obj2) -> Long.compare(((com.corz.client.utils.BuyerAppendUtils.Line) obj2).cheapestLiveTime, ((com.corz.client.utils.BuyerAppendUtils.Line) obj).cheapestLiveTime));
      return new BuyerAppendUtils.Result<>(arrayList, classTotal, classCounter, classCounter2);
   }

   public static String formatText(long buyerAppendValue) {
      return formatTimesChs(buyerAppendValue, 1);
   }

   public static final class Line<K> {
      public final int maxQuantity;
      public final long timeCost;
      public final boolean cheapestLiveActive;
      public final long cheapestLiveTime;
      public final K itemKey;

      Line(K k, int max, long time, long cheapestLiveTime, boolean flag) {
         this.itemKey = (K)k;
         this.maxQuantity = max;
         this.timeCost = time;
         this.cheapestLiveTime = cheapestLiveTime;
         this.cheapestLiveActive = flag;
      }
   }

   public static final class Result<K> {
      public final int buyCostValue;
      public final long costBuyTime;
      public final int costBuyValue;
      public final List<BuyerAppendUtils.Line<K>> cheapestLiveLineList;

      Result(List<BuyerAppendUtils.Line<K>> items, long costBuyTime, int buyCostValue, int costBuyValue) {
         this.cheapestLiveLineList = items;
         this.costBuyTime = costBuyTime;
         this.buyCostValue = buyCostValue;
         this.costBuyValue = costBuyValue;
      }

      public BuyerAppendUtils.Line<K> getNext() {
         BuyerAppendUtils.Line next = null;

         for (BuyerAppendUtils.Line line : this.cheapestLiveLineList) {
            if (line.cheapestLiveActive && line.cheapestLiveTime > 0L && (next == null || line.cheapestLiveTime > next.cheapestLiveTime)) {
               next = line;
            }
         }

         return next;
      }

      public int getCostBuy() {
         return this.buyCostValue + this.costBuyValue;
      }
   }
}
