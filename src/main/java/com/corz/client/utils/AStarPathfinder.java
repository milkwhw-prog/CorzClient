package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.LongPredicate;
import com.corz.client.utils.FallDamageMath;

public final class AStarPathfinder {
   private static final int[] CARDINAL_DX = new int[]{0, 1, 0, -1};
   private static final int[] CARDINAL_DZ = new int[]{-1, 0, 1, 0};
   private static final int[] DIAGONAL_DX = new int[]{1, 1, -1, -1};
   private static final int[] DIAGONAL_DZ = new int[]{1, -1, 1, -1};
   private static final AStarPathfinder.Move[] MOVE_VALUES = AStarPathfinder.Move.values();

   public static long packPosKey(int hashRegistryValue, int blockY, int z2) {
      return (long)(hashRegistryValue & 67108863) << 38 | (long)(z2 & 67108863) << 12 | blockY & 4095L;
   }

   public static int toBlockY(double hashRegistryValue) {
      return (int)Math.floor(hashRegistryValue + 0.001);
   }

   public static double findGroundY(AStarPathfinder.World world, int hashRegistryValue, int z2, double groundY, double hashRegistryBound, int maxMaxDrop) {
      if (world != null && Double.isFinite(groundY)) {
         int floor5 = (int)Math.floor(groundY + Math.max(hashRegistryBound, 1.8) - 0.001);
         int hashRegistryValue6 = (int)Math.floor(groundY + 0.001) - maxMaxDrop - 1;

         for (int i = floor5; i >= hashRegistryValue6; i--) {
            if (world.hasCollision(hashRegistryValue, i, z2)) {
               double mutableHash = world.getCollisionTopY(hashRegistryValue, i, z2);
               if (!Double.isNaN(mutableHash)) {
                  if (mutableHash > groundY + hashRegistryBound + 0.001) {
                     return Double.POSITIVE_INFINITY;
                  }

                  if (groundY - mutableHash > maxMaxDrop + 0.001) {
                     return Double.NaN;
                  }

                  int max7 = Math.max(i + 1, (int)Math.floor(mutableHash + 0.001));
                  int hashRegistryLimit = (int)Math.floor(mutableHash + 1.8 - 0.001);

                  for (int j = max7; j <= hashRegistryLimit; j++) {
                     if (world.hasCollision(hashRegistryValue, j, z2)) {
                        return Double.POSITIVE_INFINITY;
                     }
                  }

                  return mutableHash;
               }
            }
         }

         return Double.NaN;
      } else {
         return Double.NaN;
      }
   }

   private AStarPathfinder() {
   }

   public static boolean isHazardousCell(int hashRegistryValue, boolean hashRegistryFlag) {
      return (hashRegistryValue & 1297) != 0 || !hashRegistryFlag && (hashRegistryValue & 8) != 0;
   }

   public static AStarPathfinder.Search createSearch(
      AStarPathfinder.World world,
      int hashOpenValue,
      int hashOpenValue2,
      double hashOpenValue3,
      AStarPathfinder.Goal goal,
      AStarPathfinder.Limits limits,
      AStarPathfinder.Danger danger,
      LongPredicate longPredicate
   ) {
      return new AStarPathfinder.Search(world, hashOpenValue, hashOpenValue2, hashOpenValue3, goal, limits, danger, longPredicate);
   }

   public static long packIntPair(int hashRegistryValue, int hashRegistryValue2) {
      return (long)hashRegistryValue << 32 | hashRegistryValue2 & 4294967295L;
   }

   public static AStarPathfinder.Result findPath(
      AStarPathfinder.World world,
      int hashRegistryValue,
      int hashRegistryValue2,
      double hashRegistryValue3,
      AStarPathfinder.Goal goal,
      AStarPathfinder.Limits limits,
      AStarPathfinder.Danger danger,
      LongPredicate longPredicate
   ) {
      AStarPathfinder.Search hashOpen = createSearch(world, hashRegistryValue, hashRegistryValue2, hashRegistryValue3, goal, limits, danger, longPredicate);
      hashOpen.runIterations(Integer.MAX_VALUE);
      return hashOpen.getResult();
   }

   public interface Danger {
      AStarPathfinder.Danger INSTANCE = (dangerValue, dangerValue2, dangerValue3) -> 0.0F;

      float hpAt(double hpAtValue, double hashRegistryValue, double hpAtValue3);
   }

   public interface Goal {
      default double getClosenessCost(int goalValue, double hashRegistryValue, int z2) {
         return this.getHeuristicCost(goalValue, hashRegistryValue, z2);
      }

      double getHeuristicCost(int sqrtVecValue, double hashRegistryValue, int z2);

      double getArrivalCost(int vecValue, double vecValue2, int vecValue3);

      boolean isGoalReached(int goalValue, double groundY, int z2);
   }

