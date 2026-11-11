package com.corz.client.utils;

import com.corz.client.entity.BoxVisibilityRaycaster;
import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.PosVecUtils;
import com.corz.client.world.StateBlockUtilsCenter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.RayBudget;
import com.corz.client.utils.BlockHazardFlags;
import com.corz.client.utils.CombatMovementController;
import com.corz.client.utils.CellScoring;

public final class StandGoalPlanner {
   private DanceChaseType lastStance;
   private Supplier<List<VecBlockingData>> opponentsSupplier;
   public static final int[] FLOOR_SEARCH_OFFSETS = new int[]{1, 0, -1, -2};
   private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private float lastIncomingDamage;
   private long lastEngageTick;
   private DefensiveAggressiveType lastPosture;
   private final CombatMovementController.Env pathEnv = new CombatMovementController.McEnv();
   private final RayBudget armorSearchBudget;
   private Vec3d lastTargetPos;
   private DefendEngageType lastEngageMode;
   private final List<StandGoalPlanner.Base> crystalBases;
   private static final int[][] FACE_NORMALS = new int[][]{{0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}};
   private StandGoalPlanner.Goal still;

   private static Vec3d getNearestOpponentPos(Vec3d vec, List<VecBlockingData> list) {
      Vec3d serverPos = null;
      double serverPosLimit = Double.MAX_VALUE;

      for (VecBlockingData vecBlockingData : list) {
         Vec3d vec3d = vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos();
         if (vec3d != null) {
            double squaredDistanceTo = vec3d.squaredDistanceTo(vec);
            if (squaredDistanceTo < serverPosLimit) {
               serverPosLimit = squaredDistanceTo;
               serverPos = vec3d;
            }
         }
      }

      return serverPos;
   }

   private List<StandGoalPlanner.Cand> collectCandidateCells(
      World world,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      Vec3d vec,
      StandGoalPlanner.Band band,
      List<VecBlockingData> list,
      Predicate<Vec3d> predicate,
      BlockPos pos,
      VecBlock.Threat threat
   ) {
      ArrayList arrayList = new ArrayList();
      HashSet hashSet = new HashSet();
      Box box = vecBlockingData != null && vec != null ? getOpponentBoxAt(vecBlockingData, vec) : null;
      Vec3d pos2 = absorptionPosData.pos();
      if (band.flee()) {
         Vec3d vec3d = vec != null ? vec : getNearestOpponentPos(pos2, list);
         if (vec3d == null) {
            vec3d = pos2;
         }

         double[] serverPos = getFleeDirection(pos2, list, vecBlockingData, vec, threat);
         double angle = Math.atan2(serverPos[1], serverPos[0]);
         int fleeHealLimit = (int)Math.round(4.0);

         for (double fleeHealFactor : new double[]{8.0, 11.0, 14.0}) {
            for (int i = -fleeHealLimit; i <= fleeHealLimit; i++) {
               double angle2 = angle + Math.toRadians(i * 360.0 / 16.0);
               this.addColumnCandidate(
                  world,
                  arrayList,
                  hashSet,
                  vec3d.x + Math.cos(angle2) * fleeHealFactor,
                  vec3d.z + Math.sin(angle2) * fleeHealFactor,
                  pos.getY(),
                  pos.getY(),
                  band,
                  null,
                  list,
                  box,
                  predicate
               );
            }
         }
      } else if (band.heal()) {
         for (double fleeHealFactor2 : new double[]{1.5, 3.0, 4.0}) {
            for (int j = 0; j < 8; j++) {
               double radians = Math.toRadians(j * 360.0 / 8.0);
               this.addColumnCandidate(
                  world,
                  arrayList,
                  hashSet,
                  pos2.x + Math.cos(radians) * fleeHealFactor2,
                  pos2.z + Math.sin(radians) * fleeHealFactor2,
                  pos.getY(),
                  pos.getY(),
                  band,
                  null,
                  list,
                  box,
                  predicate
               );
            }
         }
      } else {
         int fleeHealValue = findGroundY(world, vec, pos.getY());
         this.addRingCandidates(world, arrayList, hashSet, vec, fleeHealValue, fleeHealValue, band, list, box, predicate);
         if (arrayList.size() < 4 && fleeHealValue != pos.getY()) {
            this.addRingCandidates(world, arrayList, hashSet, vec, pos.getY(), fleeHealValue, band, list, box, predicate);
         }

         if (arrayList.isEmpty()) {
            this.addLineCandidates(world, arrayList, hashSet, pos2, vec, fleeHealValue, pos.getY(), band, list, box, predicate);
         }
      }

      int contractIntersectsValue = !band.flee() && !band.heal() ? findGroundY(world, vec, pos.getY()) : pos.getY();
      if (!band.flee() || isFarFromOpponents(pos, list)) {
         this.addCandidateCell(world, arrayList, hashSet, pos, contractIntersectsValue, band, !band.flee() && !band.heal() ? vec : null, list, box, predicate);
      }

      if (this.still != null && this.still.cell() != null && (!band.flee() || isFarFromOpponents(this.still.cell(), list))) {
         this.addCandidateCell(world, arrayList, hashSet, this.still.cell(), contractIntersectsValue, band, !band.flee() && !band.heal() ? vec : null, list, box, predicate);
      }

      if (!band.flee()) {
         for (BlockStateUtilsDirections.Hole hole : BlockStateUtilsDirections.getDownSides(world, pos, 4, 6)) {
            this.addCandidateCell(world, arrayList, hashSet, hole.feet(), contractIntersectsValue, band, band.heal() ? null : vec, list, box, predicate);
         }
      }

      return arrayList;
   }

