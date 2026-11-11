package com.corz.client.utils;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import com.corz.client.utils.GlidePhysics;

public final class LandingPlanner {
   private final GlidePhysics.Body body;
   private static final float[] COARSE_PITCH_ANGLES = new float[]{-28.0F, -18.0F, -10.0F, -4.0F, 0.0F, 4.0F, 8.0F, 12.0F, 16.0F, 21.0F, 27.0F, 34.0F};
   public double lastPlanScore;
   public long planDurationNanos;
   public float bestYaw;
   private static final float[] COARSE_YAW_OFFSETS = new float[]{0.0F, -18.0F, 18.0F, -36.0F, 36.0F, -60.0F, 60.0F, -90.0F, 90.0F};
   private boolean landedOnTarget;
   public boolean bestLandsOnTarget;
   public float bestPitch;
   private static final float[] SWITCH_YAW_OFFSETS = new float[]{0.0F, -18.0F, 18.0F};
   private static final float[] SWITCH_EARLY_PITCHES = new float[]{-4.0F, 0.0F, 4.0F, 8.0F, 12.0F, 16.0F};
   private static final int[] PITCH_SWITCH_TICKS = new int[]{10, 25, 45};
   private static final float[] SWITCH_LATE_PITCHES = new float[]{-28.0F, -18.0F};
   private final GlidePhysics.Body simBody = new GlidePhysics.Body();

   private double simulateLandingCost(
      MinecraftClient client,
      float floorBodyValue,
      float coarsePitchAnglesItem,
      Vec3d vec,
      int floorBodyLimit,
      float floorBodyValue3,
      int floorBodyBound,
      double sinPitchFactor,
      LandingPlanner.FloorCheck floorCheck
   ) {
      this.landedOnTarget = false;
      GlidePhysics.Body pitchYaw = this.simBody.copyFrom(this.body);
      int maxWantYaw = Math.max(0, floorBodyBound);
      double deltaX = vec.x - pitchYaw.x;
      double deltaZ = vec.z - pitchYaw.positionZ;
      double hypot = Math.hypot(deltaX, deltaZ);
      double floorBodyFactor;
      double floorBodyFactor2;
      if (hypot > 0.001) {
         floorBodyFactor = deltaX / hypot;
         floorBodyFactor2 = deltaZ / hypot;
      } else {
         floorBodyFactor = 0.0;
         floorBodyFactor2 = 0.0;
      }

      double floorBodyTotal = 0.0;

      for (int i = 0; i < 90; i++) {
         pitchYaw.yaw = GlidePhysics.rotateToward(pitchYaw.yaw, floorBodyValue, 300.0F);
         float floorBodyValue5 = floorBodyLimit >= 0 && i >= floorBodyLimit ? floorBodyValue3 : coarsePitchAnglesItem;
         pitchYaw.pitch = GlidePhysics.stepPitchToward(pitchYaw.pitch, floorBodyValue5);
         double hypot2 = Math.hypot(pitchYaw.velocityX, pitchYaw.velocityZ);
         GlidePhysics.applyGlideTick(pitchYaw, sinPitchFactor);
         Vec3d vec3d = new Vec3d(pitchYaw.velocityX, pitchYaw.velocityY, pitchYaw.velocityZ);
         Vec3d worldCollide = getCollisionAdjustedMove(client, vec3d, getGlidingHitbox(pitchYaw.x, pitchYaw.y, pitchYaw.positionZ));
         boolean floorBodyFlag = worldCollide.x != vec3d.x;
         boolean floorBodyFlag2 = worldCollide.z != vec3d.z;
         boolean floorBodyFlag3 = worldCollide.y != vec3d.y;
         double y2 = pitchYaw.velocityY;
         if (floorBodyFlag || floorBodyFlag2) {
            double hypot3 = Math.hypot(floorBodyFlag ? 0.0 : pitchYaw.velocityX, floorBodyFlag2 ? 0.0 : pitchYaw.velocityZ);
            double floorBodyLimit2 = (hypot2 - hypot3) * 10.0 - 3.0;
            if (floorBodyLimit2 > 0.0) {
               return 1000.0 + floorBodyLimit2 * 60.0;
            }

            floorBodyTotal += 12.0;
         }

         pitchYaw.x = pitchYaw.x + worldCollide.x;
         pitchYaw.y = pitchYaw.y + worldCollide.y;
         pitchYaw.positionZ = pitchYaw.positionZ + worldCollide.z;
         if (floorBodyFlag) {
            pitchYaw.velocityX = 0.0;
         }

         if (floorBodyFlag3) {
            pitchYaw.velocityY = 0.0;
         }

         if (floorBodyFlag2) {
            pitchYaw.velocityZ = 0.0;
         }

         if (maxWantYaw > 0 && !floorBodyFlag3) {
            GlidePhysics.applyRocketBoost(pitchYaw);
            maxWantYaw--;
         }

         if (floorBodyFlag3) {
            if (y2 > 0.0) {
               return 600 + i;
            }

            double hypot4 = Math.hypot(pitchYaw.velocityX, pitchYaw.velocityZ);
            double hypot5 = Math.hypot(pitchYaw.x - vec.x, pitchYaw.positionZ - vec.z);
            int floor = MathHelper.floor(pitchYaw.x);
            int floor7 = MathHelper.floor(pitchYaw.y + 0.01);
            int floor8 = MathHelper.floor(pitchYaw.positionZ);
            boolean floorBodyFlag4 = floorCheck == null || floorCheck.isSafeLandingBlock(floor, floor7, floor8);
            if (!floorBodyFlag4) {
               return 850.0 + hypot5;
            }

            boolean floorBodyFlag5 = Math.abs(pitchYaw.y - vec.y) < 1.2 && hypot5 < 7.0;
            double max9 = Math.max(0.0, -y2 - 0.5);
            double landingRunoffPenalty = this.getLandingRunoffPenalty(client, pitchYaw.x, pitchYaw.y, pitchYaw.positionZ, pitchYaw.velocityX, pitchYaw.velocityZ);
            this.landedOnTarget = floorBodyFlag5 && landingRunoffPenalty == 0.0;
            return (floorBodyFlag5 ? 0 : 150) + landingRunoffPenalty + hypot5 * 0.6 + hypot4 * 7.0 + max9 * 45.0 + floorBodyTotal;
         }

         if (pitchYaw.y < vec.y - 6.0) {
            return 800.0;
         }
      }

      double hypot6 = Math.hypot(pitchYaw.x - vec.x, pitchYaw.positionZ - vec.z);
      double deltaY = pitchYaw.y - vec.y;
      double hypot7 = Math.hypot(pitchYaw.velocityX, pitchYaw.velocityZ);
      double max11 = Math.max(0.0, -((vec.x - pitchYaw.x) * floorBodyFactor + (vec.z - pitchYaw.positionZ) * floorBodyFactor2));
      return Math.min(280.0, 120.0 + Math.abs(deltaY - hypot6 * 0.22) * 4.0 + hypot6 * 0.4 + Math.max(0.0, hypot7 - 1.15) * 25.0 + max11 * 1.5 + floorBodyTotal);
   }

