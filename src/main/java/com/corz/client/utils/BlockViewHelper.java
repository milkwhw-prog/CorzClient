package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import com.corz.client.utils.BlockStateUtilsNodes;

public final class BlockViewHelper implements BlockStateUtilsNodes.MutableWorld {
   private final Map<Long, BlockState> overrideStateMap;
   private final BlockView world;
   private final Map<Long, BlockState> fluidOverrideMap = new HashMap<>();

   @Override
   public BlockEntity getBlockEntity(BlockPos pos2) {
      return this.fluidOverrideMap.containsKey(pos2.asLong()) ? null : this.world.getBlockEntity(pos2);
   }

   @Override
   public int getHeight() {
      return this.world.getHeight();
   }

   public BlockViewHelper(BlockView world) {
      this.overrideStateMap = new HashMap<>();
      this.world = world;
   }

   @Override
   public FluidState getFluidState(BlockPos pos2) {
      if (!this.fluidOverrideMap.isEmpty()) {
         BlockState blockState = this.fluidOverrideMap.get(pos2.asLong());
         if (blockState != null) {
            return blockState.getFluidState();
         }
      }

      return this.world.getFluidState(pos2);
   }

   public void clearOverrides() {
      this.fluidOverrideMap.clear();
      this.overrideStateMap.clear();
   }

   @Override
   public BlockState getBlockState(BlockPos pos2) {
      if (!this.fluidOverrideMap.isEmpty()) {
         BlockState blockState = this.fluidOverrideMap.get(pos2.asLong());
         if (blockState != null) {
            return blockState;
         }
      }

      return this.world.getBlockState(pos2);
   }

   @Override
   public void setOverrideState(long blockViewValue, BlockState blockState) {
      if (blockState != null) {
         BlockState put = this.fluidOverrideMap.put(blockViewValue, blockState);
         if (put != null) {
            this.overrideStateMap.put(blockViewValue, put);
         } else {
            this.overrideStateMap.remove(blockViewValue);
         }
      }
   }

   @Override
   public void clearOverrideState(long blockViewValue) {
      BlockState blockState = this.overrideStateMap.remove(blockViewValue);
      if (blockState != null) {
         this.fluidOverrideMap.put(blockViewValue, blockState);
      } else {
         this.fluidOverrideMap.remove(blockViewValue);
      }
   }

   @Override
   public int getBottomY() {
      return this.world.getBottomY();
   }
}
