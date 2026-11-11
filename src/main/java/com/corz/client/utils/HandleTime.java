package com.corz.client.utils;

import com.corz.client.world.BoxLengthUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

public final class HandleTime {
   private boolean supplierActive;
   private final Map<Integer, Long> positionMap;
   public static final int UNUSED_ONE = 1;
   private float smoothedValue;
   private final Map<Long, Long> windowedTimes = new HashMap<>();
   private float supplierAmount;
   private final Map<Long, Long> shortLivedTimes;
   private long cooldownEndMs;
   private final Map<Long, Long> twoSecondTimes;
   private long windowMs;
   private final Map<Long, Long> fiveSecondTimes;
   private long supplierValue;
   private final DoubleSupplier doubleSupplier;
   private final Map<Integer, HandleTime.Hits> hitsMap = new HashMap<>();

   public void handle(long longValue, long botCrystalValue) {
      this.shortLivedTimes.put(longValue, botCrystalValue);
      this.twoSecondTimes.remove(longValue);
   }

   public boolean canTick(int n, long longValue, int limit, long longValue2) {
      if (this.canFlag(n, longValue2)) {
         return false;
      } else {
         HandleTime.Hits hits = this.hitsMap.get(n);
         if (hits == null) {
            return true;
         } else if (longValue - hits.lastTick() < limit) {
            return false;
         } else if (hits.count() >= 3) {
            this.hitsMap.remove(n);
            this.positionMap.put(n, longValue2);
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean canTimeFlag(long limit) {
      return !this.supplierActive || limit >= this.cooldownEndMs;
   }

   public boolean canFlag(long longValue, long obsidianAliveValue, int n) {
      Long long2 = this.shortLivedTimes.get(longValue);
      return long2 == null || obsidianAliveValue - long2 >= n;
   }

   public Set<Long> getSet() {
      return new HashSet<>(this.shortLivedTimes.keySet());
   }

   public int getValue(int n) {
      HandleTime.Hits hits = this.hitsMap.get(n);
      return hits == null ? 0 : hits.count();
   }

   public HandleTime() {
      this(() -> ThreadLocalRandom.current().nextDouble());
   }

   public void handle(long longValue, long swingHandValue, long longValue3, float f, float f2) {
      this.handle(longValue, swingHandValue, longValue3, f, f2, false);
   }

   public void handle(long longValue, long entityTime, float f, float f2) {
      this.handleSupplierAmount(longValue, entityTime, f, f2, false);
   }

   public void handle(long longValue, long swingHandValue, long longValue3, float f, float f2, boolean flag) {
      this.windowedTimes.put(longValue, swingHandValue);
      this.fiveSecondTimes.put(longValue, swingHandValue);
      this.handleSupplierAmount(swingHandValue, longValue3, f, f2, flag);
   }

   public long getWindowMs() {
      return this.windowMs;
   }

   public long getTime(long longValue) {
      Long long2 = this.shortLivedTimes.get(longValue);
      return long2 == null ? Long.MIN_VALUE : long2;
   }

   static int getValue(double d) {
      return d < 0.05 ? 4 : (d < 0.3 ? 3 : 2);
   }

   public void handleSupplierAmount(long time, long supplierValue, float amount, float f2, boolean supplierFlag) {
      this.supplierActive = true;
      this.smoothedValue = amount;
      this.supplierAmount = f2;
      this.supplierValue = time;
      this.cooldownEndMs = supplierValue + (supplierFlag ? 1 : getValue(this.doubleSupplier.getAsDouble()));
   }

   public void handleTime(long limit) {
      this.windowMs = limit > 0L ? limit : 1000L;
   }

   public void handleForLong(long longValue, long livingVecValue) {
      this.shortLivedTimes.remove(longValue);
      this.twoSecondTimes.put(longValue, livingVecValue);
   }

   public boolean canTick(int n, long longValue, int limit) {
      HandleTime.Hits hits = this.hitsMap.get(n);
      return hits != null && longValue - hits.lastTick() < limit;
   }

   public void handle(long longValue) {
      this.windowedTimes.remove(longValue);
   }

   public void updateAmountTime() {
      this.windowedTimes.clear();
      this.hitsMap.clear();
      this.positionMap.clear();
      this.shortLivedTimes.clear();
      this.twoSecondTimes.clear();
      this.fiveSecondTimes.clear();
      this.supplierActive = false;
      this.smoothedValue = 0.0F;
      this.supplierAmount = 0.0F;
      this.supplierValue = 0L;
      this.cooldownEndMs = 0L;
   }

   HandleTime(DoubleSupplier doubleSupplier) {
      this.positionMap = new HashMap<>();
      this.shortLivedTimes = new HashMap<>();
      this.twoSecondTimes = new HashMap<>();
      this.fiveSecondTimes = new HashMap<>();
      this.windowMs = 1000L;
      this.doubleSupplier = doubleSupplier;
   }

   public Set<Long> getWindowedTimes() {
      return new HashSet<>(this.windowedTimes.keySet());
   }

   public void handleWindowedTimes(long longValue, long playerPosValue) {
      this.windowedTimes.values().removeIf(long2 -> longValue - long2 > this.windowMs);
      this.hitsMap.values().removeIf(tickx -> playerPosValue - tickx.lastTick() > 40L);
      this.positionMap.values().removeIf(nowMsx -> longValue - nowMsx > 5000L);
      this.shortLivedTimes.values().removeIf(long3 -> playerPosValue - long3 > 40L);
      this.twoSecondTimes.values().removeIf(nowMsx -> longValue - nowMsx > 2000L);
      this.fiveSecondTimes.values().removeIf(nowMsx -> longValue - nowMsx > 5000L);
   }

   public void handle(int n, long longValue) {
      HandleTime.Hits hits = this.hitsMap.get(n);
      this.hitsMap.put(n, new HandleTime.Hits(longValue, hits == null ? 1 : hits.count() + 1));
   }

   public boolean canFlag(int n, long longValue) {
      Long long2 = this.positionMap.get(n);
      if (long2 == null) {
         return false;
      } else if (longValue - long2 > 5000L) {
         this.positionMap.remove(n);
         return false;
      } else {
         return true;
      }
   }

   public boolean canTime(long longValue, long nowMs) {
      Long long2 = this.windowedTimes.get(longValue);
      if (long2 == null) {
         return false;
      } else if (nowMs - long2 > this.windowMs) {
         this.windowedTimes.remove(longValue);
         return false;
      } else {
         return true;
      }
   }

   public boolean canAmountTime(float f, float f2, long longValue) {
      if (!this.supplierActive) {
         return false;
      } else {
         double d = BoxLengthUtils.getAmount(this.smoothedValue, this.supplierAmount, f, f2);
         return d < 0.02 && longValue - this.supplierValue < 120L;
      }
   }

   public boolean canFlag(long longValue, long longValue2) {
      Long long2 = this.fiveSecondTimes.get(longValue);
      return long2 != null && longValue2 - long2 <= 5000L;
   }

   public boolean canFlagForLong(long longValue, long nowMs) {
      Long long2 = this.twoSecondTimes.get(longValue);
      if (long2 == null) {
         return false;
      } else if (nowMs - long2 > 2000L) {
         this.twoSecondTimes.remove(longValue);
         return false;
      } else {
         return true;
      }
   }

   private record Hits(long lastTick, int count) {
   }
}
