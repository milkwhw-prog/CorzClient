package com.corz.client.categories.network;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.RangeSetting;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.mixins.AccessorEntityS2CPacket;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.DisconnectMultiplayerHandler;
import com.corz.client.utils.FrameArrivalData;
import com.corz.client.utils.WeakReferenceHelper;
import com.corz.client.utils.BackTrackUtils;
import com.corz.client.utils.LagFakeUtilsAmount;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import com.corz.client.utils.IconsPngType;

public class BackTrack extends Module implements DisconnectMultiplayerHandler {
   private static final Options INVISIBLES_ANIMALS = Options.INSTANCE.getAnimalsTeammates(true).getInvisiblesAnimalsOptions(true);
   private long lastTrackedTimeMs;
   private float width;
   private final RangeSetting latency = new RangeSetting(
      "Latency",
      "How far back in time you can still hit someone running away, in milliseconds. Higher = more reach-back but more lag.",
      50.0,
      100.0,
      0.0,
      500.0,
      5.0
   );
   private volatile BackTrack.View view;
   private volatile long knockbackWaterValue;
   private int serverBoxValue;
   private float height;
   private final AtomicReference<FrameArrivalData> atomicReference;
   private final WeakReferenceHelper iIiWeakReferenceHelper;
   private long entityWorldValue;
   private int trackedEntityId;
   private final ColorSetting colorBase;
   private Vec3d vec3dVecSubtract;
   private Vec3d prevPos;
   private volatile boolean referenceActive;
   private final BooleanSetting booleanBase = new BooleanSetting("Render Server Pos", "Draws a box where the server really has your target right now.", true);
   private volatile long referenceCount;
   private int interpolationStepsLeft;
   private long referenceValue;
   private Vec3d add;
   private volatile boolean iIiActive;
   private volatile Packet<?> packet;

   private void handlePositionPos(BackTrack.Fold fold, Packet<?> packet, BackTrack.View view, long positionPosValue) {
      int id = fold.positionPosData.id();
      if (packet instanceof EntityS2CPacket entityS2CPacket) {
         if (entityS2CPacket.isPositionChanged() && entityS2CPacket instanceof AccessorEntityS2CPacket accessorEntityS2CPacket && accessorEntityS2CPacket.corzGetEntityId() == id) {
            fold.positionPosData = fold.positionPosData.getBase(entityS2CPacket.getDeltaX(), entityS2CPacket.getDeltaY(), entityS2CPacket.getDeltaZ(), positionPosValue);
         }
      } else if (packet instanceof EntityPositionSyncS2CPacket entityPositionSyncS2CPacket) {
         if (entityPositionSyncS2CPacket.id() == id) {
            fold.positionPosData = fold.positionPosData.getPositionPosData(entityPositionSyncS2CPacket.values().position(), positionPosValue);
         }
      } else if (packet instanceof EntityPositionS2CPacket entityPositionS2CPacket) {
         if (entityPositionS2CPacket.entityId() == id) {
            fold.positionPosData = fold.positionPosData.getBase(getRelativesZero(fold.positionPosData.pos(), entityPositionS2CPacket), positionPosValue);
         }
      } else if (packet instanceof EntitySpawnS2CPacket entitySpawnS2CPacket) {
         if (entitySpawnS2CPacket.getEntityId() == id) {
            fold.positionPosData = fold.positionPosData.getPositionPosData(new Vec3d(entitySpawnS2CPacket.getX(), entitySpawnS2CPacket.getY(), entitySpawnS2CPacket.getZ()), positionPosValue);
         }
      } else if (packet instanceof EntitiesDestroyS2CPacket entitiesDestroyS2CPacket) {
         if (entitiesDestroyS2CPacket.getEntityIds().contains(id)) {
            fold.iiIActive = true;
         }
      } else if (packet instanceof EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacke) {
         if (entityVelocityUpdateS2CPacke.getEntityId() == view.selfId()) {
            fold.positionPosActive = true;
         }
      } else if (packet instanceof ExplosionS2CPacket explosionS2CPacket && explosionS2CPacket.playerKnockback().isPresent()) {
         fold.positionPosActive = true;
      }
   }

