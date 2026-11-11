package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import com.corz.client.utils.KnifeInspectClip;

final class KnifeClipM9 {

   private static float[] getAmounts(int knifeClipValue) {
      int knifeClipLimit = 14 * knifeClipValue + 6;
      float[] floats = new float[knifeClipLimit + 6];
      floats[0] = 0.02F;
      floats[1] = 0.04F;
      floats[2] = 0.07F;
      floats[3] = 0.1F;

      for (int i = 1; i <= knifeClipLimit; i++) {
         floats[3 + i] = 0.1F + 0.86F * i / (knifeClipLimit + 1);
      }

      floats[knifeClipLimit + 4] = 0.975F;
      floats[knifeClipLimit + 5] = 0.99F;
      return floats;
   }

   private static double[] getAmounts(double knifeClipFactor, float knifeClipValue, int knifeClipDivisor) {
      double[] doubles = new double[knifeClipDivisor + 1];
      double knifeClipFactor2 = 1.0 / knifeClipDivisor;

      for (int i = 1; i <= knifeClipDivisor; i++) {
         double knifeClipValue2 = (i - 1.0) * knifeClipFactor2;
         double knifeClipValue3 = doubles[i - 1] + 0.5 * knifeClipFactor2 * knifeClipFactor * getAmount(knifeClipValue2, knifeClipValue - doubles[i - 1]);
         doubles[i] = doubles[i - 1] + knifeClipFactor2 * knifeClipFactor * getAmount(knifeClipValue2 + 0.5 * knifeClipFactor2, knifeClipValue - knifeClipValue3);
      }

      return doubles;
   }

