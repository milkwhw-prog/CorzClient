package com.corz.client.render;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;

final class BlockRenderViewHelper implements BlockRenderView {
   private final Long2ObjectOpenHashMap<BlockState> long2ObjectOpenHashMap;
   private final OpacityColorData maxData;
   private final BlockRenderView world;

   @Override
   public int getBottomY() {
      return this.world.getBottomY();
   }

   @Override
   public int getColor(BlockPos pos2, ColorResolver colorResolver) {
      return this.world.getColor(pos2, colorResolver);
   }

   BlockRenderViewHelper(BlockRenderView world, Long2ObjectOpenHashMap<BlockState> long2ObjectOpenHashMap, OpacityColorData maxData) {
      this.world = world;
      this.long2ObjectOpenHashMap = long2ObjectOpenHashMap;
      this.maxData = maxData;
   }

   @Override
   public BlockEntity getBlockEntity(BlockPos pos2) {
      return this.world.getBlockEntity(pos2);
   }

   @Override
   public BlockState getBlockState(BlockPos pos2) {
      BlockState long2objectopenhashmap = (BlockState)this.long2ObjectOpenHashMap.get(pos2.asLong());
      return long2objectopenhashmap != null && this.maxData.canPlayerForInt(pos2.getY()) ? long2objectopenhashmap : this.world.getBlockState(pos2);
   }

   @Override
   public LightingProvider getLightingProvider() {
      return this.world.getLightingProvider();
   }

   @Override
   public int getHeight() {
      return this.world.getHeight();
   }

   @Override
   public int getLightLevel(LightType lightType, BlockPos pos2) {
      return 15;
   }

   @Override
   public int getBaseLightLevel(BlockPos pos2, int lightLevelValue) {
      return 15;
   }

   @Override
   public float getBrightness(Direction direction, boolean brightnessFlag) {
      return this.world.getBrightness(direction, brightnessFlag);
   }

   @Override
   public FluidState getFluidState(BlockPos pos2) {
      return this.getBlockState(pos2).getFluidState();
   }
}
