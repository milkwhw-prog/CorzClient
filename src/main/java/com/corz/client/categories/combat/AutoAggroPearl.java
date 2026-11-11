package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.entity.FloorMathUtilsDirection;
import com.corz.client.entity.FloorMathUtilsDirection.Result;
import com.corz.client.inventory.SelectedInventoryUtils;
import com.corz.client.world.MathClampUtils;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ai.auth.Module;
import com.corz.client.utils.MillisCooldownTimer;
import com.corz.client.utils.IconsPngType;

public class AutoAggroPearl extends Module {
   private final DoubleSetting maxLandingDistance = new DoubleSetting("Max Landing Dist", "Ignore enemy pearls landing farther than this", 60.0, 10.0, 120.0, 5.0);
   private final DoubleSetting maxWallCollisionRisk;
   private final Set<Integer> answeredPearlIds;
   private final MillisCooldownTimer throwTimer;
   private final IntegerSetting throwDelayMs = new IntegerSetting("Delay", "Minimum ms between answering throws", 500, 100, 3000);
   private final BooleanSetting visibleCheck;
   private final BooleanSetting avoidWalls = new BooleanSetting(
      "Avoid Walls", "Skip the throw if the pearl would hit a wall/ceiling before reaching the target", true
   );

   private void throwPearlAt(float degrees, float iIiValue) {
      float yaw = mc.player.getYaw();
      float pitch = mc.player.getPitch();
      mc.player.setYaw(degrees);
      mc.player.setPitch(iIiValue);
      mc.player
         .networkHandler
         .sendPacket(new LookAndOnGround(degrees, iIiValue, mc.player.isOnGround(), mc.player.horizontalCollision));
      mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
      mc.player.swingHand(Hand.MAIN_HAND);
      mc.player.setYaw(yaw);
      mc.player.setPitch(pitch);
   }

   @Override
   public void onEnable() {
      this.answeredPearlIds.clear();
   }

   private boolean hasClearPath(Vec3d vec, Vec3d vec2) {
      BlockHitResult raycast = mc.world.raycast(new RaycastContext(vec, vec2, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
      return raycast.getType() != Type.BLOCK || raycast.getPos().distanceTo(vec2) <= 2.0;
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.interactionManager != null && mc.world != null) {
         if (mc.currentScreen == null) {
            this.answeredPearlIds.removeIf(integer -> mc.world.getEntityById(integer) == null);
            if (this.throwTimer.hasElapsed(this.throwDelayMs.getValue())) {
               EnderPearlEntity next = this.findUnansweredPearl();
               if (next != null) {
                  Result vecDirection = FloorMathUtilsDirection.getVecDirectionForVec3dInt(next.getEntityPos(), next.getVelocity(), 300);
                  Vec3d vec3d3 = vecDirection.targetPos;
                  Vec3d eyePos = mc.player.getEyePos();
                  if (eyePos.distanceTo(vec3d3) > this.maxLandingDistance.getValue()) {
                     this.answeredPearlIds.add(next.getId());
                  } else {
                     int selectedSlot = SelectedInventoryUtils.getValue(Items.ENDER_PEARL);
                     if (selectedSlot != -1) {
                        float iIiValue = FloorMathUtilsDirection.getAmount(eyePos, vec3d3, 1.5, 0.03);
                        if (Float.isNaN(iIiValue)) {
                           this.answeredPearlIds.add(next.getId());
                        } else {
                           float degrees = MathClampUtils.getWrapDegrees(eyePos, vec3d3)[0];
                           if (this.avoidWalls.isEnabled()) {
                              Result vecDirectionForVec3dInt = FloorMathUtilsDirection.getVecDirectionForVec3dInt(eyePos, this.getThrowVelocity(degrees, iIiValue), 300);
                              double distanceTo = vecDirectionForVec3dInt.targetPos.distanceTo(vec3d3);
                              if (vecDirectionForVec3dInt.movedFlag && distanceTo > this.maxWallCollisionRisk.getValue()) {
                                 return;
                              }
                           }

                           if (!this.visibleCheck.isEnabled() || this.hasClearPath(eyePos, vec3d3)) {
                              int selectedSlot2 = mc.player.getInventory().getSelectedSlot();
                              mc.player.getInventory().setSelectedSlot(selectedSlot);
                              this.throwPearlAt(degrees, iIiValue);
                              mc.player.getInventory().setSelectedSlot(selectedSlot2);
                              this.answeredPearlIds.add(next.getId());
                              this.throwTimer.restart();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private EnderPearlEntity findUnansweredPearl() {
      for (Entity entity : mc.world.getEntities()) {
         if (entity instanceof EnderPearlEntity next && !this.answeredPearlIds.contains(next.getId())) {
            Entity owner = next.getOwner();
            if (owner instanceof PlayerEntity && owner != mc.player && !FriendNameStore.canFlag(owner)) {
               return next;
            }
         }
      }

      return null;
   }

   public AutoAggroPearl() {
      super("Auto Aggro Pearl", IconsPngType.COMBAT);
      this.visibleCheck = new BooleanSetting("Visible Check", "Also require line of sight to the landing spot", false);
      this.maxWallCollisionRisk = new DoubleSetting(
         "Max Wall Collision Risk", "How many blocks the simulated pearl may fall short of the target before it counts as blocked", 3.0, 0.5, 12.0, 0.5
      );
      this.throwTimer = new MillisCooldownTimer();
      this.answeredPearlIds = new HashSet<>();
      this.addSettings(new ConfigSetting[]{this.maxLandingDistance, this.throwDelayMs, this.avoidWalls, this.visibleCheck, this.maxWallCollisionRisk});
   }

   private Vec3d getThrowVelocity(float degrees, float iIiValue) {
      double angle = Math.toRadians(degrees);
      double radians = Math.toRadians(iIiValue);
      Vec3d normalize = new Vec3d(-Math.sin(angle) * Math.cos(radians), -Math.sin(radians), Math.cos(angle) * Math.cos(radians)).normalize();
      return normalize.multiply(1.5).add(mc.player.getVelocity());
   }
}
