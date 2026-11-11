package com.corz.client.utils;

import java.util.Arrays;

public final class FluidStateUtilsRect {

   private FluidStateUtilsRect() {
   }

   public static int getRect(boolean[] rectFlags, int[] rectValues, int[] rectValues2, int[] rectValues3, boolean[] booleans, FluidStateUtilsRect.Sink sink) {
      Arrays.fill(booleans, 0, 256, false);
      int rect = 0;

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            int index = i * 16 + j;
            if (rectFlags[index] && !booleans[index]) {
               int rectCounter = 1;

               while (j + rectCounter < 16 && canFlag(rectFlags, rectValues, rectValues2, rectValues3, booleans, index, index + rectCounter)) {
                  rectCounter++;
               }

               int rectCounter2;
               label61:
               for (rectCounter2 = 1; i + rectCounter2 < 16; rectCounter2++) {
                  int rectValue = index + rectCounter2 * 16;

                  for (int k = 0; k < rectCounter; k++) {
                     if (!canFlag(rectFlags, rectValues, rectValues2, rectValues3, booleans, index, rectValue + k)) {
                        break label61;
                     }
                  }
               }

               for (int m = 0; m < rectCounter2; m++) {
                  int rectValue2 = index + m * 16;

                  for (int p = 0; p < rectCounter; p++) {
                     booleans[rectValue2 + p] = true;
                  }
               }

               sink.rect(j, i, rectCounter, rectCounter2, index);
               rect++;
            }
         }
      }

      return rect;
   }

   private static boolean canFlag(boolean[] fluidRectFlags, int[] fluidRectValues, int[] fluidRectValues2, int[] fluidRectValues3, boolean[] booleans, int index, int index2) {
      return fluidRectFlags[index2] && !booleans[index2] && fluidRectValues[index2] == fluidRectValues[index] && fluidRectValues2[index2] == fluidRectValues2[index] && fluidRectValues3[index2] == fluidRectValues3[index];
   }

   @FunctionalInterface
   public interface Sink {
      void rect(int rectValue, int i, int rectCounter, int rectValue4, int rectValue5);
   }
}
