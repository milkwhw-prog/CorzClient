package com.corz.client.utils;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.function.Function;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import com.corz.client.utils.GhostPlayerGeometry;

public final class PearlGhost {
   private static final float[] vertexColorTable = new float[]{0.0F, 0.3F, 0.38F, 0.44F};
   private static final float[] pearlGhostTable = new float[]{0.05F, 0.14F, 0.5F, 0.0F};
   private static final int[] ghostPearlValues = new int[]{0, 20, 16, 4, 16, 4};
   public static final RenderPipeline pearlGhostRenderPipeline = RenderPipelines.register(
      getWithCull("pearl_ghost_depth")
         .withoutBlend()
         .withColorWrite(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(true)
         .build()
   );
   public static final RenderPipeline register = RenderPipelines.register(
      getWithCull("pearl_ghost_color")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .build()
   );
   public static final RenderPipeline renderPipeline = RenderPipelines.register(
      getWithCull("pearl_ghost_occluded")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
         .withDepthWrite(false)
         .build()
   );
   public static final RenderPipeline BAND_PIPELINE = RenderPipelines.register(
      getDepthWrite("pearl_ghost_band").withBlend(BlendFunction.LIGHTNING).withCull(true).build()
   );
   private static Immediate immediate;
   public static final RenderPipeline PAD_PIPELINE = RenderPipelines.register(
      getDepthWrite("pearl_ghost_pad").withBlend(BlendFunction.TRANSLUCENT).withCull(false).withDepthBias(-1.0F, -10.0F).build()
   );
   private static final Function<Identifier, RenderLayer> pearlGhostFunction = Util.memoize(texture -> getSamplerBuild("corz_pearl_ghost_depth", pearlGhostRenderPipeline, texture));
   private static final Function<Identifier, RenderLayer> function = Util.memoize(texture -> getSamplerBuild("corz_pearl_ghost_color", register, texture));
   private static BufferAllocator bufferAllocator;
   private static final Function<Identifier, RenderLayer> memoize = Util.memoize(
      texture -> getSamplerBuild("corz_pearl_ghost_occluded", PearlGhost.renderPipeline, texture)
   );
   private static final RenderLayer bufferRenderLayer = RenderLayer.of("corz_pearl_ghost_band", RenderSetup.builder(PearlGhost.BAND_PIPELINE).build());
   private static final RenderLayer renderLayer = RenderLayer.of("corz_pearl_ghost_pad", RenderSetup.builder(PearlGhost.PAD_PIPELINE).build());
   private static final float[] ghostPadAmounts = new float[7488];
   private static final Matrix4f MATRIX4F = new Matrix4f();

   public static int getValue(int pearlGhostValue, float ghostPlayerLimit) {
      return Math.round(GhostPlayerGeometry.getAmount(ghostPlayerLimit) * 255.0F) << 24 | pearlGhostValue & 16777215;
   }

   private static int getValue(int pearlGhostValue, float pearlGhostFactor, float pearlGhostValue2, float ghostPearlBound) {
      float max2 = Math.max(0.0F, 1.0F - Math.abs(pearlGhostValue2 - ghostPearlBound) / 0.9F);
      return Math.round(GhostPlayerGeometry.getAmount(max2 * pearlGhostFactor) * 255.0F) << 24 | pearlGhostValue & 16777215;
   }

   private static void handleGhostPearl(VertexConsumer vertices, float[][] floats, float ghostPearlBound, float ghostPearlValue, int pearlGhostValue) {
      for (int i = 0; i < 6; i++) {
         float[] ghostPearlValues2 = floats[i * 2 + 1];
         float ghostPearlValue3 = ghostPearlValues2[0] - 0.15F;
         float pilotStatusBound = ghostPearlValues2[1];
         float ghostPearlValue4 = ghostPearlValues2[2] - 0.15F;
         float vertexColorValue = ghostPearlValues2[3] + 0.15F;
         float ghostPearlBound3 = ghostPearlValues2[4];
         float ghostPearlValue6 = ghostPearlValues2[5] + 0.15F;
         switch (i) {
            case 0:
               pilotStatusBound = 24.0F;
               break;
            case 1:
               ghostPearlValue3 = -4.0F;
               vertexColorValue = 4.0F;
               pilotStatusBound = 12.0F;
               ghostPearlBound3 = 24.0F;
               break;
            case 2:
               vertexColorValue = -4.0F;
               pilotStatusBound = 12.0F;
               break;
            case 3:
               ghostPearlValue3 = 4.0F;
               pilotStatusBound = 12.0F;
               break;
            case 4:
               vertexColorValue = 0.0F;
               ghostPearlBound3 = 12.0F;
               break;
            default:
               ghostPearlValue3 = 0.0F;
               ghostPearlBound3 = 12.0F;
         }

         float max4 = Math.max(pilotStatusBound, ghostPearlBound - 0.9F);
         float wrapDegreesBound = Math.min(ghostPearlBound3, ghostPearlBound + 0.9F);
         if (!(wrapDegreesBound - max4 <= 0.001F)) {
            float max7 = Math.max(max4, Math.min(wrapDegreesBound, ghostPearlBound));
            int ghostPearlValue8 = ghostPearlValues[i];

            for (int j = 0; j < 2; j++) {
               float ghostPearlValue9 = j == 0 ? max4 : max7;
               float ghostPearlValue10 = j == 0 ? max7 : wrapDegreesBound;
               if (!(ghostPearlValue10 - ghostPearlValue9 <= 0.001F)) {
                  int ghostPearlValue11 = getValue(pearlGhostValue, ghostPearlValue, ghostPearlValue9, ghostPearlBound);
                  int ghostPearlValue12 = getValue(pearlGhostValue, ghostPearlValue, ghostPearlValue10, ghostPearlBound);
                  if ((ghostPearlValue8 >> 2 & 1) == 0) {
                     handleVertexColor(vertices, ghostPearlValue3, ghostPearlValue6, ghostPearlValue3, ghostPearlValue4, ghostPearlValue9, ghostPearlValue10, ghostPearlValue11, ghostPearlValue12);
                  }

                  if ((ghostPearlValue8 >> 3 & 1) == 0) {
                     handleVertexColor(vertices, vertexColorValue, ghostPearlValue6, ghostPearlValue3, ghostPearlValue6, ghostPearlValue9, ghostPearlValue10, ghostPearlValue11, ghostPearlValue12);
                  }

                  if ((ghostPearlValue8 >> 4 & 1) == 0) {
                     handleVertexColor(vertices, vertexColorValue, ghostPearlValue4, vertexColorValue, ghostPearlValue6, ghostPearlValue9, ghostPearlValue10, ghostPearlValue11, ghostPearlValue12);
                  }

                  if ((ghostPearlValue8 >> 5 & 1) == 0) {
                     handleVertexColor(vertices, ghostPearlValue3, ghostPearlValue4, vertexColorValue, ghostPearlValue4, ghostPearlValue9, ghostPearlValue10, ghostPearlValue11, ghostPearlValue12);
                  }
               }
            }
         }
      }
   }

   private PearlGhost() {
   }

   private static Immediate getImmediateAllocator() {
      if (immediate == null) {
         bufferAllocator = new BufferAllocator(131072);
         immediate = VertexConsumerProvider.immediate(bufferAllocator);
      }

      return immediate;
   }

   public static void handleVertexColor(Matrix4f matrix, double vertexColorValue, double y, double z, int pearlGhostValue, float vertexColorFactor) {
      if (!(vertexColorFactor < 0.04F)) {
         Immediate immediateAllocator = getImmediateAllocator();
         VertexConsumer buffer = immediateAllocator.getBuffer(renderLayer);
         float vertexColorValue2 = (float)vertexColorValue;
         float vertexColorValue5 = (float)(y + 0.012F);
         float rx2 = (float)z;

         for (int i = 0; i < 32; i++) {
            double angle = (Math.PI * 2) * i / 32.0;
            double angle2 = (Math.PI * 2) * (i + 1) / 32.0;
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float cos2 = (float)Math.cos(angle2);
            float sin2 = (float)Math.sin(angle2);

            for (int j = 0; j < vertexColorTable.length - 1; j++) {
               float vertexColorFactor2 = vertexColorTable[j];
               float vertexColorFactor3 = vertexColorTable[j + 1];
               int vertexColorValue6 = getValue(pearlGhostValue, pearlGhostTable[j] * vertexColorFactor);
               int vertexColorValue7 = getValue(pearlGhostValue, pearlGhostTable[j + 1] * vertexColorFactor);
               buffer.vertex(matrix, vertexColorValue2 + cos * vertexColorFactor2, vertexColorValue5, rx2 + sin * vertexColorFactor2).color(vertexColorValue6);
               buffer.vertex(matrix, vertexColorValue2 + cos * vertexColorFactor3, vertexColorValue5, rx2 + sin * vertexColorFactor3).color(vertexColorValue7);
               buffer.vertex(matrix, vertexColorValue2 + cos2 * vertexColorFactor3, vertexColorValue5, rx2 + sin2 * vertexColorFactor3).color(vertexColorValue7);
               buffer.vertex(matrix, vertexColorValue2 + cos2 * vertexColorFactor2, vertexColorValue5, rx2 + sin2 * vertexColorFactor2).color(vertexColorValue6);
            }
         }

         immediateAllocator.draw(renderLayer);
      }
   }

   private static Builder getDepthWrite(String depthWriteText) {
      return RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
         .withLocation(Identifier.of("corzclient", "pipeline/" + depthWriteText))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false);
   }

