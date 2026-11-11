package com.corz.client.utils;

import com.corz.client.world.StateBlockUtilsCenter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import com.corz.client.utils.Drill;
import com.corz.client.utils.SmoothAimRotator;

public final class BlockAimController {
   private final Drill drill;
   private BlockPos currentTarget;
   private boolean usedDrillFallback;
   private final MinecraftClient minecraftClient;
   private Vec3d targetAimPoint;
   private double lastEyeZ;
   private BlockPos previewTarget;
   private BlockAimController.DigGuard breakGuard;
   private Vec3d previewAimPoint;
   private double lastEyeX;
   private final SmoothAimRotator yawClamp = new SmoothAimRotator();
   private final Set<BlockPos> outlineAimBlocks;
   private double lastEyeY;

   private Vec3d findAimPoint(ClientPlayerEntity player, BlockPos pos2, double offsetCenterValue) {
      Vec3d center = Vec3d.ofCenter(pos2);
      if (this.outlineAimBlocks.contains(pos2)) {
         Vec3d boxesOutline = this.findAimPointOnOutline(player, pos2, offsetCenterValue);
         if (boxesOutline != null) {
            return boxesOutline;
         }
      }

      if (canRaycastHit(player, pos2, center, offsetCenterValue)) {
         return center;
      } else {
         Box minMax = getPartialBlockBox(player.getEntityWorld(), pos2);
         if (minMax != null) {
            Vec3d center2 = minMax.getCenter();
            if (canRaycastHit(player, pos2, center2, offsetCenterValue)) {
               return center2;
            }

            for (Direction direction : sortDirectionsToward(player.getEyePos(), center2)) {
               Vec3d vec3d = new Vec3d(
                  direction.getOffsetX() > 0 ? minMax.maxX - 0.01 : (direction.getOffsetX() < 0 ? minMax.minX + 0.01 : center2.x),
                  direction.getOffsetY() > 0 ? minMax.maxY - 0.01 : (direction.getOffsetY() < 0 ? minMax.minY + 0.01 : center2.y),
                  direction.getOffsetZ() > 0 ? minMax.maxZ - 0.01 : (direction.getOffsetZ() < 0 ? minMax.minZ + 0.01 : center2.z)
               );
               if (canRaycastHit(player, pos2, vec3d, offsetCenterValue)) {
                  return vec3d;
               }
            }
         }

         double x = pos2.getX();
         double y = pos2.getY();
         double z = pos2.getZ();
         if (y <= player.getY() + 0.5) {
            Vec3d[] vec3ds = new Vec3d[]{
               new Vec3d(x + 0.5, y + 0.9, z + 0.5),
               new Vec3d(x + 0.3, y + 0.9, z + 0.5),
               new Vec3d(x + 0.7, y + 0.9, z + 0.5),
               new Vec3d(x + 0.5, y + 0.9, z + 0.3),
               new Vec3d(x + 0.5, y + 0.9, z + 0.7)
            };

            for (Vec3d offsetCenter : vec3ds) {
               if (canRaycastHit(player, pos2, offsetCenter, offsetCenterValue)) {
                  return offsetCenter;
               }
            }
         }

         for (Direction direction2 : sortDirectionsToward(player.getEyePos(), center)) {
            Vec3d vec = StateBlockUtilsCenter.getOffsetCenter(pos2, direction2);
            if (canRaycastHit(player, pos2, vec, offsetCenterValue)) {
               return vec;
            }
         }

         return null;
      }
   }

   private Vec3d resolveAimPoint(ClientPlayerEntity player, BlockPos pos2, double vecImmutableValue) {
      if (pos2.equals(this.currentTarget) && this.targetAimPoint != null && canRaycastHit(player, pos2, this.targetAimPoint, vecImmutableValue)) {
         return this.targetAimPoint;
      } else {
         return pos2.equals(this.previewTarget) && this.previewAimPoint != null && canRaycastHit(player, pos2, this.previewAimPoint, vecImmutableValue)
            ? this.previewAimPoint
            : this.findAimPoint(player, pos2, vecImmutableValue);
      }
   }

