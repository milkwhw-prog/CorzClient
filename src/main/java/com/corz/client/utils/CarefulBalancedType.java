package com.corz.client.utils;

import java.util.Locale;

public enum CarefulBalancedType {
   TERMINATOR,
   BALANCED,
   CAREFUL;

   public static CarefulBalancedType getBalancedCareful(String balancedCarefulText) {
      if (balancedCarefulText == null) {
         return TERMINATOR;
      } else {
         String lowerCase = balancedCarefulText.trim().toLowerCase(Locale.ROOT);

         return switch (lowerCase) {
            case "balanced" -> BALANCED;
            case "careful" -> CAREFUL;
            default -> TERMINATOR;
         };
      }
   }
}
