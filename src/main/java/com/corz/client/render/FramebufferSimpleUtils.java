package com.corz.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;

public final class FramebufferSimpleUtils {
   private static boolean beginCompositeFlag;
   private static Framebuffer framebuffer;
   private static SimpleFramebuffer simpleFramebuffer;
   private static boolean auxGuiFlag;
   private static SimpleFramebuffer resizeTexture;

   static boolean isFramebufferSimple() {
      if (beginCompositeFlag && framebuffer == resizeTexture) {
         simpleFramebuffer = getResizeTexture(simpleFramebuffer, "corz_gui3d_aux", resizeTexture.textureWidth, resizeTexture.textureHeight);
         if (!auxGuiFlag) {
            RenderSystem.getDevice()
               .createCommandEncoder()
               .clearColorAndDepthTextures(simpleFramebuffer.getColorAttachment(), 0, simpleFramebuffer.getDepthAttachment(), 1.0);
            auxGuiFlag = true;
         }

         framebuffer = simpleFramebuffer;
         return true;
      } else {
         return false;
      }
   }

   private FramebufferSimpleUtils() {
   }

   public static void updateFramebufferSimple() {
      if (!beginCompositeFlag && !ClampMatrixUtils.isFramePanelsActive()) {
         try {
            if (!ClampMatrixUtils.isFramePanels()) {
               return;
            }

            MinecraftClient instance = MinecraftClient.getInstance();
            Framebuffer framebuffer2 = instance.getFramebuffer();
            int textureWidth = framebuffer2.textureWidth;
            int textureHeight = framebuffer2.textureHeight;
            if (textureWidth <= 0 || textureHeight <= 0 || framebuffer2.getColorAttachment() == null) {
               return;
            }

            if (textureWidth != instance.getWindow().getFramebufferWidth() || textureHeight != instance.getWindow().getFramebufferHeight()) {
               return;
            }

            resizeTexture = getResizeTexture(resizeTexture, "corz_gui3d", textureWidth, textureHeight);
            RenderSystem.getDevice()
               .createCommandEncoder()
               .clearColorAndDepthTextures(resizeTexture.getColorAttachment(), 0, resizeTexture.getDepthAttachment(), 1.0);
            auxGuiFlag = false;
            ClampMatrixUtils.updateFlagContext();
            framebuffer = resizeTexture;
            beginCompositeFlag = true;
         } catch (Throwable e) {
            ClampMatrixUtils.handleFrameClamp("begin", e);
         }
      }
   }

   public static Framebuffer getFramebuffer() {
      return framebuffer;
   }

   private static SimpleFramebuffer getResizeTexture(SimpleFramebuffer resizeTexture, String resizeTextureText, int resizeTextureValue, int textureHeight) {
      if (resizeTexture == null) {
         return new SimpleFramebuffer(resizeTextureText, resizeTextureValue, textureHeight, true);
      } else {
         if (resizeTexture.textureWidth != resizeTextureValue || resizeTexture.textureHeight != textureHeight) {
            resizeTexture.resize(resizeTextureValue, textureHeight);
         }

         return resizeTexture;
      }
   }

   public static void updateCompositeBegin() {
      beginCompositeFlag = false;
      framebuffer = null;
   }

   public static void updateSimpleFramebuffer() {
      if (!beginCompositeFlag) {
         if (resizeTexture != null) {
            resizeTexture.delete();
            resizeTexture = null;
         }

         if (simpleFramebuffer != null) {
            simpleFramebuffer.delete();
            simpleFramebuffer = null;
         }
      }
   }

   public static boolean isCompositeBegin() {
      return beginCompositeFlag;
   }

   static void updateFramebufferSimpleBeginCompositeFlag() {
      if (beginCompositeFlag && framebuffer == simpleFramebuffer) {
         framebuffer = resizeTexture;
      }
   }

   public static void updateCompositeFramebuffer() {
      if (beginCompositeFlag) {
         beginCompositeFlag = false;
         framebuffer = null;

         try {
            ClampMatrixUtils.handleAmountMatrix(resizeTexture, auxGuiFlag ? simpleFramebuffer : null);
         } catch (Throwable e) {
            ClampMatrixUtils.handleFrameClamp("composite", e);
         }
      }
   }
}
