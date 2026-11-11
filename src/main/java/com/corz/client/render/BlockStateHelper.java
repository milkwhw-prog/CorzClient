package com.corz.client.render;

import com.corz.client.inventory.BlockStateUtilsBucket;
import com.corz.client.world.ElseBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import com.corz.client.utils.RebuildFms;

public final class BlockStateHelper {
   private ElseBuilder else2Else;
   private int boxMaxX;
   private int cachedMinX;
   private int elseValue;
   private int boxMinY;
   private int elseCount;
   private int cameraValue;
   private final List<BlockPos> elseBlockPosList = new ArrayList<>();
   private boolean elseActive;
   private int blockValue;
   private int boxMaxY;
   private static final Set<BlockStateHelper> fromMap = Collections.newSetFromMap(new WeakHashMap<>());
   private int cachedMaxZ;
   private int cachedMinZ;
   private final List<BlockState> cameraBlockStateList = new ArrayList<>();
   private boolean blockActive;
   private final List<int[]> elseIntArrayList;
   private int cachedMinY;
   private int boxMaxZ;
   private final HashOpenSchem hashOpen;
   private final HashMap<Long, Integer> elseIntegerMap = new HashMap<>();
   private int boxMinX;

   public void handleHashOpen(BlockPos pos2) {
      if (pos2 == null) {
         this.hashOpen.updateHashOpen();
      } else {
         this.hashOpen.handle(pos2.getX(), pos2.getY(), pos2.getZ());
      }
   }

   public boolean isBlockActive() {
      return this.blockActive;
   }

   public boolean canFlag(ElseBuilder elseBuilder, int blockValue, int blockValue2, int blockValue3, int n4, int n5) {
      this.handleElseMin(elseBuilder, blockValue, blockValue2, blockValue3, n4, n5);
      return this.elseActive && !this.elseBlockPosList.isEmpty();
   }

   public int getElseCount() {
      return this.elseCount;
   }

   public Integer getNumber(long numberValue) {
      return this.elseIntegerMap.get(numberValue);
   }

   public static void updateHashOpen() {
      synchronized (fromMap) {
         for (BlockStateHelper blockStateHelper : fromMap) {
            blockStateHelper.hashOpen.updateClearTransforms();
            blockStateHelper.cameraValue = -1;
         }
      }
   }

   static int[] getValues(int blockValue, int elseOrElse2Else, int blockValue3, int blockValue4, int blockValue5, int blockValue6, int n4, int n5) {
      int blockValue9 = n5 == 1 ? -blockValue : blockValue;
      int blockValue10 = n5 == 2 ? -blockValue3 : blockValue3;
      int blockValue11;
      int blockValue12;
      switch (n4) {
         case 1:
            blockValue11 = -blockValue10;
            blockValue12 = blockValue9;
            break;
         case 2:
            blockValue11 = -blockValue9;
            blockValue12 = -blockValue10;
            break;
         case 3:
            blockValue11 = blockValue10;
            blockValue12 = -blockValue9;
            break;
         default:
            blockValue11 = blockValue9;
            blockValue12 = blockValue10;
      }

      return new int[]{blockValue4 + blockValue11, blockValue5 + elseOrElse2Else, blockValue6 + blockValue12};
   }

   public BlockStateHelper() {
      this.blockActive = false;
      this.elseActive = false;
      this.elseIntArrayList = new ArrayList<>();
      this.hashOpen = new HashOpenSchem();
      this.cameraValue = -1;
      synchronized (fromMap) {
         fromMap.add(this);
      }
   }

   static BlockState getRotateMirror(BlockState rotateMirror, int rotateMirrorValue, int n4) {
      if (rotateMirror == null) {
         return null;
      } else {
         switch (rotateMirrorValue) {
            case 1:
               rotateMirror = rotateMirror.mirror(BlockMirror.FRONT_BACK);
               break;
            case 2:
               rotateMirror = rotateMirror.mirror(BlockMirror.LEFT_RIGHT);
         }

         switch (n4) {
            case 1:
               rotateMirror = rotateMirror.rotate(BlockRotation.CLOCKWISE_90);
               break;
            case 2:
               rotateMirror = rotateMirror.rotate(BlockRotation.CLOCKWISE_180);
               break;
            case 3:
               rotateMirror = rotateMirror.rotate(BlockRotation.COUNTERCLOCKWISE_90);
         }

         return rotateMirror;
      }
   }

