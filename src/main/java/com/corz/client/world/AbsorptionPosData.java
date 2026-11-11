package com.corz.client.world;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public record AbsorptionPosData(
   Vec3d pos,
   Vec3d eye,
   Box box,
   Vec3d velocity,
   boolean onGround,
   float health,
   float absorption,
   PosBlockUtilsBonus.Armor armor,
   float armorMinDurability,
   boolean totemInOffhand,
   boolean totemConfirmed,
   int totems,
   int crystals,
   int obsidian,
   int anchors,
   int glowstone,
   int gapples,
   int food,
   int pearls,
   int xpBottles,
   boolean pearlReady,
   float swordCharge,
   int foodLevel,
   int ticksSinceFresh,
   float lastScaledTaken,
   int pops,
   long lastPopTick,
   boolean usingItem,
   boolean handSwapBusy,
   boolean inputMasked,
   BlockStateUtilsDirections.Hole hole,
   int pingMs,
   double loopMs
) {
   public float getAbsorptionHealth() {
      return this.health + this.absorption;
   }
}
