package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.entity.ThreadClientHandlerConst;
import com.corz.client.entity.ThreadClientHandlerConst.Result;
import com.corz.client.world.ThreadClientHandlerHandle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntSupplier;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.WeaknessGodApi;

public class HitSelect extends Module implements ThreadClientHandlerHandle, WeaknessGodApi, ThreadClientHandlerConst {
   static final String PAUSE = "Pause";
   private final HitSelect.KnockbackWindow knockbackWindow;
   private final IntegerSetting chanceSetting;
   private long lastNormalBlockTick;
   private final EnumSetting preferenceSetting;
   private long lastKnockbackTime;
   private int criticalEntityId;
   private static final Options ENTITY_CHECK_OPTIONS = new Options(true, true, true, true, true, true);
   private final HitSelect.ChanceWindow chanceWindow;
   private long criticalTime;
   static final String CRITICAL_HITS = "Critical Hits";
   private final EnumSetting modeSetting = new EnumSetting(
      "Mode",
      "Pause: skips clicks while the target can't take damage yet. Normal: only lets hits through at the best moment to keep your sprint",
      "Pause",
      "Pause",
      "Normal"
   );
   static final String REDUCTION = "KB Reduction";
   static final String NORMAL = "Normal";

   @Override
   protected void onReset() {
      ClientPlayerEntity player = mc.player;
      if (player == null) {
         this.resetState();
      } else {
         this.knockbackWindow.tick(player.isOnGround());
      }
   }

   static boolean isCriticalWindowOpen(long hitSelectLimit, long hitSelectValue, long criticalValue, long hitSelectValue3) {
      if (hitSelectLimit >= 0L && hitSelectValue < hitSelectLimit) {
         long max2 = Math.max(100L, Math.min(1000L, hitSelectValue3 + 75L));
         return criticalValue - hitSelectLimit < max2;
      } else {
         return false;
      }
   }

   private static boolean isTargetBlockingTowardPlayer(ClientPlayerEntity clientPlayerEntity, LivingEntity entity) {
      if (!entity.isUsingItem()) {
         return false;
      } else {
         BlocksAttacksComponent activeitem = entity.getActiveItem().get(DataComponentTypes.BLOCKS_ATTACKS);
         if (activeitem == null) {
            return false;
         } else if (!hasShieldDelayElapsed(entity.getItemUseTime(), EntityDamageUtils.getValueInt(), activeitem.getBlockDelayTicks())) {
            return false;
         } else {
            Vec3d rotationVector = entity.getRotationVector(0.0F, entity.getHeadYaw());
            return isDotProductNonNegative(rotationVector.x, rotationVector.z, clientPlayerEntity.getX() - entity.getX(), clientPlayerEntity.getZ() - entity.getZ());
         }
      }
   }

   static boolean isDotProductPositive(double hitSelectFactor, double hitSelectFactor2, double hitSelectFactor3, double hitSelectFactor4) {
      return hitSelectFactor * hitSelectFactor3 + hitSelectFactor2 * hitSelectFactor4 > 0.0;
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   static int combineInvulnTicks(int hitSelectBound, int hitSelectValue) {
      return Math.max(hitSelectBound, getInvulnTicksSinceHurt(hitSelectValue));
   }

   private static boolean isMaceSmashAvailable(ClientPlayerEntity clientPlayerEntity) {
      if (!MaceItem.shouldDealAdditionalDamage(clientPlayerEntity)) {
         return false;
      } else {
         for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            if (clientPlayerEntity.getInventory().getStack(i).getItem() instanceof MaceItem) {
               return true;
            }
         }

         return false;
      }
   }

   private static int getRemainingInvulnTicks(LivingEntity entity) {
      return combineInvulnTicks(EntityDamageUtils.getMax(entity), EntityDamageUtils.getMin(entity.getId()));
   }

   @Override
   public Result resolveAttackIntercept(Object obj, Entity entity) {
      if (!(entity instanceof LivingEntity livingEntity)) {
         return Result.PASS;
      } else {
         ClientPlayerEntity player = mc.player;
         if (player == null || !EntityPlayerUtilsLiving.canSpectatorDead(entity, ENTITY_CHECK_OPTIONS) || isMaceSmashAvailable(player)) {
            return Result.PASS;
         } else if (isTargetBlockingTowardPlayer(player, livingEntity)) {
            return Result.PASS;
         } else {
            long passChargingValue = ConnectionTimeUtils.getTimeTimeMillis();
            boolean equals = "Normal".equals(this.modeSetting.getCurrentOption());
            boolean passChargingFlag = equals ? this.shouldBlockClickNormalMode(player, livingEntity, passChargingValue) : this.shouldBlockClickPauseMode(player, livingEntity, passChargingValue);
            if (!passChargingFlag) {
               return Result.PASS;
            } else {
               if (equals) {
                  this.lastNormalBlockTick = ClientThreadUtils.getTime();
                  if (obj == null || obj == ClientThreadUtils.getObject2Deque()) {
                     swingAndShowCritParticles(player, livingEntity);
                  }
               }

               return Result.BLOCK;
            }
         }
      }
   }

