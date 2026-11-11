package com.corz.client.render;

import com.corz.client.inventory.ConstLitematicaUtils;
import com.corz.client.world.BlockStateType;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CopperChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState.Variant;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3fc;

final class BlockBuffer implements AutoCloseable {
   private BufferBuilder bufferBuilder;
   private static final BlockStateType[] offsetMutableBlockStateTypes = BlockStateType.values();
   private final MatrixStack matrixStack;
   private static final Direction[] offsetMutableDirections = Direction.values();
   private ModelPart model;
   private ModelPart modelModelTextured;
   private final BufferAllocator bufferAllocator;
   private final BufferAllocator bufferAllocatorBufferAllocat;
   private static final BlockRenderLayer[] bufferCloseBlockRenderLayers = BlockRenderLayer.values();
   private ModelPart modelTexturedModelPart;
   private final BufferAllocator blockBufferBufferAllocator;
   private int indexCount;
   private BufferBuilder bufferCloseBufferBuilder;
   private final Mutable mutable;
   private final LongOpenHashSet longOpenHashSet;
   private final BlockBuffer.ShadedVertexConsumer vertexColorConsumer;
   private final Random random;
   private final int[] offsetMutableTable;
   private final BufferAllocator[] bufferAllocators = new BufferAllocator[BlockRenderLayer.values().length];
   private final float[] bufferCloseTable;
   private final IntArrayList intArrayList;
   private BufferBuilder bufferAllocatorBufferBuilder;
   private final BufferBuilder[] bufferBuilders;

   private static void handle(int[] blockBufferValues, int blockBufferValue, int blockBufferValue2, int blockBufferValue3, int blockBufferValue4, int blockBufferValue5, int blockBufferValue6, int blockBufferValue7, int blockBufferValue8, int blockBufferValue9, int blockBufferValue10, int blockBufferValue11, int dirOffset) {
      blockBufferValues[0] = blockBufferValue;
      blockBufferValues[1] = blockBufferValue2;
      blockBufferValues[2] = blockBufferValue3;
      blockBufferValues[3] = blockBufferValue4;
      blockBufferValues[4] = blockBufferValue5;
      blockBufferValues[5] = blockBufferValue6;
      blockBufferValues[6] = blockBufferValue7;
      blockBufferValues[7] = blockBufferValue8;
      blockBufferValues[8] = blockBufferValue9;
      blockBufferValues[9] = blockBufferValue10;
      blockBufferValues[10] = blockBufferValue11;
      blockBufferValues[11] = dirOffset;
   }

   private boolean canArray(BlockRenderManager blockRenderManager, BlockRenderLayer blockRenderLayer, SchemCorz schemCorz, BlockPos pos2, BlockState state, BlockBuffer.Context context, int arrayValue) {
      BufferBuilder bufferBuilders = this.getBufferBuilders(blockRenderLayer.ordinal());
      if (this.canMatrixStack(blockRenderManager, bufferBuilders, schemCorz, pos2, state, context)) {
         return true;
      } else {
         int arrayLimit = 0;
         if (canBlock(state)) {
            arrayLimit = this.getMatrixStack(this.getBufferAllocator(), schemCorz, pos2, state);
         }

         boolean array;
         if (arrayLimit > 0) {
            array = true;
            this.indexCount += arrayLimit;
         } else {
            array = this.canMinSprite(blockRenderManager, bufferBuilders, schemCorz, pos2, state, context);
            if (array) {
               this.indexCount += 6;
            }
         }

         this.intArrayList.add(arrayValue);
         return array;
      }
   }

   private static void handleNormalLine(BufferBuilder buffer, float normalLineValue, float normalLineValue2, float normalLineValue3, float normalLineValue4, float normalLineValue5, float normalLineValue6, int normalLineValue7, int normalLineValue8, int normalLineValue9, int round, float wallsThrough) {
      float normalLineFactor = normalLineValue4 - normalLineValue;
      float normalLineFactor2 = normalLineValue5 - normalLineValue2;
      float normalLineFactor3 = normalLineValue6 - normalLineValue3;
      float distance = (float)Math.sqrt(normalLineFactor * normalLineFactor + normalLineFactor2 * normalLineFactor2 + normalLineFactor3 * normalLineFactor3);
      float normalLineValue12;
      float normalLineValue13;
      float normalLineValue14;
      if (distance > 1.0E-6F) {
         normalLineValue12 = normalLineFactor / distance;
         normalLineValue13 = normalLineFactor2 / distance;
         normalLineValue14 = normalLineFactor3 / distance;
      } else {
         normalLineValue12 = 0.0F;
         normalLineValue13 = 1.0F;
         normalLineValue14 = 0.0F;
      }

      buffer.vertex(normalLineValue, normalLineValue2, normalLineValue3).color(normalLineValue7, normalLineValue8, normalLineValue9, round).normal(normalLineValue12, normalLineValue13, normalLineValue14).lineWidth(wallsThrough);
      buffer.vertex(normalLineValue4, normalLineValue5, normalLineValue6).color(normalLineValue7, normalLineValue8, normalLineValue9, round).normal(normalLineValue12, normalLineValue13, normalLineValue14).lineWidth(wallsThrough);
   }

   private static void handle(Direction direction, int blockBufferValue, int blockBufferValue2, int blockBufferValue3, int[] blockBufferValues) {
      int blockBufferValue4 = blockBufferValue + 1;
      int blockBufferValue5 = blockBufferValue2 + 1;
      int dirOffset = blockBufferValue3 + 1;
      switch (direction) {
         case DOWN:
            handle(blockBufferValues, blockBufferValue, blockBufferValue2, blockBufferValue3, blockBufferValue4, blockBufferValue2, blockBufferValue3, blockBufferValue4, blockBufferValue2, dirOffset, blockBufferValue, blockBufferValue2, dirOffset);
            break;
         case UP:
            handle(blockBufferValues, blockBufferValue, blockBufferValue5, blockBufferValue3, blockBufferValue4, blockBufferValue5, blockBufferValue3, blockBufferValue4, blockBufferValue5, dirOffset, blockBufferValue, blockBufferValue5, dirOffset);
            break;
         case NORTH:
            handle(blockBufferValues, blockBufferValue, blockBufferValue2, blockBufferValue3, blockBufferValue4, blockBufferValue2, blockBufferValue3, blockBufferValue4, blockBufferValue5, blockBufferValue3, blockBufferValue, blockBufferValue5, blockBufferValue3);
            break;
         case SOUTH:
            handle(blockBufferValues, blockBufferValue, blockBufferValue2, dirOffset, blockBufferValue4, blockBufferValue2, dirOffset, blockBufferValue4, blockBufferValue5, dirOffset, blockBufferValue, blockBufferValue5, dirOffset);
            break;
         case WEST:
            handle(blockBufferValues, blockBufferValue, blockBufferValue2, blockBufferValue3, blockBufferValue, blockBufferValue2, dirOffset, blockBufferValue, blockBufferValue5, dirOffset, blockBufferValue, blockBufferValue5, blockBufferValue3);
            break;
         case EAST:
            handle(blockBufferValues, blockBufferValue4, blockBufferValue2, blockBufferValue3, blockBufferValue4, blockBufferValue2, dirOffset, blockBufferValue4, blockBufferValue5, dirOffset, blockBufferValue4, blockBufferValue5, blockBufferValue3);
      }
   }

