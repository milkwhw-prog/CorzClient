package com.corz.client.utils;

import com.corz.client.world.MathClampUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.AbstractTorchBlock;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BellBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.EndRodBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.HangingMossBlock;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.ShelfBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.block.TwistingVinesBlock;
import net.minecraft.block.TwistingVinesPlantBlock;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.WeepingVinesBlock;
import net.minecraft.block.WeepingVinesPlantBlock;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.WorldView;
import com.corz.client.utils.PistonStickyUtils;
import com.corz.client.utils.SouthNorthUtilsFormat;
import com.corz.client.utils.MinecraftSculkUtils;

public final class BlockStateUtilsView {
   private static final double[][] FACE_SAMPLE_OFFSETS = new double[][]{
      null, {0.5, 0.3}, {0.5, 0.7}, {0.3, 0.5}, {0.7, 0.5}, {0.3, 0.3}, {0.3, 0.7}, {0.7, 0.3}, {0.7, 0.7}
   };
   private static final Direction[] ALL_DIRECTIONS = new Direction[]{
      Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
   };

   public static boolean canBlock(BlockState blockState) {
      return blockState != null && blockState.getBlock() instanceof MultifaceBlock;
   }

   public static BlockHitResult getHit(
      BlockView blockView,
      BlockPos pos,
      Vec3d vec,
      double hitValue,
      Direction direction,
      Direction placementFacing,
      Float float2,
      Axis axis,
      boolean hitFlag,
      ShapeContext shapeContext,
      BlockStateUtilsView.Policy policy
   ) {
      return getFaceChest(blockView, pos, vec, hitValue, direction, placementFacing, float2, axis, hitFlag, shapeContext, policy).hit();
   }

   public static Direction getOpposite(BlockState state) {
      Block block = state.getBlock();
      if (block instanceof HopperBlock) {
         return state.get(Properties.HOPPER_FACING);
      } else if (isContains(state)) {
         Direction blockState = getFacingFromState(state);
         int[] oppositeValues = PistonStickyUtils.getValues(blockState.ordinal(), getDispenserObserver(block));
         return oppositeValues.length == 1 ? ALL_DIRECTIONS[oppositeValues[0]] : null;
      } else if ((
            block instanceof TripwireHookBlock
               || block instanceof LadderBlock
               || block instanceof WallTorchBlock
               || block instanceof WallRedstoneTorchBlock
               || block instanceof WallBannerBlock
         )
         && state.contains(Properties.HORIZONTAL_FACING)) {
         return state.get(Properties.HORIZONTAL_FACING).getOpposite();
      } else if (block instanceof AmethystClusterBlock && state.contains(Properties.FACING)) {
         return state.get(Properties.FACING).getOpposite();
      } else if (block instanceof ShulkerBoxBlock && state.contains(Properties.FACING)) {
         return state.get(Properties.FACING).getOpposite();
      } else if ((block instanceof EndRodBlock || block instanceof LightningRodBlock) && state.contains(Properties.FACING)) {
         return state.get(Properties.FACING).getOpposite();
      } else if (block instanceof TorchBlock) {
         return Direction.DOWN;
      } else if (block instanceof BedBlock
         && state.contains(BedBlock.PART)
         && state.contains(Properties.HORIZONTAL_FACING)
         && state.get(BedBlock.PART) == BedPart.HEAD) {
         return state.get(Properties.HORIZONTAL_FACING).getOpposite();
      } else if (block instanceof WallSignBlock && state.contains(Properties.HORIZONTAL_FACING)) {
         return ALL_DIRECTIONS[SouthNorthUtilsFormat.getValue(SouthNorthUtilsFormat.SignMount.WALL, state.get(Properties.HORIZONTAL_FACING).ordinal())];
      } else if (block instanceof SignBlock) {
         return ALL_DIRECTIONS[SouthNorthUtilsFormat.getValue(SouthNorthUtilsFormat.SignMount.STANDING, Direction.NORTH.ordinal())];
      } else {
         return block instanceof HangingSignBlock ? ALL_DIRECTIONS[SouthNorthUtilsFormat.getValue(SouthNorthUtilsFormat.SignMount.HANGING, Direction.NORTH.ordinal())] : null;
      }
   }

