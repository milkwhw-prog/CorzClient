package com.corz.client.utils;

import com.corz.client.entity.ImmutablePos;
import com.corz.client.entity.UnsafeEntityUtils;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.EmptyLvl;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.TickStatusData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.PreferAnchorData;
import com.corz.client.utils.ActionPickRules;

public final class TerminatorCrystalPlanner {
   private Vec3d lastAimPoint;
   private float chosenTargetDamage;
   private BlockPos snapBasePos;
   private final EmptyLvl emptyLvl = new EmptyLvl();
   private String candidateStatusSuffix;
   private final Map<Long, Long> blockedPosExpiryTickByPos = new HashMap<>();
   private int lastTargetEntityId;
   public static final String KF_COMMAND = "/kf";
   private String statusLabel;
   public static final String CRYSTAL = "crystal";
   private long lastAimTick;
   private float snapYaw;
   public static final Set<String> BLOCKED_DIRECTIVE_STATUSES = Set.of("far", "unsafe", "blocked", "no base", "can't build", "ignored", "loot");
   public static final String UNSAFE = "unsafe";
   public static final String TERMINATOR = "Terminator";
   private long snapActionTick;
   private BlockPos chosenBasePos;
   private double chosenScore = Double.NEGATIVE_INFINITY;
   private int stuckTickCount;
   private float snapPitch;

   private static CenterVecData.CrystalStep toCrystalStep(TerminatorCrystalPlanner.Choice choice) {
      boolean kindFlag = choice.kind() == ActionPickRules.CrystalStepKind.OBSIDIAN;
      BlockEntityUtils.Support support = kindFlag ? choice.support() : null;
      boolean obsidianPlace = isPlacementKind(choice.kind());
      return new CenterVecData.CrystalStep(
         choice.base(), kindFlag ? choice.base() : null, null, obsidianPlace, !obsidianPlace, true, null, null, support == null ? null : support.pos(), support == null ? null : support.face()
      );
   }

   static double getFloorCollision(World world, Vec3d vec) {
      if (world != null && vec != null) {
         int floorCollisionValue = MathHelper.floor(vec.x);
         int floor = MathHelper.floor(vec.z);
         int floor3 = MathHelper.floor(vec.y + 0.001);
         Mutable mutable = new Mutable();

         for (int i = 0; i <= 6; i++) {
            mutable.set(floorCollisionValue, floor3 - i - 1, floor);
            if (!world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty()) {
               return floor3 - i;
            }
         }

         return vec.y;
      } else {
         return vec == null ? 0.0 : vec.y;
      }
   }

