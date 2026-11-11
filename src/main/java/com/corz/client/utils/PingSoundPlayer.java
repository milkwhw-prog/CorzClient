package com.corz.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class PingSoundPlayer {
   private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
   public static final SoundEvent PING_SOUND_EVENT = SoundEvent.of(Identifier.of("corzclient", "pingsound"));

   public static void noOpUpdate() {
   }

   public static boolean isPingSoundPending() {
      return false;
   }

   public static void playPingSound() {
      if (CLIENT.player != null) {
         CLIENT.player.playSound(PING_SOUND_EVENT, 1.0F, 1.0F);
      }
   }
}
