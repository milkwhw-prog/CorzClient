package com.corz.client.utils;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryUtil;
import com.corz.client.utils.KeyBank;
import com.corz.client.utils.Pcm;

public final class KeyClickPlayer {
   private int ticksSinceClick;
   private final Map<Integer, KeyBank> creamyMap;
   private int sourcefErrorValue;
   private final Set<Integer> keySet;
   private int[] memFree;
   private long currentContext;
   private int creamyValue;
   private final int[] errorSourcesTable = new int[8];
   private int errorSourceiValue;

   private void handleCreamCreamy(int index) {
      try {
         KeyBank hybridTopre = KeyBank.getHybridTopre(index);
         this.creamyMap.put(index, hybridTopre);
      } catch (Throwable e) {
         handle("loading " + KeyBank.creamTopreTexts[index] + " failed: " + e);
      } finally {
         this.keySet.remove(index);
      }
   }

   public synchronized void update() {
      if (this.ticksSinceClick <= 20) {
         try {
            this.isCreamyContext();
         } catch (Throwable e) {
            if (this.ticksSinceClick++ < 3) {
               handle("prepare failed: " + e);
            }
         }
      }
   }

   private boolean isCreamyContext() {
      long currentContext = ALC10.alcGetCurrentContext();
      if (currentContext == 0L) {
         return false;
      } else {
         if (currentContext != this.currentContext) {
            this.updateContext();
            if (!this.isErrorSources()) {
               this.ticksSinceClick++;
               return false;
            }

            this.currentContext = currentContext;
         }

         if (this.errorSourceiValue != this.creamyValue) {
            KeyBank creamymap = this.creamyMap.get(this.creamyValue);
            if (creamymap != null) {
               this.handleErrorSourcei(creamymap);
            }
         }

         return this.errorSourceiValue >= 0;
      }
   }

   private void updateBuffersDelete() {
      for (int buffersDeleteValue : this.memFree) {
         if (buffersDeleteValue != 0) {
            AL10.alDeleteBuffers(buffersDeleteValue);
         }
      }

      this.memFree = new int[0];
   }

   public synchronized boolean canFlag(int clickPlayerValue, boolean clickPlayerFlag, float sourcefErrorBound) {
      if (this.ticksSinceClick > 20) {
         return false;
      } else {
         try {
            return this.canSourcefError(clickPlayerValue, clickPlayerFlag, sourcefErrorBound);
         } catch (Throwable e) {
            if (this.ticksSinceClick++ < 3) {
               handle("play failed: " + e);
            }

            return false;
         }
      }
   }

   private void updateContextError() {
      if (this.currentContext != 0L && this.currentContext == ALC10.alcGetCurrentContext()) {
         AL10.alGetError();

         for (int contextErrorValue : this.errorSourcesTable) {
            if (contextErrorValue != 0) {
               AL10.alSourceStop(contextErrorValue);
               AL10.alSourcei(contextErrorValue, 4105, 0);
               AL10.alDeleteSources(contextErrorValue);
            }
         }

         this.updateBuffersDelete();
         AL10.alGetError();
      }
   }

   private boolean isErrorSources() {
      AL10.alGetError();

      for (int i = 0; i < 8; i++) {
         int alGenSources = AL10.alGenSources();
         if (AL10.alGetError() != 0 || alGenSources == 0) {
            for (int j = 0; j < i; j++) {
               AL10.alDeleteSources(this.errorSourcesTable[j]);
            }

            Arrays.fill(this.errorSourcesTable, 0);
            AL10.alGetError();
            return false;
         }

         AL10.alSourcei(alGenSources, 514, 1);
         AL10.alSource3f(alGenSources, 4100, 0.0F, 0.0F, 0.0F);
         AL10.alSourcef(alGenSources, 4129, 0.0F);
         this.errorSourcesTable[i] = alGenSources;
      }

      AL10.alGetError();
      return true;
   }

