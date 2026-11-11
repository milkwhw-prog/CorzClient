package com.corz.client.inventory;

import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.ChargeAmountApi;
import com.corz.client.world.ClampMath;
import com.corz.client.world.MathClampUtils;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HumanAimRotator;
import com.corz.client.utils.BlockHitResolver;
import com.corz.client.utils.HandleTime;

public final class BlockPosHelperBlocked {
   private Predicate<BlockState> predicate;
   private int entityUnconfirmedValue;
   private BlockPos immutable;
   private boolean immutableActive;
   private int unconfirmedValue;
   private final int entityValue;
   private final ChargeAmountApi blocked;
   private BlockState defaultState;
   private boolean confirmBlockedFlag;
   private static final Direction[] immutableSideDirections = Direction.values();
   private long confirmTime;
   private boolean blockedWorldFlag;
   private int blockSlot = -1;
   private int sightLineValue;
   private String immutableText;
   public static final int UNUSED_ONE = 1;
   private int maxConfirmTicks;
   private final boolean blockPosActive;
   private BlockPos confirmUnconfirmedBlockPos;
   private int confirmCount;
   private BlockHitResult blockHitResult;
   private long entityTime;

   private BlockPosHelperBlocked.Result getText(String text) {
      this.updateConfirmUnconfirmed();
      this.immutableText = text;
      return BlockPosHelperBlocked.Result.FAILED;
   }

   public BlockPosHelperBlocked(ChargeAmountApi blocked, int entityValue, boolean flag) {
      this.blocked = blocked;
      this.entityValue = entityValue;
      this.blockPosActive = flag;
   }

   public String getText() {
      return this.immutableText;
   }

   private void updateConfirmUnconfirmed() {
      this.immutableActive = false;
      this.blockedWorldFlag = false;
      this.immutable = null;
      this.blockSlot = -1;
      this.defaultState = null;
      this.confirmUnconfirmedBlockPos = null;
      this.predicate = null;
      this.maxConfirmTicks = 0;
      this.blockHitResult = null;
      this.confirmBlockedFlag = false;
      this.confirmTime = 0L;
      this.confirmCount = 0;
      this.sightLineValue = 0;
      this.unconfirmedValue = 0;
      this.entityUnconfirmedValue = 0;
   }

   private void updateText() {
      this.updateConfirmUnconfirmed();
      this.immutableText = null;
   }

   public boolean canImmutableFlag(BlockPos pos, int value, BlockState defaultState, BlockPos pos2, int immutableBound) {
      if (!this.immutableActive && pos != null && defaultState != null && value >= 0 && value <= 8) {
         this.updateConfirmUnconfirmed();
         this.immutableText = null;
         this.blockedWorldFlag = false;
         this.immutable = pos.toImmutable();
         this.blockSlot = value;
         this.defaultState = defaultState;
         this.confirmUnconfirmedBlockPos = pos2 == null ? null : pos2.toImmutable();
         this.maxConfirmTicks = Math.max(1, immutableBound);
         this.immutableActive = true;
         return true;
      } else {
         return false;
      }
   }

   public boolean isHitBlock() {
      return this.immutableActive && this.blockHitResult != null;
   }

   public boolean isFlag() {
      return this.immutableActive;
   }

   public BlockPosHelperBlocked(ChargeAmountApi chargeAmountApi) {
      this(chargeAmountApi, Integer.MAX_VALUE);
   }

