package com.corz.client.utils;

import com.corz.client.CorzClient;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.NativeHud;
import com.corz.client.gui.TwoDidUtils;
import com.corz.client.inventory.DrawMatricesUtils;
import com.corz.client.mixins.AccessorGameRenderer;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogRenderer.FogType;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.joml.Matrix3x2fStack;
import com.corz.client.categories.visuals.ShowHUD;

public final class HotbarHud {
   private static VertexTextureFont vertexTextureBarColor;
   private static float hotbarAmount;
   private static final float[] hotbarMatrixTable = new float[10];
   private static float hotbarMatrixAmount;
   private static boolean hotbarHudActive;
   private static final float[] barColorTable = new float[1000];
   private static int lastForcesKey = -1;
   private static final float[] hotbarHudTable = new float[10];
   private static ItemStack empty = ItemStack.EMPTY;
   private static int hotbarHudValue;
   private static final float[] emptyTable = new float[10];
   private static boolean fogRendererActive;
   private static final long[] table = new long[9];
   private static final HotbarHud.Spring[] springs = new HotbarHud.Spring[9];
   private static int scaleFactor = 2;
   private static final float[] SLOT_FLASH = new float[10];
   private static final Item[] emptyItems = new Item[10];
   private static final Color WHITE = new Color(-1, true);
   private static final long[] SLOT_EMPTY_TIMES = new long[10];
   private static final int SELECTED_SLOT_COLOR = TwoDidUtils.getValue(3, 25);
   private static final MatrixStack MATRIX_STACK = new MatrixStack();
   private static float hotbarAmount7HotbarMatrix;
   private static int hotbarValue = -1;
   private static final HotbarHud.Spring SPRING = new HotbarHud.Spring();
   private static float hotbarAmount7Hotbar;
   private static final String[] hotbarHudTexts = new String[1000];
   private static final Color TEXT_SHADOW_COLOR = new Color(-939524096, true);
   private static long lastNanoTime;
   private static final String HOTBAR = "hotbar";
   private static final HotbarHud.Spring SELECTION_SPRING = new HotbarHud.Spring();
   private static long lastNanoTimeHotbarMatrix;
   private static float hotbarHudAmount;
   private static float attackProgress;
   private static long lastNanoTimeFactorForces;
   private static final int SLOT_BORDER_COLOR = TwoDidUtils.getValue(6, 24);
   private static final int[] LAST_STACK_COUNTS = new int[10];
   private static int lastSelectedSlot = -1;
   private static final float[] SLOT_SCALE = new float[10];

   private static int getValue(float hotbarHudLimit) {
      return hotbarHudLimit > 0.5F ? CardMatrixUtils.getValue(-1535700, -8393375, (hotbarHudLimit - 0.5F) * 2.0F) : CardMatrixUtils.getValue(-1750963, -1535700, hotbarHudLimit * 2.0F);
   }

   private static float getAmount(long[] hotbarHudValues, int index, long hotbarHudValue, float hotbarHudFactor) {
      long hotbarHudValue2 = hotbarHudValues[index];
      if (hotbarHudValue2 == 0L) {
         return 1.0F;
      } else {
         float hotbarHudLimit = (float)(hotbarHudValue - hotbarHudValue2) / 2.4E8F;
         if (hotbarHudLimit >= 1.0F) {
            hotbarHudValues[index] = 0L;
            return 1.0F;
         } else {
            return 1.0F + hotbarHudFactor * NativeHud.getAmountForFloat(hotbarHudLimit);
         }
      }
   }