   private void handleErrorSourcei(KeyBank keyBank) {
      AL10.alGetError();

      for (int errorSourceiValue : this.errorSourcesTable) {
         if (errorSourceiValue != 0) {
            AL10.alSourceStop(errorSourceiValue);
            AL10.alSourcei(errorSourceiValue, 4105, 0);
         }
      }

      this.updateBuffersDelete();
      this.memFree = getMemFree(keyBank.array);
      int alGetError = AL10.alGetError();
      if (alGetError == 0 && this.memFree.length != 0) {
         this.errorSourceiValue = keyBank.errorSourceiValue;
      } else {
         handle("upload failed 0x" + Integer.toHexString(alGetError));
         this.updateBuffersDelete();
         this.errorSourceiValue = -1;
         this.ticksSinceClick++;
      }
   }

   private boolean canSourcefError(int sourcefErrorValue, boolean sourcefErrorFlag, float sourcefErrorBound) {
      if (!this.isCreamyContext()) {
         return false;
      } else {
         KeyBank creamymap = this.creamyMap.get(this.errorSourceiValue);
         if (creamymap == null) {
            return false;
         } else {
            int index = sourcefErrorFlag ? creamymap.getValueForInt(sourcefErrorValue) : creamymap.getValue(sourcefErrorValue);
            if (index >= 0 && index < this.memFree.length && this.memFree[index] != 0) {
               int sourcefErrorValue2 = this.errorSourcesTable[this.sourcefErrorValue];
               this.sourcefErrorValue = (this.sourcefErrorValue + 1) % 8;
               AL10.alGetError();
               AL10.alSourceStop(sourcefErrorValue2);
               AL10.alSourcei(sourcefErrorValue2, 4105, this.memFree[index]);
               AL10.alSourcef(sourcefErrorValue2, 4106, Math.max(0.0F, sourcefErrorBound));
               AL10.alSourcef(sourcefErrorValue2, 4099, 1.0F);
               AL10.alSourcePlay(sourcefErrorValue2);
               int alGetError = AL10.alGetError();
               if (alGetError != 0) {
                  if (this.ticksSinceClick++ < 3) {
                     handle("AL error 0x" + Integer.toHexString(alGetError) + " on play");
                  }

                  return false;
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   private void updateContext() {
      Arrays.fill(this.errorSourcesTable, 0);
      this.memFree = new int[0];
      this.currentContext = 0L;
      this.errorSourceiValue = -1;
      this.sourcefErrorValue = 0;
   }

   public synchronized boolean canContextAlc(int contextAlcValue) {
      try {
         return this.currentContext != 0L && this.errorSourceiValue == contextAlcValue && this.currentContext == ALC10.alcGetCurrentContext();
      } catch (Throwable e) {
         return false;
      }
   }

   public KeyClickPlayer() {
      this.creamyMap = new ConcurrentHashMap<>();
      this.keySet = ConcurrentHashMap.newKeySet();
      this.memFree = new int[0];
      this.errorSourceiValue = -1;
      this.creamyValue = -1;
   }

   private static void handle(String clickPlayerText) {
   }

   public synchronized void updateTicksSinceClick() {
      try {
         this.updateContextError();
      } catch (Throwable e) {
      }

      this.updateContext();
      this.ticksSinceClick = 0;
   }

   private static int[] getMemFree(Pcm[] pcms) {
      int[] ints = new int[pcms.length];

      for (int i = 0; i < pcms.length; i++) {
         Pcm pcm = pcms[i];
         int alGenBuffers = AL10.alGenBuffers();
         ShortBuffer memAllocShort = MemoryUtil.memAllocShort(pcm.samples().length);

         try {
            memAllocShort.put(pcm.samples()).flip();
            AL10.alBufferData(alGenBuffers, pcm.channels() == 2 ? 4355 : 4353, memAllocShort, pcm.rate());
         } finally {
            MemoryUtil.memFree(memAllocShort);
         }

         ints[i] = alGenBuffers;
      }

      return ints;
   }

   public synchronized void handleCreamyCorz(int creamyCorzValue) {
      int creamyValue = KeyBank.getCreamTopre(creamyCorzValue);
      this.creamyValue = creamyValue;
      if (!this.creamyMap.containsKey(creamyValue) && this.keySet.add(creamyValue)) {
         Thread thread = new Thread(() -> this.handleCreamCreamy(creamyValue), "Corz-CreamyKeys");
         thread.setDaemon(true);
         thread.setPriority(1);
         thread.start();
      }
   }
}
