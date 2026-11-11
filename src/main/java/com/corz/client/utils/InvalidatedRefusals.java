package com.corz.client.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class InvalidatedRefusals {
   private final Map<InvalidatedRefusals.Key, InvalidatedRefusals.Record> refusalMap = new LinkedHashMap<>();
   private int invalidatedRefusals;

   public String lookupRefusalReason(long invalidatedRefusalsValue, long invalidatedRefusalsValue2, long invalidatedRefusalsValue3) {
      InvalidatedRefusals.Key key = new InvalidatedRefusals.Key(invalidatedRefusalsValue, invalidatedRefusalsValue2);
      InvalidatedRefusals.Record invalidatedmap = this.refusalMap.get(key);
      if (invalidatedmap == null) {
         return null;
      } else if (invalidatedmap.dependencyFingerprint() != invalidatedRefusalsValue3) {
         this.refusalMap.remove(key);
         return null;
      } else {
         this.invalidatedRefusals++;
         return invalidatedmap.why();
      }
   }

   public void recordRefusal(long invalidatedValue, long invalidatedValue2, long invalidatedValue3, String invalidatedText, int invalidatedValue4) {
      this.refusalMap
         .put(
            new InvalidatedRefusals.Key(invalidatedValue, invalidatedValue2),
            new InvalidatedRefusals.Record(invalidatedValue3, invalidatedText != null && !invalidatedText.isBlank() ? invalidatedText : "invalidated", invalidatedValue4)
         );
   }

   public String lookupRefusalReasonForTarget(long invalidatedRefusalsValue, long invalidatedRefusalsValue2) {
      ArrayList arrayList = new ArrayList();
      String invalidatedRefusalsText = null;

      for (Entry entry : this.refusalMap.entrySet()) {
         if (((InvalidatedRefusals.Key)entry.getKey()).target() == invalidatedRefusalsValue) {
            if (((InvalidatedRefusals.Record)entry.getValue()).dependencyFingerprint() != invalidatedRefusalsValue2) {
               arrayList.add((InvalidatedRefusals.Key)entry.getKey());
            } else if (invalidatedRefusalsText == null) {
               invalidatedRefusalsText = ((InvalidatedRefusals.Record)entry.getValue()).why();
            }
         }
      }

      for (InvalidatedRefusals.Key key : (Iterable<com.corz.client.utils.InvalidatedRefusals.Key>) (arrayList)) {
         this.refusalMap.remove(key);
      }

      if (invalidatedRefusalsText != null) {
         this.invalidatedRefusals++;
      }

      return invalidatedRefusalsText;
   }

   public int getInvalidatedRefusals() {
      return this.invalidatedRefusals;
   }

   public String describeStats() {
      return "invalidatedTickets=" + this.refusalMap.size() + " invalidatedRefusals=" + this.invalidatedRefusals;
   }

   public int getTicketCount() {
      return this.refusalMap.size();
   }

   public void clearRefusals() {
      this.refusalMap.clear();
      this.invalidatedRefusals = 0;
   }

   private record Key(long target, long stand) {
   }

   private record Record(long dependencyFingerprint, String why, int tick) {
   }
}
