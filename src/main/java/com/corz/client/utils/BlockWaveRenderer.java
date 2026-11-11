package com.corz.client.utils;

import com.corz.client.render.VertexBuilderUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.corz.client.utils.BlockWaveMath;

public final class BlockWaveRenderer {
   private final Random random;
   private long frameBudgetDeadlineNanos;
   private int[] statesBlockTable;
   private final BlockWaveRenderer.MeshRecorder meshRecorder;
   private double collisionMaxY;
   private final Vector3f vectorTransformVector3f;
   private int[] statesFloorTable;
   private static Immediate immediate;
   private int currentStateId;
   private BlockState blockState;
   private final BlockWaveRenderer.SurfaceView surfaceView;
   private ClientWorld world;
   private BlockState blockState2Block;
   private BlockState[] blockStates;
   private boolean empty;
   private BlockState[] statesBlockBlockStates;
   private BlockState blockStateMutable;
   private int[] blockWaveTable;
   private float[] table;
   private final MatrixStack matrixStack;
   private final Long2ObjectOpenHashMap<BlockWaveRenderer.Slot> long2ObjectOpenHashMap;
   private int blockWaveCount;
   private final Mutable mutable;
   private final ArrayList<BlockWaveRenderer.Slot> slotsHashSlotList = new ArrayList<>();
   private final ArrayDeque<BlockWaveRenderer.Wave> arrayDeque;
   private double mutableAmount;
   private int[] blockStateIds;
   private final Mutable mutableMutable;

   private boolean canMatrixStack(BlockRenderManager blockRenderManager, BlockState blockStateBlock, BlockPos pos2, float y) {
      this.random.setSeed(blockStateBlock.getRenderingSeed(pos2));
      List parts = blockRenderManager.getModel(blockStateBlock).getParts(this.random);
      if (parts.isEmpty()) {
         return false;
      } else {
         this.matrixStack.push();

         boolean matrixStack;
         try {
            if (y != 0.0F) {
               this.matrixStack.translate(0.0F, y, 0.0F);
            }

            blockRenderManager.getModelRenderer().render(this.surfaceView, parts, blockStateBlock, pos2, this.matrixStack, this.meshRecorder, false, OverlayTexture.DEFAULT_UV);
            return true;
         } catch (RuntimeException e) {
            matrixStack = false;
         } finally {
            this.matrixStack.pop();
         }

         return matrixStack;
      }
   }

   public void handleDequeFlag(ClientWorld world, long dequeValue, double blockWaveValue, double blockWaveBound) {
      if (!this.arrayDeque.isEmpty() || !this.slotsHashSlotList.isEmpty()) {
         if (world != null && world == this.world) {
            this.handleDequeArray(dequeValue, blockWaveValue, blockWaveBound);
            int dequeCounter = 0;

            for (int i = this.slotsHashSlotList.size(); dequeCounter < i; dequeCounter++) {
               BlockWaveRenderer.Slot slot = this.slotsHashSlotList.get(dequeCounter);
               if (!slot.dequeActive && !this.canMutableState(world, slot)) {
                  slot.dequeActive = true;
               }
            }
         } else {
            this.updateClearDeque();
         }
      }
   }

   private static boolean canMinMax(VoxelShape voxelShape) {
      return voxelShape.getMin(Axis.X) <= 0.0625 && voxelShape.getMax(Axis.X) >= 0.9375 && voxelShape.getMin(Axis.Z) <= 0.0625 && voxelShape.getMax(Axis.Z) >= 0.9375;
   }

   private void handleAmountFlag(Matrix4f matrix, Vec3d vec, int blockWaveValue, boolean blockWaveFlag, float blockWaveValue2) {
      VertexConsumer vertexConsumer = null;
      int blockWaveCounter = 0;

      for (int i = this.slotsHashSlotList.size(); blockWaveCounter < i; blockWaveCounter++) {
         BlockWaveRenderer.Slot slot = this.slotsHashSlotList.get(blockWaveCounter);
         if (!slot.dequeActive && slot.recorderMeshActive && BlockWaveMath.canFlagForDouble(slot.layerAmount) && !(slot.blockWaveFactor <= 0.0F)) {
            if (vertexConsumer == null) {
               vertexConsumer = VertexBuilderUtils.getBuffer5();
            }

            int hsvRgb = getHsvRgb(slot, blockWaveValue, blockWaveFlag, blockWaveValue2);
            int valueForIntFloat = BlockWaveMath.getValue(hsvRgb, 0.2F * slot.blockWaveFactor);
            int blockWaveValue4 = BlockWaveMath.getValue(hsvRgb, 0.05F * slot.blockWaveFactor);
            float blockWaveValue5 = (float)(slot.mutableValue - vec.y) + slot.surfaceOffset + slot.layerAmount + 0.004F;
            float blockWaveValue6 = (float)(slot.blockX - vec.x) + 0.002F;
            float blockWaveValue7 = (float)(slot.recorderMeshValue - vec.z) + 0.002F;
            float blockWaveValue8 = blockWaveValue6 + 1.0F - 0.004F;
            float blockWaveValue9 = blockWaveValue7 + 1.0F - 0.004F;
            float vertexColorValue = (blockWaveValue6 + blockWaveValue8) * 0.5F;
            float vc2 = (blockWaveValue7 + blockWaveValue9) * 0.5F;
            VertexBuilderUtils.handleVertexColor(vertexConsumer, matrix, vertexColorValue, blockWaveValue5, vc2, valueForIntFloat, blockWaveValue6, blockWaveValue5, blockWaveValue7, blockWaveValue4, blockWaveValue8, blockWaveValue5, blockWaveValue7, blockWaveValue4);
            VertexBuilderUtils.handleVertexColor(vertexConsumer, matrix, vertexColorValue, blockWaveValue5, vc2, valueForIntFloat, blockWaveValue8, blockWaveValue5, blockWaveValue7, blockWaveValue4, blockWaveValue8, blockWaveValue5, blockWaveValue9, blockWaveValue4);
            VertexBuilderUtils.handleVertexColor(vertexConsumer, matrix, vertexColorValue, blockWaveValue5, vc2, valueForIntFloat, blockWaveValue8, blockWaveValue5, blockWaveValue9, blockWaveValue4, blockWaveValue6, blockWaveValue5, blockWaveValue9, blockWaveValue4);
            VertexBuilderUtils.handleVertexColor(vertexConsumer, matrix, vertexColorValue, blockWaveValue5, vc2, valueForIntFloat, blockWaveValue6, blockWaveValue5, blockWaveValue9, blockWaveValue4, blockWaveValue6, blockWaveValue5, blockWaveValue7, blockWaveValue4);
         }
      }
   }

