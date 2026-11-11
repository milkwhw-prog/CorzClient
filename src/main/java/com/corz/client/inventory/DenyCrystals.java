package com.corz.client.inventory;

import com.corz.client.utils.TrackFlick.Speed;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.ImmutablePos;
import com.corz.client.entity.ImmutablePos.Kind;
import com.corz.client.entity.TargetSelfDataTarget;
import com.corz.client.entity.UnsafeEntityUtils;
import com.corz.client.entity.UnsafeEntityUtils.Candidate;
import com.corz.client.entity.UnsafeEntityUtils.Request;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils.Predicted;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.CenterVecData.CrystalStep;
import com.corz.client.world.ForcedFaceData;
import com.corz.client.world.HimEtaData;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.PosBlockUtilsBonus.Window;
import com.corz.client.world.TickStatusData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BotActionType;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.PendingEntityHitTracker;
import com.corz.client.utils.TrackFlick;
import com.corz.client.categories.combat.CrystalAura;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.BonusLevelData;
import com.corz.client.utils.CarefulBalancedType;
import com.corz.client.utils.CaseMaxData;
import com.corz.client.utils.ComboTradeData;
import com.corz.client.utils.CrystalSelectedApi;
import com.corz.client.utils.DefendEngageType;
import com.corz.client.utils.ForceTimedType;
import com.corz.client.utils.HandleTime;
import com.corz.client.utils.RefBareData;
import com.corz.client.utils.SelfPopData;
import com.corz.client.utils.SurroundKitType;
import com.corz.client.utils.UnplacedAntiUtils;

public final class DenyCrystals implements CrystalSelectedApi {
   private int ledgerEntityId;
   private long lastTuningTime;
   private Candidate immutablePosData;
   private CaseMaxData caseMaxData;
   private Vec3d vec3dTuningKind;
   private final PendingEntityHitTracker entityLivingMaxPlayer;
   private int blockingEntityId;
   private boolean tuningKindActive;
   private final HotbarSlotSwitcher inventorySelected;
   private long ledgerHandswapValue;
   private boolean cfgActive = true;
   private VecBlockingData vecBlockingData;
   private int entityLivingValue;
   private ForceTimedType forceTimedType = ForceTimedType.TIMED;
   private long tuningKindValue;
   private final Object timedForceObject;
   private boolean denyCrystalsActive;
   private boolean ledgerHandswapActive;
   private boolean previousFlagTwo;
   private boolean entityLivingActive;
   private Kind tuningKindKind;
   private final LivingEntityHelperDeque livingEntityHelper;
   private boolean previousFlagFour;
   private TickStatusData posBlockTickStatusData = TickStatusData.INSTANCE;
   private final Map<Integer, int[]> entityLivingIntArrayMap = new HashMap<>();
   private boolean weaknessWorld;
   private long denyCrystalsValue;
   private boolean previousFlagThree;
   private final TrackFlick trackFlick;
   private final HandswapGate ledgerHandswap;
   public static final String HELD = "held";

   private void handle(int denyCrystalsValue, long denyCrystalsValue2) {
      int[] denyCrystalsValues = this.entityLivingIntArrayMap.get(denyCrystalsValue);
      if (denyCrystalsValues == null) {
         if (this.entityLivingIntArrayMap.size() >= 32) {
            this.handle(denyCrystalsValue2);
         }

         this.entityLivingIntArrayMap.put(denyCrystalsValue, new int[]{1, (int)(denyCrystalsValue2 & 2147483647L)});
      } else {
         denyCrystalsValues[0]++;
         denyCrystalsValues[1] = (int)(denyCrystalsValue2 & 2147483647L);
      }
   }

   private static boolean canTotemAwaiting(VecBlockingData vecBlockingData) {
      return vecBlockingData.awaitingTotem() || !vecBlockingData.totemInHand();
   }

