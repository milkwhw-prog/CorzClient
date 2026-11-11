package com.corz.client.utils;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.WalkRouteFinder;
import com.corz.client.utils.ClampPitch;

public final class BlockWalker {
   private long tickCounter;
   private long lastPathRefreshTick;
   private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
   private double maxReachDistance = 2.5;
   private final ClampPitch clampPitch;
   private BlockPos targetPos;
   private Vec3d lastPlayerPos;
   private int stuckTicks;
   private int pathIndex;
   private List<BlockPos> pathNodes;

   public BlockPos getTargetPos() {
      return this.targetPos;
   }

   public void clearTarget() {
      this.targetPos = null;
      this.pathNodes = null;
      this.pathIndex = 0;
      this.stuckTicks = 0;
      this.releaseMovementKeys();
   }

   private List<BlockPos> computePath(BlockPos pos, BlockPos pos2) {
      try {
         return WalkRouteFinder.findPath(pos, pos2);
      } catch (Exception e) {
         return null;
      }
   }

   private boolean isGroundWithinDrop(BlockPos pos2, int downLimit) {
      for (int i = 1; i <= downLimit; i++) {
         if (this.isSolidCollision(pos2.down(i))) {
            return false;
         }
      }

      return true;
   }

   public BlockWalker.Status tickWalk() {
      this.tickCounter++;
      if (CLIENT.player != null && CLIENT.world != null && this.targetPos != null) {
         Vec3d entityPos = CLIENT.player.getEntityPos();
         Vec3d center = Vec3d.ofCenter(this.targetPos);
         double hypot = Math.hypot(center.x - entityPos.x, center.z - entityPos.z);
         double posValue = Math.abs(center.y - entityPos.y);
         if (hypot <= this.maxReachDistance && posValue <= 2.0) {
            this.releaseMovementKeys();
            return BlockWalker.Status.ARRIVED;
         } else {
            boolean posFlag = this.pathNodes == null || this.pathIndex >= this.pathNodes.size() || this.tickCounter - this.lastPathRefreshTick > 100L || this.stuckTicks == 30;
            if (posFlag && this.tickCounter - this.lastPathRefreshTick >= 20L) {
               this.lastPathRefreshTick = this.tickCounter;
               List items = this.computePath(CLIENT.player.getBlockPos(), this.targetPos);
               if (items != null && items.size() > 1) {
                  this.pathNodes = items;
                  this.pathIndex = 0;
               } else {
                  this.pathNodes = null;
               }
            }

            BlockPos posBlock = this.getNextWaypoint(entityPos);
            this.steerToward(posBlock != null ? posBlock : this.targetPos);
            if (this.lastPlayerPos != null) {
               double hypot2 = Math.hypot(entityPos.x - this.lastPlayerPos.x, entityPos.z - this.lastPlayerPos.z);
               if (hypot2 < 0.03) {
                  this.stuckTicks++;
               } else {
                  this.stuckTicks = 0;
               }
            }

            this.lastPlayerPos = entityPos;
            if (this.stuckTicks > 12 && this.stuckTicks < 20 && CLIENT.player.isOnGround()) {
               CLIENT.options.jumpKey.setPressed(true);
            }

            if (this.stuckTicks >= 90) {
               this.releaseMovementKeys();
               return BlockWalker.Status.STUCK;
            } else {
               return this.pathNodes == null && !this.hasTarget() ? BlockWalker.Status.NO_PATH : BlockWalker.Status.MOVING;
            }
         }
      } else {
         return BlockWalker.Status.IDLE;
      }
   }

   public BlockWalker(ClampPitch clampPitch) {
      this.lastPathRefreshTick = -4611686018427387904L;
      this.clampPitch = clampPitch;
   }

