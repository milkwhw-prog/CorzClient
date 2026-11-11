package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;

public final class RevisionSurface {
   private final Map<RevisionSurface.Signature, Integer> revisionSurfaceIntegerMap = new HashMap<>();
   private int lastConfirmedCount = -1;
   private int revisionSurfaceValue;
   private long lastSurfaceRevision = Long.MIN_VALUE;

   public int getRevisionSurface(RevisionSurface.Signature signature, int value) {
      if (signature.confirmedCount() > this.lastConfirmedCount || signature.surfaceRevision() != this.lastSurfaceRevision) {
         this.revisionSurfaceIntegerMap.clear();
         this.revisionSurfaceValue = value;
         this.lastConfirmedCount = signature.confirmedCount();
         this.lastSurfaceRevision = signature.surfaceRevision();
      }

      return this.revisionSurfaceIntegerMap.size() >= 128 && !this.revisionSurfaceIntegerMap.containsKey(signature) ? 2 : this.revisionSurfaceIntegerMap.merge(signature, 1, Integer::sum);
   }

   public void updateTime() {
      this.revisionSurfaceIntegerMap.clear();
      this.lastConfirmedCount = -1;
      this.lastSurfaceRevision = Long.MIN_VALUE;
      this.revisionSurfaceValue = 0;
   }

   public int getMax(int revisionSurfaceValue) {
      return Math.max(0, revisionSurfaceValue - this.revisionSurfaceValue);
   }

   public record Signature(
      long mission, long target, long stand, String phase, long surfaceRevision, int confirmedCount, long playerSurfaceCell, long geometry, long access
   ) {
   }
}
