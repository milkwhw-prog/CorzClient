package com.corz.client.utils;
import com.corz.client.utils.CrouchedBodyUtils;

public final class BlockStandBounds {

   public static double clampToStandRange(double constLimit, int constValue) {
      double minStandCoord = getMinStandCoord(constValue);
      double maxStandCoord = getMaxStandCoord(constValue);
      return constLimit < minStandCoord ? minStandCoord : (constLimit > maxStandCoord ? maxStandCoord : constLimit);
   }

   public static double distanceOutsideStandArea(double constValue, double constLimit, int constValue3, int constValue4) {
      return Math.hypot(distanceOutsideStandRange(constValue, constValue3), distanceOutsideStandRange(constLimit, constValue4));
   }

   public static double getMinStandCoord(int constValue) {
      return constValue + 0.3 + 0.01;
   }

   public static double distanceOutsideStandRange(double constLimit, int constValue) {
      double minStandCoord = getMinStandCoord(constValue);
      double maxStandCoord = getMaxStandCoord(constValue);
      return constLimit < minStandCoord ? minStandCoord - constLimit : (constLimit > maxStandCoord ? constLimit - maxStandCoord : 0.0);
   }

   public static double getMaxStandCoord(int constValue) {
      return constValue + 1.0 - 0.3 - 0.01;
   }

   private BlockStandBounds() {
   }

   public static CrouchedBodyUtils.Interval getStandInterval(int constValue, int n2) {
      return CrouchedBodyUtils.Interval.createCentered(constValue + 0.5, n2 + 0.5, 0.19);
   }

   public static boolean isInsideStandArea(double constLimit, double d3, int constValue, int n2) {
      return constLimit >= getMinStandCoord(constValue) && constLimit <= getMaxStandCoord(constValue) && d3 >= getMinStandCoord(n2) && d3 <= getMaxStandCoord(n2);
   }
}
