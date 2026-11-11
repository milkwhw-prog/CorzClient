package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

public final class BuilderAutoUtilsTarget {
   public static boolean hasEnoughBudget(int builderAutoLimit, int builderAutoBound, int rocketsBuyBound) {
      if (builderAutoBound <= 0) {
         return builderAutoLimit > 0;
      } else {
         int builderAutoValue = Math.max(1, rocketsBuyBound) * Math.min(builderAutoBound, 4);
         return builderAutoLimit >= builderAutoValue;
      }
   }

   private static int getChebyshevDistance(BuilderAutoUtilsTarget.Candidate candidate, BuilderAutoUtilsTarget.Candidate candidate2) {
      return Math.max(Math.abs(candidate.x() - candidate2.x()), Math.abs(candidate.z() - candidate2.z()));
   }

   private BuilderAutoUtilsTarget() {
   }

   public static List<BuilderAutoUtilsTarget.Candidate> getTarget(long targetValue, List<BuilderAutoUtilsTarget.Candidate> list, int targetBound, int targetLimit) {
      if (list != null && !list.isEmpty() && targetLimit > 0) {
         BuilderAutoUtilsTarget.Candidate candidate = null;

         for (BuilderAutoUtilsTarget.Candidate candidate2 : list) {
            if (candidate2.target() == targetValue) {
               candidate = candidate2;
               break;
            }
         }

         if (candidate == null) {
            return List.of();
         } else {
            int maxLinkRadius = Math.max(1, targetBound);
            int maxMaxTargets = Math.max(1, targetLimit);
            ArrayDeque arrayDeque = new ArrayDeque();
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            ArrayList arrayList = new ArrayList();
            arrayDeque.add(candidate);
            linkedHashSet.add(candidate.target());

            while (!arrayDeque.isEmpty() && arrayList.size() < maxMaxTargets) {
               BuilderAutoUtilsTarget.Candidate removeFirst = (BuilderAutoUtilsTarget.Candidate)arrayDeque.removeFirst();
               arrayList.add(removeFirst);

               for (BuilderAutoUtilsTarget.Candidate dequeArrayValue : list) {
                  if (!linkedHashSet.contains(dequeArrayValue.target()) && dequeArrayValue.y() == candidate.y()) {
                     int aggroPreemptBound = Math.abs(dequeArrayValue.x() - removeFirst.x());
                     int rocketsBuyBound = Math.abs(dequeArrayValue.z() - removeFirst.z());
                     if (Math.max(aggroPreemptBound, rocketsBuyBound) <= maxLinkRadius) {
                        linkedHashSet.add(dequeArrayValue.target());
                        arrayDeque.addLast(dequeArrayValue);
                     }
                  }
               }
            }

            BuilderAutoUtilsTarget.Candidate candidate4 = candidate;
            arrayList.sort(
               Comparator.<BuilderAutoUtilsTarget.Candidate>comparingInt(clusterRoot -> getChebyshevDistance(clusterRoot, candidate4))
                  .thenComparingInt(BuilderAutoUtilsTarget.Candidate::z)
                  .thenComparingInt(BuilderAutoUtilsTarget.Candidate::x)
                  .thenComparingLong(BuilderAutoUtilsTarget.Candidate::target)
            );
            return List.copyOf(arrayList);
         }
      } else {
         return List.of();
      }
   }

   public record Candidate(long target, int x, int y, int z) {
   }
}
