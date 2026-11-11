package com.corz.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
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
import net.minecraft.client.render.RawProjectionMatrix;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.fog.FogRenderer.FogType;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

final class RenderMatrix {
   private static FogRenderer fogRenderer;
   private GpuBuffer buffer;
   private GpuTexture texture;
   private final float[] sliceTable;
   private final String textureCreateText;
   private int textureWidth;
   private int minMinMaxTextureSize;
   private static GpuSampler sampler;
   private GpuTextureView textureView;
   private GpuTexture textureTextureCreate;
   private ByteBuffer order;
   private RawProjectionMatrix rawProjectionMatrix;
   private final Matrix4f shaderProjectionMatrix4f;
   private final ByteBuffer orderOrderView;
   private final float[] matrixTable;
   private GpuTextureView textureViewTextureCreate;
   private final MatrixStack matrixStack = new MatrixStack();

   private GpuBufferSlice getSlice() {
      if (this.buffer == null) {
         ByteBuffer order = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());

         for (int i = 0; i < 3; i++) {
            order.putFloat(i * 4, this.matrixTable[i]);
            order.putFloat(16 + i * 4, this.sliceTable[i]);
         }

         this.buffer = RenderSystem.getDevice().createBuffer(() -> "corz xmas stage lights " + this.textureCreateText, 128, order);
      }

