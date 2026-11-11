package com.corz.client.utils;

import com.corz.client.inventory.InventoryStackUtilsHandle;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HotbarSlotUtils;
import com.corz.client.utils.ToolBreakerUtils;
import com.corz.client.utils.HunterBody;

public final class AnchorBreaker {
   private boolean breakRequested;
   private final Object inventoryAccess;
   private final HunterBody hunterBody;
   private BlockPos targetBlock;
   private final MinecraftClient client = MinecraftClient.getInstance();
   private boolean targetedThisTick;
   private int toolSlotIndex = -1;
   private boolean swapInProgress;

   public boolean isSilkTouchPickaxeUsable(ItemStack stack) {
      return stack != null
         && !stack.isEmpty()
         && stack.isIn(ItemTags.PICKAXES)
         && hasEnchantment(stack, Enchantments.SILK_TOUCH)
         && (!stack.isDamageable() || stack.getMaxDamage() - stack.getDamage() >= 25);
   }

   public boolean isReadyToBreak() {
      if (this.targetedThisTick && this.breakRequested && this.targetBlock != null) {
         ClientPlayerEntity player = this.client.player;
         if (player == null || this.client.world == null || this.client.currentScreen != null || !player.isOnGround() || player.isUsingItem()) {
            return false;
         } else {
            return this.toolSlotIndex >= 0 && this.hunterBody.isSlotSelectedAndSettled(this.toolSlotIndex) ? HunterBody.isCrosshairOnBlock(this.client, this.targetBlock) : false;
         }
      } else {
         return false;
      }
   }

   public void cancelBreaking() {
      if (this.targetBlock != null && this.client.interactionManager != null) {
         this.client.interactionManager.cancelBlockBreaking();
      }

      this.targetBlock = null;
      this.targetedThisTick = false;
      this.breakRequested = false;
   }

   public boolean isCrosshairOnTarget() {
      return HunterBody.isCrosshairOnBlock(this.client, this.targetBlock);
   }

   public static int countUsablePickaxes(PlayerInventory playerInventory) {
      int stack = 0;

      for (int i = 0; i <= 35; i++) {
         if (isUsableMiningPickaxe(playerInventory.getStack(i))) {
            stack++;
         }
      }

      return stack;
   }

   static int findFreeHotbarSlot(PlayerInventory playerInventory) {
      for (int i = 0; i <= 8; i++) {
         if (playerInventory.getStack(i).isEmpty()) {
            return i;
         }
      }

      for (int j = 0; j <= 8; j++) {
         ItemStack stack = playerInventory.getStack(j);
         if (!stack.isIn(ItemTags.PICKAXES) && stack.get(DataComponentTypes.FOOD) == null && !stack.isOf(Items.ENDER_CHEST) && !stack.isOf(Items.TOTEM_OF_UNDYING)) {
            return j;
         }
      }

      return playerInventory.getSelectedSlot() == 8 ? 7 : 8;
   }

