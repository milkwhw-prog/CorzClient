package com.corz.client.utils;

public final class PauseReadmitUtils {
   private PauseReadmitUtils() {
   }

   public static PauseReadmitUtils.Action getPauseReadmit(boolean isPaused, boolean isReadmitted) {
      return isPaused && !isReadmitted ? PauseReadmitUtils.Action.PAUSE : PauseReadmitUtils.Action.READMIT;
   }

   public static enum Action {
      READMIT,
      PAUSE;
   }
}
