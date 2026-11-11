package com.corz.client.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class TextUnmodifiable {
   private final HashMap<Long, LinkedHashMap<Long, Integer>> unmodifiableIntegerMap = new HashMap<>();
   private String unmodifiableText = "";

   public int getKeyCount() {
      return this.unmodifiableIntegerMap.size();
   }

   public void updateText() {
      this.unmodifiableIntegerMap.clear();
      this.unmodifiableText = "";
   }

   public int putText(long unmodifiableValue, long finisherOpenerType, String unmodifiableText) {
      this.unmodifiableText = unmodifiableText == null ? "" : unmodifiableText;
      LinkedHashMap ifAbsent = this.unmodifiableIntegerMap.computeIfAbsent(finisherOpenerType, long2 -> new LinkedHashMap<Long, Integer>(16, 0.75F, false) {
         @Override
         protected boolean removeEldestEntry(Entry<Long, Integer> entry) {
            return this.size() > 512;
         }
      });
      return ((int) (Integer) (ifAbsent.merge(unmodifiableValue, 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2))));
   }

   public String getText() {
      return this.unmodifiableText;
   }

   public Set<Long> getUnmodifiableSet(long unmodifiableValue) {
      Map map = this.unmodifiableIntegerMap.get(unmodifiableValue);
      return map == null ? Set.of() : Collections.unmodifiableSet(map.keySet());
   }

   public int getEntryValue(long unmodifiableValue, long formatMinPop) {
      Map map = this.unmodifiableIntegerMap.get(formatMinPop);
      return map == null ? 0 : ((int) (Integer) (map.getOrDefault(unmodifiableValue, 0)));
   }

   public boolean hasEntry(long unmodifiableValue, long formatMinPop) {
      Map map = this.unmodifiableIntegerMap.get(formatMinPop);
      return map != null && map.containsKey(unmodifiableValue);
   }
}
