package com.corz.client.utils;

import java.util.Arrays;

public final class LoftLatheCopy {
   private int entryCount;
   private float[] copyOf = new float[2304];

   public float[] getCopyOf() {
      return Arrays.copyOf(this.copyOf, this.entryCount * 9);
   }

   public static int getValue(float[] loftLatheValues, int loftLatheValue) {
      return Float.floatToRawIntBits(loftLatheValues[loftLatheValue + 8]);
   }

   public int getValue() {
      return this.entryCount;
   }

   public void handle(float loftLatheValue, float loftLatheValue2, float loftLatheValue3, float flagLoftRegion, float loftLatheValue5, float loftLatheFactor, float loftLatheFactor2, float loftLatheFactor3, int tintColor) {
      int index = this.entryCount * 9;
      if (index + 9 > this.copyOf.length) {
         this.copyOf = Arrays.copyOf(this.copyOf, this.copyOf.length * 2);
      }

      float distance = (float)Math.sqrt(loftLatheFactor * loftLatheFactor + loftLatheFactor2 * loftLatheFactor2 + loftLatheFactor3 * loftLatheFactor3);
      if (distance > 1.0E-8F) {
         loftLatheFactor /= distance;
         loftLatheFactor2 /= distance;
         loftLatheFactor3 /= distance;
      } else {
         loftLatheFactor = 0.0F;
         loftLatheFactor2 = 1.0F;
         loftLatheFactor3 = 0.0F;
      }

      this.copyOf[index] = loftLatheValue;
      this.copyOf[index + 1] = loftLatheValue2;
      this.copyOf[index + 2] = loftLatheValue3;
      this.copyOf[index + 3] = flagLoftRegion;
      this.copyOf[index + 4] = loftLatheValue5;
      this.copyOf[index + 5] = loftLatheFactor;
      this.copyOf[index + 6] = loftLatheFactor2;
      this.copyOf[index + 7] = loftLatheFactor3;
      this.copyOf[index + 8] = Float.intBitsToFloat(tintColor);
      this.entryCount++;
   }
}
