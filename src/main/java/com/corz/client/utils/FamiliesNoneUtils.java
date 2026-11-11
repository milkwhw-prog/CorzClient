package com.corz.client.utils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import com.corz.client.utils.BlockStateUtilsNodes;

public final class FamiliesNoneUtils {
   public static List<FamiliesNoneUtils.Family> getTemporaryAlternate(FamiliesNoneUtils.Reason reason) {
      ArrayList arrayList = new ArrayList();
      arrayList.add(FamiliesNoneUtils.Family.SAFE_DIRECT_STAND);
      arrayList.add(FamiliesNoneUtils.Family.ALTERNATE_SUPPORT_FACE);
      arrayList.add(FamiliesNoneUtils.Family.SHORT_RELOCATION);
      if (reason == FamiliesNoneUtils.Reason.FLUID_PLAYER_HAZARD_ALL_STANDS) {
         arrayList.add(FamiliesNoneUtils.Family.TEMPORARY_CONTAINMENT);
      }

      arrayList.add(FamiliesNoneUtils.Family.TEMPORARY_ACCESS);
      return arrayList;
   }

   public static boolean isReasonFamilySetBlocked(FamiliesNoneUtils.Reason reason, Set<FamiliesNoneUtils.Family> set) {
      return set != null && set.containsAll(getTemporaryAlternate(reason));
   }

   public static Set<FamiliesNoneUtils.Family> getEmptyFamilySet() {
      return EnumSet.noneOf(FamiliesNoneUtils.Family.class);
   }

   public static String formatUnlocksReason(
      FamiliesNoneUtils.Request request,
      Set<FamiliesNoneUtils.Family> set,
      FamiliesNoneUtils.Family family,
      String unlocksReasonText,
      FamiliesNoneUtils.Outcome outcome
   ) {
      return String.format(
         Locale.ROOT,
         "reason=%s targets=%d minUnlocks=%d tempBudget=%d candidateFamilies=%s selectedFamily=%s firstAction=%s result=%s",
         request.reason(),
         request.blocked().size(),
         request.toAccessRequest().minUnlocks(),
         request.tempBudget(),
         getTemporaryAlternate(request.reason()),
         family == null ? "none" : family,
         unlocksReasonText == null ? "none" : unlocksReasonText,
         outcome
      );
   }

   public static boolean canHazardStands(FamiliesNoneUtils.Request request, long hazardStandsValue) {
      return !request.hazardStands().contains(hazardStandsValue);
   }

   private FamiliesNoneUtils() {
   }

   public static FamiliesNoneUtils.Outcome getSolvedImpossible(FamiliesNoneUtils.Reason reason, Set<FamiliesNoneUtils.Family> set, boolean solvedImpossibleFlag) {
      if (solvedImpossibleFlag) {
         return FamiliesNoneUtils.Outcome.SOLVED;
      } else {
         return isReasonFamilySetBlocked(reason, set) ? FamiliesNoneUtils.Outcome.PROVEN_IMPOSSIBLE_UNDER_CURRENT_FINGERPRINT : FamiliesNoneUtils.Outcome.IN_PROGRESS;
      }
   }

   public static Optional<FamiliesNoneUtils.Family> getOptional(FamiliesNoneUtils.Reason reason, Set<FamiliesNoneUtils.Family> set) {
      for (FamiliesNoneUtils.Family family : getTemporaryAlternate(reason)) {
         if (set == null || !set.contains(family)) {
            return Optional.of(family);
         }
      }

      return Optional.empty();
   }

   public static enum Family {
      SAFE_DIRECT_STAND,
      ALTERNATE_SUPPORT_FACE,
      SHORT_RELOCATION,
      TEMPORARY_CONTAINMENT,
      TEMPORARY_ACCESS;
   }

   public static enum Outcome {
      SOLVED,
      IN_PROGRESS,
      PROVEN_IMPOSSIBLE_UNDER_CURRENT_FINGERPRINT;
   }

   public static enum Reason {
      MUTUAL_DEPENDENCY_CYCLE,
      NO_SAFE_DIRECT_POSE,
      FLUID_PLAYER_HAZARD_ALL_STANDS,
      LAST_DURABLE_WORKSPACE_LOST,
      SUPPORT_REQUIRES_TEMP,
      DIRECT_ACCESS_EXHAUSTED;
   }

   public record Request(FamiliesNoneUtils.Reason reason, Set<Long> blocked, int tempBudget, Set<Long> hazardStands) {
      public BlockStateUtilsNodes.AccessRequest toAccessRequest() {
         return BlockStateUtilsNodes.AccessRequest.getEscapeCompletion(this.blocked, this.tempBudget);
      }

      public boolean isBlocked() {
         return !this.blocked.isEmpty();
      }

      public static FamiliesNoneUtils.Request getHazardStands(Set<Long> blocked, Set<Long> tempBudget, int hazardStands) {
         return new FamiliesNoneUtils.Request(FamiliesNoneUtils.Reason.FLUID_PLAYER_HAZARD_ALL_STANDS, blocked, hazardStands, tempBudget);
      }

      public Request(FamiliesNoneUtils.Reason reason, Set<Long> blocked, int tempBudget, Set<Long> hazardStands) {
         blocked = blocked == null ? Set.of() : Set.copyOf(blocked);
         hazardStands = hazardStands == null ? Set.of() : Set.copyOf(hazardStands);
         tempBudget = Math.max(0, tempBudget);
         this.reason = reason;
         this.blocked = blocked;
         this.tempBudget = tempBudget;
         this.hazardStands = hazardStands;
      }
   }
}
