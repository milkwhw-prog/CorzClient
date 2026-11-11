package com.corz.client.render;

import com.corz.client.config.LayeredQuadBuffer;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CopperChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState.Variant;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import com.corz.client.utils.ModelQuadUtils;
import com.corz.client.utils.SpriteFrameUtils;

public final class ModelChest {
   private final ModelPart[] modelParts;
   private static final int[] vertexTable = new int[6];
   private final Sprite[][] sprites;
   private static ModelPart[] modelTextureModelParts;

   ModelChest.Spec getPositiveTranslate(BlockState blockState) {
      Block block = blockState.getBlock();
      if (!(block instanceof ChestBlock) && !(block instanceof EnderChestBlock)) {
         return null;
      } else {
         try {
            ChestType chestType = blockState.contains(ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
            Variant copperUnaffected = getCopperUnaffected(block);
            ChestType chestType2 = copperUnaffected == Variant.ENDER_CHEST ? ChestType.SINGLE : chestType;
            Sprite sprite = this.sprites[copperUnaffected.ordinal()][chestType2.ordinal()];
            if (sprite == null) {
               return null;
            } else {
               Direction direction = blockState.contains(ChestBlock.FACING) ? blockState.get(ChestBlock.FACING) : Direction.NORTH;
               MatrixStack matrixStack = new MatrixStack();
               matrixStack.translate(0.5F, 0.5F, 0.5F);
               matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-direction.getPositiveHorizontalDegrees()));
               matrixStack.translate(-0.5F, -0.5F, -0.5F);
               ModelChest.Baker baker = new ModelChest.Baker(sprite);
               this.modelParts[chestType.ordinal()].render(matrixStack, baker, 15728880, OverlayTexture.DEFAULT_UV);
               return baker.vertexCount != 0 && baker.positiveTranslateCount == 0
                  ? new ModelChest.Spec(Arrays.copyOf(baker.copyOf, baker.writeIndex), Arrays.copyOf(baker.copyOfVertex, baker.vertexCount))
                  : null;
            }
         } catch (Throwable e) {
            return null;
         }
      }
   }

   public static ModelChest getModelTexture(MinecraftClient client) {
      if (modelTextureModelParts == null) {
         modelTextureModelParts = new ModelPart[]{
            ChestBlockModel.getSingleTexturedModelData().createModel(),
            ChestBlockModel.getDoubleChestLeftTexturedBlockData().createModel(),
            ChestBlockModel.getDoubleChestRightTexturedBlockData().createModel()
         };
      }

      AbstractTexture texture = client.getTextureManager().getTexture(TexturedRenderLayers.CHEST_ATLAS_TEXTURE);
      if (texture != null && texture.getGlTextureView() != null) {
         Variant[] variants = Variant.values();
         ChestType[] chestTypes = ChestType.values();
         Sprite[][] sprites = new Sprite[variants.length][chestTypes.length];
         int modelTextureCounter = 0;

         for (Variant variant : variants) {
            if (variant != Variant.CHRISTMAS) {
               for (ChestType chestType : chestTypes) {
                  if (variant != Variant.ENDER_CHEST || chestType == ChestType.SINGLE) {
                     try {
                        Sprite sprite = client.getAtlasManager().getSprite(TexturedRenderLayers.getChestTextureId(variant, chestType));
                        if (sprite != null && !MissingSprite.getMissingSpriteId().equals(sprite.getContents().getId())) {
                           sprites[variant.ordinal()][chestType.ordinal()] = sprite;
                           modelTextureCounter++;
                        }
                     } catch (Throwable e) {
                     }
                  }
               }
            }
         }

         return modelTextureCounter == 0 ? null : new ModelChest(modelTextureModelParts, sprites);
      } else {
         return null;
      }
   }

   private static Variant getCopperUnaffected(Block block) {
      if (block instanceof EnderChestBlock) {
         return Variant.ENDER_CHEST;
      } else if (block instanceof CopperChestBlock copperChestBlock) {
         return switch (copperChestBlock.getOxidationLevel()) {
            case EXPOSED -> Variant.COPPER_EXPOSED;
            case WEATHERED -> Variant.COPPER_WEATHERED;
            case OXIDIZED -> Variant.COPPER_OXIDIZED;
            default -> Variant.COPPER_UNAFFECTED;
         };
      } else {
         return block instanceof TrappedChestBlock ? Variant.TRAPPED : Variant.REGULAR;
      }
   }

   private ModelChest(ModelPart[] modelParts, Sprite[][] sprites2) {
      this.modelParts = modelParts;
      this.sprites = sprites2;
   }

   static {
      for (int i = 0; i < 6; i++) {
         vertexTable[i] = LayeredQuadBuffer.packOpaqueColor(ModelQuadUtils.getValueForIntFloat(16777215, ModelQuadUtils.getAmounts(i)));
      }
   }

   private static final class Baker implements VertexConsumer {
      private float[] copyOf;
      private int writeIndex;
      private final Sprite sprite;
      private final float[][] vertexFloats = new float[4][3];
      private final float[][] bakerFloats = new float[4][2];
      private int vertexCount;
      private int[] copyOfVertex;
      private int positiveTranslateCount;

      @Override
      public VertexConsumer overlay(int overlayValue, int overlayValue2) {
         return this;
      }

      @Override
      public VertexConsumer color(int colorValue) {
         return this;
      }

      @Override
      public VertexConsumer texture(float textureValue, float textureValue2) {
         return this;
      }

      @Override
      public VertexConsumer color(int colorValue, int colorValue2, int colorValue3, int colorValue4) {
         return this;
      }

      private static void handle(float[][] floats, int index, int index2) {
         float[] bakerValues = floats[index];
         floats[index] = floats[index2];
         floats[index2] = bakerValues;
      }

      @Override
      public VertexConsumer vertex(float vertexValue, float vertexValue2, float vertexValue3) {
         return this;
      }

      @Override
      public VertexConsumer light(int lightValue, int lightValue2) {
         return this;
      }

      @Override
      public VertexConsumer normal(float normalValue, float normalValue2, float normalValue3) {
         return this;
      }

      Baker(Sprite sprite2) {
         this.copyOf = new float[360];
         this.copyOfVertex = new int[18];
         this.sprite = sprite2;
      }

      @Override
      public VertexConsumer lineWidth(float lineWidthValue) {
         return this;
      }

      @Override
      public void vertex(float vertexValue, float vertexValue2, float vertexValue3, int vertexValue4, float vertexValue5, float vertexValue6, int vertexValue7, int vertexValue8, float spriteFrameFactor, float vertexValue10, float vertexValue11) {
         this.vertexFloats[this.positiveTranslateCount][0] = vertexValue;
         this.vertexFloats[this.positiveTranslateCount][1] = vertexValue2;
         this.vertexFloats[this.positiveTranslateCount][2] = vertexValue3;
         this.bakerFloats[this.positiveTranslateCount][0] = this.sprite.getFrameU(vertexValue5);
         this.bakerFloats[this.positiveTranslateCount][1] = this.sprite.getFrameV(vertexValue6);
         if (++this.positiveTranslateCount >= 4) {
            this.positiveTranslateCount = 0;
            if (!SpriteFrameUtils.canFlag(this.vertexFloats[0], this.vertexFloats[1], this.vertexFloats[2], this.vertexFloats[3], spriteFrameFactor, vertexValue10, vertexValue11)) {
               handle(this.vertexFloats, 1, 3);
               handle(this.bakerFloats, 1, 3);
            }

            if (this.writeIndex + 20 > this.copyOf.length) {
               this.copyOf = Arrays.copyOf(this.copyOf, this.copyOf.length * 2);
            }

            if (this.vertexCount >= this.copyOfVertex.length) {
               this.copyOfVertex = Arrays.copyOf(this.copyOfVertex, this.copyOfVertex.length * 2);
            }

            for (int i = 0; i < 4; i++) {
               this.copyOf[this.writeIndex++] = this.vertexFloats[i][0];
               this.copyOf[this.writeIndex++] = this.vertexFloats[i][1];
               this.copyOf[this.writeIndex++] = this.vertexFloats[i][2];
               this.copyOf[this.writeIndex++] = this.bakerFloats[i][0];
               this.copyOf[this.writeIndex++] = this.bakerFloats[i][1];
            }

            this.copyOfVertex[this.vertexCount++] = ModelChest.vertexTable[Direction.getFacing(spriteFrameFactor, vertexValue10, vertexValue11).ordinal()];
         }
      }
   }

   static final class Spec {
      final float[] copyOf;
      final int[] specTable;

      void handle(float specValue, float specValue2, float specValue3, LayeredQuadBuffer unfinishedSink) {
         float[] copyOf = this.copyOf;
         int index = 0;

         for (int i = 0; index < this.specTable.length; index++) {
            int packOpaqueColor = this.specTable[index];

            for (int j = 0; j < 4; i += 5) {
               unfinishedSink.addVertex(specValue + copyOf[i], specValue2 + copyOf[i + 1], specValue3 + copyOf[i + 2], copyOf[i + 3], copyOf[i + 4], packOpaqueColor);
               j++;
            }
         }
      }

      Spec(float[] copyOf, int[] copyOf2) {
         this.copyOf = copyOf;
         this.specTable = copyOf2;
      }
   }
}
