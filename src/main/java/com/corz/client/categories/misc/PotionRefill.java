package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.RangeSetting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import ai.auth.Module;
import com.corz.client.utils.RefillPotion;
import com.corz.client.utils.EffectStackUtils;
import com.corz.client.utils.IconsPngType;

public class PotionRefill extends Module {
   public final RangeSetting delay = new RangeSetting("Delay", "Random milliseconds between each potion move", 30.0, 50.0, 0.0, 300.0, 1.0);
   private double healthPotAmount;
   private final RefillPotion iIiRefillPotion;
   public final BooleanSetting booleanBase = new BooleanSetting("Health Pot Only", "Only refill Instant Health potions", false);

   public PotionRefill() {
      super("Potion Refill", IconsPngType.MISC);
      this.iIiRefillPotion = new RefillPotion();
      this.healthPotAmount = 30.0;
      this.addSettings(new ConfigSetting[]{this.delay, this.booleanBase});
   }

   private void updateMinmaxPot() {
      this.healthPotAmount = this.delay.getMin() + ThreadLocalRandom.current().nextDouble() * (this.delay.getMax() - this.delay.getMin());
   }

   @Override
   protected void onReset() {
      if (mc.player != null) {
         if (mc.currentScreen instanceof InventoryScreen inventoryScreen) {
            if (this.getInventoryStack() != -1 && this.iIiRefillPotion.canFlag(this.healthPotAmount)) {
               int splashPotion = EffectStackUtils.getSplashPotion(9, 36, this.booleanBase.isEnabled() ? StatusEffects.INSTANT_HEALTH : null);
               if (splashPotion != -1) {
                  mc.interactionManager.clickSlot(inventoryScreen.getScreenHandler().syncId, splashPotion, 0, SlotActionType.QUICK_MOVE, mc.player);
                  this.updateMinmaxPot();
                  this.iIiRefillPotion.updateTime();
               }
            }
         } else {
            this.updateMinmaxPot();
            this.iIiRefillPotion.updateTime();
         }
      }
   }

   private int getInventoryStack() {
      for (int i = 0; i < 9; i++) {
         if (mc.player.getInventory().getStack(i).getItem() == Items.AIR) {
            return i;
         }
      }

      return -1;
   }
}