   static void handle(KnifeInspectClip knifeInspectClip, float knifeClipBound, float wrapDegreesBound, int intersectsCenterBound) {
      int knifeClipFactor = Math.min(intersectsCenterBound, 3);
      float knifeClipValue = Math.min(knifeClipBound, 40.0F);
      float min2 = Math.min(wrapDegreesBound, 30.0F);
      float knifeClipValue3 = 0.06F * (knifeClipFactor - 1);
      float knifeClipValue4 = -360.0F * knifeClipFactor;
      float knifeClipValue5 = 1.0F - knifeClipValue3 / 0.6F;
      KnifeClipM9.Keys keys = new KnifeClipM9.Keys();
      float knifeClipValue6 = 0.035F * knifeClipValue5;
      float knifeClipValue7 = 0.095F * knifeClipValue5;
      float knifeClipValue8 = 0.15F * knifeClipValue5;
      float knifeClipValue9 = 0.3F * knifeClipValue5;
      float knifeClipValue10 = 0.33F * knifeClipValue5;
      float knifeClipValue11 = 0.385F * knifeClipValue5;
      float knifeClipValue12 = 0.41F * knifeClipValue5;
      float knifeClipValue13 = 0.44F * knifeClipValue5;
      float knifeClipValue14 = 0.57F * knifeClipValue5;
      float knifeClipValue15 = 0.6F - knifeClipValue3;
      float knifeClipValue16 = knifeClipValue15 + 0.05F;
      float knifeClipValue17 = knifeClipValue16 + 0.15F + knifeClipValue3;
      float knifeClipValue18 = knifeClipValue17 + 0.045F;
      float knifeClipValue19 = knifeClipValue18 + 0.025F;
      float knifeClipValue20 = knifeClipValue19 + 0.07F;
      float knifeClipValue21 = knifeClipValue20 + 0.03F;
      keys.getAmount(0.0F).getKeys(0, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 10);
      keys.getAmount(knifeClipValue6)
         .getAmount(0, 0.006F)
         .getAmount(1, -0.012F)
         .getAmount(2, -0.004F)
         .getAmount(3, -knifeClipValue * 0.08F)
         .getAmount(5, -knifeClipValue * 0.1F)
         .getAmount(7, knifeClipValue * 0.3F)
         .getAmount(11, knifeClipValue * 0.12F);
      keys.getAmount(knifeClipValue7)
         .getAmount(0, -0.012F)
         .getAmount(1, 0.03F)
         .getAmount(2, -0.02F)
         .getAmount(3, 2.0F)
         .getAmount(5, 13.0F)
         .getAmount(7, -12.0F - min2 * 0.2F)
         .getAmount(11, -1.0F)
         .getAmount(13, 3.0F);
      keys.getAmount(knifeClipValue8)
         .getAmount(0, -0.012F)
         .getAmount(1, 0.028F)
         .getAmount(2, -0.02F)
         .getAmount(3, 1.5F)
         .getAmount(5, 12.0F)
         .getAmount(7, -12.0F)
         .getAmount(11, 0.0F)
         .getAmount(13, 3.0F)
         .getAmount(12, 0.0F)
         .getAmount(10, 0.0F);
      float deltaVar = knifeClipValue9 - knifeClipValue8;
      keys.getAmount(knifeClipValue8 + deltaVar * 0.5F)
         .getAmount(7, 5.0F)
         .getAmount(5, 9.0F)
         .getAmount(3, 0.0F)
         .getAmount(4, -3.0F)
         .getAmount(2, -0.026F)
         .getAmount(13, 1.0F)
         .getAmount(11, -3.0F);
      keys.getAmount(knifeClipValue9)
         .getAmount(0, -0.01F)
         .getAmount(1, 0.024F)
         .getAmount(2, -0.014F)
         .getAmount(3, 1.0F)
         .getAmount(4, 0.0F)
         .getAmount(5, 7.0F)
         .getAmount(7, 18.0F + min2 * 0.2F)
         .getAmount(12, 0.0F)
         .getAmount(11, -6.0F)
         .getAmount(13, 0.0F)
         .getAmount(10, 0.0F)
         .getAmount(6, 0.0F);
      keys.getAmount(knifeClipValue10)
         .getAmount(7, 18.0F)
         .getAmount(12, -knifeClipValue * 0.25F)
         .getAmount(5, 7.0F)
         .getAmount(3, 1.0F)
         .getAmount(10, 0.72F)
         .getAmount(6, -1.0F - knifeClipValue * 0.08F);
      keys.getAmount((knifeClipValue10 + knifeClipValue11) * 0.5F).getAmount(10, 1.2F).getAmount(6, 3.0F);
      keys.getAmount(knifeClipValue11)
         .getAmount(7, 15.0F)
         .getAmount(12, 180.0F + min2 * 0.6F)
         .getAmount(5, 8.0F)
         .getAmount(3, 3.0F)
         .getAmount(10, 0.66F)
         .getAmount(6, 1.0F);
      keys.getAmount(knifeClipValue12).getAmount(7, 17.0F).getAmount(12, 180.0F).getAmount(5, 7.0F).getAmount(3, 2.0F).getAmount(10, 0.3F).getAmount(6, 0.0F);
      keys.getAmount(knifeClipValue13)
         .getAmount(7, 18.0F)
         .getAmount(12, 180.0F)
         .getAmount(5, 7.0F)
         .getAmount(2, -0.014F)
         .getAmount(13, 0.0F)
         .getAmount(4, 0.0F)
         .getAmount(3, 2.0F)
         .getAmount(10, 0.0F)
         .getAmount(11, -6.0F);
      float deltaVar2 = knifeClipValue14 - knifeClipValue13;
      keys.getAmount(knifeClipValue13 + deltaVar2 * 0.55F)
         .getAmount(7, -24.0F)
         .getAmount(5, 1.0F)
         .getAmount(2, -0.02F)
         .getAmount(4, -2.0F)
         .getAmount(3, 0.0F)
         .getAmount(11, -2.0F);
      keys.getAmount(knifeClipValue14)
         .getAmount(7, -12.0F + min2 * 0.3F)
         .getAmount(12, 180.0F)
         .getAmount(5, 6.0F)
         .getAmount(0, -0.01F)
         .getAmount(1, 0.025F)
         .getAmount(2, -0.016F)
         .getAmount(13, 0.0F)
         .getAmount(4, -1.0F)
         .getAmount(3, 1.0F)
         .getAmount(11, 0.0F);
      keys.getAmount(knifeClipValue15)
         .getAmount(0, -0.01F)
         .getAmount(1, 0.025F)
         .getAmount(2, -0.012F)
         .getAmount(12, 180.0F)
         .getAmount(7, -10.0F)
         .getAmount(11, 0.0F)
         .getAmount(4, 0.0F)
         .getAmount(5, 5.0F)
         .getAmount(3, 2.0F)
         .getAmount(6, 0.0F)
         .getAmount(13, 0.0F)
         .getAmount(10, 0.0F);
      float knifeClipValue22 = 8.0F + knifeClipValue * 0.3F;
      float knifeClipLimit = knifeClipValue22 - knifeClipValue4;
      float knifeClipValue23 = Math.min(min2 * 0.55F, 10.0F) * 1.3F;
      float deltaVar4 = knifeClipValue17 - knifeClipValue16;
      float knifeClipValue24 = knifeClipValue16 + deltaVar4 * 0.15F;
      keys.getAmount(knifeClipValue16).getAmount(11, knifeClipValue22).getAmount(7, -12.0F).getAmount(6, -knifeClipValue * 0.3F).getAmount(3, 3.0F).getAmount(10, 0.3F);
      float[] knifeClipValues = getAmounts(knifeClipFactor);
      float[] amounts2 = getAmounts(knifeClipValues, knifeClipValue22, knifeClipLimit);

      for (int i = 0; i < knifeClipValues.length; i++) {
         keys.getAmount(knifeClipValue16 + deltaVar4 * knifeClipValues[i]).getAmount(11, knifeClipValue22 - knifeClipLimit * amounts2[i]);
      }

      keys.getAmount(knifeClipValue24).getAmount(1, 0.065F).getAmount(2, -0.08F);
      keys.getAmount(knifeClipValue16 + deltaVar4 * 0.3F).getAmount(6, 2.0F).getAmount(3, 2.0F).getAmount(7, -11.0F);
      keys.getAmount(knifeClipValue17 - 0.012F).getAmount(1, 0.065F).getAmount(2, -0.08F).getAmount(6, 1.0F).getAmount(3, 2.5F).getAmount(7, -10.0F).getAmount(10, 0.3F);
      keys.getAmount(knifeClipValue17).getAmount(11, knifeClipValue4).getAmount(6, knifeClipValue23 * 0.4F).getAmount(7, -12.0F).getAmount(3, 4.0F).getAmount(0, -0.01F);
      keys.getAmount(knifeClipValue17 + 0.014F).getAmount(6, knifeClipValue23);
      keys.getAmount(knifeClipValue18)
         .getAmount(11, knifeClipValue4)
         .getAmount(6, -2.0F)
         .getAmount(7, -10.0F)
         .getAmount(3, 2.5F)
         .getAmount(1, 0.057000004F)
         .getAmount(2, -0.064F)
         .getAmount(10, 0.3F);
      keys.getAmount(knifeClipValue19)
         .getAmount(12, 180.0F)
         .getAmount(7, -10.0F)
         .getAmount(11, knifeClipValue4)
         .getAmount(6, 0.0F)
         .getAmount(3, 2.0F)
         .getAmount(4, 0.0F)
         .getAmount(5, 5.0F)
         .getAmount(0, -0.01F)
         .getAmount(1, 0.049000002F)
         .getAmount(2, -0.048F)
         .getAmount(10, 0.72F);
      keys.getAmount((knifeClipValue19 + knifeClipValue20) * 0.5F).getAmount(10, 1.2F);
      keys.getAmount(knifeClipValue20)
         .getAmount(12, -min2 * 0.3F)
         .getAmount(7, 8.0F)
         .getAmount(3, 1.0F)
         .getAmount(5, 2.0F)
         .getAmount(0, -0.004F)
         .getAmount(1, 0.01F)
         .getAmount(2, -0.005F)
         .getAmount(10, 0.6F);
      keys.getAmount(knifeClipValue21)
         .getAmount(12, 0.0F)
         .getAmount(7, 2.0F)
         .getAmount(3, 0.0F)
         .getAmount(5, 0.5F)
         .getAmount(0, -0.001F)
         .getAmount(1, 0.002F)
         .getAmount(2, -0.001F)
         .getAmount(10, 0.120000005F);
      keys.getAmount(1.0F).getKeys(0, 1, 2, 3, 4, 5, 6, 7, 13, 12, 10).getAmount(11, knifeClipValue4);
      keys.handle(knifeInspectClip);
   }

