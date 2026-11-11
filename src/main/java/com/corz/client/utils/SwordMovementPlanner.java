package com.corz.client.utils;

import java.util.Random;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.PathFollower;
import com.corz.client.utils.PlayerCombatState;
import com.corz.client.utils.PlayerInputOverride;
import com.corz.client.utils.PreferAnchorData;
import com.corz.client.utils.AnchorRoutePlanner;
import com.corz.client.utils.SwordCombatMath;
import com.corz.client.utils.SwordAttributeAndHazardUtils;
import com.corz.client.utils.SwordBotConfig;

public final class SwordMovementPlanner {
   private final Random random;
   private int hitDelayCount;
   private int strafeSign = 1;
   public static final int DEFAULT_STRAFE_SIGN = 1;
   private double randomJitter;
   private int remainingTapTicks;
   private final AnchorRoutePlanner vecHash;
   private int lastSpacingDirection;
   private boolean critJumpActive;
   private long critJumpStartTick;
   private long nextStrafeSwapTick = Long.MIN_VALUE;
   private long jumpResetUntilTick;
   private long lastStrafeSwapTick = Long.MIN_VALUE;
   private boolean closeRangeActive;

   static boolean shouldStrafeWhileCharging(int entityPlayerLimit, int entityPlayerValue, boolean entityPlayerFlag, boolean entityPlayerFlag2, boolean entityPlayerFlag3) {
      return entityPlayerLimit >= 1 && entityPlayerLimit <= 2 && entityPlayerValue == 0 && !entityPlayerFlag && entityPlayerFlag2 && !entityPlayerFlag3;
   }

   public SwordMovementPlanner.Plan planNextMove(ClientPlayerEntity player, LivingEntity entity, SwordMovementPlanner.Ctx ctx) {
      if (player != null && entity != null && ctx != null) {
         Vec3d vec3d = new Vec3d(player.getX(), player.getY(), player.getZ());
         Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
         double[] yaw = getDirectionToTarget(player, vec3d, vec3d2);
         boolean onGround = player.isOnGround();
         boolean resetFlag = player.isTouchingWater() || player.isInLava();
         long tick = ctx.tick();
         if (this.nextStrafeSwapTick == Long.MIN_VALUE || tick >= this.nextStrafeSwapTick) {
            this.scheduleStrafeSwap(tick);
         }

         if (ctx.theirLook() < 8.0 && ctx.theirGap() < 4.2 && tick - this.lastStrafeSwapTick >= 8L) {
            this.scheduleStrafeSwap(tick);
         }

         boolean resetFlag2 = ctx.cfg().jumpReset() && this.jumpResetUntilTick != Long.MIN_VALUE && tick <= this.jumpResetUntilTick && onGround && !resetFlag;
         if (this.jumpResetUntilTick != Long.MIN_VALUE && tick > this.jumpResetUntilTick) {
            this.jumpResetUntilTick = Long.MIN_VALUE;
         }

         if (this.remainingTapTicks > 0) {
            this.remainingTapTicks--;
            return new SwordMovementPlanner.Plan(this.getTapMoveIntent(player, vec3d, yaw, ctx.cfg().strafe()), false, 0.95F, false, SwordMovementPlanner.Phase.TAP);
         } else {
            SwordMovementPlanner.Plan gapCharge = this.planEngagement(player, entity, vec3d, vec3d2, yaw, onGround, resetFlag, ctx);
            if (resetFlag2 && gapCharge.phase() != SwordMovementPlanner.Phase.CRIT) {
               this.jumpResetUntilTick = Long.MIN_VALUE;
               PlayerInputOverride.Intent dirSprint = PlayerInputOverride.Intent.moveToward(yaw[0], yaw[1], true).withJump(PlayerInputOverride.Tri.ON).asUrgent();
               return new SwordMovementPlanner.Plan(dirSprint, gapCharge.wantHit(), gapCharge.minCharge(), false, SwordMovementPlanner.Phase.RESET);
            } else {
               return gapCharge;
            }
         }
      } else {
         return SwordMovementPlanner.Plan.INSTANCE;
      }
   }