   @Override
   public long getKnockbackWater(Packet<?> packet2, boolean knockbackWaterFlag, long knockbackWaterLimit) {
      if (packet2 != null && packet2 == this.packet) {
         this.packet = null;
         BackTrack.View view = this.view;
         if (view == null) {
            return 0L;
         } else {
            long knockbackWaterValue = BackTrackUtils.getTime(view.minLatency(), view.maxLatency(), ThreadLocalRandom.current().nextDouble());
            long time = BackTrackUtils.getTime(knockbackWaterLimit, knockbackWaterValue, this.knockbackWaterValue);
            if (time <= knockbackWaterLimit) {
               return 0L;
            } else {
               this.knockbackWaterValue = time;
               return time;
            }
         }
      } else {
         return 0L;
      }
   }

   private static void handleVertexColor(
      VertexConsumer vertices,
      Matrix4f matrix,
      float vertexColorValue,
      float vertexColorValue2,
      float vertexColorValue3,
      float vertexColorValue4,
      float vertexColorValue5,
      float vertexColorValue6,
      float vertexColorValue7,
      float vertexColorValue8,
      float amountForDouble,
      float backTrackValue,
      float f2,
      float vertexColorValue12,
      int intValue
   ) {
      vertices.vertex(matrix, vertexColorValue, vertexColorValue2, vertexColorValue3).color(intValue);
      vertices.vertex(matrix, vertexColorValue4, vertexColorValue5, vertexColorValue6).color(intValue);
      vertices.vertex(matrix, vertexColorValue7, vertexColorValue8, amountForDouble).color(intValue);
      vertices.vertex(matrix, backTrackValue, f2, vertexColorValue12).color(intValue);
   }

   private void handleTimeReference(LivingEntity entity, long time) {
      if (this.trackedEntityId != -1) {
         this.update();
      }

      this.trackedEntityId = entity.getId();
      this.lastTrackedTimeMs = time;
      this.referenceActive = false;
      this.referenceValue = Long.MAX_VALUE;
      this.atomicReference.set(FrameArrivalData.getPositionPosData(this.trackedEntityId, this.referenceCount));
   }

   @Override
   protected void onDisable() {
      this.update();
      this.serverBoxValue = -1;
      this.iIiWeakReferenceHelper.updateWeakReference();
   }

   private static double getEyeHalf(BackTrack.View view, Vec3d vec) {
      return LagFakeUtilsAmount.getAmount(view.eyeX(), view.eyeY(), view.eyeZ(), vec.x, vec.y, vec.z, view.halfWidth(), view.height());
   }

   private static void handle(Matrix4f matrix, float backTrackValue, float f2, float amountForDouble, float vertexColorValue, float f5, float backTrackValue6, int intValue) {
      VertexConsumer buffer = VertexBuilderUtils.getBuffer();
      handleVertexColor(buffer, matrix, backTrackValue, f2, amountForDouble, vertexColorValue, f2, amountForDouble, vertexColorValue, f2, backTrackValue6, backTrackValue, f2, backTrackValue6, intValue);
      handleVertexColor(buffer, matrix, backTrackValue, f5, amountForDouble, backTrackValue, f5, backTrackValue6, vertexColorValue, f5, backTrackValue6, vertexColorValue, f5, amountForDouble, intValue);
      handleVertexColor(buffer, matrix, backTrackValue, f2, amountForDouble, backTrackValue, f5, amountForDouble, vertexColorValue, f5, amountForDouble, vertexColorValue, f2, amountForDouble, intValue);
      handleVertexColor(buffer, matrix, backTrackValue, f2, backTrackValue6, vertexColorValue, f2, backTrackValue6, vertexColorValue, f5, backTrackValue6, backTrackValue, f5, backTrackValue6, intValue);
      handleVertexColor(buffer, matrix, backTrackValue, f2, amountForDouble, backTrackValue, f2, backTrackValue6, backTrackValue, f5, backTrackValue6, backTrackValue, f5, amountForDouble, intValue);
      handleVertexColor(buffer, matrix, vertexColorValue, f2, amountForDouble, vertexColorValue, f5, amountForDouble, vertexColorValue, f5, backTrackValue6, vertexColorValue, f2, backTrackValue6, intValue);
   }

