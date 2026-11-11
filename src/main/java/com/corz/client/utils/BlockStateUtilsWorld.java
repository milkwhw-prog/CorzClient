package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.BrickStoneUtils;
import com.corz.client.utils.CraftedExcavation;
import com.corz.client.utils.BiomeCategoryMapper;
import com.corz.client.utils.StateBlockFlag;
import com.corz.client.utils.ExcavationTypeStats;
import com.corz.client.utils.ExcavationScanner;
import com.corz.client.utils.ExcavationCraftedType;
import com.corz.client.utils.SensorBlock;
import com.corz.client.utils.ConstChunkUtils;

public final class BlockStateUtilsWorld {
   private static final Direction[] ALL_DIRECTIONS = Direction.values();

   private static long[] getTopLayerOpaqueMask(ChunkSection chunkSection) {
      if (chunkSection != null && !chunkSection.isEmpty()) {
         long[] longs = new long[4];

         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               BlockState blockState = chunkSection.getBlockState(j, 15, i);
               if (!blockState.isAir() && blockState.isOpaqueFullCube()) {
                  int opaqueCubeValue = i << 4 | j;
                  longs[opaqueCubeValue >>> 6] = longs[opaqueCubeValue >>> 6] | 1L << (opaqueCubeValue & 63);
               }
            }
         }

