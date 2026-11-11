package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.entity.ImmutablePos;
import com.corz.client.entity.ImmutablePos.Kind;
import com.corz.client.entity.TargetSelfDataTarget;
import com.corz.client.entity.UnsafeEntityUtils;
import com.corz.client.entity.UnsafeEntityUtils.Forced;
import com.corz.client.entity.UnsafeEntityUtils.Plan;
import com.corz.client.entity.UnsafeEntityUtils.Request;
import com.corz.client.gui.TickProgress;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.BlockEntityUtils.Predicted;
import com.corz.client.inventory.BlockEntityUtils.Support;
import com.corz.client.inventory.SelectedInventoryUtils;
import com.corz.client.mixins.AccessorKeyBinding;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.ClampMath;
import com.corz.client.world.ForcedFaceData;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.TickStatusData;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.combat.SilentAim;
import com.corz.client.categories.combat.AnchorMacro;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.categories.combat.SafeAnchor;
import com.corz.client.utils.CrystalSelectedApi;
import com.corz.client.utils.HandleTime;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.WeaknessGodApi;

public class CrystalAura extends Module implements WeaknessGodApi {
   private ClientWorld world;
   private boolean obsidianSwapActive;
   private CrystalSelectedApi weakness;
   private final HandleTime actionTracker;
   private LivingEntity currentTarget;
   private final EnumSetting modeSetting = new EnumSetting(
      "Mode",
      "Manual: hold right-click with a crystal on obsidian and it places and breaks for you. Auto: picks a nearby player and does it all by itself.",
      "Manual",
      "Manual",
      "Auto"
   );
   private int aimAttemptCount;
   private BlockPos deathPos;
   private final IntegerSetting minEfficiencySetting;
   private long lastAttackTick;
   private long lastAimTick;
   private TargetSelfDataTarget controllerTargetData;
   private int highlightColor;
   private long lastSlotSwapTick;
   private long lastBreakTick;
   private long lastHoldTick;
   private BlockPos lastBreakBase;
   private long lastPlaceTick;
   private float lastSampleYaw;
   private String paused;
   private BlockPos highlightedPos;
   private long idleSinceMs;
   private long placeAllowedAfterMs;
   private final BooleanSetting antiSuicide;
   private static final int UNUSED_ONE = 1;
   private int swappedSlot;
   private long wantedSlotRequestTick;
   private int idleTickCount;
   private long idleStartDelayMs;
   private Object controllerOwner;
   private float lastAimYaw;
   private long lastTargetSeenMs;
   private String idle;
   private final DoubleSetting rangeSetting = new DoubleSetting("Range", "How far away a player can be to get targeted (Auto only).", 4.5, 3.0, 6.0, 0.1);
   private int lastActionPacketAge;
   private float lastSamplePitch;
   private static final double[] TOP_FACE_SAMPLE_OFFSETS = new double[]{0.25, 0.5, 0.75};
   private final BooleanSetting autoObsidian;
   private double lastYawChange;
   private boolean hasRotationSample;
   private float lastAimPitch;
   private long retryAfterTick;
   private boolean performingOwnAction;
   private ClientPlayerEntity player;
   private long lastActionTick;
   private boolean slotSwapActive;
   private long deathTimeMs;
   private boolean restoringAim;
   private ImmutablePos currentAction;
   private final BooleanSetting centerScreen;
   private int lastSlotPacketAge;
   private final BooleanSetting stopOnLoot;
   private final BooleanSetting showTarget;
   private int wantedSlot;
   private int lastPacketSelectedSlot;
   private final IntegerSetting maxSelfDamage;
   private int lastAttackedEntityId;
   private final DoubleSetting aimSpeed = new DoubleSetting(
      "Aim Speed", "How fast Auto turns your aim to the crystal. Higher is faster but less human.", 4.5, 1.0, 10.0, 0.5
   );

   static BlockPos resolveTargetBase(boolean blockPosFlag, BlockPos pos, BlockPos pos2) {
      return blockPosFlag && pos != null && pos2 != null && pos2.equals(pos.up()) ? pos2 : pos;
   }

   private void interactBlockAndRecord(BlockHitResult blockHitResult, BlockPos pos, long crystalAuraValue, long swingHandValue, float handStackValue, float f2) {
      this.handleSwingHand(blockHitResult, pos, crystalAuraValue, swingHandValue, handStackValue, f2, false);
   }

   private float[] getAimStep(float weaknessValue, float clamp, float f, float boxLengthBound, boolean weaknessFlag) {
      return this.weakness == null
         ? BoxLengthUtils.getAmounts(weaknessValue, clamp, f, boxLengthBound, this.aimSpeed.getValue(), randomAimFactor(), ClampMath.getSensitivityMouse())
         : this.weakness.getTrackAction(weaknessValue, clamp, f, boxLengthBound, weaknessFlag);
   }

   public void renderWhenControlled(QueueCommand queueCommand) {
      if (this.weakness != null && !this.isEnabled()) {
         this.onRender3D(queueCommand);
      }
   }

