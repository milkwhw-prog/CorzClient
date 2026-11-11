package com.corz.client.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class StateBlockUtilsCenter {
   public static boolean isIn(BlockState state) {
      return state.getFluidState().isIn(FluidTags.LAVA);
   }

   private StateBlockUtilsCenter() {
   }

   public static boolean canStateFluid(World world, BlockPos pos) {
      return world.getBlockState(pos).getFluidState().isIn(FluidTags.WATER);
   }

   public static boolean canDown(World world, BlockPos pos) {
      return canSquareState(world, pos.down()) && canCollisionShape(world, pos) && canCollisionShape(world, pos.up());
   }

   public static boolean canSquareState(World world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      return !isIn(blockState) && blockState.getFluidState().isEmpty() ? Block.isFaceFullSquare(blockState.getCollisionShape(world, pos), Direction.UP) : false;
   }

   public static boolean canHardnessState(World world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      return !blockState.isAir() && blockState.getFluidState().isEmpty() ? blockState.getHardness(world, pos) >= 0.0F : false;
   }

   public static boolean canStateBlock(World world, BlockPos pos) {
      return isIn(world.getBlockState(pos));
   }

   public static boolean canCollisionShape(World world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      return !isIn(blockState) && blockState.getCollisionShape(world, pos).isEmpty();
   }

   public static Vec3d getOffsetCenter(BlockPos pos, Direction direction) {
      return Vec3d.ofCenter(pos).add(direction.getOffsetX() * 0.5, direction.getOffsetY() * 0.5, direction.getOffsetZ() * 0.5);
   }
}
