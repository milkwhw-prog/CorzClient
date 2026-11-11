package com.corz.client.utils;

import java.util.List;

public final class StaffBadge {

   public static int getValue(String staffBadgeText, String staffBadgeText2) {
      if (staffBadgeText != null && staffBadgeText2 != null && !staffBadgeText2.isEmpty()) {
         int indexOf = staffBadgeText.indexOf(45);
         if (indexOf >= 1 && indexOf <= 3) {
            int staffBadgeValue = 0;

            for (int i = 0; i < indexOf; i++) {
               char charAt = staffBadgeText.charAt(i);
               if (charAt < '0' || charAt > '9') {
                  return -1;
               }

               staffBadgeValue = staffBadgeValue * 10 + (charAt - '0');
            }

            int index = indexOf + 1 + staffBadgeText2.length();
            if (index <= staffBadgeText.length() && staffBadgeText.regionMatches(true, indexOf + 1, staffBadgeText2, 0, staffBadgeText2.length())) {
               return index != staffBadgeText.length() && staffBadgeText.charAt(index) != 45 ? -1 : staffBadgeValue;
            } else {
               return -1;
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private static boolean canFlag(String staffBadgeText, String staffBadgeText2) {
      int partLength = staffBadgeText2.length();

      for (int i = 0; i + partLength <= staffBadgeText.length(); i++) {
         if (staffBadgeText.regionMatches(true, i, staffBadgeText2, 0, partLength)) {
            return true;
         }
      }

      return false;
   }

   static boolean canFlag(String staffBadgeText, int[] staffBadgeValues, int index, int staffBadgeValue, String minecraftAltText) {
      if (!canMinecraftAlt(minecraftAltText) && !canPlayer(minecraftAltText) && Character.isLetterOrDigit(staffBadgeValue)) {
         int staffBadgeValue2 = staffBadgeValues[index];
         int index2 = index;
         int i = index;

         while (index2 > 0 && staffBadgeValues[index2 - 1] == staffBadgeValue2 && Character.isLetterOrDigit(staffBadgeText.charAt(index2 - 1))) {
            index2--;
         }

         while (i < staffBadgeText.length() && staffBadgeValues[i] == staffBadgeValue2 && Character.isLetterOrDigit(staffBadgeText.charAt(i))) {
            i++;
         }

         return i - index2 <= 2;
      } else {
         return false;
      }
   }

   private StaffBadge() {
   }

   static StaffBadge.Tier getRgbFont(String rgbFontText, int[] rgbFontValues, int rgbFontValue, int staffBadgeValue, StaffBadge.Run run) {
      String font = run.font();
      if (canPlayer(font)) {
         return null;
      } else {
         boolean rgbFontFlag = canFlag(staffBadgeValue, font) || canFlag(rgbFontText, rgbFontValues, rgbFontValue, staffBadgeValue, font);
         if (!rgbFontFlag && !canFlag3(staffBadgeValue)) {
            return null;
         } else {
            StaffBadge.Tier purpleGreen = getPurpleGreen(run.rgb());
            if (purpleGreen == null && rgbFontFlag) {
               purpleGreen = getPurpleGreen(font);
            }

            return purpleGreen;
         }
      }
   }

   public static int getValue(int staffBadgeValue, StaffBadge.Tier tier2) {
      if (staffBadgeValue < 0) {
         staffBadgeValue = tier2 == null ? 16777215 : tier2.colorRgb;
      }

      int staffBadgeBound = staffBadgeValue >> 16 & 0xFF;
      int aggroPreemptBound = staffBadgeValue >> 8 & 0xFF;
      int rocketsBuyBound = staffBadgeValue & 0xFF;
      int staffBadgeDivisor = Math.max(staffBadgeBound, Math.max(aggroPreemptBound, rocketsBuyBound));
      int min2 = Math.min(staffBadgeBound, Math.min(aggroPreemptBound, rocketsBuyBound));
      if (staffBadgeDivisor == 0) {
         return 16777215;
      } else {
         float staffBadgeValue3 = getAmount(staffBadgeBound, aggroPreemptBound, rocketsBuyBound, staffBadgeDivisor, min2) / 60.0F;
         float staffBadgeFactor = Math.max(0.42F, Math.min(0.68F, (float)(staffBadgeDivisor - min2) / staffBadgeDivisor));
         float max2 = Math.max(0.86F, Math.min(1.0F, staffBadgeDivisor / 255.0F));
         float staffBadgeFactor3 = max2 * staffBadgeFactor;
         float staffBadgeValue4 = staffBadgeFactor3 * (1.0F - Math.abs(staffBadgeValue3 % 2.0F - 1.0F));
         float deltaVar = max2 - staffBadgeFactor3;
         float staffBadgeValue5;
         float staffBadgeValue6;
         float staffBadgeValue7;
         if (staffBadgeValue3 < 1.0F) {
            staffBadgeValue5 = staffBadgeFactor3;
            staffBadgeValue6 = staffBadgeValue4;
            staffBadgeValue7 = 0.0F;
         } else if (staffBadgeValue3 < 2.0F) {
            staffBadgeValue5 = staffBadgeValue4;
            staffBadgeValue6 = staffBadgeFactor3;
            staffBadgeValue7 = 0.0F;
         } else if (staffBadgeValue3 < 3.0F) {
            staffBadgeValue5 = 0.0F;
            staffBadgeValue6 = staffBadgeFactor3;
            staffBadgeValue7 = staffBadgeValue4;
         } else if (staffBadgeValue3 < 4.0F) {
            staffBadgeValue5 = 0.0F;
            staffBadgeValue6 = staffBadgeValue4;
            staffBadgeValue7 = staffBadgeFactor3;
         } else if (staffBadgeValue3 < 5.0F) {
            staffBadgeValue5 = staffBadgeValue4;
            staffBadgeValue6 = 0.0F;
            staffBadgeValue7 = staffBadgeFactor3;
         } else {
            staffBadgeValue5 = staffBadgeFactor3;
            staffBadgeValue6 = 0.0F;
            staffBadgeValue7 = staffBadgeValue4;
         }

         return Math.round((staffBadgeValue5 + deltaVar) * 255.0F) << 16 | Math.round((staffBadgeValue6 + deltaVar) * 255.0F) << 8 | Math.round((staffBadgeValue7 + deltaVar) * 255.0F);
      }
   }

   public static boolean canMinecraftAlt(String minecraftAltText) {
      return minecraftAltText != null && !minecraftAltText.isEmpty()
         ? minecraftAltText.equals("minecraft:default") || minecraftAltText.equals("minecraft:uniform") || minecraftAltText.equals("minecraft:alt") || minecraftAltText.equals("minecraft:illageralt")
         : true;
   }

   private static boolean canFont(String fontText, List<StaffBadge.Run> list, int[] fontValues, int index) {
      char charAt = fontText.charAt(index);
      return canFlag(charAt) && canMinecraftAlt(((StaffBadge.Run)list.get(fontValues[index])).font());
   }

   static int getValueForString(String staffBadgeText, String staffBadgeText2) {
      if (staffBadgeText2 != null && !staffBadgeText2.isEmpty() && staffBadgeText2.length() <= staffBadgeText.length()) {
         int allLength = staffBadgeText2.length();

         for (int i = staffBadgeText.length() - allLength; i >= 0; i--) {
            if (staffBadgeText.regionMatches(true, i, staffBadgeText2, 0, allLength)) {
               boolean staffBadgeFlag = i == 0 || !canFlag(staffBadgeText.charAt(i - 1));
               boolean staffBadgeFlag2 = i + allLength == staffBadgeText.length() || !canFlag(staffBadgeText.charAt(i + allLength));
               if (staffBadgeFlag && staffBadgeFlag2) {
                  return i + allLength;
               }
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   static boolean canFlag(int staffBadgeValue, String staffBadgeText) {
      if (Character.isWhitespace(staffBadgeValue) || Character.isSpaceChar(staffBadgeValue)) {
         return false;
      } else if (canFlag(staffBadgeValue)) {
         return true;
      } else if (canMinecraftAlt(staffBadgeText)) {
         return false;
      } else {
         return canSprite(staffBadgeText) ? true : !Character.isLetterOrDigit(staffBadgeValue) && !canFlagForInt(staffBadgeValue);
      }
   }

   static float getAmount(int staffBadgeValue, int aggroPreemptBound, int rocketsBuyBound, int staffBadgeValue4, int staffBadgeValue5) {
      float staffBadgeDivisor = staffBadgeValue4 - staffBadgeValue5;
      if (staffBadgeDivisor <= 0.0F) {
         return 0.0F;
      } else {
         float staffBadgeValue6;
         if (staffBadgeValue4 == staffBadgeValue) {
            staffBadgeValue6 = (aggroPreemptBound - rocketsBuyBound) / staffBadgeDivisor % 6.0F;
         } else if (staffBadgeValue4 == aggroPreemptBound) {
            staffBadgeValue6 = (rocketsBuyBound - staffBadgeValue) / staffBadgeDivisor + 2.0F;
         } else {
            staffBadgeValue6 = (staffBadgeValue - aggroPreemptBound) / staffBadgeDivisor + 4.0F;
         }

         float staffBadgeValue7 = staffBadgeValue6 * 60.0F;
         return staffBadgeValue7 < 0.0F ? staffBadgeValue7 + 360.0F : staffBadgeValue7;
      }
   }

   private static StaffBadge.Hit getRgb(String rgbText, List<StaffBadge.Run> list, int[] rgbValues, int rgbValue, String staffBadgeText) {
      int rgbValue2 = rgbValue;

      while (rgbValue2 > 0 && Character.isWhitespace(rgbText.charAt(rgbValue2 - 1))) {
         rgbValue2--;
      }

      for (int i = 0; rgbValue2 > 0 && i < 4; i++) {
         int codePointBefore = rgbText.codePointBefore(rgbValue2);
         if (Character.isWhitespace(codePointBefore)) {
            break;
         }

         int index = rgbValue2 - Character.charCount(codePointBefore);
         StaffBadge.Run staffBadgeRun = (StaffBadge.Run)list.get(rgbValues[index]);
         StaffBadge.Tier rgbFont = getRgbFont(rgbText, rgbValues, index, codePointBefore, staffBadgeRun);
         if (rgbFont != null) {
            return new StaffBadge.Hit(staffBadgeText, rgbFont, staffBadgeRun.rgb() < 0 ? rgbFont.colorRgb : staffBadgeRun.rgb() & 16777215);
         }

         rgbValue2 = index;
      }

      return null;
   }

   private static boolean canFlag(String staffBadgeText, int staffBadgeLimit, int index) {
      char ch = staffBadgeLimit > 0 ? staffBadgeText.charAt(staffBadgeLimit - 1) : 32;
      char ch2 = index < staffBadgeText.length() ? staffBadgeText.charAt(index) : 32;
      return ch == '[' || ch == '(' || ch == '{' || ch == '<' || ch2 == ']' || ch2 == ')' || ch2 == '}' || ch2 == '>';
   }

   public static boolean canFlag(int staffBadgeLimit) {
      return staffBadgeLimit >= 57344 && staffBadgeLimit <= 63743 || staffBadgeLimit >= 983040 && staffBadgeLimit <= 1048573 || staffBadgeLimit >= 1048576 && staffBadgeLimit <= 1114109;
   }

   static boolean canSprite(String spriteText) {
      return spriteText != null && spriteText.startsWith("sprite:");
   }

   private static StaffBadge.Hit getRgbForStringList(String rgbText, List<StaffBadge.Run> list, int[] rgbValues, int rgbValue, String staffBadgeText) {
      int rgbValue2 = rgbValue;

      while (rgbValue2 < rgbText.length()) {
         int codePointAt = rgbText.codePointAt(rgbValue2);
         StaffBadge.Run staffBadgeRun = (StaffBadge.Run)list.get(rgbValues[rgbValue2]);
         int from = rgbValue2;
         rgbValue2 += Character.charCount(codePointAt);
         StaffBadge.Tier rgbFont = getRgbFont(rgbText, rgbValues, from, codePointAt, staffBadgeRun);
         if (rgbFont != null) {
            return new StaffBadge.Hit(staffBadgeText, rgbFont, staffBadgeRun.rgb() < 0 ? rgbFont.colorRgb : staffBadgeRun.rgb() & 16777215);
         }
      }

      return null;
   }

   static boolean canFlag(char staffBadgeLimit) {
      return staffBadgeLimit >= 'a' && staffBadgeLimit <= 'z' || staffBadgeLimit >= 'A' && staffBadgeLimit <= 'Z' || staffBadgeLimit >= '0' && staffBadgeLimit <= '9' || staffBadgeLimit == '_';
   }

   private static boolean canFlagForInt(int staffBadgeValue) {
      if (staffBadgeValue < 128) {
         return !Character.isLetterOrDigit(staffBadgeValue);
      } else {
         int type2 = Character.getType(staffBadgeValue);
         return type2 == 20 || type2 == 21 || type2 == 22 || type2 == 23 || type2 == 24 || type2 == 29 || type2 == 30;
      }
   }

   private static String formatText(String staffBadgeText, List<StaffBadge.Run> list, int[] staffBadgeValues, int staffBadgeValue) {
      int staffBadgeLimit = staffBadgeValue;

      while (staffBadgeLimit > 0) {
         while (staffBadgeLimit > 0 && !canFont(staffBadgeText, list, staffBadgeValues, staffBadgeLimit - 1)) {
            staffBadgeLimit--;
         }

         int end = staffBadgeLimit;

         while (staffBadgeLimit > 0 && canFont(staffBadgeText, list, staffBadgeValues, staffBadgeLimit - 1)) {
            staffBadgeLimit--;
         }

         if (end <= staffBadgeLimit) {
            break;
         }

         if (end - staffBadgeLimit >= 3 && end - staffBadgeLimit <= 16 && !canFlag(staffBadgeText, staffBadgeLimit, end) && canFlagForStringInt(staffBadgeText, staffBadgeLimit, end)) {
            return staffBadgeText.substring(staffBadgeLimit, end);
         }
      }

      return null;
   }

   public static StaffBadge.Hit getPurpleGreen(StaffBadge.Hit purpleGreen, int purpleGreenLimit, String purpleGreenText) {
      if (purpleGreenLimit < 0) {
         return purpleGreen;
      } else if (purpleGreenLimit >= 9) {
         return null;
      } else if (purpleGreen != null) {
         return purpleGreen;
      } else if (purpleGreenText != null && !purpleGreenText.isEmpty()) {
         StaffBadge.Tier tier = purpleGreenLimit <= 2 ? StaffBadge.Tier.PURPLE : StaffBadge.Tier.GREEN;
         return new StaffBadge.Hit(purpleGreenText, tier, tier.colorRgb);
      } else {
         return null;
      }
   }

   private static boolean canFlagForStringInt(String staffBadgeText, int staffBadgeValue, int staffBadgeLimit) {
      for (int i = staffBadgeValue; i < staffBadgeLimit; i++) {
         if (Character.isLetter(staffBadgeText.charAt(i))) {
            return true;
         }
      }

      return false;
   }

   private static StaffBadge.Hit getRgb(String rgbText, List<StaffBadge.Run> list, int[] rgbValues) {
      int index = 0;

      while (index < rgbText.length()) {
         int codePointAt = rgbText.codePointAt(index);
         StaffBadge.Run staffBadgeRun = (StaffBadge.Run)list.get(rgbValues[index]);
         int rgbValue = index;
         index += Character.charCount(codePointAt);
         StaffBadge.Tier rgbFont = getRgbFont(rgbText, rgbValues, rgbValue, codePointAt, staffBadgeRun);
         if (rgbFont != null) {
            String formatText3 = formatText(rgbText, list, rgbValues, rgbValue);
            if (formatText3 == null) {
               formatText3 = formatTextForStringList(rgbText, list, rgbValues, index);
            }

            if (formatText3 != null) {
               return new StaffBadge.Hit(formatText3, rgbFont, staffBadgeRun.rgb() < 0 ? rgbFont.colorRgb : staffBadgeRun.rgb() & 16777215);
            }
         }
      }

      return null;
   }

   public static int getValue(int staffBadgeValue, int staffBadgeValue2, float staffBadgeFactor) {
      if (staffBadgeFactor <= 0.0F) {
         return staffBadgeValue & 16777215;
      } else if (staffBadgeFactor >= 1.0F) {
         return staffBadgeValue2 & 16777215;
      } else {
         int round = Math.round((staffBadgeValue >> 16 & 0xFF) + ((staffBadgeValue2 >> 16 & 0xFF) - (staffBadgeValue >> 16 & 0xFF)) * staffBadgeFactor);
         int round4 = Math.round((staffBadgeValue >> 8 & 0xFF) + ((staffBadgeValue2 >> 8 & 0xFF) - (staffBadgeValue >> 8 & 0xFF)) * staffBadgeFactor);
         int round5 = Math.round((staffBadgeValue & 0xFF) + ((staffBadgeValue2 & 0xFF) - (staffBadgeValue & 0xFF)) * staffBadgeFactor);
         return round << 16 | round4 << 8 | round5;
      }
   }

   public static boolean canFlag3(int staffBadgeLimit) {
      return staffBadgeLimit == 9733
         || staffBadgeLimit == 9734
         || staffBadgeLimit >= 10022 && staffBadgeLimit <= 10032
         || staffBadgeLimit >= 10036 && staffBadgeLimit <= 10041
         || staffBadgeLimit == 10050
         || staffBadgeLimit == 8902
         || staffBadgeLimit == 9055
         || staffBadgeLimit == 1645
         || staffBadgeLimit >= 11088 && staffBadgeLimit <= 11090
         || staffBadgeLimit >= 11212 && staffBadgeLimit <= 11215
         || staffBadgeLimit >= 11240 && staffBadgeLimit <= 11247
         || staffBadgeLimit == 127775
         || staffBadgeLimit == 127776
         || staffBadgeLimit >= 128960 && staffBadgeLimit <= 128984;
   }

   static StaffBadge.Tier getPurpleGreen(String purpleGreenText) {
      if (purpleGreenText == null) {
         return null;
      } else if (canFlag(purpleGreenText, "purple") || canFlag(purpleGreenText, "violet") || canFlag(purpleGreenText, "magenta")) {
         return StaffBadge.Tier.PURPLE;
      } else {
         return !canFlag(purpleGreenText, "green") && !canFlag(purpleGreenText, "lime") ? null : StaffBadge.Tier.GREEN;
      }
   }

   public static StaffBadge.Hit getText(List<StaffBadge.Run> list, String staffBadgeText) {
      if (list != null && !list.isEmpty()) {
         int staffBadgeTotal = 0;

         for (int i = 0; i < list.size(); i++) {
            staffBadgeTotal += ((StaffBadge.Run)list.get(i)).text().length();
         }

         if (staffBadgeTotal == 0) {
            return null;
         } else {
            StringBuilder stringBuilder = new StringBuilder(staffBadgeTotal);
            int[] ints = new int[staffBadgeTotal];
            int index = 0;

            for (int j = 0; index < list.size(); index++) {
               String builderText = ((StaffBadge.Run)list.get(index)).text();
               stringBuilder.append(builderText);

               for (int k = 0; k < builderText.length(); k++) {
                  ints[j + k] = index;
               }

               j += builderText.length();
            }

            String staffBadgeText3 = stringBuilder.toString();
            int staffBadgeLimit = getValueForString(staffBadgeText3, staffBadgeText);
            if (staffBadgeLimit >= 0) {
               StaffBadge.Hit rgb = getRgbForStringList(staffBadgeText3, list, ints, staffBadgeLimit, staffBadgeText);
               return rgb != null ? rgb : getRgb(staffBadgeText3, list, ints, staffBadgeLimit - staffBadgeText.length(), staffBadgeText);
            } else {
               return getRgb(staffBadgeText3, list, ints);
            }
         }
      } else {
         return null;
      }
   }

   private static String formatTextForStringList(String staffBadgeText, List<StaffBadge.Run> list, int[] staffBadgeValues, int staffBadgeValue) {
      int allLength = staffBadgeText.length();
      int staffBadgeLimit = staffBadgeValue;

      while (staffBadgeLimit < allLength && !canFont(staffBadgeText, list, staffBadgeValues, staffBadgeLimit)) {
         staffBadgeLimit++;
      }

      int start = staffBadgeLimit;

      while (staffBadgeLimit < allLength && canFont(staffBadgeText, list, staffBadgeValues, staffBadgeLimit)) {
         staffBadgeLimit++;
      }

      return staffBadgeLimit - start >= 3 && staffBadgeLimit - start <= 16 && !canFlag(staffBadgeText, start, staffBadgeLimit) && canFlagForStringInt(staffBadgeText, start, staffBadgeLimit) ? staffBadgeText.substring(start, staffBadgeLimit) : null;
   }

   public static StaffBadge.Tier getPurpleGreen(int purpleGreenValue) {
      if (purpleGreenValue < 0) {
         return null;
      } else {
         purpleGreenValue &= 16777215;
         if (purpleGreenValue == 16733695 || purpleGreenValue == 11141290) {
            return StaffBadge.Tier.PURPLE;
         } else if (purpleGreenValue != 5635925 && purpleGreenValue != 43520) {
            int purpleGreenBound = purpleGreenValue >> 16 & 0xFF;
            int aggroPreemptBound = purpleGreenValue >> 8 & 0xFF;
            int rocketsBuyBound = purpleGreenValue & 0xFF;
            int purpleGreenDivisor = Math.max(purpleGreenBound, Math.max(aggroPreemptBound, rocketsBuyBound));
            int min2 = Math.min(purpleGreenBound, Math.min(aggroPreemptBound, rocketsBuyBound));
            if (purpleGreenDivisor < 64) {
               return null;
            } else if ((float)(purpleGreenDivisor - min2) / purpleGreenDivisor < 0.28F) {
               return null;
            } else {
               float purpleGreenLimit = getAmount(purpleGreenBound, aggroPreemptBound, rocketsBuyBound, purpleGreenDivisor, min2);
               if (purpleGreenLimit >= 248.0F && purpleGreenLimit <= 326.0F) {
                  return StaffBadge.Tier.PURPLE;
               } else {
                  return purpleGreenLimit >= 75.0F && purpleGreenLimit <= 165.0F ? StaffBadge.Tier.GREEN : null;
               }
            }
         } else {
            return StaffBadge.Tier.GREEN;
         }
      }
   }

   static boolean canPlayer(String playerText) {
      return playerText != null && playerText.startsWith("player:");
   }

   public record Hit(String name, StaffBadge.Tier tier, int rgb) {
   }

   public record Run(String text, int rgb, String font) {
      public Run(String text, int rgb, String font) {
         if (text == null) {
            text = "";
         }

         this.text = text;
         this.rgb = rgb;
         this.font = font;
      }

      public static StaffBadge.Run getRun(String rgb, int text) {
         return new StaffBadge.Run(rgb, text, null);
      }
   }

   public static enum Tier {
      PURPLE("Purple", 16733695),
      GREEN("Green", 5635925);

      public final int colorRgb;
      public final String tierText;

      private Tier(String text, int value) {
         this.tierText = text;
         this.colorRgb = value;
      }
   }
}