   private static void renderHotbarMatrix(
      DrawContext context,
      RenderTickCounter renderTickCounter,
      MinecraftClient client,
      ClientPlayerEntity player,
      long nanoTime,
      int hotbarValue2,
      boolean hotbarMatrixFlag,
      float hotbarMatrixValue,
      float hotbarMatrixDivisor,
      boolean hotbarMatrixFlag2,
      int scaledWidth,
      int scaledHeight
   ) {
      float hotbarMatrixFactor = hotbarMatrixFlag2 ? 1.0F / hotbarMatrixDivisor : 1.0F;
      float hotbarMatrixBound = MatrixPeekUtilsRender.getAmount();
      float hotbarMatrixValue4 = 182.0F * hotbarMatrixFactor;
      float nativeHudValue = 22.0F * hotbarMatrixFactor;
      float hotbarMatrixValue6 = (scaledWidth / 2 - 91) * hotbarMatrixFactor;
      float hotbarMatrixValue7 = (scaledHeight - 22) * hotbarMatrixFactor;
      float amount4 = NativeHud.getAmount4(hotbarMatrixValue6);
      float hotbarMatrixValue9 = NativeHud.getAmount4(hotbarMatrixValue7);
      float panelOverlayBound = NativeHud.getAmount4(hotbarMatrixValue4);
      float hashHuntBound = NativeHud.getAmount4(nativeHudValue);
      hotbarAmount7Hotbar = hashHuntBound;
      float configsLikeValue = 0.0F;
      float glfwLimit = 0.0F;
      ShowHUD.HudScale hudScale = null;
      if (ShowHUD.canHudHotbar("hotbar")) {
         configsLikeValue = ShowHUD.getAmount4("hotbar");
         glfwLimit = ShowHUD.getAmountFloat("hotbar");
         hotbarMatrixAmount = amount4 + configsLikeValue;
         hotbarHudAmount = hotbarMatrixValue9 + glfwLimit;
         hotbarAmount = panelOverlayBound;
         hotbarAmount7HotbarMatrix = hashHuntBound;
         hotbarValue = hotbarValue2;
         hudScale = ShowHUD.getHudHotbar("hotbar", hotbarMatrixAmount, hotbarHudAmount, hotbarAmount, hotbarAmount7HotbarMatrix);
      }

      float hotbarMatrixValue12 = amount4 + Math.round(configsLikeValue);
      float hotbarMatrixValue13 = hotbarMatrixValue9 + Math.round(glfwLimit);
      PlayerInventory inventory = player.getInventory();
      int max2 = Math.max(0, Math.min(8, inventory.getSelectedSlot()));
      ItemStack offHandStack = player.getOffHandStack();
      boolean empty3 = !offHandStack.isEmpty();
      boolean attackFlag = player.getMainArm().getOpposite() == Arm.LEFT;
      if (hotbarMatrixFlag) {
         SELECTION_SPRING.handleMaxAmount(max2);

         for (int i = 0; i < 9; i++) {
            springs[i].handleMaxAmount(i == max2 ? 1.0F : 0.0F);
         }

         SPRING.handleMaxAmount(empty3 ? 1.0F : 0.0F);
         lastNanoTimeHotbarMatrix = nanoTime;
         attackProgress = 0.0F;
      } else if (max2 != lastSelectedSlot) {
         table[max2] = nanoTime;
      }

      lastSelectedSlot = max2;
      float maxAmount = SELECTION_SPRING.getMaxAmount(max2, hotbarMatrixValue, 42.0F, 0.8F);
      float nativeHudLimit = SPRING.getMaxAmount(empty3 ? 1.0F : 0.0F, hotbarMatrixValue, 30.0F, 0.55F);
      if (empty3) {
         if (empty.isEmpty() || empty.getItem() != offHandStack.getItem()) {
            empty = offHandStack.copy();
         }
      } else if (nativeHudLimit <= 0.01F) {
         empty = ItemStack.EMPTY;
      }

      float hotbarMatrixLimit = (float)(nanoTime - lastNanoTimeHotbarMatrix) / 2.2E8F;
      float panelOverlayLimit = hotbarMatrixLimit >= 1.0F ? 1.0F : NativeHud.getAmount5(hotbarMatrixLimit / 0.6F);
      float x2 = hotbarMatrixLimit >= 1.0F ? 1.0F : 0.95F + 0.050000012F * NativeHud.getAmount(hotbarMatrixLimit, 1.7F);
      float crossLimit = 20.0F * hotbarMatrixFactor;
      float barColorFactor = 16.0F * hotbarMatrixFactor;
      float hotbarMatrixValue17 = 1.0F * hotbarMatrixFactor;
      float lerpUuidFactor = 4.5F * hotbarMatrixFactor;
      float hotbarMatrixValue19 = hotbarMatrixValue12 + (panelOverlayBound - 9.0F * crossLimit) / 2.0F;
      float matrixStackValue = hotbarMatrixValue13 + (hashHuntBound - crossLimit) / 2.0F;
      float max3 = Math.max(hotbarMatrixBound, NativeHud.getAmount4(1.0F * hotbarMatrixFactor));
      int releaseRight = NativeHud.getReleaseRight();
      float hotbarMatrixValue21 = hotbarMatrixValue19 + maxAmount * crossLimit;
      if (SELECTION_SPRING.isFlag()) {
         hotbarMatrixValue21 = NativeHud.getAmount4(hotbarMatrixValue21);
      }

      float x = hotbarMatrixValue12 + panelOverlayBound / 2.0F;
      float y = hotbarMatrixValue13 + hashHuntBound;
      boolean hotbarMatrixFlag5 = Math.abs(x2 - 1.0F) > 5.0E-4F;
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);
      if (hudScale != null) {
         hudScale.handleAmountTranslate(MATRIX_STACK);
      }

