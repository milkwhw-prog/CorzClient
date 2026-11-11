package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.inventory.InventoryStackUtilsConst;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.SlotActionType;
import com.corz.client.utils.StackEmptyUtilsAbilities;
import com.corz.client.utils.BlockBreakMath;

public final class HotbarKitRestocker {
   public static final String KIT = "kit";
   private HotbarKitRestocker.Kind pendingKind;
   private final long[] kindBlockedUntil = new long[HotbarKitRestocker.Kind.values().length];
   private long actionGeneration;
   private long lastSwapTime;
   private final Object lockOwner;
   private Runnable pendingAction;
   private boolean anchorsEnabled = true;

   public static double scoreMiningTicks(float stackInventoryValue, int enchantmentsDefault, boolean stackInventoryFlag) {
      int obsidianBreakTicks = BlockBreakMath.getObsidianBreakTicks(stackInventoryValue, enchantmentsDefault, stackInventoryFlag);
      return obsidianBreakTicks == Integer.MAX_VALUE ? -1.0E9 : -obsidianBreakTicks;
   }

   private static double getSwordScore(ItemStack stack) {
      double damageSwordValue = stack.isDamageable() && stack.getMaxDamage() > 0 ? 1.0 - (double)stack.getDamage() / stack.getMaxDamage() : 1.0;
      int enchantmentsDefault = InventoryStackUtilsEmpty.getEnchantmentsDefault(stack, Enchantments.KNOCKBACK);
      int enchantmentsDefault2 = InventoryStackUtilsEmpty.getEnchantmentsDefault(stack, Enchantments.SHARPNESS);
      double damageSwordValue2 = stack.isOf(Items.NETHERITE_SWORD) ? 3.0 : (stack.isOf(Items.DIAMOND_SWORD) ? 2.0 : (stack.isOf(Items.IRON_SWORD) ? 1.0 : 0.0));
      return damageSwordValue2 * 4.0 + enchantmentsDefault * 3.0 + enchantmentsDefault2 + damageSwordValue;
   }

   static int findHotbarSlotToRefill(HotbarKitRestocker.Slot[] slots, int blockedKindValue, HotbarKitRestocker.Kind kind) {
      int index = -1;
      int blockedKindValue2 = -1;

      for (int i = 0; i < 9; i++) {
         HotbarKitRestocker.Slot slot = slots[i];
         if (slot != null && slot.kind() == kind && slot.count() > blockedKindValue2) {
            blockedKindValue2 = slot.count();
            index = i;
         }
      }

      if (index < 0) {
         return -1;
      } else {
         HotbarKitRestocker.Slot slot2 = slots[index];
         int max3 = Math.max(1, slot2.maxCount() / 4);
         if (slot2.count() >= max3) {
            return -1;
         } else {
            return index != blockedKindValue && !slot2.blocked() ? index : -1;
         }
      }
   }

   Runnable registerKindAction(Runnable runnable, HotbarKitRestocker.Kind gappleGlowstoneType) {
      long kitGappleValue = ++this.actionGeneration;
      Runnable action = () -> {
         if (kitGappleValue == this.actionGeneration) {
            this.pendingAction = null;
            this.pendingKind = null;
            runnable.run();
         }
      };
      this.pendingAction = action;
      this.pendingKind = gappleGlowstoneType;
      if (!ExtUtils.canActionText(action, true, false, "kit")) {
         this.pendingAction = null;
         this.pendingKind = null;
         return null;
      } else {
         return action;
      }
   }

   Runnable registerAction(Runnable runnable) {
      return this.registerKindAction(runnable, null);
   }

   static int findHotbarTargetSlot(HotbarKitRestocker.Slot[] slots, int clsBlockedValue, boolean clsBlockedFlag, boolean clsBlockedFlag2, int clsBlockedLimit) {
      int clsBlocked = -1;
      int maxValue = Integer.MAX_VALUE;

      for (int i = 0; i < 9; i++) {
         HotbarKitRestocker.Slot slot = slots[i] == null ? HotbarKitRestocker.Slot.INSTANCE : slots[i];
         if (i != clsBlockedValue && !slot.blocked()) {
            int replacePriority = getReplacePriority(slot.cls(), clsBlockedFlag, clsBlockedFlag2);
            if (replacePriority >= 0 && replacePriority <= clsBlockedLimit && replacePriority < maxValue) {
               maxValue = replacePriority;
               clsBlocked = i;
            }
         }
      }

      return clsBlocked;
   }

