package com.corz.client.entity;

import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class BoxVisibilityRaycaster {
   private static final double[] X_SIDES = new double[]{0.0, 1.0};
   private static final double[] Y_FRACTIONS = new double[]{0.0, 0.5, 1.0};
   private static final double[] Z_SIDES = new double[]{0.0, 1.0};

   private BoxVisibilityRaycaster() {
   }

   public static float computeVisibleFraction(Vec3d vec, Box box, Entity entity, BlockPos pos, BlockPos pos2) {
      ClientWorld world = MinecraftClient.getInstance().world;
      if (world != null && vec != null && box != null) {
         Box box3 = pos == null
            ? null
            : new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0 - 0.001, pos.getZ() + 1.0);
         ShapeContext shapeContext = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
         int raycastCounter = 0;

         for (double raycastValue : X_SIDES) {
            for (double raycastFactor : Y_FRACTIONS) {
               for (double zSidesItem : Z_SIDES) {
                  double x = raycastValue == 0.0 ? box.minX + 0.001 : box.maxX - 0.001;
                  double y = box.minY + (box.maxY - box.minY) * raycastFactor;
                  double z = zSidesItem == 0.0 ? box.minZ + 0.001 : box.maxZ - 0.001;
                  Vec3d vec3d = new Vec3d(x, y, z);
                  BlockHitResult raycast = world.raycast(new RaycastContext(vec3d, vec, ShapeType.COLLIDER, FluidHandling.NONE, shapeContext));
                  boolean raycastFlag = raycast.getType() == Type.MISS || pos2 != null && raycast.getType() == Type.BLOCK && raycast.getBlockPos().equals(pos2);
                  if (raycastFlag && box3 != null && box3.raycast(vec3d, vec).isPresent()) {
                     raycastFlag = false;
                  }

                  if (raycastFlag) {
                     raycastCounter++;
                  }
               }
            }
         }

         return raycastCounter / 12.0F;
      } else {
         return 0.0F;
      }
   }
}
