package com.corz.client.world;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class PosBlockUtilsWorld {
   private static final Direction[] directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

   private static List<BlockPos> getDownOffset(World world, BlockPos pos, int downOffsetLimit) {
      ArrayList arrayList = new ArrayList(8);

      for (Direction direction : directions) {
         BlockPos offset = pos.offset(direction);
         if (StateBlockUtilsCenter.canDown(world, offset)) {
            arrayList.add(offset);
         } else {
            BlockPos up = offset.up();
            if (StateBlockUtilsCenter.canCollisionShape(world, pos.up().up()) && StateBlockUtilsCenter.canDown(world, up)) {
               arrayList.add(up);
            } else if (StateBlockUtilsCenter.canCollisionShape(world, offset) && StateBlockUtilsCenter.canCollisionShape(world, offset.up())) {
               BlockPos blockPos = offset;

               for (int i = 0; i < downOffsetLimit; i++) {
                  BlockPos down = blockPos.down();
                  if (StateBlockUtilsCenter.canSquareState(world, down)) {
                     break;
                  }

                  if (!StateBlockUtilsCenter.canCollisionShape(world, down)) {
                     blockPos = null;
                     break;
                  }

                  blockPos = down;
               }

               if (blockPos != null && !blockPos.equals(offset) && StateBlockUtilsCenter.canDown(world, blockPos)) {
                  arrayList.add(blockPos);
               }
            }
         }
      }

      return arrayList;
   }

   public static List<BlockPos> getItems(World world, BlockPos pos, BlockPos pos2, int posBlockValue) {
      if (pos.equals(pos2)) {
         return List.of();
      } else if (!StateBlockUtilsCenter.canDown(world, pos2)) {
         return null;
      } else {
         HashMap hashMap = new HashMap();
         ArrayDeque arrayDeque = new ArrayDeque();
         arrayDeque.add(pos);
         hashMap.put(pos, null);
         int posBlockCounter = 0;

         while (!arrayDeque.isEmpty() && posBlockCounter++ < 3000) {
            BlockPos blockPos = (BlockPos)arrayDeque.poll();
            if (blockPos.equals(pos2)) {
               return getItems(hashMap, blockPos);
            }

            for (BlockPos formatGroundAir : getDownOffset(world, blockPos, posBlockValue)) {
               if (!hashMap.containsKey(formatGroundAir)) {
                  hashMap.put(formatGroundAir, blockPos);
                  arrayDeque.add(formatGroundAir);
               }
            }
         }

         return null;
      }
   }

   private static List<BlockPos> getItems(Map<BlockPos, BlockPos> map, BlockPos pos) {
      ArrayList arrayList = new ArrayList();
      BlockPos botCrystalText = pos;

      while (botCrystalText != null && map.get(botCrystalText) != null) {
         arrayList.add(botCrystalText);
         botCrystalText = (BlockPos)map.get(botCrystalText);
      }

      Collections.reverse(arrayList);
      return arrayList;
   }

   private PosBlockUtilsWorld() {
   }

   public static final class Search {
      private final Long2LongOpenHashMap long2LongOpenHashMap;
      private final BlockPos blockPos;
      private final Long2ByteOpenHashMap long2ByteOpenHashMap;
      private final boolean fifoqueueDequeueActive;
      private final long longValue;
      private final World world;
      private final int maxSearchSteps;
      private int fifoqueueDequeueCount;
      private final Mutable mutable;
      private final LongOpenHashSet longOpenHashSet;
      private final LongArrayFIFOQueue longArrayFIFOQueue = new LongArrayFIFOQueue();

      public Search(World world, BlockPos blockPos, int value) {
         this.long2LongOpenHashMap = new Long2LongOpenHashMap();
         this.longOpenHashSet = new LongOpenHashSet();
         this.long2ByteOpenHashMap = new Long2ByteOpenHashMap();
         this.mutable = new Mutable();
         this.world = world;
         this.blockPos = blockPos;
         this.maxSearchSteps = value;
         this.fifoqueueDequeueActive = value <= 16 && canFlag(blockPos);
         this.longValue = blockPos.asLong();
         this.longArrayFIFOQueue.enqueue(this.longValue);
         this.long2LongOpenHashMap.put(this.longValue, this.longValue);
      }

      private List<BlockPos> getHashOpen(long hashOpenValue) {
         ArrayList arrayList = new ArrayList();
         long hashOpenValue2 = hashOpenValue;

         while (hashOpenValue2 != this.longValue) {
            arrayList.add(BlockPos.fromLong(hashOpenValue2));
            hashOpenValue2 = this.long2LongOpenHashMap.get(hashOpenValue2);
         }

         Collections.reverse(arrayList);
         return arrayList;
      }

      private boolean canByteHash(int byteHashValue, int unpackLongY, int unpackLongZ) {
         long long2 = BlockPos.asLong(byteHashValue, unpackLongY, unpackLongZ);
         byte long2byteopenhashmap = this.long2ByteOpenHashMap.get(long2);
         if ((long2byteopenhashmap & 1) == 0) {
            long2byteopenhashmap = (byte)(long2byteopenhashmap | (StateBlockUtilsCenter.canCollisionShape(this.world, this.mutable.set(byteHashValue, unpackLongY, unpackLongZ)) ? 3 : 1));
            this.long2ByteOpenHashMap.put(long2, long2byteopenhashmap);
         }

         return (long2byteopenhashmap & 2) != 0;
      }

      public List<BlockPos> getFifoqueueDequeue(BlockPos pos) {
         if (!this.fifoqueueDequeueActive || !canFlag(pos)) {
            return PosBlockUtilsWorld.getItems(this.world, this.blockPos, pos, this.maxSearchSteps);
         } else if (this.blockPos.equals(pos)) {
            return List.of();
         } else if (!StateBlockUtilsCenter.canDown(this.world, pos)) {
            return null;
         } else {
            long long2 = pos.asLong();
            if (this.longOpenHashSet.contains(long2)) {
               return this.getHashOpen(long2);
            } else {
               while (!this.longArrayFIFOQueue.isEmpty() && this.fifoqueueDequeueCount++ < 3000) {
                  long dequeueLong = this.longArrayFIFOQueue.dequeueLong();
                  this.longOpenHashSet.add(dequeueLong);
                  this.handleUnpackOffset(dequeueLong);
                  if (dequeueLong == long2) {
                     return this.getHashOpen(long2);
                  }
               }

               return null;
            }
         }
      }

      private boolean canFlag(int posBlockValue, int unpackLongY, int unpackLongZ) {
         return this.canByteHashForInt(posBlockValue, unpackLongY - 1, unpackLongZ) && this.canByteHash(posBlockValue, unpackLongY, unpackLongZ) && this.canByteHash(posBlockValue, unpackLongY + 1, unpackLongZ);
      }

      private void handleUnpackOffset(long unpackOffsetValue) {
         int unpackLongX = BlockPos.unpackLongX(unpackOffsetValue);
         int unpackLongY = BlockPos.unpackLongY(unpackOffsetValue);
         int unpackLongZ = BlockPos.unpackLongZ(unpackOffsetValue);

         for (Direction direction : PosBlockUtilsWorld.directions) {
            int posBlockValue = unpackLongX + direction.getOffsetX();
            int unpackOffsetValue3 = unpackLongZ + direction.getOffsetZ();
            if (this.canFlag(posBlockValue, unpackLongY, unpackOffsetValue3)) {
               this.handleEnqueueHash(BlockPos.asLong(posBlockValue, unpackLongY, unpackOffsetValue3), unpackOffsetValue);
            } else if (this.canByteHash(unpackLongX, unpackLongY + 2, unpackLongZ) && this.canFlag(posBlockValue, unpackLongY + 1, unpackOffsetValue3)) {
               this.handleEnqueueHash(BlockPos.asLong(posBlockValue, unpackLongY + 1, unpackOffsetValue3), unpackOffsetValue);
            } else if (this.canByteHash(posBlockValue, unpackLongY, unpackOffsetValue3) && this.canByteHash(posBlockValue, unpackLongY + 1, unpackOffsetValue3)) {
               int unpackOffsetCounter = unpackLongY;
               boolean unpackOffsetFlag = true;

               for (int i = 0; i < this.maxSearchSteps && !this.canByteHashForInt(posBlockValue, unpackOffsetCounter - 1, unpackOffsetValue3); i++) {
                  if (!this.canByteHash(posBlockValue, unpackOffsetCounter - 1, unpackOffsetValue3)) {
                     unpackOffsetFlag = false;
                     break;
                  }

                  unpackOffsetCounter--;
               }

               if (unpackOffsetFlag && unpackOffsetCounter != unpackLongY && this.canFlag(posBlockValue, unpackOffsetCounter, unpackOffsetValue3)) {
                  this.handleEnqueueHash(BlockPos.asLong(posBlockValue, unpackOffsetCounter, unpackOffsetValue3), unpackOffsetValue);
               }
            }
         }
      }

      private void handleEnqueueHash(long enqueueHashValue, long unpackOffsetValue) {
         if (!this.long2LongOpenHashMap.containsKey(enqueueHashValue)) {
            this.long2LongOpenHashMap.put(enqueueHashValue, unpackOffsetValue);
            this.longArrayFIFOQueue.enqueue(enqueueHashValue);
         }
      }

      private static boolean canFlag(BlockPos pos) {
         return pos.getX() >= -33000000 && pos.getX() <= 33000000 && pos.getZ() >= -33000000 && pos.getZ() <= 33000000 && pos.getY() >= -2024 && pos.getY() <= 2024;
      }

      private boolean canByteHashForInt(int byteHashValue, int byteHashValue2, int unpackLongZ) {
         long long2 = BlockPos.asLong(byteHashValue, byteHashValue2, unpackLongZ);
         byte long2byteopenhashmap = this.long2ByteOpenHashMap.get(long2);
         if ((long2byteopenhashmap & 4) == 0) {
            long2byteopenhashmap = (byte)(long2byteopenhashmap | (StateBlockUtilsCenter.canSquareState(this.world, this.mutable.set(byteHashValue, byteHashValue2, unpackLongZ)) ? 12 : 4));
            this.long2ByteOpenHashMap.put(long2, long2byteopenhashmap);
         }

         return (long2byteopenhashmap & 8) != 0;
      }
   }
}
