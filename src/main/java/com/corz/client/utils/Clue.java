package com.corz.client.utils;
import com.corz.client.utils.Sense;

public enum Clue {
   BLOCKS(Sense.STRUCTURE, 0, 100, 0L, "Base", true),
   TRADING_HALL(Sense.LIFE, 50, 55, 3600000L, "Trading hall", true),
   IRON_FARM(Sense.LIFE, 5, 5, 3600000L, "Iron farm", false),
   TRADED_VILLAGER(Sense.LIFE, 2, 6, 3600000L, "Traded villagers", false),
   PET(Sense.LIFE, 3, 6, 3600000L, "Pets", false),
   NAMED_MOB(Sense.LIFE, 2, 4, 3600000L, "Named mobs", false),
   LEASHED(Sense.LIFE, 1, 3, 3600000L, "Leashed mobs", false),
   ITEM_FRAME(Sense.LIFE, 2, 6, 3600000L, "Item frames", false),
   ARMOR_STAND(Sense.LIFE, 2, 4, 3600000L, "Armor stands", false),
   STORAGE_CART(Sense.LIFE, 2, 4, 3600000L, "Storage carts", false),
   ANIMAL_PEN(Sense.LIFE, 3, 3, 3600000L, "Animal pens", false),
   CHEST_USE(Sense.ACTIVITY, 4, 8, 900000L, "Chests opened", false),
   MACHINE(Sense.ACTIVITY, 1, 4, 900000L, "Machines running", false),
   WORKSTATION(Sense.ACTIVITY, 2, 4, 900000L, "Crafting", false),
   MINING(Sense.ACTIVITY, 1, 2, 900000L, "Mining", false),
   BUILDING(Sense.ACTIVITY, 1, 2, 900000L, "Building", false),
   SPAWNER(Sense.ACTIVITY, 3, 6, 900000L, "Active spawner", false),
   DOORS(Sense.ACTIVITY, 1, 3, 900000L, "Doors and levers", false),
   LOGOUT(Sense.PRESENCE, 6, 10, 7200000L, "Logged out here", false),
   LOGIN(Sense.PRESENCE, 4, 8, 7200000L, "Logged in here", false),
   HIDDEN_PLAYER(Sense.PRESENCE, 2, 4, 600000L, "Hidden player", false),
   REMEMBERED(Sense.STRUCTURE, 0, 100, 0L, "Found before", true);
   public final String clueJoinText;
   public final boolean clueHostActive;
   public final long cooldownMs;
   public final Sense sense;
   public final int clueWeight;
   public final int clueJoinValue;

   public boolean isTime() {
      return this.cooldownMs > 0L;
   }

   private Clue(Sense sense, int value, int value2, long time, String text, boolean flag) {
      this.sense = sense;
      this.clueWeight = value;
      this.clueJoinValue = value2;
      this.cooldownMs = time;
      this.clueJoinText = text;
      this.clueHostActive = flag;
   }
}