   private void handleEntityWorld(int entityWorldValue, long time) {
      if (mc.world.getEntityById(entityWorldValue) instanceof LivingEntity livingEntity && EntityPlayerUtilsLiving.canSpectatorDead(livingEntity, INVISIBLES_ANIMALS)) {
         if (!(EntityPlayerUtilsLiving.getMinMax(livingEntity) > 6.0)) {
            if (entityWorldValue != this.trackedEntityId) {
               this.handleTimeReference(livingEntity, time);
            }

            this.entityWorldValue = time;
         }
      }
   }

   private static int getValue(int backTrackValue) {
      int backTrackValue2 = backTrackValue >>> 24;
      int backTrackValue3 = (int)((backTrackValue >> 16 & 0xFF) * 0.49F);
      int backTrackValue4 = (int)((backTrackValue >> 8 & 0xFF) * 0.49F);
      int backTrackValue5 = (int)((backTrackValue & 0xFF) * 0.49F);
      return backTrackValue2 << 24 | backTrackValue3 << 16 | backTrackValue4 << 8 | backTrackValue5;
   }

   private void updateVecPacket() {
      this.trackedEntityId = -1;
      this.view = null;
      this.atomicReference.set(null);
      this.packet = null;
      this.knockbackWaterValue = 0L;
      this.referenceActive = false;
      this.prevPos = null;
      this.add = null;
      this.vec3dVecSubtract = null;
      this.interpolationStepsLeft = 0;
   }

   private static Vec3d getRelativesZero(Vec3d vec, EntityPositionS2CPacket entityPositionS2CPacket) {
      var relatives = entityPositionS2CPacket.relatives();
      boolean relativesZeroFlag = relatives.contains(PositionFlag.X) || relatives.contains(PositionFlag.Y) || relatives.contains(PositionFlag.Z);
      if (vec == null && relativesZeroFlag) {
         return null;
      } else {
         EntityPosition entityPosition = new EntityPosition(vec != null ? vec : Vec3d.ZERO, Vec3d.ZERO, 0.0F, 0.0F);
         return EntityPosition.apply(entityPosition, entityPositionS2CPacket.change(), relatives).position();
      }
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      var packet = autoVanish.packet;
      if (!(packet instanceof GameJoinS2CPacket) && !(packet instanceof PlayerRespawnS2CPacket)) {
         BackTrack.View view = this.view;
         if (view != null && ConnectionTimeUtils.canPackets(packet, BackTrack::canFlag)) {
            long positionPosValue = this.referenceCount;
            BackTrack.Fold fold = new BackTrack.Fold();

            while (true) {
               FrameArrivalData positionPosData = this.atomicReference.get();
               if (positionPosData != null && positionPosData.id() == view.targetId()) {
                  fold.positionPosData = positionPosData;
                  fold.positionPosActive = false;
                  fold.iiIActive = false;
                  if (packet instanceof BundlePacket bundlePacket) {
                     for (Packet packet2 : (Iterable<net.minecraft.network.packet.Packet>) (bundlePacket.getPackets())) {
                        this.handlePositionPos(fold, packet2, view, positionPosValue);
                     }
                  } else {
                     this.handlePositionPos(fold, packet, view, positionPosValue);
                  }

                  if (fold.positionPosData != positionPosData && !this.atomicReference.compareAndSet(positionPosData, fold.positionPosData)) {
                     continue;
                  }

                  if (fold.iiIActive) {
                     this.referenceActive = true;
                     this.packet = null;
                     this.updateTime();
                     return;
                  }

                  if (fold.positionPosActive) {
                     this.packet = null;
                     this.updateTime();
                     return;
                  }

                  FrameArrivalData positionPosData2 = fold.positionPosData;
                  if (positionPosData2.isBasePos() && positionPosData.pos() != null && !positionPosData2.pos().equals(positionPosData.pos())) {
                     double eyeHalf = getEyeHalf(view, positionPosData.pos());
                     double backTrackLimit = getEyeHalf(view, positionPosData2.pos());
                     if (backTrackLimit <= 6.0 && BackTrackUtils.canFlag(eyeHalf, backTrackLimit)) {
                        this.packet = packet;
                        return;
                     }

                     this.packet = null;
                     this.updateTime();
                     return;
                  }

                  return;
               }

               return;
            }
         }
      } else {
         this.iIiActive = true;
         this.packet = null;
      }
   }

