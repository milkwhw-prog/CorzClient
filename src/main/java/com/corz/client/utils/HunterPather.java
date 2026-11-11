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
import com.corz.client.utils.AirPlan;

public final class HunterPather {
   public static final double DIAGONAL_COST = Math.sqrt(2.0);
   private static final int[] CARDINAL_X_OFFSETS = new int[]{0, 1, 0, -1};
   private static final int[] CARDINAL_Z_OFFSETS = new int[]{-1, 0, 1, 0};
   private static final int UNIT_STEP = 1;
   private static final int[] DIAGONAL_X_OFFSETS = new int[]{1, 1, -1, -1};
   private static final int[] DIAGONAL_Z_OFFSETS = new int[]{1, -1, 1, -1};
   private static final HunterPather.Move[] MOVE_VALUES = HunterPather.Move.values();

   private static long getPositionBloomBit(long hunterPatherValue) {
      return 1L << (int)(hunterPatherValue * -7046029254386353131L >>> 58);
   }

   private HunterPather() {
   }

   public static HunterPather.Result findPathWithoutGoal(HunterPather.Grid grid, BlockPos pos, Predicate<BlockPos> predicate, BlockPos pos2, BlockPos pos3, int hunterPatherValue) {
      return findPath(grid, pos, predicate, null, pos2, pos3, hunterPatherValue);
   }

   public static HunterPather.Search createSearch(
      HunterPather.Grid grid, BlockPos pos, Predicate<BlockPos> predicate, BlockPos pos2, BlockPos pos3, BlockPos pos4, int hunterPatherValue, HunterPather.Options options
   ) {
      return new HunterPather.Search(grid, pos, predicate, pos2, pos3, pos4, hunterPatherValue, options == null ? HunterPather.Options.INSTANCE : options);
   }

   static int computeAirBucket(boolean hunterPatherFlag, boolean hunterPatherFlag2, int hunterPatherValue) {
      return hunterPatherFlag && !hunterPatherFlag2 ? 1 + Math.max(0, Math.min(7, hunterPatherValue / 40)) : 0;
   }

   public static HunterPather.Result findPath(
      HunterPather.Grid grid, BlockPos pos, Predicate<BlockPos> predicate, BlockPos pos2, BlockPos pos3, BlockPos pos4, int hunterPatherValue
   ) {
      return getResult(grid, pos, predicate, pos2, pos3, pos4, hunterPatherValue, HunterPather.Options.INSTANCE);
   }

   public static boolean isWalkableSpot(HunterPather.Grid grid, int hunterPatherValue, int y, int z) {
      return grid.isSolidCell(hunterPatherValue, y - 1, z)
         && !grid.isSolidCell(hunterPatherValue, y, z)
         && !grid.isSolidCell(hunterPatherValue, y + 1, z)
         && !grid.isForbiddenCell(hunterPatherValue, y - 1, z)
         && !grid.isForbiddenCell(hunterPatherValue, y, z)
         && !grid.isForbiddenCell(hunterPatherValue, y + 1, z);
   }

   public static HunterPather.Result getResult(
      HunterPather.Grid grid, BlockPos pos, Predicate<BlockPos> predicate, BlockPos pos2, BlockPos pos3, BlockPos pos4, int hunterPatherValue, HunterPather.Options options
   ) {
      HunterPather.Search instance = createSearch(grid, pos, predicate, pos2, pos3, pos4, hunterPatherValue, options);

      HunterPather.Result result;
      while ((result = instance.advanceSearch(Integer.MAX_VALUE)) == null) {
         if (instance.getExpandedNodeCount() >= hunterPatherValue) {
            return instance.getBestPartialResult();
         }
      }

      return result;
   }

   public interface Grid {
      default float getLavaPenalty(int cellsValue, int x, int y) {
         return 0.0F;
      }

      boolean isSolidCell(int cellsValue, int y, int z);

      default boolean isWaterCell(int cellsValue, int z, int x) {
         return false;
      }

      default boolean isNextToLavaCell(int cellsValue, int y, int z) {
         return false;
      }

      float getDigCost(int mutableHardnessValue, int swimWalkLimit, int x);

      default boolean isForbiddenCell(int cellsHashValue, int y, int x) {
         return false;
      }

      boolean canDigCell(int cellsHashValue, int y, int x);
   }

   public static enum Move {
      WALK,
      ASCEND,
      DESCEND,
      DIG_DOWN,
      DIAGONAL,
      SWIM,
      SWIM_UP,
      SWIM_DOWN;
   }

   public record Options(int air, int maxAir, double weight, boolean swim) {
      public static final HunterPather.Options INSTANCE = new HunterPather.Options(1000000, 1000000, 1.0, true);

      public boolean isUnlimitedAir() {
         return this.maxAir >= 1000000;
      }

      public Options(int air, int maxAir, double weight, boolean swim) {
         maxAir = Math.max(1, maxAir);
         air = Math.max(0, Math.min(air, maxAir));
         weight = Double.isFinite(weight) && weight >= 1.0 ? weight : 1.0;
         this.air = air;
         this.maxAir = maxAir;
         this.weight = weight;
         this.swim = swim;
      }
   }

   public record Result(List<HunterPather.Step> steps, boolean reached, int expanded) {
   }

