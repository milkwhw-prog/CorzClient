package com.corz.client.utils;

public class PlayerScaffold {
   private final double scaffoldStrength;
   private final double playerScaffoldAmount;
   private boolean playerScaffoldActive;

   public boolean isFlag() {
      return this.playerScaffoldActive;
   }

   public void updateFlag() {
      this.playerScaffoldActive = true;
   }

   public double getPlayerScaffoldAmount() {
      return this.playerScaffoldAmount;
   }

   public PlayerScaffold(double amount, double amount2) {
      this.scaffoldStrength = amount;
      this.playerScaffoldAmount = amount2;
   }

   public double getAmount() {
      return this.scaffoldStrength;
   }
}
