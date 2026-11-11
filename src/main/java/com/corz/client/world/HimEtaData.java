package com.corz.client.world;

import net.minecraft.util.math.BlockPos;

public record HimEtaData(
   BlockPos cell, int charges, float rawUs, float fullUs, float hitUs, float dmgHim, int eta, float pDet, int ownerId, boolean shieldedFromHim, boolean busy
) {
   public static final HimEtaData INSTANCE = new HimEtaData(null, 0, 0.0F, 0.0F, 0.0F, 0.0F, 99, 0.0F, -1, false, false);

   public boolean canChargesEta(float ehp) {
      return this.isCell() && this.eta <= 1 && this.charges >= 1 && this.hitUs >= 0.5F * ehp;
   }

   public boolean isCell() {
      return this.cell != null;
   }

   public boolean canEtaHit(float ehp) {
      return this.isCell() && this.eta <= 2 && this.hitUs >= Math.max(3.0F, 0.3F * ehp);
   }

   public float getDetHit() {
      return this.pDet * this.hitUs;
   }
}
