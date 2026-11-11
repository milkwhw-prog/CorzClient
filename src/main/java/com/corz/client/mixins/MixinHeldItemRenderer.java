package com.corz.client.mixins;

import java.lang.reflect.Method;
import java.util.function.IntToDoubleFunction;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.visuals.HandCustomer;
import com.corz.client.categories.visuals.SwordInspect;
import com.corz.client.utils.AttachmentFramebuffer;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeFinishTextures;
import com.corz.client.utils.KnifeMesh;
import com.corz.client.utils.KnifeRig;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.KnifeHold;

@Mixin({HeldItemRenderer.class})
public abstract class MixinHeldItemRenderer {
   @Shadow
   private ItemStack offHand;
   @Shadow
   @Final
   private EntityRenderManager entityRenderDispatcher;
   @Unique
   private Matrix4f corz$knifeCorrection;
   @Unique
   private Entry corz$knifeArmEntry;
   @Unique
   private static Method corz$applySwingTransformsMethod;
   @Unique
   private static Method corz$applyUseTransformsMethod;
   @Unique
   private static boolean corz$lancingResolved;

   @Shadow
   public abstract void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext itemDisplayContext, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int mixinHeldValue);

   @Shadow
   protected abstract void swingArm(float swingArmValue, MatrixStack matrices, int i, Arm arm);

   @Shadow
   protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipOffsetValue);

   @Shadow
   protected abstract void applyEatOrDrinkTransformation(MatrixStack matrices, float eatOrValue, Arm arm, ItemStack stack, PlayerEntity player);

   @Shadow
   protected abstract void applyBrushTransformation(MatrixStack matrices, float brushTransformationValue, Arm arm, PlayerEntity player);

   @Shadow
   protected abstract void renderMapInBothHands(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int inBothValue, float inBothValue2, float equipOffsetValue, float swingArmValue);

   @Shadow
   protected abstract void renderMapInOneHand(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int inOneValue, float equipOffsetValue, Arm arm, float swingArmValue, ItemStack stack);

   @Shadow
   protected abstract void renderArmHoldingItem(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int armHoldingValue, float equipOffsetValue, float swingArmValue, Arm arm);

   @Inject(
      method = {"method_22976(FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_746;I)V"},
      at = {@At("HEAD")}
   )
   private void corz$beforeHands(
      float corzbeforeHandsValue, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, ClientPlayerEntity clientPlayerEntity, int corzbeforeHandsValue2, CallbackInfo callbackInfo
   ) {
      AttachmentFramebuffer.getInstance().updatePostTextureTrailSamplerActive();
   }

   @Inject(
      method = {"method_22976(FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_746;I)V"},
      at = {@At("TAIL")}
   )
   private void corz$afterHands(
      float corzafterHandsValue, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, ClientPlayerEntity clientPlayerEntity, int corzafterHandsValue2, CallbackInfo callbackInfo
   ) {
      AttachmentFramebuffer renderer = AttachmentFramebuffer.getInstance();
      renderer.updatePostTexture3();
      renderer.isAmountPost();
   }

   @Inject(
      method = {"method_3219(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;IFFLnet/minecraft/class_1306;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1007;method_4220(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_2960;Z)V"
      )}
   )
   private void corz$knifeRightArm(
      MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int corzknifeRightValue, float corzknifeRightValue2, float corzknifeRightValue3, Arm arm, CallbackInfo callbackInfo
   ) {
      this.corz$poseKnifeArm(matrixStack);
   }

   @Inject(
      method = {"method_3219(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;IFFLnet/minecraft/class_1306;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1007;method_4221(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_2960;Z)V"
      )}
   )
   private void corz$knifeLeftArm(
      MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int corzknifeLeftValue, float corzknifeLeftValue2, float corzknifeLeftValue3, Arm arm, CallbackInfo callbackInfo
   ) {
      this.corz$poseKnifeArm(matrixStack);
   }

   @Unique
   private void corz$poseKnifeArm(MatrixStack matrixStack) {
      if (this.corz$knifeCorrection != null) {
         matrixStack.multiplyPositionMatrix(this.corz$knifeCorrection);
         this.corz$knifeArmEntry = matrixStack.peek().copy();
      }
   }

   @Unique
   private boolean corz$renderKnifeInHand(
      SwordInspect swordInspect,
      KnifeRig knifeRig,
      AbstractClientPlayerEntity abstractClientPlayerEntity,
      Arm arm,
      ItemStack stack,
      float corzrenderKnifeValue,
      float equipOffsetValue,
      MatrixStack matrices,
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      int armHoldingValue
   ) {
      boolean meshes = KnifeMesh.canModel(knifeRig);
      if (!meshes && !swordInspect.canModelBaked(knifeRig)) {
         return false;
      } else {
         int side = arm == Arm.RIGHT ? 1 : -1;
         IntToDoubleFunction ch = KnifeHold.getIntToDoubleFunction(swordInspect.getSwishRound(corzrenderKnifeValue), corzrenderKnifeValue);
         this.corz$knifeCorrection = corz$matrix(KnifeHold.getAmounts(side, ch));
         this.corz$knifeArmEntry = null;
         matrices.push();

         try {
            this.renderArmHoldingItem(matrices, orderedRenderCommandQueue, armHoldingValue, equipOffsetValue, 0.0F, arm);
         } finally {
            matrices.pop();
            this.corz$knifeCorrection = null;
         }

         Entry armEntry = this.corz$knifeArmEntry;
         this.corz$knifeArmEntry = null;
         if (armEntry == null) {
            return false;
         } else {
            matrices.push();

            try {
               matrices.peek().copy(armEntry);
               this.entityRenderDispatcher.getPlayerRenderer(abstractClientPlayerEntity).getModel().getArm(arm).applyTransform(matrices);
               matrices.multiplyPositionMatrix(corz$matrix(KnifeHold.getAmounts(side, 0.48, ch, abstractClientPlayerEntity.getSkin().model() == PlayerSkinType.SLIM)));
               float[] pose = new float[3];
               ItemDisplayContext ctx = arm == Arm.RIGHT ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
               KnifeFinishTextures.Skin finish = meshes ? KnifeFinishTextures.getTimeRigs(knifeRig, KnifeFinish.getFinish(swordInspect.formatEnumKnifeFinish())) : null;

               for (int i = 0; i < knifeRig.getParentParts(); i++) {
                  matrices.push();

                  try {
                     knifeRig.handlePivotParent(i, ch, pose);
                     matrices.translate(pose[1], pose[2], 0.0F);
                     matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(pose[0]));
                     KnifeMesh mesh = meshes ? KnifeMesh.getTexturesKnife(knifeRig.getParentParts(i).model()) : null;
                     KnifeFinish.Skin skin = finish != null ? finish.getSkins(i) : null;
                     if (mesh != null && mesh.canAmounts(skin)) {
                        orderedRenderCommandQueue.submitCustom(
                           matrices, finish.renderLayerCorzrenderKnife, (entry, vertices) -> mesh.handleAmountsLight(entry, vertices, armHoldingValue, OverlayTexture.DEFAULT_UV, skin)
                        );
                        if (finish.canLayerRender(i)) {
                           orderedRenderCommandQueue.getBatchingQueue(1).submitCustom(matrices, finish.renderLayer, (entry, vertices2) -> mesh.handleAmounts(entry, vertices2, skin));
                        }

                        if (stack.hasGlint()) {
                           orderedRenderCommandQueue.submitCustom(
                              matrices, RenderLayers.entityGlint(), (entry, vertices3) -> mesh.handleAmountsLight(entry, vertices3, armHoldingValue, OverlayTexture.DEFAULT_UV)
                           );
                        }
                     } else if (mesh != null) {
                        orderedRenderCommandQueue.submitCustom(
                           matrices, KnifeMesh.getCullCutout(), (entry, vertices4) -> mesh.handleAmountsLight(entry, vertices4, armHoldingValue, OverlayTexture.DEFAULT_UV)
                        );
                        if (stack.hasGlint()) {
                           orderedRenderCommandQueue.submitCustom(
                              matrices, RenderLayers.entityGlint(), (entry, vertices5) -> mesh.handleAmountsLight(entry, vertices5, armHoldingValue, OverlayTexture.DEFAULT_UV)
                           );
                        }
                     } else {
                        matrices.translate(8.0F, 8.0F, 8.0F);
                        matrices.scale(16.0F, 16.0F, 16.0F);
                        this.renderItem(abstractClientPlayerEntity, swordInspect.getModelProbe(stack, knifeRig, i), ctx, matrices, orderedRenderCommandQueue, armHoldingValue);
                     }
                  } finally {
                     matrices.pop();
                  }
               }
            } finally {
               matrices.pop();
            }

            return true;
         }
      }
   }

   @Unique
   private static Matrix4f corz$matrix(double[] corzmatrixValues) {
      return new Matrix4f(
         (float)corzmatrixValues[0],
         (float)corzmatrixValues[4],
         (float)corzmatrixValues[8],
         (float)corzmatrixValues[12],
         (float)corzmatrixValues[1],
         (float)corzmatrixValues[5],
         (float)corzmatrixValues[9],
         (float)corzmatrixValues[13],
         (float)corzmatrixValues[2],
         (float)corzmatrixValues[6],
         (float)corzmatrixValues[10],
         (float)corzmatrixValues[14],
         (float)corzmatrixValues[3],
         (float)corzmatrixValues[7],
         (float)corzmatrixValues[11],
         (float)corzmatrixValues[15]
      );
   }

   @Unique
   private HandCustomer corz$handCustomer() {
      return (HandCustomer)ModuleRegistry.getModule(HandCustomer.class);
   }

   @Inject(
      method = {"method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$renderFirstPersonItem(
      AbstractClientPlayerEntity abstractClientPlayerEntity,
      float corzrenderFirstValue,
      float corzrenderFirstValue2,
      Hand hand,
      float corzrenderKnifeValue,
      ItemStack stack,
      float equipOffsetValue,
      MatrixStack matrices,
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      int glintEnumValue,
      CallbackInfo callbackInfo
   ) {
      SwordInspect si = (SwordInspect)ModuleRegistry.getModule(SwordInspect.class);
      if (si != null && si.canSwappedSlot(abstractClientPlayerEntity, hand, stack)) {
         si.handleGlintEnum(abstractClientPlayerEntity, stack, matrices, orderedRenderCommandQueue, glintEnumValue);
         callbackInfo.cancel();
      } else {
         if (si != null && !abstractClientPlayerEntity.isUsingSpyglass() && !abstractClientPlayerEntity.isInvisible()) {
            KnifeRig rig = si.getHand(abstractClientPlayerEntity, hand, stack);
            if (rig != null && (rig != KnifeRig.BUTTERFLY_TP || si.isSwappedButterfly()) && (!abstractClientPlayerEntity.isUsingItem() || abstractClientPlayerEntity.getActiveHand() != hand)) {
               Arm kArm = abstractClientPlayerEntity.getMainArm();
               if (this.corz$renderKnifeInHand(si, rig, abstractClientPlayerEntity, kArm, stack, corzrenderKnifeValue, equipOffsetValue, matrices, orderedRenderCommandQueue, glintEnumValue)) {
                  callbackInfo.cancel();
                  return;
               }
            }
         }

         if (si != null && si.canEmptyHand(hand, stack) && !abstractClientPlayerEntity.isUsingSpyglass()) {
            callbackInfo.cancel();
            Arm iArm = hand == Hand.MAIN_HAND ? abstractClientPlayerEntity.getMainArm() : abstractClientPlayerEntity.getMainArm().getOpposite();
            int iSide = iArm == Arm.RIGHT ? 1 : -1;
            matrices.push();
            si.handleMultiplyAnim(matrices, iSide, iArm);
            if (!abstractClientPlayerEntity.isInvisible()) {
               matrices.push();
               si.handleMultiplyTranslate(matrices, iSide, iArm);
               this.renderArmHoldingItem(matrices, orderedRenderCommandQueue, glintEnumValue, equipOffsetValue, 0.0F, iArm);
               matrices.pop();
            }

            this.applyEquipOffset(matrices, iArm, equipOffsetValue);
            si.handleAnimTranslate(matrices, iSide, iArm);
            si.handleAnimInspect(matrices, iSide, iArm);
            this.renderItem(
               abstractClientPlayerEntity,
               stack,
               iArm == Arm.RIGHT ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
               matrices,
               orderedRenderCommandQueue,
               glintEnumValue
            );
            matrices.pop();
         } else {
            HandCustomer hc = this.corz$handCustomer();
            if (hc != null && hc.isBase()) {
               if (!abstractClientPlayerEntity.isUsingSpyglass()) {
                  callbackInfo.cancel();
                  this.corz$renderFirstPersonItemInternal(
                     hc, abstractClientPlayerEntity, corzrenderFirstValue, corzrenderFirstValue2, hand, corzrenderKnifeValue, stack, equipOffsetValue, matrices, orderedRenderCommandQueue, glintEnumValue
                  );
               }
            }
         }
      }
   }

   @Unique
   private void corz$renderFirstPersonItemInternal(
      HandCustomer handCustomer,
      AbstractClientPlayerEntity abstractClientPlayerEntity,
      float corzrenderFirstValue,
      float corzrenderFirstValue2,
      Hand hand,
      float swingArmValue,
      ItemStack stack,
      float equipOffsetValue,
      MatrixStack matrices,
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      int armHoldingValue
   ) {
      boolean isMainHand = hand == Hand.MAIN_HAND;
      Arm arm = isMainHand ? abstractClientPlayerEntity.getMainArm() : abstractClientPlayerEntity.getMainArm().getOpposite();
      matrices.push();
      if (stack.isEmpty()) {
         if (handCustomer.isBase()) {
            handCustomer.handleTranslateRight(matrices, arm);
         }

         if (isMainHand && !abstractClientPlayerEntity.isInvisible()) {
            this.renderArmHoldingItem(matrices, orderedRenderCommandQueue, armHoldingValue, equipOffsetValue, swingArmValue, arm);
         }
      } else if (stack.contains(DataComponentTypes.MAP_ID)) {
         if (handCustomer.isBase()) {
            handCustomer.handleTranslateRight(matrices, arm);
         }

         if (isMainHand && this.offHand.isEmpty()) {
            this.renderMapInBothHands(matrices, orderedRenderCommandQueue, armHoldingValue, corzrenderFirstValue2, equipOffsetValue, swingArmValue);
         } else {
            this.renderMapInOneHand(matrices, orderedRenderCommandQueue, armHoldingValue, equipOffsetValue, arm, swingArmValue, stack);
         }
      } else if (stack.isOf(Items.CROSSBOW)) {
         if (handCustomer.isBase()) {
            handCustomer.handleTranslateRight(matrices, arm);
         }

         this.applyEquipOffset(matrices, arm, equipOffsetValue);
         boolean isCharged = CrossbowItem.isCharged(stack);
         boolean isRightArm = arm == Arm.RIGHT;
         int i = isRightArm ? 1 : -1;
         if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand && !isCharged) {
            matrices.translate(i * -0.4785682F, -0.094387F, 0.05731531F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 65.3F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * -9.785F));
            float f = stack.getMaxUseTime(abstractClientPlayerEntity) - (abstractClientPlayerEntity.getItemUseTimeLeft() - corzrenderFirstValue + 1.0F);
            float g = f / CrossbowItem.getPullTime(stack, abstractClientPlayerEntity);
            float clamp = MathHelper.clamp(g, 0.0F, 1.0F);
            if (clamp > 0.1F) {
               float h = MathHelper.sin((f - 0.1F) * 1.3F);
               float k = h * (clamp - 0.1F);
               matrices.translate(k * 0.0F, k * 0.004F, k * 0.0F);
            }

            matrices.translate(clamp * 0.0F, clamp * 0.0F, clamp * 0.04F);
            matrices.scale(1.0F, 1.0F, 1.0F + clamp * 0.2F);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(i * 45.0F));
         } else {
            this.swingArm(swingArmValue, matrices, i, arm);
            if (isCharged && swingArmValue < 0.001F && isMainHand) {
               matrices.translate(i * -0.641864F, 0.0F, 0.0F);
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 10.0F));
            }
         }

         this.renderItem(
            abstractClientPlayerEntity,
            stack,
            isRightArm ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            matrices,
            orderedRenderCommandQueue,
            armHoldingValue
         );
      } else {
         boolean isRightArm = arm == Arm.RIGHT;
         int side = isRightArm ? 1 : -1;
         if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
            if (handCustomer.isBase()) {
               handCustomer.handleTranslateRight(matrices, arm);
            }

            UseAction useAction = stack.getUseAction();
            if (!useAction.hasNoOffset()) {
               this.applyEquipOffset(matrices, arm, equipOffsetValue);
            }

            switch (useAction) {
               case NONE:
               default:
                  break;
               case EAT:
               case DRINK:
                  this.applyEatOrDrinkTransformation(matrices, corzrenderFirstValue, arm, stack, abstractClientPlayerEntity);
                  this.applyEquipOffset(matrices, arm, equipOffsetValue);
                  break;
               case BLOCK:
                  if (!(stack.getItem() instanceof ShieldItem)) {
                     matrices.translate(side * -0.14142136F, 0.08F, 0.14142136F);
                     matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-102.25F));
                     matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * 13.365F));
                     matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * 78.05F));
                  }
                  break;
               case BOW: {
                  matrices.translate(side * -0.2785682F, 0.18344387F, 0.15731531F);
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935F));
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * 35.3F));
                  matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * -9.785F));
                  float f = stack.getMaxUseTime(abstractClientPlayerEntity) - (abstractClientPlayerEntity.getItemUseTimeLeft() - corzrenderFirstValue + 1.0F);
                  float gx = f / 20.0F;
                  float corzrenderFirstLimit = (gx * gx + gx * 2.0F) / 3.0F;
                  if (corzrenderFirstLimit > 1.0F) {
                     corzrenderFirstLimit = 1.0F;
                  }

                  if (corzrenderFirstLimit > 0.1F) {
                     float h = MathHelper.sin((f - 0.1F) * 1.3F);
                     float k = h * (corzrenderFirstLimit - 0.1F);
                     matrices.translate(k * 0.0F, k * 0.004F, k * 0.0F);
                  }

                  matrices.translate(corzrenderFirstLimit * 0.0F, corzrenderFirstLimit * 0.0F, corzrenderFirstLimit * 0.04F);
                  matrices.scale(1.0F, 1.0F, 1.0F + corzrenderFirstLimit * 0.2F);
                  matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(side * 45.0F));
                  break;
               }
               case TRIDENT: {
                  matrices.translate(side * -0.5F, 0.7F, 0.1F);
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0F));
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * 35.3F));
                  matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * -9.785F));
                  float f = stack.getMaxUseTime(abstractClientPlayerEntity) - (abstractClientPlayerEntity.getItemUseTimeLeft() - corzrenderFirstValue + 1.0F);
                  float g = f / 10.0F;
                  if (g > 1.0F) {
                     g = 1.0F;
                  }

                  if (g > 0.1F) {
                     float h = MathHelper.sin((f - 0.1F) * 1.3F);
                     float k = h * (g - 0.1F);
                     matrices.translate(k * 0.0F, k * 0.004F, k * 0.0F);
                  }

                  matrices.translate(0.0F, 0.0F, g * 0.2F);
                  matrices.scale(1.0F, 1.0F, 1.0F + g * 0.2F);
                  matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(side * 45.0F));
                  break;
               }
               case BRUSH:
                  this.applyBrushTransformation(matrices, corzrenderFirstValue, arm, abstractClientPlayerEntity);
                  break;
               case BUNDLE:
                  this.swingArm(swingArmValue, matrices, side, arm);
                  break;
               case SPEAR: {
                  matrices.translate(side * 0.56F, -0.52F, -0.72F);
                  float f = stack.getMaxUseTime(abstractClientPlayerEntity) - (abstractClientPlayerEntity.getItemUseTimeLeft() - corzrenderFirstValue + 1.0F);
                  corz$invokeLancingUse(abstractClientPlayerEntity.getTimeSinceLastKineticAttack(corzrenderFirstValue), matrices, f, arm, stack);
               }
            }
         } else if (abstractClientPlayerEntity.isUsingRiptide()) {
            if (handCustomer.isBase()) {
               handCustomer.handleTranslateRight(matrices, arm);
            }

            this.applyEquipOffset(matrices, arm, equipOffsetValue);
            matrices.translate(side * -0.4F, 0.8F, 0.3F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * 65.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * -85.0F));
         } else {
            boolean handled = false;
            if (handCustomer.isBase()) {
               handled = handCustomer.canMultiplyDegrees(swingArmValue, equipOffsetValue, matrices, side, arm);
            }

            if (!handled) {
               if (handCustomer.isBase()) {
                  handCustomer.handleTranslateRight(matrices, arm);
               }

               this.applyEquipOffset(matrices, arm, equipOffsetValue);
               switch (stack.getSwingAnimation().type()) {
                  case NONE:
                  default:
                     break;
                  case WHACK:
                     this.swingArm(swingArmValue, matrices, side, arm);
                     break;
                  case STAB:
                     corz$invokeLancingSwing(swingArmValue, matrices, side, arm);
               }
            }
         }

         this.renderItem(
            abstractClientPlayerEntity,
            stack,
            isRightArm ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            matrices,
            orderedRenderCommandQueue,
            armHoldingValue
         );
      }

      matrices.pop();
   }

   @Unique
   private static void corz$resolveLancing() {
      if (!corz$lancingResolved) {
         corz$lancingResolved = true;

         try {
            Class<?> lancingClass = Class.forName("net.minecraft.client.render.entity.state.Lancing");

            for (Method m : lancingClass.getDeclaredMethods()) {
               if ((m.getName().equals("method_75391") || m.getName().equals("applySwingTransforms") || m.getName().equals("applySwingOffset"))
                  && m.getParameterCount() == 4) {
                  m.setAccessible(true);
                  corz$applySwingTransformsMethod = m;
               }

               if ((m.getName().equals("method_75396") || m.getName().equals("applyUseTransforms") || m.getName().equals("applyUseOffset"))
                  && m.getParameterCount() == 5) {
                  m.setAccessible(true);
                  corz$applyUseTransformsMethod = m;
               }
            }
         } catch (Exception e) {
         }
      }
   }

   @Unique
   private static void corz$invokeLancingSwing(float corzinvokeLancingValue, MatrixStack matrixStack, int side, Arm arm) {
      corz$resolveLancing();
      if (corz$applySwingTransformsMethod != null) {
         try {
            corz$applySwingTransformsMethod.invoke(null, corzinvokeLancingValue, matrixStack, side, arm);
         } catch (Exception e) {
         }
      }
   }

   @Unique
   private static void corz$invokeLancingUse(float corzinvokeLancingValue, MatrixStack matrixStack, float f, Arm arm, ItemStack stack) {
      corz$resolveLancing();
      if (corz$applyUseTransformsMethod != null) {
         try {
            corz$applyUseTransformsMethod.invoke(null, corzinvokeLancingValue, matrixStack, f, arm, stack);
         } catch (Exception e) {
         }
      }
   }
}
