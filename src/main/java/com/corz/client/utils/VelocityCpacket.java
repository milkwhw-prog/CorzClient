package com.corz.client.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.ConnectionTimeUtils;

public final class VelocityCpacket {
   private final ConcurrentLinkedQueue<VelocityCpacket.Velocity> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
   private volatile int latestVelocityTick;
   private final AtomicInteger atomicInteger = new AtomicInteger();

   public int getAtomicConcurrent(Consumer<VelocityCpacket.Velocity> consumer, int atomicConcurrentLimit) {
      int atomicConcurrent;
      for (atomicConcurrent = 0; atomicConcurrent < atomicConcurrentLimit; atomicConcurrent++) {
         VelocityCpacket.Velocity concurrentlinkedqueue = this.concurrentLinkedQueue.poll();
         if (concurrentlinkedqueue == null) {
            break;
         }

         this.atomicInteger.decrementAndGet();
         if (consumer != null) {
            consumer.accept(concurrentlinkedqueue);
         }
      }

      return atomicConcurrent;
   }

   public void handlePackets(Packet<?> packet) {
      if (!(packet instanceof BundlePacket bundlePacket)) {
         this.handleAtomicInteger(packet);
      } else {
         for (Packet packet3 : (Iterable<net.minecraft.network.packet.Packet>) (bundlePacket.getPackets())) {
            this.handleAtomicInteger(packet3);
         }
      }
   }

   public void setValue(int value) {
      this.latestVelocityTick = value;
   }

   public void updateAtomicConcurrent() {
      while (this.concurrentLinkedQueue.poll() != null) {
         this.atomicInteger.decrementAndGet();
      }
   }

   private void handleAtomicInteger(Packet<?> packet) {
      if (packet instanceof EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacke) {
         int entityId = entityVelocityUpdateS2CPacke.getEntityId();
         int atomicIntegerValue = this.latestVelocityTick;
         if (atomicIntegerValue == Integer.MIN_VALUE || atomicIntegerValue == entityId) {
            Vec3d velocity = entityVelocityUpdateS2CPacke.getVelocity();
            if (velocity != null && Double.isFinite(velocity.x) && Double.isFinite(velocity.y) && Double.isFinite(velocity.z)) {
               while (this.atomicInteger.get() >= 256 && this.concurrentLinkedQueue.poll() != null) {
                  this.atomicInteger.decrementAndGet();
               }

               this.concurrentLinkedQueue.add(new VelocityCpacket.Velocity(entityId, velocity, ConnectionTimeUtils.getTimeTimeMillis()));
               this.atomicInteger.incrementAndGet();
            }
         }
      }
   }

   public VelocityCpacket() {
      this.latestVelocityTick = Integer.MIN_VALUE;
   }

   public record Velocity(int entityId, Vec3d velocity, long atMs) {
   }
}
