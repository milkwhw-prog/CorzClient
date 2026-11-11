package com.corz.client.utils;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeMesh;

public final class KnifeFinishTextures {
   private static long useCounter;
   private static final KnifeRig[] knifeRigs = new KnifeRig[]{KnifeRig.M9_BAYONET_TP, KnifeRig.STILETTO_TP, KnifeRig.BUTTERFLY_TP, KnifeRig.INSTANCE};
   private static final int SINGLE_STEP = 1;
   private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
      1, 1, 0L, TimeUnit.MILLISECONDS, new KnifeFinishTextures.NewestFirst(), runnable -> {
         Thread thread = new Thread(runnable, "Corz knife finish baker");
         thread.setDaemon(true);
         thread.setPriority(1);
         return thread;
      }
   );
   private static final KnifeFinishTextures.Entry[][] entrys = new KnifeFinishTextures.Entry[knifeRigs.length][];
   private static GpuSampler sampler;
   private static GpuSampler sampler3SamplerLinear;

   private static KnifeFinishTextures.Entry getEntrysRigs(int index, KnifeFinish.Finish finish) {
      KnifeFinishTextures.Entry[] entries = entrys[index];
      if (entries == null || finish.getPaintsSteelsValue() >= entries.length) {
         entries = Arrays.copyOf(
            entries == null ? new KnifeFinishTextures.Entry[0] : entries, Math.max(finish.getPaintsSteelsValue() + 1, KnifeFinish.getPaintsSteels().size())
         );
         entrys[index] = entries;
      }

      KnifeFinishTextures.Entry entrysRigs = entries[finish.getPaintsSteelsValue()];
      if (entrysRigs == null) {
         entrysRigs = entries[finish.getPaintsSteelsValue()] = new KnifeFinishTextures.Entry(knifeRigs[index], finish);
      }

      return entrysRigs;
   }

   private KnifeFinishTextures() {
   }

   private static void handleClose(NativeImage[] nativeImages) {
      if (nativeImages != null) {
         for (NativeImage nativeImage : nativeImages) {
            if (nativeImage != null) {
               nativeImage.close();
            }
         }
      }
   }

   private static void handleCorzclientService(KnifeFinishTextures.Entry entry) {
      entry.corzclientValue = 1;

      try {
         EXECUTOR_SERVICE.execute(() -> handleCorzclientFinish(entry));
      } catch (RuntimeException e2) {
         entry.corzclientValue = 3;
      }
   }

   private static void handleDestroyCorzclient(TextureManager textureManager, KnifeFinishTextures.Entry entry) {
      while (true) {
         int destroyCorzclientCounter = 0;
         KnifeFinishTextures.Entry entry2 = null;

         for (KnifeFinishTextures.Entry[] entries : entrys) {
            if (entries != null) {
               for (KnifeFinishTextures.Entry entry3 : entries) {
                  if (entry3 != null && entry3.corzclientValue == 2) {
                     destroyCorzclientCounter++;
                     if (entry3 != entry && (entry2 == null || entry3.time < entry2.time)) {
                        entry2 = entry3;
                     }
                  }
               }
            }
         }

         if (destroyCorzclientCounter <= 8 || entry2 == null) {
            return;
         }

         entry2.skin = null;
         entry2.flag = false;
         entry2.corzclientValue = 0;
         textureManager.destroyTexture(entry2.corzclient);
         if (entry2.finish.isCompact()) {
            textureManager.destroyTexture(entry2.corzclient2);
         }
      }
   }

   private static NativeImage[] getArgbColor(int[][] ints, int argbColorValue, int argbColorValue2) {
      NativeImage[] nativeImages = new NativeImage[ints.length];

      try {
         for (int i = 0; i < ints.length; i++) {
            int argbColorLimit = argbColorValue >> i;
            int argbColorLimit2 = argbColorValue2 >> i;
            NativeImage nativeImage = new NativeImage(argbColorLimit, argbColorLimit2, false);
            nativeImages[i] = nativeImage;
            int[] argbColorValues = ints[i];

            for (int j = 0; j < argbColorLimit2; j++) {
               for (int k = 0; k < argbColorLimit; k++) {
                  nativeImage.setColorArgb(k, j, argbColorValues[j * argbColorLimit + k]);
               }
            }
         }

         return nativeImages;
      } catch (RuntimeException e) {
         handleClose(nativeImages);
         throw e;
      }
   }

   public static void handleCorzclientService(KnifeRig knifeRig, KnifeFinish.Finish finish) {
      if (knifeRig != null && finish != null && finish.getPaintsSteelsValue() >= 0 && !finish.isCompactFlag()) {
         int rigsKnife = getRigsKnife(knifeRig);
         if (rigsKnife >= 0) {
            KnifeFinishTextures.Entry entrysRigs = getEntrysRigs(rigsKnife, finish);
            entrysRigs.nanoTime = System.nanoTime();
            if (entrysRigs.corzclientValue == 0) {
               handleCorzclientService(entrysRigs);
            } else if (entrysRigs.corzclientValue == 1 && !entrysRigs.flag) {
               try {
                  EXECUTOR_SERVICE.execute(() -> handleCorzclientFinish(entrysRigs));
               } catch (RuntimeException e) {
               }
            }
         }
      }
   }

   public static KnifeFinishTextures.Skin getTimeRigs(KnifeRig knifeRig, KnifeFinish.Finish finish) {
      if (knifeRig != null && finish != null && finish.getPaintsSteelsValue() >= 0 && !finish.isCompactFlag()) {
         int rigsKnife = getRigsKnife(knifeRig);
         if (rigsKnife < 0) {
            return null;
         } else {
            KnifeFinishTextures.Entry entrysRigs = getEntrysRigs(rigsKnife, finish);
            long nanoTime = System.nanoTime();
            entrysRigs.time = ++useCounter;
            entrysRigs.nanoTime = nanoTime;
            KnifeFinishTextures.Skin skin = entrysRigs.skin;
            if (skin == null && entrysRigs.corzclientValue == 0) {
               handleCorzclientService(entrysRigs);
            }

            for (int i = 0; i < knifeRigs.length; i++) {
               if (i != rigsKnife && knifeRigs[i] != KnifeRig.BUTTERFLY_TP) {
                  KnifeFinishTextures.Entry entry = getEntrysRigs(i, finish);
                  entry.time = useCounter;
                  entry.nanoTime = nanoTime;
                  if (skin != null && entry.skin == null && entry.corzclientValue == 0) {
                     handleCorzclientService(entry);
                  }
               }
            }

            return skin;
         }
      } else {
         return null;
      }
   }

   private static GpuSampler getSamplerLinear(GpuDevice gpuDevice, boolean samplerLinearFlag) {
      GpuSampler samplerLinear = samplerLinearFlag ? sampler3SamplerLinear : sampler;
      if (samplerLinear != null) {
         return samplerLinear;
      } else {
         GpuSampler sampler3;
         try {
            int samplerLinearValue = Math.max(1, Math.min(8, gpuDevice.getMaxSupportedAnisotropy()));
            sampler3 = gpuDevice.createSampler(
               AddressMode.CLAMP_TO_EDGE,
               AddressMode.CLAMP_TO_EDGE,
               FilterMode.LINEAR,
               FilterMode.LINEAR,
               samplerLinearValue,
               samplerLinearFlag ? OptionalDouble.empty() : OptionalDouble.of(0.0)
            );
         } catch (RuntimeException e) {
            return RenderSystem.getSamplerCache().get(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, samplerLinearFlag);
         }

         if (samplerLinearFlag) {
            sampler3SamplerLinear = sampler3;
         } else {
            sampler = sampler3;
         }

         return sampler3;
      }
   }

   private static int getRigsKnife(KnifeRig knifeRig) {
      for (int i = 0; i < knifeRigs.length; i++) {
         if (knifeRigs[i] == knifeRig) {
            return i;
         }
      }

      return -1;
   }

   public static boolean canEntrysCorzclient(KnifeRig knifeRig, KnifeFinish.Finish finish) {
      if (knifeRig != null && finish != null && finish.getPaintsSteelsValue() >= 0 && !finish.isCompactFlag()) {
         int rigsKnife = getRigsKnife(knifeRig);
         if (rigsKnife < 0) {
            return false;
         } else {
            KnifeFinishTextures.Entry[] entries = entrys[rigsKnife];
            if (entries != null && finish.getPaintsSteelsValue() < entries.length) {
               KnifeFinishTextures.Entry entry = entries[finish.getPaintsSteelsValue()];
               return entry != null && entry.corzclientValue == 3;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private static void handleCorzclientFinish(KnifeFinishTextures.Entry entry) {
      if (!entry.flag && entry.corzclientValue == 1) {
         entry.flag = true;
         if (System.nanoTime() - entry.nanoTime > 500000000L) {
            entry.flag = false;
            entry.corzclientValue = 0;
         } else {
            NativeImage[] nativeImages = null;
            NativeImage[] nativeImages2 = null;

            try {
               KnifeFinish.Baked baked = KnifeFinish.getBaked(entry.knifeRig, entry.finish);
               if (baked == null) {
                  entry.corzclientValue = 3;
                  return;
               }

               nativeImages = getArgbColor(baked.corzclientFinishInts, baked.textureWidth, baked.textureHeight);
               nativeImages2 = baked.isFlag() ? getArgbColor(baked.bakedInts, baked.bakedValue, baked.corzclientFinishValue) : null;
               KnifeFinish.Skin[] skins = baked.skins;
               MinecraftClient instance = MinecraftClient.getInstance();
               if (instance == null) {
                  handleClose(nativeImages);
                  handleClose(nativeImages2);
                  entry.corzclientValue = 3;
                  return;
               }

               final NativeImage[] nativeImages3 = nativeImages;
               final NativeImage[] nativeImages4 = nativeImages2;
               instance.execute(() -> handleCorzclientTexture(entry, nativeImages3, nativeImages4, skins));
            } catch (Throwable e2) {
               handleClose(nativeImages);
               handleClose(nativeImages2);
               entry.corzclientValue = 3;
               System.err.println("[C" + entry.finish.corzclientLoadingText + " failed to bake: " + e2);
            }
         }
      }
   }

   private static void handleCorzclientTexture(KnifeFinishTextures.Entry entry, NativeImage[] nativeImages, NativeImage[] nativeImages2, KnifeFinish.Skin[] skins) {
      TextureManager textureManager = null;

      try {
         textureManager = MinecraftClient.getInstance().getTextureManager();
         textureManager.registerTexture(entry.corzclient, new KnifeFinishTextures.FinishTexture(entry.corzclient.toString(), nativeImages));
         RenderLayer renderLayer = null;
         if (nativeImages2 != null) {
            textureManager.registerTexture(entry.corzclient2, new KnifeFinishTextures.FinishTexture(entry.corzclient2.toString(), nativeImages2));
            renderLayer = KnifeMesh.getEyes(entry.corzclient2);
         }

         entry.skin = new KnifeFinishTextures.Skin(KnifeMesh.getNoCull(entry.corzclient), renderLayer, skins);
         entry.corzclientValue = 2;
         handleDestroyCorzclient(textureManager, entry);
      } catch (Throwable e2) {
         entry.skin = null;
         entry.corzclientValue = 3;
         if (textureManager != null) {
            try {
               textureManager.destroyTexture(entry.corzclient);
               textureManager.destroyTexture(entry.corzclient2);
            } catch (Throwable e3) {
            }
         }

         System.err.println("[C" + entry.finish.corzclientLoadingText + " failed to upload: " + e2);
      } finally {
         handleClose(nativeImages);
         handleClose(nativeImages2);
      }
   }

   static String formatRoot(String rootText) {
      StringBuilder stringBuilder = new StringBuilder();

      for (char rootLimit : rootText.toLowerCase(Locale.ROOT).toCharArray()) {
         stringBuilder.append((rootLimit < 'a' || rootLimit > 'z') && (rootLimit < '0' || rootLimit > '9') ? '_' : rootLimit);
      }

      return stringBuilder.toString();
   }

   private static final class Entry {
      volatile long nanoTime;
      volatile KnifeFinishTextures.Skin skin;
      final KnifeFinish.Finish finish;
      final Identifier corzclient;
      final KnifeRig knifeRig;
      long time;
      final Identifier corzclient2;
      volatile boolean flag;
      volatile int corzclientValue = 0;

      Entry(KnifeRig knifeRig, KnifeFinish.Finish finish2) {
         this.knifeRig = knifeRig;
         this.finish = finish2;
         String entryText = "knife_finish/"
            + KnifeFinishTextures.formatRoot(knifeRig.getParentParts(0).model())
            + "/"
            + KnifeFinishTextures.formatRoot(finish2.corzclientLoadingText);
         this.corzclient = Identifier.of("corzclient", entryText);
         this.corzclient2 = Identifier.of("corzclient", entryText + "_glow");
      }
   }

   static final class FinishTexture extends AbstractTexture {
      FinishTexture(String textureText, NativeImage[] nativeImages) {
         GpuDevice device = RenderSystem.getDevice();
         int width = nativeImages[0].getWidth();
         int height = nativeImages[0].getHeight();
         this.glTexture = device.createTexture(textureText, 5, TextureFormat.RGBA8, width, height, 1, nativeImages.length);

         try {
            this.glTextureView = device.createTextureView(this.glTexture);
            this.sampler = KnifeFinishTextures.getSamplerLinear(device, nativeImages.length > 1);
            CommandEncoder commandEncoder = device.createCommandEncoder();

            for (int i = 0; i < nativeImages.length; i++) {
               commandEncoder.writeToTexture(this.glTexture, nativeImages[i], i, 0, 0, 0, width >> i, height >> i, 0, 0);
            }
         } catch (RuntimeException e) {
            this.close();
            throw e;
         }
      }
   }

   static final class NewestFirst extends LinkedBlockingDeque<Runnable> {
      public boolean offer(Runnable runnable) {
         return this.offerFirst(runnable);
      }
   }

   public static final class Skin {
      public final RenderLayer renderLayer;
      public final RenderLayer renderLayerCorzrenderKnife;
      private final KnifeFinish.Skin[] skins;

      Skin(RenderLayer renderLayer, RenderLayer renderLayer2, KnifeFinish.Skin[] skins) {
         this.renderLayerCorzrenderKnife = renderLayer;
         this.renderLayer = renderLayer2;
         this.skins = skins;
      }

      public KnifeFinish.Skin getSkins(int index) {
         return index >= 0 && index < this.skins.length ? this.skins[index] : null;
      }

      public boolean canLayerRender(int layerRenderValue) {
         KnifeFinish.Skin skins = this.getSkins(layerRenderValue);
         return this.renderLayer != null && skins != null && skins.isFlag();
      }
   }
}
