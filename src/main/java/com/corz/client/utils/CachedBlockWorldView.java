package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import com.corz.client.utils.BlockPosApi;

public final class CachedBlockWorldView implements BlockPosApi {
   private final Long2ObjectOpenHashMap<BlockState> blockStateCache = new Long2ObjectOpenHashMap();
   private World world;

   @Override
   public float getBlastResistance(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getBlastResistance();
   }

   public CachedBlockWorldView bindWorld(World world) {
      this.world = world;
      this.blockStateCache.clear();
      return this;
   }

   @Override
   public boolean hasFluid(BlockPos pos) {
      return !this.getBlockState(pos).getFluidState().isEmpty();
   }

   @Override
   public int getToolNeeds(BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      if (!blockState.isIn(BlockTags.PICKAXE_MINEABLE)) {
         return 0;
      } else {
         return !blockState.isIn(BlockTags.NEEDS_DIAMOND_TOOL) && !blockState.isIn(BlockTags.NEEDS_IRON_TOOL) ? 1 : 2;
      }
   }

   public void invalidateBlock(BlockPos pos) {
      if (pos != null) {
         this.blockStateCache.remove(pos.asLong());
      }
   }

   @Override
   public boolean isPassable(BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      return blockState.isAir() || blockState.getCollisionShape(this.world, pos).isEmpty();
   }

   @Override
   public float getHardnessWorld(BlockPos pos) {
      return this.getBlockState(pos).getHardness(this.world, pos);
   }

   @Override
   public boolean isBedrockOrObsidian(BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      return blockState.isOf(Blocks.OBSIDIAN) || blockState.isOf(Blocks.BEDROCK);
   }

   @Override
   public boolean hasSolidTopFace(BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      return !blockState.isAir() && blockState.isSideSolidFullSquare(this.world, pos, Direction.UP);
   }

   private BlockState getBlockState(BlockPos pos) {
      if (this.world != null && pos != null) {
         long long2 = pos.asLong();
         BlockState long2objectopenhashmap = (BlockState)this.blockStateCache.get(long2);
         if (long2objectopenhashmap == null) {
            if (this.blockStateCache.size() >= 4096) {
               this.blockStateCache.clear();
            }

            long2objectopenhashmap = this.world.getBlockState(pos);
            this.blockStateCache.put(long2, long2objectopenhashmap);
         }

         return long2objectopenhashmap;
      } else {
         return Blocks.AIR.getDefaultState();
      }
   }

   @Override
   public boolean isReplaceable(BlockPos pos) {
      return this.getBlockState(pos).isReplaceable();
   }

   public World getWorld() {
      return this.world;
   }

   @Override
   public boolean isToolRequired(BlockPos pos) {
      return this.getBlockState(pos).isToolRequired();
   }
}
