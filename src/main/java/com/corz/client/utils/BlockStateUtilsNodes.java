package com.corz.client.utils;

import com.corz.client.utils.BlockStateUtilsView.Policy;
import com.corz.client.utils.BlockStateUtilsView.Reject;
import com.corz.client.utils.BlockStateUtilsView.Requirements;
import com.corz.client.utils.BlockStateUtilsView.Result;
import com.corz.client.utils.Nodes.Kind;
import com.corz.client.utils.Nodes.Step;
import com.corz.client.utils.Nodes.StepKind;
import com.corz.client.world.MathClampUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import com.corz.client.utils.WalkRouteFinder;
import com.corz.client.utils.BlockStateUtilsView;
import com.corz.client.utils.Nodes;

public final class BlockStateUtilsNodes {
   private static final int[][] tempsFeetInts = new int[][]{
      {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1}, {1, -1, 0}, {-1, -1, 0}, {0, -1, 1}, {0, -1, -1}
   };
   private static final BlockState DEFAULT_DIRT_STATE = Blocks.DIRT.getDefaultState();

   public static Nodes getNodes(BlockStateUtilsNodes.MutableWorld mutableWorld, BlockStateUtilsNodes.WorkSource workSource, BlockStateUtilsNodes.Survey survey) {
      var arrayList = new ArrayList();
      Nodes batchStatic = getStaticBatchNodes(mutableWorld, workSource, survey);
      if (batchStatic != null) {
         arrayList.add(batchStatic);
      }

      Nodes nodes = getNodes(mutableWorld, workSource, survey, 6);
      if (nodes != null) {
         arrayList.add(nodes);
      }

      if (arrayList.isEmpty()) {
         return null;
      } else {
         arrayList.sort(Comparator.comparingDouble(Nodes::getTotalAmount).reversed());
         return (Nodes)arrayList.get(0);
      }
   }

   public static int getLongY(long longYValue) {
      return BlockPos.unpackLongY(longYValue);
   }

   private BlockStateUtilsNodes() {
   }

   public static Nodes getDirBatch(BlockView blockView, BlockStateUtilsNodes.WorkSource workSource, long dirBatchValue) {
      BlockPos long2 = unpackBlockPos(dirBatchValue);
      if (!WalkRouteFinder.isStandable(blockView, long2)) {
         return null;
      } else {
         Vec3d add = getBlockCenter(long2);
         double settingAuto = workSource.getSettingAuto();
         double dirBatchValue2 = (settingAuto + 1.0) * (settingAuto + 1.0);
         var arrayList = new ArrayList();

         for (long getAutoBuilderItem : workSource.getAutoBuilder(dirBatchValue, dirBatchValue2)) {
            if (!(Vec3d.ofCenter(unpackBlockPos(getAutoBuilderItem)).squaredDistanceTo(add) > dirBatchValue2)) {
               BlockState autoBuilder = workSource.getAutoBuilder(getAutoBuilderItem);
               if (autoBuilder != null && !workSource.canBlock(autoBuilder) && workSource.canAutoBuilder(autoBuilder)) {
                  Requirements autoBuilder2 = workSource.getAutoBuilder(blockView, getAutoBuilderItem, autoBuilder);
                  if (!autoBuilder2.sixWay()
                     && BlockStateUtilsView.getFaceChest(
                           blockView,
                           unpackBlockPos(getAutoBuilderItem),
                           add,
                           settingAuto,
                           autoBuilder2.supportDir(),
                           autoBuilder2.lookDir(),
                           autoBuilder2.yaw(),
                           autoBuilder2.axis(),
                           false,
                           workSource.getAbsent(),
                           workSource.getApiAuto()
                        )
                        .isHit()) {
                     arrayList.add(getAutoBuilderItem);
                  }
               }
            }
         }

         if (arrayList.isEmpty()) {
            return null;
         } else {
            var arrayList2 = new ArrayList();

            for (long dirBatchValue4 : (Iterable<Long>) (getReplaceableCollision(blockView, workSource, dirBatchValue, arrayList))) {
               arrayList2.add(new Step(dirBatchValue, dirBatchValue4));
            }

            return new Nodes(Kind.STATIC_STAND_BATCH, arrayList2, List.of(), Math.max(0, workSource.getFacelessPave(dirBatchValue)), getDistance3d(arrayList2.size(), 0, 0));
         }
      }
   }

   private static int getRejectRank(Reject reject) {
      return switch (reject) {
         case NO_STAND -> -1;
         case NO_SUPPORT -> 0;
         case DIRECTIONAL_STATE -> 1;
         case OUT_OF_REACH -> 2;
         case NO_LOS -> 3;
         default -> 4;
      };
   }

   public static int getLongZ(long longZValue) {
      return BlockPos.unpackLongZ(longZValue);
   }

   private static double getDistanceToBlockCenter(Vec3d vec, long centerValue) {
      return MathClampUtils.getWrapDegrees(vec, Vec3d.ofCenter(unpackBlockPos(centerValue)))[0];
   }

   public static Vec3d getBlockCenter(BlockPos pos) {
      return Vec3d.ofCenter(pos).add(0.0, 1.12, 0.0);
   }

   public static long packBlockPos(int blockNodesValue, int blockNodesValue2, int blockNodesValue3) {
      return BlockPos.asLong(blockNodesValue, blockNodesValue2, blockNodesValue3);
   }

   public static Nodes getNodes(BlockView blockView, BlockStateUtilsNodes.WorkSource workSource, BlockStateUtilsNodes.Survey survey, int nodesValue) {
      return getLocalComponent(blockView, workSource, survey, nodesValue, Set.of());
   }

