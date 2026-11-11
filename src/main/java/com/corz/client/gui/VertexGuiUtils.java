package com.corz.client.gui;

import com.corz.client.render.DynMappableUtils;
import com.corz.client.render.DynMappableUtils.Vertices;
import com.corz.client.render.MatrixWindowUtils;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
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
import java.util.EnumMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.BuiltBuffer.DrawParameters;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import ai.auth.Module;

public final class VertexGuiUtils {
   private static BufferBuilder bufferBuilder;
   private static DrawMode triangles;
   private static final Map<DrawMode, RenderPipeline> PIPELINE_CACHE = new EnumMap<>(DrawMode.class);
   private static BufferBuilder begin;
   private static int batchDepth;
   private static final Matrix4f MATRIX4F = new Matrix4f();

   public static void updateBufferBuilder() {
      if (batchDepth > 0) {
         if (--batchDepth <= 0) {
            BufferBuilder bufferBuilder2 = bufferBuilder;
            bufferBuilder = null;
            if (bufferBuilder2 != null && begin == bufferBuilder2) {
               updateBufferClose();
            }
         }
      }
   }

   public static void handleVertexColor(VertexConsumer vertices, float vertexColorValue, float configsLikeValue, float customFlameFactor, float vertexColorValue4, int panelOverlay) {
      Matrix4f positionMatrix = getPositionMatrix();
      vertices.vertex(positionMatrix, vertexColorValue, configsLikeValue, 0.0F).color(panelOverlay);
      vertices.vertex(positionMatrix, vertexColorValue, vertexColorValue4, 0.0F).color(panelOverlay);
      vertices.vertex(positionMatrix, customFlameFactor, vertexColorValue4, 0.0F).color(panelOverlay);
      vertices.vertex(positionMatrix, customFlameFactor, configsLikeValue, 0.0F).color(panelOverlay);
   }

   public static VertexConsumer getBeginBuffer(DrawMode drawMode) {
      if (bufferBuilder != null) {
         BufferBuilder bufferBuilder2 = bufferBuilder;
         bufferBuilder = null;
         if (begin == bufferBuilder2) {
            updateBufferClose();
         }
      }

      begin = Tessellator.getInstance().begin(drawMode, VertexFormats.POSITION_COLOR);
      triangles = drawMode;
      return begin;
   }

   public static VertexConsumer getBufferBuilder() {
      if (bufferBuilder == null || begin != bufferBuilder) {
         getBeginBuffer(DrawMode.TRIANGLES);
         bufferBuilder = begin;
      }

      return bufferBuilder;
   }

   private static RenderPipeline getIfAbsent(DrawMode drawMode) {
      return PIPELINE_CACHE.computeIfAbsent(
         drawMode,
         drawMode2 -> {
            RenderPipeline build = RenderPipeline.builder()
               .withLocation(Identifier.of("corzclient", "pipeline/gui_color_" + drawMode2.name().toLowerCase()))
               .withVertexShader(Identifier.of("corzclient", "core/corz_gui_color"))
               .withFragmentShader(Identifier.of("corzclient", "core/corz_gui_color"))
               .withVertexFormat(VertexFormats.POSITION_COLOR, drawMode2)
               .withUniform("CorzGui", UniformType.UNIFORM_BUFFER)
               .withBlend(BlendFunction.TRANSLUCENT)
               .withCull(false)
               .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
               .withDepthWrite(false)
               .build();
            RenderPipelines.register(build);
            return build;
         }
      );
   }

   public static VertexConsumer getQuads() {
      return getBeginBuffer(DrawMode.QUADS);
   }

   public static void update() {
      batchDepth++;
   }

   public static void handleVertexColor(VertexConsumer vertices, float vertexColorValue, float vertexColorValue2, float vertexColorValue3, float vertexColorValue4, float vertexColorValue5, float vertexColorValue6, float vertexColorValue7, float vertexColorValue8, int vertexColorValue9) {
      Matrix4f positionMatrix = getPositionMatrix();
      vertices.vertex(positionMatrix, vertexColorValue, vertexColorValue2, 0.0F).color(vertexColorValue9);
      vertices.vertex(positionMatrix, vertexColorValue3, vertexColorValue4, 0.0F).color(vertexColorValue9);
      vertices.vertex(positionMatrix, vertexColorValue5, vertexColorValue6, 0.0F).color(vertexColorValue9);
      vertices.vertex(positionMatrix, vertexColorValue7, vertexColorValue8, 0.0F).color(vertexColorValue9);
   }

   public static void updateTriangleFan() {
      getIfAbsent(DrawMode.TRIANGLE_FAN);
      getIfAbsent(DrawMode.TRIANGLE_STRIP);
      getIfAbsent(DrawMode.QUADS);
      getIfAbsent(DrawMode.TRIANGLES);
   }

   public static boolean isFlag() {
      return batchDepth > 0;
   }

   public static void updateBufferClose() {
      if (begin != null) {
         BuiltBuffer endNullable = begin.endNullable();
         DrawMode drawMode = triangles;
         begin = null;
         triangles = null;
         if (endNullable != null) {
            GpuDevice device = RenderSystem.getDevice();
            ByteBuffer order = DynMappableUtils.getOrder(16);
            order.putFloat(0, (float)Module.mc.getWindow().getFramebufferWidth() / Module.mc.getWindow().getScaleFactor());
            order.putFloat(4, (float)Module.mc.getWindow().getFramebufferHeight() / Module.mc.getWindow().getScaleFactor());
            BuiltBuffer builtBuffer = endNullable;

            try {
               DrawParameters drawParameters = builtBuffer.getDrawParameters();
               Vertices mappableRing = DynMappableUtils.getMappableRing(builtBuffer.getBuffer(), VertexFormats.POSITION_COLOR.getVertexSize());
               GpuBufferSlice slice = DynMappableUtils.getSlice(order);
               ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(drawMode);
               GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(drawParameters.indexCount());
               Framebuffer framebuffer = Module.mc.getFramebuffer();
               CommandEncoder commandEncoder = device.createCommandEncoder();

               try (RenderPass renderPass = commandEncoder.createRenderPass(
                     () -> "corz_gui_color", framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty()
                  )) {
                  DynMappableUtils.samplerCorzCount++;
                  renderPass.setPipeline(getIfAbsent(drawMode));
                  MatrixWindowUtils.handleDequeScissor(renderPass);
                  renderPass.setUniform("CorzGui", slice);
                  renderPass.setVertexBuffer(0, mappableRing.buffer());
                  renderPass.setIndexBuffer(indexBuffer, sequentialBuffer.getIndexType());
                  renderPass.drawIndexed(mappableRing.baseVertex(), 0, drawParameters.indexCount(), 1);
               }
            } catch (Throwable e) {
               if (endNullable != null) {
                  try {
                     builtBuffer.close();
                  } catch (Throwable e2) {
                     e.addSuppressed(e2);
                  }
               }

               throw e;
            }

            if (endNullable != null) {
               endNullable.close();
            }
         }
      }
   }

   private VertexGuiUtils() {
   }

   public static void handle(float vertexGuiValue, float configsLikeValue, float customFlameFactor, float vertexGuiValue4, int panelOverlay) {
      VertexConsumer quads = getQuads();
      handleVertexColor(quads, vertexGuiValue, configsLikeValue, customFlameFactor, vertexGuiValue4, panelOverlay);
      updateBufferClose();
   }

   private static Matrix4f getPositionMatrix() {
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      return matrixStack == null ? MATRIX4F : matrixStack.peek().getPositionMatrix();
   }
}