   private BlockStateUtilsView() {
   }

   public static Vec3d getFacePoint(BlockView blockView, BlockState state, BlockPos pos2, Direction direction, double blockViewValue, double blockViewFactor) {
      VoxelShape shapeOutline = getShapeOutline(blockView, state, pos2);
      if (shapeOutline.isEmpty()) {
         return null;
      } else {
         Box boundingBox = shapeOutline.getBoundingBox();
         double x = pos2.getX() + boundingBox.minX;
         double x2 = pos2.getX() + boundingBox.maxX;
         double y = pos2.getY() + boundingBox.minY;
         double y2 = pos2.getY() + boundingBox.maxY;
         double z = pos2.getZ() + boundingBox.minZ;
         double z2 = pos2.getZ() + boundingBox.maxZ;

         return switch (direction) {
            case UP -> new Vec3d(lerp(x, x2, blockViewValue), y2, lerp(z, z2, blockViewFactor));
            case DOWN -> new Vec3d(lerp(x, x2, blockViewValue), y, lerp(z, z2, blockViewFactor));
            case NORTH -> new Vec3d(lerp(x, x2, blockViewValue), lerp(y, y2, blockViewFactor), z);
            case SOUTH -> new Vec3d(lerp(x, x2, blockViewValue), lerp(y, y2, blockViewFactor), z2);
            case WEST -> new Vec3d(x, lerp(y, y2, blockViewValue), lerp(z, z2, blockViewFactor));
            case EAST -> new Vec3d(x2, lerp(y, y2, blockViewValue), lerp(z, z2, blockViewFactor));
         };
      }
   }

   public static BlockStateUtilsView.Requirements getDirectionAxisData(BlockView blockView, BlockPos pos, BlockState state) {
      return new BlockStateUtilsView.Requirements(
         getBlockDirection(blockView, pos, state), getAttachedDirection(state), getRotationBlock(state), getZ(state), isContains(state)
      );
   }

   private static double getShapeBoundOnAxis(VoxelShape voxelShape, Axis axis, Direction direction) {
      if (direction.getAxis() == axis) {
         return direction.getDirection() == AxisDirection.POSITIVE ? voxelShape.getMax(axis) : voxelShape.getMin(axis);
      } else {
         return (voxelShape.getMin(axis) + voxelShape.getMax(axis)) / 2.0;
      }
   }

   public static Direction getDown(BlockState state) {
      Block block = state.getBlock();
      if (block instanceof HangingMossBlock || block instanceof WeepingVinesBlock || block instanceof WeepingVinesPlantBlock) {
         return Direction.UP;
      } else if (block instanceof TwistingVinesBlock || block instanceof TwistingVinesPlantBlock) {
         return Direction.DOWN;
      } else if (block instanceof CandleBlock) {
         return Direction.DOWN;
      } else if (block instanceof LanternBlock && state.contains(Properties.HANGING)) {
         return state.get(Properties.HANGING) ? Direction.UP : Direction.DOWN;
      } else {
         return null;
      }
   }

   public static boolean canBlockState(BlockView blockView, BlockPos pos) {
      return blockView != null && blockView.getBlockState(pos).getBlock() instanceof HopperBlock;
   }

   public static Direction getDirections(int index) {
      return ALL_DIRECTIONS[index];
   }

   public static List<Vec3d> getBoxesMax(BlockView blockView, BlockState state, BlockPos pos) {
      ArrayList arrayList = new ArrayList();
      double[][] doubles = new double[][]{{0.5, 0.5}, {0.5, 0.3}, {0.5, 0.7}, {0.3, 0.5}, {0.7, 0.5}};

      for (Box box : state.getOutlineShape(blockView, pos).getBoundingBoxes()) {
         for (double[] boxesValues : doubles) {
            arrayList.add(
               new Vec3d(pos.getX() + lerp(box.minX, box.maxX, boxesValues[0]), pos.getY() + box.maxY, pos.getZ() + lerp(box.minZ, box.maxZ, boxesValues[1]))
            );
         }
      }

      return arrayList;
   }

