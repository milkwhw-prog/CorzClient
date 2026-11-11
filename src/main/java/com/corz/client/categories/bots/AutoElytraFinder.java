package com.corz.client.categories.bots;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.utils.ConfirmScreenHelper.Status;
import com.corz.client.utils.ShipLocator.Loot;
import com.corz.client.utils.ShipLocator.Ship;
import com.corz.client.utils.ShipPather.Grid;
import com.corz.client.utils.ShipPather.Move;
import com.corz.client.utils.ShipPather.Result;
import com.corz.client.utils.ShipPather.Step;
import com.corz.client.entity.TimeInstanceUtils;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.TickProgress;
import com.corz.client.inventory.SelectedInventoryUtils;
import com.corz.client.world.MathClampUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.BlockOffsetUtils;
import com.corz.client.utils.ConfirmScreenHelper;
import com.corz.client.utils.ElytraStatus;
import com.corz.client.utils.ElytraStatusCard;
import com.corz.client.utils.ShipLocator;
import com.corz.client.utils.ShipPather;
import ai.auth.Module;
import com.corz.client.utils.HotbarSlotUtils;
import com.corz.client.utils.WrapDegreesUtils;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.ElytraAutoUtils;
import com.corz.client.utils.ExtUtils;
import com.corz.client.utils.IconsPngType;

public class AutoElytraFinder extends Module {
   private String endNeedText;
   private boolean landActive;
   private BlockPos pendingEnderChestPos;
   private final BooleanSetting flyCollectedElytraSetting;
   private final List<Vec3d> knownStructurePositions;
   private String retrieveStatusText;
   private boolean rotationOverrideActive;
   private int guardFightTick;
   private float plannedPitch;
   private final BooleanSetting combatShulkers;
   private BlockPos pendingShulkerPos;
   private int buyRetryNotBeforeTick;
   private boolean returnForStoredElytra;
   private Vec3d landingSpot;
   private Vec3d nearestShipBlockCenter;
   private AutoElytraFinder.Buy pendingPurchase;
   private BlockPos enderChestPos;
   private static final Set<Block> END_SHIP_BLOCKS = Set.of(
      Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR, Blocks.PURPUR_STAIRS, Blocks.PURPUR_SLAB, Blocks.END_STONE_BRICKS, Blocks.MAGENTA_STAINED_GLASS
   );
   private Vec3d fallbackTargetPos;
   private Vec3d cityTargetVec;
   private boolean shipBreakingActive;
   private static final float[] PITCH_TABLE_DEGREES = new float[]{-28.0F, -18.0F, -10.0F, -4.0F, 0.0F, 4.0F, 8.0F, 12.0F, 16.0F, 21.0F, 27.0F, 34.0F};
   private int storeStepStartTick;
   private float yaw;
   private static final String SHULKER_BOX = "ah shulker box";
   private boolean breakingBlockActive;
   private int shipsVisitedCount;
   private BlockPos failedExitBlockPos;
   private BlockPos placementWaitPos;
   private static final String FIREWORK_ROCKET = "ah firework rocket";
   private double flightPlanDistance;
   private int placementWaitStartTick;
   private static final float[] YAW_SWEEP_DEGREES = new float[]{0.0F, -18.0F, 18.0F, -36.0F, 36.0F, -60.0F, 60.0F, -90.0F, 90.0F};
   private Vec3d landingApproachPoint;
   private int lastSeenStoreStep;
   private Ship breakingTargetShip;
   private float yawBeforeOverride;
   private int lastNotificationTick;
   private final ConfirmScreenHelper confirmScreenHelper;
   private Direction breakingSide;
   private boolean lookTargetRequested;
   private BlockPos lastPathTargetPos;
   private float searchHeadingYaw;
   private final ShipLocator shipLocator;
   private int lastBreakPathTick;
   private static final float[] YAW_OFFSETS_DEGREES = new float[]{0.0F, -18.0F, 18.0F};
   private final IntegerSetting flightAltitudeSetting = new IntegerSetting(
      "Flight Altitude", "Cruising height. The bot climbs higher on its own to clear End Cities and ships.", 110, 70, 250
   );
   private int pillarJumpAttempts;
   private int retrieveStartTick;
   private List<Step> breakPathSteps;
   private Vec3d shulkerFightTargetPos;
   private static final int UNUSED_ONE = 1;
   private int fireworkActiveUntilTick;
   private int yawLockUntilTick;
   private float appliedPitch;
   private int rocketBuyFailureCount;
   private int frameMissingSinceTick;
   private int repairProgressTick;
   private final BooleanSetting autoStoreElytra;
   private int storeStep;
   private boolean shipPlayerActive;
   private BlockPos blockPosInteractionCancel;
   private int breakAttemptTick;
   private int healWaitStartTick;
   private boolean useOuterApproachPoint;
   private boolean hasClearColumnAbove;
   private float appliedYaw;
   private BlockPos placedShulkerPos;
   private int pillarTopY;
   private int breakPathIndex;
   private int peakElytraDamage;
   private String breakPathKey;
   private static final float[] PITCH_ANGLES_DEGREES = new float[]{-4.0F, 0.0F, 4.0F, 8.0F, 12.0F, 16.0F};
   private float lockedYaw;
   private int shulkerFightTick;
   private int shulkerCountAfterPickup;
   private final BooleanSetting statusCard;
   private int lastJumpTick;
   private boolean inventorySwapPending;
   private int droppedItemWaitStartTick;
   private final Grid breakPathGrid;
   private float pitch;
   private int lookPriority;
   private boolean enderChestSearched;
   private final IntegerSetting minFireworkCountSetting = new IntegerSetting(
      "Firework Minimum Count", "When you have fewer rockets than this, the bot lands safely and buys more from /ah.", 16, 4, 128
   );
   private int scanCellIndex;
   private int pickupStartTick;
   private int lastPillarTick;
   private int minElytraInInventory;
   private final Set<BlockPos> excludedPlacePositions;
   private int frameAnyUntilTick;
   private int elytraCountTarget;
   private String storeStatusText;
   private String pauseReason;
   private int lastInteractTick;
   private int lastAimUpdateTick;
   private int syncId;
   private int stepStartTick;
   private Float plannedYaw;
   private Integer targetShulkerId;
   private int cityWaitStartTick;
   private int placedAtTick;
   private final LongOpenHashSet failedStandPositions;
   private int lastXpThrowTick;
   private int stateStartTick;
   private int takeoffAttempts;
   private boolean interactionCancelActive;
   private int serverRemovalCount;
   private String storingElytraText;
   private int lastCitySpotTick;
   private static final int[] SCAN_DISTANCES = new int[]{10, 25, 45};
   private final ElytraStatusCard elytraStatusCard;
   private final BooleanSetting autoMending;
   private final EnumSetting notificationModeSetting;
   private int startElytraCount;
   private String lastNotificationText;
   private ElytraStatus elytraStatus;
   private int landingApproachTicks;
   private int enderChestCountBefore;
   private int lastFrameHitTick;
   private AutoElytraFinder.State stateBeforePause;
   private int silkRetryCount;
   private AutoElytraFinder.State currentState;
   private int endermanCooldownUntilTick;
   private final IntegerSetting minimumElytraDurability = new IntegerSetting(
      "Minimum Elytra Durability", "Land when the elytra you fly with gets this low - it never flies it to breaking.", 30, 25, 400
   );
   private float lookPitch;
   private Ship targetShip;
   private int endermanFleeCount;
   private final LongOpenHashSet breakPathExtraSolids;
   private int shipExitFailureCount;
   private int collectedElytraCount;
   private float lookYaw;
   private int lastFireworkUseTick;
   private static final float[] STEEP_PITCH_ANGLES_DEGREES = new float[]{-28.0F, -18.0F};
   private AutoElytraFinder.Job job;
   private double nearestShipBlockDistance;
   private static final String ENDER_CHEST = "ah ender chest";
   private BlockPos scanOrigin;
   private int tickCounter;
   private int eatingUntilTick;
   private int takeoffStartY;

   private void tickCruiseState() {
      if (mc.player.isOnGround() && !this.isGliding()) {
         AutoElytraFinder.Job job = this.job;
         this.job = AutoElytraFinder.Job.NONE;
         switch (job) {
            case MEND:
               this.enterState(AutoElytraFinder.State.MENDING);
               break;
            case BUY_ROCKETS:
               this.enterState(AutoElytraFinder.State.BUY);
               break;
            case STOP:
               this.pauseWithReason(this.pauseReason.isEmpty() ? "elytra worn out - repair or replace it" : this.pauseReason);
               break;
            case SWAP:
               if (this.getEquippedElytraDurability() > this.getMinElytraDurability()) {
                  this.enterState(AutoElytraFinder.State.PREPARING);
                  return;
               }

               this.job = AutoElytraFinder.Job.SWAP;
               if (this.swapToFreshElytra() || ExtUtils.isAction()) {
                  return;
               }

               this.pauseWithReason("elytra worn out");
               break;
            default:
               this.enterState(AutoElytraFinder.State.PREPARING);
         }
      } else if (!this.isGliding()) {
         if (mc.player.getVelocity().y < -0.3 && !this.hasLevitationEffect()) {
            this.pulseJumpToDeployElytra();
         }
      } else {
         if (this.landingSpot == null || !this.isLandingSpotStillValid()) {
            this.landingSpot = this.getBottomCenter();
         }

         if (this.landingSpot == null) {
            Vec3d vecLocator = this.getNearestKnownTarget();
            float wornElytraValue = vecLocator != null && this.getHorizontalDistanceTo(vecLocator) > 24.0 ? this.getYawToward(vecLocator) : mc.player.getYaw();
            this.steerFlight(wornElytraValue, Math.max(70.0, mc.player.getY() - 2.0), this.countFireworkRockets() > 3, vecLocator == null ? 0.0 : this.getHorizontalDistanceTo(vecLocator));
         } else {
            double hypot = this.getHorizontalDistanceTo(this.landingSpot);
            if (!(hypot > 40.0) && !this.isFireworkBoosting()) {
               if (this.tickCounter - this.lastAimUpdateTick >= 2 || this.plannedYaw == null) {
                  this.lastAimUpdateTick = this.tickCounter;
                  this.planGlideTowardTarget(this.landingSpot);
               }

               if (this.flightPlanDistance >= 900.0) {
                  this.landingSpot = null;
               }

               this.requestLookTarget(this.plannedYaw, this.plannedPitch, 2);
            } else {
               this.steerFlight(this.getYawToward(this.landingSpot), this.landingSpot.y + 14.0, hypot > 70.0, hypot);
            }
         }
      }
   }

   private void tickTakeoffState() {
      if (this.isGliding()) {
         this.enterState(AutoElytraFinder.State.CRUISE);
      } else if (this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      } else if (!this.fightNearbyEnderman()) {
         if (!mc.player.isOnGround()) {
            if (this.tickCounter - this.lastPillarTick < 20) {
               int shipCouldnLimit = this.findPillarBlockSlot();
               if (shipCouldnLimit >= 0) {
                  this.pillarUpward(shipCouldnLimit);
               }
            } else {
               this.steerTowardAscendStep();
            }
         } else if (this.needsRocketPurchase()) {
            this.job = AutoElytraFinder.Job.BUY_ROCKETS;
            this.enterState(AutoElytraFinder.State.BUY);
         } else if (this.shipExitFailureCount >= 3 || !this.isColumnAboveClear(this.getFeetBlockPos(), 14) && this.findClearTakeoffYaw() == null) {
            BlockPos blockPos = this.getFeetBlockPos();
            if (!this.isColumnAboveClear(blockPos, 14) && this.hasOpenNeighborColumn(blockPos)) {
               this.walkToOpenSide();
               if (this.getTicksInState() > 1800) {
                  this.pauseWithReason("couldn't get out of the ship");
               }
            } else {
               if (this.shipExitFailureCount >= 3 && this.failedExitBlockPos == null) {
                  this.failedExitBlockPos = blockPos;
               }

               int shipBreaking = this.walkAndMineToward("sky", pos -> this.isColumnAboveClear(pos, 14) && !pos.equals(this.failedExitBlockPos) || this.hasOpenNeighborColumn(pos), null);
               if (shipBreaking == 1) {
                  BlockPos floored = this.getFeetBlockPos();
                  if (!this.isColumnAboveClear(floored, 14)) {
                     this.walkToOpenSide();
                  } else {
                     this.shipExitFailureCount = 0;
                     this.failedExitBlockPos = null;
                     this.takeoffAttempts = 0;
                     this.enterState(AutoElytraFinder.State.TAKEOFF);
                  }
               } else {
                  if (shipBreaking < 0) {
                     this.escapeBoxedIn();
                  }

                  if (this.getTicksInState() > 1800) {
                     this.pauseWithReason("couldn't get out of the ship");
                  }
               }
            }
         } else {
            this.shipExitFailureCount++;
            this.takeoffAttempts = 0;
            this.enterState(AutoElytraFinder.State.TAKEOFF);
         }
      }
   }

   private static double horizontalDistanceBetween(Vec3d vec, Vec3d vec2) {
      return Math.hypot(vec.x - vec2.x, vec.z - vec2.z);
   }

   private void requestLookTarget(float degrees, float value, int eyePosValue) {
      if (!this.lookTargetRequested || eyePosValue >= this.lookPriority) {
         this.lookTargetRequested = true;
         this.lookPriority = eyePosValue;
         this.lookYaw = degrees;
         this.lookPitch = MathHelper.clamp(value, -90.0F, 90.0F);
      }
   }

