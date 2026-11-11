package com.corz.client.utils;

public final class Drill {
   public double reachDistance;
   public int passWidth;
   public int layerHeight = 3;
   public boolean scanLeftovers;
   public int minToolDurability;
   public int hungerDropToEat;
   public boolean sprintToTarget;
   public boolean placeBlocks;
   public String drillToolName;
   public boolean replaceDamagedTool;
   public boolean drillActive;
   public boolean autoEatEnabled;

   public Drill() {
      this.passWidth = 3;
      this.sprintToTarget = true;
      this.placeBlocks = true;
      this.scanLeftovers = true;
      this.reachDistance = 4.5;
      this.autoEatEnabled = true;
      this.hungerDropToEat = 2;
      this.replaceDamagedTool = true;
      this.minToolDurability = 50;
      this.drillToolName = "Drill";
   }
}
