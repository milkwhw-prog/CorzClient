package com.corz.client.utils;
import com.corz.client.utils.SpawnerTimeUtils;

public final class Spawner {
   public final long spawnerPositionKey;
   private String spawnerText;
   public final int spawnerRememberedValue;
   public final int rememberedSpawnerValue;
   final long spawnerValue;
   public final int spawnerRememberedValueSpawne;
   public final long regionKey;
   private Spawner.State rememberedLoadedType;

   Spawner(int spawnerRememberedValue, int y, int rememberedSpawnerValue, String spawnerText, Spawner.State rememberedLoadedType, long time) {
      this.spawnerRememberedValueSpawne = spawnerRememberedValue;
      this.spawnerRememberedValue = y;
      this.rememberedSpawnerValue = rememberedSpawnerValue;
      this.spawnerPositionKey = SpawnerTimeUtils.getTime(spawnerRememberedValue, y, rememberedSpawnerValue);
      this.regionKey = SpawnerTimeUtils.getTime(spawnerRememberedValue, rememberedSpawnerValue);
      this.spawnerText = spawnerText;
      this.rememberedLoadedType = rememberedLoadedType;
      this.spawnerValue = time;
   }

   void setRememberedLoadedType(Spawner.State rememberedLoadedType) {
      this.rememberedLoadedType = rememberedLoadedType;
   }

   public boolean isSpawnerText() {
      return this.spawnerText != null;
   }

   public String getSpawnerText() {
      return this.spawnerText;
   }

   @Override
   public String toString() {
      return "Spawner["
         + this.spawnerRememberedValueSpawne
         + " "
         + this.spawnerRememberedValue
         + " "
         + this.rememberedSpawnerValue
         + " "
         + (this.spawnerText == null ? "?" : this.spawnerText)
         + " "
         + this.rememberedLoadedType
         + "]";
   }

   public boolean isLoadedRemembered() {
      return this.rememberedLoadedType == Spawner.State.LOADED;
   }

   public Spawner.State getRememberedLoadedType() {
      return this.rememberedLoadedType;
   }

   void setSpawnerText(String spawnerText) {
      this.spawnerText = spawnerText;
   }

   public static enum State {
      LOADED,
      REMEMBERED;
   }
}
