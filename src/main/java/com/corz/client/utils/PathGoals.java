package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HeightDeltaMath;
import com.corz.client.utils.AStarPathfinder;
import com.corz.client.utils.PreferAnchorData;

public final class PathGoals {
   private static final double SQRT_TWO = Math.sqrt(2.0);
   private static final double SQRT_HALF = Math.sqrt(0.5);

   public static PathGoals.AttackGoal createMeleeGoal(Vec3d vec, Vec3d vec2, PathGoals.Los los, int byteHashValue) {
      double byteHashValue2 = 0.3;
      double min3 = Math.min(vec.y + 1.8 - 0.1, vec.y + 0.45);
      PathGoals.AttackGoal attack = new PathGoals.AttackGoal(
         true,
         vec,
         vec2,
         vec.x - byteHashValue2,
         vec.y,
         vec.z - byteHashValue2,
         vec.x + byteHashValue2,
         vec.y + 1.8,
         vec.z + byteHashValue2,
         vec.x,
         min3,
         vec.z,
         2.8,
         byteHashValue2,
         vec.y,
         los,
         byteHashValue
      );
      attack.setBodyBox(vec.x - byteHashValue2, vec.y, vec.z - byteHashValue2, vec.x + byteHashValue2, vec.y + 1.8, vec.z + byteHashValue2);
      return attack;
   }

   public static double getVerticalCost(double hashByteValue, double groundY) {
      return Double.isFinite(hashByteValue) && Double.isFinite(groundY) ? HeightDeltaMath.getHeightDeltaCost(hashByteValue - groundY) : HeightDeltaMath.getHeightDeltaCost(Double.NaN);
   }

   static double getPlanarDistance(double hashByteValue, double hashByteValue2, double hashByteValue3, double hashByteValue4) {
      return Math.hypot(hashByteValue - hashByteValue3, hashByteValue2 - hashByteValue4);
   }

   public static AStarPathfinder.Goal createCellGoal(Vec3d vec) {
      return new PathGoals.CellGoal(vec);
   }

   public static PathGoals.AttackGoal createCrystalGoal(Vec3d vec, Vec3d vec2, PathGoals.Los los, int hashByteValue) {
      PathGoals.AttackGoal byteHash = createCubeAreaGoal(vec, vec2, los, hashByteValue);
      double groundY = vec.y - 1.0;
      byteHash.maxFeetY = Double.isFinite(groundY) ? groundY + 0.6 : Double.POSITIVE_INFINITY;
      return byteHash;
   }

   public static PathGoals.AttackGoal createCubeAreaGoal(Vec3d vec, Vec3d vec2, PathGoals.Los los, int byteHashValue) {
      double byteHashValue2 = 1.0;
      PathGoals.AttackGoal attack = new PathGoals.AttackGoal(
         false,
         vec,
         vec2,
         vec.x - byteHashValue2,
         vec.y - byteHashValue2,
         vec.z - byteHashValue2,
         vec.x + byteHashValue2,
         vec.y + byteHashValue2,
         vec.z + byteHashValue2,
         vec.x,
         vec.y,
         vec.z,
         2.8,
         byteHashValue2,
         vec.y - byteHashValue2,
         los,
         byteHashValue
      );
      double byteHashValue3 = 0.5;
      attack.setBodyBox(vec.x - byteHashValue3, vec.y - byteHashValue2, vec.z - byteHashValue3, vec.x + byteHashValue3, vec.y + byteHashValue2, vec.z + byteHashValue3);
      return attack;
   }

   public static AStarPathfinder.Goal createGoalForPreference(PreferAnchorData preferAnchorData, PathGoals.Los los, int anchorPreferValue) {
      if (preferAnchorData != null && preferAnchorData.anchor() != null) {
         return (AStarPathfinder.Goal)(switch (preferAnchorData.kind()) {
            case MELEE -> createMeleeGoal(preferAnchorData.anchor(), preferAnchorData.prefer(), los, anchorPreferValue);
            case MELEE_LOW -> createMeleeGoalWithFloor(preferAnchorData.anchor(), preferAnchorData.prefer(), preferAnchorData.anchor().y, los, anchorPreferValue);
            case CRYSTAL -> createCrystalGoal(preferAnchorData.anchor(), preferAnchorData.prefer(), los, anchorPreferValue);
            case CELL -> createCellGoal(preferAnchorData.anchor());
         });
      } else {
         return null;
      }
   }

   public static PathGoals.AttackGoal createMeleeGoalWithFloor(Vec3d vec, Vec3d vec2, double hashByteValue, PathGoals.Los los, int byteHashValue) {
      PathGoals.AttackGoal byteHash = createMeleeGoal(vec, vec2, los, byteHashValue);
      byteHash.maxFeetY = Double.isFinite(hashByteValue) ? hashByteValue + 0.6 : Double.POSITIVE_INFINITY;
      return byteHash;
   }