   private static double getAimAngleBetweenPoints(Vec3d vec, Vec3d vec2, Vec3d vec3) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, vec2);
      float[] wrapDegrees2 = MathClampUtils.getWrapDegrees(vec, vec3);
      return BoxLengthUtils.getAmount(wrapDegrees[0], wrapDegrees[1], wrapDegrees2[0], wrapDegrees2[1]);
   }

   private void clearAimState() {
      this.lastAimPoint = null;
      this.lastAimTick = Long.MIN_VALUE;
   }

   private static boolean isSpotAtPos(AmountConstTime.Spot spot, BlockPos pos) {
      return spot != null && pos != null && spot.x() == pos.getX() && spot.y() == pos.getY() && spot.z() == pos.getZ();
   }

   public ComboTradeData planTerminatorCombo(SelfPopData selfPopData, ComboTradeData comboTradeData, TerminatorCrystalPlanner.Input input) {
      VecBlockingData vecBlockingData = selfPopData == null ? null : selfPopData.target();
      AbsorptionPosData absorptionPosData = selfPopData == null ? null : selfPopData.self();
      MinecraftClient instance = MinecraftClient.getInstance();
      if (vecBlockingData != null && absorptionPosData != null && comboTradeData != null && input != null && input.eye() != null && instance.player != null && instance.world != null) {
         Vec3d vec3d = vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos();
         Vec3d pos = absorptionPosData.pos();
         if (vec3d != null && pos != null) {
            if (vecBlockingData.id() != this.lastTargetEntityId) {
               this.lastTargetEntityId = vecBlockingData.id();
               this.emptyLvl.updateEmpty();
               this.blockedPosExpiryTickByPos.clear();
               this.clearChosenPosition();
               this.clearAimState();
               this.resetStuckTracking();
            }

            long tick = selfPopData.tick();
            this.blockPosOnDirectiveStatus(input.snap(), tick);
            this.detectStuckPosition(input, tick);
            this.purgeExpiredBlockedPositions(tick);
            double hypot = Math.hypot(vec3d.x - pos.x, vec3d.z - pos.z);
            double distanceTo = vec3d.distanceTo(pos);
            TerminatorCrystalPlanner.Choice posObsidian = this.chooseCrystalAction(selfPopData, input, vec3d, tick);
            CaseMaxData options = selfPopData.getOptions(SurroundKitType.ANCHOR);
            CaseMaxData options2 = selfPopData.getOptions(SurroundKitType.MELEE);
            boolean equals = "owed".equals(options.why());
            boolean equals2 = "unplaced".equals(options.why());
            boolean kitTerminatorFlag = ActionPickRules.isDamageNegligible(input.anchorBare(), equals2, options.value());
            boolean crystalFlag = input.anchorBusy() && !kitTerminatorFlag;
            boolean worthExecuting = ActionPickRules.isWorthExecuting(options.available(), options.value(), equals, crystalFlag);
            boolean kitTerminatorFlag4 = options2.available() && selfPopData.targetReadyIn() <= 0 && absorptionPosData.swordCharge() >= 0.9F;
            CenterVecData hit = input.hit();
            boolean kitTerminatorFlag5 = hit != null && hit.lane() == SurroundKitType.PEARL && hit.pearl() != null && absorptionPosData.pearlReady();
            boolean constFlag = posObsidian != null && posObsidian.ready();
            boolean kitTerminatorFlag7 = posObsidian != null && posObsidian.hold();
            boolean kitTerminatorFlag8 = posObsidian != null && ActionPickRules.canBreakPlace(posObsidian.kind(), constFlag, posObsidian.kills(), posObsidian.staged());
            boolean kitTerminatorFlag9 = worthExecuting && options.kills();
            ActionPickRules.ActionChoice crystal = ActionPickRules.pickActionPreferCrystal(
               crystalFlag,
               equals2,
               equals,
               constFlag,
               constFlag && posObsidian.kind() == ActionPickRules.CrystalStepKind.BREAK,
               kitTerminatorFlag7,
               posObsidian == null ? 0.0 : posObsidian.dealt(),
               worthExecuting,
               worthExecuting ? options.value() : 0.0,
               distanceTo,
               kitTerminatorFlag4,
               kitTerminatorFlag4 && options2.kills(),
               input.pearlAiming(),
               kitTerminatorFlag5,
               kitTerminatorFlag8,
               kitTerminatorFlag9
            );
            boolean kitTerminatorFlag10 = crystal == ActionPickRules.ActionChoice.CRYSTAL && ActionPickRules.shouldForceCrystal(crystalFlag, equals2, equals, constFlag, kitTerminatorFlag8, worthExecuting, kitTerminatorFlag9);
            CenterVecData.CrystalStep crystalStep = null;
            CenterVecData.PearlShot pearlShot = null;
            Vec3d vec3d2 = null;
            SurroundKitType surroundKitType;
            switch (crystal) {
               case CRYSTAL:
                  surroundKitType = SurroundKitType.CRYSTAL;
                  crystalStep = toCrystalStep(posObsidian);
                  vec3d2 = posObsidian.aim();
                  break;
               case ANCHOR:
                  surroundKitType = SurroundKitType.ANCHOR;
                  break;
               case MELEE:
                  surroundKitType = SurroundKitType.MELEE;
                  break;
               case PEARL:
                  surroundKitType = SurroundKitType.PEARL;
                  pearlShot = kitTerminatorFlag5 ? hit.pearl() : null;
                  break;
               default:
                  surroundKitType = SurroundKitType.NONE;
                  if (posObsidian != null) {
                     crystalStep = toCrystalStep(posObsidian);
                     vec3d2 = posObsidian.aim();
                  } else {
                     vec3d2 = this.getFallbackAimPoint(input, vec3d, distanceTo, tick);
                  }
            }

            CaseMaxData options3 = selfPopData.getOptions(SurroundKitType.KIT);
            boolean kitTerminatorFlag11 = !input.hotCrystals() && absorptionPosData.crystals() > 0;
            if (ActionPickRules.canMeleeMove(crystal, kitTerminatorFlag4 && options2.kills(), kitTerminatorFlag11, options3.available() && "crystal".equals(options3.why()))) {
               surroundKitType = SurroundKitType.KIT;
               crystalStep = null;
               pearlShot = null;
               vec3d2 = null;
            }

            CenterVecData.Stand stand = crystal == ActionPickRules.ActionChoice.PEARL
               ? (
                  hit != null && hit.stand() != null
                     ? hit.stand()
                     : new CenterVecData.Stand(CenterVecData.StandKind.HOLD, pos, 0.1, false, true, Long.MIN_VALUE)
               )
               : (
                  crystal == ActionPickRules.ActionChoice.MOVE && input.orbit() != null
                     ? input.orbit()
                     : this.computeStand(selfPopData, vecBlockingData, vec3d, pos, hypot, input.spot(), tick, crystalFlag)
               );
            this.recordSnapState(
               crystal == ActionPickRules.ActionChoice.CRYSTAL && constFlag && posObsidian.kind() != ActionPickRules.CrystalStepKind.WAIT && !isPlaceRecentlyAttempted(input, posObsidian)
                  ? posObsidian.base()
                  : null,
               input
            );
            this.statusLabel = formatChoiceLabel(surroundKitType == SurroundKitType.KIT ? "KIT" : crystal.name(), posObsidian) + (posObsidian == null ? this.candidateStatusSuffix : "");
            if (kitTerminatorFlag10 && surroundKitType == SurroundKitType.CRYSTAL) {
               this.statusLabel = this.statusLabel + "/kf";
            }

            CenterVecData centerVecData = new CenterVecData(
               CenterVecData.State.HIT_LOOP,
               CenterVecData.Proposal.NONE,
               posObsidian == null ? 0.0 : posObsidian.score(),
               CenterVecData.Abort.NONE,
               surroundKitType,
               crystalStep,
               null,
               pearlShot,
               null,
               stand,
               vec3d2,
               Integer.MAX_VALUE,
               0,
               "Terminator",
               null
            );
            DanceChaseType danceChaseType = stand.kind() == CenterVecData.StandKind.FOLLOW ? DanceChaseType.CHASE : DanceChaseType.HOLD;
            return new ComboTradeData(
               DefendEngageType.COMBO,
               comboTradeData.posture(),
               comboTradeData.weights(),
               surroundKitType,
               danceChaseType,
               false,
               1.0F,
               comboTradeData.selfWeight(),
               comboTradeData.maxSelfDamage(),
               comboTradeData.allowTotemTrade(),
               ForceTimedType.FORCE,
               true,
               surroundKitType == SurroundKitType.KIT || comboTradeData.restockWindow(),
               "Terminator",
               centerVecData,
               false,
               comboTradeData.trade()
            );
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public String getStatusLabel() {
      return this.statusLabel;
   }

   public void resetState() {
      this.emptyLvl.updateEmpty();
      this.blockedPosExpiryTickByPos.clear();
      this.clearChosenPosition();
      this.clearAimState();
      this.resetStuckTracking();
      this.lastTargetEntityId = -1;
      this.statusLabel = "-";
      this.candidateStatusSuffix = "";
   }

   static UnsafeEntityUtils.Request copyRequestWithWindow(UnsafeEntityUtils.Request request, PosBlockUtilsBonus.Window window) {
      return new UnsafeEntityUtils.Request(
         request.target(),
         request.predicted(),
         request.minEfficiency(),
         request.antiSuicide(),
         request.maxSelfDamage(),
         request.autoObsidian(),
         request.hasObsidian(),
         request.guards(),
         request.nowMs(),
         request.tick(),
         request.reattackTicks(),
         request.fromYaw(),
         request.fromPitch(),
         request.viewYaw(),
         request.tuning(),
         request.self(),
         request.spread(),
         window,
         request.selfWindow(),
         request.targetEhp(),
         request.targetTotem(),
         request.allowTotemTrade(),
         request.veto(),
         request.support()
      );
   }

   private void clearChosenPosition() {
      this.chosenBasePos = null;
      this.chosenScore = Double.NEGATIVE_INFINITY;
      this.chosenTargetDamage = 0.0F;
   }

   private TerminatorCrystalPlanner.Choice chooseCrystalAction(SelfPopData selfPopData, TerminatorCrystalPlanner.Input input, Vec3d vec, long time) {
      this.candidateStatusSuffix = "/off";
      UnsafeEntityUtils.Request request = input.request();
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientWorld world = instance.world;
      AbsorptionPosData self = selfPopData.self();
      VecBlockingData target = selfPopData.target();
      if (request != null && world != null && request.target() != null) {
         if (!BlockEntityUtils.isAmplifierEffect()) {
            this.clearChosenPosition();
            return null;
         } else {
            int maxTargetReadyIn = Math.max(0, selfPopData.targetReadyIn());
            UnsafeEntityUtils.Request selfTarget = copyRequestWithWindow(request, PosBlockUtilsBonus.Window.INSTANCE);
            UnsafeEntityUtils.Request copyRequestWithWindow2 = copyRequestWithWindow(request, new PosBlockUtilsBonus.Window(maxTargetReadyIn, target.lastScaledTaken()));
            HandleTime guards = request.guards();
            long nowMs = request.nowMs();
            Vec3d eye = input.eye();
            LivingEntity entity = request.target();
            Vec3d vec3d = request.predicted() != null && request.predicted().feet() != null ? request.predicted().feet() : vec;
            double posObsidianValue = Math.min(vec.y, vec3d.y);
            double minUnlessFlagged = ActionPickRules.getMinUnlessFlagged(target.onGround(), posObsidianValue, getMaxFloorYAroundFeet(world, vec3d));
            Vec3d pos = self.pos();
            double nearbyValueOrNaN = ActionPickRules.getNearbyValueOrNaN(self.onGround(), self.velocity() == null ? 0.0 : self.velocity().y, pos.y, getMaxFloorYAroundFeet(world, pos));
            Vec3d vec3d2 = Double.isNaN(nearbyValueOrNaN) ? null : new Vec3d(eye.x, eye.y - (pos.y - nearbyValueOrNaN), eye.z);
            boolean hotCrystals = input.hotCrystals();
            boolean posObsidianFlag = hotCrystals && self.obsidian() > 0 && request.autoObsidian() && request.hasObsidian();
            AmountConstTime.Spot spot = input.spot();
            ArrayList arrayList = new ArrayList();
            Box box = new Box(eye.x - 6.0, eye.y - 6.0, eye.z - 6.0, eye.x + 6.0, eye.y + 6.0, eye.z + 6.0);
            HitResult multiplySquared = BlockEntityUtils.getMultiplySquared(eye, input.yaw(), input.pitch());

            for (EndCrystalEntity endCrystalEntity : world.getEntitiesByClass(EndCrystalEntity.class, box, endCrystalEntity2 -> endCrystalEntity2.isAlive() && !endCrystalEntity2.isRemoved())) {
               Box boundingBox = endCrystalEntity.getBoundingBox();
               if (!(BlockEntityUtils.getMinMax(eye, boundingBox) >= 3.0)) {
                  BlockPos immutable = BlockEntityUtils.getDown(endCrystalEntity).toImmutable();
                  int id = endCrystalEntity.getId();
                  if (!this.isPosBlocked(immutable, time) && (guards == null || !guards.canFlag(id, nowMs))) {
                     boolean posObsidianFlag2 = guards != null && guards.canFlag(immutable.asLong(), nowMs);
                     boolean posObsidianFlag3 = guards != null && guards.canTick(id, time, request.reattackTicks());
                     if (!posObsidianFlag3 || immutable.equals(this.chosenBasePos)) {
                        ActionPickRules.CrystalStepKind kind = posObsidianFlag3 ? ActionPickRules.CrystalStepKind.WAIT : ActionPickRules.CrystalStepKind.BREAK;
                        Vec3d vec3d3 = multiplySquared instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == endCrystalEntity
                           ? BlockEntityUtils.getRaycastVector(boundingBox, eye, input.yaw(), input.pitch(), 3.0)
                           : null;
                        double valueUnlessFlagged = ActionPickRules.getValueUnlessFlagged(vec3d3 != null, getAimAngleToPoint(eye, boundingBox.getCenter(), input));
                        arrayList.add(
                           new TerminatorCrystalPlanner.Cand(
                              kind,
                              immutable,
                              null,
                              endCrystalEntity,
                              posObsidianFlag2,
                              ActionPickRules.computeWaitScore(
                                 kind, getProximityWeight(entity, endCrystalEntity.getEntityPos(), vec3d, posObsidianValue), valueUnlessFlagged, isSpotAtPos(spot, immutable), immutable.equals(this.chosenBasePos)
                              ),
                              valueUnlessFlagged,
                              vec3d3 != null ? vec3d3 : boundingBox.getCenter(),
                              false
                           )
                        );
                     }
                  }
               }
            }

            int floor = MathHelper.floor(vec3d.x);
            int posObsidianBound = MathHelper.floor((target.onGround() ? vec3d.y : minUnlessFlagged) + 0.001);
            int floor6 = MathHelper.floor(vec3d.z);
            int posObsidianLimit = ActionPickRules.countTicksUntilLanding(target.onGround(), target.velocity() == null ? 0.0 : target.velocity().y, posObsidianValue - minUnlessFlagged);
            int spotBound = target.isHoleSides() ? posObsidianBound : ActionPickRules.getFloorOffset(minUnlessFlagged);
            int intersectsCenterBound = target.isHoleSides() ? posObsidianBound : Math.min(posObsidianBound, ActionPickRules.getFloorOffset(posObsidianLimit > 0 ? posObsidianValue : minUnlessFlagged));
            double posObsidianValue7 = 25.0;
            Mutable mutable = new Mutable();
            if (hotCrystals) {
               for (int i = intersectsCenterBound; i >= posObsidianBound - 3; i--) {
                  for (int j = -3; j <= 3; j++) {
                     for (int k = -3; k <= 3; k++) {
                        mutable.set(floor + j, i, floor6 + k);
                        if (!(eye.squaredDistanceTo(mutable.getX() + 0.5, mutable.getY() + 0.5, mutable.getZ() + 0.5) > posObsidianValue7)
                           && isBlockWithinReach(eye, mutable)
                           && BlockEntityUtils.canBedrockObsidian(mutable)) {
                           BlockPos immutable2 = mutable.toImmutable();
                           if (!this.isPosBlocked(immutable2, time)) {
                              Vec3d vec3d4 = BlockEntityUtils.getVec3d(immutable2);
                              if (!(vec3d4.distanceTo(vec3d) > 5.0)) {
                                 boolean posObsidianFlag4 = guards != null && guards.canTime(immutable2.asLong(), nowMs);
                                 if (posObsidianFlag4 || BlockEntityUtils.canEntitiesInstance(immutable2)) {
                                    Vec3d southNorth = getAimPointOnBlock(immutable2, eye);
                                    double pitchYaw = getAimAngleToPoint(eye, southNorth, input);
                                    if (i <= spotBound || ActionPickRules.fitsWithinTickBudget(ActionPickRules.CrystalStepKind.PLACE, pitchYaw, maxTargetReadyIn, posObsidianLimit)) {
                                       arrayList.add(
                                          new TerminatorCrystalPlanner.Cand(
                                             ActionPickRules.CrystalStepKind.PLACE,
                                             immutable2,
                                             null,
                                             null,
                                             true,
                                             ActionPickRules.computeWaitScore(
                                                ActionPickRules.CrystalStepKind.PLACE,
                                                getProximityWeight(entity, vec3d4, vec3d, posObsidianValue),
                                                pitchYaw,
                                                isSpotAtPos(spot, immutable2),
                                                immutable2.equals(this.chosenBasePos)
                                             ),
                                             pitchYaw,
                                             southNorth,
                                             false
                                          )
                                       );
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (posObsidianFlag) {
               for (int m = intersectsCenterBound; m >= Math.min(intersectsCenterBound, spotBound) - 1; m--) {
                  for (int p = -2; p <= 2; p++) {
                     for (int q = -2; q <= 2; q++) {
                        if (p != 0 || q != 0) {
                           mutable.set(floor + p, m, floor6 + q);
                           if (!(eye.squaredDistanceTo(mutable.getX() + 0.5, mutable.getY() + 0.5, mutable.getZ() + 0.5) > posObsidianValue7)) {
                              boolean blockWithinReach5 = !isBlockWithinReach(eye, mutable);
                              if (!blockWithinReach5 || vec3d2 != null && isBlockWithinReach(vec3d2, mutable)) {
                                 BlockPos immutable3 = mutable.toImmutable();
                                 if (!this.isPosBlocked(immutable3, time) && (guards == null || !guards.canFlagForLong(immutable3.asLong(), nowMs)) && BlockEntityUtils.isEmpty(immutable3)) {
                                    BlockEntityUtils.Support downPitch = findObsidianSupport(immutable3, eye, input);
                                    if (downPitch != null) {
                                       Vec3d vec3d5 = BlockEntityUtils.getVec3d(downPitch.pos(), downPitch.face());
                                       double aimAngleToPoint = getAimAngleToPoint(eye, vec3d5, input);
                                       if (m <= spotBound || ActionPickRules.fitsWithinTickBudget(ActionPickRules.CrystalStepKind.OBSIDIAN, aimAngleToPoint, maxTargetReadyIn, posObsidianLimit)) {
                                          arrayList.add(
                                             new TerminatorCrystalPlanner.Cand(
                                                ActionPickRules.CrystalStepKind.OBSIDIAN,
                                                immutable3,
                                                downPitch,
                                                null,
                                                true,
                                                ActionPickRules.computeWaitScore(
                                                   ActionPickRules.CrystalStepKind.OBSIDIAN,
                                                   getProximityWeight(entity, BlockEntityUtils.getVec3d(immutable3), vec3d, posObsidianValue),
                                                   aimAngleToPoint,
                                                   false,
                                                   immutable3.equals(this.chosenBasePos)
                                                ),
                                                aimAngleToPoint,
                                                vec3d5,
                                                blockWithinReach5
                                             )
                                          );
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (arrayList.isEmpty()) {
               this.candidateStatusSuffix = "/c0";
               this.clearChosenPosition();
               return null;
            } else {
               arrayList.sort(Comparator.comparingDouble(TerminatorCrystalPlanner.Cand::pre).reversed());
               RefBareData selfPop = selfPopData.getSelfPop();
               TerminatorCrystalPlanner.Ctx ctx = new TerminatorCrystalPlanner.Ctx(
                  selfTarget, copyRequestWithWindow2, eye, pos, self.onGround(), spot, selfPop, selfPop != null && selfPop.on(), maxTargetReadyIn, Math.max(0, request.reattackTicks()), nearbyValueOrNaN
               );
               ArrayList arrayList2 = new ArrayList();
               boolean kindOursFlag = false;
               Vec3d vec3d6 = null;
               double negativeInfinity = Double.NEGATIVE_INFINITY;
               int posObsidianCounter = 0;
               int posObsidianCounter2 = 0;

               for (TerminatorCrystalPlanner.Cand cand : (Iterable<com.corz.client.utils.TerminatorCrystalPlanner.Cand>) (arrayList)) {
                  if (!isPlacementKind(cand.kind())) {
                     posObsidianCounter++;
                     TerminatorCrystalPlanner.Choice kindOurs = this.evaluateCandidate(cand, ctx, false);
                     if (kindOurs != null) {
                        if (kindOurs.kind() == ActionPickRules.CrystalStepKind.WAIT && !kindOurs.ready()) {
                           kindOursFlag = true;
                           if ((kindOurs.pops() || kindOurs.dealt() >= 2.0F) && (vec3d6 == null || kindOurs.score() > negativeInfinity)) {
                              vec3d6 = kindOurs.aim();
                              negativeInfinity = kindOurs.score();
                           }
                        }

                        arrayList2.add(kindOurs);
                     }
                  }
               }

               int posObsidianCounter3 = 0;

               for (TerminatorCrystalPlanner.Cand cand2 : (Iterable<com.corz.client.utils.TerminatorCrystalPlanner.Cand>) (arrayList)) {
                  if (isPlacementKind(cand2.kind())) {
                     if (vec3d6 != null && !ActionPickRules.isStepCostWithinLimit(cand2.kind(), cand2.turn(), getAimAngleBetweenPoints(eye, cand2.at(), vec3d6), maxTargetReadyIn)) {
                        posObsidianCounter2++;
                     } else if (posObsidianCounter3 < 6 || cand2.base().equals(this.chosenBasePos)) {
                        posObsidianCounter3++;
                        posObsidianCounter++;
                        TerminatorCrystalPlanner.Choice totalPriceData = this.evaluateCandidate(cand2, ctx, kindOursFlag);
                        if (totalPriceData != null) {
                           arrayList2.add(totalPriceData);
                        }
                     }
                  }
               }

               TerminatorCrystalPlanner.Choice next = this.selectBestChoice(arrayList2);
               if (next == null) {
                  this.candidateStatusSuffix = "/c" + arrayList.size() + "e" + posObsidianCounter + "t" + posObsidianCounter2;
                  this.clearChosenPosition();
                  return null;
               } else {
                  this.candidateStatusSuffix = "";
                  this.chosenBasePos = next.base();
                  this.chosenScore = next.score();
                  this.chosenTargetDamage = next.dealt();
                  this.lastAimPoint = next.aim();
                  this.lastAimTick = time;
                  return next;
               }
            }
         }
      } else {
         return null;
      }
   }

   private void purgeExpiredBlockedPositions(long terminatorPosLimit) {
      Iterator iterator = this.blockedPosExpiryTickByPos.entrySet().iterator();

      while (iterator.hasNext()) {
         if (terminatorPosLimit >= (Long)((Entry)iterator.next()).getValue()) {
            iterator.remove();
         }
      }
   }

   static double getMaxFloorYAroundFeet(World world, Vec3d vec) {
      if (world != null && vec != null) {
         double terminatorPosBound = Double.NEGATIVE_INFINITY;

         for (int i = 0; i < 4; i++) {
            double x = vec.x + ((i & 1) == 0 ? -0.299 : 0.299);
            double z = vec.z + ((i & 2) == 0 ? -0.299 : 0.299);
            terminatorPosBound = Math.max(terminatorPosBound, getFloorCollision(world, new Vec3d(x, vec.y, z)));
         }

         return terminatorPosBound;
      } else {
         return getFloorCollision(world, vec);
      }
   }

   private void recordSnapState(BlockPos blockPos, TerminatorCrystalPlanner.Input input) {
      TickStatusData snap = input.snap();
      this.snapBasePos = blockPos;
      this.snapActionTick = snap == null ? Long.MIN_VALUE : snap.lastActionTick();
      this.snapYaw = input.yaw();
      this.snapPitch = input.pitch();
   }

   private static boolean isPlaceRecentlyAttempted(TerminatorCrystalPlanner.Input input, TerminatorCrystalPlanner.Choice choice) {
      UnsafeEntityUtils.Request request = input.request();
      return choice.kind() == ActionPickRules.CrystalStepKind.PLACE && request != null && request.guards() != null && request.guards().canTime(choice.base().asLong(), request.nowMs());
   }

   private Vec3d getFallbackAimPoint(TerminatorCrystalPlanner.Input input, Vec3d vec, double predictedVecLimit, long predictedVecValue) {
      Vec3d eye = input.eye();
      if (this.lastAimPoint != null && predictedVecValue - this.lastAimTick <= 10L && eye.distanceTo(this.lastAimPoint) <= 6.0) {
         return this.lastAimPoint;
      } else {
         Vec3d center = getSpotAimPoint(input.spot(), eye);
         if (center == null && predictedVecLimit > 3.5) {
            UnsafeEntityUtils.Request request = input.request();
            Vec3d vec3d = request != null && request.predicted() != null && request.predicted().feet() != null ? request.predicted().feet() : vec;
            return new Vec3d(vec3d.x, getFloorCollision(MinecraftClient.getInstance().world, vec3d), vec3d.z);
         } else {
            return center;
         }
      }
   }

   private TerminatorCrystalPlanner.Choice selectBestChoice(List<TerminatorCrystalPlanner.Choice> list) {
      TerminatorCrystalPlanner.Choice next = null;
      TerminatorCrystalPlanner.Choice choice = null;
      TerminatorCrystalPlanner.Choice next2 = null;
      TerminatorCrystalPlanner.Choice next3 = null;

      for (TerminatorCrystalPlanner.Choice choice2 : list) {
         if (!choice2.ready() && !choice2.hold()) {
            if (next2 == null || choice2.score() > next2.score()) {
               next2 = choice2;
            }
         } else {
            if (next == null || choice2.score() > next.score()) {
               next = choice2;
            }

            if (choice2.base().equals(this.chosenBasePos) && (choice == null || choice2.score() > choice.score())) {
               choice = choice2;
            }

            if (choice2.ready() && choice2.own() && (next3 == null || choice2.score() > next3.score())) {
               next3 = choice2;
            }
         }
      }

      if (next == null) {
         return next2;
      } else if (next3 != null
         && next3 != next
         && ActionPickRules.isDamageSufficient(next3.kills(), next3.pops(), next3.dealt(), next3.score(), next.kills(), next.pops(), next.score())) {
         return next3;
      } else if (choice != null && choice != next) {
         return ActionPickRules.isAlternativeBetter(choice.score(), choice.pops(), choice.kills(), next.score(), next.pops(), next.kills()) ? next : choice;
      } else {
         return next;
      }
   }

   private TerminatorCrystalPlanner.Choice evaluateCandidate(TerminatorCrystalPlanner.Cand cand, TerminatorCrystalPlanner.Ctx ctx, boolean kindOursFlag) {
      if (cand.kind() == ActionPickRules.CrystalStepKind.WAIT) {
         return cand.base().equals(this.chosenBasePos) && Double.isFinite(this.chosenScore)
            ? new TerminatorCrystalPlanner.Choice(
               ActionPickRules.CrystalStepKind.WAIT, cand.base(), null, cand.crystal(), this.chosenScore, this.chosenTargetDamage, cand.at(), true, false, false, false, false, false
            )
            : null;
      } else {
         boolean kindOursFlag2 = cand.kind() == ActionPickRules.CrystalStepKind.BREAK;
         int kindOursValue = (int)ActionPickRules.getDelayBucket(cand.turn());
         int kindOursValue2 = kindOursFlag2 ? 0 : ctx.land() + (cand.kind() == ActionPickRules.CrystalStepKind.OBSIDIAN ? 1 : 0);
         boolean kindOursFlag3 = !kindOursFlag2 && ctx.holdable() && ctx.readyIn() > kindOursValue2;
         if (kindOursFlag3 && kindOursFlag && cand.kind() == ActionPickRules.CrystalStepKind.PLACE) {
            return null;
         } else {
            UnsafeEntityUtils.Forced directionsBase = getIdleDirectionsForCandidate(cand, !kindOursFlag2 && ctx.holdable() ? ctx.open() : ctx.live());
            if (directionsBase == null) {
               return null;
            } else {
               boolean ok = directionsBase.ok();
               if (!ok && !"unsafe".equals(directionsBase.why())) {
                  return null;
               } else {
                  RefBareData tm = ctx.tm();
                  float constFactor = tm != null && !kindOursFlag3 && !cand.late() ? tm.getBareRef(directionsBase.selfFull(), directionsBase.selfCost()) : directionsBase.selfFull();
                  float selfFull = directionsBase.selfFull();
                  float targetDamage = directionsBase.targetDamage();
                  boolean ehpTarget = isTargetEhpThresholdMet(targetDamage, ctx.live());
                  int constBound = kindOursFlag3 ? ctx.readyIn() : 0;
                  boolean kindOursFlag4 = kindOursFlag2 && ActionPickRules.isSelfCostAcceptable(cand.ours(), ctx.readyIn(), ok, targetDamage, ehpTarget);
                  if (kindOursFlag4) {
                     UnsafeEntityUtils.Forced idleDirectionsForCandidate = getIdleDirectionsForCandidate(cand, ctx.open());
                     if (idleDirectionsForCandidate == null || !idleDirectionsForCandidate.ok()) {
                        return null;
                     }

                     targetDamage = idleDirectionsForCandidate.targetDamage();
                     selfFull = idleDirectionsForCandidate.selfFull();
                     constFactor = selfFull;
                     ehpTarget = isTargetEhpThresholdMet(targetDamage, ctx.live());
                     constBound = ctx.readyIn();
                  }

                  boolean kindOursFlag5 = ehpTarget && !ctx.live().targetTotem();
                  double constValue = kindOursFlag2 ? cand.crystal().getY() : cand.base().getY() + 1.0;
                  if (!ActionPickRules.isSafeToProceed(cand.late() ? ctx.landY() : ctx.us().y, cand.late() || ctx.usGround(), constValue, targetDamage, constFactor, ehpTarget, kindOursFlag5)) {
                     return null;
                  } else if (!ActionPickRules.canBreak(cand.kind(), targetDamage, selfFull, constFactor, cand.ours(), ehpTarget)) {
                     return null;
                  } else {
                     double breakWait = ActionPickRules.computeStepScore(
                        cand.kind(), targetDamage, selfFull, constFactor, ehpTarget, kindOursFlag5, cand.ours(), isSpotAtPos(ctx.spot(), cand.base()), cand.turn(), constBound
                     );
                     Vec3d at = cand.at();
                     boolean kindOursFlag6 = kindOursFlag2 && cand.ours();
                     return new TerminatorCrystalPlanner.Choice(
                        kindOursFlag4 ? ActionPickRules.CrystalStepKind.WAIT : cand.kind(),
                        cand.base(),
                        cand.support(),
                        cand.crystal(),
                        breakWait,
                        targetDamage,
                        at,
                        ok && !kindOursFlag4,
                        kindOursFlag4 && ActionPickRules.isWithinTickWindow(ctx.readyIn(), kindOursValue),
                        kindOursFlag3,
                        ehpTarget,
                        kindOursFlag5,
                        kindOursFlag6
                     );
                  }
               }
            }
         }
      }
   }

   public static boolean isTerminatorAllowedForCombo(ComboTradeData comboTradeData, boolean healKitFlag, boolean healKitFlag2) {
      if (comboTradeData == null) {
         return true;
      } else {
         DefendEngageType mode = comboTradeData.mode();
         SurroundKitType lane = comboTradeData.lane();
         boolean healKitFlag3 = lane == SurroundKitType.EAT
            || lane == SurroundKitType.KIT
            || lane == SurroundKitType.XP
            || lane == SurroundKitType.SURROUND
            || lane == SurroundKitType.BREACH;
         return ActionPickRules.isAnyFlagSet(healKitFlag, healKitFlag2, mode == DefendEngageType.ESCAPE, mode == DefendEngageType.HEAL, healKitFlag3);
      }
   }

   private static double getProximityWeight(LivingEntity entity, Vec3d vec, Vec3d vec2, double distanceValue) {
      return BlockEntityUtils.getAmount(entity, vec.distanceTo(vec2), 6.0F) * ActionPickRules.getDistanceFalloff(vec.y, distanceValue);
   }

   private void detectStuckPosition(TerminatorCrystalPlanner.Input input, long posBlockValue) {
      TickStatusData snap = input.snap();
      long posBlockValue2 = snap == null ? Long.MIN_VALUE : snap.lastActionTick();
      boolean posBlockFlag = this.snapBasePos != null
         && this.snapBasePos.equals(this.chosenBasePos)
         && posBlockValue2 == this.snapActionTick
         && BoxLengthUtils.getAmount(this.snapYaw, this.snapPitch, input.yaw(), input.pitch()) <= 2.0;
      this.stuckTickCount = posBlockFlag ? this.stuckTickCount + 1 : 0;
      this.snapBasePos = null;
      if (this.stuckTickCount >= 8) {
         this.blockedPosExpiryTickByPos.put(this.chosenBasePos.asLong(), posBlockValue + 20L);
         this.clearChosenPosition();
         this.clearAimState();
         this.resetStuckTracking();
      }
   }

   private static Vec3d getAimPointOnBlock(BlockPos pos, Vec3d vec) {
      double y = pos.getY() + 1.0;
      if (vec.y >= y) {
         return new Vec3d(pos.getX() + 0.5, y, pos.getZ() + 0.5);
      } else {
         double southNorthLimit = vec.x - (pos.getX() + 0.5);
         double spotValue = vec.z - (pos.getZ() + 0.5);
         Direction direction = Math.abs(southNorthLimit) >= Math.abs(spotValue)
            ? (southNorthLimit >= 0.0 ? Direction.EAST : Direction.WEST)
            : (spotValue >= 0.0 ? Direction.SOUTH : Direction.NORTH);
         return BlockEntityUtils.getVec3d(pos, direction);
      }
   }

   static boolean isBlockWithinReach(Vec3d vec, BlockPos pos) {
      return BlockEntityUtils.getMinMax(vec, BlockEntityUtils.getBox(pos)) < 2.85;
   }

   private static double getAimAngleToPoint(Vec3d vec, Vec3d vec2, TerminatorCrystalPlanner.Input input) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, vec2);
      return BoxLengthUtils.getAmount(input.yaw(), input.pitch(), wrapDegrees[0], wrapDegrees[1]);
   }

   private boolean isPosBlocked(BlockPos pos, long terminatorPosValue) {
      Long long2 = this.blockedPosExpiryTickByPos.get(pos.asLong());
      return long2 != null && terminatorPosValue < long2;
   }

   private CenterVecData.Stand computeStand(
      SelfPopData selfPopData, VecBlockingData vecBlockingData, Vec3d vec, Vec3d vec2, double farLvlLimit, AmountConstTime.Spot spot, long farLvlValue, boolean farLvlFlag
   ) {
      ClientWorld world = MinecraftClient.getInstance().world;
      double y = vecBlockingData.onGround() ? vec.y : getFloorCollision(world, vec);
      Vec3d vec3d = new Vec3d(vec.x, y, vec.z);
      if (farLvlLimit > 4.5 || world == null) {
         this.emptyLvl.handleEmptyTime("far");
         double[] farLvlValues = ActionPickRules.getPointToward(vec.x, vec.z, vec2.x, vec2.z, 1.6);
         return new CenterVecData.Stand(
            CenterVecData.StandKind.FOLLOW, new Vec3d(farLvlValues[0], y, farLvlValues[1]), 1.0, true, false, Long.MIN_VALUE, vec3d, PreferAnchorData.Kind.MELEE_LOW
         );
      } else if (farLvlFlag) {
         return new CenterVecData.Stand(CenterVecData.StandKind.HOLD, vec2, 0.1, false, true, Long.MIN_VALUE);
      } else {
         EmptyLvl.Pick pick = this.emptyLvl.getDataForLongCells(farLvlValue, EmptyLvl.getApiForWorld(world), vec.x, vec.y, vec.z, y, vec2.x, vec2.y, vec2.z, spot, null, false);
         double constLimit = pick == null ? Double.NaN : Math.hypot(pick.getAmount2() - vec.x, pick.getAmount() - vec.z);
         Vec3d vec3d2;
         if (pick != null
            && EmptyLvl.canRelHigh(pick, farLvlLimit, vecBlockingData.onGround())
            && constLimit <= (pick.rel() <= -0.5 ? 3.2 : 2.6)
            && ActionPickRules.isMarginAcceptable(pick.rel(), constLimit, 2.2)) {
            vec3d2 = new Vec3d(pick.getAmount2(), pick.y(), pick.getAmount());
         } else {
            double[] pointToward = ActionPickRules.getPointToward(vec.x, vec.z, vec2.x, vec2.z, 2.5);
            vec3d2 = new Vec3d(pointToward[0], y, pointToward[1]);
         }

         return new CenterVecData.Stand(CenterVecData.StandKind.FOLLOW, vec3d2, 0.5, farLvlLimit > 2.0, true, Long.MIN_VALUE, vec3d, PreferAnchorData.Kind.MELEE_LOW);
      }
   }

   private static BlockEntityUtils.Support findObsidianSupport(BlockPos pos, Vec3d vec, TerminatorCrystalPlanner.Input input) {
      BlockPos down = pos.down();
      return BlockEntityUtils.canBlockState(down, Direction.UP)
         ? new BlockEntityUtils.Support(down, Direction.UP)
         : BlockEntityUtils.getOppositeImmutable(pos, vec, input.yaw(), input.pitch(), 4.4);
   }

   public TerminatorCrystalPlanner() {
      this.lastAimTick = Long.MIN_VALUE;
      this.lastTargetEntityId = -1;
      this.statusLabel = "-";
      this.candidateStatusSuffix = "";
      this.snapActionTick = Long.MIN_VALUE;
   }

   private void blockPosOnDirectiveStatus(TickStatusData tickStatusData, long directiveStatusValue) {
      if (tickStatusData != null && this.chosenBasePos != null && tickStatusData.directiveStatus() != null) {
         if (BLOCKED_DIRECTIVE_STATUSES.contains(tickStatusData.directiveStatus())) {
            this.blockedPosExpiryTickByPos.put(this.chosenBasePos.asLong(), directiveStatusValue + 6L);
            this.clearChosenPosition();
            this.clearAimState();
         }
      }
   }

   private static UnsafeEntityUtils.Forced getIdleDirectionsForCandidate(TerminatorCrystalPlanner.Cand cand, UnsafeEntityUtils.Request request) {
      return switch (cand.kind()) {
         case BREAK -> UnsafeEntityUtils.getIdleDirections(request, ImmutablePos.Kind.BREAK, cand.base(), cand.base(), cand.crystal(), BlockEntityUtils.idleDirections);
         case PLACE -> UnsafeEntityUtils.getIdleDirections(request, ImmutablePos.Kind.PLACE, cand.base(), cand.base(), null, BlockEntityUtils.idleDirections);
         case OBSIDIAN -> UnsafeEntityUtils.getIdleDirections(
            request.getSelfTarget(cand.support()), ImmutablePos.Kind.OBSIDIAN, cand.base(), cand.base(), null, BlockEntityUtils.idleDirections
         );
         case WAIT -> null;
      };
   }

   private static boolean isPlacementKind(ActionPickRules.CrystalStepKind crystalStepKind) {
      return crystalStepKind == ActionPickRules.CrystalStepKind.PLACE || crystalStepKind == ActionPickRules.CrystalStepKind.OBSIDIAN;
   }

   private void resetStuckTracking() {
      this.snapBasePos = null;
      this.snapActionTick = Long.MIN_VALUE;
      this.stuckTickCount = 0;
   }

   private static boolean isTargetEhpThresholdMet(float ehpTargetValue, UnsafeEntityUtils.Request request) {
      return Float.isFinite(request.targetEhp()) && PosBlockUtilsBonus.canFlag(ehpTargetValue, request.targetEhp());
   }

   static Vec3d getSpotAimPoint(AmountConstTime.Spot spot, Vec3d vec) {
      if (spot != null && vec != null) {
         BlockPos blockPos = new BlockPos(spot.x(), spot.y(), spot.z());
         if (BlockEntityUtils.getMinMax(vec, BlockEntityUtils.getBox(blockPos)) >= 3.0) {
            return null;
         } else {
            return vec.y >= spot.getAmount() ? new Vec3d(spot.getX(), spot.getAmount(), spot.getZ()) : BlockEntityUtils.getBox(blockPos).getCenter();
         }
      } else {
         return null;
      }
   }

   private static String formatChoiceLabel(String formatStagedReady, TerminatorCrystalPlanner.Choice choice) {
      if (choice == null) {
         return formatStagedReady;
      } else {
         BlockPos base = choice.base();
         return formatStagedReady + ":" + choice.kind().name() + (choice.ready() ? "@" : "!") + base.getX() + "," + base.getY() + "," + base.getZ() + (choice.staged() ? "h" : "");
      }
   }

   private record Cand(
      ActionPickRules.CrystalStepKind kind,
      BlockPos base,
      BlockEntityUtils.Support support,
      EndCrystalEntity crystal,
      boolean ours,
      double pre,
      double turn,
      Vec3d at,
      boolean late
   ) {
   }

   private record Choice(
      ActionPickRules.CrystalStepKind kind,
      BlockPos base,
      BlockEntityUtils.Support support,
      EndCrystalEntity crystal,
      double score,
      float dealt,
      Vec3d aim,
      boolean ready,
      boolean hold,
      boolean staged,
      boolean pops,
      boolean kills,
      boolean own
   ) {
   }

   private record Ctx(
      UnsafeEntityUtils.Request open,
      UnsafeEntityUtils.Request live,
      Vec3d eye,
      Vec3d us,
      boolean usGround,
      AmountConstTime.Spot spot,
      RefBareData tm,
      boolean holdable,
      int readyIn,
      int land,
      double landY
   ) {
   }

   public record Input(
      UnsafeEntityUtils.Request request,
      AmountConstTime.Spot spot,
      CenterVecData hit,
      TickStatusData snap,
      boolean anchorBusy,
      boolean pearlAiming,
      boolean hotCrystals,
      Vec3d eye,
      float yaw,
      float pitch,
      CenterVecData.Stand orbit,
      boolean anchorBare
   ) {
   }
}
