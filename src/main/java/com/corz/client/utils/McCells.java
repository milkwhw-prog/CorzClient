package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.Cells;

public final class McCells implements Cells {
   private final Long2ObjectOpenHashMap<WorldChunk> chunkCache;
   private World world;
   private final Mutable scratchPos;
   private long lastClearTick;
   private final LongOpenHashSet tntMinecartPositions;
   private final Long2IntOpenHashMap cellFlagCache = new Long2IntOpenHashMap(4096);

   public void scanTntMinecarts(int hashEntitiesValue, int hashEntitiesValue2, int hashEntitiesValue3, int hashEntitiesValue4) {
      if (this.world != null) {
         Box box = new Box(hashEntitiesValue - hashEntitiesValue4, hashEntitiesValue2 - hashEntitiesValue4, hashEntitiesValue3 - hashEntitiesValue4, hashEntitiesValue + hashEntitiesValue4 + 1, hashEntitiesValue2 + hashEntitiesValue4 + 1, hashEntitiesValue3 + hashEntitiesValue4 + 1);

         for (TntMinecartEntity tntMinecartEntity : this.world.getEntitiesByClass(TntMinecartEntity.class, box, Entity::isAlive)) {
            long long2 = tntMinecartEntity.getBlockPos().asLong();
            if (this.tntMinecartPositions.add(long2)) {
               this.cellFlagCache.remove(long2);
            }
         }
      }
   }

   public World getWorld() {
      return this.world;
   }

   public void resetIfStale(World world, long time) {
      if (world != this.world || time != this.lastClearTick) {
         this.cellFlagCache.clear();
         this.chunkCache.clear();
         this.tntMinecartPositions.clear();
         this.world = world;
         this.lastClearTick = time;
      }
   }

   private int computeCellFlags(int mutableBottomValue, int mutableBottomLimit, int mutableBottomValue2, long long2) {
      if (mutableBottomLimit >= this.world.getBottomY() && mutableBottomLimit <= this.world.getTopYInclusive()) {
         WorldChunk chunkHash = this.getCachedChunk(mutableBottomValue >> 4, mutableBottomValue2 >> 4);
         if (chunkHash == null) {
            return 257;
         } else {
            this.scratchPos.set(mutableBottomValue, mutableBottomLimit, mutableBottomValue2);
            BlockState blockState = chunkHash.getBlockState(this.scratchPos);
            int tripwireSculk = computeBlockFlags(this.world, this.scratchPos, blockState);
            if (this.tntMinecartPositions.contains(long2)) {
               tripwireSculk |= 16;
            }

            return tripwireSculk;
         }
      } else {
         return mutableBottomLimit < this.world.getBottomY() ? 257 : 0;
      }
   }

   public void clearCaches() {
      this.cellFlagCache.clear();
      this.chunkCache.clear();
   }

   public McCells() {
      this.chunkCache = new Long2ObjectOpenHashMap();
      this.tntMinecartPositions = new LongOpenHashSet();
      this.scratchPos = new Mutable();
      this.lastClearTick = Long.MIN_VALUE;
      this.cellFlagCache.defaultReturnValue(-1);
   }

   public static int computeBlockFlags(World world, BlockPos pos2, BlockState state) {
      int tripwireSculk = 0;
      if (!state.isAir() && !state.getCollisionShape(world, pos2).isEmpty()) {
         tripwireSculk |= 1;
      }

      FluidState fluidState = state.getFluidState();
      if (!fluidState.isEmpty()) {
         if (fluidState.isIn(FluidTags.LAVA)) {
            tripwireSculk |= 2;
         }

         if (fluidState.isIn(FluidTags.WATER)) {
            tripwireSculk |= 4;
         }
      }

      if (state.isAir()) {
         return tripwireSculk;
      } else {
         Block block = state.getBlock();
         if (state.isIn(BlockTags.FIRE)
            || CampfireBlock.isLitCampfire(state)
            || block == Blocks.MAGMA_BLOCK
            || block == Blocks.POWDER_SNOW
            || block == Blocks.CACTUS
            || block == Blocks.SWEET_BERRY_BUSH
            || block == Blocks.COBWEB
            || block == Blocks.WITHER_ROSE
            || block == Blocks.BUBBLE_COLUMN && state.get(BubbleColumnBlock.DRAG)) {
            tripwireSculk |= 8;
         }

         if (block == Blocks.TNT
            || block == Blocks.OBSERVER
            || block == Blocks.TRIPWIRE_HOOK
            || block == Blocks.TRIPWIRE
            || block == Blocks.TARGET
            || block == Blocks.SCULK_SENSOR
            || block == Blocks.CALIBRATED_SCULK_SENSOR
            || state.isIn(BlockTags.PRESSURE_PLATES)) {
            tripwireSculk |= 16;
         }

         if (block == Blocks.RESPAWN_ANCHOR) {
            tripwireSculk |= 128;
         }

         if (state.hasBlockEntity()) {
            tripwireSculk |= 64;
         }

         if (block == Blocks.OBSIDIAN || block == Blocks.CRYING_OBSIDIAN || block == Blocks.BEDROCK || block == Blocks.REINFORCED_DEEPSLATE) {
            tripwireSculk |= 32;
         } else if ((tripwireSculk & 1) != 0) {
            float hardness = state.getHardness(world, pos2);
            if (hardness < 0.0F || hardness > 6.0F) {
               tripwireSculk |= 32;
            }
         }

         return tripwireSculk;
      }
   }

   @Override
   public int getCellFlags(int hashPutValue, int mutableBottomLimit, int hashPutValue3) {
      if (this.world == null) {
         return 257;
      } else {
         long long2 = BlockPos.asLong(hashPutValue, mutableBottomLimit, hashPutValue3);
         int long2intopenhashmap = this.cellFlagCache.get(long2);
         if (long2intopenhashmap >= 0) {
            return long2intopenhashmap;
         } else {
            int mutableBottom = this.computeCellFlags(hashPutValue, mutableBottomLimit, hashPutValue3, long2);
            this.cellFlagCache.put(long2, mutableBottom);
            return mutableBottom;
         }
      }
   }

   private WorldChunk getCachedChunk(int chunkHashValue, int chunkHashValue2) {
      long long2 = ChunkPos.toLong(chunkHashValue, chunkHashValue2);
      WorldChunk long2objectopenhashmap = (WorldChunk)this.chunkCache.get(long2);
      if (long2objectopenhashmap == null && !this.chunkCache.containsKey(long2)) {
         long2objectopenhashmap = this.world.getChunkManager().getWorldChunk(chunkHashValue, chunkHashValue2);
         this.chunkCache.put(long2, long2objectopenhashmap);
      }

      return long2objectopenhashmap;
   }
}
