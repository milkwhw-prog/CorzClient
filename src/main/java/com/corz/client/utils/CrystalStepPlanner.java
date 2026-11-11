package com.corz.client.utils;

import com.corz.client.utils.BreakTimingClassifier.Kind;
import com.corz.client.entity.WorldCollisionHelper;
import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.LivingEntityHelperVisible;
import com.corz.client.inventory.LivingEntityHelperVisible.Seed;
import com.corz.client.inventory.VecPos;
import com.corz.client.inventory.VecPos.Loadout;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.PosBlockUtilsBonus.Window;
import com.corz.client.world.PosVecUtils;
import com.corz.client.world.PosVecUtils.Path;
import com.corz.client.world.PosVecUtils.Sample;
import com.corz.client.world.TickStatusData;
import com.corz.client.world.VecPosUtilsAmount;
import com.corz.client.world.VecPosUtilsBreak;
import com.corz.client.world.VecPosUtilsBreak.Ctx;
import com.corz.client.world.VecPosUtilsBreak.Op;
import com.corz.client.world.VecPosUtilsDamage;
import com.corz.client.world.VecPosUtilsDamage.Input;
import com.corz.client.world.VecPosUtilsDamage.Weights;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.RayBudget;
import com.corz.client.utils.PendingEntityHitTracker;
import com.corz.client.utils.BlockHazardFlags;
import com.corz.client.utils.EntityHeightSamples;
import com.corz.client.utils.BreakTimingClassifier;
import com.corz.client.categories.combat.CrystalAura;
import com.corz.client.utils.ConnectionTimeUtils;
import ai.auth.ModuleRegistry;

public final class CrystalStepPlanner {
   public static final double[] STAND_RING_RADII = new double[]{1.6, 2.4, 3.4};
   private List<List<Vec3d>> avoidedPaths = List.of();
   private final RayBudget exactCheckLimiter;
   private Predicate<BlockPos> excludedBasePredicate = pos -> false;
   private int maxCandidateLimit = 100;
   private static final AmountAmounts FALLBACK_AMOUNTS = new AmountAmounts();

   private static boolean isWithinFaceReach(BlockPos pos, Vec3d vec) {
      for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
         Vec3d vec3d = BlockEntityUtils.getVec3d(pos, direction);
         Vec3d vec3d2 = new Vec3d(direction.getOffsetX(), 0.0, direction.getOffsetZ());
         if (!((vec.x - vec3d.x) * vec3d2.x + (vec.z - vec3d.z) * vec3d2.z <= 0.0) && vec.distanceTo(vec3d) <= 4.25) {
            return true;
         }
      }