   public void reset() {
      this.strafeSign = this.random.nextBoolean() ? 1 : -1;
      this.nextStrafeSwapTick = Long.MIN_VALUE;
      this.lastStrafeSwapTick = Long.MIN_VALUE;
      this.remainingTapTicks = 0;
      this.closeRangeActive = false;
      this.critJumpActive = false;
      this.critJumpStartTick = Long.MIN_VALUE;
      this.hitDelayCount = 0;
      this.randomJitter = 0.0;
      this.jumpResetUntilTick = Long.MIN_VALUE;
      this.lastSpacingDirection = 0;
      if (this.vecHash != null) {
         this.vecHash.reset();
      }
   }

   private PlayerInputOverride.Intent getTapMoveIntent(ClientPlayerEntity player, Vec3d vec, double[] touchingLavaValues, boolean touchingLavaFlag) {
      int touchingLavaValue = this.closeRangeActive ? -1 : 0;
      double gapChargeValue = touchingLavaFlag ? (this.closeRangeActive ? 0.8 : 1.0) : 0.0;
      return touchingLavaValue == 0 && gapChargeValue == 0.0
         ? PlayerInputOverride.Intent.idle().asUrgent()
         : this.buildStrafeIntent(player, vec, touchingLavaValues, touchingLavaValue, gapChargeValue, false, false, player.isTouchingWater() || player.isInLava(), true);
   }

   public boolean isTapping() {
      return this.remainingTapTicks > 0;
   }

   public boolean isCritJumpActive() {
      return this.critJumpActive;
   }

   public SwordMovementPlanner(Random random, AnchorRoutePlanner vecHash) {
      this.critJumpStartTick = Long.MIN_VALUE;
      this.jumpResetUntilTick = Long.MIN_VALUE;
      this.random = random == null ? new Random() : random;
      this.vecHash = vecHash;
   }

   public PlayerInputOverride.Intent startWTap(ClientPlayerEntity player, LivingEntity entity, double randomValue, boolean randomFlag) {
      this.hitDelayCount = 0;
      this.critJumpActive = false;
      this.randomJitter = (this.random.nextDouble() * 2.0 - 1.0) * 0.06;
      this.closeRangeActive = SwordCombatMath.isWithinCloseRange(randomValue);
      this.remainingTapTicks = Math.max(0, SwordCombatMath.getStepCountForFlag(randomValue, false) - 1);
      if (player != null && entity != null) {
         Vec3d vec3d = new Vec3d(player.getX(), player.getY(), player.getZ());
         double[] yaw = getDirectionToTarget(player, vec3d, new Vec3d(entity.getX(), entity.getY(), entity.getZ()));
         return this.getTapMoveIntent(player, vec3d, yaw, randomFlag);
      } else {
         return PlayerInputOverride.Intent.idle().asUrgent();
      }
   }

   static boolean isChargeAndGapInWindow(int entityPlayerLimit, double entityPlayerLimit2) {
      return entityPlayerLimit >= 7 && entityPlayerLimit <= 10 && entityPlayerLimit2 >= 2.3 && entityPlayerLimit2 <= 5.0;
   }

