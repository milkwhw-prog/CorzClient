package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class MaterialHotbarUtils {
   private MaterialHotbarUtils() {
   }

   public static List<MaterialHotbarUtils.Candidate> getItems(List<MaterialHotbarUtils.Candidate> list) {
      ArrayList arrayList = new ArrayList(list);
      arrayList.sort(
         Comparator.<MaterialHotbarUtils.Candidate, Boolean>comparing(candidate -> !candidate.materialInHotbar())
            .thenComparingDouble(MaterialHotbarUtils.Candidate::distanceSq)
      );
      return arrayList;
   }

   public record Candidate(int index, boolean materialInHotbar, double distanceSq) {
   }

   public static final class Diag {
      public int proofsCapped;
      public int inPlan;
      public int aboveLayer;
      public int indexed;
      public int gate;
      public int rayFailed;
      public int kind;
      public int dependency;
      public int layerPolicy;

      public boolean isIndexed() {
         return this.indexed > 0;
      }

      public void updateLayerCapped() {
         this.indexed = this.aboveLayer = this.inPlan = this.kind = this.gate = this.rayFailed = this.proofsCapped = this.layerPolicy = this.dependency = 0;
      }

      public String formatLayerCapped() {
         return "indexed="
            + this.indexed
            + " aboveLayer="
            + this.aboveLayer
            + " (layerPolicy="
            + this.layerPolicy
            + " dependency="
            + this.dependency
            + ") inPlan="
            + this.inPlan
            + " kind="
            + this.kind
            + " gate="
            + this.gate
            + " rayFailed="
            + this.rayFailed
            + " proofsCapped="
            + this.proofsCapped;
      }
   }
}
