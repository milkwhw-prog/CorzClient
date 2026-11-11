package com.corz.client.utils;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ToolListingParser {
   private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9][0-9,]*(?:\\.([0-9]+))?");
   private static final long[] SUFFIX_MULTIPLIERS = new long[]{1L, 1000L, 1000000L, 1000000000L, 1000000000000L};

   public static boolean hasMatchingToolLore(ToolListingParser.Lines lines, ShardAxeType shardAxeType, String loreText) {
      if (lines != null && shardAxeType != null && lines.lore() != null) {
         return ShardAxeType.getEmpty(lines.name()).orElse(null) == shardAxeType && isToolOfType(lines, shardAxeType)
            ? DaysHoursUtils.getValue(lines.lore(), loreText, false) >= 0
            : false;
      } else {
         return false;
      }
   }

   public static Optional<ElseMaxData> parseToolListing(ToolListingParser.Lines lines, int loreDurabilityValue, int i, String loreDurabilityText, String lowerCaseText) {
      if (lines != null && lines.lore() != null) {
         String lowerCase = DaysHoursUtils.getLowerCase(loreDurabilityText);
         if (!lowerCase.isEmpty() && !DaysHoursUtils.getLowerCase(lowerCaseText).isEmpty()) {
            ShardAxeType orElse = ShardAxeType.getEmpty(lines.name()).orElse(null);
            if (orElse != null && isToolOfType(lines, orElse)) {
               int loreDurabilityValue3 = DaysHoursUtils.getValue(lines.lore(), lowerCaseText, false);
               OptionalLong optionalLong = DaysHoursUtils.getOptionalLong(lines.lore(), lowerCaseText);
               if (!optionalLong.isEmpty() && optionalLong.getAsLong() > 0L) {
                  int index = DaysHoursUtils.getValue(lines.lore(), loreDurabilityText, true);
                  if (index >= 0 && index != loreDurabilityValue3) {
                     String lowerCase2 = DaysHoursUtils.getLowerCase(lines.lore().get(index));
                     if (lowerCase2.contains(DaysHoursUtils.getLowerCase(orElse.getShortLabel()))) {
                        return Optional.empty();
                     } else {
                        String substring = lowerCase2.substring(lowerCase2.indexOf(lowerCase) + lowerCase.length());
                        OptionalLong empty = KmbtSellerUtils.getEmpty(substring);
                        if (!empty.isEmpty() && empty.getAsLong() > 0L) {
                           double clampDurabilityFraction4 = clampDurabilityFraction(lines.durability());
                           String trim = KmbtSellerUtils.formatText(lines.name()).trim();
                           String fingerprint2 = buildFingerprint(orElse, empty.getAsLong(), lines.lore(), loreDurabilityValue3, index, clampDurabilityFraction4);
                           return Optional.of(
                              new ElseMaxData(
                                 orElse,
                                 loreDurabilityValue,
                                 i,
                                 trim,
                                 empty.getAsLong(),
                                 roundUpAbbreviatedAmount(substring, empty.getAsLong()),
                                 optionalLong.getAsLong(),
                                 clampDurabilityFraction4,
                                 fingerprint2
                              )
                           );
                        } else {
                           return Optional.empty();
                        }
                     }
                  } else {
                     return Optional.empty();
                  }
               } else {
                  return Optional.empty();
               }
            } else {
               return Optional.empty();
            }
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   private static boolean isToolOfType(ToolListingParser.Lines lines, ShardAxeType shardAxeType) {
      return lines.tool() == shardAxeType.getShovelPickaxeType() && DaysHoursUtils.getValue(lines.lore(), shardAxeType.getShortLabel(), false) >= 0;
   }

   public static boolean isToolWrongForType(ToolListingParser.Lines lines, ShardAxeType shardAxeType) {
      return lines != null && shardAxeType != null && lines.lore() != null ? ShardAxeType.getEmpty(lines.name()).orElse(null) == shardAxeType && !isToolOfType(lines, shardAxeType) : false;
   }

   public static boolean areListingsWithinSeconds(ElseMaxData elseMaxData, ElseMaxData orElse, long secondsLeftBound) {
      if (elseMaxData != null && orElse != null && elseMaxData.fingerprint() != null && elseMaxData.fingerprint().equals(orElse.fingerprint())) {
         long secondsLeftLimit = Math.max(elseMaxData.secondsLeft(), orElse.secondsLeft()) - Math.min(elseMaxData.secondsLeft(), orElse.secondsLeft());
         return secondsLeftLimit >= 0L && secondsLeftLimit <= Math.max(0L, secondsLeftBound);
      } else {
         return false;
      }
   }

   static long roundUpAbbreviatedAmount(String compileText, long timesCompile) {
      Matcher matcher = NUMBER_PATTERN.matcher(compileText == null ? "" : compileText);
      if (timesCompile > 0L && matcher.find()) {
         double compileFactor;
         try {
            compileFactor = Double.parseDouble(matcher.group().replace(",", ""));
         } catch (NumberFormatException e) {
            return timesCompile;
         }

         int compileBound = matcher.group(1) == null ? 0 : matcher.group(1).length();

         for (int i = SUFFIX_MULTIPLIERS.length - 1; i >= 1; i--) {
            if (Math.round(compileFactor * SUFFIX_MULTIPLIERS[i]) == timesCompile) {
               long compileLimit = SUFFIX_MULTIPLIERS[i];

               for (int j = Math.max(1, compileBound); j > 0 && compileLimit >= 10L; j--) {
                  compileLimit /= 10L;
               }

               return timesCompile > Long.MAX_VALUE - compileLimit ? Long.MAX_VALUE : timesCompile + compileLimit - 1L;
            }
         }

         return timesCompile;
      } else {
         return timesCompile;
      }
   }

   private ToolListingParser() {
   }

   private static String buildFingerprint(ShardAxeType shardAxeType, long compileValue, List<String> list, int compileValue2, int compileValue3, double compileValue4) {
      StringJoiner stringJoiner = new StringJoiner(",");

      for (int i = 0; i < list.size(); i++) {
         if (i != compileValue2 && i != compileValue3 && !DaysHoursUtils.getEmpty(DaysHoursUtils.getLowerCase((String)list.get(i))).isPresent()) {
            String formatRoot = ShardAxeType.formatRoot((String)list.get(i));
            if (!formatRoot.isEmpty()) {
               stringJoiner.add(formatRoot);
            }
         }
      }

      return shardAxeType.name() + '|' + compileValue + '|' + stringJoiner + '|' + Math.round(compileValue4 * 1000.0);
   }

   private static double clampDurabilityFraction(double compileBound) {
      return Double.isNaN(compileBound) ? 0.0 : Math.max(0.0, Math.min(1.0, compileBound));
   }

   public record Lines(String name, List<String> lore, ShardAxeType.Tool tool, double durability) {
   }
}