   private SwordMovementPlanner.Plan planChase(
      ClientPlayerEntity player, Vec3d vec, Vec3d vec2, double[] chaseVecValues, boolean chaseVecFlag, boolean resetFlag, SwordMovementPlanner.Ctx ctx, boolean chaseVecFlag3
   ) { boolean var60_u = false;  boolean var58_u = false;  double var56_u = (double) 0;  double var54_u = (double) 0;  boolean var52_u = false; 
      boolean chaseVecFlag4 = chaseVecFlag3 && ctx.toCharge() == 0 && ctx.targetReadyIn() <= 0 && !ctx.targetBlocking();
      if (this.vecHash != null) {
         this.vecHash
            .setImmuneFireData(
               new PlayerCombatState(
                  Math.max(1.0F, player.getHealth() + player.getAbsorptionAmount()),
                  false,
                  0.0F,
                  -1,
                  player.isFireImmune(),
                  player.getHungerManager().getFoodLevel(),
                  player.isUsingItem(),
                  ctx.pingMs(),
                  0
               )
            );
         PathFollower.Cmd searchingRouted = this.vecHash
            .computeWalkCommand(
               ctx.tick(), vec, player.getVelocity(), chaseVecFlag, player.horizontalCollision, player.isTouchingWater(), PreferAnchorData.getMelee(vec2, null)
            );
         if (searchingRouted instanceof PathFollower.Cmd) {
            PathFollower.Cmd cmd = searchingRouted;

            try {
               var52_u = cmd.move();
            } catch (Throwable e) {
               throw new MatchException(e.toString(), e);
            }

            boolean var52_u2 = var52_u;
            cmd = searchingRouted;

            try {
               var54_u = cmd.dx();
            } catch (Throwable e2) {
               throw new MatchException(e2.toString(), e2);
            }

            double var54_u2 = var54_u;
            cmd = searchingRouted;

            try {
               var56_u = cmd.dz();
            } catch (Throwable e3) {
               throw new MatchException(e3.toString(), e3);
            }

            double var56_u2 = var56_u;
            cmd = searchingRouted;

            try {
               var58_u = cmd.jump();
            } catch (Throwable e4) {
               throw new MatchException(e4.toString(), e4);
            }

            boolean var58_u2 = var58_u;
            cmd = searchingRouted;

            try {
               var60_u = cmd.sprint();
            } catch (Throwable e5) {
               throw new MatchException(e5.toString(), e5);
            }

            boolean var60_u2 = var60_u;
            cmd = searchingRouted;

            try {
               cmd.done();
            } catch (Throwable e6) {
               throw new MatchException(e6.toString(), e6);
            }

            cmd = searchingRouted;

            try {
               cmd.offPath();
            } catch (Throwable e7) {
               throw new MatchException(e7.toString(), e7);
            }

            cmd = searchingRouted;

            try {
               cmd.index();
            } catch (Throwable e8) {
               throw new MatchException(e8.toString(), e8);
            }

            cmd = searchingRouted;

            try {
               cmd.remaining();
            } catch (Throwable e9) {
               throw new MatchException(e9.toString(), e9);
            }

            cmd = searchingRouted;

            try {
               cmd.next();
            } catch (Throwable e10) {
               throw new MatchException(e10.toString(), e10);
            }

            if (var52_u2) {
               boolean chaseVecFlag5 = var58_u2 || resetFlag || player.horizontalCollision && chaseVecFlag;
               PlayerInputOverride.Intent intent = new PlayerInputOverride.Intent(
                  true,
                  var54_u2,
                  var56_u2,
                  chaseVecFlag5 ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF,
                  PlayerInputOverride.Tri.OFF,
                  var60_u2 ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF,
                  false
               );
               return new SwordMovementPlanner.Plan(intent, chaseVecFlag4, 0.95F, false, SwordMovementPlanner.Phase.CHASE);
            }
         }
      }

      PlayerInputOverride.Intent intent2 = ctx.sightBlocked()
         ? this.buildStrafeIntent(player, vec, chaseVecValues, 0, 1.0, false, false, resetFlag, false)
         : this.buildStrafeIntent(player, vec, chaseVecValues, 1, 0.0, true, false, resetFlag, false);
      return new SwordMovementPlanner.Plan(intent2, chaseVecFlag4, 0.95F, false, SwordMovementPlanner.Phase.CHASE);
   }