   public static VoxelShape getShapeOutline(BlockView blockView, BlockState state, BlockPos pos2) {
      VoxelShape collisionShape = state.getCollisionShape(blockView, pos2);
      return collisionShape.isEmpty() && canBlockOpen(state) ? state.getOutlineShape(blockView, pos2) : collisionShape;
   }

   public static Axis getZ(BlockState state) {
      if (state != null && state.getBlock() instanceof AbstractRailBlock abstractRailBlock) {
         Property shapeProperty = abstractRailBlock.getShapeProperty();
         if (!state.contains(shapeProperty)) {
            return null;
         } else {
            String formatAscendingSouth = SouthNorthUtilsFormat.formatAscendingSouth(state.<RailShape>get(shapeProperty).asString());
            return formatAscendingSouth == null ? null : ("x".equals(formatAscendingSouth) ? Axis.X : Axis.Z);
         }
      } else {
         return null;
      }
   }

   public static Direction[] getDirectionsCopy() {
      return (Direction[])ALL_DIRECTIONS.clone();
   }

   public static double[][] getOffsetsCopy() {
      return (double[][])FACE_SAMPLE_OFFSETS.clone();
   }

   private static boolean canReplaceableCollision(BlockView blockView, BlockPos pos) {
      BlockState blockState = blockView.getBlockState(pos);
      return !blockState.isAir() && !blockState.isReplaceable() && !blockState.getCollisionShape(blockView, pos).isEmpty();
   }

   static Direction getOppositeOffset(BlockView blockView, BlockPos pos2, BlockState state) {
      Block block = state.getBlock();
      if ((block instanceof PillarBlock || block instanceof ChainBlock) && state.contains(Properties.AXIS)) {
         Axis want2 = state.get(Properties.AXIS);
         Direction from = Direction.from(want2, AxisDirection.NEGATIVE);
         if (canReplaceableCollision(blockView, pos2.offset(from))) {
            return from;
         } else {
            return canReplaceableCollision(blockView, pos2.offset(from.getOpposite())) ? from.getOpposite() : from;
         }
      } else {
         return null;
      }
   }

   public static Vec3d getAxisHorizontal(BlockView blockView, BlockState state, BlockPos pos, Direction direction) {
      VoxelShape shapeOutline = getShapeOutline(blockView, state, pos);
      if (shapeOutline.isEmpty()) {
         return null;
      } else {
         double minMax = getShapeBoundOnAxis(shapeOutline, Axis.X, direction);
         double shapeBoundOnAxis = getShapeBoundOnAxis(shapeOutline, Axis.Y, direction);
         double shapeBoundOnAxis3 = getShapeBoundOnAxis(shapeOutline, Axis.Z, direction);
         if (direction.getAxis().isHorizontal() && state.getBlock() instanceof HopperBlock) {
            shapeBoundOnAxis = 0.75;
         }

         return new Vec3d(pos.getX() + minMax, pos.getY() + shapeBoundOnAxis, pos.getZ() + shapeBoundOnAxis3);
      }
   }

   private static double lerp(double blockViewValue, double x2, double blockViewFactor) {
      return blockViewValue + (x2 - blockViewValue) * blockViewFactor;
   }

   public static BlockHitResult getAbsentRaycast(
      BlockView blockView, ShapeContext shapeContext, Vec3d vec, float degrees, float f2, double absentRaycastValue, BlockStateUtilsView.Policy policy
   ) {
      if (policy != null) {
         policy.countRaycast();
      }

      double angle = Math.toRadians(degrees);
      double radians = Math.toRadians(f2);
      Vec3d vec3d = new Vec3d(-Math.sin(angle) * Math.cos(radians), -Math.sin(radians), Math.cos(angle) * Math.cos(radians));
      Vec3d add = vec.add(vec3d.multiply(absentRaycastValue));
      return blockView.raycast(new RaycastContext(vec, add, ShapeType.COLLIDER, FluidHandling.NONE, shapeContext == null ? ShapeContext.absent() : shapeContext));
   }

