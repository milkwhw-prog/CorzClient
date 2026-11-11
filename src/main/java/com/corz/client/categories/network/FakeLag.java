package com.corz.client.categories.network;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.world.MathClampUtils;
import java.util.ArrayDeque;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PickItemFromBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import com.corz.client.categories.combat.SilentAim;
import ai.auth.Module;
import com.corz.client.utils.WeakReferenceHelper;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.PlayerCrystalUtils;
import com.corz.client.utils.LagFakeUtilsAmount;
import com.corz.client.utils.LagFakeUtilsConst;
import com.corz.client.utils.IconsPngType;

public class FakeLag extends Module {
   private int packetsHeldValue;
   private Vec3d heldPos;
   private long lagTimeMs;
   private final EnumSetting enumHelper = new EnumSetting(
      "Mode", "Latency: always lag by Delay. Dynamic: only lag while you walk toward the player or mob you aim at.", "Dynamic", "Latency", "Dynamic"
   );
   private long seesPacketsTime;
   private final WeakReferenceHelper iIiWeakReferenceHelper;
   private final ArrayDeque<FakeLag.Mark> arrayDeque;
   private long seesPacketsTimeIIi;
   private final BooleanSetting booleanBase;
   private Vec3d entityPos;
   private final IntegerSetting integerHelper = new IntegerSetting(
      "Delay", "How long your packets are held, in milliseconds. Higher = more lag.", 100, 10, 1000, 10
   );
   private long packetsValue;
   private static final Options ANIMALS_TEAMMATES = Options.INSTANCE.getAnimalsTeammates(true);

   private void handleDequeArray(InventoryPlayer inventoryPlayer, Vec3d entityPos) {
      if (entityPos != null && !inventoryPlayer.isInventoryPlayerActive()) {
         if (this.arrayDeque.isEmpty()) {
            this.entityPos = entityPos;
         } else {
            this.arrayDeque.addLast(new FakeLag.Mark(entityPos, 0L));
         }
      }
   }

   private void updatePacketsTime() {
      this.arrayDeque.clear();
      this.packetsHeldValue = -1;
      this.heldPos = null;
      this.entityPos = null;
      this.lagTimeMs = 0L;
      this.seesPacketsTime = Long.MIN_VALUE;
      this.packetsValue = 0L;
   }

   private static boolean canFlag(Packet<?> packet) {
      return packet instanceof PlayerInteractEntityC2SPacket
         || packet instanceof PlayerInteractBlockC2SPacket
         || packet instanceof PlayerInteractItemC2SPacket
         || packet instanceof PlayerActionC2SPacket
         || packet instanceof ChatMessageC2SPacket
         || packet instanceof CommandExecutionC2SPacket
         || packet instanceof ChatCommandSignedC2SPacket
         || packet instanceof RequestCommandCompletionsC2SPacket
         || packet instanceof CreativeInventoryActionC2SPacket
         || packet instanceof ButtonClickC2SPacket
         || packet instanceof SelectMerchantTradeC2SPacket
         || packet instanceof CraftRequestC2SPacket
         || packet instanceof UpdateSignC2SPacket
         || packet instanceof BookUpdateC2SPacket
         || packet instanceof PickItemFromBlockC2SPacket;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (this.booleanBase.isEnabled() && mc.player != null) {
         this.handleDequeArray(ConnectionTimeUtils.getTimeTimeMillis());
         Vec3d entityPos = this.entityPos;
         if (entityPos != null && ConnectionTimeUtils.Outbound.canFlag(this)) {
            if (!(entityPos.squaredDistanceTo(mc.player.getEntityPos()) < 0.0025)) {
               float widthScaled = mc.player.getWidth() / 2.0F;
               float height = mc.player.getHeight();
               if (!VertexBuilderUtils.canVector(entityPos.x, entityPos.y + height / 2.0, entityPos.z, height)) {
                  Matrix4f matrix = VertexBuilderUtils.getMatrix();
                  float fakeLagValue = VertexBuilderUtils.getAmountX(entityPos.x - widthScaled);
                  float f2 = VertexBuilderUtils.getAmount(entityPos.y);
                  float amountForDouble = VertexBuilderUtils.getAmountForDouble(entityPos.z - widthScaled);
                  float amountX = VertexBuilderUtils.getAmountX(entityPos.x + widthScaled);
                  float f5 = VertexBuilderUtils.getAmount(entityPos.y + height);
                  float amountForDouble6 = VertexBuilderUtils.getAmountForDouble(entityPos.z + widthScaled);
                  VertexBuilderUtils.handle(matrix, fakeLagValue, f2, amountForDouble, amountX, f5, amountForDouble6, 0.26666668F, 0.53333336F, 1.0F, 0.2509804F);
                  VertexBuilderUtils.handle(matrix, fakeLagValue, f2, amountForDouble, amountX, f5, amountForDouble6, -1438349057, 1.5F);
               }
            }
         }
      }
   }

