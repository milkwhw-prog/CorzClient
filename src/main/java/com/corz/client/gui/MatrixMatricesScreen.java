package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.config.SidebarPanel;
import com.corz.client.render.BetaListening;
import com.corz.client.render.BindPill;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.DynMappableUtils;
import com.corz.client.render.HalloweenMatrixUtils;
import com.corz.client.render.HelloTime;
import com.corz.client.render.KawaseGpu;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixIdentifierUtils;
import com.corz.client.render.RenderMatrixUtilsAmount;
import com.corz.client.render.RenderMatrixUtilsRelease;
import com.corz.client.render.SnowVertexUtils;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import com.corz.client.categories.theme.ThemeCustomizer;
import ai.auth.Module;
import com.corz.client.utils.AutoYourUtils;
import com.corz.client.utils.ConstAmountUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.MinecraftBotUtils;
import com.corz.client.utils.PngIcons;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;



public class MatrixMatricesScreen extends Screen implements ScreenGuiApi, GuiClickApi {
   private double animatedScrollOffset;
   private String lowerCase;
   private final MatrixStack matrixStack;
   private int mouseScrolledCount;
   private double scrollbarFadeProgress;
   private int cachedWidth;
   private static int peakSampleCount;
   private static long peakSampleNanos;
   private int clientCorzValue;
   private BindPill bindPill;
   private static boolean matrixMatricesActive = false;
   private boolean panelHovered;
   private final QuintScissor matrixMatricesQuintScissor;
   private static int samplerCount;
   private static long matrixMatricesCount;
   private final MatrixCorz matrixCorz;
   private long nanoTimeTgtBot;
   private Module next;
   private float matrixMatricesAmount;
   private HelloTime helloTime;
   private SidebarPanel logoProfile;
   private static boolean introTriggered = false;
   private int cachedHeight;
   private static int samplerAccumulatedMs;
   private float smoothedScrollPx;
   private ResetLock resetLock;
   private boolean corzMatrixActive;
   private static boolean firstRenderDone;
   private static KawaseGpu kawaseGpu;
   private boolean mouseClickedActive;
   private PngIcons instance;
   private boolean pendingRelayout;
   private BotTgt botTgt;
   private String matrixMatricesText;
   private final List<BetaListening> lowerCaseBetaListeningList;
   private boolean clientCorzFlag = false;
   private final Map<IconsPngType, List<BetaListening>> clientCorzMap = new HashMap<>();
   private long tgtBotValue;
   private double mouseClickedAmount;
   private float contentOffsetY;
   private static long timeMillis;

   private boolean isCorzMatrix() {
      return this.corzMatrixActive || this.matrixCorz.isFlag() || RenderMatrixUtilsAmount.isFlag2();
   }