   public static Nodes searchAllLanes(
      BlockStateUtilsNodes.MutableWorld mutableWorld,
      BlockStateUtilsNodes.WorkSource workSource,
      BlockStateUtilsNodes.Survey survey,
      BlockStateUtilsNodes.AccessDiag accessDiag,
      BlockStateUtilsNodes.AccessRequest accessRequest
   ) {
      var hashMap = new HashMap();
      var hashMap2 = new HashMap<Long, List<Long>>();

      for (Entry entry : survey.sixDirRejectMap.entrySet()) {
         if (canLosReach((Reject)entry.getValue())) {
            hashMap2.computeIfAbsent(getDerivedKey((Long)entry.getKey()), long2 -> new ArrayList<>()).add((Long)entry.getKey());
         }
      }

      var hashMap3 = new HashMap();

      for (long lanesValue : (Iterable<Long>) (hashMap2.keySet())) {
         hashMap3.put(lanesValue, lanesValue);
      }

      for (long longXValue : (Iterable<Long>) (hashMap2.keySet())) {
         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               for (int m = -1; m <= 1; m++) {
                  if (i != 0 || j != 0 || m != 0) {
                     long formatGroundAir = packBlockPos(getLongX(longXValue) + i, getLongY(longXValue) + j, getLongZ(longXValue) + m);
                     if (hashMap3.containsKey(formatGroundAir)) {
                        putLongMapping(hashMap3, longXValue, formatGroundAir);
                     }
                  }
               }
            }
         }
      }

      for (Entry entryItem : hashMap2.entrySet()) {
         ((java.util.List) hashMap.computeIfAbsent(getLongFromMap(hashMap3, (Long)entryItem.getKey()), obj -> new ArrayList())).addAll((Collection)entryItem.getValue());
      }

      accessDiag.clusters = hashMap.size();

      for (List list : (Iterable<java.util.List>) (hashMap.values())) {
         accessDiag.maxBest = Math.max(accessDiag.maxBest, list.size());
      }

      if (hashMap.isEmpty()) {
         accessDiag.stopped = "no stand-less work at all";
         return null;
      } else {
         Nodes allLanes = null;
         double lanesLimit4 = 0.0;
         long posBlock = workSource.getPosBlock();
         var arrayList = new ArrayList(hashMap.entrySet());
         arrayList.sort(Comparator.comparingDouble(feet -> -((List)((java.util.Map.Entry) feet).getValue()).size() / (1.0 + Math.sqrt(getMinDistanceToList((List<Long>)((java.util.Map.Entry) feet).getValue(), posBlock)))));

         for (int p = 0; p < Math.min(8, arrayList.size()); p++) {
            var cluster = (List)((Entry)arrayList.get(p)).getValue();
            if (!accessRequest.canScope(cluster)) {
               accessDiag.stopped = "cluster outside the scope of a " + accessRequest.why() + " request";
            } else if (cluster.size() < accessRequest.minUnlocks()) {
               accessDiag.stopped = "cluster of "
                  + cluster.size()
                  + " is below minUnlocks="
                  + accessRequest.minUnlocks()
                  + " ("
                  + accessRequest.why()
                  + ") \u2014 the lane search was never started";
            } else {
               Nodes tempsFeet = searchNodesWithTemps(mutableWorld, workSource, cluster, accessDiag, accessRequest);
               if (tempsFeet != null) {
                  accessDiag.lanes++;
                  double lanesLimit = tempsFeet.getTotalAmount();
                  if (lanesLimit > lanesLimit4) {
                     lanesLimit4 = lanesLimit;
                     allLanes = tempsFeet;
                  }
               }
            }
         }

         return allLanes;
      }
   }

   public static Nodes getStaticBatchNodes(BlockView blockView, BlockStateUtilsNodes.WorkSource workSource, BlockStateUtilsNodes.Survey survey) {
      long batchStaticValue = 0L;
      int batchStaticValue2 = 0;
      int batchStaticLimit = Integer.MAX_VALUE;
      boolean batchStaticFlag = false;

      for (Entry entry : survey.batchStaticListMap.entrySet()) {
         int facelessPave = workSource.getFacelessPave((Long)entry.getKey());
         if (facelessPave >= 0) {
            int size2 = ((List)entry.getValue()).size();
            if (!batchStaticFlag || size2 > batchStaticValue2 || size2 == batchStaticValue2 && facelessPave < batchStaticLimit) {
               batchStaticFlag = true;
               batchStaticValue = (Long)entry.getKey();
               batchStaticValue2 = size2;
               batchStaticLimit = facelessPave;
            }
         }
      }

      if (!batchStaticFlag) {
         return null;
      } else {
         var replaceableCollision = getReplaceableCollision(blockView, workSource, batchStaticValue, survey.batchStaticListMap.get(batchStaticValue));
         var arrayList = new ArrayList(replaceableCollision.size());

         for (long replaceableCollisionItem : replaceableCollision) {
            arrayList.add(new Step(batchStaticValue, replaceableCollisionItem));
         }

         return new Nodes(Kind.STATIC_STAND_BATCH, arrayList, List.of(), batchStaticLimit, getDistance3d(arrayList.size(), batchStaticLimit, 0));
      }
   }

   private static long getLongFromMap(Map<Long, Long> map, long blockNodesValue) {
      long botCrystalText = blockNodesValue;

      while (map.get(botCrystalText) != botCrystalText) {
         botCrystalText = (Long)map.get(botCrystalText);
      }

      while (map.get(blockNodesValue) != botCrystalText) {
         long root2 = (Long)map.get(blockNodesValue);
         map.put(blockNodesValue, botCrystalText);
         blockNodesValue = root2;
      }

      return botCrystalText;
   }

   private static boolean canLosReach(Reject reject) {
      return reject == Reject.NO_STAND || reject == Reject.OUT_OF_REACH || reject == Reject.NO_LOS;
   }

   private static List<Long> getAbsentDir(BlockView blockView, BlockStateUtilsNodes.WorkSource workSource, long absentDirValue, List<Long> list) {
      var arrayList = new ArrayList();
      Vec3d add = getBlockCenter(unpackBlockPos(absentDirValue));
      double settingAuto = workSource.getSettingAuto();

      for (long longValue : list) {
         if (!(Vec3d.ofCenter(unpackBlockPos(longValue)).squaredDistanceTo(add) > (settingAuto + 1.0) * (settingAuto + 1.0))) {
            BlockState autoBuilder = workSource.getAutoBuilder(longValue);
            if (autoBuilder != null && !workSource.canBlock(autoBuilder) && workSource.canAutoBuilder(autoBuilder)) {
               Requirements autoBuilder2 = workSource.getAutoBuilder(blockView, longValue, autoBuilder);
               if (!autoBuilder2.sixWay()
                  && BlockStateUtilsView.getFaceChest(
                        blockView,
                        unpackBlockPos(longValue),
                        add,
                        settingAuto,
                        autoBuilder2.supportDir(),
                        autoBuilder2.lookDir(),
                        autoBuilder2.yaw(),
                        autoBuilder2.axis(),
                        false,
                        ShapeContext.absent(),
                        workSource.getApiAuto()
                     )
                     .isHit()) {
                  arrayList.add(longValue);
               }
            }
         }
      }

      return arrayList;
   }

   public static BlockStateUtilsNodes.Survey buildSurvey(BlockView blockView, BlockStateUtilsNodes.WorkSource workSource) {
      BlockStateUtilsNodes.Survey survey = new BlockStateUtilsNodes.Survey();
      double settingAuto = workSource.getSettingAuto();
      int sixDirLimit = (int)Math.ceil(settingAuto) + 1;
      ShapeContext absent = workSource.getAbsent();
      var arrayList = new ArrayList();

      for (long sixDirValue : workSource.getAutoBuilder()) {
         arrayList.add(sixDirValue);
      }

      var hashMap = new HashMap<Long, List<Long>>();

      for (long longXValue : (Iterable<Long>) (arrayList)) {
         hashMap.computeIfAbsent(packColumnPos(getLongX(longXValue), getLongZ(longXValue)), long2 -> new ArrayList<>()).add(longXValue);
         survey.sixDirCount++;
      }

      for (long getFacelessPaveItem : workSource.getFacelessPave()) {
         BlockPos long3 = unpackBlockPos(getFacelessPaveItem);
         if (WalkRouteFinder.isStandable(blockView, long3)) {
            survey.directionCount++;
            Vec3d add = getBlockCenter(long3);
            ArrayList botCrystalValue = null;

            for (int i = -sixDirLimit; i <= sixDirLimit; i++) {
               for (int j = -sixDirLimit; j <= sixDirLimit; j++) {
                  var list = (List)hashMap.get(packColumnPos(getLongX(getFacelessPaveItem) + i, getLongZ(getFacelessPaveItem) + j));
                  if (list != null) {
                     for (long longYValue : (Iterable<Long>) (list)) {
                        if (Math.abs(getLongY(longYValue) - getLongY(getFacelessPaveItem)) > sixDirLimit + 1) {
                           survey.surveyCount++;
                        } else {
                           BlockPos long4 = unpackBlockPos(longYValue);
                           if (Vec3d.ofCenter(long4).squaredDistanceTo(add) > (settingAuto + 1.0) * (settingAuto + 1.0)) {
                              survey.surveyCount++;
                           } else {
                              BlockState autoBuilder = workSource.getAutoBuilder(longYValue);
                              if (autoBuilder != null && !workSource.canBlock(autoBuilder)) {
                                 Requirements autoBuilder2 = workSource.getAutoBuilder(blockView, longYValue, autoBuilder);
                                 if (!autoBuilder2.sixWay()) {
                                    Result faceChest = BlockStateUtilsView.getFaceChest(
                                       blockView, long4, add, settingAuto, autoBuilder2.supportDir(), autoBuilder2.lookDir(), autoBuilder2.yaw(), autoBuilder2.axis(), false, absent, workSource.getApiAuto()
                                    );
                                    if (!faceChest.isHit()) {
                                       survey.sixDirRejectMap.merge(longYValue, faceChest.reject(), BlockStateUtilsNodes::pickWorseReject);
                                    } else if (!workSource.canAutoBuilder(autoBuilder)) {
                                       survey.sixDirRejectMap.put(longYValue, Reject.MATERIAL);
                                    } else {
                                       if (botCrystalValue == null) {
                                          botCrystalValue = new ArrayList();
                                       }

                                       botCrystalValue.add(longYValue);
                                       survey.sixDirListMap.computeIfAbsent(longYValue, long5 -> new ArrayList<>()).add(getFacelessPaveItem);
                                       survey.sixDirRejectMap.remove(longYValue);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (botCrystalValue != null) {
               survey.batchStaticListMap.put(getFacelessPaveItem, botCrystalValue);
            }
         }
      }

      for (long autoBuilderValue : (Iterable<Long>) (arrayList)) {
         if (survey.sixDirListMap.containsKey(autoBuilderValue)) {
            survey.sixDirRejectMap.remove(autoBuilderValue);
         } else if (!survey.sixDirRejectMap.containsKey(autoBuilderValue)) {
            BlockState autoBuilder3 = workSource.getAutoBuilder(autoBuilderValue);
            if (autoBuilder3 != null && !workSource.canBlock(autoBuilder3) && !workSource.getAutoBuilder(blockView, autoBuilderValue, autoBuilder3).sixWay()) {
               survey.sixDirRejectMap.put(autoBuilderValue, Reject.NO_STAND);
            }
         }
      }

      return survey;
   }

   private static int expandLane(BlockStateUtilsNodes.MutableWorld mutableWorld, BlockStateUtilsNodes.WorkSource workSource, BlockStateUtilsNodes.Lane lane, List<Long> list) {
      var arrayList = new ArrayList();

      for (long tempsFeetValue : lane.temps()) {
         mutableWorld.setOverrideState(tempsFeetValue, DEFAULT_DIRT_STATE);
         arrayList.add(tempsFeetValue);
      }

      boolean tempsFeetFlag = false;

      int tempsFeet;
      try {
         tempsFeetFlag = true;
         var hashSet = new HashSet<>(getAbsentDir(mutableWorld, workSource, lane.feet(), list));
         int tempsFeetCounter = 12 - lane.temps().size();

         for (long longValue : list) {
            if (tempsFeetCounter <= 0) {
               break;
            }

            if (!hashSet.contains(longValue)) {
               long dirSupport = getDirSupport(mutableWorld, workSource, lane.feet(), longValue);
               if (dirSupport != Long.MIN_VALUE) {
                  tempsFeetCounter--;
                  hashSet.add(longValue);
               }
            }
         }

         tempsFeet = hashSet.size();
         tempsFeetFlag = false;
      } finally {
         if (tempsFeetFlag) {
            for (int i = arrayList.size() - 1; i >= 0; i--) {
               mutableWorld.clearOverrideState((Long)arrayList.get(i));
            }
         }
      }

      for (int j = arrayList.size() - 1; j >= 0; j--) {
         mutableWorld.clearOverrideState((Long)arrayList.get(j));
      }

      return tempsFeet;
   }

   private static BlockStateUtilsNodes.Lane getRootLane(BlockStateUtilsNodes.Lane parent) {
      while (parent.parent() != null) {
         parent = parent.parent();
      }

      return parent;
   }

   private static String getBlockIdString(BlockState state) {
      return state == null ? "" : Registries.BLOCK.getId(state.getBlock()).toString();
   }

   private static int getReachableCount(long blockNodesValue, Map<Long, Set<Long>> map, Map<Long, Integer> map2, Set<Long> set) {
      Integer dependson = (Integer)map2.get(blockNodesValue);
      if (dependson != null) {
         return dependson;
      } else if (!set.add(blockNodesValue)) {
         return 0;
      } else {
         int blockNodesBound = 0;

         for (long getOrDefaultItem : map.getOrDefault(blockNodesValue, Set.of())) {
            blockNodesBound = Math.max(blockNodesBound, 1 + getReachableCount(getOrDefaultItem, map, map2, set));
         }

         set.remove(blockNodesValue);
         map2.put(blockNodesValue, blockNodesBound);
         return blockNodesBound;
      }
   }

   private static long packColumnPos(int blockNodesValue, int blockNodesValue2) {
      return (long)blockNodesValue << 32 ^ blockNodesValue2 & 4294967295L;
   }

   public static int getLongX(long longXValue) {
      return BlockPos.unpackLongX(longXValue);
   }

   private static int getKeyDistance(long blockNodesValue, long longXValue) {
      return Math.abs(getLongX(blockNodesValue) - getLongX(longXValue)) + Math.abs(getLongY(blockNodesValue) - getLongY(longXValue)) + Math.abs(getLongZ(blockNodesValue) - getLongZ(longXValue));
   }

   private static boolean canCenterSquared(BlockStateUtilsNodes.WorkSource workSource, long centerSquaredValue, List<Long> list, int centerSquaredValue2) {
      if (centerSquaredValue2 <= 0) {
         return true;
      } else {
         Vec3d add = getBlockCenter(unpackBlockPos(centerSquaredValue));
         double centerSquaredLimit = (workSource.getSettingAuto() + 1.0) * (workSource.getSettingAuto() + 1.0);
         int centerSquaredCounter = 0;

         for (long longValue : list) {
            if (Vec3d.ofCenter(unpackBlockPos(longValue)).squaredDistanceTo(add) <= centerSquaredLimit) {
               if (++centerSquaredCounter >= centerSquaredValue2) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static Reject pickWorseReject(Reject reject, Reject reject2) {
      return getRejectRank(reject2) > getRejectRank(reject) ? reject2 : reject;
   }

   private static long getDerivedKey(long blockNodesValue) {
      return packBlockPos(getLongX(blockNodesValue) >> 2, getLongY(blockNodesValue) >> 2, getLongZ(blockNodesValue) >> 2);
   }

   private static double getSquaredDistance(double squaredDistanceValue, Vec3d vec, long longXValue) {
      return squaredDistanceValue + Math.sqrt(vec.squaredDistanceTo(getLongX(longXValue), getLongY(longXValue), getLongZ(longXValue)));
   }

   public static Nodes getNodes(
      BlockStateUtilsNodes.MutableWorld mutableWorld, BlockStateUtilsNodes.WorkSource workSource, BlockStateUtilsNodes.Survey survey, BlockStateUtilsNodes.AccessDiag accessDiag
   ) {
      return searchAllLanes(mutableWorld, workSource, survey, accessDiag, BlockStateUtilsNodes.AccessRequest.getThroughput());
   }

   private static double getMinDistanceToList(List<Long> list, long blockNodesValue) {
      double blockNodesBound = Double.MAX_VALUE;

      for (long longXValue : list) {
         double deltaLongx = getLongX(longXValue) - getLongX(blockNodesValue);
         double deltaLongy = getLongY(longXValue) - getLongY(blockNodesValue);
         double deltaLongz = getLongZ(longXValue) - getLongZ(blockNodesValue);
         blockNodesBound = Math.min(blockNodesBound, deltaLongx * deltaLongx + deltaLongy * deltaLongy + deltaLongz * deltaLongz);
      }

      return blockNodesBound;
   }

   private static void putLongMapping(Map<Long, Long> map, long blockNodesValue, long formatGroundAir) {
      long longFromMap = getLongFromMap(map, blockNodesValue);
      long botCrystalValue = getLongFromMap(map, formatGroundAir);
      if (longFromMap != botCrystalValue) {
         map.put(longFromMap, botCrystalValue);
      }
   }

   public static BlockPos unpackBlockPos(long longValue) {
      return BlockPos.fromLong(longValue);
   }

   public static Nodes computeNodesFromSurvey(BlockStateUtilsNodes.MutableWorld mutableWorld, BlockStateUtilsNodes.WorkSource workSource, BlockStateUtilsNodes.Survey survey) {
      return getNodes(mutableWorld, workSource, survey, new BlockStateUtilsNodes.AccessDiag());
   }

   public static double getDistance3d(int blockNodesValue, int batchStaticLimit, int blockNodesValue3) {
      return blockNodesValue * 0.3 + batchStaticLimit * 0.2 + blockNodesValue3 * 0.9;
   }

   private static boolean canReplaceableAir(BlockView blockView, BlockPos pos) {
      BlockState blockState = blockView.getBlockState(pos);
      return blockState.isAir() || blockState.isReplaceable();
   }

   private static boolean canReplaceableCollision(BlockView blockView, BlockPos pos, Set<Long> set) {
      for (Direction direction : Direction.values()) {
         BlockPos offset = pos.offset(direction);
         if (set.contains(offset.asLong())) {
            return true;
         }

         BlockState blockState = blockView.getBlockState(offset);
         if (!blockState.isAir() && !blockState.isReplaceable() && !blockState.getCollisionShape(blockView, offset).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   static List<Long> getReplaceableCollision(BlockView blockView, BlockStateUtilsNodes.WorkSource workSource, long replaceableCollisionValue, List<Long> list) {
      Vec3d add = getBlockCenter(unpackBlockPos(replaceableCollisionValue));
      var hashSet = new HashSet(list);
      var hashMap = new HashMap();

      for (long longValue : list) {
         var hashSet2 = new HashSet();
         boolean replaceableCollisionFlag = false;

         for (Direction direction : Direction.values()) {
            BlockPos offset = unpackBlockPos(longValue).offset(direction);
            BlockState blockState = blockView.getBlockState(offset);
            if (!blockState.isAir() && !blockState.isReplaceable() && !blockState.getCollisionShape(blockView, offset).isEmpty()) {
               replaceableCollisionFlag = true;
               break;
            }

            if (hashSet.contains(offset.asLong())) {
               hashSet2.add(offset.asLong());
            }
         }

         hashMap.put(longValue, replaceableCollisionFlag ? Set.of() : hashSet2);
      }

      var hashMap2 = new HashMap();

      for (long blockNodesValue : list) {
         hashMap2.put(blockNodesValue, getReachableCount(blockNodesValue, hashMap, hashMap2, new HashSet<>()));
      }

      var arrayList = new ArrayList(list);
      arrayList.sort(
         Comparator.<Long>comparingInt(long2 -> ((int) (Integer) (hashMap2.getOrDefault(long2, 0))))
            .thenComparing(long3 -> -Vec3d.ofCenter(unpackBlockPos(long3)).squaredDistanceTo(add))
            .thenComparing(srcx -> getBlockIdString(workSource.getAutoBuilder(srcx)))
            .thenComparingDouble(long4 -> getDistanceToBlockCenter(add, long4))
      );
      return arrayList;
   }

   public static Nodes getStandChaining(BlockStateUtilsNodes.MutableWorld mutableWorld, BlockStateUtilsNodes.WorkSource workSource, int standChainingLimit, int standChainingLimit2) {
      var arrayList = new ArrayList();
      var hashSet = new HashSet();
      int standChainingValue = -1;
      int standChainingTotal = 0;
      long posBlock = workSource.getPosBlock();

      for (int i = 0; i < standChainingLimit && arrayList.size() < standChainingLimit2; i++) {
         BlockStateUtilsNodes.Survey sixDir = buildSurvey(mutableWorld, workSource);
         if (sixDir.batchStaticListMap.isEmpty()) {
            break;
         }

         Nodes localComponent = getLocalComponent(mutableWorld, workSource, sixDir, 6, hashSet);
         if (localComponent == null || localComponent.isEmpty()) {
            break;
         }

         long longXValue = posBlock;
         boolean standChainingFlag = false;

         for (Step step : localComponent.getCopyOf()) {
            if (arrayList.size() >= standChainingLimit2) {
               break;
            }

            if (hashSet.add(step.target())) {
               arrayList.add(step);
               mutableWorld.setOverrideState(step.target(), workSource.getAutoBuilder(step.target()));
               longXValue = step.stand();
               standChainingFlag = true;
            }
         }

         if (!standChainingFlag) {
            break;
         }

         if (standChainingValue < 0) {
            standChainingValue = localComponent.getEntryNodes();
         } else {
            standChainingTotal += getKeyDistance(posBlock, localComponent.getCopyOf().get(0).stand());
         }

         standChainingTotal += getKeyDistance(localComponent.getCopyOf().get(0).stand(), longXValue);
         posBlock = longXValue;
      }

      if (arrayList.isEmpty()) {
         return null;
      } else {
         if (standChainingValue < 0) {
            standChainingValue = 0;
         }

         return new Nodes(Kind.SELF_CHAINING_SIDE_RUN, arrayList, List.of(), standChainingValue, getDistance3d(arrayList.size(), standChainingValue + standChainingTotal, 0));
      }
   }

   private static Nodes searchNodesWithTemps(
      BlockStateUtilsNodes.MutableWorld mutableWorld,
      BlockStateUtilsNodes.WorkSource workSource,
      List<Long> list,
      BlockStateUtilsNodes.AccessDiag accessDiag,
      BlockStateUtilsNodes.AccessRequest accessRequest
   ) {
      long posBlock = workSource.getPosBlock();
      long tempsFeetValue = 0L;
      double tempsFeetLimit = Double.MAX_VALUE;

      for (long longXValue : list) {
         double deltaLongx = getLongX(longXValue) - getLongX(posBlock);
         double deltaLongy = getLongY(longXValue) - getLongY(posBlock);
         double deltaLongz = getLongZ(longXValue) - getLongZ(posBlock);
         double tempsFeetValue3 = deltaLongx * deltaLongx + deltaLongy * deltaLongy + deltaLongz * deltaLongz;
         if (tempsFeetValue3 < tempsFeetLimit) {
            tempsFeetLimit = tempsFeetValue3;
            tempsFeetValue = longXValue;
         }
      }

      Vec3d center = Vec3d.ofCenter(unpackBlockPos(tempsFeetValue));
      PriorityQueue priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(BlockStateUtilsNodes.Lane::priority));
      var hashMap = new HashMap();

      for (long getFacelessPaveItem : workSource.getFacelessPave()) {
         if (workSource.getFacelessPave(getFacelessPaveItem) >= 0
            && WalkRouteFinder.isStandable(mutableWorld, unpackBlockPos(getFacelessPaveItem))
            && !(center.squaredDistanceTo(getLongX(getFacelessPaveItem), getLongY(getFacelessPaveItem), getLongZ(getFacelessPaveItem)) > 576.0)) {
            priorityQueue.add(new BlockStateUtilsNodes.Lane(getFacelessPaveItem, null, 0, 0.0, getSquaredDistance(0.0, center, getFacelessPaveItem), Set.of()));
            hashMap.put(getFacelessPaveItem * 31L, 0.0);
         }
      }

      accessDiag.seeds = Math.max(accessDiag.seeds, priorityQueue.size());
      if (priorityQueue.isEmpty()) {
         accessDiag.stopped = "no walkable stand within 24 of the cluster";
         return null;
      } else {
         BlockStateUtilsNodes.Lane lane = null;

         for (int i = 0; i < 4000 && !priorityQueue.isEmpty(); i++) {
            BlockStateUtilsNodes.Lane blockStateUtilsNodesLane = (BlockStateUtilsNodes.Lane)priorityQueue.poll();
            accessDiag.expanded++;
            if (!blockStateUtilsNodesLane.temps.isEmpty()
               && canCenterSquared(workSource, blockStateUtilsNodesLane.feet, list, accessRequest.minUnlocks())
               && expandLane(mutableWorld, workSource, blockStateUtilsNodesLane, list) >= accessRequest.minUnlocks()) {
               lane = blockStateUtilsNodesLane;
               break;
            }

            if (blockStateUtilsNodesLane.steps < 28) {
               boolean replaceableAir = canReplaceableAir(mutableWorld, unpackBlockPos(blockStateUtilsNodesLane.feet).up(2));

               for (int[] tempsFeetValues : tempsFeetInts) {
                  if (tempsFeetValues[1] <= 0 || replaceableAir) {
                     long packBlockPos5 = packBlockPos(getLongX(blockStateUtilsNodesLane.feet) + tempsFeetValues[0], getLongY(blockStateUtilsNodesLane.feet) + tempsFeetValues[1], getLongZ(blockStateUtilsNodesLane.feet) + tempsFeetValues[2]);
                     BlockPos long2 = unpackBlockPos(packBlockPos5);
                     if (canReplaceableAir(mutableWorld, long2) && canReplaceableAir(mutableWorld, long2.up())) {
                        long long3 = long2.down().asLong();
                        boolean tempsFeetFlag = WalkRouteFinder.isStandable(mutableWorld, long2) || blockStateUtilsNodesLane.temps.contains(long3);
                        Set<Long> temps = blockStateUtilsNodesLane.temps;
                        double tempsFeetValue6;
                        if (tempsFeetFlag) {
                           tempsFeetValue6 = 1.0;
                        } else {
                           if (blockStateUtilsNodesLane.temps.size() >= 12 || !canReplaceableAir(mutableWorld, long2.down()) || !canReplaceableCollision(mutableWorld, long2.down(), blockStateUtilsNodesLane.temps)) {
                              continue;
                           }

                           mutableWorld.setOverrideState(long3, DEFAULT_DIRT_STATE);
                           boolean standable = WalkRouteFinder.isStandable(mutableWorld, long2);
                           mutableWorld.clearOverrideState(long3);
                           if (!standable) {
                              continue;
                           }

                           tempsFeetValue6 = 9.0;
                           temps = new HashSet<>(blockStateUtilsNodesLane.temps);
                           temps.add(long3);
                        }

                        double squaredDistanceValue = blockStateUtilsNodesLane.cost + tempsFeetValue6;
                        long formatMinPop = packBlockPos5 * 31L + temps.size();
                        Double double2 = (Double)hashMap.get(formatMinPop);
                        if (double2 == null || !(double2 <= squaredDistanceValue)) {
                           hashMap.put(formatMinPop, squaredDistanceValue);
                           priorityQueue.add(
                              new BlockStateUtilsNodes.Lane(packBlockPos5, blockStateUtilsNodesLane, blockStateUtilsNodesLane.steps + 1, squaredDistanceValue, getSquaredDistance(squaredDistanceValue, center, packBlockPos5), (Set<Long>)temps)
                           );
                        }
                     }
                  }
               }
            }
         }

         if (lane == null) {
            if (accessDiag.lanes == 0) {
               accessDiag.stopped = "the lane search never reached the work";
            }

            return null;
         } else {
            var arrayList = new ArrayList();
            var arrayList2 = new ArrayList();
            var arrayList3 = new ArrayList();
            var hashSet = new HashSet();
            boolean tempsFeetFlag3 = false;

            Object obj;
            label445: {
               Nodes tempsFeet;
               try {
                  tempsFeetFlag3 = true;
                  var arrayList4 = new ArrayList();

                  for (BlockStateUtilsNodes.Lane totalPriceData = lane; totalPriceData != null; totalPriceData = totalPriceData.parent) {
                     arrayList4.add(totalPriceData);
                  }

                  Collections.reverse(arrayList4);

                  for (int j = 1; j < arrayList4.size(); j++) {
                     BlockStateUtilsNodes.Lane lane4 = (BlockStateUtilsNodes.Lane)arrayList4.get(j);
                     long goodsSlot = packBlockPos(getLongX(lane4.feet()), getLongY(lane4.feet()) - 1, getLongZ(lane4.feet()));
                     if (lane.temps().contains(goodsSlot)) {
                        arrayList3.add(new Step(((BlockStateUtilsNodes.Lane)arrayList4.get(j - 1)).feet(), goodsSlot, StepKind.TEMP_PLACE));
                        arrayList.add(goodsSlot);
                        mutableWorld.setOverrideState(goodsSlot, DEFAULT_DIRT_STATE);
                        arrayList2.add(goodsSlot);
                     }
                  }

                  for (long tempsItem : lane.temps()) {
                     if (!arrayList.contains(tempsItem)) {
                        arrayList3.add(new Step(lane.feet(), tempsItem, StepKind.TEMP_PLACE));
                        arrayList.add(tempsItem);
                        mutableWorld.setOverrideState(tempsItem, DEFAULT_DIRT_STATE);
                        arrayList2.add(tempsItem);
                     }
                  }

                  for (BlockStateUtilsNodes.Lane lane5 = lane; lane5 != null; lane5 = lane5.parent) {
                     for (long blockPos : getAbsentDir(mutableWorld, workSource, lane5.feet, list)) {
                        if (hashSet.add(blockPos)) {
                           arrayList3.add(new Step(lane5.feet, blockPos));
                        }
                     }
                  }

                  for (BlockStateUtilsNodes.Lane lane6 = lane; lane6 != null && arrayList.size() < 12; lane6 = lane6.parent) {
                     for (long longValue : list) {
                        if (!hashSet.contains(longValue) && arrayList.size() < 12) {
                           long dirSupport = getDirSupport(mutableWorld, workSource, lane6.feet, longValue);
                           if (dirSupport != Long.MIN_VALUE) {
                              arrayList.add(dirSupport);
                              mutableWorld.setOverrideState(dirSupport, DEFAULT_DIRT_STATE);
                              arrayList2.add(dirSupport);
                              hashSet.add(longValue);
                              arrayList3.add(new Step(lane6.feet, dirSupport, StepKind.TEMP_PLACE));
                              arrayList3.add(new Step(lane6.feet, longValue));
                           }
                        }
                     }
                  }

                  int unlocks = 0;

                  for (Step step : (Iterable<com.corz.client.utils.Nodes.Step>) (arrayList3)) {
                     if (!step.isPlaceKind()) {
                        unlocks++;
                     }
                  }

                  accessDiag.temps = arrayList.size();
                  accessDiag.unlocks = unlocks;
                  if (unlocks < accessRequest.minUnlocks()) {
                     accessDiag.stopped = "a lane reached the work but unlocked only " + unlocks + " (minUnlocks=" + accessRequest.minUnlocks() + ")";
                     obj = null;
                     tempsFeetFlag3 = false;
                     break label445;
                  }

                  accessDiag.stopped = "ok";
                  int maxFacelessPave = Math.max(0, workSource.getFacelessPave(getRootLane(lane).feet));
                  tempsFeet = new Nodes(Kind.ACCESS_CLUSTER_RUN, arrayList3, arrayList, maxFacelessPave, getDistance3d(unlocks, maxFacelessPave + lane.steps, arrayList.size()));
                  tempsFeetFlag3 = false;
               } finally {
                  if (tempsFeetFlag3) {
                     for (int k = arrayList2.size() - 1; k >= 0; k--) {
                        mutableWorld.clearOverrideState((Long)arrayList2.get(k));
                     }
                  }
               }

               for (int m = arrayList2.size() - 1; m >= 0; m--) {
                  mutableWorld.clearOverrideState((Long)arrayList2.get(m));
               }

               return tempsFeet;
            }

            for (int p = arrayList2.size() - 1; p >= 0; p--) {
               mutableWorld.clearOverrideState((Long)arrayList2.get(p));
            }

            return (Nodes)obj;
         }
      }
   }

   private static long getDirSupport(BlockStateUtilsNodes.MutableWorld mutableWorld, BlockStateUtilsNodes.WorkSource workSource, long dirSupportValue, long longValue) {
      Vec3d add = getBlockCenter(unpackBlockPos(dirSupportValue));
      double settingAuto = workSource.getSettingAuto();
      if (Vec3d.ofCenter(unpackBlockPos(longValue)).squaredDistanceTo(add) > (settingAuto + 1.0) * (settingAuto + 1.0)) {
         return Long.MIN_VALUE;
      } else {
         BlockState autoBuilder = workSource.getAutoBuilder(longValue);
         if (autoBuilder != null && !workSource.canBlock(autoBuilder) && workSource.canAutoBuilder(autoBuilder)) {
            Requirements autoBuilder2 = workSource.getAutoBuilder(mutableWorld, longValue, autoBuilder);
            if (autoBuilder2.sixWay()) {
               return Long.MIN_VALUE;
            } else {
               for (Direction direction : Direction.values()) {
                  if (autoBuilder2.supportDir() == null || direction == autoBuilder2.supportDir()) {
                     BlockPos offset = unpackBlockPos(longValue).offset(direction);
                     if (canReplaceableAir(mutableWorld, offset) && workSource.getAutoBuilder(offset.asLong()) == null && canReplaceableCollision(mutableWorld, offset, Set.of())) {
                        mutableWorld.setOverrideState(offset.asLong(), DEFAULT_DIRT_STATE);
                        Requirements autoBuilder3 = workSource.getAutoBuilder(mutableWorld, longValue, autoBuilder);
                        boolean hit = BlockStateUtilsView.getFaceChest(
                              mutableWorld,
                              unpackBlockPos(longValue),
                              add,
                              settingAuto,
                              autoBuilder3.supportDir(),
                              autoBuilder3.lookDir(),
                              autoBuilder3.yaw(),
                              autoBuilder3.axis(),
                              false,
                              workSource.getAbsent(),
                              workSource.getApiAuto()
                           )
                           .isHit();
                        mutableWorld.clearOverrideState(offset.asLong());
                        if (hit) {
                           return offset.asLong();
                        }
                     }
                  }
               }

               return Long.MIN_VALUE;
            }
         } else {
            return Long.MIN_VALUE;
         }
      }
   }

   public static Nodes getLocalComponent(
      BlockView blockView, BlockStateUtilsNodes.WorkSource workSource, BlockStateUtilsNodes.Survey survey, int localComponentLimit, Set<Long> set
   ) {
      if (survey.batchStaticListMap.isEmpty()) {
         return null;
      } else {
         var hashSet = new HashSet(set);
         var arrayList = new ArrayList();
         long posBlock = workSource.getPosBlock();
         int localComponentValue = -1;
         int localComponentTotal = 0;

         for (int i = 0; i < localComponentLimit; i++) {
            long facelessPaveValue = 0L;
            ArrayList arrayList2 = null;
            int keyDistance3 = 0;
            double localComponentValue4 = 0.0;

            for (Entry entry : survey.batchStaticListMap.entrySet()) {
               if (workSource.getFacelessPave((Long)entry.getKey()) >= 0) {
                  var arrayList3 = new ArrayList();

                  for (long tickx : (Iterable<Long>) ((List)entry.getValue())) {
                     if (!hashSet.contains(tickx)) {
                        arrayList3.add(tickx);
                     }
                  }

                  if (!arrayList3.isEmpty()) {
                     int keyDistance = getKeyDistance(posBlock, ((Long)entry.getKey()).longValue());
                     double localComponentLimit2 = arrayList3.size() / getDistance3d(arrayList3.size(), keyDistance, 0);
                     if (localComponentLimit2 > localComponentValue4) {
                        localComponentValue4 = localComponentLimit2;
                        facelessPaveValue = (Long)entry.getKey();
                        arrayList2 = arrayList3;
                        keyDistance3 = keyDistance;
                     }
                  }
               }
            }

            if (arrayList2 == null || !arrayList.isEmpty() && localComponentValue4 < 0.8333333333333334) {
               break;
            }

            if (localComponentValue < 0) {
               localComponentValue = workSource.getFacelessPave(facelessPaveValue);
            } else {
               localComponentTotal += keyDistance3;
            }

            for (long blockPos : (Iterable<Long>) (getReplaceableCollision(blockView, workSource, facelessPaveValue, arrayList2))) {
               arrayList.add(new Step(facelessPaveValue, blockPos));
               hashSet.add(blockPos);
            }

            posBlock = facelessPaveValue;
         }

         if (arrayList.isEmpty()) {
            return null;
         } else {
            if (localComponentValue < 0) {
               localComponentValue = 0;
            }

            return new Nodes(Kind.LOCAL_COMPONENT_RUN, arrayList, List.of(), localComponentValue, getDistance3d(arrayList.size(), localComponentValue + localComponentTotal, 0));
         }
      }
   }

   public static final class AccessDiag {
      public int expanded;
      public int lanes;
      public int maxBest;
      public int unlocks;
      public int clusters;
      public int seeds;
      public int temps;
      public String stopped = "not attempted";

      @Override
      public String toString() {
         return "clusters="
            + this.clusters
            + " best="
            + this.maxBest
            + " seeds="
            + this.seeds
            + " expanded="
            + this.expanded
            + " lanes="
            + this.lanes
            + " temps="
            + this.temps
            + " unlocks="
            + this.unlocks
            + " stopped="
            + this.stopped;
      }
   }

   public record AccessRequest(int minUnlocks, Set<Long> scope, int tempBudget, String why) {
      public static BlockStateUtilsNodes.AccessRequest getEscapeCompletion(Set<Long> tempBudget, int blocked) {
         int escapeCompletionBound = tempBudget == null ? 0 : tempBudget.size();
         return new BlockStateUtilsNodes.AccessRequest(Math.max(1, Math.min(3, escapeCompletionBound)), tempBudget, blocked, "completion-escape");
      }

      public boolean hasMinUnlocks() {
         return this.minUnlocks >= 3 || this.hasScope();
      }

      public boolean hasScope() {
         return !this.scope.isEmpty();
      }

      public boolean canScope(Collection<Long> cluster) {
         if (!this.hasScope()) {
            return true;
         } else {
            for (long scopeValue : cluster) {
               if (this.scope.contains(scopeValue)) {
                  return true;
               }
            }

            return false;
         }
      }

      public static BlockStateUtilsNodes.AccessRequest getThroughput() {
         return new BlockStateUtilsNodes.AccessRequest(3, Set.of(), 12, "throughput");
      }

      public AccessRequest(int minUnlocks, Set<Long> scope, int tempBudget, String why) {
         scope = scope == null ? Set.of() : Set.copyOf(scope);
         minUnlocks = Math.max(1, minUnlocks);
         this.minUnlocks = minUnlocks;
         this.scope = scope;
         this.tempBudget = tempBudget;
         this.why = why;
      }
   }

   private record Lane(long feet, BlockStateUtilsNodes.Lane parent, int steps, double cost, double priority, Set<Long> temps) {
   }

   public interface MutableWorld extends BlockView {
      void setOverrideState(long mutableWorldValue, BlockState state);

      void clearOverrideState(long mutableWorldValue);
   }

   public static final class Survey {
      public int directionCount;
      public final Map<Long, List<Long>> sixDirListMap;
      public final Map<Long, List<Long>> batchStaticListMap = new HashMap<>();
      public int sixDirCount;
      public int surveyCount;
      public final Map<Long, Reject> sixDirRejectMap;

      public int getMaxEntryCount() {
         int surveyBound = 0;

         for (List list : this.batchStaticListMap.values()) {
            surveyBound = Math.max(surveyBound, list.size());
         }

         return surveyBound;
      }

      public int getTotalCount() {
         return this.sixDirListMap.size();
      }

      public int getRejectedCount() {
         int surveyCounter = 0;

         for (List list : this.batchStaticListMap.values()) {
            if (list.size() > 1) {
               surveyCounter++;
            }
         }

         return surveyCounter;
      }

      public double getAverageCount() {
         if (this.batchStaticListMap.isEmpty()) {
            return 0.0;
         } else {
            int surveyTotal = 0;

            for (List list : this.batchStaticListMap.values()) {
               surveyTotal += list.size();
            }

            return (double)surveyTotal / this.batchStaticListMap.size();
         }
      }

      public Survey() {
         this.sixDirListMap = new HashMap<>();
         this.sixDirRejectMap = new HashMap<>();
      }
   }

   public interface WorkSource {
      default ShapeContext getAbsent() {
         return ShapeContext.absent();
      }

      default Policy getApiAuto() {
         return Policy.INSTANCE;
      }

      default Requirements getAutoBuilder(BlockView blockView, long autoBuilderValue, BlockState state) {
         return BlockStateUtilsView.getDirectionAxisData(blockView, BlockPos.fromLong(autoBuilderValue), state);
      }

      boolean canFacelessPave(long facelessPaveValue);

      default Iterable<Long> getAutoBuilder(long autoBuilderValue, double autoBuilderValue2) {
         return this.getAutoBuilder();
      }

      long getPosBlock();

      default boolean canBlock(BlockState state) {
         return BlockStateUtilsView.canFluidState(state);
      }

      default boolean canAutoBuilder(BlockState state) {
         return true;
      }

      BlockState getAutoBuilder(long autoBuilderValue);

      default int getFacelessPave(long facelessPaveValue) {
         return Math.abs(BlockStateUtilsNodes.getLongX(facelessPaveValue) - BlockStateUtilsNodes.getLongX(this.getPosBlock()))
            + Math.abs(BlockStateUtilsNodes.getLongY(facelessPaveValue) - BlockStateUtilsNodes.getLongY(this.getPosBlock()))
            + Math.abs(BlockStateUtilsNodes.getLongZ(facelessPaveValue) - BlockStateUtilsNodes.getLongZ(this.getPosBlock()));
      }

      Iterable<Long> getAutoBuilder();

      double getSettingAuto();

      Iterable<Long> getFacelessPave();
   }
}
