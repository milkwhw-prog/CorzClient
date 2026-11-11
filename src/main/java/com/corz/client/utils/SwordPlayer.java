package com.corz.client.utils;

import com.corz.client.utils.ServerPacketEventQueue.Event;
import com.corz.client.utils.ServerPacketEventQueue.EntityDamage;
import com.corz.client.utils.ServerPacketEventQueue.Pop;
import com.corz.client.utils.ServerPacketEventQueue.Swing;
import com.corz.client.utils.AStarPathfinder.Danger;
import com.corz.client.utils.PlayerInputOverride.Intent;
import com.corz.client.utils.HandswapGate.SwitchHandle;
import com.corz.client.utils.BotRunSupervisor.Level;
import com.corz.client.utils.BotRunSupervisor.Verdict;
import com.corz.client.utils.TrackFlick.Commit;
import com.corz.client.utils.SwordMovementPlanner.Ctx;
import com.corz.client.utils.SwordMovementPlanner.Phase;
import com.corz.client.utils.SwordMovementPlanner.Plan;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.gui.Button;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.ZoomHud;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.render.CodeCategoryUtils;
import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import com.corz.client.utils.ScissorTgtHandler.Tone;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.ClampMath;
import com.corz.client.world.MathClampUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HumanAimRotator;
import com.corz.client.utils.CrystalBotModuleMonitor;
import com.corz.client.utils.BotProbeFactory;
import com.corz.client.utils.ServerPacketEventQueue;
import com.corz.client.utils.AStarPathfinder;
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
import com.corz.client.utils.OutboundPacketClassifier;
import com.corz.client.utils.OutboundPacketKind;
import com.corz.client.utils.TickActionLedger;
import com.corz.client.categories.bots.SwordPvpBot;
import com.corz.client.utils.SprintResetTracker;
import com.corz.client.utils.TrackFlick;
import com.corz.client.utils.AnchorRoutePlanner;
import com.corz.client.utils.SwordMovementPlanner;
import com.corz.client.categories.misc.NameProtect;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.categories.visuals.Freelook;
import com.corz.client.utils.TargetFightTracker;
import com.corz.client.utils.EntityPlayerHook;
import com.corz.client.utils.SwordCombatMath;
import com.corz.client.utils.SwordBotConfig;

public final class SwordPlayer {
   private final RayBudget randomTiming;
   private String lastOpponentName;
   private int centerEyeMissTicks;
   private LivingEntity target;
   private final CrystalBotModuleMonitor botCrystal;
   private final UserOverrideTracker userInputTracker;
   private final AnchorRoutePlanner vecHash;
   private final ServerTpsEstimator playerTimeTracker;
   private final TickActionLedger sprintSneak = new TickActionLedger();
   private final TargetFightTracker combatTracker;
   private volatile boolean targetLocked;
   private final HandswapGate ledgerHandswap;
   private volatile boolean botRunning;
   private final ServerPacketEventQueue cpacketEntity;
   private boolean swordMissing;
   private final TrackFlick trackFlick;
   private boolean deathHandled;
   private long winMessageUntilMs;
   private int noTargetTicks;
   private static volatile boolean fightActive;
   private final SwordPvpBot swordPvpBot;
   private final FrameRotationApplier mouseLookHandler;
   private boolean sprintHitActive;
   private volatile boolean screenClosedByServer;
   private volatile Snapshot uiSnapshot;
   private final RotationInterpolator pitchYaw;
   private final BotRunSupervisor networkHandler = new BotRunSupervisor();
   public static final String SWORD_BOT = "Sword PvP Bot";
   private final HotbarSlotSwitcher inventorySelected;
   private final SprintResetTracker sprintTimeTracker;
   private final EntityPlayerHook attackHook;
   public static final String LOOKING_PLAYER_NEARBY = "Looking for a player nearby.";
   private final SwordMovementPlanner combatPlanner;
   private Phase currentPhase;
   private volatile boolean botControlling;
   public static final String PUT_SWORD_IN_HOTBAR = "Put a sword in your hotbar.";

