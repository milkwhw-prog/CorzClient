package com.corz.client.categories.bots;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.config.StringBase;
import com.corz.client.mixins.AccessorClientPlayerInteractionManager;
import com.corz.client.world.MathClampUtils;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import com.corz.client.categories.misc.FakeCoordinates;
import ai.auth.Module;
import com.corz.client.utils.ToolBreakerUtils;
import com.corz.client.utils.WrapDegreesUtils;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.ElytraAutoUtils;
import com.corz.client.utils.IconsPngType;

public class SpawnerProtect extends Module {
   private final Set<BlockPos> skippedSpawnerPositions;
   private boolean breakingActive;
   private final IntegerSetting maxMineTimeSetting;
   private Direction breakingSide;
   private final DoubleSetting spawnerRangeSetting;
   private final BooleanSetting ignoreFriendsSetting;
   private boolean rotatingActive;
   private boolean sneakHeld;
   private final DoubleSetting detectRange = new DoubleSetting(
      "Detect Range", "How close another player must come to trigger the panic mine + logoff", 24.0, 4.0, 96.0, 1.0
   );
   private final DoubleSetting rotateSpeed;
   private float yaw;
   private BlockPos targetSpawnerPos;
   private boolean awaitingBreakResult;
   private int pickupWaitTicks;
   private int brokenBlockCount;
   private int noTargetCount;
   private SpawnerProtect.Phase phase;
   private float pitch;
   private int mineTicks;
   private final BooleanSetting autoTool;
   private final BooleanSetting sneakMine;
   private final IntegerSetting pickupWait;
   private int stableInventoryTicks;
   private float targetPitch;
   private int breakAttemptTicks;
   private int actionCooldownTicks;
   private final IntegerSetting breakDelay;
   private BlockPos lastTargetPos;
   private final BooleanSetting chatFeedback;
   private int collected;
   private int reactivateCooldownTicks;
   private final StringBase logoffMessageSetting;
   private int missedPickupCount;
   private int spawnerCountBeforeBreak;
   private float targetYaw;
   private int lastSpawnerCount;
   private BlockPos breakingPos;
   private int aimStuckTicks;

   public SpawnerProtect() {
      super("SpawnerProtect", IconsPngType.DONUTSMP);
      this.spawnerRangeSetting = new DoubleSetting("Spawner Range", "How far around you to look for spawners to grab (keep this within your reach)", 5.0, 2.0, 6.0, 0.5);
      this.ignoreFriendsSetting = new BooleanSetting("Ignore Friends", "Don't trigger when the only nearby player is a saved friend", true);
      this.sneakMine = new BooleanSetting(
         "Sneak Mine",
         "Hold crouch while mining. A crouched break takes 64 off a stacked spawner instead of 1 - with this off a 1700-spawner stack would need 1700 breaks",
         true
      );
      this.breakDelay = new IntegerSetting(
         "Break Delay",
         "Ticks to pause after each break before the next one starts (20 = 1 second). The stack is broken one hit at a time with this gap, never held down",
         20,
         10,
         100,
         5
      );
      this.maxMineTimeSetting = new IntegerSetting(
         "Max Mine Time",
         "Hard cap in seconds for the whole panic mine. When it runs out it logs off with whatever it got, so it can never leave you standing there",
         120,
         5,
         600,
         5
      );
      this.autoTool = new BooleanSetting("Auto Tool", "Switch to your best pickaxe before mining a spawner", true);
      this.rotateSpeed = new DoubleSetting(
         "Rotate Speed", "Max degrees the view turns per tick while aiming at a spawner (smooth, not an instant snap)", 45.0, 6.0, 180.0, 1.0
      );
      this.pickupWait = new IntegerSetting(
         "Pickup Wait", "Ticks to wait after the last break before logging off, as a backstop for a laggy hand-over", 40, 0, 200, 5
      );
      this.chatFeedback = new BooleanSetting("Chat Feedback", "Print what it is doing (and how many spawners it got) to your chat while it panics", true);
      this.logoffMessageSetting = new StringBase("Logoff Message", "Shown on the disconnect screen when it logs you off", "SpawnerProtect - Player Nearby");
      this.phase = SpawnerProtect.Phase.IDLE;
      this.skippedSpawnerPositions = new HashSet<>();
      this.addSettings(
         new ConfigSetting[]{
            this.detectRange,
            this.spawnerRangeSetting,
            this.ignoreFriendsSetting,
            this.sneakMine,
            this.breakDelay,
            this.maxMineTimeSetting,
            this.autoTool,
            this.rotateSpeed,
            this.pickupWait,
            this.chatFeedback,
            this.logoffMessageSetting
         }
      );
   }

