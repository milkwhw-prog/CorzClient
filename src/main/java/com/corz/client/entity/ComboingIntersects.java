package com.corz.client.entity;

import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.DenyCrystals;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.inventory.LivingEntityHelperVisible;
import com.corz.client.inventory.StaleOwed;
import com.corz.client.inventory.VecPos;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.EmptyLvl;
import com.corz.client.world.HimEtaData;
import com.corz.client.world.HuntDeflect;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.PosVecUtils;
import com.corz.client.world.TickStatusData;
import com.corz.client.world.VecPosUtilsAmount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.PreferAnchorData;
import com.corz.client.utils.RoutedBreachingApi;
import com.corz.client.utils.SprintResetTracker;
import com.corz.client.utils.AnchorRoutePlanner;
import com.corz.client.utils.AmountAmounts;
import com.corz.client.utils.AmountConstTime;
import com.corz.client.utils.BowGappleType;
import com.corz.client.utils.CarefulBalancedType;
import com.corz.client.utils.CaseMaxData;
import com.corz.client.utils.IframeComboUtils;
import com.corz.client.utils.PearlsPlaystyleData;
import com.corz.client.utils.RefBareData;
import com.corz.client.utils.SelfPopData;
import com.corz.client.utils.SurroundKitType;
import com.corz.client.utils.VecShieldUtilsAmounts;

public final class ComboingIntersects {
   private long cooldownUntilTick;
   private long flooredValue;
   private int targetEntityId;
   private int kitCount;
   private long kitValue;
   private String loopText;
   private int prepAnchorCount;
   private int doneCount;
   private long posBlockValue;
   private SurroundKitType surroundKitType;
   private String anchor;
   private ComboingIntersects.Episode kindEpisode;
   private BlockPos blockPos;
   private long airValue;
   private int comboingIntersectsCount;
   private boolean busyFlag;
   private String replanText;
   private int comboingIntersectsValue;
   private int distanceValue;
   private BlockPos floored;
   private int iframeComboingCount;
   private int loopHitCount;
   private int lvlEmptyValue;
   private double distanceBonus;
   private long tickIframe;
   private long flooredLastTime;
   private BlockPos comboBase;
   private final Map<Long, Long> iframePositionMap = new HashMap<>();
   private final EmptyLvl emptyLvl;
   private ComboingIntersects.WindowPlan posBlockData;
   private int kindCount;
   private long iframeStartTick;
   private int iframeCurrentValue;
   private int readyIn;
   private int kitAttemptCount;
   private long vecValue;
   private PosVecUtils.Path pathIframe;
   private int kitSuccessCount;
   private Vec3d vecTime;
   private double iframeLosAmount;
   private final int[] kitSurroundTable;
   private long iframeLosValue;
   public static final int UNUSED_ONE = 1;
   private Vec3d prepAnchorVec;
   private RefBareData airRefBareData;
   private long moveAirTime;
   public static final String FIGHTING = "Fighting";
   private ComboingIntersects.Kicked iframeKicked;
   private long excessChipTime;
   private final HuntDeflect huntDeflect = new HuntDeflect();
   private ComboingIntersects.Pending stgPosPending;
   private int kitDelayTicks;
   private int kitMissCount;
   private static final ComboingIntersects.KickModel ZERO_KICK_MODEL = new ComboingIntersects.KickModel(new double[]{0.0, 0.0, 0.0}, new double[]{0.0, 0.0, 0.0}, 1);
   private long iframeTime;
   private int doneOpenCount;
   private ComboingIntersects.KickModel iframeKickModel;
   public static final String PEARLING = "Pearling";
   public static final String CHASING = "Chasing";
   private long approachChasingTime;
   private ComboingIntersects.Phase prepareStrikeType;
   private int kitSurroundCount;
   private int groundBoxValue;
   private String ok;
   private double distanceTo;
   private final long[] iframeTable;
   private Vec3d minEyeVec;
   private CenterVecData.Stand vecData;
   private boolean iframeActive;
   private int flooredLastValue;
   public static final String HITTING = "Hitting";
   private int touchStreak;
   private long weapon;
   private long loopHitValue;
   private int swordLoadoutSlot;
   private double y;
   private int surroundKitCount;
   private int openKindCount;
   private long iframeLastTick;
   private long doneOpenValue;
   public static final String HIT_LOOP = "HIT_LOOP";
   private boolean flooredActive;
   private int iframeCount;
   private String levelSpaceText;
   private int kitFallbackCount;
   private boolean playstyleTerminator;
   private boolean lvlActive;
   private String airText;
   private double z;
   public static final String COMBOING = "Comboing";
   private int routedSearchingValue;
   private ComboingIntersects.Ctx iframeCtx;
   private int crystalValue;
   private long iframeLastKitTime;
   private BlockPos blockPos2Iframe;
   private long touchingValue;
   private int openKindRepeatCount;
   private double x;
   private long distanceUpdateTime;
   private int doneOpenRepeatCount;
   private String none;
   private String iframeText;
   public static final String AIR_MOVE = "air:move";
   private long pitchEyeValue;
   private long excessChipTimeIframe;
   private long using;
   private int iframeValue;

