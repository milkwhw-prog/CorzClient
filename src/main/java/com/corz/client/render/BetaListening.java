package com.corz.client.render;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.ConfigListSetting;
import com.corz.client.config.ConfigBrowserPanel;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.KeybindSetting;
import com.corz.client.config.RangeSetting;
import com.corz.client.config.RadioSetting;
import com.corz.client.config.StringBase;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.FlagHandle;
import com.corz.client.gui.IntegerFlag;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.MaxRenderer;
import com.corz.client.gui.MinmaxHandle;
import com.corz.client.gui.PressOpen;
import com.corz.client.gui.RenderBeta;
import com.corz.client.gui.RenderPillUtils;
import com.corz.client.gui.RendererPress;
import com.corz.client.gui.RotateQuaternionfApi;
import com.corz.client.gui.ScissorDisable;
import com.corz.client.gui.ThemeWhole;
import com.corz.client.gui.TwoDidUtils;
import com.corz.client.gui.VertexGuiUtils;
import com.corz.client.inventory.ItemList;
import com.corz.client.inventory.SearchEdit;
import com.corz.client.render.MatrixIdentifierUtils.Spring;
import com.corz.client.render.MatrixStackUtilsAmount.Swap;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import com.corz.client.utils.RadioCom;
import com.corz.client.categories.settings.ClientSounds;
import com.corz.client.categories.configs.Configs;
import com.corz.client.categories.socials.Friends;
import com.corz.client.categories.settings.License;
import com.corz.client.categories.settings.Panic;
import com.corz.client.categories.settings.SelfDestruct;
import com.corz.client.categories.theme.ThemeCustomizer;
import ai.auth.Module;
import com.corz.client.utils.AutoYourUtils;
import com.corz.client.utils.ConstAmountUtils;
import com.corz.client.utils.FriendListBase;
import com.corz.client.utils.GlfwListeningApi;
import com.corz.client.utils.PageRaltUtils;
import com.corz.client.utils.PaletteBase;
import com.corz.client.utils.PeekBetaUtils;
import com.corz.client.utils.PngIcons;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;
import com.corz.client.utils.ToggleSanitizedUtils;

