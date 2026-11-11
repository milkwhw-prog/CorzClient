package com.corz.client.utils;

import com.corz.client.mixins.AccessorClientPlayerInteractionManager;
import com.corz.client.world.ClientInstance;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.BlockHitResolver;

public final class DigxDug {
   private static final Direction[] sidePosDirections = Direction.values();
   private String dig;
   private int lockedTargetId;
   private BlockPos lastBrokenPos;
   private BlockPos targetPos;
   private int noSideFoundCount;
   private int aimHoldBoundTicks;
   private float lockedPitch;
   private long holdStartTime;
   private boolean holdLogged;
   private String failureReason;
   private int holdTicks;
   public static final String BREAKING = "breaking";
   private final DigxDug.Io reach;
   private float lockedYaw;
   private long lastCancelTime;
   private int heldSlot;
   private long lastProgressTime;
   private Vec3d los;
   private Object heldStack;
   private long lastCompletedTime;
   private boolean cancelPending;
   private int abortCount;
   private DigxDug.Phase phase = DigxDug.Phase.IDLE;
   private Direction targetFace;

   private void finishBreak(long digxResetTime, String dig) {
      this.lastBrokenPos = this.targetPos;
      this.lastCompletedTime = digxResetTime;
      this.clearTarget();
      this.failureReason = null;
      this.dig = dig;
   }

   public long getLastCompletedTime() {
      return this.lastCompletedTime;
   }

   public static boolean isBreakProgressReady(float digxDugValue, float digxDugFactor, int digxDugBound) {
      return !(digxDugValue + digxDugFactor >= 1.0F) ? true : hasEnoughBreakProgress(digxDugFactor, digxDugBound);
   }

   public Direction getTargetFace() {
      return this.targetFace;
   }

   public void cancelBreakIfHolding() {
      boolean digxCancelFlag = this.phase == DigxDug.Phase.HOLD || this.cancelPending;
      long digxCancelTime = this.reach.getCurrentTime();
      this.clearTarget();
      this.cancelPending = false;
      if (digxCancelFlag) {
         if (this.reach.isBreakingBlock() && this.lastCancelTime != digxCancelTime && this.lastProgressTime != digxCancelTime && this.reach.isAimReadyOrBreaking()) {
            this.reach.cancelBlockBreaking();
            this.lastCancelTime = digxCancelTime;
            this.dig = "digx:cancel";
         } else if (this.reach.isBreakingBlock()) {
            this.cancelPending = true;
         }
      }
   }

   private DigxDug.Result failWithReason(String text) {
      this.clearTarget();
      this.failureReason = text;
      this.dig = "digx:" + text;
      return DigxDug.Result.FAILED;
   }

   public BlockPos getTargetPos() {
      return this.targetPos;
   }

   public String consumeStatusMessage() {
      String dig = this.dig;
      this.dig = null;
      return dig;
   }

   public boolean hasActiveTarget() {
      return this.phase != DigxDug.Phase.IDLE;
   }

   public int getHoldTicks() {
      return this.holdTicks;
   }

   public Vec3d getLos() {
      return this.los;
   }

   public long getLastProgressTime() {
      return this.lastProgressTime;
   }

   public DigxDug(DigxDug.Io reach) {
      this.lockedTargetId = -1;
      this.heldSlot = -1;
      this.holdStartTime = Long.MIN_VALUE;
      this.lastCancelTime = Long.MIN_VALUE;
      this.lastProgressTime = Long.MIN_VALUE;
      this.lastCompletedTime = Long.MIN_VALUE;
      this.reach = reach;
   }

   public DigxDug.Result advanceBreak() {
      long digxCancelTime = this.reach.getCurrentTime();
      if (this.cancelPending) {
         if (this.reach.isBreakingBlock()) {
            if (this.lastCancelTime != digxCancelTime && this.lastProgressTime != digxCancelTime && this.reach.isAimReadyOrBreaking()) {
               this.reach.cancelBlockBreaking();
               this.cancelPending = false;
               this.lastCancelTime = digxCancelTime;
               this.dig = "digx:cancel";
               return DigxDug.Result.ABORTED;
            }

            return DigxDug.Result.WAITING;
         }

         this.cancelPending = false;
      }

      if (this.phase == DigxDug.Phase.IDLE) {
         return DigxDug.Result.IDLE;
      } else if (!this.reach.isWorldReady()) {
         return this.failWithReason("world");
      } else if (this.reach.isAirOrLiquid(this.targetPos)) {
         this.finishBreak(digxCancelTime, this.phase == DigxDug.Phase.HOLD ? "dug" : "gone");
         return DigxDug.Result.DONE;
      } else {
         return this.phase == DigxDug.Phase.HOLD ? this.advanceHolding(digxCancelTime) : this.advanceAiming(digxCancelTime);
      }
   }

