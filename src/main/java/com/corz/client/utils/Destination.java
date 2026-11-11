package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public record Destination(int x, int z) {

   public static String getFormat(double blocks) {
      if (blocks < 0.0) {
         return "-";
      } else if (blocks < 1000.0) {
         return Math.round(blocks) + " m";
      } else {
         return blocks < 100000.0 ? String.format(Locale.ROOT, "%.1f km", blocks / 1000.0) : Math.round(blocks / 1000.0) + " km";
      }
   }

   public static Destination getDestination(String raw) {
      List root = getRoot(raw);
      if (root != null && (root.size() == 2 || root.size() == 3)) {
         double firstCoord = (Double)root.get(0);
         double secondCoord = (Double)root.get(root.size() - 1);
         return Double.isFinite(firstCoord) && Double.isFinite(secondCoord) && !(Math.abs(firstCoord) > 2.9999E7) && !(Math.abs(secondCoord) > 2.9999E7)
            ? new Destination((int)Math.floor(firstCoord), (int)Math.floor(secondCoord))
            : null;
      } else {
         return null;
      }
   }

   public String formatText() {
      return "X " + this.x + "  Z " + this.z;
   }

   public String formatTextString() {
      return this.x + ", " + this.z;
   }

   public double getAmount(double pz, double px) {
      return Math.hypot(this.x + 0.5 - pz, this.z + 0.5 - px);
   }

   public static String formatExampleNumbers(String raw) {
      if (raw != null && !raw.isBlank()) {
         List root = getRoot(raw);
         if (root == null) {
            return "Use only numbers, for example 1200 -3400.";
         } else if (root.size() < 2) {
            return "Add both X and Z, for example 1200 -3400.";
         } else if (root.size() > 3) {
            return "Too many numbers. Type X Z or X Y Z.";
         } else {
            return getDestination(raw) == null ? "That spot is outside the world." : null;
         }
      } else {
         return "Type the destination as X Z, for example 1200 -3400.";
      }
   }

   public static String formatText(long seconds) {
      if (seconds < 0L) {
         return "-";
      } else {
         long secondsScaled = seconds / 3600L;
         long destinationLimit = seconds % 3600L / 60L;
         long destinationValue = seconds % 60L;
         if (secondsScaled > 0L) {
            return secondsScaled + "h " + destinationLimit + "m";
         } else {
            return destinationLimit > 0L ? destinationLimit + "m " + destinationValue + "s" : destinationValue + "s";
         }
      }
   }

   private static List<Double> getRoot(String raw) {
      if (raw == null) {
         return null;
      } else {
         String trim = raw.toLowerCase(Locale.ROOT).trim();
         String replace = trim.replace("x:", " ").replace("y:", " ").replace("z:", " ").replace("x=", " ").replace("y=", " ").replace("z=", " ");
         String trim2 = replace.replaceAll("[,;/|()\\[\\]]", " ").trim();
         if (trim2.isEmpty()) {
            return new ArrayList<>();
         } else {
            ArrayList arrayList = new ArrayList();

            for (String rootText : trim2.split("\\s+")) {
               if (!rootText.isEmpty()) {
                  double rootFactor = 1.0;
                  if (rootText.endsWith("k")) {
                     rootFactor = 1000.0;
                     rootText = rootText.substring(0, rootText.length() - 1);
                  }

                  if (rootText.isEmpty() || rootText.equals("-") || rootText.equals("+")) {
                     return null;
                  }

                  try {
                     arrayList.add(Double.parseDouble(rootText) * rootFactor);
                  } catch (NumberFormatException e) {
                     return null;
                  }
               }
            }

            return arrayList;
         }
      }
   }
}
