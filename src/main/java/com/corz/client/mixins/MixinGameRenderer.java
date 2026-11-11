package com.corz.client.mixins;

import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.FramebufferSimpleUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RawProjectionMatrix;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.fog.FogRenderer.FogType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.misc.FOVSlider;
import com.corz.client.categories.misc.Zoom;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.FpsHistory;
import com.corz.client.categories.visuals.NameTags;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.FovOptionsUtils;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {
   @Shadow
   @Final
   private Camera camera;
   @Shadow
   @Final
   private GuiRenderer guiRenderer;
   @Shadow
   @Final
   private FogRenderer fogRenderer;

   @ModifyExpressionValue(
      method = {"method_3172(FZLorg/joml/Matrix4f;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_5498;method_31034()Z"
      )}
   )
   private boolean corzFreecamHands(boolean corzFreecamFlag) {
      if (Freecam.isInstanceFlag()) {
         return Freecam.isBaseInstanceBooleanBase();
      } else {
         return ClampMatrixUtils.isFrameCamera() ? false : corzFreecamFlag;
      }
   }

   @Inject(
      method = {"method_3192(Lnet/minecraft/class_9779;Z)V"},
      at = {@At("HEAD")}
   )
   private void corz$gui3dFrame(RenderTickCounter renderTickCounter, boolean corzgui3dFrameFlag, CallbackInfo callbackInfo) {
      FpsHistory.updateTimeAmount();
      ClampMatrixUtils.updateFrameFlag();
   }

   @Inject(
      method = {"method_3192(Lnet/minecraft/class_9779;Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_437;method_47413(Lnet/minecraft/class_332;IIF)V"
      )}
   )
   private void corz$gui3dBegin(RenderTickCounter renderTickCounter, boolean corzgui3dBeginFlag, CallbackInfo callbackInfo) {
      if (ClampMatrixUtils.isFlagFrame()) {
         try {
            this.guiRenderer.render(this.fogRenderer.getFogBuffer(FogType.NONE));
         } catch (Throwable e) {
            ClampMatrixUtils.handleFlag(e);
         }
      }

      FramebufferSimpleUtils.updateFramebufferSimple();
   }

   @Inject(
      method = {"method_3192(Lnet/minecraft/class_9779;Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_11228;method_70890(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
         shift = Shift.AFTER
      )}
   )
   private void corz$gui3dEnd(RenderTickCounter renderTickCounter, boolean corzgui3dEndFlag, CallbackInfo callbackInfo) {
      FramebufferSimpleUtils.updateCompositeFramebuffer();
   }

   @Inject(
      method = {"method_3192(Lnet/minecraft/class_9779;Z)V"},
      at = {@At("RETURN")}
   )
   private void corz$gui3dLeave(RenderTickCounter renderTickCounter, boolean corzgui3dLeaveFlag, CallbackInfo callbackInfo) {
      FramebufferSimpleUtils.updateCompositeBegin();
   }

   @Redirect(
      method = {"method_3188(Lnet/minecraft/class_9779;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_11286;method_71123(Lorg/joml/Matrix4f;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"
      )
   )
   private GpuBufferSlice corz_captureProjection(RawProjectionMatrix rawProjectionMatrix, Matrix4f matrix) {
      NameTags.MATRIX4F.set(matrix);
      return rawProjectionMatrix.set(matrix);
   }

   @Inject(
      method = {"method_3188(Lnet/minecraft/class_9779;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_3695;method_15405(Ljava/lang/String;)V",
         ordinal = 1
      )}
   )
   private void onWorldRender(RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      Camera cam = Module.mc.gameRenderer.getCamera();
      NameTags.MATRIX4_FMATRIX4F.identity();
      NameTags.MATRIX4F3.set(new Matrix4f().rotation(cam.getRotation().conjugate(new Quaternionf())));
   }

   @ModifyReturnValue(
      method = {"method_3196(Lnet/minecraft/class_4184;FZ)F"},
      at = {@At("RETURN")}
   )
   private float corz_customFovAndZoom(float corzCustomValue, @Local(argsOnly = true) boolean corzCustomFlag) {
      float result = corzCustomValue;
      FOVSlider customFov = (FOVSlider)ModuleRegistry.getModule(FOVSlider.class);
      if (customFov != null && customFov.isEnabled()) {
         result = customFov.getFovTime(corzCustomValue);
      }

      Zoom zoom = (Zoom)ModuleRegistry.getModule(Zoom.class);
      if (zoom != null) {
         double factor = zoom.getToggleHold();
         if (factor > 1.0001) {
            result = (float)(result / factor);
         }
      }

      if (corzCustomFlag && ClampMatrixUtils.isFrameCamera()) {
         result = ClampMatrixUtils.getPanelsFrame(result);
      }

      if (corzCustomFlag) {
         FovOptionsUtils.handleAmount(result);
      }

      return result;
   }

   @Inject(
      method = {"method_3202()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (Freecam.isInstanceFlag() || ClampMatrixUtils.isFrameCamera()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }
}
