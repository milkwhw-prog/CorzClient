package com.corz.client.utils;

import java.util.Locale;

public final class Places {
   private static final String[] placesTexts = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

   public static String getFormat(double placesValue) {
      return placesValue < 1000.0 ? Math.round(placesValue) + " m" : String.format(Locale.ROOT, "%.1f km", placesValue / 1000.0);
   }

   public static String formatTexts(double placesValue, double x) {
      double degrees = Math.toDegrees(Math.atan2(-placesValue, x));
      int floorMod = (int)Math.floorMod(Math.round(degrees / 45.0), 8L);
      return placesTexts[floorMod];
   }

   public static String formatText(double placesValue, double placesValue2, double pilotRenderLimit) {
      return Math.round(placesValue) + ", " + Math.round(placesValue2) + ", " + Math.round(pilotRenderLimit);
   }

   private Places() {
   }
}
