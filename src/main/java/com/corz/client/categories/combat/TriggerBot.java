package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.RangeSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.entity.ThreadClientHandlerConst;
import com.corz.client.entity.ThreadClientHandlerConst.Result;
import com.corz.client.entity.TimeInstanceUtils;
import com.corz.client.gui.Button;
import com.corz.client.inventory.SelectedInventoryUtils;
import com.corz.client.render.CodeCategoryUtils;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttackRangeComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.BlocksAttacksComponent.DamageReduction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.InventoryPlayerUtils;
import com.corz.client.utils.PlayerCrystalUtils;
import ai.auth.Module;
import com.corz.client.utils.RetaliationFilter;
import com.corz.client.utils.TargetDelayTimer;
import com.corz.client.utils.WeaponStackChecks;
import com.corz.client.utils.TriggerBotMath;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.SwordBot;

public class TriggerBot extends Module implements ThreadClientHandlerConst {
   private boolean activationClickActive;
   private float attackCooldownOffset;
   private long activationClickTime;
   private boolean passBlocked;
   private boolean attackKeyDown;
   private final RetaliationFilter retaliationFilter;
   private final BooleanSetting airCritsOnly;
   private final BooleanSetting weaponsOnly;
   private final BooleanSetting checkShield;
   private long approvedAttackTime;
   private final BooleanSetting requireMouseDown;
   private boolean maceSmashUsed;
   private final TargetDelayTimer targetDelayTimer;
   private static final Options MOBS_ONLY_FILTER = Options.INSTANCE.getInvisiblesAnimals(false);
   private int approvedTargetId;
   private final BooleanSetting ignoreActivationClick;
   private final EnumSetting targetsSetting = new EnumSetting(
      "Targets", "Who it attacks. Friends and teammates are never attacked.", "Players", "Players", "Players + Mobs", "Mobs"
   );
   private final BooleanSetting selectFirstHit;
   private int hurtTime;
   private final RangeSetting mouseOverDelay;
   private float cooldownProgress;
   private final RangeSetting extraDelay;

   private boolean shouldSkipShielded(LivingEntity entity) {
      return this.checkShield.isEnabled() && this.isBlockingTowardPlayer(entity);
   }

   @Override
   protected void onSwordBot(SwordBot swordBot) {
      if (swordBot.getValue() == 1 && mc.options != null) {
         if (CodeCategoryUtils.canCategoryCode(mc.options.attackKey, swordBot.getKey())) {
            this.onAttackKeyPressed();
         }
      }
   }

   private Options getTargetFilter() {
      String formatText = this.targetsSetting.getCurrentOption();

      return switch (formatText) {
         case "Players + Mobs" -> Options.INSTANCE;
         case "Mobs" -> MOBS_ONLY_FILTER;
         default -> Options.INSTANCE2;
      };
   }

