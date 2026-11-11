package com.corz.client.entity;

import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.inventory.LivingEntityHelperVisible;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.PosVecUtils;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class WorldCollisionHelper implements PosVecUtils.Terrain {
   public static final int UNUSED_ONE = 1;
   private long lastUpdateTime;
   private final Mutable scratchPos;
   private long lastCacheTime;
   private final HashMap<Long, Boolean> solidCache = new HashMap<>();
   private World world;

   @Override
   public boolean solid(int solidValue, int i, int z) {
      World world = this.world;
      if (world == null) {
         return false;
      } else {
         long long2 = BlockPos.asLong(solidValue, i, z);
         Boolean solid = this.solidCache.get(long2);
         if (solid != null) {
            return solid;
         } else {
            boolean finish;
            if (!world.isChunkLoaded(solidValue >> 4, z >> 4)) {
               finish = true;
            } else {
               this.scratchPos.set(solidValue, i, z);
               BlockState blockState = world.getBlockState(this.scratchPos);
               finish = !blockState.getCollisionShape(world, this.scratchPos).isEmpty();
            }

            if (this.solidCache.size() < 8192) {
               this.solidCache.put(long2, finish);
            }

            return finish;
         }
      }
   }

   public static Vec3d getTopCenter(BlockPos pos) {
      return new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
   }

   public static double computeAdjustedValue(double blockPosValue, double posVecLimit, double pilotRenderLimit) {
      int valueInt = PosVecUtils.getValueInt(posVecLimit);
      return blockPosValue + Math.max(0.0, PosVecUtils.getAmountDouble(posVecLimit, valueInt)) - pilotRenderLimit;
   }

   public EndCrystalEntity findCrystalAt(BlockPos pos) {
      return this.world != null && pos != null ? BlockEntityUtils.getNext(pos) : null;
   }

   static boolean shouldClearCache(long blockPosLimit, long blockPosValue) {
      return blockPosLimit == Long.MIN_VALUE || blockPosValue < blockPosLimit || blockPosValue - blockPosLimit >= 1L;
   }

   public WorldCollisionHelper() {
      this.scratchPos = new Mutable();
      this.lastUpdateTime = Long.MIN_VALUE;
      this.lastCacheTime = Long.MIN_VALUE;
   }

   public void updateCacheForWorld(long time, World world2) {
      if (world2 != this.world || shouldClearCache(this.lastUpdateTime, time)) {
         this.solidCache.clear();
         this.lastUpdateTime = time;
      }

      this.world = world2;
   }

   public boolean isReplaceableAt(BlockPos pos) {
      return this.world != null && pos != null && BlockPosUtilsRespawn.canReplaceableAir(pos);
   }

   public boolean isReplaceableWithSupportBelow(BlockPos pos) {
      return this.world != null && pos != null ? this.isReplaceableAt(pos) && BlockEntityUtils.canFlag(pos.down()) && BlockEntityUtils.isEmpty(pos) : false;
   }

   public boolean canWalkBetween(Vec3d vec, Vec3d vec2, int worldValue) {
      return this.world != null && isPathWalkable(this, vec, vec2, worldValue);
   }

   public static double computeAngleDifference(Vec3d vec, Vec3d vec2, Vec3d vec3) {
      if (vec != null && vec2 != null && vec3 != null) {
         float blockPosValue = (float)BoxLengthUtils.getAmountDouble(vec2.x - vec.x, vec2.z - vec.z);
         float clamp = (float)BoxLengthUtils.getAmount(vec2.x - vec.x, vec2.y - vec.y, vec2.z - vec.z);
         float f = (float)BoxLengthUtils.getAmountDouble(vec3.x - vec.x, vec3.z - vec.z);
         float currentPitch = (float)BoxLengthUtils.getAmount(vec3.x - vec.x, vec3.y - vec.y, vec3.z - vec.z);
         return BoxLengthUtils.getAmount(blockPosValue, clamp, f, currentPitch);
      } else {
         return 180.0;
      }
   }

   public static Box getBoxFromSample(PosVecUtils.Sample sample, double sigmaValue) {
      double max2 = Math.max(0.0, sigmaValue + (sample == null ? 0.0 : sample.sigma()));
      return sample == null ? null : getPlayerBoxAt(sample.x(), sample.y(), sample.z(), max2);
   }

   public boolean isAirAt(BlockPos pos) {
      World world = this.world;
      return world != null && pos != null && world.isAir(pos);
   }

   public static Box getPlayerBoxAt(double boxValue, double y2, double z2, double boxValue4) {
      double boxValue5 = 0.3 + boxValue4;
      return new Box(boxValue - boxValue5, y2 - boxValue4, z2 - boxValue5, boxValue + boxValue5, y2 + 1.8 + boxValue4, z2 + boxValue5);
   }

   public World getWorld() {
      return this.world;
   }

   public static double computeAngleFromRotation(float blockPosValue, float pitch, Vec3d vec, Vec3d vec2) {
      if (vec != null && vec2 != null) {
         float f = (float)BoxLengthUtils.getAmountDouble(vec2.x - vec.x, vec2.z - vec.z);
         float currentPitch = (float)BoxLengthUtils.getAmount(vec2.x - vec.x, vec2.y - vec.y, vec2.z - vec.z);
         return BoxLengthUtils.getAmount(blockPosValue, pitch, f, currentPitch);
      } else {
         return 180.0;
      }
   }

   public void updateCacheTime(long time) {
      if (time != this.lastCacheTime) {
         this.lastCacheTime = time;
         this.solidCache.clear();
      }
   }

   public boolean isSolidAt(BlockPos pos) {
      return pos != null && this.solid(pos.getX(), pos.getY(), pos.getZ());
   }

   public List<EndCrystalEntity> getCrystalsInRadius(Vec3d vec, double entitiesWorldFactor) {
      World world = this.world;
      if (world != null && vec != null && entitiesWorldFactor > 0.0) {
         Box box = new Box(vec.x - entitiesWorldFactor, vec.y - entitiesWorldFactor, vec.z - entitiesWorldFactor, vec.x + entitiesWorldFactor, vec.y + entitiesWorldFactor, vec.z + entitiesWorldFactor);
         double entitiesWorldLimit = entitiesWorldFactor * entitiesWorldFactor;
         return world.getEntitiesByClass(EndCrystalEntity.class, box, endCrystalEntity -> endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved() && endCrystalEntity.getEntityPos().squaredDistanceTo(vec) <= entitiesWorldLimit);
      } else {
         return List.of();
      }
   }

   public boolean hasBlockingEntities(BlockPos pos, Entity entity, Entity clientPlayerEntity) {
      World world = this.world;
      if (world != null && pos != null) {
         BlockPos up2 = pos.up();
         return !world.getOtherEntities(
               null,
               BlockEntityUtils.getBox3(pos),
               entity3 -> !entity3.isSpectator() && entity3 != entity && entity3 != clientPlayerEntity && !(entity3 instanceof EndCrystalEntity var4x && var4x.getBlockPos().equals(up2))
            )
            .isEmpty();
      } else {
         return false;
      }
   }

   public static double normalCdf(double blockPosFactor) {
      if (Double.isNaN(blockPosFactor)) {
         return 1.0;
      } else if (blockPosFactor == Double.POSITIVE_INFINITY) {
         return 1.0;
      } else if (blockPosFactor == Double.NEGATIVE_INFINITY) {
         return 0.0;
      } else {
         double blockPosFactor2 = 1.0 / (1.0 + 0.2316419 * Math.abs(blockPosFactor));
         double blockPosFactor3 = 0.3989422804014327 * Math.exp(-blockPosFactor * blockPosFactor / 2.0);
         double blockPosValue = blockPosFactor3 * blockPosFactor2 * (0.31938153 + blockPosFactor2 * (-0.356563782 + blockPosFactor2 * (1.781477937 + blockPosFactor2 * (-1.821255978 + blockPosFactor2 * 1.330274429))));
         return blockPosFactor >= 0.0 ? 1.0 - blockPosValue : blockPosValue;
      }
   }

   public boolean canOppositeOffsetAt(BlockPos pos) {
      return this.world != null && pos != null && BlockEntityUtils.canOppositeOffset(pos);
   }

   public static int computeStepCount(double blockPosValue, double posVecValue, double pilotRenderLimit, int blockPosLimit) {
      if (Double.isFinite(blockPosValue) && Double.isFinite(posVecValue) && Double.isFinite(pilotRenderLimit)) {
         if (blockPosValue <= pilotRenderLimit + 1.0E-6 && posVecValue <= 0.0) {
            return 0;
         } else {
            for (int i = 1; i <= blockPosLimit; i++) {
               if (blockPosValue + PosVecUtils.getAmountDouble(posVecValue, i) <= pilotRenderLimit + 1.0E-6) {
                  return i;
               }
            }

            return blockPosLimit;
         }
      } else {
         return 0;
      }
   }

   public static double horizontalDistance(Vec3d vec, Vec3d vec2) {
      double deltaX = vec.x - vec2.x;
      double deltaZ = vec.z - vec2.z;
      return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
   }

   static Vec3d toVisiblePoint(Vec3d vec) {
      return LivingEntityHelperVisible.getVec3d(vec);
   }

   public static double getAmount(double blockPosValue, double z, double flooredValue, double blockPosValue4) {
      double blockPosFactor = blockPosValue - flooredValue;
      double blockPosFactor2 = z - blockPosValue4;
      return Math.sqrt(blockPosFactor * blockPosFactor + blockPosFactor2 * blockPosFactor2);
   }

   public static double computeBoxGap(Box box, Box box2) {
      double blockPosBound = Math.max(box.minX - box2.maxX, box2.minX - box.maxX);
      double hashRegistryBound = Math.max(box.minY - box2.maxY, box2.minY - box.maxY);
      double apiPutBound = Math.max(box.minZ - box2.maxZ, box2.minZ - box.maxZ);
      return Math.max(blockPosBound, Math.max(hashRegistryBound, apiPutBound));
   }

   public void resetCache() {
      this.solidCache.clear();
      this.world = null;
      this.lastUpdateTime = Long.MIN_VALUE;
      this.lastCacheTime = Long.MIN_VALUE;
   }

   static boolean isPathWalkable(PosVecUtils.Terrain terrain, Vec3d vec, Vec3d vec2, int solidValue) {
      if (terrain != null && vec != null && vec2 != null) {
         double horizontalDistance2 = horizontalDistance(vec, vec2);
         int solidLimit = Math.max(1, (int)Math.ceil(horizontalDistance2 / 0.5));
         int floor3 = (int)Math.floor(vec.y + 0.001);
         int solidValue4 = floor3;

         for (int i = 1; i <= solidLimit; i++) {
            double solidFactor = (double)i / solidLimit;
            int floor5 = (int)Math.floor(vec.x + (vec2.x - vec.x) * solidFactor);
            int k = (int)Math.floor(vec.z + (vec2.z - vec.z) * solidFactor);
            int solidCounter = floor3;
            boolean solidFlag = false;

            for (int j = 0; j <= 1 && !solidFlag; j++) {
               int solidValue7 = floor3 + j;
               if (!terrain.solid(floor5, solidValue7, k) && !terrain.solid(floor5, solidValue7 + 1, k)) {
                  solidCounter = solidValue7;
                  solidFlag = true;
               }
            }

            if (!solidFlag) {
               return false;
            }

            int solidCounter2;
            for (solidCounter2 = 0; !terrain.solid(floor5, solidCounter - 1, k) && solidCounter2 < 4; solidCounter2++) {
               solidCounter--;
            }

            if (solidCounter2 >= 4) {
               return false;
            }

            if (solidCounter - floor3 > solidValue) {
               return false;
            }

            floor3 = solidCounter;
            if (solidCounter - solidValue4 > solidValue) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canPlaceOnBedrockObsidian(BlockPos pos) {
      return this.world != null && pos != null && BlockEntityUtils.canBedrockObsidian(pos);
   }
}