   public static boolean hasEnoughBreakProgress(float digxDugFactor, int digxDugBound) {
      return digxDugFactor * (Math.max(0, digxDugBound) + 1) >= 0.80499995F;
   }

   public void resetBreakState() {
      boolean digxResetFlag = this.phase == DigxDug.Phase.HOLD || this.cancelPending;
      this.clearTarget();
      this.cancelPending = false;
      if (digxResetFlag && this.reach.isWorldReady() && this.reach.isBreakingBlock()) {
         this.reach.cancelBlockBreaking();
         this.dig = "digx:reset";
      }

      this.lastCancelTime = Long.MIN_VALUE;
      this.lastProgressTime = Long.MIN_VALUE;
      this.lastCompletedTime = Long.MIN_VALUE;
      this.lastBrokenPos = null;
   }

   private DigxDug.Result advanceHolding(long digxCancelTime) {
      this.reach.requestBlockRotation(this.lockedYaw, this.lockedPitch);
      this.holdTicks++;
      String lostReachText = null;
      if (!this.reach.isBreakingBlock()) {
         lostReachText = "lost";
      } else if (this.reach.getSelectedSlot() != this.heldSlot || !this.reach.isSameAsMainHandStack(this.heldStack)) {
         lostReachText = "stack";
      } else if (this.reach.isPlayerUsingItem()) {
         lostReachText = "using";
      } else if (this.reach.getEyeDistanceToBlock(this.targetPos) > 4.5) {
         lostReachText = "reach";
      } else if (!this.reach.canHitFaceWithRotation(this.targetPos, this.targetFace, this.reach.getCurrentYaw(), this.reach.getCurrentPitch())) {
         lostReachText = "ray";
      } else if (this.holdTicks > this.aimHoldBoundTicks + 20) {
         lostReachText = "slow";
      }

      if (lostReachText != null) {
         if ("lost".equals(lostReachText)) {
            this.restartAiming("lost");
            return DigxDug.Result.WAITING;
         } else if (this.lastProgressTime != digxCancelTime && this.reach.isAimReadyOrBreaking()) {
            this.reach.cancelBlockBreaking();
            this.lastCancelTime = digxCancelTime;
            this.restartAiming(lostReachText);
            return ++this.abortCount >= 3 ? this.failWithReason(lostReachText) : DigxDug.Result.ABORTED;
         } else {
            return DigxDug.Result.WAITING;
         }
      } else if (!this.reach.isAimReadyOrBreaking()) {
         return DigxDug.Result.WAITING;
      } else if (!this.reach.isReadyToFinishBreak(this.targetPos, (int)Math.min(2147483647L, digxCancelTime - this.holdStartTime))) {
         if (!this.holdLogged) {
            this.holdLogged = true;
            this.dig = "digh";
         }

         return DigxDug.Result.HOLDING;
      } else {
         this.reach.continueBreakingBlock(this.targetPos, this.targetFace);
         this.lastProgressTime = digxCancelTime;
         if (!this.reach.isAirOrLiquid(this.targetPos) && this.reach.isBreakingBlock()) {
            return DigxDug.Result.HOLDING;
         } else {
            this.finishBreak(digxCancelTime, "dug");
            return DigxDug.Result.DONE;
         }
      }
   }

   public BlockPos getLastBrokenPos() {
      return this.lastBrokenPos;
   }

   public boolean tryStartTarget(BlockPos pos, int value, int aimImmutableBound) {
      if (this.phase == DigxDug.Phase.IDLE && pos != null) {
         this.clearTarget();
         this.failureReason = null;
         this.targetPos = pos.toImmutable();
         this.lockedTargetId = value;
         this.aimHoldBoundTicks = Math.max(0, aimImmutableBound);
         this.phase = DigxDug.Phase.AIM;
         return true;
      } else {
         return false;
      }
   }

