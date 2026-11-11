package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.config.BooleanSetting;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.MatrixEspUtils;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class RenderBeta {
   private boolean rendererActive = false;
   private int boxX;
   private int releaseRightValue;
   private final QuintScissor hoverAnimation;
   private int rendererValue;
   private int betaValue;
   private final QuintScissor rendererQuintScissor;
   private final BooleanSetting next;

   public boolean canFlag(double betaValue, double mouseY, int button) {
      if (button == 0 && this.canFlag(betaValue, mouseY)) {
         this.next.toggle();
         return true;
      } else {
         return false;
      }
   }

   private void render(DrawContext context, int betaValue, int betaValue2, int iconSize, int cardMatrixValue) {
      MatrixPeekUtilsRender.renderMatrixStack(context, betaValue - iconSize, betaValue2 - iconSize, iconSize * 2, iconSize * 2, iconSize, cardMatrixValue);
   }

   private void renderReleaseRight(DrawContext context, double releaseRightValue, double releaseRightLimit) {
      double cardMatrixFactor = getAmount(releaseRightValue);
      byte releaseRightValue3 = 5;
      int releaseRightValue4 = this.boxX + this.betaValue - 4 - releaseRightValue3;
      int releaseRightValue5 = this.rendererValue + this.releaseRightValue / 2;
      int releaseRightValue6 = CardMatrixUtils.getValue(-14012614, CardMatrixUtils.getReleaseRight(0.55), cardMatrixFactor);
      int cardMatrixValue = CardMatrixUtils.getValue(-9669504, MatrixEspUtils.releaseRightValue, cardMatrixFactor);
      if (releaseRightLimit > 0.004) {
         cardMatrixValue = CardMatrixUtils.getValue(cardMatrixValue, -1, 0.12 * releaseRightLimit);
      }

      MatrixPeekUtilsRender.renderMatrixStack(context, releaseRightValue4 - releaseRightValue3, releaseRightValue5 - releaseRightValue3, releaseRightValue3 * 2, releaseRightValue3 * 2, releaseRightValue3, releaseRightValue6);
      int iconSize = releaseRightValue3 - 2;
      MatrixPeekUtilsRender.renderMatrixStack(context, releaseRightValue4 - iconSize, releaseRightValue5 - iconSize, iconSize * 2, iconSize * 2, iconSize, cardMatrixValue);
   }

   public boolean canFlag(double betaLimit, double mouseY) {
      int betaValue = this.boxX + this.betaValue - 4 - RenderPillUtils.getValue() - 2;
      return betaLimit >= betaValue && betaLimit <= this.boxX + this.betaValue && mouseY >= this.rendererValue && mouseY <= this.rendererValue + this.releaseRightValue;
   }

   public void renderTimeRenderer(DrawContext context, int rendererValue, int mouseY) {
      CorzClient.fontRenderer()
         .handleForStringFloat(
            this.next.getSettingName(), this.boxX + 4, CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown(this.rendererValue, this.releaseRightValue), new Color(-3947581)
         );
      boolean booleanValue = this.next.isEnabled();
      boolean rendererFlag = this.canFlag(rendererValue, mouseY);
      double rendererValue3 = booleanValue ? 1.0 : 0.0;
      if (Math.abs(this.rendererQuintScissor.getTargetAmount() - rendererValue3) > 0.01) {
         this.rendererQuintScissor.handleAmountTime(rendererValue3);
      }

      double rendererValue4 = this.rendererQuintScissor.getAmount();
      if (rendererFlag && !this.rendererActive) {
         this.rendererActive = true;
         this.hoverAnimation.handleAmountTime(1.0);
      } else if (!rendererFlag && this.rendererActive) {
         this.rendererActive = false;
         this.hoverAnimation.handleAmountTime(0.0);
      }

      double releaseRightLimit = this.hoverAnimation.getAmount();
      if (ThemeCustomizer.equals) {
         this.render(context, rendererValue4, releaseRightLimit);
      } else {
         this.renderReleaseRight(context, rendererValue4, releaseRightLimit);
      }
   }

   private static double getAmount(double betaLimit) {
      return betaLimit < 0.0 ? 0.0 : (betaLimit > 1.0 ? 1.0 : betaLimit);
   }

   public void handle(int value, int editClusterValue, int width, int betaText) {
      this.boxX = value;
      this.rendererValue = editClusterValue;
      this.betaValue = width;
      this.releaseRightValue = betaText;
   }

   public RenderBeta(BooleanSetting next, int value, int value2, int value3, int value4) {
      this.next = next;
      this.boxX = value;
      this.rendererValue = value2;
      this.betaValue = value3;
      this.releaseRightValue = value4;
      this.rendererQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 300, next.isEnabled() ? 1.0 : 0.0);
      this.hoverAnimation = new QuintScissor(QuintBounceType.SINE_OUT, 200, 0.0);
   }

   public int getReleaseRightValue() {
      return this.releaseRightValue;
   }

   private void render(DrawContext context, double betaValue, double releaseRightLimit) {
      RenderPillUtils.render(context, this.boxX + this.betaValue, this.rendererValue, this.releaseRightValue, 4, betaValue, releaseRightLimit);
   }
}
