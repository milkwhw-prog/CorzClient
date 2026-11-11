package com.corz.client.utils;

import com.corz.client.mixins.AccessorMobSpawnerLogic;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.MinecraftSpiderUtils;
import com.corz.client.utils.SpawnerTimeUtils;

public final class ChunkBlockUtils {
   private static final Predicate<BlockState> PREDICATE = state -> state.isOf(Blocks.SPAWNER);

   public static String formatLogicSpawn(BlockEntity blockEntity) {
      if (blockEntity instanceof MobSpawnerBlockEntity mobSpawnerBlockEntity) {
         try {
            MobSpawnerEntry corzgetSpawnEntry = ((AccessorMobSpawnerLogic)mobSpawnerBlockEntity.getLogic()).corz$getSpawnEntry();
            if (corzgetSpawnEntry == null) {
               return null;
            } else {
               NbtCompound entity = corzgetSpawnEntry.entity();
               if (entity == null) {
                  return null;
               } else {
                  String logicSpawnText = entity.getString("id", "");
                  return MinecraftSpiderUtils.getLowerCase(logicSpawnText);
               }
            }
         } catch (Throwable e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private ChunkBlockUtils() {
   }

   public static ChunkBlockUtils.Result getBlockStart(WorldChunk worldChunk, int blockStartBound, int blockStartLimit, ChunkBlockUtils.Result blockStart) {
      blockStart.update();
      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      int bottomY = worldChunk.getBottomY();
      int startX = worldChunk.getPos().getStartX();
      int startZ = worldChunk.getPos().getStartZ();

      for (int i = 0; i < sectionArray.length; i++) {
         int rocketsBuyBound = bottomY + (i << 4);
         if (rocketsBuyBound <= blockStartLimit && rocketsBuyBound + 15 >= blockStartBound) {
            ChunkSection chunkSection = sectionArray[i];
            if (chunkSection != null && !chunkSection.isEmpty() && chunkSection.hasAny(PREDICATE)) {
               blockStart.iIiCount++;
               int blockStartValue = Math.max(blockStartBound, rocketsBuyBound) - rocketsBuyBound;
               int blockStartLimit2 = Math.min(blockStartLimit, rocketsBuyBound + 15) - rocketsBuyBound;

               for (int j = blockStartValue; j <= blockStartLimit2; j++) {
                  for (int k = 0; k < 16; k++) {
                     for (int m = 0; m < 16; m++) {
                        if (chunkSection.getBlockState(m, j, k).isOf(Blocks.SPAWNER)) {
                           int x = startX + m;
                           int y = rocketsBuyBound + j;
                           int z = startZ + k;
                           BlockPos blockPos = new BlockPos(x, y, z);
                           blockStart.blockStartStringMap.put(SpawnerTimeUtils.getTime(x, y, z), formatLogicSpawn(worldChunk.getBlockEntity(blockPos)));
                        }
                     }
                  }
               }
            } else {
               blockStart.blockStartCount++;
            }
         }
      }

      for (Entry entry : worldChunk.getBlockEntities().entrySet()) {
         if (entry.getValue() instanceof MobSpawnerBlockEntity mobSpawnerBlockEntity) {
            blockStart.matchCount++;
            BlockPos blockPos2 = (BlockPos)entry.getKey();
            if (blockPos2.getY() >= blockStartBound && blockPos2.getY() <= blockStartLimit && worldChunk.getBlockState(blockPos2).isOf(Blocks.SPAWNER)) {
               long obsidianIdleValue = SpawnerTimeUtils.getTime(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
               String botCrystalValue = formatLogicSpawn(mobSpawnerBlockEntity);
               if (!blockStart.blockStartStringMap.containsKey(obsidianIdleValue) || botCrystalValue != null && blockStart.blockStartStringMap.get(obsidianIdleValue) == null) {
                  blockStart.blockStartStringMap.put(obsidianIdleValue, botCrystalValue);
               }
            }
         }
      }

      return blockStart;
   }

   public static final class Result {
      public int matchCount;
      public int blockStartCount;
      public final Map<Long, String> blockStartStringMap = new HashMap<>(4);
      public int iIiCount;

      public void update() {
         this.blockStartStringMap.clear();
         this.iIiCount = 0;
         this.blockStartCount = 0;
         this.matchCount = 0;
      }
   }
}