   public BlockPosHelperBlocked.Result getBlockedHit() {
      if (!this.immutableActive) {
         return BlockPosHelperBlocked.Result.IDLE;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         ClientWorld world = instance.world;
         if (player != null && world != null) {
            BlockState blockState = world.getBlockState(this.immutable);
            if (this.blockedWorldFlag ? !this.predicate.test(blockState) : !blockState.isOf(this.defaultState.getBlock())) {
               if (!this.blockedWorldFlag && !BlockPosUtilsRespawn.canReplaceableAir(this.immutable)) {
                  return this.getText("blocked");
               } else {
                  float blockedHitValue = this.blocked.getAmount();
                  float amountFloat = this.blocked.getAmountFloat();
                  long entityTime = this.blocked.getTime();
                  if (this.confirmBlockedFlag) {
                     if (entityTime - this.confirmTime <= this.maxConfirmTicks) {
                        this.blocked.handleBlock(blockedHitValue, amountFloat);
                        return BlockPosHelperBlocked.Result.WAITING;
                     }

                     if (this.confirmCount >= 1) {
                        return this.getText("no confirm");
                     }

                     this.confirmCount++;
                     this.confirmBlockedFlag = false;
                     this.blockHitResult = null;
                  }

                  double amountDouble = this.blocked.getAmountDouble();
                  Vec3d eyePos = player.getEyePos();
                  if (this.blockHitResult == null || !this.canImmutableBlocked(this.blockHitResult, eyePos, amountDouble)) {
                     this.blockHitResult = this.getImmutableSide(eyePos, amountDouble, blockedHitValue, amountFloat);
                     if (this.blockHitResult == null) {
                        if (++this.sightLineValue >= 3) {
                           return this.getText("no line of sight");
                        }

                        this.blocked.handleEyeAction(Vec3d.ofCenter(this.immutable), false);
                        return BlockPosHelperBlocked.Result.AIMING;
                     }
                  }

                  this.sightLineValue = 0;
                  if (!this.blockedWorldFlag && FloorMathUtilsBlock.canCenterBlast(this.blockHitResult.getBlockPos())) {
                     BlockHitResult blockHitResult = this.getImmutableSide(eyePos, amountDouble, blockedHitValue, amountFloat);
                     if (blockHitResult != null && !FloorMathUtilsBlock.canCenterBlast(blockHitResult.getBlockPos())) {
                        this.blockHitResult = blockHitResult;
                     }
                  }

                  if (!this.blockedWorldFlag && FloorMathUtilsBlock.canCenterBlast(this.blockHitResult.getBlockPos())) {
                     FloorMathUtilsBlock.handle(true);
                     this.blocked.handleEyeAction(this.blockHitResult.getPos(), false);
                     return BlockPosHelperBlocked.Result.WAITING;
                  } else if (this.canBlockHit(player)) {
                     if (++this.unconfirmedValue > 40) {
                        return this.getText("unconfirmed");
                     } else {
                        FloorMathUtilsBlock.handle(false);
                        this.blocked.handleEyeAction(this.blockHitResult.getPos(), false);
                        return BlockPosHelperBlocked.Result.WAITING;
                     }
                  } else if (!this.blockedWorldFlag && FloorMathUtilsBlock.canInterpolatorLerped(this.immutable, FloorMathUtilsBlock.getValueInt())) {
                     if (++this.entityUnconfirmedValue > this.entityValue) {
                        return this.getText("entity");
                     } else {
                        this.entityTime = entityTime;
                        FloorMathUtilsBlock.handle(true);
                        this.blocked.handleEyeAction(this.blockHitResult.getPos(), false);
                        return BlockPosHelperBlocked.Result.WAITING;
                     }
                  } else if (this.blocked.isFlag2()) {
                     return this.getBlockedHit(player, world, amountDouble, blockedHitValue, amountFloat, entityTime);
                  } else if (!this.blocked.canSelectedInventoryForInt(this.blockSlot)) {
                     this.blocked.handleEyeAction(this.blockHitResult.getPos(), false);
                     return BlockPosHelperBlocked.Result.WAITING;
                  } else {
                     Direction direction = this.blockedWorldFlag ? null : this.blockHitResult.getSide();
                     BlockHitResult eyeInstance = BlockHitResolver.findBlockHitOnFace(this.blockHitResult.getBlockPos(), direction, amountDouble, blockedHitValue, amountFloat);
                     if (eyeInstance == null) {
                        this.blocked.handleEyeAction(this.blockHitResult.getPos(), false);
                        return BlockPosHelperBlocked.Result.AIMING;
                     } else if (this.blocked.getAmount4() > 1.9) {
                        float[] blockedHitValues = BlockEntityUtils.getAmounts(eyePos, blockedHitValue, amountFloat, eyeInstance.getBlockPos(), direction, ClampMath.getSensitivityMouse());
                        if (blockedHitValues != null) {
                           this.blocked.handleBlock(blockedHitValues[0], blockedHitValues[1]);
                        } else {
                           this.blocked.handleBlock(blockedHitValue, amountFloat);
                        }

                        return BlockPosHelperBlocked.Result.WAITING;
                     } else {
                        HandleTime supplier = this.blocked.getSupplier();
                        long timeLong = this.blocked.getTimeLong();
                        if (this.blocked.isFlag()
                           && this.blocked.isLedgerHandswap()
                           && (supplier == null || supplier.canTimeFlag(entityTime) && !supplier.canAmountTime(blockedHitValue, amountFloat, timeLong))
                           && (this.blockedWorldFlag || world.canPlace(this.defaultState, this.immutable, ShapeContext.absent()))
                           && (!this.blockedWorldFlag || !this.blocked.isSprintSneak() && !player.shouldCancelInteraction())) {
                           BlockHitResolver.interactWithBlock(eyeInstance);
                           if (supplier != null) {
                              supplier.handle(timeLong, entityTime, blockedHitValue, amountFloat);
                           }

                           this.blocked.handleBlock(blockedHitValue, amountFloat);
                           this.confirmBlockedFlag = true;
                           this.confirmTime = entityTime;
                           return BlockPosHelperBlocked.Result.ACTED;
                        } else {
                           this.blocked.handleBlock(blockedHitValue, amountFloat);
                           return BlockPosHelperBlocked.Result.WAITING;
                        }
                     }
                  }
               }
            } else {
               this.updateText();
               return BlockPosHelperBlocked.Result.DONE;
            }
         } else {
            return this.getText("no world");
         }
      }
   }

