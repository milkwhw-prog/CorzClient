package com.corz.client.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class TransitionsCertificate {
   private String lastTransitionsCertificate;
   private int transitionsPassCount;
   private final LinkedHashMap<Long, LinkedHashMap<Long, TransitionsCertificate.Certificate>> componentsCertificateMap = new LinkedHashMap<Long, LinkedHashMap<Long, TransitionsCertificate.Certificate>>(
      16, 0.75F, false
   ) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, LinkedHashMap<Long, TransitionsCertificate.Certificate>> entry) {
         return this.size() > 64;
      }
   };

   public int getCertificateComponents() {
      return this.componentsCertificateMap.size();
   }

   public String getLast() {
      return this.lastTransitionsCertificate;
   }

   public Set<Long> getUnmodifiableSet(long unmodifiableValue) {
      Map componentscertificatemap = this.componentsCertificateMap.get(unmodifiableValue);
      if (componentscertificatemap == null) {
         return Set.of();
      } else {
         LinkedHashSet linkedHashSet = new LinkedHashSet();

         for (TransitionsCertificate.Certificate certificate : (Iterable<com.corz.client.utils.TransitionsCertificate.Certificate>) (componentscertificatemap.values())) {
            if (certificate.result() == TransitionsCertificate.Result.FAIL) {
               linkedHashSet.add(certificate.stand());
            }
         }

         return Collections.unmodifiableSet(linkedHashSet);
      }
   }

   private TransitionsCertificate.Certificate getRemove(TransitionsCertificate.Certificate certificate) {
      LinkedHashMap ifAbsent = this.componentsCertificateMap
         .computeIfAbsent(certificate.component(), long2 -> new LinkedHashMap<Long, TransitionsCertificate.Certificate>(16, 0.75F, false) {
            @Override
            protected boolean removeEldestEntry(Entry<Long, TransitionsCertificate.Certificate> entry) {
               return this.size() > 512;
            }
         });
      TransitionsCertificate.Certificate remove = (TransitionsCertificate.Certificate)ifAbsent.remove(certificate.stand());
      ifAbsent.put(certificate.stand(), certificate);
      return remove;
   }

   public void updateTransitionsCertificate() {
      this.componentsCertificateMap.clear();
      this.transitionsPassCount = 0;
      this.lastTransitionsCertificate = "";
   }

   public int getPassTransitions(long passTransitionsValue, long l, long l2) {
      TransitionsCertificate.Certificate remove = this.getRemove(
         new TransitionsCertificate.Certificate(passTransitionsValue, l, TransitionsCertificate.Result.PASS, l2, 0, "")
      );
      if (remove != null && remove.result() == TransitionsCertificate.Result.FAIL) {
         this.transitionsPassCount++;
         return 1;
      } else {
         return 0;
      }
   }

   public TransitionsCertificate() {
      this.lastTransitionsCertificate = "";
   }

   public TransitionsCertificate.Result getUnknownRevision(long unknownRevisionValue, long l, long l2) {
      TransitionsCertificate.Certificate certificateComponents = this.getCertificateComponents(unknownRevisionValue, l);
      return certificateComponents != null && certificateComponents.geometryRevision() == l2 ? certificateComponents.result() : TransitionsCertificate.Result.UNKNOWN;
   }

   public int getRepeats(long repeatsValue, long l, long l2, String repeatsText) {
      String last = repeatsText == null ? "" : repeatsText;
      this.lastTransitionsCertificate = last;
      TransitionsCertificate.Certificate certificateComponents = this.getCertificateComponents(repeatsValue, l);
      int repeats = certificateComponents != null && certificateComponents.result() == TransitionsCertificate.Result.FAIL ? certificateComponents.repeats() + 1 : 1;
      this.getRemove(new TransitionsCertificate.Certificate(repeatsValue, l, TransitionsCertificate.Result.FAIL, l2, repeats, last));
      return repeats;
   }

   public TransitionsCertificate.Certificate getCertificateComponents(long certificateComponentsValue, long l) {
      Map componentscertificatemap = this.componentsCertificateMap.get(l);
      return componentscertificatemap == null ? null : (TransitionsCertificate.Certificate)componentscertificatemap.get(certificateComponentsValue);
   }

   public int getRepeats(long repeatsValue, long l) {
      TransitionsCertificate.Certificate certificateComponents = this.getCertificateComponents(repeatsValue, l);
      return certificateComponents == null ? 0 : certificateComponents.repeats();
   }

   public int getCertificateCount() {
      int certificateComponents = 0;

      for (Map map : this.componentsCertificateMap.values()) {
         certificateComponents += map.size();
      }

      return certificateComponents;
   }

   public int getTransitionsPassCount() {
      return this.transitionsPassCount;
   }

   public boolean canFlag(long transitionsCertificateValue, long l) {
      TransitionsCertificate.Certificate certificateComponents = this.getCertificateComponents(transitionsCertificateValue, l);
      return certificateComponents != null && certificateComponents.result() == TransitionsCertificate.Result.FAIL;
   }

   public String getAppend() {
      int appendCounter = 0;
      int appendCounter2 = 0;

      for (Map map : this.componentsCertificateMap.values()) {
         for (TransitionsCertificate.Certificate certificate : (Iterable<com.corz.client.utils.TransitionsCertificate.Certificate>) (map.values())) {
            if (certificate.result() == TransitionsCertificate.Result.FAIL) {
               appendCounter2++;
            } else {
               appendCounter++;
            }
         }
      }

      return "RouteCertificate components="
         + this.componentsCertificateMap.size()
         + " pass="
         + appendCounter
         + " fail="
         + appendCounter2
         + " transitions="
         + this.transitionsPassCount
         + (this.lastTransitionsCertificate.isEmpty() ? "" : " last=" + this.lastTransitionsCertificate);
   }

   public record Certificate(long stand, long component, TransitionsCertificate.Result result, long geometryRevision, int repeats, String failureClass) {
   }

   public static enum Result {
      PASS,
      FAIL,
      UNKNOWN;
   }
}
