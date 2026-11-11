package com.corz.client.utils;

import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.world.StateBlockUtilsCenter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class BlockHazardFlags {
   public static final int LAVA_FLAG = 1;
   private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

   public static int getHazardFlags(World world, BlockPos pos) {
      if (world != null && pos != null) {
         byte blockValue = 0;
         BlockPos up = pos.up();
         BlockPos down = pos.down();
         BlockState blockState = world.getBlockState(down);
         int blockValue2 = blockValue | getBlockHazardBits(world.getBlockState(pos));
         int stateBlock = blockValue2 | getBlockHazardBits(world.getBlockState(up));
         if (blockState.getFluidState().isIn(FluidTags.LAVA)) {
            stateBlock |= 1;
         }

         if (blockState.isOf(Blocks.MAGMA_BLOCK)) {
            stateBlock |= 8;
         }

         if (blockState.isOf(Blocks.CACTUS)) {
            stateBlock |= 4;
         }

         if (CampfireBlock.isLitCampfire(blockState)) {
            stateBlock |= 2;
         }

         if (isAnyOfThreePosEmpty(world, pos, up, down)) {
            stateBlock |= 1024;
         }

         for (Direction direction : HORIZONTAL_DIRECTIONS) {
            if (world.getBlockState(pos.offset(direction)).isOf(Blocks.CACTUS) || world.getBlockState(up.offset(direction)).isOf(Blocks.CACTUS)) {
               stateBlock |= 4;
               break;
            }
         }

         return stateBlock;
      } else {
         return 0;
      }
   }

   public static int countOpenBlocksBelow(World world, BlockPos pos, int downState) {
      if (world != null && pos != null) {
         int bottomY = world.getBottomY();
         int downState2 = 0;

         for (BlockPos down = pos.down(); downState2 < downState; down = down.down()) {
            if (down.getY() < bottomY) {
               return downState;
            }

            BlockState blockState = world.getBlockState(down);
            if (!blockState.getCollisionShape(world, down).isEmpty() || blockState.getFluidState().isIn(FluidTags.WATER)) {
               break;
            }

            downState2++;
         }

         return downState2;
      } else {
         return 0;
      }
   }

   private BlockHazardFlags() {
   }

   public static boolean isSafeToStand(World world, BlockPos pos) {
      return world != null && pos != null ? StateBlockUtilsCenter.canDown(world, pos) && (getExtendedHazardFlags(world, pos) & 287) == 0 : false;
   }

   private static boolean isOpenDropBelow(World world, BlockPos pos) {
      int bottomY = world.getBottomY();
      BlockPos pos2 = pos;

      for (int i = 0; i < 64; i++) {
         if (pos2.getY() < bottomY) {
            return true;
         }

         BlockState blockState = world.getBlockState(pos2);
         if (!blockState.getCollisionShape(world, pos2).isEmpty() || !blockState.getFluidState().isEmpty()) {
            return false;
         }

         pos2 = pos2.down();
      }

      return true;
   }

   private static int getBlockHazardBits(BlockState state) {
      int fluidLit = 0;
      if (state.getFluidState().isIn(FluidTags.LAVA)) {
         fluidLit |= 1;
      }

      if (state.getFluidState().isIn(FluidTags.WATER)) {
         fluidLit |= 32;
      }

      if (state.isIn(BlockTags.FIRE)) {
         fluidLit |= 2;
      }

      if (CampfireBlock.isLitCampfire(state)) {
         fluidLit |= 2;
      }

      if (state.isOf(Blocks.CACTUS)) {
         fluidLit |= 4;
      }

      if (state.isOf(Blocks.MAGMA_BLOCK)) {
         fluidLit |= 8;
      }

      if (state.isOf(Blocks.POWDER_SNOW)) {
         fluidLit |= 16;
      }

      if (state.isOf(Blocks.COBWEB)) {
         fluidLit |= 256;
      }

      if (state.isOf(Blocks.SWEET_BERRY_BUSH)) {
         fluidLit |= 512;
      }

      return fluidLit;
   }

   private static boolean hasNoCollision(World world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      return blockState.getCollisionShape(world, pos).isEmpty();
   }

   private static boolean isAnyOfThreePosEmpty(World world, BlockPos pos, BlockPos pos2, BlockPos pos3) {
      return FloorMathUtilsBlock.canEmpty(world)
         && (FloorMathUtilsBlock.canEmpty(world, pos) || FloorMathUtilsBlock.canEmpty(world, pos2) || FloorMathUtilsBlock.canEmpty(world, pos3));
   }

   public static int getExtendedHazardFlags(World world, BlockPos pos) {
      if (world != null && pos != null) {
         byte blockValue = 0;
         BlockPos up = pos.up();
         BlockPos down = pos.down();
         BlockState blockState = world.getBlockState(pos);
         BlockState blockState2 = world.getBlockState(up);
         BlockState blockState3 = world.getBlockState(down);
         int blockValue2 = blockValue | getBlockHazardBits(blockState);
         int stateBlock = blockValue2 | getBlockHazardBits(blockState2);
         if (blockState3.getFluidState().isIn(FluidTags.LAVA)) {
            stateBlock |= 1;
         }

         if (blockState3.isOf(Blocks.MAGMA_BLOCK)) {
            stateBlock |= 8;
         }

         if (blockState3.isOf(Blocks.CACTUS)) {
            stateBlock |= 4;
         }

         if (CampfireBlock.isLitCampfire(blockState3)) {
            stateBlock |= 2;
         }

         if (isAnyOfThreePosEmpty(world, pos, up, down)) {
            stateBlock |= 1024;
         }

         for (Direction direction : HORIZONTAL_DIRECTIONS) {
            if (world.getBlockState(pos.offset(direction)).isOf(Blocks.CACTUS) || world.getBlockState(up.offset(direction)).isOf(Blocks.CACTUS)) {
               stateBlock |= 4;
               break;
            }
         }

         if (blockState3.getCollisionShape(world, down).isEmpty() && blockState3.getFluidState().isEmpty() && isOpenDropBelow(world, down)) {
            stateBlock |= 64;
         }

         for (Direction horizontalDirectionsItem : HORIZONTAL_DIRECTIONS) {
            BlockPos offset = pos.offset(horizontalDirectionsItem);
            if (hasNoCollision(world, offset) && hasNoCollision(world, offset.up()) && countOpenBlocksBelow(world, offset, 3) >= 3) {
               stateBlock |= 128;
               break;
            }
         }

         return stateBlock;
      } else {
         return 0;
      }
   }
}
