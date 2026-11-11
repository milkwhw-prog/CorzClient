package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class FloorBuildPlanner {
   private static final int[][] NEIGHBOR_OFFSETS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

   public static List<FloorBuildPlanner.Pick> pickBestStandCells(
      FloorBuildPlanner.Deck deck,
      int standCellValue,
      int j,
      Set<Long> set,
      Predicate<FloorBuildPlanner.Pick> predicate,
      BiPredicate<Long, Long> biPredicate
   ) {
      FloorBuildPlanner.Search candidatesCandidate = searchDistancesFrom(deck, ProofSupportUtils.getTime(standCellValue, deck.layerY + 1, j), biPredicate);
      var arrayList = new ArrayList();

      for (List list : deck.frontiersByCell.values()) {
         for (FloorBuildPlanner.Frontier frontier : (Iterable<com.corz.client.utils.FloorBuildPlanner.Frontier>) (list)) {
            int orDefault = candidatesCandidate.getDistance(frontier.stand());
            if (orDefault > 0 && !set.contains(frontier.stand())) {
               arrayList.add(
                  new FloorBuildPlanner.Pick(
                     frontier.cell(),
                     frontier.stand(),
                     frontier.pose(),
                     orDefault,
                     ProofSupportUtils.getValueInt(frontier.cell()) - standCellValue,
                     ProofSupportUtils.getValueForLong(frontier.cell()) - j
                  )
               );
            }
         }
      }

      arrayList.sort(
         Comparator.comparingInt(FloorBuildPlanner.Pick::standDist)
            .thenComparingLong(FloorBuildPlanner.Pick::stand)
            .thenComparingLong(FloorBuildPlanner.Pick::cell)
            .thenComparing(FloorBuildPlanner.Pick::pose)
      );
      var arrayList2 = new ArrayList();
      HashMap<Long, Set<Long>> hashMap = new HashMap<>();
      int standCellBound = Integer.MAX_VALUE;
      var hashSet = new HashSet();

      for (FloorBuildPlanner.Pick pick : (Iterable<com.corz.client.utils.FloorBuildPlanner.Pick>) (arrayList)) {
         int standCellLimit = bucketDistance(pick.standDist());
         if (standCellLimit > 2 && standCellBound <= 2) {
            break;
         }

         if (hashSet.add(pick.cell() ^ Long.rotateLeft(pick.stand(), 29)) && predicate.test(pick)) {
            standCellBound = Math.min(standCellBound, standCellLimit);
            arrayList2.add(pick);
            hashMap.computeIfAbsent(pick.stand(), long2 -> new HashSet<>()).add(pick.cell());
         }
      }

      int standCellBound3 = standCellBound;
      arrayList2.removeIf(bestTier -> bucketDistance(((com.corz.client.utils.FloorBuildPlanner.Pick) bestTier).standDist()) != standCellBound3);
      arrayList2.sort(
         Comparator.<FloorBuildPlanner.Pick>comparingDouble(pick2 -> -((Set)hashMap.get(pick2.stand())).size() / (1.0 + pick2.standDist()))
            .thenComparingInt(FloorBuildPlanner.Pick::standDist)
            .thenComparingLong(FloorBuildPlanner.Pick::stand)
            .thenComparingLong(FloorBuildPlanner.Pick::cell)
            .thenComparing(FloorBuildPlanner.Pick::pose)
      );
      return arrayList2;
   }

   public static boolean isFloorCellBuildable(FloorBuildPlanner.Probe probe, int remainingCandidatesValue, int deckSupersedesValue, int j) {
      return probe.isSchematicTargetAt(remainingCandidatesValue, deckSupersedesValue, j) && probe.isTargetBlockPlacedAt(remainingCandidatesValue, deckSupersedesValue, j) && probe.isCollidableAt(remainingCandidatesValue, deckSupersedesValue, j) && hasHeadroom(probe, remainingCandidatesValue, deckSupersedesValue, j);
   }

   public static int bucketDistance(int remainingCandidatesValue) {
      return remainingCandidatesValue == 0 ? 0 : (remainingCandidatesValue <= 3 ? 1 : (remainingCandidatesValue <= 8 ? 2 : 3));
   }

   static Map<Long, List<FloorBuildPlanner.Frontier>> computeFrontiers(FloorBuildPlanner.Probe probe, int aboveDiagonalValue, Set<Long> set, Set<Long> set2) {
      var hashMap = new HashMap();
      var hashSet = new HashSet();

      for (long proofSupportValue : set) {
         int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
         int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);

         for (int[] aboveDiagonalValues : NEIGHBOR_OFFSETS) {
            int aboveDiagonalValue5 = valueInt + aboveDiagonalValues[0];
            int aboveDiagonalValue6 = valueForLong + aboveDiagonalValues[1];
            long time7 = ProofSupportUtils.getTime(aboveDiagonalValue5, aboveDiagonalValue, aboveDiagonalValue6);
            if (!set.contains(time7) && hashSet.add(time7) && probe.isSchematicTargetAt(aboveDiagonalValue5, aboveDiagonalValue, aboveDiagonalValue6) && probe.isPlaceableMissingBlockAt(aboveDiagonalValue5, aboveDiagonalValue, aboveDiagonalValue6)) {
               var arrayList = new ArrayList(4);
               boolean collisionInstance = probe.isCollidableAt(aboveDiagonalValue5, aboveDiagonalValue - 1, aboveDiagonalValue6);

               for (int[] neighborOffsetsItem : NEIGHBOR_OFFSETS) {
                  int aboveDiagonalValue8 = aboveDiagonalValue5 + neighborOffsetsItem[0];
                  int aboveDiagonalValue9 = aboveDiagonalValue6 + neighborOffsetsItem[1];
                  if (set.contains(ProofSupportUtils.getTime(aboveDiagonalValue8, aboveDiagonalValue, aboveDiagonalValue9))) {
                     int deltaVar = aboveDiagonalValue5 - aboveDiagonalValue8;
                     int deltaVar2 = aboveDiagonalValue6 - aboveDiagonalValue9;
                     arrayList.add(
                        new FloorBuildPlanner.Frontier(
                           time7, ProofSupportUtils.getTime(aboveDiagonalValue8, aboveDiagonalValue + 1, aboveDiagonalValue9), FloorBuildPlanner.Pose.EDGE, deltaVar, deltaVar2
                        )
                     );

                     for (int i = aboveDiagonalValue5 - 2; i <= aboveDiagonalValue5 + 2; i++) {
                        for (int j = aboveDiagonalValue6 - 2; j <= aboveDiagonalValue6 + 2; j++) {
                           if ((i != aboveDiagonalValue8 || j != aboveDiagonalValue9) && (i != aboveDiagonalValue5 || j != aboveDiagonalValue6)) {
                              long goodsSlot = ProofSupportUtils.getTime(i, aboveDiagonalValue + 1, j);
                              if (set2.contains(goodsSlot) && (i - aboveDiagonalValue8) * deltaVar + (j - aboveDiagonalValue9) * deltaVar2 > 0) {
                                 arrayList.add(new FloorBuildPlanner.Frontier(time7, goodsSlot, FloorBuildPlanner.Pose.DIAGONAL, deltaVar, deltaVar2));
                              }
                           }
                        }
                     }
                  }
               }

               if (collisionInstance) {
                  for (int k = aboveDiagonalValue5 - 2; k <= aboveDiagonalValue5 + 2; k++) {
                     for (int m = aboveDiagonalValue6 - 2; m <= aboveDiagonalValue6 + 2; m++) {
                        long time11 = ProofSupportUtils.getTime(k, aboveDiagonalValue + 1, m);
                        if (set2.contains(time11)) {
                           arrayList.add(
                              new FloorBuildPlanner.Frontier(time7, time11, FloorBuildPlanner.Pose.FROM_ABOVE, aboveDiagonalValue5 - k, aboveDiagonalValue6 - m)
                           );
                        }
                     }
                  }
               }

               if (!arrayList.isEmpty()) {
                  hashMap.put(time7, arrayList);
               }
            }
         }
      }

      var hashSet2 = new HashSet(hashMap.keySet());
      int[][] ints = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

      for (long aboveDiagonalValue12 : set) {
         int valueInt13 = ProofSupportUtils.getValueInt(aboveDiagonalValue12);
         int valueForLong14 = ProofSupportUtils.getValueForLong(aboveDiagonalValue12);

         for (int[] intsItem : ints) {
            int aboveDiagonalValue15 = valueInt13 + intsItem[0];
            int aboveDiagonalValue16 = valueForLong14 + intsItem[1];
            long time17 = ProofSupportUtils.getTime(aboveDiagonalValue15, aboveDiagonalValue, aboveDiagonalValue16);
            if (!set.contains(time17)
               && hashSet.add(time17)
               && probe.isSchematicTargetAt(aboveDiagonalValue15, aboveDiagonalValue, aboveDiagonalValue16)
               && probe.isPlaceableMissingBlockAt(aboveDiagonalValue15, aboveDiagonalValue, aboveDiagonalValue16)
               && probe.isCollidableAt(aboveDiagonalValue15, aboveDiagonalValue - 1, aboveDiagonalValue16)) {
               boolean aboveDiagonalFlag = false;

               for (int[] aboveDiagonalValues4 : NEIGHBOR_OFFSETS) {
                  if (hashSet2.contains(ProofSupportUtils.getTime(aboveDiagonalValue15 + aboveDiagonalValues4[0], aboveDiagonalValue, aboveDiagonalValue16 + aboveDiagonalValues4[1]))) {
                     aboveDiagonalFlag = true;
                     break;
                  }
               }

               if (!aboveDiagonalFlag) {
                  var finish = new ArrayList(4);

                  for (int q = aboveDiagonalValue15 - 2; q <= aboveDiagonalValue15 + 2; q++) {
                     for (int r = aboveDiagonalValue16 - 2; r <= aboveDiagonalValue16 + 2; r++) {
                        long time18 = ProofSupportUtils.getTime(q, aboveDiagonalValue + 1, r);
                        if (set2.contains(time18)) {
                           finish.add(
                              new FloorBuildPlanner.Frontier(time17, time18, FloorBuildPlanner.Pose.FROM_ABOVE, aboveDiagonalValue15 - q, aboveDiagonalValue16 - r)
                           );
                        }
                     }
                  }

                  if (!finish.isEmpty()) {
                     hashMap.put(time17, finish);
                  }
               }
            }
         }
      }

      return hashMap;
   }

   public static boolean areCellsConnected(FloorBuildPlanner.Deck deck, long candidatesValue, long candidatesValue2) {
      if (deck.floorCells.contains(candidatesValue) && deck.floorCells.contains(candidatesValue2)) {
         if (candidatesValue == candidatesValue2) {
            return true;
         } else {
            var hashSet = new HashSet();
            var arrayDeque = new ArrayDeque();
            hashSet.add(candidatesValue);
            arrayDeque.add(candidatesValue);

            while (!arrayDeque.isEmpty()) {
               long proofSupportValue = (Long)arrayDeque.poll();
               int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
               int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);

               for (int[] candidatesValues : NEIGHBOR_OFFSETS) {
                  long goodsSlot = ProofSupportUtils.getTime(valueInt + candidatesValues[0], deck.layerY + 1, valueForLong + candidatesValues[1]);
                  if (goodsSlot == candidatesValue2) {
                     return true;
                  }

                  if (deck.floorCells.contains(goodsSlot) && hashSet.add(goodsSlot)) {
                     arrayDeque.add(goodsSlot);
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static FloorBuildPlanner.ProbeResult scanLayerForBestDeck(FloorBuildPlanner.Probe probe, int remainingValue, int remainingMomentsValue, int remainingValue3, int remainingLimit, int remainingValue4) {
      int remainingCounter = 0;
      int remainingCounter2 = 0;
      FloorBuildPlanner.Deck deck = null;
      var hashSet = new HashSet();

      for (int i = -remainingLimit; i <= remainingLimit; i++) {
         for (int j = -remainingLimit; j <= remainingLimit; j++) {
            int autoBuilderValue = remainingMomentsValue + i;
            int remainingValue6 = remainingValue3 + j;
            if (probe.isSchematicTargetAt(autoBuilderValue, remainingValue, remainingValue6) && probe.isTargetBlockPlacedAt(autoBuilderValue, remainingValue, remainingValue6) && probe.isCollidableAt(autoBuilderValue, remainingValue, remainingValue6)) {
               remainingCounter++;
               if (hasHeadroom(probe, autoBuilderValue, remainingValue, remainingValue6)) {
                  remainingCounter2++;
                  long tickx = ProofSupportUtils.getTime(autoBuilderValue, remainingValue, remainingValue6);
                  if (!hashSet.contains(tickx)) {
                     FloorBuildPlanner.Deck remaining = floodFillDeck(probe, remainingValue, autoBuilderValue, remainingValue6, remainingValue4);
                     if (remaining != null) {
                        for (long proofSupportValue : remaining.floorCells) {
                           hashSet.add(ProofSupportUtils.getTime(ProofSupportUtils.getValueInt(proofSupportValue), remainingValue, ProofSupportUtils.getValueForLong(proofSupportValue)));
                        }

                        if (deck == null || remaining.getCellCount() > deck.getCellCount()) {
                           deck = remaining;
                        }
                     }
                  }
               }
            }
         }
      }

      return new FloorBuildPlanner.ProbeResult(
         remainingValue,
         remainingCounter,
         remainingCounter2,
         deck == null ? 0 : deck.getCellCount(),
         deck == null ? 0 : deck.getFrontierCount(),
         deck == null ? 0 : deck.connectedExtraCount,
         deck == null ? 0 : findOpenNeighborPairs(probe, deck).size(),
         deck
      );
   }

   public static List<FloorBuildPlanner.Pick> pickStandPoses(
      FloorBuildPlanner.Deck deck, int standPoseValue, int j, int standPoseValue3, int standPoseValue4, int standPoseFactor, int standPoseFactor2, Set<Long> set
   ) {
      long time5 = ProofSupportUtils.getTime(standPoseValue, deck.layerY + 1, j);
      var arrayList = new ArrayList();

      for (Entry entry : deck.frontiersByCell.entrySet()) {
         FloorBuildPlanner.Frontier frontier = null;
         int standPoseLimit = Integer.MAX_VALUE;
         int standPoseLimit2 = 9;

         for (FloorBuildPlanner.Frontier frontier2 : (Iterable<com.corz.client.utils.FloorBuildPlanner.Frontier>) ((List)entry.getValue())) {
            if (set == null || !set.contains(frontier2.stand())) {
               int standPoseValue6 = frontier2.stand() == time5
                  ? 0
                  : Math.max(Math.abs(ProofSupportUtils.getValueInt(frontier2.stand()) - standPoseValue), Math.abs(ProofSupportUtils.getValueForLong(frontier2.stand()) - j));
               int standPoseValue7 = frontier2.pose() == FloorBuildPlanner.Pose.FROM_ABOVE ? 0 : (frontier2.pose() == FloorBuildPlanner.Pose.DIAGONAL ? 1 : 2);
               if (standPoseValue6 < standPoseLimit || standPoseValue6 == standPoseLimit && standPoseValue7 < standPoseLimit2) {
                  frontier = frontier2;
                  standPoseLimit = standPoseValue6;
                  standPoseLimit2 = standPoseValue7;
               }
            }
         }

         if (frontier != null) {
            int valueInt = ProofSupportUtils.getValueInt(frontier.cell());
            int valueForLong = ProofSupportUtils.getValueForLong(frontier.cell());
            arrayList.add(new FloorBuildPlanner.Pick(frontier.cell(), frontier.stand(), frontier.pose(), standPoseLimit, valueInt - standPoseValue, valueForLong - j));
         }
      }

      boolean standPoseFlag = standPoseValue3 != Integer.MIN_VALUE;
      arrayList.sort(
         (lastDirXx, obj) -> {
            if (((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).standDist() != ((com.corz.client.utils.FloorBuildPlanner.Pick) obj).standDist()) {
               return Integer.compare(((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).standDist(), ((com.corz.client.utils.FloorBuildPlanner.Pick) obj).standDist());
            } else {
               if (standPoseFlag) {
                  int maxAbs = Math.max(
                     Math.abs(ProofSupportUtils.getValueInt(((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).cell()) - standPoseValue3), Math.abs(ProofSupportUtils.getValueForLong(((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).cell()) - standPoseValue4)
                  );
                  int var8x = Math.max(Math.abs(ProofSupportUtils.getValueInt(((com.corz.client.utils.FloorBuildPlanner.Pick) obj).cell()) - standPoseValue3), Math.abs(ProofSupportUtils.getValueForLong(((com.corz.client.utils.FloorBuildPlanner.Pick) obj).cell()) - standPoseValue4));
                  if (maxAbs != var8x) {
                     return Integer.compare(maxAbs, var8x);
                  }
               }

               int standPoseValue10 = ((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).dirX() * standPoseFactor + ((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).dirZ() * standPoseFactor2;
               int var10x = ((com.corz.client.utils.FloorBuildPlanner.Pick) obj).dirX() * standPoseFactor + ((com.corz.client.utils.FloorBuildPlanner.Pick) obj).dirZ() * standPoseFactor2;
               if (standPoseValue10 != var10x) {
                  return Integer.compare(var10x, standPoseValue10);
               } else {
                  int standPoseValue11 = Math.abs(((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).dirX()) + Math.abs(((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).dirZ());
                  int var12x = Math.abs(((com.corz.client.utils.FloorBuildPlanner.Pick) obj).dirX()) + Math.abs(((com.corz.client.utils.FloorBuildPlanner.Pick) obj).dirZ());
                  if (standPoseValue11 != var12x) {
                     return Integer.compare(standPoseValue11, var12x);
                  } else {
                     int var13x = Integer.compare(ProofSupportUtils.getValueInt(((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).cell()), ProofSupportUtils.getValueInt(((com.corz.client.utils.FloorBuildPlanner.Pick) obj).cell()));
                     return var13x != 0 ? var13x : Integer.compare(ProofSupportUtils.getValueForLong(((com.corz.client.utils.FloorBuildPlanner.Pick) lastDirXx).cell()), ProofSupportUtils.getValueForLong(((com.corz.client.utils.FloorBuildPlanner.Pick) obj).cell()));
                  }
               }
            }
         }
      );
      return arrayList;
   }

   public static FloorBuildPlanner.Deck floodFillDeck(FloorBuildPlanner.Probe probe, int remainingValue, int remainingCandidatesValue, int j, int remainingLimit) {
      if (!isFloorCellBuildable(probe, remainingCandidatesValue, remainingValue, j)) {
         return null;
      } else {
         var hashSet = new HashSet();
         var arrayDeque = new ArrayDeque();
         hashSet.add(ProofSupportUtils.getTime(remainingCandidatesValue, remainingValue, j));
         arrayDeque.add(new long[]{remainingCandidatesValue, j});

         while (!arrayDeque.isEmpty() && hashSet.size() < remainingLimit) {
            long[] remainingValues = (long[])arrayDeque.poll();

            for (int[] neighborOffsetsItem : NEIGHBOR_OFFSETS) {
               int proofSupportValue = (int)remainingValues[0] + neighborOffsetsItem[0];
               int remainingValue5 = (int)remainingValues[1] + neighborOffsetsItem[1];
               long tickx = ProofSupportUtils.getTime(proofSupportValue, remainingValue, remainingValue5);
               if (!hashSet.contains(tickx) && isFloorCellBuildable(probe, proofSupportValue, remainingValue, remainingValue5)) {
                  hashSet.add(tickx);
                  arrayDeque.add(new long[]{proofSupportValue, remainingValue5});
               }
            }
         }

         var hashSet2 = new HashSet(hashSet.size() * 2);

         for (long remainingValue7 : (Iterable<Long>) (hashSet)) {
            hashSet2.add(ProofSupportUtils.getTime(ProofSupportUtils.getValueInt(remainingValue7), remainingValue + 1, ProofSupportUtils.getValueForLong(remainingValue7)));
         }

         return new FloorBuildPlanner.Deck(remainingValue, hashSet2, computeFrontiers(probe, remainingValue, hashSet, hashSet2), countExtendableCells(probe, remainingValue, hashSet, remainingLimit));
      }
   }

   public static List<Long> getPathBetweenCells(FloorBuildPlanner.Deck deck, long remainingCandidatesValue, long cell, BiPredicate<Long, Long> biPredicate) {
      return searchDistancesFrom(deck, remainingCandidatesValue, biPredicate).getPathTo(cell);
   }

   static int countExtendableCells(FloorBuildPlanner.Probe probe, int remainingCandidatesValue, Set<Long> set, int remainingCandidatesLimit) {
      var hashSet = new HashSet(set);
      var arrayDeque = new ArrayDeque(set);
      int remainingCandidatesCounter = 0;

      while (!arrayDeque.isEmpty() && remainingCandidatesCounter < remainingCandidatesLimit) {
         long proofSupportValue = (Long)arrayDeque.poll();
         int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
         int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);

         for (int[] remainingCandidatesValues : NEIGHBOR_OFFSETS) {
            int remainingCandidatesValue5 = valueInt + remainingCandidatesValues[0];
            int j = valueForLong + remainingCandidatesValues[1];
            long tickx = ProofSupportUtils.getTime(remainingCandidatesValue5, remainingCandidatesValue, j);
            if (!hashSet.contains(tickx) && probe.isSchematicTargetAt(remainingCandidatesValue5, remainingCandidatesValue, j)) {
               boolean blockAuto = probe.isTargetBlockPlacedAt(remainingCandidatesValue5, remainingCandidatesValue, j);
               boolean remainingCandidatesFlag = !blockAuto && probe.isPlaceableMissingBlockAt(remainingCandidatesValue5, remainingCandidatesValue, j);
               if (blockAuto || remainingCandidatesFlag) {
                  hashSet.add(tickx);
                  arrayDeque.add(tickx);
                  if (remainingCandidatesFlag) {
                     remainingCandidatesCounter++;
                  }
               }
            }
         }
      }

      return remainingCandidatesCounter;
   }

   public static List<Long> sortCellKeys(Set<Long> set) {
      var arrayList = new ArrayList(set);
      Collections.sort(arrayList);
      return arrayList;
   }

   public static FloorBuildPlanner.Deck findNearbyDeck(FloorBuildPlanner.Probe probe, int remainingValue, int remainingCandidatesValue, int remainingValue3, int remainingLimit, int remainingValue4) {
      if (isFloorCellBuildable(probe, remainingCandidatesValue, remainingValue, remainingValue3)) {
         return floodFillDeck(probe, remainingValue, remainingCandidatesValue, remainingValue3, remainingValue4);
      } else {
         for (int i = 1; i <= remainingLimit; i++) {
            for (int j = -i; j <= i; j++) {
               for (int k = -i; k <= i; k++) {
                  if (Math.max(Math.abs(j), Math.abs(k)) == i) {
                     int remainingValue5 = remainingCandidatesValue + j;
                     int remainingValue6 = remainingValue3 + k;
                     if (isFloorCellBuildable(probe, remainingValue5, remainingValue, remainingValue6)) {
                        return floodFillDeck(probe, remainingValue, remainingValue5, remainingValue6, remainingValue4);
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   public static List<long[]> findOpenNeighborPairs(FloorBuildPlanner.Probe probe, FloorBuildPlanner.Deck deck) {
      var arrayList = new ArrayList();
      var hashSet = new HashSet();

      for (long candidatesCandidateValue : deck.floorCells) {
         int valueInt = ProofSupportUtils.getValueInt(candidatesCandidateValue);
         int valueForLong = ProofSupportUtils.getValueForLong(candidatesCandidateValue);

         for (int[] candidatesCandidateValues : NEIGHBOR_OFFSETS) {
            int proofSupportValue = valueInt + candidatesCandidateValues[0];
            int j = valueForLong + candidatesCandidateValues[1];
            long blockPos = ProofSupportUtils.getTime(proofSupportValue, deck.layerY, j);
            if (hashSet.add(blockPos)
               && !probe.isSchematicTargetAt(proofSupportValue, deck.layerY, j)
               && !probe.isCollidableAt(proofSupportValue, deck.layerY, j)
               && !probe.isCollidableAt(proofSupportValue, deck.layerY + 1, j)
               && !probe.isCollidableAt(proofSupportValue, deck.layerY + 2, j)) {
               arrayList.add(new long[]{candidatesCandidateValue, blockPos});
            }
         }
      }

      return arrayList;
   }

   public static List<FloorBuildPlanner.Pick> pickAllStandCells(
      FloorBuildPlanner.Deck deck, int remainingCandidatesValue, int j, Set<Long> set, Predicate<FloorBuildPlanner.Pick> predicate
   ) {
      return pickBestStandCells(deck, remainingCandidatesValue, j, set, predicate, (long2, long3) -> true);
   }

   public static FloorBuildPlanner.Search searchDistancesFrom(FloorBuildPlanner.Deck deck, long candidatesCandidateValue, BiPredicate<Long, Long> biPredicate) {
      var hashMap = new HashMap();
      var linkedHashMap = new HashMap();
      if (deck != null && deck.containsCell(candidatesCandidateValue)) {
         var arrayDeque = new ArrayDeque();
         arrayDeque.add(candidatesCandidateValue);
         hashMap.put(candidatesCandidateValue, 0);

         while (!arrayDeque.isEmpty()) {
            long proofSupportValue = (Long)arrayDeque.remove();
            int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
            int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);

            for (int[] candidatesCandidateValues : NEIGHBOR_OFFSETS) {
               long containsValue = ProofSupportUtils.getTime(valueInt + candidatesCandidateValues[0], deck.layerY + 1, valueForLong + candidatesCandidateValues[1]);
               if (deck.containsCell(containsValue) && !hashMap.containsKey(containsValue) && biPredicate.test(proofSupportValue, containsValue)) {
                  hashMap.put(containsValue, (Integer)hashMap.get(proofSupportValue) + 1);
                  linkedHashMap.put(containsValue, proofSupportValue);
                  arrayDeque.add(containsValue);
               }
            }
         }

         return new FloorBuildPlanner.Search(candidatesCandidateValue, Map.copyOf(hashMap), Map.copyOf(linkedHashMap));
      } else {
         return new FloorBuildPlanner.Search(candidatesCandidateValue, hashMap, linkedHashMap);
      }
   }

   static boolean hasHeadroom(FloorBuildPlanner.Probe probe, int remainingCandidatesValue, int deckSupersedesValue, int j) {
      return !probe.isCollidableAt(remainingCandidatesValue, deckSupersedesValue + 1, j) && !probe.isCollidableAt(remainingCandidatesValue, deckSupersedesValue + 2, j);
   }

   public static List<Long> getShortAdjacentPath(FloorBuildPlanner.Deck deck, long candidatesCandidateValue, long containsValue) {
      if (deck != null && deck.containsCell(candidatesCandidateValue) && deck.containsCell(containsValue) && candidatesCandidateValue != containsValue) {
         int valueInt = ProofSupportUtils.getValueInt(candidatesCandidateValue);
         int valueForLong = ProofSupportUtils.getValueForLong(candidatesCandidateValue);

         for (int[] candidatesCandidateValues : NEIGHBOR_OFFSETS) {
            long time5 = ProofSupportUtils.getTime(valueInt + candidatesCandidateValues[0], deck.layerY + 1, valueForLong + candidatesCandidateValues[1]);
            if (deck.containsCell(time5)) {
               if (time5 == containsValue) {
                  return List.of(candidatesCandidateValue, containsValue);
               }

               if (Math.abs(ProofSupportUtils.getValueInt(time5) - ProofSupportUtils.getValueInt(containsValue))
                     + Math.abs(ProofSupportUtils.getValueForLong(time5) - ProofSupportUtils.getValueForLong(containsValue))
                  == 1) {
                  return List.of(candidatesCandidateValue, time5, containsValue);
               }
            }
         }

         return List.of();
      } else {
         return List.of();
      }
   }

   public static List<Long> getCellsWithOpenAbove(FloorBuildPlanner.Probe probe, FloorBuildPlanner.Deck deck) {
      var arrayList = new ArrayList();

      for (long candidatesCandidateValue : deck.floorCells) {
         int valueInt = ProofSupportUtils.getValueInt(candidatesCandidateValue);
         int valueForLong = ProofSupportUtils.getValueForLong(candidatesCandidateValue);
         if (probe.isSchematicTargetAt(valueInt, deck.layerY + 1, valueForLong) || probe.isSchematicTargetAt(valueInt, deck.layerY + 2, valueForLong)) {
            arrayList.add(candidatesCandidateValue);
         }
      }

      return arrayList;
   }

   private FloorBuildPlanner() {
   }

   public static final class Deck {
      public final int connectedExtraCount;
      public final Map<Long, List<FloorBuildPlanner.Frontier>> frontiersByCell;
      public final int layerY;
      public final Set<Long> floorCells;

      Deck(int candidatesCandidateValue, Set<Long> chooseSet, Map<Long, List<FloorBuildPlanner.Frontier>> map, int remaining) {
         this.layerY = candidatesCandidateValue;
         this.floorCells = chooseSet;
         this.frontiersByCell = map;
         this.connectedExtraCount = remaining;
      }

      public boolean hasEnoughEdgeFrontiers() {
         int edgePose = this.countEdgeFrontiers();
         return edgePose > 0 && (edgePose >= 4 || this.connectedExtraCount >= 8);
      }

      public int countFrontiersForStand(long standValue) {
         int stand = 0;

         for (List list : this.frontiersByCell.values()) {
            for (FloorBuildPlanner.Frontier frontier : (Iterable<com.corz.client.utils.FloorBuildPlanner.Frontier>) (list)) {
               if (frontier.stand() == standValue) {
                  stand++;
                  break;
               }
            }
         }

         return stand;
      }

      public boolean containsCell(long containsValue) {
         return this.floorCells.contains(containsValue);
      }

      public int countEdgeFrontiers() {
         int edgePose = 0;

         for (List list : this.frontiersByCell.values()) {
            for (FloorBuildPlanner.Frontier frontier : (Iterable<com.corz.client.utils.FloorBuildPlanner.Frontier>) (list)) {
               if (frontier.pose() == FloorBuildPlanner.Pose.EDGE) {
                  edgePose++;
                  break;
               }
            }
         }

         return edgePose;
      }

      public int getFrontierCount() {
         return this.frontiersByCell.size();
      }

      public int getCellCount() {
         return this.floorCells.size();
      }
   }

   public record Frontier(long cell, long stand, FloorBuildPlanner.Pose pose, int dirX, int dirZ) {
   }

   public record Pick(long cell, long stand, FloorBuildPlanner.Pose pose, int standDist, int dirX, int dirZ) {
   }

   public static enum Pose {
      EDGE,
      DIAGONAL,
      FROM_ABOVE;
   }

   public interface Probe {
      boolean isPlaceableMissingBlockAt(int autoBuilderValue, int aboveDiagonalValue, int j);

      boolean isSchematicTargetAt(int autoBuilderValue, int deckSupersedesValue, int j);

      boolean isCollidableAt(int collisionValue, int deckSupersedesValue, int j);

      boolean isTargetBlockPlacedAt(int blockAutoValue, int deckSupersedesValue, int j);
   }

   public static final class ProbeResult {
      public final FloorBuildPlanner.Deck deck;
      public final int builtFloorCells;
      public final int layer;
      public final int connectedRemaining;
      public final int entryCandidates;
      public final int frontier;
      public final int largestBuiltComponent;
      public final int headroomFloorCells;

      public String formatBuiltFloor() {
         return "layer="
            + this.layer
            + " builtFloorCells="
            + this.builtFloorCells
            + " headroomFloorCells="
            + this.headroomFloorCells
            + " largestBuiltComponent="
            + this.largestBuiltComponent
            + " frontier="
            + this.frontier
            + " connectedRemaining="
            + this.connectedRemaining
            + " entryCandidates="
            + this.entryCandidates;
      }

      ProbeResult(
         int layer,
         int builtFloorCells,
         int headroomFloorCells,
         int largestBuiltComponent,
         int frontier2,
         int connectedRemaining2,
         int entryCandidates2,
         FloorBuildPlanner.Deck remaining
      ) {
         this.layer = layer;
         this.builtFloorCells = builtFloorCells;
         this.headroomFloorCells = headroomFloorCells;
         this.largestBuiltComponent = largestBuiltComponent;
         this.frontier = frontier2;
         this.connectedRemaining = connectedRemaining2;
         this.entryCandidates = entryCandidates2;
         this.deck = remaining;
      }
   }

   public record Search(long source, Map<Long, Integer> distances, Map<Long, Long> parents) {
      public List<Long> getPathTo(long cell) {
         if (!this.distances.containsKey(cell)) {
            return List.of();
         } else {
            var arrayList = new ArrayList();
            long totalPriceData = cell;

            while (true) {
               arrayList.add(totalPriceData);
               if (totalPriceData == this.source) {
                  Collections.reverse(arrayList);
                  return List.copyOf(arrayList);
               }

               totalPriceData = this.parents.get(totalPriceData);
            }
         }
      }

      public int getDistance(long cell) {
         return this.distances.getOrDefault(cell, -1);
      }

      public long getMinCellKey() {
         return this.distances.keySet().stream().mapToLong(Long::longValue).min().orElse(0L);
      }
   }
}