   public static Box getGlidingHitbox(double boxValue, double minY, double boxValue2) {
      return new Box(boxValue - 0.3, minY, boxValue2 - 0.3, boxValue + 0.3, minY + 0.6, boxValue2 + 0.3);
   }

   public void planLanding(MinecraftClient client, GlidePhysics.Body body, Vec3d vec, int yawValue, double sinPitchFactor, LandingPlanner.FloorCheck floorCheck) {
      long nanoTime = System.nanoTime();
      this.body.copyFrom(body);
      double hypot = Math.hypot(vec.x - body.x, vec.z - body.positionZ);
      float yawValue3 = hypot > 2.5 ? body.yaw + MathHelper.wrapDegrees(GlidePhysics.getYawToward(body.x, body.positionZ, vec.x, vec.z) - body.yaw) : body.yaw;
      double amount = Double.MAX_VALUE;
      float amount2 = yawValue3;
      float coarsePitchAnglesItem3 = 0.0F;
      boolean flag = false;

      for (float coarseYawOffsetsItem : COARSE_YAW_OFFSETS) {
         for (float coarsePitchAnglesItem : COARSE_PITCH_ANGLES) {
            double yawValue6 = this.simulateLandingCost(client, yawValue3 + coarseYawOffsetsItem, coarsePitchAnglesItem, vec, -1, coarsePitchAnglesItem, yawValue, sinPitchFactor, floorCheck) + Math.abs(coarseYawOffsetsItem) * 0.15;
            if (yawValue6 < amount) {
               amount = yawValue6;
               amount2 = yawValue3 + coarseYawOffsetsItem;
               coarsePitchAnglesItem3 = coarsePitchAnglesItem;
               flag = this.landedOnTarget;
            }
         }
      }

      if (hypot > 8.0 && (!flag || !(amount < 40.0))) {
         for (float switchYawOffsetsItem : SWITCH_YAW_OFFSETS) {
            for (float switchEarlyPitchesItem : SWITCH_EARLY_PITCHES) {
               for (int pitchSwitchTicksItem : PITCH_SWITCH_TICKS) {
                  for (float switchLatePitchesItem : SWITCH_LATE_PITCHES) {
                     double yawValue11 = this.simulateLandingCost(client, yawValue3 + switchYawOffsetsItem, switchEarlyPitchesItem, vec, pitchSwitchTicksItem, switchLatePitchesItem, yawValue, sinPitchFactor, floorCheck)
                        + Math.abs(switchYawOffsetsItem) * 0.15
                        + 2.0;
                     if (yawValue11 < amount) {
                        amount = yawValue11;
                        amount2 = yawValue3 + switchYawOffsetsItem;
                        coarsePitchAnglesItem3 = switchEarlyPitchesItem;
                        flag = this.landedOnTarget;
                     }
                  }
               }
            }
         }
      }

      this.bestYaw = amount2;
      this.bestPitch = coarsePitchAnglesItem3;
      this.lastPlanScore = amount;
      this.bestLandsOnTarget = flag;
      this.planDurationNanos = System.nanoTime() - nanoTime;
   }

