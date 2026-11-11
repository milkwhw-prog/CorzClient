package com.corz.client.utils;
import com.corz.client.utils.FingerprintPlacements;

public sealed interface ProvenAdmittedHandler<P> permits ProvenAdmittedHandler.Admitted, ProvenAdmittedHandler.NotProven, ProvenAdmittedHandler.Rejected {
   static <P> ProvenAdmittedHandler<P> getProvenAdmittedHandler(
      P p, FingerprintPlacements.Reject reject, int provenAdmittedValue, boolean provenAdmittedFlag, String provenAdmittedText
   ) {
      return new ProvenAdmittedHandler.NotProven<>((P)p, reject, provenAdmittedValue, provenAdmittedFlag, provenAdmittedText);
   }

   default boolean isFlag() {
      return !(this instanceof ProvenAdmittedHandler.Rejected);
   }

   boolean dependencyChainProven();

   default String formatAdmittedRejected() {
      if (this instanceof ProvenAdmittedHandler.Admitted) {
         return "ADMITTED";
      } else {
         return this instanceof ProvenAdmittedHandler.NotProven ? "NOT_PROVEN" : "REJECTED";
      }
   }

   default boolean isFlag2() {
      return this instanceof ProvenAdmittedHandler.Admitted;
   }

   static <P> ProvenAdmittedHandler<P> getProvenAdmittedHandler(P p, int provenAdmittedValue, boolean provenAdmittedFlag) {
      return new ProvenAdmittedHandler.Admitted<>((P)p, provenAdmittedValue, provenAdmittedFlag);
   }

   default P plan() {
      if (this instanceof ProvenAdmittedHandler.Admitted admitted) {
         return (P)admitted.plan();
      } else {
         return (P)(this instanceof ProvenAdmittedHandler.NotProven notProven ? notProven.plan() : null);
      }
   }

   default FingerprintPlacements.Reject reason() {
      if (this instanceof ProvenAdmittedHandler.Rejected rejected) {
         return rejected.reason();
      } else {
         return this instanceof ProvenAdmittedHandler.NotProven notProven ? notProven.observedReason() : FingerprintPlacements.Reject.NONE;
      }
   }

   default String formatProvenChain(String provenChainText, int provenChainValue) {
      StringBuilder append = new StringBuilder("kind=")
         .append(provenChainText)
         .append(" members=")
         .append(provenChainValue)
         .append(" result=")
         .append(this.formatAdmittedRejected())
         .append(" reason=")
         .append(this.reason())
         .append(" provenDirectionalMember=")
         .append(this.getMemberProven())
         .append(" dependencyChainProven=")
         .append(this.dependencyChainProven());
      if (this instanceof ProvenAdmittedHandler.NotProven notProven) {
         append.append(" unprovenBecause=").append(notProven.why());
      }

      return append.toString();
   }

   default int getMemberProven() {
      if (this instanceof ProvenAdmittedHandler.Admitted admitted) {
         return admitted.provenMemberIndex();
      } else {
         return this instanceof ProvenAdmittedHandler.NotProven notProven ? notProven.provenMemberIndex() : ((ProvenAdmittedHandler.Rejected)this).provenMemberIndex();
      }
   }

   static <P> ProvenAdmittedHandler<P> getProvenAdmittedHandler(FingerprintPlacements.Reject reject, int provenAdmittedValue, boolean provenAdmittedFlag) {
      return new ProvenAdmittedHandler.Rejected<>(reject, provenAdmittedValue, provenAdmittedFlag);
   }

   public record Admitted<P>(P plan, int provenMemberIndex, boolean dependencyChainProven) implements ProvenAdmittedHandler<P> {
   }

   public record NotProven<P>(P plan, FingerprintPlacements.Reject observedReason, int provenMemberIndex, boolean dependencyChainProven, String why)
      implements ProvenAdmittedHandler<P> {
   }

   public record Rejected<P>(FingerprintPlacements.Reject reason, int provenMemberIndex, boolean dependencyChainProven) implements ProvenAdmittedHandler<P> {
   }
}
