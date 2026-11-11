package com.corz.client.render;

import com.corz.client.mixins.AccessorHandledScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import com.corz.client.categories.donutsmp.GamblingRigger;

public class FillDraw {
   private final HandledScreen<?> handledScreen;
   private final AccessorHandledScreen accessorHandledScreen;
   private final GamblingRigger gamblingRigger;

   private void renderLiteralDraw(DrawContext context, TextRenderer textRenderer, String literalDrawText, int literalDrawValue, int y2, int releaseRightValue, boolean shadow2) {
      context.drawText(textRenderer, Text.literal(literalDrawText), literalDrawValue - textRenderer.getWidth(literalDrawText) / 2, y2, releaseRightValue, shadow2);
   }

   private void renderFillGambling(DrawContext context, TextRenderer textRenderer, int gamblingValue, int limit, int initializeClientValue, boolean gamblingFlag, int color) {
      int x1 = this.getValue(gamblingValue);
      int y1 = this.getValueForInt(gamblingValue);
      boolean gamblingFlag2 = this.gamblingRigger.getKeptSlotIndex() == gamblingValue && this.gamblingRigger.getPhase() != GamblingRigger.Phase.IDLE;
      boolean gamblingFlag3 = gamblingFlag && canFlag(limit, initializeClientValue, x1, y1, 22, 22);
      String gamblingText = Integer.toString(gamblingValue + 1);
      int literalDrawValue = x1 + 11;
      int y2 = y1 + 7;
      if (gamblingFlag2) {
         context.fill(x1, y1, x1 + 22, y1 + 22, color);
         renderFill(context, x1, y1, 22, 22, getValue3(color));
         this.renderLiteralDraw(context, textRenderer, gamblingText, literalDrawValue, y2, -16119284, false);
      } else {
         context.fill(x1, y1, x1 + 22, y1 + 22, gamblingFlag3 ? getValue(color, 0.22F) : -15460837);
         renderFill(context, x1, y1, 22, 22, gamblingFlag3 ? getValue(color, 0.6F) : -14013389);
         int releaseRightValue = !gamblingFlag ? -8421505 : (gamblingFlag3 ? getValue3(color) : -1);
         this.renderLiteralDraw(context, textRenderer, gamblingText, literalDrawValue, y2, releaseRightValue, false);
      }
   }

   private int getValue() {
      return this.getCorzY() + 8 + 12 + 4;
   }

   private int getValue(int n) {
      return this.getAccessorHandled() + 8 + n % 3 * 26;
   }

   private static void renderFill(DrawContext context, int x1, int y1, int n, int n2, int color2) {
      context.fill(x1, y1, x1 + n, y1 + 1, color2);
      context.fill(x1, y1 + n2 - 1, x1 + n, y1 + n2, color2);
      context.fill(x1, y1, x1 + 1, y1 + n2, color2);
      context.fill(x1 + n - 1, y1, x1 + n, y1 + n2, color2);
   }

   private int getValueInt() {
      return this.getValue() + 74 + 6;
   }

   private int getValue3() {
      return this.getValue5() + 16 + 7 - this.getCorzY();
   }

   private int getValue4() {
      return this.getAccessorHandled() + 8;
   }

   public boolean canGamblingRigger(double gamblingRiggerValue, double initializeClientValue, int gamblingRiggerValue3) {
      if (!this.gamblingRigger.isEnabled()) {
         return false;
      } else if (!canFlag(gamblingRiggerValue, initializeClientValue, this.getAccessorHandled(), this.getCorzY(), this.getValue6(), this.getValue3())) {
         return false;
      } else {
         if (gamblingRiggerValue3 == 0) {
            if (this.gamblingRigger.isPhaseIdle()) {
               for (int i = 0; i < 9; i++) {
                  if (canFlag(gamblingRiggerValue, initializeClientValue, this.getValue(i), this.getValueForInt(i), 22, 22)) {
                     this.gamblingRigger.startExtraction(i);
                     return true;
                  }
               }
            }

            if (this.gamblingRigger.isExtractedPhase() && canFlag(gamblingRiggerValue, initializeClientValue, this.getValue4(), this.getValueInt(), 74, 16)) {
               this.gamblingRigger.startRestore();
               return true;
            }

            if (canFlag(gamblingRiggerValue, initializeClientValue, this.getValue4(), this.getValue7(), 74, 12)) {
               this.gamblingRigger.resetWithNotice();
               return true;
            }
         }

         return true;
      }
   }

   public FillDraw(HandledScreen<?> handledScreen, GamblingRigger gamblingRigger) {
      this.handledScreen = handledScreen;
      this.accessorHandledScreen = (AccessorHandledScreen)handledScreen;
      this.gamblingRigger = gamblingRigger;
   }