public class BetaListening implements GlfwListeningApi {
   private double resetProgress;
   private boolean keybindBindActive;
   private double resetAmount;
   private final QuintScissor fadeAnimation;
   private float clampedMaxProgress;
   private double keybindAmount;
   private long nanoTimeKeybind;
   private String keybindBindText;
   private boolean listeningBindFlag;
   private boolean keybindListeningActive;
   private double keybindBindAmount;
   private static final Identifier blatantCautionIdentifier = Identifier.of("corzclient", "textures/gui/icons/info.png");
   private boolean listeningActive;
   private double keybindListeningAmount;
   private int rowHeightPx;
   private boolean betaListeningActive;
   private long nanoTimeNano;
   private static final int UNIT_VALUE = 1;
   private static final Identifier anywayEnableIdentifier = Identifier.of("corzclient", "textures/gui/icons/warning.png");
   private float blatantCautionAmount;
   private double betaListeningAmount;
   private int betaListeningValue;
   private boolean listeningPending;
   private double bindPillProgress;
   private static final Identifier betaListeningIdentifier = Identifier.of("corzclient", "textures/gui/icons/star.png");
   private double listeningSlideOffset;
   private float cachedMaxWidth;
   private boolean anywayEnableActive = false;
   private boolean blatantCautionFlag;
   private final QuintScissor keybindQuintScissor;
   private String listeningText;
   private String betaListeningText;
   private double listeningAmount;
   private int bindKeybindValue;
   private double maxBetaListeningBound;
   private static final String BETA = "BETA";
   private double listeningFadeProgress;
   private int keybindBindValue;
   private int currentTextColor;
   private final QuintScissor keybindBindQuintScissor;
   private boolean anywayEnableFlag = false;
   private Swap timeMax;
   private boolean anywayActive;
   private static final Identifier LOCK_ICON = Identifier.of("corzclient", "textures/gui/icons/lock.png");
   private static final Identifier identifier = Identifier.of("corzclient", "textures/gui/icons/bind.png");
   private final Module next;
   private boolean wasHovered;
   private final Spring keybindSpring;
   private String blatantCautionText;
   private int blatantCautionValue;
   private String displayText;
   private long timeMillis;
   private long lastTimeMillis;
   private double listeningFlag;
   private String cachedText;
   private static final Identifier CHECK_ICON = Identifier.of("corzclient", "textures/gui/icons/check.png");
   private static final int ROW_HEIGHT = 15;
   private final List<String> betaListeningStringList;
   private int lastCautionLevel;
   private final Map<String, Boolean> betaListeningBooleanMap;
   private double bindAmount;
   private int cautionColor;
   private final Map<String, Double> betaListeningDoubleMap;
   private final QuintScissor scaledWindowQuintScissor;
   private static final String AGREE_MODULE = "I agree to use this module.";
   private int targetTextColor;
   private static int texturesIconsValue = Integer.MIN_VALUE;
   private boolean betaBadgeVisible;
   private ListeningGlfw listeningGlfw;
   private final QuintScissor anywayEnableQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 160, 0.0);
   private boolean keybindActive;
   private final List<BetaListening.SettingComponent> keybindScissorSettingCompone;
   private int listeningKeybindValue;
   private final Spring anywayEnableSpring = new Spring(0.0);
   private static int texturesIconsValue4TexturesI = Integer.MAX_VALUE;

   private void renderListeningText(DrawContext context, int listeningValue, int amountKeybindListeningAmount, int listeningValue3) {
      if (this.listeningText != null) {
         VertexTextureFont fontRenderer = CorzClient.fontRenderer();
         int listeningValue4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown();
         int abcdefghijklmnopqrstuvwxyzBrown = amountKeybindListeningAmount + 22 - listeningValue4 - 3;
         Color color = new Color(138, 138, 150, 255);
         int listeningValue2 = listeningValue;

         for (int i = 0; i < this.listeningText.length(); i++) {
            String valueOf = String.valueOf(this.listeningText.charAt(i));
            fontRenderer.handle(valueOf, listeningValue2, abcdefghijklmnopqrstuvwxyzBrown, color);
            listeningValue2 += (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(valueOf) + 1;
         }

         int matrixPeekValue = listeningValue2 + 7;
         int f2 = abcdefghijklmnopqrstuvwxyzBrown + listeningValue4 / 2;
         if (matrixPeekValue < listeningValue + listeningValue3) {
            MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, f2, listeningValue + listeningValue3 - matrixPeekValue, MatrixPeekUtilsRender.getAmount(), new Color(255, 255, 255, 26).getRGB());
         }
      }
   }

   public Module getNext() {
      return this.next;
   }

   private int getRendererFont() {
      return (int)CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown() + 1;
   }

   private void render(DrawContext context, int betaListeningValue, int pillLimit, int betaListeningLimit) {
      this.renderKeybindAmount(context, betaListeningValue, 36, 8, pillLimit, betaListeningLimit);
   }

   private void update() {
      this.updateTimeFlag();
   }

   private void updateKeybindListening() {
      if (this.betaListeningActive) {
         int keybindListeningTotal = this.getAmount() + 20;
         int keybindListeningValue = this.bindKeybindValue + ConstAmountUtils.getValue();
         int valueForInt = ConstAmountUtils.getValueForInt(this.listeningKeybindValue);

         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            settingComponent.handle(keybindListeningValue, keybindListeningTotal, valueForInt);
            keybindListeningTotal += settingComponent.getBetaText();
         }
      } else if (this.listeningActive) {
         int value3 = this.getValue3();
         int keybindListeningTotal2 = this.getAmountKeybindListeningAmount() + (this.listeningText != null ? 22 : 0) + (this.isFlag7() ? 0 : 18 + (this.isFlag8() ? 20 : 4));

         for (BetaListening.SettingComponent settingComponent2 : this.keybindScissorSettingCompone) {
            settingComponent2.handle(this.bindKeybindValue + value3, keybindListeningTotal2, this.listeningKeybindValue - value3 * 2);
            keybindListeningTotal2 += settingComponent2.getBetaText();
         }
      } else {
         int amountKeybindListeningAmount = this.getAmountKeybindListeningAmount();
         int editClusterValue = amountKeybindListeningAmount + 36 + this.getValueInt();
         int bindKeybindValueOffset = this.bindKeybindValue + 8;
         int width = this.listeningKeybindValue - 16;

         for (BetaListening.SettingComponent settingComponent3 : this.keybindScissorSettingCompone) {
            settingComponent3.handle(bindKeybindValueOffset, editClusterValue, width);
            editClusterValue += settingComponent3.getBetaText();
         }
      }
   }

   private void renderAmountReset(DrawContext context, int resetLimit, int mouseY, int resetValue) {
      double matrixIdentifierFactor = MatrixClearUtils.getAmount();
      int resetRenderer = this.getResetRenderer();
      int rendererFont = this.getRendererFont();
      int keybindListening = this.getKeybindListening();
      boolean resetFlag = resetLimit >= keybindListening && resetLimit <= keybindListening + resetRenderer && mouseY >= resetValue && mouseY <= resetValue + rendererFont;
      this.resetAmount = MatrixIdentifierUtils.getAmount(this.resetAmount, resetFlag ? 1.0 : 0.0, resetFlag ? 16.0 : 12.0, matrixIdentifierFactor);
      this.resetProgress = MatrixIdentifierUtils.getAmount(this.resetProgress, 0.0, 15.0, matrixIdentifierFactor);
      if (this.resetAmount < 5.0E-4) {
         this.resetAmount = 0.0;
      }

      if (this.resetProgress < 5.0E-4) {
         this.resetProgress = 0.0;
      }

      double resetAmount2 = this.resetAmount;
      double betaListeningFactor = this.resetProgress;
      int resetValue4 = this.getValue(-15461350, -14474452, resetAmount2);
      int matrixStackValue = this.getValue(resetValue4, -15395557, betaListeningFactor);
      int round6 = (int)Math.round(36.0 * Math.max(resetAmount2, betaListeningFactor * 0.6));
      int resetValue7 = this.getValue(round6 << 24 | 16777215, CardMatrixUtils.getReleaseRight(0.22), resetAmount2 * 0.5);
      int brownValue = resetValue + (int)Math.round(betaListeningFactor);
      MatrixPeekUtilsRender.render(context, keybindListening, brownValue, resetRenderer, rendererFont, 2, matrixStackValue, resetValue7);
      String resetText = "Reset";
      float resetValue9 = CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont(resetText);
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      matrixStack.push();
      matrixStack.scale(0.95F, 0.95F, 1.0F);
      float resetValue10 = keybindListening + (resetRenderer - resetValue9 * 0.95F) / 2.0F;
      float resetValue11 = brownValue - 2;
      int resetValue12 = this.getValue(-7630437, -920587, resetAmount2);
      CorzClient.fontRenderer().handleForStringFloat(resetText, resetValue10 / 0.95F, resetValue11 / 0.95F, new Color(resetValue12, true));
      matrixStack.pop();
   }

   private void renderBindKeybind(DrawContext context, int bindKeybindValue) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      float x = 0.95F;
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      matrixStack.push();
      matrixStack.scale(x, x, 1.0F);
      fontRenderer.handleForStringFloat("Menu Bind", (int)((this.bindKeybindValue + 6) / x), (int)(bindKeybindValue / x), new Color(-7630437));
      matrixStack.pop();
      this.renderListeningRenderer(context, this.bindKeybindValue + this.listeningKeybindValue - 6, bindKeybindValue - 3);
   }

   private int[] getAgreeScaled() {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int agreeScaledFactor = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 3;
      short agreeScaledValue = 272;
      int rendererfontSize = this.getRendererFont(this.formatYouWant(), agreeScaledValue).size();
      int size2 = this.getRendererFont(this.formatTextNext(), agreeScaledValue).size();
      int max2 = Math.max(15, (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown());
      int agreeScaledValue3 = 41 + rendererfontSize * agreeScaledFactor + 6 + size2 * agreeScaledFactor + 10 + max2 + 12 + 22 + 14;
      Window window = MinecraftClient.getInstance().getWindow();
      int agreeScaledValue4 = (window.getScaledWidth() - 300) / 2;
      int agreeScaledValue5 = (window.getScaledHeight() - agreeScaledValue3) / 2;
      int agreeScaledValue6 = agreeScaledValue5 + 14 + 18 + 9 + rendererfontSize * agreeScaledFactor + 6 + size2 * agreeScaledFactor + 10;
      int agreeScaledValue7 = 22 + (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("I agree to use this module.");
      int agreeScaledValue8 = agreeScaledValue4 + (300 - agreeScaledValue7) / 2;
      int agreeScaledValue9 = agreeScaledValue5 + agreeScaledValue3 - 14 - 22;
      short agreeScaledValue10 = 132;
      int agreeScaledValue11 = agreeScaledValue4 + 14;
      int agreeScaledValue12 = agreeScaledValue11 + agreeScaledValue10 + 8;
      return new int[]{agreeScaledValue4, agreeScaledValue5, 300, agreeScaledValue3, agreeScaledValue8, agreeScaledValue6, agreeScaledValue7, max2, agreeScaledValue11, agreeScaledValue9, agreeScaledValue10, 22, agreeScaledValue12, agreeScaledValue9, agreeScaledValue10, 22};
   }

   public int getListeningKeybindValue() {
      return this.listeningKeybindValue;
   }

   private boolean isFlag() {
      return !this.listeningActive && this.next instanceof RotateQuaternionfApi;
   }

   private boolean canKeybindBind(double keybindBindLimit, double mouseY) {
      if (this.betaListeningActive) {
         return false;
      } else {
         int keybindBindValue = this.getAmountKeybindListeningAmount();
         return keybindBindLimit >= this.bindKeybindValue && keybindBindLimit <= this.bindKeybindValue + this.listeningKeybindValue && mouseY >= keybindBindValue && mouseY <= keybindBindValue + 36;
      }
   }

   public int getBindKeybindValue() {
      return this.bindKeybindValue;
   }

   private int getAmount() {
      return this.getAmountKeybindListeningAmount() + 58 - (int)Math.floor(getAmount(this.listeningSlideOffset));
   }

   private void renderScaledWindow(DrawContext context) {
      double scaledWindowValue = Math.max(0.0, Math.min(1.0, this.scaledWindowQuintScissor.getAmount()));
      if (!(scaledWindowValue <= 0.001)) {
         MinecraftClient instance = MinecraftClient.getInstance();
         int scaledWidth = instance.getWindow().getScaledWidth();
         int scaledHeight = instance.getWindow().getScaledHeight();
         int scaledX = (int)instance.mouse.getScaledX(instance.getWindow());
         int scaledY = (int)instance.mouse.getScaledY(instance.getWindow());
         VertexTextureFont fontRenderer = CorzClient.fontRenderer();
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         VertexGuiUtils.handle(-scaledWidth, -scaledHeight, scaledWidth * 2, scaledHeight * 2, ((int)(178.0 * scaledWindowValue) & 0xFF) << 24);
         int[] scaledRenderer = this.getScaledRenderer();
         int matrixPeekValue = scaledRenderer[0];
         int matrixStackValue = scaledRenderer[1];
         int scaledWindowValue4 = scaledRenderer[2];
         int materialFactor = scaledRenderer[3];
         byte corzLicenseLimit = 10;
         int scaledWindowValue7 = CardMatrixUtils.getReleaseRight(1.0) & 16777215;
         float panelOverlay = CorzRoundedUtils.getPanelOverlay((float)scaledWindowValue);

         try {
            MatrixPeekUtilsRender.renderMatrixStack(
               context,
               matrixPeekValue - 2,
               matrixStackValue - 2,
               scaledWindowValue4 + 4,
               materialFactor + 4,
               corzLicenseLimit + 1,
               new Color(scaledWindowValue7 >> 16 & 0xFF, scaledWindowValue7 >> 8 & 0xFF, scaledWindowValue7 & 0xFF, 90).getRGB(),
               9.0F,
               0.5F
            );
            MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, matrixStackValue, scaledWindowValue4, materialFactor, corzLicenseLimit, -65990120, scaledWindowValue7 | 1610612736);
            int scaledWindowValue8 = matrixPeekValue + scaledWindowValue4 / 2;
            float roundOffset = matrixStackValue + 14;
            byte peekPositionFactor = 18;
            MatrixPeekUtilsIdentifier.handle(
               scaledWindowValue8 - peekPositionFactor / 2.0F, roundOffset, peekPositionFactor, peekPositionFactor, identifier, new Color(CardMatrixUtils.getReleaseRight(1.0), true), matrixStack
            );
            float scaledWindowTotal = roundOffset + (peekPositionFactor + 9);
            int scaledWindowValue11 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 3;
            int rendererFontValue = scaledWindowValue4 - 28;

            for (String scaledWindowText : this.getRendererFont(this.formatKeybind(), rendererFontValue)) {
               fontRenderer.handleForStringFloat(scaledWindowText, matrixPeekValue + (scaledWindowValue4 - fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(scaledWindowText)) / 2.0F, scaledWindowTotal, new Color(-920587));
               scaledWindowTotal += scaledWindowValue11;
            }

            float f13 = scaledWindowTotal + 6.0F;

            for (String getRendererFontItem : this.getRendererFont(this.formatTextBetaListeningValue(), rendererFontValue)) {
               fontRenderer.handleForStringFloat(getRendererFontItem, matrixPeekValue + (scaledWindowValue4 - fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(getRendererFontItem)) / 2.0F, f13, new Color(12765135));
               f13 += scaledWindowValue11;
            }

            this.renderRendererFont(context, scaledRenderer[4], scaledRenderer[5], scaledRenderer[6], scaledRenderer[7], "Rebind", CardMatrixUtils.getReleaseRight(1.0), scaledX, scaledY, true);
            this.renderRendererFont(context, scaledRenderer[8], scaledRenderer[9], scaledRenderer[10], scaledRenderer[11], "Unbind", -6642766, scaledX, scaledY, true);
            this.renderRendererFont(context, scaledRenderer[12], scaledRenderer[13], scaledRenderer[14], scaledRenderer[15], "Use Both", -6642766, scaledX, scaledY, true);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private boolean canKeybindBindForDouble(double keybindBindLimit, double betaListeningLimit) {
      int keybindBindValue = this.getAmount();
      return this.canFlag(betaListeningLimit)
         && keybindBindLimit >= this.bindKeybindValue + ConstAmountUtils.getValue()
         && keybindBindLimit <= this.bindKeybindValue + ConstAmountUtils.getValue(this.listeningKeybindValue)
         && betaListeningLimit >= keybindBindValue
         && betaListeningLimit < keybindBindValue + 20;
   }

   public void handleTimeFlag(long nanoTime, boolean flag) {
      this.nanoTimeNano = nanoTime;
      this.betaBadgeVisible = flag;
   }

   public void handleFlag(double betaListeningValue, double heightAmount, int button) {
      if (this.keybindBindActive) {
         if (this.betaListeningActive && this.keybindListeningActive) {
            this.handleMaxAmountForDouble(heightAmount);
         } else {
            if (this.next instanceof RotateQuaternionfApi rotateQuaternionfApi) {
               rotateQuaternionfApi.handleQuaternionfRotate(betaListeningValue, heightAmount);
            }

            for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
               settingComponent.handleForDoubleInt(betaListeningValue, heightAmount, button);
            }

            if (!this.isFlagTime()) {
               if (this.listeningActive || this.listeningPending) {
                  ;
               }
            }
         }
      }
   }

   public void handleKeybindListening(int bindKeybindValue, int amount, int listeningKeybindValue, int value) {
      if (bindKeybindValue != this.bindKeybindValue || amount != this.keybindListeningAmount || listeningKeybindValue != this.listeningKeybindValue || value != this.rowHeightPx) {
         this.bindKeybindValue = bindKeybindValue;
         this.keybindListeningAmount = amount;
         this.listeningKeybindValue = listeningKeybindValue;
         this.rowHeightPx = value;
         this.listeningFlag = value;
         this.updateKeybindListening();
      }
   }

   private void updateListeningBind() {
      this.updateTimeFlag();
      this.listeningBindFlag = true;
      this.timeMillis = System.currentTimeMillis();
   }

   private String formatText() {
      return formatGlfw(this.next.getBindKey());
   }

   private void handleAnywayEnable(double anywayEnableLimit, double mouseY) {
      int[] agreeScaled = this.getAgreeScaled();
      if (anywayEnableLimit >= agreeScaled[4] && anywayEnableLimit <= agreeScaled[4] + agreeScaled[6] && mouseY >= agreeScaled[5] && mouseY <= agreeScaled[5] + agreeScaled[7]) {
         this.anywayEnableFlag = !this.anywayEnableFlag;
      } else if (anywayEnableLimit >= agreeScaled[12] && anywayEnableLimit <= agreeScaled[12] + agreeScaled[14] && mouseY >= agreeScaled[13] && mouseY <= agreeScaled[13] + agreeScaled[15]) {
         if (this.anywayEnableFlag) {
            this.updateTimeFlagAnywayEnableActive();
         }
      } else if (anywayEnableLimit >= agreeScaled[8] && anywayEnableLimit <= agreeScaled[8] + agreeScaled[10] && mouseY >= agreeScaled[9] && mouseY <= agreeScaled[9] + agreeScaled[11]) {
         this.updateTimeFlag4();
      } else {
         if (anywayEnableLimit < agreeScaled[0] || anywayEnableLimit > agreeScaled[0] + agreeScaled[2] || mouseY < agreeScaled[1] || mouseY > agreeScaled[1] + agreeScaled[3]) {
            this.updateTimeFlag4();
         }
      }
   }

   public double getKeybindListeningAmount() {
      return this.keybindListeningAmount;
   }

   public boolean isFlagTime() {
      if (!this.keybindBindActive || !this.anywayEnableActive && !(this.anywayEnableQuintScissor.getAmount() > 0.01) && !this.anywayActive && !(this.scaledWindowQuintScissor.getAmount() > 0.01)) {
         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            if (settingComponent.isFlag()) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean isBetaBadgeVisible() {
      return this.betaBadgeVisible;
   }

   private double getAmountNext() {
      return this.next.getSettings().isEmpty() ? 26.0 : ConstAmountUtils.getAmount((double)this.getValue());
   }

   private void renderRendererFont(DrawContext context, int rendererFontValue, int brownValue, int materialFactor, int boxHeight, String rendererFontText, int cardMatrixValue, int rendererFontLimit, int scaledY, boolean rendererFontFlag) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      boolean rendererFontFlag2 = cardMatrixValue == -7465964;
      boolean rendererFontFlag3 = rendererFontFlag && rendererFontLimit >= rendererFontValue && rendererFontLimit <= rendererFontValue + materialFactor && scaledY >= brownValue && scaledY <= brownValue + boxHeight;
      int rendererFontValue6;
      int matrixStackValue;
      int rendererFontValue8;
      if (rendererFontFlag2) {
         if (rendererFontFlag) {
            rendererFontValue6 = rendererFontFlag3 ? -5760486 : -7465964;
            matrixStackValue = CardMatrixUtils.getValue(rendererFontValue6, -1, 0.12);
            rendererFontValue8 = -594708;
         } else {
            rendererFontValue6 = -14411236;
            matrixStackValue = 352321535;
            rendererFontValue8 = -8753552;
         }
      } else {
         matrixStackValue = CardMatrixUtils.getValue(352321535, cardMatrixValue & 16777215 | 0xFF000000, 0.22 + (rendererFontFlag3 ? 0.78 : 0.0));
         rendererFontValue6 = CardMatrixUtils.getValue(-15329252, cardMatrixValue, 0.05 + (rendererFontFlag3 ? 0.15 : 0.0));
         rendererFontValue8 = CardMatrixUtils.getValue(cardMatrixValue, -1, rendererFontFlag3 ? 0.35 : 0.0);
      }

      MatrixPeekUtilsRender.render(context, rendererFontValue, brownValue, materialFactor, boxHeight, 5, rendererFontValue6, matrixStackValue);
      fontRenderer.handleForStringFloat(
         rendererFontText,
         rendererFontValue + (materialFactor - fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(rendererFontText)) / 2.0F,
         fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, boxHeight),
         new Color(rendererFontValue8, true)
      );
   }

   public static void updateTexturesIcons() {
      texturesIconsValue = Integer.MIN_VALUE;
      texturesIconsValue4TexturesI = Integer.MAX_VALUE;
   }

   public void updateNext() {
      if (AutoYourUtils.isContains(this.next) && !AutoYourUtils.isContainsPVP_CRYSTAL_SET(this.next)) {
         this.updateAnywayEnable();
      }
   }

   public int getListeningFlag() {
      if (this.betaListeningActive) {
         return this.rowHeightPx;
      } else if (this.listeningActive) {
         int listeningFlag = this.listeningText != null ? 22 : 0;
         if (!this.isFlag7()) {
            listeningFlag += 18 + (this.isFlag8() ? 20 : 4);
         }

         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            listeningFlag += settingComponent.getBetaText();
         }

         return listeningFlag;
      } else if (this.next.getSettings().isEmpty()) {
         return 36;
      } else {
         int listeningTotal = 0;

         for (BetaListening.SettingComponent settingComponent2 : this.keybindScissorSettingCompone) {
            listeningTotal += settingComponent2.getBetaText();
         }

         return 36 + this.getValueInt() + listeningTotal + 6 + 11 + 6;
      }
   }

   private int getValue() {
      int betaListeningTotal = 0;

      for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
         betaListeningTotal += settingComponent.getBetaText();
      }

      return betaListeningTotal;
   }

   private void handle(int value) {
      if (value >= 0) {
         this.betaListeningStringList.clear();

         for (Module getItem : PngIcons.getInstance().getItems()) {
            if (getItem != this.next && getItem.getBindKey() == value) {
               this.betaListeningStringList.add(getItem.getCategory());
            }

            for (ConfigSetting autoEspBase : getItem.getSettings()) {
               if (autoEspBase instanceof KeybindSetting keybindBase && keybindBase.isGlobalHotkey() && ToggleSanitizedUtils.canFlag(value, keybindBase.getKeyCode())) {
                  this.betaListeningStringList.add(getItem.getCategory() + ": " + keybindBase.getSettingName());
               }
            }
         }

         if (!this.betaListeningStringList.isEmpty()) {
            this.betaListeningValue = value;
            this.updateTimeFlagAnywayActive();
         }
      }
   }

   private boolean canListeningRenderer(double listeningRendererLimit, double mouseY) {
      if (!this.isFlag8()) {
         return false;
      } else {
         int listeningRendererValue = this.getAmountKeybindListeningAmount();
         int listeningRendererValue2 = listeningRendererValue + (this.listeningText != null ? 22 : 0) + 18;
         String listeningRendererText = this.listeningBindFlag ? "Listening..." : this.formatText();
         float listeningRendererValue3 = CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont(listeningRendererText) + 10.0F;
         float listeningRendererValue4 = CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown() + 6.0F;
         float listeningRendererValue5 = this.bindKeybindValue + this.listeningKeybindValue - 6 - listeningRendererValue3;
         float listeningRendererValue6 = listeningRendererValue2 - 3;
         return listeningRendererLimit >= listeningRendererValue5 && listeningRendererLimit <= listeningRendererValue5 + listeningRendererValue3 && mouseY >= listeningRendererValue6 && mouseY <= listeningRendererValue6 + listeningRendererValue4;
      }
   }

   public double getMaxBetaListeningBound() {
      return this.maxBetaListeningBound;
   }

   public void handleKeybindListening(int keybindListeningValue, double keybindListeningValue2, int listeningKeybindValue) {
      this.listeningKeybindValue = listeningKeybindValue;
      this.handleKeybindBind(keybindListeningValue, keybindListeningValue2);
   }

   private int getValueInt() {
      return this.isFlag() ? 126 : 0;
   }

   private String formatTextBetaListeningValue() {
      String formatGlfw2 = formatGlfw(this.betaListeningValue);
      int itemsSize = this.betaListeningStringList.size();
      String betaListeningText;
      if (itemsSize == 1) {
         betaListeningText = "\"" + this.betaListeningStringList.get(0) + "\"";
      } else if (itemsSize == 2) {
         betaListeningText = "\"" + this.betaListeningStringList.get(0) + "\" and \"" + this.betaListeningStringList.get(1) + "\"";
      } else {
         betaListeningText = "\"" + this.betaListeningStringList.get(0) + "\" and " + (itemsSize - 1) + " other modules";
      }

      return formatGlfw2 + " is already bound to " + betaListeningText + ".";
   }

   private boolean isNext() {
      return this.next instanceof Configs || this.next instanceof Friends;
   }

   private int getKeybindListening() {
      return this.bindKeybindValue + this.listeningKeybindValue - (this.betaListeningActive ? 12 : 8) - this.getResetRenderer();
   }

   private void renderKeybindBind(DrawContext context, int keybindBindValue, int betaListeningLimit) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int amountKeybindListeningAmount = this.getAmountKeybindListeningAmount();
      byte keybindBindValue4 = 8;
      this.keybindBindAmount = MatrixIdentifierUtils.getAmount(this.keybindBindAmount, 1.0, 12.0, MatrixClearUtils.getAmount());
      if (this.keybindBindAmount > 0.999) {
         this.keybindBindAmount = 1.0;
      }

      this.currentTextColor = CardMatrixUtils.getValue(this.keybindBindValue, this.targetTextColor, this.keybindBindAmount);
      MatrixStackUtilsAmount.getPopPush(
         this.betaListeningText, this.bindKeybindValue + keybindBindValue4, MatrixStackUtilsAmount.getAmount(amountKeybindListeningAmount + 7, 10.0F, 0.8F), 0.8F, 1.0F, -11117467
      );
      String text = this.next.getCategory();
      float amount = ConstAmountUtils.getValue(this.listeningKeybindValue, this.getFontRenderer());
      if (!text.equals(this.cachedText) || amount != this.cachedMaxWidth) {
         this.cachedText = text;
         this.cachedMaxWidth = amount;
         this.keybindBindText = fontRenderer.formatText(text, amount / 1.22F);
      }

      int matrixStackValue = AutoYourUtils.isContainsForIlIllIiIll(this.next) ? CardMatrixUtils.getReleaseRight() : -920587;
      float priceValue = MatrixStackUtilsAmount.getAmount(amountKeybindListeningAmount + 18, 18.0F, 1.22F);
      MatrixStackUtilsAmount.handlePushScale(this.keybindBindText, this.bindKeybindValue + keybindBindValue4, priceValue, 1.22F, matrixStackValue);
      MatrixStackUtilsAmount.handlePushScale(
         this.keybindBindText, this.bindKeybindValue + keybindBindValue4 + MatrixPeekUtilsRender.getAmount(), priceValue, 1.22F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.55F)
      );
      this.renderBlatantCaution(context, this.bindKeybindValue + keybindBindValue4, (int)Math.ceil(MatrixStackUtilsAmount.getAmount(this.keybindBindText, 1.22F)), amountKeybindListeningAmount + 27, keybindBindValue, betaListeningLimit);
      this.renderKeybindAmount(context, amountKeybindListeningAmount + 18, 18, 12, keybindBindValue, betaListeningLimit);
      this.timeMax
         .getAmount(
            this.displayText,
            this.bindKeybindValue + keybindBindValue4,
            MatrixStackUtilsAmount.getAmount(amountKeybindListeningAmount + 37, 10.0F, 0.82F),
            0.82F,
            this.currentTextColor,
            this.bindKeybindValue + this.listeningKeybindValue - keybindBindValue4
         );
      RenderMatrixUtilsRelease.renderForDrawContextInt(context, this.bindKeybindValue + keybindBindValue4, amountKeybindListeningAmount + 54, this.listeningKeybindValue - keybindBindValue4 * 2, 1.0);
      int keybindListeningValue = amountKeybindListeningAmount + 58;
      int keybindBindLimit = ConstAmountUtils.getMaxInt(this.rowHeightPx);
      if (keybindBindLimit > 0) {
         double hashRegistryValue = getAmount(this.listeningSlideOffset);
         int floor9 = (int)Math.floor(hashRegistryValue);
         float deltaVar = (float)(hashRegistryValue - floor9);
         int deltaVar2 = keybindListeningValue - floor9;
         int keybindScissorValue = this.canFlag(betaListeningLimit) ? keybindBindValue : this.bindKeybindValue - 4000;
         int mouseY = (int)Math.floor(betaListeningLimit + deltaVar);
         context.enableScissor(this.bindKeybindValue, keybindListeningValue, this.bindKeybindValue + this.listeningKeybindValue, keybindListeningValue + keybindBindLimit);
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(0.0F, -deltaVar, 0.0F);
         context.getMatrices().pushMatrix();
         context.getMatrices().translate(0.0F, -deltaVar);

         try {
            this.renderKeybindBind(context, deltaVar2);
            this.renderKeybindScissor(
               context, keybindScissorValue, mouseY, this.bindKeybindValue + ConstAmountUtils.getValue(), deltaVar2 + 20, ConstAmountUtils.getValueForInt(this.listeningKeybindValue)
            );
            if (!this.next.getSettings().isEmpty()) {
               this.renderAmountReset(context, keybindScissorValue, mouseY, deltaVar2 + 20 + this.getValue() + 6);
            }
         } finally {
            context.getMatrices().popMatrix();
            matrixStack.pop();
            context.disableScissor();
         }

         this.handleKeybindListening(keybindListeningValue, keybindBindLimit);
      }

      this.renderKeybindAmount(context, keybindBindValue, betaListeningLimit);
   }

   public void setListeningText(String listeningText) {
      this.listeningText = listeningText;
   }

   private int getAmountKeybindListeningAmount() {
      return (int)Math.floor(this.keybindListeningAmount);
   }

   public boolean isListeningPending() {
      return this.listeningPending;
   }

   private int getFontRenderer() {
      int fontRendererValue = (int)CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown();
      int fontRendererValue2 = Math.max(10, Math.min(14, Math.round(fontRendererValue * 0.85F))) & -2;
      int fontRendererValue3 = Math.max(10, Math.round(fontRendererValue2 * 0.88F)) & -2;
      int fontRendererTotal = AutoYourUtils.isContainsKeyForIlIllIiIll(this.next) ? 7 + getBeta(CorzClient.fontManager.getSmallFont()) + 7 : 6;
      if (AutoYourUtils.isContainsForIlIllIiIll(this.next)) {
         fontRendererTotal += fontRendererValue2 + 7;
      }

      int fontRenderer = fontRendererTotal + fontRendererValue2;
      if (AutoYourUtils.canPvpCrystal(this.next)) {
         fontRenderer += 7 + fontRendererValue3;
      }

      if (AutoYourUtils.isContainsKey(this.next)) {
         fontRenderer += 7 + fontRendererValue2;
      }

      return fontRenderer;
   }

   public void handleListeningGlfw(int listeningGlfwValue) {
      if (this.listeningGlfw != null) {
         ListeningGlfw listeningGlfw = this.listeningGlfw;
         this.listeningGlfw = null;
         listeningGlfw.handleForInt(listeningGlfwValue);
      } else {
         boolean listeningGlfwFlag = PageRaltUtils.canFlagForInt(listeningGlfwValue);
         this.next.setBindKey(listeningGlfwFlag ? -1 : listeningGlfwValue);
         this.listeningBindFlag = false;
         this.timeMillis = 0L;
         if (!listeningGlfwFlag) {
            this.handle(this.next.getBindKey());
         }
      }
   }

   public boolean canFlagListening(double listeningValue, double mouseY, int button) {
      if (!this.keybindBindActive) {
         return false;
      } else if (this.anywayEnableActive) {
         this.handleAnywayEnable(listeningValue, mouseY);
         return true;
      } else if (this.anywayActive) {
         this.handle(listeningValue, mouseY);
         return true;
      } else {
         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            if (settingComponent.isFlag()) {
               return settingComponent.canBetaListening(listeningValue, mouseY, button);
            }
         }

         if (this.betaListeningActive) {
            return this.canKeybindBind(listeningValue, mouseY, button);
         } else if (this.listeningActive) {
            if (this.canListeningRenderer(listeningValue, mouseY)) {
               this.listeningBindFlag = true;
               this.timeMillis = System.currentTimeMillis();
               return true;
            } else {
               for (BetaListening.SettingComponent settingComponent2 : this.keybindScissorSettingCompone) {
                  if (settingComponent2.canBetaListening(listeningValue, mouseY, button)) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            if (this.listeningPending && this.next instanceof RotateQuaternionfApi rotateQuaternionfApi) {
               int[] keybindListening = this.getKeybindListeningBindKeybindValue();
               if (rotateQuaternionfApi.canQuaternionfRotate(listeningValue, mouseY, keybindListening[0], keybindListening[1], keybindListening[2], keybindListening[3], button)) {
                  return true;
               }
            }

            if (!this.listeningPending && this.canKeybindBind(listeningValue, mouseY)) {
               this.listeningAmount = 1.0;
            }

            if (this.canBindKeybind(listeningValue, mouseY)) {
               this.listeningBindFlag = true;
               this.timeMillis = System.currentTimeMillis();
               return true;
            } else if (this.canFlag(listeningValue, mouseY)) {
               this.resetProgress = 1.0;
               this.next.syncSettingHandlers();
               return true;
            } else if (this.canKeybindListening(listeningValue, mouseY)) {
               this.updateLockedModule();
               return true;
            } else if (this.canKeybindBind(listeningValue, mouseY)) {
               if (!this.next.getSettings().isEmpty()) {
                  this.listeningPending = !this.listeningPending;
                  if (this.listeningPending) {
                     ClientSounds.updateCardOpen();
                  }
               }

               return true;
            } else {
               if (this.listeningPending) {
                  for (BetaListening.SettingComponent settingComponent3 : this.keybindScissorSettingCompone) {
                     if (settingComponent3.canBetaListening(listeningValue, mouseY, button)) {
                        return true;
                     }
                  }
               }

               return false;
            }
         }
      }
   }

   private void renderKeybindAmount(DrawContext context, int keybindLimit, int betaListeningLimit) {
      int keybindValue = this.getAmountKeybindListeningAmount() + 62;
      int constFactor = ConstAmountUtils.getMax(this.rowHeightPx);
      double amountNext = this.getAmountNext();
      double maxInt = ConstAmountUtils.getMaxInt(this.rowHeightPx);
      int materialFactor = ConstAmountUtils.getValue(amountNext, maxInt, constFactor);
      if (materialFactor <= 0) {
         this.keybindAmount = 0.0;
      } else {
         float betaListeningFactor = ConstAmountUtils.getAmount(amountNext, maxInt, this.listeningSlideOffset, keybindValue, constFactor);
         int matrixPeekValue = this.bindKeybindValue + ConstAmountUtils.getValueInt(this.listeningKeybindValue);
         boolean keybindFlag = this.keybindListeningActive || keybindLimit >= matrixPeekValue - 2 && keybindLimit <= matrixPeekValue + 2 + 3 && betaListeningLimit >= betaListeningFactor && betaListeningLimit <= betaListeningFactor + materialFactor;
         long nanoTime = System.nanoTime();
         if (keybindFlag
            || this.canFlag(betaListeningLimit) && keybindLimit >= this.bindKeybindValue && keybindLimit <= this.bindKeybindValue + this.listeningKeybindValue
            || Math.abs(this.listeningSlideOffset - this.maxBetaListeningBound) > 0.5) {
            this.nanoTimeKeybind = nanoTime;
         }

         boolean keybindFlag2 = this.nanoTimeKeybind != 0L && nanoTime - this.nanoTimeKeybind < 900000000L;
         this.keybindAmount = MatrixIdentifierUtils.getAmount(this.keybindAmount, keybindFlag2 ? 1.0 : 0.0, keybindFlag2 ? 18.0 : 5.0, MatrixClearUtils.getAmount());
         if (!(this.keybindAmount < 0.01)) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay((float)this.keybindAmount);

            try {
               MatrixPeekUtilsRender.renderMatrixPeek(
                  context, matrixPeekValue, (float)getAmount(betaListeningFactor), 2.0F, materialFactor, 1.0F, keybindFlag ? CardMatrixUtils.getReleaseRight(0.75) : 872415231
               );
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   public void updateAmountTime() {
      this.blatantCautionText = null;
      this.handle(MatrixClearUtils.getAmount());
      if (this.betaListeningActive) {
         this.updateAmountMax();
      } else if (this.listeningActive) {
         this.listeningFadeProgress = 1.0;
         this.listeningFlag = this.getListeningFlag();
      } else {
         double betaListeningValue = this.listeningPending ? 1.0 : 0.0;
         if (Math.abs(this.fadeAnimation.getTargetAmount() - betaListeningValue) > 0.01) {
            this.fadeAnimation.handleAmountTime(betaListeningValue);
         }

         this.listeningFadeProgress = this.fadeAnimation.getAmount();
         this.listeningFlag = getAmount(36.0 + (this.getListeningFlag() - 36) * this.listeningFadeProgress);
      }
   }

   private int[] getScaledRenderer() {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int scaledRendererFactor = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 3;
      short scaledRendererValue = 292;
      int rendererfontSize = this.getRendererFont(this.formatKeybind(), scaledRendererValue).size();
      int size2 = this.getRendererFont(this.formatTextBetaListeningValue(), scaledRendererValue).size();
      int scaledRendererValue2 = 41 + rendererfontSize * scaledRendererFactor + 6 + size2 * scaledRendererFactor + 14 + 22 + 14;
      Window window = MinecraftClient.getInstance().getWindow();
      int scaledRendererValue3 = (window.getScaledWidth() - 320) / 2;
      int scaledRendererValue4 = (window.getScaledHeight() - scaledRendererValue2) / 2;
      int scaledRendererValue5 = scaledRendererValue4 + scaledRendererValue2 - 14 - 22;
      byte scaledRendererValue6 = 92;
      int scaledRendererValue7 = scaledRendererValue3 + 14;
      int scaledRendererValue8 = scaledRendererValue7 + scaledRendererValue6 + 8;
      int scaledRendererValue9 = scaledRendererValue8 + scaledRendererValue6 + 8;
      return new int[]{scaledRendererValue3, scaledRendererValue4, 320, scaledRendererValue2, scaledRendererValue7, scaledRendererValue5, scaledRendererValue6, 22, scaledRendererValue8, scaledRendererValue5, scaledRendererValue6, 22, scaledRendererValue9, scaledRendererValue5, scaledRendererValue6, 22};
   }

   private float getListeningBind() {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      boolean listeningBindFlag = this.isFlagNext() && !this.listeningBindFlag;
      float listeningBindValue = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 6.0F;
      float round = Math.round(listeningBindValue * 0.4F);
      float listeningBindValue3 = round * 1.0F;
      return listeningBindFlag ? listeningBindValue3 + 14.0F : fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.listeningBindFlag ? "Listening..." : this.formatText()) + 10.0F;
   }

   private boolean canFlag(double betaListeningLimit, double mouseY) {
      if (!this.listeningPending || this.next.getSettings().isEmpty()) {
         return false;
      } else if (this.betaListeningActive) {
         int betaListeningValue = this.getAmount() + 20 + this.getValue() + 6;
         int keybindListening = this.getKeybindListening();
         return this.canFlag(mouseY) && betaListeningLimit >= keybindListening && betaListeningLimit <= keybindListening + this.getResetRenderer() && mouseY >= betaListeningValue && mouseY <= betaListeningValue + this.getRendererFont();
      } else {
         int amountKeybindListeningAmount = this.getAmountKeybindListeningAmount();
         int betaListeningTotal = 0;

         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            betaListeningTotal += settingComponent.getBetaText();
         }

         int betaListeningValue3 = amountKeybindListeningAmount + 36 + this.getValueInt() + betaListeningTotal + 6;
         int resetRenderer = this.getResetRenderer();
         int rendererFont = this.getRendererFont();
         int keybindListening2 = this.getKeybindListening();
         return betaListeningLimit >= keybindListening2 && betaListeningLimit <= keybindListening2 + resetRenderer && mouseY >= betaListeningValue3 && mouseY <= betaListeningValue3 + rendererFont;
      }
   }

   public void handleMax(float betaListeningBound) {
      this.clampedMaxProgress = Math.max(0.0F, Math.min(1.0F, betaListeningBound));
   }

   public boolean canAnywayFlag(int anywayValue, int scancode, int modifiers) {
      if (!this.keybindBindActive) {
         return false;
      } else if (this.anywayEnableActive) {
         if (anywayValue != 257 && anywayValue != 335) {
            if (anywayValue == 32) {
               this.anywayEnableFlag = !this.anywayEnableFlag;
            } else if (anywayValue == 256 || anywayValue == 259) {
               this.updateTimeFlag4();
            }
         } else if (this.anywayEnableFlag) {
            this.updateTimeFlagAnywayEnableActive();
         }

         return true;
      } else if (!this.anywayActive) {
         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            if (settingComponent.isFlag()) {
               return settingComponent.canFlag(anywayValue, scancode, modifiers);
            }
         }

         if (this.listeningActive) {
            for (BetaListening.SettingComponent settingComponent2 : this.keybindScissorSettingCompone) {
               if (settingComponent2.canFlag(anywayValue, scancode, modifiers)) {
                  return true;
               }
            }

            return false;
         } else if (!this.listeningPending) {
            return false;
         } else {
            for (BetaListening.SettingComponent settingComponent3 : this.keybindScissorSettingCompone) {
               if (settingComponent3.canFlag(anywayValue, scancode, modifiers)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         if (anywayValue == 257 || anywayValue == 335) {
            this.updateListeningBind();
         } else if (anywayValue == 256 || anywayValue == 259) {
            this.update();
         }

         return true;
      }
   }

   private void render(DrawContext context, Identifier id, int betaListeningValue, int betaListeningValue2, int betaListeningFactor, int betaListeningValue3, float betaListeningLimit, float betaListeningFactor2, float betaListeningFactor3, int betaListeningValue4) {
      if (betaListeningLimit > 0.01F) {
         int iconSize = betaListeningFactor / 2 + 2;
         MatrixPeekUtilsRender.renderMatrixStack(
            context, betaListeningValue - iconSize, betaListeningValue2 - iconSize, iconSize * 2, iconSize * 2, iconSize, new Color((int)(betaListeningLimit * 45.0F) << 24 | betaListeningValue4 & 16777215, true).getRGB()
         );
      }

      float configsLikeValue = betaListeningValue - betaListeningFactor2 * betaListeningFactor;
      float glfwLimit = betaListeningValue2 - betaListeningFactor3 * betaListeningFactor;
      MatrixPeekUtilsIdentifier.handle(Math.round(configsLikeValue), Math.round(glfwLimit), betaListeningFactor, betaListeningFactor, id, new Color(betaListeningValue3, true), MatrixPeekUtilsRender.getMatrixStack());
   }

   private static int getBeta(VertexTextureFont vertexTextureFont) {
      float betaTotal = 0.0F;

      for (int i = 0; i < "BETA".length(); i++) {
         betaTotal += vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(String.valueOf("BETA".charAt(i)));
      }

      return Math.round(betaTotal) + 1 * ("BETA".length() - 1);
   }

   private void handle(double betaListeningValue) {
      for (Entry entry : this.betaListeningBooleanMap.entrySet()) {
         double orDefault = this.betaListeningDoubleMap.getOrDefault(entry.getKey(), 0.0);
         double thumbAmount = ((boolean) (Boolean) (entry.getValue())) ? 1.0 : 0.0;
         double botCrystalValue = MatrixIdentifierUtils.getAmount(orDefault, thumbAmount, 14.0, betaListeningValue);
         if (Math.abs(botCrystalValue - thumbAmount) < 0.001) {
            botCrystalValue = thumbAmount;
         }

         this.betaListeningDoubleMap.put((String)entry.getKey(), botCrystalValue);
      }
   }

   private void renderAnywayEnable(DrawContext context) {
      double anywayEnableValue = Math.max(0.0, Math.min(1.0, this.anywayEnableQuintScissor.getAmount()));
      if (!(anywayEnableValue <= 0.001)) {
         MinecraftClient instance = MinecraftClient.getInstance();
         int scaledWidth = instance.getWindow().getScaledWidth();
         int scaledHeight = instance.getWindow().getScaledHeight();
         int scaledX = (int)instance.mouse.getScaledX(instance.getWindow());
         int scaledY = (int)instance.mouse.getScaledY(instance.getWindow());
         VertexTextureFont fontRenderer = CorzClient.fontRenderer();
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         VertexGuiUtils.handle(-scaledWidth, -scaledHeight, scaledWidth * 2, scaledHeight * 2, ((int)(178.0 * anywayEnableValue) & 0xFF) << 24);
         int[] agreeScaled = this.getAgreeScaled();
         int matrixPeekValue = agreeScaled[0];
         int matrixStackValue = agreeScaled[1];
         int anywayEnableValue4 = agreeScaled[2];
         int materialFactor = agreeScaled[3];
         byte corzLicenseLimit = 10;
         int anywayEnableValue7 = 10492704;
         float panelOverlay = CorzRoundedUtils.getPanelOverlay((float)anywayEnableValue);

         try {
            MatrixPeekUtilsRender.renderMatrixStack(
               context,
               matrixPeekValue - 2,
               matrixStackValue - 2,
               anywayEnableValue4 + 4,
               materialFactor + 4,
               corzLicenseLimit + 1,
               new Color(anywayEnableValue7 >> 16 & 0xFF, anywayEnableValue7 >> 8 & 0xFF, anywayEnableValue7 & 0xFF, 120).getRGB(),
               9.0F,
               0.6F
            );
            MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, matrixStackValue, anywayEnableValue4, materialFactor, corzLicenseLimit, -65990120, anywayEnableValue7 | -1342177280);
            int anywayEnableValue8 = matrixPeekValue + anywayEnableValue4 / 2;
            float roundOffset = matrixStackValue + 14;
            byte anywayEnableValue10 = 18;
            MatrixPeekUtilsIdentifier.handle(anywayEnableValue8 - anywayEnableValue10 / 2.0F, roundOffset, anywayEnableValue10, anywayEnableValue10, anywayEnableIdentifier, new Color(-6284512), matrixStack);
            float anywayEnableTotal = roundOffset + (anywayEnableValue10 + 9);
            int anywayEnableValue11 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 3;
            int rendererFontValue = anywayEnableValue4 - 28;

            for (String anywayEnableText : this.getRendererFont(this.formatYouWant(), rendererFontValue)) {
               fontRenderer.handleForStringFloat(anywayEnableText, matrixPeekValue + (anywayEnableValue4 - fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(anywayEnableText)) / 2.0F, anywayEnableTotal, new Color(-920587));
               anywayEnableTotal += anywayEnableValue11;
            }

            float f13 = anywayEnableTotal + 6.0F;

            for (String getRendererFontItem : this.getRendererFont(this.formatTextNext(), rendererFontValue)) {
               fontRenderer.handleForStringFloat(getRendererFontItem, matrixPeekValue + (anywayEnableValue4 - fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(getRendererFontItem)) / 2.0F, f13, new Color(12765135));
               f13 += anywayEnableValue11;
            }

            double anywayEnableValue13 = MatrixClearUtils.getAmount();
            this.anywayEnableSpring.handleAmount(this.anywayEnableFlag ? 1.0 : 0.0, 190.0, 14.0, anywayEnableValue13);
            if (this.anywayEnableSpring.springPosition < -0.25) {
               this.anywayEnableSpring.springPosition = -0.25;
               this.anywayEnableSpring.anywayEnableCount = 0.0;
            }

            double anywayEnableLimit = Math.max(0.0, Math.min(1.0, this.anywayEnableSpring.springPosition));
            int anywayEnableValue14 = agreeScaled[4];
            int brownValue = agreeScaled[5];
            int boxHeight = agreeScaled[7];
            int anywayEnableValue17 = brownValue + (boxHeight - 15) / 2;
            MatrixPeekUtilsRender.render(
               context,
               anywayEnableValue14,
               anywayEnableValue17,
               15,
               15,
               4,
               CardMatrixUtils.getValue(CardMatrixUtils.getValue7(), CardMatrixUtils.getReleaseRight(1.0), anywayEnableLimit),
               CardMatrixUtils.getValue(352321535, CardMatrixUtils.getReleaseRight(1.0), anywayEnableLimit)
            );
            if (anywayEnableLimit > 0.01) {
               float max18 = (float)Math.max(0.0, this.anywayEnableSpring.springPosition);
               float peekPositionFactor = 12.0F * max18;
               MatrixPeekUtilsIdentifier.handle(
                  anywayEnableValue14 + (15.0F - peekPositionFactor) / 2.0F,
                  anywayEnableValue17 + (15.0F - peekPositionFactor) / 2.0F,
                  peekPositionFactor,
                  peekPositionFactor,
                  CHECK_ICON,
                  new Color(255, 255, 255, (int)(255.0 * anywayEnableLimit)),
                  matrixStack
               );
            }

            fontRenderer.handleForStringFloat(
               "I agree to use this module.",
               anywayEnableValue14 + 15 + 7,
               fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, boxHeight),
               new Color(this.anywayEnableFlag ? -920587 : 12765135)
            );
            this.renderRendererFont(context, agreeScaled[8], agreeScaled[9], agreeScaled[10], agreeScaled[11], "Cancel", -6642766, scaledX, scaledY, true);
            this.renderRendererFont(context, agreeScaled[12], agreeScaled[13], agreeScaled[14], agreeScaled[15], "Enable Anyway", -7465964, scaledX, scaledY, this.anywayEnableFlag);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   public void updateLockedModule() {
      if (AutoYourUtils.isContainsKey(this.next)) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Module Locked", AutoYourUtils.getOrDefaultForIlIllIiIll(this.next), false);
      } else if (!this.next.isEnabled() && AutoYourUtils.isContains(this.next) && !AutoYourUtils.isContainsPVP_CRYSTAL_SET(this.next)) {
         this.updateAnywayEnable();
      } else {
         this.next.toggle();
      }
   }

   private void renderListeningRenderer(DrawContext context, float listeningRendererValue, float editValue) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      String listeningRendererText = this.listeningBindFlag ? this.formatTime() : this.formatText();
      boolean listeningRendererFlag = this.isFlagNext() && !this.listeningBindFlag;
      float listeningRendererValue3 = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 6.0F;
      float round = Math.round(listeningRendererValue3 * 0.4F);
      float peekPositionFactor = round * 1.0F;
      float listeningRendererValue6 = listeningRendererFlag ? peekPositionFactor + 14.0F : fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.listeningBindFlag ? "Listening..." : listeningRendererText) + 10.0F;
      float peekBetaValue = listeningRendererValue - listeningRendererValue6;
      if (listeningRendererFlag) {
         PeekBetaUtils.handle(peekBetaValue, editValue, (int)listeningRendererValue6, (int)listeningRendererValue3, 3.0F, new Color(-15461350), MatrixPeekUtilsRender.getMatrixStack());
         this.handleIdentifier(peekBetaValue + (listeningRendererValue6 - peekPositionFactor) / 2.0F, editValue + (listeningRendererValue3 - round) / 2.0F, peekPositionFactor, round, 1.0F);
      } else {
         PeekBetaUtils.handle(peekBetaValue, editValue, (int)listeningRendererValue6, (int)listeningRendererValue3, 3.0F, new Color(-15461350), MatrixPeekUtilsRender.getMatrixStack());
         if (this.listeningBindFlag) {
            this.renderTime(context, peekBetaValue + 5.0F, editValue + (listeningRendererValue3 - CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown()) / 2.0F - 1.0F);
         } else {
            int listeningRendererValue8 = this.next.getBindKey() >= 0 ? -920587 : -7630437;
            CorzClient.fontRenderer()
               .handleForStringFloat(listeningRendererText, peekBetaValue + 5.0F, editValue + (listeningRendererValue3 - CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown()) / 2.0F - 1.0F, new Color(listeningRendererValue8));
         }
      }
   }

   private String formatKeybind() {
      return "Keybind already in use";
   }

   private void renderBlatantCaution(DrawContext context, int blatantCautionValue, int blatantCautionValue2, int blatantCautionValue3, int blatantCautionLimit, int betaListeningLimit) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int blatantCautionValue4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown();
      int max5 = Math.max(10, Math.min(14, Math.round(blatantCautionValue4 * 0.85F)));
      int blatantCautionValue6 = max5 & -2;
      int maxRound = Math.max(10, Math.round(blatantCautionValue6 * 0.88F));
      int betaListeningFactor = maxRound & -2;
      boolean pvpCrystal = AutoYourUtils.canPvpCrystal(this.next);
      boolean contains = AutoYourUtils.isContainsForIlIllIiIll(this.next);
      boolean containsKey = AutoYourUtils.isContainsKey(this.next);
      boolean containsKeyForIlIllIiIll = AutoYourUtils.isContainsKeyForIlIllIiIll(this.next);
      VertexTextureFont vertexTexture = CorzClient.fontManager.getSmallFont();
      int round = Math.round(vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown());
      int beta = getBeta(vertexTexture);
      int betaTotal = blatantCautionValue + blatantCautionValue2 + 7;
      int brownValue = blatantCautionValue3 - round / 2;
      int blatantCautionValue11 = containsKeyForIlIllIiIll ? betaTotal + beta + 7 : blatantCautionValue + blatantCautionValue2 + 6;
      int blatantCautionValue12 = contains ? blatantCautionValue11 + blatantCautionValue6 + 7 : blatantCautionValue11;
      int blatantCautionValue13 = blatantCautionValue12 + blatantCautionValue6 + 7;
      int blatantCautionValue14 = pvpCrystal ? blatantCautionValue13 + betaListeningFactor + 7 : blatantCautionValue13;
      int blatantCautionValue15 = blatantCautionValue12 + blatantCautionValue6 / 2;
      int blatantCautionValue16 = blatantCautionValue13 + blatantCautionValue6 / 2;
      int blatantCautionValue17 = blatantCautionValue14 + blatantCautionValue6 / 2;
      boolean blatantCautionFlag = containsKeyForIlIllIiIll && blatantCautionLimit >= betaTotal - 5 && blatantCautionLimit <= betaTotal + beta + 5 && betaListeningLimit >= brownValue - 5 && betaListeningLimit <= brownValue + round + 5;
      int value = this.canFlag(blatantCautionLimit, betaListeningLimit, blatantCautionValue15, blatantCautionValue3)
         ? 1
         : (pvpCrystal && this.canFlag(blatantCautionLimit, betaListeningLimit, blatantCautionValue16, blatantCautionValue3) ? 2 : (containsKey && this.canFlag(blatantCautionLimit, betaListeningLimit, blatantCautionValue17, blatantCautionValue3) ? 3 : (blatantCautionFlag ? 4 : 0)));
      if (value != this.lastCautionLevel) {
         this.lastCautionLevel = value;
         this.lastTimeMillis = System.currentTimeMillis();
      }

      long blatantCautionValue18 = System.currentTimeMillis() - this.lastTimeMillis;
      float blatantCautionValue19 = value == 0 ? 0.0F : (float)Math.min(1.0, blatantCautionValue18 / 120.0);
      if (contains) {
         this.render(
            context,
            betaListeningIdentifier,
            blatantCautionValue11 + blatantCautionValue6 / 2,
            blatantCautionValue3,
            blatantCautionValue6,
            CardMatrixUtils.getReleaseRight(1.0),
            0.0F,
            0.482F,
            0.482F,
            MatrixEspUtils.releaseRightValue & 16777215
         );
      }

      this.render(context, blatantCautionIdentifier, blatantCautionValue15, blatantCautionValue3, blatantCautionValue6, -1, value == 1 ? blatantCautionValue19 : 0.0F, 0.482F, 0.5F, 16777215);
      if (pvpCrystal) {
         this.render(context, anywayEnableIdentifier, blatantCautionValue16, blatantCautionValue3, betaListeningFactor, -1750963, value == 2 ? blatantCautionValue19 : 0.0F, 0.482F, 0.501F, 15026253);
      }

      if (containsKey) {
         this.render(context, LOCK_ICON, blatantCautionValue17, blatantCautionValue3, blatantCautionValue6, -7630437, value == 3 ? blatantCautionValue19 : 0.0F, 0.5F, 0.5F, 16777215);
      }

      if (containsKeyForIlIllIiIll) {
         this.handleBeta(vertexTexture, betaTotal, vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, round), value == 4 ? 0.82 + 0.18000000000000005 * blatantCautionValue19 : 0.82);
      }

      if (value != 0) {
         float max = (float)Math.max(0.0, Math.min(1.0, (blatantCautionValue18 - 320L) / 140.0));
         if (max > 0.0F) {
            this.blatantCautionText = switch (value) {
               case 1 -> AutoYourUtils.getOrDefault(this.next).isEmpty() ? "No description available." : AutoYourUtils.getOrDefault(this.next);
               case 2 -> "This module is blatant, use with caution.";
               case 3 -> AutoYourUtils.getOrDefaultForIlIllIiIll(this.next);
               default -> AutoYourUtils.getOrDefaultBOT_PVP_MAP(this.next);
            };
            this.blatantCautionFlag = value == 2;
            this.blatantCautionAmount = max;

            this.cautionColor = switch (value) {
               case 1 -> blatantCautionValue15;
               case 2 -> blatantCautionValue16;
               case 3 -> blatantCautionValue17;
               default -> betaTotal + beta / 2;
            };
            this.blatantCautionValue = blatantCautionValue3 - blatantCautionValue6 / 2;
         }
      }
   }

   public void handleMaxAmount(double betaListeningBound) {
      this.maxBetaListeningBound = Math.max(0.0, betaListeningBound);
      this.listeningSlideOffset = this.maxBetaListeningBound;
      this.keybindListeningActive = false;
   }

   private void update3() {
      this.updateTimeFlag();

      for (Module getItem : PngIcons.getInstance().getItems()) {
         if (getItem != this.next && getItem.getBindKey() == this.betaListeningValue) {
            getItem.setBindKey(-1);
         }
      }
   }

   private void renderKeybindAmount(DrawContext context, int keybindValue, int keybindValue2, int keybindValue3, int pillLimit, int betaListeningLimit) {
      boolean keybindFlag = this.next.isEnabled();
      double keybindValue6 = MatrixClearUtils.getAmount();
      double springValue = keybindFlag ? 1.0 : 0.0;
      this.keybindSpring.handleAmount(springValue, 140.0, 22.0, keybindValue6);
      if (this.keybindSpring.springPosition < -0.15) {
         this.keybindSpring.springPosition = -0.15;
         this.keybindSpring.anywayEnableCount = 0.0;
      }

      if (this.keybindSpring.springPosition > 1.15) {
         this.keybindSpring.springPosition = 1.15;
         this.keybindSpring.anywayEnableCount = 0.0;
      }

      double amountForDouble = getAmountForDouble(Math.max(0.0, Math.min(1.0, this.keybindSpring.springPosition)));
      boolean canFlag2 = RenderPillUtils.canFlag(pillLimit, betaListeningLimit, this.bindKeybindValue + this.listeningKeybindValue, keybindValue, keybindValue2, keybindValue3, 7);
      if (canFlag2 && !this.keybindActive) {
         this.keybindActive = true;
         this.keybindQuintScissor.handleAmountTime(1.0);
      }

      if (!canFlag2 && this.keybindActive) {
         this.keybindActive = false;
         this.keybindQuintScissor.handleAmountTime(0.0);
      }

      double keybindValue9 = this.keybindQuintScissor.getAmount();
      boolean containsKey = AutoYourUtils.isContainsKey(this.next);
      RenderPillUtils.render(context, this.bindKeybindValue + this.listeningKeybindValue, keybindValue, keybindValue2, keybindValue3, amountForDouble, containsKey ? 0.0 : keybindValue9, containsKey ? 0.32 : 1.0);
   }

   public void handleTimeFlag(boolean flag) {
      this.listeningActive = flag;
      if (flag) {
         this.listeningPending = true;
         this.fadeAnimation.handleAmountFlag(1.0);
         this.fadeAnimation.handleAmountTime(1.0);
      }

      this.updateKeybindListening();
   }

   private boolean canKeybindListening(double keybindListeningValue, double mouseY) {
      if (this.betaListeningActive) {
         return RenderPillUtils.canFlag(keybindListeningValue, mouseY, this.bindKeybindValue + this.listeningKeybindValue, this.getAmountKeybindListeningAmount() + 18, 18, 12, 5);
      } else {
         int amountKeybindListeningAmount = this.getAmountKeybindListeningAmount();
         return RenderPillUtils.canFlag(keybindListeningValue, mouseY, this.bindKeybindValue + this.listeningKeybindValue, amountKeybindListeningAmount, 36, 8, 5);
      }
   }

   public void renderBlatantCaution(DrawContext context) {
      if (this.keybindBindActive) {
         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            settingComponent.render(context);
         }

         if (this.blatantCautionText != null) {
            this.renderKeybindBind(context, this.blatantCautionText, this.cautionColor, this.blatantCautionValue);
         }

         if (this.anywayEnableActive || this.anywayEnableQuintScissor.getAmount() > 0.001) {
            this.renderAnywayEnable(context);
         }

         if (this.anywayActive || this.scaledWindowQuintScissor.getAmount() > 0.001) {
            this.renderScaledWindow(context);
         }
      }
   }

   private int getValue3() {
      return this.isNext() ? 0 : 6;
   }

   private void renderKeybindBind(DrawContext context, String keybindBindText, int keybindBindValue, int keybindBindValue2) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int keybindBindValue3 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown();
      short rendererFontValue = 168;
      byte keybindBindValue5 = 8;
      byte keybindBindValue6 = 6;
      byte keybindBindValue7 = 3;
      byte corzLicenseLimit = 8;
      boolean blatantCautionFlag = this.blatantCautionFlag;
      int keybindBindBound = blatantCautionFlag ? 11 : 0;
      int keybindBindValue9 = blatantCautionFlag ? 7 : 0;
      var rendererFont = this.getRendererFont(keybindBindText, rendererFontValue);
      int keybindBindFactor = keybindBindValue3 + keybindBindValue7;
      int aggroPreemptBound = 0;

      for (String rendererFontItem : rendererFont) {
         aggroPreemptBound = Math.max(aggroPreemptBound, (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(rendererFontItem));
      }

      int rocketsBuyBound = rendererFont.size() * keybindBindFactor - keybindBindValue7;
      int materialFactor = keybindBindBound + keybindBindValue9 + aggroPreemptBound + keybindBindValue5 * 2;
      int keybindBindValue11 = Math.max(keybindBindBound, rocketsBuyBound) + keybindBindValue6 * 2;
      int matrixPeekValue = keybindBindValue - materialFactor / 2;
      if (matrixPeekValue < this.bindKeybindValue + 3) {
         matrixPeekValue = this.bindKeybindValue + 3;
      }

      if (matrixPeekValue + materialFactor > this.bindKeybindValue + this.listeningKeybindValue - 3) {
         matrixPeekValue = this.bindKeybindValue + this.listeningKeybindValue - 3 - materialFactor;
      }

      int matrixStackValue = keybindBindValue2 - keybindBindValue11 - 7;
      if (matrixStackValue < 3) {
         matrixStackValue = keybindBindValue2 + 20 + 7;
      }

      float max2 = Math.max(0.0F, Math.min(1.0F, this.blatantCautionAmount));
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      int keybindBindValue14 = blatantCautionFlag ? -6284512 : -1;
      int keybindBindValue15 = blatantCautionFlag ? 10492704 : 0;
      MatrixPeekUtilsRender.renderMatrixStack(
         context,
         matrixPeekValue - 2,
         matrixStackValue,
         materialFactor + 4,
         keybindBindValue11 + 3,
         corzLicenseLimit + 1,
         new Color(keybindBindValue15 >> 16 & 0xFF, keybindBindValue15 >> 8 & 0xFF, keybindBindValue15 & 0xFF, (int)((blatantCautionFlag ? 95 : 140) * max2)).getRGB(),
         blatantCautionFlag ? 7.5F : 6.0F,
         (blatantCautionFlag ? 0.6F : 0.5F) * max2
      );
      MatrixPeekUtilsRender.renderForDrawContextFloat(
         context,
         matrixPeekValue,
         matrixStackValue,
         materialFactor,
         keybindBindValue11,
         corzLicenseLimit,
         new Color(17, 18, 24, (int)(252.0F * max2)).getRGB(),
         new Color(keybindBindValue14 >> 16 & 0xFF, keybindBindValue14 >> 8 & 0xFF, keybindBindValue14 & 0xFF, (int)((blatantCautionFlag ? 175 : 22) * max2)).getRGB()
      );
      int keybindBindValue16 = matrixPeekValue + keybindBindValue5;
      if (blatantCautionFlag) {
         int roundOffset = matrixStackValue + (keybindBindValue11 - keybindBindBound) / 2;
         MatrixPeekUtilsIdentifier.handle(matrixPeekValue + keybindBindValue5, roundOffset, keybindBindBound, keybindBindBound, anywayEnableIdentifier, new Color(10492704 | (int)(255.0F * max2) << 24, true), matrixStack);
         keybindBindValue16 = matrixPeekValue + keybindBindValue5 + keybindBindBound + keybindBindValue9;
      }

      int keybindBindValue18 = blatantCautionFlag ? -6284512 : -920587;
      Color color = new Color(keybindBindValue18 >> 16 & 0xFF, keybindBindValue18 >> 8 & 0xFF, keybindBindValue18 & 0xFF, (int)(255.0F * max2));
      int keybindBindTotal = matrixStackValue + (keybindBindValue11 - rocketsBuyBound) / 2 - 2;

      for (String corzfontText : rendererFont) {
         int keybindBindValue19 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText);
         float vertexTextureValue = blatantCautionFlag ? keybindBindValue16 + (aggroPreemptBound - keybindBindValue19) / 2.0F : matrixPeekValue + (materialFactor - keybindBindValue19) / 2.0F;
         fontRenderer.handleForStringFloat(corzfontText, vertexTextureValue, keybindBindTotal, color);
         keybindBindTotal += keybindBindFactor;
      }
   }

   public static void handleTexturesIcons(int texturesIconsValue3, int texturesIconsValue4) {
      texturesIconsValue = texturesIconsValue3;
      texturesIconsValue4TexturesI = texturesIconsValue4;
   }

   private boolean isFlagNext() {
      return this.next.getBindKey() < 0;
   }

   private void handleIdentifier(float identifierValue, float roundOffset, float peekPositionFactor, float round, float colorFactor) {
      if (!(peekPositionFactor <= 0.5F) && !(round <= 0.5F) && !(colorFactor <= 0.004F)) {
         MatrixPeekUtilsIdentifier.handle(identifierValue, roundOffset, peekPositionFactor, round, identifier, getColor(-7630437, colorFactor), MatrixPeekUtilsRender.getMatrixStack());
      }
   }

   private static Color getColor(int colorValue, float colorFactor) {
      int colorFactor2 = colorValue >>> 24 & 0xFF;
      if (colorFactor2 == 0) {
         colorFactor2 = 255;
      }

      return new Color(colorValue >> 16 & 0xFF, colorValue >> 8 & 0xFF, colorValue & 0xFF, Math.max(0, Math.min(255, Math.round(colorFactor2 * colorFactor))));
   }

   private int getValue(int betaListeningValue, int betaListeningValue2, double betaListeningFactor) {
      betaListeningFactor = Math.max(0.0, Math.min(1.0, betaListeningFactor));
      int betaListeningValue3 = betaListeningValue >> 24 & 0xFF;
      int betaListeningValue4 = betaListeningValue >> 16 & 0xFF;
      int betaListeningValue5 = betaListeningValue >> 8 & 0xFF;
      int betaListeningValue6 = betaListeningValue & 0xFF;
      int betaListeningValue7 = betaListeningValue2 >> 24 & 0xFF;
      int betaListeningValue8 = betaListeningValue2 >> 16 & 0xFF;
      int betaListeningValue9 = betaListeningValue2 >> 8 & 0xFF;
      int betaListeningValue10 = betaListeningValue2 & 0xFF;
      return (int)(betaListeningValue3 + (betaListeningValue7 - betaListeningValue3) * betaListeningFactor) << 24
         | (int)(betaListeningValue4 + (betaListeningValue8 - betaListeningValue4) * betaListeningFactor) << 16
         | (int)(betaListeningValue5 + (betaListeningValue9 - betaListeningValue5) * betaListeningFactor) << 8
         | (int)(betaListeningValue6 + (betaListeningValue10 - betaListeningValue6) * betaListeningFactor);
   }

   public int getValue4() {
      return this.getAmountKeybindListeningAmount();
   }

   private int getResetRenderer() {
      return (int)(CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont("Reset") * 0.95F) + 10;
   }

   public boolean isListeningGlfw() {
      return this.listeningBindFlag || this.listeningGlfw != null;
   }

   private void renderListeningRenderer(DrawContext context, float listeningRendererValue, float f13, float listeningRendererFactor) {
      long listeningRendererValue3 = System.currentTimeMillis() - this.timeMillis;
      double listeningRendererFactor2 = (0.7 + 0.3 * Math.sin(listeningRendererValue3 / 300.0)) * listeningRendererFactor;
      CorzClient.fontRenderer().handleForStringFloat("Listening", listeningRendererValue, f13, this.getReleaseRight((int)(255.0 * listeningRendererFactor2)));
      float listeningRendererTotal = listeningRendererValue + CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont("Listening");

      for (int i = 0; i < 3; i++) {
         double max4 = Math.max(0.0, Math.min(1.0, 0.5 + 0.5 * Math.sin((listeningRendererValue3 - i * 200) / 500.0 * Math.PI * 2.0)));
         CorzClient.fontRenderer().handleForStringFloat(".", listeningRendererTotal, f13, this.getReleaseRight((int)(255.0 * max4 * listeningRendererFactor2)));
         listeningRendererTotal += CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont(".");
      }
   }

   public boolean canKeybindBind(double keybindBindLimit, double heightAmount, double keybindBindValue) {
      if (!this.keybindBindActive) {
         return false;
      } else {
         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            if (settingComponent.canFlag(keybindBindLimit, heightAmount, keybindBindValue)) {
               return true;
            }
         }

         if (this.betaListeningActive
            && keybindBindLimit >= this.bindKeybindValue
            && keybindBindLimit <= this.bindKeybindValue + this.listeningKeybindValue
            && heightAmount >= this.getAmountKeybindListeningAmount()
            && heightAmount <= this.getAmountKeybindListeningAmount() + this.rowHeightPx) {
            this.maxBetaListeningBound = ConstAmountUtils.getAmountForDouble(this.maxBetaListeningBound - keybindBindValue * 28.0, this.getAmountNext(), ConstAmountUtils.getMaxInt(this.rowHeightPx));
            this.nanoTimeKeybind = System.nanoTime();
            return true;
         } else {
            return false;
         }
      }
   }

   public void handleFlagTime(boolean flag) {
      if (this.listeningActive) {
         this.listeningPending = true;
         this.fadeAnimation.handleAmountFlag(1.0);
         this.fadeAnimation.handleAmountTime(1.0);
      } else {
         this.listeningPending = flag;
      }
   }

   public void updateAmountFlag() {
      if (!this.listeningActive) {
         this.listeningPending = false;
         this.fadeAnimation.handleAmountFlag(0.0);
         this.listeningFadeProgress = 0.0;
         this.listeningFlag = 36.0;
      }
   }

   private void renderTime(DrawContext context, float betaListeningValue, float f13) {
      if (this.timeMillis == 0L) {
         this.timeMillis = System.currentTimeMillis();
      }

      this.renderListeningRenderer(context, betaListeningValue, f13, 1.0F);
   }

   public int getAmountFlag() {
      return this.listeningActive ? this.getListeningFlag() : (int)Math.ceil(this.listeningFlag);
   }

   private BetaListening.SettingComponent getClassForConfigSettingInt(ConfigSetting autoEspBase, int classValue, int classValue2, int classValue3, int classValue4) {
      if (autoEspBase instanceof BooleanSetting booleanBase) {
         return new BetaListening.SettingComponent(new RenderBeta(booleanBase, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof ButtonBase buttonBase) {
         return new BetaListening.SettingComponent(new RendererPress(buttonBase, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof IntegerSetting integerHelper) {
         return new BetaListening.SettingComponent(new IntegerFlag(integerHelper, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof DoubleSetting doubleHelper) {
         return new BetaListening.SettingComponent(new FlagHandle(doubleHelper, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof RangeSetting minmax2) {
         return new BetaListening.SettingComponent(new MinmaxHandle(minmax2, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof EnumSetting enumHelper) {
         return new BetaListening.SettingComponent(new ScissorDisable(enumHelper, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof StringBase stringBase) {
         return new BetaListening.SettingComponent(new MaxRenderer(stringBase, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof ColorSetting colorBase) {
         return new BetaListening.SettingComponent(new ScissorMax(colorBase, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof ItemList itemList) {
         return new BetaListening.SettingComponent(new SearchEdit(itemList, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof FriendListBase friendListBase) {
         return new BetaListening.SettingComponent(new PlayersClear(friendListBase, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof ConfigListSetting configListBase) {
         return new BetaListening.SettingComponent(new ConfigBrowserPanel(configListBase, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof RadioSetting radioBase) {
         return new BetaListening.SettingComponent(new RadioCom(radioBase, classValue, classValue2, classValue3, classValue4), classValue4);
      } else if (autoEspBase instanceof PaletteBase paletteBase) {
         return new BetaListening.SettingComponent(new ThemeWhole(paletteBase, classValue, classValue2, classValue3, classValue4), classValue4);
      } else {
         return autoEspBase instanceof KeybindSetting keybindBase ? new BetaListening.SettingComponent(new ListeningGlfw(keybindBase, classValue, classValue2, classValue3, classValue4, this), classValue4) : null;
      }
   }

   public boolean isKeybindBindActive() {
      return this.keybindBindActive;
   }

   private void handle(double betaListeningLimit, double mouseY) {
      int[] scaledRenderer = this.getScaledRenderer();
      if (betaListeningLimit >= scaledRenderer[4] && betaListeningLimit <= scaledRenderer[4] + scaledRenderer[6] && mouseY >= scaledRenderer[5] && mouseY <= scaledRenderer[5] + scaledRenderer[7]) {
         this.updateListeningBind();
      } else if (betaListeningLimit >= scaledRenderer[8] && betaListeningLimit <= scaledRenderer[8] + scaledRenderer[10] && mouseY >= scaledRenderer[9] && mouseY <= scaledRenderer[9] + scaledRenderer[11]) {
         this.update3();
      } else if (betaListeningLimit >= scaledRenderer[12] && betaListeningLimit <= scaledRenderer[12] + scaledRenderer[14] && mouseY >= scaledRenderer[13] && mouseY <= scaledRenderer[13] + scaledRenderer[15]) {
         this.update();
      } else {
         if (betaListeningLimit < scaledRenderer[0] || betaListeningLimit > scaledRenderer[0] + scaledRenderer[2] || mouseY < scaledRenderer[1] || mouseY > scaledRenderer[1] + scaledRenderer[3]) {
            this.update();
         }
      }
   }

   private void renderBindListening(DrawContext context, int bindListeningValue) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int bindKeybindValueOffset = this.bindKeybindValue + 8;
      int round = Math.round(bindListeningValue + fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 1.0F);
      String bindListeningText = "KeyBind: ";
      float bindListeningValue3 = fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(bindListeningText);
      boolean bindListeningFlag = this.isFlagNext();
      this.bindAmount = MatrixIdentifierUtils.getAmount(this.bindAmount, this.listeningBindFlag ? 1.0 : 0.0, 17.0, MatrixClearUtils.getAmount());
      if (this.bindAmount < 5.0E-4) {
         this.bindAmount = 0.0;
      }

      if (this.bindAmount > 0.9995) {
         this.bindAmount = 1.0;
      }

      float bindListeningLimit = (float)getAmountForDouble(this.bindAmount);
      float bindListeningValue4 = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 1.0F;
      float bindListeningFactor = Math.round(bindListeningValue4 * 0.5F);
      float bindListeningFactor2 = bindListeningFactor * 1.0F;
      float bindListeningValue5 = bindListeningFlag ? bindListeningFactor2 + 8.0F : fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.formatText()) + 1.0F;
      float bindListeningValue6 = fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("Listening...") + 1.0F;
      float bindListeningValue7 = bindListeningValue5 + (bindListeningValue6 - bindListeningValue5) * bindListeningLimit;
      int matrixPeekValue = Math.round(bindKeybindValueOffset + bindListeningValue3);
      int configsLikeValue = round + 3;
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      float materialFactor = !(bindListeningLimit <= 0.001F) && !(bindListeningLimit >= 0.999F) ? bindListeningValue7 : (int)bindListeningValue7;
      MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, configsLikeValue, materialFactor, (int)bindListeningValue4, 3.0F, -15461350);
      fontRenderer.handleForStringFloat(bindListeningText, bindKeybindValueOffset, round, new Color(-7630437));
      if (bindListeningLimit > 0.004F) {
         this.renderListeningRenderer(context, bindKeybindValueOffset + bindListeningValue3, round, bindListeningLimit);
      }

      if (!bindListeningFlag && bindListeningLimit < 0.996F) {
         fontRenderer.handleForStringFloat(this.formatText(), bindKeybindValueOffset + bindListeningValue3, round, getColor(-920587, 1.0F - bindListeningLimit));
      }

      if (bindListeningFlag && bindListeningLimit < 0.996F) {
         float bindListeningFactor3 = 1.0F - 0.45F * bindListeningLimit;
         float peekPositionFactor = bindListeningFactor2 * bindListeningFactor3;
         float bindListeningValue12 = bindListeningFactor * bindListeningFactor3;
         this.handleIdentifier(matrixPeekValue + (bindListeningValue5 - peekPositionFactor) / 2.0F, configsLikeValue + ((int)bindListeningValue4 - bindListeningValue12) / 2.0F, peekPositionFactor, bindListeningValue12, 1.0F - bindListeningLimit);
      }
   }

   private int[] getKeybindListeningBindKeybindValue() {
      int keybindListeningValue = this.getAmountKeybindListeningAmount();
      return new int[]{this.bindKeybindValue + 8, keybindListeningValue + 36 + 2, this.listeningKeybindValue - 16, 120};
   }

   public double getAmountFlagListeningActive() {
      return this.listeningActive ? this.getListeningFlag() : this.listeningFlag;
   }

   private void handleKeybindListening(int keybindListeningValue, int keybindBindLimit) {
      if (!CardMatrixUtils.isModernFlag()) {
         float min3 = ConstAmountUtils.getMin(this.listeningSlideOffset);
         float matrixStackLimit = ConstAmountUtils.getMin(ConstAmountUtils.getAmount(this.getAmountNext(), keybindBindLimit) - this.listeningSlideOffset);
         if (!(min3 <= 0.01F) || !(matrixStackLimit <= 0.01F)) {
            int matrixStackValue = TwoDidUtils.getValue(CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), MatrixEspUtils.releaseRightValue, 0.05 * this.clampedMaxProgress));
            float bindKeybindValueOffset = this.bindKeybindValue + 4;
            float keybindListeningValue6 = this.listeningKeybindValue - 8.0F;
            MatrixStackUtilsAmount.handleVertexColor(bindKeybindValueOffset, keybindListeningValue, keybindListeningValue6, 10.0F, matrixStackValue, min3, true);
            MatrixStackUtilsAmount.handleVertexColor(bindKeybindValueOffset, keybindListeningValue + keybindBindLimit - 10, keybindListeningValue6, 10.0F, matrixStackValue, matrixStackLimit, false);
         }
      }
   }

   public String formatSecuritySocial() {
      if (this.next instanceof Friends) {
         return "SOCIAL";
      } else if (this.next instanceof License) {
         return "SECURITY";
      } else {
         return !(this.next instanceof Panic) && !(this.next instanceof SelfDestruct) ? "INTERFACE" : "SYSTEM";
      }
   }

   public int getValue5() {
      return 36;
   }

   public boolean isBetaListeningActive() {
      return this.betaListeningActive;
   }

   private Color getReleaseRight(int releaseRightBound) {
      return new Color(
         MatrixEspUtils.releaseRightValue >> 16 & 0xFF,
         MatrixEspUtils.releaseRightValue >> 8 & 0xFF,
         MatrixEspUtils.releaseRightValue & 0xFF,
         Math.max(0, Math.min(255, releaseRightBound))
      );
   }

   private String formatTextNext() {
      return "\""
         + this.next.getCategory()
         + "\" is a very blatant module. Using it on a server with an anti-cheat can get you banned instantly, or very fast. Use at your own risk.";
   }

   public void updateKeybindScissorSettingCompone() {
      this.updateListeningGlfw();

      for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
         settingComponent.update();
      }
   }

   private void renderRendererFont(DrawContext context, String rendererFontText, int rendererFontValue, int keybindScissorTotal, int width, boolean rendererFontFlag, boolean rendererFontFlag2) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int rendererFontValue4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown();
      int f13 = keybindScissorTotal + (15 - rendererFontValue4) / 2;
      Color color = new Color(rendererFontFlag2 ? -920587 : -7630437);
      String vertexTextureText = rendererFontFlag ? "-" : "+";
      fontRenderer.handleForStringFloat(vertexTextureText, rendererFontValue, f13, color);
      int rendererFontValue6 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(vertexTextureText) + 5;
      fontRenderer.handleForStringFloat(rendererFontText, rendererFontValue + rendererFontValue6, f13, color);
      int matrixPeekValue = rendererFontValue + rendererFontValue6 + (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(rendererFontText) + 8;
      int f2 = keybindScissorTotal + 7;
      if (matrixPeekValue < rendererFontValue + width) {
         MatrixPeekUtilsRender.renderMatrixPeek(
            context, matrixPeekValue, f2, rendererFontValue + width - matrixPeekValue, MatrixPeekUtilsRender.getAmount(), new Color(255, 255, 255, rendererFontFlag2 ? 52 : 30).getRGB()
         );
      }
   }

   public void handleFlagForDoubleInt(double betaListeningValue, double mouseReleasedValue, int button) {
      if (this.keybindBindActive) {
         this.keybindListeningActive = false;
         if (this.next instanceof RotateQuaternionfApi rotateQuaternionfApi) {
            rotateQuaternionfApi.updateQuaternionfRotate();
         }

         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            settingComponent.handle(betaListeningValue, mouseReleasedValue, button);
         }
      }
   }

   public void handleKeybindBind(int bindKeybindValue, double keybindBindValue) {
      this.bindKeybindValue = bindKeybindValue;
      this.keybindListeningAmount = getAmount(keybindBindValue);
      this.updateKeybindListening();
   }

   public boolean canFlag(char ch, int betaListeningValue) {
      if (!this.keybindBindActive) {
         return false;
      } else {
         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            if (settingComponent.isFlag()) {
               return settingComponent.canFlag(ch, betaListeningValue);
            }
         }

         if (this.listeningActive) {
            for (BetaListening.SettingComponent settingComponent2 : this.keybindScissorSettingCompone) {
               if (settingComponent2.canFlag(ch, betaListeningValue)) {
                  return true;
               }
            }

            return false;
         } else if (!this.listeningPending) {
            return false;
         } else {
            for (BetaListening.SettingComponent settingComponent3 : this.keybindScissorSettingCompone) {
               if (settingComponent3.canFlag(ch, betaListeningValue)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private void updateText() {
      PressOpen openDeal = PressOpen.getOpenDeal(this.next);
      if (openDeal != null) {
         this.keybindScissorSettingCompone.clear();
         this.keybindScissorSettingCompone.add(new BetaListening.SettingComponent(openDeal, 44));
      } else {
         this.keybindScissorSettingCompone.clear();
         String betaListeningText = null;

         for (ConfigSetting autoEspBase : this.next.getSettings()) {
            if (!autoEspBase.isHiddenFromList()) {
               String text = autoEspBase.getGroupName();
               if (text != null && !text.equals(betaListeningText)) {
                  this.keybindScissorSettingCompone.add(new BetaListening.SettingComponent(new BetaListening.GroupHeaderComponent(text), 15));
                  this.betaListeningBooleanMap.putIfAbsent(text, false);
                  this.betaListeningDoubleMap.putIfAbsent(text, 0.0);
               }

               betaListeningText = text;
               byte betaListeningValue;
               if (autoEspBase instanceof BooleanSetting) {
                  betaListeningValue = 20;
               } else if (autoEspBase instanceof ButtonBase) {
                  betaListeningValue = 24;
               } else if (autoEspBase instanceof EnumSetting) {
                  betaListeningValue = 24;
               } else if (autoEspBase instanceof StringBase) {
                  betaListeningValue = 44;
               } else if (autoEspBase instanceof ColorSetting) {
                  betaListeningValue = 24;
               } else if (autoEspBase instanceof ItemList) {
                  betaListeningValue = 32;
               } else if (autoEspBase instanceof FriendListBase) {
                  betaListeningValue = 24;
               } else if (autoEspBase instanceof RadioSetting) {
                  betaListeningValue = 24;
               } else if (autoEspBase instanceof KeybindSetting) {
                  betaListeningValue = 24;
               } else {
                  betaListeningValue = 32;
               }

               BetaListening.SettingComponent classForConfigSettingInt = this.getClassForConfigSettingInt(autoEspBase, 0, 0, 0, betaListeningValue);
               if (classForConfigSettingInt != null) {
                  classForConfigSettingInt.betaText = text;
                  this.keybindScissorSettingCompone.add(classForConfigSettingInt);
               }
            }
         }
      }
   }

   private List<String> getRendererFont(String rendererFontText, int rendererFontValue) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      var arrayList = new ArrayList();
      StringBuilder stringBuilder = new StringBuilder();

      for (String rendererFontText2 : rendererFontText.split(" ")) {
         String corzfontText = stringBuilder.length() == 0 ? rendererFontText2 : stringBuilder + " " + rendererFontText2;
         if (fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) > rendererFontValue && stringBuilder.length() > 0) {
            arrayList.add(stringBuilder.toString());
            stringBuilder = new StringBuilder(rendererFontText2);
         } else {
            stringBuilder.setLength(0);
            stringBuilder.append(corzfontText);
         }
      }

      if (stringBuilder.length() > 0) {
         arrayList.add(stringBuilder.toString());
      }

      return arrayList;
   }

   private void renderKeybindBind(DrawContext context, int keybindBindValue) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      fontRenderer.handleForStringFloat("Keybind", this.bindKeybindValue + ConstAmountUtils.getValue() + 4, fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(keybindBindValue, 20), new Color(-3947581));
      this.renderListeningRenderer(
         context,
         this.bindKeybindValue + ConstAmountUtils.getValue(this.listeningKeybindValue),
         keybindBindValue + (20.0F - (fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 6.0F)) / 2.0F
      );
   }

   public boolean isFlagKeybindScissorSettingCompone() {
      for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
         if (settingComponent.isBetaObject()) {
            return true;
         }
      }

      return false;
   }

   private boolean canFlag(int betaListeningValue, int betaListeningLimit, int betaListeningValue3, int betaListeningValue4) {
      byte betaListeningFactor = 10;
      int betaListeningFactor2 = betaListeningValue - betaListeningValue3;
      int betaListeningFactor3 = betaListeningLimit - betaListeningValue4;
      return betaListeningFactor2 * betaListeningFactor2 + betaListeningFactor3 * betaListeningFactor3 <= betaListeningFactor * betaListeningFactor;
   }

   private boolean isFlag7() {
      return this.next instanceof Friends || this.next instanceof License || this.next instanceof SelfDestruct || this.next instanceof Configs;
   }

   private void updateTimeFlag() {
      this.anywayActive = false;
      this.scaledWindowQuintScissor.handleAmountFlag(0.0);
   }

   private boolean isFlag8() {
      return !this.isFlag7() && !(this.next instanceof ThemeCustomizer) && !(this.next instanceof Configs);
   }

   private void updateTimeFlagAnywayEnableActive() {
      this.anywayEnableActive = false;
      this.anywayEnableQuintScissor.handleAmountFlag(0.0);
      AutoYourUtils.handlePvpCrystal(this.next);
      this.next.setEnabled(true);
   }

   private String formatYouWant() {
      return "Are you sure you want to enable this module?";
   }

   @Override
   public void handleListeningGlfw(ListeningGlfw listeningGlfw) {
      this.listeningBindFlag = false;
      this.timeMillis = 0L;
      this.listeningGlfw = listeningGlfw;
   }

   private void updateAmountMax() {
      this.listeningFadeProgress = 1.0;
      this.listeningFlag = this.rowHeightPx;
      double betaListeningValue = ConstAmountUtils.getMaxInt(this.rowHeightPx);
      double amountNext = this.getAmountNext();
      this.maxBetaListeningBound = ConstAmountUtils.getAmountForDouble(this.maxBetaListeningBound, amountNext, betaListeningValue);
      this.listeningSlideOffset = MatrixIdentifierUtils.getAmount(this.listeningSlideOffset, this.maxBetaListeningBound, 18.0, MatrixClearUtils.getAmount());
      if (Math.abs(this.listeningSlideOffset - this.maxBetaListeningBound) < 0.05) {
         this.listeningSlideOffset = this.maxBetaListeningBound;
      }

      this.listeningSlideOffset = ConstAmountUtils.getAmountForDouble(this.listeningSlideOffset, amountNext, betaListeningValue);
      this.updateKeybindListening();
   }

   private String formatTime() {
      if (this.timeMillis == 0L) {
         this.timeMillis = System.currentTimeMillis();
      }

      return "Listening" + ".".repeat((int)((System.currentTimeMillis() - this.timeMillis) / 400L % 4L));
   }

   private void renderKeybindScissor(DrawContext context, int keybindScissorValue, int mouseY, int rendererFontValue, int keybindScissorTotal, int width) {
      int index = 0;

      while (index < this.keybindScissorSettingCompone.size()) {
         BetaListening.SettingComponent settingComponent = this.keybindScissorSettingCompone.get(index);
         if (settingComponent.betaObject instanceof BetaListening.GroupHeaderComponent groupHeaderComponent) {
            double keybindScissorFactor = this.getAmount(groupHeaderComponent.keybindScissorText);
            groupHeaderComponent.handle(rendererFontValue, keybindScissorTotal, width);
            this.renderRendererFont(context, groupHeaderComponent.keybindScissorText, rendererFontValue, keybindScissorTotal, width, this.betaListeningBooleanMap.getOrDefault(groupHeaderComponent.keybindScissorText, false), groupHeaderComponent.canFlag(keybindScissorValue, mouseY));
            keybindScissorTotal += 15;
            int keybindScissorTotal2 = 0;

            int index2;
            for (index2 = index + 1; index2 < this.keybindScissorSettingCompone.size() && groupHeaderComponent.keybindScissorText.equals(this.keybindScissorSettingCompone.get(index2).betaText); index2++) {
               keybindScissorTotal2 += this.keybindScissorSettingCompone.get(index2).componentHeight;
            }

            int keybindScissorLimit = (int)Math.round(keybindScissorTotal2 * keybindScissorFactor);
            if (keybindScissorLimit > 1) {
               context.enableScissor(this.bindKeybindValue, keybindScissorTotal, this.bindKeybindValue + this.listeningKeybindValue, keybindScissorTotal + keybindScissorLimit);
               int my2 = keybindScissorTotal;

               for (int i = index + 1; i < index2; i++) {
                  BetaListening.SettingComponent settingComponent2 = this.keybindScissorSettingCompone.get(i);
                  settingComponent2.handle(rendererFontValue, my2, width);
                  settingComponent2.renderBetaText(context, keybindScissorValue, mouseY);
                  my2 += settingComponent2.componentHeight;
               }

               context.disableScissor();
            }

            keybindScissorTotal += keybindScissorLimit;
            index = index2;
         } else {
            settingComponent.handle(rendererFontValue, keybindScissorTotal, width);
            settingComponent.renderBetaText(context, keybindScissorValue, mouseY);
            keybindScissorTotal += settingComponent.getBetaText();
            index++;
         }
      }
   }

   public void handleTimeFlag(String betaListeningText) {
      this.betaListeningActive = true;
      this.listeningActive = false;
      this.betaListeningText = betaListeningText == null ? "" : betaListeningText;
      this.listeningPending = true;
      this.fadeAnimation.handleAmountFlag(1.0);
      this.fadeAnimation.handleAmountTime(1.0);
      this.listeningFadeProgress = 1.0;
      if (this.timeMax == null) {
         this.timeMax = new Swap();
      }

      this.updateKeybindListening();
   }

   private void renderRendererFont(DrawContext context, int rendererFontValue, int blatantCautionLimit, int betaListeningLimit) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      this.renderBlatantCaution(
         context,
         this.bindKeybindValue + 8,
         (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.next.getCategory()),
         rendererFontValue + (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() / 2 + 3,
         blatantCautionLimit,
         betaListeningLimit
      );
   }

   public float getAmountFloat() {
      return (float)(this.keybindListeningAmount - Math.floor(this.keybindListeningAmount));
   }

   public void handleListeningGlfwForInt(int listeningGlfwValue) {
      if (this.listeningGlfw != null) {
         ListeningGlfw listeningGlfw = this.listeningGlfw;
         this.listeningGlfw = null;
         listeningGlfw.handle(listeningGlfwValue);
      } else {
         this.next.setBindKey(PageRaltUtils.getValueForInt(listeningGlfwValue));
         this.listeningBindFlag = false;
         this.timeMillis = 0L;
         this.handle(this.next.getBindKey());
      }
   }

   private static String formatGlfw(int glfwValue) {
      String formatNone = PageRaltUtils.formatNone(glfwValue);
      if (formatNone != null) {
         return formatNone;
      } else {
         String glfwGetKeyName = GLFW.glfwGetKeyName(glfwValue, 0);
         return glfwGetKeyName != null ? glfwGetKeyName.toUpperCase() : PageRaltUtils.formatText(glfwValue);
      }
   }

   private void handleMaxAmountForDouble(double betaListeningValue) {
      int betaListeningValue2 = this.getAmountKeybindListeningAmount() + 62;
      int constFactor = ConstAmountUtils.getMax(this.rowHeightPx);
      double amountNext = this.getAmountNext();
      double maxInt = ConstAmountUtils.getMaxInt(this.rowHeightPx);
      int betaListeningValue6 = ConstAmountUtils.getValue(amountNext, maxInt, constFactor);
      if (betaListeningValue6 <= 0) {
         this.keybindListeningActive = false;
      } else {
         this.maxBetaListeningBound = ConstAmountUtils.getAmount(betaListeningValue - this.betaListeningAmount, betaListeningValue2, constFactor, betaListeningValue6, ConstAmountUtils.getAmount(amountNext, maxInt));
         this.listeningSlideOffset = this.maxBetaListeningBound;
         this.nanoTimeKeybind = System.nanoTime();
      }
   }

   private void updateTimeFlagAnywayActive() {
      this.anywayActive = true;
      this.scaledWindowQuintScissor.handleAmountTime(1.0);
   }

   private void updateAnywayEnable() {
      this.anywayEnableActive = true;
      this.anywayEnableFlag = false;
      this.anywayEnableSpring.springPosition = 0.0;
      this.anywayEnableSpring.anywayEnableCount = 0.0;
      this.anywayEnableQuintScissor.handleAmountTime(1.0);
   }

   private boolean canKeybindListeningForDouble(double keybindListeningLimit, double keybindListeningLimit2) {
      int keybindValue = this.getAmountKeybindListeningAmount() + 62;
      int keybindListeningValue = ConstAmountUtils.getMax(this.rowHeightPx);
      double amountNext = this.getAmountNext();
      double maxInt = ConstAmountUtils.getMaxInt(this.rowHeightPx);
      int keybindListeningLimit4 = ConstAmountUtils.getValue(amountNext, maxInt, keybindListeningValue);
      int keybindListeningValue4 = this.bindKeybindValue + ConstAmountUtils.getValueInt(this.listeningKeybindValue);
      if (keybindListeningLimit4 > 0 && !(keybindListeningLimit < keybindListeningValue4 - 2) && !(keybindListeningLimit > keybindListeningValue4 + 2 + 3) && !(keybindListeningLimit2 < keybindValue) && !(keybindListeningLimit2 > keybindValue + keybindListeningValue)) {
         float keybindListeningValue5 = ConstAmountUtils.getAmount(amountNext, maxInt, this.listeningSlideOffset, keybindValue, keybindListeningValue);
         this.betaListeningAmount = keybindListeningLimit2 >= keybindListeningValue5 && keybindListeningLimit2 <= keybindListeningValue5 + keybindListeningLimit4 ? keybindListeningLimit2 - keybindListeningValue5 : keybindListeningLimit4 / 2.0;
         this.keybindListeningActive = true;
         this.handleMaxAmountForDouble(keybindListeningLimit2);
         return true;
      } else {
         return false;
      }
   }

   public long getNanoTimeNano() {
      return this.nanoTimeNano;
   }

   @Override
   public boolean canGlfwListening(ListeningGlfw listeningGlfw) {
      return this.listeningGlfw == listeningGlfw;
   }

   public static double getAmount(double betaListeningFactor) {
      double betaListeningFactor2 = 1.0 / CorzRoundedUtils.getFramebufferHeight();
      return Math.rint(betaListeningFactor * betaListeningFactor2) / betaListeningFactor2;
   }

   public void handleFlagTimeForFlag(boolean flag) {
      this.keybindBindActive = flag;
      if (!flag) {
         if (this.anywayEnableActive) {
            this.anywayEnableActive = false;
            this.anywayEnableQuintScissor.handleAmountFlag(0.0);
         }

         if (this.anywayActive) {
            this.anywayActive = false;
            this.scaledWindowQuintScissor.handleAmountFlag(0.0);
         }
      }
   }

   public BetaListening(int bindKeybindValue, int amount, int listeningKeybindValue, Module next) {
      this.anywayActive = false;
      this.betaListeningValue = -1;
      this.betaListeningStringList = new ArrayList<>();
      this.scaledWindowQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 160, 0.0);
      this.blatantCautionText = null;
      this.blatantCautionAmount = 1.0F;
      this.blatantCautionFlag = false;
      this.lastCautionLevel = 0;
      this.lastTimeMillis = 0L;
      this.keybindScissorSettingCompone = new ArrayList<>();
      this.listeningFlag = 36.0;
      this.nanoTimeNano = 0L;
      this.betaBadgeVisible = true;
      this.listeningText = null;
      this.keybindBindActive = true;
      this.listeningPending = false;
      this.listeningBindFlag = false;
      this.keybindActive = false;
      this.listeningActive = false;
      this.timeMillis = 0L;
      this.bindPillProgress = 0.0;
      this.listeningAmount = 0.0;
      this.wasHovered = false;
      this.resetAmount = 0.0;
      this.resetProgress = 0.0;
      this.bindAmount = 0.0;
      this.betaListeningBooleanMap = new HashMap<>();
      this.betaListeningDoubleMap = new HashMap<>();
      this.betaListeningActive = false;
      this.keybindListeningActive = false;
      this.betaListeningText = "";
      this.displayText = "";
      this.cachedText = null;
      this.keybindBindText = "";
      this.rowHeightPx = 36;
      this.keybindBindValue = -7630437;
      this.targetTextColor = -7630437;
      this.currentTextColor = -7630437;
      this.keybindBindAmount = 1.0;
      this.maxBetaListeningBound = 0.0;
      this.listeningSlideOffset = 0.0;
      this.betaListeningAmount = 0.0;
      this.keybindAmount = 0.0;
      this.cachedMaxWidth = -1.0F;
      this.nanoTimeKeybind = 0L;
      this.bindKeybindValue = bindKeybindValue;
      this.listeningKeybindValue = listeningKeybindValue;
      this.next = next;
      this.fadeAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 250, 0.0);
      this.keybindBindQuintScissor = new QuintScissor(QuintBounceType.SINE_IN_OUT, 260, next.isEnabled() ? 1.0 : 0.0);
      this.keybindQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 100, 0.0);
      this.keybindListeningAmount = amount;
      this.updateText();
      this.updateKeybindListening();
      this.keybindSpring = new Spring(next.isEnabled() ? 1.0 : 0.0);
   }

   private double getAmount(String betaListeningText) {
      if (betaListeningText == null) {
         return 1.0;
      } else {
         Double double2 = this.betaListeningDoubleMap.get(betaListeningText);
         return double2 == null ? 1.0 : double2;
      }
   }

   private boolean canKeybindBind(double keybindBindLimit, double betaListeningLimit, int keybindBindValue) {
      boolean keybindBind = keybindBindLimit >= this.bindKeybindValue
         && keybindBindLimit <= this.bindKeybindValue + this.listeningKeybindValue
         && betaListeningLimit >= this.getAmountKeybindListeningAmount()
         && betaListeningLimit <= this.getAmountKeybindListeningAmount() + this.rowHeightPx;
      if (keybindBindValue == 0 && this.canKeybindListeningForDouble(keybindBindLimit, betaListeningLimit)) {
         return true;
      } else if (this.canKeybindListening(keybindBindLimit, betaListeningLimit)) {
         this.updateLockedModule();
         return true;
      } else if (!this.canFlag(betaListeningLimit)) {
         return keybindBind;
      } else if (this.canKeybindBindForDouble(keybindBindLimit, betaListeningLimit)) {
         this.listeningBindFlag = true;
         this.timeMillis = System.currentTimeMillis();
         return true;
      } else if (this.canFlag(keybindBindLimit, betaListeningLimit)) {
         this.resetProgress = 1.0;
         this.next.syncSettingHandlers();
         return true;
      } else {
         for (BetaListening.SettingComponent settingComponent : this.keybindScissorSettingCompone) {
            if (settingComponent.canBetaListening(keybindBindLimit, betaListeningLimit, keybindBindValue)) {
               return true;
            }
         }

         return keybindBind;
      }
   }

   public void updateListeningGlfw() {
      this.listeningBindFlag = false;
      this.timeMillis = 0L;
      this.listeningGlfw = null;
   }

   private void updateTimeFlag4() {
      this.anywayEnableActive = false;
      this.anywayEnableQuintScissor.handleAmountFlag(0.0);
   }

   private boolean canBindKeybind(double bindKeybindLimit, double mouseY) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int bindKeybindValue = this.getAmountKeybindListeningAmount();
      int round = Math.round(bindKeybindValue + 8 - 4 + fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 1.0F);
      float bindKeybindValue3 = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 1.0F;
      float bindKeybindValue4 = this.isFlagNext() ? Math.round(bindKeybindValue3 * 0.5F) * 1.0F + 8.0F : fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.formatText()) + 1.0F;
      int max3 = Math.max(
         this.bindKeybindValue + 8 + (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("KeyBind: None"),
         Math.round(this.bindKeybindValue + 8 + fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("KeyBind: ")) + (int)bindKeybindValue4
      );
      return bindKeybindLimit >= this.bindKeybindValue + 8 && bindKeybindLimit <= max3 && mouseY >= round && mouseY <= round + 3 + (int)bindKeybindValue3;
   }

   private static double getAmountForDouble(double betaListeningFactor) {
      betaListeningFactor = Math.max(0.0, Math.min(1.0, betaListeningFactor));
      return betaListeningFactor * betaListeningFactor * (3.0 - 2.0 * betaListeningFactor);
   }

   private void handleBeta(VertexTextureFont vertexTextureFont, float betaTotal, float betaValue, double betaValue2) {
      Color color = getColor(-1535700, (float)betaValue2);

      for (int i = 0; i < "BETA".length(); i++) {
         String valueOf = String.valueOf("BETA".charAt(i));
         vertexTextureFont.handle(valueOf, betaTotal, betaValue, color);
         betaTotal += vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(valueOf) + 1.0F;
      }
   }

   public void handleTextAmount(String betaListeningText, int value) {
      this.displayText = betaListeningText == null ? "" : betaListeningText;
      if (value != this.targetTextColor) {
         this.keybindBindValue = this.currentTextColor;
         this.targetTextColor = value;
         this.keybindBindAmount = 0.0;
      }
   }

   public void renderKeybindBind(DrawContext context, int keybindBindValue, int betaListeningLimit, float modulesClearValue) {
      if (this.keybindBindActive) {
         this.blatantCautionText = null;
         if (this.betaListeningActive) {
            this.renderKeybindBind(context, keybindBindValue, betaListeningLimit);
         } else if (this.listeningActive) {
            this.listeningPending = true;
            int amountKeybindListeningAmount = this.getAmountKeybindListeningAmount();
            int keybindBindTotal = amountKeybindListeningAmount;
            int value3 = this.getValue3();
            if (this.listeningText != null) {
               this.renderListeningText(context, this.bindKeybindValue + value3, amountKeybindListeningAmount, this.listeningKeybindValue - value3 * 2);
               keybindBindTotal = amountKeybindListeningAmount + 22;
            }

            if (!this.isFlag7()) {
               CorzClient.fontRenderer().handleForStringFloat(this.next.getCategory(), this.bindKeybindValue + 6, keybindBindTotal, new Color(-920587));
               keybindBindTotal += 18;
               if (this.isFlag8()) {
                  this.renderBindKeybind(context, keybindBindTotal);
                  keybindBindTotal += 20;
               } else {
                  keybindBindTotal += 4;
               }
            }

            this.renderKeybindScissor(context, keybindBindValue, betaListeningLimit, this.bindKeybindValue + value3, keybindBindTotal, this.listeningKeybindValue - value3 * 2);
         } else {
            double listeningFadeProgress11 = this.listeningFadeProgress;
            int vectorTransformValue = this.getAmountKeybindListeningAmount();
            int listeningFlag = this.getListeningFlag();
            float hashRegistryValue = (float)this.listeningFlag;
            double matrixIdentifierFactor = MatrixClearUtils.getAmount();
            boolean flag = !this.listeningActive && !this.listeningPending && this.canKeybindBind(keybindBindValue, betaListeningLimit);
            if (flag && !this.wasHovered) {
               ClientSounds.updateScrollHover();
            }

            this.wasHovered = flag;
            this.bindPillProgress = MatrixIdentifierUtils.getAmount(this.bindPillProgress, flag ? 1.0 : 0.0, flag ? 16.0 : 11.0, matrixIdentifierFactor);
            this.listeningAmount = MatrixIdentifierUtils.getAmount(this.listeningAmount, 0.0, 10.0, matrixIdentifierFactor);
            if (this.bindPillProgress < 5.0E-4) {
               this.bindPillProgress = 0.0;
            }

            if (this.listeningAmount < 5.0E-4) {
               this.listeningAmount = 0.0;
            }

            double keybindBindValue8 = this.next.isEnabled() ? 1.0 : 0.0;
            if (Math.abs(this.keybindBindQuintScissor.getTargetAmount() - keybindBindValue8) > 0.001) {
               this.keybindBindQuintScissor.handleAmountTime(keybindBindValue8);
            }

            double releaseRightLimit = this.keybindBindQuintScissor.getAmount();
            boolean keybindBindFlag = listeningFadeProgress11 > 0.001 && !this.next.getSettings().isEmpty();
            boolean contains = AutoYourUtils.isContainsForIlIllIiIll(this.next);
            if (!(vectorTransformValue + hashRegistryValue < texturesIconsValue) && vectorTransformValue <= texturesIconsValue4TexturesI) {
               if (keybindBindFlag) {
                  RenderMatrixUtilsRelease.renderForDrawContextFloat(
                     context, this.bindKeybindValue, vectorTransformValue, this.listeningKeybindValue, hashRegistryValue, 7.0F, releaseRightLimit, this.bindPillProgress, this.listeningAmount
                  );
               } else {
                  RenderMatrixUtilsRelease.render(context, this.bindKeybindValue, vectorTransformValue, this.listeningKeybindValue, hashRegistryValue, 7.0F, releaseRightLimit, this.bindPillProgress, this.listeningAmount);
               }

               int bindListeningValue = vectorTransformValue + 8 - 4;
               int keybindBindValue11 = contains ? CardMatrixUtils.getReleaseRight() : -920587;
               CorzClient.fontRenderer().handleForStringFloat(this.next.getCategory(), this.bindKeybindValue + 8, bindListeningValue, new Color(keybindBindValue11, true));
               this.renderBindListening(context, bindListeningValue);
               this.render(context, vectorTransformValue, keybindBindValue, betaListeningLimit);
               this.renderRendererFont(context, bindListeningValue, keybindBindValue, betaListeningLimit);
               if (keybindBindFlag) {
                  RenderMatrixUtilsRelease.renderForDrawContextInt(context, this.bindKeybindValue + 8, vectorTransformValue + 36 - 2, this.listeningKeybindValue - 16, Math.min(1.0, listeningFadeProgress11 * 2.0));
                  int keybindBindValue12 = vectorTransformValue + 36;
                  int keybindBindLimit = (int)Math.floor(hashRegistryValue) - 36;
                  if (keybindBindLimit > 0) {
                     if (this.next instanceof RotateQuaternionfApi rotateQuaternionfApi && listeningFadeProgress11 > 0.9) {
                        int[] keybindListening = this.getKeybindListeningBindKeybindValue();
                        rotateQuaternionfApi.renderQuaternionfRotate(context, keybindListening[0], keybindListening[1], keybindListening[2], keybindListening[3]);
                     }

                     context.enableScissor(this.bindKeybindValue, keybindBindValue12, this.bindKeybindValue + this.listeningKeybindValue, keybindBindValue12 + keybindBindLimit);
                     float y = (float)((1.0 - listeningFadeProgress11) * -5.0);
                     MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
                     matrixStack.push();
                     matrixStack.translate(0.0F, y, 0.0F);
                     context.getMatrices().pushMatrix();
                     context.getMatrices().translate(0.0F, y);

                     try {
                        this.renderKeybindScissor(context, keybindBindValue, betaListeningLimit, this.bindKeybindValue + 8, vectorTransformValue + 36 + this.getValueInt(), this.listeningKeybindValue - 16);
                        this.renderAmountReset(context, keybindBindValue, betaListeningLimit, vectorTransformValue + listeningFlag - 6 - 11);
                     } finally {
                        context.getMatrices().popMatrix();
                        matrixStack.pop();
                        context.disableScissor();
                     }
                  }
               }
            }
         }
      }
   }

   private boolean canFlag(double betaListeningLimit) {
      int betaListeningValue = this.getAmountKeybindListeningAmount() + 58;
      return betaListeningLimit >= betaListeningValue && betaListeningLimit < betaListeningValue + ConstAmountUtils.getMaxInt(this.rowHeightPx);
   }

   private static class GroupHeaderComponent {
      int headerX;
      int groupHeaderValue;
      static final int HEADER_HEIGHT = 15;
      final String keybindScissorText;
      int headerY;

      boolean canFlag(double groupHeaderLimit, double mouseY) {
         return groupHeaderLimit >= this.headerX && groupHeaderLimit <= this.headerX + this.groupHeaderValue && mouseY >= this.headerY && mouseY <= this.headerY + 15;
      }

      GroupHeaderComponent(String text) {
         this.keybindScissorText = text;
      }

      void handle(int value, int keybindScissorTotal, int width) {
         this.headerX = value;
         this.headerY = keybindScissorTotal;
         this.groupHeaderValue = width;
      }
   }

   private class SettingComponent {
      private String betaText;
      private final int componentHeight;
      private final Object betaObject;

      boolean canFlag(char ch, int settingComponentValue) {
         if (this.betaObject instanceof MaxRenderer maxRenderer) {
            return maxRenderer.canMaxTime(ch, settingComponentValue);
         } else if (this.betaObject instanceof ScissorMax scissorMax) {
            return scissorMax.canFlag(ch, settingComponentValue);
         } else if (this.betaObject instanceof SearchEdit searchEdit) {
            return searchEdit.canAppendFocus(ch, settingComponentValue);
         } else if (this.betaObject instanceof PlayersClear playersClear) {
            return playersClear.canIconsAppend(ch, settingComponentValue);
         } else {
            return this.betaObject instanceof ConfigBrowserPanel configShare ? configShare.onCharTyped(ch, settingComponentValue) : false;
         }
      }

      boolean isFlag() {
         if (this.betaObject instanceof SearchEdit searchEdit) {
            return searchEdit.isClusterOpen();
         } else if (this.betaObject instanceof PlayersClear playersClear) {
            return playersClear.isFlashCopied();
         } else {
            return this.betaObject instanceof ConfigBrowserPanel configShare ? configShare.isOverlayOpen() : false;
         }
      }

      void handle(double settingComponentValue, double mouseReleasedValue, int button) {
         if (this.betaObject instanceof IntegerFlag integerFlag) {
            integerFlag.handleFlag(settingComponentValue, mouseReleasedValue, button);
         } else if (this.betaObject instanceof FlagHandle flagHandle) {
            flagHandle.handleFlag(settingComponentValue, mouseReleasedValue, button);
         } else if (this.betaObject instanceof MinmaxHandle minmaxHandle) {
            minmaxHandle.handleFlag(settingComponentValue, mouseReleasedValue, button);
         } else if (this.betaObject instanceof ScissorMax scissorMax) {
            scissorMax.handleFlagForDoubleInt(settingComponentValue, mouseReleasedValue, button);
         } else if (this.betaObject instanceof SearchEdit searchEdit) {
            searchEdit.handleAmountFlag(settingComponentValue, mouseReleasedValue, button);
         } else if (this.betaObject instanceof PlayersClear playersClear) {
            playersClear.handleThumbHint(settingComponentValue, mouseReleasedValue, button);
         } else if (this.betaObject instanceof ConfigBrowserPanel configShare) {
            configShare.onMouseReleased(settingComponentValue, mouseReleasedValue, button);
         }
      }

      boolean canBetaListening(double betaListeningValue, double mouseY, int button) {
         if (this.betaObject instanceof PressOpen pressOpen) {
            return pressOpen.canMatrixStack(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof BetaListening.GroupHeaderComponent groupHeaderComponent) {
            if (groupHeaderComponent.canFlag(betaListeningValue, mouseY)) {
               BetaListening.this.betaListeningBooleanMap.put(groupHeaderComponent.keybindScissorText, !BetaListening.this.betaListeningBooleanMap.getOrDefault(groupHeaderComponent.keybindScissorText, false));
               return true;
            } else {
               return false;
            }
         } else if (this.betaText != null && BetaListening.this.getAmount(this.betaText) < 0.5) {
            return false;
         } else if (this.betaObject instanceof RenderBeta renderBeta) {
            return renderBeta.canFlag(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof RendererPress rendererPress) {
            return rendererPress.canTime(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof IntegerFlag integerFlag) {
            return integerFlag.canFlag(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof FlagHandle flagHandle) {
            return flagHandle.canFlag(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof MinmaxHandle minmaxHandle) {
            return minmaxHandle.canMinmaxFlag(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof ScissorDisable scissorDisable) {
            return scissorDisable.canAmount(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof MaxRenderer maxRenderer) {
            return maxRenderer.canRendererFont(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof ScissorMax scissorMax) {
            return scissorMax.canFlagTime(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof SearchEdit searchEdit) {
            return searchEdit.canOpenCluster(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof PlayersClear playersClear) {
            return playersClear.canIconsClear(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof ConfigBrowserPanel configShare) {
            return configShare.onMouseClicked(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof ThemeWhole themeWhole) {
            return themeWhole.canTheme(betaListeningValue, mouseY, button);
         } else if (this.betaObject instanceof RadioCom radioCom) {
            return radioCom.canFlag(betaListeningValue, mouseY, button);
         } else {
            return this.betaObject instanceof ListeningGlfw listeningGlfw ? listeningGlfw.canApi(betaListeningValue, mouseY, button) : false;
         }
      }

      void handleForDoubleInt(double settingComponentValue, double minmaxValue, int button) {
         if (this.betaObject instanceof IntegerFlag integerFlag) {
            integerFlag.handleFlagForDoubleInt(settingComponentValue, minmaxValue, button);
         } else if (this.betaObject instanceof FlagHandle flagHandle) {
            flagHandle.handleFlagForDoubleInt(settingComponentValue, minmaxValue, button);
         } else if (this.betaObject instanceof MinmaxHandle minmaxHandle) {
            minmaxHandle.handleMinmaxFlag(settingComponentValue, minmaxValue, button);
         } else if (this.betaObject instanceof ScissorMax scissorMax) {
            scissorMax.handleFlag(settingComponentValue, minmaxValue, button);
         } else if (this.betaObject instanceof SearchEdit searchEdit) {
            searchEdit.handleAmountThumb(settingComponentValue, minmaxValue, button);
         } else if (this.betaObject instanceof PlayersClear playersClear) {
            playersClear.handleThumbAmount(settingComponentValue, minmaxValue, button);
         } else if (this.betaObject instanceof ConfigBrowserPanel configShare) {
            configShare.onMouseDragged(settingComponentValue, minmaxValue, button);
         }
      }

      boolean canFlag(double settingComponentValue, double heightAmount, double keybindBindValue) {
         if (this.betaObject instanceof SearchEdit searchEdit) {
            return searchEdit.canAmountCluster(settingComponentValue, heightAmount, keybindBindValue);
         } else if (this.betaObject instanceof PlayersClear playersClear) {
            return playersClear.canBarAdded(settingComponentValue, heightAmount, keybindBindValue);
         } else {
            return this.betaObject instanceof ConfigBrowserPanel configShare ? configShare.onMouseScrolled(settingComponentValue, heightAmount, keybindBindValue) : false;
         }
      }

      boolean canFlag(int settingComponentValue, int scancode, int modifiers) {
         if (this.betaObject instanceof MaxRenderer maxRenderer) {
            return maxRenderer.canMaxTime(settingComponentValue, scancode, modifiers);
         } else if (this.betaObject instanceof ScissorMax scissorMax) {
            return scissorMax.canFlag(settingComponentValue, scancode, modifiers);
         } else if (this.betaObject instanceof SearchEdit searchEdit) {
            return searchEdit.canAppendFocus(settingComponentValue, scancode, modifiers);
         } else if (this.betaObject instanceof PlayersClear playersClear) {
            return playersClear.canAppendIcons(settingComponentValue, scancode, modifiers);
         } else {
            return this.betaObject instanceof ConfigBrowserPanel configShare ? configShare.onKeyPressed(settingComponentValue, scancode, modifiers) : false;
         }
      }

      void render(DrawContext context) {
         if (this.betaObject instanceof SearchEdit searchEdit) {
            searchEdit.renderSwitchSelected(context);
         } else if (this.betaObject instanceof PlayersClear playersClear) {
            playersClear.renderFlashCopied(context);
         } else if (this.betaObject instanceof ConfigBrowserPanel configShare) {
            configShare.renderTooltip(context);
         }
      }

      void update() {
         if (this.betaObject instanceof MaxRenderer maxRenderer) {
            maxRenderer.handleMaxTime(false);
         } else if (this.betaObject instanceof PlayersClear playersClear) {
            playersClear.updateIconsClear();
         } else if (this.betaObject instanceof ConfigBrowserPanel configShare) {
            configShare.clearInputFocus();
         }
      }

      int getBetaText() {
         if (this.betaObject instanceof BetaListening.GroupHeaderComponent) {
            return 15;
         } else {
            int betaFactor;
            if (this.betaObject instanceof ScissorMax scissorMax) {
               betaFactor = scissorMax.getTimeFlag();
            } else if (this.betaObject instanceof SearchEdit searchEdit) {
               betaFactor = searchEdit.getValue();
            } else if (this.betaObject instanceof PlayersClear playersClear) {
               betaFactor = playersClear.getTime();
            } else if (this.betaObject instanceof ConfigBrowserPanel configShare) {
               betaFactor = configShare.getContentHeight();
            } else if (this.betaObject instanceof ThemeWhole themeWhole) {
               betaFactor = themeWhole.getValueInt();
            } else if (this.betaObject instanceof RadioCom radioCom) {
               betaFactor = radioCom.getPlaylistsStations();
            } else {
               betaFactor = this.componentHeight;
            }

            return this.betaText != null ? (int)Math.round(betaFactor * BetaListening.this.getAmount(this.betaText)) : betaFactor;
         }
      }

      void renderBetaText(DrawContext context, int betaValue, int mouseY) {
         if (this.betaObject instanceof PressOpen pressOpen) {
            pressOpen.renderPressStart(context, betaValue, mouseY);
         } else if (!(this.betaObject instanceof BetaListening.GroupHeaderComponent)) {
            if (this.betaText == null || !(BetaListening.this.getAmount(this.betaText) < 0.02)) {
               if (this.betaObject instanceof RenderBeta renderBeta) {
                  renderBeta.renderTimeRenderer(context, betaValue, mouseY);
               } else if (this.betaObject instanceof RendererPress rendererPress) {
                  rendererPress.renderTimeRenderer(context, betaValue, mouseY);
               } else if (this.betaObject instanceof IntegerFlag integerFlag) {
                  integerFlag.renderRendererFont(context, betaValue, mouseY);
               } else if (this.betaObject instanceof FlagHandle flagHandle) {
                  flagHandle.renderTimeRenderer(context, betaValue, mouseY);
               } else if (this.betaObject instanceof MinmaxHandle minmaxHandle) {
                  minmaxHandle.renderMinmaxTime(context, betaValue, mouseY);
               } else if (this.betaObject instanceof ScissorDisable scissorDisable) {
                  scissorDisable.renderTextTime(context, betaValue, mouseY);
               } else if (this.betaObject instanceof MaxRenderer maxRenderer) {
                  maxRenderer.renderMaxRelease(context, betaValue, mouseY);
               } else if (this.betaObject instanceof ScissorMax scissorMax) {
                  scissorMax.renderScissorDisable(context, betaValue, mouseY);
               } else if (this.betaObject instanceof SearchEdit searchEdit) {
                  searchEdit.renderEditSelected(context, betaValue, mouseY);
               } else if (this.betaObject instanceof PlayersClear playersClear) {
                  playersClear.render(context, betaValue, mouseY);
               } else if (this.betaObject instanceof ConfigBrowserPanel configShare) {
                  configShare.renderPanel(context, betaValue, mouseY);
               } else if (this.betaObject instanceof ThemeWhole themeWhole) {
                  themeWhole.renderPaletteWhole(context, betaValue, mouseY);
               } else if (this.betaObject instanceof RadioCom radioCom) {
                  radioCom.renderPlaylistsReload(context, betaValue, mouseY);
               } else if (this.betaObject instanceof ListeningGlfw listeningGlfw) {
                  listeningGlfw.renderListeningRelease(context, betaValue, mouseY);
               }
            }
         }
      }

      void handle(int settingComponentValue, int editClusterValue, int width) {
         int betaText = this.getBetaText();
         if (this.betaObject instanceof PressOpen pressOpen) {
            pressOpen.handleBuilderMatrix(settingComponentValue, editClusterValue, width, betaText);
         } else if (this.betaObject instanceof BetaListening.GroupHeaderComponent groupHeaderComponent) {
            groupHeaderComponent.handle(settingComponentValue, editClusterValue, width);
         } else {
            if (this.betaObject instanceof RenderBeta renderBeta) {
               renderBeta.handle(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof RendererPress rendererPress) {
               rendererPress.handle(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof IntegerFlag integerFlag) {
               integerFlag.handle(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof FlagHandle flagHandle) {
               flagHandle.handle(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof MinmaxHandle minmaxHandle) {
               minmaxHandle.handle(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof ScissorDisable scissorDisable) {
               scissorDisable.handle(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof MaxRenderer maxRenderer) {
               maxRenderer.handle(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof ScissorMax scissorMax) {
               scissorMax.handle(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof SearchEdit searchEdit) {
               searchEdit.handleEditSelected(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof PlayersClear playersClear) {
               playersClear.handleFriendsUser(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof ConfigBrowserPanel configShare) {
               configShare.setLayout(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof ThemeWhole themeWhole) {
               themeWhole.handlePaletteWhole(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof RadioCom radioCom) {
               radioCom.handlePlaylistsReload(settingComponentValue, editClusterValue, width, betaText);
            } else if (this.betaObject instanceof ListeningGlfw listeningGlfw) {
               listeningGlfw.handleListening(settingComponentValue, editClusterValue, width, betaText);
            }
         }
      }

      boolean isBetaObject() {
         return this.betaObject instanceof MaxRenderer maxRenderer && maxRenderer.isFlag();
      }

      SettingComponent(Object object, int value) {
         this.betaObject = object;
         this.componentHeight = value;
      }
   }
}
