package com.corz.client.categories.movement;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.mixins.AccessorClientPlayNetworkHandler;
import com.corz.client.mixins.AccessorInput;
import com.corz.client.mixins.AccessorKeyBinding;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import com.corz.client.categories.bots.AutoAreaMiner;
import com.corz.client.categories.bots.AutoBuilder;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.movement.AutoWalk;
import com.corz.client.utils.ElytraAutoUtils;
import com.corz.client.utils.IconsPngType;

public class Freecam extends Module {
   private final BooleanSetting booleanBase;
   private final DoubleSetting doubleHelper = new DoubleSetting("Speed", "Camera fly speed", 1.0, 0.1, 50.0, 0.1);
   private final BooleanSetting smoothing;
   private float lastCameraPitch;
   private boolean forwardActive;
   private boolean perspectiveRawActive;
   private final BooleanSetting showOwnBody;
   private double startIIi;
   private boolean inputActive;
   private double endIIi;
   private boolean iIiActive;
   private final BooleanSetting bodyUsesMovementKeys;
   private boolean freecamActive;
   private boolean perspectiveSwitched;
   private boolean leftPressed;
   private double startPerspectiveRaw;
   private boolean sneakPressed;
   private Perspective perspective;
   private float clamp;
   private double startLerp;
   private float iIiAmount;
   private final IntegerSetting integerHelper;
   private boolean cullingEnabled;
   private double endPerspectiveRaw;
   private static Freecam singleton;
   private int perspectiveRestoreTicks;
   private boolean rightPressed;
   private boolean pressedRaw;
   private boolean pressedRawPerspectiveRaw;
   private boolean backPressed;
   private ClientWorld world;
   private boolean flying;
   private boolean jumpPressed;
   private double endLerp;
   private final DoubleSetting lookSensitivity;
   private final DoubleSetting verticalSpeed = new DoubleSetting("Vertical Speed", "Up/down speed multiplier", 1.0, 0.2, 5.0, 0.05);
   private float degrees;

   public static boolean isBaseInstance() {
      return isInstanceFlag() && singleton.showOwnBody.isEnabled();
   }

   private void handleFlagVelocity(ClientPlayerEntity clientPlayerEntity, double velocityLimit, boolean velocityFlag) {
      Vec3d velocity = clientPlayerEntity.getVelocity();
      double x = velocity.x;
      double z = velocity.z;
      if (!(x * x + z * z < velocityLimit)) {
         double angle = Math.toRadians(clientPlayerEntity.getYaw());
         double sin = -Math.sin(angle);
         double cos = Math.cos(angle);
         double velocityLimit2 = x * sin + z * cos;
         double spotValue = x * -cos + z * sin;
         boolean velocityFlag2 = velocityFlag ? Math.abs(velocityLimit2) > Math.abs(spotValue) : Math.abs(velocityLimit2) >= Math.abs(spotValue);
         if (velocityFlag2) {
            if (velocityLimit2 > 0.008) {
               this.inputActive = true;
            } else if (velocityLimit2 < -0.008) {
               this.backPressed = true;
            }
         } else if (spotValue > 0.008) {
            this.leftPressed = true;
         } else if (spotValue < -0.008) {
            this.rightPressed = true;
         }
      }
   }

   private static boolean canCodeCategory(KeyBinding keyBinding) {
      if (mc.currentScreen == null && mc.isWindowFocused()) {
         Key boundKey = ((AccessorKeyBinding)keyBinding).getBoundKey();
         long handle = mc.getWindow().getHandle();
         if (boundKey.getCategory() != Type.KEYSYM) {
            return boundKey.getCategory() == Type.MOUSE ? GLFW.glfwGetMouseButton(handle, boundKey.getCode()) == 1 : false;
         } else {
            return boundKey.getCode() != InputUtil.UNKNOWN_KEY.getCode() && GLFW.glfwGetKey(handle, boundKey.getCode()) == 1;
         }
      } else {
         return false;
      }
   }

   @Override
   protected void onEnable() {
      if (mc.player == null) {
         this.toggle();
      } else {
         this.perspectiveSwitched = true;
         this.freecamActive = false;
      }
   }

   public static boolean isBaseInstanceBooleanBase() {
      return isInstanceFlag() && singleton.booleanBase.isEnabled();
   }

   public float getLerp(float delta) {
      return MathHelper.lerp(delta, this.lastCameraPitch, this.clamp);
   }

   private void updateFlag() {
      Module iliIiiliiiii = ModuleRegistry.getModule(AutoWalk.class);
      if (iliIiiliiiii != null && iliIiiliiiii.isEnabled()) {
         this.inputActive = true;
      }
   }

