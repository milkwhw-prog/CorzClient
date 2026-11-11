package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.inventory.ItemList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class AutoSell extends Module {
   private final EnumSetting modeSetting = new EnumSetting("Mode", "Whether to whitelist or blacklist the selected items.", "Whitelist", "Whitelist", "Blacklist");
   private final ItemList itemList = new ItemList("Items", "Items to sell.", List.of(Items.SEA_PICKLE));
   private int sell;
   private boolean nudgeDirectionFlag;
   private int soldStackCount;
   private final IntegerSetting delaySetting = new IntegerSetting("Delay", "Delay in ticks between menu actions.", 1, 0, 20);
   private final BooleanSetting antiAfkEnabled = new BooleanSetting(
      "Anti-AFK", "Nudge your view now and then so the server doesn't kick you while idling in the Sell menu.", true
   );
   private final BooleanSetting notifications;
   private long lastNudgeTimeMs;
   private final IntegerSetting antiAfkInterval = new IntegerSetting("Anti-AFK Interval", "Seconds of inactivity before an anti-AFK view nudge.", 25, 5, 120);

   @Override
   public void onEnable() {
      this.sell = 20;
      this.soldStackCount = 0;
      this.lastNudgeTimeMs = System.currentTimeMillis();
   }

   private int findSellButtonSlot(ScreenHandler screenHandler, int sellStainedLimit) {
      int sellStained = -1;

      for (int i = 0; i < sellStainedLimit; i++) {
         ItemStack stack = screenHandler.getSlot(i).getStack();
         if (!stack.isEmpty()) {
            String lowerCase = stack.getName().getString().toLowerCase(Locale.ROOT);
            if (lowerCase.contains("sell")) {
               return i;
            }

            Item item = stack.getItem();
            if (item == Items.LIME_STAINED_GLASS_PANE || item == Items.GREEN_STAINED_GLASS_PANE) {
               sellStained = i;
            }
         }
      }

      return sellStained;
   }

   private boolean isSellMenuOpen(ScreenHandler screenHandler) {
      return mc.currentScreen != null && mc.currentScreen.getTitle().getString().toLowerCase(Locale.ROOT).contains("sell")
         ? true
         : this.findSellButtonSlot(screenHandler, Math.max(0, screenHandler.slots.size() - 36)) >= 0;
   }

   private void applyAntiAfkNudge() {
      if (this.antiAfkEnabled.isEnabled() && mc.player != null) {
         long timeMillis = System.currentTimeMillis();
         if (timeMillis - this.lastNudgeTimeMs >= this.antiAfkInterval.getValue() * 1000L) {
            float yawPlayerValue = this.nudgeDirectionFlag ? 8.0F : -8.0F;
            this.nudgeDirectionFlag = !this.nudgeDirectionFlag;
            mc.player.setYaw(mc.player.getYaw() + yawPlayerValue);
            this.lastNudgeTimeMs = timeMillis;
         }
      }
   }

   private void sellMatchingItems(ScreenHandler screenHandler) {
      int slotsSize = screenHandler.slots.size();
      int sellSlotValue = Math.max(0, slotsSize - 36);
      int sellStained = this.findSellButtonSlot(screenHandler, sellSlotValue);
      if (this.hasEmptyMenuSlot(screenHandler, sellSlotValue, sellStained)) {
         for (int i = sellSlotValue; i < slotsSize; i++) {
            ItemStack stack = screenHandler.getSlot(i).getStack();
            if (!stack.isEmpty() && this.isItemAllowed(stack.getItem())) {
               mc.interactionManager.clickSlot(screenHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
               this.soldStackCount++;
               this.sell = this.delaySetting.getValue();
               this.resetNudgeTimer();
               return;
            }
         }
      }

      if (sellStained >= 0) {
         if (this.soldStackCount > 0) {
            mc.interactionManager.clickSlot(screenHandler.syncId, sellStained, 0, SlotActionType.PICKUP, mc.player);
            if (this.notifications.isEnabled()) {
               mc.player.sendMessage(Text.literal("\u00a7b[AutoSell]\u00a7r sold " + this.soldStackCount + " stack(s)."), false);
            }

            this.soldStackCount = 0;
            this.sell = this.delaySetting.getValue();
            this.resetNudgeTimer();
         }
      } else {
         if (!this.hasEmptyMenuSlot(screenHandler, sellSlotValue, -1)) {
            mc.player.closeHandledScreen();
            this.sell = 20;
         }
      }
   }

   @Override
   public void onDisable() {
      this.soldStackCount = 0;
   }

   public AutoSell() {
      super("AutoSell", IconsPngType.DONUTSMP);
      this.notifications = new BooleanSetting("Notifications", "Chat feedback when a batch is sold.", true);
      this.addSettings(new ConfigSetting[]{this.modeSetting, this.itemList, this.delaySetting, this.antiAfkEnabled, this.antiAfkInterval, this.notifications});
   }

   private void resetNudgeTimer() {
      this.lastNudgeTimeMs = System.currentTimeMillis();
   }

   private boolean hasEmptyMenuSlot(ScreenHandler screenHandler, int slotStackLimit, int slotStackValue) {
      for (int i = 0; i < slotStackLimit; i++) {
         if (i != slotStackValue && screenHandler.getSlot(i).getStack().isEmpty()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.interactionManager != null) {
         if (mc.currentScreen == null || mc.currentScreen instanceof HandledScreen) {
            this.applyAntiAfkNudge();
            if (this.sell > 0) {
               this.sell--;
            } else {
               ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
               boolean iIiFlag = currentScreenHandler instanceof GenericContainerScreenHandler && this.isSellMenuOpen(currentScreenHandler);
               if (iIiFlag) {
                  this.sellMatchingItems(currentScreenHandler);
               } else if (!(currentScreenHandler instanceof GenericContainerScreenHandler)) {
                  mc.player.networkHandler.sendChatCommand("sell");
                  this.sell = 20;
               }
            }
         }
      }
   }

   private boolean isItemAllowed(Item up) {
      boolean equals = this.modeSetting.getCurrentOption().equals("Whitelist");
      return equals ? this.itemList.isContains(up) : !this.itemList.isContains(up);
   }
}
