package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.KnifeFinishBake;
import com.corz.client.utils.KnifeFinishReference;
import com.corz.client.utils.KnifeFinishGems;
import com.corz.client.utils.KnifeFinishFades;
import com.corz.client.utils.KnifeFinishSteels;
import com.corz.client.utils.KnifeFinishPaints;

public final class KnifeFinish {
   static final byte CELL_EMPTY = 0;
   public static final int CELL_FILLED = 1;
   public static final String[] fittingHandleTexts = new String[]{"blade", "edge", "spine", "handle", "handle-inner", "accent", "fitting", "pin"};
   public static final int fadeSapphireValue = getValue(0) | getValue(1) | getValue(2);
   public static final int fittingValue = getValue(3) | getValue(4) | getValue(5);
   private static volatile Map<String, KnifeFinish.Finish> paintsSteelsFinishMap;
   public static final String VANILLA = "Vanilla";
   private static volatile String[] paintsSteelsTexts;
   private static volatile List<KnifeFinish.Finish> unmodifiableList;
   public static final int PAINT_STEEL_MASK = getValue(6) | getValue(7);
   public static final int painterValue = fadeSapphireValue | fittingValue | PAINT_STEEL_MASK;
   static final byte CELL_EDGE = 2;
   public static final int[] knifeFinishTable = new int[]{
      12239049,
      14081252,
      15922682,
      8423057,
      10660018,
      6054251,
      2500395,
      3684928,
      1579292,
      5126188,
      7361602,
      13150308,
      5527650,
      11054778,
      14607081,
      13938522,
      8097188
   };

   public static int getFittingSapphire(int fittingSapphireValue) {
      int knifeFinishValue = fittingSapphireValue & 0xFF;
      return (fadeSapphireValue & getValue(knifeFinishValue)) != 0 ? fadeSapphireValue : ((fittingValue & getValue(knifeFinishValue)) != 0 ? fittingValue : PAINT_STEEL_MASK);
   }

   public static int getValue(int knifeFinishValue, float knifeFinishLimit, float knifeFinishLimit2) {
      int knifeFinishValue2 = knifeFinishValue & 16777215;

      for (int i = 0; i < knifeFinishTable.length; i++) {
         if (knifeFinishTable[i] == knifeFinishValue2) {
            return i;
         }
      }

      if (knifeFinishLimit >= 0.5F && knifeFinishLimit2 >= 0.5F) {
         return 9;
      } else {
         int knifeFinishValue3 = 0;
         long maxValue = Long.MAX_VALUE;

         for (int j = 0; j < 16; j++) {
            int knifeFinishFactor = (knifeFinishTable[j] >> 16 & 0xFF) - (knifeFinishValue2 >> 16 & 0xFF);
            int knifeFinishFactor2 = (knifeFinishTable[j] >> 8 & 0xFF) - (knifeFinishValue2 >> 8 & 0xFF);
            int knifeFinishFactor3 = (knifeFinishTable[j] & 0xFF) - (knifeFinishValue2 & 0xFF);
            long knifeFinishValue4 = (long)knifeFinishFactor * knifeFinishFactor + (long)knifeFinishFactor2 * knifeFinishFactor2 + (long)knifeFinishFactor3 * knifeFinishFactor3;
            if (knifeFinishValue4 < maxValue) {
               maxValue = knifeFinishValue4;
               knifeFinishValue3 = j;
            }
         }

         return knifeFinishValue3;
      }
   }

   public static float[] getClone(KnifeRig knifeRig) {
      KnifeFinishBake.Geometry geometry = KnifeFinishBake.getGeometry(knifeRig);
      return geometry == null ? null : (float[])geometry.cloneTable.clone();
   }

   public static int getValue(int knifeFinishValue) {
      return 1 << knifeFinishValue;
   }

   public static KnifeFinish.Finish getFinish(String knifeFinishText) {
      getPaintsSteels();
      return knifeFinishText == null ? null : paintsSteelsFinishMap.get(knifeFinishText);
   }

   private static void handleErr(List<KnifeFinish.Finish> list, String errText, Supplier<List<KnifeFinish.Finish>> supplier) {
      try {
         var wordListed = (List)supplier.get();
         if (wordListed != null) {
            list.addAll(wordListed);
         }
      } catch (Throwable e) {
         System.err.println("[C" + errText + " failed to load: " + e);
      }
   }

