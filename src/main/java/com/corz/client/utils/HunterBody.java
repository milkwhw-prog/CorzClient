package com.corz.client.utils;

import com.corz.client.world.MathClampUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import com.corz.client.utils.ClampPitch;
import com.corz.client.utils.WrapDegreesUtils;

public final class HunterBody {
   private final ClampPitch clampPitch;
   private long lastSlotSwitchTick;
   public static final int UNIT_STEP = 1;
   private int selectedSlot;
   private long currentTick;
   private int heldKeyMask;
   private int previousKeyMask;
   private final MinecraftClient client = MinecraftClient.getInstance();

   public Vec3d getVisibleAimPoint(BlockPos pos2, double eyePosValue) {
      ClientPlayerEntity player = this.client.player;
      return this.findVisibleFacePoint(player.getEyePos(), pos2, eyePosValue);
   }

   public void releaseStaleKeys() {
      GameOptions options = this.client.options;
      if (options != null) {
         int optionsValue = this.previousKeyMask & ~this.heldKeyMask;
         if (optionsValue != 0) {
            setKeysPressed(options, optionsValue, false);
         }
      }
   }

   public void updateRotation() {
      this.clampPitch.updateSmoothedRotation();
   }

   public static KeyBinding[] getControlKeys(GameOptions gameOptions) {
      return new KeyBinding[]{
         gameOptions.forwardKey, gameOptions.backKey, gameOptions.leftKey, gameOptions.rightKey, gameOptions.jumpKey, gameOptions.sneakKey, gameOptions.sprintKey, gameOptions.attackKey, gameOptions.useKey, gameOptions.dropKey, gameOptions.swapHandsKey, gameOptions.inventoryKey
      };
   }

   public BlockHitResult raycastForward(double missedValue) {
      ClientPlayerEntity player = this.client.player;
      Vec3d eyePos = player.getEyePos();
      Vec3d add = eyePos.add(MathClampUtils.getCosSin(player.getYaw(), player.getPitch()).multiply(missedValue));
      return this.client.world.raycast(new RaycastContext(eyePos, add, ShapeType.OUTLINE, FluidHandling.NONE, player)) instanceof BlockHitResult blockHitResult
         ? blockHitResult
         : BlockHitResult.createMissed(add, Direction.UP, BlockPos.ofFloored(add));
   }

   public HunterBody() {
      this.clampPitch = new ClampPitch(0.35F, 0.8F);
      this.selectedSlot = -1;
      this.lastSlotSwitchTick = Long.MIN_VALUE;
      this.clampPitch.setMaxStep(24.0F);
   }

   public void moveTowardPoint(Vec3d vec, boolean yawFlag) {
      ClientPlayerEntity player = this.client.player;
      if (yawFlag) {
         this.pressKeys(32);
      }

      double deltaX = vec.x - player.getX();
      double deltaZ = vec.z - player.getZ();
      if (!(deltaX * deltaX + deltaZ * deltaZ < 0.02)) {
         double angle = Math.toRadians(player.getYaw());
         double sin = Math.sin(angle);
         double cos = Math.cos(angle);
         double yawLimit = -deltaX * sin + deltaZ * cos;
         double yawLimit2 = deltaX * cos + deltaZ * sin;
         if (yawLimit > 0.08) {
            this.pressKeys(1);
         } else if (yawLimit < -0.08) {
            this.pressKeys(2);
         }

         if (yawLimit2 > 0.08) {
            this.pressKeys(4);
         } else if (yawLimit2 < -0.08) {
            this.pressKeys(8);
         }
      }
   }

   public void applyDeltaAngle(DeltaAngle deltaAngle) {
      this.clampPitch.applyInterpolatedAngles(deltaAngle);
   }

   private static void setKeysPressed(GameOptions gameOptions, int forwardSprintValue, boolean forwardSprintFlag) {
      if ((forwardSprintValue & 1) != 0) {
         ElytraAutoUtils.handleRawPressed(gameOptions.forwardKey, forwardSprintFlag);
      }

      if ((forwardSprintValue & 2) != 0) {
         ElytraAutoUtils.handleRawPressed(gameOptions.backKey, forwardSprintFlag);
      }

      if ((forwardSprintValue & 4) != 0) {
         ElytraAutoUtils.handleRawPressed(gameOptions.leftKey, forwardSprintFlag);
      }

      if ((forwardSprintValue & 8) != 0) {
         ElytraAutoUtils.handleRawPressed(gameOptions.rightKey, forwardSprintFlag);
      }

      if ((forwardSprintValue & 16) != 0) {
         ElytraAutoUtils.handleRawPressed(gameOptions.jumpKey, forwardSprintFlag);
      }

      if ((forwardSprintValue & 32) != 0) {
         ElytraAutoUtils.handleRawPressed(gameOptions.sneakKey, forwardSprintFlag);
      }

      if ((forwardSprintValue & 64) != 0) {
         ElytraAutoUtils.handleRawPressed(gameOptions.sprintKey, forwardSprintFlag);
      }

      if ((forwardSprintValue & 128) != 0) {
         ElytraAutoUtils.handleRawPressed(gameOptions.useKey, forwardSprintFlag);
      }
   }