   public HitSelect() {
      super("Hit Select", IconsPngType.COMBAT);
      this.chanceSetting = new IntegerSetting("Chance", "How often hit select steps in, in percent. Lower lets more of your clicks through", 90, 0, 100);
      this.preferenceSetting = new EnumSetting(
         "Preference",
         "Normal mode only. KB Reduction: hit back while knocked back. Critical Hits: wait until you fall so the hit crits",
         "KB Reduction",
         "KB Reduction",
         "Critical Hits"
      );
      this.chanceWindow = new HitSelect.ChanceWindow();
      this.knockbackWindow = new HitSelect.KnockbackWindow();
      this.lastNormalBlockTick = Long.MIN_VALUE;
      this.lastKnockbackTime = -1L;
      this.criticalEntityId = Integer.MIN_VALUE;
      this.criticalTime = -1L;
      this.addSettings(new ConfigSetting[]{this.modeSetting, this.chanceSetting, this.preferenceSetting});
   }

   static boolean hasShieldDelayElapsed(int hitSelectValue, int hitSelectBound, int hitSelectValue2) {
      return hitSelectValue + Math.max(0, hitSelectBound) >= hitSelectValue2;
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   private boolean shouldBlockClickPauseMode(ClientPlayerEntity clientPlayerEntity, LivingEntity entity, long groundValue) {
      if (!clientPlayerEntity.isOnGround()) {
         return false;
      } else {
         int groundLimit = getPingToleranceTicks(EntityDamageUtils.getValueInt());
         return getRemainingInvulnTicks(entity) <= groundLimit && !this.isCriticalWindowOpenFor(entity, groundValue) ? false : this.rollChanceForTarget(entity, -1L);
      }
   }

   private static boolean isCriticalHitPossible(ClientPlayerEntity clientPlayerEntity) {
      return clientPlayerEntity.getAttackCooldownProgress(0.5F) > 0.9F
         && clientPlayerEntity.fallDistance > 0.0
         && !clientPlayerEntity.isOnGround()
         && !clientPlayerEntity.isClimbing()
         && !clientPlayerEntity.isTouchingWater()
         && !clientPlayerEntity.hasBlindnessEffect()
         && !clientPlayerEntity.hasVehicle()
         && !clientPlayerEntity.isSprinting();
   }

   static boolean isEligibleCriticalTarget(boolean hitSelectFlag, double hitSelectValue, double hitSelectLimit, int hitSelectValue2, int hitSelectLimit2) {
      return !hitSelectFlag && hitSelectValue <= hitSelectLimit && hitSelectValue2 <= hitSelectLimit2;
   }

   static int getPingToleranceTicks(int hitSelectValue) {
      return Math.max(2, hitSelectValue + 1);
   }

   @Override
   public void handlePlayerReduction(Vec3d vec, boolean playerReductionFlag) {
      this.knockbackWindow.open();
      this.lastKnockbackTime = ConnectionTimeUtils.getTimeTimeMillis();
   }

   private boolean shouldBlockClickNormalMode(ClientPlayerEntity clientPlayerEntity, LivingEntity entity, long reductionVelocityValue) {
      boolean reductionVelocityFlag = false;
      if (this.knockbackWindow.isOpen()) {
         if ("KB Reduction".equals(this.preferenceSetting.getCurrentOption())) {
            return false;
         }

         if (clientPlayerEntity.getVelocity().y > 0.0) {
            reductionVelocityFlag = true;
         } else if (!clientPlayerEntity.isOnGround()) {
            return false;
         }
      }

      Vec3d velocity = clientPlayerEntity.getVelocity();
      if (!isDotProductPositive(velocity.x, velocity.z, entity.getX() - clientPlayerEntity.getX(), entity.getZ() - clientPlayerEntity.getZ())) {
         return false;
      } else {
         if (!reductionVelocityFlag) {
            int reductionVelocityLimit = getPingToleranceTicks(EntityDamageUtils.getValueInt());
            if (getRemainingHurtTicks(entity) <= reductionVelocityLimit && !this.isCriticalWindowOpenFor(entity, reductionVelocityValue)) {
               return false;
            }
         }

         return this.rollChanceForTarget(entity, reductionVelocityFlag ? this.lastKnockbackTime : -1L);
      }
   }

   private void resetState() {
      this.chanceWindow.reset();
      this.knockbackWindow.close();
      this.lastNormalBlockTick = Long.MIN_VALUE;
      this.lastKnockbackTime = -1L;
      this.criticalEntityId = Integer.MIN_VALUE;
      this.criticalTime = -1L;
   }

   private static void swingAndShowCritParticles(ClientPlayerEntity clientPlayerEntity, LivingEntity entity) {
      clientPlayerEntity.swingHand(Hand.MAIN_HAND, false);
      if (isCriticalHitPossible(clientPlayerEntity)) {
         clientPlayerEntity.addCritParticles(entity);
      }
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke && !inventoryPlayer.isOpenedByClient()) {
         if (mc != null && mc.isOnThread() && PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
            ClientPlayerEntity player = mc.player;
            if (player != null
               && (mc.world == null ? null : mc.world.getEntityById(PlayerCrystalUtils.getEntityId(playerInteractEntityC2SPacke))) instanceof LivingEntity livingEntity
               && isEligibleCriticalTarget(
                  isTargetBlockingTowardPlayer(player, livingEntity),
                  EntityPlayerUtilsLiving.getMinMax(livingEntity),
                  player.getAttributeValue(EntityAttributes.ENTITY_INTERACTION_RANGE),
                  getRemainingInvulnTicks(livingEntity),
                  getPingToleranceTicks(EntityDamageUtils.getValueInt())
               )) {
               this.criticalEntityId = livingEntity.getId();
               this.criticalTime = ConnectionTimeUtils.getTimeTimeMillis();
            } else {
               this.criticalEntityId = Integer.MIN_VALUE;
               this.criticalTime = -1L;
            }
         }
      }
   }

