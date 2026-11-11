package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.corz.client.utils.TotalPriceData;

public final class TotalPriceUtilsOptional {
   private TotalPriceUtilsOptional() {
   }

   public static Optional<TotalPriceData> getOptional(
      List<TotalPriceData> list, int optionalValue, long optionalValue2, long l2, boolean optionalFlag, double totalPriceBound
   ) {
      return getTotalPrice(list, optionalValue, optionalValue2, l2, optionalFlag, totalPriceBound, 0);
   }

   public static Optional<TotalPriceData> getTotalPrice(
      List<TotalPriceData> list, int totalPriceLimit, long totalPriceLimit2, long l2, boolean totalPriceFlag, double totalPriceBound, int totalPriceLimit4
   ) {
      if (list != null && totalPriceLimit > 0) {
         ArrayList arrayList = new ArrayList();

         for (TotalPriceData totalPriceData : list) {
            if (totalPriceData != null
               && totalPriceData.count() > 0
               && totalPriceData.totalPrice() >= 0L
               && (totalPriceLimit2 <= 0L || totalPriceData.getTotalPrice() <= totalPriceLimit2)
               && (l2 <= 0L || totalPriceData.totalPrice() <= l2)) {
               arrayList.add(totalPriceData);
            }
         }

         if (arrayList.isEmpty()) {
            return Optional.empty();
         } else if (!totalPriceFlag) {
            return arrayList.stream()
               .min(
                  Comparator.comparingLong(TotalPriceData::getTotalPrice)
                     .thenComparing(Comparator.comparingInt(TotalPriceData::count).reversed())
                     .thenComparingInt(TotalPriceData::slot)
               );
         } else {
            long orElse = arrayList.stream().mapToLong(obj -> ((TotalPriceData) obj).getTotalPrice()).min().orElse(Long.MAX_VALUE);
            long totalPriceLimit5 = orElse + Math.round(orElse * Math.max(0.0, totalPriceBound));
            long totalPriceLimit6 = totalPriceLimit4 > 0 ? (long)totalPriceLimit + totalPriceLimit4 : Long.MAX_VALUE;
            ArrayList arrayList2 = new ArrayList();

            for (TotalPriceData totalPriceData2 : (Iterable<com.corz.client.utils.TotalPriceData>) (arrayList)) {
               if (totalPriceData2.getTotalPrice() <= totalPriceLimit5) {
                  arrayList2.add(totalPriceData2);
               }
            }

            Optional minThenComparingInt = arrayList2.stream()
               .filter(obj2 -> ((com.corz.client.utils.TotalPriceData) obj2).count() >= totalPriceLimit && ((com.corz.client.utils.TotalPriceData) obj2).count() <= totalPriceLimit6)
               .min(Comparator.comparingInt(TotalPriceData::count).thenComparingLong(TotalPriceData::getTotalPrice).thenComparingInt(TotalPriceData::slot));
            if (minThenComparingInt.isPresent()) {
               return minThenComparingInt;
            } else {
               Optional min2 = arrayList2.stream()
                  .filter(obj3 -> ((com.corz.client.utils.TotalPriceData) obj3).count() < totalPriceLimit)
                  .min(
                     Comparator.comparingInt(TotalPriceData::count)
                        .reversed()
                        .thenComparingLong(TotalPriceData::getTotalPrice)
                        .thenComparingInt(TotalPriceData::slot)
                  );
               return min2.isPresent()
                  ? min2
                  : arrayList2.stream()
                     .filter(fneed -> ((com.corz.client.utils.TotalPriceData) fneed).count() >= totalPriceLimit)
                     .min(
                        Comparator.comparingInt(TotalPriceData::count).thenComparingLong(TotalPriceData::getTotalPrice).thenComparingInt(TotalPriceData::slot)
                     );
            }
         }
      } else {
         return Optional.empty();
      }
   }

   public static Optional<TotalPriceData> getTotalPrice(
      List<TotalPriceData> list, int totalPriceLimit, long totalPriceLimit2, long l2, double totalPriceBound, int minCap
   ) {
      if (list != null && totalPriceLimit > 0) {
         int maxUnitPrice2 = totalPriceLimit;
         ArrayList arrayList = new ArrayList();
         double maxValue = Double.MAX_VALUE;

         for (TotalPriceData totalPriceData : list) {
            if (totalPriceData != null
               && totalPriceData.count() > 0
               && totalPriceData.totalPrice() >= 0L
               && (totalPriceLimit2 <= 0L || totalPriceData.getTotalPrice() <= totalPriceLimit2)
               && (l2 <= 0L || totalPriceData.totalPrice() <= l2)) {
               maxValue = Math.min(maxValue, getTotalPrice(totalPriceData, maxUnitPrice2));
               if (minCap <= 0 || totalPriceData.count() <= minCap) {
                  arrayList.add(totalPriceData);
               }
            }
         }

         if (arrayList.isEmpty()) {
            return Optional.empty();
         } else {
            double totalPriceLimit5 = maxValue * (1.0 + Math.max(0.0, totalPriceBound));
            return arrayList.stream()
               .filter(fneed -> getTotalPrice(((com.corz.client.utils.TotalPriceData) (fneed)), maxUnitPrice2) <= totalPriceLimit5)
               .min(
                  Comparator.<TotalPriceData>comparingInt(totalPriceData2 -> Math.min(totalPriceData2.count(), maxUnitPrice2))
                     .reversed()
                     .thenComparingLong(TotalPriceData::totalPrice)
                     .thenComparingInt(TotalPriceData::count)
                     .thenComparingInt(TotalPriceData::slot)
               );
         }
      } else {
         return Optional.empty();
      }
   }

   public static double getTotalPrice(TotalPriceData totalPriceData, int totalPriceBound) {
      return (double)totalPriceData.totalPrice() / Math.max(1, Math.min(totalPriceData.count(), Math.max(1, totalPriceBound)));
   }
}