   private boolean canStateBlock(ClientWorld world, int blockValue, int blockValue2, int blockValue3, double blockValue4) {
      int blockValue5 = blockValue3 + 1;
      int blockValue6 = blockValue3 - 1 - 4;
      double naN = Double.NaN;
      int value = 0;
      BlockState blockState = null;
      BlockState blockStateMutable2 = null;
      BlockState blockState3 = world.getBlockState(this.mutableMutable.set(blockValue, blockValue5 + 2, blockValue2));
      BlockState blockState4 = world.getBlockState(this.mutableMutable.set(blockValue, blockValue5 + 1, blockValue2));

      for (int i = blockValue5; i >= blockValue6; i--) {
         BlockState blockState5 = world.getBlockState(this.mutableMutable.set(blockValue, i, blockValue2));
         if (this.canMutableMax(world, blockValue, i, blockValue2, blockState5, blockState4, blockState3)) {
            double blockWaveValue = i + this.mutableAmount;
            if (BlockWaveMath.canFlag2(blockWaveValue, naN, blockValue4)) {
               naN = blockWaveValue;
               value = i;
               blockState = blockState5;
               blockStateMutable2 = this.blockStateMutable;
            }
         }

         blockState3 = blockState4;
         blockState4 = blockState5;
      }

      if (Double.isNaN(naN)) {
         return false;
      } else {
         this.currentStateId = value;
         this.blockState = blockState;
         this.blockState2Block = blockStateMutable2;
         return true;
      }
   }

   private static Immediate getImmediateCutout() {
      if (immediate == null) {
         LinkedHashMap linkedHashMap = new LinkedHashMap();
         linkedHashMap.put(RenderLayers.solid(), new BufferAllocator(262144));
         linkedHashMap.put(RenderLayers.cutout(), new BufferAllocator(262144));
         immediate = VertexConsumerProvider.immediate(linkedHashMap, new BufferAllocator(16384));
      }

      return immediate;
   }

   private boolean canMutableState(ClientWorld world, BlockWaveRenderer.Slot slot) {
      if (world.getBlockState(this.mutableMutable.set(slot.blockX, slot.mutableValue, slot.recorderMeshValue)) != slot.blockStateBlock) {
         return false;
      } else {
         BlockState blockState = world.getBlockState(this.mutableMutable.set(slot.blockX, slot.mutableValue + 1, slot.recorderMeshValue));
         if (slot.blockState != null) {
            return blockState != slot.blockState ? false : canEmptyCollision(world, world.getBlockState(this.mutableMutable.set(slot.blockX, slot.mutableValue + 2, slot.recorderMeshValue)), this.mutableMutable);
         } else {
            return canEmptyCollision(world, blockState, this.mutableMutable);
         }
      }
   }

   public ClientWorld getWorld() {
      return this.world;
   }

