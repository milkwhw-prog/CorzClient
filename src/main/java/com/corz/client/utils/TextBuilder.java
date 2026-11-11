package com.corz.client.utils;

import java.util.HashMap;

public final class TextBuilder {
   private String builderText;
   private final HashMap<Long, long[]> builderLongArrayMap = new HashMap<>();

   public boolean containsEntryPair(long builderValue, long builderValue2) {
      long[] builderValues = this.builderLongArrayMap.get(builderValue);
      return builderValues != null && builderValues[0] == builderValue2;
   }

   public static long computeEntryKey(int builderValue, long builderValue2, long builderValue3, int builderValue4) {
      long builderValue5 = 1469598103934665603L;
      long builderValue6 = (builderValue5 ^ builderValue) * 1099511628211L;
      long builderValue7 = (builderValue6 ^ builderValue2) * 1099511628211L;
      long builderValue8 = (builderValue7 ^ builderValue3) * 1099511628211L;
      long builderValue9 = (builderValue8 ^ builderValue4) * 1099511628211L;
      return builderValue9 == 0L ? 1L : builderValue9;
   }

   public String getText() {
      return this.builderText;
   }

   public void updateText() {
      this.builderLongArrayMap.clear();
      this.builderText = "";
   }

   public int getEntryCount(long builderValue) {
      long[] builderValues = this.builderLongArrayMap.get(builderValue);
      return builderValues == null ? 0 : (int)builderValues[1];
   }

   public TextBuilder() {
      this.builderText = "";
   }

   public boolean hasEntryPair(long builderValue, long l7) {
      long[] builderValues = this.builderLongArrayMap.get(builderValue);
      if (builderValues != null && builderValues[0] == l7) {
         builderValues[1]++;
         return true;
      } else {
         return false;
      }
   }

   public void putEntry(long builderValue, long builderValue2, String builderText) {
      this.builderText = builderText == null ? "" : builderText;
      this.builderLongArrayMap.put(builderValue, new long[]{builderValue2, 0L});
   }
}
