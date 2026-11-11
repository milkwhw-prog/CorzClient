package com.corz.client.utils;

public final class RecoverReadmitUtils {
   private static final RecoverReadmitUtils.WatchdogAction READMIT_RECOVER_TYPE = RecoverReadmitUtils.WatchdogAction.RECOVER_TEMP_DEBT;

   public static int getValue(boolean recoverReadmitFlag, int recoverReadmitBound) {
      return recoverReadmitFlag ? recoverReadmitBound : Math.min(recoverReadmitBound, 4);
   }

   public static boolean canFlag(int recoverReadmitValue, int recoverReadmitLimit, int y, int recoverReadmitValue3, int n5, boolean recoverReadmitFlag, boolean recoverReadmitFlag2) {
      if (recoverReadmitFlag2 || !recoverReadmitFlag) {
         return false;
      } else if (recoverReadmitValue3 < 9) {
         return false;
      } else {
         return recoverReadmitValue < recoverReadmitLimit ? false : recoverReadmitValue - y > n5 + 1;
      }
   }

   public static RecoverReadmitUtils.WatchdogAction getReadmitRecover(boolean readmitRecoverFlag, boolean readmitRecoverFlag2) {
      if (readmitRecoverFlag) {
         return READMIT_RECOVER_TYPE;
      } else {
         return readmitRecoverFlag2 ? RecoverReadmitUtils.WatchdogAction.READMIT : RecoverReadmitUtils.WatchdogAction.WAIT;
      }
   }

   public static int getValue(int recoverReadmitValue, int n6, int recoverReadmitValue3) {
      int max4 = Math.max(0, recoverReadmitValue - n6);
      return recoverReadmitValue3 + (max4 + 1) / 2;
   }

   public static boolean canFlag(int recoverReadmitLimit, int recoverReadmitValue, boolean recoverReadmitFlag) {
      return !recoverReadmitFlag || recoverReadmitLimit >= 2 * recoverReadmitValue;
   }

   private RecoverReadmitUtils() {
   }

   public static enum WatchdogAction {
      RECOVER_TEMP_DEBT,
      READMIT,
      WAIT;
   }
}