   public void handleAmountFlag(
      MatrixStack matrices2, Vec3d vec, long blockWaveValue, double blockWaveBound, double blockWaveValue2, double blockWaveValue3, boolean blockWaveFlag, int layerValue, boolean hsvRgbFlag, float blockWaveValue5
   ) {
      this.handleDequeArray(blockWaveValue, blockWaveValue2, blockWaveValue3);
      if (!this.arrayDeque.isEmpty() && this.world != null) {
         int blockWaveCounter = 0;

         for (int i = this.slotsHashSlotList.size(); blockWaveCounter < i; blockWaveCounter++) {
            BlockWaveRenderer.Slot slot = this.slotsHashSlotList.get(blockWaveCounter);
            slot.layerAmount = 0.0F;
            slot.blockWaveFactor = 0.0F;
         }

         this.blockWaveCount = 0;
         this.frameBudgetDeadlineNanos = System.nanoTime() + 2000000L;
         double maxRainbow = Math.max(0.0, blockWaveBound);
         int blockWaveCounter2 = 0;

         for (BlockWaveRenderer.Wave wave : this.arrayDeque) {
            double seconds = (blockWaveValue - wave.startNanos) / 1.0E9;
            if (!(BlockWaveMath.getAmount(seconds, blockWaveValue3) <= 0.0)) {
               double blockWaveLimit = wave.baseStrength * (wave.dequeArrayActive ? BlockWaveMath.getAmount((blockWaveValue - wave.dequeArrayValue) / 1.0E9) : 1.0);
               if (blockWaveLimit > 0.0) {
                  for (int j = 0; j < wave.maxRadiusBlocks; j++) {
                     float blockWaveValue6 = wave.waveTable[j];
                     if (seconds < BlockWaveMath.getAmount3(blockWaveValue6, blockWaveValue2) - 0.05) {
                        break;
                     }

                     BlockWaveRenderer.Slot slot2 = wave.slots[j];
                     if (!slot2.dequeActive) {
                        if (!slot2.recorderMeshActive) {
                           if (wave.dequeArrayActive || !BlockWaveMath.canFlag(blockWaveValue6, seconds, blockWaveValue2) || !this.isTime()) {
                              continue;
                           }

                           if (!this.canRecorderMesh(this.world, slot2)) {
                              slot2.dequeActive = true;
                              continue;
                           }
                        }

                        double blockWaveFactor = BlockWaveMath.getAmount(blockWaveValue6, seconds, blockWaveValue2, blockWaveValue3, wave.waveRadius) * blockWaveLimit;
                        if (!(blockWaveFactor <= 0.0)) {
                           float amount = (float)(maxRainbow * blockWaveFactor);
                           if (amount > slot2.layerAmount) {
                              if (slot2.layerAmount < 0.03 && BlockWaveMath.canFlagForDouble(amount)) {
                                 blockWaveCounter2++;
                              }

                              slot2.layerAmount = amount;
                              slot2.blockWaveFactor = (float)blockWaveFactor;
                              slot2.hsvRgbAmount = (float)(blockWaveValue6 / Math.max(0.001, wave.waveRadius));
                           }
                        }
                     }
                  }
               }
            }
         }

         if (blockWaveCounter2 != 0) {
            Matrix4f positionMatrix = matrices2.peek().getPositionMatrix();
            Immediate immediateCutout = getImmediateCutout();
            this.handleLayerAmount(immediateCutout, RenderLayers.solid(), positionMatrix, vec, blockWaveFlag, layerValue, hsvRgbFlag, blockWaveValue5);
            this.handleLayerAmount(immediateCutout, RenderLayers.cutout(), positionMatrix, vec, blockWaveFlag, layerValue, hsvRgbFlag, blockWaveValue5);
            immediateCutout.draw();
            if (blockWaveFlag) {
               this.handleAmountFlag(positionMatrix, vec, layerValue, hsvRgbFlag, blockWaveValue5);
            }
         }
      }
   }

   private boolean canRecorderMesh(ClientWorld world, BlockWaveRenderer.Slot slot) {
      BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
      if (blockRenderManager == null) {
         return false;
      } else {
         BlockState blockStateBlock = slot.blockStateBlock;
         BlockState blockState = slot.blockState;
         if (world.getBlockState(this.mutableMutable.set(slot.blockX, slot.mutableValue, slot.recorderMeshValue)) != blockStateBlock) {
            return false;
         } else if (blockState != null && world.getBlockState(this.mutable.set(slot.blockX, slot.mutableValue + 1, slot.recorderMeshValue)) != blockState) {
            return false;
         } else {
            this.surfaceView.handleMutableState(world, slot.blockX, slot.mutableValue, slot.recorderMeshValue, blockStateBlock, blockState);

            try {
               this.meshRecorder.update();
               if (!this.canMatrixStack(blockRenderManager, blockStateBlock, this.surfaceView.mutable, 0.0F) || this.meshRecorder.quadCount < 3) {
                  return false;
               }

               slot.recorderMeshTable = this.meshRecorder.getValues(0.002F, slot.recorderMeshTable);
               slot.layerValue = this.meshRecorder.quadCount;
               slot.slotValue = 0;
               if (blockState != null) {
                  this.meshRecorder.update();
                  if (!this.canMatrixStack(blockRenderManager, blockState, this.surfaceView.recorderMeshMutable, 1.0F) || this.meshRecorder.quadCount < 3) {
                     return false;
                  }

                  slot.layerTable = this.meshRecorder.getValues(0.002F, slot.layerTable);
                  slot.slotValue = this.meshRecorder.quadCount;
                  slot.blockLayer = BlockRenderLayers.getMovingBlockLayer(blockState);
               }
            } finally {
               this.surfaceView.updateStateDefault();
            }

            slot.movingBlockLayer = BlockRenderLayers.getMovingBlockLayer(blockStateBlock);
            VoxelShape outlineShape = blockStateBlock.getOutlineShape(world, this.mutableMutable.set(slot.blockX, slot.mutableValue, slot.recorderMeshValue));
            double recorderMeshValue = outlineShape.isEmpty() ? 1.0 : outlineShape.getMax(Axis.Y);
            double blockWaveLimit = 0.0;
            if (blockState != null) {
               VoxelShape outlineShape2 = blockState.getOutlineShape(world, this.mutable.set(slot.blockX, slot.mutableValue + 1, slot.recorderMeshValue));
               blockWaveLimit = outlineShape2.isEmpty() ? 0.0 : outlineShape2.getMax(Axis.Y);
            }

            slot.surfaceOffset = BlockWaveMath.getAmount(recorderMeshValue, blockState != null, blockWaveLimit);
            slot.recorderMeshActive = true;
            return true;
         }
      }
   }

