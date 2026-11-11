package com.corz.client.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.util.math.ChunkPos;
import com.corz.client.utils.ChunkLightSnapshot;
import com.corz.client.utils.LightDataClassifier;

public final class LightPacketCache {
   private final ConcurrentLinkedQueue<LightPacketCache.PendingLight> pendingQueue;
   public static final LightPacketCache INSTANCE = new LightPacketCache();
   private ClientWorld world;
   private final AtomicInteger pendingCount;
   public final Map<Long, ChunkLightSnapshot> sectionsCoordChunkLightSnaps = new ConcurrentHashMap<>();

   private LightPacketCache.PendingLight pollPending() {
      LightPacketCache.PendingLight concurrentlinkedqueue = this.pendingQueue.poll();
      if (concurrentlinkedqueue != null) {
         this.pendingCount.decrementAndGet();
      }

      return concurrentlinkedqueue;
   }

   private void enqueueLight(int atomicIntegerValue, int atomicIntegerValue2, LightData lightData) {
      if (lightData != null) {
         if (this.pendingCount.get() < 5000) {
            this.pendingCount.incrementAndGet();
            this.pendingQueue.offer(new LightPacketCache.PendingLight(atomicIntegerValue, atomicIntegerValue2, lightData, System.nanoTime()));
         }
      }
   }

   public void processPendingLight() {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientWorld world = instance.world;
      if (world != this.world) {
         this.sectionsCoordChunkLightSnaps.clear();
         this.pendingQueue.clear();
         this.pendingCount.set(0);
         this.world = world;
      }

      if (world != null) {
         int countVerticalSections = world.countVerticalSections();
         int bottomSectionCoord = world.getBottomSectionCoord();
         int sectionsCoordCounter = 0;
         long nanoTimeOffset = System.nanoTime() + 1000000L;

         LightPacketCache.PendingLight pendingLight;
         while ((pendingLight = this.pollPending()) != null) {
            this.applyPendingLight(pendingLight, countVerticalSections, bottomSectionCoord);
            sectionsCoordCounter++;
            if (sectionsCoordCounter >= 4096 || (sectionsCoordCounter & 15) == 0 && System.nanoTime() >= nanoTimeOffset) {
               break;
            }
         }

         if (this.sectionsCoordChunkLightSnaps.size() > 8000) {
            ClientPlayerEntity player = instance.player;
            if (player != null) {
               int sectionsCoordValue = player.getBlockX() >> 4;
               int sectionsCoordValue2 = player.getBlockZ() >> 4;
               this.sectionsCoordChunkLightSnaps.keySet().removeIf(key -> {
                  int var3x = ChunkPos.getPackedX(key);
                  int var4x = ChunkPos.getPackedZ(key);
                  return Math.abs(var3x - sectionsCoordValue) > 40 || Math.abs(var4x - sectionsCoordValue2) > 40;
               });
            }
         }
      }
   }

   private LightPacketCache() {
      this.pendingQueue = new ConcurrentLinkedQueue<>();
      this.pendingCount = new AtomicInteger();
      this.world = null;
   }

   private void applyPendingLight(LightPacketCache.PendingLight pendingLight, int chunkSectionsValue, int bottomSectionCoord) {
      long long2 = ChunkPos.toLong(pendingLight.chunkX(), pendingLight.chunkZ());
      ChunkLightSnapshot chunkLightSnapshot = this.sectionsCoordChunkLightSnaps.get(long2);
      if (chunkLightSnapshot == null || chunkLightSnapshot.verticalSections != chunkSectionsValue || chunkLightSnapshot.sectionCoord != bottomSectionCoord) {
         chunkLightSnapshot = new ChunkLightSnapshot(pendingLight.chunkX(), pendingLight.chunkZ(), chunkSectionsValue, bottomSectionCoord);
         this.sectionsCoordChunkLightSnaps.put(long2, chunkLightSnapshot);
      }

      try {
         LightDataClassifier.applyLightData(pendingLight.data(), chunkLightSnapshot, pendingLight.arrivalNanos());
      } catch (Throwable e) {
      }
   }

   public static void onLightPacket(Packet<?> packet) {
      if (packet instanceof LightUpdateS2CPacket lightUpdateS2CPacket) {
         INSTANCE.enqueueLight(lightUpdateS2CPacket.getChunkX(), lightUpdateS2CPacket.getChunkZ(), lightUpdateS2CPacket.getData());
      } else if (packet instanceof ChunkDataS2CPacket chunkDataS2CPacket) {
         INSTANCE.enqueueLight(chunkDataS2CPacket.getChunkX(), chunkDataS2CPacket.getChunkZ(), chunkDataS2CPacket.getLightData());
      }
   }

   private record PendingLight(int chunkX, int chunkZ, LightData data, long arrivalNanos) {
   }
}