   public static final class Search {
      private long[][] nodeMinedPositions;
      private int[] heapNodeIndices;
      private final int maxAirTicks;
      private boolean[] nodeExpanded;
      private int bestNodeIndex;
      private int[] nodeAirSpentTicks;
      private final Long2IntOpenHashMap[] nodeIndexMapsByAirBucket = new Long2IntOpenHashMap[9];
      private int[] nodeParents;
      private final HunterPather.Options options;
      private double[] nodeCost;
      private int[] heapInsertSeq;
      private final int minY;
      private int pendingMinedCount;
      private final int maxY;
      private final long[] pendingMinedPositions;
      private final int boundsMaxZ;
      private final HunterPather.Grid grid;
      private final int boundsMaxX;
      private int heapInsertCount;
      private final int boundsMinX;
      private HunterPather.Result pathResult;
      private final int goalX;
      private final Predicate<BlockPos> predicate;
      private long[] nodeMinedBloomMask;
      private final int boundsMinZ;
      private final boolean hasGoal;
      private int nodeCount;
      private final Long2ByteOpenHashMap cellFlagCache = new Long2ByteOpenHashMap(4096);
      private int[] nodeZs;
      private int heapSize;
      private final int goalY;
      private final int maxExpandedNodes;
      private int expandedNodeCount;
      private byte[] nodeMoveOrdinals;
      private final int goalZ;
      private int[] nodeXs;
      private double[] heapHeuristic;
      private double[] nodeHeuristic;
      private double[] heapPriority;
      private final Long2FloatOpenHashMap miningCostCache = new Long2FloatOpenHashMap();
      private final Long2IntOpenHashMap surfaceCostCache = new Long2IntOpenHashMap();
      private int[] nodeYs;
      private int[] nodeAirLeftTicks;

      private boolean isSwimmableCell(int byteHashValue, int z, int x) {
         long long2 = BlockPos.asLong(byteHashValue, z, x);
         int byteHashValue4 = this.cellFlagCache.get(long2) & 255;
         if ((byteHashValue4 & 64) == 0) {
            byteHashValue4 |= this.grid.isWaterCell(byteHashValue, z, x) ? 192 : 64;
            this.cellFlagCache.put(long2, (byte)byteHashValue4);
         }

         return (byteHashValue4 & 128) != 0 && !this.isSolidCell(byteHashValue, z, x, long2);
      }

      private double getMiningCost(int hashPutValue, int swimWalkLimit, int x, long long2) {
         float long2floatopenhashmap = this.miningCostCache.get(long2);
         if (long2floatopenhashmap < 0.0F) {
            long2floatopenhashmap = Math.max(1.0F, this.grid.getDigCost(hashPutValue, swimWalkLimit, x));
            this.miningCostCache.put(long2, long2floatopenhashmap);
         }

         return long2floatopenhashmap;
      }

      private double computeHeuristic(int sqrtValue, int x, int y) {
         if (!this.hasGoal) {
            return 0.0;
         } else {
            int sqrtBound = Math.abs(this.goalX - sqrtValue);
            int rocketsBuyBound = Math.abs(this.goalZ - y);
            double hashRegistryBound = Math.max(sqrtBound, rocketsBuyBound) + (HunterPather.DIAGONAL_COST - 1.0) * Math.min(sqrtBound, rocketsBuyBound);
            int sqrtLimit = this.goalY - x;
            if (sqrtLimit > 0) {
               return Math.max(hashRegistryBound, (double)sqrtLimit) + sqrtLimit;
            } else {
               return sqrtLimit < 0 ? Math.max(hashRegistryBound, (double)((-sqrtLimit + 4 - 1) / 4)) + 0.5 * -sqrtLimit : hashRegistryBound;
            }
         }
      }

      private void pushHeapNode(int index) {
         if (this.heapSize == this.heapNodeIndices.length) {
            int hunterPatherValue = this.heapSize * 2;
            this.heapNodeIndices = Arrays.copyOf(this.heapNodeIndices, hunterPatherValue);
            this.heapInsertSeq = Arrays.copyOf(this.heapInsertSeq, hunterPatherValue);
            this.heapPriority = Arrays.copyOf(this.heapPriority, hunterPatherValue);
            this.heapHeuristic = Arrays.copyOf(this.heapHeuristic, hunterPatherValue);
         }

         int heapSize2 = this.heapSize++;
         this.heapNodeIndices[heapSize2] = index;
         this.heapInsertSeq[heapSize2] = this.heapInsertCount++;
         this.heapPriority[heapSize2] = this.getNodePriority(index);
         this.heapHeuristic[heapSize2] = this.nodeHeuristic[index];

         while (heapSize2 > 0) {
            int hunterPatherLimit = heapSize2 - 1 >>> 1;
            if (!this.isHeapEntryBefore(heapSize2, hunterPatherLimit)) {
               break;
            }

            this.swapHeapEntries(heapSize2, hunterPatherLimit);
            heapSize2 = hunterPatherLimit;
         }
      }