   @Override
   public void close() {
      for (BufferAllocator bufferAllocator : this.bufferAllocators) {
         bufferAllocator.close();
      }

      this.bufferAllocator.close();
      this.blockBufferBufferAllocator.close();
   }

   private BufferBuilder getBufferBuilders(int index) {
      if (this.bufferBuilders[index] == null) {
         this.bufferAllocators[index].reset();
         this.bufferBuilders[index] = new BufferBuilder(
            this.bufferAllocators[index], DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL
         );
      }

      return this.bufferBuilders[index];
   }

   private ModelPart getModelTextured(ChestType chestType) {
      switch (chestType) {
         case LEFT:
            if (this.model == null) {
               this.model = ChestBlockModel.getDoubleChestLeftTexturedBlockData().createModel();
            }

            return this.model;
         case RIGHT:
            if (this.modelModelTextured == null) {
               this.modelModelTextured = ChestBlockModel.getDoubleChestRightTexturedBlockData().createModel();
            }

            return this.modelModelTextured;
         default:
            if (this.modelTexturedModelPart == null) {
               this.modelTexturedModelPart = ChestBlockModel.getSingleTexturedModelData().createModel();
            }

            return this.modelTexturedModelPart;
      }
   }

   private static boolean canBlock(BlockState blockState) {
      Block block = blockState.getBlock();
      return block instanceof ChestBlock || block instanceof EnderChestBlock;
   }

   private static int getRgbScale(int rgbScaleValue, float rgbScaleValue2) {
      return ColorHelper.scaleRgb(rgbScaleValue, rgbScaleValue2);
   }

   private void handleSeedQuads(BufferBuilder buffer, BlockRenderManager blockRenderManager, SchemCorz schemCorz, BlockPos pos2, BlockState state, int seedQuadsValue, float wallsThrough) {
      List list;
      try {
         this.random.setSeed(state.getRenderingSeed(pos2));
         list = blockRenderManager.getModel(state).getParts(this.random);
      } catch (Throwable e) {
         this.handleForBufferBuilderSchemCorz(buffer, schemCorz, pos2.getX(), pos2.getY(), pos2.getZ(), seedQuadsValue, wallsThrough);
         return;
      }

      float positionValue = pos2.getX() - schemCorz.schemCorzValue;
      float seedQuadsValue4 = pos2.getY() - schemCorz.matrixStackValue;
      float seedQuadsValue5 = pos2.getZ() - schemCorz.arrayHashValue;
      int seedQuadsValue6 = seedQuadsValue >> 16 & 0xFF;
      int seedQuadsValue7 = seedQuadsValue >> 8 & 0xFF;
      int seedQuadsValue8 = seedQuadsValue & 0xFF;
      int round = Math.round(255.0F);

      for (int i = 0; i < list.size(); i++) {
         BlockModelPart blockModelPart = (BlockModelPart)list.get(i);
         this.handlePosition(buffer, blockModelPart.getQuads(null), positionValue, seedQuadsValue4, seedQuadsValue5, seedQuadsValue6, seedQuadsValue7, seedQuadsValue8, round, wallsThrough);

         for (Direction direction : offsetMutableDirections) {
            this.handlePosition(buffer, blockModelPart.getQuads(direction), positionValue, seedQuadsValue4, seedQuadsValue5, seedQuadsValue6, seedQuadsValue7, seedQuadsValue8, round, wallsThrough);
         }
      }
   }

   private void handleUnpackExtra(SchemCorz schemCorz, BlockBuffer.Context context, BlockRenderManager blockRenderManager) {
      OpacityColorData config = context.config;
      float wallsThrough = config.getWallsThrough();

      for (int i = 0; i < schemCorz.hashOpenTable.length; i++) {
         BlockStateType blockStateType = offsetMutableBlockStateTypes[schemCorz.unpackExtraTable[i]];
         if (blockStateType != BlockStateType.UNKNOWN) {
            int index = schemCorz.hashOpenTable[i];
            BlockPos position = context.positions.get(index);
            BlockState expected = context.expected.get(index);
            if (expected != null && !expected.isAir() && config.canPlayerForInt(position.getY())) {
               boolean unpackExtraFlag = expected.getRenderType() != BlockRenderType.INVISIBLE;
               boolean ghostDrawn = false;
               if (config.canModeMissing(blockStateType)) {
                  FluidState fluidState = expected.getFluidState();
                  if (!fluidState.isEmpty()) {
                     this.handleFluidRender(blockRenderManager, this.getBufferBuilders(BlockRenderLayers.getFluidLayer(fluidState).ordinal()), position, expected, context);
                     ghostDrawn = true;
                  }

                  if (unpackExtraFlag) {
                     ghostDrawn |= this.canArray(blockRenderManager, BlockRenderLayers.getBlockLayer(expected), schemCorz, position, expected, context, index);
                  }
               }

               if (config.canModeRender(blockStateType)) {
                  int unpackExtraValue = ConstLitematicaUtils.getValueForBlockStateType(blockStateType);
                  float correctMissing = config.getCorrectMissing(blockStateType, ghostDrawn);
                  if (correctMissing > 0.002F) {
                     this.handle(this.getBufferAllocatorBufferAllocatorBufferBuilder(), schemCorz, position.getX(), position.getY(), position.getZ(), unpackExtraValue, correctMissing);
                  }

                  if (config.outlines()) {
                     boolean unpackExtraFlag3 = blockStateType == BlockStateType.MISSING && unpackExtraFlag;
                     if (unpackExtraFlag3) {
                        this.handleSeedQuads(this.getBufferAllocatorBufferCloseBufferBuilder(), blockRenderManager, schemCorz, position, expected, unpackExtraValue, wallsThrough);
                     } else {
                        this.handleForBufferBuilderSchemCorz(this.getBufferAllocatorBufferCloseBufferBuilder(), schemCorz, position.getX(), position.getY(), position.getZ(), unpackExtraValue, wallsThrough);
                     }
                  }
               }
            }
         }
      }

      if (config.canModeRender(BlockStateType.EXTRA) && schemCorz.longArray.length > 0) {
         int valueForBlockStateType = ConstLitematicaUtils.getValueForBlockStateType(BlockStateType.EXTRA);
         float blockBufferBound = config.getCorrectMissing(BlockStateType.EXTRA, false);

         for (long unpackExtraValue3 : schemCorz.longArray) {
            int unpackLongX = BlockPos.unpackLongX(unpackExtraValue3);
            int unpackLongY = BlockPos.unpackLongY(unpackExtraValue3);
            int unpackLongZ = BlockPos.unpackLongZ(unpackExtraValue3);
            if (config.canPlayerForInt(unpackLongY)) {
               if (blockBufferBound > 0.002F) {
                  this.handle(this.getBufferAllocatorBufferAllocatorBufferBuilder(), schemCorz, unpackLongX, unpackLongY, unpackLongZ, valueForBlockStateType, blockBufferBound);
               }

               if (config.outlines()) {
                  this.handleForBufferBuilderSchemCorz(this.getBufferAllocatorBufferCloseBufferBuilder(), schemCorz, unpackLongX, unpackLongY, unpackLongZ, valueForBlockStateType, wallsThrough);
               }
            }
         }
      }
   }

