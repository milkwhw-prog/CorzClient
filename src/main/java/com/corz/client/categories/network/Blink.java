package com.corz.client.categories.network;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.WeakReferenceHelper;
import com.corz.client.utils.WorldExtractionEntity;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.BlinkFlagUtils;
import com.corz.client.utils.IconsPngType;

public class Blink extends Module {
   private final IntegerSetting integerHelper;
   private float yaw;
   private final BooleanSetting booleanBase;
   private long lastTimeStampMs;
   private int heldPacketTicks;
   private final List<Vec3d> sendPacketsItems;
   private final BooleanSetting spawnFake;
   private final WeakReferenceHelper worldReference;
   private int activeTickCount;
   private float width;
   private float bodyYaw;
   private float height;
   private final BooleanSetting breadcrumbs = new BooleanSetting(
      "Breadcrumbs", "Leaves small markers on the ground along the path you walked while Blink is on.", false
   );
   private long yawPlayerValue;
   private float pitch;
   private Vec3d entityPos;
   private float headYaw;

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.player != null) {
         Matrix4f matrix = VertexBuilderUtils.getMatrix();
         if (this.breadcrumbs.isEnabled() && !this.sendPacketsItems.isEmpty()) {
            this.handlePacketsSend(matrix);
         }

         if (this.entityPos != null && !WorldExtractionEntity.canInstanceWorld(mc.world)) {
            this.handleHeightWidth(matrix);
         }
      }
   }

   private void updateYawEntity() {
      if (this.entityPos != null) {
         WorldExtractionEntity.handleInventoryInstance(
            mc.world, mc.player, this.entityPos, this.yaw, this.pitch, this.headYaw, this.bodyYaw
         );
      }
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (mc.isOnThread() && mc.player != null && this.worldReference.canWeakReference(mc.world)) {
         if (!inventoryPlayer.isOpenedByClient()) {
            if (ConnectionTimeUtils.Outbound.canPacket(this, inventoryPlayer, Long.MAX_VALUE)) {
               this.activeTickCount++;
               if (inventoryPlayer.packet instanceof PlayerMoveC2SPacket playerMoveC2SPacket && playerMoveC2SPacket.changesPosition()) {
                  this.handlePacketsSend(playerMoveC2SPacket);
               }
            }
         }
      }
   }

   private void handlePacketsSend(PlayerMoveC2SPacket playerMoveC2SPacket) {
      this.heldPacketTicks++;
      if (BlinkFlagUtils.canFlag(this.heldPacketTicks) && this.sendPacketsItems.size() < 512) {
         double x = playerMoveC2SPacket.getX(mc.player.getX());
         double y = playerMoveC2SPacket.getY(mc.player.getY());
         double z = playerMoveC2SPacket.getZ(mc.player.getZ());
         Vec3d vec3d = this.sendPacketsItems.isEmpty() ? null : this.sendPacketsItems.get(this.sendPacketsItems.size() - 1);
         if (vec3d == null || BlinkFlagUtils.canFlag(vec3d.x, vec3d.z, x, z)) {
            double[] packetsSendValues = BlinkFlagUtils.getAmounts(this.sendPacketsItems.size(), playerMoveC2SPacket.getYaw(mc.player.getYaw()));
            this.sendPacketsItems.add(new Vec3d(x + packetsSendValues[0], y, z + packetsSendValues[1]));
         }
      }
   }

   private void handlePacketsSend(Matrix4f matrix) {
      for (int i = 0; i < this.sendPacketsItems.size(); i++) {
         Vec3d sendpacketsitem = this.sendPacketsItems.get(i);
         if (!VertexBuilderUtils.canVector(sendpacketsitem.x, sendpacketsitem.y, sendpacketsitem.z, 0.5F)) {
            float packetsSendValue = VertexBuilderUtils.getAmountX(sendpacketsitem.x - 0.1F);
            float amountX = VertexBuilderUtils.getAmountX(sendpacketsitem.x + 0.1F);
            float amountForDouble3 = VertexBuilderUtils.getAmountForDouble(sendpacketsitem.z - 0.1F);
            float amountForDouble = VertexBuilderUtils.getAmountForDouble(sendpacketsitem.z + 0.1F);
            float deltaY = VertexBuilderUtils.getAmount(sendpacketsitem.y + 0.01F);
            VertexBuilderUtils.handle(matrix, packetsSendValue, deltaY, amountForDouble3, packetsSendValue, deltaY, amountForDouble, amountX, deltaY, amountForDouble, amountX, deltaY, amountForDouble3, i == 0 ? -855644672 : -855691216);
            VertexBuilderUtils.handleForMatrix4fFloat(matrix, packetsSendValue, deltaY, amountForDouble3, amountX, deltaY, amountForDouble3, -872415232, 1.5F);
            VertexBuilderUtils.handleForMatrix4fFloat(matrix, amountX, deltaY, amountForDouble3, amountX, deltaY, amountForDouble, -872415232, 1.5F);
            VertexBuilderUtils.handleForMatrix4fFloat(matrix, amountX, deltaY, amountForDouble, packetsSendValue, deltaY, amountForDouble, -872415232, 1.5F);
            VertexBuilderUtils.handleForMatrix4fFloat(matrix, packetsSendValue, deltaY, amountForDouble, packetsSendValue, deltaY, amountForDouble3, -872415232, 1.5F);
         }
      }
   }

   public Blink() {
      super("Blink", IconsPngType.NETWORK);
      this.spawnFake = new BooleanSetting("Spawn Fake", "Shows a copy of you where the server still thinks you are.", true);
      this.booleanBase = new BooleanSetting("Auto Send", "Sends your held packets once Send Threshold is reached, then keeps blinking.", false);
      this.integerHelper = new IntegerSetting("Send Threshold", "How many packets Auto Send holds before sending them all. 0 = no limit.", 50, 0, 100);
      this.sendPacketsItems = new ArrayList<>();
      this.worldReference = new WeakReferenceHelper();
      this.addSettings(new ConfigSetting[]{this.breadcrumbs, this.spawnFake, this.booleanBase, this.integerHelper});
   }

   @Override
   protected void onDisable() {
      ConnectionTimeUtils.Outbound.handle(this);
      WorldExtractionEntity.updateInstance();
      this.sendPacketsItems.clear();
      this.entityPos = null;
      this.activeTickCount = 0;
      this.heldPacketTicks = 0;
      this.worldReference.updateWeakReference();
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null) {
         if (this.worldReference.canWeakReference(mc.world) && !mc.player.isDead()) {
            long time = ConnectionTimeUtils.getTime();
            if (time != this.lastTimeStampMs) {
               this.lastTimeStampMs = time;
               if (!this.booleanBase.isEnabled()) {
                  this.setEnabled(false);
               } else {
                  ConnectionTimeUtils.Outbound.handle(this);
                  this.updateYawPlayer();
               }
            } else if (!BlinkFlagUtils.canFlag(this.booleanBase.isEnabled(), this.integerHelper.getValue(), this.activeTickCount)
               && !BlinkFlagUtils.canFlag(this.yawPlayerValue, ConnectionTimeUtils.getTimeTimeMillis(), 10000L)) {
               if (!this.spawnFake.isEnabled()) {
                  WorldExtractionEntity.updateInstance();
               } else if (!WorldExtractionEntity.canInstanceWorld(mc.world)) {
                  this.updateYawEntity();
               }
            } else {
               ConnectionTimeUtils.Outbound.handle(this);
               this.updateYawPlayer();
            }
         } else {
            this.setEnabled(false);
         }
      }
   }

   private void updateYawPlayer() {
      this.activeTickCount = 0;
      this.heldPacketTicks = 0;
      this.yawPlayerValue = ConnectionTimeUtils.getTimeTimeMillis();
      this.entityPos = mc.player.getEntityPos();
      this.yaw = mc.player.getYaw();
      this.pitch = mc.player.getPitch();
      this.headYaw = mc.player.getHeadYaw();
      this.bodyYaw = mc.player.getBodyYaw();
      this.width = mc.player.getWidth();
      this.height = mc.player.getHeight();
      this.sendPacketsItems.clear();
      this.sendPacketsItems.add(this.entityPos);
      if (this.spawnFake.isEnabled()) {
         this.updateYawEntity();
      } else {
         WorldExtractionEntity.updateInstance();
      }
   }

   private void handleHeightWidth(Matrix4f matrix) {
      Vec3d entityPos = this.entityPos;
      float widthScaled = this.width / 2.0F;
      if (!VertexBuilderUtils.canVector(entityPos.x, entityPos.y + this.height / 2.0, entityPos.z, this.height)) {
         float heightWidthValue = VertexBuilderUtils.getAmountX(entityPos.x - widthScaled);
         float f2 = VertexBuilderUtils.getAmount(entityPos.y);
         float amountForDouble = VertexBuilderUtils.getAmountForDouble(entityPos.z - widthScaled);
         float amountX = VertexBuilderUtils.getAmountX(entityPos.x + widthScaled);
         float f5 = VertexBuilderUtils.getAmount(entityPos.y + this.height);
         float amountForDouble6 = VertexBuilderUtils.getAmountForDouble(entityPos.z + widthScaled);
         VertexBuilderUtils.handle(matrix, heightWidthValue, f2, amountForDouble, amountX, f5, amountForDouble6, 0.33333334F, 0.8F, 1.0F, 0.2F);
         VertexBuilderUtils.handle(matrix, heightWidthValue, f2, amountForDouble, amountX, f5, amountForDouble6, -866792193, 1.5F);
      }
   }

   @Override
   protected void onEnable() {
      if (mc.player != null && mc.world != null) {
         this.worldReference.handleWeakReference(mc.world);
         this.lastTimeStampMs = ConnectionTimeUtils.getTime();
         this.updateYawPlayer();
      } else {
         this.setEnabled(false);
      }
   }
}