   private BlockPos findPlacementSpot(BlockPos pos) {
      BlockPos floored = this.getFeetBlockPos();
      BlockPos add = null;
      double autoElytraValue = -1.0E9;

      for (int i = -3; i <= 3; i++) {
         for (int j = -3; j <= 3; j++) {
            for (int k = -1; k <= 1; k++) {
               BlockPos add2 = floored.add(i, k, j);
               if (!this.excludedPlacePositions.contains(add2) && this.canPlaceBlockAt(add2) && !add2.equals(pos) && (pos == null || !add2.equals(pos.up()))) {
                  double autoElytraLimit = -mc.player.getEyePos().distanceTo(Vec3d.ofCenter(add2)) - Math.abs(k) * 0.5;
                  if (autoElytraLimit > autoElytraValue) {
                     autoElytraValue = autoElytraLimit;
                     add = add2;
                  }
               }
            }
         }
      }

      if (add != null) {
         return add;
      } else {
         double maxValue = Double.MAX_VALUE;

         for (int m = -6; m <= 6; m++) {
            for (int p = -6; p <= 6; p++) {
               for (int q = -2; q <= 2; q++) {
                  BlockPos goodsSlot = floored.add(m, q, p);
                  if (!this.excludedPlacePositions.contains(goodsSlot) && !goodsSlot.equals(pos) && (pos == null || !goodsSlot.equals(pos.up())) && this.isFreeStandingSpot(goodsSlot)) {
                     double squaredDistanceTo = mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(goodsSlot));
                     if (squaredDistanceTo < maxValue) {
                        maxValue = squaredDistanceTo;
                        add = goodsSlot;
                     }
                  }
               }
            }
         }

         return add;
      }
   }

   private void walkToAndPickUpItem(ItemEntity itemEntity) {
      BlockPos blockPos = itemEntity.getBlockPos();
      if (this.isItemEntityInPickupRange(itemEntity)) {
         if (this.pickupStartTick < 0) {
            this.pickupStartTick = this.tickCounter;
         }

         if (this.tickCounter - this.pickupStartTick > 15 && !this.hasFreeInventorySlots(1)) {
            this.makeInventoryRoom();
         }

         if (this.tickCounter - this.pickupStartTick > 30) {
            this.walkToBlockPos(blockPos, itemEntity.getY() > mc.player.getY() + 0.6);
         } else {
            this.pressMovementKeysToward(itemEntity.getEntityPos());
         }
      } else {
         this.pickupStartTick = -1;
         int shipBreaking = this.walkAndMineToward(
            "pick" + blockPos.asLong(), pos -> pos.getX() == blockPos.getX() && pos.getZ() == blockPos.getZ() && Math.abs(pos.getY() - blockPos.getY()) <= 1, blockPos
         );
         if (shipBreaking < 0) {
            int walkAndMineToward2 = this.walkAndMineToward(
               "pick-near" + blockPos.asLong(), pos2 -> Vec3d.ofBottomCenter(pos2).squaredDistanceTo(itemEntity.getEntityPos()) <= 1.9599999999999997, blockPos
            );
            if (walkAndMineToward2 == 1) {
               this.pressMovementKeysToward(itemEntity.getEntityPos());
            }
         } else if (shipBreaking == 1) {
            this.pressMovementKeysToward(itemEntity.getEntityPos());
         }
      }
   }

   private boolean canPlaceBlockAt(BlockPos pos2) {
      if (!BlockOffsetUtils.canPlaceAt(pos2) || !this.getBlockState(pos2).isAir()) {
         return false;
      } else if (!this.hasCollision(pos2.down()) || this.getBlockState(pos2.down()).hasBlockEntity()) {
         return false;
      } else if (!this.hasCollision(pos2.up()) && !this.getBlockState(pos2.up()).hasBlockEntity()) {
         Box box = new Box(pos2);
         if (mc.player.getBoundingBox().intersects(box) || mc.player.getBoundingBox().intersects(new Box(pos2.up()))) {
            return false;
         } else if (!mc.world.getOtherEntities(mc.player, box.expand(0.1)).isEmpty()) {
            return false;
         } else if (mc.player.getEyePos().distanceTo(Vec3d.ofCenter(pos2)) > 3.4) {
            return false;
         } else {
            Vec3d eyePos = mc.player.getEyePos();
            BlockHitResult raycast = mc.world
               .raycast(new RaycastContext(eyePos, Vec3d.ofCenter(pos2), ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
            if (raycast.getType() == Type.BLOCK) {
               return false;
            } else {
               Vec3d add = Vec3d.ofCenter(pos2.down()).add(0.0, 0.45, 0.0);
               return mc.world.raycast(new RaycastContext(eyePos, add, ShapeType.OUTLINE, FluidHandling.NONE, mc.player)) instanceof BlockHitResult blockHitResult
                  && blockHitResult.getType() == Type.BLOCK
                  && blockHitResult.getBlockPos().equals(pos2.down())
                  && blockHitResult.getSide() == Direction.UP;
            }
         }
      } else {
         return false;
      }
   }

   private boolean isLineFromEyeBlocked(Vec3d vec, double eyePosValue) {
      return this.isRayBlocked(mc.player.getEyePos(), vec, eyePosValue);
   }

   private boolean areChunkSectionsEmpty(Box box) {
      int floorSectionLimit = MathHelper.floor(box.minX) - 2;
      int floorOffset = MathHelper.floor(box.maxX) + 2;
      int floorSectionLimit2 = MathHelper.floor(box.minY) - 2;
      int floorOffset2 = MathHelper.floor(box.maxY) + 2;
      int floorSectionLimit3 = MathHelper.floor(box.minZ) - 2;
      int floorOffset3 = MathHelper.floor(box.maxZ) + 2;
      if (floorOffset < floorSectionLimit || floorOffset2 < floorSectionLimit2 || floorOffset3 < floorSectionLimit3) {
         return false;
      } else if (floorSectionLimit2 >= mc.world.getBottomY() && floorOffset2 <= mc.world.getTopYInclusive()) {
         for (int i = floorSectionLimit >> 4; i <= floorOffset >> 4; i++) {
            for (int j = floorSectionLimit3 >> 4; j <= floorOffset3 >> 4; j++) {
               WorldChunk worldChunk = mc.world.getChunkManager().getWorldChunk(i, j);
               if (worldChunk == null) {
                  return false;
               }

               ChunkSection[] sectionArray = worldChunk.getSectionArray();

               for (int k = floorSectionLimit2 >> 4; k <= floorOffset2 >> 4; k++) {
                  int sectionCoordToIndex = worldChunk.sectionCoordToIndex(k);
                  if (sectionCoordToIndex < 0 || sectionCoordToIndex >= sectionArray.length || sectionArray[sectionCoordToIndex] == null || !sectionArray[sectionCoordToIndex].isEmpty()) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean makeInventoryRoom() {
      if (this.hasFreeInventorySlots(1)) {
         return true;
      } else {
         PlayerInventory inventory = mc.player.getInventory();

         for (int i = 0; i < 9; i++) {
            if (this.isPlaceableEndBlock(inventory.getStack(i))) {
               if (inventory.getSelectedSlot() != i) {
                  inventory.setSelectedSlot(i);
                  return true;
               }

               mc.player.dropSelectedItem(true);
               return true;
            }
         }

         return this.swapMatchingItemToHotbar(this::isPlaceableEndBlock) || ExtUtils.isAction();
      }
   }

   private void moveTowardWhileSneaking(Vec3d vec) {
      ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, true);
      this.pressMovementKeysToward(vec);
   }

   private boolean containsElytra(ItemStack stack) {
      ContainerComponent containerComponent = stack.get(DataComponentTypes.CONTAINER);
      if (containerComponent == null) {
         return false;
      } else {
         for (ItemStack itemStack : containerComponent.iterateNonEmpty()) {
            if (itemStack.isOf(Items.ELYTRA)) {
               return true;
            }
         }

         return false;
      }
   }

   private void tickStoreState() {
      if (!this.isGliding()) {
         if (this.storeStep != this.lastSeenStoreStep) {
            this.lastSeenStoreStep = this.storeStep;
            this.storeStepStartTick = this.tickCounter;
         }

         if (this.confirmScreenHelper.isRunningSuccess()) {
            this.storeStatusText = this.pendingPurchase == AutoElytraFinder.Buy.CHEST ? "Buying an ender chest" : "Buying a shulker box";
         } else if (this.isContainerScreenOpen() || !this.fightNearbyEnderman() && !this.isShulkerFighting()) {
            if (!mc.player.isOnGround()) {
               this.steerTowardAscendStep();
            } else {
               this.storeStatusText = "Storing the elytra";
               switch (this.storeStep) {
                  case 0:
                     if (this.enderChestPos != null && this.hasBlockMatchedForTicks(this.enderChestPos, block -> block == Blocks.ENDER_CHEST)) {
                        this.setStoreStep(1);
                        return;
                     }

                     if (this.enderChestPos != null && this.getBlockState(this.enderChestPos).isOf(Blocks.ENDER_CHEST)) {
                        return;
                     }

                     if (this.countInventoryItem(Items.ENDER_CHEST) < 1) {
                        if (this.isPurchaseFinished(AutoElytraFinder.Buy.CHEST)) {
                           boolean shulkerCarryingFlag = this.confirmScreenHelper.getStatus() == Status.SUCCESS;
                           this.confirmScreenHelper.updateHandledSuccess();
                           this.pendingPurchase = AutoElytraFinder.Buy.NONE;
                           if (!shulkerCarryingFlag || this.countInventoryItem(Items.ENDER_CHEST) < 1) {
                              this.buyRetryNotBeforeTick = this.tickCounter + 1200;
                              this.notifyStatus(AutoElytraFinder.Kind.WARN, "Couldn't buy an ender chest - carrying the elytra");
                              this.enterState(AutoElytraFinder.State.EXIT);
                           }

                           return;
                        }

                        if (this.tickCounter < this.buyRetryNotBeforeTick) {
                           this.enterState(AutoElytraFinder.State.EXIT);
                           return;
                        }

                        if (!this.startAuctionPurchase(AutoElytraFinder.Buy.CHEST, "ah ender chest", Items.ENDER_CHEST, null, false)) {
                           this.notifyStatus(AutoElytraFinder.Kind.WARN, "No ender chest - carrying the elytra");
                           this.enterState(AutoElytraFinder.State.EXIT);
                        }

                        return;
                     }

                     if (this.enderChestPos == null) {
                        this.enderChestPos = this.findPlacementSpot(null);
                     }

                     if (this.enderChestPos == null) {
                        this.notifyStatus(AutoElytraFinder.Kind.WARN, "No room to place storage - carrying the elytra");
                        this.enterState(AutoElytraFinder.State.EXIT);
                        return;
                     }

                     if (this.placedAtTick < 0) {
                        int posBlock = this.prepareBlockPlacement(this.enderChestPos);
                        if (posBlock < 0) {
                           this.enderChestPos = null;
                           return;
                        }

                        if (posBlock == 0) {
                           if (this.getTicksSincePurchaseStart() > 200) {
                              this.enterState(AutoElytraFinder.State.EXIT);
                           }

                           return;
                        }
                     }

                     if (this.placedAtTick < 0 && this.tryPlaceBlockAt(this.enderChestPos, stack -> stack.isOf(Items.ENDER_CHEST), block2 -> block2 == Blocks.ENDER_CHEST)) {
                        this.placedAtTick = this.tickCounter;
                     }

                     if (this.placedAtTick >= 0 && !this.getBlockState(this.enderChestPos).isOf(Blocks.ENDER_CHEST)) {
                        this.placedAtTick = -1;
                        if (++this.serverRemovalCount >= 3) {
                           this.notifyStatus(AutoElytraFinder.Kind.WARN, "The server keeps removing the ender chest - carrying the elytra");
                           this.enderChestPos = null;
                           this.enterState(AutoElytraFinder.State.EXIT);
                           return;
                        }

                        this.enderChestPos = null;
                     }

                     if (this.getTicksSincePurchaseStart() > 200) {
                        this.enderChestPos = null;
                        this.enterState(AutoElytraFinder.State.EXIT);
                     }
                     break;
                  case 1:
                     if (this.isContainerScreenOpen()) {
                        this.syncId = mc.player.currentScreenHandler.syncId;
                        this.setStoreStep(2);
                        return;
                     }

                     if (!this.getBlockState(this.enderChestPos).isOf(Blocks.ENDER_CHEST)) {
                        this.enderChestPos = null;
                        this.setStoreStep(0);
                        return;
                     }

                     this.interactWithPlacedBlock(this.enderChestPos);
                     if (this.getTicksSincePurchaseStart() > 200) {
                        this.abortStoreAndExit();
                     }
                     break;
                  case 2:
                     if (this.isPurchaseFinished(AutoElytraFinder.Buy.SHULKER)) {
                        boolean shulkerCarryingFlag2 = this.confirmScreenHelper.getStatus() == Status.SUCCESS;
                        this.confirmScreenHelper.updateHandledSuccess();
                        this.pendingPurchase = AutoElytraFinder.Buy.NONE;
                        if (shulkerCarryingFlag2 && this.findStorableShulkerSlot() >= 0) {
                           this.placedShulkerPos = null;
                           this.setStoreStep(3);
                           return;
                        }

                        this.buyRetryNotBeforeTick = this.tickCounter + 1200;
                        this.notifyStatus(AutoElytraFinder.Kind.WARN, "Couldn't buy a shulker - carrying the elytra");
                        this.abortStoreAndExit();
                        return;
                     }

                     if (this.findStorableShulkerSlot() >= 0) {
                        if (this.isContainerScreenOpen()) {
                           mc.player.closeHandledScreen();
                           return;
                        }

                        this.placedShulkerPos = null;
                        this.serverRemovalCount = 0;
                        this.setStoreStep(3);
                        return;
                     }

                     if (this.isContainerScreenOpen()) {
                        if (mc.player.currentScreenHandler.syncId != this.syncId) {
                           mc.player.closeHandledScreen();
                           this.updateSilkCarrying();
                           return;
                        }

                        int slotStack = this.findContainerSlot(this::isShulkerFilledOnlyWithElytra);
                        if (slotStack >= 0) {
                           if (!this.hasFreeInventorySlots(1)) {
                              mc.player.closeHandledScreen();
                              if (!this.makeInventoryRoom()) {
                                 this.abortStoreAndExit();
                              } else {
                                 this.updateSilkCarrying();
                              }

                              return;
                           }

                           if (this.tickCounter % 4 == 0) {
                              mc.interactionManager.clickSlot(this.syncId, slotStack, 0, SlotActionType.QUICK_MOVE, mc.player);
                           }

                           if (this.getTicksSincePurchaseStart() > 300) {
                              this.abortStoreAndExit();
                           }

                           return;
                        }

                        this.enderChestSearched = true;
                        mc.player.closeHandledScreen();
                        return;
                     }

                     if (!this.enderChestSearched) {
                        this.updateSilkCarrying();
                        return;
                     }

                     if (this.tickCounter < this.buyRetryNotBeforeTick) {
                        this.abortStoreAndExit();
                        return;
                     }

                     if (!this.startAuctionPurchase(
                        AutoElytraFinder.Buy.SHULKER, "ah shulker box", Items.SHULKER_BOX, stack2 -> this.canBlock(stack2) && this.countContainerItems(stack2) == 0, false
                     )) {
                        this.notifyStatus(AutoElytraFinder.Kind.WARN, "No shulker box - carrying the elytra");
                        this.abortStoreAndExit();
                     }
                     break;
                  case 3:
                     if (this.placedShulkerPos != null && this.hasBlockMatchedForTicks(this.placedShulkerPos, this::isShulkerBox)) {
                        this.setStoreStep(4);
                        return;
                     }

                     if (this.placedShulkerPos != null && this.isShulkerBox(this.getBlockState(this.placedShulkerPos).getBlock())) {
                        return;
                     }

                     if (this.placedShulkerPos == null) {
                        this.placedShulkerPos = this.findPlacementSpot(this.enderChestPos);
                     }

                     if (this.placedShulkerPos == null) {
                        this.notifyStatus(AutoElytraFinder.Kind.WARN, "No room for the shulker - carrying the elytra");
                        this.abortStoreAndExit();
                        return;
                     }

                     if (this.placedAtTick < 0) {
                        int prepareBlockPlacement2 = this.prepareBlockPlacement(this.placedShulkerPos);
                        if (prepareBlockPlacement2 < 0) {
                           this.placedShulkerPos = null;
                           return;
                        }

                        if (prepareBlockPlacement2 == 0) {
                           if (this.getTicksSincePurchaseStart() > 300) {
                              this.abortStoreAndExit();
                           }

                           return;
                        }
                     }

                     if (this.placedAtTick < 0 && this.tryPlaceBlockAt(this.placedShulkerPos, this::isShulkerFilledOnlyWithElytra, this::isShulkerBox)) {
                        this.placedAtTick = this.tickCounter;
                     }

                     if (this.placedAtTick >= 0 && !this.isShulkerBox(this.getBlockState(this.placedShulkerPos).getBlock())) {
                        this.placedAtTick = -1;
                        if (++this.serverRemovalCount >= 3) {
                           this.notifyStatus(AutoElytraFinder.Kind.WARN, "The server keeps removing the shulker - carrying the elytra");
                           this.placedShulkerPos = null;
                           this.abortStoreAndExit();
                           return;
                        }

                        this.placedShulkerPos = null;
                     }

                     if (this.getTicksSincePurchaseStart() > 200) {
                        this.abortStoreAndExit();
                     }
                     break;
                  case 4:
                     if (this.isContainerScreenOpen() && mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler) {
                        this.setStoreStep(5);
                        return;
                     }

                     if (this.isContainerScreenOpen()) {
                        mc.player.closeHandledScreen();
                        return;
                     }

                     if (!this.isShulkerBox(this.getBlockState(this.placedShulkerPos).getBlock())) {
                        this.placedShulkerPos = null;
                        this.setStoreStep(3);
                        return;
                     }

                     this.interactWithPlacedBlock(this.placedShulkerPos);
                     if (this.getTicksSincePurchaseStart() > 200) {
                        this.abortStoreAndExit();
                     }
                     break;
                  case 5:
                     if (!(mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler)) {
                        this.setStoreStep(4);
                        return;
                     }

                     if (this.countElytraInInventory() <= this.minElytraInInventory
                        || this.findPlayerSlotInScreen(this::isUnusedPlainElytra) < 0
                        || this.flyCollectedElytraSetting.isEnabled() && this.countUsableSpareElytra() <= 1) {
                        this.setStoreStep(6);
                        return;
                     }

                     if (this.tickCounter % 4 != 0) {
                        return;
                     }

                     int slotsSlot = this.findPlayerSlotInScreen(this::isUnusedPlainElytra);
                     if (slotsSlot >= 0) {
                        mc.interactionManager
                           .clickSlot(mc.player.currentScreenHandler.syncId, slotsSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
                     }

                     if (this.getTicksSincePurchaseStart() > 200) {
                        this.setStoreStep(6);
                     }
                     break;
                  case 6:
                     this.storeStatusText = "Picking the shulker back up";
                     if (this.isContainerScreenOpen()) {
                        mc.player.closeHandledScreen();
                        return;
                     }

                     if (this.countShulkerBoxItems() > this.shulkerCountAfterPickup) {
                        this.pickupStartTick = -1;
                        if (this.hasShulkerWithElytra()) {
                           this.setStoreStep(7);
                        } else {
                           this.beginRecoverChest();
                        }

                        return;
                     }

                     if (this.placedShulkerPos != null && this.isShulkerBox(this.getBlockState(this.placedShulkerPos).getBlock())) {
                        if (!this.hasFreeInventorySlots(1) && !this.makeInventoryRoom()) {
                           this.pauseWithReason("inventory full - can't pick the shulker up");
                           return;
                        }

                        if (!this.isBlockWithinEyeReach(this.placedShulkerPos, 3.6)) {
                           this.walkNearBlock(this.placedShulkerPos);
                           return;
                        }

                        this.breakBlockAt(this.placedShulkerPos, true, true);
                        if (this.getTicksSincePurchaseStart() > 1200) {
                           this.pauseWithReason("couldn't break the shulker");
                        }

                        return;
                     }

                     if (this.droppedItemWaitStartTick < 0) {
                        this.droppedItemWaitStartTick = this.tickCounter;
                     }

                     ItemEntity entitiesSquared = this.getNearestItemEntity(12.0, itemEntity -> this.canBlock(itemEntity.getStack()));
                     if (entitiesSquared != null) {
                        this.walkToAndPickUpItem(entitiesSquared);
                        if (this.tickCounter - this.droppedItemWaitStartTick > 1200) {
                           this.pauseWithReason("can't reach the dropped shulker - pick it up");
                        }

                        return;
                     }

                     if (this.tickCounter - this.droppedItemWaitStartTick > 60) {
                        this.pauseWithReason("lost the shulker with the elytra");
                     }
                     break;
                  case 7:
                     if (this.enderChestPos == null || !this.getBlockState(this.enderChestPos).isOf(Blocks.ENDER_CHEST)) {
                        this.enterState(AutoElytraFinder.State.EXIT);
                        return;
                     }

                     if (this.isContainerScreenOpen()) {
                        this.syncId = mc.player.currentScreenHandler.syncId;
                        this.setStoreStep(8);
                        return;
                     }

                     if (!this.isBlockWithinEyeReach(this.enderChestPos, 3.5)) {
                        this.walkNearBlock(this.enderChestPos);
                        return;
                     }

                     this.interactWithPlacedBlock(this.enderChestPos);
                     if (this.getTicksSincePurchaseStart() > 200) {
                        this.beginRecoverChest();
                     }
                     break;
                  case 8:
                     if (!this.isContainerScreenOpen()) {
                        this.setStoreStep(7);
                        return;
                     }

                     if (this.findPlayerSlotInScreen(this::isShulkerBoxOfOnlyElytra) < 0) {
                        this.setStoreStep(9);
                        return;
                     }

                     if (this.findContainerSlot(ItemStack::isEmpty) < 0) {
                        this.notifyStatus(AutoElytraFinder.Kind.WARN, "Ender chest is full - keeping the shulker");
                        mc.player.closeHandledScreen();
                        this.beginRecoverChest();
                        return;
                     }

                     if (this.tickCounter % 4 != 0) {
                        return;
                     }

                     int playerSlotInScreen = this.findPlayerSlotInScreen(this::isShulkerBoxOfOnlyElytra);
                     if (playerSlotInScreen >= 0) {
                        mc.interactionManager
                           .clickSlot(mc.player.currentScreenHandler.syncId, playerSlotInScreen, 0, SlotActionType.QUICK_MOVE, mc.player);
                     }

                     if (this.getTicksSincePurchaseStart() > 200) {
                        this.beginRecoverChest();
                     }
                     break;
                  case 9:
                     if (this.isContainerScreenOpen()) {
                        mc.player.closeHandledScreen();
                        return;
                     }

                     this.notifyStatus(AutoElytraFinder.Kind.GOOD, "Elytra stored in your ender chest");
                     this.beginRecoverChest();
                     break;
                  default:
                     this.enterState(AutoElytraFinder.State.EXIT);
               }
            }
         }
      }
   }

   private boolean hasFireworkRocket() {
      return this.getFireworkSlot() >= 0 || this.hasFireworkInOffhand();
   }

   private float avoidEndermanGaze(float eyeProductValue, float eyeProduct) {
      Vec3d eyePos = mc.player.getEyePos();
      List entitiesByClass = mc.world.getEntitiesByClass(EndermanEntity.class, getBox(eyePos, 64.0), endermanEntity -> endermanEntity.isAlive() && !endermanEntity.isAngry());
      if (entitiesByClass.isEmpty()) {
         return eyeProduct;
      } else {
         for (int i = 0; i < 4; i++) {
            Vec3d cosSin = MathClampUtils.getCosSin(eyeProductValue, eyeProduct);
            boolean eyeProductFlag = false;

            for (EndermanEntity endermanEntity2 : (Iterable<net.minecraft.entity.mob.EndermanEntity>) (entitiesByClass)) {
               Vec3d vec3d = new Vec3d(endermanEntity2.getX() - eyePos.x, endermanEntity2.getEyeY() - eyePos.y, endermanEntity2.getZ() - eyePos.z);
               double varLength = vec3d.length();
               if (!(varLength < 0.001)
                  && cosSin.dotProduct(vec3d.multiply(1.0 / varLength)) > 1.0 - 0.025 / varLength - 0.004
                  && this.hasLineOfSight(new Vec3d(endermanEntity2.getX(), endermanEntity2.getEyeY(), endermanEntity2.getZ()))) {
                  eyeProductFlag = true;
                  break;
               }
            }

            if (!eyeProductFlag) {
               return eyeProduct;
            }

            eyeProduct = MathHelper.clamp(eyeProduct + 6.0F, -90.0F, 90.0F);
         }

         return eyeProduct;
      }
   }

   private boolean checkElytraDurability() {
      int equippedDamage = this.getEquippedElytraDurability();
      int elytraLowValue = this.getMinElytraDurability();
      if (equippedDamage < 0 || equippedDamage > elytraLowValue) {
         return false;
      } else if (this.currentState == AutoElytraFinder.State.LAND
         || this.currentState == AutoElytraFinder.State.MENDING
         || this.currentState == AutoElytraFinder.State.PAUSED
         || this.currentState == AutoElytraFinder.State.STORE
         || this.currentState == AutoElytraFinder.State.RECOVER_CHEST
         || this.currentState == AutoElytraFinder.State.BUY
         || this.currentState == AutoElytraFinder.State.RETRIEVE) {
         return false;
      } else if (!this.isGliding() && mc.player.isOnGround()) {
         if (this.flyCollectedElytraSetting.isEnabled() && this.swapToFreshElytra()) {
            return true;
         } else if (this.canMendWithXp()) {
            this.notifyStatus(AutoElytraFinder.Kind.WARN, "Elytra low - mending with XP");
            this.enterState(AutoElytraFinder.State.MENDING);
            return true;
         } else {
            this.pauseWithReason("elytra worn out - repair it");
            return true;
         }
      } else if (!this.isGliding()) {
         return false;
      } else {
         this.job = this.canMendWithXp()
            ? AutoElytraFinder.Job.MEND
            : (this.flyCollectedElytraSetting.isEnabled() && this.hasBetterElytraInInventory() ? AutoElytraFinder.Job.SWAP : AutoElytraFinder.Job.STOP);
         this.notifyStatus(AutoElytraFinder.Kind.WARN, "Elytra low - landing");
         this.enterState(AutoElytraFinder.State.LAND);
         return true;
      }
   }

   private boolean equipFreshHotbarElytra() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(Items.ELYTRA) && this.hasDurabilityLeft(stack) && stack.getMaxDamage() - stack.getDamage() >= 10) {
            if (inventory.getSelectedSlot() != i) {
               inventory.setSelectedSlot(i);
               return true;
            }

            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            return true;
         }
      }

      return this.swapMatchingItemToHotbar(stack2 -> stack2.isOf(Items.ELYTRA) && this.hasDurabilityLeft(stack2) && stack2.getMaxDamage() - stack2.getDamage() >= 10);
   }

   private int prepareBlockPlacement(BlockPos blockPos) {
      if (this.isBlockWithinEyeReach(blockPos, 3.5) && this.canPlaceBlockAt(blockPos)) {
         this.placementWaitPos = null;
         return 1;
      } else if (!this.isFreeStandingSpot(blockPos)) {
         this.placementWaitPos = null;
         return -1;
      } else {
         if (!blockPos.equals(this.placementWaitPos)) {
            this.placementWaitPos = blockPos;
            this.placementWaitStartTick = this.tickCounter;
         }

         if (this.tickCounter - this.placementWaitStartTick > 120) {
            this.excludedPlacePositions.add(blockPos);
            this.placementWaitPos = null;
            return -1;
         } else {
            int shipBreaking = this.walkAndMineToward(
               "beside" + blockPos.asLong(),
               pos -> !pos.equals(blockPos)
                  && !pos.equals(blockPos.up())
                  && Math.abs(pos.getX() - blockPos.getX()) <= 1
                  && Math.abs(pos.getZ() - blockPos.getZ()) <= 1
                  && Math.abs(pos.getY() - blockPos.getY()) <= 1,
               blockPos
            );
            if (shipBreaking < 0) {
               this.pressMovementKeysToward(Vec3d.ofCenter(blockPos));
            }

            return 0;
         }
      }
   }

   private void setStoreStep(int shulkerBuyingValue) {
      this.storeStep = shulkerBuyingValue;
      this.storeStepStartTick = this.tickCounter;
      this.placedAtTick = -1;
      this.droppedItemWaitStartTick = -1;
      if (shulkerBuyingValue == 1) {
         this.enderChestSearched = false;
      }

      if (shulkerBuyingValue == 6) {
         this.shulkerCountAfterPickup = this.countShulkerBoxItems();
      }
   }

   private Float findClearTakeoffYaw() {
      float yaw = mc.player.getYaw();
      if (this.isRayBlocked(mc.player.getEyePos(), new Vec3d(0.0, 1.0, 0.0), 2.2)) {
         return null;
      } else {
         Vec3d entityPos = mc.player.getEntityPos();

         for (float normalizePosValue : new float[]{0.0F, 45.0F, -45.0F, 90.0F, -90.0F, 135.0F, -135.0F, 180.0F}) {
            Vec3d wrapDegrees = this.getLookVector(yaw + normalizePosValue, -32.0F);
            Vec3d multiply = new Vec3d(-wrapDegrees.z, 0.0, wrapDegrees.x).normalize().multiply(0.35);
            boolean normalizePosFlag = false;

            for (Vec3d vec3d : new Vec3d[]{
               entityPos.add(0.0, 1.2, 0.0), entityPos.add(0.0, 1.8, 0.0), entityPos.add(0.0, 1.2, 0.0).add(multiply), entityPos.add(0.0, 1.2, 0.0).subtract(multiply)
            }) {
               if (this.isRayBlocked(vec3d, wrapDegrees, 22.0)) {
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

   private Vec3d findLandingSpot(Ship ship) {
      int[][] ints = new int[][]{{4, 11}, {8, 11}, {4, 12}, {8, 12}, {4, 13}, {8, 13}, {4, 10}, {8, 10}, {4, 16}, {8, 16}, {5, 11}, {7, 11}};

      for (int i = 0; i < ints.length; i += 2) {
         Vec3d bottomCenter = this.findLandingSpotAtOffset(ship, ints[i]);
         Vec3d landingSpotAtOffset = this.findLandingSpotAtOffset(ship, ints[i + 1]);
         if (bottomCenter != null && landingSpotAtOffset != null) {
            return this.getHorizontalDistanceTo(bottomCenter) <= this.getHorizontalDistanceTo(landingSpotAtOffset) ? bottomCenter : landingSpotAtOffset;
         }

         if (bottomCenter != null) {
            return bottomCenter;
         }

         if (landingSpotAtOffset != null) {
            return landingSpotAtOffset;
         }
      }

      BlockPos offsetYclockwise = ship.getRelativePos(6, 8, 12);

      for (int j = 0; j <= 8; j++) {
         for (int k = -j; k <= j; k++) {
            for (int m = -j; m <= j; m++) {
               if (Math.max(Math.abs(k), Math.abs(m)) == j) {
                  for (int p = 3; p >= -3; p--) {
                     BlockPos add = offsetYclockwise.add(k, p, m);
                     if (this.isStandableSpot(add) && this.isColumnAboveClear(add, 3) && ShipLocator.isPurpurOrEndBlock(this.getBlockState(add.down()))) {
                        return Vec3d.ofBottomCenter(add);
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   private boolean isNearTargetShip() {
      return this.targetShip == null
         ? false
         : this.getHorizontalDistanceTo(this.targetShip.getCenter()) < 22.0
            && mc.player.getY() > this.targetShip.getY() - 8
            && mc.player.getY() < this.targetShip.getY() + 6;
   }

   private int getMinElytraDurability() {
      return Math.max(this.minimumElytraDurability.getValue(), 25);
   }

   private boolean hasFireworkInOffhand() {
      return this.isPlainFirework(mc.player.getOffHandStack());
   }

   private float getYawToward(Vec3d vec) {
      return (float)Math.toDegrees(Math.atan2(-(vec.x - mc.player.getX()), vec.z - mc.player.getZ()));
   }

   private boolean startAuctionPurchase(AutoElytraFinder.Buy buy, String confirmScreenText, Item item, Predicate<ItemStack> predicate, boolean confirmScreenFlag) {
      if (this.confirmScreenHelper.isRunningSuccess()) {
         return this.pendingPurchase == buy;
      } else {
         this.confirmScreenHelper.updateHandledSuccess();
         if (!this.confirmScreenHelper.canBoughtFull(confirmScreenText, item, predicate, 1, 30, 6)) {
            this.confirmScreenHelper.updateHandledSuccess();
            this.pendingPurchase = AutoElytraFinder.Buy.NONE;
            return false;
         } else {
            this.confirmScreenHelper.setBoughtFullActive(confirmScreenFlag);
            this.pendingPurchase = buy;
            return true;
         }
      }
   }

   private void beginShipApproach(Ship ship) {
      boolean shipEndFlag = ship == this.targetShip;
      if (!shipEndFlag) {
         this.returnForStoredElytra = false;
         this.endermanFleeCount = 0;
         this.endermanCooldownUntilTick = -1;
      }

      this.targetShip = ship;
      this.landingSpot = null;
      this.landingApproachPoint = null;
      this.useOuterApproachPoint = true;
      this.healWaitStartTick = -1;
      if (!shipEndFlag) {
         this.notifyStatus(AutoElytraFinder.Kind.INFO, "End Ship found");
      }

      this.enterState(AutoElytraFinder.State.TO_SHIP);
   }

   public boolean isBreakingBlock() {
      return this.isEnabled() && this.interactionCancelActive;
   }

   private void walkToBlockPos(BlockPos pos, boolean optionsPressedFlag) {
      Vec3d bottomCenter = Vec3d.ofBottomCenter(pos);
      double hypot = this.getHorizontalDistanceTo(bottomCenter);
      boolean optionsPressedFlag2 = pos.getY() < this.getFeetBlockPos().getY();
      if (hypot < 0.6) {
         if (!optionsPressedFlag2) {
            ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, true);
         }

         this.pressMovementKeysToward(bottomCenter);
         if (optionsPressedFlag && mc.player.isOnGround()) {
            mc.options.jumpKey.setPressed(true);
         }
      } else {
         float degrees = this.getYawToward(bottomCenter);
         this.requestLookTarget(degrees, 12.0F, 0);
         if (Math.abs(MathHelper.wrapDegrees(degrees - mc.player.getYaw())) < 35.0F) {
            mc.options.forwardKey.setPressed(true);
         } else {
            this.pressMovementKeysToward(bottomCenter);
         }

         if (!optionsPressedFlag2 && !optionsPressedFlag && hypot < 1.2) {
            ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, true);
         }

         if (optionsPressedFlag && mc.player.isOnGround() && hypot < 1.5) {
            mc.options.jumpKey.setPressed(true);
         }
      }
   }

   private Vec3d getLookVector(float degrees, float value) {
      return MathClampUtils.getCosSin(MathHelper.wrapDegrees(degrees), MathHelper.clamp(value, -90.0F, 90.0F));
   }

   private int countUsableSpareElytra() {
      PlayerInventory inventory = mc.player.getInventory();
      int damageMax = 0;

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(Items.ELYTRA)) {
            int damageValue = stack.getMaxDamage() > 0 ? stack.getMaxDamage() : 432;
            if (damageValue - stack.getDamage() > this.getMinElytraDurability() + 40) {
               damageMax++;
            }
         }
      }

      return damageMax;
   }

   private void lookAtPoint(Vec3d vec, int eyePosValue) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(mc.player.getEyePos(), vec);
      this.requestLookTarget(wrapDegrees[0], wrapDegrees[1], eyePosValue);
   }

   private void refreshStatusCard() {
      if (mc.player != null) {
         int equippedDamage = this.getEquippedElytraDurability();
         double collectedElytraValue = -1.0;
         if (this.currentState != AutoElytraFinder.State.TO_SHIP && this.currentState != AutoElytraFinder.State.LANDING) {
            if (this.currentState == AutoElytraFinder.State.TO_CITY && this.cityTargetVec != null) {
               collectedElytraValue = this.getHorizontalDistanceTo(this.cityTargetVec);
            }
         } else {
            collectedElytraValue = this.targetShip == null ? -1.0 : this.getHorizontalDistanceTo(Vec3d.ofCenter(this.targetShip.blockPos));
         }
         int collectedElytraValue2 = switch (this.currentState) {
            case LAND, MENDING, BUY -> 1;
            case PAUSED -> 2;
            default -> this.tickCounter - this.lastNotificationTick < 60 && this.lastNotificationText.startsWith("Elytra collected") ? 3 : 0;
         };
         this.elytraStatus = new ElytraStatus(
            this.getStateDescription(),
            collectedElytraValue2,
            this.collectedElytraCount,
            this.shipsVisitedCount,
            this.countFireworkRockets(),
            this.minFireworkCountSetting.getValue(),
            equippedDamage,
            equippedDamage < 0 ? -1 : this.getChestMaxDamage(),
            collectedElytraValue
         );
      }
   }

   private void onElytraCollected() {
      this.collectedElytraCount++;
      this.notifyStatus(AutoElytraFinder.Kind.GOOD, "Elytra collected (" + this.collectedElytraCount + " this session)");
      this.markShipVisited();
      this.retrieveStartTick = -1;
      if (this.autoStoreElytra.isEnabled()) {
         this.beginStoreState();
      } else {
         this.enterState(AutoElytraFinder.State.EXIT);
      }
   }

   private boolean ensureAirborneOrReset() {
      if (this.isGliding()) {
         this.takeoffAttempts = 0;
         return true;
      } else if (mc.player.isOnGround()) {
         if (this.currentState == AutoElytraFinder.State.TO_SHIP && this.targetShip != null && this.registerLandingFailure()) {
            return false;
         } else {
            this.enterState(AutoElytraFinder.State.PREPARING);
            return false;
         }
      } else {
         if (mc.player.getVelocity().y < 0.0 && !this.hasLevitationEffect()) {
            this.pulseJumpToDeployElytra();
         }

         return false;
      }
   }

   private boolean isWithinHorizontalRange(BlockPos pos, double bottomCenterLimit) {
      return this.getHorizontalDistanceTo(Vec3d.ofBottomCenter(pos)) <= bottomCenterLimit;
   }

   private void selectFireworkSlot() {
      int selectedSlot = this.getFireworkSlot();
      if (selectedSlot >= 0 && mc.player.getInventory().getSelectedSlot() != selectedSlot) {
         mc.player.getInventory().setSelectedSlot(selectedSlot);
      }
   }

   private boolean isPillarBlock(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
         Block block = blockItem.getBlock();
         return block == Blocks.END_STONE
            || block == Blocks.END_STONE_BRICKS
            || block == Blocks.PURPUR_BLOCK
            || block == Blocks.PURPUR_PILLAR
            || block == Blocks.COBBLESTONE
            || block == Blocks.COBBLED_DEEPSLATE
            || block == Blocks.DIRT
            || block == Blocks.NETHERRACK
            || block == Blocks.STONE;
      } else {
         return false;
      }
   }

   private int countElytraInInventory() {
      PlayerInventory inventory = mc.player.getInventory();
      int inventoryStack = 0;

      for (int i = 0; i <= 35; i++) {
         if (inventory.getStack(i).isOf(Items.ELYTRA)) {
            inventoryStack++;
         }
      }

      return inventoryStack;
   }

   private void abandonShip(String shipCouldnText) {
      this.notifyStatus(AutoElytraFinder.Kind.INFO, Character.toUpperCase(shipCouldnText.charAt(0)) + shipCouldnText.substring(1));
      if (this.targetShip != null) {
         this.targetShip.unreachableCount++;
         if (this.targetShip.unreachableCount >= 5) {
            this.targetShip.unreachable = true;
         }
      }

      this.searchHeadingYaw = mc.player.getYaw() + 120.0F;
      this.resetTargetShip();
   }

   private void tickRetrieveState() {
      if (!this.isGliding()) {
         if (this.isContainerScreenOpen()) {
            mc.player.closeHandledScreen();
         } else if (this.getInventoryLoadEstimate() > this.elytraCountTarget) {
            this.onElytraCollected();
         } else {
            boolean hasNext = this.getNearestElytraItemEntity(24.0) != null;
            if ((this.tickCounter - this.retrieveStartTick <= 2400 || hasNext) && this.tickCounter - this.retrieveStartTick <= 4800) {
               if (this.fightNearbyEnderman()) {
                  this.retrieveStatusText = "Fighting an enderman";
               } else if (!this.isShulkerFighting()) {
                  if (!mc.player.isOnGround()) {
                     this.steerTowardAscendStep();
                  } else if (this.eatIfHungry()) {
                     this.retrieveStatusText = "Eating";
                  } else if (mc.player.getHealth() < 9.0F && mc.player.getHungerManager().getFoodLevel() >= 18) {
                     this.retrieveStatusText = "Healing up";
                     this.requestLookTarget(mc.player.getYaw(), 60.0F, 0);
                  } else {
                     ItemEntity next = this.getNearestElytraItemEntity(24.0);
                     if (next != null) {
                        this.retrieveStatusText = "Picking up the elytra";
                        if (!this.hasFreeInventorySlots(1) && !this.makeInventoryRoom()) {
                           this.pauseWithReason("inventory full - make room for the elytra");
                        } else {
                           this.walkToAndPickUpItem(next);
                        }
                     } else {
                        ItemFrameEntity expandEntities = this.shipLocator.findItemFrame(mc, this.targetShip);
                        if (expandEntities == null) {
                           if (this.frameMissingSinceTick < 0) {
                              this.frameMissingSinceTick = this.tickCounter;
                           }

                           if (this.tickCounter - this.frameMissingSinceTick > 50) {
                              this.skipShipWithReason("the elytra frame was empty");
                           }
                        } else {
                           this.frameMissingSinceTick = -1;
                           this.retrieveStatusText = "Taking the elytra";
                           Vec3d eyePos = this.getFrameAimPoint(expandEntities);
                           if (this.getEyeDistanceToBox(expandEntities) <= 2.9) {
                              this.lookAtPoint(eyePos, 1);
                              boolean elytraFrameFlag = !this.hasLineOfSight(eyePos);
                              if (!elytraFrameFlag) {
                                 if (!this.hasFreeInventorySlots(1) && !this.makeInventoryRoom()) {
                                    this.pauseWithReason("inventory full - make room for the elytra");
                                    return;
                                 }

                                 if (this.tickCounter - this.lastFrameHitTick > 25
                                    && this.isLookingTowardPoint(eyePos, 6.0F)
                                    && this.isEntityInReach(expandEntities)
                                    && this.tryAttackEntity(expandEntities)) {
                                    this.lastFrameHitTick = this.tickCounter;
                                 }

                                 return;
                              }

                              BlockPos posRaycast = this.getBlockHitTowards(eyePos);
                              if (posRaycast != null && this.isBreakableObstacle(posRaycast)) {
                                 this.breakBlockWithBestTool(posRaycast);
                                 return;
                              }

                              this.failedStandPositions.add(this.getFeetBlockPos().asLong());
                           }

                           this.retrieveStatusText = "Breaking into the ship";
                           BlockPos blockPos = expandEntities.getBlockPos();
                           int elytraFrameValue;
                           if (this.tickCounter < this.frameAnyUntilTick) {
                              elytraFrameValue = this.walkAndMineToward("frame-any", frame -> this.canReachItemFrameFrom(frame, expandEntities, false), blockPos);
                              if (elytraFrameValue < 0 && this.tickCounter - this.retrieveStartTick > 400) {
                                 this.abandonShip("couldn't find a way to the elytra");
                                 return;
                              }
                           } else {
                              elytraFrameValue = this.walkAndMineToward("frame", pos -> this.canReachItemFrameFrom(pos, expandEntities, true), blockPos);
                              if (elytraFrameValue < 0) {
                                 this.frameAnyUntilTick = this.tickCounter + 100;
                              }
                           }

                           if (elytraFrameValue == 1 && this.getEyeDistanceToBox(expandEntities) > 2.9) {
                              this.failedStandPositions.add(this.getFeetBlockPos().asLong());
                              this.breakPathSteps = null;
                           }
                        }
                     }
                  }
               }
            } else {
               this.abandonShip("couldn't get to the elytra in time");
            }
         }
      }
   }

   private void pillarUpward(int selectedSlot) {
      this.requestLookTarget(mc.player.getYaw(), 89.0F, 3);
      if (mc.player.getInventory().getSelectedSlot() != selectedSlot) {
         mc.player.getInventory().setSelectedSlot(selectedSlot);
      } else {
         int y = mc.player.getBlockPos().getY();
         if (this.tickCounter - this.lastPillarTick > 40) {
            this.pillarTopY = y;
            this.pillarJumpAttempts = 0;
         }

         this.lastPillarTick = this.tickCounter;
         if (y > this.pillarTopY) {
            this.pillarTopY = y;
            this.pillarJumpAttempts = 0;
         }

         if (mc.player.isOnGround()) {
            if (this.tickCounter - this.lastJumpTick >= 8 && !(mc.player.getPitch() < 80.0F)) {
               if (++this.pillarJumpAttempts > 6) {
                  this.pauseWithReason("pillaring isn't working here");
               } else {
                  mc.options.jumpKey.setPressed(true);
                  this.lastJumpTick = this.tickCounter;
               }
            }
         } else {
            BlockPos down = mc.player.getBlockPos().down();
            if (BlockOffsetUtils.canPlaceAt(down) && !mc.player.getBoundingBox().intersects(new Box(down))) {
               BlockHitResult missed = this.raycastLookDirection(4.5);
               if (missed.getType() == Type.BLOCK && missed.getBlockPos().offset(missed.getSide()).equals(down)) {
                  mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, missed);
                  mc.player.swingHand(Hand.MAIN_HAND);
               }
            }
         }
      }
   }

   private boolean isLandingSpotStillValid() {
      BlockPos down = BlockPos.ofFloored(this.landingSpot).down();
      return this.hasCollision(down)
         && !this.hasCollision(down.up())
         && !this.hasCollision(down.up(2))
         && mc.player.getY() > this.landingSpot.y - 1.0;
   }

   private boolean isFreeStandingSpot(BlockPos pos2) {
      if (!BlockOffsetUtils.canPlaceAt(pos2) || !this.getBlockState(pos2).isAir()) {
         return false;
      } else if (!this.hasCollision(pos2.down()) || this.getBlockState(pos2.down()).hasBlockEntity()) {
         return false;
      } else {
         return !this.hasCollision(pos2.up()) && !this.getBlockState(pos2.up()).hasBlockEntity()
            ? mc.world.getOtherEntities(mc.player, new Box(pos2).expand(0.1)).isEmpty()
            : false;
      }
   }

   private Vec3d findVisibleBlockFacePoint(BlockPos pos2) {
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d add = null;
      double autoElytraValue = Double.MAX_VALUE;

      for (Direction direction : Direction.values()) {
         Vec3d add2 = Vec3d.ofCenter(pos2).add(direction.getOffsetX() * 0.49, direction.getOffsetY() * 0.49, direction.getOffsetZ() * 0.49);
         Vec3d subtract = eyePos.subtract(add2);
         if (!(subtract.x * direction.getOffsetX() + subtract.y * direction.getOffsetY() + subtract.z * direction.getOffsetZ() <= 0.0)) {
            double squaredDistanceTo = eyePos.squaredDistanceTo(add2);
            if (!(squaredDistanceTo > 16.0)
               && !(squaredDistanceTo >= autoElytraValue)
               && !(
                  mc.world.raycast(new RaycastContext(eyePos, add2, ShapeType.OUTLINE, FluidHandling.NONE, mc.player)) instanceof BlockHitResult blockHitResult
                     && blockHitResult.getType() == Type.BLOCK
                     && !blockHitResult.getBlockPos().equals(pos2)
               )) {
               add = add2;
               autoElytraValue = squaredDistanceTo;
            }
         }
      }

      return add;
   }

   private void notifyStatus(AutoElytraFinder.Kind kind, String collectedElytraText) {
      if (kind != AutoElytraFinder.Kind.INFO) {
         if (!collectedElytraText.equals(this.lastNotificationText) || this.tickCounter - this.lastNotificationTick >= 600) {
            this.lastNotificationText = collectedElytraText;
            this.lastNotificationTick = this.tickCounter;
            String formatText = this.notificationModeSetting.getCurrentOption();
            if (!formatText.equals("Off") || kind == AutoElytraFinder.Kind.STOP) {
               if (formatText.equals("Chat")) {
                  if (mc.player != null) {
                     mc.player.sendMessage(Text.literal("\u00a77[Elytra Finder]\u00a7r " + collectedElytraText), false);
                  }
               } else {
                  DisabledEnabled.getInstance().handleDisabledEnabled("Elytra Finder", collectedElytraText, kind == AutoElytraFinder.Kind.GOOD);
               }
            }
         }
      }
   }

   private Vec3d findLandingSpotAtOffset(Ship ship, int[] bottomCenterValues) {
      BlockPos offsetYclockwise = ship.getRelativePos(bottomCenterValues[0], 8, bottomCenterValues[1]);
      return this.isStandableSpot(offsetYclockwise) && this.isColumnAboveClear(offsetYclockwise, 3) ? Vec3d.ofBottomCenter(offsetYclockwise) : null;
   }

   private boolean hasOpenNeighborColumn(BlockPos pos) {
      for (Direction direction : net.minecraft.util.math.Direction.Type.HORIZONTAL) {
         BlockPos offset = pos.offset(direction);
         if (!this.hasCollision(offset) && !this.hasCollision(offset.up()) && this.isColumnBelowEmpty(offset, 12)) {
            return true;
         }
      }

      return false;
   }

   private int getFireworkSlot() {
      return SelectedInventoryUtils.getInstanceInventory(this::isPlainFirework);
   }

   private boolean canMendWithXp() {
      return this.autoMending.isEnabled() && this.isChestElytraMending() && this.countInventoryItem(Items.EXPERIENCE_BOTTLE) > 0;
   }

   private int findStorableShulkerSlot() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i <= 35; i++) {
         if (this.isShulkerFilledOnlyWithElytra(inventory.getStack(i))) {
            return i;
         }
      }

      return -1;
   }

   private void beginRecoverChest() {
      this.enderChestCountBefore = -1;
      this.pickupStartTick = -1;
      this.droppedItemWaitStartTick = -1;
      this.enterState(AutoElytraFinder.State.RECOVER_CHEST);
   }

   private int getTicksInState() {
      return this.tickCounter - this.stateStartTick;
   }

   private void steerFlight(float showingCardValue, double showingCardBound, boolean showingCardFlag, double showingCardLimit) {
      if (!this.hasFireworkRocket() && this.countFireworkRockets() > 0) {
         this.swapMatchingItemToHotbar(this::isPlainFirework);
      }

      if (showingCardLimit > 0.0 && showingCardLimit < 3.0) {
         showingCardValue = mc.player.getYaw();
      }

      if (mc.player.getY() < showingCardBound - 6.0 && (this.getHorizontalSpeed() < 0.6 || mc.player.getY() < 40.0)) {
         showingCardFlag = true;
      }

      double hypot = this.getHorizontalSpeed();
      double offsetUnionBound = Math.max(18.0, hypot * 16.0);
      double floorEntity = this.getTerrainHeightAhead(showingCardValue, showingCardLimit > 0.0 ? Math.min(showingCardLimit, offsetUnionBound * 3.0) : offsetUnionBound * 3.0);
      double maxTargetDist = Math.max(showingCardBound, floorEntity + 12.0);
      double showingCardLimit2 = mc.player.getY() - maxTargetDist;
      boolean showingCard = this.isFireworkBoosting();
      float showingCardValue3;
      if (showingCardLimit2 < -2.5) {
         showingCardValue3 = showingCard ? (showingCardLimit2 < -14.0 ? -42.0F : -28.0F) : (hypot > 1.4 ? -14.0F : 2.0F);
      } else if (showingCardLimit2 > 10.0) {
         showingCardValue3 = 22.0F;
      } else if (showingCardLimit2 > 3.0) {
         showingCardValue3 = 9.0F;
      } else {
         showingCardValue3 = (float)MathHelper.clamp(showingCardLimit2 * 1.4, -6.0, 6.0);
      }

      float degrees = showingCardValue;
      Vec3d velocity = mc.player.getVelocity();
      double varLength = velocity.length();
      boolean showingCardFlag2 = hypot > 0.3 && this.isHorizontalPathBlocked(new Vec3d(velocity.x, 0.0, velocity.z), Math.max(8.0, hypot * 10.0))
         || varLength > 0.5 && this.isPathBlockedAlong(velocity, Math.max(8.0, varLength * 12.0));
      if (showingCardFlag2 && this.tickCounter >= this.yawLockUntilTick) {
         Float amount = this.findClearYawOffset((float)Math.toDegrees(Math.atan2(-velocity.x, velocity.z)), offsetUnionBound);
         if (amount != null) {
            this.lockedYaw = amount;
            this.yawLockUntilTick = this.tickCounter + 20;
         }

         showingCardValue3 = this.getClearPitchOffset(amount != null ? amount : mc.player.getYaw(), showingCard, offsetUnionBound);
      }

      if (this.tickCounter < this.yawLockUntilTick) {
         degrees = this.lockedYaw;
      } else if (this.isLineFromEyeBlocked(this.getLookVector(showingCardValue, showingCardValue3), offsetUnionBound) || this.isLineFromEyeBlocked(this.getLookVector(showingCardValue, 0.0F), offsetUnionBound * 0.8)) {
         Float clearYawOffset = this.findClearYawOffset(showingCardValue, offsetUnionBound);
         if (clearYawOffset != null && Math.abs(MathHelper.wrapDegrees(clearYawOffset - showingCardValue)) > 1.0F) {
            this.lockedYaw = clearYawOffset;
            this.yawLockUntilTick = this.tickCounter + 25;
            degrees = clearYawOffset;
         } else if (showingCard) {
            showingCardValue3 = -40.0F;
         }
      }

      if (this.tickCounter < this.yawLockUntilTick && showingCard && this.isPathBlockedAlong(this.getLookVector(degrees, showingCardValue3), offsetUnionBound * 0.6)) {
         showingCardValue3 = this.getClearPitchOffset(degrees, true, offsetUnionBound);
      }

      this.requestLookTarget(degrees, showingCardValue3, 0);
      boolean chunkFloor = this.isChunkLoadedAhead(degrees, 40.0);
      if (!chunkFloor && mc.player.getY() > 60.0) {
         showingCardFlag = false;
      }

      if (showingCardFlag && !showingCard && this.canUseFirework()) {
         boolean showingCardFlag3 = hypot < 1.15 || showingCardLimit2 < -6.0;
         boolean showingCardFlag4 = hypot < 0.5 && mc.player.getVelocity().y < -0.05;
         if (showingCardFlag3 && (showingCardFlag4 || Math.abs(MathHelper.wrapDegrees(degrees - mc.player.getYaw())) < 25.0F)) {
            this.useFireworkRocket();
         }
      }
   }

   private boolean swapMatchingItemToHotbar(Predicate<ItemStack> predicate) {
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
               ExtUtils.handleExt(
                  () -> {
                     if (mc.player != null && mc.player.currentScreenHandler == mc.player.playerScreenHandler) {
                        if (predicate.test(mc.player.getInventory().getStack(playerValue))) {
                           mc.interactionManager
                              .clickSlot(
                                 mc.player.playerScreenHandler.syncId,
                                 HotbarSlotUtils.toScreenSlot(playerValue),
                                 HotbarSlotUtils.requireHotbarSlot(this.getFreeHotbarSlot()),
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

   private void interactWithPlacedBlock(BlockPos pos2) {
      if (pos2 != null) {
         BlockPos offset = this.getShulkerOpeningPos(pos2);
         if (this.hasCollision(offset) && this.isBreakableObstacle(offset)) {
            this.breakBlockWithBestTool(offset);
         } else {
            Vec3d add = this.findVisibleBlockFacePoint(pos2);
            if (add == null) {
               this.walkNearBlock(pos2);
            } else {
               this.lookAtPoint(add, 1);
               if (!mc.player.isSneaking() && this.isLookingTowardPoint(add, 6.0F) && this.tickCounter - this.lastInteractTick >= 6) {
                  BlockHitResult missed = this.raycastLookDirection(4.0);
                  if (missed.getType() == Type.BLOCK && missed.getBlockPos().equals(pos2)) {
                     mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, missed);
                     mc.player.swingHand(Hand.MAIN_HAND);
                     this.lastInteractTick = this.tickCounter;
                  }
               }
            }
         }
      }
   }

   private boolean isColumnBelowEmpty(BlockPos pos2, int downLimit) {
      for (int i = 1; i <= downLimit; i++) {
         if (this.hasCollision(pos2.down(i))) {
            return false;
         }
      }

      return true;
   }

   private void enterState(AutoElytraFinder.State state) {
      if (state != this.currentState) {
         this.breakPathSteps = null;
      }

      if (state == AutoElytraFinder.State.MENDING && this.currentState != AutoElytraFinder.State.MENDING) {
         this.peakElytraDamage = Math.max(0, this.getEquippedElytraDurability());
         this.repairProgressTick = this.tickCounter;
      }

      if (state == AutoElytraFinder.State.LAND && this.currentState != AutoElytraFinder.State.LAND) {
         this.landingSpot = null;
      }

      if (state == AutoElytraFinder.State.LANDING && this.currentState != AutoElytraFinder.State.LANDING) {
         this.plannedYaw = null;
         this.landingApproachTicks = 0;
         this.landActive = false;
      }

      if (state == AutoElytraFinder.State.LAND && this.currentState != AutoElytraFinder.State.LAND) {
         this.plannedYaw = null;
         this.landActive = false;
      }

      boolean landFlag = state == AutoElytraFinder.State.CRUISE
         || state == AutoElytraFinder.State.TO_CITY
         || state == AutoElytraFinder.State.TO_SHIP
         || state == AutoElytraFinder.State.LANDING
         || state == AutoElytraFinder.State.LAND
         || state == AutoElytraFinder.State.TAKEOFF;
      if (landFlag && (this.currentState == AutoElytraFinder.State.STORE || this.currentState == AutoElytraFinder.State.BUY) && this.pendingPurchase != AutoElytraFinder.Buy.NONE) {
         this.confirmScreenHelper.updateHandledSuccess();
         this.pendingPurchase = AutoElytraFinder.Buy.NONE;
      }

      this.currentState = state;
      this.stateStartTick = this.tickCounter;
      if (state == AutoElytraFinder.State.TAKEOFF) {
         this.takeoffAttempts = 0;
      }
   }

   private boolean isPlaceableEndBlock(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
         Block block = blockItem.getBlock();
         if (block != Blocks.END_STONE
            && block != Blocks.END_STONE_BRICKS
            && block != Blocks.PURPUR_BLOCK
            && block != Blocks.PURPUR_PILLAR
            && block != Blocks.PURPUR_STAIRS
            && block != Blocks.PURPUR_SLAB
            && block != Blocks.END_ROD
            && block != Blocks.MAGENTA_STAINED_GLASS) {
            return false;
         } else {
            int purpurEndCounter = 0;
            PlayerInventory inventory = mc.player.getInventory();

            for (int i = 0; i <= 35; i++) {
               if (this.isPillarBlock(inventory.getStack(i))) {
                  purpurEndCounter++;
               }
            }

            return purpurEndCounter > 1 || !this.isPillarBlock(stack);
         }
      } else {
         return false;
      }
   }

   private boolean isFireworkBoosting() {
      if (this.tickCounter < this.fireworkActiveUntilTick - 9) {
         return true;
      } else {
         return this.tickCounter - this.lastFireworkUseTick > 80
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

   private BlockHitResult raycastLookDirection(double missedValue) {
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d add = eyePos.add(MathClampUtils.getCosSin(mc.player.getYaw(), mc.player.getPitch()).multiply(missedValue));
      return mc.world.raycast(new RaycastContext(eyePos, add, ShapeType.OUTLINE, FluidHandling.NONE, mc.player)) instanceof BlockHitResult blockHitResult
         ? blockHitResult
         : BlockHitResult.createMissed(add, Direction.UP, BlockPos.ofFloored(add));
   }

   private void pulseJumpToDeployElytra() {
      if (!this.hasLevitationEffect() && this.hasUsableElytraEquipped() && !mc.player.isOnGround()) {
         mc.options.jumpKey.setPressed((this.tickCounter & 1) == 0);
      }
   }

   private void resetTargetShip() {
      boolean shipStoredFlag = !this.isGliding();
      this.targetShip = null;
      this.breakingTargetShip = null;
      this.retrieveStartTick = -1;
      this.landingSpot = null;
      this.landingApproachPoint = null;
      this.breakPathSteps = null;
      this.returnForStoredElytra = false;
      this.enterState(shipStoredFlag ? AutoElytraFinder.State.EXIT : AutoElytraFinder.State.CRUISE);
   }

   private int findContainerSlot(Predicate<ItemStack> predicate) {
      ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
      if (currentScreenHandler == null) {
         return -1;
      } else {
         int slotStackLimit = currentScreenHandler.slots.size() - 36;

         for (int i = 0; i < slotStackLimit; i++) {
            if (predicate.test(currentScreenHandler.getSlot(i).getStack())) {
               return i;
            }
         }

         return -1;
      }
   }

   private BlockPos getFeetBlockPos() {
      return BlockPos.ofFloored(mc.player.getX(), mc.player.getY() + 0.6, mc.player.getZ());
   }

   private void pauseWithReason(String rocketRocketsText) {
      if (this.currentState != AutoElytraFinder.State.PAUSED) {
         this.stateBeforePause = this.currentState;
      }

      if (this.currentState != AutoElytraFinder.State.PAUSED || !rocketRocketsText.equals(this.pauseReason)) {
         this.notifyStatus(AutoElytraFinder.Kind.STOP, "Paused - " + rocketRocketsText);
      }

      this.pauseReason = rocketRocketsText;
      this.cancelBlockBreaking();
      this.releaseMovementKeys();
      if (this.isContainerScreenOpen() && !this.confirmScreenHelper.isRunningSuccess()) {
         mc.player.closeHandledScreen();
      }

      this.enterState(AutoElytraFinder.State.PAUSED);
   }

   private boolean isPathBlockedAlong(Vec3d vec, double offsetUnionBound) {
      if (!(vec.lengthSquared() < 1.0E-6) && !(offsetUnionBound <= 0.0)) {
         vec = vec.normalize();
         Vec3d entityPos = mc.player.getEntityPos();
         double offsetUnionValue = this.isGliding() ? 0.6 : 1.8;
         Box box = new Box(entityPos.x - 0.3, entityPos.y, entityPos.z - 0.3, entityPos.x + 0.3, entityPos.y + offsetUnionValue, entityPos.z + 0.3);
         Vec3d vec3d = Vec3d.ZERO;
         double offsetUnionCounter = 1.0;

         while (true) {
            Vec3d multiply = vec.multiply(Math.min(offsetUnionCounter, offsetUnionBound));
            if (!mc.world.isBlockSpaceEmpty(mc.player, box.offset(vec3d).union(box.offset(multiply)))) {
               return true;
            }

            if (offsetUnionCounter >= offsetUnionBound) {
               return false;
            }

            vec3d = multiply;
            offsetUnionCounter++;
         }
      } else {
         return false;
      }
   }

   private boolean findNewShipCandidate() {
      this.scanForShipBlocks();
      if (this.nearestShipBlockCenter != null && !this.isNearVisitedCity(this.nearestShipBlockCenter)) {
         this.cityTargetVec = this.nearestShipBlockCenter;
         this.nearestShipBlockCenter = null;
         this.nearestShipBlockDistance = Double.MAX_VALUE;
         return true;
      } else {
         return false;
      }
   }

   private float computeBreakCost(BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      float maxHardness = Math.max(0.0F, blockState.getHardness(mc.world, pos));
      float hardnessMultiplierBound = 1.0F;
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i < 9; i++) {
         hardnessMultiplierBound = Math.max(hardnessMultiplierBound, inventory.getStack(i).getMiningSpeedMultiplier(blockState));
      }

      return 1.0F + maxHardness * 30.0F / hardnessMultiplierBound / 5.0F;
   }

   private boolean isLookingTowardPoint(Vec3d vec, float degrees) {
      Vec3d subtract = vec.subtract(mc.player.getEyePos());
      double varLength = subtract.length();
      if (varLength < 1.0E-4) {
         return true;
      } else {
         Vec3d cosSin = MathClampUtils.getCosSin(mc.player.getYaw(), mc.player.getPitch());
         return cosSin.dotProduct(subtract) / varLength >= Math.cos(Math.toRadians(degrees));
      }
   }

   private boolean replaceBrokenElytraMidFlight() {
      int equippedDamage = this.getEquippedElytraDurability();
      return this.isGliding() && equippedDamage >= 0 && equippedDamage <= 3 && this.equipFreshHotbarElytra();
   }

   private boolean hasUsableElytra() {
      if (this.hasUsableElytraEquipped()) {
         return true;
      } else {
         PlayerInventory inventory = mc.player.getInventory();

         for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(Items.ELYTRA) && this.hasDurabilityLeft(stack)) {
               return true;
            }
         }

         return false;
      }
   }

   private double getEyeDistanceToBox(Entity entity) {
      Vec3d eyePos = mc.player.getEyePos();
      Box boundingBox = entity.getBoundingBox();
      return eyePos.distanceTo(
         new Vec3d(
            MathHelper.clamp(eyePos.x, boundingBox.minX, boundingBox.maxX), MathHelper.clamp(eyePos.y, boundingBox.minY, boundingBox.maxY), MathHelper.clamp(eyePos.z, boundingBox.minZ, boundingBox.maxZ)
         )
      );
   }

   private Vec3d getFrameAimPoint(ItemFrameEntity itemFrameEntity) {
      return this.getItemFrameAimPoint(itemFrameEntity, mc.player.getEyePos());
   }

   private double getHorizontalDistanceTo(Vec3d vec) {
      return Math.hypot(vec.x - mc.player.getX(), vec.z - mc.player.getZ());
   }

   private boolean tryContinueOnShip() {
      if (this.targetShip == null || this.isGliding() || !mc.player.isOnGround() || !this.isNearTargetShip()) {
         return false;
      } else if (this.returnForStoredElytra) {
         this.returnForStoredElytra = false;
         if (this.placedShulkerPos != null && this.isShulkerBox(this.getBlockState(this.placedShulkerPos).getBlock())) {
            this.enterState(AutoElytraFinder.State.STORE);
            this.setStoreStep(6);
            return true;
         } else if (this.enderChestPos != null && this.getBlockState(this.enderChestPos).isOf(Blocks.ENDER_CHEST)) {
            this.beginRecoverChest();
            return true;
         } else {
            this.enterState(AutoElytraFinder.State.EXIT);
            return true;
         }
      } else if (this.targetShip.unreachable) {
         return false;
      } else {
         this.beginShipLooting();
         return true;
      }
   }

   private int getChestMaxDamage() {
      int maxDamage = mc.player.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage();
      return maxDamage > 0 ? maxDamage : 432;
   }

   private boolean hasShulkerWithElytra() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (this.isShulkerBoxOfOnlyElytra(stack)) {
            return true;
         }
      }

      return false;
   }

   private boolean isContainerScreenOpen() {
      return mc.player != null
         && mc.currentScreen instanceof HandledScreen
         && !(mc.currentScreen instanceof InventoryScreen)
         && mc.player.currentScreenHandler != null
         && mc.player.currentScreenHandler != mc.player.playerScreenHandler;
   }

   private void tickPreparingState() {
      if ((this.pendingShulkerPos != null || this.pendingEnderChestPos != null) && mc.player.isOnGround() && !this.isGliding()) {
         this.restoreStorageAfterLanding(this.pendingShulkerPos, this.pendingEnderChestPos);
         if (this.currentState != AutoElytraFinder.State.PREPARING) {
            return;
         }
      }

      if (!this.hasUsableElytraEquipped()) {
         int instanceInventory = SelectedInventoryUtils.getInstanceInventory(stack -> stack.isOf(Items.ELYTRA) && this.hasDurabilityLeft(stack));
         if (instanceInventory >= 0) {
            if (mc.player.getInventory().getSelectedSlot() != instanceInventory) {
               mc.player.getInventory().setSelectedSlot(instanceInventory);
            } else {
               if (this.getTicksInState() % 6 == 3) {
                  mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               }
            }
         } else if (!this.swapMatchingItemToHotbar(stack2 -> stack2.isOf(Items.ELYTRA) && this.hasDurabilityLeft(stack2)) && !ExtUtils.isAction()) {
            this.pauseWithReason("no usable elytra");
         }
      } else if (!this.hasFireworkRocket()) {
         if (this.countFireworkRockets() > 0) {
            this.swapMatchingItemToHotbar(this::isPlainFirework);
            if (this.getTicksInState() < 100) {
               return;
            }
         }

         if (mc.player.isOnGround()) {
            this.job = AutoElytraFinder.Job.BUY_ROCKETS;
            this.enterState(AutoElytraFinder.State.BUY);
         } else {
            this.pauseWithReason("no firework rockets");
         }
      } else if (mc.player.isOnGround() && this.countFireworkRockets() < this.getMinimumFireworkCount() && this.tickCounter >= this.buyRetryNotBeforeTick) {
         this.job = AutoElytraFinder.Job.BUY_ROCKETS;
         this.enterState(AutoElytraFinder.State.BUY);
      } else if (this.isGliding()) {
         this.enterState(AutoElytraFinder.State.CRUISE);
      } else if (!mc.player.isOnGround()) {
         this.pulseJumpToDeployElytra();
      } else {
         this.enterState(AutoElytraFinder.State.TAKEOFF);
      }
   }

   public AutoElytraFinder() {
      super("Auto Elytra Finder", IconsPngType.DONUTSMP);
      this.autoMending = new BooleanSetting(
         "Auto Mending", "When the elytra gets low and has Mending, land and throw XP bottles until it is repaired, then keep going.", true
      );
      this.combatShulkers = new BooleanSetting("Combat Shulkers", "Kill the shulker that guards the elytra room and hit away shulker bullets.", true);
      this.autoStoreElytra = new BooleanSetting(
         "Auto Store Elytra",
         "Store every elytra in one shulker kept inside your ender chest (buys them once if needed, needs a Silk Touch pickaxe). Off = just carry them.",
         true
      );
      this.flyCollectedElytraSetting = new BooleanSetting("Fly Collected Elytra", "Keep one collected elytra out of storage and wear it when yours wears out.", false);
      this.notificationModeSetting = new EnumSetting("Notifications", "How the bot tells you about finds and problems.", "Toast", "Toast", "Chat", "Off");
      this.statusCard = new BooleanSetting("Status Card", "Small card at the top of the screen showing what the bot is doing.", true);
      this.currentState = AutoElytraFinder.State.PREPARING;
      this.pauseReason = "";
      this.job = AutoElytraFinder.Job.NONE;
      this.fireworkActiveUntilTick = -1000;
      this.lastFireworkUseTick = -1000;
      this.lastJumpTick = -1000;
      this.takeoffStartY = Integer.MIN_VALUE;
      this.yawLockUntilTick = -1000;
      this.shipLocator = new ShipLocator();
      this.knownStructurePositions = new ArrayList<>();
      this.cityWaitStartTick = -1;
      this.nearestShipBlockDistance = Double.MAX_VALUE;
      this.useOuterApproachPoint = true;
      this.healWaitStartTick = -1;
      this.retrieveStartTick = -1;
      this.startElytraCount = -1;
      this.excludedPlacePositions = new HashSet<>();
      this.lastFrameHitTick = -1000;
      this.frameMissingSinceTick = -1;
      this.frameAnyUntilTick = -1000;
      this.guardFightTick = -1000;
      this.shulkerFightTick = -1000;
      this.retrieveStatusText = "Breaking into the ship";
      this.failedStandPositions = new LongOpenHashSet();
      this.breakAttemptTick = -1000;
      this.lastBreakPathTick = -1000;
      this.breakPathKey = "";
      this.breakPathExtraSolids = new LongOpenHashSet();
      this.lastSeenStoreStep = -1;
      this.syncId = -1;
      this.lastInteractTick = -1000;
      this.enderChestCountBefore = -1;
      this.pickupStartTick = -1;
      this.placedAtTick = -1;
      this.shulkerCountAfterPickup = -1;
      this.droppedItemWaitStartTick = -1;
      this.endermanCooldownUntilTick = -1;
      this.stateBeforePause = AutoElytraFinder.State.PREPARING;
      this.storingElytraText = "";
      this.storeStatusText = "Storing the elytra";
      this.lastXpThrowTick = -1000;
      this.eatingUntilTick = -1;
      this.pillarTopY = Integer.MIN_VALUE;
      this.lastPillarTick = -1000;
      this.confirmScreenHelper = new ConfirmScreenHelper();
      this.pendingPurchase = AutoElytraFinder.Buy.NONE;
      this.lastNotificationText = "";
      this.lastNotificationTick = -10000;
      this.elytraStatusCard = new ElytraStatusCard();
      this.elytraStatus = ElytraStatus.INSTANCE;
      this.lastAimUpdateTick = -1000;
      this.breakPathGrid = new Grid() {
         @Override
         public boolean isSolidBlock(int x2, int y2, int z2) {
            return AutoElytraFinder.this.breakPathExtraSolids.contains(BlockPos.asLong(x2, y2, z2)) || AutoElytraFinder.this.hasCollision(new BlockPos(x2, y2, z2));
         }

         @Override
         public boolean isBreakableBlock(int x3, int y3, int z3) {
            return !AutoElytraFinder.this.breakPathExtraSolids.contains(BlockPos.asLong(x3, y3, z3)) && AutoElytraFinder.this.isBreakableObstacle(new BlockPos(x3, y3, z3));
         }

         @Override
         public float getBlockBreakCost(int x4, int y4, int z4) {
            return AutoElytraFinder.this.computeBreakCost(new BlockPos(x4, y4, z4));
         }
      };
      this.addSettings(
         new ConfigSetting[]{
            this.flightAltitudeSetting,
            this.minFireworkCountSetting,
            this.minimumElytraDurability,
            this.autoMending,
            this.combatShulkers,
            this.autoStoreElytra,
            this.flyCollectedElytraSetting,
            this.notificationModeSetting,
            this.statusCard
         }
      );
   }

   private boolean hasCollision(BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      return !blockState.isAir() && !blockState.getCollisionShape(mc.world, pos).isEmpty();
   }

   private void restoreStorageAfterLanding(BlockPos blockPos, BlockPos pos) {
      if (mc.player != null) {
         if (!this.isGliding() && mc.player.isOnGround()) {
            this.pendingShulkerPos = null;
            this.pendingEnderChestPos = null;
            boolean blockPosFlag = pos != null && this.getBlockState(pos).isOf(Blocks.ENDER_CHEST) && pos.isWithinDistance(mc.player.getBlockPos(), 10.0);
            if (blockPos != null && this.isShulkerBox(this.getBlockState(blockPos).getBlock()) && blockPos.isWithinDistance(mc.player.getBlockPos(), 10.0)) {
               this.placedShulkerPos = blockPos;
               if (blockPosFlag) {
                  this.enderChestPos = pos;
               }

               this.enterState(AutoElytraFinder.State.STORE);
               this.setStoreStep(6);
            } else if (blockPosFlag) {
               this.enderChestPos = pos;
               this.beginRecoverChest();
            }
         } else {
            this.pendingShulkerPos = blockPos;
            this.pendingEnderChestPos = pos;
         }
      }
   }

   private boolean isInEnd() {
      return mc.world != null && mc.world.getRegistryKey() == World.END;
   }

   private BlockPos getShulkerOpeningPos(BlockPos pos2) {
      BlockState blockState = this.getBlockState(pos2);
      return blockState.getBlock() instanceof ShulkerBoxBlock && blockState.contains(ShulkerBoxBlock.FACING) ? pos2.offset(blockState.get(ShulkerBoxBlock.FACING)) : pos2.up();
   }

   private boolean hasUsableElytraEquipped() {
      ItemStack equippedStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
      return equippedStack.isOf(Items.ELYTRA) && this.hasDurabilityLeft(equippedStack);
   }

   private void abortStoreAndExit() {
      if (this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      }

      if (!this.confirmScreenHelper.isRunningSuccess()) {
         this.confirmScreenHelper.updateHandledSuccess();
         this.pendingPurchase = AutoElytraFinder.Buy.NONE;
      }

      if (this.placedShulkerPos != null && this.isShulkerBox(this.getBlockState(this.placedShulkerPos).getBlock())) {
         this.setStoreStep(6);
      } else if (this.enderChestPos != null && this.getBlockState(this.enderChestPos).isOf(Blocks.ENDER_CHEST)) {
         this.beginRecoverChest();
      } else {
         this.enterState(AutoElytraFinder.State.EXIT);
      }
   }

   private boolean swapItemToHotbar(Item item2) {
      return this.swapMatchingItemToHotbar(itemx -> itemx.isOf(item2));
   }

   private void pressMovementKeysToward(Vec3d vec) {
      double deltaX = vec.x - mc.player.getX();
      double deltaZ = vec.z - mc.player.getZ();
      if (!(deltaX * deltaX + deltaZ * deltaZ < 0.02)) {
         double angle = Math.toRadians(mc.player.getYaw());
         double sin = Math.sin(angle);
         double cos = Math.cos(angle);
         double pressedOptionsLimit = -deltaX * sin + deltaZ * cos;
         double pressedOptionsLimit2 = deltaX * cos + deltaZ * sin;
         if (pressedOptionsLimit > 0.08) {
            mc.options.forwardKey.setPressed(true);
         } else if (pressedOptionsLimit < -0.08) {
            mc.options.backKey.setPressed(true);
         }

         if (pressedOptionsLimit2 > 0.08) {
            mc.options.leftKey.setPressed(true);
         } else if (pressedOptionsLimit2 < -0.08) {
            mc.options.rightKey.setPressed(true);
         }
      }
   }

   private boolean isWearingElytra() {
      return mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
   }

   private BlockPos getBlockHitTowards(Vec3d vec) {
      Vec3d eyePos = mc.player.getEyePos();
      return mc.world.raycast(new RaycastContext(eyePos, vec, ShapeType.OUTLINE, FluidHandling.NONE, mc.player)) instanceof BlockHitResult blockHitResult
            && blockHitResult.getType() == Type.BLOCK
         ? blockHitResult.getBlockPos()
         : null;
   }

   private int countContainerItems(ItemStack stack) {
      ContainerComponent containerComponent = stack.get(DataComponentTypes.CONTAINER);
      if (containerComponent == null) {
         return 0;
      } else {
         int iterateNon = 0;

         for (ItemStack itemStack : containerComponent.iterateNonEmpty()) {
            iterateNon++;
         }

         return iterateNon;
      }
   }

   private void tickPausedState() {
      this.cancelBlockBreaking();
      if (this.isGliding()) {
         this.job = AutoElytraFinder.Job.STOP;
         this.enterState(AutoElytraFinder.State.LAND);
      } else if (mc.player.isOnGround()) {
         if (this.pauseReason.startsWith("inventory full") && this.hasFreeInventorySlots(1)) {
            if (this.pauseReason.contains("rocket")) {
               this.job = AutoElytraFinder.Job.NONE;
               this.enterState(AutoElytraFinder.State.BUY);
               return;
            }

            if (this.stateBeforePause == AutoElytraFinder.State.STORE && this.placedShulkerPos != null) {
               this.enterState(AutoElytraFinder.State.STORE);
               this.setStoreStep(this.storeStep);
               return;
            }

            if (this.stateBeforePause == AutoElytraFinder.State.RETRIEVE && this.targetShip != null) {
               this.retrieveStartTick = -1;
               this.beginShipLooting();
               return;
            }
         }

         if (this.stateBeforePause == AutoElytraFinder.State.STORE
            && this.storeStep == 6
            && this.pauseReason.contains("shulker")
            && this.shulkerCountAfterPickup >= 0
            && this.countShulkerBoxItems() > this.shulkerCountAfterPickup
            && this.hasShulkerWithElytra()
            && this.enderChestPos != null
            && this.getBlockState(this.enderChestPos).isOf(Blocks.ENDER_CHEST)) {
            this.enterState(AutoElytraFinder.State.STORE);
            this.setStoreStep(7);
         } else if (this.countFireworkRockets() == 0 && this.tickCounter >= this.buyRetryNotBeforeTick && this.pauseReason.contains("rocket")) {
            this.job = AutoElytraFinder.Job.NONE;
            this.enterState(AutoElytraFinder.State.BUY);
         } else if (this.pauseReason.startsWith("no firework") && this.hasFireworkRocket()) {
            this.enterState(AutoElytraFinder.State.PREPARING);
         } else {
            if (this.pauseReason.startsWith("couldn't buy rockets") && this.tickCounter >= this.buyRetryNotBeforeTick) {
               this.job = AutoElytraFinder.Job.NONE;
               this.enterState(AutoElytraFinder.State.BUY);
            }
         }
      }
   }

   private boolean registerLandingFailure() {
      this.targetShip.unreachableCount++;
      if (this.targetShip.unreachableCount >= 12 || this.targetShip.unreachableCount >= 6 && !this.returnForStoredElytra) {
         if (this.returnForStoredElytra) {
            this.notifyStatus(AutoElytraFinder.Kind.WARN, "Couldn't get back to the stored elytra - left it on the ship");
         }

         this.abandonShip("couldn't land on the ship");
         return true;
      } else {
         return false;
      }
   }

   private double getTerrainHeightAhead(float floorEntityValue, double floorEntityLimit) {
      Vec3d entityPos = mc.player.getEntityPos();
      double negativeInfinity = Double.NEGATIVE_INFINITY;

      for (float floorEntityValue3 : new float[]{0.0F, 14.0F, -14.0F}) {
         Vec3d wrapDegrees = this.getLookVector(floorEntityValue + floorEntityValue3, 0.0F);

         for (double floorEntityTotal = 0.0; floorEntityTotal <= floorEntityLimit; floorEntityTotal += 6.0) {
            int chunkWorld = this.getSurfaceHeight(MathHelper.floor(entityPos.x + wrapDegrees.x * floorEntityTotal), MathHelper.floor(entityPos.z + wrapDegrees.z * floorEntityTotal));
            if (chunkWorld != Integer.MIN_VALUE && chunkWorld > negativeInfinity) {
               negativeInfinity = chunkWorld;
            }
         }
      }

      return negativeInfinity == Double.NEGATIVE_INFINITY ? 0.0 : negativeInfinity;
   }

   private boolean isTargetShipLooted() {
      if (this.targetShip == null) {
         return false;
      } else {
         if (this.getHorizontalDistanceTo(Vec3d.ofCenter(this.targetShip.blockPos)) < 26.0) {
            this.shipLocator.markLooted(mc, this.targetShip);
         }

         if (this.targetShip.loot != Loot.EMPTY && (this.targetShip.lootedCount <= 60 || !(this.getHorizontalDistanceTo(Vec3d.ofCenter(this.targetShip.blockPos)) < 20.0))) {
            return false;
         } else {
            this.skipShipWithReason("this ship was already looted");
            return true;
         }
      }
   }

   private int findPillarBlockSlot() {
      int instanceInventory = SelectedInventoryUtils.getInstanceInventory(this::isPillarBlock);
      if (instanceInventory >= 0) {
         return instanceInventory;
      } else if (this.hasInventoryItemMatching(this::isPillarBlock)) {
         return !this.swapMatchingItemToHotbar(this::isPillarBlock) && !ExtUtils.isAction() ? -1 : -2;
      } else {
         int instanceInventory2 = SelectedInventoryUtils.getInstanceInventory(this::isPlainFullCubeBlock);
         if (instanceInventory2 >= 0) {
            return instanceInventory2;
         } else {
            return !this.swapMatchingItemToHotbar(this::isPlainFullCubeBlock) && !ExtUtils.isAction() ? -1 : -2;
         }
      }
   }

   private void tickLandingState() {
      if (this.targetShip == null) {
         this.enterState(AutoElytraFinder.State.CRUISE);
      } else if (!this.tryContinueOnShip()) {
         if (mc.player.isOnGround() && !this.isGliding()) {
            if (!this.registerLandingFailure()) {
               this.enterState(AutoElytraFinder.State.TAKEOFF);
            }
         } else if (!this.isGliding()) {
            if (mc.player.getVelocity().y < 0.0 && !this.hasLevitationEffect()) {
               this.pulseJumpToDeployElytra();
            }
         } else if (this.returnForStoredElytra || !this.isTargetShipLooted()) {
            if (this.landingSpot == null) {
               this.landingSpot = this.findLandingSpot(this.targetShip);
               if (this.landingSpot == null) {
                  this.skipShipWithReason("no place to land");
                  return;
               }
            }

            if (this.isFireworkBoosting()) {
               if (!this.registerLandingFailure()) {
                  this.landingApproachPoint = null;
                  this.useOuterApproachPoint = true;
                  this.enterState(AutoElytraFinder.State.TO_SHIP);
               }
            } else {
               Vec3d entityPos = mc.player.getEntityPos();
               Vec3d offset = getHorizontalDirectionVector(this.targetShip.opposite);
               double shipVecValue = (this.landingSpot.x - entityPos.x) * offset.x + (this.landingSpot.z - entityPos.z) * offset.z;
               double deltaY = mc.player.getY() - this.landingSpot.y;
               if (!(shipVecValue < -12.0) && !(deltaY < -3.0) && this.getTicksInState() <= 700 && this.landingApproachTicks <= 30) {
                  if (this.tickCounter - this.lastAimUpdateTick >= 2 || this.plannedYaw == null) {
                     this.lastAimUpdateTick = this.tickCounter;
                     this.planGlideTowardTarget(this.landingSpot);
                  }

                  this.landingApproachTicks = this.flightPlanDistance >= 300.0 ? this.landingApproachTicks + 1 : 0;
                  this.requestLookTarget(this.plannedYaw, this.plannedPitch, 2);
               } else {
                  this.landingApproachTicks = 0;
                  if (!this.registerLandingFailure()) {
                     this.landingApproachPoint = null;
                     this.useOuterApproachPoint = true;
                     this.enterState(AutoElytraFinder.State.TO_SHIP);
                  }
               }
            }
         }
      }
   }

   private boolean isEntityInReach(Entity entity) {
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d add = eyePos.add(mc.player.getRotationVec(1.0F).multiply(3.0));
      var raycast = entity.getBoundingBox().raycast(eyePos, add);
      return raycast.isPresent() && eyePos.distanceTo((Vec3d)raycast.get()) <= 3.0 && this.hasLineOfSight((Vec3d)raycast.get());
   }

   private void applyRotationOverride() {
      if (mc.player != null) {
         if (!this.lookTargetRequested) {
            this.rotationOverrideActive = false;
            float eyeProduct = this.avoidEndermanGaze(mc.player.getYaw(), mc.player.getPitch());
            if (eyeProduct != mc.player.getPitch()) {
               mc.player.setPitch(eyeProduct);
            }
         } else {
            this.yawBeforeOverride = mc.player.getYaw();
            this.pitch = mc.player.getPitch();
            this.rotationOverrideActive = true;
            float pitchYawValue = this.lookYaw + WrapDegreesUtils.getWobbleOffset(this.tickCounter, 0.25F, 0);
            float wrapDegreesValue = this.lookPitch + WrapDegreesUtils.getWobbleOffset(this.tickCounter, 0.25F, 1);
            float amount = WrapDegreesUtils.stepAngleProportional(this.yawBeforeOverride, pitchYawValue, 0.35F, 27.0F, 3.0F);
            float clamp = MathHelper.clamp(WrapDegreesUtils.stepAngleProportional(this.pitch, wrapDegreesValue, 0.35F, 27.0F, 3.0F), -90.0F, 90.0F);
            float avoidEndermanGaze2 = this.avoidEndermanGaze(amount, clamp);
            mc.player.setYaw(amount);
            mc.player.setPitch(avoidEndermanGaze2);
            this.appliedYaw = amount;
            this.appliedPitch = avoidEndermanGaze2;
         }
      }
   }

   private void markPositionKnown(Vec3d vec) {
      if (vec != null) {
         for (Vec3d vec3d : this.knownStructurePositions) {
            if (vec3d.squaredDistanceTo(vec) < 16.0) {
               return;
            }
         }

         this.knownStructurePositions.add(vec);
      }
   }

   @Override
   protected void onDisable() {
      this.releaseMovementKeys();
      this.cancelBlockBreaking();
      this.confirmScreenHelper.updateHandledSuccess();
      this.pendingPurchase = AutoElytraFinder.Buy.NONE;
      if (this.inventorySwapPending && ExtUtils.isAction()) {
         ExtUtils.updateFlagText();
      }

      this.inventorySwapPending = false;
      if (mc.player != null && this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      }

      SelectedInventoryUtils.updateSelectedSlot2();
      this.rotationOverrideActive = false;
   }

   private boolean hasDurabilityLeft(ItemStack stack) {
      int damageValue = stack.getMaxDamage() > 0 ? stack.getMaxDamage() : 432;
      return damageValue - stack.getDamage() > 1;
   }

   private boolean isBlockWithinEyeReach(BlockPos pos, double centerEyeLimit) {
      return mc.player.getEyePos().distanceTo(Vec3d.ofCenter(pos)) <= centerEyeLimit;
   }

   private void tickRecoverChestState() {
      if (!this.isGliding()) {
         if (this.isContainerScreenOpen()) {
            mc.player.closeHandledScreen();
         } else if (!this.fightNearbyEnderman() && !this.isShulkerFighting()) {
            if (!mc.player.isOnGround()) {
               this.steerTowardAscendStep();
            } else if (this.enderChestPos == null) {
               this.enterState(AutoElytraFinder.State.EXIT);
            } else if (this.getBlockState(this.enderChestPos).isOf(Blocks.ENDER_CHEST)) {
               if (this.getTicksInState() > 600) {
                  this.notifyStatus(AutoElytraFinder.Kind.WARN, "Couldn't mine the ender chest - left it");
                  this.enderChestPos = null;
                  this.enterState(AutoElytraFinder.State.EXIT);
               } else {
                  int stackInventory = this.findSilkTouchPickaxeSlot();
                  if (stackInventory != -2) {
                     if (stackInventory < 0) {
                        this.notifyStatus(AutoElytraFinder.Kind.WARN, "No Silk Touch pickaxe - left the ender chest");
                        this.enderChestPos = null;
                        this.enterState(AutoElytraFinder.State.EXIT);
                     } else {
                        if (this.enderChestCountBefore < 0) {
                           this.enderChestCountBefore = this.countInventoryItem(Items.ENDER_CHEST);
                        }

                        if (!this.hasFreeInventorySlots(1)) {
                           if (!this.makeInventoryRoom()) {
                              this.notifyStatus(AutoElytraFinder.Kind.WARN, "Inventory full - left the ender chest");
                              this.enderChestPos = null;
                              this.enterState(AutoElytraFinder.State.EXIT);
                           }
                        } else if (!this.isBlockWithinEyeReach(this.enderChestPos, 3.6)) {
                           this.walkNearBlock(this.enderChestPos);
                        } else if (mc.player.getInventory().getSelectedSlot() != stackInventory) {
                           mc.player.getInventory().setSelectedSlot(stackInventory);
                        } else {
                           this.breakBlockAt(this.enderChestPos, false, true);
                        }
                     }
                  }
               }
            } else {
               if (this.droppedItemWaitStartTick < 0) {
                  this.droppedItemWaitStartTick = this.tickCounter;
               }

               if (this.enderChestCountBefore >= 0 && this.countInventoryItem(Items.ENDER_CHEST) > this.enderChestCountBefore) {
                  this.enderChestPos = null;
                  this.enterState(AutoElytraFinder.State.EXIT);
               } else {
                  ItemEntity entitiesSquared = this.getNearestItemEntity(12.0, itemEntity -> itemEntity.getStack().isOf(Items.ENDER_CHEST));
                  if (entitiesSquared != null && this.tickCounter - this.droppedItemWaitStartTick < 900) {
                     this.walkToAndPickUpItem(entitiesSquared);
                  } else {
                     if (this.tickCounter - this.droppedItemWaitStartTick > 60) {
                        this.enderChestPos = null;
                        this.enterState(AutoElytraFinder.State.EXIT);
                     }
                  }
               }
            }
         }
      }
   }

   private String getStateDescription() {
      return switch (this.currentState) {
         case PREPARING -> "Getting ready";
         case TAKEOFF -> "Taking off";
         case CRUISE -> "Searching for End Cities";
         case TO_CITY -> "Checking an End City";
         case TO_SHIP -> "Flying to an End Ship";
         case LANDING -> "Landing on the ship";
         case RETRIEVE -> this.retrieveStatusText;
         case STORE -> this.storeStatusText;
         case RECOVER_CHEST -> "Picking up the ender chest";
         case EXIT -> "Leaving the ship";
         case LAND -> this.job == AutoElytraFinder.Job.BUY_ROCKETS ? "Landing to buy rockets" : "Landing safely";
         case MENDING -> "Repairing the elytra";
         case BUY -> "Buying rockets";
         case PAUSED -> "Paused \u00b7 " + this.pauseReason;
      };
   }

   private boolean handleEndermanThreat() {
      if (!this.isGliding() && this.currentState != AutoElytraFinder.State.TAKEOFF && mc.player.isOnGround()) {
         EndermanEntity next = this.findNearestAngryEnderman(6.0);
         boolean endermanShipFlag = this.combatShulkers.isEnabled() && this.findNearestShulkerBullet(4.0) != null;
         if (next == null && !endermanShipFlag) {
            return false;
         } else {
            if (this.confirmScreenHelper.isRunningSuccess()) {
               this.confirmScreenHelper.updateHandledSuccess();
               this.pendingPurchase = AutoElytraFinder.Buy.NONE;
            }

            if (this.isContainerScreenOpen()) {
               mc.player.closeHandledScreen();
               return true;
            } else {
               this.cancelBlockBreaking();
               if (next != null
                  && mc.player.getHealth() <= 12.0F
                  && this.hasUsableElytraEquipped()
                  && this.hasFireworkRocket()
                  && (this.canFloorContract(16) || this.findClearTakeoffYaw() != null)) {
                  this.breakPathSteps = null;
                  if ((this.currentState == AutoElytraFinder.State.STORE || this.currentState == AutoElytraFinder.State.RECOVER_CHEST)
                     && this.targetShip != null
                     && (
                        this.placedShulkerPos != null && this.isShulkerBox(this.getBlockState(this.placedShulkerPos).getBlock())
                           || this.enderChestPos != null && this.getBlockState(this.enderChestPos).isOf(Blocks.ENDER_CHEST)
                     )) {
                     if (++this.endermanFleeCount >= 2) {
                        this.returnForStoredElytra = false;
                        this.notifyStatus(AutoElytraFinder.Kind.WARN, "Left the stored elytra on the ship (enderman)");
                     } else {
                        this.returnForStoredElytra = true;
                        this.endermanCooldownUntilTick = this.tickCounter + 600;
                     }
                  }

                  this.notifyStatus(AutoElytraFinder.Kind.WARN, "Enderman attacking - flying away");
                  this.enterState(AutoElytraFinder.State.TAKEOFF);
                  return true;
               } else {
                  return next != null ? this.fightNearbyEnderman() : this.isShulkerFighting();
               }
            }
         }
      } else {
         return false;
      }
   }

   private Vec3d getNearestKnownTarget() {
      Vec3d entityPos = this.fallbackTargetPos;
      double vecLocatorLimit = entityPos == null ? Double.MAX_VALUE : this.getHorizontalDistanceTo(entityPos);

      for (Ship ship : this.shipLocator.getShips()) {
         double hypot = this.getHorizontalDistanceTo(ship.getCenter());
         if (hypot < vecLocatorLimit) {
            vecLocatorLimit = hypot;
            entityPos = ship.getCenter();
         }
      }

      for (Vec3d vec3d : this.knownStructurePositions) {
         double horizontalDistanceTo = this.getHorizontalDistanceTo(vec3d);
         if (horizontalDistanceTo < vecLocatorLimit) {
            vecLocatorLimit = horizontalDistanceTo;
            entityPos = vec3d;
         }
      }

      if (this.cityTargetVec != null && this.getHorizontalDistanceTo(this.cityTargetVec) < vecLocatorLimit) {
         entityPos = this.cityTargetVec;
      }

      return entityPos;
   }

   private void cancelBlockBreaking() {
      if (this.interactionCancelActive && mc.interactionManager != null) {
         mc.interactionManager.cancelBlockBreaking();
      }

      this.interactionCancelActive = false;
      this.blockPosInteractionCancel = null;
   }

   private void scanForShipBlocks() {
      int blockPosValue = Math.min(256, Math.max(4, mc.options.getClampedViewDistance()) * 16);
      byte blockPosFactor = 8;
      int blockPosFactor2 = 2 * blockPosValue / blockPosFactor + 1;
      int blockPosLimit = blockPosFactor2 * blockPosFactor2;
      if (this.scanOrigin == null || this.scanCellIndex % blockPosLimit == 0) {
         this.scanOrigin = mc.player.getBlockPos();
         this.scanCellIndex = 0;
      }

      short blockPosLimit2 = 1200;
      int blockPosCounter = 0;
      int minTopYInclusive = Math.min(mc.world.getTopYInclusive(), this.scanOrigin.getY() + 60);
      int maxBottomY = Math.max(mc.world.getBottomY(), this.scanOrigin.getY() - 80);

      while (blockPosCounter < blockPosLimit2 && this.scanCellIndex < blockPosLimit) {
         int scanCellIndex2 = this.scanCellIndex++;
         int x = this.scanOrigin.getX() - blockPosValue + scanCellIndex2 % blockPosFactor2 * blockPosFactor;
         int z = this.scanOrigin.getZ() - blockPosValue + scanCellIndex2 / blockPosFactor2 * blockPosFactor;
         blockPosCounter++;
         int chunkWorld = this.getSurfaceHeight(x, z);
         if (chunkWorld != Integer.MIN_VALUE && chunkWorld >= maxBottomY) {
            for (int i = Math.min(chunkWorld, minTopYInclusive); i >= maxBottomY && i >= chunkWorld - 24; i -= 3) {
               blockPosCounter++;
               BlockPos blockPos = new BlockPos(x, i, z);
               if (END_SHIP_BLOCKS.contains(this.getBlockState(blockPos).getBlock())) {
                  Vec3d center = Vec3d.ofCenter(blockPos);
                  if (!this.isNearVisitedCity(center)) {
                     double amount = this.getHorizontalDistanceTo(center);
                     if (amount < this.nearestShipBlockDistance) {
                        this.nearestShipBlockDistance = amount;
                        this.nearestShipBlockCenter = center;
                     }
                  }
                  break;
               }
            }
         }
      }

      if (this.scanCellIndex >= blockPosLimit) {
         this.scanCellIndex = 0;
      }
   }

   private double getHorizontalSpeed() {
      Vec3d velocity = mc.player.getVelocity();
      return Math.hypot(velocity.x, velocity.z);
   }

   private static Vec3d simulateGlideVelocity(Vec3d vec, float sinMultiplyValue, float sinMultiplyFactor) {
      Vec3d cosSin = MathClampUtils.getCosSin(sinMultiplyValue, sinMultiplyFactor);
      double angle = sinMultiplyFactor * (float) (Math.PI / 180.0);
      double distance = Math.sqrt(cosSin.x * cosSin.x + cosSin.z * cosSin.z);
      double hypot = Math.hypot(vec.x, vec.z);
      double sinMultiplyFactor2 = Math.cos(angle) * Math.cos(angle);
      vec = vec.add(0.0, 0.08 * (-1.0 + sinMultiplyFactor2 * 0.75), 0.0);
      if (vec.y < 0.0 && distance > 0.0) {
         double y = vec.y * -0.1 * sinMultiplyFactor2;
         vec = vec.add(cosSin.x * y / distance, y, cosSin.z * y / distance);
      }

      if (angle < 0.0 && distance > 0.0) {
         double sinMultiplyFactor3 = hypot * -MathHelper.sin((float)angle) * 0.04;
         vec = vec.add(-cosSin.x * sinMultiplyFactor3 / distance, sinMultiplyFactor3 * 3.2, -cosSin.z * sinMultiplyFactor3 / distance);
      }

      if (distance > 0.0) {
         vec = vec.add((cosSin.x / distance * hypot - vec.x) * 0.1, 0.0, (cosSin.z / distance * hypot - vec.z) * 0.1);
      }

      return vec.multiply(0.99F, 0.98F, 0.99F);
   }

   private boolean isShulkerBox(Block block) {
      return block instanceof ShulkerBoxBlock;
   }

   private boolean isPlainFullCubeBlock(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
         Block block = blockItem.getBlock();
         return !this.isShulkerBox(block)
            && block != Blocks.ENDER_CHEST
            && !(block instanceof BlockEntityProvider)
            && !isValuableBlock(block, stack)
            && block.getDefaultState().isFullCube(mc.world, BlockPos.ORIGIN);
      } else {
         return false;
      }
   }

   private boolean isBreakableObstacle(BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      if (blockState.isAir() || blockState.hasBlockEntity()) {
         return false;
      } else if (!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.CRYING_OBSIDIAN)) {
         float hardness = blockState.getHardness(mc.world, pos);
         return !(hardness < 0.0F) && !(hardness > 6.0F) ? this.targetShip == null || !pos.equals(this.targetShip.blockPos.offset(this.targetShip.facing)) : false;
      } else {
         return false;
      }
   }

   private boolean isRayBlocked(Vec3d vec, Vec3d vec2, double normalizeRaycastValue) {
      BlockHitResult raycast = mc.world
         .raycast(new RaycastContext(vec, vec.add(vec2.normalize().multiply(normalizeRaycastValue)), ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
      return raycast.getType() == Type.BLOCK;
   }

   private boolean hasLineOfSight(Vec3d vec) {
      Vec3d eyePos = mc.player.getEyePos();
      BlockHitResult raycast = mc.world.raycast(new RaycastContext(eyePos, vec, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
      return raycast.getType() != Type.BLOCK || eyePos.squaredDistanceTo(raycast.getPos()) >= eyePos.squaredDistanceTo(vec) - 0.02;
   }

   private boolean swapToFreshElytra() {
      if (mc.player.isOnGround() && !this.isGliding()) {
         PlayerInventory inventory = mc.player.getInventory();
         int selectedSlot = -1;
         int damageElytraValue = Math.max(0, this.getEquippedElytraDurability()) + 40;

         for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(Items.ELYTRA)) {
               int damageElytraValue2 = stack.getMaxDamage() > 0 ? stack.getMaxDamage() : 432;
               if (damageElytraValue2 - stack.getDamage() > damageElytraValue) {
                  damageElytraValue = damageElytraValue2 - stack.getDamage();
                  selectedSlot = i;
               }
            }
         }

         if (selectedSlot < 0) {
            return false;
         } else if (selectedSlot > 8) {
            int selectedSlot3 = selectedSlot;
            this.swapMatchingItemToHotbar(stack2 -> stack2 == mc.player.getInventory().getStack(selectedSlot3));
            return true;
         } else if (inventory.getSelectedSlot() != selectedSlot) {
            inventory.setSelectedSlot(selectedSlot);
            return true;
         } else {
            if (this.tickCounter % 6 == 0) {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               this.notifyStatus(AutoElytraFinder.Kind.GOOD, "Swapped to a fresh elytra");
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private int getSwordTier(Item item) {
      if (item == Items.NETHERITE_SWORD) {
         return 5;
      } else if (item == Items.DIAMOND_SWORD) {
         return 4;
      } else if (item == Items.IRON_SWORD) {
         return 3;
      } else if (item == Items.GOLDEN_SWORD) {
         return 2;
      } else {
         return item == Items.STONE_SWORD ? 1 : 0;
      }
   }

   private boolean isWideLandingArea(BlockPos pos) {
      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            BlockPos add = pos.add(i, 0, j);
            if (!this.hasCollision(add) || this.hasCollision(add.up()) || this.hasCollision(add.up(2))) {
               return false;
            }
         }
      }

      return true;
   }

   private void resetAllState() {
      this.targetShip = null;
      this.breakingTargetShip = null;
      this.cityTargetVec = null;
      this.cityWaitStartTick = -1;
      this.scanCellIndex = 0;
      this.scanOrigin = null;
      this.nearestShipBlockCenter = null;
      this.nearestShipBlockDistance = Double.MAX_VALUE;
      this.landingSpot = null;
      this.landingApproachPoint = null;
      this.useOuterApproachPoint = true;
      this.healWaitStartTick = -1;
      this.retrieveStartTick = -1;
      this.lastFrameHitTick = -1000;
      this.frameMissingSinceTick = -1;
      this.frameAnyUntilTick = -1000;
      this.targetShulkerId = null;
      this.shulkerFightTargetPos = null;
      this.shulkerFightTick = -1000;
      this.breakPathSteps = null;
      this.breakPathKey = "";
      this.breakAttemptTick = -1000;
      this.breakPathExtraSolids.clear();
      this.failedStandPositions.clear();
      this.interactionCancelActive = false;
      this.blockPosInteractionCancel = null;
      this.breakingSide = null;
      this.enderChestPos = null;
      this.placedShulkerPos = null;
      this.storeStep = 0;
      this.lastSeenStoreStep = -1;
      this.syncId = -1;
      this.enderChestCountBefore = -1;
      this.pickupStartTick = -1;
      this.placedAtTick = -1;
      this.serverRemovalCount = 0;
      this.shulkerCountAfterPickup = -1;
      this.returnForStoredElytra = false;
      this.takeoffAttempts = 0;
      this.shipExitFailureCount = 0;
      this.failedExitBlockPos = null;
      this.pillarJumpAttempts = 0;
      this.pillarTopY = Integer.MIN_VALUE;
      this.yawLockUntilTick = -1000;
      this.fireworkActiveUntilTick = -1000;
      this.job = AutoElytraFinder.Job.NONE;
      this.pauseReason = "";
      this.pendingPurchase = AutoElytraFinder.Buy.NONE;
      this.rocketBuyFailureCount = 0;
      this.lookTargetRequested = false;
      this.rotationOverrideActive = false;
      this.shipLocator.update();
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

   private void skipShipWithReason(String autoElytraText) {
      this.notifyStatus(AutoElytraFinder.Kind.INFO, Character.toUpperCase(autoElytraText.charAt(0)) + autoElytraText.substring(1));
      this.markShipVisited();
      this.resetTargetShip();
   }

   private boolean isUnusedPlainElytra(ItemStack stack) {
      return stack.isOf(Items.ELYTRA) && stack.getDamage() == 0 && stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).isEmpty();
   }

   private boolean canUseFirework() {
      return this.hasFireworkRocket() && this.tickCounter - this.lastFireworkUseTick >= 10 && !this.isContainerScreenOpen();
   }

   private void tickToShipState() {
      if (!this.tryContinueOnShip()) {
         if (this.ensureAirborneOrReset()) {
            if (this.targetShip == null) {
               this.enterState(AutoElytraFinder.State.CRUISE);
            } else if (this.returnForStoredElytra || !this.isTargetShipLooted()) {
               if (this.countFireworkRockets() > 3
                  || !(this.getHorizontalDistanceTo(this.targetShip.getCenter()) > 64.0) && this.landingApproachPoint != null && !(mc.player.getY() < this.landingApproachPoint.y - 2.0)
                  || !this.shouldLandForRockets()) {
                  double hypot = this.getHorizontalDistanceTo(this.targetShip.getCenter());
                  if (this.landingSpot == null && hypot > 64.0) {
                     boolean playerElytra = this.eatIfHungry();
                     this.steerFlight(this.getYawToward(this.targetShip.getCenter()), this.targetShip.getY() + 14, !playerElytra, hypot);
                     if (this.getTicksInState() > 2400) {
                        this.abandonShip("couldn't reach the ship");
                     }
                  } else {
                     if (this.landingSpot == null) {
                        this.landingSpot = this.findLandingSpot(this.targetShip);
                        this.landingApproachPoint = null;
                        this.useOuterApproachPoint = true;
                     }

                     if (this.landingSpot == null) {
                        this.skipShipWithReason("no place to land on this ship");
                     } else {
                        Vec3d offset = getHorizontalDirectionVector(this.targetShip.facing);
                        if (this.landingApproachPoint == null) {
                           this.landingApproachPoint = this.landingSpot.add(offset.multiply(40.0)).add(0.0, 10.0, 0.0);
                        }

                        Vec3d subtract = mc.player.getEntityPos().subtract(this.landingSpot);
                        double shipLandLimit = subtract.x * offset.x + subtract.z * offset.z;
                        double shipLandValue = Math.abs(subtract.x * offset.z - subtract.z * offset.x);
                        if (this.useOuterApproachPoint && shipLandLimit > 46.0 && shipLandValue < 14.0) {
                           this.useOuterApproachPoint = false;
                        }

                        Vec3d vec3d = this.useOuterApproachPoint ? this.landingSpot.add(offset.multiply(66.0)).add(0.0, 12.5, 0.0) : this.landingApproachPoint;
                        double horizontalDistanceTo = this.getHorizontalDistanceTo(vec3d);
                        if (this.useOuterApproachPoint && horizontalDistanceTo < 14.0) {
                           this.useOuterApproachPoint = false;
                        }

                        double horizontalSpeed = this.getHorizontalSpeed();
                        boolean shipLandFlag = this.getHorizontalDistanceTo(this.landingSpot) < 30.0;
                        boolean shipLandFlag2 = !shipLandFlag || mc.player.getY() < this.landingSpot.y + 4.0 || horizontalSpeed < 0.5;
                        if (mc.player.getHealth() < 12.0F && !shipLandFlag && this.hasFoodAvailable()) {
                           if (this.healWaitStartTick < 0) {
                              this.healWaitStartTick = this.tickCounter;
                           }

                           if (this.tickCounter - this.healWaitStartTick < 600) {
                              boolean eatIfHungry2 = this.eatIfHungry();
                              this.steerFlight(this.getYawToward(vec3d) + 90.0F, vec3d.y, !eatIfHungry2 && shipLandFlag2, horizontalDistanceTo);
                              return;
                           }
                        } else {
                           this.healWaitStartTick = -1;
                        }

                        float degrees = this.getYawToward(this.landingSpot);
                        boolean shipLandFlag3 = Math.abs(MathHelper.wrapDegrees(degrees - mc.player.getYaw())) < 40.0F;
                        double deltaY = mc.player.getY() - this.landingApproachPoint.y;
                        double shipLandValue2 = this.landingSpot.y + this.getHorizontalDistanceTo(this.landingSpot) * 0.14 + 2.0;
                        boolean shipLandFlag4 = mc.player.getY() >= shipLandValue2;
                        if (!this.useOuterApproachPoint && !shipLandFlag4 && shipLandLimit < 34.0) {
                           this.useOuterApproachPoint = true;
                           vec3d = this.landingSpot.add(offset.multiply(66.0)).add(0.0, 12.5, 0.0);
                           horizontalDistanceTo = this.getHorizontalDistanceTo(vec3d);
                        }

                        if (!this.useOuterApproachPoint
                           && horizontalDistanceTo < 22.0
                           && deltaY > 6.0
                           && !this.isHorizontalPathBlocked(this.getLookVector(this.getYawToward(this.landingApproachPoint) + 75.0F, 0.0F), 20.0)) {
                           this.requestLookTarget(this.getYawToward(this.landingApproachPoint) + 75.0F, deltaY > 14.0 ? 24.0F : 16.0F, 0);
                        } else if (!this.useOuterApproachPoint && horizontalDistanceTo < 16.0 && shipLandFlag3 && shipLandLimit > 18.0 && !this.isFireworkBoosting() && shipLandFlag4) {
                           this.enterState(AutoElytraFinder.State.LANDING);
                        } else {
                           float showingCardValue = !this.useOuterApproachPoint && horizontalDistanceTo < 14.0 ? degrees : this.adjustYawAroundShip(this.getYawToward(vec3d), vec3d);
                           double showingCardBound = this.useOuterApproachPoint ? vec3d.y : Math.max(vec3d.y, shipLandValue2 + 1.0);
                           this.steerFlight(
                              showingCardValue, showingCardBound, (this.useOuterApproachPoint || horizontalDistanceTo > 45.0 || mc.player.getY() < shipLandValue2 - 2.0 || horizontalSpeed < 0.6) && shipLandFlag2, horizontalDistanceTo
                           );
                           if (this.getTicksInState() > 1600) {
                              this.abandonShip("couldn't reach the ship");
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private EndermanEntity findNearestAngryEnderman(double autoElytraValue) {
      EndermanEntity next = null;
      double autoElytraLimit = Double.MAX_VALUE;

      for (EndermanEntity endermanEntity : mc.world
         .getEntitiesByClass(EndermanEntity.class, getBox(mc.player.getEyePos(), autoElytraValue), endermanEntity2 -> endermanEntity2.isAlive() && endermanEntity2.isAngry())) {
         double squaredDistanceTo = endermanEntity.squaredDistanceTo(mc.player);
         if (squaredDistanceTo < autoElytraLimit) {
            autoElytraLimit = squaredDistanceTo;
            next = endermanEntity;
         }
      }

      return next;
   }

   private boolean hasFoodAvailable() {
      return mc.player.getHungerManager().getFoodLevel() >= 18
         || SelectedInventoryUtils.getInstanceInventory(this::isSafeFood) >= 0
         || this.hasInventoryItemMatching(this::isSafeFood);
   }

   private ShulkerEntity findNearestShulker() {
      if (this.targetShip == null) {
         return null;
      } else {
         ShulkerEntity next = null;
         double autoElytraLimit = Double.MAX_VALUE;

         for (ShulkerEntity shulkerEntity : mc.world
            .getEntitiesByClass(ShulkerEntity.class, getBox(Vec3d.ofCenter(this.targetShip.blockPos), 9.0), LivingEntity::isAlive)) {
            double squaredDistanceTo = shulkerEntity.squaredDistanceTo(mc.player);
            if (squaredDistanceTo < autoElytraLimit) {
               autoElytraLimit = squaredDistanceTo;
               next = shulkerEntity;
            }
         }

         return next;
      }
   }

   private ShulkerBulletEntity findNearestShulkerBullet(double autoElytraValue) {
      ShulkerBulletEntity next = null;
      double autoElytraLimit = Double.MAX_VALUE;

      for (ShulkerBulletEntity shulkerBulletEntity : mc.world
         .getEntitiesByClass(ShulkerBulletEntity.class, getBox(mc.player.getEyePos(), autoElytraValue), Entity::isAlive)) {
         double squaredDistanceTo = shulkerBulletEntity.squaredDistanceTo(mc.player);
         if (squaredDistanceTo < autoElytraLimit) {
            autoElytraLimit = squaredDistanceTo;
            next = shulkerBulletEntity;
         }
      }

      return next;
   }

   private boolean isSolidOrNonReplaceable(BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      return !blockState.isAir() && (!blockState.getCollisionShape(mc.world, pos).isEmpty() || !blockState.isReplaceable());
   }

   private boolean isShulkerFilledOnlyWithElytra(ItemStack stack) {
      if (this.canBlock(stack) && this.countContainerItems(stack) < 27) {
         ContainerComponent containerComponent = stack.get(DataComponentTypes.CONTAINER);
         if (containerComponent != null) {
            for (ItemStack itemStack : containerComponent.iterateNonEmpty()) {
               if (!itemStack.isOf(Items.ELYTRA)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean equipBestSword() {
      int selectedSlot = -1;
      int selectedSlotValue = -1;
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (!stack.isEmpty() && stack.isIn(ItemTags.SWORDS)) {
            int swordDiamond = this.getSwordTier(stack.getItem());
            if (swordDiamond > selectedSlotValue) {
               selectedSlotValue = swordDiamond;
               selectedSlot = i;
            }
         }
      }

      if (selectedSlot < 0) {
         return !this.swapMatchingItemToHotbar(stack2 -> stack2.isIn(ItemTags.SWORDS));
      } else if (inventory.getSelectedSlot() == selectedSlot) {
         return true;
      } else {
         inventory.setSelectedSlot(selectedSlot);
         return false;
      }
   }

   private boolean isColumnAboveClear(BlockPos pos, int autoElytraLimit) {
      for (int i = 0; i <= autoElytraLimit; i++) {
         if (this.hasCollision(pos.up(i))) {
            return false;
         }
      }

      return true;
   }

   private void tickBuyState() {
      if (this.isGliding()) {
         this.confirmScreenHelper.updateHandledSuccess();
         this.pendingPurchase = AutoElytraFinder.Buy.NONE;
         this.job = AutoElytraFinder.Job.BUY_ROCKETS;
         this.enterState(AutoElytraFinder.State.LAND);
      } else if (mc.player.isOnGround()) {
         if (!this.isContainerScreenOpen()) {
            this.requestLookTarget(mc.player.getYaw(), 55.0F, 0);
         }

         if (!this.confirmScreenHelper.isRunningSuccess()) {
            if (this.isPurchaseFinished(AutoElytraFinder.Buy.ROCKETS)) {
               boolean rocketRocketsFlag = this.confirmScreenHelper.getStatus() == Status.SUCCESS;
               String fullInventoryText = this.confirmScreenHelper.getFullInventoryMatchText();
               this.confirmScreenHelper.updateHandledSuccess();
               this.pendingPurchase = AutoElytraFinder.Buy.NONE;
               if (rocketRocketsFlag) {
                  this.rocketBuyFailureCount = 0;
                  if (this.countFireworkRockets() >= this.getMinimumFireworkCount() || this.getTicksInState() >= 1200) {
                     this.notifyStatus(AutoElytraFinder.Kind.GOOD, "Bought rockets (" + this.countFireworkRockets() + ")");
                     this.enterState(AutoElytraFinder.State.PREPARING);
                  }
               } else {
                  this.buyRetryNotBeforeTick = this.tickCounter + 1200;
                  if (++this.rocketBuyFailureCount >= 3) {
                     this.rocketBuyFailureCount = 0;
                     this.pauseWithReason("couldn't buy rockets from /ah (" + fullInventoryText + ")");
                  } else {
                     this.notifyStatus(AutoElytraFinder.Kind.WARN, "Couldn't buy rockets (" + fullInventoryText + ") - retrying in a minute");
                  }
               }
            } else if (this.countFireworkRockets() >= this.getMinimumFireworkCount()) {
               this.enterState(AutoElytraFinder.State.PREPARING);
            } else if (this.tickCounter >= this.buyRetryNotBeforeTick) {
               if (!this.startAuctionPurchase(AutoElytraFinder.Buy.ROCKETS, "ah firework rocket", Items.FIREWORK_ROCKET, this::isPlainFirework, true)) {
                  this.buyRetryNotBeforeTick = this.tickCounter + 1200;
                  this.pauseWithReason(this.hasFreeInventorySlots(1) ? "couldn't start the /ah rocket buy" : "inventory full - no room for rockets");
               }
            }
         }
      }
   }

   private int findPlayerSlotInScreen(Predicate<ItemStack> predicate) {
      ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
      if (currentScreenHandler == null) {
         return -1;
      } else {
         int slotsSlotValue = currentScreenHandler.slots.size() - 36;
         if (slotsSlotValue < 0) {
            return -1;
         } else {
            for (int i = currentScreenHandler.slots.size() - 1; i >= slotsSlotValue; i--) {
               if (predicate.test(currentScreenHandler.getSlot(i).getStack())) {
                  return i;
               }
            }

            return -1;
         }
      }
   }

   private void beginStoreState() {
      this.storeStep = 0;
      this.lastSeenStoreStep = -1;
      this.storeStepStartTick = this.tickCounter;
      this.enderChestPos = null;
      this.placedShulkerPos = null;
      this.syncId = -1;
      this.placedAtTick = -1;
      this.serverRemovalCount = 0;
      this.shulkerCountAfterPickup = -1;
      this.pendingPurchase = AutoElytraFinder.Buy.NONE;
      this.silkRetryCount = 0;
      this.enderChestSearched = false;
      this.droppedItemWaitStartTick = -1;
      if (this.findSilkTouchPickaxeSlot() == -1) {
         this.notifyStatus(AutoElytraFinder.Kind.WARN, "No Silk Touch pickaxe - carrying the elytra");
         this.enterState(AutoElytraFinder.State.EXIT);
      } else {
         if (!this.hasFreeInventorySlots(1)) {
            this.makeInventoryRoom();
         }

         this.enterState(AutoElytraFinder.State.STORE);
      }
   }

   private void markShipVisited() {
      if (this.targetShip != null) {
         this.targetShip.unreachable = true;
         Vec3d center = Vec3d.ofCenter(this.targetShip.blockPos);
         if (this.cityTargetVec != null && this.cityTargetVec.distanceTo(center) < 200.0) {
            this.markPositionKnown(this.cityTargetVec);
         }

         this.markPositionKnown(center);
         this.shipsVisitedCount++;
         this.cityTargetVec = null;
         this.searchHeadingYaw = this.getHorizontalDistanceTo(center) > 30.0
            ? mc.player.getYaw()
            : this.getYawToward(center) + 180.0F + (mc.player.getRandom().nextFloat() - 0.5F) * 60.0F;
      }
   }

   private boolean isChunkLoadedAhead(float chunkFloorValue, double chunkFloorFactor) {
      Vec3d wrapDegrees = this.getLookVector(chunkFloorValue, 0.0F);
      int floor = MathHelper.floor(mc.player.getX() + wrapDegrees.x * chunkFloorFactor);
      int floor3 = MathHelper.floor(mc.player.getZ() + wrapDegrees.z * chunkFloorFactor);
      return mc.world.getChunkManager().getWorldChunk(floor >> 4, floor3 >> 4) != null;
   }

   private boolean isSafeFood(ItemStack stack) {
      return stack.get(DataComponentTypes.FOOD) != null
         && !stack.isOf(Items.CHORUS_FRUIT)
         && !stack.isOf(Items.ROTTEN_FLESH)
         && !stack.isOf(Items.SPIDER_EYE)
         && !stack.isOf(Items.POISONOUS_POTATO)
         && !stack.isOf(Items.PUFFERFISH)
         && !stack.isOf(Items.SUSPICIOUS_STEW)
         && !stack.isOf(Items.GOLDEN_APPLE)
         && !stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
   }

   private boolean isStandableSpot(BlockPos pos) {
      return this.hasCollision(pos.down()) && !this.hasCollision(pos) && !this.hasCollision(pos.up());
   }

   private boolean handleFallingWithoutGliding() {
      if (this.currentState == AutoElytraFinder.State.TAKEOFF) {
         return false;
      } else if (!this.isGliding()
         && !mc.player.isOnGround()
         && !this.hasLevitationEffect()
         && !this.hasUsableElytraEquipped()
         && mc.player.getVelocity().y < -0.45
         && this.isColumnBelowEmpty(mc.player.getBlockPos(), 8)
         && this.equipFreshHotbarElytra()) {
         return true;
      } else if (this.isGliding() || mc.player.isOnGround() || this.hasLevitationEffect() || !this.hasUsableElytraEquipped()) {
         return false;
      } else if (mc.player.isClimbing() || mc.player.isTouchingWater()) {
         return false;
      } else if (mc.player.getVelocity().y > -0.45) {
         return false;
      } else if (!this.isColumnBelowEmpty(mc.player.getBlockPos(), 8) && mc.player.fallDistance < 6.0) {
         return false;
      } else {
         this.pulseJumpToDeployElytra();
         if (this.isGroundActivityState()) {
            this.cancelBlockBreaking();
            this.breakPathSteps = null;
            if (this.isContainerScreenOpen()) {
               mc.player.closeHandledScreen();
            }

            boolean shipStoredFlag = this.currentState == AutoElytraFinder.State.STORE || this.currentState == AutoElytraFinder.State.RECOVER_CHEST;
            if (shipStoredFlag) {
               this.confirmScreenHelper.updateHandledSuccess();
               this.pendingPurchase = AutoElytraFinder.Buy.NONE;
            }

            if ((this.currentState == AutoElytraFinder.State.RETRIEVE || shipStoredFlag) && this.targetShip != null) {
               this.returnForStoredElytra = shipStoredFlag;
               this.landingApproachPoint = null;
               this.landingSpot = null;
               this.useOuterApproachPoint = true;
               this.enterState(AutoElytraFinder.State.LANDING);
            } else {
               this.job = AutoElytraFinder.Job.NONE;
               this.enterState(shipStoredFlag ? AutoElytraFinder.State.LAND : AutoElytraFinder.State.CRUISE);
            }
         }

         this.requestLookTarget(mc.player.getYaw(), 10.0F, 2);
         return true;
      }
   }

   private boolean isPlainFirework(ItemStack stack) {
      if (!stack.isOf(Items.FIREWORK_ROCKET)) {
         return false;
      } else {
         FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
         return fireworksComponent == null || fireworksComponent.explosions().isEmpty();
      }
   }

   private boolean needsRocketPurchase() {
      return this.countFireworkRockets() < this.getMinimumFireworkCount() && this.tickCounter >= this.buyRetryNotBeforeTick;
   }

   private int countShulkerBoxItems() {
      PlayerInventory inventory = mc.player.getInventory();
      int stackInventory = 0;

      for (int i = 0; i <= 35; i++) {
         if (this.canBlock(inventory.getStack(i))) {
            stackInventory += inventory.getStack(i).getCount();
         }
      }

      return stackInventory;
   }

   private boolean isShulkerFighting() {
      if (this.combatShulkers.isEnabled() && !this.isContainerScreenOpen()) {
         ShulkerEntity next = this.findNearestShulker();
         boolean shulkerFightingFlag = next != null && this.shouldFightShulker(next);
         ShulkerBulletEntity nearestShulkerBullet = this.findNearestShulkerBullet(shulkerFightingFlag ? 2.2 : 4.0);
         if (nearestShulkerBullet != null) {
            this.attackShulkerBullet(nearestShulkerBullet);
            return true;
         } else if (shulkerFightingFlag) {
            this.fightShulker(next);
            this.storeStatusText = "Fighting the guard shulker";
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean isShipStored() {
      if (!this.returnForStoredElytra || this.targetShip == null) {
         return false;
      } else if (this.targetShip.unreachableCount < 12 && (this.hasFoodAvailable() || !(mc.player.getHealth() <= 12.0F))) {
         if (this.tickCounter >= this.endermanCooldownUntilTick && mc.player.getHealth() >= 16.0F) {
            this.beginShipApproach(this.targetShip);
            return true;
         } else {
            boolean playerElytra = this.eatIfHungry();
            Vec3d center = this.targetShip.getCenter();
            this.steerFlight(this.getYawToward(center) + 90.0F, this.targetShip.getY() + 20, !playerElytra, this.getHorizontalDistanceTo(center));
            return true;
         }
      } else {
         this.notifyStatus(AutoElytraFinder.Kind.WARN, "Couldn't get back to the stored elytra - left it on the ship");
         this.returnForStoredElytra = false;
         this.targetShip = null;
         return false;
      }
   }

   private boolean isHorizontalPathBlocked(Vec3d vec, double normalizeSubtractValue) {
      Vec3d vec3d = new Vec3d(vec.x, 0.0, vec.z);
      if (vec3d.lengthSquared() < 1.0E-6) {
         return false;
      } else {
         Vec3d normalize = vec3d.normalize();
         Vec3d vec3d2 = new Vec3d(-normalize.z * 0.33, 0.0, normalize.x * 0.33);
         Vec3d entityPos = mc.player.getEntityPos();
         double normalizeSubtractValue2 = this.isGliding() ? 0.55 : 1.7;

         for (double y : new double[]{0.05, normalizeSubtractValue2}) {
            Vec3d add = entityPos.add(0.0, y, 0.0);
            if (this.isRayBlocked(add, normalize, normalizeSubtractValue)
               || this.isRayBlocked(add.add(vec3d2), normalize, normalizeSubtractValue)
               || this.isRayBlocked(add.subtract(vec3d2), normalize, normalizeSubtractValue)) {
               return true;
            }
         }

         return false;
      }
   }

   private void steerTowardAscendStep() {
      if (this.breakPathSteps != null && this.breakPathIndex < this.breakPathSteps.size()) {
         Step breakingshipitem = this.breakPathSteps.get(this.breakPathIndex);
         if (breakingshipitem.move() == Move.ASCEND) {
            Vec3d bottomCenter = Vec3d.ofBottomCenter(breakingshipitem.feet());
            float degrees = this.getYawToward(bottomCenter);
            this.requestLookTarget(degrees, 12.0F, 0);
            if (this.getHorizontalDistanceTo(bottomCenter) > 0.25 && Math.abs(MathHelper.wrapDegrees(degrees - mc.player.getYaw())) < 40.0F) {
               mc.options.forwardKey.setPressed(true);
            }
         }
      }
   }

   private double simulateGlideLandingCost(float shipPlayerValue, float shipPlayerValue2, Vec3d vec, int shipPlayerLimit, float pitchTableDegreesItem) {
      this.shipPlayerActive = false;
      Vec3d entityPos = mc.player.getEntityPos();
      Vec3d velocity = mc.player.getVelocity();
      float yaw = mc.player.getYaw();
      float pitch = mc.player.getPitch();
      double deltaX = vec.x - entityPos.x;
      double deltaZ = vec.z - entityPos.z;
      double hypot = Math.hypot(deltaX, deltaZ);
      double shipPlayerFactor;
      double shipPlayerFactor2;
      if (hypot > 0.001) {
         shipPlayerFactor = deltaX / hypot;
         shipPlayerFactor2 = deltaZ / hypot;
      } else {
         shipPlayerFactor = 0.0;
         shipPlayerFactor2 = 0.0;
      }

      double shipPlayerTotal = 0.0;

      for (int i = 0; i < 90; i++) {
         yaw = WrapDegreesUtils.stepAngleProportional(yaw, shipPlayerValue, 0.35F, 27.0F, 3.0F);
         float wrapDegreesValue = shipPlayerLimit >= 0 && i >= shipPlayerLimit ? pitchTableDegreesItem : shipPlayerValue2;
         pitch = MathHelper.clamp(WrapDegreesUtils.stepAngleProportional(pitch, wrapDegreesValue, 0.35F, 27.0F, 3.0F), -90.0F, 90.0F);
         double hypot2 = Math.hypot(velocity.x, velocity.z);
         Vec3d sinMultiply = simulateGlideVelocity(velocity, yaw, pitch);
         Vec3d worldCollide = this.getCollisionAdjustedMovement(sinMultiply, getPlayerBoxAt(entityPos.x, entityPos.y, entityPos.z));
         boolean shipPlayerFlag = worldCollide.x != sinMultiply.x;
         boolean shipPlayerFlag2 = worldCollide.z != sinMultiply.z;
         boolean shipPlayerFlag3 = worldCollide.y != sinMultiply.y;
         double y = sinMultiply.y;
         if (shipPlayerFlag || shipPlayerFlag2) {
            double hypot3 = Math.hypot(shipPlayerFlag ? 0.0 : sinMultiply.x, shipPlayerFlag2 ? 0.0 : sinMultiply.z);
            double shipPlayerLimit2 = (hypot2 - hypot3) * 10.0 - 3.0;
            if (shipPlayerLimit2 > 0.0) {
               return 1000.0 + shipPlayerLimit2 * 60.0;
            }

            shipPlayerTotal += 12.0;
         }

         entityPos = entityPos.add(worldCollide);
         velocity = new Vec3d(shipPlayerFlag ? 0.0 : sinMultiply.x, shipPlayerFlag3 ? 0.0 : sinMultiply.y, shipPlayerFlag2 ? 0.0 : sinMultiply.z);
         if (shipPlayerFlag3) {
            if (y > 0.0) {
               return 600 + i;
            }

            double hypot4 = Math.hypot(velocity.x, velocity.z);
            double hypot5 = Math.hypot(entityPos.x - vec.x, entityPos.z - vec.z);
            boolean shipPlayerFlag4 = Math.abs(entityPos.y - vec.y) < 2.6
               && (this.targetShip != null && this.currentState == AutoElytraFinder.State.LANDING ? horizontalDistanceBetween(entityPos, this.targetShip.getCenter()) < 18.0 : hypot5 < 8.0);
            double max5 = Math.max(0.0, -y - 0.5);
            double estimateFallPenalty6 = this.estimateFallPenalty(entityPos, velocity);
            this.shipPlayerActive = shipPlayerFlag4 && estimateFallPenalty6 == 0.0;
            return (shipPlayerFlag4 ? 0 : 300) + estimateFallPenalty6 + hypot5 * 0.6 + hypot4 * 7.0 + max5 * 45.0 + shipPlayerTotal;
         }

         if (entityPos.y < vec.y - 4.0) {
            return 800.0;
         }
      }

      double hypot6 = Math.hypot(entityPos.x - vec.x, entityPos.z - vec.z);
      double deltaY = entityPos.y - vec.y;
      double hypot7 = Math.hypot(velocity.x, velocity.z);
      double max7 = Math.max(0.0, -((vec.x - entityPos.x) * shipPlayerFactor + (vec.z - entityPos.z) * shipPlayerFactor2));
      return Math.min(280.0, 120.0 + Math.abs(deltaY - hypot6 * 0.22) * 4.0 + hypot6 * 0.4 + Math.max(0.0, hypot7 - 1.15) * 25.0 + max7 * 1.5 + shipPlayerTotal);
   }

   private void updateTakeoffState() {
      if (this.isGliding()) {
         if (this.takeoffStartY == Integer.MIN_VALUE) {
            this.takeoffStartY = mc.player.getBlockY();
         }

         double yawFireworkLimit = mc.player.getY() - this.takeoffStartY;
         if (this.hasClearColumnAbove && yawFireworkLimit >= 9.0) {
            this.hasClearColumnAbove = false;
            this.yaw = mc.player.getYaw();
         }

         boolean hasClearColumnAbove7 = this.hasClearColumnAbove;
         float yawFireworkValue = hasClearColumnAbove7 ? -88.0F : -30.0F;
         this.requestLookTarget(this.hasClearColumnAbove ? mc.player.getYaw() : this.yaw, yawFireworkValue, 0);
         if (!this.isFireworkBoosting() && this.canUseFirework() && Math.abs(mc.player.getPitch() - yawFireworkValue) < 15.0F && (hasClearColumnAbove7 || this.getHorizontalSpeed() < 1.2)) {
            this.useFireworkRocket();
         }

         if (yawFireworkLimit > 9.0 && this.getHorizontalSpeed() > 0.85 || this.getTicksInState() > 240) {
            this.shipExitFailureCount = 0;
            this.failedExitBlockPos = null;
            this.enterState(AutoElytraFinder.State.CRUISE);
         }
      } else if (!this.hasFireworkRocket()) {
         if (this.countFireworkRockets() > 0) {
            this.swapMatchingItemToHotbar(this::isPlainFirework);
         } else if (mc.player.isOnGround()) {
            this.job = AutoElytraFinder.Job.BUY_ROCKETS;
            this.enterState(AutoElytraFinder.State.BUY);
         } else {
            this.pauseWithReason("no firework rockets");
         }
      } else {
         this.selectFireworkSlot();
         if (mc.player.isOnGround()) {
            this.takeoffStartY = Integer.MIN_VALUE;
            if (this.tickCounter - this.lastJumpTick >= 14) {
               if (this.takeoffAttempts >= 3) {
                  this.takeoffAttempts = 0;
                  this.enterState(AutoElytraFinder.State.EXIT);
               } else {
                  this.hasClearColumnAbove = this.canFloorContract(16);
                  if (!this.hasClearColumnAbove && this.isColumnAboveClear(this.getFeetBlockPos(), 16) && this.getTicksInState() < 80) {
                     this.moveTowardWhileSneaking(Vec3d.ofBottomCenter(this.getFeetBlockPos()));
                  } else {
                     if (!this.hasClearColumnAbove) {
                        Float yaw = this.findClearTakeoffYaw();
                        if (yaw == null) {
                           this.enterState(AutoElytraFinder.State.EXIT);
                           return;
                        }

                        this.yaw = yaw;
                     }

                     float degrees = this.hasClearColumnAbove ? mc.player.getYaw() : this.yaw;
                     float yawFireworkValue3 = this.hasClearColumnAbove ? -88.0F : -35.0F;
                     this.requestLookTarget(degrees, yawFireworkValue3, 0);
                     if (!(Math.abs(MathHelper.wrapDegrees(degrees - mc.player.getYaw())) > 10.0F)
                        && !(Math.abs(mc.player.getPitch() - yawFireworkValue3) > 10.0F)) {
                        mc.options.jumpKey.setPressed(true);
                        this.lastJumpTick = this.tickCounter;
                        this.takeoffAttempts++;
                     }
                  }
               }
            }
         } else {
            this.requestLookTarget(this.hasClearColumnAbove ? mc.player.getYaw() : this.yaw, this.hasClearColumnAbove ? -88.0F : -35.0F, 0);
            if (mc.player.getVelocity().y < 0.12 && this.tickCounter - this.lastJumpTick >= 2) {
               this.pulseJumpToDeployElytra();
            }
         }
      }
   }

   private static Box getBox(Vec3d vec, double boxValue) {
      return new Box(vec.x - boxValue, vec.y - boxValue, vec.z - boxValue, vec.x + boxValue, vec.y + boxValue, vec.z + boxValue);
   }

   private boolean hasClearLineBetween(Vec3d vec, Vec3d vec2, BlockPos pos) {
      Vec3d subtract = vec2.subtract(vec);
      int subtractFlooredLimit = (int)Math.ceil(subtract.length() / 0.2);

      for (int i = 1; i < subtractFlooredLimit; i++) {
         BlockPos floored = BlockPos.ofFloored(vec.add(subtract.multiply((double)i / subtractFlooredLimit)));
         if (!floored.equals(pos) && !floored.equals(pos.up()) && this.hasCollision(floored)) {
            return false;
         }
      }

      return true;
   }

   private int countHotbarPickaxes() {
      int pickaxesInventory = 0;

      for (int i = 0; i < 9; i++) {
         if (mc.player.getInventory().getStack(i).isIn(ItemTags.PICKAXES)) {
            pickaxesInventory++;
         }
      }

      return pickaxesInventory;
   }

   private boolean hasLevitationEffect() {
      return mc.player.hasStatusEffect(StatusEffects.LEVITATION);
   }

   private void useFireworkRocket() {
      if (this.isGliding() && !this.isContainerScreenOpen()) {
         int selectedSlot = this.getFireworkSlot();
         boolean playerHandFlag = selectedSlot < 0 && this.hasFireworkInOffhand();
         if (selectedSlot < 0 && !playerHandFlag) {
            if (this.countFireworkRockets() > 0) {
               this.swapMatchingItemToHotbar(this::isPlainFirework);
            }
         } else if (!this.isLineFromEyeBlocked(this.getLookVector(mc.player.getYaw(), mc.player.getPitch()), 10.0)) {
            if (!playerHandFlag && mc.player.getInventory().getSelectedSlot() != selectedSlot) {
               mc.player.getInventory().setSelectedSlot(selectedSlot);
            } else {
               Hand hand = playerHandFlag ? Hand.OFF_HAND : Hand.MAIN_HAND;
               FireworksComponent fireworksComponent = (playerHandFlag ? mc.player.getOffHandStack() : mc.player.getInventory().getStack(selectedSlot))
                  .get(DataComponentTypes.FIREWORKS);
               int playerHandValue = fireworksComponent == null ? 1 : Math.max(1, fireworksComponent.flightDuration());
               mc.interactionManager.interactItem(mc.player, hand);
               mc.player.swingHand(hand);
               this.lastFireworkUseTick = this.tickCounter;
               this.fireworkActiveUntilTick = this.tickCounter + 10 * (playerHandValue + 1) + 13;
               if (!this.hasFireworkRocket() && this.countFireworkRockets() > 0) {
                  this.swapMatchingItemToHotbar(this::isPlainFirework);
               }
            }
         }
      }
   }

   private void updateToCityState() {
      if (this.ensureAirborneOrReset()) {
         if (!this.shouldLandForRockets()) {
            if (!this.isShipStored()) {
               Ship next = this.getNearestLootShip();
               if (next != null) {
                  this.beginShipApproach(next);
               } else if (this.cityTargetVec == null) {
                  this.enterState(AutoElytraFinder.State.CRUISE);
               } else {
                  double hypot = this.getHorizontalDistanceTo(this.cityTargetVec);
                  if (hypot < 56.0) {
                     if (this.cityWaitStartTick < 0) {
                        this.cityWaitStartTick = this.tickCounter;
                     }

                     this.steerFlight(this.getYawToward(this.cityTargetVec) + 80.0F, this.flightAltitudeSetting.getValue(), true, 0.0);
                     if (this.tickCounter - this.cityWaitStartTick > 90) {
                        this.notifyStatus(AutoElytraFinder.Kind.INFO, "No ship at this End City");
                        this.markPositionKnown(this.cityTargetVec);
                        this.shipsVisitedCount++;
                        this.searchHeadingYaw = this.getYawToward(this.cityTargetVec) + 180.0F + (mc.player.getRandom().nextFloat() - 0.5F) * 60.0F;
                        this.cityTargetVec = null;
                        this.enterState(AutoElytraFinder.State.CRUISE);
                     }
                  } else {
                     boolean playerElytra = this.eatIfHungry();
                     this.steerFlight(this.getYawToward(this.cityTargetVec), this.flightAltitudeSetting.getValue(), !playerElytra, hypot);
                     if (this.getTicksInState() > 1600) {
                        this.markPositionKnown(this.cityTargetVec);
                        this.cityTargetVec = null;
                        this.enterState(AutoElytraFinder.State.CRUISE);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isGliding() {
      return mc.player.isGliding();
   }

   private boolean tryAttackEntity(Entity entity) {
      if (this.getEyeDistanceToBox(entity) > 2.9 || !this.isEntityInReach(entity)) {
         return false;
      } else if (!TimeInstanceUtils.canFlag(entity)) {
         return false;
      } else {
         mc.interactionManager.attackEntity(mc.player, entity);
         mc.player.swingHand(Hand.MAIN_HAND);
         return true;
      }
   }

   private static Vec3d getHorizontalDirectionVector(Direction direction) {
      return new Vec3d(direction.getOffsetX(), 0.0, direction.getOffsetZ());
   }

   @Override
   protected void onEnable() {
      this.lastNotificationText = "";
      this.lastNotificationTick = -10000;
      this.fallbackTargetPos = null;
      this.endNeedText = null;
      this.elytraStatus = ElytraStatus.INSTANCE;
      if (!this.isInEnd()) {
         this.endNeedText = "Go to The End first.";
      } else if (!this.hasUsableElytra()) {
         this.endNeedText = "You need an elytra.";
      } else {
         BlockPos blockPos = this.placedShulkerPos != null ? this.placedShulkerPos : this.pendingShulkerPos;
         BlockPos pos = this.enderChestPos != null ? this.enderChestPos : this.pendingEnderChestPos;
         this.pendingShulkerPos = null;
         this.pendingEnderChestPos = null;
         this.resetAllState();
         this.startElytraCount = mc.player != null ? this.countElytraInInventory() : -1;
         String storingElytraText = this.getServerAddress();
         if (!storingElytraText.equals(this.storingElytraText)) {
            this.storingElytraText = storingElytraText;
            this.knownStructurePositions.clear();
            this.shipsVisitedCount = 0;
         }

         if (mc.player != null) {
            this.searchHeadingYaw = mc.player.getYaw();
         }

         this.confirmScreenHelper.configureConfirmAuction("Auction", "Yes,Confirm", "sure", "$", "already bought", null);
         this.elytraStatusCard.updateSmoothTime();
         this.enterState(AutoElytraFinder.State.PREPARING);
         this.restoreStorageAfterLanding(blockPos, pos);
         this.refreshStatusCard();
      }
   }

   private ItemEntity getNearestItemEntity(double entitiesSquaredValue, Predicate<ItemEntity> predicate) {
      ItemEntity entitiesSquared = null;
      double entitiesSquaredLimit = Double.MAX_VALUE;

      for (ItemEntity itemEntity : mc.world.getEntitiesByClass(ItemEntity.class, getBox(mc.player.getEntityPos(), entitiesSquaredValue), predicate)) {
         double squaredDistanceTo = itemEntity.squaredDistanceTo(mc.player);
         if (squaredDistanceTo < entitiesSquaredLimit) {
            entitiesSquaredLimit = squaredDistanceTo;
            entitiesSquared = itemEntity;
         }
      }

      return entitiesSquared;
   }

   private boolean isPurchaseFinished(AutoElytraFinder.Buy buy) {
      if (this.pendingPurchase != buy) {
         if ((
               this.pendingPurchase != AutoElytraFinder.Buy.NONE
                  || !this.confirmScreenHelper.isRunningSuccess() && this.confirmScreenHelper.getStatus() != Status.IDLE
            )
            && !this.confirmScreenHelper.isRunningSuccess()) {
            this.confirmScreenHelper.updateHandledSuccess();
            this.pendingPurchase = AutoElytraFinder.Buy.NONE;
         }

         return false;
      } else {
         return this.confirmScreenHelper.getStatus() == Status.SUCCESS || this.confirmScreenHelper.getStatus() == Status.FAILED;
      }
   }

   private int getTicksSincePurchaseStart() {
      return this.tickCounter - this.storeStepStartTick;
   }

   private float getClearPitchOffset(float autoElytraValue, boolean autoElytraFlag, double offsetUnionBound) {
      float[] autoElytraValues = autoElytraFlag ? new float[]{-40.0F, -20.0F, 0.0F, 14.0F} : new float[]{-14.0F, 0.0F, 10.0F};

      for (float autoElytraValuesItem : autoElytraValues) {
         if (!this.isPathBlockedAlong(this.getLookVector(autoElytraValue, autoElytraValuesItem), offsetUnionBound * 0.7)) {
            return autoElytraValuesItem;
         }
      }

      return autoElytraFlag ? -40.0F : -14.0F;
   }

   private static boolean isValuableBlock(Block block, ItemStack stack) {
      if (stack.getRarity() != Rarity.COMMON) {
         return true;
      } else {
         String path = Registries.BLOCK.getId(block).getPath();
         if (!path.endsWith("_ore")
            && !path.startsWith("raw_")
            && !path.equals("ancient_debris")
            && !path.contains("obsidian")
            && !path.contains("sponge")
            && !path.contains("amethyst")
            && !path.equals("beacon")
            && !path.equals("spawner")
            && !path.equals("trial_spawner")) {
            if (!path.endsWith("_block")) {
               return false;
            } else {
               for (String spawnerCoalText : new String[]{"diamond", "emerald", "netherite", "gold", "iron", "lapis", "redstone", "copper", "coal", "quartz"}) {
                  if (path.startsWith(spawnerCoalText)) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            return true;
         }
      }
   }

   private boolean isGroundActivityState() {
      return this.currentState == AutoElytraFinder.State.RETRIEVE
         || this.currentState == AutoElytraFinder.State.STORE
         || this.currentState == AutoElytraFinder.State.RECOVER_CHEST
         || this.currentState == AutoElytraFinder.State.EXIT
         || this.currentState == AutoElytraFinder.State.MENDING
         || this.currentState == AutoElytraFinder.State.BUY
         || this.currentState == AutoElytraFinder.State.PREPARING;
   }

   private void updateElytraRepairing() {
      if (this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      } else if (mc.player.isOnGround()) {
         int max = this.getEquippedElytraDurability();
         if (max < 0) {
            this.enterState(AutoElytraFinder.State.PREPARING);
         } else {
            int equippedDamage = this.getChestMaxDamage();
            if (max < equippedDamage && mc.player.getEquippedStack(EquipmentSlot.CHEST).getDamage() != 0) {
               if (max > this.peakElytraDamage) {
                  this.peakElytraDamage = max;
                  this.repairProgressTick = this.tickCounter;
               }

               if (this.tickCounter - this.repairProgressTick > 160) {
                  if (max > this.getMinElytraDurability() + 40) {
                     this.enterState(AutoElytraFinder.State.PREPARING);
                  } else {
                     this.pauseWithReason("mending isn't repairing the elytra");
                  }
               } else {
                  int selectedSlot = SelectedInventoryUtils.getValue(Items.EXPERIENCE_BOTTLE);
                  if (selectedSlot >= 0) {
                     this.requestLookTarget(mc.player.getYaw(), 82.0F, 1);
                     if (mc.player.getInventory().getSelectedSlot() != selectedSlot) {
                        mc.player.getInventory().setSelectedSlot(selectedSlot);
                     } else {
                        if (mc.player.getPitch() > 60.0F && this.tickCounter - this.lastXpThrowTick >= 5) {
                           mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                           mc.player.swingHand(Hand.MAIN_HAND);
                           this.lastXpThrowTick = this.tickCounter;
                        }
                     }
                  } else if (!this.swapItemToHotbar(Items.EXPERIENCE_BOTTLE) && !ExtUtils.isAction()) {
                     if (max > this.getMinElytraDurability() + 40) {
                        this.enterState(AutoElytraFinder.State.PREPARING);
                     } else {
                        this.pauseWithReason("out of XP bottles");
                     }
                  }
               }
            } else {
               this.notifyStatus(AutoElytraFinder.Kind.GOOD, "Elytra repaired");
               this.enterState(AutoElytraFinder.State.PREPARING);
            }
         }
      }
   }

   private boolean ensureBestToolSelected(BlockPos pos2) {
      BlockState blockState = this.getBlockState(pos2);
      PlayerInventory inventory = mc.player.getInventory();
      int selectedSlot = -1;
      float selectedSlotValue = 1.0F;

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (!stack.isEmpty()
            && (!this.hasEnchantment(stack, Enchantments.SILK_TOUCH) || blockState.isOf(Blocks.ENDER_CHEST) || this.countHotbarPickaxes() <= 1)) {
            float miningSpeedMultiplier = stack.getMiningSpeedMultiplier(blockState);
            if (miningSpeedMultiplier > selectedSlotValue) {
               selectedSlotValue = miningSpeedMultiplier;
               selectedSlot = i;
            }
         }
      }

      if (selectedSlot >= 0 && inventory.getSelectedSlot() != selectedSlot) {
         inventory.setSelectedSlot(selectedSlot);
         return false;
      } else {
         return true;
      }
   }

   private int getEquippedElytraDurability() {
      ItemStack equippedStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
      return equippedStack.isOf(Items.ELYTRA) ? Math.max(0, this.getChestMaxDamage() - equippedStack.getDamage()) : -1;
   }

   private Vec3d getCollisionAdjustedMovement(Vec3d vec, Box box) {
      Box stretch = box.stretch(vec);
      return !mc.world.isDebugWorld()
            && this.areChunkSectionsEmpty(stretch)
            && !mc.world.getWorldBorder().canCollide(mc.player, stretch)
         ? vec
         : Entity.adjustMovementForCollisions(mc.player, vec, box, mc.world, List.of());
   }

   private boolean isItemEntityInPickupRange(ItemEntity itemEntity) {
      return itemEntity.getBoundingBox().expand(0.6).intersects(mc.player.getBoundingBox());
   }

   private BlockState getBlockState(BlockPos pos) {
      return mc.world.getBlockState(pos);
   }

   private void fightShulker(ShulkerEntity shulkerEntity) {
      this.retrieveStatusText = "Fighting the guard shulker";
      this.guardFightTick = this.tickCounter;
      if (this.targetShulkerId == null || this.targetShulkerId != shulkerEntity.getId()) {
         this.targetShulkerId = shulkerEntity.getId();
         this.shulkerFightTargetPos = null;
      }

      Vec3d entityPos = shulkerEntity.getEntityPos();
      if (this.shulkerFightTargetPos != null && this.shulkerFightTargetPos.squaredDistanceTo(entityPos) > 9.0) {
         this.shulkerFightTargetPos = entityPos;
         this.shulkerFightTick = this.tickCounter + 100;
      } else {
         this.shulkerFightTargetPos = entityPos;
         Vec3d center = shulkerEntity.getBoundingBox().getCenter();
         if (this.getEyeDistanceToBox(shulkerEntity) <= 2.9) {
            this.lookAtPoint(center, 3);
            if (this.equipBestSword()) {
               if (mc.player.getAttackCooldownProgress(0.0F) >= 0.9F && this.isLookingTowardPoint(center, 8.0F) && this.isEntityInReach(shulkerEntity)) {
                  this.tryAttackEntity(shulkerEntity);
               }
            }
         } else if (!mc.player.isOnGround()) {
            this.steerTowardAscendStep();
         } else {
            BlockPos blockPos = shulkerEntity.getBlockPos();
            int shipBreaking = this.walkAndMineToward("guard" + shulkerEntity.getId(), pos -> {
               Vec3d var4x = new Vec3d(pos.getX() + 0.5, pos.getY() + mc.player.getStandingEyeHeight(), pos.getZ() + 0.5);
               Box var5x = shulkerEntity.getBoundingBox();
               double clamp = MathHelper.clamp(var4x.x, var5x.minX, var5x.maxX);
               double clamp2 = MathHelper.clamp(var4x.y, var5x.minY, var5x.maxY);
               double clamp3 = MathHelper.clamp(var4x.z, var5x.minZ, var5x.maxZ);
               return var4x.squaredDistanceTo(clamp, clamp2, clamp3) <= 7.290000000000001 && this.hasClearLineBetween(var4x, center, pos);
            }, blockPos);
            if (shipBreaking < 0) {
               this.shulkerFightTick = this.tickCounter + 200;
            }
         }
      }
   }

   private ItemEntity getNearestElytraItemEntity(double autoElytraValue) {
      Vec3d vec3d = this.targetShip != null ? Vec3d.ofCenter(this.targetShip.blockPos) : mc.player.getEntityPos();
      ItemEntity next = null;
      double autoElytraLimit = Double.MAX_VALUE;

      for (ItemEntity itemEntity : mc.world.getEntitiesByClass(ItemEntity.class, getBox(vec3d, autoElytraValue), itemEntity2 -> itemEntity2.getStack().isOf(Items.ELYTRA))) {
         double squaredDistanceTo = itemEntity.squaredDistanceTo(mc.player);
         if (squaredDistanceTo < autoElytraLimit) {
            autoElytraLimit = squaredDistanceTo;
            next = itemEntity;
         }
      }

      return next;
   }

   private int getFreeHotbarSlot() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i <= 8; i++) {
         if (inventory.getStack(i).isEmpty()) {
            return i;
         }
      }

      for (int j = 0; j <= 8; j++) {
         if (this.isPlaceableEndBlock(inventory.getStack(j))) {
            return j;
         }
      }

      return inventory.getSelectedSlot() == 0 ? 8 : 0;
   }

   private int findSilkTouchPickaxeSlot() {
      PlayerInventory inventory = mc.player.getInventory();
      Predicate predicate = obj -> ((net.minecraft.item.ItemStack) obj).isIn(ItemTags.PICKAXES) && this.hasEnchantment(((net.minecraft.item.ItemStack) (obj)), Enchantments.SILK_TOUCH);

      for (int i = 0; i < 9; i++) {
         if (predicate.test(inventory.getStack(i))) {
            return i;
         }
      }

      for (int j = 9; j <= 35; j++) {
         if (predicate.test(inventory.getStack(j))) {
            this.swapMatchingItemToHotbar(predicate);
            return -2;
         }
      }

      return -1;
   }

   private int walkAndMineToward(String breakingShipText, Predicate<BlockPos> predicate, BlockPos pos) {
      BlockPos blockPos = this.getFeetBlockPos();
      if (!mc.player.isOnGround()) {
         this.steerTowardAscendStep();
         return 0;
      } else {
         if (this.tickCounter - this.lastBreakPathTick > 1) {
            this.stepStartTick = this.tickCounter;
         }

         this.lastBreakPathTick = this.tickCounter;
         if (predicate.test(blockPos)) {
            this.breakPathSteps = null;
            if (!this.isWithinHorizontalRange(blockPos, 0.22)) {
               this.pressMovementKeysToward(Vec3d.ofBottomCenter(blockPos));
               return 0;
            } else {
               return 1;
            }
         } else {
            if (!breakingShipText.equals(this.breakPathKey)) {
               this.breakPathExtraSolids.clear();
            }

            boolean shipBreakingFlag = this.breakPathSteps == null || !breakingShipText.equals(this.breakPathKey) || this.breakPathIndex >= this.breakPathSteps.size();
            if (!shipBreakingFlag) {
               Step breakingshipitem = this.breakPathSteps.get(this.breakPathIndex);
               int shipBreakingValue = breakingshipitem.mine().isEmpty() ? 100 : 220;
               if (this.tickCounter - this.stepStartTick > shipBreakingValue) {
                  this.breakPathExtraSolids.add(breakingshipitem.feet().asLong());
                  shipBreakingFlag = true;
               }
            }

            if (!shipBreakingFlag
               && this.breakPathIndex > 0
               && !blockPos.equals(this.breakPathSteps.get(this.breakPathIndex - 1).feet())
               && !blockPos.equals(this.breakPathSteps.get(this.breakPathIndex).feet())) {
               shipBreakingFlag = true;
            }

            if (!shipBreakingFlag && this.breakPathIndex == 0 && !blockPos.equals(this.lastPathTargetPos) && !blockPos.equals(this.breakPathSteps.get(0).feet())) {
               shipBreakingFlag = true;
            }

            if (shipBreakingFlag) {
               if (breakingShipText.equals(this.breakPathKey) && this.tickCounter - this.breakAttemptTick < 10) {
                  return this.shipBreakingActive ? -1 : 0;
               }

               this.breakAttemptTick = this.tickCounter;
               this.breakPathKey = breakingShipText;
               this.lastPathTargetPos = blockPos;
               BlockPos add = blockPos.add(-24, -14, -24);
               BlockPos pos4 = blockPos.add(24, 10, 24);
               Result result = ShipPather.getResult(this.breakPathGrid, blockPos, predicate, pos, add, pos4, 12000);
               this.shipBreakingActive = result == null || !result.reached() && result.steps().isEmpty();
               if (this.shipBreakingActive) {
                  this.breakPathSteps = null;
                  return -1;
               }

               this.breakPathSteps = result.steps();
               this.breakPathIndex = 0;
               this.stepStartTick = this.tickCounter;
               if (this.breakPathSteps.isEmpty()) {
                  return result.reached() ? 1 : -1;
               }
            }

            while (this.breakPathIndex < this.breakPathSteps.size() && blockPos.equals(this.breakPathSteps.get(this.breakPathIndex).feet())) {
               this.breakPathIndex++;
               this.stepStartTick = this.tickCounter;
            }

            if (this.breakPathIndex >= this.breakPathSteps.size()) {
               this.breakPathSteps = null;
               return predicate.test(blockPos) ? 1 : 0;
            } else {
               Step breakingshipitem2 = this.breakPathSteps.get(this.breakPathIndex);

               for (BlockPos mineItem : breakingshipitem2.mine()) {
                  if (!this.getBlockState(mineItem).isAir() && this.isSolidOrNonReplaceable(mineItem)) {
                     if (breakingshipitem2.move() == Move.DIG_DOWN && mineItem.equals(blockPos.down()) && !this.isWithinHorizontalRange(blockPos, 0.22)) {
                        this.moveTowardWhileSneaking(Vec3d.ofBottomCenter(blockPos));
                        return 0;
                     }

                     if (!this.isBreakableObstacle(mineItem)) {
                        this.breakPathExtraSolids.add(breakingshipitem2.feet().asLong());
                        this.breakPathSteps = null;
                        return 0;
                     }

                     this.breakBlockWithBestTool(mineItem);
                     return 0;
                  }
               }

               switch (breakingshipitem2.move()) {
                  case WALK:
                  case DESCEND:
                     this.walkToBlockPos(breakingshipitem2.feet(), false);
                     break;
                  case ASCEND:
                     this.walkToBlockPos(breakingshipitem2.feet(), true);
                     break;
                  case DIG_DOWN:
                     this.moveTowardWhileSneaking(Vec3d.ofBottomCenter(blockPos));
               }

               return 0;
            }
         }
      }
   }

   private void updateSilkCarrying() {
      if (++this.silkRetryCount > 6) {
         this.abortStoreAndExit();
      } else {
         this.setStoreStep(1);
      }
   }

   private boolean hasBetterElytraInInventory() {
      PlayerInventory inventory = mc.player.getInventory();
      int maxEquippedDamage = Math.max(0, this.getEquippedElytraDurability());

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(Items.ELYTRA)) {
            int damageValue = stack.getMaxDamage() > 0 ? stack.getMaxDamage() : 432;
            if (damageValue - stack.getDamage() > maxEquippedDamage + 40) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean canReachItemFrameFrom(BlockPos pos, ItemFrameEntity itemFrameEntity, boolean clampFlag) {
      if (this.failedStandPositions.contains(pos.asLong())) {
         return false;
      } else {
         Vec3d vec3d = new Vec3d(pos.getX() + 0.5, pos.getY() + mc.player.getStandingEyeHeight(), pos.getZ() + 0.5);
         Box boundingBox = itemFrameEntity.getBoundingBox();
         double clamp = MathHelper.clamp(vec3d.x, boundingBox.minX, boundingBox.maxX);
         double clamp2 = MathHelper.clamp(vec3d.y, boundingBox.minY, boundingBox.maxY);
         double clamp3 = MathHelper.clamp(vec3d.z, boundingBox.minZ, boundingBox.maxZ);
         return vec3d.squaredDistanceTo(clamp, clamp2, clamp3) > 7.839999999999999
            ? false
            : !clampFlag || this.hasClearLineBetween(vec3d, this.getItemFrameAimPoint(itemFrameEntity, vec3d), pos);
      }
   }

   private boolean hasBlockMatchedForTicks(BlockPos pos2, Predicate<Block> predicate) {
      if (!predicate.test(this.getBlockState(pos2).getBlock())) {
         this.placedAtTick = -1;
         return false;
      } else {
         if (this.placedAtTick < 0) {
            this.placedAtTick = this.tickCounter;
         }

         return this.tickCounter - this.placedAtTick >= 4;
      }
   }

   private boolean tryPlaceBlockAt(BlockPos pos2, Predicate<ItemStack> predicate, Predicate<Block> predicate2) {
      if (predicate2.test(this.getBlockState(pos2).getBlock())) {
         return true;
      } else {
         ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, true);
         int instanceInventory = SelectedInventoryUtils.getInstanceInventory(predicate);
         if (instanceInventory < 0) {
            this.swapMatchingItemToHotbar(predicate);
            return false;
         } else {
            BlockPos down = pos2.down();
            Vec3d add = Vec3d.ofCenter(down).add(0.0, 0.5, 0.0);
            this.lookAtPoint(add, 1);
            if (mc.player.getInventory().getSelectedSlot() != instanceInventory) {
               mc.player.getInventory().setSelectedSlot(instanceInventory);
               return false;
            } else if (mc.player.isSneaking() && this.isLookingTowardPoint(add, 5.0F)) {
               BlockHitResult missed = this.raycastLookDirection(4.0);
               if (missed.getType() == Type.BLOCK && missed.getBlockPos().equals(down) && missed.getSide() == Direction.UP) {
                  mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, missed);
                  mc.player.swingHand(Hand.MAIN_HAND);
               }

               return predicate2.test(this.getBlockState(pos2).getBlock());
            } else {
               return false;
            }
         }
      }
   }

   private Vec3d getBottomCenter() {
      BlockPos blockPos = mc.player.getBlockPos();
      Vec3d wrapDegrees = this.getLookVector(mc.player.getYaw(), 0.0F);
      Vec3d bottomCenter = null;
      double bottomCenterValue = -1.0E9;

      for (int i = 16; i <= 128; i += 8) {
         for (int j = 0; j < 360; j += 15) {
            double angle = Math.toRadians(j);
            int x = blockPos.getX() + (int)Math.round(Math.cos(angle) * i);
            int z = blockPos.getZ() + (int)Math.round(Math.sin(angle) * i);
            int chunkWorld = this.getSurfaceHeight(x, z);
            if (chunkWorld != Integer.MIN_VALUE
               && chunkWorld >= 34
               && chunkWorld <= blockPos.getY() - 2
               && (i > 40 || !(chunkWorld + 1 > mc.player.getY() - i / 9.0 - 1.0))) {
               BlockPos pos = new BlockPos(x, chunkWorld, z);
               if (this.hasCollision(pos) && this.isWideLandingArea(pos) && !ShipLocator.isPurpurOrEndBlock(this.getBlockState(pos))) {
                  Vec3d ofBottomCenter = Vec3d.ofBottomCenter(pos.up());
                  if (mc.world.getEntitiesByClass(ShulkerEntity.class, getBox(ofBottomCenter, 18.0), Entity::isAlive).isEmpty()) {
                     Vec3d subtract = ofBottomCenter.subtract(mc.player.getEntityPos());
                     double bottomCenterValue2 = (subtract.x * wrapDegrees.x + subtract.z * wrapDegrees.z) / Math.max(1.0, Math.hypot(subtract.x, subtract.z));
                     double bottomCenterLimit = bottomCenterValue2 * 40.0 - Math.abs(i - 50) * 0.6 + (this.getBlockState(pos).isOf(Blocks.END_STONE) ? 8 : 0);
                     if (bottomCenterLimit > bottomCenterValue) {
                        bottomCenterValue = bottomCenterLimit;
                        bottomCenter = ofBottomCenter;
                     }
                  }
               }
            }
         }
      }

      return bottomCenter;
   }

   private Ship getNearestLootShip() {
      Ship next = null;
      double autoElytraLimit = Double.MAX_VALUE;

      for (Ship ship : this.shipLocator.getShips()) {
         if (!ship.unreachable && ship.loot != Loot.EMPTY && ship.unreachableCount < 5) {
            double hypot = this.getHorizontalDistanceTo(Vec3d.ofCenter(ship.blockPos));
            if (hypot < autoElytraLimit) {
               autoElytraLimit = hypot;
               next = ship;
            }
         }
      }

      return next;
   }

   private double estimateFallPenalty(Vec3d vec, Vec3d vec2) {
      double y = vec.y;
      double x = vec2.x;
      double y2 = 0.0;
      double z = vec2.z;

      for (int i = 0; i < 14; i++) {
         y2 = (y2 - 0.08) * 0.98;
         Vec3d vec3d = new Vec3d(x, y2, z);
         Vec3d worldCollide = this.getCollisionAdjustedMovement(vec3d, new Box(vec.x - 0.3, vec.y, vec.z - 0.3, vec.x + 0.3, vec.y + 1.8, vec.z + 0.3));
         vec = vec.add(worldCollide);
         if (y - vec.y > 1.2) {
            return 400.0;
         }

         boolean autoElytraFlag = worldCollide.y != vec3d.y && y2 < 0.0;
         if (autoElytraFlag) {
            y2 = 0.0;
         }

         double autoElytraFactor = autoElytraFlag ? 0.546 : 0.91;
         x = worldCollide.x != vec3d.x ? 0.0 : x * autoElytraFactor;
         z = worldCollide.z != vec3d.z ? 0.0 : z * autoElytraFactor;
         if (autoElytraFlag && Math.hypot(x, z) < 0.02) {
            return 0.0;
         }
      }

      return 0.0;
   }

   private boolean updateLowAltitudeRecovery() {
      if (this.isGliding() && this.currentState != AutoElytraFinder.State.TAKEOFF) {
         double y = mc.player.getY();
         if (this.currentState != AutoElytraFinder.State.LANDING && this.currentState != AutoElytraFinder.State.LAND
            || this.landingSpot == null
            || !(y > this.landingSpot.y + 0.5)
            || !this.landActive && (!(y > this.landingSpot.y + 3.0) || !(this.getHorizontalDistanceTo(this.landingSpot) < 70.0))) {
            boolean playerYawFlag = mc.player.getVelocity().y < -0.05;
            if (y < 30.0 || playerYawFlag && this.getHorizontalSpeed() < 0.4 && this.isColumnBelowEmpty(mc.player.getBlockPos(), 40)) {
               if (y >= 30.0 && this.getTicksInState() < 20) {
                  return false;
               } else if (!this.hasFireworkRocket()) {
                  if (this.countFireworkRockets() > 0) {
                     this.swapMatchingItemToHotbar(this::isPlainFirework);
                  }

                  this.requestLookTarget(mc.player.getYaw(), 12.0F, 2);
                  return true;
               } else {
                  Float float2 = this.findClearYawOffset(mc.player.getYaw(), 12.0);
                  float playerYawValue = float2 != null ? float2 : mc.player.getYaw();
                  this.requestLookTarget(playerYawValue, -38.0F, 2);
                  if (!this.isFireworkBoosting() && this.canUseFirework() && mc.player.getPitch() < -20.0F) {
                     this.useFireworkRocket();
                  }

                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean hasFreeInventorySlots(int inventoryStackValue) {
      PlayerInventory inventory = mc.player.getInventory();
      int inventoryStackCounter = 0;

      for (int i = 0; i <= 35; i++) {
         if (inventory.getStack(i).isEmpty()) {
            inventoryStackCounter++;
         }
      }

      return inventoryStackCounter >= inventoryStackValue;
   }

   private void walkNearBlock(BlockPos pos) {
      int shipBreaking = this.walkAndMineToward(
         "near" + pos.asLong(),
         targetx -> Vec3d.ofCenter(targetx.up()).squaredDistanceTo(Vec3d.ofCenter(pos)) <= 10.240000000000002 && !targetx.equals(pos),
         pos
      );
      if (shipBreaking < 0) {
         this.pressMovementKeysToward(Vec3d.ofCenter(pos));
      }
   }

   private void planGlideTowardTarget(Vec3d vec) {
      double hypot = this.getHorizontalDistanceTo(vec);
      float autoElytraValue = hypot > 2.5 ? this.getYawToward(vec) : mc.player.getYaw();
      double amount = Double.MAX_VALUE;
      float floatValue = autoElytraValue;
      float pitchTableDegreesItem2 = 0.0F;
      boolean flag = false;

      for (float yawSweepDegreesItem : YAW_SWEEP_DEGREES) {
         for (float pitchTableDegreesItem : PITCH_TABLE_DEGREES) {
            double autoElytraValue4 = this.simulateGlideLandingCost(autoElytraValue + yawSweepDegreesItem, pitchTableDegreesItem, vec, -1, pitchTableDegreesItem) + Math.abs(yawSweepDegreesItem) * 0.15;
            if (autoElytraValue4 < amount) {
               amount = autoElytraValue4;
               floatValue = autoElytraValue + yawSweepDegreesItem;
               pitchTableDegreesItem2 = pitchTableDegreesItem;
               flag = this.shipPlayerActive;
            }
         }
      }

      if (hypot > 8.0 && (!flag || !(amount < 40.0))) {
         for (float yawOffsetsDegreesItem : YAW_OFFSETS_DEGREES) {
            for (float pitchAnglesDegreesItem : PITCH_ANGLES_DEGREES) {
               for (int scanDistancesItem : SCAN_DISTANCES) {
                  for (float steepPitchAnglesDegreesItem : STEEP_PITCH_ANGLES_DEGREES) {
                     double autoElytraValue9 = this.simulateGlideLandingCost(autoElytraValue + yawOffsetsDegreesItem, pitchAnglesDegreesItem, vec, scanDistancesItem, steepPitchAnglesDegreesItem) + Math.abs(yawOffsetsDegreesItem) * 0.15 + 2.0;
                     if (autoElytraValue9 < amount) {
                        amount = autoElytraValue9;
                        floatValue = autoElytraValue + yawOffsetsDegreesItem;
                        pitchTableDegreesItem2 = pitchAnglesDegreesItem;
                        flag = this.shipPlayerActive;
                     }
                  }
               }
            }
         }
      }

      this.plannedYaw = floatValue;
      this.plannedPitch = pitchTableDegreesItem2;
      this.flightPlanDistance = amount;
      this.landActive = flag;
   }

   private boolean isChestElytraMending() {
      return this.hasEnchantment(mc.player.getEquippedStack(EquipmentSlot.CHEST), Enchantments.MENDING);
   }

   private Float findClearYawOffset(float floatValue, double offsetUnionBound) {
      for (float floatValue3 : new float[]{0.0F, 30.0F, -30.0F, 60.0F, -60.0F, 90.0F, -90.0F, 130.0F, -130.0F}) {
         float degrees = floatValue + floatValue3;
         if (!this.isPathBlockedAlong(this.getLookVector(degrees, -25.0F), offsetUnionBound) && !this.isPathBlockedAlong(this.getLookVector(degrees, 0.0F), offsetUnionBound * 0.8)) {
            return degrees;
         }
      }

      return null;
   }

   private static Box getPlayerBoxAt(double boxValue, double minY, double boxValue2) {
      return new Box(boxValue - 0.3, minY, boxValue2 - 0.3, boxValue + 0.3, minY + 0.6, boxValue2 + 0.3);
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      if (this.statusCard.isEnabled() && mc.currentScreen == null) {
         this.elytraStatusCard.renderCard(tickProgress.drawContext, this.elytraStatus);
      }
   }

   private void walkToOpenSide() {
      BlockPos floored = this.getFeetBlockPos();

      for (Direction direction : net.minecraft.util.math.Direction.Type.HORIZONTAL) {
         BlockPos offset = floored.offset(direction);
         if (!this.hasCollision(offset) && !this.hasCollision(offset.up()) && this.isColumnBelowEmpty(offset, 12)) {
            float degreesHorizontalValue = direction.getPositiveHorizontalDegrees();
            this.requestLookTarget(degreesHorizontalValue, 15.0F, 0);
            if (Math.abs(MathHelper.wrapDegrees(degreesHorizontalValue - mc.player.getYaw())) < 20.0F) {
               mc.options.forwardKey.setPressed(true);
            }

            return;
         }
      }
   }

   private int getMinimumFireworkCount() {
      return Math.max(4, this.minFireworkCountSetting.getValue());
   }

   public void renderStatusElytra(DrawContext context) {
      if (this.statusCard.isEnabled()) {
         this.elytraStatusCard.renderCard(context, this.elytraStatus);
      }
   }

   private void beginShipLooting() {
      if (this.breakingTargetShip != this.targetShip || this.retrieveStartTick < 0) {
         this.breakingTargetShip = this.targetShip;
         this.retrieveStartTick = this.tickCounter;
         this.elytraCountTarget = this.getInventoryLoadEstimate();
         this.minElytraInInventory = this.startElytraCount < 0 ? this.countElytraInInventory() : Math.min(this.countElytraInInventory(), this.startElytraCount);
         this.excludedPlacePositions.clear();
      }

      this.lastFrameHitTick = -1000;
      this.frameMissingSinceTick = -1;
      this.frameAnyUntilTick = -1000;
      this.targetShulkerId = null;
      this.shulkerFightTargetPos = null;
      this.shulkerFightTick = -1000;
      this.retrieveStatusText = "Breaking into the ship";
      this.breakPathSteps = null;
      this.breakPathExtraSolids.clear();
      this.failedStandPositions.clear();
      this.enterState(AutoElytraFinder.State.RETRIEVE);
   }

   private boolean isShulkerBoxOfOnlyElytra(ItemStack stack) {
      if (!this.canBlock(stack)) {
         return false;
      } else {
         ContainerComponent containerComponent = stack.get(DataComponentTypes.CONTAINER);
         if (containerComponent == null) {
            return false;
         } else {
            boolean iterateNon = false;

            for (ItemStack itemStack : containerComponent.iterateNonEmpty()) {
               if (!itemStack.isOf(Items.ELYTRA)) {
                  return false;
               }

               iterateNon = true;
            }

            return iterateNon;
         }
      }
   }

   private int countInventoryItem(Item item) {
      PlayerInventory inventory = mc.player.getInventory();
      int inventoryStack = 0;

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(item)) {
            inventoryStack += stack.getCount();
         }
      }

      return inventoryStack;
   }

   private void escapeBoxedIn() {
      BlockPos floored = this.getFeetBlockPos();
      if (this.hasCollision(floored.up(2))) {
         if (this.isBreakableObstacle(floored.up(2))) {
            this.breakBlockWithBestTool(floored.up(2));
         } else {
            this.pauseWithReason("boxed in - can't get out");
         }
      } else {
         int boxedBlocksValue = this.findPillarBlockSlot();
         if (boxedBlocksValue != -2) {
            if (boxedBlocksValue < 0) {
               this.pauseWithReason("no blocks to pillar out with");
            } else {
               this.pillarUpward(boxedBlocksValue);
            }
         }
      }
   }

   private int countFireworkRockets() {
      PlayerInventory inventory = mc.player.getInventory();
      int stackHand = 0;

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (this.isPlainFirework(stack)) {
            stackHand += stack.getCount();
         }
      }

      if (this.hasFireworkInOffhand()) {
         stackHand += mc.player.getOffHandStack().getCount();
      }

      return stackHand;
   }

   @Override
   public void onReset() {
      if (mc.player != null
         && mc.world != null
         && mc.interactionManager != null
         && mc.player.networkHandler != null) {
         if (this.endNeedText != null) {
            String endNeedText = this.endNeedText;
            this.endNeedText = null;
            this.notifyStatus(AutoElytraFinder.Kind.STOP, endNeedText);
            this.setEnabled(false);
         } else if (!mc.player.isAlive()) {
            this.notifyStatus(AutoElytraFinder.Kind.STOP, "You died - stopped.");
            this.setEnabled(false);
         } else if (!this.isInEnd()) {
            this.notifyStatus(AutoElytraFinder.Kind.STOP, "Left The End - stopped.");
            this.setEnabled(false);
         } else {
            this.tickCounter++;
            if (this.currentState != AutoElytraFinder.State.PAUSED) {
               this.releaseMovementKeys();
            }

            this.lookTargetRequested = false;
            this.breakingBlockActive = false;
            if (this.inventorySwapPending && !ExtUtils.isAction()) {
               this.inventorySwapPending = false;
            }

            if (this.confirmScreenHelper.isRunningSuccess()) {
               this.confirmScreenHelper.updateTimedBought();
            }

            this.shipLocator.scanChunkBlocks(mc, Math.max(4, mc.options.getClampedViewDistance()), 8);
            if (mc.player.isOnGround() && !mc.player.isTouchingWater()) {
               this.fallbackTargetPos = mc.player.getEntityPos();
            }

            if (!this.replaceBrokenElytraMidFlight() && !this.handleFallingWithoutGliding() && !this.updateLowAltitudeRecovery() && !this.handleEndermanThreat() && !this.checkElytraDurability()) {
               switch (this.currentState) {
                  case PREPARING:
                     this.tickPreparingState();
                     break;
                  case TAKEOFF:
                     this.updateTakeoffState();
                     break;
                  case CRUISE:
                     this.updateCruiseState();
                     break;
                  case TO_CITY:
                     this.updateToCityState();
                     break;
                  case TO_SHIP:
                     this.tickToShipState();
                     break;
                  case LANDING:
                     this.tickLandingState();
                     break;
                  case RETRIEVE:
                     this.tickRetrieveState();
                     break;
                  case STORE:
                     this.tickStoreState();
                     break;
                  case RECOVER_CHEST:
                     this.tickRecoverChestState();
                     break;
                  case EXIT:
                     this.tickTakeoffState();
                     break;
                  case LAND:
                     this.tickCruiseState();
                     break;
                  case MENDING:
                     this.updateElytraRepairing();
                     break;
                  case BUY:
                     this.tickBuyState();
                     break;
                  case PAUSED:
                     this.tickPausedState();
               }
            }

            if (!this.breakingBlockActive) {
               this.cancelBlockBreaking();
            }

            this.applyRotationOverride();
            if (this.tickCounter % 5 == 0) {
               this.refreshStatusCard();
            }
         }
      }
   }

   private boolean hasInventoryItemMatching(Predicate<ItemStack> predicate) {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i <= 35; i++) {
         if (predicate.test(inventory.getStack(i))) {
            return true;
         }
      }

      return false;
   }

   private boolean canBlock(ItemStack stack) {
      return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
   }

   private boolean breakBlockAt(BlockPos blockPos, boolean blockPosFlag, boolean blockPosFlag2) {
      if (blockPos == null) {
         return false;
      } else {
         BlockState blockState = this.getBlockState(blockPos);
         if (blockState.isAir()) {
            this.cancelBlockBreaking();
            return true;
         } else if (!blockPosFlag2 && blockState.hasBlockEntity()) {
            this.cancelBlockBreaking();
            return false;
         } else {
            Vec3d add = this.findVisibleBlockFacePoint(blockPos);
            if (add == null) {
               add = Vec3d.ofCenter(blockPos);
            }

            this.lookAtPoint(add, 1);
            if (blockPosFlag && !this.ensureBestToolSelected(blockPos)) {
               return false;
            } else if (!blockPos.equals(this.blockPosInteractionCancel) && !this.isLookingTowardPoint(add, 6.0F)) {
               return false;
            } else {
               BlockHitResult missed = this.raycastLookDirection(4.0);
               if (missed.getType() != Type.BLOCK) {
                  return false;
               } else {
                  BlockPos pos = missed.getBlockPos();
                  if (!pos.equals(blockPos)) {
                     Vec3d eyePos = mc.player.getEyePos();
                     boolean blockPosFlag3 = eyePos.squaredDistanceTo(missed.getPos()) < eyePos.squaredDistanceTo(add) - 0.05;
                     BlockPos floored = this.getFeetBlockPos();
                     if (blockPosFlag2 || !blockPosFlag3 || pos.equals(floored.down()) || pos.equals(floored) || pos.equals(floored.up()) || !this.isBreakableObstacle(pos)) {
                        return false;
                     }

                     blockPos = pos;
                  }

                  if (!blockPos.equals(this.blockPosInteractionCancel)) {
                     this.cancelBlockBreaking();
                     this.breakingSide = missed.getSide();
                  }

                  if (mc.interactionManager.updateBlockBreakingProgress(blockPos, this.breakingSide)) {
                     mc.player.swingHand(Hand.MAIN_HAND);
                  }

                  this.interactionCancelActive = true;
                  this.blockPosInteractionCancel = blockPos;
                  this.breakingBlockActive = true;
                  return false;
               }
            }
         }
      }
   }

   private int getInventoryLoadEstimate() {
      return this.countElytraInInventory() + (this.isWearingElytra() ? 1 : 0);
   }

   private String getServerAddress() {
      ServerInfo currentServerEntry = mc.getCurrentServerEntry();
      if (currentServerEntry != null) {
         return currentServerEntry.address;
      } else {
         IntegratedServer server = mc.getServer();
         return server != null ? "sp:" + server.getSaveProperties().getLevelName() : "?";
      }
   }

   private boolean isNearVisitedCity(Vec3d vec) {
      if (vec == null) {
         return false;
      } else {
         for (Vec3d vec3d : this.knownStructurePositions) {
            double deltaX = vec3d.x - vec.x;
            double deltaZ = vec3d.z - vec.z;
            if (deltaX * deltaX + deltaZ * deltaZ < 14400.0) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean shouldLandForRockets() {
      int stackHand = this.countFireworkRockets();
      boolean rocketsBuyFlag = stackHand <= 3;
      if (rocketsBuyFlag || stackHand < this.getMinimumFireworkCount() && this.tickCounter >= this.buyRetryNotBeforeTick) {
         this.job = AutoElytraFinder.Job.BUY_ROCKETS;
         this.notifyStatus(AutoElytraFinder.Kind.INFO, "Low on rockets - landing to buy more");
         this.enterState(AutoElytraFinder.State.LAND);
         return true;
      } else {
         return false;
      }
   }

   private boolean eatIfHungry() {
      HungerManager hungerManager = mc.player.getHungerManager();
      if (mc.player.isUsingItem() && this.tickCounter < this.eatingUntilTick) {
         mc.options.useKey.setPressed(true);
         return true;
      } else {
         boolean playerElytraFlag = hungerManager.getFoodLevel() <= 14 || mc.player.getHealth() < 14.0F && hungerManager.getFoodLevel() < 20;
         if (playerElytraFlag && !this.isContainerScreenOpen()) {
            if (!this.isGliding() || !(mc.player.getY() < 60.0) && !(this.getHorizontalSpeed() < 0.7) && !this.isFireworkBoosting()) {
               int instanceInventory = SelectedInventoryUtils.getInstanceInventory(this::isSafeFood);
               if (instanceInventory < 0) {
                  return this.swapMatchingItemToHotbar(this::isSafeFood);
               } else if (mc.player.getInventory().getSelectedSlot() != instanceInventory) {
                  mc.player.getInventory().setSelectedSlot(instanceInventory);
                  return true;
               } else {
                  HitResult crosshairTarget = mc.crosshairTarget;
                  boolean playerElytraFlag2 = crosshairTarget instanceof EntityHitResult
                     || crosshairTarget instanceof BlockHitResult blockHitResult && crosshairTarget.getType() == Type.BLOCK && this.getBlockState(blockHitResult.getBlockPos()).hasBlockEntity();
                  if (playerElytraFlag2) {
                     this.requestLookTarget(mc.player.getYaw(), this.isGliding() ? -10.0F : -70.0F, 1);
                     return true;
                  } else {
                     mc.options.useKey.setPressed(true);
                     if (!mc.player.isUsingItem()) {
                        this.eatingUntilTick = this.tickCounter + 45;
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
   }

   private int getSurfaceHeight(int chunkWorldValue, int z) {
      WorldChunk worldChunk = mc.world.getChunkManager().getWorldChunk(chunkWorldValue >> 4, z >> 4);
      if (worldChunk == null) {
         return Integer.MIN_VALUE;
      } else {
         int sampleHeightmap = worldChunk.sampleHeightmap(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, chunkWorldValue & 15, z & 15);
         return sampleHeightmap <= mc.world.getBottomY() ? Integer.MIN_VALUE : sampleHeightmap;
      }
   }

   private boolean shouldFightShulker(ShulkerEntity shulkerEntity) {
      if (this.combatShulkers.isEnabled() && this.tickCounter >= this.shulkerFightTick) {
         double distanceTo = shulkerEntity.getEyePos().distanceTo(mc.player.getEyePos());
         if (this.targetShulkerId != null && this.targetShulkerId == shulkerEntity.getId() && this.tickCounter - this.guardFightTick < 40 && distanceTo < 9.0) {
            return true;
         } else {
            return this.hasLevitationEffect() && distanceTo < 20.0 ? true : distanceTo < 7.5 && this.hasLineOfSight(shulkerEntity.getBoundingBox().getCenter());
         }
      } else {
         return false;
      }
   }

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      if (this.rotationOverrideActive && mc.player != null) {
         float tickDelta = deltaAngle.getTickDelta();
         deltaAngle.setAngleDegrees(MathHelper.lerpAngleDegrees(tickDelta, this.yawBeforeOverride, this.appliedYaw));
         deltaAngle.setLerp(MathHelper.lerp(tickDelta, this.pitch, this.appliedPitch));
         deltaAngle.updateFlag();
      }
   }

   private boolean hasEnchantment(ItemStack itemStack, RegistryKey<Enchantment> registryKey) {
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

   private void updateCruiseState() {
      if (!this.tryContinueOnShip()) {
         if (this.ensureAirborneOrReset()) {
            if (!this.shouldLandForRockets()) {
               if (!this.isShipStored()) {
                  Ship next = this.getNearestLootShip();
                  if (next != null) {
                     this.beginShipApproach(next);
                  } else if (this.findNewShipCandidate()) {
                     this.notifyStatus(AutoElytraFinder.Kind.INFO, "End City spotted");
                     this.cityWaitStartTick = -1;
                     this.enterState(AutoElytraFinder.State.TO_CITY);
                  } else {
                     if (this.tickCounter - this.lastCitySpotTick > 220) {
                        this.lastCitySpotTick = this.tickCounter;
                        this.searchHeadingYaw = this.searchHeadingYaw + (mc.player.getRandom().nextFloat() - 0.5F) * 40.0F;
                     }

                     if (this.isNearVisitedCity(mc.player.getEntityPos().add(this.getLookVector(this.searchHeadingYaw, 0.0F).multiply(120.0)))) {
                        this.searchHeadingYaw += 2.5F;
                     }

                     boolean playerElytra = this.eatIfHungry();
                     this.steerFlight(this.searchHeadingYaw, this.flightAltitudeSetting.getValue(), !playerElytra, 0.0);
                  }
               }
            }
         }
      }
   }

   private boolean breakBlockWithBestTool(BlockPos pos2) {
      return this.breakBlockAt(pos2, true, false);
   }

   private float adjustYawAroundShip(float shipMultiply, Vec3d vec) {
      Vec3d center = this.targetShip.getCenter();
      double y = mc.player.getY();
      if (!(y > center.y + 18.0) && !(y < center.y - 3.0)) {
         Vec3d entityPos = mc.player.getEntityPos();
         double deltaX = vec.x - entityPos.x;
         double deltaZ = vec.z - entityPos.z;
         double shipMultiplyDivisor = deltaX * deltaX + deltaZ * deltaZ;
         if (shipMultiplyDivisor < 1.0E-6) {
            return shipMultiply;
         } else {
            double clamp = MathHelper.clamp(((center.x - entityPos.x) * deltaX + (center.z - entityPos.z) * deltaZ) / shipMultiplyDivisor, 0.0, 1.0);
            double shipMultiplyFactor = entityPos.x + deltaX * clamp - center.x;
            double shipMultiplyFactor2 = entityPos.z + deltaZ * clamp - center.z;
            if (shipMultiplyFactor * shipMultiplyFactor + shipMultiplyFactor2 * shipMultiplyFactor2 > 81.0) {
               return shipMultiply;
            } else {
               Vec3d offset = getHorizontalDirectionVector(this.targetShip.rightDirection);
               double shipMultiplyLimit = (entityPos.x - center.x) * offset.x + (entityPos.z - center.z) * offset.z;
               Vec3d add = center.add(offset.multiply(shipMultiplyLimit >= 0.0 ? 13.0 : -13.0));
               return this.getHorizontalDistanceTo(add) < 4.0 ? shipMultiply : this.getYawToward(add);
            }
         }
      } else {
         return shipMultiply;
      }
   }

   private void attackShulkerBullet(ShulkerBulletEntity shulkerBulletEntity) {
      Vec3d add = shulkerBulletEntity.getBoundingBox().getCenter().add(shulkerBulletEntity.getVelocity());
      this.lookAtPoint(add, 3);
      if (this.getEyeDistanceToBox(shulkerBulletEntity) <= 2.9 && this.isLookingTowardPoint(add, 12.0F) && this.isEntityInReach(shulkerBulletEntity)) {
         this.tryAttackEntity(shulkerBulletEntity);
      }
   }

   private boolean canFloorContract(int floorContractValue) {
      Box contract = mc.player.getBoundingBox().contract(0.02, 0.0, 0.02);
      int floorMinX = MathHelper.floor(contract.minX);
      int floorMaxX = MathHelper.floor(contract.maxX);
      int floorMinZ = MathHelper.floor(contract.minZ);
      int floorMaxZ = MathHelper.floor(contract.maxZ);
      int y = this.getFeetBlockPos().getY();

      for (int i = floorMinX; i <= floorMaxX; i++) {
         for (int j = floorMinZ; j <= floorMaxZ; j++) {
            for (int k = y; k <= y + floorContractValue; k++) {
               if (this.hasCollision(new BlockPos(i, k, j))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private boolean fightNearbyEnderman() {
      EndermanEntity next = this.findNearestAngryEnderman(6.0);
      if (next == null) {
         return false;
      } else {
         Vec3d add = next.getBoundingBox().getCenter().add(0.0, -0.4, 0.0);
         this.lookAtPoint(add, 3);
         if (this.getEyeDistanceToBox(next) <= 2.9) {
            if (!this.equipBestSword()) {
               return true;
            }

            if (mc.player.getAttackCooldownProgress(0.0F) >= 0.9F && this.isLookingTowardPoint(add, 10.0F) && this.isEntityInReach(next)) {
               this.tryAttackEntity(next);
            }
         }

         return true;
      }
   }

   private Vec3d getItemFrameAimPoint(ItemFrameEntity itemFrameEntity, Vec3d vec) {
      Vec3d center = itemFrameEntity.getBoundingBox().getCenter();
      Vec3d subtract = vec.subtract(center);
      double varLength = subtract.length();
      return varLength < 0.001 ? center : center.add(subtract.multiply(0.04 / varLength));
   }

   private static enum Buy {
      NONE,
      CHEST,
      SHULKER,
      ROCKETS;
   }

   private static enum Job {
      NONE,
      MEND,
      BUY_ROCKETS,
      STOP,
      SWAP;
   }

   private static enum Kind {
      INFO,
      GOOD,
      WARN,
      STOP;
   }

   public static enum State {
      PREPARING,
      TAKEOFF,
      CRUISE,
      TO_CITY,
      TO_SHIP,
      LANDING,
      RETRIEVE,
      STORE,
      RECOVER_CHEST,
      EXIT,
      LAND,
      MENDING,
      BUY,
      PAUSED;
   }
}
