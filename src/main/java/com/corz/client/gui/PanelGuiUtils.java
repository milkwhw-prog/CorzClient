package com.corz.client.gui;

import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.DynMappableUtils;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

public final class PanelGuiUtils {
   private static GpuSampler sampler;
   private static boolean shaderCompileFlag;
   public static final RenderPipeline build = RenderPipeline.builder()
      .withLocation(Identifier.of("corzclient", "pipeline/gui3d_panel"))
      .withVertexShader(Identifier.of("corzclient", "core/gui3d_panel"))
      .withFragmentShader(Identifier.of("corzclient", "core/gui3d_panel"))
      .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
      .withUniform("CorzGui3dPanel", UniformType.UNIFORM_BUFFER)
      .withSampler("Sampler0")
      .withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   private static int samplerPanelCount;
   private static final GpuBufferSlice[] panelGuiGpuBufferSlices = new GpuBufferSlice[24];
   private static boolean valid;
   private static final GpuTextureView[] samplerPanelGpuTextureViews = new GpuTextureView[24];
   private static GpuBuffer buffer;

   public static void handlePanelSampler(
      GpuTextureView gpuTextureView,
      Matrix4f matrix,
      float panelSamplerValue,
      float panelSamplerValue2,
      float panelSamplerValue3,
      float panelSamplerValue4,
      float panelSamplerValue5,
      float panelSamplerValue6,
      float panelSamplerValue7,
      float panelSamplerValue8,
      float panelSamplerValue9,
      float panelSamplerValue10,
      float panelSamplerValue11,
      float panelSamplerValue12,
      float panelSamplerValue13,
      float panelSamplerValue14,
      float panelSamplerValue15,
      float panelSamplerValue16,
      float panelSamplerValue17,
      float panelSamplerValue18,
      float panelSamplerValue19,
      float panelSamplerValue20,
      float panelSamplerValue21,
      float panelSamplerValue22,
      float panelSamplerValue23,
      float panelSamplerValue24,
      float panelSamplerValue25,
      float panelSamplerValue26,
      float panelSamplerValue27,
      float panelSamplerValue28,
      float panelSamplerValue29,
      float panelSamplerValue30,
      float panelSamplerValue31,
      float panelSamplerValue32
   ) {
      if (samplerPanelCount < 24 && gpuTextureView != null) {
         ByteBuffer order = DynMappableUtils.getOrder(192);
         matrix.get(0, order);
         order.putFloat(64, panelSamplerValue);
         order.putFloat(68, panelSamplerValue2);
         order.putFloat(72, panelSamplerValue3);
         order.putFloat(76, panelSamplerValue4);
         order.putFloat(80, panelSamplerValue5);
         order.putFloat(84, panelSamplerValue6);
         order.putFloat(88, panelSamplerValue7);
         order.putFloat(92, panelSamplerValue8);
         order.putFloat(96, panelSamplerValue9);
         order.putFloat(100, panelSamplerValue10);
         order.putFloat(104, panelSamplerValue11);
         order.putFloat(108, panelSamplerValue12);
         order.putFloat(112, panelSamplerValue13);
         order.putFloat(116, panelSamplerValue14);
         order.putFloat(120, panelSamplerValue15);
         order.putFloat(124, panelSamplerValue16);
         order.putFloat(128, panelSamplerValue17);
         order.putFloat(132, panelSamplerValue18);
         order.putFloat(136, panelSamplerValue19);
         order.putFloat(140, panelSamplerValue20);
         order.putFloat(144, panelSamplerValue21);
         order.putFloat(148, panelSamplerValue22);
         order.putFloat(152, panelSamplerValue23);
         order.putFloat(156, panelSamplerValue24);
         order.putFloat(160, panelSamplerValue25);
         order.putFloat(164, panelSamplerValue26);
         order.putFloat(168, panelSamplerValue27);
         order.putFloat(172, panelSamplerValue28);
         order.putFloat(176, panelSamplerValue29);
         order.putFloat(180, panelSamplerValue30);
         order.putFloat(184, panelSamplerValue31);
         order.putFloat(188, panelSamplerValue32);
         panelGuiGpuBufferSlices[samplerPanelCount] = DynMappableUtils.getSlice(order);
         samplerPanelGpuTextureViews[samplerPanelCount] = gpuTextureView;
         samplerPanelCount++;
      }
   }

   private PanelGuiUtils() {
   }

   public static boolean isShaderValid() {
      if (!shaderCompileFlag) {
         shaderCompileFlag = true;

         try {
            valid = RenderSystem.getDevice().precompilePipeline(build).isValid();
         } catch (Throwable e) {
            valid = false;
         }

         if (!valid) {
            ClampMatrixUtils.handleFrameClamp("shader", new IllegalStateException("gui3d_panel pipeline did not compile"));
         }
      }

      return valid;
   }

   private static GpuSampler getSamplerLinear() {
      if (sampler == null) {
         sampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
      }

      return sampler;
   }

   private static GpuBuffer getMemBuffer() {
      if (buffer == null) {
         ByteBuffer memAlloc = MemoryUtil.memAlloc(4);

         try {
            memAlloc.putInt(0);
            memAlloc.flip();
            buffer = RenderSystem.getDevice().createBuffer(() -> "corz_gui3d_dummy", 32, memAlloc);
         } finally {
            MemoryUtil.memFree(memAlloc);
         }
      }

      return buffer;
   }

   public static void handleSamplerPanel(Framebuffer framebuffer) {
      if (samplerPanelCount != 0) {
         try {
            GpuBuffer memBuffer = getMemBuffer();
            GpuSampler samplerLinear = getSamplerLinear();
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

            try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "corz_gui3d_panel", framebuffer.getColorAttachmentView(), OptionalInt.empty())) {
               DynMappableUtils.samplerCorzCount++;
               renderPass.setPipeline(build);
               renderPass.setVertexBuffer(0, memBuffer);

               for (int i = 0; i < samplerPanelCount; i++) {
                  renderPass.bindTexture("Sampler0", samplerPanelGpuTextureViews[i], samplerLinear);
                  renderPass.setUniform("CorzGui3dPanel", panelGuiGpuBufferSlices[i]);
                  renderPass.draw(0, 6);
               }
            }
         } finally {
            updatePanelSampler();
         }
      }
   }

   public static void updatePanelSampler() {
      for (int i = 0; i < samplerPanelCount; i++) {
         panelGuiGpuBufferSlices[i] = null;
         samplerPanelGpuTextureViews[i] = null;
      }

      samplerPanelCount = 0;
   }
}