   private void applyRotationCommit(ClientPlayerEntity clientPlayerEntity, long pitchYawValue, Commit commit) {
      float yaw = clientPlayerEntity.getYaw();
      float pitch = clientPlayerEntity.getPitch();
      if (commit != null) {
         MathClampUtils.handle(this.swordPvpBot, commit.yaw(), commit.pitch(), 95, false);
      }

      if (commit != null && !commit.hold()) {
         this.pitchYaw.setSegment(pitchYawValue, yaw, pitch, commit.yaw(), commit.pitch());
      } else {
         this.pitchYaw.deactivate();
      }
   }

   private void releaseControl() {
      PlayerInputOverride.cancelRequestFrom(this.swordPvpBot);
      this.pitchYaw.deactivate();
   }

   private String formatCombatStats() {
      return "Hits "
         + this.combatTracker.getHitsTakenCount()
         + " \u00b7 Taken "
         + this.combatTracker.getHitsLandedCount()
         + " \u00b7 Best combo "
         + this.combatTracker.getMaxHitsTakenInRow();
   }

   public void onMouseDelta(DeltaAngle deltaAngle) {
      this.mouseLookHandler.applyInterpolatedRotation(deltaAngle, this.botRunning);
   }

   private long getMsSinceHook(long swordPlayerValue) {
      long hookTime = this.attackHook.getLastAttackTick();
      return hookTime == Long.MIN_VALUE ? 2147483647L : Math.max(0L, swordPlayerValue - hookTime);
   }

