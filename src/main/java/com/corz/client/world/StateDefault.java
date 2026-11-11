package com.corz.client.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

public final class StateDefault {
   public final Map<Long, BlockState> colorsModelsBlockStateMap = new HashMap<>();
   public final List<StateDefault.Decor> defaultDecorList = new ArrayList<>();
   public final List<float[]> defaultFloatArrayList;
   public static final float[] defaultTable = new float[]{-7.0F, 1.5F, 0.55F};
   public static final float[] entityRenderTable = new float[]{-3.5F, 0.0F, 5.35F, 20.0F};
   public static final float[] table = new float[]{3.5F, 0.0F, 5.35F, -20.0F};
   public final List<float[]> entityRenderFloatArrayList;
   public final List<float[]> floatArrayList;
   static final int SINGLE_STEP = 1;
   public final List<float[]> spruceDefaultFloatArrayList;
   public final List<float[]> ambFloatArrayList;
   public final List<StateDefault.Light> ambItems = new ArrayList<>();
   public final List<float[]> entityRenderPoints;
   public static final float[] DEFAULT_POSITION = new float[]{5.5F, 5.3F, 2.5F};
   static final float[] DEFAULT_ALT_POSITION = new float[]{-0.5F, 7.0F, 5.2F};
   public final List<float[]> defaultPoints;

   private void updateDefaultSnow() {
      BlockState defaultState = Blocks.SNOW_BLOCK.getDefaultState();
      this.handle(-17, -1, -16, 16, -1, -2, defaultState);

      for (int i = -17; i <= 16; i++) {
         for (int j = -16; j <= -2; j++) {
            float defaultSnowLimit = (float)(Math.sin(i * 0.55 + j * 0.3) + Math.sin(i * 0.21 - j * 0.47));
            if (defaultSnowLimit > 0.9F) {
               this.handle(i, 0, j, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, defaultSnowLimit > 1.4F ? 4 : 2));
            }
         }
      }