   @Override
   public int getHandlerPriority() {
      return 499;
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   private void recordAttackerOnHurt(ClientPlayerEntity clientPlayerEntity, long hurtAttackerValue) {
      int hurtTime = clientPlayerEntity.hurtTime;
      if (hurtTime > this.hurtTime) {
         DamageSource recentDamageSource = clientPlayerEntity.getRecentDamageSource();
         Entity entity = recentDamageSource == null ? null : recentDamageSource.getAttacker();
         if (entity != null && entity != clientPlayerEntity) {
            this.retaliationFilter.recordAttacker(entity.getId(), hurtAttackerValue);
         }
      }

      this.hurtTime = hurtTime;
   }

   private boolean canBreakShield(LivingEntity entity) {
      return WeaponStackChecks.canDisableShield(mc.player.getMainHandStack()) || InventoryPlayerUtils.canSlot(entity);
   }

   private boolean isReadyToAttack(LivingEntity entity) {
      if (!this.isTargetInAttackRange()) {
         return false;
      } else {
         boolean additionalDeal = this.canMaceSmash(entity);
         return this.airCritsOnly.isEnabled() && !additionalDeal && !this.isGroundedOrCritReady()
            ? false
            : additionalDeal || mc.player.getAttackCooldownProgress(0.5F - this.attackCooldownOffset) >= 1.0F;
      }
   }

   @Override
   protected void onButtonClick(Button button) {
      if (button.getValue() == 1 && mc.options != null) {
         if (CodeCategoryUtils.canCategoryCodeForKeyBindingInt(mc.options.attackKey, button.getButton())) {
            this.onAttackKeyPressed();
         }
      }
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   private float rollCooldownOffset() {
      return TriggerBotMath.interpolateRange(this.extraDelay.getMin(), this.extraDelay.getMax(), ThreadLocalRandom.current().nextDouble());
   }

   @Override
   protected void onReset() {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.world != null && mc.interactionManager != null && mc.options != null) {
         long nanoTimeScaled = System.nanoTime() / 1000000L;
         long fightsTridentTime = ClientThreadUtils.getTime();
         this.recordAttackerOnHurt(player, nanoTimeScaled);
         this.trackCooldownProgress(player);
         if (!MaceItem.shouldDealAdditionalDamage(player)) {
            this.maceSmashUsed = false;
         }

         if (this.activationClickActive && fightsTridentTime > this.activationClickTime + 1L) {
            this.activationClickActive = false;
         }

         boolean keyPressed = CodeCategoryUtils.isKeyPressed(mc.options.attackKey);
         if (this.attackKeyDown && !keyPressed) {
            this.attackKeyDown = false;
            if (this.requireMouseDown.isEnabled()) {
               this.retaliationFilter.resetTarget();
            }
         } else if (!this.attackKeyDown && keyPressed && mc.currentScreen == null) {
            this.attackKeyDown = true;
         }

         if (!this.selectFirstHit.isEnabled()) {
            this.retaliationFilter.resetTarget();
         }

         if (!this.isAttackContextValid(true)) {
            this.targetDelayTimer.reset();
         } else {
            LivingEntity crosshairEntity = this.getCrosshairEntity();
            if (crosshairEntity != null && this.isValidTarget(crosshairEntity)) {
               if (!this.selectFirstHit.isEnabled() || this.retaliationFilter.isTargetAllowed(crosshairEntity.getId(), nanoTimeScaled)) {
                  if (this.targetDelayTimer.hasDelayElapsed(crosshairEntity.getId(), nanoTimeScaled, this::rollMouseOverDelayMs)) {
                     if (!this.isPlayerBusy(player) && this.isReadyToAttack(crosshairEntity)) {
                        if (ClientThreadUtils.canOptionsInstance(this)) {
                           this.approvedTargetId = crosshairEntity.getId();
                           this.approvedAttackTime = fightsTridentTime;
                        }
                     }
                  }
               }
            } else {
               this.targetDelayTimer.reset();
               this.retaliationFilter.trackTargetLoss(nanoTimeScaled);
            }
         }
      } else {
         this.resetState();
      }
   }

   private Result filterActivationClick(Entity entity) {
      if (this.activationClickActive && this.requireMouseDown.isEnabled() && this.ignoreActivationClick.isEnabled() && this.isAttackContextValid(false)) {
         boolean crosshairPassFlag = entity == null && (mc.crosshairTarget == null || mc.crosshairTarget.getType() == Type.MISS);
         LivingEntity livingEntity = entity instanceof LivingEntity livingEntity2 && this.matchesTargetFilter(livingEntity2) ? livingEntity2 : null;
         boolean suppressed = livingEntity != null && !this.shouldSkipShielded(livingEntity) && this.isReadyToAttack(livingEntity);
         return TriggerBotMath.shouldBlockActivationClick(crosshairPassFlag, livingEntity != null, suppressed) ? Result.BLOCK : Result.PASS;
      } else {
         return Result.PASS;
      }
   }

   public TriggerBot() {
      super("Trigger Bot", IconsPngType.COMBAT);
      this.requireMouseDown = new BooleanSetting(
         "Require Mouse Down", "Only attacks while you hold your attack button. Off = attacks by itself whenever your crosshair is on a target.", false
      );
      this.ignoreActivationClick = new BooleanSetting(
         "Ignore Activation Click",
         "With Require Mouse Down: ignores your first click if it would swing at air, or hit a target before your weapon is charged.",
         true
      );
      this.extraDelay = new RangeSetting(
         "Extra Delay",
         "Extra ticks to wait after your weapon is fully charged (20 ticks = 1 second). Higher = slower and more human. Below 0 = hits a little early and weaker (spears always wait).",
         0.0,
         0.0,
         -5.0,
         10.0,
         1.0
      );
      this.mouseOverDelay = new RangeSetting(
         "Mouse Over Delay",
         "Milliseconds your crosshair must rest on a new target before the first hit. Higher = more human, slower first hit.",
         0.0,
         0.0,
         0.0,
         200.0,
         10.0
      );
      this.airCritsOnly = new BooleanSetting("Air Crits", "In the air, only hits when the hit will be a critical (falling and not sprinting).", false);
      this.checkShield = new BooleanSetting(
         "Check Shield", "Doesn't hit players who are blocking you with a shield, unless you are about to swing an axe that breaks it.", false
      );
      this.selectFirstHit = new BooleanSetting(
         "Select First Hit", "Doesn't start fights: waits until the target hits you first, then fights back. Forgets after 1 second of looking away.", false
      );
      this.weaponsOnly = new BooleanSetting("Weapons Only", "Only works while you hold a sword, axe, mace, trident or spear.", false);
      this.targetDelayTimer = new TargetDelayTimer();
      this.retaliationFilter = new RetaliationFilter();
      this.approvedTargetId = -1;
      this.approvedAttackTime = -1L;
      this.addSettings(
         new ConfigSetting[]{
            this.targetsSetting,
            this.requireMouseDown,
            this.ignoreActivationClick,
            this.extraDelay,
            this.mouseOverDelay,
            this.airCritsOnly,
            this.checkShield,
            this.selectFirstHit,
            this.weaponsOnly
         }
      );
   }

   private long rollMouseOverDelayMs() {
      return Math.round(TriggerBotMath.interpolateRange(this.mouseOverDelay.getMin(), this.mouseOverDelay.getMax(), ThreadLocalRandom.current().nextDouble()));
   }

   private void resetState() {
      this.attackKeyDown = false;
      this.activationClickActive = false;
      this.activationClickTime = 0L;
      this.targetDelayTimer.reset();
      this.retaliationFilter.resetAll();
      this.hurtTime = 0;
      this.cooldownProgress = 0.0F;
      this.attackCooldownOffset = this.rollCooldownOffset();
      this.maceSmashUsed = false;
      this.passBlocked = false;
      this.approvedTargetId = -1;
      this.approvedAttackTime = -1L;
   }

   private boolean isAttackContextValid(boolean focusedOverlayFlag) {
      if (mc.currentScreen != null || mc.getOverlay() != null || !mc.isWindowFocused()) {
         return false;
      } else {
         return focusedOverlayFlag && this.requireMouseDown.isEnabled() && !this.attackKeyDown
            ? false
            : !this.weaponsOnly.isEnabled() || WeaponStackChecks.isMeleeWeapon(mc.player.getMainHandStack());
      }
   }

   @Override
   public Result resolveAttackIntercept(Object obj, Entity entity) {
      if (mc.player == null) {
         return Result.PASS;
      } else if (obj == null) {
         return this.filterActivationClick(entity);
      } else if (obj != this) {
         return Result.PASS;
      } else {
         boolean passChargingFlag = entity != null && entity.getId() == this.approvedTargetId && this.approvedAttackTime == ClientThreadUtils.getTime();
         this.approvedTargetId = -1;
         if (!(passChargingFlag && entity instanceof LivingEntity livingEntity) || this.passBlocked || mc.player.isUsingItem()) {
            return Result.BLOCK;
         } else if (this.isAttackContextValid(true) && this.isValidTarget(livingEntity) && this.isReadyToAttack(livingEntity)) {
            return TimeInstanceUtils.canFlag(livingEntity) ? Result.PASS : Result.BLOCK;
         } else {
            return Result.BLOCK;
         }
      }
   }

   private boolean isPlayerBusy(ClientPlayerEntity clientPlayerEntity) {
      return clientPlayerEntity.isUsingItem()
         || this.passBlocked
         || ClientThreadUtils.isEntityActive()
         || ClientThreadUtils.isOptionsPressed()
         || ClientThreadUtils.isARRAY_DEQUE()
         || ClientThreadUtils.isFlag4()
         || AutoInventoryTotem.isHandSwapSlotActive()
         || SelectedInventoryUtils.isSelectedSlot()
         || TimeInstanceUtils.isInteractionBreaking();
   }

   private boolean hasMaceForSmash(LivingEntity entity) {
      return WeaponStackChecks.isMace(mc.player.getMainHandStack()) || InventoryPlayerUtils.canSlotForEntity(entity);
   }

   private boolean isGroundedOrCritReady() {
      ClientPlayerEntity player = mc.player;
      boolean blindnessGlidingFlag = !player.isOnGround() && !player.isClimbing() && !player.isTouchingWater() && !player.isInLava() && !player.hasVehicle() && !player.isGliding();
      return !blindnessGlidingFlag ? true : player.fallDistance > 0.0 && !player.isSprinting() && !player.hasBlindnessEffect() && player.getAttackCooldownProgress(0.5F) > 0.9F;
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.isOnThread() && !inventoryPlayer.isOpenedByClient()) {
         var packet = inventoryPlayer.packet;
         if (packet instanceof ClientTickEndC2SPacket) {
            this.passBlocked = false;
         } else if (packet instanceof PlayerInteractBlockC2SPacket || packet instanceof PlayerInteractItemC2SPacket) {
            this.passBlocked = true;
         } else if (packet instanceof PlayerActionC2SPacket playerActionC2SPacket) {
            if (playerActionC2SPacket.getAction() == Action.RELEASE_USE_ITEM) {
               this.passBlocked = true;
            }
         } else if (packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke) {
            if (!PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
               this.passBlocked = true;
            } else if (MaceItem.shouldDealAdditionalDamage(player) && WeaponStackChecks.isMace(player.getMainHandStack())) {
               this.maceSmashUsed = true;
            }
         }
      }
   }

