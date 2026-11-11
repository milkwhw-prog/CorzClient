package com.corz.client.render;

import com.corz.client.CorzClient;
import com.corz.client.config.FriendNameStore;
import com.corz.client.gui.ClearIcons;
import com.corz.client.gui.VertexGuiUtils;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;
import com.corz.client.categories.misc.NameProtect;
import com.corz.client.utils.IconsPngType;

public class HelloTime {
   private IconsPngType iconsPngType;
   private float currentX;
   private float outgoingStartOffsetY;
   private boolean resultsModuleActive;
   private float substringAmount;
   private IconsPngType helloPngIconsPngType;
   private float iconsCount;
   private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("HH:mm");
   private long nanoTimeSubstring;
   private long lastNanoTime;
   private String format;
   private long lastPatternTime;
   private float helloCount;
   private int helloValue;
   private final int baseY;
   private IconsPngType iconsPngTypeHelloPng;
   private int runningModuleValue;
   private boolean helloActive = true;
   private static final int UNUSED_ONE = 1;
   private float lineAlpha;
   private int helloValueHelloPng;
   private float helloAmount;
   private int resultsSearchValue;
   private boolean transitionStarted;
   private static boolean firstRender;
   private int iconsValue;
   private int helloPngValue = -1;
   private final HelloTime.Split substring;
   private static final MatrixStackUtilsAmount.Entrance ENTRANCE = new MatrixStackUtilsAmount.Entrance();
   private float outgoingAlpha;
   private long substringValue;
   private final HelloTime.Line substringLine;
   private boolean transitionInitialized;
   private static final String HELLO = "Hello, ";
   private float outgoingOffsetY;
   private long nanoTimeHelloPng;
   private static int hashCode;
   private boolean substringActive;
   private int lastRunningModuleValue;
   private int releaseValue;
   private int moduleResultsValue;
   private String helloText;
   private int lastModuleResultsValue;
   private long nanoTimeRelease;
   private final HelloTime.Split substringSplit;
   private static final String SEPARATOR = "  \u00b7  ";
   private boolean helloPngActive;
   private final HelloTime.Line helloLine = new HelloTime.Line();
   private float outgoingStartAlpha;
   private final ClearIcons clearIcons;
   private float patternAmount;

   private static void handleTextAmount(HelloTime.Line line, float helloValue, float abcdefghijklmnopqrstuvwxyzBrown, float helloLimit) {
      handle(line.substringText, helloValue, abcdefghijklmnopqrstuvwxyzBrown, line.substringTextColor, helloLimit);
      handle(line.lineText, helloValue + line.textWidth, abcdefghijklmnopqrstuvwxyzBrown, line.substringValue, helloLimit);
   }

   public void handleAmountIcons(int value, int value2) {
      if (value != this.iconsValue || value2 != this.releaseValue) {
         float iconsValue = value - this.iconsValue + (value2 - this.releaseValue) / 2.0F;
         this.iconsValue = value;
         this.releaseValue = value2;
         this.currentX += iconsValue;
         this.helloCount += iconsValue;
         this.iconsCount += iconsValue;
         this.clearIcons.handleMin(value + value2 - 150 - 8, this.baseY + 5, 150, 18);
      }
   }

