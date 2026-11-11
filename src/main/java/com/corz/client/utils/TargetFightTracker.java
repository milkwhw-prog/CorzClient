package com.corz.client.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.SwordCombatMath;
import com.corz.client.utils.SwordAttributeAndHazardUtils;

public final class TargetFightTracker {
   private int killCount;
   private int hitsTakenInRow;
   private String targetName;
   private int hitsLandedCount;
   private long lastHitLandedTick;
   private long lastHitTakenTick;
   private Vec3d lastTargetPos;
   private int maxHitsTakenInRow;
   private double mainHandAttackSpeed;
   private double lastTargetHealth;
   private long lastHitVictimTick;
   private int ownSwingCount;
   private Vec3d targetMotion;
   private long lastTargetSwingTick;
   private double smoothedHealthLoss;
   private int targetSwingCount;
   private int lastHitVictimId;
   private int hitsTakenCount;
   private boolean killFlagged;
   private int trackedEntityId = -1;
   private long lastTargetAttackEventTick;

   public long getTicksSinceHitLanded(long livingEntityValue) {
      return this.lastHitLandedTick == Long.MIN_VALUE ? 2147483647L : Math.max(0L, livingEntityValue - this.lastHitLandedTick);
   }

   public void updateTargetState(LivingEntity entity, double amount) {
      if (entity != null && entity.getId() == this.trackedEntityId) {
         Vec3d vec3d = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
         Vec3d vec3d2 = this.lastTargetPos == null ? Vec3d.ZERO : vec3d.subtract(this.lastTargetPos);
         this.targetMotion = !(vec3d2.lengthSquared() > 4.0) && Double.isFinite(vec3d2.lengthSquared()) ? vec3d2 : Vec3d.ZERO;
         this.lastTargetPos = vec3d;
         double zeroValue = Double.isFinite(this.lastTargetHealth) && Double.isFinite(amount) ? this.lastTargetHealth - amount : 0.0;
         this.smoothedHealthLoss = SwordCombatMath.lerpFinite(this.smoothedHealthLoss, zeroValue, 0.5);
         this.lastTargetHealth = amount;
         this.mainHandAttackSpeed = SwordAttributeAndHazardUtils.getAttackSpeed(entity.getMainHandStack());
      }
   }

   public int getMaxHitsTakenInRow() {
      return this.maxHitsTakenInRow;
   }

   public double getMainHandAttackSpeed() {
      return this.mainHandAttackSpeed;
   }

   public int getLastHitVictimId() {
      return this.lastHitVictimId;
   }

   public String getTargetName() {
      return this.targetName;
   }

   public boolean isRecentVictim(int livingEntityLimit, long livingEntityValue, long livingEntityLimit2) {
      return livingEntityLimit >= 0 && livingEntityLimit == this.lastHitVictimId && this.lastHitVictimTick != Long.MIN_VALUE && livingEntityValue - this.lastHitVictimTick <= livingEntityLimit2;
   }

   public int getOwnSwingCount() {
      return this.ownSwingCount;
   }

   public boolean detectKill(LivingEntity entity, long deadHealthValue) {
      if (this.killFlagged || entity == null || entity.getId() != this.trackedEntityId) {
         return false;
      } else if (!entity.isDead() && !(entity.getHealth() <= 0.0F)) {
         return false;
      } else if (this.lastHitTakenTick != Long.MIN_VALUE && deadHealthValue - this.lastHitTakenTick <= 80L) {
         this.killFlagged = true;
         this.killCount++;
         this.hitsTakenInRow = 0;
         return true;
      } else {
         return false;
      }
   }

   public void recordTargetSwingTime(int livingEntityValue, long time) {
      if (livingEntityValue == this.trackedEntityId && this.trackedEntityId >= 0) {
         this.lastTargetSwingTick = time;
      }
   }

   public long getTicksSinceTargetSwing(long livingEntityValue) {
      return this.lastTargetSwingTick == Long.MIN_VALUE ? 2147483647L : Math.max(0L, livingEntityValue - this.lastTargetSwingTick);
   }