   private KnifeClipM9() {
   }

   private static float[] getAmounts(float[] knifeClipValues, float knifeClipValue, float knifeClipLimit) {
      short index = 1000;
      double knifeClipFactor2 = 0.0;
      double knifeClipFactor3 = knifeClipLimit * 4.0;

      for (int i = 0; i < 24; i++) {
         double knifeClipFactor = (knifeClipFactor2 + knifeClipFactor3) * 0.5;
         if (getAmounts(knifeClipFactor, knifeClipValue, index)[index] < knifeClipLimit) {
            knifeClipFactor2 = knifeClipFactor;
         } else {
            knifeClipFactor3 = knifeClipFactor;
         }
      }

      double[] amounts2 = getAmounts((knifeClipFactor2 + knifeClipFactor3) * 0.5, knifeClipValue, index);
      float[] floats = new float[knifeClipValues.length];

      for (int j = 0; j < knifeClipValues.length; j++) {
         floats[j] = (float)(amounts2[Math.round(knifeClipValues[j] * index)] / amounts2[index]);
      }

      return floats;
   }

   private static double getAmount(double knifeClipValue, double knifeClipValue2) {
      double cos = Math.cos(Math.toRadians(knifeClipValue2 - -196.0));
      double knifeClipFactor = (1.0 + cos) * 0.5;
      double knifeClipFactor2 = cos * cos;
      return getAmount(knifeClipValue) * (1.0 + 0.3 * knifeClipFactor * knifeClipFactor + 1.0 * knifeClipFactor2 * knifeClipFactor2);
   }

