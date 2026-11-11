package com.corz.client.utils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import com.corz.client.utils.SmoothAimRotator;
import com.corz.client.utils.ScriptedMovementInput;

public final class PillarUpPlacer {
   private PillarUpPlacer.Phase phase;
   private BlockPos pillarStartPos;
   private BlockPos placedBlockPos;
   private final MinecraftClient minecraftClient;
   private final LinkedHashSet<BlockPos> recentPlacedPositions;
   private final SmoothAimRotator aimRotator = new SmoothAimRotator();
   private int successfulRiseCount;
   private final ScriptedMovementInput movementInput;
   private int placeCooldownTicks;
   private int failedSettleCount;

   public boolean tryPlaceBlockAt(World world, BlockPos pos2) {
      if (world != null && pos2 != null && this.minecraftClient.interactionManager != null && this.minecraftClient.player != null) {
         ClientPlayerEntity player = this.minecraftClient.player;
         HitResult crosshairTarget = this.minecraftClient.crosshairTarget;
         if (crosshairTarget instanceof BlockHitResult blockHitResult && crosshairTarget.getType() == Type.BLOCK && blockHitResult.getBlockPos().offset(blockHitResult.getSide()).equals(pos2)) {
            ActionResult interactBlock = this.minecraftClient.interactionManager.interactBlock(player, Hand.MAIN_HAND, blockHitResult);
            if (interactBlock.isAccepted()) {
               player.swingHand(Hand.MAIN_HAND);
            }

            this.movementInput.sneakHoldTicks = 4;
            this.recordPlacedPosition(pos2);
            return true;
         } else {
            Vec3d offsetBlock = findPlacementAimPoint(world, player, pos2);
            if (offsetBlock != null) {
               this.aimRotator.rotateTowardPoint(player, offsetBlock, 32.0F);
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public void clearRecentPlacements() {
      this.recentPlacedPositions.clear();
   }

   private static Vec3d findPlacementAimPoint(World world, ClientPlayerEntity player, BlockPos pos2) {
      Vec3d eyePos = player.getEyePos();
      Vec3d offsetBlock = null;
      double offsetBlockValue = 20.25;

      for (Direction direction : Direction.values()) {
         BlockPos offset = pos2.offset(direction);
         BlockState blockState = world.getBlockState(offset);
         if (!blockState.isAir() && blockState.getFluidState().isEmpty() && blockState.isOpaqueFullCube()) {
            Direction opposite = direction.getOpposite();
            double x = offset.getX() + 0.5 + opposite.getOffsetX() * 0.5;
            double z = offset.getZ() + 0.5 + opposite.getOffsetZ() * 0.5;
            double y = opposite.getAxis() == Axis.Y ? offset.getY() + 0.5 + opposite.getOffsetY() * 0.5 : offset.getY() + 0.5;
            Vec3d vec3d = new Vec3d(x, y, z);
            double squaredDistanceTo = eyePos.squaredDistanceTo(vec3d);
            if (!(squaredDistanceTo > offsetBlockValue)) {
               BlockHitResult raycast = world.raycast(new RaycastContext(eyePos, vec3d, ShapeType.COLLIDER, FluidHandling.NONE, player));
               if (raycast != null && raycast.getType() == Type.BLOCK) {
                  boolean equals = raycast.getBlockPos().equals(offset);
                  boolean equals2 = raycast.getBlockPos().offset(raycast.getSide()).equals(pos2);
                  if (!equals && !equals2) {
                     continue;
                  }
               }

               offsetBlockValue = squaredDistanceTo;
               offsetBlock = vec3d;
            }
         }
      }

      return offsetBlock;
   }

   public PillarUpPlacer(MinecraftClient minecraftClient, ScriptedMovementInput stateFluidInput) {
      this.recentPlacedPositions = new LinkedHashSet<>();
      this.phase = PillarUpPlacer.Phase.READY;
      this.minecraftClient = minecraftClient;
      this.movementInput = stateFluidInput;
   }

   public static int findOpaqueBlockHotbarSlot(ClientPlayerEntity player) {
      PlayerInventory inventory = player.getInventory();

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock().getDefaultState().isOpaqueFullCube()) {
            return i;
         }
      }

      return -1;
   }

   private static boolean isStandingAboveBlock(ClientPlayerEntity player, BlockPos pos) {
      return player.getBoundingBox().minY >= pos.getY() + 1.0 - 0.02;
   }

   public void recordPlacedPosition(BlockPos pos2) {
      BlockPos immutable = pos2.toImmutable();
      this.recentPlacedPositions.remove(immutable);
      this.recentPlacedPositions.add(immutable);

      while (this.recentPlacedPositions.size() > 64) {
         Iterator iterator = this.recentPlacedPositions.iterator();
         iterator.next();
         iterator.remove();
      }
   }

   public int getSuccessfulRiseCount() {
      return this.successfulRiseCount;
   }

   public static boolean hasOpaqueBlockInHotbar(ClientPlayerEntity player) {
      return player != null && findOpaqueBlockHotbarSlot(player) >= 0;
   }

   public boolean selectBlockAndPlaceAt(ClientPlayerEntity player, BlockPos pos2, double selectedSlotValue) {
      ClientWorld world = this.minecraftClient.world;
      if (world == null || this.minecraftClient.interactionManager == null) {
         return false;
      } else if (!world.getBlockState(pos2).isReplaceable()) {
         return false;
      } else {
         int opaqueCube = findOpaqueBlockHotbarSlot(player);
         if (opaqueCube < 0) {
            return false;
         } else {
            PlayerInventory inventory = player.getInventory();
            if (inventory.getSelectedSlot() != opaqueCube) {
               inventory.setSelectedSlot(opaqueCube);
            }

            if (this.placeCooldownTicks > 0) {
               this.placeCooldownTicks--;
               return true;
            } else if (this.tryPlaceBlockAt(world, pos2)) {
               this.placeCooldownTicks = 2;
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void resetRiseState() {
      this.phase = PillarUpPlacer.Phase.READY;
      this.pillarStartPos = null;
      this.placeCooldownTicks = 0;
      this.successfulRiseCount = 0;
      this.placedBlockPos = null;
      this.failedSettleCount = 0;
   }

   public boolean tickPillarRise(ClientPlayerEntity player, boolean[] risingPlacedFlags) {
      ClientWorld world = this.minecraftClient.world;
      if (world != null && this.minecraftClient.interactionManager != null) {
         int opaqueCube = findOpaqueBlockHotbarSlot(player);
         if (opaqueCube < 0) {
            return false;
         } else if (this.failedSettleCount >= 3) {
            this.failedSettleCount = 0;
            return false;
         } else {
            PlayerInventory inventory = player.getInventory();
            if (inventory.getSelectedSlot() != opaqueCube) {
               inventory.setSelectedSlot(opaqueCube);
            }

            if (this.placeCooldownTicks > 0) {
               this.placeCooldownTicks--;
            }

            switch (this.phase) {
               case READY:
                  pitchPlayerDown(player);
                  if (player.isOnGround() && this.placeCooldownTicks == 0 && player.getPitch() >= 85.0F) {
                     this.pillarStartPos = player.getBlockPos();
                     risingPlacedFlags[0] = true;
                     this.phase = PillarUpPlacer.Phase.RISING;
                  }
                  break;
               case RISING:
                  if (this.pillarStartPos == null) {
                     this.phase = PillarUpPlacer.Phase.READY;
                  } else {
                     BlockPos blockPos = new BlockPos(player.getBlockX(), this.pillarStartPos.getY(), player.getBlockZ());
                     pitchPlayerDown(player);
                     if (!world.getBlockState(blockPos).isAir()) {
                        this.phase = PillarUpPlacer.Phase.PLACED;
                        this.successfulRiseCount = 0;
                        this.placedBlockPos = blockPos.toImmutable();
                     } else if (player.isOnGround()) {
                        this.successfulRiseCount++;
                        this.phase = PillarUpPlacer.Phase.READY;
                        this.placeCooldownTicks = 5;
                     } else if (this.placeCooldownTicks == 0 && isStandingAboveBlock(player, blockPos) && this.tryPlaceBlockAt(world, blockPos)) {
                        this.placeCooldownTicks = 4;
                     }
                  }
                  break;
               case PLACED:
                  if (player.isOnGround()) {
                     this.phase = PillarUpPlacer.Phase.SETTLE;
                     this.placeCooldownTicks = 5;
                  }
                  break;
               case SETTLE:
                  if (this.placeCooldownTicks == 0) {
                     this.failedSettleCount = this.placedBlockPos != null && world.getBlockState(this.placedBlockPos).isAir() ? this.failedSettleCount + 1 : 0;
                     this.placedBlockPos = null;
                     this.phase = PillarUpPlacer.Phase.READY;
                     this.pillarStartPos = null;
                  }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static void pitchPlayerDown(ClientPlayerEntity player) {
      player.setPitch(Math.min(90.0F, player.getPitch() + 45.0F));
   }

   public Set<BlockPos> getRecentPlacedPositions() {
      return this.recentPlacedPositions;
   }

   private static enum Phase {
      READY,
      RISING,
      PLACED,
      SETTLE;
   }
}
