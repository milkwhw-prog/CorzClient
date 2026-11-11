package com.corz.client.utils;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.util.Identifier;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.C2PackAudio;
import com.corz.client.utils.C2Model;

final class C2Assets {
   static final Identifier identifier = Identifier.of("corzclient", "c2visuals/butterfly/texture.png");
   private static final ExecutorService threadExecutor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "Corz C2 knife loader");
      thread.setDaemon(true);
      thread.setPriority(4);
      return thread;
   });
   private static final Map<C2Knife, C2Assets.Slot> LOADED_VISUALS_MAP = new EnumMap<>(C2Knife.class);

   static C2Assets.Loaded getButterfly() {
      return getSupplyAsync(C2Knife.BUTTERFLY);
   }

   static void handleSupplyAsync(C2Knife c2Knife) {
      C2PackAudio.updateAsyncRun();
      if (c2Knife != null) {
         C2Knife butterflyEmerald = C2PackAudio.getButterflyEmerald(c2Knife);
         if (butterflyEmerald != null && C2PackAudio.isBadPack()) {
            handleSupplyAsync(butterflyEmerald);
         }

         C2Assets.Slot ifAbsent = getIfAbsent(c2Knife);
         if (ifAbsent.supplyAsync == null && ifAbsent.joinSupplyAsync == null && ifAbsent.load == null) {
            try {
               ifAbsent.supplyAsync = CompletableFuture.supplyAsync(() -> {
                  try {
                     return getCorzclientTexture(c2Knife);
                  } catch (Exception var2x) {
                     throw new CompletionException(var2x);
                  }
               }, threadExecutor);
            } catch (RuntimeException e) {
               ifAbsent.load = e.toString();
            }
         }
      }
   }

   static C2Assets.Loaded getSupplyAsync(C2Knife c2Knife) {
      if (c2Knife == null) {
         return null;
      } else {
         C2Assets.Slot loaded_visuals_map = LOADED_VISUALS_MAP.get(c2Knife);
         if (loaded_visuals_map == null) {
            return null;
         } else {
            if (loaded_visuals_map.supplyAsync != null && loaded_visuals_map.supplyAsync.isDone()) {
               try {
                  loaded_visuals_map.joinSupplyAsync = loaded_visuals_map.supplyAsync.join();
               } catch (CancellationException | CompletionException e) {
                  Throwable cause = e.getCause();
                  loaded_visuals_map.load = cause == null ? e.toString() : cause.toString();
                  System.err.println("[C" + c2Knife.treasureText + " failed to load: " + loaded_visuals_map.load);
               }

               loaded_visuals_map.supplyAsync = null;
            }

            return loaded_visuals_map.joinSupplyAsync;
         }
      }
   }

   static String formatButterfly() {
      return formatLoadedLoading(C2Knife.BUTTERFLY);
   }

   static boolean canVisualsLoad(C2Knife c2Knife) {
      getSupplyAsync(c2Knife);
      C2Assets.Slot slot = c2Knife == null ? null : LOADED_VISUALS_MAP.get(c2Knife);
      return slot != null && slot.load != null;
   }

   private C2Assets() {
   }

   static String formatLoadedLoading(C2Knife c2Knife) {
      if (getSupplyAsync(c2Knife) != null) {
         return "Ready";
      } else {
         C2Assets.Slot slot = c2Knife == null ? null : LOADED_VISUALS_MAP.get(c2Knife);
         if (slot == null) {
            return "Not loaded";
         } else {
            return slot.load != null ? slot.load : (slot.supplyAsync != null ? "Loading" : "Not loaded");
         }
      }
   }

   static boolean isButterfly() {
      return canVisualsLoad(C2Knife.BUTTERFLY);
   }

   private static C2Assets.Slot getIfAbsent(C2Knife c2Knife) {
      return LOADED_VISUALS_MAP.computeIfAbsent(c2Knife, c2Knife2 -> new C2Assets.Slot());
   }

   static void updateVisualsLoaded() {
      LOADED_VISUALS_MAP.clear();
   }

   static void updateButterfly() {
      handleSupplyAsync(C2Knife.BUTTERFLY);
   }

   static C2Assets.Loaded getCorzclientTexture(C2Knife c2Knife)  throws java.io.IOException {
      if (c2Knife.isClassics()) {
         return new C2Assets.Loaded(c2Knife, C2Model.getJsonAnimations(), identifier, null);
      } else {
         C2Model.Display animationJson = C2Model.getAnimationJson(c2Knife);
         C2Model knifeAnimation = C2Model.getKnifeAnimation(c2Knife);
         if (!C2Model.canFlag(animationJson.texture())) {
            throw new IOException("Missing knife texture " + animationJson.texture());
         } else {
            Identifier of = Identifier.of("corzclient", animationJson.texture());
            Identifier of2 = null;
            if (c2Knife.isAnnihilatorYellowFlag()) {
               String substringOffset = animationJson.texture().substring(0, animationJson.texture().length() - 4) + "_s.png";
               if (C2Model.canFlag(substringOffset)) {
                  of2 = Identifier.of("corzclient", substringOffset);
               }
            }

            return new C2Assets.Loaded(c2Knife, knifeAnimation, of, of2);
         }
      }
   }

   record Loaded(C2Knife knife, C2Model model, Identifier texture, Identifier specular) {
   }

   private static final class Slot {
      CompletableFuture<C2Assets.Loaded> supplyAsync;
      C2Assets.Loaded joinSupplyAsync;
      String load;
   }
}
