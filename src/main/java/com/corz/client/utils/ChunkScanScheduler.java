package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public final class ChunkScanScheduler {
   private int[] spiralOffsets;
   private static long nanosUsedThisTick;
   private int lastRadius;
   private static int lastBudgetTick = Integer.MIN_VALUE;
   private int centerChunkZ;
   private int rescanCursor;
   private int spiralCursor;
   private long overspentNanos;
   private int centerChunkX;
   private final Long2LongOpenHashMap scannedAtMs = new Long2LongOpenHashMap();
   private long lastScanTimeMs;

   public static long getFpsScaledBudgetNanos(int fpsValue) {
      long baseMicrosScaled = fpsValue * 1000L;
      int currentFps = MinecraftClient.getInstance().getCurrentFps();
      if (currentFps > 0 && currentFps < 25) {
         return baseMicrosScaled / 4L;
      } else if (currentFps > 0 && currentFps < 45) {
         return baseMicrosScaled / 2L;
      } else {
         return currentFps >= 100 ? baseMicrosScaled + baseMicrosScaled / 2L : baseMicrosScaled;
      }
   }

   private void buildSpiralOffsets(int min) {
      int hashChunkFactor = min * 2 + 1;
      long[] longs = new long[hashChunkFactor * hashChunkFactor];
      int hashChunkCounter = 0;

      for (int i = -min; i <= min; i++) {
         for (int j = -min; j <= min; j++) {
            long hashChunkValue = Math.abs(i) + Math.abs(j);
            longs[hashChunkCounter++] = hashChunkValue << 32 | (long)(i + min) << 16 | j + min;
         }
      }

      Arrays.sort(longs);
      this.spiralOffsets = new int[hashChunkCounter << 1];

      for (int k = 0; k < hashChunkCounter; k++) {
         this.spiralOffsets[k << 1] = (int)(longs[k] >> 16 & 65535L) - min;
         this.spiralOffsets[(k << 1) + 1] = (int)(longs[k] & 65535L) - min;
      }

      this.lastRadius = min;
      this.spiralCursor = 0;
      this.rescanCursor = 0;
   }

   public void removeFarChunks(int packedIteratorValue, int packedIteratorValue2, int nowMsOffset) {
      LongIterator iterator = this.scannedAtMs.keySet().iterator();

      while (iterator.hasNext()) {
         long randomLong = iterator.nextLong();
         if (Math.abs(ChunkPos.getPackedX(randomLong) - packedIteratorValue) > nowMsOffset || Math.abs(ChunkPos.getPackedZ(randomLong) - packedIteratorValue2) > nowMsOffset) {
            iterator.remove();
         }
      }
   }

   public int scanChunks(ClientWorld clientWorld, int chunkValue, int chunkValue2, int chunkLimit, long time2, long chunkValue3, ChunkScanScheduler.Scanner scanner) {
      if (clientWorld != null && chunkLimit >= 0) {
         this.lastScanTimeMs = time2;
         if (chunkLimit != this.lastRadius) {
            this.buildSpiralOffsets(chunkLimit);
         }

         if (chunkValue != this.centerChunkX || chunkValue2 != this.centerChunkZ) {
            this.centerChunkX = chunkValue;
            this.centerChunkZ = chunkValue2;
            this.spiralCursor = 0;
         }

         if (this.overspentNanos >= chunkValue3) {
            this.overspentNanos -= chunkValue3;
            return 0;
         } else {
            long chunkLimit2 = chunkValue3 - this.overspentNanos;
            this.overspentNanos = 0L;
            int ticks = MinecraftClient.getInstance().inGameHud.getTicks();
            if (ticks != lastBudgetTick) {
               lastBudgetTick = ticks;
               nanosUsedThisTick = 0L;
            }

            long chunkValue4 = 2000000L - nanosUsedThisTick;
            if (chunkValue4 <= 0L) {
               return 0;
            } else {
               boolean chunkFlag = chunkValue4 < chunkLimit2;
               if (chunkFlag) {
                  chunkLimit2 = chunkValue4;
               }

               ClientChunkManager chunkManager = clientWorld.getChunkManager();
               long currentMillis = System.currentTimeMillis();
               long nanoTime = System.nanoTime();
               int chunkLimit3 = this.spiralOffsets.length >> 1;
               int timeChunk = 0;
               boolean chunkFlag2 = false;
               int chunkCounter = 0;

               while (chunkCounter < 192 && chunkCounter < chunkLimit3) {
                  if (this.spiralCursor >= chunkLimit3) {
                     this.spiralCursor = 0;
                  }

                  int index = this.spiralCursor++ << 1;
                  chunkCounter++;
                  int chunkValue5 = chunkValue + this.spiralOffsets[index];
                  int chunkValue6 = chunkValue2 + this.spiralOffsets[index + 1];
                  long long2 = ChunkPos.toLong(chunkValue5, chunkValue6);
                  if (this.scannedAtMs.get(long2) == Long.MIN_VALUE) {
                     WorldChunk worldChunk = chunkManager.getWorldChunk(chunkValue5, chunkValue6);
                     if (worldChunk != null) {
                        this.scannedAtMs.put(long2, currentMillis);
                        scanner.scan(worldChunk, long2);
                        timeChunk++;
                        if (System.nanoTime() - nanoTime >= chunkLimit2) {
                           chunkFlag2 = true;
                           break;
                        }
                     }
                  }
               }

               if (timeChunk == 0 && time2 > 0L) {
                  int chunkCounter2 = 0;

                  while (chunkCounter2 < 192 && chunkCounter2 < chunkLimit3) {
                     if (this.rescanCursor >= chunkLimit3) {
                        this.rescanCursor = 0;
                     }

                     int index2 = this.rescanCursor++ << 1;
                     chunkCounter2++;
                     int chunkValue7 = chunkValue + this.spiralOffsets[index2];
                     int chunkValue8 = chunkValue2 + this.spiralOffsets[index2 + 1];
                     long long3 = ChunkPos.toLong(chunkValue7, chunkValue8);
                     long long2longopenhashmap = this.scannedAtMs.get(long3);
                     if (long2longopenhashmap == Long.MIN_VALUE || currentMillis - long2longopenhashmap >= time2) {
                        WorldChunk worldChunk2 = chunkManager.getWorldChunk(chunkValue7, chunkValue8);
                        if (worldChunk2 != null) {
                           this.scannedAtMs.put(long3, currentMillis);
                           scanner.scan(worldChunk2, long3);
                           timeChunk++;
                           if (System.nanoTime() - nanoTime >= chunkLimit2) {
                              chunkFlag2 = true;
                              break;
                           }
                        }
                     }
                  }
               }

               long chunkLimit4 = System.nanoTime() - nanoTime;
               nanosUsedThisTick += chunkLimit4;
               if (chunkFlag2 && !chunkFlag && chunkLimit4 > chunkLimit2) {
                  this.overspentNanos = chunkLimit4 - chunkLimit2;
               }

               return timeChunk;
            }
         }
      } else {
         return 0;
      }
   }

   public int getScannedChunkCount() {
      return this.scannedAtMs.size();
   }

   public boolean isChunkScanned(long containsValue) {
      return this.scannedAtMs.containsKey(containsValue);
   }

   public void markChunkScanned(long hashValue) {
      this.scannedAtMs.put(hashValue, System.currentTimeMillis());
   }

   public void forgetChunk(long hashOpenValue) {
      if (this.scannedAtMs.remove(hashOpenValue) != Long.MIN_VALUE) {
         this.spiralCursor = 0;
      }
   }

   public ChunkScanScheduler() {
      this.spiralOffsets = new int[0];
      this.lastRadius = -1;
      this.centerChunkX = Integer.MIN_VALUE;
      this.centerChunkZ = Integer.MIN_VALUE;
      this.scannedAtMs.defaultReturnValue(Long.MIN_VALUE);
   }

   public void shortenRescanDelay(int hashPutValue, int hashPutValue2, long hashPutValue3) {
      long long2 = ChunkPos.toLong(hashPutValue, hashPutValue2);
      long long2longopenhashmap = this.scannedAtMs.get(long2);
      if (long2longopenhashmap != Long.MIN_VALUE) {
         long hashPutValue4 = System.currentTimeMillis() + hashPutValue3 - this.lastScanTimeMs;
         if (hashPutValue4 < long2longopenhashmap) {
            this.scannedAtMs.put(long2, hashPutValue4);
         }
      }
   }

   public void clearScannedChunks() {
      this.scannedAtMs.clear();
      this.spiralCursor = 0;
      this.rescanCursor = 0;
      this.overspentNanos = 0L;
   }

   public void forgetChunkAt(int hashChunkValue, int hashChunkValue2) {
      this.forgetChunk(ChunkPos.toLong(hashChunkValue, hashChunkValue2));
   }

   @FunctionalInterface
   public interface Scanner {
      void scan(WorldChunk worldChunk, long scannerValue);
   }
}