   private PlayerInputOverride.Intent buildStrafeIntent(
      ClientPlayerEntity player, Vec3d vec, double[] groundEntityValues, int groundEntityFactor, double gapChargeValue, boolean groundEntityFlag, boolean groundEntityFlag2, boolean resetFlag, boolean gapAwayFlag
   ) {
      double groundEntityFactor3 = -groundEntityValues[1] * this.strafeSign;
      double groundEntityFactor4 = groundEntityValues[0] * this.strafeSign;
      boolean groundEntityFlag5 = groundEntityFlag2 || resetFlag;
      if (groundEntityFactor == 0 && !(gapChargeValue > 0.0)) {
         PlayerInputOverride.Intent intent = groundEntityFlag5
            ? PlayerInputOverride.Intent.idle().withJump(PlayerInputOverride.Tri.ON)
            : PlayerInputOverride.Intent.idle();
         return gapAwayFlag ? intent.asUrgent() : intent;
      } else {
         double[][] doubles = new double[][]{
            {groundEntityValues[0] * groundEntityFactor + groundEntityFactor3 * gapChargeValue, groundEntityValues[1] * groundEntityFactor + groundEntityFactor4 * gapChargeValue},
            {groundEntityValues[0] * groundEntityFactor - groundEntityFactor3 * gapChargeValue, groundEntityValues[1] * groundEntityFactor - groundEntityFactor4 * gapChargeValue},
            {groundEntityValues[0] * groundEntityFactor, groundEntityValues[1] * groundEntityFactor},
            {groundEntityFactor3, groundEntityFactor4},
            {-groundEntityFactor3, -groundEntityFactor4}
         };
         double[] groundEntityValues2 = null;
         int groundEntityValue = -1;

         for (int i = 0; i < doubles.length; i++) {
            double[] groundEntityValues3 = doubles[i];
            if (!(Math.hypot(groundEntityValues3[0], groundEntityValues3[1]) < 1.0E-6)
               && (!player.isOnGround() || resetFlag || SwordAttributeAndHazardUtils.isStepDirectionSafe(player.getEntityWorld(), vec, groundEntityValues3[0], groundEntityValues3[1]))) {
               groundEntityValues2 = groundEntityValues3;
               groundEntityValue = i;
               break;
            }
         }

         if (groundEntityValue == 1 || groundEntityValue == 4) {
            this.strafeSign = -this.strafeSign;
         }

         if (groundEntityValues2 == null) {
            PlayerInputOverride.Intent idle = PlayerInputOverride.Intent.idle();
            if (groundEntityFlag5) {
               idle = idle.withJump(PlayerInputOverride.Tri.ON);
            }

            return gapAwayFlag ? idle.asUrgent() : idle;
         } else {
            boolean groundEntityFlag6 = groundEntityValues2[0] * groundEntityValues[0] + groundEntityValues2[1] * groundEntityValues[1] > 1.0E-6;
            if (!groundEntityFlag5 && groundEntityFlag6 && player.horizontalCollision && player.isOnGround()) {
               groundEntityFlag5 = true;
            }

            PlayerInputOverride.Intent intent3 = new PlayerInputOverride.Intent(
               true,
               groundEntityValues2[0],
               groundEntityValues2[1],
               groundEntityFlag5 ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF,
               PlayerInputOverride.Tri.OFF,
               groundEntityFlag && groundEntityFlag6 ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF,
               false
            );
            return gapAwayFlag ? intent3.asUrgent() : intent3;
         }
      }
   }

   private void scheduleStrafeSwap(long time) {
      if (this.nextStrafeSwapTick != Long.MIN_VALUE) {
         this.strafeSign = -this.strafeSign;
      }

      this.lastStrafeSwapTick = time;
      this.nextStrafeSwapTick = time + 10L + this.random.nextInt(13);
   }

