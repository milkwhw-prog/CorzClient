package com.corz.client.utils;

import com.corz.client.world.StateBlockUtilsCenter;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.BlockStateUtilsFluid;
import com.corz.client.utils.AreaMinerController;
import com.corz.client.utils.MinerPathNavigator;

public final class FluidSafetyController {
   private final AreaMinerController water;
   private int updateTickCounter;
   private BlockStateUtilsFluid.Placement currentPlacement;
   private final Map<BlockPos, Integer> plugAttemptCounts;
   private final Set<Long> triedPlugFaceKeys;
   private int aimSettledTicks;
   private int placeCooldownTicks;
   private boolean inputLockActive;
   private BlockStateUtilsFluid.Target currentTarget;
   private static final String[] FOOD_NAME_FRAGMENTS = new String[]{
      "cooked_",
      "bread",
      "carrot",
      "potato",
      "apple",
      "berry",
      "berries",
      "melon",
      "pie",
      "stew",
      "soup",
      "cookie",
      "steak",
      "porkchop",
      "mutton",
      "chicken",
      "beef",
      "salmon",
      "cod"
   };
   private boolean hazardActive;
   private long lastLavaWarningMs;
   private int aimAttemptTicks;
   private boolean eatingActive;
   private final Map<BlockPos, Integer> skippedCellCooldowns = new HashMap<>();
   private final ArrayDeque<BlockPos> recentSafePositions;
   private final MinecraftClient minecraftClient;
   private int eatWaitTicks;