      return false;
   }

   private CrystalStepPlanner.Scored scoreCandidate(
      CrystalStepPlanner.Cand cand,
      WorldCollisionHelper blockPosHelperSolid,
      Path path,
      int topDistanceBound,
      boolean topDistanceFlag,
      boolean idxFeetFlag,
      int topDistanceValue,
      int topDistanceValue2,
      int ping,
      int topDistanceValue4,
      double pilotRenderLimit,
      Weights weights,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      Seed seed,
      AmountAmounts amountAmounts,
      Window window,
      Window window2,
      float topDistanceLimit,
      int noneValue,
      int topDistanceValue7,
      int topDistanceValue8,
      Loadout loadout,
      ClientPlayerEntity entity,
      Vec3d vec,
      Vec3d vec2,
      int topDistanceValue9,
      double topDistanceValue10
   ) {
      BlockPos base = cand.base();
      Vec3d vec3d = BlockEntityUtils.getVec3d(base);
      Vec3d vec3d2 = WorldCollisionHelper.getTopCenter(base);
      var arrayList = new ArrayList(4);
      var arrayList2 = new ArrayList(3);
      Vec3d vec3d3 = null;
      float yaw = entity.getYaw();
      float pitch = entity.getPitch();
      if (!cand.armed()) {
         for (BlockPos blockPos : cand.obsidian()) {
            Vec3d vec3d4 = new Vec3d(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
            double topDistanceValue11 = vec3d3 == null
               ? WorldCollisionHelper.computeAngleFromRotation(yaw, pitch, vec, vec3d4)
               : WorldCollisionHelper.computeAngleDifference(vec, vec3d3, vec3d4);
            arrayList.add(new VecPosUtilsBreak.Step(Op.PLACE_OBSIDIAN, 2, topDistanceValue11, false));
            arrayList2.add(blockPos);
            vec3d3 = vec3d4;
         }

         double topDistanceValue12 = vec3d3 == null
            ? WorldCollisionHelper.computeAngleFromRotation(yaw, pitch, vec, vec3d2)
            : WorldCollisionHelper.computeAngleDifference(vec, vec3d3, vec3d2);
         arrayList.add(new VecPosUtilsBreak.Step(Op.PLACE_CRYSTAL, 1, topDistanceValue12, false));
         arrayList2.add(null);
         vec3d3 = vec3d2;
      }

      Vec3d add = vec3d.add(0.0, 1.0, 0.0);
      double topDistanceValue13 = vec3d3 == null
         ? WorldCollisionHelper.computeAngleFromRotation(yaw, pitch, vec, add)
         : WorldCollisionHelper.computeAngleDifference(vec, vec3d3, add);
      arrayList.add(new VecPosUtilsBreak.Step(Op.BREAK, 3, topDistanceValue13, false));
      int[] topDistanceValues = VecPosUtilsBreak.getValues(new Ctx(loadout.selectedItem(), this.maxCandidateLimit, topDistanceValue8, 2, ping, topDistanceValue4, true), arrayList);
      int topDistanceLimit2 = topDistanceFlag ? path.getValue() - 1 : topDistanceBound - 1;
      double positiveInfinity = Double.POSITIVE_INFINITY;
      int topDistanceTotal = 0;

      for (int i = 0; i < arrayList2.size(); i++) {
         int aggroPreemptBound = topDistanceValues[i] + topDistanceTotal;
         int topDistanceValue14 = arrayList2.get(i) == null ? Math.max(aggroPreemptBound, cand.placeAfter()) : aggroPreemptBound;
         Box box = arrayList2.get(i) == null ? BlockEntityUtils.getBox3(base) : BlockEntityUtils.getBoxForBlockPos((BlockPos)arrayList2.get(i));
         int topDistanceValue15 = -1;

         for (int j = topDistanceValue14; j <= topDistanceLimit2; j++) {
            double minMax = WorldCollisionHelper.computeBoxGap(WorldCollisionHelper.getBoxFromSample(path.getDataForInt(j), 0.0), box);
            if (minMax > 0.0) {
               topDistanceValue15 = j;
               positiveInfinity = Math.min(positiveInfinity, minMax);
               break;
            }
         }

         if (topDistanceValue15 < 0) {
            return null;
         }

         topDistanceTotal += topDistanceValue15 - aggroPreemptBound;
      }

      int pathBound = topDistanceValues[topDistanceValues.length - 1] + topDistanceTotal;
      if (cand.armed() && cand.pendingBreakAt() > pathBound) {
         pathBound = cand.pendingBreakAt();
      }

      if (!cand.armed() && pathBound < topDistanceValues[topDistanceValues.length - 2] + topDistanceTotal + ping) {
         pathBound = topDistanceValues[topDistanceValues.length - 2] + topDistanceTotal + ping;
      }

      if (pathBound > topDistanceLimit2) {
         return null;
      } else {
         double meleeScale = amountAmounts.getMeleeScale();
         int index;
         Kind kind;
         if (!topDistanceFlag && (!idxFeetFlag || !path.getDataForInt(pathBound).onGround())) {
            int topDistanceSize = Math.min(topDistanceBound, path.getValue() - 1) + 1;
            double[] doubles = new double[topDistanceSize];
            double[] doubles2 = new double[topDistanceSize];
            double[] doubles3 = new double[topDistanceSize];

            for (int k = 0; k < topDistanceSize; k++) {
               Sample sample = path.getDataForInt(k);
               doubles[k] = sample.y() - cand.top();
               doubles2[k] = sample.vy();
               doubles3[k] = predictHeightAboveBase(path, k, topDistanceValue2, topDistanceValue, vec3d, sample, cand.top(), seed.rExpl(), meleeScale);
            }

            index = BreakTimingClassifier.findFirstUsableTick(doubles, doubles2, pathBound, noneValue, topDistanceValue7, topDistanceValue2, topDistanceValue, topDistanceBound, doubles3);
            if (index < 0) {
               return null;
            }

            double topDistanceValue17 = index + 1 < topDistanceSize ? doubles[index + 1] : doubles[index];
            kind = BreakTimingClassifier.classifyStrikeTick(
               doubles[index],
               doubles2[index],
               index >= noneValue,
               (int)Math.min(2147483647L, (long)topDistanceValue7 + index),
               topDistanceValue2,
               topDistanceValue,
               topDistanceBound - index,
               topDistanceValue17,
               doubles3[index]
            );
            if (kind == Kind.NONE) {
               kind = Kind.LIFT;
            }
         } else {
            index = -1;

            for (int m = Math.max(0, pathBound); m <= topDistanceLimit2; m++) {
               if (m >= noneValue && (long)topDistanceValue7 + m >= topDistanceValue2) {
                  Sample dataForInt = path.getDataForInt(m);
                  if (EntityHeightSamples.countSamplesAbove(dataForInt.y(), cand.top()) >= 3) {
                     index = m;
                     break;
                  }
               }
            }

            if (index < 0) {
               return null;
            }

            kind = Kind.DAMAGE;
         }

         Sample dataForInt3 = path.getDataForInt(index);
         Vec3d vec3d5 = new Vec3d(dataForInt3.x(), dataForInt3.y(), dataForInt3.z());
         int countSamplesAbove = EntityHeightSamples.countSamplesAbove(dataForInt3.y(), cand.top());
         double vecPosFactor = (double)countSamplesAbove / EntityHeightSamples.HEIGHT_SAMPLE_OFFSETS.length;
         if (!(vecPosFactor > 0.0)) {
            return null;
         } else {
            double topDistanceValue19 = 0.2 * Math.min(index, 6);
            if (!cand.armed()) {
               for (BlockPos obsidianItem : cand.obsidian()) {
                  if (vec.distanceTo(new Vec3d(obsidianItem.getX() + 0.5, obsidianItem.getY(), obsidianItem.getZ() + 0.5)) > 4.25 + topDistanceValue19) {
                     return null;
                  }
               }

               if (vec.distanceTo(vec3d2) > 4.25 + topDistanceValue19) {
                  return null;
               }
            }

            if (!(BlockEntityUtils.getMinMax(vec, BlockEntityUtils.getBox(base)) < 3.0 + topDistanceValue19)) {
               return null;
            } else {
               double vecPosValue = Math.min(vec2.y, cand.top() - 1.0);
               double reachingFraction = EntityHeightSamples.getReachingFraction(vecPosValue, cand.top());
               double blockEntityValue = Math.max(STAND_RING_RADII[0], vec2.distanceTo(vec3d) - 0.2 * Math.min(index, 6));
               float scaledValue = BlockEntityUtils.getAmount(entity, blockEntityValue, 6.0F) * (float)reachingFraction;
               float scaledInstance = PosBlockUtilsBonus.getScaledInstance(scaledValue, absorptionPosData.armor(), window, index);
               float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(scaledValue, absorptionPosData.armor());
               if (!(scaledInstance <= topDistanceLimit)) {
                  return null;
               } else if (toughnessEpf > 0.0F && toughnessEpf >= absorptionPosData.health() + absorptionPosData.absorption() - 1.0F) {
                  return null;
               } else {
                  float readyValue = BlockEntityUtils.getAmount(vecBlockingData.entity(), vec3d5.distanceTo(vec3d), 6.0F) * (float)vecPosFactor;
                  float posBlockLimit = PosBlockUtilsBonus.getReadyInstance(readyValue, vecBlockingData.armor(), window2, index) * vecBlockingData.calibration();
                  boolean canFlag = PosBlockUtilsBonus.canFlag(posBlockLimit, vecBlockingData.getHealthTrusted());
                  boolean topDistanceFlag4 = canFlag && (vecBlockingData.awaitingTotem() || !vecBlockingData.totemInHand() || topDistanceValue9 < topDistanceValue10 - 1.0);
                  double subtractExpl = this.computeStepValue(
                     cand,
                     index,
                     vecPosFactor,
                     posBlockLimit,
                     canFlag,
                     topDistanceFlag4,
                     scaledInstance,
                     topDistanceLimit,
                     path,
                     topDistanceBound,
                     topDistanceValue,
                     topDistanceValue2,
                     pilotRenderLimit,
                     seed,
                     amountAmounts,
                     vec2,
                     positiveInfinity,
                     weights
                  );
                  return !Double.isFinite(subtractExpl)
                     ? null
                     : new CrystalStepPlanner.Scored(cand, index, kind, subtractExpl, vecPosFactor, posBlockLimit, canFlag, topDistanceFlag4, scaledInstance, positiveInfinity, vec3d5, topDistanceValues);
               }
            }
         }
      }
   }

   public CrystalStepPlanner.Step planBestStep(
      long directionVecValue,
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      MinVec minVec,
      PearlsPlaystyleData pearlsPlaystyleData,
      SelfHazData selfHazData,
      VecBlock vecBlock,
      List<VecBlockingData> list,
      int idxFeetBound,
      int directionVecValue3,
      float idxFeetLimit,
      TickStatusData tickStatusData,
      Predicate<Vec3d> predicate,
      CrystalStepPlanner.Step step
   ) {
      return this.planBestStepAllowGrounded(
         directionVecValue, blockPosHelperSolid, livingEntityHelperVisible, absorptionPosData, vecBlockingData, minVec, pearlsPlaystyleData, selfHazData, vecBlock, list, idxFeetBound, directionVecValue3, idxFeetLimit, tickStatusData, predicate, step, false
      );
   }

   private static Vec3d findGroundBelow(WorldCollisionHelper blockPosHelperSolid, Vec3d vec, int solidValue) {
      int floor2 = (int)Math.floor(vec.x);
      int z = (int)Math.floor(vec.z);
      int floor4 = (int)Math.floor(solidValue - 1.0 + 1.0E-6);

      for (int i = floor4; i >= floor4 - 5; i--) {
         if (!blockPosHelperSolid.solid(floor2, i, z) && !blockPosHelperSolid.solid(floor2, i + 1, z) && blockPosHelperSolid.solid(floor2, i - 1, z)) {
            return new Vec3d(floor2 + 0.5, i, z + 0.5);
         }
      }

      return null;
   }

   private static boolean isBaseBlockedByEntity(WorldCollisionHelper blockPosHelperSolid, CrystalStepPlanner.Cand cand, Entity entity, Entity clientPlayerEntity) {
      if (!cand.armed() && blockPosHelperSolid.hasBlockingEntities(cand.base(), entity, clientPlayerEntity)) {
         return true;
      } else if (cand.obsidian().isEmpty()) {
         return false;
      } else {
         for (EndCrystalEntity endCrystalEntity : blockPosHelperSolid.getCrystalsInRadius(BlockEntityUtils.getVec3d(cand.base()), 3.0)) {
            if (!BlockEntityUtils.getDown(endCrystalEntity).equals(cand.base())) {
               for (BlockPos blockPos : cand.obsidian()) {
                  if (endCrystalEntity.getBoundingBox().intersects(BlockEntityUtils.getBoxForBlockPos(blockPos))) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private List<CrystalStepPlanner.Cand> generateCandidates(
      WorldCollisionHelper blockPosHelperSolid,
      Path path,
      int posVecBound,
      boolean posVecFlag,
      Sample sample,
      Loadout loadout,
      PearlsPlaystyleData pearlsPlaystyleData,
      Predicate<Vec3d> predicate,
      TickStatusData tickStatusData,
      long posVecValue,
      int ping,
      boolean idxFeetFlag,
      int posVecValue3,
      Entity entity,
      Entity player
   ) {
      double posVecValue4 = 2.0;
      var hashMap = new HashMap<Long, double[]>();
      int posVecLimit = posVecFlag ? Math.min(30, path.getValue() - 1) : Math.min(posVecBound, 30);

      for (int i = posVecFlag ? 0 : 1; i <= posVecLimit; i += posVecFlag ? 31 : 2) {
         Sample dataForInt = path.getDataForInt(i);
         int floor5 = (int)Math.floor(dataForInt.x() - posVecValue4);
         int floor2 = (int)Math.floor(dataForInt.x() + posVecValue4);
         int floor6 = (int)Math.floor(dataForInt.z() - posVecValue4);
         int floor3 = (int)Math.floor(dataForInt.z() + posVecValue4);

         for (int j = floor5; j <= floor2; j++) {
            for (int k = floor6; k <= floor3; k++) {
               double posVecLimit4 = WorldCollisionHelper.getAmount(j + 0.5, k + 0.5, dataForInt.x(), dataForInt.z());
               if (!(posVecLimit4 > posVecValue4)) {
                  long long2 = BlockPos.asLong(j, 0, k);
                  double[] posVecValues = (double[])hashMap.get(long2);
                  if (posVecValues == null || posVecLimit4 < posVecValues[0]) {
                     hashMap.put(long2, new double[]{posVecLimit4, dataForInt.y(), j, k});
                  }
               }
            }
         }
      }

      java.util.List<double[]> arrayList = new ArrayList(hashMap.values());
      arrayList.sort(Comparator.comparingDouble(posVecValues2 -> posVecValues2[0]));
      if (((java.util.List) arrayList).size() > 25) {
         arrayList = ((java.util.List) arrayList).subList(0, 25);
      }

      boolean posVecFlag3 = pearlsPlaystyleData.buildObsidian() && loadout.obsidianSlot() >= 0;
      var arrayList2 = new ArrayList();

      for (double[] arrayItem : arrayList) {
         int x = (int)arrayItem[2];
         int z = (int)arrayItem[3];
         double posVecValue7 = arrayItem[1];
         double hashRegistryValue = posVecFlag ? posVecValue7 + 1.0E-6 : posVecValue7 - 0.2;
         double posVecValue8 = posVecValue7 - 6.0;
         int posVecValue9 = (int)Math.floor(hashRegistryValue) - 1;
         int posVecValue10 = (int)Math.floor(posVecValue8) - 2;
         int y = Integer.MIN_VALUE;

         for (int m = posVecValue9; m >= posVecValue10; m--) {
            if (blockPosHelperSolid.solid(x, m, z)) {
               y = m;
               break;
            }
         }

         if (y != Integer.MIN_VALUE && (!blockPosHelperSolid.solid(x, y + 1, z) || y + 1 > posVecValue9)) {
            var arrayList3 = new ArrayList(3);
            BlockPos blockPos = new BlockPos(x, y, z);
            BlockPos up = blockPos.up();
            BlockPos up2 = blockPos.up(2);
            if (posVecFlag3 && !idxFeetFlag) {
               int posVecLimit6 = y + 3;
               if (posVecLimit6 <= hashRegistryValue && posVecLimit6 >= posVecValue8 && blockPosHelperSolid.isReplaceableWithSupportBelow(up) && blockPosHelperSolid.isReplaceableAt(up2) && blockPosHelperSolid.isAirAt(up2.up()) && !blockPosHelperSolid.isSolidAt(up2)) {
                  arrayList3.add(new CrystalStepPlanner.Cand(up2, List.of(up, up2), posVecLimit6, false, false, -1, 0));
               }

               int posVecLimit7 = y + 2;
               if (posVecLimit7 <= hashRegistryValue && posVecLimit7 >= posVecValue8 && blockPosHelperSolid.isReplaceableWithSupportBelow(up)) {
                  arrayList3.add(new CrystalStepPlanner.Cand(up, List.of(up), posVecLimit7, false, false, -1, 0));
               }
            }

            int posVecLimit8 = y + 1;
            if (posVecLimit8 <= hashRegistryValue && posVecLimit8 >= posVecValue8 && blockPosHelperSolid.canPlaceOnBedrockObsidian(blockPos)) {
               EndCrystalEntity world = blockPosHelperSolid.findCrystalAt(blockPos);
               boolean equals = blockPos.equals(tickStatusData.base());
               boolean obsidianKind = isTrackedBaseMatching(blockPos, tickStatusData, CrystalStepPlanner::isMarkedByCrystalAura);
               boolean posVecFlag4 = obsidianKind
                  && tickStatusData.lastPlaceTick() != Long.MIN_VALUE
                  && (tickStatusData.lastBreakTick() == Long.MIN_VALUE || tickStatusData.lastPlaceTick() > tickStatusData.lastBreakTick());
               boolean posVecFlag5 = equals
                  && tickStatusData.lastBreakTick() != Long.MIN_VALUE
                  && (tickStatusData.lastPlaceTick() == Long.MIN_VALUE || tickStatusData.lastBreakTick() >= tickStatusData.lastPlaceTick());
               boolean posVecFlag6 = posVecFlag5 && posVecValue - tickStatusData.lastBreakTick() < ping + 1;
               boolean posVecFlag7 = world == null && posVecFlag4 && posVecValue - tickStatusData.lastPlaceTick() < ping + 1;
               boolean posVecFlag8 = world != null && !posVecFlag6 || posVecFlag7;
               if (posVecFlag8 || blockPosHelperSolid.isAirAt(up) || posVecFlag6) {
                  int posVecValue11 = posVecFlag7 ? (int)Math.max(0L, tickStatusData.lastPlaceTick() + ping - posVecValue) : -1;
                  int posVecValue12 = !posVecFlag8 && posVecFlag6 ? (int)Math.max(0L, tickStatusData.lastBreakTick() + ping - posVecValue) : 0;
                  arrayList3.add(new CrystalStepPlanner.Cand(blockPos, List.of(), posVecLimit8, true, posVecFlag8, posVecValue11, posVecValue12));
               }
            }

            if (idxFeetFlag && posVecFlag3 && !blockPosHelperSolid.canPlaceOnBedrockObsidian(blockPos)) {
               BlockPos pos = new BlockPos(x, posVecValue3 - 1, z);
               if (blockPosHelperSolid.isReplaceableWithSupportBelow(pos) && pos.getY() + 1 == posVecValue3) {
                  arrayList3.add(new CrystalStepPlanner.Cand(pos, List.of(pos), posVecValue3, false, false, -1, 0));
               }
            }

            int posVecCounter = 0;

            for (CrystalStepPlanner.Cand cand : (Iterable<com.corz.client.utils.CrystalStepPlanner.Cand>) (arrayList3)) {
               if (posVecCounter >= 2) {
                  break;
               }

               if (!idxFeetFlag || cand.top() == posVecValue3) {
                  Vec3d vec3d = BlockEntityUtils.getVec3d(cand.base());
                  if (!predicate.test(vec3d)
                     && !this.excludedBasePredicate.test(cand.base())
                     && !this.intersectsAvoidedPath(cand.base())
                     && !isBaseBlockedByEntity(blockPosHelperSolid, cand, entity, player)) {
                     arrayList2.add(cand);
                     posVecCounter++;
                  }
               }
            }
         }
      }

      return arrayList2;
   }

   static boolean isTrackedBaseMatching(BlockPos pos, TickStatusData tickStatusData, Predicate<BlockPos> predicate) {
      if (pos != null && tickStatusData != null && pos.equals(tickStatusData.base())) {
         return tickStatusData.kind() == com.corz.client.entity.ImmutablePos.Kind.OBSIDIAN ? false : predicate != null && predicate.test(pos);
      } else {
         return false;
      }
   }

   public CrystalStepPlanner.Step planBestStepAllowGrounded(
      long idxFeetValue,
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      MinVec minVec,
      PearlsPlaystyleData pearlsPlaystyleData,
      SelfHazData selfHazData,
      VecBlock vecBlock,
      List<VecBlockingData> list,
      int idxFeetBound,
      int idxFeetValue2,
      float idxFeetLimit,
      TickStatusData tickStatusData,
      Predicate<Vec3d> predicate,
      CrystalStepPlanner.Step step,
      boolean idxFeetFlag
   ) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && blockPosHelperSolid != null && blockPosHelperSolid.getWorld() != null && livingEntityHelperVisible != null && absorptionPosData != null && vecBlockingData != null && pearlsPlaystyleData != null) {
         TickStatusData tickStatusData2 = tickStatusData == null ? TickStatusData.INSTANCE : tickStatusData;
         blockPosHelperSolid.updateCacheTime(tickStatusData2.lastActionTick());
         Seed seed = livingEntityHelperVisible.getDataGroundMax();
         if (seed == null) {
            return null;
         } else {
            Loadout stackKnockback = VecPos.getStackKnockback(player, pearlsPlaystyleData.buildObsidian());
            if (stackKnockback.crystalSlot() < 0) {
               return null;
            } else {
               AmountAmounts amountAmounts = minVec != null ? minVec.getArmsAmountAmounts() : FALLBACK_AMOUNTS;
               Predicate predicate2 = predicate == null ? obj -> false : predicate;
               Sample sample = seed.sample();
               Vec3d vec3d = new Vec3d(sample.x(), sample.y(), sample.z());
               Vec3d vec3d2 = LivingEntityHelperVisible.getVec3d(absorptionPosData.pos().subtract(vec3d));
               Path sampleTick = livingEntityHelperVisible.getSampleTick(40, blockPosHelperSolid, amountAmounts, vec3d2, null);
               if (sampleTick == null) {
                  return null;
               } else {
                  int valueGround = sampleTick.getValueGround();
                  boolean posVecFlag = valueGround >= sampleTick.getValue() && sample.onGround();
                  if (posVecFlag && !idxFeetFlag) {
                     return null;
                  } else {
                     int topDistanceValue = seed.time().getMax();
                     int ping = seed.time().getPing();
                     int idxFeetValue4 = VecPosUtilsBreak.getValue(ping);
                     int max5 = BoxLengthUtils.getMax((int)Math.round(seed.time().pingMs()));
                     double pilotRenderLimit = posVecFlag ? sample.y() : sampleTick.getDataForInt(Math.min(valueGround, sampleTick.getValue() - 1)).y();
                     int index = pearlsPlaystyleData.playstyle() == null ? 0 : pearlsPlaystyleData.playstyle().ordinal();
                     Weights dataForDouble = Weights.getDataForDouble(selfHazData == null ? 1.0 : selfHazData.off(), selfHazData == null ? 1.0 : selfHazData.self(), ConstVec.idxFeetTable[index]);
                     Window window = new Window(
                        PendingEntityHitTracker.getRemainingTicksFloor(absorptionPosData.ticksSinceFresh(), absorptionPosData.loopMs()), absorptionPosData.lastScaledTaken()
                     );
                     Window window2 = new Window(Math.max(0, idxFeetBound), vecBlockingData.lastScaledTaken());
                     int idxFeetValue7 = tickStatusData2.lastPlaceTick() == Long.MIN_VALUE ? 100 : (int)Math.min(100L, Math.max(0L, idxFeetValue - tickStatusData2.lastPlaceTick()));
                     int idxFeetValue8 = vecBlockingData.lastPopTick() == Long.MIN_VALUE ? Integer.MAX_VALUE : (int)Math.min(2147483647L, idxFeetValue - vecBlockingData.lastPopTick());
                     double idxFeetValue9 = minVec != null ? minVec.getMaxMin().getBoomDistance() : 8.0;
                     int round10 = (int)Math.round(pilotRenderLimit);
                     var tickBreak = this.generateCandidates(
                        blockPosHelperSolid, sampleTick, valueGround, posVecFlag, sample, stackKnockback, pearlsPlaystyleData, predicate2, tickStatusData2, idxFeetValue, ping, idxFeetFlag, round10, vecBlockingData.entity(), player
                     );
                     var arrayList = new ArrayList(tickBreak.size());
                     Vec3d vec3d3 = absorptionPosData.eye() != null ? absorptionPosData.eye() : player.getEyePos();

                     for (CrystalStepPlanner.Cand cand : (Iterable<com.corz.client.utils.CrystalStepPlanner.Cand>) (tickBreak)) {
                        CrystalStepPlanner.Scored topDistance = this.scoreCandidate(
                           cand,
                           blockPosHelperSolid,
                           sampleTick,
                           valueGround,
                           posVecFlag,
                           idxFeetFlag,
                           topDistanceValue,
                           idxFeetValue4,
                           ping,
                           max5,
                           pilotRenderLimit,
                           dataForDouble,
                           absorptionPosData,
                           vecBlockingData,
                           seed,
                           amountAmounts,
                           window,
                           window2,
                           idxFeetLimit,
                           idxFeetBound,
                           idxFeetValue2,
                           idxFeetValue7,
                           stackKnockback,
                           player,
                           vec3d3,
                           absorptionPosData.pos(),
                           idxFeetValue8,
                           idxFeetValue9
                        );
                        if (topDistance != null) {
                           arrayList.add(topDistance);
                        }
                     }

                     arrayList.sort(Comparator.comparingDouble(CrystalStepPlanner.Scored::value).reversed());
                     if (step != null && step.base() != null) {
                        for (int i = 1; i < arrayList.size(); i++) {
                           if (((CrystalStepPlanner.Scored)arrayList.get(i)).c().base().equals(step.base())) {
                              arrayList.add(0, (CrystalStepPlanner.Scored)arrayList.remove(i));
                              break;
                           }
                        }
                     }

                     var arrayList2 = new ArrayList();
                     int idxFeetCounter = 4;
                     int idxFeetTotal = 400;
                     int idxFeetCounter2 = 0;
                     int idxFeetCounter3 = 0;
                     var hashMap = new HashMap();
                     var arrayList3 = new ArrayList();

                     for (CrystalStepPlanner.Scored scored : (Iterable<com.corz.client.utils.CrystalStepPlanner.Scored>) (arrayList)) {
                        if (idxFeetCounter2 >= 6 || idxFeetCounter3 >= 16) {
                           break;
                        }

                        idxFeetCounter3++;
                        boolean idxFeetFlag3 = idxFeetCounter2 == 0 && idxFeetTotal >= 72;
                        CrystalStepPlanner.Stand topFeet = this.pickBestStand(blockPosHelperSolid, scored.c(), absorptionPosData, idxFeetFlag3 ? vecBlock : null, list, selfHazData, idxFeetFlag);
                        if (topFeet != null) {
                           if (idxFeetFlag3) {
                              idxFeetTotal -= 72;
                           }

                           Vec3d add = topFeet.feet().add(0.0, 1.62, 0.0);
                           if (isStandWithinReach(scored.c(), scored.idx(), add)) {
                              idxFeetCounter2++;
                              double reachingFraction = EntityHeightSamples.getReachingFraction(topFeet.feet().y, scored.c().top());
                              Vec3d vec3d4 = BlockEntityUtils.getVec3d(scored.c().base());
                              float scaledValue = BlockEntityUtils.getAmount(player, topFeet.feet().distanceTo(vec3d4), 6.0F) * (float)reachingFraction;
                              float scaledInstance = PosBlockUtilsBonus.getScaledInstance(scaledValue, absorptionPosData.armor(), window, scored.idx());
                              float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(scaledValue, absorptionPosData.armor());
                              double expo = scored.expo();
                              float dmg = scored.dmg();
                              BlockPos blockPos = scored.c().existing() ? null : scored.c().base();
                              boolean idxFeetFlag4 = false;
                              if (idxFeetCounter > 0 && this.exactCheckLimiter != null && vecBlockingData.entity() != null) {
                                 int bound = 45 + (reachingFraction > 0.0 ? 45 : 0);
                                 if (bound <= idxFeetTotal && this.exactCheckLimiter.tryReserveRays(bound, 2)) {
                                    idxFeetTotal -= bound;
                                    idxFeetCounter--;
                                    idxFeetFlag4 = true;
                                    Sample dataForInt = sampleTick.getDataForInt(scored.idx());
                                    float idxFeetValue13 = BlockEntityUtils.getAmount(
                                       vec3d4, WorldCollisionHelper.getPlayerBoxAt(dataForInt.x(), dataForInt.y(), dataForInt.z(), 0.0), vecBlockingData.entity(), blockPos
                                    );
                                    expo = idxFeetValue13;
                                    if (reachingFraction > 0.0) {
                                       Box box = WorldCollisionHelper.getPlayerBoxAt(topFeet.feet().x, topFeet.feet().y, topFeet.feet().z, 0.0);
                                       float creativeSpectator = BlockEntityUtils.getCreativeSpectator(player, vec3d4, topFeet.feet(), box, blockPos, null, 6.0F);
                                       scaledInstance = PosBlockUtilsBonus.getScaledInstance(creativeSpectator, absorptionPosData.armor(), window, scored.idx());
                                       toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(creativeSpectator, absorptionPosData.armor());
                                    } else {
                                       scaledInstance = 0.0F;
                                       toughnessEpf = 0.0F;
                                    }
                                 }
                              }

                              if ((!(toughnessEpf > 0.0F) || !(toughnessEpf >= absorptionPosData.health() + absorptionPosData.absorption() - 1.0F))
                                 && scaledInstance <= idxFeetLimit
                                 && expo > 0.0) {
                                 Sample dataForInt3 = sampleTick.getDataForInt(scored.idx());
                                 float readyValue = BlockEntityUtils.getAmount(vecBlockingData.entity(), new Vec3d(dataForInt3.x(), dataForInt3.y(), dataForInt3.z()).distanceTo(vec3d4), 6.0F)
                                    * (float)expo;
                                 float posBlockLimit = PosBlockUtilsBonus.getReadyInstance(readyValue, vecBlockingData.armor(), window2, scored.idx()) * vecBlockingData.calibration();
                                 boolean canFlag = PosBlockUtilsBonus.canFlag(posBlockLimit, vecBlockingData.getHealthTrusted());
                                 boolean idxFeetFlag6 = canFlag && (vecBlockingData.awaitingTotem() || !vecBlockingData.totemInHand() || idxFeetValue8 < idxFeetValue9 - 1.0);
                                 double subtractExpl = this.computeStepValue(
                                    scored.c(),
                                    scored.idx(),
                                    expo,
                                    posBlockLimit,
                                    canFlag,
                                    idxFeetFlag6,
                                    scaledInstance,
                                    idxFeetLimit,
                                    sampleTick,
                                    valueGround,
                                    topDistanceValue,
                                    idxFeetValue4,
                                    pilotRenderLimit,
                                    seed,
                                    amountAmounts,
                                    topFeet.feet(),
                                    scored.margin(),
                                    dataForDouble
                                 );
                                 if (Double.isFinite(subtractExpl)) {
                                    Direction southNorth = getFaceDirectionToward(scored.c().base(), add);
                                    boolean idxFeetFlag7 = scored.c().armed() && scored.idx() > 0;
                                    CrystalStepPlanner.Step totalPriceData = new CrystalStepPlanner.Step(
                                       scored.c().base(),
                                       scored.c().obsidian(),
                                       scored.idx(),
                                       idxFeetValue + scored.idx(),
                                       scored.kind(),
                                       subtractExpl,
                                       canFlag,
                                       idxFeetFlag6,
                                       scaledInstance,
                                       topFeet.feet(),
                                       southNorth,
                                       idxFeetFlag7
                                    );
                                    arrayList2.add(totalPriceData);
                                    arrayList3.add(idxFeetFlag4);
                                 }
                              }
                           }
                        }
                     }

                     if (arrayList2.isEmpty()) {
                        return null;
                     } else {
                        boolean contains = arrayList3.contains(Boolean.TRUE);
                        var holdingBlast = capNonExactStepValues(arrayList2, arrayList3);

                        for (int j = 0; j < holdingBlast.size(); j++) {
                           if (!contains || Boolean.TRUE.equals(arrayList3.get(j))) {
                              hashMap.put(((CrystalStepPlanner.Step)holdingBlast.get(j)).base(), (CrystalStepPlanner.Step)holdingBlast.get(j));
                           }
                        }

                        holdingBlast.sort(Comparator.comparingDouble(CrystalStepPlanner.Step::value).reversed());
                        CrystalStepPlanner.Step idxFeet = (CrystalStepPlanner.Step)holdingBlast.get(0);
                        if (step != null && step.base() != null) {
                           CrystalStepPlanner.Step crystalStepPlannerStep = (CrystalStepPlanner.Step)hashMap.get(step.base());
                           if (crystalStepPlannerStep != null && !(idxFeet.value() > crystalStepPlannerStep.value() + 2.0)) {
                              return crystalStepPlannerStep;
                           }
                        }

                        return idxFeet;
                     }
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   public void setExcludedBasePredicate(Predicate<BlockPos> predicate) {
      this.excludedBasePredicate = predicate == null ? pos -> false : predicate;
   }

   private static boolean isStandWithinReach(CrystalStepPlanner.Cand cand, int distanceValue, Vec3d vec) {
      if (!cand.armed()) {
         for (BlockPos blockPos : cand.obsidian()) {
            if (vec.distanceTo(new Vec3d(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5)) > 4.25) {
               return false;
            }
         }

         if (vec.distanceTo(WorldCollisionHelper.getTopCenter(cand.base())) > 4.25 && !isWithinFaceReach(cand.base(), vec)) {
            return false;
         }
      }

      return BlockEntityUtils.getMinMax(vec, BlockEntityUtils.getBox(cand.base())) < 3.0;
   }

   static List<CrystalStepPlanner.Step> capNonExactStepValues(List<CrystalStepPlanner.Step> list, List<Boolean> list2) {
      double holdingBlastBound = Double.POSITIVE_INFINITY;

      for (int i = 0; i < list.size(); i++) {
         if (Boolean.TRUE.equals(list2.get(i))) {
            holdingBlastBound = Math.min(holdingBlastBound, ((CrystalStepPlanner.Step)list.get(i)).value());
         }
      }

      var arrayList = new ArrayList(list.size());

      for (int j = 0; j < list.size(); j++) {
         CrystalStepPlanner.Step crystalStepPlannerStep = (CrystalStepPlanner.Step)list.get(j);
         if (Double.isFinite(holdingBlastBound) && !Boolean.TRUE.equals(list2.get(j)) && crystalStepPlannerStep.value() >= holdingBlastBound) {
            crystalStepPlannerStep = new CrystalStepPlanner.Step(
               crystalStepPlannerStep.base(),
               crystalStepPlannerStep.obsidian(),
               crystalStepPlannerStep.blastIndex(),
               crystalStepPlannerStep.breakTick(),
               crystalStepPlannerStep.kind(),
               holdingBlastBound - 0.001,
               crystalStepPlannerStep.pops(),
               crystalStepPlannerStep.kills(),
               crystalStepPlannerStep.selfDamage(),
               crystalStepPlannerStep.stand(),
               crystalStepPlannerStep.face(),
               crystalStepPlannerStep.holding()
            );
         }

         arrayList.add(crystalStepPlannerStep);
      }

      return arrayList;
   }

   private CrystalStepPlanner.Stand pickBestStand(
      WorldCollisionHelper blockPosHelperSolid, CrystalStepPlanner.Cand cand, AbsorptionPosData absorptionPosData, VecBlock vecBlock, List<VecBlockingData> list, SelfHazData selfHazData, boolean topFeetFlag
   ) {
      Vec3d vec3d = new Vec3d(cand.base().getX() + 0.5, cand.top(), cand.base().getZ() + 0.5);
      var arrayList = new ArrayList(STAND_RING_RADII.length * 8 + 1);
      Vec3d pos = absorptionPosData.pos();
      arrayList.add(pos);

      for (double topFeetFactor : topFeetFlag ? new double[0] : STAND_RING_RADII) {
         for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2) * i / 8.0;
            arrayList.add(new Vec3d(vec3d.x + Math.cos(angle) * topFeetFactor, vec3d.y, vec3d.z + Math.sin(angle) * topFeetFactor));
         }
      }

      Box box = BlockEntityUtils.getBox3(cand.base());
      var arrayList2 = new ArrayList();

      for (int j = 0; j < arrayList.size(); j++) {
         Vec3d vec3d2 = (Vec3d)arrayList.get(j);
         Vec3d vec3d3 = j == 0 ? (!absorptionPosData.onGround() && !topFeetFlag ? null : pos) : findGroundBelow(blockPosHelperSolid, vec3d2, cand.top());
         if (vec3d3 != null && !(vec3d3.y > cand.top() - 1.0 + 1.0E-6)) {
            Box playerBoxAt = WorldCollisionHelper.getPlayerBoxAt(vec3d3.x, vec3d3.y, vec3d3.z, 0.0);
            if (!playerBoxAt.intersects(box)) {
               boolean topFeetFlag2 = false;

               for (BlockPos blockPos : cand.obsidian()) {
                  if (playerBoxAt.intersects(BlockEntityUtils.getBoxForBlockPos(blockPos))) {
                     topFeetFlag2 = true;
                     break;
                  }
               }

               if (!topFeetFlag2) {
                  if (j > 0) {
                     BlockPos floored = BlockPos.ofFloored(vec3d3.x, vec3d3.y + 0.01, vec3d3.z);
                     if (!BlockHazardFlags.isSafeToStand(blockPosHelperSolid.getWorld(), floored) || !blockPosHelperSolid.canWalkBetween(pos, vec3d3, 0)) {
                        continue;
                     }
                  }

                  double topFeetValue = EntityHeightSamples.getReachingFraction(vec3d3.y, cand.top());
                  double topFeetValue2 = vec3d3.y <= cand.top() - 1.6 ? 0.5 : 0.0;
                  double topFeetValue3 = 3.0 * (1.0 - topFeetValue) + topFeetValue2 - 0.5 * pos.distanceTo(vec3d3);
                  arrayList2.add(new CrystalStepPlanner.Stand(vec3d3, topFeetValue3));
               }
            }
         }
      }

      if (arrayList2.isEmpty()) {
         return null;
      } else {
         arrayList2.sort(Comparator.comparingDouble(CrystalStepPlanner.Stand::score).reversed());
         if (vecBlock != null && selfHazData != null) {
            CrystalStepPlanner.Stand topFeet = null;
            double negativeInfinity = Double.NEGATIVE_INFINITY;

            for (int k = 0; k < Math.min(3, arrayList2.size()); k++) {
               CrystalStepPlanner.Stand stand = (CrystalStepPlanner.Stand)arrayList2.get(k);
               Box playerBoxAt3 = WorldCollisionHelper.getPlayerBoxAt(stand.feet().x, stand.feet().y, stand.feet().z, 0.0);
               double topFeetLimit = stand.score() - selfHazData.def() * vecBlock.getMaxPower(stand.feet(), playerBoxAt3, list);
               if (topFeetLimit > negativeInfinity) {
                  negativeInfinity = topFeetLimit;
                  topFeet = new CrystalStepPlanner.Stand(stand.feet(), topFeetLimit);
               }
            }

            return topFeet;
         } else {
            return (CrystalStepPlanner.Stand)arrayList2.get(0);
         }
      }
   }

   private static Direction getFaceDirectionToward(BlockPos pos, Vec3d vec) {
      double southNorthValue = pos.getY() + 1.0;
      if (vec.y > southNorthValue + 0.05) {
         return Direction.UP;
      } else {
         double southNorthLimit = vec.x - (pos.getX() + 0.5);
         double spotValue = vec.z - (pos.getZ() + 0.5);
         if (Math.abs(southNorthLimit) >= Math.abs(spotValue)) {
            return southNorthLimit >= 0.0 ? Direction.EAST : Direction.WEST;
         } else {
            return spotValue >= 0.0 ? Direction.SOUTH : Direction.NORTH;
         }
      }
   }

   private static double predictHeightAboveBase(Path path, int posVecValue, int posVecValue2, int posVecLimit, Vec3d vec, Sample sample, int livingEntityValue, double rExpl, double posVecFactor) {
      if (posVecValue2 <= posVecLimit) {
         return path.getDataForInt(posVecValue + posVecValue2).y() - livingEntityValue;
      } else {
         int countSamplesAbove = EntityHeightSamples.countSamplesAbove(sample.y(), livingEntityValue);
         double vecPosFactor = (double)countSamplesAbove / EntityHeightSamples.HEIGHT_SAMPLE_OFFSETS.length;
         double[] posVecValues = VecPosUtilsAmount.getAmounts(sample.x(), sample.y(), sample.z(), 1.62, vec.x, vec.y, vec.z, 6.0, vecPosFactor, rExpl);
         Sample dataForInt = path.getDataForInt(posVecValue + posVecLimit);
         double posVecValue7 = dataForInt.vy() + posVecValues[1] * posVecFactor;
         return dataForInt.y() + PosVecUtils.getAmountDouble(posVecValue7, posVecValue2 - posVecLimit) - livingEntityValue;
      }
   }

   private boolean intersectsAvoidedPath(BlockPos pos) {
      if (this.avoidedPaths.isEmpty()) {
         return false;
      } else {
         Box expand = BlockEntityUtils.getBox(pos).expand(0.3);

         for (List list : this.avoidedPaths) {
            if (list != null) {
               for (int i = 0; i + 1 < list.size(); i++) {
                  Vec3d vec3d = (Vec3d)list.get(i);
                  Vec3d vec3d2 = (Vec3d)list.get(i + 1);
                  if (expand.contains(vec3d) || expand.contains(vec3d2) || expand.raycast(vec3d, vec3d2).isPresent()) {
                     return true;
                  }
               }

               if (list.size() == 1 && expand.contains((Vec3d)list.get(0))) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static boolean isMarkedByCrystalAura(BlockPos pos) {
      HandleTime handleTime = ModuleRegistry.getModule(CrystalAura.class) instanceof CrystalAura crystalAura ? crystalAura.getActionTracker() : null;
      return handleTime != null && handleTime.canTime(pos.asLong(), ConnectionTimeUtils.getTimeTimeMillis());
   }

   private static boolean canReachBaseTop(CrystalStepPlanner.Cand cand, Sample sample, double topValue, double[] topValues, double topFactor, int topValue2) {
      double topFactor2 = sample.vx() + topValues[0] * topFactor;
      double topFactor3 = sample.vz() + topValues[2] * topFactor;
      double flooredValue = cand.base().getX() + 0.5;
      double topValue4 = cand.base().getZ() + 0.5;

      for (int i = topValue2; i <= 2 * topValue2; i++) {
         double topFactor4 = (1.0 - Math.pow(0.91, i)) / 0.08999999999999997;
         double blockPosValue = sample.x() + topFactor2 * topFactor4;
         double z = sample.z() + topFactor3 * topFactor4;
         double topValue7 = sample.y() + PosVecUtils.getAmountDouble(topValue, i);
         if (WorldCollisionHelper.getAmount(blockPosValue, z, flooredValue, topValue4) <= 2.0 && topValue7 - cand.top() >= 1.0) {
            return true;
         }
      }

      return false;
   }

   private double computeStepValue(
      CrystalStepPlanner.Cand cand,
      int subtractExplValue,
      double vecPosFactor,
      float posBlockLimit,
      boolean subtractExplFlag,
      boolean subtractExplFlag2,
      float scaledInstance,
      float topDistanceLimit,
      Path path,
      int topDistanceBound,
      int topDistanceValue,
      int subtractExplValue8,
      double pilotRenderLimit,
      Seed seed,
      AmountAmounts amountAmounts,
      Vec3d vec,
      double positiveInfinity,
      Weights weights
   ) {
      Vec3d vec3d = BlockEntityUtils.getVec3d(cand.base());
      Sample sample = path.getDataForInt(subtractExplValue);
      double[] subtractExplValues = VecPosUtilsAmount.getAmounts(sample.x(), sample.y(), sample.z(), 1.62, vec3d.x, vec3d.y, vec3d.z, 6.0, vecPosFactor, seed.rExpl());
      double subtractExplFactor = amountAmounts.getMeleeScale();
      int subtractExplBound = subtractExplValue + topDistanceValue;
      Sample dataForInt = path.getDataForInt(subtractExplBound);
      double posVecValue = dataForInt.vy() + subtractExplValues[1] * subtractExplFactor;
      int rocketsBuyBound = topDistanceBound < path.getValue() ? topDistanceBound : subtractExplBound;
      int stepCount = WorldCollisionHelper.computeStepCount(dataForInt.y(), posVecValue, pilotRenderLimit, 60);
      int subtractExplValue13 = subtractExplBound + stepCount - Math.max(subtractExplBound, rocketsBuyBound);
      double adjustedValue = WorldCollisionHelper.computeAdjustedValue(dataForInt.y(), posVecValue, pilotRenderLimit);
      Vec3d vec3d2 = new Vec3d(sample.x(), sample.y(), sample.z());
      Vec3d vec3d3 = LivingEntityHelperVisible.getVec3d(vec.subtract(vec3d2));
      Vec3d vec3d4 = LivingEntityHelperVisible.getVec3d(new Vec3d(subtractExplValues[0], 0.0, subtractExplValues[2]));
      double subtractExplValue15 = Math.hypot(subtractExplValues[0], subtractExplValues[2]) > 0.1 ? vec3d4.x * vec3d3.x + vec3d4.z * vec3d3.z : 0.0;
      double maxSigma = Math.max(0.05, sample.sigma());
      double subtractExplValue16 = Double.isInfinite(positiveInfinity) ? 0.0 : WorldCollisionHelper.normalCdf(-positiveInfinity / maxSigma);
      boolean baseTop = canReachBaseTop(cand, dataForInt, posVecValue, subtractExplValues, subtractExplFactor, subtractExplValue8);
      return VecPosUtilsDamage.getSelfDamage(
         new Input(posBlockLimit, subtractExplFlag, subtractExplFlag2, subtractExplValue13, adjustedValue, subtractExplValue15, scaledInstance, topDistanceLimit, subtractExplValue, cand.armed() ? 0 : cand.obsidian().size(), subtractExplValue16, baseTop), weights
      );
   }

   public void setMaxCandidateLimit(int posVecBound) {
      this.maxCandidateLimit = Math.max(0, posVecBound);
   }

   public CrystalStepPlanner(RayBudget max) {
      this.exactCheckLimiter = max;
   }

   public void setAvoidedPaths(List<List<Vec3d>> list) {
      this.avoidedPaths = list == null ? List.of() : list;
   }

   private record Cand(BlockPos base, List<BlockPos> obsidian, int top, boolean existing, boolean armed, int pendingBreakAt, int placeAfter) {
   }

   private record Scored(
      CrystalStepPlanner.Cand c,
      int idx,
      Kind kind,
      double value,
      double expo,
      float dmg,
      boolean pops,
      boolean kills,
      float selfCost,
      double margin,
      Vec3d feet,
      int[] times
   ) {
   }

   private record Stand(Vec3d feet, double score) {
   }

   public record Step(
      BlockPos base,
      List<BlockPos> obsidian,
      int blastIndex,
      long breakTick,
      Kind kind,
      double value,
      boolean pops,
      boolean kills,
      float selfDamage,
      Vec3d stand,
      Direction face,
      boolean holding
   ) {
   }
}