      private void expandNode(int index) {
         int swimOptionsValue = this.nodeXs[index];
         int hunterPatherLimit = this.nodeYs[index];
         int x = this.nodeZs[index];
         boolean swimOptionsFlag = this.options.swim() && this.isSwimmableCell(swimOptionsValue, hunterPatherLimit, x);

         for (int j = 0; j < 4; j++) {
            int swimOptionsLimit = swimOptionsValue + HunterPather.CARDINAL_X_OFFSETS[j];
            int swimOptionsLimit2 = x + HunterPather.CARDINAL_Z_OFFSETS[j];
            if (swimOptionsLimit >= this.boundsMinX && swimOptionsLimit <= this.boundsMaxX && swimOptionsLimit2 >= this.boundsMinZ && swimOptionsLimit2 <= this.boundsMaxZ) {
               if (swimOptionsFlag) {
                  this.tryFlatMove(index, hunterPatherLimit, swimOptionsLimit, swimOptionsLimit2, 1.0, HunterPather.Move.SWIM, HunterPather.Move.WALK);
                  this.tryAscendWithoutMining(index, swimOptionsValue, hunterPatherLimit, x, swimOptionsLimit, swimOptionsLimit2);
                  if (!this.isSolidAndNotMined(index, swimOptionsLimit, hunterPatherLimit, swimOptionsLimit2)
                     && !this.isSolidAndNotMined(index, swimOptionsLimit, hunterPatherLimit + 1, swimOptionsLimit2)
                     && !this.isSolidAndNotMined(index, swimOptionsLimit, hunterPatherLimit - 1, swimOptionsLimit2)
                     && !this.isSwimmableCell(swimOptionsLimit, hunterPatherLimit, swimOptionsLimit2)) {
                     this.tryDescend(index, hunterPatherLimit, swimOptionsLimit, swimOptionsLimit2, HunterPather.CARDINAL_X_OFFSETS[j], HunterPather.CARDINAL_Z_OFFSETS[j]);
                  }
               } else {
                  boolean solidAndNotMined = this.isSolidAndNotMined(index, swimOptionsLimit, hunterPatherLimit - 1, swimOptionsLimit2);
                  boolean swimWalkFlag = this.options.swim() && this.isSwimmableCell(swimOptionsLimit, hunterPatherLimit, swimOptionsLimit2);
                  if (solidAndNotMined || swimWalkFlag) {
                     this.tryWalkOrSwim(index, hunterPatherLimit, swimOptionsLimit, swimOptionsLimit2, swimWalkFlag);
                  }

                  this.tryAscendWithMining(index, swimOptionsValue, hunterPatherLimit, x, swimOptionsLimit, swimOptionsLimit2);
                  if (!solidAndNotMined && !swimWalkFlag) {
                     this.tryDescend(index, hunterPatherLimit, swimOptionsLimit, swimOptionsLimit2, HunterPather.CARDINAL_X_OFFSETS[j], HunterPather.CARDINAL_Z_OFFSETS[j]);
                  }
               }
            }
         }

         for (int k = 0; k < 4; k++) {
            this.tryDiagonalMove(index, swimOptionsValue, hunterPatherLimit, x, HunterPather.DIAGONAL_X_OFFSETS[k], HunterPather.DIAGONAL_Z_OFFSETS[k], swimOptionsFlag);
         }

         if (swimOptionsFlag) {
            this.trySwimVertical(index, swimOptionsValue, hunterPatherLimit, x);
         } else {
            this.tryDigDown(index, swimOptionsValue, hunterPatherLimit, x);
         }
      }

      private double getNodePriority(int index) {
         return this.nodeCost[index] + this.options.weight() * this.nodeHeuristic[index];
      }

      private Long2IntOpenHashMap getNodeIndexMap(int index) {
         Long2IntOpenHashMap mapsReturn = this.nodeIndexMapsByAirBucket[index];
         if (mapsReturn == null) {
            mapsReturn = new Long2IntOpenHashMap();
            mapsReturn.defaultReturnValue(-1);
            this.nodeIndexMapsByAirBucket[index] = mapsReturn;
         }

         return mapsReturn;
      }

      private List<HunterPather.Step> buildStepsTo(int hunterPatherValue) {
         ArrayList arrayList = new ArrayList();
         int index = hunterPatherValue;

         while (this.nodeParents[index] >= 0) {
            long[] hunterPatherValues = this.nodeMinedPositions[index];
            List list;
            if (hunterPatherValues == null) {
               list = List.of();
            } else {
               BlockPos[] blockPos = new BlockPos[hunterPatherValues.length];

               for (int j = 0; j < hunterPatherValues.length; j++) {
                  blockPos[j] = BlockPos.fromLong(hunterPatherValues[j]);
               }

               list = List.of(blockPos);
            }

            boolean hunterPatherFlag = this.options.swim() && this.isSwimmableCell(this.nodeXs[index], this.nodeYs[index], this.nodeZs[index]);
            arrayList.add(
               new HunterPather.Step(
                  new BlockPos(this.nodeXs[index], this.nodeYs[index], this.nodeZs[index]),
                  HunterPather.MOVE_VALUES[this.nodeMoveOrdinals[index]],
                  list,
                  hunterPatherFlag,
                  this.isSubmerged(this.nodeXs[index], this.nodeYs[index], this.nodeZs[index])
               )
            );
            index = this.nodeParents[index];
         }

         Collections.reverse(arrayList);
         return List.copyOf(arrayList);
      }

      private void tryRelaxNode(int index, int optionsValue, int x, int y, HunterPather.Move move, double optionsValue4) {
         if (!this.isHazardCell(optionsValue, x, y) && !this.isHazardCell(optionsValue, x + 1, y) && !this.isHazardCell(optionsValue, x - 1, y)) {
            boolean swimOptions = this.isSubmerged(optionsValue, x, y);
            double optionsValue5 = 0.0;
            int hunterPatherValue;
            int optionsBound;
            if (swimOptions && !this.options.isUnlimitedAir()) {
               int moveAirTicks = getMoveAirTicks(move, this.nodeXs[index] != optionsValue && this.nodeZs[index] != y);
               optionsBound = this.nodeAirSpentTicks[index] + moveAirTicks;
               hunterPatherValue = this.nodeAirLeftTicks[index] - moveAirTicks;
               int hashPut = this.getAirTicksToSurface(optionsValue, x, y);
               int airPlanBound = hashPut < 0 ? optionsBound : Math.min(hashPut, optionsBound);
               if (hunterPatherValue < AirPlan.getAirNeededForPathCost(airPlanBound) + 40) {
                  return;
               }

               optionsValue5 = 0.6 + 1.5 * (1.0 - (double)hunterPatherValue / Math.max(1, this.maxAirTicks));
            } else {
               hunterPatherValue = this.maxAirTicks;
               optionsBound = 0;
            }

            double optionsLimit = this.nodeCost[index] + optionsValue4 + optionsValue5 + Math.max(0.0F, this.grid.getLavaPenalty(optionsValue, x, y));
            long long2 = BlockPos.asLong(optionsValue, x, y);
            int airBucket = HunterPather.computeAirBucket(swimOptions, this.options.isUnlimitedAir(), hunterPatherValue);
            int mapsreturn = this.getNodeIndexMap(airBucket).get(long2);
            if (mapsreturn < 0) {
               mapsreturn = this.addNode(optionsValue, x, y, long2, airBucket);
            } else if (optionsLimit >= this.nodeCost[mapsreturn] - 1.0E-9 || swimOptions && hunterPatherValue < this.nodeAirLeftTicks[mapsreturn]) {
               return;
            }

            long optionsValue10 = this.nodeMinedBloomMask[index];

            for (int i = 0; i < this.pendingMinedCount; i++) {
               optionsValue10 |= HunterPather.getPositionBloomBit(this.pendingMinedPositions[i]);
            }

            this.nodeCost[mapsreturn] = optionsLimit;
            this.nodeParents[mapsreturn] = index;
            this.nodeMoveOrdinals[mapsreturn] = (byte)move.ordinal();
            this.nodeMinedPositions[mapsreturn] = this.pendingMinedCount == 0 ? null : Arrays.copyOf(this.pendingMinedPositions, this.pendingMinedCount);
            this.nodeMinedBloomMask[mapsreturn] = optionsValue10;
            this.nodeAirLeftTicks[mapsreturn] = hunterPatherValue;
            this.nodeAirSpentTicks[mapsreturn] = optionsBound;
            this.nodeExpanded[mapsreturn] = false;
            this.pushHeapNode(mapsreturn);
         }
      }

