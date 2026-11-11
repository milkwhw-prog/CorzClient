package com.corz.client.utils;

public final class IconsClearUtilsFormat {

   private IconsClearUtilsFormat() {
   }

   public static String formatText(String formatText, boolean iconsClearFlag, boolean substringFlag) {
      if (iconsClearFlag) {
         return "";
      } else if (substringFlag) {
         return formatText;
      } else {
         return formatText.isEmpty() ? "" : formatText.substring(0, formatText.length() - 1);
      }
   }

   public static String formatText(String iconsClearText, boolean iconsClearFlag, char ch) {
      return iconsClearFlag ? String.valueOf(ch) : iconsClearText + ch;
   }

   public static boolean canFlag(boolean iconsClearFlag, int iconsClearValue, int modifiers) {
      return iconsClearValue == 65 && (iconsClearFlag && (modifiers & 8) != 0 || !iconsClearFlag && (modifiers & 2) != 0);
   }
}
