package com.corz.client.categories.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class CrystalOptimizer extends Module {
   public CrystalOptimizer() {
      super("Crystal Optimizer", IconsPngType.COMBAT);
   }

   @Override
   public void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke) {
         if (mc.world != null && mc.player != null) {
            if (PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
               Entity entityById = mc.world.getEntityById(PlayerCrystalUtils.getEntityId(playerInteractEntityC2SPacke));
               if (entityById instanceof EndCrystalEntity) {
                  if (mc.player.getStatusEffect(StatusEffects.WEAKNESS) == null) {
                     entityById.setRemoved(RemovalReason.KILLED);
                     entityById.onRemoved();
                  }
               }
            }
         }
      }
   }
}