   private static RenderLayer getSamplerBuild(String samplerBuildText, RenderPipeline renderPipeline, Identifier id) {
      return RenderLayer.of(
         samplerBuildText, RenderSetup.builder(renderPipeline).texture("Sampler0", id, () -> RenderSystem.getSamplerCache().get(FilterMode.NEAREST)).build()
      );
   }

   private static float getAmount(float pearlGhostFactor) {
      pearlGhostFactor = GhostPlayerGeometry.getAmount(pearlGhostFactor);
      return pearlGhostFactor * pearlGhostFactor * (3.0F - 2.0F * pearlGhostFactor);
   }

   private static Builder getWithCull(String withCullText) {
      return RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(Identifier.of("corzclient", "pipeline/" + withCullText))
         .withVertexShader("core/position_tex_color")
         .withFragmentShader(Identifier.of("corzclient", "core/corz_pearl_ghost"))
         .withSampler("Sampler0")
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .withCull(true);
   }

   public static int getValueForIntFloat(int pearlGhostValue, float pearlGhostFactor) {
      int pearlGhostValue2 = pearlGhostValue >> 16 & 0xFF;
      int pearlGhostValue3 = pearlGhostValue >> 8 & 0xFF;
      int pearlGhostValue4 = pearlGhostValue & 0xFF;
      return Math.round(pearlGhostValue2 + (255 - pearlGhostValue2) * pearlGhostFactor) << 16 | Math.round(pearlGhostValue3 + (255 - pearlGhostValue3) * pearlGhostFactor) << 8 | Math.round(pearlGhostValue4 + (255 - pearlGhostValue4) * pearlGhostFactor);
   }

