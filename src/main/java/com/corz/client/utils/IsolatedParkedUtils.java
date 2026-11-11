package com.corz.client.utils;
import com.corz.client.utils.FingerprintUpdate;

public final class IsolatedParkedUtils {
   private IsolatedParkedUtils() {
   }

   static String getName(int nameValue) {
      return nameValue <= 0 ? FingerprintUpdate.Strategy.STRICT_ROI.name() : FingerprintUpdate.Strategy.NO_MOUNTS.name();
   }

   public static IsolatedParkedUtils.Recovery getIsolatedAccess(String isolatedAccessText, boolean isolatedAccessFlag, int isolatedAccessValue, boolean isolatedAccessFlag2) {
      return getParkedRegions(isolatedAccessText, isolatedAccessFlag, isolatedAccessValue, isolatedAccessFlag2, "ISOLATED_AFTER_ACCESS".equals(isolatedAccessText));
   }

   public static IsolatedParkedUtils.Recovery getParkedRegions(
      String parkedRegionsText, boolean parkedRegionsFlag, int parkedRegionsValue, boolean parkedRegionsFlag2, boolean parkedRegionsFlag3
   ) {
      if (!parkedRegionsFlag3
         || !"ISOLATED_AFTER_ACCESS".equals(parkedRegionsText)
            && !"NO_ACCESS_ROUTE".equals(parkedRegionsText)
            && !"DIRT_LOOP_BAN".equals(parkedRegionsText)
            && !"NO_STAND".equals(parkedRegionsText)
            && !"ROI_REFUSED".equals(parkedRegionsText)) {
         switch (parkedRegionsText) {
            case "ISOLATED_AFTER_ACCESS":
               if (parkedRegionsFlag2) {
                  return new IsolatedParkedUtils.Recovery(
                     parkedRegionsText, IsolatedParkedUtils.Action.LOCAL_MISSION, "missionForced (no planned exit \u2014 staircase / dig plan)"
                  );
               }

               return new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.PARK, "waitParked (isolated, no planned exit)");
            case "TEMP_DEBT_NO_RECYCLE":
               return new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.RECLAIM_TEMP_DEBT, "tempDebt (reclaim mission started)");
            case "ROI_REFUSED":
               return new IsolatedParkedUtils.Recovery(
                  parkedRegionsText, IsolatedParkedUtils.Action.RESOLVE_ROI_ALTERNATIVES, "roiAlternatives (redirect dispatched or phantoms dropped)"
               );
            case "DIRT_LOOP_BAN":
            case "NO_ACCESS_ROUTE":
               if (parkedRegionsValue < 2) {
                  return new IsolatedParkedUtils.Recovery(
                     parkedRegionsText, IsolatedParkedUtils.Action.ESCALATE_ACCESS_STRATEGY, "accessStrategy=" + getName(parkedRegionsValue)
                  );
               } else if (parkedRegionsFlag) {
                  return new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.EXCLUDE_FAILED_REGIONS, "failedRegions");
               } else {
                  if (parkedRegionsFlag2) {
                     return new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.LOCAL_MISSION, "missionForced (local cluster)");
                  }

                  return new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.PARK, "waitParked (no alternative left)");
               }
            case "NO_STAND":
            case "POSE_BLOCKED":
            case "WAITING_SUPPORT":
               if (parkedRegionsFlag) {
                  return new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.EXCLUDE_FAILED_REGIONS, "failedRegions");
               } else {
                  if (parkedRegionsFlag2) {
                     return new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.LOCAL_MISSION, "missionForced (local cluster)");
                  }

                  return new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.PARK, "waitParked (no alternative left)");
               }
            default:
               return parkedRegionsFlag
                  ? new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.EXCLUDE_FAILED_REGIONS, "failedRegions")
                  : new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.PARK, "waitParked (no alternative left)");
         }
      } else {
         return new IsolatedParkedUtils.Recovery(parkedRegionsText, IsolatedParkedUtils.Action.RETURN_TO_WORKSPACE, "playerComponent (return to the workspace)");
      }
   }

   public static enum Action {
      RECLAIM_TEMP_DEBT,
      ESCALATE_ACCESS_STRATEGY,
      EXCLUDE_FAILED_REGIONS,
      LOCAL_MISSION,
      RESOLVE_ROI_ALTERNATIVES,
      RETURN_TO_WORKSPACE,
      PARK;
   }

   public record Recovery(String blocker, IsolatedParkedUtils.Action action, String changed) {
      @Override
      public String toString() {
         return this.blocker + " \u2192 " + this.action + " (" + this.changed + ")";
      }
   }
}
