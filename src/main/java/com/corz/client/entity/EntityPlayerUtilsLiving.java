package com.corz.client.entity;

import com.corz.client.config.FriendNameStore;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.categories.combat.SilentAim;
import com.corz.client.utils.TextStyleUtils;

public final class EntityPlayerUtilsLiving {
   private static final MinecraftClient entityPlayerMinecraftClient = MinecraftClient.getInstance();
   private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

   public static LivingEntity getLivingEntity(double livingEntityValue, double styleLimit, EntityPlayerUtilsLiving.Options options) {
      LivingEntity livingEntity = null;
      double maxValue = Double.MAX_VALUE;
      double livingEntityLimit = Double.MAX_VALUE;

      for (LivingEntity entity : getInstanceWorld(livingEntityValue, options)) {
         double boundingBox = getBoundingBox(entity);
         if (TextStyleUtils.canFlag(boundingBox, styleLimit)) {
            double minMax = getMinMax(entity);
            if (boundingBox < maxValue - 1.0E-6 || Math.abs(boundingBox - maxValue) <= 1.0E-6 && minMax < livingEntityLimit) {
               livingEntity = entity;
               maxValue = boundingBox;
               livingEntityLimit = minMax;
            }
         }
      }

      return livingEntity;
   }

   public static boolean canScoreboardTeam(PlayerEntity player) {
      ClientPlayerEntity player2 = entityPlayerMinecraftClient.player;
      return player2 != null && player != null && player != player2 ? TextStyleUtils.canFriendlyEqual(player2.getScoreboardTeam(), player.getScoreboardTeam()) : false;
   }