      return this.buffer.slice();
   }

   void updateTextureEncoder() {
      if (this.textureTextureCreate != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.textureTextureCreate, 1.0);
      }
   }

   void handleShaderProjection(int shaderProjectionValue, int shaderProjectionValue2, int shaderProjectionLimit, int rocketsBuyBound, Matrix4f matrix, StageKnife.Scene scene) {
      if (this.texture != null && shaderProjectionLimit > 0 && rocketsBuyBound > 0) {
         float x = (float)shaderProjectionLimit / this.textureWidth;
         float y = (float)rocketsBuyBound / this.minMinMaxTextureSize;
         float x2 = (2.0F * shaderProjectionValue + shaderProjectionLimit) / this.textureWidth - 1.0F;
         float y2 = 1.0F - (2.0F * shaderProjectionValue2 + rocketsBuyBound) / this.minMinMaxTextureSize;
         this.shaderProjectionMatrix4f.identity().translate(x2, y2, 0.0F).scale(x, y, 1.0F).mul(matrix);
         if (this.rawProjectionMatrix == null) {
            this.rawProjectionMatrix = new RawProjectionMatrix("corz xmas stage " + this.textureCreateText);
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
            RenderSystem.outputDepthTextureOverride = this.textureViewTextureCreate;
            RenderSystem.setProjectionMatrix(this.rawProjectionMatrix.set(this.shaderProjectionMatrix4f), ProjectionType.PERSPECTIVE);
            RenderSystem.setShaderFog(getFogBuffer());
            RenderSystem.setShaderLights(this.getSlice());
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

   void handleOrderView(
      Matrix4f matrix,
      float orderViewFactor,
      float orderViewFactor2,
      float orderViewValue,
      float orderViewValue2,
      float allocMemValue,
      float orderViewValue4,
      float orderViewValue5,
      float orderViewValue6,
      float bufferSamplerBound,
      float pilotStatusBound,
      float matrixRenderFactor,
      float ctxOffset,
      float bufferSamplerFactor,
      float matrixRenderValue
   ) {
      if (this.textureView != null && !(matrixRenderValue <= 0.002F) && !(orderViewValue <= 0.0F) && !(orderViewValue2 <= 0.0F)) {
         float orderViewValue13 = matrix.m00() * orderViewFactor + matrix.m30();
         float orderViewValue14 = matrix.m11() * orderViewFactor2 + matrix.m31();
         float matrixValue = matrix.m00() * (orderViewFactor + orderViewValue) + matrix.m30();
         float orderViewValue16 = matrix.m11() * (orderViewFactor2 + orderViewValue2) + matrix.m31();
         this.orderOrderView.clear();
         handle(this.orderOrderView, orderViewValue13, orderViewValue16, allocMemValue, orderViewValue6);
         handle(this.orderOrderView, matrixValue, orderViewValue16, orderViewValue5, orderViewValue6);
         handle(this.orderOrderView, matrixValue, orderViewValue14, orderViewValue5, orderViewValue4);
         handle(this.orderOrderView, orderViewValue13, orderViewValue14, allocMemValue, orderViewValue4);
         this.orderOrderView.flip();
         this.handleBufferSampler(this.orderOrderView, 1, bufferSamplerBound, pilotStatusBound, matrixRenderFactor, ctxOffset, bufferSamplerFactor, matrixRenderValue);
      }
   }

   private void handleBufferSampler(ByteBuffer byteBuffer, int bufferSamplerValue, float bufferSamplerBound, float pilotStatusBound, float matrixRenderFactor, float rocketsElytraBound, float bufferSamplerFactor, float matrixRenderValue) {
      MinecraftClient instance = MinecraftClient.getInstance();
      int framebufferWidth = instance.getWindow().getFramebufferWidth();
      int framebufferHeight = instance.getWindow().getFramebufferHeight();
      float scaleFactor = instance.getWindow().getScaleFactor();
      float panelSamplerValue = CorzRoundedUtils.scaledWidth > 0.0F ? CorzRoundedUtils.scaledWidth : framebufferWidth / scaleFactor;
      float bufferSamplerDivisor = CorzRoundedUtils.scaledHeight > 0.0F ? CorzRoundedUtils.scaledHeight : framebufferHeight / scaleFactor;
      float bufferSamplerFactor2 = framebufferHeight / bufferSamplerDivisor;
      DynMappableUtils.Vertices mappableRing = DynMappableUtils.getMappableRing(byteBuffer, 20);
      ByteBuffer order = DynMappableUtils.getOrder(48);
      order.putFloat(0, panelSamplerValue);
      order.putFloat(4, bufferSamplerDivisor);
      order.putFloat(8, framebufferWidth);
      order.putFloat(12, framebufferHeight);
      order.putFloat(16, Math.min(bufferSamplerBound, matrixRenderFactor) * bufferSamplerFactor2);
      order.putFloat(20, framebufferHeight - Math.max(pilotStatusBound, rocketsElytraBound) * bufferSamplerFactor2);
      order.putFloat(24, Math.max(bufferSamplerBound, matrixRenderFactor) * bufferSamplerFactor2);
      order.putFloat(28, framebufferHeight - Math.min(pilotStatusBound, rocketsElytraBound) * bufferSamplerFactor2);
      order.putFloat(32, Math.max(0.0F, bufferSamplerFactor * bufferSamplerFactor2));
      order.putFloat(36, Math.min(1.0F, matrixRenderValue));
      order.putFloat(40, 0.0F);
      order.putFloat(44, 0.0F);
      GpuBufferSlice slice = DynMappableUtils.getSlice(order);
      ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
      GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(bufferSamplerValue * 6);
      Framebuffer framebuffer = instance.getFramebuffer();

      try (RenderPass renderPass = RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(
               () -> "corz_xmas_stage_composite", framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty()
            )) {
         DynMappableUtils.samplerCorzCount++;
         renderPass.setPipeline(StageKnife.build);
         MatrixWindowUtils.handleDequeScissor(renderPass);
         renderPass.setUniform("CorzKnifeStage", slice);
         renderPass.bindTexture("Sampler0", this.textureView, getSamplerLinear());
         renderPass.setVertexBuffer(0, mappableRing.buffer());
         renderPass.setIndexBuffer(indexBuffer, sequentialBuffer.getIndexType());
         renderPass.drawIndexed(mappableRing.baseVertex(), 0, bufferSamplerValue * 6, 1);
      }
   }

   void handleTextureCreate(int textureCreateBound, int rocketsBuyBound) {
      int maxNeedH = Math.max(1, textureCreateBound);
      int maxNeedW = Math.max(1, rocketsBuyBound);
      GpuDevice device = RenderSystem.getDevice();
      int minMaxTextureSize = Math.min(8192, device.getMaxTextureSize());
      int textureCreateLimit = Math.min(maxNeedH, minMaxTextureSize);
      int matrixValue = Math.min(maxNeedW, minMaxTextureSize);
      int textureCreateValue = Math.min(minMaxTextureSize, getValue(textureCreateLimit));
      int min2 = Math.min(minMaxTextureSize, getValue(matrixValue));
      if (this.texture == null || textureCreateLimit > this.textureWidth || matrixValue > this.minMinMaxTextureSize || textureCreateValue <= this.textureWidth / 2 || min2 <= this.minMinMaxTextureSize / 2) {
         this.updateTextureView();
         this.textureWidth = Math.min(minMaxTextureSize, getValue(textureCreateLimit));
         this.minMinMaxTextureSize = Math.min(minMaxTextureSize, getValue(matrixValue));
         this.texture = device.createTexture(() -> "corz xmas stage " + this.textureCreateText, 12, TextureFormat.RGBA8, this.textureWidth, this.minMinMaxTextureSize, 1, 1);
         this.textureView = device.createTextureView(this.texture);
         this.textureTextureCreate = device.createTexture(() -> "corz xmas stage depth " + this.textureCreateText, 8, TextureFormat.DEPTH32, this.textureWidth, this.minMinMaxTextureSize, 1, 1);
         this.textureViewTextureCreate = device.createTextureView(this.textureTextureCreate);
      }

      device.createCommandEncoder().clearColorAndDepthTextures(this.texture, 0, this.textureTextureCreate, 1.0);
   }

   private static GpuSampler getSamplerLinear() {
      if (sampler == null) {
         sampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
      }

      return sampler;
   }

   RenderMatrix(String text, float[] amounts, float[] amounts2) {
      this.shaderProjectionMatrix4f = new Matrix4f();
      this.orderOrderView = ByteBuffer.allocateDirect(80).order(ByteOrder.nativeOrder());
      this.textureCreateText = text;
      this.matrixTable = amounts;
      this.sliceTable = amounts2;
   }

   private static void handle(ByteBuffer byteBuffer, float matrixValue, float matrixValue2, float allocMemValue, float matrixValue4) {
      byteBuffer.putFloat(matrixValue).putFloat(matrixValue2).putFloat(0.0F).putFloat(allocMemValue).putFloat(matrixValue4);
   }

   private void handleOrder(Matrix4f matrix, float orderFactor, float orderFactor2, float orderValue, float orderValue2, float orderDivisor, float ctxOffset, int maxRound, int rocketsBuyBound) {
      float matrixValue = matrix.m00() * orderFactor + matrix.m30();
      float allocMemValue = matrix.m11() * orderFactor2 + matrix.m31();
      handle(this.order, matrixValue, allocMemValue, this.getMinForFloat((orderFactor - orderValue) / orderDivisor * maxRound), this.getMin((orderFactor2 - orderValue2) / ctxOffset * rocketsBuyBound));
   }

   void handleOrderWindow(Matrix4f matrix, float orderWindowValue, float orderWindowValue2, float[] orderWindowValues, float[] orderWindowValues2, int orderWindowLimit, float orderValue, float orderWindowValue4, float orderDivisor, float ctxOffset, int maxRound, int rocketsBuyBound, float matrixRenderValue) {
      if (this.textureView != null && !(matrixRenderValue <= 0.002F) && orderWindowLimit >= 3 && !(orderDivisor <= 0.0F) && !(ctxOffset <= 0.0F)) {
         int ticks = orderWindowLimit * 4 * 20;
         if (this.order == null || this.order.capacity() < ticks) {
            this.order = ByteBuffer.allocateDirect(ticks).order(ByteOrder.nativeOrder());
         }

         this.order.clear();

         for (int i = 0; i < orderWindowLimit; i++) {
            int index = (i + 1) % orderWindowLimit;
            this.handleOrder(matrix, orderWindowValue, orderWindowValue2, orderValue, orderWindowValue4, orderDivisor, ctxOffset, maxRound, rocketsBuyBound);
            this.handleOrder(matrix, orderWindowValues[i], orderWindowValues2[i], orderValue, orderWindowValue4, orderDivisor, ctxOffset, maxRound, rocketsBuyBound);
            this.handleOrder(matrix, orderWindowValues[index], orderWindowValues2[index], orderValue, orderWindowValue4, orderDivisor, ctxOffset, maxRound, rocketsBuyBound);
            this.handleOrder(matrix, orderWindowValues[index], orderWindowValues2[index], orderValue, orderWindowValue4, orderDivisor, ctxOffset, maxRound, rocketsBuyBound);
         }

         this.order.flip();
         MinecraftClient instance = MinecraftClient.getInstance();
         float scaleFactor = instance.getWindow().getScaleFactor();
         float matrixRenderFactor = instance.getWindow().getFramebufferWidth() / scaleFactor;
         float rocketsElytraBound = instance.getWindow().getFramebufferHeight() / scaleFactor;
         this.handleBufferSampler(this.order, orderWindowLimit, 0.0F, 0.0F, matrixRenderFactor, rocketsElytraBound, 0.0F, matrixRenderValue);
      }
   }

   private void updateTextureView() {
      if (this.textureView != null) {
         this.textureView.close();
      }

      if (this.texture != null) {
         this.texture.close();
      }

      if (this.textureViewTextureCreate != null) {
         this.textureViewTextureCreate.close();
      }

      if (this.textureTextureCreate != null) {
         this.textureTextureCreate.close();
      }

      this.textureView = null;
      this.texture = null;
      this.textureViewTextureCreate = null;
      this.textureTextureCreate = null;
      this.textureWidth = 0;
      this.minMinMaxTextureSize = 0;
   }

   static boolean isFlag() {
      return StageKnife.isValidPrecompile();
   }

   float getMin(float matrixValue) {
      return 1.0F - matrixValue / Math.max(1, this.minMinMaxTextureSize);
   }

   int getMinMinMaxTextureSize() {
      return this.minMinMaxTextureSize;
   }

   private static int getValue(int matrixValue) {
      return (matrixValue + 64 - 1) / 64 * 64;
   }

   static float getAmount(float matrixFactor, float ctxOffset, float matrixBound) {
      float matrixDivisor = Math.max(1.0F, matrixFactor * ctxOffset);
      return Math.min(matrixBound, (float)Math.sqrt(4200000.0F / matrixDivisor));
   }

   float getMinForFloat(float matrixValue) {
      return matrixValue / Math.max(1, this.textureWidth);
   }

   private static GpuBufferSlice getFogBuffer() {
      if (fogRenderer == null) {
         fogRenderer = new FogRenderer();
      }

      return fogRenderer.getFogBuffer(FogType.NONE);
   }

   int getMin() {
      return this.textureWidth;
   }

   boolean isTexture() {
      return this.texture != null;
   }

   void updateProjectionRaw() {
      this.updateTextureView();
      if (this.rawProjectionMatrix != null) {
         this.rawProjectionMatrix.close();
      }

      this.rawProjectionMatrix = null;
      if (this.buffer != null) {
         this.buffer.close();
      }

      this.buffer = null;
   }
}