   private PathGoals() {
   }

   public static final class AttackGoal implements AStarPathfinder.Goal {
      private double bodyMinY;
      private final double aimZ;
      private final Vec3d preferredPos;
      private double bodyMinX;
      private final double boxMinZ;
      private final Vec3d targetPos;
      private final boolean meleeGoal;
      private int remainingChecks;
      private final double boxMaxY;
      private final double attackValue;
      private final double aimX;
      private final double boxMaxZ;
      private double bodyMaxX;
      private int performedChecks;
      private final PathGoals.Los lineOfSight;
      private final double boxHalfExtent;
      private final double boxMinX;
      private final double boxMaxX;
      private double bodyMaxY;
      private double bodyMaxZ;
      private final double aimY;
      private final double targetY;
      private double maxFeetY;
      private final double goalRadius;
      private double bodyMinZ;
      private final Long2ByteOpenHashMap lineOfSightCache = new Long2ByteOpenHashMap();

      public boolean isMeleeGoal() {
         return this.meleeGoal;
      }

      public double getDistanceToBox(double attackValue, double feetYOffset, double attackValue3) {
         double attackFactor = Math.max(0.0, Math.max(this.boxMinX - attackValue, attackValue - this.boxMaxX));
         double max2 = Math.max(0.0, Math.max(this.attackValue - feetYOffset, feetYOffset - this.boxMaxY));
         double max3 = Math.max(0.0, Math.max(this.boxMinZ - attackValue3, attackValue3 - this.boxMaxZ));
         return Math.sqrt(attackFactor * attackFactor + max2 * max2 + max3 * max3);
      }

      public int getPerformedChecks() {
         return this.performedChecks;
      }

      @Override
      public double getHeuristicCost(int sqrtVecValue, double sqrtVecValue2, int sqrtVecValue3) {
         double sqrtVecLimit = PathGoals.getPlanarDistance(sqrtVecValue + 0.5, sqrtVecValue3 + 0.5, this.targetPos.x, this.targetPos.z) - this.goalRadius - this.boxHalfExtent * PathGoals.SQRT_TWO;
         return sqrtVecLimit > 0.0 ? sqrtVecLimit * 3.6 : 0.0;
      }

      @Override
      public double getClosenessCost(int attackValue, double groundY, int z2) {
         return Double.isFinite(groundY) && !this.overlapsBody(attackValue, groundY, z2) ? this.getDistanceToBox(attackValue + 0.5, groundY + 1.62, z2 + 0.5) : Double.MAX_VALUE;
      }

      private boolean overlapsBody(int attackValue, double groundY, int z2) {
         double attackValue4 = 0.3;
         double attackValue5 = attackValue + 0.5;
         double attackValue6 = z2 + 0.5;
         return attackValue5 - attackValue4 < this.bodyMaxX
            && attackValue5 + attackValue4 > this.bodyMinX
            && groundY < this.bodyMaxY
            && groundY + 1.8 > this.bodyMinY
            && attackValue6 - attackValue4 < this.bodyMaxZ
            && attackValue6 + attackValue4 > this.bodyMinZ;
      }

      public double getMaxFeetY() {
         return this.maxFeetY;
      }

      @Override
      public double getArrivalCost(int vecValue, double hashByteValue, int vecValue3) {
         double vecTotal = 0.0;
         if (this.preferredPos != null) {
            vecTotal += 1.0 * PathGoals.getPlanarDistance(vecValue + 0.5, vecValue3 + 0.5, this.preferredPos.x, this.preferredPos.z);
         }

         return vecTotal + PathGoals.getVerticalCost(hashByteValue, this.targetY);
      }

      @Override
      public boolean isGoalReached(int attackValue, double attackLimit, int z2) {
         if (!Double.isFinite(attackLimit)) {
            return false;
         } else if (attackLimit > this.maxFeetY + 1.0E-9) {
            return false;
         } else {
            double attackValue3 = attackValue + 0.5;
            double feetYOffset = attackLimit + 1.62;
            double attackValue4 = z2 + 0.5;
            if (this.getDistanceToBox(attackValue3, feetYOffset, attackValue4) > this.goalRadius + 1.0E-9) {
               return false;
            } else {
               return this.overlapsBody(attackValue, attackLimit, z2) ? false : this.hasLineOfSight(attackValue, attackLimit, z2, attackValue3, feetYOffset, attackValue4);
            }
         }
      }

      void setBodyBox(double amount, double attackValue, double amount2, double amount3, double amount4, double amount5) {
         this.bodyMinX = amount;
         this.bodyMinY = attackValue;
         this.bodyMinZ = amount2;
         this.bodyMaxX = amount3;
         this.bodyMaxY = amount4;
         this.bodyMaxZ = amount5;
      }

