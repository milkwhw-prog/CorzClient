package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.RangeSetting;
import com.corz.client.gui.DisabledEnabled;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class Reach extends Module {
   private final IntegerSetting chanceSetting;
   private final Reach.Engagement engagement;
   private double rolledReachDistance;
   private final EnumSetting chanceModeSetting;
   static final String ADVANCED = "Advanced";
   private final BooleanSetting sprintOnlySetting;
   static final String NORMAL = "Normal";
   private final RangeSetting range = new RangeSetting(
      "Range", "How far you can hit, in blocks (normal is 3). Higher lands more hits but is easier to get caught", 3.0, 3.1, 3.0, 6.0, 0.05
   );
   private static boolean riskWarningShown;
   private final BooleanSetting disableInWater;
   private double activeReachDistance;

   public double getActiveReachDistance() {
      return this.activeReachDistance;
   }

   public Reach() {
      super("Reach", IconsPngType.COMBAT);
      this.chanceSetting = new IntegerSetting("Chance", "How often the extra reach turns on, in percent. Lower is harder to notice", 50, 0, 100);
      this.chanceModeSetting = new EnumSetting(
         "Chance Mode",
         "Advanced: rolls when you aim at someone and turns off after your hit. Normal: rolls again every tick",
         "Advanced",
         "Advanced",
         "Normal"
      );
      this.sprintOnlySetting = new BooleanSetting("Only While Sprinting", "Only give extra reach while you are sprinting", false);
      this.disableInWater = new BooleanSetting("Disable In Water", "No extra reach while you are in water or lava", false);
      this.engagement = new Reach.Engagement();
      this.addSettings(new ConfigSetting[]{this.range, this.chanceSetting, this.chanceModeSetting, this.sprintOnlySetting, this.disableInWater});
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke && !inventoryPlayer.isOpenedByClient()) {
         if (mc != null && mc.isOnThread() && "Advanced".equals(this.chanceModeSetting.getCurrentOption())) {
            if (PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
               this.engagement.clearRollResult();
               this.activeReachDistance = 0.0;
            }
         }
      }
   }

   private boolean canApplyReach(ClientPlayerEntity clientPlayerEntity) {
      return !this.disableInWater.isEnabled() || !clientPlayerEntity.isTouchingWater() && !clientPlayerEntity.isInLava()
         ? !this.sprintOnlySetting.isEnabled() || clientPlayerEntity.isSprinting()
         : false;
   }

   @Override
   protected void onEnable() {
      this.resetReachState();
      if (!riskWarningShown && mc != null && mc.player != null) {
         riskWarningShown = true;
         DisabledEnabled.getInstance().handleDisabledEnabled("Reach is risky", "Most anticheats detect extra reach", false);
      }
   }

   private void resetReachState() {
      this.engagement.reset();
      this.rolledReachDistance = 0.0;
      this.activeReachDistance = 0.0;
   }

   @Override
   protected void onDisable() {
      this.resetReachState();
   }

   private boolean rollReachChance() {
      return ThreadLocalRandom.current().nextInt(100) < this.chanceSetting.getValue();
   }

   @Override
   protected void onReset() {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.world != null) {
         boolean touchingSprinting = this.canApplyReach(player);
         double iIiBound = Math.min(this.range.getMin(), this.range.getMax());
         double reachValue = Math.max(this.range.getMin(), this.range.getMax());
         if ("Normal".equals(this.chanceModeSetting.getCurrentOption())) {
            this.engagement.reset();
            this.activeReachDistance = touchingSprinting && this.rollReachChance() ? randomReachBetween(iIiBound, reachValue) : 0.0;
         } else {
            Object obj = mc.getCameraEntity() != null ? mc.getCameraEntity() : player;
            Entity posRaycast = raycastTargetEntity((Entity)obj, player.getBlockInteractionRange(), reachValue);
            if (this.engagement.rollForTarget(posRaycast == null ? Integer.MIN_VALUE : posRaycast.getId(), touchingSprinting, this::rollReachChance)) {
               this.rolledReachDistance = randomReachBetween(iIiBound, reachValue);
            }

            this.activeReachDistance = touchingSprinting && this.engagement.isRollSucceeded() ? Math.max(iIiBound, Math.min(reachValue, this.rolledReachDistance)) : 0.0;
         }
      } else {
         this.resetReachState();
      }
   }

   static Entity raycastTargetEntity(Entity entity, double posRaycastBound, double reachValue) {
      double maxCamera = Math.max(posRaycastBound, reachValue);
      double posRaycastValue = maxCamera * maxCamera;
      Vec3d cameraPosVec = entity.getCameraPosVec(1.0F);
      HitResult raycast = entity.raycast(maxCamera, 1.0F, false);
      double squaredDistanceTo = raycast.getPos().squaredDistanceTo(cameraPosVec);
      if (raycast.getType() != Type.MISS) {
         posRaycastValue = squaredDistanceTo;
         maxCamera = Math.sqrt(squaredDistanceTo);
      }

      Vec3d rotationVec = entity.getRotationVec(1.0F);
      Vec3d add = cameraPosVec.add(rotationVec.x * maxCamera, rotationVec.y * maxCamera, rotationVec.z * maxCamera);
      Box expand = entity.getBoundingBox().stretch(rotationVec.multiply(maxCamera)).expand(1.0, 1.0, 1.0);
      EntityHitResult raycast2 = ProjectileUtil.raycast(entity, cameraPosVec, add, expand, EntityPredicates.CAN_HIT, posRaycastValue);
      if (raycast2 != null && !(raycast2.getPos().squaredDistanceTo(cameraPosVec) >= squaredDistanceTo)) {
         return raycast2.getPos().isInRange(cameraPosVec, reachValue) ? raycast2.getEntity() : null;
      } else {
         return null;
      }
   }

   static double randomReachBetween(double reachLimit, double reachValue) {
      return reachValue <= reachLimit ? reachLimit : reachLimit + ThreadLocalRandom.current().nextDouble() * (reachValue - reachLimit);
   }

   static final class Engagement {
      private int engagedEntityId = Integer.MIN_VALUE;
      private boolean rollSucceeded;
      private int engagementTicksRemaining;

      void clearRollResult() {
         this.rollSucceeded = false;
      }

      boolean isRollSucceeded() {
         return this.rollSucceeded;
      }

      void reset() {
         this.engagementTicksRemaining = 0;
         this.engagedEntityId = Integer.MIN_VALUE;
         this.rollSucceeded = false;
      }

      int getEngagementTicksRemaining() {
         return this.engagementTicksRemaining;
      }

      boolean rollForTarget(int engagementValue, boolean engagementFlag, BooleanSupplier booleanSupplier) {
         if (this.engagementTicksRemaining > 0) {
            this.engagementTicksRemaining--;
         }

         if (engagementValue != Integer.MIN_VALUE && (this.engagedEntityId == Integer.MIN_VALUE || engagementValue == this.engagedEntityId)) {
            this.engagedEntityId = engagementValue;
            if (engagementFlag && this.engagementTicksRemaining <= 0) {
               this.rollSucceeded = booleanSupplier.getAsBoolean();
               this.engagementTicksRemaining = 10;
               return this.rollSucceeded;
            } else {
               return false;
            }
         } else {
            this.engagedEntityId = Integer.MIN_VALUE;
            return false;
         }
      }
   }
}
