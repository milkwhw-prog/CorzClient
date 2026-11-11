package com.corz.client.utils;

import com.corz.client.mixins.AccessorKeyBinding;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import org.lwjgl.glfw.GLFW;

public final class WindowCode {
   private final Map<KeyBinding, Boolean> restoreCloseBooleanMap;
   private final Map<KeyBinding, Boolean> windowCodeBooleanMap = new LinkedHashMap<>();

   public double getAmount(KeyBinding keyBinding, KeyBinding keyBinding2) {
      return (this.canFlag(keyBinding) ? 1.0 : 0.0) - (this.canFlag(keyBinding2) ? 1.0 : 0.0);
   }

   public WindowCode() {
      this.restoreCloseBooleanMap = new LinkedHashMap<>();
   }

   public void update() {
      this.windowCodeBooleanMap.clear();
   }

   public void updateWindowCodeBooleanMap() {
      this.windowCodeBooleanMap.clear();
      this.update3();
   }

   public void updateRestoreClose() {
      for (Entry entry : this.restoreCloseBooleanMap.entrySet()) {
         KeyBinding keyBinding = (KeyBinding)entry.getKey();
         if (isValue(keyBinding)) {
            if (keyBinding instanceof StickyKeyBinding stickyKeyBinding) {
               stickyKeyBinding.shouldRestoreOnScreenClose();
            }

            ElytraAutoUtils.handleRawPressed(keyBinding, (Boolean)entry.getValue());
         }
      }

      this.windowCodeBooleanMap.clear();
      this.restoreCloseBooleanMap.clear();
   }

   private static boolean isValue(KeyBinding keyBinding) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance == null || instance.options == null) {
         return false;
      } else if (keyBinding == instance.options.sneakKey) {
         return instance.options.getSneakToggled().getValue();
      } else {
         return keyBinding == instance.options.sprintKey ? instance.options.getSprintToggled().getValue() : false;
      }
   }

   private static boolean isKeyPressed(KeyBinding keyBinding) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance != null && instance.getWindow() != null && instance.isWindowFocused()) {
         Key boundKey = ((AccessorKeyBinding)keyBinding).getBoundKey();
         if (boundKey != null && boundKey.getCode() >= 0) {
            if (boundKey.getCategory() == Type.KEYSYM) {
               return InputUtil.isKeyPressed(instance.getWindow(), boundKey.getCode());
            } else {
               return boundKey.getCategory() == Type.MOUSE ? GLFW.glfwGetMouseButton(instance.getWindow().getHandle(), boundKey.getCode()) == 1 : false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean isPressedRaw(KeyBinding keyBinding) {
      return ((AccessorKeyBinding)keyBinding).getPressedRaw();
   }

   public void handle(KeyBinding keyBinding, boolean windowCodeFlag) {
      if (keyBinding != null) {
         this.windowCodeBooleanMap.put(keyBinding, windowCodeFlag);
      }
   }

   public void update3() {
      for (Entry entry : this.windowCodeBooleanMap.entrySet()) {
         KeyBinding keyBinding = (KeyBinding)entry.getKey();
         if (!this.restoreCloseBooleanMap.containsKey(keyBinding)) {
            this.restoreCloseBooleanMap.put(keyBinding, isPressedRaw(keyBinding));
         }

         ElytraAutoUtils.handleRawPressed(keyBinding, (Boolean)entry.getValue());
      }

      Iterator iterator = this.restoreCloseBooleanMap.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry next2 = (Entry)iterator.next();
         if (!this.windowCodeBooleanMap.containsKey(next2.getKey())) {
            ElytraAutoUtils.handleRawPressed(
               (KeyBinding)next2.getKey(), isValue((KeyBinding)next2.getKey()) ? (Boolean)next2.getValue() : isKeyPressed((KeyBinding)next2.getKey())
            );
            iterator.remove();
         }
      }

      this.windowCodeBooleanMap.clear();
   }

   public boolean canFlag(KeyBinding keyBinding) {
      if (keyBinding == null) {
         return false;
      } else if (isValue(keyBinding)) {
         Boolean boolean2 = this.restoreCloseBooleanMap.get(keyBinding);
         return boolean2 != null ? boolean2 : isPressedRaw(keyBinding);
      } else {
         return isKeyPressed(keyBinding);
      }
   }

   public boolean isFlag() {
      return !this.restoreCloseBooleanMap.isEmpty();
   }
}