   static HotbarKitRestocker.Slot classifyStack(ItemStack stack, boolean gappleFoodFlag) {
      if (stack == null || stack.isEmpty()) {
         return new HotbarKitRestocker.Slot(HotbarKitRestocker.SlotClass.EMPTY, null, 0, 64, 0.0, gappleFoodFlag);
      } else if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
         return new HotbarKitRestocker.Slot(HotbarKitRestocker.SlotClass.TOTEM, null, stack.getCount(), stack.getMaxCount(), 0.0, gappleFoodFlag);
      } else {
         HotbarKitRestocker.Kind kind = null;

         for (HotbarKitRestocker.Kind kind2 : HotbarKitRestocker.Kind.values()) {
            if (kind2.predicate.test(stack)) {
               kind = kind2;
               break;
            }
         }

         HotbarKitRestocker.SlotClass cls;
         if (kind != null && kind.coreItem) {
            cls = HotbarKitRestocker.SlotClass.CORE;
         } else if (kind == HotbarKitRestocker.Kind.GAPPLE) {
            cls = HotbarKitRestocker.SlotClass.GAPPLE;
         } else if (kind == HotbarKitRestocker.Kind.XP) {
            cls = HotbarKitRestocker.SlotClass.XP;
         } else if (InventoryStackUtilsEmpty.canAxesEmpty(stack) || stack.isOf(Items.SHIELD)) {
            cls = HotbarKitRestocker.SlotClass.WEAPON;
         } else if (StackEmptyUtilsAbilities.canEmptyStream(stack)) {
            cls = HotbarKitRestocker.SlotClass.PROTECTED;
         } else if (stack.contains(DataComponentTypes.FOOD)) {
            cls = HotbarKitRestocker.SlotClass.FOOD;
         } else {
            cls = HotbarKitRestocker.SlotClass.OTHER;
         }

         double gappleFoodValue = kind == HotbarKitRestocker.Kind.SWORD
            ? getSwordScore(stack)
            : (kind == HotbarKitRestocker.Kind.PICKAXE ? getPickaxeScore(stack) : stack.getCount());
         return new HotbarKitRestocker.Slot(cls, kind, stack.getCount(), stack.getMaxCount(), gappleFoodValue, gappleFoodFlag);
      }
   }

   public boolean isKindBlocked(HotbarKitRestocker.Kind kind, long stackInventoryValue) {
      return kind != null && this.kindBlockedUntil[kind.ordinal()] > stackInventoryValue;
   }

   public static HotbarKitRestocker.Move findSwapMove(
      HotbarKitRestocker.Slot[] slots, int gapplePearlValue, boolean gapplePearlFlag, boolean clsBlockedFlag, boolean gapplePearlFlag3, HotbarKitRestocker.Kind kind
   ) {
      if (slots != null && slots.length >= 36) {
         for (HotbarKitRestocker.Kind kind2 : HotbarKitRestocker.Kind.values()) {
            if ((kind == null || kind2 == kind)
               && (!kind2.anchorRelated || !gapplePearlFlag)
               && (kind != null || kind2 != HotbarKitRestocker.Kind.XP || clsBlockedFlag)
               && !hotbarContainsKind(slots, kind2)) {
               int scoreBlocked = findBestInventorySlotOfKind(slots, kind2);
               if (scoreBlocked >= 0) {
                  int clsBlockedLimit = kind2 == HotbarKitRestocker.Kind.GAPPLE && !gapplePearlFlag3 && kind == null ? 2 : Integer.MAX_VALUE;
                  int clsBlocked = findHotbarTargetSlot(slots, gapplePearlValue, clsBlockedFlag, gapplePearlFlag3, clsBlockedLimit);
                  if (clsBlocked >= 0) {
                     return new HotbarKitRestocker.Move(kind2, scoreBlocked, clsBlocked);
                  }
               }
            }
         }

         if (kind != null) {
            return null;
         } else {
            for (HotbarKitRestocker.Kind kind3 : new HotbarKitRestocker.Kind[]{
               HotbarKitRestocker.Kind.CRYSTAL, HotbarKitRestocker.Kind.OBSIDIAN, HotbarKitRestocker.Kind.PEARL
            }) {
               int blockedKind = findHotbarSlotToRefill(slots, gapplePearlValue, kind3);
               if (blockedKind >= 0) {
                  int bestInventorySlotOfKind = findBestInventorySlotOfKind(slots, kind3);
                  if (bestInventorySlotOfKind >= 0 && slots[bestInventorySlotOfKind].count() > slots[blockedKind].count()) {
                     return new HotbarKitRestocker.Move(kind3, bestInventorySlotOfKind, blockedKind);
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public boolean tryScheduleSwapForKind(HotbarKitRestocker.Kind kind) {
      if (kind == null) {
         return false;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player != null && instance.world != null && instance.interactionManager != null) {
            HotbarKitRestocker.Slot[] inventoryStack = snapshotInventory(player);
            if (hotbarContainsKind(inventoryStack, kind)) {
               return true;
            } else if (this.pendingAction != null && this.pendingKind == kind) {
               return true;
            } else if (!StackEmptyUtilsAbilities.canFlag(instance, this.lockOwner)) {
               return false;
            } else {
               long gappleBusyValue = ClientThreadUtils.getTime();
               boolean gappleBusyFlag = !BlockPosUtilsRespawn.isRegistryWorld();
               HotbarKitRestocker.Move gapplePearl = findSwapMove(
                  inventoryStack,
                  player.getInventory().getSelectedSlot(),
                  gappleBusyFlag || !this.anchorsEnabled,
                  this.isKindBlocked(HotbarKitRestocker.Kind.XP, gappleBusyValue) || kind == HotbarKitRestocker.Kind.XP,
                  this.isKindBlocked(HotbarKitRestocker.Kind.GAPPLE, gappleBusyValue) || kind == HotbarKitRestocker.Kind.GAPPLE,
                  kind
               );
               return gapplePearl == null ? false : this.tryScheduleSwap(player, gapplePearl);
            }
         } else {
            return false;
         }
      }
   }

   public static int getReplacePriority(HotbarKitRestocker.SlotClass slotClass, boolean stackInventoryFlag, boolean stackInventoryFlag2) {
      if (slotClass == null) {
         return -1;
      } else {
         return switch (slotClass) {
            case EMPTY -> 0;
            case TOTEM, WEAPON, PROTECTED, CORE -> -1;
            case GAPPLE -> stackInventoryFlag2 ? -1 : 3;
            case XP -> stackInventoryFlag ? -1 : 1;
            case FOOD -> 3;
            case OTHER -> 2;
         };
      }
   }

   private boolean tryScheduleSwap(ClientPlayerEntity player, HotbarKitRestocker.Move move) {
      int source = move.source();
      int target = move.target();
      ItemStack copy = player.getInventory().getStack(source).copy();
      ItemStack itemStack = player.getInventory().getStack(target).copy();
      Runnable kitGapple = this.registerKindAction(() -> {
         MinecraftClient var5x = MinecraftClient.getInstance();
         ClientPlayerEntity var6x = var5x.player;
         if (var6x != null && var5x.interactionManager != null) {
            if (StackEmptyUtilsAbilities.canFlag(var5x, this.lockOwner)) {
               if (!InventoryStackUtilsConst.canFlagForInt(source) && !InventoryStackUtilsConst.canFlagForInt(target) && !InventoryStackUtilsConst.canFlag(target)) {
                  PlayerInventory var7x = var6x.getInventory();
                  if (target != var7x.getSelectedSlot()) {
                     ItemStack stack = var7x.getStack(source);
                     ItemStack stack2 = var7x.getStack(target);
                     if (StackEmptyUtilsAbilities.canEmptyEqual(stack, copy) && StackEmptyUtilsAbilities.canEmptyEqual(stack2, itemStack)) {
                        if (!stack.isOf(Items.TOTEM_OF_UNDYING) && !stack2.isOf(Items.TOTEM_OF_UNDYING)) {
                           if (InventoryStackUtilsHandle.canFlagForObject(this.lockOwner)) {
                              try {
                                 int syncId = var6x.playerScreenHandler.syncId;
                                 var5x.interactionManager.clickSlot(syncId, StackEmptyUtilsAbilities.getValue(source), target, SlotActionType.SWAP, var6x);
                                 StackEmptyUtilsAbilities.handleNetworkHandler(var5x, syncId);
                              } finally {
                                 InventoryStackUtilsHandle.handle(this.lockOwner);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }, move.kind());
      if (kitGapple == null) {
         return false;
      } else {
         this.lastSwapTime = ClientThreadUtils.getTime();
         return true;
      }
   }

   public void setKindBlockedUntil(HotbarKitRestocker.Kind kind, long stackInventoryLimit) {
      if (kind != null) {
         int ordinal = kind.ordinal();
         if (stackInventoryLimit > this.kindBlockedUntil[ordinal]) {
            this.kindBlockedUntil[ordinal] = stackInventoryLimit;
         }
      }
   }

   public CaseMaxData getKitStatus(SelfPopData selfPopData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player == null) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.KIT, "no player");
      } else if (!StackEmptyUtilsAbilities.canFlag(instance, this.lockOwner)) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.KIT, "busy");
      } else {
         if (selfPopData != null && selfPopData.cfg() != null) {
            this.anchorsEnabled = selfPopData.cfg().anchors();
         }

         boolean busyKitFlag = selfPopData != null ? selfPopData.nether() : !BlockPosUtilsRespawn.isRegistryWorld();
         long busyKitValue = selfPopData != null ? selfPopData.tick() : ClientThreadUtils.getTime();
         HotbarKitRestocker.Move gapplePearl = findSwapMove(
            snapshotInventory(player),
            player.getInventory().getSelectedSlot(),
            busyKitFlag || !this.anchorsEnabled,
            this.isKindBlocked(HotbarKitRestocker.Kind.XP, busyKitValue),
            this.isKindBlocked(HotbarKitRestocker.Kind.GAPPLE, busyKitValue),
            null
         );
         return gapplePearl == null
            ? CaseMaxData.getCaseMaxData(SurroundKitType.KIT, "full")
            : new CaseMaxData(SurroundKitType.KIT, true, 1.0, 1, false, false, gapplePearl.kind().name().toLowerCase(Locale.ROOT));
      }
   }

   public static HotbarKitRestocker.Move getNextSwapMove(ClientPlayerEntity player, boolean selectedSlotFlag) {
      return player == null ? null : findSwapMove(snapshotInventory(player), player.getInventory().getSelectedSlot(), selectedSlotFlag, false, false, null);
   }

   static boolean hotbarContainsKind(HotbarKitRestocker.Slot[] slots, HotbarKitRestocker.Kind kind) {
      for (int i = 0; i < 9; i++) {
         HotbarKitRestocker.Slot slot = slots[i];
         if (slot != null && slot.kind() == kind) {
            return true;
         }
      }

      return false;
   }

   static HotbarKitRestocker.Slot[] snapshotInventory(ClientPlayerEntity player) {
      PlayerInventory inventory = player.getInventory();
      HotbarKitRestocker.Slot[] slots = new HotbarKitRestocker.Slot[36];

      for (int i = 0; i < 36; i++) {
         boolean inventoryStackFlag = InventoryStackUtilsConst.canFlagForInt(i) || i < 9 && InventoryStackUtilsConst.canFlag(i);
         slots[i] = classifyStack(inventory.getStack(i), inventoryStackFlag);
      }

      return slots;
   }

   public void cancelPendingAction() {
      this.actionGeneration++;
      Runnable action = this.pendingAction;
      this.pendingAction = null;
      this.pendingKind = null;
      if (action != null) {
         ExtUtils.canActionFlag(action);
      }
   }

   public long getLastSwapTime() {
      return this.lastSwapTime;
   }

   public boolean tryScheduleAutoSwap(boolean busyGappleFlag) {
      if (!busyGappleFlag) {
         return false;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player == null || instance.world == null || instance.interactionManager == null) {
            return false;
         } else if (!StackEmptyUtilsAbilities.canFlag(instance, this.lockOwner)) {
            return false;
         } else {
            boolean registryWorld2 = !BlockPosUtilsRespawn.isRegistryWorld();
            long busyGappleValue = ClientThreadUtils.getTime();
            HotbarKitRestocker.Move gapplePearl = findSwapMove(
               snapshotInventory(player),
               player.getInventory().getSelectedSlot(),
               registryWorld2 || !this.anchorsEnabled,
               this.isKindBlocked(HotbarKitRestocker.Kind.XP, busyGappleValue),
               this.isKindBlocked(HotbarKitRestocker.Kind.GAPPLE, busyGappleValue),
               null
            );
            return gapplePearl == null ? false : this.tryScheduleSwap(player, gapplePearl);
         }
      }
   }

   public HotbarKitRestocker(Object busyPlayerObject) {
      this.lastSwapTime = Long.MIN_VALUE;
      this.lockOwner = busyPlayerObject;
      Arrays.fill(this.kindBlockedUntil, Long.MIN_VALUE);
   }

   public static int findBestPickaxeSlot(HotbarKitRestocker.Slot[] slots) {
      int scorePickaxe = -1;
      double scorePickaxeValue = Double.NEGATIVE_INFINITY;

      for (int i = 0; i < slots.length && i <= 35; i++) {
         HotbarKitRestocker.Slot slot = slots[i];
         if (slot != null && slot.kind() == HotbarKitRestocker.Kind.PICKAXE && (slot.score() > scorePickaxeValue || slot.score() == scorePickaxeValue && i < 9 && scorePickaxe >= 9)) {
            scorePickaxeValue = slot.score();
            scorePickaxe = i;
         }
      }

      return scorePickaxe;
   }

   private static double getPickaxeScore(ItemStack stack) {
      BlockState defaultState = Blocks.OBSIDIAN.getDefaultState();
      int enchantmentsDefault = InventoryStackUtilsEmpty.getEnchantmentsDefault(stack, Enchantments.EFFICIENCY);
      return scoreMiningTicks(stack.getMiningSpeedMultiplier(defaultState), enchantmentsDefault, stack.isSuitableFor(defaultState));
   }

   static int findBestInventorySlotOfKind(HotbarKitRestocker.Slot[] slots, HotbarKitRestocker.Kind kind) {
      int scoreBlocked = -1;
      double scoreBlockedValue = Double.NEGATIVE_INFINITY;

      for (int i = 9; i <= 35 && i < slots.length; i++) {
         HotbarKitRestocker.Slot slot = slots[i];
         if (slot != null && slot.kind() == kind && !slot.blocked() && slot.score() > scoreBlockedValue) {
            scoreBlockedValue = slot.score();
            scoreBlocked = i;
         }
      }

      return scoreBlocked;
   }

   public static enum SlotClass {
      EMPTY,
      TOTEM,
      WEAPON,
      PROTECTED,
      CORE,
      GAPPLE,
      XP,
      FOOD,
      OTHER;
   }

   public static enum Kind {
      CRYSTAL(stack -> stack.isOf(Items.END_CRYSTAL), false, true),
      OBSIDIAN(stack -> stack.isOf(Items.OBSIDIAN), false, true),
      SWORD(stack -> InventoryStackUtilsEmpty.canSwordsEmpty(stack), false, true),
      PEARL(stack -> stack.isOf(Items.ENDER_PEARL), false, true),
      ANCHOR(stack -> stack.isOf(Items.RESPAWN_ANCHOR), true, true),
      GLOWSTONE(stack -> stack.isOf(Items.GLOWSTONE), true, true),
      PICKAXE(stack -> stack.isIn(ItemTags.PICKAXES), false, true),
      GAPPLE(stack -> stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE), false, false),
      XP(stack -> stack.isOf(Items.EXPERIENCE_BOTTLE), false, false);
      public final Predicate<ItemStack> predicate;
      public final boolean anchorRelated;
      public final boolean coreItem;

      private Kind(Predicate<ItemStack> predicate, boolean flag2, boolean flag3) {
         this.predicate = predicate;
         this.anchorRelated = flag2;
         this.coreItem = flag3;
      }
   }

   public record Move(HotbarKitRestocker.Kind kind, int source, int target) {
   }

   public record Slot(HotbarKitRestocker.SlotClass cls, HotbarKitRestocker.Kind kind, int count, int maxCount, double score, boolean blocked) {
      public static final HotbarKitRestocker.Slot INSTANCE = new HotbarKitRestocker.Slot(HotbarKitRestocker.SlotClass.EMPTY, null, 0, 64, 0.0, false);
   }
}
