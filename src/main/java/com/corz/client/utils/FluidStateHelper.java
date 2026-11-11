package com.corz.client.utils;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.UnreflectReference;
import com.corz.client.utils.FluidStateUtilsRect;
import com.corz.client.utils.FluidStateUtilsConst;
import com.corz.client.utils.FluidAmountUtils;
import com.corz.client.utils.AmountsWaterUtils;
import com.corz.client.utils.FluidStateUtils2;

final class FluidStateHelper {
   private int startXMod;
   private final float[] fluidMutableTable;
   private boolean unreflectReferenceActive;
   private final boolean[] fluidFlags;
   private final int[] fluidTable;
   private final int[] table;
   private int startX;
   private final int[] cornerLights;
   private boolean fluidActive;
   private int floorMod;
   private int waterColorValue;
   private byte[] cellFlags;
   private final float[] heightGrid;
   private int waterColor;
   private boolean bufferStartActive;
   private final float[] cornerHeights;
   private final FluidStateUtilsRect.Sink fluidSink;
   private boolean unreflectCacheLoaded;
   private final int[] cornerBaseLights;
   private int fluidValue;
   private float[] fluidHeights;
   private int bufferStartValue;
   private final BufferAllocator bufferAllocator;
   private final BlockState[] blockStates;
   private byte[] unreflectReferenceTable;
   private int startZ;
   private final UnreflectReference unreflectReference;
   private final boolean[] cellVisible;
   private final int[] cellHeightBits;
   private final boolean[] fluidMutableFlags;
   private boolean waterColorActive;
   private final UnreflectReference unreflectReferenceUnreflectR = new UnreflectReference();
   private float maxCornerHeight;
   private final UnreflectReference fluidUnreflectReference;
   static final VertexFormat lightNormal = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
   private int bufferValue;
   private final Mutable mutable;
   private int waterColorSouthEast;
   private BufferBuilder bufferBuilder;
   private final Mutable waterColorMutable;
   private final Mutable fluidMutable;
   private static final Predicate<BlockState> PREDICATE = state -> {
      FluidState fluidState = state.getFluidState();
      return !fluidState.isEmpty() && fluidState.getFluid().matchesType(Fluids.WATER);
   };
   private final boolean[] fluidPresentGrid;
   private int waterColorEast;
   private float minCornerHeight;
   private byte[] waterMask;
   private final FluidState[] fluidStates;
   private static final byte FLAG_HAS_FLUID = 1;
   private static final byte FLAG_SOLID = 2;

   private void handleFluidMutable(ClientWorld clientWorld, ChunkSection[] chunkSections, int fluidMutableValue, int k) {
      for (int i = 0; i < 18; i++) {
         for (int j = 0; j < 18; j++) {
            int index = i * 18 + j;
            int fluidMutableLimit = j - 1;
            int p = i - 1;
            if (fluidMutableLimit >= 0 && fluidMutableLimit < 16 && p >= 0 && p < 16) {
               int index2 = this.unreflectReferenceUnreflectR.unreflectReferenceTable[UnreflectReference.getValue(fluidMutableLimit, k, p)];
               boolean fluidMutableFlag = this.canFlag(index2);
               boolean unreflectReference = this.canUnreflectReference(chunkSections, fluidMutableValue, fluidMutableLimit, k, p);
               this.fluidPresentGrid[index] = fluidMutableFlag;
               this.fluidMutableFlags[index] = unreflectReference;
               this.heightGrid[index] = FluidStateUtilsConst.getAmount(fluidMutableFlag, unreflectReference, this.fluidHeights[index2], (this.cellFlags[index2] & 2) != 0);
               this.fluidMutableTable[index] = !fluidMutableFlag
                  ? -1.0F
                  : (unreflectReference ? 8.0F : FluidAmountUtils.getAmount(this.fluidHeights[index2], this.getUnreflectReferenceForChunkSectionArrayInt(chunkSections, fluidMutableValue, fluidMutableLimit, k, p)));
               int index3 = p * 16 + fluidMutableLimit;
               if (fluidMutableFlag && !unreflectReference) {
                  this.blockStates[index3] = this.getUnreflectReference(chunkSections, fluidMutableValue, fluidMutableLimit, k, p);
                  this.fluidStates[index3] = this.unreflectReferenceUnreflectR.copyOf[index2].getFluidState();
               } else {
                  this.blockStates[index3] = null;
                  this.fluidStates[index3] = null;
               }
            } else {
               this.mutable.set(this.startX + fluidMutableLimit, this.waterColorValue, this.startZ + p);
               BlockState blockState = clientWorld.getBlockState(this.mutable);
               FluidState fluidState = blockState.getFluidState();
               boolean fluidConstFlag = !fluidState.isEmpty() && fluidState.getFluid().matchesType(Fluids.WATER);
               boolean fluidMutableFlag3 = false;
               if (fluidConstFlag) {
                  this.waterColorMutable.set(this.mutable.getX(), this.waterColorValue + 1, this.mutable.getZ());
                  FluidState fluidState2 = clientWorld.getBlockState(this.waterColorMutable).getFluidState();
                  fluidMutableFlag3 = !fluidState2.isEmpty() && fluidState2.getFluid().matchesType(Fluids.WATER);
               }

               this.fluidPresentGrid[index] = fluidConstFlag;
               this.fluidMutableFlags[index] = fluidMutableFlag3;
               this.heightGrid[index] = FluidStateUtilsConst.getAmount(fluidConstFlag, fluidMutableFlag3, fluidConstFlag ? fluidState.getHeight() : 0.0F, blockState.isSolid());
               this.fluidMutableTable[index] = !fluidConstFlag
                  ? -1.0F
                  : (
                     fluidMutableFlag3
                        ? 8.0F
                        : FluidAmountUtils.getAmount(fluidState.getHeight(), this.getFluidState(clientWorld, this.startX + fluidMutableLimit, this.waterColorValue, this.startZ + p))
                  );
            }
         }
      }
   }

