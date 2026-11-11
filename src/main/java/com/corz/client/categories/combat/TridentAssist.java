package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.ThreadClientHandlerConst;
import com.corz.client.entity.ThreadClientHandlerConst.Result;
import com.corz.client.inventory.SelectedInventoryUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import ai.auth.Module;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.IconsPngType;

public class TridentAssist extends Module implements ThreadClientHandlerConst {
   private long approvedAttackTime;
   private int approvedAttackTargetId;
   private final BooleanSetting autoRelease;
   private final BooleanSetting meleeTiming = new BooleanSetting(
      "Melee Timing",
      "Holds your trident hit until it is fully charged, then lands it that tick, so every hit does full damage. Swings at the air are skipped while it charges.",
      true
   );
   private int critWaitTicks;
   private final BooleanSetting critTiming = new BooleanSetting(
      "Crit Timing", "When you jump, waits the few ticks until you start falling so the hit lands as a critical.", true
   );
   private int targetEntityId;
   private long targetSetTimeMs;

   static boolean isTridentCharged(int tridentAssistLimit) {
      return tridentAssistLimit >= 10;
   }

   private void releaseChargedTrident(ClientPlayerEntity player) {
      if (player.isUsingItem() && player.getActiveItem().isOf(Items.TRIDENT)) {
         int itemUseTime = player.getItemUseTime();
         if (isTridentCharged(itemUseTime)) {
            mc.interactionManager.stopUsingItem(player);
            mc.options.useKey.setPressed(false);
         }
      }
   }

   public TridentAssist() {
      super("Trident Assist", IconsPngType.COMBAT);
      this.autoRelease = new BooleanSetting(
         "Auto Release", "Lets go of a charging trident on the exact tick it is fully charged, for a perfect throw or riptide.", true
      );
      this.targetEntityId = -1;
      this.approvedAttackTargetId = -1;
      this.approvedAttackTime = -1L;
      this.addSettings(new ConfigSetting[]{this.meleeTiming, this.critTiming, this.autoRelease});
   }

   private static boolean isFreeFalling(ClientPlayerEntity player) {
      return !player.isOnGround() && !player.isClimbing() && !player.isTouchingWater() && !player.isInLava() && !player.hasVehicle() && !player.isGliding() && !player.getAbilities().flying;
   }

   private void resetState() {
      this.clearTarget();
      this.targetSetTimeMs = 0L;
      this.approvedAttackTargetId = -1;
      this.approvedAttackTime = -1L;
   }

   static boolean isCooldownFull(float tridentAssistLimit) {
      return tridentAssistLimit >= 1.0F;
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   private static boolean hasMaceSmashAvailable(ClientPlayerEntity player) {
      if (!MaceItem.shouldDealAdditionalDamage(player)) {
         return false;
      } else {
         for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            if (player.getInventory().getStack(i).getItem() instanceof MaceItem) {
               return true;
            }
         }

         return false;
      }
   }

   private void clearTarget() {
      this.targetEntityId = -1;
      this.critWaitTicks = 0;
   }

   static boolean hasTargetExpired(long tridentAssistValue, long tridentAssistValue2) {
      return tridentAssistValue - tridentAssistValue2 > 24L;
   }

   private boolean isAttackBlocked() {
      return mc.attackCooldown > 0
         || ClientThreadUtils.isEntityActive()
         || ClientThreadUtils.isARRAY_DEQUE()
         || ClientThreadUtils.isFlag4()
         || AutoInventoryTotem.isHandSwapSlotActive()
         || SelectedInventoryUtils.isSelectedSlot();
   }

   @Override
   public Result resolveAttackIntercept(Object obj, Entity entity) {
      ClientPlayerEntity player = mc.player;
      if (player == null || !this.isTimingEnabled() || !isHoldingTrident(player)) {
         return Result.PASS;
      } else if (obj != this) {
         if (obj != null) {
            return Result.PASS;
         } else if (entity == null) {
            return this.meleeTiming.isEnabled() && this.isCrosshairMiss() && !isCooldownFull(player.getAttackCooldownProgress(0.0F)) ? Result.BLOCK : Result.PASS;
         } else if (!(entity instanceof LivingEntity livingEntity && livingEntity.isAlive() && !hasMaceSmashAvailable(player))) {
            return Result.PASS;
         } else if (this.isReadyToAttack(player)) {
            this.clearTarget();
            return Result.PASS;
         } else {
            this.setTarget(player, livingEntity);
            return Result.BLOCK;
         }
      } else {
         boolean passChargingFlag = entity != null && entity.getId() == this.approvedAttackTargetId && this.approvedAttackTime == ClientThreadUtils.getTime();
         this.approvedAttackTargetId = -1;
         return passChargingFlag ? Result.PASS : Result.BLOCK;
      }
   }

