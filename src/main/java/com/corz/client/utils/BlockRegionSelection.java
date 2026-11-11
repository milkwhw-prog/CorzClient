package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Collection;
import net.minecraft.util.math.BlockPos;

public final class BlockRegionSelection {
   private BlockPos cornerB;
   private LongOpenHashSet allowedPositionKeys;
   private BlockPos cornerA;

   public void setCorners(BlockPos pos, BlockPos pos2) {
      this.cornerA = pos == null ? null : pos.toImmutable();
      this.cornerB = pos2 == null ? null : pos2.toImmutable();
   }

   public int getMinX() {
      return Math.min(this.cornerA.getX(), this.cornerB.getX());
   }

   public int getSizeX() {
      return this.getMaxX() - this.getMinX() + 1;
   }

   public boolean isInsideRegion(BlockPos pos, int posBlockValue) {
      return pos.getX() >= this.getMinX() - posBlockValue
         && pos.getX() <= this.getMaxX() + posBlockValue
         && pos.getY() >= this.getMinY() - posBlockValue
         && pos.getY() <= this.getMaxY() + posBlockValue
         && pos.getZ() >= this.getMinZ() - posBlockValue
         && pos.getZ() <= this.getMaxZ() + posBlockValue;
   }

   public int getMaxY() {
      return Math.max(this.cornerA.getY(), this.cornerB.getY());
   }

   public int getSizeZ() {
      return this.getMaxZ() - this.getMinZ() + 1;
   }

   public int getSizeY() {
      return this.getMaxY() - this.getMinY() + 1;
   }

   public void clearSelection() {
      this.cornerA = null;
      this.cornerB = null;
      this.allowedPositionKeys = null;
   }

   public boolean hasSelection() {
      return this.cornerA != null && this.cornerB != null;
   }

   public void setAllowedPositions(Collection<BlockPos> collection) {
      LongOpenHashSet longOpenHashSet = new LongOpenHashSet(collection.size());

      for (BlockPos blockPos : collection) {
         longOpenHashSet.add(blockPos.asLong());
      }

      this.allowedPositionKeys = longOpenHashSet;
   }

   public boolean isPositionAllowed(BlockPos pos) {
      return this.allowedPositionKeys == null || this.allowedPositionKeys.contains(pos.asLong());
   }

   public void clearAllowedPositions() {
      this.allowedPositionKeys = null;
   }

   public int getMaxX() {
      return Math.max(this.cornerA.getX(), this.cornerB.getX());
   }

   public boolean hasAllowedPositions() {
      return this.allowedPositionKeys != null;
   }

   public int getMinY() {
      return Math.min(this.cornerA.getY(), this.cornerB.getY());
   }

   public int getMaxZ() {
      return Math.max(this.cornerA.getZ(), this.cornerB.getZ());
   }

   public boolean canFlag(BlockPos pos) {
      return this.isInsideRegion(pos, 0);
   }

   public int getMinZ() {
      return Math.min(this.cornerA.getZ(), this.cornerB.getZ());
   }
}
