package com.corz.client.utils;
import com.corz.client.utils.SpawnerSculkUtils;

public final class CityMonument {
   public boolean trailRuins;
   public boolean monument;
   public boolean bastion;
   public boolean cityActive;
   public boolean penNatFlag;
   public boolean jungleTemple;
   public boolean cityAncientFlag;
   public boolean village;
   private final long[] structureBits;
   public boolean mansion;
   public boolean igloo;
   public boolean outpost;
   public boolean endCity;
   public boolean shipwreck;
   public boolean swampHut;
   public boolean oceanRuin;
   public boolean ancientCity;
   public boolean stronghold;
   public boolean geode;
   public boolean desertPyramid;
   public boolean penDivActive;
   public int monumentScore = 0;
   public boolean craftedExcavationActive = false;
   public boolean mineshaft;
   public boolean ruinedPortal;
   public boolean fortress;

   public boolean hasNoStructureBits() {
      return !SpawnerSculkUtils.isBitSetEmpty(this.structureBits);
   }

   public boolean hasStructureBit(int cityMonumentValue) {
      return SpawnerSculkUtils.isBitSet(this.structureBits, cityMonumentValue);
   }

   public String getStructureName() {
      if (this.cityAncientFlag) {
         return "ancient_city";
      } else if (this.ancientCity) {
         return "stronghold";
      } else if (this.stronghold) {
         return "monument";
      } else if (this.monument) {
         return "fortress";
      } else if (this.fortress) {
         return "bastion";
      } else if (this.bastion) {
         return "end_city";
      } else if (this.endCity) {
         return "mansion";
      } else if (this.mansion) {
         return "village";
      } else if (this.village) {
         return "outpost";
      } else if (this.outpost) {
         return "jungle_temple";
      } else if (this.jungleTemple) {
         return "desert_pyramid";
      } else if (this.desertPyramid) {
         return "igloo";
      } else if (this.igloo) {
         return "swamp_hut";
      } else if (this.swampHut) {
         return "trail_ruins";
      } else if (this.trailRuins) {
         return "ocean_ruin";
      } else if (this.oceanRuin) {
         return "shipwreck";
      } else if (this.shipwreck) {
         return "ruined_portal";
      } else if (this.ruinedPortal) {
         return "mineshaft";
      } else if (this.mineshaft) {
         return "geode";
      } else {
         return this.geode ? "dungeon" : null;
      }
   }

   public void copyStructureBits(long[] cityMonumentValues) {
      SpawnerSculkUtils.mergeBits(this.structureBits, cityMonumentValues);
   }

   public static int sumWhenBothLimitsReachEight(int cityMonumentLimit, int cityMonumentLimit2, int cityMonumentValue, int cityMonumentValue2) {
      boolean cityMonumentFlag = cityMonumentLimit >= 8 && cityMonumentLimit2 >= 8;
      return cityMonumentFlag ? cityMonumentLimit + cityMonumentLimit2 + cityMonumentValue + cityMonumentValue2 : 0;
   }

   public boolean hasAnyStructure() {
      return this.cityAncientFlag
         || this.ruinedPortal
         || this.ancientCity
         || this.stronghold
         || this.monument
         || this.fortress
         || this.bastion
         || this.shipwreck
         || this.geode
         || this.mansion
         || this.jungleTemple
         || this.outpost
         || this.igloo
         || this.desertPyramid
         || this.endCity
         || this.trailRuins
         || this.oceanRuin
         || this.village
         || this.swampHut;
   }

   public CityMonument() {
      this.cityAncientFlag = false;
      this.ruinedPortal = false;
      this.ancientCity = false;
      this.stronghold = false;
      this.monument = false;
      this.fortress = false;
      this.bastion = false;
      this.shipwreck = false;
      this.geode = false;
      this.mansion = false;
      this.jungleTemple = false;
      this.outpost = false;
      this.igloo = false;
      this.desertPyramid = false;
      this.endCity = false;
      this.trailRuins = false;
      this.oceanRuin = false;
      this.village = false;
      this.swampHut = false;
      this.mineshaft = false;
      this.penNatFlag = false;
      this.cityActive = false;
      this.penDivActive = false;
      this.structureBits = new long[3];
   }
}
