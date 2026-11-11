package com.corz.client.utils;

import net.minecraft.util.math.Vec3d;

public record PreferAnchorData(Vec3d anchor, PreferAnchorData.Kind kind, Vec3d prefer) {
   public Vec3d getPreferAnchor() {
      return this.prefer != null ? this.prefer : this.anchor;
   }

   public PreferAnchorData(Vec3d anchor, PreferAnchorData.Kind kind, Vec3d prefer) {
      if (kind == null) {
         kind = PreferAnchorData.Kind.CELL;
      }

      this.anchor = anchor;
      this.kind = kind;
      this.prefer = prefer;
   }

   public static PreferAnchorData getLowMelee(Vec3d prefer, Vec3d targetFeet) {
      return new PreferAnchorData(prefer, PreferAnchorData.Kind.MELEE_LOW, targetFeet);
   }

   public static PreferAnchorData getMelee(Vec3d prefer, Vec3d targetFeet) {
      return new PreferAnchorData(prefer, PreferAnchorData.Kind.MELEE, targetFeet);
   }

   public static PreferAnchorData getCell(Vec3d at) {
      return new PreferAnchorData(at, PreferAnchorData.Kind.CELL, at);
   }

   public static PreferAnchorData getCrystal(Vec3d prefer, Vec3d crystalCentre) {
      return new PreferAnchorData(prefer, PreferAnchorData.Kind.CRYSTAL, crystalCentre);
   }

   public static enum Kind {
      MELEE,
      CRYSTAL,
      CELL,
      MELEE_LOW;
   }
}