   public record Limits(
      int maxDrop,
      float hpBudget,
      float fallEpf,
      int resistanceAmp,
      boolean fireResistant,
      boolean gaps,
      boolean sprintGaps,
      double ticksPerHp,
      int radiusXz,
      int radiusY,
      int maxExpanded
   ) {
   }

   public static enum Move {
      START,
      WALK,
      DIAG,
      STEP_UP,
      JUMP_UP,
      DROP,
      GAP1,
      GAP2;
   }

   public record Node(int x, int y, int z, double feetY, AStarPathfinder.Move move, float hp, double g) {
      public double centerX() {
         return this.x + 0.5;
      }

      public double centerZ() {
         return this.z + 0.5;
      }
   }

   public record Result(AStarPathfinder.Status status, List<AStarPathfinder.Node> nodes, double ticks, float hp, int expanded) {
   }

   public static final class Search implements AStarPathfinder.World {
      private final AStarPathfinder.World world;
      private boolean[] nodeClosed;
      private int bestReachedIndex;
      private float[] nodeHpLoss;
      private int heapPopCount;
      private int[] heapNodeIds;
      private int[] nodeXCoords;
      private final AStarPathfinder.Limits limits;
      private final Long2DoubleOpenHashMap collisionTopCache;
      private final AStarPathfinder.Danger danger;
      private AStarPathfinder.Status status;
      private final int originZ;
      private double[] heapPriorities;
      private int dangerQueriesLeft;
      private AStarPathfinder.Result result;
      private final AStarPathfinder.Goal goal;
      private double[] nodeFeetYs;
      private double[] nodeHeuristics;
      private double[] nodeGoalCosts;
      private int[] nodeParents;
      private int closestNodeIndex;
      private final LongPredicate penalizedPosPredicate;
      private final Long2FloatOpenHashMap dangerHpCache;
      private boolean[] nodeFireExposed;
      private int[] heapInsertionOrder;
      private int[] nodeYCoords;
      private int nodeCount;
      private int heapSize;
      private final int originY;
      private int[] nodeZCoords;
      private int expandedCount;
      private final Long2IntOpenHashMap nodeIndexByKey;
      private final Long2IntOpenHashMap blockFlagsCache;
      private double[] nodeCosts;
      private double closestNodeCost;
      private final Long2DoubleOpenHashMap cellPenaltyCache;
      private final double ticksPerHp;
      private final int originX;
      private final double[] neighborHeights;
      private int nodeInsertionCounter;
      private double[] heapTieBreakers;
      private byte[] nodeMoveOrdinals;
      private final Long2ByteOpenHashMap collisionCache = new Long2ByteOpenHashMap(4096);

      private int createNode(int apiPutValue, int blockY, int z2, double hashRegistryValue, long packPosKey) {
         if (this.nodeCount == this.nodeXCoords.length) {
            this.growNodeArrays(this.nodeCount * 2);
         }

         int index = this.nodeCount++;
         this.nodeXCoords[index] = apiPutValue;
         this.nodeYCoords[index] = blockY;
         this.nodeZCoords[index] = z2;
         this.nodeFeetYs[index] = hashRegistryValue;
         this.nodeCosts[index] = Double.MAX_VALUE;
         double apiPutBound = this.goal == null ? 0.0 : this.goal.getHeuristicCost(apiPutValue, hashRegistryValue, z2);
         this.nodeHeuristics[index] = Double.isFinite(apiPutBound) ? Math.max(0.0, apiPutBound) : 0.0;
         this.nodeGoalCosts[index] = 0.0;
         this.nodeParents[index] = -1;
         this.nodeIndexByKey.put(packPosKey, index);
         return index;
      }

      private boolean isStandable(int hashRegistryValue, int z2, double gapDropBound) {
         if (this.isColumnBlocked(hashRegistryValue, z2, gapDropBound, gapDropBound + 1.8)) {
            return false;
         } else {
            int hashPut = this.getBlockFlags(hashRegistryValue, AStarPathfinder.toBlockY(gapDropBound), z2);
            return (hashPut & 1311) == 0;
         }
      }

      @Override
      public double getCollisionTopY(int mutableHashValue, int i, int z2) {
         long packPosKey = AStarPathfinder.packPosKey(mutableHashValue, i, z2);
         double long2doubleopenhashmap = this.collisionTopCache.get(packPosKey);
         if (long2doubleopenhashmap == Double.NEGATIVE_INFINITY) {
            long2doubleopenhashmap = this.world.getCollisionTopY(mutableHashValue, i, z2);
            this.collisionTopCache.put(packPosKey, long2doubleopenhashmap);
         }

         return long2doubleopenhashmap;
      }

