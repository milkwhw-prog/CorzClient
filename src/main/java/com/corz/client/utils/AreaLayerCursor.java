package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import com.corz.client.utils.BlockRegionSelection;

public final class AreaLayerCursor {
   private final int rowAxisMin;
   private final int nearCornerValue;
   private final int openMinX;
   public final int rowWidth;
   private final int faceAxisStart;
   public final Axis faceAxis;
   public final boolean startAtRowMax;
   private final int layerBottomY;
   private final int totalLayers;
   private int faceStepIndex;
   private final int openMinZ;
   private final int rowCount;
   private final int openMaxX;
   public final int layerHeight;
   private int layerIndex;
   private final int openMaxZ;
   private final int layerTopY;
   private final int rowAxisMax;
   public final boolean startAtFaceEnd;
   private boolean checkingLeftovers;
   private final int faceAxisEnd;
   private int rowIndex;
   private final int faceLength;
   private final int openMinY;
   private boolean allLayersDone;

   public BlockPos getCurrentTargetPos() {
      return this.toBlockPos(this.getFaceCoord(), this.getTargetRowCoord(), this.getSliceBottomY());
   }

   public boolean isFinished() {
      return this.allLayersDone;
   }

   public void resetLayerProgress() {
      this.rowIndex = 0;
      this.faceStepIndex = 0;
      this.checkingLeftovers = false;
   }

   private static int clampAwayFromEdges(int nearCornerBound, int nearCornerValue, int nearCornerValue2) {
      return nearCornerValue2 - nearCornerValue < 2 ? nearCornerBound : Math.max(nearCornerValue + 1, Math.min(nearCornerValue2 - 1, nearCornerBound));
   }

   public int getRowAxisMin() {
      return this.rowAxisMin;
   }

   public boolean isFullSizeSlice() {
      return this.getSliceWidth() == this.rowWidth && this.getSliceHeight() == this.layerHeight;
   }

   public void advanceFaceStep() {
      if (!this.checkingLeftovers) {
         this.faceStepIndex++;
         if (this.faceStepIndex >= this.faceLength) {
            this.faceStepIndex = 0;
            this.rowIndex++;
            if (this.rowIndex >= this.rowCount) {
               this.checkingLeftovers = true;
            }
         }
      }
   }

   public int getCurrentLayerTopY() {
      return this.layerTopY - this.layerIndex * this.layerHeight;
   }

   public int getLayerIndex() {
      return this.layerIndex;
   }

   private int getSliceRowMax() {
      return Math.min(this.rowAxisMax, this.getSliceRowMin() + this.rowWidth - 1);
   }

   public boolean isCheckingLeftovers() {
      return this.checkingLeftovers;
   }

   public BlockPos toBlockPos(int x, int z, int y2) {
      return this.faceAxis == Axis.X ? new BlockPos(x, y2, z) : new BlockPos(z, y2, x);
   }

   public float getProgressFraction() {
      if (this.allLayersDone) {
         return 1.0F;
      } else {
         double nearCornerValue = this.checkingLeftovers ? 1.0 : (this.rowIndex + (double)this.faceStepIndex / Math.max(1, this.faceLength)) / Math.max(1, this.rowCount);
         return (float)Math.min(1.0, (this.layerIndex + nearCornerValue) / this.totalLayers);
      }
   }

   public int getTargetRowCoord() {
      int nearCornerValue = this.getSliceCenterRow();
      return this.getSliceWidth() == 1 ? nearCornerValue : clampAwayFromEdges(nearCornerValue, this.rowAxisMin, this.rowAxisMax);
   }

   public int getSliceHeight() {
      return this.getCurrentLayerTopY() - this.getSliceBottomY() + 1;
   }

   public int getTotalLayers() {
      return this.totalLayers;
   }

   public int getFaceAxisEnd() {
      return this.faceAxisEnd;
   }

   public boolean matchesSelection(BlockRegionSelection posBlockOpen) {
      return posBlockOpen != null && posBlockOpen.hasSelection()
         ? this.openMinX == posBlockOpen.getMinX()
            && this.openMinY == posBlockOpen.getMinY()
            && this.openMinZ == posBlockOpen.getMinZ()
            && this.openMaxX == posBlockOpen.getMaxX()
            && this.nearCornerValue == posBlockOpen.getMaxY()
            && this.openMaxZ == posBlockOpen.getMaxZ()
         : false;
   }

   public int getFaceDirection() {
      int cornerNearValue = (this.layerIndex * this.rowCount + this.rowIndex) % 2 == 0 ? 1 : -1;
      return this.startAtFaceEnd ? -cornerNearValue : cornerNearValue;
   }

   public BlockPos getPreviousFacePos() {
      int blockPosLimit = this.getFaceCoord() - this.getFaceDirection();
      if (blockPosLimit < this.faceAxisStart) {
         blockPosLimit = this.faceAxisStart;
      } else if (blockPosLimit > this.faceAxisEnd) {
         blockPosLimit = this.faceAxisEnd;
      }

      return this.toBlockPos(blockPosLimit, this.getTargetRowCoord(), this.getSliceBottomY());
   }

   public int getFaceCoord() {
      return this.getFaceDirection() > 0 ? this.faceAxisStart + this.faceStepIndex : this.faceAxisEnd - this.faceStepIndex;
   }

   public int getSliceCenterRow() {
      return Math.min(this.rowAxisMax, this.rowAxisMin + this.rowWidth / 2 + this.getRowOrderIndex() * this.rowWidth);
   }

   private int getRowOrderIndex() {
      boolean cornerNearFlag = this.layerIndex % 2 == 0 != this.startAtRowMax;
      return cornerNearFlag ? this.rowIndex : this.rowCount - 1 - this.rowIndex;
   }

