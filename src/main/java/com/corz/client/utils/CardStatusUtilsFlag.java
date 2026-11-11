package com.corz.client.utils;

public final class CardStatusUtilsFlag {
   public static boolean canFlag(int cardStatusLimit) {
      return cardStatusLimit >= 32 && cardStatusLimit <= 126 || cardStatusLimit >= 160 && cardStatusLimit <= 254 || cardStatusLimit == 10;
   }

   private CardStatusUtilsFlag() {
   }

   public static String getOrDefault(String orDefault) {
      if (orDefault != null && !orDefault.isEmpty()) {
         int orDefaultLimit = orDefault.length();
         int orDefaultValue = -1;

         for (int i = 0; i < orDefaultLimit; i++) {
            if (!canFlag(orDefault.charAt(i))) {
               orDefaultValue = i;
               break;
            }
         }

         if (orDefaultValue < 0) {
            return orDefault;
         } else {
            StringBuilder stringBuilder = new StringBuilder(orDefaultLimit + 8);
            stringBuilder.append(orDefault, 0, orDefaultValue);

            for (int j = orDefaultValue; j < orDefaultLimit; j++) {
               char charAt = orDefault.charAt(j);
               if (canFlag(charAt)) {
                  stringBuilder.append(charAt);
               } else {
                  switch (charAt) {
                     case '\u00a0':
                        stringBuilder.append(' ');
                        break;
                     case '\u2013':
                     case '\u2014':
                        boolean orDefaultFlag = stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == ' ';
                        boolean orDefaultFlag2 = j + 1 < orDefaultLimit && orDefault.charAt(j + 1) == ' ';
                        if (!orDefaultFlag) {
                           stringBuilder.append(' ');
                        }

                        stringBuilder.append('-');
                        if (!orDefaultFlag2) {
                           stringBuilder.append(' ');
                        }
                        break;
                     case '\u2018':
                     case '\u2019':
                        stringBuilder.append('\'');
                        break;
                     case '\u201c':
                     case '\u201d':
                        stringBuilder.append('"');
                        break;
                     case '\u2022':
                        stringBuilder.append('*');
                        break;
                     case '\u2026':
                        stringBuilder.append("...");
                        break;
                     case '\u2190':
                        stringBuilder.append("<-");
                        break;
                     case '\u2192':
                        stringBuilder.append("->");
                        break;
                     case '\u2713':
                     case '\u2714':
                        stringBuilder.append('+');
                        break;
                     case '\u2717':
                     case '\u2718':
                        stringBuilder.append('x');
                        break;
                     default:
                        stringBuilder.append('?');
                  }
               }
            }

            return stringBuilder.toString();
         }
      } else {
         return orDefault;
      }
   }

   public static boolean canFlag(String cardStatusText) {
      if (cardStatusText == null) {
         return true;
      } else {
         for (int i = 0; i < cardStatusText.length(); i++) {
            if (!canFlag(cardStatusText.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }
}
