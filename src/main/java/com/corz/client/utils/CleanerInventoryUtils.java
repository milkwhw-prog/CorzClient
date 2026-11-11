package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CleanerInventoryUtils {

   public static List<Integer> getJunkKind(List<CleanerInventoryUtils.Entry> list, boolean junkKindFlag) {
      ArrayList arrayList = new ArrayList();

      for (CleanerInventoryUtils.Entry entry : list) {
         if (entry.junk() && canSlotKeep(entry)) {
            handle(arrayList, entry.slot());
         }
      }

      if (junkKindFlag) {
         LinkedHashMap linkedHashMap = new LinkedHashMap();

         for (CleanerInventoryUtils.Entry groupParseData : list) {
            if (groupParseData.kind() >= 0 && !groupParseData.junk()) {
               ((java.util.List) linkedHashMap.computeIfAbsent(groupParseData.kind(), obj -> new ArrayList<>())).add(groupParseData);
            }
         }

         for (List list2 : (Iterable<java.util.List>) (linkedHashMap.values())) {
            list2.sort(Comparator.comparingDouble(CleanerInventoryUtils.Entry::score).reversed());
            ArrayList arrayList2 = new ArrayList(list2.size());

            for (CleanerInventoryUtils.Entry entry3 : (Iterable<com.corz.client.utils.CleanerInventoryUtils.Entry>) (list2)) {
               if (canSlotKeep(entry3) && canDurability(entry3, arrayList2)) {
                  handle(arrayList, entry3.slot());
               } else {
                  arrayList2.add(entry3);
               }
            }
         }
      }

      arrayList.sort(Comparator.comparingInt(CleanerInventoryUtils::getValue));
      return arrayList;
   }

   private static boolean canSlotKeep(CleanerInventoryUtils.Entry entry) {
      return !entry.keep() && entry.slot() >= 0 && entry.slot() < 36;
   }

   private static void handle(List<Integer> list, int cleanerInventoryValue) {
      if (!list.contains(cleanerInventoryValue)) {
         list.add(cleanerInventoryValue);
      }
   }

   public static int getValue(int cleanerInventoryValue) {
      return cleanerInventoryValue < 9 ? 100 + cleanerInventoryValue : cleanerInventoryValue;
   }

   public static boolean canEnchantsCurses(CleanerInventoryUtils.Entry entry, CleanerInventoryUtils.Entry entry2) {
      if (entry.base() < entry2.base() - 1.0E-6) {
         return false;
      } else {
         for (Map.Entry entryItem : entry2.enchants().entrySet()) {
            if (entry.enchants().getOrDefault(entryItem.getKey(), 0) < (Integer)entryItem.getValue()) {
               return false;
            }
         }

         for (Map.Entry entry4 : entry.curses().entrySet()) {
            if (entry2.curses().getOrDefault(entry4.getKey(), 0) < (Integer)entry4.getValue()) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean canDurability(CleanerInventoryUtils.Entry entry, List<CleanerInventoryUtils.Entry> list) {
      for (CleanerInventoryUtils.Entry entry2 : list) {
         if (entry2.durability() >= 0.1 && canEnchantsCurses(entry2, entry)) {
            return true;
         }
      }

      return false;
   }

   private CleanerInventoryUtils() {
   }

   public record Entry(
      int slot, int kind, double score, boolean keep, boolean junk, double durability, double base, Map<String, Integer> enchants, Map<String, Integer> curses
   ) {
      public Entry(int kind, int junk, double slot, boolean keep, boolean durability, double score) {
         this(kind, junk, slot, keep, durability, score, slot, Map.of(), Map.of());
      }
   }
}
