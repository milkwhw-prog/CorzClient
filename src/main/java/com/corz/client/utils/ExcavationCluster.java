package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.corz.client.utils.CraftedExcavation;
import com.corz.client.utils.ExcavationCraftedType;
import com.corz.client.utils.SpawnerSculkUtils;

public final class ExcavationCluster {
   private int excavationHitCount;
   private int flaggedBit1Count;
   private int flaggedBit8Count;
   private final List<CraftedExcavation> cells = new ArrayList<>();
   private int minZ;
   private int chestCount;
   private final int[] typeIdCounts;
   private int hopperCount;
   private int peakCraftedPrimary;
   private final int[] countByCategory = new int[ExcavationCraftedType.values().length];
   private int spawnerCount;
   private boolean hasLastChunkKey;
   private int maxY;
   private int craftedSum;
   private int peakCraftedSecondary;
   private int minY;
   private int furnaceCount;
   private int peakCraftedTertiary;
   private int maxX;
   private long lastChunkKey;
   private final int[] weightByCategory = new int[ExcavationCraftedType.values().length];
   private final Set<Long> chunkKeySet;
   private int minX;
   private int shulkerCount;
   private int peakCraftedQuaternary;
   private int maxZ;
   private final Set<Integer> penDivValueSet;

   public void merge(ExcavationCluster maxCrafted) {
      for (CraftedExcavation craftedExcavation : maxCrafted.cells) {
         this.addCell(craftedExcavation);
      }

      this.craftedSum = this.craftedSum + maxCrafted.craftedSum;
      this.peakCraftedPrimary = Math.max(this.peakCraftedPrimary, maxCrafted.peakCraftedPrimary);
      this.peakCraftedQuaternary = Math.max(this.peakCraftedQuaternary, maxCrafted.peakCraftedQuaternary);
      this.peakCraftedTertiary = Math.max(this.peakCraftedTertiary, maxCrafted.peakCraftedTertiary);
      this.peakCraftedSecondary = Math.max(this.peakCraftedSecondary, maxCrafted.peakCraftedSecondary);
   }

   public int getSpawnerCount() {
      return this.spawnerCount;
   }

   public void addCell(CraftedExcavation craftedExcavation) {
      this.cells.add(craftedExcavation);
      int ordinal = craftedExcavation.excavationCraftedType.ordinal();
      this.countByCategory[ordinal]++;
      this.weightByCategory[ordinal] = this.weightByCategory[ordinal] + craftedExcavation.craftedWeight;
      if (craftedExcavation.craftedTypeId > 0 && craftedExcavation.craftedTypeId < 148) {
         this.typeIdCounts[craftedExcavation.craftedTypeId]++;
      }

      if (craftedExcavation.penDivValue != 0) {
         this.penDivValueSet.add(craftedExcavation.penDivValue);
      }

      if (craftedExcavation.hasFlag(1)) {
         this.flaggedBit1Count++;
      }

      if (craftedExcavation.hasFlag(2)) {
         this.furnaceCount++;
      }

      if (craftedExcavation.hasFlag(8)) {
         this.flaggedBit8Count++;
      }

      if (craftedExcavation.hasFlag(16)) {
         this.hopperCount++;
      }

      if (craftedExcavation.hasFlag(32)) {
         this.excavationHitCount++;
      }

      if (craftedExcavation.hasFlag(128)) {
         this.shulkerCount++;
      }

      if (craftedExcavation.hasFlag(256)) {
         this.spawnerCount++;
      }

      if (craftedExcavation.hasFlag(16) && craftedExcavation.excavationCraftedType == ExcavationCraftedType.STORAGE) {
         this.chestCount++;
      }

      long time = packChunkKey(craftedExcavation.posX >> 4, craftedExcavation.posZ >> 4);
      if (!this.hasLastChunkKey || time != this.lastChunkKey) {
         this.chunkKeySet.add(time);
         this.lastChunkKey = time;
         this.hasLastChunkKey = true;
      }

      if (craftedExcavation.posX < this.minX) {
         this.minX = craftedExcavation.posX;
      }

      if (craftedExcavation.posX > this.maxX) {
         this.maxX = craftedExcavation.posX;
      }

      if (craftedExcavation.posY < this.minY) {
         this.minY = craftedExcavation.posY;
      }

      if (craftedExcavation.posY > this.maxY) {
         this.maxY = craftedExcavation.posY;
      }

      if (craftedExcavation.posZ < this.minZ) {
         this.minZ = craftedExcavation.posZ;
      }

      if (craftedExcavation.posZ > this.maxZ) {
         this.maxZ = craftedExcavation.posZ;
      }
   }

   public int getPeakTertiary() {
      return this.peakCraftedTertiary;
   }

   public double getCenterZ() {
      return (this.minZ + this.maxZ) / 2.0;
   }

   public int getPeakSecondary() {
      return this.peakCraftedSecondary;
   }

   public int getDistinctPenDivCount() {
      return this.penDivValueSet.size();
   }

   public int getCraftedSum() {
      return this.craftedSum;
   }

   public int getPeakQuaternary() {
      return this.peakCraftedQuaternary;
   }

   public double getCenterY() {
      return (this.minY + this.maxY) / 2.0;
   }