   private void executePlacement(
      ImmutablePos immutablePos, BlockHitResult blockHitResult, boolean posBlockFlag, boolean posBlockFlag2, Vec3d vec, float posBlockValue, float f2, long yourCenterTime, long longValue, int weaknessValue
   ) {
      boolean weakness = this.isControllerFlagSet();
      if (!weakness && this.lastYawChange > 1.9) {
         float[] posBlockValues = BlockEntityUtils.getAmounts(vec, posBlockValue, f2, blockHitResult.getBlockPos(), posBlockFlag ? null : immutablePos.getUp(), ClampMath.getSensitivityMouse());
         if (posBlockValues == null) {
            float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, BlockEntityUtils.getVec3d(blockHitResult.getBlockPos(), blockHitResult.getSide()));
            posBlockValues = this.getAimStep(posBlockValue, f2, wrapDegrees[0], wrapDegrees[1], true);
         }

         this.applyAim(posBlockValues[0], posBlockValues[1], yourCenterTime);
      } else {
         boolean posBlockFlag3 = posBlockFlag
            || !FloorMathUtilsBlock.canInstanceWorld(blockHitResult.getBlockPos())
               && !FloorMathUtilsBlock.canInterpolatorLerped(immutablePos.getImmutable(), FloorMathUtilsBlock.getValueInt())
               && !FloorMathUtilsBlock.canCenterBlast(blockHitResult.getBlockPos());
         if (posBlockFlag2
            && this.actionTracker.canTimeFlag(yourCenterTime)
            && (weakness || !this.actionTracker.canAmountTime(posBlockValue, f2, longValue))
            && (posBlockFlag ? BlockEntityUtils.canEntitiesInstance(immutablePos.getImmutable()) : BlockEntityUtils.isEmpty(immutablePos.getImmutable()))
            && posBlockFlag3
            && this.isControllerAllowingAction(immutablePos)
            && this.isSlotChangeAllowed(weaknessValue)) {
            boolean time4 = !immutablePos.isTime();
            this.handleSwingHand(blockHitResult, posBlockFlag ? immutablePos.getImmutable() : null, yourCenterTime, longValue, posBlockValue, f2, weakness);
            if (!posBlockFlag) {
               this.actionTracker.handle(immutablePos.getImmutable().asLong(), yourCenterTime);
               immutablePos.handleTime(yourCenterTime);
            } else if (time4) {
               immutablePos.handleTime(yourCenterTime);
            }
         }

         this.applyAim(posBlockValue, f2, yourCenterTime);
      }
   }

   private void resetIdleTimer(long time) {
      this.idleSinceMs = time;
      this.idleStartDelayMs = randomStartDelayMs();
   }

   private void clearCurrentAction(long crystalsWeaknessValue) {
      this.currentAction = null;
      this.resetIdleTimer(getNowMs());
      if (this.weakness == null && !this.restoringAim && this.lastAimTick >= crystalsWeaknessValue - 1L) {
         this.restoringAim = true;
         this.aimAttemptCount = 0;
      }
   }

   private void runAutoTick(long weaknessPausedValue, long time) {
      ClientPlayerEntity player = mc.player;
      if ((this.weakness != null || !this.isUseKeyHeld()) && !player.isUsingItem() && !isPausedByAnchorModule()) {
         if (this.weakness == null && this.stopOnLoot.isEnabled() && this.isRelevantLootNearby(time)) {
            this.setIdleReason(weaknessPausedValue, "loot");
         } else {
            boolean weaknessPausedFlag = SelectedInventoryUtils.getValue(Items.END_CRYSTAL) >= 0;
            if (!weaknessPausedFlag && this.weakness == null) {
               this.setIdleReason(weaknessPausedValue, "no crystals");
            } else if (!BlockEntityUtils.isAmplifierEffect()) {
               this.setIdleReason(weaknessPausedValue, "weakness");
            } else {
               LivingEntity livingEntity = this.weakness == null ? this.getNearestTarget() : this.getControllerTarget();
               if (livingEntity == null) {
                  this.setIdleReason(weaknessPausedValue, "no target");
               } else {
                  if (livingEntity != this.currentTarget) {
                     this.currentTarget = livingEntity;
                     if (this.currentAction != null) {
                        this.clearCurrentAction(weaknessPausedValue);
                     }
                  }

                  this.lastTargetSeenMs = time;
                  boolean interpolatorFlag = this.lastAttackedEntityId == livingEntity.getId() && weaknessPausedValue - this.lastAttackTick <= 1L;
                  Predicted interpolatorInstance = BlockEntityUtils.getInterpolatorInstance(livingEntity, this.getPredictTicks(), interpolatorFlag, this.weakness != null);
                  ForcedFaceData forcedFaceData = this.weakness != null && this.controllerTargetData != null ? this.controllerTargetData.directive() : null;
                  if (forcedFaceData != null) {
                     this.runControllerForcedAction(forcedFaceData, livingEntity, forcedFaceData.targetAt() != null ? forcedFaceData.targetAt() : interpolatorInstance, weaknessPausedValue, time, weaknessPausedFlag);
                  } else {
                     if (this.currentAction != null && !this.isCurrentActionStillValid(this.currentAction, livingEntity, interpolatorInstance, weaknessPausedValue, time)) {
                        this.clearCurrentAction(weaknessPausedValue);
                     }

                     if (this.currentAction == null) {
                        if (this.weakness == null && time - this.idleSinceMs < this.idleStartDelayMs || weaknessPausedValue < this.retryAfterTick) {
                           this.idleTickCount++;
                           this.continueAimRestore(weaknessPausedValue);
                           return;
                        }

                        Plan tuningElite = UnsafeEntityUtils.getTuningElite(
                           this.buildPlanRequest(livingEntity, interpolatorInstance, weaknessPausedValue, time, this.weakness == null ? null : this.controllerTargetData)
                        );
                        if (tuningElite.action() == null) {
                           this.retryAfterTick = weaknessPausedValue + (this.weakness != null ? 1 : 2);
                           this.setIdleReason(weaknessPausedValue, tuningElite.status());
                           return;
                        }

                        this.currentAction = tuningElite.action();
                        this.restoringAim = false;
                     }

                     if (!weaknessPausedFlag && !this.canExecuteAction(this.currentAction, time)) {
                        this.setIdleReason(weaknessPausedValue, "no crystals");
                     } else {
                        this.paused = null;
                        this.idleTickCount = 0;
                        this.executeCurrentAction(this.currentAction, weaknessPausedValue, time);
                     }
                  }
               }
            }
         }
      } else {
         this.cancelActionAndAim(weaknessPausedValue, false);
         this.resetIdleTimer(time);
         this.paused = "paused";
         this.idleTickCount++;
      }
   }

   private static boolean isRotationOutOfSync(ClientPlayerEntity clientPlayerEntity) {
      return MathClampUtils.isClampApplied() ? true : clientPlayerEntity.getYaw() != SilentAim.getYawSilent() || clientPlayerEntity.getPitch() != SilentAim.getSilentPitch();
   }

   private boolean isValidTargetInRange(LivingEntity entity, double entityPosLimit, Vec3d vec, float entityPosValue) {
      return EntityPlayerUtilsLiving.canSpectatorDead(entity, Options.INSTANCE2)
         && EntityPlayerUtilsLiving.getMinMax(entity) <= entityPosLimit
         && BlockEntityUtils.canFlag(vec, entity.getEntityPos(), entityPosValue, 60.0);
   }

   private boolean isForcedActionStillValid(ImmutablePos immutablePos, BlockPos pos, boolean obsidianAliveFlag, LivingEntity entity, long obsidianAliveValue, long livingVecValue, boolean crystalAuraFlag) {
      if (immutablePos.getValue() != entity.getId() || !immutablePos.getImmutable().equals(pos)) {
         return false;
      } else if (obsidianAliveFlag && immutablePos.getBreakObsidianTypeKind() != Kind.OBSIDIAN) {
         return false;
      } else if (obsidianAliveValue - immutablePos.getTime() > 20L) {
         return false;
      } else {
         long long2 = pos.asLong();
         if (immutablePos.getBreakObsidianTypeKind() == Kind.OBSIDIAN) {
            boolean of = mc.world.getBlockState(pos).isOf(Blocks.OBSIDIAN);
            if (!immutablePos.isTime()) {
               if (!of) {
                  return true;
               }
            } else {
               if (!of) {
                  this.actionTracker.handleForLong(long2, livingVecValue);
                  return false;
               }

               if (!this.actionTracker.canFlag(long2, obsidianAliveValue, this.getPlacementDelayTicks(crystalAuraFlag))) {
                  return true;
               }
            }

            immutablePos.handleTimeBreak(obsidianAliveValue);
         }

         if (immutablePos.getBreakObsidianTypeKind() == Kind.BREAK) {
            EndCrystalEntity endCrystalEntity = immutablePos.getEndCrystalEntity();
            if (endCrystalEntity != null && endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved()) {
               return true;
            }

            if (!BlockEntityUtils.canBedrockObsidian(pos)) {
               return false;
            }

            immutablePos.handleTimeBreak(obsidianAliveValue);
         }

         if (!BlockEntityUtils.canBedrockObsidian(pos)) {
            return false;
         } else {
            EndCrystalEntity next = BlockEntityUtils.getNext(pos);
            if (next != null) {
               immutablePos.handleBreakTime(next, obsidianAliveValue);
            }

            return true;
         }
      }
   }

   private boolean isOwnDeathDrop(ItemEntity itemEntity, long posBlockValue) {
      return this.deathPos != null && posBlockValue - this.deathTimeMs <= 300000L
         ? itemEntity.squaredDistanceTo(this.deathPos.getX() + 0.5, this.deathPos.getY() + 0.5, this.deathPos.getZ() + 0.5) <= 100.0
         : false;
   }

   private void resetPacketAgeTracking() {
      this.performingOwnAction = false;
      this.lastSlotPacketAge = Integer.MIN_VALUE;
      this.lastActionPacketAge = Integer.MIN_VALUE;
      this.lastPacketSelectedSlot = -1;
   }

   private boolean isSlotChangeAllowed(int weaknessValue) {
      if (weaknessValue < 0) {
         return true;
      } else if (!this.isControllerFlagSet()) {
         return false;
      } else {
         this.performingOwnAction = true;

         boolean flagWeakness;
         try {
            flagWeakness = this.weakness.canSelectedInventory(weaknessValue);
         } finally {
            this.performingOwnAction = false;
         }

         return flagWeakness;
      }
   }

   private void onOutgoingPacket(InventoryPlayer inventoryPlayer) {
      if (mc.player != null && mc.isOnThread()) {
         var packet = inventoryPlayer.packet;
         if (packet instanceof UpdateSelectedSlotC2SPacket updateSelectedSlotC2SPacket) {
            this.lastPacketSelectedSlot = updateSelectedSlotC2SPacket.getSelectedSlot();
            if (!this.performingOwnAction && !inventoryPlayer.isOpenedByClient()) {
               this.lastSlotPacketAge = mc.player.age;
            }
         } else {
            boolean ageSelectedFlag = packet instanceof PlayerInteractEntityC2SPacket
               || packet instanceof PlayerInteractBlockC2SPacket
               || packet instanceof PlayerInteractItemC2SPacket
               || packet instanceof PlayerActionC2SPacket playerActionC2SPacket && playerActionC2SPacket.getAction() != Action.SWAP_ITEM_WITH_OFFHAND;
            if (ageSelectedFlag && !this.performingOwnAction && !inventoryPlayer.isOpenedByClient()) {
               this.lastActionPacketAge = mc.player.age;
            }

            if (packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke && PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
               this.lastAttackedEntityId = PlayerCrystalUtils.getEntityId(playerInteractEntityC2SPacke);
               this.lastAttackTick = ClientThreadUtils.getTime();
            }
         }
      }
   }

   private boolean isCurrentActionStillValid(ImmutablePos immutablePos, LivingEntity entity, Predicted predicted, long weaknessLivingValue, long longValue) {
      if (immutablePos.getValue() != entity.getId()) {
         return false;
      } else if (weaknessLivingValue - immutablePos.getTime() > 20L) {
         return false;
      } else {
         Vec3d eyePos = mc.player.getEyePos();
         if (!BlockEntityUtils.canFlag(eyePos, BlockEntityUtils.getVec3d(immutablePos.getImmutable()), this.getReferenceYaw(), this.getKeepConeDegrees())) {
            return false;
         } else {
            long long2 = immutablePos.getImmutable().asLong();
            if (immutablePos.getBreakObsidianTypeKind() == Kind.OBSIDIAN) {
               boolean of = mc.world.getBlockState(immutablePos.getImmutable()).isOf(Blocks.OBSIDIAN);
               if (this.actionTracker.canFlagForLong(long2, longValue)) {
                  return false;
               }

               if (!immutablePos.isTime()) {
                  if (!of) {
                     return BlockEntityUtils.isModifyBlocks()
                        && BlockEntityUtils.canBlockState(immutablePos.getDown(), immutablePos.getUp())
                        && BlockEntityUtils.isEmpty(immutablePos.getImmutable());
                  }
               } else {
                  if (!of) {
                     this.actionTracker.handleForLong(long2, longValue);
                     return false;
                  }

                  if (!this.actionTracker.canFlag(long2, weaknessLivingValue, this.isControllerFlagSet() ? 1 : this.getPingDelayTicks())) {
                     return true;
                  }
               }

               immutablePos.handleTimeBreak(weaknessLivingValue);
            }

            if (immutablePos.getBreakObsidianTypeKind() == Kind.BREAK) {
               EndCrystalEntity endCrystalEntity = immutablePos.getEndCrystalEntity();
               if (endCrystalEntity != null && endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved()) {
                  if (this.actionTracker.canFlag(endCrystalEntity.getId(), longValue)) {
                     return false;
                  }

                  if (this.weakness == null) {
                     return BlockEntityUtils.canAbsorptionHealth(endCrystalEntity.getEntityPos(), null, this.antiSuicide.isEnabled(), this.getMaxSelfDamage());
                  }

                  double guardsAlive = UnsafeEntityUtils.getGuardsAlive(this.buildPlanRequest(entity, predicted, weaknessLivingValue, longValue, this.controllerTargetData), endCrystalEntity);
                  if (Double.isNaN(guardsAlive)) {
                     return false;
                  }

                  immutablePos.setAmount(guardsAlive);
                  return true;
               }

               if (!BlockEntityUtils.canBedrockObsidian(immutablePos.getImmutable())) {
                  return false;
               }

               immutablePos.handleTimeBreak(weaknessLivingValue);
            }

            if (!BlockEntityUtils.canBedrockObsidian(immutablePos.getImmutable())) {
               return false;
            } else {
               EndCrystalEntity next = BlockEntityUtils.getNext(immutablePos.getImmutable());
               if (next != null) {
                  if (this.actionTracker.canFlag(next.getId(), longValue)) {
                     return false;
                  } else {
                     if (this.weakness == null) {
                        if (!BlockEntityUtils.canAbsorptionHealth(next.getEntityPos(), null, this.antiSuicide.isEnabled(), this.getMaxSelfDamage())) {
                           return false;
                        }
                     } else {
                        double vecHashValue = UnsafeEntityUtils.getGuardsAlive(this.buildPlanRequest(entity, predicted, weaknessLivingValue, longValue, this.controllerTargetData), next);
                        if (Double.isNaN(vecHashValue)) {
                           return false;
                        }

                        immutablePos.setAmount(vecHashValue);
                     }

                     immutablePos.handleBreakTime(next, weaknessLivingValue);
                     return true;
                  }
               } else if (this.actionTracker.canTime(long2, longValue)) {
                  return true;
               } else if (BlockEntityUtils.canEntitiesInstance(immutablePos.getImmutable()) && BlockEntityUtils.canModifyInstance(immutablePos.getImmutable())) {
                  if (predicted.box().intersects(BlockEntityUtils.getBox3(immutablePos.getImmutable()))) {
                     return false;
                  } else if (this.weakness != null) {
                     Request obsidianSelf = this.buildPlanRequest(entity, predicted, weaknessLivingValue, longValue, this.controllerTargetData);
                     double minSelf = UnsafeEntityUtils.getMinSelf(
                        this.isBaseJustBroken(immutablePos.getImmutable(), weaknessLivingValue) ? applyTuningGate(obsidianSelf) : obsidianSelf, immutablePos.getImmutable(), null
                     );
                     if (Double.isNaN(minSelf)) {
                        return false;
                     } else {
                        immutablePos.setAmount(minSelf);
                        return true;
                     }
                  } else {
                     Vec3d vec3d = BlockEntityUtils.getVec3d(immutablePos.getImmutable());
                     double distanceFeet = BlockEntityUtils.getDistanceFeet(predicted, entity, vec3d, null);
                     if (distanceFeet < this.getMinEfficiency()) {
                        return false;
                     } else {
                        immutablePos.setAmount(distanceFeet);
                        return BlockEntityUtils.canAbsorptionHealth(vec3d, null, this.antiSuicide.isEnabled(), this.getMaxSelfDamage());
                     }
                  }
               } else {
                  return false;
               }
            }
         }
      }
   }

   static boolean isBreakOrPendingPlace(Kind kind, boolean crystalAuraFlag) {
      return kind == Kind.BREAK || kind == Kind.PLACE && crystalAuraFlag;
   }

   private void breakCrystalUnderCrosshair(EndCrystalEntity endCrystalEntity, boolean posEntityFlag, long posEntityValue, long longValue) {
      this.highlightedPos = BlockEntityUtils.getDown(endCrystalEntity);
      boolean posEntityFlag2 = BlockEntityUtils.isAmplifierEffect()
         && !this.actionTracker.canFlag(endCrystalEntity.getId(), longValue)
         && BlockEntityUtils.canAbsorptionHealth(endCrystalEntity.getEntityPos(), null, this.antiSuicide.isEnabled(), this.getMaxSelfDamage());
      this.highlightColor = posEntityFlag2 ? '\uffff' : 16711680;
      if (posEntityFlag2 && posEntityFlag && this.actionTracker.canTick(endCrystalEntity.getId(), posEntityValue, this.getPingDelayTicks(), longValue)) {
         this.attackCrystal(endCrystalEntity, posEntityValue, longValue);
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (this.showTarget.isEnabled() && mc.player != null && mc.world != null) {
         Matrix4f matrix = VertexBuilderUtils.getMatrix();
         if (this.isManualMode()) {
            if (this.highlightedPos != null && this.isManualPlacementHeld()) {
               this.renderBlockHighlight(matrix, this.highlightedPos, this.highlightColor);
            }
         } else {
            ImmutablePos immutablePos = this.currentAction;
            if (immutablePos != null) {
               int crystalAuraValue = switch (immutablePos.getBreakObsidianTypeKind()) {
                  case BREAK -> 16711680;
                  case OBSIDIAN -> 255;
                  default -> 65535;
               };
               this.renderBlockHighlight(matrix, immutablePos.getImmutable(), crystalAuraValue);
            }

            LivingEntity livingEntity = this.currentTarget;
            if (livingEntity != null && livingEntity.isAlive() && !livingEntity.isRemoved() && getNowMs() - this.lastTargetSeenMs <= 1200L) {
               boolean crystalAuraFlag = immutablePos != null && immutablePos.getBreakObsidianTypeKind() == Kind.BREAK;
               this.renderTargetCircle(matrix, livingEntity, queueCommand.tickProgress, crystalAuraFlag ? 11075839 : 16722175);
            }
         }
      }
   }

   private boolean isControllerAimUsable(float[] weaknessValues) {
      float[] actionCrystal = this.weakness.getActionCrystal(weaknessValues[0], weaknessValues[1]);
      return actionCrystal != null && actionCrystal.length >= 2 && Float.isFinite(actionCrystal[0]) && Float.isFinite(actionCrystal[1]);
   }

   static boolean isRecentlyBrokenBase(BlockPos pos, BlockPos pos2, long crystalAuraValue, long crystalAuraLimit) {
      return pos != null && pos.equals(pos2) && crystalAuraValue != Long.MIN_VALUE && crystalAuraLimit >= crystalAuraValue && crystalAuraLimit - crystalAuraValue <= 3L;
   }

   private void placeCrystalUnderCrosshair(BlockHitResult blockHitResult, BlockPos blockPos, boolean handStackFlag, float handStackValue, float f2, long limit, long handStackLimit) {
      this.highlightedPos = blockPos;
      if (BlockEntityUtils.canEntitiesInstance(blockPos) && BlockEntityUtils.canModifyInstance(blockPos)) {
         boolean absorptionHealth = BlockEntityUtils.canAbsorptionHealth(BlockEntityUtils.getVec3d(blockPos), null, this.antiSuicide.isEnabled(), this.getMaxSelfDamage());
         this.highlightColor = absorptionHealth ? '\uffff' : 16711680;
         if (absorptionHealth && handStackFlag) {
            if (this.actionTracker.canFlag(blockPos.asLong(), limit, this.getPingDelayTicks())) {
               if (!this.actionTracker.canTime(blockPos.asLong(), handStackLimit) && handStackLimit >= this.placeAllowedAfterMs) {
                  if (this.actionTracker.canTimeFlag(limit) && !this.actionTracker.canAmountTime(handStackValue, f2, handStackLimit)) {
                     if (mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) {
                        this.interactBlockAndRecord(blockHitResult, blockPos, limit, handStackLimit, handStackValue, f2);
                     }
                  }
               }
            }
         }
      } else {
         this.highlightColor = 16711680;
      }
   }

   private static double randomAimFactor() {
      return 0.85 + ThreadLocalRandom.current().nextDouble() * 0.15;
   }

   private double getMinEfficiency() {
      return this.minEfficiencySetting.getValue() / 100.0;
   }

   private void placeSupportObsidian(BlockHitResult blockHitResult, boolean sideBlockFlag, float sideBlockValue, float f2, long showsCenterValue, long longValue) {
      BlockPos blockPos = blockHitResult.getBlockPos();
      if (BlockEntityUtils.isModifyBlocks()) {
         if (!mc.world.getBlockState(blockPos).isReplaceable() && BlockEntityUtils.canBlockSquare(blockPos, blockHitResult.getSide())) {
            BlockPos offset = blockPos.offset(blockHitResult.getSide());
            if (!this.actionTracker.canFlagForLong(offset.asLong(), longValue) && BlockEntityUtils.isEmpty(offset)) {
               int sideBlockLimit = SelectedInventoryUtils.getValue(Items.OBSIDIAN);
               if (sideBlockLimit >= 0) {
                  this.highlightedPos = offset;
                  boolean absorptionHealth = BlockEntityUtils.canAbsorptionHealth(
                     BlockEntityUtils.getVec3d(offset), offset, this.antiSuicide.isEnabled(), this.getMaxSelfDamage()
                  );
                  this.highlightColor = absorptionHealth ? 255 : 16711680;
                  if (absorptionHealth && sideBlockFlag && this.canSwapSlotNow(showsCenterValue)) {
                     if (this.actionTracker.canTimeFlag(showsCenterValue) && !this.actionTracker.canAmountTime(sideBlockValue, f2, longValue)) {
                        this.swapToSlot(sideBlockLimit, showsCenterValue);
                        this.obsidianSwapActive = true;
                        this.interactBlockAndRecord(blockHitResult, null, showsCenterValue, longValue, sideBlockValue, f2);
                        this.actionTracker.handle(offset.asLong(), showsCenterValue);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isRelevantLootNearby(long expandEntitiesValue) {
      Box expand = mc.player.getBoundingBox().expand(8.0, 4.0, 8.0);

      for (ItemEntity itemEntity : mc.world.getEntitiesByClass(ItemEntity.class, expand, itemEntity2 -> itemEntity2.isAlive() && !itemEntity2.isRemoved())) {
         if (!this.isOwnDeathDrop(itemEntity, expandEntitiesValue) && isRelevantLootItem(itemEntity.getStack())) {
            return true;
         }
      }

      return false;
   }

   private static boolean isPausedByAnchorModule() {
      return ModuleRegistry.getModule(AnchorMacro.class) instanceof AnchorMacro anchorMacro && anchorMacro.isMacroInteracting() ? true : isSafeAnchorAutoIdle();
   }

   public BlockPos getLastBreakBase() {
      return this.lastBreakBase;
   }

   private static boolean isRelevantLootItem(ItemStack itemStack) {
      Item item = itemStack.getItem();
      return item != Items.TOTEM_OF_UNDYING && item != Items.END_CRYSTAL && item != Items.SHEARS
         ? item.getComponents().contains(DataComponentTypes.EQUIPPABLE) || item.getComponents().contains(DataComponentTypes.TOOL)
         : true;
   }

   private int countHotbarCrystals() {
      int inventoryStack = 0;

      for (int i = 0; i < 9; i++) {
         ItemStack stack = mc.player.getInventory().getStack(i);
         if (stack.isOf(Items.END_CRYSTAL)) {
            inventoryStack += stack.getCount();
         }
      }

      return inventoryStack;
   }

   @Override
   protected void onReset() {
      if (this.weakness == null) {
         this.runTick(true);
      }
   }

   private void continueAimRestore(long crystalsValue) {
      if (this.weakness != null) {
         this.restoringAim = false;
      } else if (this.restoringAim) {
         if (this.lastAimTick != crystalsValue) {
            float yaw = MathClampUtils.getYaw();
            float clamp = MathClampUtils.getClamp();
            float f = mc.player.getYaw();
            float pitch = mc.player.getPitch();
            float sensitivityMouse = ClampMath.getSensitivityMouse();
            double crystalsLimit = BoxLengthUtils.getAmount(yaw, clamp, f, pitch);
            if (this.aimAttemptCount++ < 10 && !(crystalsLimit <= Math.max(0.5, sensitivityMouse * 2.0)) && !(crystalsLimit > 150.0)) {
               float[] crystalsValues = BoxLengthUtils.getAmounts(yaw, clamp, f, pitch, 5.0, randomAimFactor(), sensitivityMouse);
               this.applyAim(crystalsValues[0], crystalsValues[1], crystalsValue);
            } else {
               this.restoringAim = false;
            }
         }
      }
   }

   private static double getAimErrorToPoint(Vec3d vec, float crystalAuraValue, float clamp, Vec3d vec2) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, vec2);
      return BoxLengthUtils.getAmount(crystalAuraValue, clamp, wrapDegrees[0], wrapDegrees[1]);
   }

   private boolean doesLookRayHitTopFace(BlockPos pos, Vec3d vec, float vectorMultiplyValue, float entity) {
      Vec3d rotationVector = mc.player.getRotationVector(entity, vectorMultiplyValue);
      if (!(rotationVector.y < 0.0)) {
         return false;
      } else {
         double vectorMultiplyLimit = (pos.getY() + 1.0 - vec.y) / rotationVector.y;
         if (vectorMultiplyLimit > 0.0 && !(vectorMultiplyLimit > 4.4)) {
            Vec3d add = vec.add(rotationVector.multiply(vectorMultiplyLimit));
            return add.x >= pos.getX()
               && add.x <= pos.getX() + 1.0
               && add.z >= pos.getZ()
               && add.z <= pos.getZ() + 1.0
               && BlockEntityUtils.canSubtractRaycast(vec, add);
         } else {
            return false;
         }
      }
   }

   private void abortSlotSwap() {
      if (this.slotSwapActive) {
         SelectedInventoryUtils.updateSelectedSlot2();
      }

      this.slotSwapActive = false;
      this.swappedSlot = -1;
      this.obsidianSwapActive = false;
   }

   private void attackCrystal(EndCrystalEntity endCrystalEntity, long time, long immutableHandValue) {
      this.performingOwnAction = true;

      try {
         mc.interactionManager.attackEntity(mc.player, endCrystalEntity);
      } finally {
         this.performingOwnAction = false;
      }

      mc.player.swingHand(Hand.MAIN_HAND);
      this.actionTracker.handle(endCrystalEntity.getId(), time);
      this.actionTracker.handle(BlockEntityUtils.getDown(endCrystalEntity).asLong());
      this.placeAllowedAfterMs = immutableHandValue + ThreadLocalRandom.current().nextLong(101L);
      this.lastBreakTick = time;
      this.lastBreakBase = BlockEntityUtils.getDown(endCrystalEntity).toImmutable();
      this.lastActionTick = time;
   }

   private void executeCurrentAction(ImmutablePos immutablePos, long time, long obsidianSelectedValue) {
      ClientPlayerEntity player = mc.player;
      Vec3d eyePos = player.getEyePos();
      float yaw = MathClampUtils.getYaw();
      float clamp = MathClampUtils.getClamp();
      boolean obsidianSelectedFlag = immutablePos.getBreakObsidianTypeKind() == Kind.OBSIDIAN && !immutablePos.isTime();
      int selectedSlot = this.weakness != null && immutablePos.getBreakObsidianTypeKind() == Kind.BREAK
         ? player.getInventory().getSelectedSlot()
         : SelectedInventoryUtils.getValue(obsidianSelectedFlag ? Items.OBSIDIAN : Items.END_CRYSTAL);
      if (selectedSlot < 0 && this.weakness != null && this.canExecuteAction(immutablePos, obsidianSelectedValue)) {
         selectedSlot = player.getInventory().getSelectedSlot();
      }

      if (selectedSlot < 0) {
         this.setIdleReason(time, obsidianSelectedFlag ? "no obsidian" : "no crystals");
      } else {
         boolean weakness = this.isControllerFlagSet();
         boolean obsidianSelectedFlag2 = player.getInventory().getSelectedSlot() == selectedSlot;
         if (!obsidianSelectedFlag2 && !weakness) {
            if (this.weakness != null) {
               this.requestControllerSlot(selectedSlot, time);
            } else if (this.canSwapSlotNow(time)) {
               this.swapToSlot(selectedSlot, time);
               obsidianSelectedFlag2 = true;
            }
         }

         int weaknessValue = obsidianSelectedFlag2 ? -1 : selectedSlot;
         boolean obsidianSelectedFlag3 = (obsidianSelectedFlag2 || weakness) && this.isAimReadyToAct(time) && this.isInteractionAvailable();
         HitResult multiplySquared = BlockEntityUtils.getMultiplySquared(eyePos, yaw, clamp);
         Predicate<net.minecraft.util.hit.HitResult> predicate = null;
         Vec3d vec3d;
         switch (immutablePos.getBreakObsidianTypeKind()) {
            case BREAK:
               EndCrystalEntity endCrystalEntity = immutablePos.getEndCrystalEntity();
               if (multiplySquared instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == endCrystalEntity) {
                  if (obsidianSelectedFlag3 && this.actionTracker.canTick(endCrystalEntity.getId(), time, this.getPingDelayTicks(), obsidianSelectedValue)) {
                     if (this.isControllerAllowingAction(immutablePos)) {
                        if (this.isSlotChangeAllowed(weaknessValue)) {
                           boolean obsidianSelectedFlag4 = this.actionTracker.getValue(endCrystalEntity.getId()) == 0;
                           this.attackCrystal(endCrystalEntity, time, obsidianSelectedValue);
                           if (obsidianSelectedFlag4) {
                              immutablePos.setTime(time);
                           }
                        }
                     } else {
                        this.lastHoldTick = time;
                     }
                  }

                  this.applyAim(yaw, clamp, time);
                  return;
               }

               vec3d = BlockEntityUtils.getVec3d(endCrystalEntity.getBoundingBox(), endCrystalEntity, eyePos, yaw, clamp, 3.0);
               predicate = hitResult -> hitResult instanceof EntityHitResult entityHitResult2 && entityHitResult2.getEntity() == endCrystalEntity;
               if (weakness) {
                  vec3d = this.refineAimPoint(immutablePos.getImmutable(), endCrystalEntity, eyePos, yaw, clamp, vec3d);
               }
               break;
            case PLACE:
               if (this.actionTracker.canTime(immutablePos.getImmutable().asLong(), obsidianSelectedValue)) {
                  Box box = BlockEntityUtils.getBox(immutablePos.getImmutable());
                  if (BlockEntityUtils.canRaycastVector(box, eyePos, yaw, clamp, 3.0)) {
                     this.applyAim(yaw, clamp, time);
                     return;
                  }

                  vec3d = BlockEntityUtils.getVec3d(box, null, eyePos, yaw, clamp, 3.0);
                  if (weakness) {
                     vec3d = this.refineAimPoint(immutablePos.getImmutable(), null, eyePos, yaw, clamp, vec3d);
                  }
               } else {
                  BlockHitResult posBlock = BlockEntityUtils.getPosBlock(multiplySquared, immutablePos.getImmutable(), null, eyePos, 4.4);
                  if (posBlock != null) {
                     this.executePlacement(immutablePos, posBlock, true, obsidianSelectedFlag3, eyePos, yaw, clamp, time, obsidianSelectedValue, weaknessValue);
                     return;
                  }

                  BlockHitResult amountsSquared = BlockEntityUtils.getAmountsSquared(immutablePos.getImmutable(), this.getAllowedClickFaces(), null, eyePos, yaw, clamp, 4.4);
                  vec3d = amountsSquared == null ? null : amountsSquared.getPos();
                  predicate = hitResult2 -> BlockEntityUtils.getPosBlock(hitResult2, immutablePos.getImmutable(), null, eyePos, 4.4) != null;
                  if (weakness) {
                     vec3d = this.refineAimPoint(immutablePos.getImmutable(), null, eyePos, yaw, clamp, vec3d);
                  }
               }
               break;
            default:
               if (immutablePos.isTime()) {
                  if (BlockEntityUtils.getPosBlock(multiplySquared, immutablePos.getImmutable(), null, eyePos, 4.4) != null) {
                     this.applyAim(yaw, clamp, time);
                     return;
                  }

                  BlockHitResult amountsSquared2 = BlockEntityUtils.getAmountsSquared(immutablePos.getImmutable(), this.getAllowedClickFaces(), null, eyePos, yaw, clamp, 4.4);
                  vec3d = amountsSquared2 == null ? null : amountsSquared2.getPos();
                  predicate = hitResult3 -> BlockEntityUtils.getPosBlock(hitResult3, immutablePos.getImmutable(), null, eyePos, 4.4) != null;
                  if (weakness) {
                     vec3d = this.refineAimPoint(immutablePos.getImmutable(), null, eyePos, yaw, clamp, vec3d);
                  }
               } else {
                  BlockHitResult blockHitResult = BlockEntityUtils.getPosBlock(multiplySquared, immutablePos.getDown(), immutablePos.getUp(), eyePos, 4.4);
                  if (blockHitResult != null) {
                     this.executePlacement(immutablePos, blockHitResult, false, obsidianSelectedFlag3, eyePos, yaw, clamp, time, obsidianSelectedValue, weaknessValue);
                     return;
                  }

                  BlockHitResult amountsSquared3 = BlockEntityUtils.getAmountsSquared(immutablePos.getDown(), new Direction[]{immutablePos.getUp()}, immutablePos.getUp(), eyePos, yaw, clamp, 4.4);
                  vec3d = amountsSquared3 == null ? null : amountsSquared3.getPos();
                  predicate = hitResult4 -> BlockEntityUtils.getPosBlock(hitResult4, immutablePos.getDown(), immutablePos.getUp(), eyePos, 4.4) != null;
               }
         }

         if (vec3d == null) {
            this.setIdleReason(time, "out of reach");
         } else {
            float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, vec3d);
            float sensitivityMouse = ClampMath.getSensitivityMouse();
            float[] obsidianSelectedValues = predicate != null && BlockEntityUtils.canFlag(yaw, clamp, wrapDegrees, sensitivityMouse)
               ? BlockEntityUtils.getAmounts(eyePos, yaw, clamp, sensitivityMouse, predicate)
               : null;
            if (obsidianSelectedValues != null) {
               wrapDegrees = obsidianSelectedValues;
            }

            if (!weakness || !this.isControllerAimUsable(wrapDegrees)) {
               float[] aimStep = this.getAimStep(yaw, clamp, wrapDegrees[0], wrapDegrees[1], false);
               this.applyAim(aimStep[0], aimStep[1], time);
            }
         }
      }
   }

   public void detachController(Object obj) {
      if (obj != null && obj == this.controllerOwner) {
         this.abortSlotSwap();
         this.resetAutoState();
         this.resetActionTimestamps();
         this.weakness = null;
         this.controllerOwner = null;
         this.controllerTargetData = null;
         this.actionTracker.handleTime(1000L);
      }
   }

   public Request buildPredictionRequest(TargetSelfDataTarget targetSelfDataTarget) {
      if (targetSelfDataTarget != null && targetSelfDataTarget.target() != null && mc.player != null && mc.world != null) {
         long predictTargetValue = ClientThreadUtils.getTime();
         LivingEntity target = targetSelfDataTarget.target();
         boolean predictTargetFlag = this.lastAttackedEntityId == target.getId() && predictTargetValue - this.lastAttackTick <= 1L;
         Predicted interpolatorInstance = BlockEntityUtils.getInterpolatorInstance(target, Math.max(1, targetSelfDataTarget.predictTicks()), predictTargetFlag, true);
         return this.buildPlanRequest(target, interpolatorInstance, predictTargetValue, getNowMs(), targetSelfDataTarget);
      } else {
         return null;
      }
   }

   private void dropPlacementsWithCrystals() {
      for (long crystalAuraValue : this.actionTracker.getWindowedTimes()) {
         if (BlockEntityUtils.getNext(BlockPos.fromLong(crystalAuraValue)) != null) {
            this.actionTracker.handle(crystalAuraValue);
         }
      }
   }

   @Override
   public boolean isReductionCritical() {
      return this.weakness == null && this.isCrystalActionActive();
   }

   private void handleSwingHand(BlockHitResult blockHitResult, BlockPos pos, long time, long swingHandValue, float posBlockValue, float f2, boolean swingHandFlag) {
      this.performingOwnAction = true;

      ActionResult actionResult;
      try {
         actionResult = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
      } finally {
         this.performingOwnAction = false;
      }

      if (actionResult instanceof Success success && success.swingSource() == SwingSource.CLIENT) {
         mc.player.swingHand(Hand.MAIN_HAND);
      }

      if (pos != null) {
         this.actionTracker.handle(pos.asLong(), swingHandValue, time, posBlockValue, f2, swingHandFlag);
      } else {
         this.actionTracker.handleSupplierAmount(swingHandValue, time, posBlockValue, f2, swingHandFlag);
      }

      this.lastPlaceTick = time;
      this.lastActionTick = time;
   }

   public boolean isControlledExternally() {
      return this.weakness != null;
   }

   private boolean isAimApplied(long yourValue) {
      return this.lastAimTick != yourValue - 1L ? false : BoxLengthUtils.canFlag(MathClampUtils.getYaw(), MathClampUtils.getClamp(), this.lastAimYaw, this.lastAimPitch);
   }

   public TickStatusData getStatusSnapshot() {
      ImmutablePos immutablePos = this.currentAction;
      long livingValue = ClientThreadUtils.getTime();
      EndCrystalEntity endCrystalEntity = immutablePos == null ? null : immutablePos.getEndCrystalEntity();
      int livingValue2 = immutablePos != null ? immutablePos.getValue() : (this.currentTarget != null ? this.currentTarget.getId() : -1);
      return new TickStatusData(
         this.weakness != null,
         this.paused,
         immutablePos == null ? null : immutablePos.getBreakObsidianTypeKind(),
         immutablePos == null ? null : immutablePos.getImmutable(),
         endCrystalEntity == null ? -1 : endCrystalEntity.getId(),
         immutablePos == null ? 0.0 : immutablePos.getAmount(),
         livingValue2,
         this.lastHoldTick == livingValue,
         this.lastPlaceTick,
         this.lastBreakTick,
         this.lastActionTick,
         this.wantedSlotRequestTick == livingValue ? this.wantedSlot : -1,
         this.idle
      );
   }

   private Vec3d findBestTopFaceAimPoint(BlockPos pos, EndCrystalEntity endCrystalEntity, Vec3d vec, float squaredValue, float clamp, int squaredLimit) {
      double y = pos.getY() + 1.0;
      if (!(vec.y > y)) {
         return null;
      } else {
         int squaredSize = TOP_FACE_SAMPLE_OFFSETS.length * TOP_FACE_SAMPLE_OFFSETS.length;
         Vec3d[] vec3ds = new Vec3d[squaredSize];
         float[][] floats = new float[squaredSize][];
         double[] doubles = new double[squaredSize];
         int index = 0;
         double squaredValue3 = 19.360000000000003;

         for (double topFaceSampleOffsetsItem : TOP_FACE_SAMPLE_OFFSETS) {
            for (double squaredValue5 : TOP_FACE_SAMPLE_OFFSETS) {
               Vec3d vec3d = new Vec3d(pos.getX() + topFaceSampleOffsetsItem, y, pos.getZ() + squaredValue5);
               if (!(vec.squaredDistanceTo(vec3d) > squaredValue3)) {
                  float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, vec3d);
                  double crystalAuraLimit = BoxLengthUtils.getAmount(squaredValue, clamp, wrapDegrees[0], wrapDegrees[1]);
                  if (angleToBucket(crystalAuraLimit) <= squaredLimit) {
                     vec3ds[index] = vec3d;
                     floats[index] = wrapDegrees;
                     doubles[index] = crystalAuraLimit;
                     index++;
                  }
               }
            }
         }

         for (int i = 0; i < index; i++) {
            int index2 = -1;

            for (int j = 0; j < index; j++) {
               if (vec3ds[j] != null && (index2 < 0 || doubles[j] < doubles[index2])) {
                  index2 = j;
               }
            }

            Vec3d amountsSquared = vec3ds[index2];
            vec3ds[index2] = null;
            if (this.isAimPointValid(pos, endCrystalEntity, vec, floats[index2][0], floats[index2][1])) {
               return amountsSquared;
            }
         }

         return null;
      }
   }

   private boolean hasSlotPacketThisTick() {
      return this.lastSlotPacketAge == mc.player.age;
   }

   private boolean isAimReadyToAct(long weaknessValue) {
      return this.weakness == null ? this.isAimApplied(weaknessValue) : this.weakness.isFlag();
   }

   public void onOutgoingPacketWhenDisabled(InventoryPlayer inventoryPlayer) {
      if (!this.isEnabled()) {
         this.onOutgoingPacket(inventoryPlayer);
      }
   }

   private void restoreSlotWhenIdle(long lootPausedValue) {
      if (this.slotSwapActive) {
         boolean lootPausedFlag = this.isManualMode() ? !this.isManualPlacementHeld() : this.currentAction == null && this.idleTickCount >= 10;
         if (lootPausedFlag) {
            this.tryRestoreSlot(lootPausedValue);
         }
      }
   }

   private float getMaxSelfDamage() {
      return this.maxSelfDamage.getValue();
   }

   private boolean isManualPlacementHeld() {
      return !isKeyPhysicallyDown(mc.options.useKey) ? false : this.obsidianSwapActive || mc.player.getMainHandStack().isOf(Items.END_CRYSTAL);
   }

   private boolean hasActionPacketThisTick() {
      return this.lastActionPacketAge == mc.player.age;
   }

   private LivingEntity getControllerTarget() {
      LivingEntity livingEntity = this.controllerTargetData == null ? null : this.controllerTargetData.target();
      return livingEntity != null && livingEntity.isAlive() && !livingEntity.isRemoved() ? livingEntity : null;
   }

   private boolean isManualMode() {
      return this.weakness == null && "Manual".equals(this.modeSetting.getCurrentOption());
   }

   private void resetAutoState() {
      this.currentAction = null;
      this.currentTarget = null;
      this.paused = null;
      this.restoringAim = false;
      this.aimAttemptCount = 0;
      this.idleTickCount = 0;
      this.idleSinceMs = getNowMs();
      this.idleStartDelayMs = randomStartDelayMs();
      this.retryAfterTick = 0L;
      this.hasRotationSample = false;
      this.lastYawChange = 0.0;
      this.placeAllowedAfterMs = 0L;
      this.highlightedPos = null;
      this.actionTracker.updateAmountTime();
   }

   private void updateObsidianTracking(long obsidianValue, long longValue) {
      for (long getItem : this.actionTracker.getSet()) {
         if (obsidianValue - this.actionTracker.getTime(getItem) >= 1L && !mc.world.getBlockState(BlockPos.fromLong(getItem)).isOf(Blocks.OBSIDIAN)) {
            this.actionTracker.handleForLong(getItem, longValue);
         }
      }
   }

   private ImmutablePos createActionForPos(BlockPos pos, boolean posDownFlag, LivingEntity entity, long posDownValue, boolean crystalAuraFlag, BlockPos pos2, Direction direction) {
      int id = entity.getId();
      if (posDownFlag) {
         if (pos2 != null && direction != null) {
            return ImmutablePos.getImmutableObsidian(pos, 0.0, posDownValue, id, pos2, direction);
         } else {
            if (!BlockEntityUtils.canFlag(pos.down())) {
               Support oppositeImmutable = BlockEntityUtils.getOppositeImmutable(
                  pos, mc.player.getEyePos(), MathClampUtils.getYaw(), MathClampUtils.getClamp(), 4.4
               );
               if (oppositeImmutable != null) {
                  return ImmutablePos.getImmutableObsidian(pos, 0.0, posDownValue, id, oppositeImmutable.pos(), oppositeImmutable.face());
               }
            }

            return ImmutablePos.getImmutableObsidian(pos, 0.0, posDownValue, id);
         }
      } else {
         EndCrystalEntity next = BlockEntityUtils.getNext(pos);
         if (next != null) {
            return ImmutablePos.getImmutableBreak(next, 0.0, posDownValue, id);
         } else {
            long long2 = pos.asLong();
            long time2 = this.actionTracker.getTime(long2);
            if (time2 != Long.MIN_VALUE && !this.actionTracker.canFlag(long2, posDownValue, this.getPlacementDelayTicks(crystalAuraFlag))) {
               ImmutablePos immutableObsidian = ImmutablePos.getImmutableObsidian(pos, 0.0, posDownValue, id);
               immutableObsidian.handleTime(time2);
               immutableObsidian.setTime(posDownValue);
               return immutableObsidian;
            } else {
               return ImmutablePos.getImmutablePlace(pos, 0.0, posDownValue, id);
            }
         }
      }
   }

   public CrystalAura() {
      super("Auto Crystal", IconsPngType.COMBAT);
      this.minEfficiencySetting = new IntegerSetting(
         "Min Efficiency", "Auto only uses spots that hit the target for at least this % of a crystal's max damage. Lower uses more spots.", 50, 0, 100, 5
      );
      this.antiSuicide = new BooleanSetting("Anti Suicide", "Never place or break a crystal that would kill you.", true);
      this.maxSelfDamage = new IntegerSetting("Max Self Damage", "Most health a crystal may take from you (2 = one heart). Lower is safer.", 16, 0, 20, 1);
      this.autoObsidian = new BooleanSetting("Auto Obsidian", "Places obsidian from your hotbar first when there is no spot to use.", false);
      this.stopOnLoot = new BooleanSetting(
         "Stop On Loot", "Pauses while an enemy's dropped totems, crystals, armor or tools lie within 8 blocks. Your own death drops are ignored.", false
      );
      this.showTarget = new BooleanSetting("Show Target", "Highlights the block being used and circles your target.", true);
      this.centerScreen = new BooleanSetting("Center Screen", "Shows your hotbar crystal count and what Auto is doing under the crosshair.", true);
      this.actionTracker = new HandleTime();
      this.lastAimTick = -4611686018427387904L;
      this.swappedSlot = -1;
      this.lastSlotSwapTick = -4611686018427387904L;
      this.lastSlotPacketAge = Integer.MIN_VALUE;
      this.lastActionPacketAge = Integer.MIN_VALUE;
      this.lastPacketSelectedSlot = -1;
      this.highlightColor = 65535;
      this.lastAttackedEntityId = -1;
      this.lastAttackTick = -4611686018427387904L;
      this.lastPlaceTick = Long.MIN_VALUE;
      this.lastBreakTick = Long.MIN_VALUE;
      this.lastActionTick = Long.MIN_VALUE;
      this.lastHoldTick = Long.MIN_VALUE;
      this.wantedSlotRequestTick = Long.MIN_VALUE;
      this.wantedSlot = -1;
      this.addSettings(
         new ConfigSetting[]{
            this.modeSetting,
            this.rangeSetting,
            this.aimSpeed,
            this.minEfficiencySetting,
            this.antiSuicide,
            this.maxSelfDamage,
            this.autoObsidian,
            this.stopOnLoot,
            this.showTarget,
            this.centerScreen
         }
      );
   }

   private Request buildPlanRequest(LivingEntity entity, Predicted predicted, long obsidianSelfValue, long longValue, TargetSelfDataTarget targetSelfDataTarget) {
      return targetSelfDataTarget == null
         ? new Request(
            entity,
            predicted,
            this.getMinEfficiency(),
            this.antiSuicide.isEnabled(),
            this.getMaxSelfDamage(),
            this.autoObsidian.isEnabled(),
            SelectedInventoryUtils.getValue(Items.OBSIDIAN) >= 0,
            this.actionTracker,
            longValue,
            obsidianSelfValue,
            this.getPingDelayTicks(),
            MathClampUtils.getYaw(),
            MathClampUtils.getClamp(),
            SilentAim.getYawSilent()
         )
         : new Request(
            entity,
            predicted,
            0.0,
            true,
            targetSelfDataTarget.maxSelfDamage(),
            targetSelfDataTarget.autoObsidian(),
            SelectedInventoryUtils.getValue(Items.OBSIDIAN) >= 0,
            this.actionTracker,
            longValue,
            obsidianSelfValue,
            this.getPingDelayTicks(),
            MathClampUtils.getYaw(),
            MathClampUtils.getClamp(),
            mc.player.getYaw(),
            targetSelfDataTarget.tuning(),
            targetSelfDataTarget.self(),
            targetSelfDataTarget.spread(),
            targetSelfDataTarget.targetWindow(),
            targetSelfDataTarget.selfWindow(),
            targetSelfDataTarget.targetEhp(),
            targetSelfDataTarget.targetTotem(),
            targetSelfDataTarget.allowTotemTrade(),
            targetSelfDataTarget.veto()
         );
   }

   private boolean canSwapSlotNow(long showsCenterValue) {
      if (this.lastSlotSwapTick == showsCenterValue || AutoInventoryTotem.isHandSwapSlotActive()) {
         return false;
      } else {
         return !this.hasSlotPacketThisTick() && !this.hasSlotChangedSincePacket() ? this.slotSwapActive || !SelectedInventoryUtils.isSelectedSlot() : false;
      }
   }

   static Request applyTuningGate(Request request) {
      return request != null && request.tuning() != null && request.tuning().levelGate() ? request.getSelfTarget(request.tuning().getBonusMin(false)) : request;
   }

   @Override
   public boolean isGodWeaknessWaitingPlayer() {
      return this.weakness == null && this.isCrystalActionActive();
   }

   private Direction[] getAllowedClickFaces() {
      return this.weakness != null
            && this.controllerTargetData != null
            && this.controllerTargetData.directive() != null
            && this.controllerTargetData.directive().clickFace() != null
         ? new Direction[]{this.controllerTargetData.directive().clickFace()}
         : BlockEntityUtils.idleDirections;
   }

   private int getPredictTicks() {
      return this.weakness != null && this.controllerTargetData != null ? Math.max(1, this.controllerTargetData.predictTicks()) : 3;
   }

   private LivingEntity getNearestTarget() {
      double crystalAuraValue = this.rangeSetting.getValue();
      float yawSilent = SilentAim.getYawSilent();
      Vec3d eyePos = mc.player.getEyePos();
      if (this.currentTarget != null && this.isValidTargetInRange(this.currentTarget, crystalAuraValue, eyePos, yawSilent)) {
         return this.currentTarget;
      } else {
         LivingEntity next = null;
         double crystalAuraLimit = Double.MAX_VALUE;

         for (LivingEntity livingEntity : EntityPlayerUtilsLiving.getInstanceWorld(crystalAuraValue, Options.INSTANCE2)) {
            if (BlockEntityUtils.canFlag(eyePos, livingEntity.getEntityPos(), yawSilent, 60.0)) {
               double minMax = EntityPlayerUtilsLiving.getMinMax(livingEntity);
               if (minMax < crystalAuraLimit) {
                  crystalAuraLimit = minMax;
                  next = livingEntity;
               }
            }
         }

         return next;
      }
   }

   private void runControllerForcedAction(ForcedFaceData forcedFaceData, LivingEntity entity, Predicted predicted, long immutablePosValue, long livingVecValue, boolean immutablePosFlag) {
      BlockPos forcedObsidianCell = forcedFaceData.forcedObsidianCell();
      BlockPos forcedBase = forcedFaceData.forcedBase();
      boolean obsidianAliveFlag = forcedObsidianCell != null && !BlockEntityUtils.canBedrockObsidian(forcedObsidianCell);
      if (!obsidianAliveFlag && forcedObsidianCell != null && forcedBase != null && forcedBase.equals(forcedObsidianCell.up()) && !BlockEntityUtils.canBedrockObsidian(forcedBase)) {
         forcedObsidianCell = forcedBase;
         obsidianAliveFlag = true;
      }

      BlockPos blockPos = obsidianAliveFlag ? forcedObsidianCell : forcedBase;
      if (blockPos == null) {
         this.setIdleReason(immutablePosValue, "combo idle");
         this.idle = "idle";
      } else {
         boolean trustObsidian = forcedFaceData.trustObsidian();
         if (this.currentAction != null && !this.isForcedActionStillValid(this.currentAction, blockPos, obsidianAliveFlag, entity, immutablePosValue, livingVecValue, trustObsidian)) {
            this.clearCurrentAction(immutablePosValue);
         }

         boolean immutablePosFlag3 = forcedFaceData.support() != null && forcedFaceData.supportFace() != null && forcedFaceData.support().offset(forcedFaceData.supportFace()).equals(blockPos);
         BlockPos pos = immutablePosFlag3 ? forcedFaceData.support() : null;
         Direction direction = immutablePosFlag3 ? forcedFaceData.supportFace() : null;
         if (this.currentAction != null && obsidianAliveFlag && !isObsidianSupportMatching(this.currentAction, pos, direction)) {
            this.clearCurrentAction(immutablePosValue);
         }

         if (this.currentAction == null) {
            this.currentAction = this.createActionForPos(blockPos, obsidianAliveFlag, entity, immutablePosValue, trustObsidian, pos, direction);
         }

         this.restoringAim = false;
         EndCrystalEntity endCrystalEntity = !forcedFaceData.hold() && this.isTradeModeAction(this.currentAction, livingVecValue)
            ? UnsafeEntityUtils.getTuningExpand(this.buildPlanRequest(entity, predicted, immutablePosValue, livingVecValue, this.controllerTargetData), blockPos)
            : null;
         if (endCrystalEntity != null) {
            this.currentAction = ImmutablePos.getImmutableBreak(endCrystalEntity, 0.0, immutablePosValue, entity.getId());
         }

         if (!immutablePosFlag && !this.canExecuteAction(this.currentAction, livingVecValue)) {
            this.setIdleReason(immutablePosValue, "no crystals");
            this.idle = "no crystals";
         } else {
            Kind kind = this.currentAction.getBreakObsidianTypeKind() == Kind.OBSIDIAN && this.currentAction.isTime()
               ? Kind.PLACE
               : this.currentAction.getBreakObsidianTypeKind();
            Forced idleDirections = UnsafeEntityUtils.getIdleDirections(
               this.buildPlanRequestWithSupport(entity, predicted, immutablePosValue, livingVecValue, this.controllerTargetData, this.currentAction),
               kind,
               this.currentAction.getImmutable(),
               resolveTargetBase(obsidianAliveFlag, blockPos, forcedBase),
               this.currentAction.getEndCrystalEntity(),
               this.getAllowedClickFaces()
            );
            if (!idleDirections.ok()) {
               this.idle = idleDirections.why();
               this.paused = "combo " + idleDirections.why();
               this.clearCurrentAction(immutablePosValue);
               this.applyAim(MathClampUtils.getYaw(), MathClampUtils.getClamp(), immutablePosValue);
            } else {
               this.currentAction.setAmount(idleDirections.value());
               if (this.currentAction.getBreakObsidianTypeKind() == Kind.BREAK && forcedFaceData.hold()) {
                  this.currentAction.setTime(immutablePosValue);
               }

               this.idle = "ok";
               this.paused = null;
               this.idleTickCount = 0;
               this.executeCurrentAction(this.currentAction, immutablePosValue, livingVecValue);
               if (this.currentAction == null) {
                  this.idle = "out of reach".equals(this.paused) ? "far" : (this.paused == null ? "idle" : this.paused);
               }
            }
         }
      }
   }

   private boolean isAimPointValid(BlockPos pos, EndCrystalEntity endCrystalEntity, Vec3d vec, float entityValue, float entity) {
      if (!BlockEntityUtils.canRaycastVector(BlockEntityUtils.getBox(pos), vec, entityValue, entity, 3.0)) {
         return false;
      } else {
         HitResult multiplySquared = BlockEntityUtils.getMultiplySquared(vec, entityValue, entity);
         return endCrystalEntity == null
            ? BlockEntityUtils.getPosBlock(multiplySquared, pos, Direction.UP, vec, 4.4) != null
            : multiplySquared instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == endCrystalEntity && this.doesLookRayHitTopFace(pos, vec, entityValue, entity);
      }
   }

   private void sampleRotation() {
      float amount = MathClampUtils.getYaw();
      float clamp = MathClampUtils.getClamp();
      if (this.hasRotationSample) {
         double abs3 = Math.abs(BoxLengthUtils.getAmount3((double)amount - this.lastSampleYaw));
         double crystalAuraLimit = Math.abs((double)clamp - this.lastSamplePitch);
         if (abs3 > 1.0E-4 || crystalAuraLimit > 1.0E-4) {
            this.lastYawChange = abs3;
         }
      }

      this.lastSampleYaw = amount;
      this.lastSamplePitch = clamp;
      this.hasRotationSample = true;
   }

   private boolean isControllerFlagSet() {
      return this.weakness != null && this.weakness.isFlag2();
   }

   @Override
   public boolean isAgePlayer() {
      return this.weakness == null && this.isCrystalActionActive();
   }

   private int getPingDelayTicks() {
      return BoxLengthUtils.getMax(EntityDamageUtils.getCachedHurtTimeLimit());
   }

   static long getNowMs() {
      return ConnectionTimeUtils.getTimeTimeMillis();
   }

   private boolean tryRestoreSlot(long centerShowsTime) {
      if (!this.slotSwapActive) {
         return true;
      } else if (this.lastSlotSwapTick != centerShowsTime && !ClientThreadUtils.isOptionsPressed() && !ClientThreadUtils.isEntityActive() && !AutoInventoryTotem.isHandSwapSlotActive()) {
         if (mc.player.getInventory().getSelectedSlot() == this.swappedSlot) {
            if (this.hasSlotPacketThisTick() || this.hasSlotChangedSincePacket()) {
               return false;
            }

            SelectedInventoryUtils.updateSelectedSlot();
            this.lastSlotSwapTick = centerShowsTime;
         } else {
            SelectedInventoryUtils.updateSelectedSlot2();
         }

         this.slotSwapActive = false;
         this.swappedSlot = -1;
         this.obsidianSwapActive = false;
         return true;
      } else {
         return false;
      }
   }

   private boolean isControllerAllowingAction(ImmutablePos immutablePos) {
      return this.weakness == null || this.weakness.canLedgerHandswap(immutablePos);
   }

   private int getPlacementDelayTicks(boolean crystalAuraFlag) {
      if (this.isControllerFlagSet()) {
         return 1;
      } else {
         return crystalAuraFlag ? 2 : this.getPingDelayTicks();
      }
   }

   @Override
   protected void onDisable() {
      if (this.weakness == null) {
         this.restoreSlotOnDisable();
         this.resetAutoState();
      }
   }

   private void renderTargetCircle(Matrix4f matrix, LivingEntity livingEntity, float lerpedWidthValue, int lerpedWidthValue2) {
      Vec3d lerpedPos = livingEntity.getLerpedPos(lerpedWidthValue);
      double lerpedWidthFactor = livingEntity instanceof PlayerEntity ? 0.7 : Math.max(0.3, (double)livingEntity.getWidth());
      float lerpedWidthValue3 = VertexBuilderUtils.getAmount(lerpedPos.y);
      float deltaY = VertexBuilderUtils.getAmount(lerpedPos.y + livingEntity.getHeight());
      int valueInt = 838860800 | lerpedWidthValue2;
      int entity3 = lerpedWidthValue2;
      VertexConsumer buffer = VertexBuilderUtils.getBufferIMMEDIATE();

      for (int i = 0; i < 50; i++) {
         double angle = (Math.PI * 2) * i / 50.0;
         double angle2 = (Math.PI * 2) * (i + 1) / 50.0;
         float amountX = VertexBuilderUtils.getAmountX(lerpedPos.x + Math.cos(angle) * lerpedWidthFactor);
         float amountForDouble = VertexBuilderUtils.getAmountForDouble(lerpedPos.z + Math.sin(angle) * lerpedWidthFactor);
         float amountX8 = VertexBuilderUtils.getAmountX(lerpedPos.x + Math.cos(angle2) * lerpedWidthFactor);
         float amountForDouble9 = VertexBuilderUtils.getAmountForDouble(lerpedPos.z + Math.sin(angle2) * lerpedWidthFactor);
         VertexBuilderUtils.handleVertexColor(
            buffer, matrix, amountX, lerpedWidthValue3, amountForDouble, valueInt, amountX8, lerpedWidthValue3, amountForDouble9, valueInt, amountX8, deltaY, amountForDouble9, entity3, amountX, deltaY, amountForDouble, entity3
         );
      }
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      this.onOutgoingPacket(inventoryPlayer);
   }

   private void restoreSlotOnDisable() {
      if (this.slotSwapActive && mc.player != null && SelectedInventoryUtils.isSelectedSlot()) {
         if (mc.player.getInventory().getSelectedSlot() == this.swappedSlot) {
            SelectedInventoryUtils.updateSelectedSlot();
         } else {
            SelectedInventoryUtils.updateSelectedSlot2();
         }
      }

      this.slotSwapActive = false;
      this.swappedSlot = -1;
      this.obsidianSwapActive = false;
   }

   static boolean isObsidianSupportMatching(ImmutablePos immutablePos, BlockPos pos, Direction direction) {
      return immutablePos != null && immutablePos.getBreakObsidianTypeKind() == Kind.OBSIDIAN && !immutablePos.isTime() && pos != null && direction != null
         ? pos.equals(immutablePos.getDown()) && direction == immutablePos.getUp()
         : true;
   }

   private static boolean isSafeAnchorAutoIdle() {
      return ModuleRegistry.getModule(SafeAnchor.class) instanceof SafeAnchor safeAnchor && safeAnchor.isEnabled() && safeAnchor.isAutoComboRunning();
   }

   private boolean hasSlotChangedSincePacket() {
      return this.lastPacketSelectedSlot >= 0 && mc.player.getInventory().getSelectedSlot() != this.lastPacketSelectedSlot;
   }

   private void runTick(boolean playerPosFlag) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         long playerPosValue = ClientThreadUtils.getTime();
         long time = getNowMs();
         if (mc.world != this.world) {
            this.world = mc.world;
            this.abortSlotSwap();
            this.resetAutoState();
            this.resetPacketAgeTracking();
         }

         if (mc.player != this.player) {
            this.player = mc.player;
            this.lastSlotPacketAge = Integer.MIN_VALUE;
            this.lastActionPacketAge = Integer.MIN_VALUE;
         }

         this.sampleRotation();
         if (mc.player.isDead()) {
            this.deathPos = mc.player.getBlockPos();
            this.deathTimeMs = time;
         }

         this.actionTracker.handleWindowedTimes(time, playerPosValue);
         this.dropPlacementsWithCrystals();
         this.updateObsidianTracking(playerPosValue, time);
         if (this.slotSwapActive && !SelectedInventoryUtils.isSelectedSlot()) {
            this.slotSwapActive = false;
            this.swappedSlot = -1;
            this.obsidianSwapActive = false;
         }

         if (mc.currentScreen != null || mc.player.isDead() || Freecam.isInstanceFlag()) {
            this.cancelActionAndAim(playerPosValue, false);
            this.resetIdleTimer(time);
            this.highlightedPos = null;
         } else if (!playerPosFlag) {
            this.idleTickCount++;
         } else {
            if (this.isManualMode()) {
               if (this.currentAction != null) {
                  this.clearCurrentAction(playerPosValue);
               }

               this.resetIdleTimer(time);
               this.continueAimRestore(playerPosValue);
               this.runManualTick(playerPosValue, time);
            } else {
               this.highlightedPos = null;
               this.runAutoTick(playerPosValue, time);
            }

            this.restoreSlotWhenIdle(playerPosValue);
         }
      }
   }

   private void cancelActionAndAim(long crystalsImmutableValue, boolean crystalsImmutableFlag) {
      if (this.currentAction != null) {
         this.clearCurrentAction(crystalsImmutableValue);
      }

      if (!crystalsImmutableFlag) {
         this.restoringAim = false;
      }
   }

   private void runManualTick(long posBlockValue, long expandEntitiesValue) {
      this.highlightedPos = null;
      if (this.isManualPlacementHeld()) {
         ClientPlayerEntity player = mc.player;
         if (!this.restoringAim && !player.isUsingItem() && !isRotationOutOfSync(player) && !isSafeAnchorAutoIdle()) {
            if (!this.stopOnLoot.isEnabled() || !this.isRelevantLootNearby(expandEntitiesValue)) {
               if (this.obsidianSwapActive) {
                  if (!this.tryRestoreSlot(posBlockValue)) {
                     return;
                  }

                  if (!player.getMainHandStack().isOf(Items.END_CRYSTAL)) {
                     return;
                  }
               }

               Vec3d eyePos = player.getEyePos();
               float yaw = player.getYaw();
               float pitch = player.getPitch();
               HitResult multiplySquared = BlockEntityUtils.getMultiplySquared(eyePos, yaw, pitch);
               boolean breakingInteraction = this.isInteractionAvailable();
               if (multiplySquared instanceof EntityHitResult entityHitResult) {
                  if (entityHitResult.getEntity() instanceof EndCrystalEntity endCrystalEntity) {
                     this.breakCrystalUnderCrosshair(endCrystalEntity, breakingInteraction, posBlockValue, expandEntitiesValue);
                  }
               } else if (multiplySquared instanceof BlockHitResult blockHitResult) {
                  BlockPos blockPos = blockHitResult.getBlockPos();
                  if (BlockEntityUtils.canBedrockObsidian(blockPos)) {
                     this.placeCrystalUnderCrosshair(blockHitResult, blockPos, breakingInteraction, yaw, pitch, posBlockValue, expandEntitiesValue);
                  } else if (this.autoObsidian.isEnabled()) {
                     this.placeSupportObsidian(blockHitResult, breakingInteraction, yaw, pitch, posBlockValue, expandEntitiesValue);
                  }
               }
            }
         }
      }
   }

   private boolean isTradeModeAction(ImmutablePos immutablePos, long livingVecValue) {
      return immutablePos != null && this.controllerTargetData != null && this.controllerTargetData.tuning() != null && this.controllerTargetData.tuning().isTradeMode()
         ? isActionKindAllowed(
            immutablePos.getBreakObsidianTypeKind(), immutablePos.isTime(), immutablePos.getBreakObsidianTypeKind() == Kind.PLACE && this.actionTracker.canTime(immutablePos.getImmutable().asLong(), livingVecValue)
         )
         : false;
   }

   public TickStatusData tickControlled(TargetSelfDataTarget livingVecData, boolean weaknessLivingFlag) {
      if (this.weakness != null
         && livingVecData != null
         && mc.player != null
         && mc.world != null
         && mc.interactionManager != null) {
         this.controllerTargetData = livingVecData;
         this.idle = null;
         this.actionTracker.handleTime(this.isControllerFlagSet() ? Math.max(150L, 2L * EntityDamageUtils.getCachedHurtTimeLimit() + 100L) : 1000L);
         this.runTick(weaknessLivingFlag);
         return this.getStatusSnapshot();
      } else {
         return this.getStatusSnapshot();
      }
   }

   private float getReferenceYaw() {
      return this.weakness == null ? SilentAim.getYawSilent() : mc.player.getYaw();
   }

   private void requestControllerSlot(int selectedSlot, long time) {
      this.wantedSlot = selectedSlot;
      this.wantedSlotRequestTick = time;
      this.weakness.handleSelectedInventory(selectedSlot);
   }

   public boolean isCrystalActionActive() {
      if ((this.isEnabled() || this.weakness != null) && mc.player != null && mc.options != null && !Freecam.isInstanceFlag()) {
         if (this.restoringAim) {
            return true;
         } else {
            return this.isManualMode() ? this.isManualPlacementHeld() : this.currentAction != null;
         }
      } else {
         return false;
      }
   }

   private static long randomStartDelayMs() {
      return 50L + ThreadLocalRandom.current().nextLong(101L);
   }

   private void resetActionTimestamps() {
      this.lastPlaceTick = Long.MIN_VALUE;
      this.lastBreakTick = Long.MIN_VALUE;
      this.lastBreakBase = null;
      this.lastActionTick = Long.MIN_VALUE;
      this.lastHoldTick = Long.MIN_VALUE;
      this.wantedSlotRequestTick = Long.MIN_VALUE;
      this.wantedSlot = -1;
      this.idle = null;
   }

   private boolean isInteractionAvailable() {
      if (ClientThreadUtils.isEntityActive() || AutoInventoryTotem.isHandSwapSlotActive() || mc.player.isUsingItem()) {
         return false;
      } else if (this.hasActionPacketThisTick()) {
         return false;
      } else if (mc.interactionManager.isBreakingBlock()) {
         this.performingOwnAction = true;

         try {
            mc.interactionManager.cancelBlockBreaking();
         } finally {
            this.performingOwnAction = false;
         }

         return false;
      } else {
         return true;
      }
   }

   private void setIdleReason(long pausedLootValue, String paused) {
      this.paused = paused;
      if (this.currentAction != null) {
         this.clearCurrentAction(pausedLootValue);
      }

      this.resetIdleTimer(getNowMs());
      this.idleTickCount++;
      this.continueAimRestore(pausedLootValue);
   }

   private void renderBlockHighlight(Matrix4f matrix4f, BlockPos pos2, int crystalAuraValue) {
      float amountX = VertexBuilderUtils.getAmountX(pos2.getX());
      float f2 = VertexBuilderUtils.getAmount(pos2.getY());
      float amountForDouble = VertexBuilderUtils.getAmountForDouble(pos2.getZ());
      float crystalAuraValue5 = (crystalAuraValue >> 16 & 0xFF) / 255.0F;
      float crystalAuraValue6 = (crystalAuraValue >> 8 & 0xFF) / 255.0F;
      float crystalAuraValue7 = (crystalAuraValue & 0xFF) / 255.0F;
      VertexBuilderUtils.handle(matrix4f, amountX, f2, amountForDouble, amountX + 1.0F, f2 + 1.0F, amountForDouble + 1.0F, crystalAuraValue5, crystalAuraValue6, crystalAuraValue7, 0.12F);
      VertexBuilderUtils.handle(matrix4f, amountX, f2, amountForDouble, amountX + 1.0F, f2 + 1.0F, amountForDouble + 1.0F, -1073741824 | crystalAuraValue, 1.5F);
   }

   private static boolean isKeyPhysicallyDown(KeyBinding keyBinding) {
      if (mc.currentScreen == null && mc.isWindowFocused()) {
         Key boundKey = ((AccessorKeyBinding)keyBinding).getBoundKey();
         long handle = mc.getWindow().getHandle();
         if (boundKey.getCategory() == Type.MOUSE) {
            return GLFW.glfwGetMouseButton(handle, boundKey.getCode()) == 1;
         } else {
            return boundKey.getCategory() != Type.KEYSYM ? false : boundKey.getCode() != InputUtil.UNKNOWN_KEY.getCode() && GLFW.glfwGetKey(handle, boundKey.getCode()) == 1;
         }
      } else {
         return false;
      }
   }

   static int angleToBucket(double crystalAuraLimit) {
      if (Double.isNaN(crystalAuraLimit) || Double.isInfinite(crystalAuraLimit)) {
         return 9;
      } else {
         return !(crystalAuraLimit > 1.0) ? 0 : (int)Math.min(9.0, Math.ceil(crystalAuraLimit / 20.0));
      }
   }

   private double getKeepConeDegrees() {
      return this.weakness != null && this.controllerTargetData != null && this.controllerTargetData.tuning() != null ? this.controllerTargetData.tuning().keepCone() : 110.0;
   }

   private Request buildPlanRequestWithSupport(LivingEntity entity, Predicted predicted, long obsidianValue, long livingVecValue, TargetSelfDataTarget targetSelfDataTarget, ImmutablePos immutablePos) {
      Request obsidianSelf = this.buildPlanRequest(entity, predicted, obsidianValue, livingVecValue, targetSelfDataTarget);
      return immutablePos != null && immutablePos.getBreakObsidianTypeKind() == Kind.OBSIDIAN ? obsidianSelf.getSelfTarget(new Support(immutablePos.getDown(), immutablePos.getUp())) : obsidianSelf;
   }

   public HandleTime getActionTracker() {
      return this.actionTracker;
   }

   private boolean canExecuteAction(ImmutablePos immutablePos, long crystalAuraValue) {
      return immutablePos != null
         && isBreakOrPendingPlace(immutablePos.getBreakObsidianTypeKind(), immutablePos.getBreakObsidianTypeKind() == Kind.PLACE && this.actionTracker.canTime(immutablePos.getImmutable().asLong(), crystalAuraValue));
   }

   @Override
   protected void onEnable() {
      if (this.weakness == null) {
         this.resetAutoState();
         this.resetPacketAgeTracking();
         this.world = mc.world;
         this.player = mc.player;
      }
   }

   private boolean isUseKeyHeld() {
      KeyBinding useKey = mc.options.useKey;
      return useKey.isPressed() || ((AccessorKeyBinding)useKey).corzGetTimesPressed() > 0 || isKeyPhysicallyDown(useKey);
   }

   private void applyAim(float amount, float f2, long yourCenterTime) {
      if (this.weakness != null) {
         this.weakness.handleCrystal(amount, f2);
      } else {
         MathClampUtils.handle(this, amount, f2, 70, true, true);
      }

      this.lastAimTick = yourCenterTime;
      this.lastAimYaw = amount;
      this.lastAimPitch = f2;
   }

   private boolean isBaseJustBroken(BlockPos pos, long immutableValue) {
      return this.isControllerFlagSet() && isRecentlyBrokenBase(pos, this.lastBreakBase, this.lastBreakTick, immutableValue);
   }

   private Vec3d refineAimPoint(BlockPos pos, EndCrystalEntity endCrystalEntity, Vec3d vec, float vec3dValue, float vec3dValue2, Vec3d vec3d) {
      if (pos != null && this.allowsTopFaceClick()) {
         int vec3dValue3 = vec3d == null ? 9 : angleToBucket(getAimErrorToPoint(vec, vec3dValue, vec3dValue2, vec3d));
         Vec3d amountsSquared = this.findBestTopFaceAimPoint(pos, endCrystalEntity, vec, vec3dValue, vec3dValue2, vec3dValue3);
         return amountsSquared != null ? amountsSquared : vec3d;
      } else {
         return vec3d;
      }
   }

   private void swapToSlot(int selectedSlot, long centerShowsTime) {
      SelectedInventoryUtils.handleSelectedSlot(selectedSlot);
      this.slotSwapActive = true;
      this.swappedSlot = selectedSlot;
      this.lastSlotSwapTick = centerShowsTime;
   }

   private boolean allowsTopFaceClick() {
      for (Direction direction : this.getAllowedClickFaces()) {
         if (direction == Direction.UP) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isGodWeakness() {
      return this.weakness == null && this.isCrystalActionActive();
   }

   public boolean attachController(Object object, CrystalSelectedApi weakness) {
      if (object != null && weakness != null && (this.controllerOwner == null || this.controllerOwner == object)) {
         if (this.controllerOwner == object) {
            this.weakness = weakness;
            return true;
         } else {
            this.abortSlotSwap();
            this.resetAutoState();
            if (!this.isEnabled()) {
               this.resetPacketAgeTracking();
               this.world = mc.world;
               this.player = mc.player;
            }

            this.resetActionTimestamps();
            this.controllerOwner = object;
            this.weakness = weakness;
            this.controllerTargetData = null;
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      if (this.centerScreen.isEnabled() && mc.player != null && tickProgress.drawContext != null) {
         DrawContext drawContext = tickProgress.drawContext;
         int inventoryStack = this.countHotbarCrystals();
         boolean weaknessCrystals = this.isCrystalActionActive();
         int color = weaknessCrystals ? -5635841 : (inventoryStack >= 32 ? -16597446 : (inventoryStack >= 16 ? -1774 : -60396));
         String valueOf = String.valueOf(inventoryStack);
         String text = !weaknessCrystals && !this.isManualMode() && this.paused != null ? " [" + this.paused + "]" : "";
         int width = mc.textRenderer.getWidth(valueOf + text);
         int x = drawContext.getScaledWindowWidth() / 2 - width / 2;
         int y = drawContext.getScaledWindowHeight() / 2 + 10;
         drawContext.drawTextWithShadow(mc.textRenderer, valueOf, x, y, color);
         if (!text.isEmpty()) {
            drawContext.drawTextWithShadow(mc.textRenderer, text, x + mc.textRenderer.getWidth(valueOf), y, -43691);
         }
      }
   }

   static boolean isActionKindAllowed(Kind kind, boolean obsidianPlaceFlag, boolean obsidianPlaceFlag2) {
      return kind == Kind.PLACE ? !obsidianPlaceFlag2 : kind == Kind.OBSIDIAN && !obsidianPlaceFlag;
   }
}