      private void tryExpandGapJump(int index, int k, int gapDropLimit, double gapDropValue) {
         int gapDropValue2 = this.nodeXCoords[index];
         int gapDropValue3 = this.nodeZCoords[index];
         double gapDropBound = this.nodeFeetYs[index];
         if (gapDropLimit > 1) {
            int index3 = this.nodeParents[index];
            if (index3 < 0 || this.nodeXCoords[index3] != gapDropValue2 - AStarPathfinder.CARDINAL_DX[k] || this.nodeZCoords[index3] != gapDropValue3 - AStarPathfinder.CARDINAL_DZ[k]) {
               return;
            }
         }

         double gapDropValue4 = gapDropBound + 3.05;
         if (!this.isColumnBlocked(gapDropValue2, gapDropValue3, gapDropBound, gapDropValue4)) {
            for (int j = 1; j <= gapDropLimit; j++) {
               int hashRegistryValue = gapDropValue2 + AStarPathfinder.CARDINAL_DX[k] * j;
               int gapDropValue6 = gapDropValue3 + AStarPathfinder.CARDINAL_DZ[k] * j;
               if (j > 1) {
                  double groundY = AStarPathfinder.findGroundY(this, hashRegistryValue, gapDropValue6, gapDropBound, 1.25, Math.max(0, this.limits.maxDrop()));
                  if (groundY == Double.POSITIVE_INFINITY || !this.isLandingSafe(hashRegistryValue, gapDropValue6, groundY, gapDropBound)) {
                     return;
                  }
               }

               if (this.isColumnBlocked(hashRegistryValue, gapDropValue6, gapDropBound, gapDropValue4)) {
                  return;
               }
            }

            int gapDropValue8 = gapDropValue2 + AStarPathfinder.CARDINAL_DX[k] * (gapDropLimit + 1);
            int z2 = gapDropValue3 + AStarPathfinder.CARDINAL_DZ[k] * (gapDropLimit + 1);
            double groundY2 = AStarPathfinder.findGroundY(this, gapDropValue8, z2, gapDropBound, 0.6, 2);
            if (Double.isFinite(groundY2)) {
               double deltaVar = groundY2 - gapDropBound;
               double gapDropValue10 = gapDropLimit == 1 ? 0.6 : 0.0;
               if (!(deltaVar < -1.501) && !(deltaVar > gapDropValue10 + 0.001)) {
                  if (!this.isColumnBlocked(gapDropValue8, z2, Math.max(gapDropBound, groundY2), gapDropValue4)) {
                     AStarPathfinder.Move move = gapDropLimit == 1 ? AStarPathfinder.Move.GAP1 : AStarPathfinder.Move.GAP2;
                     double gapDropValue11 = (gapDropLimit + 1) * 3.6 + (gapDropLimit == 1 ? 3.0 : 5.0) + gapDropValue;
                     this.relaxNeighbor(index, gapDropValue8, groundY2, z2, move, gapDropValue11, 0.0F);
                  }
               }
            }
         }
      }

      public boolean runIterations(int reachedApiLimit) {
         if (this.status != AStarPathfinder.Status.RUNNING) {
            return true;
         } else {
            int reachedApiCounter = 0;

            while (reachedApiCounter < reachedApiLimit) {
               if (this.heapSize == 0) {
                  this.completeSearch(AStarPathfinder.Status.EXHAUSTED, this.closestNodeIndex);
                  return true;
               }

               int value = this.heapNodeIds[0];
               double reachedApiValue = this.heapPriorities[0];
               this.popHeapRoot();
               reachedApiCounter++;
               this.heapPopCount++;
               if (value < 0) {
                  int index = -value - 1;
                  if (reachedApiValue == this.nodeCosts[index] + this.nodeGoalCosts[index]) {
                     this.completeSearch(AStarPathfinder.Status.REACHED, index);
                     return true;
                  }
               } else if (!this.nodeClosed[value] && reachedApiValue == this.nodeCosts[value] + this.nodeHeuristics[value]) {
                  if (this.expandedCount >= this.limits.maxExpanded()) {
                     if (this.bestReachedIndex >= 0) {
                        this.completeSearch(AStarPathfinder.Status.REACHED, this.bestReachedIndex);
                     } else {
                        this.completeSearch(AStarPathfinder.Status.PARTIAL, this.closestNodeIndex);
                     }

                     return true;
                  }

                  this.nodeClosed[value] = true;
                  this.expandedCount++;
                  double amount = this.goal.getClosenessCost(this.nodeXCoords[value], this.nodeFeetYs[value], this.nodeZCoords[value]);
                  if (!Double.isFinite(amount)) {
                     amount = this.nodeHeuristics[value];
                  }

                  if (this.closestNodeIndex < 0
                     || amount < this.closestNodeCost - 1.0E-9
                     || Math.abs(amount - this.closestNodeCost) <= 1.0E-9 && this.nodeCosts[value] < this.nodeCosts[this.closestNodeIndex]) {
                     this.closestNodeIndex = value;
                     this.closestNodeCost = amount;
                  }

                  if (this.goal.isGoalReached(this.nodeXCoords[value], this.nodeFeetYs[value], this.nodeZCoords[value])) {
                     double vec = this.goal.getArrivalCost(this.nodeXCoords[value], this.nodeFeetYs[value], this.nodeZCoords[value]);
                     this.nodeGoalCosts[value] = Double.isFinite(vec) ? Math.max(0.0, vec) : 0.0;
                     double reachedApiValue2 = this.nodeCosts[value] + this.nodeGoalCosts[value];
                     if (this.bestReachedIndex < 0 || reachedApiValue2 < this.nodeCosts[this.bestReachedIndex] + this.nodeGoalCosts[this.bestReachedIndex]) {
                        this.bestReachedIndex = value;
                     }

                     this.pushHeapEntry(-value - 1, reachedApiValue2, -1.0);
                  }

                  this.expandNeighbors(value);
               }
            }

            return false;
         }
      }