   private void handlePositionTracked(LivingEntity entity, boolean positionTrackedFlag, long positionTrackedValue) {
      FrameArrivalData atomicreference = this.atomicReference.get();
      if (atomicreference != null && atomicreference.id() == entity.getId()) {
         boolean canFlag2 = BackTrackUtils.canFlag(positionTrackedFlag, this.referenceValue, atomicreference.arrivalFrame(), this.referenceCount);
         if (canFlag2 || !atomicreference.isBasePos() && positionTrackedValue - this.lastTrackedTimeMs >= 1500L) {
            Vec3d pos = entity.getTrackedPosition().getPos();
            Vec3d position = EntityPosition.fromEntity(entity).position();
            if (!atomicreference.isBasePos() || !pos.equals(atomicreference.base()) || !position.equals(atomicreference.pos())) {
               this.atomicReference.compareAndSet(atomicreference, atomicreference.getArrivalFrame(pos, position));
            }
         }
      }
   }

   private boolean isTime() {
      boolean backTrackFlag = ConnectionTimeUtils.Inbound.isFlag();
      if (backTrackFlag) {
         this.referenceValue = Long.MAX_VALUE;
      } else if (this.referenceValue == Long.MAX_VALUE) {
         this.referenceValue = this.referenceCount;
      }

      return backTrackFlag;
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null) {
         if (!this.iIiWeakReferenceHelper.canWeakReference(mc.world) || this.iIiActive) {
            this.iIiActive = false;
            this.iIiWeakReferenceHelper.handleWeakReference(mc.world);
            this.update();
         }

         boolean iIiFlag = this.isTime();
         long iIiValue = ConnectionTimeUtils.getTimeTimeMillis();
         int serverBoxValue = this.serverBoxValue;
         this.serverBoxValue = -1;
         if (serverBoxValue != -1) {
            this.handleEntityWorld(serverBoxValue, iIiValue);
         }

         if (this.trackedEntityId != -1) {
            if (!this.referenceActive
               && mc.world.getEntityById(this.trackedEntityId) instanceof LivingEntity livingEntity
               && EntityPlayerUtilsLiving.canSpectatorDead(livingEntity, INVISIBLES_ANIMALS)
               && !mc.player.isDead()
               && iIiValue - this.entityWorldValue <= 3000L) {
               this.handlePositionTracked(livingEntity, iIiFlag, iIiValue);
               FrameArrivalData atomicreference = this.atomicReference.get();
               Vec3d vec3d = atomicreference != null && atomicreference.id() == this.trackedEntityId && atomicreference.isBasePos() ? atomicreference.pos() : null;
               Vec3d vec3d2 = vec3d != null ? vec3d : livingEntity.getEntityPos();
               Vec3d eyePos = mc.player.getEyePos();
               double widthScaled = livingEntity.getWidth() / 2.0;
               double height = livingEntity.getHeight();
               if (LagFakeUtilsAmount.getAmount(eyePos.x, eyePos.y, eyePos.z, vec3d2.x, vec3d2.y, vec3d2.z, widthScaled, height) > 6.0) {
                  this.update();
               } else {
                  this.width = livingEntity.getWidth();
                  this.height = livingEntity.getHeight();
                  this.view = new BackTrack.View(
                     this.trackedEntityId, mc.player.getId(), eyePos.x, eyePos.y, eyePos.z, widthScaled, height, this.latency.getMin(), this.latency.getMax()
                  );
                  this.handleVecSubtract(vec3d);
               }
            } else {
               this.update();
            }
         }
      }
   }

   private void update() {
      this.updateVecPacket();
      ConnectionTimeUtils.Inbound.handle(this);
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (mc.isOnThread()) {
         if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke && PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
            this.serverBoxValue = PlayerCrystalUtils.getEntityId(playerInteractEntityC2SPacke);
         }
      }
   }

   @Override
   protected void onEnable() {
      this.updateVecPacket();
      this.serverBoxValue = -1;
      this.iIiActive = false;
      this.referenceValue = Long.MAX_VALUE;
      this.iIiWeakReferenceHelper.handleWeakReference(mc.world);
   }

   private void handleVecSubtract(Vec3d vec3d) {
      if (vec3d != null) {
         if (this.add == null) {
            this.prevPos = vec3d;
            this.add = vec3d;
            this.vec3dVecSubtract = vec3d;
            this.interpolationStepsLeft = 0;
         } else {
            if (!vec3d.equals(this.vec3dVecSubtract)) {
               this.vec3dVecSubtract = vec3d;
               this.interpolationStepsLeft = 3;
            }

            this.prevPos = this.add;
            if (this.interpolationStepsLeft > 0) {
               this.add = this.add.add(this.vec3dVecSubtract.subtract(this.add).multiply(1.0 / this.interpolationStepsLeft));
               this.interpolationStepsLeft--;
            }
         }
      }
   }

   public BackTrack() {
      super("Back Track", IconsPngType.NETWORK);
      this.colorBase = new ColorSetting("Color", "Colour of the server position box.", 1678083689);
      this.atomicReference = new AtomicReference<>();
      this.iIiWeakReferenceHelper = new WeakReferenceHelper();
      this.serverBoxValue = -1;
      this.trackedEntityId = -1;
      this.referenceValue = Long.MAX_VALUE;
      this.addSettings(new ConfigSetting[]{this.latency, this.booleanBase, this.colorBase});
   }

   private static boolean canFlag(Packet<?> packet) {
      return packet instanceof EntityS2CPacket
         || packet instanceof EntityPositionSyncS2CPacket
         || packet instanceof EntityPositionS2CPacket
         || packet instanceof EntitySpawnS2CPacket
         || packet instanceof EntitiesDestroyS2CPacket
         || packet instanceof EntityVelocityUpdateS2CPacket
         || packet instanceof ExplosionS2CPacket;
   }

   private void updateTime() {
      this.knockbackWaterValue = 0L;
      if (ConnectionTimeUtils.Inbound.canFlag(this)) {
         ConnectionTimeUtils.Inbound.handle(this);
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      this.isTime();
      this.referenceCount++;
      if (this.booleanBase.isEnabled() && this.trackedEntityId != -1 && this.add != null && this.prevPos != null) {
         float tickProgress = queueCommand.tickProgress;
         double lerp = MathHelper.lerp((double)tickProgress, this.prevPos.x, this.add.x);
         double lerp2 = MathHelper.lerp((double)tickProgress, this.prevPos.y, this.add.y);
         double destinationZ = MathHelper.lerp((double)tickProgress, this.prevPos.z, this.add.z);
         float widthScaled = this.width / 2.0F;
         if (!VertexBuilderUtils.canVector(lerp, lerp2 + this.height / 2.0, destinationZ, this.height)) {
            Matrix4f matrix = VertexBuilderUtils.getMatrix();
            float backTrackValue = VertexBuilderUtils.getAmountX(lerp - widthScaled);
            float f2 = VertexBuilderUtils.getAmount(lerp2 + 0.01);
            float amountForDouble = VertexBuilderUtils.getAmountForDouble(destinationZ - widthScaled);
            float amountX = VertexBuilderUtils.getAmountX(lerp + widthScaled);
            float f5 = VertexBuilderUtils.getAmount(lerp2 + this.height);
            float amountForDouble6 = VertexBuilderUtils.getAmountForDouble(destinationZ + widthScaled);
            int intValue = this.colorBase.getColor();
            handle(matrix, backTrackValue, f2, amountForDouble, amountX, f5, amountForDouble6, intValue);
            VertexBuilderUtils.handle(matrix, backTrackValue, f2, amountForDouble, amountX, f5, amountForDouble6, getValue(intValue), 1.5F);
         }
      }
   }

   private static final class Fold {
      boolean positionPosActive;
      boolean iiIActive;
      FrameArrivalData positionPosData;
   }

   private record View(int targetId, int selfId, double eyeX, double eyeY, double eyeZ, double halfWidth, double height, double minLatency, double maxLatency) {
   }
}