   @Override
   protected void onEnable() {
      this.updatePacketsTime();
      this.iIiWeakReferenceHelper.handleWeakReference(mc.world);
      this.seesPacketsTimeIIi = ConnectionTimeUtils.getTime();
      this.entityPos = mc.player != null ? mc.player.getEntityPos() : null;
   }

   private void updateTime() {
      this.lagTimeMs = 0L;
      if (ConnectionTimeUtils.Outbound.canFlag(this)) {
         ConnectionTimeUtils.Outbound.handle(this);
      }

      this.updateDequeArray();
   }

   private void updateDequeArray() {
      FakeLag.Mark peekLast = this.arrayDeque.peekLast();
      if (peekLast != null) {
         this.entityPos = peekLast.pos();
      }

      this.arrayDeque.clear();
   }

   private static boolean canOffhandAction(Packet<?> packet) {
      if (packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke) {
         return PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke);
      } else {
         return packet instanceof ClickSlotC2SPacket ? true : packet instanceof PlayerActionC2SPacket playerActionC2SPacket && playerActionC2SPacket.getAction() == Action.SWAP_ITEM_WITH_OFFHAND;
      }
   }

   private boolean isEquals() {
      return "Dynamic".equals(this.enumHelper.getCurrentOption());
   }

   private boolean canPlayerPackets(long playerPacketsValue, Vec3d vec, Vec3d vec2) {
      if (playerPacketsValue < this.packetsValue) {
         return false;
      } else if (mc.player.isUsingItem() && !mc.player.isBlocking()) {
         return false;
      } else if (!mc.player.isOnGround() && mc.player.fallDistance > 3.0) {
         return false;
      } else if (!(
         (this.packetsHeldValue == -1 ? null : mc.world.getEntityById(this.packetsHeldValue)) instanceof LivingEntity livingEntity
            && !livingEntity.isRemoved()
            && livingEntity.isAlive()
      )) {
         return false;
      } else if (vec2 != null && vec != null) {
         double playerPacketsValue2 = mc.player.getEyeY() - mc.player.getY();
         double widthScaled = livingEntity.getWidth() / 2.0;
         double height = livingEntity.getHeight();
         double currentValue = LagFakeUtilsAmount.getAmount(vec.x, vec.y + playerPacketsValue2, vec.z, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), widthScaled, height);
         double thresholdValue = LagFakeUtilsAmount.getAmount(vec2.x, vec2.y + playerPacketsValue2, vec2.z, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), widthScaled, height);
         return !LagFakeUtilsConst.canFlag(currentValue, thresholdValue);
      } else {
         return true;
      }
   }

   public FakeLag() {
      super("Fake Lag", IconsPngType.NETWORK);
      this.booleanBase = new BooleanSetting("Render", "Draws a box where the server still sees you while packets are held.", true);
      this.arrayDeque = new ArrayDeque<>();
      this.iIiWeakReferenceHelper = new WeakReferenceHelper();
      this.packetsHeldValue = -1;
      this.seesPacketsTime = Long.MIN_VALUE;
      this.seesPacketsTimeIIi = -1L;
      this.addSettings(new ConfigSetting[]{this.enumHelper, this.integerHelper, this.booleanBase});
   }

   private void handleDequeArray(long dequeArrayLimit) {
      if (!this.arrayDeque.isEmpty()) {
         if (!ConnectionTimeUtils.Outbound.canFlag(this)) {
            this.updateDequeArray();
         } else {
            while (!this.arrayDeque.isEmpty() && this.arrayDeque.peekFirst().until() <= dequeArrayLimit) {
               this.entityPos = this.arrayDeque.pollFirst().pos();
            }
         }
      }
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null) {
         if (!this.iIiWeakReferenceHelper.canWeakReference(mc.world)) {
            ConnectionTimeUtils.Outbound.handle(this);
            this.updatePacketsTime();
            this.iIiWeakReferenceHelper.handleWeakReference(mc.world);
            this.entityPos = mc.player.getEntityPos();
         }

         long seesPacketsTime = ConnectionTimeUtils.getTime();
         if (seesPacketsTime != this.seesPacketsTimeIIi) {
            this.seesPacketsTimeIIi = seesPacketsTime;
            this.heldPos = null;
            this.lagTimeMs = 0L;
            this.arrayDeque.clear();
            this.entityPos = mc.player.getEntityPos();
         }

         boolean equals = this.isEquals();
         this.packetsHeldValue = equals ? this.getRaycastPlayer() : -1;
         if (equals && this.packetsHeldValue == -1) {
            this.updateTime();
         }

         this.handleDequeArray(ConnectionTimeUtils.getTimeTimeMillis());
      }
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (mc.isOnThread() && mc.player != null && this.iIiWeakReferenceHelper.canWeakReference(mc.world)) {
         if (!inventoryPlayer.isOpenedByClient()) {
            var packet = inventoryPlayer.packet;
            long llIValue = ConnectionTimeUtils.getTimeTimeMillis();
            long seesPacketsTime = ClientThreadUtils.getTime();
            Vec3d vec3d = this.heldPos;
            Vec3d vec3d2 = packet instanceof PlayerMoveC2SPacket playerMoveC2SPacket && playerMoveC2SPacket.changesPosition() ? new Vec3d(playerMoveC2SPacket.getX(0.0), playerMoveC2SPacket.getY(0.0), playerMoveC2SPacket.getZ(0.0)) : null;
            if (vec3d2 != null) {
               this.heldPos = vec3d2;
            }

            if (canOffhandAction(packet)) {
               this.updateTime();
               this.seesPacketsTime = seesPacketsTime;
               this.handleDequeArray(inventoryPlayer, vec3d2);
            } else if (this.seesPacketsTime != seesPacketsTime && !mc.player.isDead()) {
               long time;
               if (this.isEquals()) {
                  if (canFlag(packet)) {
                     this.updateTime();
                     this.seesPacketsTime = seesPacketsTime;
                     if (packet instanceof PlayerInteractBlockC2SPacket) {
                        this.packetsValue = llIValue + 500L;
                     }

                     this.handleDequeArray(inventoryPlayer, vec3d2);
                     return;
                  }

                  if (!this.canPlayerPackets(llIValue, vec3d, vec3d2)) {
                     this.updateTime();
                     this.handleDequeArray(inventoryPlayer, vec3d2);
                     return;
                  }

                  time = LagFakeUtilsConst.getTime(llIValue, this.integerHelper.getValue(), this.lagTimeMs);
               } else {
                  time = llIValue + this.integerHelper.getValue();
               }

               if (time > llIValue && ConnectionTimeUtils.Outbound.canPacket(this, inventoryPlayer, time)) {
                  if (vec3d2 != null) {
                     this.lagTimeMs = time;
                     this.arrayDeque.addLast(new FakeLag.Mark(vec3d2, time));
                  }
               } else {
                  this.handleDequeArray(inventoryPlayer, vec3d2);
               }
            } else {
               this.handleDequeArray(inventoryPlayer, vec3d2);
            }
         }
      }
   }

   private int getRaycastPlayer() {
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d cosSin = MathClampUtils.getCosSin(SilentAim.getYawSilent(), SilentAim.getSilentPitch());
      Vec3d add = eyePos.add(cosSin.multiply(10.0));
      BlockHitResult raycast = mc.world.raycast(new RaycastContext(eyePos, add, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
      if (raycast != null && raycast.getType() != Type.MISS) {
         add = raycast.getPos();
      }

      Box expand = mc.player.getBoundingBox().stretch(add.subtract(eyePos)).expand(1.0);
      EntityHitResult raycast2 = ProjectileUtil.raycast(
         mc.player,
         eyePos,
         add,
         expand,
         entity -> entity != mc.player && !entity.isSpectator() && entity.canHit() && EntityPlayerUtilsLiving.canSpectatorDead(entity, ANIMALS_TEAMMATES),
         eyePos.squaredDistanceTo(add)
      );
      return raycast2 != null && raycast2.getEntity() instanceof LivingEntity livingEntity ? livingEntity.getId() : -1;
   }

   @Override
   protected void onDisable() {
      ConnectionTimeUtils.Outbound.handle(this);
      this.updatePacketsTime();
      this.iIiWeakReferenceHelper.updateWeakReference();
   }

   private record Mark(Vec3d pos, long until) {
   }
}
