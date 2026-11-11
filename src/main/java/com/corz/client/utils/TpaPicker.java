package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class TpaPicker {
   public static final Pattern compile = Pattern.compile("\\.?[A-Za-z0-9_]{3,16}");

   public static boolean canCompile(String compileText) {
      return compileText != null && compile.matcher(compileText).matches();
   }

   private TpaPicker() {
   }

   public static String formatText(Collection<String> collection, String tpaPickerText, Predicate<String> predicate, Map<String, Long> map, long tpaPickerValue, Random random) {
      List list = getItems(collection, tpaPickerText, predicate, map, tpaPickerValue);
      return list.isEmpty() ? null : (String)list.get(random == null ? 0 : random.nextInt(list.size()));
   }

   public static List<String> getItems(Collection<String> collection, String tpaPickerText, Predicate<String> predicate, Map<String, Long> map, long tpaPickerLimit) {
      ArrayList arrayList = new ArrayList();
      if (collection == null) {
         return arrayList;
      } else {
         HashSet hashSet = new HashSet();
         String lowerCase = getLowerCase(tpaPickerText);

         for (String compileText : collection) {
            if (canCompile(compileText)) {
               String blockPos = getLowerCase(compileText);
               if (!blockPos.equals(lowerCase) && hashSet.add(blockPos) && (predicate == null || !predicate.test(compileText))) {
                  Long long2 = map == null ? null : (Long)map.get(blockPos);
                  if (long2 == null || long2 <= tpaPickerLimit) {
                     arrayList.add(compileText);
                  }
               }
            }
         }

         return arrayList;
      }
   }

   public static String getLowerCase(String lowerCaseText) {
      return lowerCaseText == null ? "" : lowerCaseText.toLowerCase(Locale.ROOT);
   }

   public static void handle(Map<String, Long> map, long tpaPickerLimit, int tpaPickerBound) {
      if (map != null) {
         map.values().removeIf(nowx -> nowx == null || nowx <= tpaPickerLimit);

         while (map.size() > Math.max(0, tpaPickerBound)) {
            String tpaPickerText = null;
            long maxValue = Long.MAX_VALUE;

            for (Entry entry : map.entrySet()) {
               if (tpaPickerText == null || (Long)entry.getValue() < maxValue) {
                  maxValue = (Long)entry.getValue();
                  tpaPickerText = (String)entry.getKey();
               }
            }

            map.remove(tpaPickerText);
         }
      }
   }
}
