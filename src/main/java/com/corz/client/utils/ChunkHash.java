package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.ChunkScanScheduler;
import com.corz.client.utils.HashOpenAlloc;
import com.corz.client.utils.FluidStateHelper;

final class ChunkHash {
   final HashOpenAlloc hashOpen;
   private ClientWorld world;
   final Long2ObjectOpenHashMap<ChunkHash.Column> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
   private final ChunkScanScheduler hashChunk = new ChunkScanScheduler();
   private int viewRadiusChunks;
   private int updateCounter;
   private final FluidStateHelper fluidStateHelper = new FluidStateHelper();
   private final LongArrayList longArrayList = new LongArrayList();

   private boolean canVertexsPacked(WorldChunk worldChunk, long vertexsPackedValue) {
      if (this.world == null) {
         return false;
      } else {
         ChunkHash.Column long2objectopenhashmap = (ChunkHash.Column)this.long2ObjectOpenHashMap.get(vertexsPackedValue);
         boolean noVar = long2objectopenhashmap == null;
         if (noVar) {
            long2objectopenhashmap = new ChunkHash.Column(vertexsPackedValue);
            this.long2ObjectOpenHashMap.put(vertexsPackedValue, long2objectopenhashmap);
         }

         int vertexsPackedSize = worldChunk.getSectionArray().length;
         if (long2objectopenhashmap.vertexs.length != vertexsPackedSize) {
            long2objectopenhashmap.vertexs = new FluidStateHelper.Result[vertexsPackedSize];
            long2objectopenhashmap.sectionMask = -1L;
         }

         long vertexsPackedValue2 = long2objectopenhashmap.sectionMask == 0L ? -1L : long2objectopenhashmap.sectionMask;
         long2objectopenhashmap.sectionMask = 0L;
         int vertexsPackedValue3 = (long2objectopenhashmap.packedX >> 2) * 64;
         int vertexsPackedValue4 = (long2objectopenhashmap.packedZ >> 2) * 64;
         boolean vertexsPackedFlag = false;
         boolean vertexsPacked = true;

         try {
            for (int i = 0; i < vertexsPackedSize; i++) {
               if (vertexsPackedSize > 64 || vertexsPackedValue2 == -1L || (vertexsPackedValue2 & 1L << i) != 0L) {
                  FluidStateHelper.Result result = long2objectopenhashmap.vertexs[i];
                  FluidStateHelper.Result bufferStart = this.fluidStateHelper.getBufferStart(this.world, worldChunk, i, vertexsPackedValue3, vertexsPackedValue4);
                  long2objectopenhashmap.vertexs[i] = bufferStart;
                  if (result != null || bufferStart != null) {
                     vertexsPackedFlag = true;
                  }
               }
            }
         } catch (Throwable e) {
            vertexsPacked = false;
         }

         if (vertexsPackedFlag) {
            this.hashOpen.handleHashOpen(getTime(long2objectopenhashmap.packedX, long2objectopenhashmap.packedZ));
         }

         return vertexsPacked;
      }
   }

   void updateFluidState() {
      this.updateHashOpen();
      this.fluidStateHelper.updateBufferAllocator();
   }

   void updateHashOpen() {
      if (!this.long2ObjectOpenHashMap.isEmpty()) {
         this.long2ObjectOpenHashMap.clear();
      }

      this.hashChunk.clearScannedChunks();
      this.hashOpen.updateClearHash();
      this.world = null;
   }

   int getMax() {
      return this.viewRadiusChunks;
   }

   private void handlePackedArray(int packedArrayValue, int packedArrayValue2) {
      int nowMsOffset = this.viewRadiusChunks + 2;
      this.longArrayList.clear();
      ObjectIterator fastIterator = this.long2ObjectOpenHashMap.long2ObjectEntrySet().fastIterator();

      while (fastIterator.hasNext()) {
         ChunkHash.Column column = (ChunkHash.Column)((Entry)fastIterator.next()).getValue();
         if (Math.abs(column.packedX - packedArrayValue) > nowMsOffset || Math.abs(column.packedZ - packedArrayValue2) > nowMsOffset) {
            this.longArrayList.add(column.packedArrayValue);
         }
      }

      for (int i = 0; i < this.longArrayList.size(); i++) {
         long long2 = this.longArrayList.getLong(i);
         ChunkHash.Column long2objectopenhashmap = (ChunkHash.Column)this.long2ObjectOpenHashMap.remove(long2);
         if (long2objectopenhashmap != null) {
            this.hashOpen.handleHashOpen(getTime(long2objectopenhashmap.packedX, long2objectopenhashmap.packedZ));
         }
      }

      this.hashChunk.removeFarChunks(packedArrayValue, packedArrayValue2, nowMsOffset);
   }