   public void resetTracking() {
      this.setTarget(null);
      this.lastHitLandedTick = Long.MIN_VALUE;
      this.lastHitVictimTick = Long.MIN_VALUE;
      this.lastHitVictimId = -1;
   }

   public int getTrackedEntityId() {
      return this.trackedEntityId;
   }

   public TargetFightTracker() {
      this.lastTargetSwingTick = Long.MIN_VALUE;
      this.lastHitTakenTick = Long.MIN_VALUE;
      this.lastHitLandedTick = Long.MIN_VALUE;
      this.lastTargetAttackEventTick = Long.MIN_VALUE;
      this.lastHitVictimTick = Long.MIN_VALUE;
      this.lastHitVictimId = -1;
      this.targetMotion = Vec3d.ZERO;
      this.lastTargetHealth = Double.NaN;
      this.mainHandAttackSpeed = 4.0;
   }

   public int getHitsLandedCount() {
      return this.hitsLandedCount;
   }

   public int getHitsTakenCount() {
      return this.hitsTakenCount;
   }

   public int getTargetSwingCount() {
      return this.targetSwingCount;
   }

   public float getTargetChargeFraction(long livingEntityValue) {
      return SwordCombatMath.getChargeFraction(this.getTicksSinceTargetSwing(livingEntityValue), this.mainHandAttackSpeed);
   }

   public long getTicksSinceHitTaken(long livingEntityValue) {
      return this.lastHitTakenTick == Long.MIN_VALUE ? 2147483647L : Math.max(0L, livingEntityValue - this.lastHitTakenTick);
   }

   public void setTarget(LivingEntity entity) {
      int zeroValue = entity == null ? -1 : entity.getId();
      if (zeroValue != this.trackedEntityId) {
         this.trackedEntityId = zeroValue;
         this.targetName = entity == null ? null : entity.getName().getString();
         this.lastTargetSwingTick = Long.MIN_VALUE;
         this.lastHitTakenTick = Long.MIN_VALUE;
         this.lastTargetAttackEventTick = Long.MIN_VALUE;
         this.lastTargetPos = null;
         this.targetMotion = Vec3d.ZERO;
         this.lastTargetHealth = Double.NaN;
         this.smoothedHealthLoss = 0.0;
         this.mainHandAttackSpeed = 4.0;
         this.hitsTakenInRow = 0;
         this.killFlagged = false;
      }
   }

   public int getKillCount() {
      return this.killCount;
   }

   public void recordDamageEvent(int livingEntityValue, int value, int livingEntityValue2, long time) {
      if (livingEntityValue == livingEntityValue2 && value >= 0 && value != livingEntityValue2) {
         this.lastHitVictimId = value;
         this.lastHitVictimTick = time;
      }

      if (this.trackedEntityId >= 0) {
         if (livingEntityValue == this.trackedEntityId) {
            this.lastTargetAttackEventTick = time;
            if (value == livingEntityValue2) {
               this.hitsTakenCount++;
               this.hitsTakenInRow++;
               this.maxHitsTakenInRow = Math.max(this.maxHitsTakenInRow, this.hitsTakenInRow);
               this.lastHitTakenTick = time;
            }
         } else if (livingEntityValue == livingEntityValue2 && value == this.trackedEntityId) {
            this.hitsLandedCount++;
            this.hitsTakenInRow = 0;
            this.lastHitLandedTick = time;
         }
      }
   }

   public double getSmoothedHealthLoss() {
      return this.smoothedHealthLoss;
   }

   public long getTicksSinceTargetAttackEvent(long livingEntityValue) {
      return this.lastTargetAttackEventTick == Long.MIN_VALUE ? 2147483647L : Math.max(0L, livingEntityValue - this.lastTargetAttackEventTick);
   }

   public void countSwing(int livingEntityValue, int livingEntityValue2) {
      if (livingEntityValue == this.trackedEntityId && this.trackedEntityId >= 0) {
         this.targetSwingCount++;
      } else if (livingEntityValue == livingEntityValue2) {
         this.ownSwingCount++;
      }
   }

   public int getHitsTakenInRow() {
      return this.hitsTakenInRow;
   }

   public Vec3d getTargetMotion() {
      return this.targetMotion;
   }
}
