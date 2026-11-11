package com.corz.client.utils;

import com.corz.client.world.MathClampUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class BlockOffsetUtils {
   private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

   public static boolean canPlaceAt(BlockPos pos2) {
      if (CLIENT.world == null) {
         return false;
      } else {
         BlockState blockState = CLIENT.world.getBlockState(pos2);
         return !blockState.isAir() && !blockState.isReplaceable() ? false : findPlacementHit(pos2) != null;
      }
   }

   public static boolean placeBlockAt(Object obj, BlockPos pos2, boolean interactionFlag, boolean eyeFlag, int interactionValue) {
      if (CLIENT.player != null && CLIENT.interactionManager != null) {
         BlockHitResult offsetOpposite = findPlacementHit(pos2);
         if (offsetOpposite == null) {
            return false;
         } else {
            if (interactionFlag) {
               MathClampUtils.handleInstanceEye(obj, offsetOpposite.getPos(), interactionValue, eyeFlag);
            }

            CLIENT.interactionManager.interactBlock(CLIENT.player, Hand.MAIN_HAND, offsetOpposite);
            return true;
         }
      } else {
         return false;
      }
   }

   public static BlockHitResult findPlacementHit(BlockPos pos2) {
      if (CLIENT.world == null) {
         return null;
      } else {
         for (Direction direction : Direction.values()) {
            BlockPos offset = pos2.offset(direction);
            BlockState blockState = CLIENT.world.getBlockState(offset);
            if (!blockState.isAir() && !blockState.isReplaceable()) {
               Direction opposite = direction.getOpposite();
               Vec3d add = Vec3d.ofCenter(offset).add(opposite.getOffsetX() * 0.5, opposite.getOffsetY() * 0.5, opposite.getOffsetZ() * 0.5);
               return new BlockHitResult(add, opposite, offset, false);
            }
         }

         return null;
      }
   }

   private BlockOffsetUtils() {
   }
}
