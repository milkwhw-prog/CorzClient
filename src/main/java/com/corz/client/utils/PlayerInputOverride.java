package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.mixins.AccessorInput;
import com.corz.client.render.CodeCategoryUtils;
import com.corz.client.world.MathClampUtils;
import java.lang.ref.WeakReference;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.KeyInputSimulation;

public final class PlayerInputOverride {
   private static final int[] ZERO_DIRECTION = new int[]{0, 0};
   private static boolean rotationOverrideActive;
   private static final KeyInputSimulation.Dwell INPUT_DWELL = new KeyInputSimulation.Dwell(new Random());
   private static final KeyInputSimulation.SprintGuard SPRINT_GUARD = new KeyInputSimulation.SprintGuard();
   private static boolean inputAppliedThisTick;
   private static final PlayerInputOverride.TeleportFreeze TELEPORT_FREEZE = new PlayerInputOverride.TeleportFreeze();
   private static WeakReference<ClientPlayerEntity> lastPlayerRef = new WeakReference<>(null);
   private static int[] lastDirection = PlayerInputOverride.ZERO_DIRECTION;
   private static PlayerInputOverride.Request pendingRequest;
   private static boolean physicalKeysPressed;
   private static long lastInputTick = Long.MIN_VALUE;
   private static KeyInputSimulation.Keys lastAppliedKeys = KeyInputSimulation.Keys.INSTANCE;

