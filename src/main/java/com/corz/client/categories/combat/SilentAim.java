package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityEyeUtils;
import com.corz.client.entity.TimeInstanceUtils;
import com.corz.client.world.ClampMath;
import com.corz.client.world.MathClampUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.combat.AnchorMacro;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.IconsPngType;

public class SilentAim extends Module {
   private float pitch;
   private static volatile float lockedPitch;
   private final ClampMath rotationHumanizer;
   private float yaw;
   private boolean silentTurnActive;
   private final BooleanSetting humanizeSetting;
   private final BooleanSetting visibilityCheck;
   private final BooleanSetting hitTarget;
   private static volatile float lockedYaw;
   private final DoubleSetting rangeSetting = new DoubleSetting("Range", "Max distance to a target", 4.5, 2.0, 6.0, 0.1);
   private static volatile boolean targetLocked;
   private static volatile PlayerEntity lockedTarget;
   private final DoubleSetting fovSetting = new DoubleSetting("FOV", "Only aim at players within this cone of your view", 90.0, 10.0, 180.0, 1.0);
   private final EnumSetting rotationModeSetting;
   private final EnumSetting aimPoint = new EnumSetting("Aim Point", "Where on the target to aim", "Neck", "Head", "Neck", "Body");
   private final EnumSetting activation;
   private float prevSilentPitch;
   private final BooleanSetting revealPose;
   private float prevSilentYaw;

   public SilentAim() {
      super("Silent Aim", IconsPngType.COMBAT);
      this.visibilityCheck = new BooleanSetting("Visibility Check", "Only aim at players you can see", true);
      this.activation = new EnumSetting("Activation", "When the aim engages", "Always", "Always", "On Click");
      this.rotationModeSetting = new EnumSetting("Rotation", "How the aim is applied", "Silent Turn", "Silent Turn", "Body", "Packet", "Move Fix");
      this.humanizeSetting = new BooleanSetting(
         "Humanize",
         "Ease onto the target with mouse-like, sensitivity-quantized rotation steps so aim-analysis anticheats (Vulcan) see human aim instead of a perfect lock",
         true
      );
      this.revealPose = new BooleanSetting(
         "Reveal Pose", "In third person (e.g. with Freelook) turn your on-screen body toward the target so you can watch the aim lock on", true
      );
      this.hitTarget = new BooleanSetting(
         "Hit Target", "When you left-click, hit the locked target within reach instead of whatever your crosshair is on", true
      );
      this.rotationHumanizer = new ClampMath();
      this.addSettings(
         new ConfigSetting[]{
            this.rangeSetting,
            this.fovSetting,
            this.aimPoint,
            this.visibilityCheck,
            this.activation,
            this.rotationModeSetting,
            this.humanizeSetting,
            this.revealPose,
            this.hitTarget
         }
      );
   }