   public void renderGamblingRigger(DrawContext context, int gamblingRiggerValue, int initializeClientValue) {
      if (this.gamblingRigger.isEnabled()) {
         TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
         int releaseRightValue = MatrixEspUtils.releaseRightValue;
         int accessorHandled = this.getAccessorHandled();
         int corzY = this.getCorzY();
         int value6 = this.getValue6();
         int n2 = this.getValue3();
         context.fill(accessorHandled - 1, corzY - 1, accessorHandled + value6 + 1, corzY + n2 + 1, -435549171);
         context.fill(accessorHandled, corzY, accessorHandled + value6, corzY + n2, -16184564);
         renderFill(context, accessorHandled, corzY, value6, n2, getValue(releaseRightValue, 0.45F));
         this.renderLiteralDraw(context, textRenderer, "Gambling Rigger", accessorHandled + value6 / 2, corzY + 8, releaseRightValue, false);
         boolean phaseIdle = this.gamblingRigger.isPhaseIdle();

         for (int i = 0; i < 9; i++) {
            this.renderFillGambling(context, textRenderer, i, gamblingRiggerValue, initializeClientValue, phaseIdle, releaseRightValue);
         }

         this.renderFill(context, textRenderer, this.getValue4(), this.getValueInt(), 74, 16, "Restore", this.gamblingRigger.isExtractedPhase(), gamblingRiggerValue, initializeClientValue, releaseRightValue);
         boolean gamblingRiggerFlag = this.gamblingRigger.getPhase() != GamblingRigger.Phase.IDLE || this.gamblingRigger.getKeptSlotIndex() >= 0;
         this.renderFill(context, textRenderer, this.getValue4(), this.getValue7(), 74, 12, "Reset", gamblingRiggerFlag, gamblingRiggerValue, initializeClientValue, releaseRightValue);

         String gamblingRiggerText = switch (this.gamblingRigger.getPhase()) {
            case EXTRACTING -> "Taking... " + this.gamblingRigger.getPendingMoveCount();
            case EXTRACTED -> "Kept #" + (this.gamblingRigger.getKeptSlotIndex() + 1);
            case RESTORING -> "Restoring... " + this.gamblingRigger.getPendingMoveCount();
            default -> "Pick a slot";
         };
         this.renderLiteralDraw(context, textRenderer, gamblingRiggerText, accessorHandled + value6 / 2, this.getValue5() + 4, -8421505, false);
      }
   }

   private int getAccessorHandled() {
      int accessorHandled = this.accessorHandledScreen.corzX() + this.accessorHandledScreen.corzBackgroundWidth() + 6;
      if (accessorHandled + this.getValue6() > this.handledScreen.width) {
         int accessorHandled2 = this.accessorHandledScreen.corzX() - this.getValue6() - 6;
         if (accessorHandled2 >= 2) {
            return accessorHandled2;
         }
      }

      return accessorHandled;
   }

   private int getValue5() {
      return this.getValue7() + 12 + 5;
   }

   private int getValue6() {
      return 90;
   }

   private int getCorzY() {
      return this.accessorHandledScreen.corzY();
   }

   private static int getValue(int n, float f) {
      return ((int)(f * 255.0F) & 0xFF) << 24 | n & 16777215;
   }

   private int getValueForInt(int n) {
      return this.getValue() + n / 3 * 26;
   }

   private static boolean canFlag(double limit, double initializeClientValue, int n, int n2, int n3, int n4) {
      return limit >= n && limit < n + n3 && initializeClientValue >= n2 && initializeClientValue < n2 + n4;
   }

   private static int getValue3(int n) {
      int min2 = Math.min(255, (n >> 16 & 0xFF) + 60);
      int min3 = Math.min(255, (n >> 8 & 0xFF) + 60);
      int min4 = Math.min(255, (n & 0xFF) + 60);
      return 0xFF000000 | min2 << 16 | min3 << 8 | min4;
   }

   private int getValue7() {
      return this.getValueInt() + 16 + 3;
   }

   private void renderFill(DrawContext context, TextRenderer textRenderer, int x1, int y1, int n, int n2, String text, boolean flag, int n3, int n4, int n5) {
      boolean flag2 = flag && canFlag(n3, n4, x1, y1, n, n2);
      int color = !flag ? -15724010 : (flag2 ? getValue(n5, 0.28F) : -15263200);
      context.fill(x1, y1, x1 + n, y1 + n2, color);
      renderFill(context, x1, y1, n, n2, flag ? getValue(n5, flag2 ? 0.7F : 0.4F) : -14013389);
      int n6 = !flag ? -8421505 : (flag2 ? getValue3(n5) : -1);
      this.renderLiteralDraw(context, textRenderer, text, x1 + n / 2, y1 + (n2 - 8) / 2, n6, false);
   }
}
