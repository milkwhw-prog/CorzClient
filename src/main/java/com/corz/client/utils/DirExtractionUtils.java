package com.corz.client.utils;

public final class DirExtractionUtils {
   private static volatile boolean dirExtractionActive = false;

   public static void updateFlag() {
      dirExtractionActive = true;
   }

   private DirExtractionUtils() {
   }

   public static boolean isFlag() {
      return dirExtractionActive;
   }
}
