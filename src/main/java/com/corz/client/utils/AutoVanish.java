package com.corz.client.utils;

import net.minecraft.network.packet.Packet;

public class AutoVanish {
   private boolean autoVanishActive;
   public final Packet<?> packet;

   public boolean isFlag() {
      return this.autoVanishActive;
   }

   public AutoVanish(Packet<?> vanishPacket) {
      this.packet = vanishPacket;
   }

   public void updateFlag() {
      this.autoVanishActive = true;
   }
}
