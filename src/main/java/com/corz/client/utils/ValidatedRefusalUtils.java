package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import com.corz.client.utils.LocalUtils;

public final class ValidatedRefusalUtils {
   public static ValidatedRefusalUtils.Result getPhantomDecisionData(
      int phantomDecisionValue,
      int localValue,
      int phantomDecisionValue3,
      boolean phantomDecisionFlag,
      int betterAlternativeLimit,
      List<ValidatedRefusalUtils.AlternativeTicket> list,
      ValidatedRefusalUtils.Validator validator
   ) {
      return getAlternativeValidated(phantomDecisionValue, localValue, phantomDecisionValue3, phantomDecisionFlag, betterAlternativeLimit, list, validator, true);
   }

   public static ValidatedRefusalUtils.Result getAlternativeValidated(
      int alternativeValidatedValue,
      int localValue,
      int alternativeValidatedValue3,
      boolean alternativeValidatedFlag,
      int betterAlternativeLimit,
      List<ValidatedRefusalUtils.AlternativeTicket> list,
      ValidatedRefusalUtils.Validator validator,
      boolean alternativeValidatedFlag2
   ) {
      ArrayList arrayList = new ArrayList(list == null ? List.of() : list);
      ArrayList arrayList2 = new ArrayList();

      while (true) {
         ArrayList arrayList3 = new ArrayList(arrayList.size());

         for (ValidatedRefusalUtils.AlternativeTicket alternativeTicket : (Iterable<com.corz.client.utils.ValidatedRefusalUtils.AlternativeTicket>) (arrayList)) {
            arrayList3.add(alternativeTicket.getAssetRegion());
         }

         LocalUtils.Decision betterAlternative = LocalUtils.getBetterAlternative(
            alternativeValidatedValue, localValue, alternativeValidatedValue3, alternativeValidatedFlag, betterAlternativeLimit, arrayList3, 0L, null, alternativeValidatedFlag2
         );
         if (betterAlternative.go()) {
            return new ValidatedRefusalUtils.Result(
               arrayList2.isEmpty() ? ValidatedRefusalUtils.Decision.ACCEPT_CURRENT : ValidatedRefusalUtils.Decision.NO_VALID_ALTERNATIVE,
               betterAlternative,
               null,
               arrayList2,
               arrayList2.isEmpty() ? betterAlternative.reason().name() : betterAlternative.reason().name() + " after " + arrayList2.size() + " phantom alternative(s)"
            );
         }

         ValidatedRefusalUtils.AlternativeTicket surroundKitType = null;

         for (ValidatedRefusalUtils.AlternativeTicket alternativeTicket3 : (Iterable<com.corz.client.utils.ValidatedRefusalUtils.AlternativeTicket>) (arrayList)) {
            if (betterAlternative.best() != null && alternativeTicket3.target() == betterAlternative.best().representative() && alternativeTicket3.kind().equals(betterAlternative.best().kind())) {
               surroundKitType = alternativeTicket3;
               break;
            }
         }

         if (surroundKitType == null) {
            return new ValidatedRefusalUtils.Result(ValidatedRefusalUtils.Decision.NO_VALID_ALTERNATIVE, betterAlternative, null, arrayList2, "refusal named no ticket");
         }

         String alternativeValidatedText = validator == null ? null : validator.apply(surroundKitType);
         if (alternativeValidatedText == null) {
            return new ValidatedRefusalUtils.Result(ValidatedRefusalUtils.Decision.REDIRECT_TO_ALTERNATIVE, betterAlternative, surroundKitType, arrayList2, "BETTER_ALTERNATIVE validated");
         }

         arrayList2.add(new ValidatedRefusalUtils.Phantom(surroundKitType, alternativeValidatedText));
         int indexOf = arrayList.indexOf(surroundKitType);
         arrayList.remove(indexOf);
         if (!surroundKitType.members().isEmpty()) {
            arrayList.add(indexOf, surroundKitType.getMemberLayers());
         }
      }
   }

   private ValidatedRefusalUtils() {
   }

