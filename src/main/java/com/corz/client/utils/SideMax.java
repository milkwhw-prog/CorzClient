package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.corz.client.utils.RotationAccelerationLimiter;

public final class SideMax {
   private float nextSideYaw;
   private int lastSampleTick;
   private int sideCount;
   private int recordedSampleCount;
   private final SideMax.Sample[] sideSamples = new SideMax.Sample[128];
   private float sideAmount;
   private int sideValue;
   private final Map<Long, SideMax.Rec> sideRecMap;
   private long longValue;
   private SideMax.Rec maxSide;

   public int getAmountSide(long longValue, String sideText, String sideText2, long longValue2, int value) {
      SideMax.Sample sample = this.getDataRecordedSampleCount();
      boolean sideFlag = this.sideValue >= 0 && this.longValue == longValue;
      SideMax.Rec maxSide = new SideMax.Rec();
      maxSide.sideValue = ++this.sideCount;
      maxSide.longValue = longValue;
      maxSide.endSampleTime = longValue2;
      maxSide.recText = sideText == null ? "" : sideText;
      maxSide.sideText = sideText2 == null ? "" : sideText2;
      maxSide.sideAmount = sideFlag ? this.nextSideYaw : (sample == null ? 0.0F : sample.yaw());
      maxSide.samplePitch = sideFlag ? this.sideAmount : (sample == null ? 0.0F : sample.pitch());
      maxSide.endYaw = sample == null ? maxSide.sideAmount : sample.yaw();
      maxSide.recAmount = sample == null ? maxSide.samplePitch : sample.pitch();
      maxSide.recValue = sideFlag ? value - this.sideValue : 0;
      maxSide.endMoving = sample != null && sample.moving();
      maxSide.sideActive = sample != null && sample.sprinting();
      maxSide.recActive = sample != null && sample.sneaking();
      maxSide.ticksSinceLastSample = this.lastSampleTick < 0 ? -1 : value - this.lastSampleTick;
      this.handleMaxTick(maxSide, sideFlag ? this.sideValue : value);
      this.lastSampleTick = value;
      this.sideRecMap.put(longValue, maxSide);

      while (this.sideRecMap.size() > 64) {
         Iterator iterator = this.sideRecMap.keySet().iterator();
         iterator.next();
         iterator.remove();
      }

      this.maxSide = maxSide;
      return maxSide.sideValue;
   }

   public long getTime() {
      return this.sideValue < 0 ? 0L : this.longValue;
   }

   public SideMax() {
      this.lastSampleTick = -1;
      this.sideValue = -1;
      this.sideRecMap = new LinkedHashMap<>();
   }

   public SideMax.Sample getDataRecordedSampleCount() {
      return this.recordedSampleCount == 0 ? null : this.sideSamples[Math.floorMod(this.recordedSampleCount - 1, 128)];
   }

   public int getSideMax() {
      return this.maxSide == null ? 0 : this.maxSide.sideValue;
   }

   public void update() {
      this.sideValue = -1;
      this.longValue = 0L;
   }

   public float getMaxSide() {
      return this.maxSide == null ? 0.0F : RotationAccelerationLimiter.wrapDegrees(this.maxSide.endYaw - this.maxSide.sideAmount);
   }

   public List<SideMax.Sample> getItems() {
      ArrayList arrayList = new ArrayList(Math.min(this.recordedSampleCount, 128));
      int sideValue = Math.max(0, this.recordedSampleCount - 128);

      for (int i = sideValue; i < this.recordedSampleCount; i++) {
         SideMax.Sample sample = this.sideSamples[Math.floorMod(i, 128)];
         if (sample != null) {
            arrayList.add(sample);
         }
      }

      return arrayList;
   }

   public void addSample(SideMax.Sample sample) {
      if (sample != null) {
         this.sideSamples[Math.floorMod(this.recordedSampleCount, 128)] = sample;
         this.recordedSampleCount++;
      }
   }