   public static boolean isUsableMiningPickaxe(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return false;
      } else {
         return !stack.isOf(Items.DIAMOND_PICKAXE) && !stack.isOf(Items.NETHERITE_PICKAXE) ? false : stack.getMaxDamage() - stack.getDamage() >= 25;
      }
   }

   public AnchorBreaker(HunterBody hunterBody, Object object) {
      this.hunterBody = hunterBody;
      this.inventoryAccess = object;
   }

   public void cancelIfIdle() {
      if (!this.targetedThisTick && this.targetBlock != null) {
         this.cancelBreaking();
      }
   }

   public BlockPos getTargetBlock() {
      return this.targetBlock;
   }

   public void setBreakRequested(boolean flag) {
      this.breakRequested = flag;
   }

   private static int findHotbarSlot(PlayerInventory playerInventory, Predicate<ItemStack> predicate) {
      for (int i = 0; i < 9; i++) {
         if (predicate.test(playerInventory.getStack(i))) {
            return i;
         }
      }

      return -1;
   }

   private boolean trySwapFromInventory(PlayerInventory playerInventory, Predicate<ItemStack> predicate) {
      if (!this.swapInProgress && !ExtUtils.isAction()) {
         ClientPlayerEntity player = this.client.player;
         if (player.currentScreenHandler != player.playerScreenHandler) {
            return false;
         } else {
            for (int i = 9; i <= 35; i++) {
               if (predicate.test(playerInventory.getStack(i))) {
                  if (!InventoryStackUtilsHandle.canFlagForObject(this.inventoryAccess)) {
                     return true;
                  }

                  int screenValue = i;
                  int stackPickaxes = findFreeHotbarSlot(playerInventory);
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
                  this.swapInProgress = true;
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean hasEnchantment(ItemStack itemStack, RegistryKey<Enchantment> registryKey) {
      if (itemStack != null && !itemStack.isEmpty()) {
         ItemEnchantmentsComponent orDefault = itemStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

         for (RegistryEntry registryEntry : orDefault.getEnchantments()) {
            if (registryEntry.matchesKey(registryKey)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public AnchorBreaker.BreakStatus tickBreakTarget(BlockPos next, boolean hunterBodyFlag) {
      ClientPlayerEntity player = this.client.player;
      if (player != null && this.client.world != null && next != null) {
         BlockState blockState = this.client.world.getBlockState(next);
         if (blockState.isAir() || !blockState.getFluidState().isEmpty() && blockState.getCollisionShape(this.client.world, next).isEmpty()) {
            if (next.equals(this.targetBlock)) {
               this.targetBlock = null;
            }

            return AnchorBreaker.BreakStatus.GONE;
         } else {
            if (!next.equals(this.targetBlock)) {
               if (this.targetBlock != null && this.client.interactionManager != null) {
                  this.client.interactionManager.cancelBlockBreaking();
               }

               this.targetBlock = next;
            }

            this.targetedThisTick = true;
            Vec3d eyePos = this.hunterBody.getVisibleAimPoint(next, 4.5);
            if (eyePos == null) {
               return AnchorBreaker.BreakStatus.NO_SIGHT;
            } else {
               this.hunterBody.aimAt(eyePos, 1);
               int value = this.selectToolSlot(blockState, hunterBodyFlag);
               if (value == -2) {
                  return AnchorBreaker.BreakStatus.WAIT_TOOL;
               } else if (value < 0) {
                  return AnchorBreaker.BreakStatus.NO_TOOL;
               } else {
                  this.toolSlotIndex = value;
                  if (!this.hunterBody.isSlotSelectedAndSettled(value)) {
                     this.hunterBody.selectHotbarSlot(value);
                     return AnchorBreaker.BreakStatus.WAIT_TOOL;
                  } else {
                     return AnchorBreaker.BreakStatus.WORKING;
                  }
               }
            }
         }
      } else {
         return AnchorBreaker.BreakStatus.NO_SIGHT;
      }
   }

   public void endTick() {
      this.targetedThisTick = false;
      this.breakRequested = false;
      if (this.swapInProgress && !ExtUtils.isAction()) {
         this.swapInProgress = false;
      }
   }

   int selectToolSlot(BlockState state, boolean damageableDamageFlag) {
      ClientPlayerEntity player = this.client.player;
      PlayerInventory inventory = player.getInventory();
      boolean of = state.isOf(Blocks.RESPAWN_ANCHOR);
      boolean of2 = state.isOf(Blocks.ENDER_CHEST);
      if (damageableDamageFlag) {
         int stack = findHotbarSlot(inventory, this::isSilkTouchPickaxeUsable);
         if (stack >= 0) {
            return stack;
         } else {
            return this.trySwapFromInventory(inventory, this::isSilkTouchPickaxeUsable) ? -2 : -1;
         }
      } else if (!of && !of2) {
         float[] floats = new float[9];
         boolean[] booleans = new boolean[9];

         for (int i = 0; i < 9; i++) {
            ItemStack stack2 = inventory.getStack(i);
            boolean damageableDamageFlag2 = stack2.isDamageable() && stack2.getMaxDamage() - stack2.getDamage() < 25;
            floats[i] = damageableDamageFlag2 ? 1.0F : stack2.getMiningSpeedMultiplier(state);
            booleans[i] = !damageableDamageFlag2 && stack2.isSuitableFor(state);
         }

         int damageableDamage = ToolBreakerUtils.selectBestToolIndex(floats, booleans, inventory.getSelectedSlot());
         if (damageableDamage < 0 || damageableDamage > 8) {
            damageableDamage = inventory.getSelectedSlot();
         }

         ItemStack stack3 = inventory.getStack(damageableDamage);
         if (stack3.isDamageable() && stack3.getMaxDamage() - stack3.getDamage() < 25) {
            for (int j = 0; j < 9; j++) {
               if (!inventory.getStack(j).isDamageable()) {
                  return j;
               }
            }
         }

         return damageableDamage;
      } else {
         int damageableDamage2 = -1;
         float damageableDamageValue = 0.0F;

         for (int k = 0; k < 9; k++) {
            ItemStack stack4 = inventory.getStack(k);
            if (isUsableMiningPickaxe(stack4) && stack4.isSuitableFor(state)) {
               float damageableDamageLimit = stack4.getMiningSpeedMultiplier(state) - (hasEnchantment(stack4, Enchantments.SILK_TOUCH) ? 0.01F : 0.0F);
               if (damageableDamageLimit > damageableDamageValue) {
                  damageableDamageValue = damageableDamageLimit;
                  damageableDamage2 = k;
               }
            }
         }

         if (damageableDamage2 >= 0) {
            return damageableDamage2;
         } else {
            return this.trySwapFromInventory(inventory, stx -> isUsableMiningPickaxe(stx) && stx.isSuitableFor(state)) ? -2 : -1;
         }
      }
   }

   public static enum BreakStatus {
      WORKING,
      GONE,
      NO_TOOL,
      NO_SIGHT,
      WAIT_TOOL;
   }
}