   static Box getPartialBlockBox(BlockView blockView, BlockPos pos2) {
      VoxelShape outlineShape = blockView.getBlockState(pos2).getOutlineShape(blockView, pos2);
      if (outlineShape.isEmpty()) {
         return null;
      } else {
         Box boundingBox = outlineShape.getBoundingBox();
         return boundingBox.minX <= 0.001 && boundingBox.minY <= 0.001 && boundingBox.minZ <= 0.001 && boundingBox.maxX >= 0.999 && boundingBox.maxY >= 0.999 && boundingBox.maxZ >= 0.999
            ? null
            : boundingBox.offset(pos2);
      }
   }

   public boolean canSeeBlockFromStandSpot(BlockPos pos2, BlockPos pos3, double centerMinecraftFactor) {
      ClientPlayerEntity player = this.minecraftClient.player;
      ClientWorld world = this.minecraftClient.world;
      if (player != null && world != null) {
         Vec3d vec3d = new Vec3d(pos2.getX() + 0.5, pos2.getY() + player.getStandingEyeHeight(), pos2.getZ() + 0.5);
         Box minMax = getPartialBlockBox(world, pos3);
         Vec3d vec3d2 = minMax == null ? Vec3d.ofCenter(pos3) : minMax.getCenter();
         if (vec3d.squaredDistanceTo(vec3d2) > centerMinecraftFactor * centerMinecraftFactor) {
            return false;
         } else {
            BlockHitResult raycast = world.raycast(new RaycastContext(vec3d, vec3d2, ShapeType.OUTLINE, FluidHandling.NONE, player));
            return raycast.getType() == Type.BLOCK && raycast.getBlockPos().equals(pos3);
         }
      } else {
         return false;
      }
   }

   static int selectFastestHotbarSlot(ClientPlayerEntity player, BlockState blockState) {
      PlayerInventory inventory = player.getInventory();
      int selectedSlot = inventory.getSelectedSlot();
      float selectedSlotValue = -1.0F;
      boolean selectedSlotFlag = false;

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         float selectedSlotLimit = stack.isEmpty() ? 1.0F : stack.getMiningSpeedMultiplier(blockState);
         boolean selectedSlotFlag2 = !stack.isEmpty() && stack.isSuitableFor(blockState);
         if (selectedSlotFlag2 && !selectedSlotFlag || selectedSlotFlag2 == selectedSlotFlag && selectedSlotLimit > selectedSlotValue) {
            selectedSlotValue = selectedSlotLimit;
            selectedSlot = i;
            selectedSlotFlag = selectedSlotFlag2;
         }
      }

      if (selectedSlot != inventory.getSelectedSlot()) {
         inventory.setSelectedSlot(selectedSlot);
      }