   public void releaseKeys(int optionsValue) {
      GameOptions options = this.client.options;
      this.heldKeyMask &= ~optionsValue;
      this.previousKeyMask &= ~optionsValue;
      if (options != null) {
         setKeysPressed(options, optionsValue, false);
      }
   }

   public void aimAt(Vec3d vec, int eyeLimit) {
      ClientPlayerEntity player = this.client.player;
      if (player != null && vec != null) {
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(player.getEyePos(), vec);
         float eyeValue = eyeLimit >= 1 ? WrapDegreesUtils.getWobbleOffset(this.currentTick, 0.25F, 0) : 0.0F;
         float eyeValue2 = eyeLimit >= 1 ? WrapDegreesUtils.getWobbleOffset(this.currentTick, 0.25F, 1) : 0.0F;
         this.clampPitch.setTargetAngles(wrapDegrees[0] + eyeValue, wrapDegrees[1] + eyeValue2, eyeLimit);
      }
   }

   public static boolean isCrosshairOnBlock(MinecraftClient client, BlockPos pos2) {
      return pos2 != null && client.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK && blockHitResult.getBlockPos().equals(pos2);
   }

   public boolean isNearCell(BlockPos pos, double bottomCenterLimit) {
      return this.getHorizontalDistance(Vec3d.ofBottomCenter(pos)) <= bottomCenterLimit;
   }

   public void beginTick(long time) {
      this.currentTick = time;
      this.previousKeyMask = this.heldKeyMask;
      this.heldKeyMask = 0;
      this.clampPitch.clearTarget();
      ClientPlayerEntity player = this.client.player;
      if (player != null) {
         int selectedSlot = player.getInventory().getSelectedSlot();
         if (selectedSlot != this.selectedSlot) {
            this.selectedSlot = selectedSlot;
            this.lastSlotSwitchTick = time;
         }
      }
   }

   public double getHorizontalDistance(Vec3d vec) {
      ClientPlayerEntity player = this.client.player;
      return Math.hypot(vec.x - player.getX(), vec.z - player.getZ());
   }

   public void rotateTowardYaw(float clampPitchValue, float clampPitchValue2, int eyeLimit) {
      this.clampPitch.setTargetAngles(clampPitchValue, clampPitchValue2, eyeLimit);
   }

   public void pressKeys(int optionsValue) {
      GameOptions options = this.client.options;
      if (options != null) {
         this.heldKeyMask |= optionsValue;
         setKeysPressed(options, optionsValue, true);
      }
   }

   public void walkTowardCell(BlockPos pos, boolean groundWrapFlag, boolean groundWrapFlag2) {
      ClientPlayerEntity player = this.client.player;
      Vec3d bottomCenter = Vec3d.ofBottomCenter(pos);
      double hypot = this.getHorizontalDistance(bottomCenter);
      boolean groundWrapFlag3 = pos.getY() < this.getFeetBlock().getY();
      if (hypot < 0.6) {
         if (!groundWrapFlag3) {
            this.pressKeys(32);
         }

         this.moveTowardPoint(bottomCenter, false);
         if (groundWrapFlag && player.isOnGround()) {
            this.pressKeys(16);
         }
      } else {
         float degrees = this.getYawToward(bottomCenter);
         this.rotateTowardYaw(degrees, 12.0F, 0);
         if (Math.abs(MathHelper.wrapDegrees(degrees - player.getYaw())) < 35.0F) {
            this.pressKeys(1);
         } else {
            this.moveTowardPoint(bottomCenter, false);
         }

         if ((groundWrapFlag2 || !groundWrapFlag3 && !groundWrapFlag && hypot < 1.2) && !groundWrapFlag3) {
            this.pressKeys(32);
         }

         if (groundWrapFlag && player.isOnGround() && hypot < 1.5) {
            this.pressKeys(16);
         }
      }
   }

