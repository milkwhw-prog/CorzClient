package com.corz.client.render;

import com.corz.client.config.LayeredQuadBuffer;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.system.MemoryUtil;

public final class GpuBufferHelper implements AutoCloseable {
   private GpuBuffer gpuBuffer;
   private final int mappedLength;
   private GpuBuffer gpuBuffer2GpuBuffer;
   private final int gpuBufferValue;
   private final int bufferSizeBytes;
   private GpuBuffer gpuBufferGpuBuffer;
   private boolean gpuBufferActive;

   public GpuBuffer getGpuBuffer() {
      return this.gpuBufferActive ? null : this.gpuBufferGpuBuffer;
   }

   public static GpuBufferHelper getVertexClose(LayeredQuadBuffer unfinishedSink, LayeredQuadBuffer unfinishedSink2, LayeredQuadBuffer unfinishedSink3, String vertexCloseText) {
      if (VertexFormats.POSITION_TEXTURE_COLOR.getVertexSize() != 24) {
         throw new IllegalStateException("unexpected vertex size " + VertexFormats.POSITION_TEXTURE_COLOR.getVertexSize());
      } else {
         GpuBuffer memReleased = getMemReleased(unfinishedSink, vertexCloseText + "_solid");
         GpuBuffer gpuBuffer = null;

         GpuBuffer memReleased2;
         try {
            gpuBuffer = getMemReleased(unfinishedSink2, vertexCloseText + "_translucent");
            memReleased2 = getMemReleased(unfinishedSink3, vertexCloseText + "_chest");
         } catch (Throwable e) {
            if (memReleased != null) {
               memReleased.close();
            }

            if (gpuBuffer != null) {
               gpuBuffer.close();
            }

            throw e;
         }

         return new GpuBufferHelper(
            memReleased, unfinishedSink.getQuadCount(), gpuBuffer, unfinishedSink2.getQuadCount(), memReleased2, unfinishedSink3 == null ? 0 : unfinishedSink3.getQuadCount()
         );
      }
   }

   public boolean isFlag() {
      return this.gpuBufferActive;
   }

   public int getBufferSizeBytes() {
      return this.bufferSizeBytes;
   }

   @Override
   public void close() {
      if (!this.gpuBufferActive) {
         this.gpuBufferActive = true;
         if (this.gpuBufferGpuBuffer != null) {
            try {
               this.gpuBufferGpuBuffer.close();
            } catch (Throwable e) {
            }

            this.gpuBufferGpuBuffer = null;
         }

         if (this.gpuBuffer2GpuBuffer != null) {
            try {
               this.gpuBuffer2GpuBuffer.close();
            } catch (Throwable e2) {
            }

            this.gpuBuffer2GpuBuffer = null;
         }

         if (this.gpuBuffer != null) {
            try {
               this.gpuBuffer.close();
            } catch (Throwable e3) {
            }

            this.gpuBuffer = null;
         }
      }
   }

   public int getGpuBufferValue() {
      return this.gpuBufferValue;
   }

   private static GpuBuffer getMemReleased(LayeredQuadBuffer unfinishedSink, String memReleasedText) {
      if (unfinishedSink != null && unfinishedSink.getQuadCount() != 0 && unfinishedSink.getUsedIntCount() != 0) {
         int memReleasedLimit = unfinishedSink.getQuadCount() * 24;
         if (memReleasedLimit > unfinishedSink.getUsedIntCount()) {
            throw new IllegalStateException("mesh data was already released");
         } else {
            ByteBuffer memAlloc = MemoryUtil.memAlloc(memReleasedLimit * 4);

            GpuBuffer memReleased;
            try {
               memAlloc.asIntBuffer().put(unfinishedSink.getVertexData(), 0, memReleasedLimit);
               memAlloc.position(0).limit(memReleasedLimit * 4);
               memReleased = RenderSystem.getDevice().createBuffer(() -> memReleasedText, 32, memAlloc);
            } finally {
               MemoryUtil.memFree(memAlloc);
            }

            return memReleased;
         }
      } else {
         return null;
      }
   }

   public GpuBuffer getGpuBufferGpuBufferActive() {
      return this.gpuBufferActive ? null : this.gpuBuffer;
   }

   private GpuBufferHelper(GpuBuffer gpuBuffer, int gpuBufferValue, GpuBuffer gpuBuffer2, int gpuBufferValue2, GpuBuffer gpuBuffer3, int gpuBufferValue3) {
      this.gpuBufferGpuBuffer = gpuBuffer;
      this.bufferSizeBytes = gpuBuffer == null ? 0 : gpuBufferValue;
      this.gpuBuffer2GpuBuffer = gpuBuffer2;
      this.mappedLength = gpuBuffer2 == null ? 0 : gpuBufferValue2;
      this.gpuBuffer = gpuBuffer3;
      this.gpuBufferValue = gpuBuffer3 == null ? 0 : gpuBufferValue3;
   }

   public GpuBuffer getGpuBuffer3() {
      return this.gpuBufferActive ? null : this.gpuBuffer2GpuBuffer;
   }

   public int getValue() {
      return this.mappedLength;
   }
}
