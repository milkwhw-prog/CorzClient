package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CandidateLimitSelector {

   public static boolean isWithinWindow(long builderAutoValue, long builderAutoValue2, int builderAutoLimit) {
      return builderAutoLimit > 0 && builderAutoValue - builderAutoValue2 < builderAutoLimit;
   }

   public static boolean hasEnoughLimit(boolean builderAutoFlag, int builderAutoLimit) {
      return builderAutoFlag && builderAutoLimit >= 2;
   }

   public static int getBonusForLimit(int builderAutoLimit) {
      return builderAutoLimit >= 3 ? 200 : 0;
   }

   private CandidateLimitSelector() {
   }

   public static List<CandidateLimitSelector.Cand> selectNearestCandidates(List<CandidateLimitSelector.Cand> list, Object obj, int builderAutoLimit) {
      ArrayList arrayList = new ArrayList();
      if (list != null && !list.isEmpty() && builderAutoLimit > 0) {
         ArrayList arrayList2 = new ArrayList(list);
         arrayList2.sort(Comparator.comparingDouble(CandidateLimitSelector.Cand::dist2).thenComparingLong(CandidateLimitSelector.Cand::key));
         Object obj2 = null;
         if (obj != null) {
            for (CandidateLimitSelector.Cand cand : (Iterable<com.corz.client.utils.CandidateLimitSelector.Cand>) (arrayList2)) {
               if (obj.equals(cand.item())) {
                  obj2 = obj;
                  break;
               }
            }
         }

         if (obj2 == null) {
            obj2 = ((CandidateLimitSelector.Cand)arrayList2.get(0)).item();
         }

         for (CandidateLimitSelector.Cand totalPriceData : (Iterable<com.corz.client.utils.CandidateLimitSelector.Cand>) (arrayList2)) {
            if (arrayList.size() >= builderAutoLimit) {
               break;
            }

            if (obj2.equals(totalPriceData.item())) {
               arrayList.add(totalPriceData);
            }
         }

         return arrayList;
      } else {
         return arrayList;
      }
   }

   public record Cand(long key, Object item, double dist2) {
   }
}