   private BlockHitResult getImmutableSide(Vec3d vec, double immutableSideValue, float boxLengthValue, float amountFloat) {
      if (this.blockedWorldFlag) {
         return this.getImmutableFlag(BlockHitResolver.findNearestRotationHit(this.immutable, immutableSideValue, boxLengthValue, amountFloat), immutableSideDirections, null, immutableSideValue, boxLengthValue, amountFloat);
      } else {
         BlockHitResult blockHitResult = null;
         double immutableSideLimit = Double.MAX_VALUE;

         for (Direction direction : immutableSideDirections) {
            BlockPos offset = this.immutable.offset(direction);
            if (!offset.equals(this.confirmUnconfirmedBlockPos) && !BlockPosUtilsRespawn.canReplaceableAir(offset)) {
               Direction opposite = direction.getOpposite();
               if (BlockEntityUtils.canBlockState(offset, opposite)) {
                  BlockHitResult eyeInstance = BlockHitResolver.findBlockHitAmongFaces(offset, new Direction[]{opposite}, opposite, immutableSideValue, boxLengthValue, amountFloat);
                  if (eyeInstance != null) {
                     float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, eyeInstance.getPos());
                     double immutableSideValue4 = BoxLengthUtils.getAmount(boxLengthValue, amountFloat, wrapDegrees[0], wrapDegrees[1])
                        + (FloorMathUtilsBlock.canInstanceWorld(offset) ? 360.0 : 0.0)
                        + (FloorMathUtilsBlock.canCenterBlast(offset) ? 720.0 : 0.0);
                     if (immutableSideValue4 < immutableSideLimit) {
                        immutableSideLimit = immutableSideValue4;
                        blockHitResult = eyeInstance;
                     }
                  }
               }
            }
         }

         return blockHitResult == null ? null : this.getImmutableFlag(blockHitResult, new Direction[]{blockHitResult.getSide()}, blockHitResult.getSide(), immutableSideValue, boxLengthValue, amountFloat);
      }
   }

   private BlockPosHelperBlocked.Result getBlockedHit(ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, double blockedHitValue, float blockValue, float amountFloat, long confirmTime) {
      Direction direction = this.blockedWorldFlag ? null : this.blockHitResult.getSide();
      BlockHitResult eyeInstance = BlockHitResolver.findBlockHitOnFace(this.blockHitResult.getBlockPos(), direction, blockedHitValue, blockValue, amountFloat);
      if (eyeInstance == null) {
         this.blocked.getEyeAction(this.blockHitResult.getPos());
         return BlockPosHelperBlocked.Result.AIMING;
      } else {
         HandleTime supplier = this.blocked.getSupplier();
         long timeLong = this.blocked.getTimeLong();
         if (this.blocked.isFlag()
            && this.blocked.isLedgerHandswap()
            && (supplier == null || supplier.canTimeFlag(confirmTime))
            && (this.blockedWorldFlag || clientWorld.canPlace(this.defaultState, this.immutable, ShapeContext.absent()))
            && (!this.blockedWorldFlag || !this.blocked.isSprintSneak() && !clientPlayerEntity.shouldCancelInteraction())) {
            if (!this.blocked.canSelectedInventory(this.blockSlot)) {
               this.blocked.handleBlock(blockValue, amountFloat);
               return BlockPosHelperBlocked.Result.WAITING;
            } else {
               BlockHitResolver.interactWithBlock(eyeInstance);
               if (supplier != null) {
                  supplier.handleSupplierAmount(timeLong, confirmTime, blockValue, amountFloat, true);
               }

               this.blocked.handleBlock(blockValue, amountFloat);
               this.confirmBlockedFlag = true;
               this.confirmTime = confirmTime;
               return BlockPosHelperBlocked.Result.ACTED;
            }
         } else {
            this.blocked.handleBlock(blockValue, amountFloat);
            return BlockPosHelperBlocked.Result.WAITING;
         }
      }
   }

   private BlockHitResult getImmutableFlag(BlockHitResult blockHitResult, Direction[] directions, Direction direction, double immutableValue, float posValue, float amountFloat) {
      return this.blockPosActive && blockHitResult != null ? BlockHitResolver.chooseLessRotationHit(blockHitResult, directions, direction, immutableValue, posValue, amountFloat, this.immutable, HumanAimRotator.getMaxStepDegrees()) : blockHitResult;
   }

   public BlockPosHelperBlocked(ChargeAmountApi chargeAmountApi, int blockPosValue) {
      this(chargeAmountApi, blockPosValue, false);
   }

   public boolean isBlockedEntity() {
      return this.immutableActive && !this.blockedWorldFlag && this.entityUnconfirmedValue > 0 && this.blocked.getTime() - this.entityTime <= 1L;
   }

   public boolean canImmutableFlag(BlockPos pos, int value, Predicate<BlockState> predicate, int immutableBound) {
      if (!this.immutableActive && pos != null && predicate != null && value >= 0 && value <= 8) {
         this.updateConfirmUnconfirmed();
         this.immutableText = null;
         this.blockedWorldFlag = true;
         this.immutable = pos.toImmutable();
         this.blockSlot = value;
         this.predicate = predicate;
         this.maxConfirmTicks = Math.max(1, immutableBound);
         this.immutableActive = true;
         return true;
      } else {
         return false;
      }
   }

   private boolean canBlockHit(ClientPlayerEntity clientPlayerEntity) {
      if (this.blockHitResult == null) {
         return false;
      } else {
         return !this.blockedWorldFlag
            ? FloorMathUtilsBlock.canInstanceWorld(this.blockHitResult.getBlockPos())
            : FloorMathUtilsBlock.canInstanceWorld(this.immutable) && clientPlayerEntity.getInventory().getStack(this.blockSlot).getItem() instanceof BlockItem;
      }
   }

   private boolean canImmutableBlocked(BlockHitResult blockHitResult, Vec3d vec, double immutableBlockedFactor) {
      BlockPos blockPos = blockHitResult.getBlockPos();
      Direction side = blockHitResult.getSide();
      if (this.blockedWorldFlag) {
         if (!blockPos.equals(this.immutable)) {
            return false;
         }
      } else {
         if (!blockPos.offset(side).equals(this.immutable) || blockPos.equals(this.confirmUnconfirmedBlockPos)) {
            return false;
         }

         if (BlockPosUtilsRespawn.canReplaceableAir(blockPos) || !BlockEntityUtils.canBlockState(blockPos, side)) {
            return false;
         }
      }

      Vec3d pos = blockHitResult.getPos();
      if (vec.squaredDistanceTo(pos) > immutableBlockedFactor * immutableBlockedFactor) {
         return false;
      } else {
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, pos);
         return BlockHitResolver.findBlockHitOnFace(blockPos, this.blockedWorldFlag ? null : side, immutableBlockedFactor, wrapDegrees[0], wrapDegrees[1]) != null;
      }
   }

   public void updateTextImmutableText() {
      this.updateConfirmUnconfirmed();
      this.immutableText = null;
   }

   public int getBlockedHitImmutableActive() {
      if (this.immutableActive && this.blockHitResult != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player != null && instance.world != null) {
            float blockedHitValue = this.blocked.getAmount();
            float amountFloat = this.blocked.getAmountFloat();
            if (!this.blockedWorldFlag && FloorMathUtilsBlock.canCenterBlast(this.blockHitResult.getBlockPos())) {
               return 1;
            } else if (BlockHitResolver.findBlockHitOnFace(
                  this.blockHitResult.getBlockPos(), this.blockedWorldFlag ? null : this.blockHitResult.getSide(), this.blocked.getAmountDouble(), blockedHitValue, amountFloat
               )
               != null) {
               return 0;
            } else {
               float[] wrapDegrees = MathClampUtils.getWrapDegrees(player.getEyePos(), this.blockHitResult.getPos());
               return (int)Math.ceil(BoxLengthUtils.getAmount(blockedHitValue, amountFloat, wrapDegrees[0], wrapDegrees[1]) / 15.0);
            }
         } else {
            return Integer.MAX_VALUE;
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public static enum Result {
      IDLE,
      AIMING,
      WAITING,
      ACTED,
      DONE,
      FAILED;
   }
}