   private SwordMovementPlanner.Plan planEngagement(
      ClientPlayerEntity player, LivingEntity entity, Vec3d vec, Vec3d vec2, double[] gapChargeValues, boolean gapChargeFlag, boolean resetFlag, SwordMovementPlanner.Ctx ctx
   ) {
      SwordBotConfig cfg = ctx.cfg();
      double gapChargeBound = SwordCombatMath.getEngageRangeFromPing(ctx.pingMs(), ctx.closing()) - (cfg.aggressive() ? 0.2 : 0.0);
      double rangeThreshold = SwordCombatMath.getRangeThreshold(Math.max(1, ctx.toCharge()), gapChargeBound, this.randomJitter);
      boolean gapChargeFlag3 = ctx.gap() > 6.0 && ctx.gap() > rangeThreshold + 1.0 || Math.abs(ctx.heightDiff()) > 2.5 || ctx.sightBlocked();
      boolean gapChargeFlag4 = ctx.gap() <= 3.0;
      if (gapChargeFlag3) {
         return this.planChase(player, vec, vec2, gapChargeValues, gapChargeFlag, resetFlag, ctx, gapChargeFlag4);
      } else {
         boolean constFlag = ctx.theirGap() <= 3.15;
         boolean underFortyThreshold = SwordCombatMath.isUnderFortyThreshold(ctx.theirLook());
         boolean gapAwayFlag = constFlag && underFortyThreshold;
         if (this.critJumpActive && gapChargeFlag && ctx.tick() - this.critJumpStartTick > 2L) {
            this.critJumpActive = false;
         }

         if (this.critJumpActive) {
            double y = player.getVelocity().y;
            boolean gapChargeFlag8 = ctx.toCharge() <= 1 && y < 0.1;
            boolean gapChargeFlag9 = ctx.toCharge() == 0 && gapChargeFlag4 && !ctx.serverSprinting() && ctx.targetReadyIn() <= 0;
            boolean gapChargeFlag10 = !gapChargeFlag9 && gapAwayFlag && gapChargeFlag4 && ctx.toCharge() == 0 && ctx.targetReadyIn() <= 0;
            PlayerInputOverride.Intent intent = gapChargeFlag8
               ? this.buildStrafeIntent(player, vec, gapChargeValues, 0, cfg.strafe() ? 0.35 : 0.0, false, false, resetFlag, true)
               : this.buildStrafeIntent(player, vec, gapChargeValues, 1, 0.0, true, false, resetFlag, false);
            return new SwordMovementPlanner.Plan(intent, gapChargeFlag9 || gapChargeFlag10, 0.95F, gapChargeFlag9, SwordMovementPlanner.Phase.CRIT);
         } else if (cfg.crits() && gapChargeFlag && !resetFlag && shouldStartCritJump(ctx, gapAwayFlag)) {
            this.critJumpActive = true;
            this.critJumpStartTick = ctx.tick();
            PlayerInputOverride.Intent groundEntity = this.buildStrafeIntent(player, vec, gapChargeValues, 1, 0.0, true, true, resetFlag, true);
            return new SwordMovementPlanner.Plan(groundEntity, false, 0.95F, false, SwordMovementPlanner.Phase.CRIT);
         } else if (ctx.toCharge() == 0 && !ctx.targetBlocking()) {
            if (ctx.targetReadyIn() > 0) {
               int gapChargeLimit = SwordCombatMath.compareWithMargin(ctx.gap(), 3.0, 0.12);
               PlayerInputOverride.Intent strafeIntent2 = this.buildStrafeIntent(player, vec, gapChargeValues, gapChargeLimit, cfg.strafe() ? 1.0 : 0.0, gapChargeLimit > 0, false, resetFlag, false);
               return new SwordMovementPlanner.Plan(strafeIntent2, false, 0.95F, false, SwordMovementPlanner.Phase.SPACE);
            } else {
               boolean gapChargeFlag11 = gapChargeFlag4;
               if (gapChargeFlag4 && !ctx.serverSprinting() && !gapAwayFlag && ctx.closing() <= 0.05 && this.hitDelayCount < 1) {
                  gapChargeFlag11 = false;
                  this.hitDelayCount++;
               }

               int groundEntityFactor = ctx.gap() < 1.6 ? -1 : 1;
               double gapChargeValue = !cfg.strafe() ? 0.0 : (ctx.gap() > 3.5 ? 0.0 : 0.35);
               PlayerInputOverride.Intent strafeIntent3 = this.buildStrafeIntent(player, vec, gapChargeValues, groundEntityFactor, gapChargeValue, groundEntityFactor > 0, false, resetFlag, true);
               return new SwordMovementPlanner.Plan(strafeIntent3, gapChargeFlag11, 0.95F, false, SwordMovementPlanner.Phase.STRIKE);
            }
         } else {
            if (ctx.targetBlocking()) {
               rangeThreshold = Math.max(rangeThreshold, gapChargeBound);
            }

            int value = SwordCombatMath.getDeltaTrend(ctx.theirGap(), rangeThreshold, 0.12, this.lastSpacingDirection);
            if (value < 0 && ctx.awaySpeed() > 0.15) {
               value = 0;
            }

            this.lastSpacingDirection = value;
            boolean gapChargeFlag12 = SwordCombatMath.isAttackReady(constFlag, underFortyThreshold, gapChargeFlag4, ctx.charge(), ctx.targetReadyIn()) && !ctx.targetBlocking();
            if (shouldStrafeWhileCharging(ctx.toCharge(), value, ctx.serverSprinting(), cfg.strafe(), ctx.targetBlocking())) {
               PlayerInputOverride.Intent strafeIntent4 = this.buildStrafeIntent(player, vec, gapChargeValues, 1, 1.0, true, false, resetFlag, gapAwayFlag);
               return new SwordMovementPlanner.Plan(strafeIntent4, gapChargeFlag12, 0.6F, false, SwordMovementPlanner.Phase.SPACE);
            } else {
               double gapChargeValue2 = !cfg.strafe() ? 0.0 : (value == 0 ? 1.0 : (value < 0 ? 0.8 : 0.5));
               PlayerInputOverride.Intent strafeIntent5 = this.buildStrafeIntent(player, vec, gapChargeValues, value, gapChargeValue2, value > 0, false, resetFlag, value < 0 || gapAwayFlag);
               return new SwordMovementPlanner.Plan(strafeIntent5, gapChargeFlag12, 0.6F, false, value < 0 ? SwordMovementPlanner.Phase.BACK : SwordMovementPlanner.Phase.SPACE);
            }
         }
      }
   }

