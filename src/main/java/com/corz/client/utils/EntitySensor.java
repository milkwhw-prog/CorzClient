package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractChestBoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.world.Heightmap.Type;
import com.corz.client.utils.SiteBook;
import com.corz.client.utils.Clue;

public final class EntitySensor {
   private final Set<UUID> farmLeashedUuidSet = new HashSet<>();

   static void handleHallTrading(List<EntitySensor.Spot> list, SiteBook siteBook, long hallTradingValue) {
      var arrayList = new ArrayList();

      for (EntitySensor.Spot spot : list) {
         if (!canFlag(arrayList, spot, 20)) {
            int hallTradingCounter = 0;
            int hallTradingCounter2 = 0;

            for (EntitySensor.Spot spot2 : list) {
               if (Math.abs(spot2.x - spot.x) <= 10 && Math.abs(spot2.z - spot.z) <= 10 && Math.abs(spot2.y - spot.y) <= 4) {
                  hallTradingCounter++;
                  if (spot2.traded) {
                     hallTradingCounter2++;
                  }
               }
            }

            if (hallTradingCounter >= 4) {
               siteBook.canTimeJoin(
                  Clue.TRADING_HALL,
                  spot.x,
                  spot.y,
                  spot.z,
                  "hall:" + (spot.x >> 4) + "," + (spot.z >> 4),
                  Clue.TRADING_HALL.clueWeight + (hallTradingCounter2 >= 2 ? 5 : 0),
                  hallTradingCounter + " working villagers",
                  hallTradingValue
               );
               arrayList.add(spot);
            }
         }
      }
   }

   private static boolean isShowArms(ArmorStandEntity armorStandEntity) {
      for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
         if (equipmentSlot != EquipmentSlot.BODY && equipmentSlot != EquipmentSlot.SADDLE && !armorStandEntity.getEquippedStack(equipmentSlot).isEmpty()) {
            return true;
         }
      }

