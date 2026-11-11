package com.corz.client.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.function.Function;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public final class TexBuilderUtils {
   public static final Identifier identifier = Identifier.of("corzclient", "textures/world/bloom.png");
   public static final RenderPipeline register = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(Identifier.of("corzclient", "pipeline/gw_particles"))
         .withVertexShader("core/position_tex_color")
         .withFragmentShader("core/position_tex_color")
         .withSampler("Sampler0")
         .withBlend(BlendFunction.LIGHTNING)
         .withDepthWrite(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .build()
   );
   public static final Function<Identifier, RenderLayer> memoize = Util.memoize(
      texture -> RenderLayer.of("gw_wparticles", RenderSetup.builder(TexBuilderUtils.register).texture("Sampler0", texture).expectedBufferSize(1536).build())
   );
   public static final RenderPipeline texBuilderRenderPipeline = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(Identifier.of("corzclient", "pipeline/gw_target_esp"))
         .withVertexShader("core/position_tex_color")
         .withFragmentShader("core/position_tex_color")
         .withSampler("Sampler0")
         .withBlend(BlendFunction.LIGHTNING)
         .withDepthWrite(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .build()
   );
   public static final Function<Identifier, RenderLayer> texBuilderFunction = Util.memoize(
      texture -> RenderLayer.of("gw_target_esp", RenderSetup.builder(TexBuilderUtils.texBuilderRenderPipeline).texture("Sampler0", texture).expectedBufferSize(4096).build())
   );

   private TexBuilderUtils() {
   }

   public static void update() {
   }
}
