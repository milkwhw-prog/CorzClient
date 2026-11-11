package com.corz.client.utils;

import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.LagFakeUtilsAmount;

public final class BackTrackUtils {

   public static boolean canFlag(boolean backTrackFlag, long backTrackValue, long backTrackValue2, long backTrackLimit) {
      return !backTrackFlag && backTrackValue < backTrackLimit && backTrackValue2 < backTrackLimit;
   }

   public static long getTime(long backTrackValue, long backTrackBound, long cheapHigh) {
      return Math.max(backTrackValue + Math.max(0L, backTrackBound), cheapHigh);
   }

   private BackTrackUtils() {
   }

   public static long getTime(double backTrackBound, double goalBound, double backTrackValue) {
      double max2 = Math.max(0.0, Math.min(backTrackBound, goalBound));
      double max3 = Math.max(0.0, Math.max(backTrackBound, goalBound));
      return Math.round(max2 + (max3 - max2) * LagFakeUtilsAmount.getAmount(backTrackValue, 0.0, 1.0));
   }

   public static long getTime(double backTrackValue) {
      return Math.round(backTrackValue * 4096.0);
   }

   public static Vec3d getVec3d(Vec3d vec3d, long vec3dValue, long vec3dValue2, long vec3dValue3) {
      if (vec3dValue == 0L && vec3dValue2 == 0L && vec3dValue3 == 0L) {
         return vec3d;
      } else {
         double x = vec3dValue == 0L ? vec3d.x : getAmount(getTime(vec3d.x) + vec3dValue);
         double y = vec3dValue2 == 0L ? vec3d.y : getAmount(getTime(vec3d.y) + vec3dValue2);
         double z = vec3dValue3 == 0L ? vec3d.z : getAmount(getTime(vec3d.z) + vec3dValue3);
         return new Vec3d(x, y, z);
      }
   }

   public static boolean canFlag(double backTrackValue, double backTrackLimit) {
      return backTrackLimit > backTrackValue + 0.001;
   }

   public static double getAmount(long backTrackValue) {
      return backTrackValue / 4096.0;
   }
}
