package com.corz.client.categories.combat;

import com.corz.client.utils.SmoothAimController.Frame;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.gui.TickProgress;
import com.corz.client.render.CodeCategoryUtils;
import com.corz.client.world.ClampMath;
import com.corz.client.world.MathClampUtils;
import java.util.Random;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.MouseNoiseGenerator;
import com.corz.client.utils.AngleMath;
import com.corz.client.utils.AimStepBudget;
import com.corz.client.utils.FixedRateTicker;
import com.corz.client.utils.RotationDelta;
import com.corz.client.utils.SmoothAimController;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.categories.visuals.Freelook;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.combat.SilentAim;
import com.corz.client.categories.combat.AnchorMacro;
import com.corz.client.utils.WeaponStackChecks;
import com.corz.client.utils.IconsPngType;

public class AimAssist extends Module {
   private int lockTicksRemaining;
   private final BooleanSetting requireMouseDown;
   private final DoubleSetting verticalSpeed;
   private final BooleanSetting aimVertically;
   private final EnumSetting targetsMode;
   private final MouseNoiseGenerator simpleAimSolver;
   private final BooleanSetting weaponsOnly;
   private final BooleanSetting strafeIncrease;
   private long handCrosshairCooldownEndMs;
   private final DoubleSetting maxAngle;
   private final DoubleSetting horizontalSpeed;
   private int ticksSinceAcquire;
   private final EnumSetting aimModeSetting = new EnumSetting(
      "Mode", "Simple: light, steady pull. Adaptive: smoother, more human tracking.", "Simple", "Simple", "Adaptive"
   );
   private final BooleanSetting visibilityCheck;
   private final DoubleSetting maxDistance;
   private final FixedRateTicker assistTimer;
   private final SmoothAimController adaptiveAimSolver;
   private Vec3d lastTargetPos;
   private LivingEntity target;
   private static final Options MOBS_ONLY_FILTER = Options.INSTANCE.getInvisiblesAnimals(false);
   private final AimStepBudget mouseStepConverter;
   private String activeModeName;

   private void clearTarget() {
      this.target = null;
      this.lockTicksRemaining = 0;
      this.lastTargetPos = null;
      this.simpleAimSolver.resetCarry();
      this.adaptiveAimSolver.reset();
   }

   private boolean isValidTarget(LivingEntity entity) {
      if (EntityPlayerUtilsLiving.canSpectatorDead(entity, this.getTargetFilter()) && entity != mc.player.getVehicle()) {
         if (EntityPlayerUtilsLiving.getMinMax(entity) > this.maxDistance.getValue()) {
            return false;
         } else {
            double vehicleValue = this.maxAngle.getValue();
            return vehicleValue < 360.0 && this.getYawOffsetToEntity(entity) > vehicleValue / 2.0 + 15.0
               ? false
               : !this.visibilityCheck.isEnabled() || EntityPlayerUtilsLiving.canSeeBounding(entity);
         }
      } else {
         return false;
      }
   }

   private RotationDelta computeAdaptiveAim(int livingEntityLimit, float livingEntityValue) {
      ClientPlayerEntity player = mc.player;
      Vec3d cameraPosVec = player.getCameraPosVec(livingEntityValue);
      Vec3d lerpedPos = this.target.getLerpedPos(livingEntityValue);
      Box boundingBox = this.target.getBoundingBox();
      Vec3d velocity = player.getVelocity();
      Frame frame = new Frame(
         cameraPosVec.x,
         cameraPosVec.y,
         cameraPosVec.z,
         player.isOnGround(),
         lerpedPos.x,
         lerpedPos.z,
         boundingBox.minY,
         boundingBox.maxY,
         this.target.getX() - this.target.lastX,
         this.target.getY() - this.target.lastY,
         this.target.getZ() - this.target.lastZ,
         velocity.x,
         velocity.y,
         velocity.z,
         player.distanceTo(this.target),
         player.getYaw(),
         player.getPitch(),
         player.input.getMovementInput().x
      );
      double sensitivityMouse = ClampMath.getSensitivityMouse();
      double signumValue = this.horizontalSpeed.getValue();
      double livingEntityValue3 = this.verticalSpeed.getValue();
      boolean booleanValue = this.aimVertically.isEnabled();
      boolean enabled = this.strafeIncrease.isEnabled();

      for (int i = 0; i < livingEntityLimit; i++) {
         this.adaptiveAimSolver.updateAimFrame(frame, signumValue, livingEntityValue3, booleanValue, enabled, sensitivityMouse);
      }

      return this.adaptiveAimSolver.takePendingMouseDelta();
   }