   void handleChunkWorld(MinecraftClient client, int chunkWorldLimit) {
      if (this.world != null && client.player != null && client.world == this.world) {
         ChunkPos chunkPos = client.player.getChunkPos();
         int chunkWorldCounter = 0;

         for (int i = 0; i <= 6 && chunkWorldCounter < chunkWorldLimit; i++) {
            for (int j = -i; j <= i && chunkWorldCounter < chunkWorldLimit; j++) {
               for (int k = -i; k <= i && chunkWorldCounter < chunkWorldLimit; k++) {
                  if (Math.max(Math.abs(j), Math.abs(k)) == i) {
                     long long2 = ChunkPos.toLong(chunkPos.x + j, chunkPos.z + k);
                     ChunkHash.Column long2objectopenhashmap = (ChunkHash.Column)this.long2ObjectOpenHashMap.get(long2);
                     if (long2objectopenhashmap == null || long2objectopenhashmap.sectionMask != 0L) {
                        WorldChunk worldChunk = this.world.getChunkManager().getWorldChunk(chunkPos.x + j, chunkPos.z + k);
                        if (worldChunk != null) {
                           if (this.canVertexsPacked(worldChunk, long2) && long2objectopenhashmap == null) {
                              this.hashChunk.markChunkScanned(long2);
                           }

                           chunkWorldCounter++;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   void handleHashOpen(int hashOpenValue, int hashOpenValue2) {
      long long2 = ChunkPos.toLong(hashOpenValue, hashOpenValue2);
      if (this.long2ObjectOpenHashMap.remove(long2) != null) {
         this.hashOpen.handleHashOpen(getTime(hashOpenValue, hashOpenValue2));
      }

      this.hashChunk.forgetChunk(long2);
   }

   void handleHashChunk(int hashChunkValue, int hashChunkValue2) {
      if (this.world != null) {
         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               if ((i | j) != 0) {
                  long long2 = ChunkPos.toLong(hashChunkValue + i, hashChunkValue2 + j);
                  ChunkHash.Column long2objectopenhashmap = (ChunkHash.Column)this.long2ObjectOpenHashMap.get(long2);
                  if (long2objectopenhashmap != null) {
                     long2objectopenhashmap.sectionMask = -1L;
                     this.hashChunk.forgetChunk(long2);
                  }
               }
            }
         }
      }
   }

   ChunkHash(HashOpenAlloc hashOpen) {
      this.viewRadiusChunks = 8;
      this.hashOpen = hashOpen;
   }

   void handleCoordSection(int coordSectionValue, int coordSectionValue2, int coordSectionValue3) {
      if (this.world != null) {
         long long2 = ChunkPos.toLong(coordSectionValue, coordSectionValue3);
         ChunkHash.Column long2objectopenhashmap = (ChunkHash.Column)this.long2ObjectOpenHashMap.get(long2);
         if (long2objectopenhashmap != null) {
            int coordSectionLimit = coordSectionValue2 - this.world.getBottomSectionCoord();
            if (coordSectionLimit >= 0 && coordSectionLimit < 64 && long2objectopenhashmap.vertexs.length <= 64) {
               long2objectopenhashmap.sectionMask |= 1L << coordSectionLimit;
            } else {
               long2objectopenhashmap.sectionMask = -1L;
            }

            this.hashChunk.forgetChunk(long2);
         }
      }
   }

   static long getTime(int chunkHashValue, int chunkHashValue2) {
      return ChunkPos.toLong(chunkHashValue >> 2, chunkHashValue2 >> 2);
   }

   void handleChunkClamped(MinecraftClient client) {
      ClientWorld world = client.world;
      if (world != null && client.player != null) {
         if (world != this.world) {
            this.updateHashOpen();
            this.world = world;
         }

         this.viewRadiusChunks = Math.max(2, Math.min(client.options.getClampedViewDistance() + 1, 32));
         ChunkPos chunkPos = client.player.getChunkPos();
         this.hashChunk.scanChunks(world, chunkPos.x, chunkPos.z, this.viewRadiusChunks, 600000L, ChunkScanScheduler.getFpsScaledBudgetNanos(1400), this::canVertexsPacked);
         if (++this.updateCounter >= 40) {
            this.updateCounter = 0;
            this.handlePackedArray(chunkPos.x, chunkPos.z);
         }
      } else {
         this.updateHashOpen();
      }
   }

   static final class Column {
      final int packedZ;
      long sectionMask;
      final long packedArrayValue;
      final int packedX;
      FluidStateHelper.Result[] vertexs = new FluidStateHelper.Result[0];

      Column(long time) {
         this.sectionMask = -1L;
         this.packedArrayValue = time;
         this.packedX = ChunkPos.getPackedX(time);
         this.packedZ = ChunkPos.getPackedZ(time);
      }
   }
}
