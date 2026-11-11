package com.corz.client.utils;

public final class ClusterFieldsUtils {
   public static final int CLUSTER_FIELDS_STEP = 1;

   private ClusterFieldsUtils() {
   }

   private static long packClusterKey(long clusterFieldsValue, int clusterFieldsValue2) {
      clusterFieldsValue = (clusterFieldsValue ^ clusterFieldsValue2 & 4294967295L) * 1099511628211L;
      return clusterFieldsValue ^ clusterFieldsValue >>> 29;
   }

   private static int appendClusterField(StringBuilder stringBuilder, int clusterFieldsLimit, String clusterFieldsText, String clusterFieldsText2, String builderText, boolean clusterFieldsFlag) {
      if (!clusterFieldsFlag) {
         return clusterFieldsLimit;
      } else {
         if (clusterFieldsLimit > 0) {
            stringBuilder.append(", ");
         }

         stringBuilder.append(clusterFieldsText).append(": ").append(clusterFieldsText2).append("->").append(builderText);
         return clusterFieldsLimit + 1;
      }
   }

   public static long combineClusterValues(long... clusterFieldsValues) {
      if (clusterFieldsValues != null && clusterFieldsValues.length != 0) {
         long clusterFieldsTotal = 0L;

         for (long clusterFieldsValue : clusterFieldsValues) {
            clusterFieldsTotal += getClusterTotalKey(clusterFieldsValue);
         }

         return getClusterTotalKey(clusterFieldsTotal ^ clusterFieldsValues.length * 1099511628211L);
      } else {
         return -3750763034362895579L;
      }
   }

   public static int floorDivCluster(int floorDivValue) {
      return Math.floorDiv(floorDivValue, 2);
   }

   public static int clampClusterValue(int clusterFieldsLimit, int clusterFieldsLimit2, int j) {
      int abs4 = Math.abs(clusterFieldsLimit);
      int abs5 = Math.abs(clusterFieldsLimit2);
      int clusterFieldsValue = Math.abs(j);
      if (abs4 == 0 && abs5 == 0 && clusterFieldsValue == 0) {
         return 6;
      } else if (abs5 > abs4 && abs5 > clusterFieldsValue) {
         return clusterFieldsLimit2 > 0 ? 4 : 5;
      } else if (abs4 >= clusterFieldsValue) {
         return clusterFieldsLimit > 0 ? 2 : 3;
      } else {
         return j > 0 ? 1 : 0;
      }
   }

   private static long getClusterTotalKey(long clusterFieldsTotal) {
      clusterFieldsTotal += -7046029254386353131L;
      clusterFieldsTotal = (clusterFieldsTotal ^ clusterFieldsTotal >>> 30) * -4658895280553007687L;
      clusterFieldsTotal = (clusterFieldsTotal ^ clusterFieldsTotal >>> 27) * -7723592293110705685L;
      return clusterFieldsTotal ^ clusterFieldsTotal >>> 31;
   }

   public static String formatSouthNorth(int southNorthValue) {
      return switch (southNorthValue) {
         case 0 -> "NORTH";
         case 1 -> "SOUTH";
         case 2 -> "EAST";
         case 3 -> "WEST";
         case 4 -> "ABOVE";
         case 5 -> "BELOW";
         case 6 -> "ANY";
         default -> "SIDE" + southNorthValue;
      };
   }

   public static long packClusterCoords(int clusterFieldsValue, int clusterFieldsValue2, int clusterFieldsValue3, int clusterFieldsValue4, int clusterFieldsValue5, int autoBuilderValue) {
      long clusterFieldsValue7 = -3750763034362895579L;
      long packClusterKey8 = packClusterKey(clusterFieldsValue7, clusterFieldsValue);
      long packClusterKey9 = packClusterKey(packClusterKey8, clusterFieldsValue2);
      long packClusterKey10 = packClusterKey(packClusterKey9, clusterFieldsValue3);
      long packClusterKey11 = packClusterKey(packClusterKey10, clusterFieldsValue4);
      long packClusterKey12 = packClusterKey(packClusterKey11, clusterFieldsValue5);
      return packClusterKey(packClusterKey12, autoBuilderValue);
   }

   public static int floorDivClusterSecondary(int floorDivValue) {
      return Math.floorDiv(floorDivValue, 4);
   }

   public static String formatClusterFields(
      int clusterFieldsValue, int clusterFieldsValue2, int clusterFieldsValue3, int clusterFieldsValue4, int clusterFieldsValue5, int southNorthValue, int clusterFieldsValue7, int logoProfileCounter, int clusterFieldsValue9, int clusterFieldsValue10, int clusterFieldsValue11, int clusterFieldsValue12
   ) {
      StringBuilder stringBuilder = new StringBuilder("changedFields={");
      byte clusterFieldsValue13 = 0;
      int appendClusterField14 = appendClusterField(stringBuilder, clusterFieldsValue13, "kind", Integer.toString(clusterFieldsValue), Integer.toString(clusterFieldsValue7), clusterFieldsValue != clusterFieldsValue7);
      int appendClusterField15 = appendClusterField(stringBuilder, appendClusterField14, "clusterX", Integer.toString(clusterFieldsValue2), Integer.toString(logoProfileCounter), clusterFieldsValue2 != logoProfileCounter);
      int clusterFieldsLimit = appendClusterField(stringBuilder, appendClusterField15, "clusterY", Integer.toString(clusterFieldsValue3), Integer.toString(clusterFieldsValue9), clusterFieldsValue3 != clusterFieldsValue9);
      int appendClusterField17 = appendClusterField(stringBuilder, clusterFieldsLimit, "clusterZ", Integer.toString(clusterFieldsValue4), Integer.toString(clusterFieldsValue10), clusterFieldsValue4 != clusterFieldsValue10);
      int appendClusterField18 = appendClusterField(stringBuilder, appendClusterField17, "heightBand", Integer.toString(clusterFieldsValue5), Integer.toString(clusterFieldsValue11), clusterFieldsValue5 != clusterFieldsValue11);
      appendClusterField(stringBuilder, appendClusterField18, "entrySide", formatSouthNorth(southNorthValue), formatSouthNorth(clusterFieldsValue12), southNorthValue != clusterFieldsValue12);
      return stringBuilder.append('}').toString();
   }
}
