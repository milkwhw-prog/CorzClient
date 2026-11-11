package com.corz.client.utils;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;

public class InventoryPlayer {
   public final Packet<?> packet;
   private final ClientConnection clientConnection;
   private boolean inventoryPlayerActive;
   private boolean openedByClient;

   public ClientConnection getClientConnection() {
      return this.clientConnection;
   }

   public InventoryPlayer(Packet<?> packet2, ClientConnection clientConnection) {
      this.packet = packet2;
      this.clientConnection = clientConnection;
      this.openedByClient = false;
   }

   public void updateFlag() {
      this.openedByClient = true;
   }

   public boolean isFlag() {
      return this.openedByClient || this.inventoryPlayerActive;
   }

   public boolean isInventoryPlayerActive() {
      return this.inventoryPlayerActive;
   }

   public InventoryPlayer(Packet<?> packet2) {
      this(packet2, null);
   }

   public boolean isOpenedByClient() {
      return this.openedByClient;
   }

   public void updateFlagInventoryPlayerActive() {
      this.inventoryPlayerActive = true;
   }
}