   static boolean hasInventoryStackMatching(ClientPlayerEntity player, Predicate<ItemStack> predicate) {
      if (player == null) {
         return false;
      } else {
         PlayerInventory inventory = player.getInventory();

         for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isInLavaOrDrowning(ClientPlayerEntity player, World world) {
      if (player != null && world != null) {
         BlockPos blockPos = player.getBlockPos();
         return !player.isInLava() && !StateBlockUtilsCenter.canStateBlock(world, blockPos) && !StateBlockUtilsCenter.canStateBlock(world, blockPos.up())
            ? player.isSubmergedInWater() && player.getAir() < player.getMaxAir() / 3
            : true;
      } else {
         return false;
      }
   }

   private BlockPos findNearestSafeStandPos(ClientPlayerEntity player, World world) {
      BlockPos blockPos = player.getBlockPos();
      BlockPos dequeArray = null;
      double dequeArrayLimit = Double.MAX_VALUE;

      for (BlockPos pos : this.recentSafePositions) {
         int deltaY = pos.getY() - blockPos.getY();
         if (deltaY <= 2 && deltaY >= -3) {
            double horizontalDistanceSq = MinerPathNavigator.horizontalDistanceSq(player, pos);
            if (!(horizontalDistanceSq > 64.0) && isSafeStandPos(world, pos)) {
               double dequeArrayValue = horizontalDistanceSq + Math.abs(deltaY) * 2.0 + countSolidNeighbors(world, pos) * 6.0;
               if (dequeArrayValue < dequeArrayLimit) {
                  dequeArrayLimit = dequeArrayValue;
                  dequeArray = pos;
               }
            }
         }
      }

      if (dequeArray != null) {
         return dequeArray;
      } else {
         for (int i = 1; i <= 3; i++) {
            for (int j = -i; j <= i; j++) {
               for (int k = -i; k <= i; k++) {
                  if (Math.max(Math.abs(j), Math.abs(k)) == i) {
                     for (int m = 1; m >= -1; m--) {
                        BlockPos add = blockPos.add(j, m, k);
                        if (isSafeStandPos(world, add)) {
                           double dequeArrayValue2 = MinerPathNavigator.horizontalDistanceSq(player, add) + Math.abs(m) * 2.0 + countSolidNeighbors(world, add) * 6.0;
                           if (dequeArrayValue2 < dequeArrayLimit) {
                              dequeArrayLimit = dequeArrayValue2;
                              dequeArray = add;
                           }
                        }
                     }
                  }
               }
            }

            if (dequeArray != null) {
               return dequeArray;
            }
         }

         return null;
      }
   }

   FluidSafetyController(AreaMinerController water, MinecraftClient minecraftClient) {
      this.plugAttemptCounts = new HashMap<>();
      this.triedPlugFaceKeys = new HashSet<>();
      this.recentSafePositions = new ArrayDeque<>();
      this.water = water;
      this.minecraftClient = minecraftClient;
   }

   private void tickCooldowns() {
      if (this.placeCooldownTicks > 0) {
         this.placeCooldownTicks--;
      }

      if (!this.skippedCellCooldowns.isEmpty()) {
         this.skippedCellCooldowns.replaceAll((pos, integer) -> integer - 1);
         this.skippedCellCooldowns.values().removeIf(integer2 -> integer2 <= 0);
      }

      if (++this.updateTickCounter >= 200) {
         this.updateTickCounter = 0;
         this.plugAttemptCounts.clear();
         this.triedPlugFaceKeys.clear();
      }
   }

   private boolean isTargetStillPluggable(ClientPlayerEntity player, World world, Vec3d vec, double oppositeFactor) {
      BlockPos cell = this.currentTarget.cell();
      if (this.skippedCellCooldowns.containsKey(cell)) {
         return false;
      } else if (!BlockStateUtilsFluid.isLavaOrWater(world.getFluidState(this.currentTarget.fluid()))) {
         return false;
      } else if (!world.getBlockState(cell).isReplaceable()) {
         return false;
      } else if (player.getBoundingBox().intersects(new Box(cell))) {
         return false;
      } else {
         return vec.squaredDistanceTo(this.currentPlacement.aim()) > oppositeFactor * oppositeFactor
            ? false
            : BlockStateUtilsFluid.isSolidPlaceableBlock(world, this.currentPlacement.anchor());
      }
   }

   int findOrFetchHotbarSlot(ClientPlayerEntity player, Predicate<ItemStack> predicate) {
      if (player == null) {
         return -1;
      } else {
         PlayerInventory inventory = player.getInventory();

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
               return i;
            }
         }

         for (int j = 9; j < 36; j++) {
            ItemStack center = inventory.getStack(j);
            if (!center.isEmpty() && predicate.test(center)) {
               if (!ExtUtils.isAction()) {
                  ExtUtils.handleExt(() -> this.swapMatchingStackToHotbar(predicate), true);
               }

               return -2;
            }
         }

         return -1;
      }
   }

