package com.corz.client.utils;

import java.util.UUID;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public final class TextStyleUtils {
   public static boolean canFlag(UUID uUID) {
      return uUID != null && uUID.version() == 2;
   }

   public static double getAmount(double styleBound, double hypot, double styleBound3, double hashRegistryBound, double styleBound5, double styleBound6, double styleBound7, double goalBound, double styleBound9) {
      double styleValue = Math.max(hashRegistryBound, Math.min(styleBound, styleBound7));
      double maxMinX = Math.max(styleBound5, Math.min(hypot, goalBound));
      double maxMaxX = Math.max(styleBound6, Math.min(styleBound3, styleBound9));
      double styleFactor = styleBound - styleValue;
      double styleFactor2 = hypot - maxMinX;
      double styleFactor3 = styleBound3 - maxMaxX;
      return Math.sqrt(styleFactor * styleFactor + styleFactor2 * styleFactor2 + styleFactor3 * styleFactor3);
   }

   public static boolean canFriendlyEqual(AbstractTeam abstractTeam, AbstractTeam abstractTeam2) {
      return abstractTeam != null && abstractTeam.isEqual(abstractTeam2) && !abstractTeam.isFriendlyFireAllowed();
   }

   public static double getAmount(float degrees, float degrees2, double styleValue, double styleValue2, double styleValue3) {
      double angle = Math.toRadians(degrees);
      double radians = Math.toRadians(degrees2);
      double styleFactor = -Math.sin(angle) * Math.cos(radians);
      double sin = -Math.sin(radians);
      double styleValue5 = Math.cos(angle) * Math.cos(radians);
      return getAmount(styleFactor, sin, styleValue5, styleValue, styleValue2, styleValue3);
   }

   private TextStyleUtils() {
   }

   public static Integer getNumber(Text displayName, String numberText) {
      return displayName != null && numberText != null && !numberText.isEmpty() ? getSiblingsRgb(displayName, numberText, null) : null;
   }

   private static String formatText(String styleText) {
      StringBuilder stringBuilder = new StringBuilder(styleText.length());

      for (int i = 0; i < styleText.length(); i++) {
         char charAt = styleText.charAt(i);
         if (charAt >= 'a' && charAt <= 'z' || charAt >= 'A' && charAt <= 'Z' || charAt >= '0' && charAt <= '9' || charAt == '_') {
            stringBuilder.append(charAt);
         }
      }

      return stringBuilder.toString();
   }

   static double getAmount(double styleFactor, double styleFactor2, double styleFactor3, double styleValue, double styleFactor5, double styleFactor6) {
      double distance = Math.sqrt(styleFactor * styleFactor + styleFactor2 * styleFactor2 + styleFactor3 * styleFactor3);
      double sqrt2 = Math.sqrt(styleValue * styleValue + styleFactor5 * styleFactor5 + styleFactor6 * styleFactor6);
      if (!(distance < 1.0E-9) && !(sqrt2 < 1.0E-9)) {
         double styleBound = (styleFactor * styleValue + styleFactor2 * styleFactor5 + styleFactor3 * styleFactor6) / (distance * sqrt2);
         return Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, styleBound))));
      } else {
         return 0.0;
      }
   }

   public static boolean canFlag(double styleValue, double styleLimit) {
      return styleLimit >= 360.0 || styleValue <= styleLimit / 2.0;
   }

   private static Integer getSiblingsRgb(Text text, String siblingsRgbText, Integer integer) {
      TextColor color = text.getStyle().getColor();
      Integer siblingsRgb = color != null ? color.getRgb() : integer;
      String styleText = text.copyContentOnly().getString();
      if (siblingsRgb == null || !styleText.equals(siblingsRgbText) && !formatText(styleText).equals(siblingsRgbText)) {
         for (Text getSiblingsItem : text.getSiblings()) {
            Integer siblingsRgb2 = getSiblingsRgb(getSiblingsItem, siblingsRgbText, siblingsRgb);
            if (siblingsRgb2 != null) {
               return siblingsRgb2;
            }
         }

         return null;
      } else {
         return siblingsRgb;
      }
   }
}