      private boolean isHazardCell(int byteHashValue, int y, int x) {
         long long2 = BlockPos.asLong(byteHashValue, y, x);
         int byteHashValue4 = this.cellFlagCache.get(long2) & 255;
         if ((byteHashValue4 & 16) == 0) {
            byteHashValue4 |= this.grid.isForbiddenCell(byteHashValue, y, x) ? 48 : 16;
            this.cellFlagCache.put(long2, (byte)byteHashValue4);
         }

         return (byteHashValue4 & 32) != 0;
      }

      static int getMoveAirTicks(HunterPather.Move move, boolean hunterPatherFlag) {
         return switch (move) {
            case ASCEND -> 10;
            case DESCEND -> 5;
            default -> hunterPatherFlag ? 18 : 13;
            case SWIM_UP -> 8;
            case SWIM_DOWN -> 9;
         };
      }

      private void tryDigDown(int hunterPatherValue, int hunterPatherLimit, int hunterPatherValue2, int x) {
         if (hunterPatherLimit >= this.boundsMinX && hunterPatherLimit <= this.boundsMaxX && x >= this.boundsMinZ && x <= this.boundsMaxZ) {
            long long2 = BlockPos.asLong(hunterPatherLimit, hunterPatherValue2 - 1, x);
            if (this.isSolidAndNotMined(hunterPatherValue, hunterPatherLimit, hunterPatherValue2 - 1, x) && this.isMineable(hunterPatherLimit, hunterPatherValue2 - 1, x, long2)) {
               double hashPut = this.getMiningCost(hunterPatherLimit, hunterPatherValue2 - 1, x, long2);

               for (int j = 2; j <= 5; j++) {
                  if (this.isHazardCell(hunterPatherLimit, hunterPatherValue2 - j, x)) {
                     return;
                  }

                  if (this.options.swim() && this.isSwimmableCell(hunterPatherLimit, hunterPatherValue2 - j, x)) {
                     return;
                  }

                  if (this.isSolidAndNotMined(hunterPatherValue, hunterPatherLimit, hunterPatherValue2 - j, x)) {
                     int y = hunterPatherValue2 - j + 1;
                     if (y >= this.minY && y <= this.maxY && !this.grid.isNextToLavaCell(hunterPatherLimit, y, x)) {
                        this.pendingMinedCount = 0;
                        this.pendingMinedPositions[this.pendingMinedCount++] = long2;
                        this.tryRelaxNode(hunterPatherValue, hunterPatherLimit, y, x, HunterPather.Move.DIG_DOWN, 2.0 + hashPut + 0.5 * (j - 1));
                        return;
                     }

                     return;
                  }
               }
            }
         }
      }

      private boolean isSolidAndNotMined(int hunterPatherValue, int byteHashValue, int hunterPatherLimit, int x) {
         long long2 = BlockPos.asLong(byteHashValue, hunterPatherLimit, x);
         return this.isSolidCell(byteHashValue, hunterPatherLimit, x, long2) && !this.isMinedAlongPath(hunterPatherValue, long2);
      }

      public boolean hasResult() {
         return this.pathResult != null;
      }

      private boolean isBodyClear(int hunterPatherValue, int byteHashValue, int hunterPatherLimit, int x) {
         return !this.isSolidAndNotMined(hunterPatherValue, byteHashValue, hunterPatherLimit, x) && !this.isSolidAndNotMined(hunterPatherValue, byteHashValue, hunterPatherLimit + 1, x) && !this.isHazardCell(byteHashValue, hunterPatherLimit, x) && !this.isHazardCell(byteHashValue, hunterPatherLimit + 1, x);
      }

      public HunterPather.Result getBestPartialResult() {
         return new HunterPather.Result(this.buildStepsTo(this.bestNodeIndex), false, this.expandedNodeCount);
      }

      private void tryFlatMove(int hunterPatherValue, int hunterPatherLimit, int byteHashValue, int x, double hunterPatherFactor, HunterPather.Move move, HunterPather.Move move2) {
         if (hunterPatherLimit >= this.minY && hunterPatherLimit <= this.maxY) {
            if (!this.isSolidAndNotMined(hunterPatherValue, byteHashValue, hunterPatherLimit, x) && !this.isSolidAndNotMined(hunterPatherValue, byteHashValue, hunterPatherLimit + 1, x)) {
               this.pendingMinedCount = 0;
               if (this.isSwimmableCell(byteHashValue, hunterPatherLimit, x)) {
                  this.tryRelaxNode(hunterPatherValue, byteHashValue, hunterPatherLimit, x, move, hunterPatherFactor * (this.isSubmerged(byteHashValue, hunterPatherLimit, x) ? 2.8 : 2.0));
               } else {
                  if (this.isSolidAndNotMined(hunterPatherValue, byteHashValue, hunterPatherLimit - 1, x)) {
                     this.tryRelaxNode(hunterPatherValue, byteHashValue, hunterPatherLimit, x, move2, hunterPatherFactor * 1.5);
                  }
               }
            }
         }
      }