      if (hotbarMatrixFlag5) {
         handleTranslatePush(MATRIX_STACK, x, y, x2);
      }

      Matrix3x2fStack matrices = context.getMatrices();
      matrices.pushMatrix();

      try {
         if (hotbarMatrixFlag2) {
            matrices.scale(hotbarMatrixDivisor, hotbarMatrixDivisor);
         }

         if (hudScale != null) {
            hudScale.handleAmountTranslate(matrices);
         }

         if (hotbarMatrixFlag5) {
            matrices.translate(x, y);
            matrices.scale(x2, x2);
            matrices.translate(-x, -y);
         }

         NativeHud.handle(SLOT_BORDER_COLOR, hotbarMatrixValue12, hotbarMatrixValue13, panelOverlayBound, hashHuntBound, 1.0F);

         for (int j = 0; j < 9; j++) {
            float hotbarMatrixLimit2 = NativeHud.getMax(Math.abs(j - maxAmount) * 1.5F);
            if (hotbarMatrixLimit2 > 0.01F) {
               NativeHud.handlePeekPosition(
                  hotbarMatrixValue19 + j * crossLimit + hotbarMatrixValue17,
                  matrixStackValue + hotbarMatrixValue17,
                  crossLimit - 2.0F * hotbarMatrixValue17,
                  crossLimit - 2.0F * hotbarMatrixValue17,
                  lerpUuidFactor - hotbarMatrixValue17,
                  0.0F,
                  NativeHud.getValue(184549375, hotbarMatrixLimit2)
               );
            }
         }

         NativeHud.handlePeekPosition(hotbarMatrixValue21, matrixStackValue, crossLimit, crossLimit, lerpUuidFactor, 0.0F, NativeHud.getValue(releaseRight, 0.14F));
         NativeHud.handlePeekPosition(hotbarMatrixValue21, matrixStackValue, crossLimit, crossLimit, lerpUuidFactor, hotbarMatrixBound, NativeHud.getValue(releaseRight, 0.9F));
         float hotbarMatrixValue24 = 22.0F * hotbarMatrixFactor;
         float peekPositionValue = attackFlag ? hotbarMatrixValue12 - 29.0F * hotbarMatrixFactor : hotbarMatrixValue12 + panelOverlayBound + 7.0F * hotbarMatrixFactor;
         float max26 = NativeHud.getMax(nativeHudLimit);
         float hotbarMatrixFactor4 = 0.8F + 0.19999999F * nativeHudLimit;
         boolean hotbarMatrixFlag6 = nativeHudLimit > 0.01F && !empty.isEmpty();
         if (hotbarMatrixFlag6) {
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(max26);
            boolean hotbarMatrixFlag7 = Math.abs(hotbarMatrixFactor4 - 1.0F) > 5.0E-4F;
            if (hotbarMatrixFlag7) {
               handleTranslatePush(MATRIX_STACK, peekPositionValue + hotbarMatrixValue24 / 2.0F, hotbarMatrixValue13 + hashHuntBound / 2.0F, hotbarMatrixFactor4);
            }

            try {
               NativeHud.handle(SELECTED_SLOT_COLOR, peekPositionValue, hotbarMatrixValue13, hotbarMatrixValue24, hashHuntBound, 1.0F);
            } finally {
               if (hotbarMatrixFlag7) {
                  MATRIX_STACK.pop();
               }

               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         }

         boolean translateMatrixFlag = fogRendererActive;

         for (int k = 0; k < 9; k++) {
            ItemStack stack = inventory.getStack(k);
            handleEmptyTimes(k, stack, nanoTime, hotbarMatrixFlag);
            float maxAmount3 = springs[k].getMaxAmount(k == max2 ? 1.0F : 0.0F, hotbarMatrixValue, 36.0F, 0.5F);
            if (stack.isEmpty()) {
               SLOT_FLASH[k] = 0.0F;
            } else {
               float hotbarMatrixValue27 = getAmount(table, k, nanoTime, 0.1F) * getAmount(SLOT_EMPTY_TIMES, k, nanoTime, 0.16F);
               float hotbarMatrixValue28 = hotbarMatrixValue19 + k * crossLimit + (crossLimit - barColorFactor) / 2.0F;
               float hotbarMatrixValue29 = hotbarMatrixValue13 + (hashHuntBound - barColorFactor) / 2.0F - max3 * maxAmount3;
               hotbarHudTable[k] = hotbarMatrixValue28;
               hotbarMatrixTable[k] = hotbarMatrixValue29;
               SLOT_FLASH[k] = hotbarMatrixValue27;
               SLOT_SCALE[k] = 1.0F;
               renderTranslateMatrix(context, client, stack, hotbarMatrixValue28, hotbarMatrixValue29, barColorFactor, hotbarMatrixValue27, translateMatrixFlag);
            }
         }

         handleEmptyTimes(9, offHandStack, nanoTime, hotbarMatrixFlag);
         SLOT_FLASH[9] = 0.0F;
         if (hotbarMatrixFlag6) {
            float hotbarMatrixValue30 = hotbarMatrixFactor4 * getAmount(SLOT_EMPTY_TIMES, 9, nanoTime, 0.16F);
            float translateMatrixValue = peekPositionValue + (hotbarMatrixValue24 - barColorFactor) / 2.0F;
            float hotbarMatrixValue32 = hotbarMatrixValue13 + (hashHuntBound - barColorFactor) / 2.0F;
            float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(max26);

            try {
               renderTranslateMatrix(context, client, empty3 ? offHandStack : empty, translateMatrixValue, hotbarMatrixValue32, barColorFactor, hotbarMatrixValue30, translateMatrixFlag && empty3);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
            }

            if (empty3) {
               hotbarHudTable[9] = translateMatrixValue;
               hotbarMatrixTable[9] = hotbarMatrixValue32;
               SLOT_FLASH[9] = hotbarMatrixValue30;
               SLOT_SCALE[9] = max26;
            }
         }

         boolean hotbarMatrixFlag8 = !translateMatrixFlag && canFogRenderer(client);
         float hotbarMatrixValue33 = 8.0F * hotbarMatrixFactor;
         float max34 = Math.max(hotbarMatrixBound, NativeHud.getAmount4(0.75F * hotbarMatrixFactor));
         NativeHud.handlePeekPosition(hotbarMatrixValue21 + (crossLimit - hotbarMatrixValue33) / 2.0F, NativeHud.getAmount4(hotbarMatrixValue13 + 18.5F * hotbarMatrixFactor), hotbarMatrixValue33, max34, max34 / 2.0F, 0.0F, releaseRight);
         if (hotbarMatrixFlag8) {
            float tickProgress = renderTickCounter.getTickProgress(true);

            for (int m = 0; m <= 9; m++) {
               if (SLOT_FLASH[m] > 0.0F) {
                  handleBarColor(
                     player,
                     m < 9 ? inventory.getStack(m) : offHandStack,
                     m,
                     hotbarHudTable[m],
                     hotbarMatrixTable[m],
                     barColorFactor,
                     SLOT_FLASH[m],
                     SLOT_SCALE[m],
                     hotbarMatrixFactor,
                     hotbarMatrixBound,
                     tickProgress,
                     hotbarMatrixValue
                  );
               }
            }
         }

         handleAmountAttack(client, player, attackFlag, hotbarMatrixValue12, hotbarMatrixValue13, panelOverlayBound, hashHuntBound, hotbarMatrixFactor, hotbarMatrixBound, hotbarMatrixValue, releaseRight);
      } finally {
         matrices.popMatrix();
         if (hotbarMatrixFlag5) {
            MATRIX_STACK.pop();
         }

         if (hudScale != null) {
            MATRIX_STACK.pop();
         }

         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   public static float getHotbarAmount() {
      return ShowHUD.getTotemsHotbar() - hotbarHudValue <= 1 ? hotbarAmount7Hotbar : 0.0F;
   }

   private static void renderScaledWidth(DrawContext context, RenderTickCounter renderTickCounter, MinecraftClient client, ClientPlayerEntity player) {
      long nanoTime = System.nanoTime();
      int value = ShowHUD.getTotemsHotbar();
      boolean scaledWidthFlag = lastNanoTime == 0L || value != hotbarHudValue && value != hotbarHudValue + 1;
      float scaledWidthValue = scaledWidthFlag ? 0.0F : Math.min(0.05F, (float)(nanoTime - lastNanoTime) / 1.0E9F);
      hotbarHudValue = value;
      lastNanoTime = nanoTime;
      Window window = client.getWindow();
      float scaledWidthDivisor = (float)((double)getFactorForces(client, window, nanoTime) / window.getScaleFactor());
      boolean scaledWidthFlag2 = Math.abs(scaledWidthDivisor - 1.0F) > 1.0E-4F;
      int scaledWidth = window.getScaledWidth();
      int scaledHeight = window.getScaledHeight();
      MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
      MatrixStack matrixStack2 = MatrixPeekUtilsRender.getMatrixStack();
      CorzClient.fontManager.setMatrixStack(MATRIX_STACK);
      MatrixPeekUtilsRender.handleMatrixStack(MATRIX_STACK);
      if (scaledWidthFlag2) {
         CorzRoundedUtils.scaledWidth = Math.round(scaledWidth / scaledWidthDivisor);
         CorzRoundedUtils.scaledHeight = Math.round(scaledHeight / scaledWidthDivisor);
      }

      try {
         renderHotbarMatrix(context, renderTickCounter, client, player, nanoTime, value, scaledWidthFlag, scaledWidthValue, scaledWidthDivisor, scaledWidthFlag2, scaledWidth, scaledHeight);
      } finally {
         if (scaledWidthFlag2) {
            CorzRoundedUtils.scaledWidth = 0.0F;
            CorzRoundedUtils.scaledHeight = 0.0F;
         }

         CorzClient.fontManager.setMatrixStack(matrixStack);
         MatrixPeekUtilsRender.handleMatrixStack(matrixStack2);
      }
   }

   private static float getAmounts(VertexTextureFont vertexTextureFont, int index, String hotbarHudText) {
      if (index >= 0 && index < barColorTable.length) {
         float hotbarHudValue = barColorTable[index];
         if (hotbarHudValue < 0.0F) {
            barColorTable[index] = hotbarHudValue = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(hotbarHudText);
         }

         return hotbarHudValue;
      } else {
         return vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(hotbarHudText);
      }
   }

   public static boolean canInstanceFlag(DrawContext context, RenderTickCounter renderTickCounter) {
      if (!hotbarHudActive && context != null && renderTickCounter != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player == null) {
            return false;
         } else {
            try {
               renderScaledWidth(context, renderTickCounter, instance, player);
               return true;
            } catch (Throwable e) {
               hotbarHudActive = true;
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private static int getFactorForces(MinecraftClient client, Window window, long nanoTime) {
      int value2 = (window.getWidth() * 31 + window.getHeight()) * 31 + window.getFramebufferHeight() + (client.forcesUnicodeFont() ? 1 : 0);
      if (value2 != lastForcesKey || nanoTime - lastNanoTimeFactorForces > 1000000000L) {
         lastForcesKey = value2;
         lastNanoTimeFactorForces = nanoTime;
         scaleFactor = window.calculateScaleFactor(ShowHUD.getMax(client), client.forcesUnicodeFont());
      }

      return scaleFactor;
   }

   private static void handleEmptyTimes(int index, ItemStack stack, long emptyValue, boolean emptyFlag) {
      boolean nowEmpty = stack.isEmpty();
      Item item = nowEmpty ? null : stack.getItem();
      int emptyLimit = nowEmpty ? 0 : stack.getCount();
      if (!emptyFlag && item != null && (item != emptyItems[index] || emptyLimit > LAST_STACK_COUNTS[index])) {
         SLOT_EMPTY_TIMES[index] = emptyValue;
      }

      if (item != emptyItems[index]) {
         emptyTable[index] = Float.NaN;
      }

      emptyItems[index] = item;
      LAST_STACK_COUNTS[index] = emptyLimit;
   }

   private static void handleTranslatePush(MatrixStack matrices, float x, float y, float x2) {
      matrices.push();
      matrices.translate(x, y, 0.0F);
      matrices.scale(x2, x2, 1.0F);
      matrices.translate(-x, -y, 0.0F);
   }

   private HotbarHud() {
   }

   static {
      for (int i = 0; i < 9; i++) {
         springs[i] = new HotbarHud.Spring();
      }

      Arrays.fill(emptyTable, Float.NaN);
   }

   public static void updateHotbarAmount() {
      if (hotbarValue == ShowHUD.getTotemsHotbar()) {
         ShowHUD.getHudHotbar("hotbar", hotbarMatrixAmount, hotbarHudAmount, hotbarAmount, hotbarAmount7HotbarMatrix);
      }
   }

   private static boolean canFogRenderer(MinecraftClient client) {
      try {
         AccessorGameRenderer gameRenderer = (AccessorGameRenderer)client.gameRenderer;
         gameRenderer.corzGuiRenderer().render(gameRenderer.corzFogRenderer().getFogBuffer(FogType.NONE));
         return true;
      } catch (Throwable e) {
         fogRendererActive = true;
         return false;
      }
   }

   private static String getCount(int index) {
      if (index >= 0 && index < hotbarHudTexts.length) {
         String hotbarHudText = hotbarHudTexts[index];
         if (hotbarHudText == null) {
            hotbarHudTexts[index] = hotbarHudText = Integer.toString(index);
         }

         return hotbarHudText;
      } else {
         return Integer.toString(index);
      }
   }

   private static void handleAmountAttack(
      MinecraftClient client, ClientPlayerEntity player, boolean attackFlag, float attackValue, float nativeHudValue, float panelOverlayBound, float hashHuntBound, float hotbarMatrixFactor, float attackBound, float hotbarMatrixValue, int releaseRight
   ) {
      boolean attackFlag2 = client.options.getAttackIndicator().getValue() == AttackIndicator.HOTBAR;
      float textureVertexLimit = attackFlag2 ? NativeHud.getMax(player.getAttackCooldownProgress(0.0F)) : 1.0F;
      float attackValue9 = textureVertexLimit < 1.0F ? 1.0F : 0.0F;
      attackProgress = hotbarMatrixValue <= 0.0F ? attackValue9 : attackProgress + (attackValue9 - attackProgress) * (1.0F - (float)Math.exp(-hotbarMatrixValue / 0.06F));
      if (attackProgress < 0.01F) {
         if (attackValue9 == 0.0F) {
            attackProgress = 0.0F;
         }
      } else {
         float attackValue10 = 4.5F * hotbarMatrixFactor;
         float hotbarMatrixBound = Math.max(attackBound, NativeHud.getAmount4(1.25F * hotbarMatrixFactor));
         float lerpUuidFactor = attackValue10 + 2.5F * hotbarMatrixFactor;
         float textureVertexValue = attackFlag ? attackValue + panelOverlayBound + 7.0F * hotbarMatrixFactor + lerpUuidFactor : attackValue - 7.0F * hotbarMatrixFactor - lerpUuidFactor;
         float attackValue14 = nativeHudValue + hashHuntBound / 2.0F;
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(attackProgress);

         try {
            NativeHud.handlePeekPosition(textureVertexValue - lerpUuidFactor, attackValue14 - lerpUuidFactor, lerpUuidFactor * 2.0F, lerpUuidFactor * 2.0F, lerpUuidFactor, 0.0F, -1945104364);
            NativeHud.handleTextureVertex(textureVertexValue, attackValue14, attackValue10, hotbarMatrixBound, 1.0F, 822083583);
            NativeHud.handleTextureVertex(textureVertexValue, attackValue14, attackValue10, hotbarMatrixBound, textureVertexLimit, releaseRight);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static void renderTranslateMatrix(DrawContext context, MinecraftClient client, ItemStack stack, float translateMatrixValue, float translateMatrixValue2, float barColorFactor, float translateMatrixValue4, boolean translateMatrixFlag) {
      Matrix3x2fStack matrices = context.getMatrices();
      matrices.pushMatrix();

      try {
         if (Math.abs(translateMatrixValue4 - 1.0F) > 5.0E-4F) {
            float translateMatrixValue5 = translateMatrixValue + barColorFactor / 2.0F;
            float translateMatrixValue6 = translateMatrixValue2 + barColorFactor / 2.0F;
            matrices.translate(translateMatrixValue5, translateMatrixValue6);
            matrices.scale(translateMatrixValue4, translateMatrixValue4);
            matrices.translate(-translateMatrixValue5, -translateMatrixValue6);
         }

         matrices.translate(translateMatrixValue, translateMatrixValue2);
         float translateMatrixValue7 = barColorFactor / 16.0F;
         if (Math.abs(translateMatrixValue7 - 1.0F) > 1.0E-4F) {
            matrices.scale(translateMatrixValue7, translateMatrixValue7);
         }

         DrawMatricesUtils.renderMatricesMatrix(context, stack, 0, 0, 16);
         if (translateMatrixFlag) {
            context.drawStackOverlay(client.textRenderer, stack, 0, 0);
         }
      } finally {
         matrices.popMatrix();
      }
   }

   private static void handleBarColor(
      ClientPlayerEntity player, ItemStack stack, int index, float barColorValue, float barColorValue2, float barColorFactor, float x2, float panelOverlayLimit, float hotbarMatrixFactor, float barColorBound, float tickProgress, float hotbarMatrixValue
   ) {
      boolean itemBarVisible = stack.isItemBarVisible();
      int barColorValue8 = stack.getCount();
      float cooldownProgress = player.getItemCooldownManager().getCooldownProgress(stack, tickProgress);
      if (!itemBarVisible) {
         emptyTable[index] = Float.NaN;
      }

      if (itemBarVisible || barColorValue8 != 1 || !(cooldownProgress <= 0.001F)) {
         boolean barColorFlag = Math.abs(x2 - 1.0F) > 5.0E-4F;
         if (barColorFlag) {
            handleTranslatePush(MATRIX_STACK, barColorValue + barColorFactor / 2.0F, barColorValue2 + barColorFactor / 2.0F, x2);
         }

         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            if (cooldownProgress > 0.001F) {
               float barColorValue9 = barColorFactor * Math.min(1.0F, cooldownProgress);
               NativeHud.handlePeekPosition(
                  barColorValue, barColorValue2 + barColorFactor - barColorValue9, barColorFactor, barColorValue9, Math.min(3.0F * hotbarMatrixFactor, barColorValue9 / 2.0F), 0.0F, NativeHud.getValue(1207959551, cooldownProgress * 6.0F)
               );
            }

            if (itemBarVisible) {
               boolean damageable = stack.isDamageable();
               float barColorValue10 = damageable
                  ? NativeHud.getMax(1.0F - (float)stack.getDamage() / Math.max(1, stack.getMaxDamage()))
                  : NativeHud.getMax(stack.getItemBarStep() / 13.0F);
               float long2floatopenhashmap = emptyTable[index];
               float hotbarHudLimit;
               if (!Float.isNaN(long2floatopenhashmap) && !(hotbarMatrixValue <= 0.0F)) {
                  hotbarHudLimit = long2floatopenhashmap + (barColorValue10 - long2floatopenhashmap) * (1.0F - (float)Math.exp(-hotbarMatrixValue / 0.12F));
                  if (Math.abs(barColorValue10 - hotbarHudLimit) < 0.002F) {
                     hotbarHudLimit = barColorValue10;
                  }
               } else {
                  hotbarHudLimit = barColorValue10;
               }

               emptyTable[index] = hotbarHudLimit;
               float barColorFactor3 = 12.0F * hotbarMatrixFactor;
               float crossLimit = Math.max(barColorBound, NativeHud.getAmount4(1.0F * hotbarMatrixFactor));
               float peekPositionValue = barColorValue + 2.0F * hotbarMatrixFactor;
               float matrixStackValue = NativeHud.getAmount4(barColorValue2 + 13.5F * hotbarMatrixFactor);
               NativeHud.handlePeekPosition(peekPositionValue - barColorBound, matrixStackValue - barColorBound, barColorFactor3 + 2.0F * barColorBound, crossLimit + 2.0F * barColorBound, crossLimit / 2.0F + barColorBound, 0.0F, -1291845632);
               NativeHud.handlePeekPosition(
                  peekPositionValue, matrixStackValue, Math.max(crossLimit, barColorFactor3 * hotbarHudLimit), crossLimit, crossLimit / 2.0F, 0.0F, damageable ? getValue(hotbarHudLimit) : 0xFF000000 | stack.getItemBarColor()
               );
            }

            if (barColorValue8 != 1) {
               VertexTextureFont vertexTexture = NativeHud.getFontRenderer();
               if (vertexTexture != vertexTextureBarColor) {
                  vertexTextureBarColor = vertexTexture;
                  Arrays.fill(barColorTable, -1.0F);
               }

               float barColorLimit = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrownBrownActive();
               if (barColorLimit > 0.01F) {
                  float pilotStatusBound = 5.0F * hotbarMatrixFactor;
                  float nativeHudFactor = pilotStatusBound / barColorLimit;
                  String barColorText = getCount(barColorValue8);
                  float barColorValue15 = getAmounts(vertexTexture, barColorValue8, barColorText) * nativeHudFactor;
                  float barColorValue16 = NativeHud.getAmount4(barColorValue + 16.75F * hotbarMatrixFactor - barColorValue15);
                  float nativeHudValue = barColorValue2 + 16.0F * hotbarMatrixFactor - pilotStatusBound;
                  float f13 = NativeHud.getAmount2(nativeHudValue, pilotStatusBound, nativeHudFactor);
                  NativeHud.handlePeekPosition(barColorValue16, nativeHudValue, barColorValue15, pilotStatusBound, pilotStatusBound / 2.0F, 1375731712, 2.0F * hotbarMatrixFactor, true);
                  NativeHud.handlePushScale(barColorText, barColorValue16 + barColorBound, f13 + barColorBound, nativeHudFactor, TEXT_SHADOW_COLOR);
                  NativeHud.handlePushScale(barColorText, barColorValue16, f13, nativeHudFactor, WHITE);
               }
            }
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            if (barColorFlag) {
               MATRIX_STACK.pop();
            }
         }
      }
   }

   private static final class Spring {
      private float position;
      private float velocity;
      private boolean springActive = true;

      void handleMaxAmount(float max) {
         this.position = max;
         this.velocity = 0.0F;
         this.springActive = true;
      }

      boolean isFlag() {
         return this.springActive;
      }

      float getMaxAmount(float max, float springFactor, float springFactor2, float springFactor3) {
         float planValuesItem = this.position - max;
         if (springFactor <= 0.0F) {
            return this.position;
         } else if (Math.abs(planValuesItem) < 0.001F && Math.abs(this.velocity) < 0.02F) {
            this.position = max;
            this.velocity = 0.0F;
            this.springActive = true;
            return this.position;
         } else {
            this.springActive = false;
            float springFactor5 = springFactor3 * springFactor2;
            float springFactor6 = springFactor2 * (float)Math.sqrt(1.0F - springFactor3 * springFactor3);
            float exp = (float)Math.exp(-springFactor5 * springFactor);
            float cos = (float)Math.cos(springFactor6 * springFactor);
            float sin = (float)Math.sin(springFactor6 * springFactor);
            float springFactor7 = (this.velocity + springFactor5 * planValuesItem) / springFactor6;
            this.position = max + exp * (planValuesItem * cos + springFactor7 * sin);
            this.velocity = exp * ((springFactor7 * springFactor6 - springFactor5 * planValuesItem) * cos - (planValuesItem * springFactor6 + springFactor5 * springFactor7) * sin);
            return this.position;
         }
      }
   }
}
