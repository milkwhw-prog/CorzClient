package com.corz.client.categories.network;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.RangeSetting;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.world.MathClampUtils;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import com.corz.client.utils.KnockbackMath;
import ai.auth.Module;
import com.corz.client.utils.DisconnectMultiplayerHandler;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.IconsPngType;

public class KnockbackDelay extends Module implements DisconnectMultiplayerHandler {
   private final BooleanSetting booleanBase;
   private final RangeSetting groundDelay;
   private ClientPlayerEntity player;
   private volatile long waterKnockbackTime;
   private volatile long waterKnockbackTimeKnockbackW;
   private int groundTicks;
   private boolean knockbackWaterActive;
   private volatile KnockbackDelay.Plan iIiPlan;
   private final RangeSetting airDelay;
   private final IntegerSetting integerHelper = new IntegerSetting("Chance", "How often a hit's knockback is delayed", 40, 0, 100);
   private long knockbackWaterValue;
   private boolean knockbackDelayActive;

   @Override
   public long getKnockbackWater(Packet<?> packet, boolean knockbackWaterFlag, long waterKnockbackTime) {
      KnockbackDelay.Plan plan = this.iIiPlan;
      if (plan != null && KnockbackMath.hasCooldownElapsed(waterKnockbackTime, this.waterKnockbackTime, 475L)) {
         int selfId = plan.selfId();
         if (!ConnectionTimeUtils.canPackets(packet, packet3 -> canKnockbackEntity(packet3, selfId))) {
            return 0L;
         } else if (!KnockbackMath.isWithinWindowSince(waterKnockbackTime, EntityDamageUtils.getTime(selfId), 150L)) {
            return 0L;
         } else {
            ThreadLocalRandom current = ThreadLocalRandom.current();
            if (!KnockbackMath.rollPercentChance((double)plan.chance(), current.nextDouble())) {
               return 0L;
            } else {
               long knockbackWaterValue = KnockbackMath.rollKnockbackDelayMs(plan.grounded(), plan.airLo(), plan.airHi(), plan.groundLo(), plan.groundHi(), current.nextDouble(), 500L);
               if (knockbackWaterValue <= 0L) {
                  return 0L;
               } else {
                  this.waterKnockbackTime = waterKnockbackTime;
                  this.waterKnockbackTimeKnockbackW = waterKnockbackTime;
                  return waterKnockbackTime + knockbackWaterValue;
               }
            }
         }
      } else {
         return 0L;
      }
   }

   @Override
   protected void onDisable() {
      this.iIiPlan = null;
      ConnectionTimeUtils.Inbound.handle(this);
      this.updateKnockbackWater();
   }

   private void handleKnockbackWater(ClientPlayerEntity clientPlayerEntity) {
      long waterKnockbackTime = this.waterKnockbackTimeKnockbackW;
      if (waterKnockbackTime >= 0L) {
         if (waterKnockbackTime != this.knockbackWaterValue) {
            this.knockbackWaterValue = waterKnockbackTime;
            this.knockbackDelayActive = false;
            this.knockbackWaterActive = false;
         }

         if (!this.knockbackDelayActive && ConnectionTimeUtils.Inbound.canFlag(this)) {
            if (clientPlayerEntity.hurtTime > 0) {
               this.knockbackWaterActive = true;
            }

            if (KnockbackMath.isKnockbackDelayExpired(this.knockbackWaterActive, clientPlayerEntity.hurtTime, ConnectionTimeUtils.getTimeTimeMillis() - waterKnockbackTime, 500L)) {
               this.knockbackDelayActive = true;
               ConnectionTimeUtils.Inbound.handle(this);
            }
         }
      }
   }

   private static boolean canKnockbackEntity(Packet<?> packet, int knockbackEntityValue) {
      return packet instanceof EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacke
         ? entityVelocityUpdateS2CPacke.getEntityId() == knockbackEntityValue
         : packet instanceof ExplosionS2CPacket explosionS2CPacket && explosionS2CPacket.playerKnockback().isPresent();
   }

   private boolean canYawPlayers(ClientPlayerEntity player) {
      double yawPlayersValue = MathClampUtils.isClampActive() ? MathClampUtils.getYaw() : player.getYaw();

      for (AbstractClientPlayerEntity abstractClientPlayerEntity : mc.world.getPlayers()) {
         if (abstractClientPlayerEntity != player
            && EntityPlayerUtilsLiving.canSpectatorDead(abstractClientPlayerEntity, Options.INSTANCE2)
            && !(player.distanceTo(abstractClientPlayerEntity) >= 5.0)
            && KnockbackMath.isFacingWithinDegrees(yawPlayersValue, player.getX(), player.getZ(), abstractClientPlayerEntity.getX(), abstractClientPlayerEntity.getZ(), 45.0)
            && KnockbackMath.isFacingWithinDegrees(abstractClientPlayerEntity.getYaw(), abstractClientPlayerEntity.getX(), abstractClientPlayerEntity.getZ(), player.getX(), player.getZ(), 90.0)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void onReset() {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.world != null) {
         if (player != this.player) {
            if (this.player != null) {
               ConnectionTimeUtils.Inbound.handle(this);
            }

            this.player = player;
            this.groundTicks = 0;
         }

         this.groundTicks = player.isOnGround() ? Math.min(this.groundTicks + 1, 1000) : 0;
         this.iIiPlan = this.canTouchingWater(player)
            ? new KnockbackDelay.Plan(
               player.getId(),
               this.integerHelper.getValue(),
               KnockbackMath.hasMinGroundTicks(this.groundTicks),
               this.airDelay.getMin(),
               this.airDelay.getMax(),
               this.groundDelay.getMin(),
               this.groundDelay.getMax()
            )
            : null;
         this.handleKnockbackWater(player);
      } else {
         this.iIiPlan = null;
      }
   }

   public KnockbackDelay() {
      super("Knockback Delay", IconsPngType.NETWORK);
      this.airDelay = new RangeSetting("Air Delay", "Random milliseconds to delay knockback when you were just in the air", 50.0, 100.0, 0.0, 500.0, 10.0);
      this.groundDelay = new RangeSetting("Ground Delay", "Random milliseconds to delay knockback when you stand on the ground", 200.0, 250.0, 0.0, 500.0, 10.0);
      this.booleanBase = new BooleanSetting("Water Check", "Don't delay knockback while you are in water", false);
      this.waterKnockbackTime = -1L;
      this.waterKnockbackTimeKnockbackW = -1L;
      this.knockbackWaterValue = -1L;
      this.addSettings(new ConfigSetting[]{this.integerHelper, this.airDelay, this.groundDelay, this.booleanBase});
   }

   private void updateKnockbackWater() {
      this.iIiPlan = null;
      this.waterKnockbackTime = -1L;
      this.waterKnockbackTimeKnockbackW = -1L;
      this.player = null;
      this.groundTicks = 0;
      this.knockbackWaterValue = -1L;
      this.knockbackDelayActive = false;
      this.knockbackWaterActive = false;
   }

   @Override
   protected void onEnable() {
      this.updateKnockbackWater();
   }

   private boolean canTouchingWater(ClientPlayerEntity player2) {
      if (this.integerHelper.getValue() <= 0) {
         return false;
      } else {
         return this.booleanBase.isEnabled() && player2.isTouchingWater() ? false : this.canYawPlayers(player2);
      }
   }

   private record Plan(int selfId, int chance, boolean grounded, double airLo, double airHi, double groundLo, double groundHi) {
   }
}