   private void handle(BufferBuilder buffer, SchemCorz schemCorz, int blockBufferValue, int unpackLongY, int unpackLongZ, int unpackExtraValue, float blockBufferBound) {
      float blockBufferValue5 = blockBufferValue - schemCorz.schemCorzValue;
      float blockBufferValue6 = unpackLongY - schemCorz.matrixStackValue;
      float blockBufferValue7 = unpackLongZ - schemCorz.arrayHashValue;
      float vertexColorValue = blockBufferValue5 + 1.0F;
      float blockBufferValue9 = blockBufferValue6 + 1.0F;
      float blockBufferValue10 = blockBufferValue7 + 1.0F;
      int blockBufferValue11 = unpackExtraValue >> 16 & 0xFF;
      int blockBufferValue12 = unpackExtraValue >> 8 & 0xFF;
      int blockBufferValue13 = unpackExtraValue & 0xFF;
      int blockBufferLimit = Math.round(Math.min(1.0F, Math.max(0.0F, blockBufferBound)) * 255.0F);
      if (blockBufferLimit > 0) {
         handleVertexColor(buffer, blockBufferValue5, blockBufferValue6, blockBufferValue7, vertexColorValue, blockBufferValue6, blockBufferValue7, vertexColorValue, blockBufferValue6, blockBufferValue10, blockBufferValue5, blockBufferValue6, blockBufferValue10, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferLimit);
         handleVertexColor(buffer, blockBufferValue5, blockBufferValue9, blockBufferValue10, vertexColorValue, blockBufferValue9, blockBufferValue10, vertexColorValue, blockBufferValue9, blockBufferValue7, blockBufferValue5, blockBufferValue9, blockBufferValue7, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferLimit);
         handleVertexColor(buffer, vertexColorValue, blockBufferValue6, blockBufferValue7, blockBufferValue5, blockBufferValue6, blockBufferValue7, blockBufferValue5, blockBufferValue9, blockBufferValue7, vertexColorValue, blockBufferValue9, blockBufferValue7, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferLimit);
         handleVertexColor(buffer, blockBufferValue5, blockBufferValue6, blockBufferValue10, vertexColorValue, blockBufferValue6, blockBufferValue10, vertexColorValue, blockBufferValue9, blockBufferValue10, blockBufferValue5, blockBufferValue9, blockBufferValue10, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferLimit);
         handleVertexColor(buffer, blockBufferValue5, blockBufferValue6, blockBufferValue7, blockBufferValue5, blockBufferValue6, blockBufferValue10, blockBufferValue5, blockBufferValue9, blockBufferValue10, blockBufferValue5, blockBufferValue9, blockBufferValue7, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferLimit);
         handleVertexColor(buffer, vertexColorValue, blockBufferValue6, blockBufferValue10, vertexColorValue, blockBufferValue6, blockBufferValue7, vertexColorValue, blockBufferValue9, blockBufferValue7, vertexColorValue, blockBufferValue9, blockBufferValue10, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferLimit);
      }
   }

   private static int getColorWrong(BlockStateType blockStateType, BlockBuffer.Context context) {
      return switch (blockStateType) {
         case MISSING -> context.missingColor;
         case WRONG_BLOCK -> context.wrongBlockColor;
         case WRONG_STATE -> context.wrongStateColor;
         case EXTRA -> context.extraColor;
         case CORRECT -> context.correctColor;
         case UNKNOWN -> context.accentColor;
      };
   }

