package com.corz.client.categories.combat;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class AutoCart extends Module {
   private final Set<Integer> handledProjectileIds = new HashSet<>();
   private long nextPlaceAllowedMs;

   private BlockPos predictProjectileLandingPos(PersistentProjectileEntity persistentProjectileEntity) {
      Vec3d entityPos = persistentProjectileEntity.getEntityPos();
      Vec3d velocity = persistentProjectileEntity.getVelocity();

      for (int i = 1; i <= 80; i++) {
         Vec3d vec3d = entityPos;
         entityPos = entityPos.add(velocity);
         velocity = this.applyProjectileDrag(vec3d, velocity);
         BlockHitResult raycastCollider = this.raycastColliderBetween(vec3d, entityPos, persistentProjectileEntity);
         if (raycastCollider.getType() != Type.MISS) {
            return this.resolvePlacementPos(raycastCollider);
         }

         if (entityPos.y < mc.world.getBottomY() - 2) {
            break;
         }
      }

      return null;
   }

   @Override
   protected void onEnable() {
      this.handledProjectileIds.clear();
      this.nextPlaceAllowedMs = 0L;
   }

   public AutoCart() {
      super("AutoCart", IconsPngType.COMBAT);
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         var hashSet = new HashSet();

         for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PersistentProjectileEntity persistentProjectileEntity && !(persistentProjectileEntity instanceof TridentEntity) && persistentProjectileEntity.getOwner() == mc.player) {
               int id = persistentProjectileEntity.getId();
               hashSet.add(id);
               if (!this.handledProjectileIds.contains(id) && this.hasRailAndTntMinecart() && this.isProjectileFlying(persistentProjectileEntity)) {
                  BlockPos bottomVelocity = this.predictProjectileLandingPos(persistentProjectileEntity);
                  if (bottomVelocity != null) {
                     if (mc.player.squaredDistanceTo(Vec3d.ofCenter(bottomVelocity)) > 25.0) {
                        this.handledProjectileIds.add(id);
                     } else if (this.tryPlaceRailAndCart(bottomVelocity) || persistentProjectileEntity.isOnGround()) {
                        this.handledProjectileIds.add(id);
                     }
                  }
               }
            }
         }

         this.handledProjectileIds.retainAll(hashSet);
      }
   }

   @Override
   protected void onDisable() {
      this.handledProjectileIds.clear();
      this.nextPlaceAllowedMs = 0L;
   }

   private void moveTntMinecartToHotbar(int minecartTntLimit) {
      if (minecartTntLimit >= 0 && minecartTntLimit <= 8) {
         if (!mc.player.getInventory().getStack(minecartTntLimit).isOf(Items.TNT_MINECART)) {
            int inventoryStack = this.findItemSlot(Items.TNT_MINECART, 9, 36);
            if (inventoryStack != -1) {
               mc.interactionManager
                  .clickSlot(mc.player.currentScreenHandler.syncId, inventoryStack, minecartTntLimit, SlotActionType.SWAP, mc.player);
            }
         }
      }
   }

   private boolean hasTntMinecartAt(BlockPos pos) {
      Box expand = new Box(pos).expand(0.2, 1.0, 0.2);

      for (Entity entity : mc.world.getOtherEntities(null, expand)) {
         if (entity instanceof TntMinecartEntity) {
            return true;
         }
      }

      return false;
   }

   private BlockPos resolvePlacementPos(BlockHitResult blockHitResult) {
      BlockPos blockPos = blockHitResult.getBlockPos();
      if (this.isRailAt(blockPos)) {
         return blockPos;
      } else {
         BlockPos pos2 = blockHitResult.getSide() == Direction.UP ? blockPos.up() : blockPos;
         if (this.isValidRailSpot(pos2)) {
            return pos2;
         } else {
            BlockPos blockAbovePos = blockPos.up();
            if (this.isValidRailSpot(blockAbovePos)) {
               return blockAbovePos;
            } else {
               BlockPos down = blockPos.down();
               return this.isValidRailSpot(down) ? down : null;
            }
         }
      }
   }

   private int findItemSlot(Item item2, int inventoryStackValue, int inventoryStackLimit) {
      for (int i = inventoryStackValue; i < inventoryStackLimit; i++) {
         if (mc.player.getInventory().getStack(i).isOf(item2)) {
            return i;
         }
      }

      return -1;
   }

   private boolean isValidRailSpot(BlockPos pos2) {
      if (this.isRailAt(pos2)) {
         return true;
      } else {
         BlockState blockState = mc.world.getBlockState(pos2);
         if (!this.isReplaceableVegetation(blockState)) {
            return false;
         } else {
            BlockPos down = pos2.down();
            return mc.world.getBlockState(down).isSideSolidFullSquare(mc.world, down, Direction.UP);
         }
      }
   }

   private boolean tryPlaceRailAndCart(BlockPos pos) {
      int selectedSlotValue = this.findHotbarSlot(Items.RAIL);
      int hotbarSlot = this.findHotbarSlot(Items.TNT_MINECART);
      if (selectedSlotValue != -1 && hotbarSlot != -1) {
         int selectedSlot = mc.player.getInventory().getSelectedSlot();

         boolean selectedSlot2;
         try {
            if (this.isRailAt(pos) || this.placeRail(pos, selectedSlotValue)) {
               if (this.hasTntMinecartAt(pos)) {
                  return true;
               }

               if (System.currentTimeMillis() >= this.nextPlaceAllowedMs) {
                  if (this.placeTntMinecart(pos, hotbarSlot)) {
                     this.nextPlaceAllowedMs = System.currentTimeMillis() + 100L;
                     this.moveTntMinecartToHotbar(hotbarSlot);
                  }

                  return this.hasTntMinecartAt(pos);
               }

               return false;
            }

            selectedSlot2 = false;
         } finally {
            mc.player.getInventory().setSelectedSlot(selectedSlot);
         }

         return selectedSlot2;
      } else {
         return false;
      }
   }

   private int findHotbarSlot(Item item2) {
      return this.findItemSlot(item2, 0, 9);
   }

   private boolean isProjectileFlying(PersistentProjectileEntity persistentProjectileEntity) {
      return !persistentProjectileEntity.isOnGround() && persistentProjectileEntity.getVelocity().lengthSquared() > 1.0E-4;
   }

   private boolean hasRailAndTntMinecart() {
      return this.findHotbarSlot(Items.RAIL) != -1 && this.findHotbarSlot(Items.TNT_MINECART) != -1;
   }

   private boolean placeTntMinecart(BlockPos pos, int selectedSlot) {
      mc.player.getInventory().setSelectedSlot(selectedSlot);
      BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.15, pos.getZ() + 0.5), Direction.UP, pos, false);
      ActionResult interactBlock = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
      if (interactBlock.isAccepted()) {
         mc.player.swingHand(Hand.MAIN_HAND);
         return true;
      } else {
         return false;
      }
   }

   private BlockHitResult raycastColliderBetween(Vec3d vec, Vec3d vec2, Entity entity) {
      return mc.world.raycast(new RaycastContext(vec, vec2, ShapeType.COLLIDER, FluidHandling.NONE, entity));
   }

   private Vec3d applyProjectileDrag(Vec3d vec, Vec3d vec2) {
      FluidState fluidState = mc.world.getBlockState(BlockPos.ofFloored(vec)).getFluidState();
      double flooredValue = fluidState.isIn(FluidTags.WATER) ? 0.6 : 0.99;
      return vec2.multiply(flooredValue).add(0.0, -0.05, 0.0);
   }

   private boolean placeRail(BlockPos pos, int selectedSlot) {
      mc.player.getInventory().setSelectedSlot(selectedSlot);
      BlockPos down = pos.down();
      BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), Direction.UP, down, false);
      ActionResult interactBlock = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
      if (interactBlock.isAccepted()) {
         mc.player.swingHand(Hand.MAIN_HAND);
         return true;
      } else {
         return this.isRailAt(pos);
      }
   }

   private boolean isReplaceableVegetation(BlockState blockState) {
      if (blockState.isReplaceable()) {
         return true;
      } else if (blockState.isIn(BlockTags.SNOW)) {
         return true;
      } else {
         return blockState.isIn(BlockTags.SWORD_EFFICIENT)
            ? true
            : blockState.isOf(Blocks.SHORT_GRASS)
               || blockState.isOf(Blocks.TALL_GRASS)
               || blockState.isOf(Blocks.FERN)
               || blockState.isOf(Blocks.LARGE_FERN)
               || blockState.isOf(Blocks.DEAD_BUSH)
               || blockState.isOf(Blocks.VINE)
               || blockState.isOf(Blocks.SNOW);
      }
   }

   private boolean isRailAt(BlockPos pos2) {
      return mc.world.getBlockState(pos2).isIn(BlockTags.RAILS);
   }
}