   public boolean isKeyHeld(int hunterBodyValue) {
      return (this.heldKeyMask & hunterBodyValue) != 0;
   }

   public BlockPos getFeetBlock() {
      ClientPlayerEntity player = this.client.player;
      return BlockPos.ofFloored(player.getX(), player.getY() + 0.6, player.getZ());
   }

   public Vec3d findVisibleFacePoint(Vec3d vec, BlockPos pos2, double offsetSubtractFactor) {
      ClientPlayerEntity player = this.client.player;
      Vec3d offsetSubtract = null;
      double offsetSubtractValue = Double.MAX_VALUE;

      for (Direction direction : Direction.values()) {
         Vec3d add = Vec3d.ofCenter(pos2).add(direction.getOffsetX() * 0.49, direction.getOffsetY() * 0.49, direction.getOffsetZ() * 0.49);
         Vec3d subtract = vec.subtract(add);
         if (!(subtract.x * direction.getOffsetX() + subtract.y * direction.getOffsetY() + subtract.z * direction.getOffsetZ() <= 0.0)) {
            double squaredDistanceTo = vec.squaredDistanceTo(add);
            if (!(squaredDistanceTo > offsetSubtractFactor * offsetSubtractFactor)
               && !(squaredDistanceTo >= offsetSubtractValue)
               && !(
                  this.client.world.raycast(new RaycastContext(vec, add, ShapeType.OUTLINE, FluidHandling.NONE, player)) instanceof BlockHitResult blockHitResult
                     && blockHitResult.getType() == Type.BLOCK
                     && !blockHitResult.getBlockPos().equals(pos2)
               )) {
               offsetSubtract = add;
               offsetSubtractValue = squaredDistanceTo;
            }
         }
      }

      return offsetSubtract;
   }

   public void selectHotbarSlot(int selectedSlot) {
      ClientPlayerEntity player = this.client.player;
      if (player != null && selectedSlot >= 0 && selectedSlot <= 8) {
         if (player.getInventory().getSelectedSlot() != selectedSlot) {
            player.getInventory().setSelectedSlot(selectedSlot);
            this.selectedSlot = selectedSlot;
            this.lastSlotSwitchTick = this.currentTick;
         }
      }
   }

   public void steerTowardOffset(double hungerTouchingValue, double x, boolean hungerTouchingFlag, boolean hungerTouchingFlag2) {
      ClientPlayerEntity player = this.client.player;
      if (player != null) {
         float degrees = (float)Math.toDegrees(Math.atan2(-hungerTouchingValue, x));
         this.rotateTowardYaw(degrees, 8.0F, 0);
         if (Math.abs(MathHelper.wrapDegrees(degrees - player.getYaw())) < 32.0F) {
            this.pressKeys(1);
            if (hungerTouchingFlag && player.getHungerManager().getFoodLevel() > 6) {
               this.pressKeys(64);
            }
         }

         if (hungerTouchingFlag2 && (player.isOnGround() || player.isTouchingWater())) {
            this.pressKeys(16);
         }
      }
   }

   public void endTick() {
      GameOptions options = this.client.options;
      int clampOptionsValue = this.heldKeyMask | this.previousKeyMask;
      this.heldKeyMask = 0;
      this.previousKeyMask = 0;
      if (options != null && clampOptionsValue != 0) {
         setKeysPressed(options, clampOptionsValue, false);
      }

      this.clampPitch.clearTargetAndOverride();
   }

   public boolean isLookingToward(Vec3d vec, float degrees) {
      ClientPlayerEntity player = this.client.player;
      Vec3d subtract = vec.subtract(player.getEyePos());
      double varLength = subtract.length();
      if (varLength < 1.0E-4) {
         return true;
      } else {
         Vec3d cosSin = MathClampUtils.getCosSin(player.getYaw(), player.getPitch());
         return cosSin.dotProduct(subtract) / varLength >= Math.cos(Math.toRadians(degrees));
      }
   }

   public float getYawToward(Vec3d vec) {
      ClientPlayerEntity player = this.client.player;
      return (float)Math.toDegrees(Math.atan2(-(vec.x - player.getX()), vec.z - player.getZ()));
   }

   public boolean isSlotSelectedAndSettled(int selectedSlotValue) {
      ClientPlayerEntity player = this.client.player;
      return player != null && player.getInventory().getSelectedSlot() == selectedSlotValue && this.currentTick - this.lastSlotSwitchTick >= 1L;
   }
}
