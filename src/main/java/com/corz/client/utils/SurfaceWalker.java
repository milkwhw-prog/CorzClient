package com.corz.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.PathFollower;
import com.corz.client.utils.PlayerCombatState;
import com.corz.client.utils.PreferAnchorData;
import com.corz.client.utils.PathfindingWorldAdapter;
import com.corz.client.utils.AnchorRoutePlanner;
import com.corz.client.utils.HunterBody;

public final class SurfaceWalker {
   private final MinecraftClient client = MinecraftClient.getInstance();
   private final HunterBody hunterBody;
   private int unreachableTickCount;
   private Vec3d currentTarget;
   private final WalkerSurface stuckDetector;
   private int strafeDirection;
   private AnchorRoutePlanner pathfinder;
   private final PathfindingWorldAdapter registryHash = new PathfindingWorldAdapter();
   private int strafeTicksLeft;

   public SurfaceWalker(HunterBody hunterBody) {
      this.stuckDetector = new WalkerSurface();
      this.hunterBody = hunterBody;
   }

   public SurfaceWalker.Walk walkToward(long vecHashValue, Vec3d vec3d, boolean vecHashFlag) {
      ClientPlayerEntity player = this.client.player;
      if (player != null && vec3d != null) {
         if (this.pathfinder == null) {
            this.pathfinder = AnchorRoutePlanner.create(null, null);
         }

         if (this.currentTarget == null || this.currentTarget.squaredDistanceTo(vec3d) > 4.0) {
            this.currentTarget = vec3d;
            this.stuckDetector.updateFlagWalkerSurfaceActive();
            this.unreachableTickCount = 0;
         }

         Vec3d entityPos = player.getEntityPos();
         double hypot = Math.hypot(vec3d.x - entityPos.x, vec3d.z - entityPos.z);
         if (hypot <= 1.2 && Math.abs(vec3d.y - entityPos.y) <= 1.5) {
            return SurfaceWalker.Walk.ARRIVED;
         } else {
            boolean vecHashFlag2 = player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) || player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING);
            this.pathfinder
               .setImmuneFireData(
                  new PlayerCombatState(
                     Math.max(1.0F, player.getHealth() + player.getAbsorptionAmount()),
                     vecHashFlag2,
                     PathfindingWorldAdapter.getArmorProtectionScore(player),
                     -1,
                     player.isFireImmune(),
                     player.getHungerManager().getFoodLevel(),
                     player.isUsingItem(),
                     0,
                     2
                  )
               );
            PreferAnchorData cell = PreferAnchorData.getCell(vec3d);
            boolean jump3 = false;
            boolean hungerTouchingFlag = vecHashFlag;
            double hungerTouchingValue;
            double x;
            if (this.pathfinder.canReachDirectly(vecHashValue, entityPos, cell)) {
               hungerTouchingValue = vec3d.x - entityPos.x;
               x = vec3d.z - entityPos.z;
            } else {
               PathFollower.Cmd searchingRouted = this.pathfinder
                  .computeWalkCommand(vecHashValue, entityPos, player.getVelocity(), player.isOnGround(), player.horizontalCollision, player.isTouchingWater(), cell);
               if (searchingRouted == null) {
                  return SurfaceWalker.Walk.STUCK;
               }

               if (searchingRouted.done() && this.pathfinder.getRouteMode() != AnchorRoutePlanner.Mode.PARTIAL) {
                  return hypot <= 2.4 ? SurfaceWalker.Walk.ARRIVED : SurfaceWalker.Walk.STUCK;
               }

               if (!searchingRouted.move()) {
                  if (this.pathfinder.getRouteMode() == AnchorRoutePlanner.Mode.UNREACHABLE && ++this.unreachableTickCount > 60) {
                     return SurfaceWalker.Walk.STUCK;
                  }

                  return SurfaceWalker.Walk.MOVING;
               }

               hungerTouchingValue = searchingRouted.dx();
               x = searchingRouted.dz();
               jump3 = searchingRouted.jump();
               hungerTouchingFlag = vecHashFlag || searchingRouted.sprint();
            }

            WalkerSurface.Advice noneFlag = this.stuckDetector.getNoneFlag(true, entityPos.x, entityPos.z, hypot, player.horizontalCollision, player.isOnGround());
            switch (noneFlag) {
               case JUMP:
                  jump3 = true;
                  break;
               case REPATH:
                  int[] vecFlag = this.pathfinder.getNextNodeCoords();
                  if (vecFlag != null) {
                     this.pathfinder.markCellBlocked(vecFlag[0], vecFlag[1], vecFlag[2], vecHashValue);
                  }
                  break;
               case STRAFE:
                  this.strafeDirection = this.chooseStrafeDirection(player, entityPos);
                  this.strafeTicksLeft = this.strafeDirection == 0 ? 0 : 12;
                  if (this.strafeDirection == 0) {
                     int[] nextNodeCoords = this.pathfinder.getNextNodeCoords();
                     if (nextNodeCoords != null) {
                        this.pathfinder.markCellBlocked(nextNodeCoords[0], nextNodeCoords[1], nextNodeCoords[2], vecHashValue);
                     }
                  }
                  break;
               case UNREACHABLE:
                  this.pathfinder.markUnreachable(vecHashValue);
                  return SurfaceWalker.Walk.STUCK;
            }

            if (this.strafeTicksLeft > 0) {
               this.strafeTicksLeft--;
               if (this.canStrafeSideways(player, entityPos, this.strafeDirection == 4 ? 1 : -1)) {
                  this.hunterBody.pressKeys(this.strafeDirection | 32);
                  this.hunterBody.steerTowardOffset(hungerTouchingValue, x, false, false);
                  return SurfaceWalker.Walk.MOVING;
               }

               this.strafeTicksLeft = 0;
            }

            if (player.isTouchingWater()) {
               jump3 = true;
            }

            this.hunterBody.steerTowardOffset(hungerTouchingValue, x, hungerTouchingFlag, jump3);
            return SurfaceWalker.Walk.MOVING;
         }
      } else {
         return SurfaceWalker.Walk.STUCK;
      }
   }

   public String getPathModeName() {
      return this.pathfinder == null ? "-" : this.pathfinder.getRouteMode().name();
   }

   private boolean canStrafeSideways(ClientPlayerEntity player, Vec3d vec, int hashYawFactor) {
      double angle = Math.toRadians(player.getYaw());
      double hashYawValue = Math.cos(angle) * hashYawFactor;
      double hashYawValue2 = Math.sin(angle) * hashYawFactor;
      Vec3d vec3d = new Vec3d(vec.x + hashYawValue * 2.0, vec.y, vec.z + hashYawValue2 * 2.0);
      return PathFollower.isTraceable(this.registryHash, vec, vec3d, true, 2.0, Math.min(this.pathfinder.getMaxDrop(), 3));
   }

   private int chooseStrafeDirection(ClientPlayerEntity player, Vec3d vec) {
      if (this.canStrafeSideways(player, vec, 1)) {
         return 4;
      } else {
         return this.canStrafeSideways(player, vec, -1) ? 8 : 0;
      }
   }

   public void reset() {
      if (this.pathfinder != null) {
         this.pathfinder.reset();
      }

      this.stuckDetector.updateFlagWalkerSurfaceActive();
      this.currentTarget = null;
      this.strafeTicksLeft = 0;
      this.strafeDirection = 0;
      this.unreachableTickCount = 0;
   }

   public static enum Walk {
      MOVING,
      ARRIVED,
      STUCK;
   }
}
