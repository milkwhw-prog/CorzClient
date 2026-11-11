package com.corz.client.utils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.LongPredicate;
import com.corz.client.utils.ReusableDescentData;

public final class DescentReusable {
   private final long reusableDescentTime;
   private long next;
   private final LinkedHashSet<Long> linkedHashSet = new LinkedHashSet<>();
   private final LinkedHashSet<Long> linkedHashLinkedHashSet = new LinkedHashSet<>();
   private ReusableDescentData reusableDescentData = ReusableDescentData.INSTANCE;
   private DescentReusable.Kind descentStairType;
   private boolean descentReusableActive;
   private final LongPredicate longPredicate;
   private int maxDescentBound;

   public boolean isRemove(long descentReusableValue) {
      return this.linkedHashSet.remove(descentReusableValue);
   }

   public boolean canLinkedHash(long linkedHashValue, boolean linkedHashFlag, boolean linkedHashFlag2) {
      return linkedHashFlag && linkedHashFlag2 && !this.linkedHashLinkedHashSet.contains(linkedHashValue) && !this.longPredicate.test(linkedHashValue)
         ? this.linkedHashSet.add(linkedHashValue)
         : false;
   }

   public void handleReusableDescent(ReusableDescentData reusableDescentData) {
      this.reusableDescentData = reusableDescentData == null ? ReusableDescentData.INSTANCE : reusableDescentData;
   }

   public long getReusableDescentTime() {
      return this.reusableDescentTime;
   }

   public boolean isReusableActive() {
      return this.descentReusableActive;
   }

   public DescentReusable(long time, long next, DescentReusable.Kind descentStairType, int descentReusableBound, LongPredicate longPredicate) {
      this.reusableDescentTime = time;
      this.next = next;
      this.descentStairType = descentStairType;
      this.maxDescentBound = Math.max(0, descentReusableBound);
      this.longPredicate = longPredicate;
   }

   public int getMaxDescentBound() {
      return this.maxDescentBound;
   }

   public ReusableDescentData getReusableDescentData() {
      return this.reusableDescentData;
   }

   public boolean isContains(long containsValue) {
      return this.linkedHashSet.contains(containsValue);
   }

   public DescentReusable(long descentReusableValue, long descentReusableValue2, DescentReusable.Kind kind2, int descentReusableBound) {
      this(descentReusableValue, descentReusableValue2, kind2, descentReusableBound, cell -> false);
   }

   public void handleMax(long next, int descentReusableBound) {
      this.next = next;
      this.maxDescentBound = Math.max(this.maxDescentBound, descentReusableBound);
   }

   public DescentReusable getLinkedHash(long linkedHashValue) {
      this.linkedHashLinkedHashSet.add(linkedHashValue);
      return this;
   }

   public double getLinkedHashRatio() {
      return this.linkedHashSet.isEmpty() ? this.maxDescentBound : (double)this.maxDescentBound / this.linkedHashSet.size();
   }

   public DescentReusable.Kind getDescentStairType() {
      return this.descentStairType;
   }

   public Set<Long> getCopyOf() {
      return Set.copyOf(this.linkedHashSet);
   }

   public void handleStairDescent(int stairDescentBound) {
      this.descentStairType = DescentReusable.Kind.PLATFORM;
      this.maxDescentBound = Math.max(this.maxDescentBound, Math.max(0, stairDescentBound));
   }

   public void updateMaxFlag() {
      this.descentReusableActive = true;
      this.maxDescentBound = 0;
   }

   public long getNext() {
      return this.next;
   }

   public static enum Kind {
      PILLAR,
      STAIR,
      BRIDGE,
      PLATFORM,
      SUPPORT,
      DIRECTIONAL_CONTEXT,
      DESCENT;
   }
}
