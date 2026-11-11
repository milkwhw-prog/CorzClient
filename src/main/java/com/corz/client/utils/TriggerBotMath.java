package com.corz.client.utils;

public final class TriggerBotMath {
   public static boolean shouldBlockActivationClick(boolean forceBlock, boolean conditionMet, boolean suppressed) {
      return forceBlock || conditionMet && !suppressed;
   }

   public static int millisToTicks(int millis) {
      return Math.max(0, (millis + 25) / 50);
   }

   public static boolean hasBlockDelayElapsed(int elapsedMillis, int extraMillis, int requiredMillis) {
      return elapsedMillis + extraMillis >= requiredMillis;
   }

   public static float interpolateRange(double boundA, double boundB, double factor) {
      double minUnit = Math.min(boundA, boundB);
      double maxUnit = Math.max(boundA, boundB);
      return (float)(minUnit + (maxUnit - minUnit) * factor);
   }

   private TriggerBotMath() {
   }

   public static boolean isAngleWithin(double vectorAX, double vectorAY, double vectorBX, double vectorBY, double maxAngleDegrees) {
      double lengthB = Math.sqrt(vectorBX * vectorBX + vectorBY * vectorBY);
      double lengthA = Math.sqrt(vectorAX * vectorAX + vectorAY * vectorAY);
      double cosAngle = !(lengthB < 1.0E-9) && !(lengthA < 1.0E-9) ? (vectorBX * vectorAX + vectorBY * vectorAY) / (lengthB * lengthA) : 0.0;
      double degrees = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, cosAngle))));
      return degrees <= maxAngleDegrees;
   }
}
