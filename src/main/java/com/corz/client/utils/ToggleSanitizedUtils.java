package com.corz.client.utils;

import java.util.Locale;

public final class ToggleSanitizedUtils {

   public static int getValue(int sanitizedValue, int sanitizedValue2) {
      return sanitizedValue;
   }

   public static ToggleSanitizedUtils.Resolution getMarkKeep(int markKeepValue, int sanitizedValue) {
      return canFlag(sanitizedValue, markKeepValue) ? ToggleSanitizedUtils.Resolution.CLEAR_TOGGLE_KEEP_MARK : ToggleSanitizedUtils.Resolution.NONE;
   }

   public static String formatText(String sanitizedText) {
      return "Key already used by " + sanitizedText;
   }

   public static String formatToggleSanitized(int sanitizedValue, int sanitizedValue2, ToggleSanitizedUtils.Resolution resolution) {
      return String.format(
         Locale.ROOT, "markKey=%d toggleKey=%d resolution=%s sanitizedToggleKey=%d", sanitizedValue, sanitizedValue2, resolution, getMarkKeepForInt(sanitizedValue, sanitizedValue2)
      );
   }

   public static boolean canFlag(int sanitizedValue, int markKeepValue) {
      return sanitizedValue != -1 && sanitizedValue == markKeepValue;
   }

   public static boolean canFlagForInt(int sanitizedValue, int markKeepValue) {
      return !canFlag(sanitizedValue, markKeepValue);
   }

   private ToggleSanitizedUtils() {
   }

   public static int getMarkKeepForInt(int markKeepValue, int sanitizedValue) {
      return getMarkKeep(markKeepValue, sanitizedValue) == ToggleSanitizedUtils.Resolution.CLEAR_TOGGLE_KEEP_MARK ? -1 : sanitizedValue;
   }

   public static ToggleSanitizedUtils.Dispatch getNothingMark(int nothingMarkValue, int nothingMarkValue2, int nothingMarkValue3) {
      if (nothingMarkValue == -1) {
         return ToggleSanitizedUtils.Dispatch.NOTHING;
      } else if (nothingMarkValue == nothingMarkValue2) {
         return ToggleSanitizedUtils.Dispatch.MARK_CONTAINER;
      } else {
         return nothingMarkValue == nothingMarkValue3 ? ToggleSanitizedUtils.Dispatch.TOGGLE_MODULE : ToggleSanitizedUtils.Dispatch.NOTHING;
      }
   }

   public static enum Dispatch {
      NOTHING,
      MARK_CONTAINER,
      TOGGLE_MODULE;
   }

   public static enum Resolution {
      NONE,
      CLEAR_TOGGLE_KEEP_MARK;
   }
}
