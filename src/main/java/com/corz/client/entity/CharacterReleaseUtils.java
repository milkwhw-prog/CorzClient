package com.corz.client.entity;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.gui.MatrixMatricesScreen;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.ClampMatrixUtils.Overlay;
import com.corz.client.render.ClampMatrixUtils.Panel;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixIdentifierUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.categories.visuals.HeatColor;
import com.corz.client.categories.visuals.NameTags;
import com.corz.client.categories.visuals.PlayerESP;
import com.corz.client.categories.visuals.TargetESP;
import ai.auth.Module;
import ai.auth.ModuleRegistry;

public final class CharacterReleaseUtils {
   private static final int UNUSED_ONE = 1;
   private static float overlayFade;
   private static float scaleY;
   private static Object object2Character;
   private static final String[] placeRightTexts = new String[]{"Top", "Bottom", "Left", "Right"};
   private static float modeAmount;
   private static final String[] characterReleaseTexts = new String[]{"Place top", "Place bottom", "Place left", "Place right"};
   private static float characterReleaseAmount;
   private static final String[] skeletonGlowTexts = new String[]{"Outline Glow", "Corner ESP", "Normal ESP", "Health Box", "Skeleton"};
   private static float scaledAmount;
   private static float removeHintAlpha;
   private static float clampAmount;
   private static final String[] skeletonTagTexts = new String[]{"Glow", "Corners", "Filled", "Health Box", "Skeleton"};
   private static float modeCount;
   private static final CharacterReleaseUtils.Element MODE_TEXT = new CharacterReleaseUtils.Element(
         "Name Tag", 0, "ticket", 0, NameTags.class, null, null, false
      )
      .getModeText("Name Position");
   private static boolean modeActive;
   private static final CharacterReleaseUtils.Element ARMOR_SET_ELEMENT = new CharacterReleaseUtils.Element(
         "Set", 1, "shield", 2, NameTags.class, "Show Armor", CharacterReleaseUtils.MODE_TEXT, false
      )
      .getModeText("Set Position");
   private static float overlayAmount;
   private static final CharacterReleaseUtils.Element EFFECTS_ELEMENT = new CharacterReleaseUtils.Element(
         "Effects", 3, "flask", 8, NameTags.class, "Show Effects", MODE_TEXT, false
      )
      .getModeText("Effects Position");
   private static final CharacterReleaseUtils.Element HEALTH_BAR_ELEMENT = new CharacterReleaseUtils.Element(
         "Health Bar", 2, null, 1, NameTags.class, "Show Health", CharacterReleaseUtils.MODE_TEXT, false
      )
      .getModeText("Health Bar Position");
   private static boolean overlayPanelActive;
   private static final CharacterReleaseUtils.Element HELD_ITEMS = new CharacterReleaseUtils.Element(
      "Held Items", 1, "cube", 3, NameTags.class, "Show Held Items", ARMOR_SET_ELEMENT, false
   );
   private static final CharacterReleaseUtils.Element ESP_ELEMENT = new CharacterReleaseUtils.Element("ESP", 1, "eye", 4, PlayerESP.class, null, null, true);
   private static final CharacterReleaseUtils.Element MODE_MAX = new CharacterReleaseUtils.Element(
      "Tracers", 0, "share", 5, PlayerESP.class, "Tracers", CharacterReleaseUtils.ESP_ELEMENT, false
   );
   private static final CharacterReleaseUtils.Element TARGET = new CharacterReleaseUtils.Element(
      "Target", 1, "target", 6, TargetESP.class, null, null, false
   );
   private static boolean characterReleaseActive;
   private static float hoverBandHeight;
   private static final CharacterReleaseUtils.Element HEAT_BOX = new CharacterReleaseUtils.Element(
      "Heat Box", 1, "gauge", 7, HeatColor.class, null, null, false
   );
   private static float overlayVelocityX;
   private static float addHintAlpha;
   private static final CharacterReleaseUtils.Element[] modeMaxs = new CharacterReleaseUtils.Element[]{
      MODE_TEXT, ARMOR_SET_ELEMENT, CharacterReleaseUtils.EFFECTS_ELEMENT, HEALTH_BAR_ELEMENT, HELD_ITEMS, ESP_ELEMENT, MODE_MAX, TARGET, HEAT_BOX
   };
   private static final CharacterReleaseUtils.Flyer[] modeFlyers = new CharacterReleaseUtils.Flyer[]{
      new CharacterReleaseUtils.Flyer(),
      new CharacterReleaseUtils.Flyer(),
      new CharacterReleaseUtils.Flyer(),
      new CharacterReleaseUtils.Flyer(),
      new CharacterReleaseUtils.Flyer(),
      new CharacterReleaseUtils.Flyer()
   };
   private static CharacterReleaseUtils.Element modeMax;
   private static float selectionProgress;
   private static float characterAmount;
   private static boolean panelReady;
   private static final Panel MATRIX_CLAMP = new Panel() {
      @Override
      public boolean canFlag(float characterReleaseValue, float characterReleaseValue2, int characterReleaseValue3) {
         return CharacterReleaseUtils.canModeFlag(characterReleaseValue, characterReleaseValue2, characterReleaseValue3);
      }

      @Override
      public boolean canFlag(float characterReleaseValue4, float characterReleaseValue5, int characterReleaseValue6, double characterReleaseValue7, double characterReleaseValue8) {
         return CharacterReleaseUtils.isModeMax();
      }

      @Override
      public boolean canFlagForFloatInt(float characterReleaseValue9, float characterReleaseValue10, int characterReleaseValue11) {
         return CharacterReleaseUtils.isModeMaxModeMax3Mode();
      }

      @Override
      public void render(DrawContext context, float elementsPanelValue, float characterReleaseValue13, float characterReleaseValue14) {
         CharacterReleaseUtils.renderElementsPanel(context, elementsPanelValue, characterReleaseValue13);
      }
   };
   private static float overlayPanelAmount;
   private static final String EVERYTHING = "Everything is on";
   private static float panelLeftX;
   private static float panelContentY;
   private static float bandMaxY;
   private static float scaleX;
   private static boolean overlayActive;
   private static final Overlay PANEL_OVERLAY_HANDLER = new Overlay() {
      @Override
      public boolean canFlag(double characterReleaseValue, double characterReleaseValue2) {
         return CharacterReleaseUtils.getAmountMaxsForFloat((float)characterReleaseValue, (float)characterReleaseValue2) != null;
      }

      @Override
      public void handle(double characterReleaseValue3, double characterReleaseValue4, int characterReleaseValue5) {
         CharacterReleaseUtils.handleModeMax((float)characterReleaseValue3, (float)characterReleaseValue4);
      }

      @Override
      public void handleForDoubleInt(double characterReleaseValue6, double characterReleaseValue7, int characterReleaseValue8) {
         CharacterReleaseUtils.handleAmountMode((float)characterReleaseValue6, (float)characterReleaseValue7);
      }

      @Override
      public void render(DrawContext context, float overlayPanelValue) {
         CharacterReleaseUtils.renderOverlayPanel(context, overlayPanelValue);
      }

      @Override
      public boolean canFlag(double characterReleaseValue10, double characterReleaseValue11, int modeValue) {
         return CharacterReleaseUtils.canAmountMode((float)characterReleaseValue10, (float)characterReleaseValue11, modeValue);
      }
   };
   private static final MatrixStack MATRIX_STACK = new MatrixStack();
   private static final float[] clampMatrixTable = new float[2];
   private static final float[] modeTable = new float[2];
   private static CharacterReleaseUtils.Element modeMax3Mode;
   private static float panelTopY;
   private static final int[] colorsTable = new int[128];
   private static boolean uiVisible;
   private static float characterElementsAmount;
   private static final Color[] colors = new Color[128];
   private static int lastFrameIndex = -1;
   private static Identifier identifier;
   private static float overlayAmount2Overlay;
   private static float frameDeltaSeconds;
   private static float scaleXMode;
   private static float scaledHeight;
   private static float clamp = 1.0F;
   private static float clampMatrixAmount = 87.0F;
   private static float clampClampMatrix = 0.8F;
   private static float scaledWidth;
   private static float maxsAmount;
   private static float revealProgress = 1.0F;
   private static float releaseAmount = -1.0F;
   private static final String RELEASE = "Release to remove";
   private static float removeTextWidth = -1.0F;
   private static float everythingCharacterAmount = -1.0F;
   private static final String RELEASE_ADD_TEXT = "Release to add";
   private static float characterEverythingAmount = -1.0F;
   private static final String ELEMENTS = "Elements";
   private static final float[] characterReleaseTable = new float[4];
   private static boolean panelOverlapping;
   private static float collapseProgress;
   private static final float[] placeTable = new float[]{-1.0F, -1.0F, -1.0F, -1.0F};
   private static float subModeProgress;
   private static float overlayVelocityY;
   private static final String DRAG_YOUR_CHARACTER = "Drag onto your character";
   private static final String YOUR_CHARACTER = "your character";
   private static boolean elementsPanelActive;
   private static float scaleYMode;
   private static final float[] characterTable = new float[]{-1.0F, -1.0F, -1.0F, -1.0F};
   private static int characterReleaseValue = -1;
   private static int hoveredElementIndex = 0;
   private static final float[] table = new float[4];
   private static final float[] clampTable = new float[4];
   private static float clamp2Mode;
   private static final float[] PANEL_RECT_BUFFER = new float[4];
   private static float overlayPosX;
   private static final float[] CLAMP_POINT_BUFFER = new float[2];

   private static float getAmountAmounts(float characterReleaseValue, float characterReleaseValue2, float characterReleaseFactor) {
      float characterReleaseFactor2 = characterReleaseFactor > 0.0F ? 1.0F : 0.0F;
      handleAmountScaled(1, PANEL_RECT_BUFFER);
      float characterReleaseFactor3 = PANEL_RECT_BUFFER[1] + PANEL_RECT_BUFFER[3] - scaledAmount;
      if (scaledAmount + characterReleaseValue2 + characterReleaseFactor3 * characterReleaseFactor2 + characterReleaseValue <= scaledHeight - 6.0F) {
         return scaledAmount + characterReleaseValue2 + characterReleaseFactor3 * characterReleaseFactor;
      } else {
         handleAmountScaled(0, PANEL_RECT_BUFFER);
         float characterReleaseFactor4 = panelTopY - PANEL_RECT_BUFFER[1];
         return panelTopY - characterReleaseValue2 - characterReleaseFactor4 * characterReleaseFactor2 - characterReleaseValue >= 6.0F ? panelTopY - characterReleaseValue2 - characterReleaseValue - characterReleaseFactor4 * characterReleaseFactor : Math.min(scaledAmount + characterReleaseValue2 + characterReleaseFactor3 * characterReleaseFactor, scaledHeight - characterReleaseValue - 6.0F);
      }
   }

   public static boolean canAmountFlag(String characterReleaseText, boolean characterReleaseFlag, float[] characterReleaseValues) {
      CharacterReleaseUtils.Element maxsMode = getMaxsMode(characterReleaseText);
      if (maxsMode != null && maxsMode.modeToggle && !(maxsMode.showProgress < 0.5F) && !(maxsMode.characterAmount <= 0.0F)) {
         characterReleaseValues[0] = characterReleaseFlag ? maxsMode.maxsAmount + maxsMode.characterAmount - 6.0F : maxsMode.maxsAmount + (maxsMode.characterAmount - 12.0F) * 0.5F;
         characterReleaseValues[1] = maxsMode.rowTopY + 7.0F;
         return true;
      } else {
         return false;
      }
   }

   private static void updateClampMatrix() {
      Panel clampMatrix = ClampMatrixUtils.getClampMatrix();
      float scaledWidthScaled = scaledWidth * 0.355F;
      float scaledHeightScaled = scaledHeight * 0.3F;
      float clamp = 0.8F;
      if (clampMatrix.panelHeight > 1.0F && ClampMatrixUtils.canAmountMatrix(clampMatrix, clampMatrix.clampMatrixAmount, clampMatrix.panelY, clampMatrixTable)) {
         float clampMatrixBound = clampMatrixTable[0];
         float clampMatrixValue = clampMatrixTable[1];
         if (ClampMatrixUtils.canAmountMatrix(clampMatrix, clampMatrix.clampMatrixAmount, clampMatrix.panelY + clampMatrix.panelHeight, clampMatrixTable)) {
            float clampMatrixValue2 = clampMatrixTable[1];
            scaledWidthScaled = Math.min(clampMatrixBound, clampMatrixTable[0]) - 5.0F;
            scaledHeightScaled = clampMatrixValue + (clampMatrixValue2 - clampMatrixValue) * 0.13F;
            clamp = MathHelper.clamp((clampMatrixValue2 - clampMatrixValue) / clampMatrix.panelHeight * 1.16F, 0.7F, 1.0F);
         }
      }

      clampClampMatrix = clamp;
      MATRIX_CLAMP.clampMatrixAmount = 24.0F;
      MATRIX_CLAMP.panelY = 24.0F;
      MATRIX_CLAMP.matrixAmount = 112.0F;
      MATRIX_CLAMP.panelHeight = Math.min(clampMatrixAmount, Math.max(40.0F, scaledHeight - 48.0F));
      MATRIX_CLAMP.cornerRadius = 10.0F;
      MATRIX_CLAMP.clamp = clamp;
      MATRIX_CLAMP.panelAmount = -11.0F;
      MATRIX_CLAMP.tiltDegrees = 4.0F;
      MATRIX_CLAMP.modernThemeAmount = CardMatrixUtils.isModernFlag() && ThemeCustomizer.booleanValue ? 1.0F : 0.0F;
      MATRIX_CLAMP.modeAmount = scaledWidthScaled - 112.0F * clamp * 0.5F;
      MATRIX_CLAMP.panelCenterY = scaledHeightScaled + MATRIX_CLAMP.panelHeight * clamp * 0.5F;
      MATRIX_CLAMP.clampMatrixActive = !modeActive;
   }