      private boolean isSupported(int swimOptionsValue, int byteHashValue, int y, int x) {
         if (this.isHazardCell(byteHashValue, y, x) || this.isHazardCell(byteHashValue, y + 1, x)) {
            return false;
         } else if (this.isSolidAndNotMined(swimOptionsValue, byteHashValue, y, x)) {
            return true;
         } else {
            for (int j = 1; j <= 4; j++) {
               if (this.isHazardCell(byteHashValue, y - j, x)) {
                  return false;
               }

               if (this.isSolidAndNotMined(swimOptionsValue, byteHashValue, y - j, x) || this.options.swim() && this.isSwimmableCell(byteHashValue, y - j, x)) {
                  return true;
               }
            }

            return false;
         }
      }

      private boolean isMineable(int byteHashValue, int y, int x, long long2) {
         if (this.isHazardCell(byteHashValue, y, x)) {
            return false;
         } else {
            int byteHashValue5 = this.cellFlagCache.get(long2) & 255;
            if ((byteHashValue5 & 4) == 0) {
               byteHashValue5 |= this.grid.canDigCell(byteHashValue, y, x) ? 12 : 4;
               this.cellFlagCache.put(long2, (byte)byteHashValue5);
            }

            return (byteHashValue5 & 8) != 0;
         }
      }

      private boolean isMinedAlongPath(int hunterPatherValue, long long2) {
         long positionBloomBit = HunterPather.getPositionBloomBit(long2);
         int index = hunterPatherValue;

         while (index >= 0 && (this.nodeMinedBloomMask[index] & positionBloomBit) != 0L) {
            long[] hunterPatherValues = this.nodeMinedPositions[index];
            if (hunterPatherValues != null) {
               for (long hunterPatherValuesItem : hunterPatherValues) {
                  if (hunterPatherValuesItem == long2) {
                     return true;
                  }
               }
            }

            index = this.nodeParents[index];
         }

         return false;
      }

      private void popHeapRoot() {
         int index = --this.heapSize;
         if (index != 0) {
            this.heapNodeIndices[0] = this.heapNodeIndices[index];
            this.heapInsertSeq[0] = this.heapInsertSeq[index];
            this.heapPriority[0] = this.heapPriority[index];
            this.heapHeuristic[0] = this.heapHeuristic[index];
            int hunterPatherValue = 0;

            while (true) {
               int hunterPatherLimit = 2 * hunterPatherValue + 1;
               if (hunterPatherLimit >= this.heapSize) {
                  break;
               }

               int hunterPatherValue2 = hunterPatherLimit + 1;
               int hunterPatherValue3 = hunterPatherValue2 < this.heapSize && this.isHeapEntryBefore(hunterPatherValue2, hunterPatherLimit) ? hunterPatherValue2 : hunterPatherLimit;
               if (!this.isHeapEntryBefore(hunterPatherValue3, hunterPatherValue)) {
                  break;
               }

               this.swapHeapEntries(hunterPatherValue, hunterPatherValue3);
               hunterPatherValue = hunterPatherValue3;
            }
         }
      }

      private boolean isSubmerged(int swimOptionsValue, int hunterPatherLimit, int x) {
         return this.options.swim() && this.isSwimmableCell(swimOptionsValue, hunterPatherLimit + 1, x);
      }

      private int getAirTicksToSurface(int hashPutValue, int hashPutValue2, int x) {
         long long2 = BlockPos.asLong(hashPutValue, hashPutValue2, x);
         int long2intopenhashmap = this.surfaceCostCache.get(long2);
         if (long2intopenhashmap != Integer.MIN_VALUE) {
            return long2intopenhashmap;
         } else {
            int hashPut = -1;

            for (int i = 0; i <= 24; i++) {
               int z = hashPutValue2 + i;
               if (i > 0 && (this.isSolidCell(hashPutValue, z, x, BlockPos.asLong(hashPutValue, z, x)) || this.isHazardCell(hashPutValue, z, x))) {
                  break;
               }

               long long3 = BlockPos.asLong(hashPutValue, z + 1, x);
               if (this.isSolidCell(hashPutValue, z + 1, x, long3) || this.isHazardCell(hashPutValue, z + 1, x)) {
                  break;
               }

               if (!this.isSwimmableCell(hashPutValue, z + 1, x)) {
                  hashPut = i * 8;
                  break;
               }
            }

            this.surfaceCostCache.put(long2, hashPut);
            return hashPut;
         }
      }

      public HunterPather.Result advanceSearch(int predicateBound) {
         if (this.pathResult != null) {
            return this.pathResult;
         } else {
            long predicateValue = (long)this.expandedNodeCount + Math.max(1, predicateBound);

            while (this.heapSize > 0) {
               int value = this.heapNodeIndices[0];
               double predicateValue2 = this.heapPriority[0];
               this.popHeapRoot();
               if (!this.nodeExpanded[value] && predicateValue2 == this.getNodePriority(value)) {
                  if (this.predicate.test(new BlockPos(this.nodeXs[value], this.nodeYs[value], this.nodeZs[value]))) {
                     return this.pathResult = new HunterPather.Result(this.buildStepsTo(value), true, this.expandedNodeCount);
                  }

                  if (this.expandedNodeCount >= this.maxExpandedNodes) {
                     return this.pathResult = new HunterPather.Result(this.buildStepsTo(this.bestNodeIndex), false, this.expandedNodeCount);
                  }

                  this.nodeExpanded[value] = true;
                  this.expandedNodeCount++;
                  if (this.nodeHeuristic[value] < this.nodeHeuristic[this.bestNodeIndex]
                     || this.nodeHeuristic[value] == this.nodeHeuristic[this.bestNodeIndex] && this.nodeCost[value] < this.nodeCost[this.bestNodeIndex]) {
                     this.bestNodeIndex = value;
                  }

                  this.expandNode(value);
                  if (this.expandedNodeCount >= predicateValue) {
                     return null;
                  }
               }
            }

            return this.pathResult = new HunterPather.Result(this.buildStepsTo(this.bestNodeIndex), false, this.expandedNodeCount);
         }
      }

