package com.corz.client.world;

import java.util.Arrays;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.AmountAmounts;

public final class VecOffset {
   private double offsetY;
   private double vecOffsetAmount;
   private int lastOffsetMode;
   private boolean onGround = true;
   private double rawOffsetX;
   private double vecAmount;
   private final boolean[] vecOffsetFlags = new boolean[40];
   private boolean vecOffsetActive;
   private Vec3d vec3dVec;
   private int vecOffsetValue;
   private double directionZ = 1.0;
   private double smoothedOffsetX;
   private double smoothedOffsetZ;
   private int vecCount;
   private int vecOffsetCount;
   private boolean vecActive;

   public void updateAmountVec() {
      this.vec3dVec = null;
      this.onGround = true;
      this.updateAmountFlags();
      this.vecAmount = 0.0;
      this.directionZ = 1.0;
   }

   public boolean isFlag() {
      return this.vecOffsetActive;
   }

   private Vec3d getAmount(double vecOffsetFactor) {
      double hypot = Math.hypot(this.smoothedOffsetX, this.smoothedOffsetZ);
      double vecOffsetFactor2;
      double vecOffsetFactor3;
      if (hypot >= 0.03) {
         vecOffsetFactor2 = -this.smoothedOffsetZ / hypot;
         vecOffsetFactor3 = this.smoothedOffsetX / hypot;
      } else {
         vecOffsetFactor2 = -this.directionZ;
         vecOffsetFactor3 = this.vecAmount;
      }

      return new Vec3d(vecOffsetFactor2 * vecOffsetFactor, 0.0, vecOffsetFactor3 * vecOffsetFactor);
   }

   public VecOffset.Prediction getOffsetVecData(Box box, int offsetVecValue, Vec3d vec, VecOffset.Collider collider) {
      return this.getMinMax(box, offsetVecValue, vec, collider, null);
   }

   private double getAmount(int vecOffsetFactor, boolean vecOffsetFlag) {
      double hypot = Math.hypot(this.smoothedOffsetX, this.smoothedOffsetZ);
      double vecOffsetBound = Math.min(1.2, hypot * vecOffsetFactor * (0.3 + 0.7 * Math.min(1.0, this.getAmount() / 2.0)));
      return vecOffsetFlag ? Math.min(vecOffsetBound, 0.15) : vecOffsetBound;
   }

   public void handleVecFlag(Vec3d vec3d, boolean onGround2, Vec3d vec) {
      if (vec != null) {
         double hypot = Math.hypot(vec.x, vec.z);
         if (hypot > 1.0E-6) {
            this.vecAmount = vec.x / hypot;
            this.directionZ = vec.z / hypot;
         }
      }

      if (vec3d != null && Double.isFinite(vec3d.x) && Double.isFinite(vec3d.y) && Double.isFinite(vec3d.z)) {
         this.onGround = onGround2;
         if (this.vec3dVec == null) {
            this.vec3dVec = vec3d;
            this.vecCount = 0;
            this.vecActive = false;
         } else {
            double deltaX = vec3d.x - this.vec3dVec.x;
            double deltaY = vec3d.y - this.vec3dVec.y;
            double deltaZ = vec3d.z - this.vec3dVec.z;
            double vecLimit = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            this.vecCount++;
            if (!(vecLimit <= 1.0000000000000002E-10)) {
               int vecDivisor = this.vecActive ? Math.min(this.vecCount, 2) : this.vecCount;
               this.vec3dVec = vec3d;
               this.vecCount = 0;
               this.vecActive = false;
               if (vecLimit > 4.0 * vecDivisor * vecDivisor) {
                  this.updateAmountFlags();
               } else {
                  this.handleAmountFlag(deltaX / vecDivisor, deltaY / vecDivisor, deltaZ / vecDivisor);
               }
            } else if (this.vecCount < 3) {
               this.handleFlags(false);
            } else {
               this.vecActive = true;
               this.handleAmountFlag(0.0, 0.0, 0.0);
            }
         }
      }
   }

   private void handleAmountFlag(double amount, double amount2, double deltaZ) {
      double vecOffsetFactor = this.canAmount(amount, deltaZ) ? 0.85 : 0.6;
      this.smoothedOffsetX = vecOffsetFactor * amount + (1.0 - vecOffsetFactor) * this.smoothedOffsetX;
      this.smoothedOffsetZ = vecOffsetFactor * deltaZ + (1.0 - vecOffsetFactor) * this.smoothedOffsetZ;
      this.offsetY = amount2;
      double hypot = Math.hypot(amount, deltaZ);
      double hypot2 = Math.hypot(this.rawOffsetX, this.vecOffsetAmount);
      this.vecOffsetActive = hypot < 0.9 * hypot2 && hypot2 > 0.05;
      boolean vecOffsetFlag = false;
      if (hypot > 0.06) {
         double vecOffsetLimit = this.vecAmount * deltaZ - this.directionZ * amount;
         int value = vecOffsetLimit > 1.0E-9 ? 1 : (vecOffsetLimit < -1.0E-9 ? -1 : 0);
         if (value != 0) {
            vecOffsetFlag = this.lastOffsetMode != 0 && value != this.lastOffsetMode;
            this.lastOffsetMode = value;
         }
      }

      this.handleFlags(vecOffsetFlag);
      this.rawOffsetX = amount;
      this.vecOffsetAmount = deltaZ;
   }

   public double getAmount() {
      return this.vecOffsetCount * 20.0 / 40.0;
   }

   public Vec3d getAmountSmoothedOffsetX() {
      return new Vec3d(this.smoothedOffsetX, this.offsetY, this.smoothedOffsetZ);
   }

