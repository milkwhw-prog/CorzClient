package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class BlockStateUtilsFluid {
   private static final Direction[] fluidDirections = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private static final double[][] AIM_OFFSET_PAIRS = new double[][]{{0.0, 0.0}, {0.28, 0.28}, {-0.28, 0.28}, {0.28, -0.28}, {-0.28, -0.28}};
   private static final String[] INTERACTABLE_BLOCK_KEYS = new String[]{
      "door",
      "fence_gate",
      "button",
      "lever",
      "crafting_table",
      "anvil",
      "_bed",
      "note_block",
      "repeater",
      "comparator",
      "loom",
      "stonecutter",
      "grindstone",
      "cartography",
      "smithing",
      "cake",
      "respawn_anchor",
      "bell",
      "daylight",
      "enchanting",
      "lectern",
      "jukebox",
      "command",
      "structure",
      "jigsaw",
      "crafter",
      "dispenser",
      "dropper",
      "hopper",
      "chest",
      "barrel",
      "shulker"
   };
   private static final Set<Block> CHEAP_FILLER_BLOCKS = Set.of(
      Blocks.COBBLESTONE,
      Blocks.COBBLED_DEEPSLATE,
      Blocks.STONE,
      Blocks.DEEPSLATE,
      Blocks.NETHERRACK,
      Blocks.DIRT,
      Blocks.COARSE_DIRT,
      Blocks.ROOTED_DIRT,
      Blocks.ANDESITE,
      Blocks.DIORITE,
      Blocks.GRANITE,
      Blocks.TUFF,
      Blocks.CALCITE,
      Blocks.BLACKSTONE,
      Blocks.BASALT,
      Blocks.SMOOTH_BASALT,
      Blocks.END_STONE,
      Blocks.SANDSTONE,
      Blocks.RED_SANDSTONE,
      Blocks.MOSSY_COBBLESTONE,
      Blocks.STONE_BRICKS,
      Blocks.DEEPSLATE_BRICKS,
      Blocks.PACKED_MUD,
      Blocks.CLAY,
      Blocks.TERRACOTTA,
      Blocks.SOUL_SOIL
   );
   private static final Set<Block> VALUABLE_BLOCKS = Set.of(
      Blocks.DIAMOND_BLOCK,
      Blocks.EMERALD_BLOCK,
      Blocks.GOLD_BLOCK,
      Blocks.IRON_BLOCK,
      Blocks.NETHERITE_BLOCK,
      Blocks.LAPIS_BLOCK,
      Blocks.REDSTONE_BLOCK,
      Blocks.COAL_BLOCK,
      Blocks.RAW_IRON_BLOCK,
      Blocks.RAW_GOLD_BLOCK,
      Blocks.RAW_COPPER_BLOCK,
      Blocks.COPPER_BLOCK,
      Blocks.AMETHYST_BLOCK,
      Blocks.BUDDING_AMETHYST,
      Blocks.ANCIENT_DEBRIS,
      Blocks.OBSIDIAN,
      Blocks.CRYING_OBSIDIAN,
      Blocks.MAGMA_BLOCK,
      Blocks.SPONGE,
      Blocks.WET_SPONGE,
      Blocks.TNT
   );

   static boolean isSameFluidKind(FluidState fluidState, FluidState fluidState2) {
      return isLava(fluidState) && isLava(fluidState2) || isWater(fluidState) && isWater(fluidState2);
   }

   public static boolean isStandingOverLava(BlockView blockView, Box box, BlockPos pos2, boolean floorFlag) {
      int y = MathHelper.floor(box.minY - 0.01);
      if (pos2.getY() >= y - (floorFlag ? 1 : 0) && pos2.getY() <= y + (floorFlag ? 1 : 0)) {
         int floorValue = MathHelper.floor(box.minX + 0.001);
         int floorLimit = MathHelper.floor(box.maxX - 0.001);
         int floor = MathHelper.floor(box.minZ + 0.001);
         int floor2 = MathHelper.floor(box.maxZ - 0.001);

         for (int i = floorValue; i <= floorLimit; i++) {
            for (int j = floor; j <= floor2; j++) {
               BlockPos blockPos = new BlockPos(i, y, j);
               boolean floorFlag2 = floorFlag
                  ? Math.abs(blockPos.getX() - pos2.getX()) <= 1 && Math.abs(blockPos.getY() - pos2.getY()) <= 1 && Math.abs(blockPos.getZ() - pos2.getZ()) <= 1
                  : blockPos.equals(pos2);
               if (floorFlag2 && hasLavaBelow(blockView, blockPos)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean isSolidPlaceableBlock(BlockView blockView, BlockPos pos2) {
      BlockState blockState = blockView.getBlockState(pos2);
      if (!blockState.isAir() && !blockState.isReplaceable() && !(blockState.getBlock() instanceof FluidBlock) && !blockState.hasBlockEntity()) {
         if (blockState.getOutlineShape(blockView, pos2).isEmpty()) {
            return false;
         } else {
            String translationKey = blockState.getBlock().getTranslationKey();

            for (String blockTranslationText : INTERACTABLE_BLOCK_KEYS) {
               if (translationKey.contains(blockTranslationText)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean hasAdjacentFluid(BlockView blockView, BlockPos pos, boolean fluidFlag, Predicate<BlockPos> predicate) {
      if (!isLavaOrWater(blockView.getFluidState(pos.up())) || fluidFlag && predicate.test(pos.up())) {
         for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos offset = pos.offset(direction);
            if (isLavaOrWater(blockView.getFluidState(offset)) && (!fluidFlag || !predicate.test(offset))) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public static List<BlockStateUtilsFluid.Target> findFluidTargets(BlockView blockView, BlockPos pos, Box box, Vec3d vec, Vec3d vec2) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      ArrayList arrayList = new ArrayList();
      Mutable mutable = new Mutable();

      for (int i = 4; i >= -3; i--) {
         for (int j = -5; j <= 5; j++) {
            for (int k = -5; k <= 5; k++) {
               mutable.set(pos.getX() + j, pos.getY() + i, pos.getZ() + k);
               FluidState fluidState = blockView.getFluidState(mutable);
               if (isLavaOrWater(fluidState)) {
                  BlockPos immutable = mutable.toImmutable();
                  if (fluidState.isStill()) {
                     if (hasReplaceableNeighbor(blockView, immutable, fluidState)) {
                        linkedHashSet.add(immutable);
                     }
                  } else {
                     arrayList.add(immutable);
                  }
               }
            }
         }
      }

      HashSet hashSet = new HashSet();
      HashSet hashSet2 = new HashSet();

      for (BlockPos blockPos : (Iterable<net.minecraft.util.math.BlockPos>) (arrayList)) {
         if (isFluidSupplied(blockView, blockPos, blockView.getFluidState(blockPos))) {
            hashSet2.add(blockPos);
            if (!hashSet.contains(blockPos)) {
               floodFillFlowingFluid(blockView, blockPos, pos, linkedHashSet, hashSet);
            }
         }
      }

      HashMap hashMap = new HashMap();

      for (BlockPos blockPos2 : (Iterable<net.minecraft.util.math.BlockPos>) (linkedHashSet)) {
         FluidState fluidState2 = blockView.getFluidState(blockPos2);
         boolean fluid = isLava(fluidState2);
         addTargetIfReplaceable(hashMap, blockView, box, vec, vec2, blockPos2, blockPos2, BlockStateUtilsFluid.Kind.SOURCE, fluid, 9);

         for (Direction direction : fluidDirections) {
            BlockPos offset = blockPos2.offset(direction);
            if (isReplaceableForFluid(blockView, offset, fluidState2)) {
               addTargetIfReplaceable(hashMap, blockView, box, vec, vec2, offset, blockPos2, BlockStateUtilsFluid.Kind.SEAL, fluid, 9);
            }
         }
      }

      Box expand = box.expand(1.25);

      for (BlockPos tickx : (Iterable<net.minecraft.util.math.BlockPos>) (arrayList)) {
         FluidState fluidState3 = blockView.getFluidState(tickx);
         boolean lava = isLava(fluidState3);
         if (hashSet2.contains(tickx)) {
            addTargetIfReplaceable(hashMap, blockView, box, vec, vec2, tickx, tickx, BlockStateUtilsFluid.Kind.FLOW, lava, fluidState3.getLevel());
         } else if (expand.intersects(new Box(tickx))) {
            addTargetIfReplaceable(hashMap, blockView, box, vec, vec2, tickx, tickx, BlockStateUtilsFluid.Kind.RECEDING, lava, fluidState3.getLevel());
         }
      }

      ArrayList arrayList2 = new ArrayList(hashMap.values());
      arrayList2.sort(Comparator.comparingDouble(BlockStateUtilsFluid.Target::score));
      return arrayList2;
   }

   private static void enqueueIfInRange(BlockPos pos, BlockPos pos2, ArrayDeque<BlockPos> arrayDeque, Set<BlockPos> set) {
      if (Math.abs(pos.getX() - pos2.getX()) <= 9 && Math.abs(pos.getY() - pos2.getY()) <= 9 && Math.abs(pos.getZ() - pos2.getZ()) <= 9) {
         if (set.add(pos)) {
            arrayDeque.add(pos);
         }
      }
   }

   public static BlockStateUtilsFluid.Placement findPlacementToward(BlockView blockView, Vec3d vec, double offsetAbsentFactor, BlockPos pos, Set<Long> set) {
      double offsetAbsentValue = offsetAbsentFactor * offsetAbsentFactor;
      BlockStateUtilsFluid.Placement offsetAbsent = null;
      double maxValue = Double.MAX_VALUE;

      for (Direction direction : Direction.values()) {
         BlockPos offset = pos.offset(direction);
         Direction opposite = direction.getOpposite();
         if ((set == null || !set.contains(encodePosDirectionKey(pos, opposite))) && isSolidPlaceableBlock(blockView, offset)) {
            for (double[] offsetAbsentValues : AIM_OFFSET_PAIRS) {
               Vec3d offsetAxis = getFaceAimPoint(offset, opposite, offsetAbsentValues[0], offsetAbsentValues[1]);
               double squaredDistanceTo = vec.squaredDistanceTo(offsetAxis);
               if (!(squaredDistanceTo > offsetAbsentValue) && !(squaredDistanceTo >= maxValue)) {
                  double offsetAbsentValue3 = opposite.getOffsetX() * (vec.x - offsetAxis.x) + opposite.getOffsetY() * (vec.y - offsetAxis.y) + opposite.getOffsetZ() * (vec.z - offsetAxis.z);
                  if (!(offsetAbsentValue3 <= 0.05)) {
                     BlockHitResult raycast = blockView.raycast(new RaycastContext(vec, offsetAxis, ShapeType.OUTLINE, FluidHandling.NONE, ShapeContext.absent()));
                     if (hitMatchesPlacement(raycast, offset, opposite, pos)) {
                        offsetAbsent = new BlockStateUtilsFluid.Placement(offset, opposite, offsetAxis, pos);
                        maxValue = squaredDistanceTo;
                        break;
                     }
                  }
               }
            }
         }
      }

      return offsetAbsent;
   }

   private BlockStateUtilsFluid() {
   }

   public static boolean isLavaOrWater(FluidState fluidState) {
      return isLava(fluidState) || isWater(fluidState);
   }

   public static boolean hitMatchesPlacement(HitResult hitResult, BlockPos pos, Direction direction, BlockPos pos2) {
      if (!(hitResult instanceof BlockHitResult blockHitResult && hitResult.getType() == Type.BLOCK)) {
         return false;
      } else {
         return blockHitResult.getBlockPos().equals(pos) && blockHitResult.getSide() == direction ? true : blockHitResult.getBlockPos().offset(blockHitResult.getSide()).equals(pos2);
      }
   }

   static boolean isReplaceableForFluid(BlockView blockView, BlockPos pos, FluidState fluidState) {
      BlockState blockState = blockView.getBlockState(pos);
      FluidState fluidState2 = blockState.getFluidState();
      return !fluidState2.isEmpty() ? isSameFluidKind(fluidState2, fluidState) && !fluidState2.isStill() : blockState.isAir() || blockState.isReplaceable();
   }

   public static int getBlockPlacementPriority(ItemStack stack, boolean oreBurnableFlag) {
      if (stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
         Block block = blockItem.getBlock();
         BlockState defaultState = block.getDefaultState();
         if (block == Blocks.TNT || !defaultState.isOpaqueFullCube() || defaultState.hasBlockEntity()) {
            return -1;
         } else if (oreBurnableFlag && defaultState.isBurnable()) {
            return -1;
         } else if (CHEAP_FILLER_BLOCKS.contains(block)) {
            return 4;
         } else if (VALUABLE_BLOCKS.contains(block) || block.getTranslationKey().contains("_ore")) {
            return 0;
         } else {
            return block instanceof FallingBlock ? 1 : 2;
         }
      } else {
         return -1;
      }
   }

   public static boolean isFluidSupplied(BlockView blockView, BlockPos pos, FluidState fluidState) {
      if (fluidState.isStill()) {
         return true;
      } else if (isSameFluidKind(blockView.getFluidState(pos.up()), fluidState)) {
         return true;
      } else {
         for (Direction direction : HORIZONTAL_DIRECTIONS) {
            FluidState fluidState2 = blockView.getFluidState(pos.offset(direction));
            if (isSameFluidKind(fluidState2, fluidState) && (fluidState2.isStill() || fluidState2.getLevel() > fluidState.getLevel())) {
               return true;
            }
         }

         return false;
      }
   }

   private static void addTargetIfReplaceable(
      Map<BlockPos, BlockStateUtilsFluid.Target> map,
      BlockView blockView,
      Box box,
      Vec3d vec,
      Vec3d vec2,
      BlockPos pos,
      BlockPos pos2,
      BlockStateUtilsFluid.Kind kind,
      boolean intersectsReplaceableFlag,
      int intersectsReplaceableValue
   ) {
      if (!box.intersects(new Box(pos))) {
         if (blockView.getBlockState(pos).isReplaceable()) {
            double productDot = computeTargetScore(kind, intersectsReplaceableFlag, intersectsReplaceableValue, pos, vec, vec2);
            BlockStateUtilsFluid.Target blockStateUtilsFluidTarget = (BlockStateUtilsFluid.Target)map.get(pos);
            if (blockStateUtilsFluidTarget == null || productDot < blockStateUtilsFluidTarget.score()) {
               map.put(pos, new BlockStateUtilsFluid.Target(pos, pos2, kind, intersectsReplaceableFlag, intersectsReplaceableValue, productDot));
            }
         }
      }
   }

   public static boolean isWater(FluidState fluidState) {
      return fluidState != null && fluidState.getFluid() instanceof WaterFluid;
   }

   public static boolean isLavaAdjacent(BlockView blockView, BlockPos pos2) {
      return hasAdjacentFluid(blockView, pos2, false, null);
   }

   public static boolean hasReplaceableNeighbor(BlockView blockView, BlockPos pos, FluidState fluidState) {
      for (Direction direction : fluidDirections) {
         if (isReplaceableForFluid(blockView, pos.offset(direction), fluidState)) {
            return true;
         }
      }

      return false;
   }

   static double computeTargetScore(BlockStateUtilsFluid.Kind kind, boolean productDotFlag, int productDotValue, BlockPos pos, Vec3d vec, Vec3d vec2) {
      double productDotTotal = (productDotFlag ? 0.0 : 4000000.0) + kind.ordinal() * 1000000.0;
      if (kind == BlockStateUtilsFluid.Kind.FLOW || kind == BlockStateUtilsFluid.Kind.RECEDING) {
         productDotTotal -= productDotValue * 10000.0;
      }

      Vec3d center = Vec3d.ofCenter(pos);
      double squaredDistanceTo = vec.squaredDistanceTo(center);
      double degrees2 = 0.0;
      if (vec2 != null && squaredDistanceTo > 1.0E-6) {
         Vec3d normalize = center.subtract(vec).normalize();
         degrees2 = Math.toDegrees(Math.acos(MathHelper.clamp(normalize.dotProduct(vec2), -1.0, 1.0)));
      }

      return productDotTotal + squaredDistanceTo * 10.0 + degrees2 * 2.0 - (pos.getY() - vec.y) * 20.0;
   }

   public static boolean hasLavaBelow(BlockView blockView, BlockPos pos) {
      for (int i = 1; i <= 6; i++) {
         BlockPos down = pos.down(i);
         BlockState blockState = blockView.getBlockState(down);
         FluidState fluidState = blockState.getFluidState();
         if (isLava(fluidState)) {
            return true;
         }

         if (!fluidState.isEmpty()) {
            return false;
         }

         if (!blockState.getCollisionShape(blockView, down).isEmpty()) {
            return false;
         }
      }

      return false;
   }

   public static boolean isLava(FluidState fluidState) {
      return fluidState != null && fluidState.getFluid() instanceof LavaFluid;
   }

   public static long encodePosDirectionKey(BlockPos pos, Direction direction) {
      return pos.asLong() * 7L + direction.ordinal();
   }

   public static boolean hasLavaNearby(BlockView blockView, BlockPos pos2, boolean fluidFlag) {
      int fluidLimit = fluidFlag ? 1 : 0;

      for (int i = -fluidLimit; i <= fluidLimit; i++) {
         for (int j = -fluidLimit; j <= fluidLimit; j++) {
            for (int k = -fluidLimit; k <= fluidLimit; k++) {
               BlockPos add = pos2.add(i, j, k);
               if (fluidFlag && isLava(blockView.getFluidState(add))) {
                  return true;
               }

               for (Direction direction : Direction.values()) {
                  if (isLava(blockView.getFluidState(add.offset(direction)))) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   static Vec3d getFaceAimPoint(BlockPos pos, Direction direction, double offsetAxisValue, double offsetAxisValue2) {
      double x = pos.getX() + 0.5 + direction.getOffsetX() * 0.49;
      double y = pos.getY() + 0.5 + direction.getOffsetY() * 0.49;
      double z = pos.getZ() + 0.5 + direction.getOffsetZ() * 0.49;
      switch (direction.getAxis()) {
         case X:
            y += offsetAxisValue;
            z += offsetAxisValue2;
            break;
         case Y:
            x += offsetAxisValue;
            z += offsetAxisValue2;
            break;
         default:
            x += offsetAxisValue;
            y += offsetAxisValue2;
      }

      return new Vec3d(x, y, z);
   }

   static void floodFillFlowingFluid(BlockView blockView, BlockPos pos, BlockPos pos2, Set<BlockPos> set, Set<BlockPos> hashSet) {
      ArrayDeque arrayDeque = new ArrayDeque();
      arrayDeque.add(pos);
      hashSet.add(pos);
      int fluidLevelCounter = 384;

      while (!arrayDeque.isEmpty() && fluidLevelCounter-- > 0) {
         BlockPos blockPos = (BlockPos)arrayDeque.poll();
         FluidState fluidState = blockView.getFluidState(blockPos);
         if (isLavaOrWater(fluidState)) {
            if (fluidState.isStill()) {
               set.add(blockPos);
            } else {
               BlockPos up = blockPos.up();
               if (isSameFluidKind(blockView.getFluidState(up), fluidState)) {
                  enqueueIfInRange(up, pos2, arrayDeque, hashSet);
               }

               for (Direction direction : HORIZONTAL_DIRECTIONS) {
                  BlockPos offset = blockPos.offset(direction);
                  FluidState fluidState2 = blockView.getFluidState(offset);
                  if (isSameFluidKind(fluidState2, fluidState) && (fluidState2.isStill() || fluidState2.getLevel() > fluidState.getLevel())) {
                     enqueueIfInRange(offset, pos2, arrayDeque, hashSet);
                  }
               }
            }
         }
      }
   }

   public static enum Kind {
      SOURCE,
      SEAL,
      FLOW,
      RECEDING;
   }

   public record Placement(BlockPos anchor, Direction face, Vec3d aim, BlockPos cell) {
   }

   public record Target(BlockPos cell, BlockPos fluid, BlockStateUtilsFluid.Kind kind, boolean lava, int level, double score) {
   }
}
