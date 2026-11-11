package com.corz.client.categories.donutsmp;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.utils.BlockStateUtilsWorld.ChunkScan;
import com.corz.client.utils.StateBlockFlag.Entry;
import com.corz.client.utils.DivPenUtilsPenalized.Result;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.TickProgress;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Load;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.vehicle.AbstractChestBoatEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import com.corz.client.utils.BlockStateUtilsWorld;
import com.corz.client.utils.StateBlockFlag;
import com.corz.client.utils.ChunkFinderUtils;
import com.corz.client.utils.DivPenUtilsConst;
import com.corz.client.utils.DivPenUtilsFlag;
import com.corz.client.utils.DivPenUtilsPenalized;
import com.corz.client.utils.EvidenceDug;
import com.corz.client.utils.PenDivUtils;
import ai.auth.Module;
import com.corz.client.utils.ChunkScanScheduler;
import com.corz.client.utils.BiomeCategoryMapper;
import com.corz.client.utils.ExcavationCluster;
import com.corz.client.utils.CityMonument;
import com.corz.client.utils.ExcavationCraftedType;
import com.corz.client.utils.CraftedExcavation;
import com.corz.client.utils.ExcavationClusterer;
import com.corz.client.utils.ChunkLightSnapshot;
import com.corz.client.utils.LightPacketCache;
import com.corz.client.utils.SectionStatus;
import com.corz.client.utils.PingSoundPlayer;
import com.corz.client.utils.BrickStoneUtils;
import com.corz.client.utils.SpawnerSculkUtils;
import com.corz.client.utils.SensorBlock;
import com.corz.client.utils.ExcavationTypeStats;
import com.corz.client.utils.ConstChunkUtils;
import com.corz.client.utils.TimesSensorUtils;
import com.corz.client.utils.IconsPngType;

public class ChunkFinder extends Module {
   private int lastRebuildKey;
   private int scannedChunkCount;
   private final ConcurrentHashMap<ChunkPos, ChunkFinder.ChunkScanResult> chunkScanResults;
   private final ChunkScanScheduler chunkScanTracker;
   private final Set<Long> activeChunkKeys;
   private final BooleanSetting storageSetting;
   private final BooleanSetting trialChambers;
   private final BiomeCategoryMapper forestDark;
   private volatile boolean rebuildInFlight;
   private final ChunkScanScheduler chunkLoadQueue;
   private int scanMinHeight;
   private int scannedSectionTotal;
   private long lastStateCleanupMs;
   private volatile List<EvidenceDug> allEvidenceList;
   private final BooleanSetting notifications;
   private long totalScanTimeNanos;
   private final ConcurrentHashMap<Long, ChunkFinder.CachedChunk> chunkCache;
   private int zeroedSectionChunkCount;
   private volatile List<ChunkFinder.BaseCluster> playerBaseClusterList;
   private int tickCounter;
   private boolean rebuildPending;
   public static volatile ChunkFinder singleton;
   private long lastRebuildSubmitMs;
   private int unusedChunkValue;
   private final BooleanSetting redstoneSetting;
   private int scanSkippedTotal;
   private final BooleanSetting beams;
   private int scanMaxHeight;
   private final BooleanSetting spawners;
   private int totalExcavationCount;
   private int lastStrongSoundValue;
   private int blockChangeCount;
   private volatile ChunkFinder.RebuildJob rebuildJob;
   private final BooleanSetting soundPing;
   private final BooleanSetting labels;
   private boolean chunkResultsDirty;
   private volatile String currentDimensionId;
   private long lastBlockChangeCheckMs;
   private final IntegerSetting sensitivity = new IntegerSetting(
      "Sensitivity", "How easily something gets flagged. Higher = more finds (and more false alarms). 5 is the tuned default.", 5, 1, 10
   );
   private final ConcurrentHashMap<ChunkPos, Long> chunkNotifyTimes;
   public static final String VERSION_TAG = "chunkfinder-v5-budgeted-2026-10-04";
   private volatile List<EvidenceDug> nearbyEvidenceList;
   private volatile int resetGeneration;
   private final IntegerSetting scanRadius = new IntegerSetting("Scan Radius", "How many chunks out from you to scan.", 8, 1, 32);
   private final StateBlockFlag stateBlockTracker;
   private int worldIdentityHash;
   private final BooleanSetting tracers;
   private final ExecutorService threadExecutor;
   private final BooleanSetting farms;
   private final BooleanSetting playerBases = new BooleanSetting(
      "Player Bases", "Confirmed player builds: stashes, shulkers, beacons, enchant/brew stations, beds", true
   );
   private int normalSectionChunkCount;
   private final ConcurrentHashMap<Long, Long> baseNotifyTimes;

   private void renderClusterOutline(Matrix4f matrix4f, ChunkFinder.BaseCluster baseCluster, boolean confidenceHighFlag) {
      boolean chunkFinderFlag = baseCluster.confidence == ChunkFinder.Confidence.HIGH;
      double confidenceHighValue = baseCluster.bestConfidenceScore + 1.02;
      float deltaY = VertexBuilderUtils.getAmount(confidenceHighValue);
      int releaseRightValue = MatrixEspUtils.releaseRightValue;
      int playerValue = this.withAlpha(releaseRightValue, chunkFinderFlag ? 1.0F : 0.85F);
      int withAlpha4 = this.withAlpha(-16777216, chunkFinderFlag ? 0.6F : 0.5F);
      int withAlpha5 = this.withAlpha(releaseRightValue, chunkFinderFlag ? 0.2F : 0.14F);
      int withAlpha6 = this.withAlpha(releaseRightValue, 0.0F);
      float confidenceHighValue7 = deltaY + (chunkFinderFlag ? 4.0F : 3.0F);
      int[] copyOf = baseCluster.edgeSegments;

      for (int i = 0; i + 3 < copyOf.length; i += 4) {
         if (!VertexBuilderUtils.canVector((copyOf[i] + copyOf[i + 2]) * 0.5, confidenceHighValue, (copyOf[i + 1] + copyOf[i + 3]) * 0.5, 12.0F)) {
            this.renderClusterWall(
               matrix4f,
               VertexBuilderUtils.getAmountX(copyOf[i]),
               VertexBuilderUtils.getAmountForDouble(copyOf[i + 1]),
               VertexBuilderUtils.getAmountX(copyOf[i + 2]),
               VertexBuilderUtils.getAmountForDouble(copyOf[i + 3]),
               deltaY,
               confidenceHighValue7,
               playerValue,
               withAlpha4,
               withAlpha5,
               withAlpha6
            );
         }
      }

      if (confidenceHighFlag) {
         this.renderBeam(matrix4f, VertexBuilderUtils.getAmountX(baseCluster.centerX), deltaY, VertexBuilderUtils.getAmountForDouble(baseCluster.centerZ), releaseRightValue, chunkFinderFlag);
      }
   }

   public static void onBlockStateChanged(World world, BlockPos pos2, BlockState state, BlockState blockState) {
      ChunkFinder singleton2 = singleton;
      if (singleton2 != null && singleton2.isEnabled()) {
         singleton2.recordBlockChange(pos2, state, blockState);
      }
   }

