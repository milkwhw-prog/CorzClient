package com.corz.client.utils;

import com.corz.client.utils.C2Model.Clip;
import com.corz.client.utils.C2Model.Cue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.corz.client.utils.C2Model;

public final class C2Cues {
   static final String[] bladeDownTexts = new String[]{"up", "down", "blade2"};
   private static final Map<String, List<C2Cues.Contact>> INSPECT_BLADE_MAP = new HashMap<>();
   private static C2Model c2Model;

   static float getAmount(float c2CuesValue, float c2CuesValue2, float c2CuesFactor) {
      return c2CuesValue + (c2CuesValue2 - c2CuesValue) * c2CuesFactor;
   }

   private static void handleClackCatch(
      float[] clackCatchValues, float[] clackCatchValues2, float[] c2CuesValues3, float[] c2CuesValues2, boolean clackCatchFlag, int index, int c2CuesValue4, float clackCatchLimit, boolean c2CuesFlag2, List<C2Cues.Event> list
   ) {
      float clackCatchValue = c2CuesValues2[c2CuesValue4];
      int intersectsCenterBound = c2CuesValue4;
      int clackCatchBound = c2CuesValue4;

      for (int i = index + 1; i <= c2CuesValue4; i++) {
         if (Math.abs(c2CuesValues2[i] - clackCatchValue) <= 3.0F) {
            intersectsCenterBound = i;
            break;
         }
      }

      int peakOffset = index + 1;

      for (int j = index + 1; j <= c2CuesValue4; j++) {
         if (getAmount(clackCatchValues, c2CuesValues2, j) >= clackCatchLimit) {
            peakOffset = j;
            break;
         }
      }

      for (int k = peakOffset; k < c2CuesValue4; k++) {
         if (getAmount(clackCatchValues, c2CuesValues2, k + 1) < 0.15F * clackCatchLimit) {
            clackCatchBound = k;
            break;
         }
      }

      int min4 = Math.min(intersectsCenterBound, clackCatchBound);
      if (clackCatchFlag) {
         if (Math.abs(getAmount(clackCatchValue)) <= 4.0F) {
            list.add(new C2Cues.Event(clackCatchValues[intersectsCenterBound], C2Cues.Kind.CLACK, clackCatchLimit));
         } else if (clackCatchLimit >= 450.0F && Math.abs(getAmount(clackCatchValues2[intersectsCenterBound] - c2CuesValues3[intersectsCenterBound] - 180.0F)) <= 15.0F) {
            list.add(new C2Cues.Event(clackCatchValues[intersectsCenterBound], C2Cues.Kind.STRIKE, clackCatchLimit));
         } else if (clackCatchLimit >= 450.0F && c2CuesFlag2) {
            list.add(new C2Cues.Event(clackCatchValues[min4], C2Cues.Kind.CATCH, clackCatchLimit));
         }

         if (clackCatchLimit >= 450.0F && Math.abs(clackCatchValue - c2CuesValues2[index]) >= 90.0F) {
            for (int m = index + 1; m <= c2CuesValue4; m++) {
               if (getAmount(clackCatchValues, c2CuesValues2, m) >= 450.0F) {
                  list.add(new C2Cues.Event(clackCatchValues[m - 1], C2Cues.Kind.FLIP, clackCatchLimit));
                  break;
               }
            }
         }
      } else if (clackCatchLimit >= 450.0F) {
         list.add(new C2Cues.Event(clackCatchValues[min4], C2Cues.Kind.RING, clackCatchLimit));
      }
   }

   public static float[] getTracksLength(C2Model c2Model, Clip clip) {
      var arrayList = new ArrayList<Float>();

      for (String tracksLengthText : bladeDownTexts) {
         int bonesMesh = c2Model.getBonesMesh(tracksLengthText);
         if (bonesMesh >= 0 && bonesMesh < clip.tracks().length && clip.tracks()[bonesMesh][1] != null) {
            for (float tracksLengthLimit : clip.tracks()[bonesMesh][1].getArrayTime()) {
               if (tracksLengthLimit >= 0.0F && tracksLengthLimit <= clip.length()) {
                  arrayList.add(tracksLengthLimit);
               }
            }
         }
      }

      arrayList.add(0.0F);
      arrayList.add(clip.length());
      arrayList.sort(Float::compare);
      var arrayList2 = new ArrayList();

      for (float tracksLengthValue : (Iterable<Float>) (arrayList)) {
         if (arrayList2.isEmpty() || tracksLengthValue - (Float)arrayList2.get(arrayList2.size() - 1) > 1.0E-4F) {
            arrayList2.add(tracksLengthValue);
         }
      }

      float[] floats = new float[arrayList2.size()];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = (Float)arrayList2.get(i);
      }

