package com.corz.client.utils;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import com.corz.client.utils.C2Assets;
import com.corz.client.utils.C2Model;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.C2Renderer;
import com.corz.client.utils.C2Clips;
import com.corz.client.utils.C2Cues;
import com.corz.client.utils.C2PackAudio;
import com.corz.client.utils.C2Butterfly;

public final class C2Animator {
   private static C2Assets.Loaded loaded;
   private static C2Model.Clip clip2StaticIdle;
   private static AbstractClientPlayerEntity player;
   private static float[] clone;
   private static float[] staticIdleAmounts;
   private static Arm mainArm;
   private static ItemStack empty = ItemStack.EMPTY;
   private static int staticIdleCount;
   private static long lastNanoTime;
   private static int selectedSlot;
   private static long lastNanoTimePlayerArm;
   private static String staticIdleText = "static_idle";
   private static int staticLoadedCount;

   private static void handle(float[] c2AnimatorValues, float[] c2AnimatorValues2, float c2AnimatorFactor, float[] c2AnimatorValues3) {
      for (int i = 0; i < c2AnimatorValues.length; i++) {
         float c2AnimatorTotal = c2AnimatorValues2[i] - c2AnimatorValues[i];
         int c2AnimatorLimit = i % 9;
         if (c2AnimatorLimit >= 3 && c2AnimatorLimit < 6) {
            c2AnimatorTotal -= (float)Math.floor((c2AnimatorTotal + 180.0F) / 360.0F) * 360.0F;
         }

         c2AnimatorValues3[i] = c2AnimatorValues[i] + c2AnimatorTotal * c2AnimatorFactor;
      }
   }

   public static C2Knife getLoadedKnife() {
      return loaded == null ? null : loaded.knife();
   }

   public static boolean canStaticLoaded(LivingEntity livingEntity, Hand hand) {
      MinecraftClient instance = MinecraftClient.getInstance();
      return livingEntity == instance.player
            && instance.currentScreen == null
            && instance.isWindowFocused()
            && hand == Hand.MAIN_HAND
            && C2Renderer.canRiptidePerspective(instance.player, hand, instance.player.getStackInHand(hand))
            && isPlayerArm()
         ? canStaticIdle(C2Clips.formatMeleeUnmodifiable(loaded.knife(), loaded.model(), instance.options.useKey.isPressed(), staticLoadedCount++))
         : false;
   }

   private C2Animator() {
   }

   public static long getClipTime() {
      return clip2StaticIdle == null ? 0L : lastNanoTime;
   }

   private static void updateStaticIdle() {
      if (loaded != null) {
         Arrays.fill(staticIdleAmounts, 0.0F);

         for (int i = 0; i < loaded.model().meshModelBones.length; i++) {
            Arrays.fill(staticIdleAmounts, i * 9 + 6, i * 9 + 9, 1.0F);
         }

         C2Model.Clip unmodifiablemap = loaded.model().unmodifiableMap.get(staticIdleText);
         if (unmodifiablemap == null) {
            unmodifiablemap = loaded.model().unmodifiableMap.get("static_idle");
         }

         long nanoTime = System.nanoTime();
         unmodifiablemap.handleTracks((float)(nanoTime - lastNanoTimePlayerArm) / 1.0E9F % unmodifiablemap.length(), staticIdleAmounts);
         if (clip2StaticIdle != null) {
            float seconds = (float)(nanoTime - lastNanoTime) / 1.0E9F;
            if (seconds > clip2StaticIdle.length() + 0.12F) {
               clip2StaticIdle = null;
            } else {
               float[] staticIdleValues = null;
               if (seconds > clip2StaticIdle.length()) {
                  staticIdleValues = (float[])staticIdleAmounts.clone();
                  clip2StaticIdle.handleTracks(clip2StaticIdle.length(), staticIdleAmounts);
               } else {
                  clip2StaticIdle.handleTracks(seconds, staticIdleAmounts);
               }

               float clamp = Math.clamp(seconds / 0.12F, 0.0F, 1.0F);
               float staticIdleValue = clamp * clamp * (3.0F - 2.0F * clamp);
               if (staticIdleValues != null) {
                  float clamp2 = Math.clamp((seconds - clip2StaticIdle.length()) / 0.12F, 0.0F, 1.0F);
                  float c2AnimatorFactor = clamp2 * clamp2 * (3.0F - 2.0F * clamp2);
                  handle(staticIdleAmounts, staticIdleValues, c2AnimatorFactor, staticIdleAmounts);
               } else if (staticIdleValue < 1.0F) {
                  handle(clone, staticIdleAmounts, staticIdleValue, staticIdleAmounts);
               }
            }
         }
      }
   }

