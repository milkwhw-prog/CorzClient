package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.MatrixStackUtilsAmount.Anims;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import com.corz.client.utils.IconsClearUtilsValue2;
import com.corz.client.utils.IconsClearUtilsFormat;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class ClearIcons {
   private String substring;
   private boolean clearModulesFlag;
   private boolean clearFlag;
   private int boxY;
   private long lastNanoTime;
   private String modulesSearchText;
   private float clearAmount;
   private static final Identifier modulesClearIdentifier = Identifier.of("corzclient", "textures/gui/icons/search.png");
   private final ClearEdit substringClearEdit;
   private static final String CLEAR = "clear";
   private boolean substringActive;
   private float iconsAmount;
   private int boxWidth;
   private float iconsY;
   private float iconsX;
   private boolean modulesSearchFlag;
   private final QuintScissor hoverAnim;
   private final QuintScissor modulesClearQuintScissor;
   private int modulesClearValue;
   private int modulesSearchValue = 7;
   private static final Identifier identifier = Identifier.of("corzclient", "textures/gui/icons/command.png");
   private static final boolean contains = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac");
   private final Anims modulesAnims;
   private final QuintScissor iconsQuintScissor;
   private boolean modulesClearActive;
   private int modulesValue;

   private boolean canClearAmount(double iconsValue, double scaledY) {
      return this.clearFlag
         && !this.substring.isEmpty()
         && System.nanoTime() - this.lastNanoTime < 300000000L
         && MatrixStackUtilsAmount.canFlag(iconsValue, scaledY, this.iconsX, this.iconsY, this.clearAmount, this.iconsAmount);
   }

   private void renderClearAmount(DrawContext context, int iconsValue, int matrixStackLimit, int scaledY, float iconsLimit) {
      float configsLikeValue = this.boxY + (this.modulesClearValue - 12) / 2.0F;
      this.iconsX = iconsValue - 2;
      this.iconsY = this.boxY;
      this.clearAmount = this.modulesValue + this.boxWidth - this.iconsX;
      this.iconsAmount = this.modulesClearValue;
      this.clearFlag = iconsLimit > 0.5F;
      boolean iconsFlag = this.clearFlag && MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, this.iconsX, this.iconsY, this.clearAmount, this.iconsAmount);
      float cardMatrixFactor = this.modulesAnims.getAmount("clear", iconsFlag);
      if (!(iconsLimit <= 0.01F)) {
         float ctxOffset = iconsValue + 6.0F;
         float y = configsLikeValue + 6.0F;
         float x2 = (0.8F + 0.19999999F * MatrixStackUtilsAmount.getAmountForFloat(iconsLimit)) * this.modulesAnims.getTime("clear", 0.14F);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, ctxOffset, y, x2);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(iconsLimit);

         try {
            if (cardMatrixFactor > 0.01F) {
               MatrixPeekUtilsRender.renderMatrixPeek(context, iconsValue, configsLikeValue, 12.0F, 12.0F, 6.0F, MatrixStackUtilsAmount.getValue(-1, 0.08F * cardMatrixFactor));
            }

            MatrixPeekUtilsIdentifier.handle(
               ctxOffset - 5.0F,
               y - 5.0F,
               10.0F,
               10.0F,
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("close"),
               new Color(CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor), true),
               MatrixPeekUtilsRender.getMatrixStack()
            );
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      }
   }

   private static int getValue(int iconsValue, int iconsValue2) {
      return (iconsValue2 & 0xFF) << 24 | iconsValue & 16777215;
   }

   public boolean canClearModules(double modulesLimit, double iconsClearValue, int modulesValue) {
      boolean clearModulesFlag = modulesLimit >= this.modulesValue && modulesLimit <= this.modulesValue + this.boxWidth && iconsClearValue >= this.boxY && iconsClearValue <= this.boxY + this.modulesClearValue;
      if (clearModulesFlag && modulesValue == 0 && this.canClearAmount(modulesLimit, iconsClearValue)) {
         this.substring = "";
         this.substringActive = false;
         this.clearModulesFlag = true;
         this.substringClearEdit.updateTime();
         this.modulesAnims.handleTimeForString("clear");
         return true;
      } else {
         this.clearModulesFlag = clearModulesFlag;
         this.substringActive = false;
         if (this.clearModulesFlag) {
            this.substringClearEdit.updateTime();
         }

         return clearModulesFlag;
      }
   }

   public void setModulesSearchValue(int modulesSearchValue) {
      this.modulesSearchValue = modulesSearchValue;
   }

   public ClearIcons(int value, int value2, int min, int value3) {
      this.modulesSearchText = "CorzClient";
      this.modulesSearchFlag = true;
      this.substring = "";
      this.clearModulesFlag = false;
      this.modulesClearActive = false;
      this.substringClearEdit = new ClearEdit();
      this.substringActive = false;
      this.modulesClearQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 200);
      this.iconsQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 180, 1.0);
      this.hoverAnim = new QuintScissor(QuintBounceType.CUBIC_OUT, 150, 1.0);
      this.modulesAnims = new Anims();
      this.modulesValue = value;
      this.boxY = value2;
      this.boxWidth = min;
      this.modulesClearValue = value3;
   }

   private static void handle(QuintScissor quintScissor, double iconsValue) {
      if (Math.abs(quintScissor.getTargetAmount() - iconsValue) > 0.001) {
         quintScissor.handleAmountTime(iconsValue);
      }
   }

   public boolean canClearSubstring(char substringLimit, int substringValue) {
      if (!this.clearModulesFlag) {
         return false;
      } else if (substringLimit >= ' ' && substringLimit != 127) {
         this.substring = IconsClearUtilsFormat.formatText(this.substring, this.substringActive, substringLimit);
         this.substringActive = false;
         this.substringClearEdit.updateTime();
         return true;
      } else {
         return false;
      }
   }

   private static String formatRendererFont(String formatRendererFont, float rendererFontLimit) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      if (rendererFontLimit <= 0.0F) {
         return "";
      } else if (fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatRendererFont) <= rendererFontLimit) {
         return formatRendererFont;
      } else {
         String corzfontText = formatRendererFont;

         while (corzfontText.length() > 1 && fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) > rendererFontLimit) {
            corzfontText = corzfontText.substring(1);
         }

         return corzfontText;
      }
   }

   public void setModulesSearchFlag(boolean modulesSearchFlag) {
      this.modulesSearchFlag = modulesSearchFlag;
   }

   public boolean isClearModulesFlag() {
      return this.clearModulesFlag;
   }

   public void handleSubstringClear(String substring) {
      this.substring = substring;
      this.substringActive = false;
   }

   private static double getAmount(double iconsLimit) {
      return iconsLimit < 0.0 ? 0.0 : (iconsLimit > 1.0 ? 1.0 : iconsLimit);
   }

   public void handleMin(int value, int value2, int min, int value3) {
      this.modulesValue = value;
      this.boxY = value2;
      this.boxWidth = min;
      this.modulesClearValue = value3;
   }

   public void handleModulesSearch(String modulesSearchText) {
      this.modulesSearchText = modulesSearchText == null ? "" : modulesSearchText;
   }

   public boolean canMin(double iconsLimit, double iconsClearValue) {
      return iconsLimit >= this.modulesValue && iconsLimit <= this.modulesValue + this.boxWidth && iconsClearValue >= this.boxY && iconsClearValue <= this.boxY + this.modulesClearValue;
   }

   public boolean canClearSubstring(int substringValue, int scancode, int modifiers) {
      if (!this.modulesSearchFlag || substringValue != 75 || (!contains || (modifiers & 8) == 0) && (contains || (modifiers & 2) == 0)) {
         if (!this.clearModulesFlag) {
            return false;
         } else if (IconsClearUtilsFormat.canFlag(contains, substringValue, modifiers)) {
            this.substringActive = !this.substring.isEmpty();
            return true;
         } else if (substringValue == 259 || substringValue == 261) {
            boolean substringFlag = substringValue == 261;
            String substring = IconsClearUtilsFormat.formatText(this.substring, this.substringActive, substringFlag);
            boolean substringFlag2 = !substring.equals(this.substring) || this.substringActive;
            this.substring = substring;
            this.substringActive = false;
            this.substringClearEdit.updateTime();
            return substringFlag2 ? true : !substringFlag;
         } else if (substringValue == 256) {
            this.substringActive = false;
            this.clearModulesFlag = false;
            return true;
         } else if (substringValue != 263 && substringValue != 262 && substringValue != 268 && substringValue != 269) {
            return false;
         } else {
            this.substringActive = false;
            return true;
         }
      } else {
         this.clearModulesFlag = true;
         this.substringClearEdit.updateTime();
         return true;
      }
   }

   public String getSubstring() {
      return this.substring;
   }

   public void update() {
      this.handleClearModules(false);
   }

   private static String formatRendererFontForStringFloat(String formatRendererFont, float rendererFontLimit) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      if (rendererFontLimit <= 0.0F) {
         return "";
      } else if (fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatRendererFont) <= rendererFontLimit) {
         return formatRendererFont;
      } else {
         String corzfontText = formatRendererFont;

         while (corzfontText.length() > 1 && fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) > rendererFontLimit) {
            corzfontText = corzfontText.substring(0, corzfontText.length() - 1);
         }

         return corzfontText;
      }
   }

   public void handleClearModules(boolean clearModulesFlag) {
      this.clearModulesFlag = clearModulesFlag;
      if (!clearModulesFlag) {
         this.substringActive = false;
      }
   }

   public void renderModulesClear(DrawContext context, int modulesClearLimit, int heightValue, float modulesClearValue) {
      this.lastNanoTime = System.nanoTime();
      this.modulesClearActive = modulesClearLimit >= this.modulesValue && modulesClearLimit <= this.modulesValue + this.boxWidth && heightValue >= this.boxY && heightValue <= this.boxY + this.modulesClearValue;
      handle(this.modulesClearQuintScissor, !this.clearModulesFlag && !this.modulesClearActive ? 0.0 : 1.0);
      handle(this.iconsQuintScissor, this.substring.isEmpty() ? 1.0 : 0.0);
      handle(this.hoverAnim, this.clearModulesFlag ? 0.0 : 1.0);
      double cardMatrixFactor = getAmount(this.modulesClearQuintScissor.getAmount());
      double modulesClearValue3 = 1.0 - getAmount(this.iconsQuintScissor.getAmount());
      double modulesClearLimit3 = getAmount(1.0 - modulesClearValue3 / 0.6);
      float iconsLimit = (float)getAmount((modulesClearValue3 - 0.4) / 0.6);
      double modulesClearLimit4 = getAmount(this.hoverAnim.getAmount());
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int matrixStackValue = CardMatrixUtils.getValue(getValue(16777215, 18), getValue(MatrixEspUtils.releaseRightValue, 255), cardMatrixFactor);
      MatrixPeekUtilsRender.render(context, this.modulesValue, this.boxY, this.boxWidth, this.modulesClearValue, this.modulesSearchValue, CardMatrixUtils.getValue7(), matrixStackValue);
      int modulesClearValue6 = this.boxY + (this.modulesClearValue - 12) / 2;
      int modulesClearValue7 = CardMatrixUtils.getValue(-8421505, -1, cardMatrixFactor);
      MatrixPeekUtilsIdentifier.handle(this.modulesValue + 8, modulesClearValue6, 12.0F, 12.0F, modulesClearIdentifier, new Color(modulesClearValue7, true), MatrixPeekUtilsRender.getMatrixStack());
      int modulesClearValue8 = this.modulesValue + this.boxWidth - 7;
      if (this.modulesSearchFlag) {
         int displaySize = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown();
         int modulesClearValue10 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("K");
         int valueForInt = IconsClearUtilsValue2.getValueForInt(displaySize);
         int modulesClearValue12 = IconsClearUtilsValue2.getValue(valueForInt);
         int materialFactor = modulesClearValue12 + 2 + modulesClearValue10 + 10;
         int boxHeight = this.modulesClearValue - 6;
         int matrixPeekValue = this.modulesValue + this.boxWidth - 7 - materialFactor;
         int brownValue = this.boxY + (this.modulesClearValue - boxHeight) / 2;
         if (modulesClearLimit3 > 0.01) {
            int modulesClearValue17 = getValue(-15461350, (int)(255.0 * modulesClearLimit3));
            int modulesClearValue18 = getValue(-1, (int)(24.0 * modulesClearLimit3));
            MatrixPeekUtilsRender.render(context, matrixPeekValue, brownValue, materialFactor, boxHeight, 5, modulesClearValue17, modulesClearValue18);
            int modulesClearValue19 = getValue(-7630437, (int)(255.0 * modulesClearLimit3));
            int abcdefghijklmnopqrstuvwxyzBrown = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, boxHeight);
            float modulesClearValue21 = abcdefghijklmnopqrstuvwxyzBrown + displaySize / 2.0F;
            int modulesClearValue22 = matrixPeekValue + 5;
            int round = Math.round(modulesClearValue22 - 0.203125F * valueForInt);
            int roundOffset = Math.round(modulesClearValue21 - 0.453125F * valueForInt) + 2;
            MatrixPeekUtilsIdentifier.handle(round, roundOffset, valueForInt, valueForInt, identifier, new Color(modulesClearValue19, true), MatrixPeekUtilsRender.getMatrixStack());
            fontRenderer.handle("K", modulesClearValue22 + modulesClearValue12 + 2, abcdefghijklmnopqrstuvwxyzBrown, new Color(modulesClearValue19, true));
         }

         modulesClearValue8 = matrixPeekValue - 6;
      }

      int iconsValue = this.modulesValue + this.boxWidth - 4 - 12;
      this.renderClearAmount(context, iconsValue, modulesClearLimit, heightValue, iconsLimit);
      int vertexTextureValue = this.modulesValue + 8 + 12 + 7;
      int f13 = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(this.boxY, this.modulesClearValue);
      float rendererFontLimit = (this.substring.isEmpty() ? modulesClearValue8 : iconsValue - 4) - vertexTextureValue;
      if (this.substring.isEmpty()) {
         if (modulesClearLimit4 > 0.01) {
            int modulesClearValue28 = getValue(-8421505, (int)Math.round(255.0 * modulesClearLimit4));
            fontRenderer.handleForStringFloat(formatRendererFontForStringFloat(this.modulesSearchText, rendererFontLimit), vertexTextureValue, f13, new Color(modulesClearValue28, true));
         }
      } else {
         String corzfontText = formatRendererFont(this.substring, rendererFontLimit);
         if (this.clearModulesFlag && this.substringActive) {
            int modulesClearValue29 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText);
            int n4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 2;
            MatrixPeekUtilsRender.renderMatrixStack(
               context, vertexTextureValue - 1, this.boxY + (this.modulesClearValue - n4) / 2, modulesClearValue29 + 2, n4, 2, CardMatrixUtils.getReleaseRight(0.32)
            );
         }

         fontRenderer.handleForStringFloat(corzfontText, vertexTextureValue, f13, new Color(-1));
         if (this.clearModulesFlag && !this.substringActive) {
            float modulesClearValue31 = vertexTextureValue + fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) + 1.0F;
            int modulesClearValue32 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() - 2;
            this.substringClearEdit
               .renderAmountTime(context, modulesClearValue31, this.boxY + (this.modulesClearValue - modulesClearValue32) / 2, modulesClearValue32, MatrixEspUtils.releaseRightValue, MatrixClearUtils.getAmount());
         }
      }

      if (this.clearModulesFlag && this.substring.isEmpty()) {
         int modulesClearValue33 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() - 2;
         this.substringClearEdit.renderAmountTime(context, vertexTextureValue, this.boxY + (this.modulesClearValue - modulesClearValue33) / 2, modulesClearValue33, MatrixEspUtils.releaseRightValue, MatrixClearUtils.getAmount());
      }
   }
}
