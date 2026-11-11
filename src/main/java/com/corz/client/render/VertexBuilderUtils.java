package com.corz.client.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.LinkedHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class VertexBuilderUtils {
   private static boolean immediateActive;
   private static final BufferAllocator BUFFER_ALLOCATOR = new BufferAllocator(2097152);
   public static final RenderPipeline vertexBuilderRenderPipeline = RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
      .withLocation(Identifier.of("corzclient", "pipeline/esp_filled"))
      .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
      .withBlend(BlendFunction.TRANSLUCENT)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   private static double z;
   private static double y;
   public static final RenderPipeline build = RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
      .withLocation(Identifier.of("corzclient", "pipeline/esp_lines"))
      .withBlend(BlendFunction.TRANSLUCENT)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   private static boolean vertexBuilderActive;
   public static final RenderPipeline renderPipeline = RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
      .withLocation(Identifier.of("corzclient", "pipeline/filled_depth"))
      .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
      .withBlend(BlendFunction.TRANSLUCENT)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   public static final RenderPipeline PARTICLE_GLOW_PIPELINE = RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
      .withLocation(Identifier.of("corzclient", "pipeline/particle_glow"))
      .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLES)
      .withBlend(BlendFunction.ADDITIVE)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   public static final RenderPipeline PARTICLE_GLOW_DEPTH_PIPELINE = RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
      .withLocation(Identifier.of("corzclient", "pipeline/particle_glow_depth"))
      .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLES)
      .withBlend(BlendFunction.ADDITIVE)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
      .withDepthWrite(false)
      .build();
   private static final RenderLayer bufferRenderLayer = RenderLayer.of("corz_esp_filled", RenderSetup.builder(VertexBuilderUtils.vertexBuilderRenderPipeline).build());
   private static final RenderLayer immediateRenderLayer = RenderLayer.of("corz_esp_lines", RenderSetup.builder(build).build());
   private static final RenderLayer renderLayer = RenderLayer.of("corz_filled_depth", RenderSetup.builder(VertexBuilderUtils.renderPipeline).build());
   private static final RenderLayer vertexBuilderRenderLayer = RenderLayer.of("corz_particle_glow", RenderSetup.builder(PARTICLE_GLOW_PIPELINE).build());
   private static final RenderLayer PARTICLE_GLOW_DEPTH_LAYER = RenderLayer.of("corz_particle_glow_depth", RenderSetup.builder(VertexBuilderUtils.PARTICLE_GLOW_DEPTH_PIPELINE).build());
   private static final Immediate IMMEDIATE = getImmediate();
   private static double x;
   private static int particleCorzValue = 0;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static final Vector3f VECTOR3F = new Vector3f(0.0F, 0.0F, -1.0F);

   public static float getAmount(double vertexBuilderValue) {
      return (float)(vertexBuilderValue - y);
   }

   private static void handleVertexColor(
      VertexConsumer vertices,
      Matrix4f matrix,
      float vertexColorValue,
      float deltaY,
      float amountForDouble,
      float vertexColorValue4,
      float f2,
      float vertexColorValue6,
      float vertexColorValue7,
      float vertexColorValue8,
      float vertexColorValue9,
      float amountX,
      float vertexColorValue11,
      float vertexColorValue12,
      int vertexColorValue13
   ) {
      handleParticleCorz(4);
      vertices.vertex(matrix, vertexColorValue, deltaY, amountForDouble).color(vertexColorValue13);
      vertices.vertex(matrix, vertexColorValue4, f2, vertexColorValue6).color(vertexColorValue13);
      vertices.vertex(matrix, vertexColorValue7, vertexColorValue8, vertexColorValue9).color(vertexColorValue13);
      vertices.vertex(matrix, amountX, vertexColorValue11, vertexColorValue12).color(vertexColorValue13);
   }

   public static void updateRegisterBuild() {
      if (!vertexBuilderActive) {
         vertexBuilderActive = true;
         RenderPipelines.register(vertexBuilderRenderPipeline);
         RenderPipelines.register(build);
         RenderPipelines.register(renderPipeline);
         RenderPipelines.register(PARTICLE_GLOW_PIPELINE);
         RenderPipelines.register(PARTICLE_GLOW_DEPTH_PIPELINE);
      }
   }

   static void handleNormalLine(
      VertexConsumer vertices, Matrix4f matrix4f, float normalLineValue, float normalLineValue2, float normalLineValue3, int valueInt, float normalLineValue5, float normalLineValue6, float normalLineValue7, int normalLineValue8, float blockOverlayValue
   ) {
      float normalLineFactor = normalLineValue5 - normalLineValue;
      float normalLineFactor2 = normalLineValue6 - normalLineValue2;
      float normalLineFactor3 = normalLineValue7 - normalLineValue3;
      float normalLineLimit = normalLineFactor * normalLineFactor + normalLineFactor2 * normalLineFactor2 + normalLineFactor3 * normalLineFactor3;
      if (normalLineLimit > 1.0E-9F) {
         float normalLineValue10 = (float)(1.0 / Math.sqrt(normalLineLimit));
         normalLineFactor *= normalLineValue10;
         normalLineFactor2 *= normalLineValue10;
         normalLineFactor3 *= normalLineValue10;
      }

      handleParticleCorz(2);
      vertices.vertex(matrix4f, normalLineValue, normalLineValue2, normalLineValue3).color(valueInt).normal(normalLineFactor, normalLineFactor2, normalLineFactor3).lineWidth(blockOverlayValue);
      vertices.vertex(matrix4f, normalLineValue5, normalLineValue6, normalLineValue7).color(normalLineValue8).normal(normalLineFactor, normalLineFactor2, normalLineFactor3).lineWidth(blockOverlayValue);
   }

   public static void handleCameraMatrix(MatrixStack matrixStack) {
      MATRIX4F.set(matrixStack.peek().getPositionMatrix());
      Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
      Vec3d cameraPos = camera.getCameraPos();
      x = cameraPos.x;
      y = cameraPos.y;
      z = cameraPos.z;
      VECTOR3F.set(0.0F, 0.0F, -1.0F).rotate(camera.getRotation());
      VectorDotUtils.handleVectorRotate(camera);
   }

   public static void updateDrawImmediate() {
      if (!immediateActive) {
         immediateActive = true;
      }

      IMMEDIATE.draw();
   }

   public static void handle(Matrix4f matrix4f, float vertexBuilderValue, float vertexBuilderValue2, float vertexBuilderValue3, float normalLineValue, float vertexBuilderValue5, float vertexBuilderValue6, int vertexBuilderValue7, int valueInt, float blockOverlayValue) {
      handleNormalLine(getBufferVertexConsumer(), matrix4f, vertexBuilderValue, vertexBuilderValue2, vertexBuilderValue3, vertexBuilderValue7, normalLineValue, vertexBuilderValue5, vertexBuilderValue6, valueInt, blockOverlayValue);
   }

   public static void handle(Matrix4f matrix4f, float vertexBuilderValue, float f2, float amountForDouble, float normalLineValue, float f5, float f6, int n2, float blockOverlayValue) {
      VertexConsumer buffer = getBufferVertexConsumer();
      handleNormalLine(buffer, matrix4f, vertexBuilderValue, f2, amountForDouble, n2, normalLineValue, f2, amountForDouble, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, normalLineValue, f2, amountForDouble, n2, normalLineValue, f2, f6, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, normalLineValue, f2, f6, n2, vertexBuilderValue, f2, f6, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, vertexBuilderValue, f2, f6, n2, vertexBuilderValue, f2, amountForDouble, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, vertexBuilderValue, f5, amountForDouble, n2, normalLineValue, f5, amountForDouble, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, normalLineValue, f5, amountForDouble, n2, normalLineValue, f5, f6, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, normalLineValue, f5, f6, n2, vertexBuilderValue, f5, f6, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, vertexBuilderValue, f5, f6, n2, vertexBuilderValue, f5, amountForDouble, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, vertexBuilderValue, f2, amountForDouble, n2, vertexBuilderValue, f5, amountForDouble, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, normalLineValue, f2, amountForDouble, n2, normalLineValue, f5, amountForDouble, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, normalLineValue, f2, f6, n2, normalLineValue, f5, f6, n2, blockOverlayValue);
      handleNormalLine(buffer, matrix4f, vertexBuilderValue, f2, f6, n2, vertexBuilderValue, f5, f6, n2, blockOverlayValue);
   }

   public static float getAmountForDouble(double vertexBuilderValue) {
      return (float)(vertexBuilderValue - z);
   }

   public static VertexConsumer getBuffer() {
      return IMMEDIATE.getBuffer(renderLayer);
   }

   private VertexBuilderUtils() {
   }

   public static VertexConsumer getBufferIMMEDIATE() {
      return IMMEDIATE.getBuffer(bufferRenderLayer);
   }

   static void handleParticleCorz(int particleCorzValue2) {
      particleCorzValue += particleCorzValue2;
   }

   public static VertexConsumer getBufferVertexConsumer() {
      return IMMEDIATE.getBuffer(immediateRenderLayer);
   }

   public static void handleVertexColor(
      VertexConsumer vertices,
      Matrix4f matrix,
      float vertexColorValue,
      float vertexColorValue2,
      float vertexColorValue3,
      int valueInt,
      float amountX,
      float deltaY,
      float vertexColorValue7,
      int vertexColorValue8,
      float vertexColorValue9,
      float vertexColorValue10,
      float amountForDouble,
      int vertexColorValue12,
      float vertexColorValue13,
      float vertexColorValue14,
      float vertexColorValue15,
      int vertexColorValue16
   ) {
      handleParticleCorz(4);
      vertices.vertex(matrix, vertexColorValue, vertexColorValue2, vertexColorValue3).color(valueInt);
      vertices.vertex(matrix, amountX, deltaY, vertexColorValue7).color(vertexColorValue8);
      vertices.vertex(matrix, vertexColorValue9, vertexColorValue10, amountForDouble).color(vertexColorValue12);
      vertices.vertex(matrix, vertexColorValue13, vertexColorValue14, vertexColorValue15).color(vertexColorValue16);
   }

   public static float getAmountX(double vertexBuilderValue) {
      return (float)(vertexBuilderValue - x);
   }

   public static boolean canVector(double vectorValue, double destinationAmount, double destinationZ, float height) {
      double vectorValue5 = (vectorValue - x) * VECTOR3F.x + (destinationAmount - y) * VECTOR3F.y + (destinationZ - z) * VECTOR3F.z;
      return vectorValue5 < -height;
   }

   public static void handleForMatrix4fFloat(Matrix4f matrix4f, float vertexBuilderValue, float vertexBuilderValue2, float amountForDouble, float amountX, float deltaY, float vertexBuilderValue6, int playerValue, float z22) {
      handleNormalLine(getBufferVertexConsumer(), matrix4f, vertexBuilderValue, vertexBuilderValue2, amountForDouble, playerValue, amountX, deltaY, vertexBuilderValue6, playerValue, z22);
   }

   private static Immediate getImmediate() {
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      linkedHashMap.put(bufferRenderLayer, new BufferAllocator(1048576));
      linkedHashMap.put(renderLayer, new BufferAllocator(262144));
      linkedHashMap.put(vertexBuilderRenderLayer, new BufferAllocator(262144));
      linkedHashMap.put(PARTICLE_GLOW_DEPTH_LAYER, new BufferAllocator(262144));
      linkedHashMap.put(immediateRenderLayer, new BufferAllocator(1048576));
      return VertexConsumerProvider.immediate(linkedHashMap, BUFFER_ALLOCATOR);
   }

   private static int getValue(float vertexBuilderValue, float vertexBuilderValue2, float vertexBuilderValue3, float susChunkValue) {
      return (int)(susChunkValue * 255.0F) << 24 | (int)(vertexBuilderValue * 255.0F) << 16 | (int)(vertexBuilderValue2 * 255.0F) << 8 | (int)(vertexBuilderValue3 * 255.0F);
   }

   public static VertexConsumer getBuffer4() {
      return IMMEDIATE.getBuffer(vertexBuilderRenderLayer);
   }

   public static void handle(Matrix4f matrix4f, Vec3d vec, Vec3d vec2, int vertexBuilderValue, float z22) {
      handleForMatrix4fFloat(matrix4f, (float)vec.x, (float)vec.y, (float)vec.z, (float)vec2.x, (float)vec2.y, (float)vec2.z, vertexBuilderValue, z22);
   }

   public static VertexConsumer getBuffer5() {
      return IMMEDIATE.getBuffer(PARTICLE_GLOW_DEPTH_LAYER);
   }

   public static void handle(
      Matrix4f matrix, float vertexBuilderValue, float vertexBuilderValue2, float vertexBuilderValue3, float vertexBuilderValue4, float vertexBuilderValue5, float vertexBuilderValue6, float vertexBuilderValue7, float deltaY, float vertexBuilderValue9, float amountX, float vertexBuilderValue11, float amountForDouble, int vertexBuilderValue13
   ) {
      handleVertexColor(getBufferIMMEDIATE(), matrix, vertexBuilderValue, vertexBuilderValue2, vertexBuilderValue3, vertexBuilderValue4, vertexBuilderValue5, vertexBuilderValue6, vertexBuilderValue7, deltaY, vertexBuilderValue9, amountX, vertexBuilderValue11, amountForDouble, vertexBuilderValue13);
   }

   public static Matrix4f getMatrix() {
      return MATRIX4F;
   }

   public static void handleVertexColor(
      VertexConsumer vertices, Matrix4f matrix, float vertexColorValue, float vertexColorValue2, float vc2, int valueForIntFloat, float vertexColorValue5, float vertexColorValue6, float vertexColorValue7, int vertexColorValue8, float vertexColorValue9, float vertexColorValue10, float vertexColorValue11, int vertexColorValue12
   ) {
      handleParticleCorz(3);
      vertices.vertex(matrix, vertexColorValue, vertexColorValue2, vc2).color(valueForIntFloat);
      vertices.vertex(matrix, vertexColorValue5, vertexColorValue6, vertexColorValue7).color(vertexColorValue8);
      vertices.vertex(matrix, vertexColorValue9, vertexColorValue10, vertexColorValue11).color(vertexColorValue12);
   }

   public static void handle(Matrix4f matrix, float vertexBuilderValue, float f2, float amountForDouble, float vertexColorValue, float f5, float f6, float vertexBuilderValue7, float vertexBuilderValue8, float vertexBuilderValue9, float susChunkValue) {
      VertexConsumer buffer = getBufferIMMEDIATE();
      int vertexBuilderValue11 = getValue(vertexBuilderValue7, vertexBuilderValue8, vertexBuilderValue9, susChunkValue);
      handleVertexColor(buffer, matrix, vertexBuilderValue, f2, amountForDouble, vertexColorValue, f2, amountForDouble, vertexColorValue, f2, f6, vertexBuilderValue, f2, f6, vertexBuilderValue11);
      handleVertexColor(buffer, matrix, vertexBuilderValue, f5, amountForDouble, vertexBuilderValue, f5, f6, vertexColorValue, f5, f6, vertexColorValue, f5, amountForDouble, vertexBuilderValue11);
      handleVertexColor(buffer, matrix, vertexBuilderValue, f2, amountForDouble, vertexBuilderValue, f5, amountForDouble, vertexColorValue, f5, amountForDouble, vertexColorValue, f2, amountForDouble, vertexBuilderValue11);
      handleVertexColor(buffer, matrix, vertexBuilderValue, f2, f6, vertexColorValue, f2, f6, vertexColorValue, f5, f6, vertexBuilderValue, f5, f6, vertexBuilderValue11);
      handleVertexColor(buffer, matrix, vertexBuilderValue, f2, amountForDouble, vertexBuilderValue, f2, f6, vertexBuilderValue, f5, f6, vertexBuilderValue, f5, amountForDouble, vertexBuilderValue11);
      handleVertexColor(buffer, matrix, vertexColorValue, f2, amountForDouble, vertexColorValue, f5, amountForDouble, vertexColorValue, f5, f6, vertexColorValue, f2, f6, vertexBuilderValue11);
   }
}