   public static List<C2Cues.Contact> getClipLoaded() {
      if (loaded != null && clip2StaticIdle != null) {
         List lengthCues = C2PackAudio.getLengthCues(loaded.knife(), clip2StaticIdle);
         if (lengthCues != null) {
            return lengthCues;
         } else {
            return !isInspectClip() ? List.of() : C2Cues.getInspectBlade(loaded.model(), clip2StaticIdle);
         }
      } else {
         return List.of();
      }
   }

   public static void updateStaticIdleLoaded() {
      loaded = null;
      player = null;
      clip2StaticIdle = null;
      empty = ItemStack.EMPTY;
      staticIdleAmounts = null;
      clone = null;
      staticLoadedCount = 0;
      staticIdleCount = 0;
      staticIdleText = "static_idle";
   }

   public static boolean isInspectClip() {
      return clip2StaticIdle != null && clip2StaticIdle.name().startsWith("inspect");
   }

   public static void handleHandFocused(MinecraftClient client, boolean handFocusedFlag) {
      if (client.player != null && C2Renderer.canRiptidePerspective(client.player, Hand.MAIN_HAND, client.player.getMainHandStack())) {
         isPlayerArm();
         if (client.currentScreen == null && client.isWindowFocused()) {
            if (handFocusedFlag) {
               updateStaticLoaded();
            }
         } else {
            clip2StaticIdle = null;
         }
      } else {
         updateStaticIdleLoaded();
      }
   }

   public static void updateClipMelee() {
      if (clip2StaticIdle != null && clip2StaticIdle.name().startsWith("melee_")) {
         clip2StaticIdle = null;
      }
   }

   public static float[] getStaticIdle() {
      isPlayerArm();
      updateStaticIdle();
      return staticIdleAmounts;
   }

   public static float getClipTimeClip2StaticIdle() {
      return clip2StaticIdle == null ? 0.0F : (float)(System.nanoTime() - lastNanoTime) / 1.0E9F;
   }

   private static boolean canStaticIdle(String staticIdleText) {
      C2Model.Clip clip2 = loaded.model().unmodifiableMap.get(staticIdleText);
      if (clip2 == null) {
         return false;
      } else {
         updateStaticIdle();
         System.arraycopy(staticIdleAmounts, 0, clone, 0, staticIdleAmounts.length);
         clip2StaticIdle = clip2;
         lastNanoTime = System.nanoTime();
         return true;
      }
   }

   public static float getClipLength() {
      return clip2StaticIdle == null ? 0.0F : clip2StaticIdle.length();
   }

   static C2Assets.Loaded getLoaded() {
      return loaded;
   }

   public static String formatClipStatic() {
      return clip2StaticIdle == null ? staticIdleText : clip2StaticIdle.name();
   }

   private static boolean isPlayerArm() {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player == null) {
         return false;
      } else {
         C2Assets.Loaded supplyAsync = C2Assets.getSupplyAsync(C2Butterfly.getPlayerHand());
         if (supplyAsync == null) {
            return false;
         } else {
            if (loaded != supplyAsync
               || player != instance.player
               || selectedSlot != instance.player.getInventory().getSelectedSlot()
               || mainArm != instance.player.getMainArm()
               || !ItemStack.areItemsEqual(empty, instance.player.getMainHandStack())) {
               updateStaticIdleLoaded();
               loaded = supplyAsync;
               player = instance.player;
               selectedSlot = player.getInventory().getSelectedSlot();
               mainArm = player.getMainArm();
               empty = player.getMainHandStack().copy();
               staticIdleText = C2Clips.formatStaticIdle(supplyAsync.knife(), supplyAsync.model());
               staticIdleAmounts = loaded.model().getBonesMeshMeshModelBones();
               clone = (float[])staticIdleAmounts.clone();
               lastNanoTimePlayerArm = System.nanoTime();
               updateStaticIdle();
               canStaticIdle(C2Clips.formatDrawUnmodifiable(supplyAsync.knife(), supplyAsync.model()));
            }

            return true;
         }
      }
   }

   public static void updateStaticLoaded() {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null
         && C2Renderer.canRiptidePerspective(instance.player, Hand.MAIN_HAND, instance.player.getMainHandStack())
         && isPlayerArm()
         && !isInspectClip()) {
         List unmodifiable = C2Clips.getUnmodifiable(loaded.knife(), loaded.model());
         if (!unmodifiable.isEmpty()) {
            canStaticIdle((String)unmodifiable.get(Math.floorMod(staticIdleCount++, unmodifiable.size())));
         }
      }
   }
}
