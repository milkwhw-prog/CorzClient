package com.corz.client.utils;

import java.util.Collection;
import com.corz.client.utils.DimJsonData;

public final class PlacementClaimResolver {
   public static PlacementClaimResolver.Claim pickPreferredClaim(Collection<PlacementClaimResolver.Claim> collection) {
      PlacementClaimResolver.Claim next = null;
      if (collection != null) {
         for (PlacementClaimResolver.Claim claim : collection) {
            if (isPreferredClaim(next, claim)) {
               next = claim;
            }
         }
      }

      return next;
   }

   public static String describeAgreedPlacement(PlacementClaimResolver.Claim claim, String agreedPlacementText) {
      if (claim == null) {
         return "no placement agreed yet";
      } else {
         return claim.hostIntent()
            ? "using " + agreedPlacementText + "'s placement (they are hosting)"
            : "no host - using " + agreedPlacementText + "'s placement";
      }
   }

   private PlacementClaimResolver() {
   }

   public static boolean isPreferredClaim(PlacementClaimResolver.Claim claim, PlacementClaimResolver.Claim claim2) {
      if (claim2 == null || !claim2.hasValidAnchor()) {
         return false;
      } else if (claim != null && claim.hasValidAnchor()) {
         return claim2.hostIntent() != claim.hostIntent() ? claim2.hostIntent() : claim2.memberId().compareTo(claim.memberId()) < 0;
      } else {
         return true;
      }
   }

   public record Claim(String memberId, DimJsonData anchor, boolean hostIntent) {
      public boolean hasValidAnchor() {
         return this.memberId != null && !this.memberId.isEmpty() && this.anchor != null && this.anchor.isDimensionPlacement();
      }
   }
}
