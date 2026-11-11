package com.corz.client.utils;

import java.time.LocalDate;
import com.corz.client.categories.theme.ThemeCustomizer;

public enum HalloweenChristmasType {
   NONE,
   HALLOWEEN,
   CHRISTMAS;

   public static final String REVEALS_ITSELF_DECEMBER = "Reveals itself on 1 December";
   private static long timeMillis;
   private static boolean halloweenChristmasActive;
   private static HalloweenChristmasType halloweenChristmasHalloweenC = NONE;
   private static long halloweenChristmasValue;

   public static boolean isTimeFlag() {
      long timeMillis = System.currentTimeMillis();
      if (halloweenChristmasValue == 0L || timeMillis - halloweenChristmasValue > 30000L) {
         halloweenChristmasValue = timeMillis;
         boolean canChristmas2 = canChristmas(LocalDate.now());
         halloweenChristmasActive = canChristmas2;
      }

      return halloweenChristmasActive;
   }

   public static HalloweenChristmasType getTimeInstance() {
      HalloweenChristmasType enumInstance = getEnumInstance();
      if (enumInstance != null) {
         return enumInstance;
      } else {
         long currentTimeMillis = System.currentTimeMillis();
         if (timeMillis == 0L || currentTimeMillis - timeMillis > 30000L) {
            timeMillis = currentTimeMillis;
            HalloweenChristmasType halloweenChristmas = getHalloweenChristmas(LocalDate.now());
            halloweenChristmasHalloweenC = halloweenChristmas == CHRISTMAS && !isTimeFlag() ? NONE : halloweenChristmas;
         }

         return halloweenChristmasHalloweenC;
      }
   }

   public static HalloweenChristmasType getHalloweenChristmas(LocalDate localDate) {
      int monthValue = localDate.getMonthValue();
      int dayOfMonth = localDate.getDayOfMonth();
      if (monthValue != 10 && (monthValue != 11 || dayOfMonth != 1)) {
         return monthValue != 12 && (monthValue != 1 || dayOfMonth != 1) ? NONE : CHRISTMAS;
      } else {
         return HALLOWEEN;
      }
   }

   public static int getYear(LocalDate localDate) {
      return localDate.getMonthValue() == 1 ? localDate.getYear() - 1 : localDate.getYear();
   }

   public static HalloweenChristmasType getHalloweenChristmas(String halloweenChristmasText, boolean halloweenChristmasFlag) {
      return switch (halloweenChristmasText) {
         case "Halloween" -> HALLOWEEN;
         case "Christmas" -> halloweenChristmasFlag ? CHRISTMAS : HALLOWEEN;
         default -> null;
      };
   }

   public static HalloweenChristmasType getEnumInstance() {
      ThemeCustomizer singleton = ThemeCustomizer.singleton;
      return singleton == null ? null : getHalloweenChristmas(singleton.enumHelper.getCurrentOption(), isTimeFlag());
   }

   public static boolean canChristmas(LocalDate localDate) {
      return getHalloweenChristmas(localDate) == CHRISTMAS;
   }

   public static String formatHalloweenChristmas(String halloweenChristmasText, boolean halloweenChristmasFlag) {
      return !halloweenChristmasFlag && "Christmas".equals(halloweenChristmasText) ? "Halloween" : halloweenChristmasText;
   }

   public static String formatText(String halloweenChristmasText) {
      return halloweenChristmasText + " " + getYear(LocalDate.now());
   }
}
