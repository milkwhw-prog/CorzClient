package com.corz.client.entity;

import com.corz.client.mixins.AccessorClientWorldPending;
import com.corz.client.mixins.AccessorPendingUpdate;
import com.corz.client.mixins.AccessorPendingUpdateManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;

public final class FloorMathUtilsBlock {
   private static long expiryTime = Long.MIN_VALUE;
   private static int handledCount;
   private static int floorMathCount;
   private static BlockPos immutable;
   private static float blastRadius;

   public static boolean canEmpty(World world, BlockPos pos2) {
      if (pos2 == null) {
         return false;
      } else {
         Long2ObjectOpenHashMap byPos = getByPos(world);
         return byPos != null && !byPos.isEmpty() && byPos.containsKey(pos2.asLong());
      }
   }

   public static void handleImmutableAmount(BlockPos pos, float amount2, long time2) {
      immutable = pos == null ? null : pos.toImmutable();
      blastRadius = amount2;
      expiryTime = time2;
   }

   public static int getValue() {
      return floorMathCount;
   }

   public static boolean canCenterBlast(BlockPos pos2) {
      BlockPos pos = immutable;
      if (pos2 == null || pos == null || ConnectionTimeUtils.getTimeTimeMillis() > expiryTime) {
         return false;
      } else if (pos2.equals(pos)) {
         return true;
      } else {
         ClientWorld world = MinecraftClient.getInstance().world;
         if (world == null) {
            return false;
         } else {
            BlockState blockState = world.getBlockState(pos2);
            float blastResistance = blockState.getBlock().getBlastResistance();
            if (!blockState.isAir() && blastResistance < 9.0F) {
               double distanceTo = Vec3d.ofCenter(pos2).distanceTo(Vec3d.ofCenter(pos));
               if (distanceTo <= blastRadius) {
                  return true;
               } else {
                  return !(distanceTo <= getAmount(blastRadius, blastResistance)) ? false : canFloorBlast(world, pos, pos2, blastRadius);
               }
            } else {
               return false;
            }
         }
      }
   }

   private static boolean canFloorBlast(World world, BlockPos pos, BlockPos pos2, float floorBlastValue) {
      Vec3d center = Vec3d.ofCenter(pos);
      Vec3d subtract = Vec3d.ofCenter(pos2).subtract(center);
      double varLength = subtract.length();
      if (varLength < 1.0E-6) {
         return true;
      } else {
         double floorBlastFactor = subtract.x / varLength;
         double floorBlastFactor2 = subtract.y / varLength;
         double floorBlastFactor3 = subtract.z / varLength;
         double floorBlastTotal = 1.3F * floorBlastValue;
         int floorBlastLimit = (int)Math.ceil(varLength / 0.3F) + 2;
         Mutable mutable = new Mutable();

         for (int i = 0; i < floorBlastLimit && floorBlastTotal > 0.0; i++) {
            double floorBlastFactor4 = i * 0.3F;
            mutable.set(MathHelper.floor(center.x + floorBlastFactor * floorBlastFactor4), MathHelper.floor(center.y + floorBlastFactor2 * floorBlastFactor4), MathHelper.floor(center.z + floorBlastFactor3 * floorBlastFactor4));
            if (!mutable.equals(pos)) {
               BlockState blockState = world.getBlockState(mutable);
               if (!blockState.isAir() || !blockState.getFluidState().isEmpty()) {
                  floorBlastTotal -= (Math.max(blockState.getBlock().getBlastResistance(), blockState.getFluidState().getBlastResistance()) + 0.3F) * 0.3F;
               }

               if (mutable.equals(pos2)) {
                  return floorBlastTotal > 0.0;
               }
            }

            floorBlastTotal -= 0.225F;
         }

         return false;
      }
   }

   public static boolean canInstanceWorld(BlockPos pos2) {
      return canEmpty(MinecraftClient.getInstance().world, pos2);
   }

   public static BlockState getStateBlock(World world, BlockPos pos2) {
      if (world != null && pos2 != null) {
         BlockState serverCorz = getServerCorz(world, pos2);
         return serverCorz == null ? world.getBlockState(pos2) : serverCorz;
      } else {
         return null;
      }
   }

   public static int getMax(int floorMathBound) {
      int maxPingMs = Math.max(0, floorMathBound);
      return Math.max(1, Math.min(4, 1 + (int)Math.ceil(maxPingMs / 100.0)));
   }

