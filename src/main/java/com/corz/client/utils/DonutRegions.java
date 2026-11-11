package com.corz.client.utils;

public final class DonutRegions {
   static final String[] asiaTexts = new String[]{"EU-C", "EU-W", "NA-E", "NA-W", "Asia", "Oce"};
   static final int[][] asiaInts = new int[][]{
      {82, 5},
      {100, 3},
      {101, 3},
      {102, 3},
      {103, 2},
      {104, 2},
      {105, 2},
      {106, 2},
      {91, 2},
      {83, 5},
      {44, 3},
      {75, 3},
      {42, 3},
      {41, 2},
      {40, 2},
      {39, 2},
      {38, 2},
      {92, 2},
      {84, 5},
      {45, 3},
      {14, 3},
      {13, 3},
      {12, 2},
      {11, 2},
      {10, 2},
      {37, 2},
      {93, 2},
      {85, 5},
      {46, 5},
      {74, 5},
      {3, 3},
      {2, 2},
      {1, 2},
      {25, 2},
      {36, 2},
      {94, 2},
      {86, 4},
      {47, 4},
      {72, 4},
      {71, 4},
      {5, 2},
      {4, 2},
      {24, 2},
      {35, 2},
      {95, 2},
      {87, 4},
      {51, 1},
      {17, 1},
      {9, 0},
      {8, 0},
      {7, 0},
      {23, 0},
      {34, 0},
      {96, 2},
      {88, 4},
      {54, 1},
      {18, 1},
      {61, 0},
      {62, 0},
      {21, 0},
      {22, 0},
      {33, 0},
      {97, 0},
      {89, 0},
      {26, 1},
      {27, 0},
      {28, 0},
      {29, 0},
      {30, 0},
      {59, 0},
      {32, 0},
      {98, 0},
      {90, 0},
      {107, 1},
      {108, 1},
      {109, 1},
      {110, 1},
      {111, 1},
      {112, 1},
      {113, 1},
      {99, 0}
   };

   private DonutRegions() {
   }

   public static String formatAsiaTexts(double asiaValue, double asiaValue2) {
      int asiaLimit = (int)Math.floor((asiaValue + 225000.0) / 50000.0);
      int floor2 = (int)Math.floor((asiaValue2 + 225000.0) / 50000.0);
      if (asiaLimit >= 0 && asiaLimit < 9 && floor2 >= 0 && floor2 < 9) {
         int[] asiaValues = asiaInts[floor2 * 9 + asiaLimit];
         return "Region " + asiaValues[0] + " (" + asiaTexts[Math.min(asiaValues[1], asiaTexts.length - 1)] + ")";
      } else {
         return null;
      }
   }
}