   public record AlternativeTicket(
      String kind,
      long target,
      long stand,
      int layer,
      int expected,
      double cost,
      long region,
      long missionId,
      long assetId,
      String owner,
      long proofRevision,
      List<Long> members,
      List<Long> memberStands,
      List<Integer> memberLayers
   ) {
      public LocalUtils.Alternative getAssetRegion() {
         return new LocalUtils.Alternative(this.kind, this.expected, this.cost, this.target, this.owner, this.missionId, this.assetId, this.region);
      }

      public String getFormat() {
         return String.format(
            Locale.ROOT,
            "%s(target=%d stand=%d layer=%d expected=%d cost=%.0f roi=%.3f mission=%d asset=%d region=%d members=%d)",
            this.kind,
            this.target,
            this.stand,
            this.layer,
            this.expected,
            this.cost,
            this.getExpectedCost(),
            this.missionId,
            this.assetId,
            this.region,
            this.members.size()
         );
      }

      public AlternativeTicket(String cost, long target, long stand, int layer, int expected, double kind) {
         this(cost, target, stand, layer, expected, kind, 0L, 0L, 0L, "UNASSIGNED", 0L, List.of());
      }

      public ValidatedRefusalUtils.AlternativeTicket getMemberLayers() {
         if (this.members.isEmpty()) {
            return this;
         } else {
            long memberLayersValue = this.memberStands.isEmpty() ? this.stand : this.memberStands.get(0);
            int memberLayersValue2 = this.memberLayers.isEmpty() ? this.layer : this.memberLayers.get(0);
            return new ValidatedRefusalUtils.AlternativeTicket(
               this.kind,
               this.members.get(0),
               memberLayersValue,
               memberLayersValue2,
               this.expected - 1,
               this.cost,
               this.region,
               this.missionId,
               this.assetId,
               this.owner,
               this.proofRevision,
               this.members.subList(1, this.members.size()),
               this.memberStands.isEmpty() ? List.of() : this.memberStands.subList(1, this.memberStands.size()),
               this.memberLayers.isEmpty() ? List.of() : this.memberLayers.subList(1, this.memberLayers.size())
            );
         }
      }

      public double getExpectedCost() {
         return this.expected <= 0 ? 0.0 : this.expected / (1.0 + Math.max(0.0, this.cost));
      }

      public AlternativeTicket(
         String cost,
         long missionId,
         long layer,
         int target,
         int stand,
         double kind,
         long region,
         long members,
         long owner,
         String expected,
         long assetId,
         List<Long> proofRevision
      ) {
         this(cost, missionId, layer, target, stand, kind, region, members, owner, expected, assetId, proofRevision, List.of(), List.of());
      }

      public AlternativeTicket(
         String kind,
         long target,
         long stand,
         int layer,
         int expected,
         double cost,
         long region,
         long missionId,
         long assetId,
         String owner,
         long proofRevision,
         List<Long> members,
         List<Long> memberStands,
         List<Integer> memberLayers
      ) {
         kind = kind == null ? "LOCAL" : kind;
         owner = owner == null ? "UNASSIGNED" : owner;
         expected = Math.max(0, expected);
         members = members == null ? List.of() : List.copyOf(members);
         memberStands = memberStands != null && memberStands.size() == members.size() ? List.copyOf(memberStands) : List.of();
         memberLayers = memberLayers != null && memberLayers.size() == members.size() ? List.copyOf(memberLayers) : List.of();
         this.kind = kind;
         this.target = target;
         this.stand = stand;
         this.layer = layer;
         this.expected = expected;
         this.cost = cost;
         this.region = region;
         this.missionId = missionId;
         this.assetId = assetId;
         this.owner = owner;
         this.proofRevision = proofRevision;
         this.members = members;
         this.memberStands = memberStands;
         this.memberLayers = memberLayers;
      }
   }

   public static enum Decision {
      ACCEPT_CURRENT,
      REDIRECT_TO_ALTERNATIVE,
      NO_VALID_ALTERNATIVE;
   }

   public record Phantom(ValidatedRefusalUtils.AlternativeTicket ticket, String why) {
      public String formatTicketWhy() {
         return this.ticket.kind() + "@" + this.ticket.target() + ": " + this.why;
      }
   }

   public record Result(
      ValidatedRefusalUtils.Decision decision,
      LocalUtils.Decision roi,
      ValidatedRefusalUtils.AlternativeTicket alternative,
      List<ValidatedRefusalUtils.Phantom> phantoms,
      String reason
   ) {
      public String getAppend() {
         return "decision="
            + this.decision
            + " roi="
            + (this.roi == null ? "none" : this.roi.getAppend())
            + " phantomRejected="
            + this.phantoms.size()
            + (this.alternative == null ? "" : " alternative=" + this.alternative.getFormat())
            + " reason="
            + this.reason;
      }

      public boolean isDecisionRedirect() {
         return this.decision == ValidatedRefusalUtils.Decision.REDIRECT_TO_ALTERNATIVE;
      }

      public int getPhantoms() {
         return this.phantoms.size();
      }
   }

   public interface Validator extends Function<ValidatedRefusalUtils.AlternativeTicket, String> {
   }
}