   public boolean isHoldingOrCancelling() {
      return this.phase == DigxDug.Phase.HOLD || this.cancelPending;
   }

   public int getLockedTargetId() {
      return this.lockedTargetId;
   }

   private void restartAiming(String lostAimText) {
      this.phase = DigxDug.Phase.AIM;
      this.holdLogged = false;
      this.targetFace = null;
      this.heldStack = null;
      this.heldSlot = -1;
      this.holdTicks = 0;
      this.dig = "digx:" + lostAimText;
   }

   public int getAimHoldBoundTicks() {
      return this.aimHoldBoundTicks;
   }

   public String getFailureReason() {
      return this.failureReason;
   }

   public static DigxDug create(ClientInstance clientInstance, HandswapGate ledgerHandswap) {
      return new DigxDug(new DigxDug.Mc(clientInstance, ledgerHandswap));
   }

   private DigxDug.Result advanceAiming(long digxCancelTime) {
      if (this.reach.isBreakingBlock()) {
         if (this.lastCancelTime != digxCancelTime && this.lastProgressTime != digxCancelTime && this.reach.isAimReadyOrBreaking()) {
            this.reach.cancelBlockBreaking();
            this.lastCancelTime = digxCancelTime;
            this.dig = "digx:stale";
            return DigxDug.Result.ABORTED;
         } else {
            return DigxDug.Result.WAITING;
         }
      } else if (this.lastCancelTime == digxCancelTime || this.lastProgressTime == digxCancelTime) {
         return DigxDug.Result.WAITING;
      } else if (this.reach.isPlayerUsingItem()) {
         return DigxDug.Result.WAITING;
      } else if (this.reach.isBlockUnderPlayerFeet(this.targetPos)) {
         return this.failWithReason("support");
      } else if (this.reach.getEyeDistanceToBlock(this.targetPos) > 4.5) {
         return this.failWithReason("reach");
      } else {
         float lostAmount = this.reach.getCurrentYaw();
         float currentPitch = this.reach.getCurrentPitch();
         if (this.targetFace == null || !this.reach.canHitFaceWithRotation(this.targetPos, this.targetFace, lostAmount, currentPitch)) {
            DigxDug.Aim sidePos = this.reach.findVisibleFace(this.targetPos, lostAmount, currentPitch);
            if (sidePos == null) {
               if (++this.noSideFoundCount >= 6) {
                  return this.failWithReason("los");
               }

               if (this.los != null) {
                  this.reach.aimTowardPoint(this.los);
               }

               return DigxDug.Result.AIMING;
            }

            this.targetFace = sidePos.face();
            this.los = sidePos.point();
         }

         this.noSideFoundCount = 0;
         if (!this.reach.canHitFaceWithRotation(this.targetPos, this.targetFace, lostAmount, currentPitch)) {
            this.reach.aimTowardPoint(this.los);
            return DigxDug.Result.AIMING;
         } else if (!this.reach.isPlayerOnGround() && this.aimHoldBoundTicks > 0) {
            this.reach.requestBlockRotation(lostAmount, currentPitch);
            return DigxDug.Result.WAITING;
         } else if (!this.reach.isAimReady()) {
            this.reach.requestBlockRotation(lostAmount, currentPitch);
            return DigxDug.Result.WAITING;
         } else if (this.lockedTargetId >= 0 && !this.reach.canSelectSlot(this.lockedTargetId)) {
            this.reach.requestBlockRotation(lostAmount, currentPitch);
            return DigxDug.Result.WAITING;
         } else {
            this.reach.continueBreakingBlock(this.targetPos, this.targetFace);
            this.lastProgressTime = digxCancelTime;
            this.lockedYaw = lostAmount;
            this.lockedPitch = currentPitch;
            this.reach.requestBlockRotation(lostAmount, currentPitch);
            if (this.reach.isAirOrLiquid(this.targetPos)) {
               this.finishBreak(digxCancelTime, "dug");
               return DigxDug.Result.DONE;
            } else if (!this.reach.isBreakingBlock()) {
               return DigxDug.Result.WAITING;
            } else {
               this.phase = DigxDug.Phase.HOLD;
               this.holdStartTime = digxCancelTime;
               this.heldStack = this.reach.getHandStack();
               this.heldSlot = this.reach.getSelectedSlot();
               this.holdTicks = 0;
               this.dig = "dig";
               return DigxDug.Result.STARTED;
            }
         }
      }
   }

