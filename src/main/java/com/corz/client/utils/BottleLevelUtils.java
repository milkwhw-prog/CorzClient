package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class BottleLevelUtils {
   public static final Set<String> LEAVES_THICKNESS_SET = Set.of(
      "power",
      "powered",
      "enabled",
      "lit",
      "extended",
      "triggered",
      "locked",
      "inverted",
      "open",
      "in_wall",
      "conditional",
      "north",
      "south",
      "east",
      "west",
      "up",
      "down",
      "middle",
      "bottom",
      "waterlogged",
      "distance",
      "persistent",
      "snowy",
      "stage",
      "age",
      "note",
      "instrument",
      "occupied",
      "signal_fire",
      "berries",
      "bloom",
      "hanging",
      "disarmed",
      "attached",
      "has_bottle_0",
      "has_bottle_1",
      "has_bottle_2",
      "level",
      "honey_level",
      "eggs",
      "hatch",
      "charges",
      "flower_amount",
      "candles",
      "moisture",
      "vine_end",
      "tip",
      "thickness",
      "leaves"
   );

   public static BottleLevelUtils.Result getEmpty(Map<String, String> map, Map<String, String> map2) {
      return getEmpty(map, map2, null);
   }

   public static BottleLevelUtils.Result getEmpty(Map<String, String> map, Map<String, String> map2, Set<String> set) {
      return getThicknessLeaves(map, map2, set, null);
   }

   public static BottleLevelUtils.Result getThicknessLeaves(
      Map<String, String> map, Map<String, String> map2, Set<String> set, Set<String> copyOf
   ) {
      ArrayList arrayList = new ArrayList();

      for (Entry entry : map.entrySet()) {
         String thicknessLeavesText = (String)entry.getKey();
         if ((!LEAVES_THICKNESS_SET.contains(thicknessLeavesText) || copyOf != null && copyOf.contains(thicknessLeavesText))
            && (set == null || !set.contains(thicknessLeavesText))) {
            String wanted2 = (String)map2.get(thicknessLeavesText);
            if (wanted2 == null || !wanted2.equals(entry.getValue())) {
               arrayList.add(thicknessLeavesText + " want=" + (String)entry.getValue() + " got=" + wanted2);
            }
         }
      }

      return new BottleLevelUtils.Result(arrayList.isEmpty(), arrayList);
   }

   private BottleLevelUtils() {
   }

   public static boolean isContains(String containsText) {
      return LEAVES_THICKNESS_SET.contains(containsText);
   }

   public static final class Result {
      public final List<String> stringList;
      public final boolean empty;

      public String getJoin() {
         return String.join(", ", this.stringList);
      }

      Result(boolean empty, List<String> items) {
         this.empty = empty;
         this.stringList = items;
      }
   }
}
