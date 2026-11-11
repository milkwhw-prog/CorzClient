package com.corz.client.entity;

import com.corz.client.config.FriendNameStore;
import com.corz.client.world.MathClampUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class EntityEyeUtils {
   private static final MinecraftClient entityEyeMinecraftClient = MinecraftClient.getInstance();

   public static double getWrapDegrees(Vec3d vec, float wrapDegreesValue, float wrapDegreesValue2) {
      if (entityEyeMinecraftClient.player == null) {
         return 180.0;
      } else {
         Vec3d eyePos = entityEyeMinecraftClient.player.getEyePos();
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, vec);
         float wrapDegrees2 = MathHelper.wrapDegrees(wrapDegrees[0] - wrapDegreesValue);
         float wrapDegreesFactor = wrapDegrees[1] - wrapDegreesValue2;
         return Math.sqrt(wrapDegrees2 * wrapDegrees2 + wrapDegreesFactor * wrapDegreesFactor);
      }
   }

   public static LivingEntity getInstanceEye(double eyeFactor, Predicate<LivingEntity> predicate) {
      if (entityEyeMinecraftClient.world != null && entityEyeMinecraftClient.player != null) {
         Vec3d eyePos = entityEyeMinecraftClient.player.getEyePos();
         double eyeValue = eyeFactor * eyeFactor;
         LivingEntity instanceEye = null;
         double maxValue = Double.MAX_VALUE;

         for (Entity entity : entityEyeMinecraftClient.world.getEntities()) {
            if (entity != entityEyeMinecraftClient.player && entity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
               double squaredDistanceTo = eyePos.squaredDistanceTo(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ());
               if (!(squaredDistanceTo > eyeValue) && !(squaredDistanceTo >= maxValue) && (predicate == null || predicate.test(livingEntity))) {
                  instanceEye = livingEntity;
                  maxValue = squaredDistanceTo;
               }
            }
         }

         return instanceEye;
      } else {
         return null;
      }
   }

   private EntityEyeUtils() {
   }

   public static boolean canInstanceSquared(Vec3d vec) {
      if (entityEyeMinecraftClient.world != null && entityEyeMinecraftClient.player != null) {
         Vec3d eyePos = entityEyeMinecraftClient.player.getEyePos();
         BlockHitResult raycast = entityEyeMinecraftClient.world.raycast(new RaycastContext(eyePos, vec, ShapeType.COLLIDER, FluidHandling.NONE, entityEyeMinecraftClient.player));
         return raycast.getType() == Type.MISS || eyePos.squaredDistanceTo(raycast.getPos()) >= eyePos.squaredDistanceTo(vec);
      } else {
         return false;
      }
   }

   public static List<PlayerEntity> getInstanceEye(double eyeFactor) {
      ArrayList arrayList = new ArrayList();
      if (entityEyeMinecraftClient.world != null && entityEyeMinecraftClient.player != null) {
         Vec3d eyePos = entityEyeMinecraftClient.player.getEyePos();
         double eyeLimit = eyeFactor * eyeFactor;

         for (PlayerEntity playerEntity : entityEyeMinecraftClient.world.getPlayers()) {
            if (playerEntity != entityEyeMinecraftClient.player && playerEntity.isAlive() && !playerEntity.isSpectator()) {
               double squaredDistanceTo = eyePos.squaredDistanceTo(playerEntity.getX(), playerEntity.getEyeY(), playerEntity.getZ());
               if (squaredDistanceTo <= eyeLimit && !FriendNameStore.canFlag(playerEntity)) {
                  arrayList.add(playerEntity);
               }
            }
         }

         return arrayList;
      } else {
         return arrayList;
      }
   }

   public static PlayerEntity getNext(double entityEyeFactor) {
      if (entityEyeMinecraftClient.player == null) {
         return null;
      } else if (entityEyeMinecraftClient.world == null) {
         return null;
      } else {
         Vec3d eyePos = entityEyeMinecraftClient.player.getEyePos();
         double entityEyeLimit = entityEyeFactor * entityEyeFactor;
         PlayerEntity next = null;
         double maxValue = Double.MAX_VALUE;

         for (PlayerEntity playerEntity : entityEyeMinecraftClient.world.getPlayers()) {
            if (playerEntity != entityEyeMinecraftClient.player && playerEntity.isAlive() && !playerEntity.isSpectator()) {
               double squaredDistanceTo = eyePos.squaredDistanceTo(playerEntity.getX(), playerEntity.getEyeY(), playerEntity.getZ());
               if (squaredDistanceTo <= entityEyeLimit && (next == null || squaredDistanceTo < maxValue) && !FriendNameStore.canFlag(playerEntity)) {
                  next = playerEntity;
                  maxValue = squaredDistanceTo;
               }
            }
         }

         return next;
      }
   }

   public static double getInstancePlayer(Vec3d vec) {
      return entityEyeMinecraftClient.player == null ? 180.0 : getWrapDegrees(vec, entityEyeMinecraftClient.player.getYaw(), entityEyeMinecraftClient.player.getPitch());
   }
}
