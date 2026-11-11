package com.corz.client.gui;

import net.minecraft.client.MinecraftClient;

public final class ScreenSoundsUtils {
   public static boolean isNoScreenOpen() {
      MinecraftClient instance = getInstance();
      return instance == null ? false : instance.currentScreen == null;
   }

   private static MinecraftClient getInstance() {
      return MinecraftClient.getInstance();
   }

   private ScreenSoundsUtils() {
   }

   public static boolean isScreenGuiApiOpen() {
      MinecraftClient instance = getInstance();
      return instance != null && instance.currentScreen instanceof ScreenGuiApi;
   }

   public static boolean isGuiClickApiOpen() {
      MinecraftClient instance = getInstance();
      return instance != null && instance.currentScreen instanceof GuiClickApi;
   }
}