   private static void handle(float characterReleaseValue, float rowTopY, float panelOverlayBound, float hashHuntBound, float characterReleaseValue5, int characterReleaseValue6, int matrixStackValue) {
      float characterReleaseValue8 = MatrixPeekUtilsRender.getAmount();
      handlePanelOverlay(characterReleaseValue, rowTopY, panelOverlayBound, hashHuntBound, characterReleaseValue5, matrixStackValue);
      handlePanelOverlay(characterReleaseValue + characterReleaseValue8, rowTopY + characterReleaseValue8, panelOverlayBound - characterReleaseValue8 * 2.0F, hashHuntBound - characterReleaseValue8 * 2.0F, Math.max(0.0F, characterReleaseValue5 - characterReleaseValue8), characterReleaseValue6);
   }

   private static void renderAmountMode(DrawContext context) {
      if (!(maxsAmount <= 0.01F)) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(maxsAmount * (1.0F - (panelOverlapping ? 0.75F : 0.6F) * getAmount(subModeProgress)));

         try {
            for (CharacterReleaseUtils.Element element : modeMaxs) {
               float modeFactor = getAmount(element.showProgress);
               if (!(modeFactor <= 0.01F) && !(element.characterAmount <= 0.0F)) {
                  float maxsAmount4 = element.maxsAmount;
                  float rowTopY = element.rowTopY;
                  float panelOverlayBound = element.characterAmount;
                  boolean modeFlag = modeMax == null && modeMax3Mode == null;
                  boolean modeFlag2 = element.modeToggle
                     && element.showProgress > 0.5F
                     && scaleX >= maxsAmount4
                     && scaleX <= maxsAmount4 + panelOverlayBound
                     && scaleY >= rowTopY
                     && scaleY <= rowTopY + 14.0F;
                  boolean modeFlag3 = modeFlag2 && scaleX >= maxsAmount4 + panelOverlayBound - 12.0F;
                  boolean modeFlag4 = modeMax3Mode == element && elementsPanelActive;
                  element.selectionBlend = getAmount(element.selectionBlend, (!modeFlag2 || !modeFlag) && !modeFlag4 ? 0.0F : 1.0F, 16.0F);
                  element.hoverGlowBlend = getAmount(element.hoverGlowBlend, (!modeFlag3 || !modeFlag) && (!modeFlag4 || !characterReleaseActive) ? 0.0F : 1.0F, 16.0F);
                  float modeValue = (0.8F + 0.2F * MatrixStackUtilsAmount.getAmount(element.showProgress, 1.6F))
                     * (1.0F + 0.07F * getSin(element.flashLevel))
                     * (1.0F - 0.03F * (modeFlag4 ? 1.0F : 0.0F));
                  float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(modeFactor * (1.0F - 0.8F * element.overlayBlend));
                  boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, maxsAmount4 + panelOverlayBound, rowTopY + 7.0F, modeValue);

                  try {
                     handlePanelOverlay(maxsAmount4 + 1.0F, rowTopY + 2.0F, panelOverlayBound - 2.0F, 14.0F, 5.0F, 1493172224, 5.0F, 0.5F, true);
                     handle(
                        maxsAmount4,
                        rowTopY,
                        panelOverlayBound,
                        14.0F,
                        5.0F,
                        CardMatrixUtils.getValue(-183495911, -15065561, element.selectionBlend),
                        CardMatrixUtils.getReleaseRight(0.34 + 0.4 * element.selectionBlend)
                     );
                     handle(
                        element.getIdentifierText(),
                        maxsAmount4 + 4.5F,
                        rowTopY + 3.0F,
                        8.0F,
                        CardMatrixUtils.getValue(CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.2), -1, 0.25 * element.selectionBlend)
                     );
                     handlePushScale(
                        element.formatTextHidden(),
                        maxsAmount4 + 16.0F,
                        getAmountForFloat(rowTopY, 14.0F, 0.82F),
                        0.82F,
                        CardMatrixUtils.getValue(-5854026, -920587, 0.6 + 0.4 * element.selectionBlend)
                     );
                     float panelOverlayValue = maxsAmount4 + panelOverlayBound - 12.0F + 0.5F;
                     float amountAmounts = rowTopY + 2.0F;
                     handlePanelOverlay(panelOverlayValue, amountAmounts, 10.0F, 10.0F, 5.0F, MatrixStackUtilsAmount.getValue(-1750963, 0.2F * element.hoverGlowBlend));
                     handle(
                        getIdentifierClose(),
                        panelOverlayValue + 1.5F,
                        amountAmounts + 1.5F,
                        7.0F,
                        CardMatrixUtils.getValue(-5854026, CardMatrixUtils.getValue(-1750963, -1, 0.3), element.hoverGlowBlend)
                     );
                  } finally {
                     MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
                     CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
                  }
               }
            }
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static void handleAmountOverlay(float overlayValue, float overlayValue2) {
      float exp = (float)Math.exp(-24.0F * frameDeltaSeconds);
      float overlayValue3 = overlayPosX - overlayValue;
      float overlayValue4 = (overlayVelocityX + 24.0F * overlayValue3) * frameDeltaSeconds;
      overlayPosX = overlayValue + (overlayValue3 + overlayValue4) * exp;
      overlayVelocityX = (overlayVelocityX - 24.0F * overlayValue4) * exp;
      float overlayValue5 = overlayAmount2Overlay - overlayValue2;
      float overlayValue6 = (overlayVelocityY + 24.0F * overlayValue5) * frameDeltaSeconds;
      overlayAmount2Overlay = overlayValue2 + (overlayValue5 + overlayValue6) * exp;
      overlayVelocityY = (overlayVelocityY - 24.0F * overlayValue6) * exp;
   }

   public static void updateClampMatrixModeActive() {
      if (!modeActive) {
         if (MATRIX_CLAMP.matrixAmount <= 0.0F) {
            MATRIX_CLAMP.clampMatrixAmount = 24.0F;
            MATRIX_CLAMP.panelY = 24.0F;
            MATRIX_CLAMP.matrixAmount = 112.0F;
            MATRIX_CLAMP.panelHeight = clampMatrixAmount;
            MATRIX_CLAMP.cornerRadius = 10.0F;
            MATRIX_CLAMP.clamp = clampClampMatrix;
            MATRIX_CLAMP.panelAmount = -11.0F;
            MATRIX_CLAMP.tiltDegrees = 4.0F;
         }

         ClampMatrixUtils.handleClampMatrixForPanel(MATRIX_CLAMP);
         ClampMatrixUtils.handle(PANEL_OVERLAY_HANDLER);
      }
   }

   private static void handleFlag(CharacterReleaseUtils.Element element, float characterReleaseValue, float characterReleaseValue2) {
      element.handleModeToggle(false);
      if (element.isModeToggleModeMax()) {
         handleAmountMode(element, 2, characterReleaseValue, characterReleaseValue2);
      } else {
         element.overlayActive = true;
         element.modeActive = true;
         handleAmountMode(element, 1, characterReleaseValue, characterReleaseValue2);
      }
   }

   private static void updateModeMax() {
      CharacterReleaseUtils.Element modeMax3 = modeMax;
      modeMax = null;
      modeMax3Mode = null;
      if (modeMax3 != null) {
         handleAmountMode(modeMax3, overlayActive ? 2 : 1, overlayPosX, overlayAmount2Overlay);
      }
   }

   private static boolean isModeMax() {
      if (!modeActive && modeMax3Mode != null && !elementsPanelActive && modeMax == null) {
         float scaleX = (float)ClampMatrixUtils.getScaleX();
         float scaleY = (float)ClampMatrixUtils.getScaleY();
         float modeFactor = scaleX - scaleXMode;
         float modeFactor2 = scaleY - scaleYMode;
         if (modeFactor * modeFactor + modeFactor2 * modeFactor2 >= 9.0F) {
            handleAmountOverlay(modeMax3Mode, false, modeMax3Mode.elementY, modeMax3Mode.clampAmount);
         }

         return true;
      } else {
         return true;
      }
   }