      public int getExpandedNodeCount() {
         return this.expandedNodeCount;
      }

      private Search(
         HunterPather.Grid grid, BlockPos pos, Predicate<BlockPos> predicate, BlockPos pos2, BlockPos pos3, BlockPos pos4, int hunterPatherBound, HunterPather.Options options
      ) {
         this.nodeXs = new int[1024];
         this.nodeYs = new int[1024];
         this.nodeZs = new int[1024];
         this.nodeCost = new double[1024];
         this.nodeHeuristic = new double[1024];
         this.nodeParents = new int[1024];
         this.nodeMinedBloomMask = new long[1024];
         this.nodeMoveOrdinals = new byte[1024];
         this.nodeMinedPositions = new long[1024][];
         this.nodeExpanded = new boolean[1024];
         this.nodeAirLeftTicks = new int[1024];
         this.nodeAirSpentTicks = new int[1024];
         this.heapNodeIndices = new int[1024];
         this.heapInsertSeq = new int[1024];
         this.heapPriority = new double[1024];
         this.heapHeuristic = new double[1024];
         this.pendingMinedPositions = new long[3];
         this.grid = grid;
         this.predicate = predicate;
         this.options = options;
         this.maxAirTicks = AirPlan.getLowAirThreshold(options.maxAir());
         this.maxExpandedNodes = Math.max(1, hunterPatherBound);
         this.hasGoal = pos2 != null;
         this.goalX = this.hasGoal ? pos2.getX() : 0;
         this.goalY = this.hasGoal ? pos2.getY() : 0;
         this.goalZ = this.hasGoal ? pos2.getZ() : 0;
         this.boundsMinX = Math.min(pos3.getX(), pos4.getX());
         this.minY = Math.min(pos3.getY(), pos4.getY());
         this.boundsMinZ = Math.min(pos3.getZ(), pos4.getZ());
         this.boundsMaxX = Math.max(pos3.getX(), pos4.getX());
         this.maxY = Math.max(pos3.getY(), pos4.getY());
         this.boundsMaxZ = Math.max(pos3.getZ(), pos4.getZ());
         this.miningCostCache.defaultReturnValue(-1.0F);
         this.surfaceCostCache.defaultReturnValue(Integer.MIN_VALUE);
         boolean swimOptions = this.isSubmerged(pos.getX(), pos.getY(), pos.getZ());
         int hunterPatherValue = swimOptions ? Math.min(options.air(), this.maxAirTicks) : this.maxAirTicks;
         int put = this.addNode(pos.getX(), pos.getY(), pos.getZ(), pos.asLong(), HunterPather.computeAirBucket(swimOptions, options.isUnlimitedAir(), hunterPatherValue));
         this.nodeCost[put] = 0.0;
         this.nodeParents[put] = -1;
         this.nodeMoveOrdinals[put] = -1;
         this.nodeAirLeftTicks[put] = hunterPatherValue;
         this.nodeAirSpentTicks[put] = swimOptions ? Math.max(0, this.maxAirTicks - hunterPatherValue) : 0;
         this.pushHeapNode(put);
      }

      private void tryWalkOrSwim(int swimWalkValue, int swimWalkLimit, int optionsValue, int x, boolean swimWalkFlag) {
         if (swimWalkLimit >= this.minY && swimWalkLimit <= this.maxY) {
            this.pendingMinedCount = 0;
            double times = this.getClearCost(swimWalkValue, optionsValue, swimWalkLimit + 1, x);
            if (!(times < 0.0)) {
               double clearCost = this.getClearCost(swimWalkValue, optionsValue, swimWalkLimit, x);
               if (!(clearCost < 0.0)) {
                  if (swimWalkFlag) {
                     this.tryRelaxNode(swimWalkValue, optionsValue, swimWalkLimit, x, HunterPather.Move.SWIM, 2.0 + times + clearCost);
                  } else {
                     this.tryRelaxNode(swimWalkValue, optionsValue, swimWalkLimit, x, HunterPather.Move.WALK, 1.0 + times + clearCost);
                  }
               }
            }
         }
      }

      private boolean isHeapEntryBefore(int index, int hunterPatherLimit) {
         if (this.heapPriority[index] != this.heapPriority[hunterPatherLimit]) {
            return this.heapPriority[index] < this.heapPriority[hunterPatherLimit];
         } else {
            return this.heapHeuristic[index] != this.heapHeuristic[hunterPatherLimit] ? this.heapHeuristic[index] < this.heapHeuristic[hunterPatherLimit] : this.heapInsertSeq[index] < this.heapInsertSeq[hunterPatherLimit];
         }
      }

