package com.corz.client.utils;

import java.util.List;
import java.util.Locale;

public final class PreventsDipUtils {

   public static List<PreventsDipUtils.Box> getItems() {
      return List.of(new PreventsDipUtils.Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
   }

   public static String formatPreventsDip(String preventsDipText, List<PreventsDipUtils.Box> list) {
      double preventsDipValue = getAmount(list, true);
      return String.format(Locale.ROOT, "%s(top=%.4f centreLineDip=%.4f sneakClipPrevents=%s)", preventsDipText, getMax(list, 0.5, 0.5), preventsDipValue, canFlag(preventsDipValue));
   }

   public static boolean canFlag(double preventsDipValue, double preventsDipValue2) {
      return preventsDipValue - 0.3 >= 0.125 && preventsDipValue + 0.3 <= 0.875 && preventsDipValue2 - 0.3 >= 0.125 && preventsDipValue2 + 0.3 <= 0.875;
   }

   public static List<PreventsDipUtils.Box> getItemsList() {
      double preventsDipValue = 0.125;
      double preventsDipValue2 = 0.875;
      double preventsDipValue3 = 0.6875;
      double preventsDipValue4 = 0.625;
      return List.of(
         new PreventsDipUtils.Box(0.0, preventsDipValue4, 0.0, preventsDipValue, 1.0, 1.0),
         new PreventsDipUtils.Box(preventsDipValue2, preventsDipValue4, 0.0, 1.0, 1.0, 1.0),
         new PreventsDipUtils.Box(preventsDipValue, preventsDipValue4, 0.0, preventsDipValue2, 1.0, preventsDipValue),
         new PreventsDipUtils.Box(preventsDipValue, preventsDipValue4, preventsDipValue2, preventsDipValue2, 1.0, 1.0),
         new PreventsDipUtils.Box(preventsDipValue, preventsDipValue4, preventsDipValue, preventsDipValue2, preventsDipValue3, preventsDipValue2),
         new PreventsDipUtils.Box(0.25, 0.25, 0.25, 0.75, preventsDipValue4, 0.75),
         new PreventsDipUtils.Box(0.375, 0.0, 0.375, 0.625, 0.25, 0.625)
      );
   }

   private PreventsDipUtils() {
   }

   public static boolean canFlag(double preventsDipLimit) {
      return preventsDipLimit > 0.6;
   }

   public static double getAmount(List<PreventsDipUtils.Box> list, boolean preventsDipFlag) {
      double preventsDipBound = 0.0;

      for (int i = 0; i <= 100; i++) {
         double preventsDipValue = i / 100.0;
         double preventsDipValue2 = preventsDipFlag ? getMax(list, preventsDipValue, 0.5) : getMax(list, 0.5, preventsDipValue);
         preventsDipBound = Math.max(preventsDipBound, 1.0 - preventsDipValue2);
      }

      return preventsDipBound;
   }

   public static double getAmount(double preventsDipValue, double preventsDipValue2) {
      return 1.0 - getMax(getItemsList(), preventsDipValue, preventsDipValue2);
   }

   public static double getMax(List<PreventsDipUtils.Box> list, double preventsDipValue, double preventsDipValue2) {
      double z1 = preventsDipValue - 0.3;
      double x0 = preventsDipValue + 0.3;
      double z0 = preventsDipValue2 - 0.3;
      double x1 = preventsDipValue2 + 0.3;
      double preventsDipBound = 0.0;

      for (PreventsDipUtils.Box box : list) {
         if (box.canMinMax(z1, z0, x0, x1)) {
            preventsDipBound = Math.max(preventsDipBound, box.maxY);
         }
      }

      return preventsDipBound;
   }

   public record Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      boolean canMinMax(double z1, double z0, double x0, double x1) {
         return x0 > this.minX && z1 < this.maxX && x1 > this.minZ && z0 < this.maxZ;
      }
   }
}