   private static void handlePanelOverlay(float panelOverlayValue, float amountAmounts, float panelOverlayBound, float hashHuntBound, float wrapDegreesBound, int panelOverlayValue3, float panelOverlayValue4, float panelOverlayValue5, boolean panelOverlayFlag) {
      float panelOverlayValue6 = (panelOverlayValue3 >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount;
      if (!(panelOverlayBound <= 0.0F) && !(hashHuntBound <= 0.0F) && !(panelOverlayValue6 <= 0.003F)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         float max7 = Math.max(2.0F, panelOverlayValue4 + 2.0F);
         CorzRoundedUtils.handle(
            getPositionMatrix(),
            panelOverlayValue - max7,
            amountAmounts - max7,
            panelOverlayValue + panelOverlayBound + max7,
            amountAmounts + hashHuntBound + max7,
            panelOverlayValue / framebufferHeight,
            amountAmounts / framebufferHeight,
            panelOverlayBound / framebufferHeight,
            hashHuntBound / framebufferHeight,
            Math.min(wrapDegreesBound, Math.min(panelOverlayBound, hashHuntBound) * 0.5F) / framebufferHeight,
            (panelOverlayValue3 >> 16 & 0xFF) / 255.0F,
            (panelOverlayValue3 >> 8 & 0xFF) / 255.0F,
            (panelOverlayValue3 & 0xFF) / 255.0F,
            panelOverlayValue6,
            panelOverlayValue4 / framebufferHeight,
            panelOverlayValue5,
            panelOverlayFlag
         );
      }
   }

   private static void updateAmountMode() {
      int frame = ClampMatrixUtils.getFrame();
      if (frame != lastFrameIndex) {
         boolean modeFlag = lastFrameIndex < 0 || frame - lastFrameIndex > 3;
         lastFrameIndex = frame;
         frameDeltaSeconds = (float)MatrixClearUtils.getAmount();
         modeCount = modeCount + frameDeltaSeconds;
         if (modeCount > 3600.0F) {
            modeCount -= 3600.0F;
         }

         MinecraftClient instance = MinecraftClient.getInstance();
         scaledWidth = instance.getWindow().getScaledWidth();
         scaledHeight = instance.getWindow().getScaledHeight();
         scaleX = (float)ClampMatrixUtils.getScaleX();
         scaleY = (float)ClampMatrixUtils.getScaleY();
         ClientPlayerEntity player = instance.player;
         clamp = player == null ? 1.0F : MathHelper.clamp(player.getHealth() / Math.max(1.0F, player.getMaxHealth()), 0.0F, 1.0F);
         if (modeFlag) {
            modeMax3Mode = null;
            modeMax = null;
            panelReady = false;
            overlayFade = 0.0F;
            selectionProgress = 0.0F;
            addHintAlpha = 0.0F;
            removeHintAlpha = 0.0F;
            revealProgress = 1.0F;

            for (CharacterReleaseUtils.Flyer flyer : modeFlyers) {
               flyer.modeActive = false;
            }
         }

         if (modeMax != null) {
            boolean modeFlag2 = overlayActive ? ClampMatrixUtils.canHandler(PANEL_OVERLAY_HANDLER) : ClampMatrixUtils.canClampMatrix(MATRIX_CLAMP);
            if (!modeFlag2 || MatrixMatricesScreen.isMatrixMatricesActive()) {
               updateModeMax();
            }
         }

         if (modeMax3Mode != null && (elementsPanelActive ? !ClampMatrixUtils.canHandler(PANEL_OVERLAY_HANDLER) : !ClampMatrixUtils.canClampMatrix(MATRIX_CLAMP))) {
            modeMax3Mode = null;
         }

         updateAmountCharacter();
         boolean modeFlag3 = false;
         int modeCounter = 0;

         for (CharacterReleaseUtils.Element element : modeMaxs) {
            element.updateModeTexts();
            element.modeToggle = element.isModeToggleModeMax();
            element.modeToggleMode = element.isModeToggle();
            if (modeFlag) {
               element.toggleModeProgress = element.modeToggleMode ? 1.0F : 0.0F;
               element.showProgress = 0.0F;
               element.showDelaySeconds = element.modeToggle ? 0.12F + 0.05F * modeCounter++ : 0.0F;
               element.modeActive = false;
               element.overlayActive = false;
               element.activeProgress = 0.0F;
               element.overlayBlend = 0.0F;
               element.pulseProgress = 0.0F;
               element.flashLevel = 0.0F;
               element.hoverProgress = 0.0F;
               element.highlightAmount = 0.0F;
               element.characterAmount = -1.0F;
            }

            element.toggleModeProgress = getAmount(element.toggleModeProgress, element.modeToggleMode ? 1.0F : 0.0F, 10.0F);
            if (element.showDelaySeconds > 0.0F && element.modeToggle && ClampMatrixUtils.isFlag() && ClampMatrixUtils.getPanelsFrame() > 0.9F) {
               element.showDelaySeconds = element.showDelaySeconds - frameDeltaSeconds;
            }

            element.showProgress = getAmount(element.showProgress, element.modeToggle && element.showDelaySeconds <= 0.0F ? 1.0F : 0.0F, 12.0F);
            if (element.modeToggleMode) {
               modeFlag3 = true;
            }

            boolean modeFlag4 = modeMax == element && !overlayActive;
            if (element.modeActive && !modeFlag4 && !canModeMax(element, 1) && (element.modeToggleMode || element.toggleModeProgress < 0.02F)) {
               element.modeActive = false;
            }

            boolean modeFlag5 = modeMax == element && overlayActive;
            if (element.overlayActive && !modeFlag5 && !canModeMax(element, 2) && (element.modeToggle || element.showProgress < 0.02F)) {
               element.overlayActive = false;
            }

            element.activeProgress = getAmount(element.activeProgress, element.modeActive ? 1.0F : 0.0F, 20.0F);
            element.overlayBlend = getAmount(element.overlayBlend, element.overlayActive ? 1.0F : 0.0F, 20.0F);
            if (element.pulseProgress > 0.0F) {
               element.pulseProgress = Math.max(0.0F, element.pulseProgress - frameDeltaSeconds / 0.28F);
            }

            if (element.flashLevel > 0.0F) {
               element.flashLevel = Math.max(0.0F, element.flashLevel - frameDeltaSeconds / 0.28F);
            }
         }

         collapseProgress = modeFlag ? (modeFlag3 ? 0.0F : 1.0F) : getAmount(collapseProgress, modeFlag3 ? 0.0F : 1.0F, 9.0F);
         characterElementsAmount = getAmount(characterElementsAmount, modeMax != null && !overlayActive ? 1.0F : 0.0F, 12.0F);
         float amount9 = 59.0F;

         for (CharacterReleaseUtils.Element modeMaxsItem : modeMaxs) {
            float modeFactor = getAmount(modeMaxsItem.toggleModeProgress);
            modeMaxsItem.modeAmount = amount9;
            modeMaxsItem.elementWidth = modeMaxsItem.getModeFlag() * modeFactor;
            amount9 += (modeMaxsItem.getModeFlag() + 4.0F) * modeFactor;
         }

         panelContentY = amount9;
         clampMatrixAmount = amount9 - 24.0F + 52.0F * getAmount(collapseProgress) + 3.0F;
         updateClampMatrix();
         float modeValue = 31.0F;
         float modeValue2 = 98.0F;

         for (CharacterReleaseUtils.Element element3 : modeMaxs) {
            if (ClampMatrixUtils.canAmountMatrix(MATRIX_CLAMP, modeValue + modeValue2 * 0.5F, element3.modeAmount + Math.max(element3.elementWidth, 2.0F) * 0.5F, clampMatrixTable)) {
               element3.elementY = clampMatrixTable[0];
               element3.clampAmount = clampMatrixTable[1];
            } else if (modeFlag) {
               element3.elementY = MATRIX_CLAMP.modeAmount;
               element3.clampAmount = MATRIX_CLAMP.panelCenterY;
            }
         }

         uiVisible = ClampMatrixUtils.isFlag();
         if (uiVisible) {
            float deltaAmount = ClampMatrixUtils.getLerpAmount() - ClampMatrixUtils.getLerpedPosX();
            float deltaAmount2 = ClampMatrixUtils.getAmount() - ClampMatrixUtils.getLerpedPosZ();
            panelLeftX = ClampMatrixUtils.getLerpedPosX() - deltaAmount * 0.08F;
            modeAmount = ClampMatrixUtils.getLerpAmount() + deltaAmount * 0.08F;
            panelTopY = ClampMatrixUtils.getLerpedPosZ() - deltaAmount2 * 0.11F;
            scaledAmount = ClampMatrixUtils.getAmount() + deltaAmount2 * 0.02F;
            overlayPanelActive = true;
         }

         float amount11 = 0.0F;
         float characterAmount16 = 0.0F;

         for (CharacterReleaseUtils.Element element4 : modeMaxs) {
            if (!(element4.showProgress <= 0.004F) || element4.modeToggle) {
               float thumbAmount = element4.getAmount();
               element4.characterAmount = element4.characterAmount < 0.0F ? thumbAmount : getAmount(element4.characterAmount, thumbAmount, 16.0F);
               element4.elementAmount = amount11;
               amount11 += 17.0F * getAmount(element4.showProgress);
               if (element4.characterAmount > characterAmount16) {
                  characterAmount16 = element4.characterAmount;
               }
            }
         }

         clampAmount = amount11;
         hoverBandHeight = characterAmount16;
         if (uiVisible) {
            float pilotStatusBound = panelTopY + 4.0F;
            float long2floatopenhashmap = MODE_TEXT.toggle instanceof NameTags nameTags ? nameTags.getAmountTime(Float.NaN) : Float.NaN;
            if (!Float.isNaN(long2floatopenhashmap)) {
               pilotStatusBound = Math.max(pilotStatusBound, long2floatopenhashmap + 8.0F);
            }

            float modeBound = panelLeftX - 3.0F - getMax(2) - 6.0F;
            float max2 = Math.max(6.0F, scaledHeight - clampAmount - 6.0F);
            float max3 = Math.max(modeBound, characterAmount16 + 6.0F);
            float clamp2 = MathHelper.clamp(pilotStatusBound, 6.0F, max2);
            panelOverlapping = modeBound < characterAmount16 + 6.0F || !Float.isNaN(long2floatopenhashmap) && long2floatopenhashmap + 8.0F > max2;
            if (!panelReady) {
               panelReady = true;
               bandMaxY = max3;
               clamp2Mode = clamp2;
            } else {
               bandMaxY = getAmount(bandMaxY, max3, 14.0F);
               clamp2Mode = getAmount(clamp2Mode, clamp2, 14.0F);
            }
         }

         maxsAmount = getAmount(maxsAmount, uiVisible && panelReady ? 1.0F : 0.0F, 10.0F);

         for (CharacterReleaseUtils.Element element5 : modeMaxs) {
            element5.maxsAmount = bandMaxY - Math.max(0.0F, element5.characterAmount);
            element5.rowTopY = clamp2Mode + element5.elementAmount;
         }

         boolean modeFlag6 = modeMax != null && modeMax.getModeMax() != null && uiVisible;
         boolean modeFlag7 = modeFlag6 && modeMax.isFlag();
         characterReleaseValue = modeFlag6 ? getAmountAmounts(scaleX, scaleY, overlayActive) : -1;
         if (characterReleaseValue >= 0) {
            hoveredElementIndex = characterReleaseValue;
         }

         subModeProgress = getAmount(subModeProgress, modeFlag6 ? 1.0F : 0.0F, 12.0F);
         characterReleaseAmount = getAmount(characterReleaseAmount, modeFlag7 && characterReleaseValue >= 0 ? 1.0F : 0.0F, 16.0F);

         for (int i = 0; i < 4; i++) {
            characterReleaseTable[i] = getAmount(characterReleaseTable[i], characterReleaseValue == i ? 1.0F : 0.0F, 18.0F);
         }

         boolean modeFlag8 = modeMax != null && uiVisible && (modeFlag6 ? characterReleaseValue >= 0 : canAmount(scaleX, scaleY));
         overlayFade = getAmount(overlayFade, modeMax != null && uiVisible && !modeFlag6 ? 1.0F : 0.0F, 12.0F);
         selectionProgress = getAmount(selectionProgress, modeFlag8 && !modeFlag6 ? 1.0F : 0.0F, 16.0F);
         addHintAlpha = getAmount(addHintAlpha, modeMax != null && !overlayActive && modeFlag8 && !modeFlag7 ? 1.0F : 0.0F, 16.0F);
         removeHintAlpha = getAmount(removeHintAlpha, modeMax != null && overlayActive && uiVisible && !modeFlag8 ? 1.0F : 0.0F, 16.0F);
         overlayPanelAmount = getAmount(overlayPanelAmount, modeMax != null && overlayActive && uiVisible && !modeFlag8 ? 1.0F : 0.0F, 16.0F);
         if (revealProgress < 1.0F) {
            revealProgress = Math.min(1.0F, revealProgress + frameDeltaSeconds * 1000.0F / 460.0F);
         }

         if (modeMax != null) {
            handleAmountOverlay(scaleX, scaleY);
            overlayAmount = getAmount(overlayAmount, 1.0F, 22.0F);
         }

         for (CharacterReleaseUtils.Flyer modeFlyersItem : modeFlyers) {
            if (modeFlyersItem.modeActive) {
               handleAmountMode(modeFlyersItem);
            }
         }
      }
   }

   private static void handleAmountOverlay(CharacterReleaseUtils.Element element, boolean flag, float amount, float overlayAmount2) {
      modeMax = element;
      overlayActive = flag;
      overlayPosX = amount;
      overlayAmount2Overlay = overlayAmount2;
      overlayVelocityX = 0.0F;
      overlayVelocityY = 0.0F;
      overlayAmount = 0.0F;
      if (flag) {
         element.overlayActive = true;
      } else {
         element.modeActive = true;
      }
   }

   private static void updateAmountCharacter() {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      if (fontRenderer != object2Character) {
         object2Character = fontRenderer;
         characterAmount = MatrixStackUtilsAmount.getAmount(0.0F, 0.0F, 1.0F);
         releaseAmount = -1.0F;
         removeTextWidth = -1.0F;
         everythingCharacterAmount = -1.0F;
         characterEverythingAmount = -1.0F;
         Arrays.fill(placeTable, -1.0F);
         Arrays.fill(characterTable, -1.0F);

         for (CharacterReleaseUtils.Element element : modeMaxs) {
            element.hiddenAmount = -1.0F;
            element.cachedTextWidth = -1.0F;
            element.characterAmount = -1.0F;
            if (element.modeTable != null) {
               Arrays.fill(element.modeTable, -1.0F);
            }
         }
      }
   }

   private static CharacterReleaseUtils.Element getAmountMaxs(float maxsLimit, float maxsLimit2) {
      float maxsLimit3 = 31.0F;
      float maxsValue = 98.0F;
      if (!(maxsLimit < maxsLimit3) && !(maxsLimit > maxsLimit3 + maxsValue)) {
         for (CharacterReleaseUtils.Element amountMaxs : modeMaxs) {
            if (amountMaxs.modeToggleMode && !(amountMaxs.toggleModeProgress < 0.6F) && maxsLimit2 >= amountMaxs.modeAmount && maxsLimit2 <= amountMaxs.modeAmount + amountMaxs.elementWidth) {
               return amountMaxs;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static boolean canAmountClamp(String clampText, float[] clampValues) {
      CharacterReleaseUtils.Element maxsMode = getMaxsMode(clampText);
      if (maxsMode != null && maxsMode.modeToggleMode && !(maxsMode.toggleModeProgress < 0.6F)) {
         clampValues[0] = maxsMode.elementY;
         clampValues[1] = maxsMode.clampAmount - (maxsMode.modeFlag ? 9.5F * clampClampMatrix : 0.0F);
         return true;
      } else {
         return false;
      }
   }

   private static float getAmount(float characterReleaseValue, float thumbAmount, float ctlValue) {
      float characterReleaseValue4 = (float)MatrixIdentifierUtils.getAmount(characterReleaseValue, thumbAmount, ctlValue, frameDeltaSeconds);
      return Math.abs(characterReleaseValue4 - thumbAmount) < 0.0015F ? thumbAmount : characterReleaseValue4;
   }

   private static float getAmountsPlace(int index) {
      if (placeTable[index] < 0.0F) {
         placeTable[index] = MatrixStackUtilsAmount.getAmount3(placeRightTexts[index]);
      }

      return placeTable[index];
   }

   private static void updateAmountMin() {
      float panelOverlayValue = panelLeftX;
      float amountAmounts = panelTopY;
      float deltaAmount = modeAmount - panelLeftX;
      float hashHuntBound = scaledAmount - panelTopY;
      float characterReleaseValue = 10.0F;
      int releaseRightValue = MatrixEspUtils.releaseRightValue;
      if (overlayFade > 0.01F) {
         float characterReleaseValue2 = 0.5F + 0.5F * MathHelper.sin(modeCount * 3.6F);
         handlePanelOverlay(
            panelOverlayValue,
            amountAmounts,
            deltaAmount,
            hashHuntBound,
            characterReleaseValue,
            MatrixStackUtilsAmount.getValue(releaseRightValue, overlayFade * (0.18F + 0.26F * selectionProgress + 0.06F * characterReleaseValue2)),
            9.0F + 5.0F * selectionProgress,
            0.85F,
            false
         );
         handlePanelOverlay(panelOverlayValue, amountAmounts, deltaAmount, hashHuntBound, characterReleaseValue, MatrixStackUtilsAmount.getValue(releaseRightValue, overlayFade * 0.07F * selectionProgress));
         handlePanelOverlay(
            panelOverlayValue, amountAmounts, deltaAmount, hashHuntBound, characterReleaseValue, 1.0F, MatrixStackUtilsAmount.getValue(CardMatrixUtils.getValue(releaseRightValue, -1, 0.22), overlayFade * (0.4F + 0.5F * selectionProgress))
         );
      }

      if (revealProgress < 1.0F) {
         float amountForFloat = MatrixStackUtilsAmount.getAmountForFloat(revealProgress);
         float characterReleaseValue4 = 14.0F * amountForFloat;
         float characterReleaseFactor = 1.0F - revealProgress;
         handlePanelOverlay(
            panelOverlayValue - characterReleaseValue4, amountAmounts - characterReleaseValue4, deltaAmount + characterReleaseValue4 * 2.0F, hashHuntBound + characterReleaseValue4 * 2.0F, characterReleaseValue + characterReleaseValue4, 1.5F, MatrixStackUtilsAmount.getValue(releaseRightValue, characterReleaseFactor * 0.78F)
         );
         handlePanelOverlay(panelOverlayValue, amountAmounts, deltaAmount, hashHuntBound, characterReleaseValue, MatrixStackUtilsAmount.getValue(releaseRightValue, characterReleaseFactor * characterReleaseFactor * 0.42F), 12.0F, 0.85F, false);
         handlePanelOverlay(panelOverlayValue, amountAmounts, deltaAmount, hashHuntBound, characterReleaseValue, MatrixStackUtilsAmount.getValue(releaseRightValue, characterReleaseFactor * characterReleaseFactor * 0.13F));
      }
   }

   public static boolean canFlag(float[] characterReleaseValues) {
      if (!ClampMatrixUtils.isFlag()) {
         return false;
      } else {
         characterReleaseValues[0] = (ClampMatrixUtils.getLerpedPosX() + ClampMatrixUtils.getLerpAmount()) * 0.5F;
         characterReleaseValues[1] = (ClampMatrixUtils.getLerpedPosZ() + ClampMatrixUtils.getAmount()) * 0.5F;
         return true;
      }
   }

   private static void handleAmountScaled(int scaledValue, float[] scaledValues) {
      float deltaAmount = modeAmount - panelLeftX;
      float deltaAmount2 = scaledAmount - panelTopY;
      switch (scaledValue) {
         case 0:
            scaledValues[0] = panelLeftX;
            scaledValues[1] = Math.max(panelTopY - 3.0F - 22.0F, 4.0F);
            scaledValues[2] = deltaAmount;
            scaledValues[3] = 22.0F;
            break;
         case 1:
            scaledValues[0] = panelLeftX;
            scaledValues[1] = Math.min(scaledAmount + 3.0F, scaledHeight - 22.0F - 4.0F);
            scaledValues[2] = deltaAmount;
            scaledValues[3] = 22.0F;
            break;
         case 2:
            float max2 = getMax(2);
            scaledValues[0] = panelLeftX - 3.0F - max2;
            scaledValues[1] = panelTopY;
            scaledValues[2] = max2;
            scaledValues[3] = deltaAmount2;
            break;
         default:
            scaledValues[0] = modeAmount + 3.0F;
            scaledValues[1] = panelTopY;
            scaledValues[2] = getMax(3);
            scaledValues[3] = deltaAmount2;
      }
   }

   private static void handleStackEmpty(int stackEmptyValue, float stackEmptyValue2, float stackEmptyValue3, float stackEmptyValue4) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      int matrixStackValue = CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.18 * stackEmptyValue4);
      switch (stackEmptyValue) {
         case 0:
            handle(stackEmptyValue2, stackEmptyValue3 + 2.0F, 20.0F, 10.0F, 3.0F, -267777266, CardMatrixUtils.getReleaseRight(0.42 + 0.3 * stackEmptyValue4));
            handlePanelOverlay(stackEmptyValue2 + 3.0F, stackEmptyValue3 + 4.5F, 8.0F, 1.5F, 0.75F, -638389001);
            handlePanelOverlay(stackEmptyValue2 + 3.0F, stackEmptyValue3 + 7.5F, 14.0F, 1.5F, 0.75F, -1946157056);
            handlePanelOverlay(stackEmptyValue2 + 3.0F, stackEmptyValue3 + 7.5F, 14.0F * clamp, 1.5F, 0.75F, getClamp());
            break;
         case 1:
            handlePanelOverlay(stackEmptyValue2, stackEmptyValue3 + 5.0F, 20.0F, 4.0F, 2.0F, Integer.MIN_VALUE);
            handlePanelOverlay(stackEmptyValue2, stackEmptyValue3 + 5.0F, Math.max(4.0F, 20.0F * clamp), 4.0F, 2.0F, getClamp());
            break;
         case 2:
            for (int i = 0; i < 4; i++) {
               EquipmentSlot equipmentSlot = i == 0 ? EquipmentSlot.HEAD : (i == 1 ? EquipmentSlot.CHEST : (i == 2 ? EquipmentSlot.LEGS : EquipmentSlot.FEET));
               boolean stackEmptyFlag = player != null && !player.getEquippedStack(equipmentSlot).isEmpty();
               float panelOverlayValue = stackEmptyValue2 + i * 5.3F;
               if (stackEmptyFlag) {
                  handlePanelOverlay(panelOverlayValue, stackEmptyValue3 + 5.0F, 4.0F, 4.0F, 1.0F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.92F));
               } else {
                  handlePanelOverlay(panelOverlayValue, stackEmptyValue3 + 5.0F, 4.0F, 4.0F, 1.0F, 0.75F, 1308622847);
               }
            }
            break;
         case 3:
            boolean stackEmptyFlag2 = player != null && !player.getMainHandStack().isEmpty();
            boolean stackEmptyFlag3 = player != null && !player.getOffHandStack().isEmpty();
            if (stackEmptyFlag2) {
               handlePanelOverlay(stackEmptyValue2 + 3.0F, stackEmptyValue3 + 4.0F, 6.0F, 6.0F, 1.5F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.92F));
            } else {
               handlePanelOverlay(stackEmptyValue2 + 3.0F, stackEmptyValue3 + 4.0F, 6.0F, 6.0F, 1.5F, 0.75F, 1308622847);
            }

            if (stackEmptyFlag3) {
               handlePanelOverlay(stackEmptyValue2 + 11.0F, stackEmptyValue3 + 4.0F, 6.0F, 6.0F, 1.5F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.92F));
            } else {
               handlePanelOverlay(stackEmptyValue2 + 11.0F, stackEmptyValue3 + 4.0F, 6.0F, 6.0F, 1.5F, 0.75F, 1308622847);
            }
         case 4:
         default:
            break;
         case 5:
            float stackEmptyValue7 = modeCount * 5.0F % 6.0F;

            for (int j = 0; j < 5; j++) {
               float max8 = MatrixStackUtilsAmount.getMax(1.0F - Math.abs(stackEmptyValue7 - j) * 0.8F);
               handlePanelOverlay(
                  stackEmptyValue2 + 1.0F + j * 3.6F, stackEmptyValue3 + 11.0F - j * 2.1F, 1.6F, 1.6F, 0.8F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.34F + 0.66F * max8)
               );
            }

            handlePanelOverlay(stackEmptyValue2 + 16.2F, stackEmptyValue3 + 0.4F, 3.2F, 3.2F, 1.6F, matrixStackValue);
            break;
         case 6:
            float stackEmptyValue9 = 0.5F + 0.5F * MathHelper.sin(modeCount * 3.2F);
            float panelOverlayBound = 10.5F + 1.5F * stackEmptyValue9;
            handlePanelOverlay(
               stackEmptyValue2 + 10.0F - panelOverlayBound * 0.5F, stackEmptyValue3 + 7.0F - panelOverlayBound * 0.5F, panelOverlayBound, panelOverlayBound, panelOverlayBound * 0.5F, 1.2F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.7F + 0.3F * stackEmptyValue9)
            );
            handlePanelOverlay(stackEmptyValue2 + 8.5F, stackEmptyValue3 + 5.5F, 3.0F, 3.0F, 1.5F, matrixStackValue);
            break;
         case 7:
            int stackEmptyValue11 = 0xFF000000 | Color.HSBtoRGB(clamp * 0.333F, 1.0F, 1.0F) & 16777215;
            handlePanelOverlay(stackEmptyValue2 + 6.0F, stackEmptyValue3 + 0.5F, 8.0F, 13.0F, 1.5F, MatrixStackUtilsAmount.getValue(stackEmptyValue11, 0.2F));
            handlePanelOverlay(stackEmptyValue2 + 6.0F, stackEmptyValue3 + 0.5F, 8.0F, 13.0F, 1.5F, 1.2F, stackEmptyValue11);
            break;
         case 8:
            for (int k = 0; k < 3; k++) {
               float stackEmptyValue12 = stackEmptyValue2 + 1.0F + k * 6.5F;
               int stackEmptyValue13 = k == 0 ? -2077877 : (k == 1 ? -11688193 : -868018);
               handlePanelOverlay(stackEmptyValue12, stackEmptyValue3 + 3.0F, 5.0F, 5.0F, 2.5F, MatrixStackUtilsAmount.getValue(stackEmptyValue13, 0.9F));
               handlePanelOverlay(stackEmptyValue12, stackEmptyValue3 + 10.0F, 5.0F, 1.5F, 0.75F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.55F));
            }
      }
   }

   private static boolean canClampMatrix(float clampMatrixValue, float modeValue) {
      return MATRIX_CLAMP.clampMatrixActive && ClampMatrixUtils.canAmountFlag(MATRIX_CLAMP, clampMatrixValue, modeValue, CLAMP_POINT_BUFFER)
         ? CLAMP_POINT_BUFFER[0] >= MATRIX_CLAMP.clampMatrixAmount
            && CLAMP_POINT_BUFFER[0] <= MATRIX_CLAMP.clampMatrixAmount + MATRIX_CLAMP.matrixAmount
            && CLAMP_POINT_BUFFER[1] >= MATRIX_CLAMP.panelY
            && CLAMP_POINT_BUFFER[1] <= MATRIX_CLAMP.panelY + MATRIX_CLAMP.panelHeight
         : false;
   }

   private static void handlePanelOverlay(float panelOverlayValue, float amountAmounts, float panelOverlayBound, float hashHuntBound, float panelOverlayBound3, int matrixStackValue) {
      float panelOverlayValue4 = (matrixStackValue >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount;
      if (!(panelOverlayBound <= 0.0F) && !(hashHuntBound <= 0.0F) && !(panelOverlayValue4 <= 0.003F)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         CorzRoundedUtils.handle(
            getPositionMatrix(),
            panelOverlayValue - 1.0F,
            amountAmounts - 1.0F,
            panelOverlayValue + panelOverlayBound + 1.0F,
            amountAmounts + hashHuntBound + 1.0F,
            panelOverlayValue / framebufferHeight,
            amountAmounts / framebufferHeight,
            panelOverlayBound / framebufferHeight,
            hashHuntBound / framebufferHeight,
            Math.min(panelOverlayBound3, Math.min(panelOverlayBound, hashHuntBound) * 0.5F) / framebufferHeight,
            (matrixStackValue >> 16 & 0xFF) / 255.0F,
            (matrixStackValue >> 8 & 0xFF) / 255.0F,
            (matrixStackValue & 0xFF) / 255.0F,
            panelOverlayValue4
         );
      }
   }

   private static CharacterReleaseUtils.Element getMaxsMode(String maxsModeText) {
      String replace = getReplace(maxsModeText);

      for (CharacterReleaseUtils.Element maxsMode : modeMaxs) {
         if (maxsMode.replace.equals(replace)) {
            return maxsMode;
         }
      }

      return null;
   }

   public static boolean canPlaceTexts(String characterReleaseText, String fastSmoothText) {
      CharacterReleaseUtils.Element maxsMode = getMaxsMode(characterReleaseText);
      if (maxsMode == null) {
         return false;
      } else {
         maxsMode.updateModeTexts();
         if (!maxsMode.isFlag()) {
            return false;
         } else {
            for (int i = 0; i < placeRightTexts.length; i++) {
               if (placeRightTexts[i].equalsIgnoreCase(fastSmoothText)) {
                  maxsMode.handlePlaceTexts(i);
                  maxsMode.handleModeToggle(true);
                  return maxsMode.isModeToggleModeMax();
               }
            }

            return false;
         }
      }
   }

   private static void renderAmountMode(DrawContext context, CharacterReleaseUtils.Flyer flyer) {
      float modeValue = MatrixStackUtilsAmount.getMax(flyer.flightProgress);
      float x2;
      float panelOverlayLimit;
      if (flyer.flyerKindIndex == 0) {
         x2 = 1.04F - 0.5F * MatrixStackUtilsAmount.getAmount3(modeValue);
         panelOverlayLimit = 1.0F - MatrixStackUtilsAmount.getMax((modeValue - 0.5F) / 0.5F);
      } else {
         x2 = 1.04F - 0.14F * modeValue;
         panelOverlayLimit = 1.0F - MatrixStackUtilsAmount.getMax((modeValue - 0.78F) / 0.22F);
      }

      renderReleaseRight(context, flyer.modeMax, flyer.flyerAmount, flyer.currentPosition, x2, panelOverlayLimit, 0.0F);
   }

   private static float getAmountForFloat(float characterReleaseValue, float hashHuntBound, float characterReleaseFactor) {
      return characterReleaseValue + hashHuntBound * 0.5F + characterAmount * characterReleaseFactor;
   }

   private static void handle(Identifier id2, float characterReleaseValue, float roundOffset, float peekPositionFactor, int colorsValue) {
      MatrixPeekUtilsIdentifier.handle(characterReleaseValue, roundOffset, peekPositionFactor, peekPositionFactor, id2, getColors(colorsValue), MatrixPeekUtilsRender.getMatrixStack());
   }

   public static String formatHiddenListed() {
      StringBuilder stringBuilder = new StringBuilder();

      for (CharacterReleaseUtils.Element element : modeMaxs) {
         element.updateModeTexts();
         stringBuilder.append(element.listedHiddenText).append('=').append(element.isModeToggleModeMax() ? "on" : (element.isModeToggle() ? "listed" : "hidden"));
         if (element.isFlag()) {
            stringBuilder.append('@').append(element.next3Mode.getCurrentOption());
         }

         stringBuilder.append(' ');
      }

      return stringBuilder.toString().trim();
   }

   private static void handleClamp(int clampValue, float clampValue2, float amountAmounts, int matrixStackValue) {
      switch (clampValue) {
         case 0:
            handlePanelOverlay(clampValue2 + 4.0F, amountAmounts + 1.5F, 4.0F, 8.0F, 2.0F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.55F), 3.5F, 0.9F, false);
            handlePanelOverlay(clampValue2 + 4.0F, amountAmounts + 1.5F, 4.0F, 8.0F, 1.5F, matrixStackValue);
            break;
         case 1:
            float panelOverlayValue = clampValue2 + 1.5F;
            float clampValue6 = 9.0F;
            float clampValue7 = 11.0F;
            handlePanelOverlay(panelOverlayValue, amountAmounts, 3.0F, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue, amountAmounts, 1.0F, 3.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue + clampValue6 - 3.0F, amountAmounts, 3.0F, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue + clampValue6 - 1.0F, amountAmounts, 1.0F, 3.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue, amountAmounts + clampValue7 - 1.0F, 3.0F, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue, amountAmounts + clampValue7 - 3.0F, 1.0F, 3.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue + clampValue6 - 3.0F, amountAmounts + clampValue7 - 1.0F, 3.0F, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue + clampValue6 - 1.0F, amountAmounts + clampValue7 - 3.0F, 1.0F, 3.0F, 0.5F, matrixStackValue);
            break;
         case 2:
         default:
            handlePanelOverlay(clampValue2 + 2.0F, amountAmounts + 0.5F, 8.0F, 10.0F, 1.5F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.36F));
            handlePanelOverlay(clampValue2 + 2.0F, amountAmounts + 0.5F, 8.0F, 10.0F, 1.5F, 1.0F, matrixStackValue);
            break;
         case 3:
            handlePanelOverlay(clampValue2 + 1.0F, amountAmounts + 0.5F, 7.0F, 10.0F, 1.0F, 1.0F, matrixStackValue);
            handlePanelOverlay(clampValue2 + 9.6F, amountAmounts + 0.5F, 1.4F, 10.0F, 0.7F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.45F));
            handlePanelOverlay(clampValue2 + 9.6F, amountAmounts + 0.5F + 10.0F * (1.0F - clamp), 1.4F, 10.0F * clamp, 0.7F, matrixStackValue);
            break;
         case 4:
            handlePanelOverlay(clampValue2 + 4.8F, amountAmounts, 2.4F, 2.4F, 1.2F, matrixStackValue);
            handlePanelOverlay(clampValue2 + 5.5F, amountAmounts + 2.6F, 1.0F, 4.6F, 0.5F, matrixStackValue);
            handlePanelOverlay(clampValue2 + 2.5F, amountAmounts + 3.8F, 7.0F, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(clampValue2 + 4.0F, amountAmounts + 6.8F, 4.0F, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(clampValue2 + 4.0F, amountAmounts + 6.8F, 1.0F, 4.2F, 0.5F, matrixStackValue);
            handlePanelOverlay(clampValue2 + 7.0F, amountAmounts + 6.8F, 1.0F, 4.2F, 0.5F, matrixStackValue);
      }
   }

   private CharacterReleaseUtils() {
   }

   private static void handleAmountMode(float modeLimit, float modeValue) {
      CharacterReleaseUtils.Element element = modeMax3Mode;
      modeMax3Mode = null;
      if (modeMax != null && overlayActive) {
         CharacterReleaseUtils.Element modeMax3 = modeMax;
         modeMax = null;
         boolean modeFlag = ClampMatrixUtils.isFlag() && overlayPanelActive;
         CharacterReleaseUtils.Element modeMax4 = modeMax3.getModeMax();
         int modeLimit2 = modeMax4 != null && modeFlag ? getAmountAmounts(modeLimit, modeValue, true) : -1;
         if (modeLimit2 >= 0) {
            if (modeMax3.isFlag()) {
               modeMax3.handlePlaceTexts(modeLimit2);
               modeMax3.flashLevel = 1.0F;
            }

            handleAmountMode(modeMax3, 2, overlayPosX, overlayAmount2Overlay);
         } else if (modeFlag && (modeMax4 != null || !canAmount(modeLimit, modeValue))) {
            handleFlag(modeMax3, overlayPosX, overlayAmount2Overlay);
         } else {
            handleAmountMode(modeMax3, 2, overlayPosX, overlayAmount2Overlay);
         }
      } else if (element != null && elementsPanelActive && getAmountMaxsForFloat(modeLimit, modeValue) == element) {
         if (characterReleaseActive) {
            if (modeLimit >= element.maxsAmount + element.characterAmount - 12.0F) {
               handleFlag(element, element.maxsAmount + element.characterAmount * 0.5F, element.rowTopY + 7.0F);
            }
         } else if (element.modeFlag && element.listSize > 1) {
            element.next2Mode.handle((element.getClampMode() + 1) % element.listSize);
            element.flashLevel = 1.0F;
         }
      }
   }

   private static int getReleaseRight() {
      return CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.28);
   }

   private static String getReplace(String replaceText) {
      return replaceText.toLowerCase(Locale.ROOT).replace(" ", "").replace("_", "");
   }

   private static void handleModeMax(CharacterReleaseUtils.Flyer flyer) {
      flyer.modeActive = false;
      if (flyer.flyerKindIndex == 1) {
         flyer.modeMax.modeActive = false;
         flyer.modeMax.pulseProgress = 1.0F;
      } else if (flyer.flyerKindIndex == 2) {
         flyer.modeMax.overlayActive = false;
         flyer.modeMax.flashLevel = 1.0F;
      }
   }

   private static void renderAmountsAmount(DrawContext context) {
      float characterReleaseValue = getAmount(subModeProgress);
      if (!(characterReleaseValue <= 0.01F)) {
         int releaseRightValue = MatrixEspUtils.releaseRightValue;
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(characterReleaseValue);

         try {
            float panelOverlayValue = panelLeftX;
            float panelTopY142 = panelTopY;
            float modeAmount32 = modeAmount;
            float scaledAmount52 = scaledAmount;
            float min2 = Math.min(9.0F, Math.min(modeAmount32 - panelOverlayValue, scaledAmount52 - panelTopY142) * 0.3F);
            int matrixStackValue = MatrixStackUtilsAmount.getValue(-1, 0.62F);
            handlePanelOverlay(panelOverlayValue, panelTopY142, min2, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue, panelTopY142, 1.0F, min2, 0.5F, matrixStackValue);
            handlePanelOverlay(modeAmount32 - min2, panelTopY142, min2, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(modeAmount32 - 1.0F, panelTopY142, 1.0F, min2, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue, scaledAmount52 - 1.0F, min2, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(panelOverlayValue, scaledAmount52 - min2, 1.0F, min2, 0.5F, matrixStackValue);
            handlePanelOverlay(modeAmount32 - min2, scaledAmount52 - 1.0F, min2, 1.0F, 0.5F, matrixStackValue);
            handlePanelOverlay(modeAmount32 - 1.0F, scaledAmount52 - min2, 1.0F, min2, 0.5F, matrixStackValue);

            for (int i = 0; i < 4; i++) {
               float characterReleaseLimit = characterReleaseTable[i];
               handleAmountScaled(i, table);
               float characterReleaseValue4 = table[0];
               float characterReleaseValue5 = table[1];
               float characterReleaseValue6 = table[2];
               float hashHuntBound = table[3];
               if (characterReleaseLimit > 0.01F) {
                  handlePanelOverlay(characterReleaseValue4, characterReleaseValue5, characterReleaseValue6, hashHuntBound, 6.0F, MatrixStackUtilsAmount.getValue(releaseRightValue, 0.3F * characterReleaseLimit), 8.0F, 0.8F, false);
                  handlePanelOverlay(characterReleaseValue4, characterReleaseValue5, characterReleaseValue6, hashHuntBound, 6.0F, MatrixStackUtilsAmount.getValue(releaseRightValue, 0.16F * characterReleaseLimit));
               }

               handlePanelOverlay(
                  characterReleaseValue4,
                  characterReleaseValue5,
                  characterReleaseValue6,
                  hashHuntBound,
                  6.0F,
                  1.0F,
                  CardMatrixUtils.getValue(MatrixStackUtilsAmount.getValue(-1, 0.22F), MatrixStackUtilsAmount.getValue(releaseRightValue, 0.95F), characterReleaseLimit)
               );
               float amountsPlaceScaled = getAmountsPlace(i) * 0.78F;
               float panelOverlayBound = amountsPlaceScaled + 12.0F;
               float characterReleaseValue9 = 13.0F;
               float characterReleaseValue10 = characterReleaseValue4 + (characterReleaseValue6 - panelOverlayBound) * 0.5F;
               float rowTopY = characterReleaseValue5 + (hashHuntBound - characterReleaseValue9) * 0.5F;
               if (i == 2 || i == 3) {
                  rowTopY = characterReleaseValue5 + Math.min(hashHuntBound * 0.22F, Math.max(0.0F, hashHuntBound - characterReleaseValue9));
               }

               float x2 = 1.0F + 0.08F * characterReleaseLimit;
               boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, characterReleaseValue10 + panelOverlayBound * 0.5F, rowTopY + characterReleaseValue9 * 0.5F, x2);

               try {
                  handle(
                     characterReleaseValue10,
                     rowTopY,
                     panelOverlayBound,
                     characterReleaseValue9,
                     characterReleaseValue9 * 0.5F,
                     CardMatrixUtils.getValue(-267381991, CardMatrixUtils.getValue(-15723751, releaseRightValue, 0.35), characterReleaseLimit),
                     CardMatrixUtils.getValue(MatrixStackUtilsAmount.getValue(-1, 0.2F), MatrixStackUtilsAmount.getValue(releaseRightValue, 0.95F), characterReleaseLimit)
                  );
                  handlePushScale(placeRightTexts[i], characterReleaseValue10 + 6.0F, getAmountForFloat(rowTopY, characterReleaseValue9, 0.78F), 0.78F, CardMatrixUtils.getValue(-5854026, -1, characterReleaseLimit));
               } finally {
                  MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
               }
            }

            float panelOverlayLimit = getAmount(characterReleaseAmount);
            if (panelOverlayLimit > 0.01F) {
               int hoveredElementIndex22 = hoveredElementIndex;
               if (characterTable[hoveredElementIndex22] < 0.0F) {
                  characterTable[hoveredElementIndex22] = MatrixStackUtilsAmount.getAmount3(characterReleaseTexts[hoveredElementIndex22]);
               }

               float characterReleaseValue13 = characterTable[hoveredElementIndex22] * 0.86F;
               float characterReleaseValue14 = characterReleaseValue13 + 16.0F;
               float characterReleaseValue15 = 15.0F;
               float clamp = MathHelper.clamp(
                  (panelLeftX + modeAmount) * 0.5F, characterReleaseValue14 * 0.5F + 6.0F, Math.max(characterReleaseValue14 * 0.5F + 6.0F, scaledWidth - characterReleaseValue14 * 0.5F - 6.0F)
               );
               float amountAmounts = getAmountAmounts(characterReleaseValue15, 8.0F, 1.0F);
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);
               boolean canTranslatePush = MatrixStackUtilsAmount.canTranslatePush(
                  context, clamp, amountAmounts + characterReleaseValue15 * 0.5F, 0.84F + 0.16F * MatrixStackUtilsAmount.getAmount(panelOverlayLimit, 1.7F)
               );

               try {
                  handlePanelOverlay(
                     clamp - characterReleaseValue14 * 0.5F, amountAmounts + 1.5F, characterReleaseValue14, characterReleaseValue15, characterReleaseValue15 * 0.5F, MatrixStackUtilsAmount.getValue(releaseRightValue, 0.38F), 7.0F, 0.55F, true
                  );
                  handlePanelOverlay(clamp - characterReleaseValue14 * 0.5F, amountAmounts, characterReleaseValue14, characterReleaseValue15, characterReleaseValue15 * 0.5F, MatrixStackUtilsAmount.getValue(releaseRightValue, 0.94F));
                  handlePushScale(
                     characterReleaseTexts[hoveredElementIndex22], clamp - characterReleaseValue13 * 0.5F, getAmountForFloat(amountAmounts, characterReleaseValue15, 0.86F), 0.86F, MatrixStackUtilsAmount.getReleaseRight()
                  );
               } finally {
                  MatrixStackUtilsAmount.renderPopMatrices(context, canTranslatePush);
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static CharacterReleaseUtils.Element getAmountMaxsForFloat(float maxsLimit, float modeValue) {
      if (!modeActive && !(maxsAmount < 0.5F)) {
         for (CharacterReleaseUtils.Element amountMaxs : modeMaxs) {
            if (amountMaxs.modeToggle
               && !(amountMaxs.showProgress < 0.5F)
               && !(amountMaxs.characterAmount <= 0.0F)
               && maxsLimit >= amountMaxs.maxsAmount - 1.0F
               && maxsLimit <= amountMaxs.maxsAmount + amountMaxs.characterAmount + 1.0F
               && modeValue >= amountMaxs.rowTopY - 1.0F
               && modeValue <= amountMaxs.rowTopY + 14.0F + 1.0F) {
               return amountMaxs;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static void renderAmountClamp(DrawContext context, String clampText, float clampValue, float panelOverlayLimit, int matrixStackValue, int colorsValue) {
      if (!(panelOverlayLimit <= 0.01F)) {
         float labelWScaled = clampValue * 0.86F;
         float panelOverlayBound = labelWScaled + 16.0F;
         float clampValue6 = 15.0F;
         float clamp = MathHelper.clamp((panelLeftX + modeAmount) * 0.5F, panelOverlayBound * 0.5F + 6.0F, Math.max(panelOverlayBound * 0.5F + 6.0F, scaledWidth - panelOverlayBound * 0.5F - 6.0F));
         float amountAmounts = getAmountAmounts(clampValue6, 10.0F, getAmount(subModeProgress));
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
            context, clamp, amountAmounts + clampValue6 * 0.5F, 0.84F + 0.16F * MatrixStackUtilsAmount.getAmount(panelOverlayLimit, 1.7F)
         );

         try {
            handlePanelOverlay(clamp - panelOverlayBound * 0.5F, amountAmounts + 1.5F, panelOverlayBound, clampValue6, clampValue6 * 0.5F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.38F), 7.0F, 0.55F, true);
            handlePanelOverlay(clamp - panelOverlayBound * 0.5F, amountAmounts, panelOverlayBound, clampValue6, clampValue6 * 0.5F, matrixStackValue);
            handlePushScale(clampText, clamp - labelWScaled * 0.5F, getAmountForFloat(amountAmounts, clampValue6, 0.86F), 0.86F, colorsValue);
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static void handleAmountMode(CharacterReleaseUtils.Flyer flyer) {
      flyer.flightProgress = flyer.flightProgress + frameDeltaSeconds * 1000.0F / flyer.flightDurationMs;
      CharacterReleaseUtils.Element element = flyer.flyerKindIndex == 0 ? flyer.modeMax.getModeMax() : null;
      float amount;
      float amount2;
      if (flyer.flyerKindIndex == 0 && overlayPanelActive && element != null) {
         handleAmounts(element.getPlaceTexts(), modeTable);
         amount = modeTable[0];
         amount2 = modeTable[1];
      } else if (flyer.flyerKindIndex == 0) {
         amount = overlayPanelActive ? (panelLeftX + modeAmount) * 0.5F : flyer.modeAmount;
         amount2 = overlayPanelActive ? panelTopY + (scaledAmount - panelTopY) * 0.42F : flyer.startPosition;
      } else if (flyer.flyerKindIndex == 1) {
         amount = flyer.modeMax.elementY;
         amount2 = flyer.modeMax.clampAmount;
      } else {
         amount = flyer.modeMax.maxsAmount + flyer.modeMax.characterAmount * 0.5F;
         amount2 = flyer.modeMax.rowTopY + 7.0F;
      }

      if (flyer.flightProgress >= 1.0F) {
         flyer.flyerAmount = amount;
         flyer.currentPosition = amount2;
         handleModeMax(flyer);
      } else {
         float modeFactor = flyer.flyerKindIndex == 0 ? MatrixStackUtilsAmount.getAmount3(flyer.flightProgress) : MatrixStackUtilsAmount.getAmount(flyer.flightProgress, 0.6F);
         flyer.flyerAmount = flyer.modeAmount + (amount - flyer.modeAmount) * modeFactor;
         flyer.currentPosition = flyer.startPosition + (amount2 - flyer.startPosition) * modeFactor;
      }
   }

   private static void handleForFloatInt(float characterReleaseValue, float amountAmounts, float panelOverlayBound, float hashHuntBound, float characterReleaseValue5, int characterReleaseValue6, int matrixStackValue) {
      if (CardMatrixUtils.isModernFlag()) {
         handlePanelOverlay(characterReleaseValue, amountAmounts, panelOverlayBound, hashHuntBound, characterReleaseValue5, characterReleaseValue6);
         handlePanelOverlay(characterReleaseValue, amountAmounts, panelOverlayBound, hashHuntBound, characterReleaseValue5, MatrixPeekUtilsRender.getAmount(), matrixStackValue);
      } else {
         handle(characterReleaseValue, amountAmounts, panelOverlayBound, hashHuntBound, characterReleaseValue5, characterReleaseValue6, matrixStackValue);
      }
   }

   private static void renderElementsPanel(DrawContext context, float elementsPanelValue, float elementsPanelValue2) {
      if (!modeActive) {
         float panelOverlayAmount = CorzRoundedUtils.panelOverlayAmount;

         try {
            updateAmountMode();
            float elementsPanelValue3 = 24.0F;
            float amountAmounts = 24.0F;
            float panelOverlayBound = 112.0F;
            float panelHeight = MATRIX_CLAMP.panelHeight;
            handleForFloatInt(elementsPanelValue3, amountAmounts, panelOverlayBound, panelHeight, 10.0F, CardMatrixUtils.getValue(), 352321535);
            float scaleValue = elementsPanelValue3 + 7.0F + 1.0F;
            handlePushScale("Elements", scaleValue, amountAmounts + 4.5F, 1.12F, -920587);
            handlePushScale("Elements", scaleValue + MatrixPeekUtilsRender.getAmount(), amountAmounts + 4.5F, 1.12F, MatrixStackUtilsAmount.getValue(-920587, 0.55F));
            handlePushScale(
               "Drag onto your character", scaleValue, amountAmounts + 17.5F, 0.8F, CardMatrixUtils.getValue(-7630437, getReleaseRight(), characterElementsAmount)
            );
            context.enableScissor((int)Math.floor(elementsPanelValue3), (int)Math.floor(amountAmounts), (int)Math.ceil(elementsPanelValue3 + panelOverlayBound), (int)Math.ceil(amountAmounts + panelHeight));

            try {
               CharacterReleaseUtils.Element element = null;

               for (CharacterReleaseUtils.Element modeMaxsItem : modeMaxs) {
                  if (!(modeMaxsItem.toggleModeProgress <= 0.01F)) {
                     if (modeMax3Mode == modeMaxsItem && !elementsPanelActive) {
                        element = modeMaxsItem;
                     } else {
                        renderAmountMode(context, modeMaxsItem, elementsPanelValue, elementsPanelValue2);
                     }
                  }
               }

               if (element != null) {
                  renderAmountMode(context, element, elementsPanelValue, elementsPanelValue2);
               }

               updateCharacterEverything();
            } finally {
               context.disableScissor();
            }
         } catch (Throwable e) {
            handleModeMax("panel", e);
         } finally {
            CorzRoundedUtils.panelOverlayAmount = panelOverlayAmount;
         }
      }
   }

   private static void handlePanelOverlay(float panelOverlayValue, float amountAmounts, float panelOverlayBound, float hashHuntBound, float panelOverlayBound3, float panelOverlayValue3, int matrixStackValue) {
      float panelOverlayValue5 = (matrixStackValue >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount;
      if (!(panelOverlayBound <= 0.0F) && !(hashHuntBound <= 0.0F) && !(panelOverlayValue5 <= 0.003F)) {
         float framebufferHeight = CorzRoundedUtils.getFramebufferHeight();
         CorzRoundedUtils.handleRectRounded(
            getPositionMatrix(),
            panelOverlayValue - 1.0F,
            amountAmounts - 1.0F,
            panelOverlayValue + panelOverlayBound + 1.0F,
            amountAmounts + hashHuntBound + 1.0F,
            panelOverlayValue / framebufferHeight,
            amountAmounts / framebufferHeight,
            panelOverlayBound / framebufferHeight,
            hashHuntBound / framebufferHeight,
            Math.min(panelOverlayBound3, Math.min(panelOverlayBound, hashHuntBound) * 0.5F) / framebufferHeight,
            panelOverlayValue3 / framebufferHeight,
            (matrixStackValue >> 16 & 0xFF) / 255.0F,
            (matrixStackValue >> 8 & 0xFF) / 255.0F,
            (matrixStackValue & 0xFF) / 255.0F,
            panelOverlayValue5
         );
      }
   }

   private static void handleAmountAmounts(CharacterReleaseUtils.Element element, float characterReleaseValue, float panelOverlayBound, float characterReleaseValue3, float clampLimit, float characterReleaseValue5, boolean characterReleaseFlag) {
      float amountsModeScaled = element.getAmountsMode() * 0.8F;
      handlePushScale(
         element.formatHiddenListed(),
         characterReleaseValue + panelOverlayBound - 7.0F - amountsModeScaled,
         getAmountForFloat(characterReleaseValue3, 24.0F, 0.8F),
         0.8F,
         CardMatrixUtils.getValue(-7630437, getReleaseRight(), 0.45 + 0.55 * element.hoverProgress)
      );
      float characterReleaseValue6 = characterReleaseValue + 5.0F;
      float amountAmounts = characterReleaseValue3 + 24.0F - 2.0F;
      float characterReleaseValue8 = panelOverlayBound - 10.0F;
      float hashHuntBound = 15.0F;
      handleForFloatInt(characterReleaseValue6, amountAmounts, characterReleaseValue8, hashHuntBound, 5.0F, CardMatrixUtils.getValue7(), CardMatrixUtils.getValueInt());
      int listSize = element.listSize;
      int clampMode = element.getClampMode();
      float characterReleaseFactor = (characterReleaseValue8 - 2.0F) / listSize;
      if (!element.visited) {
         element.visited = true;
         element.indicatorOffsetX = clampMode * characterReleaseFactor;
      } else {
         element.indicatorOffsetX = getAmount(element.indicatorOffsetX, clampMode * characterReleaseFactor, 20.0F);
      }

      handlePanelOverlay(
         characterReleaseValue6 + 1.0F + element.indicatorOffsetX, amountAmounts + 1.0F, characterReleaseFactor, hashHuntBound - 2.0F, 4.0F, MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.92F)
      );
      int characterReleaseValue10 = characterReleaseFlag ? getClampAmount(element, clampLimit, characterReleaseValue5) : -1;

      for (int i = 0; i < listSize; i++) {
         element.elementTable[i] = getAmount(element.elementTable[i], characterReleaseValue10 == i && i != clampMode ? 1.0F : 0.0F, 16.0F);
         float cardMatrixFactor = MatrixStackUtilsAmount.getMax(1.0F - Math.abs(element.indicatorOffsetX - i * characterReleaseFactor) / characterReleaseFactor);
         int matrixStackValue = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-7630437, -920587, element.elementTable[i]), MatrixStackUtilsAmount.getReleaseRight(), cardMatrixFactor);
         handleClamp(element.table[i], characterReleaseValue6 + 1.0F + i * characterReleaseFactor + (characterReleaseFactor - 12.0F) * 0.5F, amountAmounts + (hashHuntBound - 11.0F) * 0.5F, matrixStackValue);
      }
   }

   private static void renderOverlayPanel(DrawContext context, float overlayPanelValue) {
      if (!modeActive) {
         float overlayPanelFactor = getAmount((overlayPanelValue - 0.35F) / 0.65F);
         MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
         MatrixStack matrixStack2 = MatrixPeekUtilsRender.getMatrixStack();
         float panelOverlayAmount = CorzRoundedUtils.panelOverlayAmount;
         CorzClient.fontManager.setMatrixStack(MATRIX_STACK);
         MatrixPeekUtilsRender.handleMatrixStack(MATRIX_STACK);

         try {
            updateAmountMode();
            if (!(overlayPanelFactor <= 0.004F)) {
               CorzRoundedUtils.panelOverlayAmount = panelOverlayAmount * overlayPanelFactor;
               if (overlayPanelActive) {
                  updateAmountMin();
                  renderAmountsAmount(context);
                  renderAmountMode(context);
                  renderReleaseAmount(context);
               }

               for (CharacterReleaseUtils.Flyer flyer : modeFlyers) {
                  if (flyer.modeActive) {
                     renderAmountMode(context, flyer);
                  }
               }

               if (modeMax != null) {
                  renderReleaseRight(context, modeMax, overlayPosX, overlayAmount2Overlay, 0.86F + 0.18F * getAmount(overlayAmount), overlayAmount, overlayPanelAmount);
               }

               return;
            }
         } catch (Throwable e) {
            handleModeMax("overlay", e);
            return;
         } finally {
            CorzRoundedUtils.panelOverlayAmount = panelOverlayAmount;
            CorzClient.fontManager.setMatrixStack(matrixStack);
            MatrixPeekUtilsRender.handleMatrixStack(matrixStack2);
         }
      }
   }

   private static void updateCharacterEverything() {
      float characterEverythingValue = getAmount(collapseProgress);
      if (!(characterEverythingValue <= 0.01F)) {
         if (everythingCharacterAmount < 0.0F) {
            everythingCharacterAmount = MatrixStackUtilsAmount.getAmount3("Everything is on");
            characterEverythingAmount = MatrixStackUtilsAmount.getAmount3("your character");
         }

         float characterEverythingValue2 = 80.0F;
         float characterEverythingValue3 = panelContentY + (1.0F - characterEverythingValue) * 4.0F;
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(characterEverythingValue);

         try {
            float panelOverlayBound = 16.0F;
            handlePanelOverlay(
               characterEverythingValue2 - panelOverlayBound * 0.5F, characterEverythingValue3 + 2.0F, panelOverlayBound, panelOverlayBound, panelOverlayBound * 0.5F, MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.16F)
            );
            handle(IcoGenUtils.getIcoCheck(), characterEverythingValue2 - 5.0F, characterEverythingValue3 + 5.0F, 10.0F, getReleaseRight());
            handlePushScale("Everything is on", characterEverythingValue2 - everythingCharacterAmount * 0.86F * 0.5F, characterEverythingValue3 + 19.5F, 0.86F, -920587);
            handlePushScale("your character", characterEverythingValue2 - characterEverythingAmount * 0.86F * 0.5F, characterEverythingValue3 + 29.5F, 0.86F, -7630437);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static void handleAmounts(int characterReleaseValue, float[] characterReleaseValues) {
      handleAmountScaled(characterReleaseValue, table);
      characterReleaseValues[0] = table[0] + table[2] * 0.5F;
      characterReleaseValues[1] = table[1] + table[3] * 0.5F;
   }

   private static int getClampAmount(CharacterReleaseUtils.Element element, float clampLimit, float clampLimit2) {
      if (element.modeFlag && element.listSize != 0) {
         float clampValue = 31.0F;
         float clampValue2 = 98.0F;
         float clampValue3 = element.modeAmount + (element.elementWidth - element.getModeFlag()) * 0.5F;
         float clampLimit3 = clampValue + 5.0F;
         float clampValue4 = clampValue3 + 24.0F - 2.0F;
         float clampValue5 = clampValue2 - 10.0F;
         float clampValue6 = 15.0F;
         return !(clampLimit < clampLimit3) && !(clampLimit > clampLimit3 + clampValue5) && !(clampLimit2 < clampValue4 - 1.0F) && !(clampLimit2 > clampValue4 + clampValue6 + 1.0F)
            ? MathHelper.clamp((int)((clampLimit - clampLimit3 - 1.0F) / ((clampValue5 - 2.0F) / element.listSize)), 0, element.listSize - 1)
            : -1;
      } else {
         return -1;
      }
   }

   private static boolean canAmountMode(float amount, float amount2, int modeValue) {
      if (modeValue == 0 && modeMax == null) {
         CharacterReleaseUtils.Element amountMaxsForFloat = getAmountMaxsForFloat(amount, amount2);
         if (amountMaxsForFloat == null) {
            return false;
         } else {
            modeMax3Mode = amountMaxsForFloat;
            elementsPanelActive = true;
            characterReleaseActive = amount >= amountMaxsForFloat.maxsAmount + amountMaxsForFloat.characterAmount - 12.0F;
            scaleXMode = amount;
            scaleYMode = amount2;
            return true;
         }
      } else {
         return false;
      }
   }

   private static float getMax(int characterReleaseValue) {
      return Math.max(22.0F, getAmountsPlace(characterReleaseValue) * 0.78F + 12.0F + 4.0F);
   }

   private static boolean canModeMax(CharacterReleaseUtils.Element element, int modeValue) {
      for (CharacterReleaseUtils.Flyer flyer : modeFlyers) {
         if (flyer.modeActive && flyer.modeMax == element && flyer.flyerKindIndex == modeValue) {
            return true;
         }
      }

      return false;
   }

   private static int getAmountAmounts(float characterReleaseLimit, float modeValue, boolean characterReleaseFlag) {
      if (!canClampMatrix(characterReleaseLimit, modeValue) && (!characterReleaseFlag || !canAmountClamp(characterReleaseLimit, modeValue))) {
         for (int i = 0; i < 4; i++) {
            handleAmountScaled(i, clampTable);
            if (characterReleaseLimit >= clampTable[0] && characterReleaseLimit <= clampTable[0] + clampTable[2] && modeValue >= clampTable[1] && modeValue <= clampTable[1] + clampTable[3]) {
               return i;
            }
         }

         float characterReleaseValue = 33.0F;
         if (!(characterReleaseLimit < panelLeftX - 3.0F - getMax(2) - 8.0F) && !(characterReleaseLimit > modeAmount + 3.0F + getMax(3) + 8.0F) && !(modeValue < panelTopY - characterReleaseValue) && !(modeValue > scaledAmount + characterReleaseValue)) {
            float characterReleaseLimit3 = (panelLeftX + modeAmount) * 0.5F;
            float characterReleaseLimit4 = (panelTopY + scaledAmount) * 0.5F;
            float characterReleaseLimit5 = Math.abs(characterReleaseLimit - characterReleaseLimit3) - (modeAmount - panelLeftX) * 0.5F;
            float characterReleaseValue2 = Math.abs(modeValue - characterReleaseLimit4) - (scaledAmount - panelTopY) * 0.5F;
            return characterReleaseLimit5 > characterReleaseValue2 ? (characterReleaseLimit < characterReleaseLimit3 ? 2 : 3) : (modeValue < characterReleaseLimit4 ? 0 : 1);
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static boolean canFlag(String characterReleaseText, boolean characterReleaseFlag) {
      CharacterReleaseUtils.Element maxsMode = getMaxsMode(characterReleaseText);
      if (maxsMode == null) {
         return false;
      } else {
         maxsMode.updateModeTexts();
         maxsMode.handleModeToggle(characterReleaseFlag);
         return maxsMode.isModeToggleModeMax() == characterReleaseFlag;
      }
   }

   private static void handleAmountMode(CharacterReleaseUtils.Element modeMax, int value, float amount, float amount2) {
      CharacterReleaseUtils.Flyer flyer = null;

      for (CharacterReleaseUtils.Flyer modeFlyersItem : modeFlyers) {
         if (modeFlyersItem.modeActive && modeFlyersItem.modeMax == modeMax) {
            flyer = modeFlyersItem;
            break;
         }
      }

      if (flyer == null) {
         for (CharacterReleaseUtils.Flyer flyer3 : modeFlyers) {
            if (!flyer3.modeActive) {
               flyer = flyer3;
               break;
            }
         }
      }

      if (flyer == null) {
         flyer = modeFlyers[0];

         for (CharacterReleaseUtils.Flyer flyer4 : modeFlyers) {
            if (flyer4.flightProgress > flyer.flightProgress) {
               flyer = flyer4;
            }
         }

         handleModeMax(flyer);
      }

      flyer.modeMax = modeMax;
      flyer.flyerKindIndex = value;
      flyer.modeActive = true;
      flyer.modeAmount = amount;
      flyer.startPosition = amount2;
      flyer.flyerAmount = amount;
      flyer.currentPosition = amount2;
      flyer.flightProgress = 0.0F;
      flyer.flightDurationMs = value == 0 ? 300.0F : 340.0F;
   }

   private static Color getColors(int colorsValue) {
      int index = colorsValue * -1640531535 >>> 25;
      Color color2 = colors[index];
      if (color2 == null || colorsTable[index] != colorsValue) {
         color2 = new Color(colorsValue, true);
         colors[index] = color2;
         colorsTable[index] = colorsValue;
      }

      return color2;
   }

   private static Identifier getIdentifierClose() {
      if (identifier == null) {
         identifier = IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("close");
      }

      return identifier;
   }

   private static void renderReleaseAmount(DrawContext context) {
      if (releaseAmount < 0.0F) {
         releaseAmount = MatrixStackUtilsAmount.getAmount3("Release to add");
         removeTextWidth = MatrixStackUtilsAmount.getAmount3("Release to remove");
      }

      renderAmountClamp(
         context,
         "Release to add",
         releaseAmount,
         addHintAlpha,
         MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.94F),
         MatrixStackUtilsAmount.getReleaseRight()
      );
      renderAmountClamp(context, "Release to remove", removeTextWidth, removeHintAlpha, MatrixStackUtilsAmount.getValue(-1750963, 0.94F), -1);
   }

   private static float getAmount(float characterReleaseFactor) {
      characterReleaseFactor = MatrixStackUtilsAmount.getMax(characterReleaseFactor);
      return characterReleaseFactor * characterReleaseFactor * (3.0F - 2.0F * characterReleaseFactor);
   }

   private static void renderReleaseRight(DrawContext context, CharacterReleaseUtils.Element element, float releaseRightValue, float y, float x2, float panelOverlayLimit, float cardMatrixFactor) {
      if (!(panelOverlayLimit <= 0.01F)) {
         float amountsModeScaled = element.getAmountsMode() * 0.92F;
         float panelOverlayBound = 21.0F + amountsModeScaled + 8.0F;
         float hashHuntBound = 17.0F;
         float panelOverlayValue = releaseRightValue - panelOverlayBound * 0.5F;
         float rowTopY = y - hashHuntBound * 0.5F;
         int matrixStackValue = CardMatrixUtils.getValue(MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 1.0F), -1750963, cardMatrixFactor);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, releaseRightValue, y, x2);

         try {
            handlePanelOverlay(panelOverlayValue + 1.0F, rowTopY + 3.5F, panelOverlayBound - 2.0F, hashHuntBound, 6.0F, 1879048192, 9.0F, 0.6F, true);
            handlePanelOverlay(panelOverlayValue, rowTopY, panelOverlayBound, hashHuntBound, 6.0F, MatrixStackUtilsAmount.getValue(matrixStackValue, 0.34F), 5.0F, 0.85F, false);
            handle(panelOverlayValue, rowTopY, panelOverlayBound, hashHuntBound, 6.0F, CardMatrixUtils.getValue(-15591908, matrixStackValue, 0.1), MatrixStackUtilsAmount.getValue(matrixStackValue, 0.88F));
            handle(element.getIdentifierText(), panelOverlayValue + 6.5F, rowTopY + (hashHuntBound - 10.0F) * 0.5F, 10.0F, CardMatrixUtils.getValue(matrixStackValue, -1, 0.25));
            handlePushScale(element.formatHiddenListed(), panelOverlayValue + 21.0F, getAmountForFloat(rowTopY, hashHuntBound, 0.92F), 0.92F, -920587);
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static void renderAmountMode(DrawContext context, CharacterReleaseUtils.Element element, float modeLimit, float modeLimit2) {
      float modeValue = 31.0F;
      float panelOverlayBound = 98.0F;
      float modeFlag = element.getModeFlag();
      float elementWidth = element.elementWidth;
      float amountAmounts = element.modeAmount;
      boolean modeFlag2 = modeMax3Mode == element && !elementsPanelActive;
      boolean characterReleaseFlag = MATRIX_CLAMP.panelsActive
         && modeMax == null
         && element.modeToggleMode
         && element.toggleModeProgress > 0.6F
         && modeLimit >= modeValue
         && modeLimit <= modeValue + panelOverlayBound
         && modeLimit2 >= amountAmounts
         && modeLimit2 <= amountAmounts + elementWidth;
      element.hoverProgress = getAmount(element.hoverProgress, !characterReleaseFlag && !modeFlag2 ? 0.0F : 1.0F, !characterReleaseFlag && !modeFlag2 ? 11.0F : 16.0F);
      element.highlightAmount = getAmount(element.highlightAmount, modeFlag2 && modeMax == null ? 1.0F : 0.0F, 18.0F);
      if (!(elementWidth < 3.0F)) {
         float min3 = Math.min(1.0F, element.toggleModeProgress * 1.8F);
         float panelOverlayLimit = MatrixStackUtilsAmount.getMax((element.toggleModeProgress - 0.55F) / 0.45F) * (1.0F - 0.86F * element.activeProgress);
         float x2 = 1.0F + 0.04F * element.highlightAmount + 0.05F * getSin(element.pulseProgress);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(min3);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, modeValue + panelOverlayBound * 0.5F, amountAmounts + elementWidth * 0.5F, x2);

         try {
            if (element.highlightAmount > 0.01F) {
               handlePanelOverlay(
                  modeValue + 1.0F, amountAmounts + 2.5F, panelOverlayBound - 2.0F, elementWidth, 6.0F, MatrixStackUtilsAmount.getValue(-16777216, 0.46F * element.highlightAmount), 7.0F, 0.55F, true
               );
            }

            int matrixStackValue = CardMatrixUtils.getValue(
               CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), -1, 0.035 * element.hoverProgress + 0.03 * element.highlightAmount), CardMatrixUtils.getValue7(), element.activeProgress
            );
            int modeValue7 = CardMatrixUtils.getValue(
               CardMatrixUtils.getValue(234881023, CardMatrixUtils.getReleaseRight(0.52), Math.max(element.hoverProgress * 0.55F, element.highlightAmount)),
               CardMatrixUtils.getReleaseRight(0.26),
               element.activeProgress
            );
            handleForFloatInt(modeValue, amountAmounts, panelOverlayBound, elementWidth, 6.0F, matrixStackValue, modeValue7);
            if (!(panelOverlayLimit <= 0.01F)) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

               try {
                  float modeValue8 = amountAmounts + (elementWidth - modeFlag) * 0.5F;
                  int colorsValue = CardMatrixUtils.getValue(-7630437, getReleaseRight(), element.hoverProgress);
                  handle(element.getIdentifierText(), modeValue + 6.0F, modeValue8 + 6.5F, 11.0F, colorsValue);
                  handlePushScale(
                     element.listedHiddenText, modeValue + 22.0F, getAmountForFloat(modeValue8, 24.0F, 1.0F), 1.0F, CardMatrixUtils.getValue(-5854026, -920587, element.hoverProgress)
                  );
                  if (element.modeFlag) {
                     handleAmountAmounts(element, modeValue, panelOverlayBound, modeValue8, modeLimit, modeLimit2, characterReleaseFlag);
                  } else {
                     handleStackEmpty(element.modeValue, modeValue + panelOverlayBound - 6.0F - 20.0F, modeValue8 + 5.0F, element.hoverProgress);
                  }

                  return;
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static void handleModeMax(float modeValue, float modeValue2) {
      if (modeMax3Mode != null && elementsPanelActive && !characterReleaseActive && modeMax == null) {
         float modeFactor = modeValue - scaleXMode;
         float modeFactor2 = modeValue2 - scaleYMode;
         if (modeFactor * modeFactor + modeFactor2 * modeFactor2 >= 9.0F) {
            handleAmountOverlay(modeMax3Mode, true, modeMax3Mode.maxsAmount + modeMax3Mode.characterAmount * 0.5F, modeMax3Mode.rowTopY + 7.0F);
         }
      }
   }

   private static float getSin(float sinValue) {
      return sinValue <= 0.0F ? 0.0F : MathHelper.sin((float) Math.PI * (1.0F - sinValue));
   }

   private static int getClamp() {
      return clamp > 0.6F ? -10494866 : (clamp > 0.3F ? -2045912 : -2077877);
   }

   private static boolean canModeFlag(float modeValue, float modeValue2, int modeValue3) {
      if (!modeActive && modeValue3 == 0 && modeMax == null) {
         CharacterReleaseUtils.Element amountMaxs = getAmountMaxs(modeValue, modeValue2);
         if (amountMaxs == null) {
            return true;
         } else {
            int clampAmount = getClampAmount(amountMaxs, modeValue, modeValue2);
            if (clampAmount >= 0) {
               if (amountMaxs.next2Mode.getCurrentIndex() != clampAmount) {
                  amountMaxs.next2Mode.handle(clampAmount);
               }

               return true;
            } else {
               modeMax3Mode = amountMaxs;
               elementsPanelActive = false;
               characterReleaseActive = false;
               scaleXMode = (float)ClampMatrixUtils.getScaleX();
               scaleYMode = (float)ClampMatrixUtils.getScaleY();
               return true;
            }
         }
      } else {
         return true;
      }
   }

   private static void handleAmountsClamp(int clampValue, float[] clampValues) {
      handleAmountScaled(clampValue, clampTable);
      float clampValue2 = clampTable[1] + clampTable[3];
      float clampValue3 = clampTable[0] + clampTable[2];
      float clampValue4 = clamp2Mode + clampAmount + 2.0F;
      float clampValue5 = bandMaxY + 2.0F;
      if (clampValue4 < clampValue2 - 2.0F) {
         clampValues[1] = (clampValue4 + clampValue2) * 0.5F;
      } else if (clamp2Mode - 2.0F > clampTable[1] + 2.0F) {
         clampValues[1] = (clampTable[1] + clamp2Mode - 2.0F) * 0.5F;
      } else if (clampValue5 < clampValue3 - 2.0F) {
         clampValues[0] = (clampValue5 + clampValue3) * 0.5F;
      }
   }

   public static boolean canModeToggle(DrawContext context, float modeToggleValue, boolean modeToggleFlag) {
      MODE_TEXT.updateModeTexts();
      return MODE_TEXT.toggle instanceof NameTags nameTags && nameTags.isEnabled() ? nameTags.canMatrixFont(context, modeToggleValue, modeToggleFlag) : false;
   }

   private static boolean canAmountClamp(float clampLimit, float modeValue) {
      return maxsAmount > 0.5F
         && hoverBandHeight > 0.0F
         && clampAmount > 0.0F
         && clampLimit >= bandMaxY - hoverBandHeight - 1.0F
         && clampLimit <= bandMaxY + 1.0F
         && modeValue >= clamp2Mode - 1.0F
         && modeValue <= clamp2Mode + clampAmount + 1.0F;
   }

   private static void handlePushScale(String scaleText, float scaleValue, float f13, float x2, int colorsValue) {
      if (Math.abs(x2 - 1.0F) < 0.001F) {
         MatrixStackUtilsAmount.getFontRenderer().handleForStringFloat(scaleText, scaleValue, f13, getColors(colorsValue));
      } else {
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.scale(x2, x2, 1.0F);
         MatrixStackUtilsAmount.getFontRenderer().handleForStringFloat(scaleText, scaleValue / x2, f13 / x2, getColors(colorsValue));
         matrixStack.pop();
      }
   }

   public static boolean canPlaceTexts(String characterReleaseText, float[] characterReleaseValues) {
      if (!overlayPanelActive) {
         return false;
      } else {
         for (int i = 0; i < placeRightTexts.length; i++) {
            if (placeRightTexts[i].equalsIgnoreCase(characterReleaseText)) {
               handleAmounts(i, characterReleaseValues);
               if (canAmountClamp(characterReleaseValues[0], characterReleaseValues[1])) {
                  handleAmountsClamp(i, characterReleaseValues);
               }

               return true;
            }
         }

         return false;
      }
   }

   private static void handleModeMax(String modeText, Throwable throwable) {
      modeActive = true;
      MATRIX_CLAMP.clampMatrixActive = false;
      modeMax3Mode = null;
      modeMax = null;
   }

   private static Matrix4f getPositionMatrix() {
      return MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
   }

   private static boolean isModeMaxModeMax3Mode() {
      modeMax3Mode = null;
      if (!modeActive && modeMax != null && !overlayActive) {
         CharacterReleaseUtils.Element modeMax3 = modeMax;
         modeMax = null;
         float scaleX = (float)ClampMatrixUtils.getScaleX();
         float scaleY = (float)ClampMatrixUtils.getScaleY();
         boolean modeFlag = ClampMatrixUtils.isFlag() && overlayPanelActive;
         CharacterReleaseUtils.Element modeMax4 = modeMax3.getModeMax();
         int modeLimit = modeMax4 != null && modeFlag ? getAmountAmounts(scaleX, scaleY, false) : -1;
         if (modeLimit >= 0 && modeMax3.isFlag()) {
            modeMax3.handlePlaceTexts(modeLimit);
         }

         if (modeFlag && (modeMax4 != null ? modeLimit >= 0 : canAmount(scaleX, scaleY))) {
            modeMax3.handleModeToggle(true);
            if (modeMax3.isModeToggleModeMax()) {
               handleAmountMode(modeMax3, 0, overlayPosX, overlayAmount2Overlay);
               revealProgress = 0.0F;
               return true;
            }
         }

         handleAmountMode(modeMax3, 1, overlayPosX, overlayAmount2Overlay);
         return true;
      } else {
         return true;
      }
   }

   private static boolean canAmount(float characterReleaseLimit, float modeValue) {
      float characterReleaseValue = (panelLeftX + modeAmount) * 0.5F;
      float characterReleaseValue2 = (panelTopY + scaledAmount) * 0.5F;
      float characterReleaseValue3 = Math.max(modeAmount - panelLeftX, 34.0F) * 0.5F * 1.2F;
      float characterReleaseValue4 = Math.max(scaledAmount - panelTopY, 56.0F) * 0.5F * 1.2F;
      return characterReleaseLimit >= characterReleaseValue - characterReleaseValue3 && characterReleaseLimit <= characterReleaseValue + characterReleaseValue3 && modeValue >= characterReleaseValue2 - characterReleaseValue4 && modeValue <= characterReleaseValue2 + characterReleaseValue4;
   }

   private static final class Element {
      final CharacterReleaseUtils.Element modeMax;
      float highlightAmount;
      float modeAmount;
      float[] modeTable;
      float[] elementTable;
      float elementAmount;
      float maxsAmount;
      float hiddenAmount;
      boolean modeToggle;
      float hoverProgress;
      float indicatorOffsetX;
      final int elementTypeIndex;
      final String replace;
      boolean modeActive;
      float showDelaySeconds;
      String hiddenText;
      BooleanSetting next;
      final String modeText;
      String elementText;
      float rowTopY;
      float clampAmount;
      final String identifierText;
      Module toggle;
      float showProgress;
      float flashLevel;
      EnumSetting next3Mode;
      float activeProgress;
      Identifier identifier;
      boolean modeToggleMode;
      float toggleModeProgress;
      final Class<? extends Module> clazz;
      float pulseProgress;
      final String listedHiddenText;
      int listSize;
      EnumSetting next2Mode;
      boolean overlayActive;
      float overlayBlend;
      String[] modeTexts;
      float hoverGlowBlend;
      final boolean modeFlag;
      final int modeValue;
      float elementY;
      boolean elementActive;
      float elementWidth;
      float characterAmount;
      boolean visited;
      float cachedTextWidth = -1.0F;
      float selectionBlend;
      String modeTextMode;
      int[] table;

      void handlePlaceTexts(int index) {
         if (this.next3Mode != null && index >= 0) {
            if (!CharacterReleaseUtils.placeRightTexts[index].equalsIgnoreCase(this.next3Mode.getCurrentOption())) {
               this.next3Mode.handle(CharacterReleaseUtils.placeRightTexts[index]);
            }
         }
      }

      float getAmount() {
         return 16.0F + this.getAmountNext3Mode() * 0.82F + 2.0F + 12.0F;
      }

      boolean isModeToggle() {
         if (!this.isModeToggleFlag()) {
            return false;
         } else {
            return this.modeMax == null ? !this.toggle.isEnabled() : this.modeMax.isModeToggleModeMax() && !this.next.isEnabled();
         }
      }

      Identifier getIdentifierText() {
         if (this.identifier == null) {
            this.identifier = switch (this.elementTypeIndex) {
               case 1 -> IcoGenUtils.getIdentifierGENERATED_IDENTIFIER_CACHE(this.identifierText);
               case 2 -> IcoGenUtils.getHeartIco();
               case 3 -> IcoGenUtils.getIdentifierForString(this.identifierText);
               default -> IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP(this.identifierText);
            };
         }

         return this.identifier;
      }

      String formatTextHidden() {
         if (this.next3Mode == null) {
            return this.formatHiddenListed();
         } else {
            String text = this.next3Mode.getCurrentOption();
            if (!text.equals(this.hiddenText)) {
               this.hiddenText = text;
               this.elementText = this.listedHiddenText + "  " + text;
               this.cachedTextWidth = -1.0F;
            }

            return this.elementText;
         }
      }

      float getAmountHidden() {
         if (this.hiddenAmount < 0.0F) {
            this.hiddenAmount = MatrixStackUtilsAmount.getAmount3(this.listedHiddenText);
         }

         return this.hiddenAmount;
      }

      int getClampMode() {
         return this.modeFlag && this.listSize > 0 ? MathHelper.clamp(this.next2Mode.getCurrentIndex(), 0, this.listSize - 1) : 0;
      }

      float getAmountNext3Mode() {
         if (this.next3Mode == null) {
            return this.getAmountsMode();
         } else {
            String formatTextHidden = this.formatTextHidden();
            if (this.cachedTextWidth < 0.0F) {
               this.cachedTextWidth = MatrixStackUtilsAmount.getAmount3(formatTextHidden);
            }

            return this.cachedTextWidth;
         }
      }

      int getPlaceTexts() {
         if (this.next3Mode == null) {
            return -1;
         } else {
            String formatText = this.next3Mode.getCurrentOption();

            for (int i = 0; i < CharacterReleaseUtils.placeRightTexts.length; i++) {
               if (CharacterReleaseUtils.placeRightTexts[i].equalsIgnoreCase(formatText)) {
                  return i;
               }
            }

            return 0;
         }
      }

      boolean isModeToggleModeMax() {
         if (!this.isModeToggleFlag()) {
            return false;
         } else {
            return this.modeMax == null ? this.toggle.isEnabled() : this.modeMax.isModeToggleModeMax() && this.next.isEnabled();
         }
      }

      String formatHiddenListed() {
         return this.modeFlag && this.listSize > 0 ? this.modeTexts[this.getClampMode()] : this.listedHiddenText;
      }

      CharacterReleaseUtils.Element getModeMax() {
         for (CharacterReleaseUtils.Element modeMax2 = this; modeMax2 != null; modeMax2 = modeMax2.modeMax) {
            if (modeMax2.isFlag()) {
               return modeMax2;
            }
         }

         return null;
      }

      float getModeFlag() {
         return this.modeFlag ? 43.0F : 24.0F;
      }

      CharacterReleaseUtils.Element getModeText(String modeText) {
         this.modeTextMode = modeText;
         return this;
      }

      void updateModeTexts() {
         if (!this.elementActive) {
            this.elementActive = true;
            this.toggle = ModuleRegistry.getModule(this.clazz);
            if (this.toggle != null) {
               for (ConfigSetting autoEspBase : this.toggle.getSettings()) {
                  if (this.modeText != null && autoEspBase instanceof BooleanSetting next && autoEspBase.getSettingName().equalsIgnoreCase(this.modeText)) {
                     this.next = next;
                  }

                  if (this.modeFlag && autoEspBase instanceof EnumSetting next2 && autoEspBase.getSettingName().equalsIgnoreCase("Mode")) {
                     this.next2Mode = next2;
                  }

                  if (this.modeTextMode != null && autoEspBase instanceof EnumSetting next3 && autoEspBase.getSettingName().equalsIgnoreCase(this.modeTextMode)) {
                     this.next3Mode = next3;
                  }
               }

               if (this.next2Mode != null) {
                  var list = this.next2Mode.getOptions();
                  this.listSize = list.size();
                  this.table = new int[this.listSize];
                  this.modeTexts = new String[this.listSize];
                  this.modeTable = new float[this.listSize];
                  this.elementTable = new float[this.listSize];

                  for (int i = 0; i < this.listSize; i++) {
                     String modeText = (String)list.get(i);
                     this.table[i] = 2;
                     this.modeTexts[i] = modeText;
                     this.modeTable[i] = -1.0F;

                     for (int j = 0; j < CharacterReleaseUtils.skeletonGlowTexts.length; j++) {
                        if (CharacterReleaseUtils.skeletonGlowTexts[j].equalsIgnoreCase(modeText)) {
                           this.table[i] = j;
                           this.modeTexts[i] = CharacterReleaseUtils.skeletonTagTexts[j];
                        }
                     }
                  }
               }
            }
         }
      }

      boolean isModeToggleFlag() {
         if (this.toggle == null) {
            return false;
         } else {
            return this.modeText != null && this.next == null ? false : !this.modeFlag || this.next2Mode != null && this.listSize > 0;
         }
      }

      Element(
         String listedHiddenText,
         int value,
         String text,
         int value2,
         Class<? extends Module> clazz,
         String modeText,
         CharacterReleaseUtils.Element modeMax,
         boolean modeFlag
      ) {
         this.hiddenAmount = -1.0F;
         this.characterAmount = -1.0F;
         this.listedHiddenText = listedHiddenText;
         this.replace = CharacterReleaseUtils.getReplace(listedHiddenText);
         this.elementTypeIndex = value;
         this.identifierText = text;
         this.modeValue = value2;
         this.clazz = clazz;
         this.modeText = modeText;
         this.modeMax = modeMax;
         this.modeFlag = modeFlag;
      }

      void handleModeToggle(boolean modeToggleFlag) {
         if (this.isModeToggleFlag()) {
            if (this.modeMax != null) {
               if (modeToggleFlag && !this.modeMax.isModeToggleModeMax()) {
                  this.modeMax.handleModeToggle(true);
               }

               if (this.next.isEnabled() != modeToggleFlag) {
                  this.next.handle(modeToggleFlag);
               }
            } else if (this.toggle.isEnabled() != modeToggleFlag) {
               this.toggle.setEnabled(modeToggleFlag);
            }
         }
      }

      float getAmountsMode() {
         if (this.modeFlag && this.listSize != 0) {
            int clampMode = this.getClampMode();
            if (this.modeTable[clampMode] < 0.0F) {
               this.modeTable[clampMode] = MatrixStackUtilsAmount.getAmount3(this.modeTexts[clampMode]);
            }

            return this.modeTable[clampMode];
         } else {
            return this.getAmountHidden();
         }
      }

      boolean isFlag() {
         return this.next3Mode != null;
      }
   }

   private static final class Flyer {
      boolean modeActive;
      CharacterReleaseUtils.Element modeMax;
      float flightProgress;
      float modeAmount;
      float flyerAmount;
      float currentPosition;
      float startPosition;
      float flightDurationMs;
      int flyerKindIndex;
   }
}
