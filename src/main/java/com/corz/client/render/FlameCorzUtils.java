package com.corz.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.BuiltBuffer.DrawParameters;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.MinimalSharpUtils;

public final class FlameCorzUtils {
   private static GpuBuffer gpuBuffer;
   public static final RenderPipeline build = RenderPipeline.builder()
      .withLocation(Identifier.of("corzclient", "pipeline/flame"))
      .withVertexShader(Identifier.of("corzclient", "core/corz_flame"))
      .withFragmentShader(Identifier.of("corzclient", "core/corz_flame"))
      .withVertexFormat(VertexFormats.POSITION, DrawMode.QUADS)
      .withUniform("CorzFlame", UniformType.UNIFORM_BUFFER)
      .withBlend(BlendFunction.TRANSLUCENT)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   private static GpuBuffer deviceCreate;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static boolean flameCorzActive;
   private static final ByteBuffer order = ByteBuffer.allocateDirect(96).order(ByteOrder.nativeOrder());

   public static void updateRegisterBuild() {
      if (MinimalSharpUtils.canFlagForFlag(flameCorzActive)) {
         flameCorzActive = true;
         RenderPipelines.register(build);
      }
   }

   private static GpuBuffer getDeviceCreate(GpuBuffer deviceCreate, String deviceCreateText, int deviceCreateValue, int remaining) {
      if (deviceCreate != null && deviceCreate.size() >= remaining) {
         return deviceCreate;
      } else {
         if (deviceCreate != null) {
            deviceCreate.close();
         }

         return RenderSystem.getDevice().createBuffer(() -> deviceCreateText, deviceCreateValue, remaining);
      }
   }

   private FlameCorzUtils() {
   }

   public static boolean canFlag(
      float flameCorzValue,
      float flameCorzFactor,
      float panelSamplerValue,
      float flameCorzBound,
      float flameCorzValue3,
      float flameCorzValue4,
      float flameCorzValue5,
      float flameCorzValue6,
      float flameCorzValue7,
      float flameCorzValue8,
      float flameCorzValue9,
      float flameCorzValue10,
      float flameCorzValue11,
      float flameCorzValue12,
      float flameCorzValue13,
      float flameCorzValue14,
      float flameCorzValue15,
      float flameCorzValue16,
      float flameCorzValue17,
      float flameCorzValue18,
      float flameCorzValue19,
      float flameCorzValue20
   ) {
      return MinimalSharpUtils.isBoolean(
         () -> {
            updateRegisterBuild();
            float flameCorzValue21 = flameCorzFactor * (1.0F - flameCorzValue20);
            float flameCorzValue22 = panelSamplerValue / Math.max(1.0F, flameCorzBound);
            ByteBuffer order2 = order;
            order2.putFloat(0, flameCorzValue);
            order2.putFloat(4, flameCorzFactor);
            order2.putFloat(8, panelSamplerValue);
            order2.putFloat(12, flameCorzBound);
            order2.putFloat(16, flameCorzValue3);
            order2.putFloat(20, flameCorzValue4);
            order2.putFloat(24, flameCorzValue5);
            order2.putFloat(28, flameCorzValue6);
            order2.putFloat(32, flameCorzValue7);
            order2.putFloat(36, flameCorzValue8);
            order2.putFloat(40, flameCorzValue9);
            order2.putFloat(44, 1.0F);
            order2.putFloat(48, flameCorzValue10);
            order2.putFloat(52, flameCorzValue11);
            order2.putFloat(56, flameCorzValue12);
            order2.putFloat(60, flameCorzValue13);
            order2.putFloat(64, flameCorzValue14);
            order2.putFloat(68, flameCorzValue15);
            order2.putFloat(72, flameCorzValue16);
            order2.putFloat(76, flameCorzValue17);
            order2.putFloat(80, flameCorzValue18);
            order2.putFloat(84, flameCorzValue19);
            order2.putFloat(88, flameCorzValue22);
            order2.putFloat(92, flameCorzValue20);
            BufferBuilder begin = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION);
            begin.vertex(MATRIX4F, 0.0F, flameCorzValue21, 0.0F);
            begin.vertex(MATRIX4F, 0.0F, flameCorzFactor, 0.0F);
            begin.vertex(MATRIX4F, flameCorzValue, flameCorzFactor, 0.0F);
            begin.vertex(MATRIX4F, flameCorzValue, flameCorzValue21, 0.0F);
            GpuDevice device = RenderSystem.getDevice();

            try (BuiltBuffer end = begin.end()) {
               DrawParameters drawParameters = end.getDrawParameters();
               ByteBuffer buffer = end.getBuffer();
               int remaining = buffer.remaining();
               deviceCreate = getDeviceCreate(deviceCreate, "corz_flame_vtx", 40, remaining);
               gpuBuffer = getDeviceCreate(gpuBuffer, "corz_flame_ubo", 136, 96);
               ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
               GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(drawParameters.indexCount());
               Framebuffer framebuffer = Module.mc.getFramebuffer();
               CommandEncoder commandEncoder = device.createCommandEncoder();
               commandEncoder.writeToBuffer(deviceCreate.slice(0L, remaining), buffer);
               commandEncoder.writeToBuffer(gpuBuffer.slice(0L, 96L), order2);

               try (RenderPass renderPass = commandEncoder.createRenderPass(
                     () -> "corz_flame", framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty()
                  )) {
                  renderPass.setPipeline(build);
                  MatrixWindowUtils.handleDequeScissor(renderPass);
                  renderPass.setUniform("CorzFlame", gpuBuffer);
                  renderPass.setVertexBuffer(0, deviceCreate);
                  renderPass.setIndexBuffer(indexBuffer, sequentialBuffer.getIndexType());
                  renderPass.drawIndexed(0, 0, drawParameters.indexCount(), 1);
               }
            }

            return true;
         }
      );
   }
}
