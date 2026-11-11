package com.corz.client.utils;

public enum SectionStatus {
   NORMAL(0, 220, 0),
   EMPTY_MASK(255, 220, 0),
   ZEROED_PRESENT(255, 40, 40),
   UNIFORM_MAX(0, 220, 255),
   MISSING(140, 140, 140),
   UNKNOWN(80, 80, 80);

   private final int red;
   private final int green;
   private final int blue;

   public int getRed() {
      return this.red;
   }

   public int getGreen() {
      return this.green;
   }

   public int getBlue() {
      return this.blue;
   }

   private SectionStatus(int value, int value2, int value3) {
      this.red = value;
      this.green = value2;
      this.blue = value3;
   }
}
