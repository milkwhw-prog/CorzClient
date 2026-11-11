package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.EnumSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class Criticals extends Module {
   private boolean resendingAttack;
   private int resendDelayTicks;
   public final EnumSetting modeSetting = new EnumSetting("Mode", "Legit = jump, Minimal = tiny hop, Silent = no jump", "Legit", "Legit", "Minimal", "Silent");
   private PlayerInteractEntityC2SPacket pendingAttackPacket;

   @Override
   protected void onDisable() {
      this.pendingAttackPacket = null;
      this.resendDelayTicks = 0;
      this.resendingAttack = false;
   }

   public Criticals() {
      super("Criticals", IconsPngType.COMBAT);
      this.addSettings(new ConfigSetting[]{this.modeSetting});
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (this.isEligibleForCrit() && !this.resendingAttack) {
         if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SP && PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SP)) {
            Entity entityById = mc.world.getEntityById(PlayerCrystalUtils.getEntityId(playerInteractEntityC2SP));
            if (!(entityById instanceof PlayerEntity)) {
               return;
            }

            if (this.modeSetting.getCurrentOption().equals("Silent")) {
               this.sendPositionOffsetPacket(0.0625F);
               this.sendPositionOffsetPacket(0.0F);
            } else {
               this.pendingAttackPacket = playerInteractEntityC2SP;
               if (this.modeSetting.getCurrentOption().equals("Legit")) {
                  mc.player.jump();
               } else {
                  Vec3d velocity = mc.player.getVelocity();
                  mc.player.setVelocity(new Vec3d(velocity.x, velocity.y + 0.15, velocity.z));
               }

               this.resendDelayTicks = this.modeSetting.getCurrentOption().equals("Legit") ? 6 : 1;
               inventoryPlayer.updateFlag();
            }
         }

         if (inventoryPlayer.packet instanceof HandSwingC2SPacket && !this.modeSetting.getCurrentOption().equals("Silent") && this.pendingAttackPacket != null) {
            inventoryPlayer.updateFlag();
         }
      }
   }

   private void sendPositionOffsetPacket(float playerPacketValue) {
      mc.player
         .networkHandler
         .sendPacket(
            new PositionAndOnGround(
               mc.player.getX(), mc.player.getY() + playerPacketValue, mc.player.getZ(), false, mc.player.horizontalCollision
            )
         );
   }

   @Override
   protected void onReset() {
      if (this.pendingAttackPacket != null) {
         if (this.resendDelayTicks > 0) {
            this.resendDelayTicks--;
         } else {
            this.resendingAttack = true;
            HitResult crosshairTarget = mc.crosshairTarget;
            if (crosshairTarget != null && crosshairTarget.getType() == Type.ENTITY && mc.player != null) {
               mc.player.networkHandler.sendPacket(this.pendingAttackPacket);
               mc.player.swingHand(Hand.MAIN_HAND);
            }

            this.resendingAttack = false;
            this.pendingAttackPacket = null;
         }
      }
   }

   private boolean isEligibleForCrit() {
      return mc.player != null
         && mc.world != null
         && mc.player.isOnGround()
         && !mc.player.isSubmergedInWater()
         && !mc.player.isInLava()
         && !mc.player.isClimbing();
   }
}