      private void completeSearch(AStarPathfinder.Status partialReachedType, int index) {
         this.status = partialReachedType;
         if (index < 0) {
            this.result = new AStarPathfinder.Result(partialReachedType, List.of(), 0.0, 0.0F, this.expandedCount);
         } else {
            List copyOf = this.buildPath(index);
            float partialReachedTotal = 0.0F;

            for (AStarPathfinder.Node node : (Iterable<com.corz.client.utils.AStarPathfinder.Node>) (copyOf)) {
               partialReachedTotal += node.hp();
            }

            this.result = new AStarPathfinder.Result(partialReachedType, copyOf, this.nodeCosts[index], partialReachedTotal, this.expandedCount);
         }
      }

      private void swapHeapEntries(int index, int index2) {
         int hashRegistryValue = this.heapNodeIds[index];
         this.heapNodeIds[index] = this.heapNodeIds[index2];
         this.heapNodeIds[index2] = hashRegistryValue;
         int hashRegistryValue2 = this.heapInsertionOrder[index];
         this.heapInsertionOrder[index] = this.heapInsertionOrder[index2];
         this.heapInsertionOrder[index2] = hashRegistryValue2;
         double hashRegistryValue3 = this.heapPriorities[index];
         this.heapPriorities[index] = this.heapPriorities[index2];
         this.heapPriorities[index2] = hashRegistryValue3;
         double hashRegistryValue4 = this.heapTieBreakers[index];
         this.heapTieBreakers[index] = this.heapTieBreakers[index2];
         this.heapTieBreakers[index2] = hashRegistryValue4;
      }

      @Override
      public boolean hasCollision(int mutableByteValue, int i, int z2) {
         long packPosKey = AStarPathfinder.packPosKey(mutableByteValue, i, z2);
         int long2byteopenhashmap = this.collisionCache.get(packPosKey);
         if (long2byteopenhashmap == 0) {
            long2byteopenhashmap = this.world.hasCollision(mutableByteValue, i, z2) ? 2 : 1;
            this.collisionCache.put(packPosKey, (byte)long2byteopenhashmap);
         }

         return long2byteopenhashmap == 2;
      }

      public int getExpandedCount() {
         return this.expandedCount;
      }

      public int getHeapPopCount() {
         return this.heapPopCount;
      }

      private void growNodeArrays(int hashRegistryValue) {
         this.nodeXCoords = Arrays.copyOf(this.nodeXCoords, hashRegistryValue);
         this.nodeYCoords = Arrays.copyOf(this.nodeYCoords, hashRegistryValue);
         this.nodeZCoords = Arrays.copyOf(this.nodeZCoords, hashRegistryValue);
         this.nodeParents = Arrays.copyOf(this.nodeParents, hashRegistryValue);
         this.nodeFeetYs = Arrays.copyOf(this.nodeFeetYs, hashRegistryValue);
         this.nodeCosts = Arrays.copyOf(this.nodeCosts, hashRegistryValue);
         this.nodeHeuristics = Arrays.copyOf(this.nodeHeuristics, hashRegistryValue);
         this.nodeGoalCosts = Arrays.copyOf(this.nodeGoalCosts, hashRegistryValue);
         this.nodeHpLoss = Arrays.copyOf(this.nodeHpLoss, hashRegistryValue);
         this.nodeMoveOrdinals = Arrays.copyOf(this.nodeMoveOrdinals, hashRegistryValue);
         this.nodeClosed = Arrays.copyOf(this.nodeClosed, hashRegistryValue);
         this.nodeFireExposed = Arrays.copyOf(this.nodeFireExposed, hashRegistryValue);
      }

      private boolean hasJumpClearance(int hashRegistryValue, double hashRegistryValue2, int z2) {
         int blockY = AStarPathfinder.toBlockY(hashRegistryValue2);
         return this.hasCollision(hashRegistryValue, blockY + 2, z2) ? false : !(hashRegistryValue2 - Math.floor(hashRegistryValue2) > 0.201) || !this.hasCollision(hashRegistryValue, blockY + 3, z2);
      }

