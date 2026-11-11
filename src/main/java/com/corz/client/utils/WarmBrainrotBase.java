package com.corz.client.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import com.corz.client.utils.CorzclientWarm;
import com.corz.client.utils.LatheLoft;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.ForeShinScale;

public abstract class WarmBrainrotBase {
   private volatile CorzclientWarm classCorzclientWarm;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private volatile CorzclientWarm classCorzclient;
   private volatile CorzclientWarm classThrowableThread;
   private volatile Throwable throwable;
   private static final ExecutorService threadExecutor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "corz-brainrot-warm");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });

   public abstract String formatTungDinString();

   public float getAmount(CorzclientWarm corzclientWarm) {
      return corzclientWarm.corzclientWarmAmount + 0.42F * (corzclientWarm.boundsMaxY - corzclientWarm.corzclientWarmAmount);
   }

   public String formatNoteBlock() {
      return null;
   }

   public final CorzclientWarm getClassThrowableThread() {
      return this.classThrowableThread;
   }

   public final Throwable getThrowable() {
      return this.throwable;
   }

   public abstract String formatTungDin();

   public final synchronized CorzclientWarm getClassClassCorzclientWarm() {
      if (this.classCorzclientWarm == null) {
         this.classCorzclientWarm = this.getClassForFloatIntArray(1.0F, null);
      }

      return this.classCorzclientWarm;
   }

   public int getValue() {
      return 1;
   }

   protected abstract void handleShinFoot(LatheLoft latheLoft);

   public final synchronized CorzclientWarm getCorzclient() {
      if (this.classCorzclient == null) {
         this.classCorzclient = this.getClassForFloatIntArray(0.5F, this.getClassClassCorzclientWarm().corzclientValues);
      }

      return this.classCorzclient;
   }

   public final CorzclientWarm getClassCorzclient() {
      return this.classCorzclient;
   }

   public float getAmountFloat() {
      return 1.0F;
   }

   public abstract void handleShinProgress(PoseProgress poseProgress, ForeShinScale ifAbsent);

   protected int getValueInt() {
      return 512;
   }

   private CorzclientWarm getClassForFloatIntArray(float classValue, int[] classValues) {
      LatheLoft latheLoft = new LatheLoft(this.getValueInt(), this.getValueInt(), classValue, classValues != null);
      this.handleShinFoot(latheLoft);
      return latheLoft.getMax(this.formatTungDin(), classValues);
   }

   public final void updateThrowableThread() {
      if (this.classThrowableThread == null && this.throwable == null && this.atomicBoolean.compareAndSet(false, true)) {
         threadExecutor.execute(() -> {
            try {
               this.getCorzclientClassThrowableThread();
            } catch (Throwable e) {
               this.throwable = e;
            }
         });
      }
   }

   public final synchronized CorzclientWarm getCorzclientClassThrowableThread() {
      if (this.classThrowableThread == null) {
         this.getCorzclient();
         this.classThrowableThread = this.getClassForFloatIntArray(0.25F, this.getClassClassCorzclientWarm().corzclientValues);
      }

      return this.classThrowableThread;
   }

   public final CorzclientWarm getClassCorzclientWarm() {
      return this.classCorzclientWarm;
   }
}
