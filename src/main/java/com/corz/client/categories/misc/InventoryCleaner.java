package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.ScreenCursor;
import com.corz.client.inventory.DequeEmpty.Verdict;
import com.corz.client.inventory.InventoryStackUtilsConst;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import com.corz.client.inventory.ItemList;
import com.corz.client.inventory.StackChestplateType;
import com.corz.client.inventory.StackEnchantmentsUtils;
import com.corz.client.utils.CleanerInventoryUtils.Entry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import com.corz.client.utils.CleanerInventoryUtils;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import com.corz.client.utils.StackEmptyUtilsAbilities;
import ai.auth.Module;
import com.corz.client.utils.EqualComponents;
import com.corz.client.utils.IconsPngType;

public class InventoryCleaner extends Module {
   private final BooleanSetting booleanBase;
   private Object world;
   private static final String TOGGLE = "Toggle";
   private final BooleanSetting removeFood;
   private final IntegerSetting integerHelper;
   private final EnumSetting enumHelper = new EnumSetting(
      "Activation", "Toggle: cleans whenever needed. On Key: press the bind to clean once, then it turns off.", "Toggle", "Toggle", "On Key"
   );
   private final BooleanSetting inventoryOnly = new BooleanSetting(
      "Inventory Only", "Only clean while your inventory is open (On Key opens it for you). Off = clean in the background while you stand still.", true
   );
   private final EqualComponents equalComponents;
   private final BooleanSetting removeNegativePotions;
   private static final String ON_KEY_LABEL = "On Key";
   private final ItemList itemList;
   private final ScreenCursor screenCursor;
   private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND
   };

   private void handle(List<Entry> list, ItemStack stack) {
      StackChestplateType armorCrossbow = StackChestplateType.getArmorCrossbow(stack);
      if (armorCrossbow != null) {
         list.add(getDataForIntItemStack(-1, stack, armorCrossbow, true, false));
      }
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         ClientPlayerEntity player = mc.player;
         if (mc.world != this.world) {
            this.world = mc.world;
            this.equalComponents.update();
         }

         boolean timeScreen = this.screenCursor.canTimeScreen(mc);
         this.equalComponents
            .handleTimeMax(kind -> StackEmptyUtilsAbilities.getEqualComponents(player, kind), this.screenCursor.getTime(), player.getX(), player.getY(), player.getZ());
         if (timeScreen) {
            boolean equals = "On Key".equals(this.enumHelper.getCurrentOption());
            boolean iIiFlag = this.screenCursor.isFlag();
            if (!equals || !iIiFlag && this.screenCursor.getSwitchTickCount() <= 200) {
               if (StackEmptyUtilsAbilities.canFlagForMinecraftClientObject(mc, this) && StackEmptyUtilsAbilities.isModifyWorld(player) && player.canDropItems()) {
                  int next = this.getNext(player);
                  if (next < 0) {
                     if (this.screenCursor.canScreenCursor(mc) && equals) {
                        this.setEnabled(false);
                     }
                  } else {
                     this.screenCursor.update();
                     Verdict stopTime = this.screenCursor.getStopTime(next, player.getInventory().getStack(next));
                     if (stopTime == Verdict.STOP) {
                        this.screenCursor.handleScreenCursor(mc);
                        if (equals) {
                           this.setEnabled(false);
                        }
                     } else if (stopTime != Verdict.SKIP) {
                        boolean isInventoryScreen = mc.currentScreen instanceof InventoryScreen;
                        if (equals || !this.screenCursor.isTime()) {
                           if (this.inventoryOnly.isEnabled() && !isInventoryScreen) {
                              if (equals) {
                                 this.screenCursor.handleScreenPlayer(mc);
                              }
                           } else if (!isInventoryScreen || this.screenCursor.isInventoryScreenTicks()) {
                              boolean cursorInventoryFlag = !isInventoryScreen && equals && !player.isSneaking() && StackEmptyUtilsAbilities.isFlag();
                              if (isInventoryScreen || cursorInventoryFlag || StackEmptyUtilsAbilities.canSprinting(player)) {
                                 this.handleCursorInventory(player, next, cursorInventoryFlag);
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               this.updateCursorScreen();
            }
         }
      } else {
         this.screenCursor.updateFlagTime();
      }
   }

   public InventoryCleaner() {
      super("Inventory Cleaner", IconsPngType.MISC);
      this.integerHelper = new IntegerSetting("Delay", "Milliseconds between throws. Higher looks more human.", 150, 100, 500, 10);
      this.booleanBase = new BooleanSetting(
         "Best Items", "Throw worse copies of your weapons, tools and armor. A copy with an enchantment your best one lacks is kept.", true
      );
      this.removeNegativePotions = new BooleanSetting("Remove Negative Potions", "Throw out potions that only have harmful effects (poison, weakness, slowness...).", true);
      this.removeFood = new BooleanSetting("Remove Food", "Throw out food except golden apples.", false);
      this.itemList = new ItemList(
         "Blacklisted",
         "Items that are always thrown out. Totems, elytra, shulkers and renamed items are always kept.",
         List.of(Items.STICK, Items.STRING, Items.FLINT, Items.FEATHER, Items.GLASS_BOTTLE, Items.ROTTEN_FLESH, Items.POISONOUS_POTATO)
      );
      this.screenCursor = new ScreenCursor(this);
      this.equalComponents = new EqualComponents();
      this.addSettings(
         new ConfigSetting[]{this.enumHelper, this.inventoryOnly, this.integerHelper, this.booleanBase, this.removeNegativePotions, this.removeFood, this.itemList}
      );
   }

   private void handleEmpty(List<Entry> list, int emptyValue, ItemStack stack, boolean emptyFlag) {
      if (!stack.isEmpty()) {
         boolean inventoryCleanerFlag = StackEmptyUtilsAbilities.canEmptyStream(stack) || InventoryStackUtilsConst.canFlag(emptyValue) || InventoryStackUtilsConst.canFlagForInt(emptyValue);
         boolean emptyFlag3 = !inventoryCleanerFlag && this.canBaseEmpty(stack);
         StackChestplateType stackChestplateType = emptyFlag ? StackChestplateType.getArmorCrossbow(stack) : null;
         if (emptyFlag3 || stackChestplateType != null) {
            list.add(getDataForIntItemStack(emptyValue, stack, stackChestplateType, inventoryCleanerFlag, emptyFlag3));
         }
      }
   }

   @Override
   protected void onEnable() {
      this.screenCursor.updateFlagTime();
      this.equalComponents.update();
      this.world = null;
   }

   private void updateCursorScreen() {
      this.screenCursor.handleScreenCursor(mc);
      this.setEnabled(false);
   }

   private static Entry getDataForIntItemStack(int inventoryCleanerValue, ItemStack stack, StackChestplateType stackChestplateType, boolean inventoryCleanerFlag, boolean inventoryCleanerFlag2) {
      double damageDamageable = StackEnchantmentsUtils.getDamageDamageable(stack);
      return stackChestplateType == null
         ? new Entry(inventoryCleanerValue, -1, 0.0, inventoryCleanerFlag, inventoryCleanerFlag2, damageDamageable)
         : new Entry(
            inventoryCleanerValue,
            stackChestplateType.ordinal(),
            StackEnchantmentsUtils.getUnbreakingMending(stack, stackChestplateType),
            inventoryCleanerFlag,
            inventoryCleanerFlag2,
            damageDamageable,
            StackEnchantmentsUtils.getMultiplierDamage(stack, stackChestplateType),
            StackEnchantmentsUtils.getEnchantmentEnchantments(stack, false),
            StackEnchantmentsUtils.getEnchantmentEnchantments(stack, true)
         );
   }

   private int getNext(ClientPlayerEntity player) {
      PlayerInventory inventory = player.getInventory();
      boolean booleanValue = this.booleanBase.isEnabled();
      var arrayList = new ArrayList(41);
      if (booleanValue) {
         for (EquipmentSlot equipmentSlot : equipmentSlots) {
            this.handle(arrayList, player.getEquippedStack(equipmentSlot));
         }
      }

      for (int i = 0; i < 36; i++) {
         this.handleEmpty(arrayList, i, inventory.getStack(i), booleanValue);
      }

      for (int next : (Iterable<Integer>) (CleanerInventoryUtils.getJunkKind(arrayList, booleanValue))) {
         ItemStack stack = inventory.getStack(next);
         if (!this.screenCursor.canFlag(next, stack) && !this.equalComponents.canEqualComponents(stack)) {
            return next;
         }
      }

      return -1;
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke && mc.isOnThread() && PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
         this.screenCursor.updateTime();
      }
   }

   public boolean canBaseEmpty(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && !StackEmptyUtilsAbilities.canEmptyStream(stack)) {
         if (this.itemList.isContains(stack.getItem())) {
            return true;
         } else {
            return this.removeNegativePotions.isEnabled() && StackEmptyUtilsAbilities.canPotionEffects(stack)
               ? true
               : this.removeFood.isEnabled() && StackEmptyUtilsAbilities.canAppleGolden(stack);
         }
      } else {
         return false;
      }
   }

   @Override
   protected void onDisable() {
      if (mc.player != null) {
         this.screenCursor.handleScreenCursor(mc);
      }

      this.screenCursor.updateFlagTime();
      this.equalComponents.update();
      this.world = null;
   }

   private void handleCursorInventory(ClientPlayerEntity player, int cursorInventoryValue, boolean cursorInventoryFlag) {
      ItemStack copy = player.getInventory().getStack(cursorInventoryValue).copy();
      this.screenCursor
         .handleFlag(
            () -> {
               if (this.isEnabled() && StackEmptyUtilsAbilities.canAbilitiesInteraction(mc, this)) {
                  ClientPlayerEntity player2 = mc.player;
                  if (player2.canDropItems() && StackEmptyUtilsAbilities.isModifyWorld(player2)) {
                     if (!InventoryStackUtilsConst.canFlagForInt(cursorInventoryValue) && !InventoryStackUtilsConst.canFlag(cursorInventoryValue)) {
                        ItemStack var4x = player2.getInventory().getStack(cursorInventoryValue);
                        if (StackEmptyUtilsAbilities.canEmptyEqual(var4x, copy) && !StackEmptyUtilsAbilities.canEmptyStream(var4x)) {
                           if (InventoryStackUtilsHandle.canFlagForObject(this)) {
                              try {
                                 int syncId = player2.playerScreenHandler.syncId;
                                 mc.interactionManager.clickSlot(syncId, StackEmptyUtilsAbilities.getValue(cursorInventoryValue), 1, SlotActionType.THROW, player2);
                                 this.screenCursor.updateFlag();
                                 this.screenCursor.handleTime(cursorInventoryValue, copy);
                                 this.equalComponents
                                    .handleEmpty(
                                       copy,
                                       StackEmptyUtilsAbilities.getEqualComponents(player2, copy),
                                       this.screenCursor.getTime(),
                                       player2.getX(),
                                       player2.getY(),
                                       player2.getZ()
                                    );
                                 StackEmptyUtilsAbilities.handleNetworkHandler(mc, syncId);
                              } finally {
                                 InventoryStackUtilsHandle.handle(this);
                              }
                           }
                        }
                     }
                  }
               }
            },
            cursorInventoryFlag,
            this.integerHelper.getValue()
         );
   }
}
