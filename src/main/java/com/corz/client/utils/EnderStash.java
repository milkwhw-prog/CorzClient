package com.corz.client.utils;

import com.corz.client.inventory.InventoryStackUtilsHandle;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HotbarSlotUtils;
import com.corz.client.utils.AnchorBreaker;
import com.corz.client.utils.HunterBody;
import com.corz.client.utils.DigMover;
import com.corz.client.utils.StashPlan;

public final class EnderStash {
   private long lastInteractTick;
   public static final Set<Block> FILLER_BLOCKS = Set.of(
      Blocks.COBBLESTONE,
      Blocks.COBBLED_DEEPSLATE,
      Blocks.DIRT,
      Blocks.STONE,
      Blocks.TUFF,
      Blocks.ANDESITE,
      Blocks.DIORITE,
      Blocks.GRANITE,
      Blocks.GRAVEL,
      Blocks.DEEPSLATE,
      Blocks.NETHERRACK,
      Blocks.SAND,
      Blocks.GRASS_BLOCK
   );
   private final AnchorBreaker anchorBreaker;
   private boolean protectedAreaDetected;
   private boolean chestScreenOpen;
   private int enderChestCountBefore;
   private final HunterBody hunterBody;
   private long pickupStartTick;
   private String failureReason;
   private boolean openingChest;
   private int anchorsInInventory;
   private int syncId;
   private long currentTick;
   private final MinecraftClient client = MinecraftClient.getInstance();
   private final Object inventoryAccess;
   private long stepStartTick;
   private BlockPos chestPos;
   private final DigMover digMover;
   private String completionNote;
   private int placeAttempts;
   private int step;
   private int anchorsStoredCount;

   private BlockPos findChestPlacementSpot() {
      BlockPos floored = this.hunterBody.getFeetBlock();
      BlockPos add = null;
      double enderStashLimit = Double.MAX_VALUE;

      for (int i = -2; i <= 2; i++) {
         for (int j = -2; j <= 2; j++) {
            for (int k = -1; k <= 1; k++) {
               BlockPos pos = floored.add(i, k, j);
               if (this.isValidChestSpot(pos)) {
                  double enderStashLimit2 = this.client.player.getEyePos().distanceTo(Vec3d.ofCenter(pos)) + Math.abs(k) * 0.5;
                  if (!(enderStashLimit2 < 1.4) && !(enderStashLimit2 > 3.4) && enderStashLimit2 < enderStashLimit) {
                     enderStashLimit = enderStashLimit2;
                     add = pos;
                  }
               }
            }
         }
      }

      return add;
   }

   public String getFailureReason() {
      return this.failureReason;
   }

   private void openChest(BlockPos pos2) {
      ClientPlayerEntity player = this.client.player;
      if (player.getEyePos().distanceTo(Vec3d.ofCenter(pos2)) > 4.0) {
         this.digMover
            .moveAlongPath(
               this.currentTick,
               "open" + pos2.asLong(),
               posx -> Vec3d.ofCenter(posx.up()).squaredDistanceTo(Vec3d.ofCenter(pos2)) <= 10.240000000000002 && !posx.equals(pos2),
               pos2,
               pos2,
               false
            );
      } else {
         Vec3d eyePos = this.hunterBody.getVisibleAimPoint(pos2, 4.0);
         if (eyePos != null) {
            this.hunterBody.aimAt(eyePos, 1);
            if (!player.isSneaking() && this.hunterBody.isLookingToward(eyePos, 6.0F) && this.currentTick - this.lastInteractTick >= 6L) {
               BlockHitResult missed = this.hunterBody.raycastForward(4.0);
               if (missed.getType() == Type.BLOCK && missed.getBlockPos().equals(pos2)) {
                  if (this.client.world.getBlockState(pos2).isOf(Blocks.ENDER_CHEST)) {
                     this.lastInteractTick = this.currentTick;
                     this.openingChest = true;
                     this.client.interactionManager.interactBlock(player, Hand.MAIN_HAND, missed);
                     player.swingHand(Hand.MAIN_HAND);
                  }
               }
            }
         }
      }
   }

   public static boolean hasChestAndSilkPickaxe(ClientPlayerEntity player, AnchorBreaker anchorBreaker) {
      PlayerInventory inventory = player.getInventory();
      boolean inventoryEnderFlag = false;
      boolean inventoryEnderFlag2 = false;

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(Items.ENDER_CHEST)) {
            inventoryEnderFlag = true;
         }

