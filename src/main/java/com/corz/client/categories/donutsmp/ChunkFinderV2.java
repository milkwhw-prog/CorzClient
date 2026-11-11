package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import com.corz.client.utils.UnreflectReference;
import ai.auth.Module;
import com.corz.client.utils.ChunkScanScheduler;
import com.corz.client.utils.IconsPngType;

public class ChunkFinderV2 extends Module {
   private final int[] belowZeroAirRunLengths;
   private final Mutable scratchPos;
   private static final int[] NEIGHBOR_OFFSET_Y = new int[]{0, 0, 1, -1, 0, 0};
   private static final byte UNUSED_BYTE_FOUR = 4;
   private final ByteArrayList collectedMarkerTypes;
   private int lightGridOriginZ;
   private static final byte UNUSED_BYTE_THREE = 3;
   private int settingsSignature;
   private static final byte UNUSED_BYTE_ONE = 1;
   private final byte[] lightLevelTable;
   private int nearbyMarkerCount;
   private static final int BLUE_BOX_COLOR = packArgb(60, 180, 100, 255);
   private int cleanupTickCounter;
   private long[] chunkKeyBuffer;
   private int lightGridOriginY;
   private static final int GREEN_PLAYER_COLOR = packArgb(60, 0, 255, 80);
   private static final byte UNUSED_BYTE_FIVE = 5;
   private final double[] richSpotZ;
   private final BooleanSetting highlightBlocksSetting;
   private static final int[] NEIGHBOR_OFFSET_X = new int[]{1, -1, 0, 0, 0, 0};
   private final LongArrayFIFOQueue recentAlertTimes;
   private boolean deepslateEnabled;
   private static final byte UNUSED_BYTE_TWO = 2;
   private final long[] nearbyMarkerPositions;
   private final Long2LongOpenHashMap alertTimesByChunk;
   private static final int ORANGE_BOX_COLOR = packArgb(60, 255, 120, 0);
   private final LongOpenHashSet farmChunkKeys;
   private int amethystSampleCount;
   private final int[] playerChunkSamples;
   private final EnumSetting notificationsSetting;
   private final BooleanSetting playerChunks;
   private int sensitivity;
   private static final int GOLD_BOX_COLOR = packArgb(120, 255, 215, 0);
   private final LongOpenHashSet amethystChunkKeys;
   private final Long2ObjectOpenHashMap<ChunkFinderV2.ChunkAnalysis> analysisByChunk;
   private final Long2ObjectOpenHashMap<ChunkFinderV2.EntitySignals> entitySignalsByChunk;
   private boolean playerChunksEnabled;
   private long[] distanceSortBuffer;
   private int entityScanTickCounter;
   private int lastCenterChunkZ;
   private final ChunkScanScheduler chunkScanner;
   private final int[] deepslateChunkSamples;
   private final IntArrayList amethystBudPositions;
   private static final Reference2IntOpenHashMap<Block> BLOCK_CATEGORY_FLAGS = new Reference2IntOpenHashMap();
   private boolean cobbledEnabled;
   private static final Direction[] ALL_DIRECTIONS = Direction.values();
   private static final int[] MARKER_COLORS = new int[]{
      packArgb(255, 100, 100, 100), packArgb(255, 80, 80, 80), packArgb(255, 120, 0, 120), packArgb(255, 255, 255, 200)
   };
   private final LongOpenHashSet playerChunkKeys;
   private final byte[] geodeBlockKindGrid;
   private static ChunkFinderV2 singleton;
   private final BooleanSetting richWaypoint;
   private final int[] amethystChunkSamples;
   private final LongOpenHashSet deepslateChunkKeys;
   private boolean farmsEnabled;
   private final double[] richSpotX;
   private boolean endStoneEnabled;
   private final UnreflectReference paletteReader;
   private final BooleanSetting endStoneSetting;
   private final ChunkFinderV2.Region region;
   private final int[] oreRunLengths;
   private int playerSampleCount;
   private final LongArrayList collectedMarkerPositions;
   private final BooleanSetting amethystSetting = new BooleanSetting("Amethyst", "Purple chunks: geodes a player has stripped or lit up (amethyst farms).", true);
   private int lastCenterChunkX;
   private final byte[] nearbyMarkerTypes;
   private final IntegerSetting sensitivitySetting;
   private final int[] airRunLengths;
   private boolean rotatedEnabled;
   private final BooleanSetting soundAlerts;
   private final BooleanSetting cobbledDeepslateSetting;
   private final int[] farmChunkSamples;
   private int worldIdentityHash;
   private final double[] richSpotY;
   private final BooleanSetting deepslateSetting;
   private boolean amethystEnabled;
   private final BooleanSetting farmsRedstone;
   private int[] paletteScratchBuffer;
   private final BooleanSetting rotatedDeepslateSetting;
   private boolean markersDirty;
   private int deepslateSampleCount;
   private static final int[] NEIGHBOR_OFFSET_Z = new int[]{0, 0, 0, 0, 1, -1};
   private int farmSampleCount;
   private int lightGridOriginX;
   private final DoubleSetting renderHeightSetting;
   private int recomputeTickCounter;

   private double getMaxGeodeMeanDistance() {
      return 6.0 + (this.sensitivity - 1) * 0.44444445;
   }

   private int getMagicAmethystThreshold() {
      return Math.max(2, 12 + (5 - this.sensitivity) * 2);
   }

   private static boolean isStorageOrCraftingBlock(Block block) {
      return block == Blocks.CHEST
         || block == Blocks.TRAPPED_CHEST
         || block == Blocks.BARREL
         || block == Blocks.ENDER_CHEST
         || block == Blocks.FURNACE
         || block == Blocks.BLAST_FURNACE
         || block == Blocks.SMOKER
         || block == Blocks.CRAFTING_TABLE
         || block == Blocks.ENCHANTING_TABLE
         || block == Blocks.ANVIL
         || block == Blocks.CHIPPED_ANVIL
         || block == Blocks.DAMAGED_ANVIL;
   }

   private static boolean isFarmRelatedBlock(BlockState blockState) {
      return getFarmBlockKind(blockState) != 0;
   }

   private static boolean removeDistantChunks(LongOpenHashSet longOpenHashSet, ChunkPos chunkPos, int iteratorValue) {
      boolean iterator = false;
      LongIterator iterator2 = longOpenHashSet.iterator();

      while (iterator2.hasNext()) {
         if (isBeyondChunkRange(iterator2.nextLong(), chunkPos, iteratorValue)) {
            iterator2.remove();
            iterator = true;
         }
      }

      return iterator;
   }