   private static boolean isAnyBoundKeyPressed(MinecraftClient client) {
      if (client.currentScreen == null && client.options != null) {
         for (KeyBinding keyBinding : getControlKeyBindings(client.options)) {
            if (CodeCategoryUtils.isKeyPressed(keyBinding)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void trackOutgoingPacket(InventoryPlayer inventoryPlayer) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (inventoryPlayer != null && instance.player != null && instance.isOnThread() && !inventoryPlayer.isOpenedByClient()) {
         var packet = inventoryPlayer.packet;
         OutboundPacketKind lookChanges = OutboundPacketClassifier.classifyPacket(packet);
         boolean sprintSneakFlag = this.ledgerHandswap.isHandswapPending();
         long sprintSneakValue = ClientThreadUtils.getTime();
         if (lookChanges == OutboundPacketKind.TICK_END) {
            this.sprintSneak.onTickEnd();
         } else {
            this.sprintSneak.recordInteractPacket(lookChanges, sprintSneakFlag, inventoryPlayer.isInventoryPlayerActive());
            switch (lookChanges) {
               case SLOT:
                  this.sprintSneak.onSlotSelected(OutboundPacketClassifier.getSelectedSlot(packet), sprintSneakFlag);
                  break;
               case MOVE:
               case MOVE_LOOK:
                  float[] changesLook = OutboundPacketClassifier.getMoveLookAngles(packet);
                  if (changesLook != null) {
                     this.sprintSneak.recordSentLook(changesLook[0], changesLook[1]);
                  }
                  break;
               case INPUT:
                  PlayerInput input = OutboundPacketClassifier.getInput(packet);
                  if (input instanceof PlayerInput(boolean sprintSneakFlag2, boolean sprintSneakFlag3, boolean sprintSneakFlag4, boolean sprintSneakFlag5, boolean sprintSneakFlag6, boolean sprintSneakFlag7, boolean sprintSneakFlag8)) {
                     this.sprintSneak.onPlayerInput(sprintSneakFlag2, sprintSneakFlag3, sprintSneakFlag4, sprintSneakFlag5, sprintSneakFlag6, sprintSneakFlag7, sprintSneakFlag8);
                  }
                  break;
               case SPRINT_START:
                  this.sprintTimeTracker.onSprintToggle(true, sprintSneakValue);
                  break;
               case SPRINT_STOP:
                  this.sprintTimeTracker.onSprintToggle(false, sprintSneakValue);
                  break;
               case CLICK_SLOT:
                  this.sprintSneak.markContainerClicked();
                  break;
               case CLOSE_SCREEN:
                  this.sprintSneak.markContainerClosed();
                  break;
               case ATTACK:
                  boolean playerFlag = this.sprintTimeTracker.isSprinting() && instance.player.getAttackCooldownProgress(0.5F) > 0.9F
                     || InventoryStackUtilsEmpty.getEnchantmentsDefault(instance.player.getMainHandStack(), Enchantments.KNOCKBACK) > 0;
                  this.sprintTimeTracker.recordAttack(playerFlag, sprintSneakValue);
            }
         }
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

   public void onMouseButtonInput(Button button) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (button != null && instance.currentScreen == null && instance.options != null && button.getValue() == 1) {
         for (KeyBinding keyBinding : getControlKeyBindings(instance.options)) {
            if (CodeCategoryUtils.canCategoryCodeForKeyBindingInt(keyBinding, button.getButton())) {
               this.userInputTracker.recordUserInputTime(ConnectionTimeUtils.getTimeTimeMillis());
               return;
            }
         }
      }
   }

   public static boolean isFightActive() {
      return fightActive;
   }

   private void resetAllState() {
      this.sprintSneak.reset();
      this.trackFlick.reset();
      this.inventorySelected.resetAll();
      this.sprintTimeTracker.reset();
      this.combatPlanner.reset();
      this.attackHook.resetAimState();
      this.combatTracker.resetTracking();
      this.cpacketEntity.clearPendingEvents();
      this.playerTimeTracker.reset();
      this.pitchYaw.deactivate();
      this.target = null;
      this.currentPhase = Phase.IDLE;
      this.noTargetTicks = 0;
      this.deathHandled = false;
      this.swordMissing = false;
      this.sprintHitActive = false;
      this.lastOpponentName = null;
      this.winMessageUntilMs = Long.MIN_VALUE;
      this.targetLocked = false;
      this.botControlling = false;
      this.botRunning = false;
      fightActive = false;
   }

   private static String truncateText(String swordPlayerText, int swordPlayerValue) {
      return swordPlayerText != null && swordPlayerText.length() > swordPlayerValue ? swordPlayerText.substring(0, Math.max(0, swordPlayerValue - 3)) + "..." : swordPlayerText;
   }

   private static String applyNameProtect(String swordPlayerText) {
      if (swordPlayerText == null) {
         return null;
      } else {
         String swordPlayerText2 = NameProtect.getString(swordPlayerText);
         return swordPlayerText2 == null ? swordPlayerText : swordPlayerText2;
      }
   }

   public void onClientTick() {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null && instance.world != null && instance.interactionManager != null && instance.getNetworkHandler() != null) {
         long waitingValue = ClientThreadUtils.getTime();
         long timeTimeMillis = ConnectionTimeUtils.getTimeTimeMillis();
         fightActive = this.targetLocked;
         if (fightActive) {
            instance.getInactivityFpsLimiter().onInput();
         }

         try {
            this.runBotTick(instance, waitingValue, timeTimeMillis);
         } catch (Throwable e) {
            this.onTickFailure(e, timeTimeMillis);
         }
      } else {
         this.targetLocked = false;
         this.botControlling = false;
         this.botRunning = false;
         fightActive = false;
         this.setUiSnapshot(this.buildUiSnapshot(Tone.IDLE, "Waiting", null, "Join a world to start."));
      }
   }

   private void onTickFailure(Throwable throwable, long networkValue) {
      try {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         if (player != null && (this.sprintSneak.hasInteractDuringHandswap() || this.sprintSneak.hasForcedInteract())) {
            this.applyRotationCommit(player, ClientThreadUtils.getTime(), this.trackFlick.resolveCommit(true));
         }
      } catch (Throwable e) {
         this.pitchYaw.deactivate();
      }

      try {
         this.releaseControl();
      } catch (Throwable e2) {
         this.targetLocked = false;
      }

      this.targetLocked = false;
      this.botControlling = false;
      this.networkHandler.recordError(networkValue);
      if (this.networkHandler.hasRepeatedErrors()) {
         this.stopBot(new Verdict(Level.STOP, com.corz.client.utils.BotRunSupervisor.Reason.ERROR_LOOP, "", true, false, false));
      }
   }

   private LivingEntity selectTarget(ClientPlayerEntity player, double livingEntityValue, double livingEntityLimit, long entityLivingValue) {
      LivingEntity livingEntity = this.target;
      if (livingEntity != null && EntityPlayerUtilsLiving.canSpectatorDead(livingEntity, Options.INSTANCE2) && EntityPlayerUtilsLiving.getMinMax(livingEntity) <= livingEntityLimit) {
         LivingEntity entityLiving = this.getRecentAggressor(livingEntity, entityLivingValue);
         return entityLiving != null && SwordCombatMath.isSafelyBeyondRange(EntityPlayerUtilsLiving.getMinMax(livingEntity), EntityPlayerUtilsLiving.getMinMax(entityLiving)) ? entityLiving : livingEntity;
      } else {
         var instanceWorld = EntityPlayerUtilsLiving.getInstanceWorld(livingEntityValue, Options.INSTANCE2);
         LivingEntity instanceWorldItem2 = null;
         double positiveInfinity = Double.POSITIVE_INFINITY;

         for (LivingEntity instanceWorldItem : instanceWorld) {
            if (instanceWorldItem != player) {
               double minMax = EntityPlayerUtilsLiving.getMinMax(instanceWorldItem);
               if (!(minMax < 0.001)) {
                  double livingEntityValue3 = minMax - (this.combatTracker.isRecentVictim(instanceWorldItem.getId(), entityLivingValue, 60L) ? 4.0 : 0.0);
                  if (livingEntityValue3 < positiveInfinity) {
                     positiveInfinity = livingEntityValue3;
                     instanceWorldItem2 = instanceWorldItem;
                  }
               }
            }
         }

         return instanceWorldItem2;
      }
   }

   private void resetTargetState() {
      this.target = null;
      this.centerEyeMissTicks = 0;
      this.combatTracker.setTarget(null);
      this.combatPlanner.reset();
      this.attackHook.resetAimState();
      this.sprintTimeTracker.reset();
      this.currentPhase = Phase.IDLE;
   }

   public void onZoomInput(ZoomHud zoomHud) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (zoomHud != null && instance.currentScreen == null) {
         this.userInputTracker.recordUserInputTime(ConnectionTimeUtils.getTimeTimeMillis());
      }
   }

   public boolean isBotControlling() {
      return this.botControlling && !this.userInputTracker.isUserOverriding(ConnectionTimeUtils.getTimeTimeMillis());
   }

   private Snapshot buildStatusSnapshot(Verdict verdict, LivingEntity entity, ClientPlayerEntity player, long lookingSwordValue) {
      if (verdict.level() != Level.RUN) {
         return this.buildUiSnapshot(Tone.PAUSED, "Paused", entity == null ? null : applyNameProtect(this.combatTracker.getTargetName()), formatModuleDetail(verdict));
      } else if (entity == null) {
         return this.lastOpponentName != null && lookingSwordValue <= this.winMessageUntilMs
            ? this.buildUiSnapshot(Tone.ACTIVE, "Won", this.lastOpponentName, this.formatCombatStats())
            : this.buildUiSnapshot(Tone.IDLE, "Looking", null, "Looking for a player nearby.");
      } else if (this.swordMissing) {
         return this.buildUiSnapshot(Tone.DANGER, "No sword", applyNameProtect(this.combatTracker.getTargetName()), "Put a sword in your hotbar.");
      } else {
         boolean lookingSwordFlag = player.getHealth() + player.getAbsorptionAmount() <= 6.0F;
         String lookingSwordText = this.combatTracker.getHitsTakenInRow() >= 2 ? "Combo x" + this.combatTracker.getHitsTakenInRow() : this.currentPhase.displayText;
         return this.buildUiSnapshot(lookingSwordFlag ? Tone.DANGER : Tone.ACTIVE, lookingSwordText, applyNameProtect(this.combatTracker.getTargetName()), this.formatCombatStats());
      }
   }

   private static double getRadialVelocity(ClientPlayerEntity player, LivingEntity entity, Vec3d vec) {
      double deltaX = entity.getX() - player.getX();
      double deltaZ = entity.getZ() - player.getZ();
      double hypot = Math.hypot(deltaX, deltaZ);
      return hypot > 0.001 && vec != null ? (vec.x * deltaX + vec.z * deltaZ) / hypot : 0.0;
   }

   private void stopBot(Verdict verdict) {
      AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled();

      try {
         this.swordPvpBot.setEnabled(false);
      } finally {
         closeQuietly(disabledEnabled);
      }

      if (!verdict.quiet()) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Sword PvP Bot", formatModuleDetail(verdict), false);
      }
   }

   public void onKeyInput(SwordBot swordBot) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (swordBot != null && instance.currentScreen == null && instance.options != null && swordBot.getValue() == 1) {
         for (KeyBinding keyBinding : getControlKeyBindings(instance.options)) {
            if (CodeCategoryUtils.canCategoryCode(keyBinding, swordBot.getKey())) {
               this.userInputTracker.recordUserInputTime(ConnectionTimeUtils.getTimeTimeMillis());
               return;
            }
         }
      }
   }