         if (anchorBreaker.isSilkTouchPickaxeUsable(stack)) {
            inventoryEnderFlag2 = true;
         }
      }

      return inventoryEnderFlag && inventoryEnderFlag2;
   }

   public boolean tryDropFillerBlock() {
      ClientPlayerEntity player = this.client.player;
      if (player != null && player.currentScreenHandler == player.playerScreenHandler && !ExtUtils.isAction()) {
         PlayerInventory inventory = player.getInventory();

         for (int i = 0; i <= 35; i++) {
            if (isFillerBlock(inventory.getStack(i))) {
               if (!InventoryStackUtilsHandle.canFlagForObject(this.inventoryAccess)) {
                  return false;
               }

               int screenValue = i;
               ExtUtils.handleExt(
                  () -> {
                     ClientPlayerEntity var2x = this.client.player;
                     if (var2x != null && this.client.interactionManager != null && var2x.currentScreenHandler == var2x.playerScreenHandler) {
                        if (isFillerBlock(var2x.getInventory().getStack(screenValue))) {
                           this.client
                              .interactionManager
                              .clickSlot(var2x.playerScreenHandler.syncId, HotbarSlotUtils.toScreenSlot(screenValue), 1, SlotActionType.THROW, var2x);
                        }
                     }
                  },
                  true
               );
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isValidChestSpot(BlockPos pos) {
      BlockState blockState = this.client.world.getBlockState(pos);
      BlockState blockState2 = this.client.world.getBlockState(pos.down());
      BlockState blockState3 = this.client.world.getBlockState(pos.up());
      if (!blockState.isAir()) {
         return false;
      } else if (blockState2.isOf(Blocks.RESPAWN_ANCHOR) || blockState2.hasBlockEntity() || blockState2.getCollisionShape(this.client.world, pos.down()).isEmpty()) {
         return false;
      } else if (!blockState2.isOpaqueFullCube()) {
         return false;
      } else if (!blockState3.getCollisionShape(this.client.world, pos.up()).isEmpty()) {
         return false;
      } else {
         Box box = new Box(pos);
         return !this.client.player.getBoundingBox().intersects(box) && !this.client.player.getBoundingBox().intersects(new Box(pos.up()))
            ? this.client.world.getOtherEntities(this.client.player, box.expand(0.1)).isEmpty()
            : false;
      }
   }

   public EnderStash.StashStatus tick(long enderChestTime) {
      this.currentTick = enderChestTime;
      ClientPlayerEntity player = this.client.player;
      if (player != null && this.client.world != null && this.client.interactionManager != null) {
         if (this.currentTick - this.stepStartTick > 300L) {
            return this.fail("ender chest step " + this.step + " timed out");
         } else {
            if (this.protectedAreaDetected) {
               this.protectedAreaDetected = false;
               if (this.step <= 1) {
                  return this.fail("this area is protected");
               }
            }

            if (!player.isOnGround() && this.step != 1) {
               return EnderStash.StashStatus.RUNNING;
            } else {
               switch (this.step) {
                  case 0:
                     boolean enderChestFlag = this.chestPos != null && this.client.world.getBlockState(this.chestPos).isOf(Blocks.ENDER_CHEST);
                     boolean hasKit = enderChestFlag || hasChestAndSilkPickaxe(player, this.anchorBreaker);
                     if (!enderChestFlag && hasKit && this.chestPos == null) {
                        this.chestPos = this.findChestPlacementSpot();
                     }

                     switch (StashPlan.getKitPlacement(enderChestFlag, hasKit, this.chestPos != null)) {
                        case OPEN:
                           this.setStep(1);
                           return EnderStash.StashStatus.RUNNING;
                        case NO_KIT:
                           return this.fail("no ender chest or Silk Touch pickaxe");
                        case NO_SPOT:
                           return this.fail("no room to place the ender chest");
                        default:
                           if (!this.tryPlaceChest(this.chestPos) && this.currentTick - this.stepStartTick > 120L) {
                              this.chestPos = null;
                              if (++this.placeAttempts >= 3) {
                                 return this.fail("could not place the ender chest");
                              }

                              this.stepStartTick = this.currentTick;
                           }

                           return EnderStash.StashStatus.RUNNING;
                     }
                  case 1:
                     if (this.isContainerScreenOpen()) {
                        this.syncId = player.currentScreenHandler.syncId;
                        this.chestScreenOpen = true;
                        this.setStep(2);
                        return EnderStash.StashStatus.RUNNING;
                     }

                     if (!this.client.world.getBlockState(this.chestPos).isOf(Blocks.ENDER_CHEST)) {
                        if (++this.placeAttempts >= 3) {
                           return this.fail("could not place the ender chest");
                        }

                        this.setStep(0);
                        this.chestPos = null;
                        return EnderStash.StashStatus.RUNNING;
                     }

                     this.openChest(this.chestPos);
                     break;
                  case 2:
                     if (!this.isContainerScreenOpen()) {
                        this.setStep(1);
                        return EnderStash.StashStatus.RUNNING;
                     }

                     ScreenHandler currentScreenHandler = player.currentScreenHandler;
                     int slotsSlot = findInventorySlotInContainer(currentScreenHandler, stack -> stack.isOf(Items.RESPAWN_ANCHOR));
                     if (slotsSlot >= 0 && findContainerSlot(currentScreenHandler, ItemStack::isEmpty) >= 0) {
                        if (this.currentTick - this.lastInteractTick >= 4L) {
                           this.lastInteractTick = this.currentTick;
                           this.client.interactionManager.clickSlot(currentScreenHandler.syncId, slotsSlot, 0, SlotActionType.QUICK_MOVE, player);
                        }
                        break;
                     }

                     this.anchorsStoredCount = Math.max(0, this.anchorsInInventory - countItem(player, Items.RESPAWN_ANCHOR));
                     if (this.anchorsStoredCount == 0) {
                        this.completionNote = slotsSlot < 0 ? "no anchors to store" : "ender chest full";
                     }

                     this.setStep(3);
                     return EnderStash.StashStatus.RUNNING;
                  case 3:
                     if (this.isContainerScreenOpen()) {
                        player.closeHandledScreen();
                        this.chestScreenOpen = false;
                        return EnderStash.StashStatus.RUNNING;
                     }

                     this.enderChestCountBefore = countItem(player, Items.ENDER_CHEST);
                     this.setStep(4);
                     break;
                  case 4:
                     if (!this.client.world.getBlockState(this.chestPos).isOf(Blocks.ENDER_CHEST)) {
                        this.setStep(5);
                        this.pickupStartTick = this.currentTick;
                        return EnderStash.StashStatus.RUNNING;
                     }

                     if (countEmptySlots(player) < 1 && !this.tryDropFillerBlock()) {
                        return this.fail("no room to pick the ender chest up");
                     }

                     if (player.getEyePos().distanceTo(Vec3d.ofCenter(this.chestPos)) > 4.0) {
                        DigMover.PathMoveResult pathStep = this.digMover
                           .moveAlongPath(
                              this.currentTick,
                              "chest" + this.chestPos.asLong(),
                              pos -> Vec3d.ofCenter(pos.up()).squaredDistanceTo(Vec3d.ofCenter(this.chestPos)) <= 10.240000000000002 && !pos.equals(this.chestPos),
                              this.chestPos,
                              this.chestPos,
                              false
                           );
                        if (pathStep == DigMover.PathMoveResult.FAILED) {
                           return this.fail("cannot reach the ender chest");
                        }

                        return EnderStash.StashStatus.RUNNING;
                     }

                     AnchorBreaker.BreakStatus hunterBody = this.anchorBreaker.tickBreakTarget(this.chestPos, true);
                     if (hunterBody == AnchorBreaker.BreakStatus.NO_TOOL) {
                        return this.fail("no Silk Touch pickaxe");
                     }

                     if (hunterBody == AnchorBreaker.BreakStatus.WORKING) {
                        this.anchorBreaker.setBreakRequested(true);
                     }
                     break;
                  case 5:
                     if (countItem(player, Items.ENDER_CHEST) > this.enderChestCountBefore) {
                        return this.completionNote != null ? this.fail(this.completionNote) : this.finishSuccess();
                     }

                     ItemEntity entityEntities = findNearestItemEntity(player, 8.0, itemEntity -> itemEntity.getStack().isOf(Items.ENDER_CHEST));
                     if (entityEntities != null && this.currentTick - this.pickupStartTick < 200L) {
                        BlockPos blockPos = entityEntities.getBlockPos();
                        DigMover.PathMoveResult moveAlongPath = this.digMover
                           .moveAlongPath(
                              this.currentTick,
                              "pick" + blockPos.asLong(),
                              pos2 -> pos2.getX() == blockPos.getX() && pos2.getZ() == blockPos.getZ() && Math.abs(pos2.getY() - blockPos.getY()) <= 1,
                              blockPos,
                              blockPos,
                              false
                           );
                        if (moveAlongPath == DigMover.PathMoveResult.ARRIVED) {
                           this.hunterBody.moveTowardPoint(entityEntities.getEntityPos(), false);
                        }

                        return EnderStash.StashStatus.RUNNING;
                     }

                     if (this.currentTick - this.pickupStartTick > 60L) {
                        return this.fail("lost the ender chest");
                     }
                     break;
                  default:
                     return this.fail("bad step");
               }

               return EnderStash.StashStatus.RUNNING;
            }
         }
      } else {
         return this.fail("not in game");
      }
   }

   public void start(long enderChestTime) {
      this.step = 0;
      this.currentTick = enderChestTime;
      this.stepStartTick = enderChestTime;
      this.chestPos = null;
      this.syncId = -1;
      this.enderChestCountBefore = -1;
      this.pickupStartTick = -1L;
      this.placeAttempts = 0;
      this.failureReason = "";
      this.chestScreenOpen = false;
      this.openingChest = false;
      this.anchorsInInventory = this.client.player == null ? 0 : countItem(this.client.player, Items.RESPAWN_ANCHOR);
      this.anchorsStoredCount = 0;
      this.completionNote = null;
      this.protectedAreaDetected = false;
   }

   public void cleanup() {
      if (this.shouldCloseChestScreen() && this.client.player != null) {
         this.client.player.closeHandledScreen();
      }

      this.chestScreenOpen = false;
      this.openingChest = false;
      InventoryStackUtilsHandle.handle(this.inventoryAccess);
   }

   private void swapItemToHotbar(Predicate<ItemStack> predicate) {
      ClientPlayerEntity player = this.client.player;
      if (!ExtUtils.isAction() && player.currentScreenHandler == player.playerScreenHandler) {
         PlayerInventory inventory = player.getInventory();

         for (int i = 9; i <= 35; i++) {
            if (predicate.test(inventory.getStack(i))) {
               if (!InventoryStackUtilsHandle.canFlagForObject(this.inventoryAccess)) {
                  return;
               }

               int screenValue = i;
               int stackPickaxes = AnchorBreaker.findFreeHotbarSlot(inventory);
               ExtUtils.handleExt(
                  () -> {
                     ClientPlayerEntity var4x = this.client.player;
                     if (var4x != null && this.client.interactionManager != null && var4x.currentScreenHandler == var4x.playerScreenHandler) {
                        if (predicate.test(var4x.getInventory().getStack(screenValue))) {
                           this.client
                              .interactionManager
                              .clickSlot(
                                 var4x.playerScreenHandler.syncId,
                                 HotbarSlotUtils.toScreenSlot(screenValue),
                                 HotbarSlotUtils.requireHotbarSlot(stackPickaxes),
                                 SlotActionType.SWAP,
                                 var4x
                              );
                        }
                     }
                  },
                  true
               );
               return;
            }
         }
      }
   }

   private EnderStash.StashStatus finishSuccess() {
      this.cleanup();
      return EnderStash.StashStatus.DONE;
   }

   private boolean tryPlaceChest(BlockPos pos2) {
      ClientPlayerEntity player = this.client.player;
      int blockValue = -1;

      for (int i = 0; i < 9; i++) {
         if (player.getInventory().getStack(i).isOf(Items.ENDER_CHEST)) {
            blockValue = i;
         }
      }

      if (blockValue < 0) {
         this.swapItemToHotbar(stack -> stack.isOf(Items.ENDER_CHEST));
         return false;
      } else {
         this.hunterBody.pressKeys(32);
         BlockPos down = pos2.down();
         Vec3d add = Vec3d.ofCenter(down).add(0.0, 0.5, 0.0);
         this.hunterBody.aimAt(add, 1);
         if (player.getInventory().getSelectedSlot() != blockValue) {
            this.hunterBody.selectHotbarSlot(blockValue);
            return false;
         } else if (player.isSneaking()
            && this.hunterBody.isLookingToward(add, 5.0F)
            && this.hunterBody.isSlotSelectedAndSettled(blockValue)
            && this.currentTick - this.lastInteractTick >= 6L) {
            BlockHitResult missed = this.hunterBody.raycastForward(4.0);
            if (missed.getType() != Type.BLOCK || !missed.getBlockPos().equals(down) || missed.getSide() != Direction.UP) {
               return false;
            } else if (this.client.world.getBlockState(missed.getBlockPos()).isOf(Blocks.RESPAWN_ANCHOR)) {
               return false;
            } else {
               this.lastInteractTick = this.currentTick;
               this.client.interactionManager.interactBlock(player, Hand.MAIN_HAND, missed);
               player.swingHand(Hand.MAIN_HAND);
               return this.client.world.getBlockState(pos2).isOf(Blocks.ENDER_CHEST);
            }
         } else {
            return false;
         }
      }
   }

   private EnderStash.StashStatus fail(String text) {
      this.failureReason = text;
      this.cleanup();
      return EnderStash.StashStatus.FAILED;
   }

   public boolean shouldCloseChestScreen() {
      return StashPlan.isStashAllowed(this.isContainerScreenOpen(), this.chestScreenOpen, this.openingChest, this.step);
   }

   private static int findContainerSlot(ScreenHandler screenHandler, Predicate<ItemStack> predicate) {
      int slotStackLimit = screenHandler.slots.size() - 36;

      for (int i = 0; i < slotStackLimit; i++) {
         if (predicate.test(screenHandler.getSlot(i).getStack())) {
            return i;
         }
      }

      return -1;
   }

   public EnderStash(HunterBody hunterBody, AnchorBreaker anchorBreaker, DigMover digMover, Object object) {
      this.syncId = -1;
      this.enderChestCountBefore = -1;
      this.pickupStartTick = -1L;
      this.lastInteractTick = -2305843009213693952L;
      this.failureReason = "";
      this.hunterBody = hunterBody;
      this.anchorBreaker = anchorBreaker;
      this.digMover = digMover;
      this.inventoryAccess = object;
   }

   public void markProtectedArea() {
      this.protectedAreaDetected = true;
   }

   private void setStep(int protectedAreaValue) {
      this.step = protectedAreaValue;
      this.stepStartTick = this.currentTick;
   }

   public boolean isContainerScreenOpen() {
      ClientPlayerEntity player = this.client.player;
      return player != null
         && this.client.currentScreen instanceof HandledScreen
         && !(this.client.currentScreen instanceof InventoryScreen)
         && player.currentScreenHandler != null
         && player.currentScreenHandler != player.playerScreenHandler;
   }

   public int getAnchorsStoredCount() {
      return this.anchorsStoredCount;
   }

   public static ItemEntity findNearestItemEntity(ClientPlayerEntity player, double entityEntitiesValue, Predicate<ItemEntity> predicate) {
      Vec3d entityPos = player.getEntityPos();
      Box box = new Box(entityPos.x - entityEntitiesValue, entityPos.y - entityEntitiesValue, entityPos.z - entityEntitiesValue, entityPos.x + entityEntitiesValue, entityPos.y + entityEntitiesValue, entityPos.z + entityEntitiesValue);
      ItemEntity entityEntities = null;
      double entityEntitiesLimit = Double.MAX_VALUE;

      for (ItemEntity itemEntity : player.getEntityWorld().getEntitiesByClass(ItemEntity.class, box, predicate)) {
         double squaredDistanceTo = itemEntity.squaredDistanceTo(player);
         if (squaredDistanceTo < entityEntitiesLimit) {
            entityEntitiesLimit = squaredDistanceTo;
            entityEntities = itemEntity;
         }
      }

      return entityEntities;
   }

   public static int countEmptySlots(ClientPlayerEntity player) {
      int inventoryStack = 0;
      PlayerInventory inventory = player.getInventory();

      for (int i = 0; i <= 35; i++) {
         if (inventory.getStack(i).isEmpty()) {
            inventoryStack++;
         }
      }

      return inventoryStack;
   }

   private static int findInventorySlotInContainer(ScreenHandler screenHandler, Predicate<ItemStack> predicate) {
      int slotsSlotValue = screenHandler.slots.size() - 36;
      if (slotsSlotValue < 0) {
         return -1;
      } else {
         for (int i = screenHandler.slots.size() - 1; i >= slotsSlotValue; i--) {
            if (predicate.test(screenHandler.getSlot(i).getStack())) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int countItem(ClientPlayerEntity player, Item item2) {
      int stackHand = 0;
      PlayerInventory inventory = player.getInventory();

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(item2)) {
            stackHand += stack.getCount();
         }
      }

      ItemStack offHandStack = player.getOffHandStack();
      if (offHandStack.isOf(item2)) {
         stackHand += offHandStack.getCount();
      }

      return stackHand;
   }

   public static boolean hasRoomForAnchor(ClientPlayerEntity player) {
      PlayerInventory inventory = player.getInventory();

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isEmpty()) {
            return true;
         }

         if (stack.isOf(Items.RESPAWN_ANCHOR) && stack.getCount() < stack.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   public static boolean isFillerBlock(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem && FILLER_BLOCKS.contains(blockItem.getBlock());
   }

   public static enum StashStatus {
      RUNNING,
      DONE,
      FAILED;
   }
}
