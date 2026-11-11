package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.ChunkScanScheduler;
import com.corz.client.utils.IconsPngType;

public class SusChunkFinderV2 extends Module {
   private final BooleanSetting vines;
   private boolean zonesDirty;
   private static final int UNIT_STEP = 1;
   private final BooleanSetting bamboo;
   private final IntegerSetting renderY;
   private List<SusChunkFinderV2.Zone> zones;
   private final Long2ObjectOpenHashMap<SusChunkFinderV2.ChunkScore> chunkScores;
   private final LongOpenHashSet announcedChunks;
   private final ChunkScanScheduler hashChunk;
   private final BooleanSetting dripstone;
   private final BooleanSetting kelp;
   private final IntegerSetting sensitivity = new IntegerSetting(
      "Sensitivity", "How much evidence a chunk needs. Lower = flags more chunks, higher = only the obvious ones.", 3, 1, 10
   );
   private final IntegerSetting opacity;
   private List<SusChunkFinderV2.Flag> flaggedChunks;
   private final BooleanSetting berries;
   private int tickCounter;
   private final BooleanSetting amethyst = new BooleanSetting("Amethyst", "Grown amethyst clusters / budding amethyst \u2014 the strongest signal.", true);
   private final EnumSetting notificationMode;
   private int settingsHash;

   private static boolean isTallKelp(ChunkSection[] chunkSections, int kelpPlantValue, BlockState state, int m, int susChunkTotal, int k) {
      Block airBlock = getBlockAt(chunkSections, kelpPlantValue, m, susChunkTotal + 1, k);
      if (airBlock != Blocks.KELP && airBlock != Blocks.KELP_PLANT) {
         int kelpPlantLimit = measureRunLength(chunkSections, kelpPlantValue, m, susChunkTotal, k, -1, Blocks.KELP, Blocks.KELP_PLANT);
         boolean kelpPlantFlag = state.isOf(Blocks.KELP) && state.contains(Properties.AGE_25) && state.get(Properties.AGE_25) == 25;
         return kelpPlantFlag && kelpPlantLimit >= 8 || kelpPlantLimit >= 14;
      } else {
         return false;
      }
   }

   private static boolean isAmethystGrowth(Block block) {
      return block == Blocks.BUDDING_AMETHYST
         || block == Blocks.AMETHYST_CLUSTER
         || block == Blocks.LARGE_AMETHYST_BUD
         || block == Blocks.MEDIUM_AMETHYST_BUD
         || block == Blocks.SMALL_AMETHYST_BUD;
   }

   private void updateChunkScore(WorldChunk worldChunk, long hashPutValue) {
      SusChunkFinderV2.ChunkScore chunkScore;
      try {
         chunkScore = this.scoreChunk(worldChunk, worldChunk.getPos());
      } catch (Throwable e) {
         chunkScore = null;
      }

      SusChunkFinderV2.ChunkScore chunkScore2 = chunkScore != null
         ? (SusChunkFinderV2.ChunkScore)this.chunkScores.put(hashPutValue, chunkScore)
         : (SusChunkFinderV2.ChunkScore)this.chunkScores.remove(hashPutValue);
      if (chunkScore2 == null ? chunkScore != null : !chunkScore2.hasSameScore(chunkScore)) {
         this.zonesDirty = true;
      }
   }

   private void resetState() {
      this.chunkScores.clear();
      this.hashChunk.clearScannedChunks();
      this.announcedChunks.clear();
      this.flaggedChunks = List.of();
      this.zones = List.of();
      this.tickCounter = 0;
      this.zonesDirty = false;
      this.settingsHash = -1;
   }

