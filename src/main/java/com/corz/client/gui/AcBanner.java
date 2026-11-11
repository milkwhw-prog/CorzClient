package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import java.util.ArrayDeque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import com.corz.client.categories.visuals.ShowHUD;

public final class AcBanner {
   private static final int round = Math.round(178.5F);
   private static float titleWidth;
   private static final Color OFF_WHITE = new Color(-920587, true);
   private static final Color GRAY = new Color(-7630437, true);
   private static final Color GREEN = new Color(-8393375, true);
   private static final Color RED = new Color(-1750963, true);
   private static float acBannerAmount;
   private static AcBanner.Card pollFirst;
   private static final ArrayDeque<AcBanner.Card> ARRAY_DEQUE = new ArrayDeque<>();
   private static float corzAmount;
   private static long maxNanoTime;
   private static final MatrixStack MATRIX_STACK = new MatrixStack();
   private static float pollAmount = -1.0F;
   private static String corzText = "";
   private static String matrixStackText = "";
   private static long nanoTime2Poll;
   private static String acBannerText = "";
   private static float bannerWidth = 200.0F;

   private static void handleMatrixStack(AcBanner.Card card, int matrixStackValue, float matrixStackLimit, float scaledWindowLimit) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      if (fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() != pollAmount) {
         handleCorzText(fontRenderer, card);
      }

      float matrixStackValue2 = -54.0F;
      float nativeHudValue;
      float panelOverlayLimit;
      float x;
      if (scaledWindowLimit >= 0.0F) {
         float matrixStackFactor = NativeHud.getMax(scaledWindowLimit / 380.0F);
         nativeHudValue = 10.0F + (matrixStackValue2 - 10.0F) * getAmountForFloat(matrixStackFactor);
         panelOverlayLimit = 1.0F - matrixStackFactor * matrixStackFactor;
         x = 1.0F;
      } else {
         float matrixStackFactor2 = getAmount(matrixStackLimit / 560.0F);
         nativeHudValue = matrixStackValue2 + (10.0F - matrixStackValue2) * matrixStackFactor2;
         panelOverlayLimit = NativeHud.getAmount5(matrixStackLimit / 308.0F);
         x = 0.94F + 0.060000002F * matrixStackFactor2;
      }

      if (!(panelOverlayLimit <= 0.002F)) {
         boolean matrixStackFlag = scaledWindowLimit < 0.0F && matrixStackLimit >= 560.0F;
         float amount4 = NativeHud.getAmount4((matrixStackValue - bannerWidth) / 2.0F);
         if (matrixStackFlag) {
            nativeHudValue = NativeHud.getAmount4(nativeHudValue);
         }

         Color color = card.good() ? GREEN : RED;
         int matrixStackValue6 = (card.good() ? -8393375 : -1750963) & 16777215;
         float matrixStackValue7 = MatrixPeekUtilsRender.getAmount();
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);
         boolean matrixStackFlag2 = Math.abs(x - 1.0F) > 5.0E-4F;
         if (matrixStackFlag2) {
            MATRIX_STACK.push();
            MATRIX_STACK.translate(amount4 + bannerWidth / 2.0F, nativeHudValue + 20.0F, 0.0F);
            MATRIX_STACK.scale(x, x, 1.0F);
            MATRIX_STACK.translate(-(amount4 + bannerWidth / 2.0F), -(nativeHudValue + 20.0F), 0.0F);
         }

