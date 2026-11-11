package com.corz.client.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;
import com.corz.client.utils.YouUtils;

public final class ProtectTexts {
   private final int[] stringTable;
   public static final ProtectTexts INSTANCE = new ProtectTexts(new LinkedHashMap<>());
   private final String[] protectTexts;
   private final String[] protectedValueTexts;

   public static boolean canFlag(String protectText) {
      if (protectText == null) {
         return false;
      } else {
         int nameLength = protectText.length();
         if (nameLength >= 3 && nameLength <= 16) {
            for (int i = 0; i < nameLength; i++) {
               if (!YouUtils.canFlag(protectText.charAt(i))) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean isFlag() {
      return this.stringTable.length == 0;
   }

   public String getString(String stringText) {
      if (stringText != null && this.stringTable.length != 0) {
         int textLength = stringText.length();
         if (textLength < 3) {
            return stringText;
         } else {
            StringBuilder stringBuilder = null;
            int stringValue = 0;
            int index = 0;

            while (index < textLength) {
               char charAt = stringText.charAt(index);
               if (charAt == 167) {
                  index += 2;
               } else if (!YouUtils.canFlag(charAt)) {
                  index++;
               } else {
                  int protectValue = index;

                  while (index < textLength && YouUtils.canFlag(stringText.charAt(index))) {
                     index++;
                  }

                  int deltaVar = index - protectValue;
                  if (deltaVar >= 3 && deltaVar <= 16) {
                     String formatTexts = this.formatTexts(stringText, protectValue, deltaVar);
                     if (formatTexts != null) {
                        if (stringBuilder == null) {
                           stringBuilder = new StringBuilder(textLength + 16);
                        }

                        stringBuilder.append(stringText, stringValue, protectValue).append(formatTexts);
                        stringValue = index;
                     }
                  }
               }
            }

            return stringBuilder == null ? stringText : stringBuilder.append(stringText, stringValue, textLength).toString();
         }
      } else {
         return stringText;
      }
   }

   private ProtectTexts(LinkedHashMap<String, String> linkedHashMap) {
      int mapSize = linkedHashMap.size();
      long[] longs = new long[mapSize];
      String[] strings = new String[mapSize];
      String[] strings2 = new String[mapSize];
      int index = 0;

      for (Entry entry : linkedHashMap.entrySet()) {
         strings[index] = (String)entry.getKey();
         strings2[index] = (String)entry.getValue();
         longs[index] = (long)strings[index].hashCode() << 32 | index & 4294967295L;
         index++;
      }

      Arrays.sort(longs);
      this.stringTable = new int[mapSize];
      this.protectTexts = new String[mapSize];
      this.protectedValueTexts = new String[mapSize];

      for (int i = 0; i < mapSize; i++) {
         int index2 = (int)longs[i];
         this.stringTable[i] = (int)(longs[i] >> 32);
         this.protectTexts[i] = strings[index2];
         this.protectedValueTexts[i] = strings2[index2];
      }
   }

   public int getValue() {
      return this.stringTable.length;
   }

   private String formatTexts(String protectText, int protectValue, int deltaVar) {
      int protectValue3 = 0;
      int protectValue2 = protectValue;

      for (int i = protectValue + deltaVar; protectValue2 < i; protectValue2++) {
         char charAt = protectText.charAt(protectValue2);
         protectValue3 = 31 * protectValue3 + (charAt >= 'A' && charAt <= 'Z' ? (char)(charAt + ' ') : charAt);
      }

      int binarySearch = Arrays.binarySearch(this.stringTable, protectValue3);
      if (binarySearch < 0) {
         return null;
      } else {
         while (binarySearch > 0 && this.stringTable[binarySearch - 1] == protectValue3) {
            binarySearch--;
         }

         while (binarySearch < this.stringTable.length && this.stringTable[binarySearch] == protectValue3) {
            String protectText2 = this.protectTexts[binarySearch];
            if (protectText2.length() == deltaVar && protectText.regionMatches(true, protectValue, protectText2, 0, deltaVar)) {
               return this.protectedValueTexts[binarySearch];
            }

            binarySearch++;
         }

         return null;
      }
   }

   public String formatText(String protectText) {
      return this.stringTable.length != 0 && canFlag(protectText) ? this.formatTexts(protectText, 0, protectText.length()) : null;
   }

   public static final class Builder {
      private final LinkedHashMap<String, String> builderStringMap = new LinkedHashMap<>();

      public ProtectTexts.Builder put(String builderText, String moduleView) {
         if (moduleView != null && ProtectTexts.canFlag(builderText)) {
            this.builderStringMap.putIfAbsent(builderText.toLowerCase(Locale.ROOT), moduleView);
            return this;
         } else {
            return this;
         }
      }

      public ProtectTexts build() {
         return this.builderStringMap.isEmpty() ? ProtectTexts.INSTANCE : new ProtectTexts(this.builderStringMap);
      }

      public boolean has(String builderText) {
         return builderText != null && this.builderStringMap.containsKey(builderText.toLowerCase(Locale.ROOT));
      }
   }
}