   public SusChunkFinderV2() {
      super("SusChunkFinderV2", IconsPngType.DONUTSMP);
      this.kelp = new BooleanSetting("Kelp", "Fully grown / unusually tall kelp columns.", true);
      this.bamboo = new BooleanSetting("Bamboo", "Fully grown, max-height bamboo.", true);
      this.berries = new BooleanSetting("Berries", "Sweet berry bushes at max growth stage.", true);
      this.vines = new BooleanSetting("Vines", "Vines grown far down from their support.", true);
      this.dripstone = new BooleanSetting("Dripstone", "Dripstone spikes longer than natural generation.", true);
      this.renderY = new IntegerSetting("Render Y", "Height the flat chunk highlights render at.", 100, -64, 320);
      this.opacity = new IntegerSetting("Opacity", "How solid the chunk highlights look (0-255).", 90, 0, 255);
      this.notificationMode = new EnumSetting("Notifications", "Announce each new zone once, with coords and distance.", "Toast", "Toast", "Chat", "Off");
      this.chunkScores = new Long2ObjectOpenHashMap();
      this.hashChunk = new ChunkScanScheduler();
      this.announcedChunks = new LongOpenHashSet();
      this.flaggedChunks = List.of();
      this.zones = List.of();
      this.settingsHash = -1;
      this.addSettings(
         new ConfigSetting[]{
            this.sensitivity,
            this.amethyst,
            this.kelp,
            this.bamboo,
            this.berries,
            this.vines,
            this.dripstone,
            this.renderY,
            this.opacity,
            this.notificationMode
         }
      );
   }

   private static int classifyBlock(Block block, boolean kelpPointedFlag, boolean enabled, boolean kelpPointedFlag3, boolean kelpPointedFlag4, boolean kelpPointedFlag5, boolean kelpPointedFlag6) {
      if (kelpPointedFlag && isAmethystGrowth(block)) {
         return 1;
      } else if (!enabled || block != Blocks.KELP && block != Blocks.KELP_PLANT) {
         if (kelpPointedFlag3 && block == Blocks.BAMBOO) {
            return 3;
         } else if (kelpPointedFlag4 && block == Blocks.SWEET_BERRY_BUSH) {
            return 4;
         } else if (kelpPointedFlag5 && block == Blocks.VINE) {
            return 5;
         } else {
            return kelpPointedFlag6 && block == Blocks.POINTED_DRIPSTONE ? 6 : 0;
         }
      } else {
         return 2;
      }
   }

   private static boolean isLongVine(ChunkSection[] chunkSections, int vineValue, int m, int susChunkTotal, int k) {
      return getBlockAt(chunkSections, vineValue, m, susChunkTotal + 1, k) == Blocks.VINE ? false : measureRunLength(chunkSections, vineValue, m, susChunkTotal, k, -1, Blocks.VINE, Blocks.VINE) >= 7;
   }

   private static boolean isLongDripstone(ChunkSection[] chunkSections, int pointedDripstoneValue, int m, int susChunkTotal, int k) {
      return getBlockAt(chunkSections, pointedDripstoneValue, m, susChunkTotal + 1, k) != Blocks.POINTED_DRIPSTONE
            && getBlockAt(chunkSections, pointedDripstoneValue, m, susChunkTotal - 1, k) == Blocks.POINTED_DRIPSTONE
         ? measureRunLength(chunkSections, pointedDripstoneValue, m, susChunkTotal, k, -1, Blocks.POINTED_DRIPSTONE, Blocks.POINTED_DRIPSTONE) >= 5
         : false;
   }

