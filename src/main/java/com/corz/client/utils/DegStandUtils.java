package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.corz.client.utils.ProofSupportUtils;

public final class DegStandUtils {

   private DegStandUtils() {
   }

   public static double getAmount(List<Float> list, float degStandValue) {
      double degStandTotal = 0.0;
      float degStandValue2 = degStandValue;
      if (list != null) {
         for (Float float2 : list) {
            if (float2 != null) {
               degStandTotal += getAmount(degStandValue2, float2);
               degStandValue2 = float2;
            }
         }
      }

      return degStandTotal;
   }

   private static boolean canOrderTarget(DegStandUtils.Item item, DegStandUtils.Item item2) {
      if (item2 == null) {
         return true;
      } else {
         return item.order() != item2.order() ? item.order() < item2.order() : item.target() < item2.target();
      }
   }

   public static double getStandDeg(
      DegStandUtils.Item item2,
      double standDegValue,
      double standDegValue2,
      long standDegValue3,
      boolean standDegFlag,
      float degStandValue,
      long standDegValue5,
      double standDegBound
   ) {
      double standDegValue6 = standDegFlag && item2.stand() == standDegValue3
         ? 0.0
         : Math.hypot(
            ProofSupportUtils.getValueInt(item2.stand()) + 0.5 - standDegValue, ProofSupportUtils.getValueForLong(item2.stand()) + 0.5 - standDegValue2
         );
      double standDegValue7 = getAmount(degStandValue, item2.headingDeg()) / 45.0;
      double standDegValue8 = item2.material() == standDegValue5 ? 0.0 : Math.max(0.0, standDegBound);
      return standDegValue6 + standDegValue7 + standDegValue8;
   }

   public static <T> List<T> getStandDeg(
      List<T> list, Function<T, DegStandUtils.Item> function, double standDegValue, double standDegValue2, float standDegValue3, long standDegValue4, double standDegBound
   ) {
      var arrayList = new ArrayList<T>();
      if (list != null) {
         for (T t : list) {
            if (t != null) {
               arrayList.add(t);
            }
         }
      }

      var arrayList2 = new ArrayList(arrayList.size());
      double view2 = standDegValue;
      double standDegValue6 = standDegValue2;
      float degStandValue = standDegValue3;
      long materialSwapCost2 = standDegValue4;
      long stand7 = 0L;
      boolean standDegFlag = false;

      while (!arrayList.isEmpty()) {
         int direction = -1;
         double standDeg9 = 0.0;
         DegStandUtils.Item item = null;

         for (int i = 0; i < arrayList.size(); i++) {
            DegStandUtils.Item apply = (DegStandUtils.Item)function.apply(arrayList.get(i));
            double standDeg = getStandDeg(apply, view2, standDegValue6, stand7, standDegFlag, degStandValue, materialSwapCost2, standDegBound);
            if (direction < 0 || standDeg < standDeg9 - 1.0E-6 || standDeg <= standDeg9 + 1.0E-6 && canOrderTarget(apply, item)) {
               direction = i;
               standDeg9 = standDeg;
               item = apply;
            }
         }

         arrayList2.add(arrayList.remove(direction));
         view2 = ProofSupportUtils.getValueInt(item.stand()) + 0.5;
         standDegValue6 = ProofSupportUtils.getValueForLong(item.stand()) + 0.5;
         stand7 = item.stand();
         standDegFlag = true;
         degStandValue = item.headingDeg();
         materialSwapCost2 = item.material();
      }

      return arrayList2;
   }

   public static float getAmount(float degStandValue) {
      float degStandTotal = degStandValue % 360.0F;
      if (degStandTotal >= 180.0F) {
         degStandTotal -= 360.0F;
      }

      if (degStandTotal < -180.0F) {
         degStandTotal += 360.0F;
      }

      return degStandTotal;
   }

   public static double getAmount(float degStandValue, float float2) {
      return Math.abs(getAmount(float2 - degStandValue));
   }

   public record Item(long target, long stand, float headingDeg, long material, int order) {
   }
}