   private static BlockState getServerCorz(World world, BlockPos pos2) {
      if (world != null && pos2 != null) {
         Long2ObjectOpenHashMap byPos = getByPos(world);
         Object obj = byPos != null && !byPos.isEmpty() ? byPos.get(pos2.asLong()) : null;
         return obj == null ? null : ((AccessorPendingUpdate)obj).corzServerState();
      } else {
         return null;
      }
   }

   public static boolean canCollisionShape(World world, BlockPos pos2) {
      BlockState serverCorz = getServerCorz(world, pos2);
      return serverCorz == null ? false : world.getBlockState(pos2).getCollisionShape(world, pos2).isEmpty() && !serverCorz.getCollisionShape(world, pos2).isEmpty();
   }

   public static boolean canEmpty(World world) {
      Long2ObjectOpenHashMap byPos = getByPos(world);
      return byPos != null && !byPos.isEmpty();
   }

   public static double getAmount(float floorMathValue, float blastResistance) {
      return Math.max(0.0, (double)((1.3F * floorMathValue - (blastResistance + 0.3F) * 0.3F) / 0.74999994F));
   }

   public static boolean canUnionOffset(Box box, Box box2, Vec3d vec, Vec3d vec2, Vec3d vec3, int unionOffsetBound) {
      if (box != null && box2 != null) {
         Vec3d vec3d = vec == null ? Vec3d.ZERO : vec;
         Vec3d vec3d2 = vec2 == null ? vec3d : vec2;
         Box offset = box2.offset(vec3d2.subtract(vec3d));
         Box box3 = vec3 == null ? offset : offset.offset(vec3.multiply(Math.max(0, unionOffsetBound)));
         return box2.union(offset).union(box3).expand(0.05).intersects(box);
      } else {
         return false;
      }
   }

