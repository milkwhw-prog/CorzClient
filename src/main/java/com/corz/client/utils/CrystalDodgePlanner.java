package com.corz.client.utils;

import java.util.function.Predicate;
import net.minecraft.util.math.Vec3d;

public final class CrystalDodgePlanner {
   private static final double[][] DODGE_DIRECTIONS = buildDirectionTable();
   public static final int ONE = 1;

   private static float getSafeDamage(CrystalDodgePlanner.ExposureFn exposureFn, Vec3d vec, boolean damageFlag) {
      float damageAt = exposureFn.damageAt(vec, damageFlag);
      return Float.isNaN(damageAt) ? Float.POSITIVE_INFINITY : Math.max(0.0F, damageAt);
   }

   public CrystalDodgePlanner.Dodge findBestDodge(Vec3d vec, Vec3d vec2, int constBound, Vec3d vec3, CrystalDodgePlanner.ExposureFn exposureFn, Predicate<Vec3d> predicate) {
      if (vec != null && exposureFn != null && isFiniteVec(vec)) {
         int constFactor = Math.max(1, Math.min(3, constBound));
         double constFactor2 = vec2 != null && Double.isFinite(vec2.x) ? vec2.x : 0.0;
         double constFactor3 = vec2 != null && Double.isFinite(vec2.z) ? vec2.z : 0.0;
         Vec3d vec3d = new Vec3d(vec.x + constFactor2 * constFactor * 0.546, vec.y, vec.z + constFactor3 * constFactor * 0.546);
         float damage = getSafeDamage(exposureFn, vec3d, false);
         double constLimit = damage + 0.5 * getHorizontalDistance(vec3d, vec3);
         float constValue = damage;
         double constValue2 = 0.0;
         double constValue3 = 0.0;
         boolean constFlag = false;
         boolean constFlag2 = false;

         for (double[] constValues : DODGE_DIRECTIONS) {
            Vec3d vec3d2 = new Vec3d(vec3d.x + constValues[0] * 0.22 * constFactor, vec.y, vec3d.z + constValues[1] * 0.22 * constFactor);
            if (predicate == null || predicate.test(vec3d2)) {
               float safeDamage = getSafeDamage(exposureFn, vec3d2, false);
               double constValue4 = safeDamage + 0.5 * getHorizontalDistance(vec3d2, vec3);
               if (constValue4 < constLimit) {
                  constLimit = constValue4;
                  constValue = safeDamage;
                  constValue2 = constValues[0];
                  constValue3 = constValues[1];
                  constFlag = false;
                  constFlag2 = true;
               }
            }
         }

         if (predicate == null || predicate.test(vec3d)) {
            float safeDamage3 = getSafeDamage(exposureFn, vec3d, true);
            double constValue5 = safeDamage3 + 0.5 * getHorizontalDistance(vec3d, vec3);
            if (constValue5 < constLimit) {
               constValue = safeDamage3;
               constValue2 = 0.0;
               constValue3 = 0.0;
               constFlag = true;
               constFlag2 = true;
            }
         }

         return constFlag2 && !(damage - constValue < getMinWorthwhileReduction(damage)) ? new CrystalDodgePlanner.Dodge(constValue2, constValue3, constFlag, constFactor, constValue, damage) : null;
      } else {
         return null;
      }
   }

   static float getMinWorthwhileReduction(float constValue) {
      return !(constValue < Float.POSITIVE_INFINITY) ? 1.5F : Math.min(1.5F, Math.max(0.75F, 0.15F * constValue));
   }

   private static double getHorizontalDistance(Vec3d vec, Vec3d vec2) {
      return vec2 != null && isFiniteVec(vec2) ? Math.hypot(vec.x - vec2.x, vec.z - vec2.z) : 0.0;
   }

   private static double[][] buildDirectionTable() {
      double[][] doubles = new double[8][];

      for (int i = 0; i < 8; i++) {
         double angle = Math.toRadians(i * 360.0 / 8.0);
         doubles[i] = new double[]{-Math.sin(angle), Math.cos(angle)};
      }

      return doubles;
   }

   private static boolean isFiniteVec(Vec3d vec) {
      return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
   }

   public record Dodge(double dirX, double dirZ, boolean jump, int holdTicks, float expected, float stay) {
   }

   public interface ExposureFn {
      float damageAt(Vec3d vec, boolean damageAtFlag);
   }
}
