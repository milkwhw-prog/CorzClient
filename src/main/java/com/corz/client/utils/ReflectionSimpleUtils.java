package com.corz.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import com.corz.client.categories.visuals.CustomSky;

public final class ReflectionSimpleUtils {
   private static boolean reflectionSkyFlag;
   private static boolean reflectionSkyActive;
   private static SimpleFramebuffer simpleFramebuffer;
   private static long reflectionSkyTime;

   private ReflectionSimpleUtils() {
   }

   public static boolean canReflectionSky(long transformsDynamicValue) {
      if (reflectionSkyActive) {
         return false;
      } else {
         CustomSky customSky = CustomSky.getCustomSky();
         if (customSky == null) {
            return false;
         } else {
            try {
               if (simpleFramebuffer == null) {
                  simpleFramebuffer = new SimpleFramebuffer("corz_sky_reflection", 512, 512, false);
                  reflectionSkyFlag = false;
               }

               if (!reflectionSkyFlag || transformsDynamicValue - reflectionSkyTime >= 33000000L) {
                  RenderSystem.getDevice().createCommandEncoder().clearColorTexture(simpleFramebuffer.getColorAttachment(), -16777216);
                  reflectionSkyFlag = customSky.canWorldPlayer(simpleFramebuffer.getColorAttachmentView(), 512);
                  reflectionSkyTime = transformsDynamicValue;
               }

               return reflectionSkyFlag;
            } catch (Throwable e) {
               reflectionSkyActive = true;
               updateSimpleFramebuffer();
               return false;
            }
         }
      }
   }

   public static GpuTextureView getAttachmentView() {
      return simpleFramebuffer == null ? null : simpleFramebuffer.getColorAttachmentView();
   }

   public static void updateSimpleFramebuffer() {
      if (simpleFramebuffer != null) {
         simpleFramebuffer.delete();
         simpleFramebuffer = null;
      }

      reflectionSkyFlag = false;
   }

   public static Framebuffer getReflectionSimple() {
      return reflectionSkyFlag ? simpleFramebuffer : null;
   }
}
