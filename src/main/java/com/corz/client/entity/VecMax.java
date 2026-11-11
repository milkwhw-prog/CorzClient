package com.corz.client.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class VecMax {
   private final LinkedHashMap<Integer, Long> vecPositionMap;
   private final LinkedHashMap<Integer, Long> ownerPositionMap;
   private long lastUpdateTick;
   private final LinkedHashMap<Integer, VecMax.Forecast> vecForecastMap;
   private final LinkedHashMap<Integer, VecMax.Entry> vecEntryMap = new LinkedHashMap<>();

   public List<VecMax.Forecast> getVecMax(int vecValue) {
      ArrayList arrayList = new ArrayList();

      for (VecMax.Entry entry : this.vecEntryMap.values()) {
         if (entry.value != vecValue && entry.maxVecData != null) {
            arrayList.add(entry.maxVecData);
         }
      }

      return arrayList;
   }

   public void updateTime() {
      this.vecEntryMap.clear();
      this.vecForecastMap.clear();
      this.vecPositionMap.clear();
      this.ownerPositionMap.clear();
      this.lastUpdateTick = Long.MIN_VALUE;
   }

   private void handle(long vecValue) {
      Iterator iterator = this.vecPositionMap.entrySet().iterator();

      while (iterator.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator.next();
         if (vecValue - (Long)entry.getValue() > 40L || this.vecForecastMap.size() > 32) {
            this.vecForecastMap.remove(entry.getKey());
            iterator.remove();
         }
      }

      this.ownerPositionMap.entrySet().removeIf(tickx -> vecValue - (Long)tickx.getValue() > 40L || !this.vecForecastMap.containsKey(tickx.getKey()));
   }

   public void handle(int[] vecValues) {
      this.handleVecMax(vecValues, Long.MIN_VALUE);
   }

   public VecMax.Forecast getVecMaxForInt(int vecValue) {
      VecMax.Forecast vecMax = null;
      long minValue = Long.MIN_VALUE;

      for (VecMax.Entry entry : this.vecEntryMap.values()) {
         if (entry.value == vecValue && entry.maxVecData != null && (vecMax == null || entry.time >= minValue)) {
            vecMax = entry.maxVecData;
            minValue = entry.time;
         }
      }

      return vecMax;
   }

   public boolean canVecSquared(Vec3d vec, double vecSquaredFactor, int vecSquaredValue) {
      if (vec == null) {
         return false;
      } else {
         double vecSquaredLimit = vecSquaredFactor * vecSquaredFactor;

         for (VecMax.Entry entry : this.vecEntryMap.values()) {
            if (entry.value != vecSquaredValue && entry.maxVecData != null && entry.maxVecData.landing().squaredDistanceTo(vec) <= vecSquaredLimit) {
               return true;
            }
         }

         return false;
      }
   }

   public VecMax.Forecast getVecMaxVecEntryMap(int vecValue) {
      VecMax.Entry entry = this.vecEntryMap.get(vecValue);
      return entry != null ? entry.maxVecData : this.vecForecastMap.get(vecValue);
   }

   public void handleVecMax(long time, World world) {
      this.handle(time);
      if (world != null && !this.vecEntryMap.isEmpty()) {
         boolean vecFlag = this.lastUpdateTick == Long.MIN_VALUE || time - this.lastUpdateTick >= 2L || time < this.lastUpdateTick;
         Iterator iterator = this.vecEntryMap.entrySet().iterator();

         while (iterator.hasNext()) {
            VecMax.Entry entry = (VecMax.Entry)((Map.Entry)iterator.next()).getValue();
            if (vecFlag || entry.maxVecData == null) {
               if (world.getEntityById(entry.value2) instanceof EnderPearlEntity enderPearlEntity && !enderPearlEntity.isRemoved()) {
                  FloorMathUtilsDirection.Result touchingOwner = FloorMathUtilsDirection.getTouchingOwner(enderPearlEntity, true, 200);
                  if (touchingOwner != null && touchingOwner.directionVecActive && touchingOwner.vec3d3VecMoved != null) {
                     List list = touchingOwner.vecVec3dList;
                     ArrayList arrayList = new ArrayList(4);
                     int vecValue = Math.max(0, list.size() - 3 - 1);

                     for (int i = vecValue; i < list.size(); i++) {
                        arrayList.add((Vec3d)list.get(i));
                     }

                     entry.maxVecData = new VecMax.Forecast(entry.value2, entry.value, touchingOwner.vec3d3VecMoved, Math.max(0, touchingOwner.flightTicks), List.copyOf(arrayList), time);
                     entry.time2 = time;
                  } else {
                     entry.maxVecData = null;
                  }
               } else if (entry.maxVecData != null || time - entry.time > 10L) {
                  if (entry.maxVecData != null) {
                     this.vecForecastMap.put(entry.value2, entry.maxVecData);
                     this.vecPositionMap.put(entry.value2, time);
                     this.ownerPositionMap.put(entry.value2, time);
                  }

                  iterator.remove();
               }
            }
         }

         if (vecFlag) {
            this.lastUpdateTick = time;
         }
      }
   }

   public void handle(int vecValue, int vecValue2, long vecValue3) {
      if (this.vecEntryMap.size() >= 32) {
         Iterator iterator = this.vecEntryMap.keySet().iterator();
         iterator.next();
         iterator.remove();
      }

      this.vecEntryMap.put(vecValue, new VecMax.Entry(vecValue, vecValue2, vecValue3));
   }

   public void handleVecMax(int[] vecValues, long vecValue) {
      if (vecValues != null) {
         for (int vecValuesItem : vecValues) {
            VecMax.Entry entry = this.vecEntryMap.remove(vecValuesItem);
            if (entry != null && entry.maxVecData != null) {
               this.vecForecastMap.put(vecValuesItem, entry.maxVecData);
               this.vecPositionMap.put(vecValuesItem, entry.maxVecData.seenTick());
               if (vecValue != Long.MIN_VALUE) {
                  this.ownerPositionMap.put(vecValuesItem, vecValue);
               }
            }
         }
      }
   }

   public boolean isContainsKey(int containsValue) {
      return this.vecEntryMap.containsKey(containsValue);
   }

   public VecMax.Forecast getOwner(int ownerValue, long ownerValue2, int ownerLimit) {
      VecMax.Forecast owner = null;
      long minValue = Long.MIN_VALUE;

      for (Map.Entry entry : this.ownerPositionMap.entrySet()) {
         long ownerLimit2 = (Long)entry.getValue();
         long ownerLimit3 = ownerValue2 - ownerLimit2;
         if (ownerLimit3 >= 0L && ownerLimit3 <= ownerLimit) {
            VecMax.Forecast forecast = this.vecForecastMap.get(entry.getKey());
            if (forecast != null && forecast.ownerId() == ownerValue && (owner == null || ownerLimit2 >= minValue)) {
               owner = forecast;
               minValue = ownerLimit2;
            }
         }
      }

      return owner;
   }

   public VecMax() {
      this.vecForecastMap = new LinkedHashMap<>();
      this.vecPositionMap = new LinkedHashMap<>();
      this.ownerPositionMap = new LinkedHashMap<>();
      this.lastUpdateTick = Long.MIN_VALUE;
   }

   private static final class Entry {
      final long time;
      VecMax.Forecast maxVecData;
      long time2 = Long.MIN_VALUE;
      final int value;
      final int value2;

      Entry(int value, int value2, long time) {
         this.value2 = value;
         this.value = value2;
         this.time = time;
      }
   }

   public record Forecast(int pearlId, int ownerId, Vec3d landing, int eta, List<Vec3d> tail, long seenTick) {
   }
}