   private static double[] getDirectionToTarget(ClientPlayerEntity player, Vec3d vec, Vec3d vec2) {
      double deltaX = vec2.x - vec.x;
      double deltaZ = vec2.z - vec.z;
      double hypot = Math.hypot(deltaX, deltaZ);
      if (hypot > 0.001) {
         return new double[]{deltaX / hypot, deltaZ / hypot};
      } else {
         double angle = Math.toRadians(player.getYaw());
         return new double[]{-Math.sin(angle), Math.cos(angle)};
      }
   }

   public void armJumpReset(long entityPlayerValue) {
      this.jumpResetUntilTick = entityPlayerValue + 3L;
   }

   public int getStrafeSign() {
      return this.strafeSign;
   }

   private static boolean shouldStartCritJump(SwordMovementPlanner.Ctx ctx, boolean gapAwayFlag) {
      int charge = ctx.toCharge();
      boolean gapAwayFlag2 = ctx.awaySpeed() > 0.08;
      boolean gapAwayFlag3 = ctx.sinceHit() <= 5L && ctx.lastHitSprint() && gapAwayFlag2 && charge >= 7 && charge <= 10 && ctx.gap() >= 1.2 && ctx.gap() <= 5.0;
      if (gapAwayFlag3) {
         return true;
      } else if (isChargeAndGapInWindow(charge, ctx.gap())) {
         return true;
      } else if (gapAwayFlag || charge < 3 || charge > 10 || ctx.gap() < 2.3 || ctx.gap() > 4.2) {
         return false;
      } else {
         return SwordCombatMath.exceedsBothThresholds(ctx.theirLook(), ctx.awaySpeed()) ? true : ctx.cfg().aggressive() && ctx.theirLook() > 60.0;
      }
   }

   public record Ctx(
      long tick,
      double gap,
      double theirGap,
      int toCharge,
      float charge,
      double theirLook,
      double awaySpeed,
      double closing,
      int targetReadyIn,
      boolean serverSprinting,
      int pingMs,
      SwordBotConfig cfg,
      boolean targetBlocking,
      double heightDiff,
      long sinceHit,
      boolean lastHitSprint,
      boolean sightBlocked
   ) {
   }

   public static enum Phase {
      IDLE("Looking"),
      CHASE("Chasing"),
      SPACE("Spacing"),
      STRIKE("Striking"),
      TAP("W-tap"),
      BACK("Backing off"),
      CRIT("Crit jump"),
      RESET("Jump reset");
      public final String displayText;

      private Phase(String text) {
         this.displayText = text;
      }
   }

   public record Plan(PlayerInputOverride.Intent intent, boolean wantHit, float minCharge, boolean crit, SwordMovementPlanner.Phase phase) {
      public static final SwordMovementPlanner.Plan INSTANCE = new SwordMovementPlanner.Plan(
         PlayerInputOverride.Intent.idle(), false, 0.95F, false, SwordMovementPlanner.Phase.IDLE
      );
   }
}