   private boolean tickPlugCurrentTarget(ClientPlayerEntity player, World world, Vec3d vec, double waterShortValue) {
      BlockStateUtilsFluid.Target next = this.currentTarget;
      BlockStateUtilsFluid.Placement oppositeOffsetData = this.currentPlacement;
      BlockPos cell = next.cell();
      String waterShortText = next.lava() ? "lava" : "water";
      this.water.getSyntheticInput().clearInput();
      this.lockInput();
      this.water.releaseAimTargets();
      Hand handSelected = this.selectPlugItemHand(player, world, next);
      HitResult raycast = player.raycast(waterShortValue, 0.0F, false);
      if (handSelected != null && this.placeCooldownTicks <= 0 && BlockStateUtilsFluid.hitMatchesPlacement(raycast, oppositeOffsetData.anchor(), oppositeOffsetData.face(), cell)) {
         BlockHitResult blockHitResult = (BlockHitResult)raycast;
         BlockPos immutable = blockHitResult.getBlockPos().offset(blockHitResult.getSide()).toImmutable();
         if (immutable.equals(cell) && !player.getBoundingBox().intersects(new Box(immutable)) && BlockStateUtilsFluid.isSolidPlaceableBlock(world, blockHitResult.getBlockPos())) {
            boolean still = world.getFluidState(immutable).isStill();
            ActionResult interactBlock = this.minecraftClient.interactionManager.interactBlock(player, handSelected, blockHitResult);
            if (interactBlock.isAccepted()) {
               player.swingHand(handSelected);
            }

            this.placeCooldownTicks = 2;
            this.water.getSyntheticInput().sneakHoldTicks = 2;
            if (this.plugAttemptCounts.merge(immutable, 1, Integer::sum) >= 4) {
               this.plugAttemptCounts.remove(immutable);
               this.skippedCellCooldowns.put(immutable, 300);
               this.water.setStatusText("the server keeps refusing the plug at " + immutable.toShortString() + " \u2014 moving on");
            }

            this.water.registerPlugBlock(immutable, still);
            this.water.setStatusText("blocked " + waterShortText + " " + describeFluidKind(next) + " at " + immutable.toShortString());
            this.clearCurrentTarget();
            this.releaseInputLock();
            return true;
         }
      }

      float[] clamp = computeYawPitchToward(vec, oppositeOffsetData.aim());
      float absWrapDegrees = Math.abs(MathHelper.wrapDegrees(clamp[0] - player.getYaw()));
      float abs2 = Math.abs(clamp[1] - player.getPitch());
      rotateTowardAngles(player, clamp);
      if (handSelected == null) {
         this.water.setStatusText("waiting for a block to reach the hotbar to stop the " + waterShortText);
         if (++this.aimAttemptTicks > 50) {
            this.giveUpOnCell(next, oppositeOffsetData);
         }

         return true;
      } else if (absWrapDegrees < 1.2F && abs2 < 1.2F && ++this.aimSettledTicks > 6) {
         this.triedPlugFaceKeys.add(BlockStateUtilsFluid.encodePosDirectionKey(cell, oppositeOffsetData.face()));
         this.clearCurrentTarget();
         return true;
      } else if (++this.aimAttemptTicks > 50) {
         this.giveUpOnCell(next, oppositeOffsetData);
         return true;
      } else {
         this.water.setStatusText("blocking " + waterShortText + " " + describeFluidKind(next) + " at " + cell.toShortString());
         return true;
      }
   }

   private void swapMatchingStackToHotbar(Predicate<ItemStack> predicate) {
      ClientPlayerEntity player = this.minecraftClient.player;
      if (player != null && this.minecraftClient.interactionManager != null && player.currentScreenHandler == player.playerScreenHandler) {
         PlayerInventory inventory = player.getInventory();

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
               return;
            }
         }