   private CenterVecData getMaxTouching(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntityHelperVisible.Seed seed, LivingEntity entity
   ) {
      float lastScaledTaken = ctx.target().lastScaledTaken();
      if (!this.playstyleTerminator || !IframeComboUtils.canFlag(view.readyIn(), lastScaledTaken, BoxLengthUtils.getMax(ctx.self().pingMs()))) {
         return null;
      } else if (!entity.isTouchingWater() && view.h() <= 6.0) {
         ComboingIntersects.Episode kindEpisode174 = this.kindEpisode;
         if (kindEpisode174 != null && kindEpisode174.doneOpenType == ComboingIntersects.Outcome.OPEN) {
            return null;
         } else {
            long time = ctx.tick();
            VecPos.Loadout loadout = ctx.loadout();
            if (loadout != null && loadout.crystalSlot() >= 0 && !this.canTimes(0, time)) {
               CaseMaxData options = ctx.s().getOptions(SurroundKitType.CRYSTAL);
               if (IframeComboUtils.canComboHeld(options.available(), options.why(), options.value())) {
                  return null;
               } else {
                  PosVecUtils.Path calToward = this.getCalToward(ctx, view, seed, 16);
                  ComboingIntersects.FollowUp idxDet = this.getIdxDet(ctx, view, clientPlayerEntity, entity, seed, calToward, 0, lastScaledTaken, null);
                  if (idxDet != null && (idxDet.damage() >= 4.0F || this.canTargetFlag(ctx, view, idxDet.damage()))) {
                     this.kindEpisode = new ComboingIntersects.Episode(time, ZERO_KICK_MODEL, false, lastScaledTaken, idxDet);
                     this.touchingValue = time;
                     this.touchStreak++;
                     this.anchor = "now:x";
                     return this.getComboingBuild(ctx, view, idxDet, ctx.world().findCrystalAt(idxDet.cell()) != null);
                  } else {
                     return null;
                  }
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   public void updateTimeIframe() {
      this.iframePositionMap.clear();
      this.kindEpisode = null;
      this.stgPosPending = null;
      this.posBlockData = null;
      this.iframeCtx = null;
      this.prepareStrikeType = ComboingIntersects.Phase.IDLE;
      this.surroundKitType = SurroundKitType.NONE;
      this.loopHitValue = Long.MIN_VALUE;
      this.kitValue = Long.MIN_VALUE;
      this.weapon = Long.MIN_VALUE;
      this.kitCount = 0;
      this.using = Long.MIN_VALUE;
      this.excessChipTimeIframe = Long.MIN_VALUE;
      this.crystalValue = 0;
      this.blockPos = null;
      this.cooldownUntilTick = Long.MIN_VALUE;
      this.comboBase = null;
      this.pitchEyeValue = Long.MIN_VALUE;
      this.iframeActive = false;
      this.vecValue = Long.MIN_VALUE;
      this.minEyeVec = null;
      this.vecTime = null;
      this.iframeValue = 0;
      this.iframeLosValue = Long.MIN_VALUE;
      this.iframeTime = Long.MIN_VALUE;
      this.iframeLosAmount = 0.0;
      this.iframeStartTick = Long.MIN_VALUE;
      this.iframeLastTick = Long.MIN_VALUE;
      this.iframeCurrentValue = -1;
      this.emptyLvl.updateEmpty();
      this.lvlEmptyValue = -1;
      this.flooredActive = false;
      this.flooredLastValue = -1;
      this.flooredLastTime = Long.MIN_VALUE;
      this.flooredValue = Long.MIN_VALUE;
      this.floored = null;
      this.comboingIntersectsValue = 0;
      this.y = Double.NaN;
      this.groundBoxValue = 0;
      this.distanceUpdateTime = Long.MIN_VALUE;
      this.targetEntityId = -1;
      this.distanceTo = Double.NaN;
      this.distanceValue = 0;
      this.distanceBonus = 0.0;
      this.routedSearchingValue = 0;
      this.x = 0.0;
      this.z = 0.0;
      this.vecData = null;
      this.approachChasingTime = Long.MIN_VALUE;
      this.touchingValue = Long.MIN_VALUE;
      this.posBlockValue = Long.MIN_VALUE;
      this.airValue = Long.MIN_VALUE;
      this.iframeLastKitTime = Long.MIN_VALUE;
      this.readyIn = Integer.MAX_VALUE;
      this.moveAirTime = Long.MIN_VALUE;
      this.iframeKicked = null;
      this.iframeKickModel = null;
      this.excessChipTime = Long.MIN_VALUE;
      this.prepAnchorVec = null;
      this.pathIframe = null;
      this.blockPos2Iframe = null;
      this.swordLoadoutSlot = -1;
      this.playstyleTerminator = false;
      this.busyFlag = false;
      this.airRefBareData = RefBareData.INSTANCE;
      this.kitDelayTicks = 0;
      this.tickIframe = Long.MIN_VALUE;
      this.iframeCount = 0;
      this.airText = "-";
      this.none = "-";
      this.ok = "-";
      this.kitSurroundTable[0] = 0;
      this.kitSurroundTable[1] = 0;
      this.kitSurroundCount = 0;
      this.iframeTable[0] = Long.MIN_VALUE;
      this.iframeTable[1] = Long.MIN_VALUE;
      this.huntDeflect.updateVecNoshot();
      this.levelSpaceText = "-";
      this.replanText = "-";
      this.anchor = "-";
      this.iframeText = "-";
      this.loopText = "-";
   }

   public CenterVecData getMoveAir(ComboingIntersects.Ctx data) {
      this.anchor = "-";
      this.iframeText = "-";
      this.levelSpaceText = "-";
      this.replanText = "-";
      this.airText = "-";
      this.none = "-";
      this.ok = "-";
      this.prepAnchorVec = null;
      this.pathIframe = null;
      this.blockPos2Iframe = null;
      this.airRefBareData = data != null && data.s() != null ? data.s().getSelfPop() : RefBareData.INSTANCE;
      if (data != null && data.s() != null && data.target() != null && data.self() != null && data.tracker() != null && data.world() != null) {
         long time = data.tick();
         this.iframeCtx = data;
         this.iframePositionMap.entrySet().removeIf(tick -> time >= tick.getValue());
         if (this.stgPosPending != null && this.stgPosPending.tick() != time) {
            this.stgPosPending = null;
         }

         if (this.blockPos != null && time > this.cooldownUntilTick) {
            this.blockPos = null;
         }

         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         LivingEntityHelperVisible.Seed seed = data.tracker().getDataGroundMax();
         LivingEntity entity = data.target().entity();
         if (player != null && seed != null && entity != null && entity.isAlive() && !entity.isRemoved()) {
            long time2 = data.snap() == null ? Long.MIN_VALUE : data.snap().lastBreakTick();
            boolean airFlag = time2 != Long.MIN_VALUE && time2 <= time && (this.airValue == Long.MIN_VALUE || time2 > this.airValue);
            if (airFlag) {
               this.airValue = time2;
            }

            ComboingIntersects.View groundBox = this.getGroundBox(data, player, seed, entity);
            this.huntDeflect
               .handleTimeTimes(time, groundBox.h(), IframeComboUtils.getAmount(groundBox.sample().vx(), groundBox.sample().vz(), groundBox.towardUs().x, groundBox.towardUs().z));
            int airValue = time2 == this.moveAirTime ? this.readyIn : groundBox.readyIn();
            if (airFlag && airValue <= 0 && canDistance(data.snap().base(), groundBox.feet())) {
               this.iframeLastKitTime = time2;
            }

            this.readyIn = groundBox.readyIn();
            this.moveAirTime = time;
            this.handleTime(time, data.target().id(), groundBox.readyIn());
            this.handleBreakTick(data, time);
            this.handleDistanceTime(data, groundBox);
            boolean blocking = data.target().blocking();
            int loadoutSword = getLoadoutSword(data, player, blocking);
            this.swordLoadoutSlot = blocking ? -1 : loadoutSword;
            this.playstyleTerminator = canPlaystyleTerminator(data);
            CenterVecData replanLevel = this.getReplanLevel(data, groundBox, player, seed, entity);
            if (this.airRefBareData.ref()) {
               return this.getPosBlock(replanLevel, data, groundBox, player, seed, entity, loadoutSword, blocking);
            } else {
               if (replanLevel == null) {
                  replanLevel = this.getReleaseComboing(data, groundBox, player, entity);
               }

               if (replanLevel == null) {
                  replanLevel = this.getNofollowWeakfollow(data, groundBox, player, seed, entity, loadoutSword, blocking);
               }

               if (replanLevel == null) {
                  replanLevel = this.getPosBlock(data, groundBox, player, entity);
               }

               CenterVecData centerVecData = replanLevel != null && replanLevel.lane() == SurroundKitType.NONE && "air:move".equals(this.anchor) ? replanLevel : null;
               if (centerVecData != null) {
                  replanLevel = null;
               }

               if (replanLevel == null) {
                  replanLevel = this.getTapComboing(data, groundBox);
               }

               if (replanLevel == null) {
                  replanLevel = this.getMaxTouching(data, groundBox, player, seed, entity);
               }

               if (replanLevel == null && centerVecData != null) {
                  this.anchor = "air:move";
                  replanLevel = centerVecData;
               }

               if (replanLevel == null) {
                  replanLevel = this.getAirOpen(data, groundBox, player, seed, entity);
               }

               if (replanLevel == null) {
                  replanLevel = this.getDigComboing(data, groundBox, entity);
               }

               if (replanLevel == null) {
                  replanLevel = this.getLvlMax(data, groundBox, player, seed, entity);
               }

               if (replanLevel == null) {
                  replanLevel = this.getSpamExcess(data, groundBox);
               }

               if (replanLevel == null) {
                  replanLevel = this.getMaxOpen(data, groundBox, player, seed, entity);
               }

               if (replanLevel == null) {
                  replanLevel = this.getIframePearling(data, groundBox);
               }

               if (replanLevel == null) {
                  replanLevel = this.getPrepAnchor(data, groundBox, player, entity);
               }

               if (replanLevel == null) {
                  replanLevel = this.getChipExcess(data, groundBox, entity, loadoutSword, blocking);
               }

               if (replanLevel == null) {
                  replanLevel = this.getAnchorCrystal(data, groundBox);
               }

               if (replanLevel == null) {
                  replanLevel = this.getSelfBusy(data, groundBox, player, entity);
               }

               if (replanLevel == null) {
                  replanLevel = this.getChasingApproach(data, groundBox);
               }

               return replanLevel;
            }
         } else {
            return this.getKitSurround(time);
         }
      } else {
         return this.getKitSurround(data == null ? Long.MIN_VALUE : data.tick());
      }
   }

   private CenterVecData getChipExcess(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, LivingEntity entity, int chipExcessValue, boolean chipExcessFlag) {
      long excessChipTime = ctx.tick();
      boolean chipExcessFlag2 = view.readyIn() > 0;
      boolean chipExcessFlag3 = chipExcessFlag2 && IframeComboUtils.canFlagForFloat(getLoadoutSword(ctx, chipExcessFlag), ctx.target().lastScaledTaken());
      if (chipExcessFlag2 && !chipExcessFlag3) {
         return null;
      } else if (this.formatPendingWeapon(ctx, view, entity, chipExcessValue, chipExcessFlag, excessChipTime, chipExcessFlag3) != null) {
         return null;
      } else {
         boolean iframeComboFlag = this.getHeldAvailable(ctx) != 0;
         if (!chipExcessFlag
            && !this.airRefBareData.ref()
            && IframeComboUtils.canFlag(view.h(), ctx.loadout() == null ? 0 : ctx.loadout().knockback(), iframeComboFlag, this.getMin(excessChipTime))) {
            this.anchor = "hug";
            return null;
         } else {
            VecPos.Loadout loadout = ctx.loadout();
            boolean chipExcessFlag5 = loadout == null || loadout.crystalSlot() < 0;
            float chipExcessLimit = chipExcessFlag3 ? 0.9F : IframeComboUtils.getAmount(this.getMin(excessChipTime), iframeComboFlag);
            if ((chipExcessFlag3 || !chipExcessFlag5) && ctx.self().swordCharge() < chipExcessLimit) {
               this.anchor = "charge";
               return null;
            } else {
               boolean vecPosFlag = ctx.sprint() != null && ctx.sprint().isSprinting();
               boolean eyeCenterFlag = !chipExcessFlag && VecPosUtilsAmount.canFlag(vecPosFlag, ctx.self().swordCharge());
               Vec3d vec3d = this.vecTime;
               this.canPitchEye(view, excessChipTime, vec3d);
               this.stgPosPending = null;
               this.excessChipTimeIframe = excessChipTime;
               LivingEntityHelperVisible.Seed seed = ctx.tracker().getDataGroundMax();
               this.iframeKickModel = seed == null ? null : getCalLoadout(ctx, view, seed, chipExcessFlag);
               this.excessChipTime = excessChipTime;
               this.anchor = chipExcessFlag3 ? "excess" : "chip";
               return this.getLoopHit(
                  ComboingIntersects.Phase.STRIKE,
                  excessChipTime,
                  0.0,
                  SurroundKitType.MELEE,
                  null,
                  getEyeCenter(view, chipExcessValue, eyeCenterFlag, vec3d),
                  null,
                  null,
                  this.getAmount(view),
                  vec3d,
                  "Hitting"
               );
            }
         }
      }
   }

   private static Vec3d getMinEye(ComboingIntersects.View view, LivingEntity entity) {
      Box targetBox = view.targetBox();
      Vec3d center = targetBox.getCenter();
      double[][] doubles = IframeComboUtils.getAmounts(targetBox.minX, targetBox.minY, targetBox.minZ, targetBox.maxX, targetBox.maxY, targetBox.maxZ, center.x - view.eye().x, center.z - view.eye().z);

      for (double[] eyeValues : doubles) {
         Vec3d vec3d = new Vec3d(eyeValues[0], eyeValues[1], eyeValues[2]);
         if (canFlag(view.eye(), vec3d, entity)) {
            return vec3d;
         }
      }

      return null;
   }

   private String formatText(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, LivingEntity entity, int comboingIntersectsValue, boolean comboingIntersectsFlag, long freshLimit) {
      return this.formatPendingWeapon(ctx, view, entity, comboingIntersectsValue, comboingIntersectsFlag, freshLimit, false);
   }

   private static Vec3d getEye(ComboingIntersects.View view, Vec3d vec, int eyeValue) {
      return eyeValue <= 1 ? view.eye() : vec.add(0.0, 1.62, 0.0);
   }

   private CenterVecData getAnchorComboing(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ComboingIntersects.WindowPlan windowPlan, PosVecUtils.Path path, int anchorComboingLimit, boolean anchorComboingFlag
   ) {
      BlockPos cell = windowPlan.cell();
      WorldCollisionHelper world = ctx.world();
      boolean hasWorld = world.findCrystalAt(cell) != null;
      boolean canPlaceOnBedrockObsidian = world.canPlaceOnBedrockObsidian(cell);
      CenterVecData.CrystalStep crystalStep = null;
      String anchorComboingText = anchorComboingFlag ? "air:" : "prep:";
      Vec3d vec3d;
      if (hasWorld) {
         vec3d = BlockEntityUtils.getVec3d(cell).add(0.0, 0.5, 0.0);
         this.anchor = anchorComboingText + "hold";
      } else if (!canPlaceOnBedrockObsidian) {
         vec3d = getVec3d(cell, windowPlan.support(), windowPlan.face());
         if (anchorComboingLimit > windowPlan.obsRd() && !this.canIntersectsFace(ctx, view, path, windowPlan, false)) {
            this.anchor = anchorComboingText + "move";
         } else {
            crystalStep = new CenterVecData.CrystalStep(cell, cell, Direction.UP, true, false, true, windowPlan.feetAt(), windowPlan.boxAt(), windowPlan.support(), windowPlan.face());
            this.anchor = anchorComboingText + "obs";
         }
      } else {
         vec3d = WorldCollisionHelper.getTopCenter(cell);
         if (anchorComboingLimit > windowPlan.cryRd() && (!anchorComboingFlag || !this.canIntersectsFace(ctx, view, path, windowPlan, true))) {
            this.anchor = anchorComboingText + "move";
         } else {
            crystalStep = new CenterVecData.CrystalStep(cell, null, Direction.UP, true, false, true, windowPlan.feetAt(), windowPlan.boxAt());
            this.anchor = anchorComboingText + "cry";
         }
      }

      CenterVecData.Stand followCrystal = getFollowCrystal(view, cell, Math.min(windowPlan.feetAt().y, view.anchor().y));
      if (crystalStep == null && !hasWorld && anchorComboingFlag && (view.readyIn() <= 0 && this.playstyleTerminator || IframeComboUtils.canFlag(view.readyIn(), ctx.target().lastScaledTaken()))) {
         String anchor = this.anchor;
         CenterVecData fillSpam = this.getFillSpam(ctx, view, followCrystal, vec3d);
         if (fillSpam != null) {
            return fillSpam;
         }

         this.anchor = anchor;
      }

      return this.getLoopHit(
         ComboingIntersects.Phase.PREPARE,
         ctx.tick(),
         windowPlan.value(),
         crystalStep == null ? SurroundKitType.NONE : SurroundKitType.CRYSTAL,
         crystalStep,
         null,
         null,
         null,
         followCrystal,
         vec3d,
         "Comboing"
      );
   }

   private void handle(BlockPos pos, long comboingIntersectsValue) {
      if (pos != null) {
         this.iframePositionMap.put(pos.asLong(), comboingIntersectsValue + 20L);
      }
   }

   private static Vec3d getVec3d(PosVecUtils.Sample sample) {
      return new Vec3d(sample.x(), sample.y(), sample.z());
   }

   private void handleBreakTick(ComboingIntersects.Ctx ctx, long comboingIntersectsLimit) {
      ComboingIntersects.Episode episode = this.kindEpisode;
      if (episode != null && episode.doneOpenType == ComboingIntersects.Outcome.OPEN) {
         ComboingIntersects.FollowUp posBlockData4 = episode.posBlockData;
         TickStatusData tickStatusData = ctx.snap() == null ? TickStatusData.INSTANCE : ctx.snap();
         if (posBlockData4.kind() != ComboingIntersects.Kind.ANCHOR
            && tickStatusData.base() != null
            && tickStatusData.lastBreakTick() != Long.MIN_VALUE
            && tickStatusData.lastBreakTick() > episode.startTick
            && posBlockData4.cell().equals(tickStatusData.base())) {
            this.handleDoneOpen(episode, tickStatusData.lastBreakTick());
         } else {
            long comboingIntersectsBound = Math.max(0L, posBlockData4.detTick() - episode.startTick);
            if (comboingIntersectsLimit > episode.startTick + IframeComboUtils.getMaxForInt((int)Math.min(2147483647L, comboingIntersectsBound))) {
               this.handleOpenDone(episode);
            }
         }
      }
   }

   private void handleIframeLos(ComboingIntersects.View view, LivingEntity entity, long time, String iframeLosText) {
      boolean iframeLosFlag = iframeLosText == null || "los".equals(iframeLosText);
      boolean equals = "los".equals(iframeLosText);
      if ("iframe".equals(iframeLosText) && BlockEntityUtils.getMinMax(view.eye(), view.targetBox()) <= 3.0) {
         iframeLosFlag = true;
         equals = this.getVecTime(view, entity, time) == null;
      }

      if (iframeLosFlag) {
         this.iframeValue = equals ? this.iframeValue + 1 : 0;
      }

      if (IframeComboUtils.canImmuneStall(iframeLosText) || "iframe".equals(iframeLosText) && BlockEntityUtils.getMinMax(view.eye(), view.targetBox()) > 3.0) {
         this.iframeValue = 0;
      }

      if (this.iframeValue >= 2) {
         if (this.iframeTime == Long.MIN_VALUE || time > this.iframeTime) {
            this.iframeLosValue = time;
         }

         this.iframeTime = time + 20L;
         this.iframeLosAmount = IframeComboUtils.getValue(time - this.iframeLosValue) * 45.0;
      } else if (this.iframeTime == Long.MIN_VALUE || time > this.iframeTime) {
         this.iframeLosAmount = 0.0;
      }
   }

   private CenterVecData getDigComboing(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, LivingEntity entity) {
      long tick = ctx.tick();
      if (entity.isTouchingWater()) {
         return null;
      } else if (!IframeComboUtils.canFlag(view.us().y - view.level(), view.grounded(), view.h(), view.lowOk(), IframeComboUtils.getTime(tick, this.airValue), view.pocket())) {
         return null;
      } else {
         ComboingIntersects.Episode kindEpisode174 = this.kindEpisode;
         if (kindEpisode174 != null && kindEpisode174.doneOpenType == ComboingIntersects.Outcome.OPEN) {
            return null;
         } else {
            ComboingIntersects.WindowPlan posBlockData6 = this.posBlockData;
            if (posBlockData6 != null && ctx.world().findCrystalAt(posBlockData6.cell()) != null) {
               return null;
            } else {
               CaseMaxData options = ctx.s().getOptions(SurroundKitType.ANCHOR);
               if ("dig".equals(options.why()) && this.canAnchorsFresh(ctx) && options.available() && options.value() >= 3.0) {
                  if ((view.pocket() || view.readyIn() <= getPrepTicks(ctx, options)) && !this.canTimes(1, tick)) {
                     this.posBlockData = null;
                     this.anchor = "dig";
                     return this.getLoopHit(
                        ComboingIntersects.Phase.RANGE,
                        tick,
                        getAmount(options.value()),
                        SurroundKitType.ANCHOR,
                        null,
                        null,
                        null,
                        null,
                        this.getAmount(view),
                        getSample(view),
                        "Comboing"
                     );
                  } else {
                     return null;
                  }
               } else {
                  return null;
               }
            }
         }
      }
   }

   private static Box getBox(PosVecUtils.Sample sample) {
      return WorldCollisionHelper.getPlayerBoxAt(sample.x(), sample.y(), sample.z(), 0.0);
   }

   private CenterVecData getCenterVecData(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      return this.getFillSpam(ctx, view, null, null);
   }

   private EmptyLvl.Pick getLvlEmpty(ComboingIntersects.Ctx ctx, Vec3d vec, double lvlEmptyValue, Vec3d vec2, double lvlEmptyValue2, boolean lvlEmptyFlag) {
      int value = ctx.target().id();
      boolean emptyFlag = value != this.lvlEmptyValue || IframeComboUtils.canFlag(IframeComboUtils.getTime(ctx.tick(), this.airValue));
      this.lvlEmptyValue = value;
      if (!(lvlEmptyValue2 <= 10.8)) {
         this.emptyLvl.handleEmptyTime("far");
         return null;
      } else {
         ClientWorld world = MinecraftClient.getInstance().world;
         if (world == null) {
            this.emptyLvl.handleEmptyTime("empty");
            return null;
         } else {
            return this.emptyLvl
               .getEmptyTime(
                  ctx.tick(), EmptyLvl.getApiForWorld(world), vec.x, vec.y, vec.z, lvlEmptyValue, vec2.x, vec2.y, vec2.z, getTargetThreat(ctx), getThreatSelf(ctx, vec2), emptyFlag, lvlEmptyFlag
               );
         }
      }
   }

   private CenterVecData getAnchorComboing(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ComboingIntersects.FollowUp followUp) {
      CenterVecData.AnchorStep anchorStep = new CenterVecData.AnchorStep(followUp.cell(), true, followUp.feetAt(), followUp.boxAt());
      if ("-".equals(this.anchor)) {
         this.anchor = "anchor";
      }

      Vec3d vec3d = followUp.support() != null && ctx.world().isReplaceableAt(followUp.cell()) ? BlockEntityUtils.getVec3d(followUp.support(), followUp.face()) : Vec3d.ofCenter(followUp.cell());
      return this.getLoopHit(
         ComboingIntersects.Phase.FOLLOW_UP,
         ctx.tick(),
         followUp.value(),
         SurroundKitType.ANCHOR,
         null,
         null,
         null,
         anchorStep,
         getFollowLow(view, followUp.feetAt(), null),
         vec3d,
         "Comboing"
      );
   }

   private void handleGround(
      ComboingIntersects.Ctx ctx,
      ComboingIntersects.View view,
      ClientPlayerEntity clientPlayerEntity,
      LivingEntity entity,
      PosVecUtils.Path path,
      int groundValue,
      int iframeComboBound,
      boolean groundFlag,
      boolean eyeFlag,
      double squaredFactor,
      float lastScaledTaken,
      Vec3d vec,
      PosVecUtils.Sample sample,
      Set<Long> set,
      List<ComboingIntersects.Cand> list
   ) {
      int floor5 = (int)Math.floor(sample.x());
      int floor6 = (int)Math.floor(sample.z());
      int round7 = (int)Math.round(sample.y());
      byte groundLimit = 2;
      int groundValue8 = sample.onGround() ? 0 : 1;

      for (int i = groundValue8; i <= IframeComboUtils.getValue(sample.onGround()); i++) {
         for (int j = -groundLimit; j <= groundLimit; j++) {
            for (int k = -groundLimit; k <= groundLimit; k++) {
               BlockPos blockPos = new BlockPos(floor5 + j, round7 - i, floor6 + k);
               if (!(Math.abs(blockPos.getX() + 0.5 - sample.x()) > groundLimit) && !(Math.abs(blockPos.getZ() + 0.5 - sample.z()) > groundLimit) && set.add(blockPos.asLong())) {
                  ComboingIntersects.Cand intersectsBuild = this.getIntersectsBuild(
                     ctx, view, clientPlayerEntity, entity, path, groundValue, iframeComboBound, groundFlag, eyeFlag, squaredFactor, lastScaledTaken, vec, blockPos
                  );
                  if (intersectsBuild != null) {
                     list.add(intersectsBuild);
                  }
               }
            }
         }
      }
   }

   private static Vec3d getVec3d(ComboingIntersects.View view, BlockPos pos, double vec3dValue) {
      return getVec3d(view, pos, getAnchor(view, pos, vec3dValue));
   }

   private static Vec3d getVec3d(BlockPos pos, BlockPos pos2, Direction direction) {
      return pos2 != null && direction != null ? BlockEntityUtils.getVec3d(pos2, direction) : getVec3d(pos);
   }

   private boolean canTimeFresh(ComboingIntersects.Ctx ctx, long freshLimit) {
      return this.iframeLastKitTime != Long.MIN_VALUE && freshLimit > this.iframeLastKitTime
         ? IframeComboUtils.canFlag(freshLimit - this.iframeLastKitTime, (int)Math.ceil(Math.max(0.0, ctx.self().loopMs()) / 50.0), ctx.target().ticksSinceFresh())
         : false;
   }

   private Vec3d getLoadoutWorld(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, PosVecUtils.Path path, int loadoutWorldValue) {
      if (path == null) {
         return null;
      } else {
         VecPos.Loadout loadout = ctx.loadout();
         if (loadout != null && loadout.crystalSlot() >= 0) {
            BlockPos intersectsDistance = this.getIntersectsDistance(view.us(), getVec3d(path.getDataForInt(loadoutWorldValue)));
            if (intersectsDistance == null) {
               return null;
            } else if (ctx.world().findCrystalAt(intersectsDistance) != null) {
               return BlockEntityUtils.getVec3d(intersectsDistance).add(0.0, 0.5, 0.0);
            } else {
               return ctx.world().canPlaceOnBedrockObsidian(intersectsDistance) ? WorldCollisionHelper.getTopCenter(intersectsDistance) : getOppositeSquared(intersectsDistance, view.eye());
            }
         } else {
            return null;
         }
      }
   }

   public String getLoopText() {
      return this.loopText;
   }

   private boolean canPitchEye(ComboingIntersects.View view, long time, Vec3d vec) {
      boolean flag = IframeComboUtils.canFlagForDouble(WorldCollisionHelper.computeAngleFromRotation(view.yaw(), view.pitch(), view.eye(), vec));
      this.pitchEyeValue = time;
      this.iframeActive = flag;
      return flag;
   }

   private boolean canFlag(BlockPos pos) {
      return pos != null && this.iframePositionMap.containsKey(pos.asLong());
   }

   private static CenterVecData.Stand getFollowLow(ComboingIntersects.View view, double followLowValue) {
      if (view.pocket()) {
         return getFollowAbove(view, null);
      } else {
         Vec3d anchor = view.anchor();
         double groundedFeet = getGroundedFeet(view);
         Vec3d lengthSquared = getLengthSquared(view, anchor.x, anchor.z);
         double[] followLowValues = IframeComboUtils.getAmounts(anchor.x, anchor.z, lengthSquared.x, lengthSquared.z, followLowValue);
         Vec3d vec3d = getVec3d(view, null, new Vec3d(followLowValues[0], groundedFeet, followLowValues[1]));
         boolean followLowFlag = IframeComboUtils.canFlag(view.h());
         return new CenterVecData.Stand(
            CenterVecData.StandKind.FOLLOW, vec3d, 0.35, followLowFlag, !followLowFlag, Long.MIN_VALUE, new Vec3d(anchor.x, groundedFeet, anchor.z), PreferAnchorData.Kind.MELEE_LOW
         );
      }
   }

   private boolean canIntersectsFace(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, PosVecUtils.Path path, ComboingIntersects.WindowPlan windowPlan, boolean intersectsFaceFlag
   ) {
      BlockPos cell = windowPlan.cell();
      Box box = intersectsFaceFlag ? BlockEntityUtils.getBox3(cell) : BlockEntityUtils.getBoxForBlockPos(cell);
      if (!box.intersects(view.targetBox()) && !box.intersects(view.ourBox())) {
         if (path == null || !box.intersects(getBoxForSample(path.getDataForInt(1))) && !box.intersects(getBoxForSample(path.getDataForInt(2)))) {
            if (!intersectsFaceFlag && path != null && canIntersects(path, box, windowPlan.window() - ctx.tick())) {
               return false;
            } else if (intersectsFaceFlag && ctx.world().hasBlockingEntities(cell, ctx.target().entity(), MinecraftClient.getInstance().player)) {
               return false;
            } else {
               double intersectsFaceLimit = getAmount(canFreshSince(ctx));
               Vec3d vec3d = intersectsFaceFlag ? WorldCollisionHelper.getTopCenter(cell) : getVec3d(cell, windowPlan.support(), windowPlan.face());
               if (!(view.eye().distanceTo(vec3d) <= intersectsFaceLimit)) {
                  return false;
               } else {
                  return intersectsFaceFlag
                     ? BlockEntityUtils.getAmountsSquared(cell, BlockEntityUtils.idleDirections, null, view.eye(), view.yaw(), view.pitch(), intersectsFaceLimit)
                        != null
                     : windowPlan.support() != null
                        && windowPlan.face() != null
                        && BlockEntityUtils.getAmountsSquared(
                              windowPlan.support(), new Direction[]{windowPlan.face()}, windowPlan.face(), view.eye(), view.yaw(), view.pitch(), intersectsFaceLimit
                           )
                           != null;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private CenterVecData getPosBlock(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntity entity) {
      int readyIn = view.readyIn();
      if (readyIn <= 0 || !(view.h() <= 6.0)) {
         return null;
      } else if (!view.grounded() && ctx.tracker().isSampleGround() && !entity.isTouchingWater()) {
         ComboingIntersects.Episode kindEpisode174 = this.kindEpisode;
         if (kindEpisode174 != null && kindEpisode174.doneOpenType == ComboingIntersects.Outcome.OPEN) {
            return null;
         } else {
            VecPos.Loadout loadout = ctx.loadout();
            long time = ctx.tick();
            if (loadout != null && loadout.crystalSlot() >= 0 && !this.canTimes(0, time)) {
               PosVecUtils.Path calToward = this.getCalToward(ctx, view, ctx.tracker().getDataGroundMax(), 40);
               if (calToward == null) {
                  return null;
               } else {
                  int posBlockLimit = IframeComboUtils.getValue(calToward.getValueGround(), calToward.getValue(), readyIn + 1);
                  int[] posBlockValues = getValues(readyIn, posBlockLimit, calToward.getValue());
                  ComboingIntersects.WindowPlan posBlockData6 = this.posBlockData;
                  if (posBlockData6 != null) {
                     for (int posBlockValue : posBlockValues) {
                        ComboingIntersects.WindowPlan posBlockData = this.getIntersectsCry(ctx, view, calToward, posBlockData6, posBlockValue);
                        if (posBlockData != null) {
                           this.posBlockData = posBlockData;
                           return this.getAnchorComboing(ctx, view, posBlockData, calToward, posBlockValue, true);
                        }
                     }
                  }

                  for (int posBlockValuesItem : posBlockValues) {
                     ComboingIntersects.WindowPlan windowPos = this.getWindowPos(ctx, view, clientPlayerEntity, entity, calToward, posBlockValuesItem);
                     if (windowPos != null) {
                        this.posBlockData = windowPos;
                        return this.getAnchorComboing(ctx, view, windowPos, calToward, posBlockValuesItem, true);
                     }
                  }

                  int posBlockBound = IframeComboUtils.getValue(BoxLengthUtils.getMax(ctx.self().pingMs()));
                  int posBlockLimit2 = posBlockLimit > 0 ? posBlockLimit : calToward.getValue();

                  for (int i = Math.max(readyIn + 1, posBlockBound); i <= posBlockBound + 1 && i < posBlockLimit2; i++) {
                     ComboingIntersects.WindowPlan windowPlan = this.getWindowPos(ctx, view, clientPlayerEntity, entity, calToward, i);
                     if (windowPlan != null) {
                        this.posBlockData = windowPlan;
                        return this.getAnchorComboing(ctx, view, windowPlan, calToward, i, true);
                     }
                  }

                  this.posBlockValue = time;
                  return null;
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private CenterVecData getLoopHit(
      ComboingIntersects.Phase prepareStrikeType,
      long time,
      double loopHitValue,
      SurroundKitType surroundKitType,
      CenterVecData.CrystalStep crystalStep,
      CenterVecData.Strike strike,
      CenterVecData.PearlShot pearlShot,
      CenterVecData.AnchorStep anchorStep,
      CenterVecData.Stand stand,
      Vec3d vec,
      String loopHitText
   ) {
      if (prepareStrikeType == ComboingIntersects.Phase.PEARL && (this.prepareStrikeType != ComboingIntersects.Phase.PEARL || this.loopHitValue != time - 1L)) {
         this.loopHitCount++;
      }

      if (prepareStrikeType != ComboingIntersects.Phase.STRIKE) {
         this.kitCount = 0;
      }

      this.prepareStrikeType = prepareStrikeType;
      this.surroundKitType = surroundKitType;
      this.loopHitValue = time;
      this.loopText = this.formatStgPos(prepareStrikeType, time, pearlShot);
      return new CenterVecData(
         CenterVecData.State.HIT_LOOP,
         CenterVecData.Proposal.NONE,
         getAmount(loopHitValue),
         CenterVecData.Abort.NONE,
         surroundKitType,
         crystalStep,
         strike,
         pearlShot,
         anchorStep,
         stand,
         vec,
         Integer.MAX_VALUE,
         0,
         loopHitText,
         "HIT_LOOP"
      );
   }

   private static Vec3d getVec3d(ComboingIntersects.View view, BlockPos pos, Vec3d vec) {
      Vec3d intersectsLow = getIntersectsLow(view, pos);
      return intersectsLow != null ? intersectsLow : vec;
   }

   private static boolean canIntersects(PosVecUtils.Path path, Box box, long intersectsBound) {
      int intersectsLimit = (int)Math.min((long)(path.getValue() - 1), Math.max(0L, intersectsBound));

      for (int i = 3; i <= intersectsLimit; i++) {
         if (box.intersects(getBoxForSample(path.getDataForInt(i)))) {
            return true;
         }
      }

      return false;
   }

   public CenterVecData.Stand getChasingApproach(long chasingApproachValue) {
      return chasingApproachValue == this.approachChasingTime ? this.vecData : null;
   }

   private CenterVecData getRefAnchor(
      ComboingIntersects.Ctx ctx,
      ComboingIntersects.View view,
      ClientPlayerEntity clientPlayerEntity,
      LivingEntityHelperVisible.Seed seed,
      LivingEntity entity,
      int refAnchorValue,
      boolean refAnchorFlag
   ) {
      long time = ctx.tick();
      CaseMaxData options = ctx.s().getOptions(SurroundKitType.CRYSTAL);
      CaseMaxData caseMaxData = ctx.s().getOptions(SurroundKitType.ANCHOR);
      if (ctx.s().committed() == SurroundKitType.ANCHOR && caseMaxData.available() && !options.kills()) {
         return this.getComboingAnchor(ctx, view, caseMaxData, "ref:anc");
      } else {
         boolean refAnchorFlag2 = this.canAnchorsFresh(ctx) && caseMaxData.available() && Double.isFinite(caseMaxData.value()) && caseMaxData.value() > 0.0 && !this.canTimes(1, time);
         double meleeAvailable = this.getMeleeAvailable(ctx, view);
         ComboingIntersects.Episode kindEpisode174 = this.kindEpisode;
         VecPos.Loadout loadout = ctx.loadout();
         ComboingIntersects.FollowUp followUp = null;
         if ((kindEpisode174 == null || kindEpisode174.doneOpenType != ComboingIntersects.Outcome.OPEN)
            && loadout != null
            && loadout.crystalSlot() >= 0
            && view.h() <= 6.0
            && !this.canTimes(0, time)) {
            followUp = this.getIdxDet(ctx, view, clientPlayerEntity, entity, seed, this.getCalToward(ctx, view, seed, 16), 0, 0.0F, null);
         }

         boolean refAnchorFlag3 = followUp != null
            && refAnchorFlag2
            && IframeComboUtils.canFlag(caseMaxData.pops(), caseMaxData.value(), followUp.damage() >= getCalibration(ctx.target()), followUp.value());
         if (followUp != null && !refAnchorFlag3) {
            CenterVecData meleeKills = this.getMeleeKills(ctx, view, entity, refAnchorValue, refAnchorFlag, meleeAvailable, followUp.damage(), followUp.damage() >= getCalibration(ctx.target()));
            if (meleeKills != null) {
               return meleeKills;
            } else {
               this.kindEpisode = new ComboingIntersects.Episode(time, ZERO_KICK_MODEL, false, 0.0F, followUp);
               this.touchingValue = time;
               this.touchStreak++;
               this.anchor = "ref";
               return this.getComboingBuild(ctx, view, followUp, ctx.world().findCrystalAt(followUp.cell()) != null);
            }
         } else {
            boolean iframeComboFlag = IframeComboUtils.canComboHeld(options.available(), options.why(), options.value()) && !this.canTimes(0, time);
            double iframeComboValue = IframeComboUtils.getAmount(options.why(), options.value());
            int refAnchorValue3 = refAnchorFlag3 ? 2 : IframeComboUtils.getValue(iframeComboFlag, iframeComboValue, refAnchorFlag2, caseMaxData.value());
            boolean refAnchorFlag5 = refAnchorValue3 == 2;
            if (refAnchorValue3 != 0) {
               CenterVecData meleeKills2 = this.getMeleeKills(ctx, view, entity, refAnchorValue, refAnchorFlag, meleeAvailable, refAnchorFlag5 ? caseMaxData.value() : iframeComboValue, refAnchorFlag5 ? caseMaxData.pops() : options.pops());
               if (meleeKills2 != null) {
                  return meleeKills2;
               }
            }

            if (refAnchorFlag5) {
               return this.getComboingAnchor(ctx, view, caseMaxData, "ref:anchor");
            } else if (refAnchorValue3 != 1) {
               return null;
            } else {
               this.anchor = "ref:fill";
               return this.getLoopHit(
                  ComboingIntersects.Phase.RANGE,
                  time,
                  getAmount(iframeComboValue),
                  SurroundKitType.CRYSTAL,
                  null,
                  null,
                  null,
                  null,
                  this.getWatchSeen(ctx, view),
                  this.getWorldSnap(ctx, view),
                  "Comboing"
               );
            }
         }
      }
   }

   private static Vec3d getLengthSquared(ComboingIntersects.View view, double lengthSquaredValue, double vec3dValue2) {
      Vec3d vec3d = LivingEntityHelperVisible.getVec3d(new Vec3d(lengthSquaredValue - view.us().x, 0.0, vec3dValue2 - view.us().z));
      return vec3d.lengthSquared() < 0.5 ? view.u() : vec3d;
   }

   private static Vec3d getOppositeSquared(BlockPos pos, Vec3d vec) {
      Vec3d oppositeSquared = null;
      double oppositeSquaredLimit = Double.POSITIVE_INFINITY;

      for (Direction direction : Direction.values()) {
         BlockPos offset = pos.offset(direction);
         Direction opposite = direction.getOpposite();
         if (BlockEntityUtils.canBlockState(offset, opposite)) {
            Vec3d vec3d = BlockEntityUtils.getVec3d(offset, opposite);
            double squaredDistanceTo = vec.squaredDistanceTo(vec3d);
            if (squaredDistanceTo < oppositeSquaredLimit) {
               oppositeSquaredLimit = squaredDistanceTo;
               oppositeSquared = vec3d;
            }
         }
      }

      return oppositeSquared;
   }

   private CenterVecData getReplanLevel(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntityHelperVisible.Seed seed, LivingEntity entity
   ) {
      ComboingIntersects.Episode episode = this.kindEpisode;
      if (episode != null && episode.doneOpenType == ComboingIntersects.Outcome.OPEN) {
         ComboingIntersects.FollowUp posBlockData4 = episode.posBlockData;
         if (posBlockData4.kind() == ComboingIntersects.Kind.ANCHOR) {
            return this.getAnchorComboing(ctx, view, posBlockData4);
         } else {
            boolean hasWorld = ctx.world().findCrystalAt(posBlockData4.cell()) != null;
            if (!hasWorld) {
               PosVecUtils.Path maxTracker = this.getMaxTracker(ctx, view, episode, seed);
               ComboingIntersects.FollowUp posBlockData = maxTracker == null ? null : this.getSpaceLevel(ctx, view, episode, maxTracker, seed);
               if (posBlockData == null) {
                  String anchor = maxTracker == null ? "fu:level" : this.levelSpaceText;
                  ComboingIntersects.FollowUp followUp = null;
                  if (maxTracker != null) {
                     followUp = this.getIdxDet(ctx, view, clientPlayerEntity, entity, seed, maxTracker, 0, episode.levelAmount, null);
                     if (followUp == null) {
                        followUp = this.getIntersectsFloored(ctx, view, clientPlayerEntity, entity, maxTracker, 0, episode.levelAmount, null);
                     }
                  }

                  if (followUp == null) {
                     this.anchor = anchor;
                     this.replanText = anchor;
                     if (++episode.replanValue >= 2) {
                        this.handleOpenDone(episode);
                     }

                     return null;
                  }

                  this.anchor = "replan";
                  posBlockData = followUp;
               }

               episode.replanValue = 0;
               episode.posBlockData = posBlockData;
               posBlockData4 = posBlockData;
               if (posBlockData.kind() == ComboingIntersects.Kind.ANCHOR) {
                  return this.getAnchorComboing(ctx, view, posBlockData);
               }
            }

            return this.getComboingBuild(ctx, view, posBlockData4, hasWorld);
         }
      } else {
         return null;
      }
   }

   private boolean canSinceOwn(ComboingIntersects.Ctx ctx) {
      return !this.airRefBareData.on() && ctx.s().ticksSinceOwnPop() <= 10;
   }

   private static CenterVecData.Stand getFollowAbove(ComboingIntersects.View view, BlockPos pos) {
      Vec3d intersectsLow = getIntersectsLow(view, pos);
      if (intersectsLow != null) {
         return new CenterVecData.Stand(CenterVecData.StandKind.FOLLOW, intersectsLow, 0.35, false, true, Long.MIN_VALUE);
      } else if (IframeComboUtils.canFlag3(view.above(), view.h())) {
         return new CenterVecData.Stand(CenterVecData.StandKind.FOLLOW, view.us(), 0.35, false, true, Long.MIN_VALUE);
      } else {
         Vec3d anchor = view.anchor();
         double followAboveValue = view.above() > 0.5 ? view.level() : view.us().y;
         return new CenterVecData.Stand(
            CenterVecData.StandKind.FOLLOW, getVec3d(view, anchor.x, anchor.z, followAboveValue, 3.0), 0.35, IframeComboUtils.canFlag(view.h()), true, Long.MIN_VALUE
         );
      }
   }

   private Vec3d getTrackerVec(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      Vec3d vec3d2 = this.prepAnchorVec;
      if (vec3d2 == null && (view.readyIn() <= 0 || this.airRefBareData.ref()) && IframeComboUtils.canFlagForDoubleInt(view.h(), 0)) {
         LivingEntityHelperVisible.Seed seed = ctx.tracker().getDataGroundMax();
         vec3d2 = this.getLoadoutWorld(ctx, view, this.getCalToward(ctx, view, seed, 3), 2);
      }

      return vec3d2 != null ? vec3d2 : getSample(view);
   }

   private static char getCh(ComboingIntersects.Kind kind) {
      return switch (kind) {
         case BUILD -> 'B';
         case DIRECT -> 'D';
         case ARMED -> 'X';
         case ANCHOR -> 'A';
      };
   }

   private boolean canPingSelf(ComboingIntersects.Ctx ctx, int pingSelfValue) {
      return IframeComboUtils.canFlagForInt(pingSelfValue, BoxLengthUtils.getMax(ctx.self().pingMs()));
   }

   private static EmptyLvl.Threat getThreatSelf(ComboingIntersects.Ctx ctx, Vec3d vec) {
      VecBlock threat = ctx.threat();
      AbsorptionPosData self = ctx.self();
      if (threat != null && self != null) {
         double threatSelfLimit = 0.5 * self.getAbsorptionHealth();
         if (!(threatSelfLimit > 0.0)) {
            return null;
         } else {
            double[] doubles = new double[]{Double.NaN};
            return (x2, y2, z2) -> {
               float maxPower = threat.getMaxPower(new Vec3d(x2, y2, z2), WorldCollisionHelper.getPlayerBoxAt(x2, y2, z2, 0.0), null);
               if (maxPower < threatSelfLimit) {
                  return true;
               } else {
                  if (Double.isNaN(doubles[0])) {
                     doubles[0] = vec == null ? Double.NEGATIVE_INFINITY : threat.getMaxPower(vec, WorldCollisionHelper.getPlayerBoxAt(vec.x, vec.y, vec.z, 0.0), null);
                  }

                  return EmptyLvl.canFlag(maxPower, threatSelfLimit, doubles[0]);
               }
            };
         }
      } else {
         return null;
      }
   }

   private static boolean canFlag(Vec3d vec, BlockPos pos, double comboingIntersectsValue) {
      return BlockEntityUtils.getMinMax(vec, BlockEntityUtils.getBox(pos)) < 3.0 - comboingIntersectsValue;
   }

   private static Vec3d getVec3d(ComboingIntersects.View view, int vec3dValue, boolean vec3dFlag) {
      return view.us().add(getMultiply(view, vec3dValue, vec3dFlag));
   }

   private static double getAnchor(ComboingIntersects.View view, double anchorValue, boolean anchorFlag) {
      return anchorFlag ? anchorValue : view.anchor().y;
   }

   private CenterVecData.Stand getWatchSeen(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      if (view.pocket()) {
         return getFollowAbove(view, null);
      } else if (ctx.s().committed() == SurroundKitType.ANCHOR) {
         return this.getAmount(view);
      } else {
         double naN = Double.NaN;
         int watchSeenValue = -1;
         VecMax.Forecast forecast = ctx.watch() == null ? null : ctx.watch().getVecMaxForInt(ctx.target().id());
         if (forecast != null && forecast.landing() != null) {
            naN = WorldCollisionHelper.horizontalDistance(view.us(), forecast.landing());
            long watchSeenValue2 = forecast.seenTick() == Long.MIN_VALUE ? 0L : Math.max(0L, ctx.tick() - forecast.seenTick());
            watchSeenValue = (int)Math.max(0L, forecast.eta() - watchSeenValue2);
         }

         boolean watchSeenFlag = IframeComboUtils.canFlag(naN, watchSeenValue);
         if (watchSeenFlag && !view.aboveNear()) {
            return new CenterVecData.Stand(CenterVecData.StandKind.HOLD, view.us(), 0.1, false, true, Long.MIN_VALUE);
         } else if (!watchSeenFlag && !IframeComboUtils.canFlag4(view.h())) {
            return this.getAmount(view);
         } else {
            Vec3d anchor = view.anchor();
            double groundedFeet = getGroundedFeet(view);
            Vec3d lowAnchor = getLowAnchor(view);
            if (lowAnchor == null) {
               lowAnchor = getVec3d(view, anchor.x, anchor.z, groundedFeet, 1.1);
            }

            return new CenterVecData.Stand(
               CenterVecData.StandKind.FOLLOW, lowAnchor, 0.3, true, true, Long.MIN_VALUE, new Vec3d(anchor.x, groundedFeet, anchor.z), PreferAnchorData.Kind.MELEE_LOW
            );
         }
      }
   }

   private static AmountConstTime.Spot getTargetThreat(ComboingIntersects.Ctx ctx) {
      VecBlock threat = ctx.threat();
      AmountConstTime amountConstTime = threat != null && ctx.target() != null ? threat.getApply(ctx.target().id()) : null;
      return amountConstTime == null ? null : amountConstTime.getTimeAmount(ctx.tick());
   }

   private double getPosBlock(BlockPos pos) {
      return this.blockPos != null && this.blockPos.equals(pos) ? 1.0 : 0.0;
   }

   private void handleOpenKind(long kindValue, ComboingIntersects.Pending pending, boolean kindFlag) {
      ComboingIntersects.Episode episode = this.kindEpisode;
      if (episode != null && episode.doneOpenType == ComboingIntersects.Outcome.OPEN) {
         this.handleOpenDone(episode);
      }

      ComboingIntersects.FollowUp plan = pending.plan();
      if (plan == null) {
         this.kindEpisode = null;
      } else {
         this.kindEpisode = new ComboingIntersects.Episode(kindValue, pending.kick(), kindFlag, pending.meleeRaw(), plan);
         this.openKindCount++;
         if (plan.kind() == ComboingIntersects.Kind.BUILD) {
            this.openKindRepeatCount++;
         } else if (plan.kind() != ComboingIntersects.Kind.ANCHOR) {
            this.kindCount++;
         }
      }
   }

   private CenterVecData getMeleeKills(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, LivingEntity entity, int meleeKillsValue, boolean meleeKillsFlag, double meleeKillsLimit, double meleeKillsValue2, boolean meleeKillsFlag2
   ) {
      if (meleeKillsLimit > 0.0 && !meleeKillsFlag2) {
         CaseMaxData options = ctx.s().getOptions(SurroundKitType.MELEE);
         return meleeKillsLimit > meleeKillsValue2 || options.available() && options.kills() ? this.getChipExcess(ctx, view, entity, meleeKillsValue, meleeKillsFlag) : null;
      } else {
         return null;
      }
   }

   private static boolean canGlowstoneObsidian(ComboingIntersects.Ctx ctx, Vec3d vec, BlockPos pos) {
      BowGappleType main = ctx.target().main();
      if (main != BowGappleType.CRYSTAL && main != BowGappleType.OBSIDIAN && main != BowGappleType.ANCHOR && main != BowGappleType.GLOWSTONE) {
         AmountConstTime.Spot targetThreat = getTargetThreat(ctx);
         return targetThreat != null && IframeComboUtils.canFlag(targetThreat.getX(), targetThreat.getZ(), targetThreat.y(), vec.x, vec.z, pos.getY());
      } else {
         return true;
      }
   }

   private CenterVecData getChasingApproach(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      if ("-".equals(this.anchor)) {
         this.anchor = "approach";
      }

      boolean chasingApproachFlag = IframeComboUtils.canFlag3(view.h());
      CenterVecData.Stand vecData = this.getRoutedSearching(ctx, view);
      this.vecData = vecData;
      this.approachChasingTime = ctx.tick();
      return this.getLoopHit(
         ComboingIntersects.Phase.CLOSE,
         ctx.tick(),
         0.0,
         SurroundKitType.NONE,
         null,
         null,
         null,
         null,
         vecData != null ? vecData : this.getWatchSeen(ctx, view),
         this.getTrackerVec(ctx, view),
         chasingApproachFlag ? "Fighting" : "Chasing"
      );
   }

   private Vec3d getWorldSnap(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      TickStatusData snap = ctx.snap();
      BlockPos blockPos = snap != null && snap.kind() != null ? snap.base() : null;
      if (blockPos == null) {
         Vec3d loadoutWorld = this.getLoadoutWorld(ctx, view, this.pathIframe != null ? this.pathIframe : this.getCalToward(ctx, view, ctx.tracker().getDataGroundMax(), 3), 0);
         return loadoutWorld != null ? loadoutWorld : getSample(view);
      } else if (ctx.world().findCrystalAt(blockPos) != null) {
         return BlockEntityUtils.getVec3d(blockPos).add(0.0, 0.5, 0.0);
      } else if (ctx.world().canPlaceOnBedrockObsidian(blockPos)) {
         return WorldCollisionHelper.getTopCenter(blockPos);
      } else {
         BlockEntityUtils.Support support = ctx.world().isReplaceableAt(blockPos)
            ? BlockEntityUtils.getOppositeImmutable(blockPos, view.eye(), view.yaw(), view.pitch(), getAmount(canFreshSince(ctx)))
            : null;
         return support != null ? getVec3d(blockPos, support.pos(), support.face()) : getSample(view);
      }
   }

   private boolean canFlooredFlag(ComboingIntersects.Ctx ctx, int value, BlockPos floored, Vec3d vec, Vec3d vec2, double flooredValue, boolean flooredFlag) {
      long time = ctx.tick();
      int value2 = ctx.target().id();
      if (value2 != this.flooredLastValue) {
         this.flooredLastValue = value2;
         this.flooredActive = false;
         this.flooredLastTime = Long.MIN_VALUE;
         this.flooredValue = Long.MIN_VALUE;
         this.floored = null;
         this.comboingIntersectsValue = 0;
      }

      boolean iframeComboFlag = IframeComboUtils.canFlag(value, flooredFlag);
      if (iframeComboFlag) {
         this.flooredLastTime = time;
         this.floored = floored;
         this.comboingIntersectsValue = value;
      }

      if (!this.flooredActive && IframeComboUtils.canFlag(iframeComboFlag, canGlowstoneObsidian(ctx, vec, floored), flooredValue)) {
         this.flooredActive = true;
         this.flooredValue = time;
      } else if (this.flooredActive && !iframeComboFlag) {
         double iframeComboValue = this.floored == null
            ? Double.POSITIVE_INFINITY
            : Math.hypot(vec2.x - (this.floored.getX() + 0.5), vec2.z - (this.floored.getZ() + 0.5));
         this.flooredActive = IframeComboUtils.canFlag(IframeComboUtils.getTime(time, this.flooredLastTime), iframeComboValue);
      }

      if (!(flooredValue <= 6.0)) {
         this.flooredActive = false;
      }

      return this.flooredActive;
   }

   private static int getValue(long comboingIntersectsBound, int comboingIntersectsValue) {
      return (int)Math.max(0L, Math.min((long)comboingIntersectsValue, comboingIntersectsBound));
   }

   private static float getCalibration(VecBlockingData vecBlockingData) {
      if (vecBlockingData == null) {
         return Float.POSITIVE_INFINITY;
      } else {
         float calibrationDivisor = vecBlockingData.calibration() > 0.0F && Float.isFinite(vecBlockingData.calibration()) ? vecBlockingData.calibration() : 1.0F;
         return vecBlockingData.getHealthTrusted() / calibrationDivisor;
      }
   }

   private static double getAmount(boolean comboingIntersectsFlag) {
      return 4.25 - (comboingIntersectsFlag ? 0.5 : 0.0);
   }

   private double getMeleeAvailable(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      if (!IframeComboUtils.canFlag(view.readyIn(), this.swordLoadoutSlot >= 0, ctx.self().swordCharge(), BlockEntityUtils.getMinMax(view.eye(), view.targetBox()))) {
         return 0.0;
      } else {
         CaseMaxData options = ctx.s().getOptions(SurroundKitType.MELEE);
         return options.available() && Double.isFinite(options.value()) ? Math.max(0.0, options.value()) : 3.0;
      }
   }

   private boolean canTargetFlag(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, float targetValue) {
      return IframeComboUtils.canFlag(targetValue, getCalibration(ctx.target()), this.getMeleeAvailable(ctx, view), this.playstyleTerminator);
   }

   private static boolean canTerminatorPlaystyle(ComboingIntersects.Ctx ctx) {
      PearlsPlaystyleData cfg = ctx.s().cfg();
      CarefulBalancedType carefulBalancedType = cfg != null && cfg.playstyle() != null ? cfg.playstyle() : CarefulBalancedType.TERMINATOR;
      AbsorptionPosData self = ctx.self();
      return carefulBalancedType == CarefulBalancedType.TERMINATOR && self.totemConfirmed() && self.totems() >= 4 && self.getAbsorptionHealth() <= 8.0F;
   }

   public ComboingIntersects.Phase getPrepareStrikeType() {
      return this.prepareStrikeType;
   }

   private void handleDoneOpen(ComboingIntersects.Episode episode, long doneOpenValue) {
      if (episode.doneOpenType == ComboingIntersects.Outcome.OPEN) {
         episode.doneOpenType = ComboingIntersects.Outcome.DONE;
         this.doneOpenRepeatCount++;
         if (episode.posBlockData.kind() != ComboingIntersects.Kind.ANCHOR) {
            this.doneOpenCount++;
            this.doneOpenValue = this.doneOpenValue + Math.max(0L, doneOpenValue - episode.startTick);
         }
      }
   }

   private void handleTime(long time, int value, int comboingIntersectsLimit) {
      boolean comboingIntersectsFlag = this.iframeLastTick == Long.MIN_VALUE || time < this.iframeLastTick || time - this.iframeLastTick > 1L || value != this.iframeCurrentValue;
      this.iframeLastTick = time;
      this.iframeCurrentValue = value;
      if (comboingIntersectsLimit > 0) {
         this.iframeStartTick = Long.MIN_VALUE;
      } else if (comboingIntersectsFlag || this.iframeStartTick == Long.MIN_VALUE) {
         this.iframeStartTick = time;
      }
   }

   static int[] getValues(int comboingIntersectsLimit, int posBlockLimit, int comboingIntersectsLimit3) {
      if (comboingIntersectsLimit > 0 && comboingIntersectsLimit3 > 0) {
         int comboingIntersectsValue = Math.min(comboingIntersectsLimit3 - 1, comboingIntersectsLimit + (comboingIntersectsLimit <= 4 ? 2 : 0));
         int index = Math.max(0, comboingIntersectsValue - comboingIntersectsLimit + 1);
         boolean comboingIntersectsFlag = posBlockLimit > comboingIntersectsValue && posBlockLimit < comboingIntersectsLimit3;
         int[] ints = new int[index + (comboingIntersectsFlag ? 1 : 0)];

         for (int i = 0; i < index; i++) {
            ints[i] = comboingIntersectsLimit + i;
         }

         if (comboingIntersectsFlag) {
            ints[index] = posBlockLimit;
         }

         return ints;
      } else {
         return new int[0];
      }
   }

   private boolean canTotemTarget(ComboingIntersects.Ctx ctx, float totemTargetBound, int totemTargetValue, float selfEhp) {
      VecBlockingData target = ctx.target();
      float scaledInstance = PosBlockUtilsBonus.getScaledInstance(
         Math.max(0.0F, totemTargetBound), target.armor(), new PosBlockUtilsBonus.Window(ctx.s().targetReadyIn(), target.lastScaledTaken()), totemTargetValue
      );
      boolean totemTargetFlag = scaledInstance >= target.getHealthTrusted();
      boolean kills = totemTargetFlag && (target.awaitingTotem() || !target.totemInHand());
      return this.airRefBareData.canBare(selfEhp, ctx.self().getAbsorptionHealth(), this.airRefBareData.canBareBehind(kills, totemTargetFlag));
   }

   private static int getInventoryStack(ClientPlayerEntity clientPlayerEntity) {
      PlayerInventory inventory = clientPlayerEntity.getInventory();

      for (int i = 0; i < 9; i++) {
         if (InventoryStackUtilsEmpty.canSecondsDisable(inventory.getStack(i))) {
            return i;
         }
      }

      return -1;
   }

   private CenterVecData getIframePearling(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      if (view.hold() && view.h() <= 6.0) {
         return null;
      } else {
         HuntDeflect.Call whyCentre = this.huntDeflect
            .getWhyCentre(
               new HuntDeflect.In(
                  ctx,
                  view.us(),
                  view.eye(),
                  view.feet(),
                  view.anchor(),
                  view.h(),
                  view.towardUs(),
                  view.yaw(),
                  view.pitch(),
                  this.crystalValue,
                  this.canHeldAvailable(ctx),
                  this::getIntersectsDistance,
                  this.distanceValue > 0
               )
            );
         if (whyCentre != null && whyCentre.shot() != null) {
            this.comboBase = whyCentre.comboBase();
            CenterVecData.PearlShot shot = whyCentre.shot();
            this.anchor = whyCentre.why() == null ? shot.why() : whyCentre.why();
            CenterVecData.Stand stand = new CenterVecData.Stand(CenterVecData.StandKind.HOLD, view.us(), 0.1, false, true, Long.MIN_VALUE);
            return this.getLoopHit(
               ComboingIntersects.Phase.PEARL, ctx.tick(), 0.0, SurroundKitType.PEARL, null, null, shot, null, stand, shot.landing(), "Pearling"
            );
         } else {
            String iframeText = this.huntDeflect.getVecNoshotText();
            if (iframeText != null && !"-".equals(iframeText)) {
               this.iframeText = iframeText;
               if ("-".equals(this.anchor) || "iframe".equals(this.anchor)) {
                  this.anchor = iframeText;
               }
            }

            return null;
         }
      }
   }

   private Vec3d getVecTime(ComboingIntersects.View view, LivingEntity entity, long time) {
      if (this.vecValue != time) {
         this.vecValue = time;
         this.minEyeVec = getMinEye(view, entity);
      }

      return this.minEyeVec;
   }

   private static float getLoadoutSword(ComboingIntersects.Ctx ctx, boolean loadoutSwordFlag) {
      return !loadoutSwordFlag && ctx.loadout() != null ? VecPos.getAxeSword(ctx.loadout().sword()) : 0.0F;
   }

   private static double getAmount(double comboingIntersectsValue) {
      return Double.isFinite(comboingIntersectsValue) ? comboingIntersectsValue : 0.0;
   }

   private PosVecUtils.Path getCalToward(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, LivingEntityHelperVisible.Seed seed, int calTowardValue) {
      return ctx.tracker().getSampleTick(calTowardValue, ctx.world(), ctx.cal(), view.towardUs(), this.getKickTick(ctx, seed));
   }

   private boolean canTimes(int index, long comboingIntersectsValue) {
      return comboingIntersectsValue < this.iframeTable[index];
   }

   private static boolean canPlaceTick(ComboingIntersects.Ctx ctx, BlockPos pos) {
      TickStatusData snap = ctx.snap();
      if (snap != null && snap.kind() == ImmutablePos.Kind.PLACE && pos.equals(snap.base()) && snap.lastPlaceTick() != Long.MIN_VALUE) {
         LivingEntityHelperVisible.Seed seed = ctx.tracker().getDataGroundMax();
         return canFlag(ctx.tick() - snap.lastPlaceTick(), seed == null ? 1 : seed.time().getPing());
      } else {
         return false;
      }
   }

   private PosVecUtils.Path getMaxTracker(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ComboingIntersects.Episode episode, LivingEntityHelperVisible.Seed seed
   ) {
      long tick = ctx.tick();
      long trackerValue = episode.startTick + episode.maxData.index();
      long trackerLimit = ctx.tracker().getFullValue();
      boolean trackerFlag = trackerLimit != Long.MIN_VALUE && trackerLimit > episode.startTick;
      long trackerLimit2 = trackerValue + (long)Math.ceil(Math.max(0.0, seed.time().getPingMs())) + 2L;
      List list = null;
      if (!trackerFlag && tick <= trackerLimit2 && episode.maxData != ZERO_KICK_MODEL) {
         double[] sprintWalk = episode.maxData.getSprintWalk(episode.trackerActive);
         int max2 = (int)Math.max(1L, Math.min(16L, trackerValue - tick));
         list = List.of(new PosVecUtils.Kick(max2, sprintWalk[0], sprintWalk[1], sprintWalk[2], true));
      }

      return ctx.tracker().getSampleTick(16, ctx.world(), ctx.cal(), view.towardUs(), list);
   }

   private CenterVecData getComboingBuild(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ComboingIntersects.FollowUp followUp, boolean comboingBuildFlag) {
      BlockPos cell = followUp.cell();
      boolean world = ctx.world().canPlaceOnBedrockObsidian(cell);
      BlockPos blockPos = followUp.kind() == ComboingIntersects.Kind.BUILD && !world ? cell : null;
      CenterVecData.CrystalStep crystalStep = new CenterVecData.CrystalStep(
         cell, blockPos, Direction.UP, false, true, true, followUp.feetAt(), followUp.boxAt(), followUp.support(), followUp.face()
      );
      Vec3d vec3d = comboingBuildFlag
         ? BlockEntityUtils.getVec3d(cell).add(0.0, 0.5, 0.0)
         : (world ? WorldCollisionHelper.getTopCenter(cell) : getVec3d(cell, followUp.support(), followUp.face()));
      if ("-".equals(this.anchor)) {
         this.anchor = comboingBuildFlag ? "break" : (world ? "place" : "build");
      }

      return this.getLoopHit(
         ComboingIntersects.Phase.FOLLOW_UP,
         ctx.tick(),
         followUp.value(),
         SurroundKitType.CRYSTAL,
         crystalStep,
         null,
         null,
         null,
         getFollowLow(view, followUp.feetAt(), cell),
         vec3d,
         "Comboing"
      );
   }

   private boolean canAnchorsFresh(ComboingIntersects.Ctx ctx) {
      PearlsPlaystyleData cfg = ctx.s().cfg();
      AbsorptionPosData self = ctx.self();
      VecBlockingData target = ctx.target();
      return cfg != null
         && cfg.anchors()
         && !ctx.s().nether()
         && self.anchors() > 0
         && self.glowstone() > 0
         && !IframeComboUtils.canFlag(
            self.ticksSinceFresh(),
            self.lastScaledTaken(),
            target == null ? Double.MAX_VALUE : target.distance(),
            StaleOwed.canInstantOwed(ctx.s().getOptions(SurroundKitType.ANCHOR).why()) || StaleOwed.canTerminator(ctx.s())
         );
   }

   public ComboingIntersects() {
      this.emptyLvl = new EmptyLvl();
      this.kitSurroundTable = new int[2];
      this.iframeTable = new long[]{Long.MIN_VALUE, Long.MIN_VALUE};
      this.prepareStrikeType = ComboingIntersects.Phase.IDLE;
      this.surroundKitType = SurroundKitType.NONE;
      this.loopHitValue = Long.MIN_VALUE;
      this.kitValue = Long.MIN_VALUE;
      this.weapon = Long.MIN_VALUE;
      this.using = Long.MIN_VALUE;
      this.excessChipTimeIframe = Long.MIN_VALUE;
      this.cooldownUntilTick = Long.MIN_VALUE;
      this.pitchEyeValue = Long.MIN_VALUE;
      this.vecValue = Long.MIN_VALUE;
      this.iframeLosValue = Long.MIN_VALUE;
      this.iframeTime = Long.MIN_VALUE;
      this.iframeStartTick = Long.MIN_VALUE;
      this.iframeLastTick = Long.MIN_VALUE;
      this.iframeCurrentValue = -1;
      this.lvlEmptyValue = -1;
      this.flooredLastValue = -1;
      this.flooredLastTime = Long.MIN_VALUE;
      this.flooredValue = Long.MIN_VALUE;
      this.y = Double.NaN;
      this.distanceUpdateTime = Long.MIN_VALUE;
      this.targetEntityId = -1;
      this.distanceTo = Double.NaN;
      this.approachChasingTime = Long.MIN_VALUE;
      this.touchingValue = Long.MIN_VALUE;
      this.posBlockValue = Long.MIN_VALUE;
      this.airValue = Long.MIN_VALUE;
      this.iframeLastKitTime = Long.MIN_VALUE;
      this.readyIn = Integer.MAX_VALUE;
      this.moveAirTime = Long.MIN_VALUE;
      this.excessChipTime = Long.MIN_VALUE;
      this.swordLoadoutSlot = -1;
      this.airRefBareData = RefBareData.INSTANCE;
      this.tickIframe = Long.MIN_VALUE;
      this.airText = "-";
      this.none = "-";
      this.ok = "-";
      this.levelSpaceText = "-";
      this.replanText = "-";
      this.anchor = "-";
      this.iframeText = "-";
      this.loopText = "-";
   }

   private CenterVecData.Stand getAmount(ComboingIntersects.View view) {
      return getFollowLow(view, this.iframeLosAmount + this.distanceBonus);
   }

   private static Box getOffsetOur(ComboingIntersects.View view, int offsetOurValue, boolean offsetOurFlag) {
      return view.ourBox().offset(getMultiply(view, offsetOurValue, offsetOurFlag));
   }

   public void handle(
      long comboingIntersectsValue,
      boolean comboingIntersectsFlag,
      boolean kindFlag,
      TickStatusData tickStatusData,
      boolean comboingIntersectsFlag3,
      boolean comboingIntersectsFlag4,
      boolean comboingIntersectsFlag5,
      boolean comboingIntersectsFlag6,
      boolean comboingIntersectsFlag7
   ) {
      this.handleTimeKit(comboingIntersectsValue, comboingIntersectsFlag, kindFlag, tickStatusData, comboingIntersectsFlag3, comboingIntersectsFlag4, comboingIntersectsFlag5, comboingIntersectsFlag6, comboingIntersectsFlag7, false);
   }

   private static CenterVecData.Stand getFollowCrystal(ComboingIntersects.View view, BlockPos pos, double followCrystalValue) {
      if (view.pocket()) {
         return getFollowAbove(view, pos);
      } else {
         Vec3d vec3d = BlockEntityUtils.getVec3d(pos);
         Vec3d vec3d2 = getVec3d(view, pos, followCrystalValue);
         boolean followCrystalFlag = IframeComboUtils.canFlag(view.h());
         return new CenterVecData.Stand(
            CenterVecData.StandKind.FOLLOW, vec3d2, 0.35, followCrystalFlag, !followCrystalFlag, Long.MIN_VALUE, vec3d.add(0.0, 1.0, 0.0), PreferAnchorData.Kind.CRYSTAL
         );
      }
   }

   private ComboingIntersects.FollowUp getSpaceLevel(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ComboingIntersects.Episode episode, PosVecUtils.Path path, LivingEntityHelperVisible.Seed seed
   ) {
      ComboingIntersects.FollowUp posBlockData4 = episode.posBlockData;
      BlockPos cell = posBlockData4.cell();
      WorldCollisionHelper world = ctx.world();
      long tick = ctx.tick();
      int maxPing = Math.max(1, seed.time().getPing());
      int spaceLevelLimit = path.getValue() - 1;
      boolean canPlaceOnBedrockObsidian = world.canPlaceOnBedrockObsidian(cell);
      int pathBound = -1;
      int spaceLevelValue;
      if (posBlockData4.kind() == ComboingIntersects.Kind.BUILD && !canPlaceOnBedrockObsidian) {
         if (!world.isReplaceableAt(cell) || !world.isAirAt(cell.up())) {
            this.levelSpaceText = "fu:block";
            return null;
         }

         if (posBlockData4.support() != null && !BlockEntityUtils.canBlockState(posBlockData4.support(), posBlockData4.face())) {
            this.levelSpaceText = "fu:block";
            return null;
         }

         pathBound = getValue(posBlockData4.obsidianTick() - tick, spaceLevelLimit);
         spaceLevelValue = Math.max(pathBound + 1, getValue(posBlockData4.placeTick() - tick, spaceLevelLimit));
      } else {
         if (!canPlaceOnBedrockObsidian || !world.isAirAt(cell.up())) {
            this.levelSpaceText = "fu:block";
            return null;
         }

         spaceLevelValue = getValue(posBlockData4.placeTick() - tick, spaceLevelLimit);
      }

      int max2 = Math.max(spaceLevelValue + maxPing, getValue(posBlockData4.detTick() - tick, spaceLevelLimit));
      if (max2 <= spaceLevelLimit && spaceLevelValue <= spaceLevelLimit) {
         PosVecUtils.Sample sample = path.getDataForInt(max2);
         double iframeComboLimit = cell.getY() + 1.0 - sample.y();
         double iframeComboValue = WorldCollisionHelper.getAmount(cell.getX() + 0.5, cell.getZ() + 0.5, sample.x(), sample.z());
         if (!IframeComboUtils.canFlag(iframeComboLimit, iframeComboValue, view.hole(), sample.onGround(), 0.2) && !IframeComboUtils.canFlag(iframeComboLimit, iframeComboValue, sample.onGround(), 0.2)) {
            this.levelSpaceText = "fu:level";
            return null;
         } else {
            boolean freshSince = canFreshSince(ctx);
            double spaceLevelValue5 = getAmount(freshSince);
            if (view.eye().distanceTo(WorldCollisionHelper.getTopCenter(cell)) > spaceLevelValue5) {
               this.levelSpaceText = "fu:reach";
               return null;
            } else if (pathBound >= 0 && view.eye().distanceTo(getVec3d(cell, posBlockData4.support(), posBlockData4.face())) > spaceLevelValue5) {
               this.levelSpaceText = "fu:reach";
               return null;
            } else if (!canFlag(getEye(view, max2, freshSince), cell, 0.0)) {
               this.levelSpaceText = "fu:break";
               return null;
            } else {
               Box box = BlockEntityUtils.getBox3(cell);
               if (!box.intersects(getBoxForSample(path.getDataForInt(spaceLevelValue))) && !box.intersects(getBoxForSample(sample)) && !box.intersects(view.ourBox())) {
                  if (pathBound >= 0) {
                     Box boxForBlockPos = BlockEntityUtils.getBoxForBlockPos(cell);
                     if (boxForBlockPos.intersects(getBoxForSample(path.getDataForInt(pathBound))) || boxForBlockPos.intersects(getBoxForSample(sample)) || boxForBlockPos.intersects(view.ourBox())) {
                        this.levelSpaceText = "fu:space";
                        return null;
                     }
                  }

                  long spaceLevelValue6 = pathBound < 0 ? posBlockData4.obsidianTick() : tick + pathBound;
                  return new ComboingIntersects.FollowUp(
                     posBlockData4.kind(),
                     cell,
                     spaceLevelValue6,
                     tick + spaceLevelValue,
                     tick + max2,
                     getVec3d(sample),
                     getBox(sample),
                     posBlockData4.value(),
                     iframeComboLimit,
                     iframeComboValue,
                     posBlockData4.damage(),
                     posBlockData4.support(),
                     posBlockData4.face()
                  );
               } else {
                  this.levelSpaceText = "fu:space";
                  return null;
               }
            }
         }
      } else {
         this.levelSpaceText = "fu:level";
         return null;
      }
   }

   private CenterVecData getNofollowWeakfollow(
      ComboingIntersects.Ctx ctx,
      ComboingIntersects.View view,
      ClientPlayerEntity clientPlayerEntity,
      LivingEntityHelperVisible.Seed seed,
      LivingEntity entity,
      int nofollowWeakfollowValue,
      boolean nofollowWeakfollowFlag
   ) {
      long tick = ctx.tick();
      String anchor = this.formatText(ctx, view, entity, nofollowWeakfollowValue, nofollowWeakfollowFlag, tick);
      boolean nofollowWeakfollowFlag2 = "reach".equals(anchor) || "los".equals(anchor);
      this.crystalValue = nofollowWeakfollowFlag2 && Math.abs(view.anchor().y - view.us().y) >= 1.5 ? this.crystalValue + 1 : 0;
      this.handleIframeLos(view, entity, tick, anchor);
      if (anchor == null && this.canAvailableWhy(ctx, nofollowWeakfollowFlag)) {
         anchor = "crystal";
      }

      if (anchor != null) {
         this.anchor = anchor;
         return null;
      } else {
         Vec3d vec3d = this.vecTime;
         boolean anchorGlowstone = canAnchorGlowstone(ctx);
         boolean vecPosFlag = ctx.sprint() != null && ctx.sprint().isSprinting();
         boolean sprintHit = !nofollowWeakfollowFlag && VecPosUtilsAmount.canFlag(vecPosFlag, ctx.self().swordCharge());
         ComboingIntersects.KickModel calLoadout = getCalLoadout(ctx, view, seed, nofollowWeakfollowFlag);
         float loadoutSword = getLoadoutSword(ctx, nofollowWeakfollowFlag);
         double[] sprintWalk = calLoadout.getSprintWalk(sprintHit);
         PosVecUtils.Path sampleTick = ctx.tracker()
            .getSampleTick(16, ctx.world(), ctx.cal(), view.towardUs(), List.of(new PosVecUtils.Kick(calLoadout.index(), sprintWalk[0], sprintWalk[1], sprintWalk[2], true)));
         ComboingIntersects.FollowUp idxDet = this.getIdxDet(ctx, view, clientPlayerEntity, entity, seed, sampleTick, 1, loadoutSword, vec3d);
         if (idxDet == null) {
            idxDet = this.getIntersectsFloored(ctx, view, clientPlayerEntity, entity, sampleTick, 1, loadoutSword, vec3d);
         }

         float iframeComboLimit = idxDet == null ? Float.NaN : idxDet.damage();
         if (IframeComboUtils.canFlag(nofollowWeakfollowFlag, anchorGlowstone, iframeComboLimit)) {
            this.anchor = idxDet == null ? "nofollow" : "weakfollow";
            return null;
         } else {
            boolean pitchEye = this.canPitchEye(view, tick, vec3d);
            this.stgPosPending = new ComboingIntersects.Pending(tick, idxDet, calLoadout, sprintHit, loadoutSword, pitchEye);
            if (IframeComboUtils.canFlag(nofollowWeakfollowFlag, false, iframeComboLimit)) {
               this.anchor = "interrupt";
            } else if (idxDet == null) {
               this.anchor = "nofollow";
            }

            CenterVecData.Strike eyeCenter = getEyeCenter(view, nofollowWeakfollowValue, sprintHit, vec3d);
            return this.getLoopHit(
               ComboingIntersects.Phase.STRIKE,
               tick,
               idxDet == null ? 0.0 : idxDet.value(),
               SurroundKitType.MELEE,
               null,
               eyeCenter,
               null,
               null,
               this.getAmount(view),
               vec3d,
               "Hitting"
            );
         }
      }
   }

   private static BlockEntityUtils.Support getOppositeImmutable(BlockPos pos, ComboingIntersects.View view, double oppositeImmutableValue) {
      for (Direction direction : Direction.values()) {
         BlockPos offset = pos.offset(direction);
         Direction opposite = direction.getOpposite();
         if (BlockEntityUtils.canBlockSquare(offset, opposite)
            && BlockEntityUtils.getAmountsSquared(offset, new Direction[]{opposite}, opposite, view.eye(), view.yaw(), view.pitch(), oppositeImmutableValue) != null) {
            return new BlockEntityUtils.Support(offset.toImmutable(), opposite);
         }
      }

      return null;
   }

   private CenterVecData getKitSurround(long time) {
      this.prepareStrikeType = ComboingIntersects.Phase.IDLE;
      this.surroundKitType = SurroundKitType.NONE;
      this.loopHitValue = time;
      this.loopText = "-";
      return CenterVecData.INSTANCE;
   }

   static double getAmount(double comboingIntersectsLimit, double groundY) {
      if (Double.isFinite(comboingIntersectsLimit) && Double.isFinite(groundY)) {
         return comboingIntersectsLimit < groundY && comboingIntersectsLimit >= groundY - 1.1 ? comboingIntersectsLimit : groundY;
      } else {
         return groundY;
      }
   }

   private static Vec3d getLowAnchor(ComboingIntersects.View view) {
      EmptyLvl.Pick low = view.low();
      if (low != null && view.lowOk()) {
         Vec3d anchor = view.anchor();
         return !(Math.hypot(low.getAmount2() - anchor.x, low.getAmount() - anchor.z) <= 1.6) ? null : new Vec3d(low.getAmount2(), low.y(), low.getAmount());
      } else {
         return null;
      }
   }

   private CenterVecData getCenterVecData(String centerVecText) {
      this.ok = centerVecText;
      return null;
   }

   private CenterVecData getFillSpam(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, CenterVecData.Stand stand, Vec3d vec) {
      boolean spamFlag = IframeComboUtils.canFlag(view.readyIn(), ctx.target().lastScaledTaken());
      if ((spamFlag || this.playstyleTerminator) && (view.readyIn() <= 0 || !this.canSinceOwn(ctx)) && !this.canReady(view)) {
         CaseMaxData options = ctx.s().getOptions(SurroundKitType.CRYSTAL);
         if (!this.canTimes(0, ctx.tick()) && IframeComboUtils.canComboHeld(options.available(), options.why(), options.value())) {
            this.anchor = spamFlag ? "fill:excess" : "fill:spam";
            return this.getLoopHit(
               ComboingIntersects.Phase.RANGE,
               ctx.tick(),
               getAmount(IframeComboUtils.getAmount(options.why(), options.value())),
               SurroundKitType.CRYSTAL,
               null,
               null,
               null,
               null,
               stand == null ? this.getWatchSeen(ctx, view) : stand,
               vec == null ? this.getWorldSnap(ctx, view) : vec,
               "Comboing"
            );
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static boolean canBreakPlace(ImmutablePos.Kind kind) {
      return kind == ImmutablePos.Kind.PLACE || kind == ImmutablePos.Kind.BREAK;
   }

   private static double getEyePitch(ComboingIntersects.View view, Vec3d vec, Vec3d vec2) {
      return vec != null ? WorldCollisionHelper.computeAngleDifference(view.eye(), vec, vec2) : WorldCollisionHelper.computeAngleFromRotation(view.yaw(), view.pitch(), view.eye(), vec2);
   }

   private CenterVecData getAnchorCrystal(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      int heldAvailable = this.getHeldAvailable(ctx);
      if (heldAvailable == 0) {
         return null;
      } else {
         boolean anchorCrystalFlag = heldAvailable == 1;
         CaseMaxData options = ctx.s().getOptions(anchorCrystalFlag ? SurroundKitType.CRYSTAL : SurroundKitType.ANCHOR);
         if ("-".equals(this.anchor)) {
            this.anchor = anchorCrystalFlag ? "crystal" : "anchor";
         }

         return this.getLoopHit(
            ComboingIntersects.Phase.RANGE,
            ctx.tick(),
            getAmount(options.value()),
            anchorCrystalFlag ? SurroundKitType.CRYSTAL : SurroundKitType.ANCHOR,
            null,
            null,
            null,
            null,
            anchorCrystalFlag ? this.getWatchSeen(ctx, view) : this.getAmount(view),
            getSample(view),
            "Fighting"
         );
      }
   }

   private static CenterVecData.Strike getEyeCenter(ComboingIntersects.View view, int eyeCenterValue, boolean eyeCenterFlag, Vec3d vec) {
      Vec3d center = view.targetBox().getCenter();
      float eyeCenterValue2 = VecShieldUtilsAmounts.getAmount(center.x - view.eye().x, center.z - view.eye().z);
      double degreesLimit = Math.max(0.5, WorldCollisionHelper.horizontalDistance(view.eye(), center));
      float degrees = (float)VecShieldUtilsAmounts.getDegrees(degreesLimit);
      return new CenterVecData.Strike(eyeCenterValue, eyeCenterFlag, eyeCenterValue2, degrees, false, true, vec);
   }

   private ComboingIntersects.WindowPlan getIntersectsCry(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, PosVecUtils.Path path, ComboingIntersects.WindowPlan windowPlan, int intersectsCryValue
   ) {
      BlockPos cell = windowPlan.cell();
      WorldCollisionHelper world = ctx.world();
      if (this.canFlag(cell)) {
         return null;
      } else {
         double intersectsCryValue2 = getAmount(canFreshSince(ctx));
         boolean hasWorld = world.findCrystalAt(cell) != null;
         boolean canPlaceOnBedrockObsidian = world.canPlaceOnBedrockObsidian(cell);
         if (!hasWorld) {
            if (!world.isAirAt(cell.up())) {
               return null;
            }

            if (canPlaceOnBedrockObsidian ? intersectsCryValue < windowPlan.cryRd() - 1 : !world.isReplaceableAt(cell) || intersectsCryValue < windowPlan.cryRd()) {
               return null;
            }

            if (!canPlaceOnBedrockObsidian && windowPlan.support() != null && !BlockEntityUtils.canBlockState(windowPlan.support(), windowPlan.face())) {
               return null;
            }
         }

         PosVecUtils.Sample sample = path.getDataForInt(intersectsCryValue);
         double iframeComboLimit = cell.getY() + 1.0 - sample.y();
         double iframeComboValue = WorldCollisionHelper.getAmount(cell.getX() + 0.5, cell.getZ() + 0.5, sample.x(), sample.z());
         if (!IframeComboUtils.canFlag(iframeComboLimit, iframeComboValue, view.hole(), sample.onGround(), 0.2) && !IframeComboUtils.canFlag(iframeComboLimit, iframeComboValue, sample.onGround(), 0.2)) {
            return null;
         } else {
            Vec3d vec3d = getVec3d(view, cell, getAnchor(view, sample.y(), sample.onGround()));
            if (!canFlag(vec3d.add(0.0, 1.62, 0.0), cell, 0.0)) {
               return null;
            } else {
               if (!hasWorld) {
                  if (!canPlaceOnBedrockObsidian && windowPlan.support() != null && getEye(view, vec3d, intersectsCryValue - windowPlan.obsRd()).distanceTo(BlockEntityUtils.getVec3d(windowPlan.support(), windowPlan.face())) > intersectsCryValue2) {
                     return null;
                  }

                  if (getEye(view, vec3d, intersectsCryValue - windowPlan.cryRd()).distanceTo(WorldCollisionHelper.getTopCenter(cell)) > intersectsCryValue2) {
                     return null;
                  }

                  int pathBound = Math.max(0, intersectsCryValue - windowPlan.cryRd());
                  Box box = BlockEntityUtils.getBox3(cell);
                  if (box.intersects(getBoxForSample(path.getDataForInt(pathBound))) || pathBound <= 1 && (box.intersects(view.targetBox()) || box.intersects(view.ourBox()))) {
                     return null;
                  }

                  if (!canPlaceOnBedrockObsidian) {
                     int max6 = Math.max(0, intersectsCryValue - windowPlan.obsRd());
                     Box boxForBlockPos = BlockEntityUtils.getBoxForBlockPos(cell);
                     if (boxForBlockPos.intersects(getBoxForSample(path.getDataForInt(max6))) || max6 <= 1 && (boxForBlockPos.intersects(view.targetBox()) || boxForBlockPos.intersects(view.ourBox()))) {
                        return null;
                     }
                  }
               }

               return new ComboingIntersects.WindowPlan(
                  windowPlan.kind(), cell, windowPlan.support(), windowPlan.face(), ctx.tick() + intersectsCryValue, getVec3d(sample), getBox(sample), windowPlan.value(), windowPlan.obsRd(), windowPlan.cryRd()
               );
            }
         }
      }
   }

   public void handleMaxTime(long time) {
      this.kitValue = time;
      this.stgPosPending = null;
      ComboingIntersects.Episode kindEpisode174 = this.kindEpisode;
      if (this.touchingValue == time && kindEpisode174 != null && kindEpisode174.startTick == time && kindEpisode174.maxData == ZERO_KICK_MODEL) {
         this.kindEpisode = null;
         this.touchingValue = Long.MIN_VALUE;
         this.touchStreak = Math.max(0, this.touchStreak - 1);
      }
   }

   private CenterVecData getLvlMax(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntityHelperVisible.Seed seed, LivingEntity entity
   ) {
      if (view.readyIn() <= 0 && !entity.isTouchingWater()) {
         PosVecUtils.Sample sample = view.sample();
         double lvlValue = ctx.target().eating() ? 0.0 : Math.sqrt(sample.vx() * sample.vx() + sample.vz() * sample.vz());
         if (!IframeComboUtils.canFlag(view.us().y - view.level(), view.grounded(), view.h(), lvlValue)) {
            return null;
         } else {
            ComboingIntersects.Episode kindEpisode174 = this.kindEpisode;
            if (kindEpisode174 != null && kindEpisode174.doneOpenType == ComboingIntersects.Outcome.OPEN) {
               return null;
            } else {
               long time = ctx.tick();
               VecPos.Loadout loadout = ctx.loadout();
               if (loadout != null && loadout.crystalSlot() >= 0 && !this.canTimes(0, time)) {
                  PosVecUtils.Path calToward = this.getCalToward(ctx, view, seed, 16);
                  this.lvlActive = true;

                  ComboingIntersects.FollowUp followUp;
                  try {
                     followUp = this.getIdxDet(ctx, view, clientPlayerEntity, entity, seed, calToward, 0, 0.0F, null);
                  } finally {
                     this.lvlActive = false;
                  }

                  if (followUp != null && (followUp.damage() >= 4.0F || this.canTargetFlag(ctx, view, followUp.damage()))) {
                     this.kindEpisode = new ComboingIntersects.Episode(time, ZERO_KICK_MODEL, false, 0.0F, followUp);
                     this.touchingValue = time;
                     this.touchStreak++;
                     this.anchor = "lvl";
                     return this.getComboingBuild(ctx, view, followUp, ctx.world().findCrystalAt(followUp.cell()) != null);
                  } else {
                     return null;
                  }
               } else {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }

   public void updateIframeComboing() {
      this.kitFallbackCount = 0;
      this.openKindCount = 0;
      this.doneOpenRepeatCount = 0;
      this.doneCount = 0;
      this.openKindRepeatCount = 0;
      this.kindCount = 0;
      this.doneOpenCount = 0;
      this.doneOpenValue = 0L;
      this.loopHitCount = 0;
      this.kitAttemptCount = 0;
      this.iframeComboingCount = 0;
      this.kitMissCount = 0;
      this.kitSuccessCount = 0;
      this.prepAnchorCount = 0;
      this.comboingIntersectsCount = 0;
      this.surroundKitCount = 0;
      this.touchStreak = 0;
   }

   private ComboingIntersects.Cand getIntersectsBuild(
      ComboingIntersects.Ctx ctx,
      ComboingIntersects.View view,
      ClientPlayerEntity clientPlayerEntity,
      LivingEntity entity,
      PosVecUtils.Path path,
      int intersectsBuildValue,
      int iframeComboBound,
      boolean intersectsBuildFlag,
      boolean eyeFlag,
      double squaredFactor,
      float lastScaledTaken,
      Vec3d vec,
      BlockPos pos
   ) {
      WorldCollisionHelper world = ctx.world();
      if (this.canFlag(pos)) {
         return null;
      } else {
         Vec3d vec3d = BlockEntityUtils.getVec3d(pos);
         if (ctx.veto() != null && ctx.veto().test(vec3d)) {
            return null;
         } else {
            ComboingIntersects.Kind kind;
            if (world.canPlaceOnBedrockObsidian(pos)) {
               if (world.findCrystalAt(pos) != null) {
                  kind = ComboingIntersects.Kind.ARMED;
               } else {
                  if (!world.isAirAt(pos.up())) {
                     return null;
                  }

                  kind = ComboingIntersects.Kind.DIRECT;
               }
            } else {
               if (!intersectsBuildFlag || !world.isReplaceableAt(pos) || !world.isAirAt(pos.up()) || !world.canOppositeOffsetAt(pos)) {
                  return null;
               }

               kind = ComboingIntersects.Kind.BUILD;
            }

            Vec3d vec3d2 = kind == ComboingIntersects.Kind.BUILD ? getOppositeSquared(pos, view.eye()) : null;
            if (kind == ComboingIntersects.Kind.BUILD && vec3d2 == null) {
               return null;
            } else {
               Vec3d vec3d3 = WorldCollisionHelper.getTopCenter(pos);
               Vec3d add = vec3d.add(0.0, 0.5, 0.0);
               int pathBound = -1;
               int intersectsBuildValue6 = -1;
               int intersectsBuildLimit;
               double intersectsBuildValue7;
               if (kind == ComboingIntersects.Kind.BUILD) {
                  double eyePitch = getEyePitch(view, vec, vec3d2);
                  double angleDifference = WorldCollisionHelper.computeAngleDifference(view.eye(), vec3d2, vec3d3);
                  double angleDifference9 = WorldCollisionHelper.computeAngleDifference(view.eye(), vec3d3, add);
                  int[] intersectsBuildValues = IframeComboUtils.getValues(true, iframeComboBound, new double[]{eyePitch, angleDifference, angleDifference9});
                  pathBound = intersectsBuildValues[0] - 1 + intersectsBuildValue;
                  intersectsBuildValue6 = intersectsBuildValues[1] - 1 + intersectsBuildValue;
                  intersectsBuildLimit = intersectsBuildValues[2] - 1 + intersectsBuildValue;
                  intersectsBuildValue7 = eyePitch + angleDifference + angleDifference9;
               } else if (kind == ComboingIntersects.Kind.DIRECT) {
                  double eyePitch2 = getEyePitch(view, vec, vec3d3);
                  double angleDifference10 = WorldCollisionHelper.computeAngleDifference(view.eye(), vec3d3, add);
                  int[] values2 = IframeComboUtils.getValues(false, iframeComboBound, new double[]{eyePitch2, angleDifference10});
                  intersectsBuildValue6 = values2[1] - 1 + intersectsBuildValue;
                  intersectsBuildLimit = values2[2] - 1 + intersectsBuildValue;
                  intersectsBuildValue7 = eyePitch2 + angleDifference10;
               } else {
                  double iframeComboValue = getEyePitch(view, vec, add);
                  intersectsBuildLimit = IframeComboUtils.getValue4(iframeComboValue) - 1 + intersectsBuildValue;
                  intersectsBuildValue7 = iframeComboValue;
               }

               if (intersectsBuildLimit >= 0 && intersectsBuildLimit < path.getValue()) {
                  PosVecUtils.Sample sample = path.getDataForInt(intersectsBuildLimit);
                  double iframeComboLimit = pos.getY() + 1.0 - sample.y();
                  double intersectsBuildValue12 = WorldCollisionHelper.getAmount(pos.getX() + 0.5, pos.getZ() + 0.5, sample.x(), sample.z());
                  if (!IframeComboUtils.canFlag(iframeComboLimit, intersectsBuildValue12, view.hole(), sample.onGround()) && !IframeComboUtils.canFlagForDoubleFlag(iframeComboLimit, intersectsBuildValue12, sample.onGround())) {
                     return null;
                  } else if (this.lvlActive && !IframeComboUtils.canFlag5(iframeComboLimit)) {
                     return null;
                  } else if (kind == ComboingIntersects.Kind.BUILD && view.eye().distanceTo(vec3d2) > squaredFactor) {
                     return null;
                  } else if (kind != ComboingIntersects.Kind.ARMED && view.eye().distanceTo(vec3d3) > squaredFactor) {
                     return null;
                  } else if (!canFlag(getEye(view, intersectsBuildLimit, eyeFlag), pos, 0.1)) {
                     return null;
                  } else {
                     Box targetBox = view.targetBox();
                     if (kind == ComboingIntersects.Kind.BUILD) {
                        Box box = BlockEntityUtils.getBoxForBlockPos(pos);
                        if (box.intersects(getBoxForSample(path.getDataForInt(pathBound)))
                           || box.intersects(getBoxForSample(sample))
                           || box.intersects(targetBox)
                           || box.intersects(view.ourBox())
                           || box.intersects(getOffsetOur(view, pathBound, eyeFlag))) {
                           return null;
                        }
                     }

                     if (kind != ComboingIntersects.Kind.ARMED) {
                        Box box3 = BlockEntityUtils.getBox3(pos);
                        if (box3.intersects(getBoxForSample(path.getDataForInt(intersectsBuildValue6)))
                           || box3.intersects(getBoxForSample(sample))
                           || box3.intersects(targetBox)
                           || box3.intersects(view.ourBox())
                           || box3.intersects(getOffsetOur(view, intersectsBuildValue6, eyeFlag))) {
                           return null;
                        }

                        if (world.hasBlockingEntities(pos, entity, clientPlayerEntity)) {
                           return null;
                        }
                     }

                     float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(
                        Math.max(0.0F, BlockEntityUtils.getAmount(entity, getVec3d(sample).distanceTo(vec3d), 6.0F) - lastScaledTaken), ctx.target().armor()
                     );
                     if (toughnessEpf < this.getAirRefBareData()) {
                        return null;
                     } else {
                        double intersectsBuildValue13 = IframeComboUtils.getAmount(toughnessEpf, 0.0, kind == ComboingIntersects.Kind.BUILD, iframeComboLimit, intersectsBuildValue12, intersectsBuildValue7)
                           + this.getPosBlock(pos);
                        return new ComboingIntersects.Cand(pos, kind, pathBound, intersectsBuildValue6, intersectsBuildLimit, sample, iframeComboLimit, intersectsBuildValue12, intersectsBuildValue7, intersectsBuildValue13);
                     }
                  }
               } else {
                  return null;
               }
            }
         }
      }
   }

   private List<PosVecUtils.Kick> getKickTick(ComboingIntersects.Ctx ctx, LivingEntityHelperVisible.Seed seed) {
      ComboingIntersects.Kicked iframeKicked145 = this.iframeKicked;
      if (iframeKicked145 != null && seed != null && iframeKicked145.id() == ctx.target().id()) {
         int kickTickValue = IframeComboUtils.getValue(iframeKicked145.tick(), iframeKicked145.kick().index(), ctx.tracker().getFullValue(), seed.time().getPingMs(), ctx.tick());
         if (kickTickValue == -1) {
            return null;
         } else {
            double[] sprintWalk = iframeKicked145.kick().getSprintWalk(iframeKicked145.sprintHit());
            return List.of(new PosVecUtils.Kick(kickTickValue, sprintWalk[0], sprintWalk[1], sprintWalk[2], true));
         }
      } else {
         return null;
      }
   }

   private void handleKitSurround(long kitSurroundValue, ComboingIntersects.Phase phase, boolean kitSurroundFlag) {
      if (kitSurroundFlag) {
         this.kitSurroundCount = 0;
      }

      int index = this.surroundKitType == SurroundKitType.CRYSTAL ? 0 : (this.surroundKitType == SurroundKitType.ANCHOR ? 1 : -1);
      if (index >= 0 && (phase == ComboingIntersects.Phase.RANGE || phase == ComboingIntersects.Phase.PREPARE || phase == ComboingIntersects.Phase.RELEASE)) {
         if (kitSurroundFlag) {
            this.kitSurroundTable[index] = 0;
            this.iframeCount = 0;
         } else if (index != 1
            || this.iframeCtx == null
            || !StaleOwed.canInstantOwed(this.iframeCtx.s().getOptions(SurroundKitType.ANCHOR).why()) && !StaleOwed.canTerminator(this.iframeCtx.s())) {
            if (IframeComboUtils.canFlag(this.airRefBareData.on(), index == 0, phase == ComboingIntersects.Phase.RANGE)) {
               this.kitSurroundCount++;
               this.kitSurroundTable[index] = 0;
            } else if (index != 0 || !this.busyFlag || (++this.iframeCount & 1) != 1) {
               if (++this.kitSurroundTable[index] >= 8) {
                  this.kitSurroundTable[index] = 0;
                  this.iframeTable[index] = kitSurroundValue + (this.playstyleTerminator ? 3 : 10);
                  this.surroundKitCount++;
               }
            }
         } else {
            this.kitSurroundTable[1] = 0;
         }
      }
   }

   private boolean canReady(ComboingIntersects.View view) {
      return view.readyIn() > 0 && this.airRefBareData.on() && this.kitSurroundCount >= 3;
   }

   private CenterVecData getSelfBusy(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntity entity) {
      long tick = ctx.tick();
      if (!view.aboveNear() && clientPlayerEntity.isOnGround()) {
         TickStatusData snap = ctx.snap();
         BlockPos blockPos = this.busyFlag && snap != null && snap.kind() == ImmutablePos.Kind.OBSIDIAN ? snap.base() : null;
         VecPos.Loadout loadout = ctx.loadout();
         int slotSword = getSlotSword(loadout, ctx, clientPlayerEntity);
         if (!IframeComboUtils.canIframe(this.anchor, view.readyIn(), 0.0, slotSword)) {
            return this.getCenterVecData(IframeComboUtils.formatIframeLate(this.anchor, view.readyIn(), slotSword));
         } else if (this.posBlockData == null && (!this.busyFlag || blockPos != null) && !this.canTimes(0, tick)) {
            PearlsPlaystyleData cfg = ctx.s().cfg();
            if (loadout == null || loadout.crystalSlot() < 0 || loadout.obsidianSlot() < 0 || cfg == null || !cfg.buildObsidian() || ctx.self().obsidian() <= 16) {
               return this.getCenterVecData("kit");
            } else if (!ctx.s().getOptions(SurroundKitType.GUARD).available() && !StaleOwed.canInstantOwed(ctx.s().getOptions(SurroundKitType.ANCHOR).why())) {
               int readyIn = view.readyIn();
               PosVecUtils.Path path = this.pathIframe != null ? this.pathIframe : this.getCalToward(ctx, view, ctx.tracker().getDataGroundMax(), Math.min(16, readyIn + 2));
               if (path == null) {
                  return this.getCenterVecData("path");
               } else {
                  PosVecUtils.Sample sample = path.getDataForInt(readyIn);
                  int selfBusyValue = readyIn;
                  if (!sample.onGround() && ctx.tracker().isSampleGround()) {
                     PosVecUtils.Path calToward = this.getCalToward(ctx, view, ctx.tracker().getDataGroundMax(), 16);
                     int pathBound = calToward == null ? -1 : IframeComboUtils.getValue(calToward.getValueGround(), calToward.getValue(), readyIn + 1);
                     if (pathBound <= readyIn) {
                        return this.getCenterVecData("air");
                     }

                     path = calToward;
                     sample = calToward.getDataForInt(pathBound);
                     selfBusyValue = pathBound;
                  }

                  BlockPos intersectsDistance = this.getIntersectsDistance(view.us(), getVec3d(sample));
                  if (intersectsDistance == null) {
                     return this.getCenterVecData("none");
                  } else if (!ctx.world().canPlaceOnBedrockObsidian(intersectsDistance) && (blockPos == null || blockPos.equals(intersectsDistance))) {
                     BlockEntityUtils.Support oppositeImmutable = BlockEntityUtils.getOppositeImmutable(intersectsDistance, view.eye(), view.yaw(), view.pitch(), getAmount(canFreshSince(ctx)));
                     if (oppositeImmutable == null) {
                        return this.getCenterVecData("sup");
                     } else {
                        Vec3d vec3d = getVec3d(intersectsDistance, oppositeImmutable.pos(), oppositeImmutable.face());
                        if (!IframeComboUtils.canIframe(this.anchor, view.readyIn(), WorldCollisionHelper.computeAngleFromRotation(view.yaw(), view.pitch(), view.eye(), vec3d), slotSword)) {
                           return this.getCenterVecData("turn");
                        } else {
                           ComboingIntersects.WindowPlan windowPlan = new ComboingIntersects.WindowPlan(
                              ComboingIntersects.Kind.BUILD, intersectsDistance, oppositeImmutable.pos(), oppositeImmutable.face(), tick + selfBusyValue, getVec3d(sample), getBox(sample), 0.0, selfBusyValue, selfBusyValue
                           );
                           if (!this.canIntersectsFace(ctx, view, path, windowPlan, false)) {
                              return this.getCenterVecData("clear");
                           } else {
                              Vec3d vec3d2 = BlockEntityUtils.getVec3d(intersectsDistance);
                              VecBlockingData target = ctx.target();
                              AbsorptionPosData self = ctx.self();
                              float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(
                                 BlockEntityUtils.getCreativeSpectator(entity, vec3d2, windowPlan.feetAt(), windowPlan.boxAt(), intersectsDistance, null, 6.0F), target.armor()
                              );
                              if (toughnessEpf < 4.0F) {
                                 return this.getCenterVecData("dmg");
                              } else {
                                 Vec3d vec3d3 = getVec3d(view, intersectsDistance, getAnchor(view, sample.y(), sample.onGround()));
                                 float creativeSpectator = BlockEntityUtils.getCreativeSpectator(
                                    clientPlayerEntity, vec3d2, vec3d3, WorldCollisionHelper.getPlayerBoxAt(vec3d3.x, vec3d3.y, vec3d3.z, 0.0), intersectsDistance, null, 6.0F
                                 );
                                 float popsTarget = PosBlockUtilsBonus.getToughnessEpf(creativeSpectator, self.armor());
                                 PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(ctx.s().selfClosedFor(), self.lastScaledTaken());
                                 float scaledInstance = PosBlockUtilsBonus.getScaledInstance(creativeSpectator, self.armor(), window, selfBusyValue);
                                 boolean selfBusyFlag = toughnessEpf >= target.getHealthTrusted() && (target.awaitingTotem() || !target.totemInHand());
                                 if ((
                                       !PosBlockUtilsBonus.canFlag(
                                             toughnessEpf,
                                             scaledInstance,
                                             popsTarget,
                                             toughnessEpf >= target.getHealthTrusted(),
                                             selfBusyFlag,
                                             self.totemConfirmed() && self.totems() >= 2,
                                             canTerminatorPlaystyle(ctx),
                                             self.getAbsorptionHealth(),
                                             ctx.s().selfSpent()
                                          )
                                          || scaledInstance > ctx.selfBudget()
                                    )
                                    && !this.airRefBareData
                                       .canRef(
                                          toughnessEpf, 0.0F, toughnessEpf >= target.getHealthTrusted(), target.getHealthTrusted(), popsTarget, scaledInstance, self.getAbsorptionHealth()
                                       )) {
                                    return this.getCenterVecData("self");
                                 } else {
                                    this.blockPos2Iframe = intersectsDistance;
                                    this.ok = "ok";
                                    this.anchor = "prep:stage";
                                    CenterVecData.CrystalStep crystalStep = new CenterVecData.CrystalStep(
                                       intersectsDistance, intersectsDistance, Direction.UP, true, false, true, windowPlan.feetAt(), windowPlan.boxAt(), oppositeImmutable.pos(), oppositeImmutable.face()
                                    );
                                    return this.getLoopHit(
                                       ComboingIntersects.Phase.PREPARE,
                                       tick,
                                       0.0,
                                       SurroundKitType.CRYSTAL,
                                       crystalStep,
                                       null,
                                       null,
                                       null,
                                       this.getWatchSeen(ctx, view),
                                       vec3d,
                                       "Comboing"
                                    );
                                 }
                              }
                           }
                        }
                     }
                  } else {
                     return this.getCenterVecData("base");
                  }
               }
            } else {
               return this.getCenterVecData("guard");
            }
         } else {
            return this.getCenterVecData("busy");
         }
      } else {
         return this.getCenterVecData("us");
      }
   }

   private static boolean canPlaystyleTerminator(ComboingIntersects.Ctx ctx) {
      PearlsPlaystyleData cfg = ctx.s().cfg();
      CarefulBalancedType carefulBalancedType = cfg != null && cfg.playstyle() != null ? cfg.playstyle() : CarefulBalancedType.TERMINATOR;
      VecBlockingData target = ctx.target();
      return target != null && (ctx.s().getSelfPop().on() || DenyCrystals.canTerminator(carefulBalancedType, target.distance()));
   }

   private static boolean canDistance(BlockPos pos, Vec3d vec) {
      return pos != null && vec != null ? BlockEntityUtils.getVec3d(pos).distanceTo(vec) <= 4.0 : true;
   }

   public String getFormat() {
      double comboingIntersectsValue = this.doneOpenCount == 0 ? 0.0 : (double)this.doneOpenValue / this.doneOpenCount;
      return String.format(
         Locale.ROOT,
         "hl=st:%d fu:%d/%d/%d b:%d d:%d h2b:%.1f pr:%d/%d w:%d stall:%d rl:%d pp:%d ch:%d ls:%d rn:%d",
         this.kitFallbackCount,
         this.openKindCount,
         this.doneOpenRepeatCount,
         this.doneCount,
         this.openKindRepeatCount,
         this.kindCount,
         comboingIntersectsValue,
         this.loopHitCount,
         this.kitAttemptCount,
         this.iframeComboingCount,
         this.kitMissCount,
         this.kitSuccessCount,
         this.prepAnchorCount,
         this.comboingIntersectsCount,
         this.surroundKitCount,
         this.touchStreak
      );
   }

   private static Vec3d getEye(ComboingIntersects.View view, int eyeValue, boolean eyeFlag) {
      return view.eye().add(getMultiply(view, eyeValue, eyeFlag));
   }

   private ComboingIntersects.FollowUp getIntersectsFloored(
      ComboingIntersects.Ctx ctx,
      ComboingIntersects.View view,
      ClientPlayerEntity clientPlayerEntity,
      LivingEntity entity,
      PosVecUtils.Path path,
      int intersectsFlooredValue,
      float loadoutSword,
      Vec3d vec
   ) {
      if (path != null && this.canAnchorsFresh(ctx)) {
         WorldCollisionHelper world = ctx.world();
         int intersectsFlooredValue2 = intersectsFlooredValue;
         PosVecUtils.Sample sample = path.getDataForInt(IframeComboUtils.getValueInt(0.0) - 1 + intersectsFlooredValue);
         BlockPos floored = BlockPos.ofFloored(sample.x(), sample.y() + 0.01, sample.z());
         Direction southNorth = getSouthNorth(view.us().x - sample.x(), view.us().z - sample.z());
         ArrayList arrayList = new ArrayList(5);
         arrayList.add(floored.offset(southNorth));
         arrayList.add(floored.up().offset(southNorth));
         arrayList.add(floored.offset(southNorth.rotateYClockwise()));
         arrayList.add(floored.offset(southNorth.rotateYCounterclockwise()));
         if (view.hole()) {
            arrayList.add(floored.up(2));
         }

         boolean freshSince = canFreshSince(ctx);
         double oppositeImmutableValue = getAmount(freshSince);
         Box offsetOur = getOffsetOur(view, intersectsFlooredValue, freshSince);
         Box box = getBoxForSample(path.getDataForInt(intersectsFlooredValue));
         BlockPos ofFloored = BlockPos.ofFloored(view.us().x, view.us().y + 0.01, view.us().z);
         Vec3d vec3d = getVec3d(view, intersectsFlooredValue, freshSince);
         BlockPos pos2 = BlockPos.ofFloored(vec3d.x, vec3d.y + 0.01, vec3d.z);
         AbsorptionPosData self = ctx.self();
         VecBlockingData target = ctx.target();
         PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(ctx.s().selfClosedFor(), self.lastScaledTaken());
         boolean intersectsFlooredFlag = self.totemConfirmed() && self.totems() >= 2;
         boolean terminatorPlaystyle = canTerminatorPlaystyle(ctx);
         float absorptionHealth = self.getAbsorptionHealth();
         float selfSpent = ctx.s().selfSpent();
         long tick = ctx.tick();
         ComboingIntersects.FollowUp intersectsFloored = null;
         int intersectsFlooredCounter = 0;

         for (BlockPos blockPos : (Iterable<net.minecraft.util.math.BlockPos>) (arrayList)) {
            if (intersectsFlooredCounter >= 6) {
               break;
            }

            if (!this.canFlag(blockPos) && !canDown(blockPos, ofFloored) && !canDown(blockPos, pos2)) {
               Vec3d center = Vec3d.ofCenter(blockPos);
               if ((ctx.veto() == null || !ctx.veto().test(center)) && world.isReplaceableAt(blockPos) && !(view.eye().distanceTo(center) > oppositeImmutableValue)) {
                  Box boxForBlockPos = BlockEntityUtils.getBoxForBlockPos(blockPos);
                  if (!boxForBlockPos.intersects(box) && !boxForBlockPos.intersects(view.targetBox()) && !boxForBlockPos.intersects(view.ourBox()) && !boxForBlockPos.intersects(offsetOur)) {
                     double eyePitch = getEyePitch(view, vec, center);
                     int pathBound = Math.min(path.getValue() - 1, IframeComboUtils.getValueInt(eyePitch) - 1 + intersectsFlooredValue);
                     PosVecUtils.Sample dataForInt = path.getDataForInt(pathBound);
                     intersectsFlooredCounter++;
                     float boxBounding = EntityCenterUtils.getBoxBounding(entity, new BlockEntityUtils.Predicted(getVec3d(dataForInt), getBox(dataForInt)), blockPos, null);
                     float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(Math.max(0.0F, boxBounding - loadoutSword), target.armor());
                     if (!(toughnessEpf < this.getRef())) {
                        Vec3d vec3d2 = getVec3d(view, pathBound, freshSince);
                        BlockEntityUtils.Predicted predicted = new BlockEntityUtils.Predicted(vec3d2, WorldCollisionHelper.getPlayerBoxAt(vec3d2.x, vec3d2.y, vec3d2.z, 0.0));
                        float toughnessEpfLimit = EntityCenterUtils.getBoxBounding(blockPos, null, predicted);
                        float selfEhp = PosBlockUtilsBonus.getToughnessEpf(toughnessEpfLimit, self.armor());
                        float scaledInstance = PosBlockUtilsBonus.getScaledInstance(toughnessEpfLimit, self.armor(), window, pathBound);
                        boolean selfBusyFlag = toughnessEpf >= target.getHealthTrusted() && (target.awaitingTotem() || !target.totemInHand());
                        if (this.airRefBareData.ref()
                           ? this.canTotemTarget(ctx, boxBounding - loadoutSword, pathBound, selfEhp)
                           : PosBlockUtilsBonus.canFlag(toughnessEpf, scaledInstance, selfEhp, toughnessEpf >= target.getHealthTrusted(), selfBusyFlag, intersectsFlooredFlag, terminatorPlaystyle, absorptionHealth, selfSpent)
                                 && !(scaledInstance > ctx.selfBudget())
                              || this.airRefBareData.canRef(toughnessEpf, 0.0F, toughnessEpf >= target.getHealthTrusted(), target.getHealthTrusted(), selfEhp, scaledInstance, absorptionHealth)) {
                           double iframeComboLimit = blockPos.getY() + 0.5 - dataForInt.y();
                           double iframeComboValue = WorldCollisionHelper.getAmount(center.x, center.z, dataForInt.x(), dataForInt.z());
                           double intersectsFlooredValue7 = IframeComboUtils.getAmount(toughnessEpf, scaledInstance, false, iframeComboLimit, iframeComboValue, eyePitch) - 1.0;
                           if (intersectsFloored == null || !(intersectsFlooredValue7 <= intersectsFloored.value())) {
                              BlockEntityUtils.Support oppositeImmutable = getOppositeImmutable(blockPos, view, oppositeImmutableValue);
                              if (oppositeImmutable != null) {
                                 intersectsFloored = new ComboingIntersects.FollowUp(
                                    ComboingIntersects.Kind.ANCHOR,
                                    blockPos,
                                    Long.MIN_VALUE,
                                    tick + intersectsFlooredValue2,
                                    tick + pathBound,
                                    getVec3d(dataForInt),
                                    getBox(dataForInt),
                                    intersectsFlooredValue7,
                                    iframeComboLimit,
                                    iframeComboValue,
                                    toughnessEpf,
                                    oppositeImmutable.pos(),
                                    oppositeImmutable.face()
                                 );
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return intersectsFloored;
      } else {
         return null;
      }
   }

   public void handleTimeKit(
      long time,
      boolean kitFlag,
      boolean kindFlag,
      TickStatusData tickStatusData,
      boolean kitFlag3,
      boolean kitFlag4,
      boolean kitFlag5,
      boolean kitFlag6,
      boolean kitFlag7,
      boolean kitSurroundFlag
   ) {
      TickStatusData tickStatusData2 = tickStatusData == null ? TickStatusData.INSTANCE : tickStatusData;
      if (kitFlag5) {
         this.airValue = time;
      }

      if (kitFlag5 && this.moveAirTime == time && this.readyIn <= 0) {
         this.iframeLastKitTime = time;
      }

      ComboingIntersects.Phase phase = this.loopHitValue == time && this.kitValue != time ? this.prepareStrikeType : ComboingIntersects.Phase.IDLE;
      ComboingIntersects.Pending pending = this.stgPosPending;
      this.stgPosPending = null;
      boolean kitFlag9 = pending != null && pending.tick() == time ? pending.turning() : this.pitchEyeValue == time && this.iframeActive;
      if (kitFlag && phase == ComboingIntersects.Phase.STRIKE) {
         this.kitFallbackCount++;
         this.kitCount = 0;
         this.using = time;
         if (this.excessChipTimeIframe == time) {
            this.comboingIntersectsCount++;
         }

         ComboingIntersects.KickModel kickModel = pending != null && pending.tick() == time
            ? pending.kick()
            : (this.excessChipTime == time ? this.iframeKickModel : null);
         if (kickModel != null) {
            this.iframeKicked = new ComboingIntersects.Kicked(time, this.iframeCurrentValue, kickModel, kindFlag);
         }

         if (pending != null && pending.tick() == time) {
            this.handleOpenKind(time, pending, kindFlag);
         }
      } else if (phase == ComboingIntersects.Phase.STRIKE && !kitFlag9) {
         this.kitCount++;
         if (this.kitCount >= 3) {
            this.kitCount = 0;
            this.weapon = time + 4L;
            this.kitMissCount++;
         }
      }

      ComboingIntersects.Episode episode = this.kindEpisode;
      if (episode != null && episode.doneOpenType == ComboingIntersects.Outcome.OPEN) {
         ComboingIntersects.FollowUp posBlockData4 = episode.posBlockData;
         if (posBlockData4.kind() == ComboingIntersects.Kind.ANCHOR) {
            if (kitFlag5) {
               this.handleDoneOpen(episode, time);
            } else if (kitFlag6 && phase == ComboingIntersects.Phase.FOLLOW_UP) {
               this.handle(posBlockData4.cell(), time);
               this.handleOpenDone(episode);
            }
         } else if (tickStatusData2.base() != null && tickStatusData2.lastBreakTick() == time && posBlockData4.cell().equals(tickStatusData2.base())) {
            this.handleDoneOpen(episode, time);
         } else if (phase == ComboingIntersects.Phase.FOLLOW_UP && canIgnoredFar(tickStatusData2.directiveStatus())) {
            this.handle(posBlockData4.cell(), time);
            this.handleOpenDone(episode);
         }
      }

      ComboingIntersects.WindowPlan posBlockData6 = this.posBlockData;
      if (posBlockData6 != null) {
         if (tickStatusData2.base() != null && tickStatusData2.lastBreakTick() == time && posBlockData6.cell().equals(tickStatusData2.base())) {
            if (phase == ComboingIntersects.Phase.RELEASE) {
               this.kitSuccessCount++;
            }

            this.posBlockData = null;
         } else if ((phase == ComboingIntersects.Phase.PREPARE || phase == ComboingIntersects.Phase.RELEASE)
            && this.surroundKitType == SurroundKitType.CRYSTAL
            && canIgnoredFar(tickStatusData2.directiveStatus())) {
            this.handle(posBlockData6.cell(), time);
            this.posBlockData = null;
         }
      }

      if (this.blockPos2Iframe != null
         && phase == ComboingIntersects.Phase.PREPARE
         && this.surroundKitType == SurroundKitType.CRYSTAL
         && canIgnoredFar(tickStatusData2.directiveStatus())) {
         this.handle(this.blockPos2Iframe, time);
      }

      this.busyFlag = IframeComboUtils.canFlag(tickStatusData2.kind() != null, tickStatusData2.status());
      this.kitDelayTicks = IframeComboUtils.getValue(
         this.kitDelayTicks,
         tickStatusData2.lastActionTick() == time
            || kitFlag5
            || kitSurroundFlag
               && phase != ComboingIntersects.Phase.IDLE
               && (this.surroundKitType == SurroundKitType.CRYSTAL || this.surroundKitType == SurroundKitType.ANCHOR),
         this.tickIframe == time
      );
      this.handleKitSurround(time, phase, kitSurroundFlag);
      if (kitSurroundFlag || kitFlag || kitFlag3 || kitFlag5) {
         this.handleDistanceAmount(time, Double.NaN);
         this.huntDeflect.handleTime(time);
      }

      if (kitFlag3 && phase == ComboingIntersects.Phase.PEARL) {
         this.kitAttemptCount++;
         this.blockPos = this.comboBase;
         this.cooldownUntilTick = time + 30L;
      }

      if (kitFlag4 && phase == ComboingIntersects.Phase.PEARL) {
         this.loopHitValue = Long.MIN_VALUE;
      }
   }

   private static double getAmount(ComboingIntersects.View view, double comboingIntersectsValue) {
      return getAmount(view.us().y, comboingIntersectsValue);
   }

   private static Vec3d getMultiply(ComboingIntersects.View view, int multiplyBound, boolean multiplyFlag) {
      return multiplyFlag ? view.u().multiply(-0.3 * Math.min(Math.max(0, multiplyBound), 3)) : view.u().multiply(IframeComboUtils.getAmount(multiplyBound));
   }

   private CenterVecData getPosBlock(
      CenterVecData posBlock,
      ComboingIntersects.Ctx ctx,
      ComboingIntersects.View view,
      ClientPlayerEntity clientPlayerEntity,
      LivingEntityHelperVisible.Seed seed,
      LivingEntity entity,
      int posBlockValue,
      boolean posBlockFlag
   ) {
      this.posBlockData = null;
      if (posBlock == null) {
         posBlock = this.getNofollowWeakfollow(ctx, view, clientPlayerEntity, seed, entity, posBlockValue, posBlockFlag);
      }

      if (posBlock == null) {
         posBlock = this.getRefAnchor(ctx, view, clientPlayerEntity, seed, entity, posBlockValue, posBlockFlag);
      }

      if (posBlock == null) {
         posBlock = this.getIframePearling(ctx, view);
      }

      if (posBlock == null) {
         posBlock = this.getChipExcess(ctx, view, entity, posBlockValue, posBlockFlag);
      }

      if (posBlock == null) {
         posBlock = this.getChasingApproach(ctx, view);
      }

      return posBlock;
   }

   private float getRef() {
      return this.airRefBareData.ref() ? 1.0F : 4.0F;
   }

   private boolean canTickLoadout(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      if (!this.canPingSelf(ctx, view.readyIn())) {
         return false;
      } else if (view.h() <= 6.0 && !this.canTimes(0, ctx.tick()) && this.posBlockValue != ctx.tick()) {
         VecPos.Loadout loadout = ctx.loadout();
         return loadout != null && loadout.crystalSlot() >= 0;
      } else {
         return false;
      }
   }

   private static boolean canAnchorsCfg(ComboingIntersects.Ctx ctx) {
      PearlsPlaystyleData cfg = ctx.s().cfg();
      AbsorptionPosData self = ctx.self();
      return cfg != null && cfg.anchors() && !ctx.s().nether() && self.anchors() > 0 && self.glowstone() >= 2;
   }

   private boolean canHeldAvailable(ComboingIntersects.Ctx ctx) {
      CaseMaxData options = ctx.s().getOptions(SurroundKitType.CRYSTAL);
      CaseMaxData options2 = ctx.s().getOptions(SurroundKitType.ANCHOR);
      boolean heldAvailableFlag = options.available() && !"held".equals(options.why());
      boolean heldAvailableFlag2 = this.canAnchorsFresh(ctx) && options2.available();
      return !this.airRefBareData.ref()
         ? IframeComboUtils.getValueForFlagDouble(heldAvailableFlag, options.value(), heldAvailableFlag2, options2.value()) != 0
         : IframeComboUtils.canComboHeld(options.available(), options.why(), options.value()) || heldAvailableFlag2 && options2.value() > 0.0;
   }

   private static boolean canDown(BlockPos pos, BlockPos pos2) {
      return pos.equals(pos2) || pos.equals(pos2.up()) || pos.equals(pos2.down());
   }

   private void handleOpenDone(ComboingIntersects.Episode episode) {
      if (episode.doneOpenType == ComboingIntersects.Outcome.OPEN) {
         episode.doneOpenType = ComboingIntersects.Outcome.FAILED;
         this.doneCount++;
      }
   }

   private static Box getBoxForSample(PosVecUtils.Sample sample) {
      return WorldCollisionHelper.getPlayerBoxAt(sample.x(), sample.y(), sample.z(), 0.05);
   }

   private ComboingIntersects.FollowUp getIdxDet(
      ComboingIntersects.Ctx ctx,
      ComboingIntersects.View view,
      ClientPlayerEntity clientPlayerEntity,
      LivingEntity entity,
      LivingEntityHelperVisible.Seed seed,
      PosVecUtils.Path path,
      int idxDetValue,
      float lastScaledTaken,
      Vec3d vec
   ) {
      if (path == null) {
         return null;
      } else {
         VecPos.Loadout loadout = ctx.loadout();
         if (loadout != null && loadout.crystalSlot() >= 0) {
            PearlsPlaystyleData cfg = ctx.s().cfg();
            boolean idxDetFlag = loadout.obsidianSlot() >= 0 && cfg != null && cfg.buildObsidian();
            boolean freshSince = canFreshSince(ctx);
            double squaredFactor = getAmount(freshSince);
            int maxPing = Math.max(1, seed.time().getPing());
            int idxDetValue4 = IframeComboUtils.getValues(false, maxPing, null)[2] - 1 + idxDetValue;
            int pathBound = IframeComboUtils.getValues(true, maxPing, null)[2] - 1 + idxDetValue;
            HashSet hashSet = new HashSet();
            ArrayList arrayList = new ArrayList();
            this.handleGround(ctx, view, clientPlayerEntity, entity, path, idxDetValue, maxPing, idxDetFlag, freshSince, squaredFactor, lastScaledTaken, vec, path.getDataForInt(idxDetValue4), hashSet, arrayList);
            if (idxDetFlag) {
               this.handleGround(ctx, view, clientPlayerEntity, entity, path, idxDetValue, maxPing, true, freshSince, squaredFactor, lastScaledTaken, vec, path.getDataForInt(pathBound), hashSet, arrayList);
            }

            if (arrayList.isEmpty()) {
               return null;
            } else {
               arrayList.sort((obj, obj2) -> Double.compare(((com.corz.client.entity.ComboingIntersects.Cand) obj2).bound(), ((com.corz.client.entity.ComboingIntersects.Cand) obj).bound()));
               AbsorptionPosData self = ctx.self();
               VecBlockingData target = ctx.target();
               PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(ctx.s().selfClosedFor(), self.lastScaledTaken());
               boolean intersectsFlooredFlag = self.totemConfirmed() && self.totems() >= 2;
               boolean terminatorPlaystyle = canTerminatorPlaystyle(ctx);
               float absorptionHealth = self.getAbsorptionHealth();
               float selfSpent = ctx.s().selfSpent();
               long tick = ctx.tick();
               ComboingIntersects.FollowUp idxDet = null;
               int idxDetCounter = 0;

               for (ComboingIntersects.Cand cand : (Iterable<com.corz.client.entity.ComboingIntersects.Cand>) (arrayList)) {
                  if (idxDetCounter >= 6 || idxDet != null && cand.bound() <= idxDet.value()) {
                     break;
                  }

                  idxDetCounter++;
                  BlockPos cell = cand.cell();
                  boolean iframeComboFlag = cand.kind() == ComboingIntersects.Kind.BUILD;
                  Vec3d vec3d = BlockEntityUtils.getVec3d(cell);
                  Vec3d vec3d2 = getVec3d(cand.at());
                  Box box = getBox(cand.at());
                  BlockPos blockPos = iframeComboFlag ? cell : null;
                  float creativeSpectator = BlockEntityUtils.getCreativeSpectator(entity, vec3d, vec3d2, box, blockPos, null, 6.0F);
                  float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(Math.max(0.0F, creativeSpectator - lastScaledTaken), target.armor());
                  if (!(toughnessEpf < this.getAirRefBareData())) {
                     Vec3d vec3d3 = getVec3d(view, cand.detIdx(), freshSince);
                     Box playerBoxAt = WorldCollisionHelper.getPlayerBoxAt(vec3d3.x, vec3d3.y, vec3d3.z, 0.0);
                     float toughnessEpfLimit = BlockEntityUtils.getCreativeSpectator(clientPlayerEntity, vec3d, vec3d3, playerBoxAt, blockPos, null, 6.0F);
                     float selfEhp = PosBlockUtilsBonus.getToughnessEpf(toughnessEpfLimit, self.armor());
                     float scaledInstance = PosBlockUtilsBonus.getScaledInstance(toughnessEpfLimit, self.armor(), window, cand.detIdx());
                     boolean selfBusyFlag = toughnessEpf >= target.getHealthTrusted() && (target.awaitingTotem() || !target.totemInHand());
                     if (this.airRefBareData.ref()
                        ? this.canTotemTarget(ctx, creativeSpectator - lastScaledTaken, cand.detIdx(), selfEhp)
                        : PosBlockUtilsBonus.canFlag(toughnessEpf, scaledInstance, selfEhp, toughnessEpf >= target.getHealthTrusted(), selfBusyFlag, intersectsFlooredFlag, terminatorPlaystyle, absorptionHealth, selfSpent)
                              && !(scaledInstance > ctx.selfBudget())
                           || this.airRefBareData.canRef(toughnessEpf, 0.0F, toughnessEpf >= target.getHealthTrusted(), target.getHealthTrusted(), selfEhp, scaledInstance, absorptionHealth)) {
                        double idxDetValue6 = IframeComboUtils.getAmount(toughnessEpf, scaledInstance, iframeComboFlag, cand.dy(), cand.hz(), cand.turn()) + this.getPosBlock(cell);
                        if (idxDet == null || !(idxDetValue6 <= idxDet.value())) {
                           BlockPos pos2 = null;
                           Direction direction = null;
                           if (iframeComboFlag) {
                              BlockEntityUtils.Support oppositeImmutable = BlockEntityUtils.getOppositeImmutable(cell, view.eye(), view.yaw(), view.pitch(), squaredFactor);
                              if (oppositeImmutable == null) {
                                 continue;
                              }

                              pos2 = oppositeImmutable.pos();
                              direction = oppositeImmutable.face();
                           } else if (cand.kind() == ComboingIntersects.Kind.DIRECT
                              && BlockEntityUtils.getAmountsSquared(cell, BlockEntityUtils.idleDirections, null, view.eye(), view.yaw(), view.pitch(), squaredFactor) == null) {
                              continue;
                           }

                           long idxDetValue7 = cand.obsidianIdx() < 0 ? Long.MIN_VALUE : tick + cand.obsidianIdx();
                           long idxDetValue8 = cand.placeIdx() < 0 ? tick : tick + cand.placeIdx();
                           idxDet = new ComboingIntersects.FollowUp(
                              cand.kind(), cell, idxDetValue7, idxDetValue8, tick + cand.detIdx(), vec3d2, box, idxDetValue6, cand.dy(), cand.hz(), toughnessEpf, pos2, direction
                           );
                        }
                     }
                  }
               }

               return idxDet;
            }
         } else {
            return null;
         }
      }
   }

   private CenterVecData.Stand getRoutedSearching(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      if (view.hold()) {
         return null;
      } else if (this.distanceValue > 0 && !IframeComboUtils.canFlag2(view.anchor().y - view.us().y, view.h())) {
         Vec3d anchor = view.anchor();
         if (this.routedSearchingValue <= 0) {
            RoutedBreachingApi nav = ctx.nav();
            AnchorRoutePlanner.Mode mode = nav == null ? null : nav.getRouteMode();
            if (mode == AnchorRoutePlanner.Mode.ROUTED || mode == AnchorRoutePlanner.Mode.PARTIAL || mode == AnchorRoutePlanner.Mode.SEARCHING) {
               return null;
            }

            Vec3d lengthSquared = getLengthSquared(view, anchor.x, anchor.z);
            this.routedSearchingValue = this.distanceValue;
            this.x = lengthSquared.x;
            this.z = lengthSquared.z;
         }

         double routedSearchingValue = IframeComboUtils.getAmountForInt(this.distanceValue - this.routedSearchingValue + 1);
         double[] routedSearchingValues = IframeComboUtils.getAmounts(anchor.x, anchor.z, this.x, this.z, routedSearchingValue, 5.0);
         return new CenterVecData.Stand(
            CenterVecData.StandKind.FOLLOW, new Vec3d(routedSearchingValues[0], view.above() > 0.5 ? getGroundedFeet(view) : view.us().y, routedSearchingValues[1]), 0.35, true, false, Long.MIN_VALUE
         );
      } else {
         return null;
      }
   }

   private static Vec3d getIntersectsLow(ComboingIntersects.View view, BlockPos pos) {
      EmptyLvl.Pick low = view.low();
      if (low != null && view.lowOk()) {
         Vec3d vec3d = new Vec3d(low.getAmount2(), low.y(), low.getAmount());
         if (pos == null) {
            return vec3d;
         } else if (!canFlag(vec3d.add(0.0, 1.62, 0.0), pos, 0.1)) {
            return null;
         } else {
            Box box = WorldCollisionHelper.getPlayerBoxAt(vec3d.x, vec3d.y, vec3d.z, 0.0);
            return !BlockEntityUtils.getBox3(pos).intersects(box) && !BlockEntityUtils.getBoxForBlockPos(pos).intersects(box) ? vec3d : null;
         }
      } else {
         return null;
      }
   }

   private static double getAmount(PosVecUtils.Sample sample, BlockPos pos, Vec3d vec) {
      return 0.5 * getAmount(sample.x(), sample.z(), pos.getX() + 0.5, pos.getZ() + 0.5, vec.x, vec.z);
   }

   private static boolean canFlag(Vec3d vec, Vec3d vec2, LivingEntity entity) {
      double deltaX = vec2.x - vec.x;
      double deltaY = vec2.y - vec.y;
      double deltaZ = vec2.z - vec.z;
      float comboingIntersectsValue = VecShieldUtilsAmounts.getAmount(deltaX, deltaZ);
      float degrees = -((float)Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ))));
      return BlockEntityUtils.canBoundingBox(vec, comboingIntersectsValue, degrees, entity, 3.0);
   }

   static boolean canFlag(long comboingIntersectsLimit, int comboingIntersectsBound) {
      return comboingIntersectsLimit >= 0L && comboingIntersectsLimit <= Math.max(1, comboingIntersectsBound) + 1;
   }

   private static Direction getSouthNorth(double southNorthLimit, double spotValue) {
      if (Math.abs(southNorthLimit) >= Math.abs(spotValue)) {
         return southNorthLimit >= 0.0 ? Direction.EAST : Direction.WEST;
      } else {
         return spotValue >= 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   private CenterVecData getSpamExcess(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      boolean spamExcessFlag = IframeComboUtils.canFlag(view.readyIn(), ctx.target().lastScaledTaken());
      boolean spamExcessFlag2 = view.readyIn() > 0 && !spamExcessFlag && canPlaystyleTerminator(ctx) && !this.canSinceOwn(ctx);
      if (view.readyIn() > 0 && !spamExcessFlag && !spamExcessFlag2) {
         return null;
      } else {
         CaseMaxData options = ctx.s().getOptions(SurroundKitType.CRYSTAL);
         CaseMaxData caseMaxData = ctx.s().getOptions(SurroundKitType.ANCHOR);
         if (view.readyIn() > 0 && this.canTickLoadout(ctx, view)) {
            return null;
         } else {
            boolean spamExcessFlag3 = options.available() && !"held".equals(options.why()) && !this.canTimes(0, ctx.tick());
            boolean iframeComboFlag = spamExcessFlag3 && IframeComboUtils.canComboHeld(options.available(), options.why(), options.value());
            boolean spamExcessFlag5 = this.canAnchorsFresh(ctx) && caseMaxData.available() && view.readyIn() <= getPrepTicks(ctx, caseMaxData) && !this.canTimes(1, ctx.tick());
            double spamExcessValue = IframeComboUtils.getAmount(options.why(), options.value());
            int spamExcessValue2 = spamExcessFlag2
               ? (iframeComboFlag ? 1 : 0)
               : IframeComboUtils.getValue(iframeComboFlag, spamExcessValue, spamExcessFlag5, caseMaxData.value(), IframeComboUtils.getMax(this.getMeleeAvailable(ctx, view)));
            if (this.airRefBareData.on()) {
               spamExcessValue2 = IframeComboUtils.getValue(iframeComboFlag && !this.canReady(view), spamExcessValue, spamExcessFlag5, caseMaxData.value());
            }

            if (spamExcessValue2 == 0) {
               return null;
            } else {
               boolean spamExcessFlag6 = spamExcessValue2 == 1;
               this.anchor = spamExcessFlag2 ? "spam" : (spamExcessFlag ? "excess" : "window");
               return this.getLoopHit(
                  ComboingIntersects.Phase.RANGE,
                  ctx.tick(),
                  getAmount(spamExcessFlag6 ? spamExcessValue : caseMaxData.value()),
                  spamExcessFlag6 ? SurroundKitType.CRYSTAL : SurroundKitType.ANCHOR,
                  null,
                  null,
                  null,
                  null,
                  spamExcessFlag6 ? this.getWatchSeen(ctx, view) : this.getAmount(view),
                  spamExcessFlag6 ? this.getWorldSnap(ctx, view) : getSample(view),
                  "Comboing"
               );
            }
         }
      }
   }

   private ComboingIntersects.WindowPlan getSelfDmg(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntity entity, PosVecUtils.Path path, int selfDmgValue
   ) {
      VecPos.Loadout loadout = ctx.loadout();
      PearlsPlaystyleData cfg = ctx.s().cfg();
      boolean selfDmgFlag = loadout.obsidianSlot() >= 0 && cfg != null && cfg.buildObsidian();
      boolean freshSince = canFreshSince(ctx);
      double squaredFactor = getAmount(freshSince);
      int iframeComboValue = BoxLengthUtils.getMax(ctx.self().pingMs());
      PosVecUtils.Sample sample = path.getDataForInt(selfDmgValue);
      int floor4 = (int)Math.floor(sample.x());
      int floor5 = (int)Math.floor(sample.z());
      int round6 = (int)Math.round(sample.y());
      byte selfDmgLimit = 2;
      int selfDmgValue7 = sample.onGround() ? 0 : 1;
      ArrayList arrayList = new ArrayList();

      for (int i = selfDmgValue7; i <= IframeComboUtils.getValue(sample.onGround()); i++) {
         for (int j = -selfDmgLimit; j <= selfDmgLimit; j++) {
            for (int k = -selfDmgLimit; k <= selfDmgLimit; k++) {
               BlockPos blockPos = new BlockPos(floor4 + j, round6 - i, floor5 + k);
               if (!(Math.abs(blockPos.getX() + 0.5 - sample.x()) > selfDmgLimit) && !(Math.abs(blockPos.getZ() + 0.5 - sample.z()) > selfDmgLimit)) {
                  ComboingIntersects.Cand intersectsEye = this.getIntersectsEye(ctx, view, clientPlayerEntity, entity, path, selfDmgValue, iframeComboValue, selfDmgFlag, squaredFactor, sample, blockPos);
                  if (intersectsEye != null) {
                     arrayList.add(intersectsEye);
                  }
               }
            }
         }
      }

      if (arrayList.isEmpty()) {
         this.none = "none";
         return null;
      } else {
         arrayList.sort((obj, obj2) -> Double.compare(((com.corz.client.entity.ComboingIntersects.Cand) obj2).bound(), ((com.corz.client.entity.ComboingIntersects.Cand) obj).bound()));
         AbsorptionPosData self = ctx.self();
         VecBlockingData target = ctx.target();
         PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(ctx.s().selfClosedFor(), self.lastScaledTaken());
         boolean intersectsFlooredFlag = self.totemConfirmed() && self.totems() >= 2;
         boolean terminatorPlaystyle = canTerminatorPlaystyle(ctx);
         float absorptionHealth = self.getAbsorptionHealth();
         float selfSpent = ctx.s().selfSpent();
         Vec3d vec3d = getVec3d(sample);
         Box box = getBox(sample);
         double anchor = getAnchor(view, sample.y(), sample.onGround());
         ComboingIntersects.WindowPlan selfDmg = null;
         int selfDmgCounter = 0;
         boolean selfDmgFlag3 = false;
         boolean selfDmgFlag4 = false;

         for (ComboingIntersects.Cand cand : (Iterable<com.corz.client.entity.ComboingIntersects.Cand>) (arrayList)) {
            if (selfDmgCounter >= 6 || selfDmg != null && cand.bound() <= selfDmg.value()) {
               break;
            }

            selfDmgCounter++;
            BlockPos cell = cand.cell();
            boolean iframeComboFlag = cand.kind() == ComboingIntersects.Kind.BUILD;
            Vec3d vec3d2 = BlockEntityUtils.getVec3d(cell);
            BlockPos pos = iframeComboFlag ? cell : null;
            float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(
               BlockEntityUtils.getCreativeSpectator(entity, vec3d2, vec3d, box, pos, null, 6.0F), target.armor()
            );
            if (!(toughnessEpf < 4.0F)) {
               selfDmgFlag3 = true;
               Vec3d vec3d3 = getVec3d(view, cell, anchor);
               Box playerBoxAt = WorldCollisionHelper.getPlayerBoxAt(vec3d3.x, vec3d3.y, vec3d3.z, 0.0);
               float creativeSpectator = BlockEntityUtils.getCreativeSpectator(clientPlayerEntity, vec3d2, vec3d3, playerBoxAt, pos, null, 6.0F);
               float selfEhp = PosBlockUtilsBonus.getToughnessEpf(creativeSpectator, self.armor());
               float scaledInstance = PosBlockUtilsBonus.getScaledInstance(creativeSpectator, self.armor(), window, selfDmgValue);
               boolean selfBusyFlag = toughnessEpf >= target.getHealthTrusted() && (target.awaitingTotem() || !target.totemInHand());
               if (PosBlockUtilsBonus.canFlag(toughnessEpf, scaledInstance, selfEhp, toughnessEpf >= target.getHealthTrusted(), selfBusyFlag, intersectsFlooredFlag, terminatorPlaystyle, absorptionHealth, selfSpent)
                     && !(scaledInstance > ctx.selfBudget())
                  || this.airRefBareData.canRef(toughnessEpf, 0.0F, toughnessEpf >= target.getHealthTrusted(), target.getHealthTrusted(), selfEhp, scaledInstance, absorptionHealth)) {
                  selfDmgFlag4 = true;
                  double selfDmgValue8 = IframeComboUtils.getAmount(toughnessEpf, scaledInstance, iframeComboFlag, cand.dy(), cand.hz(), cand.turn())
                     + this.getPosBlock(cell)
                     + getAmount(sample, cell, vec3d3);
                  if (selfDmg == null || !(selfDmgValue8 <= selfDmg.value())) {
                     BlockPos pos3 = null;
                     Direction direction = null;
                     if (iframeComboFlag) {
                        BlockEntityUtils.Support oppositeImmutable = BlockEntityUtils.getOppositeImmutable(
                           cell, getEye(view, vec3d3, selfDmgValue - cand.obsidianIdx()), view.yaw(), view.pitch(), squaredFactor
                        );
                        if (oppositeImmutable == null) {
                           continue;
                        }

                        pos3 = oppositeImmutable.pos();
                        direction = oppositeImmutable.face();
                     } else if (cand.kind() == ComboingIntersects.Kind.DIRECT
                        && BlockEntityUtils.getAmountsSquared(
                              cell, BlockEntityUtils.idleDirections, null, getEye(view, vec3d3, selfDmgValue - cand.placeIdx()), view.yaw(), view.pitch(), squaredFactor
                           )
                           == null) {
                        continue;
                     }

                     selfDmg = new ComboingIntersects.WindowPlan(
                        cand.kind(), cell, pos3, direction, ctx.tick() + selfDmgValue, vec3d, box, selfDmgValue8, cand.obsidianIdx(), cand.placeIdx()
                     );
                  }
               }
            }
         }

         if (selfDmg == null) {
            this.none = !selfDmgFlag3 ? "dmg" : (!selfDmgFlag4 ? "self" : "support");
         }

         return selfDmg;
      }
   }

   private CenterVecData getPrepAnchor(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntity entity) {
      int readyIn = view.readyIn();
      long tick = ctx.tick();
      if (readyIn <= 0) {
         return null;
      } else if (view.h() > 6.0) {
         this.posBlockData = null;
         return null;
      } else {
         VecPos.Loadout loadout = ctx.loadout();
         ComboingIntersects.WindowPlan posBlockData = null;
         PosVecUtils.Path path = null;
         if (loadout != null && loadout.crystalSlot() >= 0 && !this.canTimes(0, tick) && this.posBlockValue != tick) {
            path = this.getCalToward(ctx, view, ctx.tracker().getDataGroundMax(), Math.min(16, readyIn + 2));
            if (path != null) {
               ComboingIntersects.WindowPlan windowPlan = this.posBlockData;
               boolean prepAnchorFlag = windowPlan != null && Math.abs(windowPlan.window() - (tick + readyIn)) <= 1L;
               if (prepAnchorFlag) {
                  posBlockData = this.getIntersectsCry(ctx, view, path, windowPlan, readyIn);
               }

               if (posBlockData == null) {
                  posBlockData = this.getSelfDmg(ctx, view, clientPlayerEntity, entity, path, readyIn);
                  if (posBlockData != null && !prepAnchorFlag) {
                     this.prepAnchorCount++;
                  }
               }
            }

            if (posBlockData == null && !view.grounded() && ctx.tracker().isSampleGround()) {
               PosVecUtils.Path calToward = this.getCalToward(ctx, view, ctx.tracker().getDataGroundMax(), 16);
               int prepAnchorLimit = calToward == null ? -1 : IframeComboUtils.getValue(calToward.getValueGround(), calToward.getValue(), readyIn + 1);
               if (prepAnchorLimit > readyIn) {
                  posBlockData = this.getWindowPos(ctx, view, clientPlayerEntity, entity, calToward, prepAnchorLimit);
                  if (posBlockData != null) {
                     path = calToward;
                  }
               }
            }
         }

         this.posBlockData = posBlockData;
         if (posBlockData == null) {
            CaseMaxData options = ctx.s().getOptions(SurroundKitType.ANCHOR);
            if (this.canAnchorsFresh(ctx) && options.available() && options.value() >= 3.0 && readyIn <= getPrepTicks(ctx, options) && !this.canTimes(1, tick)) {
               this.anchor = "prep:anchor";
               return this.getLoopHit(
                  ComboingIntersects.Phase.PREPARE,
                  tick,
                  getAmount(options.value()),
                  SurroundKitType.ANCHOR,
                  null,
                  null,
                  null,
                  null,
                  this.getAmount(view),
                  getSample(view),
                  "Comboing"
               );
            } else {
               CenterVecData centerVecData = this.getCenterVecData(ctx, view);
               if (centerVecData != null) {
                  return centerVecData;
               } else {
                  this.iframeComboingCount++;
                  this.anchor = "iframe";
                  this.pathIframe = path;
                  if (IframeComboUtils.canFlagForDoubleInt(view.h(), readyIn)) {
                     this.prepAnchorVec = this.getLoadoutWorld(ctx, view, path, readyIn);
                  }

                  if (this.prepAnchorVec == null && this.airRefBareData.on() && readyIn > 2) {
                     this.prepAnchorVec = this.getLoadoutWorld(ctx, view, path, readyIn);
                  }

                  if (this.prepAnchorVec == null && getSlotSword(loadout, ctx, clientPlayerEntity) > readyIn) {
                     Vec3d vec3d = this.getLoadoutWorld(ctx, view, path, readyIn);
                     if (vec3d != null && readyIn <= IframeComboUtils.getValue(WorldCollisionHelper.computeAngleFromRotation(view.yaw(), view.pitch(), view.eye(), vec3d)) + 1) {
                        this.prepAnchorVec = vec3d;
                     }
                  }

                  return null;
               }
            }
         } else {
            BlockPos cell = posBlockData.cell();
            WorldCollisionHelper world = ctx.world();
            boolean hasWorld = world.findCrystalAt(cell) != null;
            boolean canPlaceOnBedrockObsidian = world.canPlaceOnBedrockObsidian(cell);
            CenterVecData.CrystalStep crystalStep = null;
            Vec3d vec3d2;
            if (hasWorld) {
               vec3d2 = BlockEntityUtils.getVec3d(cell).add(0.0, 0.5, 0.0);
               this.anchor = "prep:hold";
            } else if (!canPlaceOnBedrockObsidian) {
               vec3d2 = getVec3d(cell, posBlockData.support(), posBlockData.face());
               if (readyIn > posBlockData.obsRd() && !this.canIntersectsFace(ctx, view, path, posBlockData, false)) {
                  this.anchor = "prep:move";
               } else {
                  crystalStep = new CenterVecData.CrystalStep(cell, cell, Direction.UP, true, false, true, posBlockData.feetAt(), posBlockData.boxAt(), posBlockData.support(), posBlockData.face());
                  this.anchor = "prep:obs";
               }
            } else {
               vec3d2 = WorldCollisionHelper.getTopCenter(cell);
               if (readyIn <= posBlockData.cryRd()) {
                  crystalStep = new CenterVecData.CrystalStep(cell, null, Direction.UP, true, false, true, posBlockData.feetAt(), posBlockData.boxAt());
                  this.anchor = "prep:cry";
               } else {
                  this.anchor = "prep:move";
               }
            }

            CenterVecData.Stand followCrystal = getFollowCrystal(view, cell, Math.min(posBlockData.feetAt().y, view.anchor().y));
            if (crystalStep == null && !hasWorld && this.airRefBareData.on()) {
               String anchor = this.anchor;
               CenterVecData fillSpam = this.getFillSpam(ctx, view, followCrystal, vec3d2);
               if (fillSpam != null) {
                  return fillSpam;
               }

               this.anchor = anchor;
            }

            return this.getLoopHit(
               ComboingIntersects.Phase.PREPARE,
               tick,
               posBlockData.value(),
               crystalStep == null ? SurroundKitType.NONE : SurroundKitType.CRYSTAL,
               crystalStep,
               null,
               null,
               null,
               followCrystal,
               vec3d2,
               "Comboing"
            );
         }
      }
   }

   private int getMin(long comboingIntersectsLimit) {
      return this.iframeStartTick != Long.MIN_VALUE && comboingIntersectsLimit >= this.iframeStartTick ? (int)Math.min(2147483647L, comboingIntersectsLimit - this.iframeStartTick) : -1;
   }

   private BlockPos getIntersectsDistance(Vec3d vec, Vec3d vec2) {
      ComboingIntersects.Ctx iframeCtx08 = this.iframeCtx;
      if (iframeCtx08 != null && vec != null && vec2 != null && iframeCtx08.world() != null && iframeCtx08.s() != null) {
         VecPos.Loadout loadout = iframeCtx08.loadout();
         if (loadout != null && loadout.crystalSlot() >= 0) {
            PearlsPlaystyleData cfg = iframeCtx08.s().cfg();
            boolean intersectsDistanceFlag = loadout.obsidianSlot() >= 0 && cfg != null && cfg.buildObsidian();
            WorldCollisionHelper world = iframeCtx08.world();
            Vec3d add = vec.add(0.0, 1.62, 0.0);
            double intersectsDistanceValue = 4.25;
            Box box = WorldCollisionHelper.getPlayerBoxAt(vec2.x, vec2.y, vec2.z, 0.0);
            Box playerBoxAt = WorldCollisionHelper.getPlayerBoxAt(vec.x, vec.y, vec.z, 0.0);
            int floor2 = (int)Math.floor(vec2.x);
            int floor3 = (int)Math.floor(vec2.z);
            int intersectsDistanceLimit = (int)Math.floor(vec2.y + 0.001);
            BlockPos intersectsDistance = null;
            boolean intersectsDistanceFlag2 = false;
            double positiveInfinity = Double.POSITIVE_INFINITY;

            for (int i = intersectsDistanceLimit - 1; i <= intersectsDistanceLimit; i++) {
               for (int j = -2; j <= 2; j++) {
                  for (int k = -2; k <= 2; k++) {
                     BlockPos blockPos = new BlockPos(floor2 + j, i, floor3 + k);
                     double iframeComboValue = WorldCollisionHelper.getAmount(blockPos.getX() + 0.5, blockPos.getZ() + 0.5, vec2.x, vec2.z);
                     if (!(iframeComboValue > 1.6)) {
                        boolean canPlaceOnBedrockObsidian = world.canPlaceOnBedrockObsidian(blockPos);
                        if ((intersectsDistance == null || (!intersectsDistanceFlag2 || canPlaceOnBedrockObsidian) && (canPlaceOnBedrockObsidian != intersectsDistanceFlag2 || !(iframeComboValue >= positiveInfinity)))
                           && IframeComboUtils.canFlag(blockPos.getY() + 1.0 - vec2.y, iframeComboValue, false, true)
                           && !this.canFlag(blockPos)
                           && world.isAirAt(blockPos.up())
                           && (iframeCtx08.veto() == null || !iframeCtx08.veto().test(BlockEntityUtils.getVec3d(blockPos)))
                           && (canPlaceOnBedrockObsidian || intersectsDistanceFlag && world.isReplaceableAt(blockPos) && world.canOppositeOffsetAt(blockPos))) {
                           Box box3 = BlockEntityUtils.getBox3(blockPos);
                           if (!box3.intersects(box) && !box3.intersects(playerBoxAt)) {
                              if (!canPlaceOnBedrockObsidian) {
                                 Box boxForBlockPos = BlockEntityUtils.getBoxForBlockPos(blockPos);
                                 if (boxForBlockPos.intersects(box) || boxForBlockPos.intersects(playerBoxAt)) {
                                    continue;
                                 }

                                 Vec3d oppositeSquared = getOppositeSquared(blockPos, add);
                                 if (oppositeSquared == null || add.distanceTo(oppositeSquared) > intersectsDistanceValue) {
                                    continue;
                                 }
                              }

                              if (!(add.distanceTo(WorldCollisionHelper.getTopCenter(blockPos)) > intersectsDistanceValue) && canFlag(add, blockPos, 0.1)) {
                                 intersectsDistance = blockPos;
                                 intersectsDistanceFlag2 = canPlaceOnBedrockObsidian;
                                 positiveInfinity = iframeComboValue;
                              }
                           }
                        }
                     }
                  }
               }
            }

            return intersectsDistance;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void handleDistanceTime(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      long tick = ctx.tick();
      double distanceTo = view.feet().distanceTo(view.us());
      boolean distanceFlag = IframeComboUtils.canFlag(ctx.self().usingItem(), ctx.target().distance());
      if (ctx.target().id() != this.targetEntityId || this.distanceUpdateTime == Long.MIN_VALUE || tick < this.distanceUpdateTime || !distanceFlag || IframeComboUtils.canFlag(this.distanceTo, distanceTo)) {
         this.targetEntityId = ctx.target().id();
         this.handleDistanceAmount(tick, distanceTo);
      } else if (Double.isNaN(this.distanceTo)) {
         this.distanceTo = distanceTo;
      }

      this.distanceValue = IframeComboUtils.getValueForLong(IframeComboUtils.getTime(tick, this.distanceUpdateTime));
      boolean canFlag2 = IframeComboUtils.canFlag2(view.anchor().y - view.us().y, view.h());
      this.distanceBonus = this.distanceValue > 0 && canFlag2 ? IframeComboUtils.getAmountForInt(this.distanceValue + 1) : 0.0;
      if (this.distanceValue <= 0 || canFlag2) {
         this.routedSearchingValue = 0;
      }
   }

   private static double[] getAmounts(double[] comboingIntersectsValues, double comboingIntersectsValue) {
      double comboingIntersectsFactor = Double.isFinite(comboingIntersectsValue) ? comboingIntersectsValue : 1.0;
      return new double[]{comboingIntersectsValues[0] * comboingIntersectsFactor, comboingIntersectsValues[1], comboingIntersectsValues[2] * comboingIntersectsFactor};
   }

   private CenterVecData getMaxOpen(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntityHelperVisible.Seed seed, LivingEntity entity
   ) {
      if (view.readyIn() > 0) {
         return null;
      } else {
         ComboingIntersects.Episode kindEpisode174 = this.kindEpisode;
         if (kindEpisode174 != null && kindEpisode174.doneOpenType == ComboingIntersects.Outcome.OPEN) {
            return null;
         } else if (!(view.h() <= 6.0)) {
            return null;
         } else {
            long time = ctx.tick();
            VecPos.Loadout loadout = ctx.loadout();
            if (loadout != null && loadout.crystalSlot() >= 0 && !this.canTimes(0, time)) {
               PosVecUtils.Path calToward = this.getCalToward(ctx, view, seed, 16);
               ComboingIntersects.FollowUp idxDet = this.getIdxDet(ctx, view, clientPlayerEntity, entity, seed, calToward, 0, 0.0F, null);
               if (idxDet != null && (idxDet.damage() >= 4.0F || this.canTargetFlag(ctx, view, idxDet.damage()))) {
                  this.kindEpisode = new ComboingIntersects.Episode(time, ZERO_KICK_MODEL, false, 0.0F, idxDet);
                  this.touchingValue = time;
                  this.touchStreak++;
                  this.anchor = "now";
                  return this.getComboingBuild(ctx, view, idxDet, ctx.world().findCrystalAt(idxDet.cell()) != null);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }
      }
   }

   static double getAmount(double comboingIntersectsValue, double comboingIntersectsValue2, double comboingIntersectsValue3, double comboingIntersectsValue4, double comboingIntersectsValue5, double comboingIntersectsValue6) {
      double comboingIntersectsFactor = comboingIntersectsValue - comboingIntersectsValue5;
      double deltaZ = comboingIntersectsValue2 - comboingIntersectsValue6;
      double deltaX = comboingIntersectsValue3 - comboingIntersectsValue;
      double comboingIntersectsFactor4 = comboingIntersectsValue4 - comboingIntersectsValue2;
      double hypot = Math.hypot(comboingIntersectsFactor, deltaZ);
      double hypot2 = Math.hypot(deltaX, comboingIntersectsFactor4);
      return hypot > 1.0E-6 && hypot2 > 1.0E-6 ? Math.max(-1.0, Math.min(1.0, (comboingIntersectsFactor * deltaX + deltaZ * comboingIntersectsFactor4) / (hypot * hypot2))) : 0.0;
   }

   private CenterVecData getReleaseComboing(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity player, LivingEntity entity) {
      ComboingIntersects.WindowPlan posBlockData6 = this.posBlockData;
      if (posBlockData6 != null && view.readyIn() <= 0) {
         BlockPos cell = posBlockData6.cell();
         if (ctx.world().findCrystalAt(cell) != null) {
            if (!canFlag(view.eye(), cell, 0.1)) {
               this.anchor = "release:reach";
               return null;
            } else {
               Vec3d vec3d = BlockEntityUtils.getVec3d(cell);
               VecBlockingData target = ctx.target();
               float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(
                  BlockEntityUtils.getCreativeSpectator(entity, vec3d, view.feet(), view.targetBox(), null, null, 6.0F), target.armor()
               );
               if (toughnessEpf < 4.0F && !IframeComboUtils.canFlag(toughnessEpf, getCalibration(target))) {
                  this.anchor = "release:weak";
                  return null;
               } else {
                  AbsorptionPosData self = ctx.self();
                  PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(ctx.s().selfClosedFor(), self.lastScaledTaken());
                  Box box = self.box() != null ? self.box() : player.getBoundingBox();
                  float creativeSpectator = BlockEntityUtils.getCreativeSpectator(player, vec3d, view.us(), box, null, null, 6.0F);
                  float selfEhp = PosBlockUtilsBonus.getToughnessEpf(creativeSpectator, self.armor());
                  float scaledInstance = PosBlockUtilsBonus.getScaledInstance(creativeSpectator, self.armor(), window, 0);
                  boolean releaseComboingFlag = toughnessEpf >= target.getHealthTrusted() && (target.awaitingTotem() || !target.totemInHand());
                  if ((
                        !PosBlockUtilsBonus.canFlag(
                              toughnessEpf,
                              scaledInstance,
                              selfEhp,
                              toughnessEpf >= target.getHealthTrusted(),
                              releaseComboingFlag,
                              self.totemConfirmed() && self.totems() >= 2,
                              canTerminatorPlaystyle(ctx),
                              self.getAbsorptionHealth(),
                              ctx.s().selfSpent()
                           )
                           || scaledInstance > ctx.selfBudget()
                     )
                     && !this.airRefBareData.canRef(toughnessEpf, 0.0F, toughnessEpf >= target.getHealthTrusted(), target.getHealthTrusted(), selfEhp, scaledInstance, self.getAbsorptionHealth())) {
                     this.anchor = "release:self";
                     return null;
                  } else {
                     CenterVecData.CrystalStep crystalStep = new CenterVecData.CrystalStep(cell, null, Direction.UP, false, true, true, view.feet(), view.targetBox());
                     this.anchor = "release";
                     return this.getLoopHit(
                        ComboingIntersects.Phase.RELEASE,
                        ctx.tick(),
                        toughnessEpf,
                        SurroundKitType.CRYSTAL,
                        crystalStep,
                        null,
                        null,
                        null,
                        getFollowCrystal(view, cell, view.anchor().y),
                        vec3d.add(0.0, 0.5, 0.0),
                        "Comboing"
                     );
                  }
               }
            }
         } else if (!canPlaceTick(ctx, cell)) {
            return null;
         } else {
            this.anchor = "release:spawn";
            return this.getLoopHit(
               ComboingIntersects.Phase.RELEASE,
               ctx.tick(),
               posBlockData6.value(),
               SurroundKitType.NONE,
               null,
               null,
               null,
               null,
               getFollowCrystal(view, cell, view.anchor().y),
               BlockEntityUtils.getVec3d(cell).add(0.0, 0.5, 0.0),
               "Comboing"
            );
         }
      } else {
         return null;
      }
   }

   private static int getSlotSword(VecPos.Loadout loadout, ComboingIntersects.Ctx ctx, ClientPlayerEntity clientPlayerEntity) {
      int slotSwordValue = loadout == null ? -1 : loadout.swordSlot();
      return slotSwordValue < 0
         ? Integer.MAX_VALUE
         : IframeComboUtils.getValue(
            clientPlayerEntity.getInventory().getSelectedSlot() == slotSwordValue, ctx.self().swordCharge(), clientPlayerEntity.getAttackCooldownProgressPerTick(), VecPosUtilsAmount.getValue(1.6) + 1
         );
   }

   private static Vec3d getSample(ComboingIntersects.View view) {
      PosVecUtils.Sample sample = view.sample();
      return new Vec3d(sample.x() + sample.vx(), sample.y() + 0.45, sample.z() + sample.vz());
   }

   static boolean canIgnoredFar(String ignoredFarText) {
      if (ignoredFarText == null) {
         return false;
      } else {
         return switch (ignoredFarText) {
            case "blocked", "far", "unsafe", "can't build", "no base", "ignored" -> true;
            default -> false;
         };
      }
   }

   private static boolean canAnchorGlowstone(ComboingIntersects.Ctx ctx) {
      VecBlockingData target = ctx.target();
      HimEtaData anchor = ctx.s().anchor();
      boolean anchorGlowstoneFlag = anchor != null && anchor.busy() && anchor.ownerId() == target.id();
      boolean anchorGlowstoneFlag2 = target.main() == BowGappleType.ANCHOR || target.main() == BowGappleType.GLOWSTONE;
      return IframeComboUtils.canFlag(target.distance(), anchorGlowstoneFlag, anchorGlowstoneFlag2);
   }

   private int getHeldAvailable(ComboingIntersects.Ctx ctx) {
      CaseMaxData options = ctx.s().getOptions(SurroundKitType.CRYSTAL);
      CaseMaxData caseMaxData = ctx.s().getOptions(SurroundKitType.ANCHOR);
      boolean heldAvailableFlag = options.available() && !"held".equals(options.why()) && !this.canTimes(0, ctx.tick());
      boolean heldAvailableFlag2 = this.canAnchorsFresh(ctx) && caseMaxData.available() && ctx.s().targetReadyIn() <= getPrepTicks(ctx, caseMaxData) && !this.canTimes(1, ctx.tick());
      return !this.airRefBareData.on()
         ? IframeComboUtils.getValueForFlagDouble(heldAvailableFlag, options.value(), heldAvailableFlag2, caseMaxData.value())
         : IframeComboUtils.getValue(
            heldAvailableFlag && IframeComboUtils.canComboHeld(options.available(), options.why(), options.value()),
            IframeComboUtils.getAmount(options.why(), options.value()),
            heldAvailableFlag2,
            caseMaxData.value()
         );
   }

   private static double getGroundedFeet(ComboingIntersects.View view) {
      return getAnchor(view, view.feet().y, view.grounded());
   }

   private CenterVecData getTapComboing(ComboingIntersects.Ctx ctx, ComboingIntersects.View view) {
      TickStatusData snap = ctx.snap();
      if (!this.playstyleTerminator || snap == null || !IframeComboUtils.canFlag(view.readyIn(), IframeComboUtils.getTime(ctx.tick(), snap.lastBreakTick()))) {
         return null;
      } else if (this.canTickLoadout(ctx, view)) {
         return null;
      } else {
         BlockPos base = snap.base();
         if (base != null && snap.targetId() == ctx.target().id() && canBreakPlace(snap.kind())) {
            ComboingIntersects.Episode kindEpisode174 = this.kindEpisode;
            if (kindEpisode174 != null && kindEpisode174.doneOpenType == ComboingIntersects.Outcome.OPEN) {
               return null;
            } else {
               ComboingIntersects.WindowPlan posBlockData6 = this.posBlockData;
               if (posBlockData6 != null && ctx.world().findCrystalAt(posBlockData6.cell()) != null) {
                  return null;
               } else if (!this.canTimes(0, ctx.tick()) && ctx.world().canPlaceOnBedrockObsidian(base)) {
                  CaseMaxData options = ctx.s().getOptions(SurroundKitType.CRYSTAL);
                  if (!IframeComboUtils.canComboHeld(options.available(), options.why(), options.value())) {
                     return null;
                  } else {
                     this.anchor = "tap";
                     return this.getLoopHit(
                        ComboingIntersects.Phase.RANGE,
                        ctx.tick(),
                        getAmount(IframeComboUtils.getAmount(options.why(), options.value())),
                        SurroundKitType.CRYSTAL,
                        null,
                        null,
                        null,
                        null,
                        this.getWatchSeen(ctx, view),
                        WorldCollisionHelper.getTopCenter(base),
                        "Comboing"
                     );
                  }
               } else {
                  return null;
               }
            }
         } else {
            return null;
         }
      }
   }

   private ComboingIntersects.WindowPlan getWindowPos(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntity entity, PosVecUtils.Path path, int windowPosValue
   ) {
      ComboingIntersects.WindowPlan windowPlan = this.posBlockData;
      boolean windowPosFlag = windowPlan != null && Math.abs(windowPlan.window() - (ctx.tick() + windowPosValue)) <= 1L;
      ComboingIntersects.WindowPlan windowPos = windowPosFlag ? this.getIntersectsCry(ctx, view, path, windowPlan, windowPosValue) : null;
      if (windowPos == null) {
         windowPos = this.getSelfDmg(ctx, view, clientPlayerEntity, entity, path, windowPosValue);
      }

      if (windowPos != null && !windowPosFlag) {
         this.prepAnchorCount++;
      }

      return windowPos;
   }

   private float getAirRefBareData() {
      return this.airRefBareData.ref() ? 1.0F : 2.0F;
   }

   private static Vec3d getAnchor(ComboingIntersects.View view, BlockPos pos, double anchorValue) {
      double y2 = getAmount(view, anchorValue);
      return pos == null ? getVec3d(view, view.anchor().x, view.anchor().z, y2, 2.7) : getVec3d(view, pos.getX() + 0.5, pos.getZ() + 0.5, y2, 2.6);
   }

   private CenterVecData getAirOpen(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, ClientPlayerEntity clientPlayerEntity, LivingEntityHelperVisible.Seed seed, LivingEntity entity
   ) {
      if (view.readyIn() <= 0 && view.h() <= 6.0) {
         boolean airOpenFlag = !view.grounded() && ctx.tracker().isSampleGround();
         if (!IframeComboUtils.canFlag(airOpenFlag, this.getHeldAvailable(ctx))) {
            return null;
         } else {
            CenterVecData maxOpen = this.getMaxOpen(ctx, view, clientPlayerEntity, seed, entity);
            if (maxOpen != null) {
               if (airOpenFlag) {
                  this.anchor = "air:now";
               }

               return maxOpen;
            } else if (!airOpenFlag) {
               return null;
            } else {
               ComboingIntersects.Episode kindEpisode174 = this.kindEpisode;
               if (kindEpisode174 != null && kindEpisode174.doneOpenType == ComboingIntersects.Outcome.OPEN) {
                  return null;
               } else {
                  VecPos.Loadout loadout = ctx.loadout();
                  long tick = ctx.tick();
                  if (loadout != null && loadout.crystalSlot() >= 0 && !this.canTimes(0, tick)) {
                     PosVecUtils.Path calToward = this.getCalToward(ctx, view, seed, 40);
                     if (calToward == null) {
                        return null;
                     } else {
                        int airOpenValue = IframeComboUtils.getValue(calToward.getValueGround(), calToward.getValue(), 0);
                        if (airOpenValue < 0) {
                           return null;
                        } else {
                           ComboingIntersects.WindowPlan posBlockData = this.getWindowPos(ctx, view, clientPlayerEntity, entity, calToward, airOpenValue);
                           if (posBlockData == null) {
                              return null;
                           } else {
                              this.posBlockData = posBlockData;
                              return this.getAnchorComboing(ctx, view, posBlockData, calToward, airOpenValue, true);
                           }
                        }
                     }
                  } else {
                     return null;
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   private static boolean canFreshSince(ComboingIntersects.Ctx ctx) {
      int ticksSinceFresh = ctx.self().ticksSinceFresh();
      return ticksSinceFresh >= 0 && ticksSinceFresh <= 2;
   }

   private CenterVecData getComboingAnchor(ComboingIntersects.Ctx ctx, ComboingIntersects.View view, CaseMaxData caseMaxData, String anchor) {
      this.anchor = anchor;
      return this.getLoopHit(
         ComboingIntersects.Phase.RANGE,
         ctx.tick(),
         getAmount(caseMaxData.value()),
         SurroundKitType.ANCHOR,
         null,
         null,
         null,
         null,
         this.getAmount(view),
         getSample(view),
         "Comboing"
      );
   }

   private static Vec3d getVec3d(BlockPos pos) {
      return new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
   }

   private static int getPrepTicks(ComboingIntersects.Ctx ctx, CaseMaxData caseMaxData) {
      if (StaleOwed.canTerminator(ctx.s())) {
         return Integer.MAX_VALUE;
      } else {
         VecBlockingData target = ctx.target();
         boolean prepTicksFlag = target == null || StaleOwed.canFlag(target.main(), target.off());
         return IframeComboUtils.getValue(caseMaxData.prepTicks(), prepTicksFlag);
      }
   }

   private static int getLoadoutSword(ComboingIntersects.Ctx ctx, ClientPlayerEntity clientPlayerEntity, boolean loadoutSwordFlag) {
      if (loadoutSwordFlag) {
         return getInventoryStack(clientPlayerEntity);
      } else {
         VecPos.Loadout loadout = ctx.loadout();
         return loadout == null ? -1 : loadout.swordSlot();
      }
   }

   private static Vec3d getVec3d(ComboingIntersects.View view, double vec3dValue, double vec3dValue2, double y2, double vec3dFactor) {
      Vec3d lengthSquared = getLengthSquared(view, vec3dValue, vec3dValue2);
      return new Vec3d(vec3dValue - lengthSquared.x * vec3dFactor, y2, vec3dValue2 - lengthSquared.z * vec3dFactor);
   }

   private void handleDistanceAmount(long time, double distanceTo) {
      this.distanceUpdateTime = time;
      this.distanceTo = distanceTo;
      this.distanceValue = 0;
      this.distanceBonus = 0.0;
      this.routedSearchingValue = 0;
   }

   private String formatPendingWeapon(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, LivingEntity entity, int pendingWeaponValue, boolean pendingWeaponFlag, long freshLimit, boolean pendingWeaponFlag2
   ) {
      this.vecTime = null;
      if (pendingWeaponValue < 0) {
         return pendingWeaponFlag ? "shield" : "weapon";
      } else if (freshLimit < this.weapon) {
         return "stall";
      } else if (ctx.target().immune()) {
         return "immune";
      } else if (ctx.self().usingItem()) {
         return "using";
      } else if (this.using != Long.MIN_VALUE
         && IframeComboUtils.canFlag(freshLimit - this.using, (int)Math.ceil(Math.max(0.0, ctx.self().loopMs()) / 50.0), ctx.target().ticksSinceFresh())) {
         return "pending";
      } else if (!pendingWeaponFlag2 && !pendingWeaponFlag && this.canTimeFresh(ctx, freshLimit)) {
         return "pending";
      } else if (view.readyIn() > 0 && !pendingWeaponFlag2) {
         return "iframe";
      } else if (BlockEntityUtils.getMinMax(view.eye(), view.targetBox()) > 3.0) {
         return "reach";
      } else {
         this.vecTime = this.getVecTime(view, entity, freshLimit);
         return this.vecTime == null ? "los" : null;
      }
   }

   private static CenterVecData.Stand getFollowLow(ComboingIntersects.View view, Vec3d vec, BlockPos pos) {
      if (view.pocket()) {
         return getFollowAbove(view, pos);
      } else {
         Vec3d vec3d = vec == null ? view.anchor() : vec;
         double groundedFeet = getGroundedFeet(view);
         Vec3d vec3d2 = getVec3d(view, pos, getVec3d(view, vec3d.x, vec3d.z, groundedFeet, 2.6));
         return new CenterVecData.Stand(
            CenterVecData.StandKind.FOLLOW, vec3d2, 0.35, true, true, Long.MIN_VALUE, new Vec3d(vec3d.x, groundedFeet, vec3d.z), PreferAnchorData.Kind.MELEE_LOW
         );
      }
   }

   private boolean canAvailableWhy(ComboingIntersects.Ctx ctx, boolean availableWhyFlag) {
      if (!availableWhyFlag && this.playstyleTerminator && this.airRefBareData.on() && !this.canTimes(0, ctx.tick())) {
         if (IframeComboUtils.canFlag(this.kitDelayTicks, BoxLengthUtils.getMax(ctx.self().pingMs()))) {
            return false;
         } else {
            CaseMaxData options = ctx.s().getOptions(SurroundKitType.CRYSTAL);
            if (!IframeComboUtils.canComboHeld(options.available(), options.why(), options.value())) {
               return false;
            } else {
               CaseMaxData options2 = ctx.s().getOptions(SurroundKitType.MELEE);
               boolean availableWhy = IframeComboUtils.canFlag(
                  IframeComboUtils.isEquals(options.why()) && !this.airRefBareData.ref(),
                  options.value(),
                  options2.available() ? options2.value() : Double.NaN,
                  options2.available() && options2.kills()
               );
               if (availableWhy) {
                  this.tickIframe = ctx.tick();
               }

               return availableWhy;
            }
         }
      } else {
         return false;
      }
   }

   private ComboingIntersects.View getGroundBox(ComboingIntersects.Ctx ctx, ClientPlayerEntity clientPlayerEntity, LivingEntityHelperVisible.Seed seed, LivingEntity entity) {
      PosVecUtils.Sample sample = seed.sample();
      Vec3d vec3d = new Vec3d(sample.x(), sample.y(), sample.z());
      AbsorptionPosData self = ctx.self();
      Vec3d vec3d2 = self.pos() != null ? self.pos() : clientPlayerEntity.getEntityPos();
      Vec3d vec3d3 = self.eye() != null ? self.eye() : clientPlayerEntity.getEyePos();
      Box box = self.box() != null ? self.box() : clientPlayerEntity.getBoundingBox();
      Vec3d velocity = clientPlayerEntity.getVelocity();
      if (!clientPlayerEntity.isOnGround() || velocity.horizontalLengthSquared() > 1.0E-4) {
         box = box.stretch(clientPlayerEntity.isOnGround() ? new Vec3d(velocity.x, 0.0, velocity.z) : velocity).expand(0.05);
      }

      double groundBoxValue = WorldCollisionHelper.horizontalDistance(vec3d2, vec3d);
      Vec3d vec3d4 = LivingEntityHelperVisible.getVec3d(vec3d.subtract(vec3d2));
      if (vec3d4.lengthSquared() < 0.5) {
         double angle = Math.toRadians(clientPlayerEntity.getYaw());
         vec3d4 = new Vec3d(-Math.sin(angle), 0.0, Math.cos(angle));
      }

      Vec3d vec3d5 = new Vec3d(-vec3d4.x, 0.0, -vec3d4.z);
      Vec3d vec3d6 = vec3d;
      if (ctx.tracker().isSampleGround() && (entity == null || !entity.isTouchingWater())) {
         PosVecUtils.Path sampleTick = ctx.tracker().getSampleTick(40, ctx.world(), ctx.cal(), vec3d5, null);
         if (sampleTick != null) {
            vec3d6 = getVec3d(sampleTick.getDataForInt(Math.min(sampleTick.getValueGround(), sampleTick.getValue() - 1)));
         }
      }

      double groundBoxValue2 = sample.onGround() ? vec3d.y : vec3d6.y;
      ClientWorld world = MinecraftClient.getInstance().world;
      BlockPos floored = BlockPos.ofFloored(vec3d.x, vec3d.y + 0.001, vec3d.z);
      int groundBoxValue3 = sample.onGround() && world != null ? BlockStateUtilsDirections.getOffsetDirections(world, floored) : 0;
      boolean flooredFlag = this.canFlooredFlag(ctx, groundBoxValue3, floored, vec3d, vec3d6, groundBoxValue, sample.onGround());
      EmptyLvl.Pick lvlEmpty = this.getLvlEmpty(ctx, vec3d6, groundBoxValue2, vec3d2, groundBoxValue, flooredFlag);
      boolean relHigh = EmptyLvl.canRelHigh(lvlEmpty, groundBoxValue, sample.onGround());
      this.airText = EmptyLvl.formatLvlNone(vec3d2.y - groundBoxValue2, lvlEmpty, relHigh, this.emptyLvl.getEmpty());
      boolean groundBoxFlag = vec3d2.y - groundBoxValue2 > 0.5;
      long iframeComboLimit = IframeComboUtils.getTime(ctx.tick(), Math.max(this.flooredValue, this.iframeLastKitTime));
      if (clientPlayerEntity.isOnGround()) {
         this.y = vec3d2.y;
      }

      double groundBoxLimit = (!clientPlayerEntity.isOnGround() && !Double.isNaN(this.y) ? Math.min(vec3d2.y, this.y) : vec3d2.y) - groundBoxValue2;
      boolean groundBoxFlag2 = groundBoxLimit > 0.5;
      this.groundBoxValue = clientPlayerEntity.isOnGround() ? Math.min(this.groundBoxValue + 1, 2) : 0;
      boolean groundBoxFlag3 = groundBoxFlag2 && groundBoxValue <= 7.0;
      if (!relHigh && groundBoxFlag3 && this.groundBoxValue >= 2 && lvlEmpty != null && lvlEmpty.dist() <= 10.0) {
         relHigh = true;
      }

      boolean groundBoxFlag4 = flooredFlag && (relHigh || !groundBoxFlag2 && !IframeComboUtils.canFlag(iframeComboLimit, true) && (!groundBoxFlag || canAnchorsCfg(ctx)));
      return new ComboingIntersects.View(
         sample,
         vec3d,
         vec3d2,
         vec3d3,
         box,
         groundBoxValue,
         vec3d4,
         vec3d5,
         entity.getBoundingBox(),
         clientPlayerEntity.getYaw(),
         clientPlayerEntity.getPitch(),
         ctx.s().targetReadyIn(),
         ctx.target().isHoleSides(),
         vec3d6,
         sample.onGround(),
         groundBoxValue2,
         lvlEmpty,
         relHigh,
         flooredFlag,
         groundBoxFlag4,
         groundBoxLimit,
         groundBoxFlag3
      );
   }

   private static ComboingIntersects.KickModel getCalLoadout(
      ComboingIntersects.Ctx ctx, ComboingIntersects.View view, LivingEntityHelperVisible.Seed seed, boolean calLoadoutFlag
   ) {
      PosVecUtils.Sample sample = seed.sample();
      double[] doubles = new double[]{sample.vx(), sample.vy(), sample.vz()};
      float calLoadoutValue = VecShieldUtilsAmounts.getAmount(view.u().x, view.u().z);
      int enchantmentsDefault = ctx.loadout() == null ? 0 : ctx.loadout().knockback();
      VecPosUtilsAmount.HitKind hitKind = calLoadoutFlag ? VecPosUtilsAmount.HitKind.BLOCKED : VecPosUtilsAmount.HitKind.FRESH;
      double calLoadoutValue3 = !calLoadoutFlag && ctx.cal() != null ? ctx.cal().getExplosionScale() : 1.0;
      double[] freshBlocked = VecPosUtilsAmount.getFreshBlocked(
         doubles, sample.onGround(), seed.rMelee(), -view.u().x, -view.u().z, calLoadoutValue, VecPosUtilsAmount.getAmount(0.0, enchantmentsDefault, false), hitKind
      );
      double[] comboingIntersectsValues = VecPosUtilsAmount.getFreshBlocked(
         doubles, sample.onGround(), seed.rMelee(), -view.u().x, -view.u().z, calLoadoutValue, VecPosUtilsAmount.getAmount(0.0, enchantmentsDefault, true), hitKind
      );
      return new ComboingIntersects.KickModel(getAmounts(freshBlocked, calLoadoutValue3), getAmounts(comboingIntersectsValues, calLoadoutValue3), Math.max(1, seed.time().getMax()));
   }

   private ComboingIntersects.Cand getIntersectsEye(
      ComboingIntersects.Ctx ctx,
      ComboingIntersects.View view,
      ClientPlayerEntity clientPlayerEntity,
      LivingEntity entity,
      PosVecUtils.Path path,
      int intersectsEyeValue,
      int iframeComboValue,
      boolean intersectsEyeFlag,
      double squaredFactor,
      PosVecUtils.Sample sample,
      BlockPos pos
   ) {
      WorldCollisionHelper world = ctx.world();
      if (this.canFlag(pos)) {
         return null;
      } else {
         Vec3d vec3d = BlockEntityUtils.getVec3d(pos);
         if (ctx.veto() != null && ctx.veto().test(vec3d)) {
            return null;
         } else {
            ComboingIntersects.Kind kind;
            if (world.canPlaceOnBedrockObsidian(pos)) {
               if (world.findCrystalAt(pos) != null) {
                  kind = ComboingIntersects.Kind.ARMED;
               } else {
                  if (!world.isAirAt(pos.up())) {
                     return null;
                  }

                  kind = ComboingIntersects.Kind.DIRECT;
               }
            } else {
               if (!intersectsEyeFlag || !world.isReplaceableAt(pos) || !world.isAirAt(pos.up()) || !world.canOppositeOffsetAt(pos)) {
                  return null;
               }

               kind = ComboingIntersects.Kind.BUILD;
            }

            double iframeComboLimit = pos.getY() + 1.0 - sample.y();
            double intersectsEyeValue5 = WorldCollisionHelper.getAmount(pos.getX() + 0.5, pos.getZ() + 0.5, sample.x(), sample.z());
            if (!IframeComboUtils.canFlag(iframeComboLimit, intersectsEyeValue5, view.hole(), sample.onGround()) && !IframeComboUtils.canFlagForDoubleFlag(iframeComboLimit, intersectsEyeValue5, sample.onGround())) {
               return null;
            } else {
               Vec3d vec3d2 = getVec3d(view, pos, getAnchor(view, sample.y(), sample.onGround()));
               if (!canFlag(vec3d2.add(0.0, 1.62, 0.0), pos, 0.1)) {
                  return null;
               } else {
                  Vec3d vec3d3 = WorldCollisionHelper.getTopCenter(pos);
                  Vec3d add = vec3d.add(0.0, 0.5, 0.0);
                  byte intersectsEyeValue6 = -1;
                  byte intersectsEyeValue7 = -1;
                  int intersectsEyeLimit;
                  int intersectsEyeLimit2;
                  double eyePitch;
                  if (kind == ComboingIntersects.Kind.BUILD) {
                     Vec3d oppositeSquared = getOppositeSquared(pos, view.eye());
                     if (oppositeSquared == null) {
                        return null;
                     }

                     double angleFromRotation = WorldCollisionHelper.computeAngleFromRotation(view.yaw(), view.pitch(), view.eye(), oppositeSquared);
                     double angleDifference = WorldCollisionHelper.computeAngleDifference(view.eye(), oppositeSquared, vec3d3);
                     double angleDifference11 = WorldCollisionHelper.computeAngleDifference(view.eye(), vec3d3, add);
                     int[] intersectsEyeValues = IframeComboUtils.getValues(iframeComboValue, angleFromRotation, angleDifference);
                     intersectsEyeLimit = intersectsEyeValues[0];
                     intersectsEyeLimit2 = intersectsEyeValues[1];
                     if (intersectsEyeValue < intersectsEyeLimit) {
                        return null;
                     }

                     if (getEye(view, vec3d2, intersectsEyeValue - intersectsEyeLimit).distanceTo(oppositeSquared) > squaredFactor) {
                        return null;
                     }

                     if (getEye(view, vec3d2, intersectsEyeValue - intersectsEyeLimit2).distanceTo(vec3d3) > squaredFactor) {
                        return null;
                     }

                     eyePitch = angleFromRotation + angleDifference + angleDifference11;
                  } else if (kind == ComboingIntersects.Kind.DIRECT) {
                     double angleFromRotation12 = WorldCollisionHelper.computeAngleFromRotation(view.yaw(), view.pitch(), view.eye(), vec3d3);
                     double angleDifference13 = WorldCollisionHelper.computeAngleDifference(view.eye(), vec3d3, add);
                     intersectsEyeLimit2 = IframeComboUtils.getValues(iframeComboValue, 0.0, angleFromRotation12)[1];
                     intersectsEyeLimit = intersectsEyeLimit2;
                     if (intersectsEyeValue < intersectsEyeLimit2) {
                        return null;
                     }

                     if (getEye(view, vec3d2, intersectsEyeValue - intersectsEyeLimit2).distanceTo(vec3d3) > squaredFactor) {
                        return null;
                     }

                     eyePitch = angleFromRotation12 + angleDifference13;
                  } else {
                     eyePitch = WorldCollisionHelper.computeAngleFromRotation(view.yaw(), view.pitch(), view.eye(), add);
                     intersectsEyeLimit2 = IframeComboUtils.getValues(iframeComboValue, 0.0, WorldCollisionHelper.computeAngleFromRotation(view.yaw(), view.pitch(), view.eye(), vec3d3))[1];
                     intersectsEyeLimit = intersectsEyeLimit2;
                  }

                  Box box = WorldCollisionHelper.getPlayerBoxAt(vec3d2.x, vec3d2.y, vec3d2.z, 0.0);
                  if (kind == ComboingIntersects.Kind.BUILD) {
                     int max14 = Math.max(0, intersectsEyeValue - intersectsEyeLimit);
                     Box boxForBlockPos = BlockEntityUtils.getBoxForBlockPos(pos);
                     if (boxForBlockPos.intersects(getBoxForSample(path.getDataForInt(max14)))
                        || boxForBlockPos.intersects(getBoxForSample(sample))
                        || boxForBlockPos.intersects(box)
                        || max14 <= 1 && (boxForBlockPos.intersects(view.targetBox()) || boxForBlockPos.intersects(view.ourBox()))) {
                        return null;
                     }
                  }

                  if (kind != ComboingIntersects.Kind.ARMED) {
                     int pathBound = Math.max(0, intersectsEyeValue - intersectsEyeLimit2);
                     Box box3 = BlockEntityUtils.getBox3(pos);
                     if (box3.intersects(getBoxForSample(path.getDataForInt(pathBound)))
                        || box3.intersects(box)
                        || pathBound <= 1 && (box3.intersects(view.targetBox()) || box3.intersects(view.ourBox()))) {
                        return null;
                     }

                     if (world.hasBlockingEntities(pos, entity, clientPlayerEntity)) {
                        return null;
                     }
                  }

                  float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(
                     BlockEntityUtils.getAmount(entity, getVec3d(sample).distanceTo(vec3d), 6.0F), ctx.target().armor()
                  );
                  if (toughnessEpf < 4.0F) {
                     return null;
                  } else {
                     double intersectsEyeValue16 = IframeComboUtils.getAmount(toughnessEpf, 0.0, kind == ComboingIntersects.Kind.BUILD, iframeComboLimit, intersectsEyeValue5, eyePitch)
                        + this.getPosBlock(pos)
                        + getAmount(sample, pos, vec3d2);
                     return new ComboingIntersects.Cand(pos, kind, intersectsEyeLimit, intersectsEyeLimit2, intersectsEyeValue, sample, iframeComboLimit, intersectsEyeValue5, eyePitch, intersectsEyeValue16);
                  }
               }
            }
         }
      }
   }

   private String formatStgPos(ComboingIntersects.Phase phase, long stgPosValue, CenterVecData.PearlShot pearlShot) {
      StringBuilder stringBuilder = new StringBuilder(72);
      stringBuilder.append("ph=").append(phase.name()).append(" fu=");
      ComboingIntersects.Kind kind = null;
      BlockPos blockPos = null;
      long stgPosValue2 = 0L;
      if (phase == ComboingIntersects.Phase.FOLLOW_UP && this.kindEpisode != null) {
         kind = this.kindEpisode.posBlockData.kind();
         blockPos = this.kindEpisode.posBlockData.cell();
         stgPosValue2 = this.kindEpisode.posBlockData.detTick() - this.kindEpisode.startTick;
      } else if (phase == ComboingIntersects.Phase.STRIKE && this.stgPosPending != null && this.stgPosPending.plan() != null) {
         kind = this.stgPosPending.plan().kind();
         blockPos = this.stgPosPending.plan().cell();
         stgPosValue2 = this.stgPosPending.plan().detTick() - this.stgPosPending.tick();
      } else if ((phase == ComboingIntersects.Phase.PREPARE || phase == ComboingIntersects.Phase.RELEASE) && this.posBlockData != null) {
         kind = this.posBlockData.kind();
         blockPos = this.posBlockData.cell();
         stgPosValue2 = this.posBlockData.window() - stgPosValue;
      }

      if (blockPos == null) {
         stringBuilder.append(this.replanText);
      } else {
         stringBuilder.append(getCh(kind)).append(':').append(blockPos.getX()).append(',').append(blockPos.getY()).append(',').append(blockPos.getZ()).append('/').append(stgPosValue2);
      }

      stringBuilder.append(" pr=");
      if (pearlShot == null) {
         stringBuilder.append('-');
      } else {
         stringBuilder.append(pearlShot.why()).append(':').append(pearlShot.flightTicks());
      }

      stringBuilder.append(" why=").append(this.anchor);
      stringBuilder.append(" stg=").append(this.ok);
      if (!"-".equals(this.iframeText)) {
         stringBuilder.append(" pw=").append(this.iframeText);
      }

      stringBuilder.append(" lg=").append(this.airText);
      if (this.flooredActive) {
         stringBuilder.append(" pk=").append(this.comboingIntersectsValue);
      }

      if (!"-".equals(this.none)) {
         stringBuilder.append(" wd=").append(this.none);
      }

      if (this.distanceValue > 0) {
         stringBuilder.append(" lv=").append(this.distanceValue).append(this.routedSearchingValue > 0 ? "o" : "");
      }

      if (this.airRefBareData.on()) {
         stringBuilder.append(" tm=").append(this.airRefBareData.formatDeepBare());
      }

      return stringBuilder.toString();
   }

   private record Cand(
      BlockPos cell,
      ComboingIntersects.Kind kind,
      int obsidianIdx,
      int placeIdx,
      int detIdx,
      PosVecUtils.Sample at,
      double dy,
      double hz,
      double turn,
      double bound
   ) {
   }

   public record Ctx(
      long tick,
      SelfPopData s,
      VecBlockingData target,
      AbsorptionPosData self,
      LivingEntityHelperVisible tracker,
      WorldCollisionHelper world,
      AmountAmounts cal,
      DeflectZone pearls,
      VecMax watch,
      VecBlock threat,
      List<VecBlockingData> enemies,
      TickStatusData snap,
      Predicate<Vec3d> veto,
      SprintResetTracker sprint,
      float selfBudget,
      VecPos.Loadout loadout,
      RoutedBreachingApi nav
   ) {
      public Ctx(
         long selfBudget,
         SelfPopData sprint,
         VecBlockingData tick,
         AbsorptionPosData cal,
         LivingEntityHelperVisible self,
         WorldCollisionHelper pearls,
         AmountAmounts s,
         DeflectZone world,
         VecMax enemies,
         VecBlock veto,
         List<VecBlockingData> loadout,
         TickStatusData target,
         Predicate<Vec3d> tracker,
         SprintResetTracker snap,
         float watch,
         VecPos.Loadout threat
      ) {
         this(selfBudget, sprint, tick, cal, self, pearls, s, world, enemies, veto, loadout, target, tracker, snap, watch, threat, null);
      }
   }

   private static final class Episode {
      ComboingIntersects.FollowUp posBlockData;
      final long startTick;
      final ComboingIntersects.KickModel maxData;
      int replanValue;
      final boolean trackerActive;
      ComboingIntersects.Outcome doneOpenType;
      final float levelAmount;

      Episode(long time, ComboingIntersects.KickModel maxData, boolean flag, float levelAmount, ComboingIntersects.FollowUp posBlockData) {
         this.startTick = time;
         this.maxData = maxData;
         this.trackerActive = flag;
         this.levelAmount = levelAmount;
         this.posBlockData = posBlockData;
         this.doneOpenType = ComboingIntersects.Outcome.OPEN;
      }
   }

   private record FollowUp(
      ComboingIntersects.Kind kind,
      BlockPos cell,
      long obsidianTick,
      long placeTick,
      long detTick,
      Vec3d feetAt,
      Box boxAt,
      double value,
      double dy,
      double hz,
      float damage,
      BlockPos support,
      Direction face
   ) {
   }

   private record KickModel(double[] walk, double[] sprint, int index) {
      double[] getSprintWalk(boolean sprintHit) {
         return sprintHit ? this.sprint : this.walk;
      }
   }

   private record Kicked(long tick, int id, ComboingIntersects.KickModel kick, boolean sprintHit) {
   }

   private static enum Kind {
      BUILD,
      DIRECT,
      ARMED,
      ANCHOR;
   }

   private static enum Outcome {
      OPEN,
      DONE,
      FAILED;
   }

   private record Pending(long tick, ComboingIntersects.FollowUp plan, ComboingIntersects.KickModel kick, boolean sprintHit, float meleeRaw, boolean turning) {
   }

   public static enum Phase {
      IDLE,
      CLOSE,
      STRIKE,
      FOLLOW_UP,
      PEARL,
      WAIT,
      RANGE,
      RELEASE,
      PREPARE;
   }

   private record View(
      PosVecUtils.Sample sample,
      Vec3d feet,
      Vec3d us,
      Vec3d eye,
      Box ourBox,
      double h,
      Vec3d u,
      Vec3d towardUs,
      Box targetBox,
      float yaw,
      float pitch,
      int readyIn,
      boolean hole,
      Vec3d anchor,
      boolean grounded,
      double level,
      EmptyLvl.Pick low,
      boolean lowOk,
      boolean pocket,
      boolean hold,
      double above,
      boolean aboveNear
   ) {
   }

   private record WindowPlan(
      ComboingIntersects.Kind kind, BlockPos cell, BlockPos support, Direction face, long window, Vec3d feetAt, Box boxAt, double value, int obsRd, int cryRd
   ) {
   }
}
