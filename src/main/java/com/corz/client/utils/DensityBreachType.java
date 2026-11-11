package com.corz.client.utils;

public enum DensityBreachType {
   DENSITY("Density"),
   BREACH("Breach"),
   ANY("Any");

   private final String densityText;

   private DensityBreachType(String text) {
      this.densityText = text;
   }

   public String getText() {
      return this.densityText;
   }

   public static String[] getTextDensityText() {
      DensityBreachType[] densityBreachTypes = values();
      String[] strings = new String[densityBreachTypes.length];

      for (int i = 0; i < densityBreachTypes.length; i++) {
         strings[i] = densityBreachTypes[i].densityText;
      }

      return strings;
   }

   public static DensityBreachType getDensityText(String densityText) {
      for (DensityBreachType densityText2 : values()) {
         if (densityText2.densityText.equalsIgnoreCase(densityText)) {
            return densityText2;
         }
      }

      return DENSITY;
   }
}
