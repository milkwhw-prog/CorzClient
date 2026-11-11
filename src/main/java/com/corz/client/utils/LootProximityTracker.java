package com.corz.client.utils;

import com.corz.client.config.FriendNameStore;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.inventory.BlockEntityUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.ConnectionTimeUtils;

public final class LootProximityTracker {
   private final Deque<LootProximityTracker.Death> recentDeaths = new ArrayDeque<>();
   private final List<Vec3d> lootPositions;
   private long excludedCenterTime = Long.MIN_VALUE;
   private long lastRefreshTick;
   private Vec3d lastPlayerPos;
   private Object lastWorld;
   private Vec3d excludedCenter;
   private final List<Box> nearbyPlayerBoxes;
   private boolean deathTrackingEnabled = true;

   public void setExcludedCenter(Vec3d vec3d, long time) {
      if (vec3d != null) {
         this.excludedCenter = vec3d;
         this.excludedCenterTime = time;
         this.lastRefreshTick = Long.MIN_VALUE;
      }
   }

   public boolean isNearLootOrPlayers(Vec3d vec) {
      if (vec == null) {
         return true;
      } else {
         this.refreshNearbyEntities();

         for (Box box : this.nearbyPlayerBoxes) {
            if (BlockEntityUtils.getMinMax(vec, box) <= 7.0) {
               return true;
            }
         }

         if (!this.deathTrackingEnabled) {
            return false;
         } else {
            for (Vec3d vec3d : this.lootPositions) {
               if (isWithinLootRange(vec3d, vec)) {
                  return true;
               }
            }

            long dequeValue = ConnectionTimeUtils.getTimeTimeMillis();

            for (LootProximityTracker.Death death : this.recentDeaths) {
               if (dequeValue - death.atMs() <= 2000L && isWithinLootRange(death.pos(), vec)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   static boolean isValuableItem(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         Item item = itemStack.getItem();
         return item != Items.TOTEM_OF_UNDYING && item != Items.END_CRYSTAL && item != Items.SHEARS
            ? item.getComponents().contains(DataComponentTypes.EQUIPPABLE) || item.getComponents().contains(DataComponentTypes.TOOL)
            : true;
      } else {
         return false;
      }
   }

   public Vec3d getNearestLoot(Vec3d vec, double distanceLimit) {
      if (vec != null && distanceLimit > 0.0) {
         this.refreshNearbyEntities();
         long distanceValue = ConnectionTimeUtils.getTimeTimeMillis();
         this.pruneOldDeaths(distanceValue);
         Vec3d distance = null;
         double positiveInfinity = Double.POSITIVE_INFINITY;

         for (Vec3d vec3d : this.lootPositions) {
            double distanceTo = vec3d.distanceTo(vec);
            if (!(distanceTo > distanceLimit)) {
               double distanceValue2 = distanceTo - (this.isNearRecentDeath(vec3d, distanceValue) ? 8.0 : 0.0);
               if (distanceValue2 < positiveInfinity) {
                  positiveInfinity = distanceValue2;
                  distance = vec3d;
               }
            }
         }

         return distance;
      } else {
         return null;
      }
   }

   private boolean isInsideExcludedZone(Vec3d vec, long vecSquaredValue) {
      return this.excludedCenter != null && this.excludedCenterTime != Long.MIN_VALUE && vecSquaredValue - this.excludedCenterTime <= 300000L ? vec.squaredDistanceTo(this.excludedCenter) <= 100.0 : false;
   }

   public void recordDeath(Vec3d vec, long dequeValue) {
      if (vec != null) {
         this.recentDeaths.addLast(new LootProximityTracker.Death(vec, dequeValue));

         while (this.recentDeaths.size() > 16) {
            this.recentDeaths.removeFirst();
         }

         this.lastRefreshTick = Long.MIN_VALUE;
      }
   }

   public LootProximityTracker() {
      this.lastRefreshTick = Long.MIN_VALUE;
      this.lootPositions = new ArrayList<>();
      this.nearbyPlayerBoxes = new ArrayList<>();
   }

   private boolean isNearRecentDeath(Vec3d vec, long dequePosValue) {
      for (LootProximityTracker.Death death : this.recentDeaths) {
         if (dequePosValue - death.atMs() <= 60000L && isWithinLootRange(death.pos(), vec)) {
            return true;
         }
      }

      return false;
   }

   public void clearAll() {
      this.recentDeaths.clear();
      this.excludedCenter = null;
      this.excludedCenterTime = Long.MIN_VALUE;
      this.lastRefreshTick = Long.MIN_VALUE;
      this.lastWorld = null;
      this.lastPlayerPos = null;
      this.lootPositions.clear();
      this.nearbyPlayerBoxes.clear();
   }

   public boolean isDeathTrackingEnabled() {
      return this.deathTrackingEnabled;
   }

   public void setDeathTrackingEnabled(boolean flag) {
      if (this.deathTrackingEnabled != flag) {
         this.lastRefreshTick = Long.MIN_VALUE;
      }

      this.deathTrackingEnabled = flag;
   }

   public boolean isNearLoot(Vec3d vec) {
      if (vec == null) {
         return false;
      } else {
         this.refreshNearbyEntities();

         for (Vec3d vec3d : this.lootPositions) {
            if (isWithinLootRange(vec3d, vec)) {
               return true;
            }
         }

         return false;
      }
   }

   private void pruneOldDeaths(long dequeValue) {
      Iterator iterator = this.recentDeaths.iterator();

      while (iterator.hasNext()) {
         if (dequeValue - ((LootProximityTracker.Death)iterator.next()).atMs() > 60000L) {
            iterator.remove();
         }
      }
   }

   private void refreshNearbyEntities() {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      ClientWorld world = instance.world;
      long time = ClientThreadUtils.getTime();
      if (player != null && world != null) {
         Vec3d entityPos = player.getEntityPos();
         if (this.lastRefreshTick != time || this.lastWorld != world || this.lastPlayerPos == null || !(this.lastPlayerPos.squaredDistanceTo(entityPos) < 1.0)) {
            this.lastRefreshTick = time;
            this.lastWorld = world;
            this.lastPlayerPos = entityPos;
            this.lootPositions.clear();
            this.nearbyPlayerBoxes.clear();
            long entityPosValue = ConnectionTimeUtils.getTimeTimeMillis();
            Box expand = player.getBoundingBox().expand(24.0);

            for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, expand, itemEntity2 -> itemEntity2.isAlive() && !itemEntity2.isRemoved())) {
               if (isValuableItem(itemEntity.getStack()) && !this.isInsideExcludedZone(itemEntity.getEntityPos(), entityPosValue)) {
                  this.lootPositions.add(itemEntity.getEntityPos());
               }
            }

            for (PlayerEntity playerEntity : world.getPlayers()) {
               if (playerEntity != player
                  && playerEntity.isAlive()
                  && !playerEntity.isRemoved()
                  && !playerEntity.isSpectator()
                  && !(playerEntity.squaredDistanceTo(player) > 961.0)
                  && (FriendNameStore.canFlag(playerEntity) || EntityPlayerUtilsLiving.canScoreboardTeam(playerEntity))) {
                  this.nearbyPlayerBoxes.add(playerEntity.getBoundingBox());
               }
            }
         }
      } else {
         this.lootPositions.clear();
         this.nearbyPlayerBoxes.clear();
         this.lastRefreshTick = time;
         this.lastWorld = null;
      }
   }

   private static boolean isWithinLootRange(Vec3d vec, Vec3d vec2) {
      double deltaX = vec.x - vec2.x;
      double deltaZ = vec.z - vec2.z;
      return deltaX * deltaX + deltaZ * deltaZ <= 64.0 && Math.abs(vec.y - vec2.y) <= 4.0;
   }

   private record Death(Vec3d pos, long atMs) {
   }
}
