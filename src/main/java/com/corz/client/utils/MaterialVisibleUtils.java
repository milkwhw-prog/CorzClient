package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MaterialVisibleUtils {

   static int getValue(long materialVisibleValue) {
      return (int)(materialVisibleValue >> 38);
   }

   public static MaterialVisibleUtils.Plan getDataForLongInt(long materialVisibleValue, int y6, long materialVisibleValue3, List<MaterialVisibleUtils.Candidate> list) {
      return getTimeMax(materialVisibleValue, y6, materialVisibleValue3, Long.MIN_VALUE, list);
   }

   private MaterialVisibleUtils() {
   }

   public static boolean canFlag(long materialVisibleValue, long materialVisibleValue2) {
      return materialVisibleValue == materialVisibleValue2
         ? true
         : Math.abs(getValue(materialVisibleValue) - getValue(materialVisibleValue2)) <= 3 && Math.abs(getValueInt(materialVisibleValue) - getValueInt(materialVisibleValue2)) <= 3 && Math.abs(getValueForLong(materialVisibleValue) - getValueForLong(materialVisibleValue2)) <= 1;
   }

   static int getValueForLong(long materialVisibleValue) {
      return (int)(materialVisibleValue << 52 >> 52);
   }

   static int getValueInt(long materialVisibleValue) {
      return (int)(materialVisibleValue << 26 >> 38);
   }

   public static MaterialVisibleUtils.Plan getTimeMax(
      long materialVisibleValue, int y6, long materialVisibleValue3, long l8, List<MaterialVisibleUtils.Candidate> list
   ) {
      var linkedHashMap = new LinkedHashMap<Long, MaterialVisibleUtils.Group>();

      for (MaterialVisibleUtils.Candidate candidate : list) {
         if (candidate.layer() == y6) {
            linkedHashMap.computeIfAbsent(candidate.stand(), MaterialVisibleUtils.Group::new).handleMaxMaterial(candidate);
         }
      }

      MaterialVisibleUtils.Group group = null;
      double materialVisibleLimit = Double.POSITIVE_INFINITY;

      for (MaterialVisibleUtils.Group group2 : (Iterable<com.corz.client.utils.MaterialVisibleUtils.Group>) (linkedHashMap.values())) {
         int itemsSize = group2.materialCandidateList.size();
         int orElse = group2.materialIntegerMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
         double materialVisibleValue5 = group2.minRouteCost
            + group2.maxTemporaryBlocks * 18.0
            + group2.materialValue * 7.0
            + group2.totalInteractionRisk * 2.0
            + group2.materialPositionSet.size() * 12.0
            - itemsSize * 24.0
            - orElse * 12.0
            - group2.materialCount * 8.0
            - (group2.groupKey == materialVisibleValue3 ? 18.0 : 0.0);
         if (materialVisibleValue5 < materialVisibleLimit || materialVisibleValue5 == materialVisibleLimit && group2.groupKey < (group == null ? Long.MAX_VALUE : group.groupKey)) {
            group = group2;
            materialVisibleLimit = materialVisibleValue5;
         }
      }

      if (group == null) {
         return null;
      } else {
         MaterialVisibleUtils.Group group3 = group;
         var arrayList = new ArrayList<>(group.materialCandidateList);
         var arrayList2 = new ArrayList();
         var hashSet = new HashSet();
         arrayList2.add(group.groupKey);
         hashSet.add(group.groupKey);

         for (int i = 0; i < arrayList2.size() && group.materialCandidateList.size() < 32; i++) {
            long materialVisibleValue6 = (Long)arrayList2.get(i);

            for (MaterialVisibleUtils.Group group4 : (Iterable<com.corz.client.utils.MaterialVisibleUtils.Group>) (linkedHashMap.values())) {
               if (!hashSet.contains(group4.groupKey) && canFlag(group4.groupKey, materialVisibleValue6) && group.materialCandidateList.size() + group4.materialCandidateList.size() <= 32) {
                  hashSet.add(group4.groupKey);
                  arrayList2.add(group4.groupKey);
                  group.materialCandidateList.addAll(group4.materialCandidateList);
               }
            }
         }

         var hashSet2 = new HashSet();

         for (MaterialVisibleUtils.Candidate arrayItem : arrayList) {
            hashSet2.add(arrayItem.target());
         }

         group.materialCandidateList
            .sort(
               Comparator.<MaterialVisibleUtils.Candidate, Boolean>comparing(candidate3 -> !hashSet2.contains(candidate3.target()))
                  .thenComparing(candidate4 -> !candidate4.visibleNow())
                  .thenComparingInt(currentMaterialx -> currentMaterialx.material() == l8 ? 0 : (currentMaterialx.materialInHotbar() ? 1 : 2))
                  .thenComparingInt(candidate5 -> -group3.materialIntegerMap.getOrDefault(candidate5.material(), 0))
                  .thenComparingLong(MaterialVisibleUtils.Candidate::material)
                  .thenComparingInt(MaterialVisibleUtils.Candidate::interactionRisk)
                  .thenComparingInt(MaterialVisibleUtils.Candidate::order)
                  .thenComparingLong(MaterialVisibleUtils.Candidate::target)
            );
         List list2 = group.materialCandidateList.stream().map(MaterialVisibleUtils.Candidate::target).distinct().toList();
         return new MaterialVisibleUtils.Plan(
            materialVisibleValue, y6, group.groupKey, list2, materialVisibleLimit, list2.size(), group.minRouteCost, group.maxTemporaryBlocks, group.materialIntegerMap.size(), group.materialPositionSet.size()
         );
      }
   }

   public record Candidate(
      long target,
      long stand,
      int layer,
      double routeCost,
      int temporaryBlocks,
      int verticalMoves,
      int interactionRisk,
      int order,
      boolean visibleNow,
      long material,
      boolean materialInHotbar
   ) {
      public Candidate(
         long layer, long temporaryBlocks, int stand, double routeCost, int verticalMoves, int order, int visibleNow, int target, boolean interactionRisk
      ) {
         this(layer, temporaryBlocks, stand, routeCost, verticalMoves, order, visibleNow, target, interactionRisk, 0L, false);
      }
   }

   private static final class Group {
      final List<MaterialVisibleUtils.Candidate> materialCandidateList = new ArrayList<>();
      int maxTemporaryBlocks;
      final Set<Long> materialPositionSet;
      int materialValue;
      int totalInteractionRisk;
      final Map<Long, Integer> materialIntegerMap;
      final long groupKey;
      int materialCount;
      double minRouteCost = Double.POSITIVE_INFINITY;

      void handleMaxMaterial(MaterialVisibleUtils.Candidate candidate) {
         this.materialCandidateList.add(candidate);
         this.minRouteCost = Math.min(this.minRouteCost, Math.max(0.0, candidate.routeCost()));
         this.maxTemporaryBlocks = Math.max(this.maxTemporaryBlocks, Math.max(0, candidate.temporaryBlocks()));
         this.materialValue = Math.max(this.materialValue, Math.max(0, candidate.verticalMoves()));
         this.totalInteractionRisk = this.totalInteractionRisk + Math.max(0, candidate.interactionRisk());
         this.materialIntegerMap.merge(candidate.material(), 1, Integer::sum);
         if (candidate.materialInHotbar()) {
            this.materialCount++;
         } else {
            this.materialPositionSet.add(candidate.material());
         }
      }

      Group(long time) {
         this.materialIntegerMap = new LinkedHashMap<>();
         this.materialPositionSet = new LinkedHashSet<>();
         this.groupKey = time;
      }
   }

   public record Plan(
      long id,
      int layer,
      long stand,
      List<Long> targets,
      double score,
      int usefulBlocks,
      double routeCost,
      int temporaryBlocks,
      int materialBatches,
      int projectedInventorySwaps
   ) {
      public boolean isContains(long target) {
         return this.targets.contains(target);
      }
   }
}
