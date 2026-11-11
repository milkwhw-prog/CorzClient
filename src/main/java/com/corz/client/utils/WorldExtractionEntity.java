package com.corz.client.utils;

import com.mojang.authlib.GameProfile;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

public final class WorldExtractionEntity extends OtherClientPlayerEntity {
   private static volatile WorldExtractionEntity instance2Inventory;
   private static boolean extractionActive;
   private final ClientWorld world;

   public static void updateInstance() {
      instance2Inventory = null;
   }

   public static boolean canInstanceWorld(ClientWorld clientWorld) {
      WorldExtractionEntity instance2Inventory2 = instance2Inventory;
      return instance2Inventory2 != null && clientWorld != null && instance2Inventory2.world == clientWorld;
   }

   private WorldExtractionEntity(ClientWorld world2, GameProfile gameProfile) {
      super(world2, gameProfile);
      this.world = world2;
   }

   private static void handleRenderCamera(WorldExtractionContext worldExtractionContext) {
      WorldExtractionEntity instance2Inventory2 = instance2Inventory;
      if (instance2Inventory2 != null && !DirExtractionUtils.isFlag() && worldExtractionContext.world() == instance2Inventory2.world) {
         EntityRenderManager entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
         Vec3d cameraPos = worldExtractionContext.camera().getCameraPos();
         if (entityRenderDispatcher.shouldRender(instance2Inventory2, worldExtractionContext.frustum(), cameraPos.x, cameraPos.y, cameraPos.z)) {
            worldExtractionContext.worldState().entityRenderStates.add(entityRenderDispatcher.getAndUpdateRenderState(instance2Inventory2, worldExtractionContext.tickCounter().getTickProgress(true)));
         }
      }
   }

   private void handleYawRender(Vec3d vec, float inventoryValue, float pitch2, float lastHeadYaw, float lastBodyYaw) {
      this.refreshPositionAndAngles(vec.x, vec.y, vec.z, inventoryValue, pitch2);
      this.lastRenderX = vec.x;
      this.lastRenderY = vec.y;
      this.lastRenderZ = vec.z;
      this.setHeadYaw(lastHeadYaw);
      this.lastHeadYaw = lastHeadYaw;
      this.setBodyYaw(lastBodyYaw);
      this.lastBodyYaw = lastBodyYaw;
      this.setVelocity(Vec3d.ZERO);
   }

   private static int getEntity(ClientWorld clientWorld) {
      ThreadLocalRandom current = ThreadLocalRandom.current();

      for (int i = 0; i < 32; i++) {
         int entity = -(1 + current.nextInt(99999));
         if (clientWorld.getEntityById(entity) == null) {
            return entity;
         }
      }

      return 0;
   }

   private static void updateRegisterExtraction() {
      if (!extractionActive) {
         extractionActive = true;
         WorldRenderEvents.END_EXTRACTION.register(WorldExtractionEntity::handleRenderCamera);
         ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> {
            WorldExtractionEntity instance2Inventory2 = instance2Inventory;
            if (instance2Inventory2 != null) {
               client.execute(() -> {
                  if (instance2Inventory == instance2Inventory2) {
                     instance2Inventory = null;
                  }
               });
            }
         });
      }
   }

   public static void handleInventoryInstance(ClientWorld clientWorld, ClientPlayerEntity clientPlayerEntity, Vec3d vec, float inventoryValue, float inventoryValue2, float lastHeadYaw, float lastBodyYaw) {
      if (clientWorld != null && clientPlayerEntity != null && vec != null) {
         updateRegisterExtraction();
         WorldExtractionEntity instance2Inventory2 = instance2Inventory;
         if (instance2Inventory2 == null || instance2Inventory2.world != clientWorld) {
            int entity = getEntity(clientWorld);
            if (entity == 0) {
               instance2Inventory = null;
               return;
            }

            instance2Inventory2 = new WorldExtractionEntity(clientWorld, clientPlayerEntity.getGameProfile());
            instance2Inventory2.setId(entity);
         }

         instance2Inventory2.getInventory().clone(clientPlayerEntity.getInventory());
         instance2Inventory2.handleYawRender(vec, inventoryValue, inventoryValue2, lastHeadYaw, lastBodyYaw);
         instance2Inventory = instance2Inventory2;
      }
   }
}