   private static int getRemainingHurtTicks(LivingEntity entity) {
      return combineInvulnTicks(EntityDamageUtils.getHurtTime(entity), EntityDamageUtils.getMin(entity.getId()));
   }

   @Override
   public boolean isAgePlayer() {
      return this.lastNormalBlockTick == ClientThreadUtils.getTime();
   }

   private boolean isCriticalWindowOpenFor(LivingEntity entity, long criticalValue) {
      return entity.getId() != this.criticalEntityId ? false : isCriticalWindowOpen(this.criticalTime, EntityDamageUtils.getTime(entity.getId()), criticalValue, EntityDamageUtils.getTime());
   }

   @Override
   public boolean isReductionCritical() {
      return this.lastNormalBlockTick == ClientThreadUtils.getTime();
   }

   private boolean rollChanceForTarget(LivingEntity entity, long chanceWindowBound) {
      long maxExtraStamp = Math.max(EntityDamageUtils.getTime(entity.getId()), chanceWindowBound);
      return this.chanceWindow.rollChanceOncePerWindow(entity.getId(), maxExtraStamp, this.chanceSetting.getValue(), () -> ThreadLocalRandom.current().nextInt(100));
   }

   static int getInvulnTicksSinceHurt(int hitSelectValue) {
      return hitSelectValue == Integer.MAX_VALUE ? 0 : Math.max(0, 10 - hitSelectValue);
   }

   static boolean isDotProductNonNegative(double hitSelectFactor, double hitSelectFactor2, double hitSelectFactor3, double hitSelectFactor4) {
      return hitSelectFactor * hitSelectFactor3 + hitSelectFactor2 * hitSelectFactor4 >= 0.0;
   }

   static final class ChanceWindow {
      private int windowEntityId = Integer.MIN_VALUE;
      private long windowMaxTick = Long.MIN_VALUE;
      private boolean chancePassed;

      void reset() {
         this.windowEntityId = Integer.MIN_VALUE;
         this.windowMaxTick = Long.MIN_VALUE;
         this.chancePassed = false;
      }

      boolean rollChanceOncePerWindow(int chanceWindowValue, long max, int chanceWindowLimit, IntSupplier intSupplier) {
         if (chanceWindowValue != this.windowEntityId || max != this.windowMaxTick) {
            this.windowEntityId = chanceWindowValue;
            this.windowMaxTick = max;
            this.chancePassed = intSupplier.getAsInt() < chanceWindowLimit;
         }

         return this.chancePassed;
      }
   }

   static final class KnockbackWindow {
      private int windowTicksRemaining;
      private int groundedTickCount;
      private boolean leftGround;

      boolean isOpen() {
         return this.windowTicksRemaining > 0;
      }

      void close() {
         this.windowTicksRemaining = 0;
         this.groundedTickCount = 0;
         this.leftGround = false;
      }

      void open() {
         this.windowTicksRemaining = 20;
         this.groundedTickCount = 0;
         this.leftGround = false;
      }

      void tick(boolean knockbackWindowFlag) {
         if (this.windowTicksRemaining > 0) {
            this.windowTicksRemaining--;
            if (!knockbackWindowFlag) {
               this.leftGround = true;
            } else {
               if (this.leftGround || ++this.groundedTickCount >= 3) {
                  this.windowTicksRemaining = 0;
               }
            }
         }
      }
   }
}