   private boolean canUnreflectReference(ChunkSection[] chunkSections, int unreflectReferenceValue, int fluidMutableLimit, int k, int p) {
      if (k < 15) {
         return this.canFlag(this.unreflectReferenceUnreflectR.unreflectReferenceTable[UnreflectReference.getValue(fluidMutableLimit, k + 1, p)]);
      } else {
         this.handleUnreflectReferenceForChunkSectionArrayInt(chunkSections, unreflectReferenceValue);
         return this.unreflectReferenceActive ? false : (this.unreflectReferenceTable[this.unreflectReference.unreflectReferenceTable[UnreflectReference.getValue(fluidMutableLimit, 0, p)]] & 1) != 0;
      }
   }

   private boolean canUnreflectReference(ChunkSection[] chunkSections, int unreflectReferenceValue, int k) {
      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            if (this.canFlag(this.unreflectReferenceUnreflectR.unreflectReferenceTable[UnreflectReference.getValue(j, k, i)])
               && !this.canUnreflectReference(chunkSections, unreflectReferenceValue, j, k, i)) {
               return true;
            }
         }
      }

      return false;
   }

   private void handleLightNormal(float lightNormalValue, float bufferBound, float lightNormalValue3, int lightNormalValue4, float lightNormalValue5, float lightNormalValue6, int lightMutable, float lightNormalValue8, float lightNormalValue9, int lightNormalValue10) {
      this.bufferBuilder.vertex(lightNormalValue, bufferBound, lightNormalValue3).color(lightNormalValue4).texture(lightNormalValue5, lightNormalValue6).light(lightMutable).normal(lightNormalValue8, FluidAmountUtils.getAmount(lightNormalValue10), lightNormalValue9);
   }

   private static boolean canCullingCovered(BlockState state, float cullingCoveredValue) {
      if (state == null) {
         return false;
      } else {
         VoxelShape cullingFace = state.getCullingFace(Direction.DOWN);
         if (cullingFace == VoxelShapes.empty()) {
            return false;
         } else {
            return cullingFace == VoxelShapes.fullCube()
               ? cullingCoveredValue == 1.0F
               : VoxelShapes.isSideCovered(VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, cullingCoveredValue, 1.0), cullingFace, Direction.UP);
         }
      }
   }

   private void handleStartBuffer(int bufferValue, int i, int bufferValue3, int bufferValue4, float bufferValue5, float bufferValue6, float bufferValue7, float intBitsToFloat, int lightMutable, float bufferValue10, float bufferValue11, int[] bufferValues) {
      if (this.bufferBuilder == null) {
         this.bufferBuilder = new BufferBuilder(this.bufferAllocator, DrawMode.QUADS, lightNormal);
      }

      float bufferValue12 = this.startX - this.bufferStartValue + bufferValue;
      float lightNormalValue = this.startX - this.bufferStartValue + bufferValue3;
      float bufferValue14 = this.startZ - this.bufferValue + i;
      float bufferValue15 = this.startZ - this.bufferValue + bufferValue4;
      float bufferValue16 = this.startXMod + bufferValue;
      float bufferValue17 = this.startXMod + bufferValue3;
      float bufferValue18 = this.floorMod + i;
      float bufferValue19 = this.floorMod + bufferValue4;
      float waterColorValue3 = this.waterColorValue;
      float bufferBound = waterColorValue3 + bufferValue5 - 0.001F;
      float bufferBound2 = waterColorValue3 + bufferValue6 - 0.001F;
      float bufferBound3 = waterColorValue3 + bufferValue7 - 0.001F;
      float bufferBound4 = waterColorValue3 + intBitsToFloat - 0.001F;
      this.handleLightNormal(bufferValue12, bufferBound, bufferValue14, this.getWaterColor(bufferValue, i), bufferValue16, bufferValue18, lightMutable, bufferValue10, bufferValue11, bufferValues[0]);
      this.handleLightNormal(bufferValue12, bufferBound2, bufferValue15, this.getWaterColor(bufferValue, bufferValue4), bufferValue16, bufferValue19, lightMutable, bufferValue10, bufferValue11, bufferValues[1]);
      this.handleLightNormal(lightNormalValue, bufferBound3, bufferValue15, this.getWaterColor(bufferValue3, bufferValue4), bufferValue17, bufferValue19, lightMutable, bufferValue10, bufferValue11, bufferValues[2]);
      this.handleLightNormal(lightNormalValue, bufferBound4, bufferValue14, this.getWaterColor(bufferValue3, i), bufferValue17, bufferValue18, lightMutable, bufferValue10, bufferValue11, bufferValues[3]);
      this.minCornerHeight = Math.min(this.minCornerHeight, Math.min(Math.min(bufferBound, bufferBound2), Math.min(bufferBound3, bufferBound4)));
      this.maxCornerHeight = Math.max(this.maxCornerHeight, Math.max(Math.max(bufferBound, bufferBound2), Math.max(bufferBound3, bufferBound4)));
   }

   FluidStateHelper.Result getBufferStart(ClientWorld clientWorld, WorldChunk worldChunk, int index, int value, int value2) {
      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      if (index >= 0 && index < sectionArray.length) {
         ChunkSection chunkSection = sectionArray[index];
         if (chunkSection == null || chunkSection.isEmpty()) {
            return null;
         } else if (!chunkSection.hasAny(PREDICATE)) {
            return null;
         } else {
            this.unreflectReferenceUnreflectR.handleFlag(chunkSection);
            if (!this.canAmountsFluid(this.unreflectReferenceUnreflectR, false)) {
               return null;
            } else {
               this.bufferStartActive = false;
               this.unreflectCacheLoaded = false;
               if (this.bufferBuilder != null) {
                  this.bufferBuilder = null;
                  this.bufferAllocator.reset();
               }

               this.minCornerHeight = Float.MAX_VALUE;
               this.maxCornerHeight = -Float.MAX_VALUE;
               this.startX = worldChunk.getPos().getStartX();
               this.startZ = worldChunk.getPos().getStartZ();
               this.bufferStartValue = value;
               this.bufferValue = value2;
               this.startXMod = AmountsWaterUtils.getFloorMod(this.startX);
               this.floorMod = AmountsWaterUtils.getFloorMod(this.startZ);
               int bufferStartValue = worldChunk.getBottomSectionCoord() + index << 4;

               for (int i = 0; i < 16; i++) {
                  if (this.canUnreflectReference(sectionArray, index, i)) {
                     this.waterColorValue = bufferStartValue + i;
                     this.waterColorActive = false;
                     this.handleFluidMutable(clientWorld, sectionArray, index, i);
                     this.handleAmountsFlags(clientWorld);
                  }
               }

               if (this.bufferBuilder == null) {
                  return null;
               } else {
                  BuiltBuffer endNullable = this.bufferBuilder.endNullable();
                  this.bufferBuilder = null;
                  if (endNullable == null) {
                     return null;
                  } else {
                     Object obj;
                     try {
                        ByteBuffer buffer = endNullable.getBuffer();
                        int remaining = buffer.remaining();
                        if (remaining > 0) {
                           FluidStateHelper.Result bufferStart = new FluidStateHelper.Result();
                           bufferStart.bufferStartTable = new byte[remaining];
                           buffer.get(buffer.position(), bufferStart.bufferStartTable, 0, remaining);
                           bufferStart.vertexCount = endNullable.getDrawParameters().vertexCount();
                           bufferStart.bufferStartAmount = this.minCornerHeight;
                           bufferStart.maxHeight = this.maxCornerHeight;
                           return bufferStart;
                        }

                        obj = null;
                     } finally {
                        endNullable.close();
                     }

                     return (FluidStateHelper.Result)obj;
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   private boolean canAmountsFluid(UnreflectReference unreflectReference, boolean fluidFlag) {
      int fluidLimit = unreflectReference.paletteSize;
      if (fluidFlag) {
         if (this.unreflectReferenceTable.length < fluidLimit) {
            this.unreflectReferenceTable = new byte[Math.max(fluidLimit, this.unreflectReferenceTable.length * 2)];
         }
      } else {
         if (this.cellFlags.length < fluidLimit) {
            this.cellFlags = new byte[Math.max(fluidLimit, this.cellFlags.length * 2)];
         }

         if (this.fluidHeights.length < fluidLimit) {
            this.fluidHeights = new float[Math.max(fluidLimit, this.fluidHeights.length * 2)];
         }
      }

      boolean amountsFluid = false;

      for (int i = 0; i < fluidLimit; i++) {
         BlockState blockState = unreflectReference.copyOf[i];
         FluidState fluidState = blockState.getFluidState();
         boolean fluidFlag2 = !fluidState.isEmpty() && fluidState.getFluid().matchesType(Fluids.WATER);
         byte fluidValue = (byte)((fluidFlag2 ? 1 : 0) | (blockState.isSolid() ? 2 : 0));
         if (fluidFlag) {
            this.unreflectReferenceTable[i] = fluidValue;
         } else {
            this.cellFlags[i] = fluidValue;
            this.fluidHeights[i] = fluidFlag2 ? fluidState.getHeight() : 0.0F;
         }

         amountsFluid |= fluidFlag2;
      }

      return amountsFluid;
   }

   private int getFluidState(ClientWorld clientWorld, int fluidValue, int fluidValue2, int fluidValue3) {
      int fluidState = 0;
      byte fluidLimit = 8;

      for (int i = 1; i <= fluidLimit; i++) {
         FluidState fluidState2 = clientWorld.getBlockState(this.fluidMutable.set(fluidValue, fluidValue2 - i, fluidValue3)).getFluidState();
         if (fluidState2.isEmpty() || !fluidState2.getFluid().matchesType(Fluids.WATER)) {
            break;
         }

         fluidState++;
      }

      return fluidState;
   }

   private void handleAmountsFlags(ClientWorld clientWorld) {
      boolean fluidFlag = false;

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            int index = i * 16 + j;
            int index2 = (i + 1) * 18 + j + 1;
            this.cellVisible[index] = false;
            if (this.fluidPresentGrid[index2] && !this.fluidMutableFlags[index2]) {
               FluidStateUtilsConst.handle(this.heightGrid, 18, index2, this.cornerHeights);
               if (!canCullingCovered(this.blockStates[index], FluidStateUtilsConst.getMin(this.cornerHeights))) {
                  this.mutable.set(this.startX + j, this.waterColorValue, this.startZ + i);
                  if (!this.waterColorActive) {
                     this.handleWaterColor(clientWorld);
                  }

                  int lightMutable = this.getLightMutable(clientWorld, this.mutable);
                  FluidAmountUtils.handle(this.fluidMutableTable, 18, index2, this.cornerLights);
                  boolean canFlag2 = FluidStateUtilsConst.canFlag(this.cornerHeights);
                  if (canFlag2 && FluidAmountUtils.canFlag(this.cornerLights)) {
                     this.cellVisible[index] = true;
                     this.cellHeightBits[index] = Float.floatToIntBits(this.cornerHeights[0]);
                     this.table[index] = this.cornerLights[0];
                     this.fluidTable[index] = lightMutable;
                     fluidFlag = true;
                  } else if (canFlag2) {
                     this.handleStartBuffer(
                        j, i, j + 1, i + 1, this.cornerHeights[0], this.cornerHeights[1], this.cornerHeights[2], this.cornerHeights[3], lightMutable, 0.0F, 0.0F, this.cornerLights
                     );
                  } else {
                     FluidState fluidState = this.fluidStates[index];
                     Vec3d vec3d = fluidState == null ? Vec3d.ZERO : fluidState.getVelocity(clientWorld, this.mutable);
                     this.handleStartBuffer(
                        j,
                        i,
                        j + 1,
                        i + 1,
                        this.cornerHeights[0],
                        this.cornerHeights[1],
                        this.cornerHeights[2],
                        this.cornerHeights[3],
                        lightMutable,
                        (float)vec3d.x,
                        (float)vec3d.z,
                        this.cornerLights
                     );
                  }
               }
            }
         }
      }

      if (fluidFlag) {
         FluidStateUtilsRect.getRect(this.cellVisible, this.cellHeightBits, this.table, this.fluidTable, this.fluidFlags, this.fluidSink);
      }
   }

   private void handleUnreflectReference(ChunkSection[] chunkSections, int unreflectReferenceValue) {
      if (!this.unreflectCacheLoaded) {
         this.unreflectCacheLoaded = true;
         int index = unreflectReferenceValue - 1;
         ChunkSection chunkSection = index >= 0 ? chunkSections[index] : null;
         if (chunkSection != null && !chunkSection.isEmpty()) {
            this.fluidUnreflectReference.handleFlag(chunkSection);
            int unreflectReferenceLimit = this.fluidUnreflectReference.paletteSize;
            if (this.waterMask.length < unreflectReferenceLimit) {
               this.waterMask = new byte[Math.max(unreflectReferenceLimit, this.waterMask.length * 2)];
            }

            for (int i = 0; i < unreflectReferenceLimit; i++) {
               FluidState fluidState = this.fluidUnreflectReference.copyOf[i].getFluidState();
               this.waterMask[i] = (byte)(!fluidState.isEmpty() && fluidState.getFluid().matchesType(Fluids.WATER) ? 1 : 0);
            }

            this.fluidActive = false;
         } else {
            this.fluidActive = true;
         }
      }
   }

   void updateBufferAllocator() {
      this.bufferBuilder = null;
      this.bufferAllocator.close();
   }

   private int getWaterColor(int waterColorValue, int i) {
      return FluidStateUtils2.getValue(this.waterColor, this.waterColorEast, this.fluidValue, this.waterColorSouthEast, waterColorValue / 16.0F, i / 16.0F);
   }

   private BlockState getUnreflectReference(ChunkSection[] chunkSections, int unreflectReferenceValue, int fluidMutableLimit, int k, int p) {
      if (k < 15) {
         return this.unreflectReferenceUnreflectR.copyOf[this.unreflectReferenceUnreflectR.unreflectReferenceTable[UnreflectReference.getValue(fluidMutableLimit, k + 1, p)]];
      } else {
         this.handleUnreflectReferenceForChunkSectionArrayInt(chunkSections, unreflectReferenceValue);
         return this.unreflectReferenceActive
            ? Blocks.AIR.getDefaultState()
            : this.unreflectReference.copyOf[this.unreflectReference.unreflectReferenceTable[UnreflectReference.getValue(fluidMutableLimit, 0, p)]];
      }
   }

   private int getLightMutable(ClientWorld clientWorld, BlockPos pos) {
      this.waterColorMutable.set(pos.getX(), pos.getY() + 1, pos.getZ());
      return LightmapTextureManager.pack(clientWorld.getLightLevel(LightType.BLOCK, this.waterColorMutable), clientWorld.getLightLevel(LightType.SKY, this.waterColorMutable));
   }

   private void handleUnreflectReferenceForChunkSectionArrayInt(ChunkSection[] chunkSections, int unreflectReferenceValue) {
      if (!this.bufferStartActive) {
         this.bufferStartActive = true;
         int sectionsOffset = unreflectReferenceValue + 1;
         ChunkSection chunkSection = sectionsOffset < chunkSections.length ? chunkSections[sectionsOffset] : null;
         if (chunkSection != null && !chunkSection.isEmpty()) {
            this.unreflectReference.handleFlag(chunkSection);
            this.canAmountsFluid(this.unreflectReference, true);
            this.unreflectReferenceActive = false;
         } else {
            this.unreflectReferenceActive = true;
         }
      }
   }

   private int getUnreflectReferenceForChunkSectionArrayInt(ChunkSection[] chunkSections, int unreflectReferenceValue, int fluidMutableLimit, int k, int p) {
      int unreflectReference = 0;
      byte unreflectReferenceLimit = 8;

      for (int i = 1; i <= unreflectReferenceLimit; i++) {
         int unreflectReferenceLimit2 = k - i;
         boolean unreflectReferenceFlag;
         if (unreflectReferenceLimit2 >= 0) {
            unreflectReferenceFlag = this.canFlag(this.unreflectReferenceUnreflectR.unreflectReferenceTable[UnreflectReference.getValue(fluidMutableLimit, unreflectReferenceLimit2, p)]);
         } else {
            this.handleUnreflectReference(chunkSections, unreflectReferenceValue);
            unreflectReferenceFlag = !this.fluidActive && (this.waterMask[this.fluidUnreflectReference.unreflectReferenceTable[UnreflectReference.getValue(fluidMutableLimit, unreflectReferenceLimit2 + 16, p)]] & 1) != 0;
         }

         if (!unreflectReferenceFlag) {
            break;
         }

         unreflectReference++;
      }

      return unreflectReference;
   }

   private void handleWaterColor(ClientWorld clientWorld) {
      this.waterColorActive = true;
      this.waterColor = BiomeColors.getWaterColor(clientWorld, this.waterColorMutable.set(this.startX, this.waterColorValue, this.startZ));
      this.waterColorEast = BiomeColors.getWaterColor(clientWorld, this.waterColorMutable.set(this.startX + 16, this.waterColorValue, this.startZ));
      this.fluidValue = BiomeColors.getWaterColor(clientWorld, this.waterColorMutable.set(this.startX, this.waterColorValue, this.startZ + 16));
      this.waterColorSouthEast = BiomeColors.getWaterColor(clientWorld, this.waterColorMutable.set(this.startX + 16, this.waterColorValue, this.startZ + 16));
   }

   private boolean canFlag(int index) {
      return (this.cellFlags[index] & 1) != 0;
   }

   FluidStateHelper() {
      this.unreflectReference = new UnreflectReference();
      this.fluidUnreflectReference = new UnreflectReference();
      this.mutable = new Mutable();
      this.waterColorMutable = new Mutable();
      this.fluidMutable = new Mutable();
      this.bufferAllocator = new BufferAllocator(65536);
      this.cellFlags = new byte[64];
      this.fluidHeights = new float[64];
      this.unreflectReferenceTable = new byte[64];
      this.waterMask = new byte[64];
      this.heightGrid = new float[324];
      this.fluidMutableTable = new float[324];
      this.cornerLights = new int[4];
      this.cornerBaseLights = new int[4];
      this.fluidPresentGrid = new boolean[324];
      this.fluidMutableFlags = new boolean[324];
      this.blockStates = new BlockState[256];
      this.fluidStates = new FluidState[256];
      this.cellVisible = new boolean[256];
      this.cellHeightBits = new int[256];
      this.table = new int[256];
      this.fluidTable = new int[256];
      this.fluidFlags = new boolean[256];
      this.cornerHeights = new float[4];
      this.fluidSink = (cell, fluidValue, fluidValue2, fluidValue3, index) -> {
         float intBitsToFloat = Float.intBitsToFloat(this.cellHeightBits[index]);
         Arrays.fill(this.cornerBaseLights, this.table[index]);
         this.handleStartBuffer(cell, fluidValue, cell + fluidValue2, fluidValue + fluidValue3, intBitsToFloat, intBitsToFloat, intBitsToFloat, intBitsToFloat, this.fluidTable[index], 0.0F, 0.0F, this.cornerBaseLights);
      };
   }

   static final class Result {
      float maxHeight;
      byte[] bufferStartTable;
      int vertexCount;
      float bufferStartAmount;
   }
}