   private void updateTargetSelection(int livingEntityValue) {
      this.ticksSinceAcquire += livingEntityValue;
      if (this.target != null && !this.isValidTarget(this.target)) {
         this.clearTarget();
      }

      if (this.target == null) {
         if (this.lockTicksRemaining > 0) {
            this.lockTicksRemaining -= livingEntityValue;
         } else {
            this.lockTicksRemaining = 20;
            LivingEntity livingEntity = this.findBestTarget();
            if (livingEntity != null) {
               this.target = livingEntity;
               this.ticksSinceAcquire = 0;
               this.resetPrediction();
            }
         }
      } else if (!this.requireMouseDown.isEnabled() && this.ticksSinceAcquire > 700) {
         this.ticksSinceAcquire = 0;
         LivingEntity bestTarget = this.findBestTarget();
         if (bestTarget != null && bestTarget != this.target) {
            this.target = bestTarget;
            this.resetPrediction();
         }
      }
   }

   private LivingEntity findBestTarget() {
      double aimAssistValue = this.maxAngle.getValue();
      Entity vehicle = mc.player.getVehicle();
      LivingEntity next = null;
      double maxValue = Double.MAX_VALUE;
      double aimAssistLimit = Double.MAX_VALUE;

      for (LivingEntity livingEntity : EntityPlayerUtilsLiving.getInstanceWorld(this.maxDistance.getValue(), this.getTargetFilter())) {
         if (livingEntity != vehicle) {
            double playerYaw = this.getYawOffsetToEntity(livingEntity);
            if ((!(aimAssistValue < 360.0) || !(playerYaw > aimAssistValue / 2.0)) && (!this.visibilityCheck.isEnabled() || EntityPlayerUtilsLiving.canSeeBounding(livingEntity))) {
               double minMax = EntityPlayerUtilsLiving.getMinMax(livingEntity);
               if (playerYaw < maxValue - 1.0E-6 || Math.abs(playerYaw - maxValue) <= 1.0E-6 && minMax < aimAssistLimit) {
                  next = livingEntity;
                  maxValue = playerYaw;
                  aimAssistLimit = minMax;
               }
            }
         }
      }

      return next;
   }

   private RotationDelta computeSimpleAim(int lerpedLivingDivisor, float lerpedLivingValue) {
      ClientPlayerEntity player = mc.player;
      Vec3d cameraPosVec = player.getCameraPosVec(lerpedLivingValue);
      Vec3d lerpedPos = this.target.getLerpedPos(lerpedLivingValue);
      double lerpedLivingValue2 = 0.0;
      double lerpedLivingValue3 = 0.0;
      if (this.lastTargetPos != null) {
         lerpedLivingValue2 = (lerpedPos.x - this.lastTargetPos.x) / lerpedLivingDivisor;
         lerpedLivingValue3 = (lerpedPos.z - this.lastTargetPos.z) / lerpedLivingDivisor;
      }

      this.lastTargetPos = lerpedPos;
      double lerpedLivingValue4 = lerpedPos.x + lerpedLivingValue2 * 1.7;
      double lerpedLivingValue5 = lerpedPos.z + lerpedLivingValue3 * 1.7;
      float yaw = player.getYaw();
      double pitchDeltaToward = AngleMath.pitchDeltaToward(yaw, lerpedLivingValue4 - cameraPosVec.x, lerpedLivingValue5 - cameraPosVec.z);
      double abs7 = Math.abs(AngleMath.pitchDeltaToward(yaw, this.target.getX() - player.getX(), this.target.getZ() - player.getZ()));
      double clamp = MathHelper.clamp(cameraPosVec.y, lerpedPos.y + 0.1, lerpedPos.y + Math.max(0.1, this.target.getHeight() - 0.1));
      double hypot = Math.hypot(lerpedPos.x - cameraPosVec.x, lerpedPos.z - cameraPosVec.z);
      double wrapDegrees = AngleMath.wrapDegrees(AngleMath.safePitchFromDelta(clamp - cameraPosVec.y, hypot) - player.getPitch());
      double distanceTo = player.distanceTo(this.target);
      double x = player.input.getMovementInput().x;
      double lerpedLivingValue9 = this.horizontalSpeed.getValue();
      double lerpedLivingValue10 = this.verticalSpeed.getValue();
      boolean booleanValue = this.aimVertically.isEnabled();
      boolean enabled = this.strafeIncrease.isEnabled();

      for (int i = 0; i < lerpedLivingDivisor; i++) {
         this.simpleAimSolver.updateNoise(pitchDeltaToward, abs7, wrapDegrees, distanceTo, x, lerpedLivingValue9, lerpedLivingValue10, booleanValue, enabled);
      }

      return this.simpleAimSolver.takeMouseDelta();
   }

