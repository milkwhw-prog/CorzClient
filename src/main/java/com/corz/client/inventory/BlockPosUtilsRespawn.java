package com.corz.client.inventory;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.GlowstoneTimeout;

public final class BlockPosUtilsRespawn {
   public static boolean canHelmetBoots(BlockPos pos2, double helmetBootsValue) {
      Box expand = new Box(pos2).expand(helmetBootsValue);

      for (Entity entity : getInstance().world.getOtherEntities(null, expand)) {
         if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getStack();
            if (!stack.isEmpty()) {
               if (stack.isOf(Items.TNT)) {
                  return true;
               }

               String path = Registries.ITEM.getId(stack.getItem()).getPath();
               if (path.contains("helmet") || path.contains("chestplate") || path.contains("leggings") || path.contains("boots") || path.contains("sword")) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static double[] getAmounts(Vec3d vec) {
      return new double[]{vec.x, vec.y, vec.z};
   }

   public static boolean canSidePos(BlockHitResult blockHitResult, Vec3d vec, double sidePosValue) {
      return blockHitResult != null && GlowstoneTimeout.canAmounts(getAmounts(vec, blockHitResult.getBlockPos()), getValue(blockHitResult.getSide()), sidePosValue);
   }

   public static GlowstoneTimeout.CoverPlan getMinMax(BlockPos pos, Box box, Predicate<BlockPos> predicate, BlockState state) {
      double[] doubles = new double[]{
         box.minX - pos.getX(),
         box.minY - pos.getY(),
         box.minZ - pos.getZ(),
         box.maxX - pos.getX(),
         box.maxY - pos.getY(),
         box.maxZ - pos.getZ()
      };
      int blockPosSize = GlowstoneTimeout.blockPosInts.length;
      boolean[] booleans = new boolean[blockPosSize];
      boolean[] booleans2 = new boolean[blockPosSize];

      for (int i = 0; i < blockPosSize; i++) {
         BlockPos blockPos = getBlockPos(pos, i);
         booleans[i] = !canReplaceableAir(blockPos);
         booleans2[i] = !booleans[i] && canAbsentPlace(blockPos, state) && predicate.test(blockPos);
      }

      return GlowstoneTimeout.getAmounts(doubles, booleans, booleans2);
   }

   public static boolean isRegistryWorld() {
      MinecraftClient instance = getInstance();
      return instance.world != null && !World.NETHER.equals(instance.world.getRegistryKey());
   }

   public static boolean canAbsentPlace(BlockPos pos2, BlockState blockState) {
      return getInstance().world.canPlace(blockState, pos2, ShapeContext.absent());
   }

   public static double getRangeInteraction() {
      MinecraftClient instance = getInstance();
      double rangeInteractionBound = instance.player == null ? 4.5 : instance.player.getBlockInteractionRange();
      return Math.min(4.5, rangeInteractionBound) - 0.1;
   }

   public static boolean canPosBlock(BlockHitResult blockHitResult, Vec3d vec, double posBlockValue) {
      return blockHitResult != null && !canReplaceableAir(blockHitResult.getBlockPos()) && canSidePos(blockHitResult, vec, posBlockValue);
   }

   public static boolean canRespawnGlowstone(ItemStack itemStack) {
      return !itemStack.isOf(Items.GLOWSTONE) && !itemStack.isOf(Items.RESPAWN_ANCHOR) && !(itemStack.getItem() instanceof BlockItem);
   }

   public static Vec3d getAxis(Vec3d vec, Direction direction, Random random) {
      double x = (random.nextDouble() * 2.0 - 1.0) * 0.3;
      double z = (random.nextDouble() * 2.0 - 1.0) * 0.3;

      return switch (direction.getAxis()) {
         case X -> vec.add(0.0, x, z);
         case Y -> vec.add(x, 0.0, z);
         case Z -> vec.add(x, z, 0.0);
      };
   }

   public static BlockPos getBlockPos(BlockPos pos, int blockPosValue) {
      int[] blockPosValues = GlowstoneTimeout.blockPosInts[blockPosValue];
      return pos.add(blockPosValues[0], 0, blockPosValues[1]);
   }

   public static Direction getSouthNorth(BlockPos pos, BlockPos pos2) {
      int deltaX = pos2.getX() - pos.getX();
      int deltaY = pos2.getY() - pos.getY();
      int deltaZ = pos2.getZ() - pos.getZ();
      if (Math.abs(deltaX) >= Math.abs(deltaY) && Math.abs(deltaX) >= Math.abs(deltaZ)) {
         return deltaX > 0 ? Direction.EAST : Direction.WEST;
      } else if (Math.abs(deltaY) >= Math.abs(deltaX) && Math.abs(deltaY) >= Math.abs(deltaZ)) {
         return deltaY > 0 ? Direction.UP : Direction.DOWN;
      } else {
         return deltaZ > 0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   private static MinecraftClient getInstance() {
      return MinecraftClient.getInstance();
   }

   public static int getChargesRespawn(BlockPos pos2) {
      BlockState blockState = getInstance().world.getBlockState(pos2);
      return blockState.isOf(Blocks.RESPAWN_ANCHOR) ? blockState.get(RespawnAnchorBlock.CHARGES) : -1;
   }

   public static boolean canReplaceableAir(BlockPos pos2) {
      BlockState blockState = getInstance().world.getBlockState(pos2);
      return blockState.isAir() || blockState.isReplaceable();
   }

   private BlockPosUtilsRespawn() {
   }

   public static BlockHitResult getOffsetManhattan(BlockPos pos, BlockPos pos2, Vec3d vec, Vec3d vec2, double offsetManhattanValue, Random random) {
      int glowstoneTimeoutValue = pos2 != null && pos.getManhattanDistance(pos2) == 1 ? getValue(getSouthNorth(pos, pos2)) : -1;
      boolean[] booleans = new boolean[6];

      for (int i = 0; i < 6; i++) {
         booleans[i] = !canReplaceableAir(pos.offset(getUp(i)));
      }

      int blockPosValue = GlowstoneTimeout.getAmounts(booleans, glowstoneTimeoutValue, getAmounts(vec, pos), getAmounts(vec2), offsetManhattanValue);
      if (blockPosValue == -1) {
         return null;
      } else {
         Direction up = getUp(blockPosValue);
         BlockPos offset = pos.offset(up);
         Direction opposite = up.getOpposite();
         Vec3d axis = getAxis(Vec3d.ofCenter(pos).add(up.getOffsetX() * 0.5, up.getOffsetY() * 0.5, up.getOffsetZ() * 0.5), up, random);
         return new BlockHitResult(axis, opposite, offset, false);
      }
   }

   public static int getValue(Direction direction) {
      return switch (direction) {
         case DOWN -> 0;
         case UP -> 1;
         case NORTH -> 2;
         case SOUTH -> 3;
         case WEST -> 4;
         case EAST -> 5;
      };
   }

   private static double[] getAmounts(Vec3d vec, BlockPos pos) {
      return new double[]{vec.x - pos.getX(), vec.y - pos.getY(), vec.z - pos.getZ()};
   }

   public static BlockHitResult getOffsetCenter(BlockPos pos, Vec3d vec, Vec3d vec2, double offsetCenterValue, Random random) {
      int blockPosValue = GlowstoneTimeout.getAmounts(getAmounts(vec, pos), getAmounts(vec2), offsetCenterValue);
      if (blockPosValue == -1) {
         return null;
      } else {
         Direction up = getUp(blockPosValue);
         Vec3d axis = getAxis(Vec3d.ofCenter(pos).add(up.getOffsetX() * 0.5, up.getOffsetY() * 0.5, up.getOffsetZ() * 0.5), up, random);
         return new BlockHitResult(axis, up, pos, false);
      }
   }

   public static Direction getUp(int blockPosValue) {
      return switch (blockPosValue) {
         case 0 -> Direction.DOWN;
         case 1 -> Direction.UP;
         case 2 -> Direction.NORTH;
         case 3 -> Direction.SOUTH;
         case 4 -> Direction.WEST;
         case 5 -> Direction.EAST;
         default -> Direction.UP;
      };
   }
}
