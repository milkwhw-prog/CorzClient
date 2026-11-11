package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.BlockStateUtilsWorld;
import com.corz.client.utils.BrickStoneUtils;
import com.corz.client.utils.CityMonument;
import com.corz.client.utils.CraftedExcavation;
import com.corz.client.utils.ExcavationCraftedType;
import com.corz.client.utils.ExcavationTypeStats;
import com.corz.client.utils.BiomeCategoryMapper;
import com.corz.client.utils.ExcavationClusterer;
import com.corz.client.utils.ChunkScanScheduler;
import com.corz.client.utils.ExcavationCluster;
import com.corz.client.utils.SensorBlock;
import com.corz.client.utils.StateBlockFlag;
import com.corz.client.utils.TimesSensorUtils;
import com.corz.client.utils.SiteBook;
import com.corz.client.utils.BaseCore;
import com.corz.client.utils.Clue;

public final class BlockSensor {
   private ClientWorld world;
   private final StateBlockFlag excavationHashStateBlockFlag;
   private int scanTickCounter;
   private final Map<Long, Boolean> blockBubbleBooleanMap;
   private int yinclusiveValue;
   private int markersThreadValue;
   private boolean yinclusiveActive;
   private SiteBook siteBook;
   private int bottomY;
   private final BiomeCategoryMapper forestDark;
   private int blockSensorValue;
   private final ChunkScanScheduler hashChunk = new ChunkScanScheduler();
   private int topYinclusive;
   private final LongOpenHashSet longOpenHashSet;
   private volatile BlockSensor.Job job;
   private final ExecutorService threadExecutor;
   private int netherEnd;
   private int hashClearCount;
   private final Map<Long, BlockSensor.Cached> naturalCachedMap;
   private long timeMillis;
   private long lastJobTimeMs;
   private boolean markersThreadActive;
   private long timeMillisYinclusive;
   private int markersThreadCount;
   private int scanRadiusBlocks;
   private boolean excavationHashActive;

   static boolean isCityMonumentValid(CityMonument cityMonument) {
      return cityMonument.craftedExcavationActive || cityMonument.monumentScore > 0 || cityMonument.cityActive;
   }

   private void scanEntities(ExcavationCluster maxCrafted, ExcavationTypeStats flagsHandle) {
      if (this.world != null) {
         try {
            Box box = new Box(maxCrafted.getMinX() - 16, maxCrafted.getMinY() - 8, maxCrafted.getMinZ() - 16, maxCrafted.getMaxX() + 17, maxCrafted.getMaxY() + 9, maxCrafted.getMaxZ() + 17);
            int value = 0;
            int value2 = 0;
            HashMap hashMap = new HashMap();

            for (Entity entity : this.world.getOtherEntities(null, box)) {
               if (entity instanceof VillagerEntity) {
                  value++;
               } else if (entity instanceof IronGolemEntity) {
                  value2++;
               } else if (entity instanceof AnimalEntity) {
                  hashMap.merge(entity.getType(), 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
               }
            }

            int max = 0;

            for (int entitiesWorldBound : (Iterable<Integer>) (hashMap.values())) {
               max = Math.max(max, entitiesWorldBound);
            }

            flagsHandle.entitiesWorldTier = value;
            flagsHandle.entitiesWorldValue = value2;
            flagsHandle.peakValue = max;
            maxCrafted.setPeaks(value, value2, 0, max);
         } catch (Throwable e2) {
         }
      }
   }

   private void updateMarkerThread() {
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();

      for (BlockSensor.Cached cached : this.naturalCachedMap.values()) {
         arrayList.addAll(cached.cells);
         arrayList2.addAll(cached.markers);
      }

      BlockSensor.Job job = new BlockSensor.Job(this.markersThreadCount, this.netherEnd, this.timeMillisYinclusive, arrayList, arrayList2);

      for (CraftedExcavation craftedExcavation : (Iterable<com.corz.client.utils.CraftedExcavation>) (arrayList)) {
         if (isExcavationUsable(craftedExcavation)) {
            job.markerCraftedExcavations.add(craftedExcavation);
         }
      }

      for (int i = -this.scanRadiusBlocks; i <= this.scanRadiusBlocks; i++) {
         for (int j = -this.scanRadiusBlocks; j <= this.scanRadiusBlocks; j++) {
            if (this.canChunkWorld(this.blockSensorValue + i, this.markersThreadValue + j)) {
               job.scoreFortressPositionSet.add(SiteBook.getTime(this.blockSensorValue + i, this.markersThreadValue + j));
            }
         }
      }

      this.markersThreadActive = true;
      this.threadExecutor.submit(() -> {
         try {
            job.markersThreadMaxCraftedList = ExcavationClusterer.clusterCells(job.markerCraftedExcavations, 6, 1);
         } catch (Throwable e) {
            job.markersThreadMaxCraftedList = new ArrayList<>();
         }

         this.job = job;
      });
   }

   static boolean isExcavationUsable(CraftedExcavation craftedExcavation) {
      if (craftedExcavation.excavationCraftedType != ExcavationCraftedType.STORAGE
         && craftedExcavation.excavationCraftedType != ExcavationCraftedType.UTILITY
         && craftedExcavation.excavationCraftedType != ExcavationCraftedType.SPAWNER) {
         return switch (craftedExcavation.craftedTypeId) {
            case 7, 9, 15, 101, 102, 103, 104, 105, 106, 108 -> true;
            default -> false;
         };
      } else {
         return true;
      }
   }

   private boolean canChunkWorld(int chunkWorldValue, int chunkWorldValue2) {
      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            if (!this.hashChunk.isChunkScanned(SiteBook.getTime(chunkWorldValue + i, chunkWorldValue2 + j))
               || this.world.getChunkManager().getWorldChunk(chunkWorldValue + i, chunkWorldValue2 + j) == null) {
               return false;
            }
         }
      }

