package com.corz.client.utils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class KmbtSellerUtils {
   private static final Pattern kmbtSellerPattern = Pattern.compile("([0-9][0-9,]*(?:\\.[0-9]+)?)\\s*([kmbtKMBT])?");
   private static final Pattern pattern = Pattern.compile("[.,'\\s\\u00A0\\u2009\\u202F][0-9]{3}");
   private static final Pattern compile = Pattern.compile("(?:thousand|mil|million|mio|bil|billion|tril|trillion)s?(?![A-Za-z])", 2);
   private static final String[] uuidRankTexts = new String[]{
      "amount", "qty", "quantity", "count", "stock", "left", "remaining", "seller", "time", "expire", "expires", "rank", "id", "uuid"
   };
   private static final Pattern EMPTY_TEXT_PATTERN = Pattern.compile(
      "^\\s*you\\s+bought\\s+(\\d+)\\s+(.+)\\s+for\\s+\\$\\s*((?>[0-9][0-9,]*(?:\\.[0-9]+)?)[kmbtKMBT]?)(?![A-Za-z0-9]|[.,'\\s\\u00A0\\u2009\\u202F][0-9]{3})",
      2
   );

   public static OptionalLong getSellerPrice(List<String> list, String sellerPriceText) {
      if (list == null) {
         return OptionalLong.empty();
      } else {
         String sellerPriceText2 = sellerPriceText == null ? "" : sellerPriceText.toLowerCase(Locale.ROOT).trim();
         OptionalLong empty = OptionalLong.empty();
         OptionalLong empty2 = OptionalLong.empty();
         OptionalLong empty3 = OptionalLong.empty();

         for (String kmbtSellerText : list) {
            if (kmbtSellerText != null) {
               String emptyText = formatText(kmbtSellerText);
               String lowerCase = emptyText.toLowerCase(Locale.ROOT);
               OptionalLong empty4 = getEmpty(emptyText);
               if (!empty4.isEmpty()) {
                  if (!sellerPriceText2.isEmpty() && lowerCase.contains(sellerPriceText2) && empty.isEmpty()) {
                     empty = empty4;
                  }

                  if (lowerCase.contains("price") && !lowerCase.contains("seller") && empty2.isEmpty()) {
                     empty2 = empty4;
                  }

                  if (empty3.isEmpty() && !canRankUuid(lowerCase)) {
                     empty3 = empty4;
                  }
               }
            }
         }

         if (empty.isPresent()) {
            return empty;
         } else {
            return empty2.isPresent() ? empty2 : empty3;
         }
      }
   }

   public static boolean canPriceRoot(List<String> list, String priceRootText) {
      if (list == null) {
         return false;
      } else {
         String priceRootText2 = priceRootText == null ? "" : priceRootText.toLowerCase(Locale.ROOT).trim();

         for (String kmbtSellerText : list) {
            if (kmbtSellerText != null) {
               String lowerCase = formatText(kmbtSellerText).toLowerCase(Locale.ROOT);
               if (lowerCase.contains("price") || !priceRootText2.isEmpty() && lowerCase.contains(priceRootText2)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static String formatRoot(String rootText) {
      StringBuilder stringBuilder = new StringBuilder();

      for (char ch : formatText(rootText).toLowerCase(Locale.ROOT).toCharArray()) {
         if (Character.isLetterOrDigit(ch)) {
            stringBuilder.append(ch);
         }
      }

      return stringBuilder.toString();
   }

   public static OptionalLong getRoot(List<String> list, String rootText) {
      if (list == null) {
         return OptionalLong.empty();
      } else {
         String rootText2 = rootText == null ? "" : rootText.toLowerCase(Locale.ROOT).trim();
         OptionalLong empty = OptionalLong.empty();
         if (!rootText2.isEmpty()) {
            for (String kmbtSellerText : list) {
               if (kmbtSellerText != null) {
                  String lowerCase = formatText(kmbtSellerText).toLowerCase(Locale.ROOT);
                  int indexOf = lowerCase.indexOf(rootText2);
                  if (indexOf >= 0) {
                     String substring = lowerCase.substring(indexOf + rootText2.length());
                     OptionalLong empty2 = getEmpty(substring);
                     if (empty2.isEmpty()) {
                        if (substring.chars().anyMatch(Character::isDigit)) {
                           return OptionalLong.empty();
                        }

                        empty2 = getEmpty(lowerCase);
                     }

                     if (empty2.isPresent() && (empty.isEmpty() || empty2.getAsLong() > empty.getAsLong())) {
                        empty = empty2;
                     }
                  }
               }
            }
         }

         return empty.isPresent() ? empty : getSellerPrice(list, rootText);
      }
   }

   public static OptionalLong getEmpty(String emptyText) {
      String compileCommandText = formatText(emptyText);
      Matcher matcher = kmbtSellerPattern.matcher(compileCommandText);
      if (!matcher.find()) {
         return OptionalLong.empty();
      } else {
         int end = matcher.end(1);
         if (end < compileCommandText.length() && Character.isLetter(compileCommandText.charAt(end)) && "kmbtKMBT".indexOf(compileCommandText.charAt(end)) < 0) {
            return OptionalLong.empty();
         } else if (pattern.matcher(compileCommandText).region(end, compileCommandText.length()).lookingAt()) {
            return OptionalLong.empty();
         } else {
            String replace = matcher.group(1).replace(",", "");
            String group = matcher.group(2);
            if (group != null && matcher.start(2) > end && matcher.end(2) < compileCommandText.length() && Character.isLetter(compileCommandText.charAt(matcher.end(2)))) {
               if (compile.matcher(compileCommandText).region(matcher.start(2), compileCommandText.length()).lookingAt()) {
                  return OptionalLong.empty();
               }

               group = null;
            }

            if (group == null && matcher.group(1).matches("[0-9]{1,3}\\.[0-9]{3}")) {
               return OptionalLong.empty();
            } else {
               double emptyFactor;
               try {
                  emptyFactor = Double.parseDouble(replace);
               } catch (NumberFormatException e) {
                  return OptionalLong.empty();
               }
               long emptyFactor2 = switch (group != null && !group.isEmpty() ? Character.toLowerCase(group.charAt(0)) : ' ') {
                  case 'b' -> 1000000000L;
                  case 'k' -> 1000L;
                  case 'm' -> 1000000L;
                  case 't' -> 1000000000000L;
                  default -> 1L;
               };
               long emptyLimit = Math.round(emptyFactor * emptyFactor2);
               return emptyLimit >= 0L ? OptionalLong.of(emptyLimit) : OptionalLong.empty();
            }
         }
      }
   }

   private static boolean canRankUuid(String rankUuidText) {
      for (String uuidRankTextsItem : uuidRankTexts) {
         if (rankUuidText.contains(uuidRankTextsItem)) {
            return true;
         }
      }

      return false;
   }

   private KmbtSellerUtils() {
   }

   public static boolean canFlag(String kmbtSellerText, String rootText) {
      String lowerCase = formatRoot(kmbtSellerText);
      String formatRoot3 = formatRoot(rootText);
      return !lowerCase.isEmpty() && !formatRoot3.isEmpty() && (lowerCase.contains(formatRoot3) || formatRoot3.contains(lowerCase));
   }

   public static String formatText(String kmbtSellerText) {
      if (kmbtSellerText == null) {
         return "";
      } else {
         StringBuilder stringBuilder = new StringBuilder(kmbtSellerText.length());

         for (int i = 0; i < kmbtSellerText.length(); i++) {
            char charAt = kmbtSellerText.charAt(i);
            if (charAt == 167 && i + 1 < kmbtSellerText.length()) {
               i++;
            } else {
               stringBuilder.append(charAt);
            }
         }

         return stringBuilder.toString();
      }
   }

   public static Optional<KmbtSellerUtils.ChatPurchase> getEmptyForString(String emptyText) {
      Matcher matcher = EMPTY_TEXT_PATTERN.matcher(formatText(emptyText));
      if (!matcher.find()) {
         return Optional.empty();
      } else {
         int emptyLimit;
         try {
            emptyLimit = Integer.parseInt(matcher.group(1));
         } catch (NumberFormatException e) {
            return Optional.empty();
         }

         OptionalLong empty = getEmpty(matcher.group(3));
         return emptyLimit > 0 && !empty.isEmpty() ? Optional.of(new KmbtSellerUtils.ChatPurchase(emptyLimit, empty.getAsLong(), matcher.group(2).trim())) : Optional.empty();
      }
   }

   public record ChatPurchase(int count, long coins, String item) {
   }
}