   public void renderAmountTime(DrawContext context, int helloValue, int heightValue, float modulesClearValue) {
      if (this.helloActive) {
         VertexTextureFont fontRenderer = CorzClient.fontRenderer();
         long nanoTime = System.nanoTime();
         ENTRANCE.isTimeFlag();
         if (firstRender) {
            ENTRANCE.updateTimeFlag();
            firstRender = false;
         }

         RenderMatrixUtilsRelease.render(context, this.iconsValue, this.baseY, this.releaseValue, 28.0F, 7.0F, CardMatrixUtils.getValue4(), 234881023);
         int abcdefghijklmnopqrstuvwxyzBrown = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(this.baseY, 28);
         int helloValue5 = this.iconsValue + this.releaseValue - 150 - 8;
         float helloValue6 = this.iconsValue + 8;
         float y = this.baseY + 14.0F;
         this.handleFormatPattern(fontRenderer, nanoTime);
         this.handleHelloPng(fontRenderer, nanoTime, helloValue5 - 24 - this.patternAmount - helloValue6);
         this.handleAmountTime(nanoTime);
         this.handleAmountTime(nanoTime, MatrixStackUtilsAmount.getAmount((helloValue6 + this.helloLine.lineAmount + helloValue5) / 2.0F - this.patternAmount / 2.0F));
         ENTRANCE.getAmountFlag(0);
         if (ENTRANCE.entranceProgress > 0.002F) {
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, helloValue6 + this.helloLine.lineAmount / 2.0F, y, ENTRANCE.matrixScissorAmount);
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(ENTRANCE.entranceProgress);

            try {
               this.handleSubstringAmount(helloValue6, (float)abcdefghijklmnopqrstuvwxyzBrown);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            }
         }

         ENTRANCE.getAmountFlag(1);
         if (ENTRANCE.entranceProgress > 0.002F) {
            boolean canTranslatePush = MatrixStackUtilsAmount.canTranslatePush(context, this.currentX + this.patternAmount / 2.0F, y, ENTRANCE.matrixScissorAmount);
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(ENTRANCE.entranceProgress);

            try {
               this.handleSubstringAmount((float)abcdefghijklmnopqrstuvwxyzBrown, nanoTime);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               MatrixStackUtilsAmount.renderPopMatrices(context, canTranslatePush);
            }
         }

         ENTRANCE.getAmountFlag(2);
         if (ENTRANCE.entranceProgress > 0.002F) {
            boolean activeFlag = ENTRANCE.activeFlag;
            boolean matricesFlag = MatrixStackUtilsAmount.canTranslatePush(context, helloValue5 + 75.0F, y, ENTRANCE.matrixScissorAmount);
            float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(ENTRANCE.entranceProgress);

            try {
               this.clearIcons.renderModulesClear(context, activeFlag ? helloValue : -100000, heightValue, modulesClearValue);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
               MatrixStackUtilsAmount.renderPopMatrices(context, matricesFlag);
            }
         }

         this.handleTimeRelease(nanoTime);
      }
   }

   private void handleAmountTime(long helloValue) {
      float helloLimit = this.substringValue == 0L ? 1.0F : (float)(helloValue - this.substringValue) / 2.4E8F;
      if (helloLimit >= 1.0F) {
         this.substringValue = 0L;
         this.outgoingAlpha = 0.0F;
         this.outgoingOffsetY = 0.0F;
         this.lineAlpha = 1.0F;
         this.substringAmount = 0.0F;
      } else {
         float helloFactor = MatrixStackUtilsAmount.getMax(helloLimit / 0.45F);
         float amount = MatrixStackUtilsAmount.getAmountForFloat((helloLimit - 0.3F) / 0.7F);
         this.outgoingAlpha = this.outgoingStartAlpha * (1.0F - helloFactor);
         this.outgoingOffsetY = this.outgoingStartOffsetY + (-3.0F - this.outgoingStartOffsetY) * helloFactor;
         this.lineAlpha = amount;
         this.substringAmount = 3.0F * (1.0F - amount);
      }
   }

   public void handleResultsModule(IconsPngType iconsPngType, int moduleResultsValue, int runningModuleValue, boolean flag, int resultsSearchValue) {
      this.iconsPngTypeHelloPng = iconsPngType;
      this.moduleResultsValue = moduleResultsValue;
      this.runningModuleValue = runningModuleValue;
      this.resultsModuleActive = flag;
      this.resultsSearchValue = resultsSearchValue;
   }

   public HelloTime(int value, int value2, int value3) {
      this.substringLine = new HelloTime.Line();
      this.substringSplit = new HelloTime.Split();
      this.substring = new HelloTime.Split();
      this.lineAlpha = 1.0F;
      this.format = "";
      this.lastPatternTime = -1L;
      this.iconsValue = value;
      this.baseY = value2;
      this.releaseValue = value3;
      int helloValue = value + value3 - 150 - 8;
      int helloValue2 = value2 + 5;
      this.clearIcons = new ClearIcons(helloValue, helloValue2, 150, 18);
      int identityHashCode = System.identityHashCode(MinecraftClient.getInstance().currentScreen);
      boolean timeFlag = ENTRANCE.isTimeFlag();
      if (identityHashCode != hashCode || timeFlag) {
         hashCode = identityHashCode;
         firstRender = true;
      }
   }

   private void handleTimeRelease(long releaseValue) {
      float releaseValue2 = 0.0F;
      float releaseLimit = 0.0F;
      if (this.nanoTimeRelease != 0L) {
         float releaseFactor = (float)(releaseValue - this.nanoTimeRelease) / 4.5E8F;
         if (releaseFactor >= 1.0F) {
            this.nanoTimeRelease = 0L;
         } else {
            releaseValue2 = -56.0F + (this.releaseValue + 112.0F) * (1.0F - (1.0F - releaseFactor) * (1.0F - releaseFactor));
            releaseLimit = 1.0F - releaseFactor * releaseFactor * releaseFactor;
         }
      }

      int cardMatrixValue = 0xFF000000 | MatrixEspUtils.releaseRightValue;
      int configsLikeValue = this.baseY + 28 - 1;
      int releaseLimit2 = this.releaseValue * 2;
      VertexConsumer quads = VertexGuiUtils.getQuads();

      for (int i = 0; i < releaseLimit2; i++) {
         double helloLimit = (i + 0.5) / releaseLimit2;
         double min6 = 0.9 * getAmount(helloLimit, 0.35);
         int cardMatrixValue7 = cardMatrixValue;
         if (releaseLimit > 0.0F) {
            double releaseFactor2 = ((i + 0.5) * 0.5 - releaseValue2) / 56.0;
            if (releaseFactor2 > -1.0 && releaseFactor2 < 1.0) {
               double releaseValue8 = (1.0 - releaseFactor2 * releaseFactor2) * (1.0 - releaseFactor2 * releaseFactor2) * releaseLimit;
               min6 = Math.min(1.0, min6 + 0.55 * releaseValue8 * getAmount(helloLimit, 0.12));
               cardMatrixValue7 = CardMatrixUtils.getValue(cardMatrixValue, -1, 0.55 * releaseValue8);
            }
         }

         int panelOverlay = CorzRoundedUtils.getPanelOverlay((int)Math.round(min6 * 255.0) << 24 | cardMatrixValue7 & 16777215);
         if (panelOverlay >>> 24 != 0) {
            VertexGuiUtils.handleVertexColor(quads, this.iconsValue + i * 0.5F, configsLikeValue, this.iconsValue + (i + 1) * 0.5F, configsLikeValue + 1, panelOverlay);
         }
      }

      VertexGuiUtils.updateBufferClose();
   }

   public boolean canIconsClear(int iconsClearValue, int scancode, int modifiers) {
      return this.clearIcons.canClearSubstring(iconsClearValue, scancode, modifiers);
   }

   public boolean canIconsClear(double iconsClearValue, double heightAmount) {
      return this.clearIcons.canMin(iconsClearValue, heightAmount);
   }

   private static void handle(String helloText, float helloValue, float abcdefghijklmnopqrstuvwxyzBrown, int matrixStackValue, float helloLimit) {
      if (!(helloLimit <= 0.004F) && !helloText.isEmpty()) {
         MatrixStackUtilsAmount.handle(helloText, helloValue, abcdefghijklmnopqrstuvwxyzBrown, helloLimit >= 1.0F ? matrixStackValue : MatrixStackUtilsAmount.getValue(matrixStackValue, helloLimit));
      }
   }

   public boolean canIconsClear(double iconsClearValue, double heightAmount, int modulesValue) {
      return this.clearIcons.canClearModules(iconsClearValue, heightAmount, modulesValue);
   }

   public String formatIconsClear() {
      return this.clearIcons.getSubstring();
   }

   private void handleFormatPattern(VertexTextureFont vertexTextureFont, long nanoTime) {
      long time = System.currentTimeMillis() / 60000L;
      if (time != this.lastPatternTime) {
         this.lastPatternTime = time;
         String format = LocalTime.now().format(pattern);
         if (!format.equals(this.format)) {
            if (!this.format.isEmpty()) {
               this.substring.handleSubstringAmount(vertexTextureFont, this.format, format);
               this.nanoTimeSubstring = nanoTime;
            }

            this.format = format;
            this.patternAmount = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(format);
         }
      }
   }

   private void handleAmountTime(long nanoTime, float amount) {
      if (!this.transitionStarted) {
         this.transitionStarted = true;
         this.currentX = this.iconsCount = amount;
      } else {
         if (Math.abs(amount - this.iconsCount) > 0.01F) {
            this.helloCount = this.currentX;
            this.iconsCount = amount;
            this.lastNanoTime = nanoTime;
         }

         if (this.lastNanoTime != 0L) {
            float helloLimit = (float)(nanoTime - this.lastNanoTime) / 2.6E8F;
            if (helloLimit >= 1.0F) {
               this.lastNanoTime = 0L;
               this.currentX = this.iconsCount;
            } else {
               float helloFactor = this.iconsCount < this.helloCount ? MatrixStackUtilsAmount.getAmount3(helloLimit) : MatrixStackUtilsAmount.getAmountForFloat(helloLimit);
               this.currentX = this.helloCount + (this.iconsCount - this.helloCount) * helloFactor;
            }
         }
      }
   }

   public void setFlagForFlag(boolean flag) {
      this.helloActive = flag;
   }

   private void handleSubstringAmount(float substringValue, float abcdefghijklmnopqrstuvwxyzBrown) {
      if (this.substringValue == 0L) {
         handleTextAmount(this.helloLine, substringValue, abcdefghijklmnopqrstuvwxyzBrown, 1.0F);
      } else if (this.substringActive) {
         float substringValue3 = substringValue + this.helloLine.textWidth;
         handle(this.helloLine.substringText, substringValue, abcdefghijklmnopqrstuvwxyzBrown, this.helloLine.substringTextColor, 1.0F);
         handle(this.substringSplit.leadingSubstring, substringValue3, abcdefghijklmnopqrstuvwxyzBrown, this.helloLine.substringValue, 1.0F);
         handle(this.substringSplit.substring, substringValue3 + this.substringSplit.substringAmount, abcdefghijklmnopqrstuvwxyzBrown + this.outgoingOffsetY, this.helloLine.substringValue, this.outgoingAlpha);
         handle(this.substringSplit.substringText, substringValue3 + this.substringSplit.substringWidthDelta, abcdefghijklmnopqrstuvwxyzBrown + this.substringAmount, this.helloLine.substringValue, this.lineAlpha);
      } else {
         handleTextAmount(this.substringLine, substringValue, abcdefghijklmnopqrstuvwxyzBrown + this.outgoingOffsetY, this.outgoingAlpha);
         handleTextAmount(this.helloLine, substringValue, abcdefghijklmnopqrstuvwxyzBrown + this.substringAmount, this.lineAlpha);
      }
   }

   private void handleSubstringAmount(float substringValue, long nanoTime) {
      int matrixStackValue = 0xFF000000 | MatrixEspUtils.releaseRightValue;
      if (this.nanoTimeSubstring != 0L) {
         float substringValue4 = (float)(nanoTime - this.nanoTimeSubstring) / 2.6E8F;
         if (substringValue4 < 1.0F) {
            float max5 = MatrixStackUtilsAmount.getMax(substringValue4 / 0.45F);
            float amountForFloat = MatrixStackUtilsAmount.getAmountForFloat((substringValue4 - 0.3F) / 0.7F);
            handle(this.substring.leadingSubstring, this.currentX, substringValue, matrixStackValue, 1.0F);
            handle(this.substring.substring, this.currentX + this.substring.substringAmount, substringValue - 2.0F * max5, matrixStackValue, 1.0F - max5);
            handle(this.substring.substringText, this.currentX + this.substring.substringWidthDelta, substringValue + 2.0F * (1.0F - amountForFloat), matrixStackValue, amountForFloat);
            return;
         }

         this.nanoTimeSubstring = 0L;
      }

      handle(this.format, this.currentX, substringValue, matrixStackValue, 1.0F);
   }

   private static String formatText(int helloValue, String helloText) {
      return helloValue == 1 ? "1 " + helloText : helloValue + " " + helloText + "s";
   }

   private void handleHelloPng(VertexTextureFont vertexTextureFont, long nanoTime, float helloAmount) {
      if (this.iconsPngTypeHelloPng != this.iconsPngType) {
         if (this.iconsPngType != null) {
            this.nanoTimeHelloPng = nanoTime + 2600000000L;
            this.nanoTimeRelease = nanoTime;
         }

         this.iconsPngType = this.iconsPngTypeHelloPng;
      }

      if (this.resultsModuleActive && !this.helloPngActive) {
         this.nanoTimeRelease = nanoTime;
         this.nanoTimeHelloPng = nanoTime;
      }

      this.helloPngActive = this.resultsModuleActive;
      int value = this.resultsModuleActive ? 2 : (this.iconsPngTypeHelloPng != null && nanoTime < this.nanoTimeHelloPng ? 1 : 0);
      String helloText = value == 0 ? NameProtect.getString(MinecraftClient.getInstance().getSession().getUsername()) : null;
      int helloValue = value == 1 && this.iconsPngTypeHelloPng == IconsPngType.SOCIALS ? FriendNameStore.getFriendCount() : 0;
      if (value != this.helloPngValue
         || this.iconsPngTypeHelloPng != this.helloPngIconsPngType
         || this.moduleResultsValue != this.lastModuleResultsValue
         || this.runningModuleValue != this.lastRunningModuleValue
         || this.resultsSearchValue != this.helloValue
         || helloValue != this.helloValueHelloPng
         || helloAmount != this.helloAmount
         || !Objects.equals(helloText, this.helloText)) {
         this.helloPngValue = value;
         this.helloPngIconsPngType = this.iconsPngTypeHelloPng;
         this.lastModuleResultsValue = this.moduleResultsValue;
         this.lastRunningModuleValue = this.runningModuleValue;
         this.helloValue = this.resultsSearchValue;
         this.helloValueHelloPng = helloValue;
         this.helloAmount = helloAmount;
         this.helloText = helloText;
         if (value == 0) {
            this.handleAmountFlag(vertexTextureFont, "Hello, ", helloText == null ? "" : helloText, -7630437, -920587, helloAmount, nanoTime);
         } else if (value == 2) {
            boolean helloPngFlag = this.resultsSearchValue > 0;
            this.handleAmountFlag(
               vertexTextureFont, helloPngFlag ? "Search" : "No results", helloPngFlag ? "  \u00b7  " + formatText(this.resultsSearchValue, "result") : "", -920587, -7630437, helloAmount, nanoTime
            );
         } else {
            String helloPngText = "";
            if (this.moduleResultsValue >= 0) {
               boolean helloPngFlag2 = this.iconsPngTypeHelloPng == IconsPngType.BOTS;
               helloPngText = "  \u00b7  " + formatText(this.moduleResultsValue, helloPngFlag2 ? "bot" : "module");
               if (this.runningModuleValue > 0) {
                  helloPngText = helloPngText + "  \u00b7  " + this.runningModuleValue + (helloPngFlag2 ? " running" : " on");
               }
            } else if (helloValue > 0) {
               helloPngText = "  \u00b7  " + formatText(helloValue, "friend");
            }

            this.handleAmountFlag(vertexTextureFont, this.iconsPngTypeHelloPng.getText(), helloPngText, -920587, -7630437, helloAmount, nanoTime);
         }
      }
   }

   public boolean canIconsClear(char ch, int iconsClearValue) {
      return this.clearIcons.canClearSubstring(ch, iconsClearValue);
   }

   private static double getAmount(double helloLimit, double helloDivisor) {
      double helloFactor = helloLimit < helloDivisor ? helloLimit / helloDivisor : (helloLimit > 1.0 - helloDivisor ? (1.0 - helloLimit) / helloDivisor : 1.0);
      return helloFactor * helloFactor * (3.0 - 2.0 * helloFactor);
   }

   public ClearIcons getClearIcons() {
      return this.clearIcons;
   }

   private void handleAmountFlag(VertexTextureFont vertexTextureFont, String helloText, String helloPngText, int helloValue, int helloValue2, float vertexTextureLimit, long nanoTime) {
      float helloLimit = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(helloText);
      if (helloLimit > vertexTextureLimit) {
         helloText = vertexTextureFont.formatText(helloText, vertexTextureLimit);
         helloPngText = "";
      } else if (!helloPngText.isEmpty()) {
         helloPngText = vertexTextureFont.formatText(helloPngText, vertexTextureLimit - helloLimit);
      }

      if (!this.transitionInitialized) {
         this.helloLine.handleAmountText(vertexTextureFont, helloText, helloPngText, helloValue, helloValue2);
         this.transitionInitialized = true;
      } else if (!this.helloLine.canText(helloText, helloPngText, helloValue, helloValue2)) {
         boolean flag = this.helloLine.substringTextColor == helloValue && this.helloLine.substringValue == helloValue2 && this.helloLine.substringText.equals(helloText);
         this.handleAmountTime(nanoTime);
         if (this.substringValue == 0L) {
            this.substringLine.handleTextAmount(this.helloLine);
            this.outgoingStartAlpha = 1.0F;
            this.outgoingStartOffsetY = 0.0F;
            this.substringActive = flag;
         } else {
            if (!this.substringActive && flag) {
               this.helloLine.handleAmountText(vertexTextureFont, helloText, helloPngText, helloValue, helloValue2);
               return;
            }

            if (this.substringActive && !flag) {
               this.substringLine.handleTextAmount(this.helloLine);
               this.outgoingStartAlpha = 1.0F;
               this.outgoingStartOffsetY = 0.0F;
               this.substringActive = false;
            } else if (this.lineAlpha >= this.outgoingAlpha) {
               this.substringLine.handleTextAmount(this.helloLine);
               this.outgoingStartAlpha = this.lineAlpha;
               this.outgoingStartOffsetY = this.substringAmount;
            } else {
               this.outgoingStartAlpha = this.outgoingAlpha;
               this.outgoingStartOffsetY = this.outgoingOffsetY;
            }
         }

         this.helloLine.handleAmountText(vertexTextureFont, helloText, helloPngText, helloValue, helloValue2);
         if (this.substringActive) {
            this.substringSplit.handleSubstringAmount(vertexTextureFont, this.substringLine.lineText, this.helloLine.lineText);
         }

         this.substringValue = nanoTime;
         this.handleAmountTime(nanoTime);
      }
   }

   private static final class Line {
      String lineText;
      String substringText = "";
      float textWidth;
      float lineAmount;
      int substringTextColor;
      int substringValue;

      void handleTextAmount(HelloTime.Line line) {
         this.substringText = line.substringText;
         this.lineText = line.lineText;
         this.substringTextColor = line.substringTextColor;
         this.substringValue = line.substringValue;
         this.textWidth = line.textWidth;
         this.lineAmount = line.lineAmount;
      }

      boolean canText(String lineText, String helloPngText, int lineValue, int lineValue2) {
         return this.substringTextColor == lineValue && this.substringValue == lineValue2 && this.substringText.equals(lineText) && this.lineText.equals(helloPngText);
      }

      private Line() {
         this.lineText = "";
      }

      void handleAmountText(VertexTextureFont vertexTextureFont, String text, String helloPngText, int value, int value2) {
         this.substringText = text;
         this.lineText = helloPngText;
         this.substringTextColor = value;
         this.substringValue = value2;
         this.textWidth = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(text);
         this.lineAmount = this.textWidth + (helloPngText.isEmpty() ? 0.0F : vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(helloPngText));
      }
   }

   private static final class Split {
      String substring;
      float substringWidthDelta;
      String substringText;
      String leadingSubstring = "";
      float substringAmount;

      private Split() {
         this.substring = "";
         this.substringText = "";
      }

      void handleSubstringAmount(VertexTextureFont vertexTextureFont, String substringText, String corzfontText) {
         int index = 0;
         int substringLimit = Math.min(substringText.length(), corzfontText.length());

         while (index < substringLimit && substringText.charAt(index) == corzfontText.charAt(index)) {
            index++;
         }

         this.leadingSubstring = corzfontText.substring(0, index);
         this.substring = substringText.substring(index);
         this.substringText = corzfontText.substring(index);
         this.substringAmount = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(substringText) - vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.substring);
         this.substringWidthDelta = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) - vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.substringText);
      }
   }
}
