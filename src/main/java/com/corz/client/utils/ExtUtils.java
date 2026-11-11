package com.corz.client.utils;

import java.util.Arrays;

public final class ExtUtils {
   private static volatile String extText = "-";
   private static boolean actionActive;
   private static volatile boolean extActive;
   private static int consecutiveActionCount;
   private static int actionCount;
   private static int extValue;
   private static int appendValue;
   private static int appendCount;
   private static int extCount;
   private static volatile String actionText = "-";
   private static boolean actionFlagged;
   private static volatile boolean appendActive;
   private static volatile Runnable action;
   private static final boolean[] extFlags = new boolean[20];
   private static volatile boolean extConfirmed;
   public static final String EXT = "ext";
   private static double hypot;
   private static int actionValue;
   private static double lastMoveDistance;

   public static void handleExt(Runnable runnable, boolean extFlag) {
      canActionText(runnable, extFlag, true, "ext");
   }

   public static boolean canAmount(boolean extFlag) {
      return actionValue >= 4 && (!extFlag || lastMoveDistance < 0.04 && hypot < 0.04);
   }

   public static boolean isActionFlag() {
      return actionActive && action != null;
   }

   public static boolean isFlag() {
      return extConfirmed;
   }

   public static int getValue() {
      return consecutiveActionCount;
   }

   public static int getActionValue() {
      return actionValue;
   }

   public static void updateFlagText() {
      action = null;
      extActive = false;
      appendActive = false;
      actionActive = false;
      actionFlagged = false;
      extCount = 0;
      actionCount = 0;
      actionValue = 0;
      hypot = 0.0;
      lastMoveDistance = 0.0;
      extText = "-";
      actionText = "-";
      extConfirmed = false;
      Arrays.fill(extFlags, false);
      extValue = 0;
      appendCount = 0;
      appendValue = 0;
      consecutiveActionCount = 0;
   }

   public static void handleFlag(boolean flag) {
      extConfirmed = flag;
   }

   private static void updateFlags() {
      boolean actionFlag = isActionFlag2();
      if (extFlags[extValue]) {
         appendCount--;
      }

      extFlags[extValue] = actionFlag;
      if (actionFlag) {
         appendCount++;
      }

      extValue = (extValue + 1) % 20;
      appendValue = actionFlag ? Math.min(appendValue + 1, 1048576) : 0;
   }

   public static String getAppend() {
      boolean actionFlag = isActionFlag2();
      String appendText = action != null ? extText : (appendActive ? actionText : "-");
      return (actionFlag ? 1 : 0) + ":" + appendValue + ":" + appendText + ":" + appendCount;
   }

   public static int getAppendCount() {
      return appendCount;
   }

   public static boolean isActionFlagged() {
      return actionFlagged;
   }

   public static boolean isActionFlag2() {
      return action != null && extActive || appendActive;
   }

   public static boolean canActionText(Runnable runnable, boolean flag, boolean actionFlag, String actionText) {
      if (runnable == null) {
         return false;
      } else if (flag && !actionFlag && isFlagExtConfirmed()) {
         consecutiveActionCount++;
         return false;
      } else {
         if (action == null) {
            extCount = 0;
         }

         action = runnable;
         extActive = flag;
         extText = actionText != null && !actionText.isEmpty() ? actionText : "-";
         return true;
      }
   }

   public static void updateFlagAction() {
      actionCount = 0;
      appendActive = false;
      actionFlagged = false;
      Runnable action2 = action;
      boolean actionFlag = actionActive;
      actionActive = false;
      if (actionFlag && action2 != null) {
         boolean extActive5 = extActive;
         action = null;
         extActive = false;
         appendActive = extActive5;
         actionText = extText;
         actionFlagged = true;

         try {
            action2.run();
         } catch (Exception e) {
         }

         updateFlags();
      } else {
         if (action != null && ++extCount > 15) {
            action = null;
            extActive = false;
         }

         updateFlags();
      }
   }

   public static boolean isFlagExtConfirmed() {
      return extConfirmed && appendCount >= 8;
   }

   private ExtUtils() {
   }

   public static boolean canActionFlag(Runnable runnable) {
      if (runnable != null && action == runnable) {
         action = null;
         extActive = false;
         actionActive = false;
         return true;
      } else {
         return false;
      }
   }

   public static boolean isAction() {
      return action != null;
   }

   public static void handleActionFlag(boolean actionFlag, double speed, boolean extFlag) {
      actionValue = actionFlag ? Math.min(actionValue + 1, 1048576) : 0;
      lastMoveDistance = hypot;
      hypot = speed;
      if (++actionCount > 15) {
         action = null;
         extActive = false;
         appendActive = false;
         actionActive = false;
      } else {
         actionActive = action != null && canAmount(extFlag);
      }
   }
}