   private void handle(Matrix4f matrix, Vec3d vec, int blockValue, int blockValue2, int blockValue3) {
      float vertexBuilderValue = (float)(blockValue - vec.x);
      float f2 = (float)(blockValue2 - vec.y);
      float amountForDouble = (float)(blockValue3 - vec.z);
      VertexBuilderUtils.handle(matrix, vertexBuilderValue, f2, amountForDouble, vertexBuilderValue + 1.0F, f2 + 1.0F, amountForDouble + 1.0F, getReleaseRight(140), 1.5F);
   }

   public void updateHashOpen2() {
      this.hashOpen.updateHashOpen();
   }

   static int[] getValues(int blockValue, int elseOrElse2Else, int blockValue3, int blockValue4, int blockValue5, int blockValue6, int blockValue7, int blockValue8, int blockValue9, int n4, int n5) {
      int[] blockValues = getValues(blockValue, elseOrElse2Else, blockValue3, blockValue7, blockValue8, blockValue9, n4, n5);
      int[] values2 = getValues(blockValue4, blockValue5, blockValue6, blockValue7, blockValue8, blockValue9, n4, n5);
      return new int[]{
         Math.min(blockValues[0], values2[0]),
         Math.min(blockValues[1], values2[1]),
         Math.min(blockValues[2], values2[2]),
         Math.max(blockValues[0], values2[0]),
         Math.max(blockValues[1], values2[1]),
         Math.max(blockValues[2], values2[2])
      };
   }

   public int getValue() {
      return this.elseIntegerMap.size();
   }

   public RebuildFms getHashOpen() {
      return this.hashOpen.getRebuildFms();
   }

   public void updateHashOpenElseActive() {
      this.elseActive = false;
      this.hashOpen.updateHashOpen();
   }

