package com.corz.client.entity;

import com.corz.client.inventory.BlockEntityUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class EntityCenterUtils {

   public static float getBoxBounding(BlockPos pos, BlockPos pos2, BlockEntityUtils.Predicted predicted) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && pos != null) {
         Vec3d vec3d = predicted != null ? predicted.feet() : player.getEntityPos();
         Box box = predicted != null ? predicted.box() : player.getBoundingBox();
         return BlockEntityUtils.getCreativeSpectator(player, Vec3d.ofCenter(pos), vec3d, box, pos2, pos, 5.0F);
      } else {
         return 0.0F;
      }
   }

   public static float getBoxBounding(LivingEntity entity, BlockEntityUtils.Predicted predicted, BlockPos pos, BlockPos pos2) {
      if (entity != null && pos != null) {
         Vec3d vec3d = predicted != null ? predicted.feet() : entity.getEntityPos();
         Box box = predicted != null ? predicted.box() : entity.getBoundingBox();
         return BlockEntityUtils.getCreativeSpectator(entity, Vec3d.ofCenter(pos), vec3d, box, pos2, pos, 5.0F);
      } else {
         return 0.0F;
      }
   }

   private EntityCenterUtils() {
   }
}
