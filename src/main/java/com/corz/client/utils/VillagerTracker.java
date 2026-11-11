package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.TrackedVillager;

public final class VillagerTracker {
   private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
   private final Map<UUID, TrackedVillager> trackedVillagers = new HashMap<>();

   public List<TrackedVillager> getAllVillagers() {
      return new ArrayList<>(this.trackedVillagers.values());
   }

   public TrackedVillager getOrCreate(UUID uUID) {
      return this.trackedVillagers.computeIfAbsent(uUID, TrackedVillager::new);
   }

   public int getTrackedCount() {
      return this.trackedVillagers.size();
   }

   public void scanNearbyVillagers(long uuidValue, double uuidFactor) {
      if (CLIENT.world != null && CLIENT.player != null) {
         double uuidValue2 = uuidFactor * uuidFactor;

         for (Entity entity : CLIENT.world.getEntities()) {
            if (entity instanceof VillagerEntity villagerEntity && villagerEntity.isAlive() && !villagerEntity.isRemoved() && !(villagerEntity.squaredDistanceTo(CLIENT.player) > uuidValue2)) {
               this.trackedVillagers.computeIfAbsent(villagerEntity.getUuid(), TrackedVillager::new).updateFromEntity(villagerEntity, uuidValue);
            }
         }

         this.trackedVillagers.values().removeIf(villagerExperience -> uuidValue - villagerExperience.lastSeenTick > 600L);
      }
   }

   public TrackedVillager findByUuid(UUID uUID) {
      return uUID == null ? null : this.trackedVillagers.get(uUID);
   }

   public List<TrackedVillager> getSortedByDistance() {
      List instanceZero = this.getAllVillagers();
      Vec3d vec3d = CLIENT.player != null ? CLIENT.player.getEntityPos() : Vec3d.ZERO;
      instanceZero.sort((obj, obj2) -> Double.compare(((com.corz.client.utils.TrackedVillager) obj).entityPos.squaredDistanceTo(vec3d), ((com.corz.client.utils.TrackedVillager) obj2).entityPos.squaredDistanceTo(vec3d)));
      return instanceZero;
   }

   public VillagerEntity findVillagerEntity(UUID uUID) {
      if (CLIENT.world != null && uUID != null) {
         TrackedVillager villagerExperience = this.trackedVillagers.get(uUID);
         if (villagerExperience != null && CLIENT.world.getEntityById(villagerExperience.entityId) instanceof VillagerEntity byId && uUID.equals(byId.getUuid())) {
            return byId;
         } else if (CLIENT.world.getEntity(uUID) instanceof VillagerEntity byId2) {
            if (villagerExperience != null) {
               villagerExperience.entityId = byId2.getId();
            }

            return byId2;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void clear() {
      this.trackedVillagers.clear();
   }
}
