package com.corz.client.utils;

import com.corz.client.mixins.AccessorKeyBinding;
import net.minecraft.client.option.KeyBinding;

public final class ElytraAutoUtils {
   private ElytraAutoUtils() {
   }

   public static void handleRawPressed(KeyBinding keyBinding, boolean rawPressedFlag) {
      if (keyBinding != null) {
         ((AccessorKeyBinding)keyBinding).setPressedRaw(rawPressedFlag);
      }
   }
}
