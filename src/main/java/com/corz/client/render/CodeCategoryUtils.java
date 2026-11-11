package com.corz.client.render;

import com.corz.client.mixins.AccessorKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

public final class CodeCategoryUtils {
   private CodeCategoryUtils() {
   }

   public static boolean canCategoryCode(KeyBinding keyBinding, int categoryCodeValue) {
      Key boundKey = getBoundKey(keyBinding);
      return boundKey != null && boundKey.getCategory() == Type.KEYSYM && boundKey.getCode() == categoryCodeValue;
   }

   public static boolean canCategoryCodeForKeyBindingInt(KeyBinding keyBinding, int categoryCodeValue) {
      Key boundKey = getBoundKey(keyBinding);
      return boundKey != null && boundKey.getCategory() == Type.MOUSE && boundKey.getCode() == categoryCodeValue;
   }

   public static boolean isKeyPressed(KeyBinding keyBinding) {
      Key boundKey = getBoundKey(keyBinding);
      if (boundKey == null) {
         return false;
      } else {
         Window window = MinecraftClient.getInstance().getWindow();
         if (window == null) {
            return false;
         } else if (boundKey.getCategory() == Type.MOUSE) {
            return GLFW.glfwGetMouseButton(window.getHandle(), boundKey.getCode()) == 1;
         } else {
            return boundKey.getCategory() == Type.KEYSYM ? InputUtil.isKeyPressed(window, boundKey.getCode()) : false;
         }
      }
   }

   private static Key getBoundKey(KeyBinding keyBinding) {
      if (keyBinding == null) {
         return null;
      } else {
         Key boundKey = ((AccessorKeyBinding)keyBinding).getBoundKey();
         return boundKey != null && boundKey.getCode() >= 0 ? boundKey : null;
      }
   }
}
