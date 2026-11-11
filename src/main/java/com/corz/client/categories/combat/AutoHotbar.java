package com.corz.client.categories.combat;

import com.corz.client.utils.HotbarLayoutPlanner.Move;
import com.corz.client.utils.HotbarLayoutPlanner.View;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.ScreenCursor;
import com.corz.client.inventory.DequeEmpty.Verdict;
import com.corz.client.inventory.InventoryStackUtilsConst;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import com.corz.client.inventory.ItemList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import com.corz.client.utils.HotbarLayoutPlanner;
import com.corz.client.categories.misc.InventoryCleaner;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import com.corz.client.utils.StackEmptyUtilsAbilities;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.HotbarItemCategory;
import com.corz.client.utils.IconsPngType;

public class AutoHotbar extends Module {
   private final IntegerSetting clickDelayMs;
   private final BooleanSetting openInventory;
   static final String[] DEFAULT_SLOT_LABELS = new String[]{"Sword", "Axe", "Blocks", "Golden Apple", "Ender Pearl", "Water Bucket", "Bow", "Food", "None"};
   private final EnumSetting activationMode = new EnumSetting(
      "Activation", "Toggle: keeps your hotbar sorted. On Key: press the bind to sort once, then it turns off.", "Toggle", "Toggle", "On Key"
   );
   private Object lastWorld;
   private final AutoHotbar.Layout layout;
   private final BooleanSetting moveBypass;
   private final ItemStack[] rememberedHotbarStacks;
   private final ScreenCursor screenCursor;
   private int refillCooldownTicks;
   private final ItemList refillItems;
   private static final String ON_KEY_MODE = "On Key";
   private final EnumSetting[] slotLayoutSettings;
   private static final String TOGGLE = "Toggle";

