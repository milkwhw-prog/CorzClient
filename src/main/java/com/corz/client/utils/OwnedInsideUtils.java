package com.corz.client.utils;

import java.util.Locale;

public final class OwnedInsideUtils {
   private OwnedInsideUtils() {
   }

   public static String formatOwnedInside(
      int ownedInsideLimit, int ownedInsideValue, boolean ownedInsideFlag, boolean ownedInsideFlag2, boolean ownedInsideFlag3
   ) {
      if (ownedInsideLimit >= ownedInsideValue) {
         return String.format(Locale.ROOT, "component %d >= %d \u2014 free", ownedInsideLimit, ownedInsideValue);
      } else if (ownedInsideFlag) {
         return "the last transition is reversible \u2014 we can walk back out";
      } else if (ownedInsideFlag2 && ownedInsideFlag3) {
         return "real work inside and an owned block to mine out of";
      } else {
         return ownedInsideFlag2
            ? "an owned block inside can be mined to open a doorway"
            : String.format(Locale.ROOT, "one-way drop into a component of %d < %d with no owned escape", ownedInsideLimit, ownedInsideValue);
      }
   }

   public static boolean canFlag(
      int ownedInsideLimit, int ownedInsideValue, boolean ownedInsideFlag, boolean ownedInsideFlag2, boolean postVerdictFlag
   ) {
      if (ownedInsideLimit >= ownedInsideValue) {
         return true;
      } else if (ownedInsideFlag) {
         return true;
      } else {
         return ownedInsideFlag2 ? true : postVerdictFlag && ownedInsideFlag2;
      }
   }
}