   private static float getArmorReducedDamage(double difficultyValue, float boxLengthBound, PosBlockUtilsBonus.Armor armor) {
      float posBlockValue = (float)BoxLengthUtils.getAmount3(difficultyValue, boxLengthBound, 6.0);
      return PosBlockUtilsBonus.getToughnessEpf(PosBlockUtilsBonus.getAmount(posBlockValue, armor.difficulty()), armor);
   }

   public StandGoalPlanner.Goal getCurrentGoal() {
      return this.still;
   }

   private static StandGoalPlanner.Goal createStayGoal(AbsorptionPosData absorptionPosData, DanceChaseType danceChaseType) {
      Vec3d pos = absorptionPosData.pos();
      BlockPos floored = BlockPos.ofFloored(pos.x, pos.y + 0.001, pos.z);
      return new StandGoalPlanner.Goal(new Vec3d(floored.getX() + 0.5, pos.y, floored.getZ() + 0.5), floored, 0.0, danceChaseType, "still");
   }

   private static double getNearestOpponentDistance(Vec3d vec, List<VecBlockingData> list) {
      double serverPos = Double.POSITIVE_INFINITY;

      for (VecBlockingData vecBlockingData : list) {
         Vec3d vec3d = vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos();
         if (vec3d != null) {
            serverPos = Math.min(serverPos, Math.hypot(vec3d.x - vec.x, vec3d.z - vec.z));
         }
      }

      return serverPos;
   }

   private static Box getOpponentBoxAt(VecBlockingData vecBlockingData, Vec3d vec) {
      double boundingValue = 0.3;
      double boundingValue2 = 1.8;
      if (vecBlockingData.entity() != null) {
         Box boundingBox = vecBlockingData.entity().getBoundingBox();
         boundingValue = (boundingBox.maxX - boundingBox.minX) * 0.5;
         boundingValue2 = boundingBox.maxY - boundingBox.minY;
      }

      return new Box(vec.x - boundingValue, vec.y, vec.z - boundingValue, vec.x + boundingValue, vec.y + boundingValue2, vec.z + boundingValue);
   }

   private void refreshCrystalBases(World world, SelfPopData selfPopData, VecBlockingData vecBlockingData, Vec3d vec, Predicate<Vec3d> predicate) {
      this.crystalBases.clear();
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity clientPlayerEntity = instance == null ? null : instance.player;
      if (clientPlayerEntity != null) {
         AbsorptionPosData self = selfPopData.self();
         boolean calibrationAirFlag = selfPopData.cfg() != null && selfPopData.cfg().buildObsidian() && self.obsidian() > 0 && clientPlayerEntity.canModifyBlocks();
         Box minBounding = getOpponentBoxAt(vecBlockingData, vec);
         PosBlockUtilsBonus.Armor armor = vecBlockingData.armor() != null ? vecBlockingData.armor() : PosBlockUtilsBonus.Armor.INSTANCE;
         float calibrationAirFactor = vecBlockingData.calibration() > 0.0F && Float.isFinite(vecBlockingData.calibration()) ? vecBlockingData.calibration() : 1.0F;
         BlockPos floored = BlockPos.ofFloored(vec.x, vec.y + 0.001, vec.z);
         ArrayList arrayList = new ArrayList();

         for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
               for (int k = -2; k <= 0; k++) {
                  BlockPos add = floored.add(i, k, j);
                  Vec3d vec3d = BlockEntityUtils.getVec3d(add);
                  double distanceTo = vec.distanceTo(vec3d);
                  if (!(distanceTo >= 12.0)) {
                     float calibrationAirLimit = getArmorReducedDamage(distanceTo, 1.0F, armor) * calibrationAirFactor;
                     if (calibrationAirLimit > 0.5F && !BlockEntityUtils.getBox3(add).intersects(minBounding) && (predicate == null || !predicate.test(vec3d))) {
                        boolean calibrationAirFlag2 = BlockEntityUtils.canBedrockObsidian(add) && (world.isAir(add.up()) || BlockEntityUtils.getNext(add) != null);
                        boolean calibrationAirFlag3 = !calibrationAirFlag2
                           && calibrationAirFlag
                           && world.getBlockState(add).isReplaceable()
                           && world.isAir(add.up())
                           && hasPlacementSupport(add)
                           && BlockEntityUtils.isEmpty(add);
                        if (calibrationAirFlag2 || calibrationAirFlag3) {
                           arrayList.add(
                              new StandGoalPlanner.Base(
                                 add,
                                 vec3d,
                                 BlockEntityUtils.getBox(add),
                                 new Vec3d(add.getX() + 0.5, add.getY() + 1.0, add.getZ() + 0.5),
                                 calibrationAirFlag3,
                                 calibrationAirLimit
                              )
                           );
                        }
                     }
                  }
               }
            }
         }

         arrayList.sort(Comparator.comparingDouble(obj -> -((com.corz.client.utils.StandGoalPlanner.Base) obj).targetDamage()));
         LivingEntity entity = vecBlockingData.entity();

