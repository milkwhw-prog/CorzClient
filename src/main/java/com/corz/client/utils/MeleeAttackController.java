package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.EntityPlayerUtilsGliding;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BotActionType;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.HotbarSlotRules;
import com.corz.client.utils.SprintResetTracker;
import com.corz.client.utils.TrackFlick;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.AuditOwn;

public final class MeleeAttackController {
   private int settleStreakCount;
   private long lastAttackTick = Long.MIN_VALUE;
   private String statusReason = "-";
   private float expectedDamage;
   public static final String DASH_PLACEHOLDER = "-";
   private final Object gates;
   private Vec3d settlePlayerPos;
   private float rawAttackDamage;
   private long lastSettleTick;
   private final HandswapGate ledgerHandswap;
   private final TrackFlick reach;
   private long settleStartTick;
   private float lastSettleYaw;
   private final HotbarSlotSwitcher inventorySelected;
   private float lastSettlePitch;
   private int trackedEntityId = -1;
   public static final String LOS = "los";
   private boolean sprintAttack;
   private final AuditOwn auditOwn;
   private Vec3d settleTargetPos;

   private int chooseStrikeSlot(ClientPlayerEntity player, LivingEntity entity, CenterVecData.Strike strike, boolean inventoryStackFlag) {
      int weaponSlot = strike.weaponSlot();
      return !inventoryStackFlag && weaponSlot >= 0 && weaponSlot < 9 && isSlotAllowedForWeapon(weaponSlot) && InventoryStackUtilsEmpty.canSwordsEmpty(player.getInventory().getStack(weaponSlot))
         ? weaponSlot
         : this.findBestWeaponSlot(entity);
   }

