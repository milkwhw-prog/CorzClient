package com.corz.client.utils;

import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.MathClampUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class BlockHitResolver {
   public static BlockHitResult findBlockHitAmongFaces(BlockPos pos, Direction[] directions, Direction direction, double eyeValue, float squaredValue, float amountFloat) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      return player != null && instance.world != null && pos != null && directions != null
         ? BlockEntityUtils.getAmountsSquared(pos, directions, direction, player.getEyePos(), squaredValue, amountFloat, eyeValue)
         : null;
   }

   public static BlockHitResult findBlockHitOnFace(BlockPos pos, Direction direction, double eyeValue, float multiplySquaredValue, float amountFloat) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.world != null && pos != null) {
         Vec3d eyePos = player.getEyePos();
         return BlockEntityUtils.getPosBlock(BlockEntityUtils.getMultiplySquared(eyePos, multiplySquaredValue, amountFloat), pos, direction, eyePos, eyeValue);
      } else {
         return null;
      }
   }

   public static ActionResult interactWithBlock(BlockHitResult blockHitResult) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.interactionManager != null && blockHitResult != null) {
         ActionResult interactBlock = instance.interactionManager.interactBlock(player, Hand.MAIN_HAND, blockHitResult);
         if (interactBlock instanceof Success success && success.swingSource() == SwingSource.CLIENT) {
            player.swingHand(Hand.MAIN_HAND);
         }

         return interactBlock;
      } else {
         return ActionResult.PASS;
      }
   }

   public static BlockHitResult chooseLessRotationHit(
      BlockHitResult posCenter, Direction[] directions, Direction direction, double posCenterValue, float posValue, float amountFloat, BlockPos pos, float posCenterLimit
   ) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (posCenter != null && pos != null && player != null && posCenterLimit > 0.0F) {
         Vec3d eyePos = player.getEyePos();
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, Vec3d.ofCenter(pos));
         BlockHitResult eyeInstance = findBlockHitAmongFaces(posCenter.getBlockPos(), directions, direction, posCenterValue, wrapDegrees[0], wrapDegrees[1]);
         if (eyeInstance == null) {
            return posCenter;
         } else {
            return computeRotationSteps(eyePos, eyeInstance, posValue, amountFloat, posCenterLimit) <= computeRotationSteps(eyePos, posCenter, posValue, amountFloat, posCenterLimit) ? eyeInstance : posCenter;
         }
      } else {
         return posCenter;
      }
   }

   private BlockHitResolver() {
   }

   static double computeRotationSteps(Vec3d vec, BlockHitResult blockHitResult, float posValue, float clamp, float posDivisor) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, blockHitResult.getPos());
      return Math.ceil(BoxLengthUtils.getAmount(posValue, clamp, wrapDegrees[0], wrapDegrees[1]) / posDivisor);
   }

   public static BlockHitResult findNearestRotationHit(BlockPos pos, double posEyeValue, float boxLengthValue, float amountFloat) {
      BlockHitResult eyeInstance = findBlockHitAmongFaces(pos, Direction.values(), null, posEyeValue, boxLengthValue, amountFloat);
      if (eyeInstance != null) {
         return eyeInstance;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player != null && instance.world != null && pos != null) {
            Vec3d eyePos = player.getEyePos();
            BlockHitResult posEye = null;
            double posEyeLimit = Double.MAX_VALUE;

            for (Direction direction : Direction.values()) {
               BlockHitResult blockHitAmongFaces = findBlockHitAmongFaces(pos, new Direction[]{direction}, null, posEyeValue, boxLengthValue, amountFloat);
               if (blockHitAmongFaces != null) {
                  float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, blockHitAmongFaces.getPos());
                  double posEyeValue4 = BoxLengthUtils.getAmount(boxLengthValue, amountFloat, wrapDegrees[0], wrapDegrees[1]);
                  if (posEyeValue4 < posEyeLimit) {
                     posEyeLimit = posEyeValue4;
                     posEye = blockHitAmongFaces;
                  }
               }
            }

            return posEye;
         } else {
            return null;
         }
      }
   }
}