      private void tryAscendWithoutMining(int ascendValue, int byteHashValue, int hunterPatherLimit, int x, int optionsValue, int ascendValue6) {
         int ascendLimit = hunterPatherLimit + 1;
         if (ascendLimit >= this.minY && ascendLimit <= this.maxY && this.isSolidAndNotMined(ascendValue, optionsValue, hunterPatherLimit, ascendValue6)) {
            if (!this.isSolidAndNotMined(ascendValue, byteHashValue, hunterPatherLimit + 2, x) && !this.isSolidAndNotMined(ascendValue, optionsValue, ascendLimit, ascendValue6) && !this.isSolidAndNotMined(ascendValue, optionsValue, ascendLimit + 1, ascendValue6)) {
               if (!this.isHazardCell(byteHashValue, hunterPatherLimit + 2, x)) {
                  this.pendingMinedCount = 0;
                  this.tryRelaxNode(ascendValue, optionsValue, ascendLimit, ascendValue6, HunterPather.Move.ASCEND, 2.5);
               }
            }
         }
      }

      private boolean isSolidCell(int byteHashValue, int z, int x, long long2) {
         int byteHashValue5 = this.cellFlagCache.get(long2) & 255;
         if ((byteHashValue5 & 1) == 0) {
            byteHashValue5 |= this.grid.isSolidCell(byteHashValue, z, x) ? 3 : 1;
            this.cellFlagCache.put(long2, (byte)byteHashValue5);
         }

         return (byteHashValue5 & 2) != 0;
      }

      private void trySwimVertical(int swimDownValue, int byteHashValue, int hunterPatherLimit, int x) {
         if (hunterPatherLimit + 1 <= this.maxY && this.isSwimmableCell(byteHashValue, hunterPatherLimit + 1, x) && !this.isSolidAndNotMined(swimDownValue, byteHashValue, hunterPatherLimit + 2, x)) {
            this.pendingMinedCount = 0;
            this.tryRelaxNode(swimDownValue, byteHashValue, hunterPatherLimit + 1, x, HunterPather.Move.SWIM_UP, 1.7);
         }

         if (hunterPatherLimit - 1 >= this.minY && this.isSwimmableCell(byteHashValue, hunterPatherLimit - 1, x)) {
            this.pendingMinedCount = 0;
            this.tryRelaxNode(swimDownValue, byteHashValue, hunterPatherLimit - 1, x, HunterPather.Move.SWIM_DOWN, 1.8);
         }
      }

      private int addNode(int hunterPatherValue, int x, int y, long long2, int airBucket) {
         if (this.nodeCount == this.nodeXs.length) {
            int hashRegistryValue = this.nodeCount * 2;
            this.nodeXs = Arrays.copyOf(this.nodeXs, hashRegistryValue);
            this.nodeYs = Arrays.copyOf(this.nodeYs, hashRegistryValue);
            this.nodeZs = Arrays.copyOf(this.nodeZs, hashRegistryValue);
            this.nodeCost = Arrays.copyOf(this.nodeCost, hashRegistryValue);
            this.nodeHeuristic = Arrays.copyOf(this.nodeHeuristic, hashRegistryValue);
            this.nodeParents = Arrays.copyOf(this.nodeParents, hashRegistryValue);
            this.nodeMinedBloomMask = Arrays.copyOf(this.nodeMinedBloomMask, hashRegistryValue);
            this.nodeMoveOrdinals = Arrays.copyOf(this.nodeMoveOrdinals, hashRegistryValue);
            this.nodeMinedPositions = Arrays.copyOf(this.nodeMinedPositions, hashRegistryValue);
            this.nodeExpanded = Arrays.copyOf(this.nodeExpanded, hashRegistryValue);
            this.nodeAirLeftTicks = Arrays.copyOf(this.nodeAirLeftTicks, hashRegistryValue);
            this.nodeAirSpentTicks = Arrays.copyOf(this.nodeAirSpentTicks, hashRegistryValue);
         }

         int index = this.nodeCount++;
         this.nodeXs[index] = hunterPatherValue;
         this.nodeYs[index] = x;
         this.nodeZs[index] = y;
         this.nodeCost[index] = Double.MAX_VALUE;
         this.nodeHeuristic[index] = this.computeHeuristic(hunterPatherValue, x, y);
         this.getNodeIndexMap(airBucket).put(long2, index);
         return index;
      }

      private void swapHeapEntries(int index, int hunterPatherLimit) {
         int hunterPatherValue = this.heapNodeIndices[index];
         this.heapNodeIndices[index] = this.heapNodeIndices[hunterPatherLimit];
         this.heapNodeIndices[hunterPatherLimit] = hunterPatherValue;
         int hunterPatherValue2 = this.heapInsertSeq[index];
         this.heapInsertSeq[index] = this.heapInsertSeq[hunterPatherLimit];
         this.heapInsertSeq[hunterPatherLimit] = hunterPatherValue2;
         double hunterPatherValue3 = this.heapPriority[index];
         this.heapPriority[index] = this.heapPriority[hunterPatherLimit];
         this.heapPriority[hunterPatherLimit] = hunterPatherValue3;
         double hunterPatherValue4 = this.heapHeuristic[index];
         this.heapHeuristic[index] = this.heapHeuristic[hunterPatherLimit];
         this.heapHeuristic[hunterPatherLimit] = hunterPatherValue4;
      }