         try {
            NativeHud.handle(NativeHud.resizeScrollValue, amount4, nativeHudValue, bannerWidth, 40.0F, 1.0F);
            float matrixStackValue8 = amount4 + 8.0F;
            float matrixStackValue9 = nativeHudValue + 9.0F;
            float matrixStackValue10 = 4.5F;
            NativeHud.handle(matrixStackValue8, matrixStackValue9, 22.0F, 22.0F, 7.0F, 1.0F);
            MatrixPeekUtilsIdentifier.handle(matrixStackValue8 + matrixStackValue10, matrixStackValue9 + matrixStackValue10, 13.0F, 13.0F, IcoGenUtils.getIdentifierForString("shield"), color, MATRIX_STACK);
            float matrixStackValue11 = matrixStackValue8 + 22.0F + 9.0F;
            float matrixStackValue12 = 24.0F;
            float matrixStackValue13 = nativeHudValue + (40.0F - matrixStackValue12) / 2.0F;
            handleMatrixStack(fontRenderer, card.title(), matrixStackValue11, getAmount(matrixStackValue13, 12.0F, 1.0F), 1.0F, OFF_WHITE);
            if (corzAmount > 0.0F) {
               float matrixStackValue14 = matrixStackValue11 + titleWidth + 6.0F;
               float matrixStackValue15 = matrixStackValue13 + 0.0F;
               NativeHud.handlePeekPosition(matrixStackValue14, matrixStackValue15, corzAmount, 12.0F, 6.0F, 0.0F, 771751936 | matrixStackValue6);
               handleMatrixStack(fontRenderer, corzText, matrixStackValue14 + 5.0F, getAmount(matrixStackValue15, 12.0F, 0.76F), 0.76F, color);
            }

            float acBannerValue = matrixStackValue13 + 12.0F + 2.0F;
            float matrixStackValue17 = getAmount(acBannerValue, 10.0F, 0.84F);
            handleMatrixStack(fontRenderer, matrixStackText, matrixStackValue11, matrixStackValue17, 0.84F, GRAY);
            handleMatrixStack(fontRenderer, acBannerText, matrixStackValue11 + fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(matrixStackText) * 0.84F, matrixStackValue17, 0.84F, NativeHud.getWhite());
            float peekPositionValue = amount4 + 8.0F;
            float matrixStackValue19 = nativeHudValue + 40.0F - matrixStackValue7 * 2.0F;
            float matrixStackFactor3 = bannerWidth - 16.0F;
            NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue19, matrixStackFactor3, matrixStackValue7, 0.0F, 0.0F, 318767103);
            float matrixStackFactor4 = scaledWindowLimit >= 0.0F ? 0.0F : 1.0F - NativeHud.getMax((matrixStackLimit - 560.0F) / 4200.0F);
            float crossLimit = NativeHud.getAmount4(matrixStackFactor3 * matrixStackFactor4);
            if (crossLimit > 0.0F) {
               NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue19, crossLimit, matrixStackValue7, 0.0F, 0.0F, round << 24 | matrixStackValue6);
            }
         } finally {
            if (matrixStackFlag2) {
               MATRIX_STACK.pop();
            }

            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   public static void updatePollDeque() {
      ARRAY_DEQUE.clear();
      pollFirst = null;
      maxNanoTime = 0L;
   }

   public static void handleDequeArray(AcBanner.Card card) {
      if (card != null) {
         long nanoTime = System.nanoTime();
         if (pollFirst == null) {
            handlePollMax(card, nanoTime);
         } else {
            while (ARRAY_DEQUE.size() >= 2) {
               ARRAY_DEQUE.pollFirst();
            }

            ARRAY_DEQUE.addLast(card);
            if (maxNanoTime == 0L) {
               maxNanoTime = Math.max(nanoTime, nanoTime2Poll + 1400000000L);
            }
         }
      }
   }

   private AcBanner() {
   }

   public static boolean isPoll() {
      return pollFirst != null;
   }

   static float getAmount(float acBannerValue) {
      float acBannerFactor = 1.0F - NativeHud.getMax(acBannerValue);
      return 1.0F - acBannerFactor * acBannerFactor * acBannerFactor * acBannerFactor * acBannerFactor;
   }

   private static float getAmount(float acBannerValue, float acBannerValue2, float acBannerValue3) {
      return acBannerValue + acBannerValue2 / 2.0F - 8.28F * acBannerValue3;
   }

   public static void renderScaledWindow(DrawContext context) {
      if (pollFirst != null && context != null) {
         long max = System.nanoTime();
         float millis = (float)(max - nanoTime2Poll) / 1000000.0F;
         if (maxNanoTime == 0L && millis >= 4760.0F) {
            maxNanoTime = max;
         }

         float scaledWindowLimit = maxNanoTime != 0L && max >= maxNanoTime ? (float)(max - maxNanoTime) / 1000000.0F : -1.0F;
         if (scaledWindowLimit >= 380.0F) {
            AcBanner.Card card = ARRAY_DEQUE.pollFirst();
            if (card == null) {
               pollFirst = null;
               return;
            }

            handlePollMax(card, max);
            millis = 0.0F;
            scaledWindowLimit = -1.0F;
         }

         MinecraftClient instance = MinecraftClient.getInstance();
         double scaleFactor = instance.getWindow().getScaleFactor();
         int calculateScaleFactor = instance.getWindow().calculateScaleFactor(ShowHUD.getMax(instance), instance.forcesUnicodeFont());
         float scaledWindowDivisor = (float)(calculateScaleFactor / scaleFactor);
         boolean scaledWindowFlag = Math.abs(scaledWindowDivisor - 1.0F) > 1.0E-4F;
         int scaledWidth = scaledWindowFlag ? Math.round(instance.getWindow().getScaledWidth() / scaledWindowDivisor) : instance.getWindow().getScaledWidth();
         int scaledHeight = scaledWindowFlag ? Math.round(instance.getWindow().getScaledHeight() / scaledWindowDivisor) : instance.getWindow().getScaledHeight();
         MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
         MatrixStack matrixStack2 = MatrixPeekUtilsRender.getMatrixStack();
         CorzClient.fontManager.setMatrixStack(MATRIX_STACK);
         MatrixPeekUtilsRender.handleMatrixStack(MATRIX_STACK);
         if (scaledWindowFlag) {
            CorzRoundedUtils.scaledWidth = scaledWidth;
            CorzRoundedUtils.scaledHeight = scaledHeight;
         }

         try {
            handleMatrixStack(pollFirst, scaledWidth, millis, scaledWindowLimit);
         } finally {
            if (scaledWindowFlag) {
               CorzRoundedUtils.scaledWidth = 0.0F;
               CorzRoundedUtils.scaledHeight = 0.0F;
            }

            CorzClient.fontManager.setMatrixStack(matrixStack);
            MatrixPeekUtilsRender.handleMatrixStack(matrixStack2);
         }
      }
   }

   private static void handlePollMax(AcBanner.Card card, long nanoTime) {
      pollFirst = card;
      nanoTime2Poll = nanoTime;
      maxNanoTime = 0L;
      pollAmount = -1.0F;
   }

   private static void handleCorzText(VertexTextureFont vertexTextureFont, AcBanner.Card card) {
      pollAmount = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzBrown();
      float corzValue = 289.0F;
      titleWidth = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(card.title()) * 1.0F;
      corzText = vertexTextureFont.formatText(card.place(), Math.max(20.0F, (corzValue - titleWidth - 6.0F) / 0.76F - 10.0F));
      corzAmount = corzText.isEmpty() ? 0.0F : vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzText) * 0.76F + 10.0F;
      String line = card.line();
      acBannerText = line.endsWith(" - corz") ? " - corz" : "";
      String substring = line.substring(0, line.length() - acBannerText.length());
      float corzValue2 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(acBannerText) * 0.84F;
      matrixStackText = vertexTextureFont.formatText(substring, Math.max(20.0F, (corzValue - corzValue2) / 0.84F));
      acBannerAmount = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(matrixStackText) * 0.84F + corzValue2;
      float max3 = Math.max(titleWidth + (corzAmount > 0.0F ? 6.0F + corzAmount : 0.0F), acBannerAmount);
      bannerWidth = Math.max(200.0F, Math.min(340.0F, 39.0F + max3 + 8.0F + 4.0F));
   }

   static float getAmountForFloat(float acBannerValue) {
      float acBannerFactor = NativeHud.getMax(acBannerValue);
      return acBannerFactor * acBannerFactor * acBannerFactor;
   }

   private static void handleMatrixStack(VertexTextureFont vertexTextureFont, String matrixStackText, float matrixStackValue, float matrixStackValue2, float x2, Color color) {
      if (!matrixStackText.isEmpty()) {
         MATRIX_STACK.push();

         try {
            MATRIX_STACK.scale(x2, x2, 1.0F);
            vertexTextureFont.handleForStringFloat(matrixStackText, matrixStackValue / x2, matrixStackValue2 / x2, color);
         } finally {
            MATRIX_STACK.pop();
         }
      }
   }

   public record Card(String title, String place, String line, boolean good) {
   }
}
