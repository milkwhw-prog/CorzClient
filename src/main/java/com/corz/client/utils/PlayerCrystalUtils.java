package com.corz.client.utils;

import com.corz.client.mixins.AccessorPlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.Handler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public final class PlayerCrystalUtils {
   public static boolean canHandleFlag(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke) {
      PlayerCrystalUtils.AttackProbe attackProbe = new PlayerCrystalUtils.AttackProbe();
      playerInteractEntityC2SPacke.handle(attackProbe);
      return attackProbe.attackActive;
   }

   private PlayerCrystalUtils() {
   }

   public static int getEntityId(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke) {
      return ((AccessorPlayerInteractEntityC2SPacket)playerInteractEntityC2SPacke).getEntityId();
   }

   private static final class AttackProbe implements Handler {
      boolean attackActive;

      @Override
      public void interact(Hand hand) {
      }

      @Override
      public void attack() {
         this.attackActive = true;
      }

      @Override
      public void interactAt(Hand hand, Vec3d vec) {
      }
   }
}