      return floats;
   }

   public static List<C2Cues.Contact> getKindTime(List<C2Cues.Event> list, float kindValue) {
      var arrayList = new ArrayList();

      for (C2Cues.Event event : list) {
         if (!(event.time() < 0.0F) && !(event.time() > kindValue - 0.06F)) {
            C2Cues.Event event2 = arrayList.isEmpty() ? null : (C2Cues.Event)arrayList.get(arrayList.size() - 1);
            if (event2 != null && event.time() - event2.time() < 0.035F) {
               C2Cues.Event event3 = event.kind().kindPriority > event2.kind().kindPriority ? event : event2;
               arrayList.set(arrayList.size() - 1, new C2Cues.Event(event3.time(), event3.kind(), Math.max(event.speed(), event2.speed())));
            } else {
               arrayList.add(event);
            }
         }
      }

      var arrayList2 = new ArrayList();
      var iterator = arrayList.iterator();

      while (true) {
         C2Cues.Event totalPriceData;
         boolean kindFlag;
         do {
            if (!iterator.hasNext()) {
               while (arrayList2.size() > 16) {
                  C2Cues.Event event5 = null;

                  for (C2Cues.Event event6 : (Iterable<com.corz.client.utils.C2Cues.Event>) (arrayList2)) {
                     if (event5 == null || getSpeedKind(event6) < getSpeedKind(event5)) {
                        event5 = event6;
                     }
                  }

                  arrayList2.remove(event5);
               }

               var arrayList3 = new ArrayList(arrayList2.size());

               for (C2Cues.Event event7 : (Iterable<com.corz.client.utils.C2Cues.Event>) (arrayList2)) {
                  arrayList3.add(getKindSpeed(event7));
               }

               return List.copyOf(arrayList3);
            }

            totalPriceData = (C2Cues.Event)iterator.next();
            if (totalPriceData.kind() != C2Cues.Kind.FLIP) {
               break;
            }

            kindFlag = false;

            for (C2Cues.Event event8 : (Iterable<com.corz.client.utils.C2Cues.Event>) (arrayList)) {
               if (event8.kind() != C2Cues.Kind.FLIP) {
                  float deltaTime = totalPriceData.time() - event8.time();
                  if (deltaTime >= 0.0F && deltaTime < 0.07F) {
                     kindFlag = true;
                  }
               }
            }
         } while (kindFlag);

         C2Cues.Event event9 = arrayList2.isEmpty() ? null : (C2Cues.Event)arrayList2.get(arrayList2.size() - 1);
         if (event9 == null || !(totalPriceData.time() - event9.time() < 0.04F)) {
            arrayList2.add(totalPriceData);
         } else if (totalPriceData.kind().kindPriority > event9.kind().kindPriority) {
            arrayList2.set(arrayList2.size() - 1, totalPriceData);
         }
      }
   }

   static float getAmount(float[] c2CuesValues, float[] c2CuesValues2, int index) {
      float c2CuesDivisor = c2CuesValues[index] - c2CuesValues[index - 1];
      return c2CuesDivisor <= 0.0F ? 0.0F : Math.abs(c2CuesValues2[index] - c2CuesValues2[index - 1]) / c2CuesDivisor;
   }

   public static C2Cues.Contact getKindSpeed(C2Cues.Event event) {
      float clamp = Math.clamp((event.speed() - 450.0F) / 1950.0F, 0.0F, 1.0F);

      float kindSpeedValue = switch (event.kind()) {
         case FLIP -> getAmount(0.3F, 0.46F, clamp);
         case CATCH -> getAmount(0.24F, 0.34F, clamp);
         case RING -> getAmount(0.3F, 0.48F, clamp);
         case STRIKE -> getAmount(0.4F, 0.62F, clamp * 0.85F);
         case CLACK -> getAmount(0.4F, 0.62F, Math.clamp((event.speed() - 300.0F) / 2100.0F, 0.0F, 1.0F));
         case LATCH -> 0.46F;
      };

      float kindSpeedValue2 = switch (event.kind()) {
         case CATCH -> getAmount(0.94F, 1.0F, clamp);
         case LATCH -> 1.0F;
         default -> getAmount(0.97F, 1.04F, clamp);
      };
      return new C2Cues.Contact(event.time(), event.kind().kindSpeedText, kindSpeedValue, kindSpeedValue2);
   }

   static float getAmount(float c2CuesValue) {
      float c2CuesTotal = c2CuesValue % 360.0F;
      if (c2CuesTotal > 180.0F) {
         c2CuesTotal -= 360.0F;
      }

      if (c2CuesTotal < -180.0F) {
         c2CuesTotal += 360.0F;
      }

      return c2CuesTotal;
   }

   static List<C2Cues.Contact> getCuesLength(C2Model c2Model, Clip clip) {
      List bladeDown = getBladeDown(c2Model, clip);
      if (bladeDown.isEmpty() && !clip.cues().isEmpty()) {
         bladeDown = getCuesEffect(clip);
      }

      return getKindTime(bladeDown, clip.length());
   }

   static float getSpeedKind(C2Cues.Event event) {
      return event.kind().kindPriority + Math.min(1.0F, event.speed() / 2400.0F) * 0.5F;
   }

   public static List<C2Cues.Event> getBladeDown(C2Model c2Model, Clip clip) {
      float[] tracksLength = getTracksLength(c2Model, clip);
      int bonesMesh = c2Model.getBonesMesh("up");
      int bonesMesh2 = c2Model.getBonesMesh("down");
      int bonesMesh3 = c2Model.getBonesMesh("blade2");
      if (bonesMesh >= 0 && bonesMesh2 >= 0 && bonesMesh3 >= 0 && tracksLength.length >= 2) {
         float[] bonesMeshMeshModelBones = c2Model.getBonesMeshMeshModelBones();
         float[] floats = new float[tracksLength.length];
         float[] c2CuesValues3 = new float[tracksLength.length];

         for (int i = 0; i < tracksLength.length; i++) {
            Arrays.fill(bonesMeshMeshModelBones, 0.0F);
            clip.handleTracks(tracksLength[i], bonesMeshMeshModelBones);
            float bladeDownValue = bonesMeshMeshModelBones[bonesMesh * 9 + 3];
            floats[i] = bonesMeshMeshModelBones[bonesMesh2 * 9 + 3] - bladeDownValue;
            c2CuesValues3[i] = bonesMeshMeshModelBones[bonesMesh3 * 9 + 3] - bladeDownValue;
         }

         return getLatchClack(tracksLength, floats, c2CuesValues3);
      } else {
         return List.of();
      }
   }

   static C2Cues.Kind getFlipBknife(String flipBknifeText) {
      String lowerCase = flipBknifeText.toLowerCase(Locale.ROOT);
      if (lowerCase.contains("bknife") || lowerCase.contains("flip") || lowerCase.contains("rotate") || lowerCase.contains("spin")) {
         return C2Cues.Kind.FLIP;
      } else {
         return !lowerCase.contains("end") && !lowerCase.contains("close") && !lowerCase.contains("lock") && !lowerCase.endsWith("_b") ? C2Cues.Kind.CATCH : C2Cues.Kind.CLACK;
      }
   }

   public static synchronized List<C2Cues.Contact> getInspectBlade(C2Model c2Model2, Clip clip) {
      if (c2Model2 != null && clip != null && clip.name().startsWith("inspect")) {
         if (c2Model != c2Model2) {
            INSPECT_BLADE_MAP.clear();
            c2Model = c2Model2;
         }

         return INSPECT_BLADE_MAP.computeIfAbsent(clip.name(), clipx -> getCuesLength(c2Model2, clip));
      } else {
         return List.of();
      }
   }

   static List<C2Cues.Event> getCuesEffect(Clip clip) {
      var arrayList = new ArrayList();
      float cuesEffectValue = 1230.0F;

      for (Cue cue : clip.cues()) {
         arrayList.add(new C2Cues.Event(cue.time(), getFlipBknife(cue.effect()), cuesEffectValue));
      }

      arrayList.sort(Comparator.comparingDouble(C2Cues.Event::time));
      return arrayList;
   }

   public static List<C2Cues.Event> getLatchClack(float[] latchClackValues, float[] c2CuesValues2, float[] c2CuesValues3) {
      var arrayList = new ArrayList();
      handle(latchClackValues, c2CuesValues2, c2CuesValues3, true, arrayList);
      handle(latchClackValues, c2CuesValues2, c2CuesValues3, false, arrayList);
      arrayList.sort(Comparator.comparingDouble(C2Cues.Event::time));
      int latchClackLimit = -1;

      for (int i = 0; i < arrayList.size(); i++) {
         if (((C2Cues.Event)arrayList.get(i)).kind() == C2Cues.Kind.CLACK) {
            latchClackLimit = i;
         }
      }

      if (latchClackLimit >= 0) {
         boolean latchClackFlag = false;

         for (int j = latchClackLimit + 1; j < arrayList.size(); j++) {
            if (((C2Cues.Event)arrayList.get(j)).kind() != C2Cues.Kind.FLIP) {
               latchClackFlag = true;
            }
         }

         C2Cues.Event event = (C2Cues.Event)arrayList.get(latchClackLimit);
         if (!latchClackFlag) {
            arrayList.set(latchClackLimit, new C2Cues.Event(event.time(), C2Cues.Kind.LATCH, event.speed()));
         }
      }

      return arrayList;
   }

   private C2Cues() {
   }

   private static void handle(float[] c2CuesValues, float[] c2CuesValues2, float[] c2CuesValues3, boolean c2CuesFlag, List<C2Cues.Event> list) {
      float[] c2CuesValues4 = c2CuesFlag ? c2CuesValues2 : c2CuesValues3;
      int c2CuesLimit = c2CuesValues.length;
      int index = 1;

      while (index < c2CuesLimit) {
         float c2CuesValue = c2CuesValues4[index] - c2CuesValues4[index - 1];
         float c2CuesValue2 = getAmount(c2CuesValues, c2CuesValues4, index);
         if (c2CuesValue2 < 30.0F) {
            index++;
         } else {
            float signum = Math.signum(c2CuesValue);
            float c2CuesBound = 0.0F;
            int c2CuesValue3 = index - 1;
            int c2CuesValue4 = c2CuesValue3;

            for (int i = index; i < c2CuesLimit; c2CuesValue4 = i++) {
               float c2CuesValue5 = c2CuesValues4[i] - c2CuesValues4[i - 1];
               float c2CuesBound2 = getAmount(c2CuesValues, c2CuesValues4, i);
               if (Math.signum(c2CuesValue5) != signum || c2CuesBound2 < 60.0F) {
                  break;
               }

               c2CuesBound = Math.max(c2CuesBound, c2CuesBound2);
            }

            if (c2CuesValue4 == c2CuesValue3) {
               index++;
            } else {
               boolean c2CuesFlag2 = c2CuesValue4 + 1 < c2CuesLimit && getAmount(c2CuesValues, c2CuesValues4, c2CuesValue4 + 1) < 60.0F;
               if (c2CuesBound >= 300.0F) {
                  handleClackCatch(c2CuesValues, c2CuesValues2, c2CuesValues3, c2CuesValues4, c2CuesFlag, c2CuesValue3, c2CuesValue4, c2CuesBound, c2CuesFlag2, list);
               }

               index = c2CuesValue4 + 1;
            }
         }
      }
   }

   public record Contact(float time, String sound, float volume, float pitch) {
   }

   public record Event(float time, C2Cues.Kind kind, float speed) {
   }

   public static enum Kind {
      FLIP(1, "keys"),
      CATCH(2, "tick"),
      RING(3, "ring"),
      STRIKE(4, "keys_land"),
      CLACK(5, "keys_land"),
      LATCH(6, "lock");
      public final int kindPriority;
      public final String kindSpeedText;

      private Kind(int value, String text) {
         this.kindPriority = value;
         this.kindSpeedText = text;
      }
   }
}
