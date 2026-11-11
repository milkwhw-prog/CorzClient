package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public final class ShipPather {
   private static final int[] shipPatherTable = new int[]{0, 1, 0, -1};
   private static final byte BIT_FLAG_ONE = 1;
   private static final byte BIT_FLAG_FOUR = 4;
   private static final byte BIT_FLAG_EIGHT = 8;
   private static final int[] table = new int[]{-1, 0, 1, 0};
   private static final byte BIT_FLAG_TWO = 2;
   private static final ShipPather.Move[] MOVES = ShipPather.Move.values();

   public static ShipPather.Result findPath(ShipPather.Grid grid, BlockPos pos, Predicate<BlockPos> predicate, BlockPos pos2, BlockPos pos3, int shipPatherValue) {
      return getResult(grid, pos, predicate, null, pos2, pos3, shipPatherValue);
   }

   public static boolean isWalkable(ShipPather.Grid grid, int shipPatherValue, int ascendLimit, int shipPatherValue3) {
      return grid.isSolidBlock(shipPatherValue, ascendLimit - 1, shipPatherValue3) && !grid.isSolidBlock(shipPatherValue, ascendLimit, shipPatherValue3) && !grid.isSolidBlock(shipPatherValue, ascendLimit + 1, shipPatherValue3);
   }

   public static ShipPather.Result getResult(
      ShipPather.Grid grid, BlockPos pos, Predicate<BlockPos> predicate, BlockPos pos2, BlockPos pos3, BlockPos pos4, int shipPatherValue
   ) {
      return new ShipPather.Search(grid, predicate, pos2, pos3, pos4).search(pos, shipPatherValue);
   }

   private ShipPather() {
   }

   private static long hashPosition(long shipPatherValue) {
      return 1L << (int)(shipPatherValue * -7046029254386353131L >>> 58);
   }

   public interface Grid {
      float getBlockBreakCost(int finderElytraValue, int ascendLimit, int finderElytraValue3);

      boolean isSolidBlock(int finderElytraValue, int ascendLimit, int finderElytraValue3);

      boolean isBreakableBlock(int finderElytraValue, int ascendLimit, int finderElytraValue3);
   }

   public static enum Move {
      WALK,
      ASCEND,
      DESCEND,
      DIG_DOWN;
   }

   public record Result(List<ShipPather.Step> steps, boolean reached, int expanded) {
   }

   private static final class Search {
      private long[] copyOf;
      private final int targetZ;
      private int hashCount;
      private double[] copyOfPredicate;
      private double[] predicateTable;
      private final ShipPather.Grid gridByteHash;
      private int[] copyOfHash;
      private byte[] hashTable;
      private final Long2FloatOpenHashMap long2FloatOpenHashMap;
      private int[] table;
      private long[][] hashLongs;
      private final int boundsMinX;
      private final Long2ByteOpenHashMap long2ByteOpenHashMap;
      private final Long2IntOpenHashMap long2IntOpenHashMap = new Long2IntOpenHashMap();
      private final int boundsMaxX;
      private final int targetY;
      private boolean[] hashFlags;
      private int insertionOrderCounter;
      private int[] parentIndices;
      private int nodeCount;
      private final long[] neighborKeyBuffer;
      private final boolean hasTarget;
      private final int boundsMaxZ;
      private final int ascendValue;
      private final int boundsMinZ;
      private double[] nodeCosts;
      private final Predicate<BlockPos> predicate;
      private double[] heapPriorities;
      private int[] heapNodeIndices;
      private int[] nodeZs;
      private final int targetX;
      private int[] nodeYs;
      private int predicateCount;
      private final int descendValue;

      Search(ShipPather.Grid grid, Predicate<BlockPos> predicate, BlockPos pos, BlockPos pos2, BlockPos pos3) {
         this.long2ByteOpenHashMap = new Long2ByteOpenHashMap(4096);
         this.long2FloatOpenHashMap = new Long2FloatOpenHashMap();
         this.copyOfHash = new int[1024];
         this.nodeYs = new int[1024];
         this.nodeZs = new int[1024];
         this.nodeCosts = new double[1024];
         this.copyOfPredicate = new double[1024];
         this.parentIndices = new int[1024];
         this.copyOf = new long[1024];
         this.hashTable = new byte[1024];
         this.hashLongs = new long[1024][];
         this.hashFlags = new boolean[1024];
         this.heapNodeIndices = new int[1024];
         this.table = new int[1024];
         this.predicateTable = new double[1024];
         this.heapPriorities = new double[1024];
         this.neighborKeyBuffer = new long[3];
         this.gridByteHash = grid;
         this.predicate = predicate;
         this.hasTarget = pos != null;
         this.targetX = this.hasTarget ? pos.getX() : 0;
         this.targetY = this.hasTarget ? pos.getY() : 0;
         this.targetZ = this.hasTarget ? pos.getZ() : 0;
         this.boundsMinX = Math.min(pos2.getX(), pos3.getX());
         this.ascendValue = Math.min(pos2.getY(), pos3.getY());
         this.boundsMinZ = Math.min(pos2.getZ(), pos3.getZ());
         this.boundsMaxX = Math.max(pos2.getX(), pos3.getX());
         this.descendValue = Math.max(pos2.getY(), pos3.getY());
         this.boundsMaxZ = Math.max(pos2.getZ(), pos3.getZ());
         this.long2IntOpenHashMap.defaultReturnValue(-1);
         this.long2FloatOpenHashMap.defaultReturnValue(-1.0F);
      }

      private void swapHeapEntries(int index, int shipPatherLimit) {
         int shipPatherValue = this.heapNodeIndices[index];
         this.heapNodeIndices[index] = this.heapNodeIndices[shipPatherLimit];
         this.heapNodeIndices[shipPatherLimit] = shipPatherValue;
         int shipPatherValue2 = this.table[index];
         this.table[index] = this.table[shipPatherLimit];
         this.table[shipPatherLimit] = shipPatherValue2;
         double shipPatherValue3 = this.predicateTable[index];
         this.predicateTable[index] = this.predicateTable[shipPatherLimit];
         this.predicateTable[shipPatherLimit] = shipPatherValue3;
         double shipPatherValue4 = this.heapPriorities[index];
         this.heapPriorities[index] = this.heapPriorities[shipPatherLimit];
         this.heapPriorities[shipPatherLimit] = shipPatherValue4;
      }

      private boolean isInBounds(int shipPatherValue, int byteHashValue, int ascendLimit, int shipPatherValue4) {
         long long2 = BlockPos.asLong(byteHashValue, ascendLimit, shipPatherValue4);
         return this.hasVisitedFlag(byteHashValue, ascendLimit, shipPatherValue4, long2) && !this.hasKey(shipPatherValue, long2);
      }

      private boolean hasVisitedFlag(int byteHashValue, int ascendLimit, int byteHashValue3, long long2) {
         byte long2byteopenhashmap = this.long2ByteOpenHashMap.get(long2);
         if ((long2byteopenhashmap & 1) == 0) {
            long2byteopenhashmap = (byte)(long2byteopenhashmap | (this.gridByteHash.isSolidBlock(byteHashValue, ascendLimit, byteHashValue3) ? 3 : 1));
            this.long2ByteOpenHashMap.put(long2, long2byteopenhashmap);
         }

         return (long2byteopenhashmap & 2) != 0;
      }

      private void relaxNeighbor(int index, int hashValue, int ascendLimit, int hashValue3, ShipPather.Move move, double hashValue4) {
         double hashLimit = this.nodeCosts[index] + hashValue4;
         long long2 = BlockPos.asLong(hashValue, ascendLimit, hashValue3);
         int long2intopenhashmap = this.long2IntOpenHashMap.get(long2);
         if (long2intopenhashmap < 0) {
            long2intopenhashmap = this.getOrCreateNode(hashValue, ascendLimit, hashValue3, long2);
         } else if (hashLimit >= this.nodeCosts[long2intopenhashmap] - 1.0E-9) {
            return;
         }

         long hashValue5 = this.copyOf[index];

         for (int i = 0; i < this.hashCount; i++) {
            hashValue5 |= ShipPather.hashPosition(this.neighborKeyBuffer[i]);
         }

         this.nodeCosts[long2intopenhashmap] = hashLimit;
         this.parentIndices[long2intopenhashmap] = index;
         this.hashTable[long2intopenhashmap] = (byte)move.ordinal();
         this.hashLongs[long2intopenhashmap] = this.hashCount == 0 ? null : Arrays.copyOf(this.neighborKeyBuffer, this.hashCount);
         this.copyOf[long2intopenhashmap] = hashValue5;
         this.hashFlags[long2intopenhashmap] = false;
         this.siftUp(long2intopenhashmap);
      }

      private void tryAscend(int ascendValue, int byteHashValue, int walkLimit, int ascendValue4, int hashValue, int ascendValue6) {
         int ascendLimit = walkLimit + 1;
         if (ascendLimit >= this.ascendValue && ascendLimit <= this.descendValue && this.isInBounds(ascendValue, hashValue, walkLimit, ascendValue6)) {
            this.hashCount = 0;
            double times = this.getMoveCost(ascendValue, byteHashValue, walkLimit + 2, ascendValue4);
            if (!(times < 0.0)) {
               double moveCost = this.getMoveCost(ascendValue, hashValue, ascendLimit + 1, ascendValue6);
               if (!(moveCost < 0.0)) {
                  double moveCost3 = this.getMoveCost(ascendValue, hashValue, ascendLimit, ascendValue6);
                  if (!(moveCost3 < 0.0)) {
                     this.relaxNeighbor(ascendValue, hashValue, ascendLimit, ascendValue6, ShipPather.Move.ASCEND, 2.0 + times + moveCost + moveCost3);
                  }
               }
            }
         }
      }

      ShipPather.Result search(BlockPos pos, int predicateValue) {
         int putHash = this.getOrCreateNode(pos.getX(), pos.getY(), pos.getZ(), pos.asLong());
         this.nodeCosts[putHash] = 0.0;
         this.parentIndices[putHash] = -1;
         this.hashTable[putHash] = -1;
         this.siftUp(putHash);
         int predicateCounter = 0;
         int index = putHash;

         while (this.predicateCount > 0) {
            int shipPatherValue = this.heapNodeIndices[0];
            double predicateValue2 = this.predicateTable[0];
            this.growArrays();
            if (!this.hashFlags[shipPatherValue] && predicateValue2 == this.nodeCosts[shipPatherValue] + this.copyOfPredicate[shipPatherValue]) {
               if (this.predicate.test(new BlockPos(this.copyOfHash[shipPatherValue], this.nodeYs[shipPatherValue], this.nodeZs[shipPatherValue]))) {
                  return new ShipPather.Result(this.reconstructPath(shipPatherValue), true, predicateCounter);
               }

               if (predicateCounter >= predicateValue) {
                  break;
               }

               this.hashFlags[shipPatherValue] = true;
               predicateCounter++;
               if (this.copyOfPredicate[shipPatherValue] < this.copyOfPredicate[index] || this.copyOfPredicate[shipPatherValue] == this.copyOfPredicate[index] && this.nodeCosts[shipPatherValue] < this.nodeCosts[index]) {
                  index = shipPatherValue;
               }

               this.siftDown(shipPatherValue);
            }
         }

         return new ShipPather.Result(this.reconstructPath(index), false, predicateCounter);
      }

      private void tryDescend(int descendValue, int ascendLimit, int hashValue, int descendValue4) {
         this.hashCount = 0;
         double times = this.getMoveCost(descendValue, hashValue, ascendLimit + 1, descendValue4);
         if (!(times < 0.0)) {
            double moveCost = this.getMoveCost(descendValue, hashValue, ascendLimit, descendValue4);
            if (!(moveCost < 0.0)) {
               for (int j = 2; j <= 4; j++) {
                  if (this.isInBounds(descendValue, hashValue, ascendLimit - j, descendValue4)) {
                     int descendLimit = ascendLimit - j + 1;
                     if (descendLimit >= this.ascendValue && descendLimit <= this.descendValue) {
                        this.relaxNeighbor(descendValue, hashValue, descendLimit, descendValue4, ShipPather.Move.DESCEND, 1.0 + times + moveCost + 0.5 * (j - 1));
                     }

                     return;
                  }
               }
            }
         }
      }

      private void expandNodeAt(int shipPatherValue, int shipPatherLimit, int walkLimit, int shipPatherLimit2) {
         if (shipPatherLimit >= this.boundsMinX && shipPatherLimit <= this.boundsMaxX && shipPatherLimit2 >= this.boundsMinZ && shipPatherLimit2 <= this.boundsMaxZ) {
            long long2 = BlockPos.asLong(shipPatherLimit, walkLimit - 1, shipPatherLimit2);
            if (this.isInBounds(shipPatherValue, shipPatherLimit, walkLimit - 1, shipPatherLimit2) && this.isNodeClosed(shipPatherLimit, walkLimit - 1, shipPatherLimit2, long2)) {
               double hashPut = this.getStepCost(shipPatherLimit, walkLimit - 1, shipPatherLimit2, long2);

               for (int j = 2; j <= 5; j++) {
                  if (this.isInBounds(shipPatherValue, shipPatherLimit, walkLimit - j, shipPatherLimit2)) {
                     int ascendLimit = walkLimit - j + 1;
                     if (ascendLimit >= this.ascendValue && ascendLimit <= this.descendValue) {
                        this.hashCount = 0;
                        this.neighborKeyBuffer[this.hashCount++] = long2;
                        this.relaxNeighbor(shipPatherValue, shipPatherLimit, ascendLimit, shipPatherLimit2, ShipPather.Move.DIG_DOWN, 2.0 + hashPut + 0.5 * (j - 1));
                        return;
                     }

                     return;
                  }
               }
            }
         }
      }

      private double getStepCost(int hashPutValue, int ascendLimit, int hashPutValue3, long long2) {
         float long2floatopenhashmap = this.long2FloatOpenHashMap.get(long2);
         if (long2floatopenhashmap < 0.0F) {
            long2floatopenhashmap = Math.max(1.0F, this.gridByteHash.getBlockBreakCost(hashPutValue, ascendLimit, hashPutValue3));
            this.long2FloatOpenHashMap.put(long2, long2floatopenhashmap);
         }

         return long2floatopenhashmap;
      }

      private boolean isNodeClosed(int byteHashValue, int ascendLimit, int byteHashValue3, long long2) {
         byte long2byteopenhashmap = this.long2ByteOpenHashMap.get(long2);
         if ((long2byteopenhashmap & 4) == 0) {
            long2byteopenhashmap = (byte)(long2byteopenhashmap | (this.gridByteHash.isBreakableBlock(byteHashValue, ascendLimit, byteHashValue3) ? 12 : 4));
            this.long2ByteOpenHashMap.put(long2, long2byteopenhashmap);
         }

         return (long2byteopenhashmap & 8) != 0;
      }

      private void siftDown(int index) {
         int shipPatherValue = this.copyOfHash[index];
         int walkLimit = this.nodeYs[index];
         int shipPatherValue3 = this.nodeZs[index];

         for (int j = 0; j < 4; j++) {
            int shipPatherLimit = shipPatherValue + ShipPather.shipPatherTable[j];
            int shipPatherLimit2 = shipPatherValue3 + ShipPather.table[j];
            if (shipPatherLimit >= this.boundsMinX && shipPatherLimit <= this.boundsMaxX && shipPatherLimit2 >= this.boundsMinZ && shipPatherLimit2 <= this.boundsMaxZ) {
               boolean shipPatherFlag = this.isInBounds(index, shipPatherLimit, walkLimit - 1, shipPatherLimit2);
               if (shipPatherFlag) {
                  this.tryWalk(index, walkLimit, shipPatherLimit, shipPatherLimit2);
               }

               this.tryAscend(index, shipPatherValue, walkLimit, shipPatherValue3, shipPatherLimit, shipPatherLimit2);
               if (!shipPatherFlag) {
                  this.tryDescend(index, walkLimit, shipPatherLimit, shipPatherLimit2);
               }
            }
         }

         this.expandNodeAt(index, shipPatherValue, walkLimit, shipPatherValue3);
      }

      private List<ShipPather.Step> reconstructPath(int shipPatherValue) {
         ArrayList arrayList = new ArrayList();
         int index = shipPatherValue;

         while (this.parentIndices[index] >= 0) {
            long[] shipPatherValues = this.hashLongs[index];
            List list;
            if (shipPatherValues == null) {
               list = List.of();
            } else {
               BlockPos[] blockPos = new BlockPos[shipPatherValues.length];

               for (int j = 0; j < shipPatherValues.length; j++) {
                  blockPos[j] = BlockPos.fromLong(shipPatherValues[j]);
               }

               list = List.of(blockPos);
            }

            arrayList.add(
               new ShipPather.Step(new BlockPos(this.copyOfHash[index], this.nodeYs[index], this.nodeZs[index]), ShipPather.MOVES[this.hashTable[index]], list)
            );
            index = this.parentIndices[index];
         }

         Collections.reverse(arrayList);
         return List.copyOf(arrayList);
      }

      private void growArrays() {
         int index = --this.predicateCount;
         if (index != 0) {
            this.heapNodeIndices[0] = this.heapNodeIndices[index];
            this.table[0] = this.table[index];
            this.predicateTable[0] = this.predicateTable[index];
            this.heapPriorities[0] = this.heapPriorities[index];
            int shipPatherValue = 0;

            while (true) {
               int shipPatherLimit = 2 * shipPatherValue + 1;
               if (shipPatherLimit >= this.predicateCount) {
                  break;
               }

               int shipPatherValue2 = shipPatherLimit + 1;
               int shipPatherValue3 = shipPatherValue2 < this.predicateCount && this.isLowerPriority(shipPatherValue2, shipPatherLimit) ? shipPatherValue2 : shipPatherLimit;
               if (!this.isLowerPriority(shipPatherValue3, shipPatherValue)) {
                  break;
               }

               this.swapHeapEntries(shipPatherValue, shipPatherValue3);
               shipPatherValue = shipPatherValue3;
            }
         }
      }

      private boolean hasKey(int shipPatherValue, long long2) {
         long hashPosition = ShipPather.hashPosition(long2);
         int index = shipPatherValue;

         while (index >= 0 && (this.copyOf[index] & hashPosition) != 0L) {
            long[] shipPatherValues = this.hashLongs[index];
            if (shipPatherValues != null) {
               for (long shipPatherValuesItem : shipPatherValues) {
                  if (shipPatherValuesItem == long2) {
                     return true;
                  }
               }
            }

            index = this.parentIndices[index];
         }

         return false;
      }

      private double getMoveCost(int shipPatherValue, int byteHashValue, int ascendLimit, int shipPatherValue4) {
         long long2 = BlockPos.asLong(byteHashValue, ascendLimit, shipPatherValue4);
         if (!this.hasVisitedFlag(byteHashValue, ascendLimit, shipPatherValue4, long2) || this.hasKey(shipPatherValue, long2)) {
            return 0.0;
         } else if (!this.isNodeClosed(byteHashValue, ascendLimit, shipPatherValue4, long2)) {
            return -1.0;
         } else {
            this.neighborKeyBuffer[this.hashCount++] = long2;
            return this.getStepCost(byteHashValue, ascendLimit, shipPatherValue4, long2);
         }
      }

      private double getHeuristic(int shipPatherValue, int ascendLimit, int shipPatherValue3) {
         if (!this.hasTarget) {
            return 0.0;
         } else {
            int shipPatherBound = Math.abs(this.targetX - shipPatherValue) + Math.abs(this.targetZ - shipPatherValue3);
            int shipPatherLimit = this.targetY - ascendLimit;
            if (shipPatherLimit > 0) {
               return Math.max(shipPatherBound, shipPatherLimit) + shipPatherLimit;
            } else {
               return shipPatherLimit < 0 ? Math.max(shipPatherBound, (-shipPatherLimit + 4 - 1) / 4) + 0.5 * -shipPatherLimit : shipPatherBound;
            }
         }
      }

      private int getOrCreateNode(int hashValue, int ascendLimit, int hashValue3, long long2) {
         if (this.nodeCount == this.copyOfHash.length) {
            int hashRegistryValue = this.nodeCount * 2;
            this.copyOfHash = Arrays.copyOf(this.copyOfHash, hashRegistryValue);
            this.nodeYs = Arrays.copyOf(this.nodeYs, hashRegistryValue);
            this.nodeZs = Arrays.copyOf(this.nodeZs, hashRegistryValue);
            this.nodeCosts = Arrays.copyOf(this.nodeCosts, hashRegistryValue);
            this.copyOfPredicate = Arrays.copyOf(this.copyOfPredicate, hashRegistryValue);
            this.parentIndices = Arrays.copyOf(this.parentIndices, hashRegistryValue);
            this.copyOf = Arrays.copyOf(this.copyOf, hashRegistryValue);
            this.hashTable = Arrays.copyOf(this.hashTable, hashRegistryValue);
            this.hashLongs = Arrays.copyOf(this.hashLongs, hashRegistryValue);
            this.hashFlags = Arrays.copyOf(this.hashFlags, hashRegistryValue);
         }

         int index = this.nodeCount++;
         this.copyOfHash[index] = hashValue;
         this.nodeYs[index] = ascendLimit;
         this.nodeZs[index] = hashValue3;
         this.nodeCosts[index] = Double.MAX_VALUE;
         this.copyOfPredicate[index] = this.getHeuristic(hashValue, ascendLimit, hashValue3);
         this.long2IntOpenHashMap.put(long2, index);
         return index;
      }

      private void siftUp(int index) {
         if (this.predicateCount == this.heapNodeIndices.length) {
            int shipPatherValue = this.predicateCount * 2;
            this.heapNodeIndices = Arrays.copyOf(this.heapNodeIndices, shipPatherValue);
            this.table = Arrays.copyOf(this.table, shipPatherValue);
            this.predicateTable = Arrays.copyOf(this.predicateTable, shipPatherValue);
            this.heapPriorities = Arrays.copyOf(this.heapPriorities, shipPatherValue);
         }

         int predicateCount2 = this.predicateCount++;
         this.heapNodeIndices[predicateCount2] = index;
         this.table[predicateCount2] = this.insertionOrderCounter++;
         this.predicateTable[predicateCount2] = this.nodeCosts[index] + this.copyOfPredicate[index];
         this.heapPriorities[predicateCount2] = this.copyOfPredicate[index];

         while (predicateCount2 > 0) {
            int shipPatherLimit = predicateCount2 - 1 >>> 1;
            if (!this.isLowerPriority(predicateCount2, shipPatherLimit)) {
               break;
            }

            this.swapHeapEntries(predicateCount2, shipPatherLimit);
            predicateCount2 = shipPatherLimit;
         }
      }

      private void tryWalk(int walkValue, int walkLimit, int hashValue, int walkValue3) {
         if (walkLimit >= this.ascendValue && walkLimit <= this.descendValue) {
            this.hashCount = 0;
            double times = this.getMoveCost(walkValue, hashValue, walkLimit + 1, walkValue3);
            if (!(times < 0.0)) {
               double moveCost = this.getMoveCost(walkValue, hashValue, walkLimit, walkValue3);
               if (!(moveCost < 0.0)) {
                  this.relaxNeighbor(walkValue, hashValue, walkLimit, walkValue3, ShipPather.Move.WALK, 1.0 + times + moveCost);
               }
            }
         }
      }

      private boolean isLowerPriority(int index, int shipPatherLimit) {
         if (this.predicateTable[index] != this.predicateTable[shipPatherLimit]) {
            return this.predicateTable[index] < this.predicateTable[shipPatherLimit];
         } else {
            return this.heapPriorities[index] != this.heapPriorities[shipPatherLimit] ? this.heapPriorities[index] < this.heapPriorities[shipPatherLimit] : this.table[index] < this.table[shipPatherLimit];
         }
      }
   }

   public record Step(BlockPos feet, ShipPather.Move move, List<BlockPos> mine) {
   }
}
