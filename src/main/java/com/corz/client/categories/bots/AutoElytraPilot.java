package com.corz.client.categories.bots;

import com.corz.client.utils.FlightPlanner.Goal;
import com.corz.client.utils.FlightPlanner.Plan;
import com.corz.client.utils.FlightPlanner.Terrain;
import com.corz.client.utils.GlidePhysics.Body;
import com.corz.client.utils.LandingSpotFinder.Probe;
import com.corz.client.utils.LandingSpotFinder.Spot;
import com.corz.client.utils.PilotAhBuyer.Goods;
import com.corz.client.utils.PilotAhBuyer.Status;
import com.corz.client.utils.PilotRender.Scene;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.StringBase;
import com.corz.client.gui.ArrivalScreen;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.TickProgress;
import com.corz.client.inventory.SelectedInventoryUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import com.corz.client.utils.ScissorTgtHandler.Tone;
import com.corz.client.world.ClampMath;
import com.corz.client.world.MathClampUtils;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.FlightPlanner;
import com.corz.client.utils.GlidePhysics;
import com.corz.client.utils.LandingPlanner;
import com.corz.client.utils.LandingSpotFinder;
import com.corz.client.utils.PilotAhBuyer;
import com.corz.client.utils.PilotRender;
import com.corz.client.utils.PilotStatus;
import com.corz.client.utils.PilotStatusCard;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.HotbarSlotUtils;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.AutoVanish;
import com.corz.client.categories.visuals.Freelook;
import ai.auth.Module;
import com.corz.client.utils.HumanAimRotator;
import com.corz.client.utils.RotationInterpolator;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.Destination;
import com.corz.client.utils.ElytraAutoUtils;
import com.corz.client.utils.ExtUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.TripStats;

public class AutoElytraPilot extends Module implements ScissorTgtHandler {
   private final BooleanSetting autoBuyRockets;
   private final Long2IntOpenHashMap surfaceHeightCache;
   private final Probe landingSpotProbe;
   private int lastLandingProbeTick;
   private volatile int positionLookPacketCount;
   private RegistryKey<World> startDimension;
   private final BooleanSetting autoMending;
   private Vec3d landingGoalPos;
   private int failedTasksStateStamp;
   private int xpBuyRetryTick;
   private final PilotAhBuyer pilotAhBuyer;
   private final BooleanSetting autoBuyXpBottles;
   private boolean rotationRequested;
   private int bestMendingDurability;
   private int xpBuyFailureCount;
   private String tripStopReason;
   private int descentProgressTick;
   private Vec3d serviceSpotPos;
   private boolean suppressArrivalLogOff;
   private boolean circlingToLand;
   private final EnumSetting priceLimit;
   private int lastCriticalSwapTick;
   private final StringBase destinationInput = new StringBase("Destination", "Where to fly, as X Z (for example 1200 -3400). X Y Z works too.", "");
   private boolean buyingBlockedThisTrip;
   private final IntegerSetting flightHeight = new IntegerSetting(
      "Flight Height", "Cruising height. The pilot climbs higher on its own to clear mountains and builds.", 290, 180, 320, 10
   );
   private int lastApproachPlanTick;
   private long tripStartMillis;
   private volatile Snapshot menuSnapshot;
   private boolean inventorySwapPending;
   private int lastDamageTick;
   private boolean hasOpenSkyAbove;
   private double lowestHeightAboveLanding;
   private int routeProgressCheckTick;
   private final Goal flightGoal;
   private int rocketBuyFailureCount;
   private int stateChangeTick;
   private final IntegerSetting xpBottleStacks;
   private int rocketBoostEndTick;
   private final BooleanSetting statusCard;
   private int lastSeenPositionLookCount;
   private double peakSpeedPerSecond;
   private boolean fastRotationRequested;
   private double tripDistance;
   private boolean startedWithoutWorld;
   private boolean rocketsUnaffordable;
   private int rocketUseEnabledTick;
   private int lastNotificationTick;
   private final EnumSetting notifications;
   private float openSkyYaw;
   private int landingProbeAttempts;
   private int waterEntryTick;
   private Destination destination;
   private double routeTotalDistance;
   private double cruiseAltitude;
   private final PilotRender pilotRender;
   private int pitStopCount;
   private final Body glideBody;
   private int routeAdjustCount;
   private AutoElytraPilot.Task currentTask;
   private int eatCooldownEndTick;
   private int takeoffJumpCount;
   private int badApproachPlanCount;
   private final List<Vec3d> rejectedLandingSpots;
   private int overchargedPurchaseCount;
   private int mendingFailureCount;
   private final BooleanSetting waypointLine;
   private final HumanAimRotator humanAim;
   private ClientConnection connection;
   private final PilotStatusCard pilotStatusCard;
   private boolean xpUnaffordable;
   private Boolean savedPauseOnLostFocus;
   private AutoElytraPilot.Job job;
   private final LandingPlanner landingPlanner;
   private int rocketMoneyShortCount;
   private int lastGoalPlanTick;
   private float targetPitch;
   private int internalErrorCount;
   private final RotationInterpolator rotationSmoother;
   private int takeoffAttemptCount;
   private int lastRocketUseTick;
   private float targetYaw;
   private int rocketBuyRetryTick;
   private AutoElytraPilot.State flightState;
   private long tripSpentMoney;
   private final IntegerSetting rocketStacks;
   private int lastMendingProgressTick;
   private int taskStartTick;
   private double closestApproachDistance;
   private final BooleanSetting logOffOnArrival;
   private int rocketsUsedCount;
   private final EnumSetting tripBudget;
   private boolean landingSpotIsWater;
   private String lastNotificationText;
   private int goAroundCount;
   private int lastTakeoffJumpTick;
   private int rotationPriority;
   private final EnumSet<AutoElytraPilot.Task> failedTasks;
   private Plan currentFlightPlan;
   private int xpMoneyShortCount;
   private Vec3d shoreTargetPos;
   private int rocketFlightDuration;
   private PilotStatus pilotStatus;
   private float lastHealth;
   private final Terrain terrain;
   private int xpBottlesThrown;
   private int recentPositionLookCount;
   private int lastRubberbandWindowTick;
   private final Scene scene;
   private int pilotTicks;
   private String startErrorText;
   private final FlightPlanner flightPlanner;
   private int lastMendingActionTick;
   private static final int ELYTRA_SINGLE_STEP = 1;
   private Vec3d lastTripPos;

   private boolean hasCollisionAt(BlockPos pos) {
      BlockState blockState = mc.world.getBlockState(pos);
      return !blockState.isAir() && !blockState.getCollisionShape(mc.world, pos).isEmpty();
   }

   private int countRockets() {
      PlayerInventory inventory = mc.player.getInventory();
      int stackHand = 0;

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (PilotAhBuyer.isPlainFireworkRocket(stack)) {
            stackHand += stack.getCount();
         }
      }

      ItemStack offHandStack = mc.player.getOffHandStack();
      if (PilotAhBuyer.isPlainFireworkRocket(offHandStack)) {
         stackHand += offHandStack.getCount();
      }

