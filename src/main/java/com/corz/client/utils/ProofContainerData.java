package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public record ProofContainerData(long container, long stand, int interactionFace, double hitX, double hitY, double hitZ, String routeProof, String screenType) {
   private static final int[][] values = new int[][]{{0, -1, 2}, {0, 1, 3}, {-1, 0, 4}, {1, 0, 5}};

   public static double[] getAmounts(long container, int face) {
      double proofContainerTotal = ProofSupportUtils.getValueInt(container) + 0.5;
      double proofContainerValue = ProofSupportUtils.getValue(container) + 0.45;
      double proofContainerTotal2 = ProofSupportUtils.getValueForLong(container) + 0.5;
      switch (face) {
         case 1:
            proofContainerValue = ProofSupportUtils.getValue(container) + 0.85;
            break;
         case 2:
            proofContainerTotal2 -= 0.42;
            break;
         case 3:
            proofContainerTotal2 += 0.42;
            break;
         case 4:
            proofContainerTotal -= 0.42;
            break;
         case 5:
            proofContainerTotal += 0.42;
      }

      return new double[]{proofContainerTotal, proofContainerValue, proofContainerTotal2};
   }

   public static ProofContainerData getProofContainerData(long stand, long routeProof, int container, String screenType, String face) {
      double[] proofContainerValues = getAmounts(stand, container);
      return new ProofContainerData(stand, routeProof, container, proofContainerValues[0], proofContainerValues[1], proofContainerValues[2], screenType, face);
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "container=%s stand=%s interactionFace=%d hitVec=%.2f,%.2f,%.2f routeProof=%s screenType=%s",
         ProofSupportUtils.formatText(this.container),
         ProofSupportUtils.formatText(this.stand),
         this.interactionFace,
         this.hitX,
         this.hitY,
         this.hitZ,
         this.routeProof,
         this.screenType
      );
   }

   public static List<long[]> getItems(long frontFaceOrdinal, Integer container, ProofContainerData.Geometry g) {
      int proofContainerValue = ProofSupportUtils.getValueInt(frontFaceOrdinal);
      int builderAutoValue = ProofSupportUtils.getValue(frontFaceOrdinal);
      int valueForLong = ProofSupportUtils.getValueForLong(frontFaceOrdinal);
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();

      for (int[] proofContainerValues : values) {
         if (container != null && proofContainerValues[2] == container) {
            arrayList2.add(proofContainerValues);
         }
      }

      for (int[] totalPriceData : values) {
         if (container == null || totalPriceData[2] != container) {
            arrayList2.add(totalPriceData);
         }
      }

      for (int proofContainerValue4 : new int[]{0, -1, 1}) {
         for (int[] proofContainerValues3 : (Iterable<int[]>) (arrayList2)) {
            long collisionValue = ProofSupportUtils.getTime(proofContainerValue + proofContainerValues3[0], builderAutoValue, valueForLong + proofContainerValues3[1]);
            if (!g.hasCollisionShapeAt(collisionValue)) {
               long autoBuilderValue = ProofSupportUtils.getTime(proofContainerValue + proofContainerValues3[0], builderAutoValue + proofContainerValue4, valueForLong + proofContainerValues3[1]);
               if (g.isOpenUnbuiltCell(autoBuilderValue)) {
                  arrayList.add(new long[]{autoBuilderValue, proofContainerValues3[2]});
               }
            }
         }
      }

      if (!g.hasCollisionShapeAt(ProofSupportUtils.getTime(proofContainerValue, builderAutoValue + 1, valueForLong))) {
         for (int[] proofContainerValues4 : values) {
            long time7 = ProofSupportUtils.getTime(proofContainerValue + proofContainerValues4[0], builderAutoValue + 1, valueForLong + proofContainerValues4[1]);
            if (g.isOpenUnbuiltCell(time7)) {
               boolean proofContainerFlag = false;

               for (long[] proofContainerValues5 : (Iterable<long[]>) (arrayList)) {
                  if (proofContainerValues5[0] == time7) {
                     proofContainerFlag = true;
                     break;
                  }
               }

               if (!proofContainerFlag) {
                  arrayList.add(new long[]{time7, 1L});
               }
            }
         }
      }

      for (int i = -1; i <= 1; i += 2) {
         for (int j = -1; j <= 1; j += 2) {
            long time8 = ProofSupportUtils.getTime(proofContainerValue + i, builderAutoValue, valueForLong + j);
            if (g.isOpenUnbuiltCell(time8)) {
               int proofContainerValue9 = i < 0 ? 4 : 5;
               int proofContainerValue10 = j < 0 ? 2 : 3;
               boolean collisionShapeAt2 = !g.hasCollisionShapeAt(ProofSupportUtils.getTime(proofContainerValue + i, builderAutoValue, valueForLong));
               boolean collisionShapeAt3 = !g.hasCollisionShapeAt(ProofSupportUtils.getTime(proofContainerValue, builderAutoValue, valueForLong + j));
               if (collisionShapeAt2) {
                  arrayList.add(new long[]{time8, proofContainerValue9});
               } else if (collisionShapeAt3) {
                  arrayList.add(new long[]{time8, proofContainerValue10});
               }
            }
         }
      }

      return arrayList;
   }

   public static boolean canFlag(boolean chestLike, boolean aboveSolid) {
      return !chestLike || !aboveSolid;
   }

   public interface Geometry {
      boolean isOpenUnbuiltCell(long autoBuilderValue);

      boolean hasCollisionShapeAt(long collisionValue);
   }
}