   private boolean isTime() {
      if (this.blockWaveCount >= 48) {
         return false;
      } else if (this.blockWaveCount >= 6 && System.nanoTime() > this.frameBudgetDeadlineNanos) {
         return false;
      } else {
         this.blockWaveCount++;
         return true;
      }
   }

   private void handleSlotsHash(BlockWaveRenderer.Wave wave) {
      for (int i = 0; i < wave.maxRadiusBlocks; i++) {
         BlockWaveRenderer.Slot slot = wave.slots[i];
         if (--slot.blockCount <= 0) {
            int value = slot.itemsSize;
            int slotsHashLimit = this.slotsHashSlotList.size() - 1;
            if (value >= 0 && value <= slotsHashLimit && this.slotsHashSlotList.get(value) == slot) {
               BlockWaveRenderer.Slot slot2 = this.slotsHashSlotList.get(slotsHashLimit);
               this.slotsHashSlotList.set(value, slot2);
               slot2.itemsSize = value;
               this.slotsHashSlotList.remove(slotsHashLimit);
            }

            slot.itemsSize = -1;
            this.long2ObjectOpenHashMap.remove(slot.longValue);
         }
      }
   }

   private void handleStatesBlock(int statesBlockSize) {
      if (this.blockWaveTable.length < statesBlockSize) {
         this.blockWaveTable = new int[statesBlockSize];
         this.blockStateIds = new int[statesBlockSize];
         this.statesFloorTable = new int[statesBlockSize];
         this.table = new float[statesBlockSize];
         this.blockStates = new BlockState[statesBlockSize];
         this.statesBlockBlockStates = new BlockState[statesBlockSize];
         this.statesBlockTable = new int[statesBlockSize];
      }
   }

   private static int getHsvRgb(BlockWaveRenderer.Slot slot, int hsvRgbValue, boolean hsvRgbFlag, float hsvRgbValue2) {
      if (!hsvRgbFlag) {
         return hsvRgbValue & 16777215;
      } else {
         float hashRegistryValue = hsvRgbValue2 + slot.hsvRgbAmount * 0.6F;
         float hsvRgbValue4 = hashRegistryValue - (float)Math.floor(hashRegistryValue);
         return MathHelper.hsvToRgb(hsvRgbValue4, 0.75F, 1.0F) & 16777215;
      }
   }

   private void handleLayerAmount(Immediate immediate, RenderLayer renderLayer, Matrix4f matrix, Vec3d vec, boolean layerFlag, int layerValue, boolean hsvRgbFlag, float layerValue2) {
      VertexConsumer vertexConsumer = null;
      int layerCounter = 0;

      for (int i = this.slotsHashSlotList.size(); layerCounter < i; layerCounter++) {
         BlockWaveRenderer.Slot slot = this.slotsHashSlotList.get(layerCounter);
         if (!slot.dequeActive && slot.recorderMeshActive && BlockWaveMath.canFlagForDouble(slot.layerAmount)) {
            boolean layerFlag3 = slot.movingBlockLayer == renderLayer;
            boolean layerFlag4 = slot.slotValue > 0 && slot.blockLayer == renderLayer;
            if (layerFlag3 || layerFlag4) {
               if (vertexConsumer == null) {
                  vertexConsumer = immediate.getBuffer(renderLayer);
               }

               float vectorTransformValue = (float)(slot.blockX - vec.x);
               float layerValue4 = (float)(slot.mutableValue - vec.y) + slot.layerAmount;
               float layerValue5 = (float)(slot.recorderMeshValue - vec.z);
               float layerLimit = layerFlag ? 0.22F * slot.blockWaveFactor : 0.0F;
               int layerValue6 = layerLimit > 0.0F ? getHsvRgb(slot, layerValue, hsvRgbFlag, layerValue2) : 0;
               if (layerFlag3) {
                  this.handleVectorTransform(vertexConsumer, matrix, slot.recorderMeshTable, slot.layerValue, vectorTransformValue, layerValue4, layerValue5, layerLimit, layerValue6);
               }

               if (layerFlag4) {
                  this.handleVectorTransform(vertexConsumer, matrix, slot.layerTable, slot.slotValue, vectorTransformValue, layerValue4, layerValue5, layerLimit, layerValue6);
               }
            }
         }
      }
   }

