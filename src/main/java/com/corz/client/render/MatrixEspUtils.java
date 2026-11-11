package com.corz.client.render;

import com.corz.client.categories.theme.ThemeCustomizer;
import ai.auth.ModuleRegistry;

public class MatrixEspUtils {
   private static boolean matrixEspActive;
   private static int previousBlendedColor;
   private static int blendedEspColor;
   public static int releaseRightAccentColor;
   public static int releaseRightValue;
   private static long lastNanoTime;
   public static int espFillColor;
   public static int espOutlineColor;
   private static int lastTargetEspColor = -1;
   private static boolean needsColorUpdate;

   private static ThemeCustomizer getThemeCustomizer() {
      try {
         return (ThemeCustomizer)ModuleRegistry.getModule(ThemeCustomizer.class);
      } catch (Exception e) {
         return null;
      }
   }

   public static void updateFlag() {
      needsColorUpdate = true;
   }

   public static boolean isFlag() {
      return matrixEspActive;
   }

   static {
      updateFlagTime();
   }

   public static void updateFlagTime() {
      ThemeCustomizer themeCustomizer = getThemeCustomizer();
      int value7 = (themeCustomizer != null ? themeCustomizer.getValue() : -16748289) & 16777215;
      long nanoTime = System.nanoTime();
      if (value7 != lastTargetEspColor) {
         if (needsColorUpdate && lastTargetEspColor != -1) {
            previousBlendedColor = blendedEspColor;
            lastNanoTime = nanoTime;
            matrixEspActive = true;
         } else {
            blendedEspColor = value7;
            matrixEspActive = false;
         }

         lastTargetEspColor = value7;
      }

      needsColorUpdate = false;
      if (matrixEspActive) {
         float matrixEspLimit = (float)(nanoTime - lastNanoTime) / 4.2E8F;
         if (matrixEspLimit >= 1.0F) {
            blendedEspColor = lastTargetEspColor;
            matrixEspActive = false;
         } else {
            blendedEspColor = MatrixAmountUtils.getValueForIntFloat(0xFF000000 | previousBlendedColor, 0xFF000000 | lastTargetEspColor, MatrixAmountUtils.getAmountForFloat(matrixEspLimit)) & 16777215;
         }
      }

      int blendedEspColor22 = blendedEspColor;
      int matrixEspValue = blendedEspColor22 >> 16 & 0xFF;
      int matrixEspValue2 = blendedEspColor22 >> 8 & 0xFF;
      int matrixEspValue3 = blendedEspColor22 & 0xFF;
      releaseRightValue = 0xFF000000 | blendedEspColor22 & 16777215;
      espFillColor = 1610612736 | matrixEspValue << 16 | matrixEspValue2 << 8 | matrixEspValue3;
      int min4 = Math.min(255, matrixEspValue + (255 - matrixEspValue) / 3);
      int min5 = Math.min(255, matrixEspValue2 + (255 - matrixEspValue2) / 3);
      int min6 = Math.min(255, matrixEspValue3 + (255 - matrixEspValue3) / 3);
      releaseRightAccentColor = -2147483648 | min4 << 16 | min5 << 8 | min6;
      espOutlineColor = 0xFF000000 | matrixEspValue << 16 | matrixEspValue2 << 8 | matrixEspValue3;
   }
}