   private static void handleVertexColor(VertexConsumer vertices, float vertexColorValue, float vertexColorValue2, float vertexColorValue3, float vertexColorValue4, float vertexColorValue5, float vertexColorValue6, int vertexColorValue7, int vertexColorValue8) {
      vertices.vertex(MATRIX4F, vertexColorValue, vertexColorValue6, vertexColorValue2).color(vertexColorValue8);
      vertices.vertex(MATRIX4F, vertexColorValue3, vertexColorValue6, vertexColorValue4).color(vertexColorValue8);
      vertices.vertex(MATRIX4F, vertexColorValue3, vertexColorValue5, vertexColorValue4).color(vertexColorValue7);
      vertices.vertex(MATRIX4F, vertexColorValue, vertexColorValue5, vertexColorValue2).color(vertexColorValue7);
   }

   private static void handlePadGhost(VertexConsumer vertexConsumer, int padGhostLimit, float padGhostFactor, int padGhostValue, boolean padGhostFlag) {
      int padGhostValue2 = padGhostValue >> 16 & 0xFF;
      int padGhostValue3 = padGhostValue >> 8 & 0xFF;
      int padGhostValue4 = padGhostValue & 0xFF;

      for (int i = 0; i < padGhostLimit; i++) {
         int index = i * 6;
         vertexConsumer.vertex(MATRIX4F, ghostPadAmounts[index], ghostPadAmounts[index + 1], ghostPadAmounts[index + 2])
            .texture(ghostPadAmounts[index + 3], ghostPadAmounts[index + 4]);
         if (padGhostFlag) {
            vertexConsumer.color(255, 255, 255, 255);
         } else {
            float padGhostValue5 = ghostPadAmounts[index + 5];
            float padGhostFactor2 = (1.0F - padGhostValue5) * 0.3F;
            int padGhostBound = Math.round((0.26F + 0.64F * padGhostValue5) * padGhostFactor * 255.0F);
            vertexConsumer.color(
               Math.round(255.0F + (padGhostValue2 - 255.0F) * padGhostFactor2),
               Math.round(255.0F + (padGhostValue3 - 255.0F) * padGhostFactor2),
               Math.round(255.0F + (padGhostValue4 - 255.0F) * padGhostFactor2),
               padGhostBound < 1 ? 1 : Math.min(padGhostBound, 255)
            );
         }
      }
   }

