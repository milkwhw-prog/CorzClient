package com.corz.client.utils;

import com.corz.client.inventory.BlockEntityUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import com.corz.client.utils.AStarPathfinder;
import com.corz.client.utils.BlockHazardFlags;

public final class PathfindingWorldAdapter implements AStarPathfinder.World {
   private final LongOpenHashSet crystalCellKeys;
   private long cachedTick;
   private static DynamicRegistryManager registryManager;
   private static RegistryEntry<Enchantment> featherFallingEntry;
   private World cachedWorld;
   private final Mutable scratchPos = new Mutable();

   @Override
   public int getBlockFlags(int x2, int y2, int z2) {
      World world = getClientWorld();
      if (world == null) {
         return 0;
      } else {
         int stateBlock = BlockHazardFlags.getHazardFlags(world, new BlockPos(x2, y2, z2));
         if (this.isOccupiedByCrystal(world, x2, y2, z2)) {
            stateBlock |= 1048576;
         }

         return stateBlock;
      }
   }

   static boolean isWorldAndPlayerPresent() {
      MinecraftClient instance = MinecraftClient.getInstance();
      return instance != null && instance.world != null && instance.player != null;
   }

   private boolean isOccupiedByCrystal(World world, int hashOpenValue, int y2, int z2) {
      long time = world.getTime();
      if (world != this.cachedWorld || time != this.cachedTick) {
         this.cachedWorld = world;
         this.cachedTick = time;
         this.refreshCrystalCells(world);
      }

      return this.crystalCellKeys.isEmpty()
         ? false
         : this.crystalCellKeys.contains(AStarPathfinder.packPosKey(hashOpenValue, y2, z2)) || this.crystalCellKeys.contains(AStarPathfinder.packPosKey(hashOpenValue, y2 + 1, z2));
   }

   @Override
   public double getCollisionTopY(int mutableHashValue, int mutableHashValue2, int mutableHashValue3) {
      World world = getClientWorld();
      if (world == null) {
         return Double.NaN;
      } else {
         this.scratchPos.set(mutableHashValue, mutableHashValue2, mutableHashValue3);
         BlockState blockState = world.getBlockState(this.scratchPos);
         VoxelShape collisionShape = blockState.getCollisionShape(world, this.scratchPos);
         return collisionShape.isEmpty() ? Double.NaN : mutableHashValue2 + collisionShape.getMax(Axis.Y);
      }
   }

   public static float getArmorProtectionScore(LivingEntity entity) {
      if (entity == null) {
         return 0.0F;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance != null && instance.world != null) {
            float registryEquipped = BlockEntityUtils.getRegistryEquipped(entity);
            DynamicRegistryManager dynamicRegistryManager = instance.world.getRegistryManager();
            if (dynamicRegistryManager != registryManager) {
               registryManager = dynamicRegistryManager;
               featherFallingEntry = findFeatherFallingEntry(dynamicRegistryManager);
            }

            int registryEquippedValue = featherFallingEntry == null ? 0 : EnchantmentHelper.getLevel(featherFallingEntry, entity.getEquippedStack(EquipmentSlot.FEET));
            return registryEquipped + 3.0F * registryEquippedValue;
         } else {
            return 0.0F;
         }
      }
   }

   private void refreshCrystalCells(World world) {
      this.crystalCellKeys.clear();
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity clientPlayerEntity = instance == null ? null : instance.player;
      if (clientPlayerEntity != null) {
         Box expand = clientPlayerEntity.getBoundingBox().expand(32.0, 24.0, 32.0);
         int boundingBoxCounter = 0;

         for (EndCrystalEntity endCrystalEntity : world.getEntitiesByClass(EndCrystalEntity.class, expand, Entity::isAlive)) {
            if (boundingBoxCounter++ >= 24) {
               break;
            }

            Box boundingBox = endCrystalEntity.getBoundingBox();
            int floorMinX = (int)Math.floor(boundingBox.minX);
            int boundingBoxLimit = (int)Math.floor(boundingBox.maxX - 1.0E-6);
            int floorMinY = (int)Math.floor(boundingBox.minY);
            int floor2 = (int)Math.floor(boundingBox.maxY - 1.0E-6);
            int floorMinZ = (int)Math.floor(boundingBox.minZ);
            int floor3 = (int)Math.floor(boundingBox.maxZ - 1.0E-6);

            for (int i = floorMinX; i <= boundingBoxLimit; i++) {
               for (int j = floorMinY; j <= floor2; j++) {
                  for (int k = floorMinZ; k <= floor3; k++) {
                     this.crystalCellKeys.add(AStarPathfinder.packPosKey(i, j, k));
                  }
               }
            }
         }
      }
   }

   private static RegistryEntry<Enchantment> findFeatherFallingEntry(DynamicRegistryManager dynamicRegistryManager) {
      Optional optional = dynamicRegistryManager.getOptional(RegistryKeys.ENCHANTMENT);
      if (optional.isEmpty()) {
         return null;
      } else {
         Optional optional2 = ((Registry)optional.get()).getOptional(Enchantments.FEATHER_FALLING);
         return optional2.isPresent() ? (RegistryEntry)optional2.get() : null;
      }
   }

   private static World getClientWorld() {
      MinecraftClient instance = MinecraftClient.getInstance();
      return instance == null ? null : instance.world;
   }

   @Override
   public int getBottomY() {
      World world = getClientWorld();
      return world == null ? -64 : world.getBottomY();
   }

   @Override
   public boolean hasCollision(int mutableByteValue, int mutableByteValue2, int mutableByteValue3) {
      World world = getClientWorld();
      if (world == null) {
         return false;
      } else {
         this.scratchPos.set(mutableByteValue, mutableByteValue2, mutableByteValue3);
         return !world.getBlockState(this.scratchPos).getCollisionShape(world, this.scratchPos).isEmpty();
      }
   }

   public PathfindingWorldAdapter() {
      this.crystalCellKeys = new LongOpenHashSet();
      this.cachedTick = Long.MIN_VALUE;
   }
}
