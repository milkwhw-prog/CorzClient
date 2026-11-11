package com.corz.client.utils;

public final class PageRaltUtils {

   public static String formatNone(int noneValue) {
      if (noneValue < 0) {
         return "None";
      } else {
         return canFlag(noneValue) ? formatTextForInt(noneValue) : formatPageRalt(noneValue);
      }
   }

   public static String formatText(int pageRaltValue) {
      return "Key " + pageRaltValue;
   }

   public static boolean canFlag(int pageRaltLimit) {
      return pageRaltLimit >= 1000;
   }

   public static String formatPageRalt(int pageRaltLimit) {
      switch (pageRaltLimit) {
         case 32:
            return "Space";
         case 256:
            return "Esc";
         case 257:
            return "Enter";
         case 258:
            return "Tab";
         case 259:
            return "Backspace";
         case 260:
            return "Insert";
         case 261:
            return "Delete";
         case 262:
            return "Right";
         case 263:
            return "Left";
         case 264:
            return "Down";
         case 265:
            return "Up";
         case 266:
            return "Page Up";
         case 267:
            return "Page Down";
         case 268:
            return "Home";
         case 269:
            return "End";
         case 280:
            return "Caps";
         case 340:
            return "LSHIFT";
         case 341:
            return "LCTRL";
         case 342:
            return "LALT";
         case 343:
            return "LSUPER";
         case 344:
            return "RSHIFT";
         case 345:
            return "RCTRL";
         case 346:
            return "RALT";
         case 347:
            return "RSUPER";
         default:
            return pageRaltLimit >= 290 && pageRaltLimit <= 301 ? "F" + (pageRaltLimit - 289) : null;
      }
   }

   public static int getValue(int pageRaltValue) {
      return pageRaltValue - 1000;
   }

   public static String formatTextForInt(int pageRaltValue) {
      return "Mouse " + (getValue(pageRaltValue) + 1);
   }

   private PageRaltUtils() {
   }

   public static boolean canFlagForInt(int pageRaltValue) {
      return pageRaltValue == 256 || pageRaltValue == 259 || pageRaltValue == 261;
   }

   public static int getValueForInt(int pageRaltValue) {
      return 1000 + pageRaltValue;
   }
}
