package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.corz.client.utils.ElseMaxData;

public final class ComparingUtils {
   private static final ComparingUtils.Rules EMPTY_RULES = new ComparingUtils.Rules(0L, 0L, 0L);
   private static final Comparator<ElseMaxData> COMPARATOR = ComparingUtils::getPriceSeconds;
   private static final Comparator<ElseMaxData> comparingInt = COMPARATOR.thenComparing(Comparator.comparingLong(ElseMaxData::secondsLeft).reversed())
      .thenComparing(Comparator.comparingDouble(ElseMaxData::durability).reversed())
      .thenComparingLong(ElseMaxData::price)
      .thenComparingInt(ElseMaxData::page)
      .thenComparingInt(ElseMaxData::slot);

   private static int getPriceSeconds(ElseMaxData elseMaxData, ElseMaxData elseMaxData2) {
      long maxSecondsLeft = Math.max(1L, elseMaxData.secondsLeft());
      long max2 = Math.max(1L, elseMaxData2.secondsLeft());
      long multiplyHigh = Math.multiplyHigh(elseMaxData.price(), max2);
      long totalPriceData = Math.multiplyHigh(elseMaxData2.price(), maxSecondsLeft);
      return multiplyHigh != totalPriceData ? Long.compare(multiplyHigh, totalPriceData) : Long.compareUnsigned(elseMaxData.price() * max2, elseMaxData2.price() * maxSecondsLeft);
   }

   public static List<ElseMaxData> getComparingMax(Collection<ElseMaxData> collection, ComparingUtils.Rules rules) {
      ArrayList arrayList = new ArrayList();
      if (collection == null) {
         return arrayList;
      } else {
         ComparingUtils.Rules rules3 = rules == null ? EMPTY_RULES : rules;

         for (ElseMaxData elseMaxData : collection) {
            if (canLeftSeconds(elseMaxData, rules3)) {
               arrayList.add(elseMaxData);
            }
         }

         arrayList.sort(comparingInt);
         return arrayList;
      }
   }

   private static boolean canLeftSeconds(ElseMaxData elseMaxData, ComparingUtils.Rules rules) {
      if (elseMaxData == null || elseMaxData.price() <= 0L || elseMaxData.secondsLeft() <= 0L) {
         return false;
      } else if (rules.maxPrice() > 0L && elseMaxData.maxCharge() > rules.maxPrice()) {
         return false;
      } else {
         return rules.budgetLeft() > 0L && elseMaxData.maxCharge() > rules.budgetLeft() ? false : elseMaxData.secondsLeft() >= rules.minSecondsLeft();
      }
   }

   private ComparingUtils() {
   }

   public static Optional<ElseMaxData> getOptional(Collection<ElseMaxData> collection, ComparingUtils.Rules rules) {
      List comparingMax = getComparingMax(collection, rules);
      return comparingMax.isEmpty() ? Optional.empty() : Optional.of((ElseMaxData)comparingMax.get(0));
   }

   public record Rules(long maxPrice, long minSecondsLeft, long budgetLeft) {
   }
}