   @Override
   public boolean mouseClicked(Click click, boolean mouseClickedFlag) {
      if (!this.corzMatrixActive && !this.matrixCorz.isFlag() && RenderMatrixUtilsAmount.isFlag()) {
         return true;
      } else if (this.isCorzMatrix()) {
         return true;
      } else {
         double widthAmount = this.getWidthAmount(click.x());
         double heightAmount = this.getHeightAmount(click.y());
         int button = click.button();
         if (!this.isPillLock() && this.helloTime.canIconsClear(widthAmount, heightAmount)) {
            for (BetaListening betaListening : this.getTgtBot()) {
               betaListening.updateKeybindScissorSettingCompone();
            }

            if (this.bindPill != null) {
               this.bindPill.updateListenIcons2();
            }

            this.helloTime.canIconsClear(widthAmount, heightAmount, button);
            return true;
         } else {
            for (BetaListening getTgtBotItem : this.getTgtBot()) {
               if (getTgtBotItem.isListeningGlfw()) {
                  if (button >= 0 && button <= 7) {
                     getTgtBotItem.handleListeningGlfwForInt(button);
                  } else {
                     getTgtBotItem.updateListeningGlfw();
                  }

                  return true;
               }
            }

            if (this.isPillBind() && this.bindPill.isListen()) {
               if (button >= 0 && button <= 7) {
                  this.bindPill.handlePillBind(button);
               } else {
                  this.bindPill.updateListen();
               }

               return true;
            } else if (ClampMatrixUtils.canButtonClamp(button)) {
               return true;
            } else {
               if (this.bindPill != null && (!this.isPillBind() || !this.canClientCorz(widthAmount, heightAmount))) {
                  this.bindPill.updateListenIcons2();
               }

               int[] clientCorz = this.getClientCorz();
               if (clientCorz != null && button == 0) {
                  int mouseClickedValue = clientCorz[0];
                  int mouseClickedValue2 = clientCorz[1];
                  int mouseClickedValue3 = clientCorz[2];
                  int mouseClickedValue4 = clientCorz[3];
                  int mouseClickedValue5 = clientCorz[4];
                  int mouseClickedValue6 = clientCorz[5];
                  if (widthAmount > mouseClickedValue - 1 && widthAmount <= mouseClickedValue + mouseClickedValue4 + 2 && heightAmount >= mouseClickedValue2 && heightAmount <= mouseClickedValue2 + mouseClickedValue3) {
                     if (heightAmount >= mouseClickedValue5 && heightAmount <= mouseClickedValue5 + mouseClickedValue6) {
                        this.mouseClickedActive = true;
                        this.mouseClickedAmount = heightAmount - mouseClickedValue5;
                     } else {
                        this.mouseClickedAmount = mouseClickedValue6 / 2.0;
                        this.handleMax(heightAmount - this.mouseClickedAmount, mouseClickedValue2, mouseClickedValue3, mouseClickedValue6);
                        this.animatedScrollOffset = this.mouseScrolledCount;
                        this.mouseClickedActive = true;
                     }

                     return true;
                  }
               }

               BetaListening next = this.getNext();
               if (next != null) {
                  next.canFlagListening(widthAmount, heightAmount, button);
                  return true;
               } else if (RenderMatrixUtilsAmount.canPipelineFlag(widthAmount, heightAmount, button)) {
                  return true;
               } else if (this.logoProfile.canConsumeClick(widthAmount, heightAmount, button)) {
                  return true;
               } else if (this.helloTime.canIconsClear(widthAmount, heightAmount, button)) {
                  return true;
               } else if (!this.canClientCorz(widthAmount, heightAmount)) {
                  return false;
               } else if (this.isLockTheme()) {
                  return this.resetLock.canAmount(widthAmount, heightAmount + this.contentOffsetY, button);
               } else if (this.isPillBind()) {
                  return this.bindPill.canIconsClear(widthAmount, heightAmount + this.contentOffsetY, button);
               } else if (this.isTgtBots()) {
                  return this.botTgt.canMatrixScissor(widthAmount, heightAmount, button);
               } else {
                  long nanoTime = System.nanoTime();

                  for (BetaListening betaListening3 : this.getTgtBot()) {
                     if ((betaListening3.getNanoTimeNano() == 0L || !betaListening3.isBetaBadgeVisible() || !((nanoTime - betaListening3.getNanoTimeNano()) / 1000000.0 < 75.0))
                        && betaListening3.canFlagListening(widthAmount, heightAmount, button)) {
                        if (betaListening3.isListeningPending()) {
                           this.handleClientCorz(betaListening3);
                        }

                        this.instance.handle(betaListening3.getNext().getCategory(), betaListening3.isListeningPending());
                        this.updateMax();
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      }
   }

   @Override
   public boolean keyPressed(KeyInput keyInput) {
      if (!this.corzMatrixActive && !this.matrixCorz.isFlag()) {
         int pressedValue = RenderMatrixUtilsAmount.getValue(keyInput.key());
         if (pressedValue == 2) {
            this.close();
            return true;
         }

         if (pressedValue == 1) {
            return true;
         }
      }

      if (this.isCorzMatrix()) {
         return true;
      } else {
         int iconsClearValue = keyInput.key();
         int scancode = keyInput.scancode();
         int modifiers = keyInput.modifiers();
         boolean clearModulesFlag = this.helloTime.getClearIcons().isClearModulesFlag();
         if (clearModulesFlag && this.helloTime.canIconsClear(iconsClearValue, scancode, modifiers)) {
            return true;
         } else {
            BetaListening next = this.getNext();
            if (next != null) {
               next.canAnywayFlag(iconsClearValue, scancode, modifiers);
               return true;
            } else if (this.helloTime.canIconsClear(iconsClearValue, scancode, modifiers)) {
               if (this.bindPill != null) {
                  this.bindPill.updateListenIcons2();
               }

               return true;
            } else if (!clearModulesFlag && this.isPillBind() && this.bindPill.isListen()) {
               this.bindPill.handlePillBindForInt(iconsClearValue);
               return true;
            } else if (this.isPillBind() && this.bindPill.canIconsClear(iconsClearValue, scancode, modifiers)) {
               return true;
            } else {
               for (BetaListening betaListening : this.getTgtBot()) {
                  if (!clearModulesFlag && betaListening.isListeningGlfw()) {
                     betaListening.handleListeningGlfw(iconsClearValue);
                     return true;
                  }

                  if (betaListening.canAnywayFlag(iconsClearValue, scancode, modifiers)) {
                     return true;
                  }
               }

               if (this.isTgtBots() && !clearModulesFlag && this.botTgt.canMaxScissor(iconsClearValue, modifiers)) {
                  return true;
               } else if (iconsClearValue == 256) {
                  this.close();
                  return true;
               } else {
                  return false;
               }
            }
         }
      }
   }

   private void renderClientCorz(DrawContext context, int clientCorzValue, int randomValue, float peekFactor) {
      int peekValue = clientCorzValue + this.clientCorzValue + 12;
      int peekBound = 620 - this.clientCorzValue - 18;
      int matrixStackValue = randomValue + 6;
      short materialFactor = 348;
      TwoDidUtils.renderMinPeek(context, 0, peekValue, matrixStackValue, peekBound, materialFactor, 7.0F, peekFactor);
   }

   private static float getAmount(float matrixMatricesLimit) {
      return matrixMatricesLimit < 0.0F ? 0.0F : (matrixMatricesLimit > 1.0F ? 1.0F : matrixMatricesLimit);
   }

   @Override
   public void removed() {
      super.removed();
      if (this.botTgt != null) {
         this.botTgt.updateRenderMatrix();
      }

      RenderMatrixUtilsAmount.updateRenderMatrix();
      if (!this.corzMatrixActive) {
         ClampMatrixUtils.updateFramePanels();
      }
   }

   private boolean isLockTheme() {
      return !this.isLowerCase() && this.resetLock != null && this.instance.getIconsPngType() == IconsPngType.THEME;
   }

   public void updateFlag() {
      this.pendingRelayout = true;
   }

   @Override
   public void renderBackground(DrawContext context, int backgroundValue, int backgroundValue2, float backgroundValue3) {
   }

   private void updateMax() {
      this.handleClientCorz(BetaListening.getAmount(this.animatedScrollOffset));
   }

   private static float getAmountForFloat(float matrixMatricesValue) {
      matrixMatricesValue = getAmount(matrixMatricesValue);
      float matrixMatricesFactor = 1.0F - matrixMatricesValue;
      return 1.0F - matrixMatricesFactor * matrixMatricesFactor * matrixMatricesFactor;
   }

   private double getHeightAmount(double heightValue) {
      return super.height / 2.0 + (heightValue - super.height / 2.0) / this.matrixMatricesAmount;
   }

   private int getValue() {
      return 316;
   }

   private boolean isTgtBots() {
      return !this.isLowerCase() && this.botTgt != null && this.instance.getIconsPngType() == IconsPngType.BOTS;
   }

   private void updateTgtBot() {
      long nanoTime = System.nanoTime();
      this.nanoTimeTgtBot = nanoTime;
      this.tgtBotValue = nanoTime;
      if (this.isTgtBots()) {
         this.botTgt.updateTimeMax();
      } else {
         boolean tgtBotFlag = this.isFlag();
         int tgtBotCounter = 0;

         for (BetaListening betaListening : this.getTgtBot()) {
            if (betaListening.isKeybindBindActive()) {
               double tgtBotValue = tgtBotFlag ? 0.0 : Math.min(tgtBotCounter, 16) * 26.0;
               tgtBotCounter++;
               betaListening.handleTimeFlag(nanoTime + (long)(tgtBotValue * 1000000.0), true);
            }
         }
      }
   }

   private void updateResolutionChanged() {
      super.close();
      Module.mc.onResolutionChanged();
   }

   private int[] getAmountMax(int matrixMatricesValue, int matrixMatricesValue2, int constValue, int matrixMatricesValue4) {
      int matrixMatricesDivisor = this.getMax();
      if (matrixMatricesDivisor <= 0) {
         return null;
      } else {
         int pillLock = this.getPillLock();
         byte matrixMatricesValue5 = 3;
         int matrixMatricesValue6 = matrixMatricesValue + constValue + 1;
         int matrixMatricesValue7 = matrixMatricesValue2 + 6;
         int matrixMatricesFactor = matrixMatricesValue4 - 12;
         int max8 = Math.max(24, (int)((double)matrixMatricesValue4 / pillLock * matrixMatricesFactor));
         double max2 = Math.max(0.0, Math.min(1.0, this.animatedScrollOffset / matrixMatricesDivisor));
         this.smoothedScrollPx = (float)(matrixMatricesValue7 + max2 * (matrixMatricesFactor - max8));
         int round = Math.round(this.smoothedScrollPx);
         return new int[]{matrixMatricesValue6, matrixMatricesValue7, matrixMatricesFactor, matrixMatricesValue5, round, max8};
      }
   }

   @Override
   public boolean charTyped(CharInput charInput) {
      RenderMatrixUtilsAmount.update();
      if (this.isCorzMatrix()) {
         return true;
      } else {
         char codepoint = (char)charInput.codepoint();
         int modifiers = charInput.modifiers();
         if (this.helloTime.getClearIcons().isClearModulesFlag() && this.helloTime.canIconsClear(codepoint, modifiers)) {
            return true;
         } else {
            BetaListening next = this.getNext();
            if (next != null) {
               next.canFlag(codepoint, modifiers);
               return true;
            } else if (this.helloTime.canIconsClear(codepoint, modifiers)) {
               return true;
            } else if (this.isPillBind() && this.bindPill.canIconsClear(codepoint, modifiers)) {
               return true;
            } else {
               for (BetaListening betaListening : this.getTgtBot()) {
                  if (betaListening.canFlag(codepoint, modifiers)) {
                     return true;
                  }
               }

               return false;
            }
         }
      }
   }

   private static boolean canSocialsTheme(IconsPngType iconsPngType) {
      return iconsPngType == IconsPngType.SETTING || iconsPngType == IconsPngType.CONFIGS || iconsPngType == IconsPngType.THEME || iconsPngType == IconsPngType.SOCIALS;
   }

   @Override
   public void close() {
      if (!this.corzMatrixActive) {
         this.corzMatrixActive = true;
         matrixMatricesActive = true;
         this.mouseClickedActive = false;
         this.matrixMatricesQuintScissor.setValue(ClampMatrixUtils.isFramePanelsFramePanelsActive() ? 380 : 240);
         this.matrixMatricesQuintScissor.handleAmountTime(0.0);
      }
   }

   private void renderAmountMax(DrawContext context, int matrixMatricesValue, int matrixMatricesValue2, int constValue, int matrixMatricesValue4, int matrixMatricesLimit, int heightValue) {
      int[] amountMax = this.getAmountMax(matrixMatricesValue, matrixMatricesValue2, constValue, matrixMatricesValue4);
      if (amountMax == null) {
         this.scrollbarFadeProgress = 0.0;
      } else {
         int matrixMatricesValue5 = amountMax[0];
         int matrixMatricesValue6 = amountMax[1];
         int matrixMatricesValue7 = amountMax[2];
         int matrixMatricesValue8 = amountMax[3];
         int matrixMatricesValue9 = amountMax[4];
         int materialFactor = amountMax[5];
         boolean matrixMatricesFlag = matrixMatricesLimit >= matrixMatricesValue + constValue - 2 && matrixMatricesLimit <= matrixMatricesValue + constValue + 6 && heightValue >= matrixMatricesValue2 && heightValue <= matrixMatricesValue2 + matrixMatricesValue4;
         boolean matrixMatricesFlag2 = this.mouseClickedActive || matrixMatricesLimit > matrixMatricesValue5 - 1 && matrixMatricesLimit <= matrixMatricesValue5 + matrixMatricesValue8 + 2 && heightValue >= matrixMatricesValue9 && heightValue <= matrixMatricesValue9 + materialFactor;
         long nanoTime = System.nanoTime();
         if (this.mouseClickedActive || matrixMatricesFlag || Math.abs(this.animatedScrollOffset - this.mouseScrolledCount) > 0.5) {
            this.tgtBotValue = nanoTime;
         }

         boolean matrixMatricesFlag3 = nanoTime - this.tgtBotValue < 900000000L;
         this.scrollbarFadeProgress = MatrixIdentifierUtils.getAmount(this.scrollbarFadeProgress, matrixMatricesFlag3 ? 1.0 : 0.0, matrixMatricesFlag3 ? 18.0 : 5.0, MatrixClearUtils.getAmount());
         if (!(this.scrollbarFadeProgress < 0.01)) {
            int matrixStackValue = matrixMatricesFlag2 ? CardMatrixUtils.getReleaseRight(0.75) : 872415231;
            float configsLikeValue = (float)BetaListening.getAmount(this.smoothedScrollPx);
            float panelOverlay = CorzRoundedUtils.getPanelOverlay((float)this.scrollbarFadeProgress);

            try {
               MatrixPeekUtilsRender.renderMatrixPeek(context, matrixMatricesValue5 + 1, configsLikeValue, matrixMatricesValue8 - 1, materialFactor, (matrixMatricesValue8 - 1) / 2.0F, matrixStackValue);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   private int getMax() {
      return Math.max(0, this.getPillLock() - this.getValue());
   }

   private int[] getClientCorz() {
      int clientCorzValue = (super.height - 360) / 2;
      int clientCorzValue2 = (super.width - 620) / 2;
      int matrixMatricesValue = clientCorzValue2 + this.clientCorzValue + 12;
      int clientCorzValue4 = clientCorzValue + 6 + 28 + 4;
      int constValue = 620 - this.clientCorzValue - 18;
      int clientCorzValue6 = this.getValue();
      return this.getAmountMax(matrixMatricesValue, clientCorzValue4, constValue, clientCorzValue6);
   }

   @Override
   public boolean mouseReleased(Click click) {
      RenderMatrixUtilsAmount.update();
      if (ClampMatrixUtils.canPanelsButton(click.button())) {
         return true;
      } else if (this.isCorzMatrix()) {
         return true;
      } else if (this.mouseClickedActive) {
         this.mouseClickedActive = false;
         return true;
      } else {
         double widthAmount = this.getWidthAmount(click.x());
         double heightAmount = this.getHeightAmount(click.y());
         int button = click.button();
         if (this.isTgtBots()) {
            this.botTgt.handleRenderMatrixForDoubleInt(widthAmount, heightAmount, button);
            return true;
         } else if (this.isLockTheme()) {
            this.resetLock.handleScissorMusic(widthAmount, heightAmount + this.contentOffsetY, button);
            return true;
         } else if (this.isPillBind()) {
            this.bindPill.handleForDoubleInt(widthAmount, heightAmount + this.contentOffsetY, button);
            return true;
         } else {
            double mouseReleasedValue = heightAmount;

            for (BetaListening betaListening : this.getTgtBot()) {
               betaListening.handleFlagForDoubleInt(widthAmount, mouseReleasedValue, button);
            }

            return this.isPillLock();
         }
      }
   }

   private boolean isFlag() {
      return !this.isLowerCase() && canSocialsTheme(this.instance.getIconsPngType());
   }

   private int getPillLock() {
      if (this.isLockTheme()) {
         return 0 + this.resetLock.getValue();
      } else if (this.isPillBind()) {
         return 0 + this.bindPill.getTabsPanelWidth();
      } else if (this.isTgtBots()) {
         return this.getValue();
      } else {
         var tgtBot = this.getTgtBot();
         if (tgtBot.isEmpty()) {
            return 0;
         } else {
            boolean pillLockFlag = this.isFlag();
            double pillLockTotal = 0.0;
            double apiPutBound = 0.0;
            int pillLockCounter = 0;

            for (BetaListening betaListening : tgtBot) {
               if (betaListening.isKeybindBindActive()) {
                  double amountFlagOffset = betaListening.getAmountFlagListeningActive() + 4.0;
                  if (pillLockFlag) {
                     pillLockTotal += amountFlagOffset;
                  } else if (pillLockCounter % 2 == 0) {
                     pillLockTotal += amountFlagOffset;
                  } else {
                     apiPutBound += amountFlagOffset;
                  }

                  pillLockCounter++;
               }
            }

            return 0 + (int)Math.ceil(Math.max(0.0, (pillLockFlag ? pillLockTotal : Math.max(pillLockTotal, apiPutBound)) - 4.0));
         }
      }
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   private void handleLowerCase(String lowerCaseText) {
      String lowerCase = lowerCaseText.toLowerCase().trim();
      boolean lowerCaseFlag = !this.lowerCase.isEmpty();
      this.lowerCase = lowerCase;
      var arrayList = new ArrayList<>(this.lowerCaseBetaListeningList);
      this.lowerCaseBetaListeningList.clear();
      if (lowerCase.isEmpty()) {
         IconsPngType iconsPngType = this.instance.getIconsPngType();
         this.clientCorzMap.forEach((list, cat) -> cat.forEach(betaListening -> betaListening.handleFlagTimeForFlag(list == iconsPngType)));
         this.mouseScrolledCount = 0;
         this.animatedScrollOffset = 0.0;
         if (lowerCaseFlag) {
            this.updateTgtBot();
         }
      } else {
         var arrayList2 = new ArrayList<Object[]>();

         for (Entry entry : this.clientCorzMap.entrySet()) {
            if (canSocialsTheme((IconsPngType)entry.getKey())) {
               for (BetaListening betaListening2 : (Iterable<com.corz.client.render.BetaListening>) ((List)entry.getValue())) {
                  betaListening2.handleFlagTimeForFlag(false);
               }
            } else {
               String lowerCase2 = ((IconsPngType)entry.getKey()).name().toLowerCase();

               for (BetaListening betaListening3 : (Iterable<com.corz.client.render.BetaListening>) ((List)entry.getValue())) {
                  String lowerCase3 = betaListening3.getNext().getCategory().toLowerCase();
                  String orDefault = AutoYourUtils.getOrDefault(betaListening3.getNext());
                  double lowerCaseLimit = 0.0;
                  if (lowerCase3.startsWith(lowerCase)) {
                     lowerCaseLimit = 3.0;
                  } else if (lowerCase3.contains(lowerCase)) {
                     lowerCaseLimit = 2.0;
                  } else if (orDefault != null && orDefault.toLowerCase().contains(lowerCase)) {
                     lowerCaseLimit = 1.0;
                  } else if (lowerCase2.contains(lowerCase)) {
                     lowerCaseLimit = 0.5;
                  }

                  betaListening3.handleFlagTimeForFlag(lowerCaseLimit > 0.0);
                  if (lowerCaseLimit > 0.0) {
                     arrayList2.add(new Object[]{betaListening3, lowerCaseLimit});
                  }
               }
            }
         }

         arrayList2.sort((objs, objs2) -> {
            int var2x = Double.compare((Double)objs2[1], (Double)objs[1]);
            return var2x != 0 ? var2x : ((BetaListening)objs[0]).getNext().getCategory().compareToIgnoreCase(((BetaListening)objs2[0]).getNext().getCategory());
         });

         for (Object[] objs3 : (Iterable<java.lang.Object[]>) (arrayList2)) {
            this.lowerCaseBetaListeningList.add((BetaListening)objs3[0]);
         }

         this.mouseScrolledCount = 0;
         this.animatedScrollOffset = 0.0;
         long nanoTime = System.nanoTime();
         int lowerCaseCounter = 0;

         for (int i = 0; i < this.lowerCaseBetaListeningList.size(); i++) {
            BetaListening surroundKitType = this.lowerCaseBetaListeningList.get(i);
            int indexOf = arrayList.indexOf(surroundKitType);
            if (indexOf < 0) {
               surroundKitType.handleTimeFlag(nanoTime + (long)(Math.min(lowerCaseCounter++, 16) * 26.0 * 0.6 * 1000000.0), true);
            } else if (indexOf != i) {
               surroundKitType.handleTimeFlag(nanoTime, false);
            }
         }

         if (lowerCaseCounter > 0) {
            this.nanoTimeTgtBot = nanoTime;
         }
      }
   }

   private void renderMatricesTranslate(DrawContext context, float y) {
      this.matrixStack.push();
      this.matrixStack.translate(0.0F, y, 0.0F);
      context.getMatrices().pushMatrix();
      context.getMatrices().translate(0.0F, y);
   }

   private boolean canClientCorz(double clientCorzLimit, double heightAmount) {
      int clientCorzValue = (super.width - 620) / 2;
      int clientCorzValue2 = (super.height - 360) / 2;
      int clientCorzValue3 = clientCorzValue + this.clientCorzValue + 12;
      int clientCorzValue4 = clientCorzValue2 + 6 + 28 + 4;
      int clientCorzValue5 = 620 - this.clientCorzValue - 18;
      short clientCorzValue6 = 316;
      return clientCorzLimit >= clientCorzValue3 && clientCorzLimit <= clientCorzValue3 + clientCorzValue5 && heightAmount >= clientCorzValue4 && heightAmount <= clientCorzValue4 + clientCorzValue6;
   }

   private GpuTextureView getAttachmentFramebuffer() {
      if (kawaseGpu == null) {
         kawaseGpu = new KawaseGpu();
      }

      Framebuffer framebuffer = Module.mc.getFramebuffer();
      if (framebuffer != null && framebuffer.getColorAttachment() != null) {
         int framebufferWidth = Module.mc.getWindow().getFramebufferWidth();
         int framebufferHeight = Module.mc.getWindow().getFramebufferHeight();
         return framebufferWidth > 0 && framebufferHeight > 0 ? kawaseGpu.getKawaseFinal(framebuffer.getColorAttachment(), framebuffer.getColorAttachmentView(), framebufferWidth, framebufferHeight, 4, 2.5F) : null;
      } else {
         return null;
      }
   }

   @Override
   protected void init() {
      if (!this.clientCorzFlag) {
         this.clientCorzFlag = true;
         Module.mc.onResolutionChanged();
      } else {
         boolean pendingRelayout7 = this.pendingRelayout;
         this.pendingRelayout = false;
         if (pendingRelayout7 && this.logoProfile != null && super.width == this.cachedWidth && super.height == this.cachedHeight) {
            super.init();
            matrixMatricesActive = this.corzMatrixActive;
         } else {
            super.init();
            MatrixClearUtils.updateAmountTime();
            matrixMatricesActive = this.corzMatrixActive;
            this.instance = PngIcons.getInstance();
            var miscDonutsmp = this.instance.getMiscDonutsmp();
            int matrixMatricesValue = (super.width - 620) / 2;
            int matrixMatricesValue2 = (super.height - 360) / 2;
            this.logoProfile = new SidebarPanel(matrixMatricesValue + 6, matrixMatricesValue2 + 6, 128, 348, miscDonutsmp, this::handlePillTgt);
            this.clientCorzValue = this.logoProfile.getCurrentWidth();
            this.helloTime = new HelloTime(matrixMatricesValue + this.clientCorzValue + 12, matrixMatricesValue2 + 6, 620 - this.clientCorzValue - 18);
            boolean matrixMatricesFlag = false;

            for (IconsPngType iconsPngType : miscDonutsmp) {
               var list = iconsPngType == IconsPngType.BOTS ? MinecraftBotUtils.getItems(this.instance.getItems(iconsPngType)) : this.instance.getItems(iconsPngType);
               var arrayList = new ArrayList();
               int matrixMatricesValue3 = matrixMatricesValue + this.clientCorzValue + 12;
               int matrixMatricesValue4 = matrixMatricesValue2 + 6 + 28 + 4 + 0;
               int matrixMatricesValue5 = 620 - this.clientCorzValue - 18 - 0;
               boolean socialsTheme = canSocialsTheme(iconsPngType);
               int matrixMatricesValue6 = socialsTheme ? matrixMatricesValue5 : (matrixMatricesValue5 - 4) / 2;
               int matrixMatricesTotal = matrixMatricesValue4;
               int matrixMatricesTotal2 = matrixMatricesValue4;
               String matrixMatricesText = null;

               for (int i = 0; i < list.size(); i++) {
                  Module Module = (Module)list.get(i);
                  int matrixMatricesValue7;
                  int matrixMatricesTotal8;
                  if (socialsTheme) {
                     matrixMatricesValue7 = matrixMatricesValue3;
                     matrixMatricesTotal8 = matrixMatricesTotal;
                  } else {
                     boolean matrixMatricesFlag2 = i % 2 == 0;
                     matrixMatricesValue7 = matrixMatricesFlag2 ? matrixMatricesValue3 : matrixMatricesValue3 + matrixMatricesValue6 + 4;
                     matrixMatricesTotal8 = matrixMatricesFlag2 ? matrixMatricesTotal : matrixMatricesTotal2;
                  }

                  BetaListening betaListening = new BetaListening(matrixMatricesValue7, matrixMatricesTotal8, matrixMatricesValue6, Module);
                  if (socialsTheme) {
                     betaListening.handleTimeFlag(true);
                     if (iconsPngType == IconsPngType.SETTING) {
                        String formatSecuritySocial = betaListening.formatSecuritySocial();
                        if (!formatSecuritySocial.equals(matrixMatricesText)) {
                           betaListening.setListeningText(formatSecuritySocial);
                           matrixMatricesText = formatSecuritySocial;
                        }
                     }
                  }

                  betaListening.handleFlagTimeForFlag(iconsPngType == this.instance.getIconsPngType());
                  boolean matrixMatricesFlag3 = this.instance.isOrDefault(Module.getCategory()) && !matrixMatricesFlag;
                  if (this.instance.isOrDefault(Module.getCategory()) && !matrixMatricesFlag3) {
                     this.instance.handle(Module.getCategory(), false);
                  }

                  if (matrixMatricesFlag3) {
                     matrixMatricesFlag = true;
                  }

                  betaListening.handleFlagTime(matrixMatricesFlag3);
                  arrayList.add(betaListening);
                  if (socialsTheme) {
                     matrixMatricesTotal += betaListening.getListeningFlag() + 4;
                  } else {
                     boolean matrixMatricesFlag4 = i % 2 == 0;
                     if (matrixMatricesFlag4) {
                        matrixMatricesTotal += betaListening.getListeningFlag() + 4;
                     } else {
                        matrixMatricesTotal2 += betaListening.getListeningFlag() + 4;
                     }
                  }
               }

               this.clientCorzMap.put(iconsPngType, arrayList);
            }

            this.resetLock = ThemeCustomizer.singleton != null ? new ResetLock(ThemeCustomizer.singleton) : null;
            this.bindPill = new BindPill();
            if (this.botTgt != null) {
               this.botTgt.updateRenderMatrix();
            }

            this.botTgt = new BotTgt(MinecraftBotUtils.getItems(this.instance.getItems(IconsPngType.BOTS)));
            this.updateTgtBot();
            this.cachedWidth = super.width;
            this.cachedHeight = super.height;
            if (this.next != null) {
               IconsPngType iliIiiliiiii = this.next.getPageType();
               if (this.instance.getIconsPngType() != iliIiiliiiii) {
                  this.handlePillTgt(iliIiiliiiii);
               }

               if (iliIiiliiiii == IconsPngType.BOTS) {
                  this.botTgt.handle(this.next);
               }

               for (BetaListening getTgtBotItem : this.getTgtBot()) {
                  if (getTgtBotItem.getNext() == this.next) {
                     getTgtBotItem.handleFlagTimeForFlag(true);
                     getTgtBotItem.updateNext();
                     break;
                  }
               }

               this.next = null;
            }
         }
      }
   }

   private static float getAmount(float matrixMatricesValue, float matrixMatricesFactor) {
      float matrixMatricesFactor2 = getAmount(matrixMatricesValue) - 1.0F;
      return 1.0F + (matrixMatricesFactor + 1.0F) * matrixMatricesFactor2 * matrixMatricesFactor2 * matrixMatricesFactor2 + matrixMatricesFactor * matrixMatricesFactor2 * matrixMatricesFactor2;
   }

   private void handleClientCorz(double clientCorzValue) {
      int clientCorzValue2 = (super.height - 360) / 2;
      int clientCorzValue3 = (super.width - 620) / 2;
      int clientCorzValue4 = clientCorzValue3 + this.clientCorzValue + 12;
      int clientCorzValue5 = clientCorzValue2 + 6 + 28 + 4 + 0;
      int constValue = 620 - this.clientCorzValue - 18 - 0;
      if (this.isTgtBots()) {
         this.botTgt.handleUtilityInspector(clientCorzValue4, clientCorzValue5, constValue, this.getValue());
      } else {
         boolean clientCorzFlag = this.isFlag();
         int listeningKeybindValue = clientCorzFlag ? constValue : (constValue - 4) / 2;
         var tgtBot = this.getTgtBot();
         double clientCorzTotal = clientCorzValue5 - clientCorzValue;
         double clientCorzTotal2 = clientCorzValue5 - clientCorzValue;
         int clientCorzCounter = 0;

         for (BetaListening betaListening : tgtBot) {
            if (betaListening.isKeybindBindActive()) {
               int keybindListeningValue;
               double clientCorzTotal9;
               if (clientCorzFlag) {
                  keybindListeningValue = clientCorzValue4;
                  clientCorzTotal9 = clientCorzTotal;
               } else {
                  boolean clientCorzFlag2 = clientCorzCounter % 2 == 0;
                  keybindListeningValue = clientCorzFlag2 ? clientCorzValue4 : clientCorzValue4 + listeningKeybindValue + 4;
                  clientCorzTotal9 = clientCorzFlag2 ? clientCorzTotal : clientCorzTotal2;
               }

               betaListening.handleKeybindListening(keybindListeningValue, clientCorzTotal9, listeningKeybindValue);
               if (clientCorzFlag) {
                  clientCorzTotal += betaListening.getAmountFlagListeningActive() + 4.0;
               } else {
                  boolean clientCorzFlag3 = clientCorzCounter % 2 == 0;
                  if (clientCorzFlag3) {
                     clientCorzTotal += betaListening.getAmountFlagListeningActive() + 4.0;
                  } else {
                     clientCorzTotal2 += betaListening.getAmountFlagListeningActive() + 4.0;
                  }
               }

               clientCorzCounter++;
            }
         }
      }
   }

   private void updateHelloClient() {
      IconsPngType iconsPngType = this.instance.getIconsPngType();
      int helloClientLimit = -1;
      int helloClientCounter = 0;
      if (!canSocialsTheme(iconsPngType)) {
         var clientcorzmap = this.clientCorzMap.get(iconsPngType);
         helloClientLimit = clientcorzmap == null ? 0 : clientcorzmap.size();

         for (int i = 0; i < helloClientLimit; i++) {
            if (((BetaListening)clientcorzmap.get(i)).getNext().isEnabled()) {
               helloClientCounter++;
            }
         }
      }

      this.helloTime.handleResultsModule(iconsPngType, helloClientLimit, helloClientCounter, this.isLowerCase(), this.lowerCaseBetaListeningList.size());
   }

   private boolean isPillLock() {
      if (this.isLockTheme()) {
         return this.resetLock.isFlag();
      } else if (this.isPillBind()) {
         return this.bindPill.isFlag();
      } else {
         for (BetaListening betaListening : this.getTgtBot()) {
            if (betaListening.isFlagTime()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isMatrixMatricesActive() {
      return matrixMatricesActive;
   }

   @Override
   public void render(DrawContext context, int matrixMatricesValue, int heightValue, float modulesClearValue) {
      CorzRoundedUtils.update();

      try {
         this.renderMatrixMatrices(context, matrixMatricesValue, heightValue, modulesClearValue);
      } finally {
         CorzRoundedUtils.updateBufferCloseCount();
      }
   }

   private boolean isPillBind() {
      return !this.isLowerCase() && this.bindPill != null && this.instance.getIconsPngType() == IconsPngType.SETTING;
   }

   @Override
   public boolean mouseScrolled(double mouseScrolledValue, double heightValue, double mouseScrolledValue3, double panelsClampValue) {
      RenderMatrixUtilsAmount.update();
      if (this.isCorzMatrix()) {
         return true;
      } else if (ClampMatrixUtils.canPanelsClamp(panelsClampValue)) {
         return true;
      } else {
         mouseScrolledValue = this.getWidthAmount(mouseScrolledValue);
         double heightAmount = this.getHeightAmount(heightValue);
         if (this.isLockTheme() || this.isPillBind()) {
            this.mouseScrolledCount -= (int)(panelsClampValue * 28.0);
            this.mouseScrolledCount = Math.max(0, Math.min(this.mouseScrolledCount, this.getMax()));
            return true;
         } else if (this.isTgtBots()) {
            this.botTgt.handleScissorInspector(mouseScrolledValue, heightAmount, panelsClampValue);
            return true;
         } else if (!this.isPillLock()) {
            for (BetaListening betaListening : this.getTgtBot()) {
               if (betaListening.canKeybindBind(mouseScrolledValue, heightAmount, panelsClampValue)) {
                  return true;
               }
            }

            this.mouseScrolledCount -= (int)(panelsClampValue * 28.0);
            this.mouseScrolledCount = Math.max(0, Math.min(this.mouseScrolledCount, this.getMax()));
            return true;
         } else {
            for (BetaListening getTgtBotItem : this.getTgtBot()) {
               getTgtBotItem.canKeybindBind(mouseScrolledValue, heightAmount, panelsClampValue);
            }

            return true;
         }
      }
   }

   private void handlePillTgt(IconsPngType iconsPngType) {
      if (this.isLowerCase()) {
         this.helloTime.getClearIcons().handleSubstringClear("");
         this.matrixMatricesText = "";
         this.handleLowerCase("");
      }

      if (iconsPngType != this.instance.getIconsPngType()) {
         if (this.bindPill != null) {
            this.bindPill.updateListenIcons2();
         }

         if (this.botTgt != null) {
            this.botTgt.updateMatrixUtility();
         }

         this.instance.setIconsPngType(iconsPngType);
         this.mouseScrolledCount = 0;
         this.animatedScrollOffset = 0.0;
         this.clientCorzMap.forEach((iconsPngType2, cat) -> cat.forEach(cxx -> {
            if (cxx.isListeningPending()) {
               cxx.handleFlagTime(false);
               cxx.updateAmountFlag();
               this.instance.handle(cxx.getNext().getCategory(), false);
            }

            cxx.handleFlagTimeForFlag(iconsPngType2 == iconsPngType);
         }));
         this.updateTgtBot();
      }
   }

   private boolean isPillBindHelloTime() {
      if (!this.helloTime.getClearIcons().isClearModulesFlag() && !this.isPillLock()) {
         if (!this.isLowerCase()) {
            IconsPngType iconsPngType = this.instance.getIconsPngType();
            if (iconsPngType == IconsPngType.CONFIGS || iconsPngType == IconsPngType.SOCIALS) {
               return false;
            }
         }

         for (BetaListening betaListening : this.getTgtBot()) {
            if (betaListening.isListeningGlfw() || betaListening.isFlagKeybindScissorSettingCompone()) {
               return false;
            }
         }

         if (this.bindPill == null || !this.bindPill.isListen() && !this.bindPill.isIconsClear()) {
            long handle = Module.mc.getWindow().getHandle();

            for (int i = 0; i < 3; i++) {
               if (GLFW.glfwGetMouseButton(handle, i) == 1) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private List<BetaListening> getTgtBot() {
      if (this.isLowerCase()) {
         return this.lowerCaseBetaListeningList;
      } else if (this.isTgtBots()) {
         return this.botTgt.getItems();
      } else {
         var clientcorzmap = this.clientCorzMap.get(this.instance.getIconsPngType());
         return clientcorzmap != null ? clientcorzmap : List.of();
      }
   }

   private void handleClientCorz(BetaListening betaListening) {
      this.clientCorzMap.values().forEach(keepx -> keepx.forEach(keepxx -> {
         if (keepxx != betaListening && keepxx.isListeningPending()) {
            keepxx.handleFlagTime(false);
            this.instance.handle(keepxx.getNext().getCategory(), false);
         }
      }));
   }

   private void renderMatrixMatrices(DrawContext context, int matrixMatricesValue, int heightValue, float modulesClearValue) {
      long nanoTime = System.nanoTime();
      int samplerCorzCount = DynMappableUtils.samplerCorzCount;
      MatrixClearUtils.updateTimeAmount();
      MatrixEspUtils.updateFlagTime();
      if (!firstRenderDone) {
         firstRenderDone = true;
      }

      CorzClient.fontManager.setMatrixStack(this.matrixStack);
      MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
      CorzRoundedUtils.panelOverlayAmount = 1.0F;
      if (!this.corzMatrixActive && Math.abs(this.matrixMatricesQuintScissor.getTargetAmount() - 1.0) > 0.01) {
         this.matrixMatricesQuintScissor.handleAmountTime(1.0);
      }

      float matrixMatricesValue4 = (float)this.matrixMatricesQuintScissor.getAmount();
      boolean matrixMatricesFlag = ClampMatrixUtils.isFlag2();
      if (matrixMatricesFlag) {
         ClampMatrixUtils.renderContextDraw(context);
      }

      if (!this.corzMatrixActive || !(matrixMatricesValue4 < 0.006F)) {
         int scissorMatrixValue = (super.width - 620) / 2;
         int randomValue = (super.height - 360) / 2;
         if (this.matrixCorz.isFlag()) {
            this.panelHovered = true;
            if (matrixMatricesFlag) {
               ClampMatrixUtils.handleClampAmount(scissorMatrixValue, randomValue, 620.0F, 360.0F, 12.0F, 1.0F, 1.0F, true);
            }

            this.matrixCorz.renderScissorMatrix(context, scissorMatrixValue, randomValue, 620, 360, 12);
         } else {
            if (this.panelHovered) {
               this.panelHovered = false;
               this.updateTgtBot();
               RenderMatrixUtilsAmount.update2();
            }

            this.handleClientCorz(scissorMatrixValue, randomValue);
            float matrixMatricesValue7;
            float panelOverlayLimit;
            float matrixMatricesLimit;
            float amount;
            if (this.corzMatrixActive) {
               float matrixMatricesValue9 = matrixMatricesFlag ? (float)Math.cbrt(matrixMatricesValue4) : 0.0F;
               float matrixMatricesValue10 = matrixMatricesFlag ? getAmount((matrixMatricesValue9 - 0.22F) / 0.78F) : getAmount((matrixMatricesValue4 - 0.3F) / 0.7F);
               matrixMatricesValue7 = getAmount(matrixMatricesValue10 * 1.9F);
               panelOverlayLimit = getAmountForFloat(getAmount((matrixMatricesValue10 - 0.12F) / 0.88F));
               matrixMatricesLimit = getAmountForFloat(matrixMatricesFlag ? getAmount(matrixMatricesValue9 / 0.75F) : getAmount(matrixMatricesValue4 / 0.3F));
               amount = 0.96F + 0.04F * matrixMatricesValue10;
            } else {
               float amountForFloat = getAmountForFloat(matrixMatricesValue4);
               matrixMatricesLimit = amountForFloat;
               matrixMatricesValue7 = getAmount(amountForFloat * 1.9F);
               panelOverlayLimit = getAmountForFloat(getAmount((amountForFloat - 0.12F) / 0.88F));
               amount = 0.96F + 0.04F * amountForFloat;
            }

            this.matrixMatricesAmount = amount;
            matrixMatricesValue = (int)Math.round(this.getWidthAmount(matrixMatricesValue));
            heightValue = (int)Math.round(this.getHeightAmount(heightValue));
            if (matrixMatricesFlag) {
               ClampMatrixUtils.handleClampAmount(scissorMatrixValue, randomValue, 620.0F, 360.0F, 12.0F, amount, matrixMatricesValue7, false);
            }

            RenderMatrixUtilsAmount.handlePipelineCabin(
               super.width,
               super.height,
               matrixMatricesLimit,
               matrixMatricesValue,
               heightValue,
               !matrixMatricesFlag,
               Module.mc.currentScreen == this,
               this.isPillBindHelloTime(),
               !this.isPillLock(),
               scissorMatrixValue,
               randomValue,
               620,
               360
            );
            GpuTextureView gpuTextureView = null;
            boolean matrixMatricesFlag2 = CardMatrixUtils.isModernFlag() && ThemeCustomizer.booleanValue;
            if (!matrixMatricesFlag && !introTriggered && matrixMatricesFlag2 && matrixMatricesLimit > 0.02F) {
               try {
                  gpuTextureView = this.getAttachmentFramebuffer();
               } catch (Throwable e) {
                  introTriggered = true;
               }
            }

            int matrixMatricesFactor = CardMatrixUtils.isModernFlag() ? 46 : 145;
            if (matrixMatricesFlag) {
               ClampMatrixUtils.handleClampAmount(matrixMatricesLimit, matrixMatricesFactor);
               ClampMatrixUtils.handleFlag(matrixMatricesFlag2 && matrixMatricesLimit > 0.02F);
            } else if (matrixMatricesLimit > 0.001F) {
               MatrixPeekUtilsRender.renderMatrixStack(context, 0, 0, super.width, super.height, 0, (int)(matrixMatricesLimit * matrixMatricesFactor) << 24);
            }

            float widthScaled = super.width / 2.0F;
            float heightScaled = super.height / 2.0F;
            this.matrixStack.push();
            this.matrixStack.translate(widthScaled, heightScaled, 0.0F);
            this.matrixStack.scale(amount, amount, 1.0F);
            this.matrixStack.translate(-widthScaled, -heightScaled, 0.0F);
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(widthScaled, heightScaled);
            context.getMatrices().scale(amount, amount);
            context.getMatrices().translate(-widthScaled, -heightScaled);
            int botTgtValue = scissorMatrixValue + this.clientCorzValue + 12;
            int matrixMatricesValue13 = randomValue + 6 + 28 + 4;
            int constValue = 620 - this.clientCorzValue - 18;
            short matrixMatricesValue15 = 316;

            try {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(matrixMatricesValue7);

               try {
                  if (gpuTextureView != null) {
                     try {
                        float width = super.width;
                        float height = super.height;
                        float matrixMatricesValue16 = scissorMatrixValue / width;
                        float matrixMatricesValue17 = (scissorMatrixValue + 620) / width;
                        float matrixMatricesValue18 = 1.0F - randomValue / height;
                        float matrixMatricesValue19 = 1.0F - (randomValue + 360) / height;
                        CorzRoundedUtils.handleTextureColor(
                           this.matrixStack.peek().getPositionMatrix(), scissorMatrixValue, randomValue, scissorMatrixValue + 620, randomValue + 360, matrixMatricesValue16, matrixMatricesValue18, matrixMatricesValue17, matrixMatricesValue19, gpuTextureView, 12.0F, 1.0F
                        );
                     } catch (Throwable e2) {
                        introTriggered = true;
                     }
                  }

                  if (!matrixMatricesFlag) {
                     SnowVertexUtils.handleTrianglesFlag(scissorMatrixValue, randomValue, 620, 360, 12, super.width, super.height, matrixMatricesValue, heightValue);
                  }

                  MatrixPeekUtilsRender.render(context, scissorMatrixValue, randomValue, 620, 360, 12, CardMatrixUtils.getValue(), 352321535);
                  if (!CardMatrixUtils.isModernFlag()) {
                     RenderMatrixUtilsRelease.render(context, scissorMatrixValue + 6, randomValue + 6, this.clientCorzValue, 348, 7, CardMatrixUtils.getValue8());
                     float matrixReleaseValue = scissorMatrixValue + 6 + this.clientCorzValue;
                     float matrixMatricesValue21 = 608 - this.clientCorzValue;
                     RenderMatrixUtilsRelease.render(context, matrixReleaseValue, (float)(randomValue + 6), matrixMatricesValue21, 348.0F, 7.0F, CardMatrixUtils.getReleaseRightInt());
                  }
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }

               this.renderClientCorz(context, scissorMatrixValue, randomValue, matrixMatricesValue7);
               if (!matrixMatricesFlag) {
                  SnowVertexUtils.handleTrianglesMax(scissorMatrixValue, randomValue, 620, 360, matrixMatricesValue7);
               }

               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

               try {
                  this.logoProfile.renderPanel(context, matrixMatricesValue, heightValue, modulesClearValue);
                  String text = this.helloTime.formatIconsClear();
                  if (!text.equals(this.matrixMatricesText)) {
                     this.handleLowerCase(text);
                     this.matrixMatricesText = text;
                  }

                  this.updateHelloClient();
                  this.helloTime.renderAmountTime(context, matrixMatricesValue, heightValue, modulesClearValue);

                  for (BetaListening betaListening : this.getTgtBot()) {
                     if (betaListening.isKeybindBindActive()) {
                        betaListening.updateAmountTime();
                     }
                  }

                  this.mouseScrolledCount = Math.max(0, Math.min(this.mouseScrolledCount, this.getMax()));
                  this.animatedScrollOffset = MatrixIdentifierUtils.getAmount(this.animatedScrollOffset, this.mouseScrolledCount, 18.0, MatrixClearUtils.getAmount());
                  if (Math.abs(this.animatedScrollOffset - this.mouseScrolledCount) < 0.05) {
                     this.animatedScrollOffset = this.mouseScrolledCount;
                  }

                  this.animatedScrollOffset = Math.max(0.0, Math.min(this.animatedScrollOffset, (double)this.getMax()));
                  double hashRegistryValue = BetaListening.getAmount(this.animatedScrollOffset);
                  int floor23 = (int)Math.floor(hashRegistryValue);
                  this.contentOffsetY = (float)(hashRegistryValue - floor23);
                  this.handleClientCorz(hashRegistryValue);
                  boolean matrixMatricesFlag3 = this.isLockTheme() || this.isPillBind();
                  int matrixMatricesValue24 = matrixMatricesFlag3 ? 0 : (int)Math.ceil(Math.max(0.0, 3.0 - hashRegistryValue));
                  int matrixMatricesValue25 = matrixMatricesFlag3 ? 0 : (int)Math.ceil(Math.max(0.0, 3.0 - (this.getMax() - hashRegistryValue)));
                  context.enableScissor(botTgtValue - 3, matrixMatricesValue13 - matrixMatricesValue24, botTgtValue + constValue + 3, matrixMatricesValue13 + matrixMatricesValue15 + matrixMatricesValue25);
                  double millis = (System.nanoTime() - this.nanoTimeTgtBot) / 1000000.0;

                  try {
                     if (!this.isPillLock()) {
                        int matrixStackLimit = this.canClientCorz(matrixMatricesValue, heightValue) ? matrixMatricesValue : botTgtValue - 2000;
                        if (!this.isLockTheme() && !this.isPillBind()) {
                           if (this.isTgtBots()) {
                              this.botTgt.renderMaxTime(context, botTgtValue, matrixMatricesValue13, constValue, matrixMatricesValue15, matrixStackLimit, heightValue, modulesClearValue);
                           } else {
                              boolean matrixMatricesFlag4 = this.isFlag();
                              long nanoTime2 = System.nanoTime();

                              for (BetaListening getTgtBotItem : this.getTgtBot()) {
                                 if (getTgtBotItem.isKeybindBindActive()) {
                                    float matrixMatricesFactor2 = 1.0F;
                                    float x = 1.0F;
                                    float matrixMatricesLimit2 = 0.0F;
                                    boolean matrixMatricesFlag5 = true;
                                    if (getTgtBotItem.getNanoTimeNano() != 0L) {
                                       double millis2 = (nanoTime2 - getTgtBotItem.getNanoTimeNano()) / 1000000.0;
                                       if (millis2 < 260.0) {
                                          boolean betaBadgeVisible = getTgtBotItem.isBetaBadgeVisible();
                                          float max3 = (float)Math.max(0.0, millis2 / 260.0);
                                          float matrixMatricesValue27 = millis2 <= 0.0 ? 0.0F : getAmountForFloat(getAmount((float)(millis2 / 150.0)));
                                          matrixMatricesFactor2 = betaBadgeVisible ? matrixMatricesValue27 : 0.55F + 0.45F * matrixMatricesValue27;
                                          if (!matrixMatricesFlag4) {
                                             float matrixMatricesValue28 = betaBadgeVisible ? 0.9F : 0.965F;
                                             x = matrixMatricesValue28 + (1.0F - matrixMatricesValue28) * getAmount(max3, betaBadgeVisible ? 1.9F : 1.1F);
                                             if (betaBadgeVisible) {
                                                matrixMatricesLimit2 = (1.0F - matrixMatricesValue27) * 0.5F * getAmount((float)(millis / 70.0));
                                             }
                                          }

                                          matrixMatricesFlag5 = max3 > 0.6F;
                                       }
                                    }

                                    if (!(matrixMatricesFactor2 <= 0.002F) || !(matrixMatricesLimit2 <= 0.004F)) {
                                       double keybindListeningAmount = getTgtBotItem.getKeybindListeningAmount();
                                       double amountFlag = getTgtBotItem.getAmountFlagListeningActive();
                                       float min30 = (float)Math.min(keybindListeningAmount + amountFlag - (matrixMatricesValue13 - matrixMatricesValue24), matrixMatricesValue13 + matrixMatricesValue15 + matrixMatricesValue25 - keybindListeningAmount);
                                       if (!(min30 <= 0.0F)) {
                                          float matrixMatricesFactor3 = getAmount(min30 / 14.0F);
                                          float amountFloat = getTgtBotItem.getAmountFloat();
                                          this.renderMatricesTranslate(context, amountFloat);
                                          if (Math.abs(x - 1.0F) > 5.0E-4F) {
                                             float x2 = getTgtBotItem.getBindKeybindValue() + getTgtBotItem.getListeningKeybindValue() / 2.0F;
                                             float y = (float)(Math.floor(keybindListeningAmount) + Math.min(amountFlag, (double)getTgtBotItem.getValue5()) / 2.0);
                                             this.matrixStack.translate(x2, y, 0.0F);
                                             this.matrixStack.scale(x, x, 1.0F);
                                             this.matrixStack.translate(-x2, -y, 0.0F);
                                             context.getMatrices().translate(x2, y);
                                             context.getMatrices().scale(x, x);
                                             context.getMatrices().translate(-x2, -y);
                                          }

                                          if (matrixMatricesLimit2 > 0.004F) {
                                             float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(matrixMatricesLimit2 * matrixMatricesFactor3);

                                             try {
                                                RenderMatrixUtilsRelease.render(
                                                   context,
                                                   getTgtBotItem.getBindKeybindValue(),
                                                   (float)Math.floor(keybindListeningAmount),
                                                   getTgtBotItem.getListeningKeybindValue(),
                                                   (float)amountFlag,
                                                   7.0F,
                                                   0.0,
                                                   0.0,
                                                   0.0
                                                );
                                             } finally {
                                                CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
                                             }
                                          }

                                          float panelOverlay4 = CorzRoundedUtils.getPanelOverlay(matrixMatricesFactor2 * matrixMatricesFactor3);
                                          BetaListening.handleTexturesIcons(
                                             (int)Math.floor(matrixMatricesValue13 - matrixMatricesValue24 - amountFloat) - 16, (int)Math.ceil(matrixMatricesValue13 + matrixMatricesValue15 + matrixMatricesValue25 - amountFloat) + 16
                                          );

                                          try {
                                             if (matrixMatricesFactor2 > 0.002F) {
                                                getTgtBotItem.renderKeybindBind(context, matrixMatricesFlag5 ? matrixStackLimit : botTgtValue - 2000, (int)Math.floor(heightValue - amountFloat), modulesClearValue);
                                             }
                                          } finally {
                                             BetaListening.updateTexturesIcons();
                                             CorzRoundedUtils.handlePanelOverlay(panelOverlay4);
                                             this.renderPopMatrix(context);
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        } else {
                           float amountForFloat4 = getAmountForFloat(getAmount((float)(millis / 140.0)));
                           if (amountForFloat4 > 0.002F) {
                              float contentOffsetY5 = -this.contentOffsetY;
                              int sideWalkAmount = constValue - 0;
                              this.renderMatricesTranslate(context, contentOffsetY5);
                              float panelOverlay5 = CorzRoundedUtils.getPanelOverlay(amountForFloat4);

                              try {
                                 int ctxOffset = matrixMatricesValue13 + 0 - floor23;
                                 int mouseY = (int)Math.floor(heightValue - contentOffsetY5);
                                 if (this.isLockTheme()) {
                                    this.resetLock.renderTabsReset(context, botTgtValue, ctxOffset, sideWalkAmount, matrixStackLimit, mouseY);
                                 } else {
                                    this.bindPill.renderSettingsTabs(context, botTgtValue, ctxOffset, sideWalkAmount, matrixStackLimit, mouseY);
                                 }
                              } finally {
                                 CorzRoundedUtils.handlePanelOverlay(panelOverlay5);
                                 this.renderPopMatrix(context);
                              }
                           }
                        }
                     }
                  } finally {
                     context.disableScissor();
                  }

                  this.renderAmountMax(context, botTgtValue, matrixMatricesValue13, constValue, matrixMatricesValue15, matrixMatricesValue, heightValue);
                  if (!matrixMatricesFlag) {
                     HalloweenMatrixUtils.handleAmountTriangles(scissorMatrixValue, randomValue, 620, 360, 12, super.width, matrixMatricesValue, heightValue);
                  }

                  if (!matrixMatricesFlag) {
                     SnowVertexUtils.handleTrianglesFlagForInt(scissorMatrixValue, randomValue, 620, 360, 12, super.width, super.height, matrixMatricesValue, heightValue);
                  } else {
                     SnowVertexUtils.updateFlag();
                  }

                  if (!matrixMatricesFlag) {
                     RenderMatrixUtilsAmount.renderPipelineFlag(context, scissorMatrixValue, randomValue, 620, super.width, super.height);
                  }

                  if (this.isLockTheme()) {
                     this.resetLock.renderRevealsDecember(context);
                  } else if (this.isPillBind()) {
                     this.bindPill.render(context);
                  } else {
                     if (this.isTgtBots()) {
                        this.botTgt.renderMatrixScissor(context);
                     }

                     for (BetaListening betaListening3 : this.getTgtBot()) {
                        betaListening3.renderBlatantCaution(context);
                     }
                  }

                  this.logoProfile.renderCollapsedTooltip(context, this.isPillLock(), this.isTgtBots() ? botTgtValue + ConstAmountUtils.getAmount() : botTgtValue);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            } finally {
               context.getMatrices().popMatrix();
               this.matrixStack.pop();
            }

            if (!matrixMatricesFlag) {
               RenderMatrixUtilsAmount.renderPipelineFlag(context, super.width, super.height, matrixMatricesValue7, this.matrixMatricesAmount);
            }

            if (matrixMatricesFlag) {
               ClampMatrixUtils.renderPanelsAmount(context);
            }

            super.render(context, matrixMatricesValue, heightValue, modulesClearValue);
            if (matrixMatricesFlag) {
               ClampMatrixUtils.updateFlag();
            } else {
               DisabledEnabled.getInstance().renderScaledWindow(context);
            }

            CorzRoundedUtils.updateSamplerView();
            handleTimeSampler(nanoTime, samplerCorzCount);
         }
      } else if (!ClampMatrixUtils.isFramePanels4()) {
         this.updateResolutionChanged();
      }
   }

   private void renderPopMatrix(DrawContext context) {
      context.getMatrices().popMatrix();
      this.matrixStack.pop();
   }

   private static void handleTimeSampler(long samplerValue, int samplerCorzCount) {
      long time2 = System.nanoTime() - samplerValue;
      int value4 = DynMappableUtils.samplerCorzCount - samplerCorzCount;
      samplerCount++;
      matrixMatricesCount += time2;
      samplerAccumulatedMs += value4;
      if (time2 > peakSampleNanos) {
         peakSampleNanos = time2;
      }

      if (value4 > peakSampleCount) {
         peakSampleCount = value4;
      }

      long currentTimeMillis = System.currentTimeMillis();
      if (timeMillis == 0L) {
         timeMillis = currentTimeMillis;
      }

      if (currentTimeMillis - timeMillis >= 5000L) {
         samplerCount = 0;
         matrixMatricesCount = 0L;
         samplerAccumulatedMs = 0;
         peakSampleNanos = 0L;
         peakSampleCount = 0;
         timeMillis = currentTimeMillis;
      }
   }

   private BetaListening getNext() {
      for (BetaListening next : this.getTgtBot()) {
         if (next.isFlagTime()) {
            return next;
         }
      }

      return null;
   }

   private boolean isLowerCase() {
      return !this.lowerCase.isEmpty();
   }

   private double getWidthAmount(double widthValue) {
      return super.width / 2.0 + (widthValue - super.width / 2.0) / this.matrixMatricesAmount;
   }

   public MatrixMatricesScreen(Module next) {
      this();
      this.next = next;
   }

   private void handleMax(double matrixMatricesValue, int matrixMatricesValue2, int matrixMatricesValue3, int matrixMatricesValue4) {
      int matrixMatricesFactor = this.getMax();
      if (matrixMatricesFactor <= 0) {
         this.mouseScrolledCount = 0;
      } else {
         double max2 = Math.max(0.0, Math.min(1.0, (matrixMatricesValue - matrixMatricesValue2) / (matrixMatricesValue3 - matrixMatricesValue4)));
         this.mouseScrolledCount = (int)Math.round(max2 * matrixMatricesFactor);
      }
   }

   public MatrixMatricesScreen() {
      super(Text.literal("CorzClient"));
      this.clientCorzValue = 128;
      this.mouseScrolledCount = 0;
      this.animatedScrollOffset = 0.0;
      this.mouseClickedActive = false;
      this.mouseClickedAmount = 0.0;
      this.matrixMatricesText = "";
      this.nanoTimeTgtBot = System.nanoTime();
      this.contentOffsetY = 0.0F;
      this.smoothedScrollPx = 0.0F;
      this.scrollbarFadeProgress = 0.0;
      this.tgtBotValue = System.nanoTime();
      this.matrixCorz = new MatrixCorz();
      this.matrixStack = new MatrixStack();
      this.matrixMatricesQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 220, 0.0);
      this.corzMatrixActive = false;
      this.panelHovered = false;
      this.matrixMatricesAmount = 1.0F;
      this.next = null;
      this.pendingRelayout = false;
      this.cachedWidth = -1;
      this.cachedHeight = -1;
      this.lowerCase = "";
      this.lowerCaseBetaListeningList = new ArrayList<>();
      TwoDidUtils.updateTimeFlag();
      HalloweenMatrixUtils.updateHalloweenAmount();
      SnowVertexUtils.updateFlag();
      RenderMatrixUtilsAmount.updateAmountTime();
   }

   @Override
   public boolean mouseDragged(Click click, double mouseDraggedValue, double mouseDraggedValue2) {
      RenderMatrixUtilsAmount.update();
      if (this.isCorzMatrix()) {
         return true;
      } else if (ClampMatrixUtils.canPanelsClamp(click.button(), mouseDraggedValue, mouseDraggedValue2)) {
         return true;
      } else {
         double widthAmount = this.getWidthAmount(click.x());
         double heightAmount = this.getHeightAmount(click.y());
         int button = click.button();
         if (this.mouseClickedActive) {
            int[] clientCorz = this.getClientCorz();
            if (clientCorz != null) {
               this.handleMax(heightAmount - this.mouseClickedAmount, clientCorz[1], clientCorz[2], clientCorz[5]);
               this.animatedScrollOffset = this.mouseScrolledCount;
            }

            return true;
         } else if (this.isTgtBots()) {
            this.botTgt.handleRenderMatrix(widthAmount, heightAmount, button);
            return true;
         } else if (this.isLockTheme()) {
            this.resetLock.handleScissorMusicForDoubleInt(widthAmount, heightAmount + this.contentOffsetY, button);
            return true;
         } else if (this.isPillBind()) {
            this.bindPill.handle(widthAmount, heightAmount + this.contentOffsetY, button);
            return true;
         } else {
            double heightAmount3 = heightAmount;

            for (BetaListening betaListening : this.getTgtBot()) {
               betaListening.handleFlag(widthAmount, heightAmount3, button);
            }

            return this.isPillLock();
         }
      }
   }

   private void handleClientCorz(int clientCorzValue, int randomValue) {
      this.logoProfile.updateCollapseAnimation();
      int currentWidth = this.logoProfile.getCurrentWidth();
      if (currentWidth != this.clientCorzValue) {
         this.clientCorzValue = currentWidth;
         this.helloTime.handleAmountIcons(clientCorzValue + this.clientCorzValue + 12, 620 - this.clientCorzValue - 18);
      }
   }
}