   public static String[] getClone() {
      getPaintsSteels();
      return (String[])paintsSteelsTexts.clone();
   }

   public static int getInstance(KnifeRig knifeRig, int knifeFinishValue, int knifeFinishValue2, float knifeFinishValue3, float knifeFinishLimit, float knifeFinishValue4, float knifeFinishValue5, float knifeFinishValue6, float knifeFinishFactor) {
      float abs2 = Math.abs(knifeFinishFactor);

      return switch (knifeFinishValue2) {
         case 0, 1 -> 0;
         case 2 -> 1;
         case 3 -> abs2 > 0.85F ? 256 : 2;
         case 4 -> knifeRig == KnifeRig.M9_BAYONET_TP && knifeFinishLimit > 6.26F
            ? 0
            : (knifeRig == KnifeRig.STILETTO_TP && knifeFinishValue == 0 && abs2 < 0.3F && knifeFinishLimit > -3.05F && knifeFinishLimit < 5.0F ? (Math.abs(knifeFinishValue4 - 8.0F) < 0.3F ? 4 : 5) : 6);
         default -> 6;
         case 6, 9, 10 -> 3;
         case 7 -> knifeRig == KnifeRig.M9_BAYONET_TP ? 5 : 3;
         case 12 -> (knifeFinishValue4 - 8.0F) * knifeFinishFactor < 0.0F ? 4 : 3;
         case 13 -> 5;
         case 14 -> 7;
         case 16 -> abs2 > 0.5F ? 4 : 5;
      };
   }

   public static List<KnifeFinish.Finish> getPaintsSteels() {
      var unmodifiableList2 = unmodifiableList;
      if (unmodifiableList2 != null) {
         return unmodifiableList2;
      } else {
         synchronized (KnifeFinish.class) {
            if (unmodifiableList != null) {
               return unmodifiableList;
            } else {
               var arrayList = new ArrayList();
               arrayList.add(getBuilder("Vanilla").build());
               handleErr(arrayList, "Reference", KnifeFinishReference::getItems);
               handleErr(arrayList, "Gems", KnifeFinishGems::getItems);
               handleErr(arrayList, "Fades", KnifeFinishFades::getItems);
               handleErr(arrayList, "Steels", KnifeFinishSteels::getItems);
               handleErr(arrayList, "Paints", KnifeFinishPaints::getItems);
               var hashMap = new HashMap();
               var arrayList2 = new ArrayList();

               for (KnifeFinish.Finish finish : (Iterable<com.corz.client.utils.KnifeFinish.Finish>) (arrayList)) {
                  if (finish != null && finish.paintsSteelsValue < 0 && hashMap.putIfAbsent(finish.corzclientLoadingText, finish) == null) {
                     finish.paintsSteelsValue = arrayList2.size();
                     arrayList2.add(finish);
                  }
               }

               String[] texts2 = new String[arrayList2.size()];

               for (int i = 0; i < texts2.length; i++) {
                  texts2[i] = ((KnifeFinish.Finish)arrayList2.get(i)).corzclientLoadingText;
               }

               paintsSteelsFinishMap = hashMap;
               paintsSteelsTexts = texts2;
               unmodifiableList = Collections.unmodifiableList(arrayList2);
               return unmodifiableList;
            }
         }
      }
   }

   public static KnifeFinish.Builder getBuilder(String builderText) {
      return new KnifeFinish.Builder(builderText);
   }

   public static int[] getValues(KnifeRig knifeRig, KnifeFinish.Finish finish) {
      KnifeFinishBake.Layout compact = KnifeFinishBake.getCompact(knifeRig, finish);
      return compact == null ? null : new int[]{compact.compactValue, compact.atlasHeight, compact.compactAtlasWidth, compact.meshsFinishValue};
   }

   public static KnifeFinish.Baked getBaked(KnifeRig knifeRig, KnifeFinish.Finish finish) {
      return KnifeFinishBake.getMeshsFinish(knifeRig, finish);
   }

