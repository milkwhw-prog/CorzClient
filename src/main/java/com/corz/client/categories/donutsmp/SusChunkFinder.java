package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.StringBase;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.SeedFeatureEngine;
import com.corz.client.utils.ChunkScanScheduler;
import com.corz.client.utils.IconsPngType;

public class SusChunkFinder extends Module {
   private static final int UNIT_STEP = 1;
   private final BooleanSetting amethyst;
   private final BooleanSetting rotatedDeepslate;
   private final BooleanSetting beeNest;
   private final SeedFeatureEngine seedFeatureEngine;
   private int tickCounter;
   private final BooleanSetting bamboo;
   private final BooleanSetting kelp;
   private final ChunkScanScheduler hashChunk;
   private int settingsHash;
   private final BooleanSetting caveVines;
   private final BooleanSetting smartAdjustment;
   private final IntegerSetting overlayAlpha;
   private final BooleanSetting cocoa;
   private final Long2ObjectOpenHashMap<SusChunkFinder.Flag> flaggedChunks;
   private final BooleanSetting vines;
   private final IntegerSetting sensitivity;
   private final IntegerSetting simulationDistance = new IntegerSetting("Simulation Distance", "How many chunks out from you to scan and render.", 4, 1, 12);
   private final StringBase seed;
   private final List<SusChunkFinder.Tracked> trackedFeatures;