   public String formatSideAmount(long sideValue, String sideText) {
      SideMax.Rec maxSide = this.sideRecMap.remove(sideValue);
      if (maxSide == null) {
         maxSide = this.maxSide != null && this.maxSide.longValue == sideValue ? this.maxSide : new SideMax.Rec();
      }

      this.maxSide = maxSide;
      return String.format(
         Locale.ROOT,
         "id=%d target=%d block=%s yawStart=%.1f yawAtClick=%.1f deltaYaw=%.1f pitchStart=%.1f pitchAtClick=%.1f deltaPitch=%.1f turnTicks=%d peakYawVelocity=%.1f peakPitchVelocity=%.1f rotationAcceleration=%.1f ticksSinceLastClick=%d moving=%b sprinting=%b sneaking=%b support=%d face=%s result=%s",
         maxSide.sideValue,
         maxSide.longValue,
         maxSide.recText,
         maxSide.sideAmount,
         maxSide.endYaw,
         RotationAccelerationLimiter.wrapDegrees(maxSide.endYaw - maxSide.sideAmount),
         maxSide.samplePitch,
         maxSide.recAmount,
         RotationAccelerationLimiter.wrapDegrees(maxSide.recAmount - maxSide.samplePitch),
         maxSide.recValue,
         maxSide.maxAbsTick,
         maxSide.maxAbs,
         maxSide.maxTickDeviation,
         maxSide.ticksSinceLastSample,
         maxSide.endMoving,
         maxSide.sideActive,
         maxSide.recActive,
         maxSide.endSampleTime,
         maxSide.sideText,
         sideText == null ? "" : sideText
      );
   }

   public void handleAmountPitch(long longValue, int value) {
      if (this.sideValue >= 0 && this.longValue == longValue && value - this.sideValue > 128) {
         this.update();
      }

      if (this.sideValue < 0 || this.longValue != longValue) {
         SideMax.Sample next = this.getNext(value);
         if (next == null) {
            next = this.getDataRecordedSampleCount();
         }

         this.longValue = longValue;
         this.sideValue = value;
         this.nextSideYaw = next == null ? 0.0F : next.yaw();
         this.sideAmount = next == null ? 0.0F : next.pitch();
      }
   }

   private SideMax.Sample getNext(int sideValue) {
      for (SideMax.Sample next : this.getItems()) {
         if (next.tick() == sideValue) {
            return next;
         }
      }

      return null;
   }

   public void updateAmountSide() {
      Arrays.fill(this.sideSamples, null);
      this.recordedSampleCount = 0;
      this.sideCount = 0;
      this.lastSampleTick = -1;
      this.longValue = 0L;
      this.sideValue = -1;
      this.nextSideYaw = 0.0F;
      this.sideAmount = 0.0F;
      this.sideRecMap.clear();
      this.maxSide = null;
   }

   private void handleMaxTick(SideMax.Rec rec, int tickValue) {
      SideMax.Sample sample = null;
      float naN = Float.NaN;
      float naN2 = Float.NaN;

      for (SideMax.Sample getItem : this.getItems()) {
         if (getItem.tick() >= tickValue) {
            if (sample != null) {
               int tickDivisor = Math.max(1, getItem.tick() - sample.tick());
               float planValuesItem = RotationAccelerationLimiter.wrapDegrees(getItem.yaw() - sample.yaw()) / tickDivisor;
               float tickValue3 = RotationAccelerationLimiter.wrapDegrees(getItem.pitch() - sample.pitch()) / tickDivisor;
               rec.maxAbsTick = Math.max(rec.maxAbsTick, Math.abs(planValuesItem));
               rec.maxAbs = Math.max(rec.maxAbs, Math.abs(tickValue3));
               if (!Float.isNaN(naN)) {
                  rec.maxTickDeviation = Math.max(rec.maxTickDeviation, Math.max(Math.abs(planValuesItem - naN), Math.abs(tickValue3 - naN2)));
               }

               naN = planValuesItem;
               naN2 = tickValue3;
            }

            sample = getItem;
         }
      }
   }

   public int getSideMaxMaxSide() {
      return this.maxSide == null ? 0 : this.maxSide.recValue;
   }

   private static final class Rec {
      long longValue;
      String sideText;
      float maxTickDeviation;
      int ticksSinceLastSample;
      float samplePitch;
      int sideValue;
      float sideAmount;
      String recText = "";
      float maxAbs;
      boolean sideActive;
      boolean recActive;
      float recAmount;
      int recValue;
      boolean endMoving;
      float maxAbsTick;
      float endYaw;
      long endSampleTime;

      private Rec() {
         this.sideText = "";
      }
   }

   public record Sample(int tick, float yaw, float pitch, boolean moving, boolean sprinting, boolean sneaking) {
   }
}
