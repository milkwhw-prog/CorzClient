package com.corz.client.entity;

import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.BlockEntityUtils.Predicted;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.PosBlockUtilsBonus.Armor;
import com.corz.client.world.PosBlockUtilsBonus.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BonusLevelData;
import com.corz.client.utils.GlowstoneTimeout;
import com.corz.client.utils.RefBareData;

public final class PosBlockUtilsSelf {
   private static final Direction[] directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private static final Direction[] livingVecDirections = Direction.values();
   private static final double[] raycastTable = new double[]{0.5, 0.25, 0.75};
   static final int[][] flooredOffsetInts = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
   static final int[][] posBlockInts = new int[][]{{2, 1}, {1, 2}, {2, 2}};

   private static boolean canFlag(Vec3d vec, BlockPos pos2, Direction direction) {
      return switch (direction) {
         case UP -> vec.y > pos2.getY() + 1.0;
         case DOWN -> vec.y < pos2.getY();
         case NORTH -> vec.z < pos2.getZ();
         case SOUTH -> vec.z > pos2.getZ() + 1.0;
         case WEST -> vec.x < pos2.getX();
         case EAST -> vec.x > pos2.getX() + 1.0;
      };
   }

   static PosBlockUtilsSelf.Plan getPosBlockData(PosBlockUtilsSelf.Plan plan, int posBlockValue) {
      return getSelfAnchor(plan, posBlockValue, 3.0);
   }

   static boolean canFlag(boolean posBlockFlag, boolean posBlockFlag2, boolean posBlockFlag3, double posBlockValue) {
      return !posBlockFlag && posBlockFlag2 && !posBlockFlag3 && !(posBlockValue <= 60.0);
   }

   static double getAmount(double posBlockValue, boolean posBlockFlag) {
      return posBlockFlag ? posBlockValue : posBlockValue - 2.0;
   }

