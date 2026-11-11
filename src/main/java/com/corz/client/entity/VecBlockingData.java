package com.corz.client.entity;

import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.PosBlockUtilsBonus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BowGappleType;

public record VecBlockingData(
   int id,
   String name,
   LivingEntity entity,
   Vec3d pos,
   Vec3d serverPos,
   Vec3d velocity,
   Vec3d eye,
   boolean onGround,
   double distance,
   boolean visible,
   float health,
   float absorption,
   boolean healthTrusted,
   float modelEhp,
   PosBlockUtilsBonus.Armor armor,
   float meleeEpf,
   float armorMinDurability,
   float calibration,
   boolean totemInHand,
   BowGappleType main,
   BowGappleType off,
   boolean eating,
   boolean blocking,
   int ticksSinceFresh,
   float lastScaledTaken,
   boolean lastFreshOurs,
   int pops,
   long lastPopTick,
   boolean awaitingTotem,
   BlockStateUtilsDirections.Hole hole,
   double strafeRate,
   double sigma,
   Vec3d lateral,
   boolean immune
) {
   public boolean isHoleSides() {
      return this.hole != null && this.hole.type() != BlockStateUtilsDirections.Type.NONE && this.hole.openSides() == 0;
   }

   public float getHealthTrusted() {
      return this.healthTrusted ? this.health + this.absorption : this.modelEhp;
   }
}