   public boolean canStatesFloor(ClientWorld world, double statesFloorValue, double statesFloorValue2, double statesFloorValue3, double coverageValue, int dequeArrayBound, double statesFloorLimit, long statesFloorValue6) {
      if (world != null && statesFloorLimit > 0.0) {
         if (world != this.world) {
            this.updateClearDeque();
            this.world = world;
         }

         int floor = MathHelper.floor(statesFloorValue2 + 0.001);
         int floorMaxWaves = MathHelper.floor(statesFloorValue);
         int floorGain = MathHelper.floor(statesFloorValue3);
         int ceilRadius = (int)Math.ceil(coverageValue);
         int statesBlockSize = (2 * ceilRadius + 1) * (2 * ceilRadius + 1);
         this.handleStatesBlock(statesBlockSize);
         int index = 0;

         for (int i = -ceilRadius; i <= ceilRadius; i++) {
            for (int j = -ceilRadius; j <= ceilRadius; j++) {
               int blockValue = floorMaxWaves + i;
               int statesFloorValue10 = floorGain + j;
               double statesFloorFactor = blockValue + 0.5 - statesFloorValue;
               double statesFloorFactor2 = statesFloorValue10 + 0.5 - statesFloorValue3;
               if (BlockWaveMath.canFlag3(statesFloorFactor, statesFloorFactor2, coverageValue) && this.canStateBlock(world, blockValue, statesFloorValue10, floor, statesFloorValue2)) {
                  this.blockWaveTable[index] = blockValue;
                  this.blockStateIds[index] = this.currentStateId;
                  this.statesFloorTable[index] = statesFloorValue10;
                  this.table[index] = (float)Math.sqrt(statesFloorFactor * statesFloorFactor + statesFloorFactor2 * statesFloorFactor2);
                  this.blockStates[index] = this.blockState;
                  this.statesBlockBlockStates[index] = this.blockState2Block;
                  index++;
               }
            }
         }

         if (index == 0) {
            return false;
         } else {
            int statesFloorSize = BlockWaveMath.getValue(this.table, index, 512, this.statesBlockTable);
            BlockWaveRenderer.Slot[] slots = new BlockWaveRenderer.Slot[statesFloorSize];
            float[] floats = new float[statesFloorSize];
            int index2 = 0;

            for (int k = 0; k < statesFloorSize; k++) {
               int index3 = this.statesBlockTable[k];
               BlockWaveRenderer.Slot stateBlock = this.getStateBlock(
                  this.blockWaveTable[index3], this.blockStateIds[index3], this.statesFloorTable[index3], this.blockStates[index3], this.statesBlockBlockStates[index3]
               );
               if (stateBlock != null) {
                  slots[index2] = stateBlock;
                  floats[index2] = this.table[index3];
                  index2++;
               }
            }

            if (index2 == 0) {
               return false;
            } else {
               this.arrayDeque.addLast(new BlockWaveRenderer.Wave(statesFloorValue6, coverageValue, slots, floats, index2, (float)Math.min(1.0, statesFloorLimit)));
               this.handleDequeArray(dequeArrayBound, statesFloorValue6);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean canEmptyMax(ClientWorld world, BlockState state2, BlockPos pos2) {
      if (!canCutoutMoving(state2)) {
         return false;
      } else {
         VoxelShape outlineShape = state2.getOutlineShape(world, pos2);
         VoxelShape collisionShape = state2.getCollisionShape(world, pos2);
         boolean varEmpty = outlineShape.isEmpty();
         this.empty = collisionShape.isEmpty();
         this.collisionMaxY = this.empty ? 0.0 : collisionShape.getMax(Axis.Y);
         return BlockWaveMath.canFlag(
            false,
            true,
            false,
            false,
            false,
            varEmpty,
            varEmpty ? 0.0 : outlineShape.getMin(Axis.Y),
            varEmpty ? 0.0 : outlineShape.getMax(Axis.Y),
            !varEmpty && canMinMax(outlineShape),
            this.empty,
            this.collisionMaxY
         );
      }
   }

   private void handleDequeArray(int dequeArrayBound, long time) {
      int maxNow = Math.max(1, dequeArrayBound);
      int dequeArrayCounter = 0;

      for (BlockWaveRenderer.Wave wave : this.arrayDeque) {
         if (!wave.dequeArrayActive) {
            dequeArrayCounter++;
         }
      }

      Iterator iterator = this.arrayDeque.iterator();

      while (iterator.hasNext() && dequeArrayCounter > maxNow) {
         BlockWaveRenderer.Wave blockWaveRendererWave = (BlockWaveRenderer.Wave)iterator.next();
         if (!blockWaveRendererWave.dequeArrayActive) {
            blockWaveRendererWave.dequeArrayActive = true;
            blockWaveRendererWave.dequeArrayValue = time;
            dequeArrayCounter--;
         }
      }

      while (this.arrayDeque.size() > maxNow + 3) {
         this.handleSlotsHash(this.arrayDeque.removeFirst());
      }
   }

   private static boolean canEmptyCollision(ClientWorld world, BlockState state2, BlockPos pos2) {
      return state2.isAir() || state2.getFluidState().isEmpty() && state2.getCollisionShape(world, pos2).isEmpty();
   }

   private boolean canMutableMax(ClientWorld world, int mutableValue, int i, int mutableValue3, BlockState state2, BlockState blockState, BlockState state3) {
      if (!canCutoutMoving(state2)) {
         return false;
      } else {
         this.mutable.set(mutableValue, i, mutableValue3);
         VoxelShape collisionShape = state2.getCollisionShape(world, this.mutable);
         boolean varEmpty = collisionShape.isEmpty();
         double max = varEmpty ? 0.0 : collisionShape.getMax(Axis.Y);
         boolean mutableFlag = !varEmpty && canMinMax(collisionShape);
         if (!BlockWaveMath.canFlag(false, true, false, false, false, varEmpty, max, mutableFlag, true, false)) {
            return false;
         } else {
            this.mutableAmount = max;
            this.blockStateMutable = null;
            if (blockState.isAir()) {
               return true;
            } else {
               this.mutable.set(mutableValue, i + 1, mutableValue3);
               if (this.canEmptyMax(world, blockState, this.mutable)) {
                  boolean empty = this.empty;
                  double blockWaveValue = this.collisionMaxY;
                  this.mutable.set(mutableValue, i + 2, mutableValue3);
                  if (!canEmptyCollision(world, state3, this.mutable)) {
                     return false;
                  } else {
                     this.blockStateMutable = blockState;
                     this.mutableAmount = BlockWaveMath.getAmount(max, true, empty, blockWaveValue);
                     return true;
                  }
               } else {
                  return canEmptyCollision(world, blockState, this.mutable);
               }
            }
         }
      }
   }

   private void handleVectorTransform(VertexConsumer vertexConsumer, Matrix4f matrix, int[] vectorTransformValues, int vectorTransformLimit, float vectorTransformValue, float vectorTransformValue2, float vectorTransformValue3, float blockWaveLimit, int vectorTransformValue4) {
      int vectorTransformCounter = 0;

      for (int i = 0; vectorTransformCounter < vectorTransformLimit; i += 8) {
         matrix.transformPosition(
            Float.intBitsToFloat(vectorTransformValues[i]) + vectorTransformValue, Float.intBitsToFloat(vectorTransformValues[i + 1]) + vectorTransformValue2, Float.intBitsToFloat(vectorTransformValues[i + 2]) + vectorTransformValue3, this.vectorTransformVector3f
         );
         int vectorTransformValue5 = blockWaveLimit > 0.0F ? BlockWaveMath.getValue(vectorTransformValues[i + 3], vectorTransformValue4, blockWaveLimit) : vectorTransformValues[i + 3];
         int vectorTransformValue6 = vectorTransformValues[i + 7];
         vertexConsumer.vertex(
            this.vectorTransformVector3f.x,
            this.vectorTransformVector3f.y,
            this.vectorTransformVector3f.z,
            vectorTransformValue5,
            Float.intBitsToFloat(vectorTransformValues[i + 4]),
            Float.intBitsToFloat(vectorTransformValues[i + 5]),
            OverlayTexture.DEFAULT_UV,
            vectorTransformValues[i + 6],
            (byte)vectorTransformValue6 / 127.0F,
            (byte)(vectorTransformValue6 >> 8) / 127.0F,
            (byte)(vectorTransformValue6 >> 16) / 127.0F
         );
         vectorTransformCounter++;
      }
   }

   private static boolean canCutoutMoving(BlockState state2) {
      if (!state2.isAir() && state2.getRenderType() == BlockRenderType.MODEL && !state2.hasBlockEntity() && state2.getFluidState().isEmpty()) {
         RenderLayer movingBlockLayer = BlockRenderLayers.getMovingBlockLayer(state2);
         return movingBlockLayer == RenderLayers.solid() || movingBlockLayer == RenderLayers.cutout();
      } else {
         return false;
      }
   }

   public boolean isEmpty() {
      return this.arrayDeque.isEmpty();
   }

   public BlockWaveRenderer() {
      this.long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
      this.arrayDeque = new ArrayDeque<>();
      this.meshRecorder = new BlockWaveRenderer.MeshRecorder();
      this.surfaceView = new BlockWaveRenderer.SurfaceView();
      this.mutableMutable = new Mutable();
      this.mutable = new Mutable();
      this.matrixStack = new MatrixStack();
      this.random = Random.create();
      this.vectorTransformVector3f = new Vector3f();
      this.blockWaveTable = new int[0];
      this.blockStateIds = new int[0];
      this.statesFloorTable = new int[0];
      this.table = new float[0];
      this.blockStates = new BlockState[0];
      this.statesBlockBlockStates = new BlockState[0];
      this.statesBlockTable = new int[0];
   }

   private void handleDequeArray(long dequeArrayValue, double blockWaveValue, double blockWaveBound) {
      if (!this.arrayDeque.isEmpty()) {
         Iterator iterator = this.arrayDeque.iterator();

         while (iterator.hasNext()) {
            BlockWaveRenderer.Wave wave = (BlockWaveRenderer.Wave)iterator.next();
            double seconds = (dequeArrayValue - wave.startNanos) / 1.0E9;
            boolean dequeArrayFlag = wave.dequeArrayActive && BlockWaveMath.canFlag((dequeArrayValue - wave.dequeArrayValue) / 1.0E9);
            if (dequeArrayFlag || seconds > BlockWaveMath.getAmount2(wave.waveRadius, blockWaveValue, blockWaveBound)) {
               iterator.remove();
               this.handleSlotsHash(wave);
            }
         }
      }
   }

   public void updateClearDeque() {
      this.arrayDeque.clear();
      this.slotsHashSlotList.clear();
      this.long2ObjectOpenHashMap.clear();
      this.world = null;
   }

   private BlockWaveRenderer.Slot getStateBlock(int blockValue, int blockValue2, int blockValue3, BlockState blockState, BlockState blockState2) {
      long long2 = BlockPos.asLong(blockValue, blockValue2, blockValue3);
      BlockWaveRenderer.Slot long2objectopenhashmap = (BlockWaveRenderer.Slot)this.long2ObjectOpenHashMap.get(long2);
      if (long2objectopenhashmap == null) {
         if (this.slotsHashSlotList.size() >= 2048) {
            return null;
         } else {
            BlockWaveRenderer.Slot slot = new BlockWaveRenderer.Slot(long2, blockValue, blockValue2, blockValue3);
            slot.blockStateBlock = blockState;
            slot.blockState = blockState2;
            slot.blockCount = 1;
            slot.itemsSize = this.slotsHashSlotList.size();
            this.slotsHashSlotList.add(slot);
            this.long2ObjectOpenHashMap.put(long2, slot);
            return slot;
         }
      } else {
         if (long2objectopenhashmap.dequeActive || long2objectopenhashmap.blockStateBlock != blockState || long2objectopenhashmap.blockState != blockState2) {
            long2objectopenhashmap.blockStateBlock = blockState;
            long2objectopenhashmap.blockState = blockState2;
            long2objectopenhashmap.recorderMeshActive = false;
            long2objectopenhashmap.dequeActive = false;
         }

         long2objectopenhashmap.blockCount++;
         return long2objectopenhashmap;
      }
   }

   static final class MeshRecorder implements VertexConsumer {
      private int[] vertexTable = new int[768];
      int quadCount;
      private int vertexValue = -8;

      @Override
      public VertexConsumer color(int colorValue, int colorValue2, int colorValue3, int colorValue4) {
         if (this.isFlag()) {
            this.vertexTable[this.vertexValue + 3] = (colorValue4 & 0xFF) << 24 | (colorValue & 0xFF) << 16 | (colorValue2 & 0xFF) << 8 | colorValue3 & 0xFF;
         }

         return this;
      }

      private boolean isFlag() {
         return this.vertexValue >= 0;
      }

      @Override
      public VertexConsumer vertex(float vertexValue, float vertexValue2, float quadUnfinishedValue) {
         this.handle(vertexValue, vertexValue2, quadUnfinishedValue);
         return this;
      }

      @Override
      public VertexConsumer lineWidth(float lineWidthValue) {
         return this;
      }

      @Override
      public VertexConsumer texture(float textureValue, float quadUnfinishedValue) {
         if (this.isFlag()) {
            this.vertexTable[this.vertexValue + 4] = Float.floatToRawIntBits(textureValue);
            this.vertexTable[this.vertexValue + 5] = Float.floatToRawIntBits(quadUnfinishedValue);
         }

         return this;
      }

      @Override
      public VertexConsumer color(int colorValue) {
         if (this.isFlag()) {
            this.vertexTable[this.vertexValue + 3] = colorValue;
         }

         return this;
      }

      int[] getValues(float meshRecorderValue, int[] meshRecorderValues) {
         int meshRecorderSize = this.quadCount * 8;
         int[] copyOf = meshRecorderValues != null && meshRecorderValues.length >= meshRecorderSize ? meshRecorderValues : new int[meshRecorderSize];
         System.arraycopy(this.vertexTable, 0, copyOf, 0, meshRecorderSize);
         float meshRecorderFactor = 1.0F - 2.0F * meshRecorderValue;

         for (int i = 0; i < meshRecorderSize; i += 8) {
            copyOf[i] = Float.floatToRawIntBits(0.5F + (Float.intBitsToFloat(copyOf[i]) - 0.5F) * meshRecorderFactor);
            copyOf[i + 2] = Float.floatToRawIntBits(0.5F + (Float.intBitsToFloat(copyOf[i + 2]) - 0.5F) * meshRecorderFactor);
         }

         return copyOf;
      }

      private static byte getValue(float meshRecorderLimit) {
         float meshRecorderValue = meshRecorderLimit < -1.0F ? -1.0F : (meshRecorderLimit > 1.0F ? 1.0F : meshRecorderLimit);
         return (byte)Math.round(meshRecorderValue * 127.0F);
      }

      @Override
      public VertexConsumer normal(float normalValue, float meshRecorderLimit, float normalValue3) {
         if (this.isFlag()) {
            this.vertexTable[this.vertexValue + 7] = getValue(normalValue, meshRecorderLimit, normalValue3);
         }

         return this;
      }

      @Override
      public void vertex(float vertexValue, float vertexValue2, float vertexValue3, int vertexValue4, float quadUnfinishedValue, float vertexValue6, int vertexValue7, int vertexValue8, float meshRecorderValue, float meshRecorderLimit, float vertexValue11) {
         this.handle(vertexValue, vertexValue2, vertexValue3);
         this.vertexTable[this.vertexValue + 3] = vertexValue4;
         this.vertexTable[this.vertexValue + 4] = Float.floatToRawIntBits(quadUnfinishedValue);
         this.vertexTable[this.vertexValue + 5] = Float.floatToRawIntBits(vertexValue6);
         this.vertexTable[this.vertexValue + 6] = vertexValue8;
         this.vertexTable[this.vertexValue + 7] = getValue(meshRecorderValue, meshRecorderLimit, vertexValue11);
      }

      void update() {
         this.quadCount = 0;
         this.vertexValue = -8;
      }

      private void handle(float meshRecorderValue, float meshRecorderValue2, float quadUnfinishedValue) {
         this.vertexValue = this.quadCount * 8;
         if (this.vertexValue + 8 > this.vertexTable.length) {
            int[] values = new int[this.vertexTable.length * 2];
            System.arraycopy(this.vertexTable, 0, values, 0, this.vertexTable.length);
            this.vertexTable = values;
         }

         this.quadCount++;
         this.vertexTable[this.vertexValue] = Float.floatToRawIntBits(meshRecorderValue);
         this.vertexTable[this.vertexValue + 1] = Float.floatToRawIntBits(meshRecorderValue2);
         this.vertexTable[this.vertexValue + 2] = Float.floatToRawIntBits(quadUnfinishedValue);
         this.vertexTable[this.vertexValue + 3] = -1;
         this.vertexTable[this.vertexValue + 4] = 0;
         this.vertexTable[this.vertexValue + 5] = 0;
         this.vertexTable[this.vertexValue + 6] = 15728880;
         this.vertexTable[this.vertexValue + 7] = getValue(0.0F, 1.0F, 0.0F);
      }

      static int getValue(float meshRecorderValue, float meshRecorderLimit, float meshRecorderValue3) {
         return getValue(meshRecorderValue) & 0xFF | (getValue(meshRecorderLimit) & 0xFF) << 8 | (getValue(meshRecorderValue3) & 0xFF) << 16;
      }

      @Override
      public VertexConsumer overlay(int overlayValue, int overlayValue2) {
         return this;
      }

      @Override
      public VertexConsumer light(int lightValue, int lightValue2) {
         if (this.isFlag()) {
            this.vertexTable[this.vertexValue + 6] = lightValue & 65535 | (lightValue2 & 65535) << 16;
         }

         return this;
      }
   }

   static final class Slot {
      final int blockX;
      final int recorderMeshValue;
      final int mutableValue;
      int[] recorderMeshTable;
      boolean dequeActive;
      int[] layerTable;
      final long longValue;
      boolean recorderMeshActive;
      BlockState blockState;
      int layerValue;
      RenderLayer blockLayer;
      float surfaceOffset;
      RenderLayer movingBlockLayer;
      float layerAmount;
      float blockWaveFactor;
      int slotValue;
      int itemsSize = -1;
      BlockState blockStateBlock;
      int blockCount;
      float hsvRgbAmount;

      Slot(long longValue, int value, int value2, int value3) {
         this.longValue = longValue;
         this.blockX = value;
         this.mutableValue = value2;
         this.recorderMeshValue = value3;
      }
   }

   static final class SurfaceView implements BlockRenderView {
      final Mutable mutable = new Mutable();
      private BlockState blockState;
      private static final BlockState defaultState = Blocks.AIR.getDefaultState();
      private ClientWorld clientWorld;
      private final Mutable lightLevelMutable;
      final Mutable recorderMeshMutable = new Mutable();
      private BlockState defaultStateMutable;
      private int minLightY;

      @Override
      public BlockState getBlockState(BlockPos pos2) {
         if (pos2.equals(this.mutable)) {
            return this.defaultStateMutable;
         } else {
            return pos2.equals(this.recorderMeshMutable) ? this.blockState : defaultState;
         }
      }

      @Override
      public FluidState getFluidState(BlockPos pos2) {
         return this.getBlockState(pos2).getFluidState();
      }

      @Override
      public int getColor(BlockPos pos2, ColorResolver colorResolver) {
         return this.clientWorld.getColor(this.mutable, colorResolver);
      }

      @Override
      public int getHeight() {
         return this.clientWorld.getHeight();
      }

      void handleMutableState(ClientWorld clientWorld, int mutableValue, int mutableValue2, int mutableValue3, BlockState blockState, BlockState state2) {
         this.clientWorld = clientWorld;
         this.defaultStateMutable = blockState;
         this.blockState = state2 == null ? defaultState : state2;
         this.mutable.set(mutableValue, mutableValue2, mutableValue3);
         this.recorderMeshMutable.set(mutableValue, mutableValue2 + 1, mutableValue3);
         this.minLightY = mutableValue2 + 1;
      }

      @Override
      public float getBrightness(Direction direction, boolean brightnessFlag) {
         return this.clientWorld.getBrightness(direction, brightnessFlag);
      }

      @Override
      public LightingProvider getLightingProvider() {
         return this.clientWorld.getLightingProvider();
      }

      @Override
      public BlockEntity getBlockEntity(BlockPos pos2) {
         return null;
      }

      SurfaceView() {
         this.lightLevelMutable = new Mutable();
         this.defaultStateMutable = defaultState;
         this.blockState = defaultState;
      }

      @Override
      public int getBottomY() {
         return this.clientWorld.getBottomY();
      }

      @Override
      public int getLightLevel(LightType lightType, BlockPos pos2) {
         this.lightLevelMutable.set(pos2.getX(), Math.max(pos2.getY(), this.minLightY), pos2.getZ());
         return this.clientWorld.getLightLevel(lightType, this.lightLevelMutable);
      }

      void updateStateDefault() {
         this.clientWorld = null;
         this.defaultStateMutable = defaultState;
         this.blockState = defaultState;
      }
   }

   static final class Wave {
      final long startNanos;
      long dequeArrayValue;
      final float[] waveTable;
      final double waveRadius;
      final float baseStrength;
      boolean dequeArrayActive;
      final BlockWaveRenderer.Slot[] slots;
      final int maxRadiusBlocks;

      Wave(long time, double amount, BlockWaveRenderer.Slot[] slots2, float[] amounts, int value, float min) {
         this.startNanos = time;
         this.waveRadius = amount;
         this.slots = slots2;
         this.waveTable = amounts;
         this.maxRadiusBlocks = value;
         this.baseStrength = min;
      }
   }
}