      return true;
   }

   private void scoreFortressJob(BlockSensor.Job job2, int scoreFortressValue) {
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      HashSet hashSet = new HashSet<>(job2.scoreFortressPositionSet);

      for (ExcavationCluster maxCrafted : job2.markersThreadMaxCraftedList) {
         if (maxCrafted.getCells().size() >= 2) {
            if (!job2.scoreFortressPositionSet.containsAll(maxCrafted.getChunkKeys())) {
               for (int i = maxCrafted.getMinX() >> 4; i <= maxCrafted.getMaxX() >> 4; i++) {
                  for (int j = maxCrafted.getMinZ() >> 4; j <= maxCrafted.getMaxZ() >> 4; j++) {
                     hashSet.remove(SiteBook.getTime(i, j));
                  }
               }
            } else if (BaseCore.evaluateChestBed(maxCrafted.getCells(), this::getChestBlock, scoreFortressValue, BaseCore.Guard.NONE, job2.scanPassIndex).score() > 0
               && (!this.yinclusiveActive || job2.scanPassIndex != 0 || this.isMaxCraftedUsable(maxCrafted))) {
               ExcavationTypeStats naturalCounts = this.getNaturalCounts(maxCrafted, job2);
               CityMonument timesCity = TimesSensorUtils.detectCityMonument(naturalCounts);
               BaseCore.Guard guard = !timesCity.endCity && !timesCity.fortress
                  ? (!isCityAncient(maxCrafted, timesCity) && !isCityMonumentValid(timesCity) && !canUtilityRedstone(maxCrafted, naturalCounts) ? BaseCore.Guard.NONE : BaseCore.Guard.STRUCTURE)
                  : BaseCore.Guard.STRICT;
               BaseCore.Result chestBed = BaseCore.evaluateChestBed(maxCrafted.getCells(), this::getChestBlock, scoreFortressValue, guard, job2.scanPassIndex);
               if (chestBed.score() > 0 && (job2.scanPassIndex != 0 || !this.canBlockBubble(maxCrafted))) {
                  double[] craftedExcavation = getCraftedCenter(maxCrafted);
                  String scoreFortressText = "blk:" + maxCrafted.getClusterKey();

                  for (int k = 2; linkedHashMap.containsKey(scoreFortressText); k++) {
                     scoreFortressText = "blk:" + maxCrafted.getClusterKey() + "#" + k;
                  }

                  linkedHashMap.put(
                     scoreFortressText,
                     new SiteBook.Pending(
                        Clue.BLOCKS, (int)Math.round(craftedExcavation[0]), (int)Math.round(craftedExcavation[1]), (int)Math.round(craftedExcavation[2]), scoreFortressText, chestBed.score(), chestBed.describe()
                     )
                  );
               }
            }
         }
      }

      this.siteBook.handleClueText(new ArrayList<>(linkedHashMap.values()), hashSet, this.timeMillisYinclusive);
   }

   public int getSensorValue() {
      return this.naturalCachedMap.size();
   }

   static boolean canUtilityRedstone(ExcavationCluster maxCrafted, ExcavationTypeStats flagsHandle) {
      int utilityRedstoneLimit = flagsHandle.getTypeCount(132);
      int utilityRedstoneValue = flagsHandle.getTypeCount(133) + flagsHandle.getTypeCount(134) + flagsHandle.getTypeCount(135) + flagsHandle.getTypeCount(136) + flagsHandle.getTypeCount(138);
      return utilityRedstoneLimit >= 1
         && utilityRedstoneValue == 0
         && (flagsHandle.getTypeCount(137) > 0 || flagsHandle.getTypeCount(65) > 0)
         && maxCrafted.getCountForCategory(ExcavationCraftedType.UTILITY) == 0
         && maxCrafted.getCountForCategory(ExcavationCraftedType.REDSTONE) == 0
         && maxCrafted.getCountForCategory(ExcavationCraftedType.STORAGE) <= 2 * utilityRedstoneLimit;
   }

   int getChestBlock(CraftedExcavation craftedExcavation) {
      if (this.world == null) {
         return 0;
      } else {
         try {
            BlockState blockState = this.world.getBlockState(new BlockPos(craftedExcavation.posX, craftedExcavation.posY, craftedExcavation.posZ));
            if (blockState.getBlock() instanceof ChestBlock && blockState.contains(ChestBlock.CHEST_TYPE)) {
               ChestType chestType = blockState.get(ChestBlock.CHEST_TYPE);
               return chestType == ChestType.LEFT ? 1 : (chestType == ChestType.RIGHT ? 2 : 0);
            } else {
               return 0;
            }
         } catch (Throwable e) {
            return 0;
         }
      }
   }

   static int sumIntegers(List<Integer> list) {
      ArrayList arrayList = new ArrayList(list);
      Collections.sort(arrayList);
      return (Integer)arrayList.get(arrayList.size() / 2);
   }

   public BlockSensor() {
      this.forestDark = new BiomeCategoryMapper();
      this.excavationHashStateBlockFlag = new StateBlockFlag();
      this.naturalCachedMap = new ConcurrentHashMap<>();
      this.threadExecutor = Executors.newSingleThreadExecutor(runnable -> {
         Thread thread = new Thread(runnable, "Corz-Bloodhound");
         thread.setDaemon(true);
         thread.setPriority(1);
         return thread;
      });
      this.yinclusiveActive = true;
      this.yinclusiveValue = -1;
      this.longOpenHashSet = new LongOpenHashSet();
      this.scanRadiusBlocks = 8;
      this.blockBubbleBooleanMap = new LinkedHashMap<Long, Boolean>(64, 0.75F, true) {
         @Override
         protected boolean removeEldestEntry(Entry<Long, Boolean> entry) {
            return this.size() > 512;
         }
      };
   }

   boolean canBlockBubble(ExcavationCluster maxCrafted) {
      if (this.world == null) {
         return false;
      } else {
         Boolean blockBubble = this.blockBubbleBooleanMap.get(maxCrafted.getClusterKey());
         if (blockBubble != null) {
            return blockBubble;
         } else {
            int blockBubbleValue = maxCrafted.getMinX() - 3;
            int maxBottomY = Math.max(this.bottomY, maxCrafted.getMinY() - 3);
            int blockBubbleValue2 = maxCrafted.getMinZ() - 3;
            int blockBubbleLimit = maxCrafted.getMaxX() + 3;
            int minTopYinclusive = Math.min(this.topYinclusive, maxCrafted.getMaxY() + 3);
            int blockBubbleLimit2 = maxCrafted.getMaxZ() + 3;
            int blockBubbleCounter = 0;
            int blockBubbleCounter2 = 0;
            Mutable mutable = new Mutable();

            for (int i = blockBubbleValue; i <= blockBubbleLimit && blockBubbleCounter2 < 30000; i++) {
               for (int j = maxBottomY; j <= minTopYinclusive && blockBubbleCounter2 < 30000; j++) {
                  for (int k = blockBubbleValue2; k <= blockBubbleLimit2 && blockBubbleCounter2 < 30000; k++) {
                     blockBubbleCounter2++;
                     BlockState blockState = this.world.getBlockState(mutable.set(i, j, k));
                     if (blockState.isAir() || blockState.getBlock() instanceof FluidBlock || blockState.isOf(Blocks.BUBBLE_COLUMN)) {
                        blockBubbleCounter++;
                     }
                  }
               }
            }

            boolean withinLimits = isWithinLimits(blockBubbleCounter, blockBubbleCounter2);
            this.blockBubbleBooleanMap.put(maxCrafted.getClusterKey(), withinLimits);
            return withinLimits;
         }
      }
   }

   private static long computeScanSignature(List<CraftedExcavation> list, BlockStateUtilsWorld.ChunkScan chunkScan) {
      long blockSensorValue = 1125899906842597L;

      for (CraftedExcavation craftedExcavation : list) {
         long blockSensorValue2 = 31L * blockSensorValue + craftedExcavation.posX;
         long blockSensorValue3 = 31L * blockSensorValue2 + craftedExcavation.posY;
         long blockSensorValue4 = 31L * blockSensorValue3 + craftedExcavation.posZ;
         long blockSensorValue5 = 31L * blockSensorValue4 + craftedExcavation.craftedTypeId;
         long blockSensorValue6 = 31L * blockSensorValue5 + craftedExcavation.flagMask;
         blockSensorValue = 31L * blockSensorValue6 + craftedExcavation.craftedWeight;
      }

      long blockSensorValue7 = 31L * blockSensorValue + chunkScan.scannedCount;
      long blockSensorValue8 = 31L * blockSensorValue7 + chunkScan.sensorBlocks.size();

      for (int i = 1; i < 148; i++) {
         blockSensorValue8 = 31L * blockSensorValue8 + chunkScan.flagsHandle.getTypeCount(i);
      }

      return blockSensorValue8;
   }

   public void updateHashClear() {
      this.hashChunk.clearScannedChunks();
      this.naturalCachedMap.clear();
      this.excavationHashStateBlockFlag.reset();
      this.markersThreadCount++;
      this.job = null;
      this.markersThreadActive = false;
      this.excavationHashActive = false;
      this.timeMillis = 0L;
      this.lastJobTimeMs = 0L;
      this.scanTickCounter = 0;
      this.hashClearCount = 0;
      this.longOpenHashSet.clear();
      this.blockBubbleBooleanMap.clear();
   }

   static double[] getCraftedCenter(ExcavationCluster maxCrafted) {
      double craftedExcavationTotal = 0.0;
      double craftedExcavationTotal2 = 0.0;
      double craftedExcavationTotal3 = 0.0;
      double craftedExcavationDivisor = 0.0;

      for (CraftedExcavation craftedExcavation : maxCrafted.getCells()) {
         double craftedExcavationFactor = craftedExcavation.excavationCraftedType == ExcavationCraftedType.STORAGE ? 4.0 : 1.0;
         craftedExcavationTotal += craftedExcavation.posX * craftedExcavationFactor;
         craftedExcavationTotal2 += craftedExcavation.posY * craftedExcavationFactor;
         craftedExcavationTotal3 += craftedExcavation.posZ * craftedExcavationFactor;
         craftedExcavationDivisor += craftedExcavationFactor;
      }

      return craftedExcavationDivisor <= 0.0 ? new double[]{maxCrafted.getCenterX(), maxCrafted.getCenterY(), maxCrafted.getCenterZ()} : new double[]{craftedExcavationTotal / craftedExcavationDivisor, craftedExcavationTotal2 / craftedExcavationDivisor, craftedExcavationTotal3 / craftedExcavationDivisor};
   }

   static boolean isCityAncient(ExcavationCluster maxCrafted, CityMonument cityMonument) {
      boolean cityAncientFlag = maxCrafted.getMaxY() < 25;
      return cityMonument.cityAncientFlag
         || cityMonument.ruinedPortal
         || cityMonument.ancientCity
         || cityMonument.stronghold
         || cityMonument.monument
         || cityMonument.fortress
         || cityMonument.bastion
         || cityMonument.shipwreck
         || cityMonument.geode
         || cityMonument.mansion
         || cityMonument.jungleTemple
         || cityMonument.outpost
         || cityMonument.igloo
         || cityMonument.desertPyramid
         || cityMonument.endCity
         || cityMonument.village
         || cityMonument.swampHut
         || !cityAncientFlag && (cityMonument.trailRuins || cityMonument.oceanRuin);
   }

   public void onBlockChanged(BlockPos pos2, BlockState state, BlockState blockState) {
      if (state != blockState) {
         int liquidAir = BrickStoneUtils.classifyBlockState(state);
         int classifyBlockState = BrickStoneUtils.classifyBlockState(blockState);
         boolean luminanceChunkFlag = liquidAir == 3 || classifyBlockState == 3;
         if (luminanceChunkFlag) {
            this.excavationHashStateBlockFlag.onBlockTransition(pos2.getX(), pos2.getY(), pos2.getZ(), liquidAir, classifyBlockState, state, blockState, System.currentTimeMillis());
         }

         if (luminanceChunkFlag || state.getLuminance() != blockState.getLuminance()) {
            this.hashChunk.shortenRescanDelay(pos2.getX() >> 4, pos2.getZ() >> 4, 2000L);
         }
      }
   }

   public void configureScan(ClientWorld world, int value, int value2, int chunkLimit, SiteBook siteBook, int scoreFortressValue, boolean flag, long timeMillis) {
      this.world = world;
      this.siteBook = siteBook;
      this.timeMillisYinclusive = timeMillis;
      this.bottomY = world.getBottomY();
      this.topYinclusive = world.getTopYInclusive();
      this.netherEnd = BlockStateUtilsWorld.getDimensionIndex(world);
      this.scanRadiusBlocks = chunkLimit;
      this.blockSensorValue = value;
      this.markersThreadValue = value2;
      if (this.yinclusiveActive != flag || this.yinclusiveValue != scoreFortressValue) {
         this.yinclusiveActive = flag;
         this.yinclusiveValue = scoreFortressValue;
         this.excavationHashActive = true;
      }

      this.hashChunk.scanChunks(world, value, value2, chunkLimit, 45000L, ChunkScanScheduler.getFpsScaledBudgetNanos(700), this::hashExcavationChunk);
      if (++this.scanTickCounter % 10 == 0) {
         int nowMsOffset = chunkLimit + 4;
         this.naturalCachedMap.keySet().removeIf(maxDist -> {
            int yinclusiveValue = (int)(maxDist & 4294967295L);
            int yinclusiveValue2 = (int)(maxDist >> 32);
            return Math.abs(yinclusiveValue - value) > nowMsOffset || Math.abs(yinclusiveValue2 - value2) > nowMsOffset;
         });
         this.hashChunk.removeFarChunks(value, value2, nowMsOffset);
         if (this.scanTickCounter % 600 == 0) {
            this.excavationHashStateBlockFlag.purgeDistantAndStale(timeMillis, value, value2, chunkLimit + 24);
         }

         BlockSensor.Job job = this.job;
         if (job != null) {
            this.job = null;
            this.markersThreadActive = false;
            if (job.yinclusiveValue == this.markersThreadCount && job.markersThreadMaxCraftedList != null) {
               this.scoreFortressJob(job, scoreFortressValue);
               this.lastJobTimeMs = job.createdTimeMs;
            }
         }

         if (timeMillis - this.timeMillis >= 20000L) {
            this.excavationHashActive = true;
         }

         if (this.excavationHashActive && !this.markersThreadActive && timeMillis - this.timeMillis >= 2000L) {
            this.excavationHashActive = false;
            this.timeMillis = timeMillis;
            this.updateMarkerThread();
         }
      }
   }

   public void refreshThreadExecutor() {
      this.threadExecutor.shutdownNow();
   }

   public int getMarkerThreadCount() {
      return this.hashClearCount;
   }

   boolean isMaxCraftedUsable(ExcavationCluster maxCrafted) {
      if (this.world == null) {
         return true;
      } else {
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();

         for (CraftedExcavation craftedExcavation : maxCrafted.getCells()) {
            if (craftedExcavation.excavationCraftedType == ExcavationCraftedType.STORAGE
               || craftedExcavation.excavationCraftedType == ExcavationCraftedType.UTILITY
               || craftedExcavation.excavationCraftedType == ExcavationCraftedType.SPAWNER) {
               int topY = this.world.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, craftedExcavation.posX, craftedExcavation.posZ);
               if (topY <= this.bottomY) {
                  topY = this.world.getTopY(Type.MOTION_BLOCKING, craftedExcavation.posX, craftedExcavation.posZ);
               }

               arrayList.add(craftedExcavation.posY);
               arrayList2.add(topY - craftedExcavation.posY);
            }
         }

         return arrayList.isEmpty() ? false : sumIntegers(arrayList) <= 0 && sumIntegers(arrayList2) >= 4;
      }
   }

   private ExcavationTypeStats getNaturalCounts(ExcavationCluster maxCrafted, BlockSensor.Job job2) {
      int naturalValue = maxCrafted.getMinX() - 8;
      int naturalValue2 = maxCrafted.getMinY() - 8;
      int naturalValue3 = maxCrafted.getMinZ() - 8;
      int naturalLimit = maxCrafted.getMaxX() + 8;
      int naturalLimit2 = maxCrafted.getMaxY() + 8;
      int naturalLimit3 = maxCrafted.getMaxZ() + 8;
      ArrayList arrayList = new ArrayList();
      int naturalTotal = 0;

      for (int i = naturalValue >> 4; i <= naturalLimit >> 4; i++) {
         for (int j = naturalValue3 >> 4; j <= naturalLimit3 >> 4; j++) {
            BlockSensor.Cached cached = this.naturalCachedMap.get(SiteBook.getTime(i, j));
            if (cached != null) {
               for (CraftedExcavation craftedExcavation : cached.cells) {
                  if (craftedExcavation.posX >= naturalValue
                     && craftedExcavation.posX <= naturalLimit
                     && craftedExcavation.posY >= naturalValue2
                     && craftedExcavation.posY <= naturalLimit2
                     && craftedExcavation.posZ >= naturalValue3
                     && craftedExcavation.posZ <= naturalLimit3) {
                     arrayList.add(craftedExcavation);
                  }
               }

               naturalTotal += cached.natural;
            }
         }
      }

      ExcavationTypeStats naturalCounts = ExcavationTypeStats.fromExcavations(arrayList, job2.scanPassIndex);

      for (int k = naturalValue >> 4; k <= naturalLimit >> 4; k++) {
         for (int m = naturalValue3 >> 4; m <= naturalLimit3 >> 4; m++) {
            BlockSensor.Cached cached2 = this.naturalCachedMap.get(SiteBook.getTime(k, m));
            if (cached2 != null) {
               naturalCounts.addCountedTypes(cached2.counts);
            }
         }
      }

      maxCrafted.addCraftedSum(naturalTotal);

      for (SensorBlock sensorBlock : job2.naturalItems) {
         if (sensorBlock.intersectsBoxWithSingleRange(maxCrafted.getMinX(), maxCrafted.getMinY(), maxCrafted.getMinZ(), maxCrafted.getMaxX(), maxCrafted.getMaxY(), maxCrafted.getMaxZ())) {
            naturalCounts.incrementTypeCount(sensorBlock.typeId);
         } else if (sensorBlock.intersectsBoxWithRanges(maxCrafted.getMinX(), maxCrafted.getMinY(), maxCrafted.getMinZ(), maxCrafted.getMaxX(), maxCrafted.getMaxY(), maxCrafted.getMaxZ())) {
            naturalCounts.markTypeSeen(sensorBlock.typeId);
         }
      }

      this.scanEntities(maxCrafted, naturalCounts);
      return naturalCounts;
   }

   public boolean isScanDue(long blockSensorValue) {
      return !this.excavationHashActive && !this.markersThreadActive || this.lastJobTimeMs >= blockSensorValue;
   }

   static boolean isWithinLimits(int blockSensorLimit, int blockSensorLimit2) {
      return blockSensorLimit2 > 0 && blockSensorLimit > 0.6 * blockSensorLimit2;
   }

   private void hashExcavationChunk(WorldChunk worldChunk, long excavationHashValue) {
      try {
         BlockStateUtilsWorld.ChunkScan biomeCrafted = BlockStateUtilsWorld.getBiomeCrafted(
            worldChunk, this.world, this.forestDark, this.bottomY, this.topYinclusive, this.excavationHashStateBlockFlag.getChunkEntries(excavationHashValue), false, 0L, 0L
         );
         ArrayList arrayList = new ArrayList(biomeCrafted.craftedExcavations.size());

         for (CraftedExcavation craftedExcavation : biomeCrafted.craftedExcavations) {
            if (craftedExcavation.craftedTypeId != 146 && craftedExcavation.excavationCraftedType != ExcavationCraftedType.EXCAVATION && (!biomeCrafted.looksLikeBase || craftedExcavation.penDivValue != 0 || !craftedExcavation.hasFlag(64))) {
               arrayList.add(craftedExcavation);
            }
         }

         for (StateBlockFlag.Entry entry : biomeCrafted.craftedStateEntries) {
            this.excavationHashStateBlockFlag.refreshExistingEntry(entry.value, entry.value2, entry.value3, null, this.timeMillisYinclusive);
         }

         for (StateBlockFlag.Entry entry2 : biomeCrafted.otherStateEntries) {
            this.excavationHashStateBlockFlag.removeEntryAt(entry2.value, entry2.value2, entry2.value3);
         }

         if (arrayList.isEmpty() && biomeCrafted.scannedCount == 0 && !biomeCrafted.flagsHandle.hasAnyTrackedType()) {
            if (this.naturalCachedMap.remove(excavationHashValue) != null) {
               this.excavationHashActive = true;
            }
         } else {
            long scanSignature = computeScanSignature(arrayList, biomeCrafted);
            BlockSensor.Cached put = this.naturalCachedMap.put(excavationHashValue, new BlockSensor.Cached(arrayList, biomeCrafted.looksLikeBase ? 0 : biomeCrafted.scannedCount, biomeCrafted.flagsHandle, biomeCrafted.sensorBlocks, scanSignature));
            if (put == null || put.signature != scanSignature) {
               this.excavationHashActive = true;
            }
         }

         if (this.longOpenHashSet.size() >= 65536) {
            this.longOpenHashSet.clear();
         }

         if (this.longOpenHashSet.add(excavationHashValue)) {
            this.hashClearCount++;
         }
      } catch (Throwable e) {
      }
   }

   private record Cached(List<CraftedExcavation> cells, int natural, ExcavationTypeStats counts, List<SensorBlock> markers, long signature) {
   }

   private static final class Job {
      final long createdTimeMs;
      final int scanPassIndex;
      final List<SensorBlock> naturalItems;
      final Set<Long> scoreFortressPositionSet = new HashSet<>();
      final List<CraftedExcavation> jobCraftedExcavationList;
      volatile List<ExcavationCluster> markersThreadMaxCraftedList;
      final List<CraftedExcavation> markerCraftedExcavations = new ArrayList<>();
      final int yinclusiveValue;

      Job(int value, int value2, long time, List<CraftedExcavation> items, List<SensorBlock> items2) {
         this.yinclusiveValue = value;
         this.scanPassIndex = value2;
         this.createdTimeMs = time;
         this.jobCraftedExcavationList = items;
         this.naturalItems = items2;
      }
   }
}