      return stackHand;
   }

   private double getEstimatedGroundY(double floorBottomValue, double floorBottomValue2) {
      int chunkHeightmap = this.getTopBlockHeight(MathHelper.floor(floorBottomValue), MathHelper.floor(floorBottomValue2));
      if (chunkHeightmap != Integer.MIN_VALUE && chunkHeightmap > mc.world.getBottomY() + 1) {
         return chunkHeightmap;
      } else {
         return this.startDimension == World.END ? 60.0 : 68.0;
      }
   }

   private void notifyUser(AutoElytraPilot.Kind kind, String pilotElytraText) {
      if (kind != AutoElytraPilot.Kind.INFO) {
         if (!pilotElytraText.equals(this.lastNotificationText) || this.pilotTicks - this.lastNotificationTick >= 600) {
            this.lastNotificationText = pilotElytraText;
            this.lastNotificationTick = this.pilotTicks;
            String formatText = this.notifications.getCurrentOption();
            if (!formatText.equals("Off") || kind == AutoElytraPilot.Kind.STOP) {
               if (formatText.equals("Chat")) {
                  if (mc.player != null) {
                     mc.player
                        .sendMessage(Text.literal("[Elytra Pilot] ").formatted(Formatting.AQUA).append(Text.literal(pilotElytraText).formatted(Formatting.WHITE)), false);
                  }
               } else {
                  DisabledEnabled.getInstance()
                     .handleDisabledEnabled("Elytra Pilot", pilotElytraText, kind == AutoElytraPilot.Kind.GOOD || kind == AutoElytraPilot.Kind.INFO);
               }
            }
         }
      }
   }

   public boolean isInLandingPhase() {
      return this.isEnabled()
         && (
            this.flightState == AutoElytraPilot.State.DESCEND
               || this.flightState == AutoElytraPilot.State.LAND
               || this.flightState == AutoElytraPilot.State.SERVICE
               || this.flightState == AutoElytraPilot.State.ARRIVED
               || this.flightState == AutoElytraPilot.State.STOPPED
               || this.job != AutoElytraPilot.Job.ARRIVE
         );
   }

   private int getEquippedElytraDurability() {
      ItemStack equippedStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
      return equippedStack.isOf(Items.ELYTRA) ? Math.max(0, getRemainingDurability(equippedStack)) : -1;
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      if (mc.player != null && !mc.options.hudHidden) {
         if (this.scene.destinationActive) {
            this.pilotRender.renderHud(tickProgress.drawContext, tickProgress.tickProgress, this.scene);
         }

         if (this.statusCard.isEnabled() && mc.currentScreen == null) {
            this.pilotStatusCard.renderStatusCard(tickProgress.drawContext, this.pilotStatus);
         }
      }
   }

   private int getRocketTargetCount() {
      return this.rocketStacks.getValue() * 64;
   }

   private void updateServiceState() {
      if (mc.player.isTouchingWater()) {
         this.swimToShore();
      } else if (this.isGliding()) {
         this.setFlightState(AutoElytraPilot.State.LAND);
      } else if (mc.player.isOnGround()) {
         if (this.isThreatNearby()) {
            this.syncAhBuyerStats();
            if (this.isContainerScreenOpen()) {
               mc.player.closeHandledScreen();
            } else {
               boolean elytraRepairedFlag = this.countRockets() >= 2 && (this.getEquippedElytraDurability() < 0 || this.getEquippedElytraDurability() >= 30) && this.isUsableElytraEquipped();
               if (elytraRepairedFlag) {
                  this.notifyUser(AutoElytraPilot.Kind.WARN, "Danger at the landing spot - taking off");
                  this.rejectedLandingSpots.add(mc.player.getEntityPos());
                  this.setFlightState(AutoElytraPilot.State.TAKEOFF);
               } else {
                  this.finishTrip(false, "A mob reached the pit stop and the pilot had no way to take off.");
               }
            }
         } else {
            if (this.currentTask == AutoElytraPilot.Task.NONE) {
               this.currentTask = this.chooseNextTask();
               this.taskStartTick = this.pilotTicks;
               if (this.currentTask == AutoElytraPilot.Task.NONE) {
                  if (!this.isUsableElytraEquipped()) {
                     if (this.equipSpareElytra()) {
                        return;
                     }

                     this.finishTrip(false, "No usable elytra left.");
                     return;
                  }

                  if (this.countRockets() == 0) {
                     this.finishTrip(false, this.formatOutOfRocketsMessage());
                     return;
                  }

                  int equippedStack = this.getEquippedElytraDurability();
                  if (equippedStack >= 0 && equippedStack < 40) {
                     this.finishTrip(false, "The elytra is too worn to fly on and can't be repaired.");
                     return;
                  }

                  if (this.getDistanceToDestination() <= 30.0) {
                     this.job = AutoElytraPilot.Job.ARRIVE;
                     this.setFlightState(AutoElytraPilot.State.ARRIVED);
                     return;
                  }

                  this.job = AutoElytraPilot.Job.ARRIVE;
                  this.notifyUser(
                     AutoElytraPilot.Kind.GOOD, "Pit stop done - " + this.countRockets() + " rockets, " + this.countXpBottles() + " XP"
                  );
                  this.setFlightState(AutoElytraPilot.State.TAKEOFF);
                  return;
               }
            }

            switch (this.currentTask) {
               case EAT:
                  if (!this.tryEat(true) || this.pilotTicks - this.taskStartTick > 200) {
                     this.finishTask(AutoElytraPilot.Task.EAT);
                  }
                  break;
               case MEND:
                  this.updateMendingTask();
                  break;
               case BUY_ROCKETS:
                  this.runAhPurchase(Goods.ROCKETS);
                  break;
               case BUY_XP:
                  this.runAhPurchase(Goods.XP);
                  break;
               case SWAP:
                  if (!this.updateSwapTask() || this.pilotTicks - this.taskStartTick > 120) {
                     this.finishTask(AutoElytraPilot.Task.SWAP);
                  }
                  break;
               default:
                  this.currentTask = AutoElytraPilot.Task.NONE;
            }
         }
      }
   }

   public void setSuppressArrivalLogOff(boolean pilotElytraFlag) {
      this.suppressArrivalLogOff = pilotElytraFlag;
   }

   private double getGravityPerTick() {
      return mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING) && mc.player.getVelocity().y <= 0.0 ? 0.01 : 0.08;
   }

   private boolean isRayBlocked(Vec3d vec, Vec3d vec2, double normalizeRaycastValue) {
      BlockHitResult raycast = mc.world
         .raycast(new RaycastContext(vec, vec.add(vec2.normalize().multiply(normalizeRaycastValue)), ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
      return raycast.getType() == Type.BLOCK;
   }

   public boolean changeDestination(int destinationAdjustingValue, int pilotRenderLimit) {
      if (this.isEnabled()
         && mc.player != null
         && this.job == AutoElytraPilot.Job.ARRIVE
         && (this.flightState == AutoElytraPilot.State.CLIMB || this.flightState == AutoElytraPilot.State.CRUISE)) {
         Destination destination = Destination.getDestination(destinationAdjustingValue + " " + pilotRenderLimit);
         if (destination == null) {
            return false;
         } else {
            this.destination = destination;
            this.routeTotalDistance = this.destination.getAmount(mc.player.getX(), mc.player.getZ());
            this.closestApproachDistance = Double.MAX_VALUE;
            this.routeProgressCheckTick = this.pilotTicks;
            this.routeAdjustCount = 0;
            this.currentFlightPlan = null;
            this.flightPlanner.resetClimbPhase();
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean hasBetterSpareElytra() {
      PlayerInventory inventory = mc.player.getInventory();
      int maxEquippedStack = Math.max(0, this.getEquippedElytraDurability());

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(Items.ELYTRA) && getRemainingDurability(stack) > maxEquippedStack + 60) {
            return true;
         }
      }

      return false;
   }

   private boolean equipHotbarElytra() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(Items.ELYTRA) && getRemainingDurability(stack) >= 20) {
            if (inventory.getSelectedSlot() != i) {
               inventory.setSelectedSlot(i);
               return true;
            }

            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            return true;
         }
      }

      return false;
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      if (autoVanish.packet instanceof PlayerPositionLookS2CPacket) {
         this.positionLookPacketCount++;
      } else if (autoVanish.packet instanceof GameMessageS2CPacket gameMessageS2CPacket && !gameMessageS2CPacket.overlay() && gameMessageS2CPacket.content() != null) {
         this.pilotAhBuyer.onChatMessage(gameMessageS2CPacket.content().getString());
      }
   }

   private boolean canUseRocketNow() {
      return this.hasRocketInHand()
            && this.pilotTicks - this.lastRocketUseTick >= 10
            && !this.isContainerScreenOpen()
            && !mc.player.isUsingItem()
            && this.pilotTicks >= this.eatCooldownEndTick
         ? mc.currentScreen == null || mc.currentScreen instanceof GameMenuScreen || mc.currentScreen instanceof ChatScreen
         : false;
   }

   public boolean hasStartError() {
      return this.startErrorText != null || this.startedWithoutWorld;
   }

   private TripStats buildTripStats(boolean destinationTripFlag, String destinationTripText) {
      Vec3d entityPos = mc.player.getEntityPos();
      String destinationTripText2 = this.startDimension == World.END ? "The End" : "Overworld";
      long destinationTripValue = Math.max(0L, (System.currentTimeMillis() - this.tripStartMillis) / 1000L);
      double destinationTripValue2 = this.destination == null ? 0.0 : this.destination.getAmount(entityPos.x, entityPos.z);
      return new TripStats(
         destinationTripFlag,
         destinationTripFlag ? "Destination Reached" : "Trip Stopped",
         destinationTripText == null ? "" : destinationTripText,
         this.destination == null ? "" : this.destination.formatText(),
         destinationTripText2,
         MathHelper.floor(entityPos.x),
         MathHelper.floor(entityPos.y),
         MathHelper.floor(entityPos.z),
         this.tripDistance,
         destinationTripValue,
         this.rocketsUsedCount,
         this.xpBottlesThrown,
         this.pitStopCount,
         this.overchargedPurchaseCount,
         this.tripSpentMoney,
         this.peakSpeedPerSecond,
         destinationTripValue2
      );
   }

   private boolean hasTaskFailed(AutoElytraPilot.Task task) {
      if (this.failedTasksStateStamp != this.stateChangeTick) {
         this.failedTasks.clear();
         this.failedTasksStateStamp = this.stateChangeTick;
      }

      return this.failedTasks.contains(task);
   }

   private void updateArrivedState() {
      if (mc.player.isTouchingWater()) {
         if (mc.player.isSubmergedInWater()) {
            mc.options.jumpKey.setPressed(true);
         } else {
            if (this.getTicksInState() > 20) {
               this.finishTrip(true, null);
            }
         }
      } else if (this.isGliding()) {
         this.job = AutoElytraPilot.Job.ARRIVE;
         this.setFlightState(AutoElytraPilot.State.LAND);
      } else if (mc.player.isOnGround() || mc.player.isTouchingWater()) {
         Vec3d velocity = mc.player.getVelocity();
         if (this.getTicksInState() >= 10 || !(Math.hypot(velocity.x, velocity.z) > 0.03)) {
            this.finishTrip(true, null);
         }
      }
   }

   private void swimToShore() {
      if (this.waterEntryTick < 0) {
         this.waterEntryTick = this.pilotTicks;
      }

      if (this.pilotTicks - this.waterEntryTick > 1200) {
         this.finishTrip(false, "Stuck in water and couldn't reach the shore.");
      } else {
         mc.options.jumpKey.setPressed(true);
         if (this.pilotTicks % 20 == 0 || this.shoreTargetPos == null) {
            this.shoreTargetPos = this.findNearestShoreSpot(32);
         }

         if (this.shoreTargetPos == null) {
            this.requestRotation(mc.player.getYaw(), 0.0F, 0);
         } else {
            float[] wrapDegrees = MathClampUtils.getWrapDegrees(mc.player.getEyePos(), this.shoreTargetPos);
            this.requestRotation(wrapDegrees[0], 0.0F, 0);
            if (Math.abs(MathHelper.wrapDegrees(wrapDegrees[0] - mc.player.getYaw())) < 30.0F) {
               mc.options.forwardKey.setPressed(true);
            }
         }
      }
   }

   private void findLandingSpot(boolean probeDestinationFlag) {
      this.lastLandingProbeTick = this.pilotTicks;
      double x = mc.player.getX();
      double z = mc.player.getZ();
      Spot spot;
      if (this.job == AutoElytraPilot.Job.ARRIVE) {
         double probeDestinationValue = this.destination.x() + 0.5;
         double probeDestinationValue2 = this.destination.z() + 0.5;
         if (this.getTopBlockHeight(MathHelper.floor(probeDestinationValue), MathHelper.floor(probeDestinationValue2)) == Integer.MIN_VALUE) {
            return;
         }

         spot = LandingSpotFinder.getSpot(this.landingSpotProbe, probeDestinationValue, probeDestinationValue2, x, z, 96, false);
         if (spot == null) {
            spot = LandingSpotFinder.findLandingSpot(this.landingSpotProbe, probeDestinationValue, probeDestinationValue2, x, z, 96, false, true);
         }

         if (spot == null) {
            spot = LandingSpotFinder.findLandingSpot(this.landingSpotProbe, probeDestinationValue, probeDestinationValue2, x, z, 160, false, true);
         }

         if (spot == null) {
            spot = LandingSpotFinder.getSpot(this.landingSpotProbe, probeDestinationValue, probeDestinationValue2, x, z, 160, true);
         }

         if (spot == null && ++this.landingProbeAttempts >= 12) {
            spot = LandingSpotFinder.findLandingSpot(this.landingSpotProbe, probeDestinationValue, probeDestinationValue2, x, z, 300, true, true);
         }

         if (spot == null && this.landingProbeAttempts >= 24) {
            spot = LandingSpotFinder.findLandingSpot(this.landingSpotProbe, x, z, x, z, 220, true, true);
         }

         if (spot == null && this.landingProbeAttempts >= 40) {
            this.tripStopReason = "No safe place to land near the destination.";
            this.job = AutoElytraPilot.Job.STOP;
            return;
         }
      } else {
         Vec3d wrapDegrees = getHorizontalDirection(mc.player.getYaw());
         double probeDestinationFactor = probeDestinationFlag ? 30.0 : 110.0;
         double spotValue = x + wrapDegrees.x * probeDestinationFactor;
         double probeDestinationValue4 = z + wrapDegrees.z * probeDestinationFactor;
         spot = LandingSpotFinder.getSpot(this.landingSpotProbe, spotValue, probeDestinationValue4, x, z, probeDestinationFlag ? 120 : 150, false);
         if (spot == null) {
            spot = LandingSpotFinder.findLandingSpot(this.landingSpotProbe, spotValue, probeDestinationValue4, x, z, probeDestinationFlag ? 120 : 150, false, true);
         }

         if (spot == null && probeDestinationFlag) {
            spot = LandingSpotFinder.findLandingSpot(this.landingSpotProbe, x, z, x, z, 200, true, true);
         }
      }

      for (int i = 0; spot != null && i < 4 && this.isNearRejectedSpot(spot); i++) {
         double angle = Math.toRadians(90 * i + 45);
         double probeDestinationValue5 = spot.x() + Math.cos(angle) * 60.0;
         double probeDestinationValue6 = spot.z() + Math.sin(angle) * 60.0;
         spot = LandingSpotFinder.getSpot(this.landingSpotProbe, probeDestinationValue5, probeDestinationValue6, x, z, 90, this.job != AutoElytraPilot.Job.ARRIVE && probeDestinationFlag);
      }

      if (spot != null && !this.isNearRejectedSpot(spot)) {
         Vec3d vec3d = new Vec3d(spot.x() + 0.5, spot.y(), spot.z() + 0.5);
         this.landingGoalPos = vec3d;
         this.landingSpotIsWater = spot.water();
      }
   }

   private int getTicksInState() {
      return this.pilotTicks - this.stateChangeTick;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (this.scene.destinationActive && mc.player != null) {
         this.pilotRender.renderWorld(queueCommand, this.scene);
      }
   }

   private void finishTask(AutoElytraPilot.Task task) {
      this.hasTaskFailed(task);
      this.failedTasks.add(task);
      this.currentTask = AutoElytraPilot.Task.NONE;
   }

   private boolean canBuyGoods(Goods goods) {
      if (this.getRemainingBudget() == 0L || this.buyingBlockedThisTrip) {
         return false;
      } else {
         return goods == Goods.ROCKETS
            ? this.autoBuyRockets.isEnabled() && !this.rocketsUnaffordable && this.rocketBuyFailureCount < 6 && this.pilotTicks >= this.rocketBuyRetryTick
            : this.autoBuyXpBottles.isEnabled() && !this.xpUnaffordable && this.xpBuyFailureCount < 6 && this.pilotTicks >= this.xpBuyRetryTick;
      }
   }

   private boolean isNearRejectedSpot(Spot spot) {
      for (Vec3d vec3d : this.rejectedLandingSpots) {
         if (Math.hypot(vec3d.x - spot.x(), vec3d.z - spot.z()) < 30.0) {
            return true;
         }
      }

      return false;
   }

   private boolean tryRecoverFromFall() {
      if (this.flightState == AutoElytraPilot.State.TAKEOFF || this.isGliding() || mc.player.isOnGround() || this.hasLevitation()) {
         return false;
      } else if (!this.isUsableElytraEquipped() || mc.player.isClimbing() || mc.player.isTouchingWater()) {
         return false;
      } else if (mc.player.getVelocity().y > -0.5) {
         return false;
      } else if (mc.player.fallDistance < 3.0 && !this.hasNoGroundBelow(10)) {
         return false;
      } else {
         this.pressJumpToDeploy();
         this.requestRotation(mc.player.getYaw(), 8.0F, 2);
         return true;
      }
   }

   private Float findOpenSkyYaw() {
      float yaw = mc.player.getYaw();
      if (this.isRayBlocked(mc.player.getEyePos(), new Vec3d(0.0, 1.0, 0.0), 2.2)) {
         return null;
      } else {
         Vec3d entityPos = mc.player.getEntityPos();

         for (float normalizePosValue : new float[]{0.0F, 45.0F, -45.0F, 90.0F, -90.0F, 135.0F, -135.0F, 180.0F}) {
            Vec3d cosSin = MathClampUtils.getCosSin(MathHelper.wrapDegrees(yaw + normalizePosValue), -32.0F);
            Vec3d multiply = new Vec3d(-cosSin.z, 0.0, cosSin.x).normalize().multiply(0.35);
            boolean normalizePosFlag = false;

            for (Vec3d vec3d : new Vec3d[]{
               entityPos.add(0.0, 1.2, 0.0), entityPos.add(0.0, 1.8, 0.0), entityPos.add(0.0, 1.2, 0.0).add(multiply), entityPos.add(0.0, 1.2, 0.0).subtract(multiply)
            }) {
               if (this.isRayBlocked(vec3d, cosSin, 22.0)) {
                  normalizePosFlag = true;
                  break;
               }
            }

            if (!normalizePosFlag) {
               return yaw + normalizePosValue;
            }
         }

         return null;
      }
   }

   private boolean needsResupply() {
      int equippedStack = this.getEquippedElytraDurability();
      return equippedStack >= 0 && equippedStack <= (this.getUnbreakingLevel() >= 1 ? 40 : 60) || this.countRockets() <= 3;
   }

   private void updateDescendState() {
      if (this.isGlidingOrHandleGround()) {
         if (this.job != AutoElytraPilot.Job.ARRIVE || !this.tryStartServiceLanding()) {
            boolean landingGoalFlag = this.job != AutoElytraPilot.Job.ARRIVE && (this.needsResupply() || this.job == AutoElytraPilot.Job.STOP);
            if (this.landingGoalPos == null && this.pilotTicks - this.lastLandingProbeTick >= (landingGoalFlag ? 10 : 20)) {
               this.findLandingSpot(landingGoalFlag);
            }

            double amount = this.landingGoalPos != null ? this.landingGoalPos.x : this.destination.x() + 0.5;
            double amount2 = this.landingGoalPos != null ? this.landingGoalPos.z : this.destination.z() + 0.5;
            double landingGoalValue = this.landingGoalPos != null ? this.landingGoalPos.y : this.getEstimatedGroundY(amount, amount2);
            double hypot = Math.hypot(amount - mc.player.getX(), amount2 - mc.player.getZ());
            double pickingLandingAmount = mc.player.getY() - landingGoalValue;
            this.flightGoal.landingGoalActive = false;
            if (this.landingGoalPos == null && this.job != AutoElytraPilot.Job.ARRIVE) {
               this.flightGoal.steerToLandingSpot = false;
               this.flyTowardGoal(this.destination.x() + 0.5, this.destination.z() + 0.5, Double.NaN, !landingGoalFlag || this.countRockets() > 3);
            } else {
               if (this.landingGoalPos != null) {
                  if (pickingLandingAmount < this.lowestHeightAboveLanding - 3.0) {
                     this.lowestHeightAboveLanding = pickingLandingAmount;
                     this.descentProgressTick = this.pilotTicks;
                  } else if (this.pilotTicks - this.descentProgressTick > 700) {
                     this.notifyUser(AutoElytraPilot.Kind.INFO, "Can't get down to this landing spot - picking another");
                     this.rejectedLandingSpots.add(this.landingGoalPos);
                     this.landingGoalPos = null;
                     this.circlingToLand = false;
                     this.lowestHeightAboveLanding = Double.MAX_VALUE;
                     this.descentProgressTick = this.pilotTicks;
                     return;
                  }
               }

               if (this.landingGoalPos != null && hypot <= 50.0 && pickingLandingAmount <= 28.0 && pickingLandingAmount >= -1.0) {
                  this.setFlightState(AutoElytraPilot.State.LAND);
               } else {
                  double landingGoalValue2 = landingGoalValue + this.flightGoal.approachAltitudeMargin + Math.max(0.0, hypot - this.flightGoal.approachRadius) * this.flightGoal.descentSlope;
                  double landingGoalLimit = mc.player.getY() - landingGoalValue2;
                  if (!this.circlingToLand && hypot < 170.0 && pickingLandingAmount > 32.0 && (landingGoalLimit > 22.0 || hypot < 60.0)) {
                     this.circlingToLand = true;
                  }

                  if (this.circlingToLand && (pickingLandingAmount <= 26.0 || landingGoalLimit < 8.0 && hypot >= 60.0 || hypot > 260.0)) {
                     this.circlingToLand = false;
                  }

                  if (this.circlingToLand) {
                     double angle = Math.atan2(mc.player.getZ() - amount2, mc.player.getX() - amount) + Math.toRadians(55.0);
                     double landingGoalValue3 = amount + Math.cos(angle) * 42.0;
                     double landingGoalValue4 = amount2 + Math.sin(angle) * 42.0;
                     this.flightGoal.landingSpeedLimit = landingGoalLimit > 40.0 ? 3.0 : 1.9;
                     this.flightGoal.steerToLandingSpot = true;
                     this.flightGoal.landingGoalX = amount;
                     this.flightGoal.landingGoalZ = amount2;
                     this.flightGoal.landingDistanceLimit = 42.0;
                     this.flyTowardGoal(landingGoalValue3, landingGoalValue4, landingGoalValue, true);
                  } else {
                     this.flightGoal.landingSpeedLimit = 1.45;
                     this.flightGoal.steerToLandingSpot = false;
                     this.flyTowardGoal(amount, amount2, landingGoalValue, true);
                  }
               }
            }
         }
      }
   }

   private void holdYawLookDown() {
      if (!this.isContainerScreenOpen()) {
         this.requestRotation(mc.player.getYaw(), 40.0F, 0);
      }
   }

   private static Vec3d getHorizontalDirection(float degrees) {
      Vec3d cosSin = MathClampUtils.getCosSin(MathHelper.wrapDegrees(degrees), 0.0F);
      return new Vec3d(cosSin.x, 0.0, cosSin.z);
   }

   private void stopPilot(String pilotElytraText) {
      AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled();

      try {
         this.setEnabled(false);
      } finally {
         try {
            disabledEnabled.close();
         } catch (Exception e) {
         }
      }

      if (pilotElytraText != null) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Elytra Pilot", pilotElytraText, false);
      }
   }

   private String formatStatusText() {
      return switch (this.flightState) {
         case PREPARE -> "Getting ready";
         case TAKEOFF -> "Taking off";
         case CLIMB -> "Climbing to " + (int)this.cruiseAltitude;
         case CRUISE -> "Flying to " + this.destination.formatTextString();
         case DESCEND -> this.job == AutoElytraPilot.Job.ARRIVE
            ? (this.circlingToLand ? "Circling down to land" : "Descending to land")
            : (this.job == AutoElytraPilot.Job.SERVICE ? "Landing for a pit stop" : "Landing to stop safely");
         case LAND -> "Landing";
         case SERVICE -> {
            switch (this.currentTask) {
               case EAT:
                  yield "Eating";
               case MEND:
                  yield "Repairing the elytra";
               case BUY_ROCKETS:
               case BUY_XP:
                  yield this.pilotAhBuyer.isRunning() ? this.pilotAhBuyer.getStatusMessage() : "Buying supplies";
               case SWAP:
                  yield "Swapping elytra";
               default:
                  yield "Pit stop";
            }
         }
         case ARRIVED -> "Destination reached";
         case STOPPED -> "Stopping - " + this.tripStopReason;
      };
   }

   private void syncAhBuyerStats() {
      boolean buyerPilotFlag = this.pilotAhBuyer.getStatus() != Status.IDLE;
      this.pilotAhBuyer.stopRun();
      if (buyerPilotFlag) {
         this.tripSpentMoney = this.tripSpentMoney + this.pilotAhBuyer.getTotalSpent();
         this.overchargedPurchaseCount = this.overchargedPurchaseCount + this.pilotAhBuyer.getPurchaseCount();
         if (this.pilotAhBuyer.isOvercharged()) {
            this.buyingBlockedThisTrip = true;
         }
      }
   }

   private int findBestRocketSlot() {
      int durationFlight = -1;
      int durationFlightValue = -1;
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (PilotAhBuyer.isPlainFireworkRocket(stack)) {
            FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
            int durationFlightLimit = fireworksComponent == null ? 1 : fireworksComponent.flightDuration();
            if (durationFlightLimit > durationFlightValue || durationFlightLimit == durationFlightValue && i == inventory.getSelectedSlot()) {
               durationFlightValue = durationFlightLimit;
               durationFlight = i;
            }
         }
      }

      return durationFlight;
   }

   private boolean hasRocketInHand() {
      return this.findBestRocketSlot() >= 0 || PilotAhBuyer.isPlainFireworkRocket(mc.player.getOffHandStack());
   }

   private void resetState() {
      this.flightState = AutoElytraPilot.State.PREPARE;
      this.job = AutoElytraPilot.Job.ARRIVE;
      this.currentTask = AutoElytraPilot.Task.NONE;
      this.startErrorText = null;
      this.tripStopReason = "";
      this.startedWithoutWorld = false;
      this.internalErrorCount = 0;
      this.destination = null;
      this.startDimension = null;
      this.connection = null;
      this.currentFlightPlan = null;
      this.lastGoalPlanTick = -1000;
      this.surfaceHeightCache.clear();
      this.landingGoalPos = null;
      this.landingSpotIsWater = false;
      this.goAroundCount = 0;
      this.lastApproachPlanTick = -1000;
      this.badApproachPlanCount = 0;
      this.lastLandingProbeTick = -1000;
      this.circlingToLand = false;
      this.rejectedLandingSpots.clear();
      this.lastRocketUseTick = -1000;
      this.rocketBoostEndTick = -1000;
      this.lastTakeoffJumpTick = -1000;
      this.takeoffJumpCount = 0;
      this.lastMendingActionTick = -1000;
      this.eatCooldownEndTick = -1;
      this.mendingFailureCount = 0;
      this.serviceSpotPos = null;
      this.inventorySwapPending = false;
      this.lastCriticalSwapTick = -1000;
      this.rocketBuyFailureCount = 0;
      this.xpBuyFailureCount = 0;
      this.rocketBuyRetryTick = 0;
      this.xpBuyRetryTick = 0;
      this.rocketMoneyShortCount = 0;
      this.xpMoneyShortCount = 0;
      this.buyingBlockedThisTrip = false;
      this.takeoffAttemptCount = 0;
      this.landingProbeAttempts = 0;
      this.waterEntryTick = -1;
      this.lowestHeightAboveLanding = Double.MAX_VALUE;
      this.descentProgressTick = 0;
      this.rocketsUnaffordable = false;
      this.xpUnaffordable = false;
      this.tripSpentMoney = 0L;
      this.overchargedPurchaseCount = 0;
      this.tripDistance = 0.0;
      this.peakSpeedPerSecond = 0.0;
      this.lastTripPos = null;
      this.rocketsUsedCount = 0;
      this.xpBottlesThrown = 0;
      this.pitStopCount = 0;
      this.closestApproachDistance = Double.MAX_VALUE;
      this.routeProgressCheckTick = 0;
      this.routeAdjustCount = 0;
      this.lastDamageTick = -1000;
      this.positionLookPacketCount = 0;
      this.lastSeenPositionLookCount = 0;
      this.lastRubberbandWindowTick = 0;
      this.recentPositionLookCount = 0;
      this.rocketUseEnabledTick = -1000;
      this.rotationRequested = false;
      this.humanAim.resetAimState();
      this.rotationSmoother.deactivate();
      this.flightPlanner.resetClimbPhase();
      this.pilotStatus = PilotStatus.INSTANCE;
      this.lastNotificationText = "";
      this.lastNotificationTick = -10000;
      this.routeTotalDistance = -1.0;
      this.scene.destinationActive = false;
   }

   public AutoElytraPilot() {
      super("Auto Elytra Pilot", IconsPngType.BOTS);
      this.autoMending = new BooleanSetting("Auto Mending", "Lands and throws XP bottles at your feet to repair a Mending elytra long before it can break.", true);
      this.autoBuyRockets = new BooleanSetting("Auto Buy Rockets", "Buys Flight Duration 3 rockets from /ah when you run low.", true);
      this.rocketStacks = new IntegerSetting("Rocket Stacks", "How many stacks of rockets to keep. It buys up to this many.", 3, 1, 3);
      this.autoBuyXpBottles = new BooleanSetting("Auto Buy XP Bottles", "Buys XP bottles from /ah for mending when you run low.", true);
      this.xpBottleStacks = new IntegerSetting("XP Bottle Stacks", "How many stacks of XP bottles to keep. It buys up to this many.", 2, 1, 3);
      this.priceLimit = new EnumSetting(
         "Price Limit",
         "Most it pays per stack. Cheap: rockets $35K, XP $320K. Normal: rockets $60K, XP $450K. High: rockets $150K, XP $700K.",
         "Normal",
         "Cheap",
         "Normal",
         "High"
      );
      this.tripBudget = new EnumSetting("Trip Budget", "Most money it spends on one trip.", "$2M", "$500K", "$1M", "$2M", "$5M", "$10M", "No Limit");
      this.logOffOnArrival = new BooleanSetting(
         "Log Off On Arrival", "Logs you off safely after landing and shows a trip summary. Also logs off if the trip has to stop.", true
      );
      this.waypointLine = new BooleanSetting("Waypoint Line", "Draws the route and a beacon at the destination.", true);
      this.statusCard = new BooleanSetting("Status Card", "Card at the top of the screen with distance, ETA, rockets, XP and elytra health.", true);
      this.notifications = new EnumSetting("Notifications", "How the pilot tells you what it is doing.", "Toast", "Toast", "Chat", "Off");
      this.flightState = AutoElytraPilot.State.PREPARE;
      this.job = AutoElytraPilot.Job.ARRIVE;
      this.currentTask = AutoElytraPilot.Task.NONE;
      this.tripStopReason = "";
      this.flightPlanner = new FlightPlanner();
      this.flightGoal = new Goal();
      this.glideBody = new Body();
      this.lastGoalPlanTick = -1000;
      this.surfaceHeightCache = new Long2IntOpenHashMap();
      this.cruiseAltitude = 290.0;
      this.landingPlanner = new LandingPlanner();
      this.lastApproachPlanTick = -1000;
      this.lastLandingProbeTick = -1000;
      this.rejectedLandingSpots = new ArrayList<>();
      this.lastRocketUseTick = -1000;
      this.rocketBoostEndTick = -1000;
      this.rocketFlightDuration = 3;
      this.lastTakeoffJumpTick = -1000;
      this.lastMendingActionTick = -1000;
      this.eatCooldownEndTick = -1;
      this.lastCriticalSwapTick = -1000;
      this.pilotAhBuyer = new PilotAhBuyer();
      this.waterEntryTick = -1;
      this.lowestHeightAboveLanding = Double.MAX_VALUE;
      this.closestApproachDistance = Double.MAX_VALUE;
      this.lastHealth = 20.0F;
      this.lastDamageTick = -1000;
      this.rocketUseEnabledTick = -1000;
      this.humanAim = new HumanAimRotator(new Random());
      this.rotationSmoother = new RotationInterpolator();
      this.pilotRender = new PilotRender();
      this.scene = new Scene();
      this.pilotStatusCard = new PilotStatusCard();
      this.pilotStatus = PilotStatus.INSTANCE;
      this.menuSnapshot = Snapshot.INSTANCE;
      this.lastNotificationText = "";
      this.lastNotificationTick = -10000;
      this.routeTotalDistance = -1.0;
      this.failedTasks = EnumSet.noneOf(AutoElytraPilot.Task.class);
      this.failedTasksStateStamp = -1;
      this.terrain = (autoElytraValue, autoElytraValue2) -> {
         long autoElytraValue3 = (long)autoElytraValue << 32 ^ autoElytraValue2 & 4294967295L;
         if (this.surfaceHeightCache.containsKey(autoElytraValue3)) {
            return this.surfaceHeightCache.get(autoElytraValue3);
         } else {
            WorldChunk worldChunk = mc.world.getChunkManager().getWorldChunk(autoElytraValue >> 4, autoElytraValue2 >> 4);
            int minValue4;
            if (worldChunk == null) {
               minValue4 = Integer.MIN_VALUE;
            } else {
               int sampleHeightmap = worldChunk.sampleHeightmap(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, autoElytraValue & 15, autoElytraValue2 & 15);
               minValue4 = sampleHeightmap < mc.world.getBottomY() ? mc.world.getBottomY() - 64 : sampleHeightmap + 1;
            }

            this.surfaceHeightCache.put(autoElytraValue3, minValue4);
            return minValue4;
         }
      };
      this.landingSpotProbe = new Probe() {
         @Override
         public int getBlockClassAt(int probePilotValue, int blockValue, int z2) {
            return AutoElytraPilot.this.classifyBlockAt(probePilotValue, blockValue, z2);
         }

         @Override
         public int getHeightAt(int chunkHeightmapValue, int probePilotValue5) {
            return AutoElytraPilot.this.getTopBlockHeight(chunkHeightmapValue, probePilotValue5);
         }
      };
      this.addSettings(
         new ConfigSetting[]{
            this.destinationInput,
            this.flightHeight,
            this.autoMending,
            this.autoBuyRockets,
            this.rocketStacks,
            this.autoBuyXpBottles,
            this.xpBottleStacks,
            this.priceLimit,
            this.tripBudget,
            this.logOffOnArrival,
            this.waypointLine,
            this.statusCard,
            this.notifications
         }
      );
   }

   private void checkRouteProgress(double amount) {
      if (amount < this.closestApproachDistance - 60.0) {
         this.closestApproachDistance = amount;
         this.routeProgressCheckTick = this.pilotTicks;
         this.routeAdjustCount = 0;
      } else if (this.pilotTicks - this.routeProgressCheckTick >= 900) {
         this.routeProgressCheckTick = this.pilotTicks;
         this.routeAdjustCount++;
         this.notifyUser(AutoElytraPilot.Kind.WARN, "Not getting closer - adjusting the route");
         this.cruiseAltitude = Math.min(316.0, this.cruiseAltitude + 20.0);
         this.flightPlanner.resetClimbPhase();
         if (this.routeAdjustCount >= 6) {
            this.tripStopReason = "The pilot stopped making progress toward the destination.";
            this.job = AutoElytraPilot.Job.STOP;
            this.landingGoalPos = null;
            this.setFlightState(AutoElytraPilot.State.DESCEND);
         }
      }
   }

   private boolean isBelowTerrainSurface() {
      int surface = this.terrain.surface(MathHelper.floor(mc.player.getX()), MathHelper.floor(mc.player.getZ()));
      return surface != Integer.MIN_VALUE && surface > mc.player.getY() + 1.5 && this.getTicksInState() < 200;
   }

   private boolean isGroundContactAt(int jobStopValue, int blockValue, int z2) {
      int blockState = this.classifyBlockAt(jobStopValue, blockValue, z2);
      return blockState == 1 ? this.landingSpotIsWater || this.job == AutoElytraPilot.Job.STOP : blockState == 0 || blockState == 3 || blockState == 5;
   }

   private boolean hasClearanceAbove(int floorContractValue) {
      Box contract = mc.player.getBoundingBox().contract(0.02, 0.0, 0.02);
      int floorMinX = MathHelper.floor(contract.minX);
      int floorMaxX = MathHelper.floor(contract.maxX);
      int floorMinZ = MathHelper.floor(contract.minZ);
      int floorMaxZ = MathHelper.floor(contract.maxZ);
      int floor = MathHelper.floor(mc.player.getY() + 0.6);

      for (int i = floorMinX; i <= floorMaxX; i++) {
         for (int j = floorMinZ; j <= floorMaxZ; j++) {
            for (int k = floor; k <= floor + floorContractValue; k++) {
               if (this.hasCollisionAt(new BlockPos(i, k, j))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private boolean isRocketBoostActive() {
      if (this.pilotTicks < this.rocketBoostEndTick - 12 && this.pilotTicks - this.lastRocketUseTick < 4) {
         return true;
      } else {
         return this.pilotTicks - this.lastRocketUseTick > 80
            ? false
            : !mc.world
               .getEntitiesByClass(
                  FireworkRocketEntity.class,
                  mc.player.getBoundingBox().expand(3.0),
                  fireworkRocketEntity -> fireworkRocketEntity.isAlive() && fireworkRocketEntity.getOwner() == mc.player && !fireworkRocketEntity.wasShotAtAngle()
               )
               .isEmpty();
      }
   }

   private void updateStoppedState() {
      if (mc.player.isTouchingWater()) {
         if (mc.player.isSubmergedInWater()) {
            mc.options.jumpKey.setPressed(true);
         } else {
            this.finishTrip(false, this.tripStopReason.isEmpty() ? "The trip had to stop." : this.tripStopReason);
         }
      } else if (this.isGliding()) {
         this.job = AutoElytraPilot.Job.STOP;
         this.setFlightState(AutoElytraPilot.State.LAND);
      } else if (mc.player.isOnGround() || mc.player.isTouchingWater()) {
         this.finishTrip(false, this.tripStopReason.isEmpty() ? "The trip had to stop." : this.tripStopReason);
      }
   }

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      if (mc.player != null && !Freecam.isInstanceFlag() && !Freelook.isFlag()) {
         if (this.rotationSmoother.isApplicableAt(this.pilotTicks, mc.player.getYaw(), mc.player.getPitch())) {
            float tickDelta = deltaAngle.getTickDelta();
            deltaAngle.setAngleDegrees(this.rotationSmoother.getInterpolatedYaw(tickDelta));
            deltaAngle.setLerp(this.rotationSmoother.getInterpolatedPitch(tickDelta));
            deltaAngle.updateFlag();
         }
      }
   }

   private void useRocket() {
      if (this.isGliding() && !this.isContainerScreenOpen()) {
         int durationFlight = this.findBestRocketSlot();
         boolean handPlayerFlag = durationFlight < 0 && PilotAhBuyer.isPlainFireworkRocket(mc.player.getOffHandStack());
         if (durationFlight < 0 && !handPlayerFlag) {
            if (this.countRockets() > 0) {
               this.requestHotbarSwap(PilotAhBuyer::isPlainFireworkRocket);
            }
         } else if (!this.isViewBlocked(MathClampUtils.getCosSin(mc.player.getYaw(), mc.player.getPitch()), 10.0)) {
            if (!handPlayerFlag && mc.player.getInventory().getSelectedSlot() != durationFlight) {
               mc.player.getInventory().setSelectedSlot(durationFlight);
            } else {
               Hand hand = handPlayerFlag ? Hand.OFF_HAND : Hand.MAIN_HAND;
               FireworksComponent fireworksComponent = (handPlayerFlag ? mc.player.getOffHandStack() : mc.player.getInventory().getStack(durationFlight))
                  .get(DataComponentTypes.FIREWORKS);
               int max = fireworksComponent == null ? 1 : Math.max(1, fireworksComponent.flightDuration());
               mc.interactionManager.interactItem(mc.player, hand);
               mc.player.swingHand(hand);
               this.lastRocketUseTick = this.pilotTicks;
               this.rocketFlightDuration = max;
               this.rocketBoostEndTick = this.pilotTicks + GlidePhysics.getRocketBoostTicks(max);
               this.rocketsUsedCount++;
               if (!this.hasRocketInHand() && this.countRockets() > 0) {
                  this.requestHotbarSwap(PilotAhBuyer::isPlainFireworkRocket);
               }
            }
         }
      }
   }

   private long getRemainingBudget() {
      String formatText = this.tripBudget.getCurrentOption();

      long autoElytraValue = switch (formatText) {
         case "$500K" -> 500000L;
         case "$1M" -> 1000000L;
         case "$2M" -> 2000000L;
         case "$5M" -> 5000000L;
         case "$10M" -> 10000000L;
         default -> -1L;
      };
      return autoElytraValue < 0L ? -1L : Math.max(0L, autoElytraValue - this.tripSpentMoney);
   }

   private void trackTripProgress() {
      Vec3d entityPos = mc.player.getEntityPos();
      if (this.lastTripPos != null && this.isGliding()) {
         double deltaX = entityPos.x - this.lastTripPos.x;
         double deltaZ = entityPos.z - this.lastTripPos.z;
         double hypot = Math.hypot(deltaX, deltaZ);
         if (hypot < 20.0) {
            this.tripDistance += hypot;
            this.peakSpeedPerSecond = Math.max(this.peakSpeedPerSecond, hypot * 20.0);
         }
      }

      this.lastTripPos = entityPos;
      if (!mc.player.isTouchingWater()) {
         this.waterEntryTick = -1;
         this.shoreTargetPos = null;
      }

      float health = mc.player.getHealth();
      if (health < this.lastHealth - 0.5F) {
         this.lastDamageTick = this.pilotTicks;
      }

      this.lastHealth = health;
      if (this.flightState == AutoElytraPilot.State.CRUISE) {
         this.tryEat(false);
      }
   }

   public void retargetToCurrentPosition() {
      if (this.isEnabled() && mc.player != null && this.job == AutoElytraPilot.Job.SERVICE) {
         Destination destination = Destination.getDestination(MathHelper.floor(mc.player.getX()) + " " + MathHelper.floor(mc.player.getZ()));
         if (destination != null) {
            this.destination = destination;
            this.routeTotalDistance = 0.0;
         }
      }
   }

   private void handleTouchdown() {
      Vec3d entityPos = mc.player.getEntityPos();
      if (this.job == AutoElytraPilot.Job.ARRIVE) {
         double vecDestinationValue = this.landingGoalPos == null ? Double.MAX_VALUE : Math.hypot(this.landingGoalPos.x - this.destination.x() - 0.5, this.landingGoalPos.z - this.destination.z() - 0.5);
         if (!(this.getDistanceToDestination() <= 72.0)
            && (this.landingGoalPos == null || !(this.getHorizontalDistanceTo(this.landingGoalPos) <= 25.0) && !(this.getDistanceToDestination() <= vecDestinationValue + 20.0))) {
            if (this.landingGoalPos != null) {
               this.rejectedLandingSpots.add(this.landingGoalPos);
            }

            this.landingGoalPos = null;
            this.setFlightState(AutoElytraPilot.State.PREPARE);
         } else {
            this.setFlightState(AutoElytraPilot.State.ARRIVED);
         }
      } else if (this.job == AutoElytraPilot.Job.STOP) {
         this.setFlightState(AutoElytraPilot.State.STOPPED);
      } else {
         this.setFlightState(AutoElytraPilot.State.SERVICE);
      }
   }

   private void startGoAround(String landingStopText) {
      this.goAroundCount++;
      this.notifyUser(AutoElytraPilot.Kind.INFO, "Go-around (" + landingStopText + ")");
      if (this.goAroundCount >= 4) {
         if (this.landingGoalPos != null) {
            this.rejectedLandingSpots.add(this.landingGoalPos);
         }

         this.landingGoalPos = null;
         this.goAroundCount = 0;
      }

      this.setFlightState(AutoElytraPilot.State.DESCEND);
   }

   private boolean hasUsableElytra() {
      if (this.isUsableElytraEquipped()) {
         return true;
      } else {
         PlayerInventory inventory = mc.player.getInventory();

         for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(Items.ELYTRA) && isElytraUsable(stack)) {
               return true;
            }
         }

         return false;
      }
   }

   private double getCruiseAltitudeFor(double autoElytraValue) {
      double autoElytraBound = this.flightHeight.getValue();
      double floorBottom = this.getEstimatedGroundY(this.destination.x() + 0.5, this.destination.z() + 0.5);
      double autoElytraBound2 = floorBottom + this.flightGoal.approachAltitudeMargin + Math.max(0.0, autoElytraValue - this.flightGoal.approachRadius) * this.flightGoal.descentSlope + 30.0;
      double hashRegistryBound = this.startDimension == World.END ? 150.0 : 140.0;
      return Math.max(hashRegistryBound, Math.min(autoElytraBound, autoElytraBound2));
   }

   private boolean isGlidingOrHandleGround() {
      if (this.isGliding()) {
         return true;
      } else if (mc.player.isOnGround()) {
         if (this.flightState != AutoElytraPilot.State.LAND && this.flightState != AutoElytraPilot.State.DESCEND) {
            this.setFlightState(AutoElytraPilot.State.PREPARE);
            return false;
         } else {
            this.handleTouchdown();
            return false;
         }
      } else if (mc.player.isTouchingWater()) {
         this.setFlightState(AutoElytraPilot.State.PREPARE);
         return false;
      } else {
         if (mc.player.getVelocity().y < 0.0 && !this.hasLevitation()) {
            this.pressJumpToDeploy();
         }

         return false;
      }
   }

   private void finishTrip(boolean pilotElytraFlag, String pilotElytraText) {
      if (pilotElytraFlag || !this.isGliding() && (mc.player.isOnGround() || mc.player.isTouchingWater())) {
         this.syncAhBuyerStats();
         TripStats destinationTrip = this.buildTripStats(pilotElytraFlag, pilotElytraText);
         String pilotElytraText2 = pilotElytraFlag ? destinationTrip.formatTripDestinationSuccess() : "Trip stopped: " + pilotElytraText;
         if (mc.player != null) {
            mc.player
               .sendMessage(
                  Text.literal("[Elytra Pilot] ").formatted(pilotElytraFlag ? Formatting.GREEN : Formatting.RED).append(Text.literal(pilotElytraText2).formatted(Formatting.WHITE)),
                  false
               );
         }

         boolean pilotElytraFlag2 = this.logOffOnArrival.isEnabled() && !this.suppressArrivalLogOff;
         this.stopPilot(null);
         DisabledEnabled.getInstance().handleDisabledEnabled("Elytra Pilot", pilotElytraFlag ? "Destination reached" : pilotElytraText, pilotElytraFlag);
         if (pilotElytraFlag2) {
            ArrivalScreen.handleSendInstance(destinationTrip);
         }
      } else {
         this.tripStopReason = pilotElytraText == null ? "" : pilotElytraText;
         this.job = AutoElytraPilot.Job.STOP;
         if (this.flightState != AutoElytraPilot.State.LAND && this.flightState != AutoElytraPilot.State.DESCEND && this.flightState != AutoElytraPilot.State.STOPPED) {
            this.landingGoalPos = null;
            this.setFlightState(AutoElytraPilot.State.DESCEND);
         }
      }
   }

   private int getSwapDurabilityThreshold() {
      int autoElytraLimit = this.getUnbreakingLevel();
      int equippedDamage = this.getElytraMaxDurability();
      return Math.max(autoElytraLimit >= 3 ? 50 : (autoElytraLimit >= 1 ? 80 : 110), (int)(equippedDamage * (autoElytraLimit >= 3 ? 0.15 : (autoElytraLimit >= 1 ? 0.2 : 0.27))));
   }

   private void syncGlideBody() {
      Vec3d velocity = mc.player.getVelocity();
      this.glideBody
         .setState(
            mc.player.getX(),
            mc.player.getY(),
            mc.player.getZ(),
            velocity.x,
            velocity.y,
            velocity.z,
            mc.player.getYaw(),
            mc.player.getPitch()
         );
   }

   private static boolean hasEnchantment(ItemStack itemStack, RegistryKey<Enchantment> registryKey) {
      if (itemStack.isEmpty()) {
         return false;
      } else {
         ItemEnchantmentsComponent orDefault = itemStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

         for (RegistryEntry registryEntry : orDefault.getEnchantments()) {
            if (registryEntry.matchesKey(registryKey)) {
               return true;
            }
         }

         return false;
      }
   }

   private void updateClimbState() {
      if (this.isGlidingOrHandleGround()) {
         double destinationPlayer = this.getDistanceToDestination();
         this.cruiseAltitude = this.getCruiseAltitudeFor(destinationPlayer);
         if (!this.tryStartServiceLanding()) {
            double y = mc.player.getY();
            boolean floorPlayer = this.isAboveTerrainAround();
            if (y >= this.cruiseAltitude - 15.0 || !floorPlayer && this.getTicksInState() > 600) {
               this.setFlightState(destinationPlayer <= this.getDescentStartDistance() ? AutoElytraPilot.State.DESCEND : AutoElytraPilot.State.CRUISE);
            } else if (floorPlayer) {
               this.requestRotation(mc.player.getYaw(), -85.0F, 0);
               if (!this.isRocketBoostActive() && this.canUseRocketNow() && mc.player.getPitch() < -60.0F) {
                  this.useRocket();
               }
            } else if (this.isBelowTerrainSurface()) {
               this.requestRotation(this.openSkyYaw, -32.0F, 2);
               if (!this.isRocketBoostActive() && this.canUseRocketNow() && Math.abs(mc.player.getPitch() + 32.0F) < 12.0F) {
                  this.useRocket();
               }
            } else {
               this.flightGoal.landingGoalActive = true;
               this.flightGoal.steerToLandingSpot = false;
               this.flyTowardGoal(this.destination.x() + 0.5, this.destination.z() + 0.5, Double.NaN, true);
            }
         }
      }
   }

   private int findFreeHotbarSlot() {
      PlayerInventory inventory = mc.player.getInventory();
      int base = AutoInventoryTotem.getReserveSlotIfEnabled();

      for (int i = 0; i <= 8; i++) {
         if (i != base && !AutoInventoryTotem.wasTotemSlot(i) && inventory.getStack(i).isEmpty()) {
            return i;
         }
      }

      for (int j = 8; j >= 0; j--) {
         if (j != base && !AutoInventoryTotem.wasTotemSlot(j)) {
            ItemStack stack = inventory.getStack(j);
            if (!PilotAhBuyer.isPlainFireworkRocket(stack)
               && !stack.isOf(Items.EXPERIENCE_BOTTLE)
               && !stack.isOf(Items.ELYTRA)
               && !stack.isOf(Items.TOTEM_OF_UNDYING)
               && !isEdibleFood(stack, true)) {
               return j;
            }
         }
      }

      return inventory.getSelectedSlot() == 8 ? 7 : 8;
   }

   private AutoElytraPilot.Task chooseNextTask() {
      HungerManager hungerManager = mc.player.getHungerManager();
      if (this.isHungry() && this.hasFood() && !this.hasTaskFailed(AutoElytraPilot.Task.EAT)) {
         return AutoElytraPilot.Task.EAT;
      } else {
         int equippedStack = this.getEquippedElytraDurability();
         int equippedDamage = this.getElytraMaxDurability();
         boolean buyMendFlag = this.autoMending.isEnabled() && this.isEquippedElytraMending() && this.mendingFailureCount < 3;
         boolean buyMendFlag2 = this.countXpBottles() > 0 || this.canBuyGoods(Goods.XP);
         boolean buyMendFlag3 = buyMendFlag && equippedStack >= 0 && equippedStack < equippedDamage * 0.97;
         if (equippedStack >= 0 && equippedStack <= this.getSwapDurabilityThreshold() && (!buyMendFlag || !buyMendFlag2) && this.hasBetterSpareElytra() && !this.hasTaskFailed(AutoElytraPilot.Task.SWAP)) {
            return AutoElytraPilot.Task.SWAP;
         } else {
            boolean buyMendFlag4 = this.getDistanceToDestination() < 1500.0 && this.countRockets() >= 4;
            if (!buyMendFlag4) {
               if (buyMendFlag3 && this.countXpBottles() == 0 && this.canBuyGoods(Goods.XP) && !this.hasTaskFailed(AutoElytraPilot.Task.BUY_XP)) {
                  return AutoElytraPilot.Task.BUY_XP;
               } else if (buyMendFlag3 && this.countXpBottles() > 0 && !this.hasTaskFailed(AutoElytraPilot.Task.MEND)) {
                  return AutoElytraPilot.Task.MEND;
               } else if (this.countRockets() < this.getRocketTargetCount() / 2 && this.canBuyGoods(Goods.ROCKETS) && !this.hasTaskFailed(AutoElytraPilot.Task.BUY_ROCKETS)
                  )
                {
                  return AutoElytraPilot.Task.BUY_ROCKETS;
               } else {
                  return buyMendFlag
                        && this.countXpBottles() < this.getXpBottleTargetCount() / 2
                        && this.canBuyGoods(Goods.XP)
                        && !this.hasTaskFailed(AutoElytraPilot.Task.BUY_XP)
                     ? AutoElytraPilot.Task.BUY_XP
                     : AutoElytraPilot.Task.NONE;
               }
            } else {
               return buyMendFlag3 && this.countXpBottles() > 0 && !this.hasTaskFailed(AutoElytraPilot.Task.MEND)
                  ? AutoElytraPilot.Task.MEND
                  : AutoElytraPilot.Task.NONE;
            }
         }
      }
   }

   private void selectRocketSlot() {
      int durationFlight = this.findBestRocketSlot();
      if (durationFlight >= 0 && mc.player.getInventory().getSelectedSlot() != durationFlight) {
         mc.player.getInventory().setSelectedSlot(durationFlight);
      }
   }

   private void updateLandState() {
      if (mc.player.isTouchingWater()) {
         if (mc.player.isSubmergedInWater()) {
            mc.options.jumpKey.setPressed(true);
         } else {
            this.handleTouchdown();
         }
      } else if (mc.player.isOnGround() && !this.isGliding()) {
         this.handleTouchdown();
      } else if (!this.isGliding()) {
         if (mc.player.getVelocity().y < -0.3 && !this.hasLevitation()) {
            this.pressJumpToDeploy();
         }
      } else if (this.landingGoalPos == null) {
         this.setFlightState(AutoElytraPilot.State.DESCEND);
      } else {
         double hypot = this.getHorizontalDistanceTo(this.landingGoalPos);
         double deltaY = mc.player.getY() - this.landingGoalPos.y;
         if (!(deltaY < -4.0) && this.getTicksInState() <= 700 && this.badApproachPlanCount <= 24 && !(hypot > 90.0)) {
            if (this.pilotTicks - this.lastApproachPlanTick >= 2) {
               this.lastApproachPlanTick = this.pilotTicks;
               this.syncGlideBody();
               this.landingPlanner
                  .planLanding(mc, this.glideBody, this.landingGoalPos, this.getRocketBoostTicksLeft(), this.getGravityPerTick(), this::isGroundContactAt);
               this.badApproachPlanCount = this.landingPlanner.lastPlanScore >= 900.0 ? this.badApproachPlanCount + 1 : 0;
            }

            this.requestRotation(this.landingPlanner.bestYaw, this.landingPlanner.bestPitch, 1);
            this.fastRotationRequested = true;
         } else {
            this.startGoAround("approach failed");
         }
      }
   }

   private void updateTakeoffState() {
      if (mc.player.isTouchingWater()) {
         this.swimToShore();
      } else if (this.isGliding()) {
         this.setFlightState(AutoElytraPilot.State.CLIMB);
      } else if (this.takeoffAttemptCount > 6) {
         this.finishTrip(false, "Couldn't take off here. It needs open sky above you.");
      } else if (!this.hasRocketInHand()) {
         if (this.countRockets() > 0) {
            this.requestHotbarSwap(PilotAhBuyer::isPlainFireworkRocket);
            if (this.getTicksInState() < 120) {
               return;
            }
         }

         this.setFlightState(AutoElytraPilot.State.PREPARE);
      } else {
         this.selectRocketSlot();
         if (mc.player.isOnGround()) {
            if (this.pilotTicks - this.lastTakeoffJumpTick >= 14) {
               if (this.takeoffJumpCount >= 5) {
                  this.finishTrip(false, "Couldn't take off here. It needs open sky above you.");
               } else {
                  this.hasOpenSkyAbove = this.hasClearanceAbove(16);
                  if (!this.hasOpenSkyAbove) {
                     Float openSomewhereAmount = this.findOpenSkyYaw();
                     if (openSomewhereAmount == null) {
                        this.finishTrip(false, "No open sky to take off from. Stand somewhere open.");
                        return;
                     }

                     this.openSkyYaw = openSomewhereAmount;
                  }

                  float skyOpenValue = this.hasOpenSkyAbove ? mc.player.getYaw() : this.openSkyYaw;
                  float skyOpenValue2 = this.hasOpenSkyAbove ? -88.0F : -35.0F;
                  this.requestRotation(skyOpenValue, skyOpenValue2, 0);
                  if (!(Math.abs(MathHelper.wrapDegrees(skyOpenValue - mc.player.getYaw())) > 10.0F)
                     && !(Math.abs(mc.player.getPitch() - skyOpenValue2) > 10.0F)) {
                     mc.options.jumpKey.setPressed(true);
                     this.lastTakeoffJumpTick = this.pilotTicks;
                     this.takeoffJumpCount++;
                  }
               }
            }
         } else {
            this.requestRotation(this.hasOpenSkyAbove ? mc.player.getYaw() : this.openSkyYaw, this.hasOpenSkyAbove ? -88.0F : -35.0F, 0);
            if (mc.player.getVelocity().y < 0.12 && this.pilotTicks - this.lastTakeoffJumpTick >= 2) {
               this.pressJumpToDeploy();
            }
         }
      }
   }

   private void flyTowardGoal(double amount, double amount2, double landingGoalValue, boolean goalFlag) {
      if (this.pilotTicks - this.lastGoalPlanTick >= 2 || this.currentFlightPlan == null) {
         this.lastGoalPlanTick = this.pilotTicks;
         this.surfaceHeightCache.clear();
         this.syncGlideBody();
         this.flightGoal.goalX = amount;
         this.flightGoal.goalZ = amount2;
         this.flightGoal.altitudeCeiling = this.cruiseAltitude;
         this.flightGoal.goalY = landingGoalValue;
         this.flightGoal.floorClearanceHeight = 14.0;
         this.flightGoal.unknownTerrainHeight = this.startDimension == World.END ? 185.0 : 262.0;
         this.flightGoal.goalMinY = this.startDimension == World.END ? 35.0 : Double.NEGATIVE_INFINITY;
         this.flightGoal.rocketFlightDuration = this.rocketFlightDuration;
         this.flightGoal.gravity = this.getGravityPerTick();
         boolean chunkFloor = this.isChunkLoadedAhead(110.0);
         this.flightGoal.rocketsAllowed = goalFlag && this.pilotTicks >= this.rocketUseEnabledTick && (chunkFloor || mc.player.getY() < this.cruiseAltitude - 50.0);
         int stackHand = this.countRockets();
         this.flightGoal.rocketBoostPenalty = stackHand < 12 ? 140.0 : (stackHand < 32 ? 95.0 : 70.0);
         this.currentFlightPlan = this.flightPlanner.computeBestPlan(this.terrain, this.glideBody, this.getRocketBoostTicksLeft(), this.canUseRocketNow(), this.flightGoal);
      }

      Plan plan = this.currentFlightPlan;
      this.requestRotation(plan.targetYaw, plan.planPitch, !plan.hitsGround && !plan.fromWideSearch ? 0 : 2);
      if (plan.hitsGround || plan.fromWideSearch) {
         this.fastRotationRequested = true;
      }

      if (plan.usesRocketBoost
         && !this.isRocketBoostActive()
         && this.canUseRocketNow()
         && Math.abs(mc.player.getPitch() - plan.planPitch) < 20.0F
         && Math.abs(MathHelper.wrapDegrees(plan.targetYaw - mc.player.getYaw())) < 30.0F) {
         this.useRocket();
      }
   }

   private int countXpBottles() {
      PlayerInventory inventory = mc.player.getInventory();
      int experienceBottle = 0;

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(Items.EXPERIENCE_BOTTLE)) {
            experienceBottle += stack.getCount();
         }
      }

      return experienceBottle;
   }

   @Override
   protected void onReset() {
      if (mc.player != null
         && mc.world != null
         && mc.interactionManager != null
         && mc.getNetworkHandler() != null) {
         try {
            this.tickPilot();
            this.internalErrorCount = 0;
         } catch (Throwable e) {
            this.internalErrorCount++;
            if (this.internalErrorCount > 200) {
               this.stopPilot("Stopped after an internal error.");
            }
         }
      }
   }

   private double getDescentStartDistance() {
      double floorBottom = this.getEstimatedGroundY(this.destination.x() + 0.5, this.destination.z() + 0.5);
      double goalDestinationValue = Math.max(0.0, mc.player.getY() - floorBottom - this.flightGoal.approachAltitudeMargin);
      return this.flightGoal.approachRadius + goalDestinationValue / this.flightGoal.descentSlope + 60.0;
   }

   private static String formatCoordinates(Vec3d vec) {
      return String.format(Locale.ROOT, "%.1f,%.1f,%.1f", vec.x, vec.y, vec.z);
   }

   private boolean hasLevitation() {
      return mc.player.hasStatusEffect(StatusEffects.LEVITATION);
   }

   private void trackRubberbanding() {
      int value = this.positionLookPacketCount;
      if (value == this.lastSeenPositionLookCount) {
         if (this.pilotTicks - this.lastRubberbandWindowTick > 200) {
            this.lastRubberbandWindowTick = this.pilotTicks;
            this.recentPositionLookCount = 0;
         }
      } else {
         int mendingStoppedValue = value - this.lastSeenPositionLookCount;
         this.lastSeenPositionLookCount = value;
         if (this.pilotTicks - this.lastRubberbandWindowTick > 200) {
            this.lastRubberbandWindowTick = this.pilotTicks;
            this.recentPositionLookCount = 0;
         }

         this.recentPositionLookCount += mendingStoppedValue;
         if (this.isGliding() && this.recentPositionLookCount >= 3) {
            this.rocketUseEnabledTick = this.pilotTicks + 60;
         }
      }
   }

   private boolean equipSpareElytra() {
      int instanceInventory = SelectedInventoryUtils.getInstanceInventory(stack -> stack.isOf(Items.ELYTRA) && getRemainingDurability(stack) > 40);
      if (instanceInventory >= 0) {
         if (mc.player.getInventory().getSelectedSlot() != instanceInventory) {
            mc.player.getInventory().setSelectedSlot(instanceInventory);
            return true;
         } else {
            if (this.getTicksInState() % 6 == 3) {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }

            return true;
         }
      } else {
         return this.requestHotbarSwap(stack2 -> stack2.isOf(Items.ELYTRA) && getRemainingDurability(stack2) > 40) || ExtUtils.isAction();
      }
   }

   private void updatePilotStatus() {
      if (mc.player != null && this.destination != null) {
         double destinationPlayer = this.getDistanceToDestination();
         double ticks = Math.hypot(mc.player.getVelocity().x, mc.player.getVelocity().z) * 20.0;
         long destinationJobValue = this.isGliding() && ticks > 3.0 ? (long)(destinationPlayer / Math.max(18.0, ticks)) : (long)(destinationPlayer / 27.0);

         int destinationJobValue2 = switch (this.flightState) {
            case SERVICE -> 1;
            case ARRIVED -> 3;
            case STOPPED -> 2;
            default -> this.job == AutoElytraPilot.Job.STOP ? 2 : (this.job == AutoElytraPilot.Job.SERVICE ? 1 : 0);
         };
         int equippedStack = this.getEquippedElytraDurability();
         this.pilotStatus = new PilotStatus(
            this.formatStatusText(),
            destinationJobValue2,
            this.destination.formatTextString(),
            destinationPlayer,
            Math.max(destinationPlayer, this.routeTotalDistance),
            destinationJobValue,
            ticks,
            MathHelper.floor(mc.player.getY()),
            this.countRockets(),
            this.getRocketTargetCount(),
            this.countXpBottles(),
            equippedStack,
            equippedStack < 0 ? -1 : this.getElytraMaxDurability()
         );
         Tone tone = this.flightState != AutoElytraPilot.State.STOPPED && this.job != AutoElytraPilot.Job.STOP
            ? (this.flightState == AutoElytraPilot.State.SERVICE ? Tone.PAUSED : Tone.ACTIVE)
            : Tone.DANGER;
         this.menuSnapshot = new Snapshot(
            tone,
            this.formatShortPhase(),
            null,
            0,
            0,
            Destination.getFormat(destinationPlayer) + " to " + this.destination.formatTextString() + " - " + this.countRockets() + " rockets"
         );
      }
   }

   private static int getRemainingDurability(ItemStack stack) {
      int damageValue = stack.getMaxDamage() > 0 ? stack.getMaxDamage() : 432;
      return damageValue - stack.getDamage();
   }

   private boolean hasNoGroundBelow(int downPosLimit) {
      BlockPos blockPos = mc.player.getBlockPos();

      for (int i = 1; i <= downPosLimit; i++) {
         if (this.hasCollisionAt(blockPos.down(i))) {
            return false;
         }
      }

      return true;
   }

   private boolean tryStartServiceLanding() {
      if (this.flightState == AutoElytraPilot.State.DESCEND && this.job != AutoElytraPilot.Job.ARRIVE) {
         return false;
      } else {
         int equippedStack = this.getEquippedElytraDurability();
         int stackHand = this.countRockets();
         double destinationPlayer = this.getDistanceToDestination();
         int landingLimit = this.getUnbreakingLevel();
         double landingValue = destinationPlayer / 27.0 + 50.0;
         double landingValue2 = landingValue / (1 + landingLimit) * 1.35 + 25.0;
         double landingValue3 = destinationPlayer / 1000.0 * 2.6 + 5.0;
         boolean landingFlag = (equippedStack < 0 || equippedStack > landingValue2) && stackHand > landingValue3;
         boolean landingFlag2 = equippedStack >= 0 && equippedStack <= (landingLimit >= 1 ? 40 : 60);
         boolean landingFlag3 = stackHand <= 3;
         boolean landingFlag4 = landingFlag2 || landingFlag3;
         if (landingFlag && !landingFlag4) {
            return false;
         } else {
            boolean landingFlag5 = this.autoMending.isEnabled()
               && this.isEquippedElytraMending()
               && this.mendingFailureCount < 3
               && (this.countXpBottles() > 0 || this.canBuyGoods(Goods.XP));
            boolean landingFlag6 = equippedStack >= 0 && equippedStack <= this.getSwapDurabilityThreshold();
            boolean landingFlag7 = stackHand <= 20;
            AutoElytraPilot.Job job = null;
            if (landingFlag6 && landingFlag5) {
               job = AutoElytraPilot.Job.SERVICE;
            } else if (landingFlag6 && this.hasBetterSpareElytra()) {
               job = AutoElytraPilot.Job.SERVICE;
            } else if (landingFlag6 && equippedStack <= 40) {
               job = AutoElytraPilot.Job.STOP;
            }

            if (job == null && landingFlag7 && this.canBuyGoods(Goods.ROCKETS)) {
               job = AutoElytraPilot.Job.SERVICE;
            }

            if (job == null && stackHand <= 6) {
               job = AutoElytraPilot.Job.STOP;
            }

            if (job == null && landingFlag2) {
               job = !landingFlag5 && !this.hasBetterSpareElytra() ? AutoElytraPilot.Job.STOP : AutoElytraPilot.Job.SERVICE;
            }

            if (job == null && landingFlag3) {
               job = this.canBuyGoods(Goods.ROCKETS) ? AutoElytraPilot.Job.SERVICE : AutoElytraPilot.Job.STOP;
            }

            if (job == null) {
               return false;
            } else {
               if (job == AutoElytraPilot.Job.STOP && this.tripStopReason.isEmpty()) {
                  this.tripStopReason = stackHand <= 6 ? this.formatOutOfRocketsMessage() : "The elytra is worn out and can't be repaired.";
               }

               this.job = job;
               this.landingGoalPos = null;
               this.goAroundCount = 0;
               this.lastLandingProbeTick = -1000;
               this.notifyUser(AutoElytraPilot.Kind.INFO, job == AutoElytraPilot.Job.SERVICE ? "Landing for a pit stop" : "Landing to stop safely");
               this.setFlightState(AutoElytraPilot.State.DESCEND);
               return true;
            }
         }
      }
   }

   private int classifyBlockAt(int x2, int blockValue, int z2) {
      if (blockValue - 1 < mc.world.getBottomY()) {
         return 5;
      } else {
         BlockPos blockPos = new BlockPos(x2, blockValue - 1, z2);
         BlockState blockState = mc.world.getBlockState(blockPos);
         FluidState fluidState = blockState.getFluidState();
         if (fluidState.isIn(FluidTags.LAVA)) {
            return 2;
         } else if (fluidState.isIn(FluidTags.WATER)) {
            return 1;
         } else if (blockState.isIn(BlockTags.LEAVES)) {
            return 3;
         } else {
            Block block = blockState.getBlock();
            if (block == Blocks.MAGMA_BLOCK
               || block instanceof CampfireBlock
               || block == Blocks.CACTUS
               || block == Blocks.SWEET_BERRY_BUSH
               || blockState.isIn(BlockTags.FIRE)
               || block == Blocks.POWDER_SNOW
               || block == Blocks.POINTED_DRIPSTONE
               || block == Blocks.WITHER_ROSE
               || block == Blocks.COBWEB) {
               return 4;
            } else if (block != Blocks.SLIME_BLOCK && block != Blocks.HONEY_BLOCK) {
               VoxelShape collisionShape = blockState.getCollisionShape(mc.world, blockPos);
               if (collisionShape.isEmpty()) {
                  return 5;
               } else {
                  Box boundingBox = collisionShape.getBoundingBox();
                  if (!(boundingBox.maxY > 1.01) && !(boundingBox.maxY < 0.45) && !(boundingBox.maxX - boundingBox.minX < 0.9) && !(boundingBox.maxZ - boundingBox.minZ < 0.9)) {
                     BlockState blockState2 = mc.world.getBlockState(blockPos.up());
                     return blockState2.getFluidState().isEmpty() && blockState2.getCollisionShape(mc.world, blockPos.up()).isEmpty() ? 0 : 5;
                  } else {
                     return 5;
                  }
               }
            } else {
               return 5;
            }
         }
      }
   }

   private void runAhPurchase(Goods goods) {
      this.holdYawLookDown();
      if (!this.pilotAhBuyer.isRunning()) {
         Status status = this.pilotAhBuyer.getStatus();
         if (status != Status.DONE && status != Status.FAILED) {
            if (this.pilotTicks - this.taskStartTick > 4800) {
               if (goods == Goods.ROCKETS) {
                  this.rocketBuyFailureCount++;
                  this.rocketBuyRetryTick = this.pilotTicks + 6000;
               } else {
                  this.xpBuyFailureCount++;
                  this.xpBuyRetryTick = this.pilotTicks + 6000;
               }

               this.finishTask(goods == Goods.ROCKETS ? AutoElytraPilot.Task.BUY_ROCKETS : AutoElytraPilot.Task.BUY_XP);
            } else {
               int rocketsBuyValue;
               if (goods == Goods.ROCKETS) {
                  int rocketsBuyBound = this.countRockets() - PilotAhBuyer.countGoodsInInventory(mc.player.getInventory(), Goods.ROCKETS);
                  rocketsBuyValue = Math.max(0, this.getRocketTargetCount() - Math.max(0, rocketsBuyBound));
               } else {
                  rocketsBuyValue = this.getXpBottleTargetCount();
               }

               if (rocketsBuyValue
                  <= (goods == Goods.ROCKETS ? PilotAhBuyer.countGoodsInInventory(mc.player.getInventory(), Goods.ROCKETS) : this.countXpBottles())) {
                  this.finishTask(goods == Goods.ROCKETS ? AutoElytraPilot.Task.BUY_ROCKETS : AutoElytraPilot.Task.BUY_XP);
               } else {
                  long cheapHigh = this.getMaxPricePerUnit(goods);
                  long remainingBudget = this.getRemainingBudget();
                  if (remainingBudget == 0L) {
                     this.finishTask(goods == Goods.ROCKETS ? AutoElytraPilot.Task.BUY_ROCKETS : AutoElytraPilot.Task.BUY_XP);
                  } else if (!this.pilotAhBuyer.startBuying(goods, rocketsBuyValue, rocketsBuyValue, cheapHigh, remainingBudget < 0L ? 0L : remainingBudget)) {
                     if (goods == Goods.ROCKETS) {
                        this.rocketBuyFailureCount++;
                        this.rocketBuyRetryTick = this.pilotTicks + 6000;
                     } else {
                        this.xpBuyFailureCount++;
                        this.xpBuyRetryTick = this.pilotTicks + 6000;
                     }

                     this.pilotAhBuyer.stopRun();
                     this.finishTask(goods == Goods.ROCKETS ? AutoElytraPilot.Task.BUY_ROCKETS : AutoElytraPilot.Task.BUY_XP);
                  } else {
                     this.notifyUser(AutoElytraPilot.Kind.INFO, "Buying " + (goods == Goods.ROCKETS ? "rockets" : "XP bottles") + " from /ah");
                  }
               }
            }
         } else {
            boolean rocketsBuyFlag = status == Status.DONE;
            String fireworkRocketText = this.pilotAhBuyer.getStopReason();
            boolean moneyEnoughFlag = this.pilotAhBuyer.isOutOfMoney();
            boolean showedChargedFlag = this.pilotAhBuyer.isOvercharged();
            int fullInventoryCount = this.pilotAhBuyer.getBoughtCount();
            this.syncAhBuyerStats();
            String rocketsBuyText = goods == Goods.ROCKETS ? "rockets" : "XP bottles";
            if (showedChargedFlag) {
               this.buyingBlockedThisTrip = true;
               this.notifyUser(AutoElytraPilot.Kind.WARN, "The server charged more than the listing showed - stopped buying for this trip");
            }

            if (!rocketsBuyFlag && fullInventoryCount <= 0) {
               if (moneyEnoughFlag) {
                  if (goods == Goods.ROCKETS) {
                     if (++this.rocketMoneyShortCount >= 3) {
                        this.rocketsUnaffordable = true;
                     }

                     this.rocketBuyRetryTick = this.pilotTicks + 6000;
                  } else {
                     if (++this.xpMoneyShortCount >= 3) {
                        this.xpUnaffordable = true;
                     }

                     this.xpBuyRetryTick = this.pilotTicks + 6000;
                  }

                  this.notifyUser(AutoElytraPilot.Kind.WARN, "Not enough money for " + rocketsBuyText);
               } else {
                  if (goods == Goods.ROCKETS) {
                     this.rocketBuyFailureCount++;
                     this.rocketBuyRetryTick = this.pilotTicks + 6000;
                  } else {
                     this.xpBuyFailureCount++;
                     this.xpBuyRetryTick = this.pilotTicks + 6000;
                  }

                  this.notifyUser(
                     AutoElytraPilot.Kind.WARN, "Couldn't buy " + rocketsBuyText + " (" + (fireworkRocketText != null && !fireworkRocketText.isBlank() ? fireworkRocketText : "no listing") + ")"
                  );
               }

               this.finishTask(goods == Goods.ROCKETS ? AutoElytraPilot.Task.BUY_ROCKETS : AutoElytraPilot.Task.BUY_XP);
            } else {
               this.notifyUser(
                  AutoElytraPilot.Kind.GOOD, "Bought " + rocketsBuyText + " - now " + (goods == Goods.ROCKETS ? this.countRockets() : this.countXpBottles())
               );
               this.finishTask(goods == Goods.ROCKETS ? AutoElytraPilot.Task.BUY_ROCKETS : AutoElytraPilot.Task.BUY_XP);
            }
         }
      }
   }

   private double getDistanceToDestination() {
      return this.destination == null ? 0.0 : this.destination.getAmount(mc.player.getX(), mc.player.getZ());
   }

   private int getXpBottleTargetCount() {
      return this.xpBottleStacks.getValue() * 64;
   }

   private void pressJumpToDeploy() {
      if (!this.hasLevitation() && this.isUsableElytraEquipped() && !mc.player.isOnGround()) {
         mc.options.jumpKey.setPressed((this.pilotTicks & 1) == 0);
      }
   }

   private static boolean isElytraUsable(ItemStack stack) {
      return getRemainingDurability(stack) > 1;
   }

   private boolean requestHotbarSwap(Predicate<ItemStack> predicate) {
      if (SelectedInventoryUtils.getInstanceInventory(predicate) >= 0) {
         return false;
      } else if (ExtUtils.isAction()) {
         return true;
      } else if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         return false;
      } else {
         PlayerInventory inventory = mc.player.getInventory();

         for (int i = 9; i <= 35; i++) {
            if (predicate.test(inventory.getStack(i))) {
               int playerValue = i;
               int stackExperience = this.findFreeHotbarSlot();
               ExtUtils.handleExt(
                  () -> {
                     if (mc.player != null && mc.player.currentScreenHandler == mc.player.playerScreenHandler) {
                        if (predicate.test(mc.player.getInventory().getStack(playerValue))) {
                           mc.interactionManager
                              .clickSlot(
                                 mc.player.playerScreenHandler.syncId,
                                 HotbarSlotUtils.toScreenSlot(playerValue),
                                 HotbarSlotUtils.requireHotbarSlot(stackExperience),
                                 SlotActionType.SWAP,
                                 mc.player
                              );
                        }
                     }
                  },
                  true
               );
               this.inventorySwapPending = true;
               return true;
            }
         }

         return false;
      }
   }

   private boolean isEquippedElytraMending() {
      return hasEnchantment(mc.player.getEquippedStack(EquipmentSlot.CHEST), Enchantments.MENDING);
   }

   private boolean isGliding() {
      return mc.player.isGliding();
   }

   private boolean isChunkLoadedAhead(double chunkFloorFactor) {
      Vec3d velocity = mc.player.getVelocity();
      double hypot = Math.hypot(velocity.x, velocity.z);
      Vec3d vec3d = hypot > 0.2 ? new Vec3d(velocity.x / hypot, 0.0, velocity.z / hypot) : getHorizontalDirection(mc.player.getYaw());
      int chunkFloorValue = MathHelper.floor(mc.player.getX() + vec3d.x * chunkFloorFactor);
      int floor = MathHelper.floor(mc.player.getZ() + vec3d.z * chunkFloorFactor);
      return mc.world.getChunkManager().getWorldChunk(chunkFloorValue >> 4, floor >> 4) != null;
   }

   private boolean isThreatNearby() {
      if (this.pilotTicks - this.lastDamageTick < 30 && mc.player.getHealth() < 14.0F) {
         return true;
      } else {
         Box expand = mc.player.getBoundingBox().expand(9.0, 4.0, 9.0);

         for (Entity entity : mc.world.getOtherEntities(mc.player, expand, entity2 -> entity2.isAlive() && entity2 instanceof Monster)) {
            if (!(entity instanceof EndermanEntity endermanEntity && !endermanEntity.isAngry())
               && !(entity instanceof ZombifiedPiglinEntity zombifiedPiglinEntity && !zombifiedPiglinEntity.isAttacking())
               && mc.player.canSee(entity)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   protected void onEnable() {
      this.resetState();
      if (mc.player != null && mc.world != null) {
         String needWornText = Destination.formatExampleNumbers(this.destinationInput.getCurrentText());
         if (needWornText != null) {
            this.startErrorText = needWornText;
         } else {
            this.destination = Destination.getDestination(this.destinationInput.getCurrentText());
            ModuleConfigAutosave.updateFlag2();
            if (mc.world.getRegistryKey() == World.NETHER) {
               this.startErrorText = "Works in the Overworld and the End, not the Nether.";
            } else if (!this.hasUsableElytra()) {
               this.startErrorText = "You need an elytra (worn or in your inventory).";
            } else if (this.countRockets() == 0 && !this.autoBuyRockets.isEnabled()) {
               this.startErrorText = "You need firework rockets.";
            } else {
               this.startDimension = mc.world.getRegistryKey();
               this.connection = mc.getNetworkHandler() == null ? null : mc.getNetworkHandler().getConnection();
               if (mc.options != null) {
                  this.savedPauseOnLostFocus = mc.options.pauseOnLostFocus;
                  mc.options.pauseOnLostFocus = false;
               }

               this.tripStartMillis = System.currentTimeMillis();
               this.lastTripPos = mc.player.getEntityPos();
               this.lastHealth = mc.player.getHealth();
               this.routeTotalDistance = this.destination.getAmount(mc.player.getX(), mc.player.getZ());
               this.pilotStatusCard.resetState();
               this.pilotRender.resetState();
               this.notifyUser(
                  AutoElytraPilot.Kind.INFO, "Flying to " + this.destination.formatText() + " (" + Destination.getFormat(this.routeTotalDistance) + ")"
               );
               this.setFlightState(AutoElytraPilot.State.PREPARE);
            }
         }
      } else {
         this.startedWithoutWorld = true;
      }
   }

   private Vec3d findNearestShoreSpot(int floorPlayerLimit) {
      int floorPlayerValue = MathHelper.floor(mc.player.getX());
      int floor = MathHelper.floor(mc.player.getZ());
      Vec3d floorPlayer = null;
      double maxValue = Double.MAX_VALUE;

      for (int i = 1; i <= floorPlayerLimit; i++) {
         for (int j = -i; j <= i; j++) {
            for (int k = -i; k <= i; k++) {
               if (Math.max(Math.abs(j), Math.abs(k)) == i) {
                  int chunkHeightmapValue = floorPlayerValue + j;
                  int z2 = floor + k;
                  int chunkHeightmap = this.getTopBlockHeight(chunkHeightmapValue, z2);
                  if (chunkHeightmap != Integer.MIN_VALUE && this.classifyBlockAt(chunkHeightmapValue, chunkHeightmap, z2) == 0) {
                     double floorPlayerValue5 = j * j + k * k;
                     if (floorPlayerValue5 < maxValue) {
                        maxValue = floorPlayerValue5;
                        floorPlayer = new Vec3d(chunkHeightmapValue + 0.5, chunkHeightmap, z2 + 0.5);
                     }
                  }
               }
            }
         }

         if (floorPlayer != null) {
            return floorPlayer;
         }
      }

      return null;
   }

   private boolean isAboveTerrainAround() {
      int floorPlayerValue = MathHelper.floor(mc.player.getY());
      int floor = MathHelper.floor(mc.player.getX());
      int floor3 = MathHelper.floor(mc.player.getZ());

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            int surface = this.terrain.surface(floor + i, floor3 + j);
            if (surface == Integer.MIN_VALUE || surface > floorPlayerValue) {
               return false;
            }
         }
      }

      return true;
   }

   private int addScenePoint(int sceneDestination, double sceneDestinationValue, double y, double z) {
      if (sceneDestination >= this.scene.routePoints.length / 3) {
         return sceneDestination;
      } else {
         this.scene.routePoints[sceneDestination * 3] = sceneDestinationValue;
         this.scene.routePoints[sceneDestination * 3 + 1] = y;
         this.scene.routePoints[sceneDestination * 3 + 2] = z;
         return sceneDestination + 1;
      }
   }

   private int getRocketBoostTicksLeft() {
      return this.isRocketBoostActive() ? Math.max(0, this.rocketBoostEndTick - this.pilotTicks) : 0;
   }

   private void setFlightState(AutoElytraPilot.State state) {
      if (state == AutoElytraPilot.State.LAND && this.flightState != AutoElytraPilot.State.LAND) {
         this.lastApproachPlanTick = -1000;
         this.badApproachPlanCount = 0;
      }

      if (state == AutoElytraPilot.State.SERVICE && this.flightState != AutoElytraPilot.State.SERVICE) {
         this.currentTask = AutoElytraPilot.Task.NONE;
         this.pitStopCount = this.pitStopCount + (this.job == AutoElytraPilot.Job.SERVICE ? 1 : 0);
         this.serviceSpotPos = this.landingGoalPos != null && this.getHorizontalDistanceTo(this.landingGoalPos) < 8.0 ? this.landingGoalPos : mc.player.getEntityPos();
      }

      if (state == AutoElytraPilot.State.CRUISE || state == AutoElytraPilot.State.CLIMB) {
         this.flightPlanner.resetClimbPhase();
      }

      if (state == AutoElytraPilot.State.TAKEOFF) {
         this.takeoffJumpCount = 0;
      }

      if (state != AutoElytraPilot.State.SERVICE && this.pilotAhBuyer.getStatus() != Status.IDLE) {
         this.syncAhBuyerStats();
      }

      if (state == AutoElytraPilot.State.TAKEOFF) {
         this.landingGoalPos = null;
         this.goAroundCount = 0;
         this.lastLandingProbeTick = -1000;
         this.landingProbeAttempts = 0;
         if (this.job != AutoElytraPilot.Job.STOP) {
            this.job = AutoElytraPilot.Job.ARRIVE;
         }

         this.takeoffAttemptCount++;
      }

      if (state == AutoElytraPilot.State.CRUISE) {
         this.closestApproachDistance = Double.MAX_VALUE;
         this.routeProgressCheckTick = this.pilotTicks;
         this.takeoffAttemptCount = 0;
      }

      if (state == AutoElytraPilot.State.DESCEND && this.flightState != AutoElytraPilot.State.DESCEND) {
         this.lowestHeightAboveLanding = Double.MAX_VALUE;
         this.descentProgressTick = this.pilotTicks;
      }

      if (state != AutoElytraPilot.State.DESCEND) {
         this.circlingToLand = false;
      }

      this.flightState = state;
      this.stateChangeTick = this.pilotTicks;
   }

   private int getElytraMaxDurability() {
      int maxDamage = mc.player.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage();
      return maxDamage > 0 ? maxDamage : 432;
   }

   private static boolean isEdibleFood(ItemStack stack, boolean appleGoldenFlag) {
      if (stack.isEmpty() || stack.get(DataComponentTypes.FOOD) == null) {
         return false;
      } else {
         return !stack.isOf(Items.CHORUS_FRUIT)
               && !stack.isOf(Items.ROTTEN_FLESH)
               && !stack.isOf(Items.SPIDER_EYE)
               && !stack.isOf(Items.POISONOUS_POTATO)
               && !stack.isOf(Items.PUFFERFISH)
               && !stack.isOf(Items.SUSPICIOUS_STEW)
            ? appleGoldenFlag || !stack.isOf(Items.GOLDEN_APPLE) && !stack.isOf(Items.ENCHANTED_GOLDEN_APPLE)
            : false;
      }
   }

   private boolean tryEat(boolean playerPressedFlag) {
      HungerManager hungerManager = mc.player.getHungerManager();
      if (mc.player.isUsingItem() && this.pilotTicks < this.eatCooldownEndTick) {
         mc.options.useKey.setPressed(true);
         return true;
      } else if (this.isHungry() && !this.isContainerScreenOpen()) {
         if (playerPressedFlag
            || !this.isGliding()
            || !this.isRocketBoostActive()
               && !this.flightPlanner.isClimbPhaseActive()
               && !(mc.player.getY() < 70.0)
               && this.flightState != AutoElytraPilot.State.LAND
               && this.flightState != AutoElytraPilot.State.TAKEOFF
               && this.flightState != AutoElytraPilot.State.CLIMB
               && this.flightState != AutoElytraPilot.State.DESCEND) {
            boolean appleGoldenFlag = mc.player.getHealth() <= 6.0F;
            int instanceInventory = SelectedInventoryUtils.getInstanceInventory(desperate -> isEdibleFood(desperate, appleGoldenFlag));
            if (instanceInventory < 0) {
               return this.requestHotbarSwap(desperate -> isEdibleFood(desperate, appleGoldenFlag));
            } else if (mc.player.getInventory().getSelectedSlot() != instanceInventory) {
               mc.player.getInventory().setSelectedSlot(instanceInventory);
               return true;
            } else {
               HitResult crosshairTarget = mc.crosshairTarget;
               if (crosshairTarget != null && crosshairTarget.getType() == Type.ENTITY) {
                  return true;
               } else {
                  mc.options.useKey.setPressed(true);
                  if (!mc.player.isUsingItem()) {
                     this.eatCooldownEndTick = this.pilotTicks + 45;
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   protected void onDisable() {
      this.suppressArrivalLogOff = false;
      this.releaseMovementKeys();
      this.syncAhBuyerStats();
      if (this.inventorySwapPending && ExtUtils.isAction()) {
         ExtUtils.updateFlagText();
      }

      this.inventorySwapPending = false;
      if (mc.player != null && this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      }

      SelectedInventoryUtils.updateSelectedSlot2();
      this.rotationSmoother.deactivate();
      if (this.savedPauseOnLostFocus != null && mc.options != null) {
         mc.options.pauseOnLostFocus = this.savedPauseOnLostFocus;
      }

      this.savedPauseOnLostFocus = null;
      this.scene.destinationActive = false;
      this.menuSnapshot = Snapshot.INSTANCE;
   }

   private int getTopBlockHeight(int chunkHeightmapValue, int z2) {
      WorldChunk worldChunk = mc.world.getChunkManager().getWorldChunk(chunkHeightmapValue >> 4, z2 >> 4);
      return worldChunk == null ? Integer.MIN_VALUE : worldChunk.sampleHeightmap(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, chunkHeightmapValue & 15, z2 & 15) + 1;
   }

   private boolean hasFood() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i <= 35; i++) {
         if (isEdibleFood(inventory.getStack(i), false)) {
            return true;
         }
      }

      return false;
   }

   private void releaseMovementKeys() {
      mc.options.forwardKey.setPressed(false);
      mc.options.backKey.setPressed(false);
      mc.options.leftKey.setPressed(false);
      mc.options.rightKey.setPressed(false);
      mc.options.jumpKey.setPressed(false);
      ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, false);
      ElytraAutoUtils.handleRawPressed(mc.options.sprintKey, false);
      mc.options.useKey.setPressed(false);
   }

   private String formatOutOfRocketsMessage() {
      if (!this.autoBuyRockets.isEnabled()) {
         return "Out of firework rockets.";
      } else if (this.rocketsUnaffordable) {
         return "Out of rockets and not enough money to buy more.";
      } else {
         return this.getRemainingBudget() == 0L ? "Out of rockets and the trip budget is used up." : "Out of rockets and none could be bought from /ah.";
      }
   }

   private boolean isViewBlocked(Vec3d vec, double eyePosValue) {
      return this.isRayBlocked(mc.player.getEyePos(), vec, eyePosValue);
   }

   private void updateCruiseState() {
      if (this.isGlidingOrHandleGround()) {
         double destinationPlayer = this.getDistanceToDestination();
         this.cruiseAltitude = this.getCruiseAltitudeFor(destinationPlayer);
         if (!this.tryStartServiceLanding()) {
            this.checkRouteProgress(destinationPlayer);
            if (destinationPlayer <= this.getDescentStartDistance()) {
               this.job = AutoElytraPilot.Job.ARRIVE;
               this.landingGoalPos = null;
               this.goAroundCount = 0;
               this.setFlightState(AutoElytraPilot.State.DESCEND);
            } else {
               this.flightGoal.landingGoalActive = false;
               this.flightGoal.steerToLandingSpot = false;
               this.flyTowardGoal(this.destination.x() + 0.5, this.destination.z() + 0.5, Double.NaN, true);
            }
         }
      }
   }

   private void tickPilot() {
      if (this.startedWithoutWorld) {
         this.stopPilot(null);
      } else if (this.startErrorText != null) {
         String needWornText = this.startErrorText;
         this.startErrorText = null;
         this.notifyUser(AutoElytraPilot.Kind.STOP, needWornText);
         this.stopPilot(null);
      } else if (this.destination == null) {
         this.stopPilot(null);
      } else {
         ClientConnection connection = mc.getNetworkHandler().getConnection();
         if (this.connection != null && connection != this.connection) {
            this.stopPilot(null);
         } else if (!mc.player.isAlive()) {
            this.notifyUser(AutoElytraPilot.Kind.STOP, "You died - the pilot stopped.");
            this.stopPilot(null);
         } else if (this.startDimension != null && mc.world.getRegistryKey() != this.startDimension) {
            this.notifyUser(AutoElytraPilot.Kind.STOP, "You changed dimension - the pilot stopped.");
            this.stopPilot(null);
         } else {
            this.pilotTicks++;
            this.releaseMovementKeys();
            this.rotationRequested = false;
            this.fastRotationRequested = false;
            if (this.inventorySwapPending && !ExtUtils.isAction()) {
               this.inventorySwapPending = false;
            }

            this.pilotAhBuyer.onTick();
            if (mc.getInactivityFpsLimiter() != null) {
               mc.getInactivityFpsLimiter().onInput();
            }

            this.trackTripProgress();
            this.trackRubberbanding();
            if (this.isGliding()
               && this.getEquippedElytraDurability() >= 0
               && this.getEquippedElytraDurability() <= this.getSwapDurabilityThreshold()
               && this.pilotTicks % 20 == 0
               && SelectedInventoryUtils.getInstanceInventory(stack -> stack.isOf(Items.ELYTRA) && getRemainingDurability(stack) >= 60) < 0
               && this.hasBetterSpareElytra()) {
               this.requestHotbarSwap(stack2 -> stack2.isOf(Items.ELYTRA) && getRemainingDurability(stack2) >= 60);
            }

            if (!this.tryRecoverFromFall() && !this.handleCriticalElytra()) {
               switch (this.flightState) {
                  case PREPARE:
                     this.updatePrepareState();
                     break;
                  case TAKEOFF:
                     this.updateTakeoffState();
                     break;
                  case CLIMB:
                     this.updateClimbState();
                     break;
                  case CRUISE:
                     this.updateCruiseState();
                     break;
                  case DESCEND:
                     this.updateDescendState();
                     break;
                  case LAND:
                     this.updateLandState();
                     break;
                  case SERVICE:
                     this.updateServiceState();
                     break;
                  case ARRIVED:
                     this.updateArrivedState();
                     break;
                  case STOPPED:
                     this.updateStoppedState();
               }
            }

            this.applyRotationRequest();
            if (this.pilotTicks % 5 == 0) {
               this.updatePilotStatus();
            }

            this.updateWaypointScene();
         }
      }
   }

   private boolean handleCriticalElytra() {
      int equippedStack = this.getEquippedElytraDurability();
      if (this.isGliding() && equippedStack >= 0 && equippedStack <= 6) {
         if (this.pilotTicks - this.lastCriticalSwapTick < 6) {
            return false;
         } else {
            this.lastCriticalSwapTick = this.pilotTicks;
            return this.equipHotbarElytra();
         }
      } else {
         return false;
      }
   }

   private long getMaxPricePerUnit(Goods goods) {
      String formatText = this.priceLimit.getCurrentOption();
      long cheapHighValue;
      if (goods == Goods.ROCKETS) {
         cheapHighValue = formatText.equals("Cheap") ? 35000L : (formatText.equals("High") ? 150000L : 60000L);
      } else {
         cheapHighValue = formatText.equals("Cheap") ? 320000L : (formatText.equals("High") ? 700000L : 450000L);
      }

      return (cheapHighValue + 63L) / 64L;
   }

   private void requestRotation(float skyOpenValue, float value, int value2) {
      if (!this.rotationRequested || value2 >= this.rotationPriority) {
         this.rotationRequested = true;
         this.rotationPriority = value2;
         this.targetYaw = skyOpenValue;
         this.targetPitch = MathHelper.clamp(value, -90.0F, 90.0F);
      }
   }

   private void updateWaypointScene() {
      this.scene.destinationActive = this.waypointLine.isEnabled() && this.destination != null;
      if (this.scene.destinationActive) {
         this.scene.destinationX = this.landingGoalPos != null && this.job == AutoElytraPilot.Job.ARRIVE ? this.landingGoalPos.x : this.destination.x() + 0.5;
         this.scene.destinationZ = this.landingGoalPos != null && this.job == AutoElytraPilot.Job.ARRIVE ? this.landingGoalPos.z : this.destination.z() + 0.5;
         int chunkHeightmap = this.getTopBlockHeight(MathHelper.floor(this.scene.destinationX), MathHelper.floor(this.scene.destinationZ));
         this.scene.destinationY = this.landingGoalPos != null && this.job == AutoElytraPilot.Job.ARRIVE
            ? this.landingGoalPos.y
            : (chunkHeightmap == Integer.MIN_VALUE ? Double.NaN : chunkHeightmap);
         this.scene.distanceToDestination = this.getDistanceToDestination();
         this.scene.totalRouteDistance = Math.max(this.scene.distanceToDestination, this.routeTotalDistance);
         this.scene.etaSeconds = this.pilotStatus.etaSeconds();
         this.scene.destinationLabel = this.job == AutoElytraPilot.Job.ARRIVE ? "Destination" : "Pit stop";
         this.scene.highlightDestination = this.flightState == AutoElytraPilot.State.LAND || this.flightState == AutoElytraPilot.State.DESCEND;
         double x = mc.player.getX();
         double y = mc.player.getY();
         double z = mc.player.getZ();
         double sceneDestinationValue = Double.isNaN(this.scene.destinationY) ? this.getEstimatedGroundY(this.scene.destinationX, this.scene.destinationZ) : this.scene.destinationY;
         double hypot = Math.hypot(this.scene.destinationX - x, this.scene.destinationZ - z);
         byte sceneDestinationValue2 = 0;
         int sceneDestination = this.addScenePoint(sceneDestinationValue2, x, y, z);
         double sceneDestinationLimit = Math.max(y, Math.min(this.cruiseAltitude, sceneDestinationValue + this.flightGoal.approachAltitudeMargin + Math.max(0.0, hypot - this.flightGoal.approachRadius) * this.flightGoal.descentSlope));
         double max2 = Math.max(0.0, hypot - (this.flightGoal.approachRadius + Math.max(0.0, sceneDestinationLimit - sceneDestinationValue - this.flightGoal.approachAltitudeMargin) / this.flightGoal.descentSlope));
         if (max2 > 40.0 && hypot > 1.0) {
            double sceneDestinationFactor = max2 / hypot;
            if (sceneDestinationLimit > y + 4.0) {
               double min2 = Math.min(sceneDestinationFactor, Math.min(1.0, 140.0 / hypot));
               sceneDestination = this.addScenePoint(sceneDestination, x + (this.scene.destinationX - x) * min2, sceneDestinationLimit, z + (this.scene.destinationZ - z) * min2);
            }

            sceneDestination = this.addScenePoint(sceneDestination, x + (this.scene.destinationX - x) * sceneDestinationFactor, sceneDestinationLimit, z + (this.scene.destinationZ - z) * sceneDestinationFactor);
         }

         if (hypot > this.flightGoal.approachRadius + 2.0) {
            double sceneDestinationFactor3 = (hypot - this.flightGoal.approachRadius) / hypot;
            sceneDestination = this.addScenePoint(
               sceneDestination, x + (this.scene.destinationX - x) * sceneDestinationFactor3, sceneDestinationValue + this.flightGoal.approachAltitudeMargin, z + (this.scene.destinationZ - z) * sceneDestinationFactor3
            );
         }

         int value = this.addScenePoint(sceneDestination, this.scene.destinationX, sceneDestinationValue + 0.1, this.scene.destinationZ);
         this.scene.routePointCount = value;
      }
   }

   private boolean isContainerScreenOpen() {
      return mc.player != null
         && mc.currentScreen instanceof HandledScreen
         && !(mc.currentScreen instanceof InventoryScreen)
         && mc.player.currentScreenHandler != null
         && mc.player.currentScreenHandler != mc.player.playerScreenHandler;
   }

   public void renderPilotStatus(DrawContext context) {
      if (this.statusCard.isEnabled()) {
         this.pilotStatusCard.renderStatusCard(context, this.pilotStatus);
      }
   }

   private double getHorizontalDistanceTo(Vec3d vec) {
      return Math.hypot(vec.x - mc.player.getX(), vec.z - mc.player.getZ());
   }

   private void updateMendingTask() {
      if (this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      } else {
         int mendingRepairedValue = this.getEquippedElytraDurability();
         int equippedDamage = this.getElytraMaxDurability();
         if (mendingRepairedValue < 0) {
            this.finishTask(AutoElytraPilot.Task.MEND);
         } else {
            ItemStack equippedStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
            if (mendingRepairedValue < equippedDamage && equippedStack.getDamage() != 0) {
               if (this.pilotTicks - this.taskStartTick < 2) {
                  this.bestMendingDurability = mendingRepairedValue;
                  this.lastMendingProgressTick = this.pilotTicks;
               }

               if (mendingRepairedValue > this.bestMendingDurability) {
                  this.bestMendingDurability = mendingRepairedValue;
                  this.lastMendingProgressTick = this.pilotTicks;
               }

               if (this.pilotTicks - this.lastMendingProgressTick > 200) {
                  this.mendingFailureCount++;
                  this.notifyUser(AutoElytraPilot.Kind.WARN, "Mending stopped working here (other Mending gear may be taking the XP)");
                  this.finishTask(AutoElytraPilot.Task.MEND);
               } else if (this.pilotTicks - this.lastMendingProgressTick > 50
                  && this.pilotTicks - this.lastMendingProgressTick < 120
                  && this.serviceSpotPos != null
                  && this.getHorizontalDistanceTo(this.serviceSpotPos) > 0.6) {
                  float[] wrapDegrees = MathClampUtils.getWrapDegrees(mc.player.getEyePos(), this.serviceSpotPos);
                  this.requestRotation(wrapDegrees[0], 30.0F, 0);
                  if (Math.abs(MathHelper.wrapDegrees(wrapDegrees[0] - mc.player.getYaw())) < 25.0F) {
                     mc.options.forwardKey.setPressed(true);
                  }
               } else {
                  int selectedSlot = SelectedInventoryUtils.getValue(Items.EXPERIENCE_BOTTLE);
                  if (selectedSlot >= 0) {
                     this.requestRotation(mc.player.getYaw(), 86.0F, 0);
                     if (mc.player.getInventory().getSelectedSlot() != selectedSlot) {
                        mc.player.getInventory().setSelectedSlot(selectedSlot);
                     } else {
                        if (mc.player.getPitch() > 70.0F && this.pilotTicks - this.lastMendingActionTick >= 4 && !mc.player.isUsingItem()) {
                           mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                           mc.player.swingHand(Hand.MAIN_HAND);
                           this.lastMendingActionTick = this.pilotTicks;
                           this.xpBottlesThrown++;
                        }
                     }
                  } else if (!this.requestHotbarSwap(stack -> stack.isOf(Items.EXPERIENCE_BOTTLE)) && !ExtUtils.isAction()) {
                     if (this.canBuyGoods(Goods.XP) && !this.hasTaskFailed(AutoElytraPilot.Task.BUY_XP)) {
                        this.currentTask = AutoElytraPilot.Task.BUY_XP;
                        this.taskStartTick = this.pilotTicks;
                     } else {
                        this.finishTask(AutoElytraPilot.Task.MEND);
                     }
                  }
               }
            } else {
               this.mendingFailureCount = 0;
               this.notifyUser(AutoElytraPilot.Kind.GOOD, "Elytra repaired");
               this.finishTask(AutoElytraPilot.Task.MEND);
            }
         }
      }
   }

   private boolean isUsableElytraEquipped() {
      ItemStack equippedStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
      return equippedStack.isOf(Items.ELYTRA) && isElytraUsable(equippedStack);
   }

   private void updatePrepareState() {
      if (mc.player.isTouchingWater()) {
         this.swimToShore();
      } else if (this.isGliding()) {
         this.setFlightState(AutoElytraPilot.State.CLIMB);
      } else if (mc.player.isOnGround()) {
         if (this.isContainerScreenOpen() && !this.pilotAhBuyer.isRunning()) {
            mc.player.closeHandledScreen();
         } else if (!this.isUsableElytraEquipped()) {
            if (!this.equipSpareElytra()) {
               this.finishTrip(false, "No usable elytra left.");
            }
         } else {
            double destinationPlayer = this.getDistanceToDestination();
            if (destinationPlayer <= 30.0) {
               this.job = AutoElytraPilot.Job.ARRIVE;
               this.setFlightState(AutoElytraPilot.State.ARRIVED);
            } else if (this.getTicksInState() >= 4) {
               if (this.chooseNextTask() != AutoElytraPilot.Task.NONE) {
                  this.job = AutoElytraPilot.Job.SERVICE;
                  this.pitStopCount--;
                  this.setFlightState(AutoElytraPilot.State.SERVICE);
               } else if (this.countRockets() == 0) {
                  this.finishTrip(false, this.formatOutOfRocketsMessage());
               } else if (this.getEquippedElytraDurability() >= 0 && this.getEquippedElytraDurability() < 30) {
                  this.finishTrip(false, "The elytra is almost broken and can't be repaired.");
               } else {
                  this.job = AutoElytraPilot.Job.ARRIVE;
                  this.setFlightState(AutoElytraPilot.State.TAKEOFF);
               }
            }
         }
      }
   }

   private void applyRotationRequest() {
      if (mc.player != null) {
         if (!this.rotationRequested) {
            this.rotationSmoother.deactivate();
         } else {
            float yaw = mc.player.getYaw();
            float pitch = mc.player.getPitch();
            float[] amountRandom = this.humanAim
               .computeHumanizedRotation(yaw, pitch, this.targetYaw, this.targetPitch, ClampMath.getSensitivityMouse(), this.fastRotationRequested ? 540.0F : 360.0F);
            MathClampUtils.handle(this, amountRandom[0], amountRandom[1], 97, false);
            this.rotationSmoother.setSegment(this.pilotTicks, yaw, pitch, amountRandom[0], amountRandom[1]);
         }
      }
   }

   private int getUnbreakingLevel() {
      ItemStack equippedStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
      if (equippedStack.isEmpty()) {
         return 0;
      } else {
         ItemEnchantmentsComponent orDefault = equippedStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

         for (Entry entry : orDefault.getEnchantmentEntries()) {
            if (((RegistryEntry)entry.getKey()).matchesKey(Enchantments.UNBREAKING)) {
               return Math.max(0, entry.getIntValue());
            }
         }

         return 0;
      }
   }

   private boolean updateSwapTask() {
      if (mc.player.isOnGround() && !this.isGliding()) {
         PlayerInventory inventory = mc.player.getInventory();
         int selectedSlot = -1;
         int swappedElytraValue = Math.max(0, this.getEquippedElytraDurability()) + 40;

         for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(Items.ELYTRA) && getRemainingDurability(stack) > swappedElytraValue) {
               swappedElytraValue = getRemainingDurability(stack);
               selectedSlot = i;
            }
         }

         if (selectedSlot < 0) {
            return false;
         } else if (selectedSlot <= 8) {
            if (inventory.getSelectedSlot() != selectedSlot) {
               inventory.setSelectedSlot(selectedSlot);
               return true;
            } else {
               if (this.pilotTicks % 6 == 0) {
                  mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                  this.notifyUser(AutoElytraPilot.Kind.GOOD, "Swapped to a fresh elytra");
               }

               return true;
            }
         } else {
            int selectedSlot2 = selectedSlot;
            return this.requestHotbarSwap(src -> src == mc.player.getInventory().getStack(selectedSlot2)) || ExtUtils.isAction();
         }
      } else {
         return false;
      }
   }

   private String formatShortPhase() {
      return switch (this.flightState) {
         case PREPARE -> "Ready";
         case TAKEOFF -> "Takeoff";
         case CLIMB -> "Climbing";
         case CRUISE -> "Cruising";
         case DESCEND, LAND -> "Landing";
         case SERVICE -> this.currentTask == AutoElytraPilot.Task.MEND
            ? "Mending"
            : (this.currentTask != AutoElytraPilot.Task.BUY_ROCKETS && this.currentTask != AutoElytraPilot.Task.BUY_XP ? "Pit stop" : "Buying");
         case ARRIVED -> "Arrived";
         case STOPPED -> "Stopping";
      };
   }

   private boolean isHungry() {
      HungerManager hungerManager = mc.player.getHungerManager();
      return hungerManager.getFoodLevel() <= 14 || mc.player.getHealth() < 15.0F && hungerManager.getFoodLevel() < 20;
   }

   @Override
   public Snapshot getStatusSnapshot() {
      return this.isEnabled() ? this.menuSnapshot : Snapshot.INSTANCE;
   }

   private static enum Job {
      ARRIVE,
      SERVICE,
      STOP;
   }

   private static enum Kind {
      INFO,
      GOOD,
      WARN,
      STOP;
   }

   public static enum State {
      PREPARE,
      TAKEOFF,
      CLIMB,
      CRUISE,
      DESCEND,
      LAND,
      SERVICE,
      ARRIVED,
      STOPPED;
   }

   private static enum Task {
      NONE,
      EAT,
      MEND,
      BUY_ROCKETS,
      BUY_XP,
      SWAP;
   }
}
