package com.corz.client.categories.combat;

import com.corz.client.utils.KnockbackMath.JumpStep;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.RangeSetting;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.mixins.AccessorKeyBinding;
import com.corz.client.world.ThreadClientHandlerHandle;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import com.corz.client.utils.KnockbackMath;
import com.corz.client.utils.EntityDamageUtils;
import ai.auth.Module;
import com.corz.client.categories.combat.SilentAim;
import com.corz.client.utils.ElytraAutoUtils;
import com.corz.client.utils.IconsPngType;

public class AutoJumpReset extends Module implements ThreadClientHandlerHandle {
   private int ticksSinceHit;
   private final BooleanSetting onlyWhenTargeting;
   private boolean jumpResetPending;
   private final IntegerSetting chanceSetting = new IntegerSetting("Chance", "How often to try a jump reset when you get hit", 40, 0, 100);
   private final RangeSetting accuracy = new RangeSetting(
      "Accuracy", "How often the jump lands on the exact right tick. Lower looks more human", 40.0, 60.0, 0.0, 100.0, 1.0
   );
   private boolean optionsJumpActive;
   private ClientPlayerEntity player;

   public AutoJumpReset() {
      super("Auto Jump Reset", IconsPngType.COMBAT);
      this.onlyWhenTargeting = new BooleanSetting("Only When Targeting", "Only jump reset while you and an enemy face each other", false);
      this.addSettings(new ConfigSetting[]{this.chanceSetting, this.accuracy, this.onlyWhenTargeting});
   }

   @Override
   protected void onEnable() {
      this.jumpResetPending = false;
      this.ticksSinceHit = 0;
      this.optionsJumpActive = false;
      this.player = null;
   }

   private boolean isSafeToJump(ClientPlayerEntity clientPlayerEntity) {
      if (clientPlayerEntity.isUsingItem() || clientPlayerEntity.hasVehicle()) {
         return false;
      } else if (clientPlayerEntity.isTouchingWater() || clientPlayerEntity.isInLava()) {
         return false;
      } else {
         return clientPlayerEntity.hasStatusEffect(StatusEffects.JUMP_BOOST)
            ? false
            : mc.world != null
               && mc.world.getStatesInBox(clientPlayerEntity.getBoundingBox().contract(0.001)).noneMatch(state -> state.isOf(Blocks.COBWEB));
      }
   }

   private void releaseJumpKey() {
      if (this.optionsJumpActive) {
         this.optionsJumpActive = false;
         if (mc.options != null) {
            ElytraAutoUtils.handleRawPressed(
               mc.options.jumpKey, mc.currentScreen == null && isKeyPhysicallyDown(mc.options.jumpKey)
            );
         }
      }
   }

   @Override
   protected void onReset() {
      if (this.optionsJumpActive) {
         this.releaseJumpKey();
      }

      if (this.jumpResetPending) {
         ClientPlayerEntity player = mc.player;
         if (player != null && player == this.player && mc.options != null) {
            boolean iIiFlag = mc.currentScreen == null && this.isSafeToJump(player) && !mc.options.jumpKey.isPressed();
            JumpStep jumpStep = iIiFlag ? KnockbackMath.getJumpResetStep(this.ticksSinceHit, player.isOnGround(), this.rollPerfectTiming()) : JumpStep.DROP;
            switch (jumpStep) {
               case PRESS:
                  ElytraAutoUtils.handleRawPressed(mc.options.jumpKey, true);
                  this.optionsJumpActive = true;
                  this.jumpResetPending = false;
                  break;
               case WAIT:
                  this.ticksSinceHit++;
                  break;
               case DROP:
                  this.jumpResetPending = false;
            }
         } else {
            this.jumpResetPending = false;
         }
      }
   }

   private boolean rollPerfectTiming() {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      double minmaxValue = KnockbackMath.lerpClamped(this.accuracy.getMin(), this.accuracy.getMax(), current.nextDouble());
      return KnockbackMath.rollPercentChance(minmaxValue, current.nextDouble());
   }

   @Override
   public void handlePlayerReduction(Vec3d vec, boolean playerReductionFlag) {
      ClientPlayerEntity player = mc.player;
      if (!playerReductionFlag && vec != null && player != null && mc.world != null) {
         if (KnockbackMath.isKnockbackVelocity(vec.x, vec.y, vec.z)) {
            if (player.isOnGround() && this.isSafeToJump(player)) {
               if (KnockbackMath.isHurtTimeActiveOrRecent(player.hurtTime, EntityDamageUtils.getMin(player.getId()), 22)) {
                  if (!this.onlyWhenTargeting.isEnabled() || this.isFacingEnemyMutually(player)) {
                     if (KnockbackMath.rollPercentChance((double)this.chanceSetting.getValue(), ThreadLocalRandom.current().nextDouble())) {
                        this.jumpResetPending = true;
                        this.ticksSinceHit = 0;
                        this.player = player;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isFacingEnemyMutually(ClientPlayerEntity player) {
      double yawSilent = SilentAim.getYawSilent();

      for (AbstractClientPlayerEntity abstractClientPlayerEntity : mc.world.getPlayers()) {
         if (abstractClientPlayerEntity != player
            && EntityPlayerUtilsLiving.canSpectatorDead(abstractClientPlayerEntity, Options.INSTANCE2)
            && !(player.distanceTo(abstractClientPlayerEntity) >= 6.0)
            && KnockbackMath.isFacingWithinDegrees(yawSilent, player.getX(), player.getZ(), abstractClientPlayerEntity.getX(), abstractClientPlayerEntity.getZ(), 60.0)
            && KnockbackMath.isFacingWithinDegrees(abstractClientPlayerEntity.getYaw(), abstractClientPlayerEntity.getX(), abstractClientPlayerEntity.getZ(), player.getX(), player.getZ(), 60.0)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void onDisable() {
      this.releaseJumpKey();
      this.jumpResetPending = false;
      this.ticksSinceHit = 0;
      this.player = null;
   }

   private static boolean isKeyPhysicallyDown(KeyBinding keyBinding) {
      if (mc.getWindow() != null && mc.isWindowFocused()) {
         Key boundKey = ((AccessorKeyBinding)keyBinding).getBoundKey();
         long handle = mc.getWindow().getHandle();
         if (boundKey.getCategory() != Type.KEYSYM) {
            return boundKey.getCategory() == Type.MOUSE ? GLFW.glfwGetMouseButton(handle, boundKey.getCode()) == 1 : false;
         } else {
            return boundKey.getCode() != InputUtil.UNKNOWN_KEY.getCode() && GLFW.glfwGetKey(handle, boundKey.getCode()) == 1;
         }
      } else {
         return false;
      }
   }
}
