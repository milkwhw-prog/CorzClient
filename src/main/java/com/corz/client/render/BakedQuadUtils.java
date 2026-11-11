package com.corz.client.render;

import com.corz.client.config.LayeredQuadBuffer;
import com.corz.client.world.ElseBuilder;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.EmptyBlockView;
import org.joml.Vector3fc;
import com.corz.client.utils.ModelQuadUtils;
import com.corz.client.utils.SpriteFrameUtils;

public final class BakedQuadUtils {
   private static final Direction[] directions = new Direction[]{
      Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
   };
   private static final int UNIT_VALUE = 1;
   private static final float[] bakedQuadTable = new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
   private static final ExecutorService threadExecutor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "Corz-SchematicPreview");
      thread.setDaemon(true);
      thread.setPriority(4);
      return thread;
   });

   private static void handleAmounts(BakedQuadUtils.Prepared prepared, int index, int bakedQuadValue, int bakedQuadValue2, int bakedQuadValue3, BakedQuadUtils.Scratch scratch) {
      int[] bakedQuadValues = SpriteFrameUtils.seedQuadsInts[index];
      int bakedQuadValue4 = bakedQuadValue + bakedQuadValues[0];
      int bakedQuadValue5 = bakedQuadValue2 + bakedQuadValues[1];
      int bakedQuadValue6 = bakedQuadValue3 + bakedQuadValues[2];
      byte index2;
      byte index3;
      if (bakedQuadValues[1] != 0) {
         index2 = 0;
         index3 = 2;
      } else if (bakedQuadValues[2] != 0) {
         index2 = 0;
         index3 = 1;
      } else {
         index2 = 1;
         index3 = 2;
      }

      for (int i = 0; i < 4; i++) {
         int bakedQuadValue7 = ModelQuadUtils.getValue(scratch.scratchFloats[i][index2]);
         int bakedQuadValue8 = ModelQuadUtils.getValue(scratch.scratchFloats[i][index3]);
         int bakedQuadValue9 = index2 == 0 ? bakedQuadValue7 : 0;
         int bakedQuadValue10 = index2 == 1 ? bakedQuadValue7 : 0;
         int bakedQuadValue11 = index2 == 2 ? bakedQuadValue7 : 0;
         int bakedQuadValue12 = index3 == 0 ? bakedQuadValue8 : 0;
         int bakedQuadValue13 = index3 == 1 ? bakedQuadValue8 : 0;
         int bakedQuadValue14 = index3 == 2 ? bakedQuadValue8 : 0;
         boolean cubeFull = prepared.canCubeFull(bakedQuadValue4 + bakedQuadValue9, bakedQuadValue5 + bakedQuadValue10, bakedQuadValue6 + bakedQuadValue11);
         boolean canCubeFull = prepared.canCubeFull(bakedQuadValue4 + bakedQuadValue12, bakedQuadValue5 + bakedQuadValue13, bakedQuadValue6 + bakedQuadValue14);
         boolean canCubeFull3 = prepared.canCubeFull(bakedQuadValue4 + bakedQuadValue9 + bakedQuadValue12, bakedQuadValue5 + bakedQuadValue10 + bakedQuadValue13, bakedQuadValue6 + bakedQuadValue11 + bakedQuadValue14);
         scratch.shadeTable[i] = ModelQuadUtils.getValue(cubeFull, canCubeFull, canCubeFull3);
      }
   }

   private static void handleSeedQuads(
      BakedQuadUtils.Prepared prepared,
      BakedQuadUtils.Info info2,
      int seedQuadsValue,
      int seedQuadsValue2,
      int seedQuadsValue3,
      LayeredQuadBuffer unfinishedSink,
      LayeredQuadBuffer layeredQuadBuffer,
      LayeredQuadBuffer unfinishedSink3,
      BakedQuadUtils.Scratch scratch
   ) {
      if (info2.hasModel) {
         if (info2.seedDefaultActive) {
            if (info2.seedQuadsSpec != null) {
               info2.seedQuadsSpec.handle(seedQuadsValue - prepared.seedQuadsValue, seedQuadsValue2 - prepared.elseArrayValue, seedQuadsValue3 - prepared.originZ, unfinishedSink3);
            } else {
               handleAmounts(prepared, info2, seedQuadsValue, seedQuadsValue2, seedQuadsValue3, unfinishedSink, scratch);
            }
         } else {
            LayeredQuadBuffer unfinishedSink4 = info2.seedQuadsActive ? layeredQuadBuffer : unfinishedSink;
            scratch.random.setSeed(info2.defaultState.getRenderingSeed(scratch.mutable.set(seedQuadsValue, seedQuadsValue2, seedQuadsValue3)));
            scratch.seedQuadsBlockModelPartList.clear();
            info2.model.addParts(scratch.random, scratch.seedQuadsBlockModelPartList);

            for (int i = 0; i < scratch.seedQuadsBlockModelPartList.size(); i++) {
               BlockModelPart blockModelPart = scratch.seedQuadsBlockModelPartList.get(i);
               boolean seedQuadsFlag = info2.luminance == 0 && blockModelPart.useAmbientOcclusion();

               for (int j = 0; j < 6; j++) {
                  List quads = blockModelPart.getQuads(directions[j]);
                  if (!quads.isEmpty()) {
                     int[] seedQuadsValues = SpriteFrameUtils.seedQuadsInts[j];
                     BakedQuadUtils.Info arrayInstance = prepared.getArrayInstance(seedQuadsValue + seedQuadsValues[0], seedQuadsValue2 + seedQuadsValues[1], seedQuadsValue3 + seedQuadsValues[2]);
                     if (Block.shouldDrawSide(info2.defaultState, arrayInstance.defaultState, directions[j])) {
                        for (int k = 0; k < quads.size(); k++) {
                           handleAmountsShade(prepared, info2, (BakedQuad)quads.get(k), seedQuadsValue, seedQuadsValue2, seedQuadsValue3, seedQuadsFlag, unfinishedSink4, scratch);
                        }
                     }
                  }
               }

               List quads2 = blockModelPart.getQuads(null);

               for (int m = 0; m < quads2.size(); m++) {
                  handleAmountsShade(prepared, info2, (BakedQuad)quads2.get(m), seedQuadsValue, seedQuadsValue2, seedQuadsValue3, seedQuadsFlag, unfinishedSink4, scratch);
               }
            }
         }
      }

      if (info2.fluidKind != 0) {
         handleColorsModels(prepared, info2, seedQuadsValue, seedQuadsValue2, seedQuadsValue3, unfinishedSink, layeredQuadBuffer, scratch, false);
      }
   }

   private static void handleMinMax(
      float[] bakedQuadValues, int bakedQuadValue, Sprite sprite, int modelQuadValue, int colorsModelsValue, int bakedQuadValue4, int bakedQuadValue5, BakedQuadUtils.Prepared prepared, LayeredQuadBuffer unfinishedSink, BakedQuadUtils.Scratch scratch
   ) {
      int valueForIntFloat = ModelQuadUtils.getValueForIntFloat(modelQuadValue, ModelQuadUtils.getAmounts(bakedQuadValue));
      int packOpaqueColor = LayeredQuadBuffer.packOpaqueColor(valueForIntFloat);
      float minU = sprite.getMinU();
      float maxU = sprite.getMaxU();
      float minV = sprite.getMinV();
      float maxV = sprite.getMaxV();
      float bakedQuadValue8 = colorsModelsValue - prepared.seedQuadsValue;
      float bakedQuadValue9 = bakedQuadValue4 - prepared.elseArrayValue;
      float bakedQuadValue10 = bakedQuadValue5 - prepared.originZ;
      float[] scratchTable = scratch.scratchTable;

      for (int i = 0; i < 4; i++) {
         SpriteFrameUtils.handle(bakedQuadValue, i, bakedQuadValues, scratchTable);
         float quadUnfinishedValue = minU + (maxU - minU) * SpriteFrameUtils.getAmount(bakedQuadValue, scratchTable[0], scratchTable[1], scratchTable[2]);
         float bakedQuadValue12 = minV + (maxV - minV) * SpriteFrameUtils.getAmountForIntFloat(bakedQuadValue, scratchTable[0], scratchTable[1], scratchTable[2]);
         unfinishedSink.addVertex(bakedQuadValue8 + scratchTable[0], bakedQuadValue9 + scratchTable[1], bakedQuadValue10 + scratchTable[2], quadUnfinishedValue, bakedQuadValue12, packOpaqueColor);
      }
   }

   private static void handleColorsModels(
      BakedQuadUtils.Prepared prepared,
      BakedQuadUtils.Info info2,
      int colorsModelsValue,
      int colorsModelsValue2,
      int colorsModelsValue3,
      LayeredQuadBuffer unfinishedSink,
      LayeredQuadBuffer layeredQuadBuffer,
      BakedQuadUtils.Scratch scratch,
      boolean colorsModelsFlag
   ) {
      boolean colorsModelsFlag2 = info2.fluidKind == 1;
      Sprite sprite = colorsModelsFlag2 ? prepared.modelsColorsData.water() : prepared.modelsColorsData.lava();
      if (sprite != null) {
         LayeredQuadBuffer unfinishedSink3 = colorsModelsFlag2 ? layeredQuadBuffer : unfinishedSink;
         BakedQuadUtils.Info arrayInstance = prepared.getArrayInstance(colorsModelsValue, colorsModelsValue2 + 1, colorsModelsValue3);
         float colorsModelsValue4 = arrayInstance.fluidKind == info2.fluidKind ? 1.0F : 0.875F;
         float[] floats = new float[]{0.0F, 0.0F, 0.0F, 1.0F, colorsModelsValue4, 1.0F};
         int modelQuadValue = colorsModelsFlag2 ? 4159204 : 16777215;

         for (int i = 0; i < 6; i++) {
            if (!colorsModelsFlag || i == 1) {
               if (!colorsModelsFlag) {
                  int[] colorsModelsValues = SpriteFrameUtils.seedQuadsInts[i];
                  BakedQuadUtils.Info info3 = i == 1 ? arrayInstance : prepared.getArrayInstance(colorsModelsValue + colorsModelsValues[0], colorsModelsValue2 + colorsModelsValues[1], colorsModelsValue3 + colorsModelsValues[2]);
                  if (info3.fluidKind == info2.fluidKind || info3.fullCube) {
                     continue;
                  }
               }

               handleMinMax(floats, i, sprite, modelQuadValue, colorsModelsValue, colorsModelsValue2, colorsModelsValue3, prepared, unfinishedSink3, scratch);
            }
         }
      }
   }

   static BakedQuadUtils.Result getElseArray(ElseBuilder elseBuilder, BakedQuadUtils.Env env, BakedQuadUtils.Job job) {
      BakedQuadUtils.Scratch scratch = new BakedQuadUtils.Scratch();
      BakedQuadUtils.Prepared prepared = new BakedQuadUtils.Prepared(elseBuilder, env, scratch.random);
      int elseArrayLimit = prepared.spanSize;
      LayeredQuadBuffer unfinishedSink = new LayeredQuadBuffer(elseArrayLimit, 4096);
      LayeredQuadBuffer layeredQuadBuffer = new LayeredQuadBuffer(elseArrayLimit, 256);
      LayeredQuadBuffer layeredQuadBuffer3 = new LayeredQuadBuffer(elseArrayLimit, 64);
      int maxValue = Integer.MAX_VALUE;
      int maxValue3 = Integer.MAX_VALUE;
      int maxValue4 = Integer.MAX_VALUE;
      int elseArrayValue = Integer.MIN_VALUE;
      int minValue = Integer.MIN_VALUE;
      int minValue3 = Integer.MIN_VALUE;
      int elseArrayCounter = 0;
      boolean elseArrayFlag = false;

      label105:
      for (int i = 0; i < elseArrayLimit; i++) {
         if (job != null && job.elseArrayActive) {
            return null;
         }

         int elseArrayLimit5 = prepared.elseArrayValue + i;
         unfinishedSink.beginLayer(i);
         layeredQuadBuffer.beginLayer(i);
         layeredQuadBuffer3.beginLayer(i);

         for (int j = 0; j < prepared.array.length; j++) {
            ElseBuilder.Region region = prepared.array[j];
            int elseFactor = elseArrayLimit5 - prepared.elseArrayTable[j];
            if (elseFactor >= 0 && elseFactor < region.elseArrayValue) {
               BakedQuadUtils.Info[] infos = prepared.stateModels[j];

               for (int k = 0; k < region.regionValue; k++) {
                  for (int m = 0; m < region.elseValue; m++) {
                     int else3 = region.getElse(m, elseFactor, k);
                     if (else3 >= 0 && else3 < infos.length) {
                        BakedQuadUtils.Info info = infos[else3];
                        if (!info.elseArrayActive) {
                           int arrayValue = prepared.elseModelsTable[j] + m;
                           int elseArrayLimit8 = prepared.preparedTable[j] + k;
                           if (!prepared.elseArrayFlags[j] || prepared.getArrayFlag(arrayValue, elseArrayLimit5, elseArrayLimit8) == j) {
                              if (unfinishedSink.getQuadCount() + layeredQuadBuffer.getQuadCount() + layeredQuadBuffer3.getQuadCount() >= 360000) {
                                 elseArrayFlag = true;
                                 break label105;
                              }

                              elseArrayCounter++;
                              if (arrayValue < maxValue) {
                                 maxValue = arrayValue;
                              }

                              if (arrayValue > elseArrayValue) {
                                 elseArrayValue = arrayValue;
                              }

                              if (elseArrayLimit5 < maxValue3) {
                                 maxValue3 = elseArrayLimit5;
                              }

                              if (elseArrayLimit5 > minValue) {
                                 minValue = elseArrayLimit5;
                              }

                              if (elseArrayLimit8 < maxValue4) {
                                 maxValue4 = elseArrayLimit8;
                              }

                              if (elseArrayLimit8 > minValue3) {
                                 minValue3 = elseArrayLimit8;
                              }

                              try {
                                 handleSeedQuads(prepared, info, arrayValue, elseArrayLimit5, elseArrayLimit8, unfinishedSink, layeredQuadBuffer, layeredQuadBuffer3, scratch);
                              } catch (Throwable e) {
                                 if (scratch.invocationCount++ == 0) {
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      unfinishedSink.finish();
      layeredQuadBuffer.finish();
      layeredQuadBuffer3.finish();
      if (elseArrayCounter == 0) {
         return new BakedQuadUtils.Result(unfinishedSink, layeredQuadBuffer, layeredQuadBuffer3, prepared, 0, elseArrayFlag, 1, 0, 1.0F, 1.0F, 1.0F, 0.5F, 0.5F, 0.5F, 0.0F);
      } else {
         float elseArrayValue4 = elseArrayValue - maxValue + 1;
         float elseArrayValue5 = minValue - maxValue3 + 1;
         float elseArrayValue6 = minValue3 - maxValue4 + 1;
         float elseArrayValue7 = maxValue - prepared.seedQuadsValue + elseArrayValue4 * 0.5F;
         float elseArrayValue8 = maxValue3 - prepared.elseArrayValue + elseArrayValue5 * 0.5F;
         float elseArrayValue9 = maxValue4 - prepared.originZ + elseArrayValue6 * 0.5F;
         return new BakedQuadUtils.Result(
            unfinishedSink, layeredQuadBuffer, layeredQuadBuffer3, prepared, elseArrayCounter, elseArrayFlag, minValue - maxValue3 + 1, maxValue3 - prepared.elseArrayValue, elseArrayValue4, elseArrayValue5, elseArrayValue6, elseArrayValue7, elseArrayValue8, elseArrayValue9, maxValue3 - prepared.elseArrayValue
         );
      }
   }

   public static BakedQuadUtils.BuildJob getThreadExecutor(ElseBuilder elseBuilder, BakedQuadUtils.Env env) {
      BakedQuadUtils.BuildJob buildJob = new BakedQuadUtils.BuildJob(elseBuilder, env);
      threadExecutor.execute(buildJob);
      return buildJob;
   }

   private static void handleSpriteAmounts(
      BakedQuadUtils.Info info2, int spriteValue, int colorsModelsValue, int spriteValue3, int spriteValue4, BakedQuadUtils.Prepared prepared, LayeredQuadBuffer unfinishedSink, BakedQuadUtils.Scratch scratch
   ) {
      if (info2.sprite != null) {
         handleMinMax(info2.spriteTable, spriteValue, info2.sprite, 16777215, colorsModelsValue, spriteValue3, spriteValue4, prepared, unfinishedSink, scratch);
      }
   }

   private BakedQuadUtils() {
   }

   private static void handle(float[][] floats, int index, int index2) {
      float[] bakedQuadValues = floats[index];
      floats[index] = floats[index2];
      floats[index2] = bakedQuadValues;
   }

   private static void handleSeedDefault(
      BakedQuadUtils.Prepared prepared,
      BakedQuadUtils.Info info2,
      int seedDefaultValue,
      int seedDefaultValue2,
      int seedDefaultValue3,
      LayeredQuadBuffer unfinishedSink,
      LayeredQuadBuffer layeredQuadBuffer,
      BakedQuadUtils.Scratch scratch
   ) {
      BakedQuadUtils.Info arrayInstance = prepared.getArrayInstance(seedDefaultValue, seedDefaultValue2 + 1, seedDefaultValue3);
      if (info2.hasModel) {
         if (info2.seedDefaultActive) {
            if (info2.seedQuadsSpec == null && SpriteFrameUtils.canFlag(1, info2.spriteTable) && arrayInstance.fullCube) {
               handleSpriteAmounts(info2, 1, seedDefaultValue, seedDefaultValue2, seedDefaultValue3, prepared, unfinishedSink, scratch);
            }
         } else {
            LayeredQuadBuffer unfinishedSink3 = info2.seedQuadsActive ? layeredQuadBuffer : unfinishedSink;
            if (!Block.shouldDrawSide(info2.defaultState, arrayInstance.defaultState, Direction.UP)) {
               scratch.random.setSeed(info2.defaultState.getRenderingSeed(scratch.mutable.set(seedDefaultValue, seedDefaultValue2, seedDefaultValue3)));
               scratch.seedQuadsBlockModelPartList.clear();
               info2.model.addParts(scratch.random, scratch.seedQuadsBlockModelPartList);

               for (int i = 0; i < scratch.seedQuadsBlockModelPartList.size(); i++) {
                  List quads = scratch.seedQuadsBlockModelPartList.get(i).getQuads(Direction.UP);

                  for (int j = 0; j < quads.size(); j++) {
                     handleAmountsShade(prepared, info2, (BakedQuad)quads.get(j), seedDefaultValue, seedDefaultValue2, seedDefaultValue3, false, unfinishedSink3, scratch);
                  }
               }
            }
         }
      }

      if (info2.fluidKind != 0 && (arrayInstance.fluidKind == info2.fluidKind || arrayInstance.fullCube)) {
         handleColorsModels(prepared, info2, seedDefaultValue, seedDefaultValue2, seedDefaultValue3, unfinishedSink, layeredQuadBuffer, scratch, true);
      }
   }

   static BakedQuadUtils.Caps getElseArray(BakedQuadUtils.Prepared prepared, int elseArrayValue, BakedQuadUtils.Job job) {
      BakedQuadUtils.Scratch scratch = new BakedQuadUtils.Scratch();
      LayeredQuadBuffer unfinishedSink = new LayeredQuadBuffer(1, 512);
      LayeredQuadBuffer layeredQuadBuffer = new LayeredQuadBuffer(1, 64);
      int elseArrayValue2 = prepared.elseArrayValue + elseArrayValue;

      for (int i = 0; i < prepared.array.length; i++) {
         if (job != null && job.elseArrayActive) {
            return null;
         }

         ElseBuilder.Region region = prepared.array[i];
         int elseArrayLimit = elseArrayValue2 - prepared.elseArrayTable[i];
         if (elseArrayLimit >= 0 && elseArrayLimit < region.elseArrayValue) {
            BakedQuadUtils.Info[] infos = prepared.stateModels[i];

            for (int j = 0; j < region.regionValue; j++) {
               for (int k = 0; k < region.elseValue; k++) {
                  int else2 = region.getElse(k, elseArrayLimit, j);
                  if (else2 >= 0 && else2 < infos.length) {
                     BakedQuadUtils.Info info = infos[else2];
                     if (!info.elseArrayActive) {
                        int arrayValue = prepared.elseModelsTable[i] + k;
                        int elseArrayValue4 = prepared.preparedTable[i] + j;
                        if (!prepared.elseArrayFlags[i] || prepared.getArrayFlag(arrayValue, elseArrayValue2, elseArrayValue4) == i) {
                           try {
                              handleSeedDefault(prepared, info, arrayValue, elseArrayValue2, elseArrayValue4, unfinishedSink, layeredQuadBuffer, scratch);
                           } catch (Throwable e) {
                              if (scratch.invocationCount++ == 0) {
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      unfinishedSink.finish();
      layeredQuadBuffer.finish();
      return new BakedQuadUtils.Caps(unfinishedSink, layeredQuadBuffer, elseArrayValue);
   }

   private static void handleAmountsShade(
      BakedQuadUtils.Prepared prepared, BakedQuadUtils.Info info2, BakedQuad bakedQuad, int shadeValue, int shadeValue2, int shadeValue3, boolean shadeFlag, LayeredQuadBuffer unfinishedSink, BakedQuadUtils.Scratch scratch
   ) {
      Direction face = bakedQuad.face();
      int index = face == null ? 1 : face.ordinal();

      for (int i = 0; i < 4; i++) {
         Vector3fc position = bakedQuad.getPosition(i);
         scratch.scratchFloats[i][0] = position.x();
         scratch.scratchFloats[i][1] = position.y();
         scratch.scratchFloats[i][2] = position.z();
         long texcoords = bakedQuad.getTexcoords(i);
         scratch.shadeFloats[i][0] = Vector2f.getX(texcoords);
         scratch.shadeFloats[i][1] = Vector2f.getY(texcoords);
      }

      int[] shadeValues = SpriteFrameUtils.seedQuadsInts[index];
      if (!SpriteFrameUtils.canFlag(scratch.scratchFloats[0], scratch.scratchFloats[1], scratch.scratchFloats[2], scratch.scratchFloats[3], shadeValues[0], shadeValues[1], shadeValues[2])) {
         handle(scratch.scratchFloats, 1, 3);
         handle(scratch.shadeFloats, 1, 3);
      }

      int modelQuadValue = bakedQuad.hasTint() ? info2.getColorDefault(bakedQuad.tintIndex(), prepared.modelsColorsData.colors()) : 16777215;
      float shadeFactor = bakedQuad.shade() ? ModelQuadUtils.getAmounts(index) : 1.0F;
      if (shadeFlag && bakedQuad.shade()) {
         handleAmounts(prepared, index, shadeValue, shadeValue2, shadeValue3, scratch);
      } else {
         for (int j = 0; j < 4; j++) {
            scratch.shadeTable[j] = 3;
         }
      }

      int shadeValue5 = ModelQuadUtils.canFlag(scratch.shadeTable[0], scratch.shadeTable[1], scratch.shadeTable[2], scratch.shadeTable[3]) ? 1 : 0;
      float shadeValue6 = shadeValue - prepared.seedQuadsValue;
      float shadeValue7 = shadeValue2 - prepared.elseArrayValue;
      float shadeValue8 = shadeValue3 - prepared.originZ;

      for (int k = 0; k < 4; k++) {
         int index2 = shadeValue5 + k & 3;
         int valueForIntFloat = ModelQuadUtils.getValueForIntFloat(modelQuadValue, shadeFactor * ModelQuadUtils.getAmountsForInt(scratch.shadeTable[index2]));
         unfinishedSink.addVertex(
            shadeValue6 + scratch.scratchFloats[index2][0],
            shadeValue7 + scratch.scratchFloats[index2][1],
            shadeValue8 + scratch.scratchFloats[index2][2],
            scratch.shadeFloats[index2][0],
            scratch.shadeFloats[index2][1],
            LayeredQuadBuffer.packOpaqueColor(valueForIntFloat)
         );
      }
   }

   public static BakedQuadUtils.CapsJob getThreadExecutor(BakedQuadUtils.Prepared prepared, int threadExecutorValue) {
      BakedQuadUtils.CapsJob capsJob = new BakedQuadUtils.CapsJob(prepared, threadExecutorValue);
      threadExecutor.execute(capsJob);
      return capsJob;
   }

   private static void handleAmounts(
      BakedQuadUtils.Prepared prepared, BakedQuadUtils.Info info2, int bakedQuadValue, int bakedQuadValue2, int bakedQuadValue3, LayeredQuadBuffer unfinishedSink, BakedQuadUtils.Scratch scratch
   ) {
      for (int i = 0; i < 6; i++) {
         if (SpriteFrameUtils.canFlag(i, info2.spriteTable)) {
            int[] bakedQuadValues = SpriteFrameUtils.seedQuadsInts[i];
            if (prepared.canCubeFull(bakedQuadValue + bakedQuadValues[0], bakedQuadValue2 + bakedQuadValues[1], bakedQuadValue3 + bakedQuadValues[2])) {
               continue;
            }
         }

         handleSpriteAmounts(info2, i, bakedQuadValue, bakedQuadValue2, bakedQuadValue3, prepared, unfinishedSink, scratch);
      }
   }

   public static final class BuildJob extends BakedQuadUtils.Job {
      private final BakedQuadUtils.Env modelsColorsData;
      private volatile BakedQuadUtils.Result upload;
      private final ElseBuilder else2ColorsModels;

      @Override
      void updateColorsModels() {
         this.upload = BakedQuadUtils.getElseArray(this.else2ColorsModels, this.modelsColorsData, this);
      }

      BuildJob(ElseBuilder else2, BakedQuadUtils.Env modelsColorsData) {
         this.else2ColorsModels = else2;
         this.modelsColorsData = modelsColorsData;
      }

      public BakedQuadUtils.Result getUpload() {
         return this.upload;
      }
   }

   public static final class Caps {
      public final LayeredQuadBuffer unfinishedSink;
      public final LayeredQuadBuffer unfinishedSink2CapsSchem;
      public final int capsSchemValue;

      Caps(LayeredQuadBuffer unfinishedSink, LayeredQuadBuffer unfinishedSink2, int capsSchemValue) {
         this.unfinishedSink = unfinishedSink;
         this.unfinishedSink2CapsSchem = unfinishedSink2;
         this.capsSchemValue = capsSchemValue;
      }
   }

   public static final class CapsJob extends BakedQuadUtils.Job {
      private final BakedQuadUtils.Prepared arrayMax;
      private final int capsSliceIndex;
      private volatile BakedQuadUtils.Caps colorsModelsCaps;

      public BakedQuadUtils.Caps getColorsModelsCaps() {
         return this.colorsModelsCaps;
      }

      CapsJob(BakedQuadUtils.Prepared arrayMax, int value) {
         this.arrayMax = arrayMax;
         this.capsSliceIndex = value;
      }

      @Override
      void updateColorsModels() {
         this.colorsModelsCaps = BakedQuadUtils.getElseArray(this.arrayMax, this.capsSliceIndex, this);
      }

      public int getValue() {
         return this.capsSliceIndex;
      }
   }

   public record Env(BlockModels models, BlockColors colors, Sprite water, Sprite lava, ModelChest chests) {
      private static Sprite getSpriteParticle(BlockModels state, BlockState models) {
         try {
            return state.getModelParticleSprite(models);
         } catch (Throwable e) {
            return null;
         }
      }

      public static BakedQuadUtils.Env getDefaultColors() {
         MinecraftClient instance = MinecraftClient.getInstance();
         BlockModels models = instance.getBlockRenderManager().getModels();

         ModelChest modelChest;
         try {
            modelChest = ModelChest.getModelTexture(instance);
         } catch (Throwable e) {
            modelChest = null;
         }

         return new BakedQuadUtils.Env(
            models, instance.getBlockColors(), getSpriteParticle(models, Blocks.WATER.getDefaultState()), getSpriteParticle(models, Blocks.LAVA.getDefaultState()), modelChest
         );
      }
   }

   static final class Info {
      private final int[] colorDefaultTable = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      final int fluidKind;
      final boolean elseArrayActive;
      final int luminance;
      final float[] spriteTable;
      final BlockStateModel model;
      final boolean seedQuadsActive;
      final ModelChest.Spec seedQuadsSpec;
      static final BakedQuadUtils.Info INSTANCE = new BakedQuadUtils.Info();
      final boolean seedDefaultActive;
      final boolean hasModel;
      final boolean fullCube;
      final BlockState defaultState;
      final Sprite sprite;

      private static float getMaxX(double bakedQuadLimit) {
         return (float)(bakedQuadLimit < 0.0 ? 0.0 : (bakedQuadLimit > 1.0 ? 1.0 : bakedQuadLimit));
      }

      private static boolean canQuadsParts(BlockStateModel blockStateModel, Random random) {
         try {
            random.setSeed(42L);

            for (BlockModelPart blockModelPart : blockStateModel.getParts(random)) {
               if (!blockModelPart.getQuads(null).isEmpty()) {
                  return true;
               }

               for (Direction direction : BakedQuadUtils.directions) {
                  if (!blockModelPart.getQuads(direction).isEmpty()) {
                     return true;
                  }
               }
            }
         } catch (Throwable e) {
         }

         return false;
      }

      private static Sprite getSpriteParticle(BlockStateModel blockStateModel, BlockState state, BakedQuadUtils.Env env) {
         try {
            Sprite particleSprite = blockStateModel.particleSprite();
            if (particleSprite != null) {
               return particleSprite;
            }
         } catch (Throwable e) {
         }

         try {
            return env.models().getModelParticleSprite(state);
         } catch (Throwable e2) {
            return null;
         }
      }

      int getColorDefault(int index2, BlockColors blockColors) {
         if (index2 >= 0 && index2 < this.colorDefaultTable.length && this.colorDefaultTable[index2] != Integer.MIN_VALUE) {
            return this.colorDefaultTable[index2];
         } else {
            int colorDefaultValue;
            try {
               colorDefaultValue = blockColors.getColor(this.defaultState, null, null, index2);
            } catch (Throwable e) {
               colorDefaultValue = -1;
            }

            int colorDefault = colorDefaultValue == -1 ? 16777215 : colorDefaultValue & 16777215;
            if (index2 >= 0 && index2 < this.colorDefaultTable.length) {
               this.colorDefaultTable[index2] = colorDefault;
            }

            return colorDefault;
         }
      }

      private static int getFluidLava(BlockState state) {
         try {
            FluidState fluidState = state.getFluidState();
            if (fluidState == null || fluidState.isEmpty()) {
               return 0;
            }

            if (fluidState.isIn(FluidTags.WATER)) {
               return 1;
            }

            if (fluidState.isIn(FluidTags.LAVA)) {
               return 2;
            }
         } catch (Throwable e) {
         }

         return 0;
      }

      private static float[] getMinMax(BlockState state) {
         try {
            VoxelShape outlineShape = state.getOutlineShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
            if (outlineShape != null && !outlineShape.isEmpty()) {
               Box boundingBox = outlineShape.getBoundingBox();
               float[] floats = new float[]{
                  getMaxX(boundingBox.minX), getMaxX(boundingBox.minY), getMaxX(boundingBox.minZ), getMaxX(boundingBox.maxX), getMaxX(boundingBox.maxY), getMaxX(boundingBox.maxZ)
               };

               for (int i = 0; i < 3; i++) {
                  if (floats[i + 3] - floats[i] < 0.0625F) {
                     float bakedQuadValue = (floats[i] + floats[i + 3]) * 0.5F;
                     floats[i] = Math.max(0.0F, bakedQuadValue - 0.03125F);
                     floats[i + 3] = Math.min(1.0F, floats[i] + 0.0625F);
                  }
               }

               return floats;
            } else {
               return BakedQuadUtils.bakedQuadTable;
            }
         } catch (Throwable e) {
            return BakedQuadUtils.bakedQuadTable;
         }
      }

      Info(BlockState defaultState, BakedQuadUtils.Env env, Random random) {
         this.defaultState = defaultState;
         this.elseArrayActive = false;
         this.fullCube = defaultState.isOpaqueFullCube();
         this.luminance = defaultState.getLuminance();
         this.fluidKind = getFluidLava(defaultState);
         boolean bakedQuadFlag = defaultState.getRenderType() != BlockRenderType.INVISIBLE;
         BlockStateModel model = null;
         if (bakedQuadFlag) {
            try {
               model = env.models().getModel(defaultState);
            } catch (Throwable e) {
            }
         }

         this.model = model;
         this.hasModel = model != null;

         BlockRenderLayer blockRenderLayer;
         try {
            blockRenderLayer = BlockRenderLayers.getBlockLayer(defaultState);
         } catch (Throwable e2) {
            blockRenderLayer = BlockRenderLayer.SOLID;
         }

         this.seedQuadsActive = blockRenderLayer == BlockRenderLayer.TRANSLUCENT || blockRenderLayer == BlockRenderLayer.TRIPWIRE;
         boolean flag = model != null && !canQuadsParts(model, random);
         this.seedDefaultActive = flag;
         this.spriteTable = flag ? getMinMax(defaultState) : BakedQuadUtils.bakedQuadTable;
         this.sprite = flag ? getSpriteParticle(model, defaultState, env) : null;
         this.seedQuadsSpec = flag && env.chests() != null ? env.chests().getPositiveTranslate(defaultState) : null;
      }

      private Info() {
         this.defaultState = Blocks.AIR.getDefaultState();
         this.elseArrayActive = true;
         this.fullCube = false;
         this.hasModel = false;
         this.seedQuadsActive = false;
         this.luminance = 0;
         this.fluidKind = 0;
         this.model = null;
         this.seedDefaultActive = false;
         this.spriteTable = BakedQuadUtils.bakedQuadTable;
         this.sprite = null;
         this.seedQuadsSpec = null;
      }
   }

   public static class Job implements Runnable {
      volatile Throwable throwable;
      volatile boolean jobActive;
      volatile boolean elseArrayActive;

      public final boolean isFlag() {
         return this.jobActive;
      }

      void updateColorsModels() {
      }

      public final void updateFlag() {
         this.elseArrayActive = true;
      }

      public final boolean isElseArrayActive() {
         return this.elseArrayActive;
      }

      @Override
      public final void run() {
         try {
            if (!this.elseArrayActive) {
               this.updateColorsModels();
            }
         } catch (Throwable e) {
            this.throwable = e;
         } finally {
            this.jobActive = true;
         }
      }

      public final Throwable getThrowable() {
         return this.throwable;
      }
   }

   public static final class Prepared {
      final BakedQuadUtils.Info[][] stateModels;
      final int originZ;
      final int[] elseArrayTable;
      final int[] elseModelsTable;
      final BakedQuadUtils.Env modelsColorsData;
      final ElseBuilder.Region[] array;
      final boolean[] elseArrayFlags;
      final int elseArrayValue;
      final int seedQuadsValue;
      final int spanSize;
      final int[] preparedTable;

      private boolean canElseArray(int index, int k) {
         ElseBuilder.Region region = this.array[index];
         ElseBuilder.Region region2 = this.array[k];
         return this.elseModelsTable[index] < this.elseModelsTable[k] + region2.elseValue
            && this.elseModelsTable[k] < this.elseModelsTable[index] + region.elseValue
            && this.elseArrayTable[index] < this.elseArrayTable[k] + region2.elseArrayValue
            && this.elseArrayTable[k] < this.elseArrayTable[index] + region.elseArrayValue
            && this.preparedTable[index] < this.preparedTable[k] + region2.regionValue
            && this.preparedTable[k] < this.preparedTable[index] + region.regionValue;
      }

      BakedQuadUtils.Info getArrayInstance(int arrayValue, int arrayValue2, int arrayValue3) {
         for (int i = 0; i < this.array.length; i++) {
            BakedQuadUtils.Info elseModels = this.getElseModels(i, arrayValue, arrayValue2, arrayValue3);
            if (elseModels != null && !elseModels.elseArrayActive) {
               return elseModels;
            }
         }

         return BakedQuadUtils.Info.INSTANCE;
      }

      Prepared(ElseBuilder elseBuilder, BakedQuadUtils.Env modelsColorsData, Random random) {
         this.modelsColorsData = modelsColorsData;
         int itemsSize = elseBuilder.elseRegionList.size();
         this.array = elseBuilder.elseRegionList.toArray(new ElseBuilder.Region[0]);
         this.elseModelsTable = new int[itemsSize];
         this.elseArrayTable = new int[itemsSize];
         this.preparedTable = new int[itemsSize];
         this.stateModels = new BakedQuadUtils.Info[itemsSize][];
         this.elseArrayFlags = new boolean[itemsSize];
         IdentityHashMap identityHashMap = new IdentityHashMap();
         int value = Integer.MAX_VALUE;
         int intersectsCenterBound = Integer.MAX_VALUE;
         int maxValue3 = Integer.MAX_VALUE;
         int preparedBound = Integer.MIN_VALUE;

         for (int i = 0; i < itemsSize; i++) {
            ElseBuilder.Region region = this.array[i];
            this.elseModelsTable[i] = region.getElseRegionEnd();
            this.elseArrayTable[i] = region.getElseOrElse2Else();
            this.preparedTable[i] = region.getElse();
            value = Math.min(value, this.elseModelsTable[i]);
            intersectsCenterBound = Math.min(intersectsCenterBound, this.elseArrayTable[i]);
            maxValue3 = Math.min(maxValue3, this.preparedTable[i]);
            preparedBound = Math.max(preparedBound, this.elseArrayTable[i] + region.elseArrayValue - 1);
            BlockState[] blockStates = region.blockStates;
            BakedQuadUtils.Info[] infos = new BakedQuadUtils.Info[blockStates == null ? 0 : blockStates.length];

            for (int j = 0; j < infos.length; j++) {
               BlockState blockState = blockStates[j];
               if (blockState != null && !blockState.isAir()) {
                  BakedQuadUtils.Info info = (BakedQuadUtils.Info)identityHashMap.get(blockState);
                  if (info == null) {
                     info = new BakedQuadUtils.Info(blockState, modelsColorsData, random);
                     identityHashMap.put(blockState, info);
                  }

                  infos[j] = info;
               } else {
                  infos[j] = BakedQuadUtils.Info.INSTANCE;
               }
            }

            this.stateModels[i] = infos;

            for (int k = 0; k < i; k++) {
               if (this.canElseArray(i, k)) {
                  this.elseArrayFlags[i] = true;
                  break;
               }
            }
         }

         if (itemsSize == 0) {
            maxValue3 = 0;
            intersectsCenterBound = 0;
            value = 0;
            preparedBound = 0;
         }

         this.seedQuadsValue = value;
         this.elseArrayValue = intersectsCenterBound;
         this.originZ = maxValue3;
         this.spanSize = Math.max(1, preparedBound - intersectsCenterBound + 1);
      }

      int getArrayFlag(int arrayValue, int arrayValue2, int arrayValue3) {
         for (int i = 0; i < this.array.length; i++) {
            BakedQuadUtils.Info elseModels = this.getElseModels(i, arrayValue, arrayValue2, arrayValue3);
            if (elseModels != null && !elseModels.elseArrayActive) {
               return i;
            }
         }

         return -1;
      }

      boolean canCubeFull(int cubeFullValue, int cubeFullValue2, int cubeFullValue3) {
         return this.getArrayInstance(cubeFullValue, cubeFullValue2, cubeFullValue3).fullCube;
      }

      private BakedQuadUtils.Info getElseModels(int index, int elseModelsValue, int elseModelsValue2, int elseModelsValue3) {
         ElseBuilder.Region region = this.array[index];
         int elseModelsLimit = elseModelsValue - this.elseModelsTable[index];
         int elseFactor = elseModelsValue2 - this.elseArrayTable[index];
         int k = elseModelsValue3 - this.preparedTable[index];
         if (elseModelsLimit >= 0 && elseFactor >= 0 && k >= 0 && elseModelsLimit < region.elseValue && elseFactor < region.elseArrayValue && k < region.regionValue) {
            int else2 = region.getElse(elseModelsLimit, elseFactor, k);
            BakedQuadUtils.Info[] infos = this.stateModels[index];
            return else2 >= 0 && else2 < infos.length ? infos[else2] : null;
         } else {
            return null;
         }
      }
   }

   public static final class Result {
      public final LayeredQuadBuffer unfinishedSink;
      public final float cubeScale;
      public final int vertexCount;
      public final boolean cubeActive;
      public final LayeredQuadBuffer unfinishedSink2UnfinishedSin;
      public final float cubeAmount;
      public final int capsSchemValue;
      public final float secondaryRed;
      public final float secondaryBlue;
      public final int gpuUploadValue;
      public final BakedQuadUtils.Prepared arrayMax;
      public final float uploadParseAmount;
      public final float secondaryAlpha;
      public final float primaryRed;
      public final LayeredQuadBuffer upload;

      Result(
         LayeredQuadBuffer upload,
         LayeredQuadBuffer unfinishedSink,
         LayeredQuadBuffer unfinishedSink2,
         BakedQuadUtils.Prepared arrayMax,
         int value,
         boolean flag,
         int value2,
         int capsSchemValue,
         float amount,
         float amount2,
         float amount3,
         float amount4,
         float amount5,
         float amount6,
         float amount7
      ) {
         this.upload = upload;
         this.unfinishedSink = unfinishedSink;
         this.unfinishedSink2UnfinishedSin = unfinishedSink2;
         this.arrayMax = arrayMax;
         this.vertexCount = value;
         this.cubeActive = flag;
         this.gpuUploadValue = value2;
         this.capsSchemValue = capsSchemValue;
         this.primaryRed = amount;
         this.uploadParseAmount = amount2;
         this.cubeAmount = amount3;
         this.secondaryRed = amount4;
         this.cubeScale = amount5;
         this.secondaryBlue = amount6;
         this.secondaryAlpha = amount7;
      }

      public boolean isUnfinishedSink() {
         return this.upload.getQuadCount() + this.unfinishedSink.getQuadCount() + this.unfinishedSink2UnfinishedSin.getQuadCount() == 0;
      }

      public int getUnfinishedSink(int unfinishedSinkValue) {
         return unfinishedSinkValue < 0 ? this.unfinishedSink.getQuadCount() : this.unfinishedSink.getClampedLayerEndQuad(this.capsSchemValue + unfinishedSinkValue);
      }

      public int getUnfinishedSinkForInt(int unfinishedSinkValue) {
         return unfinishedSinkValue < 0 ? this.unfinishedSink2UnfinishedSin.getQuadCount() : this.unfinishedSink2UnfinishedSin.getClampedLayerEndQuad(this.capsSchemValue + unfinishedSinkValue);
      }

      public int getUploadCaps(int uploadCapsValue) {
         return uploadCapsValue < 0 ? this.upload.getQuadCount() : this.upload.getClampedLayerEndQuad(this.capsSchemValue + uploadCapsValue);
      }
   }

   private static final class Scratch {
      final float[][] shadeFloats;
      final Mutable mutable;
      final int[] shadeTable;
      final Random random = Random.create(42L);
      final float[][] scratchFloats;
      int invocationCount;
      final float[] scratchTable;
      final List<BlockModelPart> seedQuadsBlockModelPartList = new ArrayList<>();

      private Scratch() {
         this.mutable = new Mutable();
         this.scratchFloats = new float[4][3];
         this.shadeFloats = new float[4][2];
         this.shadeTable = new int[4];
         this.scratchTable = new float[3];
      }
   }
}
