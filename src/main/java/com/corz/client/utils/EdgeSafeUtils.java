package com.corz.client.utils;

import java.util.function.BiPredicate;

public final class EdgeSafeUtils {

   public static boolean canEdgeUnsupported(EdgeSafeUtils.Zone zone) {
      return zone == EdgeSafeUtils.Zone.EDGE_PLACE || zone == EdgeSafeUtils.Zone.UNSUPPORTED_EDGE;
   }

   public static boolean canSafeInterior(EdgeSafeUtils.Zone zone) {
      return zone == EdgeSafeUtils.Zone.SAFE_INTERIOR || zone == EdgeSafeUtils.Zone.SAFE_LANE;
   }

   public static boolean isEdgeSafe(double edgeSafeValue, double hashRegistryBound, double hashRegistryValue, double edgeSafeValue4, BiPredicate<Integer, Integer> biPredicate) {
      for (int i = (int)Math.floor(edgeSafeValue); i <= Math.floor(hashRegistryBound - 1.0E-7); i++) {
         for (int j = (int)Math.floor(hashRegistryValue); j <= Math.floor(edgeSafeValue4 - 1.0E-7); j++) {
            if (!biPredicate.test(i, j)) {
               return false;
            }
         }
      }

      return true;
   }

   private EdgeSafeUtils() {
   }

   public static EdgeSafeUtils.Zone getEdgeSafe(double edgeSafeValue, double edgeSafeValue2, double edgeSafeLimit, double velocityZ, boolean edgeSafeFlag, BiPredicate<Integer, Integer> biPredicate) {
      double edgeSafeBound = edgeSafeValue - 0.3;
      double hashRegistryBound = edgeSafeValue + 0.3;
      double edgeSafeBound3 = edgeSafeValue2 - 0.3;
      double edgeSafeBound4 = edgeSafeValue2 + 0.3;
      if (!isEdgeSafe(edgeSafeBound, hashRegistryBound, edgeSafeBound3, edgeSafeBound4, biPredicate)) {
         return EdgeSafeUtils.Zone.UNSUPPORTED_EDGE;
      } else {
         double floorScaled = edgeSafeLimit * 2.0;
         double edgeSafeValue3 = velocityZ * 2.0;
         double min4 = Math.min(edgeSafeBound, edgeSafeBound + floorScaled);
         double max5 = Math.max(hashRegistryBound, hashRegistryBound + floorScaled);
         double min6 = Math.min(edgeSafeBound3, edgeSafeBound3 + edgeSafeValue3);
         double max7 = Math.max(edgeSafeBound4, edgeSafeBound4 + edgeSafeValue3);
         boolean edgeSafeFlag2 = Math.abs(edgeSafeLimit) < 0.01 && Math.abs(velocityZ) < 0.01;
         double edgeSafeValue8 = min4 - (!edgeSafeFlag2 && !(edgeSafeLimit <= -0.01) ? 0.0 : 0.25);
         double edgeSafeValue9 = max5 + (!edgeSafeFlag2 && !(edgeSafeLimit >= 0.01) ? 0.0 : 0.25);
         double hashRegistryValue = min6 - (!edgeSafeFlag2 && !(velocityZ <= -0.01) ? 0.0 : 0.25);
         double edgeSafeValue11 = max7 + (!edgeSafeFlag2 && !(velocityZ >= 0.01) ? 0.0 : 0.25);
         if (!isEdgeSafe(edgeSafeValue8, edgeSafeValue9, hashRegistryValue, edgeSafeValue11, biPredicate)) {
            return edgeSafeFlag ? EdgeSafeUtils.Zone.EDGE_PLACE : EdgeSafeUtils.Zone.EDGE_APPROACH;
         } else {
            return isEdgeSafe(edgeSafeBound - 1.0, hashRegistryBound + 1.0, edgeSafeBound3 - 1.0, edgeSafeBound4 + 1.0, biPredicate) ? EdgeSafeUtils.Zone.SAFE_INTERIOR : EdgeSafeUtils.Zone.SAFE_LANE;
         }
      }
   }

   public static enum Zone {
      SAFE_INTERIOR,
      SAFE_LANE,
      EDGE_APPROACH,
      EDGE_PLACE,
      UNSUPPORTED_EDGE;
   }
}
