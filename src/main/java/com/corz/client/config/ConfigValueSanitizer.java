package com.corz.client.config;

import java.util.List;
import java.util.Locale;

public final class ConfigValueSanitizer {

   public static int sanitizeKeyCode(int jsonExtraValue) {
      return isValidKeyCode(jsonExtraValue) ? jsonExtraValue : -1;
   }

   public static String toSlug(String substringText) {
      if (substringText == null) {
         return "";
      } else {
         String lowerCase = substringText.trim().toLowerCase(Locale.ROOT);
         StringBuilder stringBuilder = new StringBuilder(lowerCase.length());
         boolean substringFlag = false;

         for (int i = 0; i < lowerCase.length(); i++) {
            char charAt = lowerCase.charAt(i);
            if ((charAt < 'a' || charAt > 'z') && (charAt < '0' || charAt > '9')) {
               if (!substringFlag && stringBuilder.length() > 0) {
                  stringBuilder.append('-');
                  substringFlag = true;
               }
            } else {
               stringBuilder.append(charAt);
               substringFlag = false;
            }
         }

         int varLength = stringBuilder.length();

         while (varLength > 0 && stringBuilder.charAt(varLength - 1) == '-') {
            varLength--;
         }

         return stringBuilder.substring(0, varLength);
      }
   }

   public static double clampToRange(double jsonExtraLimit, double jsonExtraLimit2, double jsonExtraValue) {
      if (Double.isNaN(jsonExtraLimit)) {
         return jsonExtraLimit2;
      } else if (jsonExtraValue < jsonExtraLimit2) {
         return jsonExtraLimit2;
      } else {
         return jsonExtraLimit < jsonExtraLimit2 ? jsonExtraLimit2 : (jsonExtraLimit > jsonExtraValue ? jsonExtraValue : jsonExtraLimit);
      }
   }

   public static boolean isValidKeyCode(int jsonExtraLimit) {
      if (jsonExtraLimit == -1) {
         return true;
      } else {
         return jsonExtraLimit >= 0 && jsonExtraLimit <= 400 ? true : jsonExtraLimit >= 1000 && jsonExtraLimit <= 1007;
      }
   }

   public static boolean isMouseButtonCode(int jsonExtraLimit) {
      return jsonExtraLimit >= 1000 && jsonExtraLimit <= 1007;
   }

   public static String truncate(String jsonExtraText, int end) {
      if (jsonExtraText == null) {
         return "";
      } else {
         return jsonExtraText.length() <= end ? jsonExtraText : jsonExtraText.substring(0, end);
      }
   }

   public static boolean isInRange(double jsonExtraLimit, double jsonExtraValue, double jsonExtraLimit2) {
      return !Double.isNaN(jsonExtraLimit) && jsonExtraLimit >= jsonExtraValue && jsonExtraLimit <= jsonExtraLimit2;
   }

   private ConfigValueSanitizer() {
   }

   public static boolean isKeyboardKeyCode(int jsonExtraValue) {
      return isValidKeyCode(jsonExtraValue) && !isMouseButtonCode(jsonExtraValue);
   }

   public static String findMatchIgnoreCase(String jsonExtraText, List<String> list, String formatText) {
      if (jsonExtraText != null && list != null) {
         for (String formatText2 : list) {
            if (formatText2 != null && formatText2.equalsIgnoreCase(jsonExtraText)) {
               return formatText2;
            }
         }
      }

      return formatText;
   }
}