   public void handleMinCamera(
      MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, ElseBuilder elseBuilder, int cameraValue, int cameraValue2, int cameraValue3, int n4, int n5, OpacityColorData opacityColorData
   ) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.world != null && instance.player != null) {
         this.handleElseMin(elseBuilder, cameraValue, cameraValue2, cameraValue3, n4, n5);
         Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
         Vec3d cameraPos = instance.getEntityRenderDispatcher().camera.getCameraPos();
         if (opacityColorData.showOrigin()) {
            this.handle(positionMatrix, cameraPos, cameraValue, cameraValue2, cameraValue3);
         }

         if (opacityColorData.showBox() && this.elseActive && this.boxMaxX >= this.boxMinX) {
            VertexBuilderUtils.handle(
               positionMatrix,
               (float)(this.boxMinX - cameraPos.x),
               (float)(this.boxMinY - cameraPos.y),
               (float)(this.blockValue - cameraPos.z),
               (float)(this.boxMaxX + 1 - cameraPos.x),
               (float)(this.boxMaxY + 1 - cameraPos.y),
               (float)(this.boxMaxZ + 1 - cameraPos.z),
               getReleaseRight(77),
               1.0F
            );
         }

         if (elseBuilder != null && opacityColorData.mode() != OpacityColorData.Mode.BOX_ONLY) {
            if (this.cameraValue != this.elseCount) {
               this.hashOpen.handleHashOpen(this.elseBlockPosList, this.cameraBlockStateList, this.elseIntArrayList);
               this.cameraValue = this.elseCount;
            }

            float tickProgress = instance.getRenderTickCounter().getTickProgress(false);
            this.hashOpen.canFmsRebuild(opacityColorData, cameraPos, this.elseBlockPosList, this.cameraBlockStateList, getReleaseRight(255), orderedRenderCommandQueue, tickProgress);
            VertexBuilderUtils.updateDrawImmediate();
         } else {
            VertexBuilderUtils.updateDrawImmediate();
         }
      }
   }

   public List<BlockPos> getItems() {
      return this.elseBlockPosList;
   }

   public List<BlockState> getCameraBlockStateList() {
      return this.cameraBlockStateList;
   }

   public int getElseBlockPosList() {
      return this.elseBlockPosList.size();
   }

   private static int getReleaseRight(int releaseRightValue) {
      return releaseRightValue << 24 | MatrixEspUtils.releaseRightValue & 16777215;
   }

   public static void handleHashOpenForBlockPos(BlockPos pos2) {
      synchronized (fromMap) {
         for (BlockStateHelper blockStateHelper : fromMap) {
            blockStateHelper.hashOpen.handle(pos2.getX(), pos2.getY(), pos2.getZ());
         }
      }
   }

   private void handleElseMin(ElseBuilder elseBuilder, int value, int value2, int value3, int n4, int n5) {
      if (!this.elseActive || elseBuilder != this.else2Else || value != this.cachedMinX || value2 != this.cachedMinY || value3 != this.cachedMinZ || n4 != this.elseValue || n5 != this.cachedMaxZ) {
         this.else2Else = elseBuilder;
         this.cachedMinX = value;
         this.cachedMinY = value2;
         this.cachedMinZ = value3;
         this.elseValue = n4;
         this.cachedMaxZ = n5;
         this.elseBlockPosList.clear();
         this.cameraBlockStateList.clear();
         this.elseIntegerMap.clear();
         this.blockActive = false;
         this.elseIntArrayList.clear();
         this.boxMinX = this.boxMinY = this.blockValue = Integer.MAX_VALUE;
         this.boxMaxX = this.boxMaxY = this.boxMaxZ = Integer.MIN_VALUE;
         this.elseActive = true;
         this.elseCount++;
         this.hashOpen.updateHashOpen();
         if (elseBuilder != null) {
            HashSet hashSet = new HashSet();

            for (ElseBuilder.Region region : elseBuilder.elseRegionList) {
               int elseRegionEnd = region.getElseRegionEnd();
               int elseOrElse2Else = region.getElseOrElse2Else();
               int else5 = region.getElse();
               this.elseIntArrayList
                  .add(getValues(elseRegionEnd, elseOrElse2Else, else5, elseRegionEnd + region.elseValue - 1, elseOrElse2Else + region.elseArrayValue - 1, else5 + region.regionValue - 1, value, value2, value3, n4, n5));

               for (int i = 0; i < region.elseArrayValue; i++) {
                  for (int j = 0; j < region.regionValue; j++) {
                     for (int k = 0; k < region.elseValue; k++) {
                        BlockState statesBlock = region.getStatesBlock(k, i, j);
                        if (statesBlock != null && !statesBlock.isAir()) {
                           if (statesBlock.getBlock() == Blocks.BUBBLE_COLUMN) {
                              statesBlock = Blocks.WATER.getDefaultState();
                           } else if (!(statesBlock.getBlock() instanceof FluidBlock) && BlockStateUtilsBucket.getAir(statesBlock) == Items.AIR) {
                              continue;
                           }

                           int elseValue = elseRegionEnd + k;
                           int elseValue2 = elseOrElse2Else + i;
                           int elseValue3 = else5 + j;
                           int elseValue4 = n5 == 1 ? -elseValue : elseValue;
                           int elseValue5 = n5 == 2 ? -elseValue3 : elseValue3;
                           int elseValue6;
                           int elseValue7;
                           switch (n4) {
                              case 1:
                                 elseValue6 = -elseValue5;
                                 elseValue7 = elseValue4;
                                 break;
                              case 2:
                                 elseValue6 = -elseValue4;
                                 elseValue7 = -elseValue5;
                                 break;
                              case 3:
                                 elseValue6 = elseValue5;
                                 elseValue7 = -elseValue4;
                                 break;
                              default:
                                 elseValue6 = elseValue4;
                                 elseValue7 = elseValue5;
                           }

                           int x = value + elseValue6;
                           int y = value2 + elseValue2;
                           int z = value3 + elseValue7;
                           if (hashSet.add(BlockPos.asLong(x, y, z))) {
                              this.elseBlockPosList.add(new BlockPos(x, y, z));
                              BlockState rotateMirror = getRotateMirror(statesBlock, n5, n4);
                              this.cameraBlockStateList.add(rotateMirror);
                              if (!region.elseIntegerMap.isEmpty()) {
                                 Integer integer = region.getNumber(k, i, j);
                                 if (integer != null) {
                                    this.elseIntegerMap.put(BlockPos.asLong(x, y, z), integer);
                                 }
                              }

                              this.boxMinX = Math.min(this.boxMinX, x);
                              this.boxMinY = Math.min(this.boxMinY, y);
                              this.blockValue = Math.min(this.blockValue, z);
                              this.boxMaxX = Math.max(this.boxMaxX, x);
                              this.boxMaxY = Math.max(this.boxMaxY, y);
                              this.boxMaxZ = Math.max(this.boxMaxZ, z);
                              if (this.elseBlockPosList.size() >= 300000) {
                                 this.blockActive = true;
                                 return;
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
}
