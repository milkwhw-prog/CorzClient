package com.corz.client.categories.movement;

import com.corz.client.categories.bots.AutoBuilder;
import ai.auth.Module;
import com.corz.client.categories.movement.Scaffold;
import com.corz.client.utils.ElytraAutoUtils;
import com.corz.client.utils.IconsPngType;

public class Sprint extends Module {
   @Override
   protected void onReset() {
      if (!AutoBuilder.corzGapActive) {
         if (!Scaffold.isGodBridgeFlag()) {
            if (mc.player != null && mc.player.forwardSpeed > 0.0F) {
               ElytraAutoUtils.handleRawPressed(mc.options.sprintKey, true);
            }
         }
      }
   }

   public Sprint() {
      super("Sprint", IconsPngType.MOVEMENT);
   }
}
