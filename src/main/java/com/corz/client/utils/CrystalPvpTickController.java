package com.corz.client.utils;

import com.corz.client.utils.ServerPacketEventQueue.Boom;
import com.corz.client.utils.ServerPacketEventQueue.Destroy;
import com.corz.client.utils.ServerPacketEventQueue.Event;
import com.corz.client.utils.ServerPacketEventQueue.EntityDamage;
import com.corz.client.utils.ServerPacketEventQueue.Pop;
import com.corz.client.utils.ServerPacketEventQueue.Spawn;
import com.corz.client.utils.KeyInputSimulation.Keys;
import com.corz.client.utils.PlayerInputOverride.Intent;
import com.corz.client.utils.PlayerInputOverride.Tri;
import com.corz.client.utils.HandswapGate.SwitchHandle;
import com.corz.client.utils.BotRunSupervisor.Level;
import com.corz.client.utils.BotRunSupervisor.Verdict;
import com.corz.client.utils.TrackFlick.Commit;
import com.corz.client.utils.TrackFlick.Speed;
import com.corz.client.utils.CrystalDodgePlanner.Dodge;
import com.corz.client.utils.CrystalDodgePlanner.ExposureFn;
import com.corz.client.utils.StandGoalPlanner.Goal;
import com.corz.client.utils.AutoEatController.EatState;
import com.corz.client.utils.ComboPlanner.Feedback;
import com.corz.client.utils.TerminatorCrystalPlanner.Input;
import com.corz.client.config.OpponentProfileStore;
import com.corz.client.config.FightStatTotals;
import com.corz.client.entity.BoxVisibilityRaycaster;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityCenterUtils;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.entity.TargetSelfDataTarget;
import com.corz.client.entity.UnsafeEntityUtils.Request;
import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.entity.VecMax.Forecast;
import com.corz.client.gui.Button;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.ZoomHud;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.inventory.CooldownEscape;
import com.corz.client.inventory.CooldownEscape.Plan;
import com.corz.client.inventory.DenyCrystals;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.inventory.LivingEntityHelperDeque;
import com.corz.client.inventory.StaleOwed;
import com.corz.client.render.CodeCategoryUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import com.corz.client.utils.ScissorTgtHandler.Tone;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockPosHelperGapple;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.CenterVecData.CrystalStep;
import com.corz.client.world.CenterVecData.PearlShot;
import com.corz.client.world.CenterVecData.Strike;
import com.corz.client.world.ClampMath;
import com.corz.client.world.ClientInstance;
import com.corz.client.world.HimEtaData;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.PosBlockUtilsBonus.Armor;
import com.corz.client.world.PosVecUtils;
import com.corz.client.world.TickStatusData;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HumanAimRotator;
import com.corz.client.utils.CrystalBotModuleMonitor;
import com.corz.client.utils.BotProbeFactory;
import com.corz.client.utils.ServerPacketEventQueue;
import com.corz.client.categories.bots.CrystalPvpBot;
import com.corz.client.utils.BotActionType;
import com.corz.client.utils.PlayerCombatState;
import com.corz.client.utils.KeyInputSimulation;
import com.corz.client.utils.PlayerInputOverride;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.RayBudget;
import com.corz.client.utils.PendingEntityHitTracker;
import com.corz.client.utils.BotRunSupervisor;
import com.corz.client.utils.FrameRotationApplier;
import com.corz.client.utils.RotationInterpolator;
import com.corz.client.utils.UserOverrideTracker;
import com.corz.client.utils.ServerTpsEstimator;
import com.corz.client.utils.PathfindingWorldAdapter;
import com.corz.client.utils.OutboundPacketClassifier;
import com.corz.client.utils.OutboundPacketKind;
import com.corz.client.utils.TickActionLedger;
import com.corz.client.utils.BlockHazardFlags;
import com.corz.client.utils.TrackFlick;
import com.corz.client.utils.AnchorRoutePlanner;
import com.corz.client.utils.CrystalDodgePlanner;
import com.corz.client.utils.StandGoalPlanner;
import com.corz.client.utils.AutoEatController;
import com.corz.client.utils.ComboPlanner;
import com.corz.client.utils.TerminatorCrystalPlanner;
import com.corz.client.categories.combat.CrystalAura;
import com.corz.client.categories.combat.ObsidianOptimizer;
import com.corz.client.categories.misc.NameProtect;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import com.corz.client.categories.visuals.Freelook;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.CampBreaching;
import com.corz.client.utils.TargetLockSelector;
import com.corz.client.utils.BlockPosHelperDefuse;
import com.corz.client.utils.CombatMovementController;
import com.corz.client.utils.MendingRepaired;
import com.corz.client.utils.AuditOwn;
import com.corz.client.utils.HotbarKitRestocker;
import com.corz.client.utils.LootProximityTracker;
import com.corz.client.utils.SelfStateTracker;
import com.corz.client.utils.ObsidianSurroundPlacer;
import com.corz.client.utils.AggressiveDefensive;
import com.corz.client.utils.CombatComboPlanner;
import com.corz.client.utils.AttributedDamageTracker;
import com.corz.client.utils.MeleeAttackController;
import com.corz.client.utils.TotemSupplierAdapter;
import com.corz.client.utils.AbsorptionFoodUtils;
import com.corz.client.utils.PlaystyleTuning;

public final class CrystalPvpTickController {
   private long winBannerUntilMs;
   private final CampBreaching campBreaching;
   private volatile boolean engagedForInputCheck;
   private ComboTradeData lastComboTradeData;
   private float lastModelEhp;
   private MinVec opponentProfile;
   private int placeCount;
   private int ticksSinceOwnPop;
   private boolean hitTargetThisTick;
   private static volatile boolean globalEngaged;
   private final long[] tickTimeMicros;
   private final CooldownEscape cooldownEscape;
   private int expiredCrystalHitCount;
   private int lowWeightStreak;
   private final TargetLockSelector blockingVec;
   private final BotRunSupervisor networkHandler;
   private final ComboPlanner finishingVec;
   private final PendingEntityHitTracker maxPlayer;
   private final BlockPosHelperDefuse defuseHelper;
   private final RotationInterpolator pitchYaw;
   private int eatbadEatminCount;
   private int tickTimeIndex;
   private int ownPopsThisFight;
   private volatile boolean engaged;
   private boolean anchorChargeThreat;
   private final CombatMovementController movementPlanner;
   private final HandswapGate ledgerHandswap;
   private final ArrayDeque<Integer> ownPearlIds;
   private LivingEntity targetEntity;
   private volatile boolean engagedNearTarget;
   private int calibratedTargetId;
   private final TrackFlick trackFlick;
   private OpponentProfileStore dirJson;
   private final CrystalBotModuleMonitor botCrystal;
   private final HotbarSlotSwitcher inventorySelected;
   private float lastTotalHealth;
   private final MendingRepaired mendingRepaired;
   private HimEtaData anchorEtaData;
   private volatile boolean botRunning;
   private final AnchorRoutePlanner vecHash;
   private String lastKillName;
   private final AutoEatController cooldownPlayer;
   private long busyLaneSinceTick;
   private boolean ownPopThisTick;
   private long lastTargetHitTick;
   private long lastTargetHitMs;
   private final DenyCrystals denyCrystals;
   private double nearestEnemy;
   private int targetId;
   private long lastWeightsTick;
   private final Predicate<Vec3d> canStandPredicate;
   private float lastTargetTotalHealth;
   private String targetName;
   private int ownThreatLevel;
   private final AuditOwn auditOwn;
   private boolean targetPoppedThisTick;
   private final LivingEntityHelperDeque livingEntityHelper;
   private final FrameRotationApplier mouseDeltaTracker;
   public static final String LOOKING_PLAYER_NEARBY = "Looking for a player nearby.";
   private boolean deathHandled;
   private String lastSprintLabel;
   private int quietTickCount;
   private volatile Snapshot statusSnapshot;
   private Vec3d lastTargetPos;
   private final UserOverrideTracker userInputTracker;
   private int anchorUnplacedCount;
   private long targetAcquiredTick;
   private int pausedTickCount;
   private int tickTimeSamples;
   private int breakCount;
   private final HotbarKitRestocker stackInventory;
   private int meleeTickCount;
   private int targetPopsThisFight;
   private final LootProximityTracker entityDistance;
   private SurroundKitType trackedBusyLane;
   private final ClientInstance clientInstance;
   private int etaTicks;
   private final ServerTpsEstimator worldTimeTracker;
   private final CrystalDodgePlanner dodgePlanner;
   private final SelfStateTracker stackPlayer;
   private final BlockPosHelperGapple blockPosHelper;
   private final ArrayDeque<Long> crystalHitTicks;
   private final StaleOwed budget;
   private int targetThreatLevel;
   private final ObsidianSurroundPlacer obsidianIdle;
   private int targetPopsVsTarget;
   private final AggressiveDefensive aggressiveDefensive;
   private int lastAuditValue;
   private final RayBudget sharedTiming;
   private final Predicate<Vec3d> distancePredicate;
   private int ownPopsVsTarget;
   private final CombatComboPlanner unplacedAnti;
   private TickStatusData lastTickStatus;
   private final TickActionLedger sprintSneak = new TickActionLedger();
   private final AttributedDamageTracker timesMax;
   private final VecBlock vecBlock;
   private int killCount;
   private final CrystalPvpBot crystalPvpBot;
   private final TerminatorCrystalPlanner terminatorPos;
   private int weightsTargetId;
   private final ServerPacketEventQueue cpacketEntity;
   private int weightsPrevThreatLevel;
   private long lastCrystalObsidianTick;
   private final MeleeAttackController entityShield;
   private int noTargetTickCount;
   private final StandGoalPlanner idealPosHelper;
   private final TotemSupplierAdapter supplierHandle;

   public boolean isEngagedNearTarget() {
      return this.engagedNearTarget;
   }

   private boolean isRestockWindow() {
      ComboTradeData lastComboTradeData07 = this.lastComboTradeData;
      return lastComboTradeData07 == null || lastComboTradeData07.restockWindow();
   }

   private static HandleTime getCrystalAuraHandleTime() {
      CrystalAura crystalAura = getCrystalAura();
      return crystalAura == null ? null : crystalAura.getActionTracker();
   }

   private Snapshot buildStatusSnapshot(Tone tone, String menuTurnText, String killWaitingText, String formatText) {
      return new Snapshot(tone, truncateText(menuTurnText, 12), killWaitingText, this.targetPopsThisFight, this.ownPopsThisFight, formatText == null ? null : truncateText(formatText, 48));
   }

   private void expireOldCrystalHits(long dequeArrayValue, AbsorptionPosData absorptionPosData) {
      double dequeArrayValue2 = absorptionPosData == null ? 2.0 : Math.ceil(Math.max(0.0, absorptionPosData.loopMs()) / 50.0);
      long dequeArrayValue3 = (long)dequeArrayValue2 + 3L;

      while (!this.crystalHitTicks.isEmpty() && dequeArrayValue - this.crystalHitTicks.peekFirst() > dequeArrayValue3) {
         this.crystalHitTicks.removeFirst();
         if (this.targetId >= 0) {
            this.expiredCrystalHitCount++;
         }
      }
   }

   private SurroundKitType getBusyLane() {
      if (this.budget.isNether()) {
         return SurroundKitType.ANCHOR;
      } else if (this.campBreaching.isBreachActionActive()) {
         return SurroundKitType.BREACH;
      } else if (this.defuseHelper.hasCommittedAction()) {
         return SurroundKitType.GUARD;
      } else if (!this.cooldownPlayer.isEatingFinished()) {
         return SurroundKitType.EAT;
      } else if (this.cooldownEscape.isAimCooldown()) {
         return SurroundKitType.PEARL;
      } else {
         return this.obsidianIdle.isSurroundActive() ? SurroundKitType.SURROUND : null;
      }
   }

