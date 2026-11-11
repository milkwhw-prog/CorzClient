package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.RangeSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.TimeInstanceUtils;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.lwjgl.glfw.GLFW;
import ai.auth.Module;
import com.corz.client.categories.combat.SilentAim;
import com.corz.client.utils.IconsPngType;

public class AutoClicker extends Module {
   private final RangeSetting cpsMinmax = new RangeSetting("CPS", "Clicks per second range", 10.0, 14.0, 1.0, 24.0, 1.0);
   private int clickDelayTicks;
   private double currentCpsSample;
   private int clicksThisHold;
   private boolean mouseHeld;
   private int fastClickStreak;
   private final BooleanSetting mineBlocks = new BooleanSetting(
      "Mine Blocks", "Keep the click cadence while aiming at blocks (swings only \u2014 holding the button already mines). Off = go quiet over blocks", false
   );

   @Override
   protected void onEnable() {
      this.resetState();
   }

   private void swingOrAttack(boolean handSwingFlag) {
      if (!SilentAim.tryAttackLockedTarget(false)) {
         if (SilentAim.isTargetLocked()) {
            mc.player.swingHand(Hand.MAIN_HAND);
         } else if (!handSwingFlag && mc.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            if (!TimeInstanceUtils.canTimeMin(livingEntity)) {
               mc.player.swingHand(Hand.MAIN_HAND);
            } else if (ClientThreadUtils.canPassFlag(this, livingEntity)) {
               TimeInstanceUtils.updateDequeArray();
               mc.interactionManager.attackEntity(mc.player, livingEntity);
               mc.player.swingHand(Hand.MAIN_HAND);
               mc.player.resetTicksSinceLastAttack();
            }
         } else {
            mc.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   private void resetState() {
      this.mouseHeld = false;
      this.clickDelayTicks = 0;
      this.clicksThisHold = 0;
      this.fastClickStreak = 0;
      this.currentCpsSample = 0.0;
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null && mc.currentScreen == null) {
         if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 0) != 1) {
            this.mouseHeld = false;
         } else if (!this.mouseHeld) {
            this.mouseHeld = true;
            this.clicksThisHold = 0;
            this.currentCpsSample = 0.0;
            this.clickDelayTicks = this.computeNextClickDelayTicks();
         } else if (this.clickDelayTicks > 0) {
            this.clickDelayTicks--;
         } else {
            boolean iIiFlag = mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.BLOCK;
            if (!iIiFlag || this.mineBlocks.isEnabled()) {
               this.swingOrAttack(iIiFlag);
               this.clicksThisHold++;
               this.clickDelayTicks = this.computeNextClickDelayTicks();
            }
         }
      } else {
         this.mouseHeld = false;
      }
   }

   private int computeNextClickDelayTicks() {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      double minmaxBound = this.cpsMinmax.getMin();
      double hypot = this.cpsMinmax.getMax();
      double minmaxFactor = Math.max(0.5, hypot - minmaxBound);
      if (this.currentCpsSample == 0.0) {
         this.currentCpsSample = (minmaxBound + hypot) / 2.0;
      }

      this.currentCpsSample = this.currentCpsSample + current.nextGaussian() * minmaxFactor * 0.18;
      if (current.nextDouble() < 0.05) {
         this.currentCpsSample = minmaxBound + current.nextDouble() * minmaxFactor;
      }

      this.currentCpsSample = Math.min(hypot, Math.max(minmaxBound, this.currentCpsSample));
      double minmaxDivisor = this.currentCpsSample;
      if (this.clicksThisHold < 4) {
         minmaxDivisor *= 0.78 + 0.06 * this.clicksThisHold;
      }

      double minmaxFactor2 = 1000.0 / minmaxDivisor;
      double minmaxTotal = minmaxFactor2 * (1.0 + current.nextGaussian() * 0.22);
      if (current.nextDouble() < 0.09) {
         minmaxTotal += 100.0 + current.nextDouble() * 200.0;
      }

      double minmaxValue = Math.max(1.0, minmaxTotal / 50.0);
      int minMinmax = (int)minmaxValue;
      if (current.nextDouble() < minmaxValue - minMinmax) {
         minMinmax++;
      }

      if (minMinmax >= 3) {
         this.fastClickStreak = 0;
      } else if (++this.fastClickStreak >= 8 + current.nextInt(4)) {
         minMinmax = 3 + current.nextInt(3);
         this.fastClickStreak = 0;
      }

      return minMinmax;
   }

   public AutoClicker() {
      super("AutoClicker", IconsPngType.COMBAT);
      this.addSettings(new ConfigSetting[]{this.cpsMinmax, this.mineBlocks});
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }
}