   private void applyAimDelta(RotationDelta pitchYawDataInstance) {
      if (!pitchYawDataInstance.isZero()) {
         float sensitivityMouse = ClampMath.getSensitivityMouse();
         int pitchYawFactor = this.mouseStepConverter.consumeTargetBudget(pitchYawDataInstance.yaw(), sensitivityMouse);
         int consumeMaxBudget = this.mouseStepConverter.consumeMaxBudget(pitchYawDataInstance.pitch(), sensitivityMouse);
         if (pitchYawFactor != 0 || consumeMaxBudget != 0) {
            ClientPlayerEntity player = mc.player;
            float yaw = player.getYaw();
            float pitch = player.getPitch();
            float pitchYawValue = pitchYawFactor * sensitivityMouse;
            float clamp = MathHelper.clamp(pitch + consumeMaxBudget * sensitivityMouse, -90.0F, 90.0F);
            float deltaVar = clamp - pitch;
            player.setYaw(yaw + pitchYawValue);
            player.setPitch(clamp);
            player.lastYaw += pitchYawValue;
            player.lastPitch += deltaVar;
         }
      }
   }

   @Override
   protected void onReset() {
      this.mouseStepConverter.resetBudgets();
      if (mc.player == null || mc.world == null || mc.currentScreen != null) {
         this.clearTarget();
         this.assistTimer.reset();
      }
   }

   public AimAssist() {
      super("Aim Assist", IconsPngType.COMBAT);
      this.targetsMode = new EnumSetting("Targets", "Who it aims at. Friends and teammates are never aimed at.", "Players", "Players", "Players + Mobs", "Mobs");
      this.requireMouseDown = new BooleanSetting("Require Mouse Down", "Only aims while you hold your attack button.", true);
      this.horizontalSpeed = new AimAssist.SavedDouble(
         "Horizontal Speed", "How strongly your aim is pulled sideways onto the target. Higher = faster.", 5.0, 1.0, 10.0, 0.1
      );
      this.aimVertically = new BooleanSetting("Aim Vertically", "Also pulls your aim up and down.", false);
      this.verticalSpeed = new AimAssist.SavedDouble(
         "Vertical Speed", "How strongly your aim is pulled up and down when Aim Vertically is on. Higher = faster.", 5.0, 1.0, 10.0, 0.1
      );
      this.maxAngle = new AimAssist.SavedDouble(
         "Max Angle", "Only aims at targets inside this cone in front of you, in degrees. 180 = anything in front, 360 = all around.", 180.0, 1.0, 360.0, 1.0
      );
      this.maxDistance = new AimAssist.SavedDouble("Distance", "Maximum distance in blocks to aim at someone.", 5.0, 1.0, 8.0, 0.1);
      this.visibilityCheck = new BooleanSetting("Visibility Check", "Only aims at targets you can see, never through walls.", true);
      this.weaponsOnly = new BooleanSetting("Weapons Only", "Only aims while you hold a sword, axe, mace, trident or spear.", false);
      this.strafeIncrease = new BooleanSetting("Strafe Increase", "Pulls a little harder while you strafe away from the target.", false);
      this.simpleAimSolver = new MouseNoiseGenerator(new Random());
      this.adaptiveAimSolver = new SmoothAimController(new Random());
      this.assistTimer = new FixedRateTicker(1000);
      this.mouseStepConverter = new AimStepBudget(40.0, 20.0);
      this.activeModeName = "";
      this.addSettings(
         new ConfigSetting[]{
            this.aimModeSetting,
            this.targetsMode,
            this.requireMouseDown,
            this.horizontalSpeed,
            this.aimVertically,
            this.verticalSpeed,
            this.maxAngle,
            this.maxDistance,
            this.visibilityCheck,
            this.weaponsOnly,
            this.strafeIncrease
         }
      );
   }