      return selectedSlot;
   }

   public int findDrillSlot(ClientPlayerEntity player) {
      String drillText = this.drill.drillToolName == null ? "" : this.drill.drillToolName.trim().toLowerCase();
      if (drillText.isEmpty()) {
         return -1;
      } else {
         PlayerInventory inventory = player.getInventory();

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getName().getString().toLowerCase().contains(drillText)) {
               return i;
            }
         }

         return -1;
      }
   }

   private Vec3d findAimPointOnOutline(ClientPlayerEntity player, BlockPos pos2, double boxesOutlineValue) {
      World entityWorld = player.getEntityWorld();
      ArrayList arrayList = new ArrayList<>(entityWorld.getBlockState(pos2).getOutlineShape(entityWorld, pos2).getBoundingBoxes());
      arrayList.sort((obj, obj2) -> Double.compare(getBoxWeight(((net.minecraft.util.math.Box) (obj2))), getBoxWeight(((net.minecraft.util.math.Box) (obj)))));
      Vec3d eyePos = player.getEyePos();

      for (Box box : (Iterable<net.minecraft.util.math.Box>) (arrayList)) {
         Box offset = box.offset(pos2);
         Vec3d center = offset.getCenter();
         double y = eyePos.y > offset.maxY ? offset.maxY - 0.01 : (eyePos.y < offset.minY ? offset.minY + 0.01 : center.y);
         Vec3d vec3d = new Vec3d(center.x, y, center.z);
         if (canRaycastHit(player, pos2, vec3d, boxesOutlineValue)) {
            return vec3d;
         }

         if (canRaycastHit(player, pos2, center, boxesOutlineValue)) {
            return center;
         }
      }

      return null;
   }

   public boolean consumeDrillFallbackFlag() {
      boolean blockPosFlag = this.usedDrillFallback;
      this.usedDrillFallback = false;
      return blockPosFlag;
   }

   public void invalidateAimPoint(BlockPos pos2) {
      this.outlineAimBlocks.add(pos2.toImmutable());
      if (pos2.equals(this.currentTarget)) {
         this.targetAimPoint = null;
      }

      if (pos2.equals(this.previewTarget)) {
         this.previewAimPoint = null;
      }
   }

   private static double getBoxWeight(Box box) {
      return box.getLengthX() * box.getLengthZ() + box.getLengthY() * Math.max(box.getLengthX(), box.getLengthZ());
   }

   public boolean canReachBlock(BlockPos pos2, double minecraftClientValue) {
      ClientPlayerEntity player = this.minecraftClient.player;
      return player != null && this.resolveAimPoint(player, pos2, minecraftClientValue) != null;
   }

   public boolean aimAndSelectTool(BlockPos pos2, double immutableMinecraftValue, boolean immutableMinecraftFlag) {
      ClientPlayerEntity player = this.minecraftClient.player;
      ClientWorld world = this.minecraftClient.world;
      if (player != null && world != null && this.minecraftClient.interactionManager != null) {
         BlockState blockState = world.getBlockState(pos2);
         if (blockState.isAir()) {
            this.clearTargetAndCancelBreaking();
            return false;
         } else {
            boolean allowsFlag = immutableMinecraftFlag && this.findDrillSlot(player) >= 0;
            if (this.breakGuard != null && !this.breakGuard.allows(pos2, allowsFlag)) {
               if (this.currentTarget != null) {
                  this.clearTargetAndCancelBreaking();
               }

               return false;
            } else {
               Vec3d vec3d = this.resolveAimPoint(player, pos2, immutableMinecraftValue);
               if (vec3d == null) {
                  if (!pos2.equals(this.currentTarget)) {
                     this.clearTargetAndCancelBreaking();
                  }

                  return false;
               } else {
                  if (!pos2.equals(this.currentTarget)) {
                     this.minecraftClient.interactionManager.cancelBlockBreaking();
                     this.currentTarget = pos2.toImmutable();
                  }

                  this.targetAimPoint = vec3d;
                  this.selectToolSlot(player, blockState, immutableMinecraftFlag);
                  if (!this.isCrosshairStillOn(pos2)) {
                     this.yawClamp.rotateTowardPoint(player, vec3d, 32.0F);
                  }

                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   private static Direction[] sortDirectionsToward(Vec3d vec, Vec3d vec2) {
      Direction[] clone = (Direction[])Direction.values().clone();
      double[] doubles = new double[clone.length];

      for (int i = 0; i < clone.length; i++) {
         doubles[i] = clone[i].getOffsetX() * (vec.x - vec2.x)
            + clone[i].getOffsetY() * (vec.y - vec2.y)
            + clone[i].getOffsetZ() * (vec.z - vec2.z);
      }

      for (int j = 1; j < clone.length; j++) {
         Direction direction = clone[j];
         double offsetLimit = doubles[j];

         int index;
         for (index = j - 1; index >= 0 && doubles[index] < offsetLimit; index--) {
            clone[index + 1] = clone[index];
            doubles[index + 1] = doubles[index];
         }

         clone[index + 1] = direction;
         doubles[index + 1] = offsetLimit;
      }

      return clone;
   }

   public BlockPos getCurrentTarget() {
      return this.currentTarget;
   }

   public void clearTargetAndCancelBreaking() {
      this.currentTarget = null;
      this.targetAimPoint = null;
      this.previewTarget = null;
      this.previewAimPoint = null;
      if (this.minecraftClient.interactionManager != null) {
         this.minecraftClient.interactionManager.cancelBlockBreaking();
      }
   }

   private static boolean canRaycastHit(ClientPlayerEntity player, BlockPos pos2, Vec3d vec, double posRaycastFactor) {
      Vec3d eyePos = player.getEyePos();
      if (eyePos.squaredDistanceTo(vec) > posRaycastFactor * posRaycastFactor) {
         return false;
      } else {
         BlockHitResult raycast = player.getEntityWorld().raycast(new RaycastContext(eyePos, vec, ShapeType.OUTLINE, FluidHandling.NONE, player));
         return raycast.getType() == Type.BLOCK && raycast.getBlockPos().equals(pos2);
      }
   }

   public BlockAimController(MinecraftClient minecraftClient, Drill drill) {
      this.lastEyeX = Double.NaN;
      this.outlineAimBlocks = new HashSet<>();
      this.minecraftClient = minecraftClient;
      this.drill = drill;
   }

   public void clearTarget() {
      this.currentTarget = null;
      this.targetAimPoint = null;
   }

   private boolean isCrosshairStillOn(BlockPos pos2) {
      ClientPlayerEntity player = this.minecraftClient.player;
      if (player == null) {
         return false;
      } else {
         Vec3d eyePos = player.getEyePos();
         boolean naN = Double.isNaN(this.lastEyeX);
         double deltaX = eyePos.x - this.lastEyeX;
         double deltaY = eyePos.y - this.lastEyeY;
         double deltaZ = eyePos.z - this.lastEyeZ;
         this.lastEyeX = eyePos.x;
         this.lastEyeY = eyePos.y;
         this.lastEyeZ = eyePos.z;
         return !naN && !(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > 1.0E-4)
            ? this.minecraftClient.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK && blockHitResult.getBlockPos().equals(pos2)
            : false;
      }
   }

   private void selectToolSlot(ClientPlayerEntity player, BlockState blockState, boolean selectedSlotFlag) {
      PlayerInventory inventory = player.getInventory();
      int drillText = this.findDrillSlot(player);
      if (drillText < 0) {
         selectFastestHotbarSlot(player, blockState);
      } else if (selectedSlotFlag) {
         if (inventory.getSelectedSlot() != drillText) {
            inventory.setSelectedSlot(drillText);
         }
      } else {
         int selectedSlot = -1;
         float selectedSlotValue = -1.0F;

         for (int i = 0; i < 9; i++) {
            if (i != drillText) {
               ItemStack stack = inventory.getStack(i);
               if (!stack.isEmpty() && stack.isSuitableFor(blockState)) {
                  float miningSpeedMultiplier = stack.getMiningSpeedMultiplier(blockState);
                  if (miningSpeedMultiplier > selectedSlotValue) {
                     selectedSlotValue = miningSpeedMultiplier;
                     selectedSlot = i;
                  }
               }
            }
         }

         if (selectedSlot < 0) {
            if (inventory.getStack(drillText).isSuitableFor(blockState)) {
               selectedSlot = drillText;
               this.usedDrillFallback = true;
            } else {
               for (int j = 0; j < 9; j++) {
                  if (j != drillText) {
                     ItemStack stack2 = inventory.getStack(j);
                     float selectedSlotLimit = stack2.isEmpty() ? 1.0F : stack2.getMiningSpeedMultiplier(blockState);
                     if (selectedSlotLimit > selectedSlotValue) {
                        selectedSlotValue = selectedSlotLimit;
                        selectedSlot = j;
                     }
                  }
               }
            }
         }

         if (selectedSlot >= 0 && selectedSlot != inventory.getSelectedSlot()) {
            inventory.setSelectedSlot(selectedSlot);
         }
      }
   }

   public void clearOutlineAimBlocks() {
      this.outlineAimBlocks.clear();
   }

   public boolean aimAtBlock(BlockPos pos2, double vecImmutableValue) {
      ClientPlayerEntity player = this.minecraftClient.player;
      ClientWorld world = this.minecraftClient.world;
      if (player == null || world == null) {
         return false;
      } else if (world.getBlockState(pos2).isAir()) {
         return false;
      } else {
         Vec3d vec3d = pos2.equals(this.previewTarget) && this.previewAimPoint != null && canRaycastHit(player, pos2, this.previewAimPoint, vecImmutableValue)
            ? this.previewAimPoint
            : this.findAimPoint(player, pos2, vecImmutableValue);
         if (vec3d == null) {
            return false;
         } else {
            this.previewTarget = pos2.toImmutable();
            this.previewAimPoint = vec3d;
            if (!this.isCrosshairStillOn(pos2)) {
               this.yawClamp.rotateTowardPoint(player, vec3d, 32.0F);
            }

            return true;
         }
      }
   }

   public void setBreakGuard(BlockAimController.DigGuard handler) {
      this.breakGuard = handler;
   }

   public interface DigGuard {
      boolean allows(BlockPos pos, boolean allowsFlag);
   }
}
