package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import com.corz.client.utils.TotalPriceData;

public final class FlipperAuctionUtils {
   private static long clampPercent(int flipperAuctionLimit) {
      if (flipperAuctionLimit < 0) {
         return 0L;
      } else {
         return flipperAuctionLimit > 100 ? 100L : flipperAuctionLimit;
      }
   }

   public static FlipperAuctionUtils.Result findFlipCandidates(List<TotalPriceData> list, FlipperAuctionUtils.Params params) {
      ArrayList arrayList = new ArrayList();
      if (list != null) {
         for (TotalPriceData totalPriceData : list) {
            if (totalPriceData != null && totalPriceData.count() > 0 && totalPriceData.totalPrice() >= 0L) {
               arrayList.add(totalPriceData);
            }
         }
      }

      if (arrayList.isEmpty()) {
         return new FlipperAuctionUtils.Result(0L, 0L, 0, List.of());
      } else {
         long[] longs = new long[arrayList.size()];

         for (int i = 0; i < arrayList.size(); i++) {
            longs[i] = ((TotalPriceData)arrayList.get(i)).getTotalPrice();
         }

         long[] clone = (long[])longs.clone();
         Arrays.sort(clone);
         long pricePercentLimit = getPercentileValue(clone, params.referencePercentile());
         long pricePercentValue = clone[0];
         if (arrayList.size() >= Math.max(1, params.minListings()) && pricePercentLimit > 0L) {
            long pricePercentFactor = pricePercentLimit * Math.max(0, params.resellPercent()) / 100L;
            long pricePercentValue2 = pricePercentFactor * (100L - clampPercent(params.salesTaxPercent())) / 100L;
            int maxMinReturnPercent = Math.max(0, params.minReturnPercent());
            ArrayList arrayList2 = new ArrayList();

            for (TotalPriceData totalPriceData2 : (Iterable<com.corz.client.utils.TotalPriceData>) (arrayList)) {
               long totalPrice = totalPriceData2.getTotalPrice();
               if (totalPrice > 0L && (params.maxUnitPrice() <= 0L || totalPrice <= params.maxUnitPrice()) && (params.budgetLeft() <= 0L || totalPriceData2.totalPrice() <= params.budgetLeft())) {
                  long deltaVar = pricePercentValue2 - totalPrice;
                  if (deltaVar > 0L && deltaVar >= params.minProfitPerItem() && deltaVar * 100L >= maxMinReturnPercent * totalPrice) {
                     arrayList2.add(
                        new FlipperAuctionUtils.Candidate(totalPriceData2.slot(), totalPriceData2.count(), totalPrice, totalPriceData2.totalPrice(), pricePercentLimit, pricePercentFactor, deltaVar, deltaVar * totalPriceData2.count())
                     );
                  }
               }
            }

            arrayList2.sort(
               Comparator.comparingLong(FlipperAuctionUtils.Candidate::totalProfit)
                  .reversed()
                  .thenComparingLong(FlipperAuctionUtils.Candidate::unitPrice)
                  .thenComparingInt(FlipperAuctionUtils.Candidate::slot)
            );
            return new FlipperAuctionUtils.Result(pricePercentLimit, pricePercentValue, arrayList.size(), arrayList2);
         } else {
            return new FlipperAuctionUtils.Result(pricePercentLimit, pricePercentValue, arrayList.size(), List.of());
         }
      }
   }

   static long getPercentileValue(long[] flipperAuctionValues, double flipperAuctionBound) {
      if (flipperAuctionValues != null && flipperAuctionValues.length != 0) {
         double flipperAuctionValue = Math.max(0.0, Math.min(100.0, flipperAuctionBound));
         int index = (int)Math.round(flipperAuctionValue / 100.0 * (flipperAuctionValues.length - 1));
         if (index < 0) {
            index = 0;
         }

         if (index >= flipperAuctionValues.length) {
            index = flipperAuctionValues.length - 1;
         }

         return flipperAuctionValues[index];
      } else {
         return 0L;
      }
   }

   private FlipperAuctionUtils() {
   }

   public record Candidate(int slot, int count, long unitPrice, long totalPrice, long referenceUnit, long resellUnit, long profitPerItem, long totalProfit) {
   }

   public record Params(
      double referencePercentile,
      int minListings,
      int minReturnPercent,
      long maxUnitPrice,
      long budgetLeft,
      int resellPercent,
      long minProfitPerItem,
      int salesTaxPercent
   ) {
   }

   public record Result(long referenceUnit, long floorUnit, int pricedListings, List<FlipperAuctionUtils.Candidate> candidates) {
      public FlipperAuctionUtils.Candidate getBestCandidate() {
         return this.candidates.isEmpty() ? null : this.candidates.get(0);
      }

      public boolean hasCandidates() {
         return !this.candidates.isEmpty();
      }
   }
}