         return longs;
      } else {
         return null;
      }
   }

   private static int countSolidNeighbors(
      WorldChunk worldChunk, World world, ChunkSection chunkSection, int offsetValue, int m, int q, int offsetValue4, int offsetValue5, int offsetValue6, int offsetLimit, int offsetLightLimit
   ) {
      int offsetState = 0;

      for (Direction direction : ALL_DIRECTIONS) {
         int offsetLimit2 = offsetValue + direction.getOffsetX();
         int offsetLimit3 = m + direction.getOffsetY();
         int offsetLimit4 = q + direction.getOffsetZ();
         BlockState blockState;
         if (offsetLimit2 >= 0 && offsetLimit2 < 16 && offsetLimit3 >= 0 && offsetLimit3 < 16 && offsetLimit4 >= 0 && offsetLimit4 < 16) {
            blockState = chunkSection.getBlockState(offsetLimit2, offsetLimit3, offsetLimit4);
         } else {
            int x = offsetValue4 + direction.getOffsetX();
            int y = offsetValue5 + direction.getOffsetY();
            int z = offsetValue6 + direction.getOffsetZ();
            if (y < offsetLimit || y > offsetLightLimit) {
               offsetState++;
               continue;
            }

            if (offsetLimit2 >= 0 && offsetLimit2 < 16 && offsetLimit4 >= 0 && offsetLimit4 < 16) {
               blockState = worldChunk.getBlockState(new BlockPos(x, y, z));
            } else {
               blockState = world.getBlockState(new BlockPos(x, y, z));
            }
         }

         if (blockState.isAir() || blockState.isLiquid()) {
            return -1;
         }

         if (BrickStoneUtils.isSolidForScan(blockState)) {
            offsetState++;
         }
      }

      return offsetState;
   }

   private static int getSpawnerSensorId(World world, WorldChunk worldChunk, BlockPos pos2) {
      try {
         if (worldChunk.getBlockEntity(pos2) instanceof MobSpawnerBlockEntity mobSpawnerBlockEntity) {
            Entity renderedEntity = mobSpawnerBlockEntity.getLogic().getRenderedEntity(world, pos2);
            if (renderedEntity == null) {
               return 132;
            }

            String path = EntityType.getId(renderedEntity.getType()).getPath();
            switch (path) {
               case "zombie":
               case "skeleton":
               case "spider":
                  return 132;
               case "cave_spider":
                  return 133;
               case "blaze":
                  return 134;
               case "magma_cube":
                  return 135;
               case "silverfish":
                  return 136;
               default:
                  return 138;
            }
         }
      } catch (Throwable e) {
      }

      return 132;
   }

   private BlockStateUtilsWorld() {
   }

   private static CraftedExcavation buildCraftedExcavation(int craftedExcavationValue, int y, int z, BlockState state2, BrickStoneUtils.Info blockInfo, int craftedExcavationValue4, int craftedExcavationValue5) {
      int blockState = BrickStoneUtils.getFacingIndex(state2);
      int axisHorizontal = BrickStoneUtils.getAxisIndex(state2);
      return blockInfo.craftedExcavationValue != 0
         ? CraftedExcavation.createFull(craftedExcavationValue, y, z, blockInfo.excavationCraftedType, blockInfo.priorityWeight, blockInfo.craftedExcavationValue, blockInfo.variantIndex, blockInfo.sensorId, craftedExcavationValue4, craftedExcavationValue5)
         : new CraftedExcavation(craftedExcavationValue, y, z, blockInfo.excavationCraftedType, blockInfo.priorityWeight, blockState, axisHorizontal, blockInfo.variantIndex, 0, blockInfo.sensorId, craftedExcavationValue4, craftedExcavationValue5);
   }

   private static boolean isBackedByBuddingAmethyst(WorldChunk worldChunk, World world, ChunkSection chunkSection, BlockState state2, int offsetBottomValue, int m, int q, int offsetBottomValue4, int offsetBottomValue5, int offsetBottomValue6) {
      if (!state2.contains(Properties.FACING)) {
         return true;
      } else {
         Direction opposite = state2.get(Properties.FACING).getOpposite();
         int offsetBottomLimit = offsetBottomValue + opposite.getOffsetX();
         int offsetBottomLimit2 = m + opposite.getOffsetY();
         int offsetBottomLimit3 = q + opposite.getOffsetZ();
         BlockState blockState;
         if (offsetBottomLimit >= 0 && offsetBottomLimit < 16 && offsetBottomLimit2 >= 0 && offsetBottomLimit2 < 16 && offsetBottomLimit3 >= 0 && offsetBottomLimit3 < 16) {
            blockState = chunkSection.getBlockState(offsetBottomLimit, offsetBottomLimit2, offsetBottomLimit3);
         } else {
            int x = offsetBottomValue4 + opposite.getOffsetX();
            int y = offsetBottomValue5 + opposite.getOffsetY();
            int z = offsetBottomValue6 + opposite.getOffsetZ();
            if (x >> 4 == offsetBottomValue4 >> 4 && z >> 4 == offsetBottomValue6 >> 4 && y >= worldChunk.getBottomY() && y <= worldChunk.getBottomY() + worldChunk.getHeight() - 1) {
               blockState = worldChunk.getBlockState(new BlockPos(x, y, z));
            } else {
               blockState = world.getBlockState(new BlockPos(x, y, z));
            }
         }

         return blockState.isOf(Blocks.BUDDING_AMETHYST);
      }
   }

   public static BlockStateUtilsWorld.ChunkScan scanChunk(
      WorldChunk worldChunk,
      World world,
      BiomeCategoryMapper forestDark,
      int classValue,
      int biomeCraftedLimit,
      Map<Long, StateBlockFlag.Entry> map,
      boolean classFlag,
      long sectionZeroed
   ) {
      return getBiomeCrafted(worldChunk, world, forestDark, classValue, biomeCraftedLimit, map, classFlag, sectionZeroed, 0L);
   }

   public static int getDimensionIndex(World world) {
      String netherEndText = world.getRegistryKey().getValue().toString();
      if (netherEndText.endsWith("the_nether")) {
         return 1;
      } else {
         return netherEndText.endsWith("the_end") ? 2 : 0;
      }
   }

   private static boolean isStructureSensorId(int blockWorldValue) {
      switch (blockWorldValue) {
         case 34:
         case 113:
         case 116:
         case 117:
         case 119:
         case 120:
         case 123:
         case 124:
         case 127:
         case 128:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
            return true;
         default:
            return false;
      }
   }

   public static BlockStateUtilsWorld.ChunkScan getClassForWorldChunkWorld(
      WorldChunk worldChunk, World world, BiomeCategoryMapper forestDark, int classValue, int biomeCraftedLimit, Map<Long, StateBlockFlag.Entry> map, boolean classFlag
   ) {
      return getBiomeCrafted(worldChunk, world, forestDark, classValue, biomeCraftedLimit, map, classFlag, 0L, 0L);
   }

   public static BlockStateUtilsWorld.ChunkScan getBiomeCrafted(
      WorldChunk worldChunk,
      World world,
      BiomeCategoryMapper forestDark,
      int biomeCraftedValue,
      int biomeCraftedLimit,
      Map<Long, StateBlockFlag.Entry> map,
      boolean biomeCraftedFlag,
      long sectionZeroed,
      long sectionStatuss
   ) {
      long nanoTime = System.nanoTime();
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      ExcavationTypeStats flagsHandle = new ExcavationTypeStats();
      int value = getDimensionIndex(world);
      flagsHandle.biomeCraftedValue = value;
      int biomeCraftedTotal = 0;
      int biomeCraftedCounter = 0;
      int biomeCraftedCounter2 = 0;
      int biomeCraftedCounter3 = 0;
      int biomeCraftedCounter4 = 0;
      int biomeCraftedCounter5 = 0;
      int constChunkLimit = 0;
      int constChunkValue = 0;
      long biomeCraftedValue4 = 0L;
      long biomeCraftedValue5 = 0L;
      ChunkPos pos = worldChunk.getPos();
      int startX = pos.getStartX();
      int startZ = pos.getStartZ();
      int bottomY = worldChunk.getBottomY();
      int offsetLightLimit = bottomY + worldChunk.getHeight() - 1;
      int biomeCraftedCounter8 = 0;
      int[] biomeCraftedValues = null;
      int minValue = Integer.MIN_VALUE;
      if (biomeCraftedFlag || sectionStatuss != 0L) {
         biomeCraftedValues = new int[256];

         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               int sampleHeightmap = worldChunk.sampleHeightmap(Type.WORLD_SURFACE, j, i);
               biomeCraftedValues[i * 16 + j] = sampleHeightmap;
               if (sampleHeightmap > minValue) {
                  minValue = sampleHeightmap;
               }
            }
         }
      }

      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      long[] topLayerBits2 = null;
      int biomeCraftedValue7 = -2;

      for (int k = 0; k < sectionArray.length; k++) {
         int biomeCraftedValue8 = bottomY + (k << 4);
         if (biomeCraftedValue8 <= biomeCraftedLimit && biomeCraftedValue8 + 15 >= biomeCraftedValue) {
            ChunkSection chunkSection = sectionArray[k];
            boolean biomeCraftedFlag2 = k < 64 && (sectionZeroed & 1L << k) != 0L;
            boolean biomeCraftedFlag3 = k < 64 && (sectionStatuss & 1L << k) != 0L;
            if (chunkSection != null && !chunkSection.isEmpty() && (biomeCraftedFlag2 || biomeCraftedFlag3 || chunkSection.hasAny(BrickStoneUtils::isPlacedStructureBlock))) {
               biomeCraftedCounter++;
               int biomeCraftedValue9 = biomeCraftedFlag2 ? 128 : 0;
               int biomeCraftedCounter9 = 0;
               boolean any = chunkSection.hasAny(AbstractBlockState::isAir);
               ExcavationScanner.SectionMask sectionMask = biomeCraftedFlag && any && biomeCraftedValue8 <= minValue - 2 ? new ExcavationScanner.SectionMask() : null;
               if (sectionMask != null) {
                  long[] times = biomeCraftedValue7 == k - 1 ? topLayerBits2 : getTopLayerOpaqueMask(k > 0 ? sectionArray[k - 1] : null);
                  sectionMask.setBelowLayerBits(times);
               }

               int biomeCraftedCounter10 = 0;
               RegistryEntry registryEntry = null;
               int biomeCategory10 = 0;

               for (int m = 0; m < 16; m++) {
                  int y = biomeCraftedValue8 + m;
                  if (y >= biomeCraftedValue && y <= biomeCraftedLimit) {
                     for (int p = 0; p < 16; p++) {
                        for (int q = 0; q < 16; q++) {
                           BlockState blockState = chunkSection.getBlockState(p, m, q);
                           boolean air = blockState.isAir();
                           boolean biomeCraftedFlag4 = !air && blockState.isOpaqueFullCube();
                           if (sectionMask != null) {
                              if (air) {
                                 sectionMask.setBitsetBit(p, m, q);
                              } else if (biomeCraftedFlag4) {
                                 sectionMask.setMaskBit(p, m, q);
                              }
                           }

                           if (biomeCraftedFlag3
                              && biomeCraftedFlag4
                              && blockState.getLuminance() == 0
                              && biomeCraftedCounter8 < 48
                              && biomeCraftedCounter9 < 8
                              && biomeCraftedValues != null
                              && biomeCraftedValues[q * 16 + p] - y >= 6) {
                              int classValue = startX + p;
                              int biomeCraftedValue12 = startZ + q;
                              if (isIsolatedLightSource(world, worldChunk, chunkSection, p, m, q, classValue, y, biomeCraftedValue12, bottomY, offsetLightLimit)) {
                                 RegistryEntry biome = chunkSection.getBiome(p >> 2, m >> 2, q >> 2);
                                 int biomeCategory = forestDark.getBiomeCategory(biome, value);
                                 flagsHandle.incrementTypeCount(146);
                                 flagsHandle.incrementGroupCount(biomeCategory);
                                 flagsHandle.recordWorldLoadedValue(y);
                                 arrayList.add(CraftedExcavation.createTyped(classValue, y, biomeCraftedValue12, ExcavationCraftedType.LIGHTING, 3, 146, biomeCraftedValue9, biomeCategory));
                                 biomeCraftedCounter8++;
                                 biomeCraftedCounter9++;
                                 continue;
                              }
                           }

                           BrickStoneUtils.Info ifAbsent = BrickStoneUtils.getBlockInfo(blockState.getBlock());
                           if (ifAbsent == BrickStoneUtils.EMPTY_INFO) {
                              if (!air) {
                                 if (biomeCraftedCounter10 < 96) {
                                    biomeCraftedCounter10++;
                                 }

                                 if (biomeCraftedFlag4) {
                                    biomeCraftedCounter4++;
                                 }
                              }
                           } else {
                              RegistryEntry biome2 = chunkSection.getBiome(p >> 2, m >> 2, q >> 2);
                              if (biome2 != registryEntry) {
                                 registryEntry = biome2;
                                 biomeCategory10 = forestDark.getBiomeCategory(biome2, value);
                              }

                              int x = startX + p;
                              int z = startZ + q;
                              int biomeCraftedValue14 = biomeCraftedValue9;
                              BrickStoneUtils.Info info = ifAbsent;
                              int blockWorldValue = 0;
                              if (ifAbsent.biomeCraftedValue != 0) {
                                 switch (ifAbsent.biomeCraftedValue) {
                                    case 1:
                                       info = value == 1 ? null : ifAbsent;
                                       break;
                                    case 2:
                                       if (biomeCategory10 == 2) {
                                          info = null;
                                          blockWorldValue = 118;
                                       } else {
                                          info = BrickStoneUtils.CRAFTED_SENSOR_INFO_OF;
                                       }
                                       break;
                                    case 3:
                                       if (biomeCategory10 == 2) {
                                          info = null;
                                          blockWorldValue = 118;
                                       } else {
                                          info = BrickStoneUtils.REDSTONE_SENSOR_INFO_OF;
                                       }
                                       break;
                                    case 4:
                                       if (biomeCategory10 == 2) {
                                          info = null;
                                          blockWorldValue = 117;
                                       } else {
                                          info = BrickStoneUtils.UTILITY_SENSOR_INFO;
                                       }
                                       break;
                                    case 5:
                                       info = biomeCategory10 == 3 ? null : BrickStoneUtils.CRAFTED_SENSOR_97;
                                       break;
                                    case 6:
                                       boolean offsetBottom = isBackedByBuddingAmethyst(worldChunk, world, chunkSection, blockState, p, m, q, x, y, z);
                                       if (offsetBottom) {
                                          info = null;
                                          blockWorldValue = 131;
                                       } else {
                                          info = BrickStoneUtils.CRAFTED_SENSOR_98;
                                          if (blockState.isOf(Blocks.AMETHYST_CLUSTER)) {
                                             biomeCraftedValue14 = biomeCraftedValue9 | 1;
                                          }
                                       }
                                       break;
                                    case 7:
                                       info = biomeCategory10 != 8 && biomeCategory10 != 9 ? BrickStoneUtils.FARM_SENSOR_INFO : null;
                                       if (info != null && BrickStoneUtils.isFullyGrown(blockState)) {
                                          biomeCraftedValue14 = biomeCraftedValue9 | 1;
                                       }
                                       break;
                                    case 8:
                                       info = biomeCategory10 == 10 ? null : BrickStoneUtils.FARM_SENSOR_106;
                                       break;
                                    case 9:
                                       info = biomeCategory10 == 5 ? null : BrickStoneUtils.FARM_SENSOR_107;
                                       break;
                                    case 10:
                                       info = biomeCategory10 == 10 ? null : BrickStoneUtils.FARM_SENSOR_INFO_OF;
                                       if (info != null && BrickStoneUtils.isFullyGrown(blockState)) {
                                          biomeCraftedValue14 = biomeCraftedValue9 | 1;
                                       }
                                       break;
                                    case 11:
                                       info = biomeCategory10 != 11 && biomeCategory10 != 12 ? BrickStoneUtils.FARM_SENSOR_110 : null;
                                       if (info != null && BrickStoneUtils.isFullyGrown(blockState)) {
                                          biomeCraftedValue14 = biomeCraftedValue9 | 1;
                                       }
                                       break;
                                    case 12:
                                       info = biomeCategory10 == 3 ? null : BrickStoneUtils.FARM_SENSOR_111;
                                       if (info != null && blockState.contains(Properties.BERRIES) && blockState.get(Properties.BERRIES)) {
                                          biomeCraftedValue14 = biomeCraftedValue9 | 1;
                                       }
                                       break;
                                    case 13:
                                       info = biomeCategory10 == 9 ? null : BrickStoneUtils.CRAFTED_SENSOR_82;
                                       break;
                                    case 14:
                                       info = value == 2 ? null : BrickStoneUtils.CRAFTED_SENSOR_91;
                                       break;
                                    case 15:
                                       info = value == 1 ? null : BrickStoneUtils.CRAFTED_SENSOR_75;
                                       break;
                                    case 16:
                                       info = biomeCategory10 == 15 ? null : BrickStoneUtils.UTILITY_SENSOR_INFO_OF;
                                       break;
                                    case 17:
                                       int biomeCraftedLimit3 = blockState.contains(Properties.HYDRATION) ? blockState.get(Properties.HYDRATION) : 0;
                                       if (biomeCraftedLimit3 > 0) {
                                          info = BrickStoneUtils.FARM_SENSOR_112;
                                          biomeCraftedValue14 = biomeCraftedValue9 | 2;
                                       } else {
                                          info = biomeCategory10 == 24 ? null : BrickStoneUtils.FARM_SENSOR_112;
                                       }
                                       break;
                                    case 18:
                                       blockWorldValue = 137;
                                       info = biomeCategory10 == 11 ? null : BrickStoneUtils.CRAFTED_SENSOR_INFO;
                                       break;
                                    case 19:
                                       info = blockState.contains(Properties.AXIS) && blockState.get(Properties.AXIS) != Axis.Y ? BrickStoneUtils.CRAFTED_SENSOR_72 : null;
                                       break;
                                    case 20:
                                       blockWorldValue = getSpawnerSensorId(world, worldChunk, new BlockPos(x, y, z));
                                       if (blockWorldValue == 138) {
                                          biomeCraftedValue14 = biomeCraftedValue9 | 8;
                                       }
                                       break;
                                    case 21:
                                       if (BrickStoneUtils.isFullyGrown(blockState)) {
                                          biomeCraftedValue14 = biomeCraftedValue9 | 1;
                                       }
                                       break;
                                    case 22:
                                       if (BrickStoneUtils.isLit(blockState)) {
                                          biomeCraftedValue14 = biomeCraftedValue9 | 2;
                                       }
                                       break;
                                    case 23:
                                    case 24:
                                       if (BrickStoneUtils.isLit(blockState)) {
                                          biomeCraftedValue14 = biomeCraftedValue9 | 4;
                                       }
                                       break;
                                    case 25:
                                       biomeCraftedValue14 = biomeCraftedValue9 | 1;
                                 }
                              }

                              if (blockWorldValue != 0) {
                                 flagsHandle.incrementTypeCount(blockWorldValue);
                                 flagsHandle.incrementGroupCount(biomeCategory10);
                                 flagsHandle.recordWorldLoadedValue(y);
                                 if (isStructureSensorId(blockWorldValue) && arrayList2.size() < 96) {
                                    arrayList2.add(new SensorBlock(blockWorldValue, x, y, z));
                                 }
                              }

                              if (info == null) {
                                 if (!air) {
                                    if (biomeCraftedCounter10 < 96) {
                                       biomeCraftedCounter10++;
                                    }

                                    if (biomeCraftedFlag4) {
                                       biomeCraftedCounter4++;
                                    }
                                 }
                              } else if (info.excavationCraftedType == null) {
                                 flagsHandle.incrementTypeCount(info.sensorId);
                                 flagsHandle.incrementGroupCount(biomeCategory10);
                                 flagsHandle.recordWorldLoadedValue(y);
                                 if (info.sensorId == 145) {
                                    biomeCraftedCounter3++;
                                 } else if (biomeCraftedFlag4) {
                                    biomeCraftedCounter4++;
                                 }

                                 if (isStructureSensorId(info.sensorId) && arrayList2.size() < 96) {
                                    arrayList2.add(new SensorBlock(info.sensorId, x, y, z));
                                 }
                              } else {
                                 constChunkValue++;
                                 int offsetState = countSolidNeighbors(worldChunk, world, chunkSection, p, m, q, x, y, z, bottomY, offsetLightLimit);
                                 int biomeCraftedValue16;
                                 if (offsetState < 0) {
                                    biomeCraftedValue16 = biomeCraftedValue14 | 256;
                                 } else {
                                    biomeCraftedValue16 = biomeCraftedValue14 | 32;
                                    biomeCraftedCounter5++;
                                    if (offsetState >= 3) {
                                       biomeCraftedValue16 |= 64;
                                       constChunkLimit++;
                                       biomeCraftedValue4 |= 1L << (info.sensorId & 63);
                                       biomeCraftedValue5 |= 1L << (info.sensorId >>> 6 & 63);
                                    }
                                 }

                                 flagsHandle.incrementTypeCount(info.sensorId);
                                 flagsHandle.incrementGroupCount(biomeCategory10);
                                 flagsHandle.recordWorldLoadedValue(y);
                                 if (info.sensorId == 34 && arrayList2.size() < 96) {
                                    arrayList2.add(new SensorBlock(34, x, y, z));
                                 }

                                 arrayList.add(buildCraftedExcavation(x, y, z, blockState, info, biomeCraftedValue16, biomeCategory10));
                              }
                           }
                        }
                     }
                  }
               }

               biomeCraftedTotal += biomeCraftedCounter10;
               if (sectionMask != null) {
                  topLayerBits2 = sectionMask.getTopLayerBits();
                  biomeCraftedValue7 = k;
               }

               if (sectionMask != null && sectionMask.hasBitsetBits()) {
                  List list = ExcavationScanner.findExcavations(sectionMask, startX, biomeCraftedValue8, startZ, biomeCraftedValues);
                  if (!list.isEmpty()) {
                     RegistryEntry biome3 = chunkSection.getBiome(1, 1, 1);
                     int biomeCategory17 = forestDark.getBiomeCategory(biome3, value);

                     for (CraftedExcavation craftedExcavation : (Iterable<com.corz.client.utils.CraftedExcavation>) (list)) {
                        flagsHandle.incrementTypeCount(craftedExcavation.craftedTypeId);
                        flagsHandle.incrementGroupCount(biomeCategory17);
                        flagsHandle.recordWorldLoadedValue(craftedExcavation.posY);
                        arrayList.add(
                           CraftedExcavation.createTyped(
                              craftedExcavation.posX, craftedExcavation.posY, craftedExcavation.posZ, craftedExcavation.excavationCraftedType, craftedExcavation.craftedWeight, craftedExcavation.craftedTypeId, craftedExcavation.flagMask | biomeCraftedValue9, biomeCategory17
                           )
                        );
                     }
                  }
               }
            } else {
               biomeCraftedCounter2++;
            }
         }
      }

      int biomeCraftedCounter11 = 0;
      Object emptyList = Collections.emptyList();
      Object emptyList2 = Collections.emptyList();
      if (map != null && !map.isEmpty()) {
         emptyList = new ArrayList();
         emptyList2 = new ArrayList();

         for (StateBlockFlag.Entry entry : map.values()) {
            if (entry.value2 >= biomeCraftedValue && entry.value2 <= biomeCraftedLimit && entry.value2 >= bottomY && entry.value2 <= offsetLightLimit) {
               BlockState blockState2 = worldChunk.getBlockState(new BlockPos(entry.value, entry.value2, entry.value3));
               int liquidAir = BrickStoneUtils.classifyBlockState(blockState2);
               if (liquidAir == 3) {
                  ((java.util.List) emptyList2).add(entry);
               } else if (liquidAir != 2) {
                  ((java.util.List) emptyList).add(entry);
               } else if (entry.blockState instanceof BlockState blockState3) {
                  BrickStoneUtils.Info blockInfo = BrickStoneUtils.getBlockInfo(blockState3.getBlock());
                  if (blockInfo.excavationCraftedType != null) {
                     int index = entry.value2 - bottomY >> 4;
                     int biomeCategory18 = 0;
                     if (index >= 0 && index < sectionArray.length && sectionArray[index] != null) {
                        biomeCategory18 = forestDark.getBiomeCategory(
                           sectionArray[index].getBiome((entry.value & 15) >> 2, entry.value2 - bottomY - (index << 4) >> 2, (entry.value3 & 15) >> 2), value
                        );
                     }

                     flagsHandle.incrementTypeCount(blockInfo.sensorId);
                     flagsHandle.incrementGroupCount(biomeCategory18);
                     flagsHandle.recordWorldLoadedValue(entry.value2);
                     arrayList.add(buildCraftedExcavation(entry.value, entry.value2, entry.value3, blockState3, blockInfo, 16, biomeCategory18));
                     biomeCraftedCounter11++;
                     constChunkValue++;
                  }
               }
            }
         }
      }

      int biomeCraftedValue19 = Long.bitCount(biomeCraftedValue4) + Math.max(0, Long.bitCount(biomeCraftedValue5) - 1);
      boolean passesDetectionThresholds = ConstChunkUtils.passesDetectionThresholds(constChunkLimit, biomeCraftedValue19, constChunkValue, biomeCraftedCounter3, biomeCraftedCounter4);
      long biomeCraftedValue20 = (System.nanoTime() - nanoTime) / 1000L;
      return new BlockStateUtilsWorld.ChunkScan(
         arrayList,
         biomeCraftedTotal,
         flagsHandle,
         arrayList2,
         biomeCraftedCounter,
         biomeCraftedCounter2,
         biomeCraftedValue20,
         biomeCraftedCounter3,
         biomeCraftedCounter4,
         biomeCraftedCounter5,
         constChunkLimit,
         constChunkValue,
         biomeCraftedCounter11,
         passesDetectionThresholds,
         (List<StateBlockFlag.Entry>)emptyList,
         (List<StateBlockFlag.Entry>)emptyList2
      );
   }

   private static boolean isIsolatedLightSource(
      World world, WorldChunk worldChunk, ChunkSection chunkSection, int offsetLightValue, int m, int q, int classValue, int y, int offsetLightValue6, int bottomY, int offsetLightLimit
   ) {
      Mutable mutable = new Mutable();
      int lightLevel = world.getLightLevel(LightType.BLOCK, mutable.set(classValue, y, offsetLightValue6));
      if (lightLevel < 12) {
         return false;
      } else {
         boolean offsetLight = false;

         for (Direction direction : ALL_DIRECTIONS) {
            int offsetLightLimit2 = offsetLightValue + direction.getOffsetX();
            int offsetLightLimit3 = m + direction.getOffsetY();
            int offsetLightLimit4 = q + direction.getOffsetZ();
            int offsetLightValue8 = classValue + direction.getOffsetX();
            int offsetLightLimit5 = y + direction.getOffsetY();
            int offsetLightValue9 = offsetLightValue6 + direction.getOffsetZ();
            if (offsetLightLimit5 >= bottomY && offsetLightLimit5 <= offsetLightLimit) {
               BlockState blockState;
               if (offsetLightLimit2 >= 0 && offsetLightLimit2 < 16 && offsetLightLimit3 >= 0 && offsetLightLimit3 < 16 && offsetLightLimit4 >= 0 && offsetLightLimit4 < 16) {
                  blockState = chunkSection.getBlockState(offsetLightLimit2, offsetLightLimit3, offsetLightLimit4);
               } else if (offsetLightLimit2 >= 0 && offsetLightLimit2 < 16 && offsetLightLimit4 >= 0 && offsetLightLimit4 < 16) {
                  blockState = worldChunk.getBlockState(mutable.set(offsetLightValue8, offsetLightLimit5, offsetLightValue9));
               } else {
                  blockState = world.getBlockState(mutable.set(offsetLightValue8, offsetLightLimit5, offsetLightValue9));
               }

               if (blockState.getLuminance() > 0) {
                  return false;
               }

               if (blockState.isAir() || blockState.isLiquid()) {
                  offsetLight = true;
               }

               if (world.getLightLevel(LightType.BLOCK, mutable.set(offsetLightValue8, offsetLightLimit5, offsetLightValue9)) > lightLevel) {
                  return false;
               }
            }
         }

         return offsetLight;
      }
   }

   public static BlockStateUtilsWorld.ChunkScan getClassForWorldChunkWorldBiomeCategoryMapper(WorldChunk worldChunk, World world, BiomeCategoryMapper forestDark, int classValue, int biomeCraftedLimit) {
      return getBiomeCrafted(worldChunk, world, forestDark, classValue, biomeCraftedLimit, null, true, 0L, 0L);
   }

   public static final class ChunkScan {
      public final int scannedCount;
      public final List<CraftedExcavation> craftedExcavations;
      public final int skippedSectionCount;
      public final ExcavationTypeStats flagsHandle;
      public final boolean looksLikeBase;
      public final int lateCraftedCount;
      public final List<StateBlockFlag.Entry> craftedStateEntries;
      public final int scannedSectionCount;
      public final int candidateBlockCount;
      public final int craftedBlockCount;
      public final int sensorBlockCount;
      public final List<SensorBlock> sensorBlocks;
      public final int flaggedBlockCount;
      public final int storageBlockCount;
      public final List<StateBlockFlag.Entry> otherStateEntries;
      public final long scanTimeNanos;

      ChunkScan(
         List<CraftedExcavation> items,
         int value,
         ExcavationTypeStats max,
         List<SensorBlock> items2,
         int value2,
         int value3,
         long time,
         int value4,
         int value5,
         int value6,
         int value7,
         int value8,
         int value9,
         boolean flag,
         List<StateBlockFlag.Entry> items3,
         List<StateBlockFlag.Entry> items4
      ) {
         this.craftedExcavations = items;
         this.scannedCount = value;
         this.flagsHandle = max;
         this.sensorBlocks = items2;
         this.scannedSectionCount = value2;
         this.skippedSectionCount = value3;
         this.scanTimeNanos = time;
         this.sensorBlockCount = value4;
         this.storageBlockCount = value5;
         this.candidateBlockCount = value6;
         this.flaggedBlockCount = value7;
         this.craftedBlockCount = value8;
         this.lateCraftedCount = value9;
         this.looksLikeBase = flag;
         this.otherStateEntries = items3;
         this.craftedStateEntries = items4;
      }
   }
}
