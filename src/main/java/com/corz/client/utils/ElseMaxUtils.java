package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import com.corz.client.utils.ComparingUtils;
import com.corz.client.utils.MaxDataOver;
import com.corz.client.utils.ElseMaxData;
import com.corz.client.utils.FormatFakesUtils;
import com.corz.client.utils.ShardAxeType;

public final class ElseMaxUtils {
   private static final ComparingUtils.Rules EMPTY_RULES = new ComparingUtils.Rules(0L, 0L, 0L);

   public static boolean canFlag(MaxDataOver maxDataOver, ElseMaxData elseMaxData, ComparingUtils.Rules rules, long elseValue) {
      return maxDataOver != null && elseMaxData != null ? elseMaxData.equals(getLeftSeconds(maxDataOver, rules, elseValue).getRanked()) : false;
   }

   private static ElseMaxData getDurabilitySeconds(ElseMaxData elseMaxData, long durabilitySecondsValue, long timeMillis) {
      long secondsLeft = FormatFakesUtils.getSecondsLeft(elseMaxData, durabilitySecondsValue, timeMillis);
      return secondsLeft == elseMaxData.secondsLeft()
         ? elseMaxData
         : new ElseMaxData(
            elseMaxData.kind(),
            elseMaxData.page(),
            elseMaxData.slot(),
            elseMaxData.name(),
            elseMaxData.price(),
            elseMaxData.maxCharge(),
            secondsLeft,
            elseMaxData.durability(),
            elseMaxData.fingerprint()
         );
   }

   public static boolean canScanned(MaxDataOver maxDataOver, long scannedValue) {
      return maxDataOver == null || scannedValue - maxDataOver.scannedAtMs() > 600000L;
   }

   public static ElseMaxData getScannedAll(MaxDataOver maxDataOver, ElseMaxData elseMaxData, long scannedValue) {
      return maxDataOver != null && elseMaxData != null && maxDataOver.all().contains(elseMaxData) ? getDurabilitySeconds(elseMaxData, maxDataOver.scannedAtMs(), scannedValue) : elseMaxData;
   }

   public static MaxDataOver getLeftSeconds(MaxDataOver maxDataOver, ComparingUtils.Rules rules, long leftSecondsValue) {
      if (maxDataOver == null) {
         return null;
      } else {
         ComparingUtils.Rules rules3 = rules == null ? EMPTY_RULES : rules;
         List all = maxDataOver.all();
         IdentityHashMap identityHashMap = new IdentityHashMap(all.size() * 2);
         ArrayList arrayList = new ArrayList(all.size());
         int leftSecondsCounter = 0;
         int leftSecondsCounter2 = 0;
         int leftSecondsCounter3 = 0;

         for (ElseMaxData elseMaxData : (Iterable<com.corz.client.utils.ElseMaxData>) (all)) {
            ElseMaxData durabilitySeconds = getDurabilitySeconds(elseMaxData, maxDataOver.scannedAtMs(), leftSecondsValue);
            identityHashMap.put(durabilitySeconds, elseMaxData);
            arrayList.add(durabilitySeconds);
            if (durabilitySeconds.secondsLeft() <= 0L || durabilitySeconds.secondsLeft() < rules3.minSecondsLeft()) {
               leftSecondsCounter++;
            }

            if (rules3.maxPrice() > 0L && durabilitySeconds.maxCharge() > rules3.maxPrice()) {
               leftSecondsCounter2++;
            }

            if (rules3.budgetLeft() > 0L && durabilitySeconds.maxCharge() > rules3.budgetLeft()) {
               leftSecondsCounter3++;
            }
         }

         List comparingMax = ComparingUtils.getComparingMax(arrayList, rules3);
         int leftSecondsLimit = Math.min(50, comparingMax.size());
         ArrayList arrayList2 = new ArrayList(leftSecondsLimit);

         for (int i = 0; i < leftSecondsLimit; i++) {
            arrayList2.add((ElseMaxData)identityHashMap.get(comparingMax.get(i)));
         }

         return new MaxDataOver(
            maxDataOver.kind(), arrayList2, all, maxDataOver.listings(), maxDataOver.fakes(), maxDataOver.unread(), leftSecondsCounter, leftSecondsCounter2, leftSecondsCounter3, maxDataOver.pages(), maxDataOver.opened(), maxDataOver.scannedAtMs()
         );
      }
   }

   public static MaxDataOver getMaxDataForShardAxeTypeCollection(
      ShardAxeType shardAxeType, Collection<ElseMaxData> collection, int elseValue, ComparingUtils.Rules rules, int elseValue2, boolean elseFlag, long kindValue
   ) {
      return getKind(shardAxeType, collection, elseValue, 0, rules, elseValue2, elseFlag, kindValue);
   }

   public static MaxDataOver getRankedListings(MaxDataOver rankedListings, ElseMaxData elseMaxData) {
      if (rankedListings != null && elseMaxData != null) {
         List list = getItems(rankedListings.ranked(), elseMaxData);
         List items2 = getItems(rankedListings.all(), elseMaxData);
         return list == null && items2 == null
            ? rankedListings
            : new MaxDataOver(
               rankedListings.kind(),
               list == null ? rankedListings.ranked() : list,
               items2 == null ? rankedListings.all() : items2,
               items2 == null ? rankedListings.listings() : Math.max(0, rankedListings.listings() - 1),
               rankedListings.fakes(),
               rankedListings.unread(),
               rankedListings.tooShort(),
               rankedListings.overPrice(),
               rankedListings.overBudget(),
               rankedListings.pages(),
               rankedListings.opened(),
               rankedListings.scannedAtMs()
            );
      } else {
         return rankedListings;
      }
   }

   private static List<ElseMaxData> getItems(List<ElseMaxData> list2, ElseMaxData elseMaxData) {
      int indexOf = list2.indexOf(elseMaxData);

      for (int i = 0; indexOf < 0 && i < list2.size(); i++) {
         if (canFingerprintPage((ElseMaxData)list2.get(i), elseMaxData)) {
            indexOf = i;
         }
      }

      if (indexOf < 0) {
         return null;
      } else {
         ArrayList arrayList = new ArrayList(list2);
         arrayList.remove(indexOf);
         return arrayList;
      }
   }

   private static boolean canFingerprintPage(ElseMaxData elseMaxData, ElseMaxData elseMaxData2) {
      return elseMaxData.kind() == elseMaxData2.kind() && elseMaxData.page() == elseMaxData2.page() && elseMaxData.slot() == elseMaxData2.slot() && elseMaxData.fingerprint() != null && elseMaxData.fingerprint().equals(elseMaxData2.fingerprint());
   }

   private ElseMaxUtils() {
   }

   public static MaxDataOver getKind(
      ShardAxeType shardAxeType, Collection<ElseMaxData> collection, int kindBound, int rocketsBuyBound, ComparingUtils.Rules rules, int kindBound3, boolean kindFlag, long kindValue
   ) {
      ArrayList arrayList = new ArrayList();
      if (collection != null) {
         for (ElseMaxData elseMaxData : collection) {
            if (elseMaxData != null && elseMaxData.kind() == shardAxeType) {
               arrayList.add(elseMaxData);
            }
         }
      }

      MaxDataOver maxDataOver = new MaxDataOver(
         shardAxeType, List.of(), arrayList, arrayList.size(), Math.max(0, kindBound), Math.max(0, rocketsBuyBound), 0, 0, 0, Math.max(0, kindBound3), kindFlag, kindValue
      );
      return getLeftSeconds(maxDataOver, rules, kindValue);
   }
}
