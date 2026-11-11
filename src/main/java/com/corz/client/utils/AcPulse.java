package com.corz.client.utils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class AcPulse {
   private final AtomicInteger atomicInteger;
   static final int PULSE_INCREMENT = 1;
   private final AtomicBoolean atomicBoolean;
   private AcPulse.State pulseState;
   private final AtomicBoolean atomicIntegerAtomicBoolean;
   private final AtomicInteger atomicIntegerAtomicInteger = new AtomicInteger();

   public AcPulse.Change getAtomicInteger() {
      if (this.atomicBoolean.getAndSet(false)) {
         this.pulseState = AcPulse.State.UNKNOWN;
         this.atomicIntegerAtomicInteger.set(0);
         this.atomicInteger.set(0);
         this.atomicIntegerAtomicBoolean.set(false);
         return AcPulse.Change.NONE;
      } else if (this.atomicInteger.get() >= 3) {
         return this.getWentState(AcPulse.State.ON);
      } else {
         return this.atomicIntegerAtomicInteger.get() >= 6 ? this.getWentState(AcPulse.State.OFF) : AcPulse.Change.NONE;
      }
   }

   private AcPulse.Change getWentState(AcPulse.State state) {
      if (this.pulseState == state) {
         return AcPulse.Change.NONE;
      } else {
         this.pulseState = state;
         return state == AcPulse.State.ON ? AcPulse.Change.WENT_ON : AcPulse.Change.WENT_OFF;
      }
   }

   public AcPulse.State getState() {
      return this.pulseState;
   }

   public void updateAtomic() {
      this.atomicBoolean.set(true);
   }

   public void updateAtomicInteger() {
      this.atomicIntegerAtomicBoolean.set(true);
      this.atomicIntegerAtomicInteger.set(0);
      this.atomicInteger.incrementAndGet();
   }

   public void updateAtomicIntegerAtomicIntegerAtomicBoolean() {
      if (this.atomicIntegerAtomicBoolean.getAndSet(false)) {
         this.atomicIntegerAtomicInteger.set(0);
      } else {
         if (this.atomicIntegerAtomicInteger.incrementAndGet() >= 1) {
            this.atomicInteger.set(0);
         }
      }
   }

   public AcPulse() {
      this.atomicInteger = new AtomicInteger();
      this.atomicIntegerAtomicBoolean = new AtomicBoolean();
      this.atomicBoolean = new AtomicBoolean();
      this.pulseState = AcPulse.State.UNKNOWN;
   }

   public static enum Change {
      NONE,
      WENT_ON,
      WENT_OFF;
   }

   public static enum State {
      UNKNOWN,
      ON,
      OFF;
   }
}