   public boolean tryAttackWithStrike(VecBlockingData vecBlockingData, CenterVecData.Strike strike, int reachWeaponLimit) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.world != null && instance.interactionManager != null) {
         LivingEntity livingEntity = vecBlockingData == null ? null : vecBlockingData.entity();
         if (strike != null && livingEntity != null && livingEntity.isAlive() && !livingEntity.isRemoved()) {
            long reachWeaponValue = ClientThreadUtils.getTime();
            int reachWeaponLimit2 = 2 + (int)Math.ceil(Math.max(0.0, (double)EntityDamageUtils.getTime()) / 50.0);
            if (this.lastAttackTick != Long.MIN_VALUE && reachWeaponValue - this.lastAttackTick <= reachWeaponLimit2 && vecBlockingData.ticksSinceFresh() >= reachWeaponValue - this.lastAttackTick) {
               return this.failWithReason("pending");
            } else {
               boolean entityBlocking = isTargetBlocking(vecBlockingData);
               int inventoryStack = this.chooseStrikeSlot(player, livingEntity, strike, entityBlocking);
               if (inventoryStack < 0) {
                  return this.failWithReason(entityBlocking ? "shield" : "weapon");
               } else {
                  ItemStack stack = player.getInventory().getStack(inventoryStack);
                  if (stack.isEmpty()) {
                     return this.failWithReason("weapon");
                  } else if (entityBlocking && !InventoryStackUtilsEmpty.canSecondsDisable(stack)) {
                     return this.failWithReason("shield");
                  } else {
                     Vec3d eyePos = player.getEyePos();
                     Box boundingBox = livingEntity.getBoundingBox();
                     double minMax = BlockEntityUtils.getMinMax(eyePos, boundingBox);
                     Vec3d squaredDistance = chooseAimPoint(eyePos, boundingBox, livingEntity, strike.aimPoint());
                     float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, applyRelativeMotion(squaredDistance, getMovementDelta(livingEntity), getMovementDelta(player)));
                     TrackFlick.Speed speed = minMax < 2.5 ? TrackFlick.Speed.FLICK : TrackFlick.Speed.ACTION;
                     float attackCooldownProgress = player.getAttackCooldownProgress(0.5F);
                     float entityShieldValue = getAttackAttribute(player, stack) + getSharpnessBonus(stack);
                     if (reachWeaponLimit > 0 && !canDamageThroughIframes(entityShieldValue, vecBlockingData.lastScaledTaken(), attackCooldownProgress)) {
                        this.reach.requestSmoothedRotation(BotActionType.MELEE, wrapDegrees[0], wrapDegrees[1], speed, 70);
                        return this.failWithReason("iframe");
                     } else if (!(minMax <= 2.95)) {
                        this.reach.requestSmoothedRotation(BotActionType.MELEE, wrapDegrees[0], wrapDegrees[1], speed, 70);
                        return this.failWithReason("reach");
                     } else {
                        float currentYaw = this.reach.getCurrentYaw();
                        float currentPitch = this.reach.getCurrentPitch();
                        if (!isRotationHittable(eyePos, currentYaw, currentPitch, livingEntity)) {
                           float[] amountTrack = this.reach.lockTrackRotation(BotActionType.MELEE, wrapDegrees[0], wrapDegrees[1], speed, 70, 8.0F);
                           return this.recordSettleTick(livingEntity, player.getEntityPos(), currentYaw, currentPitch, reachWeaponValue, amountTrack == null ? "ray" : "settle");
                        } else {
                           this.settleStreakCount = 0;
                           this.reach.requestSmoothedRotation(BotActionType.MELEE, wrapDegrees[0], wrapDegrees[1], speed, 70);
                           if (!this.ledgerHandswap.isSwitchAllowed()) {
                              return this.failWithReason("gates");
                           } else if (!ClientThreadUtils.canFlag(this.gates, livingEntity)) {
                              return this.failWithReason("hook");
                           } else {
                              boolean reachWeaponFlag = player.isSprinting() && attackCooldownProgress > 0.9F;
                              if (!this.inventorySelected.trySelectSlot(inventoryStack, ClientThreadUtils.getTime())) {
                                 return this.failWithReason("slot");
                              } else {
                                 this.recordAttackDamage(entityShieldValue, vecBlockingData.armor(), vecBlockingData.meleeEpf(), reachWeaponFlag);
                                 return this.performAttack(instance, player, livingEntity, eyePos, currentYaw, currentPitch);
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else {
            return this.failWithReason("target");
         }
      } else {
         return this.failWithReason("player");
      }
   }

   private boolean isSlotUsableWeapon(ClientPlayerEntity player, int inventoryStackLimit) {
      return inventoryStackLimit >= 0 && inventoryStackLimit < 9 && isSlotAllowedForWeapon(inventoryStackLimit) ? isMeleeWeapon(player.getInventory().getStack(inventoryStackLimit)) : false;
   }

   private static boolean isTargetBlocking(VecBlockingData vecBlockingData) {
      return vecBlockingData != null && vecBlockingData.entity() != null && vecBlockingData.blocking() && EntityPlayerUtilsGliding.canPlayerInstance(vecBlockingData.entity());
   }

   static boolean isAimStationary(boolean entityShieldFlag, double entityShieldValue) {
      return entityShieldFlag && entityShieldValue <= 2.0;
   }

   public float getExpectedDamage() {
      return this.expectedDamage;
   }

   private boolean recordSettleTick(LivingEntity entity, Vec3d vec3d, float amount, float currentPitch, long time, String gatesSettleText) {
      boolean gatesSettleFlag = entity.getId() == this.trackedEntityId
         && isAimStationary(this.lastSettleTick != Long.MIN_VALUE && time == this.lastSettleTick + 1L, BoxLengthUtils.getAmount(this.lastSettleYaw, this.lastSettlePitch, amount, currentPitch));
      this.settleStreakCount = gatesSettleFlag ? this.settleStreakCount + 1 : 1;
      this.trackedEntityId = entity.getId();
      this.lastSettleTick = time;
      this.lastSettleYaw = amount;
      this.lastSettlePitch = currentPitch;
      if (this.settleStreakCount >= 8 && this.settleTargetPos == null) {
         this.settleTargetPos = entity.getEntityPos();
         this.settlePlayerPos = vec3d;
         this.settleStartTick = time;
      }

      return this.failWithReason(gatesSettleText);
   }

   static double getAimYawTolerance(double entityShieldLimit, double groundY) {
      double entityShieldValue = Double.isFinite(groundY) && groundY > 0.0 ? groundY : 0.0;
      double entityShieldValue2 = entityShieldValue * 0.5 - Math.min(0.1, entityShieldValue * 0.25);
      return !(entityShieldLimit > 0.0) ? 72.0 : 0.8 * Math.toDegrees(Math.atan(entityShieldValue2 / entityShieldLimit));
   }

   static float computeDamageAfterArmor(float toughnessAmpValue, PosBlockUtilsBonus.Armor armor, float entityShieldValue) {
      PosBlockUtilsBonus.Armor armor3 = armor == null ? PosBlockUtilsBonus.Armor.INSTANCE : armor;
      return PosBlockUtilsBonus.getToughnessEpf(toughnessAmpValue, new PosBlockUtilsBonus.Armor(armor3.armor(), armor3.toughness(), armor3.resistanceAmp(), entityShieldValue, -1));
   }

   private boolean isSettleStalled(LivingEntity entity, Vec3d vec, long entityDistanceValue) {
      if (this.settleTargetPos != null && this.settlePlayerPos != null) {
         if (entity.getId() == this.trackedEntityId && !hasMovedOrTimedOut(entity.getEntityPos().distanceTo(this.settleTargetPos), vec.distanceTo(this.settlePlayerPos), entityDistanceValue - this.settleStartTick)) {
            return true;
         } else {
            this.resetSettle();
            return false;
         }
      } else {
         return false;
      }
   }

   public CaseMaxData evaluateMelee(SelfPopData selfPopData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      VecBlockingData vecBlockingData = selfPopData == null ? null : selfPopData.target();
      if (player != null && instance.world != null && vecBlockingData != null && vecBlockingData.entity() != null) {
         LivingEntity entity = vecBlockingData.entity();
         Vec3d eyePos = player.getEyePos();
         double minMax = BlockEntityUtils.getMinMax(eyePos, entity.getBoundingBox());
         if (minMax > 3.4) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.MELEE, "far");
         } else if (this.isSettleStalled(entity, player.getEntityPos(), ClientThreadUtils.getTime())) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.MELEE, "los");
         } else {
            boolean entityBlocking = isTargetBlocking(vecBlockingData);
            int selectedInventory = this.findBestWeaponSlot(entity);
            if (selectedInventory < 0) {
               return CaseMaxData.getCaseMaxData(SurroundKitType.MELEE, entityBlocking ? "shield" : "no weapon");
            } else {
               ItemStack stack = player.getInventory().getStack(selectedInventory);
               boolean shieldMeleeFlag = player.getInventory().getSelectedSlot() == selectedInventory;
               int attackProgress = getTicksUntilAttackReady(player, stack, shieldMeleeFlag);
               if (!entityBlocking) {
                  float attackAttribute = getAttackAttribute(player, stack);
                  float sharpness = getSharpnessBonus(stack);
                  PosBlockUtilsBonus.Armor armor = vecBlockingData.armor() == null ? PosBlockUtilsBonus.Armor.INSTANCE : vecBlockingData.armor();
                  int shieldMeleeLimit = attackProgress + getSelfLoopTicks(selfPopData);
                  float shieldMeleeFactor;
                  if (selfPopData.targetReadyIn() <= shieldMeleeLimit) {
                     shieldMeleeFactor = VecShieldUtilsToughness.getToughnessAmp(attackAttribute, sharpness, 0.95F, false, armor, vecBlockingData.meleeEpf());
                  } else {
                     float shieldMeleeValue = VecShieldUtilsToughness.getAmount(attackAttribute, sharpness, 0.95F, false);
                     float lastScaledTaken = vecBlockingData.lastScaledTaken();
                     float toughnessEpfLimit = Float.isFinite(lastScaledTaken) ? Math.max(0.0F, shieldMeleeValue - lastScaledTaken) : 0.0F;
                     shieldMeleeFactor = PosBlockUtilsBonus.getToughnessEpf(
                        toughnessEpfLimit, new PosBlockUtilsBonus.Armor(armor.armor(), armor.toughness(), armor.resistanceAmp(), vecBlockingData.meleeEpf(), -1)
                     );
                  }

                  float shieldMeleeFactor2 = vecBlockingData.calibration() > 0.0F && Float.isFinite(vecBlockingData.calibration()) ? vecBlockingData.calibration() : 1.0F;
                  boolean shieldMeleeFlag2 = shieldMeleeFactor * shieldMeleeFactor2 >= vecBlockingData.getHealthTrusted();
                  boolean shieldMeleeFlag3 = shieldMeleeFlag2 && (vecBlockingData.awaitingTotem() || !vecBlockingData.totemInHand());
                  return new CaseMaxData(SurroundKitType.MELEE, true, shieldMeleeFactor, attackProgress, shieldMeleeFlag2, shieldMeleeFlag3, shieldMeleeFlag ? "ready" : "select");
               } else {
                  return !InventoryStackUtilsEmpty.canSecondsDisable(stack)
                     ? CaseMaxData.getCaseMaxData(SurroundKitType.MELEE, "shield")
                     : new CaseMaxData(SurroundKitType.MELEE, true, 6.0, attackProgress, false, false, "shield");
               }
            }
         }
      } else {
         return CaseMaxData.getCaseMaxData(SurroundKitType.MELEE, "no target");
      }
   }

   static float getAttackAttribute(ClientPlayerEntity player, ItemStack stack) {
      double attributeBaseValue = player.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE);
      double attackAttributeValue = player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE) - attributeBaseValue - sumAddModifiers(player.getMainHandStack(), EntityAttributes.ATTACK_DAMAGE);
      return (float)Math.max(0.0, attributeBaseValue + sumAddModifiers(stack, EntityAttributes.ATTACK_DAMAGE) + attackAttributeValue);
   }

   public int findBestWeaponSlot(LivingEntity entity) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player == null) {
         return -1;
      } else {
         if (entity != null && EntityPlayerUtilsGliding.canPlayerInstance(entity)) {
            int selectedSlot = this.inventorySelected.findSlotMatching(InventoryStackUtilsEmpty::canSecondsDisable, false);
            if (selectedSlot >= 0) {
               return selectedSlot;
            }
         }

         int entityShieldValue = InventoryStackUtilsEmpty.getSelectedSlot(-1);
         return entityShieldValue >= 0 && isSlotAllowedForWeapon(entityShieldValue) ? entityShieldValue : this.inventorySelected.findSlotMatching(InventoryStackUtilsEmpty::canSwordsEmpty, false);
      }
   }

   public boolean tryAttackEntity(LivingEntity entity, boolean reachAttackFlag) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.world != null && instance.interactionManager != null && entity != null && entity.isAlive()) {
         int selectedInventory = this.findBestWeaponSlot(entity);
         if (selectedInventory < 0) {
            return false;
         } else {
            Vec3d eyePos = player.getEyePos();
            Box boundingBox = entity.getBoundingBox();
            if (!this.inventorySelected.requestSlot(selectedInventory, false)) {
               this.aimMeleeToward(eyePos, getTorsoPoint(boundingBox), TrackFlick.Speed.ACTION);
               return false;
            } else {
               Vec3d pointVector = BlockEntityUtils.getPointVector(boundingBox, entity, eyePos, this.reach.getCurrentYaw(), this.reach.getCurrentPitch(), 2.95, 0.1);
               if (pointVector == null) {
                  pointVector = getTorsoPoint(boundingBox);
               }

               double minMax = BlockEntityUtils.getMinMax(eyePos, boundingBox);
               this.aimMeleeToward(
                  eyePos, applyRelativeMotion(pointVector, getMovementDelta(entity), getMovementDelta(player)), minMax < 2.5 ? TrackFlick.Speed.FLICK : TrackFlick.Speed.ACTION
               );
               if (!this.ledgerHandswap.isSwitchAllowed()) {
                  return false;
               } else if (player.getAttackCooldownProgress(0.5F) < 0.95F) {
                  return false;
               } else if (reachAttackFlag && !isCritFalling(player)) {
                  return false;
               } else if (!isRotationHittable(eyePos, this.reach.getCurrentYaw(), this.reach.getCurrentPitch(), entity)) {
                  return false;
               } else if (!ClientThreadUtils.canFlag(this.gates, entity)) {
                  return false;
               } else {
                  ItemStack stack = player.getInventory().getStack(selectedInventory);
                  float attackCooldownProgress = player.getAttackCooldownProgress(0.5F);
                  this.recordAttackDamage(
                     getAttackAttribute(player, stack) + getSharpnessBonus(stack),
                     BlockEntityUtils.getAttributeArmor(entity),
                     BlockEntityUtils.getRegistryEquipped(entity),
                     player.isSprinting() && attackCooldownProgress > 0.9F
                  );
                  return this.performAttack(instance, player, entity, eyePos, this.reach.getCurrentYaw(), this.reach.getCurrentPitch());
               }
            }
         }
      } else {
         return false;
      }
   }

   private static Vec3d getMovementDelta(Entity entity) {
      return entity == null ? Vec3d.ZERO : getZero(entity.getX(), entity.getY(), entity.getZ(), entity.lastX, entity.lastY, entity.lastZ);
   }

   private static boolean isPointHittable(Vec3d vec, Vec3d vec2, LivingEntity entity) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, vec2);
      return isRotationHittable(vec, wrapDegrees[0], wrapDegrees[1], entity);
   }

   public static boolean isBoxOnProjectedPath(Box box, Vec3d vec, Vec3d vec2, double multiplyBound) {
      return box != null && vec != null && vec2 != null ? BoxLengthUtils.canLengthContract(box, vec, vec.add(vec2.multiply(Math.max(0.0, multiplyBound))), 0.1) : false;
   }

   public long getLastAttackTick() {
      return this.lastAttackTick;
   }

   static Vec3d getLowBodyPoint(Box box) {
      Vec3d center = box.getCenter();
      return new Vec3d(center.x, Math.min(box.maxY - 0.1, box.minY + 0.45), center.z);
   }

   private boolean performAttack(MinecraftClient client, ClientPlayerEntity player, LivingEntity entity, Vec3d vec, float fistHandValue, float currentPitch) {
      if (!isMeleeWeapon(player.getMainHandStack())) {
         return this.failWithReason("fist");
      } else {
         double raycastVector = this.getAimRayDistance(player, entity, vec, fistHandValue, currentPitch);
         client.interactionManager.attackEntity(player, entity);
         player.swingHand(Hand.MAIN_HAND);
         if (this.auditOwn != null) {
            this.auditOwn.checkReach(raycastVector, 3.0, true);
         }

         this.lastAttackTick = ClientThreadUtils.getTime();
         this.statusReason = "-";
         this.resetSettle();
         return true;
      }
   }

   private int pickUsableSlot(ClientPlayerEntity player, int entityShieldValue, int inventoryStackLimit) {
      if (this.isSlotUsableWeapon(player, entityShieldValue)) {
         return entityShieldValue;
      } else {
         return this.isSlotUsableWeapon(player, inventoryStackLimit) ? inventoryStackLimit : -1;
      }
   }

   static boolean canDamageThroughIframes(float entityShieldValue, float routedPartialValue, float entityShieldLimit) {
      return IframeComboUtils.canFlagForFloat(entityShieldValue, routedPartialValue) && entityShieldLimit >= 0.9F;
   }

   static Vec3d getZero(double zeroValue, double zeroValue2, double zeroValue3, double zeroValue4, double zeroValue5, double zeroValue6) {
      double x2 = zeroValue - zeroValue4;
      double y2 = zeroValue2 - zeroValue5;
      double z2 = zeroValue3 - zeroValue6;
      double zeroLimit = x2 * x2 + y2 * y2 + z2 * z2;
      return Double.isFinite(zeroLimit) && !(zeroLimit > 4.0) ? new Vec3d(x2, y2, z2) : Vec3d.ZERO;
   }

   private static int getSelfLoopTicks(SelfPopData selfPopData) {
      double selfLoopValue = selfPopData.self() == null ? 0.0 : selfPopData.self().loopMs();
      return (int)Math.ceil(Math.max(0.0, Double.isFinite(selfLoopValue) ? selfLoopValue : 0.0) / 50.0);
   }

   private static double normalizeAngle(double entityShieldValue) {
      return (entityShieldValue % 360.0 + 540.0) % 360.0 - 180.0;
   }

   private static boolean isSameAttribute(RegistryEntry<EntityAttribute> registryEntry, RegistryEntry<EntityAttribute> registryEntry2) {
      if (registryEntry == registryEntry2) {
         return true;
      } else {
         var optional = registryEntry2.getKey();
         return optional.isPresent() && registryEntry.matchesKey((RegistryKey)optional.get());
      }
   }

   private void aimMeleeToward(Vec3d vec, Vec3d vec2, TrackFlick.Speed speed) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, vec2);
      this.reach.requestSmoothedRotation(BotActionType.MELEE, wrapDegrees[0], wrapDegrees[1], speed, 70);
   }

   public static boolean canLaunchStrike(
      boolean entityShieldFlag,
      float entityShieldLimit,
      boolean entityShieldFlag2,
      boolean reachFistFlag,
      boolean entityShieldFlag4,
      boolean entityShieldFlag5,
      int entityShieldValue,
      double entityShieldValue2,
      double entityShieldLimit2,
      boolean rotationHittable
   ) {
      return entityShieldFlag
         && entityShieldLimit >= 0.95F
         && (entityShieldFlag2 ? reachFistFlag : !reachFistFlag)
         && (!entityShieldFlag5 || entityShieldFlag4)
         && entityShieldValue <= 0
         && entityShieldValue2 <= entityShieldLimit2
         && rotationHittable;
   }

   static float getSharpnessBonus(ItemStack stack) {
      int enchantmentsDefault = InventoryStackUtilsEmpty.getEnchantmentsDefault(stack, Enchantments.SHARPNESS);
      return enchantmentsDefault > 0 ? 0.5F * enchantmentsDefault + 0.5F : 0.0F;
   }

   public boolean isSprintAttack() {
      return this.sprintAttack;
   }

   static boolean hasMovedOrTimedOut(double entityShieldLimit, double vecHashValue, long entityShieldLimit3) {
      return !Double.isNaN(entityShieldLimit) && !Double.isNaN(vecHashValue) ? entityShieldLimit >= 0.5 || vecHashValue >= 0.5 || entityShieldLimit3 < 0L || entityShieldLimit3 >= 40L : true;
   }

   private void recordAttackDamage(float entityShieldBound, PosBlockUtilsBonus.Armor armor, float entityShieldValue, boolean flag) {
      this.rawAttackDamage = Float.isFinite(entityShieldBound) ? Math.max(0.0F, entityShieldBound) : 0.0F;
      this.expectedDamage = computeDamageAfterArmor(this.rawAttackDamage, armor, entityShieldValue);
      this.sprintAttack = flag;
   }

   public String getStatusReason() {
      return this.statusReason;
   }

   private static boolean isCritFalling(ClientPlayerEntity player) {
      return !player.isOnGround()
         && player.fallDistance > 0.0
         && player.getVelocity().y < 0.0
         && !player.isSprinting()
         && !player.isTouchingWater()
         && !player.isClimbing()
         && !player.hasVehicle();
   }

   public MeleeAttackController(Object gates2, TrackFlick reach, HandswapGate ledgerHandswap, HotbarSlotSwitcher inventorySelected, AuditOwn auditOwn) {
      this.lastSettleTick = Long.MIN_VALUE;
      this.settleStartTick = Long.MIN_VALUE;
      this.gates = gates2;
      this.reach = reach;
      this.ledgerHandswap = ledgerHandswap;
      this.inventorySelected = inventorySelected;
      this.auditOwn = auditOwn;
   }

   private void resetSettle() {
      this.settleStreakCount = 0;
      this.lastSettleTick = Long.MIN_VALUE;
      this.settleTargetPos = null;
      this.settlePlayerPos = null;
      this.settleStartTick = Long.MIN_VALUE;
   }

   static boolean isMeleeWeapon(ItemStack stack) {
      return stack != null && !stack.isEmpty() && (InventoryStackUtilsEmpty.canSwordsEmpty(stack) || InventoryStackUtilsEmpty.canSecondsDisable(stack));
   }

   private static double sumAddModifiers(ItemStack stack, RegistryEntry<EntityAttribute> registryEntry) {
      if (stack != null && !stack.isEmpty()) {
         double[] doubles = new double[1];
         stack.applyAttributeModifiers(EquipmentSlot.MAINHAND, (mod, attributex) -> {
            if (isSameAttribute(mod, registryEntry) && attributex.operation() == Operation.ADD_VALUE) {
               doubles[0] += attributex.value();
            }
         });
         return doubles[0];
      } else {
         return 0.0;
      }
   }

   private boolean failWithReason(String text) {
      this.statusReason = text;
      return false;
   }

   static Vec3d applyRelativeMotion(Vec3d vec, Vec3d vec2, Vec3d vec3) {
      if (vec == null) {
         return null;
      } else {
         Vec3d vec3d = vec2 == null ? Vec3d.ZERO : vec2;
         Vec3d vec3d2 = vec3 == null ? Vec3d.ZERO : vec3;
         return vec.add(vec3d.subtract(vec3d2));
      }
   }

   private static boolean isSlotAllowedForWeapon(int entityShieldValue) {
      boolean[] booleans = new boolean[9];
      boolean[] booleans2 = new boolean[9];

      for (int i = 0; i < 9; i++) {
         booleans[i] = AutoInventoryTotem.wasTotemSlot(i);
         booleans2[i] = AutoInventoryTotem.isSlotInPendingSwap(i);
      }

      return !HotbarSlotRules.isSlotUnavailable(entityShieldValue, AutoInventoryTotem.getReserveSlotIfEnabled(), booleans, booleans2, false);
   }

   public static boolean isRotationHittable(Vec3d vec, float entityShieldValue, float currentPitch, LivingEntity entity) {
      return BlockEntityUtils.canVectorMultiply(vec, entityShieldValue, currentPitch, entity, 2.95, 2.95);
   }

   private double getAimRayDistance(ClientPlayerEntity player, LivingEntity entity, Vec3d vec, float raycastVectorValue, float currentPitch) {
      Box boundingBox = entity.getBoundingBox();
      if (boundingBox.contains(vec)) {
         return 0.0;
      } else {
         Vec3d rotationVector = player.getRotationVector(currentPitch, raycastVectorValue);
         var raycast = boundingBox.raycast(vec, vec.add(rotationVector.multiply(4.0)));
         return raycast.map(vec::distanceTo).orElseGet(() -> BlockEntityUtils.getMinMax(vec, boundingBox));
      }
   }

   private static Vec3d chooseAimPoint(Vec3d vec, Box box, LivingEntity entity, Vec3d squaredDistance) {
      boolean squaredDistanceFlag = BlockEntityUtils.getMinMax(vec, box) <= 2.95;
      if (squaredDistanceFlag && squaredDistance != null && isPointHittable(vec, squaredDistance, entity)) {
         return squaredDistance;
      } else {
         Vec3d center = box.getCenter();
         double[][] doubles = IframeComboUtils.getAmounts(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, center.x - vec.x, center.z - vec.z);
         Vec3d vec3d = null;

         for (double[] squaredDistanceValues : doubles) {
            Vec3d vec3d2 = new Vec3d(squaredDistanceValues[0], squaredDistanceValues[1], squaredDistanceValues[2]);
            if (squaredDistanceFlag && isPointHittable(vec, vec3d2, entity)) {
               return vec3d2;
            }

            if (vec3d == null || vec.squaredDistanceTo(vec3d2) < vec.squaredDistanceTo(vec3d)) {
               vec3d = vec3d2;
            }
         }

         return vec3d != null ? vec3d : (squaredDistance != null ? squaredDistance : getLowBodyPoint(box));
      }
   }

   private static int getTicksUntilAttackReady(ClientPlayerEntity player, ItemStack stack, boolean attackProgressFlag) {
      double attackProgressLimit = 4.0 + sumAddModifiers(stack, EntityAttributes.ATTACK_SPEED);
      double attackProgressFactor = attackProgressLimit > 0.0 ? 20.0 / attackProgressLimit : 20.0;
      if (!attackProgressFlag) {
         return 1 + (int)Math.ceil(0.95F * attackProgressFactor);
      } else {
         float attackCooldownProgress = player.getAttackCooldownProgress(0.5F);
         return attackCooldownProgress >= 0.95F ? 0 : (int)Math.ceil((0.95F - attackCooldownProgress) * attackProgressFactor);
      }
   }

   public boolean preAimAtTarget(VecBlockingData vecBlockingData, int reachAliveValue) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      LivingEntity livingEntity = vecBlockingData == null ? null : vecBlockingData.entity();
      if (player == null || instance.world == null || livingEntity == null || !livingEntity.isAlive() || livingEntity.isRemoved()) {
         return false;
      } else if (reachAliveValue <= 2 && !this.reach.hasPendingRequest()) {
         Vec3d eyePos = player.getEyePos();
         Box boundingBox = livingEntity.getBoundingBox();
         double minMax = BlockEntityUtils.getMinMax(eyePos, boundingBox);
         if (minMax <= 3.4 && this.findBestWeaponSlot(livingEntity) >= 0) {
            Vec3d squaredDistance = chooseAimPoint(eyePos, boundingBox, livingEntity, null);
            float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, applyRelativeMotion(squaredDistance, getMovementDelta(livingEntity), getMovementDelta(player)));
            this.reach.requestSmoothedRotation(BotActionType.MELEE, wrapDegrees[0], wrapDegrees[1], minMax < 2.5 ? TrackFlick.Speed.FLICK : TrackFlick.Speed.ACTION, 30);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public float getRawAttackDamage() {
      return this.rawAttackDamage;
   }

   public boolean tryAttackWithYawLimit(VecBlockingData vecBlockingData, CenterVecData.Strike strike, SprintResetTracker timePlayer, int reachFistValue) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.world != null && instance.interactionManager != null && vecBlockingData != null && strike != null) {
         LivingEntity entity = vecBlockingData.entity();
         if (entity == null || !entity.isAlive() || entity.isRemoved()) {
            return false;
         } else if (isTargetBlocking(vecBlockingData)) {
            this.tryAttackEntity(entity, false);
            return false;
         } else {
            int ledgerHandswapLimit = this.pickUsableSlot(player, strike.weaponSlot(), this.findBestWeaponSlot(entity));
            Vec3d eyePos = player.getEyePos();
            Box boundingBox = entity.getBoundingBox();
            if (ledgerHandswapLimit < 0) {
               this.aimMeleeToward(eyePos, getTorsoPoint(boundingBox), TrackFlick.Speed.ACTION);
               return this.failWithReason("fist");
            } else {
               Vec3d center = boundingBox.getCenter();
               double deltaX = center.x - eyePos.x;
               double deltaZ = center.z - eyePos.z;
               double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
               float reachFistValue3 = VecShieldUtilsAmounts.getAmount(deltaX, deltaZ);
               double reachFistBound = getAimYawTolerance(distance, Math.min(boundingBox.getLengthX(), boundingBox.getLengthZ()));
               double max4 = Math.max(-reachFistBound, Math.min(reachFistBound, normalizeAngle(strike.launchYaw() - reachFistValue3)));
               float flickValue = (float)(reachFistValue3 + max4);
               Vec3d centerMin = getTorsoPoint(boundingBox);
               float routedPartialValue = MathClampUtils.getWrapDegrees(eyePos, centerMin)[1];
               TrackFlick.Speed speed = distance < 2.5 ? TrackFlick.Speed.FLICK : TrackFlick.Speed.ACTION;
               if (!this.inventorySelected.requestSlot(ledgerHandswapLimit, false)) {
                  this.reach.requestSmoothedRotation(BotActionType.MELEE, flickValue, routedPartialValue, speed, 70);
                  return false;
               } else {
                  this.reach.requestSmoothedRotation(BotActionType.MELEE, flickValue, routedPartialValue, speed, 70);
                  float attackCooldownProgress = player.getAttackCooldownProgress(0.5F);
                  boolean reachFistFlag = timePlayer != null && timePlayer.isSprinting();
                  double abs7 = Math.abs(normalizeAngle(this.reach.getCurrentYaw() - strike.launchYaw()));
                  boolean playerSelected = this.ledgerHandswap.isSlotSelected(ledgerHandswapLimit);
                  if (!canLaunchStrike(playerSelected, attackCooldownProgress, strike.sprintHit(), reachFistFlag, vecBlockingData.onGround(), strike.requireGrounded(), reachFistValue, abs7, strike.yawTolerance(), true)) {
                     return false;
                  } else if (!this.ledgerHandswap.isSwitchAllowed()) {
                     return false;
                  } else {
                     boolean rotationHittable = isRotationHittable(eyePos, this.reach.getCurrentYaw(), this.reach.getCurrentPitch(), entity);
                     if (!canLaunchStrike(playerSelected, attackCooldownProgress, strike.sprintHit(), reachFistFlag, vecBlockingData.onGround(), strike.requireGrounded(), reachFistValue, abs7, strike.yawTolerance(), rotationHittable)) {
                        return false;
                     } else if (!ClientThreadUtils.canFlag(this.gates, entity)) {
                        return false;
                     } else {
                        ItemStack stack = player.getInventory().getStack(ledgerHandswapLimit);
                        this.recordAttackDamage(getAttackAttribute(player, stack) + getSharpnessBonus(stack), vecBlockingData.armor(), vecBlockingData.meleeEpf(), reachFistFlag && attackCooldownProgress > 0.9F);
                        return this.performAttack(instance, player, entity, eyePos, this.reach.getCurrentYaw(), this.reach.getCurrentPitch());
                     }
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   public void clearStatusReason() {
      this.statusReason = "-";
   }

   private static Vec3d getTorsoPoint(Box box) {
      Vec3d center = box.getCenter();
      return new Vec3d(center.x, Math.min(box.maxY - 0.1, box.minY + 1.2), center.z);
   }
}