   public void onBotDisabled() {
      MinecraftClient instance = MinecraftClient.getInstance();

      try {
         PlayerInputOverride.cancelRequestFrom(this.swordPvpBot);
         ClientPlayerEntity player = instance.player;
         int selectedSlot = this.inventorySelected.getRememberedSlot();
         if (player != null && selectedSlot >= 0 && selectedSlot < 9 && player.getInventory().getSelectedSlot() != selectedSlot && !this.sprintSneak.hasInteractThisTick()) {
            player.getInventory().setSelectedSlot(selectedSlot);
         }

         this.inventorySelected.resetAll();
         this.pitchYaw.deactivate();
      } catch (Throwable e) {
      }

      this.targetLocked = false;
      this.botControlling = false;
      this.botRunning = false;
      fightActive = false;
      this.target = null;
      this.uiSnapshot = Snapshot.INSTANCE;
   }

   private void setUiSnapshot(Snapshot menuTurnData) {
      if (menuTurnData != null && !menuTurnData.equals(this.uiSnapshot)) {
         this.uiSnapshot = menuTurnData;
      }
   }

   private boolean isEyeOrCenterVisible(Vec3d vec, LivingEntity entity, Box box) {
      boolean centerEyeFlag = BlockEntityUtils.canSubtractRaycast(vec, entity.getEyePos()) || BlockEntityUtils.canSubtractRaycast(vec, box.getCenter());
      this.centerEyeMissTicks = centerEyeFlag ? 0 : this.centerEyeMissTicks + 1;
      return SwordCombatMath.isAtLeastFour(this.centerEyeMissTicks);
   }

