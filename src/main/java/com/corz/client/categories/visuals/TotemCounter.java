package com.corz.client.categories.visuals;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.NativeHud;
import com.corz.client.gui.TickProgress;
import com.corz.client.inventory.DrawMatricesUtils;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.VertexTextureFont;
import com.corz.client.render.VertexTexturePeek;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix3x2fStack;
import ai.auth.Module;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class TotemCounter extends Module {
   private boolean classicFlag;
   private final QuintScissor classicScaleAnimation;
   private final QuintScissor totemsMatrixQuintScissor;
   private final QuintScissor totemCounterQuintScissor;
   private Color color;
   private boolean classicAutoFlag;
   private static final String[] totemsRendererTexts = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
   private final QuintScissor scaledClassicQuintScissor;
   private int lastTotemCount;
   private static final String TOTEMS = "totems";
   private String totemsMatrixText;
   private int totemsMatrixValue;
   private int totemsValue;
   private final QuintScissor quintScissor;
   private float digitWidth;
   private VertexTextureFont fontRenderer;
   private static final ItemStack ITEM_STACK = new ItemStack(Items.TOTEM_OF_UNDYING);
   private final QuintScissor matrixSlideAnimation;
   private final QuintScissor matrixWidthAnimation;
   private final BooleanSetting booleanBase;
   private final BooleanSetting showLabel;
   private final QuintScissor matrixPopAnimation;
   private static final String TOTEMS_LABEL = "TOTEMS";
   private final MatrixStack matrixStack;
   private final EnumSetting enumHelper = new EnumSetting(
      "Style", "Follow the HUD skin, or force the native pill / classic card", "Auto", "Auto", "Native", "Classic"
   );
   private float totemsAmount;
   private static final Color GRAY = new Color(-7630437, true);
   private final float[] totemsAmounts;
   private float totemsRendererAmount;
   private String totemCounterText;
   private float totemsMatrixAmount;

   private static void handleTranslatePush(MatrixStack matrices, float x, float y, float x2) {
      matrices.push();
      matrices.translate(x, y, 0.0F);
      matrices.scale(x2, x2, 1.0F);
      matrices.translate(-x, -y, 0.0F);
   }

   private void handleTotemsRenderer(VertexTextureFont fontRenderer) {
      if (fontRenderer != this.fontRenderer) {
         this.fontRenderer = fontRenderer;
         float totemsRendererBound = 0.0F;

         for (int i = 0; i < 10; i++) {
            this.totemsAmounts[i] = fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(totemsRendererTexts[i]) * 0.95F;
            totemsRendererBound = Math.max(totemsRendererBound, this.totemsAmounts[i]);
         }

         this.digitWidth = (float)Math.ceil(totemsRendererBound);
         this.totemsMatrixAmount = fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("TOTEMS") * 0.7F;
         this.totemsRendererAmount = MatrixStackUtilsAmount.getAmount(0.0F, 16.0F, 0.95F);
         this.totemsAmount = MatrixStackUtilsAmount.getAmount(0.0F, 16.0F, 0.7F);
         this.totemsValue = 0;
      }
   }

   @Override
   protected void onReset() {
      this.updateTimeText();
   }

   private void handleMatrixStack(VertexTextureFont vertexTextureFont, String matrixStackText, float matrixStackValue, float matrixStackValue2, float x2, Color color) {
      this.matrixStack.push();

      try {
         this.matrixStack.scale(x2, x2, 1.0F);
         vertexTextureFont.handleForStringFloat(matrixStackText, matrixStackValue / x2, matrixStackValue2 / x2, color);
      } finally {
         this.matrixStack.pop();
      }
   }

   private void handleMatrixStack(VertexTextureFont vertexTextureFont, String matrixStackText, float matrixStackTotal, float matrixStackValue, Color color) {
      this.matrixStack.push();

      try {
         this.matrixStack.scale(0.95F, 0.95F, 1.0F);

         for (int i = 0; i < matrixStackText.length(); i++) {
            int index = matrixStackText.charAt(i) - '0';
            if (index >= 0 && index <= 9) {
               vertexTextureFont.handleForStringFloat(totemsRendererTexts[index], (matrixStackTotal + (this.digitWidth - this.totemsAmounts[index]) / 2.0F) / 0.95F, matrixStackValue / 0.95F, color);
               matrixStackTotal += this.digitWidth;
            }
         }
      } finally {
         this.matrixStack.pop();
      }
   }

   private static float getAmount(int totemCounterDivisor) {
      int maxRound = Math.max(1, Math.round(8.0F * totemCounterDivisor / 16.0F));
      return 16.0F * maxRound / totemCounterDivisor;
   }

   public void renderScaledClassic(DrawContext context) {
      if (mc.player != null && context != null) {
         if (this.lastTotemCount < 0) {
            this.updateTimeText();
         }

         if (this.lastTotemCount >= 0) {
            CorzClient.fontManager.setMatrixStack(this.matrixStack);
            MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
            double scaleFactor = mc.getWindow().getScaleFactor();
            int calculateScaleFactor = mc.getWindow().calculateScaleFactor(ShowHUD.getMax(mc), mc.forcesUnicodeFont());
            float scaledClassicDivisor = (float)(calculateScaleFactor / scaleFactor);
            boolean scaledClassicFlag = Math.abs(scaledClassicDivisor - 1.0F) > 1.0E-4F;
            int scaledWidth = mc.getWindow().getScaledWidth();
            int scaledHeight = mc.getWindow().getScaledHeight();
            int scaledWidth2 = scaledClassicFlag ? Math.round(scaledWidth / scaledClassicDivisor) : scaledWidth;
            int scaledHeight2 = scaledClassicFlag ? Math.round(scaledHeight / scaledClassicDivisor) : scaledHeight;
            if (scaledClassicFlag) {
               CorzRoundedUtils.scaledWidth = scaledWidth2;
               CorzRoundedUtils.scaledHeight = scaledHeight2;
            }

            try {
               float scaledClassicFactor = ShowHUD.getTime();
               if (scaledClassicFactor < 0.004F) {
                  return;
               }

               String formatText = this.enumHelper.getCurrentOption();
               boolean classicAutoFlag = "Classic".equals(formatText) || "Auto".equals(formatText) && !ShowHUD.isFlag();
               if (classicAutoFlag != this.classicAutoFlag) {
                  this.classicAutoFlag = classicAutoFlag;
                  this.classicFlag = true;
               }

               if (!classicAutoFlag) {
                  if (this.classicFlag) {
                     this.classicFlag = false;
                     this.scaledClassicQuintScissor.handleAmountFlag(0.0);
                     this.scaledClassicQuintScissor.handleAmountTime(1.0);
                     this.classicScaleAnimation.handleAmountFlag(0.0);
                     this.classicScaleAnimation.handleAmountTime(1.0);
                  }

                  float panelOverlay = CorzRoundedUtils.getPanelOverlay(scaledClassicFactor * (float)this.scaledClassicQuintScissor.getAmount());

                  try {
                     this.renderTotemsMatrix(context, scaledWidth2, scaledClassicDivisor, calculateScaleFactor, scaledHeight);
                     return;
                  } finally {
                     CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                  }
               }

               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(scaledClassicFactor);

               try {
                  this.renderMatricesTotems(context, scaledWidth2, scaledClassicDivisor, scaledHeight, this.getUndyingStack());
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            } finally {
               if (scaledClassicFlag) {
                  CorzRoundedUtils.scaledWidth = 0.0F;
                  CorzRoundedUtils.scaledHeight = 0.0F;
               }
            }
         }
      }
   }

   private void renderTotemsMatrix(DrawContext context, int totemsMatrixValue, float totemsMatrixDivisor, int totemCounterDivisor, int scaledHeight) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      this.handleTotemsRenderer(fontRenderer);
      boolean booleanValue = this.showLabel.isEnabled();
      boolean enabled = this.booleanBase.isEnabled();
      float totemsMatrixValue4 = getAmount(totemCounterDivisor);
      int textLength = this.totemCounterText.length();
      double totemsMatrixValue5 = booleanValue ? 1.0 : 0.0;
      double totemsMatrixValue6 = enabled && this.lastTotemCount == 0 ? 1.0 : 0.0;
      double totemsMatrixValue7 = enabled && this.lastTotemCount == 1 ? 1.0 : 0.0;
      int totemsValue = Math.round(6.0F + totemsMatrixValue4 + 5.0F + textLength * this.digitWidth + (booleanValue ? 6.0F + this.totemsMatrixAmount : 0.0F) + 6.0F);
      if (this.totemsValue == 0) {
         this.totemCounterQuintScissor.handleAmountFlag(totemsValue);
         this.matrixWidthAnimation.handleAmountFlag(totemsMatrixValue5);
         this.matrixSlideAnimation.handleAmountFlag(totemsMatrixValue6);
         this.totemsMatrixQuintScissor.handleAmountFlag(totemsMatrixValue7);
         this.totemsValue = totemsValue;
      } else if (totemsValue != this.totemsValue) {
         this.totemCounterQuintScissor.handleAmountTime(totemsValue);
         this.totemsValue = totemsValue;
      }

      if (this.matrixWidthAnimation.getTargetAmount() != totemsMatrixValue5) {
         this.matrixWidthAnimation.handleAmountTime(totemsMatrixValue5);
      }

      if (this.matrixSlideAnimation.getTargetAmount() != totemsMatrixValue6) {
         this.matrixSlideAnimation.handleAmountTime(totemsMatrixValue6);
      }

      if (this.totemsMatrixQuintScissor.getTargetAmount() != totemsMatrixValue7) {
         this.totemsMatrixQuintScissor.handleAmountTime(totemsMatrixValue7);
      }

      float panelOverlayBound = (float)this.totemCounterQuintScissor.getAmount();
      float totemsMatrixValue9 = (scaledHeight - 22.0F) / totemsMatrixDivisor - ShowHUD.getHotbarOffsets();
      float round = Math.round((totemsMatrixValue - totemsValue) / 2.0F);
      float totemsMatrixValue11 = totemsMatrixValue9 - Math.max(16.0F, ShowHUD.getKeybindsResources() + 6.0F) - 16.0F;
      float glfwLimit = ShowHUD.getAmount4("totems");
      float amountFloat = ShowHUD.getAmountFloat("totems");
      ShowHUD.HudScale hudHotbar = ShowHUD.getHudHotbar("totems", round + glfwLimit, totemsMatrixValue11 + amountFloat, totemsValue, 16.0F);
      float peekPositionValue = round + Math.round(glfwLimit) + (totemsValue - panelOverlayBound) / 2.0F;
      float nativeHudValue = totemsMatrixValue11 + Math.round(amountFloat);
      float x = peekPositionValue + panelOverlayBound / 2.0F;
      float y = nativeHudValue + 8.0F;
      float x2 = 0.92F + 0.07999998F * (float)this.classicScaleAnimation.getAmount();
      boolean totemsMatrixFlag = Math.abs(x2 - 1.0F) > 5.0E-4F;
      if (hudHotbar != null) {
         hudHotbar.handleAmountTranslate(this.matrixStack);
      }

      if (totemsMatrixFlag) {
         handleTranslatePush(this.matrixStack, x, y, x2);
      }

      try {
         float totemsMatrixLimit = (float)this.matrixSlideAnimation.getAmount();
         float cardMatrixFactor = (float)this.totemsMatrixQuintScissor.getAmount();
         NativeHud.handle(NativeHud.scrollValue, peekPositionValue, nativeHudValue, panelOverlayBound, 16.0F, 1.0F);
         if (totemsMatrixLimit > 0.004F) {
            NativeHud.handlePeekPosition(peekPositionValue, nativeHudValue, panelOverlayBound, 16.0F, 8.0F, MatrixPeekUtilsRender.getAmount(), NativeHud.getValue(1021659213, totemsMatrixLimit));
         }

         float totemsMatrixValue20 = peekPositionValue + 6.0F;
         float totemsMatrixValue21 = nativeHudValue + (16.0F - totemsMatrixValue4) / 2.0F;
         float totemsMatrixValue22 = 1.0F + 0.22F * (1.0F - (float)this.quintScissor.getAmount());
         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();

         try {
            matrices.scale(totemsMatrixDivisor, totemsMatrixDivisor);
            if (hudHotbar != null) {
               hudHotbar.handleAmountTranslate(matrices);
            }

            if (totemsMatrixFlag) {
               matrices.translate(x, y);
               matrices.scale(x2, x2);
               matrices.translate(-x, -y);
            }

            if (Math.abs(totemsMatrixValue22 - 1.0F) > 5.0E-4F) {
               float totemsMatrixValue23 = totemsMatrixValue20 + totemsMatrixValue4 / 2.0F;
               float totemsMatrixValue24 = totemsMatrixValue21 + totemsMatrixValue4 / 2.0F;
               matrices.translate(totemsMatrixValue23, totemsMatrixValue24);
               matrices.scale(totemsMatrixValue22, totemsMatrixValue22);
               matrices.translate(-totemsMatrixValue23, -totemsMatrixValue24);
            }

            matrices.translate(totemsMatrixValue20, totemsMatrixValue21);
            matrices.scale(totemsMatrixValue4 / 16.0F, totemsMatrixValue4 / 16.0F);
            DrawMatricesUtils.renderMatricesMatrix(context, ITEM_STACK, 0, 0, 16);
         } finally {
            matrices.popMatrix();
         }

         int value = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-920587, -1750963, totemsMatrixLimit), -1535700, cardMatrixFactor);
         if (this.color == null || value != this.totemsMatrixValue) {
            this.totemsMatrixValue = value;
            this.color = new Color(value, true);
         }

         float matrixStackTotal = totemsMatrixValue20 + totemsMatrixValue4 + 5.0F;
         float matrixStackValue = NativeHud.getAmount4(nativeHudValue + this.totemsRendererAmount);
         float totemsMatrixValue27 = textLength * this.digitWidth;
         float totemsMatrixValue28 = matrixStackTotal + totemsMatrixValue27 / 2.0F;
         float totemsMatrixValue29 = (float)this.matrixPopAnimation.getAmount();
         if (totemsMatrixValue29 != 1.0F) {
            float totemsMatrixFactor = Math.min(1.0F, (float)this.matrixPopAnimation.getTime() / 140.0F);
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(1.0F - totemsMatrixFactor * totemsMatrixFactor);

            try {
               this.handleMatrixStack(fontRenderer, this.totemsMatrixText, totemsMatrixValue28 - this.totemsMatrixText.length() * this.digitWidth / 2.0F, matrixStackValue, this.color);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }

            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(getAmount(totemsMatrixFactor));
            handleTranslatePush(this.matrixStack, totemsMatrixValue28, y, 1.0F + 0.15F * (1.0F - totemsMatrixValue29));

            try {
               this.handleMatrixStack(fontRenderer, this.totemCounterText, matrixStackTotal, matrixStackValue, this.color);
            } finally {
               this.matrixStack.pop();
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         } else {
            this.handleMatrixStack(fontRenderer, this.totemCounterText, matrixStackTotal, matrixStackValue, this.color);
         }

         float totemsMatrixValue30 = matrixStackTotal + totemsMatrixValue27 + 6.0F;
         float panelOverlayLimit = Math.min((float)this.matrixWidthAnimation.getAmount(), NativeHud.getMax((peekPositionValue + panelOverlayBound - 6.0F - totemsMatrixValue30 + 0.5F) / this.totemsMatrixAmount));
         if (panelOverlayLimit > 0.004F) {
            float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

            try {
               this.handleMatrixStack(fontRenderer, "TOTEMS", totemsMatrixValue30, NativeHud.getAmount4(nativeHudValue + this.totemsAmount), 0.7F, GRAY);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
            }
         }
      } finally {
         if (totemsMatrixFlag) {
            this.matrixStack.pop();
         }

         if (hudHotbar != null) {
            this.matrixStack.pop();
         }
      }
   }

   private void renderMatricesTotems(DrawContext context, int matricesTotemsValue, float matricesTotemsDivisor, int scaledHeight, int pilotElytraValue) {
      VertexTexturePeek regular = VertexTexturePeek.getRegular();
      VertexTexturePeek vertexTexturePeek = regular.isCorzclientFlag() ? regular : VertexTexturePeek.getSuisse();
      if (vertexTexturePeek.isCorzclientFlag()) {
         String valueOf = String.valueOf(pilotElytraValue);
         float maxCorzclientAmounts = Math.max(vertexTexturePeek.getCorzclientAmounts(valueOf, 12.0F), vertexTexturePeek.getCorzclientAmounts("00", 12.0F));
         float bottomFactor = 25.0F + maxCorzclientAmounts + 8.0F;
         float matricesTotemsValue5 = (scaledHeight - 22.0F) / matricesTotemsDivisor;
         float matricesTotemsValue6 = (matricesTotemsValue - bottomFactor) / 2.0F;
         float matricesTotemsValue7 = matricesTotemsValue5 - 16.0F - 20.0F;
         float glfwLimit = ShowHUD.getAmount4("totems");
         float amountFloat = ShowHUD.getAmountFloat("totems");
         float hudValue = matricesTotemsValue6 + Math.round(glfwLimit);
         float matricesTotemsValue11 = matricesTotemsValue7 + Math.round(amountFloat);
         ShowHUD.HudScale hudHotbar = ShowHUD.getHudHotbar("totems", matricesTotemsValue6 + glfwLimit, matricesTotemsValue7 + amountFloat, bottomFactor, 20.0F);
         if (hudHotbar != null) {
            hudHotbar.handleAmountTranslate(this.matrixStack);
         }

         try {
            ShowHUD.getAmount(context, hudValue, matricesTotemsValue11, bottomFactor, 20.0F, null);
            float matricesTotemsValue12 = hudValue + 8.0F;
            float matricesTotemsValue13 = matricesTotemsValue11 + 4.0F;
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(matricesTotemsDivisor, matricesTotemsDivisor);
            if (hudHotbar != null) {
               hudHotbar.handleAmountTranslate(context.getMatrices());
            }

            context.getMatrices().translate(matricesTotemsValue12, matricesTotemsValue13);
            context.getMatrices().scale(0.75F, 0.75F);
            DrawMatricesUtils.renderMatricesMatrix(context, ITEM_STACK, 0, 0, 16);
            context.getMatrices().popMatrix();
            int vertexTextureValue = pilotElytraValue == 0 ? -42406 : -1;
            vertexTexturePeek.handlePeekEnd(valueOf, matricesTotemsValue12 + 12.0F + 5.0F, matricesTotemsValue11 + (20.0F - vertexTexturePeek.getAmountForFloat(12.0F)) / 2.0F, vertexTextureValue, 12.0F);
         } finally {
            if (hudHotbar != null) {
               this.matrixStack.pop();
            }
         }
      }
   }

   public TotemCounter() {
      super("Totem Counter", IconsPngType.VISUAL);
      this.showLabel = new BooleanSetting("Show Label", "Small Totems label next to the count", true);
      this.booleanBase = new BooleanSetting("Warn Colors", "Number turns red at zero totems and amber at one", true);
      this.matrixStack = new MatrixStack();
      this.matrixPopAnimation = new QuintScissor(QuintBounceType.BACK_OUT, 220, 1.0);
      this.quintScissor = new QuintScissor(QuintBounceType.BACK_OUT, 240, 1.0);
      this.totemCounterQuintScissor = new QuintScissor(QuintBounceType.QUART_OUT, 220);
      this.matrixWidthAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 200, 1.0);
      this.matrixSlideAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 260);
      this.totemsMatrixQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 260);
      this.scaledClassicQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 160, 1.0);
      this.classicScaleAnimation = new QuintScissor(QuintBounceType.BACK_OUT, 240, 1.0);
      this.classicFlag = true;
      this.lastTotemCount = -1;
      this.totemCounterText = "0";
      this.totemsMatrixText = "0";
      this.totemsAmounts = new float[10];
      this.addSettings(new ConfigSetting[]{this.enumHelper, this.showLabel, this.booleanBase});
   }

   @Override
   public void onRender2D(TickProgress tickProgress) {
      this.renderScaledClassic(tickProgress.drawContext);
   }

   private static float getAmount(float totemCounterLimit) {
      totemCounterLimit = totemCounterLimit < 0.0F ? 0.0F : (totemCounterLimit > 1.0F ? 1.0F : totemCounterLimit);
      float totemCounterFactor = 1.0F - totemCounterLimit;
      return 1.0F - totemCounterFactor * totemCounterFactor * totemCounterFactor;
   }

   private void updateTimeText() {
      if (mc.player != null) {
         int value = this.getUndyingStack();
         if (value != this.lastTotemCount) {
            boolean totemCounterFlag = this.lastTotemCount < 0;
            this.totemsMatrixText = this.totemCounterText;
            this.totemCounterText = value < 10 ? totemsRendererTexts[value] : String.valueOf(value);
            if (!totemCounterFlag) {
               this.matrixPopAnimation.handleAmountFlag(0.0);
               this.matrixPopAnimation.handleAmountTime(1.0);
               if (value < this.lastTotemCount) {
                  this.quintScissor.handleAmountFlag(0.0);
                  this.quintScissor.handleAmountTime(1.0);
               }
            }

            this.lastTotemCount = value;
         }
      }
   }

   private int getUndyingStack() {
      int undyingStack = 0;
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i < 36; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
            undyingStack += stack.getCount();
         }
      }

      ItemStack stack2 = inventory.getStack(40);
      if (stack2.isOf(Items.TOTEM_OF_UNDYING)) {
         undyingStack += stack2.getCount();
      }

      return undyingStack;
   }

   @Override
   protected void onEnable() {
      this.classicFlag = true;
      this.lastTotemCount = -1;
      this.updateTimeText();
   }
}
