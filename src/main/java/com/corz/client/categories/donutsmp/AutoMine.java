package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class AutoMine extends Module {
   private float yaw;
   private final BooleanSetting autoBreak;
   private final BooleanSetting avoidlava;
   private final BooleanSetting autoforward;
   private final DoubleSetting pitchSetting;
   private int avoidTicksRemaining;
   private final BooleanSetting lockpitch = new BooleanSetting("LockPitch", "Locks the player's pitch to a fixed value", true);
   private final BooleanSetting lockyaw;
   private final IntegerSetting scanRange;
   private BlockPos miningTarget;
   private Direction avoidDirection;

   private void mineBlockAhead() {
      Direction horizontalDegrees = Direction.fromHorizontalDegrees(this.yaw);
      if (this.miningTarget != null) {
         BlockState blockState = mc.world.getBlockState(this.miningTarget);
         if (this.isBreakableBlock(blockState, this.miningTarget)) {
            mc.interactionManager.updateBlockBreakingProgress(this.miningTarget, horizontalDegrees.getOpposite());
            mc.player.swingHand(Hand.MAIN_HAND);
            return;
         }

         this.miningTarget = null;
      }

      BlockPos offset = mc.player.getBlockPos().offset(horizontalDegrees);
      if (this.isBreakableBlock(mc.world.getBlockState(offset), offset)) {
         this.miningTarget = offset;
         mc.interactionManager.attackBlock(this.miningTarget, horizontalDegrees.getOpposite());
         mc.player.swingHand(Hand.MAIN_HAND);
      }
   }

   private boolean isFireOrLava(BlockPos pos2) {
      Block block = mc.world.getBlockState(pos2).getBlock();
      return block == Blocks.LAVA || block == Blocks.FIRE || block == Blocks.SOUL_FIRE;
   }

   private Direction getHazardAheadDirection() {
      BlockPos blockPos = mc.player.getBlockPos();
      Direction horizontalDegrees = Direction.fromHorizontalDegrees(this.yaw);
      int horizontalDegreesLimit = this.scanRange.getValue();

      for (int i = 1; i <= horizontalDegreesLimit; i++) {
         BlockPos offset = blockPos.offset(horizontalDegrees, i);

         for (int j = -1; j <= 2; j++) {
            if (this.isFireOrLava(offset.up(j))) {
               return horizontalDegrees;
            }
         }

         Direction rotateYCounterclockwise = horizontalDegrees.rotateYCounterclockwise();
         Direction rotateYClockwise = horizontalDegrees.rotateYClockwise();

         for (int k = -1; k <= 2; k++) {
            if (this.isFireOrLava(offset.offset(rotateYCounterclockwise).up(k))) {
               return horizontalDegrees;
            }

            if (this.isFireOrLava(offset.offset(rotateYClockwise).up(k))) {
               return horizontalDegrees;
            }
         }
      }

      return null;
   }

   @Override
   protected void onEnable() {
      if (mc.player != null) {
         this.yaw = mc.player.getYaw();
      }

      this.avoidTicksRemaining = 0;
      this.avoidDirection = null;
      this.miningTarget = null;
   }

   @Override
   protected void onDisable() {
      if (mc.player != null) {
         mc.options.forwardKey.setPressed(false);
         mc.options.leftKey.setPressed(false);
         mc.options.rightKey.setPressed(false);
         mc.options.attackKey.setPressed(false);
      }

      this.avoidTicksRemaining = 0;
      this.avoidDirection = null;
   }

   private void releaseStrafeKeys() {
      mc.options.leftKey.setPressed(false);
      mc.options.rightKey.setPressed(false);
   }

   private int countHazardsOnSide(BlockPos pos, Direction direction) {
      int offsetDegrees = 0;
      Direction horizontalDegrees = Direction.fromHorizontalDegrees(this.yaw);

      for (int i = 0; i <= this.scanRange.getValue(); i++) {
         BlockPos offset = pos.offset(horizontalDegrees, i).offset(direction, 2);

         for (int j = -1; j <= 2; j++) {
            if (this.isFireOrLava(offset.up(j))) {
               offsetDegrees++;
            }
         }
      }

      return offsetDegrees;
   }

   private Direction chooseSaferSide() {
      BlockPos blockPos = mc.player.getBlockPos();
      Direction horizontalDegrees = Direction.fromHorizontalDegrees(this.yaw);
      Direction rotateYCounterclockwise = horizontalDegrees.rotateYCounterclockwise();
      Direction rotateYClockwise = horizontalDegrees.rotateYClockwise();
      int offsetDegrees = this.countHazardsOnSide(blockPos, rotateYCounterclockwise);
      int countHazardsOnSide2 = this.countHazardsOnSide(blockPos, rotateYClockwise);
      return offsetDegrees <= countHazardsOnSide2 ? rotateYCounterclockwise : rotateYClockwise;
   }

   public AutoMine() {
      super("AutoMine", IconsPngType.DONUTSMP);
      this.pitchSetting = new DoubleSetting("Pitch", "The pitch angle to lock to", 0.0, -90.0, 90.0, 1.0);
      this.lockyaw = new BooleanSetting("LockYaw", "Locks the player's yaw to the initial direction", true);
      this.autoforward = new BooleanSetting("AutoForward", "Automatically moves the player forward", true);
      this.autoBreak = new BooleanSetting("AutoBreak", "Automatically breaks blocks ahead", true);
      this.avoidlava = new BooleanSetting("AvoidLava", "Detects lava ahead and reroutes around it", true);
      this.scanRange = new IntegerSetting("ScanRange", "How many blocks ahead to scan for dangers", 4, 1, 8);
      this.yaw = 0.0F;
      this.avoidTicksRemaining = 0;
      this.avoidDirection = null;
      this.miningTarget = null;
      this.addSettings(
         new ConfigSetting[]{this.lockpitch, this.pitchSetting, this.lockyaw, this.autoforward, this.autoBreak, this.avoidlava, this.scanRange}
      );
   }

   private boolean isBreakableBlock(BlockState blockState, BlockPos pos2) {
      return !blockState.isAir() && !this.isLiquidBlock(blockState) && blockState.getHardness(mc.world, pos2) >= 0.0F;
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (this.lockpitch.isEnabled()) {
            mc.player.setPitch((float)this.pitchSetting.getValue());
         }

         if (this.lockyaw.isEnabled()) {
            mc.player.setYaw(this.yaw);
         }

         if (this.avoidlava.isEnabled()) {
            if (this.avoidTicksRemaining > 0) {
               this.applyAvoidanceMovement();
               return;
            }

            Direction horizontalDegrees = this.getHazardAheadDirection();
            if (horizontalDegrees != null) {
               this.avoidDirection = this.chooseSaferSide();
               this.avoidTicksRemaining = 8;
               this.applyAvoidanceMovement();
               return;
            }
         }

         this.releaseStrafeKeys();
         if (this.autoBreak.isEnabled()) {
            mc.options.attackKey.setPressed(true);
         }

         if (this.autoforward.isEnabled()) {
            mc.options.forwardKey.setPressed(true);
         }
      }
   }

   private void applyAvoidanceMovement() {
      this.avoidTicksRemaining--;
      if (this.lockpitch.isEnabled()) {
         mc.player.setPitch((float)this.pitchSetting.getValue());
      }

      if (this.lockyaw.isEnabled()) {
         mc.player.setYaw(this.yaw);
      }

      Direction horizontalDegrees = Direction.fromHorizontalDegrees(this.yaw);
      Direction rotateYCounterclockwise = horizontalDegrees.rotateYCounterclockwise();
      if (this.avoidDirection == rotateYCounterclockwise) {
         mc.options.leftKey.setPressed(true);
         mc.options.rightKey.setPressed(false);
      } else {
         mc.options.rightKey.setPressed(true);
         mc.options.leftKey.setPressed(false);
      }

      if (this.autoforward.isEnabled()) {
         mc.options.forwardKey.setPressed(true);
      }

      if (this.autoBreak.isEnabled()) {
         mc.options.attackKey.setPressed(true);
      }

      if (this.avoidTicksRemaining <= 0) {
         this.releaseStrafeKeys();
         this.avoidDirection = null;
      }
   }

   private boolean isLiquidBlock(BlockState blockState) {
      Block block = blockState.getBlock();
      return block == Blocks.WATER || block == Blocks.LAVA;
   }
}