   public static PosBlockUtilsSelf.Plan getForcedImmutable(PosBlockUtilsSelf.Request request) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      ClientWorld world = instance.world;
      if (request == null || request.target() == null || request.predicted() == null || player == null || world == null) {
         return null;
      } else if (!BlockPosUtilsRespawn.isRegistryWorld()) {
         return null;
      } else {
         PosBlockUtilsSelf.Scan scan = new PosBlockUtilsSelf.Scan(request, player, world);
         if (request.forcedCell() != null) {
            return scan.getLivingVec(request.forcedCell().toImmutable(), true);
         } else {
            for (BlockPos blockPos : scan.getFlooredOffset()) {
               if (scan.livingVecAttemptCount >= 10) {
                  break;
               }

               scan.handleAmountPos(blockPos);
            }

            return scan.posBlockData;
         }
      }
   }

   static boolean canFlag(boolean posBlockFlag, int posBlockLimit, int posBlockValue, boolean posBlockFlag2, float posBlockLimit2) {
      return posBlockFlag && posBlockLimit >= posBlockValue + 1 && (!posBlockFlag2 || posBlockLimit2 > 1.0F);
   }

   static PosBlockUtilsSelf.Plan getSelfAnchor(PosBlockUtilsSelf.Plan plan, int selfAnchorLimit, double selfAnchorValue) {
      return plan != null && selfAnchorLimit > 0
         ? new PosBlockUtilsSelf.Plan(
            plan.anchor(),
            plan.cover(),
            plan.placeAnchor(),
            plan.charges(),
            plan.targetDamage(),
            plan.selfCost(),
            plan.selfFull(),
            plan.needsTotem(),
            plan.kills(),
            plan.clicks(),
            plan.prepTicks(),
            plan.value() + getAmount(selfAnchorLimit, selfAnchorValue),
            plan.turn(),
            plan.steady(),
            selfAnchorLimit,
            plan.knock()
         )
         : plan;
   }

   private PosBlockUtilsSelf() {
   }

   static double getAmount(int posBlockBound, double posBlockFactor) {
      return posBlockBound <= 0 ? 0.0 : posBlockFactor * Math.min(posBlockBound, 3) / 3.0;
   }

   static int getValue(boolean posBlockFlag, boolean posBlockFlag2, int posBlockLimit, double posBlockValue) {
      return getValue(posBlockValue, !posBlockFlag && !posBlockFlag2 && posBlockLimit > 0 ? 20.0 : 15.0);
   }

   static Vec3d getVec3d(Vec3d vec, Vec3d vec2, boolean vec3dFlag) {
      if (vec != null && vec2 != null && (!vec3dFlag || canFlagForDouble(vec2.x, vec2.z))) {
         double vec3dValue = vec3dFlag ? 0.546 : 0.91;
         double x = vec.x;
         double y = vec.y;
         double z = vec.z;
         double x2 = vec2.x;
         double y2 = vec2.y;
         double z2 = vec2.z;

         for (int i = 0; i < 3; i++) {
            x += x2;
            z += z2;
            x2 *= vec3dValue;
            z2 *= vec3dValue;
            if (!vec3dFlag) {
               y += y2;
               y2 = (y2 - 0.08) * 0.98;
            }
         }

         return new Vec3d(x, Math.max(y, vec.y - 1.25), z);
      } else {
         return null;
      }
   }

   public static boolean canRaycastAmounts(Vec3d vec, double raycastFactor, BlockPos pos, BlockPos pos2, Box box, Box box2) {
      if (vec != null && pos != null) {
         Box box3 = pos2 == null ? null : BlockEntityUtils.getBoxForBlockPos(pos2);
         double raycastValue = raycastFactor * raycastFactor;

         for (Direction direction : livingVecDirections) {
            if (canFlag(vec, pos, direction) && (pos2 == null || !pos.offset(direction).equals(pos2))) {
               for (double raycastTableItem : raycastTable) {
                  for (double vec3dValue2 : raycastTable) {
                     Vec3d vec3d = getVec3d(pos, direction, raycastTableItem, vec3dValue2);
                     if (!(vec.squaredDistanceTo(vec3d) > raycastValue)
                        && (box3 == null || !box3.raycast(vec, vec3d).isPresent())
                        && (box == null || !box.raycast(vec, vec3d).isPresent())
                        && (box2 == null || !box2.raycast(vec, vec3d).isPresent())
                        && BlockEntityUtils.canSubtractRaycast(vec, vec3d)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   static boolean canFlag(boolean posBlockFlag, boolean posBlockFlag2, boolean posBlockFlag3, boolean posBlockFlag4) {
      return !posBlockFlag && posBlockFlag2 && posBlockFlag4;
   }

   static boolean canFlag(boolean posBlockFlag, int posBlockLimit, int posBlockValue, boolean posBlockFlag2, float posBlockLimit2, int posBlockValue2, int posBlockLimit3) {
      boolean posBlockFlag3 = posBlockLimit3 >= posBlockValue2 + 1;
      return posBlockFlag && posBlockLimit >= posBlockValue + 1 && (!posBlockFlag2 || posBlockFlag3 && posBlockLimit2 > 1.0F);
   }

   public static int getValue(double posBlockValue) {
      return getValue(posBlockValue, 15.0);
   }

   static boolean canDamageTarget(RefBareData refBareData, PosBlockUtilsSelf.Plan plan, PosBlockUtilsSelf.Plan plan2, float damageTargetValue) {
      if (refBareData == null || !refBareData.on() || plan == null || plan2 == null) {
         return false;
      } else if (plan.kills() && !plan2.kills()) {
         return false;
      } else {
         boolean damageTargetFlag = plan2.targetDamage() >= damageTargetValue;
         if (!refBareData.canFlag(plan2.targetDamage(), plan2.knock(), damageTargetFlag)) {
            return false;
         } else {
            boolean damageTargetFlag2 = plan.needsTotem() && !plan2.needsTotem();
            if (plan.targetDamage() >= damageTargetValue && !damageTargetFlag && !damageTargetFlag2) {
               return false;
            } else {
               return damageTargetFlag2 ? true : plan2.selfFull() + 2.0F <= plan.selfFull() && plan2.targetDamage() >= 0.7F * plan.targetDamage();
            }
         }
      }
   }

   static double getAmount(boolean posBlockFlag) {
      return posBlockFlag ? 4.0 : 1.0;
   }

   static double getAmount(double posBlockValue, double posBlockValue2, int posBlockValue3, int posBlockValue4, int posBlockValue5, int posBlockValue6, double posBlockFactor, double posBlockFactor2) {
      int posBlockLimit = posBlockValue5 - posBlockValue3;
      double posBlockFactor3 = posBlockValue4 + 0.5 - posBlockValue;
      double posBlockFactor4 = posBlockValue6 + 0.5 - posBlockValue2;
      double distance = Math.sqrt(posBlockFactor3 * posBlockFactor3 + posBlockFactor4 * posBlockFactor4);
      if (posBlockLimit >= 0 && posBlockLimit <= 1 && distance <= 1.3) {
         double sqrt2 = Math.sqrt(posBlockFactor * posBlockFactor + posBlockFactor2 * posBlockFactor2);
         return canFlagForDouble(posBlockFactor, posBlockFactor2) && !(sqrt2 > 2.0) && !(distance < 1.0E-6)
            ? 1.5 + 0.6 * (1.0 - Math.min(1.0, Math.abs(posBlockFactor3 * posBlockFactor + posBlockFactor4 * posBlockFactor2) / (distance * sqrt2)))
            : 1.5;
      } else {
         return 0.0;
      }
   }

   static boolean canFlag(double posBlockLimit, double posBlockValue) {
      return Double.isFinite(posBlockLimit) && Double.isFinite(posBlockValue) && posBlockLimit > posBlockValue - 0.5;
   }

   static boolean canFlagForDouble(double posBlockFactor, double posBlockFactor2) {
      return Math.sqrt(posBlockFactor * posBlockFactor + posBlockFactor2 * posBlockFactor2) * 2.0 >= 0.1;
   }

   static boolean canFlag3(double posBlockValue, double posBlockLimit) {
      return posBlockValue <= 1.3 && posBlockLimit >= posBlockValue + 2.0;
   }

   private static Vec3d getVec3d(BlockPos pos2, Direction direction, double vec3dValue, double vec3dValue2) {
      double x = pos2.getX();
      double y = pos2.getY();
      double z = pos2.getZ();

      return switch (direction) {
         case UP -> new Vec3d(x + vec3dValue, y + 1.0, z + vec3dValue2);
         case DOWN -> new Vec3d(x + vec3dValue, y, z + vec3dValue2);
         case NORTH -> new Vec3d(x + vec3dValue, y + vec3dValue2, z);
         case SOUTH -> new Vec3d(x + vec3dValue, y + vec3dValue2, z + 1.0);
         case WEST -> new Vec3d(x, y + vec3dValue2, z + vec3dValue);
         case EAST -> new Vec3d(x + 1.0, y + vec3dValue2, z + vec3dValue);
      };
   }

   static int getValue(double posBlockValue, double posBlockLimit) {
      return posBlockValue <= posBlockLimit ? 0 : (int)Math.ceil(posBlockValue / posBlockLimit) - 1;
   }

   public static int getValue(int posBlockBound) {
      return Math.max(0, posBlockBound) + 2;
   }

   static double getAmount(int posBlockValue) {
      return getAmount(posBlockValue, 3.0);
   }

   static boolean canFlag(float[] posBlockValues) {
      if (posBlockValues != null && posBlockValues.length != 0) {
         double posBlockTotal = 3.5;

         for (int i = 0; i < posBlockValues.length; i++) {
            if (!Float.isNaN(posBlockValues[i])) {
               posBlockTotal -= (posBlockValues[i] + 0.3) * 0.3;
            }

            if (posBlockTotal <= 0.0) {
               return false;
            }

            if (i == posBlockValues.length - 1) {
               return true;
            }

            posBlockTotal -= 0.225;
         }

         return false;
      } else {
         return false;
      }
   }

   private record Option(PosBlockUtilsSelf.Plan plan, boolean safe) {
   }

   public record Plan(
      BlockPos anchor,
      BlockPos cover,
      boolean placeAnchor,
      int charges,
      float targetDamage,
      float selfCost,
      float selfFull,
      boolean needsTotem,
      boolean kills,
      int clicks,
      int prepTicks,
      double value,
      double turn,
      boolean steady,
      int dig,
      float knock
   ) {
      public Plan(
         BlockPos targetDamage,
         BlockPos charges,
         boolean clicks,
         int steady,
         float cover,
         float needsTotem,
         float turn,
         boolean selfCost,
         boolean value,
         int kills,
         int selfFull,
         double anchor,
         double placeAnchor,
         boolean prepTicks
      ) {
         this(targetDamage, charges, clicks, steady, cover, needsTotem, turn, selfCost, value, kills, selfFull, anchor, placeAnchor, prepTicks, 0);
      }

      public PosBlockUtilsSelf.Plan getSelfAnchor(float k) {
         return new PosBlockUtilsSelf.Plan(
            this.anchor,
            this.cover,
            this.placeAnchor,
            this.charges,
            this.targetDamage,
            this.selfCost,
            this.selfFull,
            this.needsTotem,
            this.kills,
            this.clicks,
            this.prepTicks,
            this.value,
            this.turn,
            this.steady,
            this.dig,
            k
         );
      }

      public Plan(
         BlockPos needsTotem,
         BlockPos clicks,
         boolean anchor,
         int selfFull,
         float prepTicks,
         float placeAnchor,
         float steady,
         boolean turn,
         boolean value,
         int targetDamage,
         int selfCost,
         double kills,
         double charges,
         boolean cover,
         int dig
      ) {
         this(needsTotem, clicks, anchor, selfFull, prepTicks, placeAnchor, steady, turn, value, targetDamage, selfCost, kills, charges, cover, dig, 0.0F);
      }

      public Plan(
         BlockPos selfCost,
         BlockPos prepTicks,
         boolean value,
         int kills,
         float placeAnchor,
         float cover,
         float anchor,
         boolean selfFull,
         boolean charges,
         int needsTotem,
         int targetDamage,
         double clicks
      ) {
         this(selfCost, prepTicks, value, kills, placeAnchor, cover, anchor, selfFull, charges, needsTotem, targetDamage, clicks, 0.0, true);
      }
   }

   public record Request(
      LivingEntity target,
      Predicted predicted,
      Predicted self,
      float fromYaw,
      float fromPitch,
      float maxSelfDamage,
      boolean allowTotemTrade,
      Window targetWindow,
      Window selfWindow,
      float targetEhp,
      boolean targetTotem,
      boolean allowCover,
      int glowstone,
      BonusLevelData tuning,
      Predicate<Vec3d> veto,
      BlockPos forcedCell,
      Vec3d digToward
   ) {
      public Request(
         LivingEntity fromPitch,
         Predicted allowTotemTrade,
         Predicted forcedCell,
         float targetWindow,
         float allowCover,
         float selfWindow,
         boolean tuning,
         Window fromYaw,
         Window targetEhp,
         float veto,
         boolean glowstone,
         boolean self,
         int target,
         BonusLevelData targetTotem,
         Predicate<Vec3d> maxSelfDamage,
         BlockPos predicted
      ) {
         this(
            fromPitch,
            allowTotemTrade,
            forcedCell,
            targetWindow,
            allowCover,
            selfWindow,
            tuning,
            fromYaw,
            targetEhp,
            veto,
            glowstone,
            self,
            target,
            targetTotem,
            maxSelfDamage,
            predicted,
            null
         );
      }

      public Request(
         LivingEntity tuning,
         Predicted allowCover,
         Predicted fromYaw,
         float predicted,
         float self,
         float target,
         boolean glowstone,
         Window fromPitch,
         Window targetTotem,
         float selfWindow,
         boolean maxSelfDamage,
         boolean targetWindow,
         int allowTotemTrade,
         BonusLevelData veto,
         Predicate<Vec3d> targetEhp
      ) {
         this(
            tuning,
            allowCover,
            fromYaw,
            predicted,
            self,
            target,
            glowstone,
            fromPitch,
            targetTotem,
            selfWindow,
            maxSelfDamage,
            targetWindow,
            allowTotemTrade,
            veto,
            targetEhp,
            null,
            null
         );
      }
   }

   private static final class Scan {
      private final double interactionRange;
      private final Map<Long, BlockHitResult> posBlockBlockHitResultMap = new HashMap<>();
      private final LivingEntity livingEntity;
      private final ClientPlayerEntity player;
      private final boolean livingVecActive;
      private final BlockPos floored;
      private final RefBareData tradeMode;
      private final PosBlockUtilsSelf.Request livingVecData;
      private double posAmount = -Double.MAX_VALUE;
      private final double livingAmount;
      private final float livingVecAmount;
      private final Vec3d digToward;
      private final Window livingVecWindow;
      private final BonusLevelData minData;
      private final Vec3d eyePos;
      private final double moveDeltaX;
      private final Armor attributeArmor;
      private final Predicted intersectsBoxPredicted;
      private PosBlockUtilsSelf.Plan posBlockData;
      private final double anchorReach;
      private final Vec3d projectedEyePos;
      private int livingVecAttemptCount;
      private final int intersectsBoxValue;
      private final boolean sameFeetLevel;
      private final ClientWorld world;
      private final BlockPos blockPos;
      private final float effectiveHealth;
      private final Window activeTargetWindow;
      private final boolean isAirborne;
      private final Armor attributeArmorLivingVec;

      private boolean canEyePos(BlockPos pos, BlockPos pos2, Box box) {
         double eyePosValue = this.interactionRange * this.interactionRange;

         for (Direction direction : PosBlockUtilsSelf.livingVecDirections) {
            BlockPos offset = pos.offset(direction);
            if (!offset.equals(pos2) && !BlockPosUtilsRespawn.canReplaceableAir(offset)) {
               Direction opposite = direction.getOpposite();
               if (PosBlockUtilsSelf.canFlag(this.eyePos, offset, opposite) && BlockEntityUtils.canBlockSquare(offset, opposite)) {
                  for (double vec3dValue : PosBlockUtilsSelf.raycastTable) {
                     for (double vec3dValue2 : PosBlockUtilsSelf.raycastTable) {
                        Vec3d vec3d = PosBlockUtilsSelf.getVec3d(offset, opposite, vec3dValue, vec3dValue2);
                        if (!(this.eyePos.squaredDistanceTo(vec3d) > eyePosValue) && !box.raycast(this.eyePos, vec3d).isPresent()) {
                           float[] wrapDegrees = MathClampUtils.getWrapDegrees(this.eyePos, vec3d);
                           if (BlockEntityUtils.getPosBlock(
                                 BlockEntityUtils.getMultiplySquared(this.eyePos, wrapDegrees[0], wrapDegrees[1]), offset, opposite, this.eyePos, this.interactionRange
                              )
                              != null) {
                              return true;
                           }
                        }
                     }
                  }
               }
            }
         }

         return false;
      }

      private static boolean canDown(BlockPos pos, BlockPos pos2) {
         return pos.equals(pos2) || pos.equals(pos2.up()) || pos.equals(pos2.down());
      }

      private double getStandingHeight(BlockPos pos) {
         Vec3d feet = this.intersectsBoxPredicted.feet();
         Vec3d center = Vec3d.ofCenter(pos);
         Vec3d vec3d = new Vec3d(feet.x, feet.y + this.player.getStandingEyeHeight(), feet.z);
         return RefBareData.getAmount(vec3d.y - center.y, vec3d.distanceTo(center));
      }

      void handleAmountPos(BlockPos pos) {
         PosBlockUtilsSelf.Plan posBlockData = this.getLivingVec(pos, false);
         if (posBlockData != null) {
            double amount = posBlockData.value() + this.getAmountLiving(pos);
            if (amount > this.posAmount) {
               this.posBlockData = posBlockData;
               this.posAmount = amount;
            }
         }
      }

      private boolean canIntersectsBox(BlockPos pos) {
         Box box = BlockEntityUtils.getBoxForBlockPos(pos);
         return !box.intersects(this.livingVecData.predicted().box())
            && !box.intersects(this.intersectsBoxPredicted.box())
            && !canDown(pos, this.blockPos)
            && !FloorMathUtilsBlock.canInterpolatorLerped(pos, this.intersectsBoxValue);
      }

      private BlockHitResult getPosBlock(BlockPos pos, BlockPos pos2) {
         long long2 = pos.asLong();
         BlockHitResult blockHitResult;
         if (this.posBlockBlockHitResultMap.containsKey(long2)) {
            blockHitResult = this.posBlockBlockHitResultMap.get(long2);
         } else {
            blockHitResult = this.getLivingVec(pos, null);
            this.posBlockBlockHitResultMap.put(long2, blockHitResult);
         }

         return blockHitResult != null && pos2 != null && blockHitResult.getBlockPos().equals(pos2) ? this.getLivingVec(pos, pos2) : blockHitResult;
      }

      List<BlockPos> getFlooredOffset() {
         Vec3d feet = this.livingVecData.predicted().feet();
         var arrayList = new ArrayList<BlockPos>();
         int flooredOffsetLimit = (int)Math.ceil(this.interactionRange);
         BlockPos floored = BlockPos.ofFloored(this.eyePos);
         Mutable mutable = new Mutable();

         for (int i = -flooredOffsetLimit; i <= flooredOffsetLimit; i++) {
            for (int j = -flooredOffsetLimit; j <= flooredOffsetLimit; j++) {
               for (int k = -flooredOffsetLimit; k <= flooredOffsetLimit; k++) {
                  mutable.set(floored.getX() + i, floored.getY() + j, floored.getZ() + k);
                  if (this.world.getBlockState(mutable).isOf(Blocks.RESPAWN_ANCHOR)) {
                     BlockPos immutable = mutable.toImmutable();
                     if (!(BlockEntityUtils.getMinMax(this.eyePos, BlockEntityUtils.getBoxForBlockPos(immutable)) > this.interactionRange)
                        && !(feet.distanceTo(Vec3d.ofCenter(immutable)) > 10.0)) {
                        arrayList.add(immutable);
                     }
                  }
               }
            }
         }

         arrayList.sort(Comparator.comparingDouble(feetT -> feet.squaredDistanceTo(Vec3d.ofCenter(feetT))));
         var arrayList2 = new ArrayList(arrayList);
         BlockPos ofFloored = BlockPos.ofFloored(feet);
         handle(arrayList2, ofFloored.up(2));

         for (Direction direction : PosBlockUtilsSelf.directions) {
            handle(arrayList2, ofFloored.offset(direction));
            handle(arrayList2, ofFloored.up().offset(direction));
         }

         handle(arrayList2, ofFloored.up().add(1, 0, 1));
         handle(arrayList2, ofFloored.up().add(1, 0, -1));
         handle(arrayList2, ofFloored.up().add(-1, 0, 1));
         handle(arrayList2, ofFloored.up().add(-1, 0, -1));

         for (int[] flooredOffsetValues : PosBlockUtilsSelf.flooredOffsetInts) {
            handle(arrayList2, ofFloored.add(flooredOffsetValues[0], 0, flooredOffsetValues[1]));
         }

         int signum = Integer.signum(floored.getX() - ofFloored.getX());
         int signum2 = Integer.signum(floored.getZ() - ofFloored.getZ());
         if (signum != 0 || signum2 != 0) {
            for (int[] flooredOffsetValues2 : PosBlockUtilsSelf.posBlockInts) {
               handle(arrayList2, ofFloored.add(flooredOffsetValues2[0] * signum, 0, flooredOffsetValues2[1] * signum2));
            }

            for (int[] flooredOffsetValues3 : PosBlockUtilsSelf.posBlockInts) {
               handle(arrayList2, ofFloored.up().add(flooredOffsetValues3[0] * signum, 0, flooredOffsetValues3[1] * signum2));
            }
         }

         return arrayList2;
      }

      Scan(PosBlockUtilsSelf.Request livingVecData, ClientPlayerEntity entity, ClientWorld world2) {
         this.livingVecData = livingVecData;
         this.player = entity;
         this.world = world2;
         this.livingEntity = livingVecData.target();
         this.eyePos = entity.getEyePos();
         this.isAirborne = !entity.isOnGround() && !entity.isTouchingWater();
         this.projectedEyePos = PosBlockUtilsSelf.getVec3d(this.eyePos, entity.getVelocity(), !this.isAirborne);
         this.livingVecActive = entity.hurtTime >= 7;
         this.interactionRange = BlockPosUtilsRespawn.getRangeInteraction();
         this.intersectsBoxPredicted = livingVecData.self() != null ? livingVecData.self() : new Predicted(entity.getEntityPos(), entity.getBoundingBox());
         this.minData = livingVecData.tuning() != null ? livingVecData.tuning() : BonusLevelData.INSTANCE;
         this.tradeMode = this.minData.tradeMode();
         this.activeTargetWindow = livingVecData.targetWindow() != null ? livingVecData.targetWindow() : Window.INSTANCE;
         this.livingVecWindow = livingVecData.selfWindow() != null ? livingVecData.selfWindow() : Window.INSTANCE;
         this.attributeArmorLivingVec = BlockEntityUtils.getAttributeArmor(this.livingEntity);
         this.attributeArmor = BlockEntityUtils.getAttributeArmor(entity);
         this.livingVecAmount = Float.isNaN(livingVecData.targetEhp()) ? this.livingEntity.getHealth() + this.livingEntity.getAbsorptionAmount() : livingVecData.targetEhp();
         this.effectiveHealth = entity.getHealth() + entity.getAbsorptionAmount();
         this.blockPos = entity.getBlockPos();
         this.floored = BlockPos.ofFloored(this.intersectsBoxPredicted.feet());
         this.sameFeetLevel = PosBlockUtilsSelf.canFlag(this.intersectsBoxPredicted.feet().y, livingVecData.predicted().feet().y);
         this.digToward = livingVecData.digToward();
         this.anchorReach = this.digToward != null ? 6.0 : 3.0;
         this.intersectsBoxValue = FloorMathUtilsBlock.getValueInt();
         Vec3d entityPos = this.livingEntity.getEntityPos();
         this.moveDeltaX = entityPos.x - this.livingEntity.lastX;
         this.livingAmount = entityPos.z - this.livingEntity.lastZ;
      }

      private float[] getSubtractLength(BlockPos pos, BlockPos pos2, Vec3d vec) {
         Vec3d subtract = Vec3d.ofCenter(pos).subtract(vec);
         double varLength = subtract.length();
         if (varLength < 1.0E-6) {
            return new float[0];
         } else {
            double subtractLengthFactor = subtract.x / varLength;
            double subtractLengthFactor2 = subtract.y / varLength;
            double subtractLengthFactor3 = subtract.z / varLength;
            int subtractLengthSize = Math.min(24, (int)Math.ceil(varLength / 0.3) + 2);
            float[] floats = new float[subtractLengthSize];
            Mutable mutable = new Mutable();

            for (int i = 0; i < subtractLengthSize; i++) {
               double subtractLengthFactor4 = i * 0.3;
               mutable.set((int)Math.floor(vec.x + subtractLengthFactor * subtractLengthFactor4), (int)Math.floor(vec.y + subtractLengthFactor2 * subtractLengthFactor4), (int)Math.floor(vec.z + subtractLengthFactor3 * subtractLengthFactor4));
               if (mutable.equals(pos)) {
                  floats[i] = this.getMax(mutable);
                  return Arrays.copyOf(floats, i + 1);
               }

               floats[i] = mutable.equals(pos2) ? Float.NaN : this.getMax(mutable);
            }

            return new float[0];
         }
      }

      private static void handle(List<BlockPos> list, BlockPos pos) {
         if (!list.contains(pos)) {
            list.add(pos);
         }
      }

      private int getVecCenter(BlockPos pos) {
         Vec3d feet = this.livingVecData.predicted().feet();
         Vec3d center = Vec3d.ofCenter(pos);
         int vecCenterValue = (int)Math.floor(feet.y + 0.001);
         int vecCenter = 0;

         for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
               int x = pos.getX() + i;
               int z = pos.getZ() + j;
               double hypot = Math.hypot(x + 0.5 - feet.x, z + 0.5 - feet.z);
               if (!(hypot < 2.0)
                  && !(hypot > 3.2)
                  && (this.digToward == null || !((x + 0.5 - feet.x) * this.digToward.x + (z + 0.5 - feet.z) * this.digToward.z < 0.0))) {
                  for (int k = 1; k <= 2; k++) {
                     if (this.canSquareSolid(new BlockPos(x, vecCenterValue - k, z), pos, center)) {
                        vecCenter++;
                        break;
                     }
                  }
               }
            }
         }

         return vecCenter;
      }

      private boolean canIntersectsBoxForBlockPos(BlockPos pos) {
         if (!BlockPosUtilsRespawn.canReplaceableAir(pos)) {
            return false;
         } else if (!this.world.canPlace(Blocks.RESPAWN_ANCHOR.getDefaultState(), pos, ShapeContext.absent())) {
            return false;
         } else {
            Box box = BlockEntityUtils.getBoxForBlockPos(pos);
            return !box.intersects(this.livingVecData.predicted().box()) && !box.intersects(this.intersectsBoxPredicted.box())
               ? !canDown(pos, this.blockPos) && !canDown(pos, this.floored) && !FloorMathUtilsBlock.canInterpolatorLerped(pos, this.intersectsBoxValue)
               : false;
         }
      }

      private double getAmountLiving(BlockPos pos) {
         Vec3d feet = this.livingVecData.predicted().feet();
         return PosBlockUtilsSelf.getAmount(feet.x, feet.z, (int)Math.floor(feet.y + 0.001), pos.getX(), pos.getY(), pos.getZ(), this.moveDeltaX, this.livingAmount);
      }

      private boolean canOffsetEye(BlockPos pos) {
         if (BlockEntityUtils.getMinMax(this.eyePos, BlockEntityUtils.getBoxForBlockPos(pos)) > this.interactionRange) {
            return false;
         } else {
            for (Direction direction : PosBlockUtilsSelf.livingVecDirections) {
               if (!BlockPosUtilsRespawn.canReplaceableAir(pos.offset(direction))) {
                  return true;
               }
            }

            return false;
         }
      }

      private boolean canLivingBox(BlockPos pos, BlockPos pos2) {
         return PosBlockUtilsSelf.canRaycastAmounts(
            this.eyePos, this.interactionRange, pos, pos2, this.livingEntity.getBoundingBox(), this.livingVecData.predicted().box()
         );
      }

      private float getMax(BlockPos pos) {
         BlockState blockState = this.world.getBlockState(pos);
         return blockState.isAir() && blockState.getFluidState().isEmpty()
            ? Float.NaN
            : Math.max(blockState.getBlock().getBlastResistance(), blockState.getFluidState().getBlastResistance());
      }

      private boolean canVecLiving(BlockPos pos, boolean vecLivingFlag, BlockHitResult blockHitResult) {
         if (this.projectedEyePos != null && blockHitResult != null) {
            if (this.projectedEyePos.squaredDistanceTo(blockHitResult.getPos()) > this.interactionRange * this.interactionRange) {
               return false;
            } else {
               BlockHitResult blockHitResult2 = vecLivingFlag
                  ? BlockEntityUtils.getAmountsSquared(
                     pos, PosBlockUtilsSelf.livingVecDirections, null, this.projectedEyePos, this.livingVecData.fromYaw(), this.livingVecData.fromPitch(), this.interactionRange
                  )
                  : BlockEntityUtils.getAmountsSquared(
                     blockHitResult.getBlockPos(),
                     new Direction[]{blockHitResult.getSide()},
                     blockHitResult.getSide(),
                     this.projectedEyePos,
                     this.livingVecData.fromYaw(),
                     this.livingVecData.fromPitch(),
                     this.interactionRange
                  );
               return blockHitResult2 != null;
            }
         } else {
            return true;
         }
      }

      PosBlockUtilsSelf.Plan getLivingVec(BlockPos pos, boolean livingVecFlag) {
         int chargesRespawn = BlockPosUtilsRespawn.getChargesRespawn(pos);
         boolean vecLivingFlag = chargesRespawn >= 0;
         Vec3d center = Vec3d.ofCenter(pos);
         double distanceTo = this.livingVecData.predicted().feet().distanceTo(center);
         if (distanceTo > 10.0) {
            return null;
         } else if (vecLivingFlag || this.canIntersectsBoxForBlockPos(pos) && this.canOffsetEye(pos)) {
            if (this.livingVecData.veto() != null && this.livingVecData.veto().test(center)) {
               return null;
            } else {
               int livingVecValue = vecLivingFlag ? chargesRespawn : 0;
               int livingVecLimit = livingVecValue <= 0 ? 1 : 0;
               if (this.livingVecData.glowstone() < livingVecLimit) {
                  return null;
               } else {
                  float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(BlockEntityUtils.getAmount(this.livingEntity, distanceTo, 5.0F), this.attributeArmorLivingVec);
                  if (toughnessEpf <= 0.0F) {
                     return null;
                  } else {
                     if (!livingVecFlag) {
                        if (this.posBlockData != null
                           && this.minData.offWeight() * toughnessEpf + this.minData.popBonus() + this.minData.killBonus()
                                 + (this.sameFeetLevel ? this.anchorReach : 0.0)
                                 + this.tradeMode.getAmount()
                                 + this.getAmountLiving(pos)
                              <= this.posAmount) {
                           return null;
                        }

                        this.livingVecAttemptCount++;
                     }

                     BlockHitResult blockHitResult = vecLivingFlag
                        ? BlockEntityUtils.getAmountsSquared(
                           pos, PosBlockUtilsSelf.livingVecDirections, null, this.eyePos, this.livingVecData.fromYaw(), this.livingVecData.fromPitch(), this.interactionRange
                        )
                        : this.getPosBlock(pos, null);
                     if (blockHitResult == null) {
                        return null;
                     } else if (!vecLivingFlag && !this.canLivingBox(pos, null)) {
                        return null;
                     } else {
                        boolean vecLiving = this.canVecLiving(pos, vecLivingFlag, blockHitResult);
                        if (!livingVecFlag && PosBlockUtilsSelf.canFlag(vecLiving, !vecLivingFlag, this.isAirborne, this.livingVecActive)) {
                           return null;
                        } else {
                           PosBlockUtilsSelf.Option livingVec = this.getLivingVec(pos, !vecLivingFlag, livingVecValue, null, blockHitResult, vecLiving);
                           if (livingVec != null && !PosBlockUtilsSelf.canFlag(livingVecFlag, !vecLivingFlag, livingVec.plan().kills(), livingVec.plan().turn())) {
                              PosBlockUtilsSelf.Plan plan = livingVec.safe() ? livingVec.plan() : null;
                              boolean livingVecFlag3 = this.tradeMode.on()
                                 ? PosBlockUtilsSelf.canFlag(
                                    this.livingVecData.allowCover(), this.livingVecData.glowstone(), livingVecLimit, livingVec.safe(), livingVec.plan().selfFull()
                                 )
                                 : PosBlockUtilsSelf.canFlag(
                                    this.livingVecData.allowCover(),
                                    this.livingVecData.glowstone(),
                                    livingVecLimit,
                                    livingVec.safe(),
                                    livingVec.plan().selfFull(),
                                    livingVec.plan().clicks(),
                                    this.activeTargetWindow.readyIn()
                                 );
                              if (this.tradeMode.on()) {
                                 livingVecFlag3 &= !livingVec.safe() || livingVec.plan().needsTotem();
                              }

                              if (livingVecFlag3) {
                                 BlockPos boxDefault = this.getBoxDefault(pos, !vecLivingFlag);
                                 if (boxDefault != null && this.canLivingBox(pos, boxDefault)) {
                                    PosBlockUtilsSelf.Option livingVec2 = this.getLivingVec(pos, !vecLivingFlag, livingVecValue, boxDefault, blockHitResult, vecLiving);
                                    if (livingVec2 != null
                                       && (!this.tradeMode.on() || plan == null || !livingVec2.plan().needsTotem() && (!plan.kills() || livingVec2.plan().kills()))
                                       && livingVec2.safe()
                                       && (
                                          plan == null
                                             || livingVec2.plan().value() > plan.value()
                                             || PosBlockUtilsSelf.canDamageTarget(this.tradeMode, plan, livingVec2.plan(), this.livingVecAmount)
                                       )) {
                                       plan = livingVec2.plan();
                                    }
                                 }
                              }

                              return plan != null && this.sameFeetLevel ? PosBlockUtilsSelf.getSelfAnchor(plan, this.getVecCenter(pos), this.anchorReach) : plan;
                           } else {
                              return null;
                           }
                        }
                     }
                  }
               }
            }
         } else {
            return null;
         }
      }

      private BlockHitResult getLivingVec(BlockPos pos, BlockPos pos2) {
         BlockHitResult livingVec = null;
         double livingVecLimit = Double.MAX_VALUE;

         for (Direction direction : PosBlockUtilsSelf.livingVecDirections) {
            BlockPos offset = pos.offset(direction);
            if (!offset.equals(pos2) && !BlockPosUtilsRespawn.canReplaceableAir(offset)) {
               Direction opposite = direction.getOpposite();
               if (BlockEntityUtils.canBlockSquare(offset, opposite)) {
                  BlockHitResult amountsSquared = BlockEntityUtils.getAmountsSquared(
                     offset, new Direction[]{opposite}, opposite, this.eyePos, this.livingVecData.fromYaw(), this.livingVecData.fromPitch(), this.interactionRange
                  );
                  if (amountsSquared != null && this.canRaycastLiving(amountsSquared.getPos())) {
                     float[] wrapDegrees = MathClampUtils.getWrapDegrees(this.eyePos, amountsSquared.getPos());
                     double livingVecValue = BoxLengthUtils.getAmount(this.livingVecData.fromYaw(), this.livingVecData.fromPitch(), wrapDegrees[0], wrapDegrees[1]);
                     if (livingVecValue < livingVecLimit) {
                        livingVecLimit = livingVecValue;
                        livingVec = amountsSquared;
                     }
                  }
               }
            }
         }

         return livingVec;
      }

      private BlockPos getBoxDefault(BlockPos pos, boolean boxDefaultFlag) {
         GlowstoneTimeout.CoverPlan minMax = BlockPosUtilsRespawn.getMinMax(
            pos,
            this.intersectsBoxPredicted.box(),
            cellx -> this.canIntersectsBox(cellx) && this.canRaycastPos(cellx, pos, boxDefaultFlag),
            Blocks.GLOWSTONE.getDefaultState()
         );
         return minMax.isFlag() && !minMax.isShieldableBlocked() ? BlockPosUtilsRespawn.getBlockPos(pos, minMax.index()) : null;
      }

      private boolean canRaycastLiving(Vec3d vec) {
         return this.livingEntity.getBoundingBox().raycast(this.eyePos, vec).isEmpty() && this.livingVecData.predicted().box().raycast(this.eyePos, vec).isEmpty();
      }

      private boolean canEmptyState(BlockPos pos) {
         BlockState blockState = this.world.getBlockState(pos);
         return blockState.getCollisionShape(this.world, pos).isEmpty() && blockState.getFluidState().isEmpty();
      }

      private boolean canSquareSolid(BlockPos pos, BlockPos pos2, Vec3d vec) {
         BlockPos up = pos.up();
         boolean emptyState = this.canEmptyState(pos);
         boolean squareSolidFlag = this.canEmptyState(up) || up.equals(pos2);
         if (emptyState && squareSolidFlag) {
            return false;
         } else if (!emptyState && !this.canStateFluid(pos, pos2, vec)) {
            return false;
         } else if (!squareSolidFlag && !this.canStateFluid(up, pos2, vec)) {
            return false;
         } else {
            BlockPos down = pos.down();
            return !this.world.getBlockState(down).isSideSolidFullSquare(this.world, down, Direction.UP)
               ? false
               : !this.canStateFluid(down, pos2, vec);
         }
      }

      private PosBlockUtilsSelf.Option getLivingVec(BlockPos pos, boolean livingVecFlag, int livingVecValue, BlockPos pos2, BlockHitResult blockHitResult, boolean posBlockFlag) {
         int posBlockBound = (livingVecFlag ? 1 : 0) + (pos2 != null ? 1 : 0) + (livingVecValue <= 0 ? 1 : 0) + 1;
         Vec3d center = Vec3d.ofCenter(pos);
         double distanceTo = this.livingVecData.predicted().feet().distanceTo(center);
         boolean livingVecFlag3 = livingVecFlag
            && Math.abs(pos.getX() - this.blockPos.getX()) <= 3
            && Math.abs(pos.getZ() - this.blockPos.getZ()) <= 3
            && pos.getY() <= this.blockPos.getY()
            && pos.getY() >= this.blockPos.getY() - 1
            && distanceTo <= 4.0;
         if (livingVecFlag3 && pos2 != null) {
            return null;
         } else {
            boolean livingVecFlag4 = livingVecFlag3 && PosBlockUtilsSelf.canFlag3(distanceTo, this.intersectsBoxPredicted.feet().distanceTo(center));
            float[] wrapDegrees = MathClampUtils.getWrapDegrees(this.eyePos, blockHitResult.getPos());
            double posBlockValue = BoxLengthUtils.getAmount(this.livingVecData.fromYaw(), this.livingVecData.fromPitch(), wrapDegrees[0], wrapDegrees[1]);
            int livingVecFactor = PosBlockUtilsSelf.getValue(livingVecFlag, pos2 != null, livingVecValue, posBlockValue);
            int landTicks = PosBlockUtilsSelf.getValue(posBlockBound) + livingVecFactor;
            float boxBounding = EntityCenterUtils.getBoxBounding(this.livingEntity, this.livingVecData.predicted(), pos, pos2);
            float livingVecLimit = this.tradeMode.on()
               ? PosBlockUtilsBonus.getScaledInstance(boxBounding, this.attributeArmorLivingVec, this.activeTargetWindow, landTicks)
               : PosBlockUtilsBonus.getReadyInstance(boxBounding, this.attributeArmorLivingVec, this.activeTargetWindow, landTicks);
            float knock = this.tradeMode.on() ? PosBlockUtilsBonus.getDifficultyInstance(boxBounding, this.attributeArmorLivingVec, 5.0F) : 0.0F;
            if (livingVecLimit <= 0.0F && !this.tradeMode.canFlag(livingVecLimit, knock, false)) {
               return null;
            } else {
               boolean livingVecFlag5 = livingVecLimit >= this.livingVecAmount;
               boolean kills = livingVecFlag5 && !this.livingVecData.targetTotem();
               float pilotStatusBound = EntityCenterUtils.getBoxBounding(pos, pos2, null);
               if (this.livingVecData.self() != null) {
                  pilotStatusBound = Math.max(pilotStatusBound, EntityCenterUtils.getBoxBounding(pos, pos2, this.livingVecData.self()));
               }

               float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(pilotStatusBound, this.attributeArmor);
               float scaledInstance = PosBlockUtilsBonus.getScaledInstance(pilotStatusBound, this.attributeArmor, this.livingVecWindow, landTicks);
               if (this.tradeMode.on()) {
                  knock = this.tradeMode.getAmount(knock, PosBlockUtilsBonus.getDifficultyInstance(pilotStatusBound, this.attributeArmor, 5.0F), this.getStandingHeight(pos));
               }

               boolean livingVecFlag7 = toughnessEpf >= this.effectiveHealth - 1.0F;
               float selfEhp = livingVecFlag ? toughnessEpf : this.tradeMode.getBareRef(toughnessEpf, scaledInstance);
               boolean livingVecFlag8 = this.tradeMode.ref()
                  ? this.tradeMode.canBare(selfEhp, this.effectiveHealth, this.tradeMode.canBareBehind(kills, livingVecFlag5 && !livingVecFlag))
                  : (
                     this.minData.tradeRule()
                        ? PosBlockUtilsBonus.canFlag(
                              livingVecLimit,
                              scaledInstance,
                              toughnessEpf,
                              livingVecFlag5,
                              kills,
                              this.livingVecData.allowTotemTrade(),
                              this.minData.popTrade(),
                              this.effectiveHealth,
                              this.minData.selfSpent()
                           )
                           && scaledInstance <= this.livingVecData.maxSelfDamage()
                        : (!livingVecFlag7 || this.livingVecData.allowTotemTrade() && kills) && scaledInstance <= this.livingVecData.maxSelfDamage()
                  );
               if (!livingVecFlag8 && this.tradeMode.canRef(livingVecLimit, knock, livingVecFlag5, this.livingVecAmount, toughnessEpf, scaledInstance, this.effectiveHealth)) {
                  livingVecFlag8 = true;
               }

               if (livingVecFlag8 && !this.tradeMode.canRef(livingVecLimit, livingVecFlag5, toughnessEpf, selfEhp, false)) {
                  livingVecFlag8 = false;
               }

               if (livingVecFlag3 && livingVecFlag7) {
                  livingVecFlag8 = false;
               }

               double livingVecValue7 = PosBlockUtilsSelf.getAmount(
                     PosBlockUtilsBonus.getBonusWeight(livingVecLimit, livingVecFlag5, kills, scaledInstance, posBlockValue, false, this.minData) - 1.0F * (posBlockBound - 1), posBlockFlag
                  )
                  + this.tradeMode.getAmountForFloat(knock)
                  - PosBlockUtilsSelf.getAmount(livingVecFlag) * livingVecFactor
                  - (!livingVecFlag3 || livingVecFlag4 && !livingVecFlag7 ? 0.0 : 1.0);
               PosBlockUtilsSelf.Plan selfAnchor = new PosBlockUtilsSelf.Plan(
                     pos, pos2, livingVecFlag, livingVecValue, livingVecLimit, scaledInstance, toughnessEpf, livingVecFlag7, kills, posBlockBound, landTicks, livingVecValue7, posBlockValue, posBlockFlag
                  )
                  .getSelfAnchor(knock);
               return new PosBlockUtilsSelf.Option(selfAnchor, livingVecFlag8);
            }
         }
      }

      private boolean canStateFluid(BlockPos pos, BlockPos pos2, Vec3d vec) {
         return !this.world.getBlockState(pos).getFluidState().isEmpty() ? false : PosBlockUtilsSelf.canFlag(this.getSubtractLength(pos, pos2, vec));
      }

      private boolean canRaycastPos(BlockPos pos, BlockPos pos2, boolean raycastPosFlag) {
         BlockHitResult posBlock = this.getPosBlock(pos, pos2);
         if (posBlock == null) {
            return false;
         } else if (!raycastPosFlag) {
            return true;
         } else {
            Box box = BlockEntityUtils.getBoxForBlockPos(pos2);
            return box.raycast(this.eyePos, posBlock.getPos()).isEmpty() || this.canEyePos(pos, pos2, box);
         }
      }
   }
}
