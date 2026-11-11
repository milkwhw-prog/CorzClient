package com.corz.client.utils;

import java.util.Locale;

public final class YouUtils {
   public static final String YOU = "You";
   private static volatile YouUtils.Needle caseLowerDataLowerCase;

   private static YouUtils.Needle getLowerCase(String lowerCaseText) {
      YouUtils.Needle caseLowerDataLowerCase22 = caseLowerDataLowerCase;
      if (caseLowerDataLowerCase22 != null && caseLowerDataLowerCase22.self.equals(lowerCaseText)) {
         return caseLowerDataLowerCase22;
      } else {
         YouUtils.Needle caseLowerData = new YouUtils.Needle(lowerCaseText, lowerCaseText.toLowerCase(Locale.ROOT), canFlag(lowerCaseText));
         caseLowerDataLowerCase = caseLowerData;
         return caseLowerData;
      }
   }

   public static boolean canFlag(String youText, String username) {
      return youText != null && username != null && !username.isEmpty() && youText.equalsIgnoreCase(username);
   }

   public static String formatAsciiLower(String formatAsciiLower, String asciiLowerText, String builderText) {
      if (formatAsciiLower != null && asciiLowerText != null && !asciiLowerText.isEmpty() && builderText != null) {
         int aliasLength = asciiLowerText.length();
         if (formatAsciiLower.length() < aliasLength) {
            return formatAsciiLower;
         } else {
            YouUtils.Needle lowerCase = getLowerCase(asciiLowerText);
            if (lowerCase.ascii && canFlag(formatAsciiLower)) {
               return formatText(formatAsciiLower, asciiLowerText, builderText);
            } else {
               String lowerCase2 = formatAsciiLower.toLowerCase(Locale.ROOT);
               String lower = lowerCase.lower;
               int indexOf = lowerCase2.indexOf(lower);
               if (indexOf < 0) {
                  return formatAsciiLower;
               } else {
                  StringBuilder stringBuilder = null;

                  int asciiLowerValue;
                  for (asciiLowerValue = 0; indexOf >= 0; indexOf = lowerCase2.indexOf(lower, indexOf + aliasLength)) {
                     boolean asciiLowerFlag = indexOf == 0 || !canFlag(formatAsciiLower.charAt(indexOf - 1));
                     boolean asciiLowerFlag2 = indexOf + aliasLength >= formatAsciiLower.length() || !canFlag(formatAsciiLower.charAt(indexOf + aliasLength));
                     if (asciiLowerFlag && asciiLowerFlag2) {
                        if (stringBuilder == null) {
                           stringBuilder = new StringBuilder(formatAsciiLower.length());
                        }

                        stringBuilder.append(formatAsciiLower, asciiLowerValue, indexOf).append(builderText);
                        asciiLowerValue = indexOf + aliasLength;
                     }
                  }

                  return stringBuilder == null ? formatAsciiLower : stringBuilder.append(formatAsciiLower, asciiLowerValue, formatAsciiLower.length()).toString();
               }
            }
         }
      } else {
         return formatAsciiLower;
      }
   }

   private static int getValue(String youText, String youText2, int youValue) {
      int selfLength = youText2.length();
      int youLimit = youText.length() - selfLength;
      char charAt = getCharAt(youText2.charAt(0));

      for (int i = youValue; i <= youLimit; i++) {
         if (getCharAt(youText.charAt(i)) == charAt) {
            int index = 1;

            while (index < selfLength && getCharAt(youText.charAt(i + index)) == getCharAt(youText2.charAt(index))) {
               index++;
            }

            if (index == selfLength) {
               return i;
            }
         }
      }

      return -1;
   }

   private static boolean canFlag(String youText) {
      int index = 0;

      for (int i = youText.length(); index < i; index++) {
         if (youText.charAt(index) >= 128) {
            return false;
         }
      }

      return true;
   }

   private YouUtils() {
   }

   public static boolean canFlag(char youLimit) {
      return youLimit >= 'a' && youLimit <= 'z' || youLimit >= 'A' && youLimit <= 'Z' || youLimit >= '0' && youLimit <= '9' || youLimit == '_';
   }

   private static String formatText(String formatText, String youText, String builderText) {
      int selfLength = youText.length();
      int youLimit = getValue(formatText, youText, 0);
      if (youLimit < 0) {
         return formatText;
      } else {
         StringBuilder stringBuilder = null;

         int youValue;
         for (youValue = 0; youLimit >= 0; youLimit = getValue(formatText, youText, youLimit + selfLength)) {
            boolean youFlag = youLimit == 0 || !canFlag(formatText.charAt(youLimit - 1));
            boolean youFlag2 = youLimit + selfLength >= formatText.length() || !canFlag(formatText.charAt(youLimit + selfLength));
            if (youFlag && youFlag2) {
               if (stringBuilder == null) {
                  stringBuilder = new StringBuilder(formatText.length());
               }

               stringBuilder.append(formatText, youValue, youLimit).append(builderText);
               youValue = youLimit + selfLength;
            }
         }

         return stringBuilder == null ? formatText : stringBuilder.append(formatText, youValue, formatText.length()).toString();
      }
   }

   public static String formatTextForString(String youText, String username, String youText3) {
      return canFlag(youText, username) ? youText3 : youText;
   }

   private static char getCharAt(char charAtLimit) {
      return charAtLimit >= 'A' && charAtLimit <= 'Z' ? (char)(charAtLimit + ' ') : charAtLimit;
   }

   private record Needle(String self, String lower, boolean ascii) {
   }
}
