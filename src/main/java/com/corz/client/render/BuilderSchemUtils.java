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

public final class BuilderSchemUtils {
   public static final RenderPipeline registerSamplerTransforms = RenderPipelines.register(getVertexFormat("solid").build());
   public static final RenderPipeline builderSchemRenderPipeline = RenderPipelines.register(getVertexFormat("cutout").withShaderDefine("ALPHA_CUTOUT", 0.5F).build());
   public static final RenderPipeline samplerTransformsRenderPipel = RenderPipelines.register(
      getVertexFormat("translucent").withBlend(BlendFunction.TRANSLUCENT).withShaderDefine("ALPHA_CUTOUT", 0.01F).withDepthWrite(false).build()
   );
   public static final RenderPipeline register = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(getIdentifier("overlay_quads_through"))
         .withVertexShader(getIdentifierForString("corz_schem_overlay"))
         .withFragmentShader(getIdentifierForString("corz_schem_overlay"))
         .withUniform("CorzGhostData", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .build()
   );
   public static final RenderPipeline renderPipeline = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
         .withLocation(getIdentifier("outline_through"))
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .build()
   );

   public static void update() {
   }

   private static Identifier getIdentifier(String identifierText) {
      return Identifier.of("corzclient", "pipeline/schem_lit_" + identifierText);
   }

   private BuilderSchemUtils() {
   }

   private static Identifier getIdentifierForString(String identifierText) {
      return Identifier.of("corzclient", "core/" + identifierText);
   }

   private static Builder getVertexFormat(String vertexFormatText) {
      return RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(getIdentifier(vertexFormatText))
         .withVertexShader(getIdentifierForString("corz_schem_lit"))
         .withFragmentShader(getIdentifierForString("corz_schem_lit"))
         .withUniform("Fog", UniformType.UNIFORM_BUFFER)
         .withSampler("Sampler0")
         .withSampler("Sampler2")
         .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, DrawMode.QUADS);
   }
}
