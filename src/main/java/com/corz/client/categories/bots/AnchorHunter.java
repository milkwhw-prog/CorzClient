package com.corz.client.categories.bots;

import com.corz.client.utils.BotRunSupervisor.Level;
import com.corz.client.utils.BotRunSupervisor.Probe;
import com.corz.client.utils.BotRunSupervisor.Verdict;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.utils.RtpReplies.Reply;
import com.corz.client.utils.AirPlan.AirAction;
import com.corz.client.utils.AnchorBreaker.BreakStatus;
import com.corz.client.utils.FleePlanner.Threat;
import com.corz.client.utils.FleePlanner.Waypoint;
import com.corz.client.utils.PearlHop.Shot;
import com.corz.client.utils.PearlHop.State;
import com.corz.client.utils.PlayerWatch.Seen;
import com.corz.client.utils.StandFinder.Result;
import com.corz.client.utils.StandFinder.Sight;
import com.corz.client.utils.StandFinder.Stand;
import com.corz.client.utils.ThreatPlan.Input;
import com.corz.client.utils.ThreatPlan.React;
import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.TickProgress;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import com.corz.client.utils.ConnectionTimeUtils.Inbound;
import com.corz.client.utils.ConnectionTimeUtils.Outbound;
import com.corz.client.render.CodeCategoryUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import com.corz.client.utils.ScissorTgtHandler.Tone;
import it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.categories.bots.AutoElytraPilot;
import com.corz.client.categories.bots.CrystalPvpBot;
import com.corz.client.utils.BotRunSupervisor;
import com.corz.client.utils.UserOverrideTracker;
import com.corz.client.categories.bots.SwordPvpBot;
import com.corz.client.categories.combat.AnchorMacro;
import com.corz.client.categories.combat.CrystalAura;
import com.corz.client.categories.combat.SafeAnchor;
import com.corz.client.utils.RtpReplies;
import com.corz.client.utils.AirPlan;
import com.corz.client.utils.AnchorBreaker;
import com.corz.client.utils.AnchorSafety;
import com.corz.client.utils.AnchorScanner;
import com.corz.client.utils.AnchorTarget;
import com.corz.client.utils.ApproachPlan;
import com.corz.client.utils.DigMover;
import com.corz.client.utils.EnderStash;
import com.corz.client.utils.FleePlanner;
import com.corz.client.utils.HunterBody;
import com.corz.client.utils.HunterMarks;
import com.corz.client.utils.HunterState;
import com.corz.client.utils.HunterStats;
import com.corz.client.utils.HunterStatusCard;
import com.corz.client.utils.McCells;
import com.corz.client.utils.McViews;
import com.corz.client.utils.PearlHop;
import com.corz.client.utils.PlayerWatch;
import com.corz.client.utils.StandFinder;
import com.corz.client.utils.SurfaceWalker;
import com.corz.client.utils.ThreatPlan;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.categories.settings.Notifications;
import com.corz.client.categories.settings.Panic;
import com.corz.client.categories.visuals.Freelook;
import ai.auth.LicenseRuntime;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.categories.bots.AutoAreaMiner;
import com.corz.client.categories.bots.AutoElytraFinder;
import com.corz.client.categories.bots.VillagerTradeBot;
import com.corz.client.categories.bots.RtpBaseAlert;
import com.corz.client.categories.bots.SpawnerProtect;
import com.corz.client.categories.donutsmp.AutoMine;
import com.corz.client.utils.RtpRegions;
import com.corz.client.utils.RtpClock;
import com.corz.client.utils.HotbarSlotUtils;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.DirExtractionUtils;
import com.corz.client.utils.ExtUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PlayerScaffold;
import com.corz.client.utils.ScissorTgtHandler;

public class AnchorHunter extends Module implements ScissorTgtHandler {
   private boolean fleeStartRecorded;
   private long mineStartTick;
   private final ConcurrentLinkedQueue<int[]> damageEventQueue;
   private List<BlockPos> surfacePath;
   private String lastNotificationText;
   private long leftOverworldSinceMs;
   private long entityPosIgnoreUntilMs;
   private final BooleanSetting enderChestStash;
   private final BooleanSetting ignoreFriends;
   private ClientConnection sessionConnection;
   private final EnumSetting notificationMode;
   private final BooleanSetting digToHiddenAnchors;
   private boolean protectedAreaDetected;
   private final BooleanSetting showStatusCard;
   private final HunterStats hunterStats;
   private boolean air;
   private int anchorCountBeforeMining;
   private boolean nearAnchorTarget;
   private boolean rtpWarmupActive;
   private BlockPos targetAnchorPos;
   private long eatCooldownUntilTick;
   private String nearbyPlayerLabel;
   private final IntegerSetting playerRange;
   private String statusDetail;
   private World rtpWorld;
   public static final List<Class<? extends Module>> BLOCKING_MODULES = List.of(
      CrystalPvpBot.class,
      AutoBuilder.class,
      AutoAreaMiner.class,
      AutoElytraFinder.class,
      AutoElytraPilot.class,
      SwordPvpBot.class,
      VillagerTradeBot.class,
      RtpBaseAlert.class,
      SpawnerProtect.class
   );
   private EnderStash enderStash;
   public static final List<Class<? extends Module>> STOPPING_MODULES = List.of(AnchorMacro.class, SafeAnchor.class, CrystalAura.class, AutoMine.class, Freecam.class);
   private final EnumSetting region = new EnumSetting(
      "Region",
      "Which /rtp region to use. Random switches between the overworld regions. The Nether is never used.",
      "Random",
      RtpRegions.RANDOM_REGION_ITEMS.toArray(new String[0])
   );
   private final AtomicInteger pendingMessageCount;
   private HunterBody hunterBody;
   private final Set<Long> candidateStandPositions;
   private Verdict resumed;
   private long stateStartTick;
   private long lastPlayerCombatMs;
   private long lastNotificationTick;
   private SurfaceWalker surfaceWalker;
   private long lastRtpSentMs;
   private AnchorBreaker anchorBreaker;
   private long lastTeleportMs;
   private HunterStatusCard hunterStatusCard;
   private final BooleanSetting showPath;
   private boolean safeAnchorScanStarted;
   private int anchorMissingTicks;
   private boolean anchorMissing;
   private final LongOpenHashSet skippedAnchorKeys;
   private long huntTick;
   private int lastGhostsSkipsCount;
   private Waypoint fleeWaypoint;
   private String pendingStopReason;
   private long stateStartMs;
   private volatile Snapshot statusSnapshot;
   private Vec3d lastPlayerPos;
   private final List<AnchorTarget> safeAnchorTargets;
   private HunterState resumeState;
   private final UserOverrideTracker playerInputTracker;
   private final AnchorScanner anchorScanner;
   private final List<Module> disabledModules;
   private double scanCoverage;
   private final PlayerWatch playerWatch;
   private long targetSelectedMs;
   private final List<Threat> nearbyThreats;
   private Vec3d posAtRtpSend;
   private long anchorBrokenTick;
   private boolean targetHasCharges;
   private boolean breakingAnchor;
   private long lastWaypointTick;
   private final ConcurrentLinkedQueue<String> incomingMessageQueue;
   private final RtpClock rtpClock;
   private boolean airPanic;
   private DigMover digMover;
   private final BotRunSupervisor networkHandler;
   private PearlHop pearlHop;
   private String east;
   private final IntegerSetting stopAfter;
   private int anchorReappearCount;
   private final McCells mcCells;
   private final BooleanSetting logOutIfCaught;
   private int consecutiveErrorCount;
   private boolean threatVisible;
   private long lastCoverageTick;
   private HunterMarks hunterMarks;
   private final BooleanSetting pearlTravel;
   private long lastTickMs;
   private final RtpRegions rtpRegions;
   private boolean swimmingToSurface;
   private AnchorTarget anchorTarget;
   private final LongOpenHashSet failedStandKeys;
   private HunterState currentState;
   private long miningStartedMs;
   private boolean playerNearby;
   private final List<BlockPos> pendingAnchorPositions;
   private long rtpArrivalMs;
   private long lastSurfacePathTick;

   private String formatTargetInfo() {
      double sqrt = Math.sqrt(mc.player.squaredDistanceTo(Vec3d.ofCenter(this.targetAnchorPos)));
      int appendLimit = this.anchorTarget == null ? 0 : this.anchorTarget.charges();
      return Math.round(sqrt) + " m" + (appendLimit > 0 ? "  " + appendLimit + (appendLimit == 1 ? " charge" : " charges") : "");
   }

   private void resetRtpTracking() {
      this.posAtRtpSend = null;
      this.rtpWorld = null;
      this.entityPosIgnoreUntilMs = -2305843009213693952L;
   }

   private void abandonTarget(long anchorBreakerValue, String anchorBreakerText) {
      if (this.targetAnchorPos != null) {
         this.skippedAnchorKeys.add(this.targetAnchorPos.asLong());
      }

      this.hunterStats.recordSkipReason(anchorBreakerText);
      this.anchorBreaker.cancelBreaking();
      this.clearTarget();
      this.safeAnchorTargets.removeIf(anchorTarget -> this.skippedAnchorKeys.contains(BlockPos.asLong(anchorTarget.x(), anchorTarget.y(), anchorTarget.z())));
      this.safeAnchorScanStarted = false;
      this.transitionTo(HunterState.PICK, anchorBreakerText);
   }

   private void queueChatMessage(String atomicIntegerText) {
      if (atomicIntegerText != null && !atomicIntegerText.isBlank()) {
         if (this.pendingMessageCount.get() < 64) {
            this.incomingMessageQueue.offer(atomicIntegerText);
            this.pendingMessageCount.incrementAndGet();
         }
      }
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      if (autoVanish != null && autoVanish.packet != null) {
         if (!(autoVanish.packet instanceof BundlePacket bundlePacket)) {
            this.collectPacketText(autoVanish.packet);
         } else {
            for (Packet packet : (Iterable<net.minecraft.network.packet.Packet>) (bundlePacket.getPackets())) {
               this.collectPacketText(packet);
            }
         }
      }
   }

   private void tickCooldownIdle(long rtpReadyValue, boolean rtpReadyFlag) {
      if (this.rtpClock.canSendAt(rtpReadyValue)) {
         this.transitionTo(HunterState.RTP_SEND, "rtp ready");
         this.tickRtpSend(rtpReadyValue, rtpReadyFlag);
      } else {
         if (this.huntTick % 60L == 0L) {
            ClientPlayerEntity player = mc.player;
            this.hunterBody
               .rotateTowardYaw(player.getYaw() + (float)(Math.sin(this.huntTick * 0.013) * 6.0), Math.max(-10.0F, Math.min(20.0F, player.getPitch())), 0);
         }
      }
   }