   private static int classifyBlock(
      BlockState state, boolean bambooLargeFlag, boolean enabled, boolean bambooLargeFlag3, boolean bambooLargeFlag4, boolean bambooLargeFlag5, boolean bambooLargeFlag6, boolean bambooLargeFlag7, boolean bambooLargeFlag8
   ) {
      if (state.isAir()) {
         return 0;
      } else if (bambooLargeFlag && state.isOf(Blocks.DEEPSLATE) && state.contains(Properties.AXIS) && state.get(Properties.AXIS) != Axis.Y) {
         return 1;
      } else if (enabled && state.isOf(Blocks.AMETHYST_CLUSTER)) {
         return 2;
      } else if (bambooLargeFlag3 && hasGlowBerries(state)) {
         return 3;
      } else if (bambooLargeFlag4 && state.isOf(Blocks.COCOA) && state.contains(Properties.AGE_2) && state.get(Properties.AGE_2) == 2) {
         return 4;
      } else if (bambooLargeFlag5
         && (state.isOf(Blocks.BEE_NEST) || state.isOf(Blocks.BEEHIVE))
         && state.contains(Properties.HONEY_LEVEL)
         && state.get(Properties.HONEY_LEVEL) == 5) {
         return 5;
      } else if (bambooLargeFlag6 && state.isOf(Blocks.KELP_PLANT)) {
         return 6;
      } else if (bambooLargeFlag7
         && state.isOf(Blocks.BAMBOO)
         && state.contains(Properties.BAMBOO_LEAVES)
         && String.valueOf(state.get(Properties.BAMBOO_LEAVES)).equalsIgnoreCase("LARGE")) {
         return 7;
      } else {
         return bambooLargeFlag8 && state.isOf(Blocks.VINE) ? 8 : 0;
      }
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         int value = this.computeSettingsHash();
         if (value != this.settingsHash) {
            this.settingsHash = value;
            this.flaggedChunks.clear();
            this.hashChunk.clearScannedChunks();
         }

         ChunkPos chunkPos = mc.player.getChunkPos();
         int iIiValue = this.simulationDistance.getValue();
         this.hashChunk.scanChunks(mc.world, chunkPos.x, chunkPos.z, iIiValue, 15000L, ChunkScanScheduler.getFpsScaledBudgetNanos(400), this::updateChunkFlag);
         if (++this.tickCounter % 40 == 0) {
            int iIiValue2 = iIiValue + 2;
            var fastIterator = this.flaggedChunks.long2ObjectEntrySet().fastIterator();

            while (fastIterator.hasNext()) {
               long longKey = ((Entry)fastIterator.next()).getLongKey();
               if (Math.abs(ChunkPos.getPackedX(longKey) - chunkPos.x) > iIiValue2 || Math.abs(ChunkPos.getPackedZ(longKey) - chunkPos.z) > iIiValue2) {
                  fastIterator.remove();
               }
            }

            this.hashChunk.removeFarChunks(chunkPos.x, chunkPos.z, iIiValue2 + 2);
         }
      }
   }

   private static boolean hasGlowBerries(BlockState state) {
      return (state.isOf(Blocks.CAVE_VINES) || state.isOf(Blocks.CAVE_VINES_PLANT)) && state.contains(Properties.BERRIES) && state.get(Properties.BERRIES);
   }

   private static RegistryKey<PlacedFeature> getPlacedFeatureKey(String registryText) {
      return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("minecraft", registryText));
   }

   private int getFlagThreshold() {
      int susChunkValue = this.sensitivity.getValue();
      return Math.max(6, 46 - susChunkValue * 4);
   }

   @Override
   protected void onDisable() {
      this.flaggedChunks.clear();
      this.hashChunk.clearScannedChunks();
   }

   private int scoreSeedPredictedFeatures(WorldChunk worldChunk, ChunkPos chunkPos, List<int[]> list) {
      try {
         Long hashCode = SeedFeatureEngine.parseSeed(this.seed.getCurrentText());
         if (hashCode != null && mc.world != null) {
            DynamicRegistryManager registryManager = mc.world.getRegistryManager();
            int worldRegistry = 0;

            for (SusChunkFinder.Tracked tracked : this.trackedFeatures) {
               if (tracked.toggle().isEnabled()) {
                  boolean anyMatch = tracked.keys().stream().anyMatch(registryKey -> registryKey.getValue().getPath().contains("amethyst") || registryKey.getValue().getPath().contains("geode"));
                  if (anyMatch) {
                     int worldRegistryCounter = 0;
                     int worldRegistryCounter2 = 0;

                     for (RegistryKey registryKey2 : tracked.keys()) {
                        for (BlockPos blockPos : (Iterable<net.minecraft.util.math.BlockPos>) (this.seedFeatureEngine.computeFeaturePositions(mc.world, registryManager, hashCode, chunkPos.x, chunkPos.z, registryKey2))) {
                           worldRegistryCounter++;
                           if (this.isAnyBlockNearby(blockPos, tracked.blocks())) {
                              worldRegistryCounter2++;
                           } else if (anyMatch && list.size() < 64) {
                              list.add(new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
                           }
                        }
                     }

                     if (anyMatch && worldRegistryCounter > 0 && worldRegistryCounter2 == 0) {
                        worldRegistry += 14 * Math.min(worldRegistryCounter, 3);
                     }
                  }
               }
            }

            return worldRegistry;
         } else {
            return 0;
         }
      } catch (Throwable e) {
         return 0;
      }
   }

   private void updateChunkFlag(WorldChunk worldChunk, long hashOpenValue) {
      try {
         SusChunkFinder.Flag baseSection = this.analyzeChunk(worldChunk, worldChunk.getPos());
         if (baseSection != null) {
            this.flaggedChunks.put(hashOpenValue, baseSection);
         } else {
            this.flaggedChunks.remove(hashOpenValue);
         }
      } catch (Throwable e) {
         this.flaggedChunks.remove(hashOpenValue);
      }
   }

   private int computeSettingsHash() {
      int integerValue = (this.kelp.isEnabled() ? 1 : 0)
         | (this.caveVines.isEnabled() ? 2 : 0)
         | (this.vines.isEnabled() ? 4 : 0)
         | (this.amethyst.isEnabled() ? 8 : 0)
         | (this.bamboo.isEnabled() ? 16 : 0)
         | (this.cocoa.isEnabled() ? 32 : 0)
         | (this.beeNest.isEnabled() ? 64 : 0)
         | (this.rotatedDeepslate.isEnabled() ? 128 : 0)
         | (this.smartAdjustment.isEnabled() ? 256 : 0);
      return 31 * (31 * (31 * System.identityHashCode(mc.world) + this.seed.getCurrentText().hashCode()) + this.sensitivity.getValue()) + integerValue;
   }

   private SusChunkFinder.Flag analyzeChunk(WorldChunk worldChunk, ChunkPos chunkPos) {
      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      int bottomY = mc.world.getBottomY();
      int sectionTotal = 0;
      int sectionTotal2 = 0;
      int sectionTotal3 = 0;
      int sectionTotal4 = 0;
      int sectionTotal5 = 0;
      int sectionTotal6 = 0;
      int sectionTotal7 = 0;
      int sectionCounter = 0;
      var items = new ArrayList();
      int sectionLimit = Integer.MAX_VALUE;
      int sectionValue = Integer.MIN_VALUE;
      boolean booleanValue = this.rotatedDeepslate.isEnabled();
      boolean enabled = this.amethyst.isEnabled();
      boolean enabled3 = this.caveVines.isEnabled();
      boolean enabled4 = this.cocoa.isEnabled();
      boolean enabled5 = this.beeNest.isEnabled();
      boolean enabled6 = this.kelp.isEnabled();
      boolean enabled7 = this.bamboo.isEnabled();
      boolean enabled8 = this.vines.isEnabled();
      if (!booleanValue && !enabled && !enabled3 && !enabled4 && !enabled5 && !enabled6 && !enabled7 && !enabled8) {
         return null;
      } else {
         BlockState blockState = null;
         int classifyBlock2 = 0;

         for (int i = 0; i < sectionArray.length; i++) {
            ChunkSection chunkSection = sectionArray[i];
            if (chunkSection != null
               && !chunkSection.isEmpty()
               && chunkSection.hasAny(doKelp -> classifyBlock(doKelp, booleanValue, enabled, enabled3, enabled4, enabled5, enabled6, enabled7, enabled8) != 0)) {
               int sectionValue3 = bottomY + (i << 4);

               for (int j = 0; j < 16; j++) {
                  for (int k = 0; k < 16; k++) {
                     for (int m = 0; m < 16; m++) {
                        BlockState blockState2 = chunkSection.getBlockState(k, j, m);
                        if (blockState2 != blockState) {
                           blockState = blockState2;
                           classifyBlock2 = classifyBlock(blockState2, booleanValue, enabled, enabled3, enabled4, enabled5, enabled6, enabled7, enabled8);
                        }

                        if (classifyBlock2 != 0) {
                           int sectionLimit2 = 0;
                           switch (classifyBlock2) {
                              case 1:
                                 if (sectionTotal < 24) {
                                    sectionTotal += 10;
                                    sectionLimit2 = 10;
                                 }
                                 break;
                              case 2:
                                 if (sectionTotal2 < 24) {
                                    sectionTotal2 += 6;
                                    sectionLimit2 = 6;
                                 }
                                 break;
                              case 3:
                                 if (sectionTotal3 < 24) {
                                    sectionTotal3 += 5;
                                    sectionLimit2 = 5;
                                 }
                                 break;
                              case 4:
                                 if (sectionTotal4 < 24) {
                                    sectionTotal4 += 5;
                                    sectionLimit2 = 5;
                                 }
                                 break;
                              case 5:
                                 if (sectionTotal5 < 24) {
                                    sectionTotal5 += 6;
                                    sectionLimit2 = 6;
                                 }
                                 break;
                              case 6:
                                 if (sectionTotal6 < 24) {
                                    sectionTotal6 += 2;
                                    sectionLimit2 = 2;
                                 }
                                 break;
                              case 7:
                                 if (sectionTotal7 < 24) {
                                    sectionTotal7 += 2;
                                    sectionLimit2 = 2;
                                 }
                                 break;
                              case 8:
                                 if (sectionCounter < 24) {
                                    sectionCounter++;
                                    sectionLimit2 = 1;
                                 }
                           }

                           if (sectionLimit2 > 0) {
                              int sectionValue4 = (chunkPos.x << 4) + k;
                              int sectionLimit3 = sectionValue3 + j;
                              int sectionValue5 = (chunkPos.z << 4) + m;
                              if (items.size() < 64) {
                                 items.add(new int[]{sectionValue4, sectionLimit3, sectionValue5});
                              }

                              if (sectionLimit3 < sectionLimit) {
                                 sectionLimit = sectionLimit3;
                              }

                              if (sectionLimit3 > sectionValue) {
                                 sectionValue = sectionLimit3;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         int worldRegistry = this.scoreSeedPredictedFeatures(worldChunk, chunkPos, items);
         if (worldRegistry > 0) {
            for (int[] sectionValues : (Iterable<int[]>) (items)) {
               if (sectionValues[1] < sectionLimit) {
                  sectionLimit = sectionValues[1];
               }

               if (sectionValues[1] > sectionValue) {
                  sectionValue = sectionValues[1];
               }
            }
         }

         int value = sectionTotal + sectionTotal2 + sectionTotal3 + sectionTotal4 + sectionTotal5 + sectionTotal6 + sectionTotal7 + sectionCounter + worldRegistry;
         int value2 = (sectionTotal > 0 ? 1 : 0)
            + (sectionTotal2 > 0 ? 1 : 0)
            + (sectionTotal3 > 0 ? 1 : 0)
            + (sectionTotal4 > 0 ? 1 : 0)
            + (sectionTotal5 > 0 ? 1 : 0)
            + (sectionTotal6 > 0 ? 1 : 0)
            + (sectionTotal7 > 0 ? 1 : 0)
            + (sectionCounter > 0 ? 1 : 0)
            + (worldRegistry > 0 ? 1 : 0);
         int flagThreshold = this.getFlagThreshold();
         if (this.smartAdjustment.isEnabled() && worldRegistry == 0 && sectionTotal == 0 && sectionTotal2 == 0 && sectionTotal3 == 0 && sectionTotal4 == 0 && sectionTotal5 == 0) {
            flagThreshold += 12;
         }

         if (value < flagThreshold) {
            return null;
         } else {
            SusChunkFinder.Flag baseSection = new SusChunkFinder.Flag();
            baseSection.score = value;
            baseSection.distinctSignalCount = value2;
            baseSection.blockPositions = items;
            baseSection.minY = sectionLimit == Integer.MAX_VALUE ? (int)mc.player.getY() - 4 : sectionLimit;
            baseSection.maxY = sectionValue == Integer.MIN_VALUE ? (int)mc.player.getY() + 4 : sectionValue;
            return baseSection;
         }
      }
   }

   @Override
   protected void onEnable() {
      this.flaggedChunks.clear();
      this.hashChunk.clearScannedChunks();
      this.tickCounter = 0;
   }

   private static int packArgb(int susChunkValue, int susChunkValue2, int susChunkValue3, int susChunkValue4) {
      return (susChunkValue & 0xFF) << 24 | (susChunkValue2 & 0xFF) << 16 | (susChunkValue3 & 0xFF) << 8 | susChunkValue4 & 0xFF;
   }

   private boolean isAnyBlockNearby(BlockPos pos, List<Block> list) {
      int bottomY = mc.world.getBottomY();
      int bottomWorldLimit = mc.world.getBottomY() + mc.world.getHeight();
      Mutable mutable = new Mutable();

      for (int i = -40; i <= 40; i++) {
         int bottomWorldLimit2 = pos.getY() + i;
         if (bottomWorldLimit2 >= bottomY && bottomWorldLimit2 < bottomWorldLimit) {
            for (int j = -2; j <= 2; j++) {
               for (int k = -2; k <= 2; k++) {
                  mutable.set(pos.getX() + j, bottomWorldLimit2, pos.getZ() + k);
                  Block block = mc.world.getBlockState(mutable).getBlock();

                  for (Block block2 : list) {
                     if (block == block2) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public SusChunkFinder() {
      super("Sus Chunk Finder", IconsPngType.DONUTSMP);
      this.sensitivity = new IntegerSetting(
         "Sensitivity",
         "How eagerly a chunk is flagged. Higher = flags on weaker evidence (more hits, more false positives). Lower = only strong, obvious bases.",
         3,
         1,
         10
      );
      this.smartAdjustment = new BooleanSetting(
         "Smart Adjustment",
         "Auto-raise the bar in biomes where these plants grow naturally (jungle/ocean/lush caves) so wild vegetation doesn't light up the map.",
         true
      );
      this.overlayAlpha = new IntegerSetting("Alpha", "Opacity of the red chunk overlay (0-255).", 80, 0, 255);
      this.kelp = new BooleanSetting("Kelp", "Matured kelp columns (grown to the surface).", true);
      this.caveVines = new BooleanSetting("Cave Vines", "Cave vines that have grown glow berries.", true);
      this.vines = new BooleanSetting("Vines", "Overgrown vine sheets.", true);
      this.amethyst = new BooleanSetting("Amethyst", "Amethyst that has grown to clusters (buds mature only while loaded).", true);
      this.bamboo = new BooleanSetting("Bamboo", "Tall, fully-leaved bamboo.", true);
      this.cocoa = new BooleanSetting("Cocoa", "Ripe (age 2) cocoa pods.", true);
      this.beeNest = new BooleanSetting("Bee Nest", "Bee nests/hives filled with honey (level 5).", true);
      this.rotatedDeepslate = new BooleanSetting(
         "Rotated Deepslate", "Deepslate on the X/Z axis \u2014 natural deepslate is always vertical, so this is player-placed.", true
      );
      this.seed = new StringBase(
         "Seed", "World seed for exact feature-placement prediction. Blank = seedless detection only. Needs vanilla overworld generation.", ""
      );
      this.seedFeatureEngine = new SeedFeatureEngine();
      this.trackedFeatures = new ArrayList<>();
      this.flaggedChunks = new Long2ObjectOpenHashMap();
      this.hashChunk = new ChunkScanScheduler();
      this.addSettings(
         new ConfigSetting[]{
            this.simulationDistance,
            this.sensitivity,
            this.smartAdjustment,
            this.overlayAlpha,
            this.kelp,
            this.caveVines,
            this.vines,
            this.amethyst,
            this.bamboo,
            this.cocoa,
            this.beeNest,
            this.rotatedDeepslate,
            this.seed
         }
      );
      this.trackedFeatures
         .add(
            new SusChunkFinder.Tracked(
               this.kelp, List.of(getPlacedFeatureKey("kelp_cold"), getPlacedFeatureKey("kelp_warm")), List.of(Blocks.KELP, Blocks.KELP_PLANT)
            )
         );
      this.trackedFeatures
         .add(new SusChunkFinder.Tracked(this.caveVines, List.of(getPlacedFeatureKey("cave_vines")), List.of(Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT)));
      this.trackedFeatures.add(new SusChunkFinder.Tracked(this.vines, List.of(getPlacedFeatureKey("vines")), List.of(Blocks.VINE)));
      this.trackedFeatures
         .add(
            new SusChunkFinder.Tracked(
               this.bamboo,
               List.of(getPlacedFeatureKey("bamboo"), getPlacedFeatureKey("bamboo_light"), getPlacedFeatureKey("bamboo_vegetation")),
               List.of(Blocks.BAMBOO, Blocks.BAMBOO_SAPLING)
            )
         );
      this.trackedFeatures
         .add(
            new SusChunkFinder.Tracked(
               this.amethyst,
               List.of(getPlacedFeatureKey("amethyst_geode")),
               List.of(
                  Blocks.BUDDING_AMETHYST,
                  Blocks.AMETHYST_BLOCK,
                  Blocks.AMETHYST_CLUSTER,
                  Blocks.SMALL_AMETHYST_BUD,
                  Blocks.MEDIUM_AMETHYST_BUD,
                  Blocks.LARGE_AMETHYST_BUD,
                  Blocks.CALCITE
               )
            )
         );
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null && !this.flaggedChunks.isEmpty()) {
         Matrix4f matrix = VertexBuilderUtils.getMatrix();
         double x = mc.player.getX();
         double z = mc.player.getZ();
         float susChunkValue = Math.max(0, Math.min(255, this.overlayAlpha.getValue())) / 255.0F;
         float min2 = Math.min(1.0F, susChunkValue + 0.35F);
         int packArgb2 = packArgb(255, 255, 40, 40);
         int packArgb4 = packArgb(Math.min(255, this.overlayAlpha.getValue() + 90), 255, 90, 90);
         int susChunkLimit = 160;
         var fastIterator = this.flaggedChunks.long2ObjectEntrySet().fastIterator();

         while (fastIterator.hasNext()) {
            var entry = (Entry)fastIterator.next();
            long longKey = entry.getLongKey();
            SusChunkFinder.Flag flag = (SusChunkFinder.Flag)entry.getValue();
            double susChunkValue5 = ChunkPos.getPackedX(longKey) << 4;
            double vertexBuilderValue = ChunkPos.getPackedZ(longKey) << 4;
            double susChunkCounter = flag.minY - 1;
            double susChunkTotal = flag.maxY + 2;
            if (susChunkTotal - susChunkCounter < 3.0) {
               susChunkCounter--;
               susChunkTotal += 2.0;
            }

            if (!VertexBuilderUtils.canVector(susChunkValue5 + 8.0, (susChunkCounter + susChunkTotal) * 0.5, vertexBuilderValue + 8.0, 12.0F + (float)(susChunkTotal - susChunkCounter) * 0.5F)) {
               float amountX = VertexBuilderUtils.getAmountX(susChunkValue5);
               float amountForDouble = VertexBuilderUtils.getAmountForDouble(vertexBuilderValue);
               float vertexColorValue = amountX + 16.0F;
               float f6 = amountForDouble + 16.0F;
               float f2 = VertexBuilderUtils.getAmount(susChunkCounter);
               float f5 = VertexBuilderUtils.getAmount(susChunkTotal);
               VertexBuilderUtils.handle(matrix, amountX, f2, amountForDouble, vertexColorValue, f5, f6, 1.0F, 0.15F, 0.15F, susChunkValue);
               VertexBuilderUtils.handle(matrix, amountX, f2, amountForDouble, vertexColorValue, f5, f6, packArgb2, 2.0F);
               if (susChunkLimit > 0) {
                  double susChunkFactor = susChunkValue5 + 8.0 - x;
                  double susChunkFactor2 = vertexBuilderValue + 8.0 - z;
                  if (!(susChunkFactor * susChunkFactor + susChunkFactor2 * susChunkFactor2 > 5184.0)) {
                     var list = flag.blockPositions;
                     int index = 0;

                     for (int i = list.size(); index < i && susChunkLimit > 0; index++) {
                        int[] susChunkValues = (int[])list.get(index);
                        float amountX13 = VertexBuilderUtils.getAmountX(susChunkValues[0]);
                        float susChunkValue14 = VertexBuilderUtils.getAmount(susChunkValues[1]);
                        float amountForDouble15 = VertexBuilderUtils.getAmountForDouble(susChunkValues[2]);
                        VertexBuilderUtils.handle(matrix, amountX13, susChunkValue14, amountForDouble15, amountX13 + 1.0F, susChunkValue14 + 1.0F, amountForDouble15 + 1.0F, 1.0F, 0.35F, 0.35F, min2);
                        VertexBuilderUtils.handle(matrix, amountX13, susChunkValue14, amountForDouble15, amountX13 + 1.0F, susChunkValue14 + 1.0F, amountForDouble15 + 1.0F, packArgb4, 1.25F);
                        susChunkLimit--;
                     }
                  }
               }
            }
         }

         VertexBuilderUtils.updateDrawImmediate();
      }
   }

   private static final class Flag {
      int maxY;
      int score;
      int distinctSignalCount;
      int minY;
      List<int[]> blockPositions = new ArrayList<>();
   }

   private record Tracked(BooleanSetting toggle, List<RegistryKey<PlacedFeature>> keys, List<Block> blocks) {
   }
}
