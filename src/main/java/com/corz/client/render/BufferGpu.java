package com.corz.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.BuiltBuffer.DrawParameters;

final class BufferGpu implements AutoCloseable {
   DrawMode mode = DrawMode.QUADS;
   private int remaining;
   private final String bufferDeviceText;
   private GpuBuffer buffer;
   int indexCount;

   @Override
   public void close() {
      if (this.buffer != null) {
         this.buffer.close();
         this.buffer = null;
      }

      this.remaining = 0;
      this.indexCount = 0;
   }

   BufferGpu(String text) {
      this.bufferDeviceText = text;
   }

   boolean isBuffer() {
      return this.buffer == null || this.indexCount == 0;
   }

   GpuBuffer getBuffer() {
      return this.buffer;
   }

   void handleBufferDevice(BuiltBuffer builtBuffer) {
      if (builtBuffer == null) {
         this.indexCount = 0;
      } else {
         ByteBuffer buffer = builtBuffer.getBuffer();
         int remaining = buffer.remaining();
         DrawParameters drawParameters = builtBuffer.getDrawParameters();
         this.mode = drawParameters.mode();
         this.indexCount = drawParameters.indexCount();
         if (remaining != 0 && this.indexCount != 0) {
            if (this.buffer == null || this.remaining < remaining) {
               if (this.buffer != null) {
                  this.buffer.close();
               }

               this.buffer = RenderSystem.getDevice().createBuffer(() -> this.bufferDeviceText, 40, remaining);
               this.remaining = remaining;
            }

            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(0L, remaining), buffer);
         } else {
            this.indexCount = 0;
         }
      }
   }
}