      return armorStandEntity.shouldShowArms();
   }

   public void handleFarmLeashed(ClientWorld clientWorld, ClientPlayerEntity player, SiteBook siteBook, boolean farmLeashedFlag, boolean farmLeashedFlag2, long farmLeashedValue) {
      double x = player.getX();
      double z = player.getZ();
      if (player.getVehicle() != null && this.farmLeashedUuidSet.size() < 64) {
         this.farmLeashedUuidSet.add(player.getVehicle().getUuid());
      }

      double farmLeashedValue2 = 36864.0;
      var arrayList = new ArrayList();
      var arrayList2 = new ArrayList();
      var hashMap = new HashMap<EntityType<?>, List<EntitySensor.Spot>>();

      for (Entity entity : clientWorld.getEntities()) {
         if (entity != player && entity.isAlive() && !(entity instanceof PlayerEntity)) {
            double farmLeashedFactor = entity.getX() - x;
            double farmLeashedFactor2 = entity.getZ() - z;
            if (!(farmLeashedFactor * farmLeashedFactor + farmLeashedFactor2 * farmLeashedFactor2 > farmLeashedValue2)) {
               int blockX = entity.getBlockX();
               int blockY = entity.getBlockY();
               int blockZ = entity.getBlockZ();
               if (!this.canPassengerHolder(entity, player)) {
                  String uuidAsString = entity.getUuidAsString();
                  boolean farmLeashedFlag3 = !farmLeashedFlag2 || canTopMotion(clientWorld, blockX, blockY, blockZ);
                  if (entity instanceof VillagerEntity villagerEntity) {
                     boolean farmLeashedFlag4 = villagerEntity.getVillagerData().level() >= 2;
                     if (farmLeashedFlag3 && canNitwitNone(villagerEntity)) {
                        arrayList.add(new EntitySensor.Spot(blockX, blockY, blockZ, farmLeashedFlag4));
                     }

                     if (farmLeashedFlag4) {
                        siteBook.canFlag(Clue.TRADED_VILLAGER, blockX, blockY, blockZ, "vil:" + uuidAsString, farmLeashedValue);
                     }
                  } else if (entity instanceof IronGolemEntity) {
                     if (farmLeashedFlag3) {
                        arrayList2.add(new EntitySensor.Spot(blockX, blockY, blockZ, false));
                     }
                  } else if (entity instanceof ItemFrameEntity itemFrameEntity) {
                     if (itemFrameEntity.getHeldItemStack().isEmpty() || farmLeashedFlag && itemFrameEntity.getHeldItemStack().isOf(Items.ELYTRA)) {
                        continue;
                     }

                     siteBook.canFlag(Clue.ITEM_FRAME, blockX, blockY, blockZ, "frame:" + uuidAsString, farmLeashedValue);
                  } else if (entity instanceof ArmorStandEntity armorStandEntity) {
                     if (armorStandEntity.isMarker()) {
                        continue;
                     }

                     siteBook.canTimeJoin(Clue.ARMOR_STAND, blockX, blockY, blockZ, "stand:" + uuidAsString, isShowArms(armorStandEntity) ? Clue.ARMOR_STAND.clueWeight : 3, null, farmLeashedValue);
                  } else if (entity instanceof StorageMinecartEntity && !(entity instanceof ChestMinecartEntity) || entity instanceof AbstractChestBoatEntity) {
                     siteBook.canFlag(Clue.STORAGE_CART, blockX, blockY, blockZ, "cart:" + uuidAsString, farmLeashedValue);
                  }

                  if (entity instanceof TameableEntity tameableEntity && tameableEntity.isTamed() || entity instanceof AbstractHorseEntity abstractHorseEntity && abstractHorseEntity.isTame()) {
                     siteBook.canFlag(Clue.PET, blockX, blockY, blockZ, "pet:" + uuidAsString, farmLeashedValue);
                  } else if (entity instanceof MobEntity mobEntity && mobEntity.hasCustomName() && !(entity instanceof VillagerEntity)) {
                     siteBook.canFlag(Clue.NAMED_MOB, blockX, blockY, blockZ, "name:" + uuidAsString, farmLeashedValue);
                  }

                  if (entity instanceof Leashable leashable && leashable.isLeashed()) {
                     siteBook.canFlag(Clue.LEASHED, blockX, blockY, blockZ, "leash:" + uuidAsString, farmLeashedValue);
                  }

                  if (entity instanceof AnimalEntity && !(entity instanceof TameableEntity)) {
                     hashMap.computeIfAbsent(entity.getType(), entityType -> new ArrayList<>()).add(new EntitySensor.Spot(blockX, blockY, blockZ, false));
                  }
               }
            }
         }
      }

      handleHallTrading(arrayList, siteBook, farmLeashedValue);
      handle(arrayList2, 3, 16, Clue.IRON_FARM, "farm:", siteBook, farmLeashedValue);

      for (Entry entry : hashMap.entrySet()) {
         handle((List<EntitySensor.Spot>)entry.getValue(), 12, 10, Clue.ANIMAL_PEN, "pen:" + EntityType.getId((EntityType<?>)entry.getKey()) + ":", siteBook, farmLeashedValue);
      }
   }

   static boolean canTopMotion(ClientWorld clientWorld, int topMotionValue, int blockY, int blockZ) {
      int topY = clientWorld.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, topMotionValue, blockZ);
      if (topY <= clientWorld.getBottomY()) {
         topY = clientWorld.getTopY(Type.MOTION_BLOCKING, topMotionValue, blockZ);
      }

      return blockY <= 0 && topY - blockY >= 4;
   }

   private static boolean canFlag(List<EntitySensor.Spot> list, EntitySensor.Spot spot, int entitySensorLimit) {
      for (EntitySensor.Spot spot2 : list) {
         if (Math.abs(spot2.x - spot.x) <= entitySensorLimit && Math.abs(spot2.z - spot.z) <= entitySensorLimit) {
            return true;
         }
      }

      return false;
   }

   static void handle(List<EntitySensor.Spot> list, int entitySensorValue, int entitySensorLimit, Clue clue, String entitySensorText, SiteBook siteBook, long farmLeashedValue) {
      if (list.size() >= entitySensorValue) {
         var arrayList = new ArrayList();

         for (EntitySensor.Spot spot : list) {
            if (!canFlag(arrayList, spot, entitySensorLimit * 2)) {
               int entitySensorCounter = 0;

               for (EntitySensor.Spot spot2 : list) {
                  if (Math.abs(spot2.x - spot.x) <= entitySensorLimit && Math.abs(spot2.z - spot.z) <= entitySensorLimit && Math.abs(spot2.y - spot.y) <= entitySensorLimit) {
                     entitySensorCounter++;
                  }
               }

               if (entitySensorCounter >= entitySensorValue) {
                  siteBook.canFlag(clue, spot.x, spot.y, spot.z, entitySensorText + (spot.x >> 4) + "," + (spot.z >> 4), farmLeashedValue);
                  arrayList.add(spot);
               }
            }
         }
      }
   }

   private boolean canPassengerHolder(Entity entity, ClientPlayerEntity player) {
      if (!entity.hasPassenger(player) && player.getVehicle() != entity && !this.farmLeashedUuidSet.contains(entity.getUuid())) {
         return entity instanceof TameableEntity tameableEntity && tameableEntity.isTamed() && tameableEntity.isOwner(player)
            ? true
            : entity instanceof Leashable leashable && leashable.isLeashed() && leashable.getLeashHolder() == player;
      } else {
         return true;
      }
   }

   private static boolean canNitwitNone(VillagerEntity villagerEntity) {
      String idAsString = villagerEntity.getVillagerData().profession().getIdAsString();
      return !idAsString.endsWith(":none") && !idAsString.endsWith(":nitwit");
   }

   public void update() {
      this.farmLeashedUuidSet.clear();
   }

   record Spot(int x, int y, int z, boolean traded) {
   }
}
