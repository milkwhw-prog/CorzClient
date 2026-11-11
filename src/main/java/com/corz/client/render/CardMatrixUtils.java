package com.corz.client.render;

import java.awt.Color;
import com.corz.client.categories.theme.ThemeCustomizer;

public final class CardMatrixUtils {

   public static int getValue() {
      return isModernFlag() ? getValue(-16184562, 140) : -16184562;
   }

   private static int getValue(int cardMatrixValue, int cardMatrixValue2) {
      return (cardMatrixValue2 & 0xFF) << 24 | cardMatrixValue & 16777215;
   }

   public static int getValueInt() {
      return 452984831;
   }

   public static int getValue3() {
      return isModernFlag() ? getValue(-15591908, 96) : -15591908;
   }

   public static boolean isModernFlag() {
      return ThemeCustomizer.modernFlag;
   }

   public static int getValue4() {
      return isModernFlag() ? getValue(-15723751, 61) : -15723751;
   }

   public static int getValue5() {
      return -1535700;
   }

   public static int getValue6() {
      return -8393375;
   }

   public static int getValue7() {
      return isModernFlag() ? getValue(-16118769, 77) : -16118769;
   }

   public static int getValue8() {
      return isModernFlag() ? 184549375 : -15921132;
   }

   public static int getValue(int cardMatrixValue, int releaseRightValue, double cardMatrixFactor) {
      cardMatrixFactor = cardMatrixFactor < 0.0 ? 0.0 : (cardMatrixFactor > 1.0 ? 1.0 : cardMatrixFactor);
      int cardMatrixValue3 = cardMatrixValue >>> 24 & 0xFF;
      int cardMatrixValue4 = cardMatrixValue >> 16 & 0xFF;
      int cardMatrixValue5 = cardMatrixValue >> 8 & 0xFF;
      int cardMatrixValue6 = cardMatrixValue & 0xFF;
      int cardMatrixValue7 = releaseRightValue >>> 24 & 0xFF;
      int cardMatrixValue8 = releaseRightValue >> 16 & 0xFF;
      int cardMatrixValue9 = releaseRightValue >> 8 & 0xFF;
      int cardMatrixValue10 = releaseRightValue & 0xFF;
      return (int)(cardMatrixValue3 + (cardMatrixValue7 - cardMatrixValue3) * cardMatrixFactor) << 24
         | (int)(cardMatrixValue4 + (cardMatrixValue8 - cardMatrixValue4) * cardMatrixFactor) << 16
         | (int)(cardMatrixValue5 + (cardMatrixValue9 - cardMatrixValue5) * cardMatrixFactor) << 8
         | (int)(cardMatrixValue6 + (cardMatrixValue10 - cardMatrixValue6) * cardMatrixFactor);
   }

   public static int getReleaseRight(double releaseRightBound) {
      int releaseRightValue = (int)Math.round(Math.max(0.0, Math.min(1.0, releaseRightBound)) * 255.0);
      return releaseRightValue << 24 | MatrixEspUtils.releaseRightValue & 16777215;
   }

   public static int getValue9() {
      return isModernFlag() ? getValue(-15262942, 110) : -15262942;
   }

   public static Color getColor(int colorValue) {
      return new Color(colorValue, true);
   }

   public static int getReleaseRight() {
      return MatrixEspUtils.releaseRightValue;
   }

   private CardMatrixUtils() {
   }

   public static int getValue10() {
      return 2013265920;
   }

   public static int getReleaseRightInt() {
      return isModernFlag() ? 218103807 : getValue(-16184562, MatrixEspUtils.releaseRightValue, 0.04);
   }
}