         for (StandGoalPlanner.Base base : (Iterable<com.corz.client.utils.StandGoalPlanner.Base>) (arrayList)) {
            if (this.crystalBases.size() >= 6) {
               break;
            }

            float calibrationAirValue = this.armorSearchBudget != null && !this.armorSearchBudget.tryReserveRays(12, 4)
               ? 0.5F
               : BoxVisibilityRaycaster.computeVisibleFraction(base.center(), minBounding, entity, base.build() ? base.pos() : null, null);
            float calibrationAirValue2 = getArmorReducedDamage(vec.distanceTo(base.center()), calibrationAirValue, armor) * calibrationAirFactor;
            this.crystalBases.add(new StandGoalPlanner.Base(base.pos(), base.center(), base.box(), base.top(), base.build(), calibrationAirValue2));
         }

         this.crystalBases.sort(Comparator.comparingDouble(base2 -> -base2.targetDamage()));
      }
   }

   private static boolean isDefensiveOrEvasive(StandGoalPlanner.Band band) {
      return band.defensive() || band.flee() || band.heal();
   }

   private static void addAwayVector(double[] blockPosValues, Vec3d vec, Vec3d vec2, double blockPosValue) {
      if (vec2 != null) {
         double deltaX = vec.x - vec2.x;
         double deltaZ = vec.z - vec2.z;
         double hypot = Math.hypot(deltaX, deltaZ);
         if (!(hypot < 0.001) && Double.isFinite(hypot)) {
            double blockPosFactor = blockPosValue / Math.max(1.0, hypot);
            blockPosValues[0] += deltaX / hypot * blockPosFactor;
            blockPosValues[1] += deltaZ / hypot * blockPosFactor;
         }
      }
   }

   static boolean isAnyFaceVisible(Vec3d vec, BlockPos pos, double squaredDistanceFactor, RayBudget rayBudget, BiPredicate<Vec3d, Vec3d> biPredicate) {
      for (int[] squaredDistanceValues : FACE_NORMALS) {
         double x = pos.getX() + 0.5 + 0.5 * squaredDistanceValues[0];
         double y = pos.getY() + 0.5 + 0.5 * squaredDistanceValues[1];
         double z = pos.getZ() + 0.5 + 0.5 * squaredDistanceValues[2];
         if (!((vec.x - x) * squaredDistanceValues[0] + (vec.y - y) * squaredDistanceValues[1] + (vec.z - z) * squaredDistanceValues[2] <= 0.0)) {
            Vec3d vec3d = new Vec3d(x, y, z);
            if (!(vec.squaredDistanceTo(vec3d) > squaredDistanceFactor * squaredDistanceFactor)) {
               if (rayBudget != null && !rayBudget.tryReserveRays(1, 4)) {
                  return false;
               }

               if (biPredicate.test(vec, vec3d)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static boolean hasPlacementSupport(BlockPos pos) {
      if (BlockEntityUtils.canBlockSquare(pos.down(), Direction.UP)) {
         return true;
      } else {
         for (Direction direction : HORIZONTAL_DIRECTIONS) {
            if (BlockEntityUtils.canBlockSquare(pos.offset(direction), direction.getOpposite())) {
               return true;
            }
         }

         return false;
      }
   }

   private void addColumnCandidate(
      World world,
      List<StandGoalPlanner.Cand> list,
      Set<BlockPos> set,
      double blockPosValue,
      double hashRegistryValue,
      int idealValue,
      int contractIntersectsValue,
      StandGoalPlanner.Band band,
      Vec3d vec,
      List<VecBlockingData> list2,
      Box box,
      Predicate<Vec3d> predicate
   ) {
      int floorSeen = (int)Math.floor(blockPosValue);
      int z2 = (int)Math.floor(hashRegistryValue);

      for (int floorSearchOffsetsItem : FLOOR_SEARCH_OFFSETS) {
         BlockPos blockPos = new BlockPos(floorSeen, idealValue + floorSearchOffsetsItem, z2);
         if (StateBlockUtilsCenter.canDown(world, blockPos)) {
            this.addCandidateCell(world, list, set, blockPos, contractIntersectsValue, band, vec, list2, box, predicate);
            return;
         }
      }
   }

   static int findGroundY(World world, Vec3d vec, int blockPosValue) {
      int x = (int)Math.floor(vec.x);
      int z = (int)Math.floor(vec.z);
      int floor2 = (int)Math.floor(vec.y + 0.001);

      for (int i = floor2; i >= floor2 - 8; i--) {
         if (StateBlockUtilsCenter.canDown(world, new BlockPos(x, i, z))) {
            return i;
         }
      }

      return blockPosValue;
   }

   static boolean isCandidateInReach(StandGoalPlanner.Cand cand, Vec3d vec) {
      return cand != null && vec != null && IframeComboUtils.canFlag2(vec.y - cand.feetPos.y, cand.opponentDistance);
   }

   private List<VecBlockingData> getOpponents(VecBlockingData vecBlockingData) {
      List supplier = null;
      if (this.opponentsSupplier != null) {
         try {
            supplier = this.opponentsSupplier.get();
         } catch (RuntimeException e) {
            supplier = null;
         }
      }

      if (supplier != null) {
         if (vecBlockingData != null && !supplier.contains(vecBlockingData)) {
            ArrayList arrayList = new ArrayList(supplier.size() + 1);
            arrayList.addAll(supplier);
            arrayList.add(vecBlockingData);
            return arrayList;
         } else {
            return supplier;
         }
      } else {
         return vecBlockingData == null ? List.of() : List.of(vecBlockingData);
      }
   }

   private void addCandidateCell(
      World world,
      List<StandGoalPlanner.Cand> list,
      Set<BlockPos> set,
      BlockPos pos,
      int contractIntersectsValue,
      StandGoalPlanner.Band band,
      Vec3d vec,
      List<VecBlockingData> list2,
      Box box,
      Predicate<Vec3d> predicate
   ) {
      if (pos != null && set.add(pos)) {
         if (StateBlockUtilsCenter.canDown(world, pos)) {
            int stateBlock = BlockHazardFlags.getExtendedHazardFlags(world, pos);
            if ((stateBlock & 351) == 0) {
               Vec3d vec3d = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
               if (box == null || !getPlayerBoxAt(vec3d).contract(0.05).intersects(box)) {
                  if (predicate == null || !predicate.test(vec3d)) {
                     double stateMagma = getHazardPenalty(world, pos, stateBlock);
                     double sidesOpen = getHoleQuality(world, pos);
                     double hypot2;
                     if (!band.flee() && !band.heal()) {
                        hypot2 = Math.hypot(vec3d.x - vec.x, vec3d.z - vec.z);
                     } else {
                        hypot2 = getNearestOpponentDistance(vec3d, list2);
                     }

                     list.add(new StandGoalPlanner.Cand(pos, vec3d, pos.getY() - contractIntersectsValue, stateMagma, sidesOpen, hypot2));
                  }
               }
            }
         }
      }
   }

   private double getBestCrystalAdvantage(Vec3d vec, Vec3d vec2, Box box, float armorFactor, AbsorptionPosData absorptionPosData) {
      List damageMin = selectTopBases(this.crystalBases, vec, this.armorSearchBudget, BlockEntityUtils::canSubtractRaycast);
      if (damageMin.isEmpty()) {
         return 0.0;
      } else {
         boolean armorFlag = this.armorSearchBudget == null || this.armorSearchBudget.tryReserveRays(damageMin.size() * 12, 4);
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity clientPlayerEntity = instance == null ? null : instance.player;
         PosBlockUtilsBonus.Armor armor = absorptionPosData.armor() != null ? absorptionPosData.armor() : PosBlockUtilsBonus.Armor.INSTANCE;
         double maxArmor = 0.0;

         for (StandGoalPlanner.Base base : (Iterable<com.corz.client.utils.StandGoalPlanner.Base>) (damageMin)) {
            double distanceTo = vec2.distanceTo(base.center());
            float armorFactor2;
            if (distanceTo >= 12.0) {
               armorFactor2 = 0.0F;
            } else {
               float armorValue = armorFlag && clientPlayerEntity != null
                  ? BoxVisibilityRaycaster.computeVisibleFraction(base.center(), box, clientPlayerEntity, base.build() ? base.pos() : null, null)
                  : 1.0F;
               armorFactor2 = getArmorReducedDamage(distanceTo, armorValue, armor);
            }

            maxArmor = Math.max(maxArmor, (double)(base.targetDamage() - armorFactor * armorFactor2));
         }

         return maxArmor;
      }
   }

   static Vec3d predictOpponentPos(World world, VecBlockingData vecBlockingData) {
      Vec3d vec3d = vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos();
      if (vec3d == null) {
         return null;
      } else {
         PosVecUtils.Terrain terrain = (worldx, y2, z2) -> {
            BlockPos blockPos = new BlockPos(worldx, y2, z2);
            return !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
         };
         return predictLandingPos(terrain, vec3d, vecBlockingData.velocity(), vecBlockingData.onGround());
      }
   }

   private void addRingCandidates(
      World world,
      List<StandGoalPlanner.Cand> list,
      Set<BlockPos> set,
      Vec3d vec,
      int idealValue,
      int contractIntersectsValue,
      StandGoalPlanner.Band band,
      List<VecBlockingData> list2,
      Box box,
      Predicate<Vec3d> predicate
   ) {
      for (double idealFactor : new double[]{band.min(), band.ideal(), band.max()}) {
         for (int i = 0; i < 16; i++) {
            double angle = Math.toRadians(i * 360.0 / 16.0);
            this.addColumnCandidate(
               world, list, set, vec.x + Math.cos(angle) * idealFactor, vec.z + Math.sin(angle) * idealFactor, idealValue, contractIntersectsValue, band, vec, list2, box, predicate
            );
         }
      }
   }

   public StandGoalPlanner.Goal chooseGoal(long holeValue, SelfPopData selfPopData, ComboTradeData comboTradeData, VecBlock vecBlock, Predicate<Vec3d> predicate) {
      CenterVecData.Stand comboYield = CombatMovementController.getActiveComboStand(comboTradeData);
      if (comboYield != null) {
         Vec3d vec3d = comboYield.feet() != null ? comboYield.feet() : (selfPopData != null && selfPopData.self() != null ? selfPopData.self().pos() : null);
         if (vec3d != null) {
            StandGoalPlanner.Goal still = new StandGoalPlanner.Goal(
               vec3d, BlockPos.ofFloored(vec3d), 0.0, comboTradeData.stance(), comboYield.kind() == null ? CenterVecData.StandKind.HOLD.name() : comboYield.kind().name()
            );
            this.still = still;
            this.lastEngageTick = Long.MIN_VALUE;
            return still;
         }
      }

      if (selfPopData != null && comboTradeData != null && selfPopData.self() != null && selfPopData.self().pos() != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientWorld clientWorld = instance == null ? null : instance.world;
         if (clientWorld == null) {
            return this.still;
         } else {
            AbsorptionPosData self = selfPopData.self();
            VecBlockingData target = selfPopData.target();
            DanceChaseType danceChaseType = comboTradeData.stance() == null ? DanceChaseType.STILL : comboTradeData.stance();
            DefendEngageType mode = comboTradeData.mode();
            SelfHazData selfHazData = comboTradeData.weights() != null ? comboTradeData.weights() : SelfHazData.getSelfHaz(comboTradeData.posture(), selfPopData.cfg() != null ? selfPopData.cfg().playstyle() : null);
            List supplier = this.getOpponents(target);
            Vec3d vec3d2 = target != null ? predictOpponentPos(clientWorld, target) : null;
            float holeLimit = selfPopData.threat() == null ? 0.0F : selfPopData.threat().maxIncoming();
            StandGoalPlanner.Band healMelee = selectBand(danceChaseType, mode, comboTradeData.lane(), target, vec3d2, comboTradeData.posture(), supplier);
            if (healMelee == null) {
               StandGoalPlanner.Goal stayGoal = createStayGoal(self, danceChaseType);
               this.still = stayGoal;
               this.rememberEngagement(holeValue, mode, comboTradeData.posture(), danceChaseType, vec3d2, holeLimit);
               this.lastEngageTick = Long.MIN_VALUE;
               return stayGoal;
            } else {
               boolean holeFlag = this.still == null
                  || this.lastEngageTick == Long.MIN_VALUE
                  || holeValue - this.lastEngageTick >= 4L
                  || mode != this.lastEngageMode
                  || comboTradeData.posture() != this.lastPosture
                  || danceChaseType != this.lastStance
                  || holeLimit >= this.lastIncomingDamage + 4.0F
                  || vec3d2 != null && (this.lastTargetPos == null || vec3d2.distanceTo(this.lastTargetPos) > 0.75)
                  || !"still".equals(this.still.why()) && !this.isCellStillValid(clientWorld, this.still.cell(), target, vec3d2, predicate);
               if (!holeFlag) {
                  return this.still;
               } else {
                  this.rememberEngagement(holeValue, mode, comboTradeData.posture(), danceChaseType, vec3d2, holeLimit);
                  BlockPos floored = BlockPos.ofFloored(self.pos().x, self.pos().y + 0.001, self.pos().z);
                  List fleeHeal = this.collectCandidateCells(clientWorld, self, target, vec3d2, healMelee, supplier, predicate, floored, selfPopData.threat());
                  if (fleeHeal.isEmpty()) {
                     StandGoalPlanner.Goal stayGoal3 = createStayGoal(self, danceChaseType);
                     this.still = stayGoal3;
                     return stayGoal3;
                  } else {
                     boolean holeFlag2 = !healMelee.flee() && !healMelee.heal() && !healMelee.defensive() && vec3d2 != null;

                     for (StandGoalPlanner.Cand cand : (Iterable<com.corz.client.utils.StandGoalPlanner.Cand>) (fleeHeal)) {
                        cand.nearCurrentGoal = this.still != null && this.still.feet() != null && cand.feetPos.distanceTo(this.still.feet()) <= 0.75;
                        cand.isCurrentGoal = this.still != null && cand.blockPos.equals(this.still.cell());
                        cand.viable = !holeFlag2 || isCandidateInReach(cand, vec3d2);
                        double hypot = Math.hypot(cand.feetPos.x - self.pos().x, cand.feetPos.z - self.pos().z);
                        cand.pathCost = hypot / (hypot > 2.0 ? 0.26 : 0.2);
                        cand.heightAboveFloor = Math.max(0, cand.blockPos.getY() - floored.getY());
                        cand.preliminaryScore = CellScoring.scoreDefensiveCell(
                           0.0,
                           0.0,
                           cand.pathCost,
                           cand.heightAboveFloor,
                           cand.opponentDistance,
                           healMelee.min(),
                           healMelee.max(),
                           !healMelee.flee() && !healMelee.heal() ? cand.groundOffset : 0,
                           cand.terrainPenalty,
                           cand.holeQuality,
                           cand.nearCurrentGoal,
                           selfHazData,
                           healMelee.defensive()
                        );
                     }

                     fleeHeal.sort(Comparator.<StandGoalPlanner.Cand, Boolean>comparing(cand2 -> !cand2.viable).thenComparingDouble(cand3 -> -cand3.preliminaryScore));
                     ArrayList arrayList = new ArrayList(Math.min(fleeHeal.size(), 13));

                     for (StandGoalPlanner.Cand totalPriceData : (Iterable<com.corz.client.utils.StandGoalPlanner.Cand>) (fleeHeal)) {
                        if (arrayList.size() >= 12) {
                           break;
                        }

                        arrayList.add(totalPriceData);
                     }

                     for (StandGoalPlanner.Cand downOffset : (Iterable<com.corz.client.utils.StandGoalPlanner.Cand>) (fleeHeal)) {
                        if (downOffset.isCurrentGoal && !arrayList.contains(downOffset)) {
                           arrayList.add(downOffset);
                        }
                     }

                     estimatePathCosts(this.pathEnv, self.pos(), floored, arrayList);
                     if (healMelee.offense() && target != null && vec3d2 != null) {
                        this.refreshCrystalBases(clientWorld, selfPopData, target, vec3d2, predicate);
                     } else {
                        this.crystalBases.clear();
                     }

                     float armorFactor = Float.isFinite(comboTradeData.selfWeight()) ? comboTradeData.selfWeight() : selfHazData.self();
                     List list = isDefensiveOrEvasive(healMelee) ? supplier : null;
                     StandGoalPlanner.Cand cand6 = null;
                     StandGoalPlanner.Cand cand7 = null;

                     for (StandGoalPlanner.Cand cand8 : (Iterable<com.corz.client.utils.StandGoalPlanner.Cand>) (arrayList)) {
                        if (!Double.isNaN(cand8.pathCost)) {
                           Vec3d add = cand8.feetPos.add(0.0, 1.62, 0.0);
                           Box box = getPlayerBoxAt(cand8.feetPos);
                           double defHazFactor = healMelee.offense() && !this.crystalBases.isEmpty() ? this.getBestCrystalAdvantage(add, cand8.feetPos, box, armorFactor, self) : 0.0;
                           double holeValue4 = vecBlock != null ? vecBlock.getMaxPower(cand8.feetPos, box, list) : 0.0;
                           cand8.score = CellScoring.scoreDefensiveCell(
                              defHazFactor,
                              holeValue4,
                              cand8.pathCost,
                              cand8.heightAboveFloor,
                              cand8.opponentDistance,
                              healMelee.min(),
                              healMelee.max(),
                              !healMelee.flee() && !healMelee.heal() ? cand8.groundOffset : 0,
                              cand8.terrainPenalty,
                              cand8.holeQuality,
                              cand8.nearCurrentGoal,
                              selfHazData,
                              healMelee.defensive()
                           );
                           if (Double.isFinite(cand8.score)) {
                              if (cand6 == null || isBetterCandidate(cand8, cand6)) {
                                 cand6 = cand8;
                              }

                              if (cand8.isCurrentGoal) {
                                 cand7 = cand8;
                              }
                           }
                        }
                     }

                     if (cand6 != null) {
                        StandGoalPlanner.Cand cand9 = cand7 != null && (cand7.viable || !cand6.viable) && cand6.score <= cand7.score + 0.5
                           ? cand7
                           : cand6;
                        this.still = new StandGoalPlanner.Goal(
                           cand9.feetPos, cand9.blockPos, cand9.score, danceChaseType, cand9.holeQuality >= 1.0 ? "hole" : healMelee.why()
                        );
                        return this.still;
                     } else if (this.still != null && this.isCellStillValid(clientWorld, this.still.cell(), target, vec3d2, predicate)) {
                        return this.still;
                     } else {
                        StandGoalPlanner.Goal stayGoal4 = createStayGoal(self, danceChaseType);
                        this.still = stayGoal4;
                        return stayGoal4;
                     }
                  }
               }
            }
         }
      } else {
         return this.still;
      }
   }

   private static double getHazardPenalty(World world, BlockPos pos, int magmaValue) {
      double stateMagma = 0.0;
      if ((magmaValue & 128) != 0) {
         stateMagma += 0.5;
      }

      if ((magmaValue & 32) != 0) {
         stateMagma += 0.8;
      }

      if ((magmaValue & 512) != 0) {
         stateMagma++;
      }

      boolean magmaFlag = false;
      boolean magmaFlag2 = false;

      for (Direction direction : HORIZONTAL_DIRECTIONS) {
         BlockPos offset = pos.offset(direction);
         BlockState blockState = world.getBlockState(offset);
         BlockState blockState2 = world.getBlockState(offset.down());
         if (blockState.getFluidState().isIn(FluidTags.LAVA) || blockState2.getFluidState().isIn(FluidTags.LAVA)) {
            magmaFlag = true;
         }

         if (blockState.isIn(BlockTags.FIRE) || blockState.isOf(Blocks.MAGMA_BLOCK) || blockState2.isOf(Blocks.MAGMA_BLOCK)) {
            magmaFlag2 = true;
         }
      }

      if (magmaFlag) {
         stateMagma += 3.0;
      }

      if (magmaFlag2) {
         stateMagma += 2.0;
      }

      return stateMagma;
   }

   public StandGoalPlanner(RayBudget max) {
      this.lastEngageTick = Long.MIN_VALUE;
      this.crystalBases = new ArrayList<>();
      this.armorSearchBudget = max;
   }

   static List<StandGoalPlanner.Base> selectTopBases(List<StandGoalPlanner.Base> list, Vec3d vec, RayBudget rayBudget, BiPredicate<Vec3d, Vec3d> biPredicate) {
      StandGoalPlanner.Base base = null;
      StandGoalPlanner.Base base2 = null;

      for (StandGoalPlanner.Base base3 : list) {
         Box box = base3.box();
         if (!(BoxLengthUtils.getAmount(vec.x, vec.y, vec.z, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ) >= 3.0)
            && !(vec.distanceTo(base3.top()) > 4.4)
            && (base2 == null || base3.targetDamage() > base2.targetDamage())
            && isAnyFaceVisible(vec, base3.pos(), 4.4, rayBudget, biPredicate)) {
            if (base != null && !(base3.targetDamage() > base.targetDamage())) {
               base2 = base3;
            } else {
               base2 = base;
               base = base3;
            }
         }
      }

      if (base == null) {
         return List.of();
      } else {
         return base2 == null ? List.of(base) : List.of(base, base2);
      }
   }

   static void estimatePathCosts(CombatMovementController.Env env, Vec3d vec, BlockPos pos2, List<StandGoalPlanner.Cand> list) {
      int vecCounter = 0;

      for (StandGoalPlanner.Cand cand : list) {
         if (!cand.blockPos.equals(pos2)) {
            double hypot = Math.hypot(cand.feetPos.x - vec.x, cand.feetPos.z - vec.z);
            if (!(hypot > 16.0) && !CombatMovementController.canWalkStraightToHeight(env, vec, cand.feetPos, true, 16.0)) {
               if (hypot <= 3.0) {
                  cand.pathCost = Double.NaN;
               } else if (vecCounter >= 3) {
                  cand.pathCost += 20.0;
               } else {
                  vecCounter++;
                  List walkPath = env.findWalkPath(pos2, cand.blockPos);
                  if (walkPath == null) {
                     cand.pathCost = Double.NaN;
                  } else {
                     int max = 0;
                     int y = pos2.getY();

                     for (BlockPos blockPos : (Iterable<net.minecraft.util.math.BlockPos>) (walkPath)) {
                        if (blockPos.getY() > y) {
                           max++;
                        }

                        y = blockPos.getY();
                     }

                     cand.pathCost = walkPath.size() / 0.26;
                     cand.heightAboveFloor = max;
                  }
               }
            }
         }
      }
   }

   public void reset() {
      this.still = null;
      this.lastEngageTick = Long.MIN_VALUE;
      this.lastTargetPos = null;
      this.lastIncomingDamage = 0.0F;
      this.lastEngageMode = null;
      this.lastPosture = null;
      this.lastStance = null;
      this.crystalBases.clear();
   }

   private void addLineCandidates(
      World world,
      List<StandGoalPlanner.Cand> list,
      Set<BlockPos> set,
      Vec3d vec,
      Vec3d vec2,
      int idealValue,
      int idealValue2,
      StandGoalPlanner.Band band,
      List<VecBlockingData> list2,
      Box box,
      Predicate<Vec3d> predicate
   ) {
      double[] idealValues = CombatMovementController.normalizeXZ(vec.x - vec2.x, vec.z - vec2.z);
      if (idealValues != null) {
         double hypot = Math.hypot(vec.x - vec2.x, vec.z - vec2.z);

         for (double ideal = band.ideal(); ideal <= hypot && list.size() < 6; ideal++) {
            double blockPosValue = vec2.x + idealValues[0] * ideal;
            double hashRegistryValue = vec2.z + idealValues[1] * ideal;
            int bandSize = list.size();
            this.addColumnCandidate(world, list, set, blockPosValue, hashRegistryValue, idealValue2, idealValue, band, vec2, list2, box, predicate);
            if (list.size() == bandSize) {
               this.addColumnCandidate(world, list, set, blockPosValue, hashRegistryValue, idealValue2 - FLOOR_SEARCH_OFFSETS.length, idealValue, band, vec2, list2, box, predicate);
            }
         }
      }
   }

   static boolean isFarFromOpponents(BlockPos pos, List<VecBlockingData> list) {
      Vec3d vec3d = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
      return !(getNearestOpponentDistance(vec3d, list) < 8.0);
   }

   static Vec3d predictLandingPos(PosVecUtils.Terrain terrain, Vec3d vec, Vec3d vec2, boolean vec3dFlag) {
      if (vec == null) {
         return null;
      } else {
         PosVecUtils.Sample sample = PosVecUtils.getGround(createMotionSample(vec, vec2, vec3dFlag), 6, terrain, 0.0, 0.0, true, 0.0, null).getDataForInt(6);
         Vec3d vec3d = new Vec3d(sample.x(), sample.y(), sample.z());
         return Double.isFinite(vec3d.x) && Double.isFinite(vec3d.y) && Double.isFinite(vec3d.z) ? vec3d : vec;
      }
   }

   static boolean isBetterCandidate(StandGoalPlanner.Cand cand, StandGoalPlanner.Cand cand2) {
      return cand.viable != cand2.viable ? cand.viable : cand.score > cand2.score;
   }

   private static double[] getFleeDirection(Vec3d vec, List<VecBlockingData> list, VecBlockingData vecBlockingData, Vec3d vec2, VecBlock.Threat threat) {
      double[] doubles = new double[2];

      for (VecBlockingData vecBlockingData2 : list) {
         Vec3d vec3d = vecBlockingData2 == vecBlockingData && vec2 != null ? vec2 : (vecBlockingData2.serverPos() != null ? vecBlockingData2.serverPos() : vecBlockingData2.pos());
         addAwayVector(doubles, vec, vec3d, 1.0);
      }

      if (threat != null) {
         addAwayVector(doubles, vec, threat.liveCenter(), 0.5);
         BlockPos worstBase = threat.worstBase();
         if (worstBase != null) {
            addAwayVector(doubles, vec, BlockEntityUtils.getVec3d(worstBase), 0.5);
         }
      }

      double serverPosValue = doubles[0];
      double deltaZ = doubles[1];
      double hypot = Math.hypot(serverPosValue, deltaZ);
      return hypot < 1.0E-6 ? new double[]{1.0, 0.0} : new double[]{serverPosValue / hypot, deltaZ / hypot};
   }

   private boolean isCellStillValid(World world, BlockPos pos, VecBlockingData vecBlockingData, Vec3d vec, Predicate<Vec3d> predicate) {
      if (pos != null && StateBlockUtilsCenter.canDown(world, pos)) {
         if ((BlockHazardFlags.getExtendedHazardFlags(world, pos) & 351) != 0) {
            return false;
         } else {
            Vec3d vec3d = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            return vecBlockingData != null && vec != null && getPlayerBoxAt(vec3d).contract(0.05).intersects(getOpponentBoxAt(vecBlockingData, vec))
               ? false
               : predicate == null || !predicate.test(vec3d);
         }
      } else {
         return false;
      }
   }

   static PosVecUtils.Sample createMotionSample(Vec3d vec, Vec3d vec2, boolean zeroFlag) {
      Vec3d vec3d = vec2 != null ? vec2 : Vec3d.ZERO;
      double zeroValue = zeroFlag ? -0.0784 : (vec3d.y - 0.08) * 0.98;
      return new PosVecUtils.Sample(vec.x, vec.y, vec.z, vec3d.x, zeroValue, vec3d.z, zeroFlag, 0.0);
   }

   private static Box getPlayerBoxAt(Vec3d vec) {
      return new Box(vec.x - 0.3, vec.y, vec.z - 0.3, vec.x + 0.3, vec.y + 1.8, vec.z + 0.3);
   }

   private static double getHoleQuality(World world, BlockPos pos) {
      BlockStateUtilsDirections.Hole bedrockState = BlockStateUtilsDirections.getBedrockState(world, pos);
      if (bedrockState == null || bedrockState.type() == BlockStateUtilsDirections.Type.NONE) {
         return 0.0;
      } else if (bedrockState.openSides() == 0) {
         return 1.0;
      } else {
         return bedrockState.openSides() == 1 ? 0.4 : 0.0;
      }
   }

   private static StandGoalPlanner.Band selectBand(
      DanceChaseType danceChaseType,
      DefendEngageType defendEngageType,
      SurroundKitType surroundKitType,
      VecBlockingData vecBlockingData,
      Vec3d vec,
      DefensiveAggressiveType defensiveAggressiveType,
      List<VecBlockingData> list
   ) {
      if (danceChaseType == DanceChaseType.FLEE) {
         return vec == null && list.isEmpty() ? null : new StandGoalPlanner.Band("flee", 8.0, 11.0, Double.POSITIVE_INFINITY, false, true, true, false);
      } else if (danceChaseType == DanceChaseType.ENTER_HOLE || defendEngageType == DefendEngageType.HEAL) {
         return new StandGoalPlanner.Band("heal", 0.0, 0.0, Double.POSITIVE_INFINITY, false, true, false, true);
      } else if (danceChaseType == DanceChaseType.STILL || danceChaseType == DanceChaseType.LOOT) {
         return null;
      } else if (vecBlockingData != null && vec != null) {
         boolean healMeleeFlag = defensiveAggressiveType == DefensiveAggressiveType.DEFENSIVE || danceChaseType == DanceChaseType.COVER;
         if (defendEngageType == DefendEngageType.MELEE || surroundKitType == SurroundKitType.MELEE) {
            return new StandGoalPlanner.Band("melee", 1.8, 2.3, 2.8, false, healMeleeFlag, false, false);
         } else {
            return defendEngageType != DefendEngageType.ANCHOR && surroundKitType != SurroundKitType.ANCHOR
               ? new StandGoalPlanner.Band("crystal", 2.4, 3.2, 4.0, true, healMeleeFlag, false, false)
               : new StandGoalPlanner.Band("anchor", 2.2, 3.0, 3.8, false, healMeleeFlag, false, false);
         }
      } else {
         return danceChaseType == DanceChaseType.COVER ? new StandGoalPlanner.Band("heal", 0.0, 0.0, Double.POSITIVE_INFINITY, false, true, false, true) : null;
      }
   }

   public void setOpponentsSupplier(Supplier<List<VecBlockingData>> supplier) {
      this.opponentsSupplier = supplier;
   }

   private void rememberEngagement(long time, DefendEngageType defendEngageType, DefensiveAggressiveType defensiveAggressiveType, DanceChaseType danceChaseType, Vec3d vec3d, float amount) {
      this.lastEngageTick = time;
      this.lastEngageMode = defendEngageType;
      this.lastPosture = defensiveAggressiveType;
      this.lastStance = danceChaseType;
      this.lastTargetPos = vec3d;
      this.lastIncomingDamage = amount;
   }

   private record Band(String why, double min, double ideal, double max, boolean offense, boolean defensive, boolean flee, boolean heal) {
   }

   record Base(BlockPos pos, Vec3d center, Box box, Vec3d top, boolean build, float targetDamage) {
   }

   static final class Cand {
      final BlockPos blockPos;
      final double opponentDistance;
      double score;
      final Vec3d feetPos;
      int heightAboveFloor;
      boolean isCurrentGoal;
      final int groundOffset;
      final double terrainPenalty;
      double preliminaryScore;
      boolean viable = true;
      double pathCost;
      final double holeQuality;
      boolean nearCurrentGoal;

      Cand(BlockPos blockPos, Vec3d vec3d, int value, double amount, double holeAmount, double hypot) {
         this.score = Double.NEGATIVE_INFINITY;
         this.blockPos = blockPos;
         this.feetPos = vec3d;
         this.groundOffset = value;
         this.terrainPenalty = amount;
         this.holeQuality = holeAmount;
         this.opponentDistance = hypot;
      }
   }

   public record Goal(Vec3d feet, BlockPos cell, double score, DanceChaseType stance, String why) {
   }
}
