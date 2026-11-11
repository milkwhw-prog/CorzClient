package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import com.corz.client.utils.KnifeInspectClip;

final class KnifeClipStiletto {

   private static float getAmount(float degrees) {
      return -0.20000005F - 1.4F * (float)Math.cos(Math.toRadians(degrees));
   }

   private static float getAmountForFloat(float degrees) {
      return 0.5F + 1.4F * (float)Math.sin(Math.toRadians(degrees));
   }

   private KnifeClipStiletto() {
   }

   static void handleAmount(KnifeInspectClip knifeInspectClip, float knifeClipBound, float wrapDegreesBound, int intersectsCenterBound) {
      int knifeClipValue = Math.min(intersectsCenterBound, 3);
      float knifeClipValue2 = 1.0F + 0.045F * (knifeClipValue - 1);
      knifeInspectClip.clipDurationMs = 3200.0F * knifeClipValue2;
      float min3 = Math.min(knifeClipBound, 45.0F);
      float min4 = Math.min(wrapDegreesBound, 36.0F);
      KnifeClipStiletto.Keys keys = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys2 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys3 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys4 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys5 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys6 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys7 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys8 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys9 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys10 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys11 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys12 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys13 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys14 = new KnifeClipStiletto.Keys();
      KnifeClipStiletto.Keys keys15 = new KnifeClipStiletto.Keys();

      for (KnifeClipStiletto.Keys keys16 : new KnifeClipStiletto.Keys[]{
         keys, keys2, keys3, keys4, keys5, keys6, keys7, keys8, keys9, keys10, keys11, keys12, keys13, keys14, keys15
      }) {
         keys16.getTree(0.0F, 0.0F);
      }

      keys13.getTree(0.012F, -3.0F).getTree(0.08F, 92.0F).getTree(0.14F, 170.0F).getTree(0.2F, 180.0F);
      keys11.getTree(0.012F, 0.0F).getTree(0.075F, 0.6F).getTree(0.14F, 0.0F);
      keys12.getTree(0.03F, 4.0F).getTree(0.13F, -9.0F);
      keys8.getTree(0.05F, -6.0F).getTree(0.13F, -10.0F);
      keys7.getTree(0.04F, 3.0F).getTree(0.13F, -5.0F);
      keys2.getTree(0.02F, -0.006F).getTree(0.11F, 0.02F);
      keys.getTree(0.11F, -0.012F);
      keys3.getTree(0.11F, 0.012F);
      keys4.getTree(0.02F, 2.0F).getTree(0.11F, -2.0F);
      keys5.getTree(0.11F, -3.0F);
      keys6.getTree(0.11F, -1.0F);
      keys15.getTree(0.14F, 0.0F).getTree(0.155F, 7.0F).getTree(0.19F, 70.0F).getTree(0.222F, 150.0F).getTree(0.236F, 172.0F);
      keys10.getTree(0.15F, 0.0F).getTree(0.236F, 0.5F);
      keys12.getTree(0.15F, -9.5F).getTree(0.236F, -12.0F);
      keys7.getTree(0.15F, -5.0F).getTree(0.236F, -8.0F);
      keys2.getTree(0.236F, 0.024F);
      keys12.getTree(0.27F, -16.0F).getTree(0.3F, -15.0F).getTree(0.318F, -11.0F);
      keys8.getTree(0.236F, -10.0F).getTree(0.28F, 3.0F).getTree(0.315F, -5.0F);
      keys5.getTree(0.275F, -5.0F).getTree(0.318F, -4.0F);
      keys2.getTree(0.3F, 0.028F).getTree(0.318F, 0.02F);
      keys4.getTree(0.236F, -2.0F).getTree(0.3F, -3.0F).getTree(0.318F, 0.0F);
      keys15.getTree(0.326F, 172.0F).getTree(0.3305F, 166.0F).getTree(0.3415F, 88.0F).getTree(0.3475F, 22.0F).getTree(0.3505F, 0.0F).getTree(knifeClipValue2, 0.0F);
      keys10.getTree(0.3505F, 0.5F).getTree(0.4F, -1.6F);
      keys9.getTree(0.3505F, 0.0F).getTree(0.4F, 0.5F);
      keys12.getTree(0.3505F, -9.5F).getTree(0.357F, -5.0F).getTree(0.38F, -11.0F);
      keys2.getTree(0.3505F, 0.024F).getTree(0.357F, 0.034F).getTree(0.38F, 0.02F);
      keys6.getTree(0.3505F, -1.0F).getTree(0.357F, -3.0F).getTree(0.38F, 0.0F);
      keys4.getTree(0.35F, 1.0F).getTree(0.365F, -4.0F).getTree(0.39F, -2.0F);
      keys7.getTree(0.3505F, -8.0F).getTree(0.358F, -2.0F).getTree(0.38F, -7.0F);
      keys8.getTree(0.358F, 2.0F).getTree(0.39F, -4.0F);
      float knifeClipValue5 = 0.42F;
      float knifeClipValue6 = 0.44F;
      float knifeClipValue7 = 0.46F;
      float knifeClipFactor = 0.08F + 0.045F * knifeClipValue;
      float knifeClipValue8 = -min3 * 0.5F;
      keys12.getTree(knifeClipValue5, -4.0F).getTree(knifeClipValue7, knifeClipValue8);
      keys14.getTree(knifeClipValue5, -6.0F).getTree(knifeClipValue7, 0.0F);
      keys9.getTree(knifeClipValue5, 0.5F).getTree(knifeClipValue7, getAmountForFloat(knifeClipValue8));
      keys10.getTree(knifeClipValue5, -1.6F).getTree(knifeClipValue7, getAmount(knifeClipValue8));
      keys11.getTree(knifeClipValue5, 0.0F).getTree(knifeClipValue7, 0.5F);
      keys8.getTree(knifeClipValue5, -6.0F).getTree(knifeClipValue7, -2.0F);
      keys7.getTree(knifeClipValue5, -4.0F).getTree(knifeClipValue6, -1.0F).getTree(knifeClipValue7, -3.0F);
      keys2.getTree(knifeClipValue5, 0.016F).getTree(knifeClipValue6, 0.008F).getTree(knifeClipValue7, 0.022F);
      keys.getTree(knifeClipValue5, -0.01F).getTree(knifeClipValue7, -0.012F);
      keys3.getTree(knifeClipValue5, 0.01F).getTree(knifeClipValue7, 0.004F);
      keys4.getTree(knifeClipValue5, -3.0F).getTree(knifeClipValue6, 0.0F).getTree(knifeClipValue7, 1.0F);
      keys5.getTree(knifeClipValue5, -3.0F).getTree(knifeClipValue7, -2.0F);
      keys6.getTree(knifeClipValue5, 0.0F).getTree(knifeClipValue6, 2.0F).getTree(knifeClipValue7, 0.0F);
      ArrayList arrayList = new ArrayList();

      for (int i = 45; i <= 360 * knifeClipValue; i += 45) {
         arrayList.add((float)i);
      }

      arrayList.add(360.0F * knifeClipValue + min4 * 0.6F);
      float[] floats = new float[arrayList.size()];
      float knifeClipValue9 = knifeClipValue8;
      float knifeClipTotal = 0.0F;

      for (int j = 0; j < floats.length; j++) {
         float knifeClipValue10 = (Float)arrayList.get(j);
         float degrees = (knifeClipValue9 + knifeClipValue10) * 0.5F;
         float pilotStatusBound = (knifeClipValue10 - knifeClipValue9) / 45.0F * (1.0F + 0.18F * (float)Math.cos(Math.toRadians(degrees)));
         if (j == 0) {
            pilotStatusBound *= 1.5F;
         }

         if (j == floats.length - 2) {
            pilotStatusBound *= 1.15F;
         }

         if (j == floats.length - 1) {
            pilotStatusBound = Math.max(pilotStatusBound, 0.9F) * 1.6F;
         }

         floats[j] = pilotStatusBound;
         knifeClipTotal += pilotStatusBound;
         knifeClipValue9 = knifeClipValue10;
      }

      float treeValue = knifeClipValue7;

      for (int k = 0; k < floats.length; k++) {
         treeValue += knifeClipFactor * floats[k] / knifeClipTotal;
         float knifeClipValue11 = (Float)arrayList.get(k);
         keys12.getTree(treeValue, knifeClipValue11);
         keys9.getTree(treeValue, getAmountForFloat(knifeClipValue11));
         keys10.getTree(treeValue, getAmount(knifeClipValue11));
         if (k < floats.length - 2 && knifeClipValue11 % 360.0F == 90.0F) {
            keys2.getTree(treeValue, 0.012F);
            keys6.getTree(treeValue, 3.0F);
            keys8.getTree(treeValue, -6.0F);
            keys.getTree(treeValue, -0.016F);
         } else if (k < floats.length - 2 && knifeClipValue11 % 360.0F == 270.0F) {
            keys2.getTree(treeValue, 0.03F);
            keys6.getTree(treeValue, -2.0F);
            keys8.getTree(treeValue, 2.0F);
            keys.getTree(treeValue, -0.008F);
         }
      }

      float knifeClipValue12 = treeValue + 0.028F;
      float knifeClipValue13 = knifeClipValue12 + 0.022F;
      float knifeClipValue14 = 360.0F * knifeClipValue;
      keys12.getTree(knifeClipValue12, knifeClipValue14);
      keys9.getTree(knifeClipValue12, getAmountForFloat(0.0F)).getTree(knifeClipValue13, 0.0F).getTree(knifeClipValue2, 0.0F);
      keys10.getTree(knifeClipValue12, getAmount(0.0F)).getTree(knifeClipValue13, 0.0F).getTree(knifeClipValue2, 0.0F);
      keys11.getTree(knifeClipValue12, 0.5F).getTree(knifeClipValue13, 0.0F);
      keys2.getTree(treeValue, 0.026F).getTree(knifeClipValue12, 0.012F).getTree(knifeClipValue13, 0.016F);
      keys4.getTree(treeValue, 2.0F).getTree(knifeClipValue12, -3.0F).getTree(knifeClipValue13, -1.0F);
      keys8.getTree(treeValue, -2.0F).getTree(knifeClipValue12, -7.0F).getTree(knifeClipValue13, -5.0F);
      keys7.getTree(treeValue, -3.0F).getTree(knifeClipValue12, 2.0F).getTree(knifeClipValue13, -2.0F);
      keys6.getTree(treeValue, 0.0F).getTree(knifeClipValue12, 3.0F).getTree(knifeClipValue13, 1.0F);
      keys.getTree(knifeClipValue12, -0.012F);
      keys3.getTree(knifeClipValue12, 0.004F);
      keys5.getTree(knifeClipValue12, -3.0F);
      float knifeClipValue15 = knifeClipValue13 + 0.015F;
      float knifeClipValue16 = knifeClipValue2 - 0.09F;
      float deltaVar = knifeClipValue16 - knifeClipValue15;
      float knifeClipValue17 = knifeClipValue15 + Math.max(0.034F, 0.15F * deltaVar);
      float knifeClipValue18 = knifeClipValue15 + 0.36F * deltaVar;
      float knifeClipValue19 = knifeClipValue15 + 0.57F * deltaVar;
      float knifeClipValue20 = knifeClipValue15 + 0.8F * deltaVar;
      keys13.getTree(knifeClipValue15, 180.0F)
         .getTree(knifeClipValue17, 162.0F)
         .getTree(knifeClipValue18, 165.0F)
         .getTree(knifeClipValue19, 362.0F)
         .getTree(knifeClipValue20, 366.0F)
         .getTree(knifeClipValue16, 360.0F + min4 * 0.25F)
         .getTree(knifeClipValue2 - 0.07F, 360.0F)
         .getTree(knifeClipValue2, 360.0F);
      keys12.getTree(knifeClipValue15, knifeClipValue14)
         .getTree(knifeClipValue17, knifeClipValue14 + 6.0F)
         .getTree(knifeClipValue18, knifeClipValue14 + 4.0F)
         .getTree(knifeClipValue19, knifeClipValue14 - 116.0F)
         .getTree(knifeClipValue20, knifeClipValue14 - 118.0F)
         .getTree(knifeClipValue16, knifeClipValue14)
         .getTree(knifeClipValue2, knifeClipValue14);
      keys14.getTree(knifeClipValue15, 0.0F)
         .getTree(knifeClipValue17, -3.0F)
         .getTree(knifeClipValue18, -4.0F)
         .getTree(knifeClipValue19, -4.0F)
         .getTree(knifeClipValue20, -5.0F)
         .getTree(knifeClipValue16, -2.0F)
         .getTree(knifeClipValue2 - 0.07F, 0.0F);
      keys11.getTree(knifeClipValue18, 0.0F).getTree((knifeClipValue18 + knifeClipValue19) * 0.5F, 0.5F).getTree(knifeClipValue19, 0.0F).getTree(knifeClipValue2, 0.0F);
      keys7.getTree(knifeClipValue17, 10.0F).getTree(knifeClipValue18, 12.0F).getTree(knifeClipValue19, -25.0F).getTree(knifeClipValue20, -26.0F).getTree(knifeClipValue16, -3.0F);
      keys8.getTree(knifeClipValue17, -8.0F).getTree(knifeClipValue18, -8.0F).getTree(knifeClipValue19, -4.0F).getTree(knifeClipValue20, -4.0F).getTree(knifeClipValue16, -2.0F);
      keys5.getTree(knifeClipValue17, -5.0F).getTree(knifeClipValue19, -6.0F).getTree(knifeClipValue20, -6.0F).getTree(knifeClipValue16, -2.0F);
      keys.getTree(knifeClipValue17, -0.016F).getTree(knifeClipValue19, -0.006F).getTree(knifeClipValue20, -0.006F).getTree(knifeClipValue16, -0.008F);
      keys2.getTree(knifeClipValue17, 0.022F).getTree(knifeClipValue19, -0.008F).getTree(knifeClipValue20, -0.006F).getTree(knifeClipValue16, 0.01F);
      keys3.getTree(knifeClipValue17, -0.008F).getTree(knifeClipValue19, -0.004F).getTree(knifeClipValue20, -0.002F).getTree(knifeClipValue16, -0.004F);
      keys4.getTree(knifeClipValue17, -3.0F).getTree(knifeClipValue20, -3.0F).getTree(knifeClipValue16, -1.0F);
      keys6.getTree(knifeClipValue17, 1.0F).getTree(knifeClipValue19, 0.0F).getTree(knifeClipValue16, 0.0F);
      keys.getTree(knifeClipValue2 - 0.035F, 0.003F).getTree(knifeClipValue2, 0.0F);
      keys2.getTree(knifeClipValue2 - 0.038F, -0.005F).getTree(knifeClipValue2, 0.0F);
      keys3.getTree(knifeClipValue2 - 0.035F, -0.003F).getTree(knifeClipValue2, 0.0F);
      keys5.getTree(knifeClipValue2 - 0.035F, 1.0F).getTree(knifeClipValue2, 0.0F);
      keys6.getTree(knifeClipValue2, 0.0F);
      keys4.getTree(knifeClipValue2 - 0.038F, 1.5F).getTree(knifeClipValue2, 0.0F);
      keys8.getTree(knifeClipValue2 - 0.035F, 1.0F).getTree(knifeClipValue2, 0.0F);
      keys7.getTree(knifeClipValue2, 0.0F);
      keys14.getTree(knifeClipValue2, 0.0F);
      keys.handleTree(knifeInspectClip, 0, knifeClipValue2);
      keys2.handleTree(knifeInspectClip, 1, knifeClipValue2);
      keys3.handleTree(knifeInspectClip, 2, knifeClipValue2);
      keys4.handleTree(knifeInspectClip, 3, knifeClipValue2);
      keys5.handleTree(knifeInspectClip, 4, knifeClipValue2);
      keys6.handleTree(knifeInspectClip, 5, knifeClipValue2);
      keys7.handleTree(knifeInspectClip, 6, knifeClipValue2);
      keys8.handleTree(knifeInspectClip, 7, knifeClipValue2);
      keys9.handleTree(knifeInspectClip, 8, knifeClipValue2);
      keys10.handleTree(knifeInspectClip, 9, knifeClipValue2);
      keys11.handleTree(knifeInspectClip, 10, knifeClipValue2);
      keys12.handleTree(knifeInspectClip, 11, knifeClipValue2);
      keys13.handleTree(knifeInspectClip, 12, knifeClipValue2);
      keys14.handleTree(knifeInspectClip, 13, knifeClipValue2);
      keys15.handleTree(knifeInspectClip, 14, knifeClipValue2);
   }

   private static final class Keys {
      final TreeMap<Float, Float> treeMap = new TreeMap<>();

      KnifeClipStiletto.Keys getTree(float treeValue, float treeValue2) {
         if (this.treeMap.put(treeValue, treeValue2) != null) {
            throw new IllegalStateException("two keys at t=" + treeValue);
         } else {
            return this;
         }
      }

      void handleTree(KnifeInspectClip knifeInspectClip, int treeValue, float treeDivisor) {
         float[] floats = new float[this.treeMap.size() * 2];
         int treeCounter = 0;

         for (Entry entry : this.treeMap.entrySet()) {
            floats[treeCounter++] = (Float)entry.getKey() / treeDivisor;
            floats[treeCounter++] = (Float)entry.getValue();
         }

         knifeInspectClip.getTracks(treeValue, floats);
      }
   }
}
