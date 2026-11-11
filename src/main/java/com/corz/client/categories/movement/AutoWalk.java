package com.corz.client.categories.movement;

import ai.auth.Module;
import com.corz.client.utils.IconsPngType;
public class AutoWalk extends Module {
   @Override
   protected void onDisable() {
      if (mc.player != null) {
         mc.options.forwardKey.setPressed(false);
      }
   }

   @Override
   protected void onReset() {
      if (mc.player != null) {
         mc.options.forwardKey.setPressed(true);
      }
   }

   public AutoWalk() {
      super("Auto Walk", IconsPngType.MOVEMENT);
   }
}