   public static int[] getValues(KnifeRig knifeRig, KnifeFinish.Finish finish, int knifeFinishValue, int compactLimit) {
      KnifeFinishBake.Layout layout = KnifeFinishBake.getLayout(knifeRig, finish, knifeFinishValue, compactLimit);
      return layout == null ? null : KnifeFinishBake.getAmount(knifeRig, finish, layout, false);
   }

   public static final class Baked {
      public final int textureWidth;
      public final KnifeFinish.Skin[] skins;
      public final KnifeRig knifeRig;
      public final int corzclientFinishValue;
      public final int bakedValue;
      public final int[][] corzclientFinishInts;
      public final KnifeFinish.Finish finish;
      public final int[][] bakedInts;
      public final int textureHeight;

      public boolean isFlag() {
         return this.bakedInts != null;
      }

      Baked(KnifeRig knifeRig, KnifeFinish.Finish finish2, int value, int value2, int value3, int value4, int[][] values, int[][] values2, KnifeFinish.Skin[] skins) {
         this.knifeRig = knifeRig;
         this.finish = finish2;
         this.textureWidth = value;
         this.textureHeight = value2;
         this.bakedValue = value3;
         this.corzclientFinishValue = value4;
         this.corzclientFinishInts = values;
         this.bakedInts = values2;
         this.skins = skins;
      }
   }

   public static final class Builder {
      private final String finishText;
      private final List<KnifeFinish.Page> painter;
      private final List<KnifeFinish.Page> finishItems;
      private int finishValue;
      private final int[] finishValues;
      private final int[] recolorTable;
      private final int[] builderTable;
      private final byte[] paintTable = new byte[8];

      public KnifeFinish.Builder recolor(int recolorValue, int recolorValue2) {
         for (int i = 0; i < 8; i++) {
            if ((recolorValue & KnifeFinish.getValue(i)) != 0) {
               this.paintTable[i] = 1;
               this.builderTable[i] = 0xFF000000 | recolorValue2;
               this.recolorTable[i] = -1;
            }
         }

         return this;
      }

      public KnifeFinish.Builder glow(int glowValue, KnifeFinish.Painter painter) {
         int finishitemsSize = this.finishItems.size();
         this.finishItems.add(new KnifeFinish.Page(glowValue & KnifeFinish.painterValue, painter));

         for (int i = 0; i < 8; i++) {
            if ((glowValue & KnifeFinish.getValue(i)) != 0) {
               this.finishValues[i] = painter == null ? -1 : finishitemsSize;
            }
         }

         return this;
      }

      public KnifeFinish.Builder paint(int paintValue, KnifeFinish.Painter painter) {
         if (painter == null) {
            throw new IllegalArgumentException("painter");
         } else {
            int painterSize = this.painter.size();
            this.painter.add(new KnifeFinish.Page(paintValue & KnifeFinish.painterValue, painter));

            for (int i = 0; i < 8; i++) {
               if ((paintValue & KnifeFinish.getValue(i)) != 0) {
                  this.paintTable[i] = 2;
                  this.recolorTable[i] = painterSize;
               }
            }

            return this;
         }
      }

      private static KnifeFinish.Page[] compact(List<KnifeFinish.Page> list, int[] compactValues) {
         var arrayList = new ArrayList<KnifeFinish.Page>();
         int[] ints = new int[list.size()];
         Arrays.fill(ints, -1);

         for (int i = 0; i < 8; i++) {
            int index = compactValues[i];
            if (index >= 0) {
               if (ints[index] < 0) {
                  ints[index] = arrayList.size();
                  arrayList.add(new KnifeFinish.Page(0, ((KnifeFinish.Page)list.get(index)).painter()));
               }

               compactValues[i] = ints[index];
            }
         }

         for (int j = 0; j < 8; j++) {
            if (compactValues[j] >= 0) {
               KnifeFinish.Page page = (KnifeFinish.Page)arrayList.get(compactValues[j]);
               arrayList.set(compactValues[j], new KnifeFinish.Page(page.classes() | KnifeFinish.getValue(j), page.painter()));
            }
         }

         return arrayList.toArray(new KnifeFinish.Page[0]);
      }

