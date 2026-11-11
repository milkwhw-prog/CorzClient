package com.corz.client.utils;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import org.lwjgl.system.MemoryUtil;
import com.corz.client.utils.ChunkHash;
import com.corz.client.utils.FluidStateHelper;

final class HashOpenAlloc {
   final Long2ObjectOpenHashMap<HashOpenAlloc.Region> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
   final ArrayList<HashOpenAlloc.Region> hashRegionList = new ArrayList<>();
   private ByteBuffer memAlloc;
   private final LongOpenHashSet longOpenHashSet = new LongOpenHashSet();

   boolean isEmpty() {
      return this.long2ObjectOpenHashMap.isEmpty();
   }

   int getPackedHash(Long2ObjectOpenHashMap<ChunkHash.Column> long2ObjectOpenHashMap, int packedHashValue, int packedHashValue2, int packedHashLimit, long packedHashValue3, long transformsDynamicValue) {
      if (this.longOpenHashSet.isEmpty()) {
         return 0;
      } else {
         long nanoTime = System.nanoTime();
         int packedHash = 0;

         while (packedHash < packedHashLimit && !this.longOpenHashSet.isEmpty()) {
            long memAllocValue = 0L;
            int maxValue = Integer.MAX_VALUE;
            LongIterator iterator = this.longOpenHashSet.iterator();

            while (iterator.hasNext()) {
               long randomLong = iterator.nextLong();
               int maxAbs = Math.max(Math.abs(ChunkPos.getPackedX(randomLong) - packedHashValue), Math.abs(ChunkPos.getPackedZ(randomLong) - packedHashValue2));
               if (maxAbs < maxValue) {
                  maxValue = maxAbs;
                  memAllocValue = randomLong;
               }
            }

            this.longOpenHashSet.remove(memAllocValue);
            this.handleMemAlloc(long2ObjectOpenHashMap, memAllocValue, transformsDynamicValue);
            packedHash++;
            if (System.nanoTime() - nanoTime > packedHashValue3) {
               break;
            }
         }

         return packedHash;
      }
   }

   void updateMemAlloc() {
      this.updateClearHash();
      if (this.memAlloc != null) {
         MemoryUtil.memFree(this.memAlloc);
         this.memAlloc = null;
      }
   }

   int getHashOpen() {
      return this.longOpenHashSet.size();
   }

   void handleHashOpen(long hashOpenValue) {
      this.longOpenHashSet.add(hashOpenValue);
   }

   private static void handleBufferClose(HashOpenAlloc.Region region) {
      if (region.buffer != null) {
         region.buffer.close();
         region.buffer = null;
      }

      region.vertexCount = 0;
   }

   private void handleMemAlloc(Long2ObjectOpenHashMap<ChunkHash.Column> long2ObjectOpenHashMap, long memAllocValue, long time) {
      int packedX = ChunkPos.getPackedX(memAllocValue);
      int packedZ = ChunkPos.getPackedZ(memAllocValue);
      byte memAllocLimit = 4;
      int memAllocTotal = 0;
      int value = 0;
      float memAllocBound = Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;

      for (int i = 0; i < memAllocLimit; i++) {
         for (int j = 0; j < memAllocLimit; j++) {
            ChunkHash.Column column = (ChunkHash.Column)long2ObjectOpenHashMap.get(ChunkPos.toLong(packedX * memAllocLimit + i, packedZ * memAllocLimit + j));
            if (column != null) {
               for (FluidStateHelper.Result result : column.vertexs) {
                  if (result != null) {
                     memAllocTotal += result.bufferStartTable.length;
                     value += result.vertexCount;
                     memAllocBound = Math.min(memAllocBound, result.bufferStartAmount);
                     pilotStatusBound = Math.max(pilotStatusBound, result.maxHeight);
                  }
               }
            }
         }
      }

      HashOpenAlloc.Region long2objectopenhashmap = (HashOpenAlloc.Region)this.long2ObjectOpenHashMap.get(memAllocValue);
      if (value != 0 && memAllocTotal != 0) {
         if (this.memAlloc == null || this.memAlloc.capacity() < memAllocTotal) {
            if (this.memAlloc != null) {
               MemoryUtil.memFree(this.memAlloc);
            }

            this.memAlloc = MemoryUtil.memAlloc(Math.max(memAllocTotal, 65536));
         }

         this.memAlloc.clear();

         for (int k = 0; k < memAllocLimit; k++) {
            for (int m = 0; m < memAllocLimit; m++) {
               ChunkHash.Column chunkHashColumn = (ChunkHash.Column)long2ObjectOpenHashMap.get(ChunkPos.toLong(packedX * memAllocLimit + k, packedZ * memAllocLimit + m));
               if (chunkHashColumn != null) {
                  for (FluidStateHelper.Result result2 : chunkHashColumn.vertexs) {
                     if (result2 != null) {
                        this.memAlloc.put(result2.bufferStartTable);
                     }
                  }
               }
            }
         }

         this.memAlloc.flip();
         if (long2objectopenhashmap == null) {
            long2objectopenhashmap = new HashOpenAlloc.Region(memAllocValue);
            this.long2ObjectOpenHashMap.put(memAllocValue, long2objectopenhashmap);
            this.hashRegionList.add(long2objectopenhashmap);
         }

         if (long2objectopenhashmap.buffer == null || long2objectopenhashmap.buffer.isClosed() || long2objectopenhashmap.capacityBytes < memAllocTotal) {
            if (long2objectopenhashmap.buffer != null) {
               long2objectopenhashmap.buffer.close();
            }

            long max = Math.max(memAllocTotal + memAllocTotal / 4L, 4096L);
            long2objectopenhashmap.buffer = RenderSystem.getDevice().createBuffer(() -> "corz_water_region", 40, max);
            long2objectopenhashmap.capacityBytes = max;
         }

         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(long2objectopenhashmap.buffer.slice(0L, memAllocTotal), this.memAlloc);
         if (long2objectopenhashmap.vertexCount == 0) {
            long2objectopenhashmap.fadeStartNanos = time;
         }

         long2objectopenhashmap.vertexCount = value;
         int packed = long2objectopenhashmap.getPacked();
         int packedZ2 = long2objectopenhashmap.getPackedZ();
         long2objectopenhashmap.box = new Box(packed, memAllocBound - 0.5, packedZ2, packed + 64, pilotStatusBound + 0.5, packedZ2 + 64);
      } else {
         if (long2objectopenhashmap != null) {
            handleBufferClose(long2objectopenhashmap);
            this.long2ObjectOpenHashMap.remove(memAllocValue);
            this.hashRegionList.remove(long2objectopenhashmap);
         }
      }
   }

   void updateClearHash() {
      for (int i = 0; i < this.hashRegionList.size(); i++) {
         handleBufferClose(this.hashRegionList.get(i));
      }

      this.long2ObjectOpenHashMap.clear();
      this.hashRegionList.clear();
      this.longOpenHashSet.clear();
   }

   static final class Region {
      int vertexCount;
      final int packedX;
      long capacityBytes;
      final int packedZ;
      final long nextLong;
      Box box;
      long fadeStartNanos = -1L;
      GpuBuffer buffer;

      Region(long nextLong) {
         this.nextLong = nextLong;
         this.packedX = ChunkPos.getPackedX(nextLong);
         this.packedZ = ChunkPos.getPackedZ(nextLong);
      }

      int getPacked() {
         return this.packedX * 64;
      }

      int getPackedZ() {
         return this.packedZ * 64;
      }
   }
}
