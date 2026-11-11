package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;

public final class KnifeSoundVariety {
   private final Map<String, Integer> knifeSoundIntegerMap = new HashMap<>();

   public int getValue(String knifeSoundText, int knifeSoundLimit, double knifeSoundLimit2) {
      double knifeSoundFactor = knifeSoundLimit2 >= 0.0 && knifeSoundLimit2 < 1.0 ? knifeSoundLimit2 : 0.0;
      if (knifeSoundLimit <= 1) {
         this.knifeSoundIntegerMap.put(knifeSoundText, 0);
         return 0;
      } else {
         Integer integer = this.knifeSoundIntegerMap.get(knifeSoundText);
         int knifeSoundCounter;
         if (integer != null && integer >= 0 && integer < knifeSoundLimit) {
            knifeSoundCounter = Math.min(knifeSoundLimit - 2, (int)Math.floor(knifeSoundFactor * (knifeSoundLimit - 1)));
            if (knifeSoundCounter >= integer) {
               knifeSoundCounter++;
            }
         } else {
            knifeSoundCounter = Math.min(knifeSoundLimit - 1, (int)Math.floor(knifeSoundFactor * knifeSoundLimit));
         }

         this.knifeSoundIntegerMap.put(knifeSoundText, knifeSoundCounter);
         return knifeSoundCounter;
      }
   }

   public int getValue(String knifeSoundText) {
      Integer integer = this.knifeSoundIntegerMap.get(knifeSoundText);
      return integer == null ? -1 : integer;
   }

   public void update() {
      this.knifeSoundIntegerMap.clear();
   }

   public static float getAmount(float knifeSoundFactor, double knifeSoundFactor2, double knifeSoundLimit) {
      double knifeSoundValue = knifeSoundLimit >= 0.0 && knifeSoundLimit <= 1.0 ? knifeSoundLimit : 0.5;
      return (float)(knifeSoundFactor * (1.0 + knifeSoundFactor2 * (2.0 * knifeSoundValue - 1.0)));
   }

   public static float getAmount(float knifeSoundFactor, double knifeSoundLimit) {
      double knifeSoundValue = knifeSoundLimit >= 0.0 && knifeSoundLimit <= 1.0 ? knifeSoundLimit : 1.0;
      return (float)(knifeSoundFactor * (0.8 + 0.19999999999999996 * knifeSoundValue));
   }
}