   private LivingEntity getCrosshairEntity() {
      return mc.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity ? livingEntity : null;
   }

   static boolean isWaitingForCritFall(boolean tridentAssistFlag, boolean tridentAssistFlag2, double tridentAssistValue, int tridentAssistValue2) {
      return tridentAssistFlag && tridentAssistFlag2 && tridentAssistValue <= 0.0 && tridentAssistValue2 < 8;
   }

   private void updateScheduledAttack(ClientPlayerEntity player) {
      long chargingRiptideTime = ClientThreadUtils.getTime();
      if (this.isTimingEnabled() && isHoldingTrident(player) && !player.isUsingItem() && mc.currentScreen == null && !hasTargetExpired(chargingRiptideTime, this.targetSetTimeMs)) {
         LivingEntity crosshairEntity = this.getCrosshairEntity();
         if (crosshairEntity != null && crosshairEntity.getId() == this.targetEntityId) {
            if (!crosshairEntity.isAlive()) {
               this.clearTarget();
            } else if (isCooldownSatisfied(this.meleeTiming.isEnabled(), player.getAttackCooldownProgress(0.0F))) {
               if (this.shouldWaitForCrit(player)) {
                  this.critWaitTicks++;
               } else if (!this.isAttackBlocked() && !ClientThreadUtils.isOptionsPressed()) {
                  if (ClientThreadUtils.canOptionsInstance(this)) {
                     this.approvedAttackTargetId = this.targetEntityId;
                     this.approvedAttackTime = chargingRiptideTime;
                  }

                  this.clearTarget();
               }
            }
         }
      } else {
         this.clearTarget();
      }
   }

   @Override
   protected void onReset() {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.interactionManager != null && mc.options != null) {
         if (this.autoRelease.isEnabled()) {
            this.releaseChargedTrident(player);
         }

         if (this.targetEntityId != -1) {
            this.updateScheduledAttack(player);
         }
      } else {
         this.resetState();
      }
   }

   private void setTarget(ClientPlayerEntity player, LivingEntity entity) {
      long time = ClientThreadUtils.getTime();
      if (this.targetEntityId != entity.getId()) {
         this.critWaitTicks = 0;
      }

      this.targetEntityId = entity.getId();
      this.targetSetTimeMs = time;
   }

   private boolean shouldWaitForCrit(ClientPlayerEntity player) {
      return this.critTiming.isEnabled() && isWaitingForCritFall(isFreeFalling(player), canCritNormally(player), player.fallDistance, this.critWaitTicks);
   }

   private boolean isReadyToAttack(ClientPlayerEntity player) {
      return isCooldownSatisfied(this.meleeTiming.isEnabled(), player.getAttackCooldownProgress(0.0F)) && !this.shouldWaitForCrit(player);
   }

   static boolean isCooldownSatisfied(boolean tridentAssistFlag, float tridentAssistValue) {
      return !tridentAssistFlag || isCooldownFull(tridentAssistValue);
   }

   private boolean isCrosshairMiss() {
      HitResult crosshairTarget = mc.crosshairTarget;
      return crosshairTarget == null || crosshairTarget.getType() == Type.MISS;
   }

   private static boolean isHoldingTrident(ClientPlayerEntity player) {
      return player.getMainHandStack().isOf(Items.TRIDENT);
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   private static boolean canCritNormally(ClientPlayerEntity player) {
      return !player.isSprinting() && !player.hasBlindnessEffect() && !player.hasStatusEffect(StatusEffects.SLOW_FALLING) && !player.hasStatusEffect(StatusEffects.LEVITATION);
   }

   private boolean isTimingEnabled() {
      return this.meleeTiming.isEnabled() || this.critTiming.isEnabled();
   }
}