   public DigxDug.Phase getPhase() {
      return this.phase;
   }

   private void clearTarget() {
      this.phase = DigxDug.Phase.IDLE;
      this.targetPos = null;
      this.lockedTargetId = -1;
      this.aimHoldBoundTicks = 0;
      this.targetFace = null;
      this.los = null;
      this.heldStack = null;
      this.heldSlot = -1;
      this.holdStartTime = Long.MIN_VALUE;
      this.holdTicks = 0;
      this.noSideFoundCount = 0;
      this.abortCount = 0;
      this.holdLogged = false;
   }

   public record Aim(Direction face, Vec3d point) {
   }

   public interface Io {
      void requestBlockRotation(float clientValue, float currentPitch);

      DigxDug.Aim findVisibleFace(BlockPos pos, float sidePosValue, float currentPitch);

      boolean isPlayerUsingItem();

      boolean isSameAsMainHandStack(Object obj);

      long getCurrentTime();

      boolean canHitFaceWithRotation(BlockPos pos, Direction direction, float digxDugValue, float currentPitch);

      boolean isAirOrLiquid(BlockPos pos);

      void cancelBlockBreaking();

      float getCurrentYaw();

      boolean canSelectSlot(int clientValue);

      float[] aimTowardPoint(Vec3d vec);

      boolean isBlockUnderPlayerFeet(BlockPos pos);

      int getSelectedSlot();

      double getEyeDistanceToBlock(BlockPos pos);

      boolean isBreakingBlock();

      boolean isAimReady();

      Object getHandStack();

      boolean isAimReadyOrBreaking();

      boolean isWorldReady();

      float getCurrentPitch();

      boolean isPlayerOnGround();

      default boolean isReadyToFinishBreak(BlockPos pos2, int breakingCalcValue) {
         return true;
      }

      boolean continueBreakingBlock(BlockPos pos, Direction direction);
   }

   static final class Mc implements DigxDug.Io {
      private final ClientInstance clientInstance;
      private final HandswapGate breaking;

      @Override
      public void requestBlockRotation(float clientValue, float routedPartialValue) {
         this.clientInstance.handleBlock(clientValue, routedPartialValue);
      }

      @Override
      public boolean isBlockUnderPlayerFeet(BlockPos pos2) {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         if (player != null && pos2 != null) {
            Vec3d entityPos = player.getEntityPos();
            int entityValue = (int)Math.floor(entityPos.y + 1.0E-4);
            if (pos2.getY() != entityValue - 1) {
               return false;
            } else {
               int floor2 = (int)Math.floor(entityPos.x - 0.31);
               int entityLimit = (int)Math.floor(entityPos.x + 0.31);
               int floor3 = (int)Math.floor(entityPos.z - 0.31);
               int entityLimit2 = (int)Math.floor(entityPos.z + 0.31);
               return pos2.getX() >= floor2 && pos2.getX() <= entityLimit && pos2.getZ() >= floor3 && pos2.getZ() <= entityLimit2;
            }
         } else {
            return false;
         }
      }

      @Override
      public boolean canHitFaceWithRotation(BlockPos pos2, Direction direction, float digxDugValue, float amountFloat) {
         return BlockHitResolver.findBlockHitOnFace(pos2, direction, 4.5, digxDugValue, amountFloat) != null;
      }

      @Override
      public float getCurrentYaw() {
         return this.clientInstance.getAmount();
      }

      @Override
      public int getSelectedSlot() {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         return player == null ? -1 : player.getInventory().getSelectedSlot();
      }

      @Override
      public Object getHandStack() {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         return player == null ? ItemStack.EMPTY : player.getMainHandStack().copy();
      }

      @Override
      public DigxDug.Aim findVisibleFace(BlockPos pos2, float sidePosValue, float amountFloat) {
         BlockHitResult eyeInstance = BlockHitResolver.findBlockHitAmongFaces(pos2, DigxDug.sidePosDirections, null, 4.5, sidePosValue, amountFloat);
         return eyeInstance == null ? null : new DigxDug.Aim(eyeInstance.getSide(), eyeInstance.getPos());
      }