   private int computeSettingsHash() {
      return this.sensitivity.getValue()
         | (this.amethyst.isEnabled() ? 256 : 0)
         | (this.kelp.isEnabled() ? 512 : 0)
         | (this.bamboo.isEnabled() ? 1024 : 0)
         | (this.berries.isEnabled() ? 2048 : 0)
         | (this.vines.isEnabled() ? 4096 : 0)
         | (this.dripstone.isEnabled() ? 8192 : 0);
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null) {
         var copyOf = this.flaggedChunks;
         if (!copyOf.isEmpty()) {
            Matrix4f matrix = VertexBuilderUtils.getMatrix();
            int susChunkValue = MatrixEspUtils.releaseRightValue & 16777215;
            double susChunkValue2 = this.renderY.getValue();
            float deltaY = VertexBuilderUtils.getAmount(susChunkValue2);
            int susChunkValue4 = clampToByte(this.opacity.getValue()) << 24 | susChunkValue;
            int susChunkValue5 = -939524096 | susChunkValue;
            int index = 0;

            for (int i = copyOf.size(); index < i; index++) {
               SusChunkFinderV2.Flag flag = (SusChunkFinderV2.Flag)copyOf.get(index);
               double susChunkValue6 = ChunkPos.getPackedX(flag.packedChunkPos) << 4;
               double vertexBuilderValue = ChunkPos.getPackedZ(flag.packedChunkPos) << 4;
               if (!VertexBuilderUtils.canVector(susChunkValue6 + 8.0, susChunkValue2, vertexBuilderValue + 8.0, 12.0F)) {
                  float amountX = VertexBuilderUtils.getAmountX(susChunkValue6);
                  float amountForDouble = VertexBuilderUtils.getAmountForDouble(vertexBuilderValue);
                  float susChunkValue10 = amountX + 16.0F;
                  float susChunkValue11 = amountForDouble + 16.0F;
                  VertexBuilderUtils.handle(matrix, amountX, deltaY, amountForDouble, amountX, deltaY, susChunkValue11, susChunkValue10, deltaY, susChunkValue11, susChunkValue10, deltaY, amountForDouble, susChunkValue4);
                  VertexBuilderUtils.handleForMatrix4fFloat(matrix, amountX, deltaY, amountForDouble, susChunkValue10, deltaY, amountForDouble, susChunkValue5, 2.0F);
                  VertexBuilderUtils.handleForMatrix4fFloat(matrix, susChunkValue10, deltaY, amountForDouble, susChunkValue10, deltaY, susChunkValue11, susChunkValue5, 2.0F);
                  VertexBuilderUtils.handleForMatrix4fFloat(matrix, susChunkValue10, deltaY, susChunkValue11, amountX, deltaY, susChunkValue11, susChunkValue5, 2.0F);
                  VertexBuilderUtils.handleForMatrix4fFloat(matrix, amountX, deltaY, susChunkValue11, amountX, deltaY, amountForDouble, susChunkValue5, 2.0F);
               }
            }

            int playerValue = -234881024 | susChunkValue;
            var list = this.zones;
            int index2 = 0;

            for (int j = list.size(); index2 < j; index2++) {
               SusChunkFinderV2.Zone zone = (SusChunkFinderV2.Zone)list.get(index2);
               if (!VertexBuilderUtils.canVector(zone.centerX, susChunkValue2 + 3.0, zone.centerZ, 4.0F)) {
                  float amountX13 = VertexBuilderUtils.getAmountX(zone.centerX);
                  float amountForDouble14 = VertexBuilderUtils.getAmountForDouble(zone.centerZ);
                  VertexBuilderUtils.handleForMatrix4fFloat(matrix, amountX13, deltaY, amountForDouble14, amountX13, deltaY + 6.0F, amountForDouble14, playerValue, 3.0F);
                  VertexBuilderUtils.handleForMatrix4fFloat(matrix, amountX13 - 1.5F, deltaY + 0.05F, amountForDouble14, amountX13 + 1.5F, deltaY + 0.05F, amountForDouble14, playerValue, 2.0F);
                  VertexBuilderUtils.handleForMatrix4fFloat(matrix, amountX13, deltaY + 0.05F, amountForDouble14 - 1.5F, amountX13, deltaY + 0.05F, amountForDouble14 + 1.5F, playerValue, 2.0F);
               }
            }

            VertexBuilderUtils.updateDrawImmediate();
         }
      }
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         try {
            int value = this.computeSettingsHash() ^ System.identityHashCode(mc.world) << 14;
            if (value != this.settingsHash) {
               if (this.settingsHash != -1) {
                  this.chunkScores.clear();
                  this.hashChunk.clearScannedChunks();
                  this.zonesDirty = true;
               }

               this.settingsHash = value;
            }

            ChunkPos chunkPos = mc.player.getChunkPos();
            int iIiValue = Math.min(mc.options.getViewDistance().getValue() + 1, 16);
            this.hashChunk.scanChunks(mc.world, chunkPos.x, chunkPos.z, iIiValue, 12000L, ChunkScanScheduler.getFpsScaledBudgetNanos(400), this::updateChunkScore);
            this.tickCounter++;
            if (this.tickCounter % 40 == 0) {
               int iIiValue2 = iIiValue + 2;
               var fastIterator = this.chunkScores.long2ObjectEntrySet().fastIterator();

               while (fastIterator.hasNext()) {
                  long longKey = ((Entry)fastIterator.next()).getLongKey();
                  if (Math.abs(ChunkPos.getPackedX(longKey) - chunkPos.x) > iIiValue2 || Math.abs(ChunkPos.getPackedZ(longKey) - chunkPos.z) > iIiValue2) {
                     fastIterator.remove();
                     this.zonesDirty = true;
                  }
               }

               this.hashChunk.removeFarChunks(chunkPos.x, chunkPos.z, iIiValue2 + 2);
            }

            if (this.zonesDirty && this.tickCounter % 10 == 0) {
               this.zonesDirty = false;
               this.rebuildZones();
            }
         } catch (Exception e) {
         }
      }
   }

   private SusChunkFinderV2.ChunkScore scoreChunk(WorldChunk worldChunk, ChunkPos chunkPos) {
      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      int bottomY = worldChunk.getBottomY();
      boolean booleanValue = this.amethyst.isEnabled();
      boolean enabled = this.kelp.isEnabled();
      boolean enabled3 = this.bamboo.isEnabled();
      boolean enabled4 = this.berries.isEnabled();
      boolean enabled5 = this.vines.isEnabled();
      boolean enabled6 = this.dripstone.isEnabled();
      if (!booleanValue && !enabled && !enabled3 && !enabled4 && !enabled5 && !enabled6) {
         return null;
      } else {
         int ageCounter = 0;
         int ageCounter2 = 0;
         int ageCounter3 = 0;
         int ageCounter4 = 0;
         int ageCounter5 = 0;
         int ageCounter6 = 0;
         double ageTotal = 0.0;
         double ageTotal2 = 0.0;
         double ageTotal3 = 0.0;
         Block block = null;
         int ageValue = 0;

         for (int i = 0; i < sectionArray.length; i++) {
            ChunkSection chunkSection = sectionArray[i];
            if (chunkSection != null && !chunkSection.isEmpty() && chunkSection.hasAny(doVines -> classifyBlock(doVines.getBlock(), booleanValue, enabled, enabled3, enabled4, enabled5, enabled6) != 0)) {
               int ageValue2 = bottomY + (i << 4);

               for (int j = 0; j < 16; j++) {
                  for (int k = 0; k < 16; k++) {
                     for (int m = 0; m < 16; m++) {
                        BlockState blockState = chunkSection.getBlockState(m, j, k);
                        Block block2 = blockState.getBlock();
                        if (block2 != block) {
                           block = block2;
                           ageValue = classifyBlock(block2, booleanValue, enabled, enabled3, enabled4, enabled5, enabled6);
                        }

                        if (ageValue != 0) {
                           int susChunkTotal = ageValue2 + j;
                           double ageFactor = 0.0;
                           switch (ageValue) {
                              case 1:
                                 if (ageCounter < 16) {
                                    ageCounter++;
                                    ageFactor = 6.0;
                                 }
                                 break;
                              case 2:
                                 if (ageCounter2 < 6 && isTallKelp(sectionArray, bottomY, blockState, m, susChunkTotal, k)) {
                                    ageCounter2++;
                                    ageFactor = 2.0;
                                 }
                                 break;
                              case 3:
                                 if (ageCounter3 < 6 && isTallBamboo(sectionArray, bottomY, m, susChunkTotal, k)) {
                                    ageCounter3++;
                                    ageFactor = 2.0;
                                 }
                                 break;
                              case 4:
                                 if (ageCounter4 < 6 && blockState.contains(Properties.AGE_3) && blockState.get(Properties.AGE_3) == 3) {
                                    ageCounter4++;
                                    ageFactor = 2.0;
                                 }
                                 break;
                              case 5:
                                 if (ageCounter5 < 6 && isLongVine(sectionArray, bottomY, m, susChunkTotal, k)) {
                                    ageCounter5++;
                                    ageFactor = 2.0;
                                 }
                                 break;
                              case 6:
                                 if (ageCounter6 < 6 && isLongDripstone(sectionArray, bottomY, m, susChunkTotal, k)) {
                                    ageCounter6++;
                                    ageFactor = 2.0;
                                 }
                           }

                           if (ageFactor > 0.0) {
                              ageTotal += ageFactor;
                              ageTotal2 += ((chunkPos.x << 4) + m + 0.5) * ageFactor;
                              ageTotal3 += ((chunkPos.z << 4) + k + 0.5) * ageFactor;
                           }
                        }
                     }
                  }
               }
            }
         }

         double ageValue4 = ageCounter * 6.0 + (ageCounter2 + ageCounter3 + ageCounter4 + ageCounter5 + ageCounter6) * 2.0;
         return ageValue4 < this.getMinScore() ? null : new SusChunkFinderV2.ChunkScore(chunkPos.toLong(), ageValue4, ageTotal, ageTotal2, ageTotal3);
      }
   }

   private static int clampToByte(int susChunkBound) {
      return Math.max(0, Math.min(255, susChunkBound));
   }

   private List<SusChunkFinderV2.Zone> clusterIntoZones(List<SusChunkFinderV2.Flag> list, Map<Long, SusChunkFinderV2.ChunkScore> map) {
      byte packedLimit = 3;
      var hashMap = new HashMap();

      for (SusChunkFinderV2.Flag flag : list) {
         hashMap.put(flag.packedChunkPos, flag);
      }

      var arrayList = new ArrayList();
      var hashSet = new HashSet();

      for (SusChunkFinderV2.Flag flag2 : list) {
         if (hashSet.add(flag2.packedChunkPos)) {
            var arrayList2 = new ArrayList();
            var arrayDeque = new ArrayDeque();
            arrayDeque.add(flag2);

            while (!arrayDeque.isEmpty()) {
               SusChunkFinderV2.Flag susChunkFinderV2Flag = (SusChunkFinderV2.Flag)arrayDeque.poll();
               arrayList2.add(susChunkFinderV2Flag);
               int packedX = ChunkPos.getPackedX(susChunkFinderV2Flag.packedChunkPos);
               int packedZ = ChunkPos.getPackedZ(susChunkFinderV2Flag.packedChunkPos);

               for (int i = -packedLimit; i <= packedLimit; i++) {
                  for (int j = -packedLimit; j <= packedLimit; j++) {
                     if (i != 0 || j != 0) {
                        SusChunkFinderV2.Flag blockPos = (SusChunkFinderV2.Flag)hashMap.get(ChunkPos.toLong(packedX + i, packedZ + j));
                        if (blockPos != null && hashSet.add(blockPos.packedChunkPos)) {
                           arrayDeque.add(blockPos);
                        }
                     }
                  }
               }
            }

            arrayList.add(this.buildZone(arrayList2, map));
         }
      }

      arrayList.sort(Comparator.<SusChunkFinderV2.Zone>comparingDouble(zone -> zone.zoneScore).reversed());
      return arrayList;
   }

   private void rebuildZones() {
      int integer = this.getMinScore();
      var arrayList = new ArrayList();
      var hashMap = new HashMap();
      var iterator = this.chunkScores.values().iterator();

      while (iterator.hasNext()) {
         SusChunkFinderV2.ChunkScore chunkScore = (SusChunkFinderV2.ChunkScore)iterator.next();
         if (!(chunkScore.score < integer)) {
            arrayList.add(new SusChunkFinderV2.Flag(chunkScore.packedChunkPos, chunkScore.score));
            hashMap.put(chunkScore.packedChunkPos, chunkScore);
         }
      }

      this.flaggedChunks = List.copyOf(arrayList);
      this.zones = this.clusterIntoZones(arrayList, hashMap);
      this.announceNewZones();
   }

   private static boolean isTallBamboo(ChunkSection[] chunkSections, int bambooValue, int m, int susChunkTotal, int k) {
      return getBlockAt(chunkSections, bambooValue, m, susChunkTotal - 1, k) == Blocks.BAMBOO
         ? false
         : measureRunLength(chunkSections, bambooValue, m, susChunkTotal, k, 1, Blocks.BAMBOO, Blocks.BAMBOO) >= 12;
   }

   private SusChunkFinderV2.Zone buildZone(List<SusChunkFinderV2.Flag> list, Map<Long, SusChunkFinderV2.ChunkScore> map) {
      var hashSet = new HashSet();
      double packedTotal = 0.0;
      double packedTotal2 = 0.0;
      double packedTotal3 = 0.0;
      double packedTotal4 = 0.0;
      double packedTotal5 = 0.0;
      double packedTotal6 = 0.0;

      for (SusChunkFinderV2.Flag flag : list) {
         hashSet.add(flag.packedChunkPos);
         packedTotal += flag.weight;
         SusChunkFinderV2.ChunkScore bykey = (SusChunkFinderV2.ChunkScore)map.get(flag.packedChunkPos);
         if (bykey != null && bykey.totalWeight > 0.0) {
            packedTotal2 += bykey.totalWeight;
            packedTotal3 += bykey.weightedX;
            packedTotal4 += bykey.weightedZ;
         }

         packedTotal5 += (ChunkPos.getPackedX(flag.packedChunkPos) * 16 + 8) * flag.weight;
         packedTotal6 += (ChunkPos.getPackedZ(flag.packedChunkPos) * 16 + 8) * flag.weight;
      }

      double packedValue = packedTotal2 > 0.0 ? packedTotal3 / packedTotal2 : packedTotal5 / packedTotal;
      double packedValue2 = packedTotal2 > 0.0 ? packedTotal4 / packedTotal2 : packedTotal6 / packedTotal;
      return new SusChunkFinderV2.Zone(Set.copyOf(hashSet), packedValue, packedValue2, packedTotal);
   }

   private void announceNewZones() {
      if (mc.player != null) {
         for (SusChunkFinderV2.Zone zone : this.zones) {
            boolean susToastFlag = true;

            for (long susToastValue : zone.chunkKeys) {
               if (this.announcedChunks.contains(susToastValue)) {
                  susToastFlag = false;
                  break;
               }
            }

            for (long susToastValue2 : zone.chunkKeys) {
               this.announcedChunks.add(susToastValue2);
            }

            if (susToastFlag && !this.notificationMode.getCurrentOption().equals("Off")) {
               int round3 = (int)Math.round(zone.centerX);
               int round4 = (int)Math.round(zone.centerZ);
               int hypot = (int)Math.hypot(round3 - mc.player.getX(), round4 - mc.player.getZ());
               String susToastText = "Sus zone \u00b7 " + hypot + "m \u00b7 " + round3 + ", " + round4;
               if (this.notificationMode.getCurrentOption().equals("Toast")) {
                  DisabledEnabled.getInstance().handleDisabledEnabled("Sus Chunk", susToastText, true);
               } else if (this.notificationMode.getCurrentOption().equals("Chat")) {
                  mc.player.sendMessage(Text.literal("\u00a7d[Corz] \u00a7f" + susToastText), false);
               }
            }
         }
      }
   }

   private int getMinScore() {
      return this.sensitivity.getValue() * 5;
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   private static int measureRunLength(ChunkSection[] chunkSections, int susChunkValue, int m, int susChunkTotal, int k, int susChunkValue4, Block block, Block block2) {
      int susChunkCounter;
      for (susChunkCounter = 1; susChunkCounter < 40; susChunkCounter++) {
         susChunkTotal += susChunkValue4;
         Block airBlock = getBlockAt(chunkSections, susChunkValue, m, susChunkTotal, k);
         if (airBlock != block && airBlock != block2) {
            break;
         }
      }

      return susChunkCounter;
   }

   private static Block getBlockAt(ChunkSection[] chunkSections, int airBlockValue, int m, int susChunkTotal, int k) {
      int index = susChunkTotal - airBlockValue >> 4;
      if (index >= 0 && index < chunkSections.length) {
         ChunkSection chunkSection = chunkSections[index];
         return chunkSection != null && !chunkSection.isEmpty() ? chunkSection.getBlockState(m, susChunkTotal & 15, k).getBlock() : Blocks.AIR;
      } else {
         return Blocks.VOID_AIR;
      }
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   private static final class ChunkScore {
      final double score;
      final double weightedZ;
      final double weightedX;
      final double totalWeight;
      final long packedChunkPos;

      boolean hasSameScore(SusChunkFinderV2.ChunkScore chunkScore) {
         return chunkScore != null && this.score == chunkScore.score && this.totalWeight == chunkScore.totalWeight && this.weightedX == chunkScore.weightedX && this.weightedZ == chunkScore.weightedZ;
      }

      ChunkScore(long longValue, double amount, double amount2, double amount3, double amount4) {
         this.packedChunkPos = longValue;
         this.score = amount;
         this.totalWeight = amount2;
         this.weightedX = amount3;
         this.weightedZ = amount4;
      }
   }

   private static final class Flag {
      final double weight;
      final long packedChunkPos;

      Flag(long time, double amount) {
         this.packedChunkPos = time;
         this.weight = amount;
      }
   }

   private static final class Zone {
      final double zoneScore;
      final double centerZ;
      final Set<Long> chunkKeys;
      final double centerX;

      Zone(Set<Long> copyOf, double amount, double amount2, double amount3) {
         this.chunkKeys = copyOf;
         this.centerX = amount;
         this.centerZ = amount2;
         this.zoneScore = amount3;
      }
   }
}
