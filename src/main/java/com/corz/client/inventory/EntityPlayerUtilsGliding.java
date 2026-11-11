package com.corz.client.inventory;

import com.corz.client.world.MathClampUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttackRangeComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.BlocksAttacksComponent.DamageReduction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.categories.combat.SilentAim;
import com.corz.client.utils.EntityDamageUtils;

public final class EntityPlayerUtilsGliding {
   private static ClientPlayerEntity player;
   private static final MinecraftClient entityPlayerMinecraftClient = MinecraftClient.getInstance();
   private static double y = Double.NaN;

   public static void updatePlayer() {
      player = null;
      y = Double.NaN;
   }

   public static int getSpaceInstance(int spaceValue) {
      ClientPlayerEntity player = entityPlayerMinecraftClient.player;
      if (player != null && entityPlayerMinecraftClient.world != null && !player.isOnGround()) {
         Vec3d velocity = player.getVelocity();
         double[] spaceValues = FlagInventoryUtils.getAmounts(velocity.y, player.getFinalGravity(), spaceValue);
         Box boundingBox = player.getBoundingBox();
         double spaceValue2 = 0.0;
         double spaceValue3 = 0.0;
         double spaceValue4 = 0.0;

         for (int i = 0; i < spaceValues.length; i++) {
            double spaceFactor = i + 1;
            double spaceValue5 = velocity.x * spaceFactor;
            double spaceValue6 = velocity.z * spaceFactor;
            if (entityPlayerMinecraftClient.world.isSpaceEmpty(player, boundingBox.offset(spaceValue5, spaceValue4, spaceValue6))) {
               spaceValue2 = spaceValue5;
               spaceValue3 = spaceValue6;
            }

            double spaceValue7 = spaceValues[i] - spaceValue4;
            if (spaceValue7 < 0.0) {
               Box stretch = boundingBox.offset(spaceValue2, spaceValue4, spaceValue3).stretch(0.0, spaceValue7, 0.0);
               if (!entityPlayerMinecraftClient.world.isSpaceEmpty(player, stretch) || entityPlayerMinecraftClient.world.containsFluid(stretch)) {
                  return i;
               }
            }

            spaceValue4 = spaceValues[i];
         }

         return spaceValues.length;
      } else {
         return 0;
      }
   }

   public static boolean canFlag(Entity entity, int entityPlayerValue) {
      return FlagInventoryUtils.canFlag(getSpaceInstance(4), canTargetingMin(entity), entityPlayerValue);
   }

   public static void updateGlidingVehicle() {
      ClientPlayerEntity player2 = entityPlayerMinecraftClient.player;
      if (player2 != player) {
         player = player2;
         y = Double.NaN;
      }

      if (player2 != null) {
         if (player2.isOnGround() || player2.fallDistance <= 0.0 || player2.isTouchingWater() || player2.hasVehicle() || player2.isGliding()) {
            y = Double.NaN;
         }
      }
   }

   public static Vec3d getRotationVector() {
      ClientPlayerEntity player = entityPlayerMinecraftClient.player;
      if (MathClampUtils.isClampActive()) {
         return MathClampUtils.getCosSin(MathClampUtils.getYaw(), MathClampUtils.getClamp());
      } else {
         return player == null ? Vec3d.ZERO : player.getRotationVector();
      }
   }

   private EntityPlayerUtilsGliding() {
   }

   public static boolean isGlidingGround() {
      ClientPlayerEntity player = entityPlayerMinecraftClient.player;
      if (player != null && !player.isOnGround()) {
         if (!FlagInventoryUtils.canFlag(getPlayerFall(), player.isGliding(), 1.5)) {
            return false;
         } else {
            y = player.getY();
            return true;
         }
      } else {
         return false;
      }
   }

   private static double getReductionsStream(BlocksAttacksComponent blocksAttacksComponent, RegistryKey<DamageType> registryKey) {
      double reductionsStreamBound = -1.0;

      for (DamageReduction damageReduction : blocksAttacksComponent.damageReductions()) {
         if ((!(damageReduction.base() <= 0.0F) || !(damageReduction.factor() <= 0.0F))
            && (!damageReduction.type().isPresent() || !damageReduction.type().get().stream().noneMatch(registryEntry -> registryEntry.matchesKey(registryKey)))) {
            reductionsStreamBound = Math.max(reductionsStreamBound, (double)damageReduction.horizontalBlockingAngle());
         }
      }

      return reductionsStreamBound < 0.0 ? Double.NaN : FlagInventoryUtils.getCos(reductionsStreamBound);
   }

   public static boolean canGlidingGround(double glidingGroundValue) {
      ClientPlayerEntity player = entityPlayerMinecraftClient.player;
      return player != null && !player.isOnGround() ? FlagInventoryUtils.canFlag(getPlayerFall(), player.isGliding(), glidingGroundValue) : false;
   }

