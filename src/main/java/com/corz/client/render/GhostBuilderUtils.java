package com.corz.client.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class GhostBuilderUtils {
   public static final RenderPipeline GHOST_CORZ = getGhostCorz("ghost", DepthTestFunction.LEQUAL_DEPTH_TEST, true, true);
   public static final RenderPipeline GHOST_OCCLUDED_PIPELINE = getGhostCorz("ghost_occluded", DepthTestFunction.GREATER_DEPTH_TEST, true, false);
   public static final RenderPipeline CORZ_SCHEM = getCorzSchem("overlay_quads", DepthTestFunction.LEQUAL_DEPTH_TEST, true);
   public static final RenderPipeline OVERLAY_QUADS_OCCLUDED_PIPELINE = getCorzSchem("overlay_quads_occluded", DepthTestFunction.GREATER_DEPTH_TEST, false);
   public static final RenderPipeline RENDER_PIPELINE = getDepthBias("outline", DepthTestFunction.LEQUAL_DEPTH_TEST, true);
   public static final RenderPipeline DEPTH_BIAS = getDepthBias("outline_occluded", DepthTestFunction.GREATER_DEPTH_TEST, false);

   public static void update() {
      BuilderSchemUtils.update();
   }

   private static Identifier getIdentifier(String identifierText) {
      return Identifier.of("corzclient", "pipeline/schem_" + identifierText);
   }

   private static RenderPipeline getDepthBias(String depthBiasText, DepthTestFunction depthTestFunction, boolean depthBiasFlag) {
      Builder withDepthWrite = RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
         .withLocation(getIdentifier(depthBiasText))
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .withDepthTestFunction(depthTestFunction)
         .withDepthWrite(false);
      if (depthBiasFlag) {
         withDepthWrite = withDepthWrite.withDepthBias(-1.2F, -0.2F);
      }

      return getRegister(withDepthWrite.build());
   }

   private static RenderPipeline getCorzSchem(String corzSchemText, DepthTestFunction depthTestFunction, boolean corzSchemFlag) {
      Builder withDepthWrite = RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(getIdentifier(corzSchemText))
         .withVertexShader(getIdentifierForString("corz_schem_overlay"))
         .withFragmentShader(getIdentifierForString("corz_schem_overlay"))
         .withUniform("CorzGhostData", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .withDepthTestFunction(depthTestFunction)
         .withDepthWrite(false);
      if (corzSchemFlag) {
         withDepthWrite = withDepthWrite.withDepthBias(-0.4F, -0.8F);
      }

      return getRegister(withDepthWrite.build());
   }

   private static RenderPipeline getRegister(RenderPipeline renderPipeline) {
      return RenderPipelines.register(renderPipeline);
   }

   private static RenderPipeline getGhostCorz(String ghostCorzText, DepthTestFunction depthTestFunction, boolean ghostCorzFlag, boolean ghostCorzFlag2) {
      Builder withDepthWrite = RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(getIdentifier(ghostCorzText))
         .withVertexShader(getIdentifierForString("corz_schem_ghost"))
         .withFragmentShader(getIdentifierForString("corz_schem_ghost"))
         .withSampler("Sampler0")
         .withUniform("CorzGhostData", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, DrawMode.QUADS)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(ghostCorzFlag)
         .withDepthTestFunction(depthTestFunction)
         .withDepthWrite(false);
      if (ghostCorzFlag2) {
         withDepthWrite = withDepthWrite.withDepthBias(-0.3F, -0.6F);
      }

      return getRegister(withDepthWrite.build());
   }

   private GhostBuilderUtils() {
   }

   private static Identifier getIdentifierForString(String identifierText) {
      return Identifier.of("corzclient", "core/" + identifierText);
   }
}