   static CaseMaxData getCrystalsObsidian(CaseMaxData caseMaxData, Kind kind, boolean crystalsObsidianFlag, boolean tuningKindFlag) {
      if (caseMaxData == null) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.CRYSTAL, "none");
      } else {
         return canFlag(kind, false, crystalsObsidianFlag, tuningKindFlag) ? caseMaxData : CaseMaxData.getCaseMaxData(SurroundKitType.CRYSTAL, crystalsObsidianFlag ? "no obsidian" : "no crystals");
      }
   }

   static int getValue(Kind kind, int denyCrystalsValue) {
      if (kind == null) {
         return denyCrystalsValue;
      } else {
         return switch (kind) {
            case BREAK -> 0;
            case PLACE -> denyCrystalsValue;
            case OBSIDIAN -> 1 + denyCrystalsValue;
         };
      }
   }

   private boolean isSelectedInventory() {
      return this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.END_CRYSTAL), false) >= 0;
   }

   @Override
   public float[] getTrackAction(float actionValue, float pitch, float actionValue3, float routedPartialValue, boolean actionFlag) {
      return this.trackFlick.computeHumanizedRotation(actionValue, pitch, actionValue3, routedPartialValue, actionFlag ? Speed.TRACK : Speed.ACTION);
   }

   private void handleEntityLiving(VecBlockingData vecBlockingData, EndCrystalEntity endCrystalEntity, long entityLivingValue, float[] entityLivingValues) {
      LivingEntity entity = vecBlockingData.entity();
      float toughnessEpfLimit;
      float entityLivingValue3;
      if (entityLivingValues != null) {
         toughnessEpfLimit = entityLivingValues[0];
         entityLivingValue3 = entityLivingValues[1];
      } else {
         Predicted interpolatorInstance = BlockEntityUtils.getInterpolatorInstance(entity, 1, false, true);
         toughnessEpfLimit = BlockEntityUtils.getCreativeSpectator(entity, endCrystalEntity.getEntityPos(), interpolatorInstance.feet(), interpolatorInstance.box(), null, null, 6.0F);
         entityLivingValue3 = PosBlockUtilsBonus.getToughnessEpf(toughnessEpfLimit, vecBlockingData.armor());
      }

      int ceil4 = (int)Math.ceil(Math.max(0.0, (double)EntityDamageUtils.getTime()) / 50.0);
      if (this.entityLivingMaxPlayer != null) {
         this.entityLivingMaxPlayer.trackPendingEntity(vecBlockingData.id(), entityLivingValue, ceil4 + 3);
      }

      if (this.livingEntityHelper != null) {
         this.livingEntityHelper.handleLivingDeque(vecBlockingData.id(), entityLivingValue, toughnessEpfLimit, entityLivingValue3);
      }
   }

   static boolean canFlag(boolean denyCrystalsFlag, boolean denyCrystalsFlag2, boolean denyCrystalsFlag3, boolean denyCrystalsFlag4, int denyCrystalsLimit, int denyCrystalsValue) {
      return denyCrystalsFlag && !denyCrystalsFlag2 && !denyCrystalsFlag3 && !denyCrystalsFlag4 && denyCrystalsLimit > denyCrystalsValue + 2;
   }

   static boolean canFlag(boolean denyCrystalsFlag, boolean denyCrystalsFlag2, boolean denyCrystalsFlag3, boolean denyCrystalsFlag4) {
      return denyCrystalsFlag != denyCrystalsFlag2 || denyCrystalsFlag && denyCrystalsFlag3 != denyCrystalsFlag4;
   }

   private void handle(long denyCrystalsValue) {
      int denyCrystalsValue2 = (int)(denyCrystalsValue & 2147483647L);
      var iterator = this.entityLivingIntArrayMap.entrySet().iterator();

      while (iterator.hasNext()) {
         int[] denyCrystalsValues = (int[])((Entry)iterator.next()).getValue();
         int denyCrystalsLimit = denyCrystalsValue2 - denyCrystalsValues[1];
         if (denyCrystalsLimit < 0 || denyCrystalsLimit > 40) {
            iterator.remove();
         }
      }
   }

   static boolean canFlag(boolean denyCrystalsFlag, boolean denyCrystalsFlag2, boolean denyCrystalsFlag3, int denyCrystalsValue, int denyCrystalsValue2) {
      return canFlag(denyCrystalsFlag, false, denyCrystalsFlag2, denyCrystalsFlag3, denyCrystalsValue, denyCrystalsValue2);
   }

   public DenyCrystals(Object object, TrackFlick trackFlick, HandswapGate ledgerHandswap, HotbarSlotSwitcher inventorySelected, PendingEntityHitTracker max, LivingEntityHelperDeque livingEntityHelper) {
      this.ledgerHandswapValue = Long.MIN_VALUE;
      this.ledgerEntityId = -1;
      this.tuningKindValue = Long.MIN_VALUE;
      this.blockingEntityId = -1;
      this.lastTuningTime = Long.MIN_VALUE;
      this.timedForceObject = object;
      this.trackFlick = trackFlick;
      this.ledgerHandswap = ledgerHandswap;
      this.inventorySelected = inventorySelected;
      this.entityLivingMaxPlayer = max;
      this.livingEntityHelper = livingEntityHelper;
   }

   public ForceTimedType getForceTimedType() {
      return this.forceTimedType;
   }

   @Override
   public boolean canSelectedInventory(int selectedInventoryValue) {
      return this.inventorySelected.trySelectSlot(selectedInventoryValue, ClientThreadUtils.getTime());
   }

   private static Predicted getBoxPos(AbsorptionPosData absorptionPosData) {
      Vec3d vec3d = absorptionPosData.velocity() == null ? Vec3d.ZERO : absorptionPosData.velocity();
      Vec3d vec3d2 = new Vec3d(vec3d.x * 2.0, 0.0, vec3d.z * 2.0);
      if (!(vec3d2.lengthSquared() < 1.0E-6) && absorptionPosData.pos() != null && absorptionPosData.box() != null) {
         return new Predicted(absorptionPosData.pos().add(vec3d2), absorptionPosData.box().offset(vec3d2));
      } else {
         return absorptionPosData.pos() != null && absorptionPosData.box() != null ? new Predicted(absorptionPosData.pos(), absorptionPosData.box()) : null;
      }
   }

   static float getMax(float denyCrystalsBound) {
      return Float.isFinite(denyCrystalsBound) && !(denyCrystalsBound <= 0.0F) ? Math.max(0.5F, Math.min(2.0F, denyCrystalsBound)) : 2.0F;
   }

   public static ForcedFaceData getTargetObsidian(CrystalStep crystalStep) {
      if (crystalStep == null) {
         return null;
      } else {
         Predicted predicted = crystalStep.targetFeetAt() != null && crystalStep.targetBoxAt() != null ? new Predicted(crystalStep.targetFeetAt(), crystalStep.targetBoxAt()) : null;
         return new ForcedFaceData(
            crystalStep.base(), crystalStep.obsidianCell(), crystalStep.face(), crystalStep.hold(), crystalStep.breakNow(), crystalStep.trustObsidian(), predicted, crystalStep.support(), crystalStep.supportFace()
         );
      }
   }

   private static long getExpandEntities(MinecraftClient client, LivingEntity entity) {
      Box expand = entity.getBoundingBox().expand(6.0);
      long expandEntitiesTotal = 1469598103934665603L;
      int expandEntitiesCounter = 0;

      for (EndCrystalEntity endCrystalEntity : client.world.getEntitiesByClass(EndCrystalEntity.class, expand, endCrystalEntity2 -> endCrystalEntity2.isAlive() && !endCrystalEntity2.isRemoved())) {
         expandEntitiesTotal += endCrystalEntity.getId() * 1099511628211L;
         expandEntitiesCounter++;
      }

      return expandEntitiesTotal * 31L + expandEntitiesCounter;
   }

   @Override
   public boolean isFlag() {
      return this.trackFlick.didRotationMatchCommit();
   }

   public TickStatusData getPosBlockTickStatusData() {
      return this.posBlockTickStatusData;
   }

   static boolean canFlag(boolean denyCrystalsFlag, boolean cfgFlag, boolean denyCrystalsFlag3) {
      return !cfgFlag && (denyCrystalsFlag || denyCrystalsFlag3);
   }

   static boolean canBusyOwner(HimEtaData himEtaData, VecBlockingData vecBlockingData) {
      return himEtaData != null && vecBlockingData != null && himEtaData.isCell() && himEtaData.busy() && himEtaData.ownerId() == vecBlockingData.id();
   }

   @Override
   public void handleCrystal(float crystalValue, float routedPartialValue) {
      this.trackFlick.requestDirectRotation(BotActionType.CRYSTAL, crystalValue, routedPartialValue, 60);
   }

   private boolean canFlag(BlockPos pos) {
      if (pos == null) {
         return false;
      } else {
         CrystalAura crystalAura = getCrystalAura();
         HandleTime handleTime = crystalAura == null ? null : crystalAura.getActionTracker();
         return handleTime != null && handleTime.canFlag(pos.asLong(), ConnectionTimeUtils.getTimeTimeMillis());
      }
   }

   public TargetSelfDataTarget getFlagCfg(SelfPopData selfPopData, ComboTradeData comboTradeData, Predicate<Vec3d> predicate) {
      if (selfPopData == null) {
         return null;
      } else {
         VecBlockingData target = selfPopData.target();
         AbsorptionPosData self = selfPopData.self();
         if (target != null && target.entity() != null && self != null) {
            BonusLevelData minData = BonusLevelData.getMinData();
            CarefulBalancedType carefulBalancedType = selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
            this.cfgActive = carefulBalancedType != CarefulBalancedType.CAREFUL;
            RefBareData selfPop = selfPopData.getSelfPop();
            this.entityLivingActive = canTerminator(carefulBalancedType, target.distance());
            if (selfPop.on()) {
               this.entityLivingActive = true;
            }

            boolean cfgFlag = comboTradeData != null && comboTradeData.mode() == DefendEngageType.FACE_PLACE;
            boolean canFlag2 = canFlag(selfPopData.targetReadyIn(), target.lastScaledTaken());
            float cfgValue;
            boolean allowTotemTrade3;
            if (comboTradeData != null) {
               float v = comboTradeData.weights() == null ? minData.offWeight() : comboTradeData.weights().off();
               float cfgValue3 = this.cfgActive && !cfgFlag
                  ? Math.max(comboTradeData.minDamage(), getTerminator(carefulBalancedType, canFlag(target.onGround(), target.velocity()), target.distance()))
                  : comboTradeData.minDamage();
               minData = minData.getBonusLevelForFloat(cfgValue3).getBonusLevelPlanCone(comboTradeData.selfWeight()).getBonusLevel(v);
               cfgValue = comboTradeData.maxSelfDamage();
               allowTotemTrade3 = comboTradeData.allowTotemTrade();
            } else {
               cfgValue = UnplacedAntiUtils.getBareRef(self.health(), self.absorption(), self.totemConfirmed(), selfPopData.ticksSinceOwnPop(), 11.0F, selfPop);
               allowTotemTrade3 = false;
            }

            if (this.cfgActive) {
               minData = minData.getBonusLevel(selfPopData.selfSpent(), canTerminatorTotems(carefulBalancedType, self, target));
            }

            if (selfPop.on()) {
               minData = getWeightSelf(minData, selfPop);
            }

            ForcedFaceData forcedFaceData = comboTradeData != null && comboTradeData.combo() != null && !comboTradeData.comboYield() && comboTradeData.combo().crystal() != null
               ? getTargetObsidian(comboTradeData.combo().crystal())
               : null;
            BonusLevelData bonusMin = minData.getBonusMin(this.cfgActive && !cfgFlag && !target.isHoleSides() && forcedFaceData == null);
            if (canFlag(target.onGround(), target.velocity())) {
               bonusMin = bonusMin.getBonusMin(1.05);
            }

            int denyCrystalsBound = BoxLengthUtils.getMax(self.pingMs());
            int cfgValue5 = getValue(canFlag2 || this.entityLivingActive, selfPopData.targetReadyIn(), denyCrystalsBound);
            if (canFlag(this.cfgActive, this.entityLivingActive, forcedFaceData != null, canFlag2, selfPopData.targetReadyIn(), denyCrystalsBound)) {
               bonusMin = getBonusLevel(bonusMin);
            }

            Vec3d vec3d = target.lateral() == null ? Vec3d.ZERO : target.lateral();
            float cfgDivisor = target.calibration() > 0.0F && Float.isFinite(target.calibration()) ? target.calibration() : 1.0F;
            boolean selectedInventory = this.isSelectedInventory();
            if (!selectedInventory) {
               bonusMin = getBonusLevel(bonusMin);
            }

            if (selectedInventory && canFlag(this.entityLivingActive, cfgFlag, target.awaitingTotem())) {
               bonusMin = bonusMin.getBonusLevelBonusLevelData(getMax(target.getHealthTrusted() / cfgDivisor));
            }

            boolean cfgFlag4 = selectedInventory && (selfPopData.cfg() == null || selfPopData.cfg().buildObsidian());
            int cfgValue6 = selfPop.on() && forcedFaceData != null && forcedFaceData.hold() ? 0 : selfPopData.targetReadyIn();
            return new TargetSelfDataTarget(
               target.entity(),
               bonusMin,
               cfgValue,
               allowTotemTrade3,
               cfgFlag4,
               cfgValue5,
               vec3d,
               getBoxPos(self),
               new Window(cfgValue6, target.lastScaledTaken()),
               new Window(selfPopData.selfClosedFor(), self.lastScaledTaken()),
               target.getHealthTrusted() / cfgDivisor,
               target.totemInHand(),
               predicate,
               forcedFaceData
            );
         } else {
            return null;
         }
      }
   }

   public TickStatusData getPosBlock(TargetSelfDataTarget livingVecData, boolean posBlockFlag, ForceTimedType forceTimedType, VecBlockingData vecBlockingData, int value) {
      this.forceTimedType = forceTimedType == null ? ForceTimedType.TIMED : forceTimedType;
      this.vecBlockingData = vecBlockingData;
      this.entityLivingValue = value;
      CrystalAura crystalAura = getCrystalAura();
      if (crystalAura != null && this.isFlag4()) {
         long time = ClientThreadUtils.getTime();
         this.handle(time);
         this.posBlockTickStatusData = livingVecData == null ? crystalAura.getStatusSnapshot() : crystalAura.tickControlled(livingVecData, posBlockFlag);
         if (livingVecData != null && posBlockFlag) {
            this.lastTuningTime = time;
         }

         if (this.posBlockTickStatusData == null) {
            this.posBlockTickStatusData = TickStatusData.INSTANCE;
         }

         return this.posBlockTickStatusData;
      } else {
         this.posBlockTickStatusData = TickStatusData.INSTANCE;
         return this.posBlockTickStatusData;
      }
   }

   @Override
   public boolean isFlag2() {
      return true;
   }

   public void updateTimedForce() {
      CrystalAura crystalAura = getCrystalAura();
      if (crystalAura != null) {
         crystalAura.detachController(this.timedForceObject);
      }

      this.weaknessWorld = false;
      this.vecBlockingData = null;
      this.forceTimedType = ForceTimedType.TIMED;
      this.posBlockTickStatusData = TickStatusData.INSTANCE;
      this.updateFlagTime();
   }

   static float getTerminator(CarefulBalancedType carefulBalancedType, boolean terminatorFlag, double terminatorValue) {
      boolean terminatorFlag2 = Double.isFinite(terminatorValue) && terminatorValue <= 6.0;
      return carefulBalancedType == CarefulBalancedType.TERMINATOR && terminatorFlag && terminatorFlag2 ? 4.0F : 6.0F;
   }

   @Override
   public boolean canLedgerHandswap(ImmutablePos immutablePos) {
      boolean ledgerHandswapFlag = immutablePos != null && immutablePos.getBreakObsidianTypeKind() != Kind.BREAK;
      if (ledgerHandswapFlag ? this.ledgerHandswap.isSwitchAllowedWithTeleport() : this.ledgerHandswap.isSwitchAllowed()) {
         if (immutablePos != null && immutablePos.getBreakObsidianTypeKind() == Kind.BREAK) {
            EndCrystalEntity endCrystalEntity = immutablePos.getEndCrystalEntity();
            if (endCrystalEntity == null) {
               return false;
            } else {
               long time = ClientThreadUtils.getTime();
               int id = endCrystalEntity.getId();
               if (this.ledgerHandswapValue == time && this.ledgerEntityId == id) {
                  return this.ledgerHandswapActive;
               } else {
                  boolean flag = this.canEntityLiving(endCrystalEntity, time);
                  this.ledgerHandswapValue = time;
                  this.ledgerEntityId = id;
                  this.ledgerHandswapActive = flag;
                  return flag;
               }
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean canObsidian(Kind kind, BlockPos pos) {
      return kind == Kind.OBSIDIAN && pos != null && BlockEntityUtils.canBedrockObsidian(pos);
   }

   static boolean canFlag(long denyCrystalsLimit, long denyCrystalsLimit2) {
      return denyCrystalsLimit != Long.MIN_VALUE && denyCrystalsLimit <= denyCrystalsLimit2 && denyCrystalsLimit >= denyCrystalsLimit2 - 1L;
   }

   static boolean canFlag(Kind kind, boolean denyCrystalsFlag, boolean crystalsObsidianFlag, boolean tuningKindFlag) {
      if (kind == null) {
         return crystalsObsidianFlag;
      } else {
         return switch (kind) {
            case BREAK -> true;
            case PLACE -> crystalsObsidianFlag;
            case OBSIDIAN -> crystalsObsidianFlag && (denyCrystalsFlag || tuningKindFlag);
         };
      }
   }

   public int getValue(int denyCrystalsValue) {
      int[] denyCrystalsValues = this.entityLivingIntArrayMap.get(denyCrystalsValue);
      return denyCrystalsValues == null ? 0 : denyCrystalsValues[0];
   }

   static BonusLevelData getWeightSelf(BonusLevelData bonusLevelData, RefBareData refBareData) {
      return bonusLevelData != null && refBareData != null && refBareData.on() ? bonusLevelData.getBonusLevel(refBareData).getBonusLevelForFloat(1.0F).getBonusLevelPlanCone(refBareData.getAmount(bonusLevelData.selfWeight())) : bonusLevelData;
   }

   public boolean isCfgActive() {
      return this.cfgActive;
   }

   public CaseMaxData getTuningKind(SelfPopData selfPopData, TargetSelfDataTarget targetSelfDataTarget) {
      if (!this.isFlag4()) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.CRYSTAL, "idle");
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player == null || instance.world == null) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.CRYSTAL, "idle");
         } else if (!BlockEntityUtils.isAmplifierEffect()) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.CRYSTAL, "weakness");
         } else {
            VecBlockingData vecBlockingData = selfPopData == null ? null : selfPopData.target();
            if (vecBlockingData != null && vecBlockingData.entity() != null && targetSelfDataTarget != null) {
               boolean flag = this.isSelectedInventory();
               boolean tuningKindFlag = this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.OBSIDIAN), false) >= 0;
               CrystalAura crystalAura = getCrystalAura();
               TickStatusData tickStatusData = crystalAura == null ? TickStatusData.INSTANCE : crystalAura.getStatusSnapshot();
               long time = selfPopData.tick();
               boolean tuningKindFlag2 = tickStatusData.kind() != null && canFlag(this.lastTuningTime, time);
               int tuningKindValue = BoxLengthUtils.getMax(selfPopData.self() == null ? EntityDamageUtils.getCachedHurtTimeLimit() : selfPopData.self().pingMs());
               boolean heldObsidianFlag = targetSelfDataTarget.tuning() != null && targetSelfDataTarget.tuning().maxEvaluations() == 0;
               boolean busyOwner = canBusyOwner(selfPopData.anchor(), vecBlockingData);
               if (targetSelfDataTarget.directive() != null) {
                  Kind breakForced = getBreakForced(targetSelfDataTarget.directive(), tuningKindFlag2 ? tickStatusData.kind() : null);
                  if (!canFlag(breakForced, false, flag, tuningKindFlag)) {
                     return CaseMaxData.getCaseMaxData(SurroundKitType.CRYSTAL, flag ? "no obsidian" : "no crystals");
                  } else {
                     double tuningKindValue2 = tuningKindFlag2 && Double.isFinite(tickStatusData.value()) ? tickStatusData.value() : 0.0;
                     return getPrepPops(
                        new CaseMaxData(SurroundKitType.CRYSTAL, true, tuningKindValue2, tuningKindFlag2 ? getValue(tickStatusData.kind(), tuningKindValue) : 0, false, false, "combo"), busyOwner
                     );
                  }
               } else if (tuningKindFlag2
                  && tickStatusData.targetId() == vecBlockingData.id()
                  && Double.isFinite(tickStatusData.value())
                  && canFlag(tickStatusData.kind(), canObsidian(tickStatusData.kind(), tickStatusData.base()), flag, tuningKindFlag)) {
                  boolean tuningKindFlag4 = tickStatusData.kind() == Kind.BREAK && !this.canFlag(tickStatusData.base());
                  boolean tuningKindFlag5 = this.immutablePosData != null
                     && this.blockingEntityId == vecBlockingData.id()
                     && this.immutablePosData.kind() == tickStatusData.kind()
                     && this.immutablePosData.base().equals(tickStatusData.base())
                     && this.immutablePosData.pops();
                  return getPrepPops(
                     getHeldObsidian(
                        new CaseMaxData(
                           SurroundKitType.CRYSTAL,
                           true,
                           tickStatusData.value(),
                           getValue(tickStatusData.kind(), tuningKindValue),
                           tuningKindFlag5,
                           tuningKindFlag5 && canTotemAwaiting(vecBlockingData),
                           tuningKindFlag4 ? "deny" : "ok"
                        ),
                        tickStatusData.kind(),
                        heldObsidianFlag
                     ),
                     busyOwner
                  );
               } else {
                  Vec3d vec3d = vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos();
                  long expandEntities = getExpandEntities(instance, vecBlockingData.entity());
                  boolean flag2 = targetSelfDataTarget.tuning() == null || targetSelfDataTarget.tuning().maxEvaluations() > 0;
                  boolean flag3 = targetSelfDataTarget.tuning() != null && targetSelfDataTarget.tuning().levelGate();
                  boolean denyCrystalsFlag = targetSelfDataTarget.tuning() != null && targetSelfDataTarget.tuning().isTradeMode();
                  boolean flag5 = targetSelfDataTarget.targetWindow() == null || targetSelfDataTarget.targetWindow().readyIn() <= 0;
                  boolean tuningKindFlag6 = this.caseMaxData == null
                     || this.blockingEntityId != vecBlockingData.id()
                     || time - this.tuningKindValue >= 3L
                     || time < this.tuningKindValue
                     || this.vec3dTuningKind == null
                     || vec3d == null
                     || this.vec3dTuningKind.distanceTo(vec3d) > 0.75
                     || flag2 != this.previousFlagTwo
                     || flag3 != this.previousFlagThree
                     || canFlag(denyCrystalsFlag, this.previousFlagFour, flag5, this.tuningKindActive)
                     || expandEntities != this.denyCrystalsValue
                     || flag != this.denyCrystalsActive;
                  if (!tuningKindFlag6) {
                     return getPrepPops(
                        getHeldObsidian(getCrystalsObsidian(this.caseMaxData, this.tuningKindKind, flag, tuningKindFlag), this.tuningKindKind, heldObsidianFlag), busyOwner
                     );
                  } else {
                     Request request = crystalAura == null ? null : crystalAura.buildPredictionRequest(targetSelfDataTarget);
                     var list = request == null ? List.of() : UnsafeEntityUtils.getWeakUnsafe(request, 1);
                     Candidate immutablePosData = list.isEmpty() ? null : (Candidate)list.get(0);
                     CaseMaxData caseMaxData;
                     if (immutablePosData != null && Double.isFinite(immutablePosData.value())) {
                        boolean tuningKindFlag7 = immutablePosData.kind() == Kind.BREAK && !this.canFlag(immutablePosData.base());
                        double tuningKindValue3 = denyCrystalsFlag ? targetSelfDataTarget.tuning().tradeMode().getAmount(immutablePosData.value()) : immutablePosData.value();
                        caseMaxData = new CaseMaxData(
                           SurroundKitType.CRYSTAL,
                           true,
                           tuningKindValue3,
                           getValue(immutablePosData.kind(), tuningKindValue),
                           immutablePosData.pops(),
                           immutablePosData.pops() && canTotemAwaiting(vecBlockingData),
                           tuningKindFlag7 ? "deny" : immutablePosData.kind().name().toLowerCase(Locale.ROOT)
                        );
                     } else {
                        caseMaxData = new CaseMaxData(SurroundKitType.CRYSTAL, true, 0.0, tuningKindValue, false, false, "none");
                     }

                     this.caseMaxData = caseMaxData;
                     this.immutablePosData = immutablePosData;
                     this.tuningKindKind = immutablePosData != null && Double.isFinite(immutablePosData.value()) ? immutablePosData.kind() : null;
                     this.denyCrystalsActive = flag;
                     this.previousFlagTwo = flag2;
                     this.previousFlagThree = flag3;
                     this.previousFlagFour = denyCrystalsFlag;
                     this.tuningKindActive = flag5;
                     this.tuningKindValue = time;
                     this.blockingEntityId = vecBlockingData.id();
                     this.vec3dTuningKind = vec3d;
                     this.denyCrystalsValue = expandEntities;
                     return getPrepPops(getHeldObsidian(getCrystalsObsidian(caseMaxData, this.tuningKindKind, flag, tuningKindFlag), this.tuningKindKind, heldObsidianFlag), busyOwner);
                  }
               }
            } else {
               return CaseMaxData.getCaseMaxData(SurroundKitType.CRYSTAL, "no target");
            }
         }
      }
   }

   static boolean canFlag(boolean denyCrystalsFlag, Vec3d vec) {
      return denyCrystalsFlag && vec != null && Math.abs(vec.y) < 0.1;
   }

   public static boolean canTerminator(CarefulBalancedType carefulBalancedType, double terminatorValue) {
      return carefulBalancedType == CarefulBalancedType.TERMINATOR && Double.isFinite(terminatorValue) && terminatorValue <= 6.0;
   }

   static int getValue(boolean denyCrystalsFlag, int denyCrystalsLimit, int denyCrystalsBound) {
      int spotBound = !denyCrystalsFlag && denyCrystalsLimit > 0 ? Math.max(denyCrystalsBound, denyCrystalsLimit) : denyCrystalsBound;
      return Math.max(1, Math.min(8, spotBound));
   }

   private boolean canLivingEntity(Box box) {
      if (this.livingEntityHelper != null && box != null) {
         for (VecBlockingData vecBlockingData : this.livingEntityHelper.getUnmodifiableList()) {
            Vec3d vec3d = vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos();
            if (vec3d != null) {
               Vec3d add = vec3d.add(0.0, 1.62, 0.0);
               if (BlockEntityUtils.getMinMax(add, box) <= 3.2) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static CrystalAura getCrystalAura() {
      return ModuleRegistry.getModule(CrystalAura.class) instanceof CrystalAura crystalAura ? crystalAura : null;
   }

   static CaseMaxData getPrepPops(CaseMaxData caseMaxData, boolean prepPopsFlag) {
      return caseMaxData != null && prepPopsFlag && caseMaxData.available() && Double.isFinite(caseMaxData.value()) && !(caseMaxData.value() <= 0.0)
         ? new CaseMaxData(caseMaxData.lane(), true, caseMaxData.value() + 2.0, caseMaxData.prepTicks(), caseMaxData.pops(), caseMaxData.kills(), caseMaxData.why())
         : caseMaxData;
   }

   private boolean canEntityLiving(EndCrystalEntity endCrystalEntity, long entityLivingValue) {
      int id = endCrystalEntity.getId();
      VecBlockingData vecBlockingData = this.vecBlockingData;
      LivingEntity livingEntity = vecBlockingData == null ? null : vecBlockingData.entity();
      boolean entityLivingFlag = livingEntity != null && livingEntity.isAlive() && !livingEntity.isRemoved();
      if (this.forceTimedType == ForceTimedType.HOLD) {
         this.handle(id, entityLivingValue);
         return false;
      } else if (this.forceTimedType == ForceTimedType.FORCE) {
         if (entityLivingFlag && this.entityLivingValue <= 0) {
            this.handleEntityLiving(vecBlockingData, endCrystalEntity, entityLivingValue, null);
         } else if (entityLivingFlag && this.livingEntityHelper != null) {
            Predicted interpolatorInstance = BlockEntityUtils.getInterpolatorInstance(livingEntity, 1, false, true);
            this.livingEntityHelper
               .handleAmount(vecBlockingData.id(), entityLivingValue, BlockEntityUtils.getCreativeSpectator(livingEntity, endCrystalEntity.getEntityPos(), interpolatorInstance.feet(), interpolatorInstance.box(), null, null, 6.0F));
         }

         this.entityLivingIntArrayMap.remove(id);
         return true;
      } else if (!entityLivingFlag) {
         this.entityLivingIntArrayMap.remove(id);
         return true;
      } else {
         Vec3d entityPos = endCrystalEntity.getEntityPos();
         Predicted interpolatorInstance2 = BlockEntityUtils.getInterpolatorInstance(livingEntity, 1, false, true);
         float creativeSpectator = BlockEntityUtils.getCreativeSpectator(livingEntity, entityPos, interpolatorInstance2.feet(), interpolatorInstance2.box(), null, null, 6.0F);
         float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(creativeSpectator, vecBlockingData.armor());
         float scaledInstance = PosBlockUtilsBonus.getScaledInstance(creativeSpectator, vecBlockingData.armor(), new Window(this.entityLivingValue, vecBlockingData.lastScaledTaken()), 0);
         boolean posBlockFlag = scaledInstance >= vecBlockingData.getHealthTrusted();
         boolean canFlag3 = this.canFlag(BlockEntityUtils.getDown(endCrystalEntity));
         boolean entityLivingFlag4 = !canFlag3 || this.canLivingEntity(endCrystalEntity.getBoundingBox());
         int entityLivingValue2 = this.cfgActive ? 3 : 10;
         boolean entityLivingFlag5 = this.entityLivingActive
            || PosBlockUtilsBonus.canFlag(this.entityLivingValue, scaledInstance, toughnessEpf, posBlockFlag, entityLivingFlag4, this.getValue(id), entityLivingValue2)
            || this.cfgActive && scaledInstance >= 1.0F;
         if (!entityLivingFlag5) {
            this.handle(id, entityLivingValue);
            return false;
         } else {
            this.entityLivingIntArrayMap.remove(id);
            if (this.entityLivingValue <= 0) {
               this.handleEntityLiving(vecBlockingData, endCrystalEntity, entityLivingValue, new float[]{creativeSpectator, toughnessEpf});
            } else if (this.livingEntityHelper != null) {
               this.livingEntityHelper.handleAmount(vecBlockingData.id(), entityLivingValue, creativeSpectator);
            }

            return true;
         }
      }
   }

   @Override
   public void update() {
      this.trackFlick.confirmLockedTrack();
   }

   private void updateFlagTime() {
      this.entityLivingIntArrayMap.clear();
      this.ledgerHandswapValue = Long.MIN_VALUE;
      this.ledgerEntityId = -1;
      this.ledgerHandswapActive = false;
      this.caseMaxData = null;
      this.immutablePosData = null;
      this.tuningKindKind = null;
      this.denyCrystalsActive = false;
      this.previousFlagTwo = false;
      this.previousFlagThree = false;
      this.previousFlagFour = false;
      this.tuningKindActive = false;
      this.lastTuningTime = Long.MIN_VALUE;
      this.tuningKindValue = Long.MIN_VALUE;
      this.blockingEntityId = -1;
      this.vec3dTuningKind = null;
      this.denyCrystalsValue = 0L;
   }

   private static Kind getBreakForced(ForcedFaceData forcedFaceData, Kind breakForced) {
      if (breakForced == Kind.BREAK) {
         return breakForced;
      } else {
         BlockPos forcedObsidianCell = forcedFaceData.forcedObsidianCell();
         BlockPos forcedBase = forcedFaceData.forcedBase();
         boolean forcedFlag = forcedObsidianCell != null && !BlockEntityUtils.canBedrockObsidian(forcedObsidianCell);
         if (!forcedFlag && forcedObsidianCell != null && forcedBase != null && forcedBase.equals(forcedObsidianCell.up()) && !BlockEntityUtils.canBedrockObsidian(forcedBase)) {
            forcedFlag = true;
         }

         if (forcedFlag) {
            return Kind.OBSIDIAN;
         } else {
            return forcedBase != null && BlockEntityUtils.getNext(forcedBase) != null ? Kind.BREAK : Kind.PLACE;
         }
      }
   }

   @Override
   public void handleSelectedInventory(int selectedInventoryValue) {
      this.inventorySelected.queueSlotRequest(selectedInventoryValue, false);
   }

   @Override
   public float[] getActionCrystal(float actionCrystalValue, float flickBound) {
      return this.trackFlick.lockTrackRotation(BotActionType.CRYSTAL, actionCrystalValue, flickBound, Speed.ACTION, 60, 8.0F);
   }

   public boolean isFlagWeaknessWorld() {
      CrystalAura crystalAura = getCrystalAura();
      if (crystalAura == null) {
         this.weaknessWorld = false;
         return false;
      } else {
         this.weaknessWorld = crystalAura.attachController(this.timedForceObject, this);
         if (this.weaknessWorld) {
            this.updateFlagTime();
         }

         return this.weaknessWorld;
      }
   }

   static boolean canTerminatorTotems(CarefulBalancedType carefulBalancedType, AbsorptionPosData absorptionPosData, VecBlockingData vecBlockingData) {
      return carefulBalancedType == CarefulBalancedType.TERMINATOR && absorptionPosData != null && absorptionPosData.totemConfirmed() && absorptionPosData.totems() >= 4 && absorptionPosData.getAbsorptionHealth() <= 8.0F;
   }

   static CaseMaxData getHeldObsidian(CaseMaxData heldObsidian, Kind kind, boolean heldObsidianFlag) {
      if (heldObsidian == null || !heldObsidianFlag || !heldObsidian.available()) {
         return heldObsidian;
      } else {
         return kind != Kind.PLACE && kind != Kind.OBSIDIAN ? heldObsidian : new CaseMaxData(heldObsidian.lane(), true, heldObsidian.value(), heldObsidian.prepTicks(), heldObsidian.pops(), heldObsidian.kills(), "held");
      }
   }

   static BonusLevelData getBonusLevel(BonusLevelData bonusLevel) {
      if (bonusLevel != null && bonusLevel.maxEvaluations() != 0) {
         BonusLevelData bonusLevelData = new BonusLevelData(
            bonusLevel.planCone(),
            bonusLevel.keepCone(),
            bonusLevel.dyMin(),
            bonusLevel.dyMax(),
            bonusLevel.radius(),
            bonusLevel.targetRadius(),
            bonusLevel.targetDyMin(),
            bonusLevel.targetDyMax(),
            0,
            bonusLevel.eliteMode(),
            bonusLevel.offWeight(),
            bonusLevel.minDamage(),
            bonusLevel.breakMinDamage(),
            bonusLevel.popBonus(),
            bonusLevel.killBonus(),
            bonusLevel.selfWeight(),
            bonusLevel.turnCost(),
            bonusLevel.denyEnemyCrystals(),
            bonusLevel.denyThreat(),
            bonusLevel.obsidianPenalty(),
            bonusLevel.breakTempo(),
            bonusLevel.actReachMargin(),
            bonusLevel.robustTop(),
            bonusLevel.levelGate(),
            bonusLevel.levelLo(),
            bonusLevel.levelHi(),
            bonusLevel.levelLowMin(),
            bonusLevel.adjPref(),
            bonusLevel.adjMax(),
            bonusLevel.adjacentBonus(),
            bonusLevel.lowPenalty(),
            bonusLevel.tradeRule(),
            bonusLevel.selfSpent(),
            bonusLevel.popTrade(),
            bonusLevel.floorDamage()
         );
         return bonusLevelData.getBonusLevel(bonusLevel.tradeMode());
      } else {
         return bonusLevel;
      }
   }

   static boolean canFlag(int denyCrystalsLimit, float denyCrystalsValue) {
      return denyCrystalsLimit > 0 && Float.isFinite(denyCrystalsValue) && denyCrystalsValue < 20.0F;
   }

   public boolean isFlag4() {
      CrystalAura crystalAura = getCrystalAura();
      return this.weaknessWorld && crystalAura != null && crystalAura.isControlledExternally();
   }
}