   private BlockPos getNextWaypoint(Vec3d vec) {
      if (this.pathNodes != null && !this.pathNodes.isEmpty()) {
         while (this.pathIndex < this.pathNodes.size() - 1) {
            BlockPos blockPos = this.pathNodes.get(this.pathIndex);
            double hypot = Math.hypot(blockPos.getX() + 0.5 - vec.x, blockPos.getZ() + 0.5 - vec.z);
            if (!(hypot <= 1.3)) {
               break;
            }

            this.pathIndex++;
         }

         return this.pathIndex < this.pathNodes.size() ? this.pathNodes.get(this.pathIndex) : this.targetPos;
      } else {
         return this.targetPos;
      }
   }

   private boolean isSolidCollision(BlockPos pos) {
      BlockState blockState = CLIENT.world.getBlockState(pos);
      return !blockState.isAir() && !blockState.isReplaceable() && !blockState.getCollisionShape(CLIENT.world, pos).isEmpty();
   }

   private boolean hasTarget() {
      return this.targetPos != null;
   }

   private float computePitchToward(Vec3d vec) {
      Vec3d eyePos = CLIENT.player.getEyePos();
      double deltaX = vec.x - eyePos.x;
      double deltaY = vec.y - eyePos.y;
      double deltaZ = vec.z - eyePos.z;
      double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
      return (float)Math.toDegrees(Math.atan2(-deltaY, distance));
   }

   private void releaseMovementKeys() {
      CLIENT.options.forwardKey.setPressed(false);
      CLIENT.options.jumpKey.setPressed(false);
      ElytraAutoUtils.handleRawPressed(CLIENT.options.sneakKey, false);
   }

   private void steerToward(BlockPos pos) {
      if (pos != null) {
         Vec3d center = Vec3d.ofCenter(pos);
         float degrees = this.computeYawToward(center);
         float clamp = MathHelper.clamp(this.computePitchToward(center), -25.0F, 25.0F);
         this.clampPitch.setTargetAngles(degrees, clamp, 0);
         float absWrapDegrees = Math.abs(MathHelper.wrapDegrees(degrees - CLIENT.player.getYaw()));
         if (!(absWrapDegrees >= 32.0F)) {
            BlockPos blockPos = CLIENT.player.getBlockPos();
            BlockPos offset = blockPos.offset(Direction.fromHorizontalDegrees(CLIENT.player.getYaw()));
            boolean pressedFlag = !this.isSolidCollision(offset.down()) && this.isGroundWithinDrop(offset, 4);
            boolean pressedFlag2 = offset.equals(pos) || offset.down().equals(pos);
            if (pressedFlag && !pressedFlag2) {
               ElytraAutoUtils.handleRawPressed(CLIENT.options.sneakKey, true);
            } else {
               CLIENT.options.forwardKey.setPressed(true);
               if (this.isSolidCollision(offset) && !this.isSolidCollision(offset.up()) && CLIENT.player.isOnGround()) {
                  CLIENT.options.jumpKey.setPressed(true);
               }

               if (pos.getY() > blockPos.getY() + 1 && CLIENT.player.isOnGround()) {
                  CLIENT.options.jumpKey.setPressed(true);
               }
            }
         }
      }
   }

   public void setTarget(BlockPos blockPos, double posBlockBound) {
      if (blockPos == null) {
         this.clearTarget();
      } else {
         if (!blockPos.equals(this.targetPos)) {
            this.targetPos = blockPos;
            this.pathNodes = null;
            this.pathIndex = 0;
            this.stuckTicks = 0;
         }

         this.maxReachDistance = Math.max(0.6, posBlockBound);
      }
   }

   private float computeYawToward(Vec3d vec) {
      double deltaX = vec.x - CLIENT.player.getX();
      double deltaZ = vec.z - CLIENT.player.getZ();
      return (float)Math.toDegrees(Math.atan2(-deltaX, deltaZ));
   }

   public static enum Status {
      IDLE,
      MOVING,
      ARRIVED,
      STUCK,
      NO_PATH;
   }
}
