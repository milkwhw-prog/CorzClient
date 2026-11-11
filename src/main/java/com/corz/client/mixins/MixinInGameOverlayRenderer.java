package com.corz.client.mixins;

import com.corz.client.render.ClampMatrixUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.visuals.CustomFlame;
import com.corz.client.categories.visuals.NoFlame;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.MinimalSharpUtils;

@Mixin({InGameOverlayRenderer.class})
public class MixinInGameOverlayRenderer {
   @Inject(
      method = {"method_23070(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;Lnet/minecraft/class_1058;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onRenderFireOverlay(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Sprite sprite, CallbackInfo callbackInfo) {
      NoFlame noFlame = (NoFlame)ModuleRegistry.getModule(NoFlame.class);
      CustomFlame customFlame = (CustomFlame)ModuleRegistry.getModule(CustomFlame.class);
      boolean firstPerson = true;
      MinecraftClient client = MinecraftClient.getInstance();
      if (client != null && client.options != null && client.options.getPerspective() != null) {
         firstPerson = client.options.getPerspective().isFirstPerson();
      }

      boolean cancel = MinimalSharpUtils.canFlag(
         customFlame != null && customFlame.isEnabled(), customFlame != null && customFlame.isBase(), firstPerson, noFlame != null && noFlame.isEnabled()
      );
      if (cancel) {
         callbackInfo.cancel();
      }
   }

   @ModifyExpressionValue(
      method = {"method_23067(ZFLnet/minecraft/class_11659;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_5498;method_31034()Z"
      )}
   )
   private boolean corz$gui3dOverlays(boolean corzgui3dOverlaysFlag) {
      return corzgui3dOverlaysFlag && !ClampMatrixUtils.isFrameCamera();
   }
}