      @Override
      public double getEyeDistanceToBlock(BlockPos pos2) {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         if (player != null && pos2 != null) {
            Vec3d eyePos = player.getEyePos();
            double eyeFactor = Math.max(0.0, Math.max(pos2.getX() - eyePos.x, eyePos.x - (pos2.getX() + 1.0)));
            double max2 = Math.max(0.0, Math.max(pos2.getY() - eyePos.y, eyePos.y - (pos2.getY() + 1.0)));
            double max3 = Math.max(0.0, Math.max(pos2.getZ() - eyePos.z, eyePos.z - (pos2.getZ() + 1.0)));
            return Math.sqrt(eyeFactor * eyeFactor + max2 * max2 + max3 * max3);
         } else {
            return Double.MAX_VALUE;
         }
      }

      @Override
      public boolean isBreakingBlock() {
         MinecraftClient instance = MinecraftClient.getInstance();
         return instance.interactionManager != null && instance.interactionManager.isBreakingBlock();
      }

      @Override
      public float[] aimTowardPoint(Vec3d vec) {
         return this.clientInstance.getEyeAction(vec);
      }

      @Override
      public boolean isWorldReady() {
         MinecraftClient instance = MinecraftClient.getInstance();
         return instance.player != null && instance.world != null && instance.interactionManager != null;
      }

      @Override
      public float getCurrentPitch() {
         return this.clientInstance.getAmountFloat();
      }

      @Override
      public boolean canSelectSlot(int clientValue) {
         return this.clientInstance.canSelectedInventory(clientValue);
      }

      @Override
      public boolean isAirOrLiquid(BlockPos pos2) {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance.world != null && pos2 != null) {
            BlockState blockState = instance.world.getBlockState(pos2);
            return blockState.isAir() || blockState.isLiquid();
         } else {
            return true;
         }
      }

      @Override
      public boolean isAimReadyOrBreaking() {
         return this.clientInstance.isFlag() && (this.breaking.isSwitchAllowed() || "breaking".equals(this.breaking.getBlockReason()));
      }

      @Override
      public boolean isPlayerOnGround() {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         return player != null && player.isOnGround();
      }

      @Override
      public long getCurrentTime() {
         return this.clientInstance.getTime();
      }

      @Override
      public void cancelBlockBreaking() {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance.interactionManager != null) {
            try (HandswapGate.SwitchHandle handler = this.breaking.acquireHandle()) {
               instance.interactionManager.cancelBlockBreaking();
            }
         }
      }

      @Override
      public boolean isSameAsMainHandStack(Object obj) {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         return player != null && obj instanceof ItemStack itemStack ? ItemStack.areItemsAndComponentsEqual(itemStack, player.getMainHandStack()) : false;
      }

      @Override
      public boolean isPlayerUsingItem() {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         return player != null && player.isUsingItem();
      }

      Mc(ClientInstance clientInstance, HandswapGate breaking) {
         this.clientInstance = clientInstance;
         this.breaking = breaking;
      }

      @Override
      public boolean isReadyToFinishBreak(BlockPos pos2, int breakingCalcValue) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player != null && instance.world != null && instance.interactionManager != null && pos2 != null) {
            float blockBreakingDelta = instance.world.getBlockState(pos2).calcBlockBreakingDelta(player, instance.world, pos2);
            float corzBreakingProgress = ((AccessorClientPlayerInteractionManager)instance.interactionManager).corzBreakingProgress();
            return DigxDug.isBreakProgressReady(corzBreakingProgress, blockBreakingDelta, breakingCalcValue);
         } else {
            return true;
         }
      }

      @Override
      public boolean continueBreakingBlock(BlockPos pos2, Direction direction) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player != null && instance.interactionManager != null) {
            try (HandswapGate.SwitchHandle handler = this.breaking.acquireHandle()) {
               if (instance.interactionManager.updateBlockBreakingProgress(pos2, direction)) {
                  player.swingHand(Hand.MAIN_HAND);
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }

      @Override
      public boolean isAimReady() {
         return this.clientInstance.isFlag() && this.breaking.isSwitchAllowed();
      }
   }

   public static enum Phase {
      IDLE,
      AIM,
      HOLD;
   }

   public static enum Result {
      IDLE,
      AIMING,
      WAITING,
      STARTED,
      HOLDING,
      ABORTED,
      DONE,
      FAILED;
   }
}
