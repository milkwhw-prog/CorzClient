package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import com.corz.client.utils.KnifeInspectClip;

final class KnifeClipButterfly {

   private KnifeClipButterfly() {
   }

   static void handleAmount(KnifeInspectClip knifeInspectClip, float knifeClipBound, float wrapDegreesBound, int intersectsCenterBound) {
      float knifeClipValue = Math.min(knifeClipBound, 50.0F);
      float min2 = Math.min(wrapDegreesBound, 36.0F);
      int knifeClipLimit = Math.max(1, Math.min(intersectsCenterBound, 3));
      KnifeClipButterfly.Keys keys = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys2 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys3 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys4 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys5 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys6 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys7 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys8 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys9 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys10 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys11 = new KnifeClipButterfly.Keys();
      KnifeClipButterfly.Keys keys12 = new KnifeClipButterfly.Keys();
      keys8.getKeys(0.0F, 0.0F).getKeys(60.0F, 0.005F).getKeys(300.0F, -0.02F);
      keys9.getKeys(0.0F, 0.0F).getKeys(60.0F, -0.008F).getKeys(260.0F, 0.024999999F).getKeys(320.0F, 0.02F);
      keys10.getKeys(0.0F, 0.0F).getKeys(300.0F, -0.015F);
      keys11.getKeys(0.0F, 0.0F).getKeys(300.0F, -5.0F);
      keys7.getKeys(0.0F, 0.0F).getKeys(80.0F, knifeClipValue * 0.25F).getKeys(300.0F, -4.0F);
      keys6.getKeys(0.0F, 0.0F).getKeys(80.0F, knifeClipValue * 0.15F).getKeys(260.0F, 0.0F);
      keys3.getKeys(0.0F, 0.0F);
      keys.getKeys(0.0F, 0.0F);
      keys2.getKeys(0.0F, 0.0F);
      keys4.getKeys(0.0F, 0.0F);
      keys12.getKeys(0.0F, 0.0F);
      keys5.getKeys(0.0F, 0.0F);
      float knifeClipValue3 = 300.0F;
      keys3.getKeys(knifeClipValue3 + 40.0F, 0.0F)
         .getKeys(knifeClipValue3 + 90.0F, 4.0F)
         .getKeys(knifeClipValue3 + 210.0F, -8.0F)
         .getKeys(knifeClipValue3 + 300.0F, -4.4F)
         .getKeys(knifeClipValue3 + 380.0F, -2.0F)
         .getKeys(knifeClipValue3 + 470.0F, 3.0F)
         .getKeys(knifeClipValue3 + 520.0F, 0.0F);
      keys2.getKeys(knifeClipValue3 + 90.0F, 0.0F).getKeys(knifeClipValue3 + 215.0F, 95.0F).getKeys(knifeClipValue3 + 290.0F, 172.0F).getKeys(knifeClipValue3 + 300.0F, 180.0F);
      keys.getKeys(knifeClipValue3 + 90.0F, 0.0F)
         .getKeys(knifeClipValue3 + 200.0F, 18.0F)
         .getKeys(knifeClipValue3 + 290.0F, 24.0F)
         .getKeys(knifeClipValue3 + 350.0F, 55.0F)
         .getKeys(knifeClipValue3 + 430.0F, 168.0F)
         .getKeys(knifeClipValue3 + 440.0F, 180.0F)
         .getKeys(knifeClipValue3 + 470.0F, 180.0F - min2 * 0.4F)
         .getKeys(knifeClipValue3 + 510.0F, 180.0F);
      keys7.getKeys(knifeClipValue3 + 40.0F, -4.0F)
         .getKeys(knifeClipValue3 + 150.0F, -9.0F)
         .getKeys(knifeClipValue3 + 300.0F, 1.0F)
         .getKeys(knifeClipValue3 + 440.0F, -8.0F)
         .getKeys(knifeClipValue3 + 520.0F, -4.0F);
      keys9.getKeys(knifeClipValue3 + 60.0F, 0.02F)
         .getKeys(knifeClipValue3 + 200.0F, 0.031999998F)
         .getKeys(knifeClipValue3 + 330.0F, 0.02F)
         .getKeys(knifeClipValue3 + 440.0F, 0.01F)
         .getKeys(knifeClipValue3 + 520.0F, 0.02F);
      keys5.getKeys(knifeClipValue3 + 60.0F, 0.0F).getKeys(knifeClipValue3 + 200.0F, -3.0F).getKeys(knifeClipValue3 + 330.0F, 2.0F).getKeys(knifeClipValue3 + 460.0F, 0.0F);
      keys6.getKeys(knifeClipValue3 + 440.0F, 0.0F).getKeys(knifeClipValue3 + 470.0F, min2 * 0.25F).getKeys(knifeClipValue3 + 530.0F, 0.0F);
      float knifeClipValue4 = knifeClipValue3 + 520.0F;
      keys.getKeys(knifeClipValue4 + 40.0F, 180.0F).getKeys(knifeClipValue4 + 130.0F, 95.0F).getKeys(knifeClipValue4 + 195.0F, 12.0F).getKeys(knifeClipValue4 + 205.0F, 0.0F);
      keys2.getKeys(knifeClipValue4 + 215.0F, 180.0F)
         .getKeys(knifeClipValue4 + 290.0F, 95.0F)
         .getKeys(knifeClipValue4 + 355.0F, 10.0F)
         .getKeys(knifeClipValue4 + 365.0F, 0.0F)
         .getKeys(knifeClipValue4 + 395.0F, min2 * 0.5F)
         .getKeys(knifeClipValue4 + 440.0F, 0.0F);
      keys3.getKeys(knifeClipValue4 + 80.0F, 4.0F)
         .getKeys(knifeClipValue4 + 200.0F, -4.0F)
         .getKeys(knifeClipValue4 + 290.0F, -8.0F)
         .getKeys(knifeClipValue4 + 370.0F, -4.0F)
         .getKeys(knifeClipValue4 + 440.0F, 0.0F);
      keys7.getKeys(knifeClipValue4 + 120.0F, 2.0F).getKeys(knifeClipValue4 + 250.0F, -2.0F).getKeys(knifeClipValue4 + 370.0F, -8.0F).getKeys(knifeClipValue4 + 460.0F, -4.0F);
      keys9.getKeys(knifeClipValue4 + 40.0F, 0.02F)
         .getKeys(knifeClipValue4 + 120.0F, 0.03F)
         .getKeys(knifeClipValue4 + 250.0F, 0.02F)
         .getKeys(knifeClipValue4 + 365.0F, 0.01F)
         .getKeys(knifeClipValue4 + 440.0F, 0.02F);
      keys5.getKeys(knifeClipValue4 + 40.0F, 0.0F).getKeys(knifeClipValue4 + 150.0F, 2.0F).getKeys(knifeClipValue4 + 300.0F, -3.0F).getKeys(knifeClipValue4 + 440.0F, 0.0F);
      float knifeClipValue5 = knifeClipValue4 + 460.0F;

      for (int i = 0; i < knifeClipLimit; i++) {
         float knifeClipValue6 = knifeClipValue5 + i * 450.0F;
         keys.getKeys(knifeClipValue6 + 25.0F, 0.0F)
            .getKeys(knifeClipValue6 + 120.0F, 170.0F)
            .getKeys(knifeClipValue6 + 130.0F, 180.0F)
            .getKeys(knifeClipValue6 + 340.0F, 180.0F)
            .getKeys(knifeClipValue6 + 435.0F, 10.0F)
            .getKeys(knifeClipValue6 + 445.0F, 0.0F);
         keys2.getKeys(knifeClipValue6 + 140.0F, 0.0F)
            .getKeys(knifeClipValue6 + 225.0F, 170.0F)
            .getKeys(knifeClipValue6 + 235.0F, 180.0F)
            .getKeys(knifeClipValue6 + 245.0F, 180.0F)
            .getKeys(knifeClipValue6 + 325.0F, 10.0F)
            .getKeys(knifeClipValue6 + 335.0F, 0.0F);
         keys7.getKeys(knifeClipValue6 + 10.0F, -4.0F - knifeClipValue * 0.15F)
            .getKeys(knifeClipValue6 + 60.0F, 4.0F)
            .getKeys(knifeClipValue6 + 115.0F, 2.0F)
            .getKeys(knifeClipValue6 + 160.0F, -6.0F)
            .getKeys(knifeClipValue6 + 255.0F, -9.0F)
            .getKeys(knifeClipValue6 + 305.0F, -2.0F)
            .getKeys(knifeClipValue6 + 360.0F, 4.0F)
            .getKeys(knifeClipValue6 + 445.0F, -4.0F);
         keys8.getKeys(knifeClipValue6 + 10.0F, -0.02F)
            .getKeys(knifeClipValue6 + 75.0F, -0.03F)
            .getKeys(knifeClipValue6 + 230.0F, -0.02F)
            .getKeys(knifeClipValue6 + 300.0F, -0.01F)
            .getKeys(knifeClipValue6 + 440.0F, -0.02F);
         keys5.getKeys(knifeClipValue6 + 15.0F, knifeClipValue * 0.05F).getKeys(knifeClipValue6 + 120.0F, -2.5F).getKeys(knifeClipValue6 + 345.0F, 2.5F).getKeys(knifeClipValue6 + 445.0F, 0.0F);
         keys9.getKeys(knifeClipValue6 + 220.0F, 0.02F)
            .getKeys(knifeClipValue6 + 250.0F, 0.0139999995F)
            .getKeys(knifeClipValue6 + 295.0F, 0.02F)
            .getKeys(knifeClipValue6 + 435.0F, 0.02F)
            .getKeys(knifeClipValue6 + 465.0F, 0.0139999995F)
            .getKeys(knifeClipValue6 + 515.0F, 0.02F);
         keys3.getKeys(knifeClipValue6 + 228.0F, 0.0F)
            .getKeys(knifeClipValue6 + 255.0F, min2 * 0.25F)
            .getKeys(knifeClipValue6 + 300.0F, 0.0F)
            .getKeys(knifeClipValue6 + 435.0F, 0.0F)
            .getKeys(knifeClipValue6 + 465.0F, -min2 * 0.25F)
            .getKeys(knifeClipValue6 + 515.0F, 0.0F);
      }

      float knifeClipValue7 = knifeClipValue5 + knifeClipLimit * 450.0F;
      float groupParseData = 360.0F;
      keys2.getKeys(knifeClipValue7, 0.0F).getKeys(knifeClipValue7 + 25.0F, min2 * 0.5F).getKeys(knifeClipValue7 + 60.0F, 0.0F);
      keys.getKeys(knifeClipValue7 + 40.0F, 0.0F)
         .getKeys(knifeClipValue7 + 150.0F, 90.0F)
         .getKeys(knifeClipValue7 + 460.0F, 90.0F)
         .getKeys(knifeClipValue7 + 490.0F, 72.0F)
         .getKeys(knifeClipValue7 + 550.0F, 10.0F)
         .getKeys(knifeClipValue7 + 560.0F, 0.0F);
      keys3.getKeys(knifeClipValue7 + 70.0F, 0.0F)
         .getKeys(knifeClipValue7 + 170.0F, -8.0F)
         .getKeys(knifeClipValue7 + 205.0F, 27.0F)
         .getKeys(knifeClipValue7 + 410.0F, groupParseData - 30.0F)
         .getKeys(knifeClipValue7 + 440.0F, groupParseData)
         .getKeys(knifeClipValue7 + 550.0F, groupParseData)
         .getKeys(knifeClipValue7 + 580.0F, groupParseData - min2 * 0.3F)
         .getKeys(knifeClipValue7 + 620.0F, groupParseData);
      keys7.getKeys(knifeClipValue7 + 20.0F, -4.0F).getKeys(knifeClipValue7 + 180.0F, 8.0F).getKeys(knifeClipValue7 + 430.0F, 8.0F).getKeys(knifeClipValue7 + 570.0F, -4.0F);
      keys9.getKeys(knifeClipValue7 + 100.0F, 0.02F).getKeys(knifeClipValue7 + 200.0F, 0.035F).getKeys(knifeClipValue7 + 440.0F, 0.03F).getKeys(knifeClipValue7 + 570.0F, 0.02F);
      keys8.getKeys(knifeClipValue7 + 100.0F, -0.02F).getKeys(knifeClipValue7 + 200.0F, -0.03F).getKeys(knifeClipValue7 + 440.0F, -0.03F).getKeys(knifeClipValue7 + 570.0F, -0.02F);
      float knifeClipValue9 = knifeClipValue7 + 600.0F;
      keys2.getKeys(knifeClipValue9 + 20.0F, 0.0F)
         .getKeys(knifeClipValue9 + 140.0F, 150.0F)
         .getKeys(knifeClipValue9 + 350.0F, 144.0F)
         .getKeys(knifeClipValue9 + 430.0F, 12.0F)
         .getKeys(knifeClipValue9 + 440.0F, 0.0F)
         .getKeys(knifeClipValue9 + 470.0F, min2 * 0.5F)
         .getKeys(knifeClipValue9 + 510.0F, 0.0F);
      keys4.getKeys(knifeClipValue9 + 150.0F, 0.0F).getKeys(knifeClipValue9 + 350.0F, -180.0F);
      keys3.getKeys(knifeClipValue9 + 60.0F, groupParseData + 6.0F)
         .getKeys(knifeClipValue9 + 140.0F, groupParseData - 10.0F)
         .getKeys(knifeClipValue9 + 350.0F, groupParseData + 16.0F)
         .getKeys(knifeClipValue9 + 440.0F, groupParseData + 16.0F + 2.0F)
         .getKeys(knifeClipValue9 + 475.0F, groupParseData + 16.0F + 2.0F + min2 * 0.15F)
         .getKeys(knifeClipValue9 + 520.0F, groupParseData + 16.0F);
      keys7.getKeys(knifeClipValue9 + 20.0F, -4.0F)
         .getKeys(knifeClipValue9 + 120.0F, 4.0F)
         .getKeys(knifeClipValue9 + 280.0F, 0.0F)
         .getKeys(knifeClipValue9 + 440.0F, -1.0F)
         .getKeys(knifeClipValue9 + 520.0F, -4.0F);
      keys9.getKeys(knifeClipValue9 + 100.0F, 0.02F).getKeys(knifeClipValue9 + 280.0F, 0.031999998F).getKeys(knifeClipValue9 + 440.0F, 0.011999999F).getKeys(knifeClipValue9 + 520.0F, 0.02F);
      keys5.getKeys(knifeClipValue9 + 60.0F, 0.0F).getKeys(knifeClipValue9 + 200.0F, -3.0F).getKeys(knifeClipValue9 + 380.0F, 2.0F).getKeys(knifeClipValue9 + 520.0F, 0.0F);
      float knifeClipValue10 = knifeClipValue9 + 520.0F;
      float amount = knifeClipValue10 + 950.0F;
      knifeInspectClip.clipDurationMs = amount;
      keys7.getKeys(knifeClipValue10 + 40.0F, -6.0F).getKeys(knifeClipValue10 + 330.0F, -14.0F).getKeys(knifeClipValue10 + 580.0F, -16.0F).getKeys(knifeClipValue10 + 840.0F, 0.0F).getKeys(amount, 0.0F);
      keys3.getKeys(knifeClipValue10 + 40.0F, groupParseData + 16.0F)
         .getKeys(knifeClipValue10 + 330.0F, groupParseData + 20.0F)
         .getKeys(knifeClipValue10 + 580.0F, groupParseData + 20.0F - 2.0F)
         .getKeys(knifeClipValue10 + 850.0F, groupParseData)
         .getKeys(amount, groupParseData);
      keys6.getKeys(knifeClipValue10 + 40.0F, 0.0F)
         .getKeys(knifeClipValue10 + 330.0F, 8.0F)
         .getKeys(knifeClipValue10 + 580.0F, 7.0F)
         .getKeys(knifeClipValue10 + 820.0F, 0.0F)
         .getKeys(knifeClipValue10 + 880.0F, min2 * 0.15F)
         .getKeys(amount, 0.0F);
      keys4.getKeys(knifeClipValue10 + 40.0F, -180.0F)
         .getKeys(knifeClipValue10 + 330.0F, -168.0F)
         .getKeys(knifeClipValue10 + 580.0F, -172.0F)
         .getKeys(knifeClipValue10 + 850.0F, 0.0F)
         .getKeys(amount, 0.0F);
      keys9.getKeys(knifeClipValue10 + 40.0F, 0.02F)
         .getKeys(knifeClipValue10 + 330.0F, 0.031999998F)
         .getKeys(knifeClipValue10 + 580.0F, 0.027999997F)
         .getKeys(knifeClipValue10 + 820.0F, -0.006F)
         .getKeys(knifeClipValue10 + 900.0F, 0.0F)
         .getKeys(amount, 0.0F);
      keys8.getKeys(knifeClipValue10 + 580.0F, -0.02F).getKeys(knifeClipValue10 + 860.0F, 0.0F).getKeys(amount, 0.0F);
      keys10.getKeys(knifeClipValue10 + 580.0F, -0.015F).getKeys(knifeClipValue10 + 860.0F, 0.0F).getKeys(amount, 0.0F);
      keys11.getKeys(knifeClipValue10 + 580.0F, -5.0F).getKeys(knifeClipValue10 + 860.0F, 0.0F).getKeys(amount, 0.0F);
      keys5.getKeys(knifeClipValue10 + 40.0F, 0.0F).getKeys(knifeClipValue10 + 330.0F, 4.0F).getKeys(knifeClipValue10 + 580.0F, 3.0F).getKeys(knifeClipValue10 + 840.0F, 0.0F).getKeys(amount, 0.0F);
      keys.getKeys(amount, 0.0F);
      keys2.getKeys(amount, 0.0F);
      keys12.getKeys(amount, 0.0F);
      knifeInspectClip.getTracks(11, keys3.getAmounts(amount));
      knifeInspectClip.getTracks(12, keys4.getAmounts(amount));
      knifeInspectClip.getTracks(15, keys.getAmounts(amount));
      knifeInspectClip.getTracks(16, keys2.getAmounts(amount));
      knifeInspectClip.getTracks(5, keys5.getAmounts(amount));
      knifeInspectClip.getTracks(6, keys6.getAmounts(amount));
      knifeInspectClip.getTracks(7, keys7.getAmounts(amount));
      knifeInspectClip.getTracks(0, keys8.getAmounts(amount));
      knifeInspectClip.getTracks(1, keys9.getAmounts(amount));
      knifeInspectClip.getTracks(2, keys10.getAmounts(amount));
      knifeInspectClip.getTracks(4, keys11.getAmounts(amount));
      knifeInspectClip.getTracks(3, keys12.getAmounts(amount));
   }

   private static final class Keys {
      final List<Float> floatList;
      final List<Float> keyframeTimes = new ArrayList<>();

      private Keys() {
         this.floatList = new ArrayList<>();
      }

      KnifeClipButterfly.Keys getKeys(float knifeClipValue, float groupParseData) {
         this.keyframeTimes.add(knifeClipValue);
         this.floatList.add(groupParseData);
         return this;
      }

      float[] getAmounts(float knifeClipDivisor) {
         float[] floats = new float[this.keyframeTimes.size() * 2];

         for (int i = 0; i < this.keyframeTimes.size(); i++) {
            floats[2 * i] = this.keyframeTimes.get(i) / knifeClipDivisor;
            floats[2 * i + 1] = this.floatList.get(i);
         }

         floats[0] = 0.0F;
         floats[floats.length - 2] = 1.0F;
         return floats;
      }
   }
}