      public AStarPathfinder.Status getStatus() {
         return this.status;
      }

      private double getCellPenalty(int predicateHashValue, int blockY, int z2, double hashRegistryValue, long packPosKey, int hashPut) {
         double long2doubleopenhashmap = this.cellPenaltyCache.get(packPosKey);
         if (!Double.isNaN(long2doubleopenhashmap)) {
            return long2doubleopenhashmap;
         } else {
            double predicateHashTotal = 0.0;
            if ((hashPut & 4) != 0) {
               predicateHashTotal += 20.0;
            }

            if ((hashPut & 512) != 0) {
               predicateHashTotal += 12.0;
            }

            if ((hashPut & 1048576) != 0) {
               predicateHashTotal += 20.0;
            }

            if ((hashPut & 32) != 0) {
               predicateHashTotal += 6.5;
            }

            for (int i = 0; i < 4; i++) {
               if ((this.getBlockFlags(predicateHashValue + AStarPathfinder.CARDINAL_DX[i], blockY, z2 + AStarPathfinder.CARDINAL_DZ[i]) & 1) != 0) {
                  predicateHashTotal += 30.0;
                  break;
               }
            }

            if (this.penalizedPosPredicate != null && this.penalizedPosPredicate.test(packPosKey)) {
               predicateHashTotal += 40.0;
            }

            double predicateHash = predicateHashTotal + this.getDangerPenalty(predicateHashValue, hashRegistryValue, z2, packPosKey);
            this.cellPenaltyCache.put(packPosKey, predicateHash);
            return predicateHash;
         }
      }

      private void expandNeighbors(int index) {
         int gapsValue = this.nodeXCoords[index];
         int z2 = this.nodeZCoords[index];
         double gapsBound = this.nodeFeetYs[index];
         int maxMaxDrop = Math.max(0, this.limits.maxDrop());
         double gapDropValue = 0.0;

         for (int j = 0; j < 4; j++) {
            this.neighborHeights[j] = AStarPathfinder.findGroundY(
               this, gapsValue + AStarPathfinder.CARDINAL_DX[j], z2 + AStarPathfinder.CARDINAL_DZ[j], gapsBound, 1.25, maxMaxDrop
            );
            if (Double.isNaN(this.neighborHeights[j])) {
               gapDropValue = 2.0;
            }
         }

         for (int k = 0; k < 4; k++) {
            int resistantFireValue = gapsValue + AStarPathfinder.CARDINAL_DX[k];
            int gapsValue5 = z2 + AStarPathfinder.CARDINAL_DZ[k];
            double gapsValue6 = this.neighborHeights[k];
            if (gapsValue6 != Double.POSITIVE_INFINITY) {
               if (this.limits.gaps() && this.isLandingSafe(resistantFireValue, gapsValue5, gapsValue6, gapsBound)) {
                  this.tryExpandGapJump(index, k, 1, gapDropValue);
                  if (this.limits.sprintGaps()) {
                     this.tryExpandGapJump(index, k, 2, gapDropValue);
                  }
               }

               if (!Double.isNaN(gapsValue6)) {
                  double deltaVar = gapsValue6 - gapsBound;
                  if (deltaVar > 0.601) {
                     if (deltaVar <= 1.251 && this.hasJumpClearance(gapsValue, gapsBound, z2)) {
                        this.relaxNeighbor(index, resistantFireValue, gapsValue6, gapsValue5, AStarPathfinder.Move.JUMP_UP, 7.6 + gapDropValue, 0.0F);
                     }
                  } else if (deltaVar >= -0.601) {
                     boolean gapsFlag = deltaVar > 0.001;
                     this.relaxNeighbor(
                        index, resistantFireValue, gapsValue6, gapsValue5, gapsFlag ? AStarPathfinder.Move.STEP_UP : AStarPathfinder.Move.WALK, 3.6 + (gapsFlag ? 0.6 : 0.0) + gapDropValue, 0.0F
                     );
                  } else {
                     double gapsLimit = -deltaVar;
                     if (!(gapsLimit > maxMaxDrop + 0.001)) {
                        boolean hashHuntFlag = (this.getBlockFlags(resistantFireValue, AStarPathfinder.toBlockY(gapsValue6), gapsValue5) & 32) != 0;
                        float gapsFactor = FallDamageMath.computeFallDamage(gapsLimit, this.limits.fallEpf(), this.limits.resistanceAmp(), hashHuntFlag);
                        if (FallDamageMath.isDamageWithinBudget(gapsFactor, this.limits.hpBudget())) {
                           this.relaxNeighbor(
                              index, resistantFireValue, gapsValue6, gapsValue5, AStarPathfinder.Move.DROP, 3.6 + gapsLimit * 0.8 + 1.0 + gapsFactor * this.ticksPerHp + gapDropValue, gapsFactor
                           );
                        }
                     }
                  }
               }
            }
         }

         for (int m = 0; m < 4; m++) {
            int hashRegistryValue = gapsValue + AStarPathfinder.DIAGONAL_DX[m];
            int gapsValue8 = z2 + AStarPathfinder.DIAGONAL_DZ[m];
            double groundY = AStarPathfinder.findGroundY(this, hashRegistryValue, gapsValue8, gapsBound, 0.6, 1);
            if (Double.isFinite(groundY) && !(Math.abs(groundY - gapsBound) > 0.601)) {
               double gapDropBound = Math.max(gapsBound, groundY);
               if (this.isStandable(hashRegistryValue, z2, gapDropBound) && this.isStandable(gapsValue, gapsValue8, gapDropBound)) {
                  this.relaxNeighbor(index, hashRegistryValue, groundY, gapsValue8, AStarPathfinder.Move.DIAG, 5.1 + gapDropValue, 0.0F);
               }
            }
         }
      }

