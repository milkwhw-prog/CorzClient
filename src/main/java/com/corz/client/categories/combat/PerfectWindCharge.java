package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.inventory.SelectedInventoryUtils;
import com.corz.client.mixins.AccessorKeyBinding;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import com.corz.client.utils.WrapDegreesUtils;
import ai.auth.Module;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.IconsPngType;

public class PerfectWindCharge extends Module {
   private boolean pitchOverrideActive;
   private float targetPitch;
   private int originalSlot;
   private int windChargeTickCount;
   private boolean jumpKeyForced;
   private final BooleanSetting jumpSetting = new BooleanSetting(
      "Jump",
      "Jump at the instant the charge fires \u2014 the jump's upward velocity stacks with the blast for the maximum-height launch. Off = launch without jumping (e.g. under a low ceiling).",
      true
   );
   private boolean chargeFired;
   private boolean slotPrepared;
   private float playerPitch;

   private boolean isJumpKeyHeld() {
      if (mc.options != null && mc.getWindow() != null) {
         long handle = mc.getWindow().getHandle();
         Key boundKey = ((AccessorKeyBinding)mc.options.jumpKey).getBoundKey();
         if (boundKey.getCategory() == Type.KEYSYM) {
            return GLFW.glfwGetKey(handle, boundKey.getCode()) == 1;
         } else {
            return boundKey.getCategory() == Type.MOUSE ? GLFW.glfwGetMouseButton(handle, boundKey.getCode()) == 1 : false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.interactionManager != null && mc.player.networkHandler != null) {
         if (mc.currentScreen != null) {
            this.pitchOverrideActive = false;
         } else if (this.chargeFired) {
            this.restoreJumpKey();
            if (this.originalSlot != -1 && mc.player.getInventory().getSelectedSlot() != this.originalSlot) {
               mc.player.getInventory().setSelectedSlot(this.originalSlot);
            }

            this.originalSlot = -1;
            this.pitchOverrideActive = false;
            this.setEnabled(false);
         } else {
            int selectedSlot = SelectedInventoryUtils.getValue(Items.WIND_CHARGE);
            if (selectedSlot == -1) {
               this.pitchOverrideActive = false;
               this.setEnabled(false);
            } else {
               if (!this.slotPrepared) {
                  int selectedSlot2 = mc.player.getInventory().getSelectedSlot();
                  this.originalSlot = selectedSlot2 != selectedSlot ? selectedSlot2 : -1;
                  if (selectedSlot2 != selectedSlot) {
                     mc.player.getInventory().setSelectedSlot(selectedSlot);
                  }

                  this.slotPrepared = true;
               }

               this.playerPitch = mc.player.getPitch();
               this.pitchOverrideActive = true;
               float clamp = MathHelper.clamp(WrapDegreesUtils.stepAngleProportional(this.playerPitch, 90.0F, 0.5F, 60.0F, 8.0F), -90.0F, 90.0F);
               mc.player.setPitch(clamp);
               this.targetPitch = clamp;
               this.windChargeTickCount++;
               if (this.windChargeTickCount >= 2 && 90.0F - clamp <= 1.0F && mc.player.isOnGround()) {
                  if (this.jumpSetting.isEnabled() && mc.options != null) {
                     mc.options.jumpKey.setPressed(true);
                     this.jumpKeyForced = true;
                  }

                  mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                  mc.player.swingHand(Hand.MAIN_HAND);
                  this.chargeFired = true;
               }
            }
         }
      }
   }

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      if (this.pitchOverrideActive && mc.player != null) {
         deltaAngle.setLerp(MathHelper.lerp(deltaAngle.getTickDelta(), this.playerPitch, this.targetPitch));
         deltaAngle.updateFlag();
      }
   }

   @Override
   protected void onDisable() {
      this.restoreJumpKey();
      this.pitchOverrideActive = false;
      if (this.originalSlot != -1 && mc.player != null && mc.player.getInventory().getSelectedSlot() != this.originalSlot) {
         mc.player.getInventory().setSelectedSlot(this.originalSlot);
      }

      this.originalSlot = -1;
      this.slotPrepared = false;
      this.chargeFired = false;
      this.windChargeTickCount = 0;
   }

   public PerfectWindCharge() {
      super("Perfect Wind Charge", IconsPngType.COMBAT);
      this.originalSlot = -1;
      this.addSettings(new ConfigSetting[]{this.jumpSetting});
   }

   @Override
   protected void onEnable() {
      this.originalSlot = -1;
      this.slotPrepared = false;
      this.chargeFired = false;
      this.windChargeTickCount = 0;
      this.pitchOverrideActive = false;
      this.restoreJumpKey();
   }

   private void restoreJumpKey() {
      if (this.jumpKeyForced && mc.options != null) {
         mc.options.jumpKey.setPressed(this.isJumpKeyHeld());
      }

      this.jumpKeyForced = false;
   }
}
