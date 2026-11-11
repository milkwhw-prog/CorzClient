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
import com.corz.client.render.MatrixStackUtilsAmount.Hits;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ai.auth.LicenseRuntime;
import ai.auth.DeviceIdentity;

public class CorzLicense extends Screen implements ScreenGuiApi, GuiClickApi {
   private String licenseContinueText;
   private final StringBuilder stringBuilder;
   private float introFadeAlpha;
   private float layoutScale;
   private volatile boolean secureActivationFlag;
   private boolean corzLicenseActive;
   private float translateAmount;
   private boolean licenseCorzFlag;
   private long lastNanoTime;
   private final boolean activationRequired;
   private long lastNanoTimeSecureActivation;
   private final Anims field;
   private static final String CORZ_CLIENT = "Corz Client";
   private static final SoundEvent soundEvent = SoundEvent.of(Identifier.of("corzclient", "access_granted"));
   private final ClearEdit xxxxxFocusClearEdit;
   private final long corzLicenseValue;
   private boolean xxxxxCorzFlag;
   private final Screen screen;
   private final MatrixStack matrixStack = new MatrixStack();
   private String secureActivationText;
   private long timeMillis;
   private boolean focus;
   private int secureActivationValue;
   private final boolean pressedActive;
   private final boolean resolutionRestoreEnabled;
   private final Hits xxxxxFocusHits;
   private volatile boolean activate;
   private static final Identifier identifier = Identifier.of("corzclient", "textures/corz_mark.png");
   private int secureRenderValue;
   private String secureText;

   private void handleScreenResolution(boolean screenResolutionFlag) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (screenResolutionFlag) {
         instance.setScreen(new MatrixMatricesScreen());
      } else {
         instance.setScreen(this.screen);
      }