         for (int j = 9; j < 36; j++) {
            ItemStack center = inventory.getStack(j);
            if (!center.isEmpty() && predicate.test(center)) {
               this.minecraftClient.interactionManager.clickSlot(player.playerScreenHandler.syncId, j, getHotbarSwapSlot(inventory), SlotActionType.SWAP, player);
               return;
            }
         }
      }
   }

   private void releaseInputLock() {
      if (this.inputLockActive) {
         this.inputLockActive = false;
         this.water.getSyntheticInput().jumpBlocked = false;
      }
   }

   private static int countSolidNeighbors(World world, BlockPos pos) {
      int southNorth = 0;

      for (int i = 0; i <= 1; i++) {
         BlockPos up = pos.up(i);
         if (StateBlockUtilsCenter.canStateBlock(world, up.north())) {
            southNorth++;
         }

         if (StateBlockUtilsCenter.canStateBlock(world, up.south())) {
            southNorth++;
         }

         if (StateBlockUtilsCenter.canStateBlock(world, up.east())) {
            southNorth++;
         }

         if (StateBlockUtilsCenter.canStateBlock(world, up.west())) {
            southNorth++;
         }
      }

      if (StateBlockUtilsCenter.canStateBlock(world, pos.up(2))) {
         southNorth++;
      }

      return southNorth;
   }

   private void clearCurrentTarget() {
      this.currentTarget = null;
      this.currentPlacement = null;
      this.aimAttemptTicks = 0;
      this.aimSettledTicks = 0;
   }

   private Hand selectPlugItemHand(ClientPlayerEntity player, World world, BlockStateUtilsFluid.Target target) {
      PlayerInventory inventory = player.getInventory();
      if (!target.lava()) {
         if (isSponge(player.getOffHandStack())) {
            return Hand.OFF_HAND;
         }

         int stackEmpty = this.findOrFetchHotbarSlot(player, FluidSafetyController::isSponge);
         if (stackEmpty >= 0) {
            inventory.setSelectedSlot(stackEmpty);
            return Hand.MAIN_HAND;
         }

         if (stackEmpty == -2) {
            return null;
         }
      }

      int stackInventory = this.findBestPlugSlot(player, target.lava(), this.hazardActive);
      if (stackInventory < 0) {
         return stackInventory == -1 && BlockStateUtilsFluid.getBlockPlacementPriority(player.getOffHandStack(), target.lava()) >= 0 ? Hand.OFF_HAND : null;
      } else {
         if (inventory.getSelectedSlot() != stackInventory) {
            inventory.setSelectedSlot(stackInventory);
         }

         return Hand.MAIN_HAND;
      }
   }

   public void resetState() {
      this.skippedCellCooldowns.clear();
      this.plugAttemptCounts.clear();
      this.triedPlugFaceKeys.clear();
      this.recentSafePositions.clear();
      this.placeCooldownTicks = 0;
      this.hazardActive = false;
      this.inputLockActive = false;
      this.clearCurrentTarget();
      if (this.eatingActive && this.minecraftClient.options != null) {
         this.minecraftClient.options.useKey.setPressed(false);
      }

      this.eatingActive = false;
      this.eatWaitTicks = 0;
   }

   private static float signWithDeadzone(double playerEntityLimit) {
      if (Math.abs(playerEntityLimit) < 0.12) {
         return 0.0F;
      } else {
         return playerEntityLimit > 0.0 ? 1.0F : -1.0F;
      }
   }

   private static float[] computeYawPitchToward(Vec3d vec, Vec3d vec2) {
      double deltaX = vec2.x - vec.x;
      double deltaY = vec2.y - vec.y;
      double deltaZ = vec2.z - vec.z;
      double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
      float clampValue = (float)(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0);
      float clamp = MathHelper.clamp((float)(-Math.toDegrees(Math.atan2(deltaY, distance))), -90.0F, 90.0F);
      return new float[]{clampValue, clamp};
   }

   private void recordSafePosition(ClientPlayerEntity player, World world) {
      if (player.isOnGround() && !player.isInLava() && !player.isSubmergedInWater()) {
         BlockPos immutable = player.getBlockPos().toImmutable();
         if (isSafeStandPos(world, immutable)) {
            if (!immutable.equals(this.recentSafePositions.peekFirst())) {
               this.recentSafePositions.remove(immutable);
               this.recentSafePositions.addFirst(immutable);

               while (this.recentSafePositions.size() > 48) {
                  this.recentSafePositions.removeLast();
               }
            }
         }
      }
   }

   static boolean isSponge(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.getItem() == Blocks.SPONGE.asItem();
   }

   private boolean tickFluidPlugging(ClientPlayerEntity player, World world) {
      double blockInteractionRange = player.getBlockInteractionRange();
      Vec3d eyePos = player.getEyePos();
      if (this.currentTarget != null && !this.isTargetStillPluggable(player, world, eyePos, blockInteractionRange)) {
         this.clearCurrentTarget();
      }

      if (this.currentTarget == null) {
         List fluidState = BlockStateUtilsFluid.findFluidTargets(world, player.getBlockPos(), player.getBoundingBox(), eyePos, player.getRotationVec(1.0F));
         if (fluidState.isEmpty()) {
            return false;
         }

         boolean handStack = this.hasPlugItem(player, true);
         boolean plugItem2 = this.hasPlugItem(player, false);
         double boundingBoxValue = (blockInteractionRange + 1.0) * (blockInteractionRange + 1.0);
         Box expand = player.getBoundingBox().expand(3.0);
         int boundingBoxCounter = 0;

         for (BlockStateUtilsFluid.Target next : (Iterable<com.corz.client.utils.BlockStateUtilsFluid.Target>) (fluidState)) {
            if (!this.skippedCellCooldowns.containsKey(next.cell())
               && (next.lava() ? handStack : plugItem2)
               && !(eyePos.squaredDistanceTo(Vec3d.ofCenter(next.cell())) > boundingBoxValue)
               && (next.lava() || next.kind() != BlockStateUtilsFluid.Kind.RECEDING)
               && (next.lava() || next.kind() != BlockStateUtilsFluid.Kind.FLOW || expand.intersects(new Box(next.cell())))) {
               if (++boundingBoxCounter > 24) {
                  break;
               }

               BlockStateUtilsFluid.Placement oppositeOffsetData = BlockStateUtilsFluid.findPlacementToward(world, eyePos, blockInteractionRange, next.cell(), this.triedPlugFaceKeys);
               if (oppositeOffsetData != null) {
                  this.currentTarget = next;
                  this.currentPlacement = oppositeOffsetData;
                  this.aimAttemptTicks = 0;
                  this.aimSettledTicks = 0;
                  break;
               }
            }
         }

         if (this.currentTarget == null) {
            this.warnIfNoPlugBlocks(player, fluidState);
            this.releaseInputLock();
            return false;
         }
      }

      return this.tickPlugCurrentTarget(player, world, eyePos, blockInteractionRange);
   }

   private void warnIfNoPlugBlocks(ClientPlayerEntity player, List<BlockStateUtilsFluid.Target> list) {
      boolean plugCobblestoneFlag = false;

      for (BlockStateUtilsFluid.Target target : list) {
         if (target.lava()) {
            plugCobblestoneFlag = true;
            break;
         }
      }

      if (plugCobblestoneFlag && !this.hasPlugItem(player, true)) {
         long timeMillis = System.currentTimeMillis();
         if (timeMillis - this.lastLavaWarningMs >= 15000L) {
            this.lastLavaWarningMs = timeMillis;
            this.water.sendChatMessage("\u00a7cLava next to you and no blocks to plug it \u2014 put cobblestone in your hotbar!");
         }
      }
   }

   private static void rotateTowardAngles(ClientPlayerEntity player, float[] clampPitchValues) {
      float yaw = player.getYaw();
      float wrapDegrees = MathHelper.wrapDegrees(clampPitchValues[0] - yaw);
      float clampPitchValue = Math.abs(wrapDegrees) <= 2.0F ? wrapDegrees : MathHelper.clamp(wrapDegrees * 0.6F, -32.0F, 32.0F);
      player.setYaw(yaw + clampPitchValue);
      float pitch = player.getPitch();
      float planValuesItem = clampPitchValues[1] - pitch;
      float clampPitchValue3 = Math.abs(planValuesItem) <= 2.0F ? planValuesItem : MathHelper.clamp(planValuesItem * 0.6F, -32.0F, 32.0F);
      player.setPitch(MathHelper.clamp(pitch + clampPitchValue3, -90.0F, 90.0F));
   }

   boolean hasPlugItem(ClientPlayerEntity player, boolean handStackFlag) {
      if (player == null) {
         return false;
      } else if (handStackFlag || !isSponge(player.getOffHandStack()) && !hasInventoryStackMatching(player, FluidSafetyController::isSponge)) {
         return BlockStateUtilsFluid.getBlockPlacementPriority(player.getOffHandStack(), handStackFlag) >= 0
            ? true
            : hasInventoryStackMatching(player, lavax -> BlockStateUtilsFluid.getBlockPlacementPriority(lavax, handStackFlag) >= 0);
      } else {
         return true;
      }
   }

   private void lockInput() {
      this.water.getSyntheticInput().jumpBlocked = true;
      this.inputLockActive = true;
   }

   private void giveUpOnCell(BlockStateUtilsFluid.Target target, BlockStateUtilsFluid.Placement placement) {
      this.skippedCellCooldowns.put(target.cell(), 120);
      this.triedPlugFaceKeys.add(BlockStateUtilsFluid.encodePosDirectionKey(target.cell(), placement.face()));
      this.water.setStatusText("can't reach a face to plug " + target.cell().toShortString() + " \u2014 trying another spot");
      this.clearCurrentTarget();
   }

   private static int getHotbarSwapSlot(PlayerInventory playerInventory) {
      for (int i = 0; i < 9; i++) {
         if (playerInventory.getStack(i).isEmpty()) {
            return i;
         }
      }

      for (int j = 8; j >= 0; j--) {
         ItemStack stack = playerInventory.getStack(j);
         if (stack.getItem() instanceof BlockItem && !isSponge(stack)) {
            return j;
         }
      }

      for (int k = 8; k >= 0; k--) {
         ItemStack stack2 = playerInventory.getStack(k);
         if (!stack2.isDamageable() && !isFoodItem(stack2) && k != playerInventory.getSelectedSlot()) {
            return k;
         }
      }

      return 8;
   }

   private int findBestPlugSlot(ClientPlayerEntity player, boolean stackInventoryFlag, boolean stackInventoryFlag2) {
      PlayerInventory inventory = player.getInventory();
      int stackInventory = -1;
      int stackInventoryLimit = -1;

      for (int i = 0; i < 9; i++) {
         int oreBurnable = BlockStateUtilsFluid.getBlockPlacementPriority(inventory.getStack(i), stackInventoryFlag);
         if (oreBurnable > stackInventoryLimit) {
            stackInventoryLimit = oreBurnable;
            stackInventory = i;
         }
      }

      if (stackInventoryLimit >= 2) {
         return stackInventory;
      } else {
         int stackInventoryBound = -1;

         for (int j = 9; j < 36; j++) {
            stackInventoryBound = Math.max(stackInventoryBound, BlockStateUtilsFluid.getBlockPlacementPriority(inventory.getStack(j), stackInventoryFlag));
         }

         if (stackInventoryBound > stackInventoryLimit && (!stackInventoryFlag2 || stackInventory < 0)) {
            int stackInventoryValue = stackInventoryBound;
            int stackEmpty = this.findOrFetchHotbarSlot(player, want -> BlockStateUtilsFluid.getBlockPlacementPriority(want, stackInventoryFlag) >= stackInventoryValue);
            if (stackEmpty >= 0 || stackEmpty == -2) {
               return stackEmpty;
            }
         }

         return stackInventory;
      }
   }

   private static String describeFluidKind(BlockStateUtilsFluid.Target target) {
      return switch (target.kind()) {
         case SOURCE -> "source";
         case SEAL -> "next to its source";
         case FLOW -> "flow";
         case RECEDING -> "beside you";
      };
   }

   static boolean isFoodItem(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         String lowerCase = stack.getItem().getTranslationKey().toLowerCase();

         for (String translationSalmonText : FOOD_NAME_FRAGMENTS) {
            if (lowerCase.contains(translationSalmonText)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean runSafetyTick() {
      ClientPlayerEntity player = this.minecraftClient.player;
      ClientWorld world = this.minecraftClient.world;
      if (player != null && world != null) {
         this.tickCooldowns();
         this.recordSafePosition(player, world);
         boolean airSubmerged = this.isInLavaOrDrowning(player, world);
         this.hazardActive = airSubmerged || player.isOnFire();
         if (airSubmerged) {
            this.stopEating();
            this.clearCurrentTarget();
            if (this.tickEscapeHazard(player, world)) {
               return true;
            }
         }

         if (this.water.getDrillSettings().placeBlocks && this.tickFluidPlugging(player, world)) {
            this.stopEating();
            return true;
         } else {
            this.releaseInputLock();
            return this.tickAutoEatFood(player);
         }
      } else {
         return false;
      }
   }

   private static boolean isSafeStandPos(World world, BlockPos pos) {
      return !StateBlockUtilsCenter.canDown(world, pos) ? false : !StateBlockUtilsCenter.canStateFluid(world, pos.up());
   }

   private boolean tickAutoEatFood(ClientPlayerEntity player) {
      if (!this.water.getDrillSettings().autoEatEnabled) {
         if (this.eatingActive) {
            this.eatingActive = false;
            this.minecraftClient.options.useKey.setPressed(false);
         }

         return false;
      } else {
         int foodLevel = player.getHungerManager().getFoodLevel();
         if (!this.eatingActive && foodLevel <= 10 && this.findOrFetchHotbarSlot(player, FluidSafetyController::isFoodItem) >= 0) {
            this.eatingActive = true;
         }

         if (!this.eatingActive) {
            return false;
         } else if (foodLevel >= 20) {
            this.eatingActive = false;
            this.minecraftClient.options.useKey.setPressed(false);
            return false;
         } else {
            int stackEmpty = this.findOrFetchHotbarSlot(player, FluidSafetyController::isFoodItem);
            if (stackEmpty == -2 && ++this.eatWaitTicks <= 100) {
               this.minecraftClient.options.useKey.setPressed(false);
               this.water.getSyntheticInput().clearInput();
               this.water.setStatusText("waiting for food to reach the hotbar (" + foodLevel + "/20)...");
               return true;
            } else {
               this.eatWaitTicks = 0;
               if (stackEmpty < 0) {
                  this.eatingActive = false;
                  this.minecraftClient.options.useKey.setPressed(false);
                  return false;
               } else {
                  player.getInventory().setSelectedSlot(stackEmpty);
                  this.minecraftClient.options.useKey.setPressed(true);
                  this.water.getSyntheticInput().clearInput();
                  this.water.setStatusText("eating (" + foodLevel + "/20)...");
                  return true;
               }
            }
         }
      }
   }

   private boolean tickEscapeHazard(ClientPlayerEntity player, World world) {
      BlockPos dequeArray = this.findNearestSafeStandPos(player, world);
      if (dequeArray == null) {
         return false;
      } else {
         this.water.releaseAimTargets();
         double lavaGettingFactor = dequeArray.getX() + 0.5 - player.getX();
         double lavaGettingFactor2 = dequeArray.getZ() + 0.5 - player.getZ();
         double angle = Math.toRadians(player.getYaw());
         double sin = Math.sin(angle);
         double cos = Math.cos(angle);
         double lavaGettingValue = lavaGettingFactor2 * cos - lavaGettingFactor * sin;
         double playerEntityLimit = lavaGettingFactor2 * sin + lavaGettingFactor * cos;
         this.inputLockActive = false;
         this.water.getSyntheticInput().jumpBlocked = false;
         this.water.getSyntheticInput().setMovementInput(signWithDeadzone(lavaGettingValue), signWithDeadzone(playerEntityLimit), true, false);
         boolean lavaGettingFlag = player.isInLava() || StateBlockUtilsCenter.canStateBlock(world, player.getBlockPos());
         this.water.setStatusText((lavaGettingFlag ? "in lava" : "running out of air") + " \u2014 getting out to " + dequeArray.toShortString());
         return true;
      }
   }

   private void stopEating() {
      if (this.eatingActive) {
         this.eatingActive = false;
         this.eatWaitTicks = 0;
         if (this.minecraftClient.options != null) {
            this.minecraftClient.options.useKey.setPressed(false);
         }
      }
   }
}
