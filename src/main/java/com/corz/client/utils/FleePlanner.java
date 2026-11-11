package com.corz.client.utils;

import java.util.List;

public final class FleePlanner {
   public static final double[] TURN_ANGLES_DEGREES = new double[]{0.0, 25.0, -25.0, 50.0, -50.0, 75.0, -75.0};
   public static final int UNIT_STEP = 1;

   static FleePlanner.Waypoint traceFleeRay(
      double feetValue, int feetValue2, double feetValue3, double feetFactor, double feetFactor2, List<FleePlanner.Threat> list, double[] feetValues, FleePlanner.Ground ground, double degrees
   ) {
      int dx2 = feetValue2;
      double turn2 = feetValue;
      double dz2 = feetValue3;
      double feetCounter5 = 0.0;
      int dx3 = feetValue2;

      for (double feetCounter = 1.0; feetCounter <= 24.000000001; feetCounter++) {
         double feetValue6 = feetValue + feetFactor * feetCounter;
         double hashRegistryValue = feetValue3 + feetFactor2 * feetCounter;
         int floor8 = (int)Math.floor(feetValue6);
         int floor9 = (int)Math.floor(hashRegistryValue);
         int feet = ground.getFloorY(floor8, floor9, dx2);
         if (feet == Integer.MIN_VALUE || feet == -2147483647 || feet == -2147483646 || dx2 - feet > 3 || feet - dx2 > 1) {
            break;
         }

         boolean feetFlag = false;

         for (int i = 0; i < list.size(); i++) {
            double hypot = Math.hypot(feetValue6 - ((FleePlanner.Threat)list.get(i)).x(), hashRegistryValue - ((FleePlanner.Threat)list.get(i)).z());
            if (hypot < feetValues[i]) {
               feetFlag = true;
               break;
            }
         }

         if (feetFlag) {
            break;
         }

         dx2 = feet;
         turn2 = feetValue6;
         dz2 = hashRegistryValue;
         feetCounter5 = feetCounter;
         dx3 = feet;
      }

      if (feetCounter5 < 6.0) {
         return null;
      } else {
         double feetBound = Double.POSITIVE_INFINITY;

         for (int j = 0; j < list.size(); j++) {
            double hypot2 = Math.hypot(turn2 - ((FleePlanner.Threat)list.get(j)).x(), dz2 - ((FleePlanner.Threat)list.get(j)).z());
            feetBound = Math.min(feetBound, hypot2 - feetValues[j]);
         }

         return new FleePlanner.Waypoint(turn2, dx3, dz2, feetBound, degrees);
      }
   }

   public static FleePlanner.Waypoint planFleeWaypoint(double gainValue, int gainValue2, double gainValue3, List<FleePlanner.Threat> list, FleePlanner.Ground ground) {
      if (list != null && !list.isEmpty() && ground != null) {
         double[] gainValues = getFleeDirection(gainValue, gainValue3, list);
         if (gainValues == null) {
            return null;
         } else {
            double[] doubles = new double[list.size()];

            for (int i = 0; i < doubles.length; i++) {
               doubles[i] = Math.hypot(gainValue - ((FleePlanner.Threat)list.get(i)).x(), gainValue3 - ((FleePlanner.Threat)list.get(i)).z());
            }

            FleePlanner.Waypoint gainAmounts = null;
            double negativeInfinity = Double.NEGATIVE_INFINITY;

            for (double degrees : TURN_ANGLES_DEGREES) {
               double angle = Math.toRadians(degrees);
               double cos = Math.cos(angle);
               double sin = Math.sin(angle);
               double feetFactor = gainValues[0] * cos - gainValues[1] * sin;
               double gainValue6 = gainValues[0] * sin + gainValues[1] * cos;
               FleePlanner.Waypoint feet = traceFleeRay(gainValue, gainValue2, gainValue3, feetFactor, gainValue6, list, doubles, ground, degrees);
               if (feet != null) {
                  double gainLimit = feet.gain() - Math.abs(degrees) * 0.04;
                  if (gainLimit > negativeInfinity) {
                     negativeInfinity = gainLimit;
                     gainAmounts = feet;
                  }
               }
            }

            return gainAmounts;
         }
      } else {
         return null;
      }
   }

   public static double[] getFleeDirection(double fleePlannerValue, double fleePlannerValue2, List<FleePlanner.Threat> list) {
      double fleePlannerTotal = 0.0;
      double deltaZ = 0.0;

      for (FleePlanner.Threat threat : list) {
         double fleePlannerFactor = fleePlannerValue - threat.x();
         double fleePlannerFactor2 = fleePlannerValue2 - threat.z();
         double fleePlannerDivisor = fleePlannerFactor * fleePlannerFactor + fleePlannerFactor2 * fleePlannerFactor2;
         if (fleePlannerDivisor < 1.0E-6) {
            fleePlannerDivisor = 1.0E-6;
            fleePlannerFactor = 0.001;
         }

         fleePlannerTotal += fleePlannerFactor / fleePlannerDivisor;
         deltaZ += fleePlannerFactor2 / fleePlannerDivisor;
      }

      double hypot = Math.hypot(fleePlannerTotal, deltaZ);
      return hypot < 1.0E-9 ? null : new double[]{fleePlannerTotal / hypot, deltaZ / hypot};
   }

   private FleePlanner() {
   }

   public static boolean isWaypointTowardThreats(double fleePlannerValue, double fleePlannerValue2, FleePlanner.Waypoint waypoint, List<FleePlanner.Threat> list) {
      if (waypoint != null && list != null && !list.isEmpty()) {
         double fleePlannerFactor = waypoint.x() - fleePlannerValue;
         double deltaZ = waypoint.z() - fleePlannerValue2;
         double hypot = Math.hypot(fleePlannerFactor, deltaZ);
         if (hypot < 1.0E-6) {
            return false;
         } else {
            double[] fleePlannerValues = getFleeDirection(fleePlannerValue, fleePlannerValue2, list);
            return fleePlannerValues == null ? false : (fleePlannerFactor * fleePlannerValues[0] + deltaZ * fleePlannerValues[1]) / hypot < 0.3;
         }
      } else {
         return false;
      }
   }

   public interface Ground {

      int getFloorY(int feetValue, int feetValue2, int dx2);
   }

   public record Threat(double x, double z) {
   }

   public record Waypoint(double x, int y, double z, double gain, double turn) {
   }
}