   private void resetState() {
      this.screenCursor.updateFlagTime();
      Arrays.fill(this.rememberedHotbarStacks, null);
      this.refillCooldownTicks = 0;
      this.lastWorld = null;
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         ClientPlayerEntity player = mc.player;
         if (mc.world != this.lastWorld) {
            this.lastWorld = mc.world;
            Arrays.fill(this.rememberedHotbarStacks, null);
         }

         this.rememberHotbarStacks(player);
         if (this.screenCursor.canTimeScreen(mc)) {
            boolean equals = "On Key".equals(this.activationMode.getCurrentOption());
            boolean iIiFlag = this.screenCursor.isFlag();
            if (!equals || !iIiFlag && this.screenCursor.getSwitchTickCount() <= 200) {
               if (iIiFlag) {
                  this.refillCooldownTicks = 100;
               }

               if (this.refillCooldownTicks > 0) {
                  this.refillCooldownTicks--;
               }

               if (StackEmptyUtilsAbilities.canFlagForMinecraftClientObject(mc, this)) {
                  this.layout.refreshSnapshot(player);
                  boolean layoutHotbarFlag = StackEmptyUtilsAbilities.isModifyWorld(player) && (equals || !this.screenCursor.isTime());
                  Move move = HotbarLayoutPlanner.planNextMove(this.layout, layoutHotbarFlag);
                  if (move == null) {
                     if (this.screenCursor.canScreenCursor(mc) && equals) {
                        this.setEnabled(false);
                     }
                  } else {
                     this.screenCursor.update();
                     Verdict stopTime = this.screenCursor
                        .getStopTime(HotbarLayoutPlanner.getMoveKey(move.source(), move.target()), player.getInventory().getStack(move.source()));
                     if (stopTime == Verdict.STOP) {
                        this.screenCursor.handleScreenCursor(mc);
                        if (equals) {
                           this.setEnabled(false);
                        }
                     } else if (stopTime != Verdict.SKIP) {
                        boolean isInventoryScreen = mc.currentScreen instanceof InventoryScreen;
                        if (this.openInventory.isEnabled() && !isInventoryScreen && !move.urgent()) {
                           if (equals || this.refillCooldownTicks == 0 && !this.screenCursor.isTime() && StackEmptyUtilsAbilities.canSprinting(player)) {
                              this.screenCursor.handleScreenPlayer(mc);
                           }
                        } else if (!isInventoryScreen || this.screenCursor.isInventoryScreenTicks()) {
                           boolean windCharge = isPearlOrWindCharge(player.getInventory().getStack(move.source()));
                           boolean iIiFlag3 = move.urgent() && (!this.screenCursor.isTime() || windCharge);
                           boolean inventoryStackFlag = !isInventoryScreen
                              && this.moveBypass.isEnabled()
                              && (iIiFlag3 || equals)
                              && !player.isSneaking()
                              && StackEmptyUtilsAbilities.isFlag();
                           if (isInventoryScreen || inventoryStackFlag || StackEmptyUtilsAbilities.canSprinting(player)) {
                              this.scheduleSlotSwap(player, move, inventoryStackFlag);
                           }
                        }
                     }
                  }
               }
            } else {
               this.finishSorting();
            }
         }
      } else {
         this.screenCursor.updateFlagTime();
      }
   }

   private HotbarItemCategory getSlotBucketType(int index) {
      return HotbarItemCategory.fromDisplayName(this.slotLayoutSettings[index].getCurrentOption());
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   static boolean isPearlOrWindCharge(ItemStack stack) {
      return stack.isOf(Items.ENDER_PEARL) || stack.isOf(Items.WIND_CHARGE);
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke && mc.isOnThread() && PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
         this.screenCursor.updateTime();
      }
   }

   @Override
   protected void onDisable() {
      if (mc.player != null) {
         this.screenCursor.handleScreenCursor(mc);
      }

      this.resetState();
   }

   private void scheduleSlotSwap(ClientPlayerEntity player, Move move, boolean inventoryStackFlag) {
      int source = move.source();
      int target = move.target();
      ItemStack copy = player.getInventory().getStack(source).copy();
      ItemStack itemStack = player.getInventory().getStack(target).copy();
      this.screenCursor.handleFlag(() -> {
         if (this.isEnabled() && StackEmptyUtilsAbilities.canAbilitiesInteraction(mc, this)) {
            ClientPlayerEntity var5x = mc.player;
            if (!InventoryStackUtilsConst.canFlagForInt(source) && !InventoryStackUtilsConst.canFlagForInt(target) && !InventoryStackUtilsConst.canFlag(target)) {
               PlayerInventory var6x = var5x.getInventory();
               int var7x = var6x.getSelectedSlot();
               if (source >= 9 || source != var7x && !this.screenCursor.canTimeSelected(source)) {
                  ItemStack stack = var6x.getStack(source);
                  ItemStack stack2 = var6x.getStack(target);
                  if (StackEmptyUtilsAbilities.canEmptyEqual(stack, copy) && StackEmptyUtilsAbilities.canEmptyEqual(stack2, itemStack)) {
                     if (!stack.isOf(Items.TOTEM_OF_UNDYING) && !stack2.isOf(Items.TOTEM_OF_UNDYING)) {
                        if (target != var7x || stack2.isEmpty() || this.getSlotBucketType(target).matchesStack(stack2)) {
                           if (InventoryStackUtilsHandle.canFlagForObject(this)) {
                              try {
                                 int syncId = var5x.playerScreenHandler.syncId;
                                 mc.interactionManager.clickSlot(syncId, StackEmptyUtilsAbilities.getValue(source), target, SlotActionType.SWAP, var5x);
                                 this.screenCursor.updateFlag();
                                 this.screenCursor.handleTime(HotbarLayoutPlanner.getMoveKey(source, target), copy);
                                 StackEmptyUtilsAbilities.handleNetworkHandler(mc, syncId);
                              } finally {
                                 InventoryStackUtilsHandle.handle(this);
                              }

                              if (source < 9 && var6x.getStack(source).isEmpty()) {
                                 this.rememberedHotbarStacks[source] = null;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }, inventoryStackFlag, this.clickDelayMs.getValue());
   }

   static boolean hasSameItemAndPotion(ItemStack stack, ItemStack itemStack) {
      return !stack.isEmpty() && !itemStack.isEmpty() && itemStack.isOf(stack.getItem())
         ? Objects.equals(stack.get(DataComponentTypes.POTION_CONTENTS), itemStack.get(DataComponentTypes.POTION_CONTENTS))
         : false;
   }

   public AutoHotbar() {
      super("Auto Hotbar", IconsPngType.COMBAT);
      this.openInventory = new BooleanSetting(
         "Open Inventory", "Open your inventory while sorting so you can watch it. Refills stay in the background. Off = sort in the background.", false
      );
      this.clickDelayMs = new IntegerSetting("Delay", "Milliseconds between clicks. Higher looks more human.", 150, 100, 500, 10);
      this.moveBypass = new BooleanSetting(
         "Move Bypass",
         "When a slot runs out while you move, briefly stop your movement keys so the refill can be sent. In a fight only for pearls and wind charges. Off = wait until you stand still.",
         true
      );
      this.refillItems = new ItemList(
         "Refill Items", "Items that come back into a None slot from your inventory when that slot runs out. Empty = never.", List.of(Items.ENDER_PEARL)
      );
      this.slotLayoutSettings = new EnumSetting[9];
      this.screenCursor = new ScreenCursor(this);
      this.rememberedHotbarStacks = new ItemStack[9];
      this.layout = new AutoHotbar.Layout();
      String[] texts = HotbarItemCategory.getDisplayNames();

      for (int i = 0; i < 9; i++) {
         this.slotLayoutSettings[i] = new EnumSetting(
            "Slot " + (i + 1), "What goes in hotbar slot " + (i + 1) + ". None leaves the slot alone.", DEFAULT_SLOT_LABELS[i], texts
         );
         this.slotLayoutSettings[i].withGroupName("Layout");
      }

      this.addSettings(new ConfigSetting[]{this.activationMode, this.openInventory, this.clickDelayMs, this.moveBypass, this.refillItems});
      this.addSettings(this.slotLayoutSettings);
   }

   private void rememberHotbarStacks(ClientPlayerEntity player) {
      boolean comeStacksFlag = mc.currentScreen instanceof HandledScreen && !this.screenCursor.isScreen();
      PlayerInventory inventory = player.getInventory();

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (!stack.isEmpty()) {
            if (this.rememberedHotbarStacks[i] == null || !hasSameItemAndPotion(this.rememberedHotbarStacks[i], stack)) {
               this.rememberedHotbarStacks[i] = stack.copyWithCount(1);
            }
         } else if (comeStacksFlag) {
            this.rememberedHotbarStacks[i] = null;
         }
      }
   }

   private void finishSorting() {
      this.screenCursor.handleScreenCursor(mc);
      this.setEnabled(false);
   }

   private final class Layout implements View {
      private final boolean[] movableFlags;
      private final boolean[] hotbarRestrictedFlags;
      private final byte[][] fitCache;
      private final int[] hotbarBucketOrdinals;
      private final boolean[] slotLockedFlags;
      private int selectedSlot;
      private final double[][] scoreCache;
      private final ItemStack[] itemStacks;
      private final HotbarItemCategory[] bucketTypes = HotbarItemCategory.values();

      @Override
      public boolean isHotbarSlotInUse(int cursorHotbarValue) {
         return cursorHotbarValue == this.selectedSlot || AutoHotbar.this.screenCursor.canTimeSelected(cursorHotbarValue);
      }

      @Override
      public boolean isSlotMovable(int index) {
         return this.movableFlags[index];
      }

      @Override
      public double getBucketScore(int index, int index2) {
         double amountsStacks = this.scoreCache[index][index2];
         if (Double.isNaN(amountsStacks)) {
            amountsStacks = this.bucketTypes[index].getItemScore(this.itemStacks[index2]);
            this.scoreCache[index][index2] = amountsStacks;
         }

         return amountsStacks;
      }

      @Override
      public boolean isSlotLocked(int index) {
         return this.slotLockedFlags[index];
      }

      @Override
      public boolean shouldRefillSlot(int index) {
         ItemStack itemStack = AutoHotbar.this.rememberedHotbarStacks[index];
         return this.hotbarBucketOrdinals[index] == 0 && itemStack != null && AutoHotbar.this.refillItems.isContains(itemStack.getItem());
      }

      @Override
      public boolean rememberedStackFitsSlot(int index) {
         ItemStack itemStack = AutoHotbar.this.rememberedHotbarStacks[index];
         return itemStack != null && this.bucketTypes[this.hotbarBucketOrdinals[index]].matchesStack(itemStack);
      }

      @Override
      public int getStackCount(int index) {
         return this.itemStacks[index].getCount();
      }

      @Override
      public boolean isMoveAllowed(int index, int stacksCursorValue) {
         return AutoHotbar.this.screenCursor.canFlag(HotbarLayoutPlanner.getMoveKey(index, stacksCursorValue), this.itemStacks[index]);
      }

      private Layout() {
         this.itemStacks = new ItemStack[36];
         this.hotbarBucketOrdinals = new int[9];
         this.hotbarRestrictedFlags = new boolean[9];
         this.slotLockedFlags = new boolean[36];
         this.movableFlags = new boolean[36];
         this.fitCache = new byte[this.bucketTypes.length][36];
         this.scoreCache = new double[this.bucketTypes.length][36];
      }

      @Override
      public boolean isEmpty(int index) {
         return this.itemStacks[index].isEmpty();
      }

      @Override
      public int getMaxCount(int index) {
         return this.itemStacks[index].getMaxCount();
      }

      @Override
      public boolean canMergeStacks(int index, int index2) {
         return AutoHotbar.hasSameItemAndPotion(this.itemStacks[index], this.itemStacks[index2]);
      }

      @Override
      public boolean doesStackFitBucket(int index, int index2) {
         int stacksValue = this.fitCache[index][index2];
         if (stacksValue == 0) {
            stacksValue = this.bucketTypes[index].matchesStack(this.itemStacks[index2]) ? 1 : 2;
            this.fitCache[index][index2] = (byte)stacksValue;
         }

         return stacksValue == 1;
      }

      @Override
      public int getSlotBucketOrdinal(int index) {
         return this.hotbarBucketOrdinals[index];
      }

      @Override
      public boolean rememberedStackMatches(int index, int index2) {
         ItemStack itemStack = AutoHotbar.this.rememberedHotbarStacks[index];
         return itemStack != null && AutoHotbar.hasSameItemAndPotion(itemStack, this.itemStacks[index2]);
      }

      void refreshSnapshot(ClientPlayerEntity player) {
         PlayerInventory inventory = player.getInventory();
         InventoryCleaner inventoryCleaner = ModuleRegistry.getModule(InventoryCleaner.class) instanceof InventoryCleaner inventoryCleaner2 && inventoryCleaner2.isEnabled() ? inventoryCleaner2 : null;
         this.selectedSlot = inventory.getSelectedSlot();

         for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getStack(i);
            this.itemStacks[i] = stack;
            boolean of = stack.isOf(Items.TOTEM_OF_UNDYING);
            this.slotLockedFlags[i] = stack.isEmpty() || !of && !InventoryStackUtilsConst.canFlagForInt(i);
            this.movableFlags[i] = !stack.isEmpty() && !of && (inventoryCleaner == null || !inventoryCleaner.canBaseEmpty(stack));
         }

         for (int j = 0; j < 9; j++) {
            this.hotbarBucketOrdinals[j] = AutoHotbar.this.getSlotBucketType(j).ordinal();
            this.hotbarRestrictedFlags[j] = InventoryStackUtilsConst.canFlag(j) || InventoryStackUtilsConst.canFlagForInt(j);
         }

         for (byte[] selectedBytes : this.fitCache) {
            Arrays.fill(selectedBytes, (byte)0);
         }

         for (double[] selectedValues : this.scoreCache) {
            Arrays.fill(selectedValues, Double.NaN);
         }
      }

      @Override
      public boolean isHotbarSlotRestricted(int index) {
         return this.hotbarRestrictedFlags[index];
      }
   }
}
