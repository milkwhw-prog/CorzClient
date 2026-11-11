package com.corz.client.render;

import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

final class SchemCorz implements AutoCloseable {
   BufferGpu corzSchemOverlay;
   int visitedChunkCount;
   final int matrixStackValue;
   int bufferCloseValue;
   int[] intArray;
   BufferGpu corzSchemGhostChest;
   BufferGpu corzSchemOutline;
   double distanceSq;
   final long longValue;
   private static final int[] schemCorzTable = new int[0];
   long[] longArray;
   int chunkArrayCount;
   final int arrayHashValue;
   int[] hashOpenTable;
   final int schemCorzValue;
   boolean fmsRebuildActive;
   byte[] unpackExtraTable;
   private static final byte[] table = new byte[0];
   int fmsRebuildCount;
   boolean chunkArrayActive;
   boolean hashOpenActive;
   final Box box;
   final BufferGpu[] transformsDynamicBufferGpus;
   int schemCorzCount;
   static final long[] chunkArrayTable = new long[0];
   int chunkArrayRebuiltCount;
   int arrayHashCount;

   BufferGpu getSchemOverlay() {
      if (this.corzSchemOverlay == null) {
         this.corzSchemOverlay = new BufferGpu("corz_schem_overlay");
      }

      return this.corzSchemOverlay;
   }

   double getAmount(double schemCorzValue, double schemCorzValue2, double schemCorzValue3) {
      double schemCorzFactor = this.schemCorzValue + 8.0 - schemCorzValue;
      double schemCorzFactor2 = this.matrixStackValue + 8.0 - schemCorzValue2;
      double schemCorzFactor3 = this.arrayHashValue + 8.0 - schemCorzValue3;
      return schemCorzFactor * schemCorzFactor + schemCorzFactor2 * schemCorzFactor2 + schemCorzFactor3 * schemCorzFactor3;
   }

   @Override
   public void close() {
      for (int i = 0; i < this.transformsDynamicBufferGpus.length; i++) {
         if (this.transformsDynamicBufferGpus[i] != null) {
            this.transformsDynamicBufferGpus[i].close();
            this.transformsDynamicBufferGpus[i] = null;
         }
      }

      if (this.corzSchemGhostChest != null) {
         this.corzSchemGhostChest.close();
         this.corzSchemGhostChest = null;
      }

      if (this.corzSchemOverlay != null) {
         this.corzSchemOverlay.close();
         this.corzSchemOverlay = null;
      }

      if (this.corzSchemOutline != null) {
         this.corzSchemOutline.close();
         this.corzSchemOutline = null;
      }
   }

   BufferGpu getGhostSchem() {
      if (this.corzSchemGhostChest == null) {
         this.corzSchemGhostChest = new BufferGpu("corz_schem_ghost_chest");
      }

      return this.corzSchemGhostChest;
   }

   BufferGpu getGpusTransforms(BlockRenderLayer blockRenderLayer) {
      int ordinal = blockRenderLayer.ordinal();
      BufferGpu gpusTransforms = this.transformsDynamicBufferGpus[ordinal];
      if (gpusTransforms == null) {
         gpusTransforms = new BufferGpu("corz_schem_ghost_" + blockRenderLayer.getName());
         this.transformsDynamicBufferGpus[ordinal] = gpusTransforms;
      }

      return gpusTransforms;
   }

   SchemCorz(long longValue, int value, int value2, int value3) {
      this.hashOpenTable = schemCorzTable;
      this.unpackExtraTable = table;
      this.intArray = schemCorzTable;
      this.longArray = chunkArrayTable;
      this.hashOpenActive = true;
      this.transformsDynamicBufferGpus = new BufferGpu[BlockRenderLayer.values().length];
      this.longValue = longValue;
      this.schemCorzValue = value;
      this.matrixStackValue = value2;
      this.arrayHashValue = value3;
      this.box = new Box(value, value2, value3, value + 16, value2 + 16, value3 + 16);
   }

   static long getTime(int schemCorzValue, int schemCorzValue2, int schemCorzValue3) {
      return BlockPos.asLong(schemCorzValue >> 4, schemCorzValue2 >> 4, schemCorzValue3 >> 4);
   }

   boolean isSchemCorz() {
      for (BufferGpu bufferGpu : this.transformsDynamicBufferGpus) {
         if (bufferGpu != null && !bufferGpu.isBuffer()) {
            return false;
         }
      }

      return this.corzSchemGhostChest != null && !this.corzSchemGhostChest.isBuffer()
         ? false
         : (this.corzSchemOverlay == null || this.corzSchemOverlay.isBuffer()) && (this.corzSchemOutline == null || this.corzSchemOutline.isBuffer());
   }

   BufferGpu getSchemOutline() {
      if (this.corzSchemOutline == null) {
         this.corzSchemOutline = new BufferGpu("corz_schem_outline");
      }

      return this.corzSchemOutline;
   }
}
