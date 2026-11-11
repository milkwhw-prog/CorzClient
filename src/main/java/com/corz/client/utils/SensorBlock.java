package com.corz.client.utils;

public final class SensorBlock {
   public final int sensorBlockZ;
   public final int sensorBlockX;
   public final int typeId;
   public final int sensorBlockY;

   public boolean intersectsBoxWithRanges(int sensorBlockValue, int sensorBlockValue2, int sensorBlockValue3, int sensorBlockValue4, int sensorBlockValue5, int sensorBlockValue6) {
      int typeRange = this.getTypeRange();
      int zRange = this.getZRange();
      if (this.sensorBlockX < sensorBlockValue - typeRange || this.sensorBlockX > sensorBlockValue4 + typeRange) {
         return false;
      } else {
         return this.sensorBlockY >= sensorBlockValue3 - typeRange && this.sensorBlockY <= sensorBlockValue6 + typeRange ? this.sensorBlockZ >= sensorBlockValue2 - zRange && this.sensorBlockZ <= sensorBlockValue5 + zRange : false;
      }
   }

   public int getZRange() {
      return this.typeId == 34 ? 32 : 24;
   }

   public SensorBlock(int value, int value2, int value3, int value4) {
      this.typeId = value;
      this.sensorBlockX = value2;
      this.sensorBlockZ = value3;
      this.sensorBlockY = value4;
   }

   public int getEffectiveRange() {
      return isLongRangeType(this.typeId) ? this.getTypeRange() : 8;
   }

   public static boolean isLongRangeType(int sensorBlockValue) {
      switch (sensorBlockValue) {
         case 113:
         case 124:
         case 127:
         case 132:
            return true;
         default:
            return false;
      }
   }

   public boolean intersectsBoxWithSingleRange(int sensorBlockValue, int sensorBlockValue2, int sensorBlockValue3, int sensorBlockValue4, int sensorBlockValue5, int sensorBlockValue6) {
      int effectiveRange = this.getEffectiveRange();
      return this.sensorBlockX >= sensorBlockValue - effectiveRange
         && this.sensorBlockX <= sensorBlockValue4 + effectiveRange
         && this.sensorBlockY >= sensorBlockValue3 - effectiveRange
         && this.sensorBlockY <= sensorBlockValue6 + effectiveRange
         && this.sensorBlockZ >= sensorBlockValue2 - effectiveRange
         && this.sensorBlockZ <= sensorBlockValue5 + effectiveRange;
   }

   public int getTypeRange() {
      switch (this.typeId) {
         case 34:
            return 80;
         case 113:
         case 124:
         case 127:
            return 12;
         case 116:
         case 117:
            return 64;
         case 119:
         case 128:
         case 136:
            return 48;
         case 120:
            return 40;
         case 123:
            return 48;
         case 132:
            return 8;
         case 133:
         case 135:
            return 24;
         case 134:
            return 48;
         default:
            return 16;
      }
   }
}