   public static boolean canVectorRotation(Entity entity, ItemStack stack) {
      ClientPlayerEntity player = entityPlayerMinecraftClient.player;
      if (player == null || entity == null) {
         return false;
      } else {
         return entity == SilentAim.getLockedHitTarget() ? true : canRangeTargeting(player, entity, stack, player.getRotationVector());
      }
   }

   public static boolean canDelayHead(Entity entity, Vec3d vec, RegistryKey<DamageType> registryKey) {
      ClientPlayerEntity player = entityPlayerMinecraftClient.player;
      if (player == null || !(vec != null && entity instanceof LivingEntity livingEntity) || livingEntity == player) {
         return false;
      } else if (!livingEntity.isUsingItem()) {
         return false;
      } else {
         BlocksAttacksComponent activeitem = livingEntity.getActiveItem().get(DataComponentTypes.BLOCKS_ATTACKS);
         if (activeitem == null) {
            return false;
         } else {
            double reductionsStream = getReductionsStream(activeitem, registryKey);
            if (Double.isNaN(reductionsStream)) {
               return false;
            } else {
               int delayHeadValue = FlagInventoryUtils.getValue(EntityDamageUtils.getCachedHurtTimeLimit());
               if (!FlagInventoryUtils.canFlag(livingEntity.getItemUseTime(), delayHeadValue, activeitem.getBlockDelayTicks())) {
                  return false;
               } else {
                  Vec3d rotationVector = livingEntity.getRotationVector(0.0F, livingEntity.getHeadYaw());
                  Vec3d entityPos = livingEntity.getEntityPos();
                  return FlagInventoryUtils.canFlag(rotationVector.x, rotationVector.z, vec.x - entityPos.x, vec.z - entityPos.z, reductionsStream, 0.2);
               }
            }
         }
      }
   }

   public static boolean canInstancePlayer(Entity entity, ItemStack stack) {
      ClientPlayerEntity player = entityPlayerMinecraftClient.player;
      return player != null && entity != null ? canRangeTargeting(player, entity, stack, getRotationVector()) : false;
   }

   public static boolean canPlayerInstance(Entity entity) {
      return entityPlayerMinecraftClient.player != null && canDelayHead(entity, entityPlayerMinecraftClient.player.getEntityPos(), DamageTypes.PLAYER_ATTACK);
   }

   public static double getPlayerFall() {
      ClientPlayerEntity player2 = entityPlayerMinecraftClient.player;
      if (player2 == null) {
         return 0.0;
      } else {
         if (player2 != player) {
            player = player2;
            y = Double.NaN;
         }

         return FlagInventoryUtils.getAmount(player2.fallDistance, y, player2.getY());
      }
   }

   public static boolean canTargetingMin(Entity entity) {
      ClientPlayerEntity player = entityPlayerMinecraftClient.player;
      if (player != null && entity != null) {
         Vec3d add = player.getEyePos().add(player.getVelocity());
         Vec3d vec3d = new Vec3d(entity.getX() - entity.lastX, entity.getY() - entity.lastY, entity.getZ() - entity.lastZ);
         Box expand = entity.getBoundingBox().offset(vec3d).expand(entity.getTargetingMargin());
         Vec3d rotationVector = getRotationVector();
         double targetingValue = FlagInventoryUtils.getAmount(
            add.x, add.y, add.z, rotationVector.x, rotationVector.y, rotationVector.z, expand.minX, expand.minY, expand.minZ, expand.maxX, expand.maxY, expand.maxZ
         );
         return !FlagInventoryUtils.canFlag(targetingValue, player.getEntityInteractionRange() - 0.25);
      } else {
         return true;
      }
   }

   public static boolean isFlag() {
      return canGlidingGround(1.5);
   }

   private static boolean canRangeTargeting(ClientPlayerEntity player, Entity entity, ItemStack stack, Vec3d vec) {
      Vec3d eyePos = player.getEyePos();
      Box expand = entity.getBoundingBox().expand(entity.getTargetingMargin());
      double rangeTargetingValue = FlagInventoryUtils.getAmount(
         eyePos.x, eyePos.y, eyePos.z, vec.x, vec.y, vec.z, expand.minX, expand.minY, expand.minZ, expand.maxX, expand.maxY, expand.maxZ
      );
      if (rangeTargetingValue == Double.POSITIVE_INFINITY) {
         return false;
      } else {
         AttackRangeComponent attackRangeComponent = stack != null && !stack.isEmpty() ? stack.get(DataComponentTypes.ATTACK_RANGE) : null;
         return attackRangeComponent != null
            ? attackRangeComponent.isWithinRange(player, eyePos.add(vec.normalize().multiply(rangeTargetingValue)))
            : FlagInventoryUtils.canFlag(rangeTargetingValue, player.getEntityInteractionRange());
      }
   }
}
