package com.corz.client.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class FloorMathUtilsDirection {
   private static final MinecraftClient floorMathMinecraftClient = MinecraftClient.getInstance();

   public static Vec3d getAdd(LivingEntity entity) {
      float yaw = entity.getYaw();
      float pitch = entity.getPitch();
      float x = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
      float sin = -MathHelper.sin(pitch * (float) (Math.PI / 180.0));
      float z = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
      Vec3d multiply = new Vec3d(x, sin, z).normalize().multiply(1.5);
      double x2 = entity.getX() - entity.lastX;
      double y = entity.isOnGround() ? 0.0 : entity.getY() - entity.lastY;
      double z2 = entity.getZ() - entity.lastZ;
      return multiply.add(x2, y, z2);
   }

   private static boolean canInstanceWorld(Vec3d vec) {
      if (floorMathMinecraftClient.world == null) {
         return false;
      } else {
         BlockPos floored = BlockPos.ofFloored(vec);
         if (!floorMathMinecraftClient.world.isChunkLoaded(floored.getX() >> 4, floored.getZ() >> 4)) {
            return false;
         } else {
            BlockState blockState = floorMathMinecraftClient.world.getBlockState(floored);
            return !blockState.getCollisionShape(floorMathMinecraftClient.world, floored).isEmpty();
         }
      }
   }

   private static boolean canCeilFloor(ClientWorld clientWorld, Mutable mutable, double ceilFloorValue, double ceilFloorValue2, double ceilFloorValue3, double[] ceilFloorValues) {
      double ceilFloorValue4 = ceilFloorValue - 0.125 + 0.001;
      double ceilFloorValue5 = ceilFloorValue + 0.125 - 0.001;
      double ceilFloorLimit = ceilFloorValue2 + 0.001;
      double ceilFloorValue6 = ceilFloorValue2 + 0.25 - 0.001;
      double ceilFloorValue7 = ceilFloorValue3 - 0.125 + 0.001;
      double ceilFloorValue8 = ceilFloorValue3 + 0.125 - 0.001;
      int floor = MathHelper.floor(ceilFloorValue4);
      int ceil2 = MathHelper.ceil(ceilFloorValue5);
      int floor10 = MathHelper.floor(ceilFloorLimit);
      int ceil3 = MathHelper.ceil(ceilFloorValue6);
      int floor11 = MathHelper.floor(ceilFloorValue7);
      int ceil4 = MathHelper.ceil(ceilFloorValue8);
      boolean ceilFloor = false;
      double ceilFloorBound = 0.0;
      double ceilFloorTotal = 0.0;
      double ceilFloorTotal2 = 0.0;
      double ceilFloorTotal3 = 0.0;
      int ceilFloorDivisor = 0;

      for (int i = floor; i < ceil2; i++) {
         for (int j = floor10; j < ceil3; j++) {
            for (int k = floor11; k < ceil4; k++) {
               mutable.set(i, j, k);
               FluidState fluidState = clientWorld.getFluidState(mutable);
               if (fluidState.isIn(FluidTags.WATER)) {
                  double ceilFloorValue12 = j + fluidState.getHeight(clientWorld, mutable);
                  if (!(ceilFloorValue12 < ceilFloorLimit)) {
                     ceilFloor = true;
                     ceilFloorBound = Math.max(ceilFloorValue12 - ceilFloorLimit, ceilFloorBound);
                     Vec3d velocity = fluidState.getVelocity(clientWorld, mutable);
                     double ceilFloorFactor = ceilFloorBound < 0.4 ? ceilFloorBound : 1.0;
                     ceilFloorTotal += velocity.x * ceilFloorFactor;
                     ceilFloorTotal2 += velocity.y * ceilFloorFactor;
                     ceilFloorTotal3 += velocity.z * ceilFloorFactor;
                     ceilFloorDivisor++;
                  }
               }
            }
         }
      }

      ceilFloorValues[0] = ceilFloorValues[1] = ceilFloorValues[2] = 0.0;
      if (ceilFloorDivisor > 0) {
         double ceilFloorFactor2 = ceilFloorTotal / ceilFloorDivisor;
         double ceilFloorFactor3 = ceilFloorTotal2 / ceilFloorDivisor;
         double ceilFloorFactor4 = ceilFloorTotal3 / ceilFloorDivisor;
         double distance = Math.sqrt(ceilFloorFactor2 * ceilFloorFactor2 + ceilFloorFactor3 * ceilFloorFactor3 + ceilFloorFactor4 * ceilFloorFactor4);
         if (distance >= 1.0E-5F) {
            ceilFloorValues[0] = ceilFloorFactor2 / distance * 0.014;
            ceilFloorValues[1] = ceilFloorFactor3 / distance * 0.014;
            ceilFloorValues[2] = ceilFloorFactor4 / distance * 0.014;
         }
      }

      return ceilFloor;
   }

   public static FloorMathUtilsDirection.Result getVecDirection(Vec3d vec, Vec3d vec2, int vecDirectionValue) {
      return getBoundingBox(vec, vec2, false, 0, null, null, false, vecDirectionValue);
   }

   public static FloorMathUtilsDirection.Result getSubtractMultiply(Vec3d vec, Vec3d vec2, double y, double subtractMultiplyValue, int subtractMultiplyLimit) {
      ArrayList arrayList = new ArrayList();
      arrayList.add(vec);
      boolean subtractMultiplyFlag = false;

      for (int i = 0; i < subtractMultiplyLimit; i++) {
         Vec3d add = vec.add(vec2);
         if (canInstanceWorld(add)) {
            subtractMultiplyFlag = true;
            vec = add;
            arrayList.add(add);
            break;
         }

         vec = add;
         arrayList.add(add);
         vec2 = vec2.multiply(subtractMultiplyValue);
         vec2 = vec2.subtract(0.0, y, 0.0);
      }

      return new FloorMathUtilsDirection.Result(arrayList, vec, subtractMultiplyFlag);
   }

   public static float getAmount(Vec3d vec, Vec3d vec2, double floorMathFactor, double floorMathFactor2) {
      double deltaX = vec2.x - vec.x;
      double deltaZ = vec2.z - vec.z;
      double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
      if (distance < 0.5) {
         return Float.NaN;
      } else {
         double deltaY = vec2.y - vec.y;
         double floorMathValue = floorMathFactor2 * distance * distance / (2.0 * floorMathFactor * floorMathFactor);
         double squaredDistanceTo = distance * distance - 4.0 * floorMathValue * (deltaY + floorMathValue);
         if (squaredDistanceTo < 0.0) {
            return Float.NaN;
         } else {
            double floorMathValue3 = (distance - Math.sqrt(squaredDistanceTo)) / (2.0 * floorMathValue);
            return (float)(-Math.toDegrees(Math.atan(floorMathValue3)));
         }
      }
   }

   public static Vec3d getEye(LivingEntity entity) {
      return new Vec3d(entity.getX(), entity.getEyeY() - 0.1F, entity.getZ());
   }

   private static boolean canFloorBubble(ClientWorld clientWorld, Mutable mutable, Vec3d vec) {
      int floorBubbleValue = MathHelper.floor(vec.x - 0.125);
      int floorBubbleLimit = MathHelper.floor(vec.x + 0.125);
      int floor = MathHelper.floor(vec.y);
      int floor2 = MathHelper.floor(vec.y + 0.25);
      int floor3 = MathHelper.floor(vec.z - 0.125);
      int floorBubbleLimit3 = MathHelper.floor(vec.z + 0.125);

      for (int i = floorBubbleValue; i <= floorBubbleLimit; i++) {
         for (int j = floor; j <= floor2; j++) {
            for (int k = floor3; k <= floorBubbleLimit3; k++) {
               if (clientWorld.getBlockState(mutable.set(i, j, k)).isOf(Blocks.BUBBLE_COLUMN)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static FloorMathUtilsDirection.Result getVecDirectionForVec3dInt(Vec3d vec, Vec3d vec2, int vecDirectionValue) {
      return getSubtractMultiply(vec, vec2, 0.03, 0.99, vecDirectionValue);
   }

   private static Box getStretchExpand(Vec3d vec, double stretchExpandValue, double y, double z) {
      return new Box(vec.x - 0.125, vec.y, vec.z - 0.125, vec.x + 0.125, vec.y + 0.25, vec.z + 0.125).stretch(stretchExpandValue, y, z).expand(1.0);
   }

   public static FloorMathUtilsDirection.Result getBoundingBox(
      Vec3d vec, Vec3d vec2, boolean boundingBoxFlag, int boundingBoxValue, Entity entity, Entity player, boolean groundFlag, int boundingBoxLimit
   ) {
      ArrayList arrayList = new ArrayList(64);
      arrayList.add(vec);
      ClientWorld world = floorMathMinecraftClient.world;
      if (world == null) {
         return new FloorMathUtilsDirection.Result(arrayList, vec, false, null, vec, vec, -1, -1.0, null, null, -1, false);
      } else {
         ShapeContext absent = ShapeContext.absent();
         ClientChunkManager chunkManager = world.getChunkManager();
         Mutable mutable = new Mutable();
         double[] doubles = new double[3];
         double boundingBoxLimit2 = world.getBottomY() - 64;
         Vec3d age2 = vec;
         double x = vec2.x;
         double y = vec2.y;
         double z = vec2.z;
         boolean boundingBoxFlag2 = boundingBoxFlag;
         boolean intersects3 = player == null || !player.getBoundingBox().intersects(getStretchExpand(vec, x, y, z));
         if (canFloorBubble(world, mutable, vec)) {
            return new FloorMathUtilsDirection.Result(arrayList, vec, false, null, vec, vec, -1, -1.0, null, null, -1, false);
         } else {
            int boundingBoxCounter = 1;

            while (boundingBoxCounter <= boundingBoxLimit) {
               double boundingBoxFactor = y - 0.03;
               double boundingBoxFactor2 = boundingBoxFlag2 ? 0.8F : 0.99F;
               x *= boundingBoxFactor2;
               y = boundingBoxFactor * boundingBoxFactor2;
               z *= boundingBoxFactor2;
               Vec3d vec3d = new Vec3d(age2.x + x, age2.y + y, age2.z + z);
               BlockHitResult blockHitResult = world.getCollisionsIncludingWorldBorder(new RaycastContext(age2, vec3d, ShapeType.COLLIDER, FluidHandling.NONE, absent));
               boolean boundingBoxFlag4 = blockHitResult.getType() != Type.MISS;
               Vec3d vec3d2 = boundingBoxFlag4 ? blockHitResult.getPos() : vec3d;
               Entity getOtherEntitiesItem3 = null;
               if (groundFlag) {
                  float max2 = Math.max(0.0F, Math.min(0.3F, (boundingBoxValue + boundingBoxCounter - 2) / 20.0F));
                  boolean boundingBoxFlag5 = intersects3;
                  double maxValue = Double.MAX_VALUE;
                  Vec3d vec3d3 = null;

                  for (Entity getOtherEntitiesItem : world.getOtherEntities(
                     entity,
                     getStretchExpand(age2, x, y, z),
                     ownerx -> ownerx.canBeHitByProjectile() && (player == null || boundingBoxFlag5 || !player.isConnectedThroughVehicle(ownerx))
                  )) {
                     Optional raycast = getOtherEntitiesItem.getBoundingBox().expand(max2).raycast(age2, vec3d2);
                     if (!raycast.isEmpty()) {
                        double squaredDistanceTo = age2.squaredDistanceTo((Vec3d)raycast.get());
                        if (squaredDistanceTo < maxValue) {
                           maxValue = squaredDistanceTo;
                           getOtherEntitiesItem3 = getOtherEntitiesItem;
                           vec3d3 = (Vec3d)raycast.get();
                        }
                     }
                  }

                  if (getOtherEntitiesItem3 != null) {
                     vec3d2 = vec3d3;
                  }
               }

               if (!boundingBoxFlag4 && getOtherEntitiesItem3 == null) {
                  age2 = vec3d;
                  arrayList.add(vec3d);
                  if (!(vec3d.y < boundingBoxLimit2)
                     && chunkManager.isChunkLoaded(MathHelper.floor(vec3d.x) >> 4, MathHelper.floor(vec3d.z) >> 4)
                     && !canFloorBubble(world, mutable, vec3d)) {
                     if (!intersects3) {
                        intersects3 = !player.getBoundingBox().intersects(getStretchExpand(vec3d, x, y, z));
                     }

                     boundingBoxFlag2 = canCeilFloor(world, mutable, vec3d.x, vec3d.y, vec3d.z, doubles);
                     if (boundingBoxFlag2) {
                        x += doubles[0];
                        y += doubles[1];
                        z += doubles[2];
                     }

                     boundingBoxCounter++;
                     continue;
                  }
                  break;
               }

               arrayList.add(vec3d2);
               double distance = Math.sqrt(x * x + y * y + z * z);
               double boundingBoxValue3 = distance < 1.0E-9 ? 1.0 : Math.min(1.0, age2.distanceTo(vec3d2) / distance);
               boolean noVar = getOtherEntitiesItem3 == null;
               return new FloorMathUtilsDirection.Result(
                  arrayList,
                  vec3d2,
                  noVar,
                  vec3d2,
                  age2,
                  vec3d,
                  boundingBoxCounter,
                  boundingBoxCounter - 1 + boundingBoxValue3,
                  noVar ? blockHitResult.getSide() : null,
                  noVar ? blockHitResult.getBlockPos() : null,
                  getOtherEntitiesItem3 == null ? -1 : getOtherEntitiesItem3.getId(),
                  true
               );
            }

            return new FloorMathUtilsDirection.Result(arrayList, age2, false, null, age2, age2, -1, -1.0, null, null, -1, false);
         }
      }
   }

   public static FloorMathUtilsDirection.Result getTouchingOwner(EnderPearlEntity enderPearlEntity, boolean touchingOwnerFlag, int touchingOwnerValue) {
      return getBoundingBox(
         enderPearlEntity.getEntityPos(), enderPearlEntity.getVelocity(), enderPearlEntity.isTouchingWater(), enderPearlEntity.age, enderPearlEntity, enderPearlEntity.getOwner(), touchingOwnerFlag, touchingOwnerValue
      );
   }

   private FloorMathUtilsDirection() {
   }

   public static final class Result {
      public final int flightTicks;
      public final Vec3d vec3d3VecMoved;
      public final Vec3d centerPos;
      public final boolean movedFlag;
      public final boolean directionVecActive;
      public final int entityValue;
      public final Vec3d targetPos;
      public final Direction direction;
      public final Vec3d endPos;
      public final BlockPos blockPos;
      public final List<Vec3d> vecVec3dList;
      public final double exactFlightTicks;

      Result(
         List<Vec3d> items,
         Vec3d vec3d,
         boolean movedFlag,
         Vec3d vec3d2,
         Vec3d vec3d3,
         Vec3d vec3d4,
         int value,
         double amount,
         Direction direction,
         BlockPos blockPos,
         int value2,
         boolean flag
      ) {
         this.vecVec3dList = items;
         this.targetPos = vec3d;
         this.movedFlag = movedFlag;
         this.endPos = vec3d2;
         this.vec3d3VecMoved = vec3d3;
         this.centerPos = vec3d4;
         this.flightTicks = value;
         this.exactFlightTicks = amount;
         this.direction = direction;
         this.blockPos = blockPos;
         this.entityValue = value2;
         this.directionVecActive = flag;
      }

      Result(List<Vec3d> list, Vec3d vec, boolean floorMathFlag) {
         this(
            list,
            vec,
            floorMathFlag,
            floorMathFlag ? vec : null,
            vec,
            vec,
            floorMathFlag ? list.size() - 1 : -1,
            floorMathFlag ? list.size() - 1 : -1.0,
            null,
            null,
            -1,
            floorMathFlag
         );
      }
   }
}
