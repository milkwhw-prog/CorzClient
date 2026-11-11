package com.corz.client.utils;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SpawnerStorageLayout {
   private static final String PLAIN_ALPHABET = "abcdefghijklmnopqrstuvwyz";
   private static final Pattern NUMBER_PATTERN = Pattern.compile("(?<![\\d.,])(\\d++(?:[.,]\\d++)*+)\\s*(?:([km])(?![a-z])|[x\u00d7](?![a-z])|(?![a-z]))");
   private final int buttonRowY;
   public final int dropperSlotId;
   private final List<SpawnerStorageLayout.Cell> cells;
   public final int boneCount;
   private static final String ᴀʙᴄᴅᴇꜰɢʜꞮᴊᴋʟᴍɴᴏᴘǪƦꜱᴛᴜᴠᴡʏᴢ = "\u1d00\u0299\u1d04\u1d05\u1d07\ua730\u0262\u029c\u026a\u1d0a\u1d0b\u029f\u1d0d\u0274\u1d0f\u1d18\u01eb\u0280\ua731\u1d1b\u1d1c\u1d20\u1d21\u028f\u1d22";
   public final int nextSlotId;

   public static SpawnerStorageLayout.Kind classifyCell(boolean dropperFlag, boolean dropperFlag2, String dropperText) {
      if (dropperFlag) {
         return SpawnerStorageLayout.Kind.EMPTY;
      } else if (dropperFlag2) {
         return SpawnerStorageLayout.Kind.BONE;
      } else {
         String lowerCase = normalizeText(dropperText);
         if (lowerCase.contains("sell")) {
            return SpawnerStorageLayout.Kind.SELL;
         } else if (lowerCase.contains("next")) {
            return SpawnerStorageLayout.Kind.NEXT;
         } else if (lowerCase.contains("prev") || lowerCase.contains("back")) {
            return SpawnerStorageLayout.Kind.PREV;
         } else {
            return lowerCase.contains("drop") && !lowerCase.equals("dropper") ? SpawnerStorageLayout.Kind.DROPPER : SpawnerStorageLayout.Kind.OTHER;
         }
      }
   }

   public static SpawnerStorageLayout fromCells(List<SpawnerStorageLayout.Cell> list) {
      int kindDropperBound = Integer.MIN_VALUE;
      int maxValue = Integer.MAX_VALUE;

      for (SpawnerStorageLayout.Cell cell : list) {
         kindDropperBound = Math.max(kindDropperBound, cell.y());
         maxValue = Math.min(maxValue, cell.y());
      }

      if (!list.isEmpty() && maxValue != kindDropperBound) {
         int kindDropperTotal = 0;
         int kindDropperValue = -1;
         int kindDropperValue2 = -1;

         for (SpawnerStorageLayout.Cell cell2 : list) {
            if (cell2.y() != kindDropperBound) {
               if (cell2.kind() == SpawnerStorageLayout.Kind.BONE) {
                  kindDropperTotal += cell2.count();
               }
            } else if (cell2.kind() == SpawnerStorageLayout.Kind.DROPPER) {
               kindDropperValue = cell2.id();
            } else if (cell2.kind() == SpawnerStorageLayout.Kind.NEXT && kindDropperValue2 < 0) {
               kindDropperValue2 = cell2.id();
            }
         }

         return new SpawnerStorageLayout(list, kindDropperBound, kindDropperValue, kindDropperValue2, kindDropperTotal);
      } else {
         return new SpawnerStorageLayout(list, Integer.MIN_VALUE, -1, -1, 0);
      }
   }

   public static String normalizeText(String lowerCaseText) {
      if (lowerCaseText == null) {
         return "";
      } else {
         StringBuilder stringBuilder = new StringBuilder(lowerCaseText.length());

         for (int i = 0; i < lowerCaseText.length(); i++) {
            char charAt = lowerCaseText.charAt(i);
            int indexOf = "\u1d00\u0299\u1d04\u1d05\u1d07\ua730\u0262\u029c\u026a\u1d0a\u1d0b\u029f\u1d0d\u0274\u1d0f\u1d18\u01eb\u0280\ua731\u1d1b\u1d1c\u1d20\u1d21\u028f\u1d22"
               .indexOf(charAt);
            stringBuilder.append(indexOf >= 0 ? "abcdefghijklmnopqrstuvwyz".charAt(indexOf) : charAt);
         }

         return stringBuilder.toString().toLowerCase(Locale.ROOT);
      }
   }

   private SpawnerStorageLayout(List<SpawnerStorageLayout.Cell> items, int max, int lootButtonValue, int value, int disappearedLootValue) {
      this.cells = items;
      this.buttonRowY = max;
      this.dropperSlotId = lootButtonValue;
      this.nextSlotId = value;
      this.boneCount = disappearedLootValue;
   }

   static long parseAmount(String longText) {
      Matcher matcher = NUMBER_PATTERN.matcher(longText);
      if (!matcher.find()) {
         return -1L;
      } else {
         String group = matcher.group(1);
         String group2 = matcher.group(2);

         try {
            if (group2 == null) {
               return Long.parseLong(group.replace(",", "").replace(".", ""));
            } else {
               int maxLastIndexOf = Math.max(group.lastIndexOf(46), group.lastIndexOf(44));
               String numberText = maxLastIndexOf < 0 ? group : group.substring(0, maxLastIndexOf).replace(",", "").replace(".", "") + "." + group.substring(maxLastIndexOf + 1);
               return Math.round(Double.parseDouble(numberText) * (group2.equals("k") ? 1000 : 1000000));
            }
         } catch (NumberFormatException e) {
            return -1L;
         }
      }
   }

   public long computeSignature(int kindValue) {
      long kindMax = 1L;

      for (SpawnerStorageLayout.Cell cell : this.cells) {
         if (cell.id() != kindValue) {
            long kindValue2 = kindMax * 31L + cell.id();
            kindMax = kindValue2 * 31L + cell.kind().ordinal();
            if (cell.y() != this.buttonRowY) {
               long kindValue3 = kindMax * 31L + cell.count();
               kindMax = kindValue3 * 31L + cell.hash();
            }
         }
      }

      return kindMax;
   }

   public static int parseBoneCount(List<String> list) {
      for (String spawnerStorageText : list) {
         String lowerCase = normalizeText(spawnerStorageText);
         if (lowerCase.contains("bone") && !lowerCase.contains("bone meal") && !lowerCase.contains("bone block")) {
            long parseLong = parseAmount(lowerCase);
            if (parseLong >= 0L) {
               return (int)Math.min(2147483647L, parseLong);
            }
         }
      }

      return -1;
   }

   public record Cell(int id, int x, int y, SpawnerStorageLayout.Kind kind, int count, int hash) {
   }

   public static enum Kind {
      EMPTY,
      BONE,
      DROPPER,
      NEXT,
      PREV,
      SELL,
      OTHER;
   }
}