   public static void handleDrawBuffer(
      MatrixStack matrices,
      double x,
      double y,
      double z,
      float bufferValue,
      Identifier id,
      boolean bufferFlag,
      int ghostPlayerValue,
      float bufferValue3,
      float bufferFactor,
      int padGhostValue,
      boolean pearlPredictionFlag
   ) {
      if (id != null && !(bufferFactor < 0.04F)) {
         matrices.push();
         matrices.translate(x, y, z);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-bufferValue));
         matrices.scale(0.05859375F, 0.05859375F, 0.05859375F);
         matrices.translate(0.0F, 0.016F, 0.0F);
         MATRIX4F.set(matrices.peek().getPositionMatrix());
         matrices.pop();
         float[] bufferValues = GhostPlayerGeometry.getAmountsForFlag(bufferFlag);
         float ghostPlayerLimit = GhostPlayerGeometry.getAmount(bufferValue3, 1.75F);
         Immediate immediateAllocator = getImmediateAllocator();
         if (pearlPredictionFlag) {
            int bufferValue6 = GhostPlayerGeometry.getValue(bufferValues, ghostPlayerValue & 1365, ghostPlayerLimit, 1.75F, ghostPadAmounts, true);
            RenderLayer apply = memoize.apply(id);
            handlePadGhost(immediateAllocator.getBuffer(apply), bufferValue6, bufferFactor * 0.32F, padGhostValue, false);
            immediateAllocator.draw(apply);
         }

         int padGhostLimit = GhostPlayerGeometry.getValue(bufferValues, ghostPlayerValue, ghostPlayerLimit, 1.75F, ghostPadAmounts);
         RenderLayer apply2 = pearlGhostFunction.apply(id);
         handlePadGhost(immediateAllocator.getBuffer(apply2), padGhostLimit, 1.0F, padGhostValue, true);
         immediateAllocator.draw(apply2);
         RenderLayer apply3 = function.apply(id);
         handlePadGhost(immediateAllocator.getBuffer(apply3), padGhostLimit, bufferFactor, padGhostValue, false);
         immediateAllocator.draw(apply3);
         float bufferLimit = getAmount(bufferValue3 / 0.08F) * getAmount((1.0F - bufferValue3) / 0.08F) * bufferFactor;
         if (bufferLimit > 0.02F) {
            handleGhostPearl(immediateAllocator.getBuffer(bufferRenderLayer), GhostPlayerGeometry.getAmounts(bufferFlag), ghostPlayerLimit, bufferLimit, getValueForIntFloat(padGhostValue, 0.6F));
            immediateAllocator.draw(bufferRenderLayer);
         }
      }
   }
}
