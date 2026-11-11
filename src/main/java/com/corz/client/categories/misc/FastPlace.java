package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import ai.auth.Module;
import com.corz.client.utils.Cooldown;
import com.corz.client.utils.IconsPngType;

public class FastPlace extends Module {
   private final BooleanSetting booleanBase;
   private final IntegerSetting integerHelper;
   private final BooleanSetting fastPlaceBooleanBase;
   private final BooleanSetting onlyXp = new BooleanSetting("Only XP", "", false);
   private final BooleanSetting anticheatSafe;

   @Override
   public void onPostItemUse(Cooldown cooldown) {
      ItemStack mainHandStack = mc.player.getMainHandStack();
      ItemStack offHandStack = mc.player.getOffHandStack();
      Item item = mainHandStack.getItem();
      Item item2 = mc.player.getOffHandStack().getItem();
      if (mainHandStack.isOf(Items.EXPERIENCE_BOTTLE) || offHandStack.isOf(Items.EXPERIENCE_BOTTLE) || !this.onlyXp.isEnabled()) {
         if (!this.onlyXp.isEnabled()) {
            if (!(item instanceof BlockItem) && !(item2 instanceof BlockItem)) {
               if (!this.fastPlaceBooleanBase.isEnabled()) {
                  return;
               }
            } else if (!this.booleanBase.isEnabled()) {
               return;
            }
         }

         if (item.getComponents().get(DataComponentTypes.FOOD) == null) {
            if (item2.getComponents().get(DataComponentTypes.FOOD) == null) {
               if (!mainHandStack.isOf(Items.RESPAWN_ANCHOR) && !mainHandStack.isOf(Items.GLOWSTONE) && !offHandStack.isOf(Items.RESPAWN_ANCHOR) && !offHandStack.isOf(Items.GLOWSTONE)) {
                  if (!(item instanceof RangedWeaponItem) && !(item2 instanceof RangedWeaponItem)) {
                     int useCooldown = this.integerHelper.getValue();
                     if (this.anticheatSafe.isEnabled() && useCooldown < 2) {
                        double randomDouble = ThreadLocalRandom.current().nextDouble();
                        useCooldown = randomDouble < 0.05 ? 4 : (randomDouble < 0.3 ? 3 : 2);
                     }

                     cooldown.useCooldown = useCooldown;
                  }
               }
            }
         }
      }
   }

   public FastPlace() {
      super("Fast Place", IconsPngType.MISC);
      this.booleanBase = new BooleanSetting("Blocks", "", true);
      this.fastPlaceBooleanBase = new BooleanSetting("Items", "", true);
      this.integerHelper = new IntegerSetting("Delay", "", 0, 0, 10);
      this.anticheatSafe = new BooleanSetting(
         "Anticheat Safe",
         "Delay 0-1 places every tick (50ms) \u2014 under Vulcan Fastplace's minimum, flags every place. Safe floors it at the fastest passing cadence (jittered 2-3 ticks); off = raw delay",
         true
      );
      this.addSettings(new ConfigSetting[]{this.onlyXp, this.booleanBase, this.fastPlaceBooleanBase, this.integerHelper, this.anticheatSafe});
   }
}