      Search(
         AStarPathfinder.World api,
         int value,
         int z2,
         double hashRegistryValue,
         AStarPathfinder.Goal api2,
         AStarPathfinder.Limits data,
         AStarPathfinder.Danger danger2,
         LongPredicate longPredicate
      ) {
         this.collisionTopCache = new Long2DoubleOpenHashMap(1024);
         this.blockFlagsCache = new Long2IntOpenHashMap(1024);
         this.cellPenaltyCache = new Long2DoubleOpenHashMap(1024);
         this.dangerHpCache = new Long2FloatOpenHashMap();
         this.nodeIndexByKey = new Long2IntOpenHashMap(1024);
         this.nodeXCoords = new int[512];
         this.nodeYCoords = new int[512];
         this.nodeZCoords = new int[512];
         this.nodeParents = new int[512];
         this.nodeFeetYs = new double[512];
         this.nodeCosts = new double[512];
         this.nodeHeuristics = new double[512];
         this.nodeGoalCosts = new double[512];
         this.nodeHpLoss = new float[512];
         this.nodeMoveOrdinals = new byte[512];
         this.nodeClosed = new boolean[512];
         this.nodeFireExposed = new boolean[512];
         this.heapNodeIds = new int[512];
         this.heapInsertionOrder = new int[512];
         this.heapPriorities = new double[512];
         this.heapTieBreakers = new double[512];
         this.neighborHeights = new double[4];
         this.closestNodeIndex = -1;
         this.bestReachedIndex = -1;
         this.closestNodeCost = Double.MAX_VALUE;
         this.dangerQueriesLeft = 48;
         this.status = AStarPathfinder.Status.RUNNING;
         this.world = api;
         this.goal = api2;
         this.limits = data;
         this.danger = danger2 == null ? AStarPathfinder.Danger.INSTANCE : danger2;
         this.penalizedPosPredicate = longPredicate;
         this.originX = value;
         this.originZ = z2;
         this.originY = AStarPathfinder.toBlockY(hashRegistryValue);
         this.ticksPerHp = Double.isFinite(data.ticksPerHp()) ? Math.max(0.0, data.ticksPerHp()) : 0.0;
         this.nodeIndexByKey.defaultReturnValue(-1);
         this.blockFlagsCache.defaultReturnValue(-1);
         this.collisionTopCache.defaultReturnValue(Double.NEGATIVE_INFINITY);
         this.cellPenaltyCache.defaultReturnValue(Double.NaN);
         this.dangerHpCache.defaultReturnValue(Float.NaN);
         if (api != null && api2 != null && Double.isFinite(hashRegistryValue)) {
            int apiPut = this.createNode(value, this.originY, z2, hashRegistryValue, AStarPathfinder.packPosKey(value, this.originY, z2));
            this.nodeCosts[apiPut] = 0.0;
            this.nodeParents[apiPut] = -1;
            this.nodeMoveOrdinals[apiPut] = (byte)AStarPathfinder.Move.START.ordinal();
            this.pushHeapEntry(apiPut, this.nodeCosts[apiPut] + this.nodeHeuristics[apiPut], this.nodeHeuristics[apiPut]);
         } else {
            this.status = AStarPathfinder.Status.EXHAUSTED;
            this.result = new AStarPathfinder.Result(AStarPathfinder.Status.EXHAUSTED, List.of(), 0.0, 0.0F, 0);
         }
      }

