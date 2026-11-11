package com.corz.client.categories.settings;

import com.corz.client.config.FriendNameStore;
import com.corz.client.gui.Button;
import com.corz.client.gui.DisabledEnabled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class MiddleClickFriend extends Module {
   @Override
   protected void onButtonClick(Button button) {
      if (button.getValue() == 1) {
         if (button.getButton() == 2) {
            if (mc.currentScreen == null && mc.player != null) {
               if (mc.crosshairTarget instanceof EntityHitResult entityHitResult) {
                  Entity entity = entityHitResult.getEntity();
                  if (entity instanceof PlayerEntity && entity != mc.player) {
                     String middleClickText = entity.getName().getString();
                     boolean rootLinked = FriendNameStore.containsFriendIgnoreCase(middleClickText);
                     FriendNameStore.toggleFriend(middleClickText);
                     DisabledEnabled.getInstance().handleDisabledEnabled(rootLinked ? "Friend Removed" : "Friend Added", middleClickText, !rootLinked);
                  }
               }
            }
         }
      }
   }

   public MiddleClickFriend() {
      super("Quick Friend", IconsPngType.SETTING);
   }
}