   @Override
   protected void onDisable() {
      this.resetAll();
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      int timeFlag = this.assistTimer.getElapsedTicks(System.nanoTime());
      if (mc.player != null
         && mc.world != null
         && mc.interactionManager != null
         && mc.options != null
         && mc.currentScreen == null) {
         String adaptiveText = this.aimModeSetting.getCurrentOption();
         if (!adaptiveText.equals(this.activeModeName)) {
            this.activeModeName = adaptiveText;
            this.clearTarget();
         }

         if (!this.isAimAllowed()) {
            this.clearTarget();
         } else if (this.requireMouseDown.isEnabled() && !CodeCategoryUtils.isKeyPressed(mc.options.attackKey)) {
            this.clearTarget();
         } else {
            this.updateTargetSelection(timeFlag);
            if (this.target != null && timeFlag > 0) {
               RotationDelta pitchYawDataInstance = "Adaptive".equals(this.activeModeName)
                  ? this.computeAdaptiveAim(timeFlag, tickProgress.tickProgress)
                  : this.computeSimpleAim(timeFlag, tickProgress.tickProgress);
               this.applyAimDelta(pitchYawDataInstance);
            }
         }
      } else {
         this.clearTarget();
      }
   }

   private boolean isAimAllowed() {
      if (!Freecam.isInstanceFlag() && !Freelook.isFlag()) {
         if (ModuleRegistry.getModule(SilentAim.class) instanceof SilentAim silentAim
            && silentAim.isEnabled()
            && (silentAim.isSilentTurnRunning() || SilentAim.isTargetLocked())) {
            return false;
         } else if (ClientThreadUtils.isFlag6()) {
            return false;
         } else if (ModuleRegistry.getModule(AnchorMacro.class) instanceof AnchorMacro anchorMacro && anchorMacro.isEnabled() && anchorMacro.isMacroInteracting()) {
            return false;
         } else if (AutoBuilder.isAggressiveBuilderActive()) {
            return false;
         } else if (MathClampUtils.isClampApplied() && !MathClampUtils.isClampActive()) {
            return false;
         } else if (MathClampUtils.isClampSettled() || MathClampUtils.isFlag()) {
            return false;
         } else if (this.weaponsOnly.isEnabled() && !WeaponStackChecks.isMeleeWeapon(mc.player.getMainHandStack())) {
            return false;
         } else {
            long currentMillis = System.currentTimeMillis();
            boolean handCrosshairFlag = WeaponStackChecks.canShovelsPickaxes(mc.player.getMainHandStack())
               && mc.crosshairTarget != null
               && mc.crosshairTarget.getType() == Type.BLOCK;
            if (handCrosshairFlag) {
               this.handCrosshairCooldownEndMs = currentMillis + 250L;
               return false;
            } else {
               return currentMillis >= this.handCrosshairCooldownEndMs;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   protected void onEnable() {
      this.resetAll();
   }

   private void resetAll() {
      this.clearTarget();
      this.simpleAimSolver.resetAll();
      this.assistTimer.reset();
      this.mouseStepConverter.resetBudgets();
      this.ticksSinceAcquire = 0;
      this.handCrosshairCooldownEndMs = 0L;
      this.activeModeName = this.aimModeSetting.getCurrentOption();
   }

   private void resetPrediction() {
      this.lastTargetPos = null;
      this.adaptiveAimSolver.reset();
   }

   private Options getTargetFilter() {
      String formatText = this.targetsMode.getCurrentOption();

      return switch (formatText) {
         case "Players + Mobs" -> Options.INSTANCE;
         case "Mobs" -> MOBS_ONLY_FILTER;
         default -> Options.INSTANCE2;
      };
   }

   private double getYawOffsetToEntity(Entity entity) {
      return Math.abs(
         AngleMath.pitchDeltaToward(mc.player.getYaw(), entity.getX() - mc.player.getX(), entity.getZ() - mc.player.getZ())
      );
   }

   private static final class SavedDouble extends DoubleSetting {
      @Override
      public void handleMaxAmount(double savedDoubleValue) {
         double savedDoubleValue2 = this.getValue();
         super.handleMaxAmount(savedDoubleValue);
         if (this.getValue() != savedDoubleValue2) {
            ModuleConfigAutosave.updateFlag2();
         }
      }

      SavedDouble(String savedDoubleText, String description, double savedDoubleValue, double savedDoubleValue2, double savedDoubleValue3, double savedDoubleValue4) {
         super(savedDoubleText, description, savedDoubleValue, savedDoubleValue2, savedDoubleValue3, savedDoubleValue4);
      }
   }
}