      if (!(instance.currentScreen instanceof ScreenGuiApi)) {
         instance.onResolutionChanged();
      }
   }

   @Override
   protected void init() {
      if (!this.licenseCorzFlag) {
         this.licenseCorzFlag = true;
         MinecraftClient.getInstance().onResolutionChanged();
      } else {
         super.init();
      }
   }

   private void renderSecureActivation(DrawContext context, float secureActivationValue, float matrixStackValue, long repeatValue) {
      String secureActivationText;
      int secureActivationValue4;
      if (this.activate) {
         secureActivationText = "Contacting license server";
         secureActivationValue4 = -7630437;
      } else if (this.licenseContinueText != null) {
         secureActivationText = this.licenseContinueText;
         secureActivationValue4 = -7630437;
      } else if (this.secureActivationFlag) {
         secureActivationText = LicenseRuntime.getLicenseOwnerLabel();
         secureActivationValue4 = -12922502;
      } else {
         secureActivationText = "Secure Ed25519 activation.";
         secureActivationValue4 = -7630437;
      }

      long nanoTime = System.nanoTime();
      if (this.secureActivationText == null) {
         this.secureActivationText = secureActivationText;
         this.secureActivationValue = secureActivationValue4;
      } else if (!secureActivationText.equals(this.secureActivationText) || secureActivationValue4 != this.secureActivationValue) {
         this.secureText = this.secureActivationText;
         this.secureRenderValue = this.secureActivationValue;
         this.secureActivationText = secureActivationText;
         this.secureActivationValue = secureActivationValue4;
         this.lastNanoTimeSecureActivation = nanoTime;
      }

      float secureActivationLimit = this.lastNanoTimeSecureActivation == 0L ? 1.0F : MatrixStackUtilsAmount.getAmountForFloat((float)(nanoTime - this.lastNanoTimeSecureActivation) / 1.8E8F);
      if (secureActivationLimit >= 1.0F) {
         this.secureText = null;
      }

      float secureActivationValue5 = MatrixStackUtilsAmount.getAmount(matrixStackValue, 10.0F, 0.82F);
      if (this.secureText != null) {
         this.render(context, this.secureText, this.secureRenderValue, secureActivationValue, secureActivationValue5 - 3.0F * secureActivationLimit, 1.0F - secureActivationLimit, "");
      }

      this.render(context, this.secureActivationText, this.secureActivationValue, secureActivationValue, secureActivationValue5 + 3.0F * (1.0F - secureActivationLimit), secureActivationLimit, this.activate ? getRepeat(repeatValue) : "");
   }

   private static String getRepeat(long repeatValue) {
      int repeatValue2 = (int)(repeatValue / 350L % 4L);
      return ".".repeat(repeatValue2);
   }

   private void renderXxxxxFocus(DrawContext context, int xxxxxFocusValue, int brownValue, int releaseRightBound, int matrixStackLimit, int scaledY) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      boolean xxxxxFocusFlag = !this.activate && MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, xxxxxFocusValue, brownValue, releaseRightBound, 24.0F);
      float releaseRight = MatrixStackUtilsAmount.getReleaseRight(
         context,
         xxxxxFocusValue,
         brownValue,
         releaseRightBound,
         24.0F,
         IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("key"),
         this.field.getAmount("focus", this.focus ? 1.0F : 0.0F, 16.0F),
         this.field.getAmount("field", xxxxxFocusFlag),
         this.field.getAmount("failed", 0.0F, 12.0F)
      );
      int abcdefghijklmnopqrstuvwxyzBrown = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, 24);
      String formatText = this.formatText(this.stringBuilder.toString(), xxxxxFocusValue + releaseRightBound - 9.0F - releaseRight);
      if (this.stringBuilder.length() == 0) {
         MatrixStackUtilsAmount.handle("CORZ-XXXXX-XXXXX-XXXXX", releaseRight, abcdefghijklmnopqrstuvwxyzBrown, MatrixStackUtilsAmount.getValue(-7630437, 0.55F));
      } else {
         if (this.xxxxxCorzFlag) {
            float materialFactor = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() + 2;
            MatrixPeekUtilsRender.renderMatrixPeek(
               context,
               releaseRight - 1.5F,
               brownValue + (24.0F - materialFactor) / 2.0F,
               MatrixStackUtilsAmount.getAmount3(formatText) + 3.0F,
               materialFactor,
               2.0F,
               CardMatrixUtils.getReleaseRight(0.32)
            );
         }

         MatrixStackUtilsAmount.handle(formatText, releaseRight, abcdefghijklmnopqrstuvwxyzBrown, -920587);
      }

      if (this.focus && !this.activate && !this.xxxxxCorzFlag) {
         int n4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() - 2;
         this.xxxxxFocusClearEdit
            .renderAmountTime(
               context,
               releaseRight + (this.stringBuilder.length() == 0 ? 0.0F : MatrixStackUtilsAmount.getAmount3(formatText) + 1.0F),
               brownValue + (24 - n4) / 2,
               n4,
               MatrixEspUtils.releaseRightValue,
               MatrixClearUtils.getAmount()
            );
      }

      this.xxxxxFocusHits.handle(xxxxxFocusValue, brownValue, releaseRightBound, 24.0F, this::updateXxxxxFocus);
   }

   private void renderTime(DrawContext context, int corzLicenseValue, int matrixStackValue, int corzLicenseDivisor, long corzLicenseValue4) {
      float corzLicenseValue5 = (float)(corzLicenseValue4 - this.timeMillis);
      float nowOffset = corzLicenseValue + 150.0F;
      float y = matrixStackValue + corzLicenseDivisor / 2.0F - 24.0F;
      float corzLicenseLimit = 22.0F * MatrixStackUtilsAmount.getAmount((corzLicenseValue5 - 60.0F) / 340.0F, 1.7F);
      if (corzLicenseLimit > 0.25F) {
         MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
            context, nowOffset - corzLicenseLimit, y - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, MatrixStackUtilsAmount.getValue(-12922502, 0.34F), 16.0F, 0.9F
         );
         MatrixPeekUtilsRender.renderMatrixPeek(context, nowOffset - corzLicenseLimit, y - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, -12922502);
         float amountForFloat = MatrixStackUtilsAmount.getAmountForFloat((corzLicenseValue5 - 220.0F) / 200.0F);
         if (amountForFloat > 0.002F) {
            float peekPositionFactor = 24.0F;
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
               context, nowOffset, y, 0.6F + 0.4F * MatrixStackUtilsAmount.getAmount((corzLicenseValue5 - 220.0F) / 300.0F, 2.0F)
            );
            MatrixPeekUtilsIdentifier.handle(
               nowOffset - peekPositionFactor / 2.0F,
               y - peekPositionFactor / 2.0F,
               peekPositionFactor,
               peekPositionFactor,
               IcoGenUtils.getIcoCheck(),
               new Color(MatrixStackUtilsAmount.getValue(-1, amountForFloat), true),
               MatrixPeekUtilsRender.getMatrixStack()
            );
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      }

      float ctxOffset = y + 22.0F + 14.0F;
      this.handlePopTranslate(
         getAmount((corzLicenseValue5 - 360.0F) / 300.0F),
         () -> MatrixStackUtilsAmount.getAmountForDrawContextString(
            context, "Access granted", MatrixStackUtilsAmount.getAmount(nowOffset - MatrixStackUtilsAmount.getAmount("Access granted", 1.22F) / 2.0F), ctxOffset, 14.0F
         )
      );
      this.handlePopTranslate(getAmount((corzLicenseValue5 - 560.0F) / 300.0F), () -> this.handle("Welcome to Corz Client.", nowOffset, ctxOffset + 14.0F + 3.0F, -7630437));
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   private void updateLicenseContinue() {
      if (!this.activate && this.timeMillis <= 0L) {
         String trim = this.stringBuilder.toString().trim();
         if (trim.isEmpty()) {
            this.licenseContinueText = "Enter your license key to continue.";
         } else {
            this.licenseContinueText = null;
            this.activate = true;
            Thread thread = new Thread(() -> {
               try {
                  LicenseRuntime.activateLicense(trim);
               } finally {
                  this.activate = false;
                  this.secureActivationFlag = true;
               }
            }, "corz-license-ui");
            thread.setDaemon(true);
            thread.start();
         }
      }
   }

   @Override
   public boolean keyPressed(KeyInput keyInput) {
      if (this.timeMillis <= 0L && this.lastNanoTime == 0L) {
         int pressedValue = keyInput.key();
         boolean pressedFlag = (keyInput.modifiers() & 10) != 0;
         if (pressedValue == 256) {
            this.updateTimeFlag();
            return true;
         } else if (this.pressedActive) {
            return true;
         } else if (pressedValue == 257 || pressedValue == 335) {
            this.updateLicenseContinue();
            return true;
         } else if (!this.focus) {
            return true;
         } else if (pressedValue == 65 && pressedFlag) {
            this.xxxxxCorzFlag = this.stringBuilder.length() > 0;
            return true;
         } else if (pressedValue != 259 && pressedValue != 261) {
            if (pressedValue == 86 && pressedFlag) {
               try {
                  String clipboard = MinecraftClient.getInstance().keyboard.getClipboard();
                  if (clipboard != null) {
                     if (this.xxxxxCorzFlag) {
                        this.updateBuilder();
                        this.xxxxxCorzFlag = false;
                     }

                     this.handleBuilder(clipboard.trim());
                     this.updateSecureActivation();
                  }
               } catch (Exception e) {
               }

               return true;
            } else {
               return true;
            }
         } else {
            if (!this.xxxxxCorzFlag && !pressedFlag) {
               if (this.stringBuilder.length() > 0) {
                  this.stringBuilder.deleteCharAt(this.stringBuilder.length() - 1);
                  this.updateSecureActivation();
               }
            } else {
               if (this.stringBuilder.length() > 0) {
                  this.updateBuilder();
                  this.updateSecureActivation();
               }

               this.xxxxxCorzFlag = false;
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private String formatText(String formatText, float corzLicenseLimit) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      if (!formatText.isEmpty() && !(fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText) <= corzLicenseLimit)) {
         int start = 0;

         while (start < formatText.length() && fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText.substring(start)) > corzLicenseLimit) {
            start++;
         }

         return formatText.substring(start);
      } else {
         return formatText;
      }
   }

   private void render(DrawContext context, String corzLicenseText, int corzLicenseValue, float corzLicenseValue2, float priceValue, float corzLicenseLimit, String corzLicenseText2) {
      if (!(corzLicenseLimit <= 0.002F)) {
         if (corzLicenseValue == -7630437) {
            float scaleValue = MatrixStackUtilsAmount.getAmount(corzLicenseValue2 - MatrixStackUtilsAmount.getAmount(corzLicenseText, 0.82F) / 2.0F);
            MatrixStackUtilsAmount.handlePushScale(corzLicenseText + corzLicenseText2, scaleValue, priceValue, 0.82F, MatrixStackUtilsAmount.getValue(corzLicenseValue, corzLicenseLimit));
         } else {
            float corzLicenseValue5 = MatrixStackUtilsAmount.getAmount(corzLicenseValue2 - MatrixStackUtilsAmount.getAmountForString(corzLicenseText) / 2.0F);
            MatrixStackUtilsAmount.getAmount(
               context,
               MatrixStackUtilsAmount.getValue(corzLicenseValue, corzLicenseLimit),
               MatrixStackUtilsAmount.getValue(CardMatrixUtils.getValue(corzLicenseValue, -1, 0.22), corzLicenseLimit),
               corzLicenseText,
               corzLicenseValue5,
               priceValue,
               corzLicenseLimit >= 1.0F
            );
         }
      }
   }

   private void render(DrawContext context, int corzLicenseValue, int corzLicenseValue2, boolean corzLicenseFlag, int matrixStackLimit, int scaledY, long repeatValue) {
      int x = corzLicenseValue + 150;
      int xxxxxFocusValue = corzLicenseValue + 18;
      short releaseRightBound = 264;
      this.handlePopTranslate(
         0,
         () -> {
            float y = corzLicenseValue2 + 18 + 14.0F;
            MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
               context, x - 1.0F, y - 1.0F, 2.0F, 2.0F, 1.0F, CardMatrixUtils.getReleaseRight(0.2 + 0.06 * getAmount(3.6)), 30.0F, 1.0F
            );
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
               context, x, y, 0.82F + 0.18F * MatrixStackUtilsAmount.getAmount((this.translateAmount - 70.0F) / 380.0F, 1.8F)
            );
            MatrixPeekUtilsIdentifier.handle(
               x - 14.0F, corzLicenseValue2 + 18, 28.0F, 28.0F, identifier, new Color(-920587, true), MatrixPeekUtilsRender.getMatrixStack()
            );
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      );
      this.handlePopTranslate(
         1,
         () -> {
            MatrixStackUtilsAmount.getAmountForDrawContextString(
               context, "Corz Client", MatrixStackUtilsAmount.getAmount(x - MatrixStackUtilsAmount.getAmount("Corz Client", 1.22F) / 2.0F), corzLicenseValue2 + 54, 14.0F
            );
            this.handle(corzLicenseFlag ? "Paste your key to unlock the client." : "License", x, corzLicenseValue2 + 69, -7630437);
         }
      );
      int f2;
      if (corzLicenseFlag) {
         int brownValue = corzLicenseValue2 + 91;
         int corzLicenseValue12 = brownValue + 24 + 6;
         this.handlePopTranslate(2, () -> this.renderXxxxxFocus(context, xxxxxFocusValue, brownValue, releaseRightBound, matrixStackLimit, scaledY));
         this.handlePopTranslate(
            3,
            () -> MatrixStackUtilsAmount.getAmount(
               context,
               this.xxxxxFocusHits,
               this.field,
               "activate",
               this.activate ? "Activating..." : "Activate",
               null,
               xxxxxFocusValue + releaseRightBound,
               corzLicenseValue12,
               24.0F,
               releaseRightBound,
               matrixStackLimit,
               scaledY,
               !this.activate,
               this::updateLicenseContinue
            )
         );
         this.handlePopTranslate(4, () -> this.renderSecureActivation(context, x, corzLicenseValue12 + 24 + 8, repeatValue));
         f2 = corzLicenseValue2 + 175;
      } else {
         String corzLicenseText = LicenseRuntime.getLicenseOwnerLabel();
         this.handlePopTranslate(2, () -> this.render(context, corzLicenseText, true, (float)x, (float)(corzLicenseValue2 + 91)));
         this.handlePopTranslate(3, () -> this.handle("Verified securely on this device.", x, corzLicenseValue2 + 91 + 22 + 8, -7630437));
         f2 = corzLicenseValue2 + 143;
      }

      int matrixStackValue = f2 + 9;
      this.handlePopTranslate(5, () -> {
         MatrixPeekUtilsRender.renderMatrixPeek(context, xxxxxFocusValue, f2, releaseRightBound, MatrixPeekUtilsRender.getAmount(), CardMatrixUtils.getValueInt());
         String corzLicenseText2 = "CorzClient";
         String corzLicenseText3;
         if (corzLicenseText2 == null) {
            corzLicenseText3 = "...";
         } else if (corzLicenseText2.length() > 10) {
            corzLicenseText3 = corzLicenseText2;
            boolean corzLicenseFlag3 = false;
            byte corzLicenseValue14 = 10;
         } else {
            corzLicenseText3 = corzLicenseText2;
         }

         String scaleText = corzLicenseText3;
         this.handle("Device id", scaleText, xxxxxFocusValue, matrixStackValue, releaseRightBound);
         this.handle("Version", LicenseRuntime.getClientVersion(), xxxxxFocusValue, matrixStackValue + 14, releaseRightBound);
      });
      this.handlePopTranslate(
         6,
         () -> MatrixStackUtilsAmount.getAmount(
            context, this.xxxxxFocusHits, this.field, "close", "Close", xxxxxFocusValue + releaseRightBound, matrixStackValue + 28 + 10, 18.0F, matrixStackLimit, scaledY, false, this::updateTimeFlag
         )
      );
   }

   public CorzLicense(Screen screen, boolean flag, boolean flag2) {
      super(Text.literal("Corz License"));
      this.licenseCorzFlag = false;
      this.stringBuilder = new StringBuilder();
      this.focus = true;
      this.xxxxxCorzFlag = false;
      this.activate = false;
      this.corzLicenseValue = System.nanoTime();
      this.timeMillis = -1L;
      this.secureActivationFlag = false;
      this.licenseContinueText = null;
      this.xxxxxFocusHits = new Hits();
      this.field = new Anims();
      this.xxxxxFocusClearEdit = new ClearEdit();
      this.translateAmount = 0.0F;
      this.introFadeAlpha = 1.0F;
      this.layoutScale = 1.0F;
      this.lastNanoTime = 0L;
      this.corzLicenseActive = false;
      this.secureActivationText = null;
      this.secureText = null;
      this.secureActivationValue = 0;
      this.secureRenderValue = 0;
      this.lastNanoTimeSecureActivation = 0L;
      this.screen = screen;
      this.resolutionRestoreEnabled = flag;
      this.activationRequired = flag2;
      this.pressedActive = !flag2;
      DeviceIdentity.ensureDeviceId();
   }

   private void updateSecureActivation() {
      this.secureActivationFlag = false;
      this.licenseContinueText = null;
      this.xxxxxFocusClearEdit.updateTime();
   }

   private void updateBuilder() {
      for (int i = 0; i < this.stringBuilder.length(); i++) {
         this.stringBuilder.setCharAt(i, '\u0000');
      }

      this.stringBuilder.setLength(0);
   }

   @Override
   public boolean mouseClicked(Click click, boolean mouseClickedFlag) {
      if (this.timeMillis <= 0L && this.lastNanoTime == 0L) {
         if (this.xxxxxFocusHits.canAnimsPress(this.getMin(click.x(), this.width), this.getMin(click.y(), this.height))) {
            return true;
         } else if (this.pressedActive) {
            return true;
         } else {
            this.focus = false;
            this.xxxxxCorzFlag = false;
            return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public void render(DrawContext context, int corzLicenseValue, int corzLicenseValue2, float corzLicenseValue3) {
      MatrixClearUtils.updateTimeAmount();
      MatrixEspUtils.updateFlagTime();
      CorzClient.fontManager.setMatrixStack(this.matrixStack);
      MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
      CorzRoundedUtils.panelOverlayAmount = 1.0F;
      long timeMillis = System.currentTimeMillis();
      long nanoTime = System.nanoTime();
      if (this.lastNanoTime != 0L && (float)(nanoTime - this.lastNanoTime) / 1000000.0F >= 120.0F) {
         this.handleScreenResolution(this.corzLicenseActive);
      } else {
         if (this.lastNanoTime == 0L && this.timeMillis < 0L && !this.pressedActive && (!this.activationRequired || this.secureActivationFlag)) {
            this.timeMillis = timeMillis;
            this.activate = false;
            this.updateBuilder();
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(soundEvent, 1.0F, 1.0F));
         }

         if (this.timeMillis > 0L && timeMillis - this.timeMillis > 1500L) {
            this.close();
         } else {
            boolean corzLicenseFlag = this.lastNanoTime != 0L;
            this.translateAmount = (float)(nanoTime - this.corzLicenseValue) / 1000000.0F;
            float matrixStackValue = corzLicenseFlag ? (float)(nanoTime - this.lastNanoTime) / 1.2E8F : this.translateAmount / 320.0F;
            float corzLicenseFactor = this.timeMillis > 0L ? 1.0F - getAmount((float)(timeMillis - this.timeMillis - 1200L) / 300.0F) : 1.0F;
            boolean pressedActive2 = !this.pressedActive;
            int corzLicenseDivisor = (pressedActive2 ? 175 : 143) + 79;
            this.layoutScale = Math.min(1.0F, Math.min((this.width - 16) / 300.0F, (float)(this.height - 16) / corzLicenseDivisor));
            int peekValue = (this.width - 300) / 2;
            int corzLicenseValue6 = (this.height - corzLicenseDivisor) / 2;
            boolean corzLicenseFlag3 = !corzLicenseFlag && this.timeMillis <= 0L;
            int matrixStackLimit = corzLicenseFlag3 ? this.getMin(corzLicenseValue, this.width) : -4000;
            int scaledY = this.getMin(corzLicenseValue2, this.height);
            float cardMatrixFactor = this.timeMillis > 0L ? MatrixStackUtilsAmount.getAmountForFloat((float)(timeMillis - this.timeMillis) / 260.0F) : 0.0F;
            this.introFadeAlpha = this.timeMillis > 0L ? 1.0F - MatrixStackUtilsAmount.getMax((float)(timeMillis - this.timeMillis) / 140.0F) : 1.0F;
            MatrixStackUtilsAmount.render(context, this.width, this.height, 0.78F * MatrixStackUtilsAmount.getAmountFloat(matrixStackValue, corzLicenseFlag) * corzLicenseFactor);
            this.xxxxxFocusHits.update();
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
               context, this.width / 2.0F, this.height / 2.0F, MatrixStackUtilsAmount.getAmount(matrixStackValue, corzLicenseFlag) * (0.97F + 0.03F * corzLicenseFactor) * this.layoutScale
            );
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(MatrixStackUtilsAmount.getAmountForFloatFlag(matrixStackValue, corzLicenseFlag) * corzLicenseFactor);

            try {
               MatrixStackUtilsAmount.render(
                  context,
                  (float)peekValue,
                  (float)corzLicenseValue6,
                  300.0F,
                  (float)corzLicenseDivisor,
                  CardMatrixUtils.getValue(MatrixStackUtilsAmount.getValue(), -16050415, cardMatrixFactor),
                  CardMatrixUtils.getValue(352321535, MatrixStackUtilsAmount.getValue(-12922502, 0.42F), cardMatrixFactor)
               );
               TwoDidUtils.renderMinPeek(context, 2, peekValue, corzLicenseValue6, 300.0F, 97.0F, 10.0F, 0.85F * (1.0F - cardMatrixFactor));
               if (this.introFadeAlpha > 0.002F) {
                  this.render(context, peekValue, corzLicenseValue6, pressedActive2, matrixStackLimit, scaledY, timeMillis);
               }

               if (this.timeMillis > 0L) {
                  this.renderTime(context, peekValue, corzLicenseValue6, corzLicenseDivisor, timeMillis);
               }
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            }

            super.render(context, corzLicenseValue, corzLicenseValue2, corzLicenseValue3);
         }
      }
   }

   private void updateXxxxxFocus() {
      this.focus = true;
      this.xxxxxCorzFlag = false;
      this.xxxxxFocusClearEdit.updateTime();
   }

   public CorzLicense(Screen screen) {
      this(screen, false);
   }

   private void handlePopTranslate(float translateValue, Runnable runnable) {
      if (!(translateValue <= 0.002F)) {
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(0.0F, (1.0F - translateValue) * 4.0F, 0.0F);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(translateValue);

         try {
            runnable.run();
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            matrixStack.pop();
         }
      }
   }

   private void handlePopTranslate(int translateValue, Runnable runnable) {
      float translateFactor = MatrixStackUtilsAmount.getAmountForFloat((this.translateAmount - 70.0F - translateValue * 38.0F) / 230.0F);
      float panelOverlayLimit = translateFactor * this.introFadeAlpha;
      if (!(panelOverlayLimit <= 0.002F)) {
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(0.0F, (1.0F - translateFactor) * 4.0F, 0.0F);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            runnable.run();
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            matrixStack.pop();
         }
      }
   }

   @Override
   public boolean charTyped(CharInput charInput) {
      if (this.timeMillis > 0L || this.pressedActive || this.lastNanoTime != 0L) {
         return true;
      } else if (!this.focus) {
         return false;
      } else {
         char codepoint = (char)charInput.codepoint();
         if (codepoint >= ' ' && codepoint != 127) {
            if (this.xxxxxCorzFlag) {
               this.updateBuilder();
               this.xxxxxCorzFlag = false;
            }

            if (this.stringBuilder.length() < 128) {
               this.stringBuilder.append(codepoint);
               this.updateSecureActivation();
               return true;
            }
         }

         return false;
      }
   }

   private static float getAmount(float corzLicenseFactor) {
      corzLicenseFactor = MatrixStackUtilsAmount.getMax(corzLicenseFactor);
      return corzLicenseFactor * corzLicenseFactor * (3.0F - 2.0F * corzLicenseFactor);
   }

   private void render(DrawContext context, String corzLicenseText, boolean corzLicenseFlag, float corzLicenseValue, float corzLicenseValue2) {
      int matrixStackValue = corzLicenseFlag ? -12922502 : -1750963;
      float materialFactor = 6.0F;
      float corzLicenseValue5 = 6.0F;
      float corzLicenseValue6 = 10.0F;
      float corzLicenseValue7 = 12.0F;
      float corzLicenseValue8 = corzLicenseValue6 + materialFactor + corzLicenseValue5 + (float)Math.ceil(MatrixStackUtilsAmount.getAmount3(corzLicenseText)) + corzLicenseValue7;
      float matrixPeekValue = MatrixStackUtilsAmount.getAmount(corzLicenseValue - corzLicenseValue8 / 2.0F);
      float corzLicenseValue10 = getAmount(corzLicenseFlag ? 2.4 : 3.4);
      MatrixPeekUtilsRender.render(
         context, matrixPeekValue, corzLicenseValue2, corzLicenseValue8, 22.0F, 11.0F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.13F), MatrixStackUtilsAmount.getValue(matrixStackValue, 0.42F)
      );
      float corzLicenseValue11 = matrixPeekValue + corzLicenseValue6;
      float configsLikeValue = corzLicenseValue2 + (22.0F - materialFactor) / 2.0F + 0.5F;
      MatrixPeekUtilsRender.renderMatrixPeek(
         context, corzLicenseValue11, configsLikeValue, materialFactor, materialFactor, materialFactor / 2.0F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.3F + 0.34F * corzLicenseValue10), 3.0F + 2.5F * corzLicenseValue10, 0.8F
      );
      MatrixPeekUtilsRender.renderMatrixPeek(context, corzLicenseValue11, configsLikeValue, materialFactor, materialFactor, materialFactor / 2.0F, matrixStackValue);
      MatrixStackUtilsAmount.handle(
         corzLicenseText,
         corzLicenseValue11 + materialFactor + corzLicenseValue5,
         MatrixStackUtilsAmount.getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown(Math.round(corzLicenseValue2), 22),
         CardMatrixUtils.getValue(matrixStackValue, -1, 0.3)
      );
   }

   private int getMin(double corzLicenseValue, int corzLicenseValue2) {
      return (int)Math.round(corzLicenseValue2 / 2.0 + (corzLicenseValue - corzLicenseValue2 / 2.0) / this.layoutScale);
   }

   public CorzLicense(Screen screen, boolean corzLicenseFlag) {
      this(screen, corzLicenseFlag, false);
   }

   private void updateTimeFlag() {
      if (this.lastNanoTime == 0L) {
         this.lastNanoTime = System.nanoTime();
         this.corzLicenseActive = this.resolutionRestoreEnabled;
      }
   }

   private void handle(String corzLicenseText, String scaleText, int corzLicenseValue, int matrixStackValue, int releaseRightBound) {
      float priceValue = MatrixStackUtilsAmount.getAmount(matrixStackValue, 14.0F, 0.82F);
      MatrixStackUtilsAmount.handlePushScale(corzLicenseText, corzLicenseValue, priceValue, 0.82F, -7630437);
      MatrixStackUtilsAmount.handlePushScale(scaleText, MatrixStackUtilsAmount.getAmount(corzLicenseValue + releaseRightBound - MatrixStackUtilsAmount.getAmount(scaleText, 0.82F)), priceValue, 0.82F, -920587);
   }

   private static float getAmount(double corzLicenseDivisor) {
      return (float)((Math.sin(System.nanoTime() / 1.0E9 * Math.PI * 2.0 / corzLicenseDivisor) + 1.0) / 2.0);
   }

   private void handleBuilder(String builderText) {
      for (int i = 0; i < builderText.length() && this.stringBuilder.length() < 128; i++) {
         char charAt = builderText.charAt(i);
         if (charAt >= ' ' && charAt != 127) {
            this.stringBuilder.append(charAt);
         }
      }
   }

   private void handle(String corzLicenseText, float corzLicenseValue, float matrixStackValue, int corzLicenseValue3) {
      MatrixStackUtilsAmount.handlePushScale(
         corzLicenseText,
         MatrixStackUtilsAmount.getAmount(corzLicenseValue - MatrixStackUtilsAmount.getAmount(corzLicenseText, 0.82F) / 2.0F),
         MatrixStackUtilsAmount.getAmount(matrixStackValue, 10.0F, 0.82F),
         0.82F,
         corzLicenseValue3
      );
   }

   @Override
   public void renderBackground(DrawContext context, int backgroundValue, int backgroundValue2, float backgroundValue3) {
   }

   @Override
   public void close() {
      this.handleScreenResolution(this.resolutionRestoreEnabled);
   }
}