   private boolean isTargetInAttackRange() {
      ClientPlayerEntity player = mc.player;
      if (mc.attackCooldown <= 0 && !player.isRiding()) {
         ItemStack mainHandStack = player.getMainHandStack();
         if (player.isBelowMinimumAttackCharge(mainHandStack, 0)) {
            return false;
         } else {
            AttackRangeComponent attackRangeComponent = mainHandStack.get(DataComponentTypes.ATTACK_RANGE);
            HitResult crosshairTarget = mc.crosshairTarget;
            return attackRangeComponent == null || crosshairTarget == null || attackRangeComponent.isWithinRange(player, crosshairTarget.getPos());
         }
      } else {
         return false;
      }
   }

   private LivingEntity getCrosshairEntity() {
      return mc.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity ? livingEntity : null;
   }

   private boolean isValidTarget(LivingEntity entity) {
      return this.matchesTargetFilter(entity) && !this.shouldSkipShielded(entity);
   }

   private boolean canMaceSmash(LivingEntity entity) {
      return !this.maceSmashUsed && MaceItem.shouldDealAdditionalDamage(mc.player) ? this.hasMaceForSmash(entity) : false;
   }

   private void trackCooldownProgress(ClientPlayerEntity clientPlayerEntity) {
      float cooldownProgress = clientPlayerEntity.getAttackCooldownProgress(0.0F);
      if (cooldownProgress < this.cooldownProgress) {
         this.attackCooldownOffset = this.rollCooldownOffset();
      }

      this.cooldownProgress = cooldownProgress;
   }