   public static double getMinMax(Entity entity) {
      if (entityPlayerMinecraftClient.player != null && entity != null) {
         Vec3d eyePos = entityPlayerMinecraftClient.player.getEyePos();
         Box boundingBox = entity.getBoundingBox();
         return TextStyleUtils.getAmount(eyePos.x, eyePos.y, eyePos.z, boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
      } else {
         return Double.MAX_VALUE;
      }
   }

   public static boolean canFlag(Entity entity) {
      return entity instanceof Monster || entity instanceof SlimeEntity || entity instanceof GhastEntity || entity instanceof EnderDragonEntity;
   }

   public static double getInstanceEye(Vec3d vec) {
      if (entityPlayerMinecraftClient.player != null && vec != null) {
         Vec3d eyePos = entityPlayerMinecraftClient.player.getEyePos();
         return TextStyleUtils.getAmount(SilentAim.getYawSilent(), SilentAim.getSilentPitch(), vec.x - eyePos.x, vec.y - eyePos.y, vec.z - eyePos.z);
      } else {
         return 180.0;
      }
   }

   public static double getBoundingBox(Entity entity) {
      return entity == null ? 180.0 : getInstanceEye(entity.getBoundingBox().getCenter());
   }

   public static boolean canSpectatorDead(Entity entity, EntityPlayerUtilsLiving.Options options) {
      if (entity != null && options != null && entityPlayerMinecraftClient.player != null && entity != entityPlayerMinecraftClient.player) {
         if (!(entity instanceof LivingEntity livingEntity && !(entity instanceof ArmorStandEntity))) {
            return false;
         } else if (!livingEntity.isAlive() || livingEntity.isRemoved() || livingEntity.isDead() || livingEntity.getHealth() <= 0.0F || entity.isSpectator()) {
            return false;
         } else if (!options.invisibles() && canStackEmpty(livingEntity)) {
            return false;
         } else if (FriendNameStore.canFlag(entity)) {
            return false;
         } else if (!(entity instanceof PlayerEntity playerEntity)) {
            return canFlag(entity) ? options.mobs() : options.animals();
         } else if (!options.players()) {
            return false;
         } else {
            return !options.bots() && canUuidNetwork(playerEntity) ? false : options.teammates() || !canScoreboardTeam(playerEntity);
         }
      } else {
         return false;
      }
   }

   public static boolean canFlag(Entity entity, double entityPlayerValue) {
      return entity != null && TextStyleUtils.canFlag(getBoundingBox(entity), entityPlayerValue);
   }

   private EntityPlayerUtilsLiving() {
   }

   public static boolean canStackEmpty(LivingEntity entity) {
      if (entity.isInvisible() && entity.getMainHandStack().isEmpty()) {
         for (EquipmentSlot equipmentSlot : equipmentSlots) {
            if (!entity.getEquippedStack(equipmentSlot).isEmpty()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static Integer getColorValue(Team team) {
      if (team == null) {
         return null;
      } else {
         Formatting color = team.getColor();
         return color == null ? null : color.getColorValue();
      }
   }

   public static boolean canSeeBounding(Entity entity) {
      return entityPlayerMinecraftClient.player != null && entity != null ? entityPlayerMinecraftClient.player.canSee(entity) || EntityEyeUtils.canInstanceSquared(entity.getBoundingBox().getCenter()) : false;
   }

   public static Integer getScoreboardTeam(PlayerEntity player) {
      if (player == null) {
         return null;
      } else {
         ClientPlayNetworkHandler networkHandler = entityPlayerMinecraftClient.getNetworkHandler();
         if (networkHandler != null) {
            PlayerListEntry playerListEntry = networkHandler.getPlayerListEntry(player.getUuid());
            if (playerListEntry != null) {
               Text displayName = playerListEntry.getDisplayName();
               Integer scoreboardTeam = TextStyleUtils.getNumber(displayName, player.getName().getString());
               if (scoreboardTeam != null) {
                  return scoreboardTeam;
               }

               Integer colorValue = getColorValue(playerListEntry.getScoreboardTeam());
               if (colorValue != null) {
                  return colorValue;
               }
            }
         }

         return getColorValue(player.getScoreboardTeam());
      }
   }

   public static boolean canUuidNetwork(PlayerEntity player) {
      if (player == null || player == entityPlayerMinecraftClient.player) {
         return false;
      } else if (TextStyleUtils.canFlag(player.getUuid())) {
         return true;
      } else {
         ClientPlayNetworkHandler networkHandler = entityPlayerMinecraftClient.getNetworkHandler();
         return networkHandler != null && networkHandler.getPlayerListEntry(player.getUuid()) == null;
      }
   }

   public static List<LivingEntity> getInstanceWorld(double worldLimit, EntityPlayerUtilsLiving.Options options) {
      ArrayList arrayList = new ArrayList();
      if (entityPlayerMinecraftClient.world != null && entityPlayerMinecraftClient.player != null && options != null) {
         for (Entity entity : !options.mobs() && !options.animals() ? entityPlayerMinecraftClient.world.getPlayers() : entityPlayerMinecraftClient.world.getEntities()) {
            if (canSpectatorDead(entity, options) && getMinMax(entity) <= worldLimit) {
               arrayList.add((LivingEntity)entity);
            }
         }

         return arrayList;
      } else {
         return arrayList;
      }
   }

   public static LivingEntity getLivingEntity(double livingEntityValue, EntityPlayerUtilsLiving.Options options) {
      LivingEntity livingEntity = null;
      double livingEntityLimit = Double.MAX_VALUE;

      for (LivingEntity entity : getInstanceWorld(livingEntityValue, options)) {
         double minMax = getMinMax(entity);
         if (minMax < livingEntityLimit) {
            livingEntity = entity;
            livingEntityLimit = minMax;
         }
      }

      return livingEntity;
   }

   public record Options(boolean players, boolean mobs, boolean animals, boolean invisibles, boolean teammates, boolean bots) {
      public static final EntityPlayerUtilsLiving.Options INSTANCE2 = new EntityPlayerUtilsLiving.Options(true, false, false, false, false, false);
      public static final EntityPlayerUtilsLiving.Options INSTANCE = new EntityPlayerUtilsLiving.Options(true, true, false, false, false, false);

      public EntityPlayerUtilsLiving.Options getInvisiblesAnimals(boolean v) {
         return new EntityPlayerUtilsLiving.Options(v, this.mobs, this.animals, this.invisibles, this.teammates, this.bots);
      }

      public EntityPlayerUtilsLiving.Options getInvisiblesAnimalsForFlag(boolean v) {
         return new EntityPlayerUtilsLiving.Options(this.players, this.mobs, this.animals, this.invisibles, v, this.bots);
      }

      public EntityPlayerUtilsLiving.Options getAnimalsTeammates(boolean v) {
         return new EntityPlayerUtilsLiving.Options(this.players, this.mobs, this.animals, v, this.teammates, this.bots);
      }

      public EntityPlayerUtilsLiving.Options getInvisiblesTeammates(boolean v) {
         return new EntityPlayerUtilsLiving.Options(this.players, this.mobs, v, this.invisibles, this.teammates, this.bots);
      }

      public EntityPlayerUtilsLiving.Options getInvisiblesAnimalsPlayers(boolean v) {
         return new EntityPlayerUtilsLiving.Options(this.players, v, this.animals, this.invisibles, this.teammates, this.bots);
      }

      public EntityPlayerUtilsLiving.Options getInvisiblesAnimalsOptions(boolean v) {
         return new EntityPlayerUtilsLiving.Options(this.players, this.mobs, this.animals, this.invisibles, this.teammates, v);
      }
   }
}
