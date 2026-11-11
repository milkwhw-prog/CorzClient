package com.corz.client.utils;

import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.C2Renderer;
import com.corz.client.utils.C2Cues;
import com.corz.client.utils.C2Animator;
import com.corz.client.utils.C2Assets;

public final class C2Butterfly {
   private static boolean c2ButterflyActive;
   private static final BiFunction<AbstractClientPlayerEntity, ItemStack, C2Knife> BI_FUNCTION = (stack, player) -> C2Knife.BUTTERFLY;
   private static BiFunction<AbstractClientPlayerEntity, ItemStack, C2Knife> biFunction = C2Butterfly.BI_FUNCTION;

   public static void handle(
      AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int c2ButterflyValue, KnifeFinish.Finish finish, boolean c2ButterflyFlag
   ) {
      C2Renderer.handleModelPop(abstractClientPlayerEntity, matrixStack, orderedRenderCommandQueue, c2ButterflyValue, finish, c2ButterflyFlag);
   }

   public static List<C2Cues.Contact> getItems() {
      return C2Animator.getClipLoaded();
   }

   static C2Knife getPlayerHand() {
      MinecraftClient instance = MinecraftClient.getInstance();
      return instance.player == null ? null : getFunction(instance.player, instance.player.getMainHandStack());
   }

   public static void handleFlag(boolean swappedSlotFlag) {
      c2ButterflyActive = swappedSlotFlag;
      if (swappedSlotFlag) {
         C2Assets.updateButterfly();
      } else {
         C2Animator.updateStaticIdleLoaded();
      }
   }

   public static String formatText() {
      return C2Animator.formatClipStatic();
   }

   public static boolean isFlag() {
      return C2Animator.isInspectClip();
   }

   public static boolean canFlag(C2Knife c2Knife) {
      return C2Assets.canVisualsLoad(c2Knife);
   }

   public static void handleFunction(BiFunction<AbstractClientPlayerEntity, ItemStack, C2Knife> biFunction2) {
      biFunction = biFunction2 == null ? BI_FUNCTION : biFunction2;
   }

   public static float getAmount() {
      return C2Animator.getClipLength();
   }

   public static float getAmountFloat() {
      return C2Animator.getClipTimeClip2StaticIdle();
   }

   public static void handleInstanceFlag(boolean c2ButterflyFlag) {
      if (c2ButterflyActive) {
         C2Assets.updateButterfly();
         C2Knife playerHand = getPlayerHand();
         if (playerHand != null) {
            C2Assets.handleSupplyAsync(playerHand);
         }
      }

      C2Animator.handleHandFocused(MinecraftClient.getInstance(), c2ButterflyFlag);
   }

   public static void update() {
      C2Animator.updateStaticLoaded();
   }

   public static String formatTextString() {
      return C2Assets.formatButterfly();
   }

   public static void updateFlag() {
      C2Animator.updateStaticIdleLoaded();
      C2Assets.updateVisualsLoaded();
      if (c2ButterflyActive) {
         C2Assets.updateButterfly();
      }
   }

   public static boolean canFlag(LivingEntity livingEntity, Hand hand) {
      return C2Animator.canStaticLoaded(livingEntity, hand);
   }

   public static boolean isC2ButterflyActive() {
      return c2ButterflyActive;
   }

   public static C2Knife getC2Knife() {
      return C2Animator.getLoadedKnife();
   }

   public static boolean canFlag(AbstractClientPlayerEntity abstractClientPlayerEntity, Hand hand, ItemStack itemStack) {
      return C2Renderer.canRiptidePerspective(abstractClientPlayerEntity, hand, itemStack);
   }

   static C2Knife getFunction(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack) {
      return abstractClientPlayerEntity != null && itemStack != null ? biFunction.apply(abstractClientPlayerEntity, itemStack) : null;
   }

   private C2Butterfly() {
   }

   public static long getTime() {
      return C2Animator.getClipTime();
   }

   public static void handle(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int c2ButterflyValue) {
      C2Renderer.handle(abstractClientPlayerEntity, matrixStack, orderedRenderCommandQueue, c2ButterflyValue);
   }

   public static String formatText(C2Knife c2Knife) {
      return C2Assets.formatLoadedLoading(c2Knife);
   }

   public static boolean isFlag3() {
      return C2Assets.isButterfly();
   }

   public static void handle(C2Knife c2Knife) {
      C2Assets.handleSupplyAsync(c2Knife);
   }

   public static boolean canFlagForC2Knife(C2Knife c2Knife) {
      C2Assets.handleSupplyAsync(c2Knife);
      return C2Assets.getSupplyAsync(c2Knife) != null;
   }
}
