package com.corz.client.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class BlockStateUtilsDirections {
   private static final Direction[] directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

   public static int getOffsetDirections(World world, BlockPos pos) {
      if (world != null && pos != null && canEmptyState(world, pos) && canEmptyState(world, pos.up())) {
         int offsetDirections = 0;

         for (Direction direction : directions) {
            BlockPos offset = pos.offset(direction);
            if (!world.getBlockState(offset).isOf(Blocks.RESPAWN_ANCHOR) && !canEmptyState(world, offset) && (canEmptyState(world, offset.up()) || canEmptyState(world, offset.up(2)))) {
               offsetDirections++;
            }
         }

         return offsetDirections;
      } else {
         return 0;
      }
   }

   private static boolean canEmptyState(World world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      return blockState.getCollisionShape(world, pos).isEmpty() && blockState.getFluidState().isEmpty();
   }

   public static List<BlockPos> getStateAbsent(World world, BlockPos pos) {
      ArrayList arrayList = new ArrayList(4);
      if (world != null && pos != null) {
         BlockState defaultState = Blocks.OBSIDIAN.getDefaultState();

         for (Direction direction : directions) {
            BlockPos offset = pos.offset(direction);
            BlockState blockState = world.getBlockState(offset);
            if (blockState.isReplaceable() && world.canPlace(defaultState, offset, ShapeContext.absent())) {
               arrayList.add(offset);
            }
         }

         return arrayList;
      } else {
         return arrayList;
      }
   }

   public static List<BlockStateUtilsDirections.Hole> getDownSides(World world, BlockPos pos, int downSidesBound, int downSidesLimit) {
      ArrayList arrayList = new ArrayList();
      if (world != null && pos != null && downSidesLimit > 0) {
         int maxCenter = Math.max(0, downSidesBound);
         double downSidesValue = (maxCenter + 0.5) * (maxCenter + 0.5);

         for (int i = -2; i <= 2; i++) {
            for (int j = -maxCenter; j <= maxCenter; j++) {
               for (int k = -maxCenter; k <= maxCenter; k++) {
                  if (!(j * j + k * k + i * i > downSidesValue)) {
                     BlockPos add = pos.add(j, i, k);
                     if (canBlastResistance(world.getBlockState(add.down())) && canEmptyState(world, add)) {
                        BlockStateUtilsDirections.Hole bedrockState = getBedrockState(world, add);
                        if (bedrockState != null && bedrockState.type() != BlockStateUtilsDirections.Type.NONE && bedrockState.openSides() <= 2) {
                           arrayList.add(bedrockState);
                        }
                     }
                  }
               }
            }
         }

         arrayList.sort(
            Comparator.<BlockStateUtilsDirections.Hole>comparingDouble(hole -> hole.feet().getSquaredDistance(pos))
               .thenComparingInt(BlockStateUtilsDirections.Hole::openSides)
         );
         return arrayList.size() > downSidesLimit ? new ArrayList<>(arrayList.subList(0, downSidesLimit)) : arrayList;
      } else {
         return arrayList;
      }
   }

   public static boolean canBlastResistance(BlockState state) {
      return state != null && state.getBlock().getBlastResistance() >= 600.0F;
   }

   public static BlockStateUtilsDirections.Hole getBedrockState(World world, BlockPos pos) {
      if (world != null && pos != null) {
         BlockPos down = pos.down();
         BlockState blockState = world.getBlockState(down);
         ArrayList arrayList = new ArrayList(4);

         for (Direction direction : directions) {
            arrayList.add(pos.offset(direction));
         }

         if (canEmptyState(world, pos) && canEmptyState(world, pos.up()) && canBlastResistance(blockState)) {
            boolean of = blockState.isOf(Blocks.BEDROCK);
            ArrayList arrayList2 = new ArrayList(4);

            for (BlockPos blockPos : (Iterable<net.minecraft.util.math.BlockPos>) (arrayList)) {
               BlockState blockState2 = world.getBlockState(blockPos);
               if (canBlastResistance(blockState2)) {
                  if (!blockState2.isOf(Blocks.BEDROCK)) {
                     of = false;
                  }
               } else {
                  arrayList2.add(blockPos);
               }
            }

            int varSize = arrayList2.size();
            BlockStateUtilsDirections.Type type;
            if (varSize == 4) {
               type = BlockStateUtilsDirections.Type.NONE;
            } else if (varSize > 0) {
               type = BlockStateUtilsDirections.Type.PARTIAL;
            } else {
               type = of ? BlockStateUtilsDirections.Type.BEDROCK : BlockStateUtilsDirections.Type.OBSIDIAN;
            }

            return new BlockStateUtilsDirections.Hole(pos, type, varSize, List.copyOf(arrayList2));
         } else {
            return new BlockStateUtilsDirections.Hole(pos, BlockStateUtilsDirections.Type.NONE, 4, arrayList);
         }
      } else {
         return null;
      }
   }

   private BlockStateUtilsDirections() {
   }

   public record Hole(BlockPos feet, BlockStateUtilsDirections.Type type, int openSides, List<BlockPos> openCells) {
   }

   public static enum Type {
      NONE,
      PARTIAL,
      OBSIDIAN,
      BEDROCK;
   }
}