      private List<AStarPathfinder.Node> buildPath(int hashRegistryValue) {
         ArrayList arrayList = new ArrayList();
         int index = hashRegistryValue;

         while (index >= 0) {
            arrayList.add(
               new AStarPathfinder.Node(
                  this.nodeXCoords[index],
                  this.nodeYCoords[index],
                  this.nodeZCoords[index],
                  this.nodeFeetYs[index],
                  AStarPathfinder.MOVE_VALUES[this.nodeMoveOrdinals[index]],
                  this.nodeHpLoss[index],
                  this.nodeCosts[index]
               )
            );
            index = this.nodeParents[index];
         }

         Collections.reverse(arrayList);
         return List.copyOf(arrayList);
      }

      private boolean isHeapEntryLess(int index, int hashRegistryValue) {
         if (this.heapPriorities[index] != this.heapPriorities[hashRegistryValue]) {
            return this.heapPriorities[index] < this.heapPriorities[hashRegistryValue];
         } else {
            return this.heapTieBreakers[index] != this.heapTieBreakers[hashRegistryValue] ? this.heapTieBreakers[index] < this.heapTieBreakers[hashRegistryValue] : this.heapInsertionOrder[index] < this.heapInsertionOrder[hashRegistryValue];
         }
      }

      private boolean isColumnBlocked(int hashRegistryValue, int z2, double gapDropBound, double hashRegistryValue4) {
         int floor5 = (int)Math.floor(gapDropBound + 0.001);
         int hashRegistryLimit = (int)Math.floor(hashRegistryValue4 - 0.001);

         for (int i = floor5; i <= hashRegistryLimit; i++) {
            if (this.hasCollision(hashRegistryValue, i, z2)) {
               double mutableHash = this.getCollisionTopY(hashRegistryValue, i, z2);
               if (Double.isNaN(mutableHash) || mutableHash > gapDropBound + 0.001) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean isLandingSafe(int resistantFireValue, int z2, double hashRegistryValue, double gapDropBound) {
         if (Double.isNaN(hashRegistryValue)) {
            return true;
         } else if (!Double.isFinite(hashRegistryValue)) {
            return false;
         } else if (hashRegistryValue < gapDropBound - 1.5 - 0.001) {
            return true;
         } else {
            int hashPut = this.getBlockFlags(resistantFireValue, AStarPathfinder.toBlockY(hashRegistryValue), z2);
            return (hashPut & 1024) != 0 ? false : AStarPathfinder.isHazardousCell(hashPut, this.limits.fireResistant());
         }
      }

      @Override
      public int getBottomY() {
         return this.world.getBottomY();
      }

      @Override
      public int getBlockFlags(int hashPutValue, int blockY, int z2) {
         long packPosKey = AStarPathfinder.packPosKey(hashPutValue, blockY, z2);
         int long2intopenhashmap = this.blockFlagsCache.get(packPosKey);
         if (long2intopenhashmap < 0) {
            long2intopenhashmap = Math.max(0, this.world.getBlockFlags(hashPutValue, blockY, z2));
            this.blockFlagsCache.put(packPosKey, long2intopenhashmap);
         }

         return long2intopenhashmap;
      }

      private void relaxNeighbor(int index, int radiusResistantValue, double hashRegistryValue, int z2, AStarPathfinder.Move move, double radiusResistantValue4, float gapsFactor) {
         if (Math.abs(radiusResistantValue - this.originX) <= this.limits.radiusXz() && Math.abs(z2 - this.originZ) <= this.limits.radiusXz()) {
            int blockY = AStarPathfinder.toBlockY(hashRegistryValue);
            if (Math.abs(blockY - this.originY) <= this.limits.radiusY()) {
               int hashPut = this.getBlockFlags(radiusResistantValue, blockY, z2);
               if (!AStarPathfinder.isHazardousCell(hashPut, this.limits.fireResistant())) {
                  long packPosKey = AStarPathfinder.packPosKey(radiusResistantValue, blockY, z2);
                  double radiusResistantTotal = radiusResistantValue4 + this.getCellPenalty(radiusResistantValue, blockY, z2, hashRegistryValue, packPosKey, hashPut);
                  float gapsFactor2 = gapsFactor;
                  boolean radiusResistantFlag = this.nodeFireExposed[index];
                  if ((hashPut & 2) != 0) {
                     radiusResistantTotal += 6.0;
                     if (!this.limits.fireResistant() && !radiusResistantFlag) {
                        radiusResistantTotal += 3.2F * this.ticksPerHp;
                        gapsFactor2 = gapsFactor + 3.2F;
                     }

                     radiusResistantFlag = true;
                  }

                  double radiusResistantLimit = this.nodeCosts[index] + radiusResistantTotal;
                  int long2intopenhashmap = this.nodeIndexByKey.get(packPosKey);
                  if (long2intopenhashmap < 0) {
                     long2intopenhashmap = this.createNode(radiusResistantValue, blockY, z2, hashRegistryValue, packPosKey);
                  } else if (radiusResistantLimit >= this.nodeCosts[long2intopenhashmap] - 1.0E-9) {
                     return;
                  }

                  this.nodeCosts[long2intopenhashmap] = radiusResistantLimit;
                  this.nodeFeetYs[long2intopenhashmap] = hashRegistryValue;
                  this.nodeParents[long2intopenhashmap] = index;
                  this.nodeMoveOrdinals[long2intopenhashmap] = (byte)move.ordinal();
                  this.nodeHpLoss[long2intopenhashmap] = gapsFactor2;
                  this.nodeFireExposed[long2intopenhashmap] = radiusResistantFlag;
                  this.nodeClosed[long2intopenhashmap] = false;
                  this.pushHeapEntry(long2intopenhashmap, this.nodeCosts[long2intopenhashmap] + this.nodeHeuristics[long2intopenhashmap], this.nodeHeuristics[long2intopenhashmap]);
               }
            }
         }
      }

      private void popHeapRoot() {
         int index = --this.heapSize;
         if (index != 0) {
            this.heapNodeIds[0] = this.heapNodeIds[index];
            this.heapInsertionOrder[0] = this.heapInsertionOrder[index];
            this.heapPriorities[0] = this.heapPriorities[index];
            this.heapTieBreakers[0] = this.heapTieBreakers[index];
            int hashRegistryValue = 0;

            while (true) {
               int hashRegistryValue2 = 2 * hashRegistryValue + 1;
               int hashRegistryValue3 = hashRegistryValue2 + 1;
               int hashRegistryValue4 = hashRegistryValue;
               if (hashRegistryValue2 < this.heapSize && this.isHeapEntryLess(hashRegistryValue2, hashRegistryValue)) {
                  hashRegistryValue4 = hashRegistryValue2;
               }

               if (hashRegistryValue3 < this.heapSize && this.isHeapEntryLess(hashRegistryValue3, hashRegistryValue4)) {
                  hashRegistryValue4 = hashRegistryValue3;
               }

               if (hashRegistryValue4 == hashRegistryValue) {
                  return;
               }

               this.swapHeapEntries(hashRegistryValue, hashRegistryValue4);
               hashRegistryValue = hashRegistryValue4;
            }
         }
      }

      public AStarPathfinder.Result getResult() {
         return this.result;
      }

      private void pushHeapEntry(int hashRegistryValue, double hashRegistryValue2, double hashRegistryValue3) {
         if (this.heapSize == this.heapNodeIds.length) {
            int hashRegistryValue4 = this.heapSize * 2;
            this.heapNodeIds = Arrays.copyOf(this.heapNodeIds, hashRegistryValue4);
            this.heapInsertionOrder = Arrays.copyOf(this.heapInsertionOrder, hashRegistryValue4);
            this.heapPriorities = Arrays.copyOf(this.heapPriorities, hashRegistryValue4);
            this.heapTieBreakers = Arrays.copyOf(this.heapTieBreakers, hashRegistryValue4);
         }

         int index = this.heapSize++;
         this.heapNodeIds[index] = hashRegistryValue;
         this.heapInsertionOrder[index] = this.nodeInsertionCounter++;
         this.heapPriorities[index] = hashRegistryValue2;
         this.heapTieBreakers[index] = hashRegistryValue3;

         while (index > 0) {
            int hashRegistryValue5 = index - 1 >>> 1;
            if (!this.isHeapEntryLess(index, hashRegistryValue5)) {
               break;
            }

            this.swapHeapEntries(index, hashRegistryValue5);
            index = hashRegistryValue5;
         }
      }

      private double getDangerPenalty(int hashValue, double hashRegistryValue, int z2, long packPosKey) {
         if (this.danger == AStarPathfinder.Danger.INSTANCE) {
            return 0.0;
         } else {
            float long2floatopenhashmap = this.dangerHpCache.get(packPosKey);
            if (Float.isNaN(long2floatopenhashmap)) {
               if (this.dangerQueriesLeft <= 0) {
                  return 0.0;
               }

               if (this.goal.getHeuristicCost(hashValue, hashRegistryValue, z2) > 21.6) {
                  return 0.0;
               }

               this.dangerQueriesLeft--;
               long2floatopenhashmap = this.danger.hpAt(hashValue + 0.5, hashRegistryValue, z2 + 0.5);
               if (!Float.isFinite(long2floatopenhashmap)) {
                  long2floatopenhashmap = 0.0F;
               }

               this.dangerHpCache.put(packPosKey, long2floatopenhashmap);
            }

            return Math.max(0.0, long2floatopenhashmap - 0.5) * this.ticksPerHp;
         }
      }
   }

   public static enum Status {
      RUNNING,
      REACHED,
      PARTIAL,
      EXHAUSTED;
   }

   public interface World {
      int getBlockFlags(int hashPutValue, int blockY, int z2);

      boolean hasCollision(int mutableByteValue, int i, int z2);

      int getBottomY();

      double getCollisionTopY(int mutableHashValue, int i, int z2);
   }
}