   private void disableBotWithNotice(Verdict verdict) {
      AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled();

      try {
         this.crystalPvpBot.setEnabled(false);
      } finally {
         closeQuietly(disabledEnabled);
      }

      if (!verdict.quiet()) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Crystal PvP Bot", formatModuleDetail(verdict), false);
      }
   }

   private void recordShieldAttack(VecBlockingData vecBlockingData, long shieldEntityValue) {
      int ceil2 = (int)Math.ceil(Math.max(0.0, (double)EntityDamageUtils.getTime()) / 50.0);
      float shieldEntityLimit = this.entityShield.getRawAttackDamage();
      float shieldEntityBound = this.entityShield.getExpectedDamage();
      if (Float.isFinite(shieldEntityLimit) && shieldEntityLimit > 0.0F) {
         this.livingEntityHelper.handleLivingDeque(vecBlockingData.id(), shieldEntityValue, shieldEntityLimit, Float.isFinite(shieldEntityBound) ? Math.max(0.0F, shieldEntityBound) : 0.0F);
      }

      this.maxPlayer.trackPendingEntity(vecBlockingData.id(), shieldEntityValue, ceil2 + 3);
   }

   static boolean isTargetClose(VecBlockingData vecBlockingData) {
      return vecBlockingData != null && vecBlockingData.distance() <= 8.0;
   }

   public void onMouseButtonInput(Button button) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (button != null && instance.currentScreen == null && instance.options != null && button.getValue() == 1) {
         for (KeyBinding keyBinding : getTrackedKeyBindings(instance.options)) {
            if (CodeCategoryUtils.canCategoryCodeForKeyBindingInt(keyBinding, button.getButton())) {
               this.userInputTracker.recordUserInputTime(ConnectionTimeUtils.getTimeTimeMillis());
               return;
            }
         }
      }
   }

   public boolean isEngaged() {
      return this.engaged;
   }

   private String formatOpponentProfile() {
      OpponentProfileStore dirJson = this.dirJson;
      if (dirJson != null && this.opponentProfile != null) {
         return dirJson.describeProfile(this.opponentProfile);
      } else {
         return this.crystalPvpBot.getPlaystyleConfig().learnOpponents() ? "-" : "off:0:-";
      }
   }

   private static String sanitizeLabel(String replaceText) {
      return replaceText != null && !replaceText.isBlank() ? replaceText.trim().replace(' ', '_') : "-";
   }

   private static String formatModuleDetail(Verdict verdict) {
      String teleportingModuleText = verdict.reason().statusMessage;
      if (!teleportingModuleText.contains("%s")) {
         return truncateText(teleportingModuleText, 48);
      } else {
         String moduleDetailText = verdict.detail() != null && !verdict.detail().isBlank() ? verdict.detail() : "a module";
         int moduleDetailLimit = 48 - (teleportingModuleText.length() - 2);
         if (moduleDetailText.length() > moduleDetailLimit) {
            moduleDetailText = moduleDetailLimit > 3 ? moduleDetailText.substring(0, moduleDetailLimit - 3) + "..." : moduleDetailText.substring(0, Math.max(0, moduleDetailLimit));
         }

         return truncateText(String.format(Locale.ROOT, teleportingModuleText, moduleDetailText), 48);
      }
   }

   private static String applyNameProtect(String waitingPlayerText) {
      if (waitingPlayerText == null) {
         return null;
      } else {
         String waitingPlayerText2 = NameProtect.getString(waitingPlayerText);
         return waitingPlayerText2 == null ? waitingPlayerText : waitingPlayerText2;
      }
   }

   public Snapshot getStatusSnapshot() {
      return this.statusSnapshot;
   }

   private void resetBusyModules() {
      ExtUtils.handleFlag(false);
      this.stackInventory.cancelPendingAction();
      this.cooldownPlayer.cancelEating();
      PlayerInputOverride.cancelRequestFrom(this.crystalPvpBot);
      this.budget.updateNetherGone();
      this.obsidianIdle.resetSurround();
      this.cooldownEscape.updateCooldownThrown();
      this.mendingRepaired.cancelMendingSession();
      this.defuseHelper.finishDefuse();
      this.campBreaching.reset();
      this.blockPosHelper.updateFlooredTime();
      this.anchorUnplacedCount = 0;
      this.pitchYaw.deactivate();
   }

   private CaseMaxData getAnchorOption(SelfPopData selfPopData, PearlsPlaystyleData pearlsPlaystyleData, boolean budgetAnchorFlag) {
      boolean nether = this.budget.isNether();
      if (!nether) {
         if (!pearlsPlaystyleData.anchors()) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.ANCHOR, "off");
         }

         if (budgetAnchorFlag) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.ANCHOR, "nether");
         }

         if (selfPopData.target() == null) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.ANCHOR, "no target");
         }

         if (this.sharedTiming.getUsedRayCount() >= this.sharedTiming.getSoftRayLimit() && !this.budget.canFiredDone(selfPopData)) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.ANCHOR, "budget");
         }
      }

      return this.budget.getNetherOwed(selfPopData, this.lastComboTradeData, this.distancePredicate);
   }

   private void updateTargetSwitch(VecBlockingData vecBlockingData, long lostSwitchValue, long dequeValue, PearlsPlaystyleData pearlsPlaystyleData) {
      if (this.targetId >= 0 && (vecBlockingData == null || vecBlockingData.id() != this.targetId)) {
         LivingEntity livingEntity = this.targetEntity;
         boolean lostSwitchFlag = livingEntity != null && (livingEntity.isDead() || livingEntity.getHealth() <= 0.0F);
         boolean switchLostFlag = lostSwitchFlag && this.lastTargetHitMs != Long.MIN_VALUE && dequeValue - this.lastTargetHitMs <= 3000L;
         if (lostSwitchFlag) {
            this.killCount++;
         }

         if (switchLostFlag) {
            this.entityDistance.recordDeath(this.lastTargetPos, dequeValue);
            this.winBannerUntilMs = dequeValue + 5000L;
            this.lastKillName = this.targetName;
         }

         this.finishFight(switchLostFlag ? "kill" : (vecBlockingData == null ? "lost" : "switch"), switchLostFlag, false, lostSwitchValue);
      }

      if (vecBlockingData != null && this.targetId < 0) {
         this.beginFightWithTarget(vecBlockingData, lostSwitchValue, pearlsPlaystyleData);
      }

      if (vecBlockingData != null && vecBlockingData.id() == this.targetId) {
         this.targetEntity = vecBlockingData.entity();
      }
   }

   public void onKeyInput(SwordBot swordBot) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (swordBot != null && instance.currentScreen == null && instance.options != null && swordBot.getValue() == 1) {
         for (KeyBinding keyBinding : getTrackedKeyBindings(instance.options)) {
            if (CodeCategoryUtils.canCategoryCode(keyBinding, swordBot.getKey())) {
               this.userInputTracker.recordUserInputTime(ConnectionTimeUtils.getTimeTimeMillis());
               return;
            }
         }
      }
   }

   static boolean hasNearbyAnchorCharge(HimEtaData himEtaData, double chargesValue, boolean chargesFlag, double chargesValue2, boolean chargesFlag2) {
      if (himEtaData != null && himEtaData.isCell() && himEtaData.charges() >= 1 && chargesValue <= 6.0) {
         return true;
      } else {
         return chargesFlag && chargesValue2 <= 6.0 ? true : chargesFlag2;
      }
   }

   public void onClientTick() {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null && instance.world != null && instance.interactionManager != null && instance.getNetworkHandler() != null) {
         long waitingValue = ClientThreadUtils.getTime();
         long timeTimeMillis = ConnectionTimeUtils.getTimeTimeMillis();
         long nanoTime = System.nanoTime();
         globalEngaged = this.engaged || this.isAnyLaneBusy();
         if (globalEngaged) {
            instance.getInactivityFpsLimiter().onInput();
         }

         try {
            this.tickBot(instance, waitingValue, timeTimeMillis);
         } catch (Throwable e) {
            this.onTickError(e, timeTimeMillis);
         }

         this.recordTickDuration(System.nanoTime() - nanoTime);
      } else {
         this.engaged = false;
         this.engagedNearTarget = false;
         this.engagedForInputCheck = false;
         this.botRunning = false;
         globalEngaged = false;
         this.setStatusSnapshot(this.buildStatusSnapshot(Tone.IDLE, "Waiting", null, "Join a world to start."));
      }
   }

   private void onPacketEvent(Event event, long time, long playerValue, int switchLostLimit) {
      if (event instanceof Spawn spawn && spawn.type() == EntityType.ENDER_PEARL && switchLostLimit >= 0 && spawn.data() == switchLostLimit) {
         this.ownPearlIds.addLast(spawn.entityId());

         while (this.ownPearlIds.size() > 16) {
            this.ownPearlIds.removeFirst();
         }
      } else if (event instanceof Destroy destroy && destroy.ids() != null) {
         for (int switchLostValue : destroy.ids()) {
            if (this.ownPearlIds.removeFirstOccurrence(switchLostValue)) {
               this.networkHandler.setExpectedPearlTeleportMs(playerValue);
            }
         }
      }

      this.stackPlayer.onPacketEvent(event, time, switchLostLimit);
      this.livingEntityHelper.handleDequeArray(event, time, switchLostLimit);
      this.finishingVec.onEntityPacketEvent(event, time, switchLostLimit);
      this.blockPosHelper.handleMaxEntity(event, time, switchLostLimit);
      this.campBreaching.onPacketEvent(event, time, switchLostLimit);
      if (event instanceof EntityDamage fresh) {
         boolean switchLostFlag = switchLostLimit >= 0 && fresh.causeId() == switchLostLimit;
         this.maxPlayer.onPendingConfirmed(fresh.entityId(), switchLostFlag, time);
         if (switchLostFlag && this.targetId >= 0 && fresh.entityId() == this.targetId) {
            if (!this.crystalHitTicks.isEmpty()) {
               this.crystalHitTicks.removeFirst();
            }

            this.lastTargetHitMs = playerValue;
            this.lastTargetHitTick = time;
            this.hitTargetThisTick = true;
            this.lowWeightStreak = 0;
         }
      } else if (event instanceof Pop pop) {
         if (pop.entityId() == switchLostLimit) {
            this.ticksSinceOwnPop = 0;
            this.ownPopThisTick = true;
            this.timesMax.extendWindow(time);
            this.ownPopsThisFight++;
            if (this.targetId >= 0) {
               this.ownPopsVsTarget++;
            }
         } else if (this.targetId >= 0 && pop.entityId() == this.targetId) {
            this.targetPoppedThisTick = true;
            this.targetPopsThisFight++;
            this.targetPopsVsTarget++;
         }
      } else if (event instanceof com.corz.client.utils.ServerPacketEventQueue.TimeUpdate timeUpdate) {
         this.worldTimeTracker.onTimeUpdate(timeUpdate.atMs());
      } else if (event instanceof Boom boom) {
         ;
      }
   }

   private void beginFightWithTarget(VecBlockingData vecBlockingData, long time, PearlsPlaystyleData pearlsPlaystyleData) {
      this.targetId = vecBlockingData.id();
      this.targetEntity = vecBlockingData.entity();
      this.lastTargetPos = vecBlockingData.pos();
      this.targetAcquiredTick = time;
      this.targetPopsVsTarget = 0;
      this.ownPopsVsTarget = 0;
      this.lastTargetHitMs = Long.MIN_VALUE;
      this.lastTargetHitTick = Long.MIN_VALUE;
      this.targetPopsThisFight = 0;
      this.ownPopsThisFight = 0;
      this.stackPlayer.resetPopCount();
      this.livingEntityHelper.handle(vecBlockingData.id());
      this.targetName = applyNameProtect(vecBlockingData.name());
      this.winBannerUntilMs = Long.MIN_VALUE;
      if (this.dirJson == null) {
         this.dirJson = OpponentProfileStore.createDefault();
      }

      this.dirJson.setPersistenceEnabled(pearlsPlaystyleData.learnOpponents());
      UUID uuid = vecBlockingData.entity() == null ? null : vecBlockingData.entity().getUuid();
      this.opponentProfile = this.dirJson.getOrCreateProfile(vecBlockingData.name(), uuid);
      this.livingEntityHelper.handleMin(vecBlockingData.id(), this.opponentProfile);
      this.livingEntityHelper.getClassForInt(vecBlockingData.id()).handleAmountFlag(this.opponentProfile.getMaxMin().getAmount(), 4.0);
      this.finishingVec.updateTarget(vecBlockingData, this.opponentProfile);
   }

   private static KeyBinding[] getTrackedKeyBindings(GameOptions gameOptions) {
      KeyBinding[] keyBindings = new KeyBinding[]{
         gameOptions.forwardKey,
         gameOptions.backKey,
         gameOptions.leftKey,
         gameOptions.rightKey,
         gameOptions.jumpKey,
         gameOptions.sneakKey,
         gameOptions.sprintKey,
         gameOptions.attackKey,
         gameOptions.useKey,
         gameOptions.pickItemKey,
         gameOptions.dropKey,
         gameOptions.swapHandsKey,
         gameOptions.inventoryKey
      };
      KeyBinding[] keyBindings2 = gameOptions.hotbarKeys == null ? new KeyBinding[0] : gameOptions.hotbarKeys;
      KeyBinding[] copyOf = Arrays.copyOf(keyBindings, keyBindings.length + keyBindings2.length);
      System.arraycopy(keyBindings2, 0, copyOf, keyBindings.length, keyBindings2.length);
      return copyOf;
   }

   static boolean isAnchorStatusMatching(SurroundKitType surroundKitType, String unplacedPlacedText) {
      return surroundKitType == SurroundKitType.MELEE && ("unplaced".equals(unplacedPlacedText) || "placed".equals(unplacedPlacedText)) || surroundKitType == SurroundKitType.CRYSTAL && "unplaced".equals(unplacedPlacedText);
   }

   private void onTickError(Throwable throwable, long networkValue) {
      try {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         if (player != null && (this.sprintSneak.hasInteractDuringHandswap() || this.sprintSneak.hasForcedInteract())) {
            this.applyRotationCommit(player, ClientThreadUtils.getTime(), this.trackFlick.resolveCommit(true));
         }
      } catch (Throwable e) {
         this.pitchYaw.deactivate();
      }

      try {
         this.resetBusyModules();
      } catch (Throwable e2) {
         this.engaged = false;
      }

      this.engaged = false;
      this.engagedNearTarget = false;
      this.engagedForInputCheck = false;
      this.networkHandler.recordError(networkValue);
      if (this.networkHandler.hasRepeatedErrors()) {
         this.disableBotWithNotice(new Verdict(Level.STOP, com.corz.client.utils.BotRunSupervisor.Reason.ERROR_LOOP, "", true, false, false));
      }
   }

   private void cancelInactiveLanes(SelfPopData selfPopData, ComboTradeData comboTradeData) {
      SurroundKitType lane = comboTradeData.lane();
      if (lane == SurroundKitType.ANCHOR || !this.budget.isNether()) {
         this.anchorUnplacedCount = 0;
      } else if (this.anchorUnplacedCount < 2 && isAnchorStatusMatching(lane, selfPopData.getOptions(SurroundKitType.ANCHOR).why())) {
         this.anchorUnplacedCount++;
      } else {
         this.budget.updateNetherGone();
         this.anchorUnplacedCount = 0;
      }

      if (lane != SurroundKitType.GUARD && this.defuseHelper.hasCommittedAction()) {
         this.defuseHelper.finishDefuse();
      }

      if (lane != SurroundKitType.BREACH && this.campBreaching.isDigHeld()) {
         this.campBreaching.cancelMining();
      }

      if (lane != SurroundKitType.SURROUND && this.obsidianIdle.isSurroundActive()) {
         this.obsidianIdle.resetSurround();
      }

      if (lane != SurroundKitType.EAT && !this.cooldownPlayer.isEatingFinished()) {
         boolean cooldownBreachingFlag = this.cooldownPlayer.getEatState() == EatState.HOLD;
         boolean safeToEatByThreat2 = !AbsorptionFoodUtils.isSafeToEatByThreat(selfPopData);
         this.cooldownPlayer.cancelEating();
         if (cooldownBreachingFlag && safeToEatByThreat2) {
            this.finishingVec.getEatabEatbad().incrementEatabEatminCount();
         }
      }

      if (lane != SurroundKitType.PEARL && this.cooldownEscape.isAimCooldown()) {
         this.cooldownEscape.updateCooldownThrown();
      }

      if (lane != SurroundKitType.XP && this.mendingRepaired.isMendingActive()) {
         this.mendingRepaired.cancelMendingSession();
      }
   }

   private void onOwnDeath(long deathEntityValue, long switchLostValue) {
      this.deathHandled = true;
      AbsorptionPosData absorptionPosData = this.stackPlayer.getLastSnapshot();
      if (absorptionPosData != null) {
         this.entityDistance.setExcludedCenter(absorptionPosData.pos(), deathEntityValue);
      }

      this.finishFight("death", false, true, switchLostValue);
   }

   private void recordKitRestockTick(long crystalObsidianTime, ComboTradeData comboTradeData, VecBlockingData vecBlockingData, Map<SurroundKitType, CaseMaxData> map) {
      if (comboTradeData.lane() != SurroundKitType.KIT && vecBlockingData != null && comboTradeData.restockWindow() && !this.sprintSneak.hasInteractThisTick()) {
         if (!(vecBlockingData.distance() <= 8.0) || getPlaystyle(this.crystalPvpBot.getPlaystyleConfig()) == CarefulBalancedType.CAREFUL) {
            if (this.lastCrystalObsidianTick == Long.MIN_VALUE || crystalObsidianTime < this.lastCrystalObsidianTick || crystalObsidianTime - this.lastCrystalObsidianTick >= 20L) {
               CaseMaxData caseMaxData = (CaseMaxData)map.get(SurroundKitType.KIT);
               if (caseMaxData != null && caseMaxData.available()) {
                  String why = caseMaxData.why();
                  if ("crystal".equals(why)
                     || "obsidian".equals(why)
                     || "pearl".equals(why)
                     || "gapple".equals(why)
                     || "xp".equals(why)
                     || "pickaxe".equals(why)) {
                     if (this.stackInventory.tryScheduleAutoSwap(true)) {
                        this.lastCrystalObsidianTick = crystalObsidianTime;
                     }
                  }
               }
            }
         }
      }
   }

   private void updateAnchorThreatState(SelfPopData selfPopData, ComboTradeData comboTradeData, AbsorptionPosData absorptionPosData, VecBlockingData vecBlockingData) {
      HimEtaData posBlockData = selfPopData.anchor();
      VecBlock.Threat threat = selfPopData.threat() == null ? VecBlock.Threat.INSTANCE : selfPopData.threat();
      double posThreatValue = posBlockData.isCell() && absorptionPosData.pos() != null ? absorptionPosData.pos().distanceTo(Vec3d.ofCenter(posBlockData.cell())) : Double.POSITIVE_INFINITY;
      boolean posThreatFlag = vecBlockingData != null && this.blockPosHelper.canMaxTime(vecBlockingData.id());
      this.anchorChargeThreat = hasNearbyAnchorCharge(posBlockData, posThreatValue, posThreatFlag, vecBlockingData == null ? Double.POSITIVE_INFINITY : vecBlockingData.distance(), comboTradeData.lane() == SurroundKitType.GUARD);
      this.anchorEtaData = posBlockData;
      this.etaTicks = threat.etaTicks();
      this.nearestEnemy = selfPopData.nearestEnemy();
   }

   private boolean isSafeToActNow() {
      return isSafeToAct(this.isAnyLaneBusy(), this.ticksSinceOwnPop, this.nearestEnemy, this.anchorEtaData.eta(), this.etaTicks);
   }

   private void setStatusSnapshot(Snapshot menuTurnData) {
      if (menuTurnData != null && !menuTurnData.equals(this.statusSnapshot)) {
         this.statusSnapshot = menuTurnData;
      }
   }

   public CrystalPvpTickController(CrystalPvpBot crystalPvpBot) {
      this.auditOwn = new AuditOwn();
      this.networkHandler = new BotRunSupervisor();
      this.userInputTracker = new UserOverrideTracker();
      this.worldTimeTracker = new ServerTpsEstimator();
      this.cpacketEntity = new ServerPacketEventQueue();
      this.trackFlick = new TrackFlick(new Random());
      this.ledgerHandswap = new HandswapGate(this.sprintSneak, this.trackFlick);
      this.pitchYaw = new RotationInterpolator();
      this.mouseDeltaTracker = new FrameRotationApplier(this.pitchYaw);
      this.inventorySelected = new HotbarSlotSwitcher(this.sprintSneak, this.ledgerHandswap);
      this.sharedTiming = new RayBudget(700, 1200);
      this.maxPlayer = new PendingEntityHitTracker();
      this.stackPlayer = new SelfStateTracker();
      this.livingEntityHelper = new LivingEntityHelperDeque();
      this.vecBlock = new VecBlock(this.sharedTiming, this.livingEntityHelper::getClassForInt);
      this.blockPosHelper = new BlockPosHelperGapple(this.sharedTiming);
      this.blockingVec = new TargetLockSelector();
      this.aggressiveDefensive = new AggressiveDefensive();
      this.unplacedAnti = new CombatComboPlanner(this.aggressiveDefensive);
      this.terminatorPos = new TerminatorCrystalPlanner();
      this.idealPosHelper = new StandGoalPlanner(this.sharedTiming);
      this.vecHash = AnchorRoutePlanner.create(this.sharedTiming, this::getMaxPowerAt);
      this.movementPlanner = new CombatMovementController(new Random(), this.vecHash);
      this.timesMax = new AttributedDamageTracker();
      this.dodgePlanner = new CrystalDodgePlanner();
      this.entityDistance = new LootProximityTracker();
      this.distancePredicate = this.entityDistance::isNearLootOrPlayers;
      this.canStandPredicate = this::canStandAt;
      this.finishingVec = new ComboPlanner(this.sharedTiming, new Random());
      this.statusSnapshot = Snapshot.INSTANCE;
      this.lastTickStatus = TickStatusData.INSTANCE;
      this.ticksSinceOwnPop = 1000000;
      this.weightsTargetId = -1;
      this.weightsPrevThreatLevel = -1;
      this.lastWeightsTick = Long.MIN_VALUE;
      this.targetId = -1;
      this.lastTargetHitMs = Long.MIN_VALUE;
      this.lastTargetHitTick = Long.MIN_VALUE;
      this.winBannerUntilMs = Long.MIN_VALUE;
      this.crystalHitTicks = new ArrayDeque<>();
      this.ownPearlIds = new ArrayDeque<>();
      this.lastTotalHealth = Float.NaN;
      this.calibratedTargetId = -1;
      this.lastModelEhp = Float.NaN;
      this.lastTargetTotalHealth = Float.NaN;
      this.lastCrystalObsidianTick = Long.MIN_VALUE;
      this.busyLaneSinceTick = Long.MIN_VALUE;
      this.lastSprintLabel = "-";
      this.anchorEtaData = HimEtaData.INSTANCE;
      this.etaTicks = 99;
      this.nearestEnemy = Double.POSITIVE_INFINITY;
      this.tickTimeMicros = new long[240];
      this.crystalPvpBot = crystalPvpBot;
      this.clientInstance = new ClientInstance(this.trackFlick, this.ledgerHandswap, this.sprintSneak, this.inventorySelected, CrystalPvpTickController::getCrystalAuraHandleTime);
      this.denyCrystals = new DenyCrystals(crystalPvpBot, this.trackFlick, this.ledgerHandswap, this.inventorySelected, this.maxPlayer, this.livingEntityHelper);
      this.budget = new StaleOwed(this.clientInstance, this.inventorySelected);
      this.entityShield = new MeleeAttackController(crystalPvpBot, this.trackFlick, this.ledgerHandswap, this.inventorySelected, this.auditOwn);
      this.obsidianIdle = new ObsidianSurroundPlacer(this.clientInstance, this.inventorySelected);
      this.cooldownPlayer = new AutoEatController(this.trackFlick, this.ledgerHandswap, this.sprintSneak, this.inventorySelected);
      this.cooldownEscape = new CooldownEscape(this.trackFlick, this.ledgerHandswap, this.inventorySelected);
      this.mendingRepaired = new MendingRepaired(this.trackFlick, this.ledgerHandswap, this.inventorySelected);
      this.stackInventory = new HotbarKitRestocker(crystalPvpBot);
      this.supplierHandle = new TotemSupplierAdapter(crystalPvpBot, this::isRestockWindow, this::isSafeToActNow);
      this.defuseHelper = new BlockPosHelperDefuse(this.clientInstance, this.inventorySelected, this.blockPosHelper, this.sharedTiming);
      this.defuseHelper.setSpotPredictionProvider(this.livingEntityHelper::getClassForInt);
      this.cooldownEscape.setIntFunction(this.livingEntityHelper::getClassForInt);
      this.campBreaching = new CampBreaching(this.clientInstance, this.ledgerHandswap, this.inventorySelected, this.stackInventory, this.movementPlanner.getPathfinder());
      this.botCrystal = new CrystalBotModuleMonitor(crystalPvpBot);
      this.idealPosHelper.setOpponentsSupplier(this.livingEntityHelper::getUnmodifiableList);
      this.livingEntityHelper.setPredicate(CrystalPvpTickController::isPosFlaggedByAura);
      this.finishingVec.setBreachingApi(this.movementPlanner.getPathfinder());
      this.vecBlock.setBlockPosHelperForBlockPosHelperGapple(this.blockPosHelper);
      this.mendingRepaired.setStackInventory(this.stackInventory);
      this.mendingRepaired.setSafetyOverrideSupplier(this.campBreaching::isCampWatch);
      this.cooldownPlayer.setStackInventory(this.stackInventory);
   }

   private static void closeQuietly(AutoCloseable autoCloseable) {
      if (autoCloseable != null) {
         try {
            autoCloseable.close();
         } catch (Exception e) {
         }
      }
   }

   public void onZoomHudInput(ZoomHud zoomHud) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (zoomHud != null && instance.currentScreen == null) {
         this.userInputTracker.recordUserInputTime(ConnectionTimeUtils.getTimeTimeMillis());
      }
   }

   public void onMouseMove(PlayerScaffold playerScaffold) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (playerScaffold != null && instance.currentScreen == null && instance.mouse != null) {
         if (instance.mouse.isCursorLocked() && instance.isWindowFocused() && !Freelook.isFlag() && !Freecam.isInstanceFlag()) {
            this.userInputTracker.addMouseMovement(playerScaffold.getAmount(), playerScaffold.getPlayerScaffoldAmount());
         }
      }
   }

   private Snapshot getPausedStatus(Verdict verdict) {
      String killWaitingText = this.targetName;

      return switch (verdict.reason()) {
         case ARMING, LOADING, RESPAWN -> this.buildStatusSnapshot(Tone.IDLE, "Waiting", null, "Getting ready.");
         case MENU -> this.buildStatusSnapshot(Tone.PAUSED, "Paused", killWaitingText, "Close the menu to let it fight.");
         case OTHER_MACRO -> this.buildStatusSnapshot(Tone.PAUSED, "Waiting", killWaitingText, "Another module is using the controls.");
         case OTHER_BOT, CONFLICT -> this.buildStatusSnapshot(Tone.PAUSED, "Waiting", killWaitingText, formatModuleDetail(verdict));
         case USER -> this.buildStatusSnapshot(Tone.PAUSED, "Paused", killWaitingText, "You have control right now.");
         case FREECAM -> this.buildStatusSnapshot(Tone.PAUSED, "Paused", killWaitingText, "Turn off Freecam to let it fight.");
         case DEAD -> this.buildStatusSnapshot(Tone.PAUSED, "Dead", killWaitingText, "Respawn to continue.");
         default -> this.buildStatusSnapshot(Tone.PAUSED, "Waiting", killWaitingText, formatModuleDetail(verdict));
      };
   }

   private static String formatThrowable(Throwable throwable) {
      StringBuilder stringBuilder = new StringBuilder(String.valueOf(throwable));
      StackTraceElement[] stackTrace = throwable.getStackTrace();

      for (int i = 0; i < Math.min(4, stackTrace.length); i++) {
         stringBuilder.append(" at ").append(stackTrace[i]);
      }

      return stringBuilder.toString();
   }

   private void updateEatLane(SelfPopData selfPopData, ComboTradeData comboTradeData, AbsorptionPosData absorptionPosData, CarefulBalancedType carefulBalancedType) {
      if (this.cooldownPlayer.isEatingFinished()) {
         boolean abortRelease = this.cooldownPlayer.beginEating(!AbsorptionFoodUtils.shouldEatGappleForHealth(absorptionPosData, carefulBalancedType));
         if (abortRelease) {
            this.finishingVec.getEatabEatbad().recordEatminResult(!AbsorptionFoodUtils.isSafeToEatByThreatAndCombo(selfPopData), selfPopData.nearestEnemy());
         }
      }

      boolean cooldownFinishingFlag = !AbsorptionFoodUtils.isSafeToEatByThreat(selfPopData);
      EatState abortReleaseType = this.cooldownPlayer.getEatState();
      boolean cooldownFinishingFlag2 = selfPopData.threat() != null && selfPopData.threat().lethal();
      EatState tickEating = this.cooldownPlayer.tickEating(cooldownFinishingFlag || cooldownFinishingFlag2);
      if (cooldownFinishingFlag && (tickEating == EatState.RELEASE || tickEating == EatState.DONE && abortReleaseType == EatState.HOLD)) {
         this.finishingVec.getEatabEatbad().incrementEatabEatminCount();
      }
   }

   private void updateLowWeightStreak(long time, VecBlockingData vecBlockingData, Map<SurroundKitType, CaseMaxData> map, ComboTradeData comboTradeData, CarefulBalancedType carefulBalancedType) {
      if (vecBlockingData != null && vecBlockingData.id() == this.weightsTargetId) {
         if (this.targetThreatLevel == 0 && (this.weightsPrevThreatLevel > 0 || this.lastWeightsTick == Long.MIN_VALUE || time - this.lastWeightsTick >= 10L)) {
            double weightsBound = Double.NEGATIVE_INFINITY;

            for (SurroundKitType surroundKitType : new SurroundKitType[]{SurroundKitType.CRYSTAL, SurroundKitType.ANCHOR, SurroundKitType.MELEE}) {
               CaseMaxData opt = (CaseMaxData)map.get(surroundKitType);
               if (opt != null && opt.available() && Double.isFinite(opt.value())) {
                  weightsBound = Math.max(weightsBound, opt.value());
               }
            }

            float weightsValue = comboTradeData != null && comboTradeData.weights() != null
               ? comboTradeData.weights().minUseful()
               : SelfHazData.getSelfHaz(DefensiveAggressiveType.BALANCED, carefulBalancedType).minUseful();
            this.lowWeightStreak = weightsBound >= weightsValue ? 0 : this.lowWeightStreak + 1;
            this.lastWeightsTick = time;
         }

         this.weightsPrevThreatLevel = this.targetThreatLevel;
      } else {
         this.lowWeightStreak = 0;
         this.weightsTargetId = vecBlockingData == null ? -1 : vecBlockingData.id();
         this.weightsPrevThreatLevel = -1;
         this.lastWeightsTick = Long.MIN_VALUE;
      }
   }

   private float getMaxPowerAt(double x2, double y2, double z2) {
      return this.vecBlock
         .getMaxPower(new Vec3d(x2, y2, z2), new Box(x2 - 0.3, y2, z2 - 0.3, x2 + 0.3, y2 + 1.8, z2 + 0.3), this.livingEntityHelper.getUnmodifiableList());
   }

   public void onMouseDelta(DeltaAngle deltaAngle) {
      this.mouseDeltaTracker.applyInterpolatedRotation(deltaAngle, this.botRunning);
   }

   private void planMovement(
      long posBlockValue,
      SelfPopData selfPopData,
      ComboTradeData comboTradeData,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      VecBlock.Threat threat,
      Verdict verdict,
      CarefulBalancedType carefulBalancedType,
      ClientPlayerEntity clientPlayerEntity
   ) {
      if (this.engaged) {
         Intent intent = Intent.idle();
         if (verdict.moveAllowed()) {
            Goal holeAmount = this.idealPosHelper.chooseGoal(posBlockValue, selfPopData, comboTradeData, this.vecBlock, this.distancePredicate);
            boolean distance = isTargetClose(vecBlockingData);
            if (distance && isTerminatorTargetFar(comboTradeData, vecBlockingData)) {
               distance = false;
            }

            this.movementPlanner.setCloseRangeMode(distance);
            Dodge dodge = null;
            if (comboTradeData.dodge()) {
               Vec3d vec3d = holeAmount == null ? absorptionPosData.pos() : holeAmount.feet();
               int minEtaTicks = Math.min(threat.etaTicks(), selfPopData.anchor().eta());
               ExposureFn centerEta = this.buildExposureFn(absorptionPosData, threat);
               dodge = this.dodgePlanner.findBestDodge(absorptionPosData.pos(), absorptionPosData.velocity(), minEtaTicks, vec3d, distance ? makeJumpPenalizedExposure(centerEta) : centerEta, this.canStandPredicate);
            }

            Vec3d vec3d2 = vecBlockingData == null ? null : (vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos());
            this.movementPlanner.applyFireImmunity(buildImmuneFireData(clientPlayerEntity, absorptionPosData, carefulBalancedType));
            AmountConstTime amountConstTime = vecBlockingData == null ? null : this.livingEntityHelper.getClassForInt(vecBlockingData.id());
            this.movementPlanner.setThreatSpot(amountConstTime == null ? null : amountConstTime.getTimeAmount(posBlockValue));
            Intent maskedFlag = this.movementPlanner.computeMovementIntent(posBlockValue, absorptionPosData, holeAmount, vec3d2, dodge, comboTradeData, this.budget.isNetherFlag());
            if (maskedFlag != null) {
               intent = maskedFlag;
            }

            if (this.movementPlanner.getPathfinder().getTimeUnreachable() == 1) {
               this.idealPosHelper.reset();
            }

            float[] vecHash = this.movementPlanner.getRouteLookAngles(this.trackFlick.getCurrentYaw());
            if (vecHash != null
               && !this.sprintSneak.hasInteractThisTick()
               && !this.isAnyLaneBusy()
               && vecBlockingData != null
               && vecBlockingData.distance() >= 6.0
               && this.movementPlanner.getPathfinder().getEstimatedTicksRemaining() >= 12.0) {
               this.trackFlick.requestSmoothedRotation(BotActionType.HEADING, vecHash[0], vecHash[1], Speed.TRACK, 25);
            }
         }

         if (this.campBreaching.isDigHeld() && !intent.urgent()) {
            intent = intent.withJump(Tri.OFF);
         }

         if (this.budget.isNether() && !intent.urgent()) {
            intent = intent.withJump(Tri.OFF);
         }

         PlayerInputOverride.submitRequest(this.crystalPvpBot, intent, 50);
      }
   }

   static ExposureFn makeJumpPenalizedExposure(ExposureFn exposureFn) {
      return (feet, jumped) -> jumped ? Float.POSITIVE_INFINITY : exposureFn.damageAt(feet, false);
   }

   public static boolean isGloballyEngaged() {
      return globalEngaged;
   }

   private boolean isTrackFlickCommitTick(long waitingPlayerValue) {
      Commit commit = this.trackFlick.getLastCommit();
      return this.engaged && commit != null && commit.tick() == waitingPlayerValue;
   }

   private void finalizeTick(
      MinecraftClient client,
      long sprintSneakValue,
      long fightingRefillingValue,
      SelfPopData selfPopData,
      ComboTradeData data,
      SurroundKitType surroundKitType,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      boolean sprintSneakFlag,
      Verdict verdict,
      Commit commit,
      ClientPlayerEntity clientPlayerEntity
   ) {
      CenterVecData combo = data.combo();
      boolean sprintSneakFlag2 = sprintSneakFlag && combo != null && !data.comboYield() && combo.strike() != null;
      boolean sprintSneakFlag3 = sprintSneakFlag2 && (combo.strike().tempo() ? this.entityShield.isSprintAttack() : combo.strike().sprintHit());
      float amount = clientPlayerEntity.getHealth() + clientPlayerEntity.getAbsorptionAmount();
      float limit = Float.isNaN(this.lastTotalHealth) ? 0.0F : Math.max(0.0F, this.lastTotalHealth - amount);
      this.lastTotalHealth = amount;
      float amountCalibration = this.computeDamageCalibration(vecBlockingData, sprintSneakValue);
      TickStatusData tickStatusData = this.lastTickStatus;
      if (tickStatusData.lastPlaceTick() == sprintSneakValue) {
         this.placeCount++;
      }

      if (tickStatusData.lastBreakTick() == sprintSneakValue) {
         this.breakCount++;
      }

      if (sprintSneakFlag) {
         this.meleeTickCount++;
      }

      if (tickStatusData.lastBreakTick() == sprintSneakValue || this.budget.isIdleTime()) {
         this.timesMax.recordEvent(sprintSneakValue);
      }

      if (RefBareData.canFlag(this.getSelfBreakDamage(tickStatusData, sprintSneakValue, clientPlayerEntity), amount)) {
         this.timesMax.handleMax(sprintSneakValue, (int)Math.ceil(Math.max(0, absorptionPosData.pingMs()) / 50.0));
      }

      this.timesMax.recordDamage(sprintSneakValue, limit);
      this.finishingVec
         .applyFeedback(
            new Feedback(
               sprintSneakValue,
               sprintSneakFlag,
               sprintSneakFlag3,
               tickStatusData,
               this.cooldownEscape.isCooldownTime(),
               this.cooldownEscape.isTime(),
               this.budget.isIdleTime(),
               this.budget.isIdleEntity(),
               limit,
               amountCalibration,
               this.ownPopThisTick,
               this.targetPoppedThisTick,
               this.sprintSneak.hasInteractDuringHandswap()
            )
         );
      this.expireOldCrystalHits(sprintSneakValue, absorptionPosData);
      if (this.sprintSneak.hasInteractDuringHandswap() && this.sprintSneak.getFirstInteractType() != null) {
         this.lastSprintLabel = data.lane().name() + "_" + this.sprintSneak.getFirstInteractType().name();
      }

      int value = this.finishingVec.getEatabEatbad().getEatminHitCount();
      this.eatbadEatminCount = value;
      int ownViolationTotal = this.auditOwn.getOwnViolationTotal();
      if (ownViolationTotal > this.lastAuditValue) {
         this.ledgerHandswap.extendLevelTicks(20);
      }

      this.lastAuditValue = ownViolationTotal;
      this.expireStuckLane(sprintSneakValue);
      if (vecBlockingData != null && vecBlockingData.id() == this.targetId) {
         this.lastTargetPos = vecBlockingData.pos();
      }

      this.updateAnchorThreatState(selfPopData, data, absorptionPosData, vecBlockingData);
      this.setStatusSnapshot(this.getFightingStatus(verdict, data, vecBlockingData, absorptionPosData, fightingRefillingValue));
      this.lastComboTradeData = data;
   }

   public void onDisable() {
      MinecraftClient instance = MinecraftClient.getInstance();
      long waitingPlayerValue = ClientThreadUtils.getTime();

      try {
         this.resetBusyModules();
         this.finishFight("disable", false, false, waitingPlayerValue);
         this.finishingVec.endEpisode("disable", false, false);
         this.denyCrystals.updateTimedForce();
         this.supplierHandle.runSupplierAction();
         PlayerInputOverride.cancelRequestFrom(this.crystalPvpBot);
         ClientPlayerEntity player = instance.player;
         int selectedSlot = this.inventorySelected.getRememberedSlot();
         if (player != null && selectedSlot >= 0 && selectedSlot < 9 && player.getInventory().getSelectedSlot() != selectedSlot && !this.sprintSneak.hasInteractThisTick()) {
            player.getInventory().setSelectedSlot(selectedSlot);
            ObsidianOptimizer.syncSelectedSlot(selectedSlot);
         }

         this.inventorySelected.resetAll();
         this.pitchYaw.deactivate();
         if (this.dirJson != null) {
            this.dirJson.applyLoadedProfiles();
            this.dirJson.saveAllEntries();
         }
      } catch (Throwable e) {
      }

      this.engaged = false;
      this.engagedNearTarget = false;
      this.engagedForInputCheck = false;
      this.botRunning = false;
      globalEngaged = false;
      this.statusSnapshot = Snapshot.INSTANCE;
   }

   private boolean hasFreshForecast(long finishingSeenValue, int vecValue) {
      Forecast vecMax = this.finishingVec.getPearlTracker().getVecMaxForInt(vecValue);
      return vecMax != null && HandswapGate.isWithinTimingWindow(vecMax.eta(), finishingSeenValue - vecMax.seenTick(), (int)Math.ceil(Math.max(0, EntityDamageUtils.getCachedHurtTimeLimit()) / 50.0));
   }

   private void recordTickDuration(long waitingPlayerValue) {
      this.tickTimeMicros[this.tickTimeIndex] = Math.max(0L, waitingPlayerValue / 1000L);
      this.tickTimeIndex = (this.tickTimeIndex + 1) % 240;
      if (this.tickTimeSamples < 240) {
         this.tickTimeSamples++;
      }
   }

   private static PlayerCombatState buildImmuneFireData(ClientPlayerEntity entity, AbsorptionPosData absorptionPosData, CarefulBalancedType carefulBalancedType) {
      if (entity != null && absorptionPosData != null) {
         StatusEffectInstance statusEffect = entity.getStatusEffect(StatusEffects.RESISTANCE);
         int effectStatusValue = statusEffect == null ? -1 : statusEffect.getAmplifier();
         return new PlayerCombatState(
            absorptionPosData.getAbsorptionHealth(),
            absorptionPosData.totemConfirmed() && absorptionPosData.totems() >= 1,
            PathfindingWorldAdapter.getArmorProtectionScore(entity),
            effectStatusValue,
            entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE),
            absorptionPosData.foodLevel(),
            absorptionPosData.usingItem(),
            absorptionPosData.pingMs(),
            (carefulBalancedType == null ? CarefulBalancedType.TERMINATOR : carefulBalancedType).ordinal()
         );
      } else {
         return PlayerCombatState.INSTANCE;
      }
   }

   private static boolean isPosFlaggedByAura(BlockPos pos) {
      HandleTime crystalAuraHandleTime = getCrystalAuraHandleTime();
      return pos != null && crystalAuraHandleTime != null && crystalAuraHandleTime.canFlag(pos.asLong(), ConnectionTimeUtils.getTimeTimeMillis());
   }

   private static String formatPressedKeys(Keys keys) {
      if (keys instanceof Keys(boolean forwardSprintFlag, boolean forwardSprintFlag2, boolean forwardSprintFlag3, boolean forwardSprintFlag4, boolean forwardSprintFlag5, boolean forwardSprintFlag6, boolean forwardSprintFlag7)) {
         boolean forwardSprintFlag8 = forwardSprintFlag7;
         StringBuilder stringBuilder = new StringBuilder(16);
         if (forwardSprintFlag) {
            stringBuilder.append("W+");
         }

         if (forwardSprintFlag2) {
            stringBuilder.append("S+");
         }

         if (forwardSprintFlag3) {
            stringBuilder.append("A+");
         }

         if (forwardSprintFlag4) {
            stringBuilder.append("D+");
         }

         if (forwardSprintFlag5) {
            stringBuilder.append("J+");
         }

         if (forwardSprintFlag6) {
            stringBuilder.append("N+");
         }

         if (forwardSprintFlag8) {
            stringBuilder.append("R+");
         }

         if (stringBuilder.isEmpty()) {
            return "-";
         } else {
            stringBuilder.setLength(stringBuilder.length() - 1);
            return stringBuilder.toString();
         }
      } else {
         return "-";
      }
   }

   private void finishFight(String switchLostText, boolean switchLostFlag, boolean lost, long switchLostValue) {
      if (this.targetId >= 0) {
         this.finishingVec.endEpisode(switchLostText, switchLostFlag, lost);
         MinVec minVec = this.opponentProfile;
         if (minVec != null) {
            FightStatTotals classPearlJson = minVec.getClassPearlJson();
            classPearlJson.opponentPopTotal = classPearlJson.opponentPopTotal + this.targetPopsVsTarget;
            classPearlJson.ownPopTotal = classPearlJson.ownPopTotal + this.ownPopsVsTarget;
            if (switchLostFlag) {
               classPearlJson.killCount++;
            }

            if (lost) {
               classPearlJson.deathCount++;
            }

            if (switchLostValue - this.targetAcquiredTick >= 100L) {
               minVec.updateMinFlag();
            }

            minVec.updateFlag();
            if (this.dirJson != null) {
               this.dirJson.saveProfile(minVec, false);
            }
         }

         this.livingEntityHelper.handleMin(this.targetId, null);
         this.targetId = -1;
         this.targetEntity = null;
         this.targetPopsVsTarget = 0;
         this.ownPopsVsTarget = 0;
         this.calibratedTargetId = -1;
         this.lastModelEhp = Float.NaN;
         this.lastTargetTotalHealth = Float.NaN;
      }
   }

   private float computeDamageCalibration(VecBlockingData vecBlockingData, long calibrationValue) {
      if (vecBlockingData == null) {
         return 0.0F;
      } else {
         float calibrationBound = 0.0F;
         float amount = vecBlockingData.health() + vecBlockingData.absorption();
         if (vecBlockingData.id() == this.calibratedTargetId) {
            boolean calibrationFlag = this.lastTargetHitTick != Long.MIN_VALUE && calibrationValue - this.lastTargetHitTick <= 3L;
            if (this.hitTargetThisTick || calibrationFlag || this.targetPoppedThisTick) {
               float calibrationFactor = vecBlockingData.calibration() > 0.0F && Float.isFinite(vecBlockingData.calibration()) ? vecBlockingData.calibration() : 1.0F;
               float pilotStatusBound = this.hitTargetThisTick && Float.isFinite(this.lastModelEhp) ? Math.max(0.0F, this.lastModelEhp - vecBlockingData.modelEhp()) * calibrationFactor : 0.0F;
               float rocketsElytraBound = vecBlockingData.healthTrusted() && Float.isFinite(this.lastTargetTotalHealth) ? Math.max(0.0F, this.lastTargetTotalHealth - amount) : 0.0F;
               calibrationBound = Math.max(pilotStatusBound, rocketsElytraBound);
               if (this.targetPoppedThisTick && Float.isFinite(this.lastTargetTotalHealth)) {
                  calibrationBound = Math.max(calibrationBound, vecBlockingData.healthTrusted() ? this.lastTargetTotalHealth : this.lastModelEhp);
               }
            }
         }

         this.calibratedTargetId = vecBlockingData.id();
         this.lastModelEhp = vecBlockingData.modelEhp();
         this.lastTargetTotalHealth = amount;
         return Float.isFinite(calibrationBound) ? calibrationBound : 0.0F;
      }
   }

   private static float getEffectiveHealth(AbsorptionPosData absorptionPosData) {
      return UnplacedAntiUtils.getAmount(absorptionPosData.health(), absorptionPosData.absorption(), absorptionPosData.totemConfirmed(), 1000000, 11.0F);
   }

   private boolean canStandAt(Vec3d vec) {
      ClientWorld world = MinecraftClient.getInstance().world;
      if (world != null && vec != null) {
         BlockPos floored = BlockPos.ofFloored(vec);
         if (!BlockHazardFlags.isSafeToStand(world, floored)) {
            return false;
         } else {
            return (BlockHazardFlags.getExtendedHazardFlags(world, floored) & 64) != 0 ? false : BlockHazardFlags.countOpenBlocksBelow(world, floored, 4) <= 3;
         }
      } else {
         return false;
      }
   }

   private long getAverageTickMicros() {
      if (this.tickTimeSamples == 0) {
         return 0L;
      } else {
         long waitingPlayerTotal = 0L;

         for (int i = 0; i < this.tickTimeSamples; i++) {
            waitingPlayerTotal += this.tickTimeMicros[i];
         }

         return waitingPlayerTotal / this.tickTimeSamples;
      }
   }

   private boolean isAnyLaneBusy() {
      return !this.cooldownPlayer.isEatingFinished()
         || this.cooldownEscape.isAimCooldown()
         || this.budget.isNether()
         || this.obsidianIdle.isSurroundActive()
         || this.mendingRepaired.isMendingActive()
         || this.defuseHelper.hasCommittedAction()
         || this.campBreaching.isBreachActionActive();
   }

   static boolean isComboAimAvailable(CenterVecData centerVecData) {
      return centerVecData != null && centerVecData.aimPoint() != null && centerVecData.strike() == null
         ? centerVecData.crystal() != null || centerVecData.anchor() != null || "Comboing".equals(centerVecData.status()) || "Terminator".equals(centerVecData.status())
         : false;
   }

   public void onInactiveTick() {
      MinecraftClient instance = MinecraftClient.getInstance();
      long waitingNetworkValue = ConnectionTimeUtils.getTimeTimeMillis();
      this.resetAll();
      this.entityDistance.clearAll();
      this.userInputTracker.reset();
      this.botCrystal.reset();
      if (instance.player != null && instance.getNetworkHandler() != null) {
         this.networkHandler.beginSession(instance.getNetworkHandler(), waitingNetworkValue);
         this.statusSnapshot = this.buildStatusSnapshot(Tone.IDLE, "Waiting", null, "Getting ready.");
      } else {
         this.networkHandler.beginSession(null, waitingNetworkValue);
         this.statusSnapshot = this.buildStatusSnapshot(Tone.IDLE, "Waiting", null, "Join a world to start.");
      }
   }

   public void updateMovementTarget(Vec3d vec, boolean posBlockFlag) {
      this.movementPlanner.onKnockbackReceived(vec, posBlockFlag);
   }

   public void onSendPacket(InventoryPlayer inventoryPlayer) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (inventoryPlayer != null && instance.player != null && instance.isOnThread() && !inventoryPlayer.isOpenedByClient()) {
         var packet = inventoryPlayer.packet;
         OutboundPacketKind lookChanges = OutboundPacketClassifier.classifyPacket(packet);
         boolean auditOwnFlag = this.ledgerHandswap.isHandswapPending();
         long auditOwnValue = ClientThreadUtils.getTime();
         if (lookChanges == OutboundPacketKind.TICK_END) {
            this.sprintSneak.onTickEnd();
            this.auditOwn.endTick();
         } else {
            this.sprintSneak.recordInteractPacket(lookChanges, auditOwnFlag, inventoryPlayer.isInventoryPlayerActive());
            if (lookChanges.isInteractionPacket()) {
               this.auditOwn.onInteractAction(lookChanges, auditOwnFlag, instance.currentScreen != null);
            }

            switch (lookChanges) {
               case SLOT:
                  int selectedSlot = OutboundPacketClassifier.getSelectedSlot(packet);
                  this.sprintSneak.onSlotSelected(selectedSlot, auditOwnFlag);
                  this.auditOwn.recordSlotChange(selectedSlot, auditOwnFlag);
                  break;
               case MOVE:
               case MOVE_LOOK:
                  float[] changesLook = OutboundPacketClassifier.getMoveLookAngles(packet);
                  if (changesLook != null) {
                     this.sprintSneak.recordSentLook(changesLook[0], changesLook[1]);
                  }

                  this.auditOwn
                     .onMovePacket(
                        changesLook != null, changesLook == null ? 0.0F : changesLook[0], changesLook == null ? 0.0F : changesLook[1], ClampMath.getSensitivityMouse(), this.isTrackFlickCommitTick(auditOwnValue)
                     );
                  break;
               case INPUT:
                  PlayerInput input = OutboundPacketClassifier.getInput(packet);
                  if (input instanceof PlayerInput(boolean auditOwnFlag2, boolean auditOwnFlag3, boolean auditOwnFlag4, boolean auditOwnFlag5, boolean auditOwnFlag6, boolean auditOwnFlag7, boolean auditOwnFlag8)) {
                     this.sprintSneak.onPlayerInput(auditOwnFlag2, auditOwnFlag3, auditOwnFlag4, auditOwnFlag5, auditOwnFlag6, auditOwnFlag7, auditOwnFlag8);
                  }

                  this.auditOwn.onKeyFlap(PlayerInputOverride.wasInputAppliedThisTick());
                  break;
               case TELEPORT_CONFIRM:
                  this.auditOwn.onServerCorrection();
                  break;
               case USE_ITEM:
                  float[] pitchYaw = OutboundPacketClassifier.getUseItemYawPitch(packet);
                  if (pitchYaw != null) {
                     this.auditOwn.onRotationPacket(pitchYaw[0], pitchYaw[1]);
                  }
                  break;
               case SPRINT_START:
                  this.auditOwn.checkSprintWithoutForward(instance.player.input != null && instance.player.input.hasForwardMovement());
                  this.finishingVec.getSprintTimer().onSprintToggle(true, auditOwnValue);
                  break;
               case SPRINT_STOP:
                  this.finishingVec.getSprintTimer().onSprintToggle(false, auditOwnValue);
                  break;
               case ATTACK:
                  boolean playerFlag = this.finishingVec.getSprintTimer().isSprinting() && instance.player.getAttackCooldownProgress(0.5F) > 0.9F
                     || InventoryStackUtilsEmpty.getEnchantmentsDefault(instance.player.getMainHandStack(), Enchantments.KNOCKBACK) > 0;
                  this.finishingVec.getSprintTimer().recordAttack(playerFlag, auditOwnValue);
                  if (auditOwnFlag) {
                     this.recordCrystalAttack(instance, packet, auditOwnValue);
                  }
            }
         }

         CrystalAura crystalAura = getCrystalAura();
         if (crystalAura != null) {
            crystalAura.onOutgoingPacketWhenDisabled(inventoryPlayer);
         }

         this.supplierHandle.onInventoryPlayer(inventoryPlayer);
      }
   }

   public void onReceivePacket(AutoVanish autoVanish) {
      if (autoVanish != null && autoVanish.packet != null) {
         this.cpacketEntity.onPacketReceived(autoVanish.packet);
         this.finishingVec.getVelocityCpacket().handlePackets(autoVanish.packet);
         this.supplierHandle.onAutoVanish(autoVanish);
      }
   }

   static boolean isTerminatorTargetFar(ComboTradeData comboTradeData, VecBlockingData vecBlockingData) {
      return comboTradeData != null && "Terminator".equals(comboTradeData.status()) && vecBlockingData != null && vecBlockingData.distance() > 4.5;
   }

   private void onPaused(Verdict verdict, long pausedBotValue) {
      this.resetBusyModules();
      this.pausedTickCount++;
      this.botRunning = false;
      this.engaged = false;
      this.engagedNearTarget = false;
      this.engagedForInputCheck = false;
      if (this.pausedTickCount >= 40 && this.denyCrystals.isFlag4()) {
         this.denyCrystals.updateTimedForce();
      }

      if (this.pausedTickCount == 40 && this.finishingVec.getCurrentState() != com.corz.client.world.CenterVecData.State.IDLE) {
         this.finishingVec.endEpisode("paused", false, false);
      }

      if (verdict.reason() == com.corz.client.utils.BotRunSupervisor.Reason.ARMING) {
         this.trackFlick.reset();
         this.inventorySelected.syncSelectedSlot();
         this.auditOwn.resetLastSlot();
      }

      if (verdict.reason() == com.corz.client.utils.BotRunSupervisor.Reason.OTHER_BOT || verdict.reason() == com.corz.client.utils.BotRunSupervisor.Reason.CONFLICT) {
         this.botCrystal.showThrottledNotice(verdict.reason().name() + ":" + verdict.detail(), "Crystal PvP Bot", formatModuleDetail(verdict), pausedBotValue);
      }

      this.setStatusSnapshot(this.getPausedStatus(verdict));
   }

   private static String formatOneDecimal(double waitingPlayerValue) {
      return String.format(Locale.ROOT, "%.1f", waitingPlayerValue);
   }

   private ExposureFn buildExposureFn(AbsorptionPosData absorptionPosData, VecBlock.Threat threat) {
      Vec3d liveCenter = threat.liveCenter();
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      var copyOf = this.blockPosHelper.getCopyOf();
      int centerEtaLimit = copyOf == null ? 0 : copyOf.size();
      if ((liveCenter != null || centerEtaLimit != 0) && player != null) {
         Vec3d[] vec3ds = new Vec3d[3];
         BlockPos[] blockPos = new BlockPos[3];
         float[] floats = new float[3];
         int index = 0;
         if (liveCenter != null) {
            vec3ds[index] = liveCenter;
            floats[index++] = 6.0F;
         }

         int index2 = 0;

         for (int i = 0; index2 < centerEtaLimit && i < 2; index2++) {
            HimEtaData himEtaData = (HimEtaData)copyOf.get(index2);
            if (himEtaData != null && himEtaData.isCell() && !(himEtaData.pDet() <= 0.0F)) {
               i++;
               vec3ds[index] = Vec3d.ofCenter(himEtaData.cell());
               blockPos[index] = himEtaData.cell();
               floats[index++] = 5.0F;
            }
         }

         int centerEtaLimit2 = index;
         if (centerEtaLimit2 == 0) {
            return (jumped, feet) -> 0.0F;
         } else {
            int centerEtaValue = Math.max(1, Math.min(3, Math.min(threat.etaTicks(), this.blockPosHelper.getPosHimEtaData().eta())));
            double y = Math.max(0.0, PosVecUtils.getAmountDouble(0.42, centerEtaValue));
            int difficultyInstance = BlockEntityUtils.getDifficultyInstance(player);
            Armor armor2 = absorptionPosData.armor();
            return (feet, armor) -> {
               if (feet == null) {
                  return 0.0F;
               } else {
                  Vec3d var12x = armor ? feet.add(0.0, y, 0.0) : feet;
                  Box var13x = new Box(var12x.x - 0.3, var12x.y, var12x.z - 0.3, var12x.x + 0.3, var12x.y + 1.8, var12x.z + 0.3);
                  float var14x = 0.0F;

                  for (int var15x = 0; var15x < centerEtaLimit2; var15x++) {
                     double var16x = var12x.distanceTo(vec3ds[var15x]);
                     if (!(var16x > floats[var15x] * 2.0)) {
                        this.sharedTiming.tryReserveRays(12, 1);
                        float var18x = BoxVisibilityRaycaster.computeVisibleFraction(vec3ds[var15x], var13x, player, null, blockPos[var15x]);
                        float var19x = (float)BoxLengthUtils.getAmount3(var16x, var18x, floats[var15x]);
                        var14x = Math.max(var14x, PosBlockUtilsBonus.getToughnessEpf(PosBlockUtilsBonus.getAmount(var19x, difficultyInstance), armor2));
                     }
                  }

                  return var14x;
               }
            };
         }
      } else {
         return (jumped, feet) -> 0.0F;
      }
   }

   private void resetSession(long sessionPosValue) {
      this.finishFight("session", false, false, sessionPosValue);
      this.finishingVec.endEpisode("session", false, false);
      this.denyCrystals.updateTimedForce();
      this.supplierHandle.runSupplierAction();
      PlayerInputOverride.cancelRequestFrom(this.crystalPvpBot);
      this.sprintSneak.reset();
      this.trackFlick.reset();
      this.pitchYaw.deactivate();
      this.inventorySelected.clearRequest();
      this.budget.updateIdleTime();
      this.obsidianIdle.resetAll();
      this.cooldownPlayer.reset();
      this.cooldownEscape.updateLandingCooldown();
      this.mendingRepaired.resetMendingState();
      this.defuseHelper.reset();
      this.campBreaching.reset();
      this.blockPosHelper.updateFlooredTime();
      this.stackPlayer.reset();
      this.livingEntityHelper.updateMinWorld();
      this.vecBlock.updateAmountMin();
      this.blockingVec.reset();
      this.unplacedAnti.resetState();
      this.terminatorPos.resetState();
      this.idealPosHelper.reset();
      this.movementPlanner.resetState();
      this.maxPlayer.reset();
      this.timesMax.reset();
      this.finishingVec.resetAll();
      this.worldTimeTracker.reset();
      this.clearAnchorThreatState();
      this.lastComboTradeData = null;
      this.lastTickStatus = TickStatusData.INSTANCE;
      this.noTargetTickCount = 0;
      this.quietTickCount = 0;
      this.ticksSinceOwnPop = 1000000;
      this.lowWeightStreak = 0;
      this.weightsTargetId = -1;
      this.weightsPrevThreatLevel = -1;
      this.lastWeightsTick = Long.MIN_VALUE;
      this.lastTotalHealth = Float.NaN;
      this.calibratedTargetId = -1;
      this.lastModelEhp = Float.NaN;
      this.lastTargetTotalHealth = Float.NaN;
      this.crystalHitTicks.clear();
      this.ownPearlIds.clear();
      this.trackedBusyLane = null;
      this.busyLaneSinceTick = Long.MIN_VALUE;
      this.engaged = false;
      this.engagedNearTarget = false;
      this.engagedForInputCheck = false;
   }

   private long getP95TickMicros() {
      if (this.tickTimeSamples == 0) {
         return 0L;
      } else {
         long[] copyOf = Arrays.copyOf(this.tickTimeMicros, this.tickTimeSamples);
         Arrays.sort(copyOf);
         int waitingPlayerBound = (int)Math.ceil(0.95 * this.tickTimeSamples) - 1;
         return copyOf[Math.max(0, Math.min(this.tickTimeSamples - 1, waitingPlayerBound))];
      }
   }

   private static String truncateText(String formatText, int end) {
      if (formatText != null && formatText.length() > end) {
         return end <= 3 ? formatText.substring(0, end) : formatText.substring(0, end - 3) + "...";
      } else {
         return formatText;
      }
   }

   private void tickBot(MinecraftClient client, long entityBudgetValue, long playerValue) {
      ClientPlayerEntity player = client.player;
      int id = player.getId();
      this.sprintSneak.onClientTime(entityBudgetValue);
      this.userInputTracker.finishTick(playerValue, isAnyTrackedKeyPressed(client));
      this.ownPopThisTick = false;
      this.targetPoppedThisTick = false;
      this.hitTargetThisTick = false;
      if (this.ticksSinceOwnPop < 1000000) {
         this.ticksSinceOwnPop++;
      }

      BlockPos blockPos = this.budget.isNether() ? this.budget.getNetherBlockPos() : null;
      if (blockPos != null) {
         this.blockPosHelper.handleTimeFlag(blockPos, entityBudgetValue);
      }

      for (BlockPos getFiredPosItem : this.budget.getFiredPos(player.getEyePos(), this.livingEntityHelper.getUnmodifiableList())) {
         this.blockPosHelper.handleTimeFlag(getFiredPosItem, entityBudgetValue);
      }

      this.cpacketEntity.drainEvents(client.world, nowx -> this.onPacketEvent(nowx, entityBudgetValue, playerValue, id), 512);
      this.finishingVec.onVelocityPacket(entityBudgetValue);
      if (this.dirJson != null) {
         this.dirJson.applyLoadedProfiles();
      }

      if (this.networkHandler.isSessionRunning()) {
         this.supplierHandle.runTotemTickWithHandSwap(this.anchorChargeThreat);
      }

      Verdict setbackTime = this.networkHandler
         .evaluateProbe(BotProbeFactory.buildBotProbe(playerValue, entityBudgetValue, this.worldTimeTracker, this.userInputTracker, this.botCrystal, this.trackFlick));
      if (setbackTime.level() == Level.STOP) {
         this.resetBusyModules();
         this.disableBotWithNotice(setbackTime);
      } else {
         boolean entityBudgetFlag = setbackTime.reason() == com.corz.client.utils.BotRunSupervisor.Reason.DEAD || setbackTime.reason() == com.corz.client.utils.BotRunSupervisor.Reason.RESPAWN && setbackTime.resetState();
         if (entityBudgetFlag && !this.deathHandled) {
            this.onOwnDeath(playerValue, entityBudgetValue);
         }

         if (setbackTime.reason() != com.corz.client.utils.BotRunSupervisor.Reason.DEAD && setbackTime.reason() != com.corz.client.utils.BotRunSupervisor.Reason.RESPAWN) {
            this.deathHandled = false;
         }

         if (setbackTime.resetState()) {
            this.resetSession(entityBudgetValue);
            this.inventorySelected.syncSelectedSlot();
            this.auditOwn.resetLastSlot();
         }

         if (setbackTime.level() == Level.PAUSED) {
            this.onPaused(setbackTime, playerValue);
         } else {
            this.pausedTickCount = 0;
            this.botRunning = setbackTime.level() == Level.RUN;
            this.trackFlick.beginTick(entityBudgetValue, MathClampUtils.getYaw(), MathClampUtils.getClamp(), ClampMath.getSensitivityMouse());
            boolean lookRotationFlag = this.sprintSneak.hasSentLookPacket()
                  && !BoxLengthUtils.canFlag(this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), this.sprintSneak.getLastSentYaw(), this.sprintSneak.getLastSentPitch())
               || !BoxLengthUtils.canFlag(this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), player.getYaw(), player.getPitch());
            this.ledgerHandswap.reset(setbackTime.level(), lookRotationFlag);
            this.ledgerHandswap.setTeleportLookFlag(this.hasFreshForecast(entityBudgetValue, id));
            this.sharedTiming.resetFrame();
            this.entityShield.clearStatusReason();
            PearlsPlaystyleData baseEnum = this.crystalPvpBot.getPlaystyleConfig();
            CarefulBalancedType carefulBalancedType = baseEnum.playstyle() == null ? CarefulBalancedType.TERMINATOR : baseEnum.playstyle();
            this.trackFlick.setSpeedScale(baseEnum.turnScale());
            this.entityDistance.setDeathTrackingEnabled(baseEnum.protectLoot());
            if (this.dirJson != null) {
               this.dirJson.setPersistenceEnabled(baseEnum.learnOpponents());
            }

            AbsorptionPosData absorptionEmpty = this.stackPlayer.buildSnapshot(entityBudgetValue);
            if (absorptionEmpty != null) {
               double groundY = baseEnum.fightRange() + PlaystyleTuning.COUNTER_RANGE_BONUS_BY_STYLE[carefulBalancedType.ordinal()];
               VecBlockingData next = this.blockingVec.getCurrentTarget();
               Options options = Options.INSTANCE2;
               this.livingEntityHelper.handleEntityTime(entityBudgetValue, absorptionEmpty, options, baseEnum.fightRange(), next == null ? -1 : next.id(), groundY);
               VecBlockingData distanceInstance = this.blockingVec
                  .selectTarget(entityBudgetValue, this.livingEntityHelper.getUnmodifiableList(), absorptionEmpty, this.vecBlock.getMaxMinData(), groundY);
               this.updateTargetSwitch(distanceInstance, entityBudgetValue, playerValue, baseEnum);
               ExtUtils.handleFlag(distanceInstance != null && distanceInstance.distance() <= 6.0);
               double playerBound = EntityDamageUtils.getTime();
               this.targetThreatLevel = distanceInstance == null ? 0 : PendingEntityHitTracker.getRemainingTicksCeilWithMargin(EntityDamageUtils.getMin(distanceInstance.id()), playerBound, this.maxPlayer.getEscalationLevel());
               this.ownThreatLevel = PendingEntityHitTracker.getRemainingTicksFloor(EntityDamageUtils.getMin(id), playerBound);
               this.maxPlayer.onPendingTimeout(entityBudgetValue, distanceInstance != null);
               this.blockPosHelper.handleFlooredWorld(entityBudgetValue, absorptionEmpty, this.livingEntityHelper.getUnmodifiableList(), distanceInstance, this.targetThreatLevel, this.ownThreatLevel);
               VecBlock.Threat maxMin = this.vecBlock.getMaxMin(entityBudgetValue, absorptionEmpty, this.livingEntityHelper.getUnmodifiableList(), this.ownThreatLevel);
               if (maxMin == null) {
                  maxMin = VecBlock.Threat.INSTANCE;
               }

               if (distanceInstance == null) {
                  this.noTargetTickCount++;
               } else {
                  this.noTargetTickCount = 0;
               }

               if (distanceInstance != null && !this.denyCrystals.isFlag4() && this.denyCrystals.isFlagWeaknessWorld()) {
                  this.inventorySelected.resetRestoreRequest();
                  this.trackFlick.reset();
                  this.trackFlick.beginTick(entityBudgetValue, MathClampUtils.getYaw(), MathClampUtils.getClamp(), ClampMath.getSensitivityMouse());
               }

               if (this.noTargetTickCount >= 40 && this.denyCrystals.isFlag4()) {
                  this.denyCrystals.updateTimedForce();
               }

               this.engaged = distanceInstance != null && distanceInstance.distance() <= groundY + 4.0 || this.isAnyLaneBusy();
               this.engagedNearTarget = this.engaged && distanceInstance != null && distanceInstance.distance() <= 12.0;
               this.engagedForInputCheck = this.engaged;
               ComboTradeData comboTradeData = this.lastComboTradeData;
               this.clientInstance
                  .handleTimeInstance(
                     entityBudgetValue,
                     playerValue,
                     comboTradeData == null ? getEffectiveHealth(absorptionEmpty) : comboTradeData.maxSelfDamage(),
                     comboTradeData != null && comboTradeData.allowTotemTrade(),
                     comboTradeData == null ? null : comboTradeData.trade()
                  );
               boolean budgetAnchorFlag = !BlockPosUtilsRespawn.isRegistryWorld();
               SurroundKitType cooldownBreaching = this.getBusyLane();
               SurroundKitType surroundKitType = comboTradeData == null ? SurroundKitType.NONE : comboTradeData.lane();
               int entityBudgetCounter = 0;

               for (VecBlockingData vecBlockingData : this.livingEntityHelper.getUnmodifiableList()) {
                  if (vecBlockingData.distance() <= 8.0) {
                     entityBudgetCounter++;
                  }
               }

               boolean nearLoot = this.entityDistance.isNearLoot(absorptionEmpty.pos());
               double min5 = this.livingEntityHelper.getMin();
               boolean vecSquared = this.finishingVec.getPearlTracker().canVecSquared(absorptionEmpty.pos(), 20.0, id);
               DefensiveAggressiveType defensiveAggressiveType = this.unplacedAnti.getDefensiveAggressiveType();
               var enumMap = new EnumMap<SurroundKitType, CaseMaxData>(SurroundKitType.class);
               SelfPopData selfPopData = new SelfPopData(
                  entityBudgetValue,
                  absorptionEmpty,
                  distanceInstance,
                  entityBudgetCounter,
                  maxMin,
                  this.targetThreatLevel,
                  this.ownThreatLevel,
                  budgetAnchorFlag,
                  baseEnum,
                  enumMap,
                  cooldownBreaching,
                  surroundKitType,
                  this.ticksSinceOwnPop,
                  this.lowWeightStreak,
                  nearLoot,
                  min5,
                  vecSquared,
                  defensiveAggressiveType,
                  CenterVecData.INSTANCE,
                  this.timesMax.getRecentDamageSum(entityBudgetValue),
                  this.blockPosHelper.getPosHimEtaData(),
                  CenterVecData.INSTANCE,
                  this.timesMax.isWithinWindow(entityBudgetValue)
               );
               TargetSelfDataTarget flagCfg = this.denyCrystals.getFlagCfg(selfPopData, comboTradeData, this.distancePredicate);
               enumMap.put(SurroundKitType.CRYSTAL, this.denyCrystals.getTuningKind(selfPopData, flagCfg));
               enumMap.put(SurroundKitType.ANCHOR, this.getAnchorOption(selfPopData, baseEnum, budgetAnchorFlag));
               enumMap.put(SurroundKitType.MELEE, this.entityShield.evaluateMelee(selfPopData));
               enumMap.put(SurroundKitType.SURROUND, this.obsidianIdle.getSurroundCase(selfPopData));
               enumMap.put(SurroundKitType.GUARD, this.defuseHelper.getGuardAction(selfPopData));
               enumMap.put(SurroundKitType.BREACH, this.campBreaching.planBreach(selfPopData, comboTradeData));
               enumMap.put(SurroundKitType.EAT, this.cooldownPlayer.getEatOption(selfPopData));
               enumMap.put(SurroundKitType.PEARL, this.cooldownEscape.getPearlLanding(selfPopData));
               enumMap.put(SurroundKitType.XP, this.mendingRepaired.evaluateMendingOption(selfPopData));
               enumMap.put(SurroundKitType.KIT, this.stackInventory.getKitStatus(selfPopData));
               this.updateLowWeightStreak(entityBudgetValue, distanceInstance, enumMap, comboTradeData, carefulBalancedType);
               this.finishingVec.setAggroActive(setbackTime.level() == Level.ACTIONS_PAUSED);
               CenterVecData counterVec = this.finishingVec
                  .computeDecision(selfPopData, this.livingEntityHelper.getUnmodifiableList(), this.vecBlock, this.lastTickStatus, entityBudgetValue, this.distancePredicate);
               this.defuseHelper.setFallbackCombo(counterVec);
               if (distanceInstance != null && counterVec != null && counterVec.state() != com.corz.client.world.CenterVecData.State.IDLE) {
                  this.blockingVec.updateLatestTick(entityBudgetValue + 20L);
               }

               SelfPopData selfPop = selfPopData.getSelfPop(counterVec).getSelfPopForCenterVecData(this.campBreaching.getBreachDecision());
               ComboTradeData anchorAnti = this.unplacedAnti.planCombo(selfPop);
               SurroundKitType surroundKitType2 = anchorAnti != null && anchorAnti.lane() != null ? anchorAnti.lane() : SurroundKitType.NONE;
               if (carefulBalancedType == CarefulBalancedType.TERMINATOR
                  && distanceInstance != null
                  && !TerminatorCrystalPlanner.isTerminatorAllowedForCombo(anchorAnti, CombatComboPlanner.canGuardAvailable(selfPop), this.campBreaching.isBreachActionActive())) {
                  ComboTradeData kitTerminator = this.terminatorPos.planTerminatorCombo(selfPop, anchorAnti, this.buildTerminatorInput(selfPop, counterVec, flagCfg, player, entityBudgetValue));
                  if (kitTerminator != null) {
                     anchorAnti = kitTerminator;
                  }
               }

               this.finishingVec.applyTradeOverride(anchorAnti, entityBudgetValue);
               this.clientInstance.handleTimeInstance(entityBudgetValue, playerValue, anchorAnti.maxSelfDamage(), anchorAnti.allowTotemTrade(), anchorAnti.trade());
               boolean sprintSneakFlag = false;

               TickStatusData tickStatusData;
               try (SwitchHandle handler = this.ledgerHandswap.acquireHandle()) {
                  this.cancelInactiveLanes(selfPop, anchorAnti);
                  CenterVecData combo = anchorAnti.combo();
                  boolean entityBudgetFlag6 = combo != null && !anchorAnti.comboYield();
                  CrystalStep crystalStep = entityBudgetFlag6 ? combo.crystal() : null;
                  boolean posBlockFlag = anchorAnti.lane() == SurroundKitType.CRYSTAL;
                  ForceTimedType forceTimedType = crystalStep == null
                     ? anchorAnti.breaks()
                     : (crystalStep.breakNow() ? ForceTimedType.FORCE : (crystalStep.hold() ? ForceTimedType.HOLD : anchorAnti.breaks()));
                  TargetSelfDataTarget livingVecData = this.denyCrystals.getFlagCfg(selfPop, anchorAnti, this.distancePredicate);
                  tickStatusData = this.denyCrystals.isFlag4() && !this.campBreaching.isDigHeld()
                     ? this.denyCrystals.getPosBlock(livingVecData, posBlockFlag, forceTimedType, distanceInstance, this.targetThreatLevel)
                     : this.denyCrystals.getPosBlockTickStatusData();
                  switch (anchorAnti.lane()) {
                     case ANCHOR:
                        if (entityBudgetFlag6 && combo.anchor() != null) {
                           if (this.budget.canNether(combo.anchor().cell())) {
                              this.budget.updateNetherGone();
                           }

                           if (!this.budget.isNether()) {
                              this.budget.canHitIdle(selfPop, combo.anchor());
                           }

                           this.budget.handleNetherDone(combo.anchor().release());
                           break;
                        }

                        if (!this.budget.isNether()) {
                           this.budget.canHitStart(selfPop);
                        }

                        this.budget.handleNetherDone(anchorAnti.anchorRelease());
                        break;
                     case MELEE:
                        if (distanceInstance == null) {
                           break;
                        }

                        if (entityBudgetFlag6 && combo.strike() != null && combo.strike().tempo()) {
                           sprintSneakFlag = this.entityShield.tryAttackWithStrike(distanceInstance, combo.strike(), this.targetThreatLevel);
                        } else {
                           if ((!entityBudgetFlag6 || combo.strike() == null) && carefulBalancedType != CarefulBalancedType.CAREFUL) {
                              sprintSneakFlag = this.entityShield.tryAttackWithStrike(distanceInstance, this.buildMeleeStrike(distanceInstance, player), this.targetThreatLevel);
                              break;
                           }

                           sprintSneakFlag = entityBudgetFlag6 && combo.strike() != null
                              ? this.entityShield.tryAttackWithYawLimit(distanceInstance, combo.strike(), this.finishingVec.getSprintTimer(), this.targetThreatLevel)
                              : this.entityShield.tryAttackEntity(distanceInstance.entity(), this.movementPlanner.isCritJumpActive());
                        }
                        break;
                     case SURROUND:
                        if (!this.obsidianIdle.isSurroundActive()) {
                           this.obsidianIdle.tryStartSurround(selfPop);
                        }

                        this.obsidianIdle.tickSurround();
                        break;
                     case EAT:
                        this.updateEatLane(selfPop, anchorAnti, absorptionEmpty, carefulBalancedType);
                        break;
                     case PEARL:
                        if (entityBudgetFlag6 && combo.pearl() != null) {
                           PearlShot pearl = combo.pearl();
                           this.cooldownEscape
                              .handleCooldownAim(new Plan(pearl.yaw(), pearl.pitch(), pearl.landing(), pearl.why()), pearl.validUntil(), pearl.acceptRadius());
                        }

                        this.cooldownEscape.getCooldownAim();
                        break;
                     case XP:
                        this.mendingRepaired.tryThrowXpBottle();
                        break;
                     case KIT:
                        this.stackInventory.tryScheduleAutoSwap(anchorAnti.restockWindow());
                        break;
                     case GUARD:
                        this.defuseHelper.tickBlockAction(selfPop);
                        break;
                     case BREACH:
                        this.campBreaching.runDigStep(selfPop);
                  }

                  if (sprintSneakFlag && distanceInstance != null) {
                     this.recordShieldAttack(distanceInstance, entityBudgetValue);
                  }

                  BlockPos posBlock = this.budget.getPosBlock();
                  if (posBlock != null) {
                     this.livingEntityHelper.handleDequeArray(Vec3d.ofCenter(posBlock), entityBudgetValue);
                  }

                  if (posBlock != null && distanceInstance != null && distanceInstance.entity() != null && this.targetThreatLevel > 0) {
                     this.livingEntityHelper.handleAmount(distanceInstance.id(), entityBudgetValue, EntityCenterUtils.getBoxBounding(distanceInstance.entity(), null, posBlock, null));
                  }

                  boolean entityBudgetFlag8 = this.obsidianIdle.isSurroundActive() || this.budget.isNether() || this.campBreaching.isBreachActionActive();
                  HumanAimRotator.setReducedStepLimit(this.obsidianIdle.isSurroundActive() || this.budget.isNetherCharge() || this.campBreaching.isBreachActionActive());
                  Vec3d vec3d = !this.trackFlick.hasPendingRequest() && !this.sprintSneak.hasInteractDuringHandswap() ? this.budget.getVecPos(selfPop, anchorAnti, entityBudgetFlag6 ? combo : null) : null;
                  if (vec3d != null) {
                     HumanAimRotator.setReducedStepLimit(true);
                     float[] wrapDegrees = MathClampUtils.getWrapDegrees(player.getEyePos(), vec3d);
                     this.trackFlick.requestSmoothedRotation(BotActionType.ANCHOR, wrapDegrees[0], wrapDegrees[1], Speed.ACTION, 35);
                  }

                  if (entityBudgetFlag6 && !this.trackFlick.hasPendingRequest() && isComboAimAvailable(combo)) {
                     float[] wrapDegrees2 = MathClampUtils.getWrapDegrees(player.getEyePos(), combo.aimPoint());
                     this.trackFlick.requestSmoothedRotation(BotActionType.TRACK, wrapDegrees2[0], wrapDegrees2[1], Speed.ACTION, 20);
                  }

                  if (distanceInstance != null && !this.trackFlick.hasPendingRequest() && !entityBudgetFlag8) {
                     this.entityShield.preAimAtTarget(distanceInstance, this.targetThreatLevel);
                  }

                  if (entityBudgetFlag6 && !this.trackFlick.hasPendingRequest() && combo.aimPoint() != null) {
                     float[] wrapDegrees3 = MathClampUtils.getWrapDegrees(player.getEyePos(), combo.aimPoint());
                     this.trackFlick.requestSmoothedRotation(BotActionType.TRACK, wrapDegrees3[0], wrapDegrees3[1], Speed.ACTION, 20);
                  }

                  if (!this.trackFlick.hasPendingRequest() && distanceInstance != null) {
                     Vec3d vec3d2 = distanceInstance.serverPos() != null ? distanceInstance.serverPos() : distanceInstance.pos();
                     Vec3d vec3d3 = distanceInstance.velocity() == null ? Vec3d.ZERO : distanceInstance.velocity();
                     Vec3d add = vec3d2.add(0.0, 0.45, 0.0).add(vec3d3.multiply(2.0));
                     float[] wrapDegrees4 = MathClampUtils.getWrapDegrees(player.getEyePos(), add);
                     this.trackFlick.requestSmoothedRotation(BotActionType.TRACK, wrapDegrees4[0], wrapDegrees4[1], Speed.TRACK, 10);
                  }

                  this.recordKitRestockTick(entityBudgetValue, anchorAnti, distanceInstance, enumMap);
               }

               this.lastTickStatus = tickStatusData == null ? TickStatusData.INSTANCE : tickStatusData;
               this.quietTickCount = this.isAnyLaneBusy() ? 0 : Math.min(this.quietTickCount + 1, 1000000);
               if (this.noTargetTickCount >= 40 && this.quietTickCount >= 40) {
                  this.inventorySelected.rememberCurrentSlot();
               }

               this.inventorySelected.flushPendingSlotRequest(entityBudgetValue);
               if (!this.engaged && this.isAnyLaneBusy()) {
                  this.engaged = true;
                  this.engagedForInputCheck = true;
               }

               this.planMovement(entityBudgetValue, selfPop, anchorAnti, absorptionEmpty, distanceInstance, maxMin, setbackTime, carefulBalancedType, player);
               Commit pitchYaw = this.applyRotationCommit(
                  player,
                  entityBudgetValue,
                  this.trackFlick
                     .resolveCommit(this.sprintSneak.hasInteractDuringHandswap() || this.sprintSneak.hasForcedInteract() || this.trackFlick.hasPendingRequest() && this.sprintSneak.hasInteractThisTick())
               );
               this.finalizeTick(client, entityBudgetValue, playerValue, selfPop, anchorAnti, surroundKitType2, absorptionEmpty, distanceInstance, sprintSneakFlag, setbackTime, pitchYaw, player);
            }
         }
      }
   }

   public void onQueueCommand(QueueCommand queueCommand) {
      if (queueCommand != null && this.engaged) {
         CrystalAura crystalAura = getCrystalAura();
         if (crystalAura != null) {
            crystalAura.renderWhenControlled(queueCommand);
         }
      }
   }

   private static CrystalAura getCrystalAura() {
      return ModuleRegistry.getModule(CrystalAura.class) instanceof CrystalAura crystalAura ? crystalAura : null;
   }

   static boolean isSafeToAct(boolean waitingPlayerFlag, int waitingPlayerLimit, double waitingPlayerLimit2, int waitingPlayerLimit3, int waitingPlayerLimit4) {
      if (waitingPlayerFlag) {
         return false;
      } else if (waitingPlayerLimit >= 2 && waitingPlayerLimit <= 8) {
         return true;
      } else {
         return waitingPlayerLimit2 >= 6.0 ? true : waitingPlayerLimit3 >= 6 && waitingPlayerLimit4 >= 6;
      }
   }

   private Input buildTerminatorInput(SelfPopData selfPopData, CenterVecData centerVecData, TargetSelfDataTarget targetSelfDataTarget, ClientPlayerEntity clientPlayerEntity, long budgetPosValue) {
      CrystalAura crystalAura = getCrystalAura();
      Request request = crystalAura != null && targetSelfDataTarget != null && this.denyCrystals.isFlag4() ? crystalAura.buildPredictionRequest(targetSelfDataTarget) : null;
      VecBlockingData target = selfPopData.target();
      AmountConstTime amountConstTime = target == null ? null : this.livingEntityHelper.getClassForInt(target.id());
      boolean budgetPosFlag = this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.END_CRYSTAL), false) >= 0;
      BlockPos nether = this.budget.getNetherBlockPos();
      boolean budgetPosFlag2 = this.budget.isNether() && nether != null && BlockPosUtilsRespawn.getChargesRespawn(nether) < 0;
      return new Input(
         request,
         amountConstTime == null ? null : amountConstTime.getTimeAmount(budgetPosValue),
         centerVecData,
         this.lastTickStatus,
         this.budget.isNether(),
         this.cooldownEscape.isAimCooldown(),
         budgetPosFlag,
         clientPlayerEntity.getEyePos(),
         this.trackFlick.getCurrentYaw(),
         this.trackFlick.getCurrentPitch(),
         this.finishingVec.getHitLoopApproachStand(budgetPosValue),
         budgetPosFlag2
      );
   }

   private void resetAll() {
      this.finishFight("reset", false, false, ClientThreadUtils.getTime());
      this.finishingVec.endEpisode("reset", false, false);
      this.resetBusyModules();
      this.sprintSneak.reset();
      this.trackFlick.reset();
      this.inventorySelected.resetAll();
      this.budget.updateIdleTime();
      this.obsidianIdle.resetAll();
      this.cooldownPlayer.reset();
      this.cooldownEscape.updateLandingCooldown();
      this.mendingRepaired.resetMendingState();
      this.defuseHelper.reset();
      this.campBreaching.reset();
      this.blockPosHelper.updateFlooredTime();
      this.stackPlayer.reset();
      this.livingEntityHelper.updateMinWorld();
      this.vecBlock.updateAmountMin();
      this.blockingVec.reset();
      this.unplacedAnti.resetState();
      this.terminatorPos.resetState();
      this.idealPosHelper.reset();
      this.movementPlanner.resetState();
      this.maxPlayer.reset();
      this.timesMax.reset();
      this.finishingVec.resetAll();
      this.worldTimeTracker.reset();
      this.clearAnchorThreatState();
      this.cpacketEntity.clearPendingEvents();
      this.lastComboTradeData = null;
      this.lastTickStatus = TickStatusData.INSTANCE;
      this.noTargetTickCount = 0;
      this.pausedTickCount = 0;
      this.quietTickCount = 0;
      this.ticksSinceOwnPop = 1000000;
      this.deathHandled = false;
      this.lowWeightStreak = 0;
      this.weightsTargetId = -1;
      this.weightsPrevThreatLevel = -1;
      this.lastWeightsTick = Long.MIN_VALUE;
      this.lastTotalHealth = Float.NaN;
      this.calibratedTargetId = -1;
      this.lastModelEhp = Float.NaN;
      this.lastTargetTotalHealth = Float.NaN;
      this.crystalHitTicks.clear();
      this.ownPearlIds.clear();
      this.lastAuditValue = this.auditOwn.getOwnViolationTotal();
      this.eatbadEatminCount = this.finishingVec.getEatabEatbad().getEatminHitCount();
      this.lastCrystalObsidianTick = Long.MIN_VALUE;
      this.trackedBusyLane = null;
      this.busyLaneSinceTick = Long.MIN_VALUE;
      this.engaged = false;
      this.engagedNearTarget = false;
      this.engagedForInputCheck = false;
      this.botRunning = false;
      this.winBannerUntilMs = Long.MIN_VALUE;
   }

   private Snapshot getFightingStatus(Verdict verdict, ComboTradeData comboTradeData, VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, long fightingRefillingValue) {
      if (verdict.level() != Level.RUN) {
         return this.getPausedStatus(verdict);
      } else {
         String fightingRefillingText = vecBlockingData == null ? null : applyNameProtect(vecBlockingData.name());
         if (absorptionPosData.totems() <= 0) {
            return this.buildStatusSnapshot(Tone.DANGER, "No totems", fightingRefillingText, "Put totems in your inventory.");
         } else if (absorptionPosData.crystals() <= 0 && absorptionPosData.obsidian() <= 0) {
            return this.buildStatusSnapshot(Tone.DANGER, "No crystals", fightingRefillingText, "Needs end crystals and obsidian.");
         } else if (absorptionPosData.getAbsorptionHealth() < 8.0F && !absorptionPosData.totemConfirmed()) {
            return this.buildStatusSnapshot(Tone.DANGER, "Low health", fightingRefillingText, null);
         } else {
            DefendEngageType defendEngageType = comboTradeData == null ? DefendEngageType.IDLE : comboTradeData.mode();
            if (vecBlockingData == null) {
               if (defendEngageType == DefendEngageType.HEAL) {
                  return this.buildStatusSnapshot(Tone.ACTIVE, "Eating", null, null);
               } else if (defendEngageType == DefendEngageType.REFILL) {
                  return this.buildStatusSnapshot(Tone.ACTIVE, "Refilling", null, null);
               } else if (defendEngageType == DefendEngageType.MEND) {
                  return this.buildStatusSnapshot(Tone.ACTIVE, "Mending", null, null);
               } else {
                  return this.winBannerUntilMs != Long.MIN_VALUE && fightingRefillingValue < this.winBannerUntilMs
                     ? this.buildStatusSnapshot(Tone.ACTIVE, "Won", this.lastKillName, null)
                     : this.buildStatusSnapshot(Tone.ACTIVE, "Searching", null, "Looking for a player nearby.");
               }
            } else {
               String menuTurnText = switch (defendEngageType) {
                  case HEAL -> "Eating";
                  case REFILL -> "Refilling";
                  case MEND -> "Mending";
                  case COMBO, BREACH, CAMP -> comboTradeData.status() != null && !comboTradeData.status().isBlank() ? comboTradeData.status() : "Fighting";
                  case DEFEND -> !"Backing off".equals(comboTradeData.status()) && comboTradeData.lane() != SurroundKitType.GUARD ? "Fighting" : comboTradeData.status();
                  default -> "Fighting";
               };
               return this.buildStatusSnapshot(Tone.ACTIVE, menuTurnText, fightingRefillingText, null);
            }
         }
      }
   }

   private void clearAnchorThreatState() {
      this.anchorChargeThreat = false;
      this.anchorEtaData = HimEtaData.INSTANCE;
      this.etaTicks = 99;
      this.nearestEnemy = Double.POSITIVE_INFINITY;
      this.anchorUnplacedCount = 0;
   }

   private float getSelfBreakDamage(TickStatusData tickStatusData, long budgetBreakValue, ClientPlayerEntity entity) {
      float budgetBreak = 0.0F;
      BlockPos posBlock = this.budget.getPosBlock();
      if (posBlock != null) {
         budgetBreak = PosBlockUtilsBonus.getToughnessEpf(EntityCenterUtils.getBoxBounding(posBlock, null, null), BlockEntityUtils.getAttributeArmor(entity));
      }

      CrystalAura crystalAura = tickStatusData.lastBreakTick() == budgetBreakValue ? getCrystalAura() : null;
      BlockPos blockPos = crystalAura == null ? null : crystalAura.getLastBreakBase();
      if (blockPos != null) {
         budgetBreak = Math.max(budgetBreak, BlockEntityUtils.getAttributeArmor(BlockEntityUtils.getVec3d(blockPos), null));
      }

      return budgetBreak;
   }

   public boolean isEngagedWithoutRecentInput() {
      return this.engagedForInputCheck && !this.userInputTracker.isUserOverriding(ConnectionTimeUtils.getTimeTimeMillis());
   }

   private void recordCrystalAttack(MinecraftClient client, Packet<?> packet, long entityDequeValue) {
      if (packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke && client.world != null) {
         if (client.world.getEntityById(PlayerCrystalUtils.getEntityId(playerInteractEntityC2SPacke)) instanceof EndCrystalEntity endCrystalEntity) {
            this.livingEntityHelper.handleDequeArray(endCrystalEntity.getEntityPos(), entityDequeValue);
            this.crystalHitTicks.addLast(entityDequeValue);

            while (this.crystalHitTicks.size() > 64) {
               this.crystalHitTicks.removeFirst();
            }
         }
      }
   }

   private void expireStuckLane(long time) {
      SurroundKitType surroundKitType = this.getBusyLane();
      if (surroundKitType != this.trackedBusyLane) {
         this.trackedBusyLane = surroundKitType;
         this.busyLaneSinceTick = time;
      } else if (surroundKitType != null && time - this.busyLaneSinceTick >= (surroundKitType == SurroundKitType.BREACH ? 800 : 200)) {
         switch (surroundKitType) {
            case ANCHOR:
               this.budget.updateNetherGone();
            case MELEE:
            case XP:
            case KIT:
            default:
               break;
            case SURROUND:
               this.obsidianIdle.resetSurround();
               break;
            case EAT:
               this.cooldownPlayer.cancelEating();
               break;
            case PEARL:
               this.cooldownEscape.updateCooldownThrown();
               break;
            case GUARD:
               this.defuseHelper.finishDefuse();
               break;
            case BREACH:
               this.campBreaching.cancelMining();
               this.campBreaching.reset();
         }

         this.trackedBusyLane = null;
         this.busyLaneSinceTick = time;
      }
   }

   private Commit applyRotationCommit(ClientPlayerEntity clientPlayerEntity, long pitchYawValue, Commit pitchYaw) {
      float yaw = clientPlayerEntity.getYaw();
      float pitch = clientPlayerEntity.getPitch();
      if (pitchYaw != null) {
         MathClampUtils.handle(this.crystalPvpBot, pitchYaw.yaw(), pitchYaw.pitch(), 95, false);
      }

      if (pitchYaw != null && !pitchYaw.hold()) {
         this.pitchYaw.setSegment(pitchYawValue, yaw, pitch, pitchYaw.yaw(), pitchYaw.pitch());
      } else {
         this.pitchYaw.deactivate();
      }

      return pitchYaw;
   }

   private static CarefulBalancedType getPlaystyle(PearlsPlaystyleData pearlsPlaystyleData) {
      return pearlsPlaystyleData != null && pearlsPlaystyleData.playstyle() != null ? pearlsPlaystyleData.playstyle() : CarefulBalancedType.TERMINATOR;
   }

   private Strike buildMeleeStrike(VecBlockingData vecBlockingData, ClientPlayerEntity clientPlayerEntity) {
      LivingEntity entity = vecBlockingData.entity();
      Vec3d eyePos = clientPlayerEntity.getEyePos();
      Vec3d vec3d = entity != null ? entity.getBoundingBox().getCenter() : (vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos()).add(0.0, 0.9, 0.0);
      double deltaX = vec3d.x - eyePos.x;
      double deltaZ = vec3d.z - eyePos.z;
      double hypot = Math.hypot(deltaX, deltaZ);
      return new Strike(
         this.entityShield.findBestWeaponSlot(entity),
         false,
         VecShieldUtilsAmounts.getAmount(deltaX, deltaZ),
         (float)VecShieldUtilsAmounts.getDegrees(hypot),
         false,
         true,
         null
      );
   }

   private static boolean isAnyTrackedKeyPressed(MinecraftClient client) {
      if (client.currentScreen == null && client.options != null) {
         for (KeyBinding keyBinding : getTrackedKeyBindings(client.options)) {
            if (CodeCategoryUtils.isKeyPressed(keyBinding)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