   public static void onBlockStateChanged(BlockPos pos2, BlockState state, BlockState blockState) {
      ChunkFinderV2 singleton2 = singleton;
      if (singleton2 != null && state != blockState && singleton2.isEnabled()) {
         int chunkHashValue = pos2.getX() >> 4;
         int chunkHashValue2 = pos2.getZ() >> 4;

         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               singleton2.chunkScanner.shortenRescanDelay(chunkHashValue + i, chunkHashValue2 + j, 1500L);
            }
         }
      }
   }

   private static void registerBlockFlags(int referenceHashValue, Block... blocks) {
      for (Block block : blocks) {
         BLOCK_CATEGORY_FLAGS.put(block, BLOCK_CATEGORY_FLAGS.getInt(block) | referenceHashValue);
      }
   }

   public ChunkFinderV2() {
      super("ChunkFinderV2", IconsPngType.DONUTSMP);
      this.playerChunks = new BooleanSetting(
         "Player Chunks", "Green chunks: signs of someone living underground - mined shafts, hidden ore, chests, obsidian, pistons.", true
      );
      this.farmsRedstone = new BooleanSetting("Farms & Redstone", "Orange chunks: repeaters, beehives, plant farms and crafting storage.", false);
      this.deepslateSetting = new BooleanSetting("Deepslate", "Yellow chunks: deepslate sitting above its natural depth.", false);
      this.cobbledDeepslateSetting = new BooleanSetting("Cobbled Deepslate", "Yellow chunks: cobbled deepslate above Y 0 - somebody carried it up.", false);
      this.rotatedDeepslateSetting = new BooleanSetting(
         "Rotated Deepslate", "Yellow chunks: sideways deepslate. Natural deepslate is always upright, so a player placed it.", false
      );
      this.endStoneSetting = new BooleanSetting("End Stone", "Yellow chunks: end stone outside The End.", false);
      this.sensitivitySetting = new IntegerSetting(
         "Sensitivity", "How easily a chunk gets flagged. Higher = more chunks (and more false alarms). 5 is the tuned default.", 5, 1, 10
      );
      this.renderHeightSetting = new DoubleSetting("Render Height", "Height the chunk highlights are drawn at.", 64.0, -64.0, 320.0);
      this.highlightBlocksSetting = new BooleanSetting("Highlight Blocks", "Outline the nearby deepslate / end stone blocks that got a yellow chunk flagged.", true);
      this.richWaypoint = new BooleanSetting("Rich Waypoint", "Draw one mine-down beam at the richest spot of every green player chunk.", true);
      this.notificationsSetting = new EnumSetting("Notifications", "Where new finds are announced.", "Both", "Both", "Toast", "Chat", "Off");
      this.soundAlerts = new BooleanSetting("Sound Alerts", "Play a sound when a new chunk is found.", true);
      this.deepslateChunkKeys = new LongOpenHashSet();
      this.playerChunkKeys = new LongOpenHashSet();
      this.amethystChunkKeys = new LongOpenHashSet();
      this.farmChunkKeys = new LongOpenHashSet();
      this.analysisByChunk = new Long2ObjectOpenHashMap();
      this.entitySignalsByChunk = new Long2ObjectOpenHashMap();
      this.alertTimesByChunk = new Long2LongOpenHashMap();
      this.recentAlertTimes = new LongArrayFIFOQueue();
      this.chunkScanner = new ChunkScanScheduler();
      this.region = new ChunkFinderV2.Region();
      this.sensitivity = 5;
      this.settingsSignature = -1;
      this.lastCenterChunkX = Integer.MIN_VALUE;
      this.lastCenterChunkZ = Integer.MIN_VALUE;
      this.deepslateChunkSamples = new int[100];
      this.playerChunkSamples = new int[100];
      this.amethystChunkSamples = new int[100];
      this.farmChunkSamples = new int[100];
      this.richSpotX = new double[50];
      this.richSpotY = new double[50];
      this.richSpotZ = new double[50];
      this.nearbyMarkerPositions = new long[200];
      this.nearbyMarkerTypes = new byte[200];
      this.distanceSortBuffer = new long[256];
      this.chunkKeyBuffer = new long[256];
      this.collectedMarkerPositions = new LongArrayList();
      this.collectedMarkerTypes = new ByteArrayList();
      this.airRunLengths = new int[256];
      this.belowZeroAirRunLengths = new int[256];
      this.oreRunLengths = new int[256];
      this.geodeBlockKindGrid = new byte[28928];
      this.amethystBudPositions = new IntArrayList();
      this.scratchPos = new Mutable();
      this.paletteReader = new UnreflectReference();
      this.paletteScratchBuffer = new int[64];
      this.lightLevelTable = new byte[9261];
      this.alertTimesByChunk.defaultReturnValue(Long.MIN_VALUE);
      singleton = this;
      this.addSettings(
         new ConfigSetting[]{
            this.amethystSetting,
            this.playerChunks,
            this.farmsRedstone,
            this.deepslateSetting,
            this.cobbledDeepslateSetting,
            this.rotatedDeepslateSetting,
            this.endStoneSetting,
            this.sensitivitySetting,
            this.renderHeightSetting,
            this.highlightBlocksSetting,
            this.richWaypoint,
            this.notificationsSetting,
            this.soundAlerts
         }
      );
   }

   private static boolean isWaxedCopperMarker(BlockState blockState) {
      return blockState.isOf(Blocks.WAXED_COPPER_BLOCK) || blockState.isOf(Blocks.WAXED_OXIDIZED_COPPER) || blockState.isOf(Blocks.TUFF_BRICKS);
   }

   private static boolean isUprightDeepslate(BlockState blockState) {
      return blockState.isOf(Blocks.DEEPSLATE) && blockState.contains(Properties.AXIS) ? blockState.get(Properties.AXIS) == Axis.Y : false;
   }

   private void refreshSettings() {
      this.amethystEnabled = this.amethystSetting.isEnabled();
      this.playerChunksEnabled = this.playerChunks.isEnabled();
      this.farmsEnabled = this.farmsRedstone.isEnabled();
      this.deepslateEnabled = this.deepslateSetting.isEnabled();
      this.cobbledEnabled = this.cobbledDeepslateSetting.isEnabled();
      this.rotatedEnabled = this.rotatedDeepslateSetting.isEnabled();
      this.endStoneEnabled = this.endStoneSetting.isEnabled();
      this.sensitivity = this.sensitivitySetting.getValue();
      int value = this.sensitivity
         | (this.amethystEnabled ? 256 : 0)
         | (this.playerChunksEnabled ? 512 : 0)
         | (this.farmsEnabled ? 1024 : 0)
         | (this.deepslateEnabled ? 2048 : 0)
         | (this.cobbledEnabled ? 4096 : 0)
         | (this.rotatedEnabled ? 8192 : 0)
         | (this.endStoneEnabled ? 16384 : 0);
      if (value != this.settingsSignature) {
         if (this.settingsSignature != -1) {
            this.clearChunkSets();
         }

         this.settingsSignature = value;
      }
   }

   static {
      registerBlockFlags(1, Blocks.AIR, Blocks.CAVE_AIR);
      registerBlockFlags(2, Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE, Blocks.MOSSY_COBBLESTONE);
      registerBlockFlags(4, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.CHISELED_DEEPSLATE);
      registerBlockFlags(
         8,
         Blocks.STONE_STAIRS,
         Blocks.COBBLESTONE_STAIRS,
         Blocks.DEEPSLATE_BRICK_STAIRS,
         Blocks.POLISHED_DEEPSLATE_STAIRS,
         Blocks.OAK_STAIRS,
         Blocks.SPRUCE_STAIRS,
         Blocks.DARK_OAK_STAIRS,
         Blocks.COBBLED_DEEPSLATE_STAIRS
      );
      registerBlockFlags(16, Blocks.OBSIDIAN);
      registerBlockFlags(32, Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.MOVING_PISTON, Blocks.PISTON_HEAD);
      registerBlockFlags(64, Blocks.CHEST, Blocks.TRAPPED_CHEST);
      registerBlockFlags(
         128,
         Blocks.DIAMOND_ORE,
         Blocks.DEEPSLATE_DIAMOND_ORE,
         Blocks.IRON_ORE,
         Blocks.DEEPSLATE_IRON_ORE,
         Blocks.GOLD_ORE,
         Blocks.DEEPSLATE_GOLD_ORE,
         Blocks.EMERALD_ORE,
         Blocks.DEEPSLATE_EMERALD_ORE,
         Blocks.LAPIS_ORE,
         Blocks.DEEPSLATE_LAPIS_ORE,
         Blocks.REDSTONE_ORE,
         Blocks.DEEPSLATE_REDSTONE_ORE,
         Blocks.COAL_ORE,
         Blocks.DEEPSLATE_COAL_ORE,
         Blocks.COPPER_ORE,
         Blocks.DEEPSLATE_COPPER_ORE,
         Blocks.ANCIENT_DEBRIS
      );
      registerBlockFlags(256, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.ANCIENT_DEBRIS);
      registerBlockFlags(512, Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE, Blocks.STONE, Blocks.DEEPSLATE, Blocks.OBSIDIAN, Blocks.NETHERRACK);
   }

   @Override
   protected void onEnable() {
      this.resetAllState();
   }

   @Override
   protected void onDisable() {
      this.resetAllState();
   }

   private void resetAllState() {
      this.clearChunkSets();
      this.entitySignalsByChunk.clear();
      this.alertTimesByChunk.clear();
      this.recentAlertTimes.clear();
      this.entityScanTickCounter = 0;
      this.cleanupTickCounter = 0;
      this.recomputeTickCounter = 0;
      this.settingsSignature = -1;
   }

   private int isStoneLikeAt(WorldChunk worldChunk, int blockMutableValue, int blockMutableValue2, int blockMutableValue3) {
      return BLOCK_CATEGORY_FLAGS.getInt(worldChunk.getBlockState(this.scratchPos.set(blockMutableValue, blockMutableValue2, blockMutableValue3)).getBlock()) >>> 9 & 1;
   }

   private void scanDeepslateMarkers(WorldChunk worldChunk, ChunkFinderV2.ChunkAnalysis chunkAnalysis) {
      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      int startX = worldChunk.getPos().getStartX();
      int startZ = worldChunk.getPos().getStartZ();
      int bottomY = worldChunk.getBottomY();
      boolean unreflectDeepslateFlag = mc.world.getRegistryKey() == World.END;
      boolean deepslateFlag = this.deepslateEnabled;
      boolean rotatedEnabled2 = this.rotatedEnabled;
      boolean deepslateCobbledFlag = this.cobbledEnabled;
      boolean unreflectDeepslateFlag2 = this.endStoneEnabled && !unreflectDeepslateFlag;
      Predicate<BlockState> predicate = cobbled -> isWaxedCopperMarker(cobbled)
         || deepslateFlag && isUprightDeepslate(cobbled)
         || rotatedEnabled2 && isSidewaysDeepslate(cobbled)
         || deepslateCobbledFlag && ((net.minecraft.block.BlockState) cobbled).isOf(Blocks.COBBLED_DEEPSLATE)
         || unreflectDeepslateFlag2 && ((net.minecraft.block.BlockState) cobbled).isOf(Blocks.END_STONE);
      boolean unreflectDeepslateFlag3 = false;

      for (int i = 0; i < sectionArray.length; i++) {
         ChunkSection chunkSection = sectionArray[i];
         if (chunkSection != null && !chunkSection.isEmpty()) {
            int unreflectDeepslateValue = bottomY + i * 16;
            if (unreflectDeepslateValue <= 128 && unreflectDeepslateValue + 15 >= 0 && chunkSection.hasAny(predicate)) {
               if (!unreflectDeepslateFlag3) {
                  this.region.resetAround(worldChunk);
                  unreflectDeepslateFlag3 = true;
               }

               this.paletteReader.handleFlag(chunkSection);
               int[] unreflectDeepslateValues = this.paletteReader.unreflectReferenceTable;
               int[] paletteScratch = this.getPaletteScratch(this.paletteReader.paletteSize);

               for (int j = 0; j < this.paletteReader.paletteSize; j++) {
                  BlockState blockState = this.paletteReader.copyOf[j];
                  int unreflectDeepslateValue2 = 0;
                  if (!blockState.isAir()) {
                     if (isWaxedCopperMarker(blockState)) {
                        unreflectDeepslateValue2 |= 1;
                     }

                     if (deepslateFlag && isUprightDeepslate(blockState)) {
                        unreflectDeepslateValue2 |= 2;
                     }

                     if (rotatedEnabled2 && isSidewaysDeepslate(blockState)) {
                        unreflectDeepslateValue2 |= 4;
                     }

                     if (deepslateCobbledFlag && blockState.isOf(Blocks.COBBLED_DEEPSLATE)) {
                        unreflectDeepslateValue2 |= 8;
                     }

                     if (unreflectDeepslateFlag2 && blockState.isOf(Blocks.END_STONE)) {
                        unreflectDeepslateValue2 |= 16;
                     }
                  }

                  paletteScratch[j] = unreflectDeepslateValue2;
               }

               for (int k = 0; k < 16; k++) {
                  int unreflectDeepslateLimit = unreflectDeepslateValue + k;
                  if (unreflectDeepslateLimit >= 0 && unreflectDeepslateLimit <= 128) {
                     for (int m = 0; m < 16; m++) {
                        for (int p = 0; p < 16; p++) {
                           int unreflectDeepslateValue3 = paletteScratch[unreflectDeepslateValues[UnreflectReference.getValue(m, k, p)]];
                           if (unreflectDeepslateValue3 != 0) {
                              this.recordDeepslateMarker(startX + m, unreflectDeepslateLimit, startZ + p, unreflectDeepslateValue3, chunkAnalysis);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static int getGridIndex(int chunkFinderValue, int chunkFinderValue2, int q) {
      return (chunkFinderValue2 * 16 + chunkFinderValue) * 16 + q;
   }

   private int collectNearestChunks(LongOpenHashSet longOpenHashSet, ChunkPos chunkPos, int[] packedValues) {
      int outSize = longOpenHashSet.size();
      if (outSize == 0) {
         return 0;
      } else {
         if (this.distanceSortBuffer.length < outSize) {
            this.distanceSortBuffer = new long[outSize * 2];
         }

         if (this.chunkKeyBuffer.length < outSize) {
            this.chunkKeyBuffer = new long[outSize * 2];
         }

         int index = 0;

         for (LongIterator iterator = longOpenHashSet.iterator(); iterator.hasNext(); index++) {
            long randomLong = iterator.nextLong();
            long packedFactor = ChunkPos.getPackedX(randomLong) - chunkPos.x;
            long packedFactor2 = ChunkPos.getPackedZ(randomLong) - chunkPos.z;
            this.chunkKeyBuffer[index] = randomLong;
            this.distanceSortBuffer[index] = Math.min(packedFactor * packedFactor + packedFactor2 * packedFactor2, 2147483647L) << 32 | index;
         }

         Arrays.sort(this.distanceSortBuffer, 0, outSize);
         int timesPacked = Math.min(outSize, 50);

         for (int i = 0; i < timesPacked; i++) {
            long packedValue = this.chunkKeyBuffer[(int)this.distanceSortBuffer[i]];
            packedValues[i << 1] = ChunkPos.getPackedX(packedValue);
            packedValues[(i << 1) + 1] = ChunkPos.getPackedZ(packedValue);
         }

         return timesPacked;
      }
   }

   private int getCachedBlockLight(World world, int lightLevelValue, int y2, int z2) {
      int index = ((lightLevelValue - this.lightGridOriginX) * 21 + (y2 - this.lightGridOriginY)) * 21 + (z2 - this.lightGridOriginZ);
      int lightLevel = this.lightLevelTable[index];
      if (lightLevel < 0) {
         lightLevel = world.getLightLevel(LightType.BLOCK, this.scratchPos.set(lightLevelValue, y2, z2));
         this.lightLevelTable[index] = (byte)lightLevel;
      }

      return lightLevel;
   }

   private boolean shouldShowBlockHighlights() {
      return this.highlightBlocksSetting.isEnabled() && (this.deepslateEnabled || this.cobbledEnabled || this.rotatedEnabled || this.endStoneEnabled);
   }

   private static boolean isBeyondChunkRange(long packedValue, ChunkPos chunkPos, int iteratorValue) {
      return Math.abs(ChunkPos.getPackedX(packedValue) - chunkPos.x) > iteratorValue || Math.abs(ChunkPos.getPackedZ(packedValue) - chunkPos.z) > iteratorValue;
   }

   private int computePlayerActivityScore(ChunkFinderV2.ChunkAnalysis chunkAnalysis, List<String> list) {
      int veinBear = 0;
      if (chunkAnalysis.cobbleLineCount >= 3) {
         veinBear += Math.min(chunkAnalysis.cobbleLineCount, 6);
         if (list != null) {
            list.add("CobbleLine(" + chunkAnalysis.cobbleLineCount + ")");
         }
      }

      if (chunkAnalysis.polishedDeepslateCount >= 4) {
         veinBear += Math.min(chunkAnalysis.polishedDeepslateCount / 2, 5);
         if (list != null) {
            list.add("Deepslate(" + chunkAnalysis.polishedDeepslateCount + ")");
         }
      }

      if (chunkAnalysis.obsidianCount >= 3) {
         veinBear += Math.min(chunkAnalysis.obsidianCount, 6);
         if (list != null) {
            list.add("Obsidian(" + chunkAnalysis.obsidianCount + ")");
         }
      }

      if (chunkAnalysis.rareOreCount >= 1 && chunkAnalysis.storageBlockCount >= 1) {
         veinBear += 4;
         if (list != null) {
            list.add("VeinInt");
         }
      }

      if (chunkAnalysis.verticalVeinCount >= 2) {
         veinBear += chunkAnalysis.verticalVeinCount;
         if (list != null) {
            list.add("VertVein(" + chunkAnalysis.verticalVeinCount + ")");
         }
      }

      if (chunkAnalysis.needleCount >= 2) {
         veinBear += chunkAnalysis.needleCount * 2;
         if (list != null) {
            list.add("Needle(" + chunkAnalysis.needleCount + ")");
         }
      }

      if (chunkAnalysis.hiddenOreCount >= 2) {
         veinBear += chunkAnalysis.hiddenOreCount;
         if (list != null) {
            list.add("HiddenOre(" + chunkAnalysis.hiddenOreCount + ")");
         }
      }

      if (chunkAnalysis.pistonArrayCount >= 3) {
         veinBear += Math.min(chunkAnalysis.pistonArrayCount, 8);
         if (list != null) {
            list.add("PistonArray(" + chunkAnalysis.pistonArrayCount + ")");
         }
      }

      if (chunkAnalysis.mobClusterScore >= 2) {
         veinBear += chunkAnalysis.mobClusterScore;
         if (list != null) {
            list.add("MobCluster(" + chunkAnalysis.mobClusterScore * 5 + ")");
         }
      }

      if (chunkAnalysis.droppedItemCount >= 2) {
         veinBear += chunkAnalysis.droppedItemCount * 2;
         if (list != null) {
            list.add("Dropped(" + chunkAnalysis.droppedItemCount + ")");
         }
      }

      if (chunkAnalysis.airPocketScore >= 5) {
         veinBear += chunkAnalysis.airPocketScore / 3;
         if (list != null) {
            list.add("AirPocket(" + chunkAnalysis.airPocketScore + ")");
         }
      }

      if (chunkAnalysis.stairsCount >= 5) {
         veinBear += chunkAnalysis.stairsCount / 3;
         if (list != null) {
            list.add("Stairs(" + chunkAnalysis.stairsCount + ")");
         }
      }

      if (chunkAnalysis.polarBearScore > 0) {
         veinBear += chunkAnalysis.polarBearScore;
         if (list != null) {
            list.add("PolarBear");
         }
      }

      if (chunkAnalysis.chestCount >= 10) {
         veinBear += Math.min(chunkAnalysis.chestCount, 12);
         if (list != null) {
            list.add("ChestCluster(" + chunkAnalysis.chestCount + ")");
         }
      }

      if (chunkAnalysis.storageBlockCount >= 3) {
         veinBear += chunkAnalysis.storageBlockCount;
         if (list != null) {
            list.add("Storage(" + chunkAnalysis.storageBlockCount + ")");
         }
      }

      return veinBear;
   }

   private void renderChunkBoxes(Matrix4f matrix4f, int[] chunkFinderValues, int chunkFinderLimit, int chunkFinderValue, double vertexBuilderValue) {
      if (chunkFinderLimit != 0) {
         float susChunkValue = (chunkFinderValue >>> 24 & 0xFF) / 255.0F;
         float chunkFinderValue4 = (chunkFinderValue >> 16 & 0xFF) / 255.0F;
         float chunkFinderValue5 = (chunkFinderValue >> 8 & 0xFF) / 255.0F;
         float chunkFinderValue6 = (chunkFinderValue & 0xFF) / 255.0F;
         int n2 = 0xFF000000 | chunkFinderValue & 16777215;
         float f2 = VertexBuilderUtils.getAmount(vertexBuilderValue);
         float f5 = f2 + 0.3F;

         for (int i = 0; i < chunkFinderLimit; i++) {
            double chunkFinderValue10 = chunkFinderValues[i << 1] << 4;
            double chunkFinderValue11 = chunkFinderValues[(i << 1) + 1] << 4;
            if (!VertexBuilderUtils.canVector(chunkFinderValue10 + 8.0, vertexBuilderValue, chunkFinderValue11 + 8.0, 12.0F)) {
               float amountX = VertexBuilderUtils.getAmountX(chunkFinderValue10);
               float amountForDouble = VertexBuilderUtils.getAmountForDouble(chunkFinderValue11);
               VertexBuilderUtils.handle(matrix4f, amountX, f2, amountForDouble, amountX + 16.0F, f5, amountForDouble + 16.0F, chunkFinderValue4, chunkFinderValue5, chunkFinderValue6, susChunkValue);
               VertexBuilderUtils.handle(matrix4f, amountX, f2, amountForDouble, amountX + 16.0F, f5, amountForDouble + 16.0F, n2, 1.5F);
            }
         }
      }
   }

   private static int packArgb(int chunkFinderValue, int chunkFinderValue2, int chunkFinderValue3, int chunkFinderValue4) {
      return (chunkFinderValue & 0xFF) << 24 | (chunkFinderValue2 & 0xFF) << 16 | (chunkFinderValue3 & 0xFF) << 8 | chunkFinderValue4 & 0xFF;
   }

   private double getMinGeodeBudDensity() {
      return 0.015 - (this.sensitivity - 1) * 0.0013333333;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null) {
         if (this.deepslateSampleCount + this.playerSampleCount + this.amethystSampleCount + this.farmSampleCount + this.nearbyMarkerCount != 0) {
            Matrix4f matrix = VertexBuilderUtils.getMatrix();
            double chunkFinderValue = this.renderHeightSetting.getValue();
            this.renderChunkBoxes(matrix, this.deepslateChunkSamples, this.deepslateSampleCount, GOLD_BOX_COLOR, chunkFinderValue);
            this.renderChunkBoxes(matrix, this.playerChunkSamples, this.playerSampleCount, GREEN_PLAYER_COLOR, chunkFinderValue);
            this.renderChunkBoxes(matrix, this.amethystChunkSamples, this.amethystSampleCount, BLUE_BOX_COLOR, chunkFinderValue);
            this.renderChunkBoxes(matrix, this.farmChunkSamples, this.farmSampleCount, ORANGE_BOX_COLOR, chunkFinderValue);
            if (this.richWaypoint.isEnabled()) {
               this.renderRichSpotBeams(matrix);
            }

            if (this.highlightBlocksSetting.isEnabled()) {
               this.renderMarkerBoxes(matrix);
            }

            VertexBuilderUtils.updateDrawImmediate();
         }
      }
   }

   private void rebuildNearestChunkLists(ChunkPos chunkPos) {
      this.markersDirty = false;
      this.lastCenterChunkX = chunkPos.x;
      this.lastCenterChunkZ = chunkPos.z;
      this.deepslateSampleCount = this.collectNearestChunks(this.deepslateChunkKeys, chunkPos, this.deepslateChunkSamples);
      this.playerSampleCount = this.collectNearestChunks(this.playerChunkKeys, chunkPos, this.playerChunkSamples);
      this.amethystSampleCount = this.collectNearestChunks(this.amethystChunkKeys, chunkPos, this.amethystChunkSamples);
      this.farmSampleCount = this.collectNearestChunks(this.farmChunkKeys, chunkPos, this.farmChunkSamples);

      for (int i = 0; i < this.playerSampleCount; i++) {
         int arrayByteValue = this.playerChunkSamples[i << 1];
         int arrayByteValue2 = this.playerChunkSamples[(i << 1) + 1];
         ChunkFinderV2.ChunkAnalysis long2objectopenhashmap = (ChunkFinderV2.ChunkAnalysis)this.analysisByChunk.get(ChunkPos.toLong(arrayByteValue, arrayByteValue2));
         if (long2objectopenhashmap != null && long2objectopenhashmap.totalWeight > 0.0) {
            this.richSpotX[i] = long2objectopenhashmap.weightedXSum / long2objectopenhashmap.totalWeight;
            this.richSpotZ[i] = long2objectopenhashmap.weightedZSum / long2objectopenhashmap.totalWeight;
            this.richSpotY[i] = long2objectopenhashmap.weightedYSum / long2objectopenhashmap.totalWeight;
         } else {
            this.richSpotX[i] = (arrayByteValue << 4) + 8.0;
            this.richSpotZ[i] = (arrayByteValue2 << 4) + 8.0;
            this.richSpotY[i] = Double.NaN;
         }
      }

      this.nearbyMarkerCount = 0;
      if (this.shouldShowBlockHighlights()) {
         this.collectedMarkerPositions.clear();
         this.collectedMarkerTypes.clear();

         for (int j = -5; j <= 5; j++) {
            for (int k = -5; k <= 5; k++) {
               ChunkFinderV2.ChunkAnalysis long2objectopenhashmap2 = (ChunkFinderV2.ChunkAnalysis)this.analysisByChunk
                  .get(ChunkPos.toLong(chunkPos.x + j, chunkPos.z + k));
               if (long2objectopenhashmap2 != null && long2objectopenhashmap2.markerPositions != null) {
                  this.collectedMarkerPositions.addAll(long2objectopenhashmap2.markerPositions);
                  this.collectedMarkerTypes.addAll(long2objectopenhashmap2.markerTypes);
               }
            }
         }

         int longarraylistSize = this.collectedMarkerPositions.size();
         if (longarraylistSize != 0) {
            if (this.distanceSortBuffer.length < longarraylistSize) {
               this.distanceSortBuffer = new long[longarraylistSize * 2];
            }

            int blockX = mc.player.getBlockX();
            int blockY = mc.player.getBlockY();
            int blockZ = mc.player.getBlockZ();

            for (int m = 0; m < longarraylistSize; m++) {
               long long2 = this.collectedMarkerPositions.getLong(m);
               long arrayByteFactor = BlockPos.unpackLongX(long2) - blockX;
               long arrayByteFactor2 = BlockPos.unpackLongY(long2) - blockY;
               long arrayByteFactor3 = BlockPos.unpackLongZ(long2) - blockZ;
               long min3 = Math.min(arrayByteFactor * arrayByteFactor + arrayByteFactor2 * arrayByteFactor2 + arrayByteFactor3 * arrayByteFactor3, 2147483647L);
               this.distanceSortBuffer[m] = min3 << 32 | m;
            }

            Arrays.sort(this.distanceSortBuffer, 0, longarraylistSize);
            int min = Math.min(longarraylistSize, 200);

            for (int p = 0; p < min; p++) {
               int arrayByteValue4 = (int)this.distanceSortBuffer[p];
               this.nearbyMarkerPositions[p] = this.collectedMarkerPositions.getLong(arrayByteValue4);
               this.nearbyMarkerTypes[p] = this.collectedMarkerTypes.getByte(arrayByteValue4);
            }

            this.nearbyMarkerCount = min;
         }
      }
   }

   private void scanFarmBlocks(WorldChunk worldChunk, ChunkFinderV2.ChunkAnalysis chunkAnalysis) {
      for (BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
         BlockState blockState = worldChunk.getBlockState(blockEntity.getPos());
         if (blockState.isOf(Blocks.BEEHIVE) || blockState.isOf(Blocks.BEE_NEST)) {
            chunkAnalysis.beehiveCount++;
            if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
               chunkAnalysis.beeCount = chunkAnalysis.beeCount + Math.max(0, beehiveBlockEntity.getBeeCount());
            }
         } else if (isStorageOrCraftingBlock(blockState.getBlock())) {
            chunkAnalysis.craftingStorageCount++;
         }
      }

      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      int bottomY = worldChunk.getBottomY();

      for (int i = 0; i < sectionArray.length; i++) {
         ChunkSection chunkSection = sectionArray[i];
         if (chunkSection != null && !chunkSection.isEmpty() && chunkSection.hasAny(ChunkFinderV2::isFarmRelatedBlock)) {
            int unreflectReferenceValue = bottomY + i * 16;
            this.paletteReader.handleFlag(chunkSection);
            int[] unreflectReferenceValues = this.paletteReader.unreflectReferenceTable;
            int[] paletteScratch = this.getPaletteScratch(this.paletteReader.paletteSize);

            for (int j = 0; j < this.paletteReader.paletteSize; j++) {
               paletteScratch[j] = getFarmBlockKind(this.paletteReader.copyOf[j]);
            }

            for (int k = 0; k < 4096; k++) {
               int unreflectReferenceValue2 = paletteScratch[unreflectReferenceValues[k]];
               if (unreflectReferenceValue2 != 0) {
                  int unreflectReferenceLimit = unreflectReferenceValue + (k >> 8);
                  switch (unreflectReferenceValue2) {
                     case 1:
                        chunkAnalysis.repeaterCount++;
                        break;
                     case 2:
                        chunkAnalysis.repeaterCount++;
                        chunkAnalysis.poweredRepeaterCount++;
                        break;
                     case 3:
                        chunkAnalysis.vineCount++;
                        break;
                     case 4:
                        if (unreflectReferenceLimit >= 0 && unreflectReferenceLimit <= 20) {
                           chunkAnalysis.lowCobbledDeepslateCount++;
                        }
                        break;
                     case 5:
                        if (unreflectReferenceLimit <= 70) {
                           chunkAnalysis.seagrassCount++;
                        }
                        break;
                     case 6:
                        if (unreflectReferenceLimit <= 70) {
                           chunkAnalysis.flowerCount++;
                        }
                        break;
                     case 7:
                        chunkAnalysis.craftingStorageCount++;
                  }
               }
            }
         }
      }
   }

   private void recordDeepslateMarker(int deepslateCobbledValue, int chunkFinderLimit, int deepslateCobbledValue3, int deepslateCobbledValue4, ChunkFinderV2.ChunkAnalysis chunkAnalysis) {
      if ((deepslateCobbledValue4 & 1) != 0) {
         chunkAnalysis.waxedCopperCount++;
      }

      if ((deepslateCobbledValue4 & -2) != 0) {
         if (!this.isExposedToAirOrFluid(deepslateCobbledValue, chunkFinderLimit, deepslateCobbledValue3)) {
            byte deepslateCobbledLimit = -1;
            if ((deepslateCobbledValue4 & 2) != 0 && !this.hasLongDeepslateLine(deepslateCobbledValue, chunkFinderLimit, deepslateCobbledValue3)) {
               chunkAnalysis.misplacedDeepslateCount++;
               deepslateCobbledLimit = 0;
            }

            if ((deepslateCobbledValue4 & 4) != 0) {
               chunkAnalysis.sidewaysDeepslateCount++;
               deepslateCobbledLimit = 2;
            }

            if ((deepslateCobbledValue4 & 8) != 0) {
               chunkAnalysis.cobbledDeepslateCount++;
               deepslateCobbledLimit = 1;
            }

            if ((deepslateCobbledValue4 & 16) != 0) {
               chunkAnalysis.endStoneCount++;
               deepslateCobbledLimit = 3;
            }

            if (deepslateCobbledLimit >= 0) {
               chunkAnalysis.addMarker(BlockPos.asLong(deepslateCobbledValue, chunkFinderLimit, deepslateCobbledValue3), deepslateCobbledLimit);
            }
         }
      }
   }

   private int[] getPaletteScratch(int chunkFinderLimit) {
      if (this.paletteScratchBuffer.length < chunkFinderLimit) {
         this.paletteScratchBuffer = new int[Math.max(chunkFinderLimit, this.paletteScratchBuffer.length * 2)];
      }

      return this.paletteScratchBuffer;
   }

   private int computeFarmScore(ChunkFinderV2.ChunkAnalysis chunkAnalysis) {
      byte repeaterChunkValue = 0;
      int repeaterChunkValue2 = repeaterChunkValue + Math.min(90, chunkAnalysis.beehiveCount * 22 + chunkAnalysis.beeCount * 10);
      int repeaterChunkValue3 = repeaterChunkValue2 + Math.min(65, chunkAnalysis.lowCobbledDeepslateCount / 2);
      int repeaterChunkValue4 = repeaterChunkValue3 + Math.min(55, chunkAnalysis.vineCount / 4);
      int repeaterChunkValue5 = repeaterChunkValue4 + Math.min(70, chunkAnalysis.seagrassCount);
      int repeaterChunkValue6 = repeaterChunkValue5 + Math.min(75, chunkAnalysis.flowerCount * 12);
      int repeaterChunkValue7 = repeaterChunkValue6 + Math.min(90, chunkAnalysis.repeaterCount * 16 + chunkAnalysis.poweredRepeaterCount * 28);
      return repeaterChunkValue7 + Math.min(100, chunkAnalysis.craftingStorageCount * 18);
   }

   private static boolean isAmethystGrowth(BlockState blockState) {
      return blockState.isOf(Blocks.AMETHYST_CLUSTER)
         || blockState.isOf(Blocks.LARGE_AMETHYST_BUD)
         || blockState.isOf(Blocks.MEDIUM_AMETHYST_BUD)
         || blockState.isOf(Blocks.SMALL_AMETHYST_BUD)
         || blockState.isOf(Blocks.AMETHYST_BLOCK);
   }

   private boolean isExposedToAirOrFluid(int bottomHeightValue, int chunkFinderLimit, int bottomHeightValue3) {
      int bottomY = mc.world.getBottomY();
      int height = mc.world.getHeight();

      for (int i = 0; i < 6; i++) {
         int bottomHeightLimit = chunkFinderLimit + NEIGHBOR_OFFSET_Y[i];
         if (bottomHeightLimit >= bottomY && bottomHeightLimit < height) {
            BlockState chunkState = this.region.getChunkState(bottomHeightValue + NEIGHBOR_OFFSET_X[i], bottomHeightLimit, bottomHeightValue3 + NEIGHBOR_OFFSET_Z[i]);
            if (chunkState.isAir()) {
               return true;
            }

            if (!chunkState.getFluidState().isEmpty()) {
               return true;
            }
         }
      }

      return false;
   }

   private void clearChunkSets() {
      this.deepslateChunkKeys.clear();
      this.playerChunkKeys.clear();
      this.amethystChunkKeys.clear();
      this.farmChunkKeys.clear();
      this.analysisByChunk.clear();
      this.chunkScanner.clearScannedChunks();
      this.deepslateSampleCount = this.playerSampleCount = this.amethystSampleCount = this.farmSampleCount = this.nearbyMarkerCount = 0;
      this.markersDirty = true;
   }

   private double getMaxGeodeAirRatio() {
      return 0.08 + (this.sensitivity - 1) * 0.027;
   }

   private void announceFinding(ChunkPos chunkPos, long bothFinderValue, String bothFinderText) {
      long currentMillis = System.currentTimeMillis();
      if (this.recentAlertTimes.size() < 5) {
         long long2longopenhashmap = this.alertTimesByChunk.get(bothFinderValue);
         if (long2longopenhashmap == Long.MIN_VALUE || currentMillis - long2longopenhashmap >= 300000L) {
            String formatText = this.notificationsSetting.getCurrentOption();
            boolean booleanValue = this.soundAlerts.isEnabled();
            if (!formatText.equals("Off") || booleanValue) {
               String format = String.format("ChunkFinder [X %d, Z %d] - %s", chunkPos.getStartX() + 8, chunkPos.getStartZ() + 8, bothFinderText);
               if ((formatText.equals("Chat") || formatText.equals("Both")) && mc.player != null) {
                  mc.player.sendMessage(Text.literal(format), false);
               }

               if (formatText.equals("Toast") || formatText.equals("Both")) {
                  DisabledEnabled.getInstance().handleDisabledEnabled("ChunkFinder", format, true);
               }

               if (booleanValue) {
                  mc.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5F, 1.0F));
               }

               this.recentAlertTimes.enqueue(currentMillis);
               this.alertTimesByChunk.put(bothFinderValue, currentMillis);
            }
         }
      }
   }

   private static int getFarmBlockKind(BlockState blockState) {
      if (blockState.isAir()) {
         return 0;
      } else if (!blockState.isOf(Blocks.REPEATER)) {
         if (blockState.isOf(Blocks.VINE)) {
            return 3;
         } else if (blockState.isOf(Blocks.COBBLED_DEEPSLATE)) {
            return 4;
         } else if (blockState.isOf(Blocks.SEAGRASS) || blockState.isOf(Blocks.TALL_SEAGRASS)) {
            return 5;
         } else if (blockState.isIn(BlockTags.FLOWERS)) {
            return 6;
         } else {
            return isStorageOrCraftingBlock(blockState.getBlock()) ? 7 : 0;
         }
      } else {
         return blockState.contains(Properties.POWERED) && blockState.get(Properties.POWERED) ? 2 : 1;
      }
   }

   private int getMinGeodeCalciteCount() {
      return (int)(15.0 - (this.sensitivity - 1) * 1.3333333333333333);
   }

   private int measureDeepslateLineLength(int bottomHeightValue, int chunkFinderLimit, int bottomHeightValue3, int bottomHeightFactor, int bottomHeightFactor2, int bottomHeightFactor3, int bottomHeightLimit) {
      int bottomY = mc.world.getBottomY();
      int height = mc.world.getHeight();
      int bottomHeight = 1;

      for (int i = 1; i >= -1; i -= 2) {
         for (int j = 1; j < bottomHeightLimit; j++) {
            int y2 = chunkFinderLimit + bottomHeightFactor2 * j * i;
            if (y2 < bottomY || y2 >= height || !isUprightDeepslate(this.region.getChunkState(bottomHeightValue + bottomHeightFactor * j * i, y2, bottomHeightValue3 + bottomHeightFactor3 * j * i))) {
               break;
            }

            bottomHeight++;
         }
      }

      return bottomHeight;
   }

   private ChunkFinderV2.GeodeResult detectGeode(WorldChunk worldChunk) {
      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      int bottomY = worldChunk.getBottomY();
      boolean unreflectReferenceFlag = false;

      for (int i = 0; i < sectionArray.length && !unreflectReferenceFlag; i++) {
         ChunkSection chunkSection = sectionArray[i];
         if (chunkSection != null && !chunkSection.isEmpty()) {
            int unreflectReferenceValue = bottomY + i * 16;
            if (unreflectReferenceValue <= 48 && unreflectReferenceValue + 15 >= -64) {
               unreflectReferenceFlag = chunkSection.hasAny(state -> isAmethystGrowth(state) || state.isOf(Blocks.BUDDING_AMETHYST));
            }
         }
      }

      if (!unreflectReferenceFlag) {
         return ChunkFinderV2.GeodeResult.getNoGeodeResult();
      } else {
         byte[] selectedBytes = this.geodeBlockKindGrid;
         Arrays.fill(selectedBytes, (byte)0);
         IntArrayList intArrayList = this.amethystBudPositions;
         intArrayList.clear();
         int unreflectReferenceCounter = 0;
         long unreflectReferenceTotal = 0L;
         long unreflectReferenceTotal2 = 0L;
         long unreflectReferenceTotal3 = 0L;

         for (int j = 0; j < sectionArray.length; j++) {
            ChunkSection chunkSection2 = sectionArray[j];
            if (chunkSection2 != null && !chunkSection2.isEmpty()) {
               int unreflectReferenceValue2 = bottomY + j * 16;
               if (unreflectReferenceValue2 <= 48 && unreflectReferenceValue2 + 15 >= -64) {
                  this.paletteReader.handleFlag(chunkSection2);
                  int[] unreflectReferenceValues = this.paletteReader.unreflectReferenceTable;
                  int[] paletteScratch = this.getPaletteScratch(this.paletteReader.paletteSize);

                  for (int k = 0; k < this.paletteReader.paletteSize; k++) {
                     BlockState blockState = this.paletteReader.copyOf[k];
                     byte unreflectReferenceValue3;
                     if (blockState.isOf(Blocks.BUDDING_AMETHYST)) {
                        unreflectReferenceValue3 = 6;
                     } else if (isAmethystGrowth(blockState)) {
                        unreflectReferenceValue3 = 1;
                     } else if (blockState.isOf(Blocks.CALCITE)) {
                        unreflectReferenceValue3 = 2;
                     } else if (blockState.isOf(Blocks.SMOOTH_BASALT)) {
                        unreflectReferenceValue3 = 3;
                     } else if (blockState.isAir()) {
                        unreflectReferenceValue3 = 4;
                     } else {
                        unreflectReferenceValue3 = 5;
                     }

                     paletteScratch[k] = unreflectReferenceValue3;
                  }

                  for (int m = 0; m < 16; m++) {
                     int unreflectReferenceLimit = unreflectReferenceValue2 + m;
                     if (unreflectReferenceLimit >= -64 && unreflectReferenceLimit <= 48) {
                        int unreflectReferenceValue4 = unreflectReferenceLimit - -64;

                        for (int p = 0; p < 16; p++) {
                           for (int q = 0; q < 16; q++) {
                              int unreflectReferenceValue5 = paletteScratch[unreflectReferenceValues[UnreflectReference.getValue(p, m, q)]];
                              if (unreflectReferenceValue5 == 6) {
                                 return ChunkFinderV2.GeodeResult.getBuddingFoundResult();
                              }

                              if (unreflectReferenceValue5 == 1) {
                                 intArrayList.add(p);
                                 intArrayList.add(unreflectReferenceLimit);
                                 intArrayList.add(q);
                                 unreflectReferenceTotal += p;
                                 unreflectReferenceTotal2 += unreflectReferenceLimit;
                                 unreflectReferenceTotal3 += q;
                              } else if (unreflectReferenceValue5 == 2 || unreflectReferenceValue5 == 3) {
                                 unreflectReferenceCounter++;
                              }

                              selectedBytes[getGridIndex(p, unreflectReferenceValue4, q)] = (byte)unreflectReferenceValue5;
                           }
                        }
                     }
                  }
               }
            }
         }

         int unreflectReferenceLimit2 = intArrayList.size() / 3;
         if (unreflectReferenceLimit2 == 0) {
            return ChunkFinderV2.GeodeResult.getNoGeodeResult();
         } else {
            int unreflectReferenceBound = Integer.MAX_VALUE;
            int minValue = Integer.MIN_VALUE;

            for (int r = 0; r < unreflectReferenceLimit2; r++) {
               int int2 = intArrayList.getInt(r * 3 + 1);
               unreflectReferenceBound = Math.min(unreflectReferenceBound, int2);
               minValue = Math.max(minValue, int2);
            }

            double unreflectReferenceLimit3 = unreflectReferenceLimit2 / (256.0 * Math.max(1, minValue - unreflectReferenceBound + 1));
            double unreflectReferenceValue6 = (double)unreflectReferenceTotal / unreflectReferenceLimit2;
            double unreflectReferenceValue7 = (double)unreflectReferenceTotal2 / unreflectReferenceLimit2;
            double unreflectReferenceValue8 = (double)unreflectReferenceTotal3 / unreflectReferenceLimit2;
            double unreflectReferenceTotal4 = 0.0;

            for (int s = 0; s < unreflectReferenceLimit2; s++) {
               double unreflectReferenceFactor = intArrayList.getInt(s * 3) - unreflectReferenceValue6;
               double unreflectReferenceFactor2 = intArrayList.getInt(s * 3 + 1) - unreflectReferenceValue7;
               double unreflectReferenceFactor3 = intArrayList.getInt(s * 3 + 2) - unreflectReferenceValue8;
               unreflectReferenceTotal4 += Math.sqrt(unreflectReferenceFactor * unreflectReferenceFactor + unreflectReferenceFactor2 * unreflectReferenceFactor2 + unreflectReferenceFactor3 * unreflectReferenceFactor3);
            }

            double unreflectReferenceValue9 = unreflectReferenceTotal4 / unreflectReferenceLimit2;
            int unreflectReferenceCounter2 = 0;
            int unreflectReferenceCounter3 = 0;

            for (int t = 0; t < unreflectReferenceLimit2; t++) {
               int int3 = intArrayList.getInt(t * 3);
               int unreflectReferenceValue10 = intArrayList.getInt(t * 3 + 1) - -64;
               int int4 = intArrayList.getInt(t * 3 + 2);

               for (int index = 0; index < 6; index++) {
                  int chunkFinderValue = int3 + NEIGHBOR_OFFSET_X[index];
                  int unreflectReferenceLimit5 = unreflectReferenceValue10 + NEIGHBOR_OFFSET_Y[index];
                  int unreflectReferenceLimit6 = int4 + NEIGHBOR_OFFSET_Z[index];
                  if (chunkFinderValue >= 0 && chunkFinderValue <= 15 && unreflectReferenceLimit6 >= 0 && unreflectReferenceLimit6 <= 15 && unreflectReferenceLimit5 >= 0 && unreflectReferenceLimit5 < 113) {
                     byte unreflectReferenceLimit7 = selectedBytes[getGridIndex(chunkFinderValue, unreflectReferenceLimit5, unreflectReferenceLimit6)];
                     if (unreflectReferenceLimit7 == 4) {
                        unreflectReferenceCounter2++;
                     } else if (unreflectReferenceLimit7 >= 2) {
                        unreflectReferenceCounter3++;
                     }
                  }
               }
            }

            int unreflectReferenceLimit8 = unreflectReferenceCounter2 + unreflectReferenceCounter3;
            double unreflectReferenceValue11 = unreflectReferenceLimit8 > 0 ? (double)unreflectReferenceCounter2 / unreflectReferenceLimit8 : 0.0;
            boolean unreflectReferenceFlag2 = unreflectReferenceLimit2 >= this.getMinGeodeBudCount()
               && unreflectReferenceLimit3 >= this.getMinGeodeBudDensity()
               && unreflectReferenceCounter >= this.getMinGeodeCalciteCount()
               && unreflectReferenceValue9 <= this.getMaxGeodeMeanDistance()
               && unreflectReferenceValue11 <= this.getMaxGeodeAirRatio();
            return new ChunkFinderV2.GeodeResult(unreflectReferenceFlag2, unreflectReferenceLimit2);
         }
      }
   }

   private void removeChunkFromSets(long hashOpenValue) {
      boolean longopenhashset = this.deepslateChunkKeys.remove(hashOpenValue);
      boolean hashOpenFlag = longopenhashset | this.playerChunkKeys.remove(hashOpenValue);
      boolean hashOpenFlag2 = hashOpenFlag | this.amethystChunkKeys.remove(hashOpenValue);
      boolean hashOpenFlag3 = hashOpenFlag2 | this.farmChunkKeys.remove(hashOpenValue);
      if (hashOpenFlag3) {
         this.markersDirty = true;
      }
   }

   private static void addWeightedPosition(ChunkFinderV2.ChunkAnalysis chunkAnalysis, int chunkFinderValue, int chunkFinderValue2, int chunkFinderValue3, double chunkFinderFactor) {
      chunkAnalysis.weightedXSum += (chunkFinderValue + 0.5) * chunkFinderFactor;
      chunkAnalysis.weightedZSum += (chunkFinderValue3 + 0.5) * chunkFinderFactor;
      chunkAnalysis.weightedYSum += (chunkFinderValue2 + 0.5) * chunkFinderFactor;
      chunkAnalysis.totalWeight += chunkFinderFactor;
   }

   private void renderMarkerBoxes(Matrix4f matrix4f) {
      for (int i = 0; i < this.nearbyMarkerCount; i++) {
         long unpackValue = this.nearbyMarkerPositions[i];
         int unpackLongX = BlockPos.unpackLongX(unpackValue);
         int unpackLongY = BlockPos.unpackLongY(unpackValue);
         int unpackLongZ = BlockPos.unpackLongZ(unpackValue);
         if (!VertexBuilderUtils.canVector(unpackLongX + 0.5, unpackLongY + 0.5, unpackLongZ + 0.5, 1.0F)) {
            float amountX = VertexBuilderUtils.getAmountX(unpackLongX);
            float f2 = VertexBuilderUtils.getAmount(unpackLongY);
            float amountForDouble = VertexBuilderUtils.getAmountForDouble(unpackLongZ);
            VertexBuilderUtils.handle(matrix4f, amountX, f2, amountForDouble, amountX + 1.0F, f2 + 1.0F, amountForDouble + 1.0F, MARKER_COLORS[this.nearbyMarkerTypes[i]], 1.5F);
         }
      }
   }

   private static boolean isSidewaysDeepslate(BlockState blockState) {
      return blockState.isOf(Blocks.DEEPSLATE) && blockState.contains(Properties.AXIS) ? blockState.get(Properties.AXIS) != Axis.Y : false;
   }

   private void renderRichSpotBeams(Matrix4f matrix4f) {
      int playerValue = 0xFF000000 | GREEN_PLAYER_COLOR & 16777215;
      double y = mc.player.getY();

      for (int i = 0; i < this.playerSampleCount; i++) {
         double playerLimit = Double.isNaN(this.richSpotY[i]) ? y - 16.0 : this.richSpotY[i];
         double vertexBuilderValue = playerLimit;
         double playerLimit3 = y + 3.0;
         if (playerLimit3 < playerLimit) {
            double playerValue4 = playerLimit3;
            playerLimit3 = playerLimit;
            vertexBuilderValue = playerValue4;
         }

         if (!VertexBuilderUtils.canVector(this.richSpotX[i], (vertexBuilderValue + playerLimit3) * 0.5, this.richSpotZ[i], (float)(playerLimit3 - vertexBuilderValue) * 0.5F + 1.0F)) {
            float amountX = VertexBuilderUtils.getAmountX(this.richSpotX[i]);
            float amountForDouble = VertexBuilderUtils.getAmountForDouble(this.richSpotZ[i]);
            float deltaY = VertexBuilderUtils.getAmount(vertexBuilderValue);
            float playerValue8 = VertexBuilderUtils.getAmount(playerLimit3);
            VertexBuilderUtils.handleForMatrix4fFloat(matrix4f, amountX, deltaY, amountForDouble, amountX, playerValue8, amountForDouble, playerValue, 3.0F);
            VertexBuilderUtils.handleForMatrix4fFloat(matrix4f, amountX - 0.9F, deltaY, amountForDouble, amountX + 0.9F, deltaY, amountForDouble, playerValue, 2.0F);
            VertexBuilderUtils.handleForMatrix4fFloat(matrix4f, amountX, deltaY, amountForDouble - 0.9F, amountX, deltaY, amountForDouble + 0.9F, playerValue, 2.0F);
         }
      }
   }

   private int getMinGeodeBudCount() {
      return (int)(40.0 - (this.sensitivity - 1) * 3.5555555555555554);
   }

   private boolean isOreHiddenInStone(WorldChunk worldChunk, int[] chunkFinderValues, int[] paletteScratch, int chunkFinderLimit, int k, int p, int chunkFinderValue, int chunkFinderValue2, int chunkFinderValue3) {
      byte chunkFinderValue4 = 0;
      int chunkFinderValue5 = chunkFinderValue4 + (k > 0 ? paletteScratch[chunkFinderValues[UnreflectReference.getValue(chunkFinderLimit, k - 1, p)]] >>> 9 & 1 : this.isStoneLikeAt(worldChunk, chunkFinderValue, chunkFinderValue2 - 1, chunkFinderValue3));
      int chunkFinderTotal = chunkFinderValue5
         + (k < 15 ? paletteScratch[chunkFinderValues[UnreflectReference.getValue(chunkFinderLimit, k + 1, p)]] >>> 9 & 1 : this.isStoneLikeAt(worldChunk, chunkFinderValue, chunkFinderValue2 + 1, chunkFinderValue3));
      if (p > 0) {
         chunkFinderTotal += paletteScratch[chunkFinderValues[UnreflectReference.getValue(chunkFinderLimit, k, p - 1)]] >>> 9 & 1;
      }

      if (p < 15) {
         chunkFinderTotal += paletteScratch[chunkFinderValues[UnreflectReference.getValue(chunkFinderLimit, k, p + 1)]] >>> 9 & 1;
      }

      if (chunkFinderLimit > 0) {
         chunkFinderTotal += paletteScratch[chunkFinderValues[UnreflectReference.getValue(chunkFinderLimit - 1, k, p)]] >>> 9 & 1;
      }

      if (chunkFinderLimit < 15) {
         chunkFinderTotal += paletteScratch[chunkFinderValues[UnreflectReference.getValue(chunkFinderLimit + 1, k, p)]] >>> 9 & 1;
      }

      return chunkFinderTotal >= 5;
   }

   private void pruneFarChunks(ChunkPos chunkPos) {
      int hashOpenValue = mc.options.getViewDistance().getValue() + 5;
      boolean iterator = removeDistantChunks(this.deepslateChunkKeys, chunkPos, hashOpenValue);
      boolean hashOpenFlag = iterator | removeDistantChunks(this.playerChunkKeys, chunkPos, hashOpenValue);
      boolean hashOpenFlag2 = hashOpenFlag | removeDistantChunks(this.amethystChunkKeys, chunkPos, hashOpenValue);
      boolean hashOpenFlag3 = hashOpenFlag2 | removeDistantChunks(this.farmChunkKeys, chunkPos, hashOpenValue);
      if (hashOpenFlag3) {
         this.markersDirty = true;
      }

      var fastIterator = this.analysisByChunk.long2ObjectEntrySet().fastIterator();

      while (fastIterator.hasNext()) {
         if (isBeyondChunkRange(((Entry)fastIterator.next()).getLongKey(), chunkPos, hashOpenValue)) {
            fastIterator.remove();
         }
      }

      LongIterator iterator2 = this.alertTimesByChunk.keySet().iterator();
      long currentMillis = System.currentTimeMillis();

      while (iterator2.hasNext()) {
         long randomLong = iterator2.nextLong();
         if (isBeyondChunkRange(randomLong, chunkPos, hashOpenValue) && currentMillis - this.alertTimesByChunk.get(randomLong) >= 300000L) {
            iterator2.remove();
         }
      }

      this.chunkScanner.removeFarChunks(chunkPos.x, chunkPos.z, hashOpenValue);
   }

   private void updateEntitySignals() {
      this.entitySignalsByChunk.clear();

      for (Entity entity : mc.world.getEntities()) {
         long long2 = ChunkPos.toLong(entity.getBlockX() >> 4, entity.getBlockZ() >> 4);
         ChunkFinderV2.EntitySignals long2objectopenhashmap = (ChunkFinderV2.EntitySignals)this.entitySignalsByChunk.get(long2);
         if (long2objectopenhashmap == null) {
            long2objectopenhashmap = new ChunkFinderV2.EntitySignals();
            this.entitySignalsByChunk.put(long2, long2objectopenhashmap);
         }

         if (entity instanceof ItemEntity && entity.getY() < 0.0) {
            long2objectopenhashmap.deepItemCount++;
         }

         if (entity instanceof PolarBearEntity) {
            long2objectopenhashmap.polarBearScore += 3;
         }

         if (!(entity instanceof PlayerEntity)) {
            long2objectopenhashmap.nonPlayerEntityCount++;
         }
      }

      var fastIterator = this.analysisByChunk.long2ObjectEntrySet().fastIterator();

      while (fastIterator.hasNext()) {
         var entry = (Entry)fastIterator.next();
         long longKey = entry.getLongKey();
         ChunkFinderV2.ChunkAnalysis chunkAnalysis = (ChunkFinderV2.ChunkAnalysis)entry.getValue();
         ChunkFinderV2.EntitySignals long2objectopenhashmap2 = (ChunkFinderV2.EntitySignals)this.entitySignalsByChunk.get(longKey);
         int value = long2objectopenhashmap2 == null ? 0 : long2objectopenhashmap2.deepItemCount;
         int bearPolarValue = long2objectopenhashmap2 == null ? 0 : long2objectopenhashmap2.polarBearScore;
         int value2 = long2objectopenhashmap2 != null && long2objectopenhashmap2.nonPlayerEntityCount > 20 ? long2objectopenhashmap2.nonPlayerEntityCount / 5 : 0;
         if (value != chunkAnalysis.droppedItemCount || bearPolarValue != chunkAnalysis.polarBearScore || value2 != chunkAnalysis.mobClusterScore) {
            chunkAnalysis.droppedItemCount = value;
            chunkAnalysis.polarBearScore = bearPolarValue;
            chunkAnalysis.mobClusterScore = value2;
            this.evaluateChunkAnalysis(new ChunkPos(longKey), longKey, chunkAnalysis);
         }
      }
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         long currentMillis = System.currentTimeMillis();

         while (!this.recentAlertTimes.isEmpty() && currentMillis - this.recentAlertTimes.firstLong() > 60000L) {
            this.recentAlertTimes.dequeueLong();
         }

         int hashCode = System.identityHashCode(mc.world);
         if (hashCode != this.worldIdentityHash) {
            this.worldIdentityHash = hashCode;
            this.clearChunkSets();
            this.entitySignalsByChunk.clear();
         }

         this.refreshSettings();
         if (this.playerChunksEnabled && ++this.entityScanTickCounter >= 20) {
            this.entityScanTickCounter = 0;
            this.updateEntitySignals();
         }

         ChunkPos chunkPos = mc.player.getChunkPos();
         int iIiValue = Math.min(mc.options.getViewDistance().getValue() + 1, 32);
         if (this.amethystEnabled
            || this.playerChunksEnabled
            || this.farmsEnabled
            || this.deepslateEnabled
            || this.cobbledEnabled
            || this.rotatedEnabled
            || this.endStoneEnabled) {
            this.chunkScanner.scanChunks(mc.world, chunkPos.x, chunkPos.z, iIiValue, 60000L, ChunkScanScheduler.getFpsScaledBudgetNanos(700), this::analyzeChunk);
         }

         if (++this.cleanupTickCounter >= 100) {
            this.cleanupTickCounter = 0;
            this.pruneFarChunks(chunkPos);
         }

         if (++this.recomputeTickCounter >= 10 || chunkPos.x != this.lastCenterChunkX || chunkPos.z != this.lastCenterChunkZ) {
            if (this.markersDirty || chunkPos.x != this.lastCenterChunkX || chunkPos.z != this.lastCenterChunkZ || this.nearbyMarkerCount > 0 || this.shouldShowBlockHighlights()) {
               this.rebuildNearestChunkLists(chunkPos);
            }

            this.recomputeTickCounter = 0;
         }
      }
   }

   private boolean hasLongDeepslateLine(int chunkFinderValue, int chunkFinderLimit, int chunkFinderValue2) {
      int bottomHeightLimit = chunkFinderLimit > -8 ? 50 : 20;
      if (this.measureDeepslateLineLength(chunkFinderValue, chunkFinderLimit, chunkFinderValue2, 1, 0, 0, bottomHeightLimit) >= bottomHeightLimit) {
         return true;
      } else {
         return this.measureDeepslateLineLength(chunkFinderValue, chunkFinderLimit, chunkFinderValue2, 0, 0, 1, bottomHeightLimit) >= bottomHeightLimit ? true : chunkFinderLimit > 0 && this.measureDeepslateLineLength(chunkFinderValue, chunkFinderLimit, chunkFinderValue2, 0, 1, 0, bottomHeightLimit) >= bottomHeightLimit;
      }
   }

   private static boolean isBuddingOrAmethystBlock(BlockState blockState) {
      return blockState.isOf(Blocks.AMETHYST_BLOCK) || blockState.isOf(Blocks.BUDDING_AMETHYST);
   }

   private void evaluateChunkAnalysis(ChunkPos chunkPos, long deepslateRepeaterValue, ChunkFinderV2.ChunkAnalysis chunkAnalysis) {
      if (chunkAnalysis.waxedCopperCount >= 50) {
         this.removeChunkFromSets(deepslateRepeaterValue);
      } else {
         StringBuilder stringBuilder = null;
         if (this.deepslateEnabled && chunkAnalysis.misplacedDeepslateCount >= 1) {
            stringBuilder = new StringBuilder().append("Deepslate[").append(chunkAnalysis.misplacedDeepslateCount).append("] ");
         }

         if (this.cobbledEnabled && chunkAnalysis.cobbledDeepslateCount >= 4) {
            if (stringBuilder == null) {
               stringBuilder = new StringBuilder();
            }

            stringBuilder.append("CobbledDeepslate[").append(chunkAnalysis.cobbledDeepslateCount).append("] ");
         }

         if (this.rotatedEnabled && chunkAnalysis.sidewaysDeepslateCount >= 3) {
            if (stringBuilder == null) {
               stringBuilder = new StringBuilder();
            }

            stringBuilder.append("RotatedDeepslate[").append(chunkAnalysis.sidewaysDeepslateCount).append("] ");
         }

         if (this.endStoneEnabled && chunkAnalysis.endStoneCount >= 2) {
            if (stringBuilder == null) {
               stringBuilder = new StringBuilder();
            }

            stringBuilder.append("EndStone[").append(chunkAnalysis.endStoneCount).append("] ");
         }

         if (stringBuilder != null) {
            if (this.deepslateChunkKeys.add(deepslateRepeaterValue)) {
               this.markersDirty = true;
               this.announceFinding(chunkPos, deepslateRepeaterValue, stringBuilder.toString().trim());
            }
         } else if (this.deepslateChunkKeys.remove(deepslateRepeaterValue)) {
            this.markersDirty = true;
         }

         if (this.playerChunksEnabled) {
            int veinBear = this.computePlayerActivityScore(chunkAnalysis, null);
            if (veinBear >= this.getPlayerScoreThreshold()) {
               if (this.playerChunkKeys.add(deepslateRepeaterValue)) {
                  this.markersDirty = true;
                  var arrayList = new ArrayList();
                  this.computePlayerActivityScore(chunkAnalysis, arrayList);
                  this.announceFinding(chunkPos, deepslateRepeaterValue, "CORZ JACKPOT S:" + veinBear + " [" + String.join(", ", arrayList) + "]");
               }
            } else if (this.playerChunkKeys.remove(deepslateRepeaterValue)) {
               this.markersDirty = true;
            }
         } else if (this.playerChunkKeys.remove(deepslateRepeaterValue)) {
            this.markersDirty = true;
         }

         boolean deepslateRepeaterFlag = this.amethystEnabled && chunkAnalysis.geodeResult.hit();
         boolean deepslateRepeaterFlag2 = this.amethystEnabled && chunkAnalysis.darkAmethystPocketCount >= this.getMagicAmethystThreshold();
         if (!deepslateRepeaterFlag && !deepslateRepeaterFlag2) {
            if (this.amethystChunkKeys.remove(deepslateRepeaterValue)) {
               this.markersDirty = true;
            }
         } else if (this.amethystChunkKeys.add(deepslateRepeaterValue)) {
            this.markersDirty = true;
            String deepslateRepeaterText = deepslateRepeaterFlag ? "StrippedGeode[" + chunkAnalysis.geodeResult.crystals() + "]" : "";
            if (deepslateRepeaterFlag2) {
               deepslateRepeaterText = (deepslateRepeaterText.isEmpty() ? "" : deepslateRepeaterText + " ") + "MagicAmethyst[" + chunkAnalysis.darkAmethystPocketCount + "]";
            }

            this.announceFinding(chunkPos, deepslateRepeaterValue, deepslateRepeaterText);
         }

         if (this.farmsEnabled) {
            int repeaterChunk = this.computeFarmScore(chunkAnalysis);
            int deepslateRepeaterLimit = getFarmChancePercent(repeaterChunk);
            if (deepslateRepeaterLimit >= this.getFarmScoreThreshold() && repeaterChunk > 0) {
               if (this.farmChunkKeys.add(deepslateRepeaterValue)) {
                  this.markersDirty = true;
                  String deepslateRepeaterText2 = chunkAnalysis.poweredRepeaterCount >= 3 ? "Repeater chunk" : "Base chance " + deepslateRepeaterLimit + "%";
                  this.announceFinding(chunkPos, deepslateRepeaterValue, deepslateRepeaterText2 + " (score " + repeaterChunk + ")");
               }
            } else if (this.farmChunkKeys.remove(deepslateRepeaterValue)) {
               this.markersDirty = true;
            }
         } else if (this.farmChunkKeys.remove(deepslateRepeaterValue)) {
            this.markersDirty = true;
         }
      }
   }

   private int getFarmScoreThreshold() {
      return Math.max(25, Math.min(90, 45 + (5 - this.sensitivity) * 5));
   }

   private int countDarkAmethystPockets(WorldChunk worldChunk) {
      ClientWorld world = mc.world;
      if (world == null) {
         return 0;
      } else {
         ChunkPos pos = worldChunk.getPos();
         int startX = pos.getStartX();
         int startZ = pos.getStartZ();
         int bottomY = worldChunk.getBottomY();
         int offsetRegionValue = Math.max(-64, bottomY);
         int offsetRegionLimit = Math.min(48, bottomY + worldChunk.getHeight() - 1);
         ChunkSection[] sectionArray = worldChunk.getSectionArray();
         long offsetRegionTotal = 0L;
         long offsetRegionTotal2 = 0L;
         long offsetRegionTotal3 = 0L;
         int offsetRegionDivisor = 0;

         for (int i = 0; i < sectionArray.length; i++) {
            ChunkSection chunkSection = sectionArray[i];
            if (chunkSection != null && !chunkSection.isEmpty()) {
               int offsetRegionValue2 = bottomY + (i << 4);
               if (offsetRegionValue2 <= offsetRegionLimit && offsetRegionValue2 + 15 >= offsetRegionValue && chunkSection.hasAny(ChunkFinderV2::isBuddingOrAmethystBlock)) {
                  for (int j = 0; j < 16; j++) {
                     int offsetRegionLimit2 = offsetRegionValue2 + j;
                     if (offsetRegionLimit2 >= offsetRegionValue && offsetRegionLimit2 <= offsetRegionLimit) {
                        for (int k = 0; k < 16; k++) {
                           for (int m = 0; m < 16; m++) {
                              if (isBuddingOrAmethystBlock(chunkSection.getBlockState(k, j, m))) {
                                 offsetRegionTotal += startX + k;
                                 offsetRegionTotal2 += offsetRegionLimit2;
                                 offsetRegionTotal3 += startZ + m;
                                 offsetRegionDivisor++;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (offsetRegionDivisor == 0) {
            return 0;
         } else {
            int offsetRegionValue3 = (int)(offsetRegionTotal / offsetRegionDivisor);
            int offsetRegionValue4 = (int)(offsetRegionTotal2 / offsetRegionDivisor);
            int offsetRegionValue5 = (int)(offsetRegionTotal3 / offsetRegionDivisor);
            byte offsetRegionLimit3 = 8;
            int offsetRegion = 0;
            this.region.resetAround(worldChunk);
            this.lightGridOriginX = offsetRegionValue3 - offsetRegionLimit3 - 2;
            this.lightGridOriginY = offsetRegionValue4 - offsetRegionLimit3 - 2;
            this.lightGridOriginZ = offsetRegionValue5 - offsetRegionLimit3 - 2;
            Arrays.fill(this.lightLevelTable, (byte)-1);

            for (int p = -offsetRegionLimit3; p <= offsetRegionLimit3; p++) {
               for (int q = -offsetRegionLimit3; q <= offsetRegionLimit3; q++) {
                  for (int r = -offsetRegionLimit3; r <= offsetRegionLimit3; r++) {
                     int x2 = offsetRegionValue3 + p;
                     int y2 = offsetRegionValue4 + q;
                     int offsetRegionValue8 = offsetRegionValue5 + r;
                     BlockState chunkState = this.region.getChunkState(x2, y2, offsetRegionValue8);
                     if ((chunkState.isOf(Blocks.AIR) || chunkState.isOf(Blocks.AMETHYST_CLUSTER)) && this.getCachedBlockLight(world, x2, y2, offsetRegionValue8) == 0) {
                        boolean offsetRegionFlag = false;
                        int lightLevelValue9 = 0;
                        int offsetRegionValue10 = 0;
                        int offsetRegionValue11 = 0;
                        boolean offsetRegionFlag2 = false;

                        for (int s = 0; s < 6; s++) {
                           Direction direction = ALL_DIRECTIONS[s];
                           int lightLevelValue = x2 + direction.getOffsetX();
                           int offsetRegionValue13 = y2 + direction.getOffsetY();
                           int z2 = offsetRegionValue8 + direction.getOffsetZ();
                           int lightLevel = this.getCachedBlockLight(world, lightLevelValue, offsetRegionValue13, z2);
                           if (lightLevel > 4) {
                              offsetRegionFlag2 = true;
                              break;
                           }

                           if (!offsetRegionFlag && lightLevel == 0 && this.region.getChunkState(lightLevelValue, offsetRegionValue13, z2).isOf(Blocks.AIR)) {
                              offsetRegionFlag = true;
                              lightLevelValue9 = lightLevelValue;
                              offsetRegionValue10 = offsetRegionValue13;
                              offsetRegionValue11 = z2;
                           }
                        }

                        if (!offsetRegionFlag2 && offsetRegionFlag) {
                           for (int t = 0; t < 6; t++) {
                              Direction direction2 = ALL_DIRECTIONS[t];
                              if (this.getCachedBlockLight(world, lightLevelValue9 + direction2.getOffsetX(), offsetRegionValue10 + direction2.getOffsetY(), offsetRegionValue11 + direction2.getOffsetZ()) > 4) {
                                 offsetRegionFlag2 = true;
                                 break;
                              }
                           }

                           if (!offsetRegionFlag2) {
                              offsetRegion++;
                           }
                        }
                     }
                  }
               }
            }

            return offsetRegion;
         }
      }
   }

   private static int getFarmChancePercent(int chunkFinderValue) {
      return Math.max(20, Math.min(99, 25 + chunkFinderValue / 2));
   }

   private void scanPlayerActivity(WorldChunk worldChunk, ChunkFinderV2.ChunkAnalysis chunkAnalysis, long veinBearValue) {
      ChunkPos pos = worldChunk.getPos();
      int startX = pos.getStartX();
      int startZ = pos.getStartZ();
      int bottomY = worldChunk.getBottomY();
      int min2 = Math.min(worldChunk.getBottomY() + worldChunk.getHeight() - 1, 60);
      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      int[] airRunLengths13 = this.airRunLengths;
      int[] veinBearValues = this.belowZeroAirRunLengths;
      int[] oreRunLengths11 = this.oreRunLengths;
      Arrays.fill(airRunLengths13, 0);
      Arrays.fill(veinBearValues, 0);
      Arrays.fill(oreRunLengths11, 0);

      for (int i = 0; i < sectionArray.length; i++) {
         int veinBearLimit = bottomY + (i << 4);
         if (veinBearLimit > min2) {
            break;
         }

         int intersectsCenterBound = Math.min(16, min2 - veinBearLimit + 1);
         ChunkSection chunkSection = sectionArray[i];
         if (chunkSection != null && !chunkSection.isEmpty()) {
            this.paletteReader.handleFlag(chunkSection);
            int[] unreflectReferenceTable = this.paletteReader.unreflectReferenceTable;
            BlockState[] copyOf = this.paletteReader.copyOf;
            int[] paletteScratch = this.getPaletteScratch(this.paletteReader.paletteSize);

            for (int j = 0; j < this.paletteReader.paletteSize; j++) {
               paletteScratch[j] = BLOCK_CATEGORY_FLAGS.getInt(copyOf[j].getBlock());
            }

            for (int k = 0; k < intersectsCenterBound; k++) {
               int veinBearValue3 = veinBearLimit + k;
               int veinBearValue4 = k << 8;
               boolean veinBearFlag = veinBearValue3 < 0;

               for (int m = 0; m < 256; m++) {
                  int index = unreflectReferenceTable[veinBearValue4 | m];
                  int veinBearValue5 = paletteScratch[index];
                  if ((veinBearValue5 & 1) != 0) {
                     airRunLengths13[m]++;
                     if (veinBearFlag) {
                        veinBearValues[m]++;
                     }

                     oreRunLengths11[m] = 0;
                  } else {
                     if (airRunLengths13[m] >= 12 && veinBearValue3 < 20) {
                        chunkAnalysis.airPocketScore = chunkAnalysis.airPocketScore + airRunLengths13[m] / 4;
                     }

                     airRunLengths13[m] = 0;
                     if (veinBearFlag) {
                        veinBearValues[m] = 0;
                     }

                     if ((veinBearValue5 & -513) == 0) {
                        oreRunLengths11[m] = 0;
                     } else {
                        int chunkFinderLimit = m & 15;
                        int veinBearValue7 = m >> 4;
                        int chunkFinderValue = startX + chunkFinderLimit;
                        int veinBearValue9 = startZ + veinBearValue7;
                        if (veinBearValue3 < 0) {
                           if ((veinBearValue5 & 2) != 0 && chunkFinderLimit < 15 && copyOf[unreflectReferenceTable[veinBearValue4 | m + 1]].getBlock() == copyOf[index].getBlock()) {
                              chunkAnalysis.cobbleLineCount++;
                           }

                           if ((veinBearValue5 & 4) != 0) {
                              chunkAnalysis.polishedDeepslateCount++;
                              addWeightedPosition(chunkAnalysis, chunkFinderValue, veinBearValue3, veinBearValue9, 1.0);
                           }

                           if ((veinBearValue5 & 8) != 0) {
                              chunkAnalysis.stairsCount++;
                           }
                        }

                        if (veinBearValue3 < 20) {
                           if ((veinBearValue5 & 16) != 0) {
                              chunkAnalysis.obsidianCount++;
                              addWeightedPosition(chunkAnalysis, chunkFinderValue, veinBearValue3, veinBearValue9, 1.0);
                           }

                           if ((veinBearValue5 & 32) != 0) {
                              chunkAnalysis.pistonArrayCount++;
                           }
                        }

                        if (veinBearValue3 < 0 && (veinBearValue5 & 64) != 0) {
                           chunkAnalysis.chestCount++;
                           addWeightedPosition(chunkAnalysis, chunkFinderValue, veinBearValue3, veinBearValue9, 4.0);
                        }

                        if ((veinBearValue5 & 128) != 0) {
                           int veinBearValue10 = ++oreRunLengths11[m];
                           boolean veinBearFlag2 = (veinBearValue5 & 256) != 0;
                           if (veinBearFlag2) {
                              chunkAnalysis.rareOreCount++;
                           }

                           addWeightedPosition(chunkAnalysis, chunkFinderValue, veinBearValue3, veinBearValue9, veinBearFlag2 ? 6.0 : 1.0);
                           if (this.isOreHiddenInStone(worldChunk, unreflectReferenceTable, paletteScratch, chunkFinderLimit, k, veinBearValue7, chunkFinderValue, veinBearValue3, veinBearValue9)) {
                              chunkAnalysis.hiddenOreCount += 2;
                           }

                           if (veinBearValue10 == 4) {
                              chunkAnalysis.verticalVeinCount++;
                           }
                        } else {
                           oreRunLengths11[m] = 0;
                        }
                     }
                  }
               }
            }
         } else {
            int max11 = Math.max(0, Math.min(intersectsCenterBound, -veinBearLimit));

            for (int p = 0; p < 256; p++) {
               airRunLengths13[p] += intersectsCenterBound;
               veinBearValues[p] += max11;
               oreRunLengths11[p] = 0;
            }
         }
      }

      for (int q = 0; q < 256; q++) {
         if (veinBearValues[q] >= 10) {
            chunkAnalysis.needleCount++;
         }
      }

      for (BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
         if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof HopperBlockEntity) {
            chunkAnalysis.storageBlockCount++;
         }
      }

      ChunkFinderV2.EntitySignals long2objectopenhashmap = (ChunkFinderV2.EntitySignals)this.entitySignalsByChunk.get(veinBearValue);
      if (long2objectopenhashmap != null) {
         chunkAnalysis.droppedItemCount = long2objectopenhashmap.deepItemCount;
         chunkAnalysis.polarBearScore = long2objectopenhashmap.polarBearScore;
         if (long2objectopenhashmap.nonPlayerEntityCount > 20) {
            chunkAnalysis.mobClusterScore = long2objectopenhashmap.nonPlayerEntityCount / 5;
         }
      }
   }

   private void analyzeChunk(WorldChunk worldChunk, long repeaterMagicValue) {
      ChunkFinderV2.ChunkAnalysis chunkAnalysis = new ChunkFinderV2.ChunkAnalysis();

      try {
         this.scanDeepslateMarkers(worldChunk, chunkAnalysis);
         if (this.playerChunksEnabled) {
            this.scanPlayerActivity(worldChunk, chunkAnalysis, repeaterMagicValue);
         }

         if (this.amethystEnabled) {
            chunkAnalysis.geodeResult = this.detectGeode(worldChunk);
            chunkAnalysis.darkAmethystPocketCount = this.countDarkAmethystPockets(worldChunk);
         }

         if (this.farmsEnabled) {
            this.scanFarmBlocks(worldChunk, chunkAnalysis);
         }
      } catch (Throwable e) {
         this.removeChunkFromSets(repeaterMagicValue);
         if (this.analysisByChunk.remove(repeaterMagicValue) != null) {
            this.markersDirty = true;
         }

         return;
      }

      ChunkFinderV2.ChunkAnalysis put = (ChunkFinderV2.ChunkAnalysis)this.analysisByChunk.put(repeaterMagicValue, chunkAnalysis);
      if (chunkAnalysis.markerPositions != null || put != null && put.markerPositions != null) {
         this.markersDirty = true;
      }

      this.evaluateChunkAnalysis(worldChunk.getPos(), repeaterMagicValue, chunkAnalysis);
   }

   private int getPlayerScoreThreshold() {
      return Math.max(2, 10 - this.sensitivity);
   }

   private static final class ChunkAnalysis {
      int seagrassCount;
      int polishedDeepslateCount;
      int mobClusterScore;
      int waxedCopperCount;
      int droppedItemCount;
      double weightedYSum;
      int hiddenOreCount;
      int obsidianCount;
      int endStoneCount;
      int cobbleLineCount;
      int storageBlockCount;
      int needleCount;
      int verticalVeinCount;
      ChunkFinderV2.GeodeResult geodeResult = ChunkFinderV2.GeodeResult.getNoGeodeResult();
      int misplacedDeepslateCount;
      int vineCount;
      int airPocketScore;
      double weightedXSum;
      int beeCount;
      int lowCobbledDeepslateCount;
      int darkAmethystPocketCount;
      int rareOreCount;
      int pistonArrayCount;
      int chestCount;
      int beehiveCount;
      int stairsCount;
      int sidewaysDeepslateCount;
      double totalWeight;
      double weightedZSum;
      int poweredRepeaterCount;
      int flowerCount;
      int craftingStorageCount;
      int polarBearScore;
      int repeaterCount;
      LongArrayList markerPositions;
      int cobbledDeepslateCount;
      ByteArrayList markerTypes;

      void addMarker(long arrayByteValue, int deepslateCobbledLimit) {
         if (this.markerPositions == null) {
            this.markerPositions = new LongArrayList();
            this.markerTypes = new ByteArrayList();
         }

         if (this.markerPositions.size() < 512) {
            this.markerPositions.add(arrayByteValue);
            this.markerTypes.add((byte)deepslateCobbledLimit);
         }
      }
   }

   private static final class EntitySignals {
      int nonPlayerEntityCount;
      int polarBearScore;
      int deepItemCount;
   }

   private record GeodeResult(boolean hit, int crystals) {
      private static final ChunkFinderV2.GeodeResult INSTANCE = new ChunkFinderV2.GeodeResult(false, 0);

      static ChunkFinderV2.GeodeResult getNoGeodeResult() {
         return INSTANCE;
      }

      static ChunkFinderV2.GeodeResult getBuddingFoundResult() {
         return INSTANCE;
      }
   }

   private final class Region {
      private final boolean[] chunkLoaded;
      private static final int CHUNK_SHIFT_BITS = 4;
      private int bottomY;
      private final BlockState voidState;
      private final BlockState airState;
      private int originChunkZ;
      private int originChunkX;
      private final ChunkSection[][] chunkSections = new ChunkSection[81][];

      private Region() {
         this.chunkLoaded = new boolean[81];
         this.airState = Blocks.AIR.getDefaultState();
         this.voidState = Blocks.VOID_AIR.getDefaultState();
      }

      BlockState getChunkState(int x2, int y2, int z2) {
         int chunkLimit = (x2 >> 4) - this.originChunkX;
         int chunkLimit2 = (z2 >> 4) - this.originChunkZ;
         if (chunkLimit >= 0 && chunkLimit < 9 && chunkLimit2 >= 0 && chunkLimit2 < 9) {
            int index = chunkLimit2 * 9 + chunkLimit;
            if (!this.chunkLoaded[index]) {
               this.chunkLoaded[index] = true;
               WorldChunk worldChunk = Module.mc.world.getChunkManager().getWorldChunk(this.originChunkX + chunkLimit, this.originChunkZ + chunkLimit2);
               this.chunkSections[index] = worldChunk == null ? null : worldChunk.getSectionArray();
            }

            ChunkSection[] chunkSections = this.chunkSections[index];
            if (chunkSections == null) {
               return this.voidState;
            } else {
               int index2 = y2 - this.bottomY >> 4;
               if (index2 >= 0 && index2 < chunkSections.length) {
                  ChunkSection chunkSection = chunkSections[index2];
                  return chunkSection != null && !chunkSection.isEmpty() ? chunkSection.getBlockState(x2 & 15, y2 & 15, z2 & 15) : this.airState;
               } else {
                  return this.voidState;
               }
            }
         } else {
            return Module.mc.world.getBlockState(new BlockPos(x2, y2, z2));
         }
      }

      void resetAround(WorldChunk worldChunk) {
         Arrays.fill(this.chunkSections, null);
         Arrays.fill(this.chunkLoaded, false);
         this.originChunkX = worldChunk.getPos().x - 4;
         this.originChunkZ = worldChunk.getPos().z - 4;
         this.bottomY = worldChunk.getBottomY();
         byte index = 40;
         this.chunkSections[index] = worldChunk.getSectionArray();
         this.chunkLoaded[index] = true;
      }
   }
}
