package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.RangeSetting;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.mixins.AccessorKeyBinding;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.lwjgl.glfw.GLFW;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import ai.auth.Module;
import com.corz.client.utils.WTapCycle;
import com.corz.client.utils.KnockbackMath;
import com.corz.client.utils.ElytraAutoUtils;
import com.corz.client.utils.IconsPngType;

public class WTap extends Module {
   private long tickCounter;
   private final WTapCycle wTapCycle;
   private final BooleanSetting selectHitsSetting;
   private static final Options PLAYER_TARGET_FILTER = Options.INSTANCE2.getAnimalsTeammates(true).getInvisiblesAnimalsOptions(true);
   private final RangeSetting holdLength;
   private ClientPlayerEntity activePlayer;
   private final IntegerSetting releaseDelaySetting;
   private final IntegerSetting chance = new IntegerSetting("Chance", "How often to tap W when you hit a player", 90, 0, 100);

   private static boolean isKeyHeld(KeyBinding keyBinding) {
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

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke) {
         ClientPlayerEntity player = mc.player;
         if (player != null && mc.world != null && mc.options != null && mc.isOnThread()) {
            if (mc.currentScreen == null && PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
               int entityId = PlayerCrystalUtils.getEntityId(playerInteractEntityC2SPacke);
               if (!inventoryPlayer.isOpenedByClient() && this.wTapCycle.confirmPendingTap(entityId, this.tickCounter)) {
                  this.activePlayer = player;
               } else if (this.wTapCycle.isIdle()) {
                  Entity entityById = mc.world.getEntityById(entityId);
                  if (EntityPlayerUtilsLiving.canSpectatorDead(entityById, PLAYER_TARGET_FILTER)) {
                     if (KnockbackMath.isSprintHitReady(player.isSprinting(), player.getAttackCooldownProgress(0.5F))) {
                        if (mc.options.forwardKey.isPressed() && isKeyHeld(mc.options.forwardKey)) {
                           if (!this.selectHitsSetting.isEnabled()
                              || KnockbackMath.isAtMostBound(EntityDamageUtils.getMax((LivingEntity)entityById), EntityDamageUtils.getValueInt())) {
                              ThreadLocalRandom current = ThreadLocalRandom.current();
                              if (KnockbackMath.rollPercentChance((double)this.chance.getValue(), current.nextDouble())) {
                                 double llIValue = KnockbackMath.lerpClamped(this.holdLength.getMin(), this.holdLength.getMax(), current.nextDouble());
                                 int msToTicks = KnockbackMath.msToTicks(this.releaseDelaySetting.getValue());
                                 int rocketsBuyBound = KnockbackMath.msToTicks(llIValue);
                                 if (inventoryPlayer.isOpenedByClient()) {
                                    this.wTapCycle.queuePendingTap(entityId, this.tickCounter, msToTicks, rocketsBuyBound);
                                 } else {
                                    if (this.wTapCycle.armTap(this.tickCounter, msToTicks, rocketsBuyBound)) {
                                       this.activePlayer = player;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      this.restoreForwardKey();
      this.wTapCycle.reset();
      this.activePlayer = null;
   }

   @Override
   protected void onReset() {
      this.tickCounter++;
      if (!this.wTapCycle.isIdle()) {
         if (mc.player == null || mc.player != this.activePlayer || mc.options == null) {
            this.restoreForwardKey();
            this.wTapCycle.reset();
         } else if (mc.currentScreen != null) {
            this.wTapCycle.reset();
         } else {
            KeyBinding forwardKey = mc.options.forwardKey;
            switch (this.wTapCycle.advanceTap(this.tickCounter, isKeyHeld(forwardKey))) {
               case RELEASE:
               case HOLD:
                  ElytraAutoUtils.handleRawPressed(forwardKey, false);
                  break;
               case REPRESS:
                  ElytraAutoUtils.handleRawPressed(forwardKey, isKeyHeld(forwardKey));
            }
         }
      }
   }

   @Override
   protected void onEnable() {
      this.wTapCycle.reset();
      this.activePlayer = null;
   }

   private void restoreForwardKey() {
      if (this.wTapCycle.isReleased() && mc.options != null) {
         ElytraAutoUtils.handleRawPressed(
            mc.options.forwardKey, mc.currentScreen == null && isKeyHeld(mc.options.forwardKey)
         );
      }
   }

   public WTap() {
      super("W Tap", IconsPngType.COMBAT);
      this.releaseDelaySetting = new IntegerSetting("Release Delay", "Milliseconds to wait after the hit before letting go of W. 0 = right away", 0, 0, 500, 50);
      this.holdLength = new RangeSetting("Hold Length", "Random milliseconds W stays let go. 0 = one tick, the fastest reset", 50.0, 100.0, 0.0, 400.0, 10.0);
      this.selectHitsSetting = new BooleanSetting("Select Hits", "Skip the tap when the enemy can't take knockback yet", true);
      this.wTapCycle = new WTapCycle();
      this.addSettings(new ConfigSetting[]{this.chance, this.releaseDelaySetting, this.holdLength, this.selectHitsSetting});
   }
}
