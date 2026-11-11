package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.UnreflectReference;
import com.corz.client.utils.EspHoleApi;
import com.corz.client.utils.ConstCopperUtils;
import com.corz.client.utils.CopperBrickUtils;

public final class ChunkSectionHelper implements EspHoleApi {
   private int verticalSections;
   private final Long2ObjectOpenHashMap<ChunkSectionHelper.Entry> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
   private int bottomY;
   private int sectionCoord;
   private int worldTopY;
   private WorldChunk worldChunk;
   private byte[] unreflectReferenceTable;
   private final UnreflectReference unreflectReference = new UnreflectReference();
   private boolean chunkWorldActive;
   private int chunkWorldValue;
   private int chunkSectionValue;
   private ClientWorld world;

   public int getHashOpen() {
      return this.long2ObjectOpenHashMap.size();
   }

   @Override
   public byte[] getSectionHash(int sectionHashValue, int sectionHashValue2, int sectionHashValue3) {
      WorldChunk chunkWorld = this.getChunkWorld(sectionHashValue, sectionHashValue3);
      if (chunkWorld == null) {
         return null;
      } else {
         int index = sectionHashValue2 - this.sectionCoord;
         if (index < 0) {
            return ConstCopperUtils.buriedOpenTable;
         } else {
            ChunkSection[] sectionArray = chunkWorld.getSectionArray();
            if (index >= sectionArray.length) {
               return ConstCopperUtils.sectionHashTable;
            } else {
               ChunkSection chunkSection = sectionArray[index];
               if (chunkSection != null && !chunkSection.isEmpty()) {
                  long long2 = ChunkSectionPos.asLong(sectionHashValue, sectionHashValue2, sectionHashValue3);
                  long nanoTime = System.nanoTime();
                  ChunkSectionHelper.Entry long2objectopenhashmap = (ChunkSectionHelper.Entry)this.long2ObjectOpenHashMap.get(long2);
                  if (long2objectopenhashmap != null && long2objectopenhashmap.chunkSection == chunkSection && nanoTime - long2objectopenhashmap.nanoTime < 6000000000L) {
                     return long2objectopenhashmap.values;
                  } else {
                     if (long2objectopenhashmap == null) {
                        if (this.long2ObjectOpenHashMap.size() >= 1024) {
                           this.long2ObjectOpenHashMap.clear();
                           this.chunkWorldActive = false;
                           WorldChunk chunkWorld2 = this.getChunkWorld(sectionHashValue, sectionHashValue3);
                           if (chunkWorld2 == null) {
                              return null;
                           }
                        }

                        long2objectopenhashmap = new ChunkSectionHelper.Entry();
                        this.long2ObjectOpenHashMap.put(long2, long2objectopenhashmap);
                     }

                     this.handleUnreflectReference(chunkSection, long2objectopenhashmap.values);
                     long2objectopenhashmap.chunkSection = chunkSection;
                     long2objectopenhashmap.nanoTime = nanoTime;
                     return long2objectopenhashmap.values;
                  }
               } else {
                  return ConstCopperUtils.sectionHashTable;
               }
            }
         }
      }
   }

   @Override
   public int getValue() {
      return this.worldTopY;
   }

   private WorldChunk getChunkWorld(int value, int value2) {
      if (this.chunkWorldActive && this.chunkSectionValue == value && this.chunkWorldValue == value2) {
         return this.worldChunk;
      } else {
         WorldChunk worldChunk = this.world == null ? null : this.world.getChunkManager().getWorldChunk(value, value2);
         this.worldChunk = worldChunk;
         this.chunkSectionValue = value;
         this.chunkWorldValue = value2;
         this.chunkWorldActive = true;
         return worldChunk;
      }
   }

   @Override
   public int getBottom() {
      return this.bottomY;
   }

   public void handleHashOpen(int hashOpenValue, int hashOpenValue2, int hashOpenValue3) {
      this.long2ObjectOpenHashMap.remove(ChunkSectionPos.asLong(hashOpenValue >> 4, hashOpenValue2 >> 4, hashOpenValue3 >> 4));
   }

   public void handleSectionsCoord(int sectionsCoordValue, int sectionsCoordValue2) {
      for (int i = 0; i < this.verticalSections; i++) {
         this.long2ObjectOpenHashMap.remove(ChunkSectionPos.asLong(sectionsCoordValue, this.sectionCoord + i, sectionsCoordValue2));
      }

      this.chunkWorldActive = false;
   }

   private void handleUnreflectReference(ChunkSection chunkSection, byte[] unreflectReferenceBytes) {
      this.unreflectReference.handleFlag(chunkSection);
      int unreflectReferenceLimit = this.unreflectReference.paletteSize;
      if (this.unreflectReferenceTable.length < unreflectReferenceLimit) {
         this.unreflectReferenceTable = new byte[Math.max(unreflectReferenceLimit, this.unreflectReferenceTable.length * 2)];
      }

      BlockState[] copyOf = this.unreflectReference.copyOf;

      for (int i = 0; i < unreflectReferenceLimit; i++) {
         this.unreflectReferenceTable[i] = CopperBrickUtils.getByteReference(copyOf[i]);
      }

      int[] unreflectReferenceValues = this.unreflectReference.unreflectReferenceTable;

      for (int j = 0; j < 4096; j++) {
         unreflectReferenceBytes[j] = this.unreflectReferenceTable[unreflectReferenceValues[j]];
      }
   }

   public void updateClearChunk() {
      this.long2ObjectOpenHashMap.clear();
      this.chunkWorldActive = false;
      this.worldChunk = null;
   }

   public ChunkSectionHelper() {
      this.unreflectReferenceTable = new byte[64];
   }

   public void handleBottomSections(ClientWorld world) {
      if (world != this.world) {
         this.world = world;
         this.updateClearChunk();
      }

      if (world != null) {
         this.sectionCoord = world.getBottomSectionCoord();
         this.verticalSections = world.countVerticalSections();
         this.bottomY = world.getBottomY();
         this.worldTopY = world.getTopYInclusive() + 1;
      }

      this.chunkWorldActive = false;
   }

   @Override
   public int getHeightmapSample(int heightmapSampleValue, int heightmapSampleValue2) {
      WorldChunk chunkWorld = this.getChunkWorld(heightmapSampleValue >> 4, heightmapSampleValue2 >> 4);
      return chunkWorld == null ? Integer.MIN_VALUE : chunkWorld.sampleHeightmap(Type.WORLD_SURFACE, heightmapSampleValue, heightmapSampleValue2) + 1;
   }

   private static final class Entry {
      long nanoTime;
      final byte[] values = new byte[4096];
      ChunkSection chunkSection;
   }
}
