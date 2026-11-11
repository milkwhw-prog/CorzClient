package com.corz.client.config;

import java.nio.ByteOrder;
import java.util.Arrays;

public final class LayeredQuadBuffer {
   private final int[] layerQuadStarts;
   private boolean finished;
   private final int layerCount;
   private int[] vertexData;
   private int currentLayer = -1;
   private static final boolean LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
   private int writeCursor;
   private int quadCount;
   private static final int[] EMPTY_VERTEX_DATA = new int[0];

   public void addVertex(float quadUnfinishedValue, float quadUnfinishedValue2, float quadUnfinishedValue3, float quadUnfinishedValue4, float quadUnfinishedValue5, int quadUnfinishedValue6) {
      if (this.currentLayer < 0) {
         this.beginLayer(0);
      }

      if (this.writeCursor + 6 > this.vertexData.length) {
         this.growVertexData();
      }

      int[] copyOf = this.vertexData;
      int unfinishedQuadValue = this.writeCursor;
      copyOf[unfinishedQuadValue] = Float.floatToRawIntBits(quadUnfinishedValue);
      copyOf[unfinishedQuadValue + 1] = Float.floatToRawIntBits(quadUnfinishedValue2);
      copyOf[unfinishedQuadValue + 2] = Float.floatToRawIntBits(quadUnfinishedValue3);
      copyOf[unfinishedQuadValue + 3] = Float.floatToRawIntBits(quadUnfinishedValue4);
      copyOf[unfinishedQuadValue + 4] = Float.floatToRawIntBits(quadUnfinishedValue5);
      copyOf[unfinishedQuadValue + 5] = quadUnfinishedValue6;
      this.writeCursor = unfinishedQuadValue + 6;
      if (this.writeCursor % 24 == 0) {
         this.quadCount++;
      }
   }

   public int getLayerCount() {
      return this.layerCount;
   }

   public int getUsedIntCount() {
      return this.writeCursor;
   }

   public void finish() {
      if (!this.finished) {
         if (this.writeCursor % 24 != 0) {
            throw new IllegalStateException("unfinished quad");
         } else {
            while (this.currentLayer < this.layerCount) {
               this.currentLayer++;
               this.layerQuadStarts[this.currentLayer] = this.quadCount;
            }

            this.finished = true;
         }
      }
   }

   private void growVertexData() {
      long largeMeshLimit = Math.max(this.vertexData.length + 24L, this.vertexData.length * 2L);
      if (largeMeshLimit > 2147483639L) {
         throw new IllegalStateException("mesh too large");
      } else {
         this.vertexData = Arrays.copyOf(this.vertexData, (int)largeMeshLimit);
      }
   }

   public int[] getVertexData() {
      return this.vertexData;
   }

   public LayeredQuadBuffer(int unfinishedSinkBound, int rocketsBuyBound) {
      this.layerCount = Math.max(1, unfinishedSinkBound);
      this.layerQuadStarts = new int[this.layerCount + 1];
      this.vertexData = new int[Math.max(1, rocketsBuyBound) * 24];
   }

   public int getQuadCount() {
      return this.quadCount;
   }

   public int getLayerBoundary(int quadUnfinishedBound) {
      return this.layerQuadStarts[Math.max(0, Math.min(this.layerCount, quadUnfinishedBound))];
   }

   public int getLayerEndQuad(int unfinishedSinkValue) {
      return this.getLayerBoundary(unfinishedSinkValue + 1);
   }

   public void releaseVertexData() {
      this.vertexData = EMPTY_VERTEX_DATA;
      this.writeCursor = 0;
   }

   public int getClampedLayerEndQuad(int unfinishedSinkBound) {
      return unfinishedSinkBound < 0 ? 0 : this.getLayerBoundary(Math.min(this.layerCount - 1, unfinishedSinkBound) + 1);
   }

   public static int packColorBytes(int unfinishedSinkValue, int unfinishedSinkValue2, int unfinishedSinkValue3, int unfinishedSinkValue4) {
      unfinishedSinkValue &= 255;
      unfinishedSinkValue2 &= 255;
      unfinishedSinkValue3 &= 255;
      unfinishedSinkValue4 &= 255;
      return LITTLE_ENDIAN ? unfinishedSinkValue | unfinishedSinkValue2 << 8 | unfinishedSinkValue3 << 16 | unfinishedSinkValue4 << 24 : unfinishedSinkValue << 24 | unfinishedSinkValue2 << 16 | unfinishedSinkValue3 << 8 | unfinishedSinkValue4;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public static int packOpaqueColor(int unfinishedSinkValue) {
      return packColorBytes(unfinishedSinkValue >> 16, unfinishedSinkValue >> 8, unfinishedSinkValue, 255);
   }

   public void beginLayer(int unfinishedQuadBound) {
      if (this.finished) {
         throw new IllegalStateException("sink finished");
      } else {
         int unfinishedQuadLimit = Math.max(0, Math.min(this.layerCount - 1, unfinishedQuadBound));
         if (unfinishedQuadLimit < this.currentLayer) {
            throw new IllegalArgumentException("layers must not go down: " + unfinishedQuadLimit + " < " + this.currentLayer);
         } else {
            while (this.currentLayer < unfinishedQuadLimit) {
               this.currentLayer++;
               this.layerQuadStarts[this.currentLayer] = this.quadCount;
            }
         }
      }
   }
}
