package com.corz.client.utils;

public final class PistonStickyUtils {
   private static final int[] pistonStickyTable = new int[]{1, 0, 3, 2, 5, 4};
   public static final int PISTON_STICKY_STEP = 1;
   private static final int[] table = new int[]{0, 0, 0, 0, -1, 1};
   private static final int[] PISTON_STICKY_OFFSET_Y = new int[]{-1, 1, 0, 0, 0, 0};
   private static final int[] PISTON_STICKY_OFFSET_Z = new int[]{0, 0, -1, 1, 0, 0};

   public static int getValue(int pistonStickyValue, PistonStickyUtils.Kind kind) {
      return getFacePlayer(pistonStickyValue, getFaceObserver(kind));
   }

   public static int getFacePlayer(int facePlayerValue, PistonStickyUtils.Family family) {
      return family == PistonStickyUtils.Family.FACE_PLAYER ? getValue(facePlayerValue) : facePlayerValue;
   }

   public static int[] getValues(int pistonStickyValue, PistonStickyUtils.Kind kind) {
      int pistonStickyValue2 = getValue(pistonStickyValue, kind);
      if (canFlag(pistonStickyValue2)) {
         return new int[]{pistonStickyValue2};
      } else {
         int[] ints = new int[6];
         ints[0] = pistonStickyValue2;
         int pistonStickyCounter = 1;

         for (int i = 0; i < 6; i++) {
            if (i != pistonStickyValue2) {
               ints[pistonStickyCounter++] = i;
            }
         }

         return ints;
      }
   }

   public static int getValue(double pistonStickyLimit, double pistonStickyLimit2, double spotValue) {
      double abs4 = Math.abs(pistonStickyLimit);
      double abs5 = Math.abs(pistonStickyLimit2);
      double pistonStickyValue = Math.abs(spotValue);
      if (abs5 > abs4 && abs5 > pistonStickyValue) {
         return pistonStickyLimit2 >= 0.0 ? 1 : 0;
      } else if (abs4 > pistonStickyValue) {
         return pistonStickyLimit >= 0.0 ? 5 : 4;
      } else {
         return spotValue >= 0.0 ? 3 : 2;
      }
   }

   private PistonStickyUtils() {
   }

   public static boolean canFlag(int pistonStickyValue) {
      return pistonStickyValue == 0 || pistonStickyValue == 1;
   }

   public static PistonStickyUtils.Family getFaceObserver(PistonStickyUtils.Kind kind) {
      return kind == PistonStickyUtils.Kind.OBSERVER ? PistonStickyUtils.Family.FACE_AWAY : PistonStickyUtils.Family.FACE_PLAYER;
   }

   public static int[] getValues(int index) {
      return new int[]{table[index], PISTON_STICKY_OFFSET_Y[index], PISTON_STICKY_OFFSET_Z[index]};
   }

   public static int getFacePlayer(int facePlayerValue, PistonStickyUtils.Kind kind) {
      return getFaceObserver(kind) == PistonStickyUtils.Family.FACE_PLAYER ? getValue(facePlayerValue) : facePlayerValue;
   }

   public static int getValue(int index) {
      return pistonStickyTable[index];
   }

   public static enum Family {
      FACE_PLAYER,
      FACE_AWAY;
   }

   public static enum Kind {
      OBSERVER,
      DISPENSER,
      DROPPER,
      PISTON,
      STICKY_PISTON,
      BARREL,
      CRAFTER;
   }
}