   public static void applyInputOverride(AccessorInput accessorInput, ClientPlayerEntity player) { boolean var75_u = false;  boolean var73_u = false;  boolean var71_u = false; 
      inputAppliedThisTick = false;
      rotationOverrideActive = false;
      physicalKeysPressed = false;
      if (accessorInput != null && player != null) {
         if (lastPlayerRef.get() != player) {
            INPUT_DWELL.resetToKeys(KeyInputSimulation.Keys.INSTANCE);
            SPRINT_GUARD.reset();
            TELEPORT_FREEZE.reset();
            lastPlayerRef = new WeakReference<>(player);
            lastDirection = ZERO_DIRECTION;
            lastInputTick = Long.MIN_VALUE;
         }

         PlayerInputOverride.Request pendingRequest1312 = pendingRequest;
         pendingRequest = null;
         long teleportFreezeLimit = ClientThreadUtils.getTime();
         boolean inputBackwardFlag = TELEPORT_FREEZE.isFrozen(teleportFreezeLimit, ConnectionTimeUtils.getTime(), ConnectionTimeUtils.getTimeTimeMillis());
         if (pendingRequest1312 != null && pendingRequest1312.tick() == teleportFreezeLimit) {
            MinecraftClient instance = MinecraftClient.getInstance();
            if (instance != null && instance.options != null) {
               if (instance.currentScreen == null && player.isAlive() && !player.hasVehicle() && !player.isSpectator() && !player.getAbilities().flying && !player.isGliding()) {
                  physicalKeysPressed = isAnyMovementKeyDown(instance.options);
                  if (!physicalKeysPressed) {
                     if (!MathClampUtils.isClampActive() || MathClampUtils.isClampSettled()) {
                        PlayerInput playerInput = accessorInput.getPlayerInput();
                        if (playerInput instanceof PlayerInput) {
                           PlayerInput playerInput2 = playerInput;

                           try {
                              playerInput2.forward();
                           } catch (Throwable e) {
                              throw new MatchException(e.toString(), e);
                           }

                           playerInput2 = playerInput;

                           try {
                              playerInput2.backward();
                           } catch (Throwable e2) {
                              throw new MatchException(e2.toString(), e2);
                           }

                           playerInput2 = playerInput;

                           try {
                              playerInput2.left();
                           } catch (Throwable e3) {
                              throw new MatchException(e3.toString(), e3);
                           }

                           playerInput2 = playerInput;

                           try {
                              playerInput2.right();
                           } catch (Throwable e4) {
                              throw new MatchException(e4.toString(), e4);
                           }

                           playerInput2 = playerInput;

                           try {
                              var71_u = playerInput2.jump();
                           } catch (Throwable e5) {
                              throw new MatchException(e5.toString(), e5);
                           }

                           boolean var71_u2 = var71_u;
                           playerInput2 = playerInput;

                           try {
                              var73_u = playerInput2.sneak();
                           } catch (Throwable e6) {
                              throw new MatchException(e6.toString(), e6);
                           }

                           boolean var73_u2 = var73_u;
                           playerInput2 = playerInput;

                           try {
                              var75_u = playerInput2.sprint();
                           } catch (Throwable e7) {
                              throw new MatchException(e7.toString(), e7);
                           }

                           boolean var75_u2 = var75_u;
                           PlayerInputOverride.Intent intent = pendingRequest1312.intent();
                           if (intent instanceof PlayerInputOverride.Intent(boolean inputBackwardFlag2, double inputBackwardValue, double inputBackwardValue2, PlayerInputOverride.Tri tri, PlayerInputOverride.Tri tri2, PlayerInputOverride.Tri tri3, boolean inputBackwardFlag3)
                              )
                            {
                              boolean forwardFlag = inputBackwardFlag3;
                              boolean clampSettled = MathClampUtils.isClampSettled();
                              float degrees = clampSettled ? MathClampUtils.getYaw() : player.getYaw();
                              if (lastInputTick != teleportFreezeLimit - 1L) {
                                 INPUT_DWELL.syncChangedKeys(fromPlayerInput(player.getLastPlayerInput()));
                              }

                              boolean floorMin = FloorMathUtilsBlock.canFloorMin(player);
                              boolean inputBackwardFlag5 = floorMin || inputBackwardFlag;
                              int[] values3 = !inputBackwardFlag5 && inputBackwardFlag2 ? KeyInputSimulation.chooseKeyDirection(inputBackwardValue, inputBackwardValue2, degrees, lastDirection) : new int[]{0, 0};
                              lastDirection = values3;
                              boolean inputBackwardFlag6 = inputBackwardFlag || player.isOnGround() && (FloorMathUtilsBlock.canFloorMinForClientPlayerEntity(player) || floorMin);
                              KeyInputSimulation.Keys keys = new KeyInputSimulation.Keys(
                                 values3[1] > 0,
                                 values3[1] < 0,
                                 values3[0] > 0,
                                 values3[0] < 0,
                                 resolveTriState(tri, var71_u2) && !inputBackwardFlag6,
                                 resolveTriState(tri2, var73_u2),
                                 resolveTriState(tri3, var75_u2) && !inputBackwardFlag5
                              );
                              KeyInputSimulation.Keys guardedKeys = SPRINT_GUARD.computeGuardedKeys(
                                 INPUT_DWELL,
                                 teleportFreezeLimit,
                                 keys,
                                 forwardFlag,
                                 tri3 == PlayerInputOverride.Tri.OFF,
                                 player.isSprinting() && !player.isSwimming(),
                                 isMovingForward(player.getLastPlayerInput()),
                                 getSprintWindow(instance.options)
                              );
                              if (inputBackwardFlag6 && isJumpKeySet(guardedKeys)) {
                                 guardedKeys = withoutJump(guardedKeys);
                                 INPUT_DWELL.syncChangedKeys(guardedKeys);
                              }

                              if (inputBackwardFlag5 && hasMovementOrSprintKey(guardedKeys)) {
                                 guardedKeys = keepOnlyJumpAndSneak(guardedKeys);
                                 INPUT_DWELL.syncChangedKeys(guardedKeys);
                              }

                              accessorInput.setPlayerInput(toPlayerInput(guardedKeys));
                              float[] forwardBack = KeyInputSimulation.getMovementVector(guardedKeys);
                              accessorInput.setMovementVector(new Vec2f(forwardBack[0], forwardBack[1]));
                              lastAppliedKeys = guardedKeys;
                              inputAppliedThisTick = true;
                              rotationOverrideActive = clampSettled;
                              lastInputTick = teleportFreezeLimit;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   static KeyInputSimulation.Keys keepOnlyJumpAndSneak(KeyInputSimulation.Keys guardedKeys) { boolean var30_u = false;  boolean var28_u = false; 
      if (guardedKeys instanceof KeyInputSimulation.Keys) {
         KeyInputSimulation.Keys guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.forward();
         } catch (Throwable e) {
            throw new MatchException(e.toString(), e);
         }

         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.back();
         } catch (Throwable e2) {
            throw new MatchException(e2.toString(), e2);
         }

         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.left();
         } catch (Throwable e3) {
            throw new MatchException(e3.toString(), e3);
         }

         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.right();
         } catch (Throwable e4) {
            throw new MatchException(e4.toString(), e4);
         }

         guardedKeys3 = guardedKeys;

         try {
            var28_u = guardedKeys3.jump();
         } catch (Throwable e5) {
            throw new MatchException(e5.toString(), e5);
         }

         boolean var28_u2 = var28_u;
         guardedKeys3 = guardedKeys;

         try {
            var30_u = guardedKeys3.sneak();
         } catch (Throwable e6) {
            throw new MatchException(e6.toString(), e6);
         }

         boolean var30_u2 = var30_u;
         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.sprint();
         } catch (Throwable e7) {
            throw new MatchException(e7.toString(), e7);
         }

         return new KeyInputSimulation.Keys(false, false, false, false, var28_u2, var30_u2, false);
      } else {
         return KeyInputSimulation.Keys.INSTANCE;
      }
   }

   public static boolean isRotationOverrideActive() {
      return rotationOverrideActive;
   }

   public static void cancelRequestFrom(Object obj) {
      PlayerInputOverride.Request pendingRequest1312 = pendingRequest;
      if (obj != null && pendingRequest1312 != null && pendingRequest1312.owner() == obj) {
         pendingRequest = null;
      }
   }

   private static boolean isMovingForward(PlayerInput playerInput) {
      return playerInput != null && playerInput.forward() && !playerInput.backward();
   }

   static boolean hasMovementOrSprintKey(KeyInputSimulation.Keys guardedKeys) { boolean var34_u = false;  boolean var30_u = false;  boolean var28_u = false;  boolean var26_u = false;  boolean var24_u = false; 
      if (guardedKeys instanceof KeyInputSimulation.Keys) {
         KeyInputSimulation.Keys guardedKeys3 = guardedKeys;

         try {
            var24_u = guardedKeys3.forward();
         } catch (Throwable e) {
            throw new MatchException(e.toString(), e);
         }

         boolean var24_u2 = var24_u;
         guardedKeys3 = guardedKeys;

         try {
            var26_u = guardedKeys3.back();
         } catch (Throwable e2) {
            throw new MatchException(e2.toString(), e2);
         }

         boolean var26_u2 = var26_u;
         guardedKeys3 = guardedKeys;

         try {
            var28_u = guardedKeys3.left();
         } catch (Throwable e3) {
            throw new MatchException(e3.toString(), e3);
         }

         boolean var28_u2 = var28_u;
         guardedKeys3 = guardedKeys;

         try {
            var30_u = guardedKeys3.right();
         } catch (Throwable e4) {
            throw new MatchException(e4.toString(), e4);
         }

         boolean var30_u2 = var30_u;
         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.jump();
         } catch (Throwable e5) {
            throw new MatchException(e5.toString(), e5);
         }

         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.sneak();
         } catch (Throwable e6) {
            throw new MatchException(e6.toString(), e6);
         }

         guardedKeys3 = guardedKeys;

         try {
            var34_u = guardedKeys3.sprint();
         } catch (Throwable e7) {
            throw new MatchException(e7.toString(), e7);
         }

         boolean var34_u2 = var34_u;
         if (var24_u2 || var26_u2 || var28_u2 || var30_u2 || var34_u2) {
            return true;
         }
      }

      return false;
   }

   private static PlayerInput toPlayerInput(KeyInputSimulation.Keys guardedKeys) {
      switch (guardedKeys) {
         case KeyInputSimulation.Keys(boolean forwardSprintFlag, boolean forwardSprintFlag2, boolean forwardSprintFlag3, boolean forwardSprintFlag4, boolean forwardSprintFlag5, boolean forwardSprintFlag6, boolean forwardSprintFlag7):
            return new PlayerInput(forwardSprintFlag, forwardSprintFlag2, forwardSprintFlag3, forwardSprintFlag4, forwardSprintFlag5, forwardSprintFlag6, forwardSprintFlag7 && forwardSprintFlag && !forwardSprintFlag2);
         default:
            throw new MatchException(null, null);
      }
   }

   public static boolean arePhysicalKeysPressed() {
      return physicalKeysPressed;
   }

   private static boolean resolveTriState(PlayerInputOverride.Tri tri, boolean inputPlayerFlag) {
      if (tri == PlayerInputOverride.Tri.ON) {
         return true;
      } else {
         return tri == PlayerInputOverride.Tri.OFF ? false : inputPlayerFlag;
      }
   }

   public static void updateFlagTick() {
      PlayerInputOverride.Request pendingRequest1312 = pendingRequest;
      if (pendingRequest1312 != null && pendingRequest1312.tick() != ClientThreadUtils.getTime()) {
         pendingRequest = null;
      }

      inputAppliedThisTick = false;
      rotationOverrideActive = false;
   }

   public static void submitRequest(Object obj, PlayerInputOverride.Intent intent, int prioTickLimit) {
      if (obj != null && intent != null) {
         long prioTickValue = ClientThreadUtils.getTime();
         PlayerInputOverride.Request pendingRequest1312 = pendingRequest;
         if (pendingRequest1312 == null || pendingRequest1312.tick() != prioTickValue || prioTickLimit > pendingRequest1312.prio()) {
            pendingRequest = new PlayerInputOverride.Request(obj, intent, prioTickLimit, prioTickValue);
         }
      }
   }

   private PlayerInputOverride() {
   }

   public static KeyInputSimulation.Keys getLastAppliedKeys() {
      return lastAppliedKeys;
   }

   public static void resetState() {
      pendingRequest = null;
      INPUT_DWELL.resetToKeys(KeyInputSimulation.Keys.INSTANCE);
      SPRINT_GUARD.reset();
      TELEPORT_FREEZE.reset();
      lastPlayerRef = new WeakReference<>(null);
      lastDirection = ZERO_DIRECTION;
      lastInputTick = Long.MIN_VALUE;
      inputAppliedThisTick = false;
      rotationOverrideActive = false;
      physicalKeysPressed = false;
      lastAppliedKeys = KeyInputSimulation.Keys.INSTANCE;
   }

   public static boolean wasInputAppliedThisTick() {
      return inputAppliedThisTick;
   }

   private static boolean isAnyMovementKeyDown(GameOptions gameOptions) {
      return CodeCategoryUtils.isKeyPressed(gameOptions.forwardKey)
         || CodeCategoryUtils.isKeyPressed(gameOptions.backKey)
         || CodeCategoryUtils.isKeyPressed(gameOptions.leftKey)
         || CodeCategoryUtils.isKeyPressed(gameOptions.rightKey)
         || CodeCategoryUtils.isKeyPressed(gameOptions.jumpKey)
         || CodeCategoryUtils.isKeyPressed(gameOptions.sneakKey);
   }

   static boolean isJumpKeySet(KeyInputSimulation.Keys guardedKeys) { boolean var28_u = false; 
      if (guardedKeys instanceof KeyInputSimulation.Keys) {
         KeyInputSimulation.Keys guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.forward();
         } catch (Throwable e) {
            throw new MatchException(e.toString(), e);
         }

         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.back();
         } catch (Throwable e2) {
            throw new MatchException(e2.toString(), e2);
         }

         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.left();
         } catch (Throwable e3) {
            throw new MatchException(e3.toString(), e3);
         }

         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.right();
         } catch (Throwable e4) {
            throw new MatchException(e4.toString(), e4);
         }

