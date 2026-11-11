package com.corz.client.utils;

import java.util.Locale;

public final class DisplayEpoch {
   public final String uuidEpochText;
   public final String equalsText;
   public final long ancHostTime;

   @Override
   public int hashCode() {
      return this.equalsText.hashCode() * 31 + Long.hashCode(this.ancHostTime);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof DisplayEpoch displayEpoch && displayEpoch.equalsText.equals(this.equalsText) && displayEpoch.ancHostTime == this.ancHostTime;
   }

   @Override
   public String toString() {
      return this.getFormat();
   }

   public DisplayEpoch.Reject getEpochAnc(String epochAncText, long epochAncLimit) {
      if (epochAncText == null || epochAncText.isEmpty()) {
         return DisplayEpoch.Reject.UNSTAMPED;
      } else if (!this.equalsText.equals(epochAncText)) {
         return DisplayEpoch.Reject.WRONG_SESSION;
      } else if (epochAncLimit < this.ancHostTime) {
         return DisplayEpoch.Reject.STALE_EPOCH;
      } else {
         return epochAncLimit > this.ancHostTime ? DisplayEpoch.Reject.FUTURE_EPOCH : DisplayEpoch.Reject.ACCEPTED;
      }
   }

   public DisplayEpoch(String displayEpochText, String displayEpochText2, long ancHostTime) {
      this.uuidEpochText = displayEpochText == null ? "" : displayEpochText;
      this.equalsText = displayEpochText2 == null ? "" : displayEpochText2;
      this.ancHostTime = ancHostTime;
   }

   public static DisplayEpoch getDisplayEpoch(String displayEpochText, String leaveCorzText, long displayEpochValue) {
      String displayEpochText3 = (leaveCorzText == null ? "?" : leaveCorzText) + '@' + displayEpochValue;
      return new DisplayEpoch(displayEpochText, Long.toHexString(displayEpochText3.hashCode() & 4294967295L) + '-' + Long.toHexString(displayEpochValue), 1L);
   }

   public static DisplayEpoch getDisplayEpoch() {
      return new DisplayEpoch("", "", 0L);
   }

   public boolean isEpochUuid() {
      return !this.equalsText.isEmpty();
   }

   public boolean canAncEpoch(String ancEpochText, long ancEpochValue) {
      return this.isEpochUuid() && this.equalsText.equals(ancEpochText) && this.ancHostTime == ancEpochValue;
   }

   public DisplayEpoch getEpochUuid() {
      return new DisplayEpoch(this.uuidEpochText, this.equalsText, this.ancHostTime + 1L);
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT, "uuid=%s display=%s epoch=%d", this.equalsText.isEmpty() ? "none" : this.equalsText, this.uuidEpochText, this.ancHostTime
      );
   }

   public static enum Reject {
      ACCEPTED,
      WRONG_SESSION,
      STALE_EPOCH,
      FUTURE_EPOCH,
      UNSTAMPED;
   }
}