   public int getSliceBottomY() {
      return Math.max(this.layerBottomY, this.getCurrentLayerTopY() - (this.layerHeight - 1));
   }

   private static int ceilDiv(int nearCornerValue, int nearCornerDivisor) {
      return (nearCornerValue + nearCornerDivisor - 1) / nearCornerDivisor;
   }

   private int getSliceRowMin() {
      return Math.max(this.rowAxisMin, this.getSliceCenterRow() - this.rowWidth / 2);
   }

   public BlockPos getSliceCenterPos() {
      int blockPosValue = this.getSliceBottomY() + (this.getCurrentLayerTopY() - this.getSliceBottomY()) / 2;
      return this.toBlockPos(this.getFaceCoord(), this.getTargetRowCoord(), this.getSliceHeight() == 1 ? blockPosValue : clampAwayFromEdges(blockPosValue, this.layerBottomY, this.layerTopY));
   }

   public int getSliceWidth() {
      return this.getSliceRowMax() - this.getSliceRowMin() + 1;
   }

   public String getProgressDescription() {
      return this.checkingLeftovers
         ? "layer " + (this.layerIndex + 1) + "/" + this.totalLayers + " \u00b7 checking leftovers"
         : "layer "
            + (this.layerIndex + 1)
            + "/"
            + this.totalLayers
            + " \u00b7 row "
            + (this.getRowOrderIndex() + 1)
            + "/"
            + this.rowCount
            + " \u00b7 "
            + (this.faceStepIndex + 1)
            + "/"
            + this.faceLength
            + " \u00b7 face "
            + this.getSliceWidth()
            + "x"
            + this.getSliceHeight()
            + "="
            + this.getSliceWidth() * this.getSliceHeight()
            + " cells \u00b7 axis "
            + (this.faceAxis == Axis.X ? "X" : "Z")
            + (!this.startAtRowMax && !this.startAtFaceEnd ? "" : " \u00b7 from the near corner");
   }

   public int getRowAxisMax() {
      return this.rowAxisMax;
   }

   public List<BlockPos> getSurroundingPositions() {
      BlockPos blockPos = this.getSliceCenterPos();
      int face = this.getFaceCoord();
      int nearCornerValue = this.faceAxis == Axis.X ? blockPos.getZ() : blockPos.getX();
      int y = blockPos.getY();
      ArrayList arrayList = new ArrayList(9);
      arrayList.add(this.toBlockPos(face, nearCornerValue, y));
      arrayList.add(this.toBlockPos(face, nearCornerValue, y + 1));
      arrayList.add(this.toBlockPos(face, nearCornerValue, y - 1));
      arrayList.add(this.toBlockPos(face, nearCornerValue - 1, y));
      arrayList.add(this.toBlockPos(face, nearCornerValue + 1, y));
      arrayList.add(this.toBlockPos(face, nearCornerValue - 1, y + 1));
      arrayList.add(this.toBlockPos(face, nearCornerValue + 1, y + 1));
      arrayList.add(this.toBlockPos(face, nearCornerValue - 1, y - 1));
      arrayList.add(this.toBlockPos(face, nearCornerValue + 1, y - 1));
      return arrayList;
   }

   public int getFaceAxisStart() {
      return this.faceAxisStart;
   }

   public int getRowIndex() {
      return this.rowIndex;
   }

   public boolean advanceToNextLayer() {
      this.layerIndex++;
      if (this.layerIndex >= this.totalLayers) {
         this.allLayersDone = true;
         return false;
      } else {
         this.rowIndex = 0;
         this.faceStepIndex = 0;
         this.checkingLeftovers = false;
         if (!this.isFullSizeSlice()) {
            this.advanceFaceStep();
         }

         return true;
      }
   }

   public AreaLayerCursor(BlockRegionSelection posBlockOpen, int nearCornerBound, int rocketsBuyBound, boolean nearCornerFlag, boolean nearCornerFlag2) {
      this.layerHeight = Math.max(1, nearCornerBound);
      this.rowWidth = Math.max(1, rocketsBuyBound);
      this.startAtRowMax = nearCornerFlag;
      this.startAtFaceEnd = nearCornerFlag2;
      this.openMinX = posBlockOpen.getMinX();
      this.openMinY = posBlockOpen.getMinY();
      this.openMinZ = posBlockOpen.getMinZ();
      this.openMaxX = posBlockOpen.getMaxX();
      this.nearCornerValue = posBlockOpen.getMaxY();
      this.openMaxZ = posBlockOpen.getMaxZ();
      this.faceAxis = posBlockOpen.getSizeX() >= posBlockOpen.getSizeZ() ? Axis.X : Axis.Z;
      if (this.faceAxis == Axis.X) {
         this.faceAxisStart = posBlockOpen.getMinX();
         this.faceAxisEnd = posBlockOpen.getMaxX();
         this.rowAxisMin = posBlockOpen.getMinZ();
         this.rowAxisMax = posBlockOpen.getMaxZ();
      } else {
         this.faceAxisStart = posBlockOpen.getMinZ();
         this.faceAxisEnd = posBlockOpen.getMaxZ();
         this.rowAxisMin = posBlockOpen.getMinX();
         this.rowAxisMax = posBlockOpen.getMaxX();
      }

      this.layerBottomY = posBlockOpen.getMinY();
      this.layerTopY = posBlockOpen.getMaxY();
      this.totalLayers = ceilDiv(this.layerTopY - this.layerBottomY + 1, this.layerHeight);
      this.rowCount = ceilDiv(this.rowAxisMax - this.rowAxisMin + 1, this.rowWidth);
      this.faceLength = this.faceAxisEnd - this.faceAxisStart + 1;
      if (!this.isFullSizeSlice()) {
         this.advanceFaceStep();
      }
   }
}