   public int getHopperCount() {
      return this.hopperCount;
   }

   public void addCraftedSum(int craftedValue) {
      this.craftedSum += craftedValue;
   }

   public int getShulkerCount() {
      return this.shulkerCount;
   }

   public int getLightingTrial() {
      return this.countByCategory[ExcavationCraftedType.STORAGE.ordinal()]
         + this.countByCategory[ExcavationCraftedType.UTILITY.ordinal()]
         + this.countByCategory[ExcavationCraftedType.LIGHTING.ordinal()]
         + this.countByCategory[ExcavationCraftedType.REDSTONE.ordinal()]
         + this.countByCategory[ExcavationCraftedType.CRAFTED.ordinal()]
         + this.countByCategory[ExcavationCraftedType.FARM.ordinal()]
         + this.countByCategory[ExcavationCraftedType.EXCAVATION.ordinal()]
         + this.countByCategory[ExcavationCraftedType.TRIAL.ordinal()];
   }

   public double getCraftedDensityRatio() {
      int lightingTrial = this.getLightingTrial();
      int craftedValue = Math.min(this.craftedSum, lightingTrial * 4 + 16);
      return (double)craftedValue / (craftedValue + lightingTrial + 1);
   }

   public int getFlaggedBit1Count() {
      return this.flaggedBit1Count;
   }

   public int getMaxX() {
      return this.maxX;
   }

   public List<CraftedExcavation> getCells() {
      return this.cells;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public int getWeightForCategory(ExcavationCraftedType excavationCraftedType) {
      return this.weightByCategory[excavationCraftedType.ordinal()];
   }

   public int getTypeIdCount(int index) {
      return index > 0 && index < 148 ? this.typeIdCounts[index] : 0;
   }

   public int getPeakPrimary() {
      return this.peakCraftedPrimary;
   }

   public int getMinX() {
      return this.minX;
   }

   public Set<Long> getChunkKeys() {
      return this.chunkKeySet;
   }

   public static long packChunkKey(int craftedValue, int j) {
      return craftedValue & 4294967295L | (long)j << 32;
   }

   public int getExcavationHitCount() {
      return this.excavationHitCount;
   }

   public int getMaxZ() {
      return this.maxZ;
   }

   public int getMinY() {
      return this.minY;
   }

   public int getCountForCategory(ExcavationCraftedType excavationCraftedType) {
      return this.countByCategory[excavationCraftedType.ordinal()];
   }

   public int getCellCount() {
      return this.cells.size();
   }

   public ExcavationCluster() {
      this.typeIdCounts = new int[148];
      this.penDivValueSet = new HashSet<>();
      this.chunkKeySet = new HashSet<>();
      this.lastChunkKey = 0L;
      this.hasLastChunkKey = false;
      this.craftedSum = 0;
      this.flaggedBit1Count = 0;
      this.furnaceCount = 0;
      this.flaggedBit8Count = 0;
      this.hopperCount = 0;
      this.excavationHitCount = 0;
      this.shulkerCount = 0;
      this.spawnerCount = 0;
      this.chestCount = 0;
      this.peakCraftedPrimary = 0;
      this.peakCraftedQuaternary = 0;
      this.peakCraftedTertiary = 0;
      this.peakCraftedSecondary = 0;
      this.minX = Integer.MAX_VALUE;
      this.minY = Integer.MAX_VALUE;
      this.minZ = Integer.MAX_VALUE;
      this.maxX = Integer.MIN_VALUE;
      this.maxY = Integer.MIN_VALUE;
      this.maxZ = Integer.MIN_VALUE;
   }

   public void refreshStats() {
   }

   public long getClusterKey() {
      int craftedValue = this.minX >> 4;
      int j = this.minZ >> 4;
      return packChunkKey(craftedValue, j) ^ (long)(this.minY >> 4) << 20;
   }

   public double getCenterX() {
      return (this.minX + this.maxX) / 2.0;
   }

   public int getChunkKeyCount() {
      return this.chunkKeySet.size();
   }

   public int getFurnaceCount() {
      return this.furnaceCount;
   }

   public int getBarrelFlagTypeCount() {
      int craftedCounter = 0;

      for (int i = 1; i < 148; i++) {
         if (this.typeIdCounts[i] > 0 && SpawnerSculkUtils.isBarrelId(i)) {
            craftedCounter++;
         }
      }

      return craftedCounter;
   }

   public void setPeaks(int max, int max2, int max3, int max4) {
      this.peakCraftedPrimary = max;
      this.peakCraftedQuaternary = max2;
      this.peakCraftedTertiary = max3;
      this.peakCraftedSecondary = max4;
   }

   public int getMinZ() {
      return this.minZ;
   }

   public int getChestCount() {
      return this.chestCount;
   }

   public int getExcavationPatternCount() {
      int craftedCounter = 0;
      if (this.getTypeIdCount(139) > 0) {
         craftedCounter++;
      }

      if (this.getTypeIdCount(140) > 0) {
         craftedCounter++;
      }

      if (this.getTypeIdCount(141) > 0) {
         craftedCounter++;
      }

      if (this.getTypeIdCount(147) > 0) {
         craftedCounter++;
      }

      return craftedCounter;
   }
}
