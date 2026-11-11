package com.corz.client.utils;

public class SwordBot {
   private final int swordSlot;
   private boolean swordBotActive;
   private final int keyCode;

   public SwordBot(int key2, int value) {
      this.keyCode = key2;
      this.swordSlot = value;
   }

   public boolean isFlag() {
      return this.swordBotActive;
   }

   public int getKey() {
      return this.keyCode;
   }

   public void updateFlag() {
      this.swordBotActive = true;
   }

   public int getValue() {
      return this.swordSlot;
   }
}
