package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.ChunkScanScheduler;

public final class AnchorScanner {
   private final ChunkScanScheduler hashChunk = new ChunkScanScheduler();
   private final Long2ByteOpenHashMap anchorCharges = new Long2ByteOpenHashMap();
   private final ConcurrentLinkedQueue<Long> pendingBlockUpdates = new ConcurrentLinkedQueue<>();
   private volatile long lastScanTick;
   private final ConcurrentLinkedQueue<Long> pendingChunks = new ConcurrentLinkedQueue<>();
   private int scanRadiusChunks;
   private final AtomicInteger pendingBlockUpdateCount = new AtomicInteger();
   private static final Predicate<BlockState> IS_RESPAWN_ANCHOR = state -> state.isOf(Blocks.RESPAWN_ANCHOR);

   public void onPacket(Packet<?> packet, long time) {
      if (packet instanceof BlockUpdateS2CPacket blockUpdateS2CPacket) {
         BlockState blockState = blockUpdateS2CPacket.getState();
         if (blockState != null && (blockState.isOf(Blocks.RESPAWN_ANCHOR) || blockState.isAir() || !blockState.getFluidState().isEmpty())) {
            this.queueBlockUpdate(blockUpdateS2CPacket.getPos().asLong());
         }
      } else if (packet instanceof ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket) {
         chunkDeltaUpdateS2CPacket.visitUpdates((pos, state) -> {
            if (state != null && (state.isOf(Blocks.RESPAWN_ANCHOR) || state.isAir())) {
               this.queueBlockUpdate(pos.asLong());
            }
         });
      } else if (packet instanceof ChunkDataS2CPacket chunkDataS2CPacket) {
         this.lastScanTick = time;
         if (this.pendingChunks.size() < 4096) {
            this.pendingChunks.offer(ChunkPos.toLong(chunkDataS2CPacket.getChunkX(), chunkDataS2CPacket.getChunkZ()));
         }
      }
   }

   public void reset() {
      this.hashChunk.clearScannedChunks();
      this.anchorCharges.clear();

      while (this.pendingBlockUpdates.poll() != null) {
         this.pendingBlockUpdateCount.decrementAndGet();
      }

      this.pendingChunks.clear();
   }

   public void removeAnchorAt(BlockPos pos2) {
      if (pos2 != null) {
         this.anchorCharges.remove(pos2.asLong());
      }
   }

   public double getScannedChunkRatio(ClientWorld world, int chunkValue, int chunkValue2) {
      if (world == null) {
         return 0.0;
      } else {
         int chunkCounter = 0;
         int chunkCounter2 = 0;

         for (int i = -this.scanRadiusChunks; i <= this.scanRadiusChunks; i++) {
            for (int j = -this.scanRadiusChunks; j <= this.scanRadiusChunks; j++) {
               int chunkValue3 = chunkValue + i;
               int chunkValue4 = chunkValue2 + j;
               if (world.getChunkManager().getWorldChunk(chunkValue3, chunkValue4) != null) {
                  chunkCounter++;
                  if (this.hashChunk.isChunkScanned(ChunkPos.toLong(chunkValue3, chunkValue4))) {
                     chunkCounter2++;
                  }
               }
            }
         }

         return chunkCounter == 0 ? 0.0 : (double)chunkCounter2 / chunkCounter;
      }
   }

   private void scanChunkForAnchors(WorldChunk worldChunk, long packedUnpackValue) {
      int packedX = ChunkPos.getPackedX(packedUnpackValue);
      int packedZ = ChunkPos.getPackedZ(packedUnpackValue);
      LongIterator iterator = this.anchorCharges.keySet().iterator();

      while (iterator.hasNext()) {
         long randomLong = iterator.nextLong();
         if (BlockPos.unpackLongX(randomLong) >> 4 == packedX && BlockPos.unpackLongZ(randomLong) >> 4 == packedZ) {
            iterator.remove();
         }
      }

      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      int bottomY = worldChunk.getBottomY();
      int startX = worldChunk.getPos().getStartX();
      int startZ = worldChunk.getPos().getStartZ();

      for (int i = 0; i < sectionArray.length; i++) {
         ChunkSection chunkSection = sectionArray[i];
         if (chunkSection != null && !chunkSection.isEmpty() && chunkSection.hasAny(IS_RESPAWN_ANCHOR)) {
            int packedUnpackValue2 = bottomY + (i << 4);

            for (int j = 0; j < 16; j++) {
               for (int k = 0; k < 16; k++) {
                  for (int m = 0; m < 16; m++) {
                     BlockState blockState = chunkSection.getBlockState(m, j, k);
                     if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                        this.anchorCharges.put(BlockPos.asLong(startX + m, packedUnpackValue2 + j, startZ + k), (byte)getCharges(blockState));
                     }
                  }
               }
            }
         }
      }
   }

   public Long2ByteMap getAnchorCharges() {
      return this.anchorCharges;
   }

   public static int getCharges(BlockState state) {
      return state != null && state.contains(RespawnAnchorBlock.CHARGES) ? state.get(RespawnAnchorBlock.CHARGES) : 0;
   }

   private void queueBlockUpdate(long atomicIntegerValue) {
      if (this.pendingBlockUpdateCount.get() < 4096) {
         this.pendingBlockUpdates.offer(atomicIntegerValue);
         this.pendingBlockUpdateCount.incrementAndGet();
      }
   }

   public AnchorScanner() {
      this.lastScanTick = -2305843009213693952L;
      this.scanRadiusChunks = 6;
   }

   public int getScanRadiusChunks() {
      return this.scanRadiusChunks;
   }

   public int getAnchorCount() {
      return this.anchorCharges.size();
   }

   public long getLastChunkPacketTick() {
      return this.lastScanTick;
   }

   public void updateScan(ClientWorld world, int chunkClampedValue, int chunkClampedValue2) {
      if (world != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         this.scanRadiusChunks = Math.max(2, Math.min(instance.options.getClampedViewDistance(), 12));
         this.hashChunk.removeFarChunks(chunkClampedValue, chunkClampedValue2, this.scanRadiusChunks + 2);
         this.hashChunk.scanChunks(world, chunkClampedValue, chunkClampedValue2, this.scanRadiusChunks, 0L, ChunkScanScheduler.getFpsScaledBudgetNanos(600), this::scanChunkForAnchors);
      }
   }

   public void processPendingUpdates(ClientWorld world) {
      if (world != null) {
         Long long2;
         while ((long2 = this.pendingChunks.poll()) != null) {
            this.hashChunk.forgetChunk(long2);
         }

         int unpackByteCounter = 0;

         Long long3;
         for (Mutable mutable = new Mutable(); unpackByteCounter < 512 && (long3 = this.pendingBlockUpdates.poll()) != null; unpackByteCounter++) {
            this.pendingBlockUpdateCount.decrementAndGet();
            long unpackByteValue = long3;
            mutable.set(BlockPos.unpackLongX(unpackByteValue), BlockPos.unpackLongY(unpackByteValue), BlockPos.unpackLongZ(unpackByteValue));
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
               this.anchorCharges.put(unpackByteValue, (byte)getCharges(blockState));
            } else {
               this.anchorCharges.remove(unpackByteValue);
            }
         }
      }
   }
}