   void handleBufferClose(SchemCorz schemCorz, BlockBuffer.Context context) {
      MinecraftClient instance = MinecraftClient.getInstance();
      BlockRenderManager blockRenderManager = instance.getBlockRenderManager();
      this.longOpenHashSet.clear();
      this.intArrayList.clear();
      this.indexCount = 0;

      for (Direction direction : offsetMutableDirections) {
         this.bufferCloseTable[direction.ordinal()] = context.view.getBrightness(direction, true);
      }

      Arrays.fill(this.bufferBuilders, null);
      this.bufferBuilder = null;
      this.bufferAllocatorBufferBuilder = null;
      this.bufferCloseBufferBuilder = null;
      if (context.config.isLitematicaStyle()) {
         this.handleUnpackExtra(schemCorz, context, blockRenderManager);
      } else {
         this.handleConfigFluid(schemCorz, context, blockRenderManager);
      }

      for (int i = 0; i < this.bufferBuilders.length; i++) {
         BuiltBuffer builtBuffer = this.bufferBuilders[i] == null ? null : this.bufferBuilders[i].endNullable();
         BufferGpu gpusTransforms = schemCorz.getGpusTransforms(bufferCloseBlockRenderLayers[i]);

         try {
            gpusTransforms.handleBufferDevice(builtBuffer);
         } finally {
            if (builtBuffer != null) {
               builtBuffer.close();
            }
         }
      }

      BuiltBuffer builtBuffer2 = this.bufferBuilder == null ? null : this.bufferBuilder.endNullable();

      try {
         schemCorz.getGhostSchem().handleBufferDevice(builtBuffer2);
      } finally {
         if (builtBuffer2 != null) {
            builtBuffer2.close();
         }
      }

      BuiltBuffer builtBuffer3 = this.bufferAllocatorBufferBuilder == null ? null : this.bufferAllocatorBufferBuilder.endNullable();

      try {
         schemCorz.getSchemOverlay().handleBufferDevice(builtBuffer3);
      } finally {
         if (builtBuffer3 != null) {
            builtBuffer3.close();
         }
      }

      schemCorz.intArray = this.intArrayList.isEmpty() ? new int[0] : this.intArrayList.toIntArray();
      schemCorz.bufferCloseValue = this.indexCount;
      BuiltBuffer builtBuffer4 = this.bufferCloseBufferBuilder == null ? null : this.bufferCloseBufferBuilder.endNullable();

      try {
         schemCorz.getSchemOutline().handleBufferDevice(builtBuffer4);
      } finally {
         if (builtBuffer4 != null) {
            builtBuffer4.close();
         }
      }
   }

   BlockBuffer() {
      this.bufferAllocatorBufferAllocat = new BufferAllocator(262144);
      this.bufferAllocator = new BufferAllocator(262144);
      this.blockBufferBufferAllocator = new BufferAllocator(131072);
      this.matrixStack = new MatrixStack();
      this.random = Random.create();
      this.mutable = new Mutable();
      this.longOpenHashSet = new LongOpenHashSet();
      this.offsetMutableTable = new int[12];
      this.intArrayList = new IntArrayList();
      this.bufferBuilders = new BufferBuilder[BlockRenderLayer.values().length];
      this.bufferCloseTable = new float[6];
      this.vertexColorConsumer = new BlockBuffer.ShadedVertexConsumer(this.bufferCloseTable);

      for (int i = 0; i < this.bufferAllocators.length; i++) {
         this.bufferAllocators[i] = new BufferAllocator(bufferCloseBlockRenderLayers[i].getBufferSize());
      }
   }

   private void handlePosition(BufferBuilder buffer, List<BakedQuad> list, float positionValue, float positionValue2, float positionValue3, int positionValue4, int positionValue5, int positionValue6, int round, float wallsThrough) {
      for (int i = 0; i < list.size(); i++) {
         BakedQuad bakedQuad = (BakedQuad)list.get(i);

         for (int j = 0; j < 4; j++) {
            Vector3fc position = bakedQuad.getPosition(j);
            Vector3fc position2 = bakedQuad.getPosition(j + 1 & 3);
            handleNormalLine(buffer, positionValue + position.x(), positionValue2 + position.y(), positionValue3 + position.z(), positionValue + position2.x(), positionValue2 + position2.y(), positionValue3 + position2.z(), positionValue4, positionValue5, positionValue6, round, wallsThrough);
         }
      }
   }

   private static void handleVertexColor(
      BufferBuilder buffer,
      float vertexColorValue,
      float vertexColorValue2,
      float vertexColorValue3,
      float vertexColorValue4,
      float vertexColorValue5,
      float vertexColorValue6,
      float vertexColorValue7,
      float vertexColorValue8,
      float vertexColorValue9,
      float vertexColorValue10,
      float vertexColorValue11,
      float vertexColorValue12,
      int vertexColorValue13,
      int vertexColorValue14,
      int vertexColorValue15,
      int blockBufferLimit
   ) {
      buffer.vertex(vertexColorValue, vertexColorValue2, vertexColorValue3).color(vertexColorValue13, vertexColorValue14, vertexColorValue15, blockBufferLimit);
      buffer.vertex(vertexColorValue4, vertexColorValue5, vertexColorValue6).color(vertexColorValue13, vertexColorValue14, vertexColorValue15, blockBufferLimit);
      buffer.vertex(vertexColorValue7, vertexColorValue8, vertexColorValue9).color(vertexColorValue13, vertexColorValue14, vertexColorValue15, blockBufferLimit);
      buffer.vertex(vertexColorValue10, vertexColorValue11, vertexColorValue12).color(vertexColorValue13, vertexColorValue14, vertexColorValue15, blockBufferLimit);
   }