   private static double getAmount(double knifeClipLimit) {
      if (knifeClipLimit < 0.1) {
         double knifeClipFactor = knifeClipLimit / 0.1;
         return knifeClipFactor * knifeClipFactor * (3.0 - 2.0 * knifeClipFactor);
      } else {
         double knifeClipFactor2 = 0.9 + 0.2 * Math.exp(-(knifeClipLimit - 0.1) / 0.1);
         return knifeClipLimit > 0.985 ? knifeClipFactor2 * (1.0 - knifeClipLimit) / 0.015 : knifeClipFactor2;
      }
   }

   private static final class Keys {
      private float keyframeTime;
      private final Map<Integer, TreeMap<Float, Float>> floatMap = new HashMap<>();

      KnifeClipM9.Keys getKeys(int... knifeClipValues) {
         for (int knifeClipValue : knifeClipValues) {
            this.getAmount(knifeClipValue, 0.0F);
         }

         return this;
      }

      KnifeClipM9.Keys getAmount(float amount) {
         this.keyframeTime = amount;
         return this;
      }

      void handle(KnifeInspectClip knifeInspectClip) {
         for (Entry entry : this.floatMap.entrySet()) {
            ArrayList arrayList = new ArrayList();

            for (Entry entry2 : (Iterable<java.util.Map.Entry>) (((TreeMap)entry.getValue()).entrySet())) {
               arrayList.add((Float)entry2.getKey());
               arrayList.add((Float)entry2.getValue());
            }

            knifeInspectClip.getTracks((Integer)entry.getKey(), KnifeInspectClip.getAmounts(arrayList));
         }
      }

      KnifeClipM9.Keys getAmount(int knifeClipValue, float knifeClipValue2) {
         this.floatMap.computeIfAbsent(knifeClipValue, integer -> new TreeMap<>()).put(this.keyframeTime, knifeClipValue2);
         return this;
      }
   }
}
