package com.corz.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBuffer.MappedView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.minecraft.client.gl.MappableRingBuffer;

public final class DynMappableUtils {
   public static int samplerCorzCount;
   private static final ByteBuffer order = ByteBuffer.allocateDirect(256).order(ByteOrder.nativeOrder());
   public static int totalSlicesCreated;
   private static MappableRingBuffer mappableRingBuffer;
   private static MappableRingBuffer sliceMappableRingBuffer;
   public static int mappableRingCount;
   private static int dynMappableValue;
   private static int sliceCount;

   private DynMappableUtils() {
   }

   public static ByteBuffer getOrder(int orderLimit) {
      if (orderLimit > order.capacity()) {
         throw new IllegalArgumentException("scratch " + orderLimit + " > " + order.capacity());
      } else {
         order.clear();
         order.limit(orderLimit);
         return order;
      }
   }

   public static DynMappableUtils.Vertices getMappableRing(ByteBuffer byteBuffer, int mappableRingDivisor) {
      int remaining = byteBuffer.remaining();
      if (remaining > 2097152) {
         throw new IllegalArgumentException("vertex batch " + remaining + " B exceeds the " + 2097152 + " B ring");
      } else {
         if (mappableRingBuffer == null) {
            mappableRingBuffer = new MappableRingBuffer(() -> "corz_dyn_vtx", 34, 2097152);
         }

         int mappableRingValue = (dynMappableValue + mappableRingDivisor - 1) / mappableRingDivisor * mappableRingDivisor;
         if (mappableRingValue + remaining > 2097152) {
            CorzRoundedUtils.updateSamplerView();
            mappableRingBuffer.rotate();
            dynMappableValue = 0;
            mappableRingValue = 0;
            mappableRingCount++;
         }

         GpuBuffer blocking = mappableRingBuffer.getBlocking();
         GpuBufferSlice slice = blocking.slice(mappableRingValue, remaining);

         try (MappedView mapBuffer = RenderSystem.getDevice().createCommandEncoder().mapBuffer(slice, false, true)) {
            mapBuffer.data().put(0, byteBuffer, byteBuffer.position(), remaining);
         }

         dynMappableValue = mappableRingValue + remaining;
         return new DynMappableUtils.Vertices(blocking, mappableRingValue / mappableRingDivisor);
      }
   }

   public static GpuBufferSlice getSlice(ByteBuffer byteBuffer) {
      int remaining = byteBuffer.remaining();
      int sliceValue = remaining + 256 - 1 & -256;
      if (sliceMappableRingBuffer == null) {
         sliceMappableRingBuffer = new MappableRingBuffer(() -> "corz_dyn_ubo", 130, 524288);
      }

      if (sliceCount + sliceValue > 524288) {
         CorzRoundedUtils.updateSamplerView();
         sliceMappableRingBuffer.rotate();
         sliceCount = 0;
         totalSlicesCreated++;
      }

      GpuBuffer blocking = sliceMappableRingBuffer.getBlocking();
      GpuBufferSlice slice = blocking.slice(sliceCount, remaining);

      try (MappedView mapBuffer = RenderSystem.getDevice().createCommandEncoder().mapBuffer(slice, false, true)) {
         mapBuffer.data().put(0, byteBuffer, byteBuffer.position(), remaining);
      }

      sliceCount += sliceValue;
      return slice;
   }

   public record Vertices(GpuBuffer buffer, int baseVertex) {
   }
}
