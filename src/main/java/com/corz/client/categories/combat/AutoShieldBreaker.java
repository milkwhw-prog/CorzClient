package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.entity.ThreadClientHandlerConst;
import com.corz.client.entity.ThreadClientHandlerConst.Result;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.inventory.PlayerInventoryApi;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.InventoryPlayerUtils;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.SwapPreferenceData;

public class AutoShieldBreaker extends Module implements PlayerInventoryApi, ThreadClientHandlerConst {
   private final BooleanSetting requireWeapon;
   private final BooleanSetting swapBack;
   private final BooleanSetting requireAttackKey = new BooleanSetting(
      "Require Attack Key", "Only help hits made while you hold the attack button \u2014 never on its own", true
   );

   @Override
   public int getHandlerPriority() {
      return 100;
   }

   @Override
   public boolean isBreachOwnerActive() {
      throw new AbstractMethodError();
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      InventoryPlayerUtils.handleTimeLiving(inventoryPlayer);
   }

   @Override
   protected void onReset() {
      InventoryPlayerUtils.updateApiDead();
   }

   @Override
   protected void onDisable() {
      InventoryPlayerUtils.handleApiSelected(this);
   }

   @Override
   public Result resolveAttackIntercept(Object obj, Entity entity) {
      return InventoryPlayerUtils.getPassPlan(obj, entity);
   }

   @Override
   public int getSwapPriority() {
      return 2;
   }

   @Override
   public boolean isSwapAllowed(Object obj, ItemStack stack) {
      if (this.requireWeapon.isEnabled() && !InventoryStackUtilsEmpty.canAxesEmpty(stack)) {
         return false;
      } else {
         return this.requireAttackKey.isEnabled() && obj != null ? mc.options != null && mc.options.attackKey.isPressed() : true;
      }
   }

   public AutoShieldBreaker() {
      super("Auto Shield Disabler", IconsPngType.COMBAT);
      this.requireWeapon = new BooleanSetting("Require Weapon", "Only help while a sword, axe, or mace is in your main hand", true);
      this.swapBack = new BooleanSetting("Swap Back", "Return to the previously held slot after the hit", true);
      this.addSettings(new ConfigSetting[]{this.requireAttackKey, this.requireWeapon, this.swapBack});
   }

   @Override
   public SwapPreferenceData getSwapPreference() {
      return SwapPreferenceData.getDataForFlag(this.swapBack.isEnabled());
   }
}