   private boolean tryFleeFromMobs(long rtpHunterValue) {
      ClientPlayerEntity player = mc.player;
      float health = player.getHealth();
      if (health > 6.0F) {
         return false;
      } else {
         var arrayList = new ArrayList();
         Box expand = player.getBoundingBox().expand(8.0);

         for (Entity entity : mc.world.getOtherEntities(player, expand, entity2 -> entity2.isAlive() && entity2.getType().getSpawnGroup() == SpawnGroup.MONSTER)) {
            if (entity.squaredDistanceTo(player) <= 64.0) {
               arrayList.add(new Threat(entity.getX(), entity.getZ()));
            }
         }

         if (arrayList.isEmpty()) {
            return false;
         } else if (health <= 3.0F && !ThreatPlan.wasPlayerHitRecently(this.lastPlayerCombatMs < 0L ? -1L : rtpHunterValue - this.lastPlayerCombatMs)) {
            this.hunterStats.logoutCount++;
            this.stopAndDisconnect("Anchor Hunter - low health with mobs nearby");
            return true;
         } else if (this.playerNearby) {
            return false;
         } else {
            this.anchorBreaker.cancelBreaking();
            if (this.rtpClock.hasRequestTimedOut(rtpHunterValue)) {
               this.rtpRegions.incrementRegionCount(this.east);
               if (this.rtpClock.recordFailureAndCheckLimit(rtpHunterValue)) {
                  this.stopHunt("/rtp did not answer 5 times in a row - stopped.");
                  return true;
               }

               if (this.currentState == HunterState.RTP_WAIT || this.currentState == HunterState.HOLD_STILL) {
                  this.transitionTo(HunterState.COOLDOWN_IDLE, "rtp silent");
               }
            }

            if (this.rtpClock.canSendAt(rtpHunterValue)) {
               this.sendRtpCommand(rtpHunterValue);
               return true;
            } else {
               if (this.resumed.moveAllowed()) {
                  this.fleeFromThreats(arrayList);
               }

               if (this.fleeWaypoint == null || !this.resumed.moveAllowed()) {
                  this.tryEatFood(this.resumed.level() == Level.RUN);
               }

               return true;
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      this.statusSnapshot = Snapshot.INSTANCE;
      this.reenableStoppedModules();
      if (mc != null && this.hunterBody != null) {
         try {
            this.anchorBreaker.cancelBreaking();
            this.enderStash.cleanup();
            this.hunterBody.endTick();
         } finally {
            InventoryStackUtilsHandle.handle(this);
         }
      }
   }

   private void storeNearbyThreats(List<com.corz.client.utils.PlayerWatch.Threat> list) {
      this.nearbyThreats.clear();

      for (com.corz.client.utils.PlayerWatch.Threat threat : list) {
         this.nearbyThreats.add(new Threat(threat.x(), threat.z()));
      }
   }

   private void tickSettle(long overworldSentTime) {
      ClientPlayerEntity player = mc.player;
      boolean overworldSentFlag = mc.world.getRegistryKey() == World.OVERWORLD;
      if (!overworldSentFlag) {
         if (this.leftOverworldSinceMs < 0L) {
            this.leftOverworldSinceMs = overworldSentTime;
         }

         if (overworldSentTime - this.leftOverworldSinceMs > 10000L) {
            this.stopHunt("The /rtp sent you out of the overworld - stopped.");
         }
      } else {
         this.leftOverworldSinceMs = -1L;
         boolean approachPlanFlag = player.isOnGround() || player.isTouchingWater();
         double throwAnchor = this.getScanCoverage();
         if (ApproachPlan.shouldAbortApproach(approachPlanFlag, overworldSentTime, this.anchorScanner.getLastChunkPacketTick(), this.lastTeleportMs, throwAnchor, overworldSentTime - this.stateStartMs)) {
            this.transitionTo(HunterState.SCAN, "landed cov=" + Math.round(throwAnchor * 100.0));
         }
      }
   }

   private void swapFoodIntoHotbar() {
      if (!ExtUtils.isAction()) {
         ClientPlayerEntity player = mc.player;
         if (player.currentScreenHandler == player.playerScreenHandler) {
            PlayerInventory inventory = player.getInventory();

            for (int i = 9; i <= 35; i++) {
               if (isSafeFood(inventory.getStack(i))) {
                  if (!InventoryStackUtilsHandle.canFlagForObject(this)) {
                     return;
                  }

                  int stackValue = i;
                  int stackValue2 = -1;

                  for (int j = 0; j <= 8; j++) {
                     if (inventory.getStack(j).isEmpty()) {
                        stackValue2 = j;
                        break;
                     }
                  }

                  if (stackValue2 < 0) {
                     stackValue2 = inventory.getSelectedSlot() == 8 ? 7 : 8;
                  }

                  int elytraAnchorValue = stackValue2;
                  ExtUtils.handleExt(
                     () -> {
                        ClientPlayerEntity var2x = mc.player;
                        if (var2x != null && mc.interactionManager != null && var2x.currentScreenHandler == var2x.playerScreenHandler) {
                           if (isSafeFood(var2x.getInventory().getStack(stackValue))) {
                              if (!AnchorBreaker.isUsableMiningPickaxe(var2x.getInventory().getStack(elytraAnchorValue))) {
                                 mc.interactionManager
                                    .clickSlot(
                                       var2x.playerScreenHandler.syncId,
                                       HotbarSlotUtils.toScreenSlot(stackValue),
                                       HotbarSlotUtils.requireHotbarSlot(elytraAnchorValue),
                                       SlotActionType.SWAP,
                                       var2x
                                    );
                              }
                           }
                        }
                     },
                     true
                  );
                  return;
               }
            }
         }
      }
   }

   private boolean tryThrowPearl() {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.interactionManager != null && player.getMainHandStack().isOf(Items.ENDER_PEARL)) {
         ActionResult interactItem = mc.interactionManager.interactItem(player, Hand.MAIN_HAND);
         if (interactItem instanceof Success success && success.swingSource() == SwingSource.CLIENT) {
            player.swingHand(Hand.MAIN_HAND);
         }

         return interactItem.isAccepted();
      } else {
         return false;
      }
   }

   private void tickRtpWait(long rtpAnswerValue) {
      if (this.rtpClock.hasRequestTimedOut(rtpAnswerValue)) {
         this.rtpRegions.incrementRegionCount(this.east);
         if (this.rtpClock.recordFailureAndCheckLimit(rtpAnswerValue)) {
            this.stopHunt("/rtp did not answer 5 times in a row - stopped.");
         } else {
            this.transitionTo(HunterState.COOLDOWN_IDLE, "rtp silent");
         }
      } else {
         if (!this.rtpClock.isRequestPending()) {
            this.transitionTo(HunterState.COOLDOWN_IDLE, "rtp refused");
         }
      }
   }

   private void swimTowardSurface(ClientPlayerEntity player) {
      BlockPos blockPos = this.digMover.getCurrentFeetBlock(player);
      int swimmingAirLimit = this.surfacePath == null ? -1 : this.surfacePath.indexOf(blockPos);
      if (this.huntTick - this.lastSurfacePathTick >= 4L || this.surfacePath != null && swimmingAirLimit < 0) {
         this.lastSurfacePathTick = this.huntTick;
         this.surfacePath = AirPlan.findPathToAirPocket(this.mcCells, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 8000);
         swimmingAirLimit = this.surfacePath == null ? -1 : this.surfacePath.indexOf(blockPos);
      }

      if (this.surfacePath != null && swimmingAirLimit >= 0) {
         BlockPos swimmingairitem = this.surfacePath.get(Math.min(swimmingAirLimit + 1, this.surfacePath.size() - 1));
         if (swimmingairitem.getX() == blockPos.getX() && swimmingairitem.getZ() == blockPos.getZ()) {
            this.hunterBody.rotateTowardYaw(player.getYaw(), swimmingairitem.getY() < blockPos.getY() ? 20.0F : -30.0F, 2);
         } else {
            float degrees = this.hunterBody.getYawToward(Vec3d.ofBottomCenter(swimmingairitem));
            this.hunterBody.rotateTowardYaw(degrees, swimmingairitem.getY() >= blockPos.getY() ? -15.0F : 20.0F, 2);
            if (Math.abs(MathHelper.wrapDegrees(degrees - player.getYaw())) < 50.0F) {
               this.hunterBody.pressKeys(1);
            }
         }

         if (swimmingairitem.getY() < blockPos.getY()) {
            this.hunterBody.pressKeys(32);
         } else {
            this.hunterBody.pressKeys(16);
         }
      } else {
         this.hunterBody.pressKeys(16);
         this.hunterBody.rotateTowardYaw(player.getYaw(), -30.0F, 2);
      }
   }

   public boolean isMiningAnchor() {
      return this.isEnabled() && this.breakingAnchor && this.anchorBreaker != null && this.anchorBreaker.isReadyToBreak();
   }

   private void createComponentsIfNeeded() {
      if (this.hunterBody == null) {
         this.hunterBody = new HunterBody();
         this.anchorBreaker = new AnchorBreaker(this.hunterBody, this);
         this.digMover = new DigMover(this.hunterBody, this.anchorBreaker, this.mcCells);
         this.surfaceWalker = new SurfaceWalker(this.hunterBody);
         this.enderStash = new EnderStash(this.hunterBody, this.anchorBreaker, this.digMover, this);
         this.hunterStatusCard = new HunterStatusCard();
         this.pearlHop = new PearlHop(this.hunterBody, this.mcCells, this);
         this.hunterMarks = new HunterMarks();
      }
   }

   @Override
   public Snapshot getStatusSnapshot() {
      return this.isEnabled() ? this.statusSnapshot : Snapshot.INSTANCE;
   }

   private void runHuntTick(long hunterBodyValue) {
      ClientPlayerEntity player = mc.player;
      ClientWorld world = mc.world;
      this.huntTick++;
      ClientConnection connection = mc.getNetworkHandler().getConnection();
      if (this.sessionConnection == null) {
         this.sessionConnection = connection;
         this.networkHandler.beginSession(connection, hunterBodyValue);
      } else if (connection != this.sessionConnection) {
         this.stopHunt("You left the server - Anchor Hunter stopped.");
         return;
      }

      if (this.pendingStopReason != null) {
         String hunterAnchorText = this.pendingStopReason;
         this.pendingStopReason = null;
         this.stopHunt(hunterAnchorText);
      } else {
         this.hunterBody.beginTick(this.huntTick);
         this.anchorBreaker.endTick();
         this.mcCells.resetIfStale(world, this.huntTick);
         this.breakingAnchor = false;

         try {
            if (!player.isAlive() || player.isDead() || mc.currentScreen instanceof DeathScreen) {
               this.stopHunt("You died - Anchor Hunter stopped.");
               return;
            }

            String formatText = this.getConflictingModuleName();
            if (formatText != null) {
               this.stopHunt(
                  this.currentState == HunterState.PREFLIGHT ? "Turn off " + formatText + " first." : formatText + " was turned on - Anchor Hunter stopped."
               );
               return;
            }

            this.processDamageEvents(hunterBodyValue);
            this.anchorScanner.processPendingUpdates(world);
            this.detectTeleport(hunterBodyValue);
            this.processChatMessages(hunterBodyValue);
            if (!this.isEnabled()) {
               return;
            }

            this.playerInputTracker.finishTick(hunterBodyValue, this.isUserHoldingControls());
            if (this.pearlHop.getState() == State.FLYING) {
               this.networkHandler.setExpectedPearlTeleportMs(hunterBodyValue);
               this.pearlHop.updateAndGetAct(this.huntTick, this.targetAnchorPos, List::of);
            }

            this.resumed = this.networkHandler.evaluateProbe(this.buildSafetyProbe(hunterBodyValue));
            if (this.resumed.level() == Level.STOP) {
               this.stopHunt(this.resumed.reason().statusMessage);
               return;
            }

            this.accumulateHuntTime(hunterBodyValue);
            if (this.currentState == HunterState.PREFLIGHT) {
               this.tickPreflight(hunterBodyValue);
               return;
            }

            var seenThreat = this.playerWatch
               .updateThreats(
                  this.huntTick,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  this.collectOtherPlayers(player),
                  this.playerRange.getValue(),
                  this.ignoreFriends.isEnabled()
               );
            this.hunterStats.playersMetCount = this.playerWatch.getCurrentNameCount();
            this.threatVisible = !seenThreat.isEmpty();
            if (this.threatVisible) {
               com.corz.client.utils.PlayerWatch.Threat threat = (com.corz.client.utils.PlayerWatch.Threat)seenThreat.get(0);
               this.nearbyPlayerLabel = threat.name() + "@" + (int)Math.round(threat.dist());
               this.storeNearbyThreats(seenThreat);
               if (!this.playerNearby) {
                  this.playerNearby = true;
                  this.sendNotification(AnchorHunter.Kind.WARN, "Player " + threat.name() + " is " + (int)Math.round(threat.dist()) + " blocks away - leaving.");
               }
            } else if (!this.playerNearby) {
               this.nearbyPlayerLabel = "";
            }

            if (this.resumed.moveAllowed() && this.manageAirSupply(hunterBodyValue)) {
               return;
            }

            if (this.tryFleeFromMobs(hunterBodyValue)) {
               return;
            }

            if (this.playerNearby) {
               this.escapeFromPlayer(hunterBodyValue);
               return;
            }

            if (this.resumed.level() == Level.PAUSED) {
               this.pauseHunt();
               return;
            }

            if (this.currentState == HunterState.PAUSED) {
               this.transitionTo(this.resumeState == HunterState.MINE ? HunterState.APPROACH : this.resumeState, "resumed");
            }

            boolean hunterBodyFlag = this.resumed.level() == Level.RUN;
            this.anchorScanner.updateScan(world, player.getBlockX() >> 4, player.getBlockZ() >> 4);
            if (!this.isAnchorWorkState() || !ApproachPlan.isTimeoutReached(hunterBodyValue - this.rtpArrivalMs)) {
               if (this.tryEatFood(hunterBodyFlag)) {
                  return;
               }

               this.dispatchStateTick(hunterBodyValue, hunterBodyFlag);
               return;
            }

            this.skipToNextRtp(hunterBodyValue, "spot timeout");
         } finally {
            if (this.isEnabled()) {
               this.updateGhostsSkips();
               if (this.currentState != HunterState.STASH && !ExtUtils.isAction()) {
                  InventoryStackUtilsHandle.handle(this);
               }

               this.hunterBody.updateRotation();
               this.hunterBody.releaseStaleKeys();
               this.anchorBreaker.cancelIfIdle();
               this.updateStatusSnapshot(hunterBodyValue);
            }
         }
      }
   }

   private void sendRtpCommand(long rtpCombatTime) {
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      if (networkHandler != null && mc.player != null) {
         this.anchorBreaker.cancelBreaking();
         String east = this.rtpRegions.pickRegion(this.region.getCurrentOption());
         if (!RtpRegions.isKnownRegion(east)) {
            east = "east";
         }

         this.east = east;
         networkHandler.sendChatCommand("rtp " + east);
         this.rtpClock.onRequestSent(rtpCombatTime);
         this.networkHandler.setExpectedPearlTeleportMs(rtpCombatTime);
         this.posAtRtpSend = mc.player.getEntityPos();
         this.rtpWorld = mc.world;
         this.lastRtpSentMs = rtpCombatTime;
         this.entityPosIgnoreUntilMs = rtpCombatTime + 30000L;
         this.rtpWarmupActive = false;
         this.hunterStats.rtpCount++;
         if (!this.playerNearby) {
            this.transitionTo(HunterState.RTP_WAIT, "rtp " + east);
         }
      }
   }

   private void accumulateHuntTime(long joinWorldTime) {
      boolean hunterFlag = this.currentState != HunterState.PAUSED && this.currentState != HunterState.PREFLIGHT;
      if (this.lastTickMs >= 0L) {
         long hunterValue = Math.max(0L, Math.min(1000L, joinWorldTime - this.lastTickMs));
         if (hunterFlag) {
            this.hunterStats.totalHuntTimeMs += hunterValue;
            if (this.currentState.isAnchorMiningPhase()) {
               this.hunterStats.anchorsErrsCount += hunterValue;
            } else {
               this.hunterStats.hunterCount += hunterValue;
            }
         }
      }

      this.lastTickMs = joinWorldTime;
   }

   private void tickPreflight(long startedTeleportedTime) {
      ClientPlayerEntity player = mc.player;
      if (mc.world.getRegistryKey() != World.OVERWORLD) {
         this.stopHunt("Go to the overworld first - Anchor Hunter only works there.");
      } else if (AnchorBreaker.countUsablePickaxes(player.getInventory()) == 0) {
         this.stopHunt("Anchor Hunter needs a diamond or netherite pickaxe with durability left.");
      } else if (this.findFoodSlot() < 0 && player.getHungerManager().getFoodLevel() < 10) {
         this.stopHunt("Bring some food - you are hungry and have nothing to eat.");
      } else if (EnderStash.countEmptySlots(player) != 0
         || EnderStash.hasRoomForAnchor(player)
         || this.enderChestStash.isEnabled() && EnderStash.hasChestAndSilkPickaxe(player, this.anchorBreaker)) {
         if (this.resumed == null || this.resumed.level() != Level.PAUSED || this.resumed.reason() != com.corz.client.utils.BotRunSupervisor.Reason.ARMING) {
            this.sendNotification(AnchorHunter.Kind.INFO, "Started.");
            this.rtpArrivalMs = startedTeleportedTime;
            this.hunterStats.spotCount = 0;
            this.transitionTo(HunterState.RTP_SEND, "start");
         }
      } else {
         this.stopHunt("Your inventory is full - make room first.");
      }
   }

   private AnchorTarget evaluateAnchorTarget(BlockPos pos, BlockState state, Sight sight, int cellsValue) {
      this.mcCells.scanTntMinecarts(pos.getX(), pos.getY(), pos.getZ(), 4);
      Result sees = StandFinder.scanStands(this.mcCells, pos.getX(), pos.getY(), pos.getZ(), sight);
      com.corz.client.utils.AnchorSafety.Verdict lavaUnreachable = AnchorSafety.evaluateAnchorSafety(
         this.mcCells, pos.getX(), pos.getY(), pos.getZ(), cellsValue, sees, this.digToHiddenAnchors.isEnabled(), this.protectedAreaDetected
      );
      Vec3d eyePos = mc.player.getEyePos();
      return AnchorTarget.create(
         pos.getX(), pos.getY(), pos.getZ(), AnchorScanner.getCharges(state), eyePos.x, eyePos.y, eyePos.z, cellsValue, sees, lavaUnreachable, this.digToHiddenAnchors.isEnabled()
      );
   }

   private void tickMine(long sightCancelledTime, boolean anchorBrokenFlag) {
      ClientPlayerEntity player = mc.player;
      if (this.targetAnchorPos == null) {
         this.transitionTo(HunterState.PICK, "no target");
      } else {
         ClientWorld world = mc.world;
         BlockState blockState = world.getBlockState(this.targetAnchorPos);
         if (!blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
            this.anchorBreaker.cancelBreaking();
            if (!this.anchorMissing) {
               this.anchorMissing = true;
               this.anchorMissingTicks = 0;
            }

            this.anchorMissingTicks++;
            if (!FloorMathUtilsBlock.canEmpty(world, this.targetAnchorPos) || this.anchorMissingTicks > 20) {
               if (this.anchorMissingTicks >= 3) {
                  this.anchorBrokenTick = this.huntTick;
                  this.anchorScanner.removeAnchorAt(this.targetAnchorPos);
                  this.transitionTo(HunterState.COLLECT, "anchor broken");
               }
            }
         } else {
            if (this.anchorMissing) {
               this.anchorMissing = false;
               this.hunterStats.ghostAnchorCount++;
               this.anchorReappearCount++;
               if (this.anchorReappearCount >= 2) {
                  this.transitionTo(HunterState.PROTECTED, "anchor came back twice");
                  return;
               }
            }

            if (!EnderStash.hasRoomForAnchor(player)) {
               this.handleInventoryFull(sightCancelledTime);
            } else if (this.huntTick - this.mineStartTick > 900L) {
               this.abandonTarget(sightCancelledTime, "mine timeout");
            } else if (player.getEyePos().distanceTo(Vec3d.ofCenter(this.targetAnchorPos)) > 4.5) {
               this.transitionTo(HunterState.APPROACH, "out of reach");
            } else {
               BreakStatus hunterBody = this.anchorBreaker.tickBreakTarget(this.targetAnchorPos, false);
               switch (hunterBody) {
                  case NO_TOOL:
                     this.stopHunt(
                        "No diamond or netherite pickaxe with durability left - " + this.hunterStats.anchorsMinedCount + " anchors collected."
                     );
                     break;
                  case NO_SIGHT:
                     this.failedStandKeys.add(this.hunterBody.getFeetBlock().asLong());
                     if (this.failedStandKeys.size() >= 3) {
                        this.abandonTarget(sightCancelledTime, "no sight");
                        return;
                     }

                     this.candidateStandPositions.clear();
                     this.transitionTo(HunterState.APPROACH, "lost sight");
                     break;
                  case WORKING:
                     if (anchorBrokenFlag) {
                        this.anchorBreaker.setBreakRequested(true);
                        this.breakingAnchor = true;
                        this.miningStartedMs = sightCancelledTime;
                     }
               }
            }
         }
      }
   }

   public HunterStats getHunterStats() {
      return this.hunterStats;
   }

   private void processDamageEvents(long mobsHunterTime) {
      ClientPlayerEntity player = mc.player;

      int[] mobsConcurrentValues;
      while ((mobsConcurrentValues = this.damageEventQueue.poll()) != null) {
         if (mobsConcurrentValues[0] == player.getId()) {
            Entity entity = mobsConcurrentValues[1] > 0 ? mc.world.getEntityById(mobsConcurrentValues[1]) : null;
            if (entity instanceof PlayerEntity && entity != player) {
               this.lastPlayerCombatMs = mobsHunterTime;
            }
         }
      }
   }

   private void updateGhostsSkips() {
      int value = this.digMover.getBlocksDugCount();
      if (value < this.lastGhostsSkipsCount) {
         this.lastGhostsSkipsCount = 0;
      }

      if (value > this.lastGhostsSkipsCount) {
         this.hunterStats.anchorsDugCount = this.hunterStats.anchorsDugCount + (value - this.lastGhostsSkipsCount);
         this.lastGhostsSkipsCount = value;
      }
   }

   private void reenableStoppedModules() {
      if (!this.disabledModules.isEmpty()) {
         var arrayList = new ArrayList<>(this.disabledModules);
         this.disabledModules.clear();
         if (!Panic.isActivatedPanic() && !DirExtractionUtils.isFlag()) {
            try (AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled()) {
               for (Module arrayItem : arrayList) {
                  if (!arrayItem.isEnabled()) {
                     arrayItem.setEnabled(true);
                  }
               }
            } catch (Exception e) {
            }
         }
      }
   }

   private void pauseHunt() {
      if (this.currentState != HunterState.PAUSED) {
         this.resumeState = this.currentState == HunterState.STASH ? HunterState.PICK : this.currentState;
         this.anchorBreaker.cancelBreaking();
         if (this.enderStash.shouldCloseChestScreen()) {
            this.enderStash.cleanup();
         }

         this.hunterBody.endTick();
         this.transitionTo(HunterState.PAUSED, this.resumed.reason().statusMessage);
      }
   }

   private boolean isAnchorWorkState() {
      return this.currentState == HunterState.SCAN
         || this.currentState == HunterState.PICK
         || this.currentState == HunterState.APPROACH
         || this.currentState == HunterState.MINE
         || this.currentState == HunterState.COLLECT;
   }

   @Override
   protected void onPlayerScaffold(PlayerScaffold playerScaffold) {
      if (playerScaffold != null && mc != null && mc.currentScreen == null && mc.mouse != null) {
         if (mc.mouse.isCursorLocked() && mc.isWindowFocused() && !Freelook.isFlag() && !Freecam.isInstanceFlag()) {
            this.playerInputTracker.addMouseMovement(playerScaffold.getAmount(), playerScaffold.getPlayerScaffoldAmount());
         }
      }
   }

   private boolean isUserHoldingControls() {
      if (mc.currentScreen == null && mc.options != null) {
         for (KeyBinding keyBinding : HunterBody.getControlKeys(mc.options)) {
            if (CodeCategoryUtils.isKeyPressed(keyBinding)) {
               return true;
            }
         }

         for (KeyBinding keyBinding2 : mc.options.hotbarKeys) {
            if (CodeCategoryUtils.isKeyPressed(keyBinding2)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void selectTarget(long unreachableGoneTime, AnchorTarget anchorTarget) {
      this.anchorTarget = anchorTarget;
      this.targetAnchorPos = new BlockPos(anchorTarget.x(), anchorTarget.y(), anchorTarget.z());
      this.targetSelectedMs = unreachableGoneTime;
      this.nearAnchorTarget = false;
      this.anchorReappearCount = 0;
      this.anchorMissing = false;
      this.anchorMissingTicks = 0;
      this.targetHasCharges = anchorTarget.charges() > 0;
      this.candidateStandPositions.clear();
      this.failedStandKeys.clear();
      this.digMover.resetAll();
      this.surfaceWalker.reset();
      this.pearlHop.reset();
      this.digMover.setNoDigBlock(this.targetAnchorPos);
      this.transitionTo(HunterState.APPROACH, "target " + this.targetAnchorPos.toShortString());
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (queueCommand != null && this.hunterMarks != null && this.digMover != null && mc.player != null) {
         var list = this.digMover.wasUpdatedRecently(this.huntTick) ? this.digMover.getRemainingPathFeet() : null;
         Shot shot = this.pearlHop != null && this.pearlHop.getState() == State.AIM ? this.pearlHop.getShot() : null;
         this.hunterMarks
            .submitTarget(queueCommand.tickProgress, this.showPath.isEnabled(), this.targetAnchorPos, list, list == null ? null : this.digMover.getPathEnd(), shot);
      }
   }

   private int findFoodSlot() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i <= 35; i++) {
         if (isSafeFood(inventory.getStack(i))) {
            return i;
         }
      }

      return -1;
   }

   private void skipToNextRtp(long rtpBreakerValue, String rtpBreakerText) {
      this.anchorBreaker.cancelBreaking();
      this.clearTarget();
      this.transitionTo(this.rtpClock.canSendAt(rtpBreakerValue) ? HunterState.RTP_SEND : HunterState.COOLDOWN_IDLE, rtpBreakerText);
   }

   private void tickStash(long stashedStashValue, boolean stashedStashFlag) {
      if (stashedStashFlag) {
         com.corz.client.utils.EnderStash.StashStatus enderChest = this.enderStash.tick(this.huntTick);
         if (this.anchorBreaker.getTargetBlock() != null) {
            this.breakingAnchor = true;
         }

         if (enderChest == com.corz.client.utils.EnderStash.StashStatus.DONE) {
            this.sendNotification(AnchorHunter.Kind.GOOD, this.enderStash.getAnchorsStoredCount() + " anchors stored in your ender chest.");
            this.transitionTo(this.targetAnchorPos != null ? HunterState.APPROACH : HunterState.PICK, "stashed");
         } else if (enderChest == com.corz.client.utils.EnderStash.StashStatus.FAILED) {
            this.stopHunt("Inventory full - " + this.hunterStats.anchorsMinedCount + " anchors collected (" + this.enderStash.getFailureReason() + ").");
         }
      }
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      if (tickProgress != null && mc.currentScreen == null && !mc.options.hudHidden && mc.player != null) {
         if (this.hunterMarks != null && this.showPath.isEnabled() && this.targetAnchorPos != null) {
            this.hunterMarks.renderTargetLabel(tickProgress.drawContext, this.targetAnchorPos, "Anchor", this.formatTargetInfo());
         }

         if (this.hunterStatusCard != null && this.showStatusCard.isEnabled()) {
            this.renderStatusCard(tickProgress.drawContext);
         }
      }
   }

   @Override
   protected void onEnable() {
      this.hunterStats.resetAll();
      this.rtpClock.reset();
      this.rtpRegions.reset();
      this.playerWatch.reset();
      this.playerWatch.clearCurrentNames();
      this.playerInputTracker.reset();
      this.anchorScanner.reset();
      this.incomingMessageQueue.clear();
      this.damageEventQueue.clear();
      this.pendingMessageCount.set(0);
      this.skippedAnchorKeys.clear();
      this.safeAnchorTargets.clear();
      this.pendingAnchorPositions.clear();
      this.candidateStandPositions.clear();
      this.currentState = HunterState.PREFLIGHT;
      this.resumeState = HunterState.SCAN;
      this.huntTick = 0L;
      this.stateStartTick = 0L;
      this.stateStartMs = 0L;
      this.statusDetail = "";
      this.sessionConnection = null;
      this.resumed = null;
      this.breakingAnchor = false;
      this.east = null;
      this.posAtRtpSend = null;
      this.lastPlayerPos = null;
      this.rtpWorld = null;
      this.lastRtpSentMs = -2305843009213693952L;
      this.entityPosIgnoreUntilMs = -2305843009213693952L;
      this.lastTeleportMs = -2305843009213693952L;
      this.rtpWarmupActive = false;
      this.leftOverworldSinceMs = -1L;
      this.protectedAreaDetected = false;
      this.safeAnchorScanStarted = false;
      this.clearTarget();
      this.playerNearby = false;
      this.threatVisible = false;
      this.nearbyPlayerLabel = "";
      this.fleeWaypoint = null;
      this.fleeStartRecorded = false;
      this.nearbyThreats.clear();
      this.lastPlayerCombatMs = -1L;
      this.air = false;
      this.eatCooldownUntilTick = 0L;
      this.lastGhostsSkipsCount = 0;
      this.failedStandKeys.clear();
      this.lastTickMs = -1L;
      this.consecutiveErrorCount = 0;
      this.lastNotificationText = "";
      this.pendingStopReason = mc != null && mc.player != null && mc.world != null ? null : "Join a world first.";
      if (mc != null) {
         this.createComponentsIfNeeded();
         this.hunterStatusCard.resetAnimation();
         this.digMover.resetAll();
         this.surfaceWalker.reset();
         this.pearlHop.reset();
         this.nearAnchorTarget = false;
         this.swimmingToSurface = false;
         this.airPanic = false;
         this.surfacePath = null;
         this.lastSurfacePathTick = -2305843009213693952L;
         this.statusSnapshot = new Snapshot(Tone.IDLE, HunterState.PREFLIGHT.displayText, null, 0, 0, this.hunterStats.getSummaryText());
         if (this.pendingStopReason == null) {
            this.disableConflictingModules();
         }
      }
   }

   private void stopHunt(String hunterStoppedText) {
      this.statusDetail = hunterStoppedText == null ? "" : hunterStoppedText;
      this.currentState = HunterState.STOPPED;
      if (this.isEnabled()) {
         AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled();

         try {
            this.setEnabled(false);
         } finally {
            try {
               disabledEnabled.close();
            } catch (Exception e) {
               this.statusDetail = hunterStoppedText == null ? "" : hunterStoppedText;
            }
         }

         if (hunterStoppedText != null && !hunterStoppedText.isBlank()) {
            String hunterStoppedText2 = this.hunterStats.anchorsMinedCount <= 0 && this.hunterStats.rtpCount <= 0 ? "" : " " + this.hunterStats.getSummaryText() + ".";
            this.sendNotification(AnchorHunter.Kind.STOP, hunterStoppedText + hunterStoppedText2);
         }
      }
   }

   public AnchorHunter() {
      super("Anchor Hunter", IconsPngType.DONUTSMP);
      this.playerRange = new IntegerSetting(
         "Player Range",
         "Any player closer than this makes the bot leave right away. The server only shows players up to about 48 to 128 blocks, so the default means anyone you can see.",
         96,
         8,
         160
      );
      this.ignoreFriends = new BooleanSetting("Ignore Friends", "Friends from your Friends list do not make the bot leave.", true);
      this.logOutIfCaught = new BooleanSetting(
         "Log Out If Caught",
         "If a player gets within 5 blocks while /rtp is still cooling down, log out instead of running. Never logs out while you are in combat.",
         false
      );
      this.digToHiddenAnchors = new BooleanSetting(
         "Dig To Hidden Anchors", "Dig a normal tunnel to anchors inside walls or underground. Off = only take anchors you can walk up to.", true
      );
      this.pearlTravel = new BooleanSetting(
         "Pearl Travel", "Throw ender pearls to reach far anchors fast. Only when you wear armour and have enough health.", true
      );
      this.enderChestStash = new BooleanSetting(
         "Ender Chest Stash",
         "When your inventory is full, put the anchors into your ender chest. Needs an ender chest and a Silk Touch pickaxe to pick it back up.",
         true
      );
      this.stopAfter = new IntegerSetting("Stop After", "Stop after this many anchors (0 = never stop).", 0, 0, 2304);
      this.notificationMode = new EnumSetting("Notifications", "How the bot tells you about finds and problems.", "Toast", "Toast", "Chat", "Off");
      this.showStatusCard = new BooleanSetting("Status Card", "Show the small Anchor Hunter card on screen.", true);
      this.showPath = new BooleanSetting("Show Path", "Draw the route the bot walks, a mark on the anchor it is going to and its pearl throws.", true);
      this.hunterStats = new HunterStats();
      this.rtpClock = new RtpClock();
      this.rtpRegions = new RtpRegions();
      this.playerWatch = new PlayerWatch();
      this.networkHandler = new BotRunSupervisor();
      this.playerInputTracker = new UserOverrideTracker();
      this.mcCells = new McCells();
      this.anchorScanner = new AnchorScanner();
      this.incomingMessageQueue = new ConcurrentLinkedQueue<>();
      this.damageEventQueue = new ConcurrentLinkedQueue<>();
      this.pendingMessageCount = new AtomicInteger();
      this.skippedAnchorKeys = new LongOpenHashSet();
      this.safeAnchorTargets = new ArrayList<>();
      this.pendingAnchorPositions = new ArrayList<>();
      this.candidateStandPositions = new HashSet<>();
      this.disabledModules = new ArrayList<>();
      this.lastSurfacePathTick = -2305843009213693952L;
      this.currentState = HunterState.PREFLIGHT;
      this.resumeState = HunterState.SCAN;
      this.statusDetail = "";
      this.lastRtpSentMs = -2305843009213693952L;
      this.entityPosIgnoreUntilMs = -2305843009213693952L;
      this.lastTeleportMs = -2305843009213693952L;
      this.leftOverworldSinceMs = -1L;
      this.miningStartedMs = -2305843009213693952L;
      this.lastCoverageTick = -2305843009213693952L;
      this.nearbyPlayerLabel = "";
      this.lastWaypointTick = -2305843009213693952L;
      this.nearbyThreats = new ArrayList<>();
      this.lastPlayerCombatMs = -1L;
      this.failedStandKeys = new LongOpenHashSet();
      this.lastTickMs = -1L;
      this.statusSnapshot = Snapshot.INSTANCE;
      this.lastNotificationText = "";
      this.lastNotificationTick = -2305843009213693952L;
      this.addSettings(
         new ConfigSetting[]{
            this.region,
            this.playerRange,
            this.ignoreFriends,
            this.logOutIfCaught,
            this.digToHiddenAnchors,
            this.pearlTravel,
            this.enderChestStash,
            this.stopAfter,
            this.notificationMode,
            this.showStatusCard,
            this.showPath
         }
      );
   }

   public void renderStatusCard(DrawContext context) {
      if (context != null && this.hunterStatusCard != null && this.showStatusCard.isEnabled() && mc.player != null) {
         long leavingHunterValue = ConnectionTimeUtils.getTimeTimeMillis();
         String leavingHunterText = this.rtpClock.isRequestPending() ? "..." : (this.rtpClock.canSendAt(leavingHunterValue) ? "ready" : (this.rtpClock.getWaitRemainingMs(leavingHunterValue) + 999L) / 1000L + "s");
         this.hunterStatusCard
            .renderCard(
               context,
               this.statusSnapshot,
               this.hunterStats.anchorsMinedCount,
               this.hunterStats.getAnchorsPerHour(),
               leavingHunterText,
               !this.threatVisible && !this.playerNearby ? "" : "PLAYER - leaving"
            );
      }
   }

   private List<BlockPos> findPearlLandingSpots() {
      var arrayList = new ArrayList();
      BlockPos blockPos = this.targetAnchorPos;
      if (blockPos == null) {
         return arrayList;
      } else {
         Result sees = StandFinder.scanStands(this.mcCells, blockPos.getX(), blockPos.getY(), blockPos.getZ(), McViews.getSight(this.hunterBody));

         for (Stand stand : sees.exposed()) {
            if (arrayList.size() >= 3) {
               return arrayList;
            }

            if (McViews.isSafeStandSpot(this.mcCells, stand.x(), stand.y(), stand.z())) {
               arrayList.add(new BlockPos(stand.x(), stand.y(), stand.z()));
            }
         }

         if (!arrayList.isEmpty()) {
            return arrayList;
         } else {
            var arrayList2 = new ArrayList();
            byte cellsHunterLimit = 6;

            for (int i = -cellsHunterLimit; i <= cellsHunterLimit; i++) {
               for (int j = -cellsHunterLimit; j <= cellsHunterLimit; j++) {
                  int cellsHunterValue = blockPos.getX() + i;
                  int z2 = blockPos.getZ() + j;
                  if (ApproachPlan.isWithinHorizontalRange(cellsHunterValue, z2, blockPos.getX(), blockPos.getZ())) {
                     BlockPos chunkTop = McViews.getSurfaceStandPos(this.mcCells, mc.world, cellsHunterValue, z2);
                     if (chunkTop != null) {
                        arrayList2.add(chunkTop);
                     }
                  }
               }
            }

            arrayList2.sort((obj, obj2) -> Double.compare(((net.minecraft.util.math.BlockPos) obj).getSquaredDistance(blockPos), ((net.minecraft.util.math.BlockPos) obj2).getSquaredDistance(blockPos)));

            for (int k = 0; k < arrayList2.size() && arrayList.size() < 3; k++) {
               arrayList.add((BlockPos)arrayList2.get(k));
            }

            return arrayList;
         }
      }
   }

   private Probe buildSafetyProbe(long vehicleCreativeValue) {
      ClientPlayerEntity player = mc.player;
      boolean vehicleCreativeFlag = player.isAlive() && !player.isDead() && !(mc.currentScreen instanceof DeathScreen);
      boolean vehicleCreativeFlag2 = mc.currentScreen != null && !this.enderStash.shouldCloseChestScreen();
      return new Probe(
         vehicleCreativeValue,
         this.huntTick,
         this.sessionConnection,
         mc.world,
         player,
         vehicleCreativeFlag,
         vehicleCreativeFlag2,
         Freecam.isInstanceFlag(),
         ConnectionTimeUtils.getTime(),
         Outbound.isFlag(),
         Inbound.isFlag(),
         false,
         Double.NaN,
         this.playerInputTracker.isUserOverriding(vehicleCreativeValue),
         false,
         null,
         null,
         player.isCreative(),
         player.isSpectator(),
         player.hasVehicle(),
         0
      );
   }

   @Override
   protected void onReset() {
      if (mc.player != null
         && mc.world != null
         && mc.interactionManager != null
         && mc.getNetworkHandler() != null) {
         this.createComponentsIfNeeded();
         long iIiValue = ConnectionTimeUtils.getTimeTimeMillis();

         try {
            this.runHuntTick(iIiValue);
            this.consecutiveErrorCount = 0;
         } catch (Throwable e) {
            this.consecutiveErrorCount++;
            this.hunterStats.errorCount++;
            this.anchorBreaker.cancelBreaking();
            this.hunterBody.endTick();
            this.networkHandler.recordError(iIiValue);
            if (this.networkHandler.hasRepeatedErrors() || this.consecutiveErrorCount > 40) {
               this.stopHunt("Stopped after an internal error.");
            }
         }
      }
   }

   private List<Seen> collectOtherPlayers(ClientPlayerEntity player) {
      var arrayList = new ArrayList(4);

      for (AbstractClientPlayerEntity abstractClientPlayerEntity : mc.world.getPlayers()) {
         if (abstractClientPlayerEntity != player) {
            String profileGameText = abstractClientPlayerEntity.getGameProfile() == null ? abstractClientPlayerEntity.getName().getString() : abstractClientPlayerEntity.getGameProfile().name();
            arrayList.add(new Seen(profileGameText, abstractClientPlayerEntity.getX(), abstractClientPlayerEntity.getY(), abstractClientPlayerEntity.getZ(), FriendNameStore.canFlag(abstractClientPlayerEntity), abstractClientPlayerEntity.isSpectator()));
         }
      }

      return arrayList;
   }

   private void stopAndDisconnect(String disconnectLiteralText) {
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      this.stopHunt(disconnectLiteralText);
      if (networkHandler != null) {
         networkHandler.getConnection().disconnect(Text.literal(disconnectLiteralText));
      }
   }

   private void fleeFromThreats(List<Threat> list) {
      ClientPlayerEntity player = mc.player;
      if (list != null && !list.isEmpty()) {
         if (!this.fleeStartRecorded) {
            this.fleeStartRecorded = true;
            this.hunterStats.fleeCount++;
         }

         boolean waypointThrowFlag = this.fleeWaypoint != null && Math.hypot(this.fleeWaypoint.x() - player.getX(), this.fleeWaypoint.z() - player.getZ()) <= 4.0;
         boolean waypointThrowFlag2 = this.fleeWaypoint != null && FleePlanner.isWaypointTowardThreats(player.getX(), player.getZ(), this.fleeWaypoint, list);
         if (this.fleeWaypoint == null || waypointThrowFlag || waypointThrowFlag2 || this.huntTick - this.lastWaypointTick >= 40L) {
            this.lastWaypointTick = this.huntTick;
            this.fleeWaypoint = FleePlanner.planFleeWaypoint(player.getX(), this.hunterBody.getFeetBlock().getY(), player.getZ(), list, McViews.getGround(this.mcCells));
         }

         if (this.fleeWaypoint != null) {
            this.surfaceWalker.walkToward(this.huntTick, new Vec3d(this.fleeWaypoint.x(), this.fleeWaypoint.y(), this.fleeWaypoint.z()), true);
         }
      }
   }

   private void transitionTo(HunterState hunterState, String hunterUnreachableText) {
      if (hunterState != null) {
         this.statusDetail = hunterUnreachableText == null ? "" : hunterUnreachableText;
         if (hunterState != this.currentState) {
            this.currentState = hunterState;
            this.stateStartTick = this.huntTick;
            this.stateStartMs = ConnectionTimeUtils.getTimeTimeMillis();
         }
      }
   }

   private void tickCollect(long anchorHunterValue) {
      ClientPlayerEntity player = mc.player;
      int stackHand = EnderStash.countItem(player, Items.RESPAWN_ANCHOR);
      if (stackHand > this.anchorCountBeforeMining) {
         int rtpsChargedLimit = stackHand - this.anchorCountBeforeMining;
         this.hunterStats.recordAnchorsMined(rtpsChargedLimit, this.targetHasCharges);
         this.sendNotification(AnchorHunter.Kind.GOOD, "+" + rtpsChargedLimit + " anchor" + (rtpsChargedLimit == 1 ? "" : "s") + " (" + this.hunterStats.anchorsMinedCount + " total)");
         this.clearTarget();
         this.safeAnchorScanStarted = false;
         if (this.stopAfter.getValue() > 0 && this.hunterStats.anchorsMinedCount >= this.stopAfter.getValue()) {
            this.stopHunt("Done - collected " + this.hunterStats.anchorsMinedCount + " anchors.");
         } else {
            this.transitionTo(HunterState.PICK, "picked up");
         }
      } else {
         long anchorHunterLimit = this.huntTick - this.anchorBrokenTick;
         if (anchorHunterLimit >= 10L) {
            if (anchorHunterLimit > 60L) {
               this.hunterStats.lostFleeCount++;
               this.clearTarget();
               this.safeAnchorScanStarted = false;
               this.transitionTo(HunterState.PICK, "drop lost");
            } else {
               ItemEntity entityEntities = EnderStash.findNearestItemEntity(player, 6.0, itemEntity -> itemEntity.getStack().isOf(Items.RESPAWN_ANCHOR));
               if (entityEntities != null) {
                  BlockPos blockPos = entityEntities.getBlockPos();
                  Vec3d entityPos = entityEntities.getEntityPos();
                  DigMover.PathMoveResult pathStep = this.digMover.moveAlongPath(this.huntTick, "drop" + entityEntities.getId(), pos -> isStandingAt(pos, entityPos), blockPos, blockPos, false);
                  if (pathStep == DigMover.PathMoveResult.ARRIVED) {
                     this.hunterBody.moveTowardPoint(entityPos, false);
                  }
               }
            }
         }
      }
   }

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      if (this.hunterBody != null) {
         this.hunterBody.applyDeltaAngle(deltaAngle);
      }
   }

   public static boolean isStandingAt(BlockPos pos, Vec3d vec) {
      return Math.abs(vec.x - (pos.getX() + 0.5)) <= 1.1
         && Math.abs(vec.z - (pos.getZ() + 0.5)) <= 1.1
         && vec.y >= pos.getY() - 0.6
         && vec.y <= pos.getY() + 2.1;
   }

   private void onTeleportDetected(long startedTeleportedTime, double teleportedAnchorsValue) {
      this.rtpClock.onRtpCompleted(startedTeleportedTime);
      this.rtpRegions.clearRegionCount(this.east);
      this.hunterStats.emptyLandCount++;
      this.hunterStats.spotCount++;
      this.resetRtpTracking();
      this.rtpWarmupActive = false;
      this.playerNearby = false;
      this.threatVisible = false;
      this.nearbyPlayerLabel = "";
      this.fleeWaypoint = null;
      this.fleeStartRecorded = false;
      this.nearbyThreats.clear();
      this.playerWatch.reset();
      this.protectedAreaDetected = false;
      this.skippedAnchorKeys.clear();
      this.safeAnchorTargets.clear();
      this.pendingAnchorPositions.clear();
      this.safeAnchorScanStarted = false;
      this.anchorScanner.reset();
      this.anchorBreaker.cancelBreaking();
      this.clearTarget();
      this.digMover.resetAll();
      this.surfaceWalker.reset();
      this.pearlHop.reset();
      this.rtpArrivalMs = startedTeleportedTime;
      this.leftOverworldSinceMs = -1L;
      this.transitionTo(HunterState.SETTLE, "teleported");
   }

   private void processChatMessages(long combatRtpValue) {
      Set set = null;

      String combatRtpText;
      while ((combatRtpText = this.incomingMessageQueue.poll()) != null) {
         this.pendingMessageCount.decrementAndGet();
         if (set == null) {
            set = this.getOnlinePlayerNames();
         }

         var set2 = set;
         Reply textsCombat = RtpReplies.classifyReply(
            combatRtpText, this.lastRtpSentMs > -1152921504606846976L ? combatRtpValue - this.lastRtpSentMs : -1L, names -> set2.contains(names.toLowerCase(Locale.ROOT))
         );
         if (textsCombat.kind() != RtpReplies.Kind.NONE) {
            this.applyRtpReply(combatRtpValue, textsCombat);
            if (!this.isEnabled()) {
               return;
            }
         }
      }
   }

   private Set<String> getOnlinePlayerNames() {
      var hashSet = new HashSet();
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      if (networkHandler == null) {
         return hashSet;
      } else {
         for (PlayerListEntry playerListEntry : networkHandler.getPlayerList()) {
            if (playerListEntry != null && playerListEntry.getProfile() != null && playerListEntry.getProfile().name() != null) {
               hashSet.add(playerListEntry.getProfile().name().toLowerCase(Locale.ROOT));
            }
         }

         return hashSet;
      }
   }

   private boolean tryEatFood(boolean hunterBodyFlag) {
      ClientPlayerEntity player = mc.player;
      int foodLevel = player.getHungerManager().getFoodLevel();
      if (this.air) {
         if (player.isUsingItem()) {
            this.hunterBody.pressKeys(128);
            return true;
         }

         this.air = false;
         this.hunterBody.releaseKeys(128);
      }

      if (!hunterBodyFlag) {
         return false;
      } else {
         boolean hunterBodyFlag2 = this.currentState == HunterState.COOLDOWN_IDLE
            || this.currentState == HunterState.SETTLE
            || this.currentState == HunterState.SCAN
            || this.currentState == HunterState.PICK
            || this.currentState == HunterState.RTP_WAIT;
         boolean hunterBodyFlag3 = player.getHealth() <= 6.0F && foodLevel < 20;
         boolean hunterBodyFlag4 = foodLevel <= 14 && hunterBodyFlag2 || foodLevel <= 6 || player.getHealth() < 14.0F && foodLevel < 20 && hunterBodyFlag2 || hunterBodyFlag3;
         if (!hunterBodyFlag4 || this.currentState == HunterState.STASH || this.enderStash.shouldCloseChestScreen()) {
            return false;
         } else if (this.currentState == HunterState.MINE && this.anchorBreaker.getTargetBlock() != null && foodLevel > 6 && !hunterBodyFlag3) {
            return false;
         } else if (this.huntTick < this.eatCooldownUntilTick) {
            return false;
         } else {
            int inventoryStack = this.findFoodSlot();
            if (inventoryStack < 0) {
               return false;
            } else if (inventoryStack > 8) {
               this.swapFoodIntoHotbar();
               return false;
            } else {
               this.anchorBreaker.cancelBreaking();
               if (!this.hunterBody.isSlotSelectedAndSettled(inventoryStack)) {
                  this.hunterBody.selectHotbarSlot(inventoryStack);
                  return true;
               } else if (!player.isUsingItem() && mc.interactionManager != null) {
                  mc.interactionManager.interactItem(player, Hand.MAIN_HAND);
                  if (player.isUsingItem()) {
                     this.air = true;
                     this.hunterBody.pressKeys(128);
                     return true;
                  } else {
                     this.eatCooldownUntilTick = this.huntTick + 40L;
                     return false;
                  }
               } else {
                  return false;
               }
            }
         }
      }
   }

   private void disableConflictingModules() {
      this.disabledModules.clear();
      if (this.findEnabledModuleName(BLOCKING_MODULES) == null) {
         var arrayList = new ArrayList();

         try (AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled()) {
            for (Class stoppingModulesItem : STOPPING_MODULES) {
               Module iliIiiliiiii = ModuleRegistry.getModule(stoppingModulesItem);
               if (iliIiiliiiii != null && iliIiiliiiii != this && iliIiiliiiii.isEnabled()) {
                  iliIiiliiiii.setEnabled(false);
                  if (!iliIiiliiiii.isEnabled()) {
                     arrayList.add(iliIiiliiiii.getCategory());
                     if (!(iliIiiliiiii instanceof Freecam)) {
                        this.disabledModules.add(iliIiiliiiii);
                     }
                  }
               }
            }
         } catch (Exception e) {
         }

         if (!arrayList.isEmpty()) {
            this.sendNotification(
               AnchorHunter.Kind.WARN,
               "Turned off " + String.join(", ", arrayList) + " while hunting" + (this.disabledModules.isEmpty() ? "." : " - back on when Anchor Hunter stops.")
            );
         }
      }
   }

   private boolean manageAirSupply(long swimmingAirValue) {
      ClientPlayerEntity player = mc.player;
      boolean submergedInWater = player.isSubmergedInWater();
      int[] effectStatus = DigMover.getAirStatus(player);
      boolean swimmingAirFlag = effectStatus[1] >= 1000000;
      if (this.swimmingToSurface) {
         if (swimmingAirFlag || AirPlan.isAirAboveThreshold(effectStatus[0], effectStatus[1], submergedInWater)) {
            this.swimmingToSurface = false;
            this.airPanic = false;
            this.surfacePath = null;
            this.digMover.cancelPath();
            return false;
         }

         if (submergedInWater
            && !this.airPanic
            && AirPlan.decideAirAction(effectStatus[0], true, false, AirPlan.getPathMoveCost(this.surfacePath), player.getAir() <= 0) == AirAction.PANIC) {
            this.airPanic = true;
            this.sendNotification(AnchorHunter.Kind.WARN, "Low on air - swimming up.");
         }
      } else {
         if (!submergedInWater || swimmingAirFlag) {
            return false;
         }

         if (this.surfacePath == null || this.huntTick - this.lastSurfacePathTick >= 4L) {
            this.lastSurfacePathTick = this.huntTick;
            BlockPos blockPos = this.digMover.getCurrentFeetBlock(player);
            this.surfacePath = AirPlan.findPathToAirPocket(this.mcCells, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 8000);
         }

         int pathMoveCost = AirPlan.getPathMoveCost(this.surfacePath);
         AirAction panicSurface = AirPlan.decideAirAction(effectStatus[0], true, false, pathMoveCost, player.getAir() <= 0);
         if (panicSurface == AirAction.OK) {
            if (!this.digMover.wasUpdatedRecently(this.huntTick) && this.currentState != HunterState.STASH) {
               this.hunterBody.pressKeys(16);
            }

            return false;
         }

         this.swimmingToSurface = true;
         this.airPanic = panicSurface == AirAction.PANIC;
         this.digMover.cancelPath();
         this.anchorBreaker.cancelBreaking();
         if (this.enderStash.shouldCloseChestScreen()) {
            this.enderStash.cleanup();
         }

         this.pearlHop.abortAiming(this.huntTick, "air");
         this.air = false;
         this.hunterBody.releaseKeys(128);
         if (this.airPanic) {
            this.sendNotification(AnchorHunter.Kind.WARN, "Low on air - swimming up.");
         }
      }

      if (this.airPanic && this.surfacePath == null && submergedInWater) {
         if (this.rtpClock.canSendAt(swimmingAirValue) && !this.rtpClock.isRequestPending()) {
            this.sendRtpCommand(swimmingAirValue);
         }

         if (this.rtpClock.isRequestPending()) {
            return true;
         }
      }

      this.swimTowardSurface(player);
      return true;
   }

   private boolean tryPearlTravel(long throwHopValue, boolean throwHopFlag) {
      if (this.pearlHop.getState() == State.FLYING) {
         this.networkHandler.setExpectedPearlTeleportMs(throwHopValue);
         return true;
      } else if (this.pearlTravel.isEnabled() && throwHopFlag) {
         boolean throwHopFlag2 = !this.nearAnchorTarget || this.digMover.getFailureCount() > 0;
         if (!throwHopFlag2 && !this.pearlHop.isBusy()) {
            return false;
         } else {
            com.corz.client.utils.PearlHop.Act entityAiming = this.pearlHop.updateAndGetAct(this.huntTick, this.targetAnchorPos, this::findPearlLandingSpots);
            if (entityAiming == com.corz.client.utils.PearlHop.Act.THROW) {
               this.anchorBreaker.cancelBreaking();
               if (this.tryThrowPearl()) {
                  this.pearlHop.onPearlThrown(this.huntTick);
                  this.networkHandler.setExpectedPearlTeleportMs(throwHopValue);
               } else {
                  this.pearlHop.abortAiming(this.huntTick, "throw refused");
               }

               return true;
            } else {
               return entityAiming == com.corz.client.utils.PearlHop.Act.BUSY;
            }
         }
      } else {
         if (this.pearlHop.isBusy()) {
            this.pearlHop.abortAiming(this.huntTick, throwHopFlag ? "off" : "paused");
         }

         return false;
      }
   }

   private void applyRtpReply(long rtpClockValue, Reply reply) {
      switch (reply.kind()) {
         case SEARCHING:
            this.rtpClock.extendRequestDeadline(rtpClockValue);
            break;
         case COOLDOWN:
            if (this.rtpClock.isRequestPending()) {
               this.rtpClock.onCooldownReply(rtpClockValue, reply.waitMs());
               this.resetRtpTracking();
               if (this.currentState == HunterState.RTP_WAIT || this.currentState == HunterState.HOLD_STILL) {
                  this.transitionTo(HunterState.COOLDOWN_IDLE, "rtp cooldown");
               }
            }
            break;
         case COMBAT:
            if (this.rtpClock.isRequestPending()) {
               this.rtpClock.postponeLong(rtpClockValue);
               this.resetRtpTracking();
               if (this.currentState == HunterState.RTP_WAIT || this.currentState == HunterState.HOLD_STILL) {
                  this.transitionTo(HunterState.COOLDOWN_IDLE, "rtp combat");
               }
            }
            break;
         case CHAT_LIMIT:
            if (this.rtpClock.isRequestPending() && rtpClockValue - this.lastRtpSentMs < 2000L) {
               this.rtpClock.postponeShort(rtpClockValue);
               this.resetRtpTracking();
               if (this.currentState == HunterState.RTP_WAIT) {
                  this.transitionTo(HunterState.COOLDOWN_IDLE, "chat limit");
               }
            }
            break;
         case UNKNOWN_COMMAND:
            if (this.rtpClock.isRequestPending()) {
               this.resetRtpTracking();
               this.stopHunt("This server has no /rtp - stopped.");
            }
            break;
         case WARMUP:
            if (this.rtpClock.isRequestPending()) {
               this.rtpWarmupActive = true;
               if (this.currentState == HunterState.RTP_WAIT) {
                  this.transitionTo(HunterState.HOLD_STILL, "warmup");
               }
            }
            break;
         case CANCELLED:
            if (this.rtpClock.isRequestPending() || this.rtpWarmupActive) {
               this.rtpClock.postponeMedium(rtpClockValue);
               this.resetRtpTracking();
               this.rtpWarmupActive = false;
               if (this.currentState == HunterState.RTP_WAIT || this.currentState == HunterState.HOLD_STILL) {
                  this.transitionTo(HunterState.COOLDOWN_IDLE, "rtp cancelled");
               }
            }
            break;
         case PROTECTED:
            if (this.currentState == HunterState.STASH) {
               this.enderStash.markProtectedArea();
            } else if (rtpClockValue - this.miningStartedMs <= 3000L && (this.currentState == HunterState.MINE || this.currentState == HunterState.APPROACH)) {
               this.anchorBreaker.cancelBreaking();
               this.transitionTo(HunterState.PROTECTED, "protected area");
            }
      }
   }

   private void handleInventoryFull(long stashHunterValue) {
      this.anchorBreaker.cancelBreaking();
      ClientPlayerEntity player = mc.player;
      if (!this.enderStash.tryDropFillerBlock()) {
         if (!EnderStash.hasRoomForAnchor(player)) {
            if (this.enderChestStash.isEnabled() && EnderStash.hasChestAndSilkPickaxe(player, this.anchorBreaker)) {
               this.resumeState = this.currentState;
               this.enderStash.start(this.huntTick);
               this.transitionTo(HunterState.STASH, "inventory full");
            } else {
               this.stopHunt("Inventory full - " + this.hunterStats.anchorsMinedCount + " anchors collected.");
            }
         }
      }
   }

   private void collectPacketText(Packet<?> packet) {
      if (packet instanceof GameMessageS2CPacket gameMessageS2CPacket) {
         if (gameMessageS2CPacket.content() != null) {
            this.queueChatMessage(gameMessageS2CPacket.content().getString());
         }
      } else if (packet instanceof OverlayMessageS2CPacket overlayMessageS2CPacket) {
         if (overlayMessageS2CPacket.text() != null) {
            this.queueChatMessage(overlayMessageS2CPacket.text().getString());
         }
      } else if (packet instanceof TitleS2CPacket titleS2CPacket) {
         if (titleS2CPacket.text() != null) {
            this.queueChatMessage(titleS2CPacket.text().getString());
         }
      } else if (packet instanceof SubtitleS2CPacket subtitleS2CPacket) {
         if (subtitleS2CPacket.text() != null) {
            this.queueChatMessage(subtitleS2CPacket.text().getString());
         }
      } else if (packet instanceof EntityDamageS2CPacket entityDamageS2CPacket) {
         if (this.damageEventQueue.size() < 64) {
            this.damageEventQueue.offer(new int[]{entityDamageS2CPacket.entityId(), entityDamageS2CPacket.sourceCauseId()});
         }
      } else {
         this.anchorScanner.onPacket(packet, ConnectionTimeUtils.getTimeTimeMillis());
      }
   }

   private String getConflictingModuleName() {
      String formatText = this.findEnabledModuleName(BLOCKING_MODULES);
      return formatText != null ? formatText : this.findEnabledModuleName(STOPPING_MODULES);
   }

   private static boolean isSafeFood(ItemStack stack) {
      return stack != null
         && !stack.isEmpty()
         && stack.get(DataComponentTypes.FOOD) != null
         && !stack.isOf(Items.CHORUS_FRUIT)
         && !stack.isOf(Items.ROTTEN_FLESH)
         && !stack.isOf(Items.SPIDER_EYE)
         && !stack.isOf(Items.POISONOUS_POTATO)
         && !stack.isOf(Items.PUFFERFISH)
         && !stack.isOf(Items.SUSPICIOUS_STEW)
         && !stack.isOf(Items.GOLDEN_APPLE)
         && !stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
   }

   private void tickPick(long anchorsSafeValue) {
      ClientPlayerEntity player = mc.player;
      if (!this.safeAnchorScanStarted) {
         this.safeAnchorScanStarted = true;
         this.safeAnchorTargets.clear();
         this.pendingAnchorPositions.clear();
         var arrayList = new ArrayList<BlockPos>();
         var iterator = this.anchorScanner.getAnchorCharges().long2ByteEntrySet().iterator();

         while (iterator.hasNext()) {
            Entry entry = (Entry)iterator.next();
            if (!this.skippedAnchorKeys.contains(entry.getLongKey())) {
               arrayList.add(BlockPos.fromLong(entry.getLongKey()));
            }
         }

         Vec3d eyePos = player.getEyePos();
         arrayList.sort((pos, pos2) -> Double.compare(eyePos.squaredDistanceTo(Vec3d.ofCenter(pos)), eyePos.squaredDistanceTo(Vec3d.ofCenter(pos2))));

         for (int i = 0; i < arrayList.size() && i < 48; i++) {
            this.pendingAnchorPositions.add((BlockPos)arrayList.get(i));
         }
      }

      long nanoTime = System.nanoTime();
      Sight sight = McViews.getSight(this.hunterBody);
      int y = this.hunterBody.getFeetBlock().getY();

      while (!this.pendingAnchorPositions.isEmpty() && System.nanoTime() - nanoTime < 2000000L) {
         BlockPos anchorssafeitem = this.pendingAnchorPositions.remove(0);
         BlockState blockState = mc.world.getBlockState(anchorssafeitem);
         if (!blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
            this.anchorScanner.removeAnchorAt(anchorssafeitem);
         } else {
            AnchorTarget cellsBase = this.evaluateAnchorTarget(anchorssafeitem, blockState, sight, y);
            if (!cellsBase.isVerdictSafe()) {
               this.skippedAnchorKeys.add(anchorssafeitem.asLong());
               this.hunterStats.recordSkipReason(cellsBase.verdict().reason());
            } else {
               this.safeAnchorTargets.add(cellsBase);
            }
         }
      }

      if (this.pendingAnchorPositions.isEmpty()) {
         var comparing = AnchorTarget.filterSafeSorted(this.safeAnchorTargets);
         if (comparing.isEmpty()) {
            this.skipToNextRtp(anchorsSafeValue, "no safe anchors left");
         } else {
            this.selectTarget(anchorsSafeValue, (AnchorTarget)comparing.get(0));
         }
      }
   }

   private double getScanCoverage() {
      if (this.huntTick - this.lastCoverageTick >= 5L) {
         this.lastCoverageTick = this.huntTick;
         this.scanCoverage = this.anchorScanner.getScannedChunkRatio(mc.world, mc.player.getBlockX() >> 4, mc.player.getBlockZ() >> 4);
      }

      return this.scanCoverage;
   }

   private void sendNotification(AnchorHunter.Kind kind, String joinWorldText) {
      if (kind != AnchorHunter.Kind.INFO && joinWorldText != null) {
         if (kind == AnchorHunter.Kind.STOP || !joinWorldText.equals(this.lastNotificationText) || this.huntTick - this.lastNotificationTick >= 600L) {
            this.lastNotificationText = joinWorldText;
            this.lastNotificationTick = this.huntTick;
            String formatText = this.notificationMode.getCurrentOption();
            if (!formatText.equals("Off") || kind == AnchorHunter.Kind.STOP) {
               if (!formatText.equals("Chat") && Notifications.isInstanceBase()) {
                  DisabledEnabled.getInstance().handleDisabledEnabled("Anchor Hunter", joinWorldText, kind == AnchorHunter.Kind.GOOD);
               } else {
                  if (mc.player != null) {
                     mc.player.sendMessage(Text.literal("\u00a77[Anchor Hunter]\u00a7r " + joinWorldText), false);
                  }
               }
            }
         }
      }
   }

   private String findEnabledModuleName(List<Class<? extends Module>> list2) {
      for (Class class2 : list2) {
         Module iliIiiliiiii = ModuleRegistry.getModule(class2);
         if (iliIiiliiiii != null && iliIiiliiiii != this && iliIiiliiiii.isEnabled()) {
            return iliIiiliiiii.getCategory();
         }
      }

      return null;
   }

   private void detectTeleport(long entityPosValue) {
      ClientPlayerEntity player = mc.player;
      Vec3d entityPos = player.getEntityPos();
      Vec3d lastPlayerPos2 = this.lastPlayerPos;
      this.lastPlayerPos = entityPos;
      if (this.posAtRtpSend != null && entityPosValue <= this.entityPosIgnoreUntilMs) {
         boolean entityPosFlag = mc.world != this.rtpWorld;
         double entityPosLimit = entityPosFlag ? Double.POSITIVE_INFINITY : (lastPlayerPos2 == null ? 0.0 : entityPos.distanceTo(lastPlayerPos2));
         boolean entityPosFlag2 = ConnectionTimeUtils.getTime() >= this.lastRtpSentMs;
         if (entityPosFlag || entityPosLimit > 64.0 && entityPosFlag2) {
            if (entityPosFlag2) {
               this.networkHandler.setExpectedPearlTeleportMs(ConnectionTimeUtils.getTime());
            }

            this.lastTeleportMs = entityPosFlag ? -2305843009213693952L : ConnectionTimeUtils.getTime();
            this.onTeleportDetected(entityPosValue, entityPosLimit);
         }
      }
   }

   private void dispatchStateTick(long stoppedProtectedValue, boolean stoppedProtectedFlag) {
      switch (this.currentState) {
         case RTP_SEND:
            this.tickRtpSend(stoppedProtectedValue, stoppedProtectedFlag);
            break;
         case RTP_WAIT:
         case HOLD_STILL:
            this.tickRtpWait(stoppedProtectedValue);
            break;
         case COOLDOWN_IDLE:
            this.tickCooldownIdle(stoppedProtectedValue, stoppedProtectedFlag);
            break;
         case SETTLE:
            this.tickSettle(stoppedProtectedValue);
            break;
         case SCAN:
            this.tickScan(stoppedProtectedValue);
            break;
         case PICK:
            this.tickPick(stoppedProtectedValue);
            break;
         case APPROACH:
            this.tickApproach(stoppedProtectedValue, stoppedProtectedFlag);
            break;
         case MINE:
            this.tickMine(stoppedProtectedValue, stoppedProtectedFlag);
            break;
         case COLLECT:
            this.tickCollect(stoppedProtectedValue);
            break;
         case STASH:
            this.tickStash(stoppedProtectedValue, stoppedProtectedFlag);
            break;
         case PROTECTED:
            this.protectedAreaDetected = true;
            this.skipToNextRtp(stoppedProtectedValue, "protected area");
            break;
         case FLEE:
            this.transitionTo(this.rtpClock.isRequestPending() ? HunterState.RTP_WAIT : HunterState.RTP_SEND, "threat gone");
            break;
         case PAUSED:
            this.transitionTo(this.resumeState, "resumed");
            break;
         case STOPPED:
            this.stopHunt(this.statusDetail.isBlank() ? "Stopped." : this.statusDetail);
      }
   }

   public String formatStatusReport() {
      long hunterValue = ConnectionTimeUtils.getTimeTimeMillis();
      return this.hunterStats.formatDebugLine(this.currentState.name(), this.rtpClock.getWaitRemainingMs(hunterValue), this.nearbyPlayerLabel, this.statusDetail);
   }

   private void escapeFromPlayer(long hunterRtpValue) {
      ClientPlayerEntity player = mc.player;
      this.anchorBreaker.cancelBreaking();
      if (this.enderStash.shouldCloseChestScreen()) {
         this.enderStash.cleanup();
      }

      this.hunterBody.releaseKeys(128);
      this.air = false;
      if (this.currentState != HunterState.FLEE && this.currentState != HunterState.RTP_WAIT && this.currentState != HunterState.HOLD_STILL) {
         this.transitionTo(HunterState.FLEE, "player " + this.nearbyPlayerLabel);
      }

      com.corz.client.utils.PlayerWatch.Threat threat = this.playerWatch.getNearestThreat();
      if (threat == null) {
         threat = this.playerWatch.getRecentThreat(this.huntTick);
      }

      double hypot2 = threat == null ? Double.MAX_VALUE : threat.dist();
      if (!this.threatVisible && threat != null) {
         hypot2 = Math.hypot(threat.x() - player.getX(), threat.z() - player.getZ());
      }

      if (this.rtpClock.hasRequestTimedOut(hunterRtpValue)) {
         this.rtpRegions.incrementRegionCount(this.east);
         if (this.rtpClock.recordFailureAndCheckLimit(hunterRtpValue)) {
            this.stopHunt("/rtp did not answer 5 times in a row - stopped.");
            return;
         }
      }

      boolean hunterRtpFlag = this.rtpClock.canSendAt(hunterRtpValue) || this.rtpClock.canSendPredicted(hunterRtpValue);
      React rtpFlee = ThreatPlan.decideReaction(
         new Input(
            true,
            hunterRtpFlag,
            this.rtpClock.isRequestPending(),
            this.rtpWarmupActive,
            hypot2,
            this.rtpClock.getWaitRemainingMs(hunterRtpValue),
            this.logOutIfCaught.isEnabled(),
            this.lastPlayerCombatMs < 0L ? -1L : hunterRtpValue - this.lastPlayerCombatMs
         )
      );
      switch (rtpFlee) {
         case RTP_NOW:
            this.sendRtpCommand(hunterRtpValue);
            if (this.resumed.moveAllowed() && !this.rtpWarmupActive) {
               this.fleeFromThreats(this.nearbyThreats);
            }
            break;
         case LOGOUT:
            this.hunterStats.logoutCount++;
            this.stopAndDisconnect("Anchor Hunter - player nearby");
            break;
         case HOLD_STILL:
            this.hunterBody.rotateTowardYaw(player.getYaw(), player.getPitch(), 0);
            break;
         case FLEE:
            if (this.resumed.moveAllowed()) {
               this.fleeFromThreats(this.nearbyThreats);
            }
      }
   }

   private void tickRtpSend(long rtpReadyValue, boolean rtpReadyFlag) {
      if (!this.rtpClock.canSendAt(rtpReadyValue)) {
         this.transitionTo(HunterState.COOLDOWN_IDLE, "rtp not ready");
      } else if (rtpReadyFlag || this.resumed.level() == Level.ACTIONS_PAUSED) {
         this.sendRtpCommand(rtpReadyValue);
      }
   }

   private void tickScan(long anchorsScannerValue) {
      double throwAnchor = this.getScanCoverage();
      boolean anchorsScannerFlag = anchorsScannerValue - this.anchorScanner.getLastChunkPacketTick() >= 1000L;
      if (throwAnchor >= 0.95 && anchorsScannerFlag || anchorsScannerValue - this.stateStartMs > 6000L) {
         if (this.anchorScanner.getAnchorCount() == 0) {
            this.hunterStats.anchorsEmptyCount++;
            this.skipToNextRtp(anchorsScannerValue, "no anchors here");
            return;
         }

         this.safeAnchorScanStarted = false;
         this.transitionTo(HunterState.PICK, this.anchorScanner.getAnchorCount() + " anchors seen");
      }
   }

   private void updateStatusSnapshot(long hunterDivingValue) {
      Tone tone;
      if (this.playerNearby || this.threatVisible) {
         tone = Tone.DANGER;
      } else if (this.currentState == HunterState.PAUSED) {
         tone = Tone.PAUSED;
      } else if (this.currentState != HunterState.COOLDOWN_IDLE && this.currentState != HunterState.PREFLIGHT) {
         tone = Tone.ACTIVE;
      } else {
         tone = Tone.IDLE;
      }

      String divingBreathText = this.currentState.displayText;
      if (this.currentState == HunterState.PAUSED && this.resumed != null) {
         divingBreathText = this.resumed.reason().statusMessage;
      } else if (this.playerNearby) {
         divingBreathText = this.rtpClock.isRequestPending()
            ? "Player nearby - teleporting"
            : "Player nearby - running (/rtp in " + (this.rtpClock.getWaitRemainingMs(hunterDivingValue) + 999L) / 1000L + "s)";
      } else if (this.swimmingToSurface) {
         divingBreathText = "Swimming up for air";
      } else if (this.pearlHop != null && this.pearlHop.isBusy() && this.currentState == HunterState.APPROACH) {
         divingBreathText = "Pearling to an anchor";
      } else if (this.digMover != null && this.digMover.isSurfacingForAir()) {
         divingBreathText = "Catching breath before diving";
      } else if (this.currentState == HunterState.MINE || this.currentState == HunterState.APPROACH) {
         int hunterDivingLimit = Math.max(0, this.safeAnchorTargets.size());
         divingBreathText = this.currentState.displayText + (hunterDivingLimit > 0 ? " (" + hunterDivingLimit + " left here)" : "");
      } else if (this.currentState == HunterState.COOLDOWN_IDLE) {
         divingBreathText = "Waiting for /rtp (" + (this.rtpClock.getWaitRemainingMs(hunterDivingValue) + 999L) / 1000L + "s)";
      }

      String hunterDivingText = null;
      if (this.targetAnchorPos != null && mc.player != null) {
         hunterDivingText = (int)Math.round(Math.sqrt(mc.player.squaredDistanceTo(Vec3d.ofCenter(this.targetAnchorPos)))) + " b away";
      }

      this.statusSnapshot = new Snapshot(tone, divingBreathText, hunterDivingText, this.hunterStats.anchorsMinedCount, 0, this.hunterStats.getSummaryText());
   }

   private boolean refreshStandPositions() {
      BlockState blockState = mc.world.getBlockState(this.targetAnchorPos);
      if (!blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
         return false;
      } else {
         Result sees = StandFinder.scanStands(this.mcCells, this.targetAnchorPos.getX(), this.targetAnchorPos.getY(), this.targetAnchorPos.getZ(), McViews.getSight(this.hunterBody));
         com.corz.client.utils.AnchorSafety.Verdict lavaUnreachable = AnchorSafety.evaluateAnchorSafety(
            this.mcCells,
            this.targetAnchorPos.getX(),
            this.targetAnchorPos.getY(),
            this.targetAnchorPos.getZ(),
            this.hunterBody.getFeetBlock().getY(),
            sees,
            this.digToHiddenAnchors.isEnabled(),
            this.protectedAreaDetected
         );
         this.candidateStandPositions.clear();
         if (!lavaUnreachable.safe()) {
            this.statusDetail = lavaUnreachable.reason();
            return false;
         } else {
            for (Stand stand : sees.getCandidates(this.digToHiddenAnchors.isEnabled())) {
               this.candidateStandPositions.add(BlockPos.asLong(stand.x(), stand.y(), stand.z()));
            }

            return !this.candidateStandPositions.isEmpty();
         }
      }
   }

   public HunterState getCurrentState() {
      return this.currentState;
   }

   private void tickApproach(long unreachableAnchorValue, boolean unreachableAnchorFlag) {
      ClientPlayerEntity player = mc.player;
      if (this.targetAnchorPos == null) {
         this.transitionTo(HunterState.PICK, "no target");
      } else if (!mc.world.getBlockState(this.targetAnchorPos).isOf(Blocks.RESPAWN_ANCHOR)) {
         this.anchorScanner.removeAnchorAt(this.targetAnchorPos);
         this.clearTarget();
         this.safeAnchorScanStarted = false;
         this.transitionTo(HunterState.PICK, "anchor gone");
      } else if (!EnderStash.hasRoomForAnchor(player)) {
         this.handleInventoryFull(unreachableAnchorValue);
      } else if (ApproachPlan.hasExceededAttemptLimits(this.digMover.getFailureCount(), this.digMover.getBlocksDugCount(), unreachableAnchorValue - this.targetSelectedMs)) {
         this.abandonTarget(unreachableAnchorValue, "unreachable");
      } else {
         double unreachableAnchorLimit = ApproachPlan.getHorizontalDistance(player.getX(), player.getZ(), this.targetAnchorPos.getX(), this.targetAnchorPos.getZ());
         if (!this.nearAnchorTarget && unreachableAnchorLimit <= 28.0) {
            this.nearAnchorTarget = true;
         } else if (this.nearAnchorTarget && unreachableAnchorLimit > 38.0) {
            this.nearAnchorTarget = false;
         }

         if (this.resumed.moveAllowed()) {
            if (!this.tryPearlTravel(unreachableAnchorValue, unreachableAnchorFlag)) {
               if (!this.nearAnchorTarget) {
                  BlockPos blockPos = this.targetAnchorPos;
                  DigMover.PathMoveResult pathStep = this.digMover
                     .moveAlongPath(
                        this.huntTick,
                        "far" + blockPos.asLong(),
                        pos -> ApproachPlan.getHorizontalDistance(pos.getX() + 0.5, pos.getZ() + 0.5, blockPos.getX(), blockPos.getZ()) <= 24.0,
                        blockPos,
                        blockPos,
                        false
                     );
                  if (pathStep == DigMover.PathMoveResult.ARRIVED) {
                     this.nearAnchorTarget = true;
                  }
               } else if ((this.candidateStandPositions.isEmpty() || (this.huntTick - this.stateStartTick) % 100L == 0L) && !this.refreshStandPositions()) {
                  this.abandonTarget(unreachableAnchorValue, this.statusDetail.isBlank() ? "unreachable" : this.statusDetail);
               } else if (unreachableAnchorFlag) {
                  DigMover.PathMoveResult moveAlongPath = this.digMover
                     .moveAlongPath(
                        this.huntTick,
                        "anchor" + this.targetAnchorPos.asLong(),
                        pos2 -> this.candidateStandPositions.contains(pos2.asLong()) && !this.failedStandKeys.contains(pos2.asLong()),
                        this.targetAnchorPos,
                        this.targetAnchorPos,
                        this.digToHiddenAnchors.isEnabled()
                     );
                  if (this.digMover.isDigging()) {
                     this.breakingAnchor = true;
                  }

                  if (moveAlongPath == DigMover.PathMoveResult.ARRIVED) {
                     this.mineStartTick = this.huntTick;
                     this.anchorCountBeforeMining = EnderStash.countItem(player, Items.RESPAWN_ANCHOR);
                     this.anchorMissing = false;
                     this.transitionTo(HunterState.MINE, "at stand");
                  }
               }
            }
         }
      }
   }

   private void clearTarget() {
      this.anchorTarget = null;
      this.targetAnchorPos = null;
      this.nearAnchorTarget = false;
      this.candidateStandPositions.clear();
      this.anchorMissing = false;
      this.anchorMissingTicks = 0;
   }

   private static enum Kind {
      INFO,
      GOOD,
      WARN,
      STOP;
   }
}
