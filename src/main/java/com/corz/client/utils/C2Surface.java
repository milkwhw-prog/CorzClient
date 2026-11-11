package com.corz.client.utils;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class C2Surface {
   public static final RenderPipeline register = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(Identifier.of("corzclient", "pipeline/c2_knife_surface"))
         .withVertexShader(Identifier.of("corzclient", "core/corz_knife_surface"))
         .withFragmentShader(Identifier.of("corzclient", "core/corz_knife_surface"))
         .withSampler("Sampler0")
         .withSampler("Sampler1")
         .withSampler("Sampler2")
         .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(true)
         .build()
   );
   private static boolean cutoutCullActive;
   private static final Map<C2Surface.Key, RenderLayer> SAMPLER_CORE_MAP = new HashMap<>();

   private C2Surface() {
   }

   public static RenderLayer getCutoutCull(Identifier id, Identifier id2) {
      if (id2 != null && !cutoutCullActive) {
         try {
            return SAMPLER_CORE_MAP.computeIfAbsent(
               new C2Surface.Key(id, id2),
               key -> RenderLayer.of(
                  "corz_c2_knife_surface",
                  RenderSetup.builder(register)
                     .texture("Sampler0", key.color())
                     .texture("Sampler1", key.specular())
                     .useLightmap()
                     .expectedBufferSize(262144)
                     .build()
               )
            );
         } catch (RuntimeException e) {
            cutoutCullActive = true;
            System.err.println("[C" + e);
            return RenderLayers.entityCutoutNoCull(id);
         }
      } else {
         return RenderLayers.entityCutoutNoCull(id);
      }
   }

   private record Key(Identifier color, Identifier specular) {
   }
}
