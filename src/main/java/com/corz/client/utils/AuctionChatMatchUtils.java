package com.corz.client.utils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

public final class AuctionChatMatchUtils {
   public static boolean anyPriceMatches(List<Long> list, long fullYourValue) {
      if (list == null) {
         return false;
      } else {
         for (Long long2 : list) {
            if (long2 != null && arePricesClose(long2, fullYourValue)) {
               return true;
            }
         }

         return false;
      }
   }

   public static Set<ShardAxeType> parseShardAxeTypes(String noneOfText) {
      EnumSet noneOf = EnumSet.noneOf(ShardAxeType.class);
      String formatRoot = ShardAxeType.formatRoot(noneOfText);
      if (formatRoot.isEmpty()) {
         return noneOf;
      } else {
         for (ShardAxeType shardAxeType : ShardAxeType.values()) {
            if (formatRoot.contains(ShardAxeType.formatRoot(shardAxeType.getText()))) {
               noneOf.add(shardAxeType);
            }
         }

         return noneOf;
      }
   }

   public static boolean isInventoryFullMessage(String startsWithText) {
      return DaysHoursUtils.getLowerCase(startsWithText).startsWith("your inventory is full");
   }

   public static boolean matchesItemMessagePrefix(String fullYourText, String lowerCaseText) {
      String lowerCase = DaysHoursUtils.getLowerCase(lowerCaseText);
      String lowerCase2 = DaysHoursUtils.getLowerCase(fullYourText);
      return !lowerCase.isEmpty() && !lowerCase2.isEmpty() ? lowerCase2.startsWith(lowerCase) || lowerCase2.startsWith("this item was " + lowerCase) : false;
   }

   private AuctionChatMatchUtils() {
   }

   static long getPriceTolerance(long fullYourLimit) {
      long fullYourValue = 1L;

      while (fullYourValue <= 9223372036854775L && fullYourLimit >= fullYourValue * 1000L) {
         fullYourValue *= 1000L;
      }

      return fullYourValue / 10L;
   }

   public static boolean canChargePrice(long chargePriceLimit, ElseMaxData elseMaxData) {
      return chargePriceLimit > 0L && elseMaxData != null && chargePriceLimit > Math.max(elseMaxData.price(), elseMaxData.maxCharge());
   }

   public static boolean matchesYouMessagePrefix(String fullYourText, String lowerCaseText) {
      String lowerCase = DaysHoursUtils.getLowerCase(lowerCaseText);
      String lowerCase2 = DaysHoursUtils.getLowerCase(fullYourText);
      return !lowerCase.isEmpty() && !lowerCase2.isEmpty() ? lowerCase2.startsWith(lowerCase) || lowerCase2.startsWith("you " + lowerCase) || lowerCase2.startsWith("you do " + lowerCase) : false;
   }

   public static boolean hasMajorityOverlap(Set<String> set, Set<String> set2) {
      if (set != null && !set.isEmpty() && set2 != null) {
         int fullYourCounter = 0;

         for (String fullYourText : set2) {
            if (set.contains(fullYourText)) {
               fullYourCounter++;
            }
         }

         return fullYourCounter * 2 >= set.size();
      } else {
         return false;
      }
   }

   public static boolean arePricesClose(long fullYourLimit, long fullYourValue) {
      return fullYourLimit > 0L && fullYourValue > 0L ? Math.abs(fullYourLimit - fullYourValue) <= Math.max(getPriceTolerance(fullYourLimit), getPriceTolerance(fullYourValue)) : false;
   }

   public static List<Long> extractPricesAfterLabel(String fullYourText, String lowerCaseText) {
      ArrayList arrayList = new ArrayList();
      String lowerCase = DaysHoursUtils.getLowerCase(lowerCaseText);
      String lowerCase2 = DaysHoursUtils.getLowerCase(fullYourText);
      if (!lowerCase.isEmpty() && !lowerCase2.isEmpty()) {
         int indexOf = lowerCase2.indexOf(lowerCase);

         while (indexOf >= 0) {
            int indexOf2 = lowerCase2.indexOf(lowerCase, indexOf + lowerCase.length());
            OptionalLong empty = KmbtSellerUtils.getEmpty(lowerCase2.substring(indexOf + lowerCase.length(), indexOf2 < 0 ? lowerCase2.length() : indexOf2));
            if (empty.isPresent() && empty.getAsLong() > 0L) {
               arrayList.add(empty.getAsLong());
            }

            indexOf = indexOf2;
         }

         return arrayList;
      } else {
         return arrayList;
      }
   }
}
