package com.corz.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.blaze3d.vertex.VertexFormat.IndexType;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public final class SchemCorzUtils {
   public static final RenderPipeline build = getTestFunction("schem_preview_solid").withDepthWrite(true).build();
   private static GpuBufferHelper gpuBufferHelper;
   private static SimpleFramebuffer simpleFramebuffer;
   public static final RenderPipeline pipelinePipelinesCompile = getTestFunction("schem_preview_translucent").withBlend(BlendFunction.TRANSLUCENT).withDepthWrite(false).build();
   public static final RenderPipeline pipelineOrderFramebuffer = RenderPipeline.builder()
      .withLocation(Identifier.of("corzclient", "pipeline/schem_preview_composite"))
      .withVertexShader(Identifier.of("corzclient", "core/corz_schem_composite"))
      .withFragmentShader(Identifier.of("corzclient", "core/corz_schem_composite"))
      .withVertexFormat(VertexFormats.POSITION_TEXTURE, DrawMode.QUADS)
      .withUniform("CorzSchemComposite", UniformType.UNIFORM_BUFFER)
      .withSampler("Sampler0")
      .withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   private static int cachedSamplerMax;
   private static final ByteBuffer order = ByteBuffer.allocateDirect(80).order(ByteOrder.nativeOrder());
   private static GpuSampler sampler;
   private static GpuBufferHelper gpuBufferHelper4SchemSampler;
   private static boolean compilePipelinesFlag;
   private static int compositeSchemValue = -1;
   private static int compositeSchemValue4SchemSam = -1;
   private static int compositeValue = -1;
   private static boolean pipelinesCompileActive;
   private static boolean schemCorzActive;
   private static int compositeValue4SchemSampler = -1;
   private static int compositeSimpleValue = -1;
   private static int cachedSamplerSize = -1;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static int schemCorzValue;

   private static void handle(ByteBuffer byteBuffer, float schemCorzValue, float schemCorzValue2, float allocMemValue, float schemCorzValue4) {
      byteBuffer.putFloat(schemCorzValue).putFloat(schemCorzValue2).putFloat(0.0F).putFloat(allocMemValue).putFloat(schemCorzValue4);
   }

   public static void handleOrderFramebuffer(Matrix4f matrix, float orderFramebufferFactor, float orderFramebufferFactor2, float orderFramebufferValue, float orderFramebufferValue2, float orderFramebufferFactor3, float orderFramebufferBound) {
      if (simpleFramebuffer != null && !(orderFramebufferBound <= 0.002F) && !(orderFramebufferValue <= 0.0F) && !(orderFramebufferValue2 <= 0.0F)) {
         MinecraftClient instance = MinecraftClient.getInstance();
         float orderFramebufferFactor4 = 1.0F / CorzRoundedUtils.getFramebufferHeight();
         int framebufferWidth = instance.getWindow().getFramebufferWidth();
         int framebufferHeight = instance.getWindow().getFramebufferHeight();
         float panelSamplerValue = CorzRoundedUtils.scaledWidth > 0.0F ? CorzRoundedUtils.scaledWidth : (float)framebufferWidth / instance.getWindow().getScaleFactor();
         float orderFramebufferValue4 = CorzRoundedUtils.scaledHeight > 0.0F ? CorzRoundedUtils.scaledHeight : (float)framebufferHeight / instance.getWindow().getScaleFactor();
         float orderFramebufferBound2 = matrix.m00() * orderFramebufferFactor + matrix.m10() * orderFramebufferFactor2 + matrix.m30();
         float allocMemValue = matrix.m01() * orderFramebufferFactor + matrix.m11() * orderFramebufferFactor2 + matrix.m31();
         float orderFramebufferBound4 = matrix.m00() * (orderFramebufferFactor + orderFramebufferValue) + matrix.m10() * (orderFramebufferFactor2 + orderFramebufferValue2) + matrix.m30();
         float orderFramebufferBound5 = matrix.m01() * (orderFramebufferFactor + orderFramebufferValue) + matrix.m11() * (orderFramebufferFactor2 + orderFramebufferValue2) + matrix.m31();
         order.clear();
         handle(order, orderFramebufferBound2, orderFramebufferBound5, 0.0F, 0.0F);
         handle(order, orderFramebufferBound4, orderFramebufferBound5, 1.0F, 0.0F);
         handle(order, orderFramebufferBound4, allocMemValue, 1.0F, 1.0F);
         handle(order, orderFramebufferBound2, allocMemValue, 0.0F, 1.0F);
         order.flip();
         DynMappableUtils.Vertices mappableRing = DynMappableUtils.getMappableRing(order, 20);
         ByteBuffer byteBuffer = DynMappableUtils.getOrder(48);
         byteBuffer.putFloat(0, panelSamplerValue);
         byteBuffer.putFloat(4, orderFramebufferValue4);
         byteBuffer.putFloat(8, framebufferWidth);
         byteBuffer.putFloat(12, framebufferHeight);
         byteBuffer.putFloat(16, Math.min(orderFramebufferBound2, orderFramebufferBound4) * orderFramebufferFactor4);
         byteBuffer.putFloat(20, framebufferHeight - Math.max(allocMemValue, orderFramebufferBound5) * orderFramebufferFactor4);
         byteBuffer.putFloat(24, Math.max(orderFramebufferBound2, orderFramebufferBound4) * orderFramebufferFactor4);
         byteBuffer.putFloat(28, framebufferHeight - Math.min(allocMemValue, orderFramebufferBound5) * orderFramebufferFactor4);
         byteBuffer.putFloat(32, Math.max(0.0F, orderFramebufferFactor3 * Math.abs(matrix.m00()) * orderFramebufferFactor4));
         byteBuffer.putFloat(36, Math.min(1.0F, orderFramebufferBound));
         byteBuffer.putFloat(40, 0.0F);
         byteBuffer.putFloat(44, 0.0F);
         GpuBufferSlice slice = DynMappableUtils.getSlice(byteBuffer);
         ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
         GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(6);
         Framebuffer framebuffer = instance.getFramebuffer();

         try (RenderPass renderPass = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(
                  () -> "corz_schem_preview_composite",
                  framebuffer.getColorAttachmentView(),
                  OptionalInt.empty(),
                  framebuffer.getDepthAttachmentView(),
                  OptionalDouble.empty()
               )) {
            DynMappableUtils.samplerCorzCount++;
            renderPass.setPipeline(pipelineOrderFramebuffer);
            MatrixWindowUtils.handleDequeScissor(renderPass);
            renderPass.setUniform("CorzSchemComposite", slice);
            renderPass.bindTexture("Sampler0", simpleFramebuffer.getColorAttachmentView(), getSamplerLinear());
            renderPass.setVertexBuffer(0, mappableRing.buffer());
            renderPass.setIndexBuffer(indexBuffer, sequentialBuffer.getIndexType());
            renderPass.drawIndexed(mappableRing.baseVertex(), 0, 6, 1);
         }
      }
   }

   public static boolean isPipelinesCompile() {
      return schemCorzActive;
   }

   private static void handleIndexedDraw(RenderPass renderPass, GpuBuffer gpuBuffer, int indexedDrawValue, int indexedDrawLimit) {
      if (gpuBuffer != null && indexedDrawLimit > indexedDrawValue) {
         renderPass.setVertexBuffer(0, gpuBuffer);
         renderPass.drawIndexed(indexedDrawValue * 4, 0, (indexedDrawLimit - indexedDrawValue) * 6, 1);
      }
   }

   private SchemCorzUtils() {
   }

   public static void updateCompositeSimple() {
      gpuBufferHelper = null;
      gpuBufferHelper4SchemSampler = null;
      compositeValue4SchemSampler = -1;
      compositeValue = -1;
      compositeSchemValue4SchemSam = -1;
      compositeSchemValue = -1;
      cachedSamplerSize = -1;
      compositeSimpleValue = -1;
      if (simpleFramebuffer != null) {
         try {
            simpleFramebuffer.delete();
         } catch (Throwable e) {
         }

         simpleFramebuffer = null;
      }
   }

   private static GpuSampler getSamplerLinear() {
      if (sampler == null) {
         sampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
      }

      return sampler;
   }

   public static boolean isPipelinesCompileSchemCorzActive() {
      if (schemCorzActive) {
         return false;
      } else {
         if (!pipelinesCompileActive) {
            pipelinesCompileActive = true;

            try {
               GpuDevice device = RenderSystem.getDevice();
               compilePipelinesFlag = device.precompilePipeline(build).isValid()
                  && device.precompilePipeline(pipelinePipelinesCompile).isValid()
                  && device.precompilePipeline(pipelineOrderFramebuffer).isValid();
            } catch (Throwable e) {
               compilePipelinesFlag = false;
            }

            if (!compilePipelinesFlag) {
               handlePipelinesCompile("compile", new IllegalStateException("schematic preview pipelines did not compile"));
            }
         }

         return compilePipelinesFlag && !schemCorzActive;
      }
   }

   private static GpuBufferSlice getGpuBufferSlice(Matrix4f matrix, float gpuBufferValue, float panelSamplerValue, float gpuBufferValue3) {
      ByteBuffer order = DynMappableUtils.getOrder(80);
      matrix.get(0, order);
      order.putFloat(64, gpuBufferValue);
      order.putFloat(68, panelSamplerValue);
      order.putFloat(72, gpuBufferValue3);
      order.putFloat(76, 0.0F);
      return DynMappableUtils.getSlice(order);
   }

   public static int getDeviceTexture(int deviceTextureValue, int deviceTextureValue2) {
      int deviceTextureBound;
      try {
         deviceTextureBound = RenderSystem.getDevice().getMaxTextureSize();
      } catch (Throwable e) {
         deviceTextureBound = 4096;
      }

      int deviceTextureLimit = Math.min(deviceTextureBound, 8192);
      return deviceTextureValue * 2 <= deviceTextureLimit && deviceTextureValue2 * 2 <= deviceTextureLimit ? 2 : 1;
   }

   private static boolean canSimpleFramebuffer(int simpleFramebufferValue, int simpleFramebufferValue2) {
      if (simpleFramebuffer == null) {
         simpleFramebuffer = new SimpleFramebuffer("corz_schem_preview", simpleFramebufferValue, simpleFramebufferValue2, true);
         return true;
      } else if (simpleFramebuffer.textureWidth == simpleFramebufferValue && simpleFramebuffer.textureHeight == simpleFramebufferValue2) {
         return false;
      } else {
         simpleFramebuffer.resize(simpleFramebufferValue, simpleFramebufferValue2);
         return true;
      }
   }

   private static Builder getTestFunction(String functionText) {
      return RenderPipeline.builder()
         .withLocation(Identifier.of("corzclient", "pipeline/" + functionText))
         .withVertexShader(Identifier.of("corzclient", "core/corz_schem_model"))
         .withFragmentShader(Identifier.of("corzclient", "core/corz_schem_model"))
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .withUniform("CorzSchemModel", UniformType.UNIFORM_BUFFER)
         .withSampler("Sampler0")
         .withCull(true)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST);
   }

   public static void handlePipelinesCompile(String pipelinesCompileText, Throwable throwable) {
      if (!schemCorzActive) {
         schemCorzActive = true;
         updateCompositeSimple();
      }
   }

   public static void handleSchemSampler(
      GpuBufferHelper gpuBufferHelper3,
      int compositeSchemValue3,
      int compositeSchemValue4,
      int compositeValue3,
      int compositeValue4,
      int value3,
      int intersectsCenterBound,
      GpuBufferHelper gpuBufferHelper4,
      Matrix4f matrix,
      int schemSamplerBound,
      int rocketsBuyBound
   ) {
      int simpleFramebufferValue = Math.max(1, schemSamplerBound);
      int max4 = Math.max(1, rocketsBuyBound);
      boolean canSimpleFramebuffer2 = canSimpleFramebuffer(simpleFramebufferValue, max4);
      if (canSimpleFramebuffer2
         || gpuBufferHelper3 != gpuBufferHelper
         || gpuBufferHelper4 != gpuBufferHelper4SchemSampler
         || compositeSchemValue3 != compositeSchemValue
         || compositeSchemValue4 != compositeSchemValue4SchemSam
         || compositeValue3 != compositeValue
         || compositeValue4 != compositeValue4SchemSampler
         || value3 != compositeSimpleValue
         || intersectsCenterBound != cachedSamplerSize
         || simpleFramebufferValue != schemCorzValue
         || max4 != cachedSamplerMax
         || !MATRIX4F.equals(matrix)) {
         gpuBufferHelper = gpuBufferHelper3;
         gpuBufferHelper4SchemSampler = gpuBufferHelper4;
         compositeSchemValue = compositeSchemValue3;
         compositeSchemValue4SchemSam = compositeSchemValue4;
         compositeValue = compositeValue3;
         compositeValue4SchemSampler = compositeValue4;
         compositeSimpleValue = value3;
         cachedSamplerSize = intersectsCenterBound;
         schemCorzValue = simpleFramebufferValue;
         cachedSamplerMax = max4;
         MATRIX4F.set(matrix);
         GpuDevice device = RenderSystem.getDevice();
         CommandEncoder commandEncoder = device.createCommandEncoder();
         commandEncoder.clearColorAndDepthTextures(simpleFramebuffer.getColorAttachment(), 0, simpleFramebuffer.getDepthAttachment(), 1.0);
         GpuBuffer gpuBuffer = gpuBufferHelper3 == null ? null : gpuBufferHelper3.getGpuBuffer();
         GpuBuffer gpuBuffer2 = gpuBufferHelper3 == null ? null : gpuBufferHelper3.getGpuBuffer3();
         GpuBuffer gpuBuffer3 = gpuBufferHelper4 == null ? null : gpuBufferHelper4.getGpuBuffer();
         GpuBuffer gpuBuffer4 = gpuBufferHelper4 == null ? null : gpuBufferHelper4.getGpuBuffer3();
         int schemSamplerBound3 = gpuBuffer == null ? 0 : Math.min(compositeSchemValue3, gpuBufferHelper3.getBufferSizeBytes());
         int spotBound = gpuBuffer2 == null ? 0 : Math.min(compositeSchemValue4, gpuBufferHelper3.getValue());
         int aggroPreemptBound = gpuBuffer3 == null ? 0 : gpuBufferHelper4.getBufferSizeBytes();
         int schemSamplerBound6 = gpuBuffer4 == null ? 0 : gpuBufferHelper4.getValue();
         GpuBuffer gpuBuffer5 = gpuBufferHelper3 == null ? null : gpuBufferHelper3.getGpuBufferGpuBufferActive();
         int schemSamplerLimit = gpuBuffer5 == null ? 0 : Math.min(value3, gpuBufferHelper3.getGpuBufferValue());
         GpuTextureView gpuTextureView = null;
         GpuSampler gpuSampler = null;
         if (schemSamplerLimit > 0) {
            try {
               AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(TexturedRenderLayers.CHEST_ATLAS_TEXTURE);
               gpuTextureView = texture.getGlTextureView();
               gpuSampler = texture.getSampler() != null ? texture.getSampler() : getSamplerLinear();
            } catch (Throwable e) {
               gpuTextureView = null;
            }

            if (gpuTextureView == null) {
               schemSamplerLimit = 0;
            }
         }

         if (schemSamplerBound3 + spotBound + schemSamplerLimit + aggroPreemptBound + schemSamplerBound6 != 0) {
            AbstractTexture texture2 = MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            GpuTextureView glTextureView = texture2.getGlTextureView();
            GpuSampler gpuSampler2 = texture2.getSampler() != null ? texture2.getSampler() : getSamplerLinear();
            int max2 = Math.max(0, Math.min(compositeValue3, schemSamplerBound3));
            int max3 = Math.max(0, Math.min(compositeValue4, spotBound));
            int indexedDrawLimit = Math.max(0, Math.min(intersectsCenterBound, schemSamplerLimit));
            GpuBufferSlice gpuBufferSlice = getGpuBufferSlice(matrix, 0.5F, 1.0F, 1.0F);
            GpuBufferSlice gpuBufferSlice2 = max2 <= 0 && indexedDrawLimit <= 0 ? null : getGpuBufferSlice(matrix, 0.5F, 1.0F, 0.5F);
            GpuBufferSlice gpuBufferSlice3 = getGpuBufferSlice(matrix, 0.004F, 0.0F, 1.0F);
            GpuBufferSlice gpuBufferSlice4 = max3 > 0 ? getGpuBufferSlice(matrix, 0.004F, 0.0F, 0.5F) : null;
            int schemSamplerValue = Math.max(Math.max(Math.max(schemSamplerBound3, spotBound), schemSamplerLimit), Math.max(aggroPreemptBound, schemSamplerBound6));
            ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
            GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(schemSamplerValue * 6);
            IndexType indexType = sequentialBuffer.getIndexType();

            try (RenderPass renderPass = commandEncoder.createRenderPass(
                  () -> "corz_schem_preview",
                  simpleFramebuffer.getColorAttachmentView(),
                  OptionalInt.empty(),
                  simpleFramebuffer.getDepthAttachmentView(),
                  OptionalDouble.empty()
               )) {
               DynMappableUtils.samplerCorzCount++;
               if (schemSamplerBound3 + aggroPreemptBound + schemSamplerLimit > 0) {
                  renderPass.setPipeline(build);
                  renderPass.bindTexture("Sampler0", glTextureView, gpuSampler2);
                  renderPass.setIndexBuffer(indexBuffer, indexType);
                  if (max2 > 0) {
                     renderPass.setUniform("CorzSchemModel", gpuBufferSlice2);
                     handleIndexedDraw(renderPass, gpuBuffer, 0, max2);
                  }

                  renderPass.setUniform("CorzSchemModel", gpuBufferSlice);
                  handleIndexedDraw(renderPass, gpuBuffer, max2, schemSamplerBound3);
                  handleIndexedDraw(renderPass, gpuBuffer3, 0, aggroPreemptBound);
                  if (schemSamplerLimit > 0) {
                     renderPass.bindTexture("Sampler0", gpuTextureView, gpuSampler);
                     if (indexedDrawLimit > 0) {
                        renderPass.setUniform("CorzSchemModel", gpuBufferSlice2);
                        handleIndexedDraw(renderPass, gpuBuffer5, 0, indexedDrawLimit);
                     }

                     renderPass.setUniform("CorzSchemModel", gpuBufferSlice);
                     handleIndexedDraw(renderPass, gpuBuffer5, indexedDrawLimit, schemSamplerLimit);
                  }
               }

               if (spotBound + schemSamplerBound6 > 0) {
                  renderPass.setPipeline(pipelinePipelinesCompile);
                  renderPass.bindTexture("Sampler0", glTextureView, gpuSampler2);
                  renderPass.setIndexBuffer(indexBuffer, indexType);
                  if (max3 > 0) {
                     renderPass.setUniform("CorzSchemModel", gpuBufferSlice4);
                     handleIndexedDraw(renderPass, gpuBuffer2, 0, max3);
                  }

                  renderPass.setUniform("CorzSchemModel", gpuBufferSlice3);
                  handleIndexedDraw(renderPass, gpuBuffer2, max3, spotBound);
                  handleIndexedDraw(renderPass, gpuBuffer4, 0, schemSamplerBound6);
               }
            }
         }
      }
   }
}