         guardedKeys3 = guardedKeys;

         try {
            var28_u = guardedKeys3.jump();
         } catch (Throwable e5) {
            throw new MatchException(e5.toString(), e5);
         }

         boolean var28_u2 = var28_u;
         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.sneak();
         } catch (Throwable e6) {
            throw new MatchException(e6.toString(), e6);
         }

         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.sprint();
         } catch (Throwable e7) {
            throw new MatchException(e7.toString(), e7);
         }

         if (var28_u2) {
            return true;
         }
      }

      return false;
   }

   static KeyInputSimulation.Keys withoutJump(KeyInputSimulation.Keys guardedKeys) { boolean var35_u = false;  boolean var33_u = false;  boolean var30_u = false;  boolean var28_u = false;  boolean var26_u = false;  boolean var24_u = false; 
      if (guardedKeys instanceof KeyInputSimulation.Keys) {
         KeyInputSimulation.Keys guardedKeys3 = guardedKeys;

         try {
            var24_u = guardedKeys3.forward();
         } catch (Throwable e) {
            throw new MatchException(e.toString(), e);
         }

         boolean var24_u2 = var24_u;
         guardedKeys3 = guardedKeys;

         try {
            var26_u = guardedKeys3.back();
         } catch (Throwable e2) {
            throw new MatchException(e2.toString(), e2);
         }

         boolean var26_u2 = var26_u;
         guardedKeys3 = guardedKeys;

         try {
            var28_u = guardedKeys3.left();
         } catch (Throwable e3) {
            throw new MatchException(e3.toString(), e3);
         }

         boolean var28_u2 = var28_u;
         guardedKeys3 = guardedKeys;

         try {
            var30_u = guardedKeys3.right();
         } catch (Throwable e4) {
            throw new MatchException(e4.toString(), e4);
         }

         boolean var30_u2 = var30_u;
         guardedKeys3 = guardedKeys;

         try {
            guardedKeys3.jump();
         } catch (Throwable e5) {
            throw new MatchException(e5.toString(), e5);
         }

         guardedKeys3 = guardedKeys;

         try {
            var33_u = guardedKeys3.sneak();
         } catch (Throwable e6) {
            throw new MatchException(e6.toString(), e6);
         }

         boolean var33_u2 = var33_u;
         guardedKeys3 = guardedKeys;

         try {
            var35_u = guardedKeys3.sprint();
         } catch (Throwable e7) {
            throw new MatchException(e7.toString(), e7);
         }

         boolean var35_u2 = var35_u;
         return new KeyInputSimulation.Keys(var24_u2, var26_u2, var28_u2, var30_u2, false, var33_u2, var35_u2);
      } else {
         return KeyInputSimulation.Keys.INSTANCE;
      }
   }

   private static KeyInputSimulation.Keys fromPlayerInput(PlayerInput playerInput) {
      return playerInput instanceof PlayerInput(boolean backwardForwardFlag, boolean backwardForwardFlag2, boolean backwardForwardFlag3, boolean backwardForwardFlag4, boolean backwardForwardFlag5, boolean backwardForwardFlag6, boolean backwardForwardFlag7)
         ? new KeyInputSimulation.Keys(backwardForwardFlag, backwardForwardFlag2, backwardForwardFlag3, backwardForwardFlag4, backwardForwardFlag5, backwardForwardFlag6, backwardForwardFlag7)
         : KeyInputSimulation.Keys.INSTANCE;
   }

   private static int getSprintWindow(GameOptions gameOptions) {
      SimpleOption sprintWindow = gameOptions.getSprintWindow();
      Integer integer = sprintWindow != null ? (Integer)sprintWindow.getValue() : null;
      return integer != null ? Math.max(0, integer) : 7;
   }

   public record Intent(
      boolean move, double dirX, double dirZ, PlayerInputOverride.Tri jump, PlayerInputOverride.Tri sneak, PlayerInputOverride.Tri sprint, boolean urgent
   ) {
      public static PlayerInputOverride.Intent moveToward(double sprint, double dirX, boolean dirZ) {
         return new PlayerInputOverride.Intent(
            true, sprint, dirX, PlayerInputOverride.Tri.OFF, PlayerInputOverride.Tri.OFF, dirZ ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF, false
         );
      }

      public PlayerInputOverride.Intent withJump(PlayerInputOverride.Tri t) {
         return new PlayerInputOverride.Intent(this.move, this.dirX, this.dirZ, t, this.sneak, this.sprint, this.urgent);
      }

      public PlayerInputOverride.Intent withSneak(PlayerInputOverride.Tri t) {
         return new PlayerInputOverride.Intent(this.move, this.dirX, this.dirZ, this.jump, t, this.sprint, this.urgent);
      }

      public static PlayerInputOverride.Intent idle() {
         return new PlayerInputOverride.Intent(false, 0.0, 0.0, PlayerInputOverride.Tri.OFF, PlayerInputOverride.Tri.OFF, PlayerInputOverride.Tri.OFF, false);
      }

      public PlayerInputOverride.Intent asUrgent() {
         return new PlayerInputOverride.Intent(this.move, this.dirX, this.dirZ, this.jump, this.sneak, this.sprint, true);
      }

      public PlayerInputOverride.Intent withoutMovement() {
         return new PlayerInputOverride.Intent(false, 0.0, 0.0, this.jump, PlayerInputOverride.Tri.OFF, this.sprint, false);
      }

      public PlayerInputOverride.Intent withSprint(PlayerInputOverride.Tri t) {
         return new PlayerInputOverride.Intent(this.move, this.dirX, this.dirZ, this.jump, this.sneak, t, this.urgent);
      }
   }

   private record Request(Object owner, PlayerInputOverride.Intent intent, int prio, long tick) {
   }

   static final class TeleportFreeze {
      private long freezeStartMs;
      private long lastSeenTime = -1L;

      boolean isFrozen(long teleportFreezeLimit, long time, long teleportFreezeLimit2) {
         if (time < 0L) {
            return false;
         } else {
            if (time != this.lastSeenTime) {
               this.lastSeenTime = time;
               this.freezeStartMs = teleportFreezeLimit2 - time <= 1000L ? teleportFreezeLimit : Long.MIN_VALUE;
            }

            boolean teleportFreezeFlag = this.freezeStartMs != Long.MIN_VALUE && teleportFreezeLimit >= this.freezeStartMs && teleportFreezeLimit - this.freezeStartMs < 2L;
            boolean teleportFreezeFlag2 = teleportFreezeLimit2 >= time && teleportFreezeLimit2 - time < 120L;
            return teleportFreezeFlag || teleportFreezeFlag2;
         }
      }

      TeleportFreeze() {
         this.freezeStartMs = Long.MIN_VALUE;
      }

      void reset() {
         this.lastSeenTime = -1L;
         this.freezeStartMs = Long.MIN_VALUE;
      }
   }

   public static enum Tri {
      PASS,
      ON,
      OFF;
   }
}