   public void handleClampAmount(double clampFactor, double clampFactor2) {
      double clampFactor3 = this.lookSensitivity.getValue();
      this.degrees += (float)(clampFactor * clampFactor3) * 0.15F;
      this.clamp = MathHelper.clamp(this.clamp + (float)(clampFactor2 * clampFactor3) * 0.15F, -90.0F, 90.0F);
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null) {
         if (this.perspectiveSwitched) {
            this.updatePerspectiveRaw();
         }

         if (this.freecamActive) {
            if (mc.world != this.world) {
               this.setEnabled(false);
            } else {
               boolean iIiFlag = isFlag();
               if (iIiFlag) {
                  this.updateFlagInputActive();
                  this.updatePressedForward();
               } else if (this.perspectiveRestoreTicks > 0) {
                  this.perspectiveRestoreTicks--;
                  this.updateFlagForwardBodyUsesMovementKeys();
               }

               this.startIIi = this.endLerp;
               this.startLerp = this.endIIi;
               this.startPerspectiveRaw = this.endPerspectiveRaw;
               this.iIiAmount = this.degrees;
               this.lastCameraPitch = this.clamp;
               PlayerAbilities abilities = mc.player.getAbilities();
               if (!iIiFlag && abilities.creativeMode) {
                  abilities.flying = false;
                  this.iIiActive = true;
               }

               GameOptions options = mc.options;
               double iIiFactor = getAmount(options.forwardKey, options.backKey);
               double iIiFactor2 = getAmount(options.leftKey, options.rightKey);
               double iIiFactor3 = getAmount(options.jumpKey, options.sneakKey);
               double iIiFactor4 = this.doubleHelper.getValue();
               double iIiFactor5 = this.smoothing.isEnabled() ? 0.5 : 1.0;
               double angle = Math.toRadians(this.degrees);
               double iIiFactor6 = -Math.sin(angle) * iIiFactor * iIiFactor4 + Math.cos(angle) * iIiFactor2 * iIiFactor4;
               double iIiFactor7 = Math.cos(angle) * iIiFactor * iIiFactor4 + Math.sin(angle) * iIiFactor2 * iIiFactor4;
               double iIiFactor8 = iIiFactor3 * iIiFactor4 * this.verticalSpeed.getValue();
               this.endLerp += iIiFactor6 * iIiFactor5;
               this.endIIi += iIiFactor8 * iIiFactor5;
               this.endPerspectiveRaw += iIiFactor7 * iIiFactor5;
               this.updateLoadChunk();
            }
         }
      }
   }

   private void updateFlagForward() {
      ClientPlayerEntity player = mc.player;
      GameOptions options = mc.options;
      PlayerInput playerInput = player.input.playerInput;
      this.inputActive = canCodeCategory(options.forwardKey) || playerInput.forward();
      this.backPressed = canCodeCategory(options.backKey) || playerInput.backward();
      this.leftPressed = canCodeCategory(options.leftKey) || playerInput.left();
      this.rightPressed = canCodeCategory(options.rightKey) || playerInput.right();
      this.jumpPressed = canCodeCategory(options.jumpKey) || playerInput.jump();
      this.sneakPressed = canCodeCategory(options.sneakKey) || playerInput.sneak() || player.isSneaking();
      this.forwardActive = canCodeCategory(options.sprintKey) || playerInput.sprint();
      this.updateFlag();
      this.handleSpeedSideways(player);
   }

   private void updateWorldSchedule() {
      if (mc.world != null) {
         mc.world.getChunkManager().updateLoadDistance(getViewDistance());
         if (mc.worldRenderer != null) {
            mc.worldRenderer.scheduleTerrainUpdate();
         }
      }
   }

   private void updatePressedForward() {
      GameOptions options = mc.options;
      options.forwardKey.setPressed(false);
      options.backKey.setPressed(false);
      options.leftKey.setPressed(false);
      options.rightKey.setPressed(false);
      options.jumpKey.setPressed(false);
      ElytraAutoUtils.handleRawPressed(options.sneakKey, false);
      ElytraAutoUtils.handleRawPressed(options.sprintKey, false);
   }

   private void updatePerspectiveRaw() {
      ClientPlayerEntity player = mc.player;
      this.endLerp = this.startIIi = player.getX();
      this.endIIi = this.startLerp = player.getY() + player.getStandingEyeHeight();
      this.endPerspectiveRaw = this.startPerspectiveRaw = player.getZ();
      this.degrees = this.iIiAmount = player.getYaw();
      this.clamp = this.lastCameraPitch = player.getPitch();
      this.flying = player.getAbilities().flying;
      this.iIiActive = false;
      this.perspectiveRawActive = false;
      if (this.showOwnBody.isEnabled()) {
         this.perspective = mc.options.getPerspective();
         if (this.perspective.isFirstPerson()) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            this.perspectiveRawActive = true;
         }
      }

      this.perspectiveSwitched = false;
      this.freecamActive = true;
      this.world = mc.world;
      this.pressedRaw = ((AccessorKeyBinding)mc.options.sneakKey).getPressedRaw();
      this.pressedRawPerspectiveRaw = ((AccessorKeyBinding)mc.options.sprintKey).getPressedRaw();
      this.updateFlagForward();
      this.updateFlagPlayer();
      this.perspectiveRestoreTicks = 40;
      this.cullingEnabled = mc.chunkCullingEnabled;
      mc.chunkCullingEnabled = false;
      this.updateLoadChunk();
      if (mc.worldRenderer != null) {
         mc.worldRenderer.scheduleTerrainUpdate();
      }

      updateFlagInput();
   }

   public Vec3d getLerpAmount(float lerpValue) {
      return new Vec3d(
         MathHelper.lerp((double)lerpValue, this.startIIi, this.endLerp),
         MathHelper.lerp((double)lerpValue, this.startLerp, this.endIIi),
         MathHelper.lerp((double)lerpValue, this.startPerspectiveRaw, this.endPerspectiveRaw)
      );
   }

   private void updateFlagForwardBodyUsesMovementKeys() {
      ClientPlayerEntity player = mc.player;
      this.updateFlag();
      if (this.bodyUsesMovementKeys.isEnabled()) {
         GameOptions options = mc.options;
         PlayerInput playerInput = player.input.playerInput;
         this.inputActive = this.inputActive | (canCodeCategory(options.forwardKey) || playerInput.forward());
         this.backPressed = this.backPressed | (canCodeCategory(options.backKey) || playerInput.backward());
         this.leftPressed = this.leftPressed | (canCodeCategory(options.leftKey) || playerInput.left());
         this.rightPressed = this.rightPressed | (canCodeCategory(options.rightKey) || playerInput.right());
         this.jumpPressed = this.jumpPressed | (canCodeCategory(options.jumpKey) || playerInput.jump());
         this.sneakPressed = this.sneakPressed | (canCodeCategory(options.sneakKey) || playerInput.sneak());
         this.forwardActive = this.forwardActive | (canCodeCategory(options.sprintKey) || playerInput.sprint());
      } else {
         this.sneakPressed = this.sneakPressed | player.isSneaking();
      }

      this.handleSpeedSideways(player);
      this.handleFlagVelocity(player, 1.0E-10, false);
   }

   public Freecam() {
      super("Freecam", IconsPngType.MOVEMENT);
      this.smoothing = new BooleanSetting("Smoothing", "Ease camera movement", true);
      this.showOwnBody = new BooleanSetting("Show Own Body", "Render your body while detached", true);
      this.booleanBase = new BooleanSetting("Show Hands", "Keep first-person hands visible", true);
      this.bodyUsesMovementKeys = new BooleanSetting("Body Uses Movement Keys", "Physical WASD also move the body", false);
      this.lookSensitivity = new DoubleSetting("Look Sensitivity", "Camera look sensitivity", 0.5, 0.1, 2.0, 0.05);
      this.integerHelper = new IntegerSetting("Chunk Distance", "Chunks kept loaded while detached", 12, 2, 32);
      this.addSettings(
         new ConfigSetting[]{
            this.doubleHelper,
            this.verticalSpeed,
            this.smoothing,
            this.showOwnBody,
            this.booleanBase,
            this.bodyUsesMovementKeys,
            this.lookSensitivity,
            this.integerHelper
         }
      );
      singleton = this;
   }

   private static int getViewDistance() {
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      int viewDistanceLimit = networkHandler != null ? ((AccessorClientPlayNetworkHandler)networkHandler).getChunkLoadDistance() : 0;
      return viewDistanceLimit > 0 ? viewDistanceLimit : mc.options.getClampedViewDistance();
   }

   private void handleSpeedSideways(ClientPlayerEntity clientPlayerEntity) {
      if (clientPlayerEntity.forwardSpeed > 0.015F) {
         this.inputActive = true;
      }

      if (clientPlayerEntity.forwardSpeed < -0.015F) {
         this.backPressed = true;
      }

      if (clientPlayerEntity.sidewaysSpeed > 0.015F) {
         this.leftPressed = true;
      }

      if (clientPlayerEntity.sidewaysSpeed < -0.015F) {
         this.rightPressed = true;
      }
   }

   public static void updateFlagInput() {
      Freecam singleton2 = singleton;
      if (singleton2 != null && isInstanceFlag() && mc.player != null && !isFlag()) {
         ClientPlayerEntity player = mc.player;
         GameOptions options = mc.options;
         boolean booleanValue = singleton2.bodyUsesMovementKeys.isEnabled();
         boolean inputFlag = booleanValue && canCodeCategory(options.forwardKey);
         boolean inputFlag2 = booleanValue && canCodeCategory(options.backKey);
         boolean inputFlag3 = booleanValue && canCodeCategory(options.leftKey);
         boolean inputFlag4 = booleanValue && canCodeCategory(options.rightKey);
         boolean inputFlag5 = singleton2.inputActive || inputFlag;
         boolean inputFlag6 = singleton2.backPressed || inputFlag2;
         boolean inputFlag7 = singleton2.leftPressed || inputFlag3;
         boolean inputFlag8 = singleton2.rightPressed || inputFlag4;
         boolean jumpPressed11 = singleton2.jumpPressed;
         boolean sneaking = singleton2.sneakPressed || booleanValue && canCodeCategory(options.sneakKey);
         boolean inputFlag9 = singleton2.forwardActive || booleanValue && canCodeCategory(options.sprintKey);
         if (booleanValue) {
            if (inputFlag2) {
               singleton2.inputActive = false;
            }

            if (inputFlag) {
               singleton2.backPressed = false;
            }

            if (inputFlag4) {
               singleton2.leftPressed = false;
            }

            if (inputFlag3) {
               singleton2.rightPressed = false;
            }
         }

         AccessorInput input = (AccessorInput)player.input;
         input.setPlayerInput(new PlayerInput(inputFlag5, inputFlag6, inputFlag7, inputFlag8, jumpPressed11, sneaking, inputFlag9));
         float inputValue = (inputFlag7 ? 1.0F : 0.0F) - (inputFlag8 ? 1.0F : 0.0F);
         float inputValue2 = (inputFlag5 ? 1.0F : 0.0F) - (inputFlag6 ? 1.0F : 0.0F);
         input.setMovementVector(new Vec2f(inputValue, inputValue2).normalize());
         player.setSneaking(sneaking);
      }
   }

   private void updateLoadChunk() {
      if (this.freecamActive && mc.world != null) {
         ClientChunkManager chunkManager = mc.world.getChunkManager();
         chunkManager.updateLoadDistance(Math.max(getViewDistance(), this.integerHelper.getValue()));
      }
   }

   public static boolean isInstanceFlag() {
      return singleton != null && singleton.isEnabled() && singleton.freecamActive;
   }

   public float getLerpForFloat(float delta) {
      return MathHelper.lerp(delta, this.iIiAmount, this.degrees);
   }

   private static double getAmount(KeyBinding keyBinding, KeyBinding keyBinding2) {
      return (canCodeCategory(keyBinding) ? 1.0 : 0.0) - (canCodeCategory(keyBinding2) ? 1.0 : 0.0);
   }

   @Override
   protected void onDisable() {
      boolean freecamActive5 = this.freecamActive;
      this.updateFlagInputActive();
      this.perspectiveSwitched = false;
      this.freecamActive = false;
      if (this.perspectiveRawActive) {
         mc.options.setPerspective(this.perspective);
         this.perspectiveRawActive = false;
      }

      if (freecamActive5) {
         boolean freecamFlag = mc.world != null && mc.world == this.world;
         boolean freecamFlag2 = isFlag();
         mc.chunkCullingEnabled = this.cullingEnabled;
         if (mc.interactionManager != null && !freecamFlag2) {
            mc.interactionManager.cancelBlockBreaking();
         }

         if (!freecamFlag2) {
            if (mc.options.getSneakToggled().getValue()) {
               ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, this.pressedRaw);
            }

            if (mc.options.getSprintToggled().getValue()) {
               ElytraAutoUtils.handleRawPressed(mc.options.sprintKey, this.pressedRawPerspectiveRaw);
            }
         }

         if (freecamFlag) {
            this.updateWorldSchedule();
         }

         if (freecamFlag && mc.player != null && this.iIiActive) {
            PlayerAbilities abilities = mc.player.getAbilities();
            if (abilities.allowFlying) {
               abilities.flying = this.flying;
            }
         }

         this.iIiActive = false;
         this.world = null;
      }
   }

   public static Freecam getInstance() {
      return singleton;
   }

   private void updateFlagPlayer() {
      if (!this.inputActive && !this.backPressed && !this.leftPressed && !this.rightPressed) {
         this.handleFlagVelocity(mc.player, 1.0E-8, true);
      }
   }

   public static boolean isFlag() {
      Module iliIiiliiiii = ModuleRegistry.getModule(AutoAreaMiner.class);
      if (iliIiiliiiii != null && iliIiiliiiii.isEnabled()) {
         return true;
      } else {
         Module module3 = ModuleRegistry.getModule(AutoBuilder.class);
         return module3 != null && module3.isEnabled();
      }
   }

   private void updateFlagInputActive() {
      this.inputActive = false;
      this.backPressed = false;
      this.leftPressed = false;
      this.rightPressed = false;
      this.jumpPressed = false;
      this.sneakPressed = false;
      this.forwardActive = false;
      this.perspectiveRestoreTicks = 0;
   }
}