      private void tryDiagonalMove(int sqrtSwimValue, int byteHashValue, int sqrtSwimLimit, int x, int sqrtSwimValue4, int sqrtSwimValue5, boolean sqrtSwimFlag) {
         int sqrtSwimLimit2 = byteHashValue + sqrtSwimValue4;
         int sqrtSwimLimit3 = x + sqrtSwimValue5;
         if (sqrtSwimLimit2 >= this.boundsMinX && sqrtSwimLimit2 <= this.boundsMaxX && sqrtSwimLimit3 >= this.boundsMinZ && sqrtSwimLimit3 <= this.boundsMaxZ && sqrtSwimLimit >= this.minY && sqrtSwimLimit <= this.maxY) {
            if (this.isBodyClear(sqrtSwimValue, sqrtSwimLimit2, sqrtSwimLimit, sqrtSwimLimit3) && this.isBodyClear(sqrtSwimValue, byteHashValue + sqrtSwimValue4, sqrtSwimLimit, x) && this.isBodyClear(sqrtSwimValue, byteHashValue, sqrtSwimLimit, x + sqrtSwimValue5)) {
               if (sqrtSwimFlag) {
                  this.tryFlatMove(sqrtSwimValue, sqrtSwimLimit, sqrtSwimLimit2, sqrtSwimLimit3, HunterPather.DIAGONAL_COST, HunterPather.Move.SWIM, HunterPather.Move.DIAGONAL);
               } else {
                  this.pendingMinedCount = 0;
                  if (this.options.swim() && this.isSwimmableCell(sqrtSwimLimit2, sqrtSwimLimit, sqrtSwimLimit3)) {
                     this.tryRelaxNode(sqrtSwimValue, sqrtSwimLimit2, sqrtSwimLimit, sqrtSwimLimit3, HunterPather.Move.SWIM, HunterPather.DIAGONAL_COST * 2.0);
                  } else if (this.isSolidAndNotMined(sqrtSwimValue, sqrtSwimLimit2, sqrtSwimLimit - 1, sqrtSwimLimit3) && this.isSolidAndNotMined(sqrtSwimValue, byteHashValue + sqrtSwimValue4, sqrtSwimLimit - 1, x) && this.isSolidAndNotMined(sqrtSwimValue, byteHashValue, sqrtSwimLimit - 1, x + sqrtSwimValue5)) {
                     if (!this.isHazardCell(byteHashValue + sqrtSwimValue4, sqrtSwimLimit - 1, x) && !this.isHazardCell(byteHashValue, sqrtSwimLimit - 1, x + sqrtSwimValue5)) {
                        this.tryRelaxNode(sqrtSwimValue, sqrtSwimLimit2, sqrtSwimLimit, sqrtSwimLimit3, HunterPather.Move.DIAGONAL, HunterPather.DIAGONAL_COST);
                     }
                  }
               }
            }
         }
      }

      private void tryAscendWithMining(int ascendValue, int byteHashValue, int hunterPatherLimit, int ascendValue4, int optionsValue, int x) {
         int ascendLimit = hunterPatherLimit + 1;
         if (ascendLimit >= this.minY && ascendLimit <= this.maxY && this.isSolidAndNotMined(ascendValue, optionsValue, hunterPatherLimit, x)) {
            this.pendingMinedCount = 0;
            double times = this.getClearCost(ascendValue, byteHashValue, hunterPatherLimit + 2, ascendValue4);
            if (!(times < 0.0)) {
               double clearCost = this.getClearCost(ascendValue, optionsValue, ascendLimit + 1, x);
               if (!(clearCost < 0.0)) {
                  double clearCost3 = this.getClearCost(ascendValue, optionsValue, ascendLimit, x);
                  if (!(clearCost3 < 0.0)) {
                     this.tryRelaxNode(ascendValue, optionsValue, ascendLimit, x, HunterPather.Move.ASCEND, 2.0 + times + clearCost + clearCost3);
                  }
               }
            }
         }
      }

      private void tryDescend(int descendSwimValue, int hunterPatherLimit, int byteHashValue, int x, int descendSwimValue5, int descendSwimValue6) {
         this.pendingMinedCount = 0;
         double times = this.getClearCost(descendSwimValue, byteHashValue, hunterPatherLimit + 1, x);
         if (!(times < 0.0)) {
            double clearCost = this.getClearCost(descendSwimValue, byteHashValue, hunterPatherLimit, x);
            if (!(clearCost < 0.0)) {
               int descendSwimLimit = this.options.swim() ? 12 : 4;

               for (int j = 1; j <= descendSwimLimit; j++) {
                  int y = hunterPatherLimit - j;
                  if (this.isHazardCell(byteHashValue, y, x)) {
                     return;
                  }

                  if (this.options.swim() && this.isSwimmableCell(byteHashValue, y, x)) {
                     if (y >= this.minY && y <= this.maxY) {
                        this.tryRelaxNode(descendSwimValue, byteHashValue, y, x, HunterPather.Move.DESCEND, 1.0 + times + clearCost + 0.3 * j);
                        return;
                     }

                     return;
                  }

                  if (this.isSolidAndNotMined(descendSwimValue, byteHashValue, y, x)) {
                     if (j < 2) {
                        return;
                     }

                     if (j > 4) {
                        return;
                     }

                     int descendSwimLimit3 = y + 1;
                     if (descendSwimLimit3 >= this.minY && descendSwimLimit3 <= this.maxY && !this.grid.isNextToLavaCell(byteHashValue, descendSwimLimit3, x) && this.isSupported(descendSwimValue, byteHashValue + descendSwimValue5, descendSwimLimit3, x + descendSwimValue6)) {
                        this.tryRelaxNode(descendSwimValue, byteHashValue, descendSwimLimit3, x, HunterPather.Move.DESCEND, 1.0 + times + clearCost + 0.5 * (j - 1));
                        return;
                     }

                     return;
                  }
               }
            }
         }
      }

      private double getClearCost(int hunterPatherValue, int byteHashValue, int swimWalkLimit, int x) {
         long long2 = BlockPos.asLong(byteHashValue, swimWalkLimit, x);
         if (this.isHazardCell(byteHashValue, swimWalkLimit, x)) {
            return -1.0;
         } else if (!this.isSolidCell(byteHashValue, swimWalkLimit, x, long2) || this.isMinedAlongPath(hunterPatherValue, long2)) {
            return 0.0;
         } else if (!this.isMineable(byteHashValue, swimWalkLimit, x, long2)) {
            return -1.0;
         } else {
            this.pendingMinedPositions[this.pendingMinedCount++] = long2;
            return this.getMiningCost(byteHashValue, swimWalkLimit, x, long2);
         }
      }
   }

   public record Step(BlockPos feet, HunterPather.Move move, List<BlockPos> mine, boolean wet, boolean submerged) {
   }
}