   private void renderClusterWall(
      Matrix4f matrix4f, float vertexColorValue, float amountForDouble, float amountX, float vertexColorValue4, float deltaY, float vertexColorValue6, int playerValue, int vertexColorValue8, int vertexColorValue9, int vertexColorValue10
   ) {
      VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
      buffer.vertex(matrix4f, vertexColorValue, deltaY, amountForDouble).color(vertexColorValue9);
      buffer.vertex(matrix4f, amountX, deltaY, vertexColorValue4).color(vertexColorValue9);
      buffer.vertex(matrix4f, amountX, vertexColorValue6, vertexColorValue4).color(vertexColorValue10);
      buffer.vertex(matrix4f, vertexColorValue, vertexColorValue6, amountForDouble).color(vertexColorValue10);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix4f, vertexColorValue, deltaY, amountForDouble, amountX, deltaY, vertexColorValue4, vertexColorValue8, 4.5F);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix4f, vertexColorValue, deltaY, amountForDouble, amountX, deltaY, vertexColorValue4, playerValue, 2.5F);
   }

   private void renderBaseLabel(MatrixStack matrices, EvidenceDug evidenceDug, float x, float y) {
      VertexTextureFont vertexTexture = CorzClient.fontManager.getLargeFont();
      boolean playerFlag = evidenceDug.high >= 75;
      String playerText = "Player Base " + (evidenceDug.isEvidenceRemembered() ? "~" : "") + evidenceDug.high + "%";
      double playerFactor = evidenceDug.basePlayerAmount - mc.player.getX();
      double playerFactor2 = evidenceDug.finderBaseAmount - mc.player.getZ();
      String corzfontText = (int)Math.round(Math.sqrt(playerFactor * playerFactor + playerFactor2 * playerFactor2)) + "m";
      float playerValue = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown();
      float playerValue2 = playerValue * 0.42F;
      float playerValue3 = 4.0F;
      float max4 = Math.max(vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(playerText), vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText));
      float playerValue5 = 6.0F;
      float playerValue6 = 4.0F;
      float playerValue7 = 1.5F;
      float playerValue8 = playerValue2 + playerValue3 + max4 + playerValue5 * 2.0F;
      float playerValue9 = playerValue * 2.0F + playerValue7 + playerValue6 * 2.0F;
      float x2 = 0.55F;
      matrices.push();
      matrices.translate(x, y, 0.0F);
      matrices.scale(x2, x2, 1.0F);
      matrices.translate(-x, -y, 0.0F);
      float playerValue10 = x - playerValue8 / 2.0F;
      float playerValue11 = y - playerValue9;
      int playerValue12 = -16184564;
      Color color = new Color(playerValue12 >> 16 & 0xFF, playerValue12 >> 8 & 0xFF, playerValue12 & 0xFF, 215);
      int releaseRightValue = MatrixEspUtils.releaseRightValue;
      int red = releaseRightValue >> 16 & 0xFF;
      int green = releaseRightValue >> 8 & 0xFF;
      int blue = releaseRightValue & 0xFF;
      float vertexConsumerValue = playerValue10 + playerValue5;
      float f12 = playerValue11 + playerValue6 + (playerValue - playerValue2) / 2.0F;
      VertexConsumerUtilsVector.update4();

      try {
         VertexConsumerUtilsVector.handle(matrices, color, playerValue10, playerValue11, playerValue10 + playerValue8, playerValue11 + playerValue9, 2.5, 4.0);
         VertexConsumerUtilsVector.handle(matrices, new Color(red, green, blue, playerFlag ? 255 : 150), vertexConsumerValue, f12, vertexConsumerValue + playerValue2, f12 + playerValue2, playerValue2 / 2.0F, 6.0);
      } finally {
         VertexConsumerUtilsVector.update3();
      }

      float vertexTextureValue = playerValue10 + playerValue5 + playerValue2 + playerValue3;
      vertexTexture.handleForStringFloat(playerText, vertexTextureValue, playerValue11 + playerValue6, new Color(-1));
      vertexTexture.handleForStringFloat(corzfontText, vertexTextureValue, playerValue11 + playerValue6 + playerValue + playerValue7, new Color(-8421505));
      if (playerFlag) {
         VertexConsumerUtilsVector.handle(
            matrices, new Color(red, green, blue, 220), vertexTextureValue, playerValue11 + playerValue9 - playerValue6 + 1.0F, playerValue10 + playerValue8 - playerValue5, playerValue11 + playerValue9 - playerValue6 + 2.0F, 0.5, 2.0
         );
      }

      matrices.pop();
   }

   private EvidenceDug buildEvidence(ExcavationCluster maxCrafted, CityMonument cityMonument, PenDivUtils.Decision decision, String penalizedTrialText, long penalizedTrialValue, boolean penalizedTrialFlag, int percentShare) {
      var arrayList = new ArrayList<>(maxCrafted.getChunkKeys());
      String penalizedTrialText2 = decision.penalizedTrialType == Result.PENALIZED ? "trial:penalized" : cityMonument.getStructureName();
      int penalizedTrialValue3 = DivPenUtilsConst.getValue(maxCrafted);
      return new EvidenceDug(
         maxCrafted.getClusterKey(),
         penalizedTrialText,
         arrayList,
         maxCrafted.getMinX(),
         maxCrafted.getMinY(),
         maxCrafted.getMinZ(),
         maxCrafted.getMaxX(),
         maxCrafted.getMaxY(),
         maxCrafted.getMaxZ(),
         decision.penalizedValue,
         decision.penalizedTrialValue,
         maxCrafted.getCountForCategory(ExcavationCraftedType.STORAGE),
         maxCrafted.getCountForCategory(ExcavationCraftedType.UTILITY),
         maxCrafted.getCountForCategory(ExcavationCraftedType.LIGHTING),
         maxCrafted.getCountForCategory(ExcavationCraftedType.REDSTONE),
         maxCrafted.getCountForCategory(ExcavationCraftedType.CRAFTED),
         maxCrafted.getCountForCategory(ExcavationCraftedType.FARM),
         maxCrafted.getCountForCategory(ExcavationCraftedType.EXCAVATION),
         penalizedTrialValue3,
         maxCrafted.getDistinctPenDivCount(),
         maxCrafted.getHopperCount(),
         percentShare,
         penalizedTrialFlag,
         decision.penalizedTrialText,
         penalizedTrialText2,
         penalizedTrialValue
      );
   }

   private void scanChunkEvidence(WorldChunk worldChunk, long concurrentValue) {
      try {
         ChunkPos pos = worldChunk.getPos();
         long currentMillis = System.currentTimeMillis();
         long sectionZeroed = this.getZeroedSectionMask(pos.x, pos.z);
         long sectionStatuss = this.getNormalSectionMask(pos.x, pos.z);
         ChunkScan biomeCrafted = BlockStateUtilsWorld.getBiomeCrafted(
            worldChunk, mc.world, this.forestDark, this.scanMinHeight, this.scanMaxHeight, this.stateBlockTracker.getChunkEntries(concurrentValue), true, sectionZeroed, sectionStatuss
         );
         long maxFlag = computeScanHash(biomeCrafted);
         if (biomeCrafted.craftedExcavations.isEmpty() && biomeCrafted.scannedCount == 0 && !biomeCrafted.flagsHandle.hasAnyTrackedType()) {
            if (this.chunkCache.remove(concurrentValue) != null) {
               this.rebuildPending = true;
            }
         } else {
            ChunkFinder.CachedChunk put = this.chunkCache
               .put(
                  concurrentValue,
                  new ChunkFinder.CachedChunk(biomeCrafted.craftedExcavations, biomeCrafted.scannedCount, biomeCrafted.flagsHandle, biomeCrafted.sensorBlocks, biomeCrafted.craftedBlockCount, biomeCrafted.candidateBlockCount, biomeCrafted.flaggedBlockCount, biomeCrafted.looksLikeBase, maxFlag)
               );
            if (put == null || put.cachedAtStamp != maxFlag) {
               this.rebuildPending = true;
            }
         }

         for (Entry entry : biomeCrafted.craftedStateEntries) {
            this.stateBlockTracker.refreshExistingEntry(entry.value, entry.value2, entry.value3, null, currentMillis);
         }

         for (Entry entry2 : biomeCrafted.otherStateEntries) {
            this.stateBlockTracker.removeEntryAt(entry2.value, entry2.value2, entry2.value3);
         }

         if (biomeCrafted.looksLikeBase) {
            if (this.activeChunkKeys.add(concurrentValue)) {
               this.rebuildPending = true;
            }
         } else if (this.activeChunkKeys.remove(concurrentValue)) {
            this.rebuildPending = true;
         }

         this.scannedChunkCount++;
         this.scannedSectionTotal = this.scannedSectionTotal + biomeCrafted.scannedSectionCount;
         this.scanSkippedTotal = this.scanSkippedTotal + biomeCrafted.skippedSectionCount;
         this.totalExcavationCount = this.totalExcavationCount + biomeCrafted.craftedExcavations.size();
         this.totalScanTimeNanos = this.totalScanTimeNanos + biomeCrafted.scanTimeNanos;
         if (sectionZeroed != 0L) {
            this.zeroedSectionChunkCount++;
         }

         if (sectionStatuss != 0L) {
            this.normalSectionChunkCount++;
         }
      } catch (Throwable e) {
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null) {
         this.renderEvidenceEsp();
      }
   }

   @Override
   protected void onDisable() {
      this.chunkScanResults.clear();
      this.chunkNotifyTimes.clear();
      this.chunkScanTracker.clearScannedChunks();
      this.chunkResultsDirty = false;
      this.lastStrongSoundValue = -1;
      this.playerBaseClusterList = new ArrayList<>();
      this.resetState();
   }

   private void submitRebuildJob(String threadText) {
      DivPenUtilsFlag utils = this.buildDetectionSettings();
      var arrayList = new ArrayList();
      var arrayList2 = new ArrayList();
      boolean threadFlag = utils.threadActive && this.stateBlockTracker.shouldAlert();

      for (java.util.Map.Entry entry : this.chunkCache.entrySet()) {
         ChunkFinder.CachedChunk cachedChunk = (ChunkFinder.CachedChunk)entry.getValue();
         boolean threadFlag2 = utils.threadActive && (threadFlag || this.activeChunkKeys.contains(entry.getKey()));

         for (CraftedExcavation craftedExcavation : cachedChunk.excavations) {
            if (ChunkFinderUtils.canFlag(craftedExcavation.posY) && (!threadFlag2 || !craftedExcavation.hasFlag(64))) {
               arrayList.add(craftedExcavation);
            }
         }

         arrayList2.addAll(cachedChunk.sensorBlocks);
      }

      if (utils.concurrentActive) {
         arrayList.addAll(this.getEntityMarkers());
      }

      ChunkFinder.RebuildJob rebuildJob = new ChunkFinder.RebuildJob(
         threadText, this.resetGeneration, utils, threadFlag, BlockStateUtilsWorld.getDimensionIndex(mc.world), arrayList2, arrayList
      );
      this.rebuildInFlight = true;
      this.threadExecutor.submit(() -> {
         try {
            rebuildJob.craftedGroups = ExcavationClusterer.clusterCells(rebuildJob.excavations, 5, 4);
         } catch (Throwable var3x) {
            rebuildJob.craftedGroups = new ArrayList<>();
         }

         this.rebuildJob = rebuildJob;
      });
   }

   private long getZeroedSectionMask(int sectionZeroedValue, int sectionZeroedValue2) {
      try {
         ChunkLightSnapshot chunkLightSnapshot = LightPacketCache.INSTANCE.sectionsCoordChunkLightSnaps.get(ChunkPos.toLong(sectionZeroedValue, sectionZeroedValue2));
         if (chunkLightSnapshot == null) {
            return 0L;
         } else {
            long sectionZeroed = 0L;

            for (int i = 1; i <= chunkLightSnapshot.verticalSections && i - 1 < 64; i++) {
               int sectionZeroedLimit = (chunkLightSnapshot.sectionCoord + (i - 1)) * 16;
               if (sectionZeroedLimit > -30) {
                  break;
               }

               if (ChunkFinderUtils.canFlagForInt(sectionZeroedLimit) && chunkLightSnapshot.blockLightStatuses[i] == SectionStatus.ZEROED_PRESENT) {
                  sectionZeroed |= 1L << i - 1;
               }
            }

            return sectionZeroed;
         }
      } catch (Throwable e) {
         return 0L;
      }
   }

   private void renderEvidenceBox(Matrix4f matrix4f, EvidenceDug evidenceDug, boolean releaseRightFlag) {
      int releaseRightValue = MatrixEspUtils.releaseRightValue;
      int withAlpha2 = this.withAlpha(releaseRightValue, releaseRightFlag ? 1.0F : 0.8F);
      int n2 = this.withAlpha(-16777216, 0.5F);
      float amountX = VertexBuilderUtils.getAmountX(evidenceDug.storageScore);
      float f2 = VertexBuilderUtils.getAmount(evidenceDug.releaseRightValue);
      float amountForDouble = VertexBuilderUtils.getAmountForDouble(evidenceDug.utilityScore);
      float vertexColorValue = VertexBuilderUtils.getAmountX(evidenceDug.evidenceDugValue + 1.0);
      float f5 = VertexBuilderUtils.getAmount(evidenceDug.finderValue + 1.0);
      float amountForDouble6 = VertexBuilderUtils.getAmountForDouble(evidenceDug.redstoneScore + 1.0);
      float releaseRightValue10 = (releaseRightValue >> 16 & 0xFF) / 255.0F;
      float releaseRightValue11 = (releaseRightValue >> 8 & 0xFF) / 255.0F;
      float releaseRightValue12 = (releaseRightValue & 0xFF) / 255.0F;
      VertexBuilderUtils.handle(matrix4f, amountX, f2, amountForDouble, vertexColorValue, f5, amountForDouble6, releaseRightValue10, releaseRightValue11, releaseRightValue12, releaseRightFlag ? 0.1F : 0.06F);
      VertexBuilderUtils.handle(matrix4f, amountX, f2, amountForDouble, vertexColorValue, f5, amountForDouble6, n2, 4.0F);
      VertexBuilderUtils.handle(matrix4f, amountX, f2, amountForDouble, vertexColorValue, f5, amountForDouble6, withAlpha2, 2.0F);
   }

   private static long computeScanHash(ChunkScan chunkScan) {
      long chunkFinderValue = 1125899906842597L;

      for (CraftedExcavation craftedExcavation : chunkScan.craftedExcavations) {
         long chunkFinderValue2 = 31L * chunkFinderValue + craftedExcavation.posX;
         long chunkFinderValue3 = 31L * chunkFinderValue2 + craftedExcavation.posY;
         long chunkFinderValue4 = 31L * chunkFinderValue3 + craftedExcavation.posZ;
         long chunkFinderValue5 = 31L * chunkFinderValue4 + craftedExcavation.craftedTypeId;
         long chunkFinderValue6 = 31L * chunkFinderValue5 + craftedExcavation.flagMask;
         chunkFinderValue = 31L * chunkFinderValue6 + craftedExcavation.craftedWeight;
      }

      long chunkFinderValue7 = 31L * chunkFinderValue + chunkScan.scannedCount;
      long chunkFinderValue8 = 31L * chunkFinderValue7 + chunkScan.sensorBlocks.size();
      long chunkFinderValue9 = 31L * chunkFinderValue8 + chunkScan.craftedBlockCount;
      long chunkFinderValue10 = 31L * chunkFinderValue9 + chunkScan.candidateBlockCount;
      long chunkFinderValue11 = 31L * chunkFinderValue10 + chunkScan.flaggedBlockCount;
      long chunkFinderValue12 = 31L * chunkFinderValue11 + chunkScan.sensorBlockCount;
      long maxFlag = 31L * chunkFinderValue12 + (chunkScan.looksLikeBase ? 1 : 0);

      for (int i = 1; i < 148; i++) {
         maxFlag = 31L * maxFlag + chunkScan.flagsHandle.getTypeCount(i);
      }

      return maxFlag;
   }

   private void sortClustersByDistance() {
      var playerBaseClusterList2 = this.playerBaseClusterList;
      if (playerBaseClusterList2.size() >= 2 && mc.player != null) {
         double x = mc.player.getX();
         double z = mc.player.getZ();
         var items = new ArrayList<ChunkFinder.BaseCluster>(playerBaseClusterList2);
         items.sort((baseCluster, baseCluster2) -> Double.compare(getClusterDistanceSq(baseCluster, x, z), getClusterDistanceSq(baseCluster2, x, z)));
         this.playerBaseClusterList = items;
      }
   }

   private ChunkFinder.ChunkScanResult analyzeChunk(WorldChunk worldChunk, ChunkPos chunkPos) {
      int mixedMediumTotal = 0;
      int mixedMediumTotal2 = 0;
      int mixedMediumCounter = 0;
      int mixedMediumTotal3 = 0;
      int mixedMediumTotal4 = 0;
      int mixedMediumCounter2 = 0;
      int mixedMediumCounter3 = 0;
      int mixedMediumCounter4 = 0;
      boolean mixedMediumFlag = false;
      boolean mixedMediumFlag2 = false;
      boolean mixedMediumFlag3 = false;
      boolean mixedMediumFlag4 = false;
      short mixedMediumBound = 320;
      int bottomY = mc.world.getBottomY();
      int intersectsCenterBound = mc.world.getDimension().height() + mc.world.getBottomY();
      int mixedMediumValue = 0;
      var arrayList = new ArrayList();
      int mixedMediumCounter5 = 0;
      int mixedMediumCounter6 = 0;
      int mixedMediumCounter7 = 0;
      int mixedMediumCounter8 = 0;
      int mixedMediumCounter9 = 0;
      int mixedMediumCounter10 = 0;
      int craftedExcavationLimit = 0;
      int mixedMediumCounter12 = 0;
      var arrayList2 = new ArrayList();
      var arrayList3 = new ArrayList();
      var arrayList4 = new ArrayList();
      var arrayList5 = new ArrayList();
      var arrayList6 = new ArrayList();

      for (BlockPos blockPos : worldChunk.getBlockEntityPositions()) {
         if (blockPos.getY() <= mixedMediumBound) {
            BlockEntity blockEntity = mc.world.getBlockEntity(blockPos);
            if (blockEntity != null) {
               if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof TrappedChestBlockEntity) {
                  mixedMediumTotal += 5;
                  mixedMediumCounter2++;
                  mixedMediumFlag = true;
                  arrayList5.add(blockPos);
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof BarrelBlockEntity) {
                  mixedMediumTotal += 4;
                  mixedMediumCounter2++;
                  mixedMediumFlag = true;
                  arrayList5.add(blockPos);
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof ShulkerBoxBlockEntity) {
                  mixedMediumTotal += 8;
                  mixedMediumCounter2++;
                  mixedMediumTotal4 += 4;
                  mixedMediumFlag = true;
                  arrayList5.add(blockPos);
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof EnderChestBlockEntity) {
                  mixedMediumTotal4 += 3;
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof HopperBlockEntity) {
                  mixedMediumTotal += 3;
                  mixedMediumTotal3 += 2;
                  mixedMediumFlag4 = true;
                  mixedMediumFlag = true;
                  mixedMediumCounter5++;
                  arrayList2.add(blockPos);
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof FurnaceBlockEntity || blockEntity instanceof BlastFurnaceBlockEntity || blockEntity instanceof SmokerBlockEntity) {
                  mixedMediumTotal += 3;
                  mixedMediumCounter2++;
                  mixedMediumFlag = true;
                  arrayList5.add(blockPos);
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof BeaconBlockEntity) {
                  mixedMediumTotal += 15;
                  mixedMediumCounter2++;
                  mixedMediumTotal4 += 5;
                  mixedMediumFlag = true;
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof EnchantingTableBlockEntity) {
                  mixedMediumTotal4 += 3;
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof BrewingStandBlockEntity) {
                  mixedMediumTotal += 2;
                  mixedMediumTotal4 += 2;
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof BedBlockEntity) {
                  mixedMediumTotal4 += 3;
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof LecternBlockEntity) {
                  mixedMediumTotal4++;
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof TrialSpawnerBlockEntity || blockEntity instanceof VaultBlockEntity) {
                  mixedMediumCounter12++;
                  mixedMediumTotal += 10;
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof MobSpawnerBlockEntity) {
                  craftedExcavationLimit++;
                  mixedMediumTotal += 4;
                  arrayList.add(blockPos);
               } else if (blockEntity instanceof DispenserBlockEntity) {
                  if (this.redstoneSetting.isEnabled()) {
                     mixedMediumTotal2 += 3;
                     mixedMediumTotal3 += 2;
                     mixedMediumFlag2 = true;
                     mixedMediumFlag4 = true;
                     mixedMediumCounter8++;
                     arrayList6.add(blockPos);
                     arrayList.add(blockPos);
                  }
               } else if (blockEntity instanceof DropperBlockEntity && this.redstoneSetting.isEnabled()) {
                  mixedMediumTotal2 += 3;
                  mixedMediumTotal3 += 2;
                  mixedMediumFlag2 = true;
                  mixedMediumFlag4 = true;
                  mixedMediumCounter8++;
                  arrayList6.add(blockPos);
                  arrayList.add(blockPos);
               }
            }
         }
      }

      int startX = chunkPos.getStartX();
      int startZ = chunkPos.getStartZ();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            int sampleHeightmapOffset = worldChunk.sampleHeightmap(Type.MOTION_BLOCKING, i, j) + 1;
            if (sampleHeightmapOffset > mixedMediumValue) {
               mixedMediumValue = sampleHeightmapOffset;
            }
         }
      }

      boolean booleanValue = this.redstoneSetting.isEnabled();
      boolean enabled = this.farms.isEnabled();
      if (booleanValue || enabled) {
         int mixedMediumLimit = Math.min(intersectsCenterBound, mixedMediumBound);
         ChunkSection[] sectionArray = worldChunk.getSectionArray();
         int bottomY2 = worldChunk.getBottomY();

         for (int k = 0; k < sectionArray.length; k++) {
            int mixedMediumValue2 = bottomY2 + (k << 4);
            if (mixedMediumValue2 < mixedMediumLimit && mixedMediumValue2 + 15 >= bottomY) {
               ChunkSection chunkSection = sectionArray[k];
               if (chunkSection != null && !chunkSection.isEmpty() && chunkSection.hasAny(scanFarms -> isScanRelevantBlock(scanFarms.getBlock(), booleanValue, enabled))) {
                  for (int m = 0; m < 16; m++) {
                     int y = mixedMediumValue2 + m;
                     if (y >= bottomY && y < mixedMediumLimit) {
                        for (int p = 0; p < 16; p++) {
                           for (int q = 0; q < 16; q++) {
                              Block block = chunkSection.getBlockState(p, m, q).getBlock();
                              if (isScanRelevantBlock(block, booleanValue, enabled)) {
                                 int x = startX + p;
                                 int z = startZ + q;
                                 if (booleanValue) {
                                    if (block == Blocks.STICKY_PISTON) {
                                       BlockPos blockPos2 = new BlockPos(x, y, z);
                                       mixedMediumTotal2 += 2;
                                       mixedMediumTotal3 += 3;
                                       mixedMediumFlag2 = true;
                                       mixedMediumFlag4 = true;
                                       mixedMediumCounter7++;
                                       arrayList4.add(blockPos2);
                                       arrayList6.add(blockPos2);
                                       arrayList.add(blockPos2);
                                    } else if (block instanceof PistonBlock) {
                                       BlockPos totalPriceData = new BlockPos(x, y, z);
                                       mixedMediumTotal2 += 2;
                                       mixedMediumTotal3 += 2;
                                       mixedMediumFlag2 = true;
                                       mixedMediumFlag4 = true;
                                       mixedMediumCounter7++;
                                       arrayList4.add(totalPriceData);
                                       arrayList6.add(totalPriceData);
                                       arrayList.add(totalPriceData);
                                    } else if (block instanceof ObserverBlock) {
                                       BlockPos blockPos4 = new BlockPos(x, y, z);
                                       mixedMediumTotal2 += 2;
                                       mixedMediumTotal3 += 3;
                                       mixedMediumFlag2 = true;
                                       mixedMediumFlag4 = true;
                                       mixedMediumCounter6++;
                                       arrayList3.add(blockPos4);
                                       arrayList6.add(blockPos4);
                                       arrayList.add(blockPos4);
                                    } else if (block instanceof RedstoneWireBlock) {
                                       mixedMediumTotal2++;
                                       mixedMediumCounter3++;
                                       mixedMediumFlag2 = true;
                                       arrayList6.add(new BlockPos(x, y, z));
                                    } else if (block instanceof RedstoneTorchBlock) {
                                       mixedMediumTotal2++;
                                       mixedMediumCounter3++;
                                       mixedMediumFlag2 = true;
                                       arrayList6.add(new BlockPos(x, y, z));
                                    } else if (block instanceof RepeaterBlock) {
                                       BlockPos blockPos5 = new BlockPos(x, y, z);
                                       mixedMediumTotal2 += 2;
                                       mixedMediumCounter3++;
                                       mixedMediumFlag2 = true;
                                       arrayList6.add(blockPos5);
                                       arrayList.add(blockPos5);
                                    } else if (block instanceof ComparatorBlock) {
                                       BlockPos blockPos6 = new BlockPos(x, y, z);
                                       mixedMediumTotal2 += 2;
                                       mixedMediumCounter3++;
                                       mixedMediumFlag2 = true;
                                       arrayList6.add(blockPos6);
                                       arrayList.add(blockPos6);
                                    }
                                 }

                                 if (enabled) {
                                    if (block == Blocks.KELP || block == Blocks.KELP_PLANT) {
                                       mixedMediumCounter++;
                                       mixedMediumCounter4++;
                                    } else if (block == Blocks.SEA_LANTERN || block == Blocks.LANTERN) {
                                       mixedMediumCounter++;
                                       mixedMediumCounter4++;
                                    } else if (block == Blocks.SUGAR_CANE) {
                                       mixedMediumCounter++;
                                       mixedMediumCounter4++;
                                    } else if (block == Blocks.BAMBOO || block == Blocks.BAMBOO_SAPLING) {
                                       mixedMediumCounter++;
                                       mixedMediumCounter4++;
                                    } else if (block == Blocks.CACTUS) {
                                       mixedMediumCounter++;
                                       mixedMediumCounter4++;
                                    } else if (block == Blocks.WATER) {
                                       mixedMediumCounter9++;
                                    } else if (block == Blocks.WHEAT
                                       || block == Blocks.POTATOES
                                       || block == Blocks.CARROTS
                                       || block == Blocks.BEETROOTS
                                       || block == Blocks.MELON
                                       || block == Blocks.PUMPKIN
                                       || block == Blocks.NETHER_WART) {
                                       mixedMediumCounter10++;
                                       mixedMediumCounter++;
                                       mixedMediumCounter4++;
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

      if (mixedMediumCounter4 >= 15 || mixedMediumCounter10 >= 10) {
         mixedMediumFlag3 = true;
      }

      if (mixedMediumCounter4 > 40) {
         mixedMediumCounter += 15;
      } else if (mixedMediumCounter4 > 20) {
         mixedMediumCounter += 8;
      } else if (mixedMediumCounter4 > 10) {
         mixedMediumCounter += 4;
      }

      if (mixedMediumCounter10 > 30) {
         mixedMediumCounter += 10;
      }

      if (mixedMediumCounter9 > 50) {
         mixedMediumCounter += 10;
         mixedMediumTotal3 += 5;
         if (mixedMediumCounter4 >= 5) {
            mixedMediumFlag3 = true;
         }
      }

      if (mixedMediumCounter5 >= 3) {
         int countClosePairs3 = this.countClosePairs(arrayList2);
         mixedMediumTotal3 += countClosePairs3;
         mixedMediumTotal += countClosePairs3 / 2;
      }

      if (mixedMediumCounter6 >= 1 && mixedMediumCounter7 >= 1) {
         int manhattanDistance = this.countNearbyPositions(arrayList3, arrayList4, 3);
         mixedMediumTotal3 += manhattanDistance * 5;
      }

      if (mixedMediumCounter8 >= 1 && mixedMediumCounter3 >= 2) {
         mixedMediumTotal3 += mixedMediumCounter8 * 3;
      }

      int countNearbyPositions2 = 0;
      if (!arrayList5.isEmpty() && !arrayList6.isEmpty()) {
         countNearbyPositions2 = this.countNearbyPositions(arrayList5, arrayList6, 6);
         if (countNearbyPositions2 > 0) {
            mixedMediumTotal3 += countNearbyPositions2 * 4;
         }
      }

      int densestNeighborCount = this.getDensestNeighborCount(arrayList, 8);
      int mixedMediumValue4 = densestNeighborCount * 2;
      int mixedMediumLimit3 = mixedMediumTotal + mixedMediumTotal2 + mixedMediumCounter + mixedMediumTotal3 + mixedMediumValue4;
      int mixedMediumCounter13 = 0;
      if (mixedMediumFlag) {
         mixedMediumCounter13++;
      }

      if (mixedMediumFlag2) {
         mixedMediumCounter13++;
      }

      if (mixedMediumFlag3) {
         mixedMediumCounter13++;
      }

      if (mixedMediumFlag4) {
         mixedMediumCounter13++;
      }

      if (mixedMediumCounter2 <= 1 && !mixedMediumFlag2 && !mixedMediumFlag4 && !mixedMediumFlag3) {
         mixedMediumLimit3 = 0;
      }

      if (mixedMediumFlag2 && !mixedMediumFlag && !mixedMediumFlag4 && !mixedMediumFlag3 && mixedMediumCounter3 <= 3) {
         mixedMediumLimit3 = (int)(mixedMediumLimit3 * 0.2);
      }

      if (mixedMediumTotal4 >= 3 || mixedMediumCounter2 >= 8) {
         ChunkFinder.Confidence confidence = mixedMediumTotal4 < 6 && mixedMediumCounter2 < 14 ? ChunkFinder.Confidence.MEDIUM : ChunkFinder.Confidence.HIGH;
         ChunkFinder.BaseKind craftedExcavation = this.classifyStructureKind(worldChunk, bottomY, Math.min(intersectsCenterBound - 1, mixedMediumBound), craftedExcavationLimit, mixedMediumCounter12);
         return craftedExcavation != null
            ? new ChunkFinder.ChunkScanResult(
               chunkPos, mixedMediumLimit3 + mixedMediumTotal4 * 3, ChunkFinder.ChunkType.STORAGE, ChunkFinder.Confidence.MEDIUM, mixedMediumValue, mixedMediumCounter13, mixedMediumCounter2, craftedExcavationLimit, mixedMediumCounter12, mixedMediumTotal4, craftedExcavation
            )
            : new ChunkFinder.ChunkScanResult(chunkPos, mixedMediumLimit3 + mixedMediumTotal4 * 3, ChunkFinder.ChunkType.STORAGE, confidence, mixedMediumValue, mixedMediumCounter13, mixedMediumCounter2, craftedExcavationLimit, mixedMediumCounter12, mixedMediumTotal4);
      } else if (mixedMediumCounter12 > 0) {
         return new ChunkFinder.ChunkScanResult(
            chunkPos, mixedMediumLimit3 + 40, ChunkFinder.ChunkType.MIXED, ChunkFinder.Confidence.HIGH, mixedMediumValue, mixedMediumCounter13, mixedMediumCounter2, craftedExcavationLimit, mixedMediumCounter12, mixedMediumTotal4
         );
      } else if (craftedExcavationLimit > 0) {
         ChunkFinder.Confidence confidence2 = mixedMediumCounter2 > 0 ? ChunkFinder.Confidence.HIGH : ChunkFinder.Confidence.MEDIUM;
         return new ChunkFinder.ChunkScanResult(chunkPos, mixedMediumLimit3 + 20, ChunkFinder.ChunkType.STORAGE, confidence2, mixedMediumValue, mixedMediumCounter13, mixedMediumCounter2, craftedExcavationLimit, mixedMediumCounter12, mixedMediumTotal4);
      } else if (mixedMediumCounter13 >= 2) {
         boolean mixedMediumFlag5 = mixedMediumCounter6 >= 1 && mixedMediumCounter7 >= 1 || mixedMediumCounter5 >= 3 || countNearbyPositions2 >= 2 || mixedMediumCounter2 >= 3 && mixedMediumFlag2;
         ChunkFinder.Confidence medium3;
         if (mixedMediumCounter13 < 3 && (mixedMediumCounter13 < 2 || !mixedMediumFlag5 || mixedMediumLimit3 < this.getMinChunkScore() * 2)) {
            if (mixedMediumCounter13 >= 2 && mixedMediumLimit3 >= this.getMinChunkScore()) {
               medium3 = ChunkFinder.Confidence.MEDIUM;
            } else {
               medium3 = ChunkFinder.Confidence.LOW;
            }
         } else {
            medium3 = ChunkFinder.Confidence.HIGH;
         }

         ChunkFinder.ChunkType chunkType;
         if (mixedMediumFlag && mixedMediumFlag2) {
            chunkType = ChunkFinder.ChunkType.MIXED;
         } else if (mixedMediumFlag3 && mixedMediumFlag2) {
            chunkType = ChunkFinder.ChunkType.MIXED;
         } else if (mixedMediumFlag && mixedMediumFlag4) {
            chunkType = ChunkFinder.ChunkType.MIXED;
         } else if (mixedMediumFlag) {
            chunkType = ChunkFinder.ChunkType.STORAGE;
         } else if (mixedMediumFlag3) {
            chunkType = ChunkFinder.ChunkType.FARM;
         } else if (!mixedMediumFlag2 && !mixedMediumFlag4) {
            chunkType = ChunkFinder.ChunkType.MIXED;
         } else {
            chunkType = ChunkFinder.ChunkType.REDSTONE;
         }

         return new ChunkFinder.ChunkScanResult(chunkPos, mixedMediumLimit3, chunkType, medium3, mixedMediumValue, mixedMediumCounter13, mixedMediumCounter2, craftedExcavationLimit, mixedMediumCounter12, mixedMediumTotal4);
      } else if (this.getMinContainerCount() > 0 && mixedMediumCounter2 >= this.getMinContainerCount()) {
         ChunkFinder.Confidence confidence4 = mixedMediumCounter2 >= this.getMinContainerCount() * 2 ? ChunkFinder.Confidence.HIGH : ChunkFinder.Confidence.MEDIUM;
         return new ChunkFinder.ChunkScanResult(chunkPos, mixedMediumLimit3, ChunkFinder.ChunkType.STORAGE, confidence4, mixedMediumValue, mixedMediumCounter13, mixedMediumCounter2, craftedExcavationLimit, mixedMediumCounter12, mixedMediumTotal4);
      } else {
         return new ChunkFinder.ChunkScanResult(
            chunkPos, mixedMediumLimit3, ChunkFinder.ChunkType.MIXED, ChunkFinder.Confidence.LOW, mixedMediumValue, mixedMediumCounter13, mixedMediumCounter2, craftedExcavationLimit, mixedMediumCounter12, mixedMediumTotal4
         );
      }
   }

   private void notifyPlayerBase(EvidenceDug evidenceDug) {
      if (this.notifications.isEnabled()) {
         if (this.isEvidenceActive(evidenceDug)) {
            if (evidenceDug.high >= 75) {
               long currentMillis = System.currentTimeMillis();
               Long concurrenthashmap = this.baseNotifyTimes.get(evidenceDug.basePlayerTime);
               if (concurrenthashmap == null || currentMillis - concurrenthashmap >= 120000L) {
                  this.baseNotifyTimes.put(evidenceDug.basePlayerTime, currentMillis);
                  String format = String.format(
                     "Player Base %d%% at X:%d Y:%d Z:%d (%s)",
                     evidenceDug.high,
                     (int)evidenceDug.basePlayerAmount,
                     (int)evidenceDug.finderChunkAmount,
                     (int)evidenceDug.finderBaseAmount,
                     evidenceDug.formatEvidenceRemembered()
                  );
                  DisabledEnabled.getInstance().handleDisabledEnabled("ChunkFinder", format, true);
                  if (this.soundPing.isEnabled()) {
                     PingSoundPlayer.playPingSound();
                  }
               }
            }
         }
      }
   }

   private int getSensitivityStep() {
      return this.sensitivity.getValue() >= 8 ? 1 : 2;
   }

   private boolean isEvidenceActive(EvidenceDug evidenceDug) {
      return evidenceDug.evidenceDugActive;
   }

   private void renderTracerMarker(Matrix4f matrix4f, double releaseRightValue, double vertexBuilderValue, double releaseRightValue3, boolean releaseRightFlag) {
      float amountX = VertexBuilderUtils.getAmountX(releaseRightValue);
      float releaseRightValue5 = VertexBuilderUtils.getAmount(vertexBuilderValue);
      float amountForDouble = VertexBuilderUtils.getAmountForDouble(releaseRightValue3);
      float releaseRightValue7 = releaseRightFlag ? 0.6F : 0.45F;
      VertexBuilderUtils.handle(
         matrix4f,
         amountX - releaseRightValue7,
         releaseRightValue5 - releaseRightValue7,
         amountForDouble - releaseRightValue7,
         amountX + releaseRightValue7,
         releaseRightValue5 + releaseRightValue7,
         amountForDouble + releaseRightValue7,
         this.withAlpha(MatrixEspUtils.releaseRightValue, 1.0F),
         2.5F
      );
   }

   private int getBaseConfidenceThreshold() {
      return Math.max(50, Math.min(86, 70 + (5 - this.sensitivity.getValue()) * 4));
   }

   private int getDensestNeighborCount(List<BlockPos> list, int manhattanDistanceLimit) {
      if (list.size() < 3) {
         return 0;
      } else {
         int manhattanDistanceBound = 0;

         for (BlockPos blockPos : list) {
            int manhattanDistanceCounter = 0;

            for (BlockPos blockPos2 : list) {
               if (blockPos != blockPos2 && blockPos.getManhattanDistance(blockPos2) <= manhattanDistanceLimit) {
                  manhattanDistanceCounter++;
               }
            }

            if (manhattanDistanceCounter > manhattanDistanceBound) {
               manhattanDistanceBound = manhattanDistanceCounter;
            }
         }

         return Math.min(manhattanDistanceBound, 20);
      }
   }

   private void updateChunkScanResult(WorldChunk worldChunk, long strongSoundValue) {
      ChunkPos pos = worldChunk.getPos();

      try {
         ChunkFinder.ChunkScanResult mixedMedium = this.analyzeChunk(worldChunk, pos);
         if (mixedMedium != null && this.isChunkResultSignificant(mixedMedium)) {
            ChunkFinder.ChunkScanResult put = this.chunkScanResults.put(pos, mixedMedium);
            if (put == null) {
               this.chunkResultsDirty = true;
               this.notifyChunkFind(pos, mixedMedium);
            } else if (!put.isSameResult(mixedMedium)) {
               this.chunkResultsDirty = true;
            }
         } else if (this.chunkScanResults.remove(pos) != null) {
            this.chunkResultsDirty = true;
         }
      } catch (Throwable e) {
      }
   }

   private static double getClusterDistanceSq(ChunkFinder.BaseCluster baseCluster, double chunkFinderValue, double z) {
      double chunkFinderFactor = baseCluster.centerX - chunkFinderValue;
      double chunkFinderFactor2 = baseCluster.centerZ - z;
      return chunkFinderFactor * chunkFinderFactor + chunkFinderFactor2 * chunkFinderFactor2;
   }

   private static boolean isScanRelevantBlock(Block block, boolean kelpLanternFlag, boolean enabled) {
      return !kelpLanternFlag
            || !(block instanceof PistonBlock)
               && !(block instanceof ObserverBlock)
               && !(block instanceof RedstoneWireBlock)
               && !(block instanceof RedstoneTorchBlock)
               && !(block instanceof RepeaterBlock)
               && !(block instanceof ComparatorBlock)
         ? enabled
            && (
               block == Blocks.KELP
                  || block == Blocks.KELP_PLANT
                  || block == Blocks.SEA_LANTERN
                  || block == Blocks.LANTERN
                  || block == Blocks.SUGAR_CANE
                  || block == Blocks.BAMBOO
                  || block == Blocks.BAMBOO_SAPLING
                  || block == Blocks.CACTUS
                  || block == Blocks.WATER
                  || block == Blocks.WHEAT
                  || block == Blocks.POTATOES
                  || block == Blocks.CARROTS
                  || block == Blocks.BEETROOTS
                  || block == Blocks.MELON
                  || block == Blocks.PUMPKIN
                  || block == Blocks.NETHER_WART
            )
         : true;
   }

   private void renderLabels(TickProgress tickProgress) {
      if (this.labels.isEnabled()) {
         var nearbyEvidenceList3 = this.nearbyEvidenceList;
         if (!nearbyEvidenceList3.isEmpty() && System.identityHashCode(mc.world) == this.worldIdentityHash) {
            MatrixStack matrixStack = new MatrixStack();
            CorzClient.fontManager.setMatrixStack(matrixStack);

            for (int i = Math.min(nearbyEvidenceList3.size(), 10) - 1; i >= 0; i--) {
               EvidenceDug evidenceDug = (EvidenceDug)nearbyEvidenceList3.get(i);
               Vec3d vec3d = this.projectToScreen(new Vec3d(evidenceDug.basePlayerAmount, evidenceDug.finderValue + 1.5, evidenceDug.finderBaseAmount));
               if (vec3d != null && !(vec3d.z < 0.0) && !(vec3d.z > 1.0)) {
                  this.renderBaseLabel(matrixStack, evidenceDug, (float)vec3d.x, (float)vec3d.y);
               }
            }
         }
      }
   }

   private void rebuildClusters() {
      var hashMap = new HashMap<>(this.chunkScanResults);
      var items = new ArrayList();
      var hashSet = new HashSet();

      for (ChunkPos chunkPos : hashMap.keySet()) {
         if (!hashSet.contains(chunkPos)) {
            var arrayList = new ArrayList();
            var arrayDeque = new ArrayDeque();
            arrayDeque.add(chunkPos);
            hashSet.add(chunkPos);

            while (!arrayDeque.isEmpty()) {
               ChunkPos totalPriceData = (ChunkPos)arrayDeque.poll();
               arrayList.add(totalPriceData);

               for (int i = -1; i <= 1; i++) {
                  for (int j = -1; j <= 1; j++) {
                     if (i != 0 || j != 0) {
                        ChunkPos blockPos = new ChunkPos(totalPriceData.x + i, totalPriceData.z + j);
                        if (hashMap.containsKey(blockPos) && hashSet.add(blockPos)) {
                           arrayDeque.add(blockPos);
                        }
                     }
                  }
               }
            }

            items.add(this.buildCluster(arrayList, hashMap));
         }
      }

      this.playerBaseClusterList = items;
      this.sortClustersByDistance();
   }

   public static ChunkFinder.BaseKind classifyBaseKind(int farmRedstoneLimit, int farmRedstoneLimit2, int farmRedstoneLimit3, int farmRedstoneLimit4, ChunkFinder.ChunkType chunkType) {
      if (farmRedstoneLimit >= 3 || farmRedstoneLimit2 >= 8) {
         return ChunkFinder.BaseKind.PLAYER_BASE;
      } else if (farmRedstoneLimit4 > 0) {
         return ChunkFinder.BaseKind.TRIAL_CHAMBER;
      } else if (farmRedstoneLimit3 > 0) {
         return ChunkFinder.BaseKind.DUNGEON;
      } else if (chunkType == ChunkFinder.ChunkType.FARM) {
         return ChunkFinder.BaseKind.FARM;
      } else {
         return chunkType == ChunkFinder.ChunkType.REDSTONE ? ChunkFinder.BaseKind.REDSTONE : ChunkFinder.BaseKind.STORAGE;
      }
   }

   private void renderEvidenceEsp() {
      var nearbyEvidenceList3 = this.nearbyEvidenceList;
      if (!nearbyEvidenceList3.isEmpty() && System.identityHashCode(mc.world) == this.worldIdentityHash) {
         Matrix4f matrix = VertexBuilderUtils.getMatrix();
         boolean booleanValue = this.tracers.isEnabled();
         boolean enabled = this.beams.isEnabled();
         int index = 0;

         for (int i = nearbyEvidenceList3.size(); index < i; index++) {
            EvidenceDug evidenceDug = (EvidenceDug)nearbyEvidenceList3.get(index);
            boolean finderFlag = evidenceDug.high >= 75;
            this.renderEvidenceBox(matrix, evidenceDug, finderFlag);
            if (enabled) {
               this.renderBeam(
                  matrix,
                  VertexBuilderUtils.getAmountX(evidenceDug.basePlayerAmount),
                  VertexBuilderUtils.getAmount(evidenceDug.finderValue + 1.0),
                  VertexBuilderUtils.getAmountForDouble(evidenceDug.finderBaseAmount),
                  MatrixEspUtils.releaseRightValue,
                  finderFlag
               );
            }

            if (booleanValue && index < 6) {
               double finderValue = evidenceDug.finderValue + 1.5;
               VectorDotUtils.handle(evidenceDug.basePlayerAmount, finderValue, evidenceDug.finderBaseAmount, MatrixEspUtils.releaseRightValue, finderFlag);
               this.renderTracerMarker(matrix, evidenceDug.basePlayerAmount, finderValue, evidenceDug.finderBaseAmount, finderFlag);
            }
         }

         VertexBuilderUtils.updateDrawImmediate();
      }
   }

   private void renderClusterLabel(MatrixStack matrices, ChunkFinder.BaseCluster baseCluster, float x, float y) {
      VertexTextureFont vertexTexture = CorzClient.fontManager.getLargeFont();
      boolean dungeonTranslateFlag = baseCluster.confidence == ChunkFinder.Confidence.HIGH;
      String formatStashDungeon = this.getClusterLabel(baseCluster);
      double dungeonTranslateFactor = baseCluster.centerX - mc.player.getX();
      double dungeonTranslateFactor2 = baseCluster.centerZ - mc.player.getZ();
      int roundSqrt = (int)Math.round(Math.sqrt(dungeonTranslateFactor * dungeonTranslateFactor + dungeonTranslateFactor2 * dungeonTranslateFactor2));
      String dungeonTranslateText = (baseCluster.containerTotal > 0 ? baseCluster.containerTotal + " blk \u00b7 " : "") + roundSqrt + "m";
      float dungeonTranslateValue = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown();
      float dungeonTranslateValue2 = dungeonTranslateValue * 0.42F;
      float dungeonTranslateValue3 = 4.0F;
      float max4 = Math.max(vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatStashDungeon), vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(dungeonTranslateText));
      float dungeonTranslateValue5 = 6.0F;
      float dungeonTranslateValue6 = 4.0F;
      float dungeonTranslateValue7 = 1.5F;
      float dungeonTranslateValue8 = dungeonTranslateValue2 + dungeonTranslateValue3 + max4 + dungeonTranslateValue5 * 2.0F;
      float dungeonTranslateValue9 = dungeonTranslateValue * 2.0F + dungeonTranslateValue7 + dungeonTranslateValue6 * 2.0F;
      float x2 = 0.55F;
      matrices.push();
      matrices.translate(x, y, 0.0F);
      matrices.scale(x2, x2, 1.0F);
      matrices.translate(-x, -y, 0.0F);
      float dungeonTranslateValue10 = x - dungeonTranslateValue8 / 2.0F;
      float dungeonTranslateValue11 = y - dungeonTranslateValue9;
      int dungeonTranslateValue12 = -16184564;
      Color color = new Color(dungeonTranslateValue12 >> 16 & 0xFF, dungeonTranslateValue12 >> 8 & 0xFF, dungeonTranslateValue12 & 0xFF, 215);
      int releaseRightValue = MatrixEspUtils.releaseRightValue;
      int red = releaseRightValue >> 16 & 0xFF;
      int green = releaseRightValue >> 8 & 0xFF;
      int blue = releaseRightValue & 0xFF;
      float vertexConsumerValue = dungeonTranslateValue10 + dungeonTranslateValue5;
      float f12 = dungeonTranslateValue11 + dungeonTranslateValue6 + (dungeonTranslateValue - dungeonTranslateValue2) / 2.0F;
      Color color2 = new Color(red, green, blue, dungeonTranslateFlag ? 255 : 150);
      VertexConsumerUtilsVector.update4();

      try {
         VertexConsumerUtilsVector.handle(matrices, color, dungeonTranslateValue10, dungeonTranslateValue11, dungeonTranslateValue10 + dungeonTranslateValue8, dungeonTranslateValue11 + dungeonTranslateValue9, 2.5, 4.0);
         VertexConsumerUtilsVector.handle(matrices, color2, vertexConsumerValue, f12, vertexConsumerValue + dungeonTranslateValue2, f12 + dungeonTranslateValue2, dungeonTranslateValue2 / 2.0F, 6.0);
      } finally {
         VertexConsumerUtilsVector.update3();
      }

      float vertexTextureValue = dungeonTranslateValue10 + dungeonTranslateValue5 + dungeonTranslateValue2 + dungeonTranslateValue3;
      vertexTexture.handleForStringFloat(formatStashDungeon, vertexTextureValue, dungeonTranslateValue11 + dungeonTranslateValue6, new Color(-1));
      vertexTexture.handleForStringFloat(dungeonTranslateText, vertexTextureValue, dungeonTranslateValue11 + dungeonTranslateValue6 + dungeonTranslateValue + dungeonTranslateValue7, new Color(-8421505));
      if (dungeonTranslateFlag) {
         Color color3 = new Color(red, green, blue, 220);
         VertexConsumerUtilsVector.handle(matrices, color3, vertexTextureValue, dungeonTranslateValue11 + dungeonTranslateValue9 - dungeonTranslateValue6 + 1.0F, dungeonTranslateValue10 + dungeonTranslateValue8 - dungeonTranslateValue5, dungeonTranslateValue11 + dungeonTranslateValue9 - dungeonTranslateValue6 + 2.0F, 0.5, 2.0);
      }

      matrices.pop();
   }

   private boolean isSkyLit(BlockPos pos2) {
      return pos2.getY() >= mc.world.getBottomY() && pos2.getY() <= mc.world.getTopYInclusive()
         ? mc.world.getLightLevel(LightType.SKY, pos2) >= 8
         : false;
   }

   private void recordBlockChange(BlockPos pos2, BlockState state, BlockState blockState) {
      if (state != blockState) {
         if (ChunkFinderUtils.canFlag(pos2.getY())) {
            int liquidAir = BrickStoneUtils.classifyBlockState(state);
            int classifyBlockState = BrickStoneUtils.classifyBlockState(blockState);
            if (liquidAir == 3 || classifyBlockState == 3) {
               this.blockChangeCount++;
               long currentMillis = System.currentTimeMillis();
               boolean stateBlock = this.stateBlockTracker.onBlockTransition(pos2.getX(), pos2.getY(), pos2.getZ(), liquidAir, classifyBlockState, state, blockState, currentMillis);
               this.chunkLoadQueue.forgetChunkAt(pos2.getX() >> 4, pos2.getZ() >> 4);
            }
         }
      }
   }

   private void logChunkToFile(ChunkPos chunkPos, ChunkFinder.ChunkScanResult chunkScanResult) {
      try {
         BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("chunkfinder_log.txt", true));
         String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
         int startXOffset = chunkPos.getStartX() + 8;
         int startZOffset = chunkPos.getStartZ() + 8;
         bufferedWriter.write(
            String.format(
               "[%s] %s | X:%d Z:%d | Score:%d | Confidence:%s | Categories:%d%n",
               format,
               chunkScanResult.chunkType.name(),
               startXOffset,
               startZOffset,
               chunkScanResult.finderScore,
               chunkScanResult.confidence.name(),
               chunkScanResult.categoryCount
            )
         );
         bufferedWriter.close();
      } catch (IOException e) {
      }
   }

   private ChunkFinder.BaseCluster buildCluster(List<ChunkPos> items, Map<ChunkPos, ChunkFinder.ChunkScanResult> map) {
      ChunkFinder.BaseCluster baseCluster = new ChunkFinder.BaseCluster();
      baseCluster.chunkPositions = items;
      ChunkFinder.ChunkScanResult chunkScanResult = null;
      boolean chunkConfidenceFlag = false;
      long chunkConfidenceTotal = 0L;
      long chunkConfidenceTotal2 = 0L;

      for (ChunkPos chunkPos : items) {
         ChunkFinder.ChunkScanResult chunkFinderChunkScanResult = (ChunkFinder.ChunkScanResult)map.get(chunkPos);
         if (chunkFinderChunkScanResult != null) {
            if (chunkFinderChunkScanResult.baseKind == ChunkFinder.BaseKind.PLAYER_BASE) {
               chunkConfidenceFlag = true;
            }

            baseCluster.containerTotal = baseCluster.containerTotal + chunkFinderChunkScanResult.containerCount;
            baseCluster.spawnerTotal = baseCluster.spawnerTotal + chunkFinderChunkScanResult.spawnerCount;
            baseCluster.trialBlockTotal = baseCluster.trialBlockTotal + chunkFinderChunkScanResult.trialBlockCount;
            baseCluster.signalScoreTotal = baseCluster.signalScoreTotal + chunkFinderChunkScanResult.signalScore;
            baseCluster.finderScoreTotal = baseCluster.finderScoreTotal + chunkFinderChunkScanResult.finderScore;
            if (chunkFinderChunkScanResult.maxSurfaceY > baseCluster.bestConfidenceScore) {
               baseCluster.bestConfidenceScore = chunkFinderChunkScanResult.maxSurfaceY;
            }

            if (chunkFinderChunkScanResult.confidence.ordinal() > baseCluster.confidence.ordinal()) {
               baseCluster.confidence = chunkFinderChunkScanResult.confidence;
            }

            if (chunkScanResult == null || chunkFinderChunkScanResult.containerCount > chunkScanResult.containerCount || chunkFinderChunkScanResult.containerCount == chunkScanResult.containerCount && chunkFinderChunkScanResult.finderScore > chunkScanResult.finderScore) {
               chunkScanResult = chunkFinderChunkScanResult;
            }

            chunkConfidenceTotal += chunkPos.getStartX() + 8;
            chunkConfidenceTotal2 += chunkPos.getStartZ() + 8;
         }
      }

      if (chunkScanResult != null) {
         baseCluster.chunkType = chunkScanResult.chunkType;
      }

      baseCluster.baseKind = classifyBaseKind(baseCluster.signalScoreTotal, baseCluster.containerTotal, baseCluster.spawnerTotal, baseCluster.trialBlockTotal, baseCluster.chunkType);
      if (baseCluster.baseKind == ChunkFinder.BaseKind.PLAYER_BASE && !chunkConfidenceFlag && chunkScanResult != null) {
         baseCluster.baseKind = chunkScanResult.baseKind;
      }

      baseCluster.centerX = (double)chunkConfidenceTotal / Math.max(1, items.size());
      baseCluster.centerZ = (double)chunkConfidenceTotal2 / Math.max(1, items.size());
      var hashSet = new HashSet(items);
      int[] ints = new int[items.size() * 16];
      int chunkConfidenceCounter = 0;

      for (ChunkPos chunkPos2 : items) {
         int startX = chunkPos2.getStartX();
         int startZ = chunkPos2.getStartZ();
         int chunkConfidenceValue = startX + 16;
         int chunkConfidenceValue2 = startZ + 16;
         if (!hashSet.contains(new ChunkPos(chunkPos2.x, chunkPos2.z - 1))) {
            ints[chunkConfidenceCounter++] = startX;
            ints[chunkConfidenceCounter++] = startZ;
            ints[chunkConfidenceCounter++] = chunkConfidenceValue;
            ints[chunkConfidenceCounter++] = startZ;
         }

         if (!hashSet.contains(new ChunkPos(chunkPos2.x, chunkPos2.z + 1))) {
            ints[chunkConfidenceCounter++] = startX;
            ints[chunkConfidenceCounter++] = chunkConfidenceValue2;
            ints[chunkConfidenceCounter++] = chunkConfidenceValue;
            ints[chunkConfidenceCounter++] = chunkConfidenceValue2;
         }

         if (!hashSet.contains(new ChunkPos(chunkPos2.x - 1, chunkPos2.z))) {
            ints[chunkConfidenceCounter++] = startX;
            ints[chunkConfidenceCounter++] = startZ;
            ints[chunkConfidenceCounter++] = startX;
            ints[chunkConfidenceCounter++] = chunkConfidenceValue2;
         }

         if (!hashSet.contains(new ChunkPos(chunkPos2.x + 1, chunkPos2.z))) {
            ints[chunkConfidenceCounter++] = chunkConfidenceValue;
            ints[chunkConfidenceCounter++] = startZ;
            ints[chunkConfidenceCounter++] = chunkConfidenceValue;
            ints[chunkConfidenceCounter++] = chunkConfidenceValue2;
         }
      }

      baseCluster.edgeSegments = Arrays.copyOf(ints, chunkConfidenceCounter);
      return baseCluster;
   }

   private boolean isChunkResultSignificant(ChunkFinder.ChunkScanResult chunkScanResult) {
      boolean finderChunkFlag = chunkScanResult.categoryCount >= 2 && chunkScanResult.finderScore >= this.getMinChunkScore();
      boolean finderChunkFlag2 = this.getMinContainerCount() > 0 && chunkScanResult.containerCount >= this.getMinContainerCount();
      boolean finderChunkFlag3 = chunkScanResult.trialBlockCount > 0 || chunkScanResult.spawnerCount > 0;
      boolean finderChunkFlag4 = chunkScanResult.signalScore >= 3;
      return finderChunkFlag || finderChunkFlag2 || finderChunkFlag3 || finderChunkFlag4;
   }

   private void notifyChunkFind(ChunkPos chunkPos, ChunkFinder.ChunkScanResult chunkScanResult) {
      if (this.notifications.isEnabled()) {
         if (this.isKindEnabled(chunkScanResult.baseKind)) {
            if (chunkScanResult.confidence == ChunkFinder.Confidence.HIGH) {
               long currentMillis = System.currentTimeMillis();
               Long concurrenthashmap = this.chunkNotifyTimes.get(chunkPos);
               if (concurrenthashmap == null || currentMillis - concurrenthashmap >= 120000L) {
                  this.chunkNotifyTimes.put(chunkPos, currentMillis);
                  int startXOffset = chunkPos.getStartX() + 8;
                  int startZOffset = chunkPos.getStartZ() + 8;
                  String format = String.format("Possible %s at X:%d Z:%d (Score:%d)", chunkScanResult.chunkType.name().toLowerCase(), startXOffset, startZOffset, chunkScanResult.finderScore);
                  DisabledEnabled.getInstance().handleDisabledEnabled("ChunkFinder", format, true);
                  this.logChunkToFile(chunkPos, chunkScanResult);
                  if (this.soundPing.isEnabled()) {
                     PingSoundPlayer.playPingSound();
                  }
               }
            }
         }
      }
   }

   private void queueChunkScan(ChunkPos chunkPos) {
      if (this.isEnabled()) {
         this.chunkLoadQueue.forgetChunkAt(chunkPos.x, chunkPos.z);
      }
   }

   private void tickScan() {
      PingSoundPlayer.noOpUpdate();
      int hashCode = System.identityHashCode(mc.world);
      if (this.currentDimensionId == null || hashCode != this.worldIdentityHash) {
         this.resetState();
         this.currentDimensionId = mc.world.getRegistryKey().getValue().toString();
         this.worldIdentityHash = hashCode;
      }

      String threadText = this.currentDimensionId;
      ChunkPos chunkPos = mc.player.getChunkPos();
      int hashChunkValue = this.scanRadius.getValue();
      int value = ChunkFinderUtils.getMax(mc.world.getBottomY());
      int minForInt2 = ChunkFinderUtils.getMinForInt(mc.world.getTopYInclusive());
      if (minForInt2 < value) {
         if (!this.allEvidenceList.isEmpty()) {
            this.allEvidenceList = new ArrayList<>();
            this.nearbyEvidenceList = new ArrayList<>();
         }
      } else {
         int value3 = this.sensitivity.getValue();
         if (value3 != this.lastRebuildKey) {
            this.lastRebuildKey = value3;
            this.rebuildPending = true;
         }

         this.scanMinHeight = value;
         this.scanMaxHeight = minForInt2;
         this.chunkLoadQueue.scanChunks(mc.world, chunkPos.x, chunkPos.z, hashChunkValue, 30000L, ChunkScanScheduler.getFpsScaledBudgetNanos(800), this::scanChunkEvidence);
         this.tickCounter++;
         if (this.tickCounter % 10 == 0) {
            long timeMillis = System.currentTimeMillis();
            int nowMsOffset = hashChunkValue + 4;
            this.chunkCache.keySet().removeIf(playerChunk -> {
               int var4x = (int)(playerChunk & 4294967295L);
               int var5x = (int)(playerChunk >> 32);
               boolean var6x = Math.abs(var4x - chunkPos.x) > nowMsOffset || Math.abs(var5x - chunkPos.z) > nowMsOffset;
               if (var6x) {
                  this.activeChunkKeys.remove(playerChunk);
                  this.rebuildPending = true;
               }

               return var6x;
            });
            this.chunkLoadQueue.removeFarChunks(chunkPos.x, chunkPos.z, nowMsOffset);
            if (timeMillis - this.lastStateCleanupMs > 30000L) {
               this.lastStateCleanupMs = timeMillis;
               int purgeDistantAndStale = this.stateBlockTracker.purgeDistantAndStale(timeMillis, chunkPos.x, chunkPos.z, hashChunkValue + 24);
            }

            if (timeMillis - this.lastBlockChangeCheckMs > 2000L) {
               this.lastBlockChangeCheckMs = timeMillis;
               BlockPos blockPos = mc.player.getBlockPos();
               int timeFlag = this.stateBlockTracker.purgeFlaggedEntriesNear(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 5, timeMillis, 10000L);
               if (timeFlag > 0) {
                  for (int i = -1; i <= 1; i++) {
                     for (int j = -1; j <= 1; j++) {
                        this.chunkLoadQueue.forgetChunkAt(chunkPos.x + i, chunkPos.z + j);
                     }
                  }

                  this.rebuildPending = true;
               }
            }

            this.scannedChunkCount = this.scannedSectionTotal = this.scanSkippedTotal = this.totalExcavationCount = this.zeroedSectionChunkCount = this.normalSectionChunkCount = 0;
            this.totalScanTimeNanos = 0L;
            ChunkFinder.RebuildJob rebuildJob = this.rebuildJob;
            if (rebuildJob != null) {
               this.rebuildJob = null;
               this.rebuildInFlight = false;
               if (rebuildJob.generation == this.resetGeneration && rebuildJob.craftedGroups != null) {
                  this.applyRebuildResult(rebuildJob);
               }
            } else {
               this.refreshNearbyEvidence();
            }

            if (timeMillis - this.lastRebuildSubmitMs >= 5000L) {
               this.rebuildPending = true;
            }

            if (this.rebuildPending && !this.rebuildInFlight && timeMillis - this.lastRebuildSubmitMs >= 250L) {
               this.rebuildPending = false;
               this.lastRebuildSubmitMs = timeMillis;
               this.submitRebuildJob(threadText);
            }
         }
      }
   }

   public ChunkFinder() {
      super("ChunkFinder", IconsPngType.DONUTSMP);
      this.trialChambers = new BooleanSetting("Trial Chambers", "Trial spawners and vaults", true);
      this.spawners = new BooleanSetting("Spawners", "Natural mob-spawner dungeons", true);
      this.storageSetting = new BooleanSetting("Storage", "Loose storage stashes not clearly tied to a player base", true);
      this.farms = new BooleanSetting("Farms", "Crop / mob / production farms", true);
      this.redstoneSetting = new BooleanSetting("Redstone", "Redstone machines and contraptions", true);
      this.tracers = new BooleanSetting("Tracers", "Guide line from your crosshair to the nearest finds.", true);
      this.beams = new BooleanSetting("Beam", "Thin vertical beam rising from each find.", true);
      this.labels = new BooleanSetting("Labels", "Floating tag saying what the find is and how far away.", true);
      this.notifications = new BooleanSetting("Notifications", "Show a toast for strong finds.", true);
      this.soundPing = new BooleanSetting("Sound Ping", "Play a sound for strong finds.", false);
      this.chunkScanResults = new ConcurrentHashMap<>();
      this.chunkNotifyTimes = new ConcurrentHashMap<>();
      this.chunkScanTracker = new ChunkScanScheduler();
      this.chunkResultsDirty = false;
      this.lastStrongSoundValue = -1;
      this.tickCounter = 0;
      this.playerBaseClusterList = new ArrayList<>();
      this.chunkCache = new ConcurrentHashMap<>();
      this.chunkLoadQueue = new ChunkScanScheduler();
      this.nearbyEvidenceList = new ArrayList<>();
      this.lastRebuildKey = -1;
      this.baseNotifyTimes = new ConcurrentHashMap<>();
      this.allEvidenceList = new ArrayList<>();
      this.currentDimensionId = null;
      this.worldIdentityHash = 0;
      this.rebuildPending = false;
      this.threadExecutor = Executors.newSingleThreadExecutor(runnable -> {
         Thread thread = new Thread(runnable, "Corz-ChunkFinder-Cluster");
         thread.setDaemon(true);
         thread.setPriority(1);
         return thread;
      });
      this.rebuildInFlight = false;
      this.rebuildJob = null;
      this.lastRebuildSubmitMs = 0L;
      this.resetGeneration = 0;
      this.forestDark = new BiomeCategoryMapper();
      this.stateBlockTracker = new StateBlockFlag();
      this.activeChunkKeys = ConcurrentHashMap.newKeySet();
      this.lastStateCleanupMs = 0L;
      this.lastBlockChangeCheckMs = 0L;
      this.blockChangeCount = 0;
      this.addSettings(
         new ConfigSetting[]{
            this.sensitivity, this.scanRadius, this.tracers, this.beams, this.labels, this.notifications, this.soundPing
         }
      );
      singleton = this;
      ClientChunkEvents.CHUNK_LOAD.register((Load)(world, chunk) -> this.queueChunkScan(chunk.getPos()));
   }

   private static double getEvidenceDistanceSq(EvidenceDug evidenceDug, double finderValue, double z) {
      double finderFactor = evidenceDug.basePlayerAmount - finderValue;
      double finderFactor2 = evidenceDug.finderBaseAmount - z;
      return finderFactor * finderFactor + finderFactor2 * finderFactor2;
   }

   private int withAlpha(int chunkFinderValue, float chunkFinderValue2) {
      int max3 = Math.max(0, Math.min(255, Math.round(chunkFinderValue2 * 255.0F)));
      return max3 << 24 | chunkFinderValue & 16777215;
   }

   private String getClusterLabel(ChunkFinder.BaseCluster baseCluster) {
      switch (baseCluster.baseKind) {
         case PLAYER_BASE:
            return baseCluster.containerTotal >= 14 ? "Player Stash" : "Player Base";
         case TRIAL_CHAMBER:
            return "Trial Chamber";
         case DUNGEON:
            return baseCluster.containerTotal > 0 ? "Dungeon" : "Spawner";
         case STORAGE:
            return baseCluster.containerTotal >= 14 ? "Stash" : "Storage";
         case FARM:
            return "Farm";
         case REDSTONE:
            return "Redstone";
         default:
            return "Base";
      }
   }

   private void refreshNearbyEvidence() {
      var list = this.allEvidenceList;
      if (!list.isEmpty() && mc.player != null) {
         String moneyEnoughText = this.currentDimensionId;
         double x = mc.player.getX();
         double z = mc.player.getZ();
         double playerIntegerFactor = this.scanRadius.getValue() * 16.0 + 48.0;
         double playerIntegerValue = playerIntegerFactor * playerIntegerFactor;
         var items = new ArrayList();

         for (EvidenceDug evidenceDug : list) {
            if (evidenceDug.playerIntegerText.equals(moneyEnoughText) && this.isEvidenceActive(evidenceDug) && !(getEvidenceDistanceSq(evidenceDug, x, z) > playerIntegerValue)) {
               items.add(evidenceDug);
            }
         }

         items.sort((obj, obj2) -> Double.compare(getEvidenceDistanceSq(((com.corz.client.utils.EvidenceDug) (obj)), x, z), getEvidenceDistanceSq(((com.corz.client.utils.EvidenceDug) (obj2)), x, z)));
         this.nearbyEvidenceList = items;
      } else {
         if (!this.nearbyEvidenceList.isEmpty()) {
            this.nearbyEvidenceList = new ArrayList<>();
         }
      }
   }

   private static boolean isOwnedOrNamedEntity(Entity entity) {
      if (entity instanceof TameableEntity tameableEntity && tameableEntity.isTamed()) {
         return true;
      } else if (entity instanceof AbstractHorseEntity abstractHorseEntity && abstractHorseEntity.isTame()) {
         return true;
      } else {
         return entity instanceof MobEntity mobEntity && mobEntity.hasCustomName() ? true : entity instanceof Leashable leashable && leashable.isLeashed();
      }
   }

   private static String formatTopBlockCounts(ExcavationCluster maxCrafted) {
      var arrayList = new ArrayList<Integer>();

      for (int i = 1; i < 148; i++) {
         if (maxCrafted.getTypeIdCount(i) > 0) {
            arrayList.add(i);
         }
      }

      arrayList.sort((integer, integer2) -> Integer.compare(maxCrafted.getTypeIdCount(integer2), maxCrafted.getTypeIdCount(integer)));
      StringBuilder stringBuilder = new StringBuilder();

      for (int j = 0; j < arrayList.size() && j < 8; j++) {
         if (stringBuilder.length() > 0) {
            stringBuilder.append(", ");
         }

         stringBuilder.append(SpawnerSculkUtils.getIdName((Integer)arrayList.get(j))).append(' ').append(maxCrafted.getTypeIdCount((Integer)arrayList.get(j)));
      }

      return stringBuilder.toString();
   }

   private void applyRebuildResult(ChunkFinder.RebuildJob rebuildJob) {
      DivPenUtilsFlag detectionSettings = rebuildJob.detectionSettings;
      boolean concurrentFlag = rebuildJob.stateFlagged;
      var list = rebuildJob.sensorBlocks;
      int concurrentValue = rebuildJob.baselineEvidenceTotal;
      String concurrentText = rebuildJob.dimensionId;
      var craftedGroups = rebuildJob.craftedGroups;
      var items = new ArrayList();
      long currentMillis = System.currentTimeMillis();

      for (ExcavationCluster maxCrafted : craftedGroups) {
         ExcavationTypeStats flagsHandle = ExcavationTypeStats.fromExcavations(maxCrafted.getCells(), concurrentValue);
         int concurrentTotal = 0;
         int concurrentTotal2 = 0;
         int concurrentTotal3 = 0;
         int concurrentTotal4 = 0;

         for (long getChunkKeysItem : maxCrafted.getChunkKeys()) {
            ChunkFinder.CachedChunk concurrenthashmap = this.chunkCache.get(getChunkKeysItem);
            if (concurrenthashmap != null) {
               flagsHandle.addCountedTypes(concurrenthashmap.blockFlags);
               boolean concurrentFlag2 = detectionSettings.threadActive && (concurrentFlag || concurrenthashmap.excavationActive);
               if (!concurrentFlag2) {
                  concurrentTotal += concurrenthashmap.scannedCount;
               }

               int deltaValue = concurrenthashmap.evidenceTotal - concurrenthashmap.candidateBlockCount;
               if (concurrentFlag2) {
                  concurrentTotal2 += deltaValue;
                  concurrentTotal3 += concurrenthashmap.candidateBlockCount - concurrenthashmap.flaggedBlockCount;
                  concurrentTotal4 += concurrenthashmap.flaggedBlockCount;
               } else {
                  concurrentTotal2 += concurrenthashmap.evidenceTotal;
               }
            }
         }

         maxCrafted.addCraftedSum(concurrentTotal);
         int percentShare = ConstChunkUtils.getPercentShare(concurrentTotal2 + maxCrafted.getHopperCount(), concurrentTotal3, concurrentTotal4);

         for (SensorBlock sensorBlock : list) {
            if (sensorBlock.intersectsBoxWithSingleRange(maxCrafted.getMinX(), maxCrafted.getMinY(), maxCrafted.getMinZ(), maxCrafted.getMaxX(), maxCrafted.getMaxY(), maxCrafted.getMaxZ())) {
               flagsHandle.incrementTypeCount(sensorBlock.typeId);
            } else if (sensorBlock.intersectsBoxWithRanges(maxCrafted.getMinX(), maxCrafted.getMinY(), maxCrafted.getMinZ(), maxCrafted.getMaxX(), maxCrafted.getMaxY(), maxCrafted.getMaxZ())) {
               flagsHandle.markTypeSeen(sensorBlock.typeId);
            }
         }

         if (detectionSettings.concurrentActive) {
            this.countEntitiesNearEvidence(maxCrafted, flagsHandle);
         }

         if (flagsHandle.getTypeCount(120) > 0) {
            maxCrafted.refreshStats();
         }

         CityMonument timesCity = TimesSensorUtils.detectCityMonument(flagsHandle);
         PenDivUtils.Decision penDiv = PenDivUtils.getPenDiv(maxCrafted, timesCity, detectionSettings);
         boolean penalizedTrialFlag = penDiv.concurrentActive && this.isHiddenUnderground(maxCrafted);
         if (penDiv.concurrentActive) {
            items.add(this.buildEvidence(maxCrafted, timesCity, penDiv, concurrentText, currentMillis, penalizedTrialFlag, percentShare));
         }
      }

      items.sort((obj, obj2) -> Integer.compare(((com.corz.client.utils.EvidenceDug) obj2).high, ((com.corz.client.utils.EvidenceDug) obj).high));
      this.allEvidenceList = items;
      this.refreshNearbyEvidence();

      for (EvidenceDug evidenceDug : (Iterable<com.corz.client.utils.EvidenceDug>) (items)) {
         this.notifyPlayerBase(evidenceDug);
      }
   }

   private void resetState() {
      this.chunkCache.clear();
      this.chunkLoadQueue.clearScannedChunks();
      this.baseNotifyTimes.clear();
      this.allEvidenceList = new ArrayList<>();
      this.nearbyEvidenceList = new ArrayList<>();
      this.rebuildPending = false;
      this.stateBlockTracker.reset();
      this.activeChunkKeys.clear();
      this.blockChangeCount = 0;
      this.resetGeneration++;
      this.rebuildJob = null;
      this.rebuildInFlight = false;
      this.lastRebuildSubmitMs = 0L;
      this.currentDimensionId = null;
   }

   private boolean isHiddenUnderground(ExcavationCluster maxCrafted) {
      if (mc.world == null) {
         return true;
      } else {
         try {
            var list = maxCrafted.getCells();
            int varSize = list.size();
            int worldLoadedValue = Math.max(1, varSize / 48);
            int worldLoadedCounter = 0;
            int worldLoadedCounter2 = 0;
            int worldLoadedCounter3 = 0;
            Mutable mutable = new Mutable();

            for (int i = 0; i < varSize; i += worldLoadedValue) {
               CraftedExcavation craftedExcavation = (CraftedExcavation)list.get(i);
               if (mc.world.isChunkLoaded(craftedExcavation.posX >> 4, craftedExcavation.posZ >> 4)) {
                  worldLoadedCounter++;
                  if (this.isSkyLit(mutable.set(craftedExcavation.posX, craftedExcavation.posY, craftedExcavation.posZ))
                     || this.isSkyLit(mutable.set(craftedExcavation.posX, craftedExcavation.posY + 1, craftedExcavation.posZ))
                     || this.isSkyLit(mutable.set(craftedExcavation.posX, craftedExcavation.posY - 1, craftedExcavation.posZ))
                     || this.isSkyLit(mutable.set(craftedExcavation.posX + 1, craftedExcavation.posY, craftedExcavation.posZ))
                     || this.isSkyLit(mutable.set(craftedExcavation.posX - 1, craftedExcavation.posY, craftedExcavation.posZ))
                     || this.isSkyLit(mutable.set(craftedExcavation.posX, craftedExcavation.posY, craftedExcavation.posZ + 1))
                     || this.isSkyLit(mutable.set(craftedExcavation.posX, craftedExcavation.posY, craftedExcavation.posZ - 1))) {
                     worldLoadedCounter2++;
                  }

                  int topY = mc.world.getTopY(Type.WORLD_SURFACE, craftedExcavation.posX, craftedExcavation.posZ);
                  if (topY - craftedExcavation.posY >= 6) {
                     worldLoadedCounter3++;
                  }
               }
            }

            return worldLoadedCounter == 0 ? true : worldLoadedCounter2 * 4 < worldLoadedCounter && worldLoadedCounter3 * 2 > worldLoadedCounter;
         } catch (Throwable e2) {
            return true;
         }
      }
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         this.tickScan();
      }
   }

   private int countNearbyPositions(List<BlockPos> list, List<BlockPos> list2, int manhattanDistanceLimit) {
      int manhattanDistance = 0;

      for (BlockPos blockPos : list) {
         for (BlockPos blockPos2 : list2) {
            if (blockPos.getManhattanDistance(blockPos2) <= manhattanDistanceLimit) {
               manhattanDistance++;
               break;
            }
         }
      }

      return manhattanDistance;
   }

   private DivPenUtilsFlag buildDetectionSettings() {
      return new DivPenUtilsFlag(
         this.getBaseConfidenceThreshold(), this.getSensitivityBand(), this.getMinBaseScore(), this.getSensitivityStep(), 6, true, true, true, true, true, true, true, true, true, true, true, false
      );
   }

   private int getSensitivityBand() {
      int integerLimit = this.sensitivity.getValue();
      return integerLimit <= 2 ? 4 : (integerLimit >= 8 ? 2 : 3);
   }

   private long getNormalSectionMask(int sectionStatussValue, int sectionStatussValue2) {
      try {
         ChunkLightSnapshot chunkLightSnapshot = LightPacketCache.INSTANCE.sectionsCoordChunkLightSnaps.get(ChunkPos.toLong(sectionStatussValue, sectionStatussValue2));
         if (chunkLightSnapshot == null) {
            return 0L;
         } else {
            long sectionStatuss = 0L;

            for (int i = 1; i <= chunkLightSnapshot.verticalSections && i - 1 < 64; i++) {
               int sectionStatussLimit = (chunkLightSnapshot.sectionCoord + (i - 1)) * 16;
               if (sectionStatussLimit > -30) {
                  break;
               }

               if (ChunkFinderUtils.canFlagForInt(sectionStatussLimit) && chunkLightSnapshot.skyLightStatuses[i] == SectionStatus.NORMAL) {
                  sectionStatuss |= 1L << i - 1;
               }
            }

            return sectionStatuss;
         }
      } catch (Throwable e) {
         return 0L;
      }
   }

   private void renderBeam(Matrix4f matrix4f, float chunkFinderValue, float deltaY, float chunkFinderValue3, int releaseRightValue, boolean chunkFinderFlag) {
      float chunkFinderValue5 = deltaY + (chunkFinderFlag ? 72.0F : 48.0F);
      this.renderBeamQuads(matrix4f, chunkFinderValue, deltaY, chunkFinderValue3, deltaY + 2.0F, 0.14F, this.withAlpha(-1, chunkFinderFlag ? 0.95F : 0.75F), this.withAlpha(releaseRightValue, chunkFinderFlag ? 0.9F : 0.6F));
      this.renderBeamQuads(matrix4f, chunkFinderValue, deltaY, chunkFinderValue3, chunkFinderValue5, 0.32F, this.withAlpha(releaseRightValue, chunkFinderFlag ? 0.38F : 0.26F), this.withAlpha(releaseRightValue, 0.0F));
      this.renderBeamQuads(matrix4f, chunkFinderValue, deltaY, chunkFinderValue3, chunkFinderValue5, 0.11F, this.withAlpha(releaseRightValue, chunkFinderFlag ? 1.0F : 0.85F), this.withAlpha(releaseRightValue, 0.0F));
   }

   private int countClosePairs(List<BlockPos> list) {
      if (list.size() < 3) {
         return 0;
      } else {
         int chunkFinderCounter = 0;

         for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
               BlockPos position = (BlockPos)list.get(i);
               BlockPos blockPos = (BlockPos)list.get(j);
               double manhattanDistance = position.getManhattanDistance(blockPos);
               if (manhattanDistance <= 2.0) {
                  chunkFinderCounter++;
               }
            }
         }

         return Math.min(chunkFinderCounter, 15);
      }
   }

   private void renderBeamQuads(Matrix4f matrix4f, float vertexColorValue, float deltaY, float vertexColorValue3, float vertexColorValue4, float vertexColorValue5, int vertexColorValue6, int vertexColorValue7) {
      VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();
      buffer.vertex(matrix4f, vertexColorValue - vertexColorValue5, deltaY, vertexColorValue3).color(vertexColorValue6);
      buffer.vertex(matrix4f, vertexColorValue + vertexColorValue5, deltaY, vertexColorValue3).color(vertexColorValue6);
      buffer.vertex(matrix4f, vertexColorValue + vertexColorValue5, vertexColorValue4, vertexColorValue3).color(vertexColorValue7);
      buffer.vertex(matrix4f, vertexColorValue - vertexColorValue5, vertexColorValue4, vertexColorValue3).color(vertexColorValue7);
      buffer.vertex(matrix4f, vertexColorValue, deltaY, vertexColorValue3 - vertexColorValue5).color(vertexColorValue6);
      buffer.vertex(matrix4f, vertexColorValue, deltaY, vertexColorValue3 + vertexColorValue5).color(vertexColorValue6);
      buffer.vertex(matrix4f, vertexColorValue, vertexColorValue4, vertexColorValue3 + vertexColorValue5).color(vertexColorValue7);
      buffer.vertex(matrix4f, vertexColorValue, vertexColorValue4, vertexColorValue3 - vertexColorValue5).color(vertexColorValue7);
   }

   private void countEntitiesNearEvidence(ExcavationCluster maxCrafted, ExcavationTypeStats flagsHandle) {
      if (mc.world != null && mc.player != null) {
         try {
            Box box = new Box(
               maxCrafted.getMinX() - 16,
               Math.max(maxCrafted.getMinY() - 8, -72),
               maxCrafted.getMinZ() - 16,
               maxCrafted.getMaxX() + 17,
               Math.min(maxCrafted.getMaxY() + 9, -21),
               maxCrafted.getMaxZ() + 17
            );
            int value = 0;
            int value2 = 0;
            int value3 = 0;
            var hashMap = new HashMap();

            for (Entity entity : mc.world.getOtherEntities(mc.player, box)) {
               if (entity instanceof VillagerEntity) {
                  value++;
               } else if (entity instanceof IronGolemEntity) {
                  value2++;
               } else if (entity instanceof PillagerEntity) {
                  value3++;
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
            flagsHandle.cityValue = value3;
            flagsHandle.peakValue = max;
            maxCrafted.setPeaks(value, value2, value3, max);
         } catch (Throwable e2) {
         }
      }
   }

   private List<CraftedExcavation> getEntityMarkers() {
      var arrayList = new ArrayList();
      if (mc.world != null && mc.player != null) {
         try {
            double utilityMarkerValue = this.scanRadius.getValue() * 16.0 + 16.0;
            double x = mc.player.getX();
            double z = mc.player.getZ();
            Box box = new Box(x - utilityMarkerValue, -64.0, z - utilityMarkerValue, x + utilityMarkerValue, -29.0, z + utilityMarkerValue);
            int utilityMarkerCounter = 0;

            for (Entity entity : mc.world.getOtherEntities(mc.player, box)) {
               if (entity.isAlive()) {
                  BlockPos blockPos = entity.getBlockPos();
                  if (ChunkFinderUtils.canFlag(blockPos.getY())) {
                     if (entity instanceof ItemFrameEntity || entity instanceof PaintingEntity || entity instanceof ArmorStandEntity armorStandEntity && !armorStandEntity.isMarker()) {
                        arrayList.add(CraftedExcavation.createTypedNoGroup(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ExcavationCraftedType.UTILITY, 2, 142, 0));
                     } else if (entity instanceof StorageMinecartEntity || entity instanceof AbstractChestBoatEntity) {
                        arrayList.add(CraftedExcavation.createCategorizedTyped(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ExcavationCraftedType.STORAGE, 4, 32, 0, 143, 0));
                     } else if (utilityMarkerCounter < 16 && isOwnedOrNamedEntity(entity)) {
                        arrayList.add(CraftedExcavation.createTypedNoGroup(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ExcavationCraftedType.UTILITY, 3, 144, 0));
                        utilityMarkerCounter++;
                     }
                  }
               }
            }
         } catch (Throwable e) {
         }

         return arrayList;
      } else {
         return arrayList;
      }
   }

   private int getMinChunkScore() {
      return Math.max(5, 25 + (5 - this.sensitivity.getValue()) * 4);
   }

   private Vec3d projectToScreen(Vec3d vec) {
      return VertexConsumerUtilsVector.getWindowFramebuffer(vec);
   }

   private static String formatTopExcavationTypes(List<CraftedExcavation> list) {
      var hashMap = new HashMap();

      for (CraftedExcavation craftedExcavation : list) {
         if (craftedExcavation.hasFlag(64)) {
            hashMap.merge(craftedExcavation.craftedTypeId, 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
         }
      }

      var arrayList = new ArrayList<Map.Entry<Integer, Integer>>(hashMap.entrySet());
      arrayList.sort((entry, entry2) -> Integer.compare((Integer)entry2.getValue(), (Integer)entry.getValue()));
      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < arrayList.size() && i < 6; i++) {
         if (stringBuilder.length() > 0) {
            stringBuilder.append(", ");
         }

         stringBuilder.append(SpawnerSculkUtils.getIdName((Integer)((java.util.Map.Entry)arrayList.get(i)).getKey()))
            .append(' ')
            .append(((java.util.Map.Entry)arrayList.get(i)).getValue());
      }

      return stringBuilder.toString();
   }

   @Override
   protected void onEnable() {
      this.chunkScanResults.clear();
      this.chunkNotifyTimes.clear();
      this.chunkScanTracker.clearScannedChunks();
      this.chunkResultsDirty = false;
      this.lastStrongSoundValue = -1;
      this.playerBaseClusterList = new ArrayList<>();
      this.tickCounter = 0;
      this.resetState();
   }

   private ChunkFinder.BaseKind classifyStructureKind(WorldChunk worldChunk, int craftedExcavationValue, int biomeCraftedLimit, int craftedExcavationLimit, int craftedExcavationLimit2) {
      if (mc.world == null) {
         return null;
      } else {
         try {
            ChunkScan classForWorldChunkWorld = BlockStateUtilsWorld.getClassForWorldChunkWorld(worldChunk, mc.world, this.forestDark, craftedExcavationValue, biomeCraftedLimit, null, false);
            CityMonument timesCity = TimesSensorUtils.detectCityMonument(classForWorldChunkWorld.flagsHandle);
            boolean craftedExcavationFlag = timesCity.craftedExcavationActive || timesCity.monumentScore >= 40;
            if (!craftedExcavationFlag && !timesCity.hasAnyStructure()) {
               return null;
            } else {
               for (CraftedExcavation craftedExcavation : classForWorldChunkWorld.craftedExcavations) {
                  boolean craftedExcavationFlag2 = craftedExcavation.excavationCraftedType == ExcavationCraftedType.STORAGE || craftedExcavation.excavationCraftedType == ExcavationCraftedType.UTILITY;
                  if (craftedExcavationFlag2 && !timesCity.hasStructureBit(craftedExcavation.craftedTypeId)) {
                     return null;
                  }
               }

               if (craftedExcavationFlag || craftedExcavationLimit2 > 0) {
                  return ChunkFinder.BaseKind.TRIAL_CHAMBER;
               } else {
                  return craftedExcavationLimit > 0 ? ChunkFinder.BaseKind.DUNGEON : ChunkFinder.BaseKind.STORAGE;
               }
            }
         } catch (Throwable e) {
            return null;
         }
      }
   }

   private int getMinContainerCount() {
      return Math.max(2, 6 + (5 - this.sensitivity.getValue()));
   }

   @Override
   public void onRender2D(TickProgress tickProgress) {
      if (mc.world != null && mc.player != null) {
         this.renderLabels(tickProgress);
      }
   }

   private boolean isKindEnabled(ChunkFinder.BaseKind baseKind) {
      switch (baseKind) {
         case PLAYER_BASE:
            return this.playerBases.isEnabled();
         case TRIAL_CHAMBER:
            return this.trialChambers.isEnabled();
         case DUNGEON:
            return this.spawners.isEnabled();
         case STORAGE:
            return this.storageSetting.isEnabled();
         case FARM:
            return this.farms.isEnabled();
         case REDSTONE:
            return this.redstoneSetting.isEnabled();
         default:
            return true;
      }
   }

   private int getMinBaseScore() {
      return Math.max(5, Math.min(14, 10 + (5 - this.sensitivity.getValue())));
   }

   public static class BaseCluster {
      public ChunkFinder.BaseKind baseKind;
      public int bestConfidenceScore;
      public double centerZ;
      public int trialBlockTotal;
      public int spawnerTotal;
      public int containerTotal;
      public int finderScoreTotal;
      public int[] edgeSegments;
      public int signalScoreTotal;
      public ChunkFinder.ChunkType chunkType;
      public ChunkFinder.Confidence confidence;
      public List<ChunkPos> chunkPositions = new ArrayList<>();
      public double centerX;

      public BaseCluster() {
         this.bestConfidenceScore = Integer.MIN_VALUE;
         this.containerTotal = 0;
         this.spawnerTotal = 0;
         this.trialBlockTotal = 0;
         this.signalScoreTotal = 0;
         this.finderScoreTotal = 0;
         this.confidence = ChunkFinder.Confidence.LOW;
         this.chunkType = ChunkFinder.ChunkType.MIXED;
         this.baseKind = ChunkFinder.BaseKind.STORAGE;
         this.edgeSegments = new int[0];
      }
   }

   public static enum BaseKind {
      PLAYER_BASE,
      TRIAL_CHAMBER,
      DUNGEON,
      STORAGE,
      FARM,
      REDSTONE;
   }

   private static final class CachedChunk {
      final int evidenceTotal;
      final long cachedAtStamp;
      final int candidateBlockCount;
      final boolean excavationActive;
      final List<SensorBlock> sensorBlocks;
      final List<CraftedExcavation> excavations;
      final int scannedCount;
      final ExcavationTypeStats blockFlags;
      final int flaggedBlockCount;

      CachedChunk(
         List<CraftedExcavation> items,
         int value,
         ExcavationTypeStats max,
         List<SensorBlock> items2,
         int value2,
         int value3,
         int value4,
         boolean flag,
         long time
      ) {
         this.excavations = items;
         this.scannedCount = value;
         this.blockFlags = max;
         this.sensorBlocks = items2;
         this.evidenceTotal = value2;
         this.candidateBlockCount = value3;
         this.flaggedBlockCount = value4;
         this.excavationActive = flag;
         this.cachedAtStamp = time;
      }
   }

   public static class ChunkScanResult {
      public final ChunkFinder.BaseKind baseKind;
      public final int signalScore;
      public final int finderScore;
      public final int maxSurfaceY;
      public final int containerCount;
      public final int spawnerCount;
      public final ChunkPos chunkPos;
      public final int trialBlockCount;
      public final ChunkFinder.ChunkType chunkType;
      public final ChunkFinder.Confidence confidence;
      public final int categoryCount;

      public ChunkScanResult(
         ChunkPos chunkPos2,
         int finderChunkValue,
         ChunkFinder.ChunkType chunkType,
         ChunkFinder.Confidence confidence2,
         int value,
         int value2,
         int value3,
         int value4,
         int value5,
         int farmRedstoneLimit,
         ChunkFinder.BaseKind baseKind
      ) {
         this.chunkPos = chunkPos2;
         this.finderScore = finderChunkValue;
         this.chunkType = chunkType;
         this.confidence = confidence2;
         this.maxSurfaceY = value;
         this.categoryCount = value2;
         this.containerCount = value3;
         this.spawnerCount = value4;
         this.trialBlockCount = value5;
         this.signalScore = farmRedstoneLimit;
         this.baseKind = baseKind != null ? baseKind : ChunkFinder.classifyBaseKind(farmRedstoneLimit, value3, value4, value5, chunkType);
      }

      boolean isSameResult(ChunkFinder.ChunkScanResult chunkScanResult) {
         return chunkScanResult != null
            && this.finderScore == chunkScanResult.finderScore
            && this.chunkType == chunkScanResult.chunkType
            && this.confidence == chunkScanResult.confidence
            && this.maxSurfaceY == chunkScanResult.maxSurfaceY
            && this.containerCount == chunkScanResult.containerCount
            && this.spawnerCount == chunkScanResult.spawnerCount
            && this.trialBlockCount == chunkScanResult.trialBlockCount
            && this.signalScore == chunkScanResult.signalScore
            && this.baseKind == chunkScanResult.baseKind;
      }

      public ChunkScanResult(
         ChunkPos chunkPos2,
         int chunkScanValue,
         ChunkFinder.ChunkType chunkType,
         ChunkFinder.Confidence confidence2,
         int chunkScanValue2,
         int chunkScanValue3,
         int chunkScanValue4,
         int chunkScanValue5,
         int chunkScanValue6,
         int farmRedstoneLimit
      ) {
         this(chunkPos2, chunkScanValue, chunkType, confidence2, chunkScanValue2, chunkScanValue3, chunkScanValue4, chunkScanValue5, chunkScanValue6, farmRedstoneLimit, null);
      }
   }

   public static enum ChunkType {
      STORAGE,
      FARM,
      REDSTONE,
      MIXED;
   }

   public static enum Confidence {
      LOW,
      MEDIUM,
      HIGH;
   }

   private static final class RebuildJob {
      final boolean stateFlagged;
      final DivPenUtilsFlag detectionSettings;
      final List<SensorBlock> sensorBlocks;
      final List<CraftedExcavation> excavations;
      final int baselineEvidenceTotal;
      final String dimensionId;
      volatile List<ExcavationCluster> craftedGroups;
      final int generation;

      RebuildJob(
         String text, int value, DivPenUtilsFlag utils, boolean flag, int value2, List<SensorBlock> items, List<CraftedExcavation> items2
      ) {
         this.dimensionId = text;
         this.generation = value;
         this.detectionSettings = utils;
         this.stateFlagged = flag;
         this.baselineEvidenceTotal = value2;
         this.sensorBlocks = items;
         this.excavations = items2;
      }
   }
}