   public Snapshot getUiSnapshot() {
      return this.uiSnapshot;
   }

   private void runBotTick(MinecraftClient client, long entityLivingValue, long playerValue) {
      ClientPlayerEntity player = client.player;
      int id = player.getId();
      this.sprintSneak.onClientTime(entityLivingValue);
      if (this.screenClosedByServer) {
         this.screenClosedByServer = false;
         this.sprintSneak.markContainerClosed();
      }

      this.userInputTracker.finishTick(playerValue, isAnyBoundKeyPressed(client));
      this.cpacketEntity.drainEvents(client.world, tickx -> this.onCombatEvent(tickx, entityLivingValue, id), 512);
      Verdict setbackTime = this.networkHandler
         .evaluateProbe(BotProbeFactory.buildBotProbe(playerValue, entityLivingValue, this.playerTimeTracker, this.userInputTracker, this.botCrystal, this.trackFlick));
      if (setbackTime.level() == Level.STOP) {
         this.releaseControl();
         this.stopBot(setbackTime);
      } else {
         if (setbackTime.reason() == com.corz.client.utils.BotRunSupervisor.Reason.DEAD && !this.deathHandled) {
            this.deathHandled = true;
            this.target = null;
            this.combatTracker.setTarget(null);
         }

         if (setbackTime.reason() != com.corz.client.utils.BotRunSupervisor.Reason.DEAD && setbackTime.reason() != com.corz.client.utils.BotRunSupervisor.Reason.RESPAWN) {
            this.deathHandled = false;
         }

         if (setbackTime.resetState()) {
            this.resetTargetState();
            this.inventorySelected.syncSelectedSlot();
            this.sprintSneak.markContainerClosed();
         }

         if (setbackTime.level() == Level.PAUSED) {
            this.onPaused(setbackTime);
         } else {
            this.botRunning = setbackTime.level() == Level.RUN;
            this.trackFlick.beginTick(entityLivingValue, MathClampUtils.getYaw(), MathClampUtils.getClamp(), ClampMath.getSensitivityMouse());
            boolean entityLivingFlag = this.sprintSneak.hasSentLookPacket()
                  && !BoxLengthUtils.canFlag(this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), this.sprintSneak.getLastSentYaw(), this.sprintSneak.getLastSentPitch())
               || !BoxLengthUtils.canFlag(this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), player.getYaw(), player.getPitch());
            this.ledgerHandswap.reset(setbackTime.level(), entityLivingFlag);
            this.randomTiming.resetFrame();
            this.attackHook.clearFailReason();
            HumanAimRotator.setReducedStepLimit(false);
            SwordBotConfig aggressiveBase = this.swordPvpBot.buildSettings();
            this.trackFlick.setSpeedScale(aggressiveBase.turnScale());
            if (this.target != null && this.combatTracker.detectKill(this.target, entityLivingValue)) {
               this.lastOpponentName = applyNameProtect(this.combatTracker.getTargetName());
               this.winMessageUntilMs = entityLivingValue + 100L;
            }

            LivingEntity livingEntity = this.selectTarget(player, aggressiveBase.fightRange(), aggressiveBase.fightRange() + 8.0, entityLivingValue);
            boolean entityLivingFlag2 = livingEntity != null && (this.target == null || this.target.getId() != livingEntity.getId());
            if (livingEntity != null && this.target == null) {
               this.inventorySelected.resetRestoreRequest();
               this.trackFlick.reset();
               this.trackFlick.beginTick(entityLivingValue, MathClampUtils.getYaw(), MathClampUtils.getClamp(), ClampMath.getSensitivityMouse());
            }

            if (entityLivingFlag2) {
               this.combatPlanner.reset();
               this.attackHook.resetAimState();
               this.centerEyeMissTicks = 0;
            }

            this.target = livingEntity;
            this.combatTracker.setTarget(livingEntity);
            this.noTargetTicks = livingEntity == null ? this.noTargetTicks + 1 : 0;
            this.targetLocked = livingEntity != null;
            this.botControlling = this.targetLocked;
            Plan plan = Plan.INSTANCE;
            Intent intent = null;
            if (livingEntity != null) {
               try (SwitchHandle handler = this.ledgerHandswap.acquireHandle()) {
                  Vec3d eyePos = player.getEyePos();
                  Box boundingBox = livingEntity.getBoundingBox();
                  double minMax = BlockEntityUtils.getMinMax(eyePos, boundingBox);
                  double minMax2 = BlockEntityUtils.getMinMax(livingEntity.getEyePos(), player.getBoundingBox());
                  this.combatTracker.updateTargetState(livingEntity, minMax);
                  Vec3d vec3d = new Vec3d(player.getX(), player.getY() + 1.1, player.getZ());
                  Vec3d eyePos2 = livingEntity.getEyePos();
                  double angleToVector = SwordCombatMath.getAngleToVector(livingEntity.getHeadYaw(), livingEntity.getPitch(), vec3d.x - eyePos2.x, vec3d.y - eyePos2.y, vec3d.z - eyePos2.z);
                  double radialVelocity = getRadialVelocity(player, livingEntity, this.combatTracker.getTargetMotion());
                  float attackCooldownProgress = player.getAttackCooldownProgress(0.5F);
                  int ticksToReachThreshold = SwordCombatMath.getTicksToReachThreshold(attackCooldownProgress, player.getAttackCooldownProgressPerTick(), 0.95F);
                  int remainingTicksCeilWithMargin = PendingEntityHitTracker.getRemainingTicksCeilWithMargin(EntityDamageUtils.getMin(livingEntity.getId()), EntityDamageUtils.getTime(), 1);
                  Ctx ctx = new Ctx(
                     entityLivingValue,
                     minMax,
                     minMax2,
                     ticksToReachThreshold,
                     attackCooldownProgress,
                     angleToVector,
                     radialVelocity,
                     this.combatTracker.getSmoothedHealthLoss(),
                     remainingTicksCeilWithMargin,
                     this.sprintTimeTracker.isSprinting(),
                     EntityDamageUtils.getCachedHurtTimeLimit(),
                     aggressiveBase,
                     livingEntity.isBlocking(),
                     livingEntity.getY() - player.getY(),
                     this.getMsSinceHook(entityLivingValue),
                     this.sprintHitActive,
                     this.isEyeOrCenterVisible(eyePos, livingEntity, boundingBox)
                  );
                  plan = this.combatPlanner.planNextMove(player, livingEntity, ctx);
                  intent = plan.intent();
                  this.swordMissing = this.attackHook.findBestSwordSlot() < 0;
                  boolean entityLivingFlag3 = !this.swordMissing && this.attackHook.isSwordSlotSelected();
                  float[] entityLivingValues = plan.phase() == Phase.CHASE && minMax > 10.0 ? this.vecHash.getRouteLookTarget(this.trackFlick.getCurrentYaw()) : null;
                  if (entityLivingValues != null) {
                     this.attackHook.trackHeading(entityLivingValues[0], entityLivingValues[1]);
                  } else {
                     this.attackHook.aimAtTarget(livingEntity, intent);
                  }

                  boolean sprinting = this.sprintTimeTracker.isSprinting();
                  if (entityLivingFlag3 && plan.wantHit() && this.attackHook.tryAttackTarget(livingEntity, plan.minCharge(), plan.crit(), sprinting)) {
                     this.sprintHitActive = sprinting && !plan.crit();
                     intent = this.combatPlanner.startWTap(player, livingEntity, minMax, aggressiveBase.strafe());
                     plan = new Plan(intent, true, plan.minCharge(), plan.crit(), Phase.TAP);
                  }
               }
            }

            this.currentPhase = plan.phase();
            if (this.noTargetTicks >= 40) {
               this.inventorySelected.rememberCurrentSlot();
            }

            this.inventorySelected.flushPendingSlotRequest(entityLivingValue);
            if (livingEntity != null && setbackTime.moveAllowed() && intent != null) {
               PlayerInputOverride.submitRequest(this.swordPvpBot, intent, 50);
            } else {
               PlayerInputOverride.cancelRequestFrom(this.swordPvpBot);
            }

            this.applyRotationCommit(
               player,
               entityLivingValue,
               this.trackFlick.resolveCommit(this.sprintSneak.hasInteractDuringHandswap() || this.sprintSneak.hasForcedInteract() || this.trackFlick.hasPendingRequest() && this.sprintSneak.hasInteractThisTick())
            );
            this.setUiSnapshot(this.buildStatusSnapshot(setbackTime, livingEntity, player, entityLivingValue));
         }
      }
   }

   public boolean isTargetLocked() {
      return this.targetLocked;
   }

   private Snapshot buildUiSnapshot(Tone tone, String livingEntityText, String livingEntityText2, String swordPlayerText) {
      return new Snapshot(
         tone,
         truncateText(livingEntityText, 12),
         livingEntityText2,
         this.combatTracker.getTargetSwingCount(),
         this.combatTracker.getOwnSwingCount(),
         swordPlayerText == null ? null : truncateText(swordPlayerText, 48)
      );
   }

   private static String formatModuleDetail(Verdict verdict) {
      String teleportingModuleText = verdict.reason().statusMessage;
      if (!teleportingModuleText.contains("%s")) {
         return truncateText(teleportingModuleText, 48);
      } else {
         String moduleDetailText = verdict.detail() != null && !verdict.detail().isBlank() ? verdict.detail() : "a module";
         return truncateText(String.format(Locale.ROOT, teleportingModuleText, moduleDetailText), 48);
      }
   }

   private static KeyBinding[] getControlKeyBindings(GameOptions gameOptions) {
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

   public void resetAndShowWaiting() {
      MinecraftClient instance = MinecraftClient.getInstance();
      long waitingNetworkValue = ConnectionTimeUtils.getTimeTimeMillis();
      this.resetAllState();
      this.userInputTracker.reset();
      this.botCrystal.reset();
      if (instance.player != null && instance.getNetworkHandler() != null) {
         this.networkHandler.beginSession(instance.getNetworkHandler(), waitingNetworkValue);
         this.uiSnapshot = this.buildUiSnapshot(Tone.IDLE, "Waiting", null, "Getting ready.");
      } else {
         this.networkHandler.beginSession(null, waitingNetworkValue);
         this.uiSnapshot = this.buildUiSnapshot(Tone.IDLE, "Waiting", null, "Join a world to start.");
      }
   }

   public void onIncomingPacket(AutoVanish autoVanish) {
      if (autoVanish != null && autoVanish.packet != null) {
         if (autoVanish.packet instanceof CloseScreenS2CPacket) {
            this.screenClosedByServer = true;
         }

         this.cpacketEntity.onPacketReceived(autoVanish.packet);
      }
   }

   private LivingEntity getRecentAggressor(LivingEntity entity, long entityLivingValue) {
      MinecraftClient instance = MinecraftClient.getInstance();
      int entityLivingLimit = this.combatTracker.getLastHitVictimId();
      if (instance.world != null && entityLivingLimit >= 0 && entityLivingLimit != entity.getId() && this.combatTracker.isRecentVictim(entityLivingLimit, entityLivingValue, 60L)) {
         return instance.world.getEntityById(entityLivingLimit) instanceof LivingEntity livingEntity && EntityPlayerUtilsLiving.canSpectatorDead(livingEntity, Options.INSTANCE2) ? livingEntity : null;
      } else {
         return null;
      }
   }

   public void recordHookTimeFromVec(Vec3d vec, boolean entityJoinFlag) {
      if (vec != null && this.targetLocked) {
         this.attackHook.setLastSetTick(ClientThreadUtils.getTime());
         if (!entityJoinFlag) {
            this.combatPlanner.armJumpReset(ClientThreadUtils.getTime());
         }
      }
   }

   private void onCombatEvent(Event event, long entityLivingValue, int entityLivingValue2) {
      switch (event) {
         case com.corz.client.utils.ServerPacketEventQueue.TimeUpdate timeUpdate:
            this.playerTimeTracker.onTimeUpdate(timeUpdate.atMs());
            break;
         case Swing swing:
            this.combatTracker.recordTargetSwingTime(swing.entityId(), entityLivingValue);
            break;
         case EntityDamage fresh:
            this.combatTracker.recordDamageEvent(fresh.entityId(), fresh.causeId(), entityLivingValue2, entityLivingValue);
            break;
         case Pop pop:
            this.combatTracker.countSwing(pop.entityId(), entityLivingValue2);
            break;
         default:
      }
   }

   public SwordPlayer(SwordPvpBot swordPvpBot) {
      this.userInputTracker = new UserOverrideTracker();
      this.playerTimeTracker = new ServerTpsEstimator();
      this.cpacketEntity = new ServerPacketEventQueue();
      this.trackFlick = new TrackFlick(new Random());
      this.ledgerHandswap = new HandswapGate(this.sprintSneak, this.trackFlick);
      this.pitchYaw = new RotationInterpolator();
      this.mouseLookHandler = new FrameRotationApplier(this.pitchYaw);
      this.inventorySelected = new HotbarSlotSwitcher(this.sprintSneak, this.ledgerHandswap);
      this.randomTiming = new RayBudget(700, 1200);
      this.vecHash = AnchorRoutePlanner.create(this.randomTiming, Danger.INSTANCE);
      this.sprintTimeTracker = new SprintResetTracker();
      this.combatTracker = new TargetFightTracker();
      this.combatPlanner = new SwordMovementPlanner(new Random(), this.vecHash);
      this.uiSnapshot = Snapshot.INSTANCE;
      this.currentPhase = Phase.IDLE;
      this.winMessageUntilMs = Long.MIN_VALUE;
      this.swordPvpBot = swordPvpBot;
      this.attackHook = new EntityPlayerHook(swordPvpBot, this.trackFlick, this.ledgerHandswap, this.inventorySelected, this.sprintSneak);
      this.botCrystal = new CrystalBotModuleMonitor(swordPvpBot);
   }

   private void onPaused(Verdict verdict) {
      this.releaseControl();
      this.botRunning = false;
      this.targetLocked = false;
      this.botControlling = false;
      if (verdict.reason() == com.corz.client.utils.BotRunSupervisor.Reason.ARMING) {
         this.trackFlick.reset();
         this.inventorySelected.syncSelectedSlot();
      }

      this.setUiSnapshot(this.buildUiSnapshot(Tone.PAUSED, "Paused", null, formatModuleDetail(verdict)));
   }

   private static String formatThrowableTrace(Throwable throwable) {
      StringBuilder stringBuilder = new StringBuilder(String.valueOf(throwable));
      StackTraceElement[] stackTrace = throwable.getStackTrace();

      for (int i = 0; i < Math.min(4, stackTrace.length); i++) {
         stringBuilder.append(" at ").append(stackTrace[i]);
      }

      return stringBuilder.toString();
   }

   private static void closeQuietly(AutoCloseable autoCloseable) {
      if (autoCloseable != null) {
         try {
            autoCloseable.close();
         } catch (Exception e) {
         }
      }
   }
}