   public static Vec3d getCollisionAdjustedMove(MinecraftClient client, Vec3d vec, Box box) {
      Box stretch = box.stretch(vec);
      return !client.world.isDebugWorld() && areSectionsEmpty(client, stretch) && !client.world.getWorldBorder().canCollide(client.player, stretch)
         ? vec
         : Entity.adjustMovementForCollisions(client.player, vec, box, client.world, List.of());
   }

   private static boolean areSectionsEmpty(MinecraftClient client, Box box) {
      int floorSectionLimit = MathHelper.floor(box.minX) - 2;
      int floorOffset = MathHelper.floor(box.maxX) + 2;
      int floorSectionLimit2 = MathHelper.floor(box.minY) - 2;
      int floorOffset2 = MathHelper.floor(box.maxY) + 2;
      int floorSectionLimit3 = MathHelper.floor(box.minZ) - 2;
      int floorOffset3 = MathHelper.floor(box.maxZ) + 2;
      if (floorOffset < floorSectionLimit || floorOffset2 < floorSectionLimit2 || floorOffset3 < floorSectionLimit3) {
         return false;
      } else if (floorSectionLimit2 >= client.world.getBottomY() && floorOffset2 <= client.world.getTopYInclusive()) {
         for (int i = floorSectionLimit >> 4; i <= floorOffset >> 4; i++) {
            for (int j = floorSectionLimit3 >> 4; j <= floorOffset3 >> 4; j++) {
               WorldChunk worldChunk = client.world.getChunkManager().getWorldChunk(i, j);
               if (worldChunk == null) {
                  return false;
               }

               ChunkSection[] sectionArray = worldChunk.getSectionArray();

               for (int k = floorSectionLimit2 >> 4; k <= floorOffset2 >> 4; k++) {
                  int sectionCoordToIndex = worldChunk.sectionCoordToIndex(k);
                  if (sectionCoordToIndex < 0 || sectionCoordToIndex >= sectionArray.length || sectionArray[sectionCoordToIndex] == null || !sectionArray[sectionCoordToIndex].isEmpty()) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public LandingPlanner() {
      this.body = new GlidePhysics.Body();
      this.lastPlanScore = Double.MAX_VALUE;
   }

   private double getLandingRunoffPenalty(MinecraftClient client, double landingPlannerTotal, double minY, double landingPlannerTotal2, double x2, double z2) {
      double x3 = minY;
      double y2 = 0.0;

      for (int i = 0; i < 14; i++) {
         y2 = (y2 - 0.08) * 0.98;
         Vec3d vec3d = new Vec3d(x2, y2, z2);
         Vec3d worldCollide = getCollisionAdjustedMove(client, vec3d, new Box(landingPlannerTotal - 0.3, minY, landingPlannerTotal2 - 0.3, landingPlannerTotal + 0.3, minY + 1.8, landingPlannerTotal2 + 0.3));
         landingPlannerTotal += worldCollide.x;
         minY += worldCollide.y;
         landingPlannerTotal2 += worldCollide.z;
         if (x3 - minY > 1.2) {
            return 400.0;
         }

         boolean landingPlannerFlag = worldCollide.y != vec3d.y && y2 < 0.0;
         if (landingPlannerFlag) {
            y2 = 0.0;
         }

         double landingPlannerFactor = landingPlannerFlag ? 0.546 : 0.91;
         x2 = worldCollide.x != vec3d.x ? 0.0 : x2 * landingPlannerFactor;
         z2 = worldCollide.z != vec3d.z ? 0.0 : z2 * landingPlannerFactor;
         if (landingPlannerFlag && Math.hypot(x2, z2) < 0.02) {
            return 0.0;
         }
      }

      return 0.0;
   }

   public interface FloorCheck {
      boolean isSafeLandingBlock(int safeValue, int safeValue2, int safeValue3);
   }
}
