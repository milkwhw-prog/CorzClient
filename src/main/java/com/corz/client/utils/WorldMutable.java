package com.corz.client.utils;

import java.util.Arrays;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.LightType;
import com.corz.client.utils.PetalsAmounts;

final class WorldMutable implements PetalsAmounts.Ground {
   private final float[] topTable;
   private final Mutable mutable;
   private ClientWorld world;
   private int writeIndex;
   private final byte[] mutableLightTable;
   private final long[] worldTable = new long[4096];

   private static long getTime(int worldMutableValue, int worldMutableValue2) {
      return (long)worldMutableValue << 32 | worldMutableValue2 & 4294967295L;
   }

   @Override
   public float top(int topValue, int topValue2) {
      int index = getValue(topValue, topValue2);
      if (this.worldTable[index] != getTime(topValue, topValue2)) {
         if (this.world == null) {
            return Float.NEGATIVE_INFINITY;
         }

         this.handleMutableLight(index, topValue, topValue2);
      }

      return this.topTable[index];
   }

   private static int getValue(int worldMutableValue, int worldMutableValue2) {
      return (worldMutableValue & 63) << 6 | worldMutableValue2 & 63;
   }

   int getTimesAmounts(int worldMutableValue, int worldMutableValue2, double worldMutableValue3) {
      int index = getValue(worldMutableValue, worldMutableValue2);
      if (this.worldTable[index] != getTime(worldMutableValue, worldMutableValue2)) {
         return 0;
      } else {
         byte worldMutableValue4 = this.mutableLightTable[index];
         if (worldMutableValue4 <= 0) {
            return 0;
         } else {
            double worldMutableLimit = worldMutableValue3 - this.topTable[index];
            int worldMutableValue5 = worldMutableLimit > 0.0 ? worldMutableValue4 - (int)worldMutableLimit : worldMutableValue4;
            return worldMutableValue5 < 0 ? 0 : worldMutableValue5;
         }
      }
   }

   private void handleMutableLight(int index, int mutableLightValue, int mutableLightValue2) {
      this.worldTable[index] = getTime(mutableLightValue, mutableLightValue2);
      int topY = this.world.getTopY(Type.MOTION_BLOCKING, mutableLightValue, mutableLightValue2);
      this.topTable[index] = topY;
      this.mutable.set(mutableLightValue, topY, mutableLightValue2);
      this.mutableLightTable[index] = (byte)this.world.getLightLevel(LightType.BLOCK, this.mutable);
   }

   void updateTimesWorld() {
      this.world = null;
      Arrays.fill(this.worldTable, Long.MIN_VALUE);
   }

   WorldMutable() {
      this.topTable = new float[4096];
      this.mutableLightTable = new byte[4096];
      this.mutable = new Mutable();
      Arrays.fill(this.worldTable, Long.MIN_VALUE);
   }

   void handleTimesWorld(ClientWorld world) {
      if (world != this.world) {
         this.world = world;
         Arrays.fill(this.worldTable, Long.MIN_VALUE);
      }

      if (this.world != null) {
         for (int i = 0; i < 160; i++) {
            int index = this.writeIndex;
            this.writeIndex = this.writeIndex + 1 & 4095;
            long worldValue = this.worldTable[index];
            if (worldValue != Long.MIN_VALUE) {
               this.handleMutableLight(index, (int)(worldValue >> 32), (int)worldValue);
            }
         }
      }
   }
}
