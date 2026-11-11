package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.mixins.AccessorMinecraftClient;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.MathClampUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BotActionType;
import com.corz.client.utils.PlayerInputOverride;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.TickActionLedger;
import com.corz.client.utils.TrackFlick;
import com.corz.client.utils.SwordCombatMath;
import com.corz.client.utils.SwordAttributeAndHazardUtils;

public final class EntityPlayerHook {
   private long lastSetTick;
   private double aimRatioZ;
   private boolean critRequested;
   private Vec3d lerpedPos;
   private long lerpedPosTick;
   private final TickActionLedger gates;
   private final HandswapGate crit;
   public static final String EMPTY_FAIL_REASON = "-";
   private double aimRatioY;
   private final TrackFlick trackFlick;
   private int lerpedPosStepCount;
   private int lerpedEntityId;
   private String lastFailReason;
   private int aimEntityId;
   private final Object ray;
   private long lastAttackTick = Long.MIN_VALUE;
   private final HotbarSlotSwitcher inventorySelected;
   private double aimRatioX;

   public void clearFailReason() {
      this.lastFailReason = "-";
   }

   private Vec3d computeAimPoint(LivingEntity entity, Box box, Vec3d vec, double entityPlayerLimit, double entityPlayerValue) {
      double entityPlayerLimit2 = entityPlayerValue + 0.6;
      if (box.contains(vec)) {
         this.aimEntityId = -1;
         return BlockEntityUtils.getPointVector(box, entity, vec, this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), entityPlayerValue, 0.1);
      } else {
         Vec3d vec3d = this.aimEntityId == entity.getId() && entityPlayerLimit <= entityPlayerLimit2 + 0.4 ? pointInBox(box, this.aimRatioX, this.aimRatioY, this.aimRatioZ) : null;
         if (vec3d != null && !BlockEntityUtils.canSubtractRaycast(vec, vec3d)) {
            vec3d = null;
         }

         if (vec3d == null || !(entityPlayerLimit > entityPlayerValue) && !isShortenedPathInsideBox(box, vec, vec3d, SwordCombatMath.subtractReachMargin(entityPlayerValue))) {
            Vec3d vec3d2 = entityPlayerLimit <= entityPlayerLimit2 ? BlockEntityUtils.getPointVector(box, entity, vec, this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), entityPlayerValue, 0.1) : null;
            Vec3d vec3d3 = vec3d2 != null ? vec3d2 : (vec3d != null ? vec3d : getClampedCenterPoint(box, vec));
            if (entityPlayerLimit <= entityPlayerLimit2 + 0.4) {
               this.rememberAimRatios(entity.getId(), box, vec3d3);
            } else {
               this.aimEntityId = -1;
            }

            return vec3d3;
         } else {
            return vec3d;
         }
      }
   }

   static int nextLerpStepCount(boolean entityPlayerFlag, int entityPlayerValue) {
      return entityPlayerFlag ? Math.max(1, entityPlayerValue - 1) : 3;
   }

   public EntityPlayerHook(Object ray, TrackFlick trackFlick, HandswapGate crit, HotbarSlotSwitcher inventorySelected, TickActionLedger gates2) {
      this.lastFailReason = "-";
      this.aimEntityId = -1;
      this.lastSetTick = Long.MIN_VALUE;
      this.lerpedEntityId = -1;
      this.lerpedPosTick = Long.MIN_VALUE;
      this.ray = ray;
      this.trackFlick = trackFlick;
      this.crit = crit;
      this.inventorySelected = inventorySelected;
      this.gates = gates2;
   }

   public String getLastFailReason() {
      return this.lastFailReason;
   }

   static Vec3d scaleLerpDelta(Vec3d vec, int zeroLengthDivisor) {
      if (vec != null && zeroLengthDivisor > 0) {
         double leftLength = vec.length();
         return Double.isFinite(leftLength) && !(leftLength < 1.0E-4) && !(leftLength / zeroLengthDivisor > 2.0) ? vec.multiply(1.0 / zeroLengthDivisor) : Vec3d.ZERO;
      } else {
         return Vec3d.ZERO;
      }
   }

   public long getLastAttackTick() {
      return this.lastAttackTick;
   }

   public void trackHeading(float headingTrackValue, float routedPartialValue) {
      this.trackFlick.requestSmoothedRotation(BotActionType.HEADING, headingTrackValue, routedPartialValue, TrackFlick.Speed.TRACK, 20);
   }

   public void aimAtTarget(LivingEntity entity, PlayerInputOverride.Intent intent) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.world != null && entity != null) {
         Vec3d eyePos = player.getEyePos();
         Box boundingBox = entity.getBoundingBox();
         double minMax = BlockEntityUtils.getMinMax(eyePos, boundingBox);
         long flickBoundingValue = ClientThreadUtils.getTime();
         Vec3d vec3d = this.computeAimPoint(entity, boundingBox, eyePos, minMax, getInteractionRange(player));
         if (vec3d == null) {
            vec3d = getClampedCenterPoint(boundingBox, eyePos);
         }

         Vec3d zeroSubtract = applyMotionCompensation(vec3d, this.getTargetInterpolationDelta(entity, flickBoundingValue), this.getPredictedPlayerMotion(player, intent, flickBoundingValue));
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, zeroSubtract);
         TrackFlick.Speed speed = minMax < 2.6 ? TrackFlick.Speed.FLICK : (minMax < 6.0 ? TrackFlick.Speed.ACTION : TrackFlick.Speed.TRACK);
         this.trackFlick.requestSmoothedRotation(BotActionType.MELEE, wrapDegrees[0], wrapDegrees[1], speed, 70);
      }
   }

   public void resetAimState() {
      this.aimEntityId = -1;
      this.lastSetTick = Long.MIN_VALUE;
      this.lerpedEntityId = -1;
      this.lerpedPosTick = Long.MIN_VALUE;
      this.lerpedPos = null;
      this.lerpedPosStepCount = 0;
   }

   static boolean isShortenedPathInsideBox(Box box, Vec3d vec, Vec3d vec2, double subtractLengthValue) {
      Vec3d subtract = vec2.subtract(vec);
      double varLength = subtract.length();
      return !(varLength > 1.0E-4) ? true : BoxLengthUtils.canLengthContract(box, vec, vec.add(subtract.multiply(subtractLengthValue / varLength)), 0.1);
   }

   static boolean isMissCooldownActive(MinecraftClient client) {
      return ((AccessorMinecraftClient)client).corzGetAttackCooldown() > 0;
   }

   static Vec3d getClampedCenterPoint(Box box, Vec3d vec) {
      Vec3d center = box.getCenter();
      double minYOffset = box.minY + 0.35;
      double centerBound = box.maxY - 0.25;
      double y = Math.max(minYOffset, Math.min(centerBound, vec.y));
      return new Vec3d(center.x, y, center.z);
   }

   public boolean wasCritRequested() {
      return this.critRequested;
   }

   private Vec3d getPredictedPlayerMotion(ClientPlayerEntity player, PlayerInputOverride.Intent intent2, long glidingClimbingLimit) { com.corz.client.utils.PlayerInputOverride.Tri var41_u = null; 
      Vec3d zero = getEntityMotion(player);
      if (!player.isTouchingWater()
         && !player.isInLava()
         && !player.isClimbing()
         && !player.hasVehicle()
         && !player.isGliding()
         && !player.getAbilities().flying) {
         Vec3d vec3d;
         boolean glidingClimbingFlag;
         boolean glidingClimbingFlag2;
         label84: {
            vec3d = player.getVelocity();
            glidingClimbingFlag = this.lastSetTick != Long.MIN_VALUE && glidingClimbingLimit - this.lastSetTick <= 1L && glidingClimbingLimit >= this.lastSetTick;
            if (intent2 instanceof PlayerInputOverride.Intent) {
               PlayerInputOverride.Intent intent = intent2;

               try {
                  intent.move();
               } catch (Throwable e) {
                  throw new MatchException(e.toString(), e);
               }

               PlayerInputOverride.Intent intent4 = intent2;

               try {
                  intent4.dirX();
               } catch (Throwable e2) {
                  throw new MatchException(e2.toString(), e2);
               }

               PlayerInputOverride.Intent intent5 = intent2;

               try {
                  intent5.dirZ();
               } catch (Throwable e3) {
                  throw new MatchException(e3.toString(), e3);
               }

               PlayerInputOverride.Intent intent6 = intent2;

               try {
                  var41_u = intent6.jump();
               } catch (Throwable e4) {
                  throw new MatchException(e4.toString(), e4);
               }

               PlayerInputOverride.Tri var41_u2 = var41_u;
               PlayerInputOverride.Intent intent7 = intent2;

               try {
                  intent7.sneak();
               } catch (Throwable e5) {
                  throw new MatchException(e5.toString(), e5);
               }

               PlayerInputOverride.Intent intent8 = intent2;

               try {
                  intent8.sprint();
               } catch (Throwable e6) {
                  throw new MatchException(e6.toString(), e6);
               }

               PlayerInputOverride.Intent intent9 = intent2;

               try {
                  intent9.urgent();
               } catch (Throwable e7) {
                  throw new MatchException(e7.toString(), e7);
               }

               if (var41_u2 == PlayerInputOverride.Tri.ON) {
                  glidingClimbingFlag2 = true;
                  break label84;
               }
            }

            glidingClimbingFlag2 = false;
         }

         boolean glidingClimbingFlag3 = glidingClimbingFlag2;
         double y = getVerticalMotion(player.isOnGround(), vec3d.y, glidingClimbingFlag3, getJumpVelocity(player));
         return new Vec3d(glidingClimbingFlag ? vec3d.x : zero.x, y, glidingClimbingFlag ? vec3d.z : zero.z);
      } else {
         return zero;
      }
   }

   static Vec3d applyMotionCompensation(Vec3d vec, Vec3d vec2, Vec3d vec3) {
      if (vec == null) {
         return null;
      } else {
         Vec3d vec3d = vec2 == null ? Vec3d.ZERO : vec2;
         Vec3d vec3d2 = vec3 == null ? Vec3d.ZERO : vec3;
         return vec.add(vec3d.subtract(vec3d2));
      }
   }

   private void rememberAimRatios(int entityPlayerValue, Box box, Vec3d vec) {
      this.aimEntityId = entityPlayerValue;
      this.aimRatioX = getRatioInRange(vec.x, box.minX, box.maxX);
      this.aimRatioY = getRatioInRange(vec.y, box.minY, box.maxY);
      this.aimRatioZ = getRatioInRange(vec.z, box.minZ, box.maxZ);
   }

   private Vec3d getTargetInterpolationDelta(Entity entity, long time) {
      Vec3d zero = getEntityMotion(entity);
      PositionInterpolator interpolator = entity.getInterpolator();
      if (interpolator != null && interpolator.isInterpolating()) {
         Vec3d lerpedPos = interpolator.getLerpedPos();
         boolean lerpedPosFlag = this.lerpedEntityId == entity.getId() && this.lerpedPosTick == time - 1L && this.lerpedPos != null;
         int value = lerpedPosFlag ? nextLerpStepCount(lerpedPos.equals(this.lerpedPos), this.lerpedPosStepCount) : estimateLerpSteps(zero, lerpedPos.subtract(entity.getEntityPos()));
         this.lerpedEntityId = entity.getId();
         this.lerpedPosTick = time;
         this.lerpedPos = lerpedPos;
         this.lerpedPosStepCount = value;
         return scaleLerpDelta(lerpedPos.subtract(entity.getEntityPos()), value);
      } else {
         this.lerpedEntityId = -1;
         return interpolator == null ? zero : Vec3d.ZERO;
      }
   }

   static double getVerticalMotion(boolean entityPlayerFlag, double entityPlayerBound, boolean entityPlayerFlag2, double hashRegistryBound) {
      if (!Double.isFinite(entityPlayerBound)) {
         return 0.0;
      } else if (!entityPlayerFlag) {
         return entityPlayerBound;
      } else {
         return entityPlayerFlag2 ? Math.max(hashRegistryBound, entityPlayerBound) : Math.max(0.0, entityPlayerBound);
      }
   }

   static Vec3d getEntityMotion(Entity entity) {
      if (entity == null) {
         return Vec3d.ZERO;
      } else {
         double x = entity.getX() - entity.lastX;
         double y = entity.getY() - entity.lastY;
         double z = entity.getZ() - entity.lastZ;
         double zeroLimit = x * x + y * y + z * z;
         return Double.isFinite(zeroLimit) && !(zeroLimit > 4.0) ? new Vec3d(x, y, z) : Vec3d.ZERO;
      }
   }

   private static double getJumpVelocity(ClientPlayerEntity player) {
      return player.getAttributeValue(EntityAttributes.JUMP_STRENGTH) + player.getJumpBoostVelocityModifier();
   }

   static double getRatioInRange(double entityPlayerValue, double entityPlayerValue2, double entityPlayerValue3) {
      double entityPlayerLimit = entityPlayerValue3 - entityPlayerValue2;
      return entityPlayerLimit > 1.0E-4 && Double.isFinite(entityPlayerValue) ? Math.max(0.0, Math.min(1.0, (entityPlayerValue - entityPlayerValue2) / entityPlayerLimit)) : 0.5;
   }

   public static boolean canRaycastHitTarget(Vec3d vec, float entityPlayerValue, float currentPitch, LivingEntity entity, double entityPlayerLimit) {
      return !(entityPlayerLimit > 0.0) ? false : BlockEntityUtils.canVectorMultiply(vec, entityPlayerValue, currentPitch, entity, entityPlayerLimit, SwordCombatMath.subtractReachMargin(entityPlayerLimit));
   }

   public boolean isSwordSlotSelected() {
      int inventoryStack = this.findBestSwordSlot();
      return inventoryStack >= 0 && this.inventorySelected.requestSlot(inventoryStack, false);
   }

   public static double getInteractionRange(ClientPlayerEntity player) {
      return SwordCombatMath.clampInteractionRange(player.getAttributeValue(EntityAttributes.ENTITY_INTERACTION_RANGE));
   }

   public int findBestSwordSlot() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         return -1;
      } else {
         double[] doubles = new double[9];

         for (int i = 0; i < 9; i++) {
            doubles[i] = SwordAttributeAndHazardUtils.getSwordDamage(player.getInventory().getStack(i));
         }

         double inventoryStackValue = SwordAttributeAndHazardUtils.getMaxValue(doubles);
         return this.inventorySelected.findSlotMatching(best -> SwordAttributeAndHazardUtils.isSwordWithDamageAtLeast(best, inventoryStackValue), false);
      }
   }

   public boolean tryAttackTarget(LivingEntity entity, float hookGatesLimit, boolean hookFlag, boolean hookGatesFlag) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player == null || instance.world == null || instance.interactionManager == null) {
         return this.failWithReason("player");
      } else if (entity != null && entity.isAlive() && !entity.isRemoved()) {
         int inventoryStack = this.findBestSwordSlot();
         if (inventoryStack < 0) {
            return this.failWithReason("no sword");
         } else if (!this.inventorySelected.requestSlot(inventoryStack, false)) {
            return this.failWithReason("slot");
         } else if (!SwordAttributeAndHazardUtils.isSword(player.getMainHandStack())) {
            return this.failWithReason("fist");
         } else if (isMissCooldownActive(instance)) {
            return this.failWithReason("miss-cd");
         } else if (player.getAttackCooldownProgress(0.5F) < hookGatesLimit) {
            return this.failWithReason("charge");
         } else if (!hookFlag || !hookGatesFlag && isFallingForCrit(player)) {
            if (!this.crit.isSwitchAllowed()) {
               return this.failWithReason("gates");
            } else if (this.gates.isContainerScreenActive()) {
               player.closeHandledScreen();
               return this.failWithReason("invopen");
            } else {
               Vec3d eyePos = player.getEyePos();
               if (!canRaycastHitTarget(eyePos, this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), entity, getInteractionRange(player))) {
                  return this.failWithReason("ray");
               } else if (!ClientThreadUtils.canFlag(this.ray, entity)) {
                  return this.failWithReason("hook");
               } else {
                  instance.interactionManager.attackEntity(player, entity);
                  player.swingHand(Hand.MAIN_HAND);
                  this.lastAttackTick = ClientThreadUtils.getTime();
                  this.critRequested = hookFlag;
                  this.lastFailReason = "-";
                  return true;
               }
            }
         } else {
            return this.failWithReason("crit");
         }
      } else {
         return this.failWithReason("target");
      }
   }

   static int estimateLerpSteps(Vec3d vec, Vec3d vec2) {
      double lengthValue = vec2 == null ? 0.0 : vec2.length();
      double lengthLimit = vec == null ? 0.0 : vec.length();
      return lengthLimit > 1.0E-4 && Double.isFinite(lengthValue) ? (int)Math.max(1L, Math.min(3L, Math.round(lengthValue / lengthLimit))) : 3;
   }

   public void setLastSetTick(long time) {
      this.lastSetTick = time;
   }

   static Vec3d pointInBox(Box box, double entityPlayerFactor, double entityPlayerFactor2, double entityPlayerFactor3) {
      return new Vec3d(box.minX + entityPlayerFactor * (box.maxX - box.minX), box.minY + entityPlayerFactor2 * (box.maxY - box.minY), box.minZ + entityPlayerFactor3 * (box.maxZ - box.minZ));
   }

   public static boolean isFallingForCrit(ClientPlayerEntity player) {
      return !player.isOnGround()
         && player.fallDistance > 0.0
         && player.getVelocity().y < 0.0
         && !player.isSprinting()
         && !player.isTouchingWater()
         && !player.isClimbing()
         && !player.hasVehicle()
         && !player.isInLava();
   }

   private boolean failWithReason(String hookText) {
      this.lastFailReason = hookText;
      return false;
   }
}