   public static boolean isContains(BlockState state) {
      Block block = state.getBlock();
      if (block instanceof CrafterBlock) {
         return state.contains(Properties.ORIENTATION);
      } else {
         boolean containsFlag = block instanceof ObserverBlock || block instanceof DispenserBlock || block instanceof PistonBlock || block instanceof BarrelBlock;
         return containsFlag && state.contains(Properties.FACING);
      }
   }

   public static boolean canWireIterator(BlockState state) {
      if (state != null && state.getBlock() instanceof RedstoneWireBlock) {
         int wireIteratorCounter = 0;

         for (Direction direction : Type.HORIZONTAL) {
            if (state.get(RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)) != WireConnection.NONE) {
               wireIteratorCounter++;
            }
         }

         return wireIteratorCounter > 0 && wireIteratorCounter < 4;
      } else {
         return false;
      }
   }

   public static Direction getBlockDirection(BlockView blockView, BlockPos pos2, BlockState state) {
      Direction down = getDown(state);
      if (down != null && blockView instanceof WorldView worldView && !state.canPlaceAt(worldView, pos2)) {
         return down;
      } else {
         Direction oppositeOffset = getOppositeOffset(blockView, pos2, state);
         if (oppositeOffset != null) {
            return oppositeOffset;
         } else if (state.getBlock() instanceof RedstoneTorchBlock && !(state.getBlock() instanceof WallRedstoneTorchBlock)) {
            return Direction.DOWN;
         } else if (!canBlock(state)) {
            return getOpposite(state);
         } else {
            BlockState blockState = blockView.getBlockState(pos2);
            Direction blockDirection = null;

            for (Direction blockDirection2 : Direction.values()) {
               if (MultifaceBlock.hasDirection(state, blockDirection2) && (blockState.getBlock() != state.getBlock() || !MultifaceBlock.hasDirection(blockState, blockDirection2))) {
                  if (MultifaceBlock.canGrowOn(blockView, pos2, blockDirection2)) {
                     return blockDirection2;
                  }

                  if (blockDirection == null) {
                     blockDirection = blockDirection2;
                  }
               }
            }

            return blockDirection;
         }
      }
   }

   public static PistonStickyUtils.Kind getDispenserObserver(Block block) {
      if (block instanceof ObserverBlock) {
         return PistonStickyUtils.Kind.OBSERVER;
      } else if (block instanceof DropperBlock) {
         return PistonStickyUtils.Kind.DROPPER;
      } else if (block instanceof DispenserBlock) {
         return PistonStickyUtils.Kind.DISPENSER;
      } else if (block instanceof PistonBlock) {
         return PistonStickyUtils.Kind.PISTON;
      } else if (block instanceof BarrelBlock) {
         return PistonStickyUtils.Kind.BARREL;
      } else {
         return block instanceof CrafterBlock ? PistonStickyUtils.Kind.CRAFTER : null;
      }
   }

   public static List<Vec3d> getAmountsBlock(BlockView blockView, BlockState state, BlockPos pos, Direction direction, Vec3d vec) {
      ArrayList arrayList = new ArrayList(FACE_SAMPLE_OFFSETS.length + 9);
      if (direction == Direction.UP && state.getBlock() instanceof HopperBlock) {
         arrayList.addAll(getItems(pos, vec));
      }

      for (double[] blockValues : FACE_SAMPLE_OFFSETS) {
         Vec3d vec3d = blockValues == null ? getAxisHorizontal(blockView, state, pos, direction) : getFacePoint(blockView, state, pos, direction, blockValues[0], blockValues[1]);
         if (vec3d != null) {
            arrayList.add(vec3d);
         }
      }

      return arrayList;
   }

   public static Direction getFacingFromState(BlockState state) {
      return state.getBlock() instanceof CrafterBlock && state.contains(Properties.ORIENTATION)
         ? state.get(Properties.ORIENTATION).getFacing()
         : state.get(Properties.FACING);
   }

   private static BlockStateUtilsView.Reject pickWorseReject(BlockStateUtilsView.Reject reject, BlockStateUtilsView.Reject reject2) {
      return getRejectRank(reject2) > getRejectRank(reject) ? reject2 : reject;
   }

   public static boolean canPosBlock(BlockView blockView, BlockHitResult blockHitResult) {
      if (blockHitResult == null || blockHitResult.getType() != net.minecraft.util.hit.HitResult.Type.BLOCK || blockHitResult.getSide() != Direction.UP) {
         return true;
      } else if (!canBlockState(blockView, blockHitResult.getBlockPos())) {
         return true;
      } else {
         double deltaY = blockHitResult.getPos().y - blockHitResult.getBlockPos().getY();
         return deltaY >= 0.999 || deltaY <= 0.6885;
      }
   }

   public static boolean hasDirectionalState(BlockState blockState) {
      return MinecraftSculkUtils.canSafe(getBlock(blockState));
   }

   public static BlockStateUtilsView.Result getFaceChest(
      BlockView blockView,
      BlockPos pos,
      Vec3d vec,
      double faceChestFactor,
      Direction direction,
      Direction placementFacing,
      Float float2,
      Axis axis,
      boolean faceChestFlag,
      ShapeContext shapeContext,
      BlockStateUtilsView.Policy policy
   ) {
      BlockStateUtilsView.Policy policy3 = policy == null ? BlockStateUtilsView.Policy.INSTANCE : policy;
      ShapeContext shapeContext2 = shapeContext == null ? ShapeContext.absent() : shapeContext;
      double faceChestValue = faceChestFactor * faceChestFactor;
      BlockHitResult blockHitResult = null;
      BlockHitResult blockHitResult2 = null;
      BlockStateUtilsView.Reject reject = BlockStateUtilsView.Reject.NO_SUPPORT;
      String faceChestText = null;

      for (Direction direction3 : Direction.values()) {
         if (direction == null || direction3 == direction) {
            if (policy3.isFaceCooldownActive(pos, direction3.getOpposite())) {
               faceChestText = "face_blacklisted";
            } else {
               BlockPos offset = pos.offset(direction3);
               BlockState blockState = blockView.getBlockState(offset);
               if (!blockState.isAir() && !blockState.isReplaceable()) {
                  boolean faceChestFlag2 = direction == Direction.DOWN && canWireIterator(blockState);
                  if (!blockState.getCollisionShape(blockView, offset).isEmpty() || canBlockOpen(blockState) || faceChestFlag2) {
                     Direction opposite = direction3.getOpposite();
                     reject = pickWorseReject(reject, BlockStateUtilsView.Reject.DIRECTIONAL_STATE);
                     boolean directionalState3 = hasDirectionalState(blockState);
                     Vec3d vec3d = directionalState3 && !faceChestFlag ? vec.add(0.0, -0.3500000000000001, 0.0) : vec;
                     boolean autoBuilder = policy3.checkPositionPair(pos, offset);
                     Vec3d vec3d2 = null;

                     for (Vec3d vec3d3 : faceChestFlag2 ? getBoxesMax(blockView, blockState, offset) : getAmountsBlock(blockView, blockState, offset, opposite, vec3d)) {
                        if (vec3d3 != null) {
                           if (vec3d.squaredDistanceTo(vec3d3) > faceChestValue) {
                              reject = pickWorseReject(reject, BlockStateUtilsView.Reject.OUT_OF_REACH);
                           } else {
                              float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec3d, vec3d3);
                              if ((autoBuilder || placementFacing == null || Direction.fromHorizontalDegrees(wrapDegrees[0]) == placementFacing)
                                 && (axis == null || Direction.fromHorizontalDegrees(wrapDegrees[0]).getAxis() == axis)
                                 && (autoBuilder || isYawMatching(wrapDegrees[0], float2))) {
                                 reject = pickWorseReject(reject, BlockStateUtilsView.Reject.NO_LOS);
                                 BlockHitResult absentRaycast = getAbsentRaycast(
                                    blockView, shapeContext2, vec3d, wrapDegrees[0], wrapDegrees[1], faceChestFactor, policy3, canBlockOpen(blockState) || faceChestFlag2
                                 );
                                 if (absentRaycast.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK
                                    && absentRaycast.getBlockPos().offset(absentRaycast.getSide()).equals(pos)
                                    && (direction == null || absentRaycast.getBlockPos().equals(offset) && absentRaycast.getSide() == opposite)
                                    && canPosBlock(blockView, absentRaycast)) {
                                    vec3d2 = vec3d3;
                                    break;
                                 }
                              }
                           }
                        }
                     }

                     if (vec3d2 != null) {
                        BlockHitResult blockHitResult3 = new BlockHitResult(vec3d2, opposite, offset, false);
                        if (!directionalState3) {
                           if (!policy3.isRoutePlanTargetPlaceable(pos)) {
                              return BlockStateUtilsView.Result.getNone(blockHitResult3);
                           }

                           reject = pickWorseReject(reject, BlockStateUtilsView.Reject.OTHER);
                           faceChestText = "chest_plain_click_merges_wrong";
                        } else if (policy3.isOrientedPlace()) {
                           reject = pickWorseReject(reject, BlockStateUtilsView.Reject.OTHER);
                           faceChestText = "interactive_face_no_sneak";
                        } else if (policy3.isRoutePlanTarget(pos)) {
                           if (blockHitResult2 == null && opposite.getAxis().isHorizontal() && policy3.checkPositionPair(pos, offset)) {
                              blockHitResult2 = blockHitResult3;
                           }

                           reject = pickWorseReject(reject, BlockStateUtilsView.Reject.OTHER);
                           faceChestText = "chest_merge_face_refused";
                        } else if (blockHitResult == null) {
                           blockHitResult = blockHitResult3;
                        }
                     }
                  }
               }
            }
         }
      }

      BlockHitResult hit = blockHitResult != null ? blockHitResult : blockHitResult2;
      if (hit != null) {
         return BlockStateUtilsView.Result.getNone(hit);
      } else {
         if (reject == BlockStateUtilsView.Reject.DIRECTIONAL_STATE && direction == null && placementFacing == null && float2 == null && axis == null) {
            reject = BlockStateUtilsView.Reject.NO_LOS;
         }

         return BlockStateUtilsView.Result.getHitBlockData(reject, faceChestText);
      }
   }

   private static int getRejectRank(BlockStateUtilsView.Reject reject) {
      return switch (reject) {
         case OUT_OF_REACH -> 2;
         case NO_LOS -> 3;
         case NO_SUPPORT -> 0;
         case DIRECTIONAL_STATE -> 1;
         default -> 4;
      };
   }

   public static Float getRotationBlock(BlockState state) {
      return (state.getBlock() instanceof SignBlock || state.getBlock() instanceof HangingSignBlock) && state.contains(Properties.ROTATION)
         ? SouthNorthUtilsFormat.getAmount(state.get(Properties.ROTATION))
         : null;
   }

   public static BlockHitResult getAbsentRaycast(
      BlockView blockView, ShapeContext shapeContext, Vec3d vec, float degrees, float f2, double absentRaycastValue, BlockStateUtilsView.Policy policy, boolean absentRaycastFlag
   ) {
      if (policy != null) {
         policy.countRaycast();
      }

      double angle = Math.toRadians(degrees);
      double radians = Math.toRadians(f2);
      Vec3d vec3d = new Vec3d(-Math.sin(angle) * Math.cos(radians), -Math.sin(radians), Math.cos(angle) * Math.cos(radians));
      Vec3d add = vec.add(vec3d.multiply(absentRaycastValue));
      return blockView.raycast(
         new RaycastContext(vec, add, absentRaycastFlag ? ShapeType.OUTLINE : ShapeType.COLLIDER, FluidHandling.NONE, shapeContext == null ? ShapeContext.absent() : shapeContext)
      );
   }

   public static boolean canFluidState(BlockState state) {
      return state != null && state.getBlock() instanceof FluidBlock && state.getFluidState().isStill();
   }

   public static boolean isYawMatching(float blockViewValue, Float float2) {
      if (float2 == null) {
         return true;
      } else {
         int floorMod = SouthNorthUtilsFormat.getFloorMod(float2);
         return SouthNorthUtilsFormat.getFloorMod(blockViewValue) == floorMod;
      }
   }

   public static Direction getAttachedDirection(BlockState state) {
      Block block = state.getBlock();
      if (block instanceof WallMountedBlock
         && state.contains(Properties.BLOCK_FACE)
         && state.contains(Properties.HORIZONTAL_FACING)
         && state.get(Properties.BLOCK_FACE) != BlockFace.WALL) {
         int index = SouthNorthUtilsFormat.getValue(state.get(Properties.HORIZONTAL_FACING).ordinal(), false);
         return index < 0 ? null : ALL_DIRECTIONS[index];
      } else if (block instanceof FenceGateBlock && state.contains(Properties.HORIZONTAL_FACING)) {
         int index2 = SouthNorthUtilsFormat.getValue(state.get(Properties.HORIZONTAL_FACING).ordinal(), false);
         return index2 < 0 ? null : ALL_DIRECTIONS[index2];
      } else if (block instanceof StairsBlock && state.contains(Properties.HORIZONTAL_FACING)) {
         int index3 = SouthNorthUtilsFormat.getValue(state.get(Properties.HORIZONTAL_FACING).ordinal(), false);
         return index3 < 0 ? null : ALL_DIRECTIONS[index3];
      } else if (block instanceof AnvilBlock && state.contains(Properties.HORIZONTAL_FACING)) {
         return state.get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise();
      } else if (block instanceof BedBlock
         && state.contains(BedBlock.PART)
         && state.contains(Properties.HORIZONTAL_FACING)
         && state.get(BedBlock.PART) == BedPart.FOOT) {
         int index4 = SouthNorthUtilsFormat.getValue(state.get(Properties.HORIZONTAL_FACING).ordinal(), false);
         return index4 < 0 ? null : ALL_DIRECTIONS[index4];
      } else if (block instanceof CampfireBlock && state.contains(Properties.HORIZONTAL_FACING)) {
         return state.get(Properties.HORIZONTAL_FACING);
      } else {
         boolean blockFlag = block instanceof ChestBlock
            || block instanceof AbstractFurnaceBlock
            || block instanceof EnderChestBlock
            || block instanceof AbstractRedstoneGateBlock
            || block instanceof ShelfBlock
            || block instanceof ChiseledBookshelfBlock
            || block instanceof CarvedPumpkinBlock;
         if (blockFlag && state.contains(Properties.HORIZONTAL_FACING)) {
            int index5 = SouthNorthUtilsFormat.getValue(state.get(Properties.HORIZONTAL_FACING).ordinal(), true);
            return index5 < 0 ? null : ALL_DIRECTIONS[index5];
         } else {
            return null;
         }
      }
   }

   public static boolean canBlockOpen(BlockState state) {
      return state != null
         && (
            state.getBlock() instanceof AbstractTorchBlock
               || state.getBlock() instanceof PowderSnowBlock
               || state.getBlock() instanceof TripwireHookBlock
               || state.getBlock() instanceof HangingMossBlock
               || state.getBlock() instanceof TwistingVinesBlock
               || state.getBlock() instanceof TwistingVinesPlantBlock
               || state.getBlock() instanceof WeepingVinesBlock
               || state.getBlock() instanceof WeepingVinesPlantBlock
               || state.getBlock() instanceof FenceGateBlock && state.get(Properties.OPEN)
         );
   }

   public static MinecraftSculkUtils.Interaction getBlock(BlockState blockState) {
      Block block = blockState.getBlock();
      boolean blockFlag = block instanceof AbstractRedstoneGateBlock
         || block instanceof DoorBlock
         || block instanceof TrapdoorBlock
         || block instanceof FenceGateBlock
         || block instanceof ButtonBlock
         || block instanceof LeverBlock
         || block instanceof NoteBlock
         || block instanceof DaylightDetectorBlock
         || block instanceof CakeBlock
         || block instanceof BellBlock
         || block instanceof BedBlock
         || block instanceof ComposterBlock
         || block instanceof AbstractCauldronBlock
         || block instanceof CampfireBlock;
      String blockText = Registries.BLOCK.getId(block).toString();
      return MinecraftSculkUtils.getCauldronFence(blockText, block instanceof BlockWithEntity, blockFlag);
   }

   public static List<Vec3d> getItems(BlockPos pos, Vec3d vec) {
      double blockViewValue = 0.4375;
      double y = pos.getY() + 1.0;
      double blockViewValue2 = pos.getX() + 0.5;
      double blockViewValue3 = pos.getZ() + 0.5;
      ArrayList arrayList = new ArrayList(9);
      double blockViewValue4 = vec.x - blockViewValue2;
      double spotValue = vec.z - blockViewValue3;
      double maxAbs = Math.max(Math.abs(blockViewValue4), Math.abs(spotValue));
      if (maxAbs > 1.0E-6) {
         arrayList.add(new Vec3d(blockViewValue2 + blockViewValue4 / maxAbs * 0.4375, y, blockViewValue3 + spotValue / maxAbs * 0.4375));
      }

      ArrayList supplier = new ArrayList(8);

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            if (i != 0 || j != 0) {
               supplier.add(new Vec3d(blockViewValue2 + i * 0.4375, y, blockViewValue3 + j * 0.4375));
            }
         }
      }

      supplier.sort(Comparator.comparingDouble(obj -> ((net.minecraft.util.math.Vec3d) obj).squaredDistanceTo(vec)));
      arrayList.addAll(supplier);
      return arrayList;
   }

   public interface Policy {
      BlockStateUtilsView.Policy INSTANCE = new BlockStateUtilsView.Policy() {};

      default boolean isOrientedPlace() {
         return false;
      }

      default boolean checkPositionPair(BlockPos pos, BlockPos pos2) {
         return false;
      }

      default boolean isFaceCooldownActive(BlockPos pos, Direction direction) {
         return false;
      }

      default void countRaycast() {
      }

      default boolean isRoutePlanTarget(BlockPos pos) {
         return false;
      }

      default boolean isRoutePlanTargetPlaceable(BlockPos pos) {
         return false;
      }
   }

   public static enum Reject {
      NONE,
      NO_STAND,
      OUT_OF_REACH,
      NO_LOS,
      NO_SUPPORT,
      DIRECTIONAL_STATE,
      ACCESS_RESERVED,
      DEPENDENCY,
      MATERIAL,
      OTHER;
   }

   public record Requirements(Direction supportDir, Direction lookDir, Float yaw, Axis axis, boolean sixWay) {
   }

   public record Result(BlockHitResult hit, BlockStateUtilsView.Reject reject, String detail) {
      public static BlockStateUtilsView.Result getNone(BlockHitResult hit) {
         return new BlockStateUtilsView.Result(hit, BlockStateUtilsView.Reject.NONE, null);
      }

      public boolean isHit() {
         return this.hit != null;
      }

      public static BlockStateUtilsView.Result getHitBlockData(BlockStateUtilsView.Reject detail, String r) {
         return new BlockStateUtilsView.Result(null, detail, r);
      }
   }
}
