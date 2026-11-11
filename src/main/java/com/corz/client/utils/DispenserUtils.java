package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import com.corz.client.utils.PistonStickyUtils;

public final class DispenserUtils {
   public static List<int[]> getItems(int dispenserValue, int dispenserValue2, int dispenserValue3, int dispenserValue4, int dispenserLimit) {
      int[] dispenserValues = PistonStickyUtils.getValues(dispenserValue4);
      ArrayList arrayList = new ArrayList();

      for (int i = 1; i <= dispenserLimit; i++) {
         arrayList.add(new int[]{dispenserValue + dispenserValues[0] * i, dispenserValue2 + dispenserValues[1] * i, dispenserValue3 + dispenserValues[2] * i});
      }

      return arrayList;
   }

   public static List<int[]> getItemsForInt(int dispenserValue, int dispenserValue2, int dispenserValue3, int dispenserValue4, int dispenserValue5) {
      int[] dispenserValues = PistonStickyUtils.getValues(dispenserValue4);
      ArrayList arrayList = new ArrayList();

      for (int i = dispenserValue5; i >= 1; i--) {
         arrayList.add(new int[]{dispenserValue + dispenserValues[0] * i, dispenserValue2 + dispenserValues[1] * i, dispenserValue3 + dispenserValues[2] * i});
      }

      return arrayList;
   }

   public static int[] getValues(int dispenserValue, int dispenserValue2, int dispenserValue3, int dispenserValue4) {
      int[] dispenserValues = PistonStickyUtils.getValues(getDispenser(dispenserValue4));
      return new int[]{dispenserValue + dispenserValues[0], dispenserValue2 + dispenserValues[1], dispenserValue3 + dispenserValues[2]};
   }

   public static int getDispenser(int dispenserValue) {
      return PistonStickyUtils.getValue(dispenserValue, PistonStickyUtils.Kind.DISPENSER);
   }

   public static boolean canFlag(int dispenserValue) {
      return PistonStickyUtils.canFlag(dispenserValue);
   }

   public static int getMax(int dispenserBound) {
      return Math.max(0, Math.min(dispenserBound, 4));
   }

   private DispenserUtils() {
   }
}
