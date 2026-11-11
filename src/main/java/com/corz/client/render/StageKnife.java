package com.corz.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.RawProjectionMatrix;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.fog.FogRenderer.FogType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class StageKnife {
   private static final float[] sliceTable = new float[]{-0.42F, 0.78F, 0.62F};
   private final String textureCreateText;
   private GpuTexture texture;
   private static GpuBuffer buffer;
   private int textureHeight;
   private GpuTexture textureTextureClose;
   private RawProjectionMatrix rawProjectionMatrix;
   private final Matrix4f shaderProjectionMatrix4f;
   private GpuTextureView textureView;
   private static Boolean valid;
   private GpuTextureView textureViewTextureClose;
   private static final float[] stageKnifeAmounts = new float[]{0.52F, 0.08F, 0.3F};
   private final ByteBuffer order;
   private static int cachedMaxTextureSize;
   private static FogRenderer fogRenderer;
   private int minMinMaxTextureSize;
   private final MatrixStack matrixStack = new MatrixStack();
   private static GpuSampler sampler;
   public static final RenderPipeline build = RenderPipeline.builder()
      .withLocation(Identifier.of("corzclient", "pipeline/knife_stage"))
      .withVertexShader(Identifier.of("corzclient", "core/corz_knife_stage"))
      .withFragmentShader(Identifier.of("corzclient", "core/corz_knife_stage"))
      .withVertexFormat(VertexFormats.POSITION_TEXTURE, DrawMode.QUADS)
      .withUniform("CorzKnifeStage", UniformType.UNIFORM_BUFFER)
      .withSampler("Sampler0")
      .withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withDepthWrite(false)
      .build();

   public float getMin(int stageKnifeValue) {
      return (float)stageKnifeValue / Math.max(1, this.minMinMaxTextureSize);
   }

   public int getMin() {
      return this.textureHeight;
   }

   public static int getMaxDevice() {
      if (cachedMaxTextureSize <= 0) {
         cachedMaxTextureSize = Math.max(64, Math.min(8192, RenderSystem.getDevice().getMaxTextureSize()));
      }

      return cachedMaxTextureSize;
   }

   private static GpuBufferSlice getFogBuffer() {
      if (fogRenderer == null) {
         fogRenderer = new FogRenderer();
      }

      return fogRenderer.getFogBuffer(FogType.NONE);
   }

   public void handleShaderProjection(int shaderProjectionValue, int shaderProjectionValue2, int shaderProjectionLimit, int botsMatrixDivisor, Matrix4f matrix, StageKnife.Scene scene) {
      if (this.texture != null && shaderProjectionLimit > 0 && botsMatrixDivisor > 0) {
         float x = (float)shaderProjectionLimit / this.minMinMaxTextureSize;
         float y = (float)botsMatrixDivisor / this.textureHeight;
         float x2 = (2.0F * shaderProjectionValue + shaderProjectionLimit) / this.minMinMaxTextureSize - 1.0F;
         float y2 = 1.0F - (2.0F * shaderProjectionValue2 + botsMatrixDivisor) / this.textureHeight;
         this.shaderProjectionMatrix4f.identity().translate(x2, y2, 0.0F).scale(x, y, 1.0F).mul(matrix);
         if (this.rawProjectionMatrix == null) {
            this.rawProjectionMatrix = new RawProjectionMatrix("corz knife stage " + this.textureCreateText);
         }

         MinecraftClient instance = MinecraftClient.getInstance();
         GpuTextureView gpuTextureView = RenderSystem.outputColorTextureOverride;
         GpuTextureView outputDepthTextureOverride = RenderSystem.outputDepthTextureOverride;
         GpuBufferSlice projectionMatrixBuffer = RenderSystem.getProjectionMatrixBuffer();
         ProjectionType projectionType = RenderSystem.getProjectionType();
         GpuBufferSlice shaderFog = RenderSystem.getShaderFog();
         GpuBufferSlice shaderLights = RenderSystem.getShaderLights();
         ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
         boolean enabled = scissorState.isEnabled();
         int x3 = scissorState.getX();
         int y3 = scissorState.getY();
         int width = scissorState.getWidth();
         int height = scissorState.getHeight();
         Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
         modelViewStack.pushMatrix();

         try {
            modelViewStack.identity();
            if (enabled) {
               RenderSystem.disableScissorForRenderTypeDraws();
            }

            RenderSystem.outputColorTextureOverride = this.textureView;
            RenderSystem.outputDepthTextureOverride = this.textureViewTextureClose;
            RenderSystem.setProjectionMatrix(this.rawProjectionMatrix.set(this.shaderProjectionMatrix4f), ProjectionType.PERSPECTIVE);
            RenderSystem.setShaderFog(getFogBuffer());
            RenderSystem.setShaderLights(getSlice());
            RenderDispatcher entityRenderDispatcher = instance.gameRenderer.getEntityRenderDispatcher();
            this.matrixStack.loadIdentity();
            boolean shaderProjectionFlag = false ;

            try {
               shaderProjectionFlag = true;
               scene.draw(this.matrixStack, entityRenderDispatcher.getQueue());
               shaderProjectionFlag = false;
            } finally {
               if (shaderProjectionFlag) {
                  try {
                     entityRenderDispatcher.render();
                  } finally {
                     instance.getBufferBuilders().getEntityVertexConsumers().draw();
                  }
               }
            }

            try {
               entityRenderDispatcher.render();
            } finally {
               instance.getBufferBuilders().getEntityVertexConsumers().draw();
            }
         } finally {
            RenderSystem.outputColorTextureOverride = gpuTextureView;
            RenderSystem.outputDepthTextureOverride = outputDepthTextureOverride;
            if (projectionMatrixBuffer != null) {
               RenderSystem.setProjectionMatrix(projectionMatrixBuffer, projectionType);
            }

            if (shaderFog != null) {
               RenderSystem.setShaderFog(shaderFog);
            }

            if (shaderLights != null) {
               RenderSystem.setShaderLights(shaderLights);
            }

            if (enabled) {
               RenderSystem.enableScissorForRenderTypeDraws(x3, y3, width, height);
            }

            modelViewStack.popMatrix();
         }
      }
   }

   private static int getValue(int stageKnifeValue) {
      return (stageKnifeValue + 64 - 1) / 64 * 64;
   }

   public StageKnife(String text) {
      this.shaderProjectionMatrix4f = new Matrix4f();
      this.order = ByteBuffer.allocateDirect(80).order(ByteOrder.nativeOrder());
      this.textureCreateText = text;
   }

   public void handleBufferSampler(
      Matrix4f matrix,
      float bufferSamplerFactor,
      float peekPositionFactor,
      float bufferSamplerValue,
      float bufferSamplerValue2,
      float allocMemValue,
      float bufferSamplerValue4,
      float bufferSamplerValue5,
      float bufferSamplerValue6,
      float bufferSamplerBound,
      float pilotStatusBound,
      float bufferSamplerBound3,
      float rocketsElytraBound,
      float bufferSamplerFactor3,
      float hashHuntBound
   ) {
      if (this.textureView != null && !(hashHuntBound <= 0.002F) && !(bufferSamplerValue <= 0.0F) && !(bufferSamplerValue2 <= 0.0F)) {
         MinecraftClient instance = MinecraftClient.getInstance();
         int framebufferWidth = instance.getWindow().getFramebufferWidth();
         int framebufferHeight = instance.getWindow().getFramebufferHeight();
         float scaleFactor = instance.getWindow().getScaleFactor();
         float panelSamplerValue = CorzRoundedUtils.scaledWidth > 0.0F ? CorzRoundedUtils.scaledWidth : framebufferWidth / scaleFactor;
         float bufferSamplerDivisor = CorzRoundedUtils.scaledHeight > 0.0F ? CorzRoundedUtils.scaledHeight : framebufferHeight / scaleFactor;
         float bufferSamplerFactor4 = framebufferHeight / bufferSamplerDivisor;
         float bufferSamplerValue8 = matrix.m00() * bufferSamplerFactor + matrix.m30();
         float bufferSamplerValue9 = matrix.m11() * peekPositionFactor + matrix.m31();
         float orderValue = matrix.m00() * (bufferSamplerFactor + bufferSamplerValue) + matrix.m30();
         float bufferSamplerValue11 = matrix.m11() * (peekPositionFactor + bufferSamplerValue2) + matrix.m31();
         this.order.clear();
         this.handleOrder(bufferSamplerValue8, bufferSamplerValue11, allocMemValue, bufferSamplerValue6);
         this.handleOrder(orderValue, bufferSamplerValue11, bufferSamplerValue5, bufferSamplerValue6);
         this.handleOrder(orderValue, bufferSamplerValue9, bufferSamplerValue5, bufferSamplerValue4);
         this.handleOrder(bufferSamplerValue8, bufferSamplerValue9, allocMemValue, bufferSamplerValue4);
         this.order.flip();
         DynMappableUtils.Vertices mappableRing = DynMappableUtils.getMappableRing(this.order, 20);
         ByteBuffer order = DynMappableUtils.getOrder(48);
         order.putFloat(0, panelSamplerValue);
         order.putFloat(4, bufferSamplerDivisor);
         order.putFloat(8, framebufferWidth);
         order.putFloat(12, framebufferHeight);
         order.putFloat(16, Math.min(bufferSamplerBound, bufferSamplerBound3) * bufferSamplerFactor4);
         order.putFloat(20, framebufferHeight - Math.max(pilotStatusBound, rocketsElytraBound) * bufferSamplerFactor4);
         order.putFloat(24, Math.max(bufferSamplerBound, bufferSamplerBound3) * bufferSamplerFactor4);
         order.putFloat(28, framebufferHeight - Math.min(pilotStatusBound, rocketsElytraBound) * bufferSamplerFactor4);
         order.putFloat(32, Math.max(0.0F, bufferSamplerFactor3 * bufferSamplerFactor4));
         order.putFloat(36, Math.min(1.0F, hashHuntBound));
         order.putFloat(40, 0.0F);
         order.putFloat(44, 0.0F);
         GpuBufferSlice slice = DynMappableUtils.getSlice(order);
         ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
         GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(6);
         Framebuffer framebuffer = instance.getFramebuffer();

         try (RenderPass renderPass = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(
                  () -> "corz_knife_stage_composite",
                  framebuffer.getColorAttachmentView(),
                  OptionalInt.empty(),
                  framebuffer.getDepthAttachmentView(),
                  OptionalDouble.empty()
               )) {
            DynMappableUtils.samplerCorzCount++;
            renderPass.setPipeline(build);
            MatrixWindowUtils.handleDequeScissor(renderPass);
            renderPass.setUniform("CorzKnifeStage", slice);
            renderPass.bindTexture("Sampler0", this.textureView, getSamplerLinear());
            renderPass.setVertexBuffer(0, mappableRing.buffer());
            renderPass.setIndexBuffer(indexBuffer, sequentialBuffer.getIndexType());
            renderPass.drawIndexed(mappableRing.baseVertex(), 0, 6, 1);
         }
      }
   }

   public void updateTextureClose() {
      if (this.textureView != null) {
         this.textureView.close();
      }

      if (this.texture != null) {
         this.texture.close();
      }

      if (this.textureViewTextureClose != null) {
         this.textureViewTextureClose.close();
      }

      if (this.textureTextureClose != null) {
         this.textureTextureClose.close();
      }

      if (this.rawProjectionMatrix != null) {
         this.rawProjectionMatrix.close();
      }

      this.rawProjectionMatrix = null;
      this.textureView = null;
      this.texture = null;
      this.textureViewTextureClose = null;
      this.textureTextureClose = null;
      this.minMinMaxTextureSize = 0;
      this.textureHeight = 0;
   }

   public int getMinMinMaxTextureSize() {
      return this.minMinMaxTextureSize;
   }

   private void handleOrder(float orderValue, float orderValue2, float allocMemValue, float orderValue4) {
      this.order.putFloat(orderValue).putFloat(orderValue2).putFloat(0.0F).putFloat(allocMemValue).putFloat(orderValue4);
   }

   public float getMinForInt(int stageKnifeValue) {
      return 1.0F - (float)stageKnifeValue / Math.max(1, this.textureHeight);
   }

   public void handleTextureCreate(int textureCreateBound, int botsMatrixDivisor) {
      int maxNeedH = Math.max(1, textureCreateBound);
      int maxNeedW = Math.max(1, botsMatrixDivisor);
      GpuDevice device = RenderSystem.getDevice();
      int minMaxTextureSize = Math.min(8192, device.getMaxTextureSize());
      int textureCreateLimit = Math.min(maxNeedH, minMaxTextureSize);
      int stageKnifeValue = Math.min(maxNeedW, minMaxTextureSize);
      if (this.texture == null || textureCreateLimit > this.minMinMaxTextureSize || stageKnifeValue > this.textureHeight) {
         this.updateTextureClose();
         this.minMinMaxTextureSize = Math.min(minMaxTextureSize, getValue(textureCreateLimit));
         this.textureHeight = Math.min(minMaxTextureSize, getValue(stageKnifeValue));
         this.texture = device.createTexture(() -> "corz knife stage " + this.textureCreateText, 12, TextureFormat.RGBA8, this.minMinMaxTextureSize, this.textureHeight, 1, 1);
         this.textureView = device.createTextureView(this.texture);
         this.textureTextureClose = device.createTexture(() -> "corz knife stage depth " + this.textureCreateText, 8, TextureFormat.DEPTH32, this.minMinMaxTextureSize, this.textureHeight, 1, 1);
         this.textureViewTextureClose = device.createTextureView(this.textureTextureClose);
      }

      device.createCommandEncoder().clearColorAndDepthTextures(this.texture, 0, this.textureTextureClose, 1.0);
   }

   public static boolean isValidPrecompile() {
      if (valid == null) {
         try {
            valid = RenderSystem.getDevice().precompilePipeline(build).isValid();
         } catch (Throwable e) {
            valid = false;
         }
      }

      return valid;
   }

   private static GpuBufferSlice getSlice() {
      if (buffer == null) {
         ByteBuffer order = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());

         for (int i = 0; i < 3; i++) {
            order.putFloat(i * 4, sliceTable[i]);
            order.putFloat(16 + i * 4, stageKnifeAmounts[i]);
         }

         buffer = RenderSystem.getDevice().createBuffer(() -> "corz knife stage lights", 128, order);
      }

      return buffer.slice();
   }

   private static GpuSampler getSamplerLinear() {
      if (sampler == null) {
         sampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
      }

      return sampler;
   }

   public interface Scene {
      void draw(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue);
   }
}
