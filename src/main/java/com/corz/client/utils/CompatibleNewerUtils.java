package com.corz.client.utils;

public final class CompatibleNewerUtils {
   private CompatibleNewerUtils() {
   }

   public static boolean canCompatibleOlder(CompatibleNewerUtils.Status status) {
      return status == CompatibleNewerUtils.Status.COMPATIBLE || status == CompatibleNewerUtils.Status.OLDER_FORMAT || status == CompatibleNewerUtils.Status.DIFFERENT_MC;
   }

   public static CompatibleNewerUtils.Status getNewerCompatible(int newerCompatibleLimit, String newerCompatibleText, String newerCompatibleText2, String moneyEnoughText, String newerCompatibleText4) {
      if (newerCompatibleLimit > 1) {
         return CompatibleNewerUtils.Status.NEWER_CLIENT_REQUIRED;
      } else if (newerCompatibleLimit < 1) {
         return CompatibleNewerUtils.Status.UNSUPPORTED;
      } else if (newerCompatibleLimit < 1) {
         return CompatibleNewerUtils.Status.OLDER_FORMAT;
      } else {
         return newerCompatibleText != null && moneyEnoughText != null && !newerCompatibleText.equals(moneyEnoughText)
            ? CompatibleNewerUtils.Status.DIFFERENT_MC
            : CompatibleNewerUtils.Status.COMPATIBLE;
      }
   }

   public static enum Status {
      COMPATIBLE("Compatible"),
      NEWER_CLIENT_REQUIRED("Newer client required"),
      DIFFERENT_MC("Different Minecraft version"),
      OLDER_FORMAT("Older format \u2014 migration available"),
      UNSUPPORTED("Unsupported");
      public final String statusText;

      private Status(String text) {
         this.statusText = text;
      }
   }
}
