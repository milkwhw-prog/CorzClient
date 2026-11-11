package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import com.corz.client.utils.BlockBreakMath;

public final class BlockBreakTracker {
   private final ArrayDeque<BlockBreakTracker.Opened> recentOpened;
   private long lastOpenedTick;
   private final Set<BlockPos> trackedBlocks;
   private final Map<Integer, BlockBreakTracker.Dig> digsByBreakerId = new HashMap<>();

   public List<BlockBreakTracker.Opened> getRecentOpened() {
      return new ArrayList<>(this.recentOpened);
   }

   public long getLastOpenedTick() {
      return this.lastOpenedTick;
   }

   public Set<BlockPos> getTrackedBlocks() {
      return Set.copyOf(this.trackedBlocks);
   }

   public BlockBreakTracker.Dig getDigByBreakerId(int immutablePosValue) {
      return this.digsByBreakerId.get(immutablePosValue);
   }

   public boolean isTracked(BlockPos pos) {
      return pos != null && this.trackedBlocks.contains(pos);
   }

   public BlockBreakTracker.Opened getLastOpened() {
      return this.recentOpened.peekLast();
   }

   public BlockBreakTracker() {
      this.trackedBlocks = new HashSet<>();
      this.recentOpened = new ArrayDeque<>();
      this.lastOpenedTick = Long.MIN_VALUE;
   }

   public BlockBreakTracker.Dig getLatestTrackedDig() {
      BlockBreakTracker.Dig next = null;

      for (BlockBreakTracker.Dig dig : this.digsByBreakerId.values()) {
         if (this.trackedBlocks.contains(dig.pos()) && (next == null || dig.lastTick() > next.lastTick())) {
            next = dig;
         }
      }

      return next;
   }

   public void setTrackedBlocks(Collection<BlockPos> collection) {
      this.trackedBlocks.clear();
      if (collection != null) {
         for (BlockPos blockPos : collection) {
            if (blockPos != null) {
               this.trackedBlocks.add(blockPos.toImmutable());
            }
         }
      }
   }

   public int getActiveDigCount() {
      return this.digsByBreakerId.size();
   }

   public void reset() {
      this.digsByBreakerId.clear();
      this.trackedBlocks.clear();
      this.recentOpened.clear();
      this.lastOpenedTick = Long.MIN_VALUE;
   }

   public static int scaleTicksByTenths(int posImmutableValue, int immutablePosBound) {
      return BlockBreakMath.reduceTicksByTenths(posImmutableValue, immutablePosBound);
   }

   public BlockBreakTracker.Opened getRecentOpenedWithin(long dequeArrayValue, int dequeArrayLimit) {
      BlockBreakTracker.Opened peekLast = this.recentOpened.peekLast();
      return peekLast != null && dequeArrayValue - peekLast.tick() <= dequeArrayLimit ? peekLast : null;
   }

   public boolean tryRecordOpened(BlockPos pos2, boolean dequeArrayFlag, long time) {
      if (pos2 != null && dequeArrayFlag && this.trackedBlocks.contains(pos2)) {
         for (BlockBreakTracker.Opened opened : this.recentOpened) {
            if (opened.pos().equals(pos2)) {
               return false;
            }
         }

         this.recentOpened.addLast(new BlockBreakTracker.Opened(pos2.toImmutable(), time));

         while (this.recentOpened.size() > 8) {
            this.recentOpened.removeFirst();
         }

         this.lastOpenedTick = time;
         Iterator iterator = this.digsByBreakerId.entrySet().iterator();

         while (iterator.hasNext()) {
            if (((BlockBreakTracker.Dig)((Entry)iterator.next()).getValue()).pos().equals(pos2)) {
               iterator.remove();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void updateDigProgress(int immutablePosValue, BlockPos pos2, int immutablePosLimit, long immutablePosValue2) {
      if (pos2 != null) {
         if (immutablePosLimit >= 0 && immutablePosLimit <= 9) {
            BlockBreakTracker.Dig dig = this.digsByBreakerId.get(immutablePosValue);
            long immutablePosValue3 = dig != null && dig.pos().equals(pos2) ? dig.startTick() : immutablePosValue2;
            this.digsByBreakerId.put(immutablePosValue, new BlockBreakTracker.Dig(immutablePosValue, pos2.toImmutable(), immutablePosLimit, immutablePosValue3, immutablePosValue2));
         } else {
            BlockBreakTracker.Dig dig2 = this.digsByBreakerId.get(immutablePosValue);
            if (dig2 != null && dig2.pos().equals(pos2)) {
               this.digsByBreakerId.remove(immutablePosValue);
            }
         }
      }
   }

   public BlockBreakTracker.Dig getDigAt(BlockPos pos2) {
      if (pos2 == null) {
         return null;
      } else {
         for (BlockBreakTracker.Dig next : this.digsByBreakerId.values()) {
            if (next.pos().equals(pos2)) {
               return next;
            }
         }

         return null;
      }
   }

   public void pruneExpired(long dequeArrayValue) {
      this.digsByBreakerId.values().removeIf(tickx -> dequeArrayValue - tickx.lastTick() > 60L);

      while (!this.recentOpened.isEmpty() && dequeArrayValue - this.recentOpened.peekFirst().tick() > 40L) {
         this.recentOpened.removeFirst();
      }
   }

   public record Dig(int breakerId, BlockPos pos, int stage, long startTick, long lastTick) {
   }

   public record Opened(BlockPos pos, long tick) {
   }
}
