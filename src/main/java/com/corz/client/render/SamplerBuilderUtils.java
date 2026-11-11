package com.corz.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.nio.ByteBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;
import com.corz.client.utils.FovOptionsUtils;

public final class SamplerBuilderUtils {
   private static final BlendFunction BLEND_FUNCTION = new BlendFunction(SourceFactor.ONE, DestFactor.ZERO, SourceFactor.ONE, DestFactor.ZERO);
   private static long timeMillis = -1L;
   public static final RenderPipeline REGISTER = getRegister(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation("pipeline/kawase_down")
         .withUniform("KawaseData", UniformType.UNIFORM_BUFFER)
         .withSampler("Sampler0")
         .withVertexShader(getIdentifier("kawase_down"))
         .withFragmentShader(getIdentifier("kawase_down"))
         .withBlend(SamplerBuilderUtils.BLEND_FUNCTION)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
         .build()
   );
   public static final RenderPipeline KAWASE_FINAL_PIPELINE = getRegister(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation("pipeline/kawase_up")
         .withUniform("KawaseData", UniformType.UNIFORM_BUFFER)
         .withSampler("Sampler0")
         .withVertexShader(getIdentifier("kawase_up"))
         .withFragmentShader(getIdentifier("kawase_up"))
         .withBlend(BLEND_FUNCTION)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
         .build()
   );
   public static final RenderPipeline PIPELINE = getRegister(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation("pipeline/hand_fire")
         .withUniform("HandFireData", UniformType.UNIFORM_BUFFER)
         .withSampler("SceneSampler")
         .withSampler("BlurSampler")
         .withSampler("MaskSampler")
         .withVertexShader(getIdentifier("hand_fire"))
         .withFragmentShader(getIdentifier("hand_fire"))
         .withBlend(SamplerBuilderUtils.BLEND_FUNCTION)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
         .build()
   );
   private static GpuBuffer buffer;
   public static final RenderPipeline SAMPLER_TRAIL_PIPELINE = getRegister(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation("pipeline/hand_trail")
         .withUniform("HandTrailData", UniformType.UNIFORM_BUFFER)
         .withSampler("PrevTrailSampler")
         .withSampler("SceneSampler")
         .withSampler("MaskSampler")
         .withVertexShader(getIdentifier("hand_trail"))
         .withFragmentShader(getIdentifier("hand_trail"))
         .withBlend(SamplerBuilderUtils.BLEND_FUNCTION)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
         .build()
   );
   public static final RenderPipeline SAMPLER_BIND_PIPELINE = getRegister(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation("pipeline/mask_diff")
         .withUniform("MaskData", UniformType.UNIFORM_BUFFER)
         .withSampler("BeforeSampler")
         .withSampler("AfterSampler")
         .withSampler("DepthBeforeSampler")
         .withSampler("DepthAfterSampler")
         .withVertexShader(getIdentifier("mask_diff"))
         .withFragmentShader(getIdentifier("mask_diff"))
         .withBlend(SamplerBuilderUtils.BLEND_FUNCTION)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
         .build()
   );
   public static final RenderPipeline SAMPLER_DYNAMIC_PIPELINE = getRegister(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation("pipeline/hand_outline")
         .withUniform("HandOutlineData", UniformType.UNIFORM_BUFFER)
         .withSampler("SceneSampler")
         .withSampler("MaskSampler")
         .withVertexShader(getIdentifier("hand_outline"))
         .withFragmentShader(getIdentifier("hand_outline"))
         .withBlend(SamplerBuilderUtils.BLEND_FUNCTION)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
         .build()
   );
   public static final RenderPipeline SHADER_FOG = getShaderFog("drain", "drain");
   public static final RenderPipeline NEBULA_PIPELINE = getShaderFog("nebula", "nebula");
   private static ByteBuffer memAlloc;
   public static final RenderPipeline PLASMA_PIPELINE = getShaderFog("plasma", "plasma");
   public static final RenderPipeline BLOOM_PIPELINE = getShaderFog("bloom", "bloom");
   public static final RenderPipeline SATURN_PIPELINE = getShaderFog("saturn", "saturn");
   public static final RenderPipeline RENDER_PIPELINE = getShaderFog("aurora", "aurora");
   public static final RenderPipeline BLACK_HOLE_PIPELINE = getShaderFog("black_hole", "black_hole");

   private static RenderPipeline getShaderFog(String shaderFogText, String identifierText) {
      return getRegister(
         RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
            .withLocation("pipeline/" + shaderFogText)
            .withUniform("ShaderFogData", UniformType.UNIFORM_BUFFER)
            .withVertexShader(getIdentifier("shader_fog"))
            .withFragmentShader(getIdentifier(identifierText))
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
            .build()
      );
   }

   private static RenderPipeline getRegister(RenderPipeline renderPipeline) {
      return RenderPipelines.register(renderPipeline);
   }

   public static Identifier getIdentifier(String identifierText) {
      return Identifier.of("corzclient", "core/" + identifierText);
   }

   private SamplerBuilderUtils() {
   }

   public static void updateMemAlloc() {
      if (buffer != null) {
         buffer.close();
         buffer = null;
      }

      if (memAlloc != null) {
         MemoryUtil.memFree(memAlloc);
         memAlloc = null;
      }

      timeMillis = -1L;
   }

   public static void handleAllocMem(float allocMemValue, float allocMemValue2, float allocMemValue3, float allocMemValue4, Color color) {
      if (timeMillis < 0L) {
         timeMillis = System.currentTimeMillis();
      }

      GpuBuffer bufferAlloc = getBufferAlloc();
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.getWindow() != null) {
         float seconds = (float)(System.currentTimeMillis() - timeMillis) / 1000.0F;
         float framebufferWidth = instance.getWindow().getFramebufferWidth();
         float framebufferHeight = instance.getWindow().getFramebufferHeight();
         float radians5 = 0.0F;
         float radians6 = 0.0F;
         float allocMemValue7 = 70.0F;
         if (instance.gameRenderer != null && instance.gameRenderer.getCamera() != null) {
            Camera camera = instance.gameRenderer.getCamera();
            radians5 = (float)Math.toRadians(-camera.getYaw());
            radians6 = (float)Math.toRadians(camera.getPitch());
         }

         float allocMemValue8 = FovOptionsUtils.getValue();
         memAlloc.clear();
         memAlloc.putFloat(framebufferWidth);
         memAlloc.putFloat(framebufferHeight);
         memAlloc.putFloat(radians5);
         memAlloc.putFloat(radians6);
         memAlloc.putFloat(color.getRed() / 255.0F);
         memAlloc.putFloat(color.getGreen() / 255.0F);
         memAlloc.putFloat(color.getBlue() / 255.0F);
         memAlloc.putFloat(seconds);
         memAlloc.putFloat(allocMemValue);
         memAlloc.putFloat(allocMemValue2);
         memAlloc.putFloat(allocMemValue3);
         memAlloc.putFloat(allocMemValue4);
         memAlloc.putFloat(allocMemValue8);
         memAlloc.putFloat(0.0F);
         memAlloc.putFloat(0.0F);
         memAlloc.putFloat(0.0F);
         memAlloc.flip();
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
         commandEncoder.writeToBuffer(bufferAlloc.slice(), memAlloc);
      }
   }

   public static GpuBuffer getBufferAlloc() {
      if (buffer == null) {
         memAlloc = MemoryUtil.memAlloc(64);
         buffer = RenderSystem.getDevice().createBuffer(() -> "corzclient:shared_chams_shader_uniform", 136, 64L);
      }

      return buffer;
   }

   public static void update() {
   }
}
