package com.corz.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

public final class ScriptedMovementInput extends Input {
   private int tickCounter;
   public int sneakHoldTicks;
   private boolean sneakRequested;
   private float strafeAmount;
   public boolean jumpBlocked;
   private float forwardAmount;
   private boolean sprintRequested;
   private boolean jumpRequested;

   public void clearInput() {
      this.forwardAmount = 0.0F;
      this.strafeAmount = 0.0F;
      this.jumpRequested = false;
      this.sprintRequested = false;
      this.sneakRequested = false;
   }

   public void setMovementInput(float fluidInputValue, float fluidInputBound, boolean fluidInputFlag, boolean fluidInputFlag2) {
      this.setMovementInputWithSneak(fluidInputValue, fluidInputBound, fluidInputFlag, fluidInputFlag2, false);
   }

   private static float axisValue(boolean fluidInputFlag, boolean fluidInputFlag2) {
      if (fluidInputFlag == fluidInputFlag2) {
         return 0.0F;
      } else {
         return fluidInputFlag ? 1.0F : -1.0F;
      }
   }

   public void setMovementInputWithSneak(float fluidInputValue, float fluidInputBound, boolean flag, boolean flag2, boolean yawInputFlag) {
      this.forwardAmount = clampToUnit(fluidInputValue);
      this.strafeAmount = clampToUnit(fluidInputBound);
      this.jumpRequested = flag;
      this.sprintRequested = flag2;
      this.sneakRequested = yawInputFlag;
   }

   private static float clampToUnit(float fluidInputBound) {
      return Math.max(-1.0F, Math.min(1.0F, fluidInputBound));
   }

   @Override
   public void tick() {
      this.tickCounter++;
      if (this.sneakHoldTicks > 0) {
         this.sneakHoldTicks--;
      }

      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity clientPlayerEntity = instance != null ? instance.player : null;
      boolean fluidInputFlag = false;
      boolean fluidInputFlag2 = false;
      if (clientPlayerEntity != null) {
         try {
            fluidInputFlag2 = clientPlayerEntity.isSubmergedInWater() || clientPlayerEntity.isInLava();
            fluidInputFlag = clientPlayerEntity.isTouchingWater() || fluidInputFlag2;
            if (!fluidInputFlag && instance.world != null) {
               BlockPos blockPos = clientPlayerEntity.getBlockPos();
               fluidInputFlag = !instance.world.getBlockState(blockPos).getFluidState().isEmpty() || !instance.world.getBlockState(blockPos.up()).getFluidState().isEmpty();
            }
         } catch (Throwable e) {
         }
      }

      if (!fluidInputFlag && ExtUtils.isActionFlag2()) {
         this.playerInput = new PlayerInput(false, false, false, false, false, false, false);
         this.movementVector = Vec2f.ZERO;
      } else {
         boolean fluidInputFlag3 = !fluidInputFlag && (this.sneakRequested || this.sneakHoldTicks > 0);
         boolean fluidInputFlag4 = fluidInputFlag ? fluidInputFlag2 || !this.jumpBlocked : this.jumpRequested && !this.jumpBlocked;
         boolean axisPressed = this.isAxisPressed(this.forwardAmount);
         boolean axisPressed6 = this.isAxisPressed(-this.forwardAmount);
         boolean axisPressed7 = this.isAxisPressed(this.strafeAmount);
         boolean axisPressed8 = this.isAxisPressed(-this.strafeAmount);
         this.playerInput = new PlayerInput(axisPressed, axisPressed6, axisPressed7, axisPressed8, fluidInputFlag4, fluidInputFlag3, this.sprintRequested && axisPressed && !fluidInputFlag3);
         this.movementVector = new Vec2f(axisValue(axisPressed7, axisPressed8), axisValue(axisPressed, axisPressed6)).normalize();
      }
   }

   public void clearHoldState() {
      this.sneakHoldTicks = 0;
      this.jumpBlocked = false;
   }

   private boolean isAxisPressed(float fluidInputLimit) {
      return fluidInputLimit >= 0.5F || fluidInputLimit > 0.0F && this.tickCounter % 3 == 0;
   }
}
