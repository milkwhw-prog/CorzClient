package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.corz.client.utils.TotalPriceData;

public final class TotalPriceUtilsConst {

   public static boolean canFlag(TotalPriceData totalPriceData) {
      return totalPriceData != null && totalPriceData.count() >= 16;
   }

   public static TotalPriceUtilsConst.Plan getTotalPrice(List<TotalPriceData> list, int totalPriceLimit, long totalPriceLimit2, double totalPriceBound, int totalPriceValue) {
      ArrayList arrayList = new ArrayList();
      if (list != null) {
         for (TotalPriceData totalPriceData : list) {
            if (totalPriceData != null && totalPriceData.count() > 0 && totalPriceData.totalPrice() >= 0L && (totalPriceLimit2 <= 0L || totalPriceData.getTotalPrice() <= totalPriceLimit2)) {
               arrayList.add(totalPriceData);
            }
         }
      }

      if (totalPriceLimit > 0 && !arrayList.isEmpty()) {
         long orElse = arrayList.stream().mapToLong(obj -> ((TotalPriceData) obj).getTotalPrice()).min().orElse(Long.MAX_VALUE);
         long totalPriceLimit3 = orElse + Math.round(orElse * Math.max(0.0, totalPriceBound));
         ArrayList arrayList2 = new ArrayList();

         for (TotalPriceData totalPriceData2 : (Iterable<com.corz.client.utils.TotalPriceData>) (arrayList)) {
            if (totalPriceData2.getTotalPrice() <= totalPriceLimit3) {
               arrayList2.add(totalPriceData2);
            }
         }

         arrayList2.sort(
            Comparator.comparingInt(TotalPriceData::count).reversed().thenComparingLong(TotalPriceData::getTotalPrice).thenComparingInt(TotalPriceData::slot)
         );
         ArrayList arrayList3 = new ArrayList();
         int totalPriceTotal = 0;
         long totalPriceTotal2 = 0L;
         int totalPriceValue2 = totalPriceValue < 0 ? 0 : totalPriceValue;

         for (TotalPriceData totalPriceData3 : (Iterable<com.corz.client.utils.TotalPriceData>) (arrayList2)) {
            if (totalPriceTotal >= totalPriceLimit) {
               break;
            }

            if (totalPriceTotal + totalPriceData3.count() <= totalPriceLimit + totalPriceValue2) {
               arrayList3.add(totalPriceData3);
               totalPriceTotal += totalPriceData3.count();
               totalPriceTotal2 += totalPriceData3.totalPrice();
            }
         }

         if (totalPriceTotal < totalPriceLimit) {
            TotalPriceData downOffset4 = null;

            for (TotalPriceData downOffset : (Iterable<com.corz.client.utils.TotalPriceData>) (arrayList2)) {
               if (!arrayList3.contains(downOffset)
                  && totalPriceTotal + downOffset.count() >= totalPriceLimit
                  && (downOffset4 == null || downOffset.count() < downOffset4.count() || downOffset.count() == downOffset4.count() && downOffset.getTotalPrice() < downOffset4.getTotalPrice())) {
                  downOffset4 = downOffset;
               }
            }

            if (downOffset4 != null) {
               arrayList3.add(downOffset4);
               totalPriceTotal += downOffset4.count();
               totalPriceTotal2 += downOffset4.totalPrice();
            }
         }

         return new TotalPriceUtilsConst.Plan(arrayList3, totalPriceTotal, arrayList3.size(), totalPriceTotal2, Math.max(0, totalPriceTotal - totalPriceLimit));
      } else {
         return new TotalPriceUtilsConst.Plan(List.of(), 0, 0, 0L, 0);
      }
   }

   private TotalPriceUtilsConst() {
   }

   public record Plan(List<TotalPriceData> listings, int items, int transactions, long price, int overbuy) {
      public String formatOverbuyTransactions() {
         return "transactions=" + this.transactions + " items=" + this.items + " price=" + this.price + " overbuy=" + this.overbuy;
      }

      public boolean isEmpty() {
         return this.listings.isEmpty();
      }
   }
}
