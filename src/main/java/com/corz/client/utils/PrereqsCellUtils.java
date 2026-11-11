package com.corz.client.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class PrereqsCellUtils {

   private PrereqsCellUtils() {
   }

   public static PrereqsCellUtils.Plan getCellPrereqs(List<PrereqsCellUtils.Item> list) {
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (PrereqsCellUtils.Item item : list) {
         linkedHashMap.put(item.cell(), item);
      }

      LinkedHashMap linkedHashMap2 = new LinkedHashMap();

      for (PrereqsCellUtils.Item item2 : list) {
         int cellPrereqsCounter = 0;

         for (long cellPrereqsValue : item2.schematicPrereqs()) {
            if (linkedHashMap.containsKey(cellPrereqsValue) && cellPrereqsValue != item2.cell()) {
               cellPrereqsCounter++;
            }
         }

         linkedHashMap2.put(item2.cell(), cellPrereqsCounter);
      }

      ArrayList arrayList = new ArrayList();
      boolean cellPrereqsFlag = true;

      while (cellPrereqsFlag) {
         cellPrereqsFlag = false;

         for (PrereqsCellUtils.Item totalPriceData : list) {
            if (linkedHashMap2.containsKey(totalPriceData.cell()) && (Integer)linkedHashMap2.get(totalPriceData.cell()) == 0) {
               arrayList.add(totalPriceData);
               linkedHashMap2.remove(totalPriceData.cell());

               for (PrereqsCellUtils.Item item4 : list) {
                  if (linkedHashMap2.containsKey(item4.cell()) && item4.schematicPrereqs().contains(totalPriceData.cell())) {
                     linkedHashMap2.merge(item4.cell(), -1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
                  }
               }

               cellPrereqsFlag = true;
            }
         }
      }

      ArrayList arrayList2 = new ArrayList();

      for (PrereqsCellUtils.Item item5 : list) {
         if (linkedHashMap2.containsKey(item5.cell())) {
            arrayList2.add(item5);
         }
      }

      return new PrereqsCellUtils.Plan(List.copyOf(arrayList), List.copyOf(arrayList2));
   }

   public record Item(long cell, String kind, List<Long> schematicPrereqs, List<Long> tempPrereqs) {
   }

   public record Plan(List<PrereqsCellUtils.Item> ordered, List<PrereqsCellUtils.Item> unsequenced) {
      public boolean isEmpty() {
         return this.unsequenced.isEmpty();
      }
   }
}