   public boolean isBreakingSpawner() {
      return this.isEnabled() && this.breakingActive;
   }

   private void setSneakHeld(boolean flag) {
      if (flag || this.sneakHeld) {
         ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, flag);
         this.sneakHeld = flag;
      }
   }

   private void enterWaitPhase() {
      this.cancelBreaking();
      this.setSneakHeld(false);
      this.phase = SpawnerProtect.Phase.WAIT;
      this.pickupWaitTicks = 0;
      this.stableInventoryTicks = 0;
      this.lastSpawnerCount = this.countSpawnersInInventory();
   }

   private boolean isSpawnerAt(BlockPos pos) {
      return mc.world.getBlockState(pos).isOf(Blocks.SPAWNER);
   }

   private void startMining() {
      this.skippedSpawnerPositions.clear();
      this.targetSpawnerPos = null;
      this.lastTargetPos = null;
      this.actionCooldownTicks = 0;
      this.aimStuckTicks = 0;
      this.breakAttemptTicks = 0;
      this.mineTicks = 0;
      this.noTargetCount = 0;
      this.missedPickupCount = 0;
      this.brokenBlockCount = 0;
      this.collected = 0;
      this.awaitingBreakResult = false;
      if (this.findNearestSpawner() == null) {
         this.enterWaitPhase();
      } else {
         this.phase = SpawnerProtect.Phase.MINING;
         this.sendChatFeedback("Player nearby - grabbing spawners");
      }
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   private void selectBestTool(BlockPos pos) {
      PlayerInventory inventory = mc.player.getInventory();
      BlockState blockState = mc.world.getBlockState(pos);
      float[] floats = new float[PlayerInventory.getHotbarSize()];
      boolean[] booleans = new boolean[floats.length];

      for (int i = 0; i < floats.length; i++) {
         ItemStack stack = inventory.getStack(i);
         floats[i] = stack.getMiningSpeedMultiplier(blockState);
         booleans[i] = stack.isSuitableFor(blockState);
      }

      int selectedSlot = ToolBreakerUtils.selectBestToolIndex(floats, booleans, inventory.getSelectedSlot());
      if (selectedSlot != inventory.getSelectedSlot()) {
         inventory.setSelectedSlot(selectedSlot);
      }
   }

   private void continueBreaking(BlockPos blockPos, Direction side) {
      if (!this.isSpawnerAt(blockPos)) {
         this.cancelBreaking();
         this.targetSpawnerPos = null;
      } else {
         if (!blockPos.equals(this.breakingPos)) {
            this.cancelBreaking();
            this.breakingPos = blockPos;
            this.breakingSide = side;
         }

         AccessorClientPlayerInteractionManager interactionManager = (AccessorClientPlayerInteractionManager)mc.interactionManager;
         this.spawnerCountBeforeBreak = this.countSpawnersInInventory();
         if (mc.interactionManager.updateBlockBreakingProgress(blockPos, this.breakingSide)) {
            mc.player.swingHand(Hand.MAIN_HAND);
         }

         this.breakingActive = true;
         if (interactionManager.getBlockBreakingCooldown() > 0) {
            this.onBlockBroken();
         }
      }
   }

   private void checkBreakResult() {
      this.awaitingBreakResult = false;
      int grabbingSpawnersLimit = this.countSpawnersInInventory() - this.spawnerCountBeforeBreak;
      if (grabbingSpawnersLimit > 0) {
         this.collected += grabbingSpawnersLimit;
         this.missedPickupCount = 0;
         this.sendChatFeedback("+" + grabbingSpawnersLimit + " spawners (" + this.collected + " total)");
      } else if (this.collected > 0 && ++this.missedPickupCount >= 3) {
         if (this.lastTargetPos != null) {
            this.skippedSpawnerPositions.add(this.lastTargetPos);
         }

         this.missedPickupCount = 0;
         this.targetSpawnerPos = null;
      }
   }

   private BlockPos getNextTarget() {
      return this.lastTargetPos != null
            && !this.skippedSpawnerPositions.contains(this.lastTargetPos)
            && this.isSpawnerAt(this.lastTargetPos)
            && this.isInReach(this.lastTargetPos)
         ? this.lastTargetPos
         : this.findNearestSpawner();
   }

   @Override
   protected void onDisable() {
      this.cancelBreaking();
      this.setSneakHeld(false);
      this.resetState();
   }

   private BlockHitResult raycastFromEye(Vec3d vec, float degrees, float degrees2, double raycastMultiplyValue) {
      double angle = Math.toRadians(degrees);
      double radians = Math.toRadians(degrees2);
      Vec3d vec3d = new Vec3d(-Math.sin(angle) * Math.cos(radians), -Math.sin(radians), Math.cos(angle) * Math.cos(radians));
      Vec3d add = vec.add(vec3d.multiply(raycastMultiplyValue));
      return mc.world.raycast(new RaycastContext(vec, add, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
   }

   private void onBlockBroken() {
      this.breakingActive = false;
      this.breakingPos = null;
      this.breakingSide = null;
      this.brokenBlockCount++;
      this.awaitingBreakResult = true;
      this.targetSpawnerPos = null;
      this.aimStuckTicks = 0;
      this.breakAttemptTicks = 0;
      this.actionCooldownTicks = Math.max(10, this.breakDelay.getValue());
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         this.rotatingActive = false;
         if (this.reactivateCooldownTicks > 0) {
            this.reactivateCooldownTicks--;
         } else {
            switch (this.phase) {
               case IDLE:
                  if (this.isPlayerNearby()) {
                     this.startMining();
                  }
                  break;
               case MINING:
                  this.tickMining();
                  break;
               case WAIT:
                  this.tickWaiting();
            }
         }
      }
   }

   private float[] computeAimAngles(Vec3d vec) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(mc.player.getEyePos(), vec);
      return this.rotateToward(wrapDegrees[0], wrapDegrees[1]);
   }

   private void cancelBreaking() {
      if (this.breakingActive) {
         mc.interactionManager.cancelBlockBreaking();
         this.breakingActive = false;
      }

      this.breakingPos = null;
      this.breakingSide = null;
   }

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      if (this.rotatingActive && mc.player != null) {
         float tickDelta = deltaAngle.getTickDelta();
         deltaAngle.setAngleDegrees(MathHelper.lerpAngleDegrees(tickDelta, this.yaw, this.targetYaw));
         deltaAngle.setLerp(MathHelper.lerp(tickDelta, this.pitch, this.targetPitch));
      }
   }

   private void tickMining() {
      if (++this.mineTicks > this.maxMineTimeSetting.getValue() * 20) {
         this.sendChatFeedback("Mine time up - leaving with " + this.collected);
         this.enterWaitPhase();
      } else {
         this.setSneakHeld(this.sneakMine.isEnabled());
         ((AccessorClientPlayerInteractionManager)mc.interactionManager).setBlockBreakingCooldown(0);
         if (this.actionCooldownTicks > 0) {
            this.actionCooldownTicks--;
         } else {
            if (this.awaitingBreakResult) {
               this.checkBreakResult();
            }

            if (this.breakingActive && this.breakingPos != null && !this.isSpawnerAt(this.breakingPos)) {
               this.onBlockBroken();
            } else {
               if (this.targetSpawnerPos == null || !this.isSpawnerAt(this.targetSpawnerPos) || this.skippedSpawnerPositions.contains(this.targetSpawnerPos)) {
                  this.targetSpawnerPos = this.getNextTarget();
                  this.aimStuckTicks = 0;
                  this.breakAttemptTicks = 0;
                  if (this.targetSpawnerPos == null) {
                     if (++this.noTargetCount >= 2) {
                        this.sendChatFeedback("Spawners collected: " + this.collected + " - logging off");
                        this.enterWaitPhase();
                        return;
                     }

                     this.actionCooldownTicks = 20;
                     return;
                  }

                  this.noTargetCount = 0;
                  if (!this.targetSpawnerPos.equals(this.lastTargetPos)) {
                     this.lastTargetPos = this.targetSpawnerPos;
                     this.missedPickupCount = 0;
                  }
               }

               if (!this.sneakMine.isEnabled() || mc.player.isSneaking()) {
                  if (this.autoTool.isEnabled()) {
                     this.selectBestTool(this.targetSpawnerPos);
                  }

                  float[] eyePos = this.computeAimAngles(Vec3d.ofCenter(this.targetSpawnerPos));
                  if (this.breakingActive && this.targetSpawnerPos.equals(this.breakingPos)) {
                     if (++this.breakAttemptTicks > 300) {
                        this.skipCurrentTarget();
                     } else {
                        this.continueBreaking(this.targetSpawnerPos, this.breakingSide);
                     }
                  } else if (++this.aimStuckTicks > 60) {
                     this.skipCurrentTarget();
                  } else {
                     BlockHitResult raycastMultiply = this.raycastFromEye(mc.player.getEyePos(), eyePos[0], eyePos[1], 6.0);
                     if (raycastMultiply.getType() == Type.BLOCK && raycastMultiply.getBlockPos().equals(this.targetSpawnerPos)) {
                        this.continueBreaking(this.targetSpawnerPos, raycastMultiply.getSide());
                     }
                  }
               }
            }
         }
      }
   }

   private void tickWaiting() {
      this.cancelBreaking();
      this.setSneakHeld(false);
      if (this.brokenBlockCount == 0) {
         this.logOff();
      } else {
         int value = this.countSpawnersInInventory();
         if (value != this.lastSpawnerCount) {
            this.lastSpawnerCount = value;
            this.stableInventoryTicks = 0;
         } else {
            this.stableInventoryTicks++;
         }

         if (++this.pickupWaitTicks >= this.pickupWait.getValue() || this.collected > 0 && this.stableInventoryTicks >= 10) {
            this.logOff();
         }
      }
   }

   private int countSpawnersInInventory() {
      int stacksIterator = 0;

      for (ItemStack itemStack : mc.player.getInventory().getMainStacks()) {
         if (itemStack.isOf(Items.SPAWNER)) {
            stacksIterator += itemStack.getCount();
         }
      }

      return stacksIterator;
   }

   private void skipCurrentTarget() {
      if (this.targetSpawnerPos != null) {
         this.skippedSpawnerPositions.add(this.targetSpawnerPos);
      }

      this.cancelBreaking();
      this.targetSpawnerPos = null;
      this.aimStuckTicks = 0;
      this.breakAttemptTicks = 0;
   }

   private float[] rotateToward(float pitchYawValue, float wrapDegreesValue) {
      this.yaw = mc.player.getYaw();
      this.pitch = mc.player.getPitch();
      this.rotatingActive = true;
      float pitchYawValue3 = (float)this.rotateSpeed.getValue();
      float clamp = MathHelper.clamp(pitchYawValue3 / 150.0F, 0.08F, 0.9F);
      float amount = WrapDegreesUtils.stepAngleProportional(this.yaw, pitchYawValue, clamp, pitchYawValue3, 0.05F);
      float clamp2 = MathHelper.clamp(WrapDegreesUtils.stepAngleProportional(this.pitch, wrapDegreesValue, clamp, pitchYawValue3, 0.05F), -90.0F, 90.0F);
      mc.player.setYaw(amount);
      mc.player.setPitch(clamp2);
      this.targetYaw = amount;
      this.targetPitch = clamp2;
      return new float[]{amount, clamp2};
   }

   private void logOff() {
      this.cancelBreaking();
      this.setSneakHeld(false);
      String grabbingProtectText = this.logoffMessageSetting.getCurrentText();
      if (grabbingProtectText == null || grabbingProtectText.isBlank()) {
         grabbingProtectText = "SpawnerProtect - Player Nearby";
      }

      if (mc.getNetworkHandler() != null) {
         mc.getNetworkHandler().getConnection().disconnect(Text.literal(grabbingProtectText));
      }

      this.phase = SpawnerProtect.Phase.IDLE;
      this.targetSpawnerPos = null;
      this.lastTargetPos = null;
      this.breakingPos = null;
      this.breakingSide = null;
      this.breakingActive = false;
      this.brokenBlockCount = 0;
      this.collected = 0;
      this.awaitingBreakResult = false;
      this.missedPickupCount = 0;
      this.noTargetCount = 0;
      this.actionCooldownTicks = 0;
      this.skippedSpawnerPositions.clear();
      this.reactivateCooldownTicks = 60;
   }

   private boolean isInReach(BlockPos pos) {
      double centerSquaredFactor = this.spawnerRangeSetting.getValue();
      return mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(pos)) <= centerSquaredFactor * centerSquaredFactor;
   }

   private void resetState() {
      this.phase = SpawnerProtect.Phase.IDLE;
      this.targetSpawnerPos = null;
      this.lastTargetPos = null;
      this.breakingPos = null;
      this.breakingSide = null;
      this.breakingActive = false;
      this.actionCooldownTicks = 0;
      this.aimStuckTicks = 0;
      this.breakAttemptTicks = 0;
      this.mineTicks = 0;
      this.noTargetCount = 0;
      this.missedPickupCount = 0;
      this.brokenBlockCount = 0;
      this.collected = 0;
      this.awaitingBreakResult = false;
      this.spawnerCountBeforeBreak = 0;
      this.pickupWaitTicks = 0;
      this.stableInventoryTicks = 0;
      this.lastSpawnerCount = 0;
      this.reactivateCooldownTicks = 0;
      this.skippedSpawnerPositions.clear();
   }

   private boolean isPlayerNearby() {
      double spectatorPlayersFactor = this.detectRange.getValue();
      double spectatorPlayersLimit = spectatorPlayersFactor * spectatorPlayersFactor;

      for (AbstractClientPlayerEntity abstractClientPlayerEntity : mc.world.getPlayers()) {
         if (abstractClientPlayerEntity != mc.player
            && !abstractClientPlayerEntity.isSpectator()
            && (!this.ignoreFriendsSetting.isEnabled() || !FriendNameStore.canFlag(abstractClientPlayerEntity))
            && abstractClientPlayerEntity.squaredDistanceTo(mc.player) <= spectatorPlayersLimit) {
            return true;
         }
      }

      return false;
   }

   private void sendChatFeedback(String hudGameText) {
      if (this.chatFeedback.isEnabled() && mc.inGameHud != null) {
         mc.inGameHud.getChatHud().addMessage(FakeCoordinates.getLiteral(Text.literal("\u00a78[\u00a7bSpawnerProtect\u00a78] \u00a77" + hudGameText)));
      }
   }

   private BlockPos findNearestSpawner() {
      Vec3d eyePos = mc.player.getEyePos();
      double spawnerProtectFactor = this.spawnerRangeSetting.getValue();
      double spawnerProtectValue = spawnerProtectFactor * spawnerProtectFactor;
      int spawnerProtectLimit = (int)Math.ceil(spawnerProtectFactor);
      BlockPos blockPos = mc.player.getBlockPos();
      BlockPos add = null;
      double maxValue = Double.MAX_VALUE;

      for (int i = -spawnerProtectLimit; i <= spawnerProtectLimit; i++) {
         for (int j = -spawnerProtectLimit; j <= spawnerProtectLimit; j++) {
            for (int k = -spawnerProtectLimit; k <= spawnerProtectLimit; k++) {
               BlockPos goodsSlot = blockPos.add(i, j, k);
               if (!this.skippedSpawnerPositions.contains(goodsSlot) && mc.world.getBlockState(goodsSlot).isOf(Blocks.SPAWNER)) {
                  double squaredDistanceTo = eyePos.squaredDistanceTo(Vec3d.ofCenter(goodsSlot));
                  if (!(squaredDistanceTo > spawnerProtectValue) && squaredDistanceTo < maxValue) {
                     maxValue = squaredDistanceTo;
                     add = goodsSlot;
                  }
               }
            }
         }
      }

      return add;
   }

   private static enum Phase {
      IDLE,
      MINING,
      WAIT;
   }
}
