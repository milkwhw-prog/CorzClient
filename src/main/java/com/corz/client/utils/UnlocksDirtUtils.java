package com.corz.client.utils;

public final class UnlocksDirtUtils {
   public static String getAppend(UnlocksDirtUtils.Certificate certificate) {
      return certificate == null
         ? "NONE"
         : certificate.kind() + " owner=" + certificate.owner() + " unlocks=" + certificate.provenUnlocks() + " dirt=" + certificate.dirtCost() + (certificate.reason().isEmpty() ? "" : " why=" + certificate.reason());
   }

   public static UnlocksDirtUtils.Certificate buildCertificate(UnlocksDirtUtils.Kind kind, long unlocksDirtValue, long unlocksDirtValue2, int n10, int unlocksDirtBound, String unlocksDirtText) {
      if (n10 <= 0) {
         return null;
      } else {
         return unlocksDirtBound < 0
            ? null
            : new UnlocksDirtUtils.Certificate(kind, unlocksDirtValue, unlocksDirtValue2, n10, Math.max(0, unlocksDirtBound), unlocksDirtText == null ? "" : unlocksDirtText);
      }
   }

   private UnlocksDirtUtils() {
   }

   public record Certificate(UnlocksDirtUtils.Kind kind, long owner, long workPose, int provenUnlocks, int dirtCost, String reason) {
   }

   public static enum Kind {
      DIRECT_COLUMN,
      MOUNT,
      ELEVATED_TOWER,
      DECK_MOUNT,
      WORK_PLATFORM,
      SUPPORT,
      CONNECTOR,
      CONSTRUCTIVE;
   }
}