   public static float getLockedPitch() {
      return lockedPitch;
   }

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      if (this.silentTurnActive) {
         float tickDelta = deltaAngle.getTickDelta();
         deltaAngle.setAngleDegrees(MathHelper.lerp(tickDelta, this.prevSilentYaw, this.yaw));
         deltaAngle.setLerp(MathHelper.lerp(tickDelta, this.prevSilentPitch, this.pitch));
         deltaAngle.updateFlag();
      }
   }

   private static boolean isWithinInteractionRange(Entity entity) {
      double entityInteractionRange = mc.player.getEntityInteractionRange();
      Vec3d eyePos = mc.player.getEyePos();
      Box boundingBox = entity.getBoundingBox();
      double maxMinX = Math.max(boundingBox.minX, Math.min(eyePos.x, boundingBox.maxX));
      double maxMinY = Math.max(boundingBox.minY, Math.min(eyePos.y, boundingBox.maxY));
      double maxMinZ = Math.max(boundingBox.minZ, Math.min(eyePos.z, boundingBox.maxZ));
      return eyePos.squaredDistanceTo(maxMinX, maxMinY, maxMinZ) <= entityInteractionRange * entityInteractionRange;
   }

   public static PlayerEntity getLockedHitTarget() {
      SilentAim silentAim = (SilentAim)ModuleRegistry.getModule(SilentAim.class);
      if (silentAim != null && silentAim.isEnabled() && silentAim.hitTarget.isEnabled()) {
         PlayerEntity entity = lockedTarget;
         if (entity == null || !entity.isAlive() || mc.player == null || mc.interactionManager == null) {
            return null;
         } else if (!isWithinInteractionRange(entity)) {
            return null;
         } else if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.BLOCK) {
            return null;
         } else if (TimeInstanceUtils.isInteractionBreaking()) {
            return null;
         } else {
            Vec3d eyePos = mc.player.getEyePos();
            Vec3d add = eyePos.add(MathClampUtils.getCosSin(lockedYaw, lockedPitch).multiply(mc.player.getEntityInteractionRange()));
            return entity.getBoundingBox().raycast(eyePos, add).isEmpty() ? null : entity;
         }
      } else {
         return null;
      }
   }

   private void commitSilentRotation() {
      if (this.silentTurnActive) {
         this.silentTurnActive = false;
         if (mc.player != null) {
            mc.player.setYaw(this.yaw);
            mc.player.setPitch(this.pitch);
            mc.player.lastYaw = this.yaw;
            mc.player.lastPitch = this.pitch;
         }
      }
   }

   public static boolean tryAttackLockedTarget(boolean attackHandFlag) {
      PlayerEntity fixCrosshair = getLockedHitTarget();
      if (fixCrosshair == null) {
         return false;
      } else {
         if (attackHandFlag) {
            TimeInstanceUtils.handleMinTime(fixCrosshair);
         } else {
            if (!TimeInstanceUtils.canTimeMin(fixCrosshair)) {
               return false;
            }

            if (!ClientThreadUtils.canPassFlag(ModuleRegistry.getModule(SilentAim.class), fixCrosshair)) {
               return true;
            }

            TimeInstanceUtils.updateDequeArray();
         }

         mc.interactionManager.attackEntity(mc.player, fixCrosshair);
         mc.player.swingHand(Hand.MAIN_HAND);
         mc.player.resetTicksSinceLastAttack();
         return true;
      }
   }

   private Vec3d getAimPointPos(PlayerEntity player) {
      double x = player.getX();
      double y = player.getY();
      double z = player.getZ();
      double height = player.getHeight();
      String formatText = this.aimPoint.getCurrentOption();

      return switch (formatText) {
         case "Head" -> new Vec3d(x, y + height - 0.1, z);
         case "Body" -> new Vec3d(x, y + height * 0.5, z);
         default -> new Vec3d(x, y + height - 0.35, z);
      };
   }

   @Override
   protected void onDisable() {
      targetLocked = false;
      lockedTarget = null;
      this.rotationHumanizer.updateFlag();
      this.commitSilentRotation();
   }

   public void applyRotationDelta(double clampYawValue, double clampYawValue2) {
      this.prevSilentYaw = this.yaw;
      this.prevSilentPitch = this.pitch;
      this.yaw += (float)(clampYawValue * 0.15);
      this.pitch = MathHelper.clamp(this.pitch + (float)(clampYawValue2 * 0.15), -90.0F, 90.0F);
   }

   private void beginSilentTurn() {
      this.yaw = this.prevSilentYaw = mc.player.getYaw();
      this.pitch = this.prevSilentPitch = mc.player.getPitch();
      this.silentTurnActive = true;
   }

   public static boolean isRevealPoseActive() {
      SilentAim silentAim = (SilentAim)ModuleRegistry.getModule(SilentAim.class);
      return silentAim != null && silentAim.isEnabled() && targetLocked && silentAim.revealPose.isEnabled();
   }

   public static float getSilentPitch() {
      SilentAim silentAim = (SilentAim)ModuleRegistry.getModule(SilentAim.class);
      if (silentAim != null && silentAim.isEnabled() && silentAim.silentTurnActive) {
         return silentAim.pitch;
      } else {
         return mc.player != null ? mc.player.getPitch() : 0.0F;
      }
   }

   private PlayerEntity findBestTarget() {
      double silentAimValue = this.fovSetting.getValue() / 2.0;
      PlayerEntity next = null;
      double silentAimLimit = Double.MAX_VALUE;

      for (PlayerEntity playerEntity : EntityEyeUtils.getInstanceEye(this.rangeSetting.getValue())) {
         Vec3d headBody = this.getAimPointPos(playerEntity);
         double wrapDegrees = EntityEyeUtils.getWrapDegrees(headBody, this.yaw, this.pitch);
         if (!(wrapDegrees > silentAimValue) && (!this.visibilityCheck.isEnabled() || EntityEyeUtils.canInstanceSquared(headBody)) && wrapDegrees < silentAimLimit) {
            silentAimLimit = wrapDegrees;
            next = playerEntity;
         }
      }

      return next;
   }

   private boolean isActivationHeld() {
      return this.activation.getCurrentOption().equals("Always") ? true : GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 0) == 1;
   }

   public boolean isSilentTurnRunning() {
      return this.isEnabled() && this.silentTurnActive;
   }

   private boolean isRotationBlocked() {
      return ClientThreadUtils.isFlag6() ? true : ModuleRegistry.getModule(AnchorMacro.class) instanceof AnchorMacro anchorMacro && anchorMacro.isMacroInteracting();
   }

   public static boolean isTargetLocked() {
      return targetLocked;
   }

   @Override
   public void onReset() {
      targetLocked = false;
      lockedTarget = null;
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (!this.silentTurnActive) {
            this.yaw = mc.player.getYaw();
            this.pitch = mc.player.getPitch();
         }

         if (this.isRotationBlocked()) {
            this.commitSilentRotation();
            this.rotationHumanizer.updateFlag();
         } else if (!this.isActivationHeld()) {
            this.commitSilentRotation();
            this.rotationHumanizer.updateFlag();
         } else {
            PlayerEntity bestTarget = this.findBestTarget();
            if (bestTarget == null) {
               this.commitSilentRotation();
               this.rotationHumanizer.updateFlag();
            } else {
               lockedTarget = bestTarget;
               Vec3d headBody = this.getAimPointPos(bestTarget);
               float[] wrapDegrees = MathClampUtils.getWrapDegrees(mc.player.getEyePos(), headBody);
               if (this.humanizeSetting.isEnabled()) {
                  wrapDegrees = this.rotationHumanizer.getClampRandom(wrapDegrees[0], wrapDegrees[1]);
               } else {
                  this.rotationHumanizer.updateFlag();
               }

               lockedYaw = wrapDegrees[0];
               lockedPitch = wrapDegrees[1];
               targetLocked = true;
               String formatText = this.rotationModeSetting.getCurrentOption();
               if (formatText.equals("Silent Turn")) {
                  if (!this.silentTurnActive) {
                     this.beginSilentTurn();
                  }

                  MathClampUtils.handle(this, lockedYaw, lockedPitch, 50, false);
               } else {
                  this.commitSilentRotation();
                  boolean iIiFlag = !formatText.equals("Body");
                  boolean equals = formatText.equals("Move Fix");
                  MathClampUtils.handle(this, lockedYaw, lockedPitch, 50, iIiFlag, equals);
               }
            }
         }
      } else {
         this.commitSilentRotation();
         this.rotationHumanizer.updateFlag();
      }
   }

   public static float getLockedYaw() {
      return lockedYaw;
   }

   public static float getYawSilent() {
      SilentAim silentAim = (SilentAim)ModuleRegistry.getModule(SilentAim.class);
      if (silentAim != null && silentAim.isEnabled() && silentAim.silentTurnActive) {
         return silentAim.yaw;
      } else {
         return mc.player != null ? mc.player.getYaw() : 0.0F;
      }
   }
}