      AttackGoal(
         boolean flag,
         Vec3d vec3d,
         Vec3d vec3d2,
         double amount,
         double attackValue,
         double amount2,
         double amount3,
         double amount4,
         double amount5,
         double attackValue2,
         double min,
         double attackValue3,
         double amount6,
         double amount7,
         double attackValue4,
         PathGoals.Los handler,
         int attackBound
      ) {
         this.maxFeetY = Double.POSITIVE_INFINITY;
         this.meleeGoal = flag;
         this.targetPos = vec3d;
         this.preferredPos = vec3d2;
         this.boxMinX = amount;
         this.attackValue = attackValue;
         this.boxMinZ = amount2;
         this.boxMaxX = amount3;
         this.boxMaxY = amount4;
         this.boxMaxZ = amount5;
         this.aimX = attackValue2;
         this.aimY = min;
         this.aimZ = attackValue3;
         this.goalRadius = amount6;
         this.boxHalfExtent = amount7;
         this.targetY = attackValue4;
         this.lineOfSight = handler;
         this.remainingChecks = Math.max(0, attackBound);
      }

      private boolean hasLineOfSight(int byteValue, double hashRegistryValue, int z2, double losValue, double feetYOffset, double byteValue6) {
         if (this.lineOfSight == null) {
            return true;
         } else {
            long packPosKey = AStarPathfinder.packPosKey(byteValue, AStarPathfinder.toBlockY(hashRegistryValue), z2);
            int long2byteopenhashmap = this.lineOfSightCache.get(packPosKey);
            if (long2byteopenhashmap != 0) {
               return long2byteopenhashmap == 1;
            } else {
               boolean byteMax;
               if (this.remainingChecks > 0) {
                  this.remainingChecks--;
                  this.performedChecks++;
                  byteMax = this.lineOfSight.clear(losValue, feetYOffset, byteValue6, this.aimX, this.aimY, this.aimZ);
               } else {
                  double byteFactor = this.aimX - losValue;
                  double byteFactor2 = this.aimY - feetYOffset;
                  double byteFactor3 = this.aimZ - byteValue6;
                  byteMax = Math.sqrt(byteFactor * byteFactor + byteFactor2 * byteFactor2 + byteFactor3 * byteFactor3) < 2.0;
               }

               this.lineOfSightCache.put(packPosKey, (byte)(byteMax ? 1 : 2));
               return byteMax;
            }
         }
      }
   }

   static final class CellGoal implements AStarPathfinder.Goal {
      private final int cellZ;
      private final double targetX;
      private final int cellX;
      private final double targetZ;
      private final double targetY;

      CellGoal(Vec3d vec) {
         this.targetX = vec.x;
         this.targetY = vec.y;
         this.targetZ = vec.z;
         this.cellX = (int)Math.floor(vec.x);
         this.cellZ = (int)Math.floor(vec.z);
      }

      @Override
      public double getHeuristicCost(int sqrtVecValue, double sqrtVecValue2, int sqrtVecValue3) {
         double sqrtVecLimit = PathGoals.getPlanarDistance(sqrtVecValue + 0.5, sqrtVecValue3 + 0.5, this.targetX, this.targetZ) - PathGoals.SQRT_HALF;
         return sqrtVecLimit > 0.0 ? sqrtVecLimit * 3.6 : 0.0;
      }

      @Override
      public boolean isGoalReached(int cellGoalValue, double cellGoalValue2, int cellGoalValue3) {
         boolean cellGoalFlag = cellGoalValue == this.cellX && cellGoalValue3 == this.cellZ || PathGoals.getPlanarDistance(cellGoalValue + 0.5, cellGoalValue3 + 0.5, this.targetX, this.targetZ) <= 0.35;
         return !cellGoalFlag ? false : !Double.isFinite(this.targetY) || Math.abs(cellGoalValue2 - this.targetY) <= 1.1;
      }

      @Override
      public double getClosenessCost(int cellGoalValue, double groundY, int cellGoalValue3) {
         double cellGoalFactor = PathGoals.getPlanarDistance(cellGoalValue + 0.5, cellGoalValue3 + 0.5, this.targetX, this.targetZ);
         if (Double.isFinite(this.targetY) && Double.isFinite(groundY)) {
            double cellGoalFactor2 = groundY - this.targetY;
            return Math.sqrt(cellGoalFactor * cellGoalFactor + cellGoalFactor2 * cellGoalFactor2);
         } else {
            return cellGoalFactor;
         }
      }

      @Override
      public double getArrivalCost(int vecValue, double vecValue2, int vecValue3) {
         return 0.0;
      }
   }

   public interface Los {
      PathGoals.Los INSTANCE = (losValue, losValue2, losValue3, losValue4, losValue5, losValue6) -> true;

      boolean clear(double losValue, double feetYOffset, double losValue3, double losValue4, double losValue5, double losValue6);
   }
}