   private int getMatrixStack(BufferBuilder buffer, SchemCorz schemCorz, BlockPos pos2, BlockState state) {
      try {
         ChestType chestType = state.contains(ChestBlock.CHEST_TYPE) ? state.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
         ModelPart modelTextured = this.getModelTextured(chestType);
         if (modelTextured == null) {
            return 0;
         } else {
            Sprite copperChest = getCopperChest(state, chestType);
            if (copperChest == null) {
               return 0;
            } else {
               Direction direction = state.contains(ChestBlock.FACING) ? state.get(ChestBlock.FACING) : Direction.NORTH;
               this.matrixStack.push();

               try {
                  this.matrixStack.translate((float)(pos2.getX() - schemCorz.schemCorzValue), (float)(pos2.getY() - schemCorz.matrixStackValue), (float)(pos2.getZ() - schemCorz.arrayHashValue));
                  this.matrixStack.translate(0.5F, 0.5F, 0.5F);
                  this.matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-direction.getPositiveHorizontalDegrees()));
                  this.matrixStack.translate(-0.5F, -0.5F, -0.5F);
                  modelTextured.render(
                     this.matrixStack,
                     this.vertexColorConsumer.getConsumerVertex(copperChest.getTextureSpecificVertexConsumer(buffer)),
                     15728880,
                     OverlayTexture.DEFAULT_UV
                  );
               } finally {
                  this.matrixStack.pop();
               }

               return 18;
            }
         }
      } catch (Throwable e) {
         return 0;
      }
   }

   private boolean canMinSprite(BlockRenderManager blockRenderManager, BufferBuilder buffer, SchemCorz schemCorz, BlockPos pos2, BlockState state, BlockBuffer.Context context) {
      Sprite sprite = null;

      try {
         sprite = blockRenderManager.getModel(state).particleSprite();
      } catch (Throwable e) {
      }

      if (sprite == null) {
         try {
            sprite = blockRenderManager.getModels().getModelParticleSprite(state);
         } catch (Throwable e2) {
         }
      }

      if (sprite == null) {
         return false;
      } else {
         double spriteValue = 0.0;
         double spriteValue2 = 0.0;
         double spriteValue3 = 0.0;
         double spriteValue4 = 1.0;
         double spriteValue5 = 1.0;
         double spriteValue6 = 1.0;

         double minX7;
         double minY8;
         double minZ9;
         double maxX10;
         double maxY11;
         double maxZ12;
         try {
            VoxelShape outlineShape = state.getOutlineShape(context.view, pos2);
            if (outlineShape != null && !outlineShape.isEmpty()) {
               Box boundingBox = outlineShape.getBoundingBox();
               minX7 = boundingBox.minX;
               minY8 = boundingBox.minY;
               minZ9 = boundingBox.minZ;
               maxX10 = boundingBox.maxX;
               maxY11 = boundingBox.maxY;
               maxZ12 = boundingBox.maxZ;
            } else {
               minZ9 = 0.0;
               minY8 = 0.0;
               minX7 = 0.0;
               maxZ12 = 1.0;
               maxY11 = 1.0;
               maxX10 = 1.0;
            }
         } catch (Throwable e3) {
            minZ9 = 0.0;
            minY8 = 0.0;
            minX7 = 0.0;
            maxZ12 = 1.0;
            maxY11 = 1.0;
            maxX10 = 1.0;
         }

         double spriteValue13 = 0.0625;
         if (maxX10 - minX7 < 0.0625) {
            double spriteValue14 = (minX7 + maxX10) * 0.5;
            minX7 = spriteValue14 - 0.03125;
            maxX10 = spriteValue14 + 0.03125;
         }

         if (maxY11 - minY8 < 0.0625) {
            double spriteValue15 = (minY8 + maxY11) * 0.5;
            minY8 = spriteValue15 - 0.03125;
            maxY11 = spriteValue15 + 0.03125;
         }

         if (maxZ12 - minZ9 < 0.0625) {
            double spriteValue16 = (minZ9 + maxZ12) * 0.5;
            minZ9 = spriteValue16 - 0.03125;
            maxZ12 = spriteValue16 + 0.03125;
         }

         float spriteValue17 = pos2.getX() - schemCorz.schemCorzValue;
         float spriteValue18 = pos2.getY() - schemCorz.matrixStackValue;
         float spriteValue19 = pos2.getZ() - schemCorz.arrayHashValue;
         float lightNormalValue = spriteValue17 + (float)minX7;
         float spriteValue21 = spriteValue18 + (float)minY8;
         float spriteValue22 = spriteValue19 + (float)minZ9;
         float spriteValue23 = spriteValue17 + (float)maxX10;
         float spriteValue24 = spriteValue18 + (float)maxY11;
         float spriteValue25 = spriteValue19 + (float)maxZ12;
         float minU = sprite.getMinU();
         float maxU = sprite.getMaxU();
         float minV = sprite.getMinV();
         float maxV = sprite.getMaxV();
         this.handleLightNormal(
            buffer, lightNormalValue, spriteValue21, spriteValue22, spriteValue23, spriteValue21, spriteValue22, spriteValue23, spriteValue21, spriteValue25, lightNormalValue, spriteValue21, spriteValue25, minU, minV, maxU, maxV, 0.0F, -1.0F, 0.0F
         );
         this.handleLightNormal(
            buffer, lightNormalValue, spriteValue24, spriteValue25, spriteValue23, spriteValue24, spriteValue25, spriteValue23, spriteValue24, spriteValue22, lightNormalValue, spriteValue24, spriteValue22, minU, minV, maxU, maxV, 0.0F, 1.0F, 0.0F
         );
         this.handleLightNormal(
            buffer, spriteValue23, spriteValue21, spriteValue22, lightNormalValue, spriteValue21, spriteValue22, lightNormalValue, spriteValue24, spriteValue22, spriteValue23, spriteValue24, spriteValue22, minU, minV, maxU, maxV, 0.0F, 0.0F, -1.0F
         );
         this.handleLightNormal(
            buffer, lightNormalValue, spriteValue21, spriteValue25, spriteValue23, spriteValue21, spriteValue25, spriteValue23, spriteValue24, spriteValue25, lightNormalValue, spriteValue24, spriteValue25, minU, minV, maxU, maxV, 0.0F, 0.0F, 1.0F
         );
         this.handleLightNormal(
            buffer, lightNormalValue, spriteValue21, spriteValue22, lightNormalValue, spriteValue21, spriteValue25, lightNormalValue, spriteValue24, spriteValue25, lightNormalValue, spriteValue24, spriteValue22, minU, minV, maxU, maxV, -1.0F, 0.0F, 0.0F
         );
         this.handleLightNormal(
            buffer, spriteValue23, spriteValue21, spriteValue25, spriteValue23, spriteValue21, spriteValue22, spriteValue23, spriteValue24, spriteValue22, spriteValue23, spriteValue24, spriteValue25, minU, minV, maxU, maxV, 1.0F, 0.0F, 0.0F
         );
         return true;
      }
   }

   private void handleLightNormal(
      BufferBuilder buffer,
      float lightNormalValue,
      float lightNormalValue2,
      float lightNormalValue3,
      float lightNormalValue4,
      float lightNormalValue5,
      float lightNormalValue6,
      float lightNormalValue7,
      float lightNormalValue8,
      float lightNormalValue9,
      float lightNormalValue10,
      float lightNormalValue11,
      float lightNormalValue12,
      float minU,
      float minV,
      float maxU,
      float maxV,
      float lightNormalValue17,
      float lightNormalValue18,
      float lightNormalValue19
   ) {
      int rgbScale = getRgbScale(-1, this.bufferCloseTable[Direction.getFacing(lightNormalValue17, lightNormalValue18, lightNormalValue19).ordinal()]);
      buffer.vertex(lightNormalValue, lightNormalValue2, lightNormalValue3).color(rgbScale).texture(minU, maxV).light(15728880).normal(lightNormalValue17, lightNormalValue18, lightNormalValue19);
      buffer.vertex(lightNormalValue4, lightNormalValue5, lightNormalValue6).color(rgbScale).texture(maxU, maxV).light(15728880).normal(lightNormalValue17, lightNormalValue18, lightNormalValue19);
      buffer.vertex(lightNormalValue7, lightNormalValue8, lightNormalValue9).color(rgbScale).texture(maxU, minV).light(15728880).normal(lightNormalValue17, lightNormalValue18, lightNormalValue19);
      buffer.vertex(lightNormalValue10, lightNormalValue11, lightNormalValue12).color(rgbScale).texture(minU, minV).light(15728880).normal(lightNormalValue17, lightNormalValue18, lightNormalValue19);
   }

   private BufferBuilder getBufferAllocator() {
      if (this.bufferBuilder == null) {
         this.bufferAllocatorBufferAllocat.reset();
         this.bufferBuilder = new BufferBuilder(this.bufferAllocatorBufferAllocat, DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
      }

      return this.bufferBuilder;
   }

   private void handleConfigFluid(SchemCorz schemCorz, BlockBuffer.Context context, BlockRenderManager blockRenderManager) {
      for (int i = 0; i < schemCorz.hashOpenTable.length; i++) {
         BlockStateType blockStateType = offsetMutableBlockStateTypes[schemCorz.unpackExtraTable[i]];
         int index = schemCorz.hashOpenTable[i];
         BlockPos position = context.positions.get(index);
         BlockState expected = context.expected.get(index);
         if (context.config.canPlayerForInt(position.getY())) {
            boolean configFluidFlag = false;
            if (context.config.canModeMissing(blockStateType) && expected != null && !expected.isAir()) {
               boolean configFluidFlag2 = !expected.getFluidState().isEmpty() && expected.getBlock() instanceof FluidBlock;
               BlockRenderLayer blockRenderLayer = configFluidFlag2 ? BlockRenderLayers.getFluidLayer(expected.getFluidState()) : BlockRenderLayers.getBlockLayer(expected);
               if (configFluidFlag2) {
                  this.handleFluidRender(blockRenderManager, this.getBufferBuilders(blockRenderLayer.ordinal()), position, expected, context);
                  configFluidFlag = true;
               } else {
                  configFluidFlag = this.canArray(blockRenderManager, blockRenderLayer, schemCorz, position, expected, context, index);
               }
            }

            if (context.config.canModeRender(blockStateType) && blockStateType != BlockStateType.UNKNOWN) {
               int colorWrong = getColorWrong(blockStateType, context);
               float correctMissing = context.config.getCorrectMissing(blockStateType, configFluidFlag);
               if (correctMissing > 0.002F) {
                  this.handleOffsetMutable(this.getBufferAllocatorBufferAllocatorBufferBuilder(), schemCorz, position, blockStateType, colorWrong, correctMissing, context);
               }

               if (context.config.outlines()) {
                  this.handleOffsetMutable(this.getBufferAllocatorBufferCloseBufferBuilder(), schemCorz, position, blockStateType, colorWrong, context.config.getOpacityOutline(), context.config.outlineWidth(), context);
               }
            }
         }
      }
   }

   private static boolean canQuads(List<BlockModelPart> list) {
      for (int i = 0; i < list.size(); i++) {
         BlockModelPart part = (BlockModelPart)list.get(i);
         if (!part.getQuads(null).isEmpty()) {
            return true;
         }

         for (Direction direction : offsetMutableDirections) {
            if (!part.getQuads(direction).isEmpty()) {
               return true;
            }
         }
      }

      return false;
   }

   private void handleForBufferBuilderSchemCorz(BufferBuilder buffer, SchemCorz schemCorz, int blockBufferValue, int unpackLongY, int unpackLongZ, int seedQuadsValue, float wallsThrough) {
      float normalLineValue = blockBufferValue - schemCorz.schemCorzValue;
      float blockBufferValue7 = unpackLongY - schemCorz.matrixStackValue;
      float blockBufferValue8 = unpackLongZ - schemCorz.arrayHashValue;
      float blockBufferValue9 = normalLineValue + 1.0F;
      float blockBufferValue10 = blockBufferValue7 + 1.0F;
      float blockBufferValue11 = blockBufferValue8 + 1.0F;
      int blockBufferValue12 = seedQuadsValue >> 16 & 0xFF;
      int blockBufferValue13 = seedQuadsValue >> 8 & 0xFF;
      int blockBufferValue14 = seedQuadsValue & 0xFF;
      int round = Math.round(255.0F);
      handleNormalLine(buffer, normalLineValue, blockBufferValue7, blockBufferValue8, blockBufferValue9, blockBufferValue7, blockBufferValue8, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, normalLineValue, blockBufferValue10, blockBufferValue8, blockBufferValue9, blockBufferValue10, blockBufferValue8, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, normalLineValue, blockBufferValue7, blockBufferValue11, blockBufferValue9, blockBufferValue7, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, normalLineValue, blockBufferValue10, blockBufferValue11, blockBufferValue9, blockBufferValue10, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, normalLineValue, blockBufferValue7, blockBufferValue8, normalLineValue, blockBufferValue10, blockBufferValue8, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, blockBufferValue9, blockBufferValue7, blockBufferValue8, blockBufferValue9, blockBufferValue10, blockBufferValue8, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, normalLineValue, blockBufferValue7, blockBufferValue11, normalLineValue, blockBufferValue10, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, blockBufferValue9, blockBufferValue7, blockBufferValue11, blockBufferValue9, blockBufferValue10, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, normalLineValue, blockBufferValue7, blockBufferValue8, normalLineValue, blockBufferValue7, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, blockBufferValue9, blockBufferValue7, blockBufferValue8, blockBufferValue9, blockBufferValue7, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, normalLineValue, blockBufferValue10, blockBufferValue8, normalLineValue, blockBufferValue10, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
      handleNormalLine(buffer, blockBufferValue9, blockBufferValue10, blockBufferValue8, blockBufferValue9, blockBufferValue10, blockBufferValue11, blockBufferValue12, blockBufferValue13, blockBufferValue14, round, wallsThrough);
   }

   private BufferBuilder getBufferAllocatorBufferAllocatorBufferBuilder() {
      if (this.bufferAllocatorBufferBuilder == null) {
         this.bufferAllocator.reset();
         this.bufferAllocatorBufferBuilder = new BufferBuilder(this.bufferAllocator, DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      }

      return this.bufferAllocatorBufferBuilder;
   }

   private void handleOffsetMutable(BufferBuilder buffer, SchemCorz schemCorz, BlockPos pos2, BlockStateType blockStateType, int offsetMutableValue, float offsetMutableBound, BlockBuffer.Context context) {
      float vertexColorValue = pos2.getX() - schemCorz.schemCorzValue;
      float offsetMutableValue3 = pos2.getY() - schemCorz.matrixStackValue;
      float offsetMutableValue4 = pos2.getZ() - schemCorz.arrayHashValue;
      float offsetMutableValue5 = vertexColorValue + 1.0F;
      float offsetMutableValue6 = offsetMutableValue3 + 1.0F;
      float offsetMutableValue7 = offsetMutableValue4 + 1.0F;
      int offsetMutableValue8 = offsetMutableValue >> 16 & 0xFF;
      int offsetMutableValue9 = offsetMutableValue >> 8 & 0xFF;
      int offsetMutableValue10 = offsetMutableValue & 0xFF;
      int offsetMutableLimit = Math.round(Math.min(1.0F, Math.max(0.0F, offsetMutableBound)) * 255.0F);
      if (offsetMutableLimit > 0) {
         for (Direction direction : offsetMutableDirections) {
            this.mutable.set(pos2.getX() + direction.getOffsetX(), pos2.getY() + direction.getOffsetY(), pos2.getZ() + direction.getOffsetZ());
            byte orDefault = context.classByCell.getOrDefault(this.mutable.asLong(), (byte)-1);
            if (orDefault < 0 || offsetMutableBlockStateTypes[orDefault] != blockStateType) {
               switch (direction) {
                  case DOWN:
                     handleVertexColor(buffer, vertexColorValue, offsetMutableValue3, offsetMutableValue4, offsetMutableValue5, offsetMutableValue3, offsetMutableValue4, offsetMutableValue5, offsetMutableValue3, offsetMutableValue7, vertexColorValue, offsetMutableValue3, offsetMutableValue7, offsetMutableValue8, offsetMutableValue9, offsetMutableValue10, offsetMutableLimit);
                     break;
                  case UP:
                     handleVertexColor(buffer, vertexColorValue, offsetMutableValue6, offsetMutableValue7, offsetMutableValue5, offsetMutableValue6, offsetMutableValue7, offsetMutableValue5, offsetMutableValue6, offsetMutableValue4, vertexColorValue, offsetMutableValue6, offsetMutableValue4, offsetMutableValue8, offsetMutableValue9, offsetMutableValue10, offsetMutableLimit);
                     break;
                  case NORTH:
                     handleVertexColor(buffer, offsetMutableValue5, offsetMutableValue3, offsetMutableValue4, vertexColorValue, offsetMutableValue3, offsetMutableValue4, vertexColorValue, offsetMutableValue6, offsetMutableValue4, offsetMutableValue5, offsetMutableValue6, offsetMutableValue4, offsetMutableValue8, offsetMutableValue9, offsetMutableValue10, offsetMutableLimit);
                     break;
                  case SOUTH:
                     handleVertexColor(buffer, vertexColorValue, offsetMutableValue3, offsetMutableValue7, offsetMutableValue5, offsetMutableValue3, offsetMutableValue7, offsetMutableValue5, offsetMutableValue6, offsetMutableValue7, vertexColorValue, offsetMutableValue6, offsetMutableValue7, offsetMutableValue8, offsetMutableValue9, offsetMutableValue10, offsetMutableLimit);
                     break;
                  case WEST:
                     handleVertexColor(buffer, vertexColorValue, offsetMutableValue3, offsetMutableValue4, vertexColorValue, offsetMutableValue3, offsetMutableValue7, vertexColorValue, offsetMutableValue6, offsetMutableValue7, vertexColorValue, offsetMutableValue6, offsetMutableValue4, offsetMutableValue8, offsetMutableValue9, offsetMutableValue10, offsetMutableLimit);
                     break;
                  case EAST:
                     handleVertexColor(buffer, offsetMutableValue5, offsetMutableValue3, offsetMutableValue7, offsetMutableValue5, offsetMutableValue3, offsetMutableValue4, offsetMutableValue5, offsetMutableValue6, offsetMutableValue4, offsetMutableValue5, offsetMutableValue6, offsetMutableValue7, offsetMutableValue8, offsetMutableValue9, offsetMutableValue10, offsetMutableLimit);
               }
            }
         }
      }
   }

   private void handleOffsetMutable(
      BufferBuilder buffer, SchemCorz schemCorz, BlockPos pos2, BlockStateType blockStateType, int offsetMutableValue, float offsetMutableBound, float wallsThrough, BlockBuffer.Context context
   ) {
      if (!(offsetMutableBound <= 0.002F)) {
         int blockBufferValue = pos2.getX() - schemCorz.schemCorzValue;
         int offsetMutableValue4 = pos2.getY() - schemCorz.matrixStackValue;
         int dirOffset = pos2.getZ() - schemCorz.arrayHashValue;
         int offsetMutableValue6 = offsetMutableValue >> 16 & 0xFF;
         int offsetMutableValue7 = offsetMutableValue >> 8 & 0xFF;
         int offsetMutableValue8 = offsetMutableValue & 0xFF;
         int round = Math.round(Math.min(1.0F, Math.max(0.0F, offsetMutableBound)) * 255.0F);

         for (Direction direction : offsetMutableDirections) {
            this.mutable.set(pos2.getX() + direction.getOffsetX(), pos2.getY() + direction.getOffsetY(), pos2.getZ() + direction.getOffsetZ());
            byte orDefault = context.classByCell.getOrDefault(this.mutable.asLong(), (byte)-1);
            if (orDefault < 0 || offsetMutableBlockStateTypes[orDefault] != blockStateType) {
               handle(direction, blockBufferValue, offsetMutableValue4, dirOffset, this.offsetMutableTable);

               for (int i = 0; i < 4; i++) {
                  int offsetMutableValue10 = i + 1 & 3;
                  if (this.longOpenHashSet.add(getTime(this.offsetMutableTable, i, offsetMutableValue10, blockStateType))) {
                     handleNormalLine(
                        buffer,
                        this.offsetMutableTable[i * 3],
                        this.offsetMutableTable[i * 3 + 1],
                        this.offsetMutableTable[i * 3 + 2],
                        this.offsetMutableTable[offsetMutableValue10 * 3],
                        this.offsetMutableTable[offsetMutableValue10 * 3 + 1],
                        this.offsetMutableTable[offsetMutableValue10 * 3 + 2],
                        offsetMutableValue6,
                        offsetMutableValue7,
                        offsetMutableValue8,
                        round,
                        wallsThrough
                     );
                  }
               }
            }
         }
      }
   }

   private static long getTime(int[] blockBufferValues, int blockBufferValue, int blockBufferValue2, BlockStateType blockStateType) {
      long blockBufferValue3 = (long)blockBufferValues[blockBufferValue * 3] << 10 | (long)blockBufferValues[blockBufferValue * 3 + 1] << 5 | blockBufferValues[blockBufferValue * 3 + 2];
      long blockBufferLimit = (long)blockBufferValues[blockBufferValue2 * 3] << 10 | (long)blockBufferValues[blockBufferValue2 * 3 + 1] << 5 | blockBufferValues[blockBufferValue2 * 3 + 2];
      long blockBufferValue4 = blockBufferValue3 <= blockBufferLimit ? blockBufferValue3 << 15 | blockBufferLimit : blockBufferLimit << 15 | blockBufferValue3;
      return blockBufferValue4 << 3 | blockStateType.ordinal();
   }

   private static Sprite getCopperChest(BlockState state, ChestType chestType) {
      Block block = state.getBlock();
      Variant variant;
      if (block instanceof EnderChestBlock) {
         variant = Variant.ENDER_CHEST;
      } else if (block instanceof CopperChestBlock copperChestBlock) {
         variant = switch (copperChestBlock.getOxidationLevel()) {
            case EXPOSED -> Variant.COPPER_EXPOSED;
            case WEATHERED -> Variant.COPPER_WEATHERED;
            case OXIDIZED -> Variant.COPPER_OXIDIZED;
            default -> Variant.COPPER_UNAFFECTED;
         };
      } else if (block instanceof TrappedChestBlock) {
         variant = Variant.TRAPPED;
      } else {
         variant = Variant.REGULAR;
      }

      if (variant == Variant.ENDER_CHEST) {
         chestType = ChestType.SINGLE;
      }

      return MinecraftClient.getInstance().getAtlasManager().getSprite(TexturedRenderLayers.getChestTextureId(variant, chestType));
   }

   private void handleFluidRender(BlockRenderManager blockRenderManager, BufferBuilder buffer, BlockPos pos2, BlockState state, BlockBuffer.Context context) {
      try {
         blockRenderManager.renderFluid(pos2, context.view, buffer, state, state.getFluidState());
      } catch (Throwable e) {
      }
   }

   private BufferBuilder getBufferAllocatorBufferCloseBufferBuilder() {
      if (this.bufferCloseBufferBuilder == null) {
         this.blockBufferBufferAllocator.reset();
         this.bufferCloseBufferBuilder = new BufferBuilder(this.blockBufferBufferAllocator, DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);
      }

      return this.bufferCloseBufferBuilder;
   }

   private boolean canMatrixStack(BlockRenderManager blockRenderManager, BufferBuilder buffer, SchemCorz schemCorz, BlockPos pos2, BlockState state, BlockBuffer.Context context) {
      List list;
      try {
         this.random.setSeed(state.getRenderingSeed(pos2));
         list = blockRenderManager.getModel(state).getParts(this.random);
      } catch (Throwable e) {
         return false;
      }

      boolean matrixStackFlag;
      try {
         matrixStackFlag = canQuads(list);
      } catch (Throwable e2) {
         matrixStackFlag = false;
      }

      if (!matrixStackFlag) {
         return false;
      } else {
         this.matrixStack.push();

         try {
            this.matrixStack.translate((float)(pos2.getX() - schemCorz.schemCorzValue), (float)(pos2.getY() - schemCorz.matrixStackValue), (float)(pos2.getZ() - schemCorz.arrayHashValue));
            blockRenderManager.renderBlock(state, pos2, context.view, this.matrixStack, buffer, true, list);
         } catch (Throwable e3) {
         } finally {
            this.matrixStack.pop();
         }

         return true;
      }
   }

   record Context(
      List<BlockPos> positions,
      List<BlockState> expected,
      Long2ByteOpenHashMap classByCell,
      BlockRenderViewHelper view,
      OpacityColorData config,
      int accentColor,
      int correctColor,
      int missingColor,
      int wrongBlockColor,
      int wrongStateColor,
      int extraColor
   ) {
   }

   private static final class ShadedVertexConsumer implements VertexConsumer {
      private final float[] vertexTable;
      private VertexConsumer vertexConsumer;

      @Override
      public VertexConsumer overlay(int overlayValue, int overlayValue2) {
         return this.vertexConsumer.overlay(overlayValue, overlayValue2);
      }

      @Override
      public VertexConsumer vertex(float vertexValue, float vertexValue2, float vertexValue3) {
         return this.vertexConsumer.vertex(vertexValue, vertexValue2, vertexValue3);
      }

      @Override
      public VertexConsumer light(int lightValue, int lightValue2) {
         return this.vertexConsumer.light(lightValue, lightValue2);
      }

      BlockBuffer.ShadedVertexConsumer getConsumerVertex(VertexConsumer vertexConsumer) {
         this.vertexConsumer = vertexConsumer;
         return this;
      }

      @Override
      public VertexConsumer texture(float textureValue, float textureValue2) {
         return this.vertexConsumer.texture(textureValue, textureValue2);
      }

      ShadedVertexConsumer(float[] amounts) {
         this.vertexTable = amounts;
      }

      @Override
      public void vertex(float vertexValue, float vertexValue2, float vertexValue3, int rgbScaleValue, float vertexValue5, float vertexValue6, int vertexValue7, int vertexValue8, float vertexValue9, float vertexValue10, float vertexValue11) {
         float vertexValue12 = this.vertexTable[Direction.getFacing(vertexValue9, vertexValue10, vertexValue11).ordinal()];
         this.vertexConsumer.vertex(vertexValue, vertexValue2, vertexValue3, BlockBuffer.getRgbScale(rgbScaleValue, vertexValue12), vertexValue5, vertexValue6, vertexValue7, vertexValue8, vertexValue9, vertexValue10, vertexValue11);
      }

      @Override
      public VertexConsumer normal(float normalValue, float normalValue2, float normalValue3) {
         return this.vertexConsumer.normal(normalValue, normalValue2, normalValue3);
      }

      @Override
      public VertexConsumer color(int colorValue, int colorValue2, int colorValue3, int colorValue4) {
         return this.vertexConsumer.color(colorValue, colorValue2, colorValue3, colorValue4);
      }

      @Override
      public VertexConsumer color(int colorValue) {
         return this.vertexConsumer.color(colorValue);
      }

      @Override
      public VertexConsumer lineWidth(float lineWidthValue) {
         return this.vertexConsumer.lineWidth(lineWidthValue);
      }
   }
}