   public VecOffset.Prediction getMinMax(Box box2, int vecOffsetBound, Vec3d vec, VecOffset.Collider collider, AmountAmounts amountAmounts) {
      Vec3d vec3d = vec == null ? Vec3d.ZERO : vec;
      int maxBox = Math.max(0, vecOffsetBound);
      int vecOffsetLimit = Math.min(maxBox, 8);
      double x = this.smoothedOffsetX + vec3d.x;
      double y = this.offsetY + vec3d.y;
      double z = this.smoothedOffsetZ + vec3d.z;
      boolean vecOffsetFlag = !this.onGround || vec3d.y > 0.0;
      boolean vecOffsetFlag2 = vecOffsetFlag;
      Box box = box2;

      for (int i = 0; i < vecOffsetLimit; i++) {
         Vec3d vec3d2 = new Vec3d(x, y, z);
         Vec3d vec3d3 = collider == null ? vec3d2 : collider.getVec3d(box, vec3d2);
         if (vec3d3 == null) {
            vec3d3 = vec3d2;
         }

         box = box.offset(vec3d3);
         boolean vecOffsetFlag3 = Math.abs(vec3d3.y - vec3d2.y) > 1.0E-6;
         if (Math.abs(vec3d3.x - vec3d2.x) > 1.0E-6) {
            x = 0.0;
         }

         if (Math.abs(vec3d3.z - vec3d2.z) > 1.0E-6) {
            z = 0.0;
         }

         if (vecOffsetFlag3) {
            if (vec3d2.y < 0.0) {
               vecOffsetFlag2 = false;
            }

            y = 0.0;
         } else if (vec3d3.y < -1.0E-6) {
            vecOffsetFlag2 = true;
         }

         y = (y - 0.08) * 0.98;
         if (vecOffsetFlag2) {
            x *= 0.91;
            z *= 0.91;
         } else if (this.vecOffsetActive) {
            x *= 0.6;
            z *= 0.6;
         }
      }

      if (amountAmounts != null && !vecOffsetFlag && vecOffsetLimit > 0) {
         double vecOffsetValue = (box2.minX + box2.maxX) * 0.5;
         double vecOffsetValue2 = (box2.minZ + box2.maxZ) * 0.5;
         double vecOffsetValue3 = (box.minX + box.maxX) * 0.5;
         double vecOffsetValue4 = (box.minZ + box.maxZ) * 0.5;
         double deltaVar = vecOffsetValue3 - vecOffsetValue;
         double deltaVar2 = vecOffsetValue4 - vecOffsetValue2;
         double[] vecOffsetValues = amountAmounts.getAmount(deltaVar, deltaVar2, this.smoothedOffsetX, this.smoothedOffsetZ, maxBox);
         if (vecOffsetValues != null && vecOffsetValues.length >= 2 && Double.isFinite(vecOffsetValues[0]) && Double.isFinite(vecOffsetValues[1])) {
            Vec3d vec3d4 = new Vec3d(vecOffsetValues[0] - deltaVar, 0.0, vecOffsetValues[1] - deltaVar2);
            Vec3d vec3d5 = collider == null ? vec3d4 : collider.getVec3d(box, vec3d4);
            if (vec3d5 != null) {
               box = box.offset(vec3d5.x, 0.0, vec3d5.z);
            }
         }
      }

      double vecOffsetFactor = this.getAmount(maxBox, vec3d.y > 0.0);
      Vec3d vec3d6 = new Vec3d((box.minX + box.maxX) * 0.5, box.minY, (box.minZ + box.maxZ) * 0.5);
      return new VecOffset.Prediction(vec3d6, box, new Vec3d(x, y, z), vecOffsetFlag2, vecOffsetFactor, this.getAmount(vecOffsetFactor));
   }

   private boolean canAmount(double vecOffsetFactor, double deltaZ) {
      return Math.hypot(vecOffsetFactor, deltaZ) > 0.06 && Math.hypot(this.smoothedOffsetX, this.smoothedOffsetZ) > 0.06 && vecOffsetFactor * this.smoothedOffsetX + deltaZ * this.smoothedOffsetZ < 0.0;
   }

   private void handleFlags(boolean vecOffsetFlag) {
      if (this.vecOffsetFlags[this.vecOffsetValue]) {
         this.vecOffsetCount--;
      }

      this.vecOffsetFlags[this.vecOffsetValue] = vecOffsetFlag;
      if (vecOffsetFlag) {
         this.vecOffsetCount++;
      }

      this.vecOffsetValue = (this.vecOffsetValue + 1) % 40;
   }

   private void updateAmountFlags() {
      this.smoothedOffsetX = 0.0;
      this.smoothedOffsetZ = 0.0;
      this.offsetY = 0.0;
      this.rawOffsetX = 0.0;
      this.vecOffsetAmount = 0.0;
      this.vecOffsetActive = false;
      this.lastOffsetMode = 0;
      Arrays.fill(this.vecOffsetFlags, false);
      this.vecOffsetCount = 0;
      this.vecOffsetValue = 0;
      this.vecCount = 0;
      this.vecActive = false;
   }

   public Vec3d getVec3d(int vec3dValue) {
      return this.getAmount(this.getAmount(vec3dValue));
   }

   public double getAmount(int vecOffsetBound) {
      return this.getAmount(Math.max(0, vecOffsetBound), false);
   }

   public interface Collider {
      Vec3d getVec3d(Box box, Vec3d vec);
   }

   public record Prediction(Vec3d feet, Box box, Vec3d velocity, boolean airborne, double sigma, Vec3d lateral) {
   }
}