      private Builder(String finishText) {
         this.builderTable = new int[8];
         this.recolorTable = new int[8];
         this.finishValues = new int[8];
         this.painter = new ArrayList<>();
         this.finishItems = new ArrayList<>();
         if (finishText != null && !finishText.isBlank()) {
            this.finishText = finishText;
            this.finishValue = finishText.hashCode() * -1640531535;
            Arrays.fill(this.recolorTable, -1);
            Arrays.fill(this.finishValues, -1);
         } else {
            throw new IllegalArgumentException("finish name");
         }
      }

      public KnifeFinish.Builder seed(int finishValue) {
         this.finishValue = finishValue;
         return this;
      }

      public KnifeFinish.Builder keep(int keepValue) {
         for (int i = 0; i < 8; i++) {
            if ((keepValue & KnifeFinish.getValue(i)) != 0) {
               this.paintTable[i] = 0;
               this.recolorTable[i] = -1;
            }
         }

         return this;
      }

      public KnifeFinish.Finish build() {
         int[] clone = (int[])this.recolorTable.clone();
         int[] compactValues = (int[])this.finishValues.clone();
         return new KnifeFinish.Finish(
            this.finishText,
            this.finishValue,
            (byte[])this.paintTable.clone(),
            (int[])this.builderTable.clone(),
            clone,
            compactValues,
            compact(this.painter, clone),
            compact(this.finishItems, compactValues)
         );
      }
   }

   public static final class Finish {
      final int finishSeed;
      final int[] clone;
      final KnifeFinish.Page[] compact;
      final int[] table;
      int paintsSteelsValue = -1;
      final KnifeFinish.Page[] compactPages;
      public final String corzclientLoadingText;
      final int[] cloneRemap;
      final byte[] cloneCompact;

      public boolean isCompact() {
         return this.compact.length > 0;
      }

      private Finish(String corzclientLoadingText, int value, byte[] clone, int[] clone2, int[] clone3, int[] clone4, KnifeFinish.Page[] compact, KnifeFinish.Page[] compact2) {
         this.corzclientLoadingText = corzclientLoadingText;
         this.finishSeed = value;
         this.cloneCompact = clone;
         this.table = clone2;
         this.cloneRemap = clone3;
         this.clone = clone4;
         this.compactPages = compact;
         this.compact = compact2;
      }

      @Override
      public String toString() {
         return this.corzclientLoadingText;
      }

      public boolean canFlag(int knifeFinishValue) {
         return this.cloneCompact[knifeFinishValue & 0xFF] == 2;
      }

      public int getPaintsSteelsValue() {
         return this.paintsSteelsValue;
      }

      public boolean isCompactFlag() {
         if (this.compact.length > 0) {
            return false;
         } else {
            for (byte compactValue : this.cloneCompact) {
               if (compactValue != 0) {
                  return false;
               }
            }

            return true;
         }
      }

      public boolean canFlagForInt(int knifeFinishValue) {
         return this.clone[knifeFinishValue & 0xFF] >= 0;
      }
   }

   record Page(int classes, KnifeFinish.Painter painter) {
   }

   @FunctionalInterface
   public interface Painter {
      int paint(KnifeFinish.Texel texel);
   }

   public static final class Skin {
      public final int[] skinTable;
      public final int[] lightTable;
      public final float[] table;
      public final float[] skinVertexData;

      public boolean isFlag() {
         return this.skinTable.length > 0;
      }

      Skin(float[] amounts, int[] values, int[] values2, float[] amounts2) {
         this.table = amounts;
         this.lightTable = values;
         this.skinTable = values2;
         this.skinVertexData = amounts2;
      }
   }

   public static final class Texel {
      public float texelY;
      public float rampAmount;
      public float rigAmount;
      public int texelSeed;
      public float texelAmount;
      public float edgeDistance;
      public float normalizedV;
      public float texelSize;
      public float rigKnifeAmount;
      public float outlineDistance;
      public int rigKnifeValue = -1;
      public KnifeRig knifeRig;
      public float rigEdgeDistance;
      public float glowRecolorAmount;
      public float normalizedU;
      public float fieldY;
      public float edgePosition;
      public int sheenValue = -1;
      public float fieldX;

      public Texel() {
         this.edgePosition = 0.5F;
         this.edgeDistance = 1.0E20F;
         this.outlineDistance = 1.0E20F;
         this.rigKnifeAmount = 1.0F;
      }
   }
}
