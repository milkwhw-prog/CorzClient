package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import com.corz.client.utils.AutoVanish;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class Particles extends Module {
   private final DoubleSetting doubleHelper;
   private final BooleanSetting booleanBase;
   private final Map<Integer, Integer> sparkRadiusMap;
   private static final double[] particlesTable = new double[10];
   private final IntegerSetting integerHelper;
   private final List<Particles.P> sparkRadiusItems;
   private static final double[] table = new double[10];
   private final BooleanSetting hideVanillaCrits;
   private long lastNanoTime;
   private final BooleanSetting totemParticles;
   private final BooleanSetting hitParticles = new BooleanSetting("Hit Particles", "Spark burst when a living entity is hit.", true);
   private final Queue<EntityStatusS2CPacket> queue;
   private final DoubleSetting lifetime;

   @Override
   public void onPacketReceive(AutoVanish autoVanish) {
      if (this.totemParticles.isEnabled()) {
         if (autoVanish.packet instanceof EntityStatusS2CPacket entityStatusS2CPacket && entityStatusS2CPacket.getStatus() == 35) {
            this.queue.add(entityStatusS2CPacket);
         }
      }
   }

   @Override
   public void onDisable() {
      this.sparkRadiusItems.clear();
      this.sparkRadiusMap.clear();
      this.queue.clear();
      this.lastNanoTime = 0L;
   }

   private static int getValue(int particlesValue, float particlesFactor) {
      int particlesValue2 = particlesValue >> 16 & 0xFF;
      int particlesValue3 = particlesValue >> 8 & 0xFF;
      int particlesValue4 = particlesValue & 0xFF;
      int round = Math.round(particlesValue2 + (255 - particlesValue2) * particlesFactor);
      int round6 = Math.round(particlesValue3 + (255 - particlesValue3) * particlesFactor);
      int round7 = Math.round(particlesValue4 + (255 - particlesValue4) * particlesFactor);
      return round << 16 | round6 << 8 | round7;
   }

   @Override
   public void onReset() {
      if (mc.world != null && mc.player != null) {
         EntityStatusS2CPacket entityStatusS2CPacket;
         while ((entityStatusS2CPacket = this.queue.poll()) != null) {
            if (this.totemParticles.isEnabled()) {
               try {
                  Entity entity = entityStatusS2CPacket.getEntity(mc.world);
                  if (entity != null) {
                     this.handleAmountSpark(entity.getX(), entity.getY() + entity.getHeight() * 0.6, entity.getZ(), this.integerHelper.getValue() * 2, true);
                  }
               } catch (Throwable e) {
               }
            }
         }

         var hashSet = new HashSet();
         if (this.hitParticles.isEnabled()) {
            double iIiLimit = 1024.0;

            for (Entity getEntitiesItem : mc.world.getEntities()) {
               if (getEntitiesItem instanceof LivingEntity livingEntity && getEntitiesItem != mc.player) {
                  int id = getEntitiesItem.getId();
                  hashSet.add(id);
                  int hurtTime = livingEntity.hurtTime;
                  Integer put = this.sparkRadiusMap.put(id, hurtTime);
                  int iIiValue = put == null ? 0 : put;
                  if (hurtTime == livingEntity.maxHurtTime && iIiValue != livingEntity.maxHurtTime && getEntitiesItem.squaredDistanceTo(mc.player) < iIiLimit) {
                     this.handleAmountSpark(getEntitiesItem.getX(), getEntitiesItem.getY() + getEntitiesItem.getHeight() * 0.5, getEntitiesItem.getZ(), this.integerHelper.getValue(), false);
                  }
               }
            }
         }

         this.sparkRadiusMap.keySet().retainAll(hashSet);
      }
   }

   private void handleAmounts(VertexConsumer vertices, Matrix4f matrix, Vec3d vec, double particlesValue, double particlesValue2, double particlesValue3, float particlesFactor, int valueForIntFloat) {
      double particlesFactor2 = vec.x - particlesValue;
      double particlesFactor3 = vec.y - particlesValue2;
      double particlesFactor4 = vec.z - particlesValue3;
      double distance = Math.sqrt(particlesFactor2 * particlesFactor2 + particlesFactor3 * particlesFactor3 + particlesFactor4 * particlesFactor4);
      if (!(distance < 1.0E-4)) {
         double particlesFactor5 = particlesFactor2 / distance;
         double particlesFactor6 = particlesFactor3 / distance;
         double particlesFactor7 = particlesFactor4 / distance;
         double particlesValue5 = particlesFactor7;
         double particlesFactor8 = -particlesFactor5;
         double sqrt2 = Math.sqrt(particlesFactor7 * particlesFactor7 + particlesFactor8 * particlesFactor8);
         if (sqrt2 < 1.0E-4) {
            particlesValue5 = 1.0;
            particlesFactor8 = 0.0;
            sqrt2 = 1.0;
         }

         double particlesFactor9 = particlesValue5 / sqrt2;
         double particlesFactor10 = particlesFactor8 / sqrt2;
         double particlesFactor11 = 0.0;
         double particlesFactor12 = particlesFactor6 * particlesFactor10 - particlesFactor7 * particlesFactor11;
         double particlesFactor13 = particlesFactor7 * particlesFactor9 - particlesFactor5 * particlesFactor10;
         double particlesFactor14 = particlesFactor5 * particlesFactor11 - particlesFactor6 * particlesFactor9;
         float pz2 = (float)particlesValue;
         float coreArgb2 = (float)particlesValue2;
         float vc2 = (float)particlesValue3;
         int particlesValue6 = -16777216;
         float particlesValue7 = 0.0F;
         float particlesValue8 = 0.0F;
         float particlesValue9 = 0.0F;

         for (int i = 0; i <= 9; i++) {
            double particlesFactor15 = particlesTable[i];
            double particlesFactor16 = table[i];
            float particlesValue10 = (float)((particlesFactor9 * particlesFactor15 + particlesFactor12 * particlesFactor16) * particlesFactor);
            float particlesValue11 = (float)((particlesFactor11 * particlesFactor15 + particlesFactor13 * particlesFactor16) * particlesFactor);
            float particlesValue12 = (float)((particlesFactor10 * particlesFactor15 + particlesFactor14 * particlesFactor16) * particlesFactor);
            float particlesValue13 = pz2 + particlesValue10;
            float particlesValue14 = coreArgb2 + particlesValue11;
            float particlesValue15 = vc2 + particlesValue12;
            if (i > 0) {
               VertexBuilderUtils.handleVertexColor(vertices, matrix, pz2, coreArgb2, vc2, valueForIntFloat, particlesValue7, particlesValue8, particlesValue9, -16777216, particlesValue13, particlesValue14, particlesValue15, -16777216);
            }

            particlesValue7 = particlesValue13;
            particlesValue8 = particlesValue14;
            particlesValue9 = particlesValue15;
         }
      }
   }

   public boolean isBase() {
      return this.isEnabled() && this.hitParticles.isEnabled() && this.hideVanillaCrits.isEnabled();
   }

   private static int getMin(int particlesBound) {
      return particlesBound < 0 ? 0 : Math.min(particlesBound, 255);
   }

   public Particles() {
      super("Particles", IconsPngType.VISUAL);
      this.totemParticles = new BooleanSetting("Totem Particles", "Golden burst when a totem pops (any entity).", true);
      this.hideVanillaCrits = new BooleanSetting("Hide Vanilla Crits", "Remove Minecraft's own crit / enchanted-hit particles so hits aren't doubled up.", true);
      this.booleanBase = new BooleanSetting("Through Walls", "Draw the sparks over terrain (always visible).", true);
      this.integerHelper = new IntegerSetting("Amount", "Sparks per hit burst (totem pops use double).", 14, 4, 40, 1);
      this.lifetime = new DoubleSetting("Lifetime", "How long a spark lives, in seconds.", 0.8, 0.3, 2.0, 0.1);
      this.doubleHelper = new DoubleSetting("Size", "Spark size (glow radius), in blocks.", 0.1, 0.04, 0.3, 0.01);
      this.sparkRadiusItems = new ArrayList<>();
      this.sparkRadiusMap = new HashMap<>();
      this.queue = new ConcurrentLinkedQueue<>();
      this.lastNanoTime = 0L;
      this.addSettings(
         new ConfigSetting[]{
            this.hitParticles, this.totemParticles, this.hideVanillaCrits, this.booleanBase, this.integerHelper, this.lifetime, this.doubleHelper
         }
      );
   }

   static {
      for (int i = 0; i <= 9; i++) {
         double angle = (Math.PI * 2) * i / 9.0;
         particlesTable[i] = Math.cos(angle);
         table[i] = Math.sin(angle);
      }
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      if (!this.sparkRadiusItems.isEmpty() && mc.world != null) {
         Camera camera = mc.gameRenderer.getCamera();
         if (camera != null) {
            Vec3d cameraPos = camera.getCameraPos();
            long nanoTime = System.nanoTime();
            float particlesFactor = this.lastNanoTime == 0L ? 0.0F : (float)Math.min(0.1, (nanoTime - this.lastNanoTime) / 1.0E9);
            this.lastNanoTime = nanoTime;
            queueCommand.matrices.push();
            queueCommand.matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            Matrix4f positionMatrix = queueCommand.matrices.peek().getPositionMatrix();
            VertexConsumer vertexConsumer = this.booleanBase.isEnabled() ? VertexBuilderUtils.getBuffer4() : VertexBuilderUtils.getBuffer5();
            float exp = (float)Math.exp(-1.8 * particlesFactor);
            var iterator = this.sparkRadiusItems.iterator();

            while (iterator.hasNext()) {
               Particles.P p = (Particles.P)iterator.next();
               if (particlesFactor > 0.0F) {
                  p.velocityX *= exp;
                  p.sparkAmount *= exp;
                  p.velocityY = p.velocityY * exp - 7.0F * particlesFactor;
                  p.x = p.x + p.velocityX * particlesFactor;
                  p.posY = p.posY + p.velocityY * particlesFactor;
                  p.z = p.z + p.sparkAmount * particlesFactor;
                  p.sparkCount += particlesFactor;
               }

               if (p.sparkCount >= p.maxLifeTicks) {
                  iterator.remove();
               } else {
                  float particlesValue = p.sparkCount / p.maxLifeTicks;
                  float particlesFactor2 = 1.0F - particlesValue;
                  float particlesValue2 = particlesFactor2 * particlesFactor2;
                  if (!(particlesValue2 <= 0.012F)) {
                     int valueForIntFloat = getValueForIntFloat(getValue(p.baseColor, 0.35F), particlesValue2);
                     float particlesValue4 = p.baseSize * (0.6F + 0.4F * (1.0F - particlesValue));
                     this.handleAmounts(vertexConsumer, positionMatrix, cameraPos, p.x, p.posY, p.z, particlesValue4, valueForIntFloat);
                  }
               }
            }

            queueCommand.matrices.pop();
         }
      }
   }

   private static int getValueForIntFloat(int particlesValue, float particlesFactor) {
      int min2 = getMin(Math.round((particlesValue >> 16 & 0xFF) * particlesFactor));
      int min3 = getMin(Math.round((particlesValue >> 8 & 0xFF) * particlesFactor));
      int min4 = getMin(Math.round((particlesValue & 0xFF) * particlesFactor));
      return 0xFF000000 | min2 << 16 | min3 << 8 | min4;
   }

   private void handleAmountSpark(double sparkValue, double amount, double sparkValue2, int sparkLimit, boolean sparkFlag) {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      int sparkValue3 = 0xFF000000 | MatrixEspUtils.releaseRightValue & 16777215;

      for (int i = 0; i < sparkLimit && this.sparkRadiusItems.size() < 700; i++) {
         Particles.P p = new Particles.P();
         p.x = sparkValue;
         p.posY = amount;
         p.z = sparkValue2;
         double angle = current.nextDouble() * Math.PI * 2.0;
         double sparkFactor = 2.0 * current.nextDouble() - 1.0;
         double sqrt = Math.sqrt(Math.max(0.0, 1.0 - sparkFactor * sparkFactor));
         double sparkFactor2 = sqrt * Math.cos(angle);
         double sparkFactor3 = sqrt * Math.sin(angle);
         if (sparkFlag) {
            sparkFactor = Math.abs(sparkFactor) * 0.6 + 0.5;
         }

         double sparkFactor4 = sparkFlag ? 2.2 + current.nextDouble() * 3.2 : 2.8 + current.nextDouble() * 2.6;
         p.velocityX = sparkFactor2 * sparkFactor4;
         p.velocityY = sparkFactor * sparkFactor4 + (sparkFlag ? 1.6 : 0.0);
         p.sparkAmount = sparkFactor3 * sparkFactor4;
         p.sparkCount = 0.0F;
         p.maxLifeTicks = (float)this.lifetime.getValue() * (sparkFlag ? 1.25F : 1.0F) * (0.75F + current.nextFloat() * 0.5F);
         p.baseSize = (float)this.doubleHelper.getValue() * (0.7F + current.nextFloat() * 0.6F);
         if (sparkFlag) {
            p.baseColor = current.nextFloat() < 0.3F ? -12130194 : -403876;
         } else {
            p.baseColor = current.nextFloat() < 0.4F ? -1 : sparkValue3;
         }

         this.sparkRadiusItems.add(p);
      }
   }

   private static final class P {
      float baseSize;
      double sparkAmount;
      double z;
      float sparkCount;
      double x;
      double posY;
      float maxLifeTicks;
      double velocityY;
      int baseColor;
      double velocityX;
   }
}