      this.handleSpruceDefault(-7, -7, 5);
      this.handleSpruceDefault(5, -9, 6);
      this.handleSpruceDefault(-1, -14, 7);
      this.handleSpruceDefault(10, -6, 4);
      this.handleSpruceDefault(-12, -11, 6);
      this.handleSpruceDefault(1, -6, 3);
   }

   private void handle(int defaultBound, int defaultBound2, int intersectsCenterBound, int defaultBound4, int spotBound, int defaultBound6, BlockState state) {
      for (int i = Math.min(defaultBound, defaultBound4); i <= Math.max(defaultBound, defaultBound4); i++) {
         for (int j = Math.min(defaultBound2, spotBound); j <= Math.max(defaultBound2, spotBound); j++) {
            for (int k = Math.min(intersectsCenterBound, defaultBound6); k <= Math.max(intersectsCenterBound, defaultBound6); k++) {
               this.handle(i, j, k, state);
            }
         }
      }
   }

   public static boolean canFlag(float defaultValue) {
      return defaultValue < -1.0F;
   }

   private void handleSpruceDefault(int spruceDefaultValue, int j) {
      BlockState with = Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
      this.handle(spruceDefaultValue, 0, j, with);
      this.handle(spruceDefaultValue - 1, 0, j, Blocks.SPRUCE_TRAPDOOR.getDefaultState().with(TrapdoorBlock.OPEN, true).with(TrapdoorBlock.FACING, Direction.WEST));
      this.handle(spruceDefaultValue + 1, 0, j, Blocks.SPRUCE_TRAPDOOR.getDefaultState().with(TrapdoorBlock.OPEN, true).with(TrapdoorBlock.FACING, Direction.EAST));
   }

   private void updateDefaultState() {
      BlockState defaultState = Blocks.BRICKS.getDefaultState();
      this.handle(-9, 0, -1, -6, 6, -1, defaultState);
      this.handle(-9, 0, 0, -6, 6, 0, defaultState);
      this.handle(-8, 0, 0, -7, 0, 0, Blocks.STONE_BRICKS.getDefaultState());
      this.handle(-8, 1, 0, -7, 2, 0, null);
      BlockState with = Blocks.CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, true).with(CampfireBlock.FACING, Direction.SOUTH);
      this.handle(-8, 1, 0, with);
      this.handle(-7, 1, 0, with);
      this.handle(-9, -1, 0, -6, -1, 1, Blocks.STONE_BRICKS.getDefaultState());
      BlockState with2 = Blocks.SPRUCE_TRAPDOOR
         .getDefaultState()
         .with(TrapdoorBlock.HALF, BlockHalf.TOP)
         .with(TrapdoorBlock.OPEN, false)
         .with(TrapdoorBlock.FACING, Direction.SOUTH);
      this.handle(-9, 3, 1, -6, 3, 1, with2);
      float defaultValue = 4.0F;
      this.defaultDecorList.add(new StateDefault.Decor(Blocks.RED_CANDLE.getDefaultState().with(CandleBlock.CANDLES, 3).with(CandleBlock.LIT, true), -9.0F, defaultValue, 1.0F));
      this.ambFloatArrayList.add(new float[]{-8.5F, defaultValue + 0.45F, 1.5F});
      this.defaultDecorList.add(new StateDefault.Decor(Blocks.POTTED_SPRUCE_SAPLING.getDefaultState(), -8.0F, defaultValue, 1.0F));
      this.defaultDecorList.add(new StateDefault.Decor(Blocks.WHITE_CANDLE.getDefaultState().with(CandleBlock.CANDLES, 2).with(CandleBlock.LIT, true), -7.0F, defaultValue, 1.0F));
      this.ambFloatArrayList.add(new float[]{-6.5F, defaultValue + 0.42F, 1.5F});
      this.defaultDecorList.add(new StateDefault.Decor(Blocks.LANTERN.getDefaultState(), -6.0F, defaultValue, 1.0F));
      this.defaultFloatArrayList.add(new float[]{-5.5F, defaultValue + 0.3F, 1.5F});

      for (int i = 0; i < 4; i++) {
         this.entityRenderPoints.add(new float[]{-8.5F + i, 3.78F, 2.03F, (i % 2 == 0 ? 1.0F : -1.0F) * 0.06F});
      }
   }

   public boolean canOpaqueCube(int opaqueCubeValue, int r, int j) {
      BlockState blockState = this.colorsModelsBlockStateMap.get(getTime(opaqueCubeValue, r, j));
      return blockState != null && blockState.isOpaqueFullCube();
   }

   private void handleSpruceDefault(int spruceDefaultValue, int spruceDefaultValue2, int spruceDefaultLimit) {
      BlockState defaultState = Blocks.SPRUCE_LOG.getDefaultState();
      BlockState with = Blocks.SPRUCE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true);
      BlockState with2 = Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 1);

      for (int i = 0; i < spruceDefaultLimit - 1; i++) {
         this.handle(spruceDefaultValue, i, spruceDefaultValue2, defaultState);
      }

      for (int j = 1; j <= spruceDefaultLimit; j++) {
         int spruceDefaultValue3 = spruceDefaultLimit - j;
         float spruceDefaultFactor = spruceDefaultValue3 == 0 ? 0.0F : (spruceDefaultValue3 % 2 == 1 ? 1.0F : 1.8F) * Math.min(1.0F, 0.55F + spruceDefaultValue3 * 0.18F);
         int ceil2 = (int)Math.ceil(spruceDefaultFactor);

         for (int k = -ceil2; k <= ceil2; k++) {
            for (int m = -ceil2; m <= ceil2; m++) {
               if (!(k * k + m * m > spruceDefaultFactor * spruceDefaultFactor + 0.01F) && (j >= spruceDefaultLimit - 1 || k != 0 || m != 0)) {
                  this.handle(spruceDefaultValue + k, j, spruceDefaultValue2 + m, with);
               }
            }
         }
      }

      for (int p = -2; p <= 2; p++) {
         for (int q = -2; q <= 2; q++) {
            for (int r = spruceDefaultLimit; r >= 1; r--) {
               BlockState blockState = this.getBlockState(spruceDefaultValue + p, r, spruceDefaultValue2 + q);
               if (blockState != null && blockState.getBlock() == Blocks.SPRUCE_LEAVES) {
                  if (this.getBlockState(spruceDefaultValue + p, r + 1, spruceDefaultValue2 + q) == null) {
                     this.handle(spruceDefaultValue + p, r + 1, spruceDefaultValue2 + q, with2);
                  }
                  break;
               }
            }
         }
      }
   }

   BlockState getBlockState(int blockValue, int r, int j) {
      return this.colorsModelsBlockStateMap.get(getTime(blockValue, r, j));
   }

   private void updateDefaultStateDefaultDecorList() {
      BlockState defaultState = Blocks.BOOKSHELF.getDefaultState();
      this.handle(-5, 0, 0, -4, 2, 0, defaultState);
      this.defaultDecorList.add(new StateDefault.Decor(Blocks.LANTERN.getDefaultState(), -5.0F, 3.0F, 0.0F));
      this.defaultFloatArrayList.add(new float[]{-4.5F, 3.3F, 0.5F});
      this.defaultDecorList.add(new StateDefault.Decor(Blocks.POTTED_POPPY.getDefaultState(), -4.0F, 3.0F, 0.0F));
      this.handleSpruceDefault((int)Math.floor(entityRenderTable[0]), (int)Math.floor(entityRenderTable[2]));
      this.handleSpruceDefault((int)Math.floor(table[0]), (int)Math.floor(table[2]));
      this.updateCarpetDefault();
      BlockState with = Blocks.IRON_CHAIN.getDefaultState().with(PillarBlock.AXIS, Axis.Y);
      BlockState with2 = Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true);

      for (int defaultValue : new int[]{-3, 2}) {
         this.handle(defaultValue, 6, 4, with);
         this.handle(defaultValue, 5, 4, with2);
         this.defaultFloatArrayList.add(new float[]{defaultValue + 0.5F, 5.3F, 4.5F});
      }

      this.handle(-8.8F, 8.8F, 6.0F, 3.08F);
   }

   public static float[][] getAmounts() {
      return new float[][]{
         {5.5F, 5.6F, 0.8F, 14.0F},
         {7.45F, 5.0F, 0.9F, -22.0F},
         {5.55F, 6.6F, 0.75F, 33.0F},
         {7.2F, 6.5F, 0.85F, 8.0F},
         {6.1F, 7.4F, 0.8F, -40.0F},
         {4.9F, 7.1F, 0.75F, 52.0F},
         {4.8F, 6.1F, 0.7F, 21.0F},
         {7.0F, 7.3F, 0.8F, -5.0F},
         {6.15F, 5.15F, 0.75F, -33.0F}
      };
   }

   public static int getValue(long defaultValue) {
      return (int)(defaultValue >>> 40 & 1048575L) - 512;
   }

   StateDefault() {
      this.spruceDefaultFloatArrayList = new ArrayList<>();
      this.entityRenderFloatArrayList = new ArrayList<>();
      this.entityRenderPoints = new ArrayList<>();
      this.ambFloatArrayList = new ArrayList<>();
      this.defaultFloatArrayList = new ArrayList<>();
      this.floatArrayList = new ArrayList<>();
      this.defaultPoints = new ArrayList<>();
   }

   private void updateSpruceDefault() {
      BlockState defaultState = Blocks.SPRUCE_LOG.getDefaultState();
      BlockState with = Blocks.SPRUCE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true);
      this.handle(5, 0, 2, defaultState);
      this.handle(5, 1, 2, defaultState);
      float[] floats = new float[]{0.0F, 2.3F, 1.9F, 1.35F, 0.7F};

      for (int i = 1; i <= 4; i++) {
         float spruceDefaultFactor = floats[i];
         int spruceDefaultLimit = (int)Math.ceil(spruceDefaultFactor);

         for (int j = -spruceDefaultLimit; j <= spruceDefaultLimit; j++) {
            for (int k = -spruceDefaultLimit; k <= spruceDefaultLimit; k++) {
               if (!(j * j + k * k > spruceDefaultFactor * spruceDefaultFactor + 0.01F) && (i != 1 || j != 0 || k != 0)) {
                  this.handle(5 + j, i, 2 + k, with);
               }
            }
         }
      }

      int[] ints = new int[]{-45747, -15811, -11688193, -12723068, -34087, -3626};
      int spruceDefaultCounter = 0;

      for (int m = 0; m < 4; m++) {
         float spruceDefaultValue = 1.35F + m * 0.92F;
         float max2 = Math.max(0.5F, 2.4F - m * 0.55F);
         int spruceDefaultLimit2 = 7 - m;

         for (int p = 0; p < spruceDefaultLimit2; p++) {
            float angle = (float)(Math.PI * (0.08 + 0.84 * (p + 0.5F * (m & 1)) / spruceDefaultLimit2));
            float spruceDefaultValue2 = 5.5F - (float)Math.cos(angle) * max2;
            float spruceDefaultValue3 = 2.5F + (float)Math.sin(angle) * max2;
            this.spruceDefaultFloatArrayList.add(new float[]{spruceDefaultValue2, spruceDefaultValue + 0.18F * (float)Math.sin(p * 2.1F + m), spruceDefaultValue3, ints[spruceDefaultCounter++ % ints.length]});
         }
      }

      float[][] floats2 = new float[][]{{3.8F, 2.9F, 0.9F, 18.0F}, {5.0F, 3.25F, 0.75F, -12.0F}, {6.4F, 2.8F, 1.0F, 28.0F}, {4.4F, 2.2F, 0.7F, 40.0F}};

      for (int q = 0; q < floats2.length; q++) {
         this.entityRenderFloatArrayList.add(new float[]{floats2[q][0], 0.0F, floats2[q][1], floats2[q][2], floats2[q][3], q % 4});
      }
   }

   private void updateDefaultSpruce() {
      BlockState defaultState = Blocks.SPRUCE_PLANKS.getDefaultState();
      BlockState with = Blocks.SPRUCE_LOG.getDefaultState().with(PillarBlock.AXIS, Axis.X);
      BlockState with2 = Blocks.SPRUCE_LOG.getDefaultState().with(PillarBlock.AXIS, Axis.Z);
      BlockState with3 = Blocks.STRIPPED_SPRUCE_LOG.getDefaultState().with(PillarBlock.AXIS, Axis.Y);
      BlockState with4 = Blocks.STRIPPED_SPRUCE_LOG.getDefaultState().with(PillarBlock.AXIS, Axis.X);
      BlockState with5 = Blocks.STRIPPED_SPRUCE_LOG.getDefaultState().with(PillarBlock.AXIS, Axis.Z);
      BlockState with6 = Blocks.DARK_OAK_LOG.getDefaultState().with(PillarBlock.AXIS, Axis.X);
      this.handle(-10, -1, -1, 9, -1, 12, defaultState);
      this.handle(-10, 7, -1, 9, 7, 12, defaultState);
      this.handle(-9, 1, -1, 8, 6, -1, with);
      this.handle(-9, 0, -1, 8, 0, -1, with4);
      this.handle(-10, 1, 0, -10, 6, 12, with2);
      this.handle(9, 1, 0, 9, 6, 12, with2);
      this.handle(-10, 0, 0, -10, 0, 12, with5);
      this.handle(9, 0, 0, 9, 0, 12, with5);
      this.handle(-10, 0, -1, -10, 6, -1, with3);
      this.handle(9, 0, -1, 9, 6, -1, with3);

      for (int defaultSpruceValue : new int[]{2, 5, 8}) {
         this.handle(-9, 6, defaultSpruceValue, 8, 6, defaultSpruceValue, with6);
      }
   }

   private void updateAmbAmounts() {
      this.ambItems.add(new StateDefault.Light(defaultTable[0], defaultTable[1], defaultTable[2], 1.0F, 0.55F, 0.22F, 4.2F, 4.2F, true));

      for (float[] ambValues : this.defaultFloatArrayList) {
         this.ambItems.add(new StateDefault.Light(ambValues[0], ambValues[1], ambValues[2], 1.0F, 0.74F, 0.42F, 0.95F, 2.3F, false));
      }

      for (float[] ambValues2 : this.ambFloatArrayList) {
         this.ambItems.add(new StateDefault.Light(ambValues2[0], ambValues2[1], ambValues2[2], 1.0F, 0.7F, 0.38F, 0.55F, 1.4F, false));
      }

      this.ambItems.add(new StateDefault.Light(5.5F, 3.0F, 4.2F, 0.9F, 0.72F, 0.62F, 0.75F, 2.6F, false));
      this.ambItems.add(new StateDefault.Light(-0.5F, 2.6F, -0.4F, 0.42F, 0.55F, 0.9F, 0.7F, 3.4F, false));
   }

   static long getTime(int defaultValue, int r, int j) {
      return (long)(defaultValue + 512) << 40 | (long)(r + 512) << 20 | j + 512;
   }

   private void handle(float defaultValue, float defaultValue2, float defaultValue3, float defaultValue4) {
      byte defaultDivisor = 5;
      float defaultFactor = (defaultValue2 - defaultValue) / defaultDivisor;
      int[] ints = new int[]{-45747, -15811, -12723068, -11688193, -3626};
      int defaultCounter = 0;

      for (int i = 0; i < defaultDivisor; i++) {
         float defaultValue5 = defaultValue + i * defaultFactor;

         for (int j = 0; j <= 24; j++) {
            float defaultFactor2 = j / 24.0F;
            float defaultValue6 = defaultValue5 + defaultFactor * defaultFactor2;
            float defaultValue7 = 2.2F * defaultFactor2 * (1.0F - defaultFactor2);
            float defaultValue8 = (float)Math.sin(defaultValue6 * 7.3F) * 0.04F;
            this.floatArrayList.add(new float[]{defaultValue6, defaultValue3 - defaultValue7 + defaultValue8, defaultValue4 + (float)Math.cos(defaultValue6 * 5.1F) * 0.05F, 0.075F + 0.02F * (float)Math.sin(defaultValue6 * 3.7F)});
            if (j % 4 == 2) {
               this.defaultPoints.add(new float[]{defaultValue6, defaultValue3 - defaultValue7 - 0.12F, defaultValue4 + 0.06F, ints[defaultCounter++ % ints.length]});
            }
         }

         this.floatArrayList.add(new float[]{defaultValue5, defaultValue3 - 0.02F, defaultValue4 + 0.04F, -0.11F});
      }

      this.floatArrayList.add(new float[]{defaultValue2, defaultValue3 - 0.02F, defaultValue4 + 0.04F, -0.11F});
   }

   private void updateDefaultState3() {
      BlockState with = Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true);
      BlockState with2 = Blocks.STRIPPED_SPRUCE_LOG.getDefaultState().with(PillarBlock.AXIS, Axis.Y);
      BlockState with3 = Blocks.STRIPPED_SPRUCE_LOG.getDefaultState().with(PillarBlock.AXIS, Axis.X);
      this.handle(-3, 1, -1, 2, 4, -1, with);
      this.handle(-4, 1, -1, -4, 4, -1, with2);
      this.handle(3, 1, -1, 3, 4, -1, with2);
      this.handle(-4, 5, -1, 3, 5, -1, with3);
      BlockState with4 = Blocks.SPRUCE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP);
      this.handle(-3, 0, 0, 2, 0, 0, with4);
      BlockState defaultState = Blocks.RED_CARPET.getDefaultState();

      for (int i = -3; i <= 2; i++) {
         this.handle(i, 1, 0, (i & 1) == 0 ? defaultState : Blocks.WHITE_CARPET.getDefaultState());
      }
   }

   public static StateDefault getStateDefault() {
      StateDefault stateDefault = new StateDefault();
      stateDefault.updateDefaultSpruce();
      stateDefault.updateDefaultState3();
      stateDefault.updateDefaultState();
      stateDefault.updateDefaultStateDefaultDecorList();
      stateDefault.updateSpruceDefault();
      stateDefault.updateDefaultSnow();
      stateDefault.updateAmbAmounts();
      return stateDefault;
   }

   private void handle(int defaultValue, int j, int defaultValue3, BlockState state) {
      if (state == null) {
         this.colorsModelsBlockStateMap.remove(getTime(defaultValue, j, defaultValue3));
      } else {
         this.colorsModelsBlockStateMap.put(getTime(defaultValue, j, defaultValue3), state);
      }
   }

   private void updateCarpetDefault() {
      BlockState defaultState = Blocks.RED_CARPET.getDefaultState();
      BlockState defaultState2 = Blocks.WHITE_CARPET.getDefaultState();
      BlockState defaultState3 = Blocks.GREEN_CARPET.getDefaultState();
      BlockState[] blockStates = new BlockState[]{defaultState, defaultState3, defaultState2, defaultState, defaultState, defaultState2, defaultState3, defaultState};

      for (int i = -4; i <= 3; i++) {
         this.handle(i, 0, 2, (i & 1) == 0 ? defaultState : defaultState2);
         this.handle(i, 0, 3, blockStates[i + 4]);
         this.handle(i, 0, 4, (i & 1) == 0 ? defaultState2 : defaultState);
      }
   }

   public static int getValueForLong(long defaultValue) {
      return (int)(defaultValue & 1048575L) - 512;
   }

   public static int getValueInt(long defaultValue) {
      return (int)(defaultValue >>> 20 & 1048575L) - 512;
   }

   public record Decor(BlockState state, float x, float y, float z) {
   }

   public record Light(float x, float y, float z, float r, float g, float b, float strength, float radius, boolean fire) {
   }
}
