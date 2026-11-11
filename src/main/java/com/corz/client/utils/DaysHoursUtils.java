package com.corz.client.utils;

import java.util.List;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.corz.client.utils.KmbtSellerUtils;

public final class DaysHoursUtils {
   private static final Pattern daysHoursPattern = Pattern.compile(
      "(?<![0-9.])([0-9]+)(?:\\.([0-9]+))?\\s*(days?|d|hours?|hrs?|h|minutes?|mins?|m|seconds?|secs?|s)(?![a-z])"
   );
   private static final Pattern compile = Pattern.compile("[\\s\\u00A0\\u2007\\u2009\\u202F]+");

   private DaysHoursUtils() {
   }

   private static long getTime(String daysHoursText) {
      return switch (daysHoursText.charAt(0)) {
         case 'd' -> 86400L;
         case 'h' -> 3600L;
         case 'm' -> 60L;
         default -> 1L;
      };
   }

   public static String getLowerCase(String lowerCaseText) {
      return compile.matcher(KmbtSellerUtils.formatText(lowerCaseText)).replaceAll(" ").trim().toLowerCase(Locale.ROOT);
   }

   public static int getValue(List<String> list, String daysHoursText, boolean daysHoursFlag) {
      String lowerCase = getLowerCase(daysHoursText);
      if (list != null && !lowerCase.isEmpty()) {
         int daysHoursValue = -1;

         for (int i = 0; i < list.size(); i++) {
            String lowerCaseText = (String)list.get(i);
            if (lowerCaseText != null && getLowerCase(lowerCaseText).contains(lowerCase)) {
               if (!daysHoursFlag) {
                  return i;
               }

               daysHoursValue = i;
            }
         }

         return daysHoursValue;
      } else {
         return -1;
      }
   }

   public static OptionalLong getEmpty(String emptyText) {
      if (emptyText == null) {
         return OptionalLong.empty();
      } else {
         Matcher matcher = daysHoursPattern.matcher(getLowerCase(emptyText));
         long emptyValue = 0L;
         boolean emptyFlag = false;

         try {
            for (; matcher.find(); emptyFlag = true) {
               long emptyFactor = getTime(matcher.group(3));
               emptyValue = Math.addExact(emptyValue, Math.multiplyExact(Long.parseLong(matcher.group(1)), emptyFactor));
               if (matcher.group(2) != null) {
                  emptyValue = Math.addExact(emptyValue, Math.round(Double.parseDouble("0." + matcher.group(2)) * emptyFactor));
               }
            }
         } catch (ArithmeticException | NumberFormatException e) {
            return OptionalLong.empty();
         }

         return emptyFlag ? OptionalLong.of(emptyValue) : OptionalLong.empty();
      }
   }

   public static OptionalLong getOptionalLong(List<String> list, String optionalLongText) {
      int index = getValue(list, optionalLongText, false);
      if (index < 0) {
         return OptionalLong.empty();
      } else {
         String lowerCase = getLowerCase(optionalLongText);
         String lowerCase2 = getLowerCase((String)list.get(index));
         String substring = lowerCase2.substring(lowerCase2.indexOf(lowerCase) + lowerCase.length());
         int indexOf = substring.indexOf(58);
         return getEmpty(indexOf >= 0 ? substring.substring(indexOf + 1) : substring);
      }
   }

   public static String formatText(long daysHoursValue) {
      if (daysHoursValue <= 0L) {
         return "0s";
      } else {
         long secondsScaled = daysHoursValue / 3600L;
         long daysHoursLimit = daysHoursValue % 3600L / 60L;
         long daysHoursLimit2 = daysHoursValue % 60L;
         if (secondsScaled > 0L) {
            return daysHoursLimit > 0L ? secondsScaled + "h " + daysHoursLimit + "m" : secondsScaled + "h";
         } else if (daysHoursLimit > 0L) {
            return daysHoursLimit2 > 0L ? daysHoursLimit + "m " + daysHoursLimit2 + "s" : daysHoursLimit + "m";
         } else {
            return daysHoursLimit2 + "s";
         }
      }
   }
}
