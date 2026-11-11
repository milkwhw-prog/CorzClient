package com.corz.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.Vector3fc;

public final class DebrisXray {
   private static final float[] xrayNetheriteTable = new float[]{0.55F, 1.0F, 0.82F, 0.82F, 0.68F, 0.68F};
   private static BlockStateModel model;
   private static final Direction[] xrayNetheriteDirections = new Direction[]{
      Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null
   };
   private static final BlockState defaultState = Blocks.ANCIENT_DEBRIS.getDefaultState();
   private static final RenderPipeline register = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
         .withLocation(Identifier.of("corzclient", "pipeline/netherite_xray"))
         .withCull(true)
         .withDepthWrite(false)
         .build()
   );
   private static final RenderLayer renderLayer = RenderLayer.of(
      "corz_netherite_xray", RenderSetup.builder(DebrisXray.register).texture("Sampler0", SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).build()
   );
   private static float[] xrayNetheriteAmounts = new float[0];
   private static Immediate immediate;

   public static int getLayerRender(List<BlockPos> list, int layerRenderBound) {
      if (list.isEmpty()) {
         return 0;
      } else {
         float[] xrayNetherite = getXrayNetherite();
         if (xrayNetherite.length == 0) {
            return 0;
         } else {
            Matrix4f matrix = VertexBuilderUtils.getMatrix();
            Immediate immediate = getImmediate();
            VertexConsumer buffer = immediate.getBuffer(renderLayer);
            int layerRenderValue = Math.max(0, Math.min(255, layerRenderBound)) << 24;
            int layerRender = 0;

            for (int i = list.size() - 1; i >= 0; i--) {
               BlockPos blockPos = (BlockPos)list.get(i);
               if (!VertexBuilderUtils.canVector(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 1.0F)) {
                  float amountX = VertexBuilderUtils.getAmountX(blockPos.getX());
                  float layerRenderValue3 = VertexBuilderUtils.getAmount(blockPos.getY());
                  float amountForDouble = VertexBuilderUtils.getAmountForDouble(blockPos.getZ());

                  for (int j = 0; j < xrayNetherite.length; j += 6) {
                     int round = Math.round(255.0F * xrayNetherite[j + 5]);
                     buffer.vertex(matrix, amountX + xrayNetherite[j], layerRenderValue3 + xrayNetherite[j + 1], amountForDouble + xrayNetherite[j + 2])
                        .texture(xrayNetherite[j + 3], xrayNetherite[j + 4])
                        .color(layerRenderValue | round << 16 | round << 8 | round);
                  }

                  layerRender++;
               }
            }

            immediate.draw(renderLayer);
            return layerRender;
         }
      }
   }

   private static Immediate getImmediate() {
      if (immediate == null) {
         immediate = VertexConsumerProvider.immediate(new BufferAllocator(262144));
      }

      return immediate;
   }

   private DebrisXray() {
   }

   private static float[] getXrayNetherite() {
      BlockStateModel model2 = MinecraftClient.getInstance().getBlockRenderManager().getModel(defaultState);
      if (model2 == null) {
         return new float[0];
      } else if (model2 == model) {
         return xrayNetheriteAmounts;
      } else {
         List parts = model2.getParts(Random.create(42L));
         int xrayNetheriteTotal = 0;

         for (BlockModelPart blockModelPart : (Iterable<net.minecraft.client.render.model.BlockModelPart>) (parts)) {
            for (Direction direction : xrayNetheriteDirections) {
               xrayNetheriteTotal += blockModelPart.getQuads(direction).size();
            }
         }

         float[] xrayNetheriteAmounts2 = new float[xrayNetheriteTotal * 4 * 6];
         int xrayNetheriteCounter = 0;

         for (BlockModelPart blockModelPart2 : (Iterable<net.minecraft.client.render.model.BlockModelPart>) (parts)) {
            for (Direction xrayNetheriteDirectionsItem : xrayNetheriteDirections) {
               for (BakedQuad bakedQuad : blockModelPart2.getQuads(xrayNetheriteDirectionsItem)) {
                  float xrayNetheriteValue = bakedQuad.shade() && bakedQuad.face() != null ? xrayNetheriteTable[bakedQuad.face().ordinal()] : 1.0F;

                  for (int i = 0; i < 4; i++) {
                     Vector3fc position = bakedQuad.getPosition(i);
                     long texcoords = bakedQuad.getTexcoords(i);
                     xrayNetheriteAmounts2[xrayNetheriteCounter++] = position.x();
                     xrayNetheriteAmounts2[xrayNetheriteCounter++] = position.y();
                     xrayNetheriteAmounts2[xrayNetheriteCounter++] = position.z();
                     xrayNetheriteAmounts2[xrayNetheriteCounter++] = Vector2f.getX(texcoords);
                     xrayNetheriteAmounts2[xrayNetheriteCounter++] = Vector2f.getY(texcoords);
                     xrayNetheriteAmounts2[xrayNetheriteCounter++] = xrayNetheriteValue;
                  }
               }
            }
         }

         model = model2;
         xrayNetheriteAmounts = xrayNetheriteAmounts2;
         return xrayNetheriteAmounts2;
      }
   }
}