   public static boolean canFloorMin(ClientPlayerEntity player) {
      if (player == null) {
         return false;
      } else {
         World entityWorld = player.getEntityWorld();
         Long2ObjectOpenHashMap byPos = getByPos(entityWorld);
         if (byPos != null && !byPos.isEmpty()) {
            Box boundingBox = player.getBoundingBox();
            int floorBound = MathHelper.floor(boundingBox.minY + 0.001);
            int maxFloor = Math.max(floorBound, MathHelper.floor(boundingBox.maxY - 1.0E-7));
            int floorMinX = MathHelper.floor(boundingBox.minX);
            int max2 = Math.max(floorMinX, MathHelper.floor(boundingBox.maxX - 1.0E-7));
            int floorMinZ = MathHelper.floor(boundingBox.minZ);
            int max3 = Math.max(floorMinZ, MathHelper.floor(boundingBox.maxZ - 1.0E-7));

            for (int i = floorBound; i <= maxFloor; i++) {
               for (int j = floorMinX - 1; j <= max2 + 1; j++) {
                  for (int k = floorMinZ - 1; k <= max3 + 1; k++) {
                     if ((j < floorMinX || j > max2 || k < floorMinZ || k > max3) && byPos.containsKey(BlockPos.asLong(j, i, k))) {
                        return true;
                     }
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   public static boolean canInterpolatorLerped(BlockPos pos, int interpolatorLerpedValue) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientWorld world = instance.world;
      if (world != null && pos != null) {
         Box box = new Box(pos);

         for (Entity entity : world.getOtherEntities(instance.player, box.expand(4.0), entity2 -> entity2.isAlive() && !entity2.isSpectator() && entity2.intersectionChecked)) {
            Vec3d entityPos = entity.getEntityPos();
            PositionInterpolator interpolator = entity.getInterpolator();
            Vec3d vec3d = interpolator != null ? interpolator.getLerpedPos() : entityPos;
            Vec3d vec3d2 = new Vec3d(entityPos.x - entity.lastX, entityPos.y - entity.lastY, entityPos.z - entity.lastZ);
            if (canUnionOffset(box, entity.getBoundingBox(), entityPos, vec3d, vec3d2, interpolatorLerpedValue)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean canFlag(int floorMathValue, int floorMathLimit) {
      return floorMathLimit > 0 && floorMathValue == 0;
   }

   public static void update() {
      floorMathCount = 0;
      handledCount = 0;
   }

   public static int getValue(World world) {
      Long2ObjectOpenHashMap byPos = getByPos(world);
      return byPos == null ? 0 : byPos.size();
   }

   public static int getValueInt() {
      return getMax(EntityDamageUtils.getCachedHurtTimeLimit());
   }

   public static boolean canFloorMin(World world, Box box) {
      if (box == null) {
         return false;
      } else {
         Long2ObjectOpenHashMap byPos = getByPos(world);
         if (byPos != null && !byPos.isEmpty()) {
            int floorMinX = MathHelper.floor(box.minX);
            int maxFloor = Math.max(floorMinX, MathHelper.floor(box.maxX - 1.0E-7));
            int floorMinY = MathHelper.floor(box.minY);
            int max2 = Math.max(floorMinY, MathHelper.floor(box.maxY - 1.0E-7));
            int floorMinZ = MathHelper.floor(box.minZ);
            int max3 = Math.max(floorMinZ, MathHelper.floor(box.maxZ - 1.0E-7));

            for (int i = floorMinX; i <= maxFloor; i++) {
               for (int j = floorMinY; j <= max2; j++) {
                  for (int k = floorMinZ; k <= max3; k++) {
                     if (byPos.containsKey(BlockPos.asLong(i, j, k))) {
                        return true;
                     }
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   public static boolean canCollisionShapeForWorldBlockPos(World world, BlockPos pos2) {
      BlockState serverCorz = getServerCorz(world, pos2);
      return serverCorz == null ? false : !world.getBlockState(pos2).getCollisionShape(world, pos2).isEmpty() && serverCorz.getCollisionShape(world, pos2).isEmpty();
   }

   public static int getHandledCount() {
      return handledCount;
   }

   public static boolean canFloorMinForClientPlayerEntity(ClientPlayerEntity player) {
      if (player == null) {
         return false;
      } else {
         World entityWorld = player.getEntityWorld();
         Long2ObjectOpenHashMap byPos = getByPos(entityWorld);
         if (byPos != null && !byPos.isEmpty()) {
            Box boundingBox = player.getBoundingBox();
            int floorValue = MathHelper.floor(boundingBox.minY - 0.001);
            int floorMinX = MathHelper.floor(boundingBox.minX);
            int maxFloor = Math.max(floorMinX, MathHelper.floor(boundingBox.maxX - 1.0E-7));
            int floorMinZ = MathHelper.floor(boundingBox.minZ);
            int max2 = Math.max(floorMinZ, MathHelper.floor(boundingBox.maxZ - 1.0E-7));
            ShapeContext of = ShapeContext.of(player);
            Mutable mutable = new Mutable();
            int floorCounter = 0;
            int floorMathLimit = 0;
            double negativeInfinity = Double.NEGATIVE_INFINITY;
            double floorLimit = Double.NEGATIVE_INFINITY;

            for (int i = floorMinX; i <= maxFloor; i++) {
               for (int j = floorMinZ; j <= max2; j++) {
                  mutable.set(i, floorValue, j);
                  VoxelShape collisionShape = entityWorld.getBlockState(mutable).getCollisionShape(entityWorld, mutable, of);
                  if (!collisionShape.isEmpty()) {
                     double floorLimit2 = floorValue + collisionShape.getMax(Axis.Y);
                     if (!(floorLimit2 < boundingBox.minY - 0.002)) {
                        if (byPos.containsKey(mutable.asLong())) {
                           floorMathLimit++;
                           if (floorLimit2 > floorLimit) {
                              floorLimit = floorLimit2;
                           }
                        } else {
                           floorCounter++;
                           if (floorLimit2 > negativeInfinity) {
                              negativeInfinity = floorLimit2;
                           }
                        }
                     }
                  }
               }
            }

            return canFlag(floorCounter, floorMathLimit) || floorLimit > negativeInfinity + 0.002;
         } else {
            return false;
         }
      }
   }

   public static void handle(boolean floorMathFlag) {
      if (floorMathFlag) {
         handledCount++;
      } else {
         floorMathCount++;
      }
   }

   private static Long2ObjectOpenHashMap<?> getByPos(World world) {
      if (world instanceof ClientWorld clientWorld) {
         PendingUpdateManager corzPendingUpdates = ((AccessorClientWorldPending)clientWorld).corzPendingUpdates();
         return corzPendingUpdates == null ? null : ((AccessorPendingUpdateManager)corzPendingUpdates).corzPendingByPos();
      } else {
         return null;
      }
   }

   private FloorMathUtilsBlock() {
   }
}