   private boolean matchesTargetFilter(LivingEntity entity) {
      return EntityPlayerUtilsLiving.canSpectatorDead(entity, this.getTargetFilter()) && entity != mc.player.getVehicle();
   }

   private void onAttackKeyPressed() {
      if (mc.player != null && mc.currentScreen == null && mc.getOverlay() == null && !this.attackKeyDown) {
         this.attackKeyDown = true;
         if (this.requireMouseDown.isEnabled() && this.ignoreActivationClick.isEnabled()) {
            this.activationClickActive = true;
            this.activationClickTime = ClientThreadUtils.getTime();
         }
      }
   }

   private boolean isBlockingTowardPlayer(LivingEntity entity) {
      if (entity instanceof PlayerEntity && entity.isUsingItem()) {
         BlocksAttacksComponent activeitem = entity.getActiveItem().get(DataComponentTypes.BLOCKS_ATTACKS);
         if (activeitem == null) {
            return false;
         } else if (!TriggerBotMath.hasBlockDelayElapsed(entity.getItemUseTime(), TriggerBotMath.millisToTicks(EntityDamageUtils.getCachedHurtTimeLimit()), activeitem.getBlockDelayTicks())) {
            return false;
         } else {
            double reductionsEntityBound = -1.0;

            for (DamageReduction damageReduction : activeitem.damageReductions()) {
               reductionsEntityBound = Math.max(reductionsEntityBound, (double)damageReduction.horizontalBlockingAngle());
            }

            if (reductionsEntityBound < 0.0) {
               return false;
            } else {
               Vec3d rotationVector = entity.getRotationVector(0.0F, entity.getHeadYaw());
               Vec3d entityPos = mc.player.getEntityPos();
               Vec3d entityPos2 = entity.getEntityPos();
               return !TriggerBotMath.isAngleWithin(rotationVector.x, rotationVector.z, entityPos.x - entityPos2.x, entityPos.z - entityPos2.z, reductionsEntityBound + 11.5) ? false : !this.canBreakShield(entity);
            }
         }
      } else {
         return false;
      }
   }
}
